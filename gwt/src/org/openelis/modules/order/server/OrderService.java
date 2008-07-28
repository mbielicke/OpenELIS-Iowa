/**
* The contents of this file are subject to the Mozilla Public License
* Version 1.1 (the "License"); you may not use this file except in
* compliance with the License. You may obtain a copy of the License at
* http://www.mozilla.org/MPL/
* 
* Software distributed under the License is distributed on an "AS IS"
* basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
* License for the specific language governing rights and limitations under
* the License.
* 
* The Original Code is OpenELIS code.
* 
* Copyright (C) The University of Iowa.  All Rights Reserved.
*/
package org.openelis.modules.order.server;

import org.openelis.domain.BillToReportToDO;
import org.openelis.domain.IdNameDO;
import org.openelis.domain.InventoryItemAutoDO;
import org.openelis.domain.InventoryReceiptDO;
import org.openelis.domain.NoteDO;
import org.openelis.domain.OrderAddAutoFillDO;
import org.openelis.domain.OrderDO;
import org.openelis.domain.OrderItemDO;
import org.openelis.domain.OrganizationAutoDO;
import org.openelis.gwt.common.EntityLockedException;
import org.openelis.gwt.common.FieldErrorException;
import org.openelis.gwt.common.FormErrorException;
import org.openelis.gwt.common.FormRPC;
import org.openelis.gwt.common.IForm;
import org.openelis.gwt.common.LastPageException;
import org.openelis.gwt.common.QueryException;
import org.openelis.gwt.common.RPCException;
import org.openelis.gwt.common.TableFieldErrorException;
import org.openelis.gwt.common.data.AbstractField;
import org.openelis.gwt.common.data.BooleanObject;
import org.openelis.gwt.common.data.DataModel;
import org.openelis.gwt.common.data.DataSet;
import org.openelis.gwt.common.data.DropDownField;
import org.openelis.gwt.common.data.ModelField;
import org.openelis.gwt.common.data.ModelObject;
import org.openelis.gwt.common.data.NumberField;
import org.openelis.gwt.common.data.NumberObject;
import org.openelis.gwt.common.data.StringField;
import org.openelis.gwt.common.data.StringObject;
import org.openelis.gwt.common.data.TableField;
import org.openelis.gwt.common.data.TableModel;
import org.openelis.gwt.common.data.TableRow;
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

public class OrderService implements AppScreenFormServiceInt, AutoCompleteServiceInt {

    private UTFResource openElisConstants= UTFResource.getBundle((String)SessionManager.getSession().getAttribute("locale"));
    
    private static final OrderMetaMap OrderMeta = new OrderMetaMap();
    
    private static final int leftTableRowsPerPage = 15;
    
    public DataModel commitQuery(FormRPC rpcSend, DataModel model) throws RPCException {
        List orderIds;
        
        if(rpcSend == null){

            FormRPC rpc = (FormRPC)SessionManager.getSession().getAttribute("OrderQuery");
            StringField orderTypeField = new StringField();
            orderTypeField = (StringField)rpc.getField("orderType");
            String orderType = (String)orderTypeField.getValue();
            
            if(rpc == null)
                throw new QueryException(openElisConstants.getString("queryExpiredException"));

            HashMap<String,AbstractField> fields = rpc.getFieldMap();
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
            orderTypeField = (StringField)rpcSend.getField("orderType");
            String orderType = (String)orderTypeField.getValue();
            
            
            HashMap<String,AbstractField> fields = rpcSend.getFieldMap();
            fields.remove("orderType");
            fields.remove("itemsTable");
            fields.remove("label1");
            fields.remove("item");
            //FIXME we may need to put this back fields.remove(OrderMeta.getInventoryTransaction().getReceivedDate());

            try{    
                orderIds = remote.query(fields,0,leftTableRowsPerPage, orderType);
    
            }catch(Exception e){
                throw new RPCException(e.getMessage());
            }
    
            //put order type back in the field map because we may need it later
            fields.put("orderType", orderTypeField);
        
            //need to save the rpc used to the encache
            SessionManager.getSession().setAttribute("OrderQuery", rpcSend);
        }
        
        //fill the model with the query results
        int i=0;
        model.clear();
        while(i < orderIds.size() && i < leftTableRowsPerPage) {
            IdNameDO resultDO = (IdNameDO)orderIds.get(i);
 
            DataSet row = new DataSet();
            NumberObject id = new NumberObject(resultDO.getId());
            StringObject name = new StringObject(resultDO.getName());
            
            row.setKey(id);         
            row.addObject(name);
            model.add(row);
            i++;
        } 
 
        return model;
    }

    public FormRPC commitAdd(FormRPC rpcSend, FormRPC rpcReturn) throws RPCException {
//      remote interface to call the order bean
        OrderRemote remote = (OrderRemote)EJBFactory.lookup("openelis/OrderBean/remote");
        OrderDO orderDO = new OrderDO();
        List orderItems = new ArrayList();
        NoteDO customerNotes = new NoteDO();
        NoteDO orderShippingNotes = new NoteDO();
        
        String orderType = (String)rpcSend.getFieldValue("orderType");
        
        //build the orderDO from the form
        orderDO = getOrderDOFromRPC(rpcSend);
        
        //set the isExternal flag
        if(OrderRemote.INTERNAL.equals(orderType))
            orderDO.setIsExternal("N");
        else if(OrderRemote.EXTERNAL.equals(orderType))
            orderDO.setIsExternal("Y");
        else if(OrderRemote.KITS.equals(orderType))
            orderDO.setIsExternal("N");
        
        //build customer notes do from form
        customerNotes.setSubject("");
        customerNotes.setText((String)rpcSend.getFieldValue(OrderMeta.ORDER_CUSTOMER_NOTE_META.getText()));
        customerNotes.setIsExternal("Y");
        
        //build order shipping do notes from form
        orderShippingNotes.setSubject("");
        orderShippingNotes.setText((String)rpcSend.getFieldValue(OrderMeta.ORDER_SHIPPING_NOTE_META.getText()));
        orderShippingNotes.setIsExternal("Y");
        
        //order items info
        TableModel itemsTable = (TableModel)rpcSend.getField("itemsTable").getValue();
        orderItems = getOrderItemsListFromRPC(itemsTable, orderDO.getId(), orderType);
                
        //validate the fields on the backend
        List exceptionList = remote.validateForAdd(orderDO, orderType, orderItems);
        
        if(exceptionList.size() > 0){
            setRpcErrors(exceptionList, itemsTable, rpcSend);
            return rpcSend;
        } 
        
        //send the changes to the database
        Integer orderId;
        try{
            orderId = (Integer)remote.updateOrder(orderDO, orderType, orderItems, customerNotes, orderShippingNotes);
        }catch(Exception e){
            exceptionList = new ArrayList();
            exceptionList.add(e);
            
            setRpcErrors(exceptionList, itemsTable, rpcSend);
            
            return rpcSend;
        }
        
        //lookup the changes from the database and build the rpc
        orderDO.setId(orderId);

//      set the fields in the RPC
        setFieldsInRPC(rpcReturn, orderDO);

        return rpcReturn;
    }

    public FormRPC commitUpdate(FormRPC rpcSend, FormRPC rpcReturn) throws RPCException {
        //remote interface to call the order bean
        OrderRemote remote = (OrderRemote)EJBFactory.lookup("openelis/OrderBean/remote");
        OrderDO orderDO = new OrderDO();
        List orderItems = new ArrayList();
        NoteDO customerNote = new NoteDO();
        NoteDO shippingNote = new NoteDO();
        
        String orderType = (String)rpcSend.getFieldValue("orderType");

        //build the order DO from the form
        orderDO = getOrderDOFromRPC(rpcSend);
        
        //set the isExternal flag
        if(OrderRemote.INTERNAL.equals(orderType))
            orderDO.setIsExternal("N");
        else if(OrderRemote.EXTERNAL.equals(orderType))
            orderDO.setIsExternal("Y");
        else if(OrderRemote.KITS.equals(orderType))
            orderDO.setIsExternal("N");
        
        //build customer notes do from form
        customerNote.setSubject("");
        customerNote.setText((String)rpcSend.getFieldValue(OrderMeta.ORDER_CUSTOMER_NOTE_META.getText()));
        customerNote.setIsExternal("Y");
        
        //build order shipping do notes from form
        shippingNote.setSubject("");
        shippingNote.setText((String)rpcSend.getFieldValue(OrderMeta.ORDER_SHIPPING_NOTE_META.getText()));
        shippingNote.setIsExternal("Y");
        
        //contacts info
        TableModel itemsTable = (TableModel)rpcSend.getField("itemsTable").getValue();
        orderItems = getOrderItemsListFromRPC(itemsTable, orderDO.getId(), orderType);     
        
        //validate the fields on the backend
        List exceptionList = remote.validateForUpdate(orderDO, orderType, orderItems);
        if(exceptionList.size() > 0){
            setRpcErrors(exceptionList, itemsTable, rpcSend);
            
            return rpcSend;
        } 
        
        //send the changes to the database
        try{
            remote.updateOrder(orderDO, orderType, orderItems, customerNote, shippingNote);
        }catch(Exception e){
            if(e instanceof EntityLockedException)
                throw new RPCException(e.getMessage());
            
            exceptionList = new ArrayList();
            exceptionList.add(e);
            
            setRpcErrors(exceptionList, itemsTable, rpcSend);
            
            return rpcSend;
        }

        //set the fields in the RPC
        setFieldsInRPC(rpcReturn, orderDO);   
        
        return rpcReturn;
    }

    public FormRPC commitDelete(DataSet key, FormRPC rpcReturn) throws RPCException {
        return null;
    }

    public FormRPC abort(DataSet key, FormRPC rpcReturn) throws RPCException {
        //remote interface to call the order bean
        OrderRemote remote = (OrderRemote)EJBFactory.lookup("openelis/OrderBean/remote");
        
        String orderType = (String)rpcReturn.getFieldValue("orderType");
        
        OrderDO orderDO = remote.getOrderAndUnlock((Integer)key.getKey().getValue(), orderType, SessionManager.getSession().getId());

        //set the fields in the RPC
        setFieldsInRPC(rpcReturn, orderDO);
        
        return rpcReturn;  
    }

    public FormRPC fetch(DataSet key, FormRPC rpcReturn) throws RPCException {
        //remote interface to call the order bean
        OrderRemote remote = (OrderRemote)EJBFactory.lookup("openelis/OrderBean/remote");
        
        String orderType = (String)rpcReturn.getFieldValue("orderType");
        
        OrderDO orderDO = remote.getOrder((Integer)key.getKey().getValue(), orderType);

        //set the fields in the RPC
        setFieldsInRPC(rpcReturn, orderDO);
        
        return rpcReturn;
    }

    public FormRPC fetchForUpdate(DataSet key, FormRPC rpcReturn) throws RPCException {
        //remote interface to call the order bean
        OrderRemote remote = (OrderRemote)EJBFactory.lookup("openelis/OrderBean/remote");
        
        String orderType = (String)rpcReturn.getFieldValue("orderType");
        
        OrderDO orderDO = new OrderDO();
        try{
            orderDO = remote.getOrderAndLock((Integer)key.getKey().getValue(), orderType, SessionManager.getSession().getId());
        }catch(Exception e){
            throw new RPCException(e.getMessage());
        }
        
        //set the fields in the RPC
        setFieldsInRPC(rpcReturn, orderDO);
        
        return rpcReturn;  
    }

    public String getXML() throws RPCException {
        return null;
    }

    public HashMap getXMLData() throws RPCException {
        // TODO Auto-generated method stub
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
            DataModel costCenterDropdownField = (DataModel)CachingManager.getElement("InitialData", "orderCostCenterDropdown");
            
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
                CachingManager.putElement("InitialData", "orderCostCenterDropdown", costCenterDropdownField);
            }
            
            returnMap.put("status", statusDropdownField);
            returnMap.put("store", storeDropdownField);
            returnMap.put("costCenter", costCenterDropdownField);
        }
        
        return returnMap;
    }
    
    private void setRpcErrors(List exceptionList, TableModel orderItemsTable, FormRPC rpcSend){
        //we need to get the keys and look them up in the resource bundle for internationalization
        for (int i=0; i<exceptionList.size();i++) {
            //if the error is inside the order items table
            if(exceptionList.get(i) instanceof TableFieldErrorException){
                TableRow row = orderItemsTable.getRow(((TableFieldErrorException)exceptionList.get(i)).getRowIndex());
                row.getColumn(orderItemsTable.getColumnIndexByFieldName(((TableFieldErrorException)exceptionList.get(i)).getFieldName()))
                                                                        .addError(openElisConstants.getString(((FieldErrorException)exceptionList.get(i)).getMessage()));
            //if the error is on the field
            }else if(exceptionList.get(i) instanceof FieldErrorException)
                rpcSend.getField(((FieldErrorException)exceptionList.get(i)).getFieldName()).addError(openElisConstants.getString(((FieldErrorException)exceptionList.get(i)).getMessage()));
            //if the error is on the entire form
            else if(exceptionList.get(i) instanceof FormErrorException)
                rpcSend.addError(openElisConstants.getString(((FormErrorException)exceptionList.get(i)).getMessage()));
        }   
        rpcSend.status = IForm.Status.invalid;
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
            blankset.addObject(blankStringId);
            
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
            set.addObject(textObject);
            
            numberId.setValue(dropdownId);
            set.setKey(numberId);
            
            returnModel.add(set);
            
            i++;
        }       
        
        return returnModel;
    }
    
    public ModelObject getAddAutoFillValues() throws Exception {
        ModelObject modelObj = new ModelObject();
        DataModel model = new DataModel();
        DataSet set = new DataSet();
        
        OrderRemote remote = (OrderRemote)EJBFactory.lookup("openelis/OrderBean/remote");
        OrderAddAutoFillDO autoDO;
        
        autoDO = remote.getAddAutoFillValues();
                
        DropDownField status = new DropDownField();
        StringField dateOrdered = new StringField();
        StringField requestedBy = new StringField();
        
        status.setValue(autoDO.getStatus());
        dateOrdered.setValue(autoDO.getOrderedDate().toString());
        requestedBy.setValue(autoDO.getRequestedBy());
        
        set.addObject(status);
        set.addObject(dateOrdered);
        set.addObject(requestedBy);
        
        model.add(set);
        
        modelObj.setValue(model);
        
        return modelObj;
    }

    public DataModel getMatches(String cat, DataModel model, String match, HashMap params) throws RPCException {
        if("organization".equals(cat))
            return getOrganizationMatches(match);
        else if("reportTo".equals(cat))
            return getOrganizationMatches(match);
        else if("billTo".equals(cat))
            return getOrganizationMatches(match);
        else if("inventoryItemWithStoreAndLocSubItems".equals(cat))
            return getInventoryItemMatches(match, false, true);
        else if("inventoryItemWithStore".equals(cat))
            return getInventoryItemMatchesNoLoc(match, true, true);
        else if("inventoryItemWithStoreAndLocMainStore".equals(cat))
            return getInventoryItemMatches(match, true, false);
        else if("inventoryItemWithStoreAndLoc".equals(cat))
            return getInventoryItemMatches(match, false, false);
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
            data.addObject(nameObject);
            StringObject addressObject = new StringObject();
            addressObject.setValue(address);
            data.addObject(addressObject);
            StringObject cityObject = new StringObject();
            cityObject.setValue(city);
            data.addObject(cityObject);
            StringObject stateObject = new StringObject();
            stateObject.setValue(state);
            data.addObject(stateObject);
            
            //hidden fields
            StringObject aptSuiteObj = new StringObject();
            aptSuiteObj.setValue(aptSuite);
            data.addObject(aptSuiteObj);
            StringObject zipCodeObj = new StringObject();
            zipCodeObj.setValue(zipCode);
            data.addObject(zipCodeObj);
            
            
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
            String purchasedUnits = resultDO.getPurchasedUnits();
            
            DataSet data = new DataSet();
            //hidden id
            NumberObject idObject = new NumberObject(NumberObject.Type.INTEGER);
            idObject.setValue(itemId);
            data.setKey(idObject);
            //columns
            StringObject nameObject = new StringObject();
            nameObject.setValue(name);
            data.addObject(nameObject);
            StringObject storeObject = new StringObject();
            storeObject.setValue(store);
            data.addObject(storeObject);
            StringObject purchUnitsObj = new StringObject();
            purchUnitsObj.setValue(purchasedUnits);
            data.addObject(purchUnitsObj);
                        
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
            data.addObject(nameObject);
            StringObject storeObject = new StringObject();
            storeObject.setValue(store);
            data.addObject(storeObject);

            StringObject locationObject = new StringObject();
            locationObject.setValue(location);
            data.addObject(locationObject);
            StringObject lotNumObj = new StringObject();
            lotNumObj.setValue(lotNum);
            data.addObject(lotNumObj);
            StringObject expDateObj = new StringObject();
            expDateObj.setValue(expDate);
            data.addObject(expDateObj);
            NumberObject qtyObject = new NumberObject(NumberObject.Type.INTEGER);
            qtyObject.setValue(qty);
            data.addObject(qtyObject);
            NumberObject locIdObject = new NumberObject(NumberObject.Type.INTEGER);
            locIdObject.setValue(locationId);
            data.addObject(locIdObject);
                       
            //add the dataset to the datamodel
            dataModel.add(data);                            
        }       
        
        return dataModel;           
    }
    
    private OrderDO getOrderDOFromRPC(FormRPC rpcSend){
        OrderDO orderDO = new OrderDO();
        
        orderDO.setId((Integer) rpcSend.getFieldValue(OrderMeta.getId()));
        orderDO.setNeededInDays((Integer) rpcSend.getFieldValue(OrderMeta.getNeededInDays()));
        orderDO.setStatusId((Integer) rpcSend.getFieldValue(OrderMeta.getStatusId()));
        orderDO.setOrganization((String)((DropDownField)rpcSend.getField(OrderMeta.ORDER_ORGANIZATION_META.getName())).getTextValue());
        orderDO.setOrganizationId((Integer) rpcSend.getFieldValue(OrderMeta.ORDER_ORGANIZATION_META.getName()));
        orderDO.setOrderedDate(new Datetime(Datetime.YEAR, Datetime.DAY, (String)rpcSend.getFieldValue(OrderMeta.getOrderedDate())).getDate());
        orderDO.setRequestedBy((String)rpcSend.getFieldValue(OrderMeta.getRequestedBy()));
        orderDO.setCostCenter((Integer) rpcSend.getFieldValue(OrderMeta.getCostCenterId()));
        orderDO.setReportTo((String)((DropDownField)rpcSend.getField(OrderMeta.ORDER_REPORT_TO_META.getName())).getTextValue());
        orderDO.setReportToId((Integer) rpcSend.getFieldValue(OrderMeta.ORDER_REPORT_TO_META.getName()));
        orderDO.setBillTo((String)((DropDownField)rpcSend.getField(OrderMeta.ORDER_BILL_TO_META.getName())).getTextValue());
        orderDO.setBillToId((Integer) rpcSend.getFieldValue(OrderMeta.ORDER_BILL_TO_META.getName()));
        orderDO.setExternalOrderNumber((String)rpcSend.getFieldValue(OrderMeta.getExternalOrderNumber()));
        
        //set org address values
        if(orderDO.getOrganizationId() != null){
            orderDO.organizationAddressDO.setMultipleUnit((String)rpcSend.getFieldValue(OrderMeta.ORDER_ORGANIZATION_META.ADDRESS.getMultipleUnit()));
            orderDO.organizationAddressDO.setStreetAddress((String)rpcSend.getFieldValue(OrderMeta.ORDER_ORGANIZATION_META.ADDRESS.getStreetAddress()));
            orderDO.organizationAddressDO.setCity((String)rpcSend.getFieldValue(OrderMeta.ORDER_ORGANIZATION_META.ADDRESS.getCity()));
            orderDO.organizationAddressDO.setState((String)rpcSend.getFieldValue(OrderMeta.ORDER_ORGANIZATION_META.ADDRESS.getState()));
            orderDO.organizationAddressDO.setZipCode((String)rpcSend.getFieldValue(OrderMeta.ORDER_ORGANIZATION_META.ADDRESS.getZipCode()));
        }
        
        //set bill to address values
        
        //set report to addresss values
        
        return orderDO;
    }
    
    private void setFieldsInRPC(FormRPC rpcReturn, OrderDO orderDO){
        rpcReturn.setFieldValue(OrderMeta.getId(), orderDO.getId());
        rpcReturn.setFieldValue(OrderMeta.getNeededInDays(), orderDO.getNeededInDays());
        rpcReturn.setFieldValue(OrderMeta.getStatusId(), orderDO.getStatusId());
        rpcReturn.setFieldValue(OrderMeta.getOrderedDate(), orderDO.getOrderedDate().toString());
        rpcReturn.setFieldValue(OrderMeta.getRequestedBy(), orderDO.getRequestedBy());
        rpcReturn.setFieldValue(OrderMeta.getCostCenterId(), orderDO.getCostCenter());
         rpcReturn.setFieldValue(OrderMeta.getExternalOrderNumber(), orderDO.getExternalOrderNumber());
        
        //create dataset for organization auto complete
        if(orderDO.getOrganizationId() == null)
            rpcReturn.setFieldValue(OrderMeta.ORDER_ORGANIZATION_META.getName(), null);
        else{
            DataSet orgSet = new DataSet();
            NumberObject id = new NumberObject(NumberObject.Type.INTEGER);
            StringObject text = new StringObject();
            id.setValue(orderDO.getOrganizationId());
            text.setValue(orderDO.getOrganization());
            orgSet.setKey(id);
            orgSet.addObject(text);
            rpcReturn.setFieldValue(OrderMeta.ORDER_ORGANIZATION_META.getName(), orgSet);
        }
        
        //create dataset for report to auto complete
        if(orderDO.getReportToId() == null)
            rpcReturn.setFieldValue(OrderMeta.ORDER_REPORT_TO_META.getName(), null);
        else{
            DataSet reportToSet = new DataSet();
            NumberObject id = new NumberObject(NumberObject.Type.INTEGER);
            StringObject text = new StringObject();
            id.setValue(orderDO.getReportToId());
            text.setValue(orderDO.getReportTo());
            reportToSet.setKey(id);
            reportToSet.addObject(text);
            rpcReturn.setFieldValue(OrderMeta.ORDER_REPORT_TO_META.getName(), reportToSet);
        }
        
        //create dataset for bill to auto complete
        if(orderDO.getBillToId() == null)
            rpcReturn.setFieldValue(OrderMeta.ORDER_BILL_TO_META.getName(), null);
        else{
            DataSet billToSet = new DataSet();
            NumberObject id = new NumberObject(NumberObject.Type.INTEGER);
            StringObject text = new StringObject();
            id.setValue(orderDO.getBillToId());
            text.setValue(orderDO.getBillTo());
            billToSet.setKey(id);
            billToSet.addObject(text);
            rpcReturn.setFieldValue(OrderMeta.ORDER_BILL_TO_META.getName(), billToSet);
        }
        
        //organization address values
        if(orderDO.getOrganizationId() != null){
            rpcReturn.setFieldValue(OrderMeta.ORDER_ORGANIZATION_META.ADDRESS.getMultipleUnit(), orderDO.organizationAddressDO.getMultipleUnit());
            rpcReturn.setFieldValue(OrderMeta.ORDER_ORGANIZATION_META.ADDRESS.getStreetAddress(),orderDO.organizationAddressDO.getStreetAddress());
            rpcReturn.setFieldValue(OrderMeta.ORDER_ORGANIZATION_META.ADDRESS.getCity(), orderDO.organizationAddressDO.getCity());
            rpcReturn.setFieldValue(OrderMeta.ORDER_ORGANIZATION_META.ADDRESS.getState(),orderDO.organizationAddressDO.getState());
            rpcReturn.setFieldValue(OrderMeta.ORDER_ORGANIZATION_META.ADDRESS.getZipCode(), orderDO.organizationAddressDO.getZipCode());
        }
    }
    
    private List getOrderItemsListFromRPC(TableModel itemsTable, Integer orderId, String orderType){
        List orderItems = new ArrayList();
        
        for(int i=0; i<itemsTable.numRows(); i++){
            OrderItemDO orderItemDO = new OrderItemDO();
            TableRow row = itemsTable.getRow(i);
            //contact data
            NumberField itemId = (NumberField)row.getHidden("itemId");
            NumberField locationId = (NumberField)row.getHidden("locationId");
            NumberField inventoryTransactionId = (NumberField)row.getHidden("transactionId");
            StringField deleteFlag = (StringField)row.getHidden("deleteFlag");
            if(deleteFlag == null){
                orderItemDO.setDelete(false);
            }else{
                orderItemDO.setDelete("Y".equals(deleteFlag.getValue()));
            }
            //if the user created the row and clicked the remove button before commit...
            //we dont need to do anything with that row
            if(deleteFlag != null && "Y".equals(deleteFlag.getValue()) && itemId == null){
                //do nothing
            }else{
                if(itemId != null)
                    orderItemDO.setId((Integer)itemId.getValue());
                orderItemDO.setOrder(orderId);
                orderItemDO.setInventoryItemId((Integer)row.getColumn(1).getValue());
                orderItemDO.setQuantityRequested((Integer)row.getColumn(0).getValue());
                
                if(locationId != null)
                    orderItemDO.setLocationId((Integer)locationId.getValue());
                
                if(inventoryTransactionId != null)
                    orderItemDO.setTransactionId((Integer)inventoryTransactionId.getValue());
                
                orderItems.add(orderItemDO);    
            }
        }
        
        return orderItems;
    }
    
    public TableField getItemsModel(NumberObject orderId, TableField model, StringObject orderTypeObj){
        OrderRemote remote = (OrderRemote)EJBFactory.lookup("openelis/OrderBean/remote");
        boolean withLocation = false;
        String orderType = (String)orderTypeObj.getValue();
        
        if(orderType.equals(OrderRemote.INTERNAL) || orderType.equals(OrderRemote.KITS))
            withLocation = true;
        
        List itemsList = remote.getOrderItems((Integer)orderId.getValue(), withLocation);
        
        model.setValue(fillOrderItemsTable((TableModel)model.getValue(),itemsList));
        
        return model;
    }
    
    public TableField getReceiptsModel(NumberObject orderId, TableField model){
        OrderRemote remote = (OrderRemote)EJBFactory.lookup("openelis/OrderBean/remote");
        
        List receiptsList = remote.getOrderReceipts((Integer)orderId.getValue());
        
        model.setValue(fillReceiptsTable((TableModel)model.getValue(),receiptsList));
        
        return model;
    }
    
    public StringField getCustomerNotes(NumberObject orderId){
        OrderRemote remote = (OrderRemote)EJBFactory.lookup("openelis/OrderBean/remote");
        
        NoteDO noteDO = remote.getCustomerNote((Integer)orderId.getValue());
        
        StringField noteValue = new StringField();
        
        if(noteDO != null)
            noteValue.setValue(noteDO.getText());
        
        return noteValue;
    }
    
    public StringField getShippingNotes(NumberObject orderId){
        OrderRemote remote = (OrderRemote)EJBFactory.lookup("openelis/OrderBean/remote");
        
        NoteDO noteDO = remote.getOrderShippingNote((Integer)orderId.getValue());
        
        StringField noteValue = new StringField();
        
        if(noteDO != null)
            noteValue.setValue(noteDO.getText());
        
        return noteValue;
    }
    
    public ModelField getReportToBillTo(NumberObject orderId){
        OrderRemote remote = (OrderRemote)EJBFactory.lookup("openelis/OrderBean/remote");
        
        BillToReportToDO billToReportToDO = remote.getBillToReportTo((Integer)orderId.getValue());
        
        if(billToReportToDO != null){
            //datamodel
            DataModel model = new DataModel();
            DataSet set = new DataSet();
            DataSet reportToAutoset = new DataSet();
            DataSet billToAutoSet = new DataSet();
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
            
            set.addObject(billToId);
            set.addObject(billTo);
            set.addObject(billToMultUnit);
            set.addObject(billToStreetAddress);
            set.addObject(billToCity);
            set.addObject(billToState);
            set.addObject(billToZipCode);
            
            set.addObject(reportToId);
            set.addObject(reportTo);
            set.addObject(reportToMultUnit);
            set.addObject(reportToStreetAddress);
            set.addObject(reportToCity);
            set.addObject(reportToState);
            set.addObject(reportToZipCode);
            
            model.add(set);
            
            reportToAutoset.setKey(reportToId);
            reportToAutoset.addObject(reportTo);
            model.add(reportToAutoset);
            
            billToAutoSet.setKey(billToId);
            billToAutoSet.addObject(billTo);
            model.add(billToAutoSet);
            
            ModelField modelField = new ModelField();
            modelField.setValue(model);
            
            return modelField;
        }
        
        return null;
    }
    
    public TableModel fillOrderItemsTable(TableModel orderItemsModel, List orderItemsList){
        try 
        {
            orderItemsModel.reset();
            
            for(int iter = 0;iter < orderItemsList.size();iter++) {
                OrderItemDO orderItemRow = (OrderItemDO)orderItemsList.get(iter);
    
                   TableRow row = orderItemsModel.createRow();
                   NumberField id = new NumberField(orderItemRow.getId());
                   NumberField locationId = new NumberField(orderItemRow.getLocationId());
                   NumberField inventoryTransactionId = new NumberField(orderItemRow.getTransactionId());
                    row.addHidden("itemId", id);
                    
                    if(orderItemRow.getLocationId() != null)
                        row.addHidden("locationId", locationId);
                    
                    if(orderItemRow.getTransactionId() != null)
                        row.addHidden("transactionId", inventoryTransactionId);
                    
                    row.getColumn(0).setValue(orderItemRow.getQuantityRequested());
                    
                    if(orderItemRow.getInventoryItemId() == null)
                        row.getColumn(1).setValue(null);
                     else{
                         DataSet invItemSet = new DataSet();
                         NumberObject idObj = new NumberObject(orderItemRow.getInventoryItemId());
                         StringObject text = new StringObject(orderItemRow.getInventoryItem());
                         invItemSet.setKey(idObj);
                         invItemSet.addObject(text);
                         row.getColumn(1).setValue(invItemSet);
                     }
                    
                    row.getColumn(2).setValue(orderItemRow.getStore());
                    
                    if(row.numColumns() == 4)
                        row.getColumn(3).setValue(orderItemRow.getLocation());
                    
                    orderItemsModel.addRow(row);
           } 
            
        } catch (Exception e) {
    
            e.printStackTrace();
            return null;
        }       
        
        return orderItemsModel;
    }
    
    public TableModel fillReceiptsTable(TableModel receiptsModel, List receiptsList){
        try 
        {
            receiptsModel.reset();
            
            for(int iter = 0;iter < receiptsList.size();iter++) {
                InventoryReceiptDO receiptRow = (InventoryReceiptDO)receiptsList.get(iter);
    
                   TableRow row = receiptsModel.createRow();
                    
                    row.getColumn(0).setValue(receiptRow.getReceivedDate().toString());
                    row.getColumn(1).setValue(receiptRow.getInventoryItem());
                    row.getColumn(2).setValue(receiptRow.getUpc());
                    row.getColumn(3).setValue(receiptRow.getQuantityReceived());
                    row.getColumn(4).setValue(receiptRow.getUnitCost());
                    row.getColumn(5).setValue(receiptRow.getQcReference());
                    
                    receiptsModel.addRow(row);
           } 
            
        } catch (Exception e) {
    
            e.printStackTrace();
            return null;
        }       
        
        return receiptsModel;
    }
}
