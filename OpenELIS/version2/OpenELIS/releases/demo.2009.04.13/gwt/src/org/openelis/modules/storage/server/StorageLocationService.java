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

import org.openelis.domain.IdNameDO;
import org.openelis.domain.StorageLocationDO;
import org.openelis.domain.StorageUnitAutoDO;
import org.openelis.gwt.common.EntityLockedException;
import org.openelis.gwt.common.FieldErrorException;
import org.openelis.gwt.common.Form;
import org.openelis.gwt.common.FormErrorException;
import org.openelis.gwt.common.LastPageException;
import org.openelis.gwt.common.Query;
import org.openelis.gwt.common.QueryException;
import org.openelis.gwt.common.RPCException;
import org.openelis.gwt.common.TableFieldErrorException;
import org.openelis.gwt.common.ValidationErrorsList;
import org.openelis.gwt.common.data.AbstractField;
import org.openelis.gwt.common.data.DropDownField;
import org.openelis.gwt.common.data.FieldType;
import org.openelis.gwt.common.data.StringObject;
import org.openelis.gwt.common.data.TableDataModel;
import org.openelis.gwt.common.data.TableDataRow;
import org.openelis.gwt.common.data.TableField;
import org.openelis.gwt.server.ServiceUtils;
import org.openelis.gwt.services.AppScreenFormServiceInt;
import org.openelis.gwt.services.AutoCompleteServiceInt;
import org.openelis.metamap.StorageLocationMetaMap;
import org.openelis.modules.storage.client.StorageLocationForm;
import org.openelis.persistence.EJBFactory;
import org.openelis.remote.StorageLocationRemote;
import org.openelis.remote.StorageUnitRemote;
import org.openelis.server.constants.Constants;
import org.openelis.util.FormUtil;
import org.openelis.util.SessionManager;
import org.openelis.util.UTFResource;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class StorageLocationService implements AppScreenFormServiceInt<StorageLocationForm,Query<TableDataRow<Integer>>>,
   											   AutoCompleteServiceInt{

	private static final int leftTableRowsPerPage = 19;
	
    private UTFResource openElisConstants= UTFResource.getBundle((String)SessionManager.getSession().getAttribute("locale"));

    private static final StorageLocationMetaMap StorageLocationMeta = new StorageLocationMetaMap();
    
	public Query<TableDataRow<Integer>> commitQuery(Query<TableDataRow<Integer>> query) throws RPCException {
        List storageLocs = new ArrayList();
    //		if the rpc is null then we need to get the page
        /*
    		if(qList == null){
                
                qList = (ArrayList<AbstractField>)SessionManager.getSession().getAttribute("StorageLocationQuery");
    
    	        if(qList == null)
    	        	throw new QueryException(openElisConstants.getString("queryExpiredException"));
    			
    	        StorageLocationRemote remote = (StorageLocationRemote)EJBFactory.lookup("openelis/StorageLocationBean/remote");
    	        try{
    	        	storageLocs = remote.query(qList, (model.getPage()*leftTableRowsPerPage), leftTableRowsPerPage+1);
    	        }catch(Exception e){
    	        	if(e instanceof LastPageException){
    	        		throw new LastPageException(openElisConstants.getString("lastPageException"));
    	        	}else{
    	        		throw new RPCException(e.getMessage());	
    	        	}
    	        }

    		}else{ */
    			StorageLocationRemote remote = (StorageLocationRemote)EJBFactory.lookup("openelis/StorageLocationBean/remote");
    			

                try{
                    storageLocs = remote.query(query.fields,query.page*leftTableRowsPerPage,leftTableRowsPerPage);
    
        		}catch(Exception e){
        			throw new RPCException(e.getMessage());
        		}
        		
    //          need to save the rpc used to the encache
                //SessionManager.getSession().setAttribute("StorageLocationQuery", qList);
    		//}
    		
            int i=0;
            if(query.results == null)
                query.results = new TableDataModel<TableDataRow<Integer>>();
            else
                query.results.clear();
            while(i < storageLocs.size() && i < leftTableRowsPerPage) {
                IdNameDO resultDO = (IdNameDO)storageLocs.get(i);
                query.results.add(new TableDataRow<Integer>(resultDO.getId(),new StringObject(resultDO.getName())));
                i++;
             } 
            
    		return query;
    	}

    public StorageLocationForm commitAdd(StorageLocationForm rpc) throws RPCException {
//		remote interface to call the storageLocation bean
		StorageLocationRemote remote = (StorageLocationRemote)EJBFactory.lookup("openelis/StorageLocationBean/remote");
		StorageLocationDO newStorageLocDO = new StorageLocationDO();
		List storageLocationChildren = new ArrayList();
		
//		build the storage unit DO from the form
		newStorageLocDO = getStorageLocationDOFromRPC(rpc);
		
//		child locs info
        TableField<TableDataRow<Integer>> childTableField = rpc.childStorageLocsTable;
        TableDataModel<TableDataRow<Integer>> childTable = childTableField.getValue();
		storageLocationChildren = getChildStorageLocsFromRPC(childTable);
				
		//send the changes to the database
		Integer storageLocId;
		try{
			storageLocId = (Integer)remote.updateStorageLoc(newStorageLocDO, storageLocationChildren);
			
		}catch(Exception e){
            if(e instanceof ValidationErrorsList){
                setRpcErrors(((ValidationErrorsList)e).getErrorList(), childTableField, rpc);
                return rpc;
            }else
                throw new RPCException(e.getMessage());
        }
		
		newStorageLocDO.setId(storageLocId);
        
//		set the fields in the RPC
		setFieldsInRPC(rpc, newStorageLocDO);

		return rpc;
	}

	public StorageLocationForm commitUpdate(StorageLocationForm rpc) throws RPCException {
//		remote interface to call the storage loc bean
		StorageLocationRemote remote = (StorageLocationRemote)EJBFactory.lookup("openelis/StorageLocationBean/remote");
		StorageLocationDO newStorageLocDO = new StorageLocationDO();
		List storageLocationChildren = new ArrayList();
		
		//build the DO from the form
		newStorageLocDO = getStorageLocationDOFromRPC(rpc);
		
//		children info
        TableField<TableDataRow<Integer>> childTableField = rpc.childStorageLocsTable;
        TableDataModel<TableDataRow<Integer>> childTable = childTableField.getValue();
		storageLocationChildren = getChildStorageLocsFromRPC(childTable);
		
//		send the changes to the database
		try{
			remote.updateStorageLoc(newStorageLocDO, storageLocationChildren);
			
		}catch(Exception e){
            if(e instanceof ValidationErrorsList){
                setRpcErrors(((ValidationErrorsList)e).getErrorList(), childTableField, rpc);
                return rpc;
            }else
                throw new RPCException(e.getMessage());
        }
		
//		set the fields in the RPC
		setFieldsInRPC(rpc, newStorageLocDO);
        
		return rpc;
	}

	public StorageLocationForm commitDelete(StorageLocationForm rpc) throws RPCException {
//		remote interface to call the storage location bean
		StorageLocationRemote remote = (StorageLocationRemote)EJBFactory.lookup("openelis/StorageLocationBean/remote");
		
		try {
			remote.deleteStorageLoc(rpc.entityKey);
			
		}catch(Exception e){
            if(e instanceof ValidationErrorsList){
                setRpcErrors(((ValidationErrorsList)e).getErrorList(), null, rpc);
                return rpc;
            }else
                throw new RPCException(e.getMessage());
        }	
		
		setFieldsInRPC(rpc, new StorageLocationDO());
		rpc.childStorageLocsTable.setValue(null);
		
		return rpc;
	}

	public StorageLocationForm abort(StorageLocationForm rpc) throws RPCException {
    //		remote interface to call the storage location bean
    		StorageLocationRemote remote = (StorageLocationRemote)EJBFactory.lookup("openelis/StorageLocationBean/remote");
    		
    		
    		StorageLocationDO storageLocDO = remote.getStorageLocAndUnlock(rpc.entityKey, SessionManager.getSession().getId());
    
    //		set the fields in the RPC
    		setFieldsInRPC(rpc, storageLocDO);
            
    //		load the children
            List childrenList = remote.getStorageLocChildren(rpc.entityKey);
            //need to build the children table now...
            TableDataModel<TableDataRow<Integer>> rmodel = fillChildrenTable(rpc.childStorageLocsTable.getValue(),childrenList);
            rpc.childStorageLocsTable.setValue(rmodel);
            
    		return rpc;  
    	}

    public StorageLocationForm fetch(StorageLocationForm rpc) throws RPCException {
//		remote interface to call the storage loc bean
		StorageLocationRemote remote = (StorageLocationRemote)EJBFactory.lookup("openelis/StorageLocationBean/remote");
		
		StorageLocationDO storageLocDO = remote.getStorageLoc(rpc.entityKey);
		
//		set the fields in the RPC
		setFieldsInRPC(rpc, storageLocDO);
		
//		load the children
        List childrenList = remote.getStorageLocChildren(rpc.entityKey);
        //need to build the children table now...
        TableDataModel<TableDataRow<Integer>> rmodel = fillChildrenTable(rpc.childStorageLocsTable.getValue(),childrenList);
        rpc.childStorageLocsTable.setValue(rmodel);

		return rpc;
	}

	public StorageLocationForm fetchForUpdate(StorageLocationForm rpc) throws RPCException {
//		remote interface to call the storage loc bean
		StorageLocationRemote remote = (StorageLocationRemote)EJBFactory.lookup("openelis/StorageLocationBean/remote");
		StorageLocationDO storageLocDO = new StorageLocationDO();
		
		try{
			storageLocDO = remote.getStorageLocAndLock(rpc.entityKey, SessionManager.getSession().getId());
		}catch(Exception e){
			throw new RPCException(e.getMessage());
		}
		
//		set the fields in the RPC
		setFieldsInRPC(rpc, storageLocDO);
		
//		load the children
        List childrenList = remote.getStorageLocChildren(rpc.entityKey);
        //need to build the children table now...
        TableDataModel<TableDataRow<Integer>> rmodel = fillChildrenTable(rpc.childStorageLocsTable.getValue(),childrenList);
        rpc.childStorageLocsTable.setValue(rmodel);

		return rpc;
	}
    
    public StorageLocationForm getScreen(StorageLocationForm rpc) throws RPCException {
        rpc.xml = ServiceUtils.getXML(Constants.APP_ROOT+"/Forms/storageLocation.xsl");

        return rpc;
    }

    public TableDataModel<TableDataRow<Integer>> fillChildrenTable(TableDataModel<TableDataRow<Integer>> childModel, List childrenList){
    		try 
            {
    			childModel.clear();
    			
    			for(int iter = 0;iter < childrenList.size();iter++) {
    				StorageLocationDO slDO = (StorageLocationDO)childrenList.get(iter);
    
    	               TableDataRow<Integer> row = childModel.createNewSet();
    	               row.key = slDO.getId();
    	               
    //		       		we need to create a dataset for the storage unit auto complete
    		       		if(slDO.getStorageUnitId() == null)
    		       			row.getCells().get(0).setValue(null);
    		       		else{
                             TableDataModel<TableDataRow<Integer>> unitModel = new TableDataModel<TableDataRow<Integer>>();
                             unitModel.add(new TableDataRow<Integer>(slDO.getStorageUnitId(),new StringObject(slDO.getStorageUnit())));
                             ((DropDownField<Integer>)row.getCells().get(0)).setModel(unitModel);
                             row.getCells().get(0).setValue(unitModel.get(0));
    		       		}
    		       		
    	               row.getCells().get(1).setValue(slDO.getLocation());
    	       		
    	       		row.getCells().get(2).setValue(slDO.getIsAvailable());
    	                
    	            childModel.add(row);
    	       } 
    			
            } catch (Exception e) {
    
                e.printStackTrace();
                return null;
            }		
    		
    		return childModel;
    	}

    public TableDataModel getMatches(String cat, TableDataModel model, String match, HashMap params) {
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
            TableDataModel<TableDataRow<Integer>> unitModel = new TableDataModel<TableDataRow<Integer>>();
            unitModel.add(new TableDataRow<Integer>(storageLocDO.getStorageUnitId(),new StringObject(storageLocDO.getStorageUnit())));
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
	
	private List getChildStorageLocsFromRPC(TableDataModel<TableDataRow<Integer>> childTable){
		List storageLocationChildren = new ArrayList();
        List<TableDataRow<Integer>> deletedRows = childTable.getDeletions();
		
		for(int i=0; i<childTable.size(); i++){
			StorageLocationDO childDO = new StorageLocationDO();
			TableDataRow<Integer> row = childTable.get(i);
			
			//parent data
			Integer id = row.key;
			
			childDO.setId(id);
			childDO.setStorageUnitId((Integer)((DropDownField)row.getCells().get(0)).getSelectedKey());
			childDO.setLocation((String)row.getCells().get(1).getValue());
			childDO.setIsAvailable((String)row.getCells().get(2).getValue());
				
			storageLocationChildren.add(childDO);	
		}
        
		if(deletedRows != null){
            for(int j=0; j<deletedRows.size(); j++){
                TableDataRow<Integer> deletedRow = deletedRows.get(j);
                if(deletedRow.key != null){
                    StorageLocationDO childDO = new StorageLocationDO();
                    childDO.setDelete(true);
                    childDO.setId(deletedRow.key);
                    
                    storageLocationChildren.add(childDO);
                }
            }
		}
        
		return storageLocationChildren;
	}
    
	private TableDataModel<TableDataRow<Integer>> getStorageUnitMatches(String match){
		StorageUnitRemote remote = (StorageUnitRemote)EJBFactory.lookup("openelis/StorageUnitBean/remote");
		TableDataModel<TableDataRow<Integer>> dataModel = new TableDataModel<TableDataRow<Integer>>();

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
			
			TableDataRow<Integer> data = new TableDataRow<Integer>(id,
                                                                   new FieldType[] {
                                                                                    new StringObject(desc),
                                                                                    new StringObject(category)
                                                                   }
                                         );
			
			//add the dataset to the datamodel
			dataModel.add(data);
		}
		return dataModel;
	}
	
	private void setRpcErrors(List exceptionList, TableField childTable, StorageLocationForm form){
        HashMap<String,AbstractField> map = null;
        if(exceptionList.size() > 0)
            map = FormUtil.createFieldMap(form);
        for (int i=0; i<exceptionList.size();i++) {
            //if the error is inside the org contacts table
            if(exceptionList.get(i) instanceof TableFieldErrorException){
                int rowindex = ((TableFieldErrorException)exceptionList.get(i)).getRowIndex();
                childTable.getField(rowindex,((TableFieldErrorException)exceptionList.get(i)).getFieldName())
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
