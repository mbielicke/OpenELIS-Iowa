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
package org.openelis.modules.standardnote.server;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.openelis.domain.IdNameDO;
import org.openelis.domain.StandardNoteDO;
import org.openelis.gwt.common.FieldErrorException;
import org.openelis.gwt.common.Form;
import org.openelis.gwt.common.FormErrorException;
import org.openelis.gwt.common.LastPageException;
import org.openelis.gwt.common.Query;
import org.openelis.gwt.common.RPCException;
import org.openelis.gwt.common.ValidationErrorsList;
import org.openelis.gwt.common.data.AbstractField;
import org.openelis.gwt.common.data.FieldType;
import org.openelis.gwt.common.data.StringObject;
import org.openelis.gwt.common.data.TableDataModel;
import org.openelis.gwt.common.data.TableDataRow;
import org.openelis.gwt.server.ServiceUtils;
import org.openelis.gwt.services.AppScreenFormServiceInt;
import org.openelis.gwt.services.AutoCompleteServiceInt;
import org.openelis.metamap.StandardNoteMetaMap;
import org.openelis.modules.standardnote.client.StandardNoteForm;
import org.openelis.persistence.EJBFactory;
import org.openelis.remote.CategoryRemote;
import org.openelis.remote.StandardNoteRemote;
import org.openelis.server.constants.Constants;
import org.openelis.util.FormUtil;
import org.openelis.util.SessionManager;
import org.openelis.util.UTFResource;

public class StandardNoteService implements AppScreenFormServiceInt<StandardNoteForm, Query<TableDataRow<Integer>>>, AutoCompleteServiceInt {
	
	private static final int leftTableRowsPerPage = 13;
	
    private UTFResource openElisConstants= UTFResource.getBundle((String)SessionManager.getSession().getAttribute("locale"));
	
    private static final StandardNoteMetaMap StandardNoteMeta = new StandardNoteMetaMap();
    
	public Query<TableDataRow<Integer>> commitQuery(Query<TableDataRow<Integer>> query) throws RPCException {
        List standardNotes = new ArrayList();
        //		if the rpc is null then we need to get the page
        /*
    		if(qList == null){
                qList  = (ArrayList<AbstractField>)SessionManager.getSession().getAttribute("StandardNoteQuery");
    
    	        if(qList == null)
    	        	throw new QueryException(openElisConstants.getString("queryExpiredException"));
    			
    	        
    	        StandardNoteRemote remote = (StandardNoteRemote)EJBFactory.lookup("openelis/StandardNoteBean/remote");
    	        try{
    	        	standardNotes = remote.query(qList, (model.getPage()*leftTableRowsPerPage), leftTableRowsPerPage+1);
    	        }catch(Exception e){
    	        	if(e instanceof LastPageException){
    	        		throw new LastPageException(openElisConstants.getString("lastPageException"));
    	        	}else{
    	        		throw new RPCException(e.getMessage());	
    	        	}
    	        }

    		}else{*/
    			StandardNoteRemote remote = (StandardNoteRemote)EJBFactory.lookup("openelis/StandardNoteBean/remote");
    			
    			//HashMap<String,AbstractField> fields = qList;

                try{
                    standardNotes = remote.query(query.fields,query.page*leftTableRowsPerPage,leftTableRowsPerPage);
                }catch(LastPageException e) {
                    throw new LastPageException(openElisConstants.getString("lastPageException"));
        		}catch(Exception e){
        			throw new RPCException(e.getMessage());
        		}
        		
                //need to save the rpc used to the encache
                //SessionManager.getSession().setAttribute("StandardNoteQuery", qList);
            
    		//}
    		
            int i=0;
            if(query.results == null)
                query.results = new TableDataModel<TableDataRow<Integer>>();
            else
                query.results.clear();
            while(i < standardNotes.size() && i < leftTableRowsPerPage) {
                IdNameDO resultDO = (IdNameDO)standardNotes.get(i);
                query.results.add(new TableDataRow<Integer>(resultDO.getId(),new StringObject(resultDO.getName())));
                i++;
             } 
            
    		return query;
    	}

    public StandardNoteForm commitAdd(StandardNoteForm rpc) throws RPCException {
//		remote interface to call the standardNote bean
		StandardNoteRemote remote = (StandardNoteRemote)EJBFactory.lookup("openelis/StandardNoteBean/remote");
		StandardNoteDO newStandardNoteDO = new StandardNoteDO();

//		build the storage unit DO from the form
		newStandardNoteDO = getStandardNoteDOFromRPC(rpc);
		
		// send the changes to the database
		Integer standardNoteId;
		try{
			standardNoteId = (Integer) remote.updateStandardNote(newStandardNoteDO);
		}catch(Exception e){
            if(e instanceof ValidationErrorsList){
                setRpcErrors(((ValidationErrorsList)e).getErrorList(), rpc);
                return rpc;
            }else
                throw new RPCException(e.getMessage());
        }
		
        newStandardNoteDO.setId(standardNoteId);
        
//		set the fields in the RPC
		setFieldsInRPC(rpc, newStandardNoteDO);
		
		return rpc;
	}

	public StandardNoteForm commitUpdate(StandardNoteForm rpc) throws RPCException {
//		remote interface to call the standardnote bean
		StandardNoteRemote remote = (StandardNoteRemote)EJBFactory.lookup("openelis/StandardNoteBean/remote");
		StandardNoteDO newStandardNoteDO = new StandardNoteDO();

		//build the DO from the form
		newStandardNoteDO = getStandardNoteDOFromRPC(rpc);

		//send the changes to the database
		try{
			remote.updateStandardNote(newStandardNoteDO);
		}catch(Exception e){
            if(e instanceof ValidationErrorsList){
                setRpcErrors(((ValidationErrorsList)e).getErrorList(), rpc);
                return rpc;
            }else
                throw new RPCException(e.getMessage());
        }
		
//		set the fields in the RPC
		setFieldsInRPC(rpc, newStandardNoteDO);
		
		return rpc;
	}

	public StandardNoteForm commitDelete(StandardNoteForm rpc) throws RPCException {
    //		remote interface to call the standard note bean
    		StandardNoteRemote remote = (StandardNoteRemote)EJBFactory.lookup("openelis/StandardNoteBean/remote");
    		
    		try {
    			remote.deleteStandardNote(rpc.entityKey);
    			
    		}catch(Exception e){
                if(e instanceof ValidationErrorsList){
                    setRpcErrors(((ValidationErrorsList)e).getErrorList(), rpc);
                    return rpc;
                }else
                    throw new RPCException(e.getMessage());
            }
    		
    		//this should set all fields in the rpc to null
    		setFieldsInRPC(rpc, new StandardNoteDO());
    		
    		return rpc;
    	}

    public StandardNoteForm abort(StandardNoteForm rpc) throws RPCException {
    //		remote interface to call the storage unit bean
    		StandardNoteRemote remote = (StandardNoteRemote)EJBFactory.lookup("openelis/StandardNoteBean/remote");
    		
    		
    		StandardNoteDO standardNoteDO = remote.getStandardNoteAndUnlock(rpc.entityKey, SessionManager.getSession().getId());
    
    //		set the fields in the RPC
    		setFieldsInRPC(rpc, standardNoteDO);
            
          return rpc;  
    	}

    public StandardNoteForm fetch(StandardNoteForm rpc) throws RPCException {
    	//		remote interface to call the standard note bean
		StandardNoteRemote remote = (StandardNoteRemote)EJBFactory.lookup("openelis/StandardNoteBean/remote");
		
		StandardNoteDO standardNoteDO = remote.getStandardNote(rpc.entityKey);
		
//		set the fields in the RPC
		setFieldsInRPC(rpc, standardNoteDO);
		
		return rpc;
	}

	public StandardNoteForm fetchForUpdate(StandardNoteForm rpc) throws RPCException {
//		remote interface to call the standard note bean
		StandardNoteRemote remote = (StandardNoteRemote)EJBFactory.lookup("openelis/StandardNoteBean/remote");
		StandardNoteDO standardNoteDO = new StandardNoteDO();
		
		try{
			standardNoteDO = remote.getStandardNoteAndLock(rpc.entityKey, SessionManager.getSession().getId());
		}catch(Exception e){
			throw new RPCException(e.getMessage());
		}
		
//		set the fields in the RPC
		setFieldsInRPC(rpc, standardNoteDO);
		
		return rpc;
	}

    public TableDataModel getDisplay(String cat, TableDataModel model, AbstractField value) {
		return null;
	}
    
    public StandardNoteForm getScreen(StandardNoteForm rpc) throws RPCException {
        rpc.xml = ServiceUtils.getXML(Constants.APP_ROOT+"/Forms/standardNote.xsl");
	    
	    return rpc;
    }

	public TableDataModel getMatches(String cat, TableDataModel model, String match, HashMap<String,FieldType> params) {
		return null;
	}

	public TableDataModel getInitialModel(String cat) {
		Integer id = null;
		CategoryRemote remote = (CategoryRemote)EJBFactory.lookup("openelis/CategoryBean/remote");
		
		if(cat.equals("type")){
			id = remote.getCategoryId("standard_note_type");
		}
		
		List<IdNameDO> entries = new ArrayList();
		if(id != null)
			entries = remote.getDropdownValues(id);
		
		//we need to build the model to return
		TableDataModel<TableDataRow<Integer>> returnModel = new TableDataModel<TableDataRow<Integer>>();
			
        if(entries.size() > 0){
    		//create a blank entry to begin the list
    		returnModel.add(new TableDataRow<Integer>(0,new StringObject("")));
        }
        
		for(IdNameDO resultDO : entries){
			returnModel.add(new TableDataRow<Integer>(resultDO.getId(),new StringObject(resultDO.getName())));
		}		
		
		return returnModel;
	}

	private StandardNoteDO getStandardNoteDOFromRPC(StandardNoteForm form){
    	StandardNoteDO newStandardNoteDO = new StandardNoteDO();
    	newStandardNoteDO.setId(form.id.getValue());
    	newStandardNoteDO.setDescription(form.description.getValue());
    	newStandardNoteDO.setName(form.name.getValue());
    	newStandardNoteDO.setText(form.text.getValue());
    	newStandardNoteDO.setType((Integer) form.typeId.getSelectedKey());
    	
    	return newStandardNoteDO;
    }

    private void setRpcErrors(List exceptionList, StandardNoteForm form){
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

    private void setFieldsInRPC(StandardNoteForm form, StandardNoteDO standardNoteDO){
    	form.id.setValue(standardNoteDO.getId());
    	form.description.setValue(standardNoteDO.getDescription());
    	form.name.setValue(standardNoteDO.getName());
    	form.text.setValue(standardNoteDO.getText());
        
        if(standardNoteDO.getType() != null)
        	form.typeId.setValue(new TableDataRow<Integer>(standardNoteDO.getType()));
    }
}
