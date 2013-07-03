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
package org.openelis.bean;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.FlushModeType;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.jboss.security.annotation.SecurityDomain;
import org.openelis.constants.Messages;
import org.openelis.domain.ClientNotificationVO;
import org.openelis.domain.Constants;
import org.openelis.domain.FinalReportVO;
import org.openelis.domain.IdAccessionVO;
import org.openelis.domain.SampleDO;
import org.openelis.domain.SampleStatusWebReportVO;
import org.openelis.entity.Sample;
import org.openelis.meta.SampleMeta;
import org.openelis.meta.SampleWebMeta;
import org.openelis.ui.common.DataBaseUtil;
import org.openelis.ui.common.DatabaseException;
import org.openelis.ui.common.Datetime;
import org.openelis.ui.common.FormErrorException;
import org.openelis.ui.common.FormErrorWarning;
import org.openelis.ui.common.LastPageException;
import org.openelis.ui.common.NotFoundException;
import org.openelis.ui.common.ValidationErrorsList;
import org.openelis.ui.common.data.QueryData;
import org.openelis.util.QueryBuilderV2;
import org.openelis.gwt.widget.QueryFieldUtil;

@Stateless
@SecurityDomain("openelis")
public class SampleBean {

    @PersistenceContext(unitName = "openelis")
    private EntityManager           manager;

    private static final SampleMeta meta = new SampleMeta();

    private static HashMap<String, String> wellOrgFieldMap, reportToAddressFieldMap;

    static {
        wellOrgFieldMap = new HashMap<String, String>();
        wellOrgFieldMap.put(SampleMeta.getWellOrganizationName(),
                            SampleMeta.getWellOrganizationName());
        wellOrgFieldMap.put(SampleMeta.getWellReportToAddressMultipleUnit(),
                            SampleMeta.getWellReportToAddressMultipleUnit());
        wellOrgFieldMap.put(SampleMeta.getWellReportToAddressStreetAddress(),
                            SampleMeta.getWellReportToAddressStreetAddress());
        wellOrgFieldMap.put(SampleMeta.getWellReportToAddressCity(),
                            SampleMeta.getWellReportToAddressCity());
        wellOrgFieldMap.put(SampleMeta.getWellReportToAddressState(),
                            SampleMeta.getWellReportToAddressState());
        wellOrgFieldMap.put(SampleMeta.getWellReportToAddressZipCode(),
                            SampleMeta.getWellReportToAddressZipCode());
        wellOrgFieldMap.put(SampleMeta.getWellReportToAddressWorkPhone(),
                            SampleMeta.getWellReportToAddressWorkPhone());
        wellOrgFieldMap.put(SampleMeta.getWellReportToAddressFaxPhone(),
                            SampleMeta.getWellReportToAddressFaxPhone());

        reportToAddressFieldMap = new HashMap<String, String>();
        reportToAddressFieldMap.put(SampleMeta.getWellReportToName(),
                                    SampleMeta.getWellReportToName());
        reportToAddressFieldMap.put(SampleMeta.getAddressMultipleUnit(),
                                    SampleMeta.getAddressMultipleUnit());
        reportToAddressFieldMap.put(SampleMeta.getAddressStreetAddress(),
                                    SampleMeta.getAddressStreetAddress());
        reportToAddressFieldMap.put(SampleMeta.getAddressCity(),
                                    SampleMeta.getAddressCity());
        reportToAddressFieldMap.put(SampleMeta.getAddressState(),
                                    SampleMeta.getAddressState());
        reportToAddressFieldMap.put(SampleMeta.getAddressZipCode(),
                                    SampleMeta.getAddressZipCode());
        reportToAddressFieldMap.put(SampleMeta.getAddressWorkPhone(),
                                    SampleMeta.getAddressWorkPhone());
        reportToAddressFieldMap.put(SampleMeta.getAddressFaxPhone(),
                                    SampleMeta.getAddressFaxPhone());
    }

    public ArrayList<IdAccessionVO> query(ArrayList<QueryData> fields, int first, int max) throws Exception {
        String queryString, whereForFrom, sampleWhere, privateWellWhere;
        Query query;
        QueryBuilderV2 builder;
        List list;
        ArrayList<QueryData> wellFields;

        builder = new QueryBuilderV2();
        builder.setMeta(meta);
        builder.setSelect("distinct new org.openelis.domain.IdAccessionVO(" +
                          SampleMeta.getId() + "," + SampleMeta.getAccessionNumber() +
                          ") ");
        builder.constructWhere(fields);
        builder.setOrderBy(SampleMeta.getAccessionNumber());

        whereForFrom = builder.getWhereClause();

        // for the well screen we have to link to the org table and the address
        // table
        // with the same textboxes. So if they queried by these fields we need
        // to add
        // a link to the 2nd table
        wellFields = new ArrayList<QueryData>();
        privateWellWhere = createWhereFromWellFields(fields, wellFields);
        builder.clearWhereClause();
        builder.constructWhere(fields);
        sampleWhere = builder.getWhereClause();

        queryString = builder.getSelectClause() + builder.getFromClause(whereForFrom) +
                      sampleWhere + privateWellWhere + builder.getOrderBy();

        query = manager.createQuery(queryString);
        if (max > 0)
            query.setMaxResults(first + max);

        // add the well fields we created
        fields.addAll(wellFields);
        builder.setQueryParams(query, fields);

        try {
            list = query.getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }

        if (list.isEmpty())
            throw new NotFoundException();
        
        if (max > 0)
            list = (ArrayList<IdAccessionVO>)DataBaseUtil.subList(list, first, max);
        
        if (list == null)
            throw new LastPageException();

        return (ArrayList<IdAccessionVO>)list;
    }

    public ArrayList<IdAccessionVO> dataExchangeQuery(ArrayList<QueryData> fields) throws Exception {
        boolean orgPresent;
        Query query;
        List results;
        QueryBuilderV2 builder;

        builder = new QueryBuilderV2();
        builder.setMeta(meta);
        builder.setSelect("distinct new org.openelis.domain.IdAccessionVO(" +
                          SampleMeta.getId() + "," + SampleMeta.getAccessionNumber() +
                          ") ");
        builder.constructWhere(fields);

        builder.addWhere(SampleWebMeta.getAnalysisStatusId() + "=" +
                         Constants.dictionary().ANALYSIS_RELEASED);
        builder.addWhere(SampleWebMeta.getStatusId() + "!=" +
                         Constants.dictionary().SAMPLE_ERROR);

        orgPresent = false;
        for (QueryData f : fields) {
            if (SampleMeta.getOrgId().equals(f.getKey())) {
                orgPresent = true;
                continue;
            }
        }

        if (orgPresent)
            builder.addWhere(SampleMeta.getSampleOrgTypeId() + "=" +
                             Constants.dictionary().ORG_REPORT_TO);

        builder.setOrderBy(SampleMeta.getAccessionNumber());
        query = manager.createQuery(builder.getEJBQL());

        builder.setQueryParams(query, fields);

        results = query.getResultList();

        if (results.isEmpty())
            throw new NotFoundException();

        return DataBaseUtil.toArrayList(results);
    }

    public SampleDO fetchById(Integer sampleId) throws Exception {
        Query query = manager.createNamedQuery("Sample.FetchById");
        query.setParameter("id", sampleId);
        try {
            return (SampleDO)query.getSingleResult();
        } catch (NoResultException e) {
            throw new NotFoundException();
        } catch (Exception e) {
            throw new DatabaseException(e);
        }
    }

    public ArrayList<SampleDO> fetchByIds(ArrayList<Integer> ids) {
        Query query;

        query = manager.createNamedQuery("Sample.FetchByIds");
        query.setParameter("ids", ids);

        return DataBaseUtil.toArrayList(query.getResultList());
    }

    public SampleDO fetchByAccessionNumber(Integer accessionNumber) throws Exception {
        Query query;

        query = manager.createNamedQuery("Sample.FetchByAccessionNumber");
        query.setParameter("accession", accessionNumber);
        try {
            return (SampleDO)query.getSingleResult();
        } catch (NoResultException e) {
            throw new NotFoundException(Messages.get().noRecordsFound());
        } catch (Exception e) {
            throw new DatabaseException(e);
        }
    }

    public ArrayList<SampleDO> fetchSDWISByReleased(Date startDate, Date endDate) throws Exception {
        List<SampleDO> list;
        Query query;

        query = manager.createNamedQuery("Sample.FetchSDWISByReleased");
        query.setParameter("startDate", startDate);
        query.setParameter("endDate", endDate);

        list = query.getResultList();
        if (list.isEmpty())
            throw new NotFoundException();

        return DataBaseUtil.toArrayList(list);
    }

    public ArrayList<FinalReportVO> fetchForFinalReportBatch() throws Exception {
        Query query;
        List<Object[]> list;
        ArrayList<FinalReportVO> returnList;

        query = manager.createNamedQuery("Sample.FetchForFinalReportBatch");
        list = query.getResultList();

        returnList = new ArrayList<FinalReportVO>();
        for (Object[] result : list)
            returnList.add(new FinalReportVO((Integer)result[0],
                                             (Integer)result[1],
                                             (Integer)result[2],
                                             (String)result[3],
                                             (Integer)result[4],
                                             (Integer)result[5],
                                             (String)result[6],
                                             (String)result[7],
                                             (Integer)result[8]));

        return returnList;
    }

    public ArrayList<FinalReportVO> fetchForFinalReportBatchReprint(Date beginPrinted,
                                                                    Date endPrinted) throws Exception {
        Query query;
        List<Object[]> list;
        ArrayList<FinalReportVO> returnList;

        query = manager.createNamedQuery("Sample.FetchForFinalReportBatchReprint");
        query.setParameter("beginPrinted", beginPrinted);
        query.setParameter("endPrinted", endPrinted);
        list = query.getResultList();

        returnList = new ArrayList<FinalReportVO>();
        for (Object[] result : list)
            returnList.add(new FinalReportVO((Integer)result[0],
                                             (Integer)result[1],
                                             (Integer)result[2],
                                             (String)result[3],
                                             (Integer)result[4],
                                             (Integer)result[5],
                                             (String)result[6],
                                             (String)result[7],
                                             (Integer)result[8]));

        return returnList;
    }

    public ArrayList<FinalReportVO> fetchForFinalReportSingle(Integer accessionNumber) throws Exception {
        Query query;
        List<Object[]> list;
        ArrayList<FinalReportVO> returnList;

        query = manager.createNamedQuery("Sample.FetchForFinalReportSingle");
        query.setParameter("accessionNumber", accessionNumber);
        list = query.getResultList();

        returnList = new ArrayList<FinalReportVO>();
        for (Object[] result : list)
            returnList.add(new FinalReportVO((Integer)result[0],
                                             (Integer)result[1],
                                             (Integer)result[2],
                                             (String)result[3],
                                             (Integer)result[4],
                                             (Integer)result[5],
                                             (String)result[6],
                                             null,
                                             null));

        return returnList;
    }

    public FinalReportVO fetchForFinalReportPreview(Integer accessionNumber) throws Exception {
        Query query;
        Object result[];

        query = manager.createNamedQuery("Sample.FetchForFinalReportPreview");
        query.setParameter("accessionNumber", accessionNumber);
        try {
            result = (Object[])query.getSingleResult();
            return new FinalReportVO((Integer)result[0],
                                     (Integer)result[1],
                                     (Integer)result[2],
                                     (String)result[3],
                                     (Integer)result[4],
                                     null,
                                     null,
                                     null,
                                     null);
        } catch (NoResultException e) {
            throw new NotFoundException("noRecordsFound");
        }
    }

    public ArrayList<ClientNotificationVO> fetchForClientEmailReceivedReport(Date stDate,
                                                                             Date endDate) throws Exception {
        Query query;
        ClientNotificationVO notificationVo;
        ArrayList<Object[]> resultList;
        ArrayList<ClientNotificationVO> returnList;

        query = manager.createNamedQuery("Sample.FetchForClientEmailReceivedReport");
        query.setParameter("start_entered_date", stDate);
        query.setParameter("end_entered_date", endDate);

        resultList = DataBaseUtil.toArrayList(query.getResultList());
        returnList = new ArrayList<ClientNotificationVO>();
        for (Object[] result : resultList) {
            notificationVo = new ClientNotificationVO((Integer)result[0],
                                                      (Date)result[1],
                                                      (Date)result[2],
                                                      (Date)result[3],
                                                      (String)result[4],
                                                      (Integer)result[5],
                                                      (Integer)result[6],
                                                      (String)result[7],
                                                      (String)result[8],
                                                      (String)result[9],
                                                      (String)result[10]);
            returnList.add(notificationVo);
        }
        return returnList;
    }

    public ArrayList<ClientNotificationVO> fetchForClientEmailReleasedReport(Date stDate,
                                                                             Date endDate) throws Exception {
        Query query;
        ClientNotificationVO notificationVo;
        ArrayList<Object[]> resultList;
        ArrayList<ClientNotificationVO> returnList;

        query = manager.createNamedQuery("Sample.FetchForClientEmailReleasedReport");
        query.setParameter("start_released_date", stDate);
        query.setParameter("end_released_date", endDate);

        resultList = DataBaseUtil.toArrayList(query.getResultList());
        returnList = new ArrayList<ClientNotificationVO>();
        for (Object[] result : resultList) {
            notificationVo = new ClientNotificationVO((Integer)result[0],
                                                      (Date)result[1],
                                                      (Date)result[2],
                                                      (Date)result[3],
                                                      (String)result[4],
                                                      null,
                                                      null,
                                                      (String)result[5],
                                                      (String)result[6],
                                                      (String)result[7],
                                                      (String)result[8]);
            returnList.add(notificationVo);
        }
        return returnList;
    }

    public ArrayList<Object[]> fetchForBillingReport(Date stDate, Date endDate) throws Exception {
        Query query;

        query = manager.createNamedQuery("Sample.FetchForBillingReport");
        query.setParameter("startDate", stDate);
        query.setParameter("endDate", endDate);

        return DataBaseUtil.toArrayList(query.getResultList());
    }

    public ArrayList<SampleStatusWebReportVO> fetchForSampleStatusReport(ArrayList<Integer> sampleIds) throws Exception {
        Query query;
        SampleStatusWebReportVO vo;
        ArrayList<Object[]> resultList;
        ArrayList<SampleStatusWebReportVO> returnList;

        query = manager.createNamedQuery("Sample.FetchForSampleStatusReport");
        query.setParameter("sampleIds", sampleIds);
        resultList = DataBaseUtil.toArrayList(query.getResultList());

        returnList = new ArrayList<SampleStatusWebReportVO>();
        for (Object[] result : resultList) {
            vo = new SampleStatusWebReportVO((Integer)result[0],
                                             (Date)result[1],
                                             (Date)result[2],
                                             (Date)result[3],
                                             (Integer)result[4],
                                             (String)result[5],
                                             (String)result[6],
                                             (String)result[7],
                                             (String)result[8],
                                             (Integer)result[9],
                                             (Integer)result[10]);
            returnList.add(vo);
        }
        return returnList;
    }

    public ArrayList<Object[]> fetchForTurnaroundMaximumReport() throws Exception {
        Query query;

        query = manager.createNamedQuery("Sample.FetchForTurnaroundMaximumReport");

        return DataBaseUtil.toArrayList(query.getResultList());
    }

    public ArrayList<Object[]> fetchForTurnaroundWarningReport() throws Exception {
        Query query;

        query = manager.createNamedQuery("Sample.FetchForTurnaroundWarningReport");

        return DataBaseUtil.toArrayList(query.getResultList());
    }

    public ArrayList<IdAccessionVO> fetchSamplesByLastRunDate(ArrayList<QueryData> fields,
                                                              Date lastRunDate) throws Exception {
        boolean orgPresent;
        String lrdStr, crdStr, queryStr;
        ArrayList<String> dateClause;
        SimpleDateFormat dateFormat;
        List results;
        Query query;
        QueryData field;
        QueryBuilderV2 builder;
        Calendar cal;
        Date currentRunDate;

        builder = new QueryBuilderV2();
        builder.setMeta(meta);
        builder.setSelect("distinct new org.openelis.domain.IdAccessionVO(" +
                          SampleMeta.getId() + "," + SampleMeta.getAccessionNumber() +
                          ") ");

        builder.constructWhere(fields);
        builder.addWhere(SampleWebMeta.getStatusId() + "!=" +
                         Constants.dictionary().SAMPLE_ERROR);

        orgPresent = false;
        for (QueryData f : fields) {
            if (SampleMeta.getOrgId().equals(f.getKey())) {
                orgPresent = true;
                continue;
            }
        }

        if (orgPresent)
            builder.addWhere(SampleMeta.getSampleOrgTypeId() + "=" +
                             Constants.dictionary().ORG_REPORT_TO);

        dateClause = new ArrayList<String>();

        /*
         * create the clause for restricting the list of accession numbers to
         * the samples that were released between the last run date and the
         * current time minus one minute or that have at least one analysis that
         * was released during that time
         */
        dateClause.add("(");
        dateClause.add(SampleMeta.getReleasedDate());
        dateClause.add("between");
        dateClause.add(":dt00");
        dateClause.add("and");
        dateClause.add(":dt01");
        dateClause.add(")");
        dateClause.add("or");
        dateClause.add("(");
        dateClause.add(SampleMeta.getAnalysisReleasedDate());
        dateClause.add("between");
        dateClause.add(":dt100");
        dateClause.add("and");
        dateClause.add(":dt101");
        dateClause.add(")");

        builder.addWhere(DataBaseUtil.concatWithSeparator(dateClause, " "));

        dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");

        lrdStr = dateFormat.format(lastRunDate);

        cal = Calendar.getInstance();
        cal.add(Calendar.MINUTE, -1);
        currentRunDate = cal.getTime();

        crdStr = dateFormat.format(currentRunDate);

        queryStr = DataBaseUtil.concatWithSeparator(lrdStr, "..", crdStr);

        /*
         * set the values of the parameters created in the clause above
         */
        field = new QueryData();
        field.setKey("dt");
        field.setQuery(queryStr);
        field.setType(QueryData.Type.DATE);
        fields.add(field);

        field = new QueryData();
        field.setKey("dt1");
        field.setQuery(queryStr);
        field.setType(QueryData.Type.DATE);

        /*
         * these fields were not added to the list passed to constructWhere()
         * because they aren't part of the where clause but only added to
         * specify the values of the parameters
         */
        fields.add(field);

        builder.setOrderBy(SampleMeta.getAccessionNumber());
        query = manager.createQuery(builder.getEJBQL());

        builder.setQueryParams(query, fields);

        results = query.getResultList();

        if (results.isEmpty())
            throw new NotFoundException();

        return DataBaseUtil.toArrayList(results);
    }

    public SampleDO add(SampleDO data) throws Exception {
        Sample entity;

        manager.setFlushMode(FlushModeType.COMMIT);

        entity = new Sample();
        entity.setNextItemSequence(data.getNextItemSequence());
        entity.setDomain(data.getDomain());
        entity.setAccessionNumber(data.getAccessionNumber());
        entity.setRevision(data.getRevision());
        entity.setOrderId(data.getOrderId());
        entity.setEnteredDate(data.getEnteredDate());
        entity.setReceivedDate(data.getReceivedDate());
        entity.setReceivedById(data.getReceivedById());
        entity.setCollectionDate(data.getCollectionDate());
        entity.setCollectionTime(data.getCollectionTime());
        entity.setStatusId(data.getStatusId());
        entity.setPackageId(data.getPackageId());
        entity.setClientReference(data.getClientReference());
        entity.setReleasedDate(data.getReleasedDate());

        manager.persist(entity);
        data.setId(entity.getId());

        return data;
    }

    public SampleDO update(SampleDO data) throws Exception {
        Sample entity;

        if ( !data.isChanged())
            return data;

        manager.setFlushMode(FlushModeType.COMMIT);

        entity = manager.find(Sample.class, data.getId());
        entity.setNextItemSequence(data.getNextItemSequence());
        entity.setDomain(data.getDomain());
        entity.setAccessionNumber(data.getAccessionNumber());
        entity.setRevision(data.getRevision());
        entity.setOrderId(data.getOrderId());
        entity.setEnteredDate(data.getEnteredDate());
        entity.setReceivedDate(data.getReceivedDate());
        entity.setReceivedById(data.getReceivedById());
        entity.setCollectionDate(data.getCollectionDate());
        entity.setCollectionTime(data.getCollectionTime());
        entity.setStatusId(data.getStatusId());
        entity.setPackageId(data.getPackageId());
        entity.setClientReference(data.getClientReference());
        entity.setReleasedDate(data.getReleasedDate());

        return data;
    }

    public void validate(SampleDO data, Integer maxAccession, boolean ignoreWarning) throws Exception {
        String d;
        Calendar cal;
        ValidationErrorsList e;
        Datetime minEnt, rec, ent, col;

        e = new ValidationErrorsList();

        // accession number must be > 0, previously issued. we don't
        // want to check the duplicate again since it will not guarantee
        // that by the time we insert it will still be unique, and will
        // slow us down.
        if (data.getAccessionNumber() == null || data.getAccessionNumber() <= 0)
            e.add(new FormErrorException(Messages.get().sample_accessionNumberNotValidException(data.getAccessionNumber())));
        else if (maxAccession.compareTo(data.getAccessionNumber()) < 0)
            e.add(new FormErrorException(Messages.get().sample_accessionNumberNotInUse(data.getAccessionNumber())));

        // domain
        d = data.getDomain();
        if (d == null ||
            ( !Constants.domain().ANIMAL.equals(d) &&
             !Constants.domain().ENVIRONMENTAL.equals(d) &&
             !Constants.domain().HUMAN.equals(d) &&
             !Constants.domain().NEONATAL.equals(d) &&
             !Constants.domain().PRIVATEWELL.equals(d) &&
             !Constants.domain().PT.equals(d) && !Constants.domain().QUICKENTRY.equals(d) && !Constants.domain().SDWIS.equals(d)))
            e.add(new FormErrorException(Messages.get().sample_noDomainException(DataBaseUtil.asString(data.getAccessionNumber()))));
        // dates
        ent = data.getEnteredDate();
        rec = data.getReceivedDate();
        minEnt = null;
        if (ent == null)
            e.add(new FormErrorException(Messages.get().sample_enteredDateRequiredException(
                                         DataBaseUtil.asString(data.getAccessionNumber()))));
        else
            minEnt = ent.add( -180);
        if (rec == null)
            e.add(new FormErrorException(Messages.get().sample_receivedDateRequiredException(
                                         DataBaseUtil.asString(data.getAccessionNumber()))));
        else if (rec.before(minEnt) && !ignoreWarning)
            e.add(new FormErrorWarning(Messages.get().sample_receivedTooOldWarning(DataBaseUtil.asString(data.getAccessionNumber()))));
        col = data.getCollectionDate();
        if (data.getCollectionTime() != null) {
            cal = Calendar.getInstance();
            cal.setTime(col.getDate());
            cal.add(Calendar.HOUR_OF_DAY, data.getCollectionTime().get(Datetime.HOUR));
            cal.add(Calendar.MINUTE, data.getCollectionTime().get(Datetime.MINUTE));
            col = new Datetime(Datetime.YEAR, Datetime.MINUTE, cal.getTime());
        }
        if (col != null) {
            if (col.after(rec))
                e.add(new FormErrorException(Messages.get().sample_collectedDateInvalidError(
                                             DataBaseUtil.asString(data.getAccessionNumber()))));
            if (col.before(minEnt) && !ignoreWarning)
                e.add(new FormErrorException(Messages.get().sample_collectedTooOldWarning(
                                             DataBaseUtil.asString(data.getAccessionNumber()))));
        }

        if (e.size() > 0)
            throw e;
    }

    private String createWhereFromWellFields(ArrayList<QueryData> fields,
                                             ArrayList<QueryData> wellFields) {
        int i;
        String whereClause;
        QueryFieldUtil qField;
        QueryData field;

        whereClause = "";
        //
        // we extract the fields that belong only to sample private well
        // and its related organization/report to; we need to remove these
        // fields from the list sent to the bean because we don't want them to
        // be included in the where clause created from those fields, because we
        // would get repetitive and thus erroneous clauses
        //
        for (i = 0; i < fields.size(); i++ ) {
            field = fields.get(i);
            qField = new QueryFieldUtil();
            qField.parse(field.getQuery());

            if (wellOrgFieldMap.get(field.getKey()) != null) {
                wellFields.add(fields.remove(i));
                i-- ;
            } else if (reportToAddressFieldMap.get(field.getKey()) != null) {
                wellFields.add(fields.remove(i));
                i-- ;
            }
        }

        //
        // we create a where clause from the fields obtained from the previous
        // code
        //
        for (i = 0; i < wellFields.size(); i++ ) {
            field = wellFields.get(i);
            qField = new QueryFieldUtil();
            qField.parse(field.getQuery());

            if (i % 2 == 0) {
                whereClause += " and ( " +
                               QueryBuilderV2.getQueryNoOperand(qField, field.getKey());
            } else {
                whereClause += " or " +
                               QueryBuilderV2.getQueryNoOperand(qField, field.getKey()) +
                               " ) ";
            }
        }
        return whereClause;
    }

}
