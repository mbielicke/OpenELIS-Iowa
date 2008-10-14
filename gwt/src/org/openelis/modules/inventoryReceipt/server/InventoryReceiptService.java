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
package org.openelis.modules.inventoryReceipt.server;


import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.openelis.domain.InventoryItemAutoDO;
import org.openelis.domain.InventoryReceiptDO;
import org.openelis.domain.OrganizationAutoDO;
import org.openelis.domain.OrganizationContactDO;
import org.openelis.domain.StorageLocationAutoDO;
import org.openelis.gwt.common.DatetimeRPC;
import org.openelis.gwt.common.FieldErrorException;
import org.openelis.gwt.common.FormErrorException;
import org.openelis.gwt.common.FormRPC;
import org.openelis.gwt.common.LastPageException;
import org.openelis.gwt.common.QueryException;
import org.openelis.gwt.common.RPCException;
import org.openelis.gwt.common.TableFieldErrorException;
import org.openelis.gwt.common.FormRPC.Status;
import org.openelis.gwt.common.data.AbstractField;
import org.openelis.gwt.common.data.CheckField;
import org.openelis.gwt.common.data.DataMap;
import org.openelis.gwt.common.data.DataModel;
import org.openelis.gwt.common.data.DataObject;
import org.openelis.gwt.common.data.DataSet;
import org.openelis.gwt.common.data.DateField;
import org.openelis.gwt.common.data.DropDownField;
import org.openelis.gwt.common.data.ModelObject;
import org.openelis.gwt.common.data.NumberField;
import org.openelis.gwt.common.data.NumberObject;
import org.openelis.gwt.common.data.StringField;
import org.openelis.gwt.common.data.StringObject;
import org.openelis.gwt.common.data.TableField;
import org.openelis.gwt.server.ServiceUtils;
import org.openelis.gwt.services.AppScreenFormServiceInt;
import org.openelis.gwt.services.AutoCompleteServiceInt;
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

public class InventoryReceiptService implements AppScreenFormServiceInt, AutoCompleteServiceInt {

    private static final InventoryReceiptMetaMap InventoryReceiptMeta = new InventoryReceiptMetaMap();
    
    private static final int leftTableRowsPerPage = 250;
    
    private UTFResource openElisConstants = UTFResource.getBundle((String)SessionManager.getSession().getAttribute("locale"));
    
    public DataModel commitQuery(FormRPC rpcSend, DataModel model) throws RPCException {
        List receipts;
        //if the rpc is null then we need to get the page
        if(rpcSend == null){

            FormRPC rpc = (FormRPC)SessionManager.getSession().getAttribute("InventoryReceiptQuery");
    
            if(rpc == null)
                throw new QueryException(openElisConstants.getString("queryExpiredException"));

            InventoryReceiptRemote remote = (InventoryReceiptRemote)EJBFactory.lookup("openelis/InventoryReceiptBean/remote");
            try{
                receipts = remote.query(rpc.getFieldMap(), (model.getPage()*leftTableRowsPerPage), leftTableRowsPerPage+1);
            }catch(Exception e){
                if(e instanceof LastPageException){
                    throw new LastPageException(openElisConstants.getString("lastPageException"));
                }else{
                    throw new RPCException(e.getMessage()); 
                }           
            }    
        }else{
            InventoryReceiptRemote remote = (InventoryReceiptRemote)EJBFactory.lookup("openelis/InventoryReceiptBean/remote");
        
            HashMap<String,AbstractField> fields = rpcSend.getFieldMap();
            fields.remove("receiptsTable");
            
            if(isQueryEmpty(rpcSend))
                throw new QueryException(openElisConstants.getString("emptyQueryException"));
           
            try{    
                receipts = remote.query(fields,0,leftTableRowsPerPage);
    
            }catch(Exception e){
                throw new RPCException(e.getMessage());
            }
    
        
            //need to save the rpc used to the encache
            SessionManager.getSession().setAttribute("InventoryReceiptQuery", rpcSend);
        }
        
        if(model == null)
            model = new DataModel();
        else
            model.clear();
        
        //fill the model with the query results
        fillModelFromQuery(model, receipts);
 
        return model;
    }
    
    public ModelObject commitQueryAndLock(ModelObject modelObj) throws RPCException {
        List receipts;
        DataModel model = (DataModel)modelObj.getValue();
        FormRPC rpc = (FormRPC)SessionManager.getSession().getAttribute("InventoryReceiptQuery");

        if(rpc == null)
            throw new QueryException(openElisConstants.getString("queryExpiredException"));

        InventoryReceiptRemote remote = (InventoryReceiptRemote)EJBFactory.lookup("openelis/InventoryReceiptBean/remote");
        try{
            receipts = remote.queryAndLock(rpc.getFieldMap(), (model.getPage()*leftTableRowsPerPage), leftTableRowsPerPage+1);
        }catch(Exception e){
            if(e instanceof LastPageException){
                throw new LastPageException(openElisConstants.getString("lastPageException"));
            }else{
                throw new RPCException(e.getMessage()); 
            }           
        }    
        
        //fill the model with the query results
        fillModelFromQuery(model, receipts);
        modelObj.setValue(model);
        
        return modelObj;
    }
    
    public ModelObject commitQueryAndUnlock(ModelObject modelObj) throws RPCException {
        List receipts;
        DataModel model = (DataModel)modelObj.getValue();
        FormRPC rpc = (FormRPC)SessionManager.getSession().getAttribute("InventoryReceiptQuery");

        if(rpc == null)
            throw new QueryException(openElisConstants.getString("queryExpiredException"));

        InventoryReceiptRemote remote = (InventoryReceiptRemote)EJBFactory.lookup("openelis/InventoryReceiptBean/remote");
        try{
            receipts = remote.queryAndUnlock(rpc.getFieldMap(), (model.getPage()*leftTableRowsPerPage), leftTableRowsPerPage+1);
        }catch(Exception e){
            if(e instanceof LastPageException){
                throw new LastPageException(openElisConstants.getString("lastPageException"));
            }else{
                throw new RPCException(e.getMessage()); 
            }           
        }    
        
        //fill the model with the query results
        fillModelFromQuery(model, receipts);
        modelObj.setValue(model);
 
        return modelObj;
    }


    public FormRPC commitAdd(FormRPC rpcSend, FormRPC rpcReturn) throws RPCException {
        //remote interface to call the inventory receipt bean
        InventoryReceiptRemote remote = (InventoryReceiptRemote)EJBFactory.lookup("openelis/InventoryReceiptBean/remote");
        List inventoryReceipts = new ArrayList();
        
        //receipts table
        TableField recieptsTableField = (TableField)rpcSend.getField("receiptsTable");
        DataModel receiptsModel = (DataModel)recieptsTableField.getValue();
        
        //build the inventory receipts list DO from the form
        inventoryReceipts = getInventoryReceiptsFromTable(receiptsModel);
        
        //validate the fields on the backend
        List exceptionList = remote.validateForAdd(inventoryReceipts);
        
        if(exceptionList.size() > 0){
            setRpcErrors(exceptionList, recieptsTableField, rpcSend);
            return rpcSend;
        } 
        
        //send the changes to the database
        try{
            remote.updateInventoryReceipt(inventoryReceipts);
        }catch(Exception e){
            System.out.println(e.getMessage());
            exceptionList = new ArrayList();
            exceptionList.add(e);
            
            setRpcErrors(exceptionList, recieptsTableField, rpcSend);
            
            return rpcSend;
        }

        return rpcSend;
    }

    public FormRPC commitUpdate(FormRPC rpcSend, FormRPC rpcReturn) throws RPCException {
//      remote interface to call the inventory receipt bean
        InventoryReceiptRemote remote = (InventoryReceiptRemote)EJBFactory.lookup("openelis/InventoryReceiptBean/remote");
        List inventoryReceipts = new ArrayList();
        
        //receipts table
        TableField receiptsTableField = (TableField)rpcSend.getField("receiptsTable");
        DataModel receiptsModel = (DataModel)receiptsTableField.getValue();
        
        //build the inventory receipts list DO from the form
        inventoryReceipts = getInventoryReceiptsFromTable(receiptsModel);
        
        //validate the fields on the backend
        List exceptionList = remote.validateForAdd(inventoryReceipts);
        
        if(exceptionList.size() > 0){
            setRpcErrors(exceptionList, receiptsTableField, rpcSend);
            return rpcSend;
        } 
        
        //send the changes to the database
        try{
            remote.updateInventoryReceipt(inventoryReceipts);
        }catch(Exception e){
            System.out.println(e.getMessage());
            exceptionList = new ArrayList();
            exceptionList.add(e);
            
            setRpcErrors(exceptionList, receiptsTableField, rpcSend);
            
            return rpcSend;
        }

        return rpcSend;
    }

    public FormRPC commitDelete(DataSet key, FormRPC rpcReturn) throws RPCException {
        return null;
    }

    public FormRPC abort(DataSet key, FormRPC rpcReturn) throws RPCException {
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
            
            NumberField orderNumber = new NumberField(NumberObject.Type.INTEGER);
            DateField receivedDate = new DateField();
            StringField upc = new StringField();
            DropDownField inventoryItem = new DropDownField();
            DropDownField org = new DropDownField();
            NumberField qtyReceived = new NumberField(NumberObject.Type.INTEGER);
            NumberField qtyRequested = new NumberField(NumberObject.Type.INTEGER);
            NumberField cost = new NumberField(NumberObject.Type.DOUBLE);
            StringField qc = new StringField();
            StringField extRef = new StringField();
            StringField multUnit = new StringField();
            StringField streetAddress = new StringField();
            StringField city = new StringField();
            StringField state = new StringField();
            StringField zipCode = new StringField();
            StringField itemDesc = new StringField();
            StringField itemStore = new StringField();
            StringField itemDispensedUnit = new StringField();
            StringField itemIsBulk = new StringField();
            StringField itemIsLotMaintained = new StringField();
            StringField itemIsSerialMaintained = new StringField();
            NumberField orderItemId = new NumberField(NumberObject.Type.INTEGER);
            CheckField addToExisting = new CheckField();
            DropDownField inventoryLocation = new DropDownField();
            StringField lotNumber = new StringField();
            DateField expDate = new DateField();
            NumberField receiptId = new NumberField(NumberObject.Type.INTEGER);
            
            orderNumber.setValue(receiptDO.getOrderNumber());
            if(receiptDO.getReceivedDate() != null && receiptDO.getReceivedDate().getDate() != null)
                receivedDate.setValue(DatetimeRPC.getInstance(Datetime.YEAR, Datetime.DAY, receiptDO.getReceivedDate().getDate()));
            else
                receivedDate.setValue(todaysDate);
            
            upc.setValue(receiptDO.getUpc());
            
            //inventory item set
            DataSet inventoryItemSet = new DataSet();
            NumberObject itemId = new NumberObject(NumberObject.Type.INTEGER);
            StringObject itemText = new StringObject();
            itemId.setValue(receiptDO.getInventoryItemId());
            itemText.setValue(receiptDO.getInventoryItem());            
            inventoryItemSet.setKey(itemId);
            inventoryItemSet.add(itemText);
            inventoryItem.setValue(inventoryItemSet);
            
            //org set
            DataSet orgSet = new DataSet();
            NumberObject orgId = new NumberObject(NumberObject.Type.INTEGER);
            StringObject orgText = new StringObject();
            orgId.setValue(receiptDO.getOrganizationId());
            orgText.setValue(receiptDO.getOrganization());
            orgSet.setKey(orgId);
            orgSet.add(orgText);
            org.setValue(orgSet);
            
            qtyReceived.setValue(receiptDO.getQuantityReceived());
            qtyRequested.setValue(receiptDO.getItemQtyRequested());
            cost.setValue(receiptDO.getUnitCost());
            qc.setValue(receiptDO.getQcReference());
            extRef.setValue(receiptDO.getExternalReference());
            multUnit.setValue(receiptDO.getOrgAddress().getMultipleUnit());
            streetAddress.setValue(receiptDO.getOrgAddress().getStreetAddress());
            city.setValue(receiptDO.getOrgAddress().getCity());
            state.setValue(receiptDO.getOrgAddress().getState());
            zipCode.setValue(receiptDO.getOrgAddress().getZipCode());
            itemDesc.setValue(receiptDO.getItemDesc());
            itemStore.setValue(receiptDO.getItemStore());
            itemDispensedUnit.setValue(receiptDO.getItemDispensedUnits());
            itemIsBulk.setValue(receiptDO.getIsBulk());
            itemIsLotMaintained.setValue(receiptDO.getIsLotMaintained());
            itemIsSerialMaintained.setValue(receiptDO.getIsSerialMaintained());
            orderItemId.setValue(receiptDO.getOrderItemId());
            
            //FIXME dont want to default for now...
            //addToExisting.setValue(receiptDO.getIsBulk());
            addToExisting.setValue("N");
            
            //inventory location set
            if(receiptDO.getStorageLocationId() != null){
                DataSet inventoryLocSet = new DataSet();
                NumberObject invLocId = new NumberObject(NumberObject.Type.INTEGER);
                StringObject invLocText = new StringObject();
                invLocId.setValue(receiptDO.getStorageLocationId());
                invLocText.setValue(receiptDO.getStorageLocation());            
                inventoryLocSet.setKey(invLocId);
                inventoryLocSet.add(invLocText);
                inventoryLocation.setValue(inventoryLocSet);
            }else{
                inventoryLocation.setValue(null);    
            }
            
            
            lotNumber.setValue(receiptDO.getLotNumber());
            if(receiptDO.getExpDate() != null && receiptDO.getExpDate().getDate() != null)
                expDate.setValue(DatetimeRPC.getInstance(Datetime.YEAR, Datetime.DAY, receiptDO.getExpDate().getDate()));
            
            receiptId.setValue(receiptDO.getId());            
            
           // row.setKey(id);         
            set.add(orderNumber);
            set.add(receivedDate);
            set.add(upc);
            set.add(inventoryItem);
            set.add(org);
            set.add(qtyReceived);
            set.add(qtyRequested);
            set.add(cost);
            set.add(qc);
            set.add(extRef);
            set.add(multUnit);
            set.add(streetAddress);
            set.add(city);
            set.add(state);
            set.add(zipCode);
            set.add(itemDesc);
            set.add(itemStore);
            set.add(itemDispensedUnit);
            set.add(itemIsBulk);
            set.add(itemIsLotMaintained);
            set.add(itemIsSerialMaintained);
            set.add(orderItemId);
            set.add(addToExisting);
            set.add(inventoryLocation);
            set.add(lotNumber);
            set.add(expDate);
            set.add(receiptId);
            
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
            
            Integer itemId = invItemDO.getId();
            String name = invItemDO.getName();
            String store = invItemDO.getStore();
            String desc = invItemDO.getDescription();
            //String purUnits = invItemDO.getPurchasedUnits();
            String itemIsBulk = invItemDO.getIsBulk();
            String itemIsLotMaintained = invItemDO.getIsLotMaintained();
            String itemIsSerialMaintained = invItemDO.getIsSerialMaintained();
            
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
            StringObject descObj = new StringObject();
            descObj.setValue(desc);
            data.add(descObj);
            //StringObject purUnitsObj = new StringObject();
            //purUnitsObj.setValue(purUnits);
            //data.addObject(purUnitsObj);
            StringObject isBulkObj = new StringObject();
            isBulkObj.setValue(itemIsBulk);
            data.add(isBulkObj);
            StringObject isLotMaintainedObj = new StringObject();
            isLotMaintainedObj.setValue(itemIsLotMaintained);
            data.add(isLotMaintainedObj);            
            StringObject isSerialMaintainedObj = new StringObject();
            isSerialMaintainedObj.setValue(itemIsSerialMaintained);
            data.add(isSerialMaintainedObj);
            
            model.add(data);
        }
        
        modelObj.setValue(model);        
        
        return modelObj;
    }

    public ModelObject getMatchesObj(StringObject cat, ModelObject model, StringObject match, DataMap params) throws RPCException {
        return new ModelObject(getMatches((String)cat.getValue(), (DataModel)model.getValue(), (String)match.getValue(), (HashMap)params.getValue()));
        
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
            set.add(descObject);
            
            //add the dataset to the datamodel
            dataModel.add(set);                            

        }       
        
        return dataModel;       
    }
    
    private DataModel getInventoryItemMatches(String match){
        InventoryItemRemote remote = (InventoryItemRemote)EJBFactory.lookup("openelis/InventoryItemBean/remote");
        DataModel dataModel = new DataModel();
        List autoCompleteList;
        
        String parsedMatch = match.replace('*', '%');

        autoCompleteList = remote.inventoryItemStoreAutoCompleteLookupByName(parsedMatch+"%", 10, true, true);
        
        for(int i=0; i < autoCompleteList.size(); i++){
            InventoryItemAutoDO resultDO = (InventoryItemAutoDO) autoCompleteList.get(i);
            
            Integer itemId = resultDO.getId();
            String name = resultDO.getName();
            String store = resultDO.getStore();
            String desc = resultDO.getDescription();
            //String purUnits = resultDO.getPurchasedUnits();
            String itemIsBulk = resultDO.getIsBulk();
            String itemIsLotMaintained = resultDO.getIsLotMaintained();
            String itemIsSerialMaintained = resultDO.getIsSerialMaintained();
            
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
            StringObject descObj = new StringObject();
            descObj.setValue(desc);
            data.add(descObj);
            //StringObject purUnitsObj = new StringObject();
            //purUnitsObj.setValue(purUnits);
            //data.addObject(purUnitsObj);
            StringObject isBulkObj = new StringObject();
            isBulkObj.setValue(itemIsBulk);
            data.add(isBulkObj);
            StringObject isLotMaintainedObj = new StringObject();
            isLotMaintainedObj.setValue(itemIsLotMaintained);
            data.add(isLotMaintainedObj);            
            StringObject isSerialMaintainedObj = new StringObject();
            isSerialMaintainedObj.setValue(itemIsSerialMaintained);
            data.add(isSerialMaintainedObj);
            
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
            StringObject aptSuiteObj = new StringObject();
            aptSuiteObj.setValue(aptSuite);
            data.add(aptSuiteObj);
            StringObject zipCodeObj = new StringObject();
            zipCodeObj.setValue(zipCode);
            data.add(zipCodeObj);
            
            
            //add the dataset to the datamodel
            dataModel.add(data);                            
        }       
        
        return dataModel;           
    }
    
    private List getInventoryReceiptsFromTable(DataModel receiptsTable){
        List inventoryReceipts = new ArrayList();
        List deletedRows = receiptsTable.getDeletions();
        
        for(int i=0; i<receiptsTable.size(); i++){
            InventoryReceiptDO receiptDO = new InventoryReceiptDO();
            DataSet row = receiptsTable.get(i);

            DataMap map = (DataMap)row.getData();
            DropDownField storageLocation = (DropDownField)map.get(InventoryReceiptMeta.TRANS_RECEIPT_LOCATION_META.INVENTORY_LOCATION_META.getStorageLocationId());
            StringField lotNum = (StringField)map.get(InventoryReceiptMeta.TRANS_RECEIPT_LOCATION_META.INVENTORY_LOCATION_META.getLotNumber());
            DateField expDate = (DateField)map.get(InventoryReceiptMeta.TRANS_RECEIPT_LOCATION_META.INVENTORY_LOCATION_META.getExpirationDate());
            CheckField addToExisting = (CheckField)map.get("addToExisting");
            StringField itemIsBulk = (StringField)map.get("itemIsBulk");
            StringField itemIsLotMaintained = (StringField)map.get("itemIsLotMaintained");
            StringField itemIsSerialMaintained = (StringField)map.get("itemIsSerialMaintained");
            NumberField orderItemId = (NumberField)map.get("orderItemId");
            StringField invalidOrderId = (StringField)map.get("invalidOrderId");
            NumberField id = (NumberField)map.get("id");
            NumberField transReceiptOrderId = (NumberField)map.get("transReceiptOrderId");
            
            receiptDO.setOrderNumber((Integer)((NumberField)row.get(0)).getValue());
            receiptDO.setReceivedDate(((DatetimeRPC)((DateField)row.get(1)).getValue()).getDate());
            receiptDO.setUpc((String)((StringField)row.get(2)).getValue());
            receiptDO.setInventoryItemId((Integer)((DropDownField)row.get(3)).getValue());
            receiptDO.setOrganizationId((Integer)((DropDownField)row.get(4)).getValue());
            receiptDO.setQuantityReceived((Integer)((NumberField)row.get(5)).getValue());
            receiptDO.setItemQtyRequested((Integer)((NumberField)row.get(6)).getValue());
            receiptDO.setUnitCost((Double)((NumberField)row.get(7)).getValue());
            receiptDO.setQcReference((String)((StringField)row.get(8)).getValue());
            receiptDO.setExternalReference((String)((StringField)row.get(9)).getValue());                
            
            if(storageLocation != null && storageLocation.getValue() != null)
                receiptDO.setStorageLocationId((Integer)storageLocation.getValue());
            if(lotNum != null && !"".equals(lotNum.getValue()))
                receiptDO.setLotNumber((String)lotNum.getValue());
            if(expDate != null && expDate.getValue() != null)
                receiptDO.setExpDate(((DatetimeRPC)expDate.getValue()).getDate());
            if(itemIsBulk != null)
                receiptDO.setIsBulk((String)itemIsBulk.getValue());
            if(itemIsLotMaintained != null)
                receiptDO.setIsLotMaintained((String)itemIsLotMaintained.getValue());
            if(itemIsSerialMaintained != null)
                receiptDO.setIsSerialMaintained((String)itemIsSerialMaintained.getValue());
            if(orderItemId != null)
                receiptDO.setOrderItemId((Integer)orderItemId.getValue());
            if(invalidOrderId != null)
                receiptDO.setOrderNumber(new Integer(-1));
            if(id != null)
                receiptDO.setId((Integer)id.getValue());
            if(transReceiptOrderId != null)
                receiptDO.setTransReceiptOrderId((Integer)transReceiptOrderId.getValue());
            
            if(addToExisting != null){
                String addToExistingValue = (String)addToExisting.getValue();
                if(addToExistingValue != null && "Y".equals(addToExistingValue))
                    receiptDO.setAddToExisting(true);
                else
                    receiptDO.setAddToExisting(false);
            }
            
            inventoryReceipts.add(receiptDO);    
        }
        
        for(int j=0; j<deletedRows.size(); j++){
            DataSet deletedRow = (DataSet)deletedRows.get(j);
            if(deletedRow.getKey() != null){
                InventoryReceiptDO receiptDO = new InventoryReceiptDO();
                receiptDO.setDelete(true);
                receiptDO.setId((Integer)((NumberObject)deletedRow.getKey()).getValue());
                
                inventoryReceipts.add(receiptDO);
            }
        }
        
        return inventoryReceipts;
    }
    
    private void setRpcErrors(List exceptionList, TableField receiptsTable, FormRPC rpcSend){
        for (int i=0; i<exceptionList.size();i++) {
            //if the error is inside the org contacts table
            if(exceptionList.get(i) instanceof TableFieldErrorException){
                int rowindex = ((TableFieldErrorException)exceptionList.get(i)).getRowIndex();
                receiptsTable.getField(rowindex,((TableFieldErrorException)exceptionList.get(i)).getFieldName())
                    .addError(openElisConstants.getString(((FieldErrorException)exceptionList.get(i)).getMessage()));

            //if the error is on the field
            }else if(exceptionList.get(i) instanceof FieldErrorException)
                rpcSend.getField(((FieldErrorException)exceptionList.get(i)).getFieldName()).addError(openElisConstants.getString(((FieldErrorException)exceptionList.get(i)).getMessage()));
            
            //if the error is on the entire form
            else if(exceptionList.get(i) instanceof FormErrorException)
                rpcSend.addError(openElisConstants.getString(((FormErrorException)exceptionList.get(i)).getMessage()));
            }        
        
        rpcSend.status = Status.invalid;
    }
    
    private boolean isQueryEmpty(FormRPC rpc){

        return ("".equals(rpc.getFieldValue(InventoryReceiptMeta.ORDER_ITEM_META.ORDER_META.getId())) &&
                    "".equals(rpc.getFieldValue(InventoryReceiptMeta.getReceivedDate())) && 
                    "".equals(rpc.getFieldValue(InventoryReceiptMeta.getUpc())) && 
                    "".equals(rpc.getFieldValue(InventoryReceiptMeta.INVENTORY_ITEM_META.getName())) && 
                    "".equals(rpc.getFieldValue(InventoryReceiptMeta.ORGANIZATION_META.getName())) && 
                    "".equals(rpc.getFieldValue(InventoryReceiptMeta.getQuantityReceived())) && 
                    "".equals(rpc.getFieldValue(InventoryReceiptMeta.getUnitCost())) && 
                    "".equals(rpc.getFieldValue(InventoryReceiptMeta.getQcReference())) && 
                    "".equals(rpc.getFieldValue(InventoryReceiptMeta.getExternalReference())));
    }
    
    private void fillModelFromQuery(DataModel model, List receipts){
        int i=0;
        model.clear();
        while(i < receipts.size() && i < leftTableRowsPerPage) {
            InventoryReceiptDO resultDO = (InventoryReceiptDO)receipts.get(i);
 
            DataSet row = new DataSet();
            
            NumberField orderNumber = new NumberField(NumberObject.Type.INTEGER);
            DateField receivedDate = new DateField();
            StringField upc = new StringField();
            DropDownField inventoryItem = new DropDownField();
            DropDownField org = new DropDownField();
            NumberField qtyReceived = new NumberField(NumberObject.Type.INTEGER);
            NumberField qtyRequested = new NumberField(NumberObject.Type.INTEGER);
            NumberField cost = new NumberField(NumberObject.Type.DOUBLE);
            StringField qc = new StringField();
            StringField extRef = new StringField();
            StringField multUnit = new StringField();
            StringField streetAddress = new StringField();
            StringField city = new StringField();
            StringField state = new StringField();
            StringField zipCode = new StringField();
            StringField itemDesc = new StringField();
            StringField itemStore = new StringField();
            StringField itemDisUnit = new StringField();
            StringField itemIsBulk = new StringField();
            StringField itemIsLotMaintained = new StringField();
            StringField itemIsSerialMaintained = new StringField();
            NumberField orderItemId = new NumberField(NumberObject.Type.INTEGER);
            CheckField addToExisting = new CheckField();
            DropDownField inventoryLocation = new DropDownField();
            StringField lotNumber = new StringField();
            DateField expDate = new DateField();
            NumberField receiptId = new NumberField(NumberObject.Type.INTEGER);
            NumberField transReceiptOrder = new NumberField(NumberObject.Type.INTEGER);
            
            orderNumber.setValue(resultDO.getOrderNumber());
            receivedDate.setValue(DatetimeRPC.getInstance(Datetime.YEAR, Datetime.DAY, resultDO.getReceivedDate().getDate()));
            upc.setValue(resultDO.getUpc());
            
            //inventory item set
            DataSet inventoryItemSet = new DataSet();
            NumberObject itemId = new NumberObject(NumberObject.Type.INTEGER);
            StringObject itemText = new StringObject();
            itemId.setValue(resultDO.getInventoryItemId());
            itemText.setValue(resultDO.getInventoryItem());            
            inventoryItemSet.setKey(itemId);
            inventoryItemSet.add(itemText);
            inventoryItem.setValue(inventoryItemSet);
            
            //org set
            DataSet orgSet = new DataSet();
            NumberObject orgId = new NumberObject(NumberObject.Type.INTEGER);
            StringObject orgText = new StringObject();
            orgId.setValue(resultDO.getOrganizationId());
            orgText.setValue(resultDO.getOrganization());
            orgSet.setKey(orgId);
            orgSet.add(orgText);
            org.setValue(orgSet);
            
            qtyReceived.setValue(resultDO.getQuantityReceived());
            qtyRequested.setValue(resultDO.getItemQtyRequested());
            cost.setValue(resultDO.getUnitCost());
            qc.setValue(resultDO.getQcReference());
            extRef.setValue(resultDO.getExternalReference());
            multUnit.setValue(resultDO.getOrgAddress().getMultipleUnit());
            streetAddress.setValue(resultDO.getOrgAddress().getStreetAddress());
            city.setValue(resultDO.getOrgAddress().getCity());
            state.setValue(resultDO.getOrgAddress().getState());
            zipCode.setValue(resultDO.getOrgAddress().getZipCode());
            itemDesc.setValue(resultDO.getItemDesc());
            itemStore.setValue(resultDO.getItemStore());
            itemDisUnit.setValue(resultDO.getItemDispensedUnits());
            itemIsBulk.setValue(resultDO.getIsBulk());
            itemIsLotMaintained.setValue(resultDO.getIsLotMaintained());
            itemIsSerialMaintained.setValue(resultDO.getIsSerialMaintained());
            orderItemId.setValue(resultDO.getOrderItemId());
            addToExisting.setValue(resultDO.getIsBulk());
            transReceiptOrder.setValue(resultDO.getTransReceiptOrderId());
            
            //inventory location set
            DataSet inventoryLocSet = new DataSet();
            NumberObject invLocId = new NumberObject(NumberObject.Type.INTEGER);
            StringObject invLocText = new StringObject();
            invLocId.setValue(resultDO.getStorageLocationId());
            invLocText.setValue(resultDO.getStorageLocation());            
            inventoryLocSet.setKey(invLocId);
            inventoryLocSet.add(invLocText);
            inventoryLocation.setValue(inventoryLocSet);
            
            lotNumber.setValue(resultDO.getLotNumber());
            if(resultDO.getExpDate().getDate() != null)
                expDate.setValue(DatetimeRPC.getInstance(Datetime.YEAR, Datetime.DAY, resultDO.getExpDate().getDate()));
            
            receiptId.setValue(resultDO.getId());            
            
           // row.setKey(id);         
            row.add(orderNumber);
            row.add(receivedDate);
            row.add(upc);
            row.add(inventoryItem);
            row.add(org);
            row.add(qtyReceived);
            row.add(qtyRequested);
            row.add(cost);
            row.add(qc);
            row.add(extRef);
            row.add(multUnit);
            row.add(streetAddress);
            row.add(city);
            row.add(state);
            row.add(zipCode);
            row.add(itemDesc);
            row.add(itemStore);
            row.add(itemDisUnit);
            row.add(itemIsBulk);
            row.add(itemIsLotMaintained);
            row.add(itemIsSerialMaintained);
            row.add(orderItemId);
            row.add(addToExisting);
            row.add(inventoryLocation);
            row.add(lotNumber);
            row.add(expDate);
            row.add(receiptId);
            row.add(transReceiptOrder);
            
            model.add(row);
            i++;
        } 
    }
}
