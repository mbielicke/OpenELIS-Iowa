create unique index "dba".application_2 on "dba".application (name) using btree;

create        index "dba".section_2 on "dba".section (application_id) using btree;
create        index "dba".section_3 on "dba".section (name) using btree;

create        index "dba".sysmod_2 on "dba".system_module (application_id) using btree;
create        index "dba".sysmod_3 on "dba".system_module (name) using btree;

create unique index "dba".sysuser_3 on "dba".system_user (login_name) using btree;

create        index "dba".sysusrmod_2 on "dba".system_user_module (system_user_id) using btree;
create        index "dba".sysusrmod_3 on "dba".system_user_module (system_module_id) using btree;

create        index "dba".sysusrsec_2 on "dba".system_user_section (system_user_id) using btree;
create        index "dba".sysusrsec_3 on "dba".system_user_section (section_id) using btree;