package org.openelis.modules.analyte.server;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import org.openelis.domain.AnalyteDO;
import org.openelis.gwt.common.FieldErrorException;
import org.openelis.gwt.common.FormErrorException;
import org.openelis.gwt.common.FormRPC;
import org.openelis.gwt.common.IForm;
import org.openelis.gwt.common.LastPageException;
import org.openelis.gwt.common.QueryNotFoundException;
import org.openelis.gwt.common.RPCDeleteException;
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
import org.openelis.meta.AnalyteMeta;
import org.openelis.meta.AnalyteParentAnalyteMeta;
import org.openelis.persistence.CachingManager;
import org.openelis.persistence.EJBFactory;
import org.openelis.remote.AnalyteRemote;
import org.openelis.server.constants.Constants;
import org.openelis.server.constants.UTFResource;
import org.openelis.util.SessionManager;

public class AnalyteService implements AppScreenFormServiceInt, AutoCompleteServiceInt {

	private static final int leftTableRowsPerPage = 10;
	
	private UTFResource openElisConstants= UTFResource.getBundle("org.openelis.modules.main.server.constants.OpenELISConstants",
			new Locale(((SessionManager.getSession() == null  || (String)SessionManager.getSession().getAttribute("locale") == null) 
					? "en" : (String)SessionManager.getSession().getAttribute("locale"))));
	
	public FormRPC abort(DataSet key, FormRPC rpcReturn) throws RPCException {
//		remote interface to call the analyte bean
		AnalyteRemote remote = (AnalyteRemote)EJBFactory.lookup("openelis/AnalyteBean/remote");
		
		
		AnalyteDO analyteDO = remote.getAnalyteAndUnlock((Integer)key.getKey().getValue());

//		set the fields in the RPC
		setFieldsInRPC(rpcReturn, analyteDO);
        
		return rpcReturn;  
	}

	public FormRPC commitAdd(FormRPC rpcSend, FormRPC rpcReturn) throws RPCException {
//		 remote interface to call the analyte bean
		AnalyteRemote remote = (AnalyteRemote) EJBFactory.lookup("openelis/AnalyteBean/remote");
		AnalyteDO newAnalyteDO = new AnalyteDO();

		// build the analyte DO from the form
		newAnalyteDO = getAnalyteDOFromRPC(rpcSend);

		//validate the fields on the backend
		List exceptionList = remote.validateForAdd(newAnalyteDO);
		if(exceptionList.size() > 0){
			//we need to get the keys and look them up in the resource bundle for internationalization
			for (int i=0; i<exceptionList.size();i++) {
				if(exceptionList.get(i) instanceof FieldErrorException)
				rpcSend.getField(((FieldErrorException)exceptionList.get(i)).getFieldName()).addError(openElisConstants.getString(((FieldErrorException)exceptionList.get(i)).getMessage()));
				else if(exceptionList.get(i) instanceof FormErrorException)
					rpcSend.addError(openElisConstants.getString(((FormErrorException)exceptionList.get(i)).getMessage()));
			}	
			rpcSend.status = IForm.INVALID_FORM;
			return rpcSend;
		} 
		
		// send the changes to the database
		Integer analyteId;
		try{
			analyteId = (Integer) remote.updateAnalyte(newAnalyteDO);
		}catch(Exception e){
			if(e instanceof FieldErrorException){
				rpcSend.getField(((FieldErrorException)e).getFieldName()).addError(openElisConstants.getString(((FieldErrorException)e).getMessage()));
			}
				else if(e instanceof FormErrorException)
					rpcSend.addError(openElisConstants.getString(((FormErrorException)e).getMessage()));
			
			rpcSend.status = IForm.INVALID_FORM;
			return rpcSend;
		}

		// lookup the changes from the database and build the rpc
		AnalyteDO analyteDO = remote.getAnalyte(analyteId);

		// set the fields in the RPC
		setFieldsInRPC(rpcReturn, analyteDO);

		return rpcReturn;
	}

	public FormRPC commitDelete(DataSet key, FormRPC rpcReturn) throws RPCException {
//		remote interface to call the analyte bean
		AnalyteRemote remote = (AnalyteRemote)EJBFactory.lookup("openelis/AnalyteBean/remote");
		
		try {
			remote.deleteAnalyte((Integer)key.getKey().getValue());
			
		} catch (Exception e) {
			if(e instanceof RPCDeleteException){
				throw new RPCDeleteException(openElisConstants.getString("analyteDeleteException"));
			}else
			throw new RPCException(e.getMessage());
		}	
		
		setFieldsInRPC(rpcReturn, new AnalyteDO());
		
		return rpcReturn;
	}

	public DataModel commitQuery(FormRPC rpcSend, DataModel model) throws RPCException {
//		if the rpc is null then we need to get the page
		if(rpcSend == null){
//			need to get the query rpc out of the cache
	        FormRPC rpc = (FormRPC)CachingManager.getElement("screenQueryRpc", SessionManager.getSession().getAttribute("systemUserId")+":Analyte");

	        if(rpc == null)
	        	throw new QueryNotFoundException(openElisConstants.getString("queryExpiredException"));
			
	        List analytes = null;
	        AnalyteRemote remote = (AnalyteRemote)EJBFactory.lookup("openelis/AnalyteBean/remote");
	        try{
	        	analytes = remote.query(rpc.getFieldMap(), (model.getPage()*leftTableRowsPerPage), leftTableRowsPerPage+1);
	        }catch(Exception e){
	        	if(e instanceof LastPageException){
	        		throw new LastPageException(openElisConstants.getString("lastPageException"));
	        	}else{
	        		throw new RPCException(e.getMessage());	
	        	}
	        }
	        
	        int i=0;
	        model.clear();
	        while(i < analytes.size() && i < leftTableRowsPerPage) {
	    	   	Object[] result = (Object[])analytes.get(i);
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
				
				row.setKey(id);			
				row.addObject(name);
				model.add(row);
				i++;
	         } 

	        return model;
		}else{
			AnalyteRemote remote = (AnalyteRemote)EJBFactory.lookup("openelis/AnalyteBean/remote");
			
			HashMap<String,AbstractField> fields = rpcSend.getFieldMap();
			
			List analyteNames = new ArrayList();
			try{
				analyteNames = remote.query(fields,0,leftTableRowsPerPage);

		}catch(Exception e){
			throw new RPCException(e.getMessage());
		}
		
		Iterator namesItr = analyteNames.iterator();
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
			 row.setKey(idField);
			 row.addObject(nameField);

			 model.add(row);
		}
        
        //need to save the rpc used to the encache
        if(SessionManager.getSession().getAttribute("systemUserId") == null)
        	SessionManager.getSession().setAttribute("systemUserId", remote.getSystemUserId().toString());
        CachingManager.putElement("screenQueryRpc", SessionManager.getSession().getAttribute("systemUserId")+":Analyte", rpcSend);
		}
		
		return model;
	}

	public FormRPC commitUpdate(FormRPC rpcSend, FormRPC rpcReturn) throws RPCException {
//		remote interface to call the analyte bean
		AnalyteRemote remote = (AnalyteRemote)EJBFactory.lookup("openelis/AnalyteBean/remote");
		AnalyteDO newAnalyteDO = new AnalyteDO();
		List analytes = new ArrayList();

		//build the AnalyteDO from the form
		newAnalyteDO = getAnalyteDOFromRPC(rpcSend);
		
		//validate the fields on the backend
		List exceptionList = remote.validateForUpdate(newAnalyteDO);
		if(exceptionList.size() > 0){
			//we need to get the keys and look them up in the resource bundle for internationalization
			for (int i=0; i<exceptionList.size();i++) {
				if(exceptionList.get(i) instanceof FieldErrorException)
				rpcSend.getField(((FieldErrorException)exceptionList.get(i)).getFieldName()).addError(openElisConstants.getString(((FieldErrorException)exceptionList.get(i)).getMessage()));
				else if(exceptionList.get(i) instanceof FormErrorException)
					rpcSend.addError(openElisConstants.getString(((FormErrorException)exceptionList.get(i)).getMessage()));
			}	
			rpcSend.status = IForm.INVALID_FORM;
			return rpcSend;
		} 
		
		//send the changes to the database
		try{
			remote.updateAnalyte(newAnalyteDO);
		}catch(Exception e){
			if(e instanceof FieldErrorException){
				rpcSend.getField(((FieldErrorException)e).getFieldName()).addError(openElisConstants.getString(((FieldErrorException)e).getMessage()));
			}
				else if(e instanceof FormErrorException)
					rpcSend.addError(openElisConstants.getString(((FormErrorException)e).getMessage()));
			
			rpcSend.status = IForm.INVALID_FORM;
			return rpcSend;
		}
		
		//lookup the changes from the database and build the rpc
		AnalyteDO analyteDO = remote.getAnalyte(newAnalyteDO.getId());

//		set the fields in the RPC
		setFieldsInRPC(rpcReturn, analyteDO);
		
		return rpcReturn;
	}

	public FormRPC fetch(DataSet key, FormRPC rpcReturn) throws RPCException {
//		remote interface to call the storage unit bean
		AnalyteRemote remote = (AnalyteRemote)EJBFactory.lookup("openelis/AnalyteBean/remote");
		
		AnalyteDO analyteDO = remote.getAnalyte((Integer)key.getKey().getValue());
		
//		set the fields in the RPC
		setFieldsInRPC(rpcReturn, analyteDO);
		
		return rpcReturn;
	}

	public FormRPC fetchForUpdate(DataSet key, FormRPC rpcReturn) throws RPCException {
//		 remote interface to call the analyte bean
		AnalyteRemote remote = (AnalyteRemote) EJBFactory.lookup("openelis/AnalyteBean/remote");

		AnalyteDO analyteDO = new AnalyteDO();
		try {
			analyteDO = remote.getAnalyteAndLock((Integer) key.getKey().getValue());
		} catch (Exception e) {
			throw new RPCException(e.getMessage());
		}

		// set the fields in the RPC
		setFieldsInRPC(rpcReturn, analyteDO);

		return rpcReturn;
	}

	private void setFieldsInRPC(FormRPC rpcReturn, AnalyteDO analyteDO){
		rpcReturn.setFieldValue(AnalyteMeta.ID, analyteDO.getId());
		rpcReturn.setFieldValue(AnalyteMeta.NAME, (analyteDO.getName() == null ? null : analyteDO.getName().trim()));
		rpcReturn.setFieldValue(AnalyteMeta.IS_ACTIVE, (analyteDO.getIsActive() == null ? null : analyteDO.getIsActive().trim()));
		rpcReturn.setFieldValue(AnalyteMeta.ANALYTE_GROUP, analyteDO.getAnalyteGroup());
		rpcReturn.setFieldValue(AnalyteMeta.EXTERNAL_ID, (analyteDO.getExternalId() == null ? null : analyteDO.getExternalId().trim()));
		
//		we need to create a dataset for the parent organization auto complete
		if(analyteDO.getParentAnalyteId() == null)
			rpcReturn.setFieldValue(AnalyteParentAnalyteMeta.NAME, null);
		else{
			DataSet parentAnalyteSet = new DataSet();
			NumberObject id = new NumberObject();
			StringObject text = new StringObject();
			id.setType("integer");
			id.setValue(analyteDO.getParentAnalyteId());
			text.setValue(analyteDO.getParentAnalyte().trim());
			parentAnalyteSet.setKey(id);
			parentAnalyteSet.addObject(text);
			rpcReturn.setFieldValue(AnalyteParentAnalyteMeta.NAME, parentAnalyteSet);
		}
	}
	
	private AnalyteDO getAnalyteDOFromRPC(FormRPC rpcSend) {
		AnalyteDO newAnalyteDO = new AnalyteDO();

		newAnalyteDO.setId((Integer) rpcSend.getFieldValue(AnalyteMeta.ID));
		newAnalyteDO.setName(((String) rpcSend.getFieldValue(AnalyteMeta.NAME)).trim());
		newAnalyteDO.setIsActive(((String) rpcSend.getFieldValue(AnalyteMeta.IS_ACTIVE)).trim());
		newAnalyteDO.setAnalyteGroup((Integer) rpcSend.getFieldValue(AnalyteMeta.ANALYTE_GROUP));
		newAnalyteDO.setParentAnalyteId((Integer) rpcSend.getFieldValue(AnalyteParentAnalyteMeta.NAME));
		newAnalyteDO.setExternalId(((String) rpcSend.getFieldValue(AnalyteMeta.EXTERNAL_ID)).trim());		

		return newAnalyteDO;
	}
	
	public String getXML() throws RPCException {
		return ServiceUtils.getXML(Constants.APP_ROOT+"/Forms/analyte.xsl");
	}

	public DataObject[] getXMLData() throws RPCException {
		// TODO Auto-generated method stub
		return null;
	}

	public DataModel getDisplay(String cat, DataModel model, AbstractField value) throws RPCException {
		return null;
	}

	public DataModel getMatches(String cat, DataModel model, String match) throws RPCException {
		if(cat.equals("parentAnalyte"))
			return getParentAnalyteMatches(match);
		
		return null;		
	}
	
	private DataModel getParentAnalyteMatches(String match){
		AnalyteRemote remote = (AnalyteRemote)EJBFactory.lookup("openelis/AnalyteBean/remote");
		DataModel dataModel = new DataModel();

		//lookup by name
		List autoCompleteList = remote.autoCompleteLookupByName(match+"%", 10);
		Iterator itr = autoCompleteList.iterator();
		
		while(itr.hasNext()){
			Object[] result = (Object[]) itr.next();
			//org id
			Integer analyteId = (Integer)result[0];
			//org name
			String name = (String)result[1];			
			
			DataSet data = new DataSet();
			//hidden id
			NumberObject idObject = new NumberObject();
			idObject.setType("integer");
			idObject.setValue(analyteId);
			data.setKey(idObject);
			//columns
			StringObject nameObject = new StringObject();
			nameObject.setValue(name.trim());
			data.addObject(nameObject);
			
			//add the dataset to the datamodel
			dataModel.add(data);
		}
		
		return dataModel;		
	}
}
