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
package org.openelis.modules.order.server;

import org.openelis.domain.BillToReportToDO;
import org.openelis.domain.IdNameDO;
import org.openelis.domain.InventoryItemAutoDO;
import org.openelis.domain.InventoryLocationDO;
import org.openelis.domain.InventoryReceiptDO;
import org.openelis.domain.NoteDO;
import org.openelis.domain.OrderAddAutoFillDO;
import org.openelis.domain.OrderDO;
import org.openelis.domain.OrderItemDO;
import org.openelis.domain.OrganizationAutoDO;
import org.openelis.gwt.common.EntityLockedException;
import org.openelis.gwt.common.FieldErrorException;
import org.openelis.gwt.common.Form;
import org.openelis.gwt.common.FormErrorException;
import org.openelis.gwt.common.LastPageException;
import org.openelis.gwt.common.QueryException;
import org.openelis.gwt.common.RPC;
import org.openelis.gwt.common.RPCException;
import org.openelis.gwt.common.TableFieldErrorException;
import org.openelis.gwt.common.data.AbstractField;
import org.openelis.gwt.common.data.BooleanObject;
import org.openelis.gwt.common.data.Data;
import org.openelis.gwt.common.data.DataMap;
import org.openelis.gwt.common.data.DataModel;
import org.openelis.gwt.common.data.DataObject;
import org.openelis.gwt.common.data.DataSet;
import org.openelis.gwt.common.data.DropDownField;
import org.openelis.gwt.common.data.NumberField;
import org.openelis.gwt.common.data.NumberObject;
import org.openelis.gwt.common.data.StringField;
import org.openelis.gwt.common.data.StringObject;
import org.openelis.gwt.common.data.TableField;
import org.openelis.gwt.server.ServiceUtils;
import org.openelis.gwt.services.AppScreenFormServiceInt;
import org.openelis.gwt.services.AutoCompleteServiceInt;
import org.openelis.metamap.OrderMetaMap;
import org.openelis.persistence.CachingManager;
import org.openelis.persistence.EJBFactory;
import org.openelis.remote.CategoryRemote;
import org.openelis.remote.InventoryItemRemote;
import org.openelis.remote.OrderRemote;
import org.openelis.remote.OrganizationRemote;
import org.openelis.server.constants.Constants;
import org.openelis.util.Datetime;
import org.openelis.util.SessionManager;
import org.openelis.util.UTFResource;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class OrderService implements AppScreenFormServiceInt<RPC, DataModel<DataSet>>, AutoCompleteServiceInt {

    private UTFResource openElisConstants= UTFResource.getBundle((String)SessionManager.getSession().getAttribute("locale"));
    
    private static final OrderMetaMap OrderMeta = new OrderMetaMap();
    
    private static final int leftTableRowsPerPage = 15;
    
    public DataModel<DataSet> commitQuery(Form form, DataModel<DataSet> model) throws RPCException {
        List orderIds;
        
        if(form == null){

            form = (Form)SessionManager.getSession().getAttribute("OrderQuery");
            StringField orderTypeField = new StringField();
            orderTypeField = (StringField)form.getField("orderType");
            String orderType = (String)orderTypeField.getValue();
            
            if(form == null)
                throw new QueryException(openElisConstants.getString("queryExpiredException"));

            HashMap<String,AbstractField> fields = form.getFieldMap();
            fields.remove("orderType");
            
            OrderRemote remote = (OrderRemote)EJBFactory.lookup("openelis/OrderBean/remote");
            try{
                orderIds = remote.query(fields, (model.getPage()*leftTableRowsPerPage), leftTableRowsPerPage+1, orderType);
            }catch(Exception e){
                //put order type back in the field map because we may need it later
                fields.put("orderType", orderTypeField);
                
                if(e instanceof LastPageException){
                    throw new LastPageException(openElisConstants.getString("lastPageException"));
                }else{
                    throw new RPCException(e.getMessage()); 
                }
            }    
            
            //put order type back in the field map because we may need it later
            fields.put("orderType", orderTypeField);
            
            //need to save the rpc used to the encache
            //SessionManager.getSession().setAttribute("OrderQuery", rpc);
        }else{
            OrderRemote remote = (OrderRemote)EJBFactory.lookup("openelis/OrderBean/remote");
            StringField orderTypeField = new StringField();
            orderTypeField = (StringField)form.getField("orderType");
            String orderType = (String)orderTypeField.getValue();
            
            
            HashMap<String,AbstractField> fields = form.getFieldMap();
            fields.remove("orderType");
            fields.remove("itemsTable");
            fields.remove("label1");
            fields.remove("item");
            fields.remove("receiptsTable");
            //FIXME we may need to put this back fields.remove(OrderMeta.getInventoryTransaction().getReceivedDate());

            try{    
                orderIds = remote.query(fields,0,leftTableRowsPerPage, orderType);
    
            }catch(Exception e){
                throw new RPCException(e.getMessage());
            }
    
            //put order type back in the field map because we may need it later
            fields.put("orderType", orderTypeField);
        
            //need to save the rpc used to the encache
            SessionManager.getSession().setAttribute("OrderQuery", form);
        }
        
        //fill the model with the query results
        int i=0;
        if(model == null)
            model = new DataModel<DataSet>();
        else
            model.clear();
        while(i < orderIds.size() && i < leftTableRowsPerPage) {
            IdNameDO resultDO = (IdNameDO)orderIds.get(i);
            model.add(new DataSet<Data>(new NumberObject(resultDO.getId()),new StringObject(resultDO.getName())));
            i++;
        } 
 
        return model;
    }

    public RPC commitAdd(RPC rpc) throws RPCException {
//      remote interface to call the order bean
        OrderRemote remote = (OrderRemote)EJBFactory.lookup("openelis/OrderBean/remote");
        OrderDO orderDO = new OrderDO();
        List orderItems = new ArrayList();
        NoteDO customerNotes = new NoteDO();
        NoteDO orderShippingNotes = new NoteDO();
        
        String orderType = (String)rpc.form.getFieldValue("orderType");
        
        //build the orderDO from the form
        orderDO = getOrderDOFromRPC(rpc.form);
        
        //set the isExternal flag
        if(OrderRemote.INTERNAL.equals(orderType))
            orderDO.setIsExternal("N");
        else if(OrderRemote.EXTERNAL.equals(orderType))
            orderDO.setIsExternal("Y");
        else if(OrderRemote.KITS.equals(orderType))
            orderDO.setIsExternal("N");
        
        //build customer notes do from form
        if(OrderRemote.KITS.equals(orderType)){
            customerNotes.setSubject("");
            customerNotes.setText((String)((Form)rpc.form.getField("custNote")).getFieldValue(OrderMeta.ORDER_CUSTOMER_NOTE_META.getText()));
            customerNotes.setIsExternal("Y");
        }
        
        //build order shipping do notes from form
        orderShippingNotes.setSubject("");
        orderShippingNotes.setText((String)((Form)rpc.form.getField("shippingNote")).getFieldValue(OrderMeta.ORDER_SHIPPING_NOTE_META.getText()));
        orderShippingNotes.setIsExternal("Y");
        
        //order items info
        TableField itemsTableField = (TableField)((Form)rpc.form.getField("items")).getField("itemsTable");
        DataModel itemsModel = (DataModel)itemsTableField.getValue();
        orderItems = getOrderItemsListFromRPC(itemsModel, orderDO.getId(), orderType);
                
        //validate the fields on the backend
        List exceptionList = remote.validateForAdd(orderDO, orderType, orderItems);
        
        if(exceptionList.size() > 0){
            setRpcErrors(exceptionList, itemsTableField, rpc.form);
            return rpc;
        } 
        
        //send the changes to the database
        Integer orderId;
        try{
            orderId = (Integer)remote.updateOrder(orderDO, orderType, orderItems, customerNotes, orderShippingNotes);
        }catch(Exception e){
            exceptionList = new ArrayList();
            exceptionList.add(e);
            
            setRpcErrors(exceptionList, itemsTableField, rpc.form);
            
            return rpc;
        }
        
        //lookup the changes from the database and build the rpc
        orderDO.setId(orderId);

        //set the fields in the RPC
        setFieldsInRPC(rpc.form, orderDO);

        return rpc;
    }

    public RPC commitUpdate(RPC rpc) throws RPCException {
        //remote interface to call the order bean
        OrderRemote remote = (OrderRemote)EJBFactory.lookup("openelis/OrderBean/remote");
        OrderDO orderDO = new OrderDO();
        List orderItems = new ArrayList();
        NoteDO customerNote = new NoteDO();
        NoteDO shippingNote = new NoteDO();
        Integer originalStatus = (Integer)rpc.form.getFieldValue("originalStatus");
        boolean qtyErrors = false;
        
        String orderType = (String)rpc.form.getFieldValue("orderType");

        //build the order DO from the form
        orderDO = getOrderDOFromRPC(rpc.form);
        
        //set the isExternal flag
        if(OrderRemote.INTERNAL.equals(orderType))
            orderDO.setIsExternal("N");
        else if(OrderRemote.EXTERNAL.equals(orderType))
            orderDO.setIsExternal("Y");
        else if(OrderRemote.KITS.equals(orderType))
            orderDO.setIsExternal("N");
        
        //build customer notes do from form (only for kit orders)
        if(OrderRemote.KITS.equals(orderType)){
            customerNote.setSubject("");
            customerNote.setText((String)((Form)rpc.form.getField("custNote")).getFieldValue(OrderMeta.ORDER_CUSTOMER_NOTE_META.getText()));
            customerNote.setIsExternal("Y");
        }
        
        //build order shipping do notes from form
        shippingNote.setSubject("");
        shippingNote.setText((String)((Form)rpc.form.getField("shippingNote")).getFieldValue(OrderMeta.ORDER_SHIPPING_NOTE_META.getText()));
        shippingNote.setIsExternal("Y");
        
        //items info
        TableField itemsTableField = (TableField)((Form)rpc.form.getField("items")).getField("itemsTable");
        DataModel itemsModel = (DataModel)itemsTableField.getValue();
        orderItems = getOrderItemsListFromRPC(itemsModel, orderDO.getId(), orderType);     
        
        //if the status changed we need to verify we have enough quantity to fill the order
        if((OrderRemote.INTERNAL.equals(orderType) || OrderRemote.KITS.equals(orderType)) && !originalStatus.equals(orderDO.getStatusId())){
            CategoryRemote catRemote = (CategoryRemote) EJBFactory.lookup("openelis/CategoryBean/remote");
            try {
                Integer completedStatusId = catRemote.getEntryIdForSystemName("order_status_processed");
                
                if(orderDO.getStatusId().equals(completedStatusId)){
                    //validate the quantities
                    List exceptionList = remote.validateQuantities(orderItems);
                    
                    if(exceptionList.size() > 0){
                        setRpcErrors(exceptionList, itemsTableField, rpc.form);
                        qtyErrors = true;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        
        //validate the fields on the backend
        List exceptionList = remote.validateForUpdate(orderDO, orderType, orderItems, ((Form)rpc.form.getField("items")).load);
        if(exceptionList.size() > 0 || qtyErrors){
            setRpcErrors(exceptionList, itemsTableField, rpc.form);
            
            return rpc;
        } 
        
        //send the changes to the database
        try{
            remote.updateOrder(orderDO, orderType, orderItems, customerNote, shippingNote);
        }catch(Exception e){
            if(e instanceof EntityLockedException)
                throw new RPCException(e.getMessage());
            
            exceptionList = new ArrayList();
            exceptionList.add(e);
            
            setRpcErrors(exceptionList, itemsTableField, rpc.form);
            
            return rpc;
        }

        //set the fields in the RPC
        setFieldsInRPC(rpc.form, orderDO);   
        
        return rpc;
    }

    public RPC commitDelete(RPC rpc) throws RPCException {
        return null;
    }

    public RPC abort(RPC rpc) throws RPCException {
        //remote interface to call the order bean
        OrderRemote remote = (OrderRemote)EJBFactory.lookup("openelis/OrderBean/remote");
        
        String orderType = (String)rpc.form.getFieldValue("orderType");
        StringObject orderTypeObj = new StringObject(orderType);
        
        OrderDO orderDO = remote.getOrderAndUnlock((Integer)((DataObject)((DataSet)rpc.key).getKey()).getValue(), orderType, SessionManager.getSession().getId());

        //set the fields in the RPC
        setFieldsInRPC(rpc.form, orderDO);
        
        if(((Form)rpc.form.getField("items")).load){
            Form itemsForm = (Form)rpc.form.getField("items");
            loadItems((DataSet)rpc.key, new BooleanObject(false), orderTypeObj, itemsForm);
        }
        
        if(rpc.form.getField("receipts") != null && ((Form)rpc.form.getField("receipts")).load){
            Form receipts = (Form)rpc.form.getField("receipts");
            loadReceipts((DataSet)rpc.key, orderTypeObj, receipts);
        }
        
        if(rpc.form.getField("shippingNote") != null && ((Form)rpc.form.getField("shippingNote")).load){
            Form shippingNoteRpc = (Form)rpc.form.getField("shippingNote");
            loadOrderShippingNotes((DataSet)rpc.key, shippingNoteRpc);
        }
        
        if(rpc.form.getField("reportToBillTo") != null && ((Form)rpc.form.getField("reportToBillTo")).load){
            Form reportToBillToRpc = (Form)rpc.form.getField("reportToBillTo");
            loadReportToBillTo((DataSet)rpc.key, reportToBillToRpc);
        }
        
        if(rpc.form.getField("custNote") != null && ((Form)rpc.form.getField("custNote")).load){
            Form custNoteRpc = (Form)rpc.form.getField("custNote");
            loadCustomerNotes((DataSet)rpc.key, custNoteRpc);
        }
        
        return rpc;  
    }

    public RPC fetch(RPC rpc) throws RPCException {
        //remote interface to call the order bean
        OrderRemote remote = (OrderRemote)EJBFactory.lookup("openelis/OrderBean/remote");
        
        String orderType = (String)rpc.form.getFieldValue("orderType");
        StringObject orderTypeObj = new StringObject(orderType);
        
        OrderDO orderDO = remote.getOrder((Integer)((DataObject)((DataSet)rpc.key).getKey()).getValue(), orderType);

        //set the fields in the RPC
        setFieldsInRPC(rpc.form, orderDO);
        
        String tab = (String)rpc.form.getFieldValue("orderTabPanel");
        if(tab.equals("itemsTab")){
            loadItems((DataSet)rpc.key, new BooleanObject(false), orderTypeObj, (Form)rpc.form.getField("items"));
        }
        if(tab.equals("receiptsTab")){
            loadReceipts((DataSet)rpc.key, orderTypeObj, (Form)rpc.form.getField("receipts"));
        }
        if(tab.equals("orderNotesTab")){
            loadOrderShippingNotes((DataSet)rpc.key, (Form)rpc.form.getField("shippingNote"));
        }
        if(tab.equals("customerNotesTab")){
            loadCustomerNotes((DataSet)rpc.key, (Form)rpc.form.getField("custNote"));
        }
        if(tab.equals("reportToBillToTab")){
            loadReportToBillTo((DataSet)rpc.key, (Form)rpc.form.getField("reportToBillTo"));
        }
        
        return rpc;
    }

    public RPC fetchForUpdate(RPC rpc) throws RPCException {
        //remote interface to call the order bean
        OrderRemote remote = (OrderRemote)EJBFactory.lookup("openelis/OrderBean/remote");
        
        String orderType = (String)rpc.form.getFieldValue("orderType");
        StringObject orderTypeObj = new StringObject(orderType);
        
        OrderDO orderDO = new OrderDO();
        try{
            orderDO = remote.getOrderAndLock((Integer)((DataObject)((DataSet)rpc.key).getKey()).getValue(), orderType, SessionManager.getSession().getId());
        }catch(Exception e){
            throw new RPCException(e.getMessage());
        }
        
        //set the fields in the RPC
        setFieldsInRPC(rpc.form, orderDO);
        
        String tab = (String)rpc.form.getFieldValue("orderTabPanel");
        if(tab.equals("itemsTab")){
            loadItems((DataSet)rpc.key, new BooleanObject(false), orderTypeObj, (Form)rpc.form.getField("items"));
        }
        if(tab.equals("receiptsTab")){
            loadReceipts((DataSet)rpc.key, orderTypeObj, (Form)rpc.form.getField("receipts"));
        }
        if(tab.equals("orderNotesTab")){
            loadOrderShippingNotes((DataSet)rpc.key, (Form)rpc.form.getField("shippingNote"));
        }
        if(tab.equals("customerNotesTab")){
            loadCustomerNotes((DataSet)rpc.key, (Form)rpc.form.getField("custNote"));
        }
        if(tab.equals("reportToBillToTab")){
            loadReportToBillTo((DataSet)rpc.key, (Form)rpc.form.getField("reportToBillTo"));
        }
        
        return rpc;  
    }

    public String getXML() throws RPCException {
        return null;
    }

    public HashMap getXMLData() throws RPCException {
        return null;
    }

    public HashMap getXMLData(HashMap args) throws RPCException {
        String action = (String)((StringObject)args.get("type")).getValue();
        boolean loaded = ((Boolean)((BooleanObject)args.get("loaded")).getValue()).booleanValue();
        HashMap returnMap = new HashMap();
        StringObject xmlString = new StringObject();
        
        if(OrderRemote.INTERNAL.equals(action))
            xmlString.setValue(ServiceUtils.getXML(Constants.APP_ROOT+"/Forms/internalOrder.xsl"));
        else if(OrderRemote.EXTERNAL.equals(action))
            xmlString.setValue(ServiceUtils.getXML(Constants.APP_ROOT+"/Forms/vendorOrder.xsl"));
        else if(OrderRemote.KITS.equals(action))
            xmlString.setValue(ServiceUtils.getXML(Constants.APP_ROOT+"/Forms/kitOrder.xsl"));
        
        returnMap.put("xml", xmlString);
        
        if(!loaded){        
            DataModel statusDropdownField = (DataModel)CachingManager.getElement("InitialData", "orderStatusDropdown");
            DataModel storeDropdownField = (DataModel)CachingManager.getElement("InitialData", "orderStoreDropdown");
            DataModel costCenterDropdownField = (DataModel)CachingManager.getElement("InitialData", "costCenterDropdown");
            DataModel shipFromDropdownField = (DataModel)CachingManager.getElement("InitialData", "shipFromDropdown");
            
            //status dropdown
            if(statusDropdownField == null){
                statusDropdownField = getInitialModel("status");
                CachingManager.putElement("InitialData", "orderStatusDropdown", statusDropdownField);
            }
            //store dropdown
            if(storeDropdownField == null){
                storeDropdownField = getInitialModel("store");
                CachingManager.putElement("InitialData", "orderStoreDropdown", storeDropdownField);
                }
            //cost center type dropdown
            if(costCenterDropdownField == null){
                costCenterDropdownField = getInitialModel("costCenter");
                CachingManager.putElement("InitialData", "costCenterDropdown", costCenterDropdownField);
            }
            //ship from dropdown
            if(shipFromDropdownField == null){
                shipFromDropdownField = getInitialModel("shipFrom");
                CachingManager.putElement("InitialData", "shipFromDropdown", shipFromDropdownField);
            }
            
            returnMap.put("status", statusDropdownField);
            returnMap.put("store", storeDropdownField);
            returnMap.put("costCenter", costCenterDropdownField);
            returnMap.put("shipFrom", shipFromDropdownField);    
        }
        
        return returnMap;
    }
    
    public RPC getScreen(RPC rpc) {
        return rpc;
    }
    
    public Form loadItems(DataSet key, BooleanObject forDuplicate, StringObject orderTypeObj, Form form) throws RPCException {
        getItemsModel((NumberObject)key.getKey(), forDuplicate, (TableField)form.getField("itemsTable"), orderTypeObj);
        form.load = true;
        return form;
    }

    public Form loadReceipts(DataSet key, StringObject orderTypeObj, Form form) throws RPCException {
        getReceiptsModel((NumberObject)key.getKey(), (TableField)form.getField("receiptsTable"), orderTypeObj);
        form.load = true;
        return form;
    }

    public Form loadOrderShippingNotes(DataSet key, Form form) throws RPCException {
        form.setFieldValue(OrderMeta.ORDER_SHIPPING_NOTE_META.getText(), getShippingNotes((NumberObject)key.getKey()));
        form.load = true;
        return form;
    }
    
    public Form loadCustomerNotes(DataSet key, Form form) throws RPCException {
        form.setFieldValue(OrderMeta.ORDER_CUSTOMER_NOTE_META.getText(), getCustomerNotes((NumberObject)key.getKey()));
        form.load = true;
        return form;
    }

    public Form loadReportToBillTo(DataSet key, Form form) throws RPCException {
        fillReportToBillToValues(form, getReportToBillTo((NumberObject)key.getKey()));
        form.load = true;
        return form;
    }
    
    private void setRpcErrors(List exceptionList, TableField orderItemsTable, Form  form){
        for (int i=0; i<exceptionList.size();i++) {
            if(exceptionList.get(i) instanceof TableFieldErrorException){
                int rowindex = ((TableFieldErrorException)exceptionList.get(i)).getRowIndex();
                orderItemsTable.getField(rowindex,((TableFieldErrorException)exceptionList.get(i)).getFieldName())
                    .addError(openElisConstants.getString(((FieldErrorException)exceptionList.get(i)).getMessage()));

            //if the error is on the field
            }else if(exceptionList.get(i) instanceof FieldErrorException)
                form.getField(((FieldErrorException)exceptionList.get(i)).getFieldName()).addError(openElisConstants.getString(((FieldErrorException)exceptionList.get(i)).getMessage()));
            
            //if the error is on the entire form
            else if(exceptionList.get(i) instanceof FormErrorException)
                form.addError(openElisConstants.getString(((FormErrorException)exceptionList.get(i)).getMessage()));
            }        
        
        form.status = Form.Status.invalid;
    }
    
    public DataModel getInitialModel(String cat){
        Integer id = null;
        CategoryRemote remote = (CategoryRemote)EJBFactory.lookup("openelis/CategoryBean/remote");
        
        if(cat.equals("status"))
            id = remote.getCategoryId("order_status");
        else if(cat.equals("store"))
            id = remote.getCategoryId("inventory_item_stores");
        else if(cat.equals("costCenter"))
            id = remote.getCategoryId("cost_centers");
        else if(cat.equals("shipFrom"))
            id = remote.getCategoryId("shipFrom");
        
        List entries = new ArrayList();
        if(id != null)
            entries = remote.getDropdownValues(id);
        
        //we need to build the model to return
        DataModel returnModel = new DataModel();

        if(entries.size() > 0){ 
            //create a blank entry to begin the list
            DataSet blankset = new DataSet();
            
            StringObject blankStringId = new StringObject();
            NumberObject blankNumberId = new NumberObject(NumberObject.Type.INTEGER);
            
            blankStringId.setValue("");
            blankset.add(blankStringId);
            
            blankNumberId.setValue(new Integer(0));
            blankset.setKey(blankNumberId);
            
            returnModel.add(blankset);
        }
        
        int i=0;
        while(i < entries.size()){
            DataSet set = new DataSet();
            IdNameDO resultDO = (IdNameDO) entries.get(i);
            //id
            Integer dropdownId = resultDO.getId();
            //entry
            String dropdownText = resultDO.getName();
            
            StringObject textObject = new StringObject();
            NumberObject numberId = new NumberObject(NumberObject.Type.INTEGER);
        
            textObject.setValue(dropdownText);
            set.add(textObject);
            
            numberId.setValue(dropdownId);
            set.setKey(numberId);
            
            returnModel.add(set);
            
            i++;
        }       
        
        return returnModel;
    }
    
    public DataModel getAddAutoFillValues() throws Exception {
        DataModel model = new DataModel();
        DataSet set = new DataSet();
        
        OrderRemote remote = (OrderRemote)EJBFactory.lookup("openelis/OrderBean/remote");
        OrderAddAutoFillDO autoDO;
        
        autoDO = remote.getAddAutoFillValues();
                
        DropDownField status = new DropDownField();
        StringField dateOrdered = new StringField();
        StringField requestedBy = new StringField();
        
        status.setValue(new DataSet(new NumberObject(autoDO.getStatus())));
        dateOrdered.setValue(autoDO.getOrderedDate().toString());
        requestedBy.setValue(autoDO.getRequestedBy());
        
        set.add(status);
        set.add(dateOrdered);
        set.add(requestedBy);
        
        model.add(set);
        
        return model;
    }

    public DataModel getMatches(String cat, DataModel model, String match, HashMap params) throws RPCException {
        if("organization".equals(cat))
            return getOrganizationMatches(match);
        else if("reportTo".equals(cat))
            return getOrganizationMatches(match);
        else if("billTo".equals(cat))
            return getOrganizationMatches(match);
        else if("inventoryItemWithStoreAndSubItems".equals(cat))        //internal order
            return getInventoryItemMatchesNoLoc(match, false, true);
        else if("inventoryItemWithStoreMain".equals(cat))               //vendor order
            return getInventoryItemMatchesNoLoc(match, true, true);
        else if("inventoryItemWithStoreAndLocMainStore".equals(cat))
            return getInventoryItemMatches(match, true, false);
        else if("inventoryItemWithStore".equals(cat))                   //kit order
            return getInventoryItemMatchesNoLoc(match, false, false);
        else if("orderDesc".equals(cat))
            return getOrderDescMatches(match);
        return null;
    }
    
    private DataModel getOrganizationMatches(String match){
        OrganizationRemote remote = (OrganizationRemote)EJBFactory.lookup("openelis/OrganizationBean/remote");
        DataModel dataModel = new DataModel();
        List autoCompleteList;
    
        try{
            int id = Integer.parseInt(match); //this will throw an exception if it isnt an id
            //lookup by id...should only bring back 1 result
            autoCompleteList = remote.autoCompleteLookupById(id);
            
        }catch(NumberFormatException e){
            //it isnt an id
            //lookup by name
            autoCompleteList = remote.autoCompleteLookupByName(match+"%", 10);
        }
        
        for(int i=0; i < autoCompleteList.size(); i++){
            OrganizationAutoDO resultDO = (OrganizationAutoDO) autoCompleteList.get(i);
            //org id
            Integer orgId = resultDO.getId();
            //org name
            String name = resultDO.getName();
            //org apt suite #
            String aptSuite = resultDO.getAptSuite();
            //org street address
            String address = resultDO.getAddress();
            //org city
            String city = resultDO.getCity();
            //org state
            String state = resultDO.getState();
            //org zipcode
            String zipCode = resultDO.getZipCode();
            
            DataSet data = new DataSet();
            //hidden id
            NumberObject idObject = new NumberObject(NumberObject.Type.INTEGER);
            idObject.setValue(orgId);
            data.setKey(idObject);
            //columns
            StringObject nameObject = new StringObject();
            nameObject.setValue(name);
            data.add(nameObject);
            StringObject addressObject = new StringObject();
            addressObject.setValue(address);
            data.add(addressObject);
            StringObject cityObject = new StringObject();
            cityObject.setValue(city);
            data.add(cityObject);
            StringObject stateObject = new StringObject();
            stateObject.setValue(state);
            data.add(stateObject);
            
            //hidden fields
            DataMap map = new DataMap();
            StringObject aptSuiteObj = new StringObject();
            aptSuiteObj.setValue(aptSuite);
            map.put("aptSuite", aptSuiteObj);
            StringObject zipCodeObj = new StringObject();
            zipCodeObj.setValue(zipCode);
            map.put("zipCode", zipCodeObj);
            
            data.setData(map);
            
            //add the dataset to the datamodel
            dataModel.add(data);                            
        }       
        
        return dataModel;           
    }
    
    private DataModel getInventoryItemMatchesNoLoc(String match, boolean limitToMainStore, boolean allowSubAssembly){
        InventoryItemRemote remote = (InventoryItemRemote)EJBFactory.lookup("openelis/InventoryItemBean/remote");
        DataModel dataModel = new DataModel();
        List autoCompleteList;

        String parsedMatch = match.replace('*', '%');
        
        autoCompleteList = remote.inventoryItemStoreAutoCompleteLookupByName(parsedMatch+"%", 10, limitToMainStore, allowSubAssembly);
        
        for(int i=0; i < autoCompleteList.size(); i++){
            InventoryItemAutoDO resultDO = (InventoryItemAutoDO) autoCompleteList.get(i);
            
            Integer itemId = resultDO.getId();
            String name = resultDO.getName();
            String store = resultDO.getStore();
            String dispensedUnits = resultDO.getDispensedUnits();
            
            DataSet data = new DataSet();
            //hidden id
            NumberObject idObject = new NumberObject(NumberObject.Type.INTEGER);
            idObject.setValue(itemId);
            data.setKey(idObject);
            //columns
            StringObject nameObject = new StringObject();
            nameObject.setValue(name);
            data.add(nameObject);
            StringObject storeObject = new StringObject();
            storeObject.setValue(store);
            data.add(storeObject);
            StringObject disUnitsObj = new StringObject();
            disUnitsObj.setValue(dispensedUnits);
            data.add(disUnitsObj);
                        
            //add the dataset to the datamodel
            dataModel.add(data);                            
        }       
        
        return dataModel;           
    }
    
    private DataModel getInventoryItemMatches(String match, boolean limitToMainStore, boolean allowSubAssembly){
        InventoryItemRemote remote = (InventoryItemRemote)EJBFactory.lookup("openelis/InventoryItemBean/remote");
        DataModel dataModel = new DataModel();
        List autoCompleteList;
        
        String parsedMatch = match.replace('*', '%');
        
        autoCompleteList = remote.inventoryItemStoreLocAutoCompleteLookupByName(parsedMatch+"%", 10, limitToMainStore, allowSubAssembly);
        
        for(int i=0; i < autoCompleteList.size(); i++){
            InventoryItemAutoDO resultDO = (InventoryItemAutoDO) autoCompleteList.get(i);
            
            Integer itemId = resultDO.getId();
            String name = resultDO.getName();
            String store = resultDO.getStore();
            Integer locationId = resultDO.getLocationId();
            String location = resultDO.getLocation();
            String lotNum = resultDO.getLotNum();
            Datetime expDateTime = resultDO.getExpDate();
            
            String expDate = null;
            if(expDateTime.getDate() != null)
                expDate = expDateTime.toString();
            
            Integer qty = resultDO.getQuantityOnHand();
            
            DataSet data = new DataSet();
            //hidden id
            NumberObject idObject = new NumberObject(NumberObject.Type.INTEGER);
            idObject.setValue(itemId);
            data.setKey(idObject);
            //columns
            StringObject nameObject = new StringObject();
            nameObject.setValue(name);
            data.add(nameObject);
            StringObject storeObject = new StringObject();
            storeObject.setValue(store);
            data.add(storeObject);

            StringObject locationObject = new StringObject();
            locationObject.setValue(location);
            data.add(locationObject);
            StringObject lotNumObj = new StringObject();
            lotNumObj.setValue(lotNum);
            data.add(lotNumObj);
            StringObject expDateObj = new StringObject();
            expDateObj.setValue(expDate);
            data.add(expDateObj);
            NumberObject qtyObject = new NumberObject(NumberObject.Type.INTEGER);
            qtyObject.setValue(qty);
            data.add(qtyObject);
            
            DataMap map = new DataMap();
            NumberObject locIdObject = new NumberObject(NumberObject.Type.INTEGER);
            locIdObject.setValue(locationId);
            map.put("locId", locIdObject);
            data.setData(map);
                       
            //add the dataset to the datamodel
            dataModel.add(data);                            
        }       
        
        return dataModel;           
    }
    
    private DataModel getOrderDescMatches(String match){
        OrderRemote remote = (OrderRemote)EJBFactory.lookup("openelis/OrderBean/remote");
        DataModel dataModel = new DataModel();
        List autoCompleteList;
        
        String parsedMatch = match.replace('*', '%');
        
        autoCompleteList = remote.orderDescriptionAutoCompleteLookup(parsedMatch+"%", 10);
        
        if(autoCompleteList.size() == 0){
            DataSet data = new DataSet();
            //string value
            StringObject idObject = new StringObject();
            idObject.setValue(match);
            data.setKey(idObject);
            //columns
            StringObject nameObject = new StringObject();
            nameObject.setValue(match);
            data.add(nameObject);
            
            //add the dataset to the datamodel
            dataModel.add(data);    
        }else{
            for(int i=0; i < autoCompleteList.size(); i++){
                IdNameDO resultDO = (IdNameDO) autoCompleteList.get(i);
                
                String name = resultDO.getName();
                
                DataSet data = new DataSet();
                StringObject idObject = new StringObject(name);
                data.setKey(idObject);
                
                //columns
                StringObject nameObject = new StringObject();
                nameObject.setValue(name);
                data.add(nameObject);

                //add the dataset to the datamodel
                dataModel.add(data);                            
            }       
        }                                 
    
        return dataModel;           
    }
    
    private OrderDO getOrderDOFromRPC(Form form){
        OrderDO orderDO = new OrderDO();
        
        orderDO.setId((Integer) form.getFieldValue(OrderMeta.getId()));
        orderDO.setNeededInDays((Integer) form.getFieldValue(OrderMeta.getNeededInDays()));
        orderDO.setStatusId((Integer)((DropDownField)form.getField(OrderMeta.getStatusId())).getSelectedKey());
        orderDO.setOrderedDate(new Datetime(Datetime.YEAR, Datetime.DAY, (String)form.getFieldValue(OrderMeta.getOrderedDate())).getDate());
        orderDO.setRequestedBy((String)form.getFieldValue(OrderMeta.getRequestedBy()));
        orderDO.setCostCenter((Integer)((DropDownField)form.getField(OrderMeta.getCostCenterId())).getSelectedKey());
        
        if(form.getField(OrderMeta.getShipFromId()) != null)
            orderDO.setShipFromId((Integer)((DropDownField)form.getField(OrderMeta.getShipFromId())).getSelectedKey());
        
        if(form.getField(OrderMeta.getExternalOrderNumber()) != null)
            orderDO.setExternalOrderNumber((String)form.getFieldValue(OrderMeta.getExternalOrderNumber()));
        
        //set org values
        if(form.getField(OrderMeta.ORDER_ORGANIZATION_META.getName()) != null){
            orderDO.setOrganization((String)((DropDownField)form.getField(OrderMeta.ORDER_ORGANIZATION_META.getName())).getTextValue());
            orderDO.setOrganizationId((Integer)((DropDownField)form.getField(OrderMeta.ORDER_ORGANIZATION_META.getName())).getSelectedKey());
            orderDO.organizationAddressDO.setMultipleUnit((String)form.getFieldValue(OrderMeta.ORDER_ORGANIZATION_META.ADDRESS.getMultipleUnit()));
            orderDO.organizationAddressDO.setStreetAddress((String)form.getFieldValue(OrderMeta.ORDER_ORGANIZATION_META.ADDRESS.getStreetAddress()));
            orderDO.organizationAddressDO.setCity((String)form.getFieldValue(OrderMeta.ORDER_ORGANIZATION_META.ADDRESS.getCity()));
            orderDO.organizationAddressDO.setState((String)form.getFieldValue(OrderMeta.ORDER_ORGANIZATION_META.ADDRESS.getState()));
            orderDO.organizationAddressDO.setZipCode((String)form.getFieldValue(OrderMeta.ORDER_ORGANIZATION_META.ADDRESS.getZipCode()));
        }
        
        if(form.getField("reportToBillTo") != null){
            Form reportToBillToRPC = (Form)form.getField("reportToBillTo");
            orderDO.setReportTo((String)((DropDownField)reportToBillToRPC.getField(OrderMeta.ORDER_REPORT_TO_META.getName())).getTextValue());
            orderDO.setReportToId((Integer)((DropDownField)reportToBillToRPC.getField(OrderMeta.ORDER_REPORT_TO_META.getName())).getSelectedKey());
            orderDO.setBillTo((String)((DropDownField)reportToBillToRPC.getField(OrderMeta.ORDER_BILL_TO_META.getName())).getTextValue());
            orderDO.setBillToId((Integer)((DropDownField)reportToBillToRPC.getField(OrderMeta.ORDER_BILL_TO_META.getName())).getSelectedKey());
        }
        
        //set description
        if(form.getField(OrderMeta.getDescription()) != null)
            orderDO.setDescription((String)((DropDownField)form.getField(OrderMeta.getDescription())).getSelectedKey());
        
        return orderDO;
    }
    
    private void setFieldsInRPC(Form form, OrderDO orderDO){
        form.setFieldValue(OrderMeta.getId(), orderDO.getId());
        form.setFieldValue(OrderMeta.getNeededInDays(), orderDO.getNeededInDays());
        form.setFieldValue(OrderMeta.getStatusId(), new DataSet(new NumberObject(orderDO.getStatusId())));
        form.setFieldValue(OrderMeta.getOrderedDate(), orderDO.getOrderedDate().toString());
        form.setFieldValue(OrderMeta.getRequestedBy(), orderDO.getRequestedBy());
        
        if(orderDO.getCostCenter() != null)
            form.setFieldValue(OrderMeta.getCostCenterId(), new DataSet(new NumberObject(orderDO.getCostCenter())));
        
        if(orderDO.getExternalOrderNumber() != null)
            form.setFieldValue(OrderMeta.getExternalOrderNumber(), orderDO.getExternalOrderNumber());
        
        if(form.getField(OrderMeta.getShipFromId()) != null && orderDO.getShipFromId() != null)
            form.setFieldValue(OrderMeta.getShipFromId(), new DataSet(new NumberObject(orderDO.getShipFromId())));
        
        //create dataset for organization auto complete
        if(form.getField(OrderMeta.ORDER_ORGANIZATION_META.getName()) != null){
            if(orderDO.getOrganizationId() == null)
                form.setFieldValue(OrderMeta.ORDER_ORGANIZATION_META.getName(), null);
            else{
                DataModel orgModel = new DataModel();
                DataSet orgSet = new DataSet();
                
                orgSet.setKey(new NumberObject(orderDO.getOrganizationId()));
                //columns
                orgSet.add(new StringObject(orderDO.getOrganization()));
                orgSet.add(new StringObject(orderDO.organizationAddressDO.getStreetAddress()));
                orgSet.add(new StringObject(orderDO.organizationAddressDO.getCity()));
                orgSet.add(new StringObject(orderDO.organizationAddressDO.getState()));
                                
                //hidden fields
                DataMap map = new DataMap();
                map.put("aptSuite", new StringObject(orderDO.organizationAddressDO.getMultipleUnit()));
                map.put("zipCode", new StringObject(orderDO.organizationAddressDO.getZipCode()));
                orgSet.setData(map);
                
                orgModel.add(orgSet);

                ((DropDownField)form.getField(OrderMeta.ORDER_ORGANIZATION_META.getName())).setModel(orgModel);
                form.setFieldValue(OrderMeta.ORDER_ORGANIZATION_META.getName(), orgModel.get(0));
            }
            form.setFieldValue(OrderMeta.ORDER_ORGANIZATION_META.ADDRESS.getMultipleUnit(), orderDO.organizationAddressDO.getMultipleUnit());
            form.setFieldValue(OrderMeta.ORDER_ORGANIZATION_META.ADDRESS.getStreetAddress(),orderDO.organizationAddressDO.getStreetAddress());
            form.setFieldValue(OrderMeta.ORDER_ORGANIZATION_META.ADDRESS.getCity(), orderDO.organizationAddressDO.getCity());
            form.setFieldValue(OrderMeta.ORDER_ORGANIZATION_META.ADDRESS.getState(),orderDO.organizationAddressDO.getState());
            form.setFieldValue(OrderMeta.ORDER_ORGANIZATION_META.ADDRESS.getZipCode(), orderDO.organizationAddressDO.getZipCode());
        }
        
        //create dataset for description autocomplete
        if(form.getField(OrderMeta.getDescription()) != null){
            if(orderDO.getDescription() == null)
                form.setFieldValue(OrderMeta.getDescription(), null);
            else{
                DataModel descModel = new DataModel();
                descModel.add(new StringObject(orderDO.getDescription()),new StringObject(orderDO.getDescription()));
                ((DropDownField)form.getField(OrderMeta.getDescription())).setModel(descModel);
                form.setFieldValue(OrderMeta.getDescription(), descModel.get(0));
            }
        }
    }
    
    private List getOrderItemsListFromRPC(DataModel<DataSet> itemsTable, Integer orderId, String orderType){
        List orderItems = new ArrayList();
        List deletedRows = itemsTable.getDeletions();
        
        for(int i=0; i<itemsTable.size(); i++){
            OrderItemDO orderItemDO = new OrderItemDO();
            DataSet<Data> row = itemsTable.get(i);
            //contact data
            NumberObject itemId = (NumberObject)row.getKey();
            DataMap map = (DataMap)row.getData();
            NumberField locationId = null;
            NumberField qtyOnHand = null;
            NumberField inventoryTransactionId = null;
            if(map != null){
                locationId = (NumberField)map.get("locationId");
                qtyOnHand = (NumberField)map.get("qtyOnHand");
                inventoryTransactionId = (NumberField)map.get("transactionId");
            }

            if(itemId != null)
                orderItemDO.setId((Integer)itemId.getValue());
            orderItemDO.setOrder(orderId);
            orderItemDO.setInventoryItemId((Integer)((DropDownField)row.get(1)).getSelectedKey());
            orderItemDO.setQuantity((Integer)row.get(0).getValue());
            
            if(row.size() == 5){
                orderItemDO.setUnitCost((Double)row.get(3).getValue());
                orderItemDO.setCatalogNumber((String)row.get(4).getValue());
            }
            
            if(locationId != null)
                orderItemDO.setLocationId((Integer)locationId.getValue());
            
            if(qtyOnHand != null)
                orderItemDO.setQuantityOnHand((Integer)qtyOnHand.getValue());
            
            if(inventoryTransactionId != null)
                orderItemDO.setTransactionId((Integer)inventoryTransactionId.getValue());
            
            orderItems.add(orderItemDO);    
        }
        
        for(int j=0; j<deletedRows.size(); j++){
            DataSet deletedRow = (DataSet)deletedRows.get(j);
            if(deletedRow.getKey() != null){
                OrderItemDO itemDO = new OrderItemDO();
                itemDO.setDelete(true);
                itemDO.setId((Integer)((NumberObject)deletedRow.getKey()).getValue());
                
                orderItems.add(itemDO);
            }
        }
        
        return orderItems;
    }
    
    public void getItemsModel(NumberObject orderId, BooleanObject forDuplicate, TableField model, StringObject orderTypeObj){
        OrderRemote remote = (OrderRemote)EJBFactory.lookup("openelis/OrderBean/remote");
        //boolean withLocation = false;
        String orderType = (String)orderTypeObj.getValue();
        
        //if(orderType.equals(OrderRemote.INTERNAL) || orderType.equals(OrderRemote.KITS))
        //    withLocation = true;
        
        List itemsList = remote.getOrderItems((Integer)orderId.getValue());
        
        model.setValue(fillOrderItemsTable((DataModel)model.getValue(),itemsList, ((Boolean)forDuplicate.getValue()).booleanValue()));
    }
    
    public void getReceiptsModel(NumberObject orderId, TableField model, StringObject orderTypeObj){
        OrderRemote remote = (OrderRemote)EJBFactory.lookup("openelis/OrderBean/remote");
        
        String orderType = (String)orderTypeObj.getValue();
        
        List receiptsList = new ArrayList();
        if(orderType.equals(OrderRemote.EXTERNAL))
            receiptsList = remote.getOrderReceipts((Integer)orderId.getValue());
        else
            receiptsList = remote.getOrderLocTransactions((Integer)orderId.getValue());
        
        model.setValue(fillReceiptsTable((DataModel)model.getValue(),receiptsList, orderType));
    }
    
    public String getCustomerNotes(NumberObject orderId){
        OrderRemote remote = (OrderRemote)EJBFactory.lookup("openelis/OrderBean/remote");
        
        NoteDO noteDO = remote.getCustomerNote((Integer)orderId.getValue());
        
        if(noteDO != null)
            return noteDO.getText();
        
        return null;
    }
    
    public String getShippingNotes(NumberObject orderId){
        OrderRemote remote = (OrderRemote)EJBFactory.lookup("openelis/OrderBean/remote");
        
        NoteDO noteDO = remote.getOrderShippingNote((Integer)orderId.getValue());
        
        if(noteDO != null)
            return noteDO.getText();
        
        return null;
    }
    
    public DataModel getReportToBillTo(NumberObject orderId){
        OrderRemote remote = (OrderRemote)EJBFactory.lookup("openelis/OrderBean/remote");
        
        BillToReportToDO billToReportToDO = remote.getBillToReportTo((Integer)orderId.getValue());
        
        if(billToReportToDO != null){
            //datamodel
            DataModel model = new DataModel();
            DataSet set = new DataSet();
            NumberObject billToId = new NumberObject(NumberObject.Type.INTEGER);
            StringObject billTo = new StringObject();
            StringObject billToMultUnit = new StringObject();
            StringObject billToStreetAddress = new StringObject();
            StringObject billToCity = new StringObject();
            StringObject billToState = new StringObject();
            StringObject billToZipCode = new StringObject();
            
            
            NumberObject reportToId = new NumberObject(NumberObject.Type.INTEGER);
            StringObject reportTo = new StringObject();
            StringObject reportToMultUnit = new StringObject();
            StringObject reportToStreetAddress = new StringObject();
            StringObject reportToCity = new StringObject();
            StringObject reportToState = new StringObject();
            StringObject reportToZipCode = new StringObject();
            
            billToId.setValue(billToReportToDO.getBillToId());
            billTo.setValue(billToReportToDO.getBillTo());
            billToMultUnit.setValue(billToReportToDO.billToAddress.getMultipleUnit());
            billToStreetAddress.setValue(billToReportToDO.billToAddress.getStreetAddress());
            billToCity.setValue(billToReportToDO.billToAddress.getCity());
            billToState.setValue(billToReportToDO.billToAddress.getState());
            billToZipCode.setValue(billToReportToDO.billToAddress.getZipCode());
            
            reportToId.setValue(billToReportToDO.getReportToId());
            reportTo.setValue(billToReportToDO.getReportTo());
            reportToMultUnit.setValue(billToReportToDO.reportToAddress.getMultipleUnit());
            reportToStreetAddress.setValue(billToReportToDO.reportToAddress.getStreetAddress());
            reportToCity.setValue(billToReportToDO.reportToAddress.getCity());
            reportToState.setValue(billToReportToDO.reportToAddress.getState());
            reportToZipCode.setValue(billToReportToDO.reportToAddress.getZipCode());
            
            set.add(billToId);
            set.add(billTo);
            set.add(billToMultUnit);
            set.add(billToStreetAddress);
            set.add(billToCity);
            set.add(billToState);
            set.add(billToZipCode);
            
            set.add(reportToId);
            set.add(reportTo);
            set.add(reportToMultUnit);
            set.add(reportToStreetAddress);
            set.add(reportToCity);
            set.add(reportToState);
            set.add(reportToZipCode);
            
            model.add(set);
            
            /*reportToAutoset.setKey(reportToId);
            reportToAutoset.add(reportTo);
            model.add(reportToAutoset);
            
            billToAutoSet.setKey(billToId);
            billToAutoSet.add(billTo);
            model.add(billToAutoSet);*/
            
            return model;
        }
        
        return null;
    }
    
    public void fillReportToBillToValues(Form form, DataModel<DataSet> model){
        if(model == null)
            return;
        
        DataSet set = model.get(0);
        
        //bill to values
        DataModel billToModel = new DataModel();
        billToModel.add((NumberObject)set.get(0),(StringObject)set.get(1));
        ((DropDownField)form.getField(OrderMeta.ORDER_BILL_TO_META.getName())).setModel(billToModel);
        form.setFieldValue(OrderMeta.ORDER_BILL_TO_META.getName(), billToModel.get(0));
        
        form.setFieldValue(OrderMeta.ORDER_BILL_TO_META.ADDRESS.getMultipleUnit(), (String)((StringObject)set.get(2)).getValue());
        form.setFieldValue(OrderMeta.ORDER_BILL_TO_META.ADDRESS.getStreetAddress(), (String)((StringObject)set.get(3)).getValue());
        form.setFieldValue(OrderMeta.ORDER_BILL_TO_META.ADDRESS.getCity(), (String)((StringObject)set.get(4)).getValue());
        form.setFieldValue(OrderMeta.ORDER_BILL_TO_META.ADDRESS.getState(), (String)((StringObject)set.get(5)).getValue());
        form.setFieldValue(OrderMeta.ORDER_BILL_TO_META.ADDRESS.getZipCode(), (String)((StringObject)set.get(6)).getValue());
        
        //report to values
        DataModel reportToModel = new DataModel();
        reportToModel.add((NumberObject)set.get(7),(StringObject)set.get(8));
        ((DropDownField)form.getField(OrderMeta.ORDER_REPORT_TO_META.getName())).setModel(reportToModel);
        form.setFieldValue(OrderMeta.ORDER_REPORT_TO_META.getName(), reportToModel.get(0));
        
        form.setFieldValue(OrderMeta.ORDER_REPORT_TO_META.ADDRESS.getMultipleUnit(), (String)((StringObject)set.get(9)).getValue());
        form.setFieldValue(OrderMeta.ORDER_REPORT_TO_META.ADDRESS.getStreetAddress(), (String)((StringObject)set.get(10)).getValue());
        form.setFieldValue(OrderMeta.ORDER_REPORT_TO_META.ADDRESS.getCity(), (String)((StringObject)set.get(11)).getValue());
        form.setFieldValue(OrderMeta.ORDER_REPORT_TO_META.ADDRESS.getState(), (String)((StringObject)set.get(12)).getValue());
        form.setFieldValue(OrderMeta.ORDER_REPORT_TO_META.ADDRESS.getZipCode(), (String)((StringObject)set.get(13)).getValue());
    }
    
    public DataModel fillOrderItemsTable(DataModel<DataSet> orderItemsModel, List orderItemsList, boolean forDuplicate){
        try 
        {
            orderItemsModel.clear();
            
            for(int iter = 0;iter < orderItemsList.size();iter++) {
                OrderItemDO orderItemRow = (OrderItemDO)orderItemsList.get(iter);
    
                   DataSet<Data> row = orderItemsModel.createNewSet();
                   NumberObject id = new NumberObject(orderItemRow.getId());
                   NumberField locationId = new NumberField(orderItemRow.getLocationId());
                   NumberField qtyOnHand = new NumberField(orderItemRow.getQuantityOnHand());
                   NumberField inventoryTransactionId = new NumberField(orderItemRow.getTransactionId());
                   
                   id.setValue(orderItemRow.getId());
                   locationId.setValue(orderItemRow.getLocationId());
                   inventoryTransactionId.setValue(orderItemRow.getTransactionId());
                   
                   DataMap map = new DataMap();
                   
                    if(orderItemRow.getId() != null && !forDuplicate)
                        row.setKey(id);
                    
                    //if(orderItemRow.getLocationId() != null)
                    //    map.put("locationId", locationId);
                    
                    if(orderItemRow.getQuantityOnHand() != null)
                        map.put("qtyOnHand", qtyOnHand);
                    
                    //if(orderItemRow.getTransactionId() != null && !forDuplicate)
                    //    map.put("transactionId", inventoryTransactionId);
                    
                    row.setData(map);
                    row.get(0).setValue(orderItemRow.getQuantity());
                    
                    if(orderItemRow.getInventoryItemId() == null)
                        row.get(1).setValue(null);
                     else{
                         DataModel invItemModel = new DataModel();
                         invItemModel.add(new NumberObject(orderItemRow.getInventoryItemId()),new StringObject(orderItemRow.getInventoryItem()));
                         ((DropDownField)row.get(1)).setModel(invItemModel);
                         row.get(1).setValue(invItemModel.get(0));
                     }
                    
                    row.get(2).setValue(orderItemRow.getStore());
                    
                    if(row.size() == 5){
                        row.get(3).setValue(orderItemRow.getUnitCost());
                        row.get(4).setValue(orderItemRow.getCatalogNumber());
                    }
                    
                    orderItemsModel.add(row);
           } 
            
        } catch (Exception e) {
    
            e.printStackTrace();
            return null;
        }       
        
        return orderItemsModel;
    }
    
    public DataModel fillReceiptsTable(DataModel<DataSet> receiptsModel, List receiptsList, String orderType){
        try 
        {
            receiptsModel.clear();
            
            for(int iter = 0;iter < receiptsList.size();iter++) {
                if(orderType.equals(OrderRemote.EXTERNAL)){
                    InventoryReceiptDO receiptRow = (InventoryReceiptDO)receiptsList.get(iter);
    
                    DataSet<Data> row = receiptsModel.createNewSet();
                    
                    row.get(0).setValue(receiptRow.getReceivedDate().toString());
                    row.get(1).setValue(receiptRow.getInventoryItem());
                    row.get(2).setValue(receiptRow.getUpc());
                    row.get(3).setValue(receiptRow.getQuantityReceived());
                    row.get(4).setValue(receiptRow.getUnitCost());
                    row.get(5).setValue(receiptRow.getExternalReference());
                    
                    receiptsModel.add(row);
                }else{
                    InventoryLocationDO locRow = (InventoryLocationDO)receiptsList.get(iter);
                    
                    DataSet<Data> row = receiptsModel.createNewSet();
                    
                    row.get(0).setValue(locRow.getInventoryItem());                    
                    row.get(1).setValue(locRow.getStorageLocation());
                    row.get(2).setValue(locRow.getQuantityOnHand());
                    row.get(3).setValue(locRow.getLotNumber());
                    if(locRow.getExpirationDate() != null && locRow.getExpirationDate().getDate() != null)
                        row.get(4).setValue(locRow.getExpirationDate().toString());
                    
                    receiptsModel.add(row);
                }
           } 
            
        } catch (Exception e) {
    
            e.printStackTrace();
            return null;
        }       
        
        return receiptsModel;
    }
}