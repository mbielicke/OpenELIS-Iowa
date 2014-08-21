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
import org.openelis.domain.PWSMonitorDO;
import org.openelis.exception.ParseException;
import org.openelis.ui.common.DataBaseUtil;
import org.openelis.ui.common.Datetime;
import org.openelis.ui.common.ReportStatus;

/**
 * Reads data from a PWS Monitor file and updates the pws_monitor table in the
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
public class PWSMonitorImportHelperBean {

    @EJB
    private SessionCacheBean    session;

    @EJB
    private SystemVariableBean  systemVariable;

    @Resource
    private SessionContext      ctx;

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
    public void load(ReportStatus status) throws Exception {
        boolean toCommit;
        int i, onePercent;
        Integer tiamrtaskIsNumber;
        UserTransaction ut;
        PWSMonitorDO pm;
        HashMap<Integer, PWSMonitorDO> pmm;
        ArrayList<Integer> deleteList;
        ArrayList<PWSMonitorDO> records;

        ut = ctx.getUserTransaction();
        try {
            deleteList = new ArrayList<Integer>();
            status.setMessage("Reading file 4 of 4: PWS monitor file");
            status.setPercentComplete(75);
            session.setAttribute("PWSFileImport", status);
            /*
             * create a hash map from the file
             */
            pmm = parse();
            records = pwsMonitor.fetchAll();
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
            for (PWSMonitorDO record : records) {
                tiamrtaskIsNumber = record.getTiamrtaskIsNumber();
                pm = pmm.get(tiamrtaskIsNumber);
                /*
                 * the record is not present in the file, so it needs to be
                 * deleted
                 */
                if (pm == null) {
                    deleteList.add(record.getTiamrtaskIsNumber());
                    continue;
                }
                /*
                 * check if the database needs to be updated
                 */
                if ( !equals(pm, record)) {
                    i++ ;
                    pwsMonitor.update(pm);
                    toCommit = true;
                    if (i % 1000 == 0) {
                        ut.commit();
                        ut.begin();
                        toCommit = false;
                    }
                    if (i % onePercent == 0) {
                        status.setMessage("Loading file 4 of 4: PWS monitor file");
                        status.setPercentComplete(50 + i / onePercent);
                        session.setAttribute("PWSFileImport", status);
                    }
                }
                /*
                 * this map must only have records that don't exist in the
                 * database
                 */
                pmm.remove(tiamrtaskIsNumber);
            }
            /*
             * delete records from the database that are not present in the file
             */
            if (deleteList.size() > 0) {
                pwsMonitor.deleteList(deleteList);
                ut.commit();
                ut.begin();
            }
            i = 0;
            /*
             * calculate the number of records needed to be added to indicate
             * one percent of progress towards completion
             */
            onePercent = (int) (pmm.size() / 12.5);
            if (onePercent == 0)
                onePercent = 12;
            for (PWSMonitorDO pwsm : pmm.values()) {
                i++ ;
                pwsMonitor.add(pwsm);
                toCommit = true;
                if (i % 1000 == 0) {
                    ut.commit();
                    ut.begin();
                    toCommit = false;
                }
                if (i % onePercent == 0) {
                    status.setMessage("Loading file 4 of 4: PWS monitor file");
                    status.setPercentComplete(75 + 13 + i / onePercent);
                    session.setAttribute("PWSFileImport", status);
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
     * reads the file for pws_monitor table and return a mapping between
     * tiamrtask_is_number and pws_monitor field data associated with it
     */
    private HashMap<Integer, PWSMonitorDO> parse() throws Exception {
        int i;
        String line, path, buf[];
        PWSMonitorDO data;
        BufferedReader reader;
        HashMap<Integer, PWSMonitorDO> pwsMonitorMap;

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
         * key, and the data from that line in the file being the value
         */
        pwsMonitorMap = new HashMap<Integer, PWSMonitorDO>();

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
                data.setTinwsfIsNumber(Integer.parseInt(buf[0]));
                data.setTiamrtaskIsNumber(Integer.parseInt(buf[1]));
                data.setTinwsysIsNumber(Integer.parseInt(buf[2]));
                data.setStAsgnIdentCd(buf[3]);
                data.setName(buf[4]);
                if ( !DataBaseUtil.isEmpty(buf[6]))
                    data.setTiaanlgpTiaanlytName(buf[6]);
                else
                    data.setTiaanlgpTiaanlytName(buf[5]);
                data.setNumberSamples(Integer.parseInt(buf[7]));
                data.setCompBeginDate(Datetime.getInstance(Datetime.YEAR,
                                                           Datetime.DAY,
                                                           new Date(buf[8])));
                data.setCompEndDate(Datetime.getInstance(Datetime.YEAR,
                                                         Datetime.DAY,
                                                         new Date(buf[9])));
                data.setFrequencyName(buf[10]);
                data.setPeriodName(buf[11]);

                pwsMonitorMap.put(data.getTiamrtaskIsNumber(), data);
            }
        } catch (Exception e) {
            throw new Exception("Data file for pws_monitor has error at line " + i + ": " +
                                e.getMessage());
        } finally {
            reader.close();
        }
        return pwsMonitorMap;
    }

    /**
     * compares the corresponding fields in the two DOs and returns true if they
     * are all the same, false otherwise
     */
    private boolean equals(PWSMonitorDO pwsMonitor, PWSMonitorDO pwsMonitor2) {
        return !DataBaseUtil.isDifferent(pwsMonitor.getTinwsysIsNumber(),
                                         pwsMonitor2.getTinwsysIsNumber()) &&
               !DataBaseUtil.isDifferent(pwsMonitor.getStAsgnIdentCd(),
                                         pwsMonitor2.getStAsgnIdentCd()) &&
               !DataBaseUtil.isDifferent(pwsMonitor.getName(), pwsMonitor2.getName()) &&
               !DataBaseUtil.isDifferent(pwsMonitor.getTiaanlgpTiaanlytName(),
                                         pwsMonitor2.getTiaanlgpTiaanlytName()) &&
               !DataBaseUtil.isDifferent(pwsMonitor.getNumberSamples(),
                                         pwsMonitor2.getNumberSamples()) &&
               !DataBaseUtil.isDifferentDT(pwsMonitor.getCompBeginDate(),
                                           pwsMonitor2.getCompBeginDate()) &&
               !DataBaseUtil.isDifferentDT(pwsMonitor.getCompEndDate(),
                                           pwsMonitor2.getCompEndDate()) &&
               !DataBaseUtil.isDifferent(pwsMonitor.getFrequencyName(),
                                         pwsMonitor2.getFrequencyName()) &&
               !DataBaseUtil.isDifferent(pwsMonitor.getPeriodName(), pwsMonitor2.getPeriodName());
    }
}
