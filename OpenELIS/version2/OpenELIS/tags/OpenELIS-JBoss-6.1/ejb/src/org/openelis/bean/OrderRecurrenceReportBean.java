/** Exhibit A - UIRF Open-source Based Public Software License.
* 
* The contents of this file are subject to the UIRF Open-source Based
* Public Software License(the "License"); you may not use this file except
* in compliance with the License. You may obtain a copy of the License at
* openelis.uhl.uiowa.edu
* 
* Software distributed under the License is distributed on an "AS IS"
* basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
* License for the specific language governing rights and limitations
* under the License.
* 
* The Original Code is OpenELIS code.
* 
* The Initial Developer of the Original Code is The University of Iowa.
* Portions created by The University of Iowa are Copyright 2006-2008. All
* Rights Reserved.
* 
* Contributor(s): ______________________________________.
* 
* Alternatively, the contents of this file marked
* "Separately-Licensed" may be used under the terms of a UIRF Software
* license ("UIRF Software License"), in which case the provisions of a
* UIRF Software License are applicable instead of those above. 
*/
package org.openelis.bean;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ejb.Asynchronous;
import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.jboss.ejb3.annotation.SecurityDomain;
import org.jboss.ejb3.annotation.TransactionTimeout;
import org.openelis.domain.Constants;
import org.openelis.domain.OrderRecurrenceDO;
import org.openelis.gwt.common.Datetime;
import org.openelis.gwt.common.NotFoundException;
import org.openelis.local.OrderManagerLocal;
import org.openelis.local.OrderRecurrenceLocal;
import org.openelis.local.OrderRecurrenceReportLocal;
import org.openelis.remote.OrderRecurrenceReportRemote;
import org.openelis.report.Prompt;

@Stateless
@SecurityDomain("openelis")

public class OrderRecurrenceReportBean implements OrderRecurrenceReportLocal, OrderRecurrenceReportRemote {
    
    @EJB
    private OrderRecurrenceLocal  orderRecurrence;

    @EJB
    private OrderManagerLocal     orderManager;

    private static final Logger log = Logger.getLogger("openelis");
    
    public ArrayList<Prompt> getPrompts() throws Exception {
        ArrayList<Prompt> p;

       p = new ArrayList<Prompt>();
       p.add(new Prompt("PRESS_BUTTON", Prompt.Type.STRING).setPrompt("Press the button:").setWidth(200));
       return p;
    }

    /**
     * Creates new orders from the orders that are to be recurred today 
     */
    @Asynchronous
    @TransactionTimeout(600)    
    public void recurOrders() {
        Datetime today;
        Calendar cal;
        ArrayList<OrderRecurrenceDO> list;

        cal = Calendar.getInstance();
        today = Datetime.getInstance(Datetime.YEAR, Datetime.DAY);
        cal.setTime(today.getDate());
        
        try {
            list = orderRecurrence.fetchActiveList();
        } catch (NotFoundException e) {
            log.fine("No recurring orders found");
            return;
        } catch (Exception e) {
            log.log(Level.SEVERE, "Failed to fetch orders", e);
            return;
        }        

        for (OrderRecurrenceDO data : list) {
            try {
                /*
                 * we need to make sure that the order can be recurred today
                 * before creating a new one from it
                 */
                if (recurs(cal, data)) 
                    orderManager.recur(data.getOrderId());                
            } catch (Exception e) {                
                log.log(Level.SEVERE, "Failed to recur order: "+ data.getOrderId().toString(), e);
            }
        }
    }
    
    /**  
      * An order only recurs on a given date if adding a specific number (frequency)
      * of units (days,months etc.) to its begin date produces that date exactly 
      */
    private boolean recurs(Calendar today, OrderRecurrenceDO data) {
        Integer freq, unitId;
        Datetime bd;
        Calendar next;          
        
        freq = data.getFrequency();
        unitId = data.getUnitId();
        bd = data.getActiveBegin();        
        next = Calendar.getInstance();
        next.setTime(bd.getDate());
        
        while (next.compareTo(today) < 1) {            
            if (next.compareTo(today) == 0) 
                return true;            
            if (Constants.dictionary().ORDER_RECURRENCE_UNIT_DAYS.equals(unitId))
                next.add(Calendar.DAY_OF_MONTH, freq);
            else if (Constants.dictionary().ORDER_RECURRENCE_UNIT_MONTHS.equals(unitId))
                next.add(Calendar.MONTH, freq);
            else if (Constants.dictionary().ORDER_RECURRENCE_UNIT_YEARS.equals(unitId))
                next.add(Calendar.YEAR, freq);
        }
        
        return false;
    }
}
