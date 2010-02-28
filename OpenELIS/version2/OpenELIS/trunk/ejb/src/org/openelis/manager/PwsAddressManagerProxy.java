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

import java.util.ArrayList;

import javax.naming.InitialContext;

import org.openelis.domain.PwsAddressDO;
import org.openelis.local.PwsAddressLocal;


public class PwsAddressManagerProxy {

    public PwsAddressManager fetchByTinwsysIsNumber(Integer tinwsysIsNumber) throws Exception {
        PwsAddressManager man;
        ArrayList<PwsAddressDO> addresses;
        
        addresses = local().fetchByTinwsysIsNumber(tinwsysIsNumber);
        man = PwsAddressManager.getInstance();
        man.setTinwsysIsNumber(tinwsysIsNumber);
        man.setAddresses(addresses);
        
        return man;
    }
    
    private PwsAddressLocal local() {
        try {
            InitialContext ctx = new InitialContext();
            return (PwsAddressLocal) ctx.lookup("openelis/PwsAddressBean/local");
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        }
    }
}
