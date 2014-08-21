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
package org.openelis.manager;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

import org.openelis.domain.DataObject;
import org.openelis.domain.NoteDO;
import org.openelis.domain.NoteViewDO;
import org.openelis.domain.ResultViewDO;
import org.openelis.domain.WorksheetAnalysisViewDO;
import org.openelis.domain.WorksheetItemDO;
import org.openelis.domain.WorksheetQcResultViewDO;
import org.openelis.domain.WorksheetResultViewDO;
import org.openelis.domain.WorksheetViewDO;

/**
 * This class encapsulates a worksheet and all its related information including
 * items, analysis, etc. Although the class provides some basic functions
 * internally, it is designed to interact with EJB methods to provide majority
 * of the operations needed to manage a sample.
 */
public class WorksheetManager1 implements Serializable {
    private static final long                    serialVersionUID = 1L;

    /**
     * Flags that specifies what optional data to load with the manager
     */
    public enum Load {
        DETAIL, NOTE
    };

    protected WorksheetViewDO                     worksheet;
    protected ArrayList<WorksheetItemDO>          items;
    protected ArrayList<WorksheetAnalysisViewDO>  analyses;
    protected ArrayList<WorksheetResultViewDO>    results;
    protected ArrayList<WorksheetQcResultViewDO>  qcResults;
    protected ArrayList<NoteViewDO>               notes;
    protected ArrayList<DataObject>               removed;
    protected ArrayList<ResultViewDO>             modifiedResults;
    protected int                                 nextUID  = -1;

    transient public final WorksheetItem          item     = new WorksheetItem();
    transient public final WorksheetAnalysis      analysis = new WorksheetAnalysis();
    transient public final WorksheetResult        result   = new WorksheetResult();
    transient public final WorksheetQcResult      qcResult = new WorksheetQcResult();
    transient public final WorksheetNote          note     = new WorksheetNote();
    transient private HashMap<String, DataObject> uidMap;

    /**
     * Initialize an empty worksheet manager
     */
    public WorksheetManager1() {
    }

    /**
     * Returns the worksheet DO
     */
    public WorksheetViewDO getWorksheet() {
        return worksheet;
    }

    /**
     * Returns the next negative Id for this worksheet's newly created and as yet
     * uncommitted worksheet items, analyses
     */
    public int getNextUID() {
        return --nextUID;
    }

    /**
     * Returns a unique id representing the data object's type and key. This id
     * can be used to directly find the object this manager rather than serially
     * traversing the lists.
     */
    
    public String getUid(WorksheetItemDO data) {
        return getWorksheetItemUid(data.getId());
    }

    public String getUid(WorksheetAnalysisViewDO data) {
        return getWorksheetAnalysisUid(data.getId());
    }

    public String getUid(WorksheetResultViewDO data) {
        return getWorksheetResultUid(data.getId());
    }

    public String getUid(WorksheetQcResultViewDO data) {
        return getWorksheetQcResultUid(data.getId());
    }

    public String getUid(NoteDO data) {
        return getNoteUid(data.getId());
    }

    /**
     * Returns the data object using its Uid.
     */
    public DataObject getObject(String uid) {
        if (uidMap == null) {
            uidMap = new HashMap<String, DataObject>();

            if (items != null)
                for (WorksheetItemDO data : items)
                    uidMap.put(getWorksheetItemUid(data.getId()), data);

            if (analyses != null)
                for (WorksheetAnalysisViewDO data : analyses)
                    uidMap.put(getWorksheetAnalysisUid(data.getId()), data);

            if (results != null)
                for (WorksheetResultViewDO data : results)
                    uidMap.put(getWorksheetResultUid(data.getId()), data);

            if (qcResults != null)
                for (WorksheetQcResultViewDO data : qcResults)
                    uidMap.put(getWorksheetQcResultUid(data.getId()), data);

            if (notes != null)
                for (NoteDO data : notes)
                    uidMap.put(getNoteUid(data.getId()), data);
        }
        return uidMap.get(uid);
    }

    /**
     * Returns the unique identifiers for each data object.
     */

    public String getWorksheetItemUid(Integer id) {
        return "I:" + id;
    }

    public String getWorksheetAnalysisUid(Integer id) {
        return "A:" + id;
    }

    public String getWorksheetResultUid(Integer id) {
        return "R:" + id;
    }

    public String getWorksheetQcResultUid(Integer id) {
        return "Q:" + id;
    }

    public String getNoteUid(Integer id) {
        return "N:" + id;
    }
    
    public ArrayList<ResultViewDO> getModifiedResults() {
        return modifiedResults;
    }

    public void setModifiedResults(ArrayList<ResultViewDO> results) {
        this.modifiedResults = results;
    }

    /**
     * Class to manage worksheet items
     */
    public class WorksheetItem {
        /**
         * Returns the item at specified id.
         */
        public WorksheetItemDO getById(Integer id) {
            return (WorksheetItemDO)getObject(getWorksheetItemUid(id));
        }
        
        /**
         * Returns the item at specified index.
         */
        public WorksheetItemDO get(int i) {
            return items.get(i);
        }

        /**
         * Returns a new item initialized with the next position
         */
        public WorksheetItemDO add(int index) {
            int pos;
            WorksheetItemDO data, temp;

            assert worksheet != null : "Manager.worksheet == null";

            if (items == null)
                items = new ArrayList<WorksheetItemDO>();

            if (items.size() > 0) {
                if (index < items.size()) {
                    pos = items.get(index).getPosition();
                } else {
                    pos = items.get(items.size() - 1).getPosition() + 1;
                    index = items.size();
                }
            } else {
                pos = 1;
                index = 0;
            }

            data = new WorksheetItemDO();
            data.setId(getNextUID());
            data.setPosition(pos);
            items.add(index, data);
            uidMapAdd(getWorksheetItemUid(data.getId()), data);

            //
            // increment the positions on all items after the added item by one
            //
            for (++index; index < items.size(); index++) {
                temp = items.get(index);
                temp.setPosition(temp.getPosition() + 1);
            }

            return data;
        }

        /**
         * Removes an item from the list
         */
        public void remove(int i) {
            WorksheetItemDO data;

            data = items.get(i);
            items.remove(data);
            dataObjectRemove(data.getId(), data);
            uidMapRemove(getWorksheetItemUid(data.getId()));

            //
            // decrement the positions on all items after the removed item by one
            //
            for (; i < items.size(); i++) {
                data = items.get(i);
                data.setPosition(data.getPosition() - 1);
            }
            
            while (analysis.count(data) > 0)
                analysis.remove(data, 0);
        }

        public void remove(WorksheetItemDO data) {
            int i;
            
            i = items.indexOf(data);
            items.remove(data);
            dataObjectRemove(data.getId(), data);
            uidMapRemove(getWorksheetItemUid(data.getId()));

            while (analysis.count(data) > 0)
                analysis.remove(data, 0);

            //
            // decrement the positions on all items after the removed item by one
            //
            if (i != -1) {
                for (; i < items.size(); i++) {
                    data = items.get(i);
                    data.setPosition(data.getPosition() - 1);
                }
            }
        }

        /**
         * Returns the number of items associated with this worksheet
         */
        public int count() {
            if (items != null)
                return items.size();
            return 0;
        }
    }

    /**
     * Class to manage worksheet analysis data
     */
    public class WorksheetAnalysis {
        transient protected HashMap<Integer, ArrayList<WorksheetAnalysisViewDO>> localMap = null;

        /**
         * Returns the worksheet item's worksheet analysis at specified index.
         */
        public WorksheetAnalysisViewDO get(WorksheetItemDO item, int i) {
            localMapBuild();
            return localMap.get(item.getId()).get(i);
        }

        /**
         * Returns a new worksheet analysis for a worksheet item
         */
        public WorksheetAnalysisViewDO add(WorksheetItemDO item) {
            WorksheetAnalysisViewDO data;

            data = new WorksheetAnalysisViewDO();
            data.setId(getNextUID());
            data.setWorksheetItemId(item.getId());
            if (analyses == null)
                analyses = new ArrayList<WorksheetAnalysisViewDO>();
            analyses.add(data);
            localMapAdd(data);
            uidMapAdd(getWorksheetAnalysisUid(data.getId()), data);

            return data;
        }

        /**
         * Removes a worksheet analysis
         */
        public void remove(WorksheetItemDO item, int i) {
            WorksheetAnalysisViewDO data;

            localMapBuild();
            data = localMap.get(item.getId()).get(i);
            analyses.remove(data);
            localMapRemove(data);
            dataObjectRemove(data.getId(), data);
            uidMapRemove(getWorksheetAnalysisUid(data.getId()));
            
            if (data.getAnalysisId() != null)
                while (result.count(data) > 0)
                    result.remove(data, 0);
            else if (data.getQcLotId() != null)
                while (qcResult.count(data) > 0)
                    qcResult.remove(data, 0);
        }

        public void remove(WorksheetItemDO item, WorksheetAnalysisViewDO data) {
            analyses.remove(data);
            localMapRemove(data);
            dataObjectRemove(data.getId(), data);
            uidMapRemove(getWorksheetAnalysisUid(data.getId()));
            
            if (data.getAnalysisId() != null)
                while (result.count(data) > 0)
                    result.remove(data, 0);
            else if (data.getQcLotId() != null)
                while (qcResult.count(data) > 0)
                    qcResult.remove(data, 0);
        }

        /**
         * Returns the number of worksheet analyses associated with specified
         * worksheet item
         */
        public int count(WorksheetItemDO item) {
            ArrayList<WorksheetAnalysisViewDO> l;

            if (analyses != null) {
                localMapBuild();
                l = localMap.get(item.getId());
                if (l != null)
                    return l.size();
            }
            return 0;
        }

        /*
         * create a hash localMap from analyses list
         */
        private void localMapBuild() {
            if (localMap == null && analyses != null) {
                localMap = new HashMap<Integer, ArrayList<WorksheetAnalysisViewDO>>();
                for (WorksheetAnalysisViewDO data : analyses)
                    localMapAdd(data);
            }
        }

        /*
         * adds a new worksheet analysis to the hash localMap
         */
        private void localMapAdd(WorksheetAnalysisViewDO data) {
            ArrayList<WorksheetAnalysisViewDO> l;

            if (localMap != null) {
                l = localMap.get(data.getWorksheetItemId());
                if (l == null) {
                    l = new ArrayList<WorksheetAnalysisViewDO>();
                    localMap.put(data.getWorksheetItemId(), l);
                }
                l.add(data);
            }
        }

        /*
         * removes the worksheet analysis from hash localMap
         */
        private void localMapRemove(WorksheetAnalysisViewDO data) {
            ArrayList<WorksheetAnalysisViewDO> l;

            if (localMap != null) {
                l = localMap.get(data.getWorksheetItemId());
                if (l != null)
                    l.remove(data);
            }
        }
    }

    /**
     * Class to manage worksheet result data
     */
    public class WorksheetResult {
        transient protected HashMap<Integer, ArrayList<WorksheetResultViewDO>> localMap = null;

        /**
         * Returns the worksheet analysis's worksheet result at specified index.
         */
        public WorksheetResultViewDO get(WorksheetAnalysisViewDO analysis, int i) {
            localMapBuild();
            return localMap.get(analysis.getId()).get(i);
        }

        /**
         * Returns a new worksheet result for a worksheet analysis
         */
        public WorksheetResultViewDO add(WorksheetAnalysisViewDO analysis) {
            WorksheetResultViewDO data;

            data = new WorksheetResultViewDO();
            data.setId(getNextUID());
            data.setWorksheetAnalysisId(analysis.getId());
            if (results == null)
                results = new ArrayList<WorksheetResultViewDO>();
            results.add(data);
            localMapAdd(data);
            uidMapAdd(getWorksheetResultUid(data.getId()), data);

            return data;
        }

        /**
         * Removes a worksheet result
         */
        public void remove(WorksheetAnalysisViewDO analysis, int i) {
            WorksheetResultViewDO data;

            localMapBuild();
            data = localMap.get(analysis.getId()).get(i);
            results.remove(data);
            localMapRemove(data);
            dataObjectRemove(data.getId(), data);
            uidMapRemove(getWorksheetResultUid(data.getId()));
        }

        public void remove(WorksheetAnalysisViewDO analysis, WorksheetResultViewDO data) {
            results.remove(data);
            localMapRemove(data);
            dataObjectRemove(data.getId(), data);
            uidMapRemove(getWorksheetResultUid(data.getId()));
        }

        /**
         * Returns the number of worksheet results associated with specified
         * worksheet analysis
         */
        public int count(WorksheetAnalysisViewDO analysis) {
            ArrayList<WorksheetResultViewDO> l;

            if (results != null) {
                localMapBuild();
                l = localMap.get(analysis.getId());
                if (l != null)
                    return l.size();
            }
            return 0;
        }

        /*
         * create a hash localMap from worksheet results list
         */
        private void localMapBuild() {
            if (localMap == null && results != null) {
                localMap = new HashMap<Integer, ArrayList<WorksheetResultViewDO>>();
                for (WorksheetResultViewDO data : results)
                    localMapAdd(data);
            }
        }

        /*
         * adds a new worksheet results to the hash localMap
         */
        private void localMapAdd(WorksheetResultViewDO data) {
            ArrayList<WorksheetResultViewDO> l;

            if (localMap != null) {
                l = localMap.get(data.getWorksheetAnalysisId());
                if (l == null) {
                    l = new ArrayList<WorksheetResultViewDO>();
                    localMap.put(data.getWorksheetAnalysisId(), l);
                }
                l.add(data);
            }
        }

        /*
         * removes the worksheet result from hash localMap
         */
        private void localMapRemove(WorksheetResultViewDO data) {
            ArrayList<WorksheetResultViewDO> l;

            if (localMap != null) {
                l = localMap.get(data.getWorksheetAnalysisId());
                if (l != null)
                    l.remove(data);
            }
        }
    }

    /**
     * Class to manage worksheet qc result data
     */
    public class WorksheetQcResult {
        transient protected HashMap<Integer, ArrayList<WorksheetQcResultViewDO>> localMap = null;

        /**
         * Returns the worksheet analysis's worksheet qc result at specified index.
         */
        public WorksheetQcResultViewDO get(WorksheetAnalysisViewDO analysis, int i) {
            localMapBuild();
            return localMap.get(analysis.getId()).get(i);
        }

        /**
         * Returns a new worksheet qc result for a worksheet analysis
         */
        public WorksheetQcResultViewDO add(WorksheetAnalysisViewDO analysis) {
            WorksheetQcResultViewDO data;

            data = new WorksheetQcResultViewDO();
            data.setId(getNextUID());
            data.setWorksheetAnalysisId(analysis.getId());
            if (qcResults == null)
                qcResults = new ArrayList<WorksheetQcResultViewDO>();
            qcResults.add(data);
            localMapAdd(data);
            uidMapAdd(getWorksheetQcResultUid(data.getId()), data);

            return data;
        }

        /**
         * Removes a worksheet qc result
         */
        public void remove(WorksheetAnalysisViewDO analysis, int i) {
            WorksheetQcResultViewDO data;

            localMapBuild();
            data = localMap.get(analysis.getId()).get(i);
            qcResults.remove(data);
            localMapRemove(data);
            dataObjectRemove(data.getId(), data);
            uidMapRemove(getWorksheetQcResultUid(data.getId()));
        }

        public void remove(WorksheetAnalysisViewDO analysis, WorksheetQcResultViewDO data) {
            qcResults.remove(data);
            localMapRemove(data);
            dataObjectRemove(data.getId(), data);
            uidMapRemove(getWorksheetQcResultUid(data.getId()));
        }

        /**
         * Returns the number of worksheet qc results associated with specified
         * worksheet analysis
         */
        public int count(WorksheetAnalysisViewDO analysis) {
            ArrayList<WorksheetQcResultViewDO> l;

            if (qcResults != null) {
                localMapBuild();
                l = localMap.get(analysis.getId());
                if (l != null)
                    return l.size();
            }
            return 0;
        }

        /*
         * create a hash localMap from worksheet qc results list
         */
        private void localMapBuild() {
            if (localMap == null && qcResults != null) {
                localMap = new HashMap<Integer, ArrayList<WorksheetQcResultViewDO>>();
                for (WorksheetQcResultViewDO data : qcResults)
                    localMapAdd(data);
            }
        }

        /*
         * adds a new worksheet qc results to the hash localMap
         */
        private void localMapAdd(WorksheetQcResultViewDO data) {
            ArrayList<WorksheetQcResultViewDO> l;

            if (localMap != null) {
                l = localMap.get(data.getWorksheetAnalysisId());
                if (l == null) {
                    l = new ArrayList<WorksheetQcResultViewDO>();
                    localMap.put(data.getWorksheetAnalysisId(), l);
                }
                l.add(data);
            }
        }

        /*
         * removes the worksheet qc result from hash localMap
         */
        private void localMapRemove(WorksheetQcResultViewDO data) {
            ArrayList<WorksheetQcResultViewDO> l;

            if (localMap != null) {
                l = localMap.get(data.getWorksheetAnalysisId());
                if (l != null)
                    l.remove(data);
            }
        }
    }

    /**
     * Class to manage worksheet notes
     */
    public class WorksheetNote {

        /**
         * Returns the worksheet's internal note at specified index.
         */
        public NoteViewDO get(int i) {
            return notes.get(i);
        }

        /**
         * Returns the editing note. For internal, only the currently uncommitted
         * note can be edited. If no editing note currently exists, one is created
         * and returned.
         */
        public NoteViewDO getEditing() {
            NoteViewDO data;

            if (notes == null)
                notes = new ArrayList<NoteViewDO>(1);
            
            if (notes.size() == 0 ||
                (notes.get(0).getId() != null && notes.get(0).getId() > 0)) {
                data = new NoteViewDO();
                data.setId(getNextUID());
                data.setIsExternal("N");
                notes.add(0, data);
                uidMapAdd(getNoteUid(data.getId()), data);
            }
            
            return notes.get(0);
        }

        /**
         * Removes the editing note. For internal, only the uncommitted note is removed.
         */
        public void removeEditing() {
            NoteViewDO data;

            if (notes != null && notes.size() > 0) {
                data = notes.get(0);
                if (data.getId() < 0) {
                    notes.remove(0);
                }
            }
        }

        /**
         * Returns the number of note(s)
         */
        public int count() {
            return (notes == null) ? 0 : notes.size();
        }
    }
    
    /**
     * adds an object to uid map
     */
    private void uidMapAdd(String uid, DataObject data) {
        if (uidMap != null)
            uidMap.put(uid, data);
    }
    
    /**
     * removes the object from uid map
     */
    private void uidMapRemove(String uid) {
        if (uidMap != null)
            uidMap.remove(uid);        
    }
    
    /**
     * adds the data object to the list of objects that should be removed from
     * the database
     */
    private void dataObjectRemove(Integer id, DataObject data) {
        if (removed == null)
            removed = new ArrayList<DataObject>();

        if (id > 0)
            removed.add(data);
    }
}