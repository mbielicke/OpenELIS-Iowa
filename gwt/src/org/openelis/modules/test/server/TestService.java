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

import org.openelis.common.AutocompleteRPC;
import org.openelis.domain.TestIdNameMethodNameDO;
import org.openelis.gwt.common.LastPageException;
import org.openelis.gwt.common.RPC;
import org.openelis.gwt.common.data.Query;
import org.openelis.gwt.server.ServiceUtils;
import org.openelis.manager.TestAnalyteManager;
import org.openelis.manager.TestManager;
import org.openelis.manager.TestPrepManager;
import org.openelis.manager.TestReflexManager;
import org.openelis.manager.TestResultManager;
import org.openelis.manager.TestTypeOfSampleManager;
import org.openelis.manager.TestWorksheetManager;
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
    
    public Query<TestIdNameMethodNameDO> query(Query<TestIdNameMethodNameDO> query) throws Exception {

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
            throw new Exception(e.getMessage());
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
    
    public TestPrepManager fetchPrepTestsByTestId(Integer testId) throws Exception{
        TestManagerRemote remote = (TestManagerRemote)EJBFactory.lookup("openelis/TestManagerBean/remote");
        return remote.fetchPrepTestsByTestId(testId);
    }
    
    public TestReflexManager fetchReflexiveTestsByTestId(Integer testId) throws Exception{
        TestManagerRemote remote = (TestManagerRemote)EJBFactory.lookup("openelis/TestManagerBean/remote");
        return remote.fetchReflexiveTestsByTestId(testId);
    }
    
    public TestWorksheetManager fetchWorksheetByTestId(Integer testId) throws Exception{
        TestManagerRemote remote = (TestManagerRemote)EJBFactory.lookup("openelis/TestManagerBean/remote");
        return remote.fetchWorksheetByTestId(testId);
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
    
    public TestManager fetchWithPrepTestsAndReflexTests(Integer testId) throws Exception {
        TestManagerRemote remote = (TestManagerRemote)EJBFactory.lookup("openelis/TestManagerBean/remote");
        TestManager man = remote.fetchWithPrepTestsAndReflexTests(testId);
        
        return man;
    }
    
    public TestManager fetchWithWorksheet(Integer testId) throws Exception {
        TestManagerRemote remote = (TestManagerRemote)EJBFactory.lookup("openelis/TestManagerBean/remote");
        TestManager man = remote.fetchWithWorksheet(testId);
        
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
    
    public String getScreen() throws Exception {
        return ServiceUtils.getXML(Constants.APP_ROOT+"/Forms/test.xsl");      
    }     
    
    public AutocompleteRPC getAnalyteMatches(AutocompleteRPC rpc) {
        ArrayList<RPC> entries;
        AnalyteRemote aremote;
        
        aremote = (AnalyteRemote)EJBFactory.lookup("openelis/AnalyteBean/remote");
        entries = (ArrayList<RPC>)aremote.autoCompleteLookupByName(rpc.match.trim() + "%", 10);
        rpc.model = (ArrayList<RPC>)entries;        

        return rpc;
    } 
    
    public AutocompleteRPC getMethodMatches(AutocompleteRPC rpc) {
        ArrayList<RPC> entries;       
        MethodRemote mremote;        
        
        mremote = (MethodRemote)EJBFactory.lookup("openelis/MethodBean/remote");
        entries = (ArrayList<RPC>)mremote.autoCompleteLookupByName(rpc.match.trim() + "%", 10);
        rpc.model = (ArrayList<RPC>)entries;        
        
        return rpc;
    }
    
    public AutocompleteRPC getQCNameMatches(AutocompleteRPC rpc) {
        List entries;       
        QcRemote qremote;
       
        qremote = (QcRemote)EJBFactory.lookup("openelis/QcBean/remote");
        entries = qremote.qcAutocompleteByName(rpc.match.trim() + "%", 10);
        rpc.model = (ArrayList<RPC>)entries;       
 
        return rpc;
    }  
    
    public AutocompleteRPC getTestMethodMatches(AutocompleteRPC rpc) {
        ArrayList<RPC> entries;
        TestRemote tremote;

        tremote = (TestRemote)EJBFactory.lookup("openelis/TestBean/remote");
        entries = (ArrayList<RPC>)tremote.getTestAutoCompleteByName(rpc.match.trim() + "%", 10);
        rpc.model = (ArrayList<RPC>)entries;               

        return rpc;
    } 
    
    public AutocompleteRPC getScriptletMatches(AutocompleteRPC rpc) {
        ArrayList<RPC> entries;
        ScriptletRemote sremote;
        
        sremote = (ScriptletRemote)EJBFactory.lookup("openelis/ScriptletBean/remote");
        entries = (ArrayList<RPC>)sremote.getScriptletAutoCompleteByName(rpc.match.trim() + "%",
                                                         10);
        rpc.model = (ArrayList<RPC>)entries;        

        return rpc;
    }
    
    public AutocompleteRPC getTrailerMatches(AutocompleteRPC rpc) {
        ArrayList<RPC> entries;
        TestTrailerRemote ttremote;

        ttremote = (TestTrailerRemote)EJBFactory.lookup("openelis/TestTrailerBean/remote");
        entries = (ArrayList<RPC>)ttremote.getTestTrailerAutoCompleteByName(rpc.match.trim()    + "%",
                                                                10);
        rpc.model = (ArrayList<RPC>)entries;
        return rpc;
    }
    
    public AutocompleteRPC getLabelMatches(AutocompleteRPC rpc) {
        ArrayList<RPC> entries;        
        LabelRemote lremote;

        lremote = (LabelRemote)EJBFactory.lookup("openelis/LabelBean/remote");
        entries = (ArrayList<RPC>)lremote.getLabelAutoCompleteByName(rpc.match.trim() + "%", 10);
        rpc.model = (ArrayList<RPC>)entries;        

        return rpc;
    }
    
    
}
