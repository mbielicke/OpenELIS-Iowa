package org.openelis.modules.storage.server;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.openelis.domain.IdNameDO;
import org.openelis.domain.StorageLocationDO;
import org.openelis.domain.StorageUnitAutoDO;
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
import org.openelis.gwt.common.data.CheckField;
import org.openelis.gwt.common.data.DataModel;
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
import org.openelis.meta.StorageLocationChildMeta;
import org.openelis.meta.StorageLocationMeta;
import org.openelis.meta.StorageLocationStorageUnitMeta;
import org.openelis.persistence.EJBFactory;
import org.openelis.remote.StorageLocationRemote;
import org.openelis.remote.StorageUnitRemote;
import org.openelis.server.constants.Constants;
import org.openelis.util.SessionManager;
import org.openelis.util.UTFResource;

public class StorageLocationService implements AppScreenFormServiceInt,
   											   AutoCompleteServiceInt{

	private static final int leftTableRowsPerPage = 19;
	
    private UTFResource openElisConstants= UTFResource.getBundle((String)SessionManager.getSession().getAttribute("locale"));

	public DataModel commitQuery(FormRPC rpcSend, DataModel model) throws RPCException {
        List storageLocs = new ArrayList();
    //		if the rpc is null then we need to get the page
    		if(rpcSend == null){
                
                FormRPC rpc = (FormRPC)SessionManager.getSession().getAttribute("StorageLocationQuery");
    
    	        if(rpc == null)
    	        	throw new QueryNotFoundException(openElisConstants.getString("queryExpiredException"));
    			
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

    		}else{
    			StorageLocationRemote remote = (StorageLocationRemote)EJBFactory.lookup("openelis/StorageLocationBean/remote");
    			
    			HashMap<String,AbstractField> fields = rpcSend.getFieldMap();
    			fields.remove("childStorageLocsTable");

                try{
                    storageLocs = remote.query(fields,0,leftTableRowsPerPage);
    
        		}catch(Exception e){
        			throw new RPCException(e.getMessage());
        		}
        		
    //          need to save the rpc used to the encache
                SessionManager.getSession().setAttribute("StorageLocationQuery", rpcSend);
    		}
    		
            int i=0;
            model.clear();
            while(i < storageLocs.size() && i < leftTableRowsPerPage) {
                IdNameDO resultDO = (IdNameDO)storageLocs.get(i);
                //org id
                Integer idResult = resultDO.getId();
                //org name
                String nameResult = resultDO.getName();

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
            if(e instanceof EntityLockedException)
                throw new RPCException(e.getMessage());
            
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

	public String getXML() throws RPCException {
    	return ServiceUtils.getXML(Constants.APP_ROOT+"/Forms/storageLocation.xsl");
    }

    public HashMap getXMLData() throws RPCException {
        StringObject xml = new StringObject();
        xml.setValue(ServiceUtils.getXML(Constants.APP_ROOT+"/Forms/storageLocation.xsl"));
        DataModel model = new DataModel();
        ModelField data = new ModelField();
        data.setValue(model);
        HashMap map = new HashMap();
        map.put("xml", xml);
        map.put("data",data);
        return map;
    }

    public HashMap getXMLData(HashMap args) throws RPCException {
    	// TODO Auto-generated method stub
    	return null;
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
    		       			storageUnitText.setValue(slDO.getStorageUnit());
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

    public DataModel getInitialModel(String cat) {
    	return null;
    }

    public DataModel getMatches(String cat, DataModel model, String match, HashMap params) {
    	if(cat.equals("storageUnit"))
    		return getStorageUnitMatches(match);
    	
    	//return null if cat doesnt match
    	return null;
    }

    private void setFieldsInRPC(FormRPC rpcReturn, StorageLocationDO storageLocDO){
		rpcReturn.setFieldValue(StorageLocationMeta.ID, storageLocDO.getId());
		rpcReturn.setFieldValue(StorageLocationMeta.IS_AVAILABLE, storageLocDO.getIsAvailable());
		rpcReturn.setFieldValue(StorageLocationMeta.LOCATION, storageLocDO.getLocation());
		rpcReturn.setFieldValue(StorageLocationMeta.NAME, storageLocDO.getName());
		
//		we need to create a dataset for the storage unit auto complete
		if(storageLocDO.getStorageUnitId() == null)
			rpcReturn.setFieldValue(StorageLocationStorageUnitMeta.DESCRIPTION, null);
		else{
			DataSet storageUnitSet = new DataSet();
			NumberObject id = new NumberObject(NumberObject.INTEGER);
			StringObject text = new StringObject();
			id.setValue(storageLocDO.getStorageUnitId());
			text.setValue(storageLocDO.getStorageUnit());
			storageUnitSet.setKey(id);
			storageUnitSet.addObject(text);
			rpcReturn.setFieldValue(StorageLocationStorageUnitMeta.DESCRIPTION, storageUnitSet);
		}
	}
	
	private StorageLocationDO getStorageLocationDOFromRPC(FormRPC rpcSend){
		StorageLocationDO newStorageLocDO = new StorageLocationDO();
		
		newStorageLocDO.setId((Integer)rpcSend.getFieldValue(StorageLocationMeta.ID));
		newStorageLocDO.setIsAvailable((String) rpcSend.getFieldValue(StorageLocationMeta.IS_AVAILABLE));
		newStorageLocDO.setLocation(((String)rpcSend.getFieldValue(StorageLocationMeta.LOCATION)));
		newStorageLocDO.setName(((String)rpcSend.getFieldValue(StorageLocationMeta.NAME)));
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
				childDO.setLocation(((String)((StringField)row.getColumn(1)).getValue()));
				childDO.setIsAvailable(((String)((CheckField)row.getColumn(2)).getValue()));
					
				storageLocationChildren.add(childDO);	
			}
		}
		return storageLocationChildren;
	}
    
	private DataModel getStorageUnitMatches(String match){
		StorageUnitRemote remote = (StorageUnitRemote)EJBFactory.lookup("openelis/StorageUnitBean/remote");
		DataModel dataModel = new DataModel();

		//lookup by desc		
		List autoCompleteList = remote.autoCompleteLookupByDescription(match+"%", 10);
		Iterator itr = autoCompleteList.iterator();
		
		while(itr.hasNext()){
			StorageUnitAutoDO resultDO = (StorageUnitAutoDO) itr.next();
			//id
			Integer id = resultDO.getId();
			//desc
			String desc = resultDO.getDescription();
			//category
			String category = resultDO.getCategory();
			
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
		return dataModel;
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
}
