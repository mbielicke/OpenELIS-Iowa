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

import javax.naming.InitialContext;

import org.openelis.domain.OrganizationContactDO;
import org.openelis.gwt.common.DataBaseUtil;
import org.openelis.gwt.common.ValidationErrorsList;
import org.openelis.local.OrganizationContactLocal;
import org.openelis.manager.OrganizationContactManager;

public class OrganizationContactManagerProxy {

    public OrganizationContactManager fetchByOrganizationId(Integer id) throws Exception {
        OrganizationContactManager cm;
        ArrayList<OrganizationContactDO> contacts;

        contacts = local().fetchByOrganizationId(id);
        cm = OrganizationContactManager.getInstance();
        cm.setOrganizationId(id);
        cm.setContacts(contacts);

        return cm;
    }

    public OrganizationContactManager add(OrganizationContactManager man) throws Exception {
        OrganizationContactLocal cl;
        OrganizationContactDO contact;

        cl = local();
        for (int i = 0; i < man.count(); i++ ) {
            contact = man.getContactAt(i);
            contact.setOrganizationId(man.getOrganizationId());
            cl.add(contact);
        }

        return man;
    }

    public OrganizationContactManager update(OrganizationContactManager man) throws Exception {
        OrganizationContactLocal cl;
        OrganizationContactDO contact;

        cl = local();
        for (int j = 0; j < man.deleteCount(); j++ )
            cl.delete(man.getDeletedAt(j));

        for (int i = 0; i < man.count(); i++ ) {
            contact = man.getContactAt(i);

            if (contact.getId() == null) {
                contact.setOrganizationId(man.getOrganizationId());
                cl.add(contact);
            } else {
                cl.update(contact);
            }
        }

        return man;
    }
    
    public void validate(OrganizationContactManager man) throws Exception {
        ValidationErrorsList list;
        OrganizationContactLocal cl;

        cl = local();
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

    private OrganizationContactLocal local() {
        try {
            InitialContext ctx = new InitialContext();
            return (OrganizationContactLocal)ctx.lookup("openelis/OrganizationContactBean/local");
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        }
    }
}
