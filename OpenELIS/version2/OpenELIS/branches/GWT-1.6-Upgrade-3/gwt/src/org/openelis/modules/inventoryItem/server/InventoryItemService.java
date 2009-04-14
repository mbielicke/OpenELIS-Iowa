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
import org.openelis.gwt.common.FieldErrorException;
import org.openelis.gwt.common.Form;
import org.openelis.gwt.common.FormErrorException;
import org.openelis.gwt.common.LastPageException;
import org.openelis.gwt.common.Query;
import org.openelis.gwt.common.RPCException;
import org.openelis.gwt.common.TableFieldErrorException;
import org.openelis.gwt.common.ValidationErrorsList;
import org.openelis.gwt.common.data.AbstractField;
import org.openelis.gwt.common.data.DoubleField;
import org.openelis.gwt.common.data.DropDownField;
import org.openelis.gwt.common.data.Field;
import org.openelis.gwt.common.data.FieldType;
import org.openelis.gwt.common.data.StringField;
import org.openelis.gwt.common.data.StringObject;
import org.openelis.gwt.common.data.TableDataModel;
import org.openelis.gwt.common.data.TableDataRow;
import org.openelis.gwt.common.data.TableField;
import org.openelis.gwt.server.ServiceUtils;
import org.openelis.gwt.services.AppScreenFormServiceInt;
import org.openelis.gwt.services.AutoCompleteServiceInt;
import org.openelis.gwt.widget.CheckBox;
import org.openelis.metamap.InventoryItemMetaMap;
import org.openelis.modules.inventoryItem.client.InventoryCommentsForm;
import org.openelis.modules.inventoryItem.client.InventoryComponentAutoRPC;
import org.openelis.modules.inventoryItem.client.InventoryComponentsForm;
import org.openelis.modules.inventoryItem.client.InventoryItemForm;
import org.openelis.modules.inventoryItem.client.InventoryLocationsForm;
import org.openelis.modules.inventoryItem.client.InventoryManufacturingForm;
import org.openelis.persistence.EJBFactory;
import org.openelis.remote.InventoryItemRemote;
import org.openelis.security.domain.SystemUserDO;
import org.openelis.security.remote.SystemUserRemote;
import org.openelis.server.constants.Constants;
import org.openelis.server.handlers.InventoryItemCategoriesCacheHandler;
import org.openelis.server.handlers.InventoryItemDispensedUnitsCacheHandler;
import org.openelis.server.handlers.InventoryItemStoresCacheHandler;
import org.openelis.util.Datetime;
import org.openelis.util.FormUtil;
import org.openelis.util.SessionManager;
import org.openelis.util.UTFResource;
import org.openelis.util.XMLUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class InventoryItemService implements AppScreenFormServiceInt<InventoryItemForm,Query<TableDataRow<Integer>>>, 
									     AutoCompleteServiceInt {
    
    private static final int leftTableRowsPerPage = 22;
    
    private static final InventoryItemMetaMap InvItemMeta = new InventoryItemMetaMap();
    
    private UTFResource openElisConstants= UTFResource.getBundle((String)SessionManager.getSession().getAttribute("locale"));

	public Query<TableDataRow<Integer>> commitQuery(Query<TableDataRow<Integer>> query) throws RPCException {
        List inventoryItemNames;
        //if the rpc is null then we need to get the page
        /*
        if(qList == null){

            qList = (ArrayList<AbstractField>)SessionManager.getSession().getAttribute("InventoryItemQuery");
    
            if(qList == null)
                throw new QueryException(openElisConstants.getString("queryExpiredException"));

            InventoryItemRemote remote = (InventoryItemRemote)EJBFactory.lookup("openelis/InventoryItemBean/remote");
            try{
                inventoryItemNames = remote.query(qList, (model.getPage()*leftTableRowsPerPage), leftTableRowsPerPage+1);
            }catch(Exception e){
                if(e instanceof LastPageException){
                    throw new LastPageException(openElisConstants.getString("lastPageException"));
                }else{
                    throw new RPCException(e.getMessage()); 
                }           
            }    
        }else{*/
            InventoryItemRemote remote = (InventoryItemRemote)EJBFactory.lookup("openelis/InventoryItemBean/remote");
            /*
            HashMap<String,AbstractField> fields = form.getFieldMap();
            fields.remove("componentsTable");
            fields.remove("locQuantitiesTable");
            */
            try{    
                inventoryItemNames = remote.query(query.fields,query.page*leftTableRowsPerPage,leftTableRowsPerPage);
            }catch(LastPageException e) {
                throw new LastPageException(openElisConstants.getString("lastPageException"));
            }catch(Exception e){
                throw new RPCException(e.getMessage());
            }
    
        
            //need to save the rpc used to the encache
            //SessionManager.getSession().setAttribute("InventoryItemQuery", qList);
        //}
        
        //fill the model with the query results
        int i=0;
        if(query.results == null)
            query.results = new TableDataModel<TableDataRow<Integer>>();
        else
            query.results.clear();
        while(i < inventoryItemNames.size() && i < leftTableRowsPerPage) {
            IdNameStoreDO resultDO = (IdNameStoreDO)inventoryItemNames.get(i);

            query.results.add(new TableDataRow<Integer>(resultDO.getId(),new FieldType[] {new StringObject(resultDO.getName()), new StringObject(resultDO.getStore())}));
            i++;
        } 
 
        return query;
    }

    public InventoryItemForm commitAdd(InventoryItemForm rpc) throws RPCException {
        //remote interface to call the inventory bean
        InventoryItemRemote remote = (InventoryItemRemote)EJBFactory.lookup("openelis/InventoryItemBean/remote");
        InventoryItemDO inventoryItemDO = new InventoryItemDO();
        List components = new ArrayList();
       
        NoteDO inventoryItemNote = new NoteDO();
        NoteDO inventoryManuNote = new NoteDO();

        inventoryItemDO = getInventoryItemDOFromRPC(rpc);
        
        //components info
        TableField<TableDataRow<Integer>> componentsField = rpc.components.componentsTable;
        TableDataModel<TableDataRow<Integer>> componentsTable = (TableDataModel<TableDataRow<Integer>>)componentsField.getValue();
        components = getComponentsListFromRPC(componentsTable, inventoryItemDO.getId());
        
        //build the noteDo from the form
        InventoryCommentsForm noteRPC = rpc.comments;
        inventoryItemNote.setSubject(noteRPC.subject.getValue());
        inventoryItemNote.setText(noteRPC.text.getValue());
        inventoryItemNote.setIsExternal("Y");
        
        //build the manufacturing note
        InventoryManufacturingForm manForm = rpc.manufacturing;
        inventoryManuNote.setText(manForm.manufacturingText.getValue());
        inventoryManuNote.setIsExternal("N");
        
        //send the changes to the database
        Integer inventoryItemId;
        try{
            inventoryItemId = (Integer)remote.updateInventory(inventoryItemDO, components, inventoryItemNote, inventoryManuNote);
        }catch(Exception e){
            if(e instanceof ValidationErrorsList){
                setRpcErrors(((ValidationErrorsList)e).getErrorList(), componentsField, rpc);
                return rpc;
            }else
                throw new RPCException(e.getMessage());
        }
        
        inventoryItemDO.setId(inventoryItemId);

        //set the fields in the RPC
        setFieldsInRPC(rpc, inventoryItemDO, false);

        //we need to refresh the comments tab if it is showing
        String tab = rpc.itemTabPanel;
        if(tab.equals("commentsTab")){

            loadCommentsForm(rpc.entityKey, rpc.comments);
        }
        
        //we need to clear out the note subject and the note text fields after a commit
        rpc.comments.subject.setValue(null);
        rpc.comments.text.setValue(null);  
        
        //we need to set the component rpc value to the orig component value since it is an add
        rpc.components.componentsTable.setValue(componentsTable);
        
        return rpc;
    }

    public InventoryItemForm commitUpdate(InventoryItemForm rpc) throws RPCException {
        //remote interface to call the inventory bean
        InventoryItemRemote remote = (InventoryItemRemote)EJBFactory.lookup("openelis/InventoryItemBean/remote");
        InventoryItemDO inventoryItemDO = new InventoryItemDO();
        List components = new ArrayList();
        //List locations = new ArrayList();
        NoteDO inventoryItemNote = new NoteDO();
        NoteDO inventoryManuNote = new NoteDO();
        
        //build the organizationAddress DO from the form
        inventoryItemDO = getInventoryItemDOFromRPC(rpc);
        
        //components info
        TableField<TableDataRow<Integer>> componentsField = rpc.components.componentsTable;
        TableDataModel<TableDataRow<Integer>> componentsTable = componentsField.getValue();
        components = getComponentsListFromRPC(componentsTable, inventoryItemDO.getId());   
        
        //build the noteDo from the form
        inventoryItemNote.setSubject(rpc.comments.subject.getValue());
        inventoryItemNote.setText(rpc.comments.text.getValue());
        inventoryItemNote.setIsExternal("Y");
        
        //build the manufacturing note
        InventoryManufacturingForm manForm = rpc.manufacturing;
        inventoryManuNote.setId(manForm.id);
        inventoryManuNote.setText(manForm.manufacturingText.getValue());
        inventoryManuNote.setIsExternal("N");
        
        //send the changes to the database
        try{
            remote.updateInventory(inventoryItemDO, components, inventoryItemNote, inventoryManuNote);
            
        }catch(Exception e){
            if(e instanceof ValidationErrorsList){
                setRpcErrors(((ValidationErrorsList)e).getErrorList(), componentsField, rpc);
                return rpc;
            }else
                throw new RPCException(e.getMessage());
        }
        
        //set the fields in the RPC
        setFieldsInRPC(rpc, inventoryItemDO, false);   
        
        //we need to refresh the comments tab if it is showing
        String tab = rpc.itemTabPanel;
        if(tab.equals("commentsTab")){     
            loadCommentsForm(rpc.entityKey, rpc.comments);
        }
        
        //we need to set the notes load param to true because update doesnt call resetRPC
        rpc.comments.load = false;
        
        //we need to clear out the note subject and the note text fields after a commit
        rpc.comments.subject.setValue(null);
        rpc.comments.text.setValue(null);
        
        return rpc;
    }

    public InventoryItemForm commitDelete(InventoryItemForm rpc) throws RPCException {
    	return null;
    }

    public InventoryItemForm abort(InventoryItemForm rpc) throws RPCException {
        //remote interface to call the inventory bean
        InventoryItemRemote remote = (InventoryItemRemote)EJBFactory.lookup("openelis/InventoryItemBean/remote");
        
        
        InventoryItemDO inventoryItemDO = remote.getInventoryItemAndUnlock(rpc.entityKey, SessionManager.getSession().getId());

        //set the fields in the RPC
        setFieldsInRPC(rpc, inventoryItemDO, false);
        
        String tab = rpc.itemTabPanel;
        if(tab.equals("componentsTab")){
            loadComponentsForm(rpc.entityKey, false, rpc.components);
        }
       
        if(tab.equals("locationsTab")){
            loadLocationsForm(rpc.entityKey, rpc.isSerialMaintained.getValue(), rpc.locations);
        }
        
        if(tab.equals("commentsTab")){
            loadCommentsForm(rpc.entityKey, rpc.comments);
        }
        
        return rpc;  
    }

    public InventoryItemForm fetch(InventoryItemForm rpc) throws RPCException {
        /*
         * Call checkModels to make screen has most recent versions of dropdowns
         */
        checkModels(rpc);
        
        //remote interface to call the inventory bean
        InventoryItemRemote remote = (InventoryItemRemote)EJBFactory.lookup("openelis/InventoryItemBean/remote");
        
        InventoryItemDO inventoryItemDO = remote.getInventoryItem(rpc.entityKey);

        //set the fields in the RPC
        setFieldsInRPC(rpc, inventoryItemDO, false);
        
        String tab = rpc.itemTabPanel;
        if(tab.equals("componentsTab")){
            loadComponentsForm(rpc.entityKey, false, rpc.components);
        }
       
        if(tab.equals("locationsTab")){
            loadLocationsForm(rpc.entityKey, rpc.isSerialMaintained.getValue(), rpc.locations);
        }
        
        if(tab.equals("manufacturingTab")){
            loadManufacturingForm(rpc.entityKey, rpc.manufacturing);
        }
        
        if(tab.equals("commentsTab")){
            loadCommentsForm(rpc.entityKey, rpc.comments);
        }
        
      return rpc;
    }

    public InventoryItemForm fetchForUpdate(InventoryItemForm rpc) throws RPCException {
        /*
         * Call checkModels to make screen has most recent versions of dropdowns
         */
        checkModels(rpc);
        
        //remote interface to call the inventory bean
        InventoryItemRemote remote = (InventoryItemRemote)EJBFactory.lookup("openelis/InventoryItemBean/remote");
        
        InventoryItemDO inventoryItemDO = new InventoryItemDO();
        try{
            inventoryItemDO = remote.getInventoryItemAndLock(rpc.entityKey, SessionManager.getSession().getId());
        }catch(Exception e){
            throw new RPCException(e.getMessage());
        }
        
        //set the fields in the RPC
        setFieldsInRPC(rpc, inventoryItemDO, false);
        
        String tab = rpc.itemTabPanel;
        if(tab.equals("componentsTab")){
            loadComponentsForm(rpc.entityKey, false, rpc.components);
        }
       
        if(tab.equals("locationsTab")){
            loadLocationsForm(rpc.entityKey, rpc.isSerialMaintained.getValue(), rpc.locations);
        }
        
        if(tab.equals("manufacturingTab")){
            loadManufacturingForm(rpc.entityKey, rpc.manufacturing);
        }
        
        if(tab.equals("commentsTab")){
            loadCommentsForm(rpc.entityKey, rpc.comments);
        }
        
        return rpc;  
    }
    
    public InventoryItemForm getDuplicateRPC(InventoryItemForm rpc) throws RPCException{
        InventoryItemRemote remote = (InventoryItemRemote)EJBFactory.lookup("openelis/InventoryItemBean/remote");        
        
        InventoryItemDO inventoryItemDO = remote.getInventoryItem(rpc.entityKey);                       
        
        //reload the main form
        setFieldsInRPC(rpc, inventoryItemDO, true);
        
        //reload the components
        rpc.components.entityKey = rpc.entityKey;
        rpc.components.forDuplicate = true;
        loadComponents(rpc.components);
        
        //clear locations
        rpc.locations.locQuantitiesTable.setValue(null);
        
        //clear notes
        rpc.comments.subject.setValue(null);
        rpc.comments.text.setValue(null);
        rpc.comments.notesPanel.setValue(null);
        
        //clear the keys
        rpc.comments.entityKey = null;
        rpc.locations.entityKey = null;
        rpc.entityKey = null;
        rpc.components.entityKey = null;
        
        return rpc;
    }
    
    public InventoryComponentsForm loadComponents(InventoryComponentsForm rpc) throws RPCException {
        loadComponentsForm(rpc.entityKey, rpc.forDuplicate, rpc);
        return rpc;
    }
    
    public void loadComponentsForm(Integer key, boolean forDuplicate, InventoryComponentsForm form) throws RPCException {
        getComponentsModel(key, forDuplicate, form.componentsTable);
        form.load = true;
    }
    
    public InventoryLocationsForm loadLocations(InventoryLocationsForm rpc) throws RPCException {
        loadLocationsForm(rpc.entityKey, rpc.isSerialized, rpc);
        return rpc;
    }
    
    public void loadLocationsForm(Integer key, String isSerialized, InventoryLocationsForm form) throws RPCException {
        getLocationsModel(key, isSerialized, form.locQuantitiesTable);
        form.load = true;
    }
    
    public InventoryCommentsForm loadComments(InventoryCommentsForm rpc) throws RPCException {
        loadCommentsForm(rpc.entityKey, rpc);
        return rpc;
    }
    
    public void loadCommentsForm(Integer key, InventoryCommentsForm form) throws RPCException {
        form.notesPanel.setValue(getCommentsModel(key));
        form.load = true;
    }
    
    public InventoryManufacturingForm loadManufacturing(InventoryManufacturingForm rpc) throws RPCException {
        loadManufacturingForm(rpc.entityKey, rpc);
        return rpc;
    }
    
    public void loadManufacturingForm(Integer key, InventoryManufacturingForm form) throws RPCException {
        getManufacturingText(key, form);
        form.load = true;
    }
    
    public InventoryItemForm getScreen(InventoryItemForm rpc) throws RPCException {
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
    
    public void checkModels(InventoryItemForm rpc) {
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
    
    public TableField getComponentsModel(Integer itemId, boolean forDuplicate, TableField<TableDataRow<Integer>> model){
        InventoryItemRemote remote = (InventoryItemRemote)EJBFactory.lookup("openelis/InventoryItemBean/remote");
        List componentsList = remote.getInventoryComponents(itemId);
        model.setValue(fillComponentsTable(model.getValue(), forDuplicate, componentsList));
        return model;
    }
    
    public TableField getLocationsModel(Integer itemId, String isSerialized, TableField<TableDataRow<Integer>> model){
        InventoryItemRemote remote = (InventoryItemRemote)EJBFactory.lookup("openelis/InventoryItemBean/remote");
        List locationsList = remote.getInventoryLocations(itemId);
        model.setValue(fillLocationsTable(model.getValue(), isSerialized, locationsList));
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
    
    public void getManufacturingText(Integer key, InventoryManufacturingForm form){
        //remote interface to call the organization bean
        InventoryItemRemote remote = (InventoryItemRemote)EJBFactory.lookup("openelis/InventoryItemBean/remote");
    
        //gets the whole notes list now
        NoteDO note = remote.getInventoryMaunfacturingRecipe(key);
        
        if(note != null){
            form.manufacturingText.setValue(note.getText());
            form.id = note.getId();
        }
        
    }

    public TableDataModel fillComponentsTable(TableDataModel<TableDataRow<Integer>> componentsModel, boolean forDuplicate, List componentsList){
        try {
            componentsModel.clear();
            
            for(int iter = 0;iter < componentsList.size();iter++) {
                InventoryComponentDO componentRow = (InventoryComponentDO)componentsList.get(iter);
    
                   TableDataRow<Integer> row = componentsModel.createNewSet();
                   
                   if(componentRow.getId() != null && !forDuplicate){
                       row.key = (componentRow.getId());
                   }

//                  we need to create a dataset for the component auto complete
                    if(componentRow.getComponentNameId() == null)
                        ((DropDownField<Integer>)row.cells[0]).clear();
                    else{
                        TableDataModel<TableDataRow<Integer>> componentModel = new TableDataModel<TableDataRow<Integer>>();
                        componentModel.add(new TableDataRow<Integer>(componentRow.getComponentNameId(),new StringObject(componentRow.getComponentName())));
                        ((DropDownField<Integer>)(Field)row.cells[0]).setModel(componentModel);
                        ((DropDownField<Integer>)(Field)row.cells[0]).setValue(componentModel.get(0));
                    }
                    
                    row.cells[1].setValue(componentRow.getComponentDesc());
                    row.cells[2].setValue(componentRow.getQuantity());     
                    
                    componentsModel.add(row);
           } 
            
        } catch (Exception e) {
    
            e.printStackTrace();
            return null;
        }       
        
        return componentsModel;
    }
    
    public TableDataModel fillLocationsTable(TableDataModel<TableDataRow<Integer>> locationsModel, String isSerialized, List locationsList){
        try {
            locationsModel.clear();
            
            for(int iter = 0;iter < locationsList.size();iter++) {
                InventoryLocationDO locationRow = (InventoryLocationDO)locationsList.get(iter);
    
                   TableDataRow<Integer> row = locationsModel.createNewSet();
           
                    row.key = (locationRow.getId());
                    
                    row.cells[0].setValue(locationRow.getStorageLocation());
                    row.cells[1].setValue(locationRow.getLotNumber());
                    
                    if(CheckBox.CHECKED.equals(isSerialized))
                        row.cells[2].setValue(locationRow.getId());
                    else
                        row.cells[2].setValue(null);
                    
                    Datetime expDate = locationRow.getExpirationDate();
                    if(expDate.getDate() != null)
                        row.cells[3].setValue(expDate.toString());
                    else
                        row.cells[3].setValue(null);
                    
                    row.cells[4].setValue(locationRow.getQuantityOnHand());
                    
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
    
    public TableDataModel getMatches(String cat, TableDataModel model, String match, HashMap<String, FieldType> params) throws RPCException {
        //if(cat.equals("component"))
          //  return getComponentMatches(match, params);
        //else if(cat.equals("queryComponent"))
        //    return getComponentMatches(match, null);
        if(cat.equals("parentItem"))
            return getParentItemMatches(match);
        
        return null;    
    }
    
    private TableDataModel getParentItemMatches(String match) throws RPCException{
        InventoryItemRemote remote = (InventoryItemRemote)EJBFactory.lookup("openelis/InventoryItemBean/remote");
        TableDataModel<TableDataRow<Integer>> dataModel = new TableDataModel<TableDataRow<Integer>>();
        List autoCompleteList;

        String parsedMatch = match.replace('*', '%');
        
        autoCompleteList = remote.inventoryItemStoreAutoCompleteLookupByName(parsedMatch+"%", 10, false, true);
        
        for(int i=0; i < autoCompleteList.size(); i++){
            InventoryItemAutoDO resultDO = (InventoryItemAutoDO) autoCompleteList.get(i);
            
            Integer itemId = resultDO.getId();
            String name = resultDO.getName();
            String store = resultDO.getStore();
            
            dataModel.add(new TableDataRow<Integer>(itemId,
                                                    new FieldType[] {
                                                                     new StringObject(name),
                                                                     new StringObject(store)
                                                    }
                          )
            );
        }       
        
        return dataModel;           
    }
    
    private TableDataModel getComponentMatches(String match, Integer store, String currentName) throws RPCException{
        InventoryItemRemote remote = (InventoryItemRemote)EJBFactory.lookup("openelis/InventoryItemBean/remote");
        TableDataModel<TableDataRow<Integer>> dataModel = new TableDataModel<TableDataRow<Integer>>();
        List<IdNameDO> autoCompleteList;

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
        
        for(IdNameDO resultDO : autoCompleteList){

            dataModel.add(new TableDataRow<Integer>(resultDO.getId(), new StringObject(resultDO.getName())));

        }       
        
        return dataModel;       
    }
    
    private void setFieldsInRPC(InventoryItemForm form, InventoryItemDO inventoryItemDO, boolean forDuplicate){
        form.categoryId.setValue(new TableDataRow<Integer>(inventoryItemDO.getCategory()));
        form.description.setValue(inventoryItemDO.getDescription());
        form.dispensedUnitsId.setValue(new TableDataRow<Integer>(inventoryItemDO.getDispensedUnits()));
        
        if(!forDuplicate){
            form.id.setValue(inventoryItemDO.getId());
            form.averageCost.setValue(inventoryItemDO.getAveCost());
            form.averageDailyUse.setValue(inventoryItemDO.getAveDailyUse());
            form.averageLeadTime.setValue(inventoryItemDO.getAveLeadTime());
        }else{
            form.id.setValue(null);
            form.averageCost.setValue(null);
            form.averageDailyUse.setValue(null);
            form.averageLeadTime.setValue(null);
        }
        
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
        form.storeId.setValue(new TableDataRow<Integer>(inventoryItemDO.getStore()));
        form.parentRatio.setValue(inventoryItemDO.getParentRatio());    
        
        //we need to create a dataset for the parent inventory item auto complete
        if(inventoryItemDO.getParentInventoryItemId() == null)
            form.parentInventoryItem.clear();
        else{
            TableDataModel<TableDataRow<Integer>> parentItemModel = new TableDataModel<TableDataRow<Integer>>();
            parentItemModel.add(new TableDataRow<Integer>(inventoryItemDO.getParentInventoryItemId(),new StringObject(inventoryItemDO.getParentInventoryItem())));
            form.parentInventoryItem.setModel(parentItemModel);
            form.parentInventoryItem.setValue(parentItemModel.get(0));
        }
    }
    
    private InventoryItemDO getInventoryItemDOFromRPC(InventoryItemForm form){
        InventoryItemDO inventoryItemDO = new InventoryItemDO();
        
        if(form.averageCost != null && form.averageCost.getValue() != null)
            inventoryItemDO.setAveCost(form.averageCost.getValue());
        
        inventoryItemDO.setAveDailyUse(form.averageDailyUse.getValue());
        inventoryItemDO.setAveLeadTime(form.averageLeadTime.getValue());
        inventoryItemDO.setCategory((Integer)form.categoryId.getSelectedKey());
        inventoryItemDO.setDescription(form.description.getValue());
        inventoryItemDO.setDispensedUnits((Integer)form.dispensedUnitsId.getSelectedKey());
        inventoryItemDO.setId(form.id.getValue());
        inventoryItemDO.setIsActive(form.isActive.getValue());
        inventoryItemDO.setIsBulk(form.isBulk.getValue());
        inventoryItemDO.setIsLabor(form.isLabor.getValue());
        inventoryItemDO.setIsLotMaintained(form.isLotMaintained.getValue());
        inventoryItemDO.setIsNoInventory(form.isNoInventory.getValue());
        inventoryItemDO.setIsNotForSale(form.isNotForSale.getValue());
        inventoryItemDO.setIsReorderAuto(form.isReorderAuto.getValue());
        inventoryItemDO.setIsSerialMaintained(form.isSerialMaintained.getValue());
        inventoryItemDO.setIsSubAssembly(form.isSubAssembly.getValue());
        inventoryItemDO.setName(form.name.getValue());
        inventoryItemDO.setProductUri(form.productUri.getValue());
        inventoryItemDO.setQuantityMaxLevel(form.quantityMaxLevel.getValue());
        inventoryItemDO.setQuantityMinLevel(form.quantityMinLevel.getValue());
        inventoryItemDO.setQuantityToReorder(form.quantityToReorder.getValue());
        inventoryItemDO.setStore((Integer)form.storeId.getSelectedKey());
        inventoryItemDO.setParentInventoryItemId((Integer)form.parentInventoryItem.getSelectedKey());
        inventoryItemDO.setParentInventoryItem((String)form.parentInventoryItem.getTextValue());
        inventoryItemDO.setParentRatio(form.parentRatio.getValue());
        
        return inventoryItemDO;
    }
    
    private List getComponentsListFromRPC(TableDataModel<TableDataRow<Integer>> componentsTable, Integer itemId){
        List components = new ArrayList();
        List<TableDataRow<Integer>> deletedRows = componentsTable.getDeletions();

        for(int i=0; i<componentsTable.size(); i++){
            InventoryComponentDO componentDO = new InventoryComponentDO();
            TableDataRow<Integer> row = componentsTable.get(i);
            //hidden data
            Integer id = row.key;

            if(id != null)
                componentDO.setId(id);

            componentDO.setComponentNameId((Integer)((DropDownField)row.cells[0]).getSelectedKey());
            componentDO.setComponentName((String)((DropDownField)row.cells[0]).getTextValue());
            componentDO.setComponentDesc((String)((StringField)row.cells[1]).getValue());
            componentDO.setQuantity((Double)((DoubleField)row.cells[2]).getValue());
            componentDO.setParentInventoryItemId(itemId);
            
            components.add(componentDO);    
        }
        
        if(deletedRows != null){
            for(int j=0; j<deletedRows.size(); j++){
                TableDataRow<Integer> deletedRow = deletedRows.get(j);
                if(deletedRow.key != null){
                    InventoryComponentDO componentDO = new InventoryComponentDO();
                    componentDO.setDelete(true);
                    componentDO.setId(deletedRow.key);
                                    
                    components.add(componentDO);
                }
            }
        }
        
        return components;
    }
    
    public InventoryItemForm getComponentDescriptionText(InventoryItemForm rpc){
        InventoryItemRemote remote = (InventoryItemRemote)EJBFactory.lookup("openelis/InventoryItemBean/remote");
        
        rpc.descText = remote.getInventoryDescription(rpc.componentId);
        
        return rpc;
    }
    
    private void setRpcErrors(List exceptionList, TableField componentsTable, Form<? extends Object> form){
        HashMap<String,AbstractField> map = null;
        if(exceptionList.size() > 0)
            map = FormUtil.createFieldMap(form);
        //we need to get the keys and look them up in the resource bundle for internationalization
        for (int i=0; i<exceptionList.size();i++) {
            //if the error is inside the table
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
}
