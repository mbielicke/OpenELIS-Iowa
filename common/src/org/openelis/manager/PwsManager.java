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
package org.openelis.manager;

import org.openelis.domain.PwsDO;
import org.openelis.gwt.common.NotFoundException;
import org.openelis.gwt.common.RPC;

public class PwsManager implements RPC {

    private static final long                  serialVersionUID = 1L;

    protected PwsDO                            pws;
    protected PwsAddressManager                addresses;
    protected PwsFacilityManager               facilities;
    protected PwsMonitorManager                monitors;

    protected transient static PwsManagerProxy proxy;

    /**
     * This is a protected constructor. See the three static methods for
     * allocation.
     */
    protected PwsManager() {
        pws = null;
        addresses = null;
        facilities = null;
        monitors = null;
    }

    /**
     * Creates a new instance of this object. A default pws object is also
     * created.
     */
    public static PwsManager getInstance() {
        PwsManager manager;

        manager = new PwsManager();
        manager.pws = new PwsDO();
        
        return manager;
    }

    public PwsDO getPws() {
        return pws;
    }

    public void setPws(PwsDO pws) {
        this.pws = pws;
    }

    // service methods
    public static PwsManager fetchByTinwsysIsNumber(Integer tinwsysIsNumber) throws Exception {
        return proxy().fetchByTinwsysIsNumber(tinwsysIsNumber);
    }

    public static PwsManager fetchWithFacilities(Integer tinwsysIsNumber) throws Exception {
        return proxy().fetchWithFacilitites(tinwsysIsNumber);
    }

    public static PwsManager fetchWithAddresses(Integer tinwsysIsNumber) throws Exception {
        return proxy().fetchWithAddresses(tinwsysIsNumber);
    }

    public static PwsManager fetchWithMonitors(Integer tinwsysIsNumber) throws Exception {
        return proxy().fetchWithMonitors(tinwsysIsNumber);
    }

    //
    // other managers
    //
    public PwsFacilityManager getFacilities() throws Exception {
        if (facilities == null) {
            if (pws.getTinwsysIsNumber() != null) {
                try {
                    facilities = PwsFacilityManager.fetchByTinwsysIsNumber(pws.getTinwsysIsNumber());
                } catch (NotFoundException e) {
                    // ignore
                } catch (Exception e) {
                    throw e;
                }
            }
            if (facilities == null)
                facilities = PwsFacilityManager.getInstance();
        }
        return facilities;
    }
    
    public PwsAddressManager getAddresses() throws Exception {
        if (addresses == null) {
            if (pws.getTinwsysIsNumber() != null) {
                try {
                    addresses = PwsAddressManager.fetchByTinwsysIsNumber(pws.getTinwsysIsNumber());
                } catch (NotFoundException e) {
                    // ignore
                } catch (Exception e) {
                    throw e;
                }
            }
            if (addresses == null)
                addresses = PwsAddressManager.getInstance();
        }
        return addresses;
    }
    
    public PwsMonitorManager getMonitors() throws Exception {
        if (monitors == null) {
            if (pws.getTinwsysIsNumber() != null) {
                try {
                    monitors = PwsMonitorManager.fetchByTinwsysIsNumber(pws.getTinwsysIsNumber());
                } catch (NotFoundException e) {
                    // ignore
                } catch (Exception e) {
                    throw e;
                }
            }
            if (monitors == null)
                monitors = PwsMonitorManager.getInstance();
        }
        return monitors;
    }
    
    private static PwsManagerProxy proxy() {
        if (proxy == null)
            proxy = new PwsManagerProxy();
        
        return proxy;
    }

}
