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

import javax.naming.InitialContext;

import org.openelis.domain.InventoryXAdjustViewDO;
import org.openelis.gwt.common.TableFieldErrorException;
import org.openelis.gwt.common.ValidationErrorsList;
import org.openelis.local.InventoryXAdjustLocal;
import org.openelis.meta.InventoryAdjustmentMeta;
import org.openelis.utilcommon.DataBaseUtil;

public class InventoryXAdjustManagerProxy {

    public InventoryXAdjustManager fetchByInventoryAdjustmentId(Integer id) throws Exception {
        InventoryXAdjustManager m;
        ArrayList<InventoryXAdjustViewDO> items;

        items = local().fetchByInventoryAdjustmentId(id);
        m = InventoryXAdjustManager.getInstance();
        m.setInventoryAdjustmentId(id);
        m.setAdjustments(items);

        return m;
    }

    public InventoryXAdjustManager add(InventoryXAdjustManager man) throws Exception {
        InventoryXAdjustLocal cl;
        InventoryXAdjustViewDO item;

        cl = local();
        for (int i = 0; i < man.count(); i++ ) {
            item = man.getAdjustmentAt(i);
            item.setInventoryAdjustmentId(man.getInventoryAdjustmentId());
            cl.add(item);
        }

        return man;
    }

    public InventoryXAdjustManager update(InventoryXAdjustManager man) throws Exception {
        InventoryXAdjustLocal cl;
        InventoryXAdjustViewDO item;
        int i;

        cl = local();
        for (i = 0; i < man.deleteCount(); i++ )
            cl.delete(man.getDeletedAt(i));

        for (i = 0; i < man.count(); i++ ) {
            item = man.getAdjustmentAt(i);

            if (item.getId() == null) {
                item.setInventoryAdjustmentId(man.getInventoryAdjustmentId());
                cl.add(item);
            } else {
                cl.update(item);
            }
        }

        return man;
    }
    
    public void validate(InventoryXAdjustManager man) throws Exception {
        Integer locationId;
        ArrayList<Integer> locationIdList;
        ValidationErrorsList list;
        InventoryXAdjustLocal cl;
        InventoryXAdjustViewDO data;

        cl = local();        
        list = new ValidationErrorsList();
        data = null;
        locationIdList = new ArrayList<Integer>();
        for (int i = 0; i < man.count(); i++ ) {
            try {
                data = man.getAdjustmentAt(i);
                cl.validate(data);
            } catch (Exception e) {
                DataBaseUtil.mergeException(list, e, "adjustmentTable", i);
            }
            
            locationId = data.getInventoryLocationId();
            if (locationId != null && locationIdList.contains(locationId)) {
                list.add(new TableFieldErrorException("fieldUniqueOnlyException",i,
                                                      InventoryAdjustmentMeta.getInventoryLocationInventoryItemName(),
                                                      "adjustmentTable"));
            } else {
                locationIdList.add(locationId);
            }
        }
        if (list.size() > 0)
            throw list;
    }

    private InventoryXAdjustLocal local() {
        try {
            InitialContext ctx = new InitialContext();
            return (InventoryXAdjustLocal)ctx.lookup("openelis/InventoryXAdjustBean/local");
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        }
    }
}
