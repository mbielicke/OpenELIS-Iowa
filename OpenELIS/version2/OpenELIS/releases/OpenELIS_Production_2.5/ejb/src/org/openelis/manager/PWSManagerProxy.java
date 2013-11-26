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
package org.openelis.manager;

import org.openelis.bean.PWSBean;
import org.openelis.domain.PWSDO;
import org.openelis.utils.EJBFactory;

public class PWSManagerProxy {

    public PWSManager fetchByTinwsysIsNumber(Integer tinwsysIsNumber) throws Exception {
        PWSBean pl;
        PWSDO data;
        PWSManager m;
        
        pl = EJBFactory.getPWS();
        data = pl.fetchByTinwsysIsNumber(tinwsysIsNumber);
        m = PWSManager.getInstance();
        
        m.setPWS(data);
        
        return m;
    }
    
    public PWSManager fetchWithFacilitites(Integer tinwsysIsNumber) throws Exception {
        PWSManager m;
        
        m = fetchByTinwsysIsNumber(tinwsysIsNumber);
        m.getFacilities();
        
        return m;
    }

    public PWSManager fetchWithAddresses(Integer tinwsysIsNumber) throws Exception {
        PWSManager m;
        
        m = fetchByTinwsysIsNumber(tinwsysIsNumber);
        m.getAddresses();
        
        return m;
    }

    public PWSManager fetchWithMonitors(Integer tinwsysIsNumber) throws Exception {
        PWSManager m;
        
        m = fetchByTinwsysIsNumber(tinwsysIsNumber);
        m.getMonitors();
        
        return m;
    }
}
