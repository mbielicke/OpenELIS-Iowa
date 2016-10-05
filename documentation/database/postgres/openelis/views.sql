-------------------------------------------------------------------------------
--
-- views
--
-------------------------------------------------------------------------------

create or replace view analysis_view as

   select s.id as sample_id, s.domain, s.accession_number, s.received_date, s.collection_date,
       s.collection_time, s.entered_date, o.name as primary_organization_name,
       case
           when s.domain = 'E' then
               case
                   when sen.priority is not null then '[pri]' || sen.priority || ' '
                   else ''
               end ||
               case
                   when p.name is not null then '[prj]' || btrim(p.name) || ' '
                   else ''
               end ||
               case
                   when sen.location is not null then '[loc]' || btrim(sen.location)
                   else ''
               end
           when s.domain = 'S' then
               case
                   when pws.name is not null then '[pws]' || btrim(pws.name) || ' '
                   else ''
               end ||
               case
                   when ssd.facility_id is not null then '[fac]' || btrim(ssd.facility_id) || ' '
                   else ''
               end ||
               case
                   when ssd.priority is not null then '[pri]' || ssd.priority
                   else ''
               end
           when s.domain = 'C' then
               case
                   when pat.last_name is not null then btrim(pat.last_name) || ', '
                   else ''
               end ||
               case
                   when pat.first_name is not null then btrim(pat.first_name) || ', '
                   else ''
               end ||
               case
                   when si.container_reference is not null then btrim(si.container_reference)
                   else ''
               end
           when s.domain = 'P' then
               case
                   when ptp.entry is not null then '[ptp]' || btrim(ptp.entry) || ' '
                   else ''
               end ||
               case
                   when spt.series is not null then '[ser]' || btrim(spt.series) || ' '
                   else ''
               end ||
               case
                   when si.container_reference is not null then '[cnt]' || btrim(si.container_reference)
                   else ''
               end
           when s.domain = 'A' then
               case
                   when acn.entry is not null then '[acn]' || btrim(acn.entry) || ' '
                   else ''
               end ||
               case
                   when san.location is not null then '[loc]' || btrim(san.location) || ' '
                   else ''
               end
           else null
       end as todo_description,
       case
           when s.domain = 'E' then
               case
                   when sen.location is not null then '[loc]' || btrim(sen.location) || ' '
                   else ''
               end ||
               case
                   when o.name is not null then '[rpt]' || btrim(o.name)
                   else ''
               end
           when s.domain = 'S' then
               case
                   when ssd.location is not null then '[loc]' || btrim(ssd.location) || ' '
                   else ''
               end ||
               case
                   when o.name is not null then '[rpt]' || btrim(o.name)
                   else ''
               end
           when s.domain = 'C' then
               case
                   when pat.last_name is not null then btrim(pat.last_name) || ', '
                   else ''
               end ||
               case
                   when pat.first_name is not null then btrim(pat.first_name) || ', '
                   else ''
               end ||
               case
                   when si.container_reference is not null then btrim(si.container_reference)
                   else ''
               end
           when s.domain = 'P' then
               case
                   when ptp.entry is not null then '[ptp]' || btrim(ptp.entry) || ' '
                   else ''
               end ||
               case
                   when spt.series is not null then '[ser]' || btrim(spt.series) || ' '
                   else ''
               end ||
               case
                   when si.container_reference is not null then '[cnt]' || btrim(si.container_reference)
                   else ''
               end
           when s.domain = 'A' then
               case
                   when acn.entry is not null then '[acn]' || btrim(acn.entry) || ' '
                   else ''
               end ||
               case
                   when san.location is not null then '[loc]' || btrim(san.location) || ' '
                   else ''
               end
           else null
       end as worksheet_description,
       case
           when s.domain = 'E' then sen.priority
           when s.domain = 'S' then ssd.priority
           else null
       end as priority,
       t.id as test_id, t.name as test_name, m.name as method_name, t.time_ta_average, t.time_holding, si.type_of_sample_id,
       a.id as analysis_id, a.status_id as analysis_status_id, sec.id as section_id, sec.name as section_name, a.available_date, a.started_date, a.completed_date,
       a.released_date,
       case
           when sqa.id is not null or aqa.id is not null then 'Y'
           else 'N'
       end as analysis_result_override,
       a.unit_of_measure_id, tw.format_id as worksheet_format_id
  from sample s
       join sample_item si on s.id = si.sample_id
       join analysis a on si.id = a.sample_item_id
       join test t on a.test_id = t.id
       join method m on t.method_id = m.id
       join section sec on a.section_id = sec.id
       left join sample_environmental sen on s.id = sen.sample_id
       left join sample_sdwis ssd on s.id = ssd.sample_id
       left join pws on ssd.pws_id = pws.id
       left join sample_clinical scl on s.id = scl.sample_id
       left join patient pat on scl.patient_id = pat.id
       left join sample_pt spt on s.id = spt.sample_id
       left join dictionary ptp on spt.pt_provider_id = ptp.id
       left join sample_animal san on s.id = san.sample_id
       left join dictionary acn on san.animal_common_name_id = acn.id
       left join sample_organization so on s.id = so.sample_id and
                                           so.type_id = (select d.id
                                                           from dictionary d
                                                          where d.system_name = 'org_report_to')
       left join organization o on so.organization_id = o.id
       left join sample_project sp on s.id = sp.sample_id and
                                      sp.is_permanent = 'Y'
       left join project p on sp.project_id = p.id
       left join sample_qaevent sqa on s.id = sqa.sample_id and
                                       sqa.type_id = (select d.id
                                                        from dictionary d
                                                       where d.system_name = 'qaevent_override')
       left join qaevent sq on sqa.qaevent_id = sq.id
       left join analysis_qaevent aqa on a.id = aqa.analysis_id and
                                         aqa.type_id = (select d.id
                                                          from dictionary d
                                                         where d.system_name = 'qaevent_override')
       left join qaevent aq on aqa.qaevent_id = aq.id
       left join test_worksheet tw on tw.test_id = t.id
;

create or replace view sample_patient_view as

   select sn.sample_id, sn.patient_id
     from sample_neonatal sn
   union
   select sc.sample_id, sc.patient_id
     from sample_clinical sc
;

create or replace view sample_view as

   select s.id as sample_id, s.domain, s.accession_number, s.revision as sample_revision, s.received_date,
       s.collection_date, s.collection_time, s.status_id as sample_status_id,
       s.client_reference, s.released_date as sample_released_date, o.id as report_to_id, o.name as report_to_name,
       case 
           when s.domain = 'E' then sen.collector
           when s.domain = 'S' then ssd.collector
           else null
       end as collector,
       case 
           when s.domain = 'E' then sen.location
           when s.domain = 'S' then ssd.location
           when s.domain = 'A' then san.location
           else null
       end as location,
       case
           when s.domain = 'E' then ead.street_address
           else null
       end as location_street_address,
       case
           when s.domain = 'E' then ead.city
           when s.domain = 'A' then aad.city
           else null
       end as location_city,
       p.id as project_id, p.name as project_name, pws.number0 as pws_number0,
       pws.name as pws_name, ssd.facility_id as sdwis_facility_id,
       case 
           when s.domain = 'C' then cpa.last_name
           when s.domain = 'N' then npa.last_name
           else null
       end as patient_last_name,
       case 
           when s.domain = 'C' then cpa.first_name
           when s.domain = 'N' then npa.first_name
           else null
       end as patient_first_name,
       case 
           when s.domain = 'C' then cpa.birth_date
           when s.domain = 'N' then npa.birth_date
           else null
       end as patient_birth_date,
       case
           when s.domain = 'C' then
               case
                   when pv.last_name is not null then btrim(pv.last_name) || ', '
                   else ''
               end ||
               case
                   when pv.first_name is not null then btrim(pv.first_name)
                   else ''
               end
           when s.domain = 'A' then
               case
                   when apv.last_name is not null then btrim(apv.last_name) || ', '
                   else ''
               end ||
               case
                   when apv.first_name is not null then btrim(apv.first_name)
                   else ''
               end
           else null
       end as provider_name,
       case
           when s.domain = 'A' then acn.entry
           else null
       end as animal_common_name,
       a.id as analysis_id, a.revision as analysis_revision, a.is_reportable as analysis_is_reportable,
       a.status_id as analysis_status_id, a.released_date as analysis_released_date,
       t.reporting_description as test_reporting_description,
       m.reporting_description as method_reporting_description
  from sample s
       join sample_item si on s.id = si.sample_id
       join analysis a on si.id = a.sample_item_id
       join test t on a.test_id = t.id
       join method m on t.method_id = m.id
       left join sample_environmental sen on s.id = sen.sample_id
       left join address ead on sen.location_address_id = ead.id
       left join sample_sdwis ssd on s.id = ssd.sample_id
       left join pws on ssd.pws_id = pws.id
       left join sample_clinical scl on s.id = scl.sample_id
       left join patient cpa on scl.patient_id = cpa.id
       left join provider pv on scl.provider_id = pv.id
       left join sample_neonatal snn on s.id = snn.sample_id
       left join patient npa on snn.patient_id = npa.id
       left join sample_animal san on s.id = san.sample_id
       left join dictionary acn on san.animal_common_name_id = acn.id
       left join address aad on san.location_address_id = aad.id
       left join provider apv on san.provider_id = apv.id
       left join sample_organization so on s.id = so.sample_id and
                                           so.type_id = (select d.id
                                                           from dictionary d
                                                          where d.system_name = 'org_report_to')
       left join organization o on so.organization_id = o.id
       left join sample_project sp on s.id = sp.sample_id and
                                      sp.is_permanent = 'Y'
       left join project p on sp.project_id = p.id
;

create or replace view test_analyte_view as

   select ca.id, t.id as test_id, t.name as test_name, m.id as method_id, m.name as method_name,
       t.is_active as test_is_active, t.active_begin as test_active_begin, t.active_end as test_active_end,
       ra.id as row_test_analyte_id, ra.analyte_id as row_analyte_id, raa.name as row_analyte_name,
       ca.analyte_id as col_analyte_id, caa.name as col_analyte_name
  from test t
       join method m on m.id = t.method_id
       join test_analyte ra on ra.test_id = t.id
       join test_analyte ca on ca.test_id = t.id
       join analyte raa on raa.id = ra.analyte_id
       join analyte caa on caa.id = ca.analyte_id
 where ra.test_id = ca.test_id and
       ra.sort_order = (select max(ta.sort_order)
                          from test_analyte ta
                         where ta.test_id = t.id and ta.row_group = ca.row_group and
                               ta.is_column = 'N' and ta.sort_order <= ca.sort_order)
 order by t.name, m.name, ra.sort_order, ca.sort_order
;

create or replace view todo_sample_view as

   select s.id as sample_id, s.domain, s.accession_number, s.received_date, s.collection_date,
       s.collection_time, o.name as primary_organization_name,
       case
           when s.domain = 'E' then
               case
                   when sen.priority is not null then '[pri]' || sen.priority || ' '
                   else ''
               end ||
               case
                   when p.name is not null then '[prj]' || btrim(p.name) || ' '
                   else ''
               end ||
               case
                   when sen.location is not null then '[loc]' || btrim(sen.location)
                   else ''
               end
           when s.domain = 'S' then
               case
                   when pws.name is not null then '[pws]' || btrim(pws.name) || ' '
                   else ''
               end ||
               case
                   when ssd.facility_id is not null then '[fac]' || btrim(ssd.facility_id) || ' '
                   else ''
               end ||
               case
                   when ssd.priority is not null then '[pri]' || ssd.priority
                   else ''
               end
           when s.domain = 'C' then
               case
                   when pat.last_name is not null then btrim(pat.last_name) || ', '
                   else ''
               end ||
               case
                   when pat.first_name is not null then btrim(pat.first_name) || ', '
                   else ''
               end ||
               case
                   when si.container_reference is not null then btrim(si.container_reference)
                   else ''
               end
           when s.domain = 'P' then
               case
                   when ptp.entry is not null then '[ptp]' || btrim(ptp.entry) || ' '
                   else ''
               end ||
               case
                   when spt.series is not null then '[ser]' || btrim(spt.series) || ' '
                   else ''
               end ||
               case
                   when si.container_reference is not null then '[cnt]' || btrim(si.container_reference)
                   else ''
               end
           when s.domain = 'A' then
               case
                   when acn.entry is not null then '[acn]' || btrim(acn.entry) || ' '
                   else ''
               end ||
               case
                   when san.location is not null then '[loc]' || btrim(san.location) || ' '
                   else ''
               end
           else null
       end as description,
       s.status_id as sample_status_id,
       case
           when sqa.id is not null or
                (s.id in (select sample_id
                            from sample_item
                                 join analysis on analysis.sample_item_id = sample_item.id
                                 join analysis_qaevent on analysis_qaevent.analysis_id = analysis.id and
                                                          analysis_qaevent.type_id = (select d.id
                                                                                        from dictionary d
                                                                                       where d.system_name = 'qaevent_override')
                           where sample_item.sample_id = s.id)) then 'Y'
           else 'N'
       end as sample_result_override, s.released_date
  from sample s
       join sample_item si on s.id = si.sample_id
       left join sample_environmental sen on s.id = sen.sample_id
       left join sample_sdwis ssd on s.id = ssd.sample_id
       left join pws on ssd.pws_id = pws.id
       left join sample_clinical scl on s.id = scl.sample_id
       left join patient pat on scl.patient_id = pat.id
       left join sample_pt spt on s.id = spt.sample_id
       left join dictionary ptp on spt.pt_provider_id = ptp.id
       left join sample_animal san on s.id = san.sample_id
       left join dictionary acn on san.animal_common_name_id = acn.id
       left join sample_organization so on s.id = so.sample_id and
                                           so.type_id = (select d.id
                                                           from dictionary d
                                                          where d.system_name = 'org_report_to')
       left join organization o on so.organization_id = o.id
       left join sample_project sp on s.id = sp.sample_id and
                                             sp.is_permanent = 'Y'
       left join project p on sp.project_id = p.id
       left join sample_qaevent sqa on s.id = sqa.sample_id and
                                       sqa.type_id = (select d.id
                                                        from dictionary d
                                                       where d.system_name = 'qaevent_override')
       left join qaevent sq on sqa.qaevent_id = sq.id
;

create or replace view worksheet_analysis_view as

   select wa.id, wi.id as worksheet_item_id, wi.position, w.id as worksheet_id,
       w.format_id, w.description as worksheet_description,
       case
           when wa.analysis_id is not null then s.accession_number::char(10)
           when wa.qc_lot_id is not null then
               case
                   when wa.from_other_id is not null then wi2.worksheet_id || '.' || wi2.position
                   else wi.worksheet_id || '.' || wi.position
               end
           else null
       end as accession_number,
       wa.analysis_id, wa.qc_lot_id, q.id as qc_id, wa.worksheet_analysis_id, wa.system_users,
       wa.started_date, wa.completed_date, wa.from_other_id, wa.change_flags_id,
       case
           when wa.analysis_id is not null then
               case
                   when s.domain = 'E' then
                       case
                           when sen.location is not null then '[loc]' || btrim(sen.location) || ' '
                           else ''
                       end ||
                       case
                           when o.name is not null then '[rpt]' || btrim(o.name)
                           else ''
                       end
                   when s.domain = 'S' then
                       case
                           when ssd.location is not null then '[loc]' || btrim(ssd.location) || ' '
                           else ''
                       end ||
                       case
                           when o.name is not null then '[rpt]' || btrim(o.name)
                           else ''
                       end
                   when s.domain = 'C' then
                       case
                           when pat.last_name is not null then btrim(pat.last_name) || ', '
                           else ''
                       end ||
                       case
                           when pat.first_name is not null then btrim(pat.first_name) || ', '
                           else ''
                       end ||
                       case
                           when si.container_reference is not null then btrim(si.container_reference)
                           else ''
                       end
                   when s.domain = 'P' then
                       case
                           when ptp.entry is not null then '[ptp]' || btrim(ptp.entry) || ' '
                           else ''
                       end ||
                       case
                           when spt.series is not null then '[ser]' || btrim(spt.series) || ' '
                           else ''
                       end ||
                       case
                           when si.container_reference is not null then '[cnt]' || btrim(si.container_reference)
                           else ''
                       end
		           when s.domain = 'A' then
		               case
		                   when acn.entry is not null then '[acn]' || btrim(acn.entry) || ' '
		                   else ''
		               end ||
		               case
		                   when san.location is not null then '[loc]' || btrim(san.location) || ' '
		                   else ''
		               end
                   else null
               end
           when wa.qc_lot_id is not null then btrim(q.name) || ' (' || ql.lot_number || ')'
           else null
       end as description,
       t.id as test_id, t.name as test_name, m.name as method_name, t.time_ta_average, t.time_holding,
       sec.name as section_name, a.unit_of_measure_id, uom.entry as unit_of_measure,
       a.status_id as analysis_status_id, ans.entry as analysis_status, a.type_id as analysis_type_id,
       s.collection_date, s.collection_time, s.received_date,
       case
           when s.domain = 'E' then sen.priority
           when s.domain = 'S' then ssd.priority
           else null::integer
       end as priority
  from worksheet_analysis wa
       join worksheet_item wi on wa.worksheet_item_id = wi.id
       join worksheet w on wi.worksheet_id = w.id
       left join analysis a on wa.analysis_id = a.id
       left join test t on a.test_id = t.id
       left join method m on t.method_id = m.id
       left join section sec on a.section_id = sec.id
       left join sample_item si on a.sample_item_id = si.id
       left join sample s on si.sample_id = s.id
       left join sample_environmental sen on s.id = sen.sample_id
       left join sample_sdwis ssd on s.id = ssd.sample_id
       left join pws on ssd.pws_id = pws.id
       left join sample_clinical scl on s.id = scl.sample_id
       left join patient pat on scl.patient_id = pat.id
       left join sample_pt spt on s.id = spt.sample_id
       left join dictionary ptp on spt.pt_provider_id = ptp.id
       left join sample_animal san on s.id = san.sample_id
       left join dictionary acn on san.animal_common_name_id = acn.id
       left join sample_organization so on s.id = so.sample_id and
                                           so.type_id = (select d.id
                                                           from dictionary d
                                                          where d.system_name = 'org_report_to')
       left join organization o on so.organization_id = o.id
       left join dictionary uom on a.unit_of_measure_id = uom.id
       left join dictionary ans on a.status_id = ans.id
       left join qc_lot ql on wa.qc_lot_id = ql.id
       left join qc q on ql.qc_id = q.id
       left join worksheet_analysis wa2 on wa.from_other_id = wa2.id
       left join worksheet_item wi2 on wa2.worksheet_item_id = wi2.id
;

create or replace view worksheet_qc_result_view as

   select wqr.id, wqr.worksheet_analysis_id, wqr.sort_order, wqr.value_1, wqr.value_2,
       wqr.value_3, wqr.value_4, wqr.value_5, wqr.value_6, wqr.value_7, wqr.value_8,
       wqr.value_9, wqr.value_10, wqr.value_11, wqr.value_12, wqr.value_13, wqr.value_14,
       wqr.value_15, wqr.value_16, wqr.value_17, wqr.value_18, wqr.value_19, wqr.value_20,
       wqr.value_21, wqr.value_22, wqr.value_23, wqr.value_24, wqr.value_25, wqr.value_26,
       wqr.value_27, wqr.value_28, wqr.value_29, wqr.value_30, q.name as qc_name,
       q.type_id as qc_type_id, q.source, ql.lot_number, ql.location_id, ql.prepared_date,
       ql.prepared_volume, ql.prepared_unit_id, ql.prepared_by_id, ql.usable_date, ql.expire_date,
       qa.analyte_id, qa.type_id as qc_analyte_type_id, qa.value as expected_value, w.format_id
  from worksheet_qc_result wqr
       join worksheet_analysis wa on wqr.worksheet_analysis_id = wa.id
       join worksheet_item wi on wa.worksheet_item_id = wi.id
       join worksheet w on wi.worksheet_id = w.id
       join qc_analyte qa on wqr.qc_analyte_id = qa.id
       join qc_lot ql on wa.qc_lot_id = ql.id
       join qc q on ql.qc_id = q.id
;