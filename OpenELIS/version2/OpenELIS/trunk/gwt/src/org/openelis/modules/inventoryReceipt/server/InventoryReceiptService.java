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
package org.openelis.modules.inventoryReceipt.server;


import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.openelis.domain.InventoryItemAutoDO;
import org.openelis.domain.InventoryReceiptDO;
import org.openelis.domain.OrganizationAutoDO;
import org.openelis.domain.StorageLocationAutoDO;
import org.openelis.gwt.common.DatetimeRPC;
import org.openelis.gwt.common.FieldErrorException;
import org.openelis.gwt.common.FormErrorException;
import org.openelis.gwt.common.FormRPC;
import org.openelis.gwt.common.IForm;
import org.openelis.gwt.common.RPCException;
import org.openelis.gwt.common.TableFieldErrorException;
import org.openelis.gwt.common.data.CheckField;
import org.openelis.gwt.common.data.DataModel;
import org.openelis.gwt.common.data.DataObject;
import org.openelis.gwt.common.data.DataSet;
import org.openelis.gwt.common.data.DateField;
import org.openelis.gwt.common.data.DateObject;
import org.openelis.gwt.common.data.DropDownField;
import org.openelis.gwt.common.data.ModelObject;
import org.openelis.gwt.common.data.NumberField;
import org.openelis.gwt.common.data.NumberObject;
import org.openelis.gwt.common.data.StringField;
import org.openelis.gwt.common.data.StringObject;
import org.openelis.gwt.common.data.TableModel;
import org.openelis.gwt.common.data.TableRow;
import org.openelis.gwt.server.ServiceUtils;
import org.openelis.gwt.services.AppScreenFormServiceInt;
import org.openelis.gwt.services.AutoCompleteServiceInt;
import org.openelis.gwt.widget.AutoCompleteDropdown;
import org.openelis.gwt.widget.FormCalendarWidget;
import org.openelis.metamap.InventoryReceiptMetaMap;
import org.openelis.persistence.EJBFactory;
import org.openelis.remote.InventoryItemRemote;
import org.openelis.remote.InventoryReceiptRemote;
import org.openelis.remote.OrganizationRemote;
import org.openelis.remote.StorageLocationRemote;
import org.openelis.server.constants.Constants;
import org.openelis.util.Datetime;
import org.openelis.util.SessionManager;
import org.openelis.util.UTFResource;

import com.google.gwt.user.client.ui.TextBox;

public class InventoryReceiptService implements AppScreenFormServiceInt, AutoCompleteServiceInt {

    private static final InventoryReceiptMetaMap InventoryReceiptMeta = new InventoryReceiptMetaMap();
    
    private UTFResource openElisConstants = UTFResource.getBundle((String)SessionManager.getSession().getAttribute("locale"));
    
    public FormRPC abort(DataSet key, FormRPC rpcReturn) throws RPCException {
        // TODO Auto-generated method stub
        return null;
    }

    public FormRPC commitAdd(FormRPC rpcSend, FormRPC rpcReturn) throws RPCException {
        //remote interface to call the inventory receipt bean
        InventoryReceiptRemote remote = (InventoryReceiptRemote)EJBFactory.lookup("openelis/InventoryReceiptBean/remote");
        List inventoryReceipts = new ArrayList();
        
        //receipts table
        TableModel receiptsTable = (TableModel)rpcSend.getField("receiptsTable").getValue();
        
        //build the inventory receipts list DO from the form
        inventoryReceipts = getInventoryReceiptsFromTable(receiptsTable);
        
        //validate the fields on the backend
        List exceptionList = remote.validateForAdd(inventoryReceipts);
        
        if(exceptionList.size() > 0){
            setRpcErrors(exceptionList, receiptsTable, rpcSend);
            return rpcSend;
        } 
        
        //send the changes to the database
        try{
            remote.updateInventoryReceipt(inventoryReceipts);
        }catch(Exception e){
            System.out.println(e.getMessage());
            exceptionList = new ArrayList();
            exceptionList.add(e);
            
            setRpcErrors(exceptionList, receiptsTable, rpcSend);
            
            return rpcSend;
        }
        
        //FIXME ?? newOrganizationDO.setOrganizationId(orgId);

        //set the fields in the RPC
        //FIXME ?? setFieldsInRPC(rpcReturn, newOrganizationDO);

        //rpc send for now...
        return rpcSend;
    }

    public FormRPC commitDelete(DataSet key, FormRPC rpcReturn) throws RPCException {
        // TODO Auto-generated method stub
        return null;
    }

    public DataModel commitQuery(FormRPC rpcSend, DataModel model) throws RPCException {
        // TODO Auto-generated method stub
        return null;
    }

    public FormRPC commitUpdate(FormRPC rpcSend, FormRPC rpcReturn) throws RPCException {
        // TODO Auto-generated method stub
        return null;
    }

    public FormRPC fetch(DataSet key, FormRPC rpcReturn) throws RPCException {
        // TODO Auto-generated method stub
        return null;
    }

    public FormRPC fetchForUpdate(DataSet key, FormRPC rpcReturn) throws RPCException {
        // TODO Auto-generated method stub
        return null;
    }

    public String getXML() throws RPCException {
        return ServiceUtils.getXML(Constants.APP_ROOT+"/Forms/inventoryReceipt.xsl");
    }

    public HashMap getXMLData() throws RPCException {
        // TODO Auto-generated method stub
        return null;
    }

    public HashMap getXMLData(HashMap args) throws RPCException {
        // TODO Auto-generated method stub
        return null;
    }
    
    public ModelObject getReceipts(NumberObject orderId){
        InventoryReceiptRemote remote = (InventoryReceiptRemote)EJBFactory.lookup("openelis/InventoryReceiptBean/remote");
        
        List receiptRecords = remote.getInventoryReceiptRecords((Integer)orderId.getValue());
        
        ModelObject modelObj = new ModelObject();
        DataModel model = new DataModel(); 
        DatetimeRPC todaysDate = DatetimeRPC.getInstance(Datetime.YEAR,Datetime.DAY,new Date()); 
        
        for(int i=0; i<receiptRecords.size(); i++){
            InventoryReceiptDO receiptDO = (InventoryReceiptDO)receiptRecords.get(i);
            DataSet set = new DataSet();
            
            NumberObject orderNum = new NumberObject(NumberObject.Type.INTEGER);
            DateObject dateRec = new DateObject();
            NumberObject invItemId = new NumberObject(NumberObject.Type.INTEGER);
            StringObject invItem = new StringObject();
            NumberObject orderItemId = new NumberObject(NumberObject.Type.INTEGER);
            NumberObject orgId = new NumberObject(NumberObject.Type.INTEGER);
            StringObject org = new StringObject();
            
            //address fields
            StringObject multUnit = new StringObject();
            StringObject streetAddress = new StringObject();
            StringObject city = new StringObject();
            StringObject state = new StringObject();
            StringObject zipCode = new StringObject();
            
            //item fields
            StringObject itemDescription = new StringObject();
            StringObject itemStore = new StringObject();
            StringObject itemPurchUnit = new StringObject();
            StringObject itemIsBulk = new StringObject();
            StringObject itemIsLotMaintained = new StringObject();
            StringObject itemIsSerialMaintained = new StringObject();
            
            //set the values
            orderNum.setValue(receiptDO.getOrderNumber());
            dateRec.setValue(todaysDate);
            invItemId.setValue(receiptDO.getInventoryItemId());
            invItem.setValue(receiptDO.getInventoryItem());
            orderItemId.setValue(receiptDO.getOrderItemId());
            orgId.setValue(receiptDO.getOrganizationId());
            org.setValue(receiptDO.getOrganization());
            multUnit.setValue(receiptDO.getOrgAddress().getMultipleUnit());
            streetAddress.setValue(receiptDO.getOrgAddress().getStreetAddress());
            city.setValue(receiptDO.getOrgAddress().getCity());
            state.setValue(receiptDO.getOrgAddress().getState());
            zipCode.setValue(receiptDO.getOrgAddress().getZipCode());
            itemDescription.setValue(receiptDO.getItemDesc());
            itemStore.setValue(receiptDO.getItemStore());
            itemPurchUnit.setValue(receiptDO.getItemPurchasedUnits());
            itemIsBulk.setValue(receiptDO.getIsBulk());
            itemIsLotMaintained.setValue(receiptDO.getIsLotMaintained());
            itemIsSerialMaintained.setValue(receiptDO.getIsSerialMaintained());            
            
            set.addObject(orderNum);
            set.addObject(dateRec);
            set.addObject(invItemId);
            set.addObject(invItem);
            set.addObject(orderItemId);
            set.addObject(orgId);
            set.addObject(org);
            set.addObject(multUnit);
            set.addObject(streetAddress);
            set.addObject(city);
            set.addObject(state);
            set.addObject(zipCode);
            set.addObject(itemDescription);
            set.addObject(itemStore);
            set.addObject(itemPurchUnit);
            set.addObject(itemIsBulk);
            set.addObject(itemIsLotMaintained);
            set.addObject(itemIsSerialMaintained);
            
            model.add(set);
        }
        
        modelObj.setValue(model);        
        
        return modelObj;
    }
    
    public ModelObject getInvItemFromUPC(StringObject upc){
        InventoryReceiptRemote remote = (InventoryReceiptRemote)EJBFactory.lookup("openelis/InventoryReceiptBean/remote");
        
        List invItems = remote.getInventoryItemsByUPC((String)upc.getValue());
        
        ModelObject modelObj = new ModelObject();
        DataModel model = new DataModel(); 
        
        for(int i=0; i<invItems.size(); i++){
            InventoryItemAutoDO invItemDO = (InventoryItemAutoDO)invItems.get(i);
            DataSet set = new DataSet();
            
            NumberObject invItemId = new NumberObject(NumberObject.Type.INTEGER);
            StringObject invItemText = new StringObject();
            
            invItemId.setValue(invItemDO.getId());
            invItemText.setValue(invItemDO.getName());
            
            set.addObject(invItemId);
            set.addObject(invItemText);
            
            model.add(set);
        }
        
        modelObj.setValue(model);        
        
        return modelObj;
    }

    public DataModel getMatches(String cat, DataModel model, String match, HashMap<String, DataObject> params) throws RPCException {
        if(cat.equals("location"))
            return getLocationMatches(match, params);
        else if(cat.equals("inventoryItem"))
                return getInventoryItemMatches(match);
        else if(cat.equals("organization"))
            return getOrganizationMatches(match);

        return null;
    }
    
    private DataModel getLocationMatches(String match, HashMap params) throws RPCException{
        //InventoryItemRemote remote = (InventoryItemRemote)EJBFactory.lookup("openelis/InventoryItemBean/remote");
        DataModel dataModel = new DataModel();
        String addToExisting = null;
        List autoCompleteList = new ArrayList();
        
        addToExisting = (String)((CheckField)params.get("addToExisting")).getValue();
        
        if("Y".equals(addToExisting)){
            InventoryReceiptRemote remote = (InventoryReceiptRemote)EJBFactory.lookup("openelis/InventoryReceiptBean/remote");
            //lookup by name
            autoCompleteList = remote.autoCompleteLocationLookupByName(match+"%", 10);
        }else{
            StorageLocationRemote remote = (StorageLocationRemote)EJBFactory.lookup("openelis/StorageLocationBean/remote");
            //lookup by name
            autoCompleteList = remote.autoCompleteLookupByName(match+"%", 10);    
        }        
        
        for(int i=0; i < autoCompleteList.size(); i++){
            StorageLocationAutoDO resultDO = (StorageLocationAutoDO) autoCompleteList.get(i);
            //id
            Integer id = resultDO.getId();
            //desc
            String desc = resultDO.getLocation();
          
            DataSet set = new DataSet();
            //hidden id
            NumberObject idObject = new NumberObject(id);
            set.setKey(idObject);
            //columns
            StringObject descObject = new StringObject();
            descObject.setValue(desc);
            set.addObject(descObject);
            
            //add the dataset to the datamodel
            dataModel.add(set);                            

        }       
        
        return dataModel;       
    }
    
    private DataModel getInventoryItemMatches(String match){
        InventoryItemRemote remote = (InventoryItemRemote)EJBFactory.lookup("openelis/InventoryItemBean/remote");
        DataModel dataModel = new DataModel();
        List autoCompleteList;

        autoCompleteList = remote.inventoryItemStoreLocAutoCompleteLookupByName(match+"%", 10, false);
        
        for(int i=0; i < autoCompleteList.size(); i++){
            InventoryItemAutoDO resultDO = (InventoryItemAutoDO) autoCompleteList.get(i);
            
            Integer itemId = resultDO.getId();
            String name = resultDO.getName();
            String store = resultDO.getStore();
            String desc = resultDO.getDescription();
            String purUnits = resultDO.getPurchasedUnits();
            
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
            StringObject descObj = new StringObject();
            descObj.setValue(desc);
            data.addObject(descObj);
            StringObject purUnitsObj = new StringObject();
            purUnitsObj.setValue(purUnits);
            data.addObject(purUnitsObj);
            
            //add the dataset to the datamodel
            dataModel.add(data);                            
        }       
        
        return dataModel;           
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
    
    private List getInventoryReceiptsFromTable(TableModel receiptsTable){
        List inventoryReceipts = new ArrayList();
        
        for(int i=0; i<receiptsTable.numRows(); i++){
            InventoryReceiptDO receiptDO = new InventoryReceiptDO();
            TableRow row = receiptsTable.getRow(i);

            DropDownField storageLocation = (DropDownField)row.getHidden(InventoryReceiptMeta.INVENTORY_LOCATION_META.getStorageLocationId());
            StringField lotNum = (StringField)row.getHidden(InventoryReceiptMeta.INVENTORY_LOCATION_META.getLotNumber());
            DateField expDate = (DateField)row.getHidden(InventoryReceiptMeta.INVENTORY_LOCATION_META.getExpirationDate());
            CheckField addToExisting = (CheckField)row.getHidden("addToExisting");
            StringField deleteFlag = (StringField)row.getHidden("deleteFlag");
            StringField itemIsBulk = (StringField)row.getHidden("itemIsBulk");
            StringField itemIsLotMaintained = (StringField)row.getHidden("itemIsLotMaintained");
            StringField itemIsSerialMaintained = (StringField)row.getHidden("itemIsSerialMaintained");
            NumberField orderItemId = (NumberField)row.getHidden("orderItemId");
            
            if(deleteFlag == null){
                receiptDO.setDelete(false);
            }else{
                receiptDO.setDelete("Y".equals(deleteFlag.getValue()));
            }
            //if the user created the row and clicked the remove button before commit...
            //we dont need to do anything with that row
            if(deleteFlag != null && "Y".equals(deleteFlag.getValue()) && receiptDO.getId() == null){
                //do nothing
            }else{
                receiptDO.setReceivedDate(((DatetimeRPC)((DateField)row.getColumn(1)).getValue()).getDate());
                receiptDO.setUpc((String)((StringField)row.getColumn(2)).getValue());
                receiptDO.setInventoryItemId((Integer)((DropDownField)row.getColumn(3)).getValue());
                receiptDO.setOrganizationId((Integer)((DropDownField)row.getColumn(4)).getValue());
                receiptDO.setQuantityReceived((Integer)((NumberField)row.getColumn(5)).getValue());
                receiptDO.setUnitCost((Double)((NumberField)row.getColumn(6)).getValue());
                receiptDO.setQcReference((String)((StringField)row.getColumn(7)).getValue());
                receiptDO.setExternalReference((String)((StringField)row.getColumn(8)).getValue());                
                
                if(storageLocation != null)
                    receiptDO.setStorageLocationId((Integer)storageLocation.getValue());
                if(lotNum != null)
                    receiptDO.setLotNumber((String)lotNum.getValue());
                if(expDate != null)
                    receiptDO.setExpDate(((DatetimeRPC)expDate.getValue()).getDate());
                if(itemIsBulk != null)
                    receiptDO.setIsBulk((String)itemIsBulk.getValue());
                if(itemIsLotMaintained != null)
                    receiptDO.setIsLotMaintained((String)itemIsLotMaintained.getValue());
                if(itemIsSerialMaintained != null)
                    receiptDO.setIsSerialMaintained((String)itemIsSerialMaintained.getValue());
                if(orderItemId != null)
                    receiptDO.setOrderItemId((Integer)orderItemId.getValue());
                
                if(addToExisting != null){
                    String addToExistingValue = (String)addToExisting.getValue();
                    if(addToExistingValue != null && "Y".equals(addToExistingValue))
                        receiptDO.setAddToExisting(true);
                    else
                        receiptDO.setAddToExisting(false);
                }
                
                inventoryReceipts.add(receiptDO);    
            }
        }
        
        return inventoryReceipts;
    }
    
    private void setRpcErrors(List exceptionList, TableModel receiptsTable, FormRPC rpcSend){
        //we need to get the keys and look them up in the resource bundle for internationalization
        for (int i=0; i<exceptionList.size();i++) {
            //if the error is inside the org contacts table
            if(exceptionList.get(i) instanceof TableFieldErrorException){
                TableRow row = receiptsTable.getRow(((TableFieldErrorException)exceptionList.get(i)).getRowIndex());
                row.getColumn(receiptsTable.getColumnIndexByFieldName(((TableFieldErrorException)exceptionList.get(i)).getFieldName()))
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
}
