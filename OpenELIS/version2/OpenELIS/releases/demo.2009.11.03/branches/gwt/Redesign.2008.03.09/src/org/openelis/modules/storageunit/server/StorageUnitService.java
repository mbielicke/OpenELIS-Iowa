package org.openelis.modules.storageunit.server;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import org.openelis.domain.StorageUnitDO;
import org.openelis.gwt.common.FormRPC;
import org.openelis.gwt.common.LastPageException;
import org.openelis.gwt.common.QueryNotFoundException;
import org.openelis.gwt.common.RPCDeleteException;
import org.openelis.gwt.common.RPCException;
import org.openelis.gwt.common.data.AbstractField;
import org.openelis.gwt.common.data.BooleanObject;
import org.openelis.gwt.common.data.DataModel;
import org.openelis.gwt.common.data.DataObject;
import org.openelis.gwt.common.data.DataSet;
import org.openelis.gwt.common.data.ModelField;
import org.openelis.gwt.common.data.NumberObject;
import org.openelis.gwt.common.data.StringObject;
import org.openelis.gwt.server.ServiceUtils;
import org.openelis.gwt.services.AppScreenFormServiceInt;
import org.openelis.gwt.services.AutoCompleteServiceInt;
import org.openelis.persistence.CachingManager;
import org.openelis.persistence.EJBFactory;
import org.openelis.remote.CategoryRemote;
import org.openelis.remote.StorageUnitRemote;
import org.openelis.server.constants.Constants;
import org.openelis.server.constants.UTFResource;
import org.openelis.util.SessionManager;

public class StorageUnitService implements AppScreenFormServiceInt,
														  AutoCompleteServiceInt{

	private static final long serialVersionUID = -7614978840440946815L;
	private static final int leftTableRowsPerPage = 10;
	
	private UTFResource openElisConstants= UTFResource.getBundle("org.openelis.modules.main.server.constants.OpenELISConstants",
			new Locale(((SessionManager.getSession() == null  || (String)SessionManager.getSession().getAttribute("locale") == null) 
					? "en" : (String)SessionManager.getSession().getAttribute("locale"))));

	public FormRPC abort(DataSet key, FormRPC rpcReturn) throws RPCException {
//		remote interface to call the storage unit bean
		StorageUnitRemote remote = (StorageUnitRemote)EJBFactory.lookup("openelis/StorageUnitBean/remote");
		
		
		StorageUnitDO storageUnitDO = remote.getStorageUnitAndUnlock((Integer)key.getObject(0).getValue());

//		set the fields in the RPC
		rpcReturn.setFieldValue("id", storageUnitDO.getId());
		rpcReturn.setFieldValue("categoryId", storageUnitDO.getCategory().trim());
		rpcReturn.setFieldValue("description", storageUnitDO.getDescription().trim());
		rpcReturn.setFieldValue("isSingular", ("Y".equals(storageUnitDO.getIsSingular())));
        
      return rpcReturn;  
	}

	public FormRPC commitAdd(FormRPC rpcSend, FormRPC rpcReturn) throws RPCException {
//		remote interface to call the storageunit bean
		StorageUnitRemote remote = (StorageUnitRemote)EJBFactory.lookup("openelis/StorageUnitBean/remote");
		StorageUnitDO newStorageUnitDO = new StorageUnitDO();

//		build the storage unit DO from the form
		newStorageUnitDO.setCategory(((String)rpcSend.getFieldValue("categoryId")).trim());
		newStorageUnitDO.setDescription(((String)rpcSend.getFieldValue("description")).trim());
		newStorageUnitDO.setIsSingular(((Boolean) rpcSend.getFieldValue("isSingular")?"Y":"N"));
		
		//send the changes to the database
		Integer storageUnitId = (Integer)remote.updateStorageUnit(newStorageUnitDO);
		
//		lookup the changes from the database and build the rpc
		StorageUnitDO storageDO = remote.getStorageUnit(storageUnitId);

//		set the fields in the RPC
		rpcReturn.setFieldValue("id", storageDO.getId());
		rpcReturn.setFieldValue("categoryId", storageDO.getCategory().trim());
		rpcReturn.setFieldValue("description", storageDO.getDescription().trim());
		rpcReturn.setFieldValue("isSingular", ("Y".equals(storageDO.getIsSingular())));
		
		return rpcReturn;
	}

	public DataModel commitQuery(FormRPC rpcSend, DataModel model) throws RPCException {
//		if the rpc is null then we need to get the page
		if(rpcSend == null){
//			need to get the query rpc out of the cache
	        FormRPC rpc = (FormRPC)CachingManager.getElement("screenQueryRpc", SessionManager.getSession().getAttribute("systemUserId")+":StorageUnit");

	        if(rpc == null)
	        	throw new QueryNotFoundException(openElisConstants.getString("queryExpiredException"));
			
	        List storageUnits = null;
	        StorageUnitRemote remote = (StorageUnitRemote)EJBFactory.lookup("openelis/StorageUnitBean/remote");
	        try{
	        	storageUnits = remote.query(rpc.getFieldMap(), (model.getPage()*leftTableRowsPerPage), leftTableRowsPerPage+1);
	        }catch(Exception e){
	        	if(e instanceof LastPageException){
	        		throw new LastPageException(openElisConstants.getString("lastPageException"));
	        	}else{
	        		throw new RPCException(e.getMessage());	
	        	}
	        }
	        
	        int i=0;
	        model.clear();
	        while(i < storageUnits.size() && i < leftTableRowsPerPage) {
	    	   	Object[] result = (Object[])storageUnits.get(i);
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
			StorageUnitRemote remote = (StorageUnitRemote)EJBFactory.lookup("openelis/StorageUnitBean/remote");
			
			HashMap<String,AbstractField> fields = rpcSend.getFieldMap();
			
			List storageUnitNames = new ArrayList();
			try{
				storageUnitNames = remote.query(fields,0,leftTableRowsPerPage);

		}catch(Exception e){
			throw new RPCException(e.getMessage());
		}
		
		Iterator namesItr = storageUnitNames.iterator();
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
//		remote interface to call the storage unit bean
		StorageUnitRemote remote = (StorageUnitRemote)EJBFactory.lookup("openelis/StorageUnitBean/remote");
		StorageUnitDO newStorageUnitDO = new StorageUnitDO();

		//build the DO from the form
		newStorageUnitDO.setId((Integer)rpcSend.getFieldValue("id"));
		newStorageUnitDO.setCategory(((String)rpcSend.getFieldValue("categoryId")).trim());
		newStorageUnitDO.setDescription(((String)rpcSend.getFieldValue("description")).trim());
		newStorageUnitDO.setIsSingular(((Boolean) rpcSend.getFieldValue("isSingular")?"Y":"N"));

//		send the changes to the database
		remote.updateStorageUnit(newStorageUnitDO);
		
		//lookup the changes from the database and build the rpc
		StorageUnitDO storageUnitDO = remote.getStorageUnit(newStorageUnitDO.getId());

//		set the fields in the RPC
		rpcReturn.setFieldValue("id", storageUnitDO.getId());
		rpcReturn.setFieldValue("categoryId", storageUnitDO.getCategory().trim());
		rpcReturn.setFieldValue("description", storageUnitDO.getDescription().trim());
		rpcReturn.setFieldValue("isSingular", "Y".equals(storageUnitDO.getIsSingular().trim()));
		
		return rpcReturn;
	}

	public FormRPC commitDelete(DataSet key, FormRPC rpcReturn) throws RPCException {
//		remote interface to call the storage unit bean
		StorageUnitRemote remote = (StorageUnitRemote)EJBFactory.lookup("openelis/StorageUnitBean/remote");
		
		try {
			remote.deleteStorageUnit((Integer)key.getObject(0).getValue());
			
		} catch (Exception e) {
			if(e instanceof RPCDeleteException){
				throw new RPCDeleteException(openElisConstants.getString("storageUnitDeleteException"));
			}else
				throw new RPCException(e.getMessage());
		}	
		
		rpcReturn.setFieldValue("id", null);
		rpcReturn.setFieldValue("categoryId", null);
		rpcReturn.setFieldValue("description", null);
		rpcReturn.setFieldValue("isSingular", null);
		
		return rpcReturn;
	}

	public FormRPC fetch(DataSet key, FormRPC rpcReturn) throws RPCException {
//		remote interface to call the storage unit bean
		StorageUnitRemote remote = (StorageUnitRemote)EJBFactory.lookup("openelis/StorageUnitBean/remote");
		
		StorageUnitDO storageUnitDO = remote.getStorageUnit((Integer)key.getObject(0).getValue());
		
//		set the fields in the RPC
		rpcReturn.setFieldValue("id", storageUnitDO.getId());
		rpcReturn.setFieldValue("categoryId", storageUnitDO.getCategory().trim());
		rpcReturn.setFieldValue("description", storageUnitDO.getDescription().trim());
		rpcReturn.setFieldValue("isSingular", ("Y".equals(storageUnitDO.getIsSingular())));
		
		return rpcReturn;
	}

	public FormRPC fetchForUpdate(DataSet key, FormRPC rpcReturn) throws RPCException {
//		remote interface to call the storage unit bean
		StorageUnitRemote remote = (StorageUnitRemote)EJBFactory.lookup("openelis/StorageUnitBean/remote");
		
		StorageUnitDO storageUnitDO = new StorageUnitDO();
		try{
			storageUnitDO = remote.getStorageUnitAndLock((Integer)key.getObject(0).getValue());
		}catch(Exception e){
			throw new RPCException(e.getMessage());
		}
		
//		set the fields in the RPC
		rpcReturn.setFieldValue("id", storageUnitDO.getId());
		rpcReturn.setFieldValue("categoryId", storageUnitDO.getCategory().trim());
		rpcReturn.setFieldValue("description", storageUnitDO.getDescription().trim());
		rpcReturn.setFieldValue("isSingular", ("Y".equals(storageUnitDO.getIsSingular())));
		
		return rpcReturn;
	}

	public String getXML() throws RPCException {
		return ServiceUtils.getXML(Constants.APP_ROOT+"/Forms/storageUnit.xsl");
	}
    
    public DataObject[] getXMLData() throws RPCException {
        StringObject xml = new StringObject();
        xml.setValue(ServiceUtils.getXML(Constants.APP_ROOT+"/Forms/storageUnit.xsl"));
        
        DataModel storageUnitCategoryDropdownField = (DataModel)CachingManager.getElement("InitialData", "storageUnitCategoryDropdown");
        
        //storage unit category dropdown
        if(storageUnitCategoryDropdownField == null){
        	storageUnitCategoryDropdownField = getInitialModel("category");
        	CachingManager.putElement("InitialData", "storageUnitCategoryDropdown", storageUnitCategoryDropdownField);
        }
        
        return new DataObject[] {xml,storageUnitCategoryDropdownField};
    }

	public DataModel getDisplay(String cat, DataModel model, AbstractField value) {
		// TODO Auto-generated method stub
		return null;
	}

	public DataModel getInitialModel(String cat) {
		int id = -1;
		CategoryRemote remote = (CategoryRemote)EJBFactory.lookup("openelis/CategoryBean/remote");

		if(cat.equals("category")){
			id = remote.getCategoryId("storage_unit_category");
			//id = remote.getCategoryId("test");
		}

		List entries = new ArrayList();
		if(id > -1)
			entries = remote.getDropdownValues(id);
		
		//we need to build the model to return
		DataModel returnModel = new DataModel();
			
		//create a blank entry to begin the list
		DataSet blankset = new DataSet();
		
		StringObject blankStringId = new StringObject();
		BooleanObject blankSelected = new BooleanObject();
		
		blankStringId.setValue("");
		blankset.addObject(blankStringId);
		blankset.addObject(blankStringId);			
		
		blankSelected.setValue(new Boolean(false));
		blankset.addObject(blankSelected);
		
		returnModel.add(blankset);
		int i=0;
		while(i < entries.size()){
			DataSet set = new DataSet();
			Object[] result = (Object[]) entries.get(i);
			//id
			Integer dropdownId = (Integer)result[0];
			//entry
			String dropdownText = (String)result[1];
			
			StringObject textObject = new StringObject();
			StringObject stringId = new StringObject();
			BooleanObject selected = new BooleanObject();
			
			textObject.setValue(dropdownText);
			set.addObject(textObject);
			
			stringId.setValue(dropdownText);
			set.addObject(stringId);			
			
			selected.setValue(new Boolean(false));
			set.addObject(selected);
			
			returnModel.add(set);
			
			i++;
		}		
		
		return returnModel;
	}

	public DataModel getMatches(String cat, DataModel model, String match) {
		// TODO Auto-generated method stub
		return null;
	}
	
	public ModelField getModelField(StringObject cat) {
        ModelField modelField = new ModelField();
        DataModel model = getInitialModel((String)cat.getValue());
        modelField.setValue(model);
        return modelField;
    } 
}
