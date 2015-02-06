\copy category from 'category.dat' with delimiter as '|';
\copy dictionary from 'dictionary.dat' with delimiter as '|';
\copy system_variable from 'system_variable.dat' with delimiter as '|';

select setval('category_id_seq', max(id)) from category;
select setval('dictionary_id_seq', max(id)) from dictionary;
select setval('system_variable_id_seq', max(id)) from system_variable;

