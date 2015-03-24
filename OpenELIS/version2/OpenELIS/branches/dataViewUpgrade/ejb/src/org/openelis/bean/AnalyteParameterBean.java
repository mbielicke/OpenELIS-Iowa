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
import org.openelis.domain.ReferenceIdTableIdNameVO;
import org.openelis.entity.AnalyteParameter;
import org.openelis.meta.AnalyteParameterMeta;
import org.openelis.ui.common.DataBaseUtil;
import org.openelis.ui.common.DatabaseException;
import org.openelis.ui.common.FieldErrorException;
import org.openelis.ui.common.FormErrorException;
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

    private static final AnalyteParameterMeta meta = new AnalyteParameterMeta();

    public ArrayList<AnalyteParameterViewDO> fetchByReferenceIdReferenceTableId(Integer referenceId,
                                                                                Integer referenceTableId) throws Exception {
        Query query;

        query = manager.createNamedQuery("AnalyteParameter.FetchByRefIdRefTableId");
        query.setParameter("referenceId", referenceId);
        query.setParameter("referenceTableId", referenceTableId);

        return DataBaseUtil.toArrayList(query.getResultList());
    }
    
    public ArrayList<AnalyteParameterViewDO> fetchByReferenceIdReferenceTableId(Integer referenceId,
                                                                                Integer referenceTableId, int max) throws Exception {
        Query query;

        query = manager.createNamedQuery("AnalyteParameter.FetchByRefIdRefTableId");
        query.setParameter("referenceId", referenceId);
        query.setParameter("referenceTableId", referenceTableId);
        query.setMaxResults(max);

        return DataBaseUtil.toArrayList(query.getResultList());
    }

    public ArrayList<AnalyteParameterViewDO> fetchByAnalyteIdReferenceIdReferenceTableId(Integer analyteId,
                                                                                         Integer refId,
                                                                                         Integer refTableId) throws Exception {
        Query query;

        query = manager.createNamedQuery("AnalyteParameter.FetchByAnaIdRefIdRefTableId");
        query.setParameter("analyteId", analyteId);
        query.setParameter("referenceId", refId);
        query.setParameter("referenceTableId", refTableId);

        return DataBaseUtil.toArrayList(query.getResultList());
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

    public ArrayList<AnalyteParameterViewDO> fetchByActiveDate(Integer refId, Integer refTableId,
                                                               Date activeDate) throws Exception {
        List list;
        Query query;

        query = manager.createNamedQuery("AnalyteParameter.FetchByActiveDate");
        query.setParameter("referenceId", refId);
        query.setParameter("referenceTableId", refTableId);
        query.setParameter("activeDate", activeDate);

        list = query.getResultList();
        if (list.isEmpty())
            throw new NotFoundException();

        return DataBaseUtil.toArrayList(list);
    }

    public ArrayList<ReferenceIdTableIdNameVO> query(ArrayList<QueryData> fields,
                                                     int first, int max) throws Exception {
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

        manager.setFlushMode(FlushModeType.COMMIT);

        entity = new AnalyteParameter();
        entity.setReferenceId(data.getReferenceId());
        entity.setReferenceTableId(data.getReferenceTableId());
        entity.setAnalyteId(data.getAnalyteId());
        entity.setTypeOfSampleId(data.getTypeOfSampleId());
        entity.setUnitOfMeasureId(data.getUnitOfMeasureId());
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
        entity.setUnitOfMeasureId(data.getUnitOfMeasureId());
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
    
    public void delete(AnalyteParameterViewDO data) throws Exception {
        AnalyteParameter entity;
        
        manager.setFlushMode(FlushModeType.COMMIT);
        entity = manager.find(AnalyteParameter.class, data.getId());
        if (entity != null)
            manager.remove(entity);
    }
    
    /**
     * Used by the new manager
     */
    public void validate(AnalyteParameterViewDO data, String combo) throws Exception {
        ValidationErrorsList e;
        boolean validateED;

        e = new ValidationErrorsList();
        validateED = true;

        if (data.getActiveBegin() == null) {
            e.add(new FormErrorException(Messages.get()
                                                       .analyteParameter_beginDateRequiredException(combo)));
            validateED = false;
        }
        if (data.getActiveEnd() == null) {
            e.add(new FormErrorException(Messages.get()
                                                       .analyteParameter_endDateRequiredException(combo)));
            validateED = false;
        }
        if (data.getP1() == null && data.getP2() == null && data.getP3() == null)
            e.add(new FormErrorException(Messages.get()
                                                       .analyteParameter_atleastOnePRequiredException(combo)));

        if (validateED && (data.getActiveBegin().compareTo(data.getActiveEnd()) >= 0))
            e.add(new FormErrorException(Messages.get()
                                                       .analyteParameter_endDateAfterBeginException(combo)));

        if (e.size() > 0)
            throw e;
    }

    /**
     * Used by the old manager
     */
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
}