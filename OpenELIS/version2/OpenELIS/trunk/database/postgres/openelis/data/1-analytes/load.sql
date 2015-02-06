\copy analyte from 'analyte.dat' with delimiter as '|';

select setval('analyte_id_seq', max(id)) from analyte;


