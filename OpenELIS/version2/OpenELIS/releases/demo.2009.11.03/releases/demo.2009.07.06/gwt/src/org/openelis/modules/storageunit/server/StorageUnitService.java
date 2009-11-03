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
package org.openelis.modules.storageunit.server;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.openelis.domain.IdNameDO;
import org.openelis.domain.StorageUnitDO;
import org.openelis.gwt.common.FieldErrorException;
import org.openelis.gwt.common.Form;
import org.openelis.gwt.common.FormErrorException;
import org.openelis.gwt.common.LastPageException;
import org.openelis.gwt.common.Query;
import org.openelis.gwt.common.RPCException;
import org.openelis.gwt.common.ValidationErrorsList;
import org.openelis.gwt.common.data.AbstractField;
import org.openelis.gwt.common.data.StringObject;
import org.openelis.gwt.common.data.TableDataModel;
import org.openelis.gwt.common.data.TableDataRow;
import org.openelis.gwt.server.ServiceUtils;
import org.openelis.gwt.services.AppScreenFormServiceInt;
import org.openelis.metamap.StorageUnitMetaMap;
import org.openelis.modules.storageunit.client.StorageUnitForm;
import org.openelis.persistence.EJBFactory;
import org.openelis.remote.StorageUnitRemote;
import org.openelis.server.constants.Constants;
import org.openelis.util.FormUtil;
import org.openelis.util.SessionManager;
import org.openelis.util.UTFResource;

public class StorageUnitService implements AppScreenFormServiceInt<StorageUnitForm,Query<TableDataRow<Integer>>> {

	private static final int leftTableRowsPerPage = 10;

    private UTFResource openElisConstants= UTFResource.getBundle((String)SessionManager.getSession().getAttribute("locale"));

    private static final StorageUnitMetaMap StorageUnitMeta = new StorageUnitMetaMap();
    
	public Query<TableDataRow<Integer>> commitQuery(Query<TableDataRow<Integer>> query) throws RPCException {
        List storageUnits = new ArrayList();
		// if the rpc is null then we need to get the page
		/*if (qList == null) {
            qList = (ArrayList<AbstractField>)SessionManager.getSession().getAttribute("StorageUnitQuery");

			if (qList == null)
				throw new QueryException(openElisConstants
						.getString("queryExpiredException"));

			
			StorageUnitRemote remote = (StorageUnitRemote) EJBFactory
					.lookup("openelis/StorageUnitBean/remote");
			try {
				storageUnits = remote.query(qList,
						(model.getPage() * leftTableRowsPerPage),
						leftTableRowsPerPage + 1);
			} catch (Exception e) {
				if (e instanceof LastPageException) {
					throw new LastPageException(openElisConstants
							.getString("lastPageException"));
				} else {
					throw new RPCException(e.getMessage());
				}
			}

		} else {*/
			StorageUnitRemote remote = (StorageUnitRemote) EJBFactory
					.lookup("openelis/StorageUnitBean/remote");

			

			try {
                storageUnits = remote.query(query.fields, query.page*leftTableRowsPerPage, leftTableRowsPerPage);
            }catch(LastPageException e) {
                throw new LastPageException(openElisConstants.getString("lastPageException"));
			} catch (Exception e) {
				throw new RPCException(e.getMessage());
			}

            //need to save the rpc used to the encache
           // SessionManager.getSession().setAttribute("StorageUnitQuery", qList);
		//}
        
        int i = 0;
        if(query.results == null)
            query.results = new TableDataModel<TableDataRow<Integer>>();
        else
            query.results.clear();
        while (i < storageUnits.size() && i < leftTableRowsPerPage) {
            IdNameDO resultDO = (IdNameDO) storageUnits.get(i);
            query.results.add(new TableDataRow<Integer>(resultDO.getId(),new StringObject(resultDO.getName())));
            i++;
        }

		return query;
	}

	public StorageUnitForm commitUpdate(StorageUnitForm rpc)
			throws RPCException {
		// remote interface to call the storage unit bean
		StorageUnitRemote remote = (StorageUnitRemote) EJBFactory
				.lookup("openelis/StorageUnitBean/remote");
		StorageUnitDO newStorageUnitDO = new StorageUnitDO();

		// build the DO from the form
		newStorageUnitDO = getStorageUnitDOFromRPC(rpc);
		
		// send the changes to the database
		try{
			remote.updateStorageUnit(newStorageUnitDO);
		}catch(Exception e){
            if(e instanceof ValidationErrorsList){
                setRpcErrors(((ValidationErrorsList)e).getErrorList(), rpc);
                return rpc;
            }else
                throw new RPCException(e.getMessage());
        }
		
		// set the fields in the RPC
		setFieldsInRPC(rpc, newStorageUnitDO);

		return rpc;
	}

	public StorageUnitForm commitAdd(StorageUnitForm rpc)
    		throws RPCException {
    	// remote interface to call the storageunit bean
    	StorageUnitRemote remote = (StorageUnitRemote) EJBFactory.lookup("openelis/StorageUnitBean/remote");
    	StorageUnitDO newStorageUnitDO = new StorageUnitDO();
    
    	// build the storage unit DO from the form
    	newStorageUnitDO = getStorageUnitDOFromRPC(rpc);
    
    	// send the changes to the database
    	Integer storageUnitId;
    	try{
    		storageUnitId = (Integer) remote.updateStorageUnit(newStorageUnitDO);
    	}catch(Exception e){
            if(e instanceof ValidationErrorsList){
                setRpcErrors(((ValidationErrorsList)e).getErrorList(), rpc);
                return rpc;
            }else
                throw new RPCException(e.getMessage());
        }
    
        newStorageUnitDO.setId(storageUnitId);
    
    	// set the fields in the RPC
    	setFieldsInRPC(rpc, newStorageUnitDO);
    
    	return rpc;
    }

    public StorageUnitForm commitDelete(StorageUnitForm rpc)
			throws RPCException {
		// remote interface to call the storage unit bean
		StorageUnitRemote remote = (StorageUnitRemote) EJBFactory.lookup("openelis/StorageUnitBean/remote");
		
		try {
			remote.deleteStorageUnit(rpc.entityKey);

		}catch(Exception e){
            if(e instanceof ValidationErrorsList){
                setRpcErrors(((ValidationErrorsList)e).getErrorList(), rpc);
                return rpc;
            }else
                throw new RPCException(e.getMessage());
        }	

		setFieldsInRPC(rpc, new StorageUnitDO());

		return rpc;
	}

	public StorageUnitForm abort(StorageUnitForm rpc) throws RPCException {
    	// remote interface to call the storage unit bean
    	StorageUnitRemote remote = (StorageUnitRemote) EJBFactory
    			.lookup("openelis/StorageUnitBean/remote");
    
    	StorageUnitDO storageUnitDO = remote
    			.getStorageUnitAndUnlock(rpc.entityKey, SessionManager.getSession().getId());
    
    	// set the fields in the RPC
    	setFieldsInRPC(rpc, storageUnitDO);
    
    	return rpc;
    }

    public StorageUnitForm fetch(StorageUnitForm rpc) throws RPCException {
		// remote interface to call the storage unit bean
		StorageUnitRemote remote = (StorageUnitRemote) EJBFactory
				.lookup("openelis/StorageUnitBean/remote");

		StorageUnitDO storageUnitDO = remote.getStorageUnit(rpc.entityKey);

		// set the fields in the RPC
		setFieldsInRPC(rpc, storageUnitDO);

		return rpc;
	}

	public StorageUnitForm fetchForUpdate(StorageUnitForm rpc)
			throws RPCException {
		// remote interface to call the storage unit bean
		StorageUnitRemote remote = (StorageUnitRemote) EJBFactory
				.lookup("openelis/StorageUnitBean/remote");

		StorageUnitDO storageUnitDO = new StorageUnitDO();
		try {
			storageUnitDO = remote.getStorageUnitAndLock(rpc.entityKey, SessionManager.getSession().getId());
		} catch (Exception e) {
			throw new RPCException(e.getMessage());
		}

		// set the fields in the RPC
		setFieldsInRPC(rpc, storageUnitDO);

		return rpc;
	}
    
    public StorageUnitForm getScreen(StorageUnitForm rpc) throws RPCException {
        rpc.xml = ServiceUtils.getXML(Constants.APP_ROOT+"/Forms/storageUnit.xsl");
        
        return rpc;
    }
    
	private void setRpcErrors(List exceptionList, StorageUnitForm form){
        HashMap<String,AbstractField> map = null;
        if(exceptionList.size() > 0)
            map = FormUtil.createFieldMap(form);
		//we need to get the keys and look them up in the resource bundle for internationalization
		for (int i=0; i<exceptionList.size();i++) {
			if(exceptionList.get(i) instanceof FieldErrorException)
			map.get(((FieldErrorException)exceptionList.get(i)).getFieldName()).addError(openElisConstants.getString(((FieldErrorException)exceptionList.get(i)).getMessage()));
			else if(exceptionList.get(i) instanceof FormErrorException)
				form.addError(openElisConstants.getString(((FormErrorException)exceptionList.get(i)).getMessage()));
		}	
		form.status = Form.Status.invalid;
    }

    private void setFieldsInRPC(StorageUnitForm form, StorageUnitDO storageUnitDO) {
        form.id.setValue(storageUnitDO.getId());
        
        if(storageUnitDO.getCategory() != null)
            form.category.setValue(new TableDataRow<String>(storageUnitDO.getCategory()));
        
    	form.description.setValue(storageUnitDO.getDescription());
    	form.isSingular.setValue(storageUnitDO.getIsSingular());
    }

    private StorageUnitDO getStorageUnitDOFromRPC(StorageUnitForm form) {
    	StorageUnitDO newStorageUnitDO = new StorageUnitDO();
    
    	newStorageUnitDO.setId(form.id.getValue());
    	newStorageUnitDO.setCategory((String)form.category.getSelectedKey());
    	newStorageUnitDO.setDescription(form.description.getValue());
    	newStorageUnitDO.setIsSingular(form.isSingular.getValue());
    
    	return newStorageUnitDO;
    }
}
