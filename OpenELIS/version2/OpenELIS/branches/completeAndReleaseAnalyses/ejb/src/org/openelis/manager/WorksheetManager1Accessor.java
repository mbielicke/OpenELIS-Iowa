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

import java.util.ArrayList;

import org.openelis.domain.DataObject;
import org.openelis.domain.NoteViewDO;
import org.openelis.domain.WorksheetAnalysisViewDO;
import org.openelis.domain.WorksheetItemDO;
import org.openelis.domain.WorksheetQcResultViewDO;
import org.openelis.domain.WorksheetResultViewDO;
import org.openelis.domain.WorksheetViewDO;

/**
 * This class is used to bulk load worksheet manager.
 */
public class WorksheetManager1Accessor {
    /**
     * Set/get objects from worksheet manager 
     */
    public static WorksheetViewDO getWorksheet(WorksheetManager1 wm) {
        return wm.worksheet;
    }

    public static void setWorksheet(WorksheetManager1 wm, WorksheetViewDO worksheet) {
        wm.worksheet = worksheet;
    }
    
    public static ArrayList<WorksheetItemDO> getItems(WorksheetManager1 wm) {
        return wm.items;
    }
    
    public static void setItems(WorksheetManager1 wm, ArrayList<WorksheetItemDO> items) {
        wm.items = items;
    }
    
    public static void addItem(WorksheetManager1 wm, WorksheetItemDO item) {
        if (wm.items == null)
            wm.items = new ArrayList<WorksheetItemDO>();
        wm.items.add(item);
    }
    
    public static ArrayList<WorksheetAnalysisViewDO> getAnalyses(WorksheetManager1 wm) {
        return wm.analyses;
    }
    
    public static void setAnalyses(WorksheetManager1 wm, ArrayList<WorksheetAnalysisViewDO> analyses) {
        wm.analyses = analyses;
    }
    
    public static void addAnalysis(WorksheetManager1 wm, WorksheetAnalysisViewDO analysis) {
        if (wm.analyses == null)
            wm.analyses = new ArrayList<WorksheetAnalysisViewDO>();
        wm.analyses.add(analysis);
    }
    
    public static ArrayList<WorksheetResultViewDO> getResults(WorksheetManager1 wm) {
        return wm.results;
    }
    
    public static void setResults(WorksheetManager1 wm, ArrayList<WorksheetResultViewDO> results) {
        wm.results = results;
    }
    
    public static void addResult(WorksheetManager1 wm, WorksheetResultViewDO result) {
        if (wm.results == null)
            wm.results = new ArrayList<WorksheetResultViewDO>();
        wm.results.add(result);
    }
    
    public static ArrayList<WorksheetQcResultViewDO> getQcResults(WorksheetManager1 wm) {
        return wm.qcResults;
    }
    
    public static void setQcResults(WorksheetManager1 wm, ArrayList<WorksheetQcResultViewDO> qcResults) {
        wm.qcResults = qcResults;
    }
    
    public static void addQcResult(WorksheetManager1 wm, WorksheetQcResultViewDO qcResult) {
        if (wm.qcResults == null)
            wm.qcResults = new ArrayList<WorksheetQcResultViewDO>();
        wm.qcResults.add(qcResult);
    }
    
    public static ArrayList<NoteViewDO> getNotes(WorksheetManager1 wm) {
        return wm.notes;
    }
    
    public static void setNotes(WorksheetManager1 wm, ArrayList<NoteViewDO> notes) {
        wm.notes = notes;
    }
    
    public static void addNote(WorksheetManager1 wm, NoteViewDO note) {
        if (wm.notes == null)
            wm.notes = new ArrayList<NoteViewDO>();
        wm.notes.add(note);
    }

    public static ArrayList<DataObject> getRemoved(WorksheetManager1 wm) {
        return wm.removed;
    }
    
    public static void setRemoved(WorksheetManager1 wm, ArrayList<DataObject> removed) {
        wm.removed = removed;
    }
}