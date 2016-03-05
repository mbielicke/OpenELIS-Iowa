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

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.jboss.security.annotation.SecurityDomain;
import org.openelis.constants.Messages;
import org.openelis.ui.common.ReportStatus;

/**
 * Reads PWS files and updates pws, pws_facility, pws_address, and pws_monitor
 * tables. These processes manage their own transactions in order to reduce
 * memory usage. Tables are not locked when data is being updated, so there
 * could be problems if the code is run when there are other operations being
 * performed on the tables.
 * 
 */
@Stateless
@SecurityDomain("openelis")
public class PWSFileImportBean {

    @EJB
    private SessionCacheBean            session;

    @EJB
    private PWSImportHelperBean         pwsImportHelper;

    @EJB
    private PWSFacilityImportHelperBean pwsFacilityImportHelper;

    @EJB
    private PWSAddressImportHelperBean  pwsAddressImportHelper;

    @EJB
    private PWSMonitorImportHelperBean  pwsMonitorImportHelper;

    private static final Logger         logger = Logger.getLogger("openelis");

    /**
     * parse all pws files and update the database with this data.
     */
    public void importFiles() throws Exception {
        ReportStatus status;

        status = new ReportStatus();
        status.setMessage(Messages.get().gen_initializing());
        status.setPercentComplete(0);
        session.setAttribute("PWSFileImport", status);
        try {
            logger.info("calling pws importer");
            pwsImportHelper.load(status);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Could not load PWS: " + e.getMessage(), e);
            throw e;
        }

        try {
            logger.info("calling pws facility importer");
            pwsFacilityImportHelper.load(status);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Could not load PWS Facilities: " + e.getMessage(), e);
            throw e;
        }

        try {
            logger.info("calling pws address importer");
            pwsAddressImportHelper.load(status);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Could not load PWS Addresses: " + e.getMessage(), e);
            throw e;
        }

        try {
            logger.info("calling pws monitor importer");
            pwsMonitorImportHelper.load(status);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Could not load PWS Monitors: " + e.getMessage(), e);
            throw e;
        }
    }
    
    /**
     * parses just the pws file and updates the database with this data.
     */
    public void importPWSFile() throws Exception {
        ReportStatus status;

        status = new ReportStatus();
        status.setMessage(Messages.get().gen_initializing());
        status.setPercentComplete(0);
        session.setAttribute("PWSFileImport", status);
        try {
            logger.info("calling pws importer");
            pwsImportHelper.load(status);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Could not load PWS: " + e.getMessage(), e);
            throw e;
        }
    }

    /**
     * parses just the pws facility file and updates the database with this data.
     */
    public void importFacilityFile() throws Exception {
        ReportStatus status;

        status = new ReportStatus();
        status.setMessage(Messages.get().gen_initializing());
        status.setPercentComplete(0);
        session.setAttribute("PWSFileImport", status);
        try {
            logger.info("calling pws facility importer");
            pwsFacilityImportHelper.load(status);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Could not load PWS Facilities: " + e.getMessage(), e);
            throw e;
        }
    }

    /**
     * parses just the pws address file and updates the database with this data.
     */
    public void importAddressFile() throws Exception {
        ReportStatus status;

        status = new ReportStatus();
        status.setMessage(Messages.get().gen_initializing());
        status.setPercentComplete(0);
        session.setAttribute("PWSFileImport", status);
        try {
            logger.info("calling pws address importer");
            pwsAddressImportHelper.load(status);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Could not load PWS Addresses: " + e.getMessage(), e);
            throw e;
        }
    }
    
    /**
     * parses just the pws monitor file and updates the database with this data.
     */
    public void importMonitorFile() throws Exception {
        ReportStatus status;

        status = new ReportStatus();
        status.setMessage(Messages.get().gen_initializing());
        status.setPercentComplete(0);
        session.setAttribute("PWSFileImport", status);
        try {
            logger.info("calling pws monitor importer");
            pwsMonitorImportHelper.load(status);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Could not load PWS Monitors: " + e.getMessage(), e);
            throw e;
        }
    }
}