
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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
import org.openelis.gwt.common.RPCException;
import org.openelis.gwt.common.TableFieldErrorException;
import org.openelis.gwt.common.data.AbstractField;
import org.openelis.gwt.common.data.DataModel;
import org.openelis.gwt.common.data.DataSet;
import org.openelis.gwt.common.data.DropDownField;
import org.openelis.gwt.common.data.FieldType;
import org.openelis.gwt.common.data.IntegerObject;
import org.openelis.gwt.common.data.StringField;
import org.openelis.gwt.common.data.StringObject;
import org.openelis.gwt.common.data.TableField;
import org.openelis.gwt.server.ServiceUtils;
import org.openelis.gwt.services.AppScreenFormServiceInt;
import org.openelis.gwt.services.AutoCompleteServiceInt;
import org.openelis.metamap.OrderMetaMap;
import org.openelis.modules.order.client.ItemsForm;
import org.openelis.modules.order.client.OrderForm;
import org.openelis.modules.order.client.OrderItemRPC;
import org.openelis.modules.order.client.OrderNoteForm;
import org.openelis.modules.order.client.OrderNoteRPC;
import org.openelis.modules.order.client.OrderOrgKey;
import org.openelis.modules.order.client.OrderRPC;
import org.openelis.modules.order.client.OrderReceiptRPC;
import org.openelis.modules.order.client.OrderShippingNoteForm;
import org.openelis.modules.order.client.OrderShippingNoteRPC;
import org.openelis.modules.order.client.ReceiptForm;
import org.openelis.modules.order.client.ReportToBillToForm;
import org.openelis.modules.order.client.ReportToBillToRpc;
import org.openelis.persistence.EJBFactory;
import org.openelis.remote.CategoryRemote;
import org.openelis.remote.InventoryItemRemote;
import org.openelis.remote.OrderRemote;
import org.openelis.remote.OrganizationRemote;
import org.openelis.server.constants.Constants;
import org.openelis.server.handlers.CostCentersCacheHandler;
import org.openelis.server.handlers.OrderStatusCacheHandler;
import org.openelis.server.handlers.ShipFromCacheHandler;
import org.openelis.util.Datetime;
import org.openelis.util.SessionManager;
import org.openelis.util.UTFResource;

public class OrderService implements AppScreenFormServiceInt<OrderRPC, Integer>, AutoCompleteServiceInt {

    private UTFResource openElisConstants= UTFResource.getBundle((String)SessionManager.getSession().getAttribute("locale"));
    
    private static final OrderMetaMap OrderMeta = new OrderMetaMap();
    
    private static final int leftTableRowsPerPage = 15;
    
    public DataModel<Integer> commitQuery(Form form, DataModel<Integer> model) throws RPCException {
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
            model = new DataModel<Integer>();
        else
            model.clear();
        while(i < orderIds.size() && i < leftTableRowsPerPage) {
            IdNameDO resultDO = (IdNameDO)orderIds.get(i);
            model.add(new DataSet<Integer>(resultDO.getId(),new StringObject(resultDO.getName())));
            i++;
        } 
 
        return model;
    }

    public OrderRPC commitAdd(OrderRPC rpc) throws RPCException {
//      remote interface to call the order bean
        OrderRemote remote = (OrderRemote)EJBFactory.lookup("openelis/OrderBean/remote");
        OrderDO orderDO = new OrderDO();
        List orderItems = new ArrayList();
        NoteDO customerNotes = new NoteDO();
        NoteDO orderShippingNotes = new NoteDO();
        
        String orderType = rpc.orderType;
        
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
            customerNotes.setText((String)rpc.form.customerNotes.text.getValue());
            customerNotes.setIsExternal("Y");
        }
        
        //build order shipping do notes from form
        orderShippingNotes.setSubject("");
        orderShippingNotes.setText((String)rpc.form.shippingNotes.text.getValue());
        orderShippingNotes.setIsExternal("Y");
        
        //order items info
        TableField<Integer> itemsTableField = rpc.form.items.itemsTable;
        DataModel<Integer> itemsModel = (DataModel<Integer>)itemsTableField.getValue();
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

    public OrderRPC commitUpdate(OrderRPC rpc) throws RPCException {
        //remote interface to call the order bean
        OrderRemote remote = (OrderRemote)EJBFactory.lookup("openelis/OrderBean/remote");
        OrderDO orderDO = new OrderDO();
        List orderItems = new ArrayList();
        NoteDO customerNote = new NoteDO();
        NoteDO shippingNote = new NoteDO();

        Integer originalStatus = rpc.originalStatus;
        boolean qtyErrors = false;
        
        String orderType = rpc.orderType;

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
            customerNote.setText(rpc.form.customerNotes.text.getValue());
            customerNote.setIsExternal("Y");
        }
        
        //build order shipping do notes from form
        shippingNote.setSubject("");
        shippingNote.setText(rpc.form.shippingNotes.text.getValue());
        shippingNote.setIsExternal("Y");
        
        //items info
        TableField<Integer> itemsTableField = rpc.form.items.itemsTable;
        DataModel<Integer> itemsModel = (DataModel<Integer>)itemsTableField.getValue();
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
        List exceptionList = remote.validateForUpdate(orderDO, orderType, orderItems, rpc.form.items.load);
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

    public OrderRPC commitDelete(OrderRPC rpc) throws RPCException {
        return null;
    }

    public OrderRPC abort(OrderRPC rpc) throws RPCException {
        //remote interface to call the order bean
        OrderRemote remote = (OrderRemote)EJBFactory.lookup("openelis/OrderBean/remote");
        
        String orderType = rpc.orderType;
        //StringObject orderTypeObj = new StringObject(orderType);
        
        OrderDO orderDO = remote.getOrderAndUnlock(rpc.key, orderType, SessionManager.getSession().getId());

        //set the fields in the RPC
        setFieldsInRPC(rpc.form, orderDO);
        
        if(rpc.form.items.load)
            loadItemsForm(rpc.key, false, rpc.form.items);
        
        if(rpc.form.receipts.load)
            loadReceiptsForm(rpc.key, orderType, rpc.form.receipts);
        
        if(rpc.form.shippingNotes.load)
            loadOrderShippingNotesForm(rpc.key, rpc.form.shippingNotes);
        
        if(rpc.form.reportToBillTo.load)
            loadReportToBillToForm(rpc.key, rpc.form.reportToBillTo);
        
        if(rpc.form.customerNotes.load)
            loadCustomerNotesForm(rpc.key, rpc.form.customerNotes);
        
        return rpc;  
    }

    public OrderRPC fetch(OrderRPC rpc) throws RPCException {
        /*
         * Call checkModels to make screen has most recent versions of dropdowns
         */
        checkModels(rpc);
        
        //remote interface to call the order bean
        OrderRemote remote = (OrderRemote)EJBFactory.lookup("openelis/OrderBean/remote");
        
        String orderType = rpc.orderType;
        //StringObject orderTypeObj = new StringObject(orderType);
        
        OrderDO orderDO = remote.getOrder(rpc.key, orderType);

        //set the fields in the RPC
        setFieldsInRPC(rpc.form, orderDO);
        
        String tab = rpc.form.orderTabPanel;
        if(tab.equals("itemsTab"))
            loadItemsForm(rpc.key, false, rpc.form.items);
        
        if(tab.equals("receiptsTab"))
            loadReceiptsForm(rpc.key, orderType, rpc.form.receipts);
        
        if(tab.equals("orderNotesTab"))
            loadOrderShippingNotesForm(rpc.key, rpc.form.shippingNotes);
        
        if(tab.equals("customerNotesTab"))
            loadCustomerNotesForm(rpc.key, rpc.form.customerNotes);
        
        if(tab.equals("reportToBillToTab"))
            loadReportToBillToForm(rpc.key, rpc.form.reportToBillTo);
        
        return rpc;
    }

    public OrderRPC fetchForUpdate(OrderRPC rpc) throws RPCException {
        /*
         * Call checkModels to make screen has most recent versions of dropdowns
         */
        checkModels(rpc);
        
        //remote interface to call the order bean
        OrderRemote remote = (OrderRemote)EJBFactory.lookup("openelis/OrderBean/remote");
        
        String orderType = rpc.orderType;
        //StringObject orderTypeObj = new StringObject(orderType);
        
        OrderDO orderDO = new OrderDO();
        try{
            orderDO = remote.getOrderAndLock(rpc.key, orderType, SessionManager.getSession().getId());
        }catch(Exception e){
            throw new RPCException(e.getMessage());
        }
        
        //set the fields in the RPC
        setFieldsInRPC(rpc.form, orderDO);
        
        String tab = rpc.form.orderTabPanel;
        if(tab.equals("itemsTab"))
            loadItemsForm(rpc.key, false, rpc.form.items);
        
        if(tab.equals("receiptsTab"))
            loadReceiptsForm(rpc.key, orderType, rpc.form.receipts);
        
        if(tab.equals("orderNotesTab"))
            loadOrderShippingNotesForm(rpc.key, rpc.form.shippingNotes);
        
        if(tab.equals("customerNotesTab"))
            loadCustomerNotesForm(rpc.key, rpc.form.customerNotes);
        
        if(tab.equals("reportToBillToTab"))
            loadReportToBillToForm(rpc.key, rpc.form.reportToBillTo);
        
        return rpc;  
    }

    public String getXML() throws RPCException {
        return null;
    }

    public HashMap<String, FieldType> getXMLData() throws RPCException {
        return null;
    }

    public HashMap<String, FieldType> getXMLData(HashMap<String, FieldType> args) throws RPCException {
        return null;
    }
    
    public OrderRPC getScreen(OrderRPC rpc) throws RPCException {
        String action = rpc.orderType;
        
        if(OrderRemote.INTERNAL.equals(action))
            rpc.xml = ServiceUtils.getXML(Constants.APP_ROOT+"/Forms/internalOrder.xsl");
        else if(OrderRemote.EXTERNAL.equals(action))
            rpc.xml = ServiceUtils.getXML(Constants.APP_ROOT+"/Forms/vendorOrder.xsl");
        else if(OrderRemote.KITS.equals(action))
            rpc.xml = ServiceUtils.getXML(Constants.APP_ROOT+"/Forms/kitOrder.xsl");

        /*
         * Load initial  models to RPC and store cache verison of models into Session for 
         * comparisons for later fetches
         */
        rpc.status = OrderStatusCacheHandler.getStatuses();
        SessionManager.getSession().setAttribute("orderStatusVersion",OrderStatusCacheHandler.version);
        rpc.costCenters = CostCentersCacheHandler.getCostCenters();
        SessionManager.getSession().setAttribute("costCenterVersion",CostCentersCacheHandler.version);
        rpc.shipFrom = ShipFromCacheHandler.getShipFroms();
        SessionManager.getSession().setAttribute("shipFromVersion",ShipFromCacheHandler.version);

        return rpc;
    }
    
    public void checkModels(OrderRPC rpc) {
        /*
         * Retrieve current version of models from session.
         */
        int statuses = (Integer)SessionManager.getSession().getAttribute("orderStatusVersion");
        int costCenters = (Integer)SessionManager.getSession().getAttribute("costCenterVersion");
        int shipFroms = (Integer)SessionManager.getSession().getAttribute("shipFromVersion");
        /*
         * Compare stored version to current cache versions and update if necessary. 
         */
        if(statuses != OrderStatusCacheHandler.version){
            rpc.status = OrderStatusCacheHandler.getStatuses();
            SessionManager.getSession().setAttribute("orderStatusVersion",OrderStatusCacheHandler.version);
        }
        if(costCenters != CostCentersCacheHandler.version){
            rpc.costCenters = CostCentersCacheHandler.getCostCenters();
            SessionManager.getSession().setAttribute("costCenterVersion",CostCentersCacheHandler.version);
        }
        if(shipFroms != ShipFromCacheHandler.version){
            rpc.shipFrom = ShipFromCacheHandler.getShipFroms();
            SessionManager.getSession().setAttribute("shipFromVersion",ShipFromCacheHandler.version);
        }
    }
    
    public OrderItemRPC loadContacts(OrderItemRPC rpc) throws RPCException {
        loadItemsForm(rpc.key, rpc.forDuplicate, rpc.form);
        return rpc;
    }
    
    public void loadItemsForm(Integer key, boolean forDuplicate, ItemsForm form) throws RPCException {
        getItemsModel(key, forDuplicate, form.itemsTable);
        form.load = true;
    }

    public OrderReceiptRPC loadReceipts(OrderReceiptRPC rpc) throws RPCException {
        loadReceiptsForm(rpc.key, rpc.orderType, rpc.form);
        return rpc;
    }
    
    public void loadReceiptsForm(Integer key, String orderType, ReceiptForm form) throws RPCException {
        getReceiptsModel(key, form.receiptsTable, orderType);
        form.load = true;
    }

    public OrderShippingNoteRPC loadOrderShippingNotes(OrderShippingNoteRPC rpc) throws RPCException {
        loadOrderShippingNotesForm(rpc.key, rpc.form);
        return rpc;
    }
    
    public void loadOrderShippingNotesForm(Integer key, OrderShippingNoteForm form) throws RPCException {
        form.text.setValue(getShippingNotes(key));
        form.load = true;
    }
    
    public OrderNoteRPC loadCustomerNotes(OrderNoteRPC rpc) throws RPCException {
        loadCustomerNotesForm(rpc.key, rpc.form);
        return rpc;
    }
    
    public void loadCustomerNotesForm(Integer key, OrderNoteForm form) throws RPCException {
        form.text.setValue(getCustomerNotes(key));
        form.load = true;
    }

    public ReportToBillToRpc loadReportToBillTo(ReportToBillToRpc rpc) throws RPCException {
        loadReportToBillToForm(rpc.key, rpc.form);
        return rpc;
    }
    
    public void loadReportToBillToForm(Integer key, ReportToBillToForm form) throws RPCException {
        fillReportToBillToValues(key, form);
        form.load = true;
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
    
    /*
    public DataModel<Integer> getInitialModel(String cat){
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
        DataModel<Integer> returnModel = new DataModel<Integer>();

        if(entries.size() > 0){ 
            
            returnModel.add(new DataSet<Integer>(0,new StringObject(" ")));
        }
        
        int i=0;
        while(i < entries.size()){
            IdNameDO resultDO = (IdNameDO) entries.get(i);
            
            returnModel.add(new DataSet<Integer>(resultDO.getId(),new StringObject(resultDO.getName())));
            
            i++;
        }       
        
        return returnModel;
    }
    */
    
    public OrderRPC getAddAutoFillValues(OrderRPC orderRpc) throws Exception {
        OrderRemote remote = (OrderRemote)EJBFactory.lookup("openelis/OrderBean/remote");
        OrderAddAutoFillDO autoDO;
        
        autoDO = remote.getAddAutoFillValues();
                
        orderRpc.form.statusId.setValue(new DataSet<Integer>(autoDO.getStatus()));
        orderRpc.form.orderedDate.setValue(autoDO.getOrderedDate().toString());
        orderRpc.form.requestedBy.setValue(autoDO.getRequestedBy());
        
        return orderRpc;
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
    
    private DataModel<Integer> getOrganizationMatches(String match){
        OrganizationRemote remote = (OrganizationRemote)EJBFactory.lookup("openelis/OrganizationBean/remote");
        DataModel<Integer> dataModel = new DataModel<Integer>();
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
            
            DataSet<Integer> data = new DataSet<Integer>();
            //hidden id
            data.setKey(orgId);
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
            OrderOrgKey key = new OrderOrgKey();
            key.aptSuite = aptSuite;
            key.zipCode = zipCode;
            
            data.setData(key);
            
            //add the dataset to the datamodel
            dataModel.add(data);                            
        }       
        
        return dataModel;           
    }
    
    private DataModel<Integer> getInventoryItemMatchesNoLoc(String match, boolean limitToMainStore, boolean allowSubAssembly){
        InventoryItemRemote remote = (InventoryItemRemote)EJBFactory.lookup("openelis/InventoryItemBean/remote");
        DataModel<Integer> dataModel = new DataModel<Integer>();
        List autoCompleteList;

        String parsedMatch = match.replace('*', '%');
        
        autoCompleteList = remote.inventoryItemStoreAutoCompleteLookupByName(parsedMatch+"%", 10, limitToMainStore, allowSubAssembly);
        
        for(int i=0; i < autoCompleteList.size(); i++){
            InventoryItemAutoDO resultDO = (InventoryItemAutoDO) autoCompleteList.get(i);
            
            Integer itemId = resultDO.getId();
            String name = resultDO.getName();
            String store = resultDO.getStore();
            String dispensedUnits = resultDO.getDispensedUnits();
            
            DataSet<Integer> data = new DataSet<Integer>();
            //hidden id
            data.setKey(itemId);
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
    
    private DataModel<Integer> getInventoryItemMatches(String match, boolean limitToMainStore, boolean allowSubAssembly){
        InventoryItemRemote remote = (InventoryItemRemote)EJBFactory.lookup("openelis/InventoryItemBean/remote");
        DataModel<Integer> dataModel = new DataModel<Integer>();
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
            
            DataSet<Integer> data = new DataSet<Integer>();
            //hidden id
            data.setKey(itemId);
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
            IntegerObject qtyObject = new IntegerObject();
            qtyObject.setValue(qty);
            data.add(qtyObject);
            
            IntegerObject locIdObject = new IntegerObject();
            locIdObject.setValue(locationId);
            data.setData(locIdObject);
                       
            //add the dataset to the datamodel
            dataModel.add(data);                            
        }       
        
        return dataModel;           
    }
    
    private DataModel<String> getOrderDescMatches(String match){
        OrderRemote remote = (OrderRemote)EJBFactory.lookup("openelis/OrderBean/remote");
        DataModel<String> dataModel = new DataModel<String>();
        List autoCompleteList;
        
        String parsedMatch = match.replace('*', '%');
        
        autoCompleteList = remote.orderDescriptionAutoCompleteLookup(parsedMatch+"%", 10);
        
        if(autoCompleteList.size() == 0){
            DataSet<String> data = new DataSet<String>();
            //string value
            data.setKey(match);
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
                
                DataSet<String> data = new DataSet<String>();
                data.setKey(name);
                
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
    
    private OrderDO getOrderDOFromRPC(OrderForm form){
        OrderDO orderDO = new OrderDO();
        
        orderDO.setId(form.id.getValue());
        orderDO.setNeededInDays(form.neededInDays.getValue());
        orderDO.setStatusId((Integer)form.statusId.getSelectedKey());
        orderDO.setOrderedDate(new Datetime(Datetime.YEAR, Datetime.DAY, form.orderedDate.getValue()).getDate());
        orderDO.setRequestedBy(form.requestedBy.getValue());
        orderDO.setCostCenter((Integer)form.costCenterId.getSelectedKey());
        orderDO.setShipFromId((Integer)form.shipFromId.getSelectedKey());
        orderDO.setExternalOrderNumber(form.externalOrderNumber.getValue());
        
        //set org values
        orderDO.setOrganization((String)form.organization.getTextValue());
        orderDO.setOrganizationId((Integer)form.organization.getSelectedKey());
        orderDO.organizationAddressDO.setMultipleUnit(form.multipleUnit.getValue());
        orderDO.organizationAddressDO.setStreetAddress(form.streetAddress.getValue());
        orderDO.organizationAddressDO.setCity(form.city.getValue());
        orderDO.organizationAddressDO.setState(form.state.getValue());
        orderDO.organizationAddressDO.setZipCode(form.zipCode.getValue());

        //set report to / bill to values
        orderDO.setReportTo((String)form.reportToBillTo.reportTo.getTextValue());
        orderDO.setReportToId((Integer)form.reportToBillTo.reportTo.getSelectedKey());
        orderDO.setBillTo((String)form.reportToBillTo.billTo.getTextValue());
        orderDO.setBillToId((Integer)form.reportToBillTo.billTo.getSelectedKey());
        

        orderDO.setDescription((String)form.description.getSelectedKey());
        
        return orderDO;
    }
    
    private void setFieldsInRPC(OrderForm form, OrderDO orderDO){
        form.id.setValue(orderDO.getId());
        form.neededInDays.setValue(orderDO.getNeededInDays());
        form.statusId.setValue(new DataSet<Integer>(orderDO.getStatusId()));
        form.orderedDate.setValue(orderDO.getOrderedDate().toString());
        form.requestedBy.setValue(orderDO.getRequestedBy());
        
        if(orderDO.getCostCenter() != null)
            form.costCenterId.setValue(new DataSet<Integer>(orderDO.getCostCenter()));
        
        if(orderDO.getExternalOrderNumber() != null)
            form.externalOrderNumber.setValue(orderDO.getExternalOrderNumber());
        
        if(orderDO.getShipFromId() != null)
            form.shipFromId.setValue(new DataSet<Integer>(orderDO.getShipFromId()));
        
        //create dataset for organization auto complete
        //if(form.getField(OrderMeta.ORDER_ORGANIZATION_META.getName()) != null){
        if(orderDO.getOrganizationId() == null)
            form.organization.clear();
        else{
            DataModel<Integer> orgModel = new DataModel<Integer>();
            DataSet<Integer> orgSet = new DataSet<Integer>();
            
            orgSet.setKey(orderDO.getOrganizationId());
            //columns
            orgSet.add(new StringObject(orderDO.getOrganization()));
            orgSet.add(new StringObject(orderDO.organizationAddressDO.getStreetAddress()));
            orgSet.add(new StringObject(orderDO.organizationAddressDO.getCity()));
            orgSet.add(new StringObject(orderDO.organizationAddressDO.getState()));
                            
            //hidden fields
            OrderOrgKey data = new OrderOrgKey();
            data.aptSuite = orderDO.organizationAddressDO.getMultipleUnit();
            data.zipCode = orderDO.organizationAddressDO.getZipCode();
            orgSet.setData(data);
            
            orgModel.add(orgSet);

            form.organization.setModel(orgModel);
            form.organization.setValue(orgModel.get(0));
        }
        form.multipleUnit.setValue(orderDO.organizationAddressDO.getMultipleUnit());
        form.streetAddress.setValue(orderDO.organizationAddressDO.getStreetAddress());
        form.city.setValue(orderDO.organizationAddressDO.getCity());
        form.state.setValue(orderDO.organizationAddressDO.getState());
        form.zipCode.setValue(orderDO.organizationAddressDO.getZipCode());
        //}
        
        //create dataset for description autocomplete
        //if(form.getField(OrderMeta.getDescription()) != null){
        if(orderDO.getDescription() == null)
            form.description.clear();
        else{
            DataModel<String> descModel = new DataModel<String>();
            descModel.add(new DataSet<String>(orderDO.getDescription(),new StringObject(orderDO.getDescription())));
            form.description.setModel(descModel);
            form.description.setValue(descModel.get(0));
        }
        //}
    }
    
    private List getOrderItemsListFromRPC(DataModel<Integer> itemsTable, Integer orderId, String orderType){
        List orderItems = new ArrayList();
        List<DataSet<Integer>> deletedRows = itemsTable.getDeletions();
        
        for(int i=0; i<itemsTable.size(); i++){
            OrderItemDO orderItemDO = new OrderItemDO();
            DataSet<Integer> row = itemsTable.get(i);
            //contact data
            Integer itemId = row.getKey();
           // DataMap map = (DataMap)row.getData();
           // NumberField locationId = null;
          //  NumberField qtyOnHand = null;
          //  NumberField inventoryTransactionId = null;
           // if(map != null){
           //     locationId = (NumberField)map.get("locationId");
           //     qtyOnHand = (NumberField)map.get("qtyOnHand");
           //     inventoryTransactionId = (NumberField)map.get("transactionId");
            //}

            if(itemId != null)
                orderItemDO.setId(itemId);
            orderItemDO.setOrder(orderId);
            orderItemDO.setInventoryItemId((Integer)((DropDownField)row.get(1)).getSelectedKey());
            orderItemDO.setQuantity((Integer)row.get(0).getValue());
            
            if(row.size() == 5){
                orderItemDO.setUnitCost((Double)row.get(3).getValue());
                orderItemDO.setCatalogNumber((String)row.get(4).getValue());
            }
            
           // if(locationId != null)
           //     orderItemDO.setLocationId(locationId.getIntegerValue());
            
            //if(qtyOnHand != null)
            //    orderItemDO.setQuantityOnHand(qtyOnHand.getIntegerValue());
            
            ////if(inventoryTransactionId != null)
            //    orderItemDO.setTransactionId(inventoryTransactionId.getIntegerValue());
            
            orderItems.add(orderItemDO);    
        }
        
        for(int j=0; j<deletedRows.size(); j++){
            DataSet<Integer> deletedRow = deletedRows.get(j);
            if(deletedRow.getKey() != null){
                OrderItemDO itemDO = new OrderItemDO();
                itemDO.setDelete(true);
                itemDO.setId(deletedRow.getKey());
                
                orderItems.add(itemDO);
            }
        }
        
        return orderItems;
    }
    
    public void getItemsModel(Integer orderId, boolean forDuplicate, TableField<Integer> model){
        OrderRemote remote = (OrderRemote)EJBFactory.lookup("openelis/OrderBean/remote");
        
        //if(orderType.equals(OrderRemote.INTERNAL) || orderType.equals(OrderRemote.KITS))
        //    withLocation = true;
        
        List itemsList = remote.getOrderItems(orderId);
        
        model.setValue(fillOrderItemsTable((DataModel<Integer>)model.getValue(), itemsList, forDuplicate));
    }
    
    public void getReceiptsModel(Integer orderId, TableField<Integer> model, String orderType){
        OrderRemote remote = (OrderRemote)EJBFactory.lookup("openelis/OrderBean/remote");
        
        List receiptsList = new ArrayList();
        if(orderType.equals(OrderRemote.EXTERNAL))
            receiptsList = remote.getOrderReceipts(orderId);
        else
            receiptsList = remote.getOrderLocTransactions(orderId);
        
        model.setValue(fillReceiptsTable((DataModel)model.getValue(),receiptsList, orderType));
    }
    
    public String getCustomerNotes(Integer orderId){
        OrderRemote remote = (OrderRemote)EJBFactory.lookup("openelis/OrderBean/remote");
        
        NoteDO noteDO = remote.getCustomerNote(orderId);
        
        if(noteDO != null)
            return noteDO.getText();
        
        return null;
    }
    
    public String getShippingNotes(Integer orderId){
        OrderRemote remote = (OrderRemote)EJBFactory.lookup("openelis/OrderBean/remote");
        
        NoteDO noteDO = remote.getOrderShippingNote(orderId);
        
        if(noteDO != null)
            return noteDO.getText();
        
        return null;
    }
    
    /*
    public DataModel getReportToBillTo(Integer orderId){
        OrderRemote remote = (OrderRemote)EJBFactory.lookup("openelis/OrderBean/remote");
        
        BillToReportToDO billToReportToDO = remote.getBillToReportTo(orderId);
        
        if(billToReportToDO != null){
            DataModel model = new DataModel();
            DataSet set = new DataSet();
            IntegerObject billToId = new IntegerObject();
            StringObject billTo = new StringObject();
            StringObject billToMultUnit = new StringObject();
            StringObject billToStreetAddress = new StringObject();
            StringObject billToCity = new StringObject();
            StringObject billToState = new StringObject();
            StringObject billToZipCode = new StringObject();
            
            
            IntegerObject reportToId = new IntegerObject();
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
            
            return model;
        }
        
        return null;
    }
    */
    
    public void fillReportToBillToValues(Integer key, ReportToBillToForm form){
        OrderRemote remote = (OrderRemote)EJBFactory.lookup("openelis/OrderBean/remote");
        
        BillToReportToDO billToReportToDO = remote.getBillToReportTo(key);
        
        if(billToReportToDO != null){
            //bill to values
            DataModel<Integer> billToModel = new DataModel<Integer>();
            billToModel.add(new DataSet<Integer>(billToReportToDO.getBillToId(),new StringObject(billToReportToDO.getBillTo())));
            form.billTo.setModel(billToModel);
            form.billTo.setValue(billToModel.get(0));
            
            form.billToMultUnit.setValue(billToReportToDO.billToAddress.getMultipleUnit());
            form.billToStreetAddress.setValue(billToReportToDO.billToAddress.getStreetAddress());
            form.billToCity.setValue(billToReportToDO.billToAddress.getCity());
            form.billToState.setValue(billToReportToDO.billToAddress.getState());
            form.billToZipCode.setValue(billToReportToDO.billToAddress.getZipCode());
            
            //report to values
            DataModel<Integer> reportToModel = new DataModel<Integer>();
            reportToModel.add(new DataSet<Integer>(billToReportToDO.getReportToId(),new StringObject(billToReportToDO.getReportTo())));
            form.reportTo.setModel(reportToModel);
            form.reportTo.setValue(reportToModel.get(0));
            
            form.reportToMultUnit.setValue(billToReportToDO.reportToAddress.getMultipleUnit());
            form.reportToStreetAddress.setValue(billToReportToDO.reportToAddress.getStreetAddress());
            form.reportToCity.setValue(billToReportToDO.reportToAddress.getCity());
            form.reportToState.setValue(billToReportToDO.reportToAddress.getState());
            form.reportToZipCode.setValue(billToReportToDO.reportToAddress.getZipCode());
            
        }
    }
    
    public DataModel<Integer> fillOrderItemsTable(DataModel<Integer> orderItemsModel, List orderItemsList, boolean forDuplicate){
        try 
        {
            orderItemsModel.clear();
            
            for(int iter = 0;iter < orderItemsList.size();iter++) {
                OrderItemDO orderItemRow = (OrderItemDO)orderItemsList.get(iter);
    
                   DataSet<Integer> row = orderItemsModel.createNewSet();
                   Integer id = orderItemRow.getId();
                   //NumberField locationId = new NumberField(orderItemRow.getLocationId());
                   //NumberField qtyOnHand = new NumberField(orderItemRow.getQuantityOnHand());
                  // NumberField inventoryTransactionId = new NumberField(orderItemRow.getTransactionId());
                  
                  // locationId.setValue(orderItemRow.getLocationId());
                  // inventoryTransactionId.setValue(orderItemRow.getTransactionId());
                   
                  // DataMap map = new DataMap();
                   
                    if(orderItemRow.getId() != null && !forDuplicate)
                        row.setKey(id);
                    
                    //if(orderItemRow.getLocationId() != null)
                    //    map.put("locationId", locationId);
                    
                  //  if(orderItemRow.getQuantityOnHand() != null)
                  //      map.put("qtyOnHand", qtyOnHand);
                    
                    //if(orderItemRow.getTransactionId() != null && !forDuplicate)
                    //    map.put("transactionId", inventoryTransactionId);
                    
                  //  row.setData(map);
                    row.get(0).setValue(orderItemRow.getQuantity());
                    
                    if(orderItemRow.getInventoryItemId() == null)
                        row.get(1).setValue(null);
                     else{
                         DataModel<Integer> invItemModel = new DataModel<Integer>();
                         invItemModel.add(new DataSet<Integer>(orderItemRow.getInventoryItemId(),new StringObject(orderItemRow.getInventoryItem())));
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
    
    public DataModel fillReceiptsTable(DataModel<Integer> receiptsModel, List receiptsList, String orderType){
        try 
        {
            receiptsModel.clear();
            
            for(int iter = 0;iter < receiptsList.size();iter++) {
                if(orderType.equals(OrderRemote.EXTERNAL)){
                    InventoryReceiptDO receiptRow = (InventoryReceiptDO)receiptsList.get(iter);
    
                    DataSet<Integer> row = receiptsModel.createNewSet();
                    
                    row.get(0).setValue(receiptRow.getReceivedDate().toString());
                    row.get(1).setValue(receiptRow.getInventoryItem());
                    row.get(2).setValue(receiptRow.getUpc());
                    row.get(3).setValue(receiptRow.getQuantityReceived());
                    row.get(4).setValue(receiptRow.getUnitCost());
                    row.get(5).setValue(receiptRow.getExternalReference());
                    
                    receiptsModel.add(row);
                }else{
                    InventoryLocationDO locRow = (InventoryLocationDO)receiptsList.get(iter);
                    
                    DataSet<Integer> row = receiptsModel.createNewSet();
                    
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