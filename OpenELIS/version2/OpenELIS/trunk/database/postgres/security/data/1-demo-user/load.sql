\copy section from 'section.dat' with delimiter as '|';
\copy system_user from 'system_user.dat' with delimiter as '|';
\copy system_user_module from 'system_user_module.dat' with delimiter as '|';
\copy system_user_section from 'system_user_section.dat' with delimiter as '|';

select setval('section_id_seq', max(id)) from section;
select setval('system_user_id_seq', max(id)) from system_user;
select setval('system_user_module_id_seq', max(id)) from system_user_module;
select setval('system_user_section_id_seq', max(id)) from system_user_section;

