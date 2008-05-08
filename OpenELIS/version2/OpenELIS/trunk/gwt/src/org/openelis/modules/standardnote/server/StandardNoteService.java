
package org.openelis.modules.standardnote.server;

import org.openelis.domain.StandardNoteDO;
import org.openelis.gwt.common.FieldErrorException;
import org.openelis.gwt.common.FormErrorException;
import org.openelis.gwt.common.FormRPC;
import org.openelis.gwt.common.IForm;
import org.openelis.gwt.common.LastPageException;
import org.openelis.gwt.common.QueryNotFoundException;
import org.openelis.gwt.common.RPCException;
import org.openelis.gwt.common.data.AbstractField;
import org.openelis.gwt.common.data.DataModel;
import org.openelis.gwt.common.data.DataObject;
import org.openelis.gwt.common.data.DataSet;
import org.openelis.gwt.common.data.NumberObject;
import org.openelis.gwt.common.data.StringObject;
import org.openelis.gwt.server.ServiceUtils;
import org.openelis.gwt.services.AppScreenFormServiceInt;
import org.openelis.gwt.services.AutoCompleteServiceInt;
import org.openelis.meta.StandardNoteMeta;
import org.openelis.persistence.CachingManager;
import org.openelis.persistence.EJBFactory;
import org.openelis.remote.CategoryRemote;
import org.openelis.remote.StandardNoteRemote;
import org.openelis.server.constants.Constants;
import org.openelis.util.SessionManager;
import org.openelis.util.UTFResource;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

public class StandardNoteService implements AppScreenFormServiceInt, 
																AutoCompleteServiceInt {
	
	private static final long serialVersionUID = 734713425110147476L;
	private static final int leftTableRowsPerPage = 10;
	
	private UTFResource openElisConstants= UTFResource.getBundle("org.openelis.modules.main.server.constants.OpenELISConstants",
			new Locale(((SessionManager.getSession() == null  || (String)SessionManager.getSession().getAttribute("locale") == null) 
					? "en" : (String)SessionManager.getSession().getAttribute("locale"))));
	
	public FormRPC abort(DataSet key, FormRPC rpcReturn) throws RPCException {
//		remote interface to call the storage unit bean
		StandardNoteRemote remote = (StandardNoteRemote)EJBFactory.lookup("openelis/StandardNoteBean/remote");
		
		
		StandardNoteDO standardNoteDO = remote.getStandardNoteAndUnlock((Integer)key.getKey().getValue());

//		set the fields in the RPC
		setFieldsInRPC(rpcReturn, standardNoteDO);
        
      return rpcReturn;  
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

	public FormRPC commitDelete(DataSet key, FormRPC rpcReturn) throws RPCException {
//		remote interface to call the standard note bean
		StandardNoteRemote remote = (StandardNoteRemote)EJBFactory.lookup("openelis/StandardNoteBean/remote");
		
		//validate the fields on the backend
		List exceptionList = remote.validateForDelete((Integer)key.getKey().getValue());
		if(exceptionList.size() > 0){
			setRpcErrors(exceptionList, rpcReturn);
			
			return rpcReturn;
		} 
		
		try {
			remote.deleteStandardNote((Integer)key.getKey().getValue());
			
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

	public DataModel commitQuery(FormRPC rpcSend, DataModel model) throws RPCException {
//		if the rpc is null then we need to get the page
		if(rpcSend == null){
//			need to get the query rpc out of the cache
	        FormRPC rpc = (FormRPC)CachingManager.getElement("screenQueryRpc", SessionManager.getSession().getAttribute("systemUserId")+":StandardNote");

	        if(rpc == null)
	        	throw new QueryNotFoundException(openElisConstants.getString("queryExpiredException"));
			
	        List standardNotes = null;
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
	        
	        int i=0;
	        model.clear();
	        while(i < standardNotes.size() && i < leftTableRowsPerPage) {
	    	   	Object[] result = (Object[])standardNotes.get(i);
				//org id
				Integer idResult = (Integer)result[0];
				//org name
				String nameResult = (String)result[1];

				DataSet row = new DataSet();
				NumberObject id = new NumberObject(NumberObject.INTEGER);
				StringObject name = new StringObject();
				name.setValue(nameResult);
				id.setValue(idResult);
				
				row.setKey(id);			
				row.addObject(name);
				model.add(row);
				i++;
	         } 

	        return model;
		}else{
			StandardNoteRemote remote = (StandardNoteRemote)EJBFactory.lookup("openelis/StandardNoteBean/remote");
			
			HashMap<String,AbstractField> fields = rpcSend.getFieldMap();
			
			List standardNoteNames = new ArrayList();
			try{
				standardNoteNames = remote.query(fields,0,leftTableRowsPerPage);

		}catch(Exception e){
			throw new RPCException(e.getMessage());
		}
		
		Iterator namesItr = standardNoteNames.iterator();
		model=  new DataModel();
		
		while(namesItr.hasNext()){
			Object[] result = (Object[])namesItr.next();
			//org id
			Integer id = (Integer)result[0];
			//org name
			String name = (String)result[1];

			DataSet row = new DataSet();

			 NumberObject idField = new NumberObject(NumberObject.INTEGER);
			 StringObject nameField = new StringObject();
			 nameField.setValue(name);
      
			 idField.setValue(id);
			 row.setKey(idField);
			 row.addObject(nameField);

			 model.add(row);
		}
        
        //need to save the rpc used to the encache
        if(SessionManager.getSession().getAttribute("systemUserId") == null)
        	SessionManager.getSession().setAttribute("systemUserId", remote.getSystemUserId().toString());
        CachingManager.putElement("screenQueryRpc", SessionManager.getSession().getAttribute("systemUserId")+":StandardNote", rpcSend);
		}
		
		return model;
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
			exceptionList = new ArrayList();
			exceptionList.add(e);
			
			setRpcErrors(exceptionList, rpcSend);
			return rpcSend;
		}

//		set the fields in the RPC
		setFieldsInRPC(rpcReturn, newStandardNoteDO);
		
		return rpcReturn;
	}

	public FormRPC fetch(DataSet key, FormRPC rpcReturn) throws RPCException {
//		remote interface to call the standard note bean
		StandardNoteRemote remote = (StandardNoteRemote)EJBFactory.lookup("openelis/StandardNoteBean/remote");
		
		StandardNoteDO standardNoteDO = remote.getStandardNote((Integer)key.getKey().getValue());
		
//		set the fields in the RPC
		setFieldsInRPC(rpcReturn, standardNoteDO);
		
		return rpcReturn;
	}

	public FormRPC fetchForUpdate(DataSet key, FormRPC rpcReturn) throws RPCException {
//		remote interface to call the standard note bean
		StandardNoteRemote remote = (StandardNoteRemote)EJBFactory.lookup("openelis/StandardNoteBean/remote");
		StandardNoteDO standardNoteDO = new StandardNoteDO();
		
		try{
			standardNoteDO = remote.getStandardNoteAndLock((Integer)key.getKey().getValue());
		}catch(Exception e){
			throw new RPCException(e.getMessage());
		}
		
//		set the fields in the RPC
		setFieldsInRPC(rpcReturn, standardNoteDO);
		
		return rpcReturn;
	}

	private void setFieldsInRPC(FormRPC rpcReturn, StandardNoteDO standardNoteDO){
		rpcReturn.setFieldValue(StandardNoteMeta.ID, standardNoteDO.getId());
		rpcReturn.setFieldValue(StandardNoteMeta.DESCRIPTION, (standardNoteDO.getDescription() == null ? null : standardNoteDO.getDescription().trim()));
		rpcReturn.setFieldValue(StandardNoteMeta.NAME, (standardNoteDO.getName() == null ? null : standardNoteDO.getName().trim()));
		rpcReturn.setFieldValue(StandardNoteMeta.TEXT, (standardNoteDO.getText() == null ? null : standardNoteDO.getText().trim()));
		rpcReturn.setFieldValue(StandardNoteMeta.TYPE, standardNoteDO.getType());
	}
	
	private StandardNoteDO getStandardNoteDOFromRPC(FormRPC rpcSend){
		StandardNoteDO newStandardNoteDO = new StandardNoteDO();
		newStandardNoteDO.setId((Integer)rpcSend.getFieldValue(StandardNoteMeta.ID));
		newStandardNoteDO.setDescription((String)rpcSend.getFieldValue(StandardNoteMeta.DESCRIPTION));
		newStandardNoteDO.setName((String)rpcSend.getFieldValue(StandardNoteMeta.NAME));
		newStandardNoteDO.setText((String)rpcSend.getFieldValue(StandardNoteMeta.TEXT));
		newStandardNoteDO.setType((Integer)rpcSend.getFieldValue(StandardNoteMeta.TYPE));
		
		return newStandardNoteDO;
	}
	
	public String getXML() throws RPCException {
		return ServiceUtils.getXML(Constants.APP_ROOT+"/Forms/standardNote.xsl");
	}
    
    public DataObject[] getXMLData() throws RPCException {
        StringObject xml = new StringObject();
        xml.setValue(ServiceUtils.getXML(Constants.APP_ROOT+"/Forms/standardNote.xsl"));
        
        DataModel standardNoteTypeDropdownField = (DataModel)CachingManager.getElement("InitialData", "standardNoteTypeDropdown");
        
        //standard note type dropdown
        if(standardNoteTypeDropdownField == null){
        	standardNoteTypeDropdownField = getInitialModel("type");
        	CachingManager.putElement("InitialData", "standardNoteTypeDropdown", standardNoteTypeDropdownField);
        }
        
        return new DataObject[] {xml,standardNoteTypeDropdownField};
    }

	public DataModel getDisplay(String cat, DataModel model, AbstractField value) {
		return null;
	}

	public DataModel getMatches(String cat, DataModel model, String match) {
		return null;
	}

	public DataModel getInitialModel(String cat) {
		int id = -1;
		CategoryRemote remote = (CategoryRemote)EJBFactory.lookup("openelis/CategoryBean/remote");
		
		if(cat.equals("type")){
			id = remote.getCategoryId("standard_note_type");
		}
		
		List entries = new ArrayList();
		if(id > -1)
			entries = remote.getDropdownValues(id);
		
		//we need to build the model to return
		DataModel returnModel = new DataModel();
			
		//create a blank entry to begin the list
		DataSet blankset = new DataSet();
		
		StringObject blankStringId = new StringObject();
		NumberObject blankNumberId = new NumberObject(NumberObject.INTEGER);
				
		blankStringId.setValue("");
		blankset.addObject(blankStringId);
		
		blankNumberId.setValue(new Integer(0));
		
		blankset.setKey(blankNumberId);
		
		returnModel.add(blankset);
		int i=0;
		while(i < entries.size()){
			DataSet set = new DataSet();
			Object[] result = (Object[]) entries.get(i);
			//id
			Integer dropdownId = (Integer)result[0];
			//entry
			String dropdownText = (String)result[1];
			
			StringObject textObject = new StringObject();
			NumberObject numberId = new NumberObject(NumberObject.INTEGER);
			
			textObject.setValue(dropdownText);
			set.addObject(textObject);
			
			numberId.setValue(dropdownId);

			set.setKey(numberId);
			
			returnModel.add(set);
			
			i++;
		}		
		
		return returnModel;
	}

	private void setRpcErrors(List exceptionList, FormRPC rpcSend){
		//we need to get the keys and look them up in the resource bundle for internationalization
		for (int i=0; i<exceptionList.size();i++) {
			if(exceptionList.get(i) instanceof FieldErrorException)
			rpcSend.getField(((FieldErrorException)exceptionList.get(i)).getFieldName()).addError(openElisConstants.getString(((FieldErrorException)exceptionList.get(i)).getMessage()));
			else if(exceptionList.get(i) instanceof FormErrorException)
				rpcSend.addError(openElisConstants.getString(((FormErrorException)exceptionList.get(i)).getMessage()));
		}	
		rpcSend.status = IForm.INVALID_FORM;
    }
	
	public DataObject[] getXMLData(DataObject[] args) throws RPCException {
		// TODO Auto-generated method stub
		return null;
	}
}
