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
import org.openelis.gwt.common.Form;
import org.openelis.gwt.common.FormErrorException;
import org.openelis.gwt.common.LastPageException;
import org.openelis.gwt.common.QueryException;
import org.openelis.gwt.common.RPCException;
import org.openelis.gwt.common.ValidationErrorsList;
import org.openelis.gwt.common.data.AbstractField;
import org.openelis.gwt.common.data.DataModel;
import org.openelis.gwt.common.data.DataSet;
import org.openelis.gwt.common.data.FieldType;
import org.openelis.gwt.common.data.StringObject;
import org.openelis.gwt.server.ServiceUtils;
import org.openelis.gwt.services.AppScreenFormServiceInt;
import org.openelis.gwt.services.AutoCompleteServiceInt;
import org.openelis.metamap.AnalyteMetaMap;
import org.openelis.modules.analyte.client.AnalyteForm;
import org.openelis.modules.analyte.client.AnalyteRPC;
import org.openelis.persistence.EJBFactory;
import org.openelis.remote.AnalyteRemote;
import org.openelis.server.constants.Constants;
import org.openelis.util.SessionManager;
import org.openelis.util.UTFResource;

public class AnalyteService implements AppScreenFormServiceInt<AnalyteRPC,Integer>, AutoCompleteServiceInt {

	private static final int leftTableRowsPerPage = 9;
	
    private UTFResource openElisConstants= UTFResource.getBundle((String)SessionManager.getSession().getAttribute("locale"));
    
    private static final AnalyteMetaMap Meta = new AnalyteMetaMap();
    
	public DataModel<Integer> commitQuery(Form form, DataModel<Integer> model) throws RPCException {
        List analyteNames = new ArrayList();
    //		if the rpc is null then we need to get the page
    		if(form == null){
    //			need to get the query rpc out of the cache
                form = (Form)SessionManager.getSession().getAttribute("AnalyteQuery");
    
    	        if(form == null)
    	        	throw new QueryException(openElisConstants.getString("queryExpiredException"));

    	        AnalyteRemote remote = (AnalyteRemote)EJBFactory.lookup("openelis/AnalyteBean/remote");
    	        try{
                    analyteNames = remote.query(form.getFieldMap(), (model.getPage()*leftTableRowsPerPage), leftTableRowsPerPage+1);
    	        }catch(Exception e){
    	        	if(e instanceof LastPageException){
    	        		throw new LastPageException(openElisConstants.getString("lastPageException"));
    	        	}else{
    	        		throw new RPCException(e.getMessage());	
    	        	}
    	        }

    		}else{
    			AnalyteRemote remote = (AnalyteRemote)EJBFactory.lookup("openelis/AnalyteBean/remote");
    			
    			HashMap<String,AbstractField> fields = form.getFieldMap();
    			
    			try{
    				analyteNames = remote.query(fields,0,leftTableRowsPerPage);
    
        		}catch(Exception e){
        			throw new RPCException(e.getMessage());
        		}
                
                //need to save the rpc used to the encache
                SessionManager.getSession().setAttribute("AnalyteQuery", form);
    		}
    		
            int i=0;
            if(model == null)
                model = new DataModel<Integer>();
            else
                model.clear();
            while(i < analyteNames.size() && i < leftTableRowsPerPage) {
                IdNameDO resultDO = (IdNameDO)analyteNames.get(i);
                //org id
                Integer idResult = resultDO.getId();
                //org name
                String nameResult = resultDO.getName();

                DataSet<Integer> row = new DataSet<Integer>();
                StringObject name = new StringObject(nameResult);                
                row.setKey(idResult);         
                row.add(name);
                model.add(row);
                i++;
             } 
            
    		return model;
    	}

    public AnalyteRPC commitAdd(AnalyteRPC rpc) throws RPCException {
//		 remote interface to call the analyte bean
		AnalyteRemote remote = (AnalyteRemote) EJBFactory.lookup("openelis/AnalyteBean/remote");
		AnalyteDO newAnalyteDO = new AnalyteDO();

		// build the analyte DO from the form
		newAnalyteDO = getAnalyteDOFromRPC(rpc.form);
		
		// send the changes to the database
		Integer analyteId;
		try{
			analyteId = (Integer) remote.updateAnalyte(newAnalyteDO);
		}catch(Exception e){
            if(e instanceof ValidationErrorsList){
                setRpcErrors(((ValidationErrorsList)e).getErrorList(), rpc.form);
                return rpc;
            }else
                throw new RPCException(e.getMessage());
		}

		newAnalyteDO.setId(analyteId);
        
		// set the fields in the RPC
		setFieldsInRPC(rpc.form, newAnalyteDO);

		return rpc;
	}

	public AnalyteRPC commitUpdate(AnalyteRPC rpc) throws RPCException {
    //		remote interface to call the analyte bean
    		AnalyteRemote remote = (AnalyteRemote)EJBFactory.lookup("openelis/AnalyteBean/remote");
    		AnalyteDO newAnalyteDO = new AnalyteDO();
    
    		//build the AnalyteDO from the form
    		newAnalyteDO = getAnalyteDOFromRPC(rpc.form);
    		
    		//send the changes to the database
    		try{
    			remote.updateAnalyte(newAnalyteDO);
    		}catch(Exception e){
                if(e instanceof ValidationErrorsList){
                    setRpcErrors(((ValidationErrorsList)e).getErrorList(), rpc.form);
                    return rpc;
                }else
                    throw new RPCException(e.getMessage());
    		}
    
    //		set the fields in the RPC
    		setFieldsInRPC(rpc.form, newAnalyteDO);
    		
    		return rpc;
    	}

    public AnalyteRPC commitDelete(AnalyteRPC rpc) throws RPCException {
//		remote interface to call the analyte bean
		AnalyteRemote remote = (AnalyteRemote)EJBFactory.lookup("openelis/AnalyteBean/remote");
		
		try {
			remote.deleteAnalyte(rpc.key);
			
		} catch (Exception e) {
            if(e instanceof ValidationErrorsList){
                setRpcErrors(((ValidationErrorsList)e).getErrorList(), rpc.form);
                return rpc;
            }else
                throw new RPCException(e.getMessage());
		}	
		
		setFieldsInRPC(rpc.form, new AnalyteDO());
		
		return rpc;
	}

	public AnalyteRPC abort(AnalyteRPC rpc) throws RPCException {
    //		remote interface to call the analyte bean
    		AnalyteRemote remote = (AnalyteRemote)EJBFactory.lookup("openelis/AnalyteBean/remote");
    		
    		
    		AnalyteDO analyteDO = remote.getAnalyteAndUnlock(rpc.key, SessionManager.getSession().getId());
    
    //		set the fields in the RPC
    		setFieldsInRPC(rpc.form, analyteDO);
            
    		return rpc;  
    	}

    public AnalyteRPC fetch(AnalyteRPC rpc) throws RPCException {
//		remote interface to call the storage unit bean
		AnalyteRemote remote = (AnalyteRemote)EJBFactory.lookup("openelis/AnalyteBean/remote");
		
		AnalyteDO analyteDO = remote.getAnalyte(rpc.key);
		
//		set the fields in the RPC
		setFieldsInRPC(rpc.form, analyteDO);
		
		return rpc;
	}

	public AnalyteRPC fetchForUpdate(AnalyteRPC rpc) throws RPCException {
//		 remote interface to call the analyte bean
		AnalyteRemote remote = (AnalyteRemote) EJBFactory.lookup("openelis/AnalyteBean/remote");

		AnalyteDO analyteDO = new AnalyteDO();
		try {
			analyteDO = remote.getAnalyteAndLock(rpc.key, SessionManager.getSession().getId());
		} catch (Exception e) {
			throw new RPCException(e.getMessage());
		}

		// set the fields in the RPC
		setFieldsInRPC(rpc.form, analyteDO);

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
    
    public AnalyteRPC getScreen(AnalyteRPC rpc) throws RPCException {
        rpc.xml = ServiceUtils.getXML(Constants.APP_ROOT+"/Forms/analyte.xsl");
        
        return rpc;
    }

    public DataModel getDisplay(String cat, DataModel model, AbstractField value) throws RPCException {
    	return null;
    }

    public DataModel getMatches(String cat, DataModel model, String match, HashMap params) throws RPCException {
    	if(cat.equals("parentAnalyte"))
    		return getParentAnalyteMatches(match);
    	
    	return null;		
    }

    private void setFieldsInRPC(AnalyteForm form, AnalyteDO analyteDO){
    	form.id.setValue(analyteDO.getId());
    	form.name.setValue(analyteDO.getName());
    	form.isActive.setValue(analyteDO.getIsActive());
    	form.externalId.setValue(analyteDO.getExternalId());
		
//		we need to create a dataset for the parent organization auto complete
		if(analyteDO.getParentAnalyteId() == null)
			form.parentName.clear();
		else{
            DataModel<Integer> parentModel = new DataModel<Integer>();
            parentModel.add(new DataSet<Integer>(analyteDO.getParentAnalyteId(),new StringObject(analyteDO.getParentAnalyte())));
            form.parentName.setModel(parentModel);
            form.parentName.setValue(parentModel.get(0));
		}
	}
	
	private AnalyteDO getAnalyteDOFromRPC(AnalyteForm form) {
		AnalyteDO newAnalyteDO = new AnalyteDO();

		newAnalyteDO.setId(form.id.getValue());
		newAnalyteDO.setName(form.name.getValue());
		newAnalyteDO.setIsActive(form.isActive.getValue());
		newAnalyteDO.setParentAnalyteId((Integer) form.parentName.getSelectedKey());
        newAnalyteDO.setParentAnalyte((String) form.parentName.getTextValue());
		newAnalyteDO.setExternalId(form.externalId.getValue());		

		return newAnalyteDO;
	}
	
	private DataModel getParentAnalyteMatches(String match){
		AnalyteRemote remote = (AnalyteRemote)EJBFactory.lookup("openelis/AnalyteBean/remote");
		DataModel<Integer> dataModel = new DataModel<Integer>();

		//lookup by name
		List autoCompleteList = remote.autoCompleteLookupByName(match+"%", 10);
		Iterator itr = autoCompleteList.iterator();
		
		while(itr.hasNext()){
            IdNameDO resultDO = (IdNameDO) itr.next();
			//parent id
			Integer analyteId = resultDO.getId();
			//parent name
			String name = resultDO.getName();			
			
			DataSet<Integer> data = new DataSet<Integer>();
			//hidden id
			data.setKey(analyteId);
			//columns
			StringObject nameObject = new StringObject();
			nameObject.setValue(name);
			data.add(nameObject);
			
			//add the dataset to the datamodel
			dataModel.add(data);
		}
		
		return dataModel;		
	}

	private void setRpcErrors(List exceptionList, Form form){
		//we need to get the keys and look them up in the resource bundle for internationalization
		for (int i=0; i<exceptionList.size();i++) {
			if(exceptionList.get(i) instanceof FieldErrorException)
			form.getField(((FieldErrorException)exceptionList.get(i)).getFieldName()).addError(openElisConstants.getString(((FieldErrorException)exceptionList.get(i)).getMessage()));
			else if(exceptionList.get(i) instanceof FormErrorException)
				form.addError(openElisConstants.getString(((FormErrorException)exceptionList.get(i)).getMessage()));
		}	
		form.status = Form.Status.invalid;
    }
}
