-------------------------------------------------------------------------------
--
-- tables
--
-------------------------------------------------------------------------------

create table application
(
    id                        serial not null,
    name                      varchar(20) not null,
    description               varchar(60)
);

create table lock
(
    reference_table_id        integer not null,
    reference_id              integer not null,
    expires                   int8 not null,
    system_user_id            integer not null,
    session_id                varchar(80)
);


create table section
(
    id                        serial not null,
    application_id            integer not null,
    name                      varchar(20) not null,
    description               varchar(60)
);

create table system_module
(
    id                        serial not null,
    application_id            integer not null,
    name                      varchar(32) not null,
    description               varchar(80),
    has_select_flag           char(1),
    has_add_flag              char(1),
    has_update_flag           char(1),
    has_delete_flag           char(1),
    clause                    varchar(255)
);

create table system_user
(
    id                        serial not null,
    external_id               varchar(80),
    login_name                varchar(20) not null,
    last_name                 varchar(30),
    first_name                varchar(20),
    initials                  varchar(10),
    is_employee               char(1) not null,
    is_active                 char(1) not null,
    is_template               char(1) not null
);

create table system_user_module
(
    id                        serial not null,
    system_user_id            integer not null,
    system_module_id          integer not null,
    has_select                char(1),
    has_add                   char(1),
    has_update                char(1),
    has_delete                char(1),
    clause                    varchar(255)
);

create table system_user_section
(
    id                        serial not null,
    system_user_id            integer not null,
    section_id                integer not null,
    has_view                  char(1),
    has_assign                char(1),
    has_complete              char(1),
    has_release               char(1),
    has_cancel                char(1)
);

-------------------------------------------------------------------------------
--
-- procedures/functions
--
-------------------------------------------------------------------------------

CREATE TYPE ejb_role AS
   (module_permission character varying(50),
    role character(5));
ALTER TYPE ejb_role
  OWNER TO dba;
COMMENT ON TYPE ejb_role
  IS 'Return data structure for function ejb_roles';

CREATE OR REPLACE FUNCTION ejb_roles(user_name character varying, application_name character varying)
  RETURNS SETOF ejb_role AS
$BODY$
--
--  This procedure is used by JBOSS to grant access to beans.
--  procedure returns:
--      1) the module name with permission postfix for given user_name
--      2) contant 'Roles'
--
   declare 
      perm         record;
      role         ejb_role;
      has_roles    smallint;

begin
   has_roles := 0;
   role.role := 'Roles';
   
   for perm in
       select system_module.name, system_user_module.has_select,
              system_user_module.has_add, system_user_module.has_update,
              system_user_module.has_delete
         from system_user, system_module, system_user_module, application
        where system_user.id = system_user_module.system_user_id and
              system_user.login_name = user_name and
              system_module.id = system_user_module.system_module_id and
              system_module.application_id = application.id and
              application.name = application_name loop

       if perm.has_select = 'Y' then
           role.module_permission := perm.name || '-select';
           return next role;
       end if;

       if perm.has_add = 'Y' then
           role.module_permission := perm.name || '-add';
           return next role;
       end if;

       if perm.has_update = 'Y' then
           role.module_permission := perm.name || '-update';
           return next role;
       end if;

       if perm.has_delete = 'Y' then
           role.module_permission := perm.name || '-delete';
           return next role;
       end if;

       has_roles := 1;
   end loop;

   if has_roles = 0 then
       role.module_permission := 'no_roles';
       return next role;
   end if;

   return;
end;
$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100
  ROWS 1000;
ALTER FUNCTION ejb_roles(character varying, character varying)
  OWNER TO dba;


