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
    initials                  char(3),
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