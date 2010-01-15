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


public class BuildKitsService {
}
/*
    private static final InventoryItemMetaMap InventoryItemMeta = new InventoryItemMetaMap();
    private UTFResource openElisConstants= UTFResource.getBundle((String)SessionManager.getSession().getAttribute("locale"));
    
    //not used
    public BuildKitsForm abort(BuildKitsForm rpc) throws Exception {
        return null;
    }

    public BuildKitsForm commitAdd(BuildKitsForm rpc) throws Exception {
        //remote interface to call the build kits bean
        BuildKitsRemote remote = (BuildKitsRemote)EJBFactory.lookup("openelis/BuildKitsBean/remote");
        BuildKitDO buildKitDO = new BuildKitDO();
        List<BuildKitComponentDO> components = new ArrayList<BuildKitComponentDO>();
        
        //build the build kits DO from the form
        buildKitDO = getBuildKitDOFromRPC(rpc);
        
        //components info
        TableField<TableDataRow<Integer>> componentsField = rpc.subItemsTable;
        TableDataModel<TableDataRow<Integer>> componentsTable = componentsField.getValue();
        components = getComponentsListFromRPC(componentsTable);
        
        //send the changes to the database
        try{
            remote.updateBuildKits(buildKitDO, components);
        }catch(Exception e){
            if(e instanceof ValidationErrorsList){
                setRpcErrors(((ValidationErrorsList)e).getErrorList(), componentsField, rpc);
                return rpc;
            }else
                throw new Exception(e.getMessage());
        }
        
        //set the fields in the RPC
        setFieldsInRPC(rpc, buildKitDO);

        return rpc;
    }

    //not used
    public BuildKitsForm commitDelete(BuildKitsForm rpc) throws Exception {
        return null;
    }

    //not used
    public Query<TableDataRow<Integer>> commitQuery(Query<TableDataRow<Integer>> model) throws Exception {
        return null;
    }

    //not used
    public BuildKitsForm commitUpdate(BuildKitsForm rpc) throws Exception {
        return null;
    }

    //not used
    public BuildKitsForm fetch(BuildKitsForm rpc) throws Exception {
        return null;
    }

    //not used
    public BuildKitsForm fetchForUpdate(BuildKitsForm rpc) throws Exception {
        return null;
    }
    
    public BuildKitsForm getScreen(BuildKitsForm rpc) throws Exception{
    	rpc.xml = ServiceUtils.getXML(Constants.APP_ROOT+"/Forms/buildKits.xsl");
        
        return rpc;
    }
    
    private BuildKitDO getBuildKitDOFromRPC(BuildKitsForm form){
        BuildKitDO buildKitDO = new BuildKitDO();
        
        DropDownField<Integer> invItemDrop = form.inventoryItem;
        //ArrayList<DataSet<Integer>> selected = invItemDrop.getValue();
        BuildKitsInvItemKey selectedData = null;
        //if(selected.size() > 0)
          //  selectedData = (BuildKitsInvItemKey)selected.get(0).getData();
        
        buildKitDO.setInventoryItemId((Integer)form.inventoryItem.getSelectedKey());
        buildKitDO.setInventoryItem((String)form.inventoryItem.getTextValue());
        buildKitDO.setNumberRequested(form.numRequested.getValue());
        buildKitDO.setAddToExisting("Y".equals(form.addToExisiting.getValue()));
        if(selectedData != null){
            buildKitDO.setBulk(selectedData.isBulk);
            buildKitDO.setSerialized(selectedData.isSerialized);
        }
        buildKitDO.setLocationId((Integer) form.storageLocation.getSelectedKey());
        buildKitDO.setLocation((String)form.storageLocation.getTextValue());
        buildKitDO.setLotNumber(form.lotNumber.getValue());
        if(form.expirationDate.getValue() != null && ((Datetime)form.expirationDate.getValue()).getDate() != null)
            buildKitDO.setExpDate(((Datetime)form.expirationDate.getValue()).getDate());
        
        return buildKitDO;
    }
    
    private List<BuildKitComponentDO> getComponentsListFromRPC(TableDataModel<TableDataRow<Integer>> componentsTable){
        List<BuildKitComponentDO> components = new ArrayList<BuildKitComponentDO>();
        
        for(int i=0; i<componentsTable.size(); i++){
            BuildKitComponentDO componentDO = new BuildKitComponentDO();
            TableDataRow<Integer> row = componentsTable.get(i);
            
            componentDO.setInventoryItemId(row.key);
            componentDO.setComponent((String)row.getCells().get(0).getValue());
            componentDO.setLocationId((Integer)((DropDownField<Integer>)row.getCells().get(1)).getSelectedKey());
            componentDO.setLotNum((String)row.getCells().get(2).getValue());
            componentDO.setUnit((Double)row.getCells().get(3).getValue());
            componentDO.setTotal((Integer)row.getCells().get(4).getValue());
            componentDO.setQtyOnHand((Integer)row.getCells().get(5).getValue());
            
            components.add(componentDO);    
        }
        
        return components;
    }
    
    private void setFieldsInRPC(BuildKitsForm form, BuildKitDO kitDO){
        //we need to create a dataset for the kit name auto complete
        if(kitDO.getInventoryItemId() == null)
        	form.inventoryItem.clear();
        else{
            TableDataModel<TableDataRow<Integer>> model = new TableDataModel<TableDataRow<Integer>>();
            model.add(new TableDataRow<Integer>(kitDO.getInventoryItemId(),new StringObject(kitDO.getInventoryItem())));
            form.inventoryItem.setModel(model);
            form.inventoryItem.setValue(model.get(0));
        }
        form.addToExisiting.setValue((kitDO.isAddToExisting() ? "Y" : "N"));
        form.numRequested.setValue(kitDO.getNumberRequested());
        
        //we need to create a dataset for the kit location auto complete
        if(kitDO.getLocationId() == null)
        	form.storageLocation.clear();
        else{
            TableDataModel<TableDataRow<Integer>> model = new TableDataModel<TableDataRow<Integer>>();
            model.add(new TableDataRow<Integer>(kitDO.getLocationId(),new StringObject(kitDO.getLocation())));
            form.storageLocation.setModel(model);
            form.storageLocation.setValue(model.get(0));
        }
        
        form.lotNumber.setValue(kitDO.getLotNumber());
        
        if(kitDO.getExpDate() != null && kitDO.getExpDate().getDate() != null)
            form.expirationDate.setValue(Datetime.getInstance(Datetime.YEAR, Datetime.DAY, kitDO.getExpDate().getDate()));
    }
    
    public SubLocationAutoRPC getMatchesObj(SubLocationAutoRPC rpc) throws Exception {
    	if("invLocation".equals(rpc.cat))
            rpc.matchesModel = getLocationMatches(rpc.match, rpc.addToExisting);
        else if("componentLocation".equals(rpc.cat))
            rpc.matchesModel = getComponentLocationMatches(rpc.match, rpc.id);
    	
        return rpc;
        
    }
    
    public TableDataModel getMatches(String cat, TableDataModel model, String match, HashMap params) throws Exception {
        if(cat.equals("kitDropdown"))
            return getKitMatches(match);
        
        return null;    
    }
    
    private TableDataModel getKitMatches(String match) throws Exception{
        InventoryItemRemote remote = (InventoryItemRemote)EJBFactory.lookup("openelis/InventoryItemBean/remote");
        TableDataModel<TableDataRow<Integer>> dataModel = new TableDataModel<TableDataRow<Integer>>();
        List autoCompleteList = null;
    
        //lookup by name
//        autoCompleteList = remote.inventoryItemWithComponentsAutoCompleteLookupByName(match+"%", 10);
        
        for(int i=0; i < autoCompleteList.size(); i++){
            InventoryItemAutoDO resultDO = (InventoryItemAutoDO) autoCompleteList.get(i);
            
            Integer itemId = resultDO.getId();
            String name = resultDO.getName();
            String store = resultDO.getStore();
            String dispensedUnits = resultDO.getDispensedUnits();
            
            TableDataRow<Integer> data = new TableDataRow<Integer>(itemId, 
                                                                   new FieldType[] {
                                                                                     new StringObject(name),
                                                                                     new StringObject(store),
                                                                                     new StringObject(dispensedUnits)
                                                                   }
                                         );
            
            //hidden fields
            BuildKitsInvItemKey hiddenData = new BuildKitsInvItemKey();
            hiddenData.isBulk = "Y".equals(resultDO.getIsBulk());
            hiddenData.isSerialized = "Y".equals(resultDO.getIsSerialMaintained());

//            data.setData(hiddenData);
                        
            //add the dataset to the datamodel
            dataModel.add(data);                        
        }       
        
        return dataModel;       
    }
    
    private TableDataModel getLocationMatches(String match, String addToExisting) throws Exception{
        TableDataModel<TableDataRow<Integer>> dataModel = new TableDataModel<TableDataRow<Integer>>();
        List autoCompleteList = new ArrayList();
        
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
            StorageLocationVO resultDO = (StorageLocationVO) autoCompleteList.get(i);
            //id
            Integer id = resultDO.getId();
            //desc
            String desc = resultDO.getLocation();
          
            TableDataRow<Integer> set = new TableDataRow<Integer>(id,new StringObject(desc));
            
            //add the dataset to the datamodel
            dataModel.add(set);                            

        }       
        
        return dataModel;       
    }
    
    private TableDataModel getComponentLocationMatches(String match, Integer id) throws Exception{
        TableDataModel<TableDataRow<Integer>> dataModel = new TableDataModel<TableDataRow<Integer>>();
        List autoCompleteList = new ArrayList();
        
        InventoryReceiptRemote remote = (InventoryReceiptRemote)EJBFactory.lookup("openelis/InventoryReceiptBean/remote");
        //lookup by name
        autoCompleteList = remote.autoCompleteLocationLookupByNameInvId(match+"%", id, 10);

        for(int i=0; i < autoCompleteList.size(); i++){
            StorageLocationVO resultDO = (StorageLocationVO) autoCompleteList.get(i);
            //id
            Integer autoId = resultDO.getLocationId();
            //desc
            String desc = resultDO.getLocation();
          
            TableDataRow<Integer> set = new TableDataRow<Integer>(autoId,
                                                                  new FieldType[] {
                                                                                   new StringObject(desc),
                                                                                   new StringObject(resultDO.getName()),
                                                                                   new IntegerObject(resultDO.getQtyOnHand())
                                                                  }
                                        );
            
            //add the dataset to the datamodel
            dataModel.add(set);                            
        }       
        
        return dataModel;       
    }
    
    public BuildKitsForm getComponentsFromId(BuildKitsForm rpc){
        InventoryItemRemote remote = (InventoryItemRemote)EJBFactory.lookup("openelis/InventoryItemBean/remote");
        Integer invItemId = rpc.kitId;
        TableDataModel<TableDataRow<Integer>> model = new TableDataModel<TableDataRow<Integer>>();
        /*                
        List components = remote.getInventoryComponents(invItemId);

        for(int i=0; i<components.size(); i++){
            InventoryComponentDO componentDO = (InventoryComponentDO)components.get(i);
            TableDataRow<Integer> set = new TableDataRow<Integer>(componentDO.getComponentInventoryItemId(),
                                                                  new FieldType[] {
                                                                                   new StringObject(componentDO.getComponentName()),
                                                                                   new DoubleObject(componentDO.getQuantity())
                                                                  }
                                        );            
            model.add(set);
        }
        rpc.subItemsModel = model;
        return rpc;
    }
    
    private void setRpcErrors(List exceptionList, TableField componentsTable, BuildKitsForm form){
        HashMap<String,AbstractField> map = null;
        if(exceptionList.size() > 0) {
            map = FormUtil.createFieldMap(form);
        }
        for (int i=0; i<exceptionList.size();i++) {
            //if the error is inside the org contacts table
            if(exceptionList.get(i) instanceof TableFieldErrorException){
                int rowindex = ((TableFieldErrorException)exceptionList.get(i)).getRowIndex();
                componentsTable.getField(rowindex,((TableFieldErrorException)exceptionList.get(i)).getFieldName())
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
    
    public BuildKitsForm fetchLocationAndLock(BuildKitsForm rpc) throws Exception {
        InventoryReceiptRemote remote = (InventoryReceiptRemote)EJBFactory.lookup("openelis/InventoryReceiptBean/remote");
        InventoryLocationDO locDO;
        
        try{
            locDO = remote.lockLocationAndFetch(rpc.lastLocId, rpc.locId);

        }catch(Exception e){
            throw new Exception(e.getMessage());
        }

        if(locDO != null)
            rpc.qtyOnHand = locDO.getQuantityOnHand();
        
        return rpc;
    }
    
    public void unlockLocations(InvReceiptItemInfoForm rpc){
        InventoryReceiptRemote remote = (InventoryReceiptRemote)EJBFactory.lookup("openelis/InventoryReceiptBean/remote");
        remote.unlockLocations(rpc.lockedIds);
        
    }
*/
