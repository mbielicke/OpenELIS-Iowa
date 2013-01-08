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

import org.openelis.domain.Constants;
import org.openelis.domain.OrderOrganizationViewDO;
import org.openelis.gwt.common.DataBaseUtil;
import org.openelis.gwt.common.FieldErrorException;
import org.openelis.gwt.common.ValidationErrorsList;
import org.openelis.local.OrderOrganizationLocal;
import org.openelis.utils.EJBFactory;

public class OrderOrganizationManagerProxy {

    public OrderOrganizationManager fetchByOrderId(Integer OrderId) throws Exception {
        ArrayList<OrderOrganizationViewDO> orgs;
        OrderOrganizationManager som;

        orgs = EJBFactory.getOrderOrganization().fetchByOrderId(OrderId);

        som = OrderOrganizationManager.getInstance();
        som.setOrganizations(orgs);
        som.setOrderId(OrderId);

        return som;
    }

    public OrderOrganizationManager add(OrderOrganizationManager man) throws Exception {
        OrderOrganizationViewDO data;
        OrderOrganizationLocal l;

        l = EJBFactory.getOrderOrganization();
        for (int i = 0; i < man.count(); i++ ) {
            data = man.getOrganizationAt(i);
            data.setOrderId(man.getOrderId());

            l.add(data);
        }

        return man;
    }

    public OrderOrganizationManager update(OrderOrganizationManager man) throws Exception {
        int i;
        OrderOrganizationViewDO data;
        OrderOrganizationLocal l;

        l = EJBFactory.getOrderOrganization();
        for (i = 0; i < man.deleteCount(); i++ ) {
            l.delete(man.getDeletedAt(i));
        }

        for (i = 0; i < man.count(); i++ ) {
            data = man.getOrganizationAt(i);

            if (data.getId() == null) {
                data.setOrderId(man.getOrderId());
                l.add(data);
            } else {
                l.update(data);
            }
        }

        return man;
    }

    public void validate(OrderOrganizationManager man) throws Exception {
        int numBillTo, numReportTo;
        OrderOrganizationViewDO data;
        ValidationErrorsList list;
        OrderOrganizationLocal ol;

        ol = EJBFactory.getOrderOrganization();
        numReportTo = 0;
        numBillTo = 0;
        list = new ValidationErrorsList();
        
        for (int i = 0; i < man.count(); i++ ) {
            data = man.getOrganizationAt(i);
            try {
                ol.validate(data);                
            } catch (Exception e) {
                DataBaseUtil.mergeException(list, e, "organizationTable", i);
            }
            
            if (Constants.dictionary().ORG_REPORT_TO.equals(data.getTypeId()))
                numReportTo++ ;
            
            if (Constants.dictionary().ORG_BILL_TO.equals(data.getTypeId()))
                numBillTo++ ;
        }

        if (numReportTo > 1)
            list.add(new FieldErrorException("multipleReportToException", null));
        
        if (numBillTo > 1)
            list.add(new FieldErrorException("multipleBillToException", null));

        if (list.size() > 0)
            throw list;
    }
}