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
import org.openelis.gwt.common.Query;
import org.openelis.gwt.common.QueryException;
import org.openelis.gwt.common.RPCException;
import org.openelis.gwt.common.ValidationErrorsList;
import org.openelis.gwt.common.data.AbstractField;
import org.openelis.gwt.common.data.StringObject;
import org.openelis.gwt.common.data.TableDataModel;
import org.openelis.gwt.common.data.TableDataRow;
import org.openelis.gwt.server.ServiceUtils;
import org.openelis.gwt.services.AppScreenFormServiceInt;
import org.openelis.metamap.TestTrailerMetaMap;
import org.openelis.modules.testTrailer.client.TestTrailerForm;
import org.openelis.persistence.EJBFactory;
import org.openelis.remote.TestTrailerRemote;
import org.openelis.server.constants.Constants;
import org.openelis.util.FormUtil;
import org.openelis.util.SessionManager;
import org.openelis.util.UTFResource;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class TestTrailerService implements AppScreenFormServiceInt<TestTrailerForm,Query<TableDataRow<Integer>>> {

	private static final int leftTableRowsPerPage = 9;
	
    private UTFResource openElisConstants= UTFResource.getBundle((String)SessionManager.getSession().getAttribute("locale"));
	
    private static final TestTrailerMetaMap TestTrailerMeta = new TestTrailerMetaMap();
    
	public Query<TableDataRow<Integer>> commitQuery(Query<TableDataRow<Integer>> query) throws RPCException {
        List testTrailers = new ArrayList();
    //		if the rpc is null then we need to get the page
        /*
    		if(qList == null){
                qList = (ArrayList<AbstractField>)SessionManager.getSession().getAttribute("TestTrailerQuery");
    
    	        if(qList == null)
    	        	throw new QueryException(openElisConstants.getString("queryExpiredException"));

    	        TestTrailerRemote remote = (TestTrailerRemote)EJBFactory.lookup("openelis/TestTrailerBean/remote");
    	        try{
    	        	testTrailers = remote.query(qList, (model.getPage()*leftTableRowsPerPage), leftTableRowsPerPage+1);
    	        }catch(Exception e){
    	        	if(e instanceof LastPageException){
    	        		throw new LastPageException(openElisConstants.getString("lastPageException"));
    	        	}else{
    	        		throw new RPCException(e.getMessage());	
    	        	}
    	        }
            }else{ */
    			TestTrailerRemote remote = (TestTrailerRemote)EJBFactory.lookup("openelis/TestTrailerBean/remote");
    			
    			
    			try{
                    testTrailers = remote.query(query.fields,query.page*leftTableRowsPerPage,leftTableRowsPerPage);
                }catch(LastPageException e) {
                    throw new LastPageException(openElisConstants.getString("lastPageException"));
        		}catch(Exception e){
        			throw new RPCException(e.getMessage());
        		}
                
                //need to save the rpc used to the encache
               // SessionManager.getSession().setAttribute("TestTrailerQuery", qList);
    		//}
    		
            int i=0;
            if(query.results == null)
                query.results = new TableDataModel<TableDataRow<Integer>>();
            else
                query.results.clear();
            while(i < testTrailers.size() && i < leftTableRowsPerPage) {
                IdNameDO resultDO = (IdNameDO)testTrailers.get(i);
                query.results.add(new TableDataRow<Integer>(resultDO.getId(),new StringObject(resultDO.getName())));
                i++;
             } 
            
    		return query;
    	}

    public TestTrailerForm commitAdd(TestTrailerForm rpc) throws RPCException {
		//remote interface to call the testTrailer bean
		TestTrailerRemote remote = (TestTrailerRemote)EJBFactory.lookup("openelis/TestTrailerBean/remote");
		TestTrailerDO newTestTrailerDO = new TestTrailerDO();
		
		//build the testTrailer DO from the form
		newTestTrailerDO = getTestTrailerDOFromRPC(rpc);
				
		
		//send the changes to the database
		Integer testTrailerId;
		try{
			testTrailerId = (Integer)remote.updateTestTrailer(newTestTrailerDO);
		}catch(Exception e){
            if(e instanceof ValidationErrorsList){
                setRpcErrors(((ValidationErrorsList)e).getErrorList(), rpc);
                return rpc;
            }else
                throw new RPCException(e.getMessage());
        }
		
        newTestTrailerDO.setId(testTrailerId);

		//set the fields in the RPC
		setFieldsInRPC(rpc, newTestTrailerDO);
	
		return rpc;
	}

	public TestTrailerForm commitUpdate(TestTrailerForm rpc) throws RPCException {
    	//remote interface to call the test trailer bean
    	TestTrailerRemote remote = (TestTrailerRemote)EJBFactory.lookup("openelis/TestTrailerBean/remote");
    	TestTrailerDO newTestTrailerDO = new TestTrailerDO();
    	
    	//build the DO from the form
    	newTestTrailerDO = getTestTrailerDOFromRPC(rpc);
    	
    	//send the changes to the database
    	try{
    		remote.updateTestTrailer(newTestTrailerDO);
    		
    	}catch(Exception e){
            if(e instanceof ValidationErrorsList){
                setRpcErrors(((ValidationErrorsList)e).getErrorList(), rpc);
                return rpc;
            }else
                throw new RPCException(e.getMessage());
        }
    
    	//set the fields in the RPC
    	setFieldsInRPC(rpc, newTestTrailerDO);
        
    	return rpc;
    }

    public TestTrailerForm commitDelete(TestTrailerForm rpc) throws RPCException {
		//remote interface to call the test trailer bean
		TestTrailerRemote remote = (TestTrailerRemote)EJBFactory.lookup("openelis/TestTrailerBean/remote");

		try {
			remote.deleteTestTrailer(rpc.entityKey);
			
		}catch(Exception e){
            if(e instanceof ValidationErrorsList){
                setRpcErrors(((ValidationErrorsList)e).getErrorList(), rpc);
                return rpc;
            }else
                throw new RPCException(e.getMessage());
        }
		
		setFieldsInRPC(rpc, new TestTrailerDO());
		
		return rpc;
	}

	public TestTrailerForm abort(TestTrailerForm rpc) throws RPCException {
    //		remote interface to call the testTrailer bean
    		TestTrailerRemote remote = (TestTrailerRemote)EJBFactory.lookup("openelis/TestTrailerBean/remote");
    		
    		
    		TestTrailerDO testTrailerDO = remote.getTestTrailerAndUnlock(rpc.entityKey, SessionManager.getSession().getId());
    
    //		set the fields in the RPC
    		setFieldsInRPC(rpc, testTrailerDO);
            
    		return rpc;  
    	}

    public TestTrailerForm fetch(TestTrailerForm rpc) throws RPCException {
//		remote interface to call the test trailer bean
		TestTrailerRemote remote = (TestTrailerRemote)EJBFactory.lookup("openelis/TestTrailerBean/remote");
		
		TestTrailerDO testTrailerDO = remote.getTestTrailer(rpc.entityKey);
		
//		set the fields in the RPC
		setFieldsInRPC(rpc, testTrailerDO);

		return rpc;
	}

	public TestTrailerForm fetchForUpdate(TestTrailerForm rpc) throws RPCException {
//		remote interface to call the test trailer bean
		TestTrailerRemote remote = (TestTrailerRemote)EJBFactory.lookup("openelis/TestTrailerBean/remote");
		TestTrailerDO testTrailerDO = new TestTrailerDO();
		
		try{
			testTrailerDO = remote.getTestTrailerAndLock(rpc.entityKey, SessionManager.getSession().getId());
		}catch(Exception e){
			throw new RPCException(e.getMessage());
		}
		
//		set the fields in the RPC
		setFieldsInRPC(rpc, testTrailerDO);

		return rpc;
	}

    public TestTrailerForm getScreen(TestTrailerForm rpc) throws RPCException {
        rpc.xml = ServiceUtils.getXML(Constants.APP_ROOT+"/Forms/testTrailer.xsl");

        return rpc;
    }
    
    private void setFieldsInRPC(TestTrailerForm form, TestTrailerDO testTrailerDO){
        form.id.setValue(testTrailerDO.getId());
        form.name.setValue(testTrailerDO.getName());
        form.description.setValue(testTrailerDO.getDescription());
        form.text.setValue(testTrailerDO.getText());		
	}
	
	private TestTrailerDO getTestTrailerDOFromRPC(TestTrailerForm form){
		TestTrailerDO newTestTrailerDO = new TestTrailerDO();
		
		newTestTrailerDO.setId(form.id.getValue());
		newTestTrailerDO.setName(form.name.getValue());
		newTestTrailerDO.setDescription(form.description.getValue());
		newTestTrailerDO.setText(form.text.getValue());
		
		return newTestTrailerDO;
	}
	
	private void setRpcErrors(List exceptionList, TestTrailerForm form){
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

}
