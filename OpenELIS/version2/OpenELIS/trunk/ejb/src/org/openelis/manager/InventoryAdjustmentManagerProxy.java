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

import org.openelis.domain.InventoryAdjustmentViewDO;
import org.openelis.gwt.common.ValidationErrorsList;
import org.openelis.local.InventoryAdjustmentLocal;
import org.openelis.local.InventoryXAdjustLocal;

public class InventoryAdjustmentManagerProxy {

    public InventoryAdjustmentManager fetchById(Integer id) throws Exception {
        InventoryAdjustmentViewDO data;
        InventoryAdjustmentManager m;

        data = local().fetchById(id);
        m = InventoryAdjustmentManager.getInstance();

        m.setInventoryAdjustment(data);

        return m;
    }

    public InventoryAdjustmentManager fetchWithAdjustments(Integer id) throws Exception {
        InventoryAdjustmentManager m;

        m = fetchById(id);
        m.getAdjustments();

        return m;
    }

    public InventoryAdjustmentManager add(InventoryAdjustmentManager man) throws Exception {
        Integer id;

        local().add(man.getInventoryAdjustment());
        id = man.getInventoryAdjustment().getId();
        
        if (man.adjustments != null) {
            man.getAdjustments().setInventoryAdjustmentId(id);
            man.getAdjustments().add();
        }
        return man;
    }

    public InventoryAdjustmentManager update(InventoryAdjustmentManager man) throws Exception {
        Integer id;

        local().update(man.getInventoryAdjustment());
        id = man.getInventoryAdjustment().getId();
        
        if (man.adjustments != null) {
            man.getAdjustments().setInventoryAdjustmentId(id);
            man.getAdjustments().update();
        }
        return man;
    }

    public InventoryAdjustmentManager fetchForUpdate(InventoryAdjustmentManager man) throws Exception {
        assert false : "not supported";
        return null;
    }

    public InventoryAdjustmentManager abortUpdate(Integer id) throws Exception {
        assert false : "not supported";
        return null;
    }

    public void validate(InventoryAdjustmentManager man) throws Exception {
        ValidationErrorsList list;
        
        list = new ValidationErrorsList();
        
        if (list.size() > 0)
            throw list;
    }

    private InventoryAdjustmentLocal local() {
        try {
            InitialContext ctx = new InitialContext();
            return (InventoryAdjustmentLocal)ctx.lookup("openelis/InventoryAdjustmentBean/local");
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        }
    }
    
    private InventoryXAdjustLocal xAdjustLocal() {
        try {
            InitialContext ctx = new InitialContext();
            return (InventoryXAdjustLocal)ctx.lookup("openelis/InventoryXAdjustBean/local");
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        }
    }
}