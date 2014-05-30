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
import java.util.Iterator;
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
import org.openelis.domain.AnalysisUserViewDO;
import org.openelis.domain.AnalysisViewDO;
import org.openelis.domain.AnalyteDO;
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
import org.openelis.domain.WorksheetResultsTransferVO;
import org.openelis.domain.WorksheetViewDO;
import org.openelis.manager.Preferences;
import org.openelis.manager.SampleManager1;
import org.openelis.manager.SampleManager1Accessor;
import org.openelis.manager.TestManager;
import org.openelis.manager.TestWorksheetManager;
import org.openelis.manager.WorksheetManager1;
import org.openelis.manager.WorksheetManager1Accessor;
import org.openelis.ui.common.DataBaseUtil;
import org.openelis.ui.common.Datetime;
import org.openelis.ui.common.FormErrorException;
import org.openelis.ui.common.InconsistencyException;
import org.openelis.ui.common.NotFoundException;
import org.openelis.ui.common.SectionPermission;
import org.openelis.ui.common.SystemUserVO;
import org.openelis.ui.common.ValidationErrorsList;
import org.openelis.ui.common.data.QueryData;
import org.openelis.utilcommon.ResultFormatter;
import org.openelis.utilcommon.ResultHelper;
import org.openelis.utils.User;

@Stateless
@SecurityDomain("openelis")
public class WorksheetManager1Bean {

    @Resource
    private SessionContext               ctx;
    
    @EJB
    private AnalysisHelperBean           aHelper;
    @EJB
    private AnalyteBean                  analyte;
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
    private TestManagerBean              testManager;
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
        ArrayList<ResultViewDO> resultList;
        ArrayList<WorksheetManager1> wms;
        HashMap<Integer, ArrayList<ResultViewDO>> arMap;
        HashMap<Integer, Integer> anaIdMap;
        HashMap<Integer, WorksheetManager1> map1, map2;
        HashSet<Integer> anaIds;
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
                anaIds = new HashSet<Integer>();
                anaIdMap = new HashMap<Integer, Integer>();
                for (WorksheetAnalysisViewDO data : analysis.fetchByWorksheetIds(ids1)) {
                    wm = map1.get(data.getWorksheetId());
                    addAnalysis(wm, data);
                    if (!map2.containsKey(data.getId())) {
                        ids2.add(data.getId());
                        map2.put(data.getId(), wm);
                    }
                    if (data.getAnalysisId() != null) {
                        anaIds.add(data.getAnalysisId());
                        anaIdMap.put(data.getId(), data.getAnalysisId());
                    }
                }
        
                arMap = new HashMap<Integer, ArrayList<ResultViewDO>>();
                if (!anaIds.isEmpty()) {
                    for (ResultViewDO rVDO : result.fetchByAnalysisIds(new ArrayList<Integer>(anaIds))) {
                        resultList = arMap.get(rVDO.getAnalysisId());
                        if (resultList == null) {
                            resultList = new ArrayList<ResultViewDO>();
                            arMap.put(rVDO.getAnalysisId(), resultList);
                        }
                        resultList.add(rVDO);
                    }
                }
                    
                if (!ids2.isEmpty()) {
                    for (WorksheetResultViewDO data : wResult.fetchByWorksheetAnalysisIds(ids2)) {
                        wm = map2.get(data.getWorksheetAnalysisId());
                        data.setIsReportable(arMap.get(anaIdMap.get(data.getWorksheetAnalysisId())).get(data.getResultRow()).getIsReportable());
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
//    @RolesAllowed("worksheet-update")
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
     * Returns a locked worksheet manager and the accompanying locked sample managers
     */
    @RolesAllowed("sample-update")
    public WorksheetResultsTransferVO fetchForTransfer(Integer worksheetId) throws Exception {
        ArrayList<Integer> ids;
        ArrayList<SampleManager1> sms;
        ArrayList<WorksheetManager1> wms;
        HashSet<Integer> analysisIds;
        WorksheetManager1 wMan;
        
        wMan = null;
        sms = null;
        
        lock.lock(Constants.table().WORKSHEET, worksheetId);

        ids = new ArrayList<Integer>(1);
        ids.add(worksheetId);
        wms = fetchByIds(ids, WorksheetManager1.Load.DETAIL, WorksheetManager1.Load.NOTE);
        if (wms.size() > 0) {
            wMan = wms.get(0);
            analysisIds = new HashSet<Integer>();
            for (WorksheetAnalysisViewDO waVDO : getAnalyses(wMan)) {
                if (waVDO.getAnalysisId() != null)
                    analysisIds.add(waVDO.getAnalysisId());
            }
            if (analysisIds.size() > 0)
                sms = sampleMan.fetchForUpdateByAnalyses(new ArrayList<Integer>(analysisIds),
                                                         SampleManager1.Load.ORGANIZATION,
                                                         SampleManager1.Load.QA,
                                                         SampleManager1.Load.SINGLERESULT);
        }
        
        return new WorksheetResultsTransferVO(wMan, sms);
    }

    /**
     * Unlocks and returns a worksheet manager with specified worksheet id and
     * requested load elements
     */
//    @RolesAllowed({"worksheet-add", "worksheet-update"})
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
//    @RolesAllowed({"worksheet-add", "worksheet-update"})
    public WorksheetManager1 update(WorksheetManager1 wm, WorksheetManager1.ANALYSIS_UPDATE updateFlag) throws Exception {
        int dep, ldep;
        boolean locked, nodep, unlock;
        ArrayList<Integer> analyteIndexes, excludedIds;
        ArrayList<ResultViewDO> results;
        ArrayList<SampleManager1> sMans;
        ArrayList<TestAnalyteViewDO> analytes;
        ArrayList<WorksheetResultViewDO> wResults;
        Datetime createdDate, startedDate;
        HashMap<Integer, AnalyteDO> analyteMap;
        HashMap<Integer, ArrayList<Integer>> excludedMap;
        HashMap<Integer, ArrayList<ResultViewDO>> newResults, resultHash;
        HashMap<Integer, ArrayList<WorksheetResultViewDO>> wResultHash;
        HashMap<Integer, Integer> imap, amap;
        HashMap<Integer, QcAnalyteViewDO> qcAnalyteMap;
        HashMap<Integer, ResultViewDO> updatedResults;
        HashMap<Integer, SampleManager1> sMansByAnalysisId;
        HashMap<Integer, String> testMethodNames;
        HashMap<Integer, WorksheetItemDO> wItems;
        HashMap<Integer, WorksheetAnalysisViewDO> updatedWorksheetAnalyses;
        HashMap<String, Integer> formatColumnMap;
        HashSet<Integer> analysisIds, initAnalysisIds, updateAnalysisIds;
        Integer tmpid, id, col;
        QcAnalyteViewDO qcaVDO;
        ResultViewDO rVDO;
        SampleManager1 sManager;
        SectionPermission userPermission;
        StringBuffer description;
        TestAnalyteViewDO taVDO;
        WorksheetAnalysisViewDO waVDO;
        WorksheetItemDO itemDO;
        ValidationErrorsList errors;
        
        validate(wm);

        locked = false;
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
                if (getAnalyses(wm) != null) {
                    for (WorksheetAnalysisViewDO data : getAnalyses(wm)) {
                        if (!testMethodNames.containsKey(data.getTestId()) && data.getTestId() != null)
                            testMethodNames.put(data.getTestId(), data.getTestName() + 
                                                                  ", " +data.getMethodName());
                    }
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
        wItems = new HashMap<Integer, WorksheetItemDO>();
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
            wItems.put(data.getId(), data);
        }
        
        analysisIds = new HashSet<Integer>();
        initAnalysisIds = new HashSet<Integer>();
        updateAnalysisIds = new HashSet<Integer>();
        updatedWorksheetAnalyses = new HashMap<Integer, WorksheetAnalysisViewDO>();
        if (getAnalyses(wm) != null) {
            for (WorksheetAnalysisViewDO data : getAnalyses(wm)) {
                if (data.getAnalysisId() != null) {
                    if (WorksheetManager1.ANALYSIS_UPDATE.FAILED_RUN.equals(updateFlag) ||
                        WorksheetManager1.ANALYSIS_UPDATE.VOID.equals(updateFlag)) {
                        updateAnalysisIds.add(data.getAnalysisId());
                        if (!updatedWorksheetAnalyses.containsKey(data.getAnalysisId()))
                            updatedWorksheetAnalyses.put(data.getAnalysisId(), data);
                    } else if (WorksheetManager1.ANALYSIS_UPDATE.UPDATE.equals(updateFlag)) {
                        if (data.getId() < 0 && data.getFromOtherId() == null) {
                            initAnalysisIds.add(data.getAnalysisId());
                            updatedWorksheetAnalyses.put(data.getAnalysisId(), data);
                            updateAnalysisIds.add(data.getAnalysisId());
                        } else if ((data.isChanged() || data.isStatusChanged() || data.isUnitChanged()) &&
                                   !updatedWorksheetAnalyses.containsKey(data.getAnalysisId())) {
                            updatedWorksheetAnalyses.put(data.getAnalysisId(), data);
                            updateAnalysisIds.add(data.getAnalysisId());
                        }
                    }
                    analysisIds.add(data.getAnalysisId());
                }
            }
        }

        newResults = new HashMap<Integer, ArrayList<ResultViewDO>>();
        updatedResults = new HashMap<Integer, ResultViewDO>();
        if (WorksheetManager1.ANALYSIS_UPDATE.UPDATE.equals(updateFlag) && wm.getModifiedResults() != null) {
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
            wm.setModifiedResults(null);
        }

        createdDate = getWorksheet(wm).getCreatedDate();
        if (!updateAnalysisIds.isEmpty()) {
            sMans = sampleMan.fetchForUpdateByAnalyses(new ArrayList<Integer>(updateAnalysisIds),
                                                       SampleManager1.Load.ORGANIZATION,
                                                       SampleManager1.Load.QA,
                                                       SampleManager1.Load.SINGLERESULT);
            for (SampleManager1 sMan : sMans) {
                for (AnalysisViewDO ana : SampleManager1Accessor.getAnalyses(sMan)) {
                    if (updateAnalysisIds.contains(ana.getId())) {
                        userPermission = userCache.getPermission().getSection(ana.getSectionName());
                        if (userPermission == null || !userPermission.hasCompletePermission())
                            throw new EJBException(Messages.get()
                                                           .analysis_noCompletePermission(DataBaseUtil.toString(sMan.getSample()
                                                                                                                    .getAccessionNumber()),
                                                                                          ana.getTestName(),
                                                                                          ana.getMethodName()));
                        if (WorksheetManager1.ANALYSIS_UPDATE.FAILED_RUN.equals(updateFlag)) {
                            if (Constants.dictionary().ANALYSIS_INITIATED.equals(ana.getStatusId()) ||
                                Constants.dictionary().ANALYSIS_ERROR_INITIATED.equals(ana.getStatusId()))
                                aHelper.changeAnalysisStatus(sMan, ana.getId(), Constants.dictionary().ANALYSIS_REQUEUE);
                        } else if (WorksheetManager1.ANALYSIS_UPDATE.VOID.equals(updateFlag)) {
                            if (Constants.dictionary().ANALYSIS_INITIATED.equals(ana.getStatusId()) ||
                                Constants.dictionary().ANALYSIS_ERROR_INITIATED.equals(ana.getStatusId())) {
                                startedDate = ana.getStartedDate();
                                if (startedDate == null || !startedDate.before(createdDate))
                                    aHelper.changeAnalysisStatus(sMan, ana.getId(), Constants.dictionary().ANALYSIS_LOGGED_IN);
                            }
                        } else {
                            waVDO = updatedWorksheetAnalyses.get(ana.getId());
                            if (initAnalysisIds.contains(ana.getId())) {
                                if (!Constants.dictionary().ANALYSIS_COMPLETED.equals(ana.getStatusId()) &&
                                    !Constants.dictionary().ANALYSIS_ERROR_COMPLETED.equals(ana.getStatusId()))
                                     aHelper.changeAnalysisStatus(sMan, ana.getId(), Constants.dictionary().ANALYSIS_INITIATED);
                            } else if (DataBaseUtil.isDifferent(waVDO.getStatusId(),
                                                                ana.getStatusId())) {
                                aHelper.changeAnalysisStatus(sMan, ana.getId(), waVDO.getStatusId());
                            } 
                            if (DataBaseUtil.isDifferent(waVDO.getUnitOfMeasureId(),
                                                         ana.getUnitOfMeasureId()))
                                aHelper.changeAnalysisUnit(sMan, ana.getId(), waVDO.getUnitOfMeasureId());

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
                    }
                }
                if (WorksheetManager1.ANALYSIS_UPDATE.UPDATE.equals(updateFlag)) {
                    for (ResultViewDO res : SampleManager1Accessor.getResults(sMan)) {
                        if (updatedResults.containsKey(res.getId())) {
                            rVDO = updatedResults.get(res.getId());
                            if (DataBaseUtil.isDifferent(rVDO.getIsReportable(), res.getIsReportable()))
                                res.setIsReportable(rVDO.getIsReportable());
                        }
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
        resultHash = new HashMap<Integer, ArrayList<ResultViewDO>>();
        if (!analysisIds.isEmpty()) {
            sMans = sampleMan.fetchByAnalyses(new ArrayList<Integer>(analysisIds), SampleManager1.Load.SINGLERESULT);
            for (SampleManager1 sMan : sMans) {
                for (AnalysisViewDO aVDO : SampleManager1Accessor.getAnalyses(sMan))
                    sMansByAnalysisId.put(aVDO.getId(), sMan);
                for (ResultViewDO res : SampleManager1Accessor.getResults(sMan)) {
                    results = resultHash.get(res.getAnalysisId());
                    if (results == null) {
                        results = new ArrayList<ResultViewDO>();
                        resultHash.put(res.getAnalysisId(), results);
                    }
                    results.add(res);
                }
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
        analyteMap = new HashMap<Integer, AnalyteDO>();
        excludedMap = new HashMap<Integer, ArrayList<Integer>>();
        formatColumnMap = new HashMap<String, Integer>();
        wResultHash = new HashMap<Integer, ArrayList<WorksheetResultViewDO>>();
        for (IdNameVO column : getColumnNames(wm.getWorksheet().getFormatId()))
            formatColumnMap.put(column.getName(), column.getId() - 10);
        if (getResults(wm) != null) {
            for (WorksheetResultViewDO res : getResults(wm)) {
                wResults = wResultHash.get(res.getWorksheetAnalysisId());
                if (wResults == null) {
                    wResults = new ArrayList<WorksheetResultViewDO>();
                    wResultHash.put(res.getWorksheetAnalysisId(), wResults);
                }
                wResults.add(res);
            }
        }
        do {
            ldep = dep;
            dep = 0;
            // add/update worksheet analysis
            if (getAnalyses(wm) != null) {
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
                                        synchronizeResults(wm,
                                                           data,
                                                           resultHash.get(aVDO.getId()),
                                                           wResultHash.get(data.getId()),
                                                           excludedIds,
                                                           formatColumnMap,
                                                           analyteMap);
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
                                itemDO = wItems.get(data.getWorksheetItemId());
                                if (data.getAccessionNumber().startsWith("X."))
                                    data.setAccessionNumber(DataBaseUtil.concatWithSeparator(getWorksheet(wm).getId(),
                                                                                             ".",
                                                                                             itemDO.getPosition()));
                                data.setStartedDate(Datetime.getInstance(Datetime.YEAR,
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
            qcAnalyteMap = new HashMap<Integer, QcAnalyteViewDO>();
            for (WorksheetQcResultViewDO data : getQcResults(wm)) {
                if (data.getId() < 0) {
                    data.setWorksheetAnalysisId(amap.get(data.getWorksheetAnalysisId()));
                    qcaVDO = qcAnalyteMap.get(data.getQcAnalyteId());
                    if (qcaVDO == null) {
                        try {
                            qcaVDO = qcAnalyte.fetchById(data.getQcAnalyteId());
                            qcAnalyteMap.put(data.getQcAnalyteId(), qcaVDO);
                        } catch (Exception anyE) {
                            log.severe("Error looking up analyte for worksheet: "+anyE.getMessage());
                            qcaVDO = null;
                        }
                    }
                    if (qcaVDO != null) {
                        col = formatColumnMap.get("expected_value");
                        if (col != null && (data.getValueAt(col) == null || data.getValueAt(col).length() == 0))
                            data.setValueAt(col, qcaVDO.getValue());
                    }
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
        ArrayList<WorksheetAnalysisViewDO> newAnalyses, removed, waVDOs;
        ArrayList<WorksheetItemDO> items, newItems;
        ArrayList<WorksheetResultViewDO> wrVDOs;
        ArrayList<WorksheetQcResultViewDO> wqrVDOs;
        HashMap<String, ArrayList<QcLotViewDO>> qcMap;
        HashMap<Integer, ArrayList<WorksheetAnalysisViewDO>> waVDOsByItemId;
        HashMap<Integer, ArrayList<WorksheetResultViewDO>> wrVDOsByAnalysisId;
        HashMap<Integer, ArrayList<WorksheetQcResultViewDO>> wqrVDOsByAnalysisId;
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
            getWorksheet(wm).setFormatId(Constants.dictionary().WF_TOTAL);
            getWorksheet(wm).setSubsetCapacity(500);
            return wqcVO;
        } else {
            wm.setTotalCapacity(twDO.getTotalCapacity());
            getWorksheet(wm).setFormatId(twDO.getFormatId());
            getWorksheet(wm).setSubsetCapacity(twDO.getSubsetCapacity());
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
        waVDOsByItemId = new HashMap<Integer, ArrayList<WorksheetAnalysisViewDO>>();
        if (getAnalyses(wm) != null) {
            for (WorksheetAnalysisViewDO data : getAnalyses(wm)) {
                waVDOs = waVDOsByItemId.get(data.getWorksheetItemId());
                if (waVDOs == null) {
                    waVDOs = new ArrayList<WorksheetAnalysisViewDO>();
                    waVDOsByItemId.put(data.getWorksheetItemId(), waVDOs);
                }
                waVDOs.add(data);
            }
        }
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
                    waVDO1 = waVDOsByItemId.get(newItems.get(i - 1).getId()).get(0);
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
                    addAnalysis(wm, waVDO);
                    waVDOs = waVDOsByItemId.get(waVDO.getWorksheetItemId());
                    if (waVDOs == null) {
                        waVDOs = new ArrayList<WorksheetAnalysisViewDO>();
                        waVDOsByItemId.put(waVDO.getWorksheetItemId(), waVDOs);
                    }
                    waVDOs.add(waVDO);
                    newAnalyses.add(waVDO);
                    newItems.add(wiDO);
                } else {
                    waVDO.setAccessionNumber("X." + wiDO.getPosition());
                    try {
                        loadQcForAnalysis(wm, twiDO, i, waVDO, prefs, wqcVO, qcMap);
                        waVDOs = waVDOsByItemId.get(waVDO.getWorksheetItemId());
                        if (waVDOs == null) {
                            waVDOs = new ArrayList<WorksheetAnalysisViewDO>();
                            waVDOsByItemId.put(waVDO.getWorksheetItemId(), waVDOs);
                        }
                        waVDOs.add(waVDO);
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
                waVDOs = waVDOsByItemId.get(waVDO.getWorksheetItemId());
                if (waVDOs == null) {
                    waVDOs = new ArrayList<WorksheetAnalysisViewDO>();
                    waVDOsByItemId.put(waVDO.getWorksheetItemId(), waVDOs);
                }
                waVDOs.add(waVDO);
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
                    waVDOs = waVDOsByItemId.get(waVDO.getWorksheetItemId());
                    if (waVDOs == null) {
                        waVDOs = new ArrayList<WorksheetAnalysisViewDO>();
                        waVDOsByItemId.put(waVDO.getWorksheetItemId(), waVDOs);
                    }
                    waVDOs.add(waVDO);
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

        wrVDOsByAnalysisId = new HashMap<Integer, ArrayList<WorksheetResultViewDO>>();
        if (getResults(wm) != null) {
            for (WorksheetResultViewDO data : getResults(wm)) {
                wrVDOs = wrVDOsByAnalysisId.get(data.getWorksheetAnalysisId());
                if (wrVDOs == null) {
                    wrVDOs = new ArrayList<WorksheetResultViewDO>();
                    wrVDOsByAnalysisId.put(data.getWorksheetAnalysisId(), wrVDOs);
                }
                wrVDOs.add(data);
            }
        }
        
        wqrVDOsByAnalysisId = new HashMap<Integer, ArrayList<WorksheetQcResultViewDO>>();
        if (getQcResults(wm) != null) {
            for (WorksheetQcResultViewDO data : getQcResults(wm)) {
                wqrVDOs = wqrVDOsByAnalysisId.get(data.getWorksheetAnalysisId());
                if (wqrVDOs == null) {
                    wqrVDOs = new ArrayList<WorksheetQcResultViewDO>();
                    wqrVDOsByAnalysisId.put(data.getWorksheetAnalysisId(), wqrVDOs);
                }
                wqrVDOs.add(data);
            }
        }

        if (j < items.size()) {
            removed = new ArrayList<WorksheetAnalysisViewDO>();
            while (j < items.size()) {
                wiDO = items.get(j);
                for (WorksheetAnalysisViewDO aData : waVDOsByItemId.get(wiDO.getId())) {
                    if (aData.getAnalysisId() != null) {
                        for (WorksheetResultViewDO rData : wrVDOsByAnalysisId.get(aData.getId())) {
                            getResults(wm).remove(rData);
                            if (rData.getId() > 0)
                                getRemoved(wm).add(rData);
                        }
                    } else if (aData.getQcLotId() != null) {
                        for (WorksheetQcResultViewDO rData : wqrVDOsByAnalysisId.get(aData.getId())) {
                            getQcResults(wm).remove(rData);
                            if (rData.getId() > 0)
                                getRemoved(wm).add(rData);
                        }
                    }
                    removed.add(aData);
                    getAnalyses(wm).remove(aData);
                    if (aData.getId() > 0)
                        getRemoved(wm).add(aData);
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
                        wrVDO = new WorksheetResultViewDO();
                        wrVDO.setId(wm.getNextUID());
                        wrVDO.setWorksheetAnalysisId(waVDO.getId());
                        wrVDO.setTestAnalyteId(resultDO.getTestAnalyteId());
                        wrVDO.setResultRow(i);
                        wrVDO.setAnalyteId(resultDO.getAnalyteId());
                        wrVDO.setAnalyteName(resultDO.getAnalyte());
                        addResult(wm, wrVDO);
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
                        wqrVDO = new WorksheetQcResultViewDO();
                        wqrVDO.setId(wm.getNextUID());
                        wqrVDO.setWorksheetAnalysisId(waVDO.getId());
                        wqrVDO.setSortOrder(qcaVDO.getSortOrder());
                        wqrVDO.setQcAnalyteId(qcaVDO.getId());
                        wqrVDO.setAnalyteId(qcaVDO.getAnalyteId());
                        wqrVDO.setAnalyteName(qcaVDO.getAnalyteName());
                        addQcResult(wm, wqrVDO);
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
            columnNames = getColumnNames(fromWorksheet.getFormatId());
            for (IdNameVO vo : columnNames)
                fromColumnMap.put(vo.getId(), vo.getName());
            columnNames = getColumnNames(toWorksheet.getFormatId());
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
                        toWrVDO = new WorksheetResultViewDO();
                        toWrVDO.setId(wm.getNextUID());
                        toWrVDO.setWorksheetAnalysisId(toWaVDO.getId());
                        toWrVDO.setTestAnalyteId(fromWrVDO.getTestAnalyteId());
                        toWrVDO.setResultRow(fromWrVDO.getResultRow());
                        toWrVDO.setAnalyteId(fromWrVDO.getAnalyteId());
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
                        addResult(wm, toWrVDO);
                    }
                } catch (NotFoundException nfE) {
                    log.log(Level.INFO, nfE.getMessage());
                }
            } else if (fromWaVDO.getQcLotId() != null) {
                try {
                    fromWqrVDOs = wqResult.fetchByWorksheetAnalysisId(fromWaVDO.getId());
                    for (WorksheetQcResultViewDO fromWqrVDO : fromWqrVDOs) {
                        toWqrVDO = new WorksheetQcResultViewDO();
                        toWqrVDO.setId(wm.getNextUID());
                        toWqrVDO.setWorksheetAnalysisId(toWaVDO.getId());
                        toWqrVDO.setSortOrder(fromWqrVDO.getSortOrder());
                        toWqrVDO.setQcAnalyteId(fromWqrVDO.getQcAnalyteId());
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
                        addQcResult(wm, toWqrVDO);
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
        
        i = 1;
        for (WorksheetItemDO wiDO : getItems(wm))
            wiDO.setPosition(i++);

        return wm;
    }
    
    public ArrayList<IdNameVO> getHeaderLabelsForScreen(Integer formatId) throws Exception {
        int i;
        ArrayList<DictionaryDO> names;
        ArrayList<IdNameVO> headers;
        CategoryCacheVO categoryVO;
        DictionaryDO formatDO;

        headers = new ArrayList<IdNameVO>();
        
        try {
            formatDO = dictionary.getById(formatId);
        } catch (NotFoundException nfE) {
            formatDO = new DictionaryDO();
            formatDO.setSystemName("wf_default");
        } catch (Exception anyE) {
            throw new Exception("Error retrieving worksheet format: " + anyE.getMessage());
        }

        try {
            categoryVO = category.getBySystemName(formatDO.getSystemName());
            names = categoryVO.getDictionaryList();
        } catch (Exception anyE) {
            throw new Exception("Error retrieving worksheet column names: " + anyE.getMessage());
        }

        for (i = 0; i < names.size() && i < 30; i++)
            headers.add(new IdNameVO(i, names.get(i).getEntry()));

        return headers;
    }

    public ArrayList<IdNameVO> getColumnNames(Integer formatId) throws Exception {
        int i;
        ArrayList<DictionaryDO> columnDOs;
        ArrayList<IdNameVO> columnNames;
        CategoryCacheVO categoryVO;
        DictionaryDO columnDO, formatDO;
        String columnName;

        columnNames = new ArrayList<IdNameVO>();

        try {
            formatDO = dictionary.getById(formatId);
        } catch (NotFoundException nfE) {
            try {
                formatDO = dictionary.getBySystemName("wf_total");
            } catch (Exception anyE1) {
                throw new Exception("Error retrieving worksheet format: " + anyE1.getMessage());
            }
        } catch (Exception anyE) {
            throw new Exception("Error retrieving worksheet format: " + anyE.getMessage());
        }

        try {
            categoryVO = category.getBySystemName(formatDO.getSystemName());
            columnDOs = categoryVO.getDictionaryList();
        } catch (Exception anyE) {
            throw new Exception("Error retrieving worksheet column names: " + anyE.getMessage());
        }
        
        for (i = 0; i < columnDOs.size(); i++) {
            columnDO = columnDOs.get(i);
            columnName = columnDO.getSystemName().substring(formatDO.getSystemName().length() + 1);
            columnNames.add(new IdNameVO(10 + i, columnName));
        }

        return columnNames;
    }

    public WorksheetResultsTransferVO transferResults(WorksheetManager1 manager,
                                                      ArrayList<WorksheetAnalysisViewDO> waVDOs,
                                                      ArrayList<SampleManager1> sampleMans) throws Exception {
        boolean update;
        int i, c;
        AnalysisUserViewDO auVDO;
        AnalysisViewDO aVDO;
        ArrayList<ArrayList<ResultViewDO>> resultRows, analyteRows, reflexResultsList;
        ArrayList<IdNameVO> formatColumns;
        ArrayList<Integer> unlockIds, newAnalyteIndexes;
        ArrayList<ResultViewDO> resultRow, reflexResults;
        ArrayList<SampleManager1> reflexMans;
        ArrayList<TestAnalyteViewDO> newAnalytes;
        ArrayList<WorksheetResultViewDO> wrVDOs, newAnalyteWRVDOs;
        HashMap<Integer, HashMap<Integer, TestAnalyteViewDO>> testTestAnalyteMap;
        HashMap<Integer, AnalyteDO> analytesById;
        HashMap<Integer, AnalysisViewDO> analysesById;
        HashMap<Integer, ArrayList<ArrayList<ResultViewDO>>> resultsByAnalysisId, resultRowsByAnalyteId;
        HashMap<Integer, SampleManager1> sampleMansByAnalysisId;
        HashMap<Integer, TestAnalyteViewDO> taMap;
        HashMap<Integer, TestManager> tManMap;
        HashMap<Integer, ArrayList<WorksheetResultViewDO>> wrVDOsByWorksheetAnalysisId;
        HashMap<String, Integer> formatColumnMap;
        HashSet<Integer> updateIds, completeUserIds;
        Integer lastAnaId;
        Iterator<SampleManager1> smIter;
        ResultFormatter rf;
        ResultViewDO rVDO;
        SampleManager1 sMan;
        SystemUserVO userVO;
        TestManager tMan;
        ValidationErrorsList errorList;
        WorksheetItemDO wiDO;
        
        errorList = new ValidationErrorsList();
        
        formatColumnMap = new HashMap<String, Integer>();
        formatColumns = getColumnNames(manager.getWorksheet().getFormatId());
        for (IdNameVO column : formatColumns)
            formatColumnMap.put(column.getName(), column.getId() - 10);
        
        wrVDOsByWorksheetAnalysisId = new HashMap<Integer, ArrayList<WorksheetResultViewDO>>();
        for (WorksheetResultViewDO wrVDO : getResults(manager)) {
            wrVDOs = wrVDOsByWorksheetAnalysisId.get(wrVDO.getWorksheetAnalysisId());
            if (wrVDOs == null) {
                wrVDOs = new ArrayList<WorksheetResultViewDO>();
                wrVDOsByWorksheetAnalysisId.put(wrVDO.getWorksheetAnalysisId(), wrVDOs);
            }
            wrVDOs.add(wrVDO);
        }
        
        analysesById = new HashMap<Integer, AnalysisViewDO>();
        sampleMansByAnalysisId = new HashMap<Integer, SampleManager1>();
        resultsByAnalysisId = new HashMap<Integer, ArrayList<ArrayList<ResultViewDO>>>();
        for (SampleManager1 sData : sampleMans) {
            for (AnalysisViewDO aData : SampleManager1Accessor.getAnalyses(sData)) {
                analysesById.put(aData.getId(), aData);
                sampleMansByAnalysisId.put(aData.getId(), sData);
            }
            lastAnaId = -1;
            resultRow = null;
            resultRows = null;
            for (ResultViewDO rData : SampleManager1Accessor.getResults(sData)) {
                if (!lastAnaId.equals(rData.getAnalysisId())) {
                    resultRows = new ArrayList<ArrayList<ResultViewDO>>();
                    resultsByAnalysisId.put(rData.getAnalysisId(), resultRows);
                    lastAnaId = rData.getAnalysisId();
                }
                if ("N".equals(rData.getIsColumn())) {
                    resultRow = new ArrayList<ResultViewDO>();
                    resultRows.add(resultRow);
                }
                resultRow.add(rData);
            }
        }
        
        updateIds = new HashSet<Integer>();
        completeUserIds = new HashSet<Integer>();
        tManMap = new HashMap<Integer, TestManager>();
        testTestAnalyteMap = new HashMap<Integer, HashMap<Integer, TestAnalyteViewDO>>();
        analytesById = new HashMap<Integer, AnalyteDO>();
        resultRowsByAnalyteId = new HashMap<Integer, ArrayList<ArrayList<ResultViewDO>>>();
        newAnalytes = new ArrayList<TestAnalyteViewDO>();
        newAnalyteIndexes = new ArrayList<Integer>();
        newAnalyteWRVDOs = new ArrayList<WorksheetResultViewDO>();
        reflexMans = new ArrayList<SampleManager1>();
        reflexResultsList = new ArrayList<ArrayList<ResultViewDO>>();
        for (WorksheetAnalysisViewDO waVDO : waVDOs) {
            update = false;
            if (Constants.dictionary().ANALYSIS_RELEASED.equals(waVDO.getStatusId()) ||
                Constants.dictionary().ANALYSIS_CANCELLED.equals(waVDO.getStatusId()))
                continue;

            wiDO = (WorksheetItemDO)manager.getObject(manager.getWorksheetItemUid(waVDO.getWorksheetItemId()));
            aVDO = analysesById.get(waVDO.getAnalysisId());
            sMan = sampleMansByAnalysisId.get(waVDO.getAnalysisId());
            
            if (waVDO.getSystemUsers() != null && waVDO.getSystemUsers().length() > 0) {
                completeUserIds.clear();
                for (AnalysisUserViewDO auData : SampleManager1Accessor.getUsers(sMan)) {
                    if (aVDO.getId().equals(auData.getAnalysisId()) &&
                        Constants.dictionary().AN_USER_AC_COMPLETED.equals(auData.getActionId()))
                        completeUserIds.add(auData.getSystemUserId());
                }
                for (String userName : waVDO.getSystemUsers().split(",")) {
                    userVO = userCache.getSystemUser(userName);
                    if (userVO != null) {
                        if (!completeUserIds.contains(userVO.getId())) {
                            auVDO = new AnalysisUserViewDO();
                            auVDO.setActionId(Constants.dictionary().AN_USER_AC_COMPLETED);
                            auVDO.setSystemUserId(userVO.getId());
                            auVDO.setSystemUser(userVO.getLoginName());
                            SampleManager1Accessor.getUsers(sMan).add(auVDO);
                            update = true;
                        }
                    } else {
                        errorList.add(new FormErrorException(Messages.get().worksheet_illegalWorksheetUserFormException(userName,
                                                                                                                        String.valueOf(wiDO.getPosition()),
                                                                                                                        "'" +
                                                                                                                        waVDO.getTestName() +
                                                                                                                        ", " +
                                                                                                                        waVDO.getMethodName())));
                    }
                }
            }
            
            if (waVDO.getStartedDate() != null &&
                DataBaseUtil.isDifferent(waVDO.getStartedDate(), aVDO.getStartedDate())) {
                aVDO.setStartedDate(waVDO.getStartedDate());
                update = true;
            }
            
            if (waVDO.getCompletedDate() != null &&
                DataBaseUtil.isDifferent(waVDO.getCompletedDate(), aVDO.getCompletedDate())) {
                aVDO.setCompletedDate(waVDO.getCompletedDate());
                update = true;
            }

            if (DataBaseUtil.isDifferent(waVDO.getUnitOfMeasureId(), aVDO.getUnitOfMeasureId())) {
                try {
                    aHelper.changeAnalysisUnit(sMan, aVDO.getId(), waVDO.getUnitOfMeasureId());
                    update = true;
                } catch (Exception anyE) {
                    errorList.add(new FormErrorException(Messages.get().worksheet_errorChangingAnalysisUnit(String.valueOf(wiDO.getPosition()),
                                                                                                            "'" +
                                                                                                            waVDO.getTestName() +
                                                                                                            ", " +
                                                                                                            waVDO.getMethodName(),
                                                                                                            anyE.getMessage())));
                }
            }

            if (!Constants.dictionary().ANALYSIS_REQUEUE.equals(waVDO.getStatusId())) {
                tMan = tManMap.get(aVDO.getTestId());
                taMap = testTestAnalyteMap.get(aVDO.getTestId());
                if (tMan == null) {
                    try {
                        tMan = testManager.fetchById(aVDO.getTestId());
                        tManMap.put(aVDO.getTestId(), tMan);
                        taMap = new HashMap<Integer, TestAnalyteViewDO>();
                        for (ArrayList<TestAnalyteViewDO> taRow : tMan.getTestAnalytes().getAnalytes())
                            taMap.put(taRow.get(0).getId(), taRow.get(0));
                        testTestAnalyteMap.put(aVDO.getTestId(), taMap);
                    } catch (Exception anyE) {
                        errorList.add(new FormErrorException(Messages.get().worksheet_errorLoadingResultFormatter("'" +
                                                                                                                  waVDO.getTestName() +
                                                                                                                  ", " +
                                                                                                                  waVDO.getMethodName(),
                                                                                                                  anyE.getMessage())));
                        continue;
                    }
                }
                rf = tMan.getFormatter();

                resultRowsByAnalyteId.clear();
                resultRows = resultsByAnalysisId.get(waVDO.getAnalysisId());
                for (ArrayList<ResultViewDO> rowData : resultRows) {
                    rVDO = rowData.get(0);
                    analyteRows = resultRowsByAnalyteId.get(rVDO.getAnalyteId());
                    if (analyteRows == null) {
                        analyteRows = new ArrayList<ArrayList<ResultViewDO>>();
                        resultRowsByAnalyteId.put(rVDO.getAnalyteId(), analyteRows);
                    }
                    analyteRows.add(rowData);
                }
                
                newAnalytes.clear();
                newAnalyteIndexes.clear();
                newAnalyteWRVDOs.clear();
                reflexResults = new ArrayList<ResultViewDO>();
                for (WorksheetResultViewDO wrVDO : wrVDOsByWorksheetAnalysisId.get(waVDO.getId())) {
                    analyteRows = resultRowsByAnalyteId.get(wrVDO.getAnalyteId());
                    if (analyteRows != null) {
                        resultRow = analyteRows.get(0);
                        update = worksheetResultToAnalysisResult(manager, wrVDO, resultRow,
                                                                 aVDO, rf, formatColumnMap,
                                                                 analytesById, reflexResults,
                                                                 errorList) ||
                                 update;
                        
                        analyteRows.remove(0);
                        if (analyteRows.size() == 0)
                            resultRowsByAnalyteId.remove(wrVDO.getAnalyteId());
                    } else {
                        newAnalytes.add(taMap.get(wrVDO.getTestAnalyteId()));
                        newAnalyteIndexes.add(wrVDO.getResultRow());
                        newAnalyteWRVDOs.add(wrVDO);
                    }
                }

                newAnalyteIndexes = aHelper.addRowAnalytes(sMan, aVDO, newAnalytes, newAnalyteIndexes);
                if (newAnalyteWRVDOs.size() != newAnalyteIndexes.size()) {
                    errorList.add(new FormErrorException(Messages.get().worksheet_errorLoadingAdditionalAnalytes(String.valueOf(wiDO.getPosition()),
                                                                                                                 waVDO.getTestName(),
                                                                                                                 waVDO.getMethodName())));
                    continue;
                }
                
                for (i = 0; i < newAnalyteIndexes.size(); i++) {
                    resultRow = new ArrayList<ResultViewDO>();
                    c = newAnalyteIndexes.get(i);
                    rVDO = SampleManager1Accessor.getResults(sMan).get(c);
                    do {
                        resultRow.add(rVDO);
                        rVDO = SampleManager1Accessor.getResults(sMan).get(++c);
                    } while ("Y".equals(rVDO.getIsColumn())) ;
                    resultRows.add(resultRow);
                    
                    update = worksheetResultToAnalysisResult(manager, newAnalyteWRVDOs.get(i),
                                                             resultRow, aVDO, rf,
                                                             formatColumnMap, analytesById,
                                                             reflexResults, errorList) ||
                             update;
                }
                
                if (reflexResults.size() > 0) {
                    reflexMans.add(sMan);
                    reflexResultsList.add(reflexResults);
                }
            }
            
            try {
                if (Constants.dictionary().ANALYSIS_LOGGED_IN.equals(waVDO.getStatusId()) ||
                    Constants.dictionary().ANALYSIS_INITIATED.equals(waVDO.getStatusId()) ||
                    Constants.dictionary().ANALYSIS_COMPLETED.equals(waVDO.getStatusId())) {
                    aHelper.changeAnalysisStatus(sMan, aVDO.getId(), Constants.dictionary().ANALYSIS_COMPLETED);
                    update = true;
                } else if (DataBaseUtil.isDifferent(waVDO.getStatusId(), aVDO.getStatusId())) {
                    aHelper.changeAnalysisStatus(sMan, aVDO.getId(), waVDO.getStatusId());
                    update = true;
                }
            } catch (Exception anyE) {
                errorList.add(new FormErrorException(Messages.get().worksheet_errorChangingAnalysisStatus(String.valueOf(wiDO.getPosition()),
                                                                                                          "'" +
                                                                                                          waVDO.getTestName() +
                                                                                                          ", " +
                                                                                                          waVDO.getMethodName(),
                                                                                                          anyE.getMessage())));
            }
            
            if (update)
                updateIds.add(sMan.getSample().getId());
        }
        
        if (errorList.size() > 0)
            throw errorList;
        
        unlockIds = new ArrayList<Integer>();
        smIter = sampleMans.iterator();
        while (smIter.hasNext()) {
            sMan = smIter.next();
            if (!updateIds.contains(sMan.getSample().getId())) {
                unlockIds.add(sMan.getSample().getId());
                smIter.remove();
            } else if (reflexMans.contains(sMan)) {
                smIter.remove();
            }
        }

        sampleMan.update(sampleMans, true);
        
        if (unlockIds.size() > 0)
            lock.unlock(Constants.table().SAMPLE, unlockIds);
        
        if (reflexMans.size() > 0)
            return new WorksheetResultsTransferVO(manager, reflexMans, reflexResultsList);
        else
            return new WorksheetResultsTransferVO(unlock(manager.getWorksheet().getId(),
                                                         WorksheetManager1.Load.DETAIL,
                                                         WorksheetManager1.Load.NOTE), null);
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
                waVDO.setQcId(qcsByLoc.get(0).getQcId());
                waVDO.setDescription(qcsByLoc.get(0).getQcName() + " (" +
                                     qcsByLoc.get(0).getLotNumber() + ")");
                addAnalysis(wm, waVDO);
            } else {
                uids = new ArrayList<String>();
                for (i = 0; i < qcs.size(); i++)
                    uids.add(wm.getUid(waVDO));
                wqcVO.addChoices(qcs);
                wqcVO.addChoiceUids(uids);

                waVDO.setQcLotId(qcs.get(0).getId());
                waVDO.setQcId(qcs.get(0).getQcId());
                waVDO.setDescription(qcs.get(0).getQcName() + " (" + qcs.get(0).getLotNumber() + ")");
                addAnalysis(wm, waVDO);
            }
        } else {
            waVDO.setQcLotId(qcs.get(0).getId());
            waVDO.setDescription(qcs.get(0).getQcName() + " (" + qcs.get(0).getLotNumber() + ")");
            addAnalysis(wm, waVDO);
        }
    }
    
    private void synchronizeResults(WorksheetManager1 wm, WorksheetAnalysisViewDO waVDO,
                                    ArrayList<ResultViewDO> results,
                                    ArrayList<WorksheetResultViewDO> wResults,
                                    ArrayList<Integer> excludedIds,
                                    HashMap<String, Integer> formatColumnMap,
                                    HashMap<Integer, AnalyteDO> analyteMap) throws Exception {
        int i, rowIndex;
        AnalyteDO anaDO;
        ArrayList<WorksheetResultViewDO> wrList;
        DictionaryDO dDO;
        HashMap<Integer, ArrayList<WorksheetResultViewDO>> wrMap;
        Integer col;
        ResultViewDO rVDO;
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
        
        rowIndex = -1;
        for (i = 0; i < results.size(); i++) {
            rVDO = results.get(i);
            if ("Y".equals(rVDO.getIsColumn()))
                continue;
            
            rowIndex++;
            if (excludedIds.contains(rVDO.getTestAnalyteId()))
                continue;
            
            wrList = wrMap.get(rVDO.getTestAnalyteId());
            if (wrList != null) {
                wrVDO = wrList.get(0);
                if (wrVDO.getResultRow() != rowIndex)
                    wrVDO.setResultRow(rowIndex);
                wrList.remove(0);
                if (wrList.size() == 0)
                    wrMap.remove(wrList);
            } else {
                wrVDO = new WorksheetResultViewDO();
                wrVDO.setId(wm.getNextUID());
                wrVDO.setWorksheetAnalysisId(waVDO.getId());
                wrVDO.setTestAnalyteId(rVDO.getTestAnalyteId());
                wrVDO.setResultRow(rowIndex);
                wrVDO.setAnalyteId(rVDO.getAnalyteId());
                wrVDO.setAnalyteName(rVDO.getAnalyte());
                addResult(wm, wrVDO);
            }
            
            col = formatColumnMap.get("final_value");
            if (col != null && wrVDO.getId() <= 0 && (wrVDO.getValueAt(col) == null || wrVDO.getValueAt(col).length() == 0)) {
                if (Constants.dictionary().TEST_RES_TYPE_DICTIONARY.equals(rVDO.getTypeId())) {
                    try {
                        dDO = dictionary.getById(Integer.valueOf(rVDO.getValue()));
                        wrVDO.setValueAt(col, dDO.getEntry());
                    } catch (Exception anyE) {
                        log.severe("Error copying result values to worksheet: "+anyE.getMessage());
                    }
                } else {
                    wrVDO.setValueAt(col, rVDO.getValue());
                }
            }
            
            i++;
            if (i < results.size()) {
                rVDO = results.get(i);
                while ("Y".equals(rVDO.getIsColumn())) {
                    anaDO = analyteMap.get(rVDO.getAnalyteId());
                    if (anaDO == null) {
                        try {
                            anaDO = analyte.fetchById(rVDO.getAnalyteId());
                            analyteMap.put(rVDO.getAnalyteId(), anaDO);
                        } catch (Exception anyE) {
                            log.severe("Error looking up analyte for worksheet: "+anyE.getMessage());
                            anaDO = null;
                        }
                    }
                    if (anaDO != null) {
                        col = formatColumnMap.get(anaDO.getExternalId());
                        if (col != null && wrVDO.getId() <= 0 && (wrVDO.getValueAt(col) == null || wrVDO.getValueAt(col).length() == 0)) {
                            if (Constants.dictionary().TEST_RES_TYPE_DICTIONARY.equals(rVDO.getTypeId())) {
                                try {
                                    dDO = dictionary.getById(Integer.valueOf(rVDO.getValue()));
                                    wrVDO.setValueAt(col, dDO.getEntry());
                                } catch (Exception anyE) {
                                    log.severe("Error copying result values to worksheet: "+anyE.getMessage());
                                }
                            } else {
                                wrVDO.setValueAt(col, rVDO.getValue());
                            }
                        }
                    }
                    i++;
                    if (i == results.size())
                        break;
                    rVDO = results.get(i);
                }
            }
            i--;
        }
    }
    
    private void unlockSamples(ArrayList<SampleManager1> sMans) throws Exception {
        ArrayList<Integer> sampleIds;
        
        sampleIds = new ArrayList<Integer>();
        for (SampleManager1 sMan : sMans)
            sampleIds.add(sMan.getSample().getId());
        sampleMan.unlock(sampleIds, SampleManager1.Load.SINGLERESULT);
    }
    
    private boolean worksheetResultToAnalysisResult(WorksheetManager1 wMan,
                                                    WorksheetResultViewDO wrVDO,
                                                    ArrayList<ResultViewDO> resultRow,
                                                    AnalysisViewDO aVDO, ResultFormatter rf,
                                                    HashMap<String, Integer> formatColumnMap,
                                                    HashMap<Integer, AnalyteDO> analytesById,
                                                    ArrayList<ResultViewDO> reflexResults,
                                                    ValidationErrorsList errorList) {
        boolean update;
        int c;
        AnalyteDO aDO;
        Integer wCol;
        ResultViewDO rVDO;
        WorksheetItemDO wiDO;
        WorksheetAnalysisViewDO waVDO;
        
        update = false;
        waVDO = (WorksheetAnalysisViewDO) wMan.getObject(wMan.getWorksheetAnalysisUid(wrVDO.getWorksheetAnalysisId()));
        wiDO = (WorksheetItemDO) wMan.getObject(wMan.getWorksheetItemUid(waVDO.getWorksheetItemId()));
        for (c = 0; c < resultRow.size(); c++) {
            rVDO = resultRow.get(c);
            if (c == 0) {
                wCol = formatColumnMap.get("final_value");
                try {
                    if (wCol != null && !DataBaseUtil.isEmpty(wrVDO.getValueAt(wCol))) {
                        ResultHelper.formatValue(rVDO, wrVDO.getValueAt(wCol), aVDO.getUnitOfMeasureId(), rf);
                        if (rVDO.isChanged())
                            reflexResults.add(rVDO);
                        update = true;
                    }
                } catch (Exception anyE) {
                    errorList.add(new FormErrorException(Messages.get().worksheet_errorPrefix(String.valueOf(wiDO.getPosition()),
                                                                                              "'" +
                                                                                              waVDO.getTestName() +
                                                                                              ", " +
                                                                                              waVDO.getMethodName(),
                                                                                              anyE.getMessage())));
                }
                if (DataBaseUtil.isDifferent(wrVDO.getIsReportable(), rVDO.getIsReportable())) {
                    rVDO.setIsReportable(wrVDO.getIsReportable());
                    update = true;
                }
            } else {
                aDO = analytesById.get(rVDO.getAnalyteId());
                if (aDO == null) {
                    try {
                        aDO = analyte.fetchById(rVDO.getAnalyteId());
                        analytesById.put(aDO.getId(), aDO);
                    } catch (Exception anyE) {
                        errorList.add(new FormErrorException(Messages.get().worksheet_errorPrefix(String.valueOf(wiDO.getPosition()),
                                                                                                  "'" +
                                                                                                  waVDO.getTestName() +
                                                                                                  ", " +
                                                                                                  waVDO.getMethodName(),
                                                                                                  anyE.getMessage())));
                        continue;
                    }
                }
                wCol = formatColumnMap.get(aDO.getExternalId());
                if (wCol != null) {
                    try {
                        if (!DataBaseUtil.isEmpty(wrVDO.getValueAt(wCol))) {
                            ResultHelper.formatValue(rVDO, wrVDO.getValueAt(wCol), aVDO.getUnitOfMeasureId(), rf);
                            if (rVDO.isChanged())
                                reflexResults.add(rVDO);
                            update = true;
                        }
                    } catch (Exception anyE) {
                        errorList.add(new FormErrorException(Messages.get().worksheet_errorPrefix(String.valueOf(wiDO.getPosition()),
                                                                                                  "'" +
                                                                                                  waVDO.getTestName() +
                                                                                                  ", " +
                                                                                                  waVDO.getMethodName(),
                                                                                                  anyE.getMessage())));
                    }
                }
            }
        }

        return update;
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