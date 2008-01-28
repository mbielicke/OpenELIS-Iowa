package org.openelis.server;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import org.openelis.client.supply.screen.storage.StorageLocationServletInt;
import org.openelis.domain.StorageLocationDO;
import org.openelis.gwt.client.services.AppScreenFormServiceInt;
import org.openelis.gwt.client.services.AutoCompleteServiceInt;
import org.openelis.gwt.common.FormRPC;
import org.openelis.gwt.common.LastPageException;
import org.openelis.gwt.common.QueryNotFoundException;
import org.openelis.gwt.common.RPCException;
import org.openelis.gwt.common.data.AbstractField;
import org.openelis.gwt.common.data.DataModel;
import org.openelis.gwt.common.data.DataSet;
import org.openelis.gwt.common.data.NumberObject;
import org.openelis.gwt.common.data.StringObject;
import org.openelis.gwt.server.AppServlet;
import org.openelis.gwt.server.ServiceUtils;
import org.openelis.persistence.CachingManager;
import org.openelis.persistence.EJBFactory;
import org.openelis.remote.StorageLocationRemote;
import org.openelis.remote.StorageUnitRemote;
import org.openelis.server.constants.Constants;
import org.openelis.server.constants.UTFResource;
import org.openelis.util.SessionManager;

public class StorageLocationServlet extends AppServlet implements AppScreenFormServiceInt, 
														  StorageLocationServletInt,
														  AutoCompleteServiceInt{

	private static final long serialVersionUID = -7614978840440946815L;
	private static final int leftTableRowsPerPage = 10;
	
	private String systemUserId = "";
	
	private UTFResource openElisConstants= UTFResource.getBundle("org.openelis.client.main.constants.OpenELISConstants",
			new Locale(((SessionManager.getSession() == null  || (String)SessionManager.getSession().getAttribute("locale") == null) 
					? "en" : (String)SessionManager.getSession().getAttribute("locale"))));

	public FormRPC abort(DataSet key, FormRPC rpcReturn) throws RPCException {
//		remote interface to call the storage location bean
		StorageLocationRemote remote = (StorageLocationRemote)EJBFactory.lookup("openelis/StorageLocationBean/remote");
		
		
		StorageLocationDO storageLocDO = remote.getStorageLoc((Integer)key.getObject(0).getValue(),true);

//		set the fields in the RPC
		rpcReturn.setFieldValue("id", storageLocDO.getId());
		rpcReturn.setFieldValue("isAvailable", "Y".equals(storageLocDO.getIsAvailable().trim()));
		rpcReturn.setFieldValue("location", storageLocDO.getLocation().trim());
		rpcReturn.setFieldValue("name", storageLocDO.getName().trim());
		rpcReturn.setFieldValue("parentStorageId", storageLocDO.getParentStorageLocation());
		rpcReturn.setFieldValue("sortOrderId", storageLocDO.getSortOrder());
		rpcReturn.setFieldValue("storageUnitId", storageLocDO.getStorageUnit());
        
		return rpcReturn;  
	}

	public FormRPC commitAdd(FormRPC rpcSend, FormRPC rpcReturn) throws RPCException {
//		remote interface to call the storageLocation bean
		StorageLocationRemote remote = (StorageLocationRemote)EJBFactory.lookup("openelis/StorageLocationBean/remote");
		StorageLocationDO newStorageLocDO = new StorageLocationDO();

//		build the storage unit DO from the form
		newStorageLocDO.setIsAvailable(((Boolean) rpcSend.getFieldValue("isAvailable")?"Y":"N"));
		newStorageLocDO.setLocation(((String)rpcSend.getFieldValue("location")).trim());
		newStorageLocDO.setName(((String)rpcSend.getFieldValue("name")).trim());
		newStorageLocDO.setParentStorageLocation((Integer)rpcSend.getFieldValue("parentStorageId"));
		newStorageLocDO.setSortOrder((Integer)rpcSend.getFieldValue("sortOrderId"));
		newStorageLocDO.setStorageUnit((Integer)rpcSend.getFieldValue("storageUnitId"));
		
		//send the changes to the database
		Integer storageLocId = (Integer)remote.updateStorageLoc(newStorageLocDO);
		
//		lookup the changes from the database and build the rpc
		StorageLocationDO storageDO = remote.getStorageLoc(storageLocId,false);

//		set the fields in the RPC
		rpcReturn.setFieldValue("id", storageDO.getId());
		rpcReturn.setFieldValue("isAvailable", "Y".equals(storageDO.getIsAvailable().trim()));
		rpcReturn.setFieldValue("location", storageDO.getLocation().trim());
		rpcReturn.setFieldValue("name", storageDO.getName().trim());
		rpcReturn.setFieldValue("parentStorageId", storageDO.getParentStorageLocation());
		rpcReturn.setFieldValue("sortOrderId", storageDO.getSortOrder());
		rpcReturn.setFieldValue("storageUnitId", storageDO.getStorageUnit());
		
		return rpcReturn;
	}

	public DataModel commitQuery(FormRPC rpcSend, DataModel model) throws RPCException {
//		if the rpc is null then we need to get the page
		if(rpcSend == null){
//			need to get the query rpc out of the cache
	        FormRPC rpc = (FormRPC)CachingManager.getElement("screenQueryRpc", systemUserId+":StorageUnit");

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
				NumberObject id = new NumberObject();
				StringObject name = new StringObject();
				id.setType("integer");
				name.setValue(nameResult);
				id.setValue(idResult);
				
				row.addObject(id);			
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

			 NumberObject idField = new NumberObject();
			 idField.setType("integer");
			 StringObject nameField = new StringObject();
			 nameField.setValue(name);
      
			 idField.setValue(id);
			 row.addObject(idField);
			 row.addObject(nameField);

			 model.add(row);
		}
        
        //need to save the rpc used to the encache
        if(systemUserId.equals(""))
        	systemUserId = remote.getSystemUserId().toString();
        CachingManager.putElement("screenQueryRpc", systemUserId+":StorageUnit", rpcSend);
		}
		
		return model;
	}

	public FormRPC commitUpdate(FormRPC rpcSend, FormRPC rpcReturn) throws RPCException {
//		remote interface to call the storage loc bean
		StorageLocationRemote remote = (StorageLocationRemote)EJBFactory.lookup("openelis/StorageLocationBean/remote");
		StorageLocationDO newStorageLocDO = new StorageLocationDO();

		//build the DO from the form
		newStorageLocDO.setId((Integer)rpcSend.getFieldValue("id"));
		newStorageLocDO.setIsAvailable(((Boolean) rpcSend.getFieldValue("isAvailable")?"Y":"N"));
		newStorageLocDO.setLocation(((String)rpcSend.getFieldValue("location")).trim());
		newStorageLocDO.setName(((String)rpcSend.getFieldValue("name")).trim());
		newStorageLocDO.setParentStorageLocation((Integer)rpcSend.getFieldValue("parentStorageId"));
		newStorageLocDO.setSortOrder((Integer)rpcSend.getFieldValue("sortOrderId"));
		newStorageLocDO.setStorageUnit((Integer)rpcSend.getFieldValue("storageUnitId"));

//		send the changes to the database
		remote.updateStorageLoc(newStorageLocDO);
		
		//lookup the changes from the database and build the rpc
		StorageLocationDO storageLocDO = remote.getStorageLoc(newStorageLocDO.getId(),false);

//		set the fields in the RPC
		rpcReturn.setFieldValue("id", storageLocDO.getId());
		rpcReturn.setFieldValue("isAvailable", "Y".equals(storageLocDO.getIsAvailable().trim()));
		rpcReturn.setFieldValue("location", storageLocDO.getLocation().trim());
		rpcReturn.setFieldValue("name", storageLocDO.getName().trim());
		rpcReturn.setFieldValue("parentStorageId", storageLocDO.getParentStorageLocation());
		rpcReturn.setFieldValue("sortOrderId", storageLocDO.getSortOrder());
		rpcReturn.setFieldValue("storageUnitId", storageLocDO.getStorageUnit());
		
		return rpcReturn;
	}

	public FormRPC commitDelete(DataSet key, FormRPC rpcReturn) throws RPCException {
		// TODO Auto-generated method stub
		return null;
	}

	public FormRPC fetch(DataSet key, FormRPC rpcReturn) throws RPCException {
//		remote interface to call the storage loc bean
		StorageLocationRemote remote = (StorageLocationRemote)EJBFactory.lookup("openelis/StorageLocationBean/remote");
		
		StorageLocationDO storageLocDO = remote.getStorageLoc((Integer)key.getObject(0).getValue(),false);
		
//		set the fields in the RPC
		rpcReturn.setFieldValue("id", storageLocDO.getId());
		rpcReturn.setFieldValue("isAvailable", "Y".equals(storageLocDO.getIsAvailable().trim()));
		rpcReturn.setFieldValue("location", storageLocDO.getLocation().trim());
		rpcReturn.setFieldValue("name", storageLocDO.getName().trim());
		rpcReturn.setFieldValue("parentStorageId", storageLocDO.getParentStorageLocation());
		rpcReturn.setFieldValue("sortOrderId", storageLocDO.getSortOrder());
		rpcReturn.setFieldValue("storageUnitId", storageLocDO.getStorageUnit());
		
		return rpcReturn;
	}

	public FormRPC fetchForUpdate(DataSet key, FormRPC rpcReturn) throws RPCException {
		return fetch(key, rpcReturn);
	}

	public String getXML() throws RPCException {
		return ServiceUtils.getXML(Constants.APP_ROOT+"/Forms/storageLocation.xsl");
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
		
		List autoCompleteList = remote.autoCompleteLookupById(value);
		
		Object[] result = (Object[]) autoCompleteList.get(0);
		//id
		Integer suId = (Integer)result[0];
		//desc
		String desc = (String)result[1];
		
		DataModel model = new DataModel();
		DataSet data = new DataSet();
		
		NumberObject id = new NumberObject();
		id.setType("integer");
		id.setValue(suId);
		StringObject nameObject = new StringObject();
		nameObject.setValue(desc.trim());
		
		data.addObject(id);
		data.addObject(nameObject);
		
		model.add(data);

		return model;		
	}
	
	private DataModel getParentStorageLocDisplay(Integer value){
		StorageLocationRemote remote = (StorageLocationRemote)EJBFactory.lookup("openelis/StorageLocationBean/remote");
		
		List autoCompleteList = remote.autoCompleteLookupById(value);
		
		Object[] result = (Object[]) autoCompleteList.get(0);
		//id
		Integer suId = (Integer)result[0];
		//name
		String name = (String)result[1];
		
		DataModel model = new DataModel();
		DataSet data = new DataSet();
		
		NumberObject id = new NumberObject();
		id.setType("integer");
		id.setValue(suId);
		StringObject nameObject = new StringObject();
		nameObject.setValue(name.trim());
		
		data.addObject(id);
		data.addObject(nameObject);
		
		model.add(data);

		return model;		
	}

	public DataModel getInitialModel(String cat) {
		// TODO Auto-generated method stub
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
				//category
				String category = (String)result[2];
				//is singular
				String isSingular = (String)result[3];	
				
				DataSet data = new DataSet();
				//hidden id
				NumberObject idObject = new NumberObject();
				idObject.setType("integer");
				idObject.setValue(id);
				data.addObject(idObject);
				//columns
				StringObject idStringObject = new StringObject();
				idStringObject.setValue(String.valueOf(id));
				data.addObject(idStringObject);
				StringObject descObject = new StringObject();
				descObject.setValue(desc.trim());
				data.addObject(descObject);
				StringObject categoryObject = new StringObject();
				categoryObject.setValue(category.trim());
				data.addObject(categoryObject);
				StringObject isSingularObject = new StringObject();
				isSingularObject.setValue(isSingular.trim());
				data.addObject(isSingularObject);
				//display text
				StringObject displayObject = new StringObject();
				displayObject.setValue(desc.trim());
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
	
	private DataModel getParentStorageLocMatches(String match){
		StorageLocationRemote remote = (StorageLocationRemote)EJBFactory.lookup("openelis/StorageLocationBean/remote");
		DataModel dataModel = new DataModel();

		try{
			int id = Integer.parseInt(match); //this will throw an exception if it isnt an id
			
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
				NumberObject idObject = new NumberObject();
				idObject.setType("integer");
				idObject.setValue(id);
				data.addObject(idObject);
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
}
