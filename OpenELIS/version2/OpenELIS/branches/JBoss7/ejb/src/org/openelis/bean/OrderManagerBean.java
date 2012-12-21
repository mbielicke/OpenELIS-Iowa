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

import java.util.ArrayList;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.transaction.UserTransaction;

import org.apache.log4j.Logger;
import org.jboss.security.annotation.SecurityDomain;
import org.openelis.domain.AuxDataViewDO;
import org.openelis.domain.AuxFieldValueViewDO;
import org.openelis.domain.AuxFieldViewDO;
import org.openelis.domain.NoteViewDO;
import org.openelis.domain.OrderContainerDO;
import org.openelis.domain.OrderItemViewDO;
import org.openelis.domain.OrderOrganizationViewDO;
import org.openelis.domain.OrderRecurrenceDO;
import org.openelis.domain.OrderTestAnalyteViewDO;
import org.openelis.domain.OrderTestViewDO;
import org.openelis.domain.OrderViewDO;
import org.openelis.domain.ReferenceTable;
import org.openelis.gwt.common.Datetime;
import org.openelis.gwt.common.ModulePermission.ModuleFlags;
import org.openelis.manager.AuxDataManager;
import org.openelis.manager.NoteManager;
import org.openelis.manager.OrderContainerManager;
import org.openelis.manager.OrderFillManager;
import org.openelis.manager.OrderItemManager;
import org.openelis.manager.OrderManager;
import org.openelis.manager.OrderOrganizationManager;
import org.openelis.manager.OrderReceiptManager;
import org.openelis.manager.OrderTestAnalyteManager;
import org.openelis.manager.OrderTestManager;

@Stateless
@SecurityDomain("openelis")
@TransactionManagement(TransactionManagementType.BEAN)

public class OrderManagerBean {

    @Resource
    private SessionContext      ctx;

    @EJB
    private LockBean           lock;

    @EJB
    private DictionaryBean     dictionary;
    
    @EJB
    private UserCacheBean      userCache;

    private static final Logger log = Logger.getLogger(OrderManagerBean.class);

    private static Integer      pendingId;
    
    @PostConstruct
    public void init() {
        if (pendingId == null) {
            try {
                pendingId = dictionary.fetchBySystemName("order_status_pending").getId();
            } catch (Throwable e) {
                log.error("Failed to lookup constants for dictionary entries", e);
            }
        }
    }

    public OrderManager fetchById(Integer id) throws Exception {
        return OrderManager.fetchById(id);
    }
    
    public OrderManager fetchWithOrganizations(Integer id) throws Exception {
        return OrderManager.fetchWithOrganizations(id);
    }

    public OrderManager fetchWithItems(Integer id) throws Exception {
        return OrderManager.fetchWithItems(id);
    }

    public OrderManager fetchWithFills(Integer id) throws Exception {
        return OrderManager.fetchWithFills(id);
    }

    public OrderManager fetchWithNotes(Integer id) throws Exception {
        return OrderManager.fetchWithNotes(id);
    }
    
    public OrderManager fetchWithTests(Integer id) throws Exception {        
        return OrderManager.fetchWithTests(id);
    }
    
    public OrderManager fetchWithContainers(Integer id) throws Exception {        
        return OrderManager.fetchWithContainers(id);
    }
    
    public OrderManager fetchWithRecurring(Integer id) throws Exception {
        return OrderManager.fetchWithRecurrence(id);
    }
    
    public OrderManager add(OrderManager man) throws Exception {
        UserTransaction ut;

        checkSecurity(ModuleFlags.ADD);

        man.validate();

        ut = ctx.getUserTransaction();
        try {
            ut.begin();
            man.add();
            ut.commit();
        } catch (Exception e) {
            ut.rollback();
            throw e;
        }

        return man;
    }

    public OrderManager update(OrderManager man) throws Exception {
        UserTransaction ut;
        
        checkSecurity(ModuleFlags.UPDATE);

        man.validate();

        ut = ctx.getUserTransaction();
        try {
            ut.begin();
            lock.validateLock(ReferenceTable.ORDER, man.getOrder().getId());        
            man.update();
            lock.unlock(ReferenceTable.ORDER, man.getOrder().getId());
            ut.commit();
        } catch (Exception e) {
            ut.rollback();
            throw e;
        }

        return man;
    }

    public OrderManager fetchForUpdate(Integer id) throws Exception {
        UserTransaction ut;
        OrderManager man;

        ut = ctx.getUserTransaction();
        try {
            ut.begin();
            lock.lock(ReferenceTable.ORDER, id);
            man = fetchById(id);
            man.getRecurrence();
            ut.commit();
            return man;
        } catch (Exception e) {
            ut.rollback();
            throw e;
        }
    }

    public OrderManager abortUpdate(Integer id) throws Exception {
        lock.unlock(ReferenceTable.ORDER, id);
        return fetchById(id);
    }
    
    public OrderManager duplicate(Integer id) throws Exception {
        OrderManager newMan, oldMan;
        
        oldMan = fetchById(id);
        newMan = OrderManager.getInstance();      
        duplicateOrder(oldMan, newMan, false);
        
        return newMan;
    }
    
    public void recur(Integer id) throws Exception {
        OrderManager newMan, oldMan;
        
        oldMan = fetchById(id);
        newMan = OrderManager.getInstance();      
        duplicateOrder(oldMan, newMan, true);
        add(newMan);
    }
    
    public OrderOrganizationManager fetchOrganizationByOrderId(Integer id) throws Exception {
        return OrderOrganizationManager.fetchByOrderId(id);
    } 
    
    public OrderItemManager fetchItemByOrderId(Integer id) throws Exception {
        return OrderItemManager.fetchByOrderId(id);
    }

    public OrderFillManager fetchFillByOrderId(Integer id) throws Exception {
        return OrderFillManager.fetchByOrderId(id);
    }
    
    public OrderReceiptManager fetchReceiptByOrderId(Integer id) throws Exception {
        return OrderReceiptManager.fetchByOrderId(id);
    }
    
    public OrderTestManager fetchTestByOrderId(Integer id) throws Exception {       
        return OrderTestManager.fetchByOrderId(id);
    }
    
    public OrderTestAnalyteManager fetchTestAnalyteByOrderTestId(Integer id) throws Exception {        
        return OrderTestAnalyteManager.fetchByOrderTestId(id);
    }
    
    public OrderTestAnalyteManager fetchMergedTestAnalyteByOrderTestId(Integer id) throws Exception {
        return OrderTestAnalyteManager.fetchMergedByOrderTestId(id);
    }
    
    public OrderTestAnalyteManager fetchTestAnalyteByTestId(Integer id) throws Exception {
        return OrderTestAnalyteManager.fetchByTestId(id);
    }
    
    public OrderContainerManager fetchContainerByOrderId(Integer id) throws Exception {        
        return OrderContainerManager.fetchByOrderId(id);
    }
    
    public OrderRecurrenceDO fetchRecurrenceByOrderId(Integer id) throws Exception {        
        return OrderManager.fetchRecurrenceByOrderId(id);
    }
    
    private void checkSecurity(ModuleFlags flag) throws Exception {
        userCache.applyPermission("order", flag);
    }
    
    private void duplicateOrder(OrderManager oldMan, OrderManager newMan, boolean forRecurrence) throws Exception {
        Datetime now;
        OrderViewDO oldData, newData;
        
        now = Datetime.getInstance(Datetime.YEAR, Datetime.DAY);
        
        oldData = oldMan.getOrder();
        newData = newMan.getOrder();
        
        if (forRecurrence)
            newData.setParentOrderId(oldData.getId());
        newData.setDescription(oldData.getDescription());
        newData.setStatusId(pendingId);
        newData.setOrderedDate(now);
        newData.setNeededInDays(oldData.getNeededInDays());
        newData.setRequestedBy(forRecurrence ? oldData.getRequestedBy() : userCache.getName());
        newData.setCostCenterId(oldData.getCostCenterId());
        newData.setType(oldData.getType());
        newData.setExternalOrderNumber(oldData.getExternalOrderNumber());
        newData.setOrganization(oldData.getOrganization());
        newData.setOrganizationAttention(oldData.getOrganizationAttention());
        newData.setOrganizationId(oldData.getOrganizationId());        
        newData.setShipFromId(oldData.getShipFromId());   
        newData.setNumberOfForms(oldData.getNumberOfForms());               
        
        duplicateOrganizations(oldMan.getOrganizations(), newMan.getOrganizations());
        duplicateItems(oldMan.getItems(), newMan.getItems());        
        duplicateNotes(oldMan.getShippingNotes(), newMan.getShippingNotes());
        duplicateNotes(oldMan.getCustomerNotes(), newMan.getCustomerNotes());
        duplicateTests(oldMan.getTests(), newMan.getTests(), forRecurrence);
        duplicateContainers(oldMan.getContainers(), newMan.getContainers());
        duplicateAuxData(oldMan.getAuxData(), newMan.getAuxData(), forRecurrence);
    }
    
    private void duplicateOrganizations(OrderOrganizationManager oldMan, OrderOrganizationManager newMan)  {        
        OrderOrganizationViewDO oldData, newData;
        
        for (int i = 0; i < oldMan.count(); i++) {
            oldData = oldMan.getOrganizationAt(i);
            newData = new OrderOrganizationViewDO();
            newData.setOrganizationId(oldData.getOrganizationId());
            newData.setOrganizationAttention(oldData.getOrganizationAttention());
            newData.setTypeId(oldData.getTypeId());
            newData.setOrganizationName(oldData.getOrganizationName());
            newData.setOrganizationAddressMultipleUnit(oldData.getOrganizationAddressMultipleUnit());
            newData.setOrganizationAddressStreetAddress(oldData.getOrganizationAddressStreetAddress());
            newData.setOrganizationAddressCity(oldData.getOrganizationAddressCity());
            newData.setOrganizationAddressState(oldData.getOrganizationAddressState());
            newData.setOrganizationAddressZipCode(oldData.getOrganizationAddressZipCode());
            newData.setOrganizationAddressWorkPhone(oldData.getOrganizationAddressWorkPhone());
            newData.setOrganizationAddressFaxPhone(oldData.getOrganizationAddressFaxPhone());
            newData.setOrganizationAddressCountry(oldData.getOrganizationAddressCountry());
            newMan.addOrganization(newData);
        }
    }

    private void duplicateItems(OrderItemManager oldMan, OrderItemManager newMan)  {        
        OrderItemViewDO oldData, newData;
        
        for (int i = 0; i < oldMan.count(); i++) {
            oldData = oldMan.getItemAt(i);
            newData = new OrderItemViewDO();
            newData.setInventoryItemId(oldData.getInventoryItemId());
            newData.setQuantity(oldData.getQuantity());
            newData.setCatalogNumber(oldData.getCatalogNumber());
            newData.setUnitCost(oldData.getUnitCost());
            newData.setInventoryItemName(oldData.getInventoryItemName());
            newData.setStoreId(oldData.getStoreId());
            newMan.addItem(newData);
        }
    }
    
    private void duplicateNotes(NoteManager oldMan, NoteManager newMan) throws Exception {
        NoteViewDO oldData, newData;
        
        for (int i = 0; i < oldMan.count(); i++) {
            oldData = oldMan.getNoteAt(i);
            newData = new NoteViewDO();
            newData.setIsExternal(oldData.getIsExternal());
            newData.setSystemUserId(oldData.getSystemUserId());
            newData.setSubject(oldData.getSubject());
            newData.setText(oldData.getText()); 
            newData.setSystemUser(oldData.getSystemUser());
            newMan.addNote(newData);
        }
    }
    
    private void duplicateTests(OrderTestManager oldMan, OrderTestManager newMan, boolean forRecurrence) throws Exception {
        OrderTestViewDO oldData, newData;
        OrderTestAnalyteManager oldAnaMan;
        
        for (int i = 0; i < oldMan.count(); i++) {
            oldData = oldMan.getTestAt(i);
            newData = new OrderTestViewDO();
            newData.setItemSequence(oldData.getItemSequence());
            newData.setSortOrder(oldData.getSortOrder());
            newData.setTestId(oldData.getTestId());
            newData.setTestName(oldData.getTestName());
            newData.setMethodName(oldData.getMethodName());
            newData.setDescription(oldData.getDescription());
            newData.setIsActive(oldData.getIsActive());
            newMan.addTest(newData);
            
            oldAnaMan = forRecurrence ? oldMan.getAnalytesAt(i) : oldMan.getMergedAnalytesAt(i);
            duplicateAnalytes(oldAnaMan, newMan.getAnalytesAt(i));
        }
    }        
    
    private void duplicateAnalytes(OrderTestAnalyteManager oldMan, OrderTestAnalyteManager newMan) {
        OrderTestAnalyteViewDO oldData, newData;
        
        for (int i = 0; i < oldMan.count(); i++) {
            oldData = oldMan.getAnalyteAt(i);
            newData = new OrderTestAnalyteViewDO();
            newData.setAnalyteId(oldData.getAnalyteId());
            newData.setAnalyteName(oldData.getAnalyteName());
            newData.setTestAnalyteSortOrder(oldData.getTestAnalyteSortOrder());            
            newData.setTestAnalyteTypeId(oldData.getTestAnalyteTypeId());
            newData.setTestAnalyteIsReportable(oldData.getTestAnalyteIsReportable());
            newData.setTestAnalyteIsPresent(oldData.getTestAnalyteIsPresent());
            newMan.addAnalyte(newData);
        }
    }
    
    private void duplicateContainers(OrderContainerManager oldMan, OrderContainerManager newMan) {
        OrderContainerDO oldData, newData;
        
        for (int i = 0; i < oldMan.count(); i++) {
            oldData = oldMan.getContainerAt(i);
            newData = new OrderContainerDO();
            newData.setContainerId(oldData.getContainerId());
            newData.setItemSequence(oldData.getItemSequence());
            newData.setTypeOfSampleId(oldData.getTypeOfSampleId());            
            newMan.addContainer(newData);
        }
    }
    
    private void duplicateAuxData(AuxDataManager oldMan, AuxDataManager newMan,
                                  boolean forRecurrence) {
        AuxDataViewDO oldData, newData;   
        ArrayList<AuxFieldValueViewDO> values;
        AuxFieldViewDO fieldDO;
        
        for (int i = 0; i < oldMan.count(); i++) {
            oldData = oldMan.getAuxDataAt(i);
            newData = new AuxDataViewDO();
            newData.setSortOrder(oldData.getSortOrder());
            newData.setAuxFieldId(oldData.getAuxFieldId());
            newData.setIsReportable(oldData.getIsReportable());
            newData.setTypeId(oldData.getTypeId());
            newData.setValue(oldData.getValue());
            newData.setDictionary(oldData.getDictionary());
            newData.setGroupId(oldData.getGroupId());
            newData.setAnalyteName(oldData.getAnalyteName());
            newData.setAnalyteId(oldData.getAnalyteId());
            newData.setAnalyteExternalId(oldData.getAnalyteExternalId());
            if (!forRecurrence) {
                fieldDO = oldMan.getAuxFieldAt(i);
                values = oldMan.getAuxValuesAt(i);
                newMan.addAuxDataFieldAndValues(newData, fieldDO, values);
            } else {
                newMan.addAuxData(newData);
            }
        }
    }
}