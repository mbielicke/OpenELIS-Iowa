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

import org.openelis.domain.IdNameDO;
import org.openelis.domain.TestTrailerDO;
import org.openelis.gwt.common.EntityLockedException;
import org.openelis.gwt.common.FieldErrorException;
import org.openelis.gwt.common.Form;
import org.openelis.gwt.common.FormErrorException;
import org.openelis.gwt.common.LastPageException;
import org.openelis.gwt.common.QueryException;
import org.openelis.gwt.common.RPC;
import org.openelis.gwt.common.RPCException;
import org.openelis.gwt.common.data.AbstractField;
import org.openelis.gwt.common.data.Data;
import org.openelis.gwt.common.data.DataModel;
import org.openelis.gwt.common.data.DataObject;
import org.openelis.gwt.common.data.DataSet;
import org.openelis.gwt.common.data.NumberObject;
import org.openelis.gwt.common.data.StringObject;
import org.openelis.gwt.server.ServiceUtils;
import org.openelis.gwt.services.AppScreenFormServiceInt;
import org.openelis.metamap.TestTrailerMetaMap;
import org.openelis.persistence.EJBFactory;
import org.openelis.remote.TestTrailerRemote;
import org.openelis.server.constants.Constants;
import org.openelis.util.SessionManager;
import org.openelis.util.UTFResource;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class TestTrailerService implements AppScreenFormServiceInt<RPC,DataModel<DataSet>> {

	private static final int leftTableRowsPerPage = 9;
	
    private UTFResource openElisConstants= UTFResource.getBundle((String)SessionManager.getSession().getAttribute("locale"));
	
    private static final TestTrailerMetaMap TestTrailerMeta = new TestTrailerMetaMap();
    
	public DataModel<DataSet> commitQuery(Form form, DataModel<DataSet> model) throws RPCException {
        List testTrailers = new ArrayList();
    //		if the rpc is null then we need to get the page
    		if(form == null){
                form = (Form)SessionManager.getSession().getAttribute("TestTrailerQuery");
    
    	        if(form == null)
    	        	throw new QueryException(openElisConstants.getString("queryExpiredException"));

    	        TestTrailerRemote remote = (TestTrailerRemote)EJBFactory.lookup("openelis/TestTrailerBean/remote");
    	        try{
    	        	testTrailers = remote.query(form.getFieldMap(), (model.getPage()*leftTableRowsPerPage), leftTableRowsPerPage+1);
    	        }catch(Exception e){
    	        	if(e instanceof LastPageException){
    	        		throw new LastPageException(openElisConstants.getString("lastPageException"));
    	        	}else{
    	        		throw new RPCException(e.getMessage());	
    	        	}
    	        }
            }else{
    			TestTrailerRemote remote = (TestTrailerRemote)EJBFactory.lookup("openelis/TestTrailerBean/remote");
    			
    			HashMap<String,AbstractField> fields = form.getFieldMap();
    			
    			try{
                    testTrailers = remote.query(fields,0,leftTableRowsPerPage);
    
        		}catch(Exception e){
        			throw new RPCException(e.getMessage());
        		}
                
                //need to save the rpc used to the encache
                SessionManager.getSession().setAttribute("TestTrailerQuery", form);
    		}
    		
            int i=0;
            if(model == null)
                model = new DataModel<DataSet>();
            else
                model.clear();
            while(i < testTrailers.size() && i < leftTableRowsPerPage) {
                IdNameDO resultDO = (IdNameDO)testTrailers.get(i);
                model.add(new DataSet<Data>(new NumberObject(resultDO.getId()),new StringObject(resultDO.getName())));
                i++;
             } 
            
    		return model;
    	}

    public RPC commitAdd(RPC rpc) throws RPCException {
		//remote interface to call the testTrailer bean
		TestTrailerRemote remote = (TestTrailerRemote)EJBFactory.lookup("openelis/TestTrailerBean/remote");
		TestTrailerDO newTestTrailerDO = new TestTrailerDO();
		
		//build the testTrailer DO from the form
		newTestTrailerDO = getTestTrailerDOFromRPC(rpc.form);
				
		//validate the fields on the backend
		List exceptionList = remote.validateForAdd(newTestTrailerDO);
		if(exceptionList.size() > 0){
			setRpcErrors(exceptionList, rpc.form);
			
			return rpc;
		} 
		
		//send the changes to the database
		Integer testTrailerId;
		try{
			testTrailerId = (Integer)remote.updateTestTrailer(newTestTrailerDO);
		}catch(Exception e){
			exceptionList = new ArrayList();
			exceptionList.add(e);
			
			setRpcErrors(exceptionList, rpc.form);
			return rpc;
		}
		
        newTestTrailerDO.setId(testTrailerId);

		//set the fields in the RPC
		setFieldsInRPC(rpc.form, newTestTrailerDO);
	
		return rpc;
	}

	public RPC commitUpdate(RPC rpc) throws RPCException {
    	//remote interface to call the test trailer bean
    	TestTrailerRemote remote = (TestTrailerRemote)EJBFactory.lookup("openelis/TestTrailerBean/remote");
    	TestTrailerDO newTestTrailerDO = new TestTrailerDO();
    	
    	//build the DO from the form
    	newTestTrailerDO = getTestTrailerDOFromRPC(rpc.form);
    	
    	//validate the fields on the backend
    	List exceptionList = remote.validateForUpdate(newTestTrailerDO);
    	if(exceptionList.size() > 0){
    		setRpcErrors(exceptionList, rpc.form);
    		
    		return rpc;
    	}
    	
    	//send the changes to the database
    	try{
    		remote.updateTestTrailer(newTestTrailerDO);
    	}catch(Exception e){
            if(e instanceof EntityLockedException)
                throw new RPCException(e.getMessage());
            
    		exceptionList = new ArrayList();
    		exceptionList.add(e);
    		
    		setRpcErrors(exceptionList, rpc.form);
    		return rpc;
    	}
    
    	//set the fields in the RPC
    	setFieldsInRPC(rpc.form, newTestTrailerDO);
        
    	return rpc;
    }

    public RPC commitDelete(RPC rpc) throws RPCException {
		//remote interface to call the test trailer bean
		TestTrailerRemote remote = (TestTrailerRemote)EJBFactory.lookup("openelis/TestTrailerBean/remote");

		//validate the fields on the backend
		List exceptionList = remote.validateForDelete((Integer)((DataObject)((DataSet)rpc.key).getKey()).getValue());
		if(exceptionList.size() > 0){
			setRpcErrors(exceptionList, rpc.form);
			
			return rpc;
		} 
		
		try {
			remote.deleteTestTrailer((Integer)((DataObject)((DataSet)rpc.key).getKey()).getValue());
			
		} catch (Exception e) {
			exceptionList = new ArrayList();
			exceptionList.add(e);
			
			setRpcErrors(exceptionList, rpc.form);
			return rpc;
		}
		
		setFieldsInRPC(rpc.form, new TestTrailerDO());
		
		return rpc;
	}

	public RPC abort(RPC rpc) throws RPCException {
    //		remote interface to call the testTrailer bean
    		TestTrailerRemote remote = (TestTrailerRemote)EJBFactory.lookup("openelis/TestTrailerBean/remote");
    		
    		
    		TestTrailerDO testTrailerDO = remote.getTestTrailerAndUnlock((Integer)((DataObject)((DataSet)rpc.key).getKey()).getValue(), SessionManager.getSession().getId());
    
    //		set the fields in the RPC
    		setFieldsInRPC(rpc.form, testTrailerDO);
            
    		return rpc;  
    	}

    public RPC fetch(RPC rpc) throws RPCException {
//		remote interface to call the test trailer bean
		TestTrailerRemote remote = (TestTrailerRemote)EJBFactory.lookup("openelis/TestTrailerBean/remote");
		
		TestTrailerDO testTrailerDO = remote.getTestTrailer((Integer)((DataObject)((DataSet)rpc.key).getKey()).getValue());
		
//		set the fields in the RPC
		setFieldsInRPC(rpc.form, testTrailerDO);

		return rpc;
	}

	public RPC fetchForUpdate(RPC rpc) throws RPCException {
//		remote interface to call the test trailer bean
		TestTrailerRemote remote = (TestTrailerRemote)EJBFactory.lookup("openelis/TestTrailerBean/remote");
		TestTrailerDO testTrailerDO = new TestTrailerDO();
		
		try{
			testTrailerDO = remote.getTestTrailerAndLock((Integer)((DataObject)((DataSet)rpc.key).getKey()).getValue(), SessionManager.getSession().getId());
		}catch(Exception e){
			throw new RPCException(e.getMessage());
		}
		
//		set the fields in the RPC
		setFieldsInRPC(rpc.form, testTrailerDO);

		return rpc;
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

    public RPC getScreen(RPC rpc) {
        return rpc;
    }
    
    private void setFieldsInRPC(Form form, TestTrailerDO testTrailerDO){
		form.setFieldValue(TestTrailerMeta.getId(), testTrailerDO.getId());
		form.setFieldValue(TestTrailerMeta.getName(), testTrailerDO.getName());
		form.setFieldValue(TestTrailerMeta.getDescription(), testTrailerDO.getDescription());
		form.setFieldValue(TestTrailerMeta.getText(), testTrailerDO.getText());		
	}
	
	private TestTrailerDO getTestTrailerDOFromRPC(Form form){
		TestTrailerDO newTestTrailerDO = new TestTrailerDO();
		
		newTestTrailerDO.setId((Integer)form.getFieldValue(TestTrailerMeta.getId()));
		newTestTrailerDO.setName(((String) form.getFieldValue(TestTrailerMeta.getName())));
		newTestTrailerDO.setDescription(((String)form.getFieldValue(TestTrailerMeta.getDescription())));
		newTestTrailerDO.setText(((String)form.getFieldValue(TestTrailerMeta.getText())));
		
		return newTestTrailerDO;
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

}
