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

import java.util.Calendar;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.Schedule;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.ejb.Timer;

import org.jboss.ejb3.annotation.SecurityDomain;
import org.jboss.ejb3.annotation.TransactionTimeout;
import org.openelis.entity.Cron;
import org.openelis.local.CronLocal;
import org.openelis.utils.FixedPeriodCron;

@Stateless
@SecurityDomain("openelis")
public class CronSchedulerBean {

    @Resource
    private SessionContext       ctx;

    @EJB
    private CronLocal            cron;

    private static final Logger log = Logger.getLogger("openelis");

    /**
     * This method will be scheduled as a timer that goes off a the top of every
     * minute automatically by JBoss on a server start.
     * 
     * @param timer
     */
    @Schedule(hour = "*", minute = "*", second = "0", persistent = false)
    @TransactionTimeout(600)
    public void timer(Timer timer) {
        Calendar   now;
        int        month, day, hour, minute, dayOfWeek;
        List<Cron> cronTabs;

        try {
            now = Calendar.getInstance();

            month = now.get(Calendar.MONTH) + 1;
            day = now.get(Calendar.DAY_OF_MONTH);
            hour = now.get(Calendar.HOUR_OF_DAY);
            minute = now.get(Calendar.MINUTE);
            dayOfWeek = now.get(Calendar.DAY_OF_WEEK) - 1;
            
            cronTabs = cron.fetchActive();
            log.fine("Evaluating "+ cronTabs.size()+ " entries");

            for (Cron cronTab : cronTabs)
                checkForRun(cronTab, month, day, hour, minute, dayOfWeek);
        } catch (Exception e) {
            log.log(Level.SEVERE, "Could not execute cron job(s)", e);
        }
    }

    /**
     * This method will use the cron expression in the entry to compute the next
     * run date based on the last run date stored in the cron entry. If next run
     * date is equal to or before now date then the cron job will execute.
     * 
     * @param cronTab
     * @throws Exception
     */
    private void checkForRun(Cron cronTab, int month, int day, int hour, int minute, int dayOfWeek) throws Exception {
        FixedPeriodCron cronUtil;

        cronUtil = new FixedPeriodCron(cronTab.getCronTab());

        if (cronUtil.getMonths().contains(month) && cronUtil.getDays().contains(day) &&
            cronUtil.getHours().contains(hour) && cronUtil.getMinutes().contains(minute) &&
            cronUtil.getDaysOfWeek().contains(dayOfWeek)) {
            run(cronTab);
        }
    }

    /**
     * This method uses reflection to call the bean and method specified in the
     * cron entry
     * 
     * @param cronTab
     * @throws Exception
     */
    private void run(Cron cronTab) throws Exception {
        Object beanInst;
        Object[] params;
        Class[] classes;
        
        beanInst = ctx.lookup(cronTab.getBean());

        if (cronTab.getParameters() != null) {
            params = cronTab.getParameters().split(";");
            classes = new Class[params.length];
            for (int i = 0; i < params.length; i++ ) {
                classes[i] = String.class;
            }
        } else {
            classes = new Class[] {};
            params = new String[] {};
        }

        log.info("Starting job: "+cronTab.getName());        
        try {
            beanInst.getClass().getMethod(cronTab.getMethod(), classes).invoke(beanInst, params);
        } catch (Exception e) {
            log.log(Level.SEVERE, "Job: "+ cronTab.getName(), e);
        }
    }
}