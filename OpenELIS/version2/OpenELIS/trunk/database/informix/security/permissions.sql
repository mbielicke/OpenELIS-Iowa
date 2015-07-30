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

revoke all on application from "public";
revoke all on lock from "public";
revoke all on section from "public";
revoke all on system_module from "public";
revoke all on system_user from "public";
revoke all on system_user_module from "public";
revoke all on system_user_section from "public";

grant select on application to "public";
grant select on lock to "public";
grant select on section to "public";
grant select on system_module to "public";
grant select on system_user to "public";
grant select on system_user_module to "public";
grant select on system_user_section to "public";

grant execute on function ejb_roles (varchar,varchar) to "public";
grant execute on function getgroups (char,char) to "public";
grant execute on function getmodules (char,char) to "public";
grant execute on function getsqls (char,char) to "public";