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
import java.util.Collection;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.FlushModeType;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.jboss.ejb3.annotation.SecurityDomain;
import org.openelis.domain.IdNameVO;
import org.openelis.domain.QaEventDO;
import org.openelis.domain.QaEventVO;
import org.openelis.domain.QaEventViewDO;
import org.openelis.domain.ReferenceTable;
import org.openelis.entity.QaEvent;
import org.openelis.gwt.common.DataBaseUtil;
import org.openelis.gwt.common.DatabaseException;
import org.openelis.gwt.common.FieldErrorException;
import org.openelis.gwt.common.FormErrorException;
import org.openelis.gwt.common.LastPageException;
import org.openelis.gwt.common.ModulePermission.ModuleFlags;
import org.openelis.gwt.common.NotFoundException;
import org.openelis.gwt.common.ValidationErrorsList;
import org.openelis.gwt.common.data.QueryData;
import org.openelis.local.LockLocal;
import org.openelis.local.QaeventLocal;
import org.openelis.meta.QaEventMeta;
import org.openelis.remote.QaEventRemote;
import org.openelis.util.QueryBuilderV2;
import org.openelis.utils.EJBFactory;

@Stateless
@SecurityDomain("openelis")

public class QaEventBean implements QaEventRemote, QaeventLocal {

    @PersistenceContext(unitName = "openelis")
    private EntityManager               manager;

    @EJB
    private LockLocal                   lock;

    private static final QaEventMeta    meta = new QaEventMeta();

    public QaEventViewDO fetchById(Integer id) throws Exception {
        Query query;
        QaEventViewDO data;

        query = manager.createNamedQuery("QaEvent.FetchById");
        query.setParameter("id", id);
        try {
            data = (QaEventViewDO)query.getSingleResult();
        } catch (NoResultException e) {
            throw new NotFoundException();
        } catch (Exception e) {
            throw new DatabaseException(e);
        }
        return data;
    }
    
    @SuppressWarnings("unchecked")
    public ArrayList<QaEventViewDO> fetchByIds(Collection<Integer> ids) {
        Query query;

        query = manager.createNamedQuery("QaEvent.FetchByIds");
        query.setParameter("ids", ids);
        return DataBaseUtil.toArrayList(query.getResultList());
    }
    
    @SuppressWarnings("unchecked")
    public ArrayList<QaEventVO> fetchByName(String name) throws Exception {
        Query query;
    
        query = manager.createNamedQuery("QaEvent.FetchByName");
        query.setParameter("name", name);
        return DataBaseUtil.toArrayList(query.getResultList());
    }

    @SuppressWarnings("unchecked")
    public ArrayList<QaEventVO> fetchByTestId(Integer id) throws Exception {
        Query query;
        
        query = manager.createNamedQuery("QaEvent.FetchByTestId");
        query.setParameter("testId", id);
        return DataBaseUtil.toArrayList(query.getResultList());
    }

    @SuppressWarnings("unchecked")
    public ArrayList<QaEventVO> fetchByCommon() throws Exception {
        Query query;

        query = manager.createNamedQuery("QaEvent.FetchByCommon");
        return DataBaseUtil.toArrayList(query.getResultList());
    }
    
    @SuppressWarnings("unchecked")
    public ArrayList<QaEventDO> fetchBySampleId(Integer sampleId) {
        Query query;

        query = manager.createNamedQuery("QaEvent.FetchBySampleId");
        query.setParameter("id", sampleId);
        return DataBaseUtil.toArrayList(query.getResultList());
    }

    @SuppressWarnings("unchecked")
    public ArrayList<QaEventDO> fetchNotInternalBySampleId(Integer sampleId) {
        Query query;

        query = manager.createNamedQuery("QaEvent.FetchNotInternalBySampleId");
        query.setParameter("id", sampleId);
        return DataBaseUtil.toArrayList(query.getResultList());
    }

    @SuppressWarnings("unchecked")
    public ArrayList<QaEventDO> fetchByAnalysisId(Integer analysisId) {
        Query query;

        query = manager.createNamedQuery("QaEvent.FetchByAnalysisId");
        query.setParameter("id", analysisId);
        return DataBaseUtil.toArrayList(query.getResultList());
    }

    @SuppressWarnings("unchecked")
    public ArrayList<QaEventDO> fetchNotInternalByAnalysisId(Integer analysisId) {
        Query query;

        query = manager.createNamedQuery("QaEvent.FetchNotInternalByAnalysisId");
        query.setParameter("id", analysisId);
        return DataBaseUtil.toArrayList(query.getResultList());
    }

    @SuppressWarnings("unchecked")
    public ArrayList<IdNameVO> query(ArrayList<QueryData> fields, int first, int max) throws Exception {
        Query query;
        QueryBuilderV2 builder;
        List list;

        builder = new QueryBuilderV2();
        builder.setMeta(meta);
        builder.setSelect("distinct new org.openelis.domain.IdNameVO(" +
                          QaEventMeta.getId() + "," +
                          QaEventMeta.getName() + "," +
                          QaEventMeta.getTestName() + ")");
        builder.constructWhere(fields);
        builder.setOrderBy(QaEventMeta.getName() + "," + QaEventMeta.getTestName());

        query = manager.createQuery(builder.getEJBQL());
        query.setMaxResults(first + max);
        builder.setQueryParams(query, fields);

        list = query.getResultList();
        if (list.isEmpty())
            throw new NotFoundException();
        list = (ArrayList<IdNameVO>)DataBaseUtil.subList(list, first, max);
        if (list == null)
            throw new LastPageException();

        return (ArrayList<IdNameVO>)list;
    }

    public QaEventViewDO add(QaEventViewDO data) throws Exception {
        QaEvent entity;

        checkSecurity(ModuleFlags.ADD);

        validate(data);

        manager.setFlushMode(FlushModeType.COMMIT);

        entity = new QaEvent();
        entity.setName(data.getName());
        entity.setDescription(data.getDescription());
        entity.setTestId(data.getTestId());
        entity.setTypeId(data.getTypeId());
        entity.setIsBillable(data.getIsBillable());
        entity.setReportingSequence(data.getReportingSequence());
        entity.setReportingText(data.getReportingText());

        manager.persist(entity);
        data.setId(entity.getId());

        return data;
    }

    public QaEventViewDO update(QaEventViewDO data) throws Exception {
        QaEvent entity;

        if ( !data.isChanged()) {
            lock.unlock(ReferenceTable.QAEVENT, data.getId());
            return data;
        }
        checkSecurity(ModuleFlags.UPDATE);

        validate(data);

        lock.validateLock(ReferenceTable.QAEVENT, data.getId());

        manager.setFlushMode(FlushModeType.COMMIT);
        entity = manager.find(QaEvent.class, data.getId());
        entity.setName(data.getName());
        entity.setDescription(data.getDescription());
        entity.setTestId(data.getTestId());
        entity.setTypeId(data.getTypeId());
        entity.setIsBillable(data.getIsBillable());
        entity.setReportingSequence(data.getReportingSequence());
        entity.setReportingText(data.getReportingText());

        lock.unlock(ReferenceTable.QAEVENT, data.getId());

        return data;
    }

    public QaEventViewDO fetchForUpdate(Integer id) throws Exception {
        try {
            lock.lock(ReferenceTable.QAEVENT, id);
            return fetchById(id);
        } catch (NotFoundException e) {
            throw new DatabaseException(e);
        }
    }

    public QaEventViewDO abortUpdate(Integer id) throws Exception {
        lock.unlock(ReferenceTable.QAEVENT, id);
        return fetchById(id);
    }

    public void validate(QaEventDO data) throws Exception {
        ValidationErrorsList list;
        ArrayList<QaEventVO> dups;

        list = new ValidationErrorsList();

        if (DataBaseUtil.isEmpty(data.getName())) {
            list.add(new FieldErrorException("fieldRequiredException", QaEventMeta.getName()));
        } else {
            //
            // do not allow duplicates names for the same test or
            // for the general group (test = null)
            //
            try {
                dups = fetchByName(data.getName());
                for (QaEventVO dup : dups) {
                    if (DataBaseUtil.isDifferent(data.getId(), dup.getId()) &&
                        !DataBaseUtil.isDifferent(data.getName(), dup.getName()) &&
                        !DataBaseUtil.isDifferent(data.getTestId(), dup.getTestId()))
                        list.add(new FormErrorException("qaeventTestComboUnique"));
                }
            } catch (NotFoundException ignE) {
            }
        }

        if (DataBaseUtil.isEmpty(data.getTypeId()))
            list.add(new FieldErrorException("fieldRequiredException", QaEventMeta.getTypeId()));

        if (DataBaseUtil.isEmpty(data.getReportingText()))
            list.add(new FieldErrorException("fieldRequiredException", QaEventMeta.getReportingText()));

        if (list.size() > 0)
            throw list;
    }

    private void checkSecurity(ModuleFlags flag) throws Exception {
        EJBFactory.getUserCache().applyPermission("qaevent", flag);
    }
}
