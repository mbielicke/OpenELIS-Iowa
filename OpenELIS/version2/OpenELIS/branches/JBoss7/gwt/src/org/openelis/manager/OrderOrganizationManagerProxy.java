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

import org.openelis.cache.DictionaryCache;
import org.openelis.modules.order.client.OrderService;

public class OrderOrganizationManagerProxy {
    protected static Integer      orgBillToId, orgReportToId, orgSecondaryReportToId;

    public OrderOrganizationManagerProxy() {
        
        if (orgBillToId == null) {
            try {
                orgBillToId = DictionaryCache.getIdBySystemName("org_bill_to");
                orgReportToId = DictionaryCache.getIdBySystemName("org_report_to");
                orgSecondaryReportToId = DictionaryCache.getIdBySystemName("org_second_report_to");                
            } catch (Exception e) {
                e.printStackTrace();
                orgBillToId = null;
            }
        }
    }

    public OrderOrganizationManager fetchByOrderId(Integer OrderId) throws Exception {
        return OrderService.get().fetchOrganizationByOrderId(OrderId);
    }

    public OrderOrganizationManager add(OrderOrganizationManager man) throws Exception {
        assert false : "not supported";
        return null;
    }

    public OrderOrganizationManager update(OrderOrganizationManager man) throws Exception {
        assert false : "not supported";
        return null;
    }

    public void validate(OrderOrganizationManager man) throws Exception {
    }
}