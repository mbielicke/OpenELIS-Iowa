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
package org.openelis.modules.pws.server;

import java.util.ArrayList;

import javax.ejb.EJB;
import javax.servlet.annotation.WebServlet;

import org.openelis.bean.PWSBean;
import org.openelis.bean.PWSFileImportBean;
import org.openelis.bean.PWSManagerBean;
import org.openelis.bean.SDWISAdditionalScannerBean;
import org.openelis.bean.SDWISViolationScannerBean;
import org.openelis.bean.SessionCacheBean;
import org.openelis.domain.IdNameVO;
import org.openelis.domain.PWSDO;
import org.openelis.ui.common.DatabaseException;
import org.openelis.ui.common.ReportStatus;
import org.openelis.ui.common.data.Query;
import org.openelis.ui.server.RemoteServlet;
import org.openelis.manager.PWSAddressManager;
import org.openelis.manager.PWSFacilityManager;
import org.openelis.manager.PWSManager;
import org.openelis.manager.PWSMonitorManager;
import org.openelis.manager.PWSViolationManager;
import org.openelis.modules.pws.client.PWSServiceInt;

@WebServlet("/openelis/pws")
public class PWSServlet extends RemoteServlet implements PWSServiceInt {

    private static final long          serialVersionUID = 1L;

    @EJB
    private SessionCacheBean           session;

    @EJB
    PWSManagerBean                     pwsManager;

    @EJB
    PWSBean                            pws;

    @EJB
    PWSFileImportBean                  pwsFileImport;

    @EJB
    private SDWISViolationScannerBean  sdwisViolationScanner;

    @EJB
    private SDWISAdditionalScannerBean sdwisAdditionalScanner;

    public PWSManager fetchByTinwsysIsNumber(Integer tinwsysIsNumber) throws Exception {
        try {
            return pwsManager.fetchByTinwsysIsNumber(tinwsysIsNumber);
        } catch (Exception anyE) {
            throw serializeForGWT(anyE);
        }
    }

    public PWSManager fetchWithFacilities(Integer tinwsysIsNumber) throws Exception {
        try {
            return pwsManager.fetchWithFacilities(tinwsysIsNumber);
        } catch (Exception anyE) {
            throw serializeForGWT(anyE);
        }
    }

    public PWSManager fetchWithAddresses(Integer tinwsysIsNumber) throws Exception {
        try {
            return pwsManager.fetchWithAddresses(tinwsysIsNumber);
        } catch (Exception anyE) {
            throw serializeForGWT(anyE);
        }
    }

    public PWSManager fetchWithMonitors(Integer tinwsysIsNumber) throws Exception {
        try {
            return pwsManager.fetchWithMonitors(tinwsysIsNumber);
        } catch (Exception anyE) {
            throw serializeForGWT(anyE);
        }
    }

    public PWSManager fetchWithViolations(Integer tinwsysIsNumber) throws Exception {
        try {
            return pwsManager.fetchWithViolations(tinwsysIsNumber);
        } catch (Exception anyE) {
            throw serializeForGWT(anyE);
        }
    }

    public ArrayList<IdNameVO> query(Query query) throws Exception {
        try {
            return pws.query(query.getFields(),
                             query.getPage() * query.getRowsPerPage(),
                             query.getRowsPerPage());
        } catch (RuntimeException e) {
            throw new DatabaseException(e);
        }
    }

    //
    // support for OrganizationContactManager and OrganizationParameterManager
    //
    public PWSFacilityManager fetchFacilityByTinwsysIsNumber(Integer tinwsysIsNumber) throws Exception {
        try {
            return pwsManager.fetchFacilityByTinwsysIsNumber(tinwsysIsNumber);
        } catch (Exception anyE) {
            throw serializeForGWT(anyE);
        }
    }

    public PWSAddressManager fetchAddressByTinwsysIsNumber(Integer tinwsysIsNumber) throws Exception {
        try {
            return pwsManager.fetchAddressByTinwsysIsNumber(tinwsysIsNumber);
        } catch (Exception anyE) {
            throw serializeForGWT(anyE);
        }
    }

    public PWSMonitorManager fetchMonitorByTinwsysIsNumber(Integer tinwsysIsNumber) throws Exception {
        try {
            return pwsManager.fetchMonitorByTinwsysIsNumber(tinwsysIsNumber);
        } catch (Exception anyE) {
            throw serializeForGWT(anyE);
        }
    }

    public PWSViolationManager fetchViolationByTinwsysIsNumber(Integer tinwsysIsNumber) throws Exception {
        try {
            return pwsManager.fetchViolationByTinwsysIsNumber(tinwsysIsNumber);
        } catch (Exception anyE) {
            throw serializeForGWT(anyE);
        }
    }

    public PWSDO fetchPwsByNumber0(String pwsNumber0) throws Exception {
        try {
            return pws.fetchByNumber0(pwsNumber0);
        } catch (Exception anyE) {
            throw serializeForGWT(anyE);
        }
    }

    public void importFiles() throws Exception {
        try {
            pwsFileImport.importFiles();
        } catch (Exception anyE) {
            throw serializeForGWT(anyE);
        } catch (Throwable th) {
            throw serializeForGWT(th);
        }
    }

    public void sdwisViolationScan() throws Exception {
        try {
            sdwisViolationScanner.scan();
        } catch (Exception anyE) {
            throw serializeForGWT(anyE);
        } catch (Throwable th) {
            throw serializeForGWT(th);
        }
    }

    public void sdwisAdditionalScan() throws Exception {
        try {
            sdwisAdditionalScanner.scan();
        } catch (Exception anyE) {
            throw serializeForGWT(anyE);
        } catch (Throwable th) {
            throw serializeForGWT(th);
        }
    }

    @Override
    public ReportStatus getStatus() throws Exception {
        return (ReportStatus)session.getAttribute("PWSFileImport");
    }
}
