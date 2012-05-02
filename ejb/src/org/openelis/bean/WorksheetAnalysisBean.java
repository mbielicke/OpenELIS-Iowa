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

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.FlushModeType;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.jboss.ejb3.annotation.SecurityDomain;
import org.openelis.domain.QcChartResultVO;
import org.openelis.domain.WorksheetAnalysisDO;
import org.openelis.domain.WorksheetCacheVO;
import org.openelis.entity.WorksheetAnalysis;
import org.openelis.gwt.common.DataBaseUtil;
import org.openelis.gwt.common.DatabaseException;
import org.openelis.gwt.common.FieldErrorException;
import org.openelis.gwt.common.NotFoundException;
import org.openelis.gwt.common.SystemUserVO;
import org.openelis.gwt.common.ValidationErrorsList;
import org.openelis.local.DictionaryLocal;
import org.openelis.local.UserCacheLocal;
import org.openelis.local.WorksheetAnalysisLocal;
import org.openelis.meta.WorksheetCompletionMeta;

@Stateless
@SecurityDomain("openelis")
public class WorksheetAnalysisBean implements WorksheetAnalysisLocal {

    @EJB
    private DictionaryLocal      dictionary;

    @EJB
    private UserCacheLocal       userCache;

    private static Integer       workingId;

    @PostConstruct
    public void init() {
        if (workingId == null) {
            try {
                workingId = dictionary.fetchBySystemName("worksheet_working").getId();
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
    }

    @PersistenceContext(unitName = "openelis")
    private EntityManager manager;

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

    public ArrayList<WorksheetCacheVO> fetchByWorking() throws Exception {
        Query query;
        List<WorksheetCacheVO> list;
        SystemUserVO user;

        query = manager.createNamedQuery("WorksheetAnalysis.FetchByWorksheetStatusId");
        query.setParameter("statusId", workingId);
        list = query.getResultList();

        for (WorksheetCacheVO data : list) {
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
        Query query;
        query = manager.createNamedQuery("WorksheetAnalysis.FetchByInstancesForQcChart");
        //query.setParameter("numInstance", numInstances);
        query.setParameter("qcName", qcName);
        query.setMaxResults(numInstances);
        return DataBaseUtil.toArrayList(query.getResultList());
    }

    public WorksheetAnalysisDO add(WorksheetAnalysisDO data) throws Exception {
        WorksheetAnalysis entity;

        manager.setFlushMode(FlushModeType.COMMIT);

        entity = new WorksheetAnalysis();
        entity.setWorksheetItemId(data.getWorksheetItemId());
        entity.setAccessionNumber(data.getAccessionNumber());
        entity.setAnalysisId(data.getAnalysisId());
        entity.setQcId(data.getQcId());
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
        entity.setQcId(data.getQcId());
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
        if (DataBaseUtil.isEmpty(data.getAnalysisId()) && DataBaseUtil.isEmpty(data.getQcId())) {
            list.add(new FieldErrorException("fieldRequiredException",
                                             WorksheetCompletionMeta.getWorksheetAnalysisAnalysisId()));
            list.add(new FieldErrorException("fieldRequiredException",
                                             WorksheetCompletionMeta.getWorksheetAnalysisQcId()));
        }

        if (list.size() > 0)
            throw list;
    }

    /*protected QcChartResultVO getQcChartResultVO(Object[] result) throws Exception {
        Integer sortOrderV1, sortOrderV2;
        Double plotValue;
        String value1, value2, systemName;
        QcChartResultVO vo;

        vo = null;
        sortOrderV2 = null;
        systemName = (String)result[7];
        if ("wf_rad1".equals(systemName)) {
            sortOrderV1 = dictionaryCache.getBySystemName(systemName + "_final_value")
                                         .getSortOrder();
            sortOrderV2 = dictionaryCache.getBySystemName(systemName + "_expected_value")
                                         .getSortOrder();
        } else if ("wf_envana2".equals(systemName)) {
            sortOrderV1 = dictionaryCache.getBySystemName(systemName + "_calculated_value")
                                         .getSortOrder();
            sortOrderV2 = dictionaryCache.getBySystemName(systemName + "_expected_value")
                                         .getSortOrder();
        } else {
            sortOrderV1 = dictionaryCache.getBySystemName(systemName + "_final_value")
                                         .getSortOrder();
        }
        value1 = (String)result[8 + sortOrderV1];
        plotValue = null;
        value2 = null;
        try {
        if (sortOrderV2 != null) {
            value2 = (String)result[8 + sortOrderV2];
            if (value1 != null && value2 != null)
                plotValue = new Double( (Double.parseDouble(value1) / Double.parseDouble(value2)) * 100);
        } else if (value1 != null) {
            plotValue = new Double(value1);
        }
        } catch(Exception e){
            Log.error(e);
        }

        /*
         * fill up the vo only for non null plotValues
         */
       /* if (plotValue != null) {
            vo = new QcChartResultVO();
            vo.setAccessionNumber((String)result[0]);
            vo.setLotNumber((String)result[1]);
            vo.setWorksheetCreatedDate(DataBaseUtil.toYM((Date)result[6]));
            vo.setAnalyteParameter((String)result[2]);
            vo.setValue1(value1);
            vo.setValue2(value2);
            vo.setPlotValue(plotValue);
        }
        return vo;
    }

    private void calculateStatistics(ArrayList<QcChartResultVO> list) {
        int i, numValue;
        double mean, sd, diff, sqDiffSum, sum, uWL, uCL, lWL, lCL;
        QcChartResultVO data;

        numValue = list.size();
        sum = 0.0;
        for (i = 0; i < numValue; i++ ) {
            data = list.get(i);
            sum = sum + data.getPlotValue();
        }
        mean = sum / numValue;
        sqDiffSum = 0.0;
        for (i = 0; i < numValue; i++ ) {
            data = list.get(i);
            diff = data.getPlotValue() - mean;
            sqDiffSum += diff * diff;
        }
        sd = Math.sqrt(sqDiffSum / (numValue - 1));
        uWL = mean + 2 * sd;
        uCL = mean + 3 * sd;
        lWL = mean - 2 * sd;
        lCL = mean - 3 * sd;
        for (i = 0; i < numValue; i++ ) {
            data = list.get(i);
            data.setMean(mean);
            data.setUWL(uWL);
            data.setUCL(uCL);
            data.setLWL(lWL);
            data.setLCL(lCL);
        }
    }*/
}
