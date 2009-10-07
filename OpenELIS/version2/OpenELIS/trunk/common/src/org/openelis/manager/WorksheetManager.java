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

import org.openelis.domain.WorksheetViewDO;
import org.openelis.gwt.common.InconsistencyException;
import org.openelis.gwt.common.NotFoundException;
import org.openelis.gwt.common.RPC;

public class WorksheetManager implements RPC, HasNotesInt {

    private static final long      serialVersionUID = 1L;
//    protected WorksheetItemManager items;
    protected NoteManager          notes;
    protected WorksheetViewDO      worksheet;
    protected Integer              worksheetReferenceTable;

    protected transient static WorksheetManagerProxy proxy;

    /**
     * This is a protected constructor. See the three static methods for
     * allocation.
     */
    protected WorksheetManager() {
        worksheet = null;
        notes = null;
    }

    /**
     * Creates a new instance of this object. A default organization object is
     * also created.
     */
    public static WorksheetManager getInstance() {
        WorksheetManager wm;

        wm = new WorksheetManager();
        wm.worksheet = new WorksheetViewDO();

        return wm;
    }

    public static WorksheetManager findById(Integer id) throws Exception {
        return proxy().fetch(id);
    }
/*
    public static WorksheetManager findByIdWithItems(Integer id) throws Exception {
        return proxy().fetchWithItems(id);
    }
*/
    public static WorksheetManager findByIdWithNotes(Integer id) throws Exception {
        return proxy().fetchWithNotes(id);
    }

    public WorksheetManager fetchForUpdate() throws Exception {
        if (worksheet.getId() == null)
            throw new InconsistencyException("worksheet id is null");

        return proxy().fetchForUpdate(worksheet.getId());
    }

    // getters/setters
/*
    public WorksheetItemManager getItems() throws Exception {
        if (items == null) {
            if (worksheet.getId() != null) {
                try {
                    items = WorksheetItemManager.findByWorksheetId(worksheet.getId());
                } catch (NotFoundException e) {
                    // ignore
                } catch (Exception e) {
                    throw e;
                }
            }
        }

        if (items == null)
            items = WorksheetItemManager.getInstance();

        return items;
    }
*/
    public NoteManager getNotes() throws Exception {
        if (notes == null) {
            if (worksheet.getId() != null && worksheetReferenceTable != null) {
                try {
                    notes = NoteManager.findByRefTableRefId(worksheetReferenceTable,
                                                            worksheet.getId());

                } catch (NotFoundException e) {
                    // ignore
                } catch (Exception e) {
                    throw e;
                }
            }
        }

        if (notes == null)
            notes = NoteManager.getInstance();

        return notes;
    }

    public WorksheetViewDO getWorksheet() {
        return worksheet;
    }

    public void setWorksheet(WorksheetViewDO worksheet) {
        this.worksheet = worksheet;
    }
    
    public Integer getWorksheetReferenceTable() {
        return worksheetReferenceTable;
    }

    public void setWorksheetReferenceTable(Integer worksheetReferenceTable) {
        this.worksheetReferenceTable = worksheetReferenceTable;
    }

    // service methods
    public WorksheetManager add() throws Exception {
        return proxy().add(this);

    }

    public WorksheetManager update() throws Exception {
        return proxy().update(this);

    }

    public WorksheetManager abort() throws Exception {
        return proxy().abort(worksheet.getId());
    }

    public void validate() throws Exception {
        proxy().validate(this);
    }

    private static WorksheetManagerProxy proxy() {
        if (proxy == null)
            proxy = new WorksheetManagerProxy();

        return proxy;
    }
}
