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
import org.openelis.domain.StorageLocationAutoDO;
import org.openelis.gwt.common.DatetimeRPC;
import org.openelis.gwt.common.FieldErrorException;
import org.openelis.gwt.common.Form;
import org.openelis.gwt.common.FormErrorException;
import org.openelis.gwt.common.LastPageException;
import org.openelis.gwt.common.QueryException;
import org.openelis.gwt.common.RPCException;
import org.openelis.gwt.common.TableFieldErrorException;
import org.openelis.gwt.common.ValidationErrorsList;
import org.openelis.gwt.common.data.AbstractField;
import org.openelis.gwt.common.data.CheckField;
import org.openelis.gwt.common.data.DataModel;
import org.openelis.gwt.common.data.DataSet;
import org.openelis.gwt.common.data.DateField;
import org.openelis.gwt.common.data.DoubleField;
import org.openelis.gwt.common.data.DropDownField;
import org.openelis.gwt.common.data.Field;
import org.openelis.gwt.common.data.FieldType;
import org.openelis.gwt.common.data.IntegerField;
import org.openelis.gwt.common.data.StringField;
import org.openelis.gwt.common.data.StringObject;
import org.openelis.gwt.common.data.TableField;
import org.openelis.gwt.server.ServiceUtils;
import org.openelis.gwt.services.AppScreenFormServiceInt;
import org.openelis.gwt.services.AutoCompleteServiceInt;
import org.openelis.metamap.InventoryReceiptMetaMap;
import org.openelis.modules.inventoryReceipt.client.InvReceiptItemInfoForm;
import org.openelis.modules.inventoryReceipt.client.InvReceiptItemInfoRPC;
import org.openelis.modules.inventoryReceipt.client.InvReceiptOrgKey;
import org.openelis.modules.inventoryReceipt.client.InventoryReceiptRPC;
import org.openelis.modules.inventoryReceipt.client.ReceiptInvItemKey;
import org.openelis.modules.inventoryReceipt.client.ReceiptInvLocationAutoRPC;
import org.openelis.persistence.EJBFactory;
import org.openelis.remote.InventoryItemRemote;
import org.openelis.remote.InventoryReceiptRemote;
import org.openelis.remote.OrganizationRemote;
import org.openelis.remote.StorageLocationRemote;
import org.openelis.server.constants.Constants;
import org.openelis.util.Datetime;
import org.openelis.util.FormInitUtil;
import org.openelis.util.SessionManager;
import org.openelis.util.UTFResource;
import org.openelis.util.XMLUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class InventoryReceiptService implements AppScreenFormServiceInt<InventoryReceiptRPC, InvReceiptItemInfoRPC>, AutoCompleteServiceInt {

    private static final InventoryReceiptMetaMap InventoryReceiptMeta = new InventoryReceiptMetaMap();
    
    private static final int leftTableRowsPerPage = 250;
    private static Node subRpcNode;
    
    private UTFResource openElisConstants = UTFResource.getBundle((String)SessionManager.getSession().getAttribute("locale"));
    
    public DataModel<InvReceiptItemInfoRPC> commitQuery(Form form, DataModel<InvReceiptItemInfoRPC> model) throws RPCException {
        List receipts;
        String type;
        //if the rpc is null then we need to get the page
        if(form == null){

            form = (Form)SessionManager.getSession().getAttribute("InventoryReceiptQuery");
            //get screen type
            type = (String)form.getFieldValue("type");
            HashMap<String,AbstractField> fields = form.getFieldMap();
            fields.remove("type");
            
            if(form == null)
                throw new QueryException(openElisConstants.getString("queryExpiredException"));

            InventoryReceiptRemote remote = (InventoryReceiptRemote)EJBFactory.lookup("openelis/InventoryReceiptBean/remote");
            try{
                receipts = remote.query(fields, (model.getPage()*leftTableRowsPerPage), leftTableRowsPerPage+1, (InventoryReceiptRemote.RECEIPT.equals(type)));
            }catch(Exception e){
                if(e instanceof LastPageException){
                    throw new LastPageException(openElisConstants.getString("lastPageException"));
                }else{
                    throw new RPCException(e.getMessage()); 
                }           
            }    
            fields.put("type", new StringField(type));
        }else{
            //get screen type
            type = (String)form.getFieldValue("type");
            
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
    
            fields.put("type", new StringField(type));
            //need to save the rpc used to the encache
            SessionManager.getSession().setAttribute("InventoryReceiptQuery", form);
        }
        
        if(model == null)
            model = new DataModel<InvReceiptItemInfoRPC>();
        else
            model.clear();
        
        //fill the model with the query results
        fillModelFromQuery(model, receipts, type);
        
        return model;
    }
    
    public InventoryReceiptRPC commitQueryAndLock(InventoryReceiptRPC rpc) throws RPCException {
        List receipts;
        Form form = (Form)SessionManager.getSession().getAttribute("InventoryReceiptQuery");

        //get screen type
        String type = (String)form.getFieldValue("type");
        HashMap<String,AbstractField> fields = form.getFieldMap();
        fields.remove("type");
        
        if(form == null)
            throw new QueryException(openElisConstants.getString("queryExpiredException"));

        InventoryReceiptRemote remote = (InventoryReceiptRemote)EJBFactory.lookup("openelis/InventoryReceiptBean/remote");
        try{
            receipts = remote.queryAndLock(fields, (rpc.tableRows.getPage()*leftTableRowsPerPage), leftTableRowsPerPage+1, (InventoryReceiptRemote.RECEIPT.equals(type)));
        }catch(Exception e){
            if(e instanceof LastPageException){
                throw new LastPageException(openElisConstants.getString("lastPageException"));
            }else{
                throw new RPCException(e.getMessage()); 
            }           
        }
        
        fields.put("type", new StringField(type));
        
        //fill the model with the query results
        fillModelFromQuery(rpc.tableRows, receipts, type);
        
        return rpc;
    }
    
    public InventoryReceiptRPC commitQueryAndUnlock(InventoryReceiptRPC rpc) throws RPCException {
        List receipts;
        Form form = (Form)SessionManager.getSession().getAttribute("InventoryReceiptQuery");

        //get screen type
        String type = (String)form.getFieldValue("type");
        HashMap<String,AbstractField> fields = form.getFieldMap();
        fields.remove("type");
        
        if(form == null)
            throw new QueryException(openElisConstants.getString("queryExpiredException"));

        InventoryReceiptRemote remote = (InventoryReceiptRemote)EJBFactory.lookup("openelis/InventoryReceiptBean/remote");
        try{
            receipts = remote.queryAndUnlock(fields, (rpc.tableRows.getPage()*leftTableRowsPerPage), leftTableRowsPerPage+1, (InventoryReceiptRemote.RECEIPT.equals(type)));
        }catch(Exception e){
            if(e instanceof LastPageException){
                throw new LastPageException(openElisConstants.getString("lastPageException"));
            }else{
                throw new RPCException(e.getMessage()); 
            }           
        }    
        
        fields.put("type", new StringField(type));
        
        //fill the model with the query results
        fillModelFromQuery(rpc.tableRows, receipts, type);

        return rpc;
    }


    public InventoryReceiptRPC commitAdd(InventoryReceiptRPC rpc) throws RPCException {
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

    public InventoryReceiptRPC commitUpdate(InventoryReceiptRPC rpc) throws RPCException {
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

    public InventoryReceiptRPC commitDelete(InventoryReceiptRPC rpc) throws RPCException {
        return null;
    }

    public InventoryReceiptRPC abort(InventoryReceiptRPC rpc) throws RPCException {
        return null;
    }

    public InventoryReceiptRPC fetch(InventoryReceiptRPC rpc) throws RPCException {
        return null;
    }

    public InventoryReceiptRPC fetchForUpdate(InventoryReceiptRPC rpc) throws RPCException {
        return null;
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
    
    public InventoryReceiptRPC getScreen(InventoryReceiptRPC rpc) throws RPCException{
        String type = rpc.screenType;
        
        if(type.equals(InventoryReceiptRemote.RECEIPT))
            rpc.xml = ServiceUtils.getXML(Constants.APP_ROOT+"/Forms/inventoryReceipt.xsl");
        else if(type.equals(InventoryReceiptRemote.TRANSFER))
            rpc.xml = ServiceUtils.getXML(Constants.APP_ROOT+"/Forms/inventoryTransfer.xsl");
        
        if(subRpcNode == null){
            try{
                Document xml = XMLUtil.parse(rpc.xml);
                NodeList rpcNodes = (NodeList)xml.getElementsByTagName("rpc");
                int i=0;
                while(i<rpcNodes.getLength() && !"itemInformation".equals(rpcNodes.item(i).getAttributes().getNamedItem("key").getNodeValue()))
                    i++;
                
                if(i<rpcNodes.getLength())
                    subRpcNode = rpcNodes.item(i);
        
            }catch(Exception e){
                System.out.println(e.getMessage());
            }
        }
        
        return rpc;
    }
    
    public InventoryReceiptRPC getReceipts(InventoryReceiptRPC rpc) {
        InventoryReceiptRemote remote = (InventoryReceiptRemote)EJBFactory.lookup("openelis/InventoryReceiptBean/remote");
        
        List receiptRecords = remote.getInventoryReceiptRecords(rpc.orderId);
        
        DataModel<InvReceiptItemInfoRPC> model = new DataModel<InvReceiptItemInfoRPC>(); 
        fillModelFromQuery(model, receiptRecords, "receipt");
        rpc.tableRows = model;
        
        return rpc;
    }
    
    public InventoryReceiptRPC getInvItemFromUPC(InventoryReceiptRPC rpc){
        InventoryReceiptRemote remote = (InventoryReceiptRemote)EJBFactory.lookup("openelis/InventoryReceiptBean/remote");
        
        List invItems = remote.getInventoryItemsByUPC(rpc.upcValue);
        
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
        rpc.invItemsByUPC = model;
        return rpc;
    }

    public ReceiptInvLocationAutoRPC getMatchesCall(ReceiptInvLocationAutoRPC rpc) throws RPCException {
        if("location".equals(rpc.cat))
            rpc.autoMatches = getLocationMatches(rpc.match, rpc.addToExisting, rpc.invItemId);
        else if("toInventoryItemTrans".equals(rpc.cat))
            rpc.autoMatches = getInventoryItemMatches(rpc.match, false, rpc.invItemId);
        else
            return null;
        
        return rpc;
    }
    
    
    public DataModel getMatches(String cat, DataModel model, String match, HashMap<String, FieldType> params) throws RPCException {
        if(cat.equals("inventoryItem"))
            return getInventoryItemMatches(match, false, -1);
        else if(cat.equals("inventoryItemTrans"))
            return getInventoryItemMatches(match, true, -1);
        else if(cat.equals("organization"))
            return getOrganizationMatches(match);

        return null;
    }
    
    private DataModel getLocationMatches(String match, String addToExisting, Integer inventoryItemId) throws RPCException{
        //InventoryItemRemote remote = (InventoryItemRemote)EJBFactory.lookup("openelis/InventoryItemBean/remote");
        DataModel<Integer> dataModel = new DataModel<Integer>();
        List autoCompleteList = new ArrayList();
        
        if("Y".equals(addToExisting)){
            //TODO needs inv item id
            InventoryReceiptRemote remote = (InventoryReceiptRemote)EJBFactory.lookup("openelis/InventoryReceiptBean/remote");
            //lookup by name
            autoCompleteList = remote.autoCompleteLocationLookupByNameInvId(match+"%", inventoryItemId, 10);
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
    
    private DataModel<Integer> getInventoryItemMatches(String match, boolean loc, Integer inventoryItemId) throws RPCException{
        InventoryItemRemote remote = (InventoryItemRemote)EJBFactory.lookup("openelis/InventoryItemBean/remote");
        Integer parentId = null;
        if(inventoryItemId == null)
                throw new FormErrorException(openElisConstants.getString("inventoryTransferFromItemException"));

        DataModel<Integer> dataModel = new DataModel<Integer>();
        List autoCompleteList;
        
        String parsedMatch = match.replace('*', '%');

        if(loc)
            autoCompleteList = remote.inventoryItemStoreLocAutoCompleteLookupByName(parsedMatch+"%", 10, false, true);    
        else if(inventoryItemId.compareTo(new Integer(-1)) > 0)
            autoCompleteList = remote.inventoryItemStoreChildAutoCompleteLookupByName(parsedMatch+"%", parentId, 10);
        else
            autoCompleteList = remote.inventoryItemStoreAutoCompleteLookupByName(parsedMatch+"%", 10, true, true); //this one
        
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
                IntegerField qtyOnHandObj = new IntegerField(qtyOnHand);
                data.add(qtyOnHandObj);
            }
            
            ReceiptInvItemKey setData = new ReceiptInvItemKey();
            setData.desc = desc;
            setData.isBulk = itemIsBulk;
            setData.isLotMaintained = itemIsLotMaintained;
            setData.isSerialMaintained = itemIsSerialMaintained;
            setData.dispensedUnits = dispensedUnits;
            if(loc){
                setData.locId = locationId;
                setData.lotNum = resultDO.getLotNum();
                if(resultDO.getExpDate() != null && resultDO.getExpDate().getDate() != null)
                    setData.expDate = DatetimeRPC.getInstance(Datetime.YEAR, Datetime.DAY, resultDO.getExpDate().getDate());
            }
            
            setData.parentRatio = resultDO.getParentRatio();
            
            data.setData(setData);
            
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
            InvReceiptOrgKey orgKey = new InvReceiptOrgKey();
            orgKey.aptSuite = aptSuite;
            orgKey.zipCode = zipCode;
            data.setData(orgKey);
            
            //add the dataset to the datamodel
            dataModel.add(data);                            
        }       
        
        return dataModel;           
    }
    
    private List getInventoryReceiptsFromTable(DataModel<InvReceiptItemInfoRPC> receiptsTable, boolean transfer){
        List inventoryReceipts = new ArrayList();
        List deletedRows = receiptsTable.getDeletions();
        
        for(int i=0; i<receiptsTable.size(); i++){
            InventoryReceiptDO receiptDO = new InventoryReceiptDO();
            DataSet<InvReceiptItemInfoRPC> row = (DataSet<InvReceiptItemInfoRPC>)receiptsTable.get(i);

            InvReceiptItemInfoRPC hiddenRpc = row.getKey();
            /*
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
            */
            
            if(transfer){
                receiptDO.setFromInventoryItemId((Integer)((DropDownField)row.get(0)).getSelectedKey());
                receiptDO.setFromInventoryItem((String)((DropDownField)row.get(0)).getTextValue());
                receiptDO.setFromQtyOnHand((Integer)((Field)row.get(2)).getValue());
                receiptDO.setInventoryItemId((Integer)((DropDownField)row.get(3)).getSelectedKey());
                receiptDO.setInventoryItem((String)((DropDownField)row.get(3)).getTextValue());
                //if(storageLocation != null && storageLocation.getSelectedKey() != null)
                 //   receiptDO.setFromStorageLocationId((Integer)storageLocation.getSelectedKey());
                receiptDO.setStorageLocationId((Integer)((DropDownField)row.get(5)).getSelectedKey());
                receiptDO.setQuantityReceived((Integer)((Field)row.get(6)).getValue());
                //if(parentRatio != null)
                //    receiptDO.setToParentRatio(parentRatio.getIntegerValue());
            }else{
                receiptDO.setOrderNumber(((IntegerField)row.get(0)).getValue());
                receiptDO.setReceivedDate(((DatetimeRPC)((DateField)row.get(1)).getValue()).getDate());
                receiptDO.setUpc((String)((StringField)row.get(2)).getValue());
                receiptDO.setInventoryItemId((Integer)((DropDownField<Integer>)row.get(3)).getSelectedKey());
                receiptDO.setOrganizationId((Integer)((DropDownField<Integer>)row.get(4)).getSelectedKey());
                receiptDO.setQuantityReceived(((IntegerField)row.get(5)).getValue());
                receiptDO.setItemQtyRequested(((IntegerField)row.get(6)).getValue());
                receiptDO.setUnitCost(((DoubleField)row.get(7)).getValue());
                receiptDO.setQcReference(((StringField)row.get(8)).getValue());
                receiptDO.setExternalReference(((StringField)row.get(9)).getValue());                
                
                //if(storageLocation != null && storageLocation.getSelectedKey() != null)
                receiptDO.setStorageLocationId((Integer)hiddenRpc.form.storageLocationId.getSelectedKey());
                //if(lotNum != null && !"".equals(lotNum.getValue()))
                receiptDO.setLotNumber(hiddenRpc.form.lotNumber.getValue());
                if(hiddenRpc.form.expirationDate != null && hiddenRpc.form.expirationDate.getValue() != null)
                    receiptDO.setExpDate(((DatetimeRPC)hiddenRpc.form.expirationDate.getValue()).getDate());
                receiptDO.setIsBulk(hiddenRpc.itemIsBulk);
                receiptDO.setIsLotMaintained(hiddenRpc.itemIsLotMaintained);
                receiptDO.setIsSerialMaintained(hiddenRpc.itemIsSerialMaintained);
                receiptDO.setOrderItemId(hiddenRpc.orderItemId);
                //if(invalidOrderId != null)
                //    receiptDO.setOrderNumber(new Integer(-1));
                receiptDO.setId(hiddenRpc.receiptId);
                //if(transReceiptOrderId != null)
               //     receiptDO.setTransReceiptOrderId((Integer)transReceiptOrderId.getValue());
                
                if("Y".equals(hiddenRpc.form.addToExisting.getValue()))
                    receiptDO.setAddToExisting(true);
                else
                    receiptDO.setAddToExisting(false);
            }
            
            inventoryReceipts.add(receiptDO);
  
        }
        
        for(int j=0; j<deletedRows.size(); j++){
            DataSet<InvReceiptItemInfoRPC> deletedRow = (DataSet<InvReceiptItemInfoRPC>)deletedRows.get(j);
            if(deletedRow.getKey() != null){
                InvReceiptItemInfoRPC hiddenRPC = deletedRow.getKey();
                InventoryReceiptDO receiptDO = new InventoryReceiptDO();
                receiptDO.setDelete(true);
                receiptDO.setId(hiddenRPC.receiptId);
                
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
    
    //this method needs to fill the receipts table
    //the key needs to be the subform
    private void fillModelFromQuery(DataModel<InvReceiptItemInfoRPC> model, List receipts, String screenType){
        model.paged = false;
        //receipt code
        //ord #
        //date rec
        //upc
        //inv item
        //org
        //# rec
        //# req
        //cost
        //ext QC
        //ext ref
        
        //transfer code
        //From Item
        //From Loc
        //On Hand
        //To Item
        //Ext
        //To Loc
        //Qty
        
        int i=0;
        model.clear();
        DatetimeRPC todaysDate = DatetimeRPC.getInstance(Datetime.YEAR,Datetime.DAY,new Date());
        while(i < receipts.size() && i < leftTableRowsPerPage) {
            InventoryReceiptDO resultDO = (InventoryReceiptDO)receipts.get(i);
            
            if("receipt".equals(screenType)){
                DataSet<InvReceiptItemInfoRPC> set = new DataSet<InvReceiptItemInfoRPC>();
                //ord #
                set.add(new IntegerField(resultDO.getOrderNumber()));
                //date rec
                DateField dateRec = new DateField();
                if(resultDO.getReceivedDate() != null)
                    dateRec.setValue(DatetimeRPC.getInstance(Datetime.YEAR, Datetime.DAY, resultDO.getReceivedDate().getDate()));
                else
                    dateRec.setValue(todaysDate);
                set.add(dateRec);
                //upc
                set.add(new StringField(resultDO.getUpc()));
                //inv item
                DropDownField<Integer> invItem = new DropDownField<Integer>();
                DataModel<Integer> invItemModel = new DataModel<Integer>();
                invItemModel.add(new DataSet<Integer>(resultDO.getInventoryItemId(),new StringObject(resultDO.getInventoryItem())));
                invItem.setModel(invItemModel);
                invItem.setValue(invItemModel.get(0));
                set.add(invItem);
                //org
                DropDownField<Integer> org = new DropDownField<Integer>();
                if(resultDO.getOrganization() != null){
                    DataModel<Integer> orgModel = new DataModel<Integer>();
                    orgModel.add(new DataSet<Integer>(resultDO.getOrganizationId(),new StringObject(resultDO.getOrganization())));
                    org.setModel(orgModel);
                    org.setValue(orgModel.get(0));
                }
                set.add(org);
                //# rec
                set.add(new IntegerField(resultDO.getQuantityReceived()));
                //# req
                set.add(new IntegerField(resultDO.getItemQtyRequested()));
                //cost
                set.add(new DoubleField(resultDO.getUnitCost()));
                //ext QC
                set.add(new StringField(resultDO.getQcReference()));
                //ext ref
                set.add(new StringField(resultDO.getExternalReference()));
                
                //setup the subform
                InvReceiptItemInfoRPC keyRPC = new InvReceiptItemInfoRPC();
                InvReceiptItemInfoForm keyForm = new InvReceiptItemInfoForm();
                FormInitUtil.setForm(keyForm, subRpcNode);
                keyRPC.form = keyForm;
                keyRPC.disableInvItem = true;
                keyRPC.disableOrderId = true;
                keyRPC.disableOrg = true;
                keyRPC.disableUpc = true;
                keyRPC.receiptId = resultDO.getId();
                keyRPC.orderItemId = resultDO.getOrderItemId();
                
                keyRPC.form.multUnit.setValue(resultDO.getOrgAddress().getMultipleUnit());
                keyRPC.form.streetAddress.setValue(resultDO.getOrgAddress().getStreetAddress());
                keyRPC.form.city.setValue(resultDO.getOrgAddress().getCity());
                keyRPC.form.state.setValue(resultDO.getOrgAddress().getState());
                keyRPC.form.zipCode.setValue(resultDO.getOrgAddress().getZipCode());
                
                keyRPC.form.description.setValue(resultDO.getItemDesc());
                keyRPC.form.dispensedUnits.setValue(resultDO.getItemDispensedUnits());
                keyRPC.form.storeId.setValue(resultDO.getItemStore());
                
                keyRPC.form.addToExisting.setValue(resultDO.getIsBulk());
                keyRPC.form.lotNumber.setValue(resultDO.getLotNumber());
                
                if(resultDO.getExpDate() != null && resultDO.getExpDate().getDate() != null)
                    keyRPC.form.expirationDate.setValue(DatetimeRPC.getInstance(Datetime.YEAR, Datetime.DAY, resultDO.getExpDate().getDate()));
                
                if(resultDO.getStorageLocationId() != null){
                    DataModel<Integer> locModel = new DataModel<Integer>();
                    locModel.add(new DataSet<Integer>(resultDO.getStorageLocationId(),new StringObject(resultDO.getStorageLocation())));
                    keyRPC.form.storageLocationId.setModel(locModel);
                    keyRPC.form.storageLocationId.setValue(locModel.get(0));
                }
                
                set.setKey(keyRPC);
                
                model.add(set);
                
            }else if("transfer".equals(screenType)){
                DataSet<InvReceiptItemInfoRPC> set = new DataSet<InvReceiptItemInfoRPC>();
                //From Item
                if(resultDO.getFromInventoryItemId() != null){
                    DropDownField<Integer> fromInvItem = new DropDownField<Integer>();
                    DataModel<Integer> fromInvItemModel = new DataModel<Integer>();
                    fromInvItemModel.add(new DataSet<Integer>(resultDO.getFromInventoryItemId(),new StringObject(resultDO.getFromInventoryItem())));
                    fromInvItem.setModel(fromInvItemModel);
                    fromInvItem.setValue(fromInvItemModel.get(0));
                    set.add(fromInvItem);
                    
                    //From loc
                    //TODO label for now but should be auto complete later...maybe
                    set.add(new StringField(resultDO.getFromStorageLocation()));
                    
                    //DataModel<Integer> fromInvLocModel = new DataModel<Integer>();
                    //fromInvLocModel.add(new DataSet<Integer>(resultDO.getFromStorageLocationId(),new StringObject(resultDO.getFromStorageLocation())));
                    //rowData.fromInvLoc = fromInvLocModel;
                    //rowData.itemDesc = resultDO.getFromItemDesc();
                }
                //On Hand
                set.add(new IntegerField(resultDO.getFromQtyOnHand()));
                //To Item
                DropDownField<Integer> invItem = new DropDownField<Integer>();
                DataModel<Integer> invItemModel = new DataModel<Integer>();
                invItemModel.add(new DataSet<Integer>(resultDO.getInventoryItemId(),new StringObject(resultDO.getInventoryItem())));
                invItem.setModel(invItemModel);
                invItem.setValue(invItemModel.get(0));
                set.add(invItem);
                //Ext
                set.add(new CheckField(resultDO.getIsBulk()));
                
                //To Loc
                DropDownField<Integer> loc = new DropDownField<Integer>();
                if(resultDO.getStorageLocationId() != null){
                    DataModel<Integer> locModel = new DataModel<Integer>();
                    locModel.add(new DataSet<Integer>(resultDO.getStorageLocationId(),new StringObject(resultDO.getStorageLocation())));
                    loc.setModel(locModel);
                    loc.setValue(locModel.get(0));
                }
                set.add(loc);
                
                //Qty
                set.add(new IntegerField(resultDO.getQuantityReceived()));
                
              //TODO need to add the key/data which would be the ids and the subform below
                model.add(set);
            }
 
            //DataSetWithItemForm row = new DataSetWithItemForm();
            //InventoryReceiptTableRowRPC rowData = new InventoryReceiptTableRowRPC();
            //InvReceiptItemInfoForm subForm = new InvReceiptItemInfoForm();
            //row.invItemInformationForm = subForm;
            /*
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
            */
            /*rowData.orderNumber = resultDO.getOrderNumber();
            
            if(resultDO.getReceivedDate() != null)
                rowData.receivedDate = DatetimeRPC.getInstance(Datetime.YEAR, Datetime.DAY, resultDO.getReceivedDate().getDate());
            else
                rowData.receivedDate = todaysDate;
            rowData.upc = resultDO.getUpc();
            
            //inventory item set
            DataModel<Integer> invItemModel = new DataModel<Integer>();
            invItemModel.add(new DataSet<Integer>(resultDO.getInventoryItemId(),new StringObject(resultDO.getInventoryItem())));
            rowData.inventoryItem = invItemModel;
            
            //org set
            if(resultDO.getOrganization() != null){
                DataModel<Integer> orgModel = new DataModel<Integer>();
                orgModel.add(new DataSet<Integer>(resultDO.getOrganizationId(),new StringObject(resultDO.getOrganization())));
                rowData.org = orgModel;
            }
           
            rowData.qtyReceived = resultDO.getQuantityReceived();
            rowData.qtyRequested = resultDO.getItemQtyRequested();
            rowData.cost = resultDO.getUnitCost();
            rowData.qc = resultDO.getQcReference();
            rowData.extRef = resultDO.getExternalReference();
            rowData.multUnit = resultDO.getOrgAddress().getMultipleUnit();
            rowData.streetAddress = resultDO.getOrgAddress().getStreetAddress();
            rowData.city = resultDO.getOrgAddress().getCity();
            rowData.state = resultDO.getOrgAddress().getState();
            rowData.zipCode = resultDO.getOrgAddress().getZipCode();
            rowData.itemDesc = resultDO.getItemDesc();
            rowData.itemStore = resultDO.getItemStore();
            rowData.itemDisUnit = resultDO.getItemDispensedUnits();
            rowData.itemIsBulk = resultDO.getIsBulk();
            rowData.itemIsLotMaintained = resultDO.getIsLotMaintained();
            rowData.itemIsSerialMaintained = resultDO.getIsSerialMaintained();
            rowData.orderItemId = resultDO.getOrderItemId();
            rowData.addToExisting = resultDO.getIsBulk();
            rowData.qtyOnHand = resultDO.getFromQtyOnHand();
            //transReceiptOrder.setValue(resultDO.getTransReceiptOrderId());
            
            //inventory location set
            if(resultDO.getStorageLocationId() != null){
                DataModel<Integer> locModel = new DataModel<Integer>();
                locModel.add(new DataSet<Integer>(resultDO.getStorageLocationId(),new StringObject(resultDO.getStorageLocation())));
                rowData.inventoryLocation = locModel;
            }
            
            rowData.lotNumber = resultDO.getLotNumber();
            if(resultDO.getExpDate() != null && resultDO.getExpDate().getDate() != null)
                rowData.expDate = DatetimeRPC.getInstance(Datetime.YEAR, Datetime.DAY, resultDO.getExpDate().getDate());
            
            rowData.receiptId = resultDO.getId();    
            
            if(resultDO.getFromInventoryItemId() != null){
                DataModel<Integer> fromInvItemModel = new DataModel<Integer>();
                fromInvItemModel.add(new DataSet<Integer>(resultDO.getFromInventoryItemId(),new StringObject(resultDO.getFromInventoryItem())));
                rowData.fromInvItem = fromInvItemModel;
                
                DataModel<Integer> fromInvLocModel = new DataModel<Integer>();
                fromInvLocModel.add(new DataSet<Integer>(resultDO.getFromStorageLocationId(),new StringObject(resultDO.getFromStorageLocation())));
                rowData.fromInvLoc = fromInvLocModel;
                rowData.itemDesc = resultDO.getFromItemDesc();
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
               
           // row.setKey(rowData);
            
            model.add(row);*/
            i++;
        } 
    }
}