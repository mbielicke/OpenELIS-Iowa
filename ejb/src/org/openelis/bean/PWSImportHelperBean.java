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
import org.openelis.domain.PWSDO;
import org.openelis.exception.ParseException;
import org.openelis.ui.common.DataBaseUtil;
import org.openelis.ui.common.ReportStatus;
import org.openelis.utils.ParseUtil;

/**
 * Reads data from a PWS file and updates the pws table in the database. This
 * bean manages its own transactions in order to reduce memory usage.
 */
@Stateless
@SecurityDomain("openelis")
public class PWSImportHelperBean {

    @PersistenceContext(unitName = "openelis")
    private EntityManager       manager;

    @EJB
    private SessionCacheBean    session;

    @EJB
    private SystemVariableBean  systemVariable;

    @EJB
    private PWSBean             pws;

    private static final String PWS_FILE  = "openelis_pws.txt";

    private static final String DELIMITER = "\\|";

    private static final Logger log       = Logger.getLogger("openelis");

    /**
     * Read data from the pws file. Create new records in the database for data
     * that does not yet exist there, update records that are different, and
     * remove records that are not present in the file.
     */
    @RolesAllowed("pws-add")
    public void load(ReportStatus status) throws Exception {
        int i, r, onePercent;
        PWSDO p;
        ArrayList<PWSDO> records;
        HashMap<Integer, PWSDO> pm;

        status.setMessage("Reading file 1 of 4: PWS file");
        session.setAttribute("PWSFileImport", status);

        /*
         * create a hash map from the file and fetch records from table
         */
        pm = parse();
        records = pws.fetchAll();

        /*
         * calculate the number of records needed to be updated to indicate
         * one percent of progress towards completion
         */
        onePercent = (int)Math.max(records.size() / 12.5, 12);
        status.setMessage("Loading file 1 of 4: PWS file");
        session.setAttribute("PWSFileImport", status);

        /*
         * update the records in the database with records from file
         */
        i = r = 0;
        for (PWSDO record : records) {
            i++ ;

            p = pm.get(record.getTinwsysIsNumber());
            if (p != null) {
                /*
                 * check if the database needs to be updated
                 */
                if ( !equals(p, record)) {
                    p.setId(record.getId());
                    pws.update(p);
                    r++ ;

                    /*
                     * batch update
                     */
                    if (r % 200 == 0)
                        manager.flush();
                }

                pm.remove(p.getTinwsysIsNumber());
            }
            
            if (i % onePercent == 0) {
                status.setPercentComplete(i / onePercent);
                session.setAttribute("PWSFileImport", status);
            }
        }

        /*
         * add the records that were in the file but not in database
         */
        onePercent = (int)Math.max(pm.size() / 12.5, 12);

        i = 0;
        for (PWSDO tp : pm.values()) {
            i++ ;

            pws.add(tp);
            r++;

            if (r % 200 == 0)
                manager.flush();

            if (i % onePercent == 0) {
                status.setPercentComplete(13 + i / onePercent);
                session.setAttribute("PWSFileImport", status);
            }
        }

        manager.flush();
    }

    /**
     * reads the file for pws table and return a mapping between
     * tinwsys_is_number and pws field data associated with it
     */
    private HashMap<Integer, PWSDO> parse() throws Exception {
        int i;
        String line, path, buf[];
        PWSDO data;
        BufferedReader reader;
        HashMap<Integer, PWSDO> pm;

        try {
            path = systemVariable.fetchByName("pws_path").getValue();
        } catch (Exception e) {
            log.severe("No 'pws_path' system variable defined");
            throw e;
        }

        reader = new BufferedReader(new FileReader(path + PWS_FILE));
        i = 1;

        /*
         * map from the pws file, with the tinwsys_is_number being the key, and
         * the data for pws fields being the value
         */
        pm = new HashMap<Integer, PWSDO>();

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
                buf = line.split(DELIMITER, 17);

                if (buf.length < 16)
                    throw new ParseException("Too few columns");

                data = new PWSDO();
                data.setTinwsysIsNumber(ParseUtil.parseIntField(buf[0], false, "TINWSYS_IS_NUMBER"));
                data.setNumber0(ParseUtil.parseStrField(buf[1], 12, false, "NUMBER0"));
                data.setAlternateStNum(ParseUtil.parseStrField(buf[2], 5, true, "ALTERNATE_ST_NUM"));
                data.setName(ParseUtil.parseStrField(buf[3], 40, true, "NAME"));
                data.setActivityStatusCd(ParseUtil.parseStrField(buf[4], 1, true, "ACTIVITY_STATUS_CD"));
                data.setDPrinCitySvdNm(ParseUtil.parseStrField(buf[5], 40, true, "D_PRIN_CITY_SVD_NM"));
                data.setDPrinCntySvdNm(ParseUtil.parseStrField(buf[6], 40, true, "D_PRIN_CITY_SVD_NM"));
                data.setDPopulationCount(ParseUtil.parseIntField(buf[7], false, "D_POPULATION_COUNT"));
                data.setDPwsStTypeCd(ParseUtil.parseStrField(buf[8], 4, true, "PWS_ST_TYPE_CD"));
                data.setActivityRsnTxt(ParseUtil.parseStrField(buf[9], -255, true, "ACTIVITY_RSN_TXT"));
                data.setStartDay(ParseUtil.parseIntField(buf[10], false, "START_DAY"));
                data.setStartMonth(ParseUtil.parseIntField(buf[11], false, "START_MONTH"));
                data.setEndDay(ParseUtil.parseIntField(buf[12], false, "END_DAY"));
                data.setEndMonth(ParseUtil.parseIntField(buf[13], false, "END_MONTH"));
                data.setEffBeginDt(ParseUtil.parseDateField(buf[14], false, "EFF_BEGIN_DT"));
                data.setEffEndDt(ParseUtil.parseDateField(buf[15], true, "EFF_END_DT"));

                pm.put(data.getTinwsysIsNumber(), data);
            }
        } catch (Exception e) {
            throw new Exception(PWS_FILE + " has error in line " + i + ": " + e.getMessage());
        } finally {
            reader.close();
        }

        return pm;
    }

    /**
     * compares the corresponding fields in the two DOs and returns true if they
     * are all the same, false otherwise
     */
    private boolean equals(PWSDO a, PWSDO b) {
        return !DataBaseUtil.isDifferent(a.getTinwsysIsNumber(), b.getTinwsysIsNumber()) &&
               !DataBaseUtil.isDifferent(a.getNumber0(), b.getNumber0()) &&
               !DataBaseUtil.isDifferent(a.getAlternateStNum(), b.getAlternateStNum()) &&
               !DataBaseUtil.isDifferent(a.getName(), b.getName()) &&
               !DataBaseUtil.isDifferent(a.getActivityStatusCd(), b.getActivityStatusCd()) &&
               !DataBaseUtil.isDifferent(a.getDPrinCitySvdNm(), b.getDPrinCitySvdNm()) &&
               !DataBaseUtil.isDifferent(a.getDPrinCntySvdNm(), b.getDPrinCntySvdNm()) &&
               !DataBaseUtil.isDifferent(a.getDPopulationCount(), b.getDPopulationCount()) &&
               !DataBaseUtil.isDifferent(a.getDPwsStTypeCd(), b.getDPwsStTypeCd()) &&
               !DataBaseUtil.isDifferent(a.getActivityRsnTxt(), b.getActivityRsnTxt()) &&
               !DataBaseUtil.isDifferent(a.getStartDay(), b.getStartDay()) &&
               !DataBaseUtil.isDifferent(a.getStartMonth(), b.getStartMonth()) &&
               !DataBaseUtil.isDifferent(a.getEndDay(), b.getEndDay()) &&
               !DataBaseUtil.isDifferent(a.getEndMonth(), b.getEndMonth()) &&
               !DataBaseUtil.isDifferentDT(a.getEffBeginDt(), b.getEffBeginDt()) &&
               !DataBaseUtil.isDifferentDT(a.getEffEndDt(), b.getEffEndDt());
    }
}