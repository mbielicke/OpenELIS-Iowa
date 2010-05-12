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
import java.util.HashMap;

import javax.naming.InitialContext;

import org.openelis.domain.InventoryXUseViewDO;
import org.openelis.gwt.common.FieldErrorException;
import org.openelis.gwt.common.ValidationErrorsList;
import org.openelis.local.InventoryXUseLocal;

public class OrderFillManagerProxy {

    public OrderFillManager fetchByOrderId(Integer id) throws Exception {
        InventoryXUseLocal ul;
        OrderFillManager m;
        ArrayList<InventoryXUseViewDO> list;

        ul = local();
        list = ul.fetchByOrderId(id);
        m = OrderFillManager.getInstance();

        m.setFills(list);

        return m;
    }

    public OrderFillManager add(OrderFillManager man) throws Exception {
        InventoryXUseLocal ul;
        InventoryXUseViewDO fill;        

        ul = local();
        for (int i = 0; i < man.count(); i++ ) {
            fill = man.getFillAt(i);
            ul.add(fill);
        }

        return man;
    }

    public OrderFillManager update(OrderFillManager man) throws Exception {
        InventoryXUseLocal ul;
        InventoryXUseViewDO fill;        

        ul = local();
        for (int i = 0; i < man.count(); i++ ) {
            fill = man.getFillAt(i);
            if(fill.getId() != null)
                ul.update(fill);
            else
                ul.add(fill);
        }

        return man;
    }

    public OrderFillManager fetchForUpdate(OrderFillManager man) throws Exception {
        assert false : "not supported";
        return null;
    }

    public OrderFillManager abortUpdate(Integer id) throws Exception {
        assert false : "not supported";
        return null;
    }

    public void validate(OrderFillManager man) throws Exception {
        ValidationErrorsList list;
        InventoryXUseViewDO data;
        Integer sum, locationId;
        HashMap<Integer, Integer> locationSumMap;
        FieldErrorException exc;
        ArrayList<String[]> names;
        String[] name;
        int j;

        list = new ValidationErrorsList();
        locationSumMap = new HashMap<Integer, Integer>();
        names = new ArrayList<String[]>();
        name = null;

        for (j = 0; j < man.count(); j++ ) {
            data = man.getFillAt(j);

            locationId = data.getInventoryLocationId();
            sum = locationSumMap.get(locationId);
            if (sum == null)
                sum = 0;

            sum += data.getQuantity();

            if (sum > data.getInventoryLocationQuantityOnhand()) {
                name = new String[2];
                name[0] = data.getInventoryItemName();
                name[1] = data.getStorageLocationName();
                names.add(name);
            }
            
            locationSumMap.put(locationId, sum);
        }

        for (j = 0; j < names.size(); j++ ) {
            exc = new FieldErrorException("totalItemsMoreThanQtyOnHandException", null, names.get(j));
            list.add(exc);
        }

        if (list.size() > 0) 
            throw list;        
    }

    private InventoryXUseLocal local() {
        try {
            InitialContext ctx = new InitialContext();
            return (InventoryXUseLocal)ctx.lookup("openelis/InventoryXUseBean/local");
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        }
    }
}