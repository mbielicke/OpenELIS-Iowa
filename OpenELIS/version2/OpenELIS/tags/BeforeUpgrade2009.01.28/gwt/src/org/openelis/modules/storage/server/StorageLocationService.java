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
import org.openelis.gwt.common.FormErrorException;
import org.openelis.gwt.common.FormRPC;
import org.openelis.gwt.common.LastPageException;
import org.openelis.gwt.common.QueryException;
import org.openelis.gwt.common.RPCException;
import org.openelis.gwt.common.TableFieldErrorException;
import org.openelis.gwt.common.FormRPC.Status;
import org.openelis.gwt.common.data.AbstractField;
import org.openelis.gwt.common.data.CheckField;
import org.openelis.gwt.common.data.DataModel;
import org.openelis.gwt.common.data.DataObject;
import org.openelis.gwt.common.data.DataSet;
import org.openelis.gwt.common.data.DropDownField;
import org.openelis.gwt.common.data.ModelField;
import org.openelis.gwt.common.data.NumberObject;
import org.openelis.gwt.common.data.StringField;
import org.openelis.gwt.common.data.StringObject;
import org.openelis.gwt.common.data.TableField;
import org.openelis.gwt.server.ServiceUtils;
import org.openelis.gwt.services.AppScreenFormServiceInt;
import org.openelis.gwt.services.AutoCompleteServiceInt;
import org.openelis.metamap.StorageLocationMetaMap;
import org.openelis.persistence.EJBFactory;
import org.openelis.remote.StorageLocationRemote;
import org.openelis.remote.StorageUnitRemote;
import org.openelis.server.constants.Constants;
import org.openelis.util.SessionManager;
import org.openelis.util.UTFResource;

public class StorageLocationService implements AppScreenFormServiceInt<FormRPC, DataSet, DataModel>,
   											   AutoCompleteServiceInt{

	private static final int leftTableRowsPerPage = 19;
	
    private UTFResource openElisConstants= UTFResource.getBundle((String)SessionManager.getSession().getAttribute("locale"));

    private static final StorageLocationMetaMap StorageLocationMeta = new StorageLocationMetaMap();
    
	public DataModel commitQuery(FormRPC rpcSend, DataModel model) throws RPCException {
        List storageLocs = new ArrayList();
    //		if the rpc is null then we need to get the page
    		if(rpcSend == null){
                
                FormRPC rpc = (FormRPC)SessionManager.getSession().getAttribute("StorageLocationQuery");
    
    	        if(rpc == null)
    	        	throw new QueryException(openElisConstants.getString("queryExpiredException"));
    			
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
            if(model == null)
                model = new DataModel();
            else
                model.clear();
            while(i < storageLocs.size() && i < leftTableRowsPerPage) {
                IdNameDO resultDO = (IdNameDO)storageLocs.get(i);
                //org id
                Integer idResult = resultDO.getId();
                //org name
                String nameResult = resultDO.getName();

                DataSet row = new DataSet();
                NumberObject id = new NumberObject(NumberObject.Type.INTEGER);
                StringObject name = new StringObject();
                name.setValue(nameResult);
                id.setValue(idResult);
                
                row.setKey(id);         
                row.add(name);
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
        TableField childTableField = (TableField)rpcSend.getField("childStorageLocsTable");
        DataModel childTable = (DataModel)childTableField.getValue();
		storageLocationChildren = getChildStorageLocsFromRPC(childTable);
				
		//validate the fields on the backend
		List exceptionList = remote.validateForAdd(newStorageLocDO, storageLocationChildren);
		
		if(exceptionList.size() > 0){
			setRpcErrors(exceptionList, childTableField, rpcSend);
			return rpcSend;
		} 
		
		//send the changes to the database
		Integer storageLocId;
		try{
			storageLocId = (Integer)remote.updateStorageLoc(newStorageLocDO, storageLocationChildren);
		}catch(Exception e){
			exceptionList = new ArrayList();
			exceptionList.add(e);
			
			setRpcErrors(exceptionList, childTableField, rpcSend);
			
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
        TableField childTableField = (TableField)rpcSend.getField("childStorageLocsTable");
        DataModel childTable = (DataModel)childTableField.getValue();
		storageLocationChildren = getChildStorageLocsFromRPC(childTable);
		
		//validate the fields on the backend
		List exceptionList = remote.validateForUpdate(newStorageLocDO, storageLocationChildren);
		if(exceptionList.size() > 0){
			setRpcErrors(exceptionList, childTableField, rpcSend);
			
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
			
			setRpcErrors(exceptionList, childTableField, rpcSend);
			
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
		List exceptionList = remote.validateForDelete((Integer)((DataObject)key.getKey()).getValue());
		if(exceptionList.size() > 0){
			setRpcErrors(exceptionList, null, rpcReturn);
			
			return rpcReturn;
		} 
		
		try {
			remote.deleteStorageLoc((Integer)((DataObject)key.getKey()).getValue());
			
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
    		
    		
    		StorageLocationDO storageLocDO = remote.getStorageLocAndUnlock((Integer)((DataObject)key.getKey()).getValue(), SessionManager.getSession().getId());
    
    //		set the fields in the RPC
    		setFieldsInRPC(rpcReturn, storageLocDO);
            
    //		load the children
            List childrenList = remote.getStorageLocChildren((Integer)((DataObject)key.getKey()).getValue());
            //need to build the children table now...
            DataModel rmodel = (DataModel)fillChildrenTable((DataModel)rpcReturn.getField("childStorageLocsTable").getValue(),childrenList);
            rpcReturn.setFieldValue("childStorageLocsTable",rmodel);
            
    		return rpcReturn;  
    	}

    public FormRPC fetch(DataSet key, FormRPC rpcReturn) throws RPCException {
//		remote interface to call the storage loc bean
		StorageLocationRemote remote = (StorageLocationRemote)EJBFactory.lookup("openelis/StorageLocationBean/remote");
		
		StorageLocationDO storageLocDO = remote.getStorageLoc((Integer)((DataObject)key.getKey()).getValue());
		
//		set the fields in the RPC
		setFieldsInRPC(rpcReturn, storageLocDO);
		
//		load the children
        List childrenList = remote.getStorageLocChildren((Integer)((DataObject)key.getKey()).getValue());
        //need to build the children table now...
        DataModel rmodel = (DataModel)fillChildrenTable((DataModel)rpcReturn.getField("childStorageLocsTable").getValue(),childrenList);
        rpcReturn.setFieldValue("childStorageLocsTable",rmodel);

		return rpcReturn;
	}

	public FormRPC fetchForUpdate(DataSet key, FormRPC rpcReturn) throws RPCException {
//		remote interface to call the storage loc bean
		StorageLocationRemote remote = (StorageLocationRemote)EJBFactory.lookup("openelis/StorageLocationBean/remote");
		StorageLocationDO storageLocDO = new StorageLocationDO();
		
		try{
			storageLocDO = remote.getStorageLocAndLock((Integer)((DataObject)key.getKey()).getValue(), SessionManager.getSession().getId());
		}catch(Exception e){
			throw new RPCException(e.getMessage());
		}
		
//		set the fields in the RPC
		setFieldsInRPC(rpcReturn, storageLocDO);
		
//		load the children
        List childrenList = remote.getStorageLocChildren((Integer)((DataObject)key.getKey()).getValue());
        //need to build the children table now...
        DataModel rmodel = (DataModel)fillChildrenTable((DataModel)rpcReturn.getField("childStorageLocsTable").getValue(),childrenList);
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

    public DataModel fillChildrenTable(DataModel childModel, List childrenList){
    		try 
            {
    			childModel.clear();
    			
    			for(int iter = 0;iter < childrenList.size();iter++) {
    				StorageLocationDO slDO = (StorageLocationDO)childrenList.get(iter);
    
    	               DataSet row = childModel.createNewSet();
    	               
    	               NumberObject id = new NumberObject(slDO.getId());
    	               
    	               row.setKey(id);
    	               
    //		       		we need to create a dataset for the storage unit auto complete
    		       		if(slDO.getStorageUnitId() == null)
    		       			row.get(0).setValue(null);
    		       		else{
                             DataModel unitModel = new DataModel();
                             unitModel.add(new NumberObject(slDO.getStorageUnitId()),new StringObject(slDO.getStorageUnit()));
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

    private void setFieldsInRPC(FormRPC rpcReturn, StorageLocationDO storageLocDO){
		rpcReturn.setFieldValue(StorageLocationMeta.getId(), storageLocDO.getId());
		rpcReturn.setFieldValue(StorageLocationMeta.getIsAvailable(), storageLocDO.getIsAvailable());
		rpcReturn.setFieldValue(StorageLocationMeta.getLocation(), storageLocDO.getLocation());
		rpcReturn.setFieldValue(StorageLocationMeta.getName(), storageLocDO.getName());
		
//		we need to create a dataset for the storage unit auto complete
		if(storageLocDO.getStorageUnitId() == null)
			rpcReturn.setFieldValue(StorageLocationMeta.STORAGE_UNIT_META.getDescription(), null);
		else{
            DataModel unitModel = new DataModel();
            unitModel.add(new NumberObject(storageLocDO.getStorageUnitId()),new StringObject(storageLocDO.getStorageUnit()));
            ((DropDownField)rpcReturn.getField(StorageLocationMeta.STORAGE_UNIT_META.getDescription())).setModel(unitModel);
            rpcReturn.setFieldValue(StorageLocationMeta.STORAGE_UNIT_META.getDescription(), unitModel.get(0));
		}
	}
	
	private StorageLocationDO getStorageLocationDOFromRPC(FormRPC rpcSend){
		StorageLocationDO newStorageLocDO = new StorageLocationDO();
		
		newStorageLocDO.setId((Integer)rpcSend.getFieldValue(StorageLocationMeta.getId()));
		newStorageLocDO.setIsAvailable((String) rpcSend.getFieldValue(StorageLocationMeta.getIsAvailable()));
		newStorageLocDO.setLocation(((String)rpcSend.getFieldValue(StorageLocationMeta.getLocation())));
		newStorageLocDO.setName(((String)rpcSend.getFieldValue(StorageLocationMeta.getName())));
		newStorageLocDO.setParentStorageLocationId((Integer)rpcSend.getFieldValue(StorageLocationMeta.CHILD_STORAGE_LOCATION_META.getId()));
		newStorageLocDO.setStorageUnitId((Integer)((DropDownField)rpcSend.getField(StorageLocationMeta.STORAGE_UNIT_META.getDescription())).getSelectedKey());
        newStorageLocDO.setStorageUnit((String)((DropDownField)rpcSend.getField(StorageLocationMeta.STORAGE_UNIT_META.getDescription())).getTextValue());
		
		return newStorageLocDO;
	}
	
	private List getChildStorageLocsFromRPC(DataModel childTable){
		List storageLocationChildren = new ArrayList();
        List deletedRows = childTable.getDeletions();
		
		for(int i=0; i<childTable.size(); i++){
			StorageLocationDO childDO = new StorageLocationDO();
			DataSet row = childTable.get(i);
			
			//parent data
			NumberObject id = (NumberObject)row.getKey();
			
			if(id != null)
				childDO.setId((Integer)id.getValue());
			
			childDO.setStorageUnitId((Integer)((DropDownField)row.get(0)).getSelectedKey());
			childDO.setLocation(((String)((StringField)row.get(1)).getValue()));
			childDO.setIsAvailable(((String)((CheckField)row.get(2)).getValue()));
				
			storageLocationChildren.add(childDO);	
		}
        
        for(int j=0; j<deletedRows.size(); j++){
            DataSet deletedRow = (DataSet)deletedRows.get(j);
            if(deletedRow.getKey() != null){
                StorageLocationDO childDO = new StorageLocationDO();
                childDO.setDelete(true);
                childDO.setId((Integer)((NumberObject)deletedRow.getKey()).getValue());
                
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
			NumberObject idObject = new NumberObject(NumberObject.Type.INTEGER);
			idObject.setValue(id);
			data.setKey(idObject);
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
	
	private void setRpcErrors(List exceptionList, TableField childTable, FormRPC rpcSend){
        for (int i=0; i<exceptionList.size();i++) {
            //if the error is inside the org contacts table
            if(exceptionList.get(i) instanceof TableFieldErrorException){
                int rowindex = ((TableFieldErrorException)exceptionList.get(i)).getRowIndex();
                childTable.getField(rowindex,((TableFieldErrorException)exceptionList.get(i)).getFieldName())
                    .addError(openElisConstants.getString(((FieldErrorException)exceptionList.get(i)).getMessage()));

            //if the error is on the field
            }else if(exceptionList.get(i) instanceof FieldErrorException)
                rpcSend.getField(((FieldErrorException)exceptionList.get(i)).getFieldName()).addError(openElisConstants.getString(((FieldErrorException)exceptionList.get(i)).getMessage()));
            
            //if the error is on the entire form
            else if(exceptionList.get(i) instanceof FormErrorException)
                rpcSend.addError(openElisConstants.getString(((FormErrorException)exceptionList.get(i)).getMessage()));
            }        
        
        rpcSend.status = Status.invalid;
    }
}
