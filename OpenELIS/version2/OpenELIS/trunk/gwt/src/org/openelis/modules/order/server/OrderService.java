
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
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.openelis.domain.BillToReportToDO;
import org.openelis.domain.IdNameDO;
import org.openelis.domain.InventoryItemAutoDO;
import org.openelis.domain.InventoryLocationDO;
import org.openelis.domain.InventoryReceiptDO;
import org.openelis.domain.NoteViewDO;
import org.openelis.domain.OrderAddAutoFillDO;
import org.openelis.domain.OrderDO;
import org.openelis.domain.OrderItemDO;
import org.openelis.domain.OrganizationDO;
import org.openelis.gwt.common.Datetime;
import org.openelis.gwt.common.FieldErrorException;
import org.openelis.gwt.common.FormErrorException;
import org.openelis.gwt.common.LastPageException;
import org.openelis.gwt.common.TableFieldErrorException;
import org.openelis.gwt.common.ValidationErrorsList;
import org.openelis.gwt.common.data.deprecated.AbstractField;
import org.openelis.gwt.common.data.deprecated.DropDownField;
import org.openelis.gwt.common.data.deprecated.FieldType;
import org.openelis.gwt.common.data.deprecated.IntegerObject;
import org.openelis.gwt.common.data.deprecated.StringObject;
import org.openelis.gwt.common.data.deprecated.TableDataModel;
import org.openelis.gwt.common.data.deprecated.TableDataRow;
import org.openelis.gwt.common.data.deprecated.TableField;
import org.openelis.gwt.common.deprecated.Form;
import org.openelis.gwt.server.ServiceUtils;
import org.openelis.gwt.services.deprecated.AppScreenFormServiceInt;
import org.openelis.gwt.services.deprecated.AutoCompleteServiceInt;
import org.openelis.metamap.OrderMetaMap;
import org.openelis.modules.order.client.ItemsForm;
import org.openelis.modules.order.client.OrderForm;
import org.openelis.modules.order.client.OrderNoteForm;
import org.openelis.modules.order.client.OrderOrgKey;
import org.openelis.modules.order.client.OrderQuery;
import org.openelis.modules.order.client.OrderShippingNoteForm;
import org.openelis.modules.order.client.ReceiptForm;
import org.openelis.modules.order.client.ReportToBillToForm;
import org.openelis.persistence.EJBFactory;
import org.openelis.remote.InventoryItemRemote;
import org.openelis.remote.OrderRemote;
import org.openelis.remote.OrganizationRemote;
import org.openelis.server.constants.Constants;
import org.openelis.util.FormUtil;
import org.openelis.util.SessionManager;
import org.openelis.util.UTFResource;

import com.google.gwt.user.client.Window;

public class OrderService implements AppScreenFormServiceInt<OrderForm, OrderQuery>, AutoCompleteServiceInt {

    private UTFResource openElisConstants= UTFResource.getBundle((String)SessionManager.getSession().getAttribute("locale"));
    
    private static final OrderMetaMap OrderMeta = new OrderMetaMap();
    
    private static final int leftTableRowsPerPage = 15;
    
    public OrderQuery commitQuery(OrderQuery query) throws Exception {
        List orderIds;
        /*OrderQuery ordQuery = null;
        if(qList != null)
            ordQuery = (OrderQuery)qList;
        
        if(ordQuery == null) {
            ordQuery = (OrderQuery)SessionManager.getSession().getAttribute("OrderQuery");
         
            if(ordQuery == null)
                throw new QueryException(openElisConstants.getString("queryExpiredException"));

            //HashMap<String,AbstractField> fields = qList;
            //fields.remove("orderType");
            
            OrderRemote remote = (OrderRemote)EJBFactory.lookup("openelis/OrderBean/remote");
            try{
                orderIds = remote.query(ordQuery, (model.getPage()*leftTableRowsPerPage), leftTableRowsPerPage+1, ordQuery.type);
            }catch(Exception e){
                
                if(e instanceof LastPageException){
                    throw new LastPageException(openElisConstants.getString("lastPageException"));
                }else{
                    throw new Exception(e.getMessage()); 
                }
            }    
            
        }else{*/
            OrderRemote remote = (OrderRemote)EJBFactory.lookup("openelis/OrderBean/remote");
                        
            try{    
                orderIds = remote.query(query.fields,query.page*leftTableRowsPerPage,leftTableRowsPerPage, query.type);
            }catch(LastPageException e) {
                throw new LastPageException(openElisConstants.getString("lastPageException"));
            }catch(Exception e){
                throw new Exception(e.getMessage());
            }
    
            //put order type back in the field map because we may need it later
        
            //need to save the rpc used to the encache
            //SessionManager.getSession().setAttribute("OrderQuery", ordQuery);
        //}
        
        //fill the model with the query results
        int i=0;
        if(query.results == null)
            query.results = new TableDataModel<TableDataRow<Integer>>();
        else
            query.results.clear();
        while(i < orderIds.size() && i < leftTableRowsPerPage) {
            IdNameDO resultDO = (IdNameDO)orderIds.get(i);
            query.results.add(new TableDataRow<Integer>(resultDO.getId(),new StringObject(resultDO.getName())));
            i++;
        } 
 
        return query;
    }

    public OrderForm commitAdd(OrderForm rpc) throws Exception {
//      remote interface to call the order bean
        OrderRemote remote = (OrderRemote)EJBFactory.lookup("openelis/OrderBean/remote");
        OrderDO orderDO = new OrderDO();
        List orderItems = new ArrayList();
        NoteViewDO customerNotes = new NoteViewDO();
        NoteViewDO orderShippingNotes = new NoteViewDO();
        
        String orderType = rpc.orderType;
        
        //build the orderDO from the form
        orderDO = getOrderDOFromRPC(rpc);
        
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
            customerNotes.setText((String)rpc.customerNotes.text.getValue());
            customerNotes.setIsExternal("Y");
        }
        
        //build order shipping do notes from form
        orderShippingNotes.setSubject("");
        orderShippingNotes.setText((String)rpc.shippingNotes.text.getValue());
        orderShippingNotes.setIsExternal("Y");
        
        //order items info
        TableField<TableDataRow<Integer>> itemsTableField = rpc.items.itemsTable;
        TableDataModel<TableDataRow<Integer>> itemsModel = (TableDataModel<TableDataRow<Integer>>)itemsTableField.getValue();
        orderItems = getOrderItemsListFromRPC(itemsModel, orderDO.getId(), orderType);
                
        //send the changes to the database
        Integer orderId;
        try{
            orderId = (Integer)remote.updateOrder(orderDO, orderType, orderItems, customerNotes, orderShippingNotes);
        }catch(Exception e){
            if(e instanceof ValidationErrorsList){
                setRpcErrors(((ValidationErrorsList)e).getErrorList(), itemsTableField, rpc);
                return rpc;
            }else
                throw new Exception(e.getMessage());
        }
        
        //lookup the changes from the database and build the rpc
        orderDO.setId(orderId);

        //set the fields in the RPC
        setFieldsInRPC(rpc, orderDO, false);

        return rpc;
    }

    public OrderForm commitUpdate(OrderForm rpc) throws Exception {
        //remote interface to call the order bean
        OrderRemote remote = (OrderRemote)EJBFactory.lookup("openelis/OrderBean/remote");
        OrderDO orderDO = new OrderDO();
        List orderItems = new ArrayList();
        NoteViewDO customerNote = new NoteViewDO();
        NoteViewDO shippingNote = new NoteViewDO();

        Integer originalStatus = rpc.originalStatus;
        boolean qtyErrors = false;
        
        String orderType = rpc.orderType;

        //build the order DO from the form
        orderDO = getOrderDOFromRPC(rpc);
        
        //set the isExternal flag
        if(OrderRemote.INTERNAL.equals(orderType))
            orderDO.setIsExternal("N");
        else if(OrderRemote.EXTERNAL.equals(orderType))
            orderDO.setIsExternal("Y");
        else if(OrderRemote.KITS.equals(orderType))
            orderDO.setIsExternal("N");
        
        //build customer notes do from form (only for kit orders)
        if(OrderRemote.KITS.equals(orderType)){
            customerNote.setId(rpc.customerNotes.id);
            customerNote.setText(rpc.customerNotes.text.getValue());
            customerNote.setIsExternal("Y");
        }
        
        //build order shipping do notes from form
        shippingNote.setId(rpc.shippingNotes.id);
        shippingNote.setText(rpc.shippingNotes.text.getValue());
        shippingNote.setIsExternal("Y");
        
        //items info
        TableField<TableDataRow<Integer>> itemsTableField = rpc.items.itemsTable;
        TableDataModel<TableDataRow<Integer>> itemsModel = itemsTableField.getValue();
        orderItems = getOrderItemsListFromRPC(itemsModel, orderDO.getId(), orderType);     
        
        //send the changes to the database
        try{
            remote.updateOrder(orderDO, orderType, orderItems, customerNote, shippingNote);
            
        }catch(Exception e){
            if(e instanceof ValidationErrorsList){
                setRpcErrors(((ValidationErrorsList)e).getErrorList(), itemsTableField, rpc);
                return rpc;
            }else
                throw new Exception(e.getMessage());
        }

        //set the fields in the RPC
        setFieldsInRPC(rpc, orderDO, false);   
        
        return rpc;
    }

    public OrderForm commitDelete(OrderForm rpc) throws Exception {
        return null;
    }

    public OrderForm abort(OrderForm rpc) throws Exception {
        //remote interface to call the order bean
        OrderRemote remote = (OrderRemote)EJBFactory.lookup("openelis/OrderBean/remote");
        
        String orderType = rpc.orderType;
        //StringObject orderTypeObj = new StringObject(orderType);
        
        OrderDO orderDO = remote.getOrderAndUnlock(rpc.entityKey, orderType, SessionManager.getSession().getId());

        //set the fields in the RPC
        setFieldsInRPC(rpc, orderDO, false);
        
        if(rpc.items.load)
            loadItemsForm(rpc.entityKey, false, rpc.items);
        
        if(rpc.receipts.load)
            loadReceiptsForm(rpc.entityKey, orderType, rpc.receipts);
        
        if(rpc.shippingNotes.load)
            loadOrderShippingNotesForm(rpc.entityKey, rpc.shippingNotes);
        
        if(rpc.reportToBillTo.load)
            loadReportToBillToForm(rpc.entityKey, rpc.reportToBillTo);
        
        if(rpc.customerNotes.load)
            loadCustomerNotesForm(rpc.entityKey, rpc.customerNotes);
        
        return rpc;  
    }

    public OrderForm fetch(OrderForm rpc) throws Exception {
        //remote interface to call the order bean
        OrderRemote remote = (OrderRemote)EJBFactory.lookup("openelis/OrderBean/remote");
        
        String orderType = rpc.orderType;
        //StringObject orderTypeObj = new StringObject(orderType);
        
        OrderDO orderDO = remote.getOrder(rpc.entityKey, orderType);

        //set the fields in the RPC
        setFieldsInRPC(rpc, orderDO, false);
        
        String tab = rpc.orderTabPanel;
        if(tab.equals("itemsTab"))
            loadItemsForm(rpc.entityKey, false, rpc.items);
        
        if(tab.equals("receiptsTab"))
            loadReceiptsForm(rpc.entityKey, orderType, rpc.receipts);
        
        if(tab.equals("orderNotesTab"))
            loadOrderShippingNotesForm(rpc.entityKey, rpc.shippingNotes);
        
        if(tab.equals("customerNotesTab"))
            loadCustomerNotesForm(rpc.entityKey, rpc.customerNotes);
        
        if(tab.equals("reportToBillToTab"))
            loadReportToBillToForm(rpc.entityKey, rpc.reportToBillTo);
        
        return rpc;
    }

    public OrderForm fetchForUpdate(OrderForm rpc) throws Exception {
        //remote interface to call the order bean
        OrderRemote remote = (OrderRemote)EJBFactory.lookup("openelis/OrderBean/remote");
        
        String orderType = rpc.orderType;
        //StringObject orderTypeObj = new StringObject(orderType);
        
        OrderDO orderDO = new OrderDO();
        try{
            orderDO = remote.getOrderAndLock(rpc.entityKey, orderType, SessionManager.getSession().getId());
        }catch(Exception e){
            throw new Exception(e.getMessage());
        }
        
        //set the fields in the RPC
        setFieldsInRPC(rpc, orderDO, false);
        
        String tab = rpc.orderTabPanel;
        if(tab.equals("itemsTab"))
            loadItemsForm(rpc.entityKey, false, rpc.items);
        
        if(tab.equals("receiptsTab"))
            loadReceiptsForm(rpc.entityKey, orderType, rpc.receipts);
        
        if(tab.equals("orderNotesTab"))
            loadOrderShippingNotesForm(rpc.entityKey, rpc.shippingNotes);
        
        if(tab.equals("customerNotesTab"))
            loadCustomerNotesForm(rpc.entityKey, rpc.customerNotes);
        
        if(tab.equals("reportToBillToTab"))
            loadReportToBillToForm(rpc.entityKey, rpc.reportToBillTo);
        
        return rpc;  
    }
    
    public OrderForm getDuplicateRPC(OrderForm rpc) throws Exception{
        OrderRemote remote = (OrderRemote)EJBFactory.lookup("openelis/OrderBean/remote");        
        
        String orderType = rpc.orderType;
        //StringObject orderTypeObj = new StringObject(orderType);
        
        OrderDO orderDO = remote.getOrder(rpc.entityKey, orderType);
        
        //reload the main form
        setFieldsInRPC(rpc, orderDO, true);

        //reload the items
        loadItemsForm(rpc.entityKey, true, rpc.items);
        
        //clear the receipts
        rpc.receipts.receiptsTable.setValue(null);
        
        //clear shipping notes
        rpc.shippingNotes.text.setValue(null);

        //clear customer notes
        rpc.customerNotes.text.setValue(null);
        
        //clear the keys
        rpc.entityKey = null;
        rpc.items.entityKey = null;
        rpc.receipts.entityKey = null;
        rpc.shippingNotes.entityKey = null;
        rpc.customerNotes.entityKey = null;

        return rpc;
    }
    
    public OrderForm getScreen(OrderForm rpc) throws Exception {
        String action = rpc.orderType;
        
        if(OrderRemote.INTERNAL.equals(action))
            rpc.xml = ServiceUtils.getXML(Constants.APP_ROOT+"/Forms/internalOrder.xsl");
        else if(OrderRemote.EXTERNAL.equals(action))
            rpc.xml = ServiceUtils.getXML(Constants.APP_ROOT+"/Forms/vendorOrder.xsl");
        else if(OrderRemote.KITS.equals(action))
            rpc.xml = ServiceUtils.getXML(Constants.APP_ROOT+"/Forms/kitOrder.xsl");

        return rpc;
    }
    
    public ItemsForm loadItems(ItemsForm rpc) throws Exception {
        loadItemsForm(rpc.entityKey, rpc.forDuplicate, rpc);
        return rpc;
    }
    
    public void loadItemsForm(Integer key, boolean forDuplicate, ItemsForm form) throws Exception {
        getItemsModel(key, forDuplicate, form.itemsTable);
        form.load = true;
    }

    public ReceiptForm loadReceipts(ReceiptForm rpc) throws Exception {
        loadReceiptsForm(rpc.entityKey, rpc.orderType, rpc);
        return rpc;
    }
    
    public void loadReceiptsForm(Integer key, String orderType, ReceiptForm form) throws Exception {
        getReceiptsModel(key, form.receiptsTable, orderType);
        form.load = true;
    }

    public OrderShippingNoteForm loadOrderShippingNotes(OrderShippingNoteForm rpc) throws Exception {
        loadOrderShippingNotesForm(rpc.entityKey, rpc);
        return rpc;
    }
    
    public void loadOrderShippingNotesForm(Integer key, OrderShippingNoteForm form) throws Exception {
        getShippingNotes(key, form);
        form.load = true;
    }
    
    public OrderNoteForm loadCustomerNotes(OrderNoteForm rpc) throws Exception {
        loadCustomerNotesForm(rpc.entityKey, rpc);
        return rpc;
    }
    
    public void loadCustomerNotesForm(Integer key, OrderNoteForm form) throws Exception {
        getCustomerNotes(key, form);
        form.load = true;
    }

    public ReportToBillToForm loadReportToBillTo(ReportToBillToForm rpc) throws Exception {
        loadReportToBillToForm(rpc.entityKey, rpc);
        return rpc;
    }
    
    public void loadReportToBillToForm(Integer key, ReportToBillToForm form) throws Exception {
        fillReportToBillToValues(key, form);
        form.load = true;
    }
    
    private void setRpcErrors(List exceptionList, TableField orderItemsTable, Form<? extends Object>  form){
        HashMap<String,AbstractField> map = null;
        if(exceptionList.size() > 0)
            map = FormUtil.createFieldMap(form);
        for (int i=0; i<exceptionList.size();i++) {
            if(exceptionList.get(i) instanceof TableFieldErrorException){
                int rowindex = ((TableFieldErrorException)exceptionList.get(i)).getRowIndex();
                orderItemsTable.getField(rowindex,((TableFieldErrorException)exceptionList.get(i)).getFieldName())
                    .addError(openElisConstants.getString(((FieldErrorException)exceptionList.get(i)).getMessage()));

            //if the error is on the field
            }else if(exceptionList.get(i) instanceof FieldErrorException)
                map.get(((FieldErrorException)exceptionList.get(i)).getFieldName()).addError(openElisConstants.getString(((FieldErrorException)exceptionList.get(i)).getMessage()));
            
            //if the error is on the entire form
            else if(exceptionList.get(i) instanceof FormErrorException)
                form.addError(openElisConstants.getString(((FormErrorException)exceptionList.get(i)).getMessage()));
            }        
        
        form.status = Form.Status.invalid;
    }
    
    public OrderForm getAddAutoFillValues(OrderForm orderRpc) throws Exception {
        OrderRemote remote = (OrderRemote)EJBFactory.lookup("openelis/OrderBean/remote");
        OrderAddAutoFillDO autoDO;
        
        autoDO = remote.getAddAutoFillValues();
                
        orderRpc.statusId.setValue(new TableDataRow<Integer>(autoDO.getStatus()));
        orderRpc.orderedDate.setValue(autoDO.getOrderedDate().toString());
        orderRpc.requestedBy.setValue(autoDO.getRequestedBy());
        
        return orderRpc;
    }

    public TableDataModel getMatches(String cat, TableDataModel model, String match, HashMap params) throws Exception {
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
    
    private TableDataModel<TableDataRow<Integer>> getOrganizationMatches(String match){
        OrganizationRemote remote = (OrganizationRemote)EJBFactory.lookup("openelis/OrganizationBean/remote");
        TableDataModel<TableDataRow<Integer>> dataModel = new TableDataModel<TableDataRow<Integer>>();
        ArrayList<OrganizationDO> autoCompleteList = null;
        Integer id;
        
        try{
            //this will throw an exception if it isnt an id
            //lookup by id...should only bring back 1 result
            id = Integer.parseInt(match);
        }catch(NumberFormatException e){
            id = null;
        }
        
        try {
            if (id != null) {
                autoCompleteList = new ArrayList<OrganizationDO>(1);
                autoCompleteList.add(remote.fetchActiveById(id));
            } else {
                autoCompleteList = remote.fetchActiveByName(match+"%", 10);
            }
        } catch (Exception e) {
            Window.alert(e.getMessage());
        }
        
        for(int i=0; i < autoCompleteList.size(); i++){
            OrganizationDO resultDO = autoCompleteList.get(i);
            //org id
            Integer orgId = resultDO.getId();
            //org name
            String name = resultDO.getName();
            //org apt suite #
            String aptSuite = resultDO.getAddress().getMultipleUnit();
            //org street address
            String address = resultDO.getAddress().getStreetAddress();
            //org city
            String city = resultDO.getAddress().getCity();
            //org state
            String state = resultDO.getAddress().getState();
            //org zipcode
            String zipCode = resultDO.getAddress().getZipCode();
            
            TableDataRow<Integer> data = new TableDataRow<Integer>(orgId,
                                                                   new FieldType[] {
                                                                                    new StringObject(name),
                                                                                    new StringObject(address),
                                                                                    new StringObject(city),
                                                                                    new StringObject(state)
                                                                   }
                            );
            
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
    
    private TableDataModel<TableDataRow<Integer>> getInventoryItemMatchesNoLoc(String match, boolean limitToMainStore, boolean allowSubAssembly){
        InventoryItemRemote remote = (InventoryItemRemote)EJBFactory.lookup("openelis/InventoryItemBean/remote");
        TableDataModel<TableDataRow<Integer>> dataModel = new TableDataModel<TableDataRow<Integer>>();
        List autoCompleteList;

        String parsedMatch = match.replace('*', '%');
        
        autoCompleteList = remote.inventoryItemStoreAutoCompleteLookupByName(parsedMatch+"%", 10, limitToMainStore, allowSubAssembly);
        
        for(int i=0; i < autoCompleteList.size(); i++){
            InventoryItemAutoDO resultDO = (InventoryItemAutoDO) autoCompleteList.get(i);
            
            Integer itemId = resultDO.getId();
            String name = resultDO.getName();
            String store = resultDO.getStore();
            String dispensedUnits = resultDO.getDispensedUnits();
            
            TableDataRow<Integer> data = new TableDataRow<Integer>(itemId,
                                                                   new FieldType[] {
                                                                                    new StringObject(name),
                                                                                    new StringObject(store),
                                                                                    new StringObject(dispensedUnits)
                                                                   }
                            );
                        
            //add the dataset to the datamodel
            dataModel.add(data);                            
        }       
        
        return dataModel;           
    }
    
    private TableDataModel<TableDataRow<Integer>> getInventoryItemMatches(String match, boolean limitToMainStore, boolean allowSubAssembly){
        InventoryItemRemote remote = (InventoryItemRemote)EJBFactory.lookup("openelis/InventoryItemBean/remote");
        TableDataModel<TableDataRow<Integer>> dataModel = new TableDataModel<TableDataRow<Integer>>();
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
            
            TableDataRow<Integer> data = new TableDataRow<Integer>(itemId,
                                                                   new FieldType[] {
                                                                                    new StringObject(name),
                                                                                    new StringObject(store),
                                                                                    new StringObject(location),
                                                                                    new StringObject(lotNum),
                                                                                    new StringObject(expDate),
                                                                                    new IntegerObject(qty),
                                                                                    new IntegerObject(locationId)
                                                                   }
                                        );
                       
            //add the dataset to the datamodel
            dataModel.add(data);                            
        }       
        
        return dataModel;           
    }
    
    private TableDataModel<TableDataRow<String>> getOrderDescMatches(String match){
        OrderRemote remote = (OrderRemote)EJBFactory.lookup("openelis/OrderBean/remote");
        TableDataModel<TableDataRow<String>> dataModel = new TableDataModel<TableDataRow<String>>();
        List autoCompleteList;
        
        String parsedMatch = match.replace('*', '%');
        
        autoCompleteList = remote.orderDescriptionAutoCompleteLookup(parsedMatch+"%", 10);
        
        if(autoCompleteList.size() == 0){
            TableDataRow<String> data = new TableDataRow<String>(match,new StringObject(match));
            //add the dataset to the datamodel
            dataModel.add(data);    
        }else{
            for(int i=0; i < autoCompleteList.size(); i++){
                IdNameDO resultDO = (IdNameDO) autoCompleteList.get(i);
                
                String name = resultDO.getName();
                
                TableDataRow<String> data = new TableDataRow<String>(name,new StringObject(name));
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
        orderDO.setOrderedDate(new Datetime(Datetime.YEAR, Datetime.DAY, new Date(form.orderedDate.getValue())).getDate());
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
    
    private void setFieldsInRPC(OrderForm form, OrderDO orderDO, boolean forDuplicate){
        if(!forDuplicate){
            form.id.setValue(orderDO.getId());
            form.externalOrderNumber.setValue(orderDO.getExternalOrderNumber());
        }else{
            form.id.setValue(null);
            form.externalOrderNumber.setValue(null);
        }
        
        form.neededInDays.setValue(orderDO.getNeededInDays());
        form.statusId.setValue(new TableDataRow<Integer>(orderDO.getStatusId()));
        form.orderedDate.setValue(orderDO.getOrderedDate().toString());
        form.requestedBy.setValue(orderDO.getRequestedBy());
        
        if(orderDO.getCostCenter() != null)
            form.costCenterId.setValue(new TableDataRow<Integer>(orderDO.getCostCenter()));
        
        if(orderDO.getShipFromId() != null)
            form.shipFromId.setValue(new TableDataRow<Integer>(orderDO.getShipFromId()));
        
        //create dataset for organization auto complete
        if(orderDO.getOrganizationId() == null)
            form.organization.clear();
        else{
            TableDataModel<TableDataRow<Integer>> orgModel = new TableDataModel<TableDataRow<Integer>>();
            TableDataRow<Integer> orgSet = new TableDataRow<Integer>(orderDO.getOrganizationId(),
                                                                     new FieldType[] {
                                                                                      new StringObject(orderDO.getOrganization()),
                                                                                      new StringObject(orderDO.organizationAddressDO.getStreetAddress()),
                                                                                      new StringObject(orderDO.organizationAddressDO.getCity()),
                                                                                      new StringObject(orderDO.organizationAddressDO.getState())
                                                                     }
                                            );
                            
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
            TableDataModel<TableDataRow<String>> descModel = new TableDataModel<TableDataRow<String>>();
            descModel.add(new TableDataRow<String>(orderDO.getDescription(),new StringObject(orderDO.getDescription())));
            form.description.setModel(descModel);
            form.description.setValue(descModel.get(0));
        }
        //}
    }
    
    private List getOrderItemsListFromRPC(TableDataModel<TableDataRow<Integer>> itemsTable, Integer orderId, String orderType){
        List orderItems = new ArrayList();
        List<TableDataRow<Integer>> deletedRows = itemsTable.getDeletions();
        
        for(int i=0; i<itemsTable.size(); i++){
            OrderItemDO orderItemDO = new OrderItemDO();
            TableDataRow<Integer> row = itemsTable.get(i);
            //contact data
            Integer itemId = row.key;
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
            orderItemDO.setInventoryItemId((Integer)((DropDownField)row.cells[1]).getSelectedKey());
            orderItemDO.setQuantity((Integer)row.cells[0].getValue());
            
            if(row.size() == 5){
                orderItemDO.setUnitCost((Double)row.cells[3].getValue());
                orderItemDO.setCatalogNumber((String)row.cells[4].getValue());
            }
            
           // if(locationId != null)
           //     orderItemDO.setLocationId(locationId.getIntegerValue());
            
            //if(qtyOnHand != null)
            //    orderItemDO.setQuantityOnHand(qtyOnHand.getIntegerValue());
            
            ////if(inventoryTransactionId != null)
            //    orderItemDO.setTransactionId(inventoryTransactionId.getIntegerValue());
            
            orderItems.add(orderItemDO);    
        }
        
        if(deletedRows != null){
            for(int j=0; j<deletedRows.size(); j++){
                TableDataRow<Integer> deletedRow = deletedRows.get(j);
                if(deletedRow.key != null){
                    OrderItemDO itemDO = new OrderItemDO();
                    itemDO.setDelete(true);
                    itemDO.setId(deletedRow.key);
                    
                    orderItems.add(itemDO);
                }
            }
        }
        
        return orderItems;
    }
    
    public void getItemsModel(Integer orderId, boolean forDuplicate, TableField<TableDataRow<Integer>> model){
        OrderRemote remote = (OrderRemote)EJBFactory.lookup("openelis/OrderBean/remote");
        
        //if(orderType.equals(OrderRemote.INTERNAL) || orderType.equals(OrderRemote.KITS))
        //    withLocation = true;
        
        List itemsList = remote.getOrderItems(orderId);
        
        model.setValue(fillOrderItemsTable((TableDataModel<TableDataRow<Integer>>)model.getValue(), itemsList, forDuplicate));
    }
    
    public void getReceiptsModel(Integer orderId, TableField<TableDataRow<Integer>> model, String orderType){
        OrderRemote remote = (OrderRemote)EJBFactory.lookup("openelis/OrderBean/remote");
        
        List receiptsList = new ArrayList();
        if(orderType.equals(OrderRemote.EXTERNAL))
            receiptsList = remote.getOrderReceipts(orderId);
        else
            receiptsList = remote.getOrderLocTransactions(orderId);
        
        model.setValue(fillReceiptsTable(model.getValue(),receiptsList, orderType));
    }
    
    public void getCustomerNotes(Integer orderId, OrderNoteForm form){
        OrderRemote remote = (OrderRemote)EJBFactory.lookup("openelis/OrderBean/remote");
        
        NoteViewDO noteDO = remote.getCustomerNote(orderId);
        
        if(noteDO != null){
            form.id = noteDO.getId();
            form.text.setValue(noteDO.getText());
        }
    }
    
    public void getShippingNotes(Integer orderId, OrderShippingNoteForm form){
        OrderRemote remote = (OrderRemote)EJBFactory.lookup("openelis/OrderBean/remote");
        
        NoteViewDO noteDO = remote.getOrderShippingNote(orderId);
        
        if(noteDO != null){
            form.id = noteDO.getId();
            form.text.setValue(noteDO.getText());
        }
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
            TableDataModel<TableDataRow<Integer>> billToModel = new TableDataModel<TableDataRow<Integer>>();
            billToModel.add(new TableDataRow<Integer>(billToReportToDO.getBillToId(),new StringObject(billToReportToDO.getBillTo())));
            form.billTo.setModel(billToModel);
            form.billTo.setValue(billToModel.get(0));
            
            form.billToMultUnit.setValue(billToReportToDO.billToAddress.getMultipleUnit());
            form.billToStreetAddress.setValue(billToReportToDO.billToAddress.getStreetAddress());
            form.billToCity.setValue(billToReportToDO.billToAddress.getCity());
            form.billToState.setValue(billToReportToDO.billToAddress.getState());
            form.billToZipCode.setValue(billToReportToDO.billToAddress.getZipCode());
            
            //report to values
            TableDataModel<TableDataRow<Integer>> reportToModel = new TableDataModel<TableDataRow<Integer>>();
            reportToModel.add(new TableDataRow<Integer>(billToReportToDO.getReportToId(),new StringObject(billToReportToDO.getReportTo())));
            form.reportTo.setModel(reportToModel);
            form.reportTo.setValue(reportToModel.get(0));
            
            form.reportToMultUnit.setValue(billToReportToDO.reportToAddress.getMultipleUnit());
            form.reportToStreetAddress.setValue(billToReportToDO.reportToAddress.getStreetAddress());
            form.reportToCity.setValue(billToReportToDO.reportToAddress.getCity());
            form.reportToState.setValue(billToReportToDO.reportToAddress.getState());
            form.reportToZipCode.setValue(billToReportToDO.reportToAddress.getZipCode());
            
        }
    }
    
    public TableDataModel<TableDataRow<Integer>> fillOrderItemsTable(TableDataModel<TableDataRow<Integer>> orderItemsModel, List orderItemsList, boolean forDuplicate){
        try 
        {
            orderItemsModel.clear();
            
            for(int iter = 0;iter < orderItemsList.size();iter++) {
                OrderItemDO orderItemRow = (OrderItemDO)orderItemsList.get(iter);
    
                   TableDataRow<Integer> row = orderItemsModel.createNewSet();
                   Integer id = orderItemRow.getId();
                   //NumberField locationId = new NumberField(orderItemRow.getLocationId());
                   //NumberField qtyOnHand = new NumberField(orderItemRow.getQuantityOnHand());
                  // NumberField inventoryTransactionId = new NumberField(orderItemRow.getTransactionId());
                  
                  // locationId.setValue(orderItemRow.getLocationId());
                  // inventoryTransactionId.setValue(orderItemRow.getTransactionId());
                   
                  // DataMap map = new DataMap();
                   
                    if(orderItemRow.getId() != null && !forDuplicate)
                        row.key = id;
                    
                    //if(orderItemRow.getLocationId() != null)
                    //    map.put("locationId", locationId);
                    
                  //  if(orderItemRow.getQuantityOnHand() != null)
                  //      map.put("qtyOnHand", qtyOnHand);
                    
                    //if(orderItemRow.getTransactionId() != null && !forDuplicate)
                    //    map.put("transactionId", inventoryTransactionId);
                    
                  //  row.setData(map);
                    row.cells[0].setValue(orderItemRow.getQuantity());
                    
                    if(orderItemRow.getInventoryItemId() == null)
                        row.cells[1].setValue(null);
                     else{
                         TableDataModel<TableDataRow<Integer>> invItemModel = new TableDataModel<TableDataRow<Integer>>();
                         invItemModel.add(new TableDataRow<Integer>(orderItemRow.getInventoryItemId(),new StringObject(orderItemRow.getInventoryItem())));
                         ((DropDownField<Integer>)row.cells[1]).setModel(invItemModel);
                         row.cells[1].setValue(invItemModel.get(0));
                     }
                    
                    row.cells[2].setValue(orderItemRow.getStore());
                    
                    if(row.size() == 5){
                        row.cells[3].setValue(orderItemRow.getUnitCost());
                        row.cells[4].setValue(orderItemRow.getCatalogNumber());
                    }
                    
                    orderItemsModel.add(row);
           } 
            
        } catch (Exception e) {
    
            e.printStackTrace();
            return null;
        }       
        
        return orderItemsModel;
    }
    
    public TableDataModel<TableDataRow<Integer>> fillReceiptsTable(TableDataModel<TableDataRow<Integer>> receiptsModel, List receiptsList, String orderType){
        try 
        {
            receiptsModel.clear();
            
            for(int iter = 0;iter < receiptsList.size();iter++) {
                if(orderType.equals(OrderRemote.EXTERNAL)){
                    InventoryReceiptDO receiptRow = (InventoryReceiptDO)receiptsList.get(iter);
    
                    TableDataRow<Integer> row = receiptsModel.createNewSet();
                    
                    row.cells[0].setValue(receiptRow.getReceivedDate().toString());
                    row.cells[1].setValue(receiptRow.getInventoryItem());
                    row.cells[2].setValue(receiptRow.getUpc());
                    row.cells[3].setValue(receiptRow.getQuantityReceived());
                    row.cells[4].setValue(receiptRow.getUnitCost());
                    row.cells[5].setValue(receiptRow.getExternalReference());
                    
                    receiptsModel.add(row);
                }else{
                    InventoryLocationDO locRow = (InventoryLocationDO)receiptsList.get(iter);
                    
                    TableDataRow<Integer> row = receiptsModel.createNewSet();
                    
                    row.cells[0].setValue(locRow.getInventoryItem());                    
                    row.cells[1].setValue(locRow.getStorageLocation());
                    row.cells[2].setValue(locRow.getQuantityOnHand());
                    row.cells[3].setValue(locRow.getLotNumber());
                    if(locRow.getExpirationDate() != null && locRow.getExpirationDate().getDate() != null)
                        row.cells[4].setValue(locRow.getExpirationDate().toString());
                    
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