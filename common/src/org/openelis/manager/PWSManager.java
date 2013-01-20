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

import java.io.Serializable;

import org.openelis.domain.PWSDO;
import org.openelis.gwt.common.NotFoundException;

public class PWSManager implements Serializable {

    private static final long                  serialVersionUID = 1L;

    protected PWSDO                            pws;
    protected PWSAddressManager                addresses;
    protected PWSFacilityManager               facilities;
    protected PWSMonitorManager                monitors;

    protected transient static PWSManagerProxy proxy;

    /**
     * This is a protected constructor. See the three static methods for
     * allocation.
     */
    protected PWSManager() {
        pws = null;
        addresses = null;
        facilities = null;
        monitors = null;
    }

    /**
     * Creates a new instance of this object. A default pws object is also
     * created.
     */
    public static PWSManager getInstance() {
        PWSManager manager;

        manager = new PWSManager();
        manager.pws = new PWSDO();
        
        return manager;
    }

    public PWSDO getPWS() {
        return pws;
    }

    public void setPWS(PWSDO pws) {
        this.pws = pws;
    }

    // service methods
    public static PWSManager fetchByTinwsysIsNumber(Integer tinwsysIsNumber) throws Exception {
        return proxy().fetchByTinwsysIsNumber(tinwsysIsNumber);
    }

    public static PWSManager fetchWithFacilities(Integer tinwsysIsNumber) throws Exception {
        return proxy().fetchWithFacilitites(tinwsysIsNumber);
    }

    public static PWSManager fetchWithAddresses(Integer tinwsysIsNumber) throws Exception {
        return proxy().fetchWithAddresses(tinwsysIsNumber);
    }

    public static PWSManager fetchWithMonitors(Integer tinwsysIsNumber) throws Exception {
        return proxy().fetchWithMonitors(tinwsysIsNumber);
    }

    //
    // other managers
    //
    public PWSFacilityManager getFacilities() throws Exception {
        if (facilities == null) {
            if (pws.getTinwsysIsNumber() != null) {
                try {
                    facilities = PWSFacilityManager.fetchByTinwsysIsNumber(pws.getTinwsysIsNumber());
                } catch (NotFoundException e) {
                    // ignore
                } catch (Exception e) {
                    throw e;
                }
            }
            if (facilities == null)
                facilities = PWSFacilityManager.getInstance();
        }
        return facilities;
    }
    
    public PWSAddressManager getAddresses() throws Exception {
        if (addresses == null) {
            if (pws.getTinwsysIsNumber() != null) {
                try {
                    addresses = PWSAddressManager.fetchByTinwsysIsNumber(pws.getTinwsysIsNumber());
                } catch (NotFoundException e) {
                    // ignore
                } catch (Exception e) {
                    throw e;
                }
            }
            if (addresses == null)
                addresses = PWSAddressManager.getInstance();
        }
        return addresses;
    }
    
    public PWSMonitorManager getMonitors() throws Exception {
        if (monitors == null) {
            if (pws.getTinwsysIsNumber() != null) {
                try {
                    monitors = PWSMonitorManager.fetchByTinwsysIsNumber(pws.getTinwsysIsNumber());
                } catch (NotFoundException e) {
                    // ignore
                } catch (Exception e) {
                    throw e;
                }
            }
            if (monitors == null)
                monitors = PWSMonitorManager.getInstance();
        }
        return monitors;
    }
    
    private static PWSManagerProxy proxy() {
        if (proxy == null)
            proxy = new PWSManagerProxy();
        
        return proxy;
    }

}
