grant dba to "dba";
grant connect to "public";

revoke all on "dba".application from "public" as "dba";
revoke all on "dba".lock from "public" as "dba";
revoke all on "dba".section from "public" as "dba";
revoke all on "dba".system_module from "public" as "dba";
revoke all on "dba".system_user from "public" as "dba";
revoke all on "dba".system_user_module from "public" as "dba";
revoke all on "dba".system_user_section from "public" as "dba";

grant select,update,insert,delete,index on "dba".application to "jboss_demo" as "dba";
grant select,update,insert,delete,index on "dba".application to "jboss_dev" as "dba";
grant select,index on "dba".application to "public" as "dba";

grant select,update,insert,delete,index on "dba".lock to "jboss_demo" as "dba";
grant select,update,insert,delete,index on "dba".lock to "jboss_dev" as "dba";
grant select,index on "dba".lock to "public" as "dba";

grant select,update,insert,delete,index on "dba".section to "jboss_demo" as "dba";
grant select,update,insert,delete,index on "dba".section to "jboss_dev" as "dba";
grant select,index on "dba".section to "public" as "dba";

grant select,update,insert,delete,index on "dba".system_module to "jboss_demo" as "dba";
grant select,update,insert,delete,index on "dba".system_module to "jboss_dev" as "dba";
grant select,index on "dba".system_module to "public" as "dba";

grant select,update,insert,delete,index on "dba".system_user to "jboss_demo" as "dba";
grant select,update,insert,delete,index on "dba".system_user to "jboss_dev" as "dba";
grant select,index on "dba".system_user to "public" as "dba";

grant select,update,insert,delete,index on "dba".system_user_module to "jboss_demo" as "dba";
grant select,update,insert,delete,index on "dba".system_user_module to "jboss_dev" as "dba";
grant select,index on "dba".system_user_module to "public" as "dba";

grant select,update,insert,delete,index on "dba".system_user_section to "jboss_demo" as "dba";
grant select,update,insert,delete,index on "dba".system_user_section to "jboss_dev" as "dba";
grant select,index on "dba".system_user_section to "public" as "dba";

grant execute on function "dba".ejb_roles (varchar,varchar) to "public" as "dba";
grant execute on function "dba".getgroups (char,char) to "public" as "dba";
grant execute on function "dba".getmodules (char,char) to "public" as "dba";
grant execute on function "dba".getsqls (char,char) to "public" as "dba";