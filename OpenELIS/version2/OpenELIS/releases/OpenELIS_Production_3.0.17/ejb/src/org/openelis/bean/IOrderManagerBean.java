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

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.transaction.UserTransaction;

import org.jboss.security.annotation.SecurityDomain;
import org.openelis.domain.AuxDataViewDO;
import org.openelis.domain.AuxFieldValueViewDO;
import org.openelis.domain.AuxFieldViewDO;
import org.openelis.domain.Constants;
import org.openelis.domain.NoteViewDO;
import org.openelis.domain.IOrderContainerDO;
import org.openelis.domain.IOrderItemViewDO;
import org.openelis.domain.IOrderOrganizationViewDO;
import org.openelis.domain.IOrderRecurrenceDO;
import org.openelis.domain.IOrderTestAnalyteViewDO;
import org.openelis.domain.IOrderTestViewDO;
import org.openelis.domain.IOrderViewDO;
import org.openelis.manager.AuxDataManager;
import org.openelis.manager.NoteManager;
import org.openelis.manager.IOrderContainerManager;
import org.openelis.manager.IOrderFillManager;
import org.openelis.manager.IOrderItemManager;
import org.openelis.manager.IOrderManager;
import org.openelis.manager.IOrderOrganizationManager;
import org.openelis.manager.IOrderReceiptManager;
import org.openelis.manager.IOrderTestAnalyteManager;
import org.openelis.manager.IOrderTestManager;
import org.openelis.ui.common.Datetime;
import org.openelis.ui.common.ModulePermission.ModuleFlags;
import org.openelis.utils.User;

@Stateless
@SecurityDomain("openelis")
@TransactionManagement(TransactionManagementType.BEAN)
public class IOrderManagerBean {
    @Resource
    private SessionContext ctx;

    @EJB
    private LockBean      lock;

    @EJB
    private UserCacheBean userCache;
    
    @EJB
    private IOrderBean order;

    public IOrderManager fetchById(Integer id) throws Exception {
        return IOrderManager.fetchById(id);
    }
    
    public IOrderManager fetchByIorderItemId(Integer id) throws Exception {
        IOrderViewDO data;
        
        data = order.fetchByIorderItemId(id);
        return IOrderManager.fetchById(data.getId());
    }

    public IOrderManager fetchWithOrganizations(Integer id) throws Exception {
        return IOrderManager.fetchWithOrganizations(id);
    }

    public IOrderManager fetchWithItems(Integer id) throws Exception {
        return IOrderManager.fetchWithItems(id);
    }

    public IOrderManager fetchWithFills(Integer id) throws Exception {
        return IOrderManager.fetchWithFills(id);
    }

    public IOrderManager fetchWithNotes(Integer id) throws Exception {
        return IOrderManager.fetchWithNotes(id);
    }

    public IOrderManager fetchWithTests(Integer id) throws Exception {
        return IOrderManager.fetchWithTests(id);
    }

    public IOrderManager fetchWithContainers(Integer id) throws Exception {
        return IOrderManager.fetchWithContainers(id);
    }

    public IOrderManager fetchWithRecurring(Integer id) throws Exception {
        return IOrderManager.fetchWithRecurrence(id);
    }

    public IOrderManager add(IOrderManager man) throws Exception {
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

    public IOrderManager update(IOrderManager man) throws Exception {
        UserTransaction ut;

        checkSecurity(ModuleFlags.UPDATE);

        man.validate();

        ut = ctx.getUserTransaction();
        try {
            ut.begin();
            lock.validateLock(Constants.table().IORDER, man.getIorder().getId());
            man.update();
            lock.unlock(Constants.table().IORDER, man.getIorder().getId());
            ut.commit();
        } catch (Exception e) {
            ut.rollback();
            throw e;
        }

        return man;
    }

    public IOrderManager fetchForUpdate(Integer id) throws Exception {
        UserTransaction ut;
        IOrderManager man;

        ut = ctx.getUserTransaction();
        try {
            ut.begin();
            lock.lock(Constants.table().IORDER, id);
            man = fetchById(id);
            man.getRecurrence();
            ut.commit();
            return man;
        } catch (Exception e) {
            ut.rollback();
            throw e;
        }
    }

    public IOrderManager abortUpdate(Integer id) throws Exception {
        lock.unlock(Constants.table().IORDER, id);
        return fetchById(id);
    }

    public IOrderManager duplicate(Integer id) throws Exception {
        IOrderManager newMan, oldMan;

        oldMan = fetchById(id);
        newMan = IOrderManager.getInstance();
        duplicateIorder(oldMan, newMan, false);

        return newMan;
    }

    public void recur(Integer id) throws Exception {
        IOrderManager newMan, oldMan;

        oldMan = fetchById(id);
        newMan = IOrderManager.getInstance();
        duplicateIorder(oldMan, newMan, true);
        add(newMan);
    }

    public IOrderOrganizationManager fetchOrganizationByIorderId(Integer id) throws Exception {
        return IOrderOrganizationManager.fetchByIorderId(id);
    }

    public IOrderItemManager fetchItemByIorderId(Integer id) throws Exception {
        return IOrderItemManager.fetchByIorderId(id);
    }

    public IOrderFillManager fetchFillByIorderId(Integer id) throws Exception {
        return IOrderFillManager.fetchByOrderId(id);
    }

    public IOrderReceiptManager fetchReceiptByIorderId(Integer id) throws Exception {
        return IOrderReceiptManager.fetchByIorderId(id);
    }

    public IOrderTestManager fetchTestByIorderId(Integer id) throws Exception {
        return IOrderTestManager.fetchByIorderId(id);
    }

    public IOrderTestAnalyteManager fetchTestAnalyteByIorderTestId(Integer id) throws Exception {
        return IOrderTestAnalyteManager.fetchByIorderTestId(id);
    }

    public IOrderTestAnalyteManager fetchMergedTestAnalyteByIorderTestId(Integer id) throws Exception {
        return IOrderTestAnalyteManager.fetchMergedByIorderTestId(id);
    }

    public IOrderTestAnalyteManager fetchTestAnalyteByTestId(Integer id) throws Exception {
        return IOrderTestAnalyteManager.fetchByTestId(id);
    }

    public IOrderContainerManager fetchContainerByIorderId(Integer id) throws Exception {
        return IOrderContainerManager.fetchByIorderId(id);
    }

    public IOrderRecurrenceDO fetchRecurrenceByIorderId(Integer id) throws Exception {
        return IOrderManager.fetchRecurrenceByIorderId(id);
    }

    private void checkSecurity(ModuleFlags flag) throws Exception {
        userCache.applyPermission("order", flag);
    }

    private void duplicateIorder(IOrderManager oldMan, IOrderManager newMan,
                                boolean forRecurrence) throws Exception {
        Datetime now;
        IOrderViewDO oldData, newData;

        now = Datetime.getInstance(Datetime.YEAR, Datetime.DAY);

        oldData = oldMan.getIorder();
        newData = newMan.getIorder();

        if (forRecurrence)
            newData.setParentIorderId(oldData.getId());
        newData.setDescription(oldData.getDescription());
        newData.setStatusId(Constants.dictionary().ORDER_STATUS_PENDING);
        newData.setOrderedDate(now);
        newData.setNeededInDays(oldData.getNeededInDays());
        newData.setRequestedBy(forRecurrence ? oldData.getRequestedBy() : User.getName(ctx));
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

    private void duplicateOrganizations(IOrderOrganizationManager oldMan,
                                        IOrderOrganizationManager newMan) {
        IOrderOrganizationViewDO oldData, newData;

        for (int i = 0; i < oldMan.count(); i++ ) {
            oldData = oldMan.getOrganizationAt(i);
            newData = new IOrderOrganizationViewDO();
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

    private void duplicateItems(IOrderItemManager oldMan, IOrderItemManager newMan) {
        IOrderItemViewDO oldData, newData;

        for (int i = 0; i < oldMan.count(); i++ ) {
            oldData = oldMan.getItemAt(i);
            newData = new IOrderItemViewDO();
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

        for (int i = 0; i < oldMan.count(); i++ ) {
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

    private void duplicateTests(IOrderTestManager oldMan, IOrderTestManager newMan,
                                boolean forRecurrence) throws Exception {
        IOrderTestViewDO oldData, newData;
        IOrderTestAnalyteManager oldAnaMan;

        for (int i = 0; i < oldMan.count(); i++ ) {
            oldData = oldMan.getTestAt(i);
            newData = new IOrderTestViewDO();
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

    private void duplicateAnalytes(IOrderTestAnalyteManager oldMan,
                                   IOrderTestAnalyteManager newMan) {
        IOrderTestAnalyteViewDO oldData, newData;

        for (int i = 0; i < oldMan.count(); i++ ) {
            oldData = oldMan.getAnalyteAt(i);
            newData = new IOrderTestAnalyteViewDO();
            newData.setAnalyteId(oldData.getAnalyteId());
            newData.setAnalyteName(oldData.getAnalyteName());
            newData.setTestAnalyteSortOrder(oldData.getTestAnalyteSortOrder());
            newData.setTestAnalyteTypeId(oldData.getTestAnalyteTypeId());
            newData.setTestAnalyteIsReportable(oldData.getTestAnalyteIsReportable());
            newData.setTestAnalyteIsPresent(oldData.getTestAnalyteIsPresent());
            newMan.addAnalyte(newData);
        }
    }

    private void duplicateContainers(IOrderContainerManager oldMan,
                                     IOrderContainerManager newMan) {
        IOrderContainerDO oldData, newData;

        for (int i = 0; i < oldMan.count(); i++ ) {
            oldData = oldMan.getContainerAt(i);
            newData = new IOrderContainerDO();
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

        for (int i = 0; i < oldMan.count(); i++ ) {
            oldData = oldMan.getAuxDataAt(i);
            newData = new AuxDataViewDO();
            newData.setSortOrder(oldData.getSortOrder());
            newData.setAuxFieldId(oldData.getAuxFieldId());
            newData.setIsReportable(oldData.getIsReportable());
            newData.setTypeId(oldData.getTypeId());
            newData.setValue(oldData.getValue());
            newData.setDictionary(oldData.getDictionary());
            newData.setAuxFieldGroupId(oldData.getAuxFieldGroupId());
            newData.setAnalyteName(oldData.getAnalyteName());
            newData.setAnalyteId(oldData.getAnalyteId());
            newData.setAnalyteExternalId(oldData.getAnalyteExternalId());
            if ( !forRecurrence) {
                fieldDO = oldMan.getAuxFieldAt(i);
                values = oldMan.getAuxValuesAt(i);
                newMan.addAuxDataFieldAndValues(newData, fieldDO, values);
            } else {
                newMan.addAuxData(newData);
            }
        }
    }
}