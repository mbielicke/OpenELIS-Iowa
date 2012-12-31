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
import org.openelis.domain.Constants;
import org.openelis.domain.QcChartResultVO;
import org.openelis.domain.ToDoWorksheetVO;
import org.openelis.domain.WorksheetAnalysisDO;
import org.openelis.entity.WorksheetAnalysis;
import org.openelis.gwt.common.DataBaseUtil;
import org.openelis.gwt.common.DatabaseException;
import org.openelis.gwt.common.FieldErrorException;
import org.openelis.gwt.common.NotFoundException;
import org.openelis.gwt.common.SystemUserVO;
import org.openelis.gwt.common.ValidationErrorsList;
import org.openelis.meta.WorksheetCompletionMeta;

@Stateless
@SecurityDomain("openelis")
public class WorksheetAnalysisBean {

    @PersistenceContext(unitName = "openelis")
    private EntityManager manager;
    
    @EJB
    private UserCacheBean        userCache;
    
    @SuppressWarnings("unchecked")
    public ArrayList<WorksheetAnalysisDO> fetchByWorksheetItemId(Integer id) throws Exception {
        Query query;
        List list;

        query = manager.createNamedQuery("WorksheetAnalysis.FetchByWorksheetItemId");
        query.setParameter("id", id);

        list = query.getResultList();
        if (list.isEmpty())
            throw new NotFoundException();

        return DataBaseUtil.toArrayList(list);
    }

    public WorksheetAnalysisDO fetchById(Integer id) throws Exception {
        Query query;
        WorksheetAnalysisDO data;

        query = manager.createNamedQuery("WorksheetAnalysis.FetchById");
        query.setParameter("id", id);
        try {
            data = (WorksheetAnalysisDO)query.getSingleResult();
        } catch (NoResultException e) {
            throw new NotFoundException();
        } catch (Exception e) {
            throw new DatabaseException(e);
        }

        return data;
    }
    
    @SuppressWarnings("unchecked")
    public ArrayList<WorksheetAnalysisDO> fetchByQcLotId(Integer id) throws Exception {
        Query query;
        List list;

        query = manager.createNamedQuery("WorksheetAnalysis.FetchByQcLotId");
        query.setParameter("id", id);

        list = query.getResultList();
        if (list.isEmpty())
            throw new NotFoundException();

        return DataBaseUtil.toArrayList(list);
    }

    public ArrayList<ToDoWorksheetVO> fetchByWorking() throws Exception {
        Query query;
        List<ToDoWorksheetVO> list;
        SystemUserVO user;

        query = manager.createNamedQuery("WorksheetAnalysis.FetchByWorksheetStatusId");
        query.setParameter("statusId", Constants.dictionary().WORKSHEET_WORKING);
        list = query.getResultList();

        for (ToDoWorksheetVO data : list) {
            user = userCache.getSystemUser(data.getSystemUserId());
            if (user != null)
                data.setSystemUserName(user.getLoginName());
        }
        return DataBaseUtil.toArrayList(list);
    }
    
    public ArrayList<QcChartResultVO> fetchByDateForQcChart(Date dateFrom, Date dateTo, String qcName) throws Exception {
        Query query;
        query = manager.createNamedQuery("WorksheetAnalysis.FetchByDateForQcChart");
        query.setParameter("startedDate", dateFrom);
        query.setParameter("endDate", dateTo);
        query.setParameter("qcName", qcName);
        
        return DataBaseUtil.toArrayList(query.getResultList());
    }
    
    public ArrayList<QcChartResultVO> fetchByInstancesForQcChart(Integer numInstances, String qcName) throws Exception {
        Integer id;
        Query query;
        ArrayList<Object[]> list;
        ArrayList<Integer> ids;

        query = manager.createNamedQuery("WorksheetAnalysis.FetchByInstancesForQcChart");
        query.setParameter("qcName", qcName);
        query.setMaxResults(numInstances);

        list = DataBaseUtil.toArrayList(query.getResultList());
        
        ids = new ArrayList<Integer>();
        for (int i = 0; i < list.size(); i++ ) {
            id = (Integer)(list.get(i))[1];
            if (id != null)
                ids.add(id);
        }
        if (ids.size() == 0)
            return new ArrayList<QcChartResultVO>();

        query = manager.createNamedQuery("WorksheetAnalysis.FetchAnalytesForQcChart");
        query.setParameter("ids", ids);

        return DataBaseUtil.toArrayList(query.getResultList());
    }
    
    public WorksheetAnalysisDO add(WorksheetAnalysisDO data) throws Exception {
        WorksheetAnalysis entity;

        manager.setFlushMode(FlushModeType.COMMIT);

        entity = new WorksheetAnalysis();
        entity.setWorksheetItemId(data.getWorksheetItemId());
        entity.setAccessionNumber(data.getAccessionNumber());
        entity.setAnalysisId(data.getAnalysisId());
        entity.setQcLotId(data.getQcLotId());
        entity.setWorksheetAnalysisId(data.getWorksheetAnalysisId());
        entity.setQcSystemUserId(data.getQcSystemUserId());
        entity.setQcStartedDate(data.getQcStartedDate());
        entity.setIsFromOther(data.getIsFromOther());

        manager.persist(entity);
        data.setId(entity.getId());

        return data;
    }

    public WorksheetAnalysisDO update(WorksheetAnalysisDO data) throws Exception {
        WorksheetAnalysis entity;

        if ( !data.isChanged())
            return data;

        manager.setFlushMode(FlushModeType.COMMIT);

        entity = manager.find(WorksheetAnalysis.class, data.getId());
        entity.setAccessionNumber(data.getAccessionNumber());
        entity.setAnalysisId(data.getAnalysisId());
        entity.setQcLotId(data.getQcLotId());
        entity.setWorksheetAnalysisId(data.getWorksheetAnalysisId());
        entity.setQcSystemUserId(data.getQcSystemUserId());
        entity.setQcStartedDate(data.getQcStartedDate());
        entity.setIsFromOther(data.getIsFromOther());

        return data;
    }

    public void delete(WorksheetAnalysisDO data) throws Exception {
        WorksheetAnalysis entity;

        manager.setFlushMode(FlushModeType.COMMIT);

        entity = manager.find(WorksheetAnalysis.class, data.getId());
        if (entity != null)
            manager.remove(entity);
    }

    public void validate(WorksheetAnalysisDO data) throws Exception {
        ValidationErrorsList list;

        list = new ValidationErrorsList();
        if (DataBaseUtil.isEmpty(data.getAccessionNumber()))
            list.add(new FieldErrorException("fieldRequiredException",
                                             WorksheetCompletionMeta.getWorksheetAnalysisAccessionNumber()));
        if (DataBaseUtil.isEmpty(data.getAnalysisId()) && DataBaseUtil.isEmpty(data.getQcLotId())) {
            list.add(new FieldErrorException("fieldRequiredException",
                                             WorksheetCompletionMeta.getWorksheetAnalysisAnalysisId()));
            list.add(new FieldErrorException("fieldRequiredException",
                                             WorksheetCompletionMeta.getWorksheetAnalysisQcLotId()));
        }

        if (list.size() > 0)
            throw list;
    }   
}
