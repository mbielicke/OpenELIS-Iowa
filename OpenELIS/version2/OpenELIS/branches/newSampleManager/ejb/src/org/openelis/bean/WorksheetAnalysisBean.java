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
import org.openelis.domain.Constants;
import org.openelis.domain.QcChartResultVO;
import org.openelis.domain.ToDoWorksheetVO;
import org.openelis.domain.WorksheetAnalysisDO;
import org.openelis.domain.WorksheetAnalysisViewDO;
import org.openelis.domain.WorksheetAnalysisViewVO;
import org.openelis.entity.WorksheetAnalysis;
import org.openelis.meta.WorksheetCompletionMeta;
import org.openelis.ui.common.DataBaseUtil;
import org.openelis.ui.common.DatabaseException;
import org.openelis.ui.common.FieldErrorException;
import org.openelis.ui.common.NotFoundException;
import org.openelis.ui.common.SystemUserVO;
import org.openelis.ui.common.ValidationErrorsList;

@Stateless
@SecurityDomain("openelis")
public class WorksheetAnalysisBean {

    @PersistenceContext(unitName = "openelis")
    private EntityManager manager;
    
    @EJB
    private UserCacheBean       userCache;

    @SuppressWarnings("unchecked")
    public ArrayList<WorksheetAnalysisViewDO> fetchByWorksheetId(Integer id) throws Exception {
        int i;
        List list, returnList;
        Query query;
        WorksheetAnalysisViewDO waVDO;
        WorksheetAnalysisViewVO waVVO;

        query = manager.createNamedQuery("WorksheetAnalysisView.FetchByWorksheetId");
        query.setParameter("worksheetId", id);

        list = query.getResultList();
        if (list.isEmpty())
            throw new NotFoundException();

        returnList = new ArrayList<WorksheetAnalysisViewDO>();
        for (i = 0; i < list.size(); i++) {
            waVVO = (WorksheetAnalysisViewVO) list.get(i);
            waVDO = new WorksheetAnalysisViewDO();
            copyViewToDO(waVVO, waVDO);
            returnList.add(waVDO);
        }
        
        return DataBaseUtil.toArrayList(returnList);
    }

    @SuppressWarnings("unchecked")
    public ArrayList<WorksheetAnalysisViewDO> fetchByWorksheetIds(ArrayList<Integer> ids) throws Exception {
        int i;
        List list, returnList;
        Query query;
        WorksheetAnalysisViewDO waVDO;
        WorksheetAnalysisViewVO waVVO;

        query = manager.createNamedQuery("WorksheetAnalysisView.FetchByWorksheetIds");
        query.setParameter("worksheetIds", ids);

        list = query.getResultList();
        if (list.isEmpty())
            throw new NotFoundException();

        returnList = new ArrayList<WorksheetAnalysisViewDO>();
        for (i = 0; i < list.size(); i++) {
            waVVO = (WorksheetAnalysisViewVO) list.get(i);
            waVDO = new WorksheetAnalysisViewDO();
            copyViewToDO(waVVO, waVDO);
            returnList.add(waVDO);
        }
        
        return DataBaseUtil.toArrayList(returnList);
    }

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

    @SuppressWarnings("unchecked")
    public ArrayList<WorksheetAnalysisDO> fetchByWorksheetItemIds(ArrayList<Integer> ids) throws Exception {
        Query query;
        List list;

        query = manager.createNamedQuery("WorksheetAnalysis.FetchByWorksheetItemIds");
        query.setParameter("ids", ids);
        
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
    
    public ArrayList<QcChartResultVO> fetchByDateForQcChart(Date dateFrom, Date dateTo, String qcName, Integer qcLocation) throws Exception {
        Query query;
        query = manager.createNamedQuery("WorksheetAnalysis.FetchByDateForQcChart");
        query.setParameter("startedDate", dateFrom);
        query.setParameter("endDate", dateTo);
        query.setParameter("qcName", qcName);
        query.setParameter("qcLocation", qcLocation);
        
        return DataBaseUtil.toArrayList(query.getResultList());
    }
    
    public ArrayList<QcChartResultVO> fetchByInstancesForQcChart(Integer numInstances, String qcName, Integer qcLocation) throws Exception {
        Integer id;
        Query query;
        ArrayList<Object[]> list;
        ArrayList<Integer> ids;

        query = manager.createNamedQuery("WorksheetAnalysis.FetchByInstancesForQcChart");
        query.setParameter("qcName", qcName);
        query.setParameter("qcLocation", qcLocation);
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
            list.add(new FieldErrorException(Messages.get().fieldRequiredException(),
                                             WorksheetCompletionMeta.getWorksheetAnalysisAccessionNumber()));
        if (DataBaseUtil.isEmpty(data.getAnalysisId()) && DataBaseUtil.isEmpty(data.getQcLotId())) {
            list.add(new FieldErrorException(Messages.get().fieldRequiredException(),
                                             WorksheetCompletionMeta.getWorksheetAnalysisAnalysisId()));
            list.add(new FieldErrorException(Messages.get().fieldRequiredException(),
                                             WorksheetCompletionMeta.getWorksheetAnalysisQcLotId()));
        }

        if (list.size() > 0)
            throw list;
    }
    
    private void copyViewToDO(WorksheetAnalysisViewVO waVVO, WorksheetAnalysisViewDO waVDO) {
        waVDO.setId(waVVO.getId());
        waVDO.setWorksheetItemId(waVVO.getWorksheetItemId());
        waVDO.setAccessionNumber(waVVO.getAccessionNumber());
        waVDO.setAnalysisId(waVVO.getAnalysisId());
        waVDO.setQcLotId(waVVO.getQcLotId());
        waVDO.setWorksheetAnalysisId(waVVO.getWorksheetAnalysisId());
        waVDO.setQcSystemUserId(waVVO.getQcSystemUserId());
        waVDO.setQcStartedDate(DataBaseUtil.toYM(waVVO.getQcStartedDate()));
        waVDO.setIsFromOther(waVVO.getIsFromOther());
        waVDO.setWorksheetId(waVVO.getWorksheetId());
        waVDO.setDescription(waVVO.getDescription());
        waVDO.setTestId(waVVO.getTestId());
        waVDO.setTestName(waVVO.getTestName());
        waVDO.setMethodName(waVVO.getMethodName());
        waVDO.setUnitOfMeasureId(waVVO.getUnitOfMeasureId());
        waVDO.setUnitOfMeasure(waVVO.getUnitOfMeasure());
        waVDO.setStatusId(waVVO.getStatusId());
        waVDO.setCollectionDate(DataBaseUtil.toYD(waVVO.getCollectionDate()));
        waVDO.setReceivedDate(DataBaseUtil.toYM(waVVO.getReceivedDate()));

        if (waVDO.getAnalysisId() != null) {
            //
            // Compute and set the number of days until the analysis is 
            // due to be completed based on when the sample was received,
            // what the tests average turnaround time is, and whether the
            // client requested a priority number of days.
            //
            if (waVVO.getPriority() != null)
                waVDO.setDueDays(DataBaseUtil.getDueDays(waVVO.getReceivedDate(), waVVO.getPriority()));
            else
                waVDO.setDueDays(DataBaseUtil.getDueDays(waVVO.getReceivedDate(), waVVO.getTimeTaAverage()));
            
            //
            // Compute and set the expiration date on the analysis based
            // on the collection date and the tests definition of holding
            // hours.
            //
            waVDO.setExpireDate(DataBaseUtil.getExpireDate(waVVO.getCollectionDate(),
                                                           waVVO.getCollectionTime(),
                                                           waVVO.getTimeHolding()));
        }
    }
}
