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
import org.openelis.gwt.common.Form;
import org.openelis.gwt.common.FormErrorException;
import org.openelis.gwt.common.LastPageException;
import org.openelis.gwt.common.QueryException;
import org.openelis.gwt.common.RPCException;
import org.openelis.gwt.common.TableFieldErrorException;
import org.openelis.gwt.common.data.AbstractField;
import org.openelis.gwt.common.data.CheckField;
import org.openelis.gwt.common.data.DataModel;
import org.openelis.gwt.common.data.DataSet;
import org.openelis.gwt.common.data.DropDownField;
import org.openelis.gwt.common.data.FieldType;
import org.openelis.gwt.common.data.StringField;
import org.openelis.gwt.common.data.StringObject;
import org.openelis.gwt.common.data.TableField;
import org.openelis.gwt.server.ServiceUtils;
import org.openelis.gwt.services.AppScreenFormServiceInt;
import org.openelis.gwt.services.AutoCompleteServiceInt;
import org.openelis.metamap.StorageLocationMetaMap;
import org.openelis.modules.storage.client.StorageLocationForm;
import org.openelis.modules.storage.client.StorageLocationRPC;
import org.openelis.persistence.EJBFactory;
import org.openelis.remote.StorageLocationRemote;
import org.openelis.remote.StorageUnitRemote;
import org.openelis.server.constants.Constants;
import org.openelis.util.SessionManager;
import org.openelis.util.UTFResource;

public class StorageLocationService implements AppScreenFormServiceInt<StorageLocationRPC,Integer>,
   											   AutoCompleteServiceInt{

	private static final int leftTableRowsPerPage = 19;
	
    private UTFResource openElisConstants= UTFResource.getBundle((String)SessionManager.getSession().getAttribute("locale"));

    private static final StorageLocationMetaMap StorageLocationMeta = new StorageLocationMetaMap();
    
	public DataModel<Integer> commitQuery(Form form, DataModel<Integer> model) throws RPCException {
        List storageLocs = new ArrayList();
    //		if the rpc is null then we need to get the page
    		if(form == null){
                
                form = (Form)SessionManager.getSession().getAttribute("StorageLocationQuery");
    
    	        if(form == null)
    	        	throw new QueryException(openElisConstants.getString("queryExpiredException"));
    			
    	        StorageLocationRemote remote = (StorageLocationRemote)EJBFactory.lookup("openelis/StorageLocationBean/remote");
    	        try{
    	        	storageLocs = remote.query(form.getFieldMap(), (model.getPage()*leftTableRowsPerPage), leftTableRowsPerPage+1);
    	        }catch(Exception e){
    	        	if(e instanceof LastPageException){
    	        		throw new LastPageException(openElisConstants.getString("lastPageException"));
    	        	}else{
    	        		throw new RPCException(e.getMessage());	
    	        	}
    	        }

    		}else{
    			StorageLocationRemote remote = (StorageLocationRemote)EJBFactory.lookup("openelis/StorageLocationBean/remote");
    			
    			HashMap<String,AbstractField> fields = form.getFieldMap();
    			fields.remove("childStorageLocsTable");

                try{
                    storageLocs = remote.query(fields,0,leftTableRowsPerPage);
    
        		}catch(Exception e){
        			throw new RPCException(e.getMessage());
        		}
        		
    //          need to save the rpc used to the encache
                SessionManager.getSession().setAttribute("StorageLocationQuery", form);
    		}
    		
            int i=0;
            if(model == null)
                model = new DataModel<Integer>();
            else
                model.clear();
            while(i < storageLocs.size() && i < leftTableRowsPerPage) {
                IdNameDO resultDO = (IdNameDO)storageLocs.get(i);
                model.add(new DataSet<Integer>(resultDO.getId(),new StringObject(resultDO.getName())));
                i++;
             } 
            
    		return model;
    	}

    public StorageLocationRPC commitAdd(StorageLocationRPC rpc) throws RPCException {
//		remote interface to call the storageLocation bean
		StorageLocationRemote remote = (StorageLocationRemote)EJBFactory.lookup("openelis/StorageLocationBean/remote");
		StorageLocationDO newStorageLocDO = new StorageLocationDO();
		List storageLocationChildren = new ArrayList();
		
//		build the storage unit DO from the form
		newStorageLocDO = getStorageLocationDOFromRPC(rpc.form);
		
//		child locs info
        TableField childTableField = rpc.form.childStorageLocsTable;
        DataModel childTable = (DataModel)childTableField.getValue();
		storageLocationChildren = getChildStorageLocsFromRPC(childTable);
				
		//validate the fields on the backend
		List exceptionList = remote.validateForAdd(newStorageLocDO, storageLocationChildren);
		
		if(exceptionList.size() > 0){
			setRpcErrors(exceptionList, childTableField, rpc.form);
			return rpc;
		} 
		
		//send the changes to the database
		Integer storageLocId;
		try{
			storageLocId = (Integer)remote.updateStorageLoc(newStorageLocDO, storageLocationChildren);
		}catch(Exception e){
			exceptionList = new ArrayList();
			exceptionList.add(e);
			
			setRpcErrors(exceptionList, childTableField, rpc.form);
			
			return rpc;
		}
		
		newStorageLocDO.setId(storageLocId);
        
//		set the fields in the RPC
		setFieldsInRPC(rpc.form, newStorageLocDO);

		return rpc;
	}

	public StorageLocationRPC commitUpdate(StorageLocationRPC rpc) throws RPCException {
//		remote interface to call the storage loc bean
		StorageLocationRemote remote = (StorageLocationRemote)EJBFactory.lookup("openelis/StorageLocationBean/remote");
		StorageLocationDO newStorageLocDO = new StorageLocationDO();
		List storageLocationChildren = new ArrayList();
		
		//build the DO from the form
		newStorageLocDO = getStorageLocationDOFromRPC(rpc.form);
		
//		children info
        TableField childTableField = rpc.form.childStorageLocsTable;
        DataModel childTable = (DataModel)childTableField.getValue();
		storageLocationChildren = getChildStorageLocsFromRPC(childTable);
		
		//validate the fields on the backend
		List exceptionList = remote.validateForUpdate(newStorageLocDO, storageLocationChildren);
		if(exceptionList.size() > 0){
			setRpcErrors(exceptionList, childTableField, rpc.form);
			
			return rpc;
		} 
		
//		send the changes to the database
		try{
			remote.updateStorageLoc(newStorageLocDO, storageLocationChildren);
		}catch(Exception e){
            if(e instanceof EntityLockedException)
                throw new RPCException(e.getMessage());
            
			exceptionList = new ArrayList();
			exceptionList.add(e);
			
			setRpcErrors(exceptionList, childTableField, rpc.form);
			
			return rpc;
		}
		
//		set the fields in the RPC
		setFieldsInRPC(rpc.form, newStorageLocDO);
        
		return rpc;
	}

	public StorageLocationRPC commitDelete(StorageLocationRPC rpc) throws RPCException {
//		remote interface to call the storage location bean
		StorageLocationRemote remote = (StorageLocationRemote)EJBFactory.lookup("openelis/StorageLocationBean/remote");
		
		//validate the fields on the backend
		List exceptionList = remote.validateForDelete(rpc.key);
		if(exceptionList.size() > 0){
			setRpcErrors(exceptionList, null, rpc.form);
			
			return rpc;
		} 
		
		try {
			remote.deleteStorageLoc(rpc.key);
			
		} catch (Exception e) {
			exceptionList = new ArrayList();
			exceptionList.add(e);
			
			setRpcErrors(exceptionList, null, rpc.form);
			return rpc;
		}	
		
		setFieldsInRPC(rpc.form, new StorageLocationDO());
		rpc.form.childStorageLocsTable.setValue(null);
		
		return rpc;
	}

	public StorageLocationRPC abort(StorageLocationRPC rpc) throws RPCException {
    //		remote interface to call the storage location bean
    		StorageLocationRemote remote = (StorageLocationRemote)EJBFactory.lookup("openelis/StorageLocationBean/remote");
    		
    		
    		StorageLocationDO storageLocDO = remote.getStorageLocAndUnlock(rpc.key, SessionManager.getSession().getId());
    
    //		set the fields in the RPC
    		setFieldsInRPC(rpc.form, storageLocDO);
            
    //		load the children
            List childrenList = remote.getStorageLocChildren(rpc.key);
            //need to build the children table now...
            DataModel rmodel = (DataModel)fillChildrenTable(rpc.form.childStorageLocsTable.getValue(),childrenList);
            rpc.form.childStorageLocsTable.setValue(rmodel);
            
    		return rpc;  
    	}

    public StorageLocationRPC fetch(StorageLocationRPC rpc) throws RPCException {
//		remote interface to call the storage loc bean
		StorageLocationRemote remote = (StorageLocationRemote)EJBFactory.lookup("openelis/StorageLocationBean/remote");
		
		StorageLocationDO storageLocDO = remote.getStorageLoc(rpc.key);
		
//		set the fields in the RPC
		setFieldsInRPC(rpc.form, storageLocDO);
		
//		load the children
        List childrenList = remote.getStorageLocChildren(rpc.key);
        //need to build the children table now...
        DataModel rmodel = (DataModel)fillChildrenTable(rpc.form.childStorageLocsTable.getValue(),childrenList);
        rpc.form.childStorageLocsTable.setValue(rmodel);

		return rpc;
	}

	public StorageLocationRPC fetchForUpdate(StorageLocationRPC rpc) throws RPCException {
//		remote interface to call the storage loc bean
		StorageLocationRemote remote = (StorageLocationRemote)EJBFactory.lookup("openelis/StorageLocationBean/remote");
		StorageLocationDO storageLocDO = new StorageLocationDO();
		
		try{
			storageLocDO = remote.getStorageLocAndLock(rpc.key, SessionManager.getSession().getId());
		}catch(Exception e){
			throw new RPCException(e.getMessage());
		}
		
//		set the fields in the RPC
		setFieldsInRPC(rpc.form, storageLocDO);
		
//		load the children
        List childrenList = remote.getStorageLocChildren(rpc.key);
        //need to build the children table now...
        DataModel rmodel = (DataModel)fillChildrenTable(rpc.form.childStorageLocsTable.getValue(),childrenList);
        rpc.form.childStorageLocsTable.setValue(rmodel);

		return rpc;
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
    
    public StorageLocationRPC getScreen(StorageLocationRPC rpc) throws RPCException {
        rpc.xml = ServiceUtils.getXML(Constants.APP_ROOT+"/Forms/storageLocation.xsl");

        return rpc;
    }

    public DataModel<Integer> fillChildrenTable(DataModel<Integer> childModel, List childrenList){
    		try 
            {
    			childModel.clear();
    			
    			for(int iter = 0;iter < childrenList.size();iter++) {
    				StorageLocationDO slDO = (StorageLocationDO)childrenList.get(iter);
    
    	               DataSet<Integer> row = childModel.createNewSet();
    	               row.setKey(slDO.getId());
    	               
    //		       		we need to create a dataset for the storage unit auto complete
    		       		if(slDO.getStorageUnitId() == null)
    		       			row.get(0).setValue(null);
    		       		else{
                             DataModel<Integer> unitModel = new DataModel<Integer>();
                             unitModel.add(new DataSet<Integer>(slDO.getStorageUnitId(),new StringObject(slDO.getStorageUnit())));
                             ((DropDownField)row.get(0)).setModel(unitModel);
                             row.get(0).setValue(unitModel.get(0));
    		       		}
    		       		
    	               row.get(1).setValue(slDO.getLocation());
    	       		
    	       		row.get(2).setValue(slDO.getIsAvailable());
    	                
    	            childModel.add(row);
    	       } 
    			
            } catch (Exception e) {
    
                e.printStackTrace();
                return null;
            }		
    		
    		return childModel;
    	}

    public DataModel getMatches(String cat, DataModel model, String match, HashMap params) {
    	if(cat.equals("storageUnit"))
    		return getStorageUnitMatches(match);
    	
    	//return null if cat doesnt match
    	return null;
    }

    private void setFieldsInRPC(StorageLocationForm form, StorageLocationDO storageLocDO){
        form.id.setValue(storageLocDO.getId());
        form.isAvailable.setValue(storageLocDO.getIsAvailable());
        form.location.setValue(storageLocDO.getLocation());
        form.name.setValue(storageLocDO.getName());
        
//		we need to create a dataset for the storage unit auto complete
		if(storageLocDO.getStorageUnitId() == null)
			form.storageUnit.clear();
		else{
            DataModel<Integer> unitModel = new DataModel<Integer>();
            unitModel.add(new DataSet<Integer>(storageLocDO.getStorageUnitId(),new StringObject(storageLocDO.getStorageUnit())));
            form.storageUnit.setModel(unitModel);
            form.storageUnit.setValue(unitModel.get(0));
		}
	}
	
	private StorageLocationDO getStorageLocationDOFromRPC(StorageLocationForm form){
		StorageLocationDO newStorageLocDO = new StorageLocationDO();
		
		newStorageLocDO.setId(form.id.getValue());
		newStorageLocDO.setIsAvailable(form.isAvailable.getValue());
		newStorageLocDO.setLocation(form.location.getValue());
		newStorageLocDO.setName(form.name.getValue());
		newStorageLocDO.setStorageUnitId((Integer)form.storageUnit.getSelectedKey());
        newStorageLocDO.setStorageUnit((String)form.storageUnit.getTextValue());
		
		return newStorageLocDO;
	}
	
	private List getChildStorageLocsFromRPC(DataModel<Integer> childTable){
		List storageLocationChildren = new ArrayList();
        List<DataSet<Integer>> deletedRows = childTable.getDeletions();
		
		for(int i=0; i<childTable.size(); i++){
			StorageLocationDO childDO = new StorageLocationDO();
			DataSet row = childTable.get(i);
			
			//parent data
			Integer id = (Integer)row.getKey();
			
			childDO.setId(id);
			childDO.setStorageUnitId((Integer)((DropDownField)row.get(0)).getSelectedKey());
			childDO.setLocation(((String)((StringField)row.get(1)).getValue()));
			childDO.setIsAvailable(((String)((CheckField)row.get(2)).getValue()));
				
			storageLocationChildren.add(childDO);	
		}
        
        for(int j=0; j<deletedRows.size(); j++){
            DataSet<Integer> deletedRow = deletedRows.get(j);
            if(deletedRow.getKey() != null){
                StorageLocationDO childDO = new StorageLocationDO();
                childDO.setDelete(true);
                childDO.setId(deletedRow.getKey());
                
                storageLocationChildren.add(childDO);
            }
        }
        
		return storageLocationChildren;
	}
    
	private DataModel<Integer> getStorageUnitMatches(String match){
		StorageUnitRemote remote = (StorageUnitRemote)EJBFactory.lookup("openelis/StorageUnitBean/remote");
		DataModel<Integer> dataModel = new DataModel<Integer>();

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
			
			DataSet<Integer> data = new DataSet<Integer>();
			//hidden id
			data.setKey(id);
			//columns
			StringObject descObject = new StringObject();
			descObject.setValue(desc);
			data.add(descObject);
			StringObject categoryObject = new StringObject();
			categoryObject.setValue(category);
			data.add(categoryObject);
			
			//add the dataset to the datamodel
			dataModel.add(data);
		}
		return dataModel;
	}
	
	private void setRpcErrors(List exceptionList, TableField childTable, Form form){
        for (int i=0; i<exceptionList.size();i++) {
            //if the error is inside the org contacts table
            if(exceptionList.get(i) instanceof TableFieldErrorException){
                int rowindex = ((TableFieldErrorException)exceptionList.get(i)).getRowIndex();
                childTable.getField(rowindex,((TableFieldErrorException)exceptionList.get(i)).getFieldName())
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
