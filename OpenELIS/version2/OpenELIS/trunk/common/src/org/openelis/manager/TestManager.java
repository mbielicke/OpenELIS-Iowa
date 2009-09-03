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
package org.openelis.manager;

import org.openelis.domain.TestDO;
import org.openelis.exception.InconsistencyException;
import org.openelis.exception.NotFoundException;
import org.openelis.gwt.common.RPC;

public class TestManager implements RPC {

    private static final long serialVersionUID = 1L;
    protected TestDO test;
    protected TestSectionManager testSections;
    protected TestTypeOfSampleManager sampleTypes;
    protected TestAnalyteManager testAnalytes;
    protected TestResultManager testResults;
        
    protected transient static TestManagerProxy proxy;

    /**
     * This is a protected constructor
     */
    protected TestManager() {
        test = null;
        testSections = null;
        sampleTypes = null;
        testAnalytes = null;
        testResults = null;
    }
    
    /**
     * Creates a new instance of this object. A default TestDO object is also created.
     */
    public static TestManager getInstance() {
        TestManager tm;
        
        tm = new TestManager();
        tm.test = new TestDO();
        tm.testSections = new TestSectionManager();        
        return tm;
    }
    
    public static TestManager findById(Integer id) throws Exception {        
        return proxy().fetch(id);
    }
    
    public static TestManager findByIdWithSampleTypes(Integer id) throws Exception{
        return proxy().fetchWithSampleTypes(id);
    }
    
    public static TestManager findByIdWithAnalytesAndResults(Integer id) throws Exception{
        return proxy().fetchWithAnalytesAndResults(id);
    }
    
    
    public TestManager fetchForUpdate() throws Exception {
        if(test.getId() == null)
            throw new InconsistencyException("test id is null");
        
        return proxy().fetchForUpdate(test.getId());
    }  
    
    public TestTypeOfSampleManager getSampleTypes() throws Exception {
        if(sampleTypes == null){
            if(test.getId() != null) {
                try {
                    sampleTypes = TestTypeOfSampleManager.findByTestId(test.getId());
                } catch(NotFoundException e){
                    //ignore
                }catch(Exception e){
                    e.printStackTrace();
                    throw e;
                }
            }
        }
        
        if(sampleTypes == null)
            sampleTypes = TestTypeOfSampleManager.getInstance();        
        
        return sampleTypes;
    }
    
    /*public TestAnalyteManager getAnalytesAndResults() throws Exception {
        if(testAnalytes == null || testResults == null){
            if(test.getId() != null) {
                try {
                    testAnalytes = TestAnalyteManager.findByTestId(test.getId());
                    testResults = TestResultManager.findByTestId(test.getId());
                } catch(NotFoundException e){
                    //ignore
                }catch(Exception e){
                    e.printStackTrace();
                    throw e;
                }
            }
        }
        
        if(testAnalytes == null)
            testAnalytes = TestAnalyteManager.getInstance(); 
        
        if(testResults == null)
            testResults = TestResultManager.getInstance();        
        
        return testAnalytes;
    }*/
    
    public TestAnalyteManager getTestAnalytes() throws Exception {
        if(testAnalytes == null){
            if(test.getId() != null) {
                try {
                    testAnalytes = TestAnalyteManager.findByTestId(test.getId());                    
                } catch(NotFoundException e){
                    //ignore
                }catch(Exception e){
                    e.printStackTrace();
                    throw e;
                }
            }
        }
        
        if(testAnalytes == null)
            testAnalytes = TestAnalyteManager.getInstance();                      
        
        return testAnalytes;
    }
    
    public TestResultManager getTestResults() throws Exception {
        if(testResults == null){
            if(test.getId() != null) {
                try {                    
                    testResults = TestResultManager.findByTestId(test.getId());
                } catch(NotFoundException e){
                    //ignore
                }catch(Exception e){
                    e.printStackTrace();
                    throw e;
                }
            }
        }                
        
        if(testResults == null)
            testResults = TestResultManager.getInstance();        
        
        return testResults;
    }
    
    public TestDO getTest() {
        return test;
    }

    public void setTest(TestDO test) {
        this.test = test;
    }
    
    public TestSectionManager getTestSections() {        
        return testSections;
    }
    
    //service methods
    public TestManager add() throws Exception {
        return proxy().add(this);
    }
    
    public TestManager update() throws Exception {
        return proxy().update(this);
    }
    
    public TestManager abort() throws Exception {
        return proxy.abort(test.getId());                
    }
    
    public void validate() throws Exception {
        proxy().validate(this);
    }
    
    private static TestManagerProxy proxy() {
        if(proxy == null)
            proxy = new TestManagerProxy();
        
        return proxy;
    }


}
