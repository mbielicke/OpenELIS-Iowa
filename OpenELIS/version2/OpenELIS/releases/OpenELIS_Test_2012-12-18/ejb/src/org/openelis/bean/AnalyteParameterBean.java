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
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.FlushModeType;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.jboss.ejb3.annotation.SecurityDomain;
import org.openelis.domain.AnalyteParameterViewDO;
import org.openelis.domain.QcAnalyteViewDO;
import org.openelis.domain.ReferenceIdTableIdNameVO;
import org.openelis.domain.ReferenceTable;
import org.openelis.domain.TestAnalyteViewDO;
import org.openelis.entity.AnalyteParameter;
import org.openelis.gwt.common.DataBaseUtil;
import org.openelis.gwt.common.DatabaseException;
import org.openelis.gwt.common.Datetime;
import org.openelis.gwt.common.FieldErrorException;
import org.openelis.gwt.common.LastPageException;
import org.openelis.gwt.common.NotFoundException;
import org.openelis.gwt.common.ValidationErrorsList;
import org.openelis.gwt.common.data.QueryData;
import org.openelis.local.AnalyteParameterLocal;
import org.openelis.local.LockLocal;
import org.openelis.local.QcAnalyteLocal;
import org.openelis.local.TestAnalyteLocal;
import org.openelis.meta.AnalyteParameterMeta;
import org.openelis.remote.AnalyteParameterRemote;
import org.openelis.util.QueryBuilderV2;

@Stateless
@SecurityDomain("openelis")
public class AnalyteParameterBean implements AnalyteParameterRemote, AnalyteParameterLocal {

    @PersistenceContext(unitName = "openelis")
    private EntityManager                     manager;

    @EJB
    private LockLocal                         lock;   
    
    @EJB
    private TestAnalyteLocal                  testAnalyte; 
    
    @EJB
    private QcAnalyteLocal                    qcAnalyte;  
    
    private static final AnalyteParameterMeta meta = new AnalyteParameterMeta();
    
    public ArrayList<AnalyteParameterViewDO> fetchActiveByReferenceIdReferenceTableId(Integer referenceId,
                                                                                Integer referenceTableId) throws Exception {
        List list;        
        
        list = null;        
        switch (referenceTableId) {
            case ReferenceTable.TEST:
                list = fetchByTestId(referenceId);                    
                break;
            case ReferenceTable.QC:
                list = fetchByQcId(referenceId);
                break;
            case ReferenceTable.PROVIDER:
                break;
        }
        
        if (list.isEmpty())
            throw new NotFoundException();

        return DataBaseUtil.toArrayList(list);
    }
    
    public ArrayList<AnalyteParameterViewDO> fetchByAnalyteIdReferenceIdReferenceTableId(Integer analyteId, Integer refId, 
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
    
    public AnalyteParameterViewDO fetchActiveByAnalyteIdReferenceIdReferenceTableId(Integer analyteId, Integer refId, 
                                                                                         Integer refTableId) throws Exception {
        Query query;
        AnalyteParameterViewDO data;

        query = manager.createNamedQuery("AnalyteParameter.FetchActiveByAnaIdRefIdRefTableId");
        query.setParameter("analyteId", analyteId);
        query.setParameter("referenceId", refId);
        query.setParameter("referenceTableId", refTableId);
        try {
            data = (AnalyteParameterViewDO) query.getSingleResult();
        } catch (NoResultException e) {
            throw new NotFoundException();
        } catch (Exception e) {
            throw new DatabaseException(e);
        }
        return data;
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
    
    public AnalyteParameterViewDO fetchForQcChartReport (Integer analyteId, Integer refId, 
                                                         Integer refTableId, Date worksheetCreatedDate) throws Exception {
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

    public ArrayList<ReferenceIdTableIdNameVO> query(ArrayList<QueryData> fields,
                                                     int first, int max)  throws Exception {
        QueryBuilderV2 builder;
        Integer refTableId;
        QueryData refName;
        List results;

        builder = new QueryBuilderV2();
        builder.setMeta(meta);

        refTableId = null;
        results = null;
        refName = null;
        for (QueryData field : fields) {
            if (AnalyteParameterMeta.getReferenceTableId().equals(field.key)) 
                refTableId = Integer.parseInt(field.query);                                        
            else if (AnalyteParameterMeta.getReferenceName().equals(field.key)) 
                refName = field;                            
        }        
        switch (refTableId) {
            /*
             * The field that shows the name of the test, qc etc. on the screen 
             * has the key AnalyteParameterMeta.getReferenceName() because
             * it can't be named "test.name" or "qc.name" as it can represent
             * different reference tables at different times. Thus when a query 
             * contains that field, we have to replace its key with the right 
             * field for the reference table e.g "test.name" because otherwise,
             * the query that gets built below doesn't contain the name of the
             * reference table in the "from" clause.   
             */
            case ReferenceTable.TEST:                
                if (refName != null) {
                    refName.key = AnalyteParameterMeta.getTestName();
                } else { 
                    refName = new QueryData();
                    refName.key = AnalyteParameterMeta.getTestName();
                    refName.query = "*";
                    refName.type = QueryData.Type.STRING;
                    fields.add(refName);
                }
                results = testQuery(fields, builder, first, max);
                break;
            case ReferenceTable.QC:
                if (refName != null) {
                    refName.key = AnalyteParameterMeta.getQcName();
                } else {
                    refName = new QueryData();
                    refName.key = AnalyteParameterMeta.getQcName();
                    refName.query = "*";
                    refName.type = QueryData.Type.STRING;
                    fields.add(refName);
                }
                results = qcQuery(fields, builder, first, max);
                break;
            case ReferenceTable.PROVIDER:
                results = providerQuery(fields, builder, first, max);                
                break;
        }
        
        if (results.isEmpty())
            throw new NotFoundException();
        results = (ArrayList<ReferenceIdTableIdNameVO>)DataBaseUtil.subList(results, first, max);
        if (results == null)
            throw new LastPageException();

        return (ArrayList<ReferenceIdTableIdNameVO>)results;
    }

    public AnalyteParameterViewDO add(AnalyteParameterViewDO data) throws Exception {
        AnalyteParameter entity;
                
        if (DataBaseUtil.isEmpty(data.getActiveBegin()) || DataBaseUtil.isEmpty(data.getActiveEnd()) ||
                        DataBaseUtil.isEmpty(data.getP1()))
            return data;
        
        manager.setFlushMode(FlushModeType.COMMIT);
         
        entity = new AnalyteParameter();
        entity.setReferenceId(data.getReferenceId());
        entity.setReferenceTableId(data.getReferenceTableId());
        entity.setAnalyteId(data.getAnalyteId());
        entity.setTypeOfSampleId(data.getTypeOfSampleId());
        entity.setIsActive(data.getIsActive());
        entity.setActiveBegin(data.getActiveBegin());
        entity.setActiveEnd(data.getActiveEnd());
        entity.setP1(data.getP1());
        entity.setP2(data.getP2());
        entity.setP3(data.getP3());
        
        manager.persist(entity);
        data.setId(entity.getId());
        
        return data;
    }    
    
    public AnalyteParameterViewDO update(AnalyteParameterViewDO data) throws Exception {
        AnalyteParameter entity;
        
        if (!data.isChanged())
            return data;
        
        manager.setFlushMode(FlushModeType.COMMIT);
        
        entity = manager.find(AnalyteParameter.class, data.getId());
        entity.setReferenceId(data.getReferenceId());
        entity.setReferenceTableId(data.getReferenceTableId());
        entity.setAnalyteId(data.getAnalyteId());
        entity.setTypeOfSampleId(data.getTypeOfSampleId());
        entity.setIsActive(data.getIsActive());
        entity.setActiveBegin(data.getActiveBegin());
        entity.setActiveEnd(data.getActiveEnd());
        entity.setP1(data.getP1());
        entity.setP2(data.getP2());
        entity.setP3(data.getP3());
        
        return data;
    }   

    public AnalyteParameterViewDO fetchForUpdate(Integer id) throws Exception {
        lock.lock(ReferenceTable.ANALYTE_PARAMETER, id);
        return fetchById(id);
    }

    public AnalyteParameterViewDO abortUpdate(Integer id) throws Exception {
        lock.unlock(ReferenceTable.ANALYTE_PARAMETER, id);
        return fetchById(id);
    }

    public void validate(AnalyteParameterViewDO data) throws Exception {
        ValidationErrorsList errors;
        boolean validateED;

        errors = new ValidationErrorsList();
        validateED = true;
        
        if (DataBaseUtil.isEmpty(data.getActiveBegin())) {
            errors.add(new FieldErrorException("beginDateRequiredForAnalyteException","", data.getAnalyteName()));
            validateED = false;
        } 
        if (DataBaseUtil.isEmpty(data.getActiveEnd())) {
            errors.add(new FieldErrorException("endDateRequiredForAnalyteException","", data.getAnalyteName()));
            validateED = false;
        }
        if (DataBaseUtil.isEmpty(data.getP1()))
            errors.add(new FieldErrorException("p1RequiredForAnalyteException","",data.getAnalyteName()));
            
        if (validateED && !endDateValid(data)) 
            errors.add(new FieldErrorException("endDateInvalidWithParamException","", data.getAnalyteName()));                
        
        if (errors.size() > 0)
            throw errors;
    }    
    
    private List testQuery(ArrayList<QueryData> fields, QueryBuilderV2 builder,
                           int first, int max) throws Exception{
        Query query;
        
        builder = new QueryBuilderV2();
        builder.setMeta(meta);                        
      
        builder.setSelect("distinct new org.openelis.domain.ReferenceIdTableIdNameVO(" +AnalyteParameterMeta.getReferenceId() +
                          ", " + AnalyteParameterMeta.getReferenceTableId()+                          
                          ", " + AnalyteParameterMeta.getTestName()+                          
                          ", " + AnalyteParameterMeta.getTestMethodName()+  ") ");        
        builder.constructWhere(fields);
        builder.setOrderBy(AnalyteParameterMeta.getTestName());

        query = manager.createQuery(builder.getEJBQL());
        query.setMaxResults(first + max);
        builder.setQueryParams(query, fields);

        return query.getResultList();
    }   
    
    private List qcQuery(ArrayList<QueryData> fields, QueryBuilderV2 builder,
                           int first, int max) throws Exception{
        Query query;
        
        builder = new QueryBuilderV2();
        builder.setMeta(meta);                        
      
        builder.setSelect("distinct new org.openelis.domain.ReferenceIdTableIdNameVO(" +AnalyteParameterMeta.getReferenceId() +
                          ", " + AnalyteParameterMeta.getReferenceTableId()+                          
                          ", " + AnalyteParameterMeta.getQcName()+", ''"+  ") ");        
        builder.constructWhere(fields);
        builder.setOrderBy(AnalyteParameterMeta.getQcName());

        query = manager.createQuery(builder.getEJBQL());
        query.setMaxResults(first + max);
        builder.setQueryParams(query, fields);

        return query.getResultList();
    }   
    
    private List providerQuery(ArrayList<QueryData> fields,
                               QueryBuilderV2 builder,
                               int first,
                               int max) {
        // TODO Auto-generated method stub
        return new ArrayList();
    }
    
    private ArrayList<AnalyteParameterViewDO> fetchByTestId(Integer testId) throws Exception {
        Query query;
        ArrayList<ArrayList<TestAnalyteViewDO>> grid;
        Object taList[];
        List<AnalyteParameterViewDO> list;
        AnalyteParameterViewDO data;
        HashMap<Integer, TestAnalyteViewDO> taMap;    
        
        try {
            grid = testAnalyte.fetchByTestId(testId);
        } catch (NotFoundException ex) {            
            return new ArrayList<AnalyteParameterViewDO>();
        }
        
        /*
         * the HashMap created below is used to make sure that all the test analytes
         * for this test get added in the form of a AnalyeParameterViewDO to the
         * list returned by this method even if they are not present in the table
         * analyte_parameter for this test         
         */
        taMap = new HashMap<Integer, TestAnalyteViewDO>();
        for (ArrayList<TestAnalyteViewDO> row : grid) {
            for (TestAnalyteViewDO ta : row) {
                if ("N".equals(ta.getIsColumn())) {
                    taMap.put(ta.getAnalyteId(), ta);
                    break;
                } 
            }
        }        
        
        query = manager.createNamedQuery("AnalyteParameter.FetchActiveByRefIdRefTableId");                            
        query.setParameter("referenceId", testId);   
        query.setParameter("referenceTableId", ReferenceTable.TEST);    
        list = query.getResultList();
        
        /*
         * if an analyte was found in the results returned by the previous query
         * then we remove the entry pertaining to it from the HashMap so that
         * after the loop below ends, only those analytes that are not active but
         * are present in test_analyte are in the HashMap  
         */
        for (AnalyteParameterViewDO temp : list) {
            if (taMap.get(temp.getAnalyteId()) != null) 
                taMap.remove(temp.getAnalyteId());                
        }
                
        if (taMap.size() > 0) {
            taList = taMap.values().toArray();
             //
             // we add any additional analytes to the list returned by this method  
             //
            for (Object o : taList) {
                data = new AnalyteParameterViewDO();
                data.setReferenceId(testId);
                data.setReferenceTableId(ReferenceTable.TEST);
                data.setAnalyteId(((TestAnalyteViewDO)o).getAnalyteId());
                data.setAnalyteName(((TestAnalyteViewDO)o).getAnalyteName());
                data.setIsActive("N");                
                list.add(data);
            }
        }
        return DataBaseUtil.toArrayList(list);
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
         * the HashMap created below is used to make sure that all the test analytes
         * for this test get added in the form of a AnalyeParameterViewDO to the
         * list returned by this method even if they are not present in the table
         * analyte_parameter for this qc         
         */
        for (QcAnalyteViewDO qca : anaList) 
            qcaMap.put(qca.getAnalyteId(), qca);
        
        query = manager.createNamedQuery("AnalyteParameter.FetchActiveByRefIdRefTableId");                            
        query.setParameter("referenceId", qcId);    
        query.setParameter("referenceTableId", ReferenceTable.QC);    
        list = query.getResultList();

        /*
         * if an analyte was found in the results returned by the previous query
         * then we remove the entry pertaining to it from the HashMap so that
         * after the loop below ends, only those analytes that are not active but
         * are present in qc_analyte are in the HashMap  
         */
        for (AnalyteParameterViewDO temp : list) {
            if (qcaMap.get(temp.getAnalyteId()) != null) 
                qcaMap.remove(temp.getAnalyteId());                
        }
        
        if (qcaMap.size() > 0) {
            taList = qcaMap.values().toArray();
             //
             // we add any additional analytes to the list returned by this method  
             //
            for (Object o : taList) {
                data = new AnalyteParameterViewDO();
                data.setReferenceId(qcId);
                data.setReferenceTableId(ReferenceTable.QC);
                data.setAnalyteId(((QcAnalyteViewDO)o).getAnalyteId());
                data.setAnalyteName(((QcAnalyteViewDO)o).getAnalyteName());
                data.setIsActive("N");                
                list.add(data);
            }
        }
        return DataBaseUtil.toArrayList(list);        
    }          
    
    private boolean endDateValid(AnalyteParameterViewDO data) {
        //
        // end date must be after begin date 
        //
        return (data.getActiveBegin().compareTo(data.getActiveEnd()) == -1);        
    }
}
