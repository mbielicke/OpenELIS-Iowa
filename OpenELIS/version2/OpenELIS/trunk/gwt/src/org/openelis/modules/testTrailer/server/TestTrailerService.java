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
package org.openelis.modules.testTrailer.server;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.openelis.domain.IdNameDO;
import org.openelis.domain.TestTrailerDO;
import org.openelis.gwt.common.EntityLockedException;
import org.openelis.gwt.common.FieldErrorException;
import org.openelis.gwt.common.FormErrorException;
import org.openelis.gwt.common.FormRPC;
import org.openelis.gwt.common.IForm;
import org.openelis.gwt.common.LastPageException;
import org.openelis.gwt.common.QueryException;
import org.openelis.gwt.common.RPCException;
import org.openelis.gwt.common.FormRPC.Status;
import org.openelis.gwt.common.data.AbstractField;
import org.openelis.gwt.common.data.DataModel;
import org.openelis.gwt.common.data.DataObject;
import org.openelis.gwt.common.data.DataSet;
import org.openelis.gwt.common.data.NumberObject;
import org.openelis.gwt.common.data.StringObject;
import org.openelis.gwt.server.ServiceUtils;
import org.openelis.gwt.services.AppScreenFormServiceInt;
import org.openelis.meta.TestTrailerMeta;
import org.openelis.metamap.StandardNoteMetaMap;
import org.openelis.metamap.TestTrailerMetaMap;
import org.openelis.persistence.EJBFactory;
import org.openelis.remote.TestTrailerRemote;
import org.openelis.server.constants.Constants;
import org.openelis.util.SessionManager;
import org.openelis.util.UTFResource;

public class TestTrailerService implements AppScreenFormServiceInt<FormRPC, DataSet, DataModel> {

	private static final int leftTableRowsPerPage = 9;
	
    private UTFResource openElisConstants= UTFResource.getBundle((String)SessionManager.getSession().getAttribute("locale"));
	
    private static final TestTrailerMetaMap TestTrailerMeta = new TestTrailerMetaMap();
    
	public DataModel commitQuery(FormRPC rpcSend, DataModel model) throws RPCException {
        List testTrailers = new ArrayList();
    //		if the rpc is null then we need to get the page
    		if(rpcSend == null){
                FormRPC rpc = (FormRPC)SessionManager.getSession().getAttribute("TestTrailerQuery");
    
    	        if(rpc == null)
    	        	throw new QueryException(openElisConstants.getString("queryExpiredException"));

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
            }else{
    			TestTrailerRemote remote = (TestTrailerRemote)EJBFactory.lookup("openelis/TestTrailerBean/remote");
    			
    			HashMap<String,AbstractField> fields = rpcSend.getFieldMap();
    			
    			try{
                    testTrailers = remote.query(fields,0,leftTableRowsPerPage);
    
        		}catch(Exception e){
        			throw new RPCException(e.getMessage());
        		}
                
                //need to save the rpc used to the encache
                SessionManager.getSession().setAttribute("TestTrailerQuery", rpcSend);
    		}
    		
            int i=0;
            if(model == null)
                model = new DataModel();
            else
                model.clear();
            while(i < testTrailers.size() && i < leftTableRowsPerPage) {
                IdNameDO resultDO = (IdNameDO)testTrailers.get(i);
                //org id
                Integer idResult = resultDO.getId();
                //name
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
		//remote interface to call the testTrailer bean
		TestTrailerRemote remote = (TestTrailerRemote)EJBFactory.lookup("openelis/TestTrailerBean/remote");
		TestTrailerDO newTestTrailerDO = new TestTrailerDO();
		
		//build the testTrailer DO from the form
		newTestTrailerDO = getTestTrailerDOFromRPC(rpcSend);
				
		//validate the fields on the backend
		List exceptionList = remote.validateForAdd(newTestTrailerDO);
		if(exceptionList.size() > 0){
			setRpcErrors(exceptionList, rpcSend);
			
			return rpcSend;
		} 
		
		//send the changes to the database
		Integer testTrailerId;
		try{
			testTrailerId = (Integer)remote.updateTestTrailer(newTestTrailerDO);
		}catch(Exception e){
			exceptionList = new ArrayList();
			exceptionList.add(e);
			
			setRpcErrors(exceptionList, rpcSend);
			return rpcSend;
		}
		
        newTestTrailerDO.setId(testTrailerId);

		//set the fields in the RPC
		setFieldsInRPC(rpcReturn, newTestTrailerDO);
	
		return rpcReturn;
	}

	public FormRPC commitUpdate(FormRPC rpcSend, FormRPC rpcReturn) throws RPCException {
    	//remote interface to call the test trailer bean
    	TestTrailerRemote remote = (TestTrailerRemote)EJBFactory.lookup("openelis/TestTrailerBean/remote");
    	TestTrailerDO newTestTrailerDO = new TestTrailerDO();
    	
    	//build the DO from the form
    	newTestTrailerDO = getTestTrailerDOFromRPC(rpcSend);
    	
    	//validate the fields on the backend
    	List exceptionList = remote.validateForUpdate(newTestTrailerDO);
    	if(exceptionList.size() > 0){
    		setRpcErrors(exceptionList, rpcSend);
    		
    		return rpcSend;
    	}
    	
    	//send the changes to the database
    	try{
    		remote.updateTestTrailer(newTestTrailerDO);
    	}catch(Exception e){
            if(e instanceof EntityLockedException)
                throw new RPCException(e.getMessage());
            
    		exceptionList = new ArrayList();
    		exceptionList.add(e);
    		
    		setRpcErrors(exceptionList, rpcSend);
    		return rpcSend;
    	}
    
    	//set the fields in the RPC
    	setFieldsInRPC(rpcReturn, newTestTrailerDO);
        
    	return rpcReturn;
    }

    public FormRPC commitDelete(DataSet key, FormRPC rpcReturn) throws RPCException {
		//remote interface to call the test trailer bean
		TestTrailerRemote remote = (TestTrailerRemote)EJBFactory.lookup("openelis/TestTrailerBean/remote");

		//validate the fields on the backend
		List exceptionList = remote.validateForDelete((Integer)((DataObject)key.getKey()).getValue());
		if(exceptionList.size() > 0){
			setRpcErrors(exceptionList, rpcReturn);
			
			return rpcReturn;
		} 
		
		try {
			remote.deleteTestTrailer((Integer)((DataObject)key.getKey()).getValue());
			
		} catch (Exception e) {
			exceptionList = new ArrayList();
			exceptionList.add(e);
			
			setRpcErrors(exceptionList, rpcReturn);
			return rpcReturn;
		}
		
		setFieldsInRPC(rpcReturn, new TestTrailerDO());
		
		return rpcReturn;
	}

	public FormRPC abort(DataSet key, FormRPC rpcReturn) throws RPCException {
    //		remote interface to call the testTrailer bean
    		TestTrailerRemote remote = (TestTrailerRemote)EJBFactory.lookup("openelis/TestTrailerBean/remote");
    		
    		
    		TestTrailerDO testTrailerDO = remote.getTestTrailerAndUnlock((Integer)((DataObject)key.getKey()).getValue(), SessionManager.getSession().getId());
    
    //		set the fields in the RPC
    		setFieldsInRPC(rpcReturn, testTrailerDO);
            
    		return rpcReturn;  
    	}

    public FormRPC fetch(DataSet key, FormRPC rpcReturn) throws RPCException {
//		remote interface to call the test trailer bean
		TestTrailerRemote remote = (TestTrailerRemote)EJBFactory.lookup("openelis/TestTrailerBean/remote");
		
		TestTrailerDO testTrailerDO = remote.getTestTrailer((Integer)((DataObject)key.getKey()).getValue());
		
//		set the fields in the RPC
		setFieldsInRPC(rpcReturn, testTrailerDO);

		return rpcReturn;
	}

	public FormRPC fetchForUpdate(DataSet key, FormRPC rpcReturn) throws RPCException {
//		remote interface to call the test trailer bean
		TestTrailerRemote remote = (TestTrailerRemote)EJBFactory.lookup("openelis/TestTrailerBean/remote");
		TestTrailerDO testTrailerDO = new TestTrailerDO();
		
		try{
			testTrailerDO = remote.getTestTrailerAndLock((Integer)((DataObject)key.getKey()).getValue(), SessionManager.getSession().getId());
		}catch(Exception e){
			throw new RPCException(e.getMessage());
		}
		
//		set the fields in the RPC
		setFieldsInRPC(rpcReturn, testTrailerDO);

		return rpcReturn;
	}

	public String getXML() throws RPCException {
    	return ServiceUtils.getXML(Constants.APP_ROOT+"/Forms/testTrailer.xsl");
    }

    public HashMap getXMLData() throws RPCException {
        // TODO Auto-generated method stub
        return null;
    }

    public HashMap getXMLData(HashMap args) throws RPCException {
    	// TODO Auto-generated method stub
    	return null;
    }

    private void setFieldsInRPC(FormRPC rpcReturn, TestTrailerDO testTrailerDO){
		rpcReturn.setFieldValue(TestTrailerMeta.getId(), testTrailerDO.getId());
		rpcReturn.setFieldValue(TestTrailerMeta.getName(), testTrailerDO.getName());
		rpcReturn.setFieldValue(TestTrailerMeta.getDescription(), testTrailerDO.getDescription());
		rpcReturn.setFieldValue(TestTrailerMeta.getText(), testTrailerDO.getText());		
	}
	
	private TestTrailerDO getTestTrailerDOFromRPC(FormRPC rpcSend){
		TestTrailerDO newTestTrailerDO = new TestTrailerDO();
		
		newTestTrailerDO.setId((Integer)rpcSend.getFieldValue(TestTrailerMeta.getId()));
		newTestTrailerDO.setName(((String) rpcSend.getFieldValue(TestTrailerMeta.getName())));
		newTestTrailerDO.setDescription(((String)rpcSend.getFieldValue(TestTrailerMeta.getDescription())));
		newTestTrailerDO.setText(((String)rpcSend.getFieldValue(TestTrailerMeta.getText())));
		
		return newTestTrailerDO;
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

}
