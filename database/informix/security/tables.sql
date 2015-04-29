create table "dba".application
(
    id                        serial not null,
    name                      char(18) not null,
    description               char(60),
    primary                   key (id)
);

create table "dba".lock
(
    reference_table_id        integer not null,
    reference_id              integer not null,
    expires                   int8 not null,
    system_user_id            integer not null,
    session_id                varchar(80,1),
    primary                   key (reference_table_id,reference_id)
);

create table "dba".section
(
    id                        serial not null,
    application_id            integer not null,
    name                      char(20) not null,
    description               char(60),
    primary                   key (id)
);

create table "dba".system_module
(
    id                        serial not null,
    application_id            integer not null,
    name                      varchar(32,1) not null,
    description               varchar(80,1),
    has_select_flag           char(1),
    has_add_flag              char(1),
    has_update_flag           char(1),
    has_delete_flag           char(1),
    clause                    varchar(255,1),
    primary                   key (id)
);

create table "dba".system_user
(
    id                        serial not null,
    external_id               varchar(80,1),
    login_name                char(20) not null,
    last_name                 varchar(30,1),
    first_name                varchar(20,1),
    initials                  char(3),
    is_employee               char(1) not null,
    is_active                 char(1) not null,
    is_template               char(1) not null,
    primary                   key (id)
);

create table "dba".system_user_module
(
    id                        serial not null,
    system_user_id            integer not null,
    system_module_id          integer not null,
    has_select                char(1),
    has_add                   char(1),
    has_update                char(1),
    has_delete                char(1),
    clause                    varchar(255,1),
    primary                   key (id)
);

create table "dba".system_user_section
(
    id                        serial not null,
    system_user_id            integer not null,
    section_id                integer not null,
    has_view                  char(1),
    has_assign                char(1),
    has_complete              char(1),
    has_release               char(1),
    has_cancel                char(1),
    primary                   key (id)
);