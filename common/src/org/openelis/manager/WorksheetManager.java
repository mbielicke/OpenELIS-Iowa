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

import org.openelis.domain.ReferenceTable;
import org.openelis.domain.WorksheetDO;
import org.openelis.gwt.common.NotFoundException;
import org.openelis.gwt.common.RPC;

public class WorksheetManager implements RPC, HasNotesInt {

    private static final long      serialVersionUID = 1L;

    protected WorksheetDO          worksheet;
    protected WorksheetItemManager items;
    protected NoteManager          notes;

    protected transient static WorksheetManagerProxy proxy;

    /**
     * This is a protected constructor. See the three static methods for
     * allocation.
     */
    protected WorksheetManager() {
        worksheet = null;
        items     = null;
        notes     = null;
    }

    /**
     * Creates a new instance of this object. A default organization object is
     * also created.
     */
    public static WorksheetManager getInstance() {
        WorksheetManager manager;

        manager = new WorksheetManager();
        manager.worksheet = new WorksheetDO();

        return manager;
    }

    public WorksheetDO getWorksheet() {
        return worksheet;
    }

    public void setWorksheet(WorksheetDO worksheet) {
        this.worksheet = worksheet;
    }
    
    // service methods
    public static WorksheetManager fetchById(Integer id) throws Exception {
        return proxy().fetchById(id);
    }

    public static WorksheetManager fetchWithItems(Integer id) throws Exception {
        return proxy().fetchWithItems(id);
    }

    public static WorksheetManager fetchWithNotes(Integer id) throws Exception {
        return proxy().fetchWithNotes(id);
    }

    public WorksheetManager add() throws Exception {
        return proxy().add(this);

    }

    public WorksheetManager update() throws Exception {
        return proxy().update(this);

    }

    public WorksheetManager fetchForUpdate() throws Exception {
        return proxy().fetchForUpdate(worksheet.getId());
    }

    public WorksheetManager abortUpdate() throws Exception {
        return proxy().abortUpdate(worksheet.getId());
    }

    public void validate() throws Exception {
        proxy().validate(this);
    }

    //
    // other managers
    //
    public WorksheetItemManager getItems() throws Exception {
        if (items == null) {
            if (worksheet.getId() != null) {
                try {
                    items = WorksheetItemManager.fetchByWorksheetId(worksheet.getId());
                } catch (NotFoundException e) {
                    // ignore
                } catch (Exception e) {
                    throw e;
                }
            }
            if (items == null)
                items = WorksheetItemManager.getInstance();
        }
        return items;
    }

    public NoteManager getNotes() throws Exception {
        if (notes == null) {
            if (worksheet.getId() != null) {
                try {
                    notes = NoteManager.findByRefTableRefId(ReferenceTable.WORKSHEET,
                                                            worksheet.getId());
                } catch (NotFoundException e) {
                    // ignore
                } catch (Exception e) {
                    throw e;
                }
            }
            if (notes == null)
                notes = NoteManager.getInstance();
        }
        return notes;
    }

    private static WorksheetManagerProxy proxy() {
        if (proxy == null)
            proxy = new WorksheetManagerProxy();

        return proxy;
    }
}
