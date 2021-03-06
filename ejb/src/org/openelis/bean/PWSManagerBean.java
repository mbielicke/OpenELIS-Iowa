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
package org.openelis.bean;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.jboss.security.annotation.SecurityDomain;
import org.openelis.domain.PWSDO;
import org.openelis.manager.PWSAddressManager;
import org.openelis.manager.PWSFacilityManager;
import org.openelis.manager.PWSManager;
import org.openelis.manager.PWSMonitorManager;
import org.openelis.manager.PWSViolationManager;

@Stateless
@SecurityDomain("openelis")
public class PWSManagerBean {

    @EJB
    private PWSBean pws;

    public PWSManager fetchById(Integer id) throws Exception {
        PWSDO data;

        data = pws.fetchById(id);

        return fetchByTinwsysIsNumber(data.getTinwsysIsNumber());
    }

    public PWSManager fetchByTinwsysIsNumber(Integer tinwsysIsNumber) throws Exception {
        return PWSManager.fetchByTinwsysIsNumber(tinwsysIsNumber);
    }

    public PWSManager fetchWithFacilities(Integer tinwsysIsNumber) throws Exception {
        return PWSManager.fetchWithFacilities(tinwsysIsNumber);
    }

    public PWSManager fetchWithAddresses(Integer tinwsysIsNumber) throws Exception {
        return PWSManager.fetchWithAddresses(tinwsysIsNumber);
    }

    public PWSManager fetchWithMonitors(Integer tinwsysIsNumber) throws Exception {
        return PWSManager.fetchWithMonitors(tinwsysIsNumber);
    }

    public PWSManager fetchWithViolations(Integer tinwsysIsNumber) throws Exception {
        return PWSManager.fetchWithViolations(tinwsysIsNumber);
    }

    public PWSFacilityManager fetchFacilityByTinwsysIsNumber(Integer tinwsysIsNumber) throws Exception {
        return PWSFacilityManager.fetchByTinwsysIsNumber(tinwsysIsNumber);
    }

    public PWSAddressManager fetchAddressByTinwsysIsNumber(Integer tinwsysIsNumber) throws Exception {
        return PWSAddressManager.fetchByTinwsysIsNumber(tinwsysIsNumber);
    }

    public PWSMonitorManager fetchMonitorByTinwsysIsNumber(Integer tinwsysIsNumber) throws Exception {
        return PWSMonitorManager.fetchByTinwsysIsNumber(tinwsysIsNumber);
    }

    public PWSViolationManager fetchViolationByTinwsysIsNumber(Integer tinwsysIsNumber) throws Exception {
        return PWSViolationManager.fetchByTinwsysIsNumber(tinwsysIsNumber);
    }

}
