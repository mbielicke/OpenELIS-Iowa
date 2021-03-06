/**
 * Exhibit A - UIRF Open-source Based Public Software License.
 * 
 * The contents of this file are subject to the UIRF Open-source Based Public
 * Software License(the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * openelis.uhl.uiowa.edu
 * 
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License for
 * the specific language governing rights and limitations under the License.
 * 
 * The Original Code is OpenELIS code.
 * 
 * The Initial Developer of the Original Code is The University of Iowa.
 * Portions created by The University of Iowa are Copyright 2006-2008. All
 * Rights Reserved.
 * 
 * Contributor(s): ______________________________________.
 * 
 * Alternatively, the contents of this file marked "Separately-Licensed" may be
 * used under the terms of a UIRF Software license ("UIRF Software License"), in
 * which case the provisions of a UIRF Software License are applicable instead
 * of those above.
 */
package org.openelis.entity;

/**
 * Sample Entity POJO for database
 */

import java.util.Collection;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.ColumnResult;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.NamedNativeQueries;
import javax.persistence.NamedNativeQuery;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.SqlResultSetMapping;
import javax.persistence.SqlResultSetMappings;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.openelis.domain.Constants;
import org.openelis.ui.common.DataBaseUtil;
import org.openelis.ui.common.Datetime;
import org.openelis.utils.Audit;
import org.openelis.utils.AuditUtil;
import org.openelis.utils.Auditable;

@NamedQueries({
    @NamedQuery(name = "Sample.FetchById",
               query = "select new org.openelis.domain.SampleDO(id, nextItemSequence, domain, "
                     + "accessionNumber, revision, orderId, enteredDate, receivedDate, receivedById, collectionDate, collectionTime,"
                     + "statusId, packageId, clientReference, releasedDate)"
                     + " from Sample where id = :id"),
    @NamedQuery(name = "Sample.FetchByIds",
               query = "select new org.openelis.domain.SampleDO(id, nextItemSequence, domain, "
                     + "accessionNumber, revision, orderId, enteredDate, receivedDate, receivedById, collectionDate, collectionTime,"
                     + "statusId, packageId, clientReference, releasedDate)"
                     + " from Sample where id in (:ids)"),
    @NamedQuery(name = "Sample.FetchByAccessionNumber",
               query = "select new org.openelis.domain.SampleDO(id, nextItemSequence, domain,"
                     + "accessionNumber, revision, orderId, enteredDate, receivedDate,"
                     + "receivedById, collectionDate, collectionTime, statusId, packageId,"
                     + "clientReference, releasedDate)"
                     + " from Sample where accessionNumber = :accession"),
    @NamedQuery(name = "Sample.FetchByAccessionNumbers",
               query = "select new org.openelis.domain.SampleDO(id, nextItemSequence, domain,"
                     + "accessionNumber, revision, orderId, enteredDate, receivedDate,"
                     + "receivedById, collectionDate, collectionTime, statusId, packageId,"
                     + "clientReference, releasedDate)"
                     + " from Sample where accessionNumber in (:accessions)"),
    @NamedQuery(name = "Sample.FetchByEOrderId",
               query = "select new org.openelis.domain.SampleDO(id, nextItemSequence, domain, "
                     + "accessionNumber, revision, orderId, enteredDate, receivedDate, receivedById, collectionDate, collectionTime,"
                     + "statusId, packageId, clientReference, releasedDate)"
                     + " from Sample s where s.domain in ('C','N') and s.orderId = :eorderId"),
    @NamedQuery(name = "Sample.FetchSDWISByReleased",
               query = "select distinct new org.openelis.domain.SampleDO(s.id, s.nextItemSequence, s.domain,"
                     + "s.accessionNumber, s.revision, s.orderId, s.enteredDate, s.receivedDate,"
                     + "s.receivedById, s.collectionDate, s.collectionTime, s.statusId, s.packageId,"
                     + "s.clientReference, s.releasedDate)"
                     + " from Sample s where s.domain = 'S' and s.releasedDate between :startDate and :endDate")})
@NamedNativeQueries({
    @NamedNativeQuery(name = "Sample.FetchForFinalReportBatch",
                     query = "select s.id s_id, s.accession_number s_accession_number, s.revision s_revision, CAST(s.domain AS varchar(1)) s_domain, so.organization_id o_id, CAST(o.name AS varchar(40)) o_name, so.type_id o_type_id, so.organization_attention o_attention, a.id a_id"
                           + " from sample s, sample_item si, analysis a, sample_organization so, organization o"
                           + " where s.status_id in (select id from dictionary where system_name = 'sample_released') and"
                           + " si.sample_id = s.id and a.sample_item_id = si.id and a.printed_date is null and"
                           + " a.status_id in (select id from dictionary where system_name = 'analysis_released') and a.is_reportable = 'Y' and"
                           + " so.sample_id = s.id and so.organization_id = o.id and so.type_id in (select id from dictionary where system_name in ('org_report_to', 'org_second_report_to'))"
                           + " union "
                           + "select s.id s_id, s.accession_number s_accession_number, s.revision s_revision, CAST(s.domain AS varchar(1)) s_domain, so.organization_id o_id, CAST(o.name AS varchar(40)) o_name, so.type_id o_type_id, so.organization_attention o_attention, a.id a_id"
                           + " from sample s, sample_item si, analysis a, test t, sample_organization so, organization o"
                           + " where s.status_id not in (select id from dictionary where system_name in ('sample_released', 'sample_error', 'sample_not_verified')) and"
                           + " si.sample_id = s.id and a.sample_item_id = si.id and a.printed_date is null and"
                           + " a.status_id in (select id from dictionary where system_name = 'analysis_released') and a.is_reportable = 'Y' and"
                           + " a.test_id = t.id and t.reporting_method_id in (select id from dictionary where system_name = 'analyses_released') and"
                           + " so.sample_id = s.id and so.organization_id = o.id and so.type_id in (select id from dictionary where system_name in ('org_report_to', 'org_second_report_to'))"
                           + " order by o_name, o_id, s_id",
          resultSetMapping = "Sample.FetchForFinalReportBatchMapping"),
    @NamedNativeQuery(name = "Sample.FetchForFinalReportBatchReprint",
                     query = "select s.id s_id, s.accession_number s_accession_number, s.revision s_revision, CAST(s.domain AS varchar(1)) s_domain, so.organization_id o_id, CAST(o.name AS varchar(40)) o_name, so.type_id o_type_id, so.organization_attention o_attention, a.id a_id"
                           + " from sample s, sample_item si, analysis a, sample_organization so, organization o"
                           + " where s.status_id in (select id from dictionary where system_name = 'sample_released') and"
                           + " si.sample_id = s.id and a.sample_item_id = si.id and a.printed_date between :beginPrinted and :endPrinted and"
                           + " a.status_id in (select id from dictionary where system_name = 'analysis_released') and a.is_reportable = 'Y' and"
                           + " so.sample_id = s.id and so.organization_id = o.id and so.type_id in (select id from dictionary where system_name in ('org_report_to', 'org_second_report_to'))"
                           + " union "
                           + "select s.id s_id, s.accession_number s_accession_number, s.revision s_revision, CAST(s.domain AS varchar(1)) s_domain, so.organization_id o_id, CAST(o.name AS varchar(40)) o_name, so.type_id o_type_id, so.organization_attention o_attention, a.id a_id"
                           + " from sample s, sample_item si, analysis a, test t, sample_organization so, organization o"
                           + " where s.status_id not in (select id from dictionary where system_name in ('sample_released', 'sample_error', 'sample_not_verified')) and"
                           + " si.sample_id = s.id and a.sample_item_id = si.id and a.printed_date between :beginPrinted and :endPrinted and"
                           + " a.status_id in (select id from dictionary where system_name = 'analysis_released') and a.is_reportable = 'Y' and"
                           + " a.test_id = t.id and t.reporting_method_id in (select id from dictionary where system_name = 'analyses_released') and"
                           + " so.sample_id = s.id and so.organization_id = o.id and so.organization_id = o.id and"
                           + " so.type_id in (select id from dictionary where system_name in ('org_report_to', 'org_second_report_to'))"
                           + " order by o_name, o_id, s_id",
          resultSetMapping = "Sample.FetchForFinalReportBatchReprintMapping"),
    @NamedNativeQuery(name = "Sample.FetchForFinalReportSingle",
                     query = "select distinct s.id s_id, s.accession_number s_accession_number, s.revision s_revision, CAST(s.domain AS varchar(1)) s_domain, so.organization_id o_id, so.type_id o_type_id, CAST(o.name AS varchar(40)) o_name"
                           + " from sample s, sample_item si, analysis a, sample_organization so, organization o"
                           + " where s.accession_number = :accessionNumber and s.status_id not in (select id from dictionary where system_name in ('sample_error', 'sample_not_verified')) and"
                           + " si.sample_id = s.id and a.sample_item_id = si.id and a.status_id in (select id from dictionary where system_name = 'analysis_released') and a.is_reportable = 'Y' and"
                           + " so.sample_id = s.id and so.organization_id = o.id and so.type_id in (select id from dictionary where system_name in ('org_report_to', 'org_second_report_to'))"
                           + " order by o_name, o_id, s_id",
          resultSetMapping = "Sample.FetchForFinalReportSingleMapping"),
    @NamedNativeQuery(name = "Sample.FetchForFinalReportPreview",
                     query = "select distinct s.id s_id, s.accession_number s_accession_number, s.revision s_revision, CAST(s.domain AS varchar(1)) s_domain, so.organization_id o_id"
                           + " from sample s, sample_item si, analysis a, sample_organization so"
                           + " where s.accession_number = :accessionNumber and si.sample_id = s.id and a.sample_item_id = si.id and"
                           + " a.status_id in (select id from dictionary where system_name in ('analysis_released', 'analysis_completed')) and a.is_reportable = 'Y' and"
                           + " so.sample_id = s.id and so.type_id in (select id from dictionary where system_name = 'org_report_to')"
                           + " order by s_id , o_id",
          resultSetMapping = "Sample.FetchForFinalReportPreviewMapping"),
    @NamedNativeQuery(name = "Sample.FetchForClientEmailReceivedReport",
                     query = "select s.accession_number, s.collection_date, s.collection_time, s.received_date,"
                           +       " op.value email, sq.qaevent_id s_qaevent_id, aq.qaevent_id a_qaevent_id,"
                           +       " se.collector ref_field1, se.location ref_field2, CAST(s.client_reference AS varchar(20)) ref_field3,"
                           +       " CAST(p.name AS varchar(20)) ref_field4"
                           +  " from sample s"
                           +  " join sample_item si on si.sample_id = s.id"
                           +  " left join sample_project sp on sp.sample_id = s.id and sp.is_permanent = 'Y'"
                           +  " left join project p on p.id = sp.project_id"
                           +  " join sample_organization so on so.sample_id = s.id and so.type_id = (select id from dictionary where system_name = 'org_report_to')"
                           +  " join organization_parameter op on op.organization_id = so.organization_id and op.type_id = (select id from dictionary where system_name = 'receivable_reportto_email')"
                           +  " left join sample_qaevent sq on sq.sample_id = s.id and sq.type_id = (select id from dictionary where system_name = 'qaevent_override')"
                           +  " join sample_environmental se on se.sample_id = s.id"
                           +  " join analysis a on a.sample_item_id = si.id and a.id in (select f.analysis_id from analysis_report_flags f where f.analysis_id = a.id and f.notified_received = 'N')"
                           +  " left join analysis_qaevent aq on aq.analysis_id = a.id and aq.type_id = (select id from dictionary where system_name = 'qaevent_override')"
                           + " where s.entered_date between :start_entered_date and :end_entered_date and"
                           +       " s.status_id != (select id from dictionary where system_name = 'sample_not_verified')"
                           +" UNION"
                           +" select s.accession_number, s.collection_date, s.collection_time, s.received_date,"
                           +       " op.value email, sq.qaevent_id s_qaevent_id, aq.qaevent_id a_qaevent_id,"
                           +       " ss.collector ref_field1, ss.location ref_field2, CAST(s.client_reference AS varchar(20)) ref_field3,"
                           +       " CAST(p.name AS varchar(20)) ref_field4"
                           +  " from sample s"
                           +  " join sample_item si on si.sample_id = s.id"
                           +  " left join sample_project sp on sp.sample_id = s.id and sp.is_permanent = 'Y'"
                           +  " left join project p on p.id = sp.project_id"
                           +  " join sample_organization so on so.sample_id = s.id and so.type_id = (select id from dictionary where system_name = 'org_report_to')"
                           +  " join organization_parameter op on op.organization_id = so.organization_id and op.type_id = (select id from dictionary where system_name = 'receivable_reportto_email')"
                           +  " left join sample_qaevent sq on sq.sample_id = s.id and sq.type_id = (select id from dictionary where system_name = 'qaevent_override')"
                           +  " join sample_sdwis ss on ss.sample_id = s.id"
                           +  " join analysis a on a.sample_item_id = si.id and a.id in (select f.analysis_id from analysis_report_flags f where f.analysis_id = a.id and f.notified_received = 'N')"
                           +  " left join analysis_qaevent aq on aq.analysis_id = a.id and aq.type_id = (select id from dictionary where system_name = 'qaevent_override')"
                           + " where s.entered_date between :start_entered_date and :end_entered_date and"
                           +       " s.status_id != (select id from dictionary where system_name = 'sample_not_verified')"
                           +" UNION"
                           +" select s.accession_number, s.collection_date, s.collection_time, s.received_date,"
                           +       " op.value email, sq.qaevent_id s_qaevent_id, aq.qaevent_id a_qaevent_id,"
                           +       " CAST(pv.last_name AS varchar(30)) ref_field1, CAST(pv.first_name AS varchar(20)) ref_field2, CAST(s.client_reference AS varchar(20)) ref_field3,"
                           +       " CAST(p.name AS varchar(20)) ref_field4"
                           +  " from sample s"
                           +  " join sample_item si on si.sample_id = s.id"
                           +  " left join sample_project sp on sp.sample_id = s.id and sp.is_permanent = 'Y'"
                           +  " left join project p on p.id = sp.project_id"
                           +  " join sample_organization so on so.sample_id = s.id and so.type_id = (select id from dictionary where system_name = 'org_report_to')"
                           +  " join organization_parameter op on op.organization_id = so.organization_id and op.type_id = (select id from dictionary where system_name = 'receivable_reportto_email')"
                           +  " left join sample_qaevent sq on sq.sample_id = s.id and sq.type_id = (select id from dictionary where system_name = 'qaevent_override')"
                           +  " join sample_clinical sc on sc.sample_id = s.id"
                           +  " left join provider pv on pv.id = sc.provider_id"
                           +  " join analysis a on a.sample_item_id = si.id and a.id in (select f.analysis_id from analysis_report_flags f where f.analysis_id = a.id and f.notified_received = 'N')"
                           +  " left join analysis_qaevent aq on aq.analysis_id = a.id and aq.type_id = (select id from dictionary where system_name = 'qaevent_override')"
                           + " where s.entered_date between :start_entered_date and :end_entered_date and"
                           +       " s.status_id != (select id from dictionary where system_name = 'sample_not_verified')"
                           + " order by email, accession_number",
          resultSetMapping = "Sample.FetchForClientEmailReceivedReportMapping"),
    @NamedNativeQuery(name = "Sample.FetchForClientEmailReleasedReport",
                     query = "select distinct s.accession_number, s.collection_date,s.collection_time, s.received_date, op.value email,"
                           +       " se.collector ref_field1, se.location ref_field2, CAST(s.client_reference AS varchar(20)) ref_field3,"
                           +       " CAST(p.name AS varchar(20)) ref_field4, a.id a_id"
                           +  " from sample s"
                           +  " join sample_item si on si.sample_id = s.id"
                           +  " left join sample_project sp on sp.sample_id = s.id and sp.is_permanent = 'Y'"
                           +  " left join project p on p.id = sp.project_id"
                           +  " join sample_organization so on so.sample_id = s.id and so.type_id = (select id from dictionary where system_name = 'org_report_to')"
                           +  " join organization_parameter op on op.organization_id = so.organization_id and op.type_id = (select id from dictionary where system_name = 'released_reportto_email')"
                           +  " join sample_environmental se on se.sample_id = s.id"
                           +  " join analysis a on a.sample_item_id = si.id and a.status_id = (select id from dictionary where system_name = 'analysis_released') and"
                           +                     " a.is_reportable = 'Y' and a.id in (select f.analysis_id from analysis_report_flags f where f.analysis_id = a.id and f.notified_released = 'N')"
                           + " where s.released_date between :start_released_date and :end_released_date and"
                           +       " s.status_id = (select id from dictionary where system_name = 'sample_released')"
                           +" UNION"
                           +" select distinct s.accession_number, s.collection_date,s.collection_time, s.received_date, op.value email,"
                           +       " se.collector ref_field1, se.location ref_field2, CAST(s.client_reference AS varchar(20)) ref_field3,"
                           +       " CAST(p.name AS varchar(20)) ref_field4, a.id a_id "
                           +  " from sample s"
                           +  " join sample_item si on si.sample_id = s.id"
                           +  " left join sample_project sp on sp.sample_id = s.id and sp.is_permanent = 'Y'"
                           +  " left join project p on p.id = sp.project_id"
                           +  " join sample_organization so on so.sample_id = s.id and so.type_id = (select id from dictionary where system_name = 'org_report_to')"
                           +  " join organization_parameter op on op.organization_id = so.organization_id and op.type_id = (select id from dictionary where system_name = 'released_reportto_email')"
                           +  " join sample_environmental se on se.sample_id = s.id"
                           +  " join analysis a on a.sample_item_id = si.id and a.released_date between :start_released_date and :end_released_date and"
                           +                     " a.status_id = (select id from dictionary where system_name = 'analysis_released') and a.is_reportable = 'Y' and"
                           +                     " a.id in (select f.analysis_id from analysis_report_flags f where f.analysis_id =a.id and f.notified_released = 'N')"
                           +  " join test t on t.id = a.test_id and t.reporting_method_id = (select id from dictionary where system_name = 'analyses_released')"
                           + " where s.status_id in (select id from dictionary where system_name not in ('sample_released', 'sample_error'))"
                           +" UNION"
                           +" select distinct s.accession_number, s.collection_date,s.collection_time, s.received_date, op.value email,"
                           +       " ss.collector ref_field1, ss.location ref_field2, CAST(s.client_reference AS varchar(20)) ref_field3,"
                           +       " CAST(p.name AS varchar(20)) ref_field4, a.id a_id"
                           +  " from sample s"
                           +  " join sample_item si on si.sample_id = s.id"
                           +  " left join sample_project sp on sp.sample_id = s.id and sp.is_permanent = 'Y'"
                           +  " left join project p on p.id = sp.project_id"
                           +  " join sample_organization so on so.sample_id = s.id and so.type_id = (select id from dictionary where system_name = 'org_report_to')"
                           +  " join organization_parameter op on op.organization_id = so.organization_id and op.type_id = (select id from dictionary where system_name = 'released_reportto_email')"
                           +  " join sample_sdwis ss on ss.sample_id = s.id"
                           +  " join analysis a on a.sample_item_id = si.id and a.status_id = (select id from dictionary where system_name = 'analysis_released') and"
                           +                     " a.is_reportable = 'Y' and a.id in (select f.analysis_id from analysis_report_flags f where f.analysis_id = a.id and f.notified_released = 'N')"
                           + " where s.released_date between :start_released_date and :end_released_date and"
                           +       " s.status_id = (select id from dictionary where system_name = 'sample_released')"
                           +" UNION"
                           +" select distinct s.accession_number, s.collection_date,s.collection_time, s.received_date, op.value email,"
                           +       " ss.collector ref_field1, ss.location ref_field2, CAST(s.client_reference AS varchar(20)) ref_field3,"
                           +       " CAST(p.name AS varchar(20)) ref_field4, a.id a_id "
                           +  " from sample s"
                           +  " join sample_item si on si.sample_id = s.id"
                           +  " left join sample_project sp on sp.sample_id = s.id and sp.is_permanent = 'Y'"
                           +  " left join project p on p.id = sp.project_id"
                           +  " join sample_organization so on so.sample_id = s.id and so.type_id = (select id from dictionary where system_name = 'org_report_to')"
                           +  " join organization_parameter op on op.organization_id = so.organization_id and op.type_id = (select id from dictionary where system_name = 'released_reportto_email')"
                           +  " join sample_sdwis ss on ss.sample_id = s.id"
                           +  " join analysis a on a.sample_item_id = si.id and a.released_date between :start_released_date and :end_released_date and"
                           +                     " a.status_id = (select id from dictionary where system_name = 'analysis_released') and a.is_reportable = 'Y' and"
                           +                     " a.id in (select f.analysis_id from analysis_report_flags f where f.analysis_id = a.id and f.notified_released = 'N')"
                           +  " join test t on t.id = a.test_id and t.reporting_method_id = (select id from dictionary where system_name = 'analyses_released')"
                           + " where s.status_id in (select id from dictionary where system_name not in ('sample_released', 'sample_error'))"
                           +" UNION"
                           +" select distinct s.accession_number, s.collection_date,s.collection_time, s.received_date, op.value email,"
                           +       " CAST(pv.last_name AS varchar(30)) ref_field1, CAST(pv.first_name AS varchar(20)) ref_field2, CAST(s.client_reference AS varchar(20)) ref_field3,"
                           +       " CAST(p.name AS varchar(20)) ref_field4, a.id a_id"
                           +  " from sample s"
                           +  " join sample_item si on si.sample_id = s.id"
                           +  " left join sample_project sp on sp.sample_id = s.id and sp.is_permanent = 'Y'"
                           +  " left join project p on p.id = sp.project_id"
                           +  " join sample_organization so on so.sample_id = s.id and so.type_id = (select id from dictionary where system_name = 'org_report_to')"
                           +  " join organization_parameter op on op.organization_id = so.organization_id and op.type_id = (select id from dictionary where system_name = 'released_reportto_email')"
                           +  " join sample_clinical sc on sc.sample_id = s.id"
                           +  " left join provider pv on pv.id = sc.provider_id"
                           +  " join analysis a on a.sample_item_id = si.id and a.status_id = (select id from dictionary where system_name = 'analysis_released') and"
                           +                     " a.is_reportable = 'Y' and a.id in (select f.analysis_id from analysis_report_flags f where f.analysis_id = a.id and f.notified_released = 'N')"
                           + " where s.released_date between :start_released_date and :end_released_date and"
                           +       " s.status_id = (select id from dictionary where system_name = 'sample_released')"
                           +" UNION"
                           +" select distinct s.accession_number, s.collection_date,s.collection_time, s.received_date, op.value email,"
                           +       " CAST(pv.last_name AS varchar(30)) ref_field1, CAST(pv.first_name AS varchar(20)) ref_field2, CAST(s.client_reference AS varchar(20)) ref_field3,"
                           +       " CAST(p.name AS varchar(20)) ref_field4, a.id a_id "
                           +  " from sample s"
                           +  " join sample_item si on si.sample_id = s.id"
                           +  " left join sample_project sp on sp.sample_id = s.id and sp.is_permanent = 'Y'"
                           +  " left join project p on p.id = sp.project_id"
                           +  " join sample_organization so on so.sample_id = s.id and so.type_id = (select id from dictionary where system_name = 'org_report_to')"
                           +  " join organization_parameter op on op.organization_id = so.organization_id and op.type_id = (select id from dictionary where system_name = 'released_reportto_email')"
                           +  " join sample_clinical sc on sc.sample_id = s.id"
                           +  " left join provider pv on pv.id = sc.provider_id"
                           +  " join analysis a on a.sample_item_id = si.id and a.released_date between :start_released_date and :end_released_date and"
                           +                     " a.status_id = (select id from dictionary where system_name = 'analysis_released') and a.is_reportable = 'Y' and"
                           +                     " a.id in (select f.analysis_id from analysis_report_flags f where f.analysis_id = a.id and f.notified_released = 'N')"
                           +  " join test t on t.id = a.test_id and t.reporting_method_id = (select id from dictionary where system_name = 'analyses_released')"
                           + " where s.status_id in (select id from dictionary where system_name not in ('sample_released', 'sample_error'))"
                           + " order by email, accession_number",
          resultSetMapping = "Sample.FetchForClientEmailReleasedReportMapping"),
    @NamedNativeQuery(name = "Sample.FetchForSampleStatusReport",
                     query = "select s.accession_number s_anum, s.received_date s_rec, s.collection_date s_col_date, s.collection_time s_col_time,"
                           + " a.status_id a_stat_id, CAST(s.client_reference AS varchar(20)) s_cl_ref, se.collector s_col,"
                           + " t.reporting_description t_rep_desc, m.reporting_description m_rep_desc, s.id s_id, a.id a_id "
                           + "from sample s, sample_environmental se, sample_item si, analysis a, test t, method m "
                           + "where s.id in (:sampleIds) and a.sample_item_id = si.id and a.test_id = t.id and t.method_id = m.id and se.sample_id = s.id and"
                           + " si.sample_id = s.id and a.status_id != (select id from dictionary where system_name = ('analysis_cancelled')) "
                           + "UNION "
                           + "select s.accession_number s_anum, s.received_date s_rec, s.collection_date s_col_date, s.collection_time s_col_time,"
                           + " a.status_id a_stat_id, CAST(s.client_reference AS varchar(20)) s_cl_ref, sw.collector s_col,"
                           + " t.reporting_description t_rep_desc, m.reporting_description m_rep_desc, s.id s_id, a.id a_id "
                           + "from sample s, sample_sdwis sw, sample_item si, analysis a, test t, method m "
                           + "where s.id in (:sampleIds) and a.sample_item_id = si.id and a.test_id = t.id and t.method_id = m.id and sw.sample_id = s.id and"
                           + " si.sample_id = s.id and a.status_id != (select id from dictionary where system_name = ('analysis_cancelled')) "
                           + "UNION "
                           + "select s.accession_number s_anum, s.received_date s_rec, s.collection_date s_col_date, s.collection_time s_col_time,"
                           + " a.status_id a_stat_id, CAST(s.client_reference AS varchar(20)) s_cl_ref, '' s_col,"
                           + " t.reporting_description t_rep_desc, m.reporting_description m_rep_desc, s.id s_id, a.id a_id "
                           + "from sample s, sample_clinical sc, sample_item si, analysis a, test t, method m "
                           + "where s.id in (:sampleIds) and a.sample_item_id = si.id and a.test_id = t.id and t.method_id = m.id and sc.sample_id = s.id and"
                           + " si.sample_id = s.id and a.status_id != (select id from dictionary where system_name = ('analysis_cancelled')) "
                           + "order by s_anum, t_rep_desc, m_rep_desc ",
          resultSetMapping = "Sample.FetchForSampleStatusReport"),
    @NamedNativeQuery(name = "Sample.FetchForTurnaroundWarningReport",
                     query = "select distinct s.accession_number, s.collection_date, s.collection_time, s.received_date, CAST(o.name AS varchar(40)) o_name,"
                           + " CAST(t.name AS varchar(20)) t_name, t.time_ta_warning, t.time_holding, CAST(m.name AS varchar(20)) m_name,"
                           + " se.id se_id, CAST(se.name AS varchar(20)) se_name, d2.entry a_status, a.available_date"
                           + " from sample s, sample_organization so, organization o, sample_item si, analysis a, test t, method m,"
                           + " section se, section_parameter sp, dictionary d1, dictionary d2, dictionary d3"
                           + " where so.sample_id = s.id and so.type_id = d1.id and d1.system_name = 'org_report_to' and so.organization_id = o.id and"
                           + " si.sample_id = s.id and a.sample_item_id = si.id and a.test_id = t.id and t.method_id = m.id and"
                           + " a.status_id = d2.id and d2.system_name not in ('analysis_released', 'analysis_cancelled') and"
                           + " a.section_id = se.id and sp.section_id = se.id and sp.type_id = d3.id and d3.system_name = 'section_ta_warn' and"
                           + " a.available_date is not null"
                           + " order by se.id, s.accession_number",
          resultSetMapping = "Sample.FetchForTurnaroundWarningReport"),
    @NamedNativeQuery(name = "Sample.FetchForTurnaroundMaximumReport",
                     query = "select distinct s.accession_number, s.collection_date, s.collection_time, s.received_date, CAST(o.name AS varchar(40)) o_name,"
                           + " CAST(t.name AS varchar(20)) t_name, t.time_ta_max, t.time_holding, CAST(m.name AS varchar(20)) m_name,"
                           + " se.id se_id, CAST(se.name AS varchar(20)) se_name, d2.entry a_status, a.available_date"
                           + " from sample s, sample_organization so, organization o, sample_item si, analysis a, test t, method m,"
                           + " section se, section_parameter sp, dictionary d1, dictionary d2, dictionary d3"
                           + " where so.sample_id = s.id and so.type_id = d1.id and d1.system_name = 'org_report_to' and so.organization_id = o.id and"
                           + " si.sample_id = s.id and a.sample_item_id = si.id and a.test_id = t.id and t.method_id = m.id and"
                           + " a.status_id = d2.id and d2.system_name not in ('analysis_released', 'analysis_cancelled') and"
                           + " a.section_id = se.id and sp.section_id = se.id and sp.type_id = d3.id and d3.system_name = 'section_ta_max' and"
                           + " a.available_date is not null"
                           + " order by se.id, s.accession_number",
          resultSetMapping = "Sample.FetchForTurnaroundMaximumReport")})
@SqlResultSetMappings({
    @SqlResultSetMapping(name = "Sample.FetchForFinalReportBatchMapping",
                      columns = {@ColumnResult(name = "s_id"),
                                 @ColumnResult(name = "s_accession_number"),
                                 @ColumnResult(name = "s_revision"),
                                 @ColumnResult(name = "s_domain"),
                                 @ColumnResult(name = "o_id"),
                                 @ColumnResult(name = "o_type_id"),
                                 @ColumnResult(name = "o_name"),
                                 @ColumnResult(name = "o_attention"),
                                 @ColumnResult(name = "a_id")}),
    @SqlResultSetMapping(name = "Sample.FetchForFinalReportBatchReprintMapping",
                      columns = {@ColumnResult(name = "s_id"),
                                 @ColumnResult(name = "s_accession_number"),
                                 @ColumnResult(name = "s_revision"),
                                 @ColumnResult(name = "s_domain"),
                                 @ColumnResult(name = "o_id"),
                                 @ColumnResult(name = "o_type_id"),
                                 @ColumnResult(name = "o_name"),
                                 @ColumnResult(name = "o_attention"),
                                 @ColumnResult(name = "a_id")}),
    @SqlResultSetMapping(name = "Sample.FetchForFinalReportSingleMapping",
                      columns = {@ColumnResult(name = "s_id"),
                                 @ColumnResult(name = "s_accession_number"),
                                 @ColumnResult(name = "s_revision"),
                                 @ColumnResult(name = "s_domain"),
                                 @ColumnResult(name = "o_id"),
                                 @ColumnResult(name = "o_type_id"),
                                 @ColumnResult(name = "o_name")}),
    @SqlResultSetMapping(name = "Sample.FetchForFinalReportPreviewMapping",
                      columns = {@ColumnResult(name = "s_id"),
                                 @ColumnResult(name = "s_accession_number"),
                                 @ColumnResult(name = "s_revision"),
                                 @ColumnResult(name = "s_domain"),
                                 @ColumnResult(name = "o_id")}),
    @SqlResultSetMapping(name = "Sample.FetchForClientEmailReceivedReportMapping",
                      columns = {@ColumnResult(name = "accession_number"),
                                 @ColumnResult(name = "collection_date"),
                                 @ColumnResult(name = "collection_time"),
                                 @ColumnResult(name = "received_date"),
                                 @ColumnResult(name = "email"),
                                 @ColumnResult(name = "s_qaevent_id"),
                                 @ColumnResult(name = "a_qaevent_id"),
                                 @ColumnResult(name = "ref_field1"),
                                 @ColumnResult(name = "ref_field2"),
                                 @ColumnResult(name = "ref_field3"),
                                 @ColumnResult(name = "ref_field4")}),
    @SqlResultSetMapping(name = "Sample.FetchForClientEmailReleasedReportMapping",
                      columns = {@ColumnResult(name = "accession_number"),
                                 @ColumnResult(name = "collection_date"),
                                 @ColumnResult(name = "collection_time"),
                                 @ColumnResult(name = "received_date"),
                                 @ColumnResult(name = "email"),
                                 @ColumnResult(name = "ref_field1"),
                                 @ColumnResult(name = "ref_field2"),
                                 @ColumnResult(name = "ref_field3"),
                                 @ColumnResult(name = "ref_field4"),
                                 @ColumnResult(name = "a_id")}),
    @SqlResultSetMapping(name = "Sample.FetchForSampleStatusReport",
                      columns = {@ColumnResult(name = "s_anum"),
                                 @ColumnResult(name = "s_rec"),
                                 @ColumnResult(name = "s_col_date"),
                                 @ColumnResult(name = "s_col_time"),
                                 @ColumnResult(name = "a_stat_id"),
                                 @ColumnResult(name = "s_cl_ref"),
                                 @ColumnResult(name = "s_col"),
                                 @ColumnResult(name = "t_rep_desc"),
                                 @ColumnResult(name = "m_rep_desc"),
                                 @ColumnResult(name = "s_id"),
                                 @ColumnResult(name = "a_id")}),
    @SqlResultSetMapping(name = "Sample.FetchForTurnaroundWarningReport",
                      columns = {@ColumnResult(name = "accession_number"),
                                 @ColumnResult(name = "collection_date"),
                                 @ColumnResult(name = "collection_time"),
                                 @ColumnResult(name = "received_date"),
                                 @ColumnResult(name = "o_name"),
                                 @ColumnResult(name = "t_name"),
                                 @ColumnResult(name = "time_ta_warning"),
                                 @ColumnResult(name = "time_holding"),
                                 @ColumnResult(name = "m_name"),
                                 @ColumnResult(name = "se_id"),
                                 @ColumnResult(name = "se_name"),
                                 @ColumnResult(name = "a_status"),
                                 @ColumnResult(name = "available_date")}),
    @SqlResultSetMapping(name = "Sample.FetchForTurnaroundMaximumReport",
                      columns = {@ColumnResult(name = "accession_number"),
                                 @ColumnResult(name = "collection_date"),
                                 @ColumnResult(name = "collection_time"),
                                 @ColumnResult(name = "received_date"),
                                 @ColumnResult(name = "o_name"),
                                 @ColumnResult(name = "t_name"),
                                 @ColumnResult(name = "time_ta_max"),
                                 @ColumnResult(name = "time_holding"),
                                 @ColumnResult(name = "m_name"),
                                 @ColumnResult(name = "se_id"),
                                 @ColumnResult(name = "se_name"),
                                 @ColumnResult(name = "a_status"),
                                 @ColumnResult(name = "available_date")})})
@Entity
@Table(name = "sample")
@EntityListeners({AuditUtil.class})
public class Sample implements Auditable, Cloneable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer                         id;

    @Column(name = "next_item_sequence")
    private Integer                         nextItemSequence;

    @Column(name = "domain")
    private String                          domain;

    @Column(name = "accession_number")
    private Integer                         accessionNumber;

    @Column(name = "revision")
    private Integer                         revision;

    @Column(name = "order_id")
    private Integer                         orderId;

    @Column(name = "entered_date")
    private Date                            enteredDate;

    @Column(name = "received_date")
    private Date                            receivedDate;

    @Column(name = "received_by_id")
    private Integer                         receivedById;

    @Column(name = "collection_date")
    private Date                            collectionDate;

    @Column(name = "collection_time")
    private Date                            collectionTime;

    @Column(name = "status_id")
    private Integer                         statusId;

    @Column(name = "package_id")
    private Integer                         packageId;

    @Column(name = "client_reference")
    private String                          clientReference;

    @Column(name = "released_date")
    private Date                            releasedDate;

    // sample environmental
    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "sample_id", insertable = false, updatable = false)
    private Collection<SampleEnvironmental> sampleEnvironmental;

    // sample sdwis
    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "sample_id", insertable = false, updatable = false)
    private Collection<SampleSDWIS>         sampleSDWIS;

    // sample neonatal
    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "sample_id", insertable = false, updatable = false)
    private Collection<SampleNeonatal>      sampleNeonatal;

    // sample clinical
    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "sample_id", insertable = false, updatable = false)
    private Collection<SampleClinical>      sampleClinical;

    // sample pt
    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "sample_id", insertable = false, updatable = false)
    private Collection<SamplePT>            samplePT;
    
    // sample pt
    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "sample_id", insertable = false, updatable = false)
    private Collection<SampleAnimal>            sampleAnimal;

    // eorder
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", insertable = false, updatable = false)
    private EOrder                          eorder;

    // sample organizations
    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "sample_id", insertable = false, updatable = false)
    private Collection<SampleOrganization>  sampleOrganization;

    // sample projects
    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "sample_id", insertable = false, updatable = false)
    private Collection<SampleProject>       sampleProject;

    // projects
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "sample_project",
               joinColumns = {@JoinColumn(name = "sample_id", insertable = false, updatable = false)},
               inverseJoinColumns = {@JoinColumn(name = "project_id",
                                                 insertable = false,
                                                 updatable = false)})
    private Collection<Project>             project;

    // sample items
    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "sample_id", insertable = false, updatable = false)
    private Collection<SampleItem>          sampleItem;

    // sample qa events
    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "sample_id", insertable = false, updatable = false)
    private Collection<SampleQaevent>       sampleQAEvent;

    // aux data records
    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "reference_id", insertable = false, updatable = false)
    private Collection<AuxData>             auxData;

    // attachment item records
    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "reference_id", insertable = false, updatable = false)
    private Collection<AttachmentItem>      attachmentItem;

    // history records
    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "reference_id", insertable = false, updatable = false)
    private Collection<History>             history;

    @Transient
    private Sample                          original;

    public Integer getId() {
        return id;
    }

    protected void setId(Integer id) {
        if (DataBaseUtil.isDifferent(id, this.id))
            this.id = id;
    }

    public Integer getNextItemSequence() {
        return nextItemSequence;
    }

    public void setNextItemSequence(Integer nextItemSequence) {
        if (DataBaseUtil.isDifferent(nextItemSequence, this.nextItemSequence))
            this.nextItemSequence = nextItemSequence;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        if (DataBaseUtil.isDifferent(domain, this.domain))
            this.domain = domain;
    }

    public Integer getAccessionNumber() {
        return accessionNumber;
    }

    public void setAccessionNumber(Integer accessionNumber) {
        if (DataBaseUtil.isDifferent(accessionNumber, this.accessionNumber))
            this.accessionNumber = accessionNumber;
    }

    public Integer getRevision() {
        return revision;
    }

    public void setRevision(Integer revision) {
        if (DataBaseUtil.isDifferent(revision, this.revision))
            this.revision = revision;
    }

    public Integer getOrderId() {
        return orderId;
    }

    public void setOrderId(Integer orderId) {
        if (DataBaseUtil.isDifferent(orderId, this.orderId))
            this.orderId = orderId;
    }

    public Datetime getEnteredDate() {
        return DataBaseUtil.toYM(enteredDate);
    }

    public void setEnteredDate(Datetime enteredDate) {
        if (DataBaseUtil.isDifferentYM(enteredDate, this.enteredDate))
            this.enteredDate = DataBaseUtil.toDate(enteredDate);
    }

    public Datetime getReceivedDate() {
        return DataBaseUtil.toYD(receivedDate);
    }

    public void setReceivedDate(Datetime receivedDate) {
        if (DataBaseUtil.isDifferentYM(receivedDate, this.receivedDate))
            this.receivedDate = DataBaseUtil.toDate(receivedDate);
    }

    public Integer getReceivedById() {
        return receivedById;
    }

    public void setReceivedById(Integer receivedById) {
        if (DataBaseUtil.isDifferent(receivedById, this.receivedById))
            this.receivedById = receivedById;
    }

    public Datetime getCollectionDate() {
        return DataBaseUtil.toYD(collectionDate);
    }

    public void setCollectionDate(Datetime collectionDate) {
        if (DataBaseUtil.isDifferentYD(collectionDate, this.collectionDate))
            this.collectionDate = DataBaseUtil.toDate(collectionDate);
    }

    public Datetime getCollectionTime() {
        return DataBaseUtil.toHM(collectionTime);
    }

    public void setCollectionTime(Datetime collectionTime) {
        if (DataBaseUtil.isDifferentHM(collectionTime, this.collectionTime))
            this.collectionTime = DataBaseUtil.toDate(collectionTime);
    }

    public Integer getStatusId() {
        return statusId;
    }

    public void setStatusId(Integer statusId) {
        if (DataBaseUtil.isDifferent(statusId, this.statusId))
            this.statusId = statusId;
    }

    public Integer getPackageId() {
        return packageId;
    }

    public void setPackageId(Integer packageId) {
        if (DataBaseUtil.isDifferent(packageId, this.packageId))
            this.packageId = packageId;
    }

    public String getClientReference() {
        return clientReference;
    }

    public void setClientReference(String clientReference) {
        if (DataBaseUtil.isDifferent(clientReference, this.clientReference))
            this.clientReference = clientReference;
    }

    public Datetime getReleasedDate() {
        return DataBaseUtil.toYM(releasedDate);
    }

    public void setReleasedDate(Datetime releasedDate) {
        if (DataBaseUtil.isDifferentYM(releasedDate, this.releasedDate))
            this.releasedDate = DataBaseUtil.toDate(releasedDate);
    }

    public Collection<SampleItem> getSampleItem() {
        return sampleItem;
    }

    public void setSampleItem(Collection<SampleItem> sampleItem) {
        this.sampleItem = sampleItem;
    }

    public Collection<SampleEnvironmental> getSampleEnvironmental() {
        return sampleEnvironmental;
    }

    public void setSampleEnvironmental(Collection<SampleEnvironmental> sampleEnvironmental) {
        this.sampleEnvironmental = sampleEnvironmental;
    }

    public Collection<SampleOrganization> getSampleOrganization() {
        return sampleOrganization;
    }

    public void setSampleOrganization(Collection<SampleOrganization> sampleOrganization) {
        this.sampleOrganization = sampleOrganization;
    }

    public Collection<SampleProject> getSampleProject() {
        return sampleProject;
    }

    public void setSampleProject(Collection<SampleProject> sampleProject) {
        this.sampleProject = sampleProject;
    }

    public Collection<SampleQaevent> getSampleQAEvent() {
        return sampleQAEvent;
    }

    public void setSampleQAEvent(Collection<SampleQaevent> sampleQAEvent) {
        this.sampleQAEvent = sampleQAEvent;
    }

    public Collection<Project> getProject() {
        return project;
    }

    public void setProject(Collection<Project> project) {
        this.project = project;
    }


    public Collection<SampleSDWIS> getSampleSDWIS() {
        return sampleSDWIS;
    }

    public void setSampleSDWIS(Collection<SampleSDWIS> sampleSDWIS) {
        this.sampleSDWIS = sampleSDWIS;
    }

    public Collection<SampleNeonatal> getSampleNeonatal() {
        return sampleNeonatal;
    }

    public void setSampleNeonatal(Collection<SampleNeonatal> sampleNeonatal) {
        this.sampleNeonatal = sampleNeonatal;
    }

    public Collection<SampleClinical> getSampleClinical() {
        return sampleClinical;
    }

    public void setSampleClinical(Collection<SampleClinical> sampleClinical) {
        this.sampleClinical = sampleClinical;
    }

    public Collection<SamplePT> getSamplePT() {
        return samplePT;
    }

    public void setSamplePT(Collection<SamplePT> samplePT) {
        this.samplePT = samplePT;
    }

    public Collection<SampleAnimal> getSampleAnimal() {
        return sampleAnimal;
    }

    public void setSampleAnimal(Collection<SampleAnimal> sampleAnimal) {
        this.sampleAnimal = sampleAnimal;
    }

    public Collection<AuxData> getAuxData() {
        return auxData;
    }

    public void setAuxData(Collection<AuxData> auxData) {
        this.auxData = auxData;
    }

    public Collection<AttachmentItem> getAttachmentItem() {
        return attachmentItem;
    }

    public void setAttachmentItem(Collection<AttachmentItem> attachmentItem) {
        this.attachmentItem = attachmentItem;
    }

    public EOrder getEOrder() {
        return eorder;
    }

    public void setEOrder(EOrder eorder) {
        this.eorder = eorder;
    }

    public Collection<History> getHistory() {
        return history;
    }

    public void setHistory(Collection<History> history) {
        this.history = history;
    }

    public void setClone() {
        try {
            original = (Sample)this.clone();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Audit getAudit(Integer activity) {
        Audit audit;

        audit = new Audit(activity);
        audit.setReferenceTableId(Constants.table().SAMPLE);
        audit.setReferenceId(getId());
        if (original != null)
            audit.setField("id", id, original.id)
                 .setField("next_item_sequence", nextItemSequence, original.nextItemSequence)
                 .setField("domain", domain, original.domain)
                 .setField("accession_number", accessionNumber, original.accessionNumber)
                 .setField("revision", revision, original.revision)
                 .setField("order_id", orderId, original.orderId, Constants.table().IORDER)
                 .setField("entered_date", enteredDate, original.enteredDate)
                 .setField("received_date", receivedDate, original.receivedDate)
                 .setField("received_by_id", receivedById, original.receivedById)
                 .setField("collection_date", collectionDate, original.collectionDate)
                 .setField("collection_time", collectionTime, original.collectionTime)
                 .setField("status_id", statusId, original.statusId, Constants.table().DICTIONARY)
                 .setField("package_id", packageId, original.packageId)
                 .setField("client_reference", clientReference, original.clientReference)
                 .setField("released_date", releasedDate, original.releasedDate);

        return audit;
    }
}