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

import javax.naming.InitialContext;

import org.openelis.domain.PwsDO;
import org.openelis.local.PwsLocal;

public class PwsManagerProxy {

    public PwsManager fetchByTinwsysIsNumber(Integer tinwsysIsNumber) throws Exception {
        PwsLocal pl;
        PwsDO data;
        PwsManager m;
        
        pl = local();
        data = pl.fetchByTinwsysIsNumber(tinwsysIsNumber);
        m = PwsManager.getInstance();
        
        m.setPws(data);
        
        return m;
    }
    
    public PwsManager fetchWithFacilitites(Integer tinwsysIsNumber) throws Exception {
        PwsManager m;
        
        m = fetchByTinwsysIsNumber(tinwsysIsNumber);
        m.getFacilities();
        
        return m;
    }

    public PwsManager fetchWithAddresses(Integer tinwsysIsNumber) throws Exception {
        PwsManager m;
        
        m = fetchByTinwsysIsNumber(tinwsysIsNumber);
        m.getAddresses();
        
        return m;
    }

    public PwsManager fetchWithMonitors(Integer tinwsysIsNumber) throws Exception {
        PwsManager m;
        
        m = fetchByTinwsysIsNumber(tinwsysIsNumber);
        m.getMonitors();
        
        return m;
    }
    
    private PwsLocal local() {
        try {
            InitialContext ctx = new InitialContext();
            return (PwsLocal)ctx.lookup("openelis/PwsBean/local");
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        }
    }
}
