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
package org.openelis.modules.analyte.server;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.openelis.domain.AnalyteDO;
import org.openelis.domain.IdNameDO;
import org.openelis.gwt.common.FieldErrorException;
import org.openelis.gwt.common.FormErrorException;
import org.openelis.gwt.common.FormRPC;
import org.openelis.gwt.common.LastPageException;
import org.openelis.gwt.common.QueryException;
import org.openelis.gwt.common.RPCException;
import org.openelis.gwt.common.ValidationErrorsList;
import org.openelis.gwt.common.FormRPC.Status;
import org.openelis.gwt.common.data.AbstractField;
import org.openelis.gwt.common.data.DataModel;
import org.openelis.gwt.common.data.DataObject;
import org.openelis.gwt.common.data.DataSet;
import org.openelis.gwt.common.data.DropDownField;
import org.openelis.gwt.common.data.NumberObject;
import org.openelis.gwt.common.data.StringObject;
import org.openelis.gwt.server.ServiceUtils;
import org.openelis.gwt.services.AppScreenFormServiceInt;
import org.openelis.gwt.services.AutoCompleteServiceInt;
import org.openelis.metamap.AnalyteMetaMap;
import org.openelis.persistence.EJBFactory;
import org.openelis.remote.AnalyteRemote;
import org.openelis.server.constants.Constants;
import org.openelis.util.SessionManager;
import org.openelis.util.UTFResource;

public class AnalyteService implements AppScreenFormServiceInt<FormRPC, DataSet, DataModel>, AutoCompleteServiceInt {

	private static final int leftTableRowsPerPage = 9;
	
    private UTFResource openElisConstants= UTFResource.getBundle((String)SessionManager.getSession().getAttribute("locale"));
    
    private static final AnalyteMetaMap Meta = new AnalyteMetaMap();
    
	public DataModel commitQuery(FormRPC rpcSend, DataModel model) throws RPCException {
        List analyteNames = new ArrayList();
    //		if the rpc is null then we need to get the page
    		if(rpcSend == null){
    //			need to get the query rpc out of the cache
                FormRPC rpc = (FormRPC)SessionManager.getSession().getAttribute("AnalyteQuery");
    
    	        if(rpc == null)
    	        	throw new QueryException(openElisConstants.getString("queryExpiredException"));

    	        AnalyteRemote remote = (AnalyteRemote)EJBFactory.lookup("openelis/AnalyteBean/remote");
    	        try{
                    analyteNames = remote.query(rpc.getFieldMap(), (model.getPage()*leftTableRowsPerPage), leftTableRowsPerPage+1);
    	        }catch(Exception e){
    	        	if(e instanceof LastPageException){
    	        		throw new LastPageException(openElisConstants.getString("lastPageException"));
    	        	}else{
    	        		throw new RPCException(e.getMessage());	
    	        	}
    	        }

    		}else{
    			AnalyteRemote remote = (AnalyteRemote)EJBFactory.lookup("openelis/AnalyteBean/remote");
    			
    			HashMap<String,AbstractField> fields = rpcSend.getFieldMap();
    			
    			try{
    				analyteNames = remote.query(fields,0,leftTableRowsPerPage);
    
        		}catch(Exception e){
        			throw new RPCException(e.getMessage());
        		}
                
                //need to save the rpc used to the encache
                SessionManager.getSession().setAttribute("AnalyteQuery", rpcSend);
    		}
    		
            int i=0;
            if(model == null)
                model = new DataModel();
            else
                model.clear();
            while(i < analyteNames.size() && i < leftTableRowsPerPage) {
                IdNameDO resultDO = (IdNameDO)analyteNames.get(i);
                //org id
                Integer idResult = resultDO.getId();
                //org name
                String nameResult = resultDO.getName();

                DataSet row = new DataSet();
                NumberObject id = new NumberObject(idResult);
                StringObject name = new StringObject(nameResult);
                
                row.setKey(id);         
                row.add(name);
                model.add(row);
                i++;
             } 
            
    		return model;
    	}

    public FormRPC commitAdd(FormRPC rpcSend, FormRPC rpcReturn) throws RPCException {
//		 remote interface to call the analyte bean
		AnalyteRemote remote = (AnalyteRemote) EJBFactory.lookup("openelis/AnalyteBean/remote");
		AnalyteDO newAnalyteDO = new AnalyteDO();

		// build the analyte DO from the form
		newAnalyteDO = getAnalyteDOFromRPC(rpcSend);
		
		// send the changes to the database
		Integer analyteId;
		try{
			analyteId = (Integer) remote.updateAnalyte(newAnalyteDO);
		}catch(Exception e){
            if(e instanceof ValidationErrorsList){
                setRpcErrors(((ValidationErrorsList)e).getErrorList(), rpcSend);
                return rpcSend;
            }else
                throw new RPCException(e.getMessage());
		}

		newAnalyteDO.setId(analyteId);
        
		// set the fields in the RPC
		setFieldsInRPC(rpcReturn, newAnalyteDO);

		return rpcReturn;
	}

	public FormRPC commitUpdate(FormRPC rpcSend, FormRPC rpcReturn) throws RPCException {
    //		remote interface to call the analyte bean
    		AnalyteRemote remote = (AnalyteRemote)EJBFactory.lookup("openelis/AnalyteBean/remote");
    		AnalyteDO newAnalyteDO = new AnalyteDO();
    
    		//build the AnalyteDO from the form
    		newAnalyteDO = getAnalyteDOFromRPC(rpcSend);
    		
    		//send the changes to the database
    		try{
    			remote.updateAnalyte(newAnalyteDO);
    		}catch(Exception e){
                if(e instanceof ValidationErrorsList){
                    setRpcErrors(((ValidationErrorsList)e).getErrorList(), rpcSend);
                    return rpcSend;
                }else
                    throw new RPCException(e.getMessage());
    		}
    
    //		set the fields in the RPC
    		setFieldsInRPC(rpcReturn, newAnalyteDO);
    		
    		return rpcReturn;
    	}

    public FormRPC commitDelete(DataSet key, FormRPC rpcReturn) throws RPCException {
//		remote interface to call the analyte bean
		AnalyteRemote remote = (AnalyteRemote)EJBFactory.lookup("openelis/AnalyteBean/remote");
		
		try {
			remote.deleteAnalyte((Integer)((DataObject)key.getKey()).getValue());
			
		} catch (Exception e) {
            if(e instanceof ValidationErrorsList){
                setRpcErrors(((ValidationErrorsList)e).getErrorList(), rpcReturn);
                return rpcReturn;
            }else
                throw new RPCException(e.getMessage());
		}	
		
		setFieldsInRPC(rpcReturn, new AnalyteDO());
		
		return rpcReturn;
	}

	public FormRPC abort(DataSet key, FormRPC rpcReturn) throws RPCException {
    //		remote interface to call the analyte bean
    		AnalyteRemote remote = (AnalyteRemote)EJBFactory.lookup("openelis/AnalyteBean/remote");
    		
    		
    		AnalyteDO analyteDO = remote.getAnalyteAndUnlock((Integer)((DataObject)key.getKey()).getValue(), SessionManager.getSession().getId());
    
    //		set the fields in the RPC
    		setFieldsInRPC(rpcReturn, analyteDO);
            
    		return rpcReturn;  
    	}

    public FormRPC fetch(DataSet key, FormRPC rpcReturn) throws RPCException {
//		remote interface to call the storage unit bean
		AnalyteRemote remote = (AnalyteRemote)EJBFactory.lookup("openelis/AnalyteBean/remote");
		
		AnalyteDO analyteDO = remote.getAnalyte((Integer)((DataObject)key.getKey()).getValue());
		
//		set the fields in the RPC
		setFieldsInRPC(rpcReturn, analyteDO);
		
		return rpcReturn;
	}

	public FormRPC fetchForUpdate(DataSet key, FormRPC rpcReturn) throws RPCException {
//		 remote interface to call the analyte bean
		AnalyteRemote remote = (AnalyteRemote) EJBFactory.lookup("openelis/AnalyteBean/remote");

		AnalyteDO analyteDO = new AnalyteDO();
		try {
			analyteDO = remote.getAnalyteAndLock((Integer)((DataObject)key.getKey()).getValue(), SessionManager.getSession().getId());
		} catch (Exception e) {
			throw new RPCException(e.getMessage());
		}

		// set the fields in the RPC
		setFieldsInRPC(rpcReturn, analyteDO);

		return rpcReturn;
	}

	public String getXML() throws RPCException {
    	return ServiceUtils.getXML(Constants.APP_ROOT+"/Forms/analyte.xsl");
    }

    public HashMap getXMLData() throws RPCException {
    	// TODO Auto-generated method stub
    	return null;
    }

    public HashMap getXMLData(HashMap args) throws RPCException {
    	// TODO Auto-generated method stub
    	return null;
    }

    public DataModel getDisplay(String cat, DataModel model, AbstractField value) throws RPCException {
    	return null;
    }

    public DataModel getMatches(String cat, DataModel model, String match, HashMap params) throws RPCException {
    	if(cat.equals("parentAnalyte"))
    		return getParentAnalyteMatches(match);
    	
    	return null;		
    }

    private void setFieldsInRPC(FormRPC rpcReturn, AnalyteDO analyteDO){
		rpcReturn.setFieldValue(Meta.getId(), analyteDO.getId());
		rpcReturn.setFieldValue(Meta.getName(), (analyteDO.getName() == null ? null : analyteDO.getName()));
		rpcReturn.setFieldValue(Meta.getIsActive(), (analyteDO.getIsActive() == null ? null : analyteDO.getIsActive()));
		rpcReturn.setFieldValue(Meta.getAnalyteGroupId(), analyteDO.getAnalyteGroup());
		rpcReturn.setFieldValue(Meta.getExternalId(), (analyteDO.getExternalId() == null ? null : analyteDO.getExternalId()));
		
//		we need to create a dataset for the parent organization auto complete
		if(analyteDO.getParentAnalyteId() == null)
			rpcReturn.setFieldValue(Meta.getParentAnalyte().getName(), null);
		else{
            DataModel parentModel = new DataModel();
            parentModel.add(new NumberObject(analyteDO.getParentAnalyteId()),new StringObject(analyteDO.getParentAnalyte()));
            ((DropDownField)rpcReturn.getField(Meta.getParentAnalyte().getName())).setModel(parentModel);
            rpcReturn.setFieldValue(Meta.getParentAnalyte().getName(), parentModel.get(0));
		}
	}
	
	private AnalyteDO getAnalyteDOFromRPC(FormRPC rpcSend) {
		AnalyteDO newAnalyteDO = new AnalyteDO();

		newAnalyteDO.setId((Integer) rpcSend.getFieldValue(Meta.getId()));
		newAnalyteDO.setName(((String) rpcSend.getFieldValue(Meta.getName())));
		newAnalyteDO.setIsActive(((String) rpcSend.getFieldValue(Meta.getIsActive())));
		newAnalyteDO.setAnalyteGroup((Integer) rpcSend.getFieldValue(Meta.getAnalyteGroupId()));
		newAnalyteDO.setParentAnalyteId((Integer) rpcSend.getFieldValue(Meta.getParentAnalyte().getId()));
        newAnalyteDO.setParentAnalyte((String)((DropDownField)rpcSend.getField(Meta.getParentAnalyte().getName())).getTextValue());
		newAnalyteDO.setExternalId(((String) rpcSend.getFieldValue(Meta.getExternalId())));		

		return newAnalyteDO;
	}
	
	private DataModel getParentAnalyteMatches(String match){
		AnalyteRemote remote = (AnalyteRemote)EJBFactory.lookup("openelis/AnalyteBean/remote");
		DataModel dataModel = new DataModel();

		//lookup by name
		List autoCompleteList = remote.autoCompleteLookupByName(match+"%", 10);
		Iterator itr = autoCompleteList.iterator();
		
		while(itr.hasNext()){
            IdNameDO resultDO = (IdNameDO) itr.next();
			//parent id
			Integer analyteId = resultDO.getId();
			//parent name
			String name = resultDO.getName();			
			
			DataSet data = new DataSet();
			//hidden id
			NumberObject idObject = new NumberObject(analyteId);
			data.setKey(idObject);
			//columns
			StringObject nameObject = new StringObject();
			nameObject.setValue(name);
			data.add(nameObject);
			
			//add the dataset to the datamodel
			dataModel.add(data);
		}
		
		return dataModel;		
	}

	private void setRpcErrors(List exceptionList, FormRPC rpcSend){
		//we need to get the keys and look them up in the resource bundle for internationalization
		for (int i=0; i<exceptionList.size();i++) {
			if(exceptionList.get(i) instanceof FieldErrorException)
			rpcSend.getField(((FieldErrorException)exceptionList.get(i)).getFieldName()).addError(openElisConstants.getString(((FieldErrorException)exceptionList.get(i)).getMessage()));
			else if(exceptionList.get(i) instanceof FormErrorException)
				rpcSend.addError(openElisConstants.getString(((FormErrorException)exceptionList.get(i)).getMessage()));
		}	
		rpcSend.status = Status.invalid;
    }
}
