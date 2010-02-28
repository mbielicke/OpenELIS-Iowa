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
package org.openelis.bean;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;

import org.jboss.annotation.security.SecurityDomain;
import org.openelis.manager.PwsAddressManager;
import org.openelis.manager.PwsFacilityManager;
import org.openelis.manager.PwsManager;
import org.openelis.manager.PwsMonitorManager;
import org.openelis.remote.PwsManagerRemote;

@Stateless
@SecurityDomain("openelis")
@RolesAllowed("pws-select")
public class PwsManagerBean implements PwsManagerRemote {

    public PwsManager fetchByTinwsysIsNumber(Integer tinwsysIsNumber) throws Exception {        
        return PwsManager.fetchByTinwsysIsNumber(tinwsysIsNumber);
    }
    
    public PwsManager fetchWithFacilities(Integer tinwsysIsNumber) throws Exception {        
        return PwsManager.fetchWithFacilities(tinwsysIsNumber);
    }
    
    public PwsManager fetchWithAddresses(Integer tinwsysIsNumber) throws Exception {       
        return PwsManager.fetchWithAddresses(tinwsysIsNumber);
    }

    public PwsManager fetchWithMonitors(Integer tinwsysIsNumber) throws Exception {        
        return PwsManager.fetchWithMonitors(tinwsysIsNumber);
    }
    
    public PwsFacilityManager fetchFacilityByTinwsysIsNumber(Integer tinwsysIsNumber) throws Exception {        
        return PwsFacilityManager.fetchByTinwsysIsNumber(tinwsysIsNumber);
    }
    
    public PwsAddressManager fetchAddressByTinwsysIsNumber(Integer tinwsysIsNumber) throws Exception {
        return PwsAddressManager.fetchByTinwsysIsNumber(tinwsysIsNumber);
    }

    public PwsMonitorManager fetchMonitorByTinwsysIsNumber(Integer tinwsysIsNumber) throws Exception {        
        return PwsMonitorManager.fetchByTinwsysIsNumber(tinwsysIsNumber);
    }

}
