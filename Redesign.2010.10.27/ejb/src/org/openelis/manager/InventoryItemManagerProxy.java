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

import javax.naming.InitialContext;

import org.openelis.domain.InventoryItemViewDO;
import org.openelis.domain.ReferenceTable;
import org.openelis.gwt.common.DataBaseUtil;
import org.openelis.gwt.common.ValidationErrorsList;
import org.openelis.local.InventoryItemLocal;

public class InventoryItemManagerProxy {

    public InventoryItemManager fetchById(Integer id) throws Exception {
        InventoryItemLocal ol;
        InventoryItemViewDO data;
        InventoryItemManager m;

        ol = local();
        data = ol.fetchById(id);
        m = InventoryItemManager.getInstance();

        m.setInventoryItem(data);

        return m;
    }

    public InventoryItemManager fetchWithComponents(Integer id) throws Exception {
        InventoryItemManager m;

        m = fetchById(id);
        m.getComponents();

        return m;
    }

    public InventoryItemManager fetchWithLocations(Integer id) throws Exception {
        InventoryItemManager m;

        m = fetchById(id);
        m.getLocations();
        
        return m;
    }

    public InventoryItemManager fetchWithManufacturing(Integer id) throws Exception {
        InventoryItemManager m;

        m = fetchById(id);
        m.getManufacturing();

        return m;
    }

    public InventoryItemManager fetchWithNotes(Integer id) throws Exception {
        InventoryItemManager m;

        m = fetchById(id);
        m.getNotes();

        return m;
    }

    public InventoryItemManager add(InventoryItemManager man) throws Exception {
        Integer id;
        InventoryItemLocal ol;

        ol = local();
        ol.add(man.getInventoryItem());
        id = man.getInventoryItem().getId();

        if (man.components != null) {
            man.getComponents().setInventoryItemId(id);
            man.getComponents().add();
        }
        if (man.locations != null) {
            man.getLocations().setInventoryItemId(id);
            man.getLocations().add();
        }
        if (man.manufacturing != null) {
            man.getManufacturing().setReferenceId(id);
            man.getManufacturing().setReferenceTableId(ReferenceTable.INVENTORY_ITEM_MANUFACTURING);
            man.getManufacturing().add();
        }
        if (man.notes != null) {
            man.getNotes().setReferenceId(id);
            man.getNotes().setReferenceTableId(ReferenceTable.INVENTORY_ITEM);
            man.getNotes().add();
        }

        return man;
    }

    public InventoryItemManager update(InventoryItemManager man) throws Exception {
        Integer id;
        InventoryItemLocal ol;

        ol = local();
        ol.update(man.getInventoryItem());
        id = man.getInventoryItem().getId();

        if (man.components != null) {
            man.getComponents().setInventoryItemId(id);
            man.getComponents().update();
        }
        if (man.locations != null) {
            man.getLocations().setInventoryItemId(id);
            man.getLocations().update();
        }
        if (man.manufacturing != null) {
            man.getManufacturing().setReferenceId(id);
            man.getManufacturing().setReferenceTableId(ReferenceTable.INVENTORY_ITEM_MANUFACTURING);
            man.getManufacturing().update();
        }
        if (man.notes != null) {
            man.getNotes().setReferenceId(id);
            man.getNotes().setReferenceTableId(ReferenceTable.INVENTORY_ITEM);
            man.getNotes().update();
        }

        return man;
    }

    public InventoryItemManager fetchForUpdate(InventoryItemManager man) throws Exception {
        assert false : "not supported";
        return null;
    }

    public InventoryItemManager abortUpdate(Integer id) throws Exception {
        assert false : "not supported";
        return null;
    }

    public void validate(InventoryItemManager man) throws Exception {
        ValidationErrorsList list;
        
        list = new ValidationErrorsList();
        try {
            local().validate(man.getInventoryItem());
        } catch (Exception e) {
            DataBaseUtil.mergeException(list, e);
        }
        try {
            if (man.components != null)
                man.getComponents().validate();
        } catch (Exception e) {
            DataBaseUtil.mergeException(list, e);
        }
        try {
            if (man.locations != null)
                man.getLocations().validate();
        } catch (Exception e) {
            DataBaseUtil.mergeException(list, e);
        }
        
        if (list.size() > 0)
            throw list;
    }

    private InventoryItemLocal local() {
        try {
            InitialContext ctx = new InitialContext();
            return (InventoryItemLocal)ctx.lookup("openelis/InventoryItemBean/local");
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        }
    }
}