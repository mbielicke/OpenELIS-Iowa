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

import org.openelis.domain.OrderRecurrenceDO;
import org.openelis.gwt.services.ScreenService;

public class OrderManagerProxy {

    protected static final String MANAGER_SERVICE_URL = "org.openelis.modules.order.server.OrderService";
    protected ScreenService       service;

    public OrderManagerProxy() {
        service = new ScreenService("controller?service=" + MANAGER_SERVICE_URL);
    }

    public OrderManager fetchById(Integer id) throws Exception {
        return service.call("fetchById", id);
    }

    public OrderManager fetchWithItems(Integer id) throws Exception {
        return service.call("fetchWithItems", id);
    }

    public OrderManager fetchWithFills(Integer id) throws Exception {
        return service.call("fetchWithFills", id);
    }

    public OrderManager fetchWithNotes(Integer id) throws Exception {
        return service.call("fetchWithNotes", id);
    }
    
    public OrderManager fetchWithTestsAndContainers(Integer id) throws Exception {
        return service.call("fetchWithTestsAndContainers", id);
    }
    
    public OrderManager fetchWithRecurring(Integer id) throws Exception {
        return service.call("fetchWithRecurring", id);
    }
    
    public OrderRecurrenceDO fetchRecurrenceByOrderId(Integer id) throws Exception {
        return service.call("fetchRecurrenceByOrderId", id);
    }

    public OrderManager add(OrderManager man) throws Exception {
        return service.call("add", man);
    }

    public OrderManager update(OrderManager man) throws Exception {
        return service.call("update", man);
    }

    public OrderManager fetchForUpdate(Integer id) throws Exception {
        return service.call("fetchForUpdate", id);
    }

    public OrderManager abortUpdate(Integer id) throws Exception {
        return service.call("abortUpdate", id);
    }

    public void validate(OrderManager man) throws Exception {
    }
}