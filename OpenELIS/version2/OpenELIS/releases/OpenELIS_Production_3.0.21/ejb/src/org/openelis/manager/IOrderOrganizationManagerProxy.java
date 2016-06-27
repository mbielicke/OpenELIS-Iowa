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

import org.openelis.bean.IOrderOrganizationBean;
import org.openelis.constants.Messages;
import org.openelis.domain.Constants;
import org.openelis.domain.IOrderOrganizationViewDO;
import org.openelis.ui.common.DataBaseUtil;
import org.openelis.ui.common.FieldErrorException;
import org.openelis.ui.common.ValidationErrorsList;
import org.openelis.utils.EJBFactory;

public class IOrderOrganizationManagerProxy {

    public IOrderOrganizationManager fetchByIorderId(Integer id) throws Exception {
        ArrayList<IOrderOrganizationViewDO> orgs;
        IOrderOrganizationManager som;

        orgs = EJBFactory.getIOrderOrganization().fetchByIorderId(id);

        som = IOrderOrganizationManager.getInstance();
        som.setOrganizations(orgs);
        som.setIorderId(id);

        return som;
    }

    public IOrderOrganizationManager add(IOrderOrganizationManager man) throws Exception {
        IOrderOrganizationViewDO data;
        IOrderOrganizationBean l;

        l = EJBFactory.getIOrderOrganization();
        for (int i = 0; i < man.count(); i++ ) {
            data = man.getOrganizationAt(i);
            data.setIorderId(man.getIorderId());

            l.add(data);
        }

        return man;
    }

    public IOrderOrganizationManager update(IOrderOrganizationManager man) throws Exception {
        int i;
        IOrderOrganizationViewDO data;
        IOrderOrganizationBean l;

        l = EJBFactory.getIOrderOrganization();
        for (i = 0; i < man.deleteCount(); i++ ) {
            l.delete(man.getDeletedAt(i));
        }

        for (i = 0; i < man.count(); i++ ) {
            data = man.getOrganizationAt(i);

            if (data.getId() == null) {
                data.setIorderId(man.getIorderId());
                l.add(data);
            } else {
                l.update(data);
            }
        }

        return man;
    }

    public void validate(IOrderOrganizationManager man) throws Exception {
        int numBillTo, numReportTo;
        IOrderOrganizationViewDO data;
        ValidationErrorsList list;
        IOrderOrganizationBean ol;

        ol = EJBFactory.getIOrderOrganization();
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
            list.add(new FieldErrorException(Messages.get().multipleReportToException(), null));
        
        if (numBillTo > 1)
            list.add(new FieldErrorException(Messages.get().multipleBillToException(), null));

        if (list.size() > 0)
            throw list;
    }
}