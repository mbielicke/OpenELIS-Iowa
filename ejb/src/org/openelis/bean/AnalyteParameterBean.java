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

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.FlushModeType;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.jboss.security.annotation.SecurityDomain;
import org.openelis.constants.Messages;
import org.openelis.domain.AnalyteParameterDO;
import org.openelis.domain.AnalyteParameterViewDO;
import org.openelis.domain.Constants;
import org.openelis.domain.QcAnalyteViewDO;
import org.openelis.domain.ReferenceIdTableIdNameVO;
import org.openelis.domain.TestAnalyteViewDO;
import org.openelis.entity.AnalyteParameter;
import org.openelis.meta.AnalyteParameterMeta;
import org.openelis.ui.common.DataBaseUtil;
import org.openelis.ui.common.DatabaseException;
import org.openelis.ui.common.FieldErrorException;
import org.openelis.ui.common.LastPageException;
import org.openelis.ui.common.NotFoundException;
import org.openelis.ui.common.ValidationErrorsList;
import org.openelis.ui.common.data.QueryData;
import org.openelis.util.QueryBuilderV2;

@Stateless
@SecurityDomain("openelis")
public class AnalyteParameterBean {

    @PersistenceContext(unitName = "openelis")
    private EntityManager                     manager;

    @EJB
    private LockBean                          lock;

    @EJB
    private TestAnalyteBean                   testAnalyte;

    @EJB
    private QcAnalyteBean                     qcAnalyte;

    private static final AnalyteParameterMeta meta = new AnalyteParameterMeta();

    public ArrayList<AnalyteParameterViewDO> fetchByReferenceIdReferenceTableId(Integer referenceId,
                                                                                Integer referenceTableId) throws Exception {
        ArrayList<AnalyteParameterViewDO> aps;

        aps = null;

        if (Constants.table().TEST.equals(referenceTableId))
            aps = fetchByTestId(referenceId);
        else if (Constants.table().QC.equals(referenceTableId))
            aps = fetchByQcId(referenceId);
        else if (Constants.table().PROVIDER.equals(referenceTableId))
            aps = fetchByProviderId(referenceId);

        if (aps == null || aps.isEmpty())
            throw new NotFoundException();

        return DataBaseUtil.toArrayList(aps);
    }

    public ArrayList<AnalyteParameterViewDO> fetchByAnalyteIdReferenceIdReferenceTableId(Integer analyteId,
                                                                                         Integer refId,
                                                                                         Integer refTableId) throws Exception {
        Query query;
        List list;

        query = manager.createNamedQuery("AnalyteParameter.FetchByAnaIdRefIdRefTableId");
        query.setParameter("analyteId", analyteId);
        query.setParameter("referenceId", refId);
        query.setParameter("referenceTableId", refTableId);

        list = query.getResultList();
        if (list.isEmpty())
            throw new NotFoundException();

        return DataBaseUtil.toArrayList(list);
    }

    public AnalyteParameterViewDO fetchById(Integer id) throws Exception {
        Query query;
        AnalyteParameterViewDO data;

        query = manager.createNamedQuery("AnalyteParameter.FetchById");
        query.setParameter("id", id);

        try {
            data = (AnalyteParameterViewDO)query.getSingleResult();
        } catch (NoResultException e) {
            throw new NotFoundException();
        } catch (Exception e) {
            throw new DatabaseException(e);
        }

        return data;
    }

    public AnalyteParameterViewDO fetchForQcChartReport(Integer analyteId, Integer refId,
                                                        Integer refTableId,
                                                        Date worksheetCreatedDate) throws Exception {
        Query query;
        AnalyteParameterViewDO data;

        query = manager.createNamedQuery("AnalyteParameter.FetchForQcChartReport");
        query.setParameter("analyteId", analyteId);
        query.setParameter("referenceId", refId);
        query.setParameter("referenceTableId", refTableId);
        query.setParameter("worksheetCreatedDate", worksheetCreatedDate);

        try {
            data = (AnalyteParameterViewDO)query.getSingleResult();
        } catch (NoResultException e) {
            throw new NotFoundException();
        } catch (Exception e) {
            throw new DatabaseException(e);
        }
        return data;
    }

    public ArrayList<ReferenceIdTableIdNameVO> query(ArrayList<QueryData> fields, int first, int max) throws Exception {
        QueryBuilderV2 builder;
        Integer refTableId;
        List results;

        builder = new QueryBuilderV2();
        builder.setMeta(meta);

        refTableId = null;
        results = null;
        for (QueryData field : fields) {
            if (AnalyteParameterMeta.getReferenceTableId().equals(field.getKey()))
                refTableId = Integer.parseInt(field.getQuery());
        }

        if (Constants.table().TEST.equals(refTableId))
            results = testQuery(fields, builder, first, max);
        else if (Constants.table().QC.equals(refTableId))
            results = qcQuery(fields, builder, first, max);
        else if (Constants.table().PROVIDER.equals(refTableId))
            results = providerQuery(fields, builder, first, max);        

        if (results.isEmpty())
            throw new NotFoundException();
        results = (ArrayList<ReferenceIdTableIdNameVO>)DataBaseUtil.subList(results, first, max);
        if (results == null)
            throw new LastPageException();

        return (ArrayList<ReferenceIdTableIdNameVO>)results;
    }

    public AnalyteParameterDO add(AnalyteParameterDO data) throws Exception {
        AnalyteParameter entity;

        if (data.getActiveBegin() == null || data.getActiveEnd() == null ||
            (data.getP1() == null && data.getP2() == null && data.getP3() == null))
            return data;

        manager.setFlushMode(FlushModeType.COMMIT);

        entity = new AnalyteParameter();
        entity.setReferenceId(data.getReferenceId());
        entity.setReferenceTableId(data.getReferenceTableId());
        entity.setAnalyteId(data.getAnalyteId());
        entity.setTypeOfSampleId(data.getTypeOfSampleId());
        entity.setIsActive("N");
        entity.setActiveBegin(data.getActiveBegin());
        entity.setActiveEnd(data.getActiveEnd());
        entity.setP1(data.getP1());
        entity.setP2(data.getP2());
        entity.setP3(data.getP3());

        manager.persist(entity);
        data.setId(entity.getId());

        return data;
    }

    public AnalyteParameterDO update(AnalyteParameterDO data) throws Exception {
        AnalyteParameter entity;

        if ( !data.isChanged())
            return data;

        manager.setFlushMode(FlushModeType.COMMIT);

        entity = manager.find(AnalyteParameter.class, data.getId());
        entity.setReferenceId(data.getReferenceId());
        entity.setReferenceTableId(data.getReferenceTableId());
        entity.setAnalyteId(data.getAnalyteId());
        entity.setTypeOfSampleId(data.getTypeOfSampleId());
        entity.setIsActive("N");
        entity.setActiveBegin(data.getActiveBegin());
        entity.setActiveEnd(data.getActiveEnd());
        entity.setP1(data.getP1());
        entity.setP2(data.getP2());
        entity.setP3(data.getP3());

        return data;
    }

    public AnalyteParameterViewDO fetchForUpdate(Integer id) throws Exception {
        lock.lock(Constants.table().ANALYTE_PARAMETER, id);
        return fetchById(id);
    }

    public AnalyteParameterViewDO abortUpdate(Integer id) throws Exception {
        lock.unlock(Constants.table().ANALYTE_PARAMETER, id);
        return fetchById(id);
    }

    public void validate(AnalyteParameterViewDO data) throws Exception {
        ValidationErrorsList errors;
        boolean validateED;

        errors = new ValidationErrorsList();
        validateED = true;

        if (data.getActiveBegin() == null) {
            errors.add(new FieldErrorException(Messages.get()
                                                       .beginDateRequiredForAnalyteException(data.getAnalyteName()),
                                               ""));
            validateED = false;
        }
        if (data.getActiveEnd() == null) {
            errors.add(new FieldErrorException(Messages.get()
                                                       .endDateRequiredForAnalyteException(data.getAnalyteName()),
                                               ""));
            validateED = false;
        }
        if (data.getP1() == null && data.getP2() == null && data.getP3() == null)
            errors.add(new FieldErrorException(Messages.get()
                                                       .atleastOnePRequiredForAnalyteException(data.getAnalyteName()),
                                               ""));

        if (validateED && (data.getActiveBegin().compareTo(data.getActiveEnd()) >= 0))
            errors.add(new FieldErrorException(Messages.get()
                                                       .endDateInvalidWithParamException(data.getAnalyteName()),
                                               ""));

        if (errors.size() > 0)
            throw errors;
    }

    private List testQuery(ArrayList<QueryData> fields, QueryBuilderV2 builder, int first, int max) throws Exception {
        Query query;

        builder = new QueryBuilderV2();
        builder.setMeta(meta);

        builder.setSelect("distinct new org.openelis.domain.ReferenceIdTableIdNameVO(" +
                          AnalyteParameterMeta.getReferenceId() + ", " +
                          AnalyteParameterMeta.getReferenceTableId() + ", " +
                          AnalyteParameterMeta.getTestName() + ", " +
                          AnalyteParameterMeta.getTestMethodName() + ") ");
        builder.constructWhere(fields);
        builder.setOrderBy(AnalyteParameterMeta.getTestName());

        query = manager.createQuery(builder.getEJBQL());
        query.setMaxResults(first + max);
        builder.setQueryParams(query, fields);

        return query.getResultList();
    }

    private List qcQuery(ArrayList<QueryData> fields, QueryBuilderV2 builder, int first, int max) throws Exception {
        Query query;

        builder = new QueryBuilderV2();
        builder.setMeta(meta);

        builder.setSelect("distinct new org.openelis.domain.ReferenceIdTableIdNameVO(" +
                          AnalyteParameterMeta.getReferenceId() + ", " +
                          AnalyteParameterMeta.getReferenceTableId() + ", " +
                          AnalyteParameterMeta.getQcName() + ", ''" + ") ");
        builder.constructWhere(fields);
        builder.setOrderBy(AnalyteParameterMeta.getQcName());

        query = manager.createQuery(builder.getEJBQL());
        query.setMaxResults(first + max);
        builder.setQueryParams(query, fields);

        return query.getResultList();
    }

    private List providerQuery(ArrayList<QueryData> fields, QueryBuilderV2 builder, int first,
                               int max) {
        // TODO Auto-generated method stub
        return new ArrayList();
    }

    private ArrayList<AnalyteParameterViewDO> fetchByTestId(Integer testId) throws Exception {
        int start;
        Query query;
        AnalyteParameterViewDO ap;
        HashSet<Integer> foundAnas;
        ArrayList<ArrayList<TestAnalyteViewDO>> tas;
        ArrayList<AnalyteParameterViewDO> aps, mergedAps, tmp;
        HashMap<Integer, ArrayList<AnalyteParameterViewDO>> apMap;

        query = manager.createNamedQuery("AnalyteParameter.FetchByRefIdRefTableId");
        query.setParameter("referenceId", testId);
        query.setParameter("referenceTableId", Constants.table().TEST);
        aps = DataBaseUtil.toArrayList(query.getResultList());

        if (aps.isEmpty())
            return null;

        /*
         * create a mapping to group analyte parameters by their analytes
         */
        apMap = new HashMap<Integer, ArrayList<AnalyteParameterViewDO>>();
        for (int i = 0; i < aps.size(); i++ ) {
            ap = aps.get(i);            
            tmp = apMap.get(ap.getAnalyteId());
            if (tmp == null) {
                tmp = new ArrayList<AnalyteParameterViewDO>();
                apMap.put(ap.getAnalyteId(), tmp);
            }
            tmp.add(ap);
        }

        /*
         * create a merged list from existing parameters and test analytes;
         * existing parameters are added before empty parameters for analytes
         * that don't have parameters; among the existing ones, parameters are
         * ordered according to the test definition
         */
        tas = testAnalyte.fetchByTestId(testId);
        mergedAps = new ArrayList<AnalyteParameterViewDO>();
        start = 0;
        foundAnas = new HashSet<Integer>();
        for (ArrayList<TestAnalyteViewDO> row : tas) {
            for (TestAnalyteViewDO ta : row) {
                /*
                 * consider only row analytes and don't add parameters for an
                 * analyte more than once if it's in the test multiple times
                 */
                if ("Y".equals(ta.getIsColumn()) || foundAnas.contains(ta.getAnalyteId()))
                    continue;

                tmp = apMap.get(ta.getAnalyteId());
                if (tmp != null) {
                    /*
                     * add the parameters for this analyte right after the
                     * parameters for the previous analyte in the test
                     * definition
                     */
                    mergedAps.addAll(start, tmp);
                    start += tmp.size();
                } else {
                    /*
                     * this analyte doesn't have any parameters, so add an empty
                     * parameter for it at the end of the merged list
                     */
                    ap = new AnalyteParameterViewDO();
                    ap.setAnalyteId(ta.getAnalyteId());
                    ap.setAnalyteName(ta.getAnalyteName());
                    mergedAps.add(ap);
                }

                foundAnas.add(ta.getAnalyteId());
            }
        }

        return mergedAps;
    }

    private ArrayList<AnalyteParameterViewDO> fetchByQcId(Integer qcId) throws Exception {
        Query query;
        ArrayList<QcAnalyteViewDO> anaList;
        List<AnalyteParameterViewDO> list;
        Object taList[];
        AnalyteParameterViewDO data;
        HashMap<Integer, QcAnalyteViewDO> qcaMap;

        try {
            anaList = qcAnalyte.fetchByQcId(qcId);
        } catch (NotFoundException ex) {
            return new ArrayList<AnalyteParameterViewDO>();
        }

        qcaMap = new HashMap<Integer, QcAnalyteViewDO>();
        /*
         * the HashMap created below is used to make sure that all the test
         * analytes for this test get added in the form of a
         * AnalyeParameterViewDO to the list returned by this method even if
         * they are not present in the table analyte_parameter for this qc
         */
        for (QcAnalyteViewDO qca : anaList)
            qcaMap.put(qca.getAnalyteId(), qca);

        query = manager.createNamedQuery("AnalyteParameter.FetchByRefIdRefTableId");
        query.setParameter("referenceId", qcId);
        query.setParameter("referenceTableId", Constants.table().QC);
        list = query.getResultList();

        /*
         * if an analyte was found in the results returned by the previous query
         * then we remove the entry pertaining to it from the HashMap so that
         * after the loop below ends, only those analytes that are not active
         * but are present in qc_analyte are in the HashMap
         */
        for (AnalyteParameterViewDO temp : list) {
            if (qcaMap.get(temp.getAnalyteId()) != null)
                qcaMap.remove(temp.getAnalyteId());
        }

        if (qcaMap.size() > 0) {
            taList = qcaMap.values().toArray();
            //
            // we add any additional analytes to the list returned by this
            // method
            //
            for (Object o : taList) {
                data = new AnalyteParameterViewDO();
                data.setReferenceId(qcId);
                data.setReferenceTableId(Constants.table().QC);
                data.setAnalyteId( ((QcAnalyteViewDO)o).getAnalyteId());
                data.setAnalyteName( ((QcAnalyteViewDO)o).getAnalyteName());
                list.add(data);
            }
        }
        return DataBaseUtil.toArrayList(list);
    }

    private ArrayList<AnalyteParameterViewDO> fetchByProviderId(Integer referenceId) {
        // TODO Auto-generated method stub
        return null;
    }
}