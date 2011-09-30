/** Exhibit A - UIRF Open-source Based Public Software License.
* 
* The contents of this file are subject to the UIRF Open-source Based
* Public Software License(the "License"); you may not use this file except
* in compliance with the License. You may obtain a copy of the License at
* openelis.uhl.uiowa.edu
* 
* Software distributed under the License is distributed on an "AS IS"
* basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
* License for the specific language governing rights and limitations
* under the License.
* 
* The Original Code is OpenELIS code.
* 
* The Initial Developer of the Original Code is The University of Iowa.
* Portions created by The University of Iowa are Copyright 2006-2008. All
* Rights Reserved.
* 
* Contributor(s): ______________________________________.
* 
* Alternatively, the contents of this file marked
* "Separately-Licensed" may be used under the terms of a UIRF Software
* license ("UIRF Software License"), in which case the provisions of a
* UIRF Software License are applicable instead of those above. 
*/
package org.openelis.modules.pws.server;

import java.util.ArrayList;

import org.openelis.domain.IdNameVO;
import org.openelis.domain.PWSDO;
import org.openelis.gwt.common.DatabaseException;
import org.openelis.gwt.common.data.Query;
import org.openelis.manager.PWSAddressManager;
import org.openelis.manager.PWSFacilityManager;
import org.openelis.manager.PWSManager;
import org.openelis.manager.PWSMonitorManager;
import org.openelis.persistence.EJBFactory;
import org.openelis.remote.PWSManagerRemote;
import org.openelis.remote.PWSRemote;

public class PWSService {
    
    public PWSManager fetchByTinwsysIsNumber(Integer tinwsysIsNumber) throws Exception {
        try {
            return remoteManager().fetchByTinwsysIsNumber(tinwsysIsNumber);
        } catch (RuntimeException e) {
            throw new DatabaseException(e);
        }
    }
    
    public PWSManager fetchWithFacilities(Integer tinwsysIsNumber) throws Exception {
        try {
            return remoteManager().fetchWithFacilities(tinwsysIsNumber);
        } catch (RuntimeException e) {
            throw new DatabaseException(e);
        }
    }
    
    public PWSManager fetchWithAddresses(Integer tinwsysIsNumber) throws Exception {
        try {
            return remoteManager().fetchWithAddresses(tinwsysIsNumber);
        } catch (RuntimeException e) {
            throw new DatabaseException(e);
        }
    }
    
    public PWSManager fetchWithMonitors(Integer tinwsysIsNumber) throws Exception {
        try {
            return remoteManager().fetchWithMonitors(tinwsysIsNumber);
        } catch (RuntimeException e) {
            throw new DatabaseException(e);
        }
    }
    
    public ArrayList<IdNameVO> query(Query query) throws Exception {
        try {
            return remote().query(query.getFields(), query.getPage() * query.getRowsPerPage(), query.getRowsPerPage());
        } catch (RuntimeException e) {
            throw new DatabaseException(e);
        }
    }
    
    //
    // support for OrganizationContactManager and OrganizationParameterManager
    //
    public PWSFacilityManager fetchFacilityByTinwsysIsNumber(Integer tinwsysIsNumber) throws Exception {
        try {
            return remoteManager().fetchFacilityByTinwsysIsNumber(tinwsysIsNumber);
        } catch (RuntimeException e) {
            throw new DatabaseException(e);
        }
    }
    
    public PWSAddressManager fetchAddressByTinwsysIsNumber(Integer tinwsysIsNumber) throws Exception {
        try {
            return remoteManager().fetchAddressByTinwsysIsNumber(tinwsysIsNumber);
        } catch (RuntimeException e) {
            throw new DatabaseException(e);
        }
    }
    
    public PWSMonitorManager fetchMonitorByTinwsysIsNumber(Integer tinwsysIsNumber) throws Exception {
        try {
            return remoteManager().fetchMonitorByTinwsysIsNumber(tinwsysIsNumber);
        } catch (RuntimeException e) {
            throw new DatabaseException(e);
        }
    }
    
    public PWSDO fetchPwsByNumber0(String pwsNumber0) throws Exception {
        return remote().fetchByNumber0(pwsNumber0);
    }    
    
    private PWSRemote remote() {
        return (PWSRemote)EJBFactory.lookup("openelis/PWSBean/remote");  
    }   
    
    private PWSManagerRemote remoteManager() {
        return (PWSManagerRemote)EJBFactory.lookup("openelis/PWSManagerBean/remote");  
    } 
}
