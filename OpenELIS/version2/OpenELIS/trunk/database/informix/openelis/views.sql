create view analysis_view (sample_id, domain, accession_number, received_date,
                           collection_date, collection_time, entered_date,
                           primary_organization_name, todo_description, worksheet_description,
                           priority, test_id, test_name, method_name, time_ta_average,
                           time_holding, type_of_sample_id, analysis_id, analysis_status_id,
                           section_id, section_name, available_date, started_date,
                           completed_date, released_date, analysis_result_override,
                           unit_of_measure_id, worksheet_format_id)
                           AS
                           (
select s.id, s.domain, s.accession_number, s.received_date, s.collection_date,
       s.collection_time, s.entered_date,
       case
           when spw.organization_id is not null then spw_org.name
           when spw.report_to_name is not null then spw.report_to_name
           else o.name
       end,
       case 
           when s.domain = 'E' then
               case
                   when sen.priority is not null then '[pri]' || sen.priority || ' '
                   else ''
               end ||
               case
                   when p.name is not null then '[prj]' || trim(p.name) || ' '
                   else ''
               end ||
               case
                   when sen.location is not null then '[loc]' || trim(sen.location)
                   else ''
               end
           when s.domain = 'W' then
               case
                   when spw.owner is not null then '[own]' || trim(spw.owner)
                   else null
               end
           when s.domain = 'S' then
               case
                   when pws.name is not null then '[pws]' || trim(pws.name) || ' '
                   else ''
               end ||
               case
                   when ssd.facility_id is not null then '[fac]' || trim(ssd.facility_id) || ' '
                   else ''
               end ||
               case
                   when ssd.priority is not null then '[pri]' || ssd.priority
                   else ''
               end
           when s.domain = 'C' then
               case
                   when pat.last_name is not null then '[lst]' || trim(pat.last_name) || ' '
                   else ''
               end ||
               case
                   when pat.first_name is not null then '[fst]' || trim(pat.first_name) || ' '
                   else ''
               end ||
               case
                   when si.container_reference is not null then '[cnt]' || trim(si.container_reference)
                   else ''
               end
           when s.domain = 'P' then
               case
                   when ptp.entry is not null then '[ptp]' || trim(ptp.entry) || ' '
                   else ''
               end ||
               case
                   when spt.series is not null then '[ser]' || trim(spt.series) || ' '
                   else ''
               end ||
               case
                   when si.container_reference is not null then '[cnt]' || trim(si.container_reference)
                   else ''
               end
           else null
       end,
       case
           when s.domain = 'E' then
               case
                   when sen.location is not null then '[loc]' || trim(sen.location) || ' '
                   else ''
               end ||
               case
                   when o.name is not null then '[rpt]' || trim(o.name)
                   else ''
               end
           when s.domain = 'S' then
               case
                   when ssd.location is not null then '[loc]' || trim(ssd.location) || ' '
                   else ''
               end ||
               case
                   when o.name is not null then '[rpt]' || trim(o.name)
                   else ''
               end
           when s.domain = 'W' then
               case
                   when spw.location is not null then '[loc]' || trim(spw.location)
                   else null
               end
           when s.domain = 'C' then
               case
                   when pat.last_name is not null then '[lst]' || tim(pat.last_name) || ' '
                   else ''
               end ||
               case
                   when pat.first_name is not null then '[fst]' || trim(pat.first_name) || ' '
                   else ''
               end ||
               case
                   when si.container_reference is not null then '[cnt]' || trim(si.container_reference)
                   else ''
               end
           when s.domain = 'P' then
               case
                   when ptp.entry is not null then '[ptp]' || trim(ptp.entry) || ' '
                   else ''
               end ||
               case
                   when spt.series is not null then '[ser]' || trim(spt.series) || ' '
                   else ''
               end ||
               case
                   when si.container_reference is not null then '[cnt]' || trim(si.container_reference)
                   else ''
               end
           else null
       end,
       case
           when s.domain = 'E' then sen.priority
           when s.domain = 'S' then ssd.priority
           else null
       end,
       t.id, t.name, m.name, t.time_ta_average, t.time_holding, si.type_of_sample_id, 
       a.id, a.status_id, sec.id, sec.name, a.available_date, a.started_date, a.completed_date,
       a.released_date,
       case
           when sqa.id is not null or aqa.id is not null then 'Y'
           else 'N'
       end,
       a.unit_of_measure_id, tw.format_id
  from sample s
       join sample_item si on s.id = si.sample_id
       join analysis a on si.id = a.sample_item_id
       join test t on a.test_id = t.id
       join method m on t.method_id = m.id
       join section sec on a.section_id = sec.id
       left join sample_environmental sen on s.id = sen.sample_id
       left join sample_private_well spw on s.id = spw.sample_id
       left join organization spw_org on spw.organization_id = spw_org.id
       left join sample_sdwis ssd on s.id = ssd.sample_id
       left join pws on ssd.pws_id = pws.id
       left join sample_clinical scl on s.id = scl.sample_id
       left join patient pat on scl.patient_id = pat.id
       left join sample_pt spt on s.id = spt.sample_id
       left join dictionary ptp on spt.pt_provider_id = ptp.id
       left join sample_organization so on s.id = so.sample_id and
                                           so.type_id = (select dictionary.id
                                                           from dictionary
                                                          where dictionary.system_name = 'org_report_to')
       left join organization o on so.organization_id = o.id
       left join sample_project sp on s.id = sp.sample_id and
                                       sp.is_permanent = 'Y'
       left join project p on sp.project_id = p.id
       left join sample_qaevent sqa on s.id = sqa.sample_id and
                                              sqa.type_id = (select dictionary.id
                                                               from dictionary
                                                              where dictionary.system_name = 'qaevent_override')
       left join qaevent sq on sqa.qaevent_id = sq.id
       left join analysis_qaevent aqa on a.id = aqa.analysis_id and
                                         aqa.type_id = (select dictionary.id
                                                          from dictionary
                                                         where dictionary.system_name = 'qaevent_override')
       left join qaevent aq on aqa.qaevent_id = aq.id
       left join test_worksheet tw on tw.test_id = t.id);

create view sample_patient_view (sample_id, patient_id)
                                 AS
                                 (
select sn.sample_id, sn.patient_id
  from sample_neonatal sn
union
select sc.sample_id, sc.patient_id
  from sample_clinical sc);

create view sample_view (sample_id, domain, accession_number, sample_revision, received_date,
                         collection_date, collection_time, sample_status_id, client_reference,
                         sample_released_date, report_to_id, report_to_name, collector,
                         location, location_city, project_id, project_name, pws_number0,
                         pws_name, sdwis_facility_id, patient_last_name, patient_first_name,
                         patient_birth_date, provider_name, analysis_id, analysis_revision, analysis_is_reportable,
                         analysis_status_id, analysis_released_date, test_reporting_description,
                         method_reporting_description)
                         AS
                         (
select s.id, s.domain, s.accession_number, s.revision, s.received_date, s.collection_date,
       s.collection_time, s.status_id, s.client_reference, s.released_date,
       case
           when s.domain = 'W' then spwo.id
           else o.id
       end,
       case
           when s.domain = 'W' then spwo.name
           else o.name
       end,
       case 
           when s.domain = 'E' then sen.collector
           when s.domain = 'S' then ssd.collector
           when s.domain = 'W' then spw.collector
           else null
       end,
       case 
           when s.domain = 'E' then sen.location
           when s.domain = 'S' then ssd.location
           when s.domain = 'W' then spw.location
           else null
       end,
       case
           when s.domain = 'E' then ead.city
           when s.domain = 'W' then wad.city
           else null
       end,
       p.id, p.name, pws.number0, pws.name, ssd.facility_id,
       case 
           when s.domain = 'C' then cpa.last_name
           when s.domain = 'N' then npa.last_name
           else null
       end,
       case 
           when s.domain = 'C' then cpa.first_name
           when s.domain = 'N' then npa.first_name
           else null
       end,
       case 
           when s.domain = 'C' then cpa.birth_date
           when s.domain = 'N' then npa.birth_date
           else null
       end,
       case 
           when s.domain = 'C' then
               case
                   when pv.last_name is not null then trim(pv.last_name) || ', '
                   else ''
               end ||
               case
                   when pv.first_name is not null then trim(pv.first_name)
                   else ''
               end
           else null
       end,
       a.id, a.revision, a.is_reportable, a.status_id, a.released_date, t.reporting_description, m.reporting_description
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
     left join sample_private_well spw on s.id = spw.sample_id
     left join organization spwo on spw.organization_id = spwo.id
     left join address wad on spw.location_address_id = wad.id
     left join sample_organization so on s.id = so.sample_id and
                                         so.type_id = (select dictionary.id
                                                         from dictionary
                                                        where dictionary.system_name = 'org_report_to')
     left join organization o on so.organization_id = o.id
     left join sample_project sp on s.id = sp.sample_id and
                                    sp.is_permanent = 'Y'
     left join project p on sp.project_id = p.id);

create view test_analyte_view (id, test_id, test_name, method_id, method_name,
                               test_is_active, test_active_begin, test_active_end,
                               row_test_analyte_id, row_analyte_id, row_analyte_name,
                               col_analyte_id, col_analyte_name)
                               AS
select ca.id, t.id, t.name, m.id, m.name,
       t.is_active, t.active_begin, t.active_end,
       ra.id, ra.analyte_id, raa.name,
       ca.analyte_id, caa.name
  from test t
       join method m on m.id = t.method_id
       join test_analyte ra on ra.test_id = t.id
       join test_analyte ca on ca.test_id = t.id
       join analyte raa on raa.id = ra.analyte_id
       join analyte caa on caa.id = ca.analyte_id
 where ra.test_id = ca.test_id and
       ra.sort_order = (select max(ta.sort_order) from test_analyte ta
                         where ta.test_id = t.id and
                               ta.row_group = ca.row_group and
                               ta.is_column = 'N' and
                               ta.sort_order <= ca.sort_order)
order by t.name, m.name, ra.sort_order, ca.sort_order;

create view todo_sample_view (sample_id, domain, accession_number, received_date,
                              collection_date, collection_time, primary_organization_name,
                              description, sample_status_id, sample_result_override)
                              AS
                              (
select s.id, s.domain, s.accession_number, s.received_date, s.collection_date,
       s.collection_time,
       case
           when spw.organization_id is not null then spw_org.name
           when spw.report_to_name is not null then spw.report_to_name
           else o.name
       end,
       case 
           when s.domain = 'E' then
               case
                   when sen.priority is not null then '[pri]' || sen.priority || ' '
                   else ''
               end ||
               case
                   when p.name is not null then '[prj]' || trim(p.name) || ' '
                   else ''
               end ||
               case
                   when sen.location is not null then '[loc]' || trim(sen.location)
                   else ''
               end
            when s.domain = 'W' then
                case
                    when spw.owner is not null then '[own]' || trim(spw.owner)
                    else null
                end
            when s.domain = 'S' then
                case
                    when pws.name is not null then '[pws]' || trim(pws.name) || ' '
                    else ''
                end ||
                case
                    when ssd.facility_id is not null then ' [fac]' || trim(ssd.facility_id) || ' '
                    else ''
                end ||
                case
                    when ssd.priority is not null then ' [pri]' || ssd.priority
                    else ''
                end
            when s.domain = 'C' then
                case
                    when pat.last_name is not null then '[lst]' || trim(pat.last_name) || ' '
                    else ''
                end ||
                case
                    when pat.first_name is not null then '[fst]' || trim(pat.first_name) || ' '
                    else ''
                end ||
                case
                    when si.container_reference is not null then '[cnt]' || trim(si.container_reference)
                    else ''
                end
            when s.domain = 'P' then
                case
                    when ptp.entry is not null then '[ptp]' || trim(ptp.entry) || ' '
                    else ''
                end ||
                case
                    when spt.series is not null then '[ser]' || trim(spt.series) || ' '
                    else ''
                end ||
                case
                    when si.container_reference is not null then '[cnt]' || trim(si.container_reference)
                    else ''
                end
            else null
       end,
       s.status_id,
       case
           when sqa.id is not null or
                (s.id in (select sample_id
                            from sample_item
                                 join analysis on analysis.sample_item_id = sample_item.id
                                 join analysis_qaevent on analysis_qaevent.analysis_id = analysis.id and
                                                          analysis_qaevent.type_id = (select dictionary.id from dictionary
                                                                                       where dictionary.system_name = 'qaevent_override')
                           where sample_item.sample_id = s.id)) then "Y"
           else "N"
       end
 from sample s
      join sample_item si on s.id = si.sample_id
      left join sample_environmental sen on s.id = sen.sample_id
      left join sample_private_well spw on s.id = spw.sample_id
      left join organization spw_org on spw.organization_id = spw_org.id
      left join sample_sdwis ssd on s.id = ssd.sample_id
      left join pws on ssd.pws_id = pws.id
      left join sample_clinical scl on s.id = scl.sample_id
      left join patient pat on scl.patient_id = pat.id
      left join sample_pt spt on s.id = spt.sample_id
      left join dictionary ptp on spt.pt_provider_id = ptp.id
      left join sample_organization so on s.id = so.sample_id and
                                          so.type_id = (select dictionary.id
                                                          from dictionary
                                                         where dictionary.system_name = 'org_report_to')
      left join organization o on so.organization_id = o.id
      left join sample_project sp on s.id = sp.sample_id and
                                            sp.is_permanent = 'Y'
      left join project p on sp.project_id = p.id
      left join sample_qaevent sqa on s.id = sqa.sample_id and
                                             sqa.type_id = (select dictionary.id 
                                                              from dictionary
                                                             where dictionary.system_name = 'qaevent_override')
      left join qaevent sq on sqa.qaevent_id = sq.id);

create view worksheet_analysis_view (id, worksheet_item_id, postition, worksheet_id,
                                     format_id, worksheet_description, accession_number,
                                     analysis_id, qc_lot_id, qc_id, worksheet_analysis_id,
                                     system_users, started_date, completed_date,
                                     from_other_id, change_flags_id, description,
                                     test_id, test_name, method_name, time_ta_average,
                                     time_holding, section_name, unit_of_measure_id,
                                     unit_of_measure, analysis_status_id, analysis_status,
                                     collection_date, collection_time, received_date,
                                     priority)
                                     AS
                                     (
select wa.id, wi.id, wi.position, w.id, w.format_id, w.description,
       case 
           when wa.analysis_id is not null then s.accession_number::char(10)
           when wa.qc_lot_id is not null then
               case
                   when wa.from_other_id is not null then wi2.worksheet_id || '.' || wi2.position
                   else wi.worksheet_id || '.' || wi.position
               end
           else null
       end,
       wa.analysis_id, wa.qc_lot_id, q.id, wa.worksheet_analysis_id, wa.system_users,
       wa.started_date, wa.completed_date, wa.from_other_id, wa.change_flags_id,
       case
           when wa.analysis_id is not null then
               case 
                   when s.domain = 'E' then
                       case
                           when sen.location is not null then '[loc]' || trim(sen.location) || ' '
                           else ''
                       end ||
                       case
                           when o.name is not null then '[rpt]' || trim(o.name)
                           else ''
                       end
                   when s.domain = 'S' then
                       case
                           when ssd.location is not null then '[loc]' || trim(ssd.location) || ' '
                           else ''
                       end ||
                       case
                           when o.name is not null then '[rpt]' || trim(o.name)
                           else ''
                       end
                   when s.domain = 'W' then
                       case
                           when spw.location is not null then '[loc]' || trim(spw.location) || ' '
                           else ''
                       end ||
                       case
                           when o.name is not null then '[rpt]' || trim(o.name)
                           else ''
                       end
                   when s.domain = 'C' then
                       case
                           when pat.last_name is not null then '[lst]' || trim(pat.last_name) || ' '
                           else ''
                       end ||
                       case
                           when pat.first_name is not null then '[fst]' || trim(pat.first_name) || ' '
                           else ''
                       end ||
                       case
                           when si.container_reference is not null then '[cnt]' || trim(si.container_reference)
                           else ''
                       end
                   when s.domain = 'P' then
                       case
                           when ptp.entry is not null then '[ptp]' || trim(ptp.entry) || ' '
                           else ''
                       end ||
                       case
                           when spt.series is not null then '[ser]' || trim(spt.series) || ' '
                           else ''
                       end ||
                       case
                           when si.container_reference is not null then '[cnt]' || trim(si.container_reference)
                           else ''
                       end
                   else null
               end
           when wa.qc_lot_id is not null then trim(q.name) || ' (' || ql.lot_number || ')'
           else null
       end,
       t.id, t.name, m.name, t.time_ta_average, t.time_holding, sec.name, a.unit_of_measure_id,
       uom.entry, a.status_id, ans.entry, s.collection_date, s.collection_time, s.received_date,
       case
           when s.domain = 'E' then sen.priority
           when s.domain = 'S' then ssd.priority
           else null::integer
       end
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
       left join sample_private_well spw on s.id = spw.sample_id
       left join organization spw_org on spw.organization_id = spw_org.id
       left join sample_sdwis ssd on s.id = ssd.sample_id
       left join pws on ssd.pws_id = pws.id
       left join sample_clinical scl on s.id = scl.sample_id
       left join patient pat on scl.patient_id = pat.id
       left join sample_pt spt on s.id = spt.sample_id
       left join dictionary ptp on spt.pt_provider_id = ptp.id
       left join sample_organization so on s.id = so.sample_id and
                                           so.type_id = (select dictionary.id
                                                           from dictionary
                                                          where dictionary.system_name = 'org_report_to')
       left join organization o on so.organization_id = o.id
       left join dictionary uom on a.unit_of_measure_id = uom.id
       left join dictionary ans on a.status_id = ans.id
       left join qc_lot ql on wa.qc_lot_id = ql.id
       left join qc q on ql.qc_id = q.id
       left join worksheet_analysis wa2 on wa.from_other_id = wa2.id
       left join worksheet_item wi2 on wa2.worksheet_item_id = wi2.id);

create view worksheet_qc_result_view (id, worksheet_analysis_id, sort_order, value_1,
                                      value_2, value_3, value_4, value_5, value_6,
                                      value_7, value_8, value_9, value_10, value_11,
                                      value_12, value_13, value_14, value_15, value_16,
                                      value_17, value_18, value_19, value_20, value_21,
                                      value_22, value_23, value_24, value_25, value_26,
                                      value_27, value_28, value_29, value_30, qc_name,
                                      qc_type_id, source, lot_number, location_id,
                                      prepared_date, prepared_volume, prepared_unit_id,
                                      prepared_by_id, usable_date, expire_date,
                                      analyte_id, qc_analyte_type_id, expected_value,
                                      format_id)
                                      AS
                                      (
select wqr.id, wqr.worksheet_analysis_id, wqr.sort_order, wqr.value_1, wqr.value_2,
       wqr.value_3, wqr.value_4, wqr.value_5, wqr.value_6, wqr.value_7, wqr.value_8,
       wqr.value_9, wqr.value_10, wqr.value_11, wqr.value_12, wqr.value_13, wqr.value_14,
       wqr.value_15, wqr.value_16, wqr.value_17, wqr.value_18, wqr.value_19, wqr.value_20,
       wqr.value_21, wqr.value_22, wqr.value_23, wqr.value_24, wqr.value_25, wqr.value_26,
       wqr.value_27, wqr.value_28, wqr.value_29, wqr.value_30, q.name, q.type_id,
       q.source, ql.lot_number, ql.location_id, ql.prepared_date, ql.prepared_volume,
       ql.prepared_unit_id, ql.prepared_by_id, ql.usable_date, ql.expire_date, qa.analyte_id,
       qa.type_id, qa.value, w.format_id
  from worksheet_qc_result wqr
       join worksheet_analysis wa on wqr.worksheet_analysis_id = wa.id
       join worksheet_item wi on wa.worksheet_item_id = wi.id
       join worksheet w on wi.worksheet_id = w.id
       join qc_analyte qa on wqr.qc_analyte_id = qa.id
       join qc_lot ql on wa.qc_lot_id = ql.id
       join qc q on ql.qc_id = q.id);