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

import org.openelis.domain.IdNameVO;
import org.openelis.gwt.common.data.Query;
import org.openelis.manager.InventoryReceiptManager;
import org.openelis.persistence.EJBFactory;
import org.openelis.remote.InventoryReceiptManagerRemote;
import org.openelis.remote.InventoryReceiptRemote;

public class InventoryReceiptService {
    
    public ArrayList<IdNameVO> fetchByUpc(String search) throws Exception {
        return remote().fetchByUpc(search + "%", 10);
    }
    
    public ArrayList<InventoryReceiptManager> query(Query query) throws Exception {
        return remote().query(query.getFields());
    }
    
    public InventoryReceiptManager add(InventoryReceiptManager man) throws Exception {
        return remoteManager().add(man);
    }
    
    public InventoryReceiptManager update(InventoryReceiptManager man) throws Exception {
        return remoteManager().update(man);
    }
    
    public InventoryReceiptManager fetchForUpdate(InventoryReceiptManager man) throws Exception {
        return remoteManager().fetchForUpdate(man);
    }
    
    public InventoryReceiptManager abortUpdate(InventoryReceiptManager man) throws Exception {
        return remoteManager().abortUpdate(man);
    }
    
    private InventoryReceiptRemote remote() {
        return (InventoryReceiptRemote)EJBFactory.lookup("openelis/InventoryReceiptBean/remote");
    }
    
    private InventoryReceiptManagerRemote remoteManager() {
        return (InventoryReceiptManagerRemote)EJBFactory.lookup("openelis/InventoryReceiptManagerBean/remote");        
    }
}
/*
    private static final InventoryReceiptMetaMap InventoryReceiptMeta = new InventoryReceiptMetaMap();
    
    private static final int leftTableRowsPerPage = 250;
    private static Node subRpcNode;
    
    private UTFResource openElisConstants = UTFResource.getBundle((String)SessionManager.getSession().getAttribute("locale"));
    
    public InventoryReceiptQuery commitQuery(InventoryReceiptQuery query) throws Exception {
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
                    throw new Exception(e.getMessage()); 
                }           
            }    
        }else{
            //get screen type
            
            InventoryReceiptRemote remote = (InventoryReceiptRemote)EJBFactory.lookup("openelis/InventoryReceiptBean/remote");
        
            if(query.fields.isEmpty())
                throw new QueryException(openElisConstants.getString("emptyQueryException"));
           
            try{    
                receipts = remote.query(query.fields,query.page*leftTableRowsPerPage,leftTableRowsPerPage, (InventoryReceiptRemote.RECEIPT.equals(query.type)));
            }catch(LastPageException e) {
                throw new LastPageException(openElisConstants.getString("lastPageException"));
            }catch(Exception e){
                throw new Exception(e.getMessage());
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
    
    public InventoryReceiptForm commitQueryAndLock(InventoryReceiptForm rpc) throws Exception {
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
                throw new Exception(e.getMessage()); 
            }           
        }
        
        
        //fill the model with the query results
        fillModelFromQuery(rpc.tableRows, receipts, invQuery.type);
        
        return rpc;
    }
    
    public InventoryReceiptForm commitQueryAndUnlock(InventoryReceiptForm rpc) throws Exception {
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
                throw new Exception(e.getMessage()); 
            }           
        }    
        
        //fill the model with the query results
        fillModelFromQuery(rpc.tableRows, receipts, invQuery.type);

        return rpc;
    }


    public InventoryReceiptForm commitAdd(InventoryReceiptForm rpc) throws Exception {
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
                throw new Exception(e.getMessage());
        }
        
        return rpc;
    }

    public InventoryReceiptForm commitUpdate(InventoryReceiptForm rpc) throws Exception {
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
                throw new Exception(e.getMessage());
        }

        return rpc;
    }

    public InventoryReceiptForm commitDelete(InventoryReceiptForm rpc) throws Exception {
        return null;
    }

    public InventoryReceiptForm abort(InventoryReceiptForm rpc) throws Exception {
        return null;
    }

    public InventoryReceiptForm fetch(InventoryReceiptForm rpc) throws Exception {
        return null;
    }

    public InventoryReceiptForm fetchForUpdate(InventoryReceiptForm rpc) throws Exception {
        return null;
    }
    
    public InventoryReceiptForm getScreen(InventoryReceiptForm rpc) throws Exception{
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
    
    public InventoryReceiptForm getReceiptsAndLockOrder(InventoryReceiptForm rpc) throws Exception {
        InventoryReceiptRemote remote = (InventoryReceiptRemote)EJBFactory.lookup("openelis/InventoryReceiptBean/remote");
        List receiptRecords = null;
        try{
            receiptRecords = remote.getInventoryReceiptRecordsAndLock(rpc.orderId);
            
        }catch(Exception e){
            throw new Exception(e.getMessage());
        }
        
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

    public ReceiptInvLocationAutoRPC getMatchesCall(ReceiptInvLocationAutoRPC rpc) throws Exception {
        if("location".equals(rpc.cat))
            rpc.autoMatches = getLocationMatches(rpc.match, rpc.addToExisting, rpc.invItemId);
        else if("toInventoryItemTrans".equals(rpc.cat))
            rpc.autoMatches = getInventoryItemMatches(rpc.match, false, rpc.invItemId, rpc.parentInvItemId);
        else
            return null;
        
        return rpc;
    }
    
    
    public TableDataModel getMatches(String cat, TableDataModel model, String match, HashMap<String, FieldType> params) throws Exception {
        if(cat.equals("inventoryItem"))
            return getInventoryItemMatches(match, false, -1, -1);
        else if(cat.equals("inventoryItemTrans"))
            return getInventoryItemMatches(match, true, -1, -1);
        else if(cat.equals("organization"))
            return getOrganizationMatches(match);

        return null;
    }
    
    private TableDataModel getLocationMatches(String match, String addToExisting, Integer inventoryItemId) throws Exception{
        //InventoryItemRemote remote = (InventoryItemRemote)EJBFactory.lookup("openelis/InventoryItemBean/remote");
        TableDataModel<TableDataRow<Integer>> dataModel = new TableDataModel<TableDataRow<Integer>>();
        List<StorageLocationVO> autoCompleteList = new ArrayList();
        
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
        
        for(StorageLocationVO resultDO : autoCompleteList){
            dataModel.add(new TableDataRow<Integer>(resultDO.getId(),new StringObject(resultDO.getLocation())));
        }       
        
        return dataModel;       
    }
    
    private TableDataModel<TableDataRow<Integer>> getInventoryItemMatches(String match, boolean loc, Integer inventoryItemId, Integer parentInventoryItemId) throws Exception{
        InventoryItemRemote remote = (InventoryItemRemote)EJBFactory.lookup("openelis/InventoryItemBean/remote");
        if(inventoryItemId == null && parentInventoryItemId == null)
                throw new FormErrorException(openElisConstants.getString("inventoryTransferFromItemException"));

        TableDataModel<TableDataRow<Integer>> dataModel = new TableDataModel<TableDataRow<Integer>>();
        List<InventoryItemAutoDO> autoCompleteList = null;
        
        String parsedMatch = match.replace('*', '%');
/*
        if(loc)
            autoCompleteList = remote.inventoryItemStoreLocAutoCompleteLookupByName(parsedMatch+"%", 10, false, true);    
        else if(new Integer(-1).compareTo(inventoryItemId) < 0 || new Integer(-1).compareTo(parentInventoryItemId) < 0)
            autoCompleteList = remote.inventoryItemStoreChildAutoCompleteLookupByName(parsedMatch+"%", parentInventoryItemId, inventoryItemId, 10);
        else
            autoCompleteList = remote.inventoryItemStoreAutoCompleteLookupByName(parsedMatch+"%", 10, true, true); 

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
                                                                  new StringObject(store),
                                                                  new StringObject(location),
                                                                  new IntegerObject(qtyOnHand)
                                                 }
                       );
            }else {
                data = new TableDataRow<Integer>(itemId,
                                new FieldType[] {
                                                 new StringObject(name),
                                                 new StringObject(store)
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
                    setData.expDate = Datetime.getInstance(Datetime.YEAR, Datetime.DAY, resultDO.getExpDate().getDate());
            }
            
            setData.parentInvItemId = resultDO.getParentInventoryItemId();
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

        List<OrganizationDO> autoCompleteList = null;
        Integer id;

        try {
            id = Integer.parseInt(match);
            // this will throw an exception if it isnt an id
            // lookup by id...should only bring back 1 result
        } catch (NumberFormatException e) {
            id = null;
        }

        try {
            if (id != null) {
                autoCompleteList = new ArrayList(1);
                autoCompleteList.add(remote.fetchActiveById(id));
            } else {
                autoCompleteList = remote.fetchActiveByName(match + "%", 10);
            }
        } catch (Exception e) {
            Window.alert(e.getMessage());
        }

        for(OrganizationDO resultDO : autoCompleteList){
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

            
            if(transfer){
                DropDownField<Integer> fromItemField = (DropDownField)row.cells[0];
                ArrayList<TableDataRow<Integer>> fromItemSelections = fromItemField.getValue();
                DropDownField<Integer> toItemField = (DropDownField)row.cells[3];
                ArrayList<TableDataRow<Integer>> toItemSelections = toItemField.getValue();
                
                ReceiptInvItemKey fromSetData = null;
                ReceiptInvItemKey toSetData = null;
                
                if(fromItemSelections.size() > 0)
                    fromSetData = (ReceiptInvItemKey)fromItemSelections.get(0).getData();
                
                if(fromSetData == null || fromSetData.parentRatio == null)
                    toSetData = (ReceiptInvItemKey)toItemSelections.get(0).getData();
                
                receiptDO.setFromInventoryItemId((Integer)(fromItemField).getSelectedKey());
                receiptDO.setFromInventoryItem((String)(fromItemField).getTextValue());
                receiptDO.setFromQtyOnHand((Integer)((Field)row.cells[2]).getValue());
                receiptDO.setInventoryItemId((Integer)(toItemField).getSelectedKey());
                receiptDO.setInventoryItem((String)(toItemField).getTextValue());

                receiptDO.setFromStorageLocationId(fromSetData.locId);
                receiptDO.setStorageLocationId((Integer)((DropDownField)row.cells[5]).getSelectedKey());
                receiptDO.setQuantityReceived((Integer)((Field)row.cells[6]).getValue());
                if(toSetData != null && toSetData.parentRatio != null)
                    receiptDO.setParentRatio(toSetData.parentRatio);
                else if(fromSetData != null && fromSetData.parentRatio != null)
                    receiptDO.setChildRatio(fromSetData.parentRatio);
                
            }else{
                receiptDO.setOrderNumber(((IntegerField)row.cells[0]).getValue());
                receiptDO.setReceivedDate(((Datetime)((DateField)row.cells[1]).getValue()).getDate());
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
                    receiptDO.setExpDate(((Datetime)hiddenRpc.expirationDate.getValue()).getDate());
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
        
        if(deletedRows != null){
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
        Datetime todaysDate = Datetime.getInstance(Datetime.YEAR,Datetime.DAY,new Date());
        while(i < receipts.size() && i < leftTableRowsPerPage) {
            InventoryReceiptDO resultDO = (InventoryReceiptDO)receipts.get(i);
            
            if("receipt".equals(screenType)){
                TableDataRow<InvReceiptItemInfoForm> set = new TableDataRow<InvReceiptItemInfoForm>(10);
                //ord #
                set.cells[0] = new IntegerField(resultDO.getOrderNumber());
                //date rec
                DateField dateRec = new DateField();
                if(resultDO.getReceivedDate() != null)
                    dateRec.setValue(Datetime.getInstance(Datetime.YEAR, Datetime.DAY, resultDO.getReceivedDate().getDate()));
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
                FormUtil.setForm(keyRPC, subRpcNode);
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
                    keyRPC.expirationDate.setValue(Datetime.getInstance(Datetime.YEAR, Datetime.DAY, resultDO.getExpDate().getDate()));
                
                if(resultDO.getStorageLocationId() != null){
                    TableDataModel<TableDataRow<Integer>> locModel = new TableDataModel<TableDataRow<Integer>>();
                    locModel.add(new TableDataRow<Integer>(resultDO.getStorageLocationId(),new StringObject(resultDO.getStorageLocation())));
                    keyRPC.storageLocationId.setModel(locModel);
                    keyRPC.storageLocationId.setValue(locModel.get(0));
                }
                
                set.key = (keyRPC);
                
                model.add(set);
                
            }else if("transfer".equals(screenType)){
                TableDataRow<InvReceiptItemInfoForm> set = new TableDataRow<InvReceiptItemInfoForm>(7);
                //From Item
                if(resultDO.getFromInventoryItemId() != null){
                    DropDownField<Integer> fromInvItem = new DropDownField<Integer>();
                    TableDataModel<TableDataRow<Integer>> fromInvItemModel = new TableDataModel<TableDataRow<Integer>>();
                    fromInvItemModel.add(new TableDataRow<Integer>(resultDO.getFromInventoryItemId(),new StringObject(resultDO.getFromInventoryItem())));
                    fromInvItem.setModel(fromInvItemModel);
                    fromInvItem.setValue(fromInvItemModel.get(0));
                    set.cells[0] = (fromInvItem);
                    
                    //From loc
                    StringField fromStorageLocation = new StringField();
                    fromStorageLocation.setValue(resultDO.getFromStorageLocation());
                    set.cells[1] = fromStorageLocation;

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
                
              //setup the subform
                InvReceiptItemInfoForm keyRPC = new InvReceiptItemInfoForm();
                FormUtil.setForm(keyRPC, subRpcNode);

                keyRPC.description.setValue(resultDO.getFromItemDesc());
                keyRPC.dispensedUnits.setValue(resultDO.getFromitemDispensedUnits());
                keyRPC.storeId.setValue(resultDO.getFromItemStore());
                keyRPC.itemIsBulk = resultDO.getIsBulk();
                keyRPC.itemIsLotMaintained = resultDO.getIsLotMaintained();
                keyRPC.itemIsSerialMaintained = resultDO.getIsSerialMaintained();
                
                keyRPC.toDescription.setValue(resultDO.getItemDesc());
                keyRPC.toDispensedUnits.setValue(resultDO.getItemDispensedUnits());
                keyRPC.toStoreId.setValue(resultDO.getItemStore());
                
                keyRPC.lotNumber.setValue(resultDO.getLotNumber());
                keyRPC.toLotNumber.setValue(resultDO.getLotNumber());
                
                if(resultDO.getExpDate() != null && resultDO.getExpDate().getDate() != null){
                    keyRPC.expirationDate.setValue(Datetime.getInstance(Datetime.YEAR, Datetime.DAY, resultDO.getExpDate().getDate()));
                    keyRPC.toExpDate.setValue(Datetime.getInstance(Datetime.YEAR, Datetime.DAY, resultDO.getExpDate().getDate()));
                }

                set.key = keyRPC;
                
                model.add(set);
            }
            i++;
        } 
    }
    
    public TransferLocationRPC fetchLocationAndLock(TransferLocationRPC rpc) throws Exception {
        InventoryReceiptRemote remote = (InventoryReceiptRemote)EJBFactory.lookup("openelis/InventoryReceiptBean/remote");
        InventoryLocationDO locDO;
        
        try{
            locDO = remote.lockLocationAndFetch(rpc.oldLocId, rpc.currentLocId);

        }catch(Exception e){
            throw new Exception(e.getMessage());
        }

        if(locDO != null)
            rpc.currentQtyOnHand = locDO.getQuantityOnHand();
        
        return rpc;
    }
    
    public void unlockLocations(InvReceiptItemInfoForm rpc){
        InventoryReceiptRemote remote = (InventoryReceiptRemote)EJBFactory.lookup("openelis/InventoryReceiptBean/remote");
        remote.unlockLocations(rpc.lockedIds);
        
    }
    
    public void unlockOrders(InvReceiptItemInfoForm rpc){
        InventoryReceiptRemote remote = (InventoryReceiptRemote)EJBFactory.lookup("openelis/InventoryReceiptBean/remote");
        remote.unlockOrders(rpc.lockedIds);
        
    }
*/