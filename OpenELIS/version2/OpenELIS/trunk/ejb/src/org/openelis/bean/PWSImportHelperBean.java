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
import java.util.Date;
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
import org.openelis.domain.PWSDO;
import org.openelis.exception.ParseException;
import org.openelis.ui.common.DataBaseUtil;
import org.openelis.ui.common.Datetime;
import org.openelis.ui.common.ReportStatus;

/**
 * Reads data from a PWS file and updates the pws table in the database. This
 * bean manages its own transactions in order to reduce memory usage.
 */
@Stateless
@SecurityDomain("openelis")
@TransactionManagement(TransactionManagementType.BEAN)
@Resource(name = "jdbc/OpenELISDB",
          type = DataSource.class,
          authenticationType = javax.annotation.Resource.AuthenticationType.CONTAINER,
          mappedName = "java:/OpenELISDS")
public class PWSImportHelperBean {

    @EJB
    private SessionCacheBean    session;

    @EJB
    private SystemVariableBean  systemVariable;

    @Resource
    private SessionContext      ctx;

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
    public void load(ReportStatus status) throws Exception {
        boolean toCommit;
        int i, onePercent;
        PWSDO p;
        UserTransaction ut;
        ArrayList<PWSDO> records;
        HashMap<Integer, PWSDO> pwsm;

        ut = ctx.getUserTransaction();

        try {
            status.setMessage("Reading file 1 of 4: PWS file");
            session.setAttribute("PWSFileImport", status);
            /*
             * create a hash map from the file
             */
            pwsm = parse();
            records = pws.fetchAll();
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
            for (PWSDO record : records) {
                p = pwsm.get(record.getTinwsysIsNumber());
                if (p == null)
                    continue;
                /*
                 * check if the database needs to be updated
                 */
                if ( !equals(p, record)) {
                    i++ ;
                    p.setId(record.getId());
                    pws.update(p);
                    toCommit = true;
                    if (i % 1000 == 0) {
                        ut.commit();
                        ut.begin();
                        toCommit = false;
                    }
                    if (i % onePercent == 0) {
                        status.setMessage("Loading file 1 of 4: PWS file");
                        status.setPercentComplete(i / onePercent);
                        session.setAttribute("PWSFileImport", status);
                    }
                }
                /*
                 * this map must only have records that don't exist in the
                 * database
                 */
                pwsm.remove(p.getTinwsysIsNumber());
            }
            i = 0;
            /*
             * calculate the number of records needed to be added to indicate
             * one percent of progress towards completion
             */
            onePercent = (int) (pwsm.size() / 12.5);
            if (onePercent == 0)
                onePercent = 12;
            for (PWSDO toAdd : pwsm.values()) {
                i++ ;
                pws.add(toAdd);
                toCommit = true;
                if (i % 1000 == 0) {
                    ut.commit();
                    ut.begin();
                    toCommit = false;
                }
                if (i % onePercent == 0) {
                    status.setMessage("Loading file 1 of 4: PWS file");
                    status.setPercentComplete(13 + i / onePercent);
                    session.setAttribute("PWSFileImport", status);
                }
            }
            if (toCommit)
                ut.commit();
            else
                ut.rollback();
        } catch (Throwable e) {
            ut.rollback();
            throw e;
        }
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
        HashMap<Integer, PWSDO> pwsMap;

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
        pwsMap = new HashMap<Integer, PWSDO>();

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
                data.setTinwsysIsNumber(Integer.parseInt(buf[0]));
                data.setNumber0(buf[1]);
                data.setAlternateStNum(buf[2]);
                data.setName(buf[3]);
                data.setActivityStatusCd(buf[4]);
                data.setDPrinCitySvdNm(buf[5]);
                data.setDPrinCntySvdNm(buf[6]);
                data.setDPopulationCount(Integer.parseInt(buf[7]));
                data.setDPwsStTypeCd(buf[8]);
                if (buf[9].length() > 255)
                    data.setActivityRsnTxt(buf[9].substring(0, 255));
                else
                    data.setActivityRsnTxt(buf[9]);
                data.setStartDay(Integer.parseInt(buf[10]));
                data.setStartMonth(Integer.parseInt(buf[11]));
                data.setEndDay(Integer.parseInt(buf[12]));
                data.setEndMonth(Integer.parseInt(buf[13]));
                data.setEffBeginDt(Datetime.getInstance(Datetime.YEAR,
                                                        Datetime.DAY,
                                                        new Date(buf[14])));
                if ( !DataBaseUtil.isEmpty(buf[15]))
                    data.setEffEndDt(Datetime.getInstance(Datetime.YEAR,
                                                          Datetime.DAY,
                                                          new Date(buf[15])));

                pwsMap.put(data.getTinwsysIsNumber(), data);
            }
        } catch (Exception e) {
            throw new Exception("Data file for pws has error at line " + i + ": " + e.getMessage());
        } finally {
            reader.close();
        }
        return pwsMap;
    }

    /**
     * compares the corresponding fields in the two DOs and returns true if they
     * are all the same, false otherwise
     */
    private boolean equals(PWSDO pws, PWSDO pws2) {
        return !DataBaseUtil.isDifferent(pws.getTinwsysIsNumber(), pws2.getTinwsysIsNumber()) &&
               !DataBaseUtil.isDifferent(pws.getNumber0(), pws2.getNumber0()) &&
               !DataBaseUtil.isDifferent(pws.getAlternateStNum(), pws2.getAlternateStNum()) &&
               !DataBaseUtil.isDifferent(pws.getName(), pws2.getName()) &&
               !DataBaseUtil.isDifferent(pws.getActivityStatusCd(), pws2.getActivityStatusCd()) &&
               !DataBaseUtil.isDifferent(pws.getDPrinCitySvdNm(), pws2.getDPrinCitySvdNm()) &&
               !DataBaseUtil.isDifferent(pws.getDPrinCntySvdNm(), pws2.getDPrinCntySvdNm()) &&
               !DataBaseUtil.isDifferent(pws.getDPopulationCount(), pws2.getDPopulationCount()) &&
               !DataBaseUtil.isDifferent(pws.getDPwsStTypeCd(), pws2.getDPwsStTypeCd()) &&
               !DataBaseUtil.isDifferent(pws.getActivityRsnTxt(), pws2.getActivityRsnTxt()) &&
               !DataBaseUtil.isDifferent(pws.getStartDay(), pws2.getStartDay()) &&
               !DataBaseUtil.isDifferent(pws.getStartMonth(), pws2.getStartMonth()) &&
               !DataBaseUtil.isDifferent(pws.getEndDay(), pws2.getEndDay()) &&
               !DataBaseUtil.isDifferent(pws.getEndMonth(), pws2.getEndMonth()) &&
               !DataBaseUtil.isDifferentDT(pws.getEffBeginDt(), pws2.getEffBeginDt()) &&
               !DataBaseUtil.isDifferentDT(pws.getEffEndDt(), pws2.getEffEndDt());
    }
}
