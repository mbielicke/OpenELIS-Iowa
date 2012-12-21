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
package org.openelis.modules.shipping.server;

import java.util.ArrayList;

import javax.ejb.EJB;
import javax.servlet.annotation.WebServlet;

import org.openelis.bean.ShippingBean;
import org.openelis.bean.ShippingManagerBean;
import org.openelis.domain.IdNameVO;
import org.openelis.domain.ShippingViewDO;
import org.openelis.gwt.common.data.Query;
import org.openelis.gwt.server.AppServlet;
import org.openelis.manager.ShippingItemManager;
import org.openelis.manager.ShippingManager;
import org.openelis.manager.ShippingTrackingManager;
import org.openelis.modules.shipping.client.ShippingServiceInt;

@WebServlet("/openelis/shipping")
public class ShippingServlet extends AppServlet implements ShippingServiceInt {
            
    private static final long serialVersionUID = 1L;
    
    @EJB
    ShippingManagerBean shippingManager;
    
    @EJB
    ShippingBean        shipping;
    

    public ShippingManager fetchById(Integer id) throws Exception {
        return shippingManager.fetchById(id);
    }    
    
    public ShippingViewDO fetchByOrderId(Integer id) throws Exception {
        return shipping.fetchByOrderId(id);
    }
    
    public ShippingManager fetchWithItemsAndTrackings (Integer id) throws Exception {
        return shippingManager.fetchWithItemsAndTracking(id);
    }
    
    public ShippingManager fetchWithNotes(Integer id) throws Exception {
        return shippingManager.fetchWithNotes(id);
    }
    
    public ArrayList<IdNameVO> query(Query query) throws Exception {
        return shipping.query(query.getFields(), query.getPage() * query.getRowsPerPage(), query.getRowsPerPage());
    }
    
    public ShippingManager add(ShippingManager man) throws Exception {
        return shippingManager.add(man);
    }
    
    public ShippingManager update(ShippingManager man) throws Exception {
        return shippingManager.update(man);
    }
    
    public ShippingManager fetchForUpdate(Integer id) throws Exception {
        return shippingManager.fetchForUpdate(id);
    }
    
    public ShippingManager abortUpdate(Integer id) throws Exception {
        return shippingManager.abortUpdate(id);
    }
    
    public ShippingItemManager fetchItemByShippingId(Integer id) throws Exception {
        return shippingManager.fetchItemByShippingId(id);
    }
    
    public ShippingTrackingManager fetchTrackingByShippingId(Integer id) throws Exception {
        return shippingManager.fetchTrackingByShippingId(id);
    }    
}