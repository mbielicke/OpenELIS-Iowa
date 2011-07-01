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

import org.openelis.domain.IdNameVO;
import org.openelis.domain.OrganizationDO;
import org.openelis.gwt.common.DatabaseException;
import org.openelis.gwt.common.NotFoundException;
import org.openelis.gwt.common.data.Query;
import org.openelis.gwt.common.data.QueryData;
import org.openelis.manager.OrganizationContactManager;
import org.openelis.manager.OrganizationManager;
import org.openelis.manager.OrganizationParameterManager;
import org.openelis.persistence.EJBFactory;
import org.openelis.remote.OrganizationManagerRemote;
import org.openelis.remote.OrganizationRemote;

/*
 * This class provides service for OrganizationManager and
 * OrganizationContactManager.
 */
public class OrganizationService {

    private static final int rowPP = 20;

    public OrganizationManager fetchById(Integer id) throws Exception {
        return remoteManager().fetchById(id);
    }
    
    public ArrayList<OrganizationManager> fetchByIdList(Query query) throws Exception {
        Integer id;
        ArrayList<Integer> ids;
        ArrayList<OrganizationManager> list;
        
        ids = new ArrayList<Integer>();
        for (QueryData f : query.getFields()) {
            id = Integer.parseInt(f.query);
            ids.add(id);
        }
        list = remoteManager().fetchByIdList(ids);
        return list; 
    }
    
    public ArrayList<OrganizationDO> fetchByIdOrName(String search) throws Exception {
        int id;
        ArrayList<OrganizationDO> list;

        try {
            id = Integer.parseInt(search);
            list = new ArrayList<OrganizationDO>(1);
            list.add(remote().fetchActiveById(id));
        } catch (NumberFormatException e) {
            list = remote().fetchActiveByName(search + "%", 10);
        } catch (NotFoundException e) {
            list = new ArrayList<OrganizationDO>(0);
        } catch (RuntimeException e) {
            throw new DatabaseException(e);
        }
        return list;
    }       

    public OrganizationManager fetchWithContacts(Integer id) throws Exception {
        return remoteManager().fetchWithContacts(id);
    }
    
    public OrganizationManager fetchWithNotes(Integer id) throws Exception {
        return remoteManager().fetchWithNotes(id);
    }

    public OrganizationManager fetchWithParameters(Integer id) throws Exception {
        return remoteManager().fetchWithParameters(id);
    }
    
    public ArrayList<IdNameVO> query(Query query) throws Exception {
        return remote().query(query.getFields(), query.getPage() * rowPP, rowPP);
    }

    public OrganizationManager add(OrganizationManager man) throws Exception {
        return remoteManager().add(man);
    }

    public OrganizationManager update(OrganizationManager man) throws Exception {
        return remoteManager().update(man);
    }

    public OrganizationManager fetchForUpdate(Integer id) throws Exception {
        return remoteManager().fetchForUpdate(id);
    }

    public OrganizationManager abortUpdate(Integer id) throws Exception {
        return remoteManager().abortUpdate(id);
    }

    //
    // support for OrganizationContactManager and OrganizationParameterManager
    //
    public OrganizationContactManager fetchContactByOrganizationId(Integer id) throws Exception {
        return remoteManager().fetchContactByOrganizationId(id);
    }

    public OrganizationParameterManager fetchParameterByOrganizationId(Integer id) throws Exception {
        return remoteManager().fetchParameterByOrganizationId(id);
    }

    private OrganizationRemote remote() {
        return (OrganizationRemote)EJBFactory.lookup("openelis/OrganizationBean/remote");
    }

    private OrganizationManagerRemote remoteManager() {
        return (OrganizationManagerRemote)EJBFactory.lookup("openelis/OrganizationManagerBean/remote");
    }
}
