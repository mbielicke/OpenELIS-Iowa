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
import org.openelis.gwt.common.FormErrorException;
import org.openelis.gwt.common.FormRPC;
import org.openelis.gwt.common.LastPageException;
import org.openelis.gwt.common.QueryException;
import org.openelis.gwt.common.RPCException;
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
import org.openelis.metamap.StandardNoteMetaMap;
import org.openelis.persistence.CachingManager;
import org.openelis.persistence.EJBFactory;
import org.openelis.remote.CategoryRemote;
import org.openelis.remote.StandardNoteRemote;
import org.openelis.server.constants.Constants;
import org.openelis.util.SessionManager;
import org.openelis.util.UTFResource;

public class StandardNoteService implements AppScreenFormServiceInt<FormRPC, DataSet, DataModel>, 
																AutoCompleteServiceInt {
	
	private static final long serialVersionUID = 734713425110147476L;
	private static final int leftTableRowsPerPage = 13;
	
    private UTFResource openElisConstants= UTFResource.getBundle((String)SessionManager.getSession().getAttribute("locale"));
	
    private static final StandardNoteMetaMap StandardNoteMeta = new StandardNoteMetaMap();
    
	public DataModel commitQuery(FormRPC rpcSend, DataModel model) throws RPCException {
        List standardNotes = new ArrayList();
        //		if the rpc is null then we need to get the page
    		if(rpcSend == null){
                FormRPC rpc = (FormRPC)SessionManager.getSession().getAttribute("StandardNoteQuery");
    
    	        if(rpc == null)
    	        	throw new QueryException(openElisConstants.getString("queryExpiredException"));
    			
    	        
    	        StandardNoteRemote remote = (StandardNoteRemote)EJBFactory.lookup("openelis/StandardNoteBean/remote");
    	        try{
    	        	standardNotes = remote.query(rpc.getFieldMap(), (model.getPage()*leftTableRowsPerPage), leftTableRowsPerPage+1);
    	        }catch(Exception e){
    	        	if(e instanceof LastPageException){
    	        		throw new LastPageException(openElisConstants.getString("lastPageException"));
    	        	}else{
    	        		throw new RPCException(e.getMessage());	
    	        	}
    	        }

    		}else{
    			StandardNoteRemote remote = (StandardNoteRemote)EJBFactory.lookup("openelis/StandardNoteBean/remote");
    			
    			HashMap<String,AbstractField> fields = rpcSend.getFieldMap();

                try{
                    standardNotes = remote.query(fields,0,leftTableRowsPerPage);
    
        		}catch(Exception e){
        			throw new RPCException(e.getMessage());
        		}
        		
                //need to save the rpc used to the encache
                SessionManager.getSession().setAttribute("StandardNoteQuery", rpcSend);
            
    		}
    		
            int i=0;
            if(model == null)
                model = new DataModel();
            else
                model.clear();
            while(i < standardNotes.size() && i < leftTableRowsPerPage) {
                IdNameDO resultDO = (IdNameDO)standardNotes.get(i);
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
//		remote interface to call the standardNote bean
		StandardNoteRemote remote = (StandardNoteRemote)EJBFactory.lookup("openelis/StandardNoteBean/remote");
		StandardNoteDO newStandardNoteDO = new StandardNoteDO();

//		build the storage unit DO from the form
		newStandardNoteDO = getStandardNoteDOFromRPC(rpcSend);
		
		//validate the fields on the backend
		List exceptionList = remote.validateForAdd(newStandardNoteDO);
		if(exceptionList.size() > 0){
			setRpcErrors(exceptionList, rpcSend);
			
			return rpcSend;
		} 
		
		// send the changes to the database
		Integer standardNoteId;
		try{
			standardNoteId = (Integer) remote.updateStandardNote(newStandardNoteDO);
		}catch(Exception e){
			exceptionList = new ArrayList();
			exceptionList.add(e);
			
			setRpcErrors(exceptionList, rpcSend);
			return rpcSend;
		}
		
        newStandardNoteDO.setId(standardNoteId);
        
//		set the fields in the RPC
		setFieldsInRPC(rpcReturn, newStandardNoteDO);
		
		return rpcReturn;
	}

	public FormRPC commitUpdate(FormRPC rpcSend, FormRPC rpcReturn) throws RPCException {
//		remote interface to call the standardnote bean
		StandardNoteRemote remote = (StandardNoteRemote)EJBFactory.lookup("openelis/StandardNoteBean/remote");
		StandardNoteDO newStandardNoteDO = new StandardNoteDO();

		//build the DO from the form
		newStandardNoteDO = getStandardNoteDOFromRPC(rpcSend);

		//validate the fields on the backend
		List exceptionList = remote.validateForUpdate(newStandardNoteDO);
		if(exceptionList.size() > 0){
			setRpcErrors(exceptionList, rpcSend);
			
			return rpcSend;
		} 
		
		//send the changes to the database
		try{
			remote.updateStandardNote(newStandardNoteDO);
		}catch(Exception e){
            if(e instanceof EntityLockedException)
                throw new RPCException(e.getMessage());
            
			exceptionList = new ArrayList();
			exceptionList.add(e);
			
			setRpcErrors(exceptionList, rpcSend);
			return rpcSend;
		}

//		set the fields in the RPC
		setFieldsInRPC(rpcReturn, newStandardNoteDO);
		
		return rpcReturn;
	}

	public FormRPC commitDelete(DataSet key, FormRPC rpcReturn) throws RPCException {
    //		remote interface to call the standard note bean
    		StandardNoteRemote remote = (StandardNoteRemote)EJBFactory.lookup("openelis/StandardNoteBean/remote");
    		
    		//validate the fields on the backend
    		List exceptionList = remote.validateForDelete((Integer)((DataObject)key.getKey()).getValue());
    		if(exceptionList.size() > 0){
    			setRpcErrors(exceptionList, rpcReturn);
    			
    			return rpcReturn;
    		} 
    		
    		try {
    			remote.deleteStandardNote((Integer)((DataObject)key.getKey()).getValue());
    			
    		} catch (Exception e) {
    			exceptionList = new ArrayList();
    			exceptionList.add(e);
    			
    			setRpcErrors(exceptionList, rpcReturn);
    			return rpcReturn;
    		}
    		
    		//this should set all fields in the rpc to null
    		setFieldsInRPC(rpcReturn, new StandardNoteDO());
    		
    		return rpcReturn;
    	}

    public FormRPC abort(DataSet key, FormRPC rpcReturn) throws RPCException {
    //		remote interface to call the storage unit bean
    		StandardNoteRemote remote = (StandardNoteRemote)EJBFactory.lookup("openelis/StandardNoteBean/remote");
    		
    		
    		StandardNoteDO standardNoteDO = remote.getStandardNoteAndUnlock((Integer)((DataObject)key.getKey()).getValue(), SessionManager.getSession().getId());
    
    //		set the fields in the RPC
    		setFieldsInRPC(rpcReturn, standardNoteDO);
            
          return rpcReturn;  
    	}

    public FormRPC fetch(DataSet key, FormRPC rpcReturn) throws RPCException {
//		remote interface to call the standard note bean
		StandardNoteRemote remote = (StandardNoteRemote)EJBFactory.lookup("openelis/StandardNoteBean/remote");
		
		StandardNoteDO standardNoteDO = remote.getStandardNote((Integer)((DataObject)key.getKey()).getValue());
		
//		set the fields in the RPC
		setFieldsInRPC(rpcReturn, standardNoteDO);
		
		return rpcReturn;
	}

	public FormRPC fetchForUpdate(DataSet key, FormRPC rpcReturn) throws RPCException {
//		remote interface to call the standard note bean
		StandardNoteRemote remote = (StandardNoteRemote)EJBFactory.lookup("openelis/StandardNoteBean/remote");
		StandardNoteDO standardNoteDO = new StandardNoteDO();
		
		try{
			standardNoteDO = remote.getStandardNoteAndLock((Integer)((DataObject)key.getKey()).getValue(), SessionManager.getSession().getId());
		}catch(Exception e){
			throw new RPCException(e.getMessage());
		}
		
//		set the fields in the RPC
		setFieldsInRPC(rpcReturn, standardNoteDO);
		
		return rpcReturn;
	}

	public String getXML() throws RPCException {
    	return ServiceUtils.getXML(Constants.APP_ROOT+"/Forms/standardNote.xsl");
    }

    public HashMap getXMLData() throws RPCException {
        StringObject xml = new StringObject();
        xml.setValue(ServiceUtils.getXML(Constants.APP_ROOT+"/Forms/standardNote.xsl"));
        
        DataModel standardNoteTypeDropdownField = (DataModel)CachingManager.getElement("InitialData", "standardNoteTypeDropdown");
        
        //standard note type dropdown
        if(standardNoteTypeDropdownField == null){
            standardNoteTypeDropdownField = getInitialModel("type");
            CachingManager.putElement("InitialData", "standardNoteTypeDropdown", standardNoteTypeDropdownField);
        }
        
        HashMap map = new HashMap();
        map.put("xml",xml);
        map.put("noteTypes",standardNoteTypeDropdownField);
        
        return map;
    }

    public HashMap getXMLData(HashMap args) throws RPCException {
    	// TODO Auto-generated method stub
    	return null;
    }

    public DataModel getDisplay(String cat, DataModel model, AbstractField value) {
		return null;
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
		DataModel returnModel = new DataModel();
			
        if(entries.size() > 0){
    		//create a blank entry to begin the list
    		DataSet blankset = new DataSet();
    		
    		StringObject blankStringId = new StringObject();
    		NumberObject blankNumberId = new NumberObject(NumberObject.Type.INTEGER);
    				
    		blankStringId.setValue("");
    		blankset.add(blankStringId);
    		
    		blankNumberId.setValue(new Integer(0));
    		
    		blankset.setKey(blankNumberId);
    		
    		returnModel.add(blankset);
        }
        
		int i=0;
		while(i < entries.size()){
			DataSet set = new DataSet();
			IdNameDO resultDO = (IdNameDO) entries.get(i);
			//id
			Integer dropdownId = resultDO.getId();
			//entry
			String dropdownText = resultDO.getName();
			
			StringObject textObject = new StringObject();
			NumberObject numberId = new NumberObject(NumberObject.Type.INTEGER);
			
			textObject.setValue(dropdownText);
			set.add(textObject);
			
			numberId.setValue(dropdownId);

			set.setKey(numberId);
			
			returnModel.add(set);
			
			i++;
		}		
		
		return returnModel;
	}

	private StandardNoteDO getStandardNoteDOFromRPC(FormRPC rpcSend){
    	StandardNoteDO newStandardNoteDO = new StandardNoteDO();
    	newStandardNoteDO.setId((Integer)rpcSend.getFieldValue(StandardNoteMeta.getId()));
    	newStandardNoteDO.setDescription((String)rpcSend.getFieldValue(StandardNoteMeta.getDescription()));
    	newStandardNoteDO.setName((String)rpcSend.getFieldValue(StandardNoteMeta.getName()));
    	newStandardNoteDO.setText((String)rpcSend.getFieldValue(StandardNoteMeta.getText()));
    	newStandardNoteDO.setType((Integer)((DropDownField)rpcSend.getField(StandardNoteMeta.getTypeId())).getSelectedKey());
    	
    	return newStandardNoteDO;
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

    private void setFieldsInRPC(FormRPC rpcReturn, StandardNoteDO standardNoteDO){
    	rpcReturn.setFieldValue(StandardNoteMeta.getId(), standardNoteDO.getId());
    	rpcReturn.setFieldValue(StandardNoteMeta.getDescription(), standardNoteDO.getDescription());
    	rpcReturn.setFieldValue(StandardNoteMeta.getName(), standardNoteDO.getName());
    	rpcReturn.setFieldValue(StandardNoteMeta.getText(), standardNoteDO.getText());
        
        if(standardNoteDO.getType() != null)
            rpcReturn.setFieldValue(StandardNoteMeta.getTypeId(), new DataSet(new NumberObject(standardNoteDO.getType())));
    }
}
