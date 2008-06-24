package org.openelis.modules.inventoryItem.server;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.openelis.domain.IdNameDO;
import org.openelis.domain.InventoryComponentDO;
import org.openelis.domain.InventoryItemDO;
import org.openelis.domain.InventoryLocationDO;
import org.openelis.domain.NoteDO;
import org.openelis.gwt.common.EntityLockedException;
import org.openelis.gwt.common.FieldErrorException;
import org.openelis.gwt.common.FormErrorException;
import org.openelis.gwt.common.FormRPC;
import org.openelis.gwt.common.IForm;
import org.openelis.gwt.common.LastPageException;
import org.openelis.gwt.common.QueryNotFoundException;
import org.openelis.gwt.common.RPCException;
import org.openelis.gwt.common.TableFieldErrorException;
import org.openelis.gwt.common.data.AbstractField;
import org.openelis.gwt.common.data.BooleanObject;
import org.openelis.gwt.common.data.DataModel;
import org.openelis.gwt.common.data.DataObject;
import org.openelis.gwt.common.data.DataSet;
import org.openelis.gwt.common.data.DropDownField;
import org.openelis.gwt.common.data.NumberField;
import org.openelis.gwt.common.data.NumberObject;
import org.openelis.gwt.common.data.StringField;
import org.openelis.gwt.common.data.StringObject;
import org.openelis.gwt.common.data.TableField;
import org.openelis.gwt.common.data.TableModel;
import org.openelis.gwt.common.data.TableRow;
import org.openelis.gwt.server.ServiceUtils;
import org.openelis.gwt.services.AppScreenFormServiceInt;
import org.openelis.gwt.services.AutoCompleteServiceInt;
import org.openelis.meta.InventoryItemMeta;
import org.openelis.meta.InventoryNoteMeta;
import org.openelis.newmeta.InventoryItemMetaMap;
import org.openelis.newmeta.InventoryLocationMetaMap;
import org.openelis.newmeta.StorageLocationMeta;
import org.openelis.persistence.CachingManager;
import org.openelis.persistence.EJBFactory;
import org.openelis.remote.CategoryRemote;
import org.openelis.remote.InventoryItemRemote;
import org.openelis.server.constants.Constants;
import org.openelis.util.SessionManager;
import org.openelis.util.UTFResource;
import org.openelis.util.XMLUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import edu.uiowa.uhl.security.domain.SystemUserDO;
import edu.uiowa.uhl.security.remote.SystemUserRemote;

public class InventoryItemService implements AppScreenFormServiceInt, 
									     AutoCompleteServiceInt {
    
    private static final int leftTableRowsPerPage = 24;
    
    private static final InventoryItemMetaMap InvItemMeta = new InventoryItemMetaMap();
    
    private UTFResource openElisConstants= UTFResource.getBundle((String)SessionManager.getSession().getAttribute("locale"));

	public DataModel commitQuery(FormRPC rpcSend, DataModel model) throws RPCException {
        List inventoryItemNames;
        //if the rpc is null then we need to get the page
        if(rpcSend == null){

            FormRPC rpc = (FormRPC)SessionManager.getSession().getAttribute("InventoryItemQuery");
    
            if(rpc == null)
                throw new QueryNotFoundException(openElisConstants.getString("queryExpiredException"));

            InventoryItemRemote remote = (InventoryItemRemote)EJBFactory.lookup("openelis/InventoryItemBean/remote");
            try{
                inventoryItemNames = remote.query(rpc.getFieldMap(), (model.getPage()*leftTableRowsPerPage), leftTableRowsPerPage+1);
            }catch(Exception e){
                if(e instanceof LastPageException){
                    throw new LastPageException(openElisConstants.getString("lastPageException"));
                }else{
                    throw new RPCException(e.getMessage()); 
                }           
            }    
        }else{
            InventoryItemRemote remote = (InventoryItemRemote)EJBFactory.lookup("openelis/InventoryItemBean/remote");

            HashMap<String,AbstractField> fields = rpcSend.getFieldMap();
            fields.remove("componentsTable");
            fields.remove("locQuantitiesTable");

            try{    
                inventoryItemNames = remote.query(fields,0,leftTableRowsPerPage);
    
            }catch(Exception e){
                throw new RPCException(e.getMessage());
            }
    
        
            //need to save the rpc used to the encache
            SessionManager.getSession().setAttribute("InventoryItemQuery", rpcSend);
        }
        
        //fill the model with the query results
        int i=0;
        model.clear();
        while(i < inventoryItemNames.size() && i < leftTableRowsPerPage) {
            IdNameDO resultDO = (IdNameDO)inventoryItemNames.get(i);
            model.add(new NumberObject(resultDO.getId()),new StringObject(resultDO.getName()));
            i++;
        } 
 
        return model;
    }

    public FormRPC commitAdd(FormRPC rpcSend, FormRPC rpcReturn) throws RPCException {
//      remote interface to call the inventory bean
        InventoryItemRemote remote = (InventoryItemRemote)EJBFactory.lookup("openelis/InventoryItemBean/remote");
        InventoryItemDO inventoryItemDO = new InventoryItemDO();
        List components = new ArrayList();
       
        NoteDO inventoryItemNote = new NoteDO();
        
        //build the organizationAddress DO from the form
        //organization info
        inventoryItemDO = getInventoryItemDOFromRPC(rpcSend);
        
        //components info
        TableModel componentsTable = (TableModel)rpcSend.getField("componentsTable").getValue();
        components = getComponentsListFromRPC(componentsTable, inventoryItemDO.getId());
        
//      build the noteDo from the form
        inventoryItemNote.setSubject((String)rpcSend.getFieldValue(InventoryNoteMeta.SUBJECT));
        inventoryItemNote.setText((String)rpcSend.getFieldValue(InventoryNoteMeta.TEXT));
        inventoryItemNote.setIsExternal("Y");
        
        //validate the fields on the backend
        List exceptionList = remote.validateForAdd(inventoryItemDO, components);
        
        if(exceptionList.size() > 0){
            setRpcErrors(exceptionList, componentsTable, rpcSend);
            return rpcSend;
        } 
        
        //send the changes to the database
        Integer inventoryItemId;
        try{
            inventoryItemId = (Integer)remote.updateInventory(inventoryItemDO, components, inventoryItemNote);
        }catch(Exception e){
            exceptionList = new ArrayList();
            exceptionList.add(e);
            
            setRpcErrors(exceptionList, componentsTable, rpcSend);
            
            return rpcSend;
        }
        
        inventoryItemDO.setId(inventoryItemId);

//      set the fields in the RPC
        setFieldsInRPC(rpcReturn, inventoryItemDO);

        return rpcReturn;
    }

    public FormRPC commitUpdate(FormRPC rpcSend, FormRPC rpcReturn) throws RPCException {
//      remote interface to call the inventory bean
        InventoryItemRemote remote = (InventoryItemRemote)EJBFactory.lookup("openelis/InventoryItemBean/remote");
        InventoryItemDO inventoryItemDO = new InventoryItemDO();
        List components = new ArrayList();
        //List locations = new ArrayList();
        NoteDO inventoryItemNote = new NoteDO();

        //build the organizationAddress DO from the form
        inventoryItemDO = getInventoryItemDOFromRPC(rpcSend);
        
        //components info
        TableModel componentsTable = (TableModel)rpcSend.getField("componentsTable").getValue();
        components = getComponentsListFromRPC(componentsTable, inventoryItemDO.getId());   
        
        //locations info
        
//      build the noteDo from the form
        inventoryItemNote.setSubject((String)rpcSend.getFieldValue(InventoryNoteMeta.SUBJECT));
        inventoryItemNote.setText((String)rpcSend.getFieldValue(InventoryNoteMeta.TEXT));
        inventoryItemNote.setIsExternal("Y");
        
//      validate the fields on the backend
        List exceptionList = remote.validateForUpdate(inventoryItemDO, components);
        if(exceptionList.size() > 0){
            setRpcErrors(exceptionList, componentsTable, rpcSend);
            
            return rpcSend;
        } 
        
//      send the changes to the database
        try{
            remote.updateInventory(inventoryItemDO, components, inventoryItemNote);
        }catch(Exception e){
            if(e instanceof EntityLockedException)
                throw new RPCException(e.getMessage());
            
            exceptionList = new ArrayList();
            exceptionList.add(e);
            
            setRpcErrors(exceptionList, componentsTable, rpcSend);
            
            return rpcSend;
        }
        
        //set the fields in the RPC
        setFieldsInRPC(rpcReturn, inventoryItemDO);   
        
        return rpcReturn;
    }

    public FormRPC commitDelete(DataSet key, FormRPC rpcReturn) throws RPCException {
    	// TODO Auto-generated method stub
    	return null;
    }

    public FormRPC abort(DataSet key, FormRPC rpcReturn) throws RPCException {
//      remote interface to call the inventory bean
        InventoryItemRemote remote = (InventoryItemRemote)EJBFactory.lookup("openelis/InventoryItemBean/remote");
        
        
        InventoryItemDO inventoryItemDO = remote.getInventoryItemAndUnlock((Integer)key.getKey().getValue());

//      set the fields in the RPC
        setFieldsInRPC(rpcReturn, inventoryItemDO);
        
        return rpcReturn;  
    }

    public FormRPC fetch(DataSet key, FormRPC rpcReturn) throws RPCException {
        //remote interface to call the inventory bean
        InventoryItemRemote remote = (InventoryItemRemote)EJBFactory.lookup("openelis/InventoryItemBean/remote");
        
        InventoryItemDO inventoryItemDO = remote.getInventoryItem((Integer)key.getKey().getValue());

//      set the fields in the RPC
        setFieldsInRPC(rpcReturn, inventoryItemDO);
        
      return rpcReturn;
    }

    public FormRPC fetchForUpdate(DataSet key, FormRPC rpcReturn) throws RPCException {
//      remote interface to call the inventory bean
        InventoryItemRemote remote = (InventoryItemRemote)EJBFactory.lookup("openelis/InventoryItemBean/remote");
        
        InventoryItemDO inventoryItemDO = new InventoryItemDO();
        try{
            inventoryItemDO = remote.getInventoryItemAndLock((Integer)key.getKey().getValue());
        }catch(Exception e){
            throw new RPCException(e.getMessage());
        }
        
//      set the fields in the RPC
        setFieldsInRPC(rpcReturn, inventoryItemDO);
        
        return rpcReturn;  
    }

    public String getXML() throws RPCException {
    	return ServiceUtils.getXML(Constants.APP_ROOT+"/Forms/inventoryItem.xsl");
    }

    public HashMap<String,DataObject> getXMLData() throws RPCException {
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
        
        HashMap<String,DataObject> map = new HashMap<String,DataObject>();
        map.put("xml", xml);
        map.put("categories", categoriesDropdownField);
        map.put("stores", storesDropdownField);
        map.put("units", unitsDropdownField);
        
        return map;
    }

    public HashMap<String,DataObject> getXMLData(HashMap<String,DataObject> args) throws RPCException {
    	// TODO Auto-generated method stub
    	return null;
    }
    
    public TableField getComponentsModel(NumberObject itemId, BooleanObject forDuplicate, TableField model){
        InventoryItemRemote remote = (InventoryItemRemote)EJBFactory.lookup("openelis/InventoryItemBean/remote");
        List componentsList = remote.getInventoryComponents((Integer)itemId.getValue());
        model.setValue(fillComponentsTable((TableModel)model.getValue(),componentsList, ((Boolean)forDuplicate.getValue()).booleanValue()));
        return model;
    }
    
    public TableField getLocationsModel(NumberObject itemId, TableField model){
        InventoryItemRemote remote = (InventoryItemRemote)EJBFactory.lookup("openelis/InventoryItemBean/remote");
        List locationsList = remote.getInventoryLocations((Integer)itemId.getValue());
        model.setValue(fillLocationsTable((TableModel)model.getValue(),locationsList));
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
    
             Element mainRowPanel = (Element) doc.createElement("panel");
             Element topRowPanel = (Element) doc.createElement("panel");
             Element titleWidgetTag = (Element) doc.createElement("widget");
             Element titleText = (Element) doc.createElement("text");
             Element authorWidgetTag = (Element) doc.createElement("widget");
             Element authorPanel = (Element) doc.createElement("panel");
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
             mainRowPanel.setAttribute("layout", "vertical");
             mainRowPanel.setAttribute("width", "531px");
             
             topRowPanel.setAttribute("layout", "horizontal");
             topRowPanel.setAttribute("width", "531px");
             titleText.setAttribute("key", "note"+i+"Title");
             titleText.setAttribute("style", "notesSubjectText");
             titleText.appendChild(doc.createTextNode(subject));
             authorWidgetTag.setAttribute("halign", "right");
             authorPanel.setAttribute("layout", "vertical");
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

    public TableModel fillComponentsTable(TableModel componentsModel, List componentsList, boolean forDuplicate){
        try {
            componentsModel.reset();
            
            for(int iter = 0;iter < componentsList.size();iter++) {
                InventoryComponentDO componentRow = (InventoryComponentDO)componentsList.get(iter);
    
                   TableRow row = componentsModel.createRow();
                   NumberField id = new NumberField(NumberObject.Type.INTEGER);
                    
                   if(forDuplicate)
                       id.setValue(null);
                   else
                       id.setValue(componentRow.getId());
                    
                    row.addHidden("id", id);

//                  we need to create a dataset for the component auto complete
                    if(componentRow.getComponentNameId() == null)
                        row.getColumn(0).setValue(null);
                    else{
                        DataSet componentSet = new DataSet();
                        NumberObject componentId = new NumberObject(componentRow.getComponentNameId());
                        StringObject componentText = new StringObject(componentRow.getComponentName());
                        componentSet.setKey(componentId);
                        componentSet.addObject(componentText);
                        row.getColumn(0).setValue(componentSet);
                    }
                    
                    row.getColumn(1).setValue(componentRow.getComponentDesc());
                    row.getColumn(2).setValue(componentRow.getQuantity());     
                    
                    componentsModel.addRow(row);
           } 
            
        } catch (Exception e) {
    
            e.printStackTrace();
            return null;
        }       
        
        return componentsModel;
    }
    
    public TableModel fillLocationsTable(TableModel locationsModel, List locationsList){
        try {
            locationsModel.reset();
            
            for(int iter = 0;iter < locationsList.size();iter++) {
                InventoryLocationDO locationRow = (InventoryLocationDO)locationsList.get(iter);
    
                   TableRow row = locationsModel.createRow();
                   NumberField id = new NumberField(locationRow.getId());
           
                    row.addHidden("id", id);
                    
                    row.getColumn(0).setValue(locationRow.getStorageLocation());
                    row.getColumn(1).setValue(locationRow.getLotNumber());
                    row.getColumn(2).setValue(locationRow.getExpirationDate().toString());
                    row.getColumn(3).setValue(locationRow.getQuantityOnHand());
                    
                    locationsModel.addRow(row);
           } 
            
        } catch (Exception e) {
    
            e.printStackTrace();
            return null;
        }       
        
        return locationsModel;
    }
    
    public DataModel getMatches(String cat, DataModel model, String match, HashMap params) throws RPCException {
        if(cat.equals("component"))
            return getComponentMatches(match, params);
        else if(cat.equals("queryComponent"))
            return getComponentMatches(match, null);
        
        return null;    
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
        int id = -1;
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
                    
        returnModel.add(new NumberObject(0),new StringObject(""));
        
        for(IdNameDO resultDO : entries) { 
            returnModel.add(new NumberObject(resultDO.getId()),new StringObject(resultDO.getName()));
        }       
        
        return returnModel;
    }

    private void setFieldsInRPC(FormRPC rpcReturn, InventoryItemDO inventoryItemDO){
        rpcReturn.setFieldValue(InventoryItemMeta.AVERAGE_COST, inventoryItemDO.getAveCost());
        rpcReturn.setFieldValue(InventoryItemMeta.AVERAGE_DAILY_USE, inventoryItemDO.getAveDailyUse());
        rpcReturn.setFieldValue(InventoryItemMeta.AVERAGE_LEAD_TIME, inventoryItemDO.getAveLeadTime());
        rpcReturn.setFieldValue(InventoryItemMeta.CATEGORY_ID, inventoryItemDO.getCategory());
        rpcReturn.setFieldValue(InventoryItemMeta.DESCRIPTION, inventoryItemDO.getDescription());
        rpcReturn.setFieldValue(InventoryItemMeta.DISPENSED_UNITS_ID, inventoryItemDO.getDispensedUnits());
        rpcReturn.setFieldValue(InventoryItemMeta.ID, inventoryItemDO.getId());
        rpcReturn.setFieldValue(InventoryItemMeta.IS_ACTIVE, inventoryItemDO.getIsActive());
        rpcReturn.setFieldValue(InventoryItemMeta.IS_BULK, inventoryItemDO.getIsBulk());
        rpcReturn.setFieldValue(InventoryItemMeta.IS_LABOR, inventoryItemDO.getIsLabor());
        rpcReturn.setFieldValue(InventoryItemMeta.IS_LOT_MAINTAINED, inventoryItemDO.getIsLotMaintained());
        rpcReturn.setFieldValue(InventoryItemMeta.IS_NO_INVENTORY, inventoryItemDO.getIsNoInventory());
        rpcReturn.setFieldValue(InventoryItemMeta.IS_NOT_FOR_SALE, inventoryItemDO.getIsNotForSale());
        rpcReturn.setFieldValue(InventoryItemMeta.IS_REORDER_AUTO, inventoryItemDO.getIsReorderAuto());
        rpcReturn.setFieldValue(InventoryItemMeta.IS_SERIAL_MAINTAINED, inventoryItemDO.getIsSerialMaintained());
        rpcReturn.setFieldValue(InventoryItemMeta.IS_SUB_ASSEMBLY, inventoryItemDO.getIsSubAssembly());
        rpcReturn.setFieldValue(InventoryItemMeta.NAME, inventoryItemDO.getName());
        rpcReturn.setFieldValue(InventoryItemMeta.PRODUCT_URI, inventoryItemDO.getProductUri());
        rpcReturn.setFieldValue(InventoryItemMeta.PURCHASED_UNITS_ID, inventoryItemDO.getPurchasedUnits());
        rpcReturn.setFieldValue(InventoryItemMeta.QUANTITY_MAX_LEVEL, inventoryItemDO.getQuantityMaxLevel());
        rpcReturn.setFieldValue(InventoryItemMeta.QUANTITY_MIN_LEVEL, inventoryItemDO.getQuantityMinLevel());
        rpcReturn.setFieldValue(InventoryItemMeta.QUANTITY_TO_REORDER, inventoryItemDO.getQuantityToReorder());
        rpcReturn.setFieldValue(InventoryItemMeta.STORE_ID, inventoryItemDO.getStore());    }
    
    private InventoryItemDO getInventoryItemDOFromRPC(FormRPC rpcSend){
        InventoryItemDO inventoryItemDO = new InventoryItemDO();
        
        if(rpcSend.getFieldValue(InventoryItemMeta.AVERAGE_COST) != null)
            inventoryItemDO.setAveCost((Double)rpcSend.getFieldValue(InventoryItemMeta.AVERAGE_COST));
        inventoryItemDO.setAveDailyUse((Integer)rpcSend.getFieldValue(InventoryItemMeta.AVERAGE_DAILY_USE));
        inventoryItemDO.setAveLeadTime((Integer)rpcSend.getFieldValue(InventoryItemMeta.AVERAGE_LEAD_TIME));
        inventoryItemDO.setCategory((Integer)rpcSend.getFieldValue(InventoryItemMeta.CATEGORY_ID));
        inventoryItemDO.setDescription((String)rpcSend.getFieldValue(InventoryItemMeta.DESCRIPTION));
        inventoryItemDO.setDispensedUnits((Integer)rpcSend.getFieldValue(InventoryItemMeta.DISPENSED_UNITS_ID));
        inventoryItemDO.setId((Integer)rpcSend.getFieldValue(InventoryItemMeta.ID));
        inventoryItemDO.setIsActive((String)rpcSend.getFieldValue(InventoryItemMeta.IS_ACTIVE));
        inventoryItemDO.setIsBulk((String)rpcSend.getFieldValue(InventoryItemMeta.IS_BULK));
        inventoryItemDO.setIsLabor((String)rpcSend.getFieldValue(InventoryItemMeta.IS_LABOR));
        inventoryItemDO.setIsLotMaintained((String)rpcSend.getFieldValue(InventoryItemMeta.IS_LOT_MAINTAINED));
        inventoryItemDO.setIsNoInventory((String)rpcSend.getFieldValue(InventoryItemMeta.IS_NO_INVENTORY));
        inventoryItemDO.setIsNotForSale((String)rpcSend.getFieldValue(InventoryItemMeta.IS_NOT_FOR_SALE));
        inventoryItemDO.setIsReorderAuto((String)rpcSend.getFieldValue(InventoryItemMeta.IS_REORDER_AUTO));
        inventoryItemDO.setIsSerialMaintained((String)rpcSend.getFieldValue(InventoryItemMeta.IS_SERIAL_MAINTAINED));
        inventoryItemDO.setIsSubAssembly((String)rpcSend.getFieldValue(InventoryItemMeta.IS_SUB_ASSEMBLY));
        inventoryItemDO.setName((String)rpcSend.getFieldValue(InventoryItemMeta.NAME));
        inventoryItemDO.setProductUri((String)rpcSend.getFieldValue(InventoryItemMeta.PRODUCT_URI));
        inventoryItemDO.setPurchasedUnits((Integer)rpcSend.getFieldValue(InventoryItemMeta.PURCHASED_UNITS_ID));
        inventoryItemDO.setQuantityMaxLevel((Integer)rpcSend.getFieldValue(InventoryItemMeta.QUANTITY_MAX_LEVEL));
        inventoryItemDO.setQuantityMinLevel((Integer)rpcSend.getFieldValue(InventoryItemMeta.QUANTITY_MIN_LEVEL));
        inventoryItemDO.setQuantityToReorder((Integer)rpcSend.getFieldValue(InventoryItemMeta.QUANTITY_TO_REORDER));
        inventoryItemDO.setStore((Integer)rpcSend.getFieldValue(InventoryItemMeta.STORE_ID));
        
        return inventoryItemDO;
    }
    
    private List getComponentsListFromRPC(TableModel componentsTable, Integer itemId){
        List components = new ArrayList();
        
        for(int i=0; i<componentsTable.numRows(); i++){
            InventoryComponentDO componentDO = new InventoryComponentDO();
            TableRow row = componentsTable.getRow(i);
            //hidden data
            NumberField id = (NumberField)row.getHidden("id");
            StringField deleteFlag = (StringField)row.getHidden("deleteFlag");
            if(deleteFlag == null){
                componentDO.setDelete(false);
            }else{
                componentDO.setDelete("Y".equals(deleteFlag.getValue()));
            }
            //if the user created the row and clicked the remove button before commit...
            //we dont need to do anything with that row
            if(deleteFlag != null && "Y".equals(deleteFlag.getValue()) && id == null){
                //do nothing
            }else{
                if(id != null)
                    componentDO.setId((Integer)id.getValue());

                componentDO.setComponentNameId((Integer)((DropDownField)row.getColumn(0)).getValue());
                componentDO.setComponentName((String)((DropDownField)row.getColumn(0)).getTextValue());
                componentDO.setComponentDesc((String)((StringField)row.getColumn(1)).getValue());
                componentDO.setQuantity((Double)((NumberField)row.getColumn(2)).getValue());
                componentDO.setInventoryItemId(itemId);
                
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
    
    private void setRpcErrors(List exceptionList, TableModel componentsTable, FormRPC rpcSend){
        //we need to get the keys and look them up in the resource bundle for internationalization
        for (int i=0; i<exceptionList.size();i++) {
            //if the error is inside the org contacts table
            if(exceptionList.get(i) instanceof TableFieldErrorException){
                TableRow row = componentsTable.getRow(((TableFieldErrorException)exceptionList.get(i)).getRowIndex());
                row.getColumn(componentsTable.getColumnIndexByFieldName(((TableFieldErrorException)exceptionList.get(i)).getFieldName()))
                                                                        .addError(openElisConstants.getString(((FieldErrorException)exceptionList.get(i)).getMessage()));
            //if the error is on the field
            }else if(exceptionList.get(i) instanceof FieldErrorException)
                rpcSend.getField(((FieldErrorException)exceptionList.get(i)).getFieldName()).addError(openElisConstants.getString(((FieldErrorException)exceptionList.get(i)).getMessage()));
            //if the error is on the entire form
            else if(exceptionList.get(i) instanceof FormErrorException)
                rpcSend.addError(openElisConstants.getString(((FormErrorException)exceptionList.get(i)).getMessage()));
        }   
        rpcSend.status = IForm.Status.invalid;
    }
}
