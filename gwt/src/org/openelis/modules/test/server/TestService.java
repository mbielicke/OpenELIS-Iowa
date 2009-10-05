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
import org.openelis.domain.TestMethodViewDO;
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

public class TestService {
    
    private static final int rowPP = 23;         
    
    public ArrayList<TestMethodViewDO> query(Query query) throws Exception {
        return testRemote().query(query.getFields(), query.getPage() * rowPP, rowPP);
    }

    public TestManager fetch(Integer testId) throws Exception {
        return managerRemote().fetch(testId);
    }
        
    public TestTypeOfSampleManager fetchSampleTypeByTestId(Integer testId) throws Exception{
        return managerRemote().fetchSampleTypeByTestId(testId);
    }
    
    public TestAnalyteManager fetchTestAnalytesByTestId(Integer testId) throws Exception{       
        return managerRemote().fetchTestAnalytesByTestId(testId);
    }
    
    public TestResultManager fetchTestResultsByTestId(Integer testId) throws Exception{       
        return managerRemote().fetchTestResultsByTestId(testId);
    }
    
    public TestPrepManager fetchPrepTestsByTestId(Integer testId) throws Exception{
        return managerRemote().fetchPrepTestsByTestId(testId);
    }
    
    public TestReflexManager fetchReflexiveTestsByTestId(Integer testId) throws Exception{
        return managerRemote().fetchReflexiveTestsByTestId(testId);
    }
    
    public TestWorksheetManager fetchWorksheetByTestId(Integer testId) throws Exception{
        return managerRemote().fetchWorksheetByTestId(testId);
    }
    
    public TestManager add(TestManager man) throws Exception {    
        return managerRemote().add(man);        
    }
    
    public TestManager update(TestManager man) throws Exception {               
        return managerRemote().update(man);                
    }
    
    public TestManager fetchWithSampleTypes(Integer testId) throws Exception {       
        return managerRemote().fetchWithSampleTypes(testId);
    }
    
    public TestManager fetchWithAnalytesAndResults(Integer testId) throws Exception {     
        return managerRemote().fetchWithAnalytesAndResults(testId);
    }
    
    public TestManager fetchWithPrepTests(Integer testId) throws Exception {
        return managerRemote().fetchWithPrepTests(testId);
    }
    
    public TestManager fetchWithPrepTestsAndReflexTests(Integer testId) throws Exception {        
        return managerRemote().fetchWithPrepTestsAndReflexTests(testId);
    }
    
    public TestManager fetchWithWorksheet(Integer testId) throws Exception {        
        return managerRemote().fetchWithWorksheet(testId);
    } 
    
    public TestManager fetchForUpdate(Integer testId) throws Exception {        
        return managerRemote().fetchForUpdate(testId);
    }
    
    public TestManager abort(Integer orgId) throws Exception {        
        return managerRemote().abortUpdate(orgId);
    } 
    
    public String getScreen() throws Exception {
        return ServiceUtils.getXML(Constants.APP_ROOT+"/Forms/test.xsl");      
    }     
    
    public AutocompleteRPC getAnalyteMatches(AutocompleteRPC rpc) {
        rpc.model = (ArrayList<RPC>)analyteRemote().autoCompleteLookupByName(rpc.match.trim() + "%", 10);        

        return rpc;
    } 
    
    public AutocompleteRPC getMethodMatches(AutocompleteRPC rpc) {
        rpc.model = (ArrayList<RPC>)methodRemote().autoCompleteLookupByName(rpc.match.trim() + "%", 10);        
        
        return rpc;
    }
    
    public AutocompleteRPC getQCNameMatches(AutocompleteRPC rpc) {
        List entries;       
       
        entries = qcRemote().qcAutocompleteByName(rpc.match.trim() + "%", 10);
        rpc.model = (ArrayList<RPC>)entries;       
 
        return rpc;
    }  
    
    public AutocompleteRPC getTestMethodMatches(AutocompleteRPC rpc) {
        rpc.model = (ArrayList<RPC>)testRemote().getTestAutoCompleteByName(rpc.match.trim() + "%", 10);               
        return rpc;
    } 
    
    public AutocompleteRPC getScriptletMatches(AutocompleteRPC rpc) {
        rpc.model = (ArrayList<RPC>)scriptletRemote().getScriptletAutoCompleteByName(rpc.match.trim() + "%",10);        
        return rpc;
    }
    
    public AutocompleteRPC getTrailerMatches(AutocompleteRPC rpc) {
        rpc.model = (ArrayList<RPC>)trailerRemote().getTestTrailerAutoCompleteByName(rpc.match.trim()+ "%",10);
        return rpc;
    }
    
    public AutocompleteRPC getLabelMatches(AutocompleteRPC rpc) {
        rpc.model = (ArrayList<RPC>)labelRemote().getLabelAutoCompleteByName(rpc.match.trim() + "%", 10);        

        return rpc;
    }
    
    private TestRemote testRemote() {
        return (TestRemote)EJBFactory.lookup("openelis/TestBean/remote");
    }
    
    private TestManagerRemote managerRemote() {
        return (TestManagerRemote)EJBFactory.lookup("openelis/TestManagerBean/remote");   
    }
    
    private AnalyteRemote analyteRemote() {
        return (AnalyteRemote)EJBFactory.lookup("openelis/AnalyteBean/remote");
    } 
    
    private MethodRemote methodRemote() {
        return (MethodRemote)EJBFactory.lookup("openelis/MethodBean/remote");
    } 
    
    private QcRemote qcRemote() {
        return (QcRemote)EJBFactory.lookup("openelis/QcBean/remote");
    }
    
    private ScriptletRemote scriptletRemote() {
        return (ScriptletRemote)EJBFactory.lookup("openelis/ScriptletBean/remote");
    }
    
    private TestTrailerRemote trailerRemote() {
        return (TestTrailerRemote)EJBFactory.lookup("openelis/TestTrailerBean/remote");
    }
    
    private LabelRemote labelRemote() {
        return (LabelRemote)EJBFactory.lookup("openelis/LabelBean/remote");
    }
    
}
