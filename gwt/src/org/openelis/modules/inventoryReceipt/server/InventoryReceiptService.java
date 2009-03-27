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
import org.openelis.gwt.common.FieldErrorException;
import org.openelis.gwt.common.Form;
import org.openelis.gwt.common.FormErrorException;
import org.openelis.gwt.common.LastPageException;
import org.openelis.gwt.common.Query;
import org.openelis.gwt.common.QueryException;
import org.openelis.gwt.common.RPCException;
import org.openelis.gwt.common.TableFieldErrorException;
import org.openelis.gwt.common.ValidationErrorsList;
import org.openelis.gwt.common.data.AbstractField;
import org.openelis.gwt.common.data.CheckField;
import org.openelis.gwt.common.data.DateField;
import org.openelis.gwt.common.data.DoubleField;
import org.openelis.gwt.common.data.DropDownField;
import org.openelis.gwt.common.data.Field;
import org.openelis.gwt.common.data.FieldType;
import org.openelis.gwt.common.data.IntegerField;
import org.openelis.gwt.common.data.StringField;
import org.openelis.gwt.common.data.StringObject;
import org.openelis.gwt.common.data.TableDataModel;
import org.openelis.gwt.common.data.TableDataRow;
import org.openelis.gwt.common.data.TableField;
import org.openelis.gwt.server.ServiceUtils;
import org.openelis.gwt.services.AppScreenFormServiceInt;
import org.openelis.gwt.services.AutoCompleteServiceInt;
import org.openelis.metamap.InventoryReceiptMetaMap;
import org.openelis.modules.inventoryReceipt.client.InvReceiptItemInfoForm;
import org.openelis.modules.inventoryReceipt.client.InvReceiptOrgKey;
import org.openelis.modules.inventoryReceipt.client.InventoryReceiptForm;
import org.openelis.modules.inventoryReceipt.client.InventoryReceiptQuery;
import org.openelis.modules.inventoryReceipt.client.ReceiptInvItemKey;
import org.openelis.modules.inventoryReceipt.client.ReceiptInvLocationAutoRPC;
import org.openelis.persistence.EJBFactory;
import org.openelis.remote.InventoryItemRemote;
import org.openelis.remote.InventoryReceiptRemote;
import org.openelis.remote.OrganizationRemote;
import org.openelis.remote.StorageLocationRemote;
import org.openelis.server.constants.Constants;
import org.openelis.util.Datetime;
import org.openelis.util.FormUtil;
import org.openelis.util.SessionManager;
import org.openelis.util.UTFResource;
import org.openelis.util.XMLUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class InventoryReceiptService implements AppScreenFormServiceInt<InventoryReceiptForm, InventoryReceiptQuery>, AutoCompleteServiceInt {

    private static final InventoryReceiptMetaMap InventoryReceiptMeta = new InventoryReceiptMetaMap();
    
    private static final int leftTableRowsPerPage = 250;
    private static Node subRpcNode;
    
    private UTFResource openElisConstants = UTFResource.getBundle((String)SessionManager.getSession().getAttribute("locale"));
    
    public InventoryReceiptQuery commitQuery(InventoryReceiptQuery query) throws RPCException {
        List receipts;
        /*InventoryReceiptQuery invQuery = null;
        if(qList != null)
            invQuery = (InventoryReceiptQuery)qList;
            
        //if the rpc is null then we need to get the page
        if(invQuery == null){

            invQuery = (InventoryReceiptQuery)SessionManager.getSession().getAttribute("InventoryReceiptQuery");
            //get screen type
            
            if(invQuery == null)
                throw new QueryException(openElisConstants.getString("queryExpiredException"));

            InventoryReceiptRemote remote = (InventoryReceiptRemote)EJBFactory.lookup("openelis/InventoryReceiptBean/remote");
            try{
                receipts = remote.query(invQuery, (model.getPage()*leftTableRowsPerPage), leftTableRowsPerPage+1, (InventoryReceiptRemote.RECEIPT.equals(invQuery.type)));
            }catch(Exception e){
                if(e instanceof LastPageException){
                    throw new LastPageException(openElisConstants.getString("lastPageException"));
                }else{
                    throw new RPCException(e.getMessage()); 
                }           
            }    
        }else{*/
            //get screen type
            
            InventoryReceiptRemote remote = (InventoryReceiptRemote)EJBFactory.lookup("openelis/InventoryReceiptBean/remote");
        
            if(query.fields.isEmpty())
                throw new QueryException(openElisConstants.getString("emptyQueryException"));
           
            try{    
                receipts = remote.query(query.fields,query.page*leftTableRowsPerPage,leftTableRowsPerPage, (InventoryReceiptRemote.RECEIPT.equals(query.type)));
    
            }catch(Exception e){
                throw new RPCException(e.getMessage());
            }
    
            //need to save the rpc used to the encache
            SessionManager.getSession().setAttribute("InventoryReceiptQuery", query);
        //}
        
        if(query.results == null)
            query.results = new TableDataModel<TableDataRow<InvReceiptItemInfoForm>>();
        else
            query.results.clear();
        
        //fill the model with the query results
        fillModelFromQuery(query.results, receipts, query.type);
        
        return query;
    }
    
    public InventoryReceiptForm commitQueryAndLock(InventoryReceiptForm rpc) throws RPCException {
        List receipts;
        InventoryReceiptQuery invQuery = (InventoryReceiptQuery)SessionManager.getSession().getAttribute("InventoryReceiptQuery");
        
        if(invQuery == null)
            throw new QueryException(openElisConstants.getString("queryExpiredException"));

        InventoryReceiptRemote remote = (InventoryReceiptRemote)EJBFactory.lookup("openelis/InventoryReceiptBean/remote");
        try{
            receipts = remote.queryAndLock(invQuery.fields, (rpc.tableRows.getPage()*leftTableRowsPerPage), leftTableRowsPerPage+1, (InventoryReceiptRemote.RECEIPT.equals(invQuery.type)));
        }catch(Exception e){
            if(e instanceof LastPageException){
                throw new LastPageException(openElisConstants.getString("lastPageException"));
            }else{
                throw new RPCException(e.getMessage()); 
            }           
        }
        
        
        //fill the model with the query results
        fillModelFromQuery(rpc.tableRows, receipts, invQuery.type);
        
        return rpc;
    }
    
    public InventoryReceiptForm commitQueryAndUnlock(InventoryReceiptForm rpc) throws RPCException {
        List receipts;
        InventoryReceiptQuery invQuery = (InventoryReceiptQuery)SessionManager.getSession().getAttribute("InventoryReceiptQuery");
        
        if(invQuery == null)
            throw new QueryException(openElisConstants.getString("queryExpiredException"));

        InventoryReceiptRemote remote = (InventoryReceiptRemote)EJBFactory.lookup("openelis/InventoryReceiptBean/remote");
        try{
            receipts = remote.queryAndUnlock(invQuery.fields, (rpc.tableRows.getPage()*leftTableRowsPerPage), leftTableRowsPerPage+1, (InventoryReceiptRemote.RECEIPT.equals(invQuery.type)));
        }catch(Exception e){
            if(e instanceof LastPageException){
                throw new LastPageException(openElisConstants.getString("lastPageException"));
            }else{
                throw new RPCException(e.getMessage()); 
            }           
        }    
        
        //fill the model with the query results
        fillModelFromQuery(rpc.tableRows, receipts, invQuery.type);

        return rpc;
    }


    public InventoryReceiptForm commitAdd(InventoryReceiptForm rpc) throws RPCException {
        //remote interface to call the inventory receipt bean
        InventoryReceiptRemote remote = (InventoryReceiptRemote)EJBFactory.lookup("openelis/InventoryReceiptBean/remote");
        List inventoryReceipts = new ArrayList();
        
        //get screen type
        String type = (String)rpc.type.getValue();
        
        //receipts table
        TableField<TableDataRow<InvReceiptItemInfoForm>> recieptsTableField = rpc.receiptsTable;
        TableDataModel<TableDataRow<InvReceiptItemInfoForm>> receiptsModel = recieptsTableField.getValue();
        
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
                setRpcErrors(((ValidationErrorsList)e).getErrorList(), recieptsTableField, rpc);
                return rpc;
            }else
                throw new RPCException(e.getMessage());
        }
        
        return rpc;
    }

    public InventoryReceiptForm commitUpdate(InventoryReceiptForm rpc) throws RPCException {
        //remote interface to call the inventory receipt bean
        InventoryReceiptRemote remote = (InventoryReceiptRemote)EJBFactory.lookup("openelis/InventoryReceiptBean/remote");
        List inventoryReceipts = new ArrayList();
        
        //get screen type
        String type = (String)rpc.type.getValue();
        
        //receipts table
        TableField<TableDataRow<InvReceiptItemInfoForm>> receiptsTableField = rpc.receiptsTable;
        TableDataModel<TableDataRow<InvReceiptItemInfoForm>> receiptsModel = receiptsTableField.getValue();
        
        //build the inventory receipts list DO from the form
        inventoryReceipts = getInventoryReceiptsFromTable(receiptsModel, InventoryReceiptRemote.TRANSFER.equals(type));
        
        //send the changes to the database
        try{
            remote.updateInventoryReceipt(inventoryReceipts);
            
        }catch(Exception e){
            if(e instanceof ValidationErrorsList){
                setRpcErrors(((ValidationErrorsList)e).getErrorList(), receiptsTableField, rpc);
                return rpc;
            }else
                throw new RPCException(e.getMessage());
        }

        return rpc;
    }

    public InventoryReceiptForm commitDelete(InventoryReceiptForm rpc) throws RPCException {
        return null;
    }

    public InventoryReceiptForm abort(InventoryReceiptForm rpc) throws RPCException {
        return null;
    }

    public InventoryReceiptForm fetch(InventoryReceiptForm rpc) throws RPCException {
        return null;
    }

    public InventoryReceiptForm fetchForUpdate(InventoryReceiptForm rpc) throws RPCException {
        return null;
    }
    
    public InventoryReceiptForm getScreen(InventoryReceiptForm rpc) throws RPCException{
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
    
    public InventoryReceiptForm getReceipts(InventoryReceiptForm rpc) {
        InventoryReceiptRemote remote = (InventoryReceiptRemote)EJBFactory.lookup("openelis/InventoryReceiptBean/remote");
        
        List receiptRecords = remote.getInventoryReceiptRecords(rpc.orderId);
        
        TableDataModel<TableDataRow<InvReceiptItemInfoForm>> model = new TableDataModel<TableDataRow<InvReceiptItemInfoForm>>(); 
        fillModelFromQuery(model, receiptRecords, "receipt");
        rpc.tableRows = model;
        
        return rpc;
    }
    
    public InventoryReceiptForm getInvItemFromUPC(InventoryReceiptForm rpc){
        InventoryReceiptRemote remote = (InventoryReceiptRemote)EJBFactory.lookup("openelis/InventoryReceiptBean/remote");
        
        List<InventoryItemAutoDO> invItems = remote.getInventoryItemsByUPC(rpc.upcValue);
        
        TableDataModel<TableDataRow<Integer>> model = new TableDataModel<TableDataRow<Integer>>(); 
        
        for(InventoryItemAutoDO invItemDO : invItems) {
            model.add(new TableDataRow<Integer>(invItemDO.getId(),
                                                new FieldType[] {
                                                                 new StringObject(invItemDO.getName()),
                                                                 new StringObject(invItemDO.getStore()),
                                                                 new StringObject(invItemDO.getDescription()),
                                                                 new StringObject(invItemDO.getDispensedUnits()),
                                                                 new StringObject(invItemDO.getIsBulk()),
                                                                 new StringObject(invItemDO.getIsLotMaintained()),
                                                                 new StringObject(invItemDO.getIsSerialMaintained())
                                                }
                       )
            );
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
    
    
    public TableDataModel getMatches(String cat, TableDataModel model, String match, HashMap<String, FieldType> params) throws RPCException {
        if(cat.equals("inventoryItem"))
            return getInventoryItemMatches(match, false, -1);
        else if(cat.equals("inventoryItemTrans"))
            return getInventoryItemMatches(match, true, -1);
        else if(cat.equals("organization"))
            return getOrganizationMatches(match);

        return null;
    }
    
    private TableDataModel getLocationMatches(String match, String addToExisting, Integer inventoryItemId) throws RPCException{
        //InventoryItemRemote remote = (InventoryItemRemote)EJBFactory.lookup("openelis/InventoryItemBean/remote");
        TableDataModel<TableDataRow<Integer>> dataModel = new TableDataModel<TableDataRow<Integer>>();
        List<StorageLocationAutoDO> autoCompleteList = new ArrayList();
        
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
        
        for(StorageLocationAutoDO resultDO : autoCompleteList){
            dataModel.add(new TableDataRow<Integer>(resultDO.getId(),new StringObject(resultDO.getLocation())));
        }       
        
        return dataModel;       
    }
    
    private TableDataModel<TableDataRow<Integer>> getInventoryItemMatches(String match, boolean loc, Integer inventoryItemId) throws RPCException{
        InventoryItemRemote remote = (InventoryItemRemote)EJBFactory.lookup("openelis/InventoryItemBean/remote");
        Integer parentId = null;
        if(inventoryItemId == null)
                throw new FormErrorException(openElisConstants.getString("inventoryTransferFromItemException"));

        TableDataModel<TableDataRow<Integer>> dataModel = new TableDataModel<TableDataRow<Integer>>();
        List<InventoryItemAutoDO> autoCompleteList;
        
        String parsedMatch = match.replace('*', '%');

        if(loc)
            autoCompleteList = remote.inventoryItemStoreLocAutoCompleteLookupByName(parsedMatch+"%", 10, false, true);    
        else if(inventoryItemId.compareTo(new Integer(-1)) > 0)
            autoCompleteList = remote.inventoryItemStoreChildAutoCompleteLookupByName(parsedMatch+"%", parentId, 10);
        else
            autoCompleteList = remote.inventoryItemStoreAutoCompleteLookupByName(parsedMatch+"%", 10, true, true); //this one
        
        for(InventoryItemAutoDO resultDO : autoCompleteList){
            
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
            
            TableDataRow<Integer> data = null;
            if(loc) {
                data = new TableDataRow<Integer>(itemId,
                                                 new FieldType[] {
                                                                  new StringObject(name),
                                                                  new StringField(store),
                                                                  new StringField(location),
                                                                  new IntegerField(qtyOnHand)
                                                 }
                       );
            }else {
                data = new TableDataRow<Integer>(itemId,
                                new FieldType[] {
                                                 new StringObject(name),
                                                 new StringField(store)
                                }
                        );
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
    
    private TableDataModel<TableDataRow<Integer>> getOrganizationMatches(String match){
        OrganizationRemote remote = (OrganizationRemote)EJBFactory.lookup("openelis/OrganizationBean/remote");
        TableDataModel<TableDataRow<Integer>> dataModel = new TableDataModel<TableDataRow<Integer>>();
        List<OrganizationAutoDO> autoCompleteList;
    
        try{
            int id = Integer.parseInt(match); //this will throw an exception if it isnt an id
            //lookup by id...should only bring back 1 result
            autoCompleteList = remote.autoCompleteLookupById(id);
            
        }catch(NumberFormatException e){
            //it isnt an id
            //lookup by name
            autoCompleteList = remote.autoCompleteLookupByName(match+"%", 10);
        }
        
        for(OrganizationAutoDO resultDO : autoCompleteList){
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
            
            TableDataRow<Integer> data = new TableDataRow<Integer>(orgId,
                                                                   new FieldType[] {
                                                                                    new StringObject(name),
                                                                                    new StringObject(address),
                                                                                    new StringObject(city),
                                                                                    new StringObject(state)
                                                                   }
            );
            
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
    
    private List getInventoryReceiptsFromTable(TableDataModel<TableDataRow<InvReceiptItemInfoForm>> receiptsTable, boolean transfer){
        List inventoryReceipts = new ArrayList();
        List deletedRows = receiptsTable.getDeletions();
        
        for(int i=0; i<receiptsTable.size(); i++){
            InventoryReceiptDO receiptDO = new InventoryReceiptDO();
            TableDataRow<InvReceiptItemInfoForm> row = receiptsTable.get(i);

            InvReceiptItemInfoForm hiddenRpc = row.key;
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
                receiptDO.setFromInventoryItemId((Integer)((DropDownField)row.cells[0]).getSelectedKey());
                receiptDO.setFromInventoryItem((String)((DropDownField)row.cells[0]).getTextValue());
                receiptDO.setFromQtyOnHand((Integer)((Field)row.cells[2]).getValue());
                receiptDO.setInventoryItemId((Integer)((DropDownField)row.cells[3]).getSelectedKey());
                receiptDO.setInventoryItem((String)((DropDownField)row.cells[3]).getTextValue());
                //if(storageLocation != null && storageLocation.getSelectedKey() != null)
                 //   receiptDO.setFromStorageLocationId((Integer)storageLocation.getSelectedKey());
                receiptDO.setStorageLocationId((Integer)((DropDownField)row.cells[5]).getSelectedKey());
                receiptDO.setQuantityReceived((Integer)((Field)row.cells[6]).getValue());
                //if(parentRatio != null)
                //    receiptDO.setToParentRatio(parentRatio.getIntegerValue());
            }else{
                receiptDO.setOrderNumber(((IntegerField)row.cells[0]).getValue());
                receiptDO.setReceivedDate(((DatetimeRPC)((DateField)row.cells[1]).getValue()).getDate());
                receiptDO.setUpc((String)((StringField)row.cells[2]).getValue());
                receiptDO.setInventoryItemId((Integer)((DropDownField<Integer>)row.cells[3]).getSelectedKey());
                receiptDO.setOrganizationId((Integer)((DropDownField<Integer>)row.cells[4]).getSelectedKey());
                receiptDO.setQuantityReceived(((IntegerField)row.cells[5]).getValue());
                receiptDO.setItemQtyRequested(((IntegerField)row.cells[6]).getValue());
                receiptDO.setUnitCost(((DoubleField)row.cells[7]).getValue());
                receiptDO.setQcReference(((StringField)row.cells[8]).getValue());
                receiptDO.setExternalReference(((StringField)row.cells[9]).getValue());                
                
                //if(storageLocation != null && storageLocation.getSelectedKey() != null)
                receiptDO.setStorageLocationId((Integer)hiddenRpc.storageLocationId.getSelectedKey());
                //if(lotNum != null && !"".equals(lotNum.getValue()))
                receiptDO.setLotNumber(hiddenRpc.lotNumber.getValue());
                if(hiddenRpc.expirationDate != null && hiddenRpc.expirationDate.getValue() != null)
                    receiptDO.setExpDate(((DatetimeRPC)hiddenRpc.expirationDate.getValue()).getDate());
                receiptDO.setIsBulk(hiddenRpc.itemIsBulk);
                receiptDO.setIsLotMaintained(hiddenRpc.itemIsLotMaintained);
                receiptDO.setIsSerialMaintained(hiddenRpc.itemIsSerialMaintained);
                receiptDO.setOrderItemId(hiddenRpc.orderItemId);
                //if(invalidOrderId != null)
                //    receiptDO.setOrderNumber(new Integer(-1));
                receiptDO.setId(hiddenRpc.receiptId);
                //if(transReceiptOrderId != null)
               //     receiptDO.setTransReceiptOrderId((Integer)transReceiptOrderId.getValue());
                
                if("Y".equals(hiddenRpc.addToExisting.getValue()))
                    receiptDO.setAddToExisting(true);
                else
                    receiptDO.setAddToExisting(false);
            }
            
            inventoryReceipts.add(receiptDO);
  
        }
        
        for(int j=0; j<deletedRows.size(); j++){
            TableDataRow<InvReceiptItemInfoForm> deletedRow = (TableDataRow<InvReceiptItemInfoForm>)deletedRows.get(j);
            if(deletedRow.key != null){
                InvReceiptItemInfoForm hiddenRPC = deletedRow.key;
                InventoryReceiptDO receiptDO = new InventoryReceiptDO();
                receiptDO.setDelete(true);
                receiptDO.setId(hiddenRPC.receiptId);
                
                inventoryReceipts.add(receiptDO);
            }
        }
        
        return inventoryReceipts;
    }
    
    private void setRpcErrors(List exceptionList, TableField receiptsTable, Form<? extends Object> form){
        HashMap<String,AbstractField> map = null;
        if(exceptionList.size() > 0)
            map = FormUtil.createFieldMap(form);
        for (int i=0; i<exceptionList.size();i++) {
            //if the error is inside the org contacts table
            if(exceptionList.get(i) instanceof TableFieldErrorException){
                int rowindex = ((TableFieldErrorException)exceptionList.get(i)).getRowIndex();
                receiptsTable.getField(rowindex,((TableFieldErrorException)exceptionList.get(i)).getFieldName())
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
    private void fillModelFromQuery(TableDataModel<TableDataRow<InvReceiptItemInfoForm>> model, List receipts, String screenType){
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
                TableDataRow<InvReceiptItemInfoForm> set = new TableDataRow<InvReceiptItemInfoForm>(10);
                //ord #
                set.cells[0] = new IntegerField(resultDO.getOrderNumber());
                //date rec
                DateField dateRec = new DateField();
                if(resultDO.getReceivedDate() != null)
                    dateRec.setValue(DatetimeRPC.getInstance(Datetime.YEAR, Datetime.DAY, resultDO.getReceivedDate().getDate()));
                else
                    dateRec.setValue(todaysDate);
                set.cells[1] = (dateRec);
                //upc
                set.cells[2] = (new StringField(resultDO.getUpc()));
                //inv item
                DropDownField<Integer> invItem = new DropDownField<Integer>();
                TableDataModel<TableDataRow<Integer>> invItemModel = new TableDataModel<TableDataRow<Integer>>();
                invItemModel.add(new TableDataRow<Integer>(resultDO.getInventoryItemId(),new StringObject(resultDO.getInventoryItem())));
                invItem.setModel(invItemModel);
                invItem.setValue(invItemModel.get(0));
                set.cells[3] = (invItem);
                //org
                DropDownField<Integer> org = new DropDownField<Integer>();
                if(resultDO.getOrganization() != null){
                    TableDataModel<TableDataRow<Integer>> orgModel = new TableDataModel<TableDataRow<Integer>>();
                    orgModel.add(new TableDataRow<Integer>(resultDO.getOrganizationId(),new StringObject(resultDO.getOrganization())));
                    org.setModel(orgModel);
                    org.setValue(orgModel.get(0));
                }
                set.cells[4] = (org);
                //# rec
                set.cells[5] = (new IntegerField(resultDO.getQuantityReceived()));
                //# req
                set.cells[6] = (new IntegerField(resultDO.getItemQtyRequested()));
                //cost
                set.cells[7] = (new DoubleField(resultDO.getUnitCost()));
                //ext QC
                set.cells[8] = (new StringField(resultDO.getQcReference()));
                //ext ref
                set.cells[9] = (new StringField(resultDO.getExternalReference()));
                
                //setup the subform
                InvReceiptItemInfoForm keyRPC = new InvReceiptItemInfoForm();
                InvReceiptItemInfoForm keyForm = new InvReceiptItemInfoForm();
                FormUtil.setForm(keyForm, subRpcNode);
                keyRPC = keyForm;
                keyRPC.disableInvItem = true;
                keyRPC.disableOrderId = true;
                keyRPC.disableOrg = true;
                keyRPC.disableUpc = true;
                keyRPC.receiptId = resultDO.getId();
                keyRPC.orderItemId = resultDO.getOrderItemId();
                
                keyRPC.multUnit.setValue(resultDO.getOrgAddress().getMultipleUnit());
                keyRPC.streetAddress.setValue(resultDO.getOrgAddress().getStreetAddress());
                keyRPC.city.setValue(resultDO.getOrgAddress().getCity());
                keyRPC.state.setValue(resultDO.getOrgAddress().getState());
                keyRPC.zipCode.setValue(resultDO.getOrgAddress().getZipCode());
                
                keyRPC.description.setValue(resultDO.getItemDesc());
                keyRPC.dispensedUnits.setValue(resultDO.getItemDispensedUnits());
                keyRPC.storeId.setValue(resultDO.getItemStore());
                
                keyRPC.addToExisting.setValue(resultDO.getIsBulk());
                keyRPC.lotNumber.setValue(resultDO.getLotNumber());
                
                if(resultDO.getExpDate() != null && resultDO.getExpDate().getDate() != null)
                    keyRPC.expirationDate.setValue(DatetimeRPC.getInstance(Datetime.YEAR, Datetime.DAY, resultDO.getExpDate().getDate()));
                
                if(resultDO.getStorageLocationId() != null){
                    TableDataModel<TableDataRow<Integer>> locModel = new TableDataModel<TableDataRow<Integer>>();
                    locModel.add(new TableDataRow<Integer>(resultDO.getStorageLocationId(),new StringObject(resultDO.getStorageLocation())));
                    keyRPC.storageLocationId.setModel(locModel);
                    keyRPC.storageLocationId.setValue(locModel.get(0));
                }
                
                set.key = (keyRPC);
                
                model.add(set);
                
            }else if("transfer".equals(screenType)){
                TableDataRow<InvReceiptItemInfoForm> set = new TableDataRow<InvReceiptItemInfoForm>();
                //From Item
                if(resultDO.getFromInventoryItemId() != null){
                    DropDownField<Integer> fromInvItem = new DropDownField<Integer>();
                    TableDataModel<TableDataRow<Integer>> fromInvItemModel = new TableDataModel<TableDataRow<Integer>>();
                    fromInvItemModel.add(new TableDataRow<Integer>(resultDO.getFromInventoryItemId(),new StringObject(resultDO.getFromInventoryItem())));
                    fromInvItem.setModel(fromInvItemModel);
                    fromInvItem.setValue(fromInvItemModel.get(0));
                    set.cells[0] = (fromInvItem);
                    
                    //From loc
                    //TODO label for now but should be auto complete later...maybe
                    set.cells[1] = (new StringField(resultDO.getFromStorageLocation()));
                    
                    //DataModel<Integer> fromInvLocModel = new DataModel<Integer>();
                    //fromInvLocModel.add(new DataSet<Integer>(resultDO.getFromStorageLocationId(),new StringObject(resultDO.getFromStorageLocation())));
                    //rowData.fromInvLoc = fromInvLocModel;
                    //rowData.itemDesc = resultDO.getFromItemDesc();
                }
                //On Hand
                set.cells[2] = (new IntegerField(resultDO.getFromQtyOnHand()));
                //To Item
                DropDownField<Integer> invItem = new DropDownField<Integer>();
                TableDataModel<TableDataRow<Integer>> invItemModel = new TableDataModel<TableDataRow<Integer>>();
                invItemModel.add(new TableDataRow<Integer>(resultDO.getInventoryItemId(),new StringObject(resultDO.getInventoryItem())));
                invItem.setModel(invItemModel);
                invItem.setValue(invItemModel.get(0));
                set.cells[3] = (invItem);
                //Ext
                set.cells[4] = (new CheckField(resultDO.getIsBulk()));
                
                //To Loc
                DropDownField<Integer> loc = new DropDownField<Integer>();
                if(resultDO.getStorageLocationId() != null){
                    TableDataModel<TableDataRow<Integer>> locModel = new TableDataModel<TableDataRow<Integer>>();
                    locModel.add(new TableDataRow<Integer>(resultDO.getStorageLocationId(),new StringObject(resultDO.getStorageLocation())));
                    loc.setModel(locModel);
                    loc.setValue(locModel.get(0));
                }
                set.cells[5] = (loc);
                
                //Qty
                set.cells[6] = (new IntegerField(resultDO.getQuantityReceived()));
                
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