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

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.sql.DataSource;
import javax.transaction.UserTransaction;

import org.jboss.security.annotation.SecurityDomain;
import org.openelis.domain.PWSFacilityDO;
import org.openelis.exception.ParseException;
import org.openelis.gwt.common.DataBaseUtil;
import org.openelis.ui.common.ReportStatus;

/**
 * Reads data from a PWS Facility file and updates the pws_facility table in the
 * database. This bean manages its own transactions in order to reduce memory
 * usage.
 */
@Stateless
@SecurityDomain("openelis")
@TransactionManagement(TransactionManagementType.BEAN)
@Resource(name = "jdbc/OpenELISDB",
          type = DataSource.class,
          authenticationType = javax.annotation.Resource.AuthenticationType.CONTAINER,
          mappedName = "java:/OpenELISDS")
public class PWSFacilityImportHelperBean {

    @EJB
    private SessionCacheBean    session;

    @EJB
    private SystemVariableBean  systemVariable;

    @Resource
    private SessionContext      ctx;

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
    public void load(ReportStatus status) throws Exception {
        boolean toCommit;
        int i, count, onePercent;
        Integer tinwsfIsNumber, tsasmpptIsNumber;
        UserTransaction ut;
        PWSFacilityDO pf;
        ArrayList<PWSFacilityDO> records;
        HashMap<Integer, HashMap<Integer, PWSFacilityDO>> pfm;
        HashMap<Integer, PWSFacilityDO> childMap;
        ArrayList<PWSFacilityDO> deleteList;

        ut = ctx.getUserTransaction();
        try {
            deleteList = new ArrayList<PWSFacilityDO>();
            status.setMessage("Reading file 2 of 4: PWS facility file");
            status.setPercentComplete(25);
            session.setAttribute("PWSFileImport", status);
            /*
             * create a hash map from the file
             */
            pfm = parse();
            records = pwsFacility.fetchAll();
            /*
             * calculate the number of records needed to be updated to indicate
             * one percent of progress towards completion
             */
            onePercent = (int) (records.size() / 12.5);
            if (onePercent == 0)
                onePercent = 12;
            i = 0;
            ut.begin();
            toCommit = false;
            for (PWSFacilityDO record : records) {
                tinwsfIsNumber = record.getTinwsfIsNumber();
                tsasmpptIsNumber = record.getTsasmpptIsNumber();
                childMap = pfm.get(tinwsfIsNumber);
                /*
                 * the record is not present in the file, so it needs to be
                 * deleted
                 */
                if (childMap == null) {
                    deleteList.add(record);
                    continue;
                }
                pf = childMap.get(tsasmpptIsNumber);
                if (pf == null) {
                    deleteList.add(record);
                    continue;
                }
                /*
                 * check if the database needs to be updated
                 */
                if ( !equals(pf, record)) {
                    i++ ;
                    pwsFacility.update(pf);
                    toCommit = true;
                    if (i % 1000 == 0) {
                        ut.commit();
                        ut.begin();
                        toCommit = false;
                    }
                    if (i % onePercent == 0) {
                        status.setMessage("Loading file 2 of 4: PWS facility file");
                        status.setPercentComplete(25 + i / onePercent);
                        session.setAttribute("PWSFileImport", status);
                    }
                }
                /*
                 * this map must only have records that don't exist in the
                 * database
                 */
                childMap.remove(tsasmpptIsNumber);
                if (childMap.size() < 1)
                    pfm.remove(tinwsfIsNumber);
            }
            /*
             * delete records from the database that are not present in the file
             */
            if (deleteList.size() > 0) {
                for (PWSFacilityDO facility : deleteList) {
                    pwsFacility.delete(facility);
                }
                ut.commit();
                ut.begin();
            }
            i = 0;
            count = 0;
            /*
             * calculate the number of records needed to be added to indicate
             * one percent of progress towards completion
             */
            onePercent = (int) (pfm.size() / 12.5);
            if (onePercent == 0)
                onePercent = 12;
            for (HashMap<Integer, PWSFacilityDO> pwsfm : pfm.values()) {
                count++ ;
                for (PWSFacilityDO pwsf : pwsfm.values()) {
                    i++ ;
                    pwsFacility.add(pwsf);
                    toCommit = true;
                    if (i % 1000 == 0) {
                        ut.commit();
                        ut.begin();
                        toCommit = false;
                    }
                    if (count % onePercent == 0) {
                        status.setMessage("Loading file 2 of 4: PWS facility file");
                        status.setPercentComplete(25 + 13 + count / onePercent);
                        session.setAttribute("PWSFileImport", status);
                    }
                }
            }
            if (toCommit)
                ut.commit();
            else
                ut.rollback();
        } catch (Exception e) {
            ut.rollback();
            throw e;
        }
    }

    /**
     * reads the file for pws_facility table and return a mapping between
     * tinwsf_is_number and tsasmppt_is_number and pws_facility field data
     * associated with them
     */
    private HashMap<Integer, HashMap<Integer, PWSFacilityDO>> parse() throws Exception {
        int i;
        Integer tinwsfIsNumber, tinsasmpptIsNumber, zero;
        String line, path, buf[];
        PWSFacilityDO data;
        BufferedReader reader;
        HashMap<Integer, HashMap<Integer, PWSFacilityDO>> pwsFacilityMap;
        HashMap<Integer, PWSFacilityDO> childMap;

        try {
            path = systemVariable.fetchByName("pws_path").getValue();
        } catch (Exception e) {
            log.severe("No 'pws_path' system variable defined");
            throw e;
        }
        reader = new BufferedReader(new FileReader(path + PWS_FACILITY_FILE));
        i = 1;
        zero = new Integer(0);
        /*
         * Map from the pws facility file, with the tinwsf_is_number being the
         * key, and a hashmap with all records with that tinwsf_is_number being
         * the value. The key of the inner map is the tsasmppt_is_number of the
         * facility.
         */
        pwsFacilityMap = new HashMap<Integer, HashMap<Integer, PWSFacilityDO>>();

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
                data.setTinwsfIsNumber(Integer.parseInt(buf[0]));
                if ( !DataBaseUtil.isEmpty(buf[1]))
                    data.setTsasmpptIsNumber(Integer.parseInt(buf[1]));
                else
                    data.setTsasmpptIsNumber(zero);
                data.setTinwsysIsNumber(Integer.parseInt(buf[2]));
                data.setName(buf[3]);
                data.setTypeCode(buf[4]);
                data.setStAsgnIdentCd(buf[5]);
                data.setActivityStatusCd(buf[6]);
                data.setWaterTypeCode(buf[7]);
                data.setAvailabilityCode(buf[8]);
                data.setIdentificationCd(buf[9]);
                if (buf[10].length() > 20)
                    data.setDescriptionText(buf[10].substring(0, 20));
                else
                    data.setDescriptionText(buf[10]);
                data.setSourceTypeCode(buf[11]);

                tinwsfIsNumber = data.getTinwsfIsNumber();
                tinsasmpptIsNumber = data.getTsasmpptIsNumber();
                if (pwsFacilityMap.get(tinwsfIsNumber) == null)
                    childMap = new HashMap<Integer, PWSFacilityDO>();
                else
                    childMap = pwsFacilityMap.get(tinwsfIsNumber);
                childMap.put(tinsasmpptIsNumber, data);
                pwsFacilityMap.put(tinwsfIsNumber, childMap);
            }
        } catch (Exception e) {
            throw new Exception("Data file for pws_facility has error at line " + i + ": " +
                                e.getMessage());
        } finally {
            reader.close();
        }
        return pwsFacilityMap;
    }

    /**
     * compares the corresponding fields in the two DOs and returns true if they
     * are all the same, false otherwise
     */
    private boolean equals(PWSFacilityDO pwsFacility, PWSFacilityDO pwsFacility2) {
        return !DataBaseUtil.isDifferent(pwsFacility.getTinwsfIsNumber(),
                                         pwsFacility2.getTinwsfIsNumber()) &&
               !DataBaseUtil.isDifferent(pwsFacility.getTsasmpptIsNumber(),
                                         pwsFacility2.getTsasmpptIsNumber()) &&
               !DataBaseUtil.isDifferent(pwsFacility.getTinwsysIsNumber(),
                                         pwsFacility2.getTinwsysIsNumber()) &&
               !DataBaseUtil.isDifferent(pwsFacility.getName(), pwsFacility2.getName()) &&
               !DataBaseUtil.isDifferent(pwsFacility.getTypeCode(), pwsFacility2.getTypeCode()) &&
               !DataBaseUtil.isDifferent(pwsFacility.getStAsgnIdentCd(),
                                         pwsFacility2.getStAsgnIdentCd()) &&
               !DataBaseUtil.isDifferent(pwsFacility.getActivityStatusCd(),
                                         pwsFacility2.getActivityStatusCd()) &&
               !DataBaseUtil.isDifferent(pwsFacility.getWaterTypeCode(),
                                         pwsFacility2.getWaterTypeCode()) &&
               !DataBaseUtil.isDifferent(pwsFacility.getAvailabilityCode(),
                                         pwsFacility2.getAvailabilityCode()) &&
               !DataBaseUtil.isDifferent(pwsFacility.getIdentificationCd(),
                                         pwsFacility2.getIdentificationCd()) &&
               !DataBaseUtil.isDifferent(pwsFacility.getDescriptionText(),
                                         pwsFacility2.getDescriptionText()) &&
               !DataBaseUtil.isDifferent(pwsFacility.getSourceTypeCode(),
                                         pwsFacility2.getSourceTypeCode());
    }
}