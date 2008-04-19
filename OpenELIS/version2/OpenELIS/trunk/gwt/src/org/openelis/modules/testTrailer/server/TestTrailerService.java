package org.openelis.modules.testTrailer.server;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import org.openelis.domain.TestTrailerDO;
import org.openelis.gwt.common.FormRPC;
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
import org.openelis.meta.TestTrailerMeta;
import org.openelis.persistence.CachingManager;
import org.openelis.persistence.EJBFactory;
import org.openelis.remote.TestTrailerRemote;
import org.openelis.server.constants.Constants;
import org.openelis.server.constants.UTFResource;
import org.openelis.util.SessionManager;

public class TestTrailerService implements AppScreenFormServiceInt {

	private static final int leftTableRowsPerPage = 10;
	
	private UTFResource openElisConstants= UTFResource.getBundle("org.openelis.modules.main.server.constants.OpenELISConstants",
			new Locale(((SessionManager.getSession() == null  || (String)SessionManager.getSession().getAttribute("locale") == null) 
					? "en" : (String)SessionManager.getSession().getAttribute("locale"))));
	
	public FormRPC abort(DataSet key, FormRPC rpcReturn) throws RPCException {
//		remote interface to call the testTrailer bean
		TestTrailerRemote remote = (TestTrailerRemote)EJBFactory.lookup("openelis/TestTrailerBean/remote");
		
		
		TestTrailerDO testTrailerDO = remote.getTestTrailerAndUnlock((Integer)key.getKey().getValue());

//		set the fields in the RPC
		setFieldsInRPC(rpcReturn, testTrailerDO);
        
		return rpcReturn;  
	}

	public FormRPC commitAdd(FormRPC rpcSend, FormRPC rpcReturn) throws RPCException {
//		remote interface to call the testTrailer bean
		TestTrailerRemote remote = (TestTrailerRemote)EJBFactory.lookup("openelis/TestTrailerBean/remote");
		TestTrailerDO newTestTrailerDO = new TestTrailerDO();
		
//		build the testTrailer DO from the form
		newTestTrailerDO = getTestTrailerDOFromRPC(rpcSend);
				
		//send the changes to the database
		Integer testTrailerId = (Integer)remote.updateTestTrailer(newTestTrailerDO);
		
//		lookup the changes from the database and build the rpc
		TestTrailerDO testTrailerDO = remote.getTestTrailer(testTrailerId);

//		set the fields in the RPC
		setFieldsInRPC(rpcReturn, testTrailerDO);
	
		return rpcReturn;
	}

	public FormRPC commitDelete(DataSet key, FormRPC rpcReturn) throws RPCException {
//		remote interface to call the test trailer bean
		TestTrailerRemote remote = (TestTrailerRemote)EJBFactory.lookup("openelis/TestTrailerBean/remote");
		
		try {
			remote.deleteTestTrailer((Integer)key.getKey().getValue());
			
		} catch (Exception e) {
			if(e instanceof RPCDeleteException){
				throw new RPCDeleteException("");
			}else
			throw new RPCException(e.getMessage());
		}	
		
		setFieldsInRPC(rpcReturn, new TestTrailerDO());
		
		return rpcReturn;
	}

	public DataModel commitQuery(FormRPC rpcSend, DataModel model) throws RPCException {
//		if the rpc is null then we need to get the page
		if(rpcSend == null){
//			need to get the query rpc out of the cache
	        FormRPC rpc = (FormRPC)CachingManager.getElement("screenQueryRpc", SessionManager.getSession().getAttribute("systemUserId")+":TestTrailer");

	        if(rpc == null)
	        	throw new QueryNotFoundException(openElisConstants.getString("queryExpiredException"));
			
	        List testTrailers = null;
	        TestTrailerRemote remote = (TestTrailerRemote)EJBFactory.lookup("openelis/TestTrailerBean/remote");
	        try{
	        	testTrailers = remote.query(rpc.getFieldMap(), (model.getPage()*leftTableRowsPerPage), leftTableRowsPerPage+1);
	        }catch(Exception e){
	        	if(e instanceof LastPageException){
	        		throw new LastPageException(openElisConstants.getString("lastPageException"));
	        	}else{
	        		throw new RPCException(e.getMessage());	
	        	}
	        }
	        
	        int i=0;
	        model.clear();
	        while(i < testTrailers.size() && i < leftTableRowsPerPage) {
	    	   	Object[] result = (Object[])testTrailers.get(i);
				//org id
				Integer idResult = (Integer)result[0];
				//name
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
			TestTrailerRemote remote = (TestTrailerRemote)EJBFactory.lookup("openelis/TestTrailerBean/remote");
			
			HashMap<String,AbstractField> fields = rpcSend.getFieldMap();
			
			List testTrailerNames = new ArrayList();
			try{
				testTrailerNames = remote.query(fields,0,leftTableRowsPerPage);

		}catch(Exception e){
			throw new RPCException(e.getMessage());
		}
		
		Iterator namesItr = testTrailerNames.iterator();
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
        CachingManager.putElement("screenQueryRpc", SessionManager.getSession().getAttribute("systemUserId")+":TestTrailer", rpcSend);
		}
		
		return model;
	}

	public FormRPC commitUpdate(FormRPC rpcSend, FormRPC rpcReturn) throws RPCException {
//		remote interface to call the test trailer bean
		TestTrailerRemote remote = (TestTrailerRemote)EJBFactory.lookup("openelis/TestTrailerBean/remote");
		TestTrailerDO newTestTrailerDO = new TestTrailerDO();
		
		//build the DO from the form
		newTestTrailerDO = getTestTrailerDOFromRPC(rpcSend);
		
//		send the changes to the database
		remote.updateTestTrailer(newTestTrailerDO);
		
		//lookup the changes from the database and build the rpc
		TestTrailerDO testTrailerDO = remote.getTestTrailer(newTestTrailerDO.getId());

//		set the fields in the RPC
		setFieldsInRPC(rpcReturn, testTrailerDO);
        
		return rpcReturn;
	}

	public FormRPC fetch(DataSet key, FormRPC rpcReturn) throws RPCException {
//		remote interface to call the test trailer bean
		TestTrailerRemote remote = (TestTrailerRemote)EJBFactory.lookup("openelis/TestTrailerBean/remote");
		
		TestTrailerDO testTrailerDO = remote.getTestTrailer((Integer)key.getKey().getValue());
		
//		set the fields in the RPC
		setFieldsInRPC(rpcReturn, testTrailerDO);

		return rpcReturn;
	}

	public FormRPC fetchForUpdate(DataSet key, FormRPC rpcReturn) throws RPCException {
//		remote interface to call the test trailer bean
		TestTrailerRemote remote = (TestTrailerRemote)EJBFactory.lookup("openelis/TestTrailerBean/remote");
		TestTrailerDO testTrailerDO = new TestTrailerDO();
		
		try{
			testTrailerDO = remote.getTestTrailerAndLock((Integer)key.getKey().getValue());
		}catch(Exception e){
			throw new RPCException(e.getMessage());
		}
		
//		set the fields in the RPC
		setFieldsInRPC(rpcReturn, testTrailerDO);

		return rpcReturn;
	}

	private void setFieldsInRPC(FormRPC rpcReturn, TestTrailerDO testTrailerDO){
		rpcReturn.setFieldValue(TestTrailerMeta.ID, testTrailerDO.getId());
		rpcReturn.setFieldValue(TestTrailerMeta.NAME, (testTrailerDO.getName() == null ? null : testTrailerDO.getName().trim()));
		rpcReturn.setFieldValue(TestTrailerMeta.DESCRIPTION, (testTrailerDO.getDescription() == null ? null : testTrailerDO.getDescription().trim()));
		rpcReturn.setFieldValue(TestTrailerMeta.TEXT, (testTrailerDO.getText() == null ? null : testTrailerDO.getText().trim()));		
	}
	
	private TestTrailerDO getTestTrailerDOFromRPC(FormRPC rpcSend){
		TestTrailerDO newTestTrailerDO = new TestTrailerDO();
		
		newTestTrailerDO.setId((Integer)rpcSend.getFieldValue(TestTrailerMeta.ID));
		newTestTrailerDO.setName(((String) rpcSend.getFieldValue(TestTrailerMeta.NAME)).trim());
		newTestTrailerDO.setDescription(((String)rpcSend.getFieldValue(TestTrailerMeta.DESCRIPTION)).trim());
		newTestTrailerDO.setText(((String)rpcSend.getFieldValue(TestTrailerMeta.TEXT)).trim());
		
		return newTestTrailerDO;
	}
	
	public String getXML() throws RPCException {
		return ServiceUtils.getXML(Constants.APP_ROOT+"/Forms/testTrailer.xsl");
	}

	public DataObject[] getXMLData() throws RPCException {
		// TODO Auto-generated method stub
		return null;
	}

	public DataObject[] getXMLData(DataObject[] args) throws RPCException {
		// TODO Auto-generated method stub
		return null;
	}

}
