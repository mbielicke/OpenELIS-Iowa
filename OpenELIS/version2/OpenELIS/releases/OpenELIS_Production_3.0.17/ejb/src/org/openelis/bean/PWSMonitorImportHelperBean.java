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
import org.openelis.domain.PWSMonitorDO;
import org.openelis.exception.ParseException;
import org.openelis.ui.common.DataBaseUtil;
import org.openelis.ui.common.ReportStatus;
import org.openelis.utils.ParseUtil;

/**
 * Reads data from a PWS Monitor file and updates the pws_monitor table in the
 * database. This bean manages its own transactions in order to reduce memory
 * usage.
 */
@Stateless
@SecurityDomain("openelis")

public class PWSMonitorImportHelperBean {

    @PersistenceContext(unitName = "openelis")
    private EntityManager       manager;

    @EJB
    private SessionCacheBean    session;

    @EJB
    private SystemVariableBean  systemVariable;

    @EJB
    private PWSMonitorBean      pwsMonitor;

    private String              PWS_MONITOR_FILE = "openelis_monitor.txt";

    private static final String DELIMITER        = "\\|";

    private static final Logger log              = Logger.getLogger("openelis");

    /**
     * Read data from the pws monitor file. Create new records in the database
     * for data that does not yet exist there, update records that are
     * different, and remove records that are not present in the file.
     */
    @RolesAllowed("pws-add")
    public void load(ReportStatus status) throws Exception {
        int i, r, onePercent;
        PWSMonitorDO p;
        ArrayList<PWSMonitorDO> records;
        HashMap<Integer, PWSMonitorDO> pmm;
        ArrayList<PWSMonitorDO> deleteList;

        status.setMessage("Reading file 4 of 4: PWS monitor file");
        status.setPercentComplete(75);
        session.setAttribute("PWSFileImport", status);

        /*
         * create a hash map from the file and fetch records from table
         */
        pmm = parse();
        records = pwsMonitor.fetchAll();

        /*
         * calculate the number of records needed to be updated to indicate
         * one percent of progress towards completion
         */
        onePercent = (int)Math.max(records.size() / 12.5, 12);
        status.setMessage("Loading file 4 of 4: PWS monitor file");
        session.setAttribute("PWSFileImport", status);

        i = r = 0;
        deleteList = new ArrayList<PWSMonitorDO>();
        for (PWSMonitorDO record : records) {
            i++ ;

            /*
             * the record is not present in the file, so it needs to be
             * deleted
             */
            p = pmm.get(record.getTiamrtaskIsNumber());
            if (p == null) {
                deleteList.add(record);
            } else {
                /*
                 * check if the database needs to be updated
                 */
                if ( !equals(p, record)) {
                    pwsMonitor.update(p);
                    r++ ;

                    /*
                     * batch update
                     */
                    if (r % 200 == 0)
                        manager.flush();
                }

                pmm.remove(record.getTiamrtaskIsNumber());
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
            for (PWSMonitorDO tp : deleteList) {
                pwsMonitor.delete(tp);
                r++ ;

                if (r % 200 == 0)
                    manager.flush();
            }
        }

        /*
         * add the records that were in the file but not in database
         */
        onePercent = (int)Math.max(pmm.size() / 12.5, 12);

        i = 0;
        for (PWSMonitorDO pm : pmm.values()) {
            i++ ;

            pwsMonitor.add(pm);
            r++ ;

            if (r % 200 == 0)
                manager.flush();
            
            if (i % onePercent == 0) {
                status.setPercentComplete(75 + 13 + i / onePercent);
                session.setAttribute("PWSFileImport", status);
            }
        }

        manager.flush();
    }

    /**
     * reads the file for pws_monitor table and return a mapping between
     * tiamrtask_is_number and pws_monitor field data associated with it
     */
    private HashMap<Integer, PWSMonitorDO> parse() throws Exception {
        int i;
        String line, path, buf[];
        PWSMonitorDO data;
        BufferedReader reader;
        HashMap<Integer, PWSMonitorDO> pmm;

        try {
            path = systemVariable.fetchByName("pws_path").getValue();
        } catch (Exception e) {
            log.severe("No 'pws_path' system variable defined");
            throw e;
        }

        reader = new BufferedReader(new FileReader(path + PWS_MONITOR_FILE));
        i = 1;

        /*
         * map from the pws monitor file, with the tiamrtask_is_number being the
         * key.
         */
        pmm = new HashMap<Integer, PWSMonitorDO>();

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

                data = new PWSMonitorDO();
                data.setTinwsfIsNumber(ParseUtil.parseIntField(buf[0], false, "TINWSF_IS_NUMBER"));
                data.setTiamrtaskIsNumber(ParseUtil.parseIntField(buf[1], false, "TIAMRTASK_IS_NUMBER"));
                data.setTinwsysIsNumber(ParseUtil.parseIntField(buf[2], false, "TINWSYS_IS_NUMBER"));
                data.setStAsgnIdentCd(ParseUtil.parseStrField(buf[3], 12, true, "ST_ASGN_IDENT_CD"));
                data.setName(ParseUtil.parseStrField(buf[4], 40, true, "TINWSF_NAME"));
                data.setTiaanlgpTiaanlytName(ParseUtil.parseStrField(buf[6], -64, true, "TSAANLYT_NAME"));
                if (data.getTiaanlgpTiaanlytName() == null)
                    data.setTiaanlgpTiaanlytName(ParseUtil.parseStrField(buf[5], -64, true, "TSAANLYT_NAME"));
                data.setNumberSamples(ParseUtil.parseIntField(buf[7], false, "NUMBER_SAMPLES"));
                data.setCompBeginDate(ParseUtil.parseDateField(buf[8], false, "COMP_BEGIN_DATE"));
                data.setCompEndDate(ParseUtil.parseDateField(buf[9], true, "COMP_END_DATE"));
                data.setFrequencyName(ParseUtil.parseStrField(buf[10], 25, false, "FREQUENCY_NAME"));
                data.setPeriodName(ParseUtil.parseStrField(buf[11], 20, false, "PERIOD_NAME"));

                pmm.put(data.getTiamrtaskIsNumber(), data);
            }
        } catch (Exception e) {
            throw new Exception(PWS_MONITOR_FILE+" has error in line " + i + ": " + e.getMessage());
        } finally {
            reader.close();
        }
        return pmm;
    }

    /**
     * compares the corresponding fields in the two DOs and returns true if they
     * are all the same, false otherwise
     */
    private boolean equals(PWSMonitorDO a, PWSMonitorDO b) {
        return !DataBaseUtil.isDifferent(a.getTinwsysIsNumber(), b.getTinwsysIsNumber()) &&
               !DataBaseUtil.isDifferent(a.getStAsgnIdentCd(), b.getStAsgnIdentCd()) &&
               !DataBaseUtil.isDifferent(a.getName(), b.getName()) &&
               !DataBaseUtil.isDifferent(a.getTiaanlgpTiaanlytName(), b.getTiaanlgpTiaanlytName()) &&
               !DataBaseUtil.isDifferent(a.getNumberSamples(), b.getNumberSamples()) &&
               !DataBaseUtil.isDifferentDT(a.getCompBeginDate(), b.getCompBeginDate()) &&
               !DataBaseUtil.isDifferentDT(a.getCompEndDate(), b.getCompEndDate()) &&
               !DataBaseUtil.isDifferent(a.getFrequencyName(), b.getFrequencyName()) &&
               !DataBaseUtil.isDifferent(a.getPeriodName(), b.getPeriodName());
    }
}
