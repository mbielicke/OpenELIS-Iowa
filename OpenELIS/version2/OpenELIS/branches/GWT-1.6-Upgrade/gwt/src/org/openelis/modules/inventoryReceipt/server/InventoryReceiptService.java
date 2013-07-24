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


import org.openelis.domain.InventoryItemAutoDO;
import org.openelis.domain.InventoryReceiptDO;
import org.openelis.domain.OrganizationAutoDO;
import org.openelis.domain.StorageLocationAutoDO;
import org.openelis.gwt.common.DatetimeRPC;
import org.openelis.gwt.common.DefaultRPC;
import org.openelis.gwt.common.FieldErrorException;
import org.openelis.gwt.common.Form;
import org.openelis.gwt.common.FormErrorException;
import org.openelis.gwt.common.LastPageException;
import org.openelis.gwt.common.QueryException;
import org.openelis.gwt.common.RPC;
import org.openelis.gwt.common.RPCException;
import org.openelis.gwt.common.TableFieldErrorException;
import org.openelis.gwt.common.ValidationErrorsList;
import org.openelis.gwt.common.data.AbstractField;
import org.openelis.gwt.common.data.CheckField;
import org.openelis.gwt.common.data.Data;
import org.openelis.gwt.common.data.DataMap;
import org.openelis.gwt.common.data.DataModel;
import org.openelis.gwt.common.data.DataSet;
import org.openelis.gwt.common.data.DateField;
import org.openelis.gwt.common.data.DropDownField;
import org.openelis.gwt.common.data.Field;
import org.openelis.gwt.common.data.FieldType;
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

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class InventoryReceiptService implements AppScreenFormServiceInt<DefaultRPC, Integer>, AutoCompleteServiceInt {

    private static final InventoryReceiptMetaMap InventoryReceiptMeta = new InventoryReceiptMetaMap();
    
    private static final int leftTableRowsPerPage = 250;
    
    private UTFResource openElisConstants = UTFResource.getBundle((String)SessionManager.getSession().getAttribute("locale"));
    
    public DataModel<Integer> commitQuery(Form form, DataModel<Integer> model) throws RPCException {
        List receipts;
        //if the rpc is null then we need to get the page
        if(form == null){

            form = (Form)SessionManager.getSession().getAttribute("InventoryReceiptQuery");
            //get screen type
            String type = (String)form.getFieldValue("type");
            
            if(form == null)
                throw new QueryException(openElisConstants.getString("queryExpiredException"));

            InventoryReceiptRemote remote = (InventoryReceiptRemote)EJBFactory.lookup("openelis/InventoryReceiptBean/remote");
            try{
                receipts = remote.query(form.getFieldMap(), (model.getPage()*leftTableRowsPerPage), leftTableRowsPerPage+1, (InventoryReceiptRemote.RECEIPT.equals(type)));
            }catch(Exception e){
                if(e instanceof LastPageException){
                    throw new LastPageException(openElisConstants.getString("lastPageException"));
                }else{
                    throw new RPCException(e.getMessage()); 
                }           
            }    
        }else{
            //get screen type
            String type = (String)form.getFieldValue("type");
            
            InventoryReceiptRemote remote = (InventoryReceiptRemote)EJBFactory.lookup("openelis/InventoryReceiptBean/remote");
        
            HashMap<String,AbstractField> fields = form.getFieldMap();
            fields.remove("receiptsTable");
            fields.remove("type");
            fields.remove("label1");
            
            
            if(isQueryEmpty(form))
                throw new QueryException(openElisConstants.getString("emptyQueryException"));
           
            try{    
                receipts = remote.query(fields,0,leftTableRowsPerPage, (InventoryReceiptRemote.RECEIPT.equals(type)));
    
            }catch(Exception e){
                throw new RPCException(e.getMessage());
            }
    
        
            //need to save the rpc used to the encache
            SessionManager.getSession().setAttribute("InventoryReceiptQuery", form);
        }
        
        if(model == null)
            model = new DataModel<Integer>();
        else
            model.clear();
        
        //fill the model with the query results
        fillModelFromQuery(model, receipts);
 
        return model;
    }
    
    public DataModel<Integer> commitQueryAndLock(DataModel<Integer> model) throws RPCException {
        List receipts;
        Form form = (Form)SessionManager.getSession().getAttribute("InventoryReceiptQuery");

        //get screen type
        String type = (String)form.getFieldValue("type");
        
        if(form == null)
            throw new QueryException(openElisConstants.getString("queryExpiredException"));

        InventoryReceiptRemote remote = (InventoryReceiptRemote)EJBFactory.lookup("openelis/InventoryReceiptBean/remote");
        try{
            receipts = remote.queryAndLock(form.getFieldMap(), (model.getPage()*leftTableRowsPerPage), leftTableRowsPerPage+1, (InventoryReceiptRemote.RECEIPT.equals(type)));
        }catch(Exception e){
            if(e instanceof LastPageException){
                throw new LastPageException(openElisConstants.getString("lastPageException"));
            }else{
                throw new RPCException(e.getMessage()); 
            }           
        }    
        
        //fill the model with the query results
        fillModelFromQuery(model, receipts);
        
        return model;
    }
    
    public DataModel<Integer> commitQueryAndUnlock(DataModel<Integer> model) throws RPCException {
        List receipts;
        Form form = (Form)SessionManager.getSession().getAttribute("InventoryReceiptQuery");

        //get screen type
        String type = (String)form.getFieldValue("type");
        
        if(form == null)
            throw new QueryException(openElisConstants.getString("queryExpiredException"));

        InventoryReceiptRemote remote = (InventoryReceiptRemote)EJBFactory.lookup("openelis/InventoryReceiptBean/remote");
        try{
            receipts = remote.queryAndUnlock(form.getFieldMap(), (model.getPage()*leftTableRowsPerPage), leftTableRowsPerPage+1, (InventoryReceiptRemote.RECEIPT.equals(type)));
        }catch(Exception e){
            if(e instanceof LastPageException){
                throw new LastPageException(openElisConstants.getString("lastPageException"));
            }else{
                throw new RPCException(e.getMessage()); 
            }           
        }    
        
        //fill the model with the query results
        fillModelFromQuery(model, receipts);

        return model;
    }


    public DefaultRPC commitAdd(DefaultRPC rpc) throws RPCException {
        //remote interface to call the inventory receipt bean
        InventoryReceiptRemote remote = (InventoryReceiptRemote)EJBFactory.lookup("openelis/InventoryReceiptBean/remote");
        List inventoryReceipts = new ArrayList();
        
        //get screen type
        String type = (String)rpc.form.getFieldValue("type");
        
        //receipts table
        TableField recieptsTableField = (TableField)rpc.form.getField("receiptsTable");
        DataModel receiptsModel = (DataModel)recieptsTableField.getValue();
        
        //build the inventory receipts list DO from the form
        inventoryReceipts = getInventoryReceiptsFromTable(receiptsModel, InventoryReceiptRemote.TRANSFER.equals(type));
        
        //send the changes to the database
        try{
            if(type.equals(InventoryReceiptRemote.RECEIPT))
                remote.updateInventoryReceipt(inventoryReceipts);
            else
                remote.updateInventoryTransfer(inventoryReceipts);
            
        }catch(Exception e){
            if(e instanceof ValidationErrorsList){
                setRpcErrors(((ValidationErrorsList)e).getErrorList(), recieptsTableField, rpc.form);
                return rpc;
            }else
                throw new RPCException(e.getMessage());
        }
        
        return rpc;
    }

    public DefaultRPC commitUpdate(DefaultRPC rpc) throws RPCException {
        //remote interface to call the inventory receipt bean
        InventoryReceiptRemote remote = (InventoryReceiptRemote)EJBFactory.lookup("openelis/InventoryReceiptBean/remote");
        List inventoryReceipts = new ArrayList();
        
        //get screen type
        String type = (String)rpc.form.getFieldValue("type");
        
        //receipts table
        TableField receiptsTableField = (TableField)rpc.form.getField("receiptsTable");
        DataModel receiptsModel = (DataModel)receiptsTableField.getValue();
        
        //build the inventory receipts list DO from the form
        inventoryReceipts = getInventoryReceiptsFromTable(receiptsModel, InventoryReceiptRemote.TRANSFER.equals(type));
        
        //send the changes to the database
        try{
            remote.updateInventoryReceipt(inventoryReceipts);
            
        }catch(Exception e){
            if(e instanceof ValidationErrorsList){
                setRpcErrors(((ValidationErrorsList)e).getErrorList(), receiptsTableField, rpc.form);
                return rpc;
            }else
                throw new RPCException(e.getMessage());
        }

        return rpc;
    }

    public DefaultRPC commitDelete(DefaultRPC rpc) throws RPCException {
        return null;
    }

    public DefaultRPC abort(DefaultRPC rpc) throws RPCException {
        // TODO Auto-generated method stub
        return null;
    }

    public DefaultRPC fetch(DefaultRPC rpc) throws RPCException {
        // TODO Auto-generated method stub
        return null;
    }

    public DefaultRPC fetchForUpdate(DefaultRPC rpc) throws RPCException {
        // TODO Auto-generated method stub
        return null;
    }

    public String getXML() throws RPCException {
        return ServiceUtils.getXML(Constants.APP_ROOT+"/Forms/inventoryReceipt.xsl");
    }

    public HashMap<String, FieldType> getXMLData() throws RPCException {
        return null;
    }

    public HashMap<String, FieldType> getXMLData(HashMap<String, FieldType> args) throws RPCException {
        String type = (String)((StringObject)args.get("type")).getValue();
        HashMap<String,FieldType> returnMap = new HashMap<String,FieldType>();
        StringObject xmlString = new StringObject();
        
        if(type.equals(InventoryReceiptRemote.RECEIPT))
            xmlString.setValue(ServiceUtils.getXML(Constants.APP_ROOT+"/Forms/inventoryReceipt.xsl"));
        else if(type.equals(InventoryReceiptRemote.TRANSFER))
            xmlString.setValue(ServiceUtils.getXML(Constants.APP_ROOT+"/Forms/inventoryTransfer.xsl"));
        
        returnMap.put("xml", xmlString);
        
        return returnMap;
    }
    
    public DefaultRPC getScreen(DefaultRPC rpc) {
        return rpc;
    }
    
    public DataModel<Integer> getReceipts(NumberObject orderId){
        InventoryReceiptRemote remote = (InventoryReceiptRemote)EJBFactory.lookup("openelis/InventoryReceiptBean/remote");
        
        List receiptRecords = remote.getInventoryReceiptRecords(orderId.getIntegerValue());
        
        DataModel<Integer> model = new DataModel<Integer>(); 
        fillModelFromQuery(model, receiptRecords);

        return model;
    }
    
    public DataModel<Integer> getInvItemFromUPC(StringObject upc){
        InventoryReceiptRemote remote = (InventoryReceiptRemote)EJBFactory.lookup("openelis/InventoryReceiptBean/remote");
        
        List invItems = remote.getInventoryItemsByUPC((String)upc.getValue());
        
        DataModel<Integer> model = new DataModel<Integer>(); 
        
        for(int i=0; i<invItems.size(); i++){
            InventoryItemAutoDO invItemDO = (InventoryItemAutoDO)invItems.get(i);
            
            Integer itemId = invItemDO.getId();
            String name = invItemDO.getName();
            String store = invItemDO.getStore();
            String desc = invItemDO.getDescription();
            String disUnits = invItemDO.getDispensedUnits();
            String itemIsBulk = invItemDO.getIsBulk();
            String itemIsLotMaintained = invItemDO.getIsLotMaintained();
            String itemIsSerialMaintained = invItemDO.getIsSerialMaintained();
            
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
            StringObject descObj = new StringObject();
            descObj.setValue(desc);
            data.add(descObj);
            StringObject disUnitsObj = new StringObject();
            disUnitsObj.setValue(disUnits);
            data.add(disUnitsObj);
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
        
        return model;
    }

    public DataModel getMatchesObj(StringObject cat, DataModel model, StringObject match, DataMap params) throws RPCException {
        return getMatches((String)cat.getValue(), model, (String)match.getValue(), params);
        
    }
    
    public DataModel getMatches(String cat, DataModel model, String match, HashMap<String, FieldType> params) throws RPCException {
        if(cat.equals("location"))
            return getLocationMatches(match, params);
        else if(cat.equals("inventoryItem"))
                return getInventoryItemMatches(match, false, null);
        else if(cat.equals("inventoryItemTrans"))
            return getInventoryItemMatches(match, true, null);
        else if(cat.equals("toInventoryItemTrans"))
            return getInventoryItemMatches(match, false, params);
        else if(cat.equals("organization"))
            return getOrganizationMatches(match);

        return null;
    }
    
    private DataModel getLocationMatches(String match, HashMap params) throws RPCException{
        //InventoryItemRemote remote = (InventoryItemRemote)EJBFactory.lookup("openelis/InventoryItemBean/remote");
        DataModel<Integer> dataModel = new DataModel<Integer>();
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
          
            DataSet<Integer> set = new DataSet<Integer>();
            //hidden id
            set.setKey(id);
            //columns
            StringObject descObject = new StringObject();
            descObject.setValue(desc);
            set.add(descObject);
            
            //add the dataset to the datamodel
            dataModel.add(set);

        }       
        
        return dataModel;       
    }
    
    private DataModel<Integer> getInventoryItemMatches(String match, boolean loc, HashMap params) throws RPCException{
        InventoryItemRemote remote = (InventoryItemRemote)EJBFactory.lookup("openelis/InventoryItemBean/remote");
        Integer parentId = null;
        if(params != null){
            if(((NumberField)params.get("fromInvItemId")).getValue() == null){
                //we dont want to do anything...throw error
                throw new FormErrorException(openElisConstants.getString("inventoryTransferFromItemException"));
            }
            parentId = ((NumberField)params.get("fromInvItemId")).getIntegerValue();
        }
        DataModel<Integer> dataModel = new DataModel<Integer>();
        List autoCompleteList;
        
        String parsedMatch = match.replace('*', '%');

        if(loc)
            autoCompleteList = remote.inventoryItemStoreLocAutoCompleteLookupByName(parsedMatch+"%", 10, false, true);    
        else if(parentId != null)
            autoCompleteList = remote.inventoryItemStoreChildAutoCompleteLookupByName(parsedMatch+"%", parentId, 10);
        else
            autoCompleteList = remote.inventoryItemStoreAutoCompleteLookupByName(parsedMatch+"%", 10, true, true);
        
        for(int i=0; i < autoCompleteList.size(); i++){
            InventoryItemAutoDO resultDO = (InventoryItemAutoDO) autoCompleteList.get(i);
            
            Integer itemId = resultDO.getId();
            String name = resultDO.getName();
            String location = resultDO.getLocation();
            Integer locationId = resultDO.getLocationId();
            Integer qtyOnHand = resultDO.getQuantityOnHand();
            String store = resultDO.getStore();
            String desc = resultDO.getDescription();
            String dispensedUnits = resultDO.getDispensedUnits();
            String itemIsBulk = resultDO.getIsBulk();
            String itemIsLotMaintained = resultDO.getIsLotMaintained();
            String itemIsSerialMaintained = resultDO.getIsSerialMaintained();
            
            DataSet<Integer> data = new DataSet<Integer>();
            //hidden id
            data.setKey(itemId);
            //columns
            StringObject nameObject = new StringObject();
            nameObject.setValue(name);
            data.add(nameObject);
            StringField storeObject = new StringField();
            storeObject.setValue(store);
            data.add(storeObject);
            if(loc){
                StringField locObject = new StringField();
                locObject.setValue(location);
                data.add(locObject);
                NumberField qtyOnHandObj = new NumberField(qtyOnHand);
                data.add(qtyOnHandObj);
            }
            
            
            DataMap map = new DataMap();
            
            StringField descObj = new StringField();
            descObj.setValue(desc);
            map.put("desc", descObj);
            StringField isBulkObj = new StringField();
            isBulkObj.setValue(itemIsBulk);
            map.put("isBulk", isBulkObj);
            StringField isLotMaintainedObj = new StringField();
            isLotMaintainedObj.setValue(itemIsLotMaintained);
            map.put("isLotMaintained", isLotMaintainedObj);            
            StringField isSerialMaintainedObj = new StringField();
            isSerialMaintainedObj.setValue(itemIsSerialMaintained);
            map.put("isSerialMaintained", isSerialMaintainedObj);
            StringField dispensedUnitsObj = new StringField();
            dispensedUnitsObj.setValue(dispensedUnits);
            map.put("dispensedUnits", dispensedUnitsObj);
            if(loc){
                //loc id
                NumberObject locId = new NumberObject(locationId);
                map.put("locId", locId);
                //lot #
                StringField lotNumObj = new StringField(resultDO.getLotNum());
                map.put("lotNum", lotNumObj);
                //exp date
                DateField expDateObj = new DateField();
                if(resultDO.getExpDate() != null && resultDO.getExpDate().getDate() != null)
                    expDateObj.setValue(DatetimeRPC.getInstance(Datetime.YEAR, Datetime.DAY, resultDO.getExpDate().getDate()));
                map.put("expDate", expDateObj);
            }
            if(parentId != null){
                //parent ratio
                NumberField parentRatioObj = new NumberField(resultDO.getParentRatio());
                map.put("parentRatio", parentRatioObj);
            }
            
            data.setData(map);
            
            //add the dataset to the datamodel
            dataModel.add(data);                            
        }       
        
        return dataModel;           
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
    
    private List getInventoryReceiptsFromTable(DataModel receiptsTable, boolean transfer){
        List inventoryReceipts = new ArrayList();
        List deletedRows = receiptsTable.getDeletions();
        
        for(int i=0; i<receiptsTable.size(); i++){
            InventoryReceiptDO receiptDO = new InventoryReceiptDO();
            DataSet row = (DataSet)receiptsTable.get(i);

            DataMap map = (DataMap)row.getData();
            DropDownField storageLocation = (DropDownField)map.get(InventoryReceiptMeta.TRANS_RECEIPT_LOCATION_META.INVENTORY_LOCATION_META.getStorageLocationId());
            StringField lotNum = (StringField)map.get("lotNum");
            DateField expDate = (DateField)map.get("expDate");
            CheckField addToExisting = (CheckField)map.get("addToExisting");
            StringField itemIsBulk = (StringField)map.get("itemIsBulk");
            StringField itemIsLotMaintained = (StringField)map.get("itemIsLotMaintained");
            StringField itemIsSerialMaintained = (StringField)map.get("itemIsSerialMaintained");
            NumberField orderItemId = (NumberField)map.get("orderItemId");
            NumberField id = (NumberField)map.get("id");
            NumberField transReceiptOrderId = (NumberField)map.get("transReceiptOrderId");
            NumberField parentRatio = (NumberField)map.get("parentRatio");
            
            if(transfer){
                receiptDO.setFromInventoryItemId((Integer)((DropDownField)row.get(0)).getSelectedKey());
                receiptDO.setFromInventoryItem((String)((DropDownField)row.get(0)).getTextValue());
                receiptDO.setFromQtyOnHand((Integer)((Field)row.get(2)).getValue());
                receiptDO.setInventoryItemId((Integer)((DropDownField)row.get(3)).getSelectedKey());
                receiptDO.setInventoryItem((String)((DropDownField)row.get(3)).getTextValue());
                if(storageLocation != null && storageLocation.getSelectedKey() != null)
                    receiptDO.setFromStorageLocationId((Integer)storageLocation.getSelectedKey());
                receiptDO.setStorageLocationId((Integer)((DropDownField)row.get(5)).getSelectedKey());
                receiptDO.setQuantityReceived((Integer)((Field)row.get(6)).getValue());
                if(parentRatio != null)
                    receiptDO.setToParentRatio(parentRatio.getIntegerValue());
            }else{
                receiptDO.setOrderNumber(((NumberField)row.get(0)).getIntegerValue());
                receiptDO.setReceivedDate(((DatetimeRPC)((DateField)row.get(1)).getValue()).getDate());
                receiptDO.setUpc((String)((StringField)row.get(2)).getValue());
                receiptDO.setInventoryItemId((Integer)((DropDownField)row.get(3)).getSelectedKey());
                receiptDO.setOrganizationId((Integer)((DropDownField)row.get(4)).getSelectedKey());
                receiptDO.setQuantityReceived(((NumberField)row.get(5)).getIntegerValue());
                receiptDO.setItemQtyRequested(((NumberField)row.get(6)).getIntegerValue());
                receiptDO.setUnitCost((Double)((NumberField)row.get(7)).getValue());
                receiptDO.setQcReference((String)((StringField)row.get(8)).getValue());
                receiptDO.setExternalReference((String)((StringField)row.get(9)).getValue());                
                
                if(storageLocation != null && storageLocation.getSelectedKey() != null)
                    receiptDO.setStorageLocationId((Integer)storageLocation.getSelectedKey());
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
                    receiptDO.setOrderItemId(orderItemId.getIntegerValue());
                //if(invalidOrderId != null)
                //    receiptDO.setOrderNumber(new Integer(-1));
                if(id != null)
                    receiptDO.setId(id.getIntegerValue());
                //if(transReceiptOrderId != null)
               //     receiptDO.setTransReceiptOrderId((Integer)transReceiptOrderId.getValue());
                
                if(addToExisting != null){
                    String addToExistingValue = (String)addToExisting.getValue();
                    if(addToExistingValue != null && "Y".equals(addToExistingValue))
                        receiptDO.setAddToExisting(true);
                    else
                        receiptDO.setAddToExisting(false);
                }
            }
            
            inventoryReceipts.add(receiptDO);    
        }
        
        for(int j=0; j<deletedRows.size(); j++){
            DataSet deletedRow = (DataSet)deletedRows.get(j);
            if(deletedRow.getKey() != null){
                InventoryReceiptDO receiptDO = new InventoryReceiptDO();
                receiptDO.setDelete(true);
                receiptDO.setId(((NumberObject)deletedRow.getKey()).getIntegerValue());
                
                inventoryReceipts.add(receiptDO);
            }
        }
        
        return inventoryReceipts;
    }
    
    private void setRpcErrors(List exceptionList, TableField receiptsTable, Form form){
        for (int i=0; i<exceptionList.size();i++) {
            //if the error is inside the org contacts table
            if(exceptionList.get(i) instanceof TableFieldErrorException){
                int rowindex = ((TableFieldErrorException)exceptionList.get(i)).getRowIndex();
                receiptsTable.getField(rowindex,((TableFieldErrorException)exceptionList.get(i)).getFieldName())
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
    
    private boolean isQueryEmpty(Form rpc){
        return false;
        /*return ("".equals(rpc.getFieldValue(InventoryReceiptMeta.ORDER_ITEM_META.ORDER_META.getId())) &&
                    "".equals(rpc.getFieldValue(InventoryReceiptMeta.getReceivedDate())) && 
                    "".equals(rpc.getFieldValue(InventoryReceiptMeta.getUpc())) && 
                    "".equals(rpc.getFieldValue(InventoryReceiptMeta.INVENTORY_ITEM_META.getName())) && 
                    "".equals(rpc.getFieldValue(InventoryReceiptMeta.ORGANIZATION_META.getName())) && 
                    "".equals(rpc.getFieldValue(InventoryReceiptMeta.getQuantityReceived())) && 
                    "".equals(rpc.getFieldValue(InventoryReceiptMeta.getUnitCost())) && 
                    "".equals(rpc.getFieldValue(InventoryReceiptMeta.getQcReference())) && 
                    "".equals(rpc.getFieldValue(InventoryReceiptMeta.getExternalReference())));*/
    }
    
    private void fillModelFromQuery(DataModel<Integer> model, List receipts){
        int i=0;
        model.clear();
        DatetimeRPC todaysDate = DatetimeRPC.getInstance(Datetime.YEAR,Datetime.DAY,new Date());
        while(i < receipts.size() && i < leftTableRowsPerPage) {
            InventoryReceiptDO resultDO = (InventoryReceiptDO)receipts.get(i);
 
            DataSet<Integer> row = new DataSet<Integer>();
            
            NumberField orderNumber = new NumberField(NumberField.Type.INTEGER);
            DateField receivedDate = new DateField();
            StringField upc = new StringField();
            DropDownField inventoryItem = new DropDownField();
            DropDownField org = new DropDownField();
            NumberField qtyReceived = new NumberField(NumberField.Type.INTEGER);
            NumberField qtyRequested = new NumberField(NumberField.Type.INTEGER);
            NumberField cost = new NumberField(NumberField.Type.DOUBLE);
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
            NumberField orderItemId = new NumberField(NumberField.Type.INTEGER);
            CheckField addToExisting = new CheckField();
            DropDownField inventoryLocation = new DropDownField();
            StringField lotNumber = new StringField();
            DateField expDate = new DateField();
            NumberField receiptId = new NumberField(NumberField.Type.INTEGER);
            NumberField transReceiptOrder = new NumberField(NumberField.Type.INTEGER);
            NumberField qtyOnHand = new NumberField();
            
            orderNumber.setValue(resultDO.getOrderNumber());
            if(resultDO.getReceivedDate() != null)
                receivedDate.setValue(DatetimeRPC.getInstance(Datetime.YEAR, Datetime.DAY, resultDO.getReceivedDate().getDate()));
            else
                receivedDate.setValue(todaysDate);
            upc.setValue(resultDO.getUpc());
            
            //inventory item set
            DataModel<Integer> invItemModel = new DataModel<Integer>();
            invItemModel.add(new DataSet<Integer>(resultDO.getInventoryItemId(),new StringObject(resultDO.getInventoryItem())));
            inventoryItem.setModel(invItemModel);
            inventoryItem.setValue(invItemModel.get(0));
            
            //org set
            if(resultDO.getOrganization() != null){
                DataModel<Integer> orgModel = new DataModel<Integer>();
                orgModel.add(new DataSet<Integer>(resultDO.getOrganizationId(),new StringObject(resultDO.getOrganization())));
                org.setModel(orgModel);
                org.setValue(orgModel.get(0));
            }
            
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
            qtyOnHand.setValue(resultDO.getFromQtyOnHand());
            //transReceiptOrder.setValue(resultDO.getTransReceiptOrderId());
            
            //inventory location set
            if(resultDO.getStorageLocationId() != null){
                DataModel<Integer> locModel = new DataModel<Integer>();
                locModel.add(new DataSet<Integer>(resultDO.getStorageLocationId(),new StringObject(resultDO.getStorageLocation())));
                inventoryLocation.setModel(locModel);
                inventoryLocation.setValue(locModel.get(0));
            }
            
            lotNumber.setValue(resultDO.getLotNumber());
            if(resultDO.getExpDate() != null && resultDO.getExpDate().getDate() != null)
                expDate.setValue(DatetimeRPC.getInstance(Datetime.YEAR, Datetime.DAY, resultDO.getExpDate().getDate()));
            
            receiptId.setValue(resultDO.getId());    
            
            DropDownField<Integer> fromInvItem = new DropDownField<Integer>();
            DropDownField<Integer> fromInvLoc = new DropDownField<Integer>();
            if(resultDO.getFromInventoryItemId() != null){
                fromInvItem = new DropDownField<Integer>();
                DataModel<Integer> fromInvItemModel = new DataModel<Integer>();
                fromInvItemModel.add(new DataSet<Integer>(resultDO.getFromInventoryItemId(),new StringObject(resultDO.getFromInventoryItem())));
                fromInvItem.setModel(fromInvItemModel);
                fromInvItem.setValue(fromInvItemModel.get(0));
                
                fromInvLoc = new DropDownField<Integer>();
                DataModel<Integer> fromInvLocModel = new DataModel<Integer>();
                fromInvLocModel.add(new DataSet<Integer>(resultDO.getFromStorageLocationId(),new StringObject(resultDO.getFromStorageLocation())));
                fromInvLoc.setModel(fromInvLocModel);
                fromInvLoc.setValue(fromInvLocModel.get(0));
                
                itemDesc.setValue(resultDO.getFromItemDesc());
            }
            
            DataMap map = new DataMap();
            map.put("orderNumber", orderNumber); 
            map.put("receivedDate", receivedDate);
            map.put("upc", upc);
            map.put("inventoryItem", inventoryItem);
            map.put("org", org);
            map.put("qtyReceived", qtyReceived);
            map.put("qtyRequested", qtyRequested);
            map.put("cost", cost);
            map.put("qc", qc);
            map.put("extRef", extRef);
            map.put("multUnit", multUnit);
            map.put("streetAddress", streetAddress);
            map.put("city", city);
            map.put("state", state);
            map.put("zipCode", zipCode);
            map.put("itemDesc", itemDesc);
            map.put("itemStore", itemStore);
            map.put("itemDisUnit", itemDisUnit);
            map.put("itemIsBulk", itemIsBulk);
            map.put("itemIsLotMaintained", itemIsLotMaintained);
            map.put("itemIsSerialMaintained", itemIsSerialMaintained);
            map.put("orderItemId", orderItemId);
            map.put("addToExisting", addToExisting);
            map.put("lotNum", lotNumber);
            map.put("expDate", expDate);
            map.put("id", receiptId);
            map.put("transReceiptOrderId", transReceiptOrder);
            map.put("fromQtyOnHand", qtyOnHand);
            if(resultDO.getFromInventoryItemId() != null){
                map.put("fromInventoryItem", fromInvItem);
                map.put("toInventoryLocation", inventoryLocation);
                map.put(InventoryReceiptMeta.TRANS_RECEIPT_LOCATION_META.INVENTORY_LOCATION_META.getStorageLocationId(), fromInvLoc);
            }else
                map.put(InventoryReceiptMeta.TRANS_RECEIPT_LOCATION_META.INVENTORY_LOCATION_META.getStorageLocationId(), inventoryLocation);
                
            row.setData(map);
            
            model.add(row);
            i++;
        } 
    }
}