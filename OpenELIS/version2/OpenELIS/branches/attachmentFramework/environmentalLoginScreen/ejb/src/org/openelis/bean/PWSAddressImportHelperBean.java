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
import org.openelis.domain.PWSAddressDO;
import org.openelis.exception.ParseException;
import org.openelis.gwt.common.DataBaseUtil;
import org.openelis.ui.common.ReportStatus;

/**
 * Reads data from a PWS Address file and updates the pws_address table in the
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
public class PWSAddressImportHelperBean {

    @EJB
    private SessionCacheBean    session;

    @EJB
    private SystemVariableBean  systemVariable;

    @Resource
    private SessionContext      ctx;

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
    public void load(ReportStatus status) throws Exception {
        boolean toCommit;
        int i, count, onePercent;
        Integer tinwslecIsNumber, tinlgentIsNumber;
        UserTransaction ut;
        PWSAddressDO pa;
        ArrayList<PWSAddressDO> records;
        HashMap<Integer, HashMap<Integer, PWSAddressDO>> pam;
        HashMap<Integer, PWSAddressDO> childMap;
        ArrayList<PWSAddressDO> deleteList;

        ut = null;
        try {
            deleteList = new ArrayList<PWSAddressDO>();
            status.setMessage("Reading file 3 of 4: PWS address file");
            status.setPercentComplete(50);
            session.setAttribute("PWSFileImport", status);
            /*
             * create a hash map from the file
             */
            pam = parse();
            records = pwsAddress.fetchAll();
            /*
             * calculate the number of records needed to be updated to indicate
             * one percent of progress towards completion
             */
            onePercent = (int) (records.size() / 12.5);
            if (onePercent == 0)
                onePercent = 12;
            i = 0;
            ut = ctx.getUserTransaction();
            ut.begin();
            toCommit = false;
            for (PWSAddressDO record : records) {
                tinwslecIsNumber = record.getTinwslecIsNumber();
                tinlgentIsNumber = record.getTinlgentIsNumber();
                childMap = pam.get(tinwslecIsNumber);
                /*
                 * the record is not present in the file, so it needs to be
                 * deleted
                 */
                if (childMap == null) {
                    deleteList.add(record);
                    continue;
                }
                pa = childMap.get(tinlgentIsNumber);
                if (pa == null) {
                    deleteList.add(record);
                    continue;
                }
                /*
                 * check if the database needs to be updated
                 */
                if ( !equals(pa, record)) {
                    i++ ;
                    pwsAddress.update(pa);
                    toCommit = true;
                    if (i % 1000 == 0) {
                        ut.commit();
                        ut.begin();
                        toCommit = false;
                    }
                    if (i % onePercent == 0) {
                        status.setMessage("Loading file 3 of 4: PWS address file");
                        status.setPercentComplete(50 + i / onePercent);
                        session.setAttribute("PWSFileImport", status);
                    }
                }
                /*
                 * this map must only have records that don't exist in the
                 * database
                 */
                childMap.remove(tinlgentIsNumber);
                if (childMap.size() == 0)
                    pam.remove(tinwslecIsNumber);
            }
            /*
             * delete records from the database that are not present in the file
             */
            if (deleteList.size() > 0) {
                for (PWSAddressDO address : deleteList) {
                    pwsAddress.delete(address);
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
            onePercent = (int) (pam.size() / 12.5);
            if (onePercent == 0)
                onePercent = 12;
            for (HashMap<Integer, PWSAddressDO> pwsam : pam.values()) {
                count++ ;
                for (PWSAddressDO pwsa : pwsam.values()) {
                    i++ ;
                    pwsAddress.add(pwsa);
                    toCommit = true;
                    if (i % 1000 == 0) {
                        ut.commit();
                        ut.begin();
                        toCommit = false;
                    }
                    if (count % onePercent == 0) {
                        status.setMessage("Loading file 3 of 4: PWS address file");
                        status.setPercentComplete(50 + 13 + count / onePercent);
                        session.setAttribute("PWSFileImport", status);
                    }
                }
            }
            if (toCommit)
                ut.commit();
            else
                ut.rollback();
        } catch (Exception e) {
            if (ut != null)
                ut.rollback();
            throw e;
        }
    }

    /**
     * reads the file for pws_address table and return a mapping between
     * tinwslec_is_number and tinlgent_is_number and pws_address field data
     * associated with them
     */
    private HashMap<Integer, HashMap<Integer, PWSAddressDO>> parse() throws Exception {
        int i;
        Integer tinwslecIsNumber, tinlgentIsNumber, zero;
        String line, path, buf[];
        PWSAddressDO data;
        BufferedReader reader;
        HashMap<Integer, HashMap<Integer, PWSAddressDO>> pwsAddressMap;
        HashMap<Integer, PWSAddressDO> childMap;

        try {
            path = systemVariable.fetchByName("pws_path").getValue();
        } catch (Exception e) {
            log.severe("No 'pws_path' system variable defined");
            throw e;
        }
        reader = new BufferedReader(new FileReader(path + PWS_ADDRESS_FILE));
        i = 1;
        zero = new Integer(0);
        /*
         * A map from the pws address file, with the tinwslec_is_number being
         * the key, and a hashmap with all records with that tinwslec_is_number
         * being the value. The key of the inner map is the tinlgent_is_number
         * of the facility.
         */
        pwsAddressMap = new HashMap<Integer, HashMap<Integer, PWSAddressDO>>();

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
                data.setTinwslecIsNumber(Integer.parseInt(buf[0]));
                if ( !DataBaseUtil.isEmpty(buf[1]))
                    data.setTinlgentIsNumber(Integer.parseInt(buf[1]));
                else
                    data.setTinlgentIsNumber(zero);
                data.setTinwsysIsNumber(Integer.parseInt(buf[2]));
                data.setTypeCode(buf[3]);
                data.setActiveIndCd(buf[4]);
                data.setName(buf[5]);
                data.setAddrLineOneTxt(buf[6]);
                data.setAddrLineTwoTxt(buf[7]);
                data.setAddressCityName(buf[8]);
                data.setAddressStateCode(buf[9]);
                data.setAddressZipCode(buf[10]);
                data.setStateFipsCode(buf[11]);
                data.setPhoneNumber(buf[12]);

                tinwslecIsNumber = data.getTinwslecIsNumber();
                tinlgentIsNumber = data.getTinlgentIsNumber();
                if (pwsAddressMap.get(tinwslecIsNumber) == null)
                    childMap = new HashMap<Integer, PWSAddressDO>();
                else
                    childMap = pwsAddressMap.get(tinwslecIsNumber);
                childMap.put(tinlgentIsNumber, data);
                pwsAddressMap.put(tinwslecIsNumber, childMap);
            }
        } catch (Exception e) {
            throw new Exception("Data file for pws_address has error at line " + i + ": " +
                                e.getMessage());
        } finally {
            reader.close();
        }
        return pwsAddressMap;
    }

    /**
     * compares the corresponding fields in the two DOs and returns true if they
     * are all the same, false otherwise
     */
    private boolean equals(PWSAddressDO pwsAddress, PWSAddressDO pwsAddress2) {
        return !DataBaseUtil.isDifferent(pwsAddress.getTinwsysIsNumber(),
                                         pwsAddress2.getTinwsysIsNumber()) &&
               !DataBaseUtil.isDifferent(pwsAddress.getTypeCode(), pwsAddress2.getTypeCode()) &&
               !DataBaseUtil.isDifferent(pwsAddress.getActiveIndCd(), pwsAddress2.getActiveIndCd()) &&
               !DataBaseUtil.isDifferent(pwsAddress.getName(), pwsAddress2.getName()) &&
               !DataBaseUtil.isDifferent(pwsAddress.getAddrLineOneTxt(),
                                         pwsAddress2.getAddrLineOneTxt()) &&
               !DataBaseUtil.isDifferent(pwsAddress.getAddrLineTwoTxt(),
                                         pwsAddress2.getAddrLineTwoTxt()) &&
               !DataBaseUtil.isDifferent(pwsAddress.getAddressCityName(),
                                         pwsAddress2.getAddressCityName()) &&
               !DataBaseUtil.isDifferent(pwsAddress.getAddressStateCode(),
                                         pwsAddress2.getAddressStateCode()) &&
               !DataBaseUtil.isDifferent(pwsAddress.getAddressZipCode(),
                                         pwsAddress2.getAddressZipCode()) &&
               !DataBaseUtil.isDifferent(pwsAddress.getStateFipsCode(),
                                         pwsAddress2.getStateFipsCode()) &&
               !DataBaseUtil.isDifferent(pwsAddress.getPhoneNumber(), pwsAddress2.getPhoneNumber());
    }
}