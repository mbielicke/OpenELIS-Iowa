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

import org.openelis.domain.AnalyteDO;
import org.openelis.domain.IdNameDO;
import org.openelis.gwt.common.FieldErrorException;
import org.openelis.gwt.common.FormErrorException;
import org.openelis.gwt.common.LastPageException;
import org.openelis.gwt.common.QueryException;
import org.openelis.gwt.common.RPCException;
import org.openelis.gwt.common.ValidationErrorsList;
import org.openelis.gwt.common.data.deprecated.AbstractField;
import org.openelis.gwt.common.data.deprecated.FieldType;
import org.openelis.gwt.common.data.deprecated.StringObject;
import org.openelis.gwt.common.data.deprecated.TableDataModel;
import org.openelis.gwt.common.data.deprecated.TableDataRow;
import org.openelis.gwt.common.deprecated.Form;
import org.openelis.gwt.common.deprecated.Query;
import org.openelis.gwt.server.ServiceUtils;
import org.openelis.gwt.services.deprecated.AppScreenFormServiceInt;
import org.openelis.gwt.services.deprecated.AutoCompleteServiceInt;
import org.openelis.metamap.AnalyteMetaMap;
import org.openelis.modules.analyte.client.AnalyteForm;
import org.openelis.persistence.EJBFactory;
import org.openelis.remote.AnalyteRemote;
import org.openelis.server.constants.Constants;
import org.openelis.util.FormUtil;
import org.openelis.util.SessionManager;
import org.openelis.util.UTFResource;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class AnalyteService implements AppScreenFormServiceInt<AnalyteForm,Query<TableDataRow<Integer>>>, AutoCompleteServiceInt {

	private static final int leftTableRowsPerPage = 9;
	
    private UTFResource openElisConstants= UTFResource.getBundle((String)SessionManager.getSession().getAttribute("locale"));
    
    private static final AnalyteMetaMap Meta = new AnalyteMetaMap();
    
	public  Query<TableDataRow<Integer>> commitQuery(Query<TableDataRow<Integer>> query) throws RPCException {
        List analyteNames = new ArrayList();
    //		if the rpc is null then we need to get the page
            /*
    		if(qList == null){
    //			need to get the query rpc out of the cache
                qList = (ArrayList<AbstractField>)SessionManager.getSession().getAttribute("AnalyteQuery");
    
    	        if(qList == null)
    	        	throw new QueryException(openElisConstants.getString("queryExpiredException"));

    	        AnalyteRemote remote = (AnalyteRemote)EJBFactory.lookup("openelis/AnalyteBean/remote");
    	        try{
                    analyteNames = remote.query(qList, (model.getPage()*leftTableRowsPerPage), leftTableRowsPerPage+1);
    	        }catch(Exception e){
    	        	if(e instanceof LastPageException){
    	        		throw new LastPageException(openElisConstants.getString("lastPageException"));
    	        	}else{
    	        		throw new RPCException(e.getMessage());	
    	        	}
    	        }

    		}else{
            */
    			AnalyteRemote remote = (AnalyteRemote)EJBFactory.lookup("openelis/AnalyteBean/remote");
    			
    			
    			try{
    				analyteNames = remote.query(query.fields,query.page*leftTableRowsPerPage,leftTableRowsPerPage);
                }catch(LastPageException e) {
                    throw new LastPageException(openElisConstants.getString("lastPageException"));
        		}catch(Exception e){
        			throw new RPCException(e.getMessage());
        		}
                
                //need to save the rpc used to the encache
                //SessionManager.getSession().setAttribute("AnalyteQuery", qList);
    		//}
    		
            int i=0;
            if(query.results == null)
                query.results = new TableDataModel<TableDataRow<Integer>>();
            else
                query.results.clear();
            while(i < analyteNames.size() && i < leftTableRowsPerPage) {
                IdNameDO resultDO = (IdNameDO)analyteNames.get(i);
                //org id
                Integer idResult = resultDO.getId();
                //org name
                String nameResult = resultDO.getName();

                TableDataRow<Integer> row = new TableDataRow<Integer>(1);
                StringObject name = new StringObject(nameResult);                
                row.key = idResult;         
                row.cells[0] = name;
                query.results.add(row);
                i++;
             } 
            
    		return query;
    	}

    public AnalyteForm commitAdd(AnalyteForm rpc) throws RPCException {
//		 remote interface to call the analyte bean
		AnalyteRemote remote = (AnalyteRemote) EJBFactory.lookup("openelis/AnalyteBean/remote");
		AnalyteDO newAnalyteDO = new AnalyteDO();

		// build the analyte DO from the form
		newAnalyteDO = getAnalyteDOFromRPC(rpc);
		
		// send the changes to the database
		Integer analyteId;
		try{
			analyteId = (Integer) remote.updateAnalyte(newAnalyteDO, SessionManager.getSession().getId());
		}catch(Exception e){
            if(e instanceof ValidationErrorsList){
                setRpcErrors(((ValidationErrorsList)e).getErrorList(), rpc);
                return rpc;
            }else
                throw new RPCException(e.getMessage());
		}

		newAnalyteDO.setId(analyteId);
        
		// set the fields in the RPC
		setFieldsInRPC(rpc, newAnalyteDO);

		return rpc;
	}

	public AnalyteForm commitUpdate(AnalyteForm rpc) throws RPCException {
    //		remote interface to call the analyte bean
    		AnalyteRemote remote = (AnalyteRemote)EJBFactory.lookup("openelis/AnalyteBean/remote");
    		AnalyteDO newAnalyteDO = new AnalyteDO();
    
    		//build the AnalyteDO from the form
    		newAnalyteDO = getAnalyteDOFromRPC(rpc);
    		
    		//send the changes to the database
    		try{
    			remote.updateAnalyte(newAnalyteDO, SessionManager.getSession().getId());
    		}catch(Exception e){
                if(e instanceof ValidationErrorsList){
                    setRpcErrors(((ValidationErrorsList)e).getErrorList(), rpc);
                    return rpc;
                }else
                    throw new RPCException(e.getMessage());
    		}
    
    //		set the fields in the RPC
    		setFieldsInRPC(rpc, newAnalyteDO);
    		
    		return rpc;
    	}

    public AnalyteForm commitDelete(AnalyteForm rpc) throws RPCException {
//		remote interface to call the analyte bean
		AnalyteRemote remote = (AnalyteRemote)EJBFactory.lookup("openelis/AnalyteBean/remote");
		
		try {
			remote.deleteAnalyte(rpc.entityKey, SessionManager.getSession().getId());
			
		} catch (Exception e) {
            if(e instanceof ValidationErrorsList){
                setRpcErrors(((ValidationErrorsList)e).getErrorList(), rpc);
                return rpc;
            }else
                throw new RPCException(e.getMessage());
		}	
		
		setFieldsInRPC(rpc, new AnalyteDO());
		
		return rpc;
	}

	public AnalyteForm abort(AnalyteForm rpc) throws RPCException {
    //		remote interface to call the analyte bean
    		AnalyteRemote remote = (AnalyteRemote)EJBFactory.lookup("openelis/AnalyteBean/remote");
    		
    		
    		AnalyteDO analyteDO = remote.getAnalyteAndUnlock(rpc.entityKey, SessionManager.getSession().getId());
    
    //		set the fields in the RPC
    		setFieldsInRPC(rpc, analyteDO);
            
    		return rpc;  
    	}

    public AnalyteForm fetch(AnalyteForm rpc) throws RPCException {
//		remote interface to call the storage unit bean
		AnalyteRemote remote = (AnalyteRemote)EJBFactory.lookup("openelis/AnalyteBean/remote");
		
		AnalyteDO analyteDO = remote.getAnalyte(rpc.entityKey);
		
//		set the fields in the RPC
		setFieldsInRPC(rpc, analyteDO);
		
		return rpc;
	}

	public AnalyteForm fetchForUpdate(AnalyteForm rpc) throws RPCException {
//		 remote interface to call the analyte bean
		AnalyteRemote remote = (AnalyteRemote) EJBFactory.lookup("openelis/AnalyteBean/remote");

		AnalyteDO analyteDO = new AnalyteDO();
		try {
			analyteDO = remote.getAnalyteAndLock(rpc.entityKey, SessionManager.getSession().getId());
		} catch (Exception e) {
			throw new RPCException(e.getMessage());
		}

		// set the fields in the RPC
		setFieldsInRPC(rpc, analyteDO);

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
    
    public AnalyteForm getScreen(AnalyteForm rpc) throws RPCException {
        rpc.xml = ServiceUtils.getXML(Constants.APP_ROOT+"/Forms/analyte.xsl");
        
        return rpc;
    }

    public TableDataModel getDisplay(String cat, TableDataModel model, AbstractField value) throws RPCException {
    	return null;
    }

    public TableDataModel getMatches(String cat, TableDataModel model, String match, HashMap<String,FieldType> params) throws RPCException {
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
            TableDataModel<TableDataRow<Integer>> parentModel = new TableDataModel<TableDataRow<Integer>>();
            parentModel.add(new TableDataRow<Integer>(analyteDO.getParentAnalyteId(),new StringObject(analyteDO.getParentAnalyte())));
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
	
	private TableDataModel getParentAnalyteMatches(String match){
		AnalyteRemote remote = (AnalyteRemote)EJBFactory.lookup("openelis/AnalyteBean/remote");
		TableDataModel<TableDataRow<Integer>> dataModel = new TableDataModel<TableDataRow<Integer>>();

		//lookup by name
		List autoCompleteList = remote.autoCompleteLookupByName(match+"%", 10);
		Iterator itr = autoCompleteList.iterator();
		
		while(itr.hasNext()){
            IdNameDO resultDO = (IdNameDO) itr.next();
			//parent id
			Integer analyteId = resultDO.getId();
			//parent name
			String name = resultDO.getName();			
			
			TableDataRow<Integer> data = new TableDataRow<Integer>(analyteId,new StringObject(name));
			
			//add the dataset to the datamodel
			dataModel.add(data);
		}
		
		return dataModel;		
	}

	private void setRpcErrors(List exceptionList, AnalyteForm form){
		//we need to get the keys and look them up in the resource bundle for internationalization
        HashMap<String,AbstractField> map = null;
        if(exceptionList.size() > 0)
            map = FormUtil.createFieldMap(form);
		for (int i=0; i<exceptionList.size();i++) {
			if(exceptionList.get(i) instanceof FieldErrorException)
			    map.get(((FieldErrorException)exceptionList.get(i)).getFieldName()).addError(openElisConstants.getString(((FieldErrorException)exceptionList.get(i)).getMessage()));
			else if(exceptionList.get(i) instanceof FormErrorException)
				form.addError(openElisConstants.getString(((FormErrorException)exceptionList.get(i)).getMessage()));
		}	
		form.status = Form.Status.invalid;
    }
}
