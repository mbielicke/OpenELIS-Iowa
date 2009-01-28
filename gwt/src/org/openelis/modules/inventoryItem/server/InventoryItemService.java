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
package org.openelis.modules.inventoryItem.server;

import org.openelis.domain.IdNameDO;
import org.openelis.domain.IdNameStoreDO;
import org.openelis.domain.InventoryComponentDO;
import org.openelis.domain.InventoryItemAutoDO;
import org.openelis.domain.InventoryItemDO;
import org.openelis.domain.InventoryLocationDO;
import org.openelis.domain.NoteDO;
import org.openelis.gwt.common.EntityLockedException;
import org.openelis.gwt.common.FieldErrorException;
import org.openelis.gwt.common.Form;
import org.openelis.gwt.common.FormErrorException;
import org.openelis.gwt.common.LastPageException;
import org.openelis.gwt.common.QueryException;
import org.openelis.gwt.common.RPC;
import org.openelis.gwt.common.RPCException;
import org.openelis.gwt.common.TableFieldErrorException;
import org.openelis.gwt.common.data.AbstractField;
import org.openelis.gwt.common.data.BooleanObject;
import org.openelis.gwt.common.data.Data;
import org.openelis.gwt.common.data.DataMap;
import org.openelis.gwt.common.data.DataModel;
import org.openelis.gwt.common.data.DataObject;
import org.openelis.gwt.common.data.DataSet;
import org.openelis.gwt.common.data.DropDownField;
import org.openelis.gwt.common.data.NumberField;
import org.openelis.gwt.common.data.NumberObject;
import org.openelis.gwt.common.data.StringField;
import org.openelis.gwt.common.data.StringObject;
import org.openelis.gwt.common.data.TableField;
import org.openelis.gwt.server.ServiceUtils;
import org.openelis.gwt.services.AppScreenFormServiceInt;
import org.openelis.gwt.services.AutoCompleteServiceInt;
import org.openelis.gwt.widget.CheckBox;
import org.openelis.metamap.InventoryItemMetaMap;
import org.openelis.persistence.CachingManager;
import org.openelis.persistence.EJBFactory;
import org.openelis.remote.CategoryRemote;
import org.openelis.remote.InventoryItemRemote;
import org.openelis.security.domain.SystemUserDO;
import org.openelis.security.remote.SystemUserRemote;
import org.openelis.server.constants.Constants;
import org.openelis.util.Datetime;
import org.openelis.util.SessionManager;
import org.openelis.util.UTFResource;
import org.openelis.util.XMLUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class InventoryItemService implements AppScreenFormServiceInt<RPC, DataModel<DataSet>>, 
									     AutoCompleteServiceInt {
    
    private static final int leftTableRowsPerPage = 22;
    
    private static final InventoryItemMetaMap InvItemMeta = new InventoryItemMetaMap();
    
    private UTFResource openElisConstants= UTFResource.getBundle((String)SessionManager.getSession().getAttribute("locale"));

	public DataModel<DataSet> commitQuery(Form form, DataModel<DataSet> model) throws RPCException {
        List inventoryItemNames;
        //if the rpc is null then we need to get the page
        if(form == null){

            form = (Form)SessionManager.getSession().getAttribute("InventoryItemQuery");
    
            if(form == null)
                throw new QueryException(openElisConstants.getString("queryExpiredException"));

            InventoryItemRemote remote = (InventoryItemRemote)EJBFactory.lookup("openelis/InventoryItemBean/remote");
            try{
                inventoryItemNames = remote.query(form.getFieldMap(), (model.getPage()*leftTableRowsPerPage), leftTableRowsPerPage+1);
            }catch(Exception e){
                if(e instanceof LastPageException){
                    throw new LastPageException(openElisConstants.getString("lastPageException"));
                }else{
                    throw new RPCException(e.getMessage()); 
                }           
            }    
        }else{
            InventoryItemRemote remote = (InventoryItemRemote)EJBFactory.lookup("openelis/InventoryItemBean/remote");

            HashMap<String,AbstractField> fields = form.getFieldMap();
            fields.remove("componentsTable");
            fields.remove("locQuantitiesTable");

            try{    
                inventoryItemNames = remote.query(fields,0,leftTableRowsPerPage);
    
            }catch(Exception e){
                throw new RPCException(e.getMessage());
            }
    
        
            //need to save the rpc used to the encache
            SessionManager.getSession().setAttribute("InventoryItemQuery", form);
        }
        
        //fill the model with the query results
        int i=0;
        if(model == null)
            model = new DataModel();
        else
            model.clear();
        while(i < inventoryItemNames.size() && i < leftTableRowsPerPage) {
            IdNameStoreDO resultDO = (IdNameStoreDO)inventoryItemNames.get(i);

            model.add(new NumberObject(resultDO.getId()),new DataObject[] {new StringObject(resultDO.getName()), new StringObject(resultDO.getStore())});
            i++;
        } 
 
        return model;
    }

    public RPC commitAdd(RPC rpc) throws RPCException {
        //remote interface to call the inventory bean
        InventoryItemRemote remote = (InventoryItemRemote)EJBFactory.lookup("openelis/InventoryItemBean/remote");
        InventoryItemDO inventoryItemDO = new InventoryItemDO();
        List components = new ArrayList();
       
        NoteDO inventoryItemNote = new NoteDO();

        inventoryItemDO = getInventoryItemDOFromRPC(rpc.form);
        
        //components info
        TableField componentsField = (TableField)((Form)rpc.form.getField("components")).getField("componentsTable");
        DataModel componentsTable = (DataModel)componentsField.getValue();
        components = getComponentsListFromRPC(componentsTable, inventoryItemDO.getId());
        
        //build the noteDo from the form
        Form noteRPC = (Form)rpc.form.getField("comments");
        inventoryItemNote.setSubject((String)noteRPC.getFieldValue(InvItemMeta.ITEM_NOTE.getSubject()));
        inventoryItemNote.setText((String)noteRPC.getFieldValue(InvItemMeta.ITEM_NOTE.getText()));
        inventoryItemNote.setIsExternal("Y");
        
        //validate the fields on the backend
        List exceptionList = remote.validateForAdd(inventoryItemDO, components);
        
        if(exceptionList.size() > 0){
            setRpcErrors(exceptionList, componentsField, rpc.form);
            return rpc;
        } 
        
        //send the changes to the database
        Integer inventoryItemId;
        try{
            inventoryItemId = (Integer)remote.updateInventory(inventoryItemDO, components, inventoryItemNote);
        }catch(Exception e){
            exceptionList = new ArrayList();
            exceptionList.add(e);
            
            setRpcErrors(exceptionList, componentsField, rpc.form);
            
            return rpc;
        }
        
        inventoryItemDO.setId(inventoryItemId);

        //set the fields in the RPC
        setFieldsInRPC(rpc.form, inventoryItemDO);

        //we need to refresh the comments tab if it is showing
        String tab = (String)rpc.form.getFieldValue("itemTabPanel");
        if(tab.equals("commentsTab")){
            DataSet key = new DataSet();
            NumberObject id = new NumberObject(NumberObject.Type.INTEGER, inventoryItemDO.getId());
            key.setKey(id);
            
            loadComments((DataSet)key, (Form)rpc.form.getField("notes"));
        }
        
        //we need to clear out the note subject and the note text fields after a commit
        ((Form)rpc.form.getField("comments")).setFieldValue(InvItemMeta.getNote().getSubject(), null);
        ((Form)rpc.form.getField("comments")).setFieldValue(InvItemMeta.getNote().getText(), null);  
        
        //we need to set the component rpc value to the orig component value since it is an add
        ((Form)rpc.form.getField("components")).setFieldValue("componentsTable", componentsTable);
        
        return rpc;
    }

    public RPC commitUpdate(RPC rpc) throws RPCException {
        //remote interface to call the inventory bean
        InventoryItemRemote remote = (InventoryItemRemote)EJBFactory.lookup("openelis/InventoryItemBean/remote");
        InventoryItemDO inventoryItemDO = new InventoryItemDO();
        List components = new ArrayList();
        //List locations = new ArrayList();
        NoteDO inventoryItemNote = new NoteDO();

        //build the organizationAddress DO from the form
        inventoryItemDO = getInventoryItemDOFromRPC(rpc.form);
        
        //components info
        TableField componentsField = (TableField)((Form)rpc.form.getField("components")).getField("componentsTable");
        DataModel componentsTable = (DataModel)componentsField.getValue();
        components = getComponentsListFromRPC(componentsTable, inventoryItemDO.getId());   
        
        //build the noteDo from the form
        Form noteRPC = (Form)rpc.form.getField("comments");
        inventoryItemNote.setSubject((String)noteRPC.getFieldValue(InvItemMeta.ITEM_NOTE.getSubject()));
        inventoryItemNote.setText((String)noteRPC.getFieldValue(InvItemMeta.ITEM_NOTE.getText()));
        inventoryItemNote.setIsExternal("Y");
        
        //validate the fields on the backend
        List exceptionList = remote.validateForUpdate(inventoryItemDO, components);
        if(exceptionList.size() > 0){
            setRpcErrors(exceptionList, componentsField, rpc.form);
            
            return rpc;
        } 
        
        //send the changes to the database
        try{
            remote.updateInventory(inventoryItemDO, components, inventoryItemNote);
        }catch(Exception e){
            if(e instanceof EntityLockedException)
                throw new RPCException(e.getMessage());
            
            exceptionList = new ArrayList();
            exceptionList.add(e);
            
            setRpcErrors(exceptionList, componentsField, rpc.form);
            
            return rpc;
        }
        
        //set the fields in the RPC
        setFieldsInRPC(rpc.form, inventoryItemDO);   
        
        //we need to refresh the comments tab if it is showing
        String tab = (String)rpc.form.getFieldValue("itemTabPanel");
        if(tab.equals("commentsTab")){
            DataSet key = new DataSet();
            NumberObject id = new NumberObject(NumberObject.Type.INTEGER, inventoryItemDO.getId());
            key.setKey(id);
            
            loadComments(key, (Form)rpc.form.getField("comments"));
        }
        
        //we need to set the notes load param to true because update doesnt call resetRPC
        ((Form)rpc.form.getField("comments")).load = false;
        
        //we need to clear out the note subject and the note text fields after a commit
        ((Form)rpc.form.getField("comments")).setFieldValue(InvItemMeta.getNote().getSubject(), null);
        ((Form)rpc.form.getField("comments")).setFieldValue(InvItemMeta.getNote().getText(), null);  
        
        return rpc;
    }

    public RPC commitDelete(RPC rpc) throws RPCException {
    	// TODO Auto-generated method stub
    	return null;
    }

    public RPC abort(RPC rpc) throws RPCException {
        //remote interface to call the inventory bean
        InventoryItemRemote remote = (InventoryItemRemote)EJBFactory.lookup("openelis/InventoryItemBean/remote");
        
        
        InventoryItemDO inventoryItemDO = remote.getInventoryItemAndUnlock((Integer)((DataObject)((DataSet)rpc.key).getKey()).getValue(), SessionManager.getSession().getId());

        //set the fields in the RPC
        setFieldsInRPC(rpc.form, inventoryItemDO);
        
        String tab = (String)rpc.form.getFieldValue("itemTabPanel");
        if(tab.equals("componentsTab")){
            loadComponents((DataSet)rpc.key, new BooleanObject(false), (Form)rpc.form.getField("components"));
        }
       
        if(tab.equals("locationsTab")){
            loadLocations((DataSet)rpc.key, new StringObject(inventoryItemDO.getIsSerialMaintained()), (Form)rpc.form.getField("locations"));
        }
        
        if(tab.equals("commentsTab")){
            loadComments((DataSet)rpc.key, (Form)rpc.form.getField("comments"));
        }
        
        return rpc;  
    }

    public RPC fetch(RPC rpc) throws RPCException {
        //remote interface to call the inventory bean
        InventoryItemRemote remote = (InventoryItemRemote)EJBFactory.lookup("openelis/InventoryItemBean/remote");
        
        InventoryItemDO inventoryItemDO = remote.getInventoryItem((Integer)((DataObject)((DataSet)rpc.key).getKey()).getValue());

        //set the fields in the RPC
        setFieldsInRPC(rpc.form, inventoryItemDO);
        
        String tab = (String)rpc.form.getFieldValue("itemTabPanel");
        if(tab.equals("componentsTab")){
            loadComponents((DataSet)rpc.key, new BooleanObject(false), (Form)rpc.form.getField("components"));
        }
       
        if(tab.equals("locationsTab")){
            loadLocations((DataSet)rpc.key, new StringObject(inventoryItemDO.getIsSerialMaintained()), (Form)rpc.form.getField("locations"));
        }
        
        if(tab.equals("commentsTab")){
            loadComments((DataSet)rpc.key, (Form)rpc.form.getField("comments"));
        }
        
      return rpc;
    }

    public RPC fetchForUpdate(RPC rpc) throws RPCException {
        //remote interface to call the inventory bean
        InventoryItemRemote remote = (InventoryItemRemote)EJBFactory.lookup("openelis/InventoryItemBean/remote");
        
        InventoryItemDO inventoryItemDO = new InventoryItemDO();
        try{
            inventoryItemDO = remote.getInventoryItemAndLock((Integer)((DataObject)((DataSet)rpc.key).getKey()).getValue(), SessionManager.getSession().getId());
        }catch(Exception e){
            throw new RPCException(e.getMessage());
        }
        
        //set the fields in the RPC
        setFieldsInRPC(rpc.form, inventoryItemDO);
        
        String tab = (String)rpc.form.getFieldValue("itemTabPanel");
        if(tab.equals("componentsTab")){
            loadComponents((DataSet)rpc.key, new BooleanObject(false), (Form)rpc.form.getField("components"));
        }
       
        if(tab.equals("locationsTab")){
            loadLocations((DataSet)rpc.key, new StringObject(inventoryItemDO.getIsSerialMaintained()), (Form)rpc.form.getField("locations"));
        }
        
        if(tab.equals("commentsTab")){
            loadComments((DataSet)rpc.key, (Form)rpc.form.getField("comments"));
        }
        
        return rpc;  
    }
    
    public Form loadComponents(DataSet key, BooleanObject forDuplicate, Form form) throws RPCException {
        getComponentsModel((NumberObject)key.getKey(), forDuplicate, (TableField)form.getField("componentsTable"));
        form.load = true;
        return form;
    }
    
    public Form loadLocations(DataSet key, StringObject isSerialized, Form form) throws RPCException {
        getLocationsModel((NumberObject)key.getKey(), isSerialized, (TableField)form.getField("locQuantitiesTable"));
        form.load = true;
        return form;
    }
    
    public Form loadComments(DataSet key, Form form) throws RPCException {
        StringObject so = getCommentsModel((NumberObject)key.getKey());
        form.setFieldValue("notesPanel",so.getValue());
        form.load = true;
        return form;
    }

    public String getXML() throws RPCException {
    	return ServiceUtils.getXML(Constants.APP_ROOT+"/Forms/inventoryItem.xsl");
    }

    public HashMap<String,Data> getXMLData() throws RPCException {
        StringObject xml = new StringObject();
        xml.setValue(ServiceUtils.getXML(Constants.APP_ROOT+"/Forms/inventoryItem.xsl"));
        
        DataModel categoriesDropdownField = (DataModel)CachingManager.getElement("InitialData", "inventoryItemCategoriesDropdown");
        DataModel storesDropdownField = (DataModel)CachingManager.getElement("InitialData", "inventoryItemStoresDropdown");
        DataModel unitsDropdownField = (DataModel)CachingManager.getElement("InitialData", "inventoryItemUnitsDropdown");
        
        //state dropdown
        if(categoriesDropdownField == null){
            categoriesDropdownField = getInitialModel("itemCategories");
            CachingManager.putElement("InitialData", "inventoryItemCategoriesDropdown", categoriesDropdownField);
        }
        //country dropdown
        if(storesDropdownField == null){
            storesDropdownField = getInitialModel("itemStores");
            CachingManager.putElement("InitialData", "inventoryItemStoresDropdown", storesDropdownField);
            }
        //contact type dropdown
        if(unitsDropdownField == null){
            unitsDropdownField = getInitialModel("itemUnits");
            CachingManager.putElement("InitialData", "inventoryItemUnitsDropdown", unitsDropdownField);
        }
        
        HashMap<String,Data> map = new HashMap<String,Data>();
        map.put("xml", xml);
        map.put("categories", categoriesDropdownField);
        map.put("stores", storesDropdownField);
        map.put("units", unitsDropdownField);
        
        return map;
    }

    public HashMap<String,Data> getXMLData(HashMap<String,Data> args) throws RPCException {
    	return null;
    }
    
    public RPC getScreen(RPC rpc) {
        return rpc;
    }
    
    public TableField getComponentsModel(NumberObject itemId, BooleanObject forDuplicate, TableField model){
        InventoryItemRemote remote = (InventoryItemRemote)EJBFactory.lookup("openelis/InventoryItemBean/remote");
        List componentsList = remote.getInventoryComponents((Integer)itemId.getValue());
        model.setValue(fillComponentsTable((DataModel)model.getValue(),(Boolean)forDuplicate.getValue(), componentsList));
        return model;
    }
    
    public TableField getLocationsModel(NumberObject itemId, StringObject isSerialized, TableField model){
        InventoryItemRemote remote = (InventoryItemRemote)EJBFactory.lookup("openelis/InventoryItemBean/remote");
        List locationsList = remote.getInventoryLocations((Integer)itemId.getValue());
        model.setValue(fillLocationsTable((DataModel)model.getValue(), (String)isSerialized.getValue(), locationsList));
        return model;
    }
    
    public StringObject getCommentsModel(NumberObject key){
        //remote interface to call the organization bean
        InventoryItemRemote remote = (InventoryItemRemote)EJBFactory.lookup("openelis/InventoryItemBean/remote");
    
        //gets the whole notes list now
        List notesList = remote.getInventoryNotes((Integer)key.getValue());
        
        Iterator itr = notesList.iterator();
        try{
        Document doc = XMLUtil.createNew("panel");
        Element root = (Element) doc.getDocumentElement();
        root.setAttribute("key", "notePanel");   
        int i=0;
        while(itr.hasNext()){
            NoteDO noteRow = (NoteDO)itr.next();
            
            //user id
            Integer userId = noteRow.getSystemUser();
            //body
            String body = noteRow.getText();
            
            if(body == null)
                body = "";
            
            //date
            String date = noteRow.getTimestamp().toString();
            //subject
            String subject = noteRow.getSubject();
            
            if(subject == null)
                subject = "";
                        
            
            SystemUserRemote securityRemote = (SystemUserRemote)EJBFactory.lookup("SystemUserBean/remote");
            SystemUserDO user = securityRemote.getSystemUser(userId,false);
            
            String userName = user.getLoginName().trim();  
    
            Element mainRowPanel = (Element) doc.createElement("VerticalPanel");
             Element topRowPanel = (Element) doc.createElement("HorizontalPanel");
             Element titleWidgetTag = (Element) doc.createElement("widget");
             Element titleText = (Element) doc.createElement("text");
             Element authorWidgetTag = (Element) doc.createElement("widget");
             Element authorPanel = (Element) doc.createElement("VerticalPanel");
             Element dateText = (Element) doc.createElement("text");
             Element authorText = (Element) doc.createElement("text");
             Element bodyWidgetTag = (Element) doc.createElement("widget");
             Element bodytextTag =  (Element) doc.createElement("text");
             
             mainRowPanel.setAttribute("key", "note"+i);
             if(i % 2 == 1){
                    mainRowPanel.setAttribute("style", "AltTableRow");
                }else{
                 mainRowPanel.setAttribute("style", "TableRow");
                }
             mainRowPanel.setAttribute("width", "530px");
             
             topRowPanel.setAttribute("width", "530px");
             titleText.setAttribute("key", "note"+i+"Title");
             titleText.setAttribute("style", "notesSubjectText");
             titleText.appendChild(doc.createTextNode(subject));
             authorWidgetTag.setAttribute("halign", "right");
             dateText.setAttribute("key", "note"+i+"Date");
             dateText.appendChild(doc.createTextNode(date));
             authorText.setAttribute("key", "note"+i+"Author");
             authorText.appendChild(doc.createTextNode("by "+userName));
             bodytextTag.setAttribute("key", "note"+i+"Body");
             bodytextTag.setAttribute("wordwrap", "true");
             bodytextTag.appendChild(doc.createTextNode(body));
             
             root.appendChild(mainRowPanel);
             mainRowPanel.appendChild(topRowPanel);
             mainRowPanel.appendChild(bodyWidgetTag);
             topRowPanel.appendChild(titleWidgetTag);
             topRowPanel.appendChild(authorWidgetTag);
             titleWidgetTag.appendChild(titleText);
             authorWidgetTag.appendChild(authorPanel);
             authorPanel.appendChild(dateText);
             authorPanel.appendChild(authorText);
             bodyWidgetTag.appendChild(bodytextTag);
             
             i++;
      }
    
        StringObject returnObject = new StringObject();
        returnObject.setValue(XMLUtil.toString(doc));
        
        return returnObject;
        
        }catch(Exception e){
            System.out.println(e.getMessage());
        }
        return null;
    }

    public DataModel fillComponentsTable(DataModel componentsModel, boolean forDuplicate, List componentsList){
        try {
            componentsModel.clear();
            
            for(int iter = 0;iter < componentsList.size();iter++) {
                InventoryComponentDO componentRow = (InventoryComponentDO)componentsList.get(iter);
    
                   DataSet<Data> row = componentsModel.createNewSet();
                   NumberObject id = new NumberObject(NumberObject.Type.INTEGER);
                   
                   if(componentRow.getId() != null && !forDuplicate){
                       id.setValue(componentRow.getId());
                       row.setKey(id);
                   }

//                  we need to create a dataset for the component auto complete
                    if(componentRow.getComponentNameId() == null)
                        row.get(0).setValue(null);
                    else{
                        DataModel componentModel = new DataModel();
                        componentModel.add(new NumberObject(componentRow.getComponentNameId()),new StringObject(componentRow.getComponentName()));
                        ((DropDownField)row.get(0)).setModel(componentModel);
                        row.get(0).setValue(componentModel.get(0));
                    }
                    
                    row.get(1).setValue(componentRow.getComponentDesc());
                    row.get(2).setValue(componentRow.getQuantity());     
                    
                    componentsModel.add(row);
           } 
            
        } catch (Exception e) {
    
            e.printStackTrace();
            return null;
        }       
        
        return componentsModel;
    }
    
    public DataModel fillLocationsTable(DataModel locationsModel, String isSerialized, List locationsList){
        try {
            locationsModel.clear();
            
            for(int iter = 0;iter < locationsList.size();iter++) {
                InventoryLocationDO locationRow = (InventoryLocationDO)locationsList.get(iter);
    
                   DataSet<Data> row = locationsModel.createNewSet();
                   NumberField id = new NumberField(locationRow.getId());
           
                    row.setKey(id);
                    
                    row.get(0).setValue(locationRow.getStorageLocation());
                    row.get(1).setValue(locationRow.getLotNumber());
                    
                    if(CheckBox.CHECKED.equals(isSerialized))
                        row.get(2).setValue(locationRow.getId());
                    else
                        row.get(2).setValue(null);
                    
                    Datetime expDate = locationRow.getExpirationDate();
                    if(expDate.getDate() != null)
                        row.get(3).setValue(expDate.toString());
                    else
                        row.get(3).setValue(null);
                    
                    row.get(4).setValue(locationRow.getQuantityOnHand());
                    
                    locationsModel.add(row);
           } 
            
        } catch (Exception e) {
    
            e.printStackTrace();
            return null;
        }       
        
        return locationsModel;
    }
    
    public DataModel getMatchesObj(StringObject cat, DataModel model, StringObject match, DataMap params) throws RPCException {
        return getMatches((String)cat.getValue(), model, (String)match.getValue(), params);
        
    }
    
    public DataModel getMatches(String cat, DataModel model, String match, HashMap params) throws RPCException {
        if(cat.equals("component"))
            return getComponentMatches(match, params);
        else if(cat.equals("queryComponent"))
            return getComponentMatches(match, null);
        else if(cat.equals("parentItem"))
            return getParentItemMatches(match);
        
        return null;    
    }
    
    private DataModel getParentItemMatches(String match) throws RPCException{
        InventoryItemRemote remote = (InventoryItemRemote)EJBFactory.lookup("openelis/InventoryItemBean/remote");
        DataModel dataModel = new DataModel();
        List autoCompleteList;

        String parsedMatch = match.replace('*', '%');
        
        autoCompleteList = remote.inventoryItemStoreAutoCompleteLookupByName(parsedMatch+"%", 10, false, true);
        
        for(int i=0; i < autoCompleteList.size(); i++){
            InventoryItemAutoDO resultDO = (InventoryItemAutoDO) autoCompleteList.get(i);
            
            Integer itemId = resultDO.getId();
            String name = resultDO.getName();
            String store = resultDO.getStore();
            
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
                        
            //add the dataset to the datamodel
            dataModel.add(data);                            
        }       
        
        return dataModel;           
    }
    
    private DataModel getComponentMatches(String match, HashMap params) throws RPCException{
        InventoryItemRemote remote = (InventoryItemRemote)EJBFactory.lookup("openelis/InventoryItemBean/remote");
        DataModel dataModel = new DataModel();
        List autoCompleteList;
        
        Integer store = null;
        String currentName = null;
        
        if(params != null){
            if(((DropDownField)params.get("store")).getValue() == null || (Integer)((DropDownField)params.get("store")).getValue() == 0){
                //we dont want to do anything...throw error
                throw new FormErrorException(openElisConstants.getString("inventoryComponentAutoException"));
            }
            
            if("".equals((String)((StringField)params.get("name")).getValue())){
                //we dont want to do anything...throw error
                throw new FormErrorException(openElisConstants.getString("inventoryComponentAutoException"));
            }
            
            store = (Integer)((DropDownField)params.get("store")).getValue();
            currentName = (String)((StringField)params.get("name")).getValue();
        }
    
        //lookup by name
        autoCompleteList = remote.inventoryComponentAutoCompleteLookupByName(match+"%", store, currentName, 10);
        
        for(int i=0; i < autoCompleteList.size(); i++){
            IdNameDO resultDO = (IdNameDO) autoCompleteList.get(i);
            //org id
            Integer itemId = resultDO.getId();
            //org name
            String name = resultDO.getName();
          
            //hidden id
            dataModel.add(new NumberObject(itemId), new StringObject(name));

        }       
        
        return dataModel;       
    }
    
    public DataModel getInitialModel(String cat){
        Integer id = null;
        CategoryRemote remote = (CategoryRemote)EJBFactory.lookup("openelis/CategoryBean/remote");

        if(cat.equals("itemCategories"))
            id = remote.getCategoryId("inventory_item_categories");
        else if(cat.equals("itemStores"))
            id = remote.getCategoryId("inventory_item_stores");
        else if(cat.equals("itemUnits"))
            id = remote.getCategoryId("inventory_item_units");
        
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

    private void setFieldsInRPC(Form form, InventoryItemDO inventoryItemDO){
        form.setFieldValue(InvItemMeta.getAverageCost(), inventoryItemDO.getAveCost());
        form.setFieldValue(InvItemMeta.getAverageDailyUse(), inventoryItemDO.getAveDailyUse());
        form.setFieldValue(InvItemMeta.getAverageLeadTime(), inventoryItemDO.getAveLeadTime());
        form.setFieldValue(InvItemMeta.getCategoryId(), new DataSet(new NumberObject(inventoryItemDO.getCategory())));
        form.setFieldValue(InvItemMeta.getDescription(), inventoryItemDO.getDescription());
        form.setFieldValue(InvItemMeta.getDispensedUnitsId(), new DataSet(new NumberObject(inventoryItemDO.getDispensedUnits())));
        form.setFieldValue(InvItemMeta.getId(), inventoryItemDO.getId());
        form.setFieldValue(InvItemMeta.getIsActive(), inventoryItemDO.getIsActive());
        form.setFieldValue(InvItemMeta.getIsBulk(), inventoryItemDO.getIsBulk());
        form.setFieldValue(InvItemMeta.getIsLabor(), inventoryItemDO.getIsLabor());
        form.setFieldValue(InvItemMeta.getIsLotMaintained(), inventoryItemDO.getIsLotMaintained());
        form.setFieldValue(InvItemMeta.getIsNoInventory(), inventoryItemDO.getIsNoInventory());
        form.setFieldValue(InvItemMeta.getIsNotForSale(), inventoryItemDO.getIsNotForSale());
        form.setFieldValue(InvItemMeta.getIsReorderAuto(), inventoryItemDO.getIsReorderAuto());
        form.setFieldValue(InvItemMeta.getIsSerialMaintained(), inventoryItemDO.getIsSerialMaintained());
        form.setFieldValue(InvItemMeta.getIsSubAssembly(), inventoryItemDO.getIsSubAssembly());
        form.setFieldValue(InvItemMeta.getName(), inventoryItemDO.getName());
        form.setFieldValue(InvItemMeta.getProductUri(), inventoryItemDO.getProductUri());
        form.setFieldValue(InvItemMeta.getQuantityMaxLevel(), inventoryItemDO.getQuantityMaxLevel());
        form.setFieldValue(InvItemMeta.getQuantityMinLevel(), inventoryItemDO.getQuantityMinLevel());
        form.setFieldValue(InvItemMeta.getQuantityToReorder(), inventoryItemDO.getQuantityToReorder());
        form.setFieldValue(InvItemMeta.getStoreId(), new DataSet(new NumberObject(inventoryItemDO.getStore())));
        form.setFieldValue(InvItemMeta.getParentRatio(), inventoryItemDO.getParentRatio());    
        
        //we need to create a dataset for the parent inventory item auto complete
        if(inventoryItemDO.getParentInventoryItemId() == null)
            form.setFieldValue(InvItemMeta.PARENT_INVENTORY_ITEM.getName(), null);
        else{
            DataModel parentItemModel = new DataModel();
            parentItemModel.add(new NumberObject(inventoryItemDO.getParentInventoryItemId()),new StringObject(inventoryItemDO.getParentInventoryItem()));
            ((DropDownField)form.getField(InvItemMeta.PARENT_INVENTORY_ITEM.getName())).setModel(parentItemModel);
            form.setFieldValue(InvItemMeta.PARENT_INVENTORY_ITEM.getName(), parentItemModel.get(0));
        }
    }
    
    private InventoryItemDO getInventoryItemDOFromRPC(Form form){
        InventoryItemDO inventoryItemDO = new InventoryItemDO();
        
        if(form.getFieldValue(InvItemMeta.getAverageCost()) != null)
            inventoryItemDO.setAveCost((Double)form.getFieldValue(InvItemMeta.getAverageCost()));
        
        inventoryItemDO.setAveDailyUse((Integer)form.getFieldValue(InvItemMeta.getAverageDailyUse()));
        inventoryItemDO.setAveLeadTime((Integer)form.getFieldValue(InvItemMeta.getAverageLeadTime()));
        inventoryItemDO.setCategory((Integer)((DropDownField)form.getField(InvItemMeta.getCategoryId())).getSelectedKey());
        inventoryItemDO.setDescription((String)form.getFieldValue(InvItemMeta.getDescription()));
        inventoryItemDO.setDispensedUnits((Integer)((DropDownField)form.getField(InvItemMeta.getDispensedUnitsId())).getSelectedKey());
        inventoryItemDO.setId((Integer)form.getFieldValue(InvItemMeta.getId()));
        inventoryItemDO.setIsActive((String)form.getFieldValue(InvItemMeta.getIsActive()));
        inventoryItemDO.setIsBulk((String)form.getFieldValue(InvItemMeta.getIsBulk()));
        inventoryItemDO.setIsLabor((String)form.getFieldValue(InvItemMeta.getIsLabor()));
        inventoryItemDO.setIsLotMaintained((String)form.getFieldValue(InvItemMeta.getIsLotMaintained()));
        inventoryItemDO.setIsNoInventory((String)form.getFieldValue(InvItemMeta.getIsNoInventory()));
        inventoryItemDO.setIsNotForSale((String)form.getFieldValue(InvItemMeta.getIsNotForSale()));
        inventoryItemDO.setIsReorderAuto((String)form.getFieldValue(InvItemMeta.getIsReorderAuto()));
        inventoryItemDO.setIsSerialMaintained((String)form.getFieldValue(InvItemMeta.getIsSerialMaintained()));
        inventoryItemDO.setIsSubAssembly((String)form.getFieldValue(InvItemMeta.getIsSubAssembly()));
        inventoryItemDO.setName((String)form.getFieldValue(InvItemMeta.getName()));
        inventoryItemDO.setProductUri((String)form.getFieldValue(InvItemMeta.getProductUri()));
        inventoryItemDO.setQuantityMaxLevel((Integer)form.getFieldValue(InvItemMeta.getQuantityMaxLevel()));
        inventoryItemDO.setQuantityMinLevel((Integer)form.getFieldValue(InvItemMeta.getQuantityMinLevel()));
        inventoryItemDO.setQuantityToReorder((Integer)form.getFieldValue(InvItemMeta.getQuantityToReorder()));
        inventoryItemDO.setStore((Integer)((DropDownField)form.getField(InvItemMeta.getStoreId())).getSelectedKey());
        inventoryItemDO.setParentInventoryItemId((Integer)((DropDownField)form.getField(InvItemMeta.PARENT_INVENTORY_ITEM.getName())).getSelectedKey());
        inventoryItemDO.setParentInventoryItem((String)((DropDownField)form.getField(InvItemMeta.PARENT_INVENTORY_ITEM.getName())).getTextValue());
        inventoryItemDO.setParentRatio((Integer)form.getFieldValue(InvItemMeta.getParentRatio()));
        
        return inventoryItemDO;
    }
    
    private List getComponentsListFromRPC(DataModel<DataSet> componentsTable, Integer itemId){
        List components = new ArrayList();
        List deletedRows = componentsTable.getDeletions();

        
        for(int i=0; i<componentsTable.size(); i++){
            InventoryComponentDO componentDO = new InventoryComponentDO();
            DataSet<Data> row = componentsTable.get(i);
            //hidden data
            NumberObject id = (NumberObject)row.getKey();

            if(id != null)
                componentDO.setId((Integer)id.getValue());

            componentDO.setComponentNameId((Integer)((DropDownField)row.get(0)).getSelectedKey());
            componentDO.setComponentName((String)((DropDownField)row.get(0)).getTextValue());
            componentDO.setComponentDesc((String)((StringField)row.get(1)).getValue());
            componentDO.setQuantity((Double)((NumberField)row.get(2)).getValue());
            componentDO.setParentInventoryItemId(itemId);
            
            components.add(componentDO);    
        }
        
        for(int j=0; j<deletedRows.size(); j++){
            DataSet deletedRow = (DataSet)deletedRows.get(j);
            if(deletedRow.getKey() != null){
                InventoryComponentDO componentDO = new InventoryComponentDO();
                componentDO.setDelete(true);
                componentDO.setId((Integer)((NumberObject)deletedRow.getKey()).getValue());
                                
                components.add(componentDO);
            }
        }
        
        return components;
    }
    
    public StringObject getComponentDescriptionText(NumberObject componentId){
        InventoryItemRemote remote = (InventoryItemRemote)EJBFactory.lookup("openelis/InventoryItemBean/remote");
        StringObject descText = new StringObject();
        
        descText.setValue(remote.getInventoryDescription((Integer)componentId.getValue()));
        
        return descText;
    }
    
    private void setRpcErrors(List exceptionList, TableField componentsTable, Form form){
        //we need to get the keys and look them up in the resource bundle for internationalization
        for (int i=0; i<exceptionList.size();i++) {
            //if the error is inside the table
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
