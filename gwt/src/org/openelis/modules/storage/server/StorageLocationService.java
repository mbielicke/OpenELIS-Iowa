package org.openelis.modules.storage.server;

import org.openelis.domain.StorageLocationDO;
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
import org.openelis.gwt.common.data.CheckField;
import org.openelis.gwt.common.data.DataModel;
import org.openelis.gwt.common.data.DataObject;
import org.openelis.gwt.common.data.DataSet;
import org.openelis.gwt.common.data.DropDownField;
import org.openelis.gwt.common.data.ModelField;
import org.openelis.gwt.common.data.NumberField;
import org.openelis.gwt.common.data.NumberObject;
import org.openelis.gwt.common.data.StringField;
import org.openelis.gwt.common.data.StringObject;
import org.openelis.gwt.common.data.TableModel;
import org.openelis.gwt.common.data.TableRow;
import org.openelis.gwt.server.ServiceUtils;
import org.openelis.gwt.services.AppScreenFormServiceInt;
import org.openelis.gwt.services.AutoCompleteServiceInt;
import org.openelis.meta.OrganizationParentOrganizationMeta;
import org.openelis.meta.StorageLocationChildMeta;
import org.openelis.meta.StorageLocationMeta;
import org.openelis.meta.StorageLocationStorageUnitMeta;
import org.openelis.persistence.CachingManager;
import org.openelis.persistence.EJBFactory;
import org.openelis.remote.StorageLocationRemote;
import org.openelis.remote.StorageUnitRemote;
import org.openelis.server.constants.Constants;
import org.openelis.util.SessionManager;
import org.openelis.util.UTFResource;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

public class StorageLocationService implements AppScreenFormServiceInt,
   											   AutoCompleteServiceInt{

	private static final int leftTableRowsPerPage = 19;
	
	private UTFResource openElisConstants= UTFResource.getBundle("org.openelis.modules.main.server.constants.OpenELISConstants",
			new Locale(((SessionManager.getSession() == null  || (String)SessionManager.getSession().getAttribute("locale") == null) 
					? "en" : (String)SessionManager.getSession().getAttribute("locale"))));

	public FormRPC abort(DataSet key, FormRPC rpcReturn) throws RPCException {
//		remote interface to call the storage location bean
		StorageLocationRemote remote = (StorageLocationRemote)EJBFactory.lookup("openelis/StorageLocationBean/remote");
		
		
		StorageLocationDO storageLocDO = remote.getStorageLocAndUnlock((Integer)key.getKey().getValue());

//		set the fields in the RPC
		setFieldsInRPC(rpcReturn, storageLocDO);
        
//		load the children
        List childrenList = remote.getStorageLocChildren((Integer)key.getKey().getValue());
        //need to build the children table now...
        TableModel rmodel = (TableModel)fillChildrenTable((TableModel)rpcReturn.getField("childStorageLocsTable").getValue(),childrenList);
        rpcReturn.setFieldValue("childStorageLocsTable",rmodel);
        
		return rpcReturn;  
	}

	public FormRPC commitAdd(FormRPC rpcSend, FormRPC rpcReturn) throws RPCException {
//		remote interface to call the storageLocation bean
		StorageLocationRemote remote = (StorageLocationRemote)EJBFactory.lookup("openelis/StorageLocationBean/remote");
		StorageLocationDO newStorageLocDO = new StorageLocationDO();
		List storageLocationChildren = new ArrayList();
		
//		build the storage unit DO from the form
		newStorageLocDO = getStorageLocationDOFromRPC(rpcSend);
		
//		child locs info
		TableModel childTable = (TableModel)rpcSend.getField("childStorageLocsTable").getValue();
		storageLocationChildren = getChildStorageLocsFromRPC(childTable);
				
		//validate the fields on the backend
		List exceptionList = remote.validateForAdd(newStorageLocDO, storageLocationChildren);
		
		if(exceptionList.size() > 0){
			setRpcErrors(exceptionList, childTable, rpcSend);
			return rpcSend;
		} 
		
		//send the changes to the database
		Integer storageLocId;
		try{
			storageLocId = (Integer)remote.updateStorageLoc(newStorageLocDO, storageLocationChildren);
		}catch(Exception e){
			exceptionList = new ArrayList();
			exceptionList.add(e);
			
			setRpcErrors(exceptionList, childTable, rpcSend);
			
			return rpcSend;
		}
		
		newStorageLocDO.setId(storageLocId);
        
//		set the fields in the RPC
		setFieldsInRPC(rpcReturn, newStorageLocDO);

		return rpcReturn;
	}

	public DataModel commitQuery(FormRPC rpcSend, DataModel model) throws RPCException {
//		if the rpc is null then we need to get the page
		if(rpcSend == null){
//			need to get the query rpc out of the cache
	        FormRPC rpc = (FormRPC)CachingManager.getElement("screenQueryRpc", SessionManager.getSession().getAttribute("systemUserId")+":StorageLocation");

	        if(rpc == null)
	        	throw new QueryNotFoundException(openElisConstants.getString("queryExpiredException"));
			
	        List storageLocs = null;
	        StorageLocationRemote remote = (StorageLocationRemote)EJBFactory.lookup("openelis/StorageLocationBean/remote");
	        try{
	        	storageLocs = remote.query(rpc.getFieldMap(), (model.getPage()*leftTableRowsPerPage), leftTableRowsPerPage+1);
	        }catch(Exception e){
	        	if(e instanceof LastPageException){
	        		throw new LastPageException(openElisConstants.getString("lastPageException"));
	        	}else{
	        		throw new RPCException(e.getMessage());	
	        	}
	        }
	        
	        int i=0;
	        model.clear();
	        while(i < storageLocs.size() && i < leftTableRowsPerPage) {
	    	   	Object[] result = (Object[])storageLocs.get(i);
				//org id
				Integer idResult = (Integer)result[0];
				//org name
				String nameResult = (String)result[1];

				DataSet row = new DataSet();
				NumberObject id = new NumberObject(NumberObject.INTEGER);
				StringObject name = new StringObject();
				name.setValue(nameResult);
				id.setValue(idResult);
				
				row.setKey(id);			
				row.addObject(name);
				model.add(row);
				i++;
	         } 

	        return model;
		}else{
			StorageLocationRemote remote = (StorageLocationRemote)EJBFactory.lookup("openelis/StorageLocationBean/remote");
			
			HashMap<String,AbstractField> fields = rpcSend.getFieldMap();
			
			List storageLocNames = new ArrayList();
			try{
				storageLocNames = remote.query(fields,0,leftTableRowsPerPage);

		}catch(Exception e){
			throw new RPCException(e.getMessage());
		}
		
		Iterator namesItr = storageLocNames.iterator();
		model=  new DataModel();
		
		while(namesItr.hasNext()){
			Object[] result = (Object[])namesItr.next();
			//org id
			Integer id = (Integer)result[0];
			//org name
			String name = (String)result[1];

			DataSet row = new DataSet();

			 NumberObject idField = new NumberObject(NumberObject.INTEGER);
			 StringObject nameField = new StringObject();
			 nameField.setValue(name);
      
			 idField.setValue(id);
			 row.setKey(idField);
			 row.addObject(nameField);

			 model.add(row);
		}
        
        //need to save the rpc used to the encache
        if(SessionManager.getSession().getAttribute("systemUserId") == null)
        	SessionManager.getSession().setAttribute("systemUserId", remote.getSystemUserId().toString());
        CachingManager.putElement("screenQueryRpc", SessionManager.getSession().getAttribute("systemUserId")+":StorageLocation", rpcSend);
		}
		
		return model;
	}

	public FormRPC commitUpdate(FormRPC rpcSend, FormRPC rpcReturn) throws RPCException {
//		remote interface to call the storage loc bean
		StorageLocationRemote remote = (StorageLocationRemote)EJBFactory.lookup("openelis/StorageLocationBean/remote");
		StorageLocationDO newStorageLocDO = new StorageLocationDO();
		List storageLocationChildren = new ArrayList();
		
		//build the DO from the form
		newStorageLocDO = getStorageLocationDOFromRPC(rpcSend);
		
//		children info
		TableModel childTable = (TableModel)rpcSend.getField("childStorageLocsTable").getValue();
		storageLocationChildren = getChildStorageLocsFromRPC(childTable);
		
		//validate the fields on the backend
		List exceptionList = remote.validateForUpdate(newStorageLocDO, storageLocationChildren);
		if(exceptionList.size() > 0){
			setRpcErrors(exceptionList, childTable, rpcSend);
			
			return rpcSend;
		} 
		
//		send the changes to the database
		try{
			remote.updateStorageLoc(newStorageLocDO, storageLocationChildren);
		}catch(Exception e){
			exceptionList = new ArrayList();
			exceptionList.add(e);
			
			setRpcErrors(exceptionList, childTable, rpcSend);
			
			return rpcSend;
		}
		
//		set the fields in the RPC
		setFieldsInRPC(rpcReturn, newStorageLocDO);
        
		return rpcReturn;
	}

	public FormRPC commitDelete(DataSet key, FormRPC rpcReturn) throws RPCException {
//		remote interface to call the storage location bean
		StorageLocationRemote remote = (StorageLocationRemote)EJBFactory.lookup("openelis/StorageLocationBean/remote");
		
		//validate the fields on the backend
		List exceptionList = remote.validateForDelete((Integer)key.getKey().getValue());
		if(exceptionList.size() > 0){
			setRpcErrors(exceptionList, null, rpcReturn);
			
			return rpcReturn;
		} 
		
		try {
			remote.deleteStorageLoc((Integer)key.getKey().getValue());
			
		} catch (Exception e) {
			exceptionList = new ArrayList();
			exceptionList.add(e);
			
			setRpcErrors(exceptionList, null, rpcReturn);
			return rpcReturn;
		}	
		
		setFieldsInRPC(rpcReturn, new StorageLocationDO());
		rpcReturn.setFieldValue("childStorageLocsTable",null);
		
		return rpcReturn;
	}

	public FormRPC fetch(DataSet key, FormRPC rpcReturn) throws RPCException {
//		remote interface to call the storage loc bean
		StorageLocationRemote remote = (StorageLocationRemote)EJBFactory.lookup("openelis/StorageLocationBean/remote");
		
		StorageLocationDO storageLocDO = remote.getStorageLoc((Integer)key.getKey().getValue());
		
//		set the fields in the RPC
		setFieldsInRPC(rpcReturn, storageLocDO);
		
//		load the children
        List childrenList = remote.getStorageLocChildren((Integer)key.getKey().getValue());
        //need to build the children table now...
        TableModel rmodel = (TableModel)fillChildrenTable((TableModel)rpcReturn.getField("childStorageLocsTable").getValue(),childrenList);
        rpcReturn.setFieldValue("childStorageLocsTable",rmodel);

		return rpcReturn;
	}

	public FormRPC fetchForUpdate(DataSet key, FormRPC rpcReturn) throws RPCException {
//		remote interface to call the storage loc bean
		StorageLocationRemote remote = (StorageLocationRemote)EJBFactory.lookup("openelis/StorageLocationBean/remote");
		StorageLocationDO storageLocDO = new StorageLocationDO();
		
		try{
			storageLocDO = remote.getStorageLocAndLock((Integer)key.getKey().getValue());
		}catch(Exception e){
			throw new RPCException(e.getMessage());
		}
		
//		set the fields in the RPC
		setFieldsInRPC(rpcReturn, storageLocDO);
		
//		load the children
        List childrenList = remote.getStorageLocChildren((Integer)key.getKey().getValue());
        //need to build the children table now...
        TableModel rmodel = (TableModel)fillChildrenTable((TableModel)rpcReturn.getField("childStorageLocsTable").getValue(),childrenList);
        rpcReturn.setFieldValue("childStorageLocsTable",rmodel);

		return rpcReturn;
	}

	private void setFieldsInRPC(FormRPC rpcReturn, StorageLocationDO storageLocDO){
		rpcReturn.setFieldValue(StorageLocationMeta.ID, storageLocDO.getId());
		rpcReturn.setFieldValue(StorageLocationMeta.IS_AVAILABLE, (storageLocDO.getIsAvailable() == null ? null : storageLocDO.getIsAvailable().trim()));
		rpcReturn.setFieldValue(StorageLocationMeta.LOCATION, (storageLocDO.getLocation() == null ? null : storageLocDO.getLocation().trim()));
		rpcReturn.setFieldValue(StorageLocationMeta.NAME, (storageLocDO.getName() == null ? null : storageLocDO.getName().trim()));
		
//		we need to create a dataset for the storage unit auto complete
		if(storageLocDO.getStorageUnitId() == null)
			rpcReturn.setFieldValue(StorageLocationStorageUnitMeta.DESCRIPTION, null);
		else{
			DataSet storageUnitSet = new DataSet();
			NumberObject id = new NumberObject(NumberObject.INTEGER);
			StringObject text = new StringObject();
			id.setValue(storageLocDO.getStorageUnitId());
			text.setValue(storageLocDO.getStorageUnit().trim());
			storageUnitSet.setKey(id);
			storageUnitSet.addObject(text);
			rpcReturn.setFieldValue(StorageLocationStorageUnitMeta.DESCRIPTION, storageUnitSet);
		}
	}
	
	private StorageLocationDO getStorageLocationDOFromRPC(FormRPC rpcSend){
		StorageLocationDO newStorageLocDO = new StorageLocationDO();
		
		newStorageLocDO.setId((Integer)rpcSend.getFieldValue(StorageLocationMeta.ID));
		newStorageLocDO.setIsAvailable((String) rpcSend.getFieldValue(StorageLocationMeta.IS_AVAILABLE));
		newStorageLocDO.setLocation(((String)rpcSend.getFieldValue(StorageLocationMeta.LOCATION)).trim());
		newStorageLocDO.setName(((String)rpcSend.getFieldValue(StorageLocationMeta.NAME)).trim());
		newStorageLocDO.setParentStorageLocationId((Integer)rpcSend.getFieldValue(StorageLocationChildMeta.ID));
		newStorageLocDO.setStorageUnitId((Integer)rpcSend.getFieldValue(StorageLocationStorageUnitMeta.DESCRIPTION));
        newStorageLocDO.setStorageUnit((String)((DropDownField)rpcSend.getField(StorageLocationStorageUnitMeta.DESCRIPTION)).getTextValue());
		
		return newStorageLocDO;
	}
	
	private List getChildStorageLocsFromRPC(TableModel childTable){
		List storageLocationChildren = new ArrayList();
		
		for(int i=0; i<childTable.numRows(); i++){
			StorageLocationDO childDO = new StorageLocationDO();
			TableRow row = childTable.getRow(i);
			
			//parent data
			NumberField id = (NumberField)row.getHidden("id");
			StringField deleteFlag = (StringField)row.getHidden("deleteFlag");
			if(deleteFlag == null){
				childDO.setDelete(false);
			}else{
				childDO.setDelete("Y".equals(deleteFlag.getValue()));
			}
				//if the user created the row and clicked the remove button before commit...
				//we dont need to do anything with that row
				if(deleteFlag != null && "Y".equals(deleteFlag.getValue()) && id == null){
					//do nothing
				}else{
				
				if(id != null)
					childDO.setId((Integer)id.getValue());
				
				childDO.setStorageUnitId((Integer)((DropDownField)row.getColumn(0)).getValue());
				childDO.setLocation(((String)((StringField)row.getColumn(1)).getValue()).trim());
				childDO.setIsAvailable(((String)((CheckField)row.getColumn(2)).getValue()));
					
				storageLocationChildren.add(childDO);	
			}
		}
		return storageLocationChildren;
	}
	
	public String getXML() throws RPCException {
		return ServiceUtils.getXML(Constants.APP_ROOT+"/Forms/storageLocation.xsl");
	}

    public DataObject[] getXMLData() throws RPCException {
        StringObject xml = new StringObject();
        xml.setValue(ServiceUtils.getXML(Constants.APP_ROOT+"/Forms/storageLocation.xsl"));
        DataModel model = new DataModel();
        ModelField data = new ModelField();
        data.setValue(model);
        return new DataObject[] {xml,data};
    }
    
    public BooleanObject validateUniqueName(StringObject name){
		StorageLocationRemote remote = (StorageLocationRemote)EJBFactory.lookup("openelis/StorageLocationBean/remote");
		
		BooleanObject returnObject = new BooleanObject();
		returnObject.setValue(remote.getStorageLocByName((String)name.getValue()) != null);
    	return returnObject;
    }
    
	public DataModel getDisplay(String cat, DataModel model, AbstractField value) {
		if(cat.equals("storageUnit"))
			return getStorageUnitDisplay((Integer)value.getValue());
		else if(cat.equals("parentStorageLoc"))
			return getParentStorageLocDisplay((Integer)value.getValue());
		
		//return null if cat doesnt match
		return null;
	}
	
	private DataModel getStorageUnitDisplay(Integer value){
		StorageUnitRemote remote = (StorageUnitRemote)EJBFactory.lookup("openelis/StorageUnitBean/remote");
		
		Object[] result  = (Object[])remote.autoCompleteLookupById(value); 

		//id
		Integer suId = (Integer)result[0];
		//desc
		String desc = (String)result[1];
		if(desc != null)
			desc = desc.trim();
		
		DataModel model = new DataModel();
		DataSet data = new DataSet();
		
		NumberObject id = new NumberObject(NumberObject.INTEGER);
		id.setValue(suId);
		StringObject nameObject = new StringObject();
		nameObject.setValue(desc);
		
		data.addObject(id);
		data.addObject(nameObject);
		
		model.add(data);
		
		return model;		
	}
	
	private DataModel getParentStorageLocDisplay(Integer value){
		StorageLocationRemote remote = (StorageLocationRemote)EJBFactory.lookup("openelis/StorageLocationBean/remote");

		Object[] result  = (Object[])remote.autoCompleteLookupById(value); 
	
		//id
		Integer suId = (Integer)result[0];
		//name
		String name = (String)result[1];
		
		if(name != null)
			name = name.trim();
		
		DataModel model = new DataModel();
		DataSet data = new DataSet();
		
		NumberObject id = new NumberObject(NumberObject.INTEGER);
		id.setValue(suId);
		StringObject nameObject = new StringObject();
		nameObject.setValue(name);
		
		data.addObject(id);
		data.addObject(nameObject);
		
		model.add(data);

		return model;		
	}

	public DataModel getInitialModel(String cat) {
		return null;
	}

	public DataModel getMatches(String cat, DataModel model, String match) {
		if(cat.equals("storageUnit"))
			return getStorageUnitMatches(match);
		else if(cat.equals("parentStorageLoc"))
			return getParentStorageLocMatches(match);
		
		//return null if cat doesnt match
		return null;
	}
	
	private DataModel getStorageUnitMatches(String match){
		StorageUnitRemote remote = (StorageUnitRemote)EJBFactory.lookup("openelis/StorageUnitBean/remote");
		DataModel dataModel = new DataModel();

		try{
			int id = Integer.parseInt(match); //this will throw an exception if it isnt an id
//			lookup by id...should only bring back 1 result
			Object[] result  = (Object[])remote.autoCompleteLookupById(id); 
			if(result[0] != null){
//				id
				Integer slId = (Integer)result[0];
				//desc
				String desc = (String)result[1];
				if(desc != null)
					desc = desc.trim();
				//category
				String category = (String)result[2];
				if(category != null)
					category = category.trim();
			
				DataSet data = new DataSet();
				//hidden id
				NumberObject idObject = new NumberObject(NumberObject.INTEGER);
				idObject.setValue(slId);
				data.setKey(idObject);
				//columns
				StringObject descObject = new StringObject();
				descObject.setValue(desc);
				data.addObject(descObject);
				StringObject categoryObject = new StringObject();
				categoryObject.setValue(category);
				data.addObject(categoryObject);
			
				//add the dataset to the datamodel
				dataModel.add(data);
			}			
		}catch(NumberFormatException e){
			//it isnt an id
			//lookup by name
			
			List autoCompleteList = remote.autoCompleteLookupByDescription(match+"%", 10);
			Iterator itr = autoCompleteList.iterator();
			
			while(itr.hasNext()){
				Object[] result = (Object[]) itr.next();
				//id
				Integer id = (Integer)result[0];
				//desc
				String desc = (String)result[1];
				if(desc != null)
					desc = desc.trim();
				//category
				String category = (String)result[2];
				if(category != null)
					category = category.trim();
				
				DataSet data = new DataSet();
				//hidden id
				NumberObject idObject = new NumberObject(NumberObject.INTEGER);
				idObject.setValue(id);
				data.setKey(idObject);
				//columns
				StringObject descObject = new StringObject();
				descObject.setValue(desc);
				data.addObject(descObject);
				StringObject categoryObject = new StringObject();
				categoryObject.setValue(category);
				data.addObject(categoryObject);
				
				//add the dataset to the datamodel
				dataModel.add(data);
			}
			
		}
		return dataModel;
	}
	
	private DataModel getParentStorageLocMatches(String match){
		StorageLocationRemote remote = (StorageLocationRemote)EJBFactory.lookup("openelis/StorageLocationBean/remote");
		DataModel dataModel = new DataModel();

		try{
			int id = Integer.parseInt(match); //this will throw an exception if it isnt an id
			
//			lookup by id...should only bring back 1 result
			Object[] result  = (Object[])remote.autoCompleteLookupById(id); 
			if(result[0] != null){
//				id
				Integer pslId = (Integer)result[0];
				//name
				String name = (String)result[1];
				//location
				String location = (String)result[2];
				
				DataSet data = new DataSet();
//				hidden id
				NumberObject idObject = new NumberObject(NumberObject.INTEGER);
				idObject.setValue(pslId);
				data.setKey(idObject);
				//columns
				StringObject nameObject = new StringObject();
				nameObject.setValue(name.trim());
				data.addObject(nameObject);
				StringObject locationObject = new StringObject();
				locationObject.setValue(location.trim());
				data.addObject(locationObject);
				
				//add the dataset to the datamodel
				dataModel.add(data);
			}
			
		}catch(NumberFormatException e){
			//it isnt an id
			//lookup by name
			
			List autoCompleteList = remote.autoCompleteLookupByName(match+"%", 10);
			Iterator itr = autoCompleteList.iterator();
			
			while(itr.hasNext()){
				Object[] result = (Object[]) itr.next();
				//id
				Integer id = (Integer)result[0];
				//name
				String name = (String)result[1];
				//location
				String location = (String)result[2];
				
				DataSet data = new DataSet();
				//hidden id
				NumberObject idObject = new NumberObject(NumberObject.INTEGER);
				idObject.setValue(id);
				data.setKey(idObject);
				//columns
				StringObject idStringObject = new StringObject();
				idStringObject.setValue(String.valueOf(id));
				data.addObject(idStringObject);
				StringObject nameObject = new StringObject();
				nameObject.setValue(name.trim());
				data.addObject(nameObject);
				StringObject locationObject = new StringObject();
				locationObject.setValue(location.trim());
				data.addObject(locationObject);
				//display text
				StringObject displayObject = new StringObject();
				displayObject.setValue(name.trim());
				data.addObject(displayObject);
				//selected flag
				StringObject selectedFlag = new StringObject();
				selectedFlag.setValue("N");
				data.addObject(selectedFlag);
				
				//add the dataset to the datamodel
				dataModel.add(data);
			}
			
		}
		return dataModel;
	}
	
	public TableModel fillChildrenTable(TableModel childModel, List childrenList){
		try 
        {
			childModel.reset();
			
			for(int iter = 0;iter < childrenList.size();iter++) {
				StorageLocationDO slDO = (StorageLocationDO)childrenList.get(iter);

	               TableRow row = childModel.createRow();
	               
	               NumberField id = new NumberField(NumberObject.INTEGER);
	               id.setValue(slDO.getId());
	               
	               row.addHidden("id", id);
	               
//		       		we need to create a dataset for the storage unit auto complete
		       		if(slDO.getStorageUnitId() == null)
		       			row.getColumn(0).setValue(null);
		       		else{
		       			DataSet storageUnitSet = new DataSet();
		       			NumberObject storageUnitId = new NumberObject(NumberObject.INTEGER);
		       			StringObject storageUnitText = new StringObject();
		       			storageUnitId.setValue(slDO.getStorageUnitId());
		       			storageUnitText.setValue(slDO.getStorageUnit().trim());
		       			storageUnitSet.setKey(storageUnitId);
		       			storageUnitSet.addObject(storageUnitText);
		       			row.getColumn(0).setValue(storageUnitSet);
		       		}
		       		
	               row.getColumn(1).setValue(slDO.getLocation());
	       		
	       		row.getColumn(2).setValue(slDO.getIsAvailable());
	                
	            childModel.addRow(row);
	       } 
			
        } catch (Exception e) {

            e.printStackTrace();
            return null;
        }		
		
		return childModel;
	}

	private void setRpcErrors(List exceptionList, TableModel contactsTable, FormRPC rpcSend){
    	//we need to get the keys and look them up in the resource bundle for internationalization
		for (int i=0; i<exceptionList.size();i++) {
			//if the error is inside the org contacts table
			if(exceptionList.get(i) instanceof TableFieldErrorException){
				TableRow row = contactsTable.getRow(((TableFieldErrorException)exceptionList.get(i)).getRowIndex());
				row.getColumn(contactsTable.getColumnIndexByFieldName(((TableFieldErrorException)exceptionList.get(i)).getFieldName()))
																		.addError(openElisConstants.getString(((FieldErrorException)exceptionList.get(i)).getMessage()));
			//if the error is on the field
			}else if(exceptionList.get(i) instanceof FieldErrorException)
				rpcSend.getField(((FieldErrorException)exceptionList.get(i)).getFieldName()).addError(openElisConstants.getString(((FieldErrorException)exceptionList.get(i)).getMessage()));
			//if the error is on the entire form
			else if(exceptionList.get(i) instanceof FormErrorException)
				rpcSend.addError(openElisConstants.getString(((FormErrorException)exceptionList.get(i)).getMessage()));
		}	
		rpcSend.status = IForm.INVALID_FORM;
    }
	
	public DataObject[] getXMLData(DataObject[] args) throws RPCException {
		// TODO Auto-generated method stub
		return null;
	}
}
