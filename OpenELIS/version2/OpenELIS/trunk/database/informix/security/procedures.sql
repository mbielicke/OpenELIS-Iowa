create procedure "dba".ejb_roles(user_name varchar(30), application_name varchar(30))
       returning varchar(50), char(5);
--
--  procedure returns:
--      1) the module name with permission postfix for given user_name
--      2) contant "Roles"
--
    define mod_name         char(32);
    define has_select       char(1);
    define has_add          char(1);
    define has_update       char(1);
    define has_delete       char(1);
    define roles            varchar(50);

    foreach select system_module.name, system_user_module.has_select,
                   system_user_module.has_add, system_user_module.has_update,
                   system_user_module.has_delete
              into mod_name, has_select, has_add, has_update, has_delete
              from system_user, system_module, system_user_module, application
             where system_user.id = system_user_module.system_user_id and
                   system_user.login_name = user_name and
                   system_module.id = system_user_module.system_module_id and
                   system_module.application_id = application.id and
                   application.name = application_name
        if has_select = "Y" then
            let roles = trim(mod_name) || "-select";
            return roles, "Roles" with resume;
        end if
        if has_add = "Y" then
            let roles = trim(mod_name) || "-add";
            return roles, "Roles" with resume; 
        end if
        if has_update = "Y" then
            let roles = trim(mod_name) || "-update";
            return roles, "Roles" with resume; 
        end if
        if has_delete = "Y" then
            let roles = trim(mod_name) || "-delete";
            return roles, "Roles" with resume; 
        end if
    end foreach

    return "no_roles", "Roles";

end procedure;

create procedure "dba".getgroups(username char(30), databasename char(18))
                 returning char(20), char(5);
--
-- returns the group.name/group.permission list for the
-- given user/database
--

   define   dbs_id      integer;
   define   user_id     integer;
   define   groupname   char(20);
   define   groupperm   varchar(5);
   define   v,a,c,r,x   char(1);

-- find the user id

   select   id into user_id
      from  system_user
      where login_name = username;
   if user_id is NULL then
      return;
   end if

-- find the database id

   select   id into dbs_id
      from  application
      where name = databasename;
   if dbs_id is NULL then
      return;
   end if

-- find all the groups records that match user/dbs pair

   foreach
      select   s.name, u.has_view, u.has_assign, u.has_complete,
               u.has_release, u.has_cancel
         into  groupname,v,a,c,r,x
         from  section s, system_user_section u
         where u.system_user_id = user_id and u.section_id = s.id and
               s.application_id = dbs_id

      let groupperm = "";
      if v = "Y" then
        let groupperm = groupperm || "s";
      end if
      if a = "Y" then
        let groupperm = groupperm || "a";
      end if
      if c = "Y" then
        let groupperm = groupperm || "c";
      end if
      if r = "Y" then
        let groupperm = groupperm || "r";
      end if
      if x = "Y" then
        let groupperm = groupperm || "x";
      end if
      return groupname, groupperm with resume;
   end foreach

end procedure;

create procedure "dba".getmodules(username char(30), databasename char(18))
                 returning char(32), char(4);
--
-- returns the modules.name/modules.permission list for the
-- given user/database
--

   define   dbs_id      integer;
   define   user_id     integer;
   define   modulename  char(32);
   define   moduleperm  varchar(4);
   define   s,a,u,d     char(1);

-- find the user id

   select   id into user_id
      from  system_user
      where login_name = username;
   if user_id is NULL then
      return;
   end if

-- find the database id

   select   id into dbs_id
      from  application
      where name = databasename;
   if dbs_id is NULL then
      return;
   end if

-- find all the modules records that match user/dbs pair

   foreach
      select   s.name, u.has_select, u.has_add, u.has_update, u.has_delete
         into  modulename,s,a,u,d
         from  system_module s, system_user_module u
         where u.system_user_id = user_id and u.system_module_id = s.id and
               s.application_id = dbs_id

      let moduleperm = "";
      if s = "Y" then
        let moduleperm = moduleperm || "s";
      end if
      if a = "Y" then
        let moduleperm = moduleperm || "a";
      end if
      if u = "Y" then
        let moduleperm = moduleperm || "u";
      end if
      if d = "Y" then
        let moduleperm = moduleperm || "d";
      end if
      return modulename, moduleperm with resume;
   end foreach

end procedure;

create procedure "dba".getsqls(username char(30), databasename char(18))
                 returning char(32), char(255);
--
-- returns the modules.name/modules.permission list for the
-- given user/database
--

   define   dbs_id          integer;
   define   user_id         integer;
   define   modulename      char(32);
   define   where_clause    char(255);

-- find the user id

   select   id into user_id
      from  system_user
      where login_name = username;
   if user_id is NULL then
      return;
   end if

-- find the database id

   select   id into dbs_id
      from  application
      where name = databasename;
   if dbs_id is NULL then
      return;
   end if

-- find all the modules records that match user/dbs pair

   foreach
      select   s.name, u.clause
         into  modulename, where_clause
         from  system_module s, system_user_module u
         where u.system_user_id = user_id and u.system_module_id = s.id and
               s.application_id = dbs_id and length(u.clause) > 0

      return modulename, where_clause with resume;
   end foreach

end procedure;