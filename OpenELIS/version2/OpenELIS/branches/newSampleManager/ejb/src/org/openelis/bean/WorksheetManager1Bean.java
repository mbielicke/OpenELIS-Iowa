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
import java.util.EnumSet;
import java.util.HashMap;
import java.util.logging.Logger;

import javax.annotation.security.RolesAllowed;
import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.jboss.security.annotation.SecurityDomain;
import org.openelis.constants.Messages;
import org.openelis.domain.Constants;
import org.openelis.domain.DataObject;
import org.openelis.domain.IdVO;
import org.openelis.domain.NoteViewDO;
import org.openelis.domain.WorksheetAnalysisDO;
import org.openelis.domain.WorksheetItemDO;
import org.openelis.domain.WorksheetQcResultViewDO;
import org.openelis.domain.WorksheetResultViewDO;
import org.openelis.domain.WorksheetViewDO;
import org.openelis.manager.WorksheetManager1;
import org.openelis.ui.common.DataBaseUtil;
import org.openelis.ui.common.Datetime;
import org.openelis.ui.common.FormErrorException;
import org.openelis.ui.common.InconsistencyException;
import org.openelis.ui.common.ValidationErrorsList;
import org.openelis.ui.common.data.QueryData;

@Stateless
@SecurityDomain("openelis")
public class WorksheetManager1Bean {

    @EJB
    private LockBean                     lock;

    @EJB
    private WorksheetBean                worksheet;

    @EJB
    private WorksheetItemBean            item;

    @EJB
    private WorksheetAnalysisBean        analysis;

    @EJB
    private WorksheetResultBean          result;

    @EJB
    private WorksheetQcResultBean        qcResult;

    @EJB
    private NoteBean                     note;

    @EJB
    private UserCacheBean                userCache;

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
        w.setSystemUser(userCache.getName());

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
            ids2 = new ArrayList<Integer>();
            map2 = new HashMap<Integer, WorksheetManager1>();
            for (WorksheetItemDO data : item.fetchByWorksheetIds(ids1)) {
                wm = map1.get(data.getWorksheetId());
                addItem(wm, data);
                if (!map2.containsKey(data.getId())) {
                    ids2.add(data.getId());
                    map2.put(data.getId(), wm);
                }
            }
    
            /*
             * build level 3, everything is based on analysis ids
             */
            ids1 = new ArrayList<Integer>();
            map1 = new HashMap<Integer, WorksheetManager1>();
            for (WorksheetAnalysisDO data : analysis.fetchByWorksheetItemIds(ids2)) {
                wm = map2.get(data.getWorksheetItemId());
                addAnalysis(wm, data);
                if (!map1.containsKey(data.getId())) {
                    ids1.add(data.getId());
                    map1.put(data.getId(), wm);
                }
            }
            ids2 = null;
            map2 = null;
    
            for (WorksheetResultViewDO data : result.fetchByWorksheetAnalysisIds(ids1)) {
                wm = map1.get(data.getWorksheetAnalysisId());
                addResult(wm, data);
            }

            for (WorksheetQcResultViewDO data : qcResult.fetchByWorksheetAnalysisIds(ids1)) {
                wm = map1.get(data.getWorksheetAnalysisId());
                addQcResult(wm, data);
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

        for (IdVO vo : worksheet.fetchByQuery(fields, first, max))
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
            for (WorksheetAnalysisDO data : analysis.fetchByWorksheetItemIds(ids)) {
                addAnalysis(wm, data);
                ids2.add(data.getId());
            }
    
            setResults(wm, null);
            for (WorksheetResultViewDO data : result.fetchByWorksheetAnalysisIds(ids2))
                addResult(wm, data);
            
            setQcResults(wm, null);
            for (WorksheetQcResultViewDO data : qcResult.fetchByWorksheetAnalysisIds(ids2))
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
        int dep, ldep;
        boolean locked, nodep;
        Integer tmpid, id;
        HashMap<Integer, Integer> imap, amap;

        validate(wm);
        
        locked = false;
        if (getWorksheet(wm).getId() != null) {
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
                if (data instanceof WorksheetItemDO)
                    item.delete((WorksheetItemDO)data);
                else if (data instanceof WorksheetAnalysisDO)
                    analysis.delete((WorksheetAnalysisDO)data);
                else if (data instanceof WorksheetResultViewDO)
                    result.delete((WorksheetResultViewDO)data);
                else if (data instanceof WorksheetQcResultViewDO)
                    qcResult.delete((WorksheetQcResultViewDO)data);
                else if (data instanceof NoteViewDO)
                    note.delete((NoteViewDO)data);
                else
                    throw new Exception("ERROR: DataObject passed for removal is of unknown type");
            }
        }

        // add/update worksheet
        if (getWorksheet(wm).getId() == null)
            worksheet.add(getWorksheet(wm));
        else
            worksheet.update(getWorksheet(wm));

        if (getNotes(wm) != null) {
            for (NoteViewDO data : getNotes(wm)) {
                if (data.getId() == null) {
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
                item.add(data);
            } else {
                tmpid = data.getId();
                item.update(data);
            }
            imap.put(tmpid, data.getId());
        }

        /*
         * some worksheet analyses can be dependent on other worksheet analyses
         * for qc link. This code tries to resolve those dependencies by
         * alternating between adding/updating worksheet analysis until all the
         * records have been added/updated. The code also detects infinite loops
         * by ensuring every iteration resolves some dependency
         */
        dep = ldep = 0;
        do {
            ldep = dep;
            dep = 0;
            // add/update worksheet analysis
            for (WorksheetAnalysisDO data : getAnalyses(wm)) {
                nodep = true;

                if (data.getWorksheetAnalysisId() != null && data.getWorksheetAnalysisId() < 0) {
                    id = amap.get(data.getWorksheetAnalysisId());
                    if (id != null)
                        data.setWorksheetAnalysisId(id);
                    else
                        nodep = false;
                }

                if (nodep) {
                    if (data.getId() < 0) {
                        tmpid = data.getId();
                        data.setWorksheetItemId(imap.get(data.getWorksheetItemId()));
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
                    result.add(data);
                } else {
                    result.update(data);
                }
            }
        }

        // add/update qc results
        if (getQcResults(wm) != null) {
            for (WorksheetQcResultViewDO data : getQcResults(wm)) {
                if (data.getId() < 0) {
                    data.setWorksheetAnalysisId(amap.get(data.getWorksheetAnalysisId()));
                    qcResult.add(data);
                } else {
                    qcResult.update(data);
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
        HashMap<Integer, WorksheetAnalysisDO> amap;
        ValidationErrorsList e;
        WorksheetAnalysisDO ana;

        e = new ValidationErrorsList();
        imap = new HashMap<Integer, WorksheetItemDO>();
        amap = new HashMap<Integer, WorksheetAnalysisDO>();

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
            for (WorksheetAnalysisDO data : getAnalyses(wm)) {
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
                        result.validate(data);
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
                        qcResult.validate(data);
                    } catch (Exception err) {
                        DataBaseUtil.mergeException(e, err);
                    }
                }
            }
        }

        if (e.size() > 0)
            throw e;
    }
}