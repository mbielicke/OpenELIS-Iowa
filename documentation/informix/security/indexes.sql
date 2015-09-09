-------------------------------------------------------------------------------
--
-- indicies
--
-------------------------------------------------------------------------------

create unique index application_1_idx on application(id);

create unique index lock_1_idx on lock(reference_table_id, reference_id);

create unique index section_1_idx on section(id);
create unique index section_2_idx on section(application_id, name);

create unique index system_module_1_idx on system_module(id);
create unique index system_module_2_idx on system_module(application_id, name);

create unique index system_user_1_idx on system_user(id);

create unique index system_user_module_1_idx on system_user_module(id);
create unique index system_user_module_2_idx on system_user_module(system_user_id,system_module_id);

create unique index system_user_section_1_idx on system_user_section(id);
create unique index system_user_section_2_idx on system_user_section(system_user_id,section_id);

-------------------------------------------------------------------------------
--
-- primary and foreign key
--
-------------------------------------------------------------------------------

alter table application add constraint primary key(id) constraint application_pk;

alter table lock add constraint primary key(reference_table_id, reference_id) constraint lock_pk;
alter table lock add constraint foreign key(system_user_id) references system_user(id) constraint lock_1_fk;

alter table section add constraint primary key(id) constraint section_pk;
alter table section add constraint foreign key(application_id) references application(id) constraint section_1_fk;

alter table system_module add constraint primary key(id) constraint system_module_pk;
alter table system_module add constraint foreign key(application_id) references application(id) constraint system_module_1_fk;

alter table system_user add constraint primary key(id) constraint system_user_pk;

alter table system_user_module add constraint primary key(id) constraint system_user_module_pk;
alter table system_user_module add constraint foreign key(system_module_id) references system_module(id) constraint system_user_module_1_fk;
alter table system_user_module add constraint foreign key(system_user_id) references system_user(id) constraint system_user_module_2_fk;

alter table system_user_section add constraint primary key(id) constraint system_user_section_pk;
alter table system_user_section add constraint foreign key(section_id) references section(id) constraint system_user_section_1_fk;
alter table system_user_section add constraint foreign key(system_user_id) references system_user(id) constraint system_user_section_2_fk;