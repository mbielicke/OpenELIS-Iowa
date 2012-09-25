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
import org.openelis.domain.OrganizationParameterDO;
import org.openelis.gwt.common.DatabaseException;
import org.openelis.gwt.common.NotFoundException;
import org.openelis.gwt.common.data.Query;
import org.openelis.gwt.common.data.QueryData;
import org.openelis.manager.OrganizationContactManager;
import org.openelis.manager.OrganizationManager;
import org.openelis.manager.OrganizationParameterManager;
import org.openelis.remote.OrganizationRemote;
import org.openelis.server.EJBFactory;

/*
 * This class provides service for OrganizationManager and
 * OrganizationContactManager.
 */
public class OrganizationService {

    public OrganizationManager fetchById(Integer id) throws Exception {
        return EJBFactory.getOrganizationManager().fetchById(id);
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
        list = EJBFactory.getOrganizationManager().fetchByIdList(ids);
        return list;
    }

    public ArrayList<OrganizationDO> fetchByIdOrName(String search) throws Exception {
        int id;
        ArrayList<OrganizationDO> list;
        OrganizationRemote remote;

        remote = EJBFactory.getOrganization();
        try {
            id = Integer.parseInt(search);
            list = new ArrayList<OrganizationDO>(1);
            list.add(remote.fetchActiveById(id));
        } catch (NumberFormatException e) {
            list = remote.fetchActiveByName(search + "%", 10);
        } catch (NotFoundException e) {
            list = new ArrayList<OrganizationDO>(0);
        } catch (RuntimeException e) {
            throw new DatabaseException(e);
        }
        return list;
    }
    
    public ArrayList<OrganizationParameterDO> fetchParametersByDictionarySystemName(String systemName) throws Exception {
        return EJBFactory.getOrganization().fetchParametersByDictionarySystemName(systemName);
    }

    public OrganizationManager fetchWithContacts(Integer id) throws Exception {
        return EJBFactory.getOrganizationManager().fetchWithContacts(id);
    }

    public OrganizationManager fetchWithNotes(Integer id) throws Exception {
        return EJBFactory.getOrganizationManager().fetchWithNotes(id);
    }

    public OrganizationManager fetchWithParameters(Integer id) throws Exception {
        return EJBFactory.getOrganizationManager().fetchWithParameters(id);
    }

    public ArrayList<IdNameVO> query(Query query) throws Exception {
        return EJBFactory.getOrganization().query(query.getFields(),
                                                  query.getPage() * query.getRowsPerPage(),
                                                  query.getRowsPerPage());
    }

    public OrganizationManager add(OrganizationManager man) throws Exception {
        return EJBFactory.getOrganizationManager().add(man);
    }

    public OrganizationManager update(OrganizationManager man) throws Exception {
        return EJBFactory.getOrganizationManager().update(man);
    }

    public OrganizationManager updateForNotify(OrganizationManager man) throws Exception {
        return EJBFactory.getOrganizationManager().updateForNotify(man);
    }

    public OrganizationManager fetchForUpdate(Integer id) throws Exception {
        return EJBFactory.getOrganizationManager().fetchForUpdate(id);
    }

    public OrganizationManager abortUpdate(Integer id) throws Exception {
        return EJBFactory.getOrganizationManager().abortUpdate(id);
    }

    //
    // support for OrganizationContactManager and OrganizationParameterManager
    //
    public OrganizationContactManager fetchContactByOrganizationId(Integer id) throws Exception {
        return EJBFactory.getOrganizationManager().fetchContactByOrganizationId(id);
    }

    public OrganizationParameterManager fetchParameterByOrganizationId(Integer id) throws Exception {
        return EJBFactory.getOrganizationManager().fetchParameterByOrganizationId(id);
    }

}
