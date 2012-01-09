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

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.Schedule;
import javax.ejb.SessionContext;
import javax.ejb.Singleton;
import javax.ejb.Timer;

import org.apache.log4j.Logger;
import org.jboss.ejb3.annotation.SecurityDomain;
import org.jboss.ejb3.annotation.TransactionTimeout;
import org.openelis.entity.Cron;
import org.openelis.local.CronLocal;
import org.openelis.utils.FixedPeriodCron;

@Singleton
@SecurityDomain("openelis")
public class CronSchedulerBean {

    @Resource
    SessionContext ctx;

    @EJB
    CronLocal      cronLocal;

    Calendar       now;
    int            month, day, hour, minute, dayOfWeek;
    
    private static final Logger log = Logger.getLogger(CronSchedulerBean.class);

    /**
     * This method will be scheduled as a timer that goes off a the top of every
     * minute automatically by JBoss on a server start.
     * 
     * @param timer
     */
    @Schedule(hour = "*", minute = "*", second = "0", persistent = false)
    @TransactionTimeout(600)
    public void timer(Timer timer) {
        List<Cron> cronTabs;

        try {
            now = Calendar.getInstance();

            month = now.get(Calendar.MONTH) + 1;
            day = now.get(Calendar.DAY_OF_MONTH);
            hour = now.get(Calendar.HOUR_OF_DAY);
            minute = now.get(Calendar.MINUTE);
            dayOfWeek = now.get(Calendar.DAY_OF_WEEK) - 1;
            
            cronTabs = cronLocal.fetchActive();
            log.debug("Evaluating "+ cronTabs.size()+ " entries");

            for (Cron cron : cronTabs) {
                checkForRun(cron);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * This method will use the cron expression in the entry to compute the next
     * run date based on the last run date stored in the cron entry. If next run
     * date is equal to or before now date then the cron job will execute.
     * 
     * @param cron
     * @throws Exception
     */
    private void checkForRun(Cron cron) throws Exception {
        FixedPeriodCron cronUtil;

        cronUtil = new FixedPeriodCron(cron.getCronTab());

        if (cronUtil.getMonths().contains(month) && cronUtil.getDays().contains(day) &&
            cronUtil.getHours().contains(hour) && cronUtil.getMinutes().contains(minute) &&
            cronUtil.getDaysOfWeek().contains(dayOfWeek)) {
            run(cron);
        }
    }

    /**
     * This method uses reflection to call the bean and method specified in the
     * cron entry
     * 
     * @param cron
     * @throws Exception
     */
    private void run(Cron cron) throws Exception {
        Object beanInst;
        Object[] params;
        Class[] classes;
        
        beanInst = ctx.lookup(cron.getBean());

        if (cron.getParameters() != null) {
            params = cron.getParameters().split(";");
            classes = new Class[params.length];
            for (int i = 0; i < params.length; i++ ) {
                classes[i] = String.class;
            }
        } else {
            classes = new Class[] {};
            params = new String[] {};
        }

        log.info("Starting job: "+cron.getName());        
        try {
            beanInst.getClass().getMethod(cron.getMethod(), classes).invoke(beanInst, params);
        } catch (Exception e) {
            log.error("Job: "+ cron.getName(), e);
        }
    }
}