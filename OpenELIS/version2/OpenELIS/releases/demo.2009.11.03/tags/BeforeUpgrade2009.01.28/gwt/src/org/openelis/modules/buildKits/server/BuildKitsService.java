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
package org.openelis.modules.buildKits.server;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.openelis.domain.BuildKitComponentDO;
import org.openelis.domain.BuildKitDO;
import org.openelis.domain.InventoryComponentDO;
import org.openelis.domain.InventoryItemAutoDO;
import org.openelis.domain.StorageLocationAutoDO;
import org.openelis.gwt.common.DatetimeRPC;
import org.openelis.gwt.common.FieldErrorException;
import org.openelis.gwt.common.FormErrorException;
import org.openelis.gwt.common.FormRPC;
import org.openelis.gwt.common.RPCException;
import org.openelis.gwt.common.TableFieldErrorException;
import org.openelis.gwt.common.ValidationErrorsList;
import org.openelis.gwt.common.FormRPC.Status;
import org.openelis.gwt.common.data.CheckField;
import org.openelis.gwt.common.data.Data;
import org.openelis.gwt.common.data.DataMap;
import org.openelis.gwt.common.data.DataModel;
import org.openelis.gwt.common.data.DataSet;
import org.openelis.gwt.common.data.DropDownField;
import org.openelis.gwt.common.data.NumberObject;
import org.openelis.gwt.common.data.StringObject;
import org.openelis.gwt.common.data.TableField;
import org.openelis.gwt.server.ServiceUtils;
import org.openelis.gwt.services.AppScreenFormServiceInt;
import org.openelis.gwt.services.AutoCompleteServiceInt;
import org.openelis.metamap.InventoryItemMetaMap;
import org.openelis.persistence.EJBFactory;
import org.openelis.remote.BuildKitsRemote;
import org.openelis.remote.InventoryItemRemote;
import org.openelis.remote.InventoryReceiptRemote;
import org.openelis.remote.StorageLocationRemote;
import org.openelis.server.constants.Constants;
import org.openelis.util.Datetime;
import org.openelis.util.SessionManager;
import org.openelis.util.UTFResource;

public class BuildKitsService implements AppScreenFormServiceInt<FormRPC, DataSet, DataModel>, AutoCompleteServiceInt{

    private static final InventoryItemMetaMap InventoryItemMeta = new InventoryItemMetaMap();
    private UTFResource openElisConstants= UTFResource.getBundle((String)SessionManager.getSession().getAttribute("locale"));
    
    //not used
    public FormRPC abort(DataSet key, FormRPC rpcReturn) throws RPCException {
        return null;
    }

    public FormRPC commitAdd(FormRPC rpcSend, FormRPC rpcReturn) throws RPCException {
        //remote interface to call the build kits bean
        BuildKitsRemote remote = (BuildKitsRemote)EJBFactory.lookup("openelis/BuildKitsBean/remote");
        BuildKitDO buildKitDO = new BuildKitDO();
        List<BuildKitComponentDO> components = new ArrayList<BuildKitComponentDO>();
        
        //build the build kits DO from the form
        buildKitDO = getBuildKitDOFromRPC(rpcSend);
        
        //contacts info
        TableField componentsField = (TableField)rpcSend.getField("subItemsTable");
        DataModel componentsTable = (DataModel)componentsField.getValue();
        components = getComponentsListFromRPC(componentsTable);
        
        //send the changes to the database
        try{
            remote.updateBuildKits(buildKitDO, components);
        }catch(Exception e){
            if(e instanceof ValidationErrorsList){
                setRpcErrors(((ValidationErrorsList)e).getErrorList(), componentsField, rpcSend);
                return rpcSend;
            }else
                throw new RPCException(e.getMessage());
        }
        
        //set the fields in the RPC
        setFieldsInRPC(rpcReturn, buildKitDO);

        return rpcReturn;
    }

    //not used
    public FormRPC commitDelete(DataSet key, FormRPC rpcReturn) throws RPCException {
        return null;
    }

    //not used
    public DataModel commitQuery(FormRPC rpcSend, DataModel model) throws RPCException {
        return null;
    }

    //not used
    public FormRPC commitUpdate(FormRPC rpcSend, FormRPC rpcReturn) throws RPCException {
        return null;
    }

    //not used
    public FormRPC fetch(DataSet key, FormRPC rpcReturn) throws RPCException {
        return null;
    }

    //not used
    public FormRPC fetchForUpdate(DataSet key, FormRPC rpcReturn) throws RPCException {
        return null;
    }

    public String getXML() throws RPCException {
        return ServiceUtils.getXML(Constants.APP_ROOT+"/Forms/buildKits.xsl");
    }

    public HashMap<String, Data> getXMLData() throws RPCException {
        return null;
    }

    public HashMap<String, Data> getXMLData(HashMap<String, Data> args) throws RPCException {
        return null;
    }
    
    private BuildKitDO getBuildKitDOFromRPC(FormRPC rpcSend){
        BuildKitDO buildKitDO = new BuildKitDO();
        
        buildKitDO.setKitId((Integer)((DropDownField)rpcSend.getField(InventoryItemMeta.getName())).getSelectedKey());
        buildKitDO.setKit((String)((DropDownField)rpcSend.getField(InventoryItemMeta.getName())).getTextValue());
        buildKitDO.setNumberRequested((Integer)rpcSend.getFieldValue("numRequested"));
        buildKitDO.setAddToExisiting("Y".equals((String)rpcSend.getFieldValue("addToExisting")));
        buildKitDO.setLocationId((Integer)((DropDownField)rpcSend.getField(InventoryItemMeta.INVENTORY_LOCATION.INVENTORY_LOCATION_STORAGE_LOCATION.getLocation())).getSelectedKey());
        buildKitDO.setLocation((String)((DropDownField)rpcSend.getField(InventoryItemMeta.INVENTORY_LOCATION.INVENTORY_LOCATION_STORAGE_LOCATION.getLocation())).getTextValue());
        buildKitDO.setLotNumber((String)rpcSend.getFieldValue(InventoryItemMeta.INVENTORY_LOCATION.getLotNumber()));
        if(rpcSend.getFieldValue(InventoryItemMeta.INVENTORY_LOCATION.getExpirationDate()) != null && 
                        ((DatetimeRPC)rpcSend.getFieldValue(InventoryItemMeta.INVENTORY_LOCATION.getExpirationDate())).getDate() != null)
            buildKitDO.setExpDate(((DatetimeRPC)rpcSend.getFieldValue(InventoryItemMeta.INVENTORY_LOCATION.getExpirationDate())).getDate());
        
        return buildKitDO;
    }
    
    private List<BuildKitComponentDO> getComponentsListFromRPC(DataModel componentsTable){
        List<BuildKitComponentDO> components = new ArrayList<BuildKitComponentDO>();
        
        for(int i=0; i<componentsTable.size(); i++){
            BuildKitComponentDO componentDO = new BuildKitComponentDO();
            DataSet row = componentsTable.get(i);
            
            componentDO.setComponent((String)row.get(0).getValue());
            componentDO.setLocationId((Integer)((DropDownField)row.get(1)).getSelectedKey());
            componentDO.setLotNum((String)row.get(2).getValue());
            componentDO.setUnit((Double)row.get(3).getValue());
            componentDO.setTotal((Integer)row.get(4).getValue());
            componentDO.setQtyOnHand((Integer)row.get(5).getValue());
            
            components.add(componentDO);    
        }
        
        return components;
    }
    
    private void setFieldsInRPC(FormRPC rpcReturn, BuildKitDO kitDO){
        //we need to create a dataset for the kit name auto complete
        if(kitDO.getKitId() == null)
            rpcReturn.setFieldValue(InventoryItemMeta.getName(), null);
        else{
            DataModel model = new DataModel();
            model.add(new NumberObject(kitDO.getKitId()),new StringObject(kitDO.getKit()));
            ((DropDownField)rpcReturn.getField(InventoryItemMeta.getName())).setModel(model);
            rpcReturn.setFieldValue(InventoryItemMeta.getName(), model.get(0));
        }
        rpcReturn.setFieldValue("addToExisiting", (kitDO.isAddToExisiting() ? "Y" : "N"));
        rpcReturn.setFieldValue("numRequested", kitDO.getNumberRequested());
        
        //we need to create a dataset for the kit location auto complete
        if(kitDO.getLocationId() == null)
            rpcReturn.setFieldValue(InventoryItemMeta.INVENTORY_LOCATION.INVENTORY_LOCATION_STORAGE_LOCATION.getLocation(), null);
        else{
            DataModel model = new DataModel();
            model.add(new NumberObject(kitDO.getLocationId()),new StringObject(kitDO.getLocation()));
            ((DropDownField)rpcReturn.getField(InventoryItemMeta.INVENTORY_LOCATION.INVENTORY_LOCATION_STORAGE_LOCATION.getLocation())).setModel(model);
            rpcReturn.setFieldValue(InventoryItemMeta.INVENTORY_LOCATION.INVENTORY_LOCATION_STORAGE_LOCATION.getLocation(), model.get(0));
        }
        
        rpcReturn.setFieldValue(InventoryItemMeta.INVENTORY_LOCATION.getLotNumber(), kitDO.getLotNumber());
        
        if(kitDO.getExpDate() != null && kitDO.getExpDate().getDate() != null)
            rpcReturn.setFieldValue(InventoryItemMeta.INVENTORY_LOCATION.getExpirationDate(), DatetimeRPC.getInstance(Datetime.YEAR, Datetime.DAY, kitDO.getExpDate().getDate()));
    }
    
    public DataModel getMatchesObj(StringObject cat, DataModel model, StringObject match, DataMap params) throws RPCException {
        return getMatches((String)cat.getValue(), model, (String)match.getValue(), params);
        
    }
    
    public DataModel getMatches(String cat, DataModel model, String match, HashMap params) throws RPCException {
        if(cat.equals("kitDropdown"))
            return getKitMatches(match);
        else if(cat.equals("invLocation"))
            return getLocationMatches(match, params);
        else if(cat.equals("componentLocation"))
            return getComponentLocationMatches(match, params);
        
        return null;    
    }
    
    private DataModel getKitMatches(String match) throws RPCException{
        InventoryItemRemote remote = (InventoryItemRemote)EJBFactory.lookup("openelis/InventoryItemBean/remote");
        DataModel dataModel = new DataModel();
        List autoCompleteList;
    
        //lookup by name
        autoCompleteList = remote.inventoryItemWithComponentsAutoCompleteLookupByName(match+"%", 10);
        
        for(int i=0; i < autoCompleteList.size(); i++){
            InventoryItemAutoDO resultDO = (InventoryItemAutoDO) autoCompleteList.get(i);
            
            Integer itemId = resultDO.getId();
            String name = resultDO.getName();
            String store = resultDO.getStore();
            String dispensedUnits = resultDO.getDispensedUnits();
            
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
            StringObject disUnitsObj = new StringObject();
            disUnitsObj.setValue(dispensedUnits);
            data.add(disUnitsObj);
                        
            //add the dataset to the datamodel
            dataModel.add(data);                        
        }       
        
        return dataModel;       
    }
    
    private DataModel getLocationMatches(String match, HashMap params) throws RPCException{
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
    
    private DataModel getComponentLocationMatches(String match, HashMap params) throws RPCException{
        DataModel dataModel = new DataModel();
        Integer id = null;
        List autoCompleteList = new ArrayList();
        
        id = (Integer)((NumberObject)params.get("id")).getValue();

        InventoryReceiptRemote remote = (InventoryReceiptRemote)EJBFactory.lookup("openelis/InventoryReceiptBean/remote");
        //lookup by name
        autoCompleteList = remote.autoCompleteLocationLookupByNameInvId(match+"%", id, 10);

        for(int i=0; i < autoCompleteList.size(); i++){
            StorageLocationAutoDO resultDO = (StorageLocationAutoDO) autoCompleteList.get(i);
            //id
            Integer autoId = resultDO.getId();
            //desc
            String desc = resultDO.getLocation();
          
            DataSet set = new DataSet();
            //hidden id
            NumberObject idObject = new NumberObject(autoId);
            set.setKey(idObject);
            //columns
            StringObject descObject = new StringObject();
            descObject.setValue(desc);
            set.add(descObject);
            
            DataMap map = new DataMap();
            map.put("qtyOnHand", new NumberObject(resultDO.getQtyOnHand()));
            map.put("lotNumber", new StringObject(resultDO.getLotNum()));
            set.setData(map);
            
            //add the dataset to the datamodel
            dataModel.add(set);                            
        }       
        
        return dataModel;       
    }
    
    public DataModel getComponentsFromId(NumberObject inventoryItemId){
        InventoryItemRemote remote = (InventoryItemRemote)EJBFactory.lookup("openelis/InventoryItemBean/remote");
        Integer invItemId = (Integer)inventoryItemId.getValue();
        DataModel model = new DataModel();
        
        List components = remote.getInventoryComponents(invItemId);
        
        for(int i=0; i<components.size(); i++){
            InventoryComponentDO componentDO = (InventoryComponentDO)components.get(i);
            DataSet set = new DataSet();
            NumberObject id = new NumberObject(componentDO.getComponentInventoryItemId());
            set.setKey(id);
            StringObject name = new StringObject(componentDO.getComponentName());
            set.add(name);
            NumberObject qty = new NumberObject(componentDO.getQuantity());
            set.add(qty);
            
            model.add(set);
        }
        
        return model;
    }
    
    private void setRpcErrors(List exceptionList, TableField componentsTable, FormRPC rpcSend){
        for (int i=0; i<exceptionList.size();i++) {
            //if the error is inside the org contacts table
            if(exceptionList.get(i) instanceof TableFieldErrorException){
                int rowindex = ((TableFieldErrorException)exceptionList.get(i)).getRowIndex();
                componentsTable.getField(rowindex,((TableFieldErrorException)exceptionList.get(i)).getFieldName())
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
}
