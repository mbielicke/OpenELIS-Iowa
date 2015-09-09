\copy application from 'application.dat' with delimiter as '|';
\copy system_module from 'system_module.dat' with delimiter as '|';
\copy system_user from 'system_user.dat' with delimiter as '|';
\copy system_user_module from 'system_user_module.dat' with delimiter as '|';

select setval('application_id_seq', max(id)) from application;
select setval('system_module_id_seq', max(id)) from system_module;
select setval('system_user_id_seq', max(id)) from system_user;
select setval('system_user_module_id_seq', max(id)) from system_user_module;
