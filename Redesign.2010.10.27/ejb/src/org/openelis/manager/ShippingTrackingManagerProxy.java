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

import org.openelis.domain.ShippingTrackingDO;
import org.openelis.gwt.common.DataBaseUtil;
import org.openelis.gwt.common.ValidationErrorsList;
import org.openelis.local.ShippingTrackingLocal;

public class ShippingTrackingManagerProxy {
    
    public ShippingTrackingManager fetchByShippingId(Integer id) throws Exception {
        ShippingTrackingManager m;
        ArrayList<ShippingTrackingDO> trackings;
    
        trackings = local().fetchByShippingId(id);
        m = ShippingTrackingManager.getInstance();
        m.setShippingId(id);
        m.setTrackings(trackings);
        
        return m;
    }
    
    public ShippingTrackingManager add(ShippingTrackingManager man) throws Exception {
        ShippingTrackingLocal tl; 
        ShippingTrackingDO data;
    
        tl = local();
        
        for (int i = 0; i < man.count(); i++ ) {
            data = man.getTrackingAt(i);
            data.setShippingId(man.getShippingId());
            tl.add(data);
        }

        return man;
    }
    
    public ShippingTrackingManager update(ShippingTrackingManager man) throws Exception {
        ShippingTrackingLocal tl; 
        ShippingTrackingDO data;
    
        tl = local();
        
        for (int j = 0; j < man.deleteCount(); j++ )
            tl.delete(man.getDeletedAt(j));

        for (int i = 0; i < man.count(); i++ ) {
            data = man.getTrackingAt(i);

            if (data.getId() == null) {
                data.setShippingId(man.getShippingId());
                tl.add(data);                
            } else {                
                tl.update(data);
            }
        }

        return man;
    }
    
    public void validate(ShippingTrackingManager man) throws Exception {
        ValidationErrorsList list;
        ShippingTrackingLocal tl;
        
        tl = local();
        list = new ValidationErrorsList();
        for (int i = 0; i < man.count(); i++ ) {
            try {
                tl.validate(man.getTrackingAt(i));
            } catch (Exception e) {
                DataBaseUtil.mergeException(list, e, "itemTable", i);
            }
        }
        if (list.size() > 0)
            throw list;
    } 
    
    private ShippingTrackingLocal local() {
        try {
            InitialContext ctx = new InitialContext();
            return (ShippingTrackingLocal)ctx.lookup("openelis/ShippingTrackingBean/local");
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

}
