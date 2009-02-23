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
import org.openelis.gwt.common.EntityLockedException;
import org.openelis.gwt.common.FieldErrorException;
import org.openelis.gwt.common.Form;
import org.openelis.gwt.common.FormErrorException;
import org.openelis.gwt.common.LastPageException;
import org.openelis.gwt.common.QueryException;
import org.openelis.gwt.common.RPCException;
import org.openelis.gwt.common.data.AbstractField;
import org.openelis.gwt.common.data.DataModel;
import org.openelis.gwt.common.data.DataSet;
import org.openelis.gwt.common.data.FieldType;
import org.openelis.gwt.common.data.StringObject;
import org.openelis.gwt.server.ServiceUtils;
import org.openelis.gwt.services.AppScreenFormServiceInt;
import org.openelis.gwt.services.AutoCompleteServiceInt;
import org.openelis.metamap.StandardNoteMetaMap;
import org.openelis.modules.standardnote.client.StandardNoteForm;
import org.openelis.modules.standardnote.client.StandardNoteRPC;
import org.openelis.persistence.EJBFactory;
import org.openelis.remote.CategoryRemote;
import org.openelis.remote.StandardNoteRemote;
import org.openelis.server.constants.Constants;
import org.openelis.server.handlers.StandardNoteTypeCacheHandler;
import org.openelis.util.SessionManager;
import org.openelis.util.UTFResource;

public class StandardNoteService implements AppScreenFormServiceInt<StandardNoteRPC, Integer>, AutoCompleteServiceInt {
	
	private static final int leftTableRowsPerPage = 13;
	
    private UTFResource openElisConstants= UTFResource.getBundle((String)SessionManager.getSession().getAttribute("locale"));
	
    private static final StandardNoteMetaMap StandardNoteMeta = new StandardNoteMetaMap();
    
	public DataModel<Integer> commitQuery(Form form, DataModel<Integer> model) throws RPCException {
        List standardNotes = new ArrayList();
        //		if the rpc is null then we need to get the page
    		if(form == null){
                form  = (Form)SessionManager.getSession().getAttribute("StandardNoteQuery");
    
    	        if(form == null)
    	        	throw new QueryException(openElisConstants.getString("queryExpiredException"));
    			
    	        
    	        StandardNoteRemote remote = (StandardNoteRemote)EJBFactory.lookup("openelis/StandardNoteBean/remote");
    	        try{
    	        	standardNotes = remote.query(form.getFieldMap(), (model.getPage()*leftTableRowsPerPage), leftTableRowsPerPage+1);
    	        }catch(Exception e){
    	        	if(e instanceof LastPageException){
    	        		throw new LastPageException(openElisConstants.getString("lastPageException"));
    	        	}else{
    	        		throw new RPCException(e.getMessage());	
    	        	}
    	        }

    		}else{
    			StandardNoteRemote remote = (StandardNoteRemote)EJBFactory.lookup("openelis/StandardNoteBean/remote");
    			
    			HashMap<String,AbstractField> fields = form.getFieldMap();

                try{
                    standardNotes = remote.query(fields,0,leftTableRowsPerPage);
    
        		}catch(Exception e){
        			throw new RPCException(e.getMessage());
        		}
        		
                //need to save the rpc used to the encache
                SessionManager.getSession().setAttribute("StandardNoteQuery", form);
            
    		}
    		
            int i=0;
            if(model == null)
                model = new DataModel<Integer>();
            else
                model.clear();
            while(i < standardNotes.size() && i < leftTableRowsPerPage) {
                IdNameDO resultDO = (IdNameDO)standardNotes.get(i);
                model.add(new DataSet<Integer>(resultDO.getId(),new StringObject(resultDO.getName())));
                i++;
             } 
            
    		return model;
    	}

    public StandardNoteRPC commitAdd(StandardNoteRPC rpc) throws RPCException {
//		remote interface to call the standardNote bean
		StandardNoteRemote remote = (StandardNoteRemote)EJBFactory.lookup("openelis/StandardNoteBean/remote");
		StandardNoteDO newStandardNoteDO = new StandardNoteDO();

//		build the storage unit DO from the form
		newStandardNoteDO = getStandardNoteDOFromRPC(rpc.form);
		
		//validate the fields on the backend
		List exceptionList = remote.validateForAdd(newStandardNoteDO);
		if(exceptionList.size() > 0){
			setRpcErrors(exceptionList, rpc.form);
			
			return rpc;
		} 
		
		// send the changes to the database
		Integer standardNoteId;
		try{
			standardNoteId = (Integer) remote.updateStandardNote(newStandardNoteDO);
		}catch(Exception e){
			exceptionList = new ArrayList();
			exceptionList.add(e);
			
			setRpcErrors(exceptionList, rpc.form);
			return rpc;
		}
		
        newStandardNoteDO.setId(standardNoteId);
        
//		set the fields in the RPC
		setFieldsInRPC(rpc.form, newStandardNoteDO);
		
		return rpc;
	}

	public StandardNoteRPC commitUpdate(StandardNoteRPC rpc) throws RPCException {
//		remote interface to call the standardnote bean
		StandardNoteRemote remote = (StandardNoteRemote)EJBFactory.lookup("openelis/StandardNoteBean/remote");
		StandardNoteDO newStandardNoteDO = new StandardNoteDO();

		//build the DO from the form
		newStandardNoteDO = getStandardNoteDOFromRPC(rpc.form);

		//validate the fields on the backend
		List exceptionList = remote.validateForUpdate(newStandardNoteDO);
		if(exceptionList.size() > 0){
			setRpcErrors(exceptionList, rpc.form);
			
			return rpc;
		} 
		
		//send the changes to the database
		try{
			remote.updateStandardNote(newStandardNoteDO);
		}catch(Exception e){
            if(e instanceof EntityLockedException)
                throw new RPCException(e.getMessage());
            
			exceptionList = new ArrayList();
			exceptionList.add(e);
			
			setRpcErrors(exceptionList, rpc.form);
			return rpc;
		}

//		set the fields in the RPC
		setFieldsInRPC(rpc.form, newStandardNoteDO);
		
		return rpc;
	}

	public StandardNoteRPC commitDelete(StandardNoteRPC rpc) throws RPCException {
    //		remote interface to call the standard note bean
    		StandardNoteRemote remote = (StandardNoteRemote)EJBFactory.lookup("openelis/StandardNoteBean/remote");
    		
    		//validate the fields on the backend
    		List exceptionList = remote.validateForDelete(rpc.key);
    		if(exceptionList.size() > 0){
    			setRpcErrors(exceptionList, rpc.form);
    			
    			return rpc;
    		} 
    		
    		try {
    			remote.deleteStandardNote(rpc.key);
    			
    		} catch (Exception e) {
    			exceptionList = new ArrayList();
    			exceptionList.add(e);
    			
    			setRpcErrors(exceptionList, rpc.form);
    			return rpc;
    		}
    		
    		//this should set all fields in the rpc to null
    		setFieldsInRPC(rpc.form, new StandardNoteDO());
    		
    		return rpc;
    	}

    public StandardNoteRPC abort(StandardNoteRPC rpc) throws RPCException {
    //		remote interface to call the storage unit bean
    		StandardNoteRemote remote = (StandardNoteRemote)EJBFactory.lookup("openelis/StandardNoteBean/remote");
    		
    		
    		StandardNoteDO standardNoteDO = remote.getStandardNoteAndUnlock(rpc.key, SessionManager.getSession().getId());
    
    //		set the fields in the RPC
    		setFieldsInRPC(rpc.form, standardNoteDO);
            
          return rpc;  
    	}

    public StandardNoteRPC fetch(StandardNoteRPC rpc) throws RPCException {
    	/*
         * Call checkModels to make screen has most recent versions of dropdowns
         */
        checkModels(rpc);
        
//		remote interface to call the standard note bean
		StandardNoteRemote remote = (StandardNoteRemote)EJBFactory.lookup("openelis/StandardNoteBean/remote");
		
		StandardNoteDO standardNoteDO = remote.getStandardNote(rpc.key);
		
//		set the fields in the RPC
		setFieldsInRPC(rpc.form, standardNoteDO);
		
		return rpc;
	}

	public StandardNoteRPC fetchForUpdate(StandardNoteRPC rpc) throws RPCException {
		/*
         * Call checkModels to make screen has most recent versions of dropdowns
         */
        checkModels(rpc);
        
//		remote interface to call the standard note bean
		StandardNoteRemote remote = (StandardNoteRemote)EJBFactory.lookup("openelis/StandardNoteBean/remote");
		StandardNoteDO standardNoteDO = new StandardNoteDO();
		
		try{
			standardNoteDO = remote.getStandardNoteAndLock(rpc.key, SessionManager.getSession().getId());
		}catch(Exception e){
			throw new RPCException(e.getMessage());
		}
		
//		set the fields in the RPC
		setFieldsInRPC(rpc.form, standardNoteDO);
		
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

    public DataModel getDisplay(String cat, DataModel model, AbstractField value) {
		return null;
	}
    
    public StandardNoteRPC getScreen(StandardNoteRPC rpc) throws RPCException {
        rpc.xml = ServiceUtils.getXML(Constants.APP_ROOT+"/Forms/standardNote.xsl");
	    
	    /*
	     * Load initial  models to RPC and store cache verison of models into Session for 
	     * comparisons for later fetches
	     */
	    rpc.noteTypes = StandardNoteTypeCacheHandler.getNoteTypes();
	    SessionManager.getSession().setAttribute("standardNoteTypeVersion",StandardNoteTypeCacheHandler.version);
	    
	    return rpc;
    }

	public void checkModels(StandardNoteRPC rpc) {
	    /*
	     * Retrieve current version of models from session.
	     */
	    int noteTypes = (Integer)SessionManager.getSession().getAttribute("standardNoteTypeVersion");
	    
	    /*
	     * Compare stored version to current cache versions and update if necessary. 
	     */
	    if(noteTypes != StandardNoteTypeCacheHandler.version){
	        rpc.noteTypes = StandardNoteTypeCacheHandler.getNoteTypes();
	        SessionManager.getSession().setAttribute("standardNoteTypeVersion",StandardNoteTypeCacheHandler.version);
	    }
	}

	public DataModel getMatches(String cat, DataModel model, String match, HashMap params) {
		return null;
	}

	public DataModel getInitialModel(String cat) {
		Integer id = null;
		CategoryRemote remote = (CategoryRemote)EJBFactory.lookup("openelis/CategoryBean/remote");
		
		if(cat.equals("type")){
			id = remote.getCategoryId("standard_note_type");
		}
		
		List entries = new ArrayList();
		if(id != null)
			entries = remote.getDropdownValues(id);
		
		//we need to build the model to return
		DataModel<Integer> returnModel = new DataModel<Integer>();
			
        if(entries.size() > 0){
    		//create a blank entry to begin the list
    		returnModel.add(new DataSet<Integer>(0,new StringObject("")));
        }
        
		int i=0;
		while(i < entries.size()){
			IdNameDO resultDO = (IdNameDO) entries.get(i);
			returnModel.add(new DataSet<Integer>(resultDO.getId(),new StringObject(resultDO.getName())));
			
			i++;
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

    private void setFieldsInRPC(StandardNoteForm form, StandardNoteDO standardNoteDO){
    	form.id.setValue(standardNoteDO.getId());
    	form.description.setValue(standardNoteDO.getDescription());
    	form.name.setValue(standardNoteDO.getName());
    	form.text.setValue(standardNoteDO.getText());
        
        if(standardNoteDO.getType() != null)
        	form.typeId.setValue(new DataSet<Integer>(standardNoteDO.getType()));
    }
}
