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
import org.openelis.gwt.common.DatetimeRPC;
import org.openelis.gwt.common.EntityLockedException;
import org.openelis.gwt.common.FieldErrorException;
import org.openelis.gwt.common.FormErrorException;
import org.openelis.gwt.common.FormRPC;
import org.openelis.gwt.common.LastPageException;
import org.openelis.gwt.common.RPCException;
import org.openelis.gwt.common.TableFieldErrorException;
import org.openelis.gwt.common.FormRPC.Status;
import org.openelis.gwt.common.data.AbstractField;
import org.openelis.gwt.common.data.Data;
import org.openelis.gwt.common.data.DataMap;
import org.openelis.gwt.common.data.DataModel;
import org.openelis.gwt.common.data.DataObject;
import org.openelis.gwt.common.data.DataSet;
import org.openelis.gwt.common.data.DateField;
import org.openelis.gwt.common.data.DropDownField;
import org.openelis.gwt.common.data.NumberField;
import org.openelis.gwt.common.data.NumberObject;
import org.openelis.gwt.common.data.StringField;
import org.openelis.gwt.common.data.StringObject;
import org.openelis.gwt.common.data.TableField;
import org.openelis.gwt.server.ServiceUtils;
import org.openelis.gwt.services.AppScreenFormServiceInt;
import org.openelis.gwt.services.AutoCompleteServiceInt;
import org.openelis.metamap.InventoryAdjustmentMetaMap;
import org.openelis.persistence.CachingManager;
import org.openelis.persistence.EJBFactory;
import org.openelis.remote.CategoryRemote;
import org.openelis.remote.InventoryAdjustmentRemote;
import org.openelis.remote.InventoryItemRemote;
import org.openelis.server.constants.Constants;
import org.openelis.util.Datetime;
import org.openelis.util.SessionManager;
import org.openelis.util.UTFResource;

public class InventoryAdjustmentService implements AppScreenFormServiceInt<FormRPC, DataSet, DataModel>, AutoCompleteServiceInt{
    
    private static final InventoryAdjustmentMetaMap InventoryAdjustmentMeta = new InventoryAdjustmentMetaMap();
    private static final int leftTableRowsPerPage = 20;
    
    private UTFResource openElisConstants= UTFResource.getBundle((String)SessionManager.getSession().getAttribute("locale"));
    
    public FormRPC abort(DataSet key, FormRPC rpcReturn) throws RPCException {
        //remote interface to call the inventory adjustment bean
        InventoryAdjustmentRemote remote = (InventoryAdjustmentRemote)EJBFactory.lookup("openelis/InventoryAdjustmentBean/remote");        
        
        InventoryAdjustmentDO invAdjustmentDO = remote.getInventoryAdjustmentAndUnlock((Integer)((DataObject)key.getKey()).getValue());

        //set the fields in the RPC
        setFieldsInRPC(rpcReturn, invAdjustmentDO);

        //load the adjustment child records
        List childList = remote.getChildRecordsAndUnlock((Integer)((DataObject)key.getKey()).getValue());
        DataModel rmodel = (DataModel)fillChildrenTable((DataModel)rpcReturn.getField("adjustmentsTable").getValue(),childList);
        rpcReturn.setFieldValue("adjustmentsTable",rmodel);
        
        return rpcReturn;  
    }

    public FormRPC commitAdd(FormRPC rpcSend, FormRPC rpcReturn) throws RPCException {
        //remote interface to call the inventory adjustment bean
        InventoryAdjustmentRemote remote = (InventoryAdjustmentRemote)EJBFactory.lookup("openelis/InventoryAdjustmentBean/remote");
        InventoryAdjustmentDO invAdjustmentDO = new InventoryAdjustmentDO();
        List<InventoryAdjustmentChildDO> adjustmentChildren = new ArrayList<InventoryAdjustmentChildDO>();
        
        //build the inventoryAdjustmentDO from the form
        invAdjustmentDO = getInventoryAdjustmentDOFromRPC(rpcSend);
        
        //adjustment child info
        TableField adjTableField = (TableField)rpcSend.getField("adjustmentsTable");
        DataModel adjustmentsTable = (DataModel)adjTableField.getValue();
        adjustmentChildren = getAdjustmentsListFromRPC(adjustmentsTable, invAdjustmentDO.getId());
        
        //validate the fields on the backend
        List exceptionList = remote.validateForAdd(invAdjustmentDO, adjustmentChildren);
            
        if(exceptionList.size() > 0){
            setRpcErrors(exceptionList, adjTableField, rpcSend);
        } 
        
        //send the changes to the database
        Integer adjustmentId;
        try{
            adjustmentId = (Integer)remote.updateInventoryAdjustment(invAdjustmentDO, adjustmentChildren);
        }catch(Exception e){
            exceptionList = new ArrayList();
            exceptionList.add(e);
            
            setRpcErrors(exceptionList, adjTableField, rpcSend);
            
            return rpcSend;
        }
        
        //lookup the changes from the database and build the rpc
        invAdjustmentDO.setId(adjustmentId);

        //set the fields in the RPC
        setFieldsInRPC(rpcReturn, invAdjustmentDO);

        return rpcReturn;
    }

    public FormRPC commitDelete(DataSet key, FormRPC rpcReturn) throws RPCException {
        return null;
    }

    public DataModel commitQuery(FormRPC rpcSend, DataModel model) throws RPCException {
        List inventoryAdjustmentNames;
        
        //if the rpc is null then we need to get the page
        if(rpcSend == null){

            FormRPC rpc = (FormRPC)SessionManager.getSession().getAttribute("InventoryAdjustmentQuery");
    
            if(rpc == null)
                throw new RPCException(openElisConstants.getString("queryExpiredException"));

            InventoryAdjustmentRemote remote = (InventoryAdjustmentRemote)EJBFactory.lookup("openelis/InventoryAdjustmentBean/remote");
            try{
                inventoryAdjustmentNames = remote.query(rpc.getFieldMap(), (model.getPage()*leftTableRowsPerPage), leftTableRowsPerPage+1);
            }catch(Exception e){
                if(e instanceof LastPageException){
                    throw new LastPageException(openElisConstants.getString("lastPageException"));
                }else{
                    throw new RPCException(e.getMessage()); 
                }           
            }    
        }else{
            InventoryAdjustmentRemote remote = (InventoryAdjustmentRemote)EJBFactory.lookup("openelis/InventoryAdjustmentBean/remote");
        
            HashMap<String,AbstractField> fields = rpcSend.getFieldMap();
            fields.remove("adjustmentsTable");
            fields.remove("label1");
            fields.remove(InventoryAdjustmentMeta.getSystemUserId());
            
            try{    
                inventoryAdjustmentNames = remote.query(fields,0,leftTableRowsPerPage);
    
            }catch(Exception e){
                throw new RPCException(e.getMessage());
            }
    
        
            //need to save the rpc used to the encache
            SessionManager.getSession().setAttribute("InventoryAdjustmentQuery", rpcSend);
        }
        
        //fill the model with the query results
        int i=0;
        
        if(model == null)
            model = new DataModel();
        else
        model.clear();
        
        while(i < inventoryAdjustmentNames.size() && i < leftTableRowsPerPage) {
            IdNameDateDO resultDO = (IdNameDateDO)inventoryAdjustmentNames.get(i);
 
            DataSet row = new DataSet();
            NumberObject id = new NumberObject(resultDO.getId());
            StringObject name = new StringObject(resultDO.getName());
            
            row.setKey(id);         
            row.add(name);
            model.add(row);
            i++;
        } 
 
        return model;
    }

    public FormRPC commitUpdate(FormRPC rpcSend, FormRPC rpcReturn) throws RPCException {
        //remote interface to call the inventory adjustment bean
        InventoryAdjustmentRemote remote = (InventoryAdjustmentRemote)EJBFactory.lookup("openelis/InventoryAdjustmentBean/remote");
        InventoryAdjustmentDO invAdjustmentDO = new InventoryAdjustmentDO();
        List childRows = new ArrayList();

        //build the inventoryAdjustmentDO from the form
        invAdjustmentDO = getInventoryAdjustmentDOFromRPC(rpcSend);
        
        //child info
        TableField adjTableField = (TableField)rpcSend.getField("adjustmentsTable");
        DataModel adjustmentsTable = (DataModel)adjTableField.getValue();
        childRows = getAdjustmentsListFromRPC(adjustmentsTable, invAdjustmentDO.getId());
        
        //validate the fields on the backend
        List exceptionList = remote.validateForUpdate(invAdjustmentDO, childRows);
        if(exceptionList.size() > 0){
            setRpcErrors(exceptionList, adjTableField, rpcSend);
            
            return rpcSend;
        } 
        
        //send the changes to the database
        try{
            remote.updateInventoryAdjustment(invAdjustmentDO, childRows);
        }catch(Exception e){
            if(e instanceof EntityLockedException)
                throw new RPCException(e.getMessage());
            
            exceptionList = new ArrayList();
            exceptionList.add(e);
            
            setRpcErrors(exceptionList, adjTableField, rpcSend);
            
            return rpcSend;
        }

        //set the fields in the RPC
        setFieldsInRPC(rpcReturn, invAdjustmentDO);   
        
        return rpcReturn;
    }

    public FormRPC fetch(DataSet key, FormRPC rpcReturn) throws RPCException {
        //remote interface to call the organization bean
        InventoryAdjustmentRemote remote = (InventoryAdjustmentRemote)EJBFactory.lookup("openelis/InventoryAdjustmentBean/remote");
        
        InventoryAdjustmentDO inventoryAdjustmentDO = remote.getInventoryAdjustment((Integer)((DataObject)key.getKey()).getValue());

        //set the fields in the RPC
        setFieldsInRPC(rpcReturn, inventoryAdjustmentDO);

        //load the adjustment child records
        List childList = remote.getChildRecords((Integer)((DataObject)key.getKey()).getValue());
        DataModel rmodel = (DataModel)fillChildrenTable((DataModel)rpcReturn.getField("adjustmentsTable").getValue(),childList);
        rpcReturn.setFieldValue("adjustmentsTable",rmodel);
        
        return rpcReturn;  
    }

    public FormRPC fetchForUpdate(DataSet key, FormRPC rpcReturn) throws RPCException {
        //remote interface to call the inventory adjustment bean
        InventoryAdjustmentRemote remote = (InventoryAdjustmentRemote)EJBFactory.lookup("openelis/InventoryAdjustmentBean/remote");
        
        InventoryAdjustmentDO invAdjustmentDO;
        List childList;
        
        try{
            invAdjustmentDO = remote.getInventoryAdjustmentAndLock((Integer)((DataObject)key.getKey()).getValue());
            childList = remote.getChildRecordsAndLock((Integer)((DataObject)key.getKey()).getValue());
            
        }catch(Exception e){
            throw new RPCException(e.getMessage());
        }
        
        //set the fields in the RPC
        setFieldsInRPC(rpcReturn, invAdjustmentDO);        
        
        DataModel rmodel = (DataModel)fillChildrenTable((DataModel)rpcReturn.getField("adjustmentsTable").getValue(),childList);
        rpcReturn.setFieldValue("adjustmentsTable",rmodel);
        
        return rpcReturn;  
    }

    public String getXML() throws RPCException {
        return ServiceUtils.getXML(Constants.APP_ROOT+"/Forms/inventoryAdjustment.xsl");
    }

    public HashMap getXMLData() throws RPCException {
        StringObject xml = new StringObject();
        xml.setValue(ServiceUtils.getXML(Constants.APP_ROOT+"/Forms/inventoryAdjustment.xsl"));
        
        DataModel storesDropdownField = (DataModel)CachingManager.getElement("InitialData", "inventoryItemStoresDropdown");
        
        if(storesDropdownField == null){
            storesDropdownField = getInitialModel("itemStores");
            CachingManager.putElement("InitialData", "inventoryItemStoresDropdown", storesDropdownField);
        }
                
        HashMap<String,Data> map = new HashMap<String,Data>();
        map.put("xml", xml);
        map.put("stores", storesDropdownField);
        
        return map;
    }

    public HashMap getXMLData(HashMap args) throws RPCException {
        // TODO Auto-generated method stub
        return null;
    }
    
    public DataModel getAddAutoFillValues() throws Exception {
        DataModel model = new DataModel();
        DataSet set = new DataSet();
        
        InventoryAdjustmentRemote remote = (InventoryAdjustmentRemote)EJBFactory.lookup("openelis/InventoryAdjustmentBean/remote");
        InventoryAdjustmentAddAutoFillDO autoDO;
        
        autoDO = remote.getAddAutoFillValues();
                
        StringField systemUser = new StringField();
        NumberField systemUserId = new NumberField(NumberObject.Type.INTEGER);
        DateField adjustmentDate = new DateField();
        
        systemUser.setValue(autoDO.getSystemUser());
        systemUserId.setValue(autoDO.getSystemUserId());
        adjustmentDate.setValue(DatetimeRPC.getInstance(Datetime.YEAR, Datetime.DAY, autoDO.getAdjustmentDate().getDate()));
        
        set.add(systemUser);
        set.add(systemUserId);
        set.add(adjustmentDate);
        
        model.add(set);
        
        return model;
    }
    
    public DataModel getInventoryItemInformation(NumberObject locId, NumberObject storeId){
        InventoryAdjustmentRemote remote = (InventoryAdjustmentRemote)EJBFactory.lookup("openelis/InventoryAdjustmentBean/remote");
        
        List invLocationRecords = remote.getInventoryitemData((Integer)locId.getValue(), (Integer)storeId.getValue());

        DataModel model = new DataModel(); 
         
        for(int i=0; i<invLocationRecords.size(); i++){
            InventoryAdjLocationAutoDO locDO = (InventoryAdjLocationAutoDO)invLocationRecords.get(i);
            DataSet set = new DataSet();
            
            NumberField locationId = new NumberField(NumberObject.Type.INTEGER);
            DropDownField inventoryItem = new DropDownField();
            StringField storageLocation = new StringField();
            NumberField qtyOnHand = new NumberField(NumberObject.Type.INTEGER);
            
            locationId.setValue(locDO.getInventoryLocationId());
            inventoryItem.setValue(locDO.getInventoryItem());
            storageLocation.setValue(locDO.getStorageLocation());
            qtyOnHand.setValue(locDO.getQuantityOnHand());
            
            //inventory item set
            DataModel itemModel = new DataModel();
            itemModel.add(new NumberObject(locDO.getInventoryItemId()),new StringObject(locDO.getInventoryItem()));
            inventoryItem.setModel(itemModel);
            inventoryItem.setValue(itemModel.get(0));
            
            set.add(locationId);
            set.add(inventoryItem);
            set.add(storageLocation);
            set.add(qtyOnHand);
            
            model.add(set);
        }
        
        return model;
    }
    
    public DataModel getMatchesObj(StringObject cat, DataModel model, StringObject match, DataMap params) throws RPCException {
        return getMatches((String)cat.getValue(), model, (String)match.getValue(), params);
        
    }

    public DataModel getMatches(String cat, DataModel model, String match, HashMap<String, Data> params) throws RPCException {
        if(cat.equals("inventoryItem"))
            return getInventoryItemMatches(match, params);
        
        return null;
    }
    
    private DataModel getInventoryItemMatches(String match, HashMap<String, Data> params) throws RPCException{
         Integer storeId = (Integer)((DropDownField)params.get("storeId")).getValue();
         
        if(storeId == null || storeId == 0){
            //we dont want to do anything...throw error
            throw new FormErrorException(openElisConstants.getString("inventoryAdjItemAutoException"));
        }

        DataModel dataModel = new DataModel();
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

            StringObject locationObject = new StringObject();
            locationObject.setValue(location);
            data.add(locationObject);
            StringObject lotNumObj = new StringObject();
            lotNumObj.setValue(lotNum);
            data.add(lotNumObj);
            StringObject expDateObj = new StringObject();
            expDateObj.setValue(expDate);
            data.add(expDateObj);
            NumberObject qtyObject = new NumberObject(NumberObject.Type.INTEGER);
            qtyObject.setValue(qty);
            data.add(qtyObject);
            
            NumberObject locNumObject = new NumberObject(NumberObject.Type.INTEGER);
            locNumObject.setValue(locId);
            data.setData(locNumObject);
           
            //add the dataset to the datamodel
            dataModel.add(data);                                 

        }       
        
        return dataModel;       
    }
    
    private DataModel getInitialModel(String cat){
        Integer id = null;
        CategoryRemote remote = (CategoryRemote)EJBFactory.lookup("openelis/CategoryBean/remote");

        if(cat.equals("itemStores"))
            id = remote.getCategoryId("inventory_item_stores");
        
        List<IdNameDO> entries = new ArrayList<IdNameDO>();
        if(id > -1)
            entries = (List<IdNameDO>)remote.getDropdownValues(id);
        
        
        //we need to build the model to return
        DataModel returnModel = new DataModel();
        
        if(entries.size() > 0){ 
            returnModel.add(new NumberObject(0),new StringObject(""));
        }
        
        for(IdNameDO resultDO : entries) { 
            returnModel.add(new NumberObject(resultDO.getId()),new StringObject(resultDO.getName()));
        }       
        
        return returnModel;
    }
    
    private void setFieldsInRPC(FormRPC rpcReturn, InventoryAdjustmentDO adjustmentDO){
        rpcReturn.setFieldValue(InventoryAdjustmentMeta.getId(), adjustmentDO.getId());
        rpcReturn.setFieldValue(InventoryAdjustmentMeta.getDescription(),adjustmentDO.getDescription());
        rpcReturn.setFieldValue(InventoryAdjustmentMeta.getSystemUserId(), adjustmentDO.getSystemUser());
        rpcReturn.setFieldValue("systemUserId", adjustmentDO.getSystemUserId());
        rpcReturn.setFieldValue(InventoryAdjustmentMeta.getAdjustmentDate(), DatetimeRPC.getInstance(Datetime.YEAR, Datetime.DAY, adjustmentDO.getAdjustmentDate().getDate()));
        rpcReturn.setFieldValue(InventoryAdjustmentMeta.TRANS_ADJUSTMENT_LOCATION_META.INVENTORY_LOCATION_META.INVENTORY_ITEM_META.getStoreId(), new DataSet(new NumberObject(adjustmentDO.getStoreId())));        
    }
    
    private InventoryAdjustmentDO getInventoryAdjustmentDOFromRPC(FormRPC rpcSend){
        InventoryAdjustmentDO inventoryAdjustmentDO = new InventoryAdjustmentDO();
        
        inventoryAdjustmentDO.setId((Integer) rpcSend.getFieldValue(InventoryAdjustmentMeta.getId()));
        inventoryAdjustmentDO.setAdjustmentDate(((DatetimeRPC)rpcSend.getFieldValue(InventoryAdjustmentMeta.getAdjustmentDate())).getDate());
        inventoryAdjustmentDO.setDescription((String) rpcSend.getFieldValue(InventoryAdjustmentMeta.getDescription()));
        inventoryAdjustmentDO.setSystemUser((String) rpcSend.getFieldValue(InventoryAdjustmentMeta.getSystemUserId()));
        inventoryAdjustmentDO.setSystemUserId((Integer) rpcSend.getFieldValue("systemUserId"));
        inventoryAdjustmentDO.setStoreId((Integer)((DropDownField)rpcSend.getField(InventoryAdjustmentMeta.TRANS_ADJUSTMENT_LOCATION_META.INVENTORY_LOCATION_META.INVENTORY_ITEM_META.getStoreId())).getSelectedKey());
        
        return inventoryAdjustmentDO;
    }
    
    private List<InventoryAdjustmentChildDO> getAdjustmentsListFromRPC(DataModel adjustmentsModel, Integer adjustmentId){
        List<InventoryAdjustmentChildDO> adjustmentsList = new ArrayList<InventoryAdjustmentChildDO>();
        List deletedRows = adjustmentsModel.getDeletions();
        
        for(int i=0; i<adjustmentsModel.size(); i++){
            InventoryAdjustmentChildDO childDO = new InventoryAdjustmentChildDO();
            DataSet row = adjustmentsModel.get(i);
            
            //hidden data
            NumberObject childId = (NumberObject)row.getKey();
            
            if(childId != null)
                childDO.setId((Integer)childId.getValue());
            
            childDO.setAdjustmentId(adjustmentId);
            childDO.setLocationId((Integer)((NumberField)row.get(0)).getValue());
            childDO.setAdjustedQuantity((Integer)((NumberField)row.get(5)).getValue());
            childDO.setPhysicalCount((Integer)((NumberField)row.get(4)).getValue());
            
            adjustmentsList.add(childDO);    
        }
        
        for(int j=0; j<deletedRows.size(); j++){
            DataSet deletedRow = (DataSet)deletedRows.get(j);
            if(deletedRow.getKey() != null){
                InventoryAdjustmentChildDO childDO = new InventoryAdjustmentChildDO();
                childDO.setDelete(true);
                childDO.setId((Integer)((NumberObject)deletedRow.getKey()).getValue());
                
                adjustmentsList.add(childDO);
            }
        }
        
        return adjustmentsList;
    }
    
    private void setRpcErrors(List exceptionList, TableField adjustmentsTable, FormRPC rpcSend){
        for (int i=0; i<exceptionList.size();i++) {
            //if the error is inside the org contacts table
            if(exceptionList.get(i) instanceof TableFieldErrorException){
                int rowindex = ((TableFieldErrorException)exceptionList.get(i)).getRowIndex();
                adjustmentsTable.getField(rowindex,((TableFieldErrorException)exceptionList.get(i)).getFieldName())
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
    
    private DataModel fillChildrenTable(DataModel childModel, List childrenList){
        try 
        {
            childModel.clear();
            
            for(int iter = 0;iter < childrenList.size();iter++) {
                InventoryAdjustmentChildDO childDO = (InventoryAdjustmentChildDO)childrenList.get(iter);

               DataSet row = childModel.createNewSet();
               
               NumberObject id = new NumberObject(childDO.getId());
               row.setKey(id);
               
               row.get(0).setValue(childDO.getLocationId());
               
               //we need to create a dataset for the inventory item auto complete
                if(childDO.getInventoryItemId() == null)
                    row.get(1).setValue(null);
                else{
                    DataModel itemModel = new DataModel();
                    itemModel.add(new NumberObject(childDO.getInventoryItemId()),new StringObject(childDO.getInventoryItem()));
                    ((DropDownField)row.get(1)).setModel(itemModel);
                    row.get(1).setValue(itemModel.get(0));
                }

                row.get(2).setValue(childDO.getStorageLocation());
                
                //we need to calculate the quantity on hand
                row.get(3).setValue(childDO.getPhysicalCount() + (-1 * childDO.getAdjustedQuantity()));
                
                row.get(4).setValue(childDO.getPhysicalCount());
                row.get(5).setValue(childDO.getAdjustedQuantity());
                
                childModel.add(row);
           } 
            
        } catch (Exception e) {

            e.printStackTrace();
            return null;
        }       
        
        return childModel;
    }
}
