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

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Logger;

import javax.annotation.security.RolesAllowed;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.jboss.security.annotation.SecurityDomain;
import org.openelis.domain.PWSFacilityDO;
import org.openelis.exception.ParseException;
import org.openelis.gwt.common.DataBaseUtil;
import org.openelis.ui.common.ReportStatus;
import org.openelis.utils.ParseUtil;

/**
 * Reads data from a PWS Facility file and updates the pws_facility table in the
 * database. This bean manages its own transactions in order to reduce memory
 * usage.
 */
@Stateless
@SecurityDomain("openelis")

public class PWSFacilityImportHelperBean {

    @PersistenceContext(unitName = "openelis")
    private EntityManager       manager;

    @EJB
    private SessionCacheBean    session;

    @EJB
    private SystemVariableBean  systemVariable;

    @EJB
    private PWSFacilityBean     pwsFacility;

    private String              PWS_FACILITY_FILE = "openelis_facility.txt";

    private static final String DELIMITER         = "\\|";

    private static final Logger log               = Logger.getLogger("openelis");

    /**
     * Read data from the pws facility file. Create new records in the database
     * for data that does not yet exist there, update records that are
     * different, and remove records that are not present in the file.
     */
    @RolesAllowed("pws-add")
    public void load(ReportStatus status) throws Exception {
        int i, r, onePercent;
        PWSFacilityDO p;
        ArrayList<PWSFacilityDO> records;
        HashMap<Integer, HashMap<Integer, PWSFacilityDO>> pfm;
        HashMap<Integer, PWSFacilityDO> childMap;
        ArrayList<PWSFacilityDO> deleteList;

        status.setMessage("Reading file 2 of 4: PWS facility file");
        status.setPercentComplete(25);
        session.setAttribute("PWSFileImport", status);

        /*
         * create a hash map from the file and fetch records from table
         */
        pfm = parse();
        records = pwsFacility.fetchAll();

        /*
         * calculate the number of records needed to be updated to indicate
         * one percent of progress towards completion
         */
        onePercent = (int)Math.max(records.size() / 12.5, 12);
        status.setMessage("Loading file 2 of 4: PWS facility file");
        session.setAttribute("PWSFileImport", status);

        /*
         * update the records in the database with records from file
         */
        i = r = 0;
        deleteList = new ArrayList<PWSFacilityDO>();
        for (PWSFacilityDO record : records) {
            i++;
            
            /*
             * the record is not present in the file, so it needs to be
             * deleted
             */
            childMap = pfm.get(record.getTinwsfIsNumber());
            if (childMap == null || (p = childMap.get(record.getTsasmpptIsNumber())) == null) {
                deleteList.add(record);
            } else {
                /*
                 * check if the database needs to be updated
                 */
                if ( !equals(p, record)) {
                    pwsFacility.update(p);
                    r++ ;

                    /*
                     * batch update
                     */
                    if (r % 200 == 0)
                        manager.flush();
                }
                
                childMap.remove(record.getTsasmpptIsNumber());
                if (childMap.size() == 0)
                    pfm.remove(record.getTinwsfIsNumber());
            }

            if (i % onePercent == 0) {
                status.setPercentComplete(25 + i / onePercent);
                session.setAttribute("PWSFileImport", status);
            }
        }

        /*
         * delete records from the database that are not present in the file
         */
        if (deleteList.size() > 0) {
            for (PWSFacilityDO pf : deleteList) {
                pwsFacility.delete(pf);
                r++ ;

                if (r % 200 == 0)
                    manager.flush();
            }
        }

        /*
         * add the records that were in the file but not in database
         */
        onePercent = (int)Math.max(pfm.size() / 12.5, 12);

        i = 0;
        for (HashMap<Integer, PWSFacilityDO> child : pfm.values()) {
            i++ ;
            for (PWSFacilityDO pf : child.values()) {
                pwsFacility.add(pf);
                r++ ;

                if (r % 200 == 0)
                    manager.flush();
            }

            if (i % onePercent == 0) {
                status.setPercentComplete(25 + 13 + i / onePercent);
                session.setAttribute("PWSFileImport", status);
            }
        }

        manager.flush();
    }

    /**
     * reads the file for pws_facility table and return a mapping between
     * tinwsf_is_number and tsasmppt_is_number and pws_facility field data
     * associated with them
     */
    private HashMap<Integer, HashMap<Integer, PWSFacilityDO>> parse() throws Exception {
        int i;
        String line, path, buf[];
        PWSFacilityDO data;
        Integer zero;
        BufferedReader reader;
        HashMap<Integer, HashMap<Integer, PWSFacilityDO>> pfm;
        HashMap<Integer, PWSFacilityDO> childMap;

        try {
            path = systemVariable.fetchByName("pws_path").getValue();
        } catch (Exception e) {
            log.severe("No 'pws_path' system variable defined");
            throw e;
        }

        reader = new BufferedReader(new FileReader(path + PWS_FACILITY_FILE));
        i = 1;

        /*
         * Map from the pws facility file, with the tinwsf_is_number being the
         * key, with a hashmap of all the facilities with sasmppt_is_number as key.
         */
        pfm = new HashMap<Integer, HashMap<Integer, PWSFacilityDO>>();
        zero = new Integer(0);

        try {
            /*
             * skip the first line
             */
            reader.readLine();
            while ( (line = reader.readLine()) != null) {
                i++ ;
                /*
                 * remove special characters from the string
                 */
                line = line.replaceAll("[^\\x00-\\x7F]", "");
                buf = line.split(DELIMITER, 13);

                if (buf.length < 12)
                    throw new ParseException("Too few columns");

                data = new PWSFacilityDO();
                data.setTinwsfIsNumber(ParseUtil.parseIntField(buf[0], false, "TINWSF_IS_NUMBER"));
                data.setTsasmpptIsNumber(ParseUtil.parseIntField(buf[1], true, "TSASMPPT_IS_NUMBER"));
                if (data.getTsasmpptIsNumber() == null)
                    data.setTsasmpptIsNumber(zero);
                data.setTinwsysIsNumber(ParseUtil.parseIntField(buf[2], false, "TINWSYS_IS_NUMBER"));
                data.setName(ParseUtil.parseStrField(buf[3], 40, true, "NAME"));
                data.setTypeCode(ParseUtil.parseStrField(buf[4], 2, true, "TYPE_CODE"));
                data.setStAsgnIdentCd(ParseUtil.parseStrField(buf[5], 12, true, "ST_ASGN_IDENT_CD"));
                data.setActivityStatusCd(ParseUtil.parseStrField(buf[6], 1, true, "ACTIVITY_STATUS_CD"));
                data.setWaterTypeCode(ParseUtil.parseStrField(buf[7], 3, true, "WATER_TYPE_CODE"));
                data.setAvailabilityCode(ParseUtil.parseStrField(buf[8], 1, true, "AVAILABILITY_CODE"));
                data.setIdentificationCd(ParseUtil.parseStrField(buf[9], -11, true, "IDENTIFICATION_CD"));
                data.setDescriptionText(ParseUtil.parseStrField(buf[10], -20, true, "DESCRIPTION_TEXT"));
                data.setSourceTypeCode(ParseUtil.parseStrField(buf[11], 2, true, "SOURCE_TYPE_CODE"));

                childMap = pfm.get(data.getTinwsfIsNumber());
                if (childMap == null) {
                    childMap = new HashMap<Integer, PWSFacilityDO>();
                    pfm.put(data.getTinwsfIsNumber(), childMap);
                }
                childMap.put(data.getTsasmpptIsNumber(), data);
            }
        } catch (Exception e) {
            throw new Exception(PWS_FACILITY_FILE+" has error in line " + i + ": " + e.getMessage());
        } finally {
            reader.close();
        }
        return pfm;
    }

    /**
     * compares the corresponding fields in the two DOs and returns true if they
     * are all the same, false otherwise
     */
    private boolean equals(PWSFacilityDO a, PWSFacilityDO b) {
        return !DataBaseUtil.isDifferent(a.getTinwsfIsNumber(), b.getTinwsfIsNumber()) &&
               !DataBaseUtil.isDifferent(a.getTsasmpptIsNumber(), b.getTsasmpptIsNumber()) &&
               !DataBaseUtil.isDifferent(a.getTinwsysIsNumber(), b.getTinwsysIsNumber()) &&
               !DataBaseUtil.isDifferent(a.getName(), b.getName()) &&
               !DataBaseUtil.isDifferent(a.getTypeCode(), b.getTypeCode()) &&
               !DataBaseUtil.isDifferent(a.getStAsgnIdentCd(), b.getStAsgnIdentCd()) &&
               !DataBaseUtil.isDifferent(a.getActivityStatusCd(), b.getActivityStatusCd()) &&
               !DataBaseUtil.isDifferent(a.getWaterTypeCode(), b.getWaterTypeCode()) &&
               !DataBaseUtil.isDifferent(a.getAvailabilityCode(), b.getAvailabilityCode()) &&
               !DataBaseUtil.isDifferent(a.getIdentificationCd(), b.getIdentificationCd()) &&
               !DataBaseUtil.isDifferent(a.getDescriptionText(), b.getDescriptionText()) &&
               !DataBaseUtil.isDifferent(a.getSourceTypeCode(), b.getSourceTypeCode());
    }
}