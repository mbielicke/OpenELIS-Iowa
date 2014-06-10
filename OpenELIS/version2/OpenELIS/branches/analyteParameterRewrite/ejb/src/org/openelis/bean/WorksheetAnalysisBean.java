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
import org.openelis.domain.IdVO;
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
    private EntityManager       manager;

    @EJB
    private UserCacheBean       userCache;

    @SuppressWarnings("unchecked")
    public ArrayList<WorksheetAnalysisViewDO> fetchByWorksheetId(Integer id) throws Exception {
        int i;
        List<WorksheetAnalysisViewDO> returnList;
        List<WorksheetAnalysisViewVO> list;
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
            waVDO = copyViewToDO(waVVO);
            returnList.add(waVDO);
        }
        
        return DataBaseUtil.toArrayList(returnList);
    }

    @SuppressWarnings("unchecked")
    public ArrayList<WorksheetAnalysisViewDO> fetchByWorksheetIds(ArrayList<Integer> ids) throws Exception {
        int i;
        List<WorksheetAnalysisViewDO> returnList;
        List<WorksheetAnalysisViewVO> list;
        Query query;
        WorksheetAnalysisViewDO waVDO;
        WorksheetAnalysisViewVO waVVO;

        query = manager.createNamedQuery("WorksheetAnalysisView.FetchByWorksheetIds");
        query.setParameter("worksheetIds", ids);

        list = query.getResultList();
//        if (list.isEmpty())
//            throw new NotFoundException();

        returnList = new ArrayList<WorksheetAnalysisViewDO>();
        for (i = 0; i < list.size(); i++) {
            waVVO = (WorksheetAnalysisViewVO) list.get(i);
            waVDO = copyViewToDO(waVVO);
            returnList.add(waVDO);
        }
        
        return DataBaseUtil.toArrayList(returnList);
    }

    @SuppressWarnings("unchecked")
    public ArrayList<WorksheetAnalysisDO> fetchByWorksheetItemId(Integer id) throws Exception {
        Query query;
        List<WorksheetAnalysisDO> list;

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
        List<WorksheetAnalysisDO> list;

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
        List<WorksheetAnalysisDO> list;

        query = manager.createNamedQuery("WorksheetAnalysis.FetchByQcLotId");
        query.setParameter("id", id);

        list = query.getResultList();
        if (list.isEmpty())
            throw new NotFoundException();

        return DataBaseUtil.toArrayList(list);
    }

    @SuppressWarnings("unchecked")
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
    
    @SuppressWarnings("unchecked")
    public ArrayList<QcChartResultVO> fetchByDateForQcChart(Date dateFrom, Date dateTo, String qcName, Integer qcLocation) throws Exception {
        Query query;
        query = manager.createNamedQuery("WorksheetAnalysis.FetchByDateForQcChart");
        query.setParameter("startedDate", dateFrom);
        query.setParameter("endDate", dateTo);
        query.setParameter("qcName", qcName);
        query.setParameter("qcLocation", qcLocation);
        
        return DataBaseUtil.toArrayList(query.getResultList());
    }
    
    @SuppressWarnings("unchecked")
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
        entity.setSystemUsers(data.getSystemUsers());
        entity.setStartedDate(data.getStartedDate());
        entity.setCompletedDate(data.getCompletedDate());
        entity.setFromOtherId(data.getFromOtherId());
        entity.setChangeFlagsId(data.getChangeFlagsId());

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
        entity.setWorksheetItemId(data.getWorksheetItemId());
        entity.setAccessionNumber(data.getAccessionNumber());
        entity.setAnalysisId(data.getAnalysisId());
        entity.setQcLotId(data.getQcLotId());
        entity.setWorksheetAnalysisId(data.getWorksheetAnalysisId());
        entity.setSystemUsers(data.getSystemUsers());
        entity.setStartedDate(data.getStartedDate());
        entity.setCompletedDate(data.getCompletedDate());
        entity.setFromOtherId(data.getFromOtherId());
        entity.setChangeFlagsId(data.getChangeFlagsId());

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
    
    private WorksheetAnalysisViewDO copyViewToDO(WorksheetAnalysisViewVO waVVO) {
        Date collectionDate, completedDate, receivedDate, startedDate;
        WorksheetAnalysisViewDO waVDO;

        collectionDate = null;
        completedDate = null;
        startedDate = null;
        receivedDate = null;
        if (waVVO.getCollectionDate() != null)
            collectionDate = DataBaseUtil.toYD(waVVO.getCollectionDate()).getDate();
        if (waVVO.getStartedDate() != null)
            startedDate = DataBaseUtil.toYM(waVVO.getStartedDate()).getDate();
        if (waVVO.getCompletedDate() != null)
            completedDate = DataBaseUtil.toYM(waVVO.getCompletedDate()).getDate();
        if (waVVO.getReceivedDate() != null)
            receivedDate = DataBaseUtil.toYM(waVVO.getReceivedDate()).getDate();

        waVDO = new WorksheetAnalysisViewDO(waVVO.getId(), waVVO.getWorksheetItemId(),
                                            waVVO.getWorksheetId(), waVVO.getAccessionNumber(),
                                            waVVO.getAnalysisId(), waVVO.getQcLotId(),
                                            waVVO.getQcId(), waVVO.getWorksheetAnalysisId(),
                                            waVVO.getSystemUsers(), startedDate,
                                            completedDate, waVVO.getFromOtherId(),
                                            waVVO.getChangeFlagsId(), waVVO.getDescription(),
                                            waVVO.getTestId(), waVVO.getTestName(),
                                            waVVO.getMethodName(), waVVO.getSectionName(),
                                            waVVO.getUnitOfMeasureId(), waVVO.getUnitOfMeasure(),
                                            waVVO.getStatusId(), collectionDate,
                                            receivedDate, (Integer)null, (Date)null);
        if (waVVO.getAnalysisId() != null) {
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
        
        return waVDO;
    }
}