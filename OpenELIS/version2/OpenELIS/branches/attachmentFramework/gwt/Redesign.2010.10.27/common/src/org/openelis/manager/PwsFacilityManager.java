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

import org.openelis.domain.PwsFacilityDO;
import org.openelis.gwt.common.RPC;

public class PwsFacilityManager implements RPC {

    private static final long serialVersionUID = 1L;
    
    protected Integer tinwsysIsNumber;
    protected ArrayList<PwsFacilityDO> facilities;
    
    protected transient static PwsFacilityManagerProxy proxy;
    
    protected PwsFacilityManager() {
    }
    
    /**
     * Creates a new instance of this object.
     */
    public static PwsFacilityManager getInstance() {
        return new PwsFacilityManager();
    }
    
    public PwsFacilityDO getFacilityAt(int i) {
        return facilities.get(i);
    } 
    
    public int count() {
        if (facilities == null)
            return 0;
    
        return facilities.size();
    }

    public static PwsFacilityManager fetchByTinwsysIsNumber(Integer tinwsysIsNumber) throws Exception {
        return proxy().fetchByTinwsysIsNumber(tinwsysIsNumber);
    }
    
    Integer getTinwsysIsNumber() {
        return tinwsysIsNumber;
    }

    void setTinwsysIsNumber(Integer tinwsysIsNumber) {
        this.tinwsysIsNumber = tinwsysIsNumber;
    }

    ArrayList<PwsFacilityDO> getFacilities() {
        return facilities;
    }

    void setFacilities(ArrayList<PwsFacilityDO> facilities) {
        this.facilities = facilities;
    }
    
    private static PwsFacilityManagerProxy proxy() {
        if (proxy == null)
            proxy = new PwsFacilityManagerProxy();
        return proxy;
    }
}
