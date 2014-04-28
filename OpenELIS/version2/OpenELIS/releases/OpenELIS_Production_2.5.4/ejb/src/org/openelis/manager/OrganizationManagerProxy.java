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

import org.openelis.domain.Constants;
import org.openelis.domain.OrganizationViewDO;
import org.openelis.ui.common.DataBaseUtil;
import org.openelis.ui.common.ValidationErrorsList;
import org.openelis.utils.EJBFactory;

public class OrganizationManagerProxy {

    public OrganizationManager fetchById(Integer id) throws Exception {
        OrganizationViewDO data;
        OrganizationManager m;

        data = EJBFactory.getOrganization().fetchById(id);
        m = OrganizationManager.getInstance();

        m.setOrganization(data);

        return m;
    }

    public OrganizationManager fetchWithContacts(Integer id) throws Exception {
        OrganizationManager m;

        m = fetchById(id);
        m.getContacts();

        return m;
    }

    public OrganizationManager fetchWithNotes(Integer id) throws Exception {
        OrganizationManager m;

        m = fetchById(id);
        m.getNotes();

        return m;
    }

    public OrganizationManager fetchWithParameters(Integer id) throws Exception {
        OrganizationManager m;

        m = fetchById(id);
        m.getParameters();

        return m;
    }

    public OrganizationManager add(OrganizationManager man) throws Exception {
        Integer id;

        EJBFactory.getOrganization().add(man.getOrganization());
        id = man.getOrganization().getId();

        if (man.contacts != null) {
            man.getContacts().setOrganizationId(id);
            man.getContacts().add();
        }
        if (man.parameters != null) {
            man.getParameters().setOrganizationId(id);
            man.getParameters().add();
        }
        if (man.notes != null) {
            man.getNotes().setReferenceId(id);
            man.getNotes().setReferenceTableId(Constants.table().ORGANIZATION);
            man.getNotes().add();
        }

        return man;
    }

    public OrganizationManager update(OrganizationManager man) throws Exception {
        Integer id;

        EJBFactory.getOrganization().update(man.getOrganization());
        id = man.getOrganization().getId();

        if (man.contacts != null) {
            man.getContacts().setOrganizationId(id);
            man.getContacts().update();
        }
        if (man.parameters != null) {
            man.getParameters().setOrganizationId(id);
            man.getParameters().update();
        }
        if (man.notes != null) {
            man.getNotes().setReferenceId(id);
            man.getNotes().setReferenceTableId(Constants.table().ORGANIZATION);
            man.getNotes().update();
        }

        return man;
    }

    public OrganizationManager updateForNotify(OrganizationManager man) throws Exception {
        Integer id;

        id = man.getOrganization().getId();
        if (man.parameters != null) {
            man.getParameters().setOrganizationId(id);
            man.getParameters().update();
        }

        return man;
    }

    public OrganizationManager fetchForUpdate(OrganizationManager man) throws Exception {
        assert false : "not supported";
        return null;
    }

    public OrganizationManager abortUpdate(Integer id) throws Exception {
        assert false : "not supported";
        return null;
    }

    public void validate(OrganizationManager man) throws Exception {
        ValidationErrorsList list;

        list = new ValidationErrorsList();
        try {
            EJBFactory.getOrganization().validate(man.getOrganization());
        } catch (Exception e) {
            DataBaseUtil.mergeException(list, e);
        }
        try {
            if (man.contacts != null)
                man.getContacts().validate();
        } catch (Exception e) {
            DataBaseUtil.mergeException(list, e);
        }
        try {
            if (man.parameters != null)
                man.getParameters().validate();
        } catch (Exception e) {
            DataBaseUtil.mergeException(list, e);
        }

        if (list.size() > 0)
            throw list;
    }
}