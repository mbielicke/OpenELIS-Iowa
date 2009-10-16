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
package org.openelis.modules.organization.server;

import java.util.ArrayList;

import org.openelis.common.AutocompleteRPC;
import org.openelis.domain.IdNameVO;
import org.openelis.domain.OrganizationDO;
import org.openelis.gwt.common.DatabaseException;
import org.openelis.gwt.common.data.Query;
import org.openelis.gwt.widget.QueryFieldUtil;
import org.openelis.manager.OrganizationContactManager;
import org.openelis.manager.OrganizationManager;
import org.openelis.persistence.EJBFactory;
import org.openelis.remote.OrganizationManagerRemote;
import org.openelis.remote.OrganizationRemote;

/*
 * This class provides service for OrganizationManager and OrganizationContactManager. 
 */
public class OrganizationService {

    private static final int rowPP = 20;

    public OrganizationManager fetchById(Integer id) throws Exception {
        try {
            return remoteManager().fetchById(id);
        } catch (RuntimeException e) {
            throw new DatabaseException(e);
        }
    }

    public OrganizationManager fetchWithContacts(Integer id) throws Exception {
        try {
            return remoteManager().fetchWithContacts(id);
        } catch (RuntimeException e) {
            throw new DatabaseException(e);
        }
    }

    public OrganizationManager fetchWithNotes(Integer id) throws Exception {
        try {
            return remoteManager().fetchWithNotes(id);
        } catch (RuntimeException e) {
            throw new DatabaseException(e);
        }
    }

    public OrganizationManager fetchWithIdentifiers(Integer id) throws Exception {
        try {
            return remoteManager().fetchWithIdentifiers(id);
        } catch (RuntimeException e) {
            throw new DatabaseException(e);
        }
    }

    public ArrayList<IdNameVO> query(Query query) throws Exception {
        try {
            return remote().query(query.getFields(), query.getPage() * rowPP, rowPP);
        } catch (RuntimeException e) {
            throw new DatabaseException(e);
        }
    }

    public OrganizationManager add(OrganizationManager man) throws Exception {
        try {
            return remoteManager().add(man);
        } catch (RuntimeException e) {
            throw new DatabaseException(e);
        }
    }

    public OrganizationManager update(OrganizationManager man) throws Exception {
        try {
            return remoteManager().update(man);
        } catch (RuntimeException e) {
            throw new DatabaseException(e);
        }

    }

    public OrganizationManager fetchForUpdate(Integer id) throws Exception {
        try {
            return remoteManager().fetchForUpdate(id);
        } catch (RuntimeException e) {
            throw new DatabaseException(e);
        }
    }

    public OrganizationManager abortUpdate(Integer id) throws Exception {
        try {
            return remoteManager().abortUpdate(id);
        } catch (RuntimeException e) {
            throw new DatabaseException(e);
        }
    }

    public ArrayList<OrganizationDO> fetchByIdOrName(Query query) throws Exception {
        int id;
        String value;
        ArrayList<OrganizationDO> list;
        
        value = query.getFields().get(0).query;
        try {
            id = Integer.parseInt(value);
            list = new ArrayList<OrganizationDO>(1);
            list.add(remote().fetchActiveById(id));
            return list;
        } catch (NumberFormatException e) {
            return remote().fetchActiveByName(value+"%", 10);
        }
    }

    //
    // support for OrganizationContactManager
    //
    public OrganizationContactManager fetchContactByOrganizationId(Integer id) throws Exception {
        try {
            return remoteManager().fetchContactByOrganizationId(id);
        } catch (RuntimeException e) {
            throw new DatabaseException(e);
        }
    }
    
    private OrganizationRemote remote() {
        return (OrganizationRemote)EJBFactory.lookup("openelis/OrganizationBean/remote");
    }

    private OrganizationManagerRemote remoteManager() {
        return (OrganizationManagerRemote)EJBFactory.lookup("openelis/OrganizationManagerBean/remote");
    }
}
