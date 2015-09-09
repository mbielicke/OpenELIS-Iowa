-------------------------------------------------------------------------------
--
-- procedures/functions
--
-------------------------------------------------------------------------------

create type ejb_role as (module_permission character varying(50), role character(5));
alter type ejb_role owner to dba;

create or replace function ejb_roles(user_name character varying, application_name character varying)
  returns setof ejb_role as
$BODY$
--
--  This procedure is used by JBOSS to grant access to beans.
--  procedure returns:
--      1) the module name with permission postfix for given user_name
--      2) contant 'Roles'
--
   declare perm         record;
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