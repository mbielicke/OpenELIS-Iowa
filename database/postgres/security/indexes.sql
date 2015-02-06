-------------------------------------------------------------------------------
--
-- indicies
--
-------------------------------------------------------------------------------

create unique index on application(id);

create unique index on lock(reference_table_id, reference_id);

create unique index on section(id);
create unique index on section(application_id, name);

create unique index on system_module(id);
create unique index on system_module(application_id, name);

create unique index on system_user(id);

create unique index on system_user_module(id);
create unique index on system_user_module(system_user_id,system_module_id);

create unique index on system_user_section(id);
create unique index on system_user_section(system_user_id,section_id);

-------------------------------------------------------------------------------
--
-- primary and foreign key
--
-------------------------------------------------------------------------------

alter table application add primary key(id);

alter table lock add primary key(reference_table_id, reference_id);

alter table section add primary key(id);
alter table section add foreign key(application_id) references application(id);

alter table system_module add primary key(id);
alter table system_module add foreign key(application_id) references application(id);

alter table system_user add primary key(id);

alter table system_user_module add primary key(id);
alter table system_user_module add foreign key(system_module_id) references system_module(id);
alter table system_user_module add foreign key(system_user_id) references system_user(id);

alter table system_user_section add primary key(id);
alter table system_user_section add foreign key(section_id) references section(id);
alter table system_user_section add foreign key(system_user_id) references system_user(id);
