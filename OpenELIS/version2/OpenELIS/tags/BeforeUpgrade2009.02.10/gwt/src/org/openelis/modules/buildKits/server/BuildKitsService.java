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

import org.openelis.domain.BuildKitComponentDO;
import org.openelis.domain.BuildKitDO;
import org.openelis.domain.InventoryComponentDO;
import org.openelis.domain.InventoryItemAutoDO;
import org.openelis.domain.StorageLocationAutoDO;
import org.openelis.gwt.common.DatetimeRPC;
import org.openelis.gwt.common.FieldErrorException;
import org.openelis.gwt.common.Form;
import org.openelis.gwt.common.FormErrorException;
import org.openelis.gwt.common.RPC;
import org.openelis.gwt.common.RPCException;
import org.openelis.gwt.common.TableFieldErrorException;
import org.openelis.gwt.common.ValidationErrorsList;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class BuildKitsService implements AppScreenFormServiceInt<RPC, DataModel<DataSet>>, AutoCompleteServiceInt{

    private static final InventoryItemMetaMap InventoryItemMeta = new InventoryItemMetaMap();
    private UTFResource openElisConstants= UTFResource.getBundle((String)SessionManager.getSession().getAttribute("locale"));
    
    //not used
    public RPC abort(RPC rpc) throws RPCException {
        return null;
    }

    public RPC commitAdd(RPC rpc) throws RPCException {
        //remote interface to call the build kits bean
        BuildKitsRemote remote = (BuildKitsRemote)EJBFactory.lookup("openelis/BuildKitsBean/remote");
        BuildKitDO buildKitDO = new BuildKitDO();
        List<BuildKitComponentDO> components = new ArrayList<BuildKitComponentDO>();
        
        //build the build kits DO from the form
        buildKitDO = getBuildKitDOFromRPC(rpc.form);
        
        //components info
        TableField componentsField = (TableField)rpc.form.getField("subItemsTable");
        DataModel componentsTable = (DataModel)componentsField.getValue();
        components = getComponentsListFromRPC(componentsTable);
        
        //send the changes to the database
        try{
            remote.updateBuildKits(buildKitDO, components);
        }catch(Exception e){
            if(e instanceof ValidationErrorsList){
                setRpcErrors(((ValidationErrorsList)e).getErrorList(), componentsField, rpc.form);
                return rpc;
            }else
                throw new RPCException(e.getMessage());
        }
        
        //set the fields in the RPC
        setFieldsInRPC(rpc.form, buildKitDO);

        return rpc;
    }

    //not used
    public RPC commitDelete(RPC rpc) throws RPCException {
        return null;
    }

    //not used
    public DataModel<DataSet> commitQuery(Form form, DataModel<DataSet> model) throws RPCException {
        return null;
    }

    //not used
    public RPC commitUpdate(RPC rpc) throws RPCException {
        return null;
    }

    //not used
    public RPC fetch(RPC rpc) throws RPCException {
        return null;
    }

    //not used
    public RPC fetchForUpdate(RPC rpc) throws RPCException {
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
    
    public RPC getScreen(RPC rpc) {
        return rpc;
    }
    
    private BuildKitDO getBuildKitDOFromRPC(Form form){
        BuildKitDO buildKitDO = new BuildKitDO();
        
        DropDownField invItemDrop = (DropDownField)form.getField(InventoryItemMeta.getName());
        ArrayList<DataSet> selected = invItemDrop.getSelections();
        DataSet selectedRow = null;
        DataMap selectedRowMap = null;
        if(selected.size() > 0){
            selectedRow = selected.get(0);
            selectedRowMap = (DataMap)selectedRow.getData();
        }
        buildKitDO.setInventoryItemId((Integer)invItemDrop.getSelectedKey());
        buildKitDO.setInventoryItem((String)invItemDrop.getTextValue());
        buildKitDO.setNumberRequested((Integer)form.getFieldValue("numRequested"));
        buildKitDO.setAddToExisting("Y".equals((String)form.getFieldValue("addToExisting")));
        if(selectedRowMap != null){
            buildKitDO.setBulk("Y".equals((String)((StringObject)selectedRowMap.get("isBulk")).getValue()));
            buildKitDO.setSerialized("Y".equals((String)((StringObject)selectedRowMap.get("isSerialMaintained")).getValue()));
        }
        buildKitDO.setLocationId((Integer)((DropDownField)form.getField(InventoryItemMeta.INVENTORY_LOCATION.INVENTORY_LOCATION_STORAGE_LOCATION.getLocation())).getSelectedKey());
        buildKitDO.setLocation((String)((DropDownField)form.getField(InventoryItemMeta.INVENTORY_LOCATION.INVENTORY_LOCATION_STORAGE_LOCATION.getLocation())).getTextValue());
        buildKitDO.setLotNumber((String)form.getFieldValue(InventoryItemMeta.INVENTORY_LOCATION.getLotNumber()));
        if(form.getFieldValue(InventoryItemMeta.INVENTORY_LOCATION.getExpirationDate()) != null && 
                        ((DatetimeRPC)form.getFieldValue(InventoryItemMeta.INVENTORY_LOCATION.getExpirationDate())).getDate() != null)
            buildKitDO.setExpDate(((DatetimeRPC)form.getFieldValue(InventoryItemMeta.INVENTORY_LOCATION.getExpirationDate())).getDate());
        
        return buildKitDO;
    }
    
    private List<BuildKitComponentDO> getComponentsListFromRPC(DataModel<DataSet> componentsTable){
        List<BuildKitComponentDO> components = new ArrayList<BuildKitComponentDO>();
        
        for(int i=0; i<componentsTable.size(); i++){
            BuildKitComponentDO componentDO = new BuildKitComponentDO();
            DataSet<Data> row = componentsTable.get(i);
            
            componentDO.setInventoryItemId((Integer)((NumberObject)row.getKey()).getValue());
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
    
    private void setFieldsInRPC(Form form, BuildKitDO kitDO){
        //we need to create a dataset for the kit name auto complete
        if(kitDO.getInventoryItemId() == null)
            form.setFieldValue(InventoryItemMeta.getName(), null);
        else{
            DataModel<DataSet> model = new DataModel();
            model.add(new NumberObject(kitDO.getInventoryItemId()),new StringObject(kitDO.getInventoryItem()));
            ((DropDownField)form.getField(InventoryItemMeta.getName())).setModel(model);
            form.setFieldValue(InventoryItemMeta.getName(), model.get(0));
        }
        form.setFieldValue("addToExisiting", (kitDO.isAddToExisting() ? "Y" : "N"));
        form.setFieldValue("numRequested", kitDO.getNumberRequested());
        
        //we need to create a dataset for the kit location auto complete
        if(kitDO.getLocationId() == null)
            form.setFieldValue(InventoryItemMeta.INVENTORY_LOCATION.INVENTORY_LOCATION_STORAGE_LOCATION.getLocation(), null);
        else{
            DataModel<DataSet> model = new DataModel();
            model.add(new NumberObject(kitDO.getLocationId()),new StringObject(kitDO.getLocation()));
            ((DropDownField)form.getField(InventoryItemMeta.INVENTORY_LOCATION.INVENTORY_LOCATION_STORAGE_LOCATION.getLocation())).setModel(model);
            form.setFieldValue(InventoryItemMeta.INVENTORY_LOCATION.INVENTORY_LOCATION_STORAGE_LOCATION.getLocation(), model.get(0));
        }
        
        form.setFieldValue(InventoryItemMeta.INVENTORY_LOCATION.getLotNumber(), kitDO.getLotNumber());
        
        if(kitDO.getExpDate() != null && kitDO.getExpDate().getDate() != null)
            form.setFieldValue(InventoryItemMeta.INVENTORY_LOCATION.getExpirationDate(), DatetimeRPC.getInstance(Datetime.YEAR, Datetime.DAY, kitDO.getExpDate().getDate()));
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
            
            //hidden fields
            DataMap map = new DataMap();
            StringObject isBulkObj = new StringObject(resultDO.getIsBulk());
            map.put("isBulk", isBulkObj);
            StringObject isSerialMaintainedObj = new StringObject(resultDO.getIsSerialMaintained());
            map.put("isSerialMaintained", isSerialMaintainedObj);
            data.setData(map);
                        
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
            Integer autoId = resultDO.getLocationId();
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
            StringObject lotNum = new StringObject(resultDO.getLotNum());
            set.add(lotNum);
            NumberObject qtyOnHand = new NumberObject(resultDO.getQtyOnHand());
            set.add(qtyOnHand);
            
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
    
    private void setRpcErrors(List exceptionList, TableField componentsTable, Form form){
        for (int i=0; i<exceptionList.size();i++) {
            //if the error is inside the org contacts table
            if(exceptionList.get(i) instanceof TableFieldErrorException){
                int rowindex = ((TableFieldErrorException)exceptionList.get(i)).getRowIndex();
                componentsTable.getField(rowindex,((TableFieldErrorException)exceptionList.get(i)).getFieldName())
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
}
