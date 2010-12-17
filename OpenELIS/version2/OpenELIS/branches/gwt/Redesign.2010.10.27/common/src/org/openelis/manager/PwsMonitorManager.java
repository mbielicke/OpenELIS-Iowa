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

import org.openelis.domain.PwsMonitorDO;
import org.openelis.gwt.common.RPC;

public class PwsMonitorManager implements RPC {

    private static final long serialVersionUID = 1L;
    
    protected Integer tinwsysIsNumber;
    protected ArrayList<PwsMonitorDO> monitors;
    
    protected transient static PwsMonitorManagerProxy proxy;
    
    protected PwsMonitorManager() {
    }
    
    /**
     * Creates a new instance of this object.
     */
    public static PwsMonitorManager getInstance() {
        return new PwsMonitorManager();
    }
    
    public PwsMonitorDO getMonitorAt(int i) {
        return monitors.get(i);
    } 
    
    public int count() {
        if (monitors == null)
            return 0;
    
        return monitors.size();
    }

    public static PwsMonitorManager fetchByTinwsysIsNumber(Integer tinwsysIsNumber) throws Exception {
        return proxy().fetchByTinwsysIsNumber(tinwsysIsNumber);
    }
    
    Integer getTinwsysIsNumber() {
        return tinwsysIsNumber;
    }

    void setTinwsysIsNumber(Integer tinwsysIsNumber) {
        this.tinwsysIsNumber = tinwsysIsNumber;
    }

    ArrayList<PwsMonitorDO> getMonitors() {
        return monitors;
    }

    void setMonitors(ArrayList<PwsMonitorDO> monitors) {
        this.monitors = monitors;
    }
    
    private static PwsMonitorManagerProxy proxy() {
        if (proxy == null)
            proxy = new PwsMonitorManagerProxy();
        return proxy;
    }
}