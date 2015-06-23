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

import static org.openelis.manager.SampleManager1Accessor.*;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.FlushModeType;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.jboss.security.annotation.SecurityDomain;
import org.openelis.constants.Messages;
import org.openelis.domain.AnalysisDO;
import org.openelis.domain.AnalysisViewDO;
import org.openelis.domain.AnalysisViewVO;
import org.openelis.domain.IdAccessionVO;
import org.openelis.domain.MCLViolationReportVO;
import org.openelis.domain.SDWISUnloadReportVO;
import org.openelis.domain.SampleItemDO;
import org.openelis.entity.Analysis;
import org.openelis.manager.SampleManager1;
import org.openelis.manager.TestManager;
import org.openelis.meta.SampleMeta;
import org.openelis.ui.common.DataBaseUtil;
import org.openelis.ui.common.DatabaseException;
import org.openelis.ui.common.Datetime;
import org.openelis.ui.common.FormErrorCaution;
import org.openelis.ui.common.FormErrorException;
import org.openelis.ui.common.FormErrorWarning;
import org.openelis.ui.common.LastPageException;
import org.openelis.ui.common.NotFoundException;
import org.openelis.ui.common.ValidationErrorsList;
import org.openelis.ui.common.data.QueryData;
import org.openelis.util.QueryBuilderV2;

@Stateless
@SecurityDomain("openelis")
public class AnalysisBean {

    @PersistenceContext(unitName = "openelis")
    private EntityManager           manager;

    private static final SampleMeta meta = new SampleMeta();

    private static final Logger     log  = Logger.getLogger("openelis");

    @SuppressWarnings("unchecked")
    public ArrayList<IdAccessionVO> query(ArrayList<QueryData> fields, int first, int max) throws Exception {
        Query query;
        QueryBuilderV2 builder;
        List list;

        builder = new QueryBuilderV2();
        builder.setMeta(meta);
        builder.setSelect("distinct new org.openelis.domain.IdAccessionVO(" +
                          SampleMeta.getAnalysisId() + ", " + SampleMeta.getAccessionNumber() +
                          ") ");
        builder.constructWhere(fields);
        /*
         * this is done to make sure that some analyses belonging to a sample
         * don't get excluded from the returned list; this can happen if the
         * difference between the ids of the analyses of the same sample is
         * greater than the max number of results
         */
        builder.setOrderBy(SampleMeta.getAccessionNumber());

        /*
         * the following is done to provide necessary links between the aliases
         * for sample and analysis in the query, so that it's well-formed even
         * if the passed list doesn't contain any analysis field, since only
         * analysis id is getting fetched
         */
        builder.addWhere(SampleMeta.getItemId() + "=" + SampleMeta.getAnalysisSampleItemId());

        query = manager.createQuery(builder.getEJBQL());
        query.setMaxResults(first + max);
        builder.setQueryParams(query, fields);

        list = query.getResultList();
        if (list.isEmpty())
            throw new NotFoundException();
        list = (ArrayList<IdAccessionVO>)DataBaseUtil.subList(list, first, max);
        if (list == null)
            throw new LastPageException();

        return (ArrayList<IdAccessionVO>)list;
    }

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

    public ArrayList<MCLViolationReportVO> fetchForMCLViolationReport(Date startDate, Date endDate) throws Exception {
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

    public ArrayList<AnalysisViewVO> fetchByPatientId(Integer patientId) throws Exception {
        List<AnalysisViewVO> list;
        Query query;

        query = manager.createNamedQuery("AnalysisView.FetchByPatientId");
        query.setParameter("patientId", patientId);

        list = query.getResultList();
        if (list.isEmpty())
            throw new NotFoundException();

        return DataBaseUtil.toArrayList(list);
    }

    public ArrayList<Object[]> fetchForTurnaroundWarningReport() throws Exception {
        Query query;

        query = manager.createNamedQuery("Analysis.FetchForTurnaroundWarningReport");

        return DataBaseUtil.toArrayList(query.getResultList());
    }

    public ArrayList<Object[]> fetchForTurnaroundMaximumReport() throws Exception {
        Query query;

        query = manager.createNamedQuery("Analysis.FetchForTurnaroundMaximumReport");

        return DataBaseUtil.toArrayList(query.getResultList());
    }

    public AnalysisDO add(AnalysisDO data) throws Exception {
        Analysis entity;

        manager.setFlushMode(FlushModeType.COMMIT);

        entity = new Analysis();
        entity.setSampleItemId(data.getSampleItemId());
        entity.setRevision(data.getRevision());
        entity.setTestId(data.getTestId());
        entity.setSectionId(data.getSectionId());
        entity.setPanelId(data.getPanelId());
        entity.setPreAnalysisId(data.getPreAnalysisId());
        entity.setParentAnalysisId(data.getParentAnalysisId());
        entity.setParentResultId(data.getParentResultId());
        entity.setTypeId(data.getTypeId());
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
        entity.setPanelId(data.getPanelId());
        entity.setPreAnalysisId(data.getPreAnalysisId());
        entity.setParentAnalysisId(data.getParentAnalysisId());
        entity.setParentResultId(data.getParentResultId());
        entity.setTypeId(data.getTypeId());
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

    public void validate(AnalysisDO data, TestManager tm, SampleManager1 sm, SampleItemDO item) throws Exception {
        Integer accession, sequence;
        String test, method;
        ValidationErrorsList e;
        Date now;
        Calendar ent;

        e = new ValidationErrorsList();
        test = null;
        method = null;
        sequence = null;

        /*
         * for display
         */
        accession = getSample(sm).getAccessionNumber();
        if (accession == null)
            accession = 0;

        if (item != null)
            sequence = item.getItemSequence();

        if (tm != null) {
            test = tm.getTest().getName();
            method = tm.getTest().getMethodName();

            /*
             * don't allow missing unit if the test definition does not have any
             * empty unit; also, don't allow the sample item to have a sample
             * type that isn't present in the test definition
             */
            if (data.getUnitOfMeasureId() == null) {
                if ( !tm.getSampleTypes().hasEmptyUnit())
                    e.add(new FormErrorException(Messages.get()
                                                         .analysis_unitRequiredException(accession,
                                                                                         sequence,
                                                                                         test,
                                                                                         method)));
                /*
                 * this check is done only if the sample type is not null, to
                 * avoid showing the user two errors for the same problem;
                 * null sample type is part of sample item validation
                 */
                if (item != null && item.getTypeOfSampleId() != null &&
                    !tm.getSampleTypes().hasType(item.getTypeOfSampleId()))
                    e.add(new FormErrorWarning(Messages.get()
                                                       .analysis_sampleTypeInvalidWarning(accession,
                                                                                          sequence,
                                                                                          test,
                                                                                          method)));
            }
            
            /*
             * validate sample type & unit 
             */
            if (data.getUnitOfMeasureId() != null && item != null &&
                !tm.getSampleTypes().hasUnit(data.getUnitOfMeasureId(), item.getTypeOfSampleId()))
                e.add(new FormErrorWarning(Messages.get().analysis_unitInvalidWarning(accession,
                                                                                      sequence,
                                                                                      test,
                                                                                      method)));
        } else {
            test = null;
            method = null;
        }

        if (data.getTestId() == null)
            e.add(new FormErrorException(Messages.get().analysis_testIdMissingException(accession,
                                                                                        sequence)));

        if (data.getSectionId() == null)
            e.add(new FormErrorException(Messages.get()
                                                 .analysis_sectionIdMissingException(accession,
                                                                                     sequence,
                                                                                     test,
                                                                                     method)));

        now = new Date();
        if (data.getStartedDate() != null) {
            /*
             * started date can't be after completed date
             */
            if (data.getCompletedDate() != null &&
                data.getStartedDate().after(data.getCompletedDate()))
                e.add(new FormErrorException(Messages.get()
                                                     .analysis_startedDateAfterCompletedException(accession,
                                                                                                  sequence,
                                                                                                  test,
                                                                                                  method)));

            /*
             * if the started date is more than 3 days before entered date and
             * also before available date, then that could be a problem
             */
            if (data.getAvailableDate() != null &&
                data.getStartedDate().before(data.getAvailableDate())) {
                ent = Calendar.getInstance();
                ent.setTime(getSample(sm).getEnteredDate().getDate());
                ent.add(Calendar.DATE, -3);
                if (data.getStartedDate().before(ent.getTime()))
                    e.add(new FormErrorCaution(Messages.get()
                                                       .analysis_startedDateBeforeAvailableCaution(accession,
                                                                                                   sequence,
                                                                                                   test,
                                                                                                   method)));
            }

            /*
             * started date can't be in the future
             */
            if (data.getStartedDate().after(now)) {
                e.add(new FormErrorException(Messages.get()
                                                     .analysis_startedDateInFutureException(accession,
                                                                                            sequence,
                                                                                            test,
                                                                                            method)));
                log.log(Level.SEVERE, "Future Started date " +
                                      data.getStartedDate().getDate().getTime() + " Now " +
                                      now.getTime());
            }
        }

        if (data.getCompletedDate() != null) {
            /*
             * completed date can't be after released date
             */
            if (data.getReleasedDate() != null &&
                data.getCompletedDate().after(data.getReleasedDate()))
                e.add(new FormErrorException(Messages.get()
                                                     .analysis_completedDateAfterReleasedException(accession,
                                                                                                   sequence,
                                                                                                   test,
                                                                                                   method)));
            /*
             * completed date can't be in the future
             */
            if (data.getCompletedDate().after(now)) {
                e.add(new FormErrorException(Messages.get()
                                                     .analysis_completedDateInFutureException(accession,
                                                                                              sequence,
                                                                                              test,
                                                                                              method)));
                log.log(Level.SEVERE, "Future Completed date " +
                                      data.getCompletedDate().getDate().getTime() + " Now " +
                                      now.getTime());
            }
        }

        if (e.size() > 0)
            throw e;
    }
}