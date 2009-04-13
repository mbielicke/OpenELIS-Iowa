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
package org.openelis.modules.inventoryAdjustment.server;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.openelis.domain.IdNameDO;
import org.openelis.domain.IdNameDateDO;
import org.openelis.domain.InventoryAdjLocationAutoDO;
import org.openelis.domain.InventoryAdjustmentAddAutoFillDO;
import org.openelis.domain.InventoryAdjustmentChildDO;
import org.openelis.domain.InventoryAdjustmentDO;
import org.openelis.domain.InventoryItemAutoDO;
import org.openelis.domain.InventoryLocationDO;
import org.openelis.gwt.common.DatetimeRPC;
import org.openelis.gwt.common.EntityLockedException;
import org.openelis.gwt.common.FieldErrorException;
import org.openelis.gwt.common.Form;
import org.openelis.gwt.common.FormErrorException;
import org.openelis.gwt.common.LastPageException;
import org.openelis.gwt.common.Query;
import org.openelis.gwt.common.RPCException;
import org.openelis.gwt.common.TableFieldErrorException;
import org.openelis.gwt.common.ValidationErrorsList;
import org.openelis.gwt.common.data.AbstractField;
import org.openelis.gwt.common.data.DropDownField;
import org.openelis.gwt.common.data.Field;
import org.openelis.gwt.common.data.FieldType;
import org.openelis.gwt.common.data.IntegerObject;
import org.openelis.gwt.common.data.StringObject;
import org.openelis.gwt.common.data.TableDataModel;
import org.openelis.gwt.common.data.TableDataRow;
import org.openelis.gwt.common.data.TableField;
import org.openelis.gwt.server.ServiceUtils;
import org.openelis.gwt.services.AppScreenFormServiceInt;
import org.openelis.gwt.services.AutoCompleteServiceInt;
import org.openelis.metamap.InventoryAdjustmentMetaMap;
import org.openelis.modules.inventoryAdjustment.client.InventoryAdjustmentForm;
import org.openelis.modules.inventoryAdjustment.client.InventoryAdjustmentItemAutoRPC;
import org.openelis.modules.inventoryReceipt.client.InvReceiptItemInfoForm;
import org.openelis.modules.inventoryReceipt.client.TransferLocationRPC;
import org.openelis.persistence.EJBFactory;
import org.openelis.remote.CategoryRemote;
import org.openelis.remote.InventoryAdjustmentRemote;
import org.openelis.remote.InventoryItemRemote;
import org.openelis.remote.InventoryReceiptRemote;
import org.openelis.server.constants.Constants;
import org.openelis.server.handlers.InventoryItemStoresCacheHandler;
import org.openelis.util.Datetime;
import org.openelis.util.FormUtil;
import org.openelis.util.SessionManager;
import org.openelis.util.UTFResource;

public class InventoryAdjustmentService implements AppScreenFormServiceInt<InventoryAdjustmentForm,Query<TableDataRow<Integer>>>, AutoCompleteServiceInt{
    
    private static final InventoryAdjustmentMetaMap InventoryAdjustmentMeta = new InventoryAdjustmentMetaMap();
    private static final int leftTableRowsPerPage = 20;
    
    private UTFResource openElisConstants= UTFResource.getBundle((String)SessionManager.getSession().getAttribute("locale"));
    
    public InventoryAdjustmentForm abort(InventoryAdjustmentForm rpc) throws RPCException {
        //remote interface to call the inventory adjustment bean
        InventoryAdjustmentRemote remote = (InventoryAdjustmentRemote)EJBFactory.lookup("openelis/InventoryAdjustmentBean/remote");        
        
        InventoryAdjustmentDO invAdjustmentDO = remote.getInventoryAdjustment(rpc.entityKey);

        //set the fields in the RPC
        setFieldsInRPC(rpc, invAdjustmentDO);

        //load the adjustment child records
        List childList = remote.getChildRecordsAndUnlock(rpc.entityKey);
        fillChildrenTable(rpc.adjustmentsTable.getValue(), childList);
        
        return rpc;  
    }

    public InventoryAdjustmentForm commitAdd(InventoryAdjustmentForm rpc) throws RPCException {
        //remote interface to call the inventory adjustment bean
        InventoryAdjustmentRemote remote = (InventoryAdjustmentRemote)EJBFactory.lookup("openelis/InventoryAdjustmentBean/remote");
        InventoryAdjustmentDO invAdjustmentDO = new InventoryAdjustmentDO();
        List<InventoryAdjustmentChildDO> adjustmentChildren = new ArrayList<InventoryAdjustmentChildDO>();
        
        //build the inventoryAdjustmentDO from the form
        invAdjustmentDO = getInventoryAdjustmentDOFromRPC(rpc);
        
        //adjustment child info
        TableField<TableDataRow<Integer>> adjTableField = rpc.adjustmentsTable;
        TableDataModel<TableDataRow<Integer>> adjustmentsTable = adjTableField.getValue();
        adjustmentChildren = getAdjustmentsListFromRPC(adjustmentsTable, invAdjustmentDO.getId());
        
        //send the changes to the database
        Integer adjustmentId;
        try{
            adjustmentId = (Integer)remote.updateInventoryAdjustment(invAdjustmentDO, adjustmentChildren);
        }catch(Exception e){
            if(e instanceof ValidationErrorsList){
                setRpcErrors(((ValidationErrorsList)e).getErrorList(), adjTableField, rpc);
                return rpc;
            }else
                throw new RPCException(e.getMessage());
        }
        
        //lookup the changes from the database and build the rpc
        invAdjustmentDO.setId(adjustmentId);

        //set the fields in the RPC
        setFieldsInRPC(rpc, invAdjustmentDO);

        return rpc;
    }

    public InventoryAdjustmentForm commitDelete(InventoryAdjustmentForm rpc) throws RPCException {
        return null;
    }

    public Query<TableDataRow<Integer>> commitQuery(Query<TableDataRow<Integer>> query) throws RPCException {
        List inventoryAdjustmentNames;
        /*
        //if the rpc is null then we need to get the page
        if(qList == null){

            qList = (ArrayList<AbstractField>)SessionManager.getSession().getAttribute("InventoryAdjustmentQuery");
    
            if(qList == null)
                throw new RPCException(openElisConstants.getString("queryExpiredException"));

            InventoryAdjustmentRemote remote = (InventoryAdjustmentRemote)EJBFactory.lookup("openelis/InventoryAdjustmentBean/remote");
            try{
                inventoryAdjustmentNames = remote.query(qList, (model.getPage()*leftTableRowsPerPage), leftTableRowsPerPage+1);
            }catch(Exception e){
                if(e instanceof LastPageException){
                    throw new LastPageException(openElisConstants.getString("lastPageException"));
                }else{
                    throw new RPCException(e.getMessage()); 
                }           
            }    
        }else{*/
            InventoryAdjustmentRemote remote = (InventoryAdjustmentRemote)EJBFactory.lookup("openelis/InventoryAdjustmentBean/remote");
            /*
            HashMap<String,AbstractField> fields = qList.getFieldMap();
            fields.remove("adjustmentsTable");
            fields.remove("label1");
            fields.remove(InventoryAdjustmentMeta.getSystemUserId());
            */
            try{    
                inventoryAdjustmentNames = remote.query(query.fields,query.page*leftTableRowsPerPage,leftTableRowsPerPage);
            }catch(LastPageException e) {
                throw new LastPageException(openElisConstants.getString("lastPageException"));
            }catch(Exception e){
                throw new RPCException(e.getMessage());
            }
    
        
            //need to save the rpc used to the encache
        //    SessionManager.getSession().setAttribute("InventoryAdjustmentQuery", qList);
       // }
        
        //fill the model with the query results
        int i=0;
        
        if(query.results == null)
            query.results = new TableDataModel<TableDataRow<Integer>>();
        else
        query.results.clear();
        
        while(i < inventoryAdjustmentNames.size() && i < leftTableRowsPerPage) {
            IdNameDateDO resultDO = (IdNameDateDO)inventoryAdjustmentNames.get(i);
            query.results.add(new TableDataRow<Integer>(resultDO.getId(),new StringObject(resultDO.getName())));
            i++;
        } 
 
        return query;
    }

    public InventoryAdjustmentForm commitUpdate(InventoryAdjustmentForm rpc) throws RPCException {
        //remote interface to call the inventory adjustment bean
        InventoryAdjustmentRemote remote = (InventoryAdjustmentRemote)EJBFactory.lookup("openelis/InventoryAdjustmentBean/remote");
        InventoryAdjustmentDO invAdjustmentDO = new InventoryAdjustmentDO();
        List childRows = new ArrayList();

        //build the inventoryAdjustmentDO from the form
        invAdjustmentDO = getInventoryAdjustmentDOFromRPC(rpc);
        
        //child info
        TableField<TableDataRow<Integer>> adjTableField = rpc.adjustmentsTable;
        TableDataModel<TableDataRow<Integer>> adjustmentsTable = adjTableField.getValue();
        childRows = getAdjustmentsListFromRPC(adjustmentsTable, invAdjustmentDO.getId());
        
        //send the changes to the database
        try{
            remote.updateInventoryAdjustment(invAdjustmentDO, childRows);
        }catch(Exception e){
            if(e instanceof ValidationErrorsList){
                setRpcErrors(((ValidationErrorsList)e).getErrorList(), adjTableField, rpc);
                return rpc;
            }else
                throw new RPCException(e.getMessage());
        }
        
        //set the fields in the RPC
        setFieldsInRPC(rpc, invAdjustmentDO);   
        
        return rpc;
    }

    public InventoryAdjustmentForm fetch(InventoryAdjustmentForm rpc) throws RPCException {
        /*
         * Call checkModels to make screen has most recent versions of dropdowns
         */
        checkModels(rpc);
        
        //remote interface to call the organization bean
        InventoryAdjustmentRemote remote = (InventoryAdjustmentRemote)EJBFactory.lookup("openelis/InventoryAdjustmentBean/remote");
        
        InventoryAdjustmentDO inventoryAdjustmentDO = remote.getInventoryAdjustment(rpc.entityKey);

        //set the fields in the RPC
        setFieldsInRPC(rpc, inventoryAdjustmentDO);

        //load the adjustment child records
        List childList = remote.getChildRecords(rpc.entityKey);
        fillChildrenTable(rpc.adjustmentsTable.getValue(),childList);
        
        return rpc;  
    }

    public InventoryAdjustmentForm fetchForUpdate(InventoryAdjustmentForm rpc) throws RPCException {
        /*
         * Call checkModels to make screen has most recent versions of dropdowns
         */
        checkModels(rpc);
        
        //remote interface to call the inventory adjustment bean
        InventoryAdjustmentRemote remote = (InventoryAdjustmentRemote)EJBFactory.lookup("openelis/InventoryAdjustmentBean/remote");
        
        InventoryAdjustmentDO invAdjustmentDO;
        List childList;
        
        try{
            invAdjustmentDO = remote.getInventoryAdjustment(rpc.entityKey);
            childList = remote.getChildRecordsAndLock(rpc.entityKey);
            
        }catch(Exception e){
            throw new RPCException(e.getMessage());
        }
        
        //set the fields in the RPC
        setFieldsInRPC(rpc, invAdjustmentDO);        
        
        fillChildrenTable(rpc.adjustmentsTable.getValue(), childList);
        
        return rpc;  
    }
    
    public InventoryAdjustmentForm getScreen(InventoryAdjustmentForm rpc) throws RPCException{
        rpc.xml = ServiceUtils.getXML(Constants.APP_ROOT+"/Forms/inventoryAdjustment.xsl");
        /*
         * Load initial  models to RPC and store cache verison of models into Session for 
         * comparisons for later fetches
         */
        rpc.stores = InventoryItemStoresCacheHandler.getStores();
        SessionManager.getSession().setAttribute("invItemStoresVersion",InventoryItemStoresCacheHandler.version);
        return rpc;
    }
    
    public void checkModels(InventoryAdjustmentForm rpc) {
        /*
         * Retrieve current version of models from session.
         */
        int stores = (Integer)SessionManager.getSession().getAttribute("invItemStoresVersion");
        /*
         * Compare stored version to current cache versions and update if necessary. 
         */
         if(stores != InventoryItemStoresCacheHandler.version){
            rpc.stores = InventoryItemStoresCacheHandler.getStores();
            SessionManager.getSession().setAttribute("invItemStoresVersion", InventoryItemStoresCacheHandler.version);
        }
    }
    
    public InventoryAdjustmentForm getAddAutoFillValues(InventoryAdjustmentForm rpc) throws Exception {
        InventoryAdjustmentRemote remote = (InventoryAdjustmentRemote)EJBFactory.lookup("openelis/InventoryAdjustmentBean/remote");
        InventoryAdjustmentAddAutoFillDO autoDO;
        
        autoDO = remote.getAddAutoFillValues();
        
        rpc.systemUser.setValue(autoDO.getSystemUser());
        rpc.systemUserId = autoDO.getSystemUserId();
        rpc.adjustmentDate.setValue(DatetimeRPC.getInstance(Datetime.YEAR, Datetime.DAY, autoDO.getAdjustmentDate().getDate()));
        
        return rpc;
    }
    
    public InventoryAdjustmentForm getInventoryItemInformation(InventoryAdjustmentForm rpc) throws RPCException {
        InventoryAdjustmentRemote remote = (InventoryAdjustmentRemote)EJBFactory.lookup("openelis/InventoryAdjustmentBean/remote");
        
        List invLocationRecords;
        try{
            invLocationRecords = remote.getInventoryitemDataAndLockLoc(rpc.locId, rpc.oldLocId, rpc.storeIdKey);
        
        }catch(Exception e){
            throw new RPCException(e.getMessage());
        }

        if(invLocationRecords.size() > 0){
            InventoryAdjLocationAutoDO locDO = (InventoryAdjLocationAutoDO)invLocationRecords.get(0);
            
            //inventory item model
            TableDataModel<TableDataRow<Integer>> itemModel = new TableDataModel<TableDataRow<Integer>>();
            itemModel.add(new TableDataRow<Integer>(locDO.getInventoryItemId(),new StringObject(locDO.getInventoryItem())));
            
            rpc.locId = locDO.getInventoryLocationId();
            rpc.invItemModel = itemModel;
            rpc.storageLocation = locDO.getStorageLocation();
            rpc.qtyOnHand = locDO.getQuantityOnHand();
        }
        
        return rpc;
    }
    
    public InventoryAdjustmentItemAutoRPC getMatchesObj(InventoryAdjustmentItemAutoRPC rpc) throws RPCException {
        if("inventoryItem".equals(rpc.cat))
            rpc.autoMatches = getInventoryItemMatches(rpc.text, rpc.storeId);
            return rpc;
    }

    public TableDataModel getMatches(String cat, TableDataModel model, String match, HashMap<String, FieldType> params) throws RPCException {
        //if(cat.equals("inventoryItem"))
        //    return getInventoryItemMatches(match, params);
        
        return null;
    }
    
    private TableDataModel getInventoryItemMatches(String match, Integer storeId) throws RPCException{
        if(storeId == null || storeId == 0){
            //we dont want to do anything...throw error
            throw new FormErrorException(openElisConstants.getString("inventoryAdjItemAutoException"));
        }

        TableDataModel<TableDataRow<Integer>> dataModel = new TableDataModel<TableDataRow<Integer>>();
        List autoCompleteList = new ArrayList();

        String parsedMatch = match.replace('*', '%');
        
        InventoryItemRemote remote = (InventoryItemRemote)EJBFactory.lookup("openelis/InventoryItemBean/remote");
        //lookup by name
        autoCompleteList = remote.inventoryAdjItemAutoCompleteLookupByName(parsedMatch+"%",storeId, 10);    
        
        for(int i=0; i < autoCompleteList.size(); i++){
            InventoryItemAutoDO resultDO = (InventoryItemAutoDO) autoCompleteList.get(i);

            Integer itemId = resultDO.getId();
            String name = resultDO.getName();
            String store = resultDO.getStore();
            String location = resultDO.getLocation();
            String lotNum = resultDO.getLotNum();
            Datetime expDateTime = resultDO.getExpDate();
            Integer locId = resultDO.getLocationId();
            
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
                                                                                    new IntegerObject(qty)
                                                                   }
            );
            data.setData(new IntegerObject(locId));
            //add the dataset to the datamodel
            dataModel.add(data);                                 

        }       
        
        return dataModel;       
    }
    
    private TableDataModel<TableDataRow<Integer>> getInitialModel(String cat){
        Integer id = null;
        CategoryRemote remote = (CategoryRemote)EJBFactory.lookup("openelis/CategoryBean/remote");

        if(cat.equals("itemStores"))
            id = remote.getCategoryId("inventory_item_stores");
        
        List<IdNameDO> entries = new ArrayList<IdNameDO>();
        if(id > -1)
            entries = (List<IdNameDO>)remote.getDropdownValues(id);
        
        
        //we need to build the model to return
        TableDataModel<TableDataRow<Integer>> returnModel = new TableDataModel<TableDataRow<Integer>>();
        
        if(entries.size() > 0){ 
            returnModel.add(new TableDataRow<Integer>(0,new StringObject("")));
        }
        
        for(IdNameDO resultDO : entries) { 
            returnModel.add(new TableDataRow<Integer>(resultDO.getId(),new StringObject(resultDO.getName())));
        }       
        
        return returnModel;
    }
    
    private void setFieldsInRPC(InventoryAdjustmentForm form, InventoryAdjustmentDO adjustmentDO){
        form.id.setValue(adjustmentDO.getId());
        form.description.setValue(adjustmentDO.getDescription());
        form.systemUser.setValue(adjustmentDO.getSystemUser());
        form.systemUserId = adjustmentDO.getSystemUserId();
        form.adjustmentDate.setValue(DatetimeRPC.getInstance(Datetime.YEAR, Datetime.DAY, adjustmentDO.getAdjustmentDate().getDate()));
        form.storeId.setValue(new TableDataRow<Integer>(adjustmentDO.getStoreId()));        
    }
    
    private InventoryAdjustmentDO getInventoryAdjustmentDOFromRPC(InventoryAdjustmentForm form){
        InventoryAdjustmentDO inventoryAdjustmentDO = new InventoryAdjustmentDO();
        
        inventoryAdjustmentDO.setId(form.id.getValue());
        inventoryAdjustmentDO.setAdjustmentDate(form.adjustmentDate.getValue().getDate());
        inventoryAdjustmentDO.setDescription(form.description.getValue());
        inventoryAdjustmentDO.setSystemUser(form.systemUser.getValue());
        inventoryAdjustmentDO.setSystemUserId(form.systemUserId);
        inventoryAdjustmentDO.setStoreId((Integer)form.storeId.getSelectedKey());
        
        return inventoryAdjustmentDO;
    }
    
    private List<InventoryAdjustmentChildDO> getAdjustmentsListFromRPC(TableDataModel<TableDataRow<Integer>> adjustmentsModel, Integer adjustmentId){
        List<InventoryAdjustmentChildDO> adjustmentsList = new ArrayList<InventoryAdjustmentChildDO>();
        List<TableDataRow<Integer>> deletedRows = adjustmentsModel.getDeletions();
        
        for(int i=0; i<adjustmentsModel.size(); i++){
            InventoryAdjustmentChildDO childDO = new InventoryAdjustmentChildDO();
            TableDataRow<Integer> row = adjustmentsModel.get(i);
            
            //hidden data
            Integer childId = row.key;
            
            if(childId != null)
                childDO.setId(childId);
            
            childDO.setAdjustmentId(adjustmentId);
            childDO.setLocationId((Integer)row.cells[0].getValue());
            childDO.setAdjustedQuantity((Integer)row.cells[5].getValue());
            childDO.setPhysicalCount((Integer)row.cells[4].getValue());
            
            adjustmentsList.add(childDO);    
        }
        
        if(deletedRows != null){
            for(int j=0; j<deletedRows.size(); j++){
                TableDataRow<Integer> deletedRow = deletedRows.get(j);
                if(deletedRow.key != null){
                    InventoryAdjustmentChildDO childDO = new InventoryAdjustmentChildDO();
                    childDO.setDelete(true);
                    childDO.setId(deletedRow.key);
                    
                    adjustmentsList.add(childDO);
                }
            }
        }
        
        return adjustmentsList;
    }
    
    private void setRpcErrors(List exceptionList, TableField adjustmentsTable, InventoryAdjustmentForm form){
        HashMap<String,AbstractField> map = null;
        if(exceptionList.size() > 0)
            map = FormUtil.createFieldMap(form);
        for (int i=0; i<exceptionList.size();i++) {
            //if the error is inside the org contacts table
            if(exceptionList.get(i) instanceof TableFieldErrorException){
                int rowindex = ((TableFieldErrorException)exceptionList.get(i)).getRowIndex();
                adjustmentsTable.getField(rowindex,((TableFieldErrorException)exceptionList.get(i)).getFieldName())
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
    
    private TableDataModel fillChildrenTable(TableDataModel<TableDataRow<Integer>> childModel, List childrenList){
        try 
        {
            childModel.clear();
            
            for(int iter = 0;iter < childrenList.size();iter++) {
                InventoryAdjustmentChildDO childDO = (InventoryAdjustmentChildDO)childrenList.get(iter);

               TableDataRow<Integer> row = childModel.createNewSet();
               
               row.key = childDO.getId();
               
               row.cells[0].setValue(childDO.getLocationId());
               
               //we need to create a dataset for the inventory item auto complete
                if(childDO.getInventoryItemId() == null)
                    ((DropDownField<Integer>)row.cells[1]).clear();
                else{
                    TableDataModel<TableDataRow<Integer>> itemModel = new TableDataModel<TableDataRow<Integer>>();
                    itemModel.add(new TableDataRow<Integer>(childDO.getInventoryItemId(), new StringObject(childDO.getInventoryItem())));
                    ((DropDownField<Integer>)(Field)row.cells[1]).setModel(itemModel);
                    ((DropDownField<Integer>)(Field)row.cells[1]).setValue(itemModel.get(0));
                }

                row.cells[2].setValue(childDO.getStorageLocation());
                
                //we need to calculate the quantity on hand
                row.cells[3].setValue(childDO.getPhysicalCount() + (-1 * childDO.getAdjustedQuantity()));
                
                row.cells[4].setValue(childDO.getPhysicalCount());
                row.cells[5].setValue(childDO.getAdjustedQuantity());
                
                childModel.add(row);
           } 
            
        } catch (Exception e) {

            e.printStackTrace();
            return null;
        }       
        
        return childModel;
    }
    
    public InventoryAdjustmentForm fetchLocationAndLock(InventoryAdjustmentForm rpc) throws RPCException {
        InventoryReceiptRemote remote = (InventoryReceiptRemote)EJBFactory.lookup("openelis/InventoryReceiptBean/remote");
        InventoryLocationDO locDO;
        
        try{
            locDO = remote.lockLocationAndFetch(rpc.oldLocId, rpc.locId);

        }catch(Exception e){
            throw new RPCException(e.getMessage());
        }

        if(locDO != null)
            rpc.qtyOnHand = locDO.getQuantityOnHand();
        
        return rpc;
    }
    
    public void unlockLocations(InventoryAdjustmentForm rpc){
        InventoryReceiptRemote remote = (InventoryReceiptRemote)EJBFactory.lookup("openelis/InventoryReceiptBean/remote");
        remote.unlockLocations(rpc.lockedIds);
        
    }
}
