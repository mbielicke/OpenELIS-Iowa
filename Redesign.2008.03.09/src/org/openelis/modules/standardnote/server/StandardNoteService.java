package org.openelis.modules.standardnote.server;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import org.openelis.domain.StandardNoteDO;
import org.openelis.gwt.common.FormRPC;
import org.openelis.gwt.common.LastPageException;
import org.openelis.gwt.common.QueryNotFoundException;
import org.openelis.gwt.common.RPCException;
import org.openelis.gwt.common.data.AbstractField;
import org.openelis.gwt.common.data.BooleanObject;
import org.openelis.gwt.common.data.DataModel;
import org.openelis.gwt.common.data.DataObject;
import org.openelis.gwt.common.data.DataSet;
import org.openelis.gwt.common.data.ModelField;
import org.openelis.gwt.common.data.NumberObject;
import org.openelis.gwt.common.data.StringObject;
import org.openelis.gwt.server.ServiceUtils;
import org.openelis.gwt.services.AppScreenFormServiceInt;
import org.openelis.gwt.services.AutoCompleteServiceInt;
import org.openelis.persistence.CachingManager;
import org.openelis.persistence.EJBFactory;
import org.openelis.remote.CategoryRemote;
import org.openelis.remote.StandardNoteRemote;
import org.openelis.server.constants.Constants;
import org.openelis.server.constants.UTFResource;
import org.openelis.util.SessionManager;

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
		
		
		StandardNoteDO standardNoteDO = remote.getStandardNoteAndUnlock((Integer)key.getObject(0).getValue());

//		set the fields in the RPC
		rpcReturn.setFieldValue("id", standardNoteDO.getId());
		rpcReturn.setFieldValue("description", standardNoteDO.getDescription().trim());
		rpcReturn.setFieldValue("name", standardNoteDO.getName().trim());
		rpcReturn.setFieldValue("text", standardNoteDO.getText().trim());
		rpcReturn.setFieldValue("typeId", standardNoteDO.getType());
        
      return rpcReturn;  
	}

	public FormRPC commitAdd(FormRPC rpcSend, FormRPC rpcReturn) throws RPCException {
//		remote interface to call the standardNote bean
		StandardNoteRemote remote = (StandardNoteRemote)EJBFactory.lookup("openelis/StandardNoteBean/remote");
		StandardNoteDO newStandardNoteDO = new StandardNoteDO();

//		build the storage unit DO from the form
		newStandardNoteDO.setDescription((String)rpcSend.getFieldValue("description"));
		newStandardNoteDO.setName((String)rpcSend.getFieldValue("name"));
		newStandardNoteDO.setText((String)rpcSend.getFieldValue("text"));
		newStandardNoteDO.setType((Integer)rpcSend.getFieldValue("typeId"));
		
		//send the changes to the database
		Integer standardNoteId = (Integer)remote.updateStandardNote(newStandardNoteDO);
		
//		lookup the changes from the database and build the rpc
		StandardNoteDO standardNoteDO = remote.getStandardNote(standardNoteId);

//		set the fields in the RPC
		rpcReturn.setFieldValue("id", standardNoteDO.getId());
		rpcReturn.setFieldValue("description", standardNoteDO.getDescription().trim());
		rpcReturn.setFieldValue("name", standardNoteDO.getName().trim());
		rpcReturn.setFieldValue("text", standardNoteDO.getText().trim());
		rpcReturn.setFieldValue("typeId", standardNoteDO.getType());
		
		return rpcReturn;
	}

	public FormRPC commitDelete(DataSet key, FormRPC rpcReturn) throws RPCException {
//		remote interface to call the standard note bean
		StandardNoteRemote remote = (StandardNoteRemote)EJBFactory.lookup("openelis/StandardNoteBean/remote");
		
		try {
			remote.deleteStandardNote((Integer)key.getObject(0).getValue());
			
		} catch (Exception e) {
			//if(e instanceof RPCDeleteException){
			//	throw new RPCDeleteException(openElisConstants.getString("storageLocDeleteException"));
			//}else
			throw new RPCException(e.getMessage());
		}	
		
		rpcReturn.setFieldValue("id", null);
		rpcReturn.setFieldValue("description", null);
		rpcReturn.setFieldValue("name", null);
		rpcReturn.setFieldValue("text", null);
		rpcReturn.setFieldValue("typeId", null);
		
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
				NumberObject id = new NumberObject();
				StringObject name = new StringObject();
				id.setType("integer");
				name.setValue(nameResult);
				id.setValue(idResult);
				
				row.addObject(id);			
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

			 NumberObject idField = new NumberObject();
			 idField.setType("integer");
			 StringObject nameField = new StringObject();
			 nameField.setValue(name);
      
			 idField.setValue(id);
			 row.addObject(idField);
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
		newStandardNoteDO.setId((Integer)rpcSend.getFieldValue("id"));
		newStandardNoteDO.setDescription((String)rpcSend.getFieldValue("description"));
		newStandardNoteDO.setName((String)rpcSend.getFieldValue("name"));
		newStandardNoteDO.setText((String)rpcSend.getFieldValue("text"));
		newStandardNoteDO.setType((Integer)rpcSend.getFieldValue("typeId"));

//		send the changes to the database
		remote.updateStandardNote(newStandardNoteDO);
		
		//lookup the changes from the database and build the rpc
		StandardNoteDO standardNoteDO = remote.getStandardNote(newStandardNoteDO.getId());

//		set the fields in the RPC
		rpcReturn.setFieldValue("id", standardNoteDO.getId());
		rpcReturn.setFieldValue("description", standardNoteDO.getDescription().trim());
		rpcReturn.setFieldValue("name", standardNoteDO.getName().trim());
		rpcReturn.setFieldValue("text", standardNoteDO.getText().trim());
		rpcReturn.setFieldValue("typeId", standardNoteDO.getType());
		
		return rpcReturn;
	}

	public FormRPC fetch(DataSet key, FormRPC rpcReturn) throws RPCException {
//		remote interface to call the standard note bean
		StandardNoteRemote remote = (StandardNoteRemote)EJBFactory.lookup("openelis/StandardNoteBean/remote");
		
		StandardNoteDO standardNoteDO = remote.getStandardNote((Integer)key.getObject(0).getValue());
		
//		set the fields in the RPC
		rpcReturn.setFieldValue("id", standardNoteDO.getId());
		rpcReturn.setFieldValue("description", standardNoteDO.getDescription().trim());
		rpcReturn.setFieldValue("name", standardNoteDO.getName().trim());
		rpcReturn.setFieldValue("text", standardNoteDO.getText().trim());
		rpcReturn.setFieldValue("typeId", standardNoteDO.getType());
		
		return rpcReturn;
	}

	public FormRPC fetchForUpdate(DataSet key, FormRPC rpcReturn) throws RPCException {
//		remote interface to call the standard note bean
		StandardNoteRemote remote = (StandardNoteRemote)EJBFactory.lookup("openelis/StandardNoteBean/remote");
		StandardNoteDO standardNoteDO = new StandardNoteDO();
		
		try{
			standardNoteDO = remote.getStandardNoteAndLock((Integer)key.getObject(0).getValue());
		}catch(Exception e){
			throw new RPCException(e.getMessage());
		}
		
//		set the fields in the RPC
		rpcReturn.setFieldValue("id", standardNoteDO.getId());
		rpcReturn.setFieldValue("description", standardNoteDO.getDescription().trim());
		rpcReturn.setFieldValue("name", standardNoteDO.getName().trim());
		rpcReturn.setFieldValue("text", standardNoteDO.getText().trim());
		rpcReturn.setFieldValue("typeId", standardNoteDO.getType());
		
		return rpcReturn;
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
		// TODO Auto-generated method stub
		return null;
	}

	public DataModel getMatches(String cat, DataModel model, String match) {
		// TODO Auto-generated method stub
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
		NumberObject blankNumberId = new NumberObject();
		BooleanObject blankSelected = new BooleanObject();
				
		blankStringId.setValue("");
		blankset.addObject(blankStringId);
		
		blankNumberId.setType("integer");
		blankNumberId.setValue(new Integer(0));
		
		blankset.addObject(blankNumberId);
		
		blankSelected.setValue(new Boolean(false));
		blankset.addObject(blankSelected);
		
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
			NumberObject numberId = new NumberObject();
			BooleanObject selected = new BooleanObject();
			
			textObject.setValue(dropdownText);
			set.addObject(textObject);
			
			numberId.setType("integer");
			numberId.setValue(dropdownId);
			set.addObject(numberId);
			
			selected.setValue(new Boolean(false));
			set.addObject(selected);
			
			returnModel.add(set);
			
			i++;
		}		
		
		return returnModel;
	}
}
