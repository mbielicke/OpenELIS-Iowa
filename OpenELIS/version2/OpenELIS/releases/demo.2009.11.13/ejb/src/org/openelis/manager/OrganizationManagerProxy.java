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

import javax.naming.InitialContext;

import org.openelis.domain.OrganizationViewDO;
import org.openelis.domain.ReferenceTable;
import org.openelis.gwt.common.ValidationErrorsList;
import org.openelis.local.OrganizationLocal;
import org.openelis.utilcommon.DataBaseUtil;

public class OrganizationManagerProxy {

    public OrganizationManager fetchById(Integer id) throws Exception {
        OrganizationLocal ol;
        OrganizationViewDO data;
        OrganizationManager m;

        ol = local();
        data = ol.fetchById(id);
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
        OrganizationLocal ol;

        ol = local();
        ol.add(man.getOrganization());
        id = man.getOrganization().getId();

        man.getContacts().setOrganizationId(id);
        man.getContacts().add();

        man.getParameters().setOrganizationId(id);
        man.getParameters().add();

        man.getNotes().setReferenceId(id);
        man.getNotes().setReferenceTableId(ReferenceTable.ORGANIZATION);
        man.getNotes().add();

        return man;
    }

    public OrganizationManager update(OrganizationManager man) throws Exception {
        Integer id;
        OrganizationLocal ol;

        ol = local();
        ol.update(man.getOrganization());
        id = man.getOrganization().getId();

        man.getContacts().setOrganizationId(id);
        man.getContacts().update();

        man.getParameters().setOrganizationId(id);
        man.getParameters().update();

        man.getNotes().setReferenceId(id);
        man.getNotes().setReferenceTableId(ReferenceTable.ORGANIZATION);
        man.getNotes().update();

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
            local().validate(man.getOrganization());
        } catch (Exception e) {
            DataBaseUtil.mergeException(list, e);
        }
        try {
            man.getContacts().validate();
        } catch (Exception e) {
            DataBaseUtil.mergeException(list, e);
        }
        try {
            man.getParameters().validate();
        } catch (Exception e) {
            DataBaseUtil.mergeException(list, e);
        }
        
        if (list.size() > 0)
            throw list;
    }

    private OrganizationLocal local() {
        try {
            InitialContext ctx = new InitialContext();
            return (OrganizationLocal)ctx.lookup("openelis/OrganizationBean/local");
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        }
    }
}