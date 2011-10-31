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

import javax.annotation.PostConstruct;
import javax.annotation.security.RolesAllowed;
import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.apache.log4j.Logger;
import org.jboss.ejb3.annotation.SecurityDomain;
import org.jboss.ejb3.annotation.TransactionTimeout;
import org.openelis.domain.AuxDataViewDO;
import org.openelis.domain.NoteViewDO;
import org.openelis.domain.OrderContainerDO;
import org.openelis.domain.OrderItemViewDO;
import org.openelis.domain.OrderRecurrenceDO;
import org.openelis.domain.OrderTestViewDO;
import org.openelis.domain.OrderViewDO;
import org.openelis.gwt.common.Datetime;
import org.openelis.gwt.common.NotFoundException;
import org.openelis.local.DictionaryLocal;
import org.openelis.local.OrderRecurrenceReportLocal;
import org.openelis.manager.AuxDataManager;
import org.openelis.manager.NoteManager;
import org.openelis.manager.OrderContainerManager;
import org.openelis.manager.OrderItemManager;
import org.openelis.manager.OrderManager;
import org.openelis.manager.OrderTestManager;
import org.openelis.remote.OrderRecurrenceReportRemote;
import org.openelis.report.Prompt;
import org.openelis.utils.EJBFactory;

@Stateless
@SecurityDomain("openelis")
public class OrderRecurrenceReportBean implements OrderRecurrenceReportLocal, OrderRecurrenceReportRemote {
    
    @EJB
    private DictionaryLocal     dictionary;

    private static Integer      daysId, monthsId, yearsId, pendingId;

    private static final Logger log = Logger.getLogger(OrderRecurrenceReportBean.class);
    
    @PostConstruct
    public void init() {
        if (daysId == null) {
            try {
                daysId = dictionary.fetchBySystemName("order_recurrence_unit_days").getId();
                monthsId = dictionary.fetchBySystemName("order_recurrence_unit_months").getId();
                yearsId = dictionary.fetchBySystemName("order_recurrence_unit_years").getId();
                pendingId = dictionary.fetchBySystemName("order_status_pending").getId();
            } catch (Throwable e) {
                log.error("Failed to lookup constants for dictionary entries", e);
            }
        }
    }
    
    public ArrayList<Prompt> getPrompts() {
        return new ArrayList<Prompt>();
    }

    /**
     * Creates new orders from the orders that are to be recurred today 
     */
    @RolesAllowed("r_final-select")
    @TransactionTimeout(600)
    public void recurOrders() {
        Integer id;
        Datetime now, today;
        Calendar cal;
        OrderManager man, nom;
        ArrayList<OrderRecurrenceDO> list;

        now = Datetime.getInstance(Datetime.YEAR, Datetime.MINUTE);
        today = Datetime.getInstance(Datetime.YEAR, Datetime.DAY);
        cal = Calendar.getInstance();
        cal.setTime(today.getDate());
        id = null;
        
        try {
            list = EJBFactory.getOrderRecurrence().fetchActiveList();
        } catch (NotFoundException e) {
            log.info("No recurring orders found", e);
            return;
        } catch (Exception e) {
            log.error("Failed to fetch orders", e);
            return;
        }        

        for (OrderRecurrenceDO data : list) {
            id = data.getOrderId();
            try {
                man = OrderManager.fetchById(id);
                /*
                 * we need to make sure that the order can be recurred today
                 * before creating a new one from it
                 */
                if (recurs(cal, data)) {
                    nom = getOrderManager(man, now, today);
                    nom.add();
                }
            } catch (Exception e) {                
                log.error("Failed to recur order: "+ id.toString(), e);
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
            if (daysId.equals(unitId))
                next.add(Calendar.DAY_OF_MONTH, freq);
            else if (monthsId.equals(unitId))
                next.add(Calendar.MONTH, freq);
            else if (yearsId.equals(unitId))
                next.add(Calendar.YEAR, freq);
        }
        
        return false;
    }
    
    /** 
     * Creates a new OrderManager from "man" without the data from OrderFillManager   
     */
    private OrderManager getOrderManager(OrderManager man, Datetime now, Datetime today) throws Exception {
        OrderManager nom;
        
        nom = OrderManager.getInstance();        
        fillOrder(man.getOrder(), nom.getOrder(), today);         
        fillItemManager(man.getItems(), nom.getItems());        
        fillNoteManager(man.getShippingNotes(), nom.getShippingNotes(), now);
        fillNoteManager(man.getCustomerNotes(), nom.getCustomerNotes(), now);
        fillTestManager(man.getTests(), nom.getTests());
        fillContainerManager(man.getContainers(), nom.getContainers());
        fillAuxDataManager(man.getAuxData(), nom.getAuxData());
        
        return nom;
    }        

    /**
     * Fills a new OrderViewDO (ndata) with the data in an existing one (data) 
     */
    private void fillOrder(OrderViewDO data, OrderViewDO ndata, Datetime today) {               
        ndata.setParentOrderId(data.getId());
        ndata.setDescription(data.getDescription());
        ndata.setStatusId(pendingId);
        ndata.setOrderedDate(today);
        ndata.setNeededInDays(data.getNeededInDays());
        ndata.setRequestedBy(data.getRequestedBy());
        ndata.setCostCenterId(data.getCostCenterId());
        ndata.setOrganizationId(data.getOrganizationId());
        ndata.setOrganizationAttention(data.getOrganizationAttention());
        ndata.setType(data.getType());
        ndata.setExternalOrderNumber(data.getExternalOrderNumber());
        ndata.setReportToId(data.getReportToId());
        ndata.setReportToAttention(data.getReportToAttention());
        ndata.setBillToId(data.getBillToId());
        ndata.setBillToAttention(data.getBillToAttention());
        ndata.setShipFromId(data.getShipFromId());   
        ndata.setNumberOfForms(data.getNumberOfForms());
    }
    
    /**
     * Fills a new OrderItemManager (nim) with the data in an existing one (man) 
     */
    private void fillItemManager(OrderItemManager man, OrderItemManager nim)  {        
        OrderItemViewDO data, ndata;
        
        for (int i = 0; i < man.count(); i++) {
            data = man.getItemAt(i);
            ndata = new OrderItemViewDO();
            ndata.setInventoryItemId(data.getInventoryItemId());
            ndata.setQuantity(data.getQuantity());
            ndata.setCatalogNumber(data.getCatalogNumber());
            ndata.setUnitCost(data.getUnitCost());
            nim.addItem(ndata);
        }
    }
    
    /**
     * Fills a new NoteManager (nnm) with the data in an existing one (man) 
     */
    private void fillNoteManager(NoteManager man, NoteManager nnm, Datetime now) throws Exception {
        NoteViewDO data, ndata;
        
        for (int i = 0; i < man.count(); i++) {
            data = man.getNoteAt(i);
            ndata = new NoteViewDO();
            ndata.setTimestamp(now);
            ndata.setIsExternal(data.getIsExternal());
            ndata.setSystemUserId(data.getSystemUserId());
            ndata.setSubject(data.getSubject());
            ndata.setText(data.getText());            
            nnm.addNote(ndata);
        }
    }
    
    /**
     * Fills a new OrderTestManager (notm) with the data in an existing one (man) 
     */
    private void fillTestManager(OrderTestManager man, OrderTestManager notm) {
        OrderTestViewDO data, ndata;
        
        for (int i = 0; i < man.count(); i++) {
            data = man.getTestAt(i);
            ndata = new OrderTestViewDO();
            ndata.setSortOrder(data.getSortOrder());
            ndata.setTestId(data.getTestId());            
            notm.addTest(ndata);
        }
    }
    
    /**
     * Fills a new OrderContainerManager (nocm) with the data in an existing one (man) 
     */
    private void fillContainerManager(OrderContainerManager man, OrderContainerManager nocm) {
        OrderContainerDO data, ndata;
        
        for (int i = 0; i < man.count(); i++) {
            data = man.getContainerAt(i);
            ndata = new OrderContainerDO();
            ndata.setContainerId(data.getContainerId());
            ndata.setNumberOfContainers(data.getNumberOfContainers());
            ndata.setTypeOfSampleId(data.getTypeOfSampleId());            
            nocm.addContainer(ndata);
        }
    }
    
    /**
     * Fills a new AuxDataManager (nadm) with the data in an existing one (man) 
     */
    private void fillAuxDataManager(AuxDataManager man, AuxDataManager nadm) {
        AuxDataViewDO data, ndata;
        
        for (int i = 0; i < man.count(); i++) {
            data = man.getAuxDataAt(i);
            ndata = new AuxDataViewDO();
            ndata.setSortOrder(data.getSortOrder());
            ndata.setAuxFieldId(data.getAuxFieldId());
            ndata.setIsReportable(data.getIsReportable());
            ndata.setTypeId(data.getTypeId());
            ndata.setValue(data.getValue());
            nadm.addAuxData(ndata);
        }
    }
}
