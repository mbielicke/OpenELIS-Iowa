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

import org.openelis.domain.PwsAddressDO;
import org.openelis.gwt.common.RPC;

public class PwsAddressManager implements RPC {

    private static final long serialVersionUID = 1L;
    
    protected Integer tinwsysIsNumber;
    protected ArrayList<PwsAddressDO> addresses;
    
    protected transient static PwsAddressManagerProxy proxy;
    
    protected PwsAddressManager() {
    }
    
    /**
     * Creates a new instance of this object.
     */
    public static PwsAddressManager getInstance() {
        return new PwsAddressManager();
    }
    
    public PwsAddressDO getAddressAt(int i) {
        return addresses.get(i);
    } 
    
    public int count() {
        if (addresses == null)
            return 0;
    
        return addresses.size();
    }

    public static PwsAddressManager fetchByTinwsysIsNumber(Integer tinwsysIsNumber) throws Exception {
        return proxy().fetchByTinwsysIsNumber(tinwsysIsNumber);
    }
    
    Integer getTinwsysIsNumber() {
        return tinwsysIsNumber;
    }

    void setTinwsysIsNumber(Integer tinwsysIsNumber) {
        this.tinwsysIsNumber = tinwsysIsNumber;
    }

    ArrayList<PwsAddressDO> getAddresses() {
        return addresses;
    }

    void setAddresses(ArrayList<PwsAddressDO> addresses) {
        this.addresses = addresses;
    }
    
    private static PwsAddressManagerProxy proxy() {
        if (proxy == null)
            proxy = new PwsAddressManagerProxy();
        return proxy;
    }
}

