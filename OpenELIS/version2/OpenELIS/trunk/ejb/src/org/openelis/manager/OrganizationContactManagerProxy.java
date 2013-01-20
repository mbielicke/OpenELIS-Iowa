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

import org.openelis.bean.OrganizationContactBean;
import org.openelis.domain.OrganizationContactDO;
import org.openelis.gwt.common.DataBaseUtil;
import org.openelis.gwt.common.ValidationErrorsList;
import org.openelis.utils.EJBFactory;

public class OrganizationContactManagerProxy {

    public OrganizationContactManager fetchByOrganizationId(Integer id) throws Exception {
        OrganizationContactManager cm;
        ArrayList<OrganizationContactDO> list;

        list = EJBFactory.getOrganizationContact().fetchByOrganizationId(id);
        cm = OrganizationContactManager.getInstance();
        cm.setOrganizationId(id);
        cm.setContacts(list);

        return cm;
    }

    public OrganizationContactManager add(OrganizationContactManager man) throws Exception {
        OrganizationContactBean cl;
        OrganizationContactDO data;

        cl = EJBFactory.getOrganizationContact();
        for (int i = 0; i < man.count(); i++ ) {
            data = man.getContactAt(i);
            data.setOrganizationId(man.getOrganizationId());
            cl.add(data);
        }

        return man;
    }

    public OrganizationContactManager update(OrganizationContactManager man) throws Exception {
        int i;
        OrganizationContactBean cl;
        OrganizationContactDO data;

        cl = EJBFactory.getOrganizationContact();
        for (i = 0; i < man.deleteCount(); i++ )
            cl.delete(man.getDeletedAt(i));

        for (i = 0; i < man.count(); i++ ) {
            data = man.getContactAt(i);

            if (data.getId() == null) {
                data.setOrganizationId(man.getOrganizationId());
                cl.add(data);
            } else {
                cl.update(data);
            }
        }

        return man;
    }
    
    public void validate(OrganizationContactManager man) throws Exception {
        ValidationErrorsList list;
        OrganizationContactBean cl;

        cl = EJBFactory.getOrganizationContact();
        list = new ValidationErrorsList();
        for (int i = 0; i < man.count(); i++ ) {
            try {
                cl.validate(man.getContactAt(i));
            } catch (Exception e) {
                DataBaseUtil.mergeException(list, e, "contactTable", i);
            }
        }
        if (list.size() > 0)
            throw list;
    }
}
