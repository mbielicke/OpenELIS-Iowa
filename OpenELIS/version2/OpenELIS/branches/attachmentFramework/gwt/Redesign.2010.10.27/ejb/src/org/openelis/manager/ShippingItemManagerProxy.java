/** Exhibit A - UIRF Open-source Based Public Software License.
* 
* The contents of this file are subject to the UIRF Open-source Based
* Public Software License(the "License"); you may not use this file except
* in compliance with the License. You may obtain a copy of the License at
* openelis.uhl.uiowa.edu
* 
* Software distributed under the License is distributed on an "AS IS"
* basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
* License for the specific language governing rights and limitations
* under the License.
* 
* The Original Code is OpenELIS code.
* 
* The Initial Developer of the Original Code is The University of Iowa.
* Portions created by The University of Iowa are Copyright 2006-2008. All
* Rights Reserved.
* 
* Contributor(s): ______________________________________.
* 
* Alternatively, the contents of this file marked
* "Separately-Licensed" may be used under the terms of a UIRF Software
* license ("UIRF Software License"), in which case the provisions of a
* UIRF Software License are applicable instead of those above. 
*/
package org.openelis.manager;

import java.util.ArrayList;

import javax.naming.InitialContext;

import org.openelis.domain.ShippingItemDO;
import org.openelis.gwt.common.DataBaseUtil;
import org.openelis.gwt.common.FieldErrorException;
import org.openelis.gwt.common.ValidationErrorsList;
import org.openelis.local.ShippingItemLocal;

public class ShippingItemManagerProxy {
    
    public ShippingItemManager fetchByShippingId(Integer id) throws Exception {
        ShippingItemManager m;
        ArrayList<ShippingItemDO> items;
    
        items = local().fetchByShippingId(id);
        m = ShippingItemManager.getInstance();
        m.setShippingId(id);
        m.setItems(items);
        
        return m;
    }
    
    public ShippingItemManager add(ShippingItemManager man) throws Exception {
        ShippingItemLocal tl; 
        ShippingItemDO item;
    
        tl = local();
        
        for (int i = 0; i < man.count(); i++ ) {
            item = man.getItemAt(i);
            item.setShippingId(man.getShippingId());
            tl.add(item);
        }

        return man;
    }
    
    public ShippingItemManager update(ShippingItemManager man) throws Exception {
        ShippingItemLocal tl; 
        ShippingItemDO item;
    
        tl = local();
        
        for (int j = 0; j < man.deleteCount(); j++ )
            tl.delete(man.getDeletedAt(j));

        for (int i = 0; i < man.count(); i++ ) {
            item = man.getItemAt(i);

            if (item.getId() == null) {
                item.setShippingId(man.getShippingId());
                tl.add(item);
            } else {
                tl.update(item);
            }
        }

        return man;
    }
    
    public void validate(ShippingItemManager man) throws Exception {
        ValidationErrorsList list;
        ShippingItemLocal il;
        int count;
        
        list = new ValidationErrorsList();
        il = local();
        count = man.count();
        
        if(count == 0) {
            list.add(new FieldErrorException("noShippingItemsException", null));
        } else {
            for (int i = 0; i < count; i++ ) {
                try {
                    il.validate(man.getItemAt(i));
                } catch (Exception e) {
                    DataBaseUtil.mergeException(list, e, "itemTable", i);
                }
            }
        }             
        
        if(list.size() > 0)
            throw list;
    } 
    
    private ShippingItemLocal local() {
        try {
            InitialContext ctx = new InitialContext();
            return (ShippingItemLocal)ctx.lookup("openelis/ShippingItemBean/local");
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

}
