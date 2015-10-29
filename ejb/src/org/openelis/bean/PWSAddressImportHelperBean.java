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
import org.openelis.domain.PWSAddressDO;
import org.openelis.exception.ParseException;
import org.openelis.gwt.common.DataBaseUtil;
import org.openelis.ui.common.ReportStatus;
import org.openelis.utils.ParseUtil;

/**
 * Reads data from a PWS Address file and updates the pws_address table in the
 * database. This bean manages its own transactions in order to reduce memory
 * usage.
 */
@Stateless
@SecurityDomain("openelis")

public class PWSAddressImportHelperBean {

    @PersistenceContext(unitName = "openelis")
    private EntityManager       manager;

    @EJB
    private SessionCacheBean    session;

    @EJB
    private SystemVariableBean  systemVariable;

    @EJB
    private PWSAddressBean      pwsAddress;

    private String              PWS_ADDRESS_FILE = "openelis_address.txt";

    private static final String DELIMITER        = "\\|";

    private static final Logger log              = Logger.getLogger("openelis");

    /**
     * Read data from the pws address file. Create new records in the database
     * for data that does not yet exist there, update records that are
     * different, and remove records that are not present in the file.
     */
    @RolesAllowed("pws-add")
    public void load(ReportStatus status) throws Exception {
        int i, r, onePercent;
        PWSAddressDO p;
        ArrayList<PWSAddressDO> records;
        HashMap<Integer, HashMap<Integer, PWSAddressDO>> pam;
        HashMap<Integer, PWSAddressDO> childMap;
        ArrayList<PWSAddressDO> deleteList;

        status.setMessage("Reading file 3 of 4: PWS address file");
        status.setPercentComplete(50);
        session.setAttribute("PWSFileImport", status);

        /*
         * create a hash map from the file and fetch records from table
         */
        pam = parse();
        records = pwsAddress.fetchAll();

        /*
         * calculate the number of records needed to be updated to indicate
         * one percent of progress towards completion
         */
        onePercent = (int)Math.max(records.size() / 12.5, 12);
        status.setMessage("Loading file 3 of 4: PWS address file");
        session.setAttribute("PWSFileImport", status);

        /*
         * update the records in the database with records from file
         */
        i = r = 0;
        deleteList = new ArrayList<PWSAddressDO>();
        for (PWSAddressDO record : records) {
            i++;

            /*
             * the record is not present in the file, so it needs to be
             * deleted
             */
            childMap = pam.get(record.getTinwslecIsNumber());
            if (childMap == null || (p = childMap.get(record.getTinlgentIsNumber())) == null) {
                deleteList.add(record);
            } else {
                /*
                 * check if the database needs to be updated
                 */
                if ( !equals(p, record)) {
                    pwsAddress.update(p);
                    r++ ;

                    /*
                     * batch update
                     */
                    if (r % 200 == 0)
                        manager.flush();
                }

                childMap.remove(record.getTinlgentIsNumber());
                if (childMap.size() == 0)
                    pam.remove(record.getTinwslecIsNumber());
            }

            if (i % onePercent == 0) {
                status.setPercentComplete(50 + i / onePercent);
                session.setAttribute("PWSFileImport", status);
            }
        }

        /*
         * delete records from the database that are not present in the file
         */
        if (deleteList.size() > 0) {
            for (PWSAddressDO address : deleteList) {
                pwsAddress.delete(address);
                r++ ;
            }

            if (r % 200 == 0)
                manager.flush();
        }

        /*
         * add the records that were in the file but not in database
         */
        onePercent = (int)Math.max(pam.size() / 12.5, 12);

        i = 0;
        for (HashMap<Integer, PWSAddressDO> child : pam.values()) {
            i++ ;
            for (PWSAddressDO pa : child.values()) {
                pwsAddress.add(pa);
                r++ ;

                if (r % 200 == 0)
                    manager.flush();
            }

            if (i % onePercent == 0) {
                status.setPercentComplete(50 + 13 + i / onePercent);
                session.setAttribute("PWSFileImport", status);
            }
        }
        
        manager.flush();
    }

    /**
     * reads the file for pws_address table and return a mapping between
     * tinwslec_is_number and tinlgent_is_number and pws_address field data
     * associated with them
     */
    private HashMap<Integer, HashMap<Integer, PWSAddressDO>> parse() throws Exception {
        int i;
        String line, path, buf[];
        PWSAddressDO data;
        Integer zero;
        BufferedReader reader;
        HashMap<Integer, HashMap<Integer, PWSAddressDO>> pam;
        HashMap<Integer, PWSAddressDO> childMap;

        try {
            path = systemVariable.fetchByName("pws_path").getValue();
        } catch (Exception e) {
            log.severe("No 'pws_path' system variable defined");
            throw e;
        }

        reader = new BufferedReader(new FileReader(path + PWS_ADDRESS_FILE));
        i = 1;

        /*
         * A map from the pws address file, with the tinwslec_is_number being
         * the key, with a hashmap of all the addresses with tinlgent_is_number
         * as key.
         */
        pam = new HashMap<Integer, HashMap<Integer, PWSAddressDO>>();
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
                buf = line.split(DELIMITER, 14);

                if (buf.length < 13)
                    throw new ParseException("Too few columns");

                data = new PWSAddressDO();
                data.setTinwslecIsNumber(ParseUtil.parseIntField(buf[0], false, "TINWSLEC_IS_NUMBER"));
                data.setTinlgentIsNumber(ParseUtil.parseIntField(buf[1], true, "TINLGENT_IS_NUMBER"));
                if (data.getTinlgentIsNumber() == null)
                    data.setTinlgentIsNumber(zero);
                data.setTinwsysIsNumber(ParseUtil.parseIntField(buf[2], false, "TINWSYS_IS_NUMBER"));
                data.setTypeCode(ParseUtil.parseStrField(buf[3], 3, true, "TYPE_CODE"));
                data.setActiveIndCd(ParseUtil.parseStrField(buf[4], 1, true, "ACTIVE_IND_CD"));
                data.setName(ParseUtil.parseStrField(buf[5], 40, true, "NAME"));
                data.setAddrLineOneTxt(ParseUtil.parseStrField(buf[6], 40, true, "ADDR_LINE_ONE_TXT"));
                data.setAddrLineTwoTxt(ParseUtil.parseStrField(buf[7], 40, true, "ADDR_LINE_TWO_TXT"));
                data.setAddressCityName(ParseUtil.parseStrField(buf[8], 40, true, "ADDRESS_CITY_NAME"));
                data.setAddressStateCode(ParseUtil.parseStrField(buf[9], 2, true, "ADDRESS_STATE_CODE"));
                data.setAddressZipCode(ParseUtil.parseStrField(buf[10], 10, true, "ADDRESS_ZIP_CODE"));
                data.setStateFipsCode(ParseUtil.parseStrField(buf[11], 2, true, "STATE_FIPS_CODE"));
                data.setPhoneNumber(ParseUtil.parseStrField(buf[12], 12, true, "PHONE_NUMBER"));

                childMap = pam.get(data.getTinwslecIsNumber());
                if (childMap == null) {
                    childMap = new HashMap<Integer, PWSAddressDO>();
                    pam.put(data.getTinwslecIsNumber(), childMap);
                }
                childMap.put(data.getTinlgentIsNumber(), data);
            }
        } catch (Exception e) {
            throw new Exception(PWS_ADDRESS_FILE+" has error in line " + i + ": " + e.getMessage());
        } finally {
            reader.close();
        }
        return pam;
    }

    /**
     * compares the corresponding fields in the two DOs and returns true if they
     * are all the same, false otherwise
     */
    private boolean equals(PWSAddressDO a, PWSAddressDO b) {
        return !DataBaseUtil.isDifferent(a.getTinwsysIsNumber(), b.getTinwsysIsNumber()) &&
               !DataBaseUtil.isDifferent(a.getTypeCode(), b.getTypeCode()) &&
               !DataBaseUtil.isDifferent(a.getActiveIndCd(), b.getActiveIndCd()) &&
               !DataBaseUtil.isDifferent(a.getName(), b.getName()) &&
               !DataBaseUtil.isDifferent(a.getAddrLineOneTxt(), b.getAddrLineOneTxt()) &&
               !DataBaseUtil.isDifferent(a.getAddrLineTwoTxt(), b.getAddrLineTwoTxt()) &&
               !DataBaseUtil.isDifferent(a.getAddressCityName(), b.getAddressCityName()) &&
               !DataBaseUtil.isDifferent(a.getAddressStateCode(), b.getAddressStateCode()) &&
               !DataBaseUtil.isDifferent(a.getAddressZipCode(), b.getAddressZipCode()) &&
               !DataBaseUtil.isDifferent(a.getStateFipsCode(), b.getStateFipsCode()) &&
               !DataBaseUtil.isDifferent(a.getPhoneNumber(), b.getPhoneNumber());
    }
}