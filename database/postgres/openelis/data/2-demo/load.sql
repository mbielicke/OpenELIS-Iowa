\copy section from 'section.dat' with delimiter as '|';
\copy method from 'method.dat' with delimiter as '|';
\copy test_trailer from 'test_trailer.dat' with delimiter as '|';
\copy test from 'test.dat' with delimiter as '|';
\copy test_section from 'test_section.dat' with delimiter as '|';
\copy test_type_of_sample from 'test_type_of_sample.dat' with delimiter as '|';
\copy test_analyte from 'test_analyte.dat' with delimiter as '|';
\copy test_result from 'test_result.dat' with delimiter as '|';
\copy test_prep from 'test_prep.dat' with delimiter as '|';
\copy test_reflex from 'test_reflex.dat' with delimiter as '|';
\copy test_worksheet_analyte from 'test_worksheet_analyte.dat' with delimiter as '|';
\copy test_worksheet from 'test_worksheet.dat' with delimiter as '|';
\copy test_worksheet_item from 'test_worksheet_item.dat' with delimiter as '|';

select setval('section_id_seq', max(id)) from section;
select setval('method_id_seq', max(id)) from method;
select setval('test_trailer_id_seq', max(id)) from test_trailer;
select setval('test_id_seq', max(id)) from test;
select setval('test_section_id_seq', max(id)) from test_section;
select setval('test_type_of_sample_id_seq', max(id)) from test_type_of_sample;
select setval('test_analyte_id_seq', max(id)) from test_analyte;
select setval('test_result_id_seq', max(id)) from test_result;
select setval('test_prep_id_seq', max(id)) from test_prep;
select setval('test_reflex_id_seq', max(id)) from test_reflex;
select setval('test_worksheet_analyte_id_seq', max(id)) from test_worksheet_analyte;
select setval('test_worksheet_id_seq', max(id)) from test_worksheet;
select setval('test_worksheet_item_id_seq', max(id)) from test_worksheet_item;

