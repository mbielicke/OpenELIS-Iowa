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
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.FlushModeType;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.jboss.ejb3.annotation.SecurityDomain;
import org.openelis.domain.AnalysisDO;
import org.openelis.domain.AnalysisViewDO;
import org.openelis.domain.MCLViolationReportVO;
import org.openelis.domain.SDWISUnloadReportVO;
import org.openelis.domain.SampleItemDO;
import org.openelis.entity.Analysis;
import org.openelis.gwt.common.DataBaseUtil;
import org.openelis.gwt.common.DatabaseException;
import org.openelis.gwt.common.Datetime;
import org.openelis.gwt.common.FormErrorException;
import org.openelis.gwt.common.FormErrorWarning;
import org.openelis.gwt.common.NotFoundException;
import org.openelis.gwt.common.ValidationErrorsList;
import org.openelis.local.AnalysisLocal;
import org.openelis.manager.TestManager;
import org.openelis.remote.AnalysisRemote;

@Stateless
@SecurityDomain("openelis")
public class AnalysisBean implements AnalysisLocal, AnalysisRemote {

    @PersistenceContext(unitName = "openelis")
    private EntityManager manager;

    public ArrayList<AnalysisViewDO> fetchBySampleId(Integer sampleId) throws Exception {
        List returnList;
        Query query;

        query = manager.createNamedQuery("Analysis.FetchBySampleId");
        query.setParameter("id", sampleId);

        returnList = query.getResultList();
        if (returnList.size() == 0)
            throw new NotFoundException();

        return DataBaseUtil.toArrayList(returnList);
    }

    public ArrayList<SDWISUnloadReportVO> fetchBySampleIdForSDWISUnloadReport(Integer sampleId) throws Exception {
        List returnList;
        Query query;

        query = manager.createNamedQuery("Analysis.FetchBySampleIdForSDWISUnload");
        query.setParameter("id", sampleId);

        returnList = query.getResultList();
        if (returnList.size() == 0)
            throw new NotFoundException();

        return DataBaseUtil.toArrayList(returnList);
    }

    public ArrayList<AnalysisViewDO> fetchBySampleItemId(Integer sampleItemId) throws Exception {
        List returnList;
        Query query;

        query = manager.createNamedQuery("Analysis.FetchBySampleItemId");
        query.setParameter("id", sampleItemId);

        returnList = query.getResultList();
        if (returnList.size() == 0)
            throw new NotFoundException();

        return DataBaseUtil.toArrayList(returnList);
    }

    public ArrayList<AnalysisViewDO> fetchBySampleItemIds(ArrayList<Integer> sampleItemIds) {
        Query query;

        query = manager.createNamedQuery("Analysis.FetchBySampleItemIds");
        query.setParameter("ids", sampleItemIds);

        return DataBaseUtil.toArrayList(query.getResultList());
    }

    public AnalysisViewDO fetchById(Integer id) throws Exception {
        Query query;
        AnalysisViewDO data;

        query = manager.createNamedQuery("Analysis.FetchById");
        query.setParameter("id", id);
        try {
            data = (AnalysisViewDO)query.getSingleResult();
        } catch (NoResultException e) {
            throw new NotFoundException();
        } catch (Exception e) {
            throw new DatabaseException(e);
        }
        return data;
    }

    public ArrayList<MCLViolationReportVO> fetchForMCLViolationReport(Date startDate,
                                                                      Date endDate) throws Exception {
        List<MCLViolationReportVO> list;
        Query query;

        query = manager.createNamedQuery("Analysis.FetchForMCLViolation");
        query.setParameter("startDate", startDate);
        query.setParameter("endDate", endDate);

        list = query.getResultList();
        if (list.isEmpty())
            throw new NotFoundException();

        return DataBaseUtil.toArrayList(list);
    }

    public AnalysisDO add(AnalysisDO data) throws Exception {
        Analysis entity;

        manager.setFlushMode(FlushModeType.COMMIT);

        entity = new Analysis();
        entity.setSampleItemId(data.getSampleItemId());
        entity.setRevision(data.getRevision());
        entity.setTestId(data.getTestId());
        entity.setSectionId(data.getSectionId());
        entity.setPreAnalysisId(data.getPreAnalysisId());
        entity.setParentAnalysisId(data.getParentAnalysisId());
        entity.setParentResultId(data.getParentResultId());
        entity.setIsReportable(data.getIsReportable());
        entity.setUnitOfMeasureId(data.getUnitOfMeasureId());
        entity.setStatusId(data.getStatusId());
        entity.setAvailableDate(data.getAvailableDate());
        entity.setStartedDate(data.getStartedDate());
        entity.setCompletedDate(data.getCompletedDate());
        entity.setReleasedDate(data.getReleasedDate());
        entity.setPrintedDate(data.getPrintedDate());

        manager.persist(entity);
        data.setId(entity.getId());

        return data;
    }

    public AnalysisDO update(AnalysisDO data) throws Exception {
        Analysis entity;

        if ( !data.isChanged())
            return data;

        manager.setFlushMode(FlushModeType.COMMIT);

        entity = manager.find(Analysis.class, data.getId());
        entity.setSampleItemId(data.getSampleItemId());
        entity.setRevision(data.getRevision());
        entity.setTestId(data.getTestId());
        entity.setSectionId(data.getSectionId());
        entity.setPreAnalysisId(data.getPreAnalysisId());
        entity.setParentAnalysisId(data.getParentAnalysisId());
        entity.setParentResultId(data.getParentResultId());
        entity.setIsReportable(data.getIsReportable());
        entity.setUnitOfMeasureId(data.getUnitOfMeasureId());
        entity.setStatusId(data.getStatusId());
        entity.setAvailableDate(data.getAvailableDate());
        entity.setStartedDate(data.getStartedDate());
        entity.setCompletedDate(data.getCompletedDate());
        entity.setReleasedDate(data.getReleasedDate());
        entity.setPrintedDate(data.getPrintedDate());

        return data;
    }

    public void updatePrintedDate(Set<Integer> ids, Datetime printedDate) throws Exception {
        int i;
        Query query;
        ArrayList<Integer> queryList;
        Iterator<Integer> iter;

        manager.setFlushMode(FlushModeType.COMMIT);
        query = manager.createNamedQuery("Analysis.UpdatePrintedDateByIds");
        query.setParameter("printedDate", printedDate.getDate());
        if (ids.size() <= 1000) {
            query.setParameter("ids", ids);
            query.executeUpdate();
        } else {
            queryList = new ArrayList<Integer>();
            iter = ids.iterator();
            while (iter.hasNext()) {
                i = 0;
                queryList.clear();
                while (iter.hasNext() && i++ < 1000)
                    queryList.add(iter.next());
                query.setParameter("ids", queryList);
                query.executeUpdate();
            }
        }
    }

    public void updatePrintedDate(Integer id, Datetime timeStamp) throws Exception {
        Analysis entity;

        manager.setFlushMode(FlushModeType.COMMIT);

        entity = manager.find(Analysis.class, id);
        entity.setPrintedDate(timeStamp);
    }

    public void delete(AnalysisDO data) throws Exception {
        Analysis entity;
        manager.setFlushMode(FlushModeType.COMMIT);

        entity = manager.find(Analysis.class, data.getId());

        if (entity != null)
            manager.remove(entity);
    }

    public void validate(AnalysisDO data, TestManager tm, Integer accession,
                         SampleItemDO item, boolean ignoreWarning) throws Exception {
        Integer sequence;
        String test, method;
        ValidationErrorsList e;

        e = new ValidationErrorsList();
        test = null;
        method = null;
        sequence = null;

        if (item != null)
            sequence = item.getItemSequence();

        if (tm != null) {
            test = tm.getTest().getName();
            method = tm.getTest().getMethodName();

            /*
             * don't allow missing unit if the test definition does not have any
             * empty unit
             */
            if (data.getUnitOfMeasureId() == null && tm.getSampleTypes().hasEmptyUnit())
                e.add(new FormErrorException("sample.analysisUnitRequired", accession, sequence,
                                             test, method));
            /*
             * validate unit & sample type
             */
            if (data.getUnitOfMeasureId() != null && item != null && !ignoreWarning && 
                !tm.getSampleTypes().hasUnit(data.getUnitOfMeasureId(),
                                             item.getTypeOfSampleId()))
                e.add(new FormErrorWarning("sample.analysisUnitInvalid", accession, sequence,
                                           test, method));
        } else {
            test = null;
            method = null;
        }
        if (data.getTestId() == null)
            e.add(new FormErrorException("sample.analysisTestIdMissing", accession, sequence));

        if (data.getSectionId() == null)
            e.add(new FormErrorException("sample.analysisSectionIdMissing", accession, 
                                         sequence, test, method));

        if (data.getStartedDate() != null && data.getCompletedDate() != null &&
            data.getStartedDate().compareTo(data.getCompletedDate()) == 1)
            e.add(new FormErrorException("sample.startedDateInvalidError", accession,
                                         sequence, test, method));
        if (data.getCompletedDate() != null && data.getReleasedDate() != null &&
            data.getCompletedDate().compareTo(data.getReleasedDate()) == 1)
            e.add(new FormErrorException("sample.completedDateInvalidError", accession,
                                         sequence, test, method));
        if (e.size() > 0)
            throw e;
    }
}
