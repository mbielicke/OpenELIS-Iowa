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

import org.openelis.domain.InventoryComponentDO;
import org.openelis.domain.InventoryItemAutoDO;
import org.openelis.domain.StorageLocationAutoDO;
import org.openelis.entity.InventoryComponent;
import org.openelis.gwt.common.FormRPC;
import org.openelis.gwt.common.RPCException;
import org.openelis.gwt.common.data.CheckField;
import org.openelis.gwt.common.data.DataMap;
import org.openelis.gwt.common.data.DataModel;
import org.openelis.gwt.common.data.DataObject;
import org.openelis.gwt.common.data.DataSet;
import org.openelis.gwt.common.data.ModelObject;
import org.openelis.gwt.common.data.NumberField;
import org.openelis.gwt.common.data.NumberObject;
import org.openelis.gwt.common.data.StringObject;
import org.openelis.gwt.server.ServiceUtils;
import org.openelis.gwt.services.AppScreenFormServiceInt;
import org.openelis.gwt.services.AutoCompleteServiceInt;
import org.openelis.persistence.EJBFactory;
import org.openelis.remote.InventoryItemRemote;
import org.openelis.remote.InventoryReceiptRemote;
import org.openelis.remote.StorageLocationRemote;
import org.openelis.server.constants.Constants;

public class BuildKitsService implements AppScreenFormServiceInt, AutoCompleteServiceInt{

    public FormRPC abort(DataSet key, FormRPC rpcReturn) throws RPCException {
        // TODO Auto-generated method stub
        return null;
    }

    public FormRPC commitAdd(FormRPC rpcSend, FormRPC rpcReturn) throws RPCException {
        // TODO Auto-generated method stub
        return null;
    }

    public FormRPC commitDelete(DataSet key, FormRPC rpcReturn) throws RPCException {
        // TODO Auto-generated method stub
        return null;
    }

    public DataModel commitQuery(FormRPC rpcSend, DataModel model) throws RPCException {
        // TODO Auto-generated method stub
        return null;
    }

    public FormRPC commitUpdate(FormRPC rpcSend, FormRPC rpcReturn) throws RPCException {
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
        return ServiceUtils.getXML(Constants.APP_ROOT+"/Forms/buildKits.xsl");
    }

    public HashMap<String, DataObject> getXMLData() throws RPCException {
        // TODO Auto-generated method stub
        return null;
    }

    public HashMap<String, DataObject> getXMLData(HashMap<String, DataObject> args) throws RPCException {
        // TODO Auto-generated method stub
        return null;
    }
    
    public ModelObject getMatchesObj(StringObject cat, ModelObject model, StringObject match, DataMap params) throws RPCException {
        return new ModelObject(getMatches((String)cat.getValue(), (DataModel)model.getValue(), (String)match.getValue(), (HashMap)params.getValue()));
        
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
            data.addObject(nameObject);
            StringObject storeObject = new StringObject();
            storeObject.setValue(store);
            data.addObject(storeObject);
            StringObject disUnitsObj = new StringObject();
            disUnitsObj.setValue(dispensedUnits);
            data.addObject(disUnitsObj);
                        
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
            set.addObject(descObject);
            
            //add the dataset to the datamodel
            dataModel.add(set);                            

        }       
        
        return dataModel;       
    }
    
    private DataModel getComponentLocationMatches(String match, HashMap params) throws RPCException{
        DataModel dataModel = new DataModel();
        Integer id = null;
        List autoCompleteList = new ArrayList();
        
        id = (Integer)((NumberField)params.get("id")).getValue();

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
            set.addObject(descObject);
            NumberObject qtyOnHand = new NumberObject(resultDO.getQtyOnHand());
            set.addObject(qtyOnHand);
            
            //add the dataset to the datamodel
            dataModel.add(set);                            
        }       
        
        return dataModel;       
    }
    
    public ModelObject getComponentsFromId(NumberObject inventoryItemId){
        InventoryItemRemote remote = (InventoryItemRemote)EJBFactory.lookup("openelis/InventoryItemBean/remote");
        Integer invItemId = (Integer)inventoryItemId.getValue();
        ModelObject modelObj = new ModelObject();
        DataModel model = new DataModel();
        
        List components = remote.getInventoryComponents(invItemId);
        
        for(int i=0; i<components.size(); i++){
            InventoryComponentDO componentDO = (InventoryComponentDO)components.get(i);
            DataSet set = new DataSet();
            NumberObject id = new NumberObject(componentDO.getComponentInventoryItemId());
            set.setKey(id);
            StringObject name = new StringObject(componentDO.getComponentName());
            set.addObject(name);
            NumberObject qty = new NumberObject(componentDO.getQuantity());
            set.addObject(qty);
            
            model.add(set);
        }
        
        modelObj.setValue(model);
        
        return modelObj;
    }
}
