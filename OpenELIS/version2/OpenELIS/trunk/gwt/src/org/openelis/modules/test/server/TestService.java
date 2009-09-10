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
package org.openelis.modules.test.server;

import java.util.ArrayList;
import java.util.List;

import org.openelis.domain.IdNameDO;
import org.openelis.domain.TestIdNameMethodNameDO;
import org.openelis.domain.TestMethodAutoDO;
import org.openelis.gwt.common.LastPageException;
import org.openelis.gwt.common.RPCException;
import org.openelis.gwt.common.rewrite.Query;
import org.openelis.gwt.server.ServiceUtils;
import org.openelis.manager.TestAnalyteManager;
import org.openelis.manager.TestManager;
import org.openelis.manager.TestResultManager;
import org.openelis.manager.TestTypeOfSampleManager;
import org.openelis.modules.test.client.TestAutoRPC;
import org.openelis.persistence.EJBFactory;
import org.openelis.remote.AnalyteRemote;
import org.openelis.remote.LabelRemote;
import org.openelis.remote.MethodRemote;
import org.openelis.remote.QcRemote;
import org.openelis.remote.ScriptletRemote;
import org.openelis.remote.TestManagerRemote;
import org.openelis.remote.TestRemote;
import org.openelis.remote.TestTrailerRemote;
import org.openelis.server.constants.Constants;
import org.openelis.util.SessionManager;
import org.openelis.util.UTFResource;

public class TestService {
    
    private static final int leftTableRowsPerPage = 27; 
    
    private UTFResource openElisConstants= UTFResource.getBundle((String)SessionManager.getSession().getAttribute("locale"));
    
    public Query<TestIdNameMethodNameDO> query(Query<TestIdNameMethodNameDO> query) throws RPCException {

        TestRemote remote = (TestRemote)EJBFactory.lookup("openelis/TestBean/remote");

        try{    
            query.results = new ArrayList<TestIdNameMethodNameDO>();
            ArrayList<TestIdNameMethodNameDO> results = (ArrayList<TestIdNameMethodNameDO>)remote.query(query.fields,query.page*leftTableRowsPerPage,leftTableRowsPerPage);
            for(TestIdNameMethodNameDO result : results) {
                query.results.add(result);
            }
        }catch(LastPageException e) {
            throw new LastPageException(openElisConstants.getString("lastPageException"));
        }catch(Exception e){
            e.printStackTrace();
            throw new RPCException(e.getMessage());
        }
        return query;
    }

    public TestManager fetch(Integer testId) throws Exception {
        TestManagerRemote remote = (TestManagerRemote)EJBFactory.lookup("openelis/TestManagerBean/remote");
        
        return remote.fetch(testId);
    }
    
    
    public TestTypeOfSampleManager fetchSampleTypeByTestId(Integer testId) throws Exception{
        TestManagerRemote remote = (TestManagerRemote)EJBFactory.lookup("openelis/TestManagerBean/remote");
        return remote.fetchSampleTypeByTestId(testId);
    }
    
    public TestAnalyteManager fetchTestAnalytesByTestId(Integer testId) throws Exception{       
        TestManagerRemote remote = (TestManagerRemote)EJBFactory.lookup("openelis/TestManagerBean/remote");
        return remote.fetchTestAnalytesByTestId(testId);
    }
    
    public TestResultManager fetchTestResultsByTestId(Integer testId) throws Exception{       
        TestManagerRemote remote = (TestManagerRemote)EJBFactory.lookup("openelis/TestManagerBean/remote");
        return remote.fetchTestResultsByTestId(testId);
    }
    
    public TestManager add(TestManager man) throws Exception {
        TestManagerRemote remote = (TestManagerRemote)EJBFactory.lookup("openelis/TestManagerBean/remote");

        return remote.add(man);
    }
    
    public TestManager update(TestManager man) throws Exception {
        TestManagerRemote remote = (TestManagerRemote)EJBFactory.lookup("openelis/TestManagerBean/remote");
        
        return remote.update(man);
        
    }
    
    public TestManager fetchWithSampleTypes(Integer testId) throws Exception {
        TestManagerRemote remote = (TestManagerRemote)EJBFactory.lookup("openelis/TestManagerBean/remote");
        TestManager man = remote.fetchWithSampleTypes(testId);
        
        return man;
    }
    
    public TestManager fetchWithAnalytesAndResults(Integer testId) throws Exception {
        TestManagerRemote remote = (TestManagerRemote)EJBFactory.lookup("openelis/TestManagerBean/remote");
        TestManager man = remote.fetchWithAnalytesAndResults(testId);
        
        return man;
    }
    
    public TestManager fetchForUpdate(Integer testId) throws Exception {
        //remote interface to call TestBean
        TestManagerRemote remote = (TestManagerRemote)EJBFactory.lookup("openelis/TestManagerBean/remote");
        
        TestManager man = remote.fetchForUpdate(testId);
        
        return man;
    }
    
    public TestManager abort(Integer orgId) throws Exception {
        //remote interface to call the TestBean
        TestManagerRemote remote = (TestManagerRemote)EJBFactory.lookup("openelis/TestManagerBean/remote");
        
        TestManager man = remote.abortUpdate(orgId);
        
        return man;
    } 
    
    public String getScreen() throws RPCException {
        return ServiceUtils.getXML(Constants.APP_ROOT+"/Forms/test.xsl");      
    }     
    
    public TestAutoRPC getAnalyteMatches(TestAutoRPC rpc) {
        List<IdNameDO> entries;
        AnalyteRemote aremote;
        
        aremote = (AnalyteRemote)EJBFactory.lookup("openelis/AnalyteBean/remote");
        entries = aremote.autoCompleteLookupByName(rpc.match.trim() + "%", 10);
        rpc.idNameList = (ArrayList<IdNameDO>)entries;        

        return rpc;
    } 
    
    public TestAutoRPC getMethodMatches(TestAutoRPC rpc) {
        List<IdNameDO> entries;       
        MethodRemote mremote;        
        
        mremote = (MethodRemote)EJBFactory.lookup("openelis/MethodBean/remote");
        entries = mremote.autoCompleteLookupByName(rpc.match.trim() + "%", 10);
        rpc.idNameList = (ArrayList<IdNameDO>)entries;
        
        return rpc;
    }
    
    public TestAutoRPC getQCNameMatches(TestAutoRPC rpc) {
        List<IdNameDO> entries;        
        QcRemote qremote;
       
        qremote = (QcRemote)EJBFactory.lookup("openelis/QcBean/remote");
        entries = qremote.qcAutocompleteByName(rpc.match.trim() + "%", 10);
        rpc.idNameList = (ArrayList<IdNameDO>)entries;
 
        return rpc;
    }  
    
    public TestAutoRPC getTestMethodMatches(TestAutoRPC rpc) {
        List<TestMethodAutoDO> tmlist;
        TestRemote tremote;

        tremote = (TestRemote)EJBFactory.lookup("openelis/TestBean/remote");
        tmlist = tremote.getTestAutoCompleteByName(rpc.match.trim() + "%", 10);
        rpc.testMethodList = (ArrayList<TestMethodAutoDO>)tmlist;        

        return rpc;
    } 
    
    public TestAutoRPC getScriptletMatches(TestAutoRPC rpc) {
        List<IdNameDO> entries;
        ScriptletRemote sremote;
        
        sremote = (ScriptletRemote)EJBFactory.lookup("openelis/ScriptletBean/remote");
        entries = sremote.getScriptletAutoCompleteByName(rpc.match.trim() + "%",
                                                         10);
        rpc.idNameList = (ArrayList<IdNameDO>)entries;        

        return rpc;
    }
    
    public TestAutoRPC getTrailerMatches(TestAutoRPC rpc) {
        List<IdNameDO> entries;
        TestTrailerRemote ttremote;

        ttremote = (TestTrailerRemote)EJBFactory.lookup("openelis/TestTrailerBean/remote");
        entries = ttremote.getTestTrailerAutoCompleteByName(rpc.match.trim()    + "%",
                                                                10);
        rpc.idNameList = (ArrayList<IdNameDO>)entries;
        return rpc;
    }
    
    public TestAutoRPC getLabelMatches(TestAutoRPC rpc) {
        List<IdNameDO> entries;        
        LabelRemote lremote;

        lremote = (LabelRemote)EJBFactory.lookup("openelis/LabelBean/remote");
        entries = lremote.getLabelAutoCompleteByName(rpc.match.trim() + "%", 10);
        rpc.idNameList = (ArrayList<IdNameDO>)entries;        

        return rpc;
    }
    
    
}
