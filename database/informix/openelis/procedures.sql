CREATE PROCEDURE "dba".instrument_get_analytes(worksheet int, position int)
       returning varchar(60) as analyte,
                 float as p1, float as p2, float as p3,
                 varchar(60) as c1_analyte, varchar(30) as c1_type,
                 char(1) as c1_reportable, varchar(80) as c1_default,
                 varchar(60) as c2_analyte, varchar(30) as c2_type,
                 char(1) as c2_reportable, varchar(80) as c2_default,
                 varchar(60) as c3_analyte, varchar(30) as c3_type,
                 char(1) as c3_reportable, varchar(80) as c3_default,
                 varchar(60) as c4_analyte, varchar(30) as c4_type,
                 char(1) as c4_reportable, varchar(80) as c4_default,
                 varchar(60) as c5_analyte, varchar(30) as c5_type,
                 char(1) as c5_reportable, varchar(80) as c5_default,
                 varchar(60) as c6_analyte, varchar(30) as c6_type,
                 char(1) as c6_reportable, varchar(80) as c6_default,
                 varchar(60) as c7_analyte, varchar(30) as c7_type,
                 char(1) as c7_reportable, varchar(80) as c7_default,
                 varchar(60) as c8_analyte, varchar(30) as c8_type,
                 char(1) as c8_reportable, varchar(80) as c8_default,
                 varchar(60) as c9_analyte, varchar(30) as c9_type,
                 char(1) as c9_reportable, varchar(80) as c9_default,
                 varchar(60) as c10_analyte, varchar(30) as c10_type,
                 char(1) as c10_reportable, varchar(80) as c10_default;
--
-- This procedure returns the row analyte and all the column
-- analytes defined in the test/qc for given worksheet;position;analyte
--
    define a0, a1, a2, a3, a4, a5, a6, a7, a8, a9, a10 varchar(60);
    define p1, p2, p3 float;
    define t0, t1, t2, t3, t4, t5, t6, t7, t8, t9, t10 varchar(30);
    define r0, r1, r2, r3, r4, r5, r6, r7, r8, r9, r10 char(1);
    define d0, d1, d2, d3, d4, d5, d6, d7, d8, d9, d10 varchar(80);
--
    define a_id, q_id, t_id, u_id, i, cnt integer;
    define sort_order, type_id, an_id, result_group, uom_id integer;
    define is_column, is_reportable char(1);
    define td_system_name, an_name, an_default, expected_value varchar(80);
    define created_date datetime year to minute;

    select w.created_date
    into   created_date
    from   worksheet w
    where  w.id = worksheet;
    
    select first 1 wa.analysis_id, ql.qc_id
    into   a_id, q_id
    from   worksheet_item wi, worksheet_analysis wa, outer qc_lot ql
    where  ql.id = wa.qc_lot_id and
           wi.id = wa.worksheet_item_id and
           wi.worksheet_id = worksheet and
           wi.position = position;

    if a_id is null and q_id is null then
        raise exception -746, 0, 'Specified worksheet;position not found';
    end if

--
-- analysis or qc
--
    if a_id is not null then
        select a.test_id, a.unit_of_measure_id
          into t_id, u_id
          from analysis a
         where a.id = a_id;

        if t_id is null then
            raise exception -746, 1, 'Test for specified analysis not found';
        end if

--
-- get all the analytes (regardless of type) from test definition. for 
-- reportable flag, we want to use the actual value from the result rather
-- than the test definition
--
        let i = 1;
        let cnt = 0;
        foreach select ta.sort_order, ta.is_column, ta.analyte_id, ta.type_id,
                       r.is_reportable, ta.result_group, td.system_name, an.name
                into   sort_order, is_column, an_id, type_id,
                       is_reportable, result_group, td_system_name, an_name
                from   test_analyte ta, analyte an, dictionary td,
                       outer result r
                where  ta.test_id = t_id and
                       ta.analyte_id = an.id and
                       ta.type_id = td.id and
                       ta.id = r.test_analyte_id and
                       r.analysis_id = a_id
                order by sort_order
--
-- if the reportable flag comes from test definition, then mark the
-- as no
--
            if is_reportable is null then
               let is_reportable = "N";
            end if
--
-- get default value
--
            let an_default = null;

            select  tr.value
            into    an_default
            from    test_result tr
            where   tr.test_id = t_id and
                    tr.result_group = result_group and
                    tr.type_id = (select id from dictionary where
                                  system_name = 'test_res_type_default') and
                    tr.unit_of_measure_id = u_id;

            if an_default is null then
                select  tr.value
                into    an_default
                from    test_result tr
                where   tr.test_id = t_id and
                        tr.result_group = result_group and
                        tr.type_id = (select id from dictionary where
                                      system_name = 'test_res_type_default') and
                        tr.unit_of_measure_id is null;
            end if

            if is_column = "N" then
                if cnt > 0 then
                    return a0, p1, p2, p3,
                           a1, t1, r1, d1,
                           a2, t2, r2, d2,
                           a3, t3, r3, d3,
                           a4, t4, r4, d4,
                           a5, t5, r5, d5,
                           a6, t6, r6, d6,
                           a7, t7, r7, d7,
                           a8, t8, r8, d8,
                           a9, t9, r9, d9,
                           a10, t10, r10, d10
                    with resume;
                end if
                let i = 1;
                let a0 = null;
                let p1 = null;
                let p2 = null;
                let p3 = null;
                let a1 = null;
                let t1 = null;
                let r1 = null;
                let d1 = null;
                let a2 = null;
                let t2 = null;
                let r2 = null;
                let d2 = null;
                let a3 = null;
                let t3 = null;
                let r3 = null;
                let d3 = null;
                let a4 = null;
                let t4 = null;
                let r4 = null;
                let d4 = null;
                let a5 = null;
                let t5 = null;
                let r5 = null;
                let d5 = null;
                let a6 = null;
                let t6 = null;
                let r6 = null;
                let d6 = null;
                let a7 = null;
                let t7 = null;
                let r7 = null;
                let d7 = null;
                let a8 = null;
                let t8 = null;
                let r8 = null;
                let d8 = null;
                let a9 = null;
                let t9 = null;
                let r9 = null;
                let d9 = null;
                let a10 = null;
                let t10 = null;
                let r10 = null;
                let d10 = null;
            end if

            if i = 1 then
--
-- Find the analyte parameter with matching units
--
                select first 1 ap.p1, ap.p2, ap.p3
                into   p1, p2, p3
                from   analyte_parameter ap
                where  ap.reference_id = t_id and
                       ap.analyte_id = an_id and
                       ap.reference_table_id = 22 and
                       ap.unit_of_measure_id = u_id and
                       ap.active_begin <= created_date and
                       ap.active_end >= created_date;
--
-- If no matching analyte parameter is found, try to find one with no units
--
                if p1 is null and p2 is null and p3 is null then
                    select first 1 ap.p1, ap.p2, ap.p3
                    into   p1, p2, p3
                    from   analyte_parameter ap
                    where  ap.reference_id = t_id and
                           ap.analyte_id = an_id and
                           ap.reference_table_id = 22 and
                           ap.unit_of_measure_id is null and
                           ap.active_begin <= created_date and
                           ap.active_end >= created_date;
                end if

                let a0 = an_name;
                let a1 = "Value";
                let t1 = td_system_name;
                let r1 = is_reportable;
                let d1 = an_default;
            elif i = 2 then
                let a2 = an_name;
                let t2 = td_system_name;
                let r2 = is_reportable;
                let d2 = an_default;
            elif i = 3 then
                let a3 = an_name;
                let t3 = td_system_name;
                let r3 = is_reportable;
                let d3 = an_default;
            elif i = 4 then
                let a4 = an_name;
                let t4 = td_system_name;
                let r4 = is_reportable;
                let d4 = an_default;
            elif i = 5 then
                let a5 = an_name;
                let t5 = td_system_name;
                let r5 = is_reportable;
                let d5 = an_default;
            elif i = 6 then
                let a6 = an_name;
                let t6 = td_system_name;
                let r6 = is_reportable;
                let d6 = an_default;
            elif i = 7 then
                let a7 = an_name;
                let t7 = td_system_name;
                let r7 = is_reportable;
                let d7 = an_default;
            elif i = 8 then
                let a8 = an_name;
                let t8 = td_system_name;
                let r8 = is_reportable;
                let d8 = an_default;
            elif i = 9 then
                let a9 = an_name;
                let t9 = td_system_name;
                let r9 = is_reportable;
                let d9 = an_default;
            elif i = 10 then
                let a10 = an_name;
                let t10 = td_system_name;
                let r10 = is_reportable;
                let d10 = an_default;
            end if
            let i = i + 1;
            let cnt = cnt + 1;
        end foreach
        return a0, p1, p2, p3,
               a1, t1, r1, d1,
               a2, t2, r2, d2,
               a3, t3, r3, d3,
               a4, t4, r4, d4,
               a5, t5, r5, d5,
               a6, t6, r6, d6,
               a7, t7, r7, d7,
               a8, t8, r8, d8,
               a9, t9, r9, d9,
               a10, t10, r10, d10
        with resume;
    elif q_id is not null then
        foreach select qa.sort_order, qa.analyte_id, qa.type_id, td.system_name,
                       an.name, qa.value
                into   sort_order, an_id, type_id, td_system_name, an_name, expected_value
                from   qc_analyte qa, analyte an, dictionary td
                where  qa.qc_id = q_id and
                       qa.analyte_id = an.id and
                       qa.type_id = td.id
                order by sort_order
--
-- get default value
--
            let an_default = expected_value;

            select first 1 ap.p1, ap.p2, ap.p3
            into   p1, p2, p3
            from   analyte_parameter ap
            where  ap.reference_id = q_id and
                   ap.analyte_id = an_id and
                   ap.reference_table_id = 51 and
                   ap.active_begin <= created_date and
                   ap.active_end >= created_date;

            return an_name, p1, p2, p3,
                   "Value", td_system_name, null, an_default,
                   null, null, null, null,
                   null, null, null, null,
                   null, null, null, null,
                   null, null, null, null,
                   null, null, null, null,
                   null, null, null, null,
                   null, null, null, null,
                   null, null, null, null,
                   null, null, null, null
            with resume;
        end foreach
    end if
END procedure;