
-------------------------------------------------------------------------------
--
-- permissions
--
-------------------------------------------------------------------------------

grant select,insert,update,delete on application to "jboss_dev";
grant select,insert,update,delete on lock to "jboss_dev";
grant select,insert,update,delete on section to "jboss_dev";
grant select,insert,update,delete on system_module to "jboss_dev";
grant select,insert,update,delete on system_user to "jboss_dev";
grant select,insert,update,delete on system_user_module to "jboss_dev";
grant select,insert,update,delete on system_user_section to "jboss_dev";

grant usage,select on sequence application_id_seq to "jboss_dev";
grant usage,select on sequence section_id_seq to "jboss_dev";
grant usage,select on sequence system_module_id_seq to "jboss_dev";
grant usage,select on sequence system_user_id_seq to "jboss_dev";
grant usage,select on sequence system_user_module_id_seq to "jboss_dev";
grant usage,select on sequence system_user_section_id_seq to "jboss_dev";
