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
package org.openelis.manager;

import org.openelis.domain.Constants;
import org.openelis.bean.IOrderRecurrenceBean;
import org.openelis.domain.IOrderRecurrenceDO;
import org.openelis.domain.IOrderViewDO;
import org.openelis.ui.common.DataBaseUtil;
import org.openelis.ui.common.ValidationErrorsList;
import org.openelis.utils.EJBFactory;

public class IOrderManagerProxy {

    public IOrderManager fetchById(Integer id) throws Exception {
        IOrderViewDO data;
        IOrderManager m;

        data = EJBFactory.getIOrder().fetchById(id);
        m = IOrderManager.getInstance();

        m.setIorder(data);

        return m;
    }

    public IOrderManager fetchWithOrganizations(Integer id) throws Exception {
        IOrderManager m;

        m = fetchById(id);
        m.getOrganizations();

        return m;
    }

    public IOrderManager fetchWithItems(Integer id) throws Exception {
        IOrderManager m;

        m = fetchById(id);
        m.getItems();

        return m;
    }

    public IOrderManager fetchWithFills(Integer id) throws Exception {
        IOrderManager m;

        m = fetchById(id);
        m.getFills();

        return m;
    }

    public IOrderManager fetchWithNotes(Integer id) throws Exception {
        IOrderManager m;

        m = fetchById(id);
        m.getShippingNotes();
        m.getCustomerNotes();
        m.getInternalNotes();
        m.getSampleNotes();

        return m;
    }

    public IOrderManager fetchWithTests(Integer id) throws Exception {
        IOrderManager m;

        m = fetchById(id);
        m.getTests();

        return m;
    }

    public IOrderManager fetchWithContainers(Integer id) throws Exception {
        IOrderManager m;

        m = fetchById(id);
        m.getContainers();

        return m;
    }

    public IOrderManager fetchWithRecurring(Integer id) throws Exception {
        return fetchById(id);
    }

    public IOrderRecurrenceDO fetchRecurrenceByIorderId(Integer id) throws Exception {
        return EJBFactory.getIOrderRecurrence().fetchByIorderId(id);
    }

    public IOrderManager add(IOrderManager man) throws Exception {
        Integer id;
        IOrderViewDO data;
        IOrderRecurrenceDO ord;
        IOrderRecurrenceBean orl;

        data = man.getIorder();
        EJBFactory.getIOrder().add(data);
        id = data.getId();

        if (man.organizations != null) {
            man.getOrganizations().setIorderId(id);
            man.getOrganizations().add();
        }

        if (man.items != null) {
            man.getItems().setOrderId(id);
            man.getItems().add();
        }

        if (man.fills != null) {
            man.getFills().setOrderId(id);
            man.getFills().add();
        }

        if (man.shipNotes != null) {
            man.getShippingNotes().setReferenceId(id);
            man.getShippingNotes()
               .setReferenceTableId(Constants.table().IORDER_SHIPPING_NOTE);
            man.getShippingNotes().add();
        }

        if (man.customerNotes != null) {
            man.getCustomerNotes().setReferenceId(id);
            man.getCustomerNotes()
               .setReferenceTableId(Constants.table().IORDER_CUSTOMER_NOTE);
            man.getCustomerNotes().add();
        }

        if (man.internalNotes != null) {
            man.getInternalNotes().setReferenceId(id);
            man.getInternalNotes().setReferenceTableId(Constants.table().IORDER);
            man.getInternalNotes().add();
        }

        if (man.sampleNotes != null) {
            man.getSampleNotes().setReferenceId(id);
            man.getSampleNotes().setReferenceTableId(Constants.table().IORDER_SAMPLE_NOTE);
            man.getSampleNotes().add();
        }

        if (man.auxData != null) {
            man.getAuxData().setReferenceId(id);
            man.getAuxData().setReferenceTableId(Constants.table().IORDER);
            man.getAuxData().add();
        }

        if (man.containers != null) {
            man.getContainers().setOrderId(id);
            man.getContainers().add();
        }

        if (man.tests != null) {
            man.getTests().setIorderId(id);
            man.getTests().add();
        }

        ord = man.recurrence;
        if (ord != null && ord.isChanged()) {
            orl = EJBFactory.getIOrderRecurrence();
            if ( !orl.isEmpty(ord)) {
                ord.setIorderId(id);
                orl.add(ord);
            }
        }

        return man;
    }

    public IOrderManager update(IOrderManager man) throws Exception {
        Integer id;
        IOrderViewDO data;
        IOrderRecurrenceDO ord;

        data = man.getIorder();
        id = data.getId();
        EJBFactory.getIOrder().update(data);

        if (man.organizations != null) {
            man.getOrganizations().setIorderId(id);
            man.getOrganizations().update();
        }

        if (man.items != null) {
            man.getItems().setOrderId(id);
            man.getItems().update();
        }

        if (man.fills != null) {
            man.getFills().setOrderId(id);
            man.getFills().update();
        }

        if (man.shipNotes != null) {
            man.getShippingNotes().setReferenceId(id);
            man.getShippingNotes()
               .setReferenceTableId(Constants.table().IORDER_SHIPPING_NOTE);
            man.getShippingNotes().update();
        }

        if (man.customerNotes != null) {
            man.getCustomerNotes().setReferenceId(id);
            man.getCustomerNotes()
               .setReferenceTableId(Constants.table().IORDER_CUSTOMER_NOTE);
            man.getCustomerNotes().update();
        }

        if (man.internalNotes != null) {
            man.getInternalNotes().setReferenceId(id);
            man.getInternalNotes().setReferenceTableId(Constants.table().IORDER);
            man.getInternalNotes().update();
        }

        if (man.sampleNotes != null) {
            man.getSampleNotes().setReferenceId(id);
            man.getSampleNotes().setReferenceTableId(Constants.table().IORDER_SAMPLE_NOTE);
            man.getSampleNotes().update();
        }

        if (man.auxData != null) {
            man.getAuxData().setReferenceId(id);
            man.getAuxData().setReferenceTableId(Constants.table().IORDER);
            man.getAuxData().update();
        }

        if (man.containers != null) {
            man.getContainers().setOrderId(id);
            man.getContainers().update();
        }

        if (man.tests != null) {
            man.getTests().setIorderId(id);
            man.getTests().update();
        }

        ord = man.recurrence;
        if (ord != null && ord.isChanged()) {
            if (ord.getIorderId() == null) {
                man.recurrence.setIorderId(id);
                EJBFactory.getIOrderRecurrence().add(ord);
            } else {
                EJBFactory.getIOrderRecurrence().update(ord);
            }
        }

        return man;
    }

    public IOrderManager fetchForUpdate(IOrderManager man) throws Exception {
        assert false : "not supported";
        return null;
    }

    public IOrderManager fetchForUpdate(Integer id) throws Exception {
        assert false : "not supported";
        return null;
    }
    
    public IOrderManager abortUpdate(Integer id) throws Exception {
        assert false : "not supported";
        return null;
    }

    public void validate(IOrderManager man) throws Exception {
        ValidationErrorsList list;
        IOrderRecurrenceDO data;

        list = new ValidationErrorsList();
        try {
            EJBFactory.getIOrder().validate(man.getIorder());
        } catch (Exception e) {
            DataBaseUtil.mergeException(list, e);
        }

        try {
            if (man.organizations != null)
                man.getOrganizations().validate();
        } catch (Exception e) {
            DataBaseUtil.mergeException(list, e);
        }

        try {
            if (man.items != null)
                man.getItems().validate(man.getIorder().getType());
        } catch (Exception e) {
            DataBaseUtil.mergeException(list, e);
        }

        try {
            if (man.fills != null)
                man.getFills().validate();
        } catch (Exception e) {
            DataBaseUtil.mergeException(list, e);
        }

        try {
            if (man.tests != null)
                man.getTests().validate();
        } catch (Exception e) {
            DataBaseUtil.mergeException(list, e);
        }

        try {
            if (man.containers != null)
                man.getContainers().validate();
        } catch (Exception e) {
            DataBaseUtil.mergeException(list, e);
        }

        if (man.auxData != null)
            man.getAuxData().validate(list);

        data = man.recurrence;
        if (data != null && "Y".equals(data.getIsActive()) && data.isChanged()) {
            try {
                EJBFactory.getIOrderRecurrence().validate(data);
            } catch (Exception e) {
                DataBaseUtil.mergeException(list, e);
            }
        }

        if (list.size() > 0)
            throw list;
    }
}