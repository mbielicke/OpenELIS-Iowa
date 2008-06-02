package org.openelis.modules.analyte.server;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.openelis.domain.AnalyteDO;
import org.openelis.domain.IdNameDO;
import org.openelis.gwt.common.EntityLockedException;
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
import org.openelis.gwt.common.data.DropDownField;
import org.openelis.gwt.common.data.NumberObject;
import org.openelis.gwt.common.data.StringObject;
import org.openelis.gwt.server.ServiceUtils;
import org.openelis.gwt.services.AppScreenFormServiceInt;
import org.openelis.gwt.services.AutoCompleteServiceInt;
import org.openelis.meta.AnalyteMeta;
import org.openelis.meta.AnalyteParentAnalyteMeta;
import org.openelis.persistence.EJBFactory;
import org.openelis.remote.AnalyteRemote;
import org.openelis.server.constants.Constants;
import org.openelis.util.SessionManager;
import org.openelis.util.UTFResource;

public class AnalyteService implements AppScreenFormServiceInt, AutoCompleteServiceInt {

	private static final int leftTableRowsPerPage = 10;
	
    private UTFResource openElisConstants= UTFResource.getBundle((String)SessionManager.getSession().getAttribute("locale"));
    
	public DataModel commitQuery(FormRPC rpcSend, DataModel model) throws RPCException {
        List analyteNames = new ArrayList();
    //		if the rpc is null then we need to get the page
    		if(rpcSend == null){
    //			need to get the query rpc out of the cache
                FormRPC rpc = (FormRPC)SessionManager.getSession().getAttribute("AnalyteQuery");
    
    	        if(rpc == null)
    	        	throw new QueryNotFoundException(openElisConstants.getString("queryExpiredException"));

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
            model.clear();
            while(i < analyteNames.size() && i < leftTableRowsPerPage) {
                IdNameDO resultDO = (IdNameDO)analyteNames.get(i);
                //org id
                Integer idResult = resultDO.getId();
                //org name
                String nameResult = resultDO.getName();

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
			setRpcErrors(exceptionList, rpcSend);
			
			return rpcSend;
		} 
		
		// send the changes to the database
		Integer analyteId;
		try{
			analyteId = (Integer) remote.updateAnalyte(newAnalyteDO);
		}catch(Exception e){
			exceptionList = new ArrayList();
			exceptionList.add(e);
			
			setRpcErrors(exceptionList, rpcSend);
			return rpcSend;
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
    		
    		//validate the fields on the backend
    		List exceptionList = remote.validateForUpdate(newAnalyteDO);
    		if(exceptionList.size() > 0){
    			setRpcErrors(exceptionList, rpcSend);
    			
    			return rpcSend;
    		} 
    		
    		//send the changes to the database
    		try{
    			remote.updateAnalyte(newAnalyteDO);
    		}catch(Exception e){
                if(e instanceof EntityLockedException)
                    throw new RPCException(e.getMessage());
                
    			exceptionList = new ArrayList();
    			exceptionList.add(e);
    			
    			setRpcErrors(exceptionList, rpcSend);
    			return rpcSend;
    		}
    
    //		set the fields in the RPC
    		setFieldsInRPC(rpcReturn, newAnalyteDO);
    		
    		return rpcReturn;
    	}

    public FormRPC commitDelete(DataSet key, FormRPC rpcReturn) throws RPCException {
//		remote interface to call the analyte bean
		AnalyteRemote remote = (AnalyteRemote)EJBFactory.lookup("openelis/AnalyteBean/remote");
		
		//validate the fields on the backend
		List exceptionList = remote.validateForDelete((Integer)key.getKey().getValue());
		if(exceptionList.size() > 0){
			setRpcErrors(exceptionList, rpcReturn);
			
			return rpcReturn;
		} 
		
		try {
			remote.deleteAnalyte((Integer)key.getKey().getValue());
			
		} catch (Exception e) {
			exceptionList = new ArrayList();
			exceptionList.add(e);
			
			setRpcErrors(exceptionList, rpcReturn);
			return rpcReturn;
		}	
		
		setFieldsInRPC(rpcReturn, new AnalyteDO());
		
		return rpcReturn;
	}

	public FormRPC abort(DataSet key, FormRPC rpcReturn) throws RPCException {
    //		remote interface to call the analyte bean
    		AnalyteRemote remote = (AnalyteRemote)EJBFactory.lookup("openelis/AnalyteBean/remote");
    		
    		
    		AnalyteDO analyteDO = remote.getAnalyteAndUnlock((Integer)key.getKey().getValue());
    
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
		rpcReturn.setFieldValue(AnalyteMeta.ID, analyteDO.getId());
		rpcReturn.setFieldValue(AnalyteMeta.NAME, (analyteDO.getName() == null ? null : analyteDO.getName()));
		rpcReturn.setFieldValue(AnalyteMeta.IS_ACTIVE, (analyteDO.getIsActive() == null ? null : analyteDO.getIsActive()));
		rpcReturn.setFieldValue(AnalyteMeta.ANALYTE_GROUP, analyteDO.getAnalyteGroup());
		rpcReturn.setFieldValue(AnalyteMeta.EXTERNAL_ID, (analyteDO.getExternalId() == null ? null : analyteDO.getExternalId()));
		
//		we need to create a dataset for the parent organization auto complete
		if(analyteDO.getParentAnalyteId() == null)
			rpcReturn.setFieldValue(AnalyteParentAnalyteMeta.NAME, null);
		else{
			DataSet parentAnalyteSet = new DataSet();
			NumberObject id = new NumberObject(NumberObject.INTEGER);
			StringObject text = new StringObject();
			id.setValue(analyteDO.getParentAnalyteId());
			text.setValue(analyteDO.getParentAnalyte());
			parentAnalyteSet.setKey(id);
			parentAnalyteSet.addObject(text);
			rpcReturn.setFieldValue(AnalyteParentAnalyteMeta.NAME, parentAnalyteSet);
		}
	}
	
	private AnalyteDO getAnalyteDOFromRPC(FormRPC rpcSend) {
		AnalyteDO newAnalyteDO = new AnalyteDO();

		newAnalyteDO.setId((Integer) rpcSend.getFieldValue(AnalyteMeta.ID));
		newAnalyteDO.setName(((String) rpcSend.getFieldValue(AnalyteMeta.NAME)));
		newAnalyteDO.setIsActive(((String) rpcSend.getFieldValue(AnalyteMeta.IS_ACTIVE)));
		newAnalyteDO.setAnalyteGroup((Integer) rpcSend.getFieldValue(AnalyteMeta.ANALYTE_GROUP));
		newAnalyteDO.setParentAnalyteId((Integer) rpcSend.getFieldValue(AnalyteParentAnalyteMeta.NAME));
        newAnalyteDO.setParentAnalyte((String)((DropDownField)rpcSend.getField(AnalyteParentAnalyteMeta.NAME)).getTextValue());
		newAnalyteDO.setExternalId(((String) rpcSend.getFieldValue(AnalyteMeta.EXTERNAL_ID)));		

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
			NumberObject idObject = new NumberObject(NumberObject.INTEGER);
			idObject.setValue(analyteId);
			data.setKey(idObject);
			//columns
			StringObject nameObject = new StringObject();
			nameObject.setValue(name);
			data.addObject(nameObject);
			
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
		rpcSend.status = IForm.Status.invalid;
    }
}
