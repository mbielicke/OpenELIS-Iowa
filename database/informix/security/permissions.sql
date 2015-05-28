grant dba to "dba";
grant connect to "public";

revoke all on application from "public";
revoke all on lock from "public";
revoke all on section from "public";
revoke all on system_module from "public";
revoke all on system_user from "public";
revoke all on system_user_module from "public";
revoke all on system_user_section from "public";

grant select,update,insert,delete,index on application to "jboss_demo";
grant select,update,insert,delete,index on application to "jboss_dev";
grant select,index on application to "public";

grant select,update,insert,delete,index on lock to "jboss_demo";
grant select,update,insert,delete,index on lock to "jboss_dev";
grant select,index on lock to "public";

grant select,update,insert,delete,index on section to "jboss_demo";
grant select,update,insert,delete,index on section to "jboss_dev";
grant select,index on section to "public";

grant select,update,insert,delete,index on system_module to "jboss_demo";
grant select,update,insert,delete,index on system_module to "jboss_dev";
grant select,index on system_module to "public";

grant select,update,insert,delete,index on system_user to "jboss_demo";
grant select,update,insert,delete,index on system_user to "jboss_dev";
grant select,index on system_user to "public";

grant select,update,insert,delete,index on system_user_module to "jboss_demo";
grant select,update,insert,delete,index on system_user_module to "jboss_dev";
grant select,index on system_user_module to "public";

grant select,update,insert,delete,index on system_user_section to "jboss_demo";
grant select,update,insert,delete,index on system_user_section to "jboss_dev";
grant select,index on system_user_section to "public";

grant execute on function ejb_roles (varchar,varchar) to "public";
grant execute on function getgroups (char,char) to "public";
grant execute on function getmodules (char,char) to "public";
grant execute on function getsqls (char,char) to "public";