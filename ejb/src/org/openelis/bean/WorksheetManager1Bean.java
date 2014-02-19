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

import static org.openelis.manager.WorksheetManager1Accessor.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.Resource;
import javax.annotation.security.RolesAllowed;
import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;

import org.jboss.security.annotation.SecurityDomain;
import org.openelis.constants.Messages;
import org.openelis.domain.AnalysisViewDO;
import org.openelis.domain.CategoryCacheVO;
import org.openelis.domain.Constants;
import org.openelis.domain.DataObject;
import org.openelis.domain.DictionaryDO;
import org.openelis.domain.IdNameVO;
import org.openelis.domain.NoteViewDO;
import org.openelis.domain.QcAnalyteViewDO;
import org.openelis.domain.QcLotViewDO;
import org.openelis.domain.ResultViewDO;
import org.openelis.domain.TestAnalyteViewDO;
import org.openelis.domain.TestWorksheetAnalyteViewDO;
import org.openelis.domain.TestWorksheetDO;
import org.openelis.domain.TestWorksheetItemDO;
import org.openelis.domain.WorksheetAnalysisViewDO;
import org.openelis.domain.WorksheetItemDO;
import org.openelis.domain.WorksheetQcChoiceVO;
import org.openelis.domain.WorksheetQcResultViewDO;
import org.openelis.domain.WorksheetResultViewDO;
import org.openelis.domain.WorksheetViewDO;
import org.openelis.manager.Preferences;
import org.openelis.manager.SampleManager1;
import org.openelis.manager.SampleManager1Accessor;
import org.openelis.manager.TestWorksheetManager;
import org.openelis.manager.WorksheetManager1;
import org.openelis.manager.WorksheetManager1Accessor;
import org.openelis.ui.common.DataBaseUtil;
import org.openelis.ui.common.Datetime;
import org.openelis.ui.common.FormErrorException;
import org.openelis.ui.common.InconsistencyException;
import org.openelis.ui.common.NotFoundException;
import org.openelis.ui.common.SectionPermission;
import org.openelis.ui.common.ValidationErrorsList;
import org.openelis.ui.common.data.QueryData;
import org.openelis.utils.User;

@Stateless
@SecurityDomain("openelis")
public class WorksheetManager1Bean {

    @Resource
    private SessionContext               ctx;
    
    @EJB
    private AnalysisHelperBean           aHelper;
    
    @EJB
    private CategoryCacheBean            category;
    
    @EJB
    private DictionaryCacheBean          dictionary;
    
    @EJB
    private LockBean                     lock;

    @EJB
    private NoteBean                     note;
    
    @EJB
    private QcAnalyteBean                qcAnalyte;
    
    @EJB
    private QcLotBean                    qcLot;
    
    @EJB
    private ResultBean                   result;
    
    @EJB
    private SampleManager1Bean           sampleMan;
    
    @EJB
    private TestAnalyteBean              testAnalyte;
    
    @EJB
    private TestWorksheetAnalyteBean     twAnalyte;
    
    @EJB
    private UserCacheBean                userCache;

    @EJB
    private WorksheetAnalysisBean        analysis;

    @EJB
    private WorksheetBean                worksheet;

    @EJB
    private WorksheetItemBean            item;

    @EJB
    private WorksheetQcResultBean        wqResult;

    @EJB
    private WorksheetResultBean          wResult;

    private static final Logger          log = Logger.getLogger("openelis");

    /**
     * Returns a new instance of worksheet manager with pre-initailized worksheet
     * and other structures.
     */
    public WorksheetManager1 getInstance() throws Exception {
        WorksheetManager1 wm;
        WorksheetViewDO w;
        Datetime now;

        // worksheet
        now = Datetime.getInstance(Datetime.YEAR, Datetime.MINUTE);
        wm = new WorksheetManager1();

        w = new WorksheetViewDO();
        w.setCreatedDate(now);
        w.setSystemUserId(userCache.getId());
        w.setStatusId(Constants.dictionary().WORKSHEET_WORKING);
        w.setSystemUser(User.getName(ctx));

        setWorksheet(wm, w);

        return wm;
    }

    /**
     * Returns a worksheet manager for specified primary id and requested load
     * elements
     */
    public WorksheetManager1 fetchById(Integer worksheetId, WorksheetManager1.Load... elements) throws Exception {
        ArrayList<Integer> ids;
        ArrayList<WorksheetManager1> wms;

        ids = new ArrayList<Integer>(1);
        ids.add(worksheetId);
        wms = fetchByIds(ids, elements);
        return wms.size() == 0 ? null : wms.get(0);
    }

    /**
     * Returns worksheet managers for specified primary ids and requested load
     * elements
     */
    public ArrayList<WorksheetManager1> fetchByIds(ArrayList<Integer> worksheetIds,
                                                   WorksheetManager1.Load... elements) throws Exception {
        WorksheetManager1 wm;
        ArrayList<Integer> ids1, ids2;
        ArrayList<WorksheetManager1> wms;
        HashMap<Integer, WorksheetManager1> map1, map2;
        EnumSet<WorksheetManager1.Load> el;

        /*
         * to reduce database select calls, we are going to fetch everything for
         * a given select and unroll through loops.
         */
        wms = new ArrayList<WorksheetManager1>();
        if (elements != null && elements.length > 0)
            el = EnumSet.copyOf(Arrays.asList(elements));
        else
            el = EnumSet.noneOf(WorksheetManager1.Load.class);

        /*
         * build level 1, everything is based on worksheet ids
         */
        ids1 = new ArrayList<Integer>();
        map1 = new HashMap<Integer, WorksheetManager1>();

        for (WorksheetViewDO data : worksheet.fetchByIds(worksheetIds)) {
            wm = new WorksheetManager1();
            setWorksheet(wm, data);
            wms.add(wm);

            ids1.add(data.getId()); // for fetch
            map1.put(data.getId(), wm); // for linking
        }

        if (!ids1.isEmpty()) {
            if (el.contains(WorksheetManager1.Load.NOTE)) {
                for (NoteViewDO data : note.fetchByIds(ids1, Constants.table().WORKSHEET)) {
                    wm = map1.get(data.getReferenceId());
                    addNote(wm, data);
                }
            }
    
            if (el.contains(WorksheetManager1.Load.DETAIL)) {
                /*
                 * build level 2, everything is based on item ids
                 */
                for (WorksheetItemDO data : item.fetchByWorksheetIds(ids1)) {
                    wm = map1.get(data.getWorksheetId());
                    addItem(wm, data);
                }
        
                /*
                 * build level 3, everything is based on analysis ids
                 */
                ids2 = new ArrayList<Integer>();
                map2 = new HashMap<Integer, WorksheetManager1>();
                for (WorksheetAnalysisViewDO data : analysis.fetchByWorksheetIds(ids1)) {
                    wm = map1.get(data.getWorksheetId());
                    addAnalysis(wm, data);
                    if (!map2.containsKey(data.getId())) {
                        ids2.add(data.getId());
                        map2.put(data.getId(), wm);
                    }
                }
        
                if (!ids2.isEmpty()) {
                    for (WorksheetResultViewDO data : wResult.fetchByWorksheetAnalysisIds(ids2)) {
                        wm = map2.get(data.getWorksheetAnalysisId());
                        addResult(wm, data);
                    }
        
                    for (WorksheetQcResultViewDO data : wqResult.fetchByWorksheetAnalysisIds(ids2)) {
                        wm = map2.get(data.getWorksheetAnalysisId());
                        addQcResult(wm, data);
                    }
                }
            }
        }
        
        return wms;
    }

    /**
     * Returns a worksheet manager based on the specified query and requested load
     * elements
     */
    public ArrayList<WorksheetManager1> fetchByQuery(ArrayList<QueryData> fields, int first, int max,
                                                     WorksheetManager1.Load... elements) throws Exception {
        ArrayList<Integer> ids;

        ids = new ArrayList<Integer>();

        for (IdNameVO vo : worksheet.query(fields, first, max))
            ids.add(vo.getId());
        return fetchByIds(ids, elements);
    }

    /**
     * Returns a worksheet manager loaded with additional elements
     */
    public WorksheetManager1 fetchWith(WorksheetManager1 wm, WorksheetManager1.Load... elements) throws Exception {
        ArrayList<Integer> worksheetId, ids, ids2;
        EnumSet<WorksheetManager1.Load> el;

        if (elements != null)
            el = EnumSet.copyOf(Arrays.asList(elements));
        else
            return wm;

        /*
         * various lists for each worksheet
         */
        worksheetId = new ArrayList<Integer>(1);
        worksheetId.add(getWorksheet(wm).getId());
        if (el.contains(WorksheetManager1.Load.NOTE)) {
            setNotes(wm, null);
            for (NoteViewDO data : note.fetchByIds(worksheetId, Constants.table().WORKSHEET))
                addNote(wm, data);
        }

        if (el.contains(WorksheetManager1.Load.DETAIL)) {
            /*
             * build level 2, everything is based on item ids
             */
            ids = new ArrayList<Integer>();
            setItems(wm, null);
            for (WorksheetItemDO data : item.fetchByWorksheetIds(worksheetId)) {
                addItem(wm, data);
                ids.add(data.getId());
            }
    
            /*
             * build level 3, everything is based on analysis ids
             */
            ids2 = new ArrayList<Integer>();
            setAnalyses(wm, null);
            for (WorksheetAnalysisViewDO data : analysis.fetchByWorksheetIds(worksheetId)) {
                addAnalysis(wm, data);
                ids2.add(data.getId());
            }
    
            setResults(wm, null);
            for (WorksheetResultViewDO data : wResult.fetchByWorksheetAnalysisIds(ids2))
                addResult(wm, data);
            
            setQcResults(wm, null);
            for (WorksheetQcResultViewDO data : wqResult.fetchByWorksheetAnalysisIds(ids2))
                addQcResult(wm, data);
        }

        return wm;
    }

    /**
     * Returns a locked worksheet manager with specified worksheet id
     */
    @RolesAllowed("worksheet-update")
    public WorksheetManager1 fetchForUpdate(Integer worksheetId) throws Exception {
        ArrayList<Integer> ids;
        ArrayList<WorksheetManager1> wms;

        lock.lock(Constants.table().WORKSHEET, worksheetId);

        ids = new ArrayList<Integer>(1);
        ids.add(worksheetId);
        wms = fetchByIds(ids, WorksheetManager1.Load.DETAIL, WorksheetManager1.Load.NOTE);
        return wms.size() == 0 ? null : wms.get(0);
    }

    /**
     * Unlocks and returns a worksheet manager with specified worksheet id and
     * requested load elements
     */
    @RolesAllowed({"worksheet-add", "worksheet-update"})
    public WorksheetManager1 unlock(Integer worksheetId, WorksheetManager1.Load... elements) throws Exception {
        ArrayList<Integer> ids;
        ArrayList<WorksheetManager1> wms;

        lock.unlock(Constants.table().WORKSHEET, worksheetId);

        ids = new ArrayList<Integer>(1);
        ids.add(worksheetId);
        wms = fetchByIds(ids, elements);
        return wms.size() == 0 ? null : wms.get(0);
    }

    /**
     * Adds/updates the worksheet and related records in the database. The records
     * are validated before add/update and the worksheet record must have a lock
     * record if it has an id.
     */
    public WorksheetManager1 update(WorksheetManager1 wm) throws Exception {
        int i, dep, ldep;
        boolean failedRun, locked, nodep, unlock, voidRun;
        ArrayList<Integer> analyteIndexes, excludedIds;
        ArrayList<ResultViewDO> results;
        ArrayList<SampleManager1> sMans;
        ArrayList<TestAnalyteViewDO> analytes;
        ArrayList<WorksheetResultViewDO> wResults;
        Datetime createdDate, startedDate;
        Integer tmpid, id;
        HashMap<Integer, Integer> imap, amap;
        HashMap<Integer, ArrayList<Integer>> excludedMap;
        HashMap<Integer, ArrayList<ResultViewDO>> newResults;
        HashMap<Integer, ResultViewDO> updatedResults;
        HashMap<Integer, SampleManager1> sMansByAnalysisId;
        HashMap<Integer, String> testMethodNames;
        HashMap<Integer, WorksheetAnalysisViewDO> updatedAnalyses;
        HashSet<Integer> analysisIds, initAnalysisIds, updateAnalysisIds;
        ResultViewDO rVDO;
        SampleManager1 sManager;
        SectionPermission userPermission;
        StringBuffer description;
        TestAnalyteViewDO taVDO;
        WorksheetAnalysisViewDO waVDO;
        WorksheetItemDO itemDO;
        ValidationErrorsList errors;
        
        validate(wm);

        failedRun = false;
        locked = false;
        voidRun = false;
        if (getWorksheet(wm).getId() != null && getWorksheet(wm).getId() > 0) {
            lock.validateLock(Constants.table().WORKSHEET, getWorksheet(wm).getId());
            locked = true;
        }

        /*
         * the front code uses negative ids (temporary ids) to link worksheet items
         * and analysis, analysis and results. The negative ids are mapped to
         * actual database ids through several maps: imap for items, amap for
         * analysis.
         */
        imap = new HashMap<Integer, Integer>();
        amap = new HashMap<Integer, Integer>();

        /*
         * go through remove list and delete all the unwanted records
         */
        if (getRemoved(wm) != null) {
            for (DataObject data : getRemoved(wm)) {
                if (data instanceof WorksheetResultViewDO)
                    wResult.delete((WorksheetResultViewDO)data);
            }
            for (DataObject data : getRemoved(wm)) {
                if (data instanceof WorksheetQcResultViewDO)
                    wqResult.delete((WorksheetQcResultViewDO)data);
            }
            for (DataObject data : getRemoved(wm)) {
                if (data instanceof WorksheetAnalysisViewDO)
                    analysis.delete((WorksheetAnalysisViewDO)data);
            }
            for (DataObject data : getRemoved(wm)) {
                if (data instanceof WorksheetItemDO)
                    item.delete((WorksheetItemDO)data);
            }
            for (DataObject data : getRemoved(wm)) {
                if (data instanceof NoteViewDO)
                    note.delete((NoteViewDO)data);
            }
        }

        // add/update worksheet
        if (getWorksheet(wm).getId() == null || getWorksheet(wm).getId() < 0) {
            //
            // If the description is blank, set it to the list of tests/methods
            //
            if (getWorksheet(wm).getDescription() == null || getWorksheet(wm).getDescription().length() == 0) {
                description = new StringBuffer();
                testMethodNames = new HashMap<Integer, String>();
                for (WorksheetAnalysisViewDO data : getAnalyses(wm)) {
                    if (!testMethodNames.containsKey(data.getTestId()) && data.getTestId() != null)
                        testMethodNames.put(data.getTestId(), data.getTestName() + 
                                                              ", " +data.getMethodName());
                }
                for (String name : testMethodNames.values()) {
                    if (description.length() + 2 + name.length() > 60)
                        break;
                    if (description.length() > 0)
                        description.append("; ");
                    description.append(name);
                }
                getWorksheet(wm).setDescription(description.toString());
            }
            worksheet.add(getWorksheet(wm));
        } else {
            worksheet.update(getWorksheet(wm));
        }

        if (getNotes(wm) != null) {
            for (NoteViewDO data : getNotes(wm)) {
                if (data.getId() < 0) {
                    data.setReferenceTableId(Constants.table().WORKSHEET);
                    data.setReferenceId(getWorksheet(wm).getId());
                    note.add(data);
                } else {
                    note.update(data);
                }
            }
        }

        // add/update worksheet items. keep track of all the keys (pos or neg)
        for (WorksheetItemDO data : getItems(wm)) {
            if (data.getId() < 0) {
                tmpid = data.getId();
                data.setWorksheetId(getWorksheet(wm).getId());
                item.add(data);
            } else {
                tmpid = data.getId();
                item.update(data);
            }
            imap.put(tmpid, data.getId());
        }
        
        if (Constants.dictionary().WORKSHEET_FAILED.equals(getWorksheet(wm).getStatusId()))
            failedRun = true;
        else if (Constants.dictionary().WORKSHEET_VOID.equals(getWorksheet(wm).getStatusId()))
            voidRun = true;

        analysisIds = new HashSet<Integer>();
        initAnalysisIds = new HashSet<Integer>();
        updateAnalysisIds = new HashSet<Integer>();
        updatedAnalyses = new HashMap<Integer, WorksheetAnalysisViewDO>();
        for (WorksheetAnalysisViewDO data : getAnalyses(wm)) {
            if (data.getAnalysisId() != null) {
                if (data.getId() < 0 && data.getFromOtherId() == null) {
                    initAnalysisIds.add(data.getAnalysisId());
                    updatedAnalyses.put(data.getAnalysisId(), data);
                    updateAnalysisIds.add(data.getAnalysisId());
                } else if ((data.isChanged() || data.isStatusChanged() || data.isUnitChanged() ||
                            failedRun || voidRun) && !updatedAnalyses.containsKey(data.getAnalysisId())) {
                    updatedAnalyses.put(data.getAnalysisId(), data);
                    updateAnalysisIds.add(data.getAnalysisId());
                }
                analysisIds.add(data.getAnalysisId());
            }
        }

        newResults = new HashMap<Integer, ArrayList<ResultViewDO>>();
        updatedResults = new HashMap<Integer, ResultViewDO>();
        for (ResultViewDO res : wm.getModifiedResults()) {
            if (res.isChanged()) {
                if (res.getId() == null) {
                    results = newResults.get(res.getAnalysisId());
                    if (results == null) {
                        results = new ArrayList<ResultViewDO>();
                        newResults.put(res.getAnalysisId(), results);
                    }
                    res.setId(wm.getNextUID());
                    results.add(res);
                } else {
                    updatedResults.put(res.getId(), res);
                }
                updateAnalysisIds.add(res.getAnalysisId());
            }
        }

        createdDate = getWorksheet(wm).getCreatedDate();
        if (!updateAnalysisIds.isEmpty()) {
            sMans = sampleMan.fetchForUpdateByAnalyses(new ArrayList<Integer>(updateAnalysisIds),
                                                       SampleManager1.Load.ORGANIZATION,
                                                       SampleManager1.Load.QA,
                                                       SampleManager1.Load.SINGLERESULT);
            for (SampleManager1 sMan : sMans) {
                for (AnalysisViewDO ana : SampleManager1Accessor.getAnalyses(sMan)) {
                    if (initAnalysisIds.contains(ana.getId())) {
                        if (!Constants.dictionary().ANALYSIS_COMPLETED.equals(ana.getStatusId()) &&
                            !Constants.dictionary().ANALYSIS_ERROR_COMPLETED.equals(ana.getStatusId()))
                            aHelper.changeAnalysisStatus(sMan, ana.getId(), Constants.dictionary().ANALYSIS_INITIATED);
                        waVDO = updatedAnalyses.get(ana.getId());
                        if (DataBaseUtil.isDifferent(waVDO.getUnitOfMeasureId(),
                                                     ana.getUnitOfMeasureId()))
                            aHelper.changeAnalysisUnit(sMan, ana.getId(), waVDO.getUnitOfMeasureId());
                    } else if (updatedAnalyses.containsKey(ana.getId())) {
                        userPermission = userCache.getPermission().getSection(ana.getSectionName());
                        if (userPermission == null || !userPermission.hasCompletePermission())
                            throw new EJBException(Messages.get()
                                                           .analysis_noCompletePermission(DataBaseUtil.toString(sMan.getSample()
                                                                                                                    .getAccessionNumber()),
                                                                                          ana.getTestName(),
                                                                                          ana.getMethodName()));
                        
                        waVDO = updatedAnalyses.get(ana.getId());
                        if (DataBaseUtil.isDifferent(waVDO.getUnitOfMeasureId(),
                                                     ana.getUnitOfMeasureId()))
                            aHelper.changeAnalysisUnit(sMan, ana.getId(), waVDO.getUnitOfMeasureId());
                        if (failedRun && (Constants.dictionary().ANALYSIS_INITIATED.equals(ana.getStatusId()) ||
                                          Constants.dictionary().ANALYSIS_ERROR_INITIATED.equals(ana.getStatusId()))) {
                            aHelper.changeAnalysisStatus(sMan, ana.getId(), Constants.dictionary().ANALYSIS_REQUEUE);
                        } else if (voidRun && (Constants.dictionary().ANALYSIS_INITIATED.equals(ana.getStatusId()) ||
                                               Constants.dictionary().ANALYSIS_ERROR_INITIATED.equals(ana.getStatusId()))) {
                            startedDate = ana.getStartedDate();
                            if (startedDate == null || !startedDate.before(createdDate))
                                aHelper.changeAnalysisStatus(sMan, ana.getId(), Constants.dictionary().ANALYSIS_LOGGED_IN);
                        } else if (DataBaseUtil.isDifferent(waVDO.getStatusId(),
                                                            ana.getStatusId())) {
                            aHelper.changeAnalysisStatus(sMan, ana.getId(), waVDO.getStatusId());
                        }
                    }
                    results = newResults.get(ana.getId());
                    if (results != null && results.size() > 0) {
                        analytes = new ArrayList<TestAnalyteViewDO>();
                        analyteIndexes = new ArrayList<Integer>();
                        for (ResultViewDO res : results) {
                            taVDO = new TestAnalyteViewDO();
                            taVDO.setId(res.getTestAnalyteId());
                            taVDO.setRowGroup(res.getRowGroup());
                            taVDO.setAnalyteId(res.getAnalyteId());
                            taVDO.setIsReportable(res.getIsReportable());
                            taVDO.setAnalyteName(res.getAnalyte());
                            analytes.add(taVDO);
                            analyteIndexes.add(res.getSortOrder());
                        }
                        aHelper.addRowAnalytes(sMan, ana, analytes, analyteIndexes);
                    }
                }
                for (ResultViewDO res : SampleManager1Accessor.getResults(sMan)) {
                    if (updatedResults.containsKey(res.getId())) {
                        rVDO = updatedResults.get(res.getId());
                        if (DataBaseUtil.isDifferent(rVDO.getIsReportable(), res.getIsReportable()))
                            res.setIsReportable(rVDO.getIsReportable());
                    }
                }
            }
            
            try {
                sampleMan.update(sMans, true);
            } catch (Exception anyE) {
                unlock = true;
                if (anyE instanceof ValidationErrorsList) {
                    errors = (ValidationErrorsList)anyE;
                    if (!errors.hasErrors())
                        unlock = false;
                } else {
                    errors = new ValidationErrorsList();
                    errors.add(anyE);
                }
                if (unlock) {
                    try {
                        unlockSamples(sMans);
                    } catch (Exception anyE1) {
                        errors.add(anyE1);
                    }
                    ctx.setRollbackOnly();
                    throw errors;
                }
            }
        }

        sMansByAnalysisId = new HashMap<Integer, SampleManager1>();
        if (!analysisIds.isEmpty()) {
            sMans = sampleMan.fetchByAnalyses(new ArrayList<Integer>(analysisIds), SampleManager1.Load.SINGLERESULT);
            for (SampleManager1 sMan : sMans) {
                for (AnalysisViewDO aVDO : SampleManager1Accessor.getAnalyses(sMan))
                    sMansByAnalysisId.put(aVDO.getId(), sMan);
            }
        }

        /*
         * some worksheet analyses can be dependent on other worksheet analyses
         * for qc link. This code tries to resolve those dependencies by
         * alternating between adding/updating worksheet analysis until all the
         * records have been added/updated. The code also detects infinite loops
         * by ensuring every iteration resolves some dependency
         */
        dep = ldep = 0;
        excludedMap = new HashMap<Integer, ArrayList<Integer>>();
        results = new ArrayList<ResultViewDO>();
        wResults = new ArrayList<WorksheetResultViewDO>();
        do {
            ldep = dep;
            dep = 0;
            // add/update worksheet analysis
            for (WorksheetAnalysisViewDO data : getAnalyses(wm)) {
                nodep = true;

                if (data.getWorksheetAnalysisId() != null && data.getWorksheetAnalysisId() < 0) {
                    id = amap.get(data.getWorksheetAnalysisId());
                    if (id != null)
                        data.setWorksheetAnalysisId(id);
                    else
                        nodep = false;
                }

                if (nodep) {
                    if (data.getAnalysisId() != null) {
                        sManager = sMansByAnalysisId.get(data.getAnalysisId());
                        if (sManager != null) {
                            for (AnalysisViewDO aVDO : SampleManager1Accessor.getAnalyses(sManager)) {
                                if (data.getAnalysisId().equals(aVDO.getId())) {
                                    results.clear();
                                    wResults.clear();
                                    for (i = 0; i < sManager.result.count(aVDO); i++)
                                        results.add(sManager.result.get(aVDO, i, 0));
                                    for (i = 0; i < wm.result.count(data); i++)
                                        wResults.add(wm.result.get(data, i));
        
                                    excludedIds = excludedMap.get(aVDO.getTestId());
                                    if (excludedIds == null) {
                                        excludedIds = new ArrayList<Integer>();
                                        try {
                                            for (TestWorksheetAnalyteViewDO twaVDO : twAnalyte.fetchByTestId(aVDO.getTestId()))
                                                excludedIds.add(twaVDO.getTestAnalyteId());
                                        } catch (Exception anyE) {
                                            throw new Exception("Error loading excluded analytes: " + anyE.getMessage());
                                        }
                                        excludedMap.put(aVDO.getTestId(), excludedIds);
                                    }
                                    synchronizeResults(wm, data, results, wResults, excludedIds);
                                    break;
                                }
                            }
                        }
                    }
                    if (data.getId() < 0) {
                        tmpid = data.getId();
                        data.setWorksheetItemId(imap.get(data.getWorksheetItemId()));
                        //
                        // Rewrite temporary QC accession number
                        //
                        if (data.getQcLotId() != null) {
                            itemDO = wm.item.getById(data.getWorksheetItemId());
                            if (data.getAccessionNumber().startsWith("X."))
                                data.setAccessionNumber(DataBaseUtil.concatWithSeparator(getWorksheet(wm).getId(),
                                                                                         ".",
                                                                                         itemDO.getPosition()));
                            data.setQcStartedDate(Datetime.getInstance(Datetime.YEAR,
                                                                       Datetime.MINUTE));
                        }
                        analysis.add(data);
                        amap.put(tmpid, data.getId());
                        amap.put(data.getId(), data.getId());
                    } else if (!amap.containsKey(data.getId())) {
                        tmpid = data.getId();
                        analysis.update(data);
                        amap.put(tmpid, data.getId());
                    }
                } else {
                    dep++ ;
                }
            }
        } while (dep > 0 && ldep != dep);

        if (dep > 0 && ldep == dep)
            throw new InconsistencyException(Messages.get().worksheetAnalysisLinkError());

        // add/update results
        if (getResults(wm) != null) {
            for (WorksheetResultViewDO data : getResults(wm)) {
                if (data.getId() < 0) {
                    data.setWorksheetAnalysisId(amap.get(data.getWorksheetAnalysisId()));
                    wResult.add(data);
                } else {
                    wResult.update(data);
                }
            }
        }

        // add/update qc results
        if (getQcResults(wm) != null) {
            for (WorksheetQcResultViewDO data : getQcResults(wm)) {
                if (data.getId() < 0) {
                    data.setWorksheetAnalysisId(amap.get(data.getWorksheetAnalysisId()));
                    wqResult.add(data);
                } else {
                    wqResult.update(data);
                }
            }
        }

        if (locked)
            lock.unlock(Constants.table().WORKSHEET, getWorksheet(wm).getId());

        return wm;
    }

    /**
     * Validates the worksheet manager for add or update. The routine throws a
     * list of exceptions/warnings listing all the problems for each worksheet.
     */
    protected void validate(WorksheetManager1 wm) throws Exception {
        HashMap<Integer, WorksheetItemDO> imap;
        HashMap<Integer, WorksheetAnalysisViewDO> amap;
        ValidationErrorsList e;
        WorksheetAnalysisViewDO ana;

        e = new ValidationErrorsList();
        imap = new HashMap<Integer, WorksheetItemDO>();
        amap = new HashMap<Integer, WorksheetAnalysisViewDO>();

        /*
         * worksheet level
         */
        if (getWorksheet(wm) != null && getWorksheet(wm).isChanged()) {
            try {
                worksheet.validate(getWorksheet(wm));
            } catch (Exception err) {
                DataBaseUtil.mergeException(e, err);
            }
        }

        /*
         * at least one worksheet item must be on the worksheet
         */
        if (getItems(wm) == null || getItems(wm).size() < 1) {
            e.add(new FormErrorException(Messages.get().worksheetNotSaveEmpty()));
        } else {
            for (WorksheetItemDO data : getItems(wm)) {
                imap.put(data.getId(), data);
                if (data.isChanged()) {
                    try {
                        item.validate(data);
                    } catch (Exception err) {
                        DataBaseUtil.mergeException(e, err);
                    }
                }
            }
        }

        /*
         * each analysis must be valid
         */
        if (getAnalyses(wm) != null) {
            for (WorksheetAnalysisViewDO data : getAnalyses(wm)) {
                amap.put(data.getId(), data);
                if (data.isChanged() || imap.get(data.getWorksheetItemId()).isChanged()) {
                    try {
                        analysis.validate(data);
                    } catch (Exception err) {
                        DataBaseUtil.mergeException(e, err);
                    }
                }
            }
        }

        /*
         * results must be valid
         */
        if (getResults(wm) != null) {
            for (WorksheetResultViewDO data : getResults(wm)) {
                ana = amap.get(data.getWorksheetAnalysisId());
                if (data.isChanged() || ana.isChanged()) {
                    try {
                        wResult.validate(data);
                    } catch (Exception err) {
                        DataBaseUtil.mergeException(e, err);
                    }
                }
            }
        }

        /*
         * qc results must be valid
         */
        if (getQcResults(wm) != null) {
            for (WorksheetQcResultViewDO data : getQcResults(wm)) {
                ana = amap.get(data.getWorksheetAnalysisId());
                if (data.isChanged() || ana.isChanged()) {
                    try {
                        wqResult.validate(data);
                    } catch (Exception err) {
                        DataBaseUtil.mergeException(e, err);
                    }
                }
            }
        }

        if (e.size() > 0)
            throw e;
    }
    
    public ArrayList<ResultViewDO> fetchAnalytesByAnalysis(Integer analysisId, Integer testId) throws Exception {
        int rowIndex;
        ArrayList<ArrayList<ResultViewDO>> results;
        ArrayList<ResultViewDO> resultAnalytes;
        ArrayList<TestAnalyteViewDO> testAnalytes;
        HashSet<Integer> analyteIds;
        ResultViewDO rVDO, newResult;
        
        resultAnalytes = new ArrayList<ResultViewDO>();
        analyteIds = new HashSet<Integer>();
        results = new ArrayList<ArrayList<ResultViewDO>>();
        result.fetchByAnalysisIdForDisplay(analysisId, results);
        for (ArrayList<ResultViewDO> resultRow : results) {
            rVDO = resultRow.get(0);
            resultAnalytes.add(rVDO);
            analyteIds.add(rVDO.getAnalyteId());
        }
        
        rowIndex = 0;
        testAnalytes = testAnalyte.fetchRowAnalytesByTestId(testId);
        for (TestAnalyteViewDO taVDO : testAnalytes) {
            if (!analyteIds.contains(taVDO.getAnalyteId())) {
                rVDO = (ResultViewDO)resultAnalytes.get(rowIndex);
                while (!rVDO.getRowGroup().equals(taVDO.getRowGroup()) && rowIndex < resultAnalytes.size() - 1)
                    rVDO = (ResultViewDO)resultAnalytes.get(++rowIndex);
                while (rVDO.getRowGroup().equals(taVDO.getRowGroup()) && rowIndex < resultAnalytes.size() - 1)
                    rVDO = (ResultViewDO)resultAnalytes.get(++rowIndex);
                
                newResult = new ResultViewDO();
                newResult.setAnalysisId(analysisId);
                newResult.setTestAnalyteId(taVDO.getId());
                newResult.setSortOrder(rowIndex);
                newResult.setIsReportable("N");
                newResult.setAnalyteId(taVDO.getAnalyteId());
                newResult.setAnalyte(taVDO.getAnalyteName());
                newResult.setRowGroup(taVDO.getRowGroup());
                resultAnalytes.add(rowIndex, newResult);
            }
        }
        
        return resultAnalytes;
    }

    public WorksheetQcChoiceVO loadTemplate(WorksheetManager1 wm, Integer testId) {
        int i, j, k, numSub, pos;
        ArrayList<TestWorksheetItemDO> lastBothList, lastRunList, lastSubsetList,
                                       randList;
        ArrayList<WorksheetAnalysisViewDO> newAnalyses, removed;
        ArrayList<WorksheetItemDO> items, newItems;
        HashMap<String, ArrayList<QcLotViewDO>> qcMap;
        Preferences prefs;
        TestWorksheetDO twDO;
        TestWorksheetItemDO qcTemplate[], twiDO, twiDO1;
        TestWorksheetManager twMan;
        ValidationErrorsList errors;
        WorksheetAnalysisViewDO waVDO, waVDO1;
        WorksheetItemDO wiDO;
        WorksheetQcChoiceVO wqcVO;
        
        errors = new ValidationErrorsList();
        prefs = null;
        qcMap = new HashMap<String, ArrayList<QcLotViewDO>>();
        twMan = null;
        
        wqcVO = new WorksheetQcChoiceVO();
        wqcVO.setManager(wm);
        try {
            twMan = TestWorksheetManager.fetchByTestId(testId);
        } catch (Exception anyE) {
            errors.add(anyE);
            wqcVO.setErrors(errors);
            return wqcVO;
        }
        
        //
        // If there is no worksheet definition, an empty manager is returned
        //
        twDO = twMan.getWorksheet();
        if (twDO.getId() == null) {
            //
            // If there is no worksheet definition for the test, load
            // default template
            //
            twDO.setTotalCapacity(500);
            wm.setTotalCapacity(500);
            wm.getWorksheet().setFormatId(Constants.dictionary().WF_TOTAL);
            wm.getWorksheet().setSubsetCapacity(500);
            return wqcVO;
        } else {
            wm.setTotalCapacity(twDO.getTotalCapacity());
            wm.getWorksheet().setFormatId(twDO.getFormatId());
            wm.getWorksheet().setSubsetCapacity(twDO.getSubsetCapacity());
        }
        
        //
        // Build the QC Template
        //
        qcTemplate = new TestWorksheetItemDO[twDO.getTotalCapacity()];
        randList = new ArrayList<TestWorksheetItemDO>();
        lastRunList = new ArrayList<TestWorksheetItemDO>();
        lastSubsetList = new ArrayList<TestWorksheetItemDO>();
        lastBothList = new ArrayList<TestWorksheetItemDO>();
        for (i = 0; i < twMan.itemCount(); i++) {
            twiDO = twMan.getItemAt(i);
            if (Constants.dictionary().POS_DUPLICATE.equals(twiDO.getTypeId()) || 
                Constants.dictionary().POS_FIXED.equals(twiDO.getTypeId()) ||
                Constants.dictionary().POS_FIXED_ALWAYS.equals(twiDO.getTypeId())) {
                for (j = twiDO.getPosition(); j < twDO.getTotalCapacity(); j += twDO.getSubsetCapacity())
                    qcTemplate[j - 1] = twiDO;
            } else if (Constants.dictionary().POS_RANDOM.equals(twiDO.getTypeId())) {
                randList.add(twiDO);
            } else if (Constants.dictionary().POS_LAST_OF_RUN.equals(twiDO.getTypeId())) {
                lastRunList.add(twiDO);
            } else if (Constants.dictionary().POS_LAST_OF_SUBSET.equals(twiDO.getTypeId())) {
                lastSubsetList.add(twiDO);
            } else if (Constants.dictionary().POS_LAST_OF_SUBSET_AND_RUN.equals(twiDO.getTypeId())) {
                lastBothList.add(twiDO);
            }
        }
        
        //
        // Insert Last of Subset/Both QC items into the worksheet per subset
        //
        if (lastSubsetList.isEmpty())
            lastSubsetList = lastBothList;
        pos = twDO.getSubsetCapacity() - lastSubsetList.size() - 1;
        for (i = 0; i < lastSubsetList.size(); i++) {
            twiDO = lastSubsetList.get(i);
            for (j = pos; j < twDO.getTotalCapacity(); j += twDO.getSubsetCapacity())
                qcTemplate[j] = twiDO;
            pos++;
        }
        
        //
        // Insert random QC items into the worksheet per subset
        //
        numSub = twDO.getTotalCapacity() / twDO.getSubsetCapacity();
        for (i = 0; i < numSub; i++) {
            j = 0;
            while (j < randList.size()) {
                twiDO = randList.get(j);
                pos = (int) (Math.random() * (twDO.getSubsetCapacity() - 1)) + i * twDO.getSubsetCapacity();
                if (qcTemplate[pos] == null) {
                    if ((pos + 1) < twDO.getTotalCapacity()) {
                        twiDO1 = qcTemplate[pos + 1];
                        if (twiDO1 != null && 
                            Constants.dictionary().POS_DUPLICATE.equals(twiDO1.getTypeId()))
                            continue;
                    }
                    qcTemplate[pos] = twiDO;
                    j++;
                }
            }
        }

        try {
            prefs = Preferences.userRoot();
        } catch (Exception anyE) {
            errors.add(anyE);
        }
        
        //
        // Merge in the existing records and add the last of run qcs.  If there
        // are more than one matching qc for a particular position, add it to 
        // the list of choices for the user to make.
        //
        j = 0;
        if (lastRunList.isEmpty())
            lastRunList = lastBothList;
        items = getItems(wm);
        newItems = new ArrayList<WorksheetItemDO>();
        newAnalyses = new ArrayList<WorksheetAnalysisViewDO>();
        for (i = 0; i < twDO.getTotalCapacity() - lastRunList.size(); i++) {
            twiDO = qcTemplate[i];
            if (twiDO == null) {
                if (j >= items.size())
                    break;
                wiDO = items.get(j);
                wiDO.setPosition(i + 1);
                newItems.add(wiDO);
                j++;
            } else {
                wiDO = new WorksheetItemDO();
                wiDO.setId(wm.getNextUID());
                wiDO.setPosition(i + 1);
                waVDO = new WorksheetAnalysisViewDO();
                waVDO.setId(wm.getNextUID());
                waVDO.setWorksheetItemId(wiDO.getId());
                if (Constants.dictionary().POS_DUPLICATE.equals(twiDO.getTypeId())) {
                    waVDO1 = wm.analysis.get(newItems.get(i - 1), 0);
                    waVDO.setAccessionNumber("D" + waVDO1.getAccessionNumber());
                    waVDO.setAnalysisId(waVDO1.getAnalysisId());
                    waVDO.setWorksheetAnalysisId(waVDO1.getId());
                    waVDO.setDescription("Duplicate of " + waVDO1.getAccessionNumber());
                    waVDO.setTestId(waVDO1.getTestId());
                    waVDO.setTestName(waVDO1.getTestName());
                    waVDO.setMethodName(waVDO1.getMethodName());
                    waVDO.setSectionName(waVDO1.getSectionName());
                    waVDO.setUnitOfMeasureId(waVDO1.getUnitOfMeasureId());
                    waVDO.setUnitOfMeasure(waVDO1.getUnitOfMeasure());
                    waVDO.setStatusId(waVDO1.getStatusId());
                    waVDO.setCollectionDate(waVDO1.getCollectionDate());
                    waVDO.setReceivedDate(waVDO1.getReceivedDate());
                    waVDO.setDueDays(waVDO1.getDueDays());
                    waVDO.setExpireDate(waVDO1.getExpireDate());
                    getAnalyses(wm).add(waVDO);
                    newAnalyses.add(waVDO);
                    newItems.add(wiDO);
                } else {
                    waVDO.setAccessionNumber("X." + wiDO.getPosition());
                    try {
                        loadQcForAnalysis(wm, twiDO, i, waVDO, prefs, wqcVO, qcMap);
                        newAnalyses.add(waVDO);
                        newItems.add(wiDO);
                    } catch (FormErrorException feE) {
                        errors.add(feE);
                        if (j < items.size()) {
                            wiDO = items.get(j);
                            wiDO.setPosition(i + 1);
                            newItems.add(wiDO);
                            j++;
                        }
                    } catch (Exception anyE) {
                        errors.add(new FormErrorException("Error loading QC for position " +
                                                          (i + 1) + "; " + anyE.getMessage()));
                        if (j < items.size()) {
                            wiDO = items.get(j);
                            wiDO.setPosition(i + 1);
                            newItems.add(wiDO);
                            j++;
                        }
                    }
                }
            }
        }
    
        //
        // Append Last of Run QC items interweaving any fixedAlways QC items
        //
        for (k = 0; k < lastRunList.size() && i < twDO.getTotalCapacity(); i++) {
            wiDO = new WorksheetItemDO();
            wiDO.setId(wm.getNextUID());
            wiDO.setPosition(i + 1);
            waVDO = new WorksheetAnalysisViewDO();
            waVDO.setId(wm.getNextUID());
            waVDO.setWorksheetItemId(wiDO.getId());
            waVDO.setAccessionNumber("X." + wiDO.getPosition());

            twiDO = qcTemplate[i];
            if (twiDO == null || !Constants.dictionary().POS_FIXED_ALWAYS.equals(twiDO.getTypeId())) {
                twiDO = lastRunList.get(k);
                k++;
            }
            
            try {
                loadQcForAnalysis(wm, twiDO, i, waVDO, prefs, wqcVO, qcMap);
                newAnalyses.add(waVDO);
                newItems.add(wiDO);
            } catch (FormErrorException feE) {
                errors.add(feE);
            } catch (Exception anyE) {
                errors.add(new FormErrorException("Error loading QC for position " +
                                                  (i + 1) + "; " + anyE.getMessage()));
            }
        }
        
        //
        // Add in any remaining fixed always QCs
        //
        while (i < twDO.getTotalCapacity()) {
            twiDO = qcTemplate[i];
            if (twiDO != null && Constants.dictionary().POS_FIXED_ALWAYS.equals(twiDO.getTypeId())) {
                wiDO = new WorksheetItemDO();
                wiDO.setId(wm.getNextUID());
                wiDO.setPosition(i + 1);
                waVDO = new WorksheetAnalysisViewDO();
                waVDO.setId(wm.getNextUID());
                waVDO.setWorksheetItemId(wiDO.getId());
                waVDO.setAccessionNumber("X." + wiDO.getPosition());

                try {
                    loadQcForAnalysis(wm, twiDO, i, waVDO, prefs, wqcVO, qcMap);
                    newAnalyses.add(waVDO);
                    newItems.add(wiDO);
                } catch (FormErrorException feE) {
                    errors.add(feE);
                } catch (Exception anyE) {
                    errors.add(new FormErrorException("Error loading QC for position " +
                                                      (i + 1) + "; " + anyE.getMessage()));
                }
            }
            i++;
        }

        if (j < items.size()) {
            removed = new ArrayList<WorksheetAnalysisViewDO>();
            while (j < items.size()) {
                wiDO = items.get(j);
                for (i = 0; i < wm.analysis.count(wiDO);) {
                    waVDO = wm.analysis.get(wiDO, i);
                    if (waVDO.getAnalysisId() != null) {
                        for (k = 0; k < wm.result.count(waVDO);)
                            wm.result.remove(waVDO, k);
                    } else if (waVDO.getQcLotId() != null) {
                        for (k = 0; k < wm.qcResult.count(waVDO);)
                            wm.qcResult.remove(waVDO, k);
                    }
                    removed.add(waVDO);
                    wm.analysis.remove(wiDO, i);
                }
                j++;
            }
            wqcVO.setRemovedAnalyses(removed);
        }
        
        setItems(wm, newItems);

        wqcVO.setNewAnalyses(newAnalyses);
        try {
            initializeResults(wm, newAnalyses);
        } catch (Exception anyE) {
            errors.add(anyE);
        }
        
        wqcVO.setErrors(errors);
        return wqcVO;
    }
    
    public WorksheetManager1 initializeResults(WorksheetManager1 wm, ArrayList<WorksheetAnalysisViewDO> analyses) throws Exception {
        int i;
        ArrayList<Integer> excludedIds;
        ArrayList<ArrayList<ResultViewDO>> resultDOs;
        ArrayList<QcAnalyteViewDO> qcAnalytes;
        ArrayList<ResultViewDO> resultRow;
        HashMap<Integer, ArrayList<Integer>> excludedMap;
        HashMap<Integer, ArrayList<QcAnalyteViewDO>> qcAnalyteMap;
        QcLotViewDO qclVDO;
        ResultViewDO resultDO;
        WorksheetQcResultViewDO wqrVDO;
        WorksheetResultViewDO wrVDO;

        excludedMap = new HashMap<Integer, ArrayList<Integer>>();
        qcAnalyteMap = new HashMap<Integer, ArrayList<QcAnalyteViewDO>>();
        
        for (WorksheetAnalysisViewDO waVDO : analyses) {
            if (waVDO.getAnalysisId() != null) {
                excludedIds = excludedMap.get(waVDO.getTestId());
                if (excludedIds == null) {
                    excludedIds = new ArrayList<Integer>();
                    try {
                        for (TestWorksheetAnalyteViewDO twaVDO : twAnalyte.fetchByTestId(waVDO.getTestId()))
                            excludedIds.add(twaVDO.getTestAnalyteId());
                    } catch (Exception anyE) {
                        throw new Exception("Error loading excluded analytes: " + anyE.getMessage());
                    }
                    excludedMap.put(waVDO.getTestId(), excludedIds);
                }
        
                resultDOs = new ArrayList<ArrayList<ResultViewDO>>();
                try {
                    result.fetchByAnalysisIdForDisplay(waVDO.getAnalysisId(), resultDOs);
                } catch (Exception anyE) {
                    throw new Exception("Error loading results from test definition: " + anyE.getMessage());
                }
        
                for (i = 0; i < resultDOs.size(); i++) {
                    resultRow = resultDOs.get(i);
                    resultDO = resultRow.get(0);
                    if (excludedIds.size() == 0 || !excludedIds.contains(resultDO.getTestAnalyteId())) {
                        wrVDO = wm.result.add(waVDO);
                        wrVDO.setTestAnalyteId(resultDO.getTestAnalyteId());
                        wrVDO.setResultRow(i);
                        wrVDO.setAnalyteId(resultDO.getAnalyteId());
                        wrVDO.setTypeId(resultDO.getTypeId());
                        wrVDO.setAnalyteName(resultDO.getAnalyte());
                    }
                }
            } else if (waVDO.getQcLotId() != null) {
                try {
                    qclVDO = qcLot.fetchById(waVDO.getQcLotId());
                    qcAnalytes = qcAnalyteMap.get(qclVDO.getQcId());
                    if (qcAnalytes == null) {
                        qcAnalytes = qcAnalyte.fetchByQcId(qclVDO.getQcId());
                        qcAnalyteMap.put(qclVDO.getQcId(), qcAnalytes);
                    }
                    for (QcAnalyteViewDO qcaVDO : qcAnalytes) {
                        wqrVDO = wm.qcResult.add(waVDO);
                        wqrVDO.setSortOrder(qcaVDO.getSortOrder());
                        wqrVDO.setQcAnalyteId(qcaVDO.getId());
                        wqrVDO.setTypeId(qcaVDO.getTypeId());
                        wqrVDO.setAnalyteId(qcaVDO.getAnalyteId());
                        wqrVDO.setAnalyteName(qcaVDO.getAnalyteName());
                    }
                } catch (NotFoundException nfE) {
                    // if there are no analytes for the qc we do not need to 
                    // create worksheet_qc_result records
                } catch (Exception anyE) {
                    throw new Exception("Error loading analytes for qc lot: " + anyE.getMessage());
                }
            }
        }
        
        return wm;
    }
    
    public WorksheetManager1 initializeResultsFromOther(WorksheetManager1 wm,
                                                        ArrayList<WorksheetAnalysisViewDO> fromAnalyses,
                                                        ArrayList<WorksheetAnalysisViewDO> toAnalyses,
                                                        Integer fromWorksheetId) throws Exception {
        int a, i;
        ArrayList<IdNameVO> columnNames;
        ArrayList<WorksheetQcResultViewDO> fromWqrVDOs;
        ArrayList<WorksheetResultViewDO> fromWrVDOs;
        HashMap<Integer, String> fromColumnMap;
        HashMap<String, Integer> toColumnMap;
        Integer toIndex;
        String fromName;
        WorksheetAnalysisViewDO fromWaVDO, toWaVDO;
        WorksheetQcResultViewDO toWqrVDO;
        WorksheetResultViewDO toWrVDO;
        WorksheetViewDO fromWorksheet, toWorksheet;
        
        toWorksheet = wm.getWorksheet();
        if (toWorksheet.getFormatId() == null)
            throw new Exception(Messages.get().worksheetChooseFormatBeforeAddFromOther());
        fromWorksheet = worksheet.fetchById(fromWorksheetId);
        
        fromColumnMap = new HashMap<Integer, String>();
        toColumnMap = new HashMap<String, Integer>();
        if (!toWorksheet.getFormatId().equals(fromWorksheet.getFormatId())) {
            columnNames = worksheet.getColumnNames(fromWorksheet.getFormatId());
            for (IdNameVO vo : columnNames)
                fromColumnMap.put(vo.getId(), vo.getName());
            columnNames = worksheet.getColumnNames(toWorksheet.getFormatId());
            for (IdNameVO vo : columnNames)
                toColumnMap.put(vo.getName(), vo.getId());
        }

        for (a = 0; a < fromAnalyses.size() && a < toAnalyses.size(); a++) {
            fromWaVDO = fromAnalyses.get(a);
            toWaVDO = toAnalyses.get(a);
            if (fromWaVDO.getAnalysisId() != null) {
                try {
                    fromWrVDOs = wResult.fetchByWorksheetAnalysisId(fromWaVDO.getId());
                    for (WorksheetResultViewDO fromWrVDO : fromWrVDOs) {
                        toWrVDO = wm.result.add(toWaVDO);
                        toWrVDO.setTestAnalyteId(fromWrVDO.getTestAnalyteId());
                        toWrVDO.setTestResultId(fromWrVDO.getTestResultId());
                        toWrVDO.setResultRow(fromWrVDO.getResultRow());
                        toWrVDO.setAnalyteId(fromWrVDO.getAnalyteId());
                        toWrVDO.setTypeId(fromWrVDO.getTypeId());
                        if (!toWorksheet.getFormatId().equals(fromWorksheet.getFormatId())) {
                            for (i = 0; i < 30; i++) {
                                fromName = fromColumnMap.get(i + 9);
                                if (fromName != null) {
                                    toIndex = toColumnMap.get(fromName);
                                    if (toIndex != null)
                                        toWrVDO.setValueAt(toIndex.intValue() - 9, fromWrVDO.getValueAt(i));
                                }
                            }
                        } else {
                            for (i = 0; i < 30; i++)
                                toWrVDO.setValueAt(i, fromWrVDO.getValueAt(i));
                        }
                        toWrVDO.setAnalyteName(fromWrVDO.getAnalyteName());
                        toWrVDO.setAnalyteExternalId(fromWrVDO.getAnalyteExternalId());
                        toWrVDO.setResultGroup(fromWrVDO.getResultGroup());
                    }
                } catch (NotFoundException nfE) {
                    log.log(Level.INFO, nfE.getMessage());
                }
            } else if (fromWaVDO.getQcLotId() != null) {
                try {
                    fromWqrVDOs = wqResult.fetchByWorksheetAnalysisId(fromWaVDO.getId());
                    for (WorksheetQcResultViewDO fromWqrVDO : fromWqrVDOs) {
                        toWqrVDO = wm.qcResult.add(toWaVDO);
                        toWqrVDO.setSortOrder(fromWqrVDO.getSortOrder());
                        toWqrVDO.setQcAnalyteId(fromWqrVDO.getQcAnalyteId());
                        toWqrVDO.setTypeId(fromWqrVDO.getTypeId());
                        if (!toWorksheet.getFormatId().equals(fromWorksheet.getFormatId())) {
                            for (i = 0; i < 30; i++) {
                                fromName = fromColumnMap.get(i + 9);
                                if (fromName != null) {
                                    toIndex = toColumnMap.get(fromName);
                                    if (toIndex != null)
                                        toWqrVDO.setValueAt(toIndex.intValue() - 9, fromWqrVDO.getValueAt(i));
                                }
                            }
                        } else {
                            for (i = 0; i < 30; i++)
                                toWqrVDO.setValueAt(i, fromWqrVDO.getValueAt(i));
                        }
                        toWqrVDO.setAnalyteId(fromWqrVDO.getAnalyteId());
                        toWqrVDO.setAnalyteName(fromWqrVDO.getAnalyteName());
                    }
                } catch (NotFoundException nfE) {
                    log.log(Level.INFO, nfE.getMessage());
                }
            }
        }
        
        return wm;
    }
    
    public WorksheetManager1 sortItems(WorksheetManager1 wm, int col, int dir) {
        int i;
        
        Collections.sort(WorksheetManager1Accessor.getItems(wm),
                         new WorksheetComparator(wm, col, dir));
        
        for (i = 0; i < wm.item.count(); i++)
            wm.item.get(i).setPosition(i + 1);

        return wm;
    }
    
    private void loadQcForAnalysis(WorksheetManager1 wm, TestWorksheetItemDO twiDO,
                                   int index, WorksheetAnalysisViewDO waVDO, Preferences prefs,
                                   WorksheetQcChoiceVO wqcVO, HashMap<String, ArrayList<QcLotViewDO>> qcMap) throws Exception {
        int i;
        ArrayList<String> uids;
        ArrayList<QcLotViewDO> qcs, qcsByLoc;
        
        qcs = qcMap.get(twiDO.getQcName());
        if (qcs == null) {
            qcs = qcLot.fetchActiveByQcName(twiDO.getQcName(), 10);
            qcMap.put(twiDO.getQcName(), qcs);
        }
        if (qcs.size() == 0) {
            throw new FormErrorException(Messages.get().noMatchingActiveQc(twiDO.getQcName(), String.valueOf(index + 1)));
        } else if (qcs.size() > 1) {
            qcsByLoc = null;
            if (prefs != null) {
                for (QcLotViewDO tempLot : qcs) {
                    if (tempLot.getLocationId() != null && tempLot.getLocationId().equals(prefs.getInt("location", -1))) {
                        if (qcsByLoc == null)
                            qcsByLoc = new ArrayList<QcLotViewDO>();
                        qcsByLoc.add(tempLot);
                    }
                }
            }
            if (qcsByLoc != null) {
                if (qcsByLoc.size() > 1) {
                    uids = new ArrayList<String>();
                    for (i = 0; i < qcsByLoc.size(); i++)
                        uids.add(wm.getUid(waVDO));
                    wqcVO.addChoices(qcsByLoc);
                    wqcVO.addChoiceUids(uids);
                }
                waVDO.setQcLotId(qcsByLoc.get(0).getId());
                waVDO.setDescription(qcsByLoc.get(0).getQcName() + " (" +
                                     qcsByLoc.get(0).getLotNumber() + ")");
                getAnalyses(wm).add(waVDO);
            } else {
                uids = new ArrayList<String>();
                for (i = 0; i < qcs.size(); i++)
                    uids.add(wm.getUid(waVDO));
                wqcVO.addChoices(qcs);
                wqcVO.addChoiceUids(uids);

                waVDO.setQcLotId(qcs.get(0).getId());
                waVDO.setDescription(qcs.get(0).getQcName() + " (" + qcs.get(0).getLotNumber() + ")");
                getAnalyses(wm).add(waVDO);
            }
        } else {
            waVDO.setQcLotId(qcs.get(0).getId());
            waVDO.setDescription(qcs.get(0).getQcName() + " (" + qcs.get(0).getLotNumber() + ")");
            getAnalyses(wm).add(waVDO);
        }
    }
    
    private void synchronizeResults(WorksheetManager1 wm, WorksheetAnalysisViewDO waVDO,
                                    ArrayList<ResultViewDO> results,
                                    ArrayList<WorksheetResultViewDO> wResults,
                                    ArrayList<Integer> excludedIds) throws Exception {
        int rowIndex;
        ArrayList<WorksheetResultViewDO> wrList;
        HashMap<Integer, ArrayList<WorksheetResultViewDO>> wrMap;
        WorksheetResultViewDO wrVDO;
        
        wrMap = new HashMap<Integer, ArrayList<WorksheetResultViewDO>>();
        for (WorksheetResultViewDO data : wResults) {
            wrList = wrMap.get(data.getTestAnalyteId());
            if (wrList == null) {
                wrList = new ArrayList<WorksheetResultViewDO>();
                wrMap.put(data.getTestAnalyteId(), wrList);
            }
            wrList.add(data);
        }
        
        rowIndex = 0;
        for (ResultViewDO rVDO : results) {
            if ("Y".equals(rVDO.getIsColumn()) || excludedIds.contains(rVDO.getTestAnalyteId()))
                continue;
            
            wrList = wrMap.get(rVDO.getTestAnalyteId());
            if (wrList != null) {
                wrVDO = wrList.get(0);
                if (wrVDO.getResultRow() != rowIndex)
                    wrVDO.setResultRow(rowIndex);
                rowIndex++;
                wrList.remove(0);
                if (wrList.size() == 0)
                    wrMap.remove(wrList);
            } else {
                wrVDO = wm.result.add(waVDO);
                wrVDO.setTestAnalyteId(rVDO.getTestAnalyteId());
                wrVDO.setResultRow(rowIndex++);
                wrVDO.setAnalyteId(rVDO.getAnalyteId());
                wrVDO.setTypeId(rVDO.getTypeId());
                wrVDO.setAnalyteName(rVDO.getAnalyte());
            }
        }
    }
    
    private void unlockSamples(ArrayList<SampleManager1> sMans) throws Exception {
        ArrayList<Integer> sampleIds;
        
        sampleIds = new ArrayList<Integer>();
        for (SampleManager1 sMan : sMans)
            sampleIds.add(sMan.getSample().getId());
        sampleMan.unlock(sampleIds, SampleManager1.Load.SINGLERESULT);
    }
    
    class WorksheetComparator<T extends WorksheetItemDO> implements Comparator<T> {
        int col, dir;
        WorksheetManager1 wm;
        
        public WorksheetComparator(WorksheetManager1 wm, int col, int dir) {
            this.wm = wm;
            this.col = col;
            this.dir = dir;
        }
        
        public int compare(WorksheetItemDO wiDO1, WorksheetItemDO wiDO2) {
            Comparable c1, c2;
            WorksheetAnalysisViewDO waVDO1, waVDO2;
            
            c1 = null;
            c2 = null;
            waVDO1 = wm.analysis.get(wiDO1, 0);
            waVDO2 = wm.analysis.get(wiDO2, 0);
            switch (col) {
                case 0:         // position
                case 3:         // qc link
                    return 0;
                    
                case 1:
                    c1 = waVDO1.getAccessionNumber();
                    c2 = waVDO2.getAccessionNumber();
                    break;
                    
                case 2:
                    c1 = waVDO1.getDescription();
                    c2 = waVDO2.getDescription();
                    break;
                    
                case 4:
                    c1 = waVDO1.getTestName();
                    c2 = waVDO2.getTestName();
                    break;
                    
                case 5:
                    c1 = waVDO1.getMethodName();
                    c2 = waVDO2.getMethodName();
                    break;
                    
                case 6:
                    c1 = waVDO1.getUnitOfMeasure();
                    c2 = waVDO2.getUnitOfMeasure();
                    break;
                    
                case 7:
                    try {
                        c1 = dictionary.getById(waVDO1.getStatusId()).getEntry();
                    } catch (Exception ignE) {}
                    try {
                        c2 = dictionary.getById(waVDO2.getStatusId()).getEntry();
                    } catch (Exception ignE) {}
                    break;
                    
                case 8:
                    c1 = waVDO1.getCollectionDate();
                    c2 = waVDO2.getCollectionDate();
                    break;
                    
                case 9:
                    c1 = waVDO1.getReceivedDate();
                    c2 = waVDO2.getReceivedDate();
                    break;
                    
                case 10:
                    c1 = waVDO1.getDueDays();
                    c2 = waVDO2.getDueDays();
                    break;
                    
                case 11:
                    c1 = waVDO1.getExpireDate();
                    c2 = waVDO2.getExpireDate();
                    break;
            }
            
            if (c1 == null && c2 == null) {
                return 0;
            } else if (c1 != null && c2 != null) {
                return dir * c1.compareTo(c2);
            } else {
                if (c1 == null && c2 != null)
                    return 1;
                else 
                    return -1;
            }           
        }
    }
}
