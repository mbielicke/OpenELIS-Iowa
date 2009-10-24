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
import org.openelis.domain.TestMethodVO;
import org.openelis.gwt.common.DatabaseException;
import org.openelis.gwt.common.data.Query;
import org.openelis.manager.TestAnalyteManager;
import org.openelis.manager.TestManager;
import org.openelis.manager.TestPrepManager;
import org.openelis.manager.TestReflexManager;
import org.openelis.manager.TestResultManager;
import org.openelis.manager.TestTypeOfSampleManager;
import org.openelis.manager.TestWorksheetManager;
import org.openelis.modules.test.client.TestResultCategoryRPC;
import org.openelis.persistence.EJBFactory;
import org.openelis.remote.AnalyteRemote;
import org.openelis.remote.CategoryRemote;
import org.openelis.remote.LabelRemote;
import org.openelis.remote.MethodRemote;
import org.openelis.remote.QcRemote;
import org.openelis.remote.ScriptletRemote;
import org.openelis.remote.TestManagerRemote;
import org.openelis.remote.TestRemote;
import org.openelis.remote.TestTrailerRemote;
import org.openelis.utilcommon.DataBaseUtil;

public class TestService {
    
    private static final int rowPP = 23;         
    
    public ArrayList<TestMethodVO> query(Query query) throws Exception {
        try {
            return testRemote().query(query.getFields(), query.getPage() * rowPP, rowPP);
        } catch (RuntimeException e) {
            throw new DatabaseException(e);
        }
    }

    public TestManager fetchById(Integer testId) throws Exception {
        try {
            return managerRemote().fetchById(testId);
        } catch (RuntimeException e) {
            throw new DatabaseException(e);
        }
    }
        
    public TestTypeOfSampleManager fetchSampleTypeByTestId(Integer testId) throws Exception{
        try {
            return managerRemote().fetchSampleTypeByTestId(testId);
        } catch (RuntimeException e) {
            throw new DatabaseException(e);
        }
    }
    
    public TestAnalyteManager fetchTestAnalyteByTestId(Integer testId) throws Exception{       
        try {
            return managerRemote().fetchTestAnalytesByTestId(testId);
        } catch (RuntimeException e) {
            throw new DatabaseException(e);
        }
    }
    
    public TestResultManager fetchTestResultByTestId(Integer testId) throws Exception{       
        try {
            return managerRemote().fetchTestResultsByTestId(testId);
        } catch (RuntimeException e) {
            throw new DatabaseException(e);
        }
    }
    
    public TestPrepManager fetchPrepTestsByTestId(Integer testId) throws Exception{
        try {
            return managerRemote().fetchPrepTestsByTestId(testId);
        } catch (RuntimeException e) {
            throw new DatabaseException(e);
        }
    }
    
    public TestReflexManager fetchReflexiveTestByTestId(Integer testId) throws Exception{
        try {
            return managerRemote().fetchReflexiveTestsByTestId(testId);
        } catch (RuntimeException e) {
            throw new DatabaseException(e);
        }
    }
    
    public TestWorksheetManager fetchWorksheetByTestId(Integer testId) throws Exception{
        try {
            return managerRemote().fetchWorksheetByTestId(testId);
        } catch (RuntimeException e) {
            throw new DatabaseException(e);
        }
    }
    
    public TestManager fetchWithSampleTypes(Integer testId) throws Exception {       
        try {
            return managerRemote().fetchWithSampleTypes(testId);
        } catch (RuntimeException e) {
            throw new DatabaseException(e);
        }
    }
    
    public TestManager fetchWithAnalytesAndResults(Integer testId) throws Exception {     
        try {
            return managerRemote().fetchWithAnalytesAndResults(testId);
        } catch (RuntimeException e) {
            throw new DatabaseException(e);
        }
        
    }
    
    public TestManager fetchWithPrepTests(Integer testId) throws Exception {
        try {
            return managerRemote().fetchWithPrepTests(testId);
        } catch (RuntimeException e) {
            throw new DatabaseException(e);
        }
    }
    
    public TestManager fetchWithPrepTestsAndReflexTests(Integer testId) throws Exception {        
        try {
            return managerRemote().fetchWithPrepTestsAndReflexTests(testId);
        } catch (RuntimeException e) {
            throw new DatabaseException(e);
        }
    }
    
    public TestManager fetchWithWorksheet(Integer testId) throws Exception {        
        try {
            return managerRemote().fetchWithWorksheet(testId);
        } catch (RuntimeException e) {
            throw new DatabaseException(e);
        }
    } 
    
    public TestManager add(TestManager man) throws Exception {    
        try {
            return managerRemote().add(man);      
        } catch (RuntimeException e) {
            e.printStackTrace();
            throw new DatabaseException(e);
        }
    }
    
    public TestManager update(TestManager man) throws Exception {  
        try {
            return managerRemote().update(man);
        } catch (RuntimeException e) {
            throw new DatabaseException(e);
        }
    }
    
    public TestManager fetchForUpdate(Integer testId) throws Exception {   
        try {
            return managerRemote().fetchForUpdate(testId);
        } catch (RuntimeException e) {
            throw new DatabaseException(e);
        }
    }
    
    public TestManager abortUpdate(Integer testId) throws Exception {        
        try {
            return managerRemote().abortUpdate(testId);
        } catch (RuntimeException e) {
            throw new DatabaseException(e);
        }
    } 
    
    public List<IdNameDO> getAnalyteMatches(Query query) {
        String value;
        
        value = query.getFields().get(0).query;                
        return analyteRemote().autoCompleteLookupByName(value+ "%", 10);
    } 
    
    public List<IdNameDO> getMethodMatches(Query query) {
        String value;
        
        value = query.getFields().get(0).query;
        return methodRemote().autoCompleteLookupByName(value+ "%", 10);               
    }
    
    public List<IdNameDO> getQCNameMatches(Query query) {       
        String value;
        
        value = query.getFields().get(0).query;        
        return qcRemote().qcAutocompleteByName(value + "%", 10);
    }  
    
    public List<TestMethodVO> findByName(Query query) {       
        String value;
        
        value = query.getFields().get(0).query;          
        return testRemote().fetchByName(value + "%", 10);
    } 
    
    public List<IdNameDO> getScriptletMatches(Query query) {
        //String value;
        
        //value = query.getFields().get(0).query;            
        //return scriptletRemote().getScriptletAutoCompleteByName(value + "%",10);                
        return null;
    }
    
    public List<IdNameDO> getTrailerMatches(Query query) {
        String value;
        
        value = query.getFields().get(0).query;
        return trailerRemote().getTestTrailerAutoCompleteByName(value+ "%",10);        
    }
    
    public List<IdNameDO> getLabelMatches(Query query) {
        String value;
        
        value = query.getFields().get(0).query;
        return labelRemote().getLabelAutoCompleteByName(value+ "%",10);               
    }   
    
    public TestResultCategoryRPC getDictIdForResultValue(TestResultCategoryRPC rpc) {
        rpc.dictIdList = categoryRemote().getDictionaryListByEntry(DataBaseUtil.trim(rpc.resultValue));
        
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
    
    private CategoryRemote categoryRemote() {
        return (CategoryRemote)EJBFactory.lookup("openelis/CategoryBean/remote");
    }
    
}
