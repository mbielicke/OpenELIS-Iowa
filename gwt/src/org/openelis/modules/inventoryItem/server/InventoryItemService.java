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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

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
import org.openelis.gwt.common.RPCException;
import org.openelis.gwt.common.TableFieldErrorException;
import org.openelis.gwt.common.data.AbstractField;
import org.openelis.gwt.common.data.DataMap;
import org.openelis.gwt.common.data.DataModel;
import org.openelis.gwt.common.data.DataSet;
import org.openelis.gwt.common.data.DoubleField;
import org.openelis.gwt.common.data.DropDownField;
import org.openelis.gwt.common.data.Field;
import org.openelis.gwt.common.data.FieldType;
import org.openelis.gwt.common.data.StringField;
import org.openelis.gwt.common.data.StringObject;
import org.openelis.gwt.common.data.TableField;
import org.openelis.gwt.server.ServiceUtils;
import org.openelis.gwt.services.AppScreenFormServiceInt;
import org.openelis.gwt.services.AutoCompleteServiceInt;
import org.openelis.gwt.widget.CheckBox;
import org.openelis.metamap.InventoryItemMetaMap;
import org.openelis.modules.inventoryItem.client.InventoryCommentsForm;
import org.openelis.modules.inventoryItem.client.InventoryCommentsRPC;
import org.openelis.modules.inventoryItem.client.InventoryComponentAutoRPC;
import org.openelis.modules.inventoryItem.client.InventoryComponentsForm;
import org.openelis.modules.inventoryItem.client.InventoryComponentsRPC;
import org.openelis.modules.inventoryItem.client.InventoryItemForm;
import org.openelis.modules.inventoryItem.client.InventoryItemRPC;
import org.openelis.modules.inventoryItem.client.InventoryLocationsForm;
import org.openelis.modules.inventoryItem.client.InventoryLocationsRPC;
import org.openelis.persistence.EJBFactory;
import org.openelis.remote.InventoryItemRemote;
import org.openelis.security.domain.SystemUserDO;
import org.openelis.security.remote.SystemUserRemote;
import org.openelis.server.constants.Constants;
import org.openelis.server.handlers.InventoryItemCategoriesCacheHandler;
import org.openelis.server.handlers.InventoryItemDispensedUnitsCacheHandler;
import org.openelis.server.handlers.InventoryItemStoresCacheHandler;
import org.openelis.util.Datetime;
import org.openelis.util.SessionManager;
import org.openelis.util.UTFResource;
import org.openelis.util.XMLUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class InventoryItemService implements AppScreenFormServiceInt<InventoryItemRPC,Integer>, 
									     AutoCompleteServiceInt {
    
    private static final int leftTableRowsPerPage = 22;
    
    private static final InventoryItemMetaMap InvItemMeta = new InventoryItemMetaMap();
    
    private UTFResource openElisConstants= UTFResource.getBundle((String)SessionManager.getSession().getAttribute("locale"));

	public DataModel<Integer> commitQuery(Form form, DataModel<Integer> model) throws RPCException {
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
            model = new DataModel<Integer>();
        else
            model.clear();
        while(i < inventoryItemNames.size() && i < leftTableRowsPerPage) {
            IdNameStoreDO resultDO = (IdNameStoreDO)inventoryItemNames.get(i);

            model.add(new DataSet<Integer>(resultDO.getId(),new FieldType[] {new StringObject(resultDO.getName()), new StringObject(resultDO.getStore())}));
            i++;
        } 
 
        return model;
    }

    public InventoryItemRPC commitAdd(InventoryItemRPC rpc) throws RPCException {
        //remote interface to call the inventory bean
        InventoryItemRemote remote = (InventoryItemRemote)EJBFactory.lookup("openelis/InventoryItemBean/remote");
        InventoryItemDO inventoryItemDO = new InventoryItemDO();
        List components = new ArrayList();
       
        NoteDO inventoryItemNote = new NoteDO();

        inventoryItemDO = getInventoryItemDOFromRPC(rpc.form);
        
        //components info
        TableField<Integer> componentsField = rpc.form.components.componentsTable;
        DataModel<Integer> componentsTable = (DataModel<Integer>)componentsField.getValue();
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
        String tab = rpc.form.itemTabPanel;
        if(tab.equals("commentsTab")){

            loadCommentsForm(rpc.key, rpc.form.comments);
        }
        
        //we need to clear out the note subject and the note text fields after a commit
        rpc.form.comments.subject.setValue(null);
        rpc.form.comments.text.setValue(null);  
        
        //we need to set the component rpc value to the orig component value since it is an add
        rpc.form.components.componentsTable.setValue(componentsTable);
        
        return rpc;
    }

    public InventoryItemRPC commitUpdate(InventoryItemRPC rpc) throws RPCException {
        //remote interface to call the inventory bean
        InventoryItemRemote remote = (InventoryItemRemote)EJBFactory.lookup("openelis/InventoryItemBean/remote");
        InventoryItemDO inventoryItemDO = new InventoryItemDO();
        List components = new ArrayList();
        //List locations = new ArrayList();
        NoteDO inventoryItemNote = new NoteDO();

        //build the organizationAddress DO from the form
        inventoryItemDO = getInventoryItemDOFromRPC(rpc.form);
        
        //components info
        TableField<Integer> componentsField = rpc.form.components.componentsTable;
        DataModel<Integer> componentsTable = (DataModel<Integer>)componentsField.getValue();
        components = getComponentsListFromRPC(componentsTable, inventoryItemDO.getId());   
        
        //build the noteDo from the form
        inventoryItemNote.setSubject(rpc.form.comments.subject.getValue());
        inventoryItemNote.setText(rpc.form.comments.text.getValue());
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
        String tab = rpc.form.itemTabPanel;
        if(tab.equals("commentsTab")){     
            loadCommentsForm(rpc.key, rpc.form.comments);
        }
        
        //we need to set the notes load param to true because update doesnt call resetRPC
        rpc.form.comments.load = false;
        
        //we need to clear out the note subject and the note text fields after a commit
        rpc.form.comments.subject.setValue(null);
        rpc.form.comments.text.setValue(null);
        
        return rpc;
    }

    public InventoryItemRPC commitDelete(InventoryItemRPC rpc) throws RPCException {
    	// TODO Auto-generated method stub
    	return null;
    }

    public InventoryItemRPC abort(InventoryItemRPC rpc) throws RPCException {
        //remote interface to call the inventory bean
        InventoryItemRemote remote = (InventoryItemRemote)EJBFactory.lookup("openelis/InventoryItemBean/remote");
        
        
        InventoryItemDO inventoryItemDO = remote.getInventoryItemAndUnlock(rpc.key, SessionManager.getSession().getId());

        //set the fields in the RPC
        setFieldsInRPC(rpc.form, inventoryItemDO);
        
        String tab = rpc.form.itemTabPanel;
        if(tab.equals("componentsTab")){
            loadComponentsForm(rpc.key, false, rpc.form.components);
        }
       
        if(tab.equals("locationsTab")){
            loadLocationsForm(rpc.key, rpc.form.isSerialMaintained.getValue(), rpc.form.locations);
        }
        
        if(tab.equals("commentsTab")){
            loadCommentsForm(rpc.key, rpc.form.comments);
        }
        
        return rpc;  
    }

    public InventoryItemRPC fetch(InventoryItemRPC rpc) throws RPCException {
        /*
         * Call checkModels to make screen has most recent versions of dropdowns
         */
        checkModels(rpc);
        
        //remote interface to call the inventory bean
        InventoryItemRemote remote = (InventoryItemRemote)EJBFactory.lookup("openelis/InventoryItemBean/remote");
        
        InventoryItemDO inventoryItemDO = remote.getInventoryItem(rpc.key);

        //set the fields in the RPC
        setFieldsInRPC(rpc.form, inventoryItemDO);
        
        String tab = rpc.form.itemTabPanel;
        if(tab.equals("componentsTab")){
            loadComponentsForm(rpc.key, false, rpc.form.components);
        }
       
        if(tab.equals("locationsTab")){
            loadLocationsForm(rpc.key, rpc.form.isSerialMaintained.getValue(), rpc.form.locations);
        }
        
        if(tab.equals("commentsTab")){
            loadCommentsForm(rpc.key, rpc.form.comments);
        }
        
      return rpc;
    }

    public InventoryItemRPC fetchForUpdate(InventoryItemRPC rpc) throws RPCException {
        /*
         * Call checkModels to make screen has most recent versions of dropdowns
         */
        checkModels(rpc);
        
        //remote interface to call the inventory bean
        InventoryItemRemote remote = (InventoryItemRemote)EJBFactory.lookup("openelis/InventoryItemBean/remote");
        
        InventoryItemDO inventoryItemDO = new InventoryItemDO();
        try{
            inventoryItemDO = remote.getInventoryItemAndLock(rpc.key, SessionManager.getSession().getId());
        }catch(Exception e){
            throw new RPCException(e.getMessage());
        }
        
        //set the fields in the RPC
        setFieldsInRPC(rpc.form, inventoryItemDO);
        
        String tab = rpc.form.itemTabPanel;
        if(tab.equals("componentsTab")){
            loadComponentsForm(rpc.key, false, rpc.form.components);
        }
       
        if(tab.equals("locationsTab")){
            loadLocationsForm(rpc.key, rpc.form.isSerialMaintained.getValue(), rpc.form.locations);
        }
        
        if(tab.equals("commentsTab")){
            loadCommentsForm(rpc.key, rpc.form.comments);
        }
        
        return rpc;  
    }
    
    public InventoryComponentsRPC loadComponents(InventoryComponentsRPC rpc) throws RPCException {
        loadComponentsForm(rpc.key, rpc.forDuplicate, rpc.form);
        return rpc;
    }
    
    public void loadComponentsForm(Integer key, boolean forDuplicate, InventoryComponentsForm form) throws RPCException {
        getComponentsModel(key, forDuplicate, form.componentsTable);
        form.load = true;
    }
    
    public InventoryLocationsRPC loadLocations(InventoryLocationsRPC rpc) throws RPCException {
        loadLocationsForm(rpc.key, rpc.isSerialized, rpc.form);
        return rpc;
    }
    
    public void loadLocationsForm(Integer key, String isSerialized, InventoryLocationsForm form) throws RPCException {
        getLocationsModel(key, isSerialized, form.locQuantitiesTable);
        form.load = true;
    }
    
    public InventoryCommentsRPC loadComments(InventoryCommentsRPC rpc) throws RPCException {
        loadCommentsForm(rpc.key, rpc.form);
        return rpc;
    }
    
    public void loadCommentsForm(Integer key, InventoryCommentsForm form) throws RPCException {
        form.notesPanel.setValue(getCommentsModel(key));
        form.load = true;
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
    
    public InventoryItemRPC getScreen(InventoryItemRPC rpc) throws RPCException {
        rpc.xml = ServiceUtils.getXML(Constants.APP_ROOT+"/Forms/inventoryItem.xsl");
        /*
         * Load initial  models to RPC and store cache verison of models into Session for 
         * comparisons for later fetches
         */
        rpc.categories = InventoryItemCategoriesCacheHandler.getCategories();
        SessionManager.getSession().setAttribute("invItemCategoriesVersion",InventoryItemCategoriesCacheHandler.version);
        rpc.dispensedUnits = InventoryItemDispensedUnitsCacheHandler.getDispensedUnits();
        SessionManager.getSession().setAttribute("invItemDispensedUnitsVersion",InventoryItemDispensedUnitsCacheHandler.version);
        rpc.stores = InventoryItemStoresCacheHandler.getStores();
        SessionManager.getSession().setAttribute("invItemStoresVersion",InventoryItemStoresCacheHandler.version);
        return rpc;
    }
    
    public void checkModels(InventoryItemRPC rpc) {
        /*
         * Retrieve current version of models from session.
         */
        int categories = (Integer)SessionManager.getSession().getAttribute("invItemCategoriesVersion");
        int dispensedUnits = (Integer)SessionManager.getSession().getAttribute("invItemDispensedUnitsVersion");
        int stores = (Integer)SessionManager.getSession().getAttribute("invItemStoresVersion");
        /*
         * Compare stored version to current cache versions and update if necessary. 
         */
        if(categories != InventoryItemCategoriesCacheHandler.version){
            rpc.categories = InventoryItemCategoriesCacheHandler.getCategories();
            SessionManager.getSession().setAttribute("invItemCategoriesVersion", InventoryItemCategoriesCacheHandler.version);
        }
        if(dispensedUnits != InventoryItemDispensedUnitsCacheHandler.version){
            rpc.dispensedUnits = InventoryItemDispensedUnitsCacheHandler.getDispensedUnits();
            SessionManager.getSession().setAttribute("invItemDispensedUnitsVersion", InventoryItemDispensedUnitsCacheHandler.version);
        }
        if(stores != InventoryItemStoresCacheHandler.version){
            rpc.stores = InventoryItemStoresCacheHandler.getStores();
            SessionManager.getSession().setAttribute("invItemStoresVersion", InventoryItemStoresCacheHandler.version);
        }
    }
    
    public TableField getComponentsModel(Integer itemId, boolean forDuplicate, TableField<Integer> model){
        InventoryItemRemote remote = (InventoryItemRemote)EJBFactory.lookup("openelis/InventoryItemBean/remote");
        List componentsList = remote.getInventoryComponents(itemId);
        model.setValue(fillComponentsTable((DataModel)model.getValue(), forDuplicate, componentsList));
        return model;
    }
    
    public TableField getLocationsModel(Integer itemId, String isSerialized, TableField<Integer> model){
        InventoryItemRemote remote = (InventoryItemRemote)EJBFactory.lookup("openelis/InventoryItemBean/remote");
        List locationsList = remote.getInventoryLocations(itemId);
        model.setValue(fillLocationsTable((DataModel)model.getValue(), isSerialized, locationsList));
        return model;
    }
    
    public String getCommentsModel(Integer key){
        //remote interface to call the organization bean
        InventoryItemRemote remote = (InventoryItemRemote)EJBFactory.lookup("openelis/InventoryItemBean/remote");
    
        //gets the whole notes list now
        List notesList = remote.getInventoryNotes(key);
        
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
    
        return XMLUtil.toString(doc);
        
        }catch(Exception e){
            System.out.println(e.getMessage());
        }
        return null;
    }

    public DataModel fillComponentsTable(DataModel<Integer> componentsModel, boolean forDuplicate, List componentsList){
        try {
            componentsModel.clear();
            
            for(int iter = 0;iter < componentsList.size();iter++) {
                InventoryComponentDO componentRow = (InventoryComponentDO)componentsList.get(iter);
    
                   DataSet<Integer> row = componentsModel.createNewSet();
                   
                   if(componentRow.getId() != null && !forDuplicate){
                       row.setKey(componentRow.getId());
                   }

//                  we need to create a dataset for the component auto complete
                    if(componentRow.getComponentNameId() == null)
                        ((DropDownField<Integer>)row.get(0)).clear();
                    else{
                        DataModel<Integer> componentModel = new DataModel<Integer>();
                        componentModel.add(new DataSet<Integer>(componentRow.getComponentNameId(),new StringObject(componentRow.getComponentName())));
                        ((DropDownField<Integer>)(Field)row.get(0)).setModel(componentModel);
                        ((DropDownField<Integer>)(Field)row.get(0)).setValue(componentModel.get(0));
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
    
    public DataModel fillLocationsTable(DataModel<Integer> locationsModel, String isSerialized, List locationsList){
        try {
            locationsModel.clear();
            
            for(int iter = 0;iter < locationsList.size();iter++) {
                InventoryLocationDO locationRow = (InventoryLocationDO)locationsList.get(iter);
    
                   DataSet<Integer> row = locationsModel.createNewSet();
           
                    row.setKey(locationRow.getId());
                    
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
    
    public InventoryComponentAutoRPC getMatchesCall(InventoryComponentAutoRPC rpc) throws RPCException {
        if("component".equals(rpc.cat))
            rpc.autoMatches = getComponentMatches(rpc.match, rpc.storeId, rpc.name);
        return rpc;
        
    }
    
    public DataModel getMatches(String cat, DataModel model, String match, HashMap<String, FieldType> params) throws RPCException {
        //if(cat.equals("component"))
          //  return getComponentMatches(match, params);
        //else if(cat.equals("queryComponent"))
        //    return getComponentMatches(match, null);
        if(cat.equals("parentItem"))
            return getParentItemMatches(match);
        
        return null;    
    }
    
    private DataModel getParentItemMatches(String match) throws RPCException{
        InventoryItemRemote remote = (InventoryItemRemote)EJBFactory.lookup("openelis/InventoryItemBean/remote");
        DataModel<Integer> dataModel = new DataModel<Integer>();
        List autoCompleteList;

        String parsedMatch = match.replace('*', '%');
        
        autoCompleteList = remote.inventoryItemStoreAutoCompleteLookupByName(parsedMatch+"%", 10, false, true);
        
        for(int i=0; i < autoCompleteList.size(); i++){
            InventoryItemAutoDO resultDO = (InventoryItemAutoDO) autoCompleteList.get(i);
            
            Integer itemId = resultDO.getId();
            String name = resultDO.getName();
            String store = resultDO.getStore();
            
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
                        
            //add the dataset to the datamodel
            dataModel.add(data);                            
        }       
        
        return dataModel;           
    }
    
    private DataModel getComponentMatches(String match, Integer store, String currentName) throws RPCException{
        InventoryItemRemote remote = (InventoryItemRemote)EJBFactory.lookup("openelis/InventoryItemBean/remote");
        DataModel<Integer> dataModel = new DataModel<Integer>();
        List autoCompleteList;

        if(store == null || store == 0){
            //we dont want to do anything...throw error
            throw new FormErrorException(openElisConstants.getString("inventoryComponentAutoException"));
        }
        
        if("".equals(currentName)){
            //we dont want to do anything...throw error
            throw new FormErrorException(openElisConstants.getString("inventoryComponentAutoException"));
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
            dataModel.add(new DataSet<Integer>(itemId, new StringObject(name)));

        }       
        
        return dataModel;       
    }
    
    /*public DataModel getInitialModel(String cat){
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
        DataModel<Integer> returnModel = new DataModel<Integer>();
        
        if(entries.size() > 0){ 
            returnModel.add(new DataSet<Integer>(0,new StringObject("")));
        }
        
        for(IdNameDO resultDO : entries) { 
            returnModel.add(new DataSet<Integer>(resultDO.getId(),new StringObject(resultDO.getName())));
        }       
        
        return returnModel;
    }
    */

    private void setFieldsInRPC(InventoryItemForm form, InventoryItemDO inventoryItemDO){
        form.averageCost.setValue(inventoryItemDO.getAveCost());
        form.averageDailyUse.setValue(inventoryItemDO.getAveDailyUse());
        form.averageLeadTime.setValue(inventoryItemDO.getAveLeadTime());
        form.categoryId.setValue(new DataSet<Integer>(inventoryItemDO.getCategory()));
        form.description.setValue(inventoryItemDO.getDescription());
        form.dispensedUnitsId.setValue(new DataSet<Integer>(inventoryItemDO.getDispensedUnits()));
        form.id.setValue(inventoryItemDO.getId());
        form.isActive.setValue(inventoryItemDO.getIsActive());
        form.isBulk.setValue(inventoryItemDO.getIsBulk());
        form.isLabor.setValue(inventoryItemDO.getIsLabor());
        form.isLotMaintained.setValue(inventoryItemDO.getIsLotMaintained());
        form.isNoInventory.setValue(inventoryItemDO.getIsNoInventory());
        form.isNotForSale.setValue(inventoryItemDO.getIsNotForSale());
        form.isReorderAuto.setValue(inventoryItemDO.getIsReorderAuto());
        form.isSerialMaintained.setValue(inventoryItemDO.getIsSerialMaintained());
        form.isSubAssembly.setValue(inventoryItemDO.getIsSubAssembly());
        form.name.setValue(inventoryItemDO.getName());
        form.productUri.setValue(inventoryItemDO.getProductUri());
        form.quantityMaxLevel.setValue(inventoryItemDO.getQuantityMaxLevel());
        form.quantityMinLevel.setValue(inventoryItemDO.getQuantityMinLevel());
        form.quantityToReorder.setValue(inventoryItemDO.getQuantityToReorder());
        form.storeId.setValue(new DataSet<Integer>(inventoryItemDO.getStore()));
        form.parentRatio.setValue(inventoryItemDO.getParentRatio());    
        
        //we need to create a dataset for the parent inventory item auto complete
        if(inventoryItemDO.getParentInventoryItemId() == null)
            form.parentInventoryItem.clear();
        else{
            DataModel<Integer> parentItemModel = new DataModel<Integer>();
            parentItemModel.add(new DataSet<Integer>(inventoryItemDO.getParentInventoryItemId(),new StringObject(inventoryItemDO.getParentInventoryItem())));
            form.parentInventoryItem.setModel(parentItemModel);
            form.parentInventoryItem.setValue(parentItemModel.get(0));
        }
    }
    
    private InventoryItemDO getInventoryItemDOFromRPC(InventoryItemForm form){
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
    
    private List getComponentsListFromRPC(DataModel<Integer> componentsTable, Integer itemId){
        List components = new ArrayList();
        List<DataSet<Integer>> deletedRows = componentsTable.getDeletions();

        for(int i=0; i<componentsTable.size(); i++){
            InventoryComponentDO componentDO = new InventoryComponentDO();
            DataSet<Integer> row = componentsTable.get(i);
            //hidden data
            Integer id = row.getKey();

            if(id != null)
                componentDO.setId(id);

            componentDO.setComponentNameId((Integer)((DropDownField)row.get(0)).getSelectedKey());
            componentDO.setComponentName((String)((DropDownField)row.get(0)).getTextValue());
            componentDO.setComponentDesc((String)((StringField)row.get(1)).getValue());
            componentDO.setQuantity((Double)((DoubleField)row.get(2)).getValue());
            componentDO.setParentInventoryItemId(itemId);
            
            components.add(componentDO);    
        }
        
        for(int j=0; j<deletedRows.size(); j++){
            DataSet<Integer> deletedRow = deletedRows.get(j);
            if(deletedRow.getKey() != null){
                InventoryComponentDO componentDO = new InventoryComponentDO();
                componentDO.setDelete(true);
                componentDO.setId(deletedRow.getKey());
                                
                components.add(componentDO);
            }
        }
        
        return components;
    }
    
    public InventoryItemRPC getComponentDescriptionText(InventoryItemRPC rpc){
        InventoryItemRemote remote = (InventoryItemRemote)EJBFactory.lookup("openelis/InventoryItemBean/remote");
        
        rpc.descText = remote.getInventoryDescription(rpc.componentId);
        
        return rpc;
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
