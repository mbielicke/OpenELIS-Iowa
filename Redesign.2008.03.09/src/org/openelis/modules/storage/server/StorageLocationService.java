package org.openelis.modules.storage.server;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import org.openelis.domain.StorageLocationDO;
import org.openelis.gwt.common.FormRPC;
import org.openelis.gwt.common.LastPageException;
import org.openelis.gwt.common.QueryNotFoundException;
import org.openelis.gwt.common.RPCDeleteException;
import org.openelis.gwt.common.RPCException;
import org.openelis.gwt.common.data.AbstractField;
import org.openelis.gwt.common.data.CheckField;
import org.openelis.gwt.common.data.CollectionField;
import org.openelis.gwt.common.data.DataModel;
import org.openelis.gwt.common.data.DataObject;
import org.openelis.gwt.common.data.DataSet;
import org.openelis.gwt.common.data.ModelField;
import org.openelis.gwt.common.data.NumberField;
import org.openelis.gwt.common.data.NumberObject;
import org.openelis.gwt.common.data.QueryStringField;
import org.openelis.gwt.common.data.StringField;
import org.openelis.gwt.common.data.StringObject;
import org.openelis.gwt.common.data.TableModel;
import org.openelis.gwt.common.data.TableRow;
import org.openelis.gwt.server.ServiceUtils;
import org.openelis.gwt.services.AppScreenFormServiceInt;
import org.openelis.gwt.services.AutoCompleteServiceInt;
import org.openelis.persistence.CachingManager;
import org.openelis.persistence.EJBFactory;
import org.openelis.remote.StorageLocationRemote;
import org.openelis.remote.StorageUnitRemote;
import org.openelis.server.constants.Constants;
import org.openelis.server.constants.UTFResource;
import org.openelis.util.SessionManager;

public class StorageLocationService implements AppScreenFormServiceInt, 
														  
														  AutoCompleteServiceInt{

	private static final long serialVersionUID = -7614978840440946815L;
	private static final int leftTableRowsPerPage = 10;
	
	private UTFResource openElisConstants= UTFResource.getBundle("org.openelis.modules.main.server.constants.OpenELISConstants",
			new Locale(((SessionManager.getSession() == null  || (String)SessionManager.getSession().getAttribute("locale") == null) 
					? "en" : (String)SessionManager.getSession().getAttribute("locale"))));

	public FormRPC abort(DataSet key, FormRPC rpcReturn) throws RPCException {
//		remote interface to call the storage location bean
		StorageLocationRemote remote = (StorageLocationRemote)EJBFactory.lookup("openelis/StorageLocationBean/remote");
		
		
		StorageLocationDO storageLocDO = remote.getStorageLocAndUnlock((Integer)key.getObject(0).getValue());

//		set the fields in the RPC
		rpcReturn.setFieldValue("id", storageLocDO.getId());
		rpcReturn.setFieldValue("isAvailable", "Y".equals(storageLocDO.getIsAvailable().trim()));
		rpcReturn.setFieldValue("location", storageLocDO.getLocation().trim());
		rpcReturn.setFieldValue("name", storageLocDO.getName().trim());
		rpcReturn.setFieldValue("parentStorageId", storageLocDO.getParentStorageLocation());
		//rpcReturn.setFieldValue("sortOrderId", storageLocDO.getSortOrder());
		rpcReturn.setFieldValue("storageUnitId", storageLocDO.getStorageUnit());
        
//		load the children
        List childrenList = remote.getStorageLocChildren((Integer)key.getObject(0).getValue(),false);
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
		newStorageLocDO.setIsAvailable(((Boolean) rpcSend.getFieldValue("isAvailable")?"Y":"N"));
		newStorageLocDO.setLocation(((String)rpcSend.getFieldValue("location")).trim());
		newStorageLocDO.setName(((String)rpcSend.getFieldValue("name")).trim());
		newStorageLocDO.setParentStorageLocation((Integer)rpcSend.getFieldValue("parentStorageId"));
		//newStorageLocDO.setSortOrder((Integer)rpcSend.getFieldValue("sortOrderId"));
		newStorageLocDO.setStorageUnit((Integer)rpcSend.getFieldValue("storageUnitId"));
		
//		child locs info
		TableModel childTable = (TableModel)rpcSend.getField("childStorageLocsTable").getValue();
		System.out.println("TABLE SIZE: "+childTable.numRows());
		for(int i=0; i<childTable.numRows(); i++){
			StorageLocationDO childDO = new StorageLocationDO();
			TableRow row = childTable.getRow(i);
			childDO.setName(((String)((StringField)row.getColumn(0)).getValue()).trim());
			childDO.setLocation(((String)((StringField)row.getColumn(1)).getValue()).trim());
			childDO.setStorageUnit((Integer)((NumberField)row.getColumn(2)).getValue());
			childDO.setIsAvailable(((Boolean)((CheckField)row.getColumn(3)).getValue()?"Y":"N"));
			
			storageLocationChildren.add(childDO);
		}
				
		//send the changes to the database
		Integer storageLocId = (Integer)remote.updateStorageLoc(newStorageLocDO, storageLocationChildren);
		
//		lookup the changes from the database and build the rpc
		StorageLocationDO storageDO = remote.getStorageLoc(storageLocId);

//		set the fields in the RPC
		rpcReturn.setFieldValue("id", storageDO.getId());
		rpcReturn.setFieldValue("isAvailable", "Y".equals(storageDO.getIsAvailable().trim()));
		rpcReturn.setFieldValue("location", storageDO.getLocation().trim());
		rpcReturn.setFieldValue("name", storageDO.getName().trim());
		rpcReturn.setFieldValue("parentStorageId", storageDO.getParentStorageLocation());
		//rpcReturn.setFieldValue("sortOrderId", storageDO.getSortOrder());
		rpcReturn.setFieldValue("storageUnitId", storageDO.getStorageUnit());
		
//		load the children
        List childrenList = remote.getStorageLocChildren(storageLocId,false);
        //need to build the children table now...
        TableModel rmodel = (TableModel)fillChildrenTable((TableModel)rpcReturn.getField("childStorageLocsTable").getValue(),childrenList);
        rpcReturn.setFieldValue("childStorageLocsTable",rmodel);
		return rpcReturn;
	}

	public DataModel commitQuery(FormRPC rpcSend, DataModel model) throws RPCException {
//		if the rpc is null then we need to get the page
		if(rpcSend == null){
//			need to get the query rpc out of the cache
	        FormRPC rpc = (FormRPC)CachingManager.getElement("screenQueryRpc", SessionManager.getSession().getAttribute("systemUserId")+":StorageUnit");

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
			
//			children table
			TableModel childrenTable = null;
			if(rpcSend.getField("childStorageLocsTable") != null)
				childrenTable = (TableModel)rpcSend.getField("childStorageLocsTable").getValue();		
			
			if(childrenTable != null){
				fields.put("childName",(QueryStringField)childrenTable.getRow(0).getColumn(0));
				fields.put("childLocation",(QueryStringField)childrenTable.getRow(0).getColumn(1));
				fields.put("childStorageUnit",(QueryStringField)childrenTable.getRow(0).getColumn(2));
				fields.put("childIsAvailable",(CollectionField)childrenTable.getRow(0).getColumn(3));
			}
			
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
        if(SessionManager.getSession().getAttribute("systemUserId") == null)
        	SessionManager.getSession().setAttribute("systemUserId", remote.getSystemUserId().toString());
        CachingManager.putElement("screenQueryRpc", SessionManager.getSession().getAttribute("systemUserId")+":StorageUnit", rpcSend);
		}
		
		return model;
	}

	public FormRPC commitUpdate(FormRPC rpcSend, FormRPC rpcReturn) throws RPCException {
//		remote interface to call the storage loc bean
		StorageLocationRemote remote = (StorageLocationRemote)EJBFactory.lookup("openelis/StorageLocationBean/remote");
		StorageLocationDO newStorageLocDO = new StorageLocationDO();
		List storageLocationChildren = new ArrayList();
		
		//build the DO from the form
		newStorageLocDO.setId((Integer)rpcSend.getFieldValue("id"));
		newStorageLocDO.setIsAvailable(((Boolean) rpcSend.getFieldValue("isAvailable")?"Y":"N"));
		newStorageLocDO.setLocation(((String)rpcSend.getFieldValue("location")).trim());
		newStorageLocDO.setName(((String)rpcSend.getFieldValue("name")).trim());
		newStorageLocDO.setParentStorageLocation((Integer)rpcSend.getFieldValue("parentStorageId"));
		//newStorageLocDO.setSortOrder((Integer)rpcSend.getFieldValue("sortOrderId"));
		newStorageLocDO.setStorageUnit((Integer)rpcSend.getFieldValue("storageUnitId"));

//		children info
		TableModel childTable = (TableModel)rpcSend.getField("childStorageLocsTable").getValue();
		for(int i=0; i<childTable.numRows(); i++){
			StorageLocationDO childDO = new StorageLocationDO();
			TableRow row = childTable.getRow(i);
			
			//contact data
			NumberField id = (NumberField)row.getHidden("id");
			
			if(id != null)
				childDO.setId((Integer)id.getValue());
			
			childDO.setName(((String)((StringField)row.getColumn(0)).getValue()).trim());
			childDO.setLocation(((String)((StringField)row.getColumn(1)).getValue()).trim());
			childDO.setStorageUnit((Integer)((NumberField)row.getColumn(2)).getValue());
			childDO.setIsAvailable(((Boolean)((CheckField)row.getColumn(3)).getValue()?"Y":"N"));
				
			storageLocationChildren.add(childDO);	
		}
		
//		send the changes to the database
		remote.updateStorageLoc(newStorageLocDO, storageLocationChildren);
		
		//lookup the changes from the database and build the rpc
		StorageLocationDO storageLocDO = remote.getStorageLoc(newStorageLocDO.getId());

//		set the fields in the RPC
		rpcReturn.setFieldValue("id", storageLocDO.getId());
		rpcReturn.setFieldValue("isAvailable", "Y".equals(storageLocDO.getIsAvailable().trim()));
		rpcReturn.setFieldValue("location", storageLocDO.getLocation().trim());
		rpcReturn.setFieldValue("name", storageLocDO.getName().trim());
		rpcReturn.setFieldValue("parentStorageId", storageLocDO.getParentStorageLocation());
		//rpcReturn.setFieldValue("sortOrderId", storageLocDO.getSortOrder());
		rpcReturn.setFieldValue("storageUnitId", storageLocDO.getStorageUnit());
		
//		load the children
        List childrenList = remote.getStorageLocChildren(storageLocDO.getId(),false);
        //need to build the children table now...
        TableModel rmodel = (TableModel)fillChildrenTable((TableModel)rpcReturn.getField("childStorageLocsTable").getValue(),childrenList);
        rpcReturn.setFieldValue("childStorageLocsTable",rmodel);
        
		return rpcReturn;
	}

	public FormRPC commitDelete(DataSet key, FormRPC rpcReturn) throws RPCException {
//		remote interface to call the storage location bean
		StorageLocationRemote remote = (StorageLocationRemote)EJBFactory.lookup("openelis/StorageLocationBean/remote");
		
		try {
			remote.deleteStorageLoc((Integer)key.getObject(0).getValue());
			
		} catch (Exception e) {
			if(e instanceof RPCDeleteException){
				throw new RPCDeleteException(openElisConstants.getString("storageLocDeleteException"));
			}else
			throw new RPCException(e.getMessage());
		}	
		
		rpcReturn.setFieldValue("id", null);
		rpcReturn.setFieldValue("isAvailable", null);
		rpcReturn.setFieldValue("location", null);
		rpcReturn.setFieldValue("name", null);
		rpcReturn.setFieldValue("parentStorageId", null);
		//rpcReturn.setFieldValue("sortOrderId", null);
		rpcReturn.setFieldValue("storageUnitId", null);
		rpcReturn.setFieldValue("childStorageLocsTable", null);
		
		return rpcReturn;
	}

	public FormRPC fetch(DataSet key, FormRPC rpcReturn) throws RPCException {
//		remote interface to call the storage loc bean
		StorageLocationRemote remote = (StorageLocationRemote)EJBFactory.lookup("openelis/StorageLocationBean/remote");
		
		StorageLocationDO storageLocDO = remote.getStorageLoc((Integer)key.getObject(0).getValue());
		
//		set the fields in the RPC
		rpcReturn.setFieldValue("id", storageLocDO.getId());
		rpcReturn.setFieldValue("isAvailable", "Y".equals(storageLocDO.getIsAvailable().trim()));
		rpcReturn.setFieldValue("location", storageLocDO.getLocation().trim());
		rpcReturn.setFieldValue("name", storageLocDO.getName().trim());
		rpcReturn.setFieldValue("parentStorageId", storageLocDO.getParentStorageLocation());
		rpcReturn.setFieldValue("storageUnitId", storageLocDO.getStorageUnit());
		
//		load the children
        List childrenList = remote.getStorageLocChildren((Integer)key.getObject(0).getValue(),false);
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
			storageLocDO = remote.getStorageLocAndLock((Integer)key.getObject(0).getValue());
		}catch(Exception e){
			throw new RPCException(e.getMessage());
		}
		
//		set the fields in the RPC
		rpcReturn.setFieldValue("id", storageLocDO.getId());
		rpcReturn.setFieldValue("isAvailable", "Y".equals(storageLocDO.getIsAvailable().trim()));
		rpcReturn.setFieldValue("location", storageLocDO.getLocation().trim());
		rpcReturn.setFieldValue("name", storageLocDO.getName().trim());
		rpcReturn.setFieldValue("parentStorageId", storageLocDO.getParentStorageLocation());
		rpcReturn.setFieldValue("storageUnitId", storageLocDO.getStorageUnit());
		
//		load the children
        List childrenList = remote.getStorageLocChildren((Integer)key.getObject(0).getValue(),false);
        //need to build the children table now...
        TableModel rmodel = (TableModel)fillChildrenTable((TableModel)rpcReturn.getField("childStorageLocsTable").getValue(),childrenList);
        rpcReturn.setFieldValue("childStorageLocsTable",rmodel);

		return rpcReturn;
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
		
		NumberObject id = new NumberObject();
		id.setType("integer");
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
		
		NumberObject id = new NumberObject();
		id.setType("integer");
		id.setValue(suId);
		StringObject nameObject = new StringObject();
		nameObject.setValue(name);
		
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
				//is singular
				String isSingular = (String)result[3];
				if(isSingular != null)
					isSingular = isSingular.trim();
				
				DataSet data = new DataSet();
				//hidden id
				NumberObject idObject = new NumberObject();
				idObject.setType("integer");
				idObject.setValue(slId);
				data.addObject(idObject);
				//columns
				StringObject idStringObject = new StringObject();
				idStringObject.setValue(String.valueOf(slId));
				data.addObject(idStringObject);
				StringObject descObject = new StringObject();
				descObject.setValue(desc);
				data.addObject(descObject);
				StringObject categoryObject = new StringObject();
				categoryObject.setValue(category);
				data.addObject(categoryObject);
				StringObject isSingularObject = new StringObject();
				isSingularObject.setValue(isSingular);
				data.addObject(isSingularObject);
				
				//display text
				StringObject displayObject = new StringObject();
				displayObject.setValue(desc);
				data.addObject(displayObject);
				//selected flag
				StringObject selectedFlag = new StringObject();
				selectedFlag.setValue("N");
				data.addObject(selectedFlag);
				
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
				//is singular
				String isSingular = (String)result[3];
				if(isSingular != null)
					isSingular = isSingular.trim();
				
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
				descObject.setValue(desc);
				data.addObject(descObject);
				StringObject categoryObject = new StringObject();
				categoryObject.setValue(category);
				data.addObject(categoryObject);
				StringObject isSingularObject = new StringObject();
				isSingularObject.setValue(isSingular);
				data.addObject(isSingularObject);
				//display text
				StringObject displayObject = new StringObject();
				displayObject.setValue(desc);
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
				NumberObject idObject = new NumberObject();
				idObject.setType("integer");
				idObject.setValue(pslId);
				data.addObject(idObject);
				//columns
				StringObject idStringObject = new StringObject();
				idStringObject.setValue(String.valueOf(pslId));
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
	
	public TableModel fillChildrenTable(TableModel childModel, List childrenList){
		try 
        {
			childModel.reset();
			
			for(int iter = 0;iter < childrenList.size();iter++) {
				StorageLocationDO slDO = (StorageLocationDO)childrenList.get(iter);

	               TableRow row = childModel.createRow();
	               
	               NumberField id = new NumberField();
	               id.setType("integer");
	               id.setValue(slDO.getId());
	               
	               row.addHidden("id", id);
	               row.getColumn(0).setValue(slDO.getName());
	               row.getColumn(1).setValue(slDO.getLocation());
	               row.getColumn(2).setValue(slDO.getStorageUnit());
	               row.getColumn(3).setValue(slDO.getIsAvailable());
	                
	               childModel.addRow(row);
	       } 
			
        } catch (Exception e) {

            e.printStackTrace();
            return null;
        }		
		
		return childModel;
	}
}
