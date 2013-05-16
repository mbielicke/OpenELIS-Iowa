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
import org.openelis.domain.WorksheetAnalysisDO;
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
    protected ArrayList<WorksheetAnalysisDO>      analyses;
    protected ArrayList<WorksheetResultViewDO>    results;
    protected ArrayList<WorksheetQcResultViewDO>  qcResults;
    protected ArrayList<NoteViewDO>               notes;
    protected ArrayList<DataObject>               removed;
    protected int                                 nextUID  = -1;

    transient public final WorksheetItem          item     = new WorksheetItem();
    transient public final WorksheetAnalysis      analysis = new WorksheetAnalysis();
    transient public final WorksheetResult        result   = new WorksheetResult();
    transient public final WorksheetQcResult      qcResult = new WorksheetQcResult();
    transient public final WorksheetNote          note     = new WorksheetNote();
    transient private HashMap<String, DataObject> doMap;

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

    public String getUid(WorksheetAnalysisDO data) {
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
        if (doMap == null) {
            doMap = new HashMap<String, DataObject>();

            if (items != null)
                for (WorksheetItemDO data : items)
                    doMap.put(getWorksheetItemUid(data.getId()), data);

            if (analyses != null)
                for (WorksheetAnalysisDO data : analyses)
                    doMap.put(getWorksheetAnalysisUid(data.getId()), data);

            if (results != null)
                for (WorksheetResultViewDO data : results)
                    doMap.put(getWorksheetResultUid(data.getId()), data);

            if (qcResults != null)
                for (WorksheetQcResultViewDO data : qcResults)
                    doMap.put(getWorksheetQcResultUid(data.getId()), data);

            if (notes != null)
                for (NoteDO data : notes)
                    doMap.put(getNoteUid(data.getId()), data);
        }
        return doMap.get(uid);
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
        public WorksheetItemDO add() {
            int pos;
            WorksheetItemDO data;

            assert worksheet != null : "Manager.worksheet == null";

            if (items == null)
                items = new ArrayList<WorksheetItemDO>();

            if (items.size() > 0)
                pos = items.get(items.size() - 1).getPosition() + 1;
            else
                pos = 1;

            data = new WorksheetItemDO();
            data.setPosition(pos);
            items.add(data);

            return data;
        }

        /**
         * Removes an item from the list
         */
        public void remove(int i) {
            WorksheetItemDO data;

            data = items.remove(i);
            if (data.getId() != null && data.getId() > 0)
                removeDataObject(data);
        }

        public void remove(WorksheetItemDO data) {
            if (items.remove(data) && data.getId() != null && data.getId() > 0)
                removeDataObject(data);
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
        transient protected HashMap<Integer, ArrayList<WorksheetAnalysisDO>> map = null;

        /**
         * Returns the worksheet item's worksheet analysis at specified index.
         */
        public WorksheetAnalysisDO get(WorksheetItemDO item, int i) {
            mapBuild();
            return map.get(item.getId()).get(i);
        }

        /**
         * Returns the number of worksheet analyses associated with specified
         * worksheet item
         */
        public int count(WorksheetItemDO item) {
            ArrayList<WorksheetAnalysisDO> l;

            if (analyses != null) {
                mapBuild();
                l = map.get(item.getId());
                if (l != null)
                    return l.size();
            }
            return 0;
        }

        /*
         * create a hash map from analyses list
         */
        private void mapBuild() {
            if (map == null && analyses != null) {
                map = new HashMap<Integer, ArrayList<WorksheetAnalysisDO>>();
                for (WorksheetAnalysisDO data : analyses)
                    mapAdd(data);
            }
        }

        /*
         * adds a new worksheet analysis to the hash map
         */
        private void mapAdd(WorksheetAnalysisDO data) {
            ArrayList<WorksheetAnalysisDO> l;

            if (map != null) {
                l = map.get(data.getWorksheetItemId());
                if (l == null) {
                    l = new ArrayList<WorksheetAnalysisDO>();
                    map.put(data.getWorksheetItemId(), l);
                }
                l.add(data);
            }
        }
    }

    /**
     * Class to manage worksheet result data
     */
    public class WorksheetResult {
        transient protected HashMap<Integer, ArrayList<WorksheetResultViewDO>> map = null;

        /**
         * Returns the worksheet analysis's worksheet result at specified index.
         */
        public WorksheetResultViewDO get(WorksheetAnalysisDO analysis, int i) {
            mapBuild();
            return map.get(analysis.getId()).get(i);
        }

        /**
         * Returns the number of worksheet results associated with specified
         * worksheet analysis
         */
        public int count(WorksheetAnalysisDO analysis) {
            ArrayList<WorksheetResultViewDO> l;

            if (results != null) {
                mapBuild();
                l = map.get(analysis.getId());
                if (l != null)
                    return l.size();
            }
            return 0;
        }

        /*
         * create a hash map from worksheet results list
         */
        private void mapBuild() {
            if (map == null && results != null) {
                map = new HashMap<Integer, ArrayList<WorksheetResultViewDO>>();
                for (WorksheetResultViewDO data : results)
                    mapAdd(data);
            }
        }

        /*
         * adds a new worksheet results to the hash map
         */
        private void mapAdd(WorksheetResultViewDO data) {
            ArrayList<WorksheetResultViewDO> l;

            if (map != null) {
                l = map.get(data.getWorksheetAnalysisId());
                if (l == null) {
                    l = new ArrayList<WorksheetResultViewDO>();
                    map.put(data.getWorksheetAnalysisId(), l);
                }
                l.add(data);
            }
        }
    }

    /**
     * Class to manage worksheet qc result data
     */
    public class WorksheetQcResult {
        transient protected HashMap<Integer, ArrayList<WorksheetQcResultViewDO>> map = null;

        /**
         * Returns the worksheet analysis's worksheet qc result at specified index.
         */
        public WorksheetQcResultViewDO get(WorksheetAnalysisDO analysis, int i) {
            mapBuild();
            return map.get(analysis.getId()).get(i);
        }

        /**
         * Returns the number of worksheet qc results associated with specified
         * worksheet analysis
         */
        public int count(WorksheetAnalysisDO analysis) {
            ArrayList<WorksheetQcResultViewDO> l;

            if (qcResults != null) {
                mapBuild();
                l = map.get(analysis.getId());
                if (l != null)
                    return l.size();
            }
            return 0;
        }

        /*
         * create a hash map from worksheet qc results list
         */
        private void mapBuild() {
            if (map == null && qcResults != null) {
                map = new HashMap<Integer, ArrayList<WorksheetQcResultViewDO>>();
                for (WorksheetQcResultViewDO data : qcResults)
                    mapAdd(data);
            }
        }

        /*
         * adds a new worksheet qc results to the hash map
         */
        private void mapAdd(WorksheetQcResultViewDO data) {
            ArrayList<WorksheetQcResultViewDO> l;

            if (map != null) {
                l = map.get(data.getWorksheetAnalysisId());
                if (l == null) {
                    l = new ArrayList<WorksheetQcResultViewDO>();
                    map.put(data.getWorksheetAnalysisId(), l);
                }
                l.add(data);
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
                data.setIsExternal("N");
                notes.add(0, data);
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
                if (data.getId() == null || data.getId() < 0)
                    notes.remove(0);
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
     * Adds the specified data object to the list of objects that should be
     * removed from the database.
     */
    protected void removeDataObject(DataObject data) {
        removed.add(data);
    }
}