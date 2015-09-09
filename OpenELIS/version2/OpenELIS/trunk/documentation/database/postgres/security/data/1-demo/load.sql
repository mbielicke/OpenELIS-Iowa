\copy section from 'section.dat' with delimiter as '|';
\copy system_user_section from 'system_user_section.dat' with delimiter as '|';

select setval('section_id_seq', max(id)) from section;
select setval('system_user_section_id_seq', max(id)) from system_user_section;