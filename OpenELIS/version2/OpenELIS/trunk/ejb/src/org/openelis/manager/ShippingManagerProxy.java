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

import javax.naming.InitialContext;

import org.openelis.domain.ReferenceTable;
import org.openelis.domain.ShippingViewDO;
import org.openelis.gwt.common.ValidationErrorsList;
import org.openelis.local.ShippingLocal;
import org.openelis.utilcommon.DataBaseUtil;

public class ShippingManagerProxy {
    
    public ShippingManager fetchById(Integer id) throws Exception {
        ShippingViewDO data;
        ShippingManager m;
        
        data = local().fetchById(id);
        m = ShippingManager.getInstance();

        m.setShipping(data);

        return m;
    }
    
    public ShippingManager fetchWithItemsAndTrackings(Integer id) throws Exception {
        ShippingManager m;
        
        m = fetchById(id);
        m.getItems();
        m.getTrackings();

        return m;        
    }
    
    public ShippingManager fetchWithNotes(Integer id) throws Exception {
        ShippingManager m;
        
        m = fetchById(id);
        m.getNotes();

        return m;
    }
    
    public ShippingManager add(ShippingManager man) throws Exception {
        Integer id;

        local().add(man.getShipping());
        id = man.getShipping().getId();                
        
        if (man.items != null) {
            man.getItems().setShippingId(id);
            man.getItems().add();
        }       
        
        if (man.trackings != null) {
            man.getTrackings().setShippingId(id);
            man.getTrackings().add();
        }
        
        if (man.shipNotes != null) {
            man.getNotes().setReferenceId(id);
            man.getNotes().setReferenceTableId(ReferenceTable.SHIPPING);
            man.getNotes().add();
        }
        
        return man;
    }
    
    public ShippingManager update(ShippingManager man) throws Exception {
        Integer id;

        local().update(man.getShipping());
        id = man.getShipping().getId();
        
        if (man.items != null) {
            man.getItems().setShippingId(id);
            man.getItems().update();
        }
        
        if (man.trackings != null) {
            man.getTrackings().setShippingId(id);
            man.getTrackings().update();
        }
        
        if (man.shipNotes != null) {
            man.getNotes().setReferenceId(id);
            man.getNotes().setReferenceTableId(ReferenceTable.SHIPPING);
            man.getNotes().update();
        }
        
        return man;
    }
    
    public ShippingManager fetchForUpdate(ShippingManager man) throws Exception {
        assert false : "not supported";
        return null;
    }
    
    public ShippingManager abortUpdate(Integer id) throws Exception {
        assert false : "not supported";
        return null;
    }
    
    public void validate(ShippingManager man) throws Exception {
        ValidationErrorsList list;
        
        list = new ValidationErrorsList();
        try {
            local().validate(man.getShipping());
        } catch (Exception e) {
            DataBaseUtil.mergeException(list, e);
        }       
        
        try {            
            if (man.items != null)
                man.getItems().validate();
        } catch (Exception e) {
            DataBaseUtil.mergeException(list, e);
        }                
        
        try {            
            if (man.trackings != null)
                man.getTrackings().validate();
        } catch (Exception e) {
            DataBaseUtil.mergeException(list, e);
        }    
        
        if (list.size() > 0)
            throw list;
    }
    
    private ShippingLocal local() {
        try {
            InitialContext ctx = new InitialContext();
            return (ShippingLocal)ctx.lookup("openelis/ShippingBean/local");
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        }
            
    }

}
