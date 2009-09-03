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

import java.util.ArrayList;

import javax.naming.InitialContext;

import org.openelis.domain.TestDO;
import org.openelis.domain.TestSectionDO;
import org.openelis.local.TestLocal;

public class TestManagerProxy {

    public TestManager add(TestManager man) throws Exception {
        TestLocal tl;
        TestSectionManager tsm;
        TestTypeOfSampleManager ttsm;
        TestAnalyteManager tam;
        TestResultManager trm;        
        Integer testId;
        
        tl = getTestLocal();
        tl.add(man.getTest());
        
        testId = man.getTest().getId();
                
        tsm = man.getTestSections();
        tsm.setTestId(testId);
        tsm.add();
       
        ttsm = man.getSampleTypes();
        ttsm.setTestId(testId);
        ttsm.add();
        
        tam = man.getTestAnalytes();
        tam.setTestId(testId);
        tam.add();
        
        trm = man.getTestResults();
        trm.setTestId(testId);
        trm.add();
        
        return man;
    }
    
    public TestManager update(TestManager man) throws Exception {
        TestLocal tl;
        TestSectionManager tsm;
        TestTypeOfSampleManager ttsm;
        TestAnalyteManager tam;
        TestResultManager trm;
        Integer testId;
        
        tl = getTestLocal();
        tl.update(man.getTest());
        
        testId = man.getTest().getId();
        
        tsm = man.getTestSections();
        tsm.setTestId(testId);
        tsm.update();
       
        ttsm = man.getSampleTypes();
        ttsm.setTestId(testId);
        ttsm.update();
        
        tam = man.getTestAnalytes();
        tam.setTestId(testId);
        tam.update();
        
        trm = man.getTestResults();
        trm.setTestId(testId);
        trm.update();
        
        return man;
    }
    
    public TestManager fetch(Integer testId) throws Exception {
        TestLocal tl;
        TestDO testDO;
        TestManager man;
        TestSectionManager tsm;
        ArrayList<TestSectionDO> sections;
        
        tl = getTestLocal();
        testDO = tl.fetchById(testId);
        man = TestManager.getInstance();
        man.setTest(testDO);
        
        sections = (ArrayList<TestSectionDO>)tl.getTestSections(testId);       
        
        tsm = man.getTestSections();
        tsm.testSections = sections;
        
        return man;
    }
    
    public TestManager fetchWithSampleTypes(Integer testId) throws Exception {
        TestManager man;
        
        man = fetch(testId);
        man.getSampleTypes();
        
        return man;
    }
    
    public TestManager fetchWithAnalytesAndResults(Integer testId) throws Exception {
        TestManager man;
        
        man = fetch(testId);
        man.getTestAnalytes();
        man.getTestResults();
        
        return man;
    }
    
    public TestManager fetchForUpdate(Integer testId) throws Exception {
        //do nothing
        return null;
    }
    
    public TestManager abort(Integer testId) throws Exception {
        //do nothing
        return null;
    }
    
    public void validate(TestManager man) throws Exception {
        TestLocal tl;
        
        tl = getTestLocal();
        tl.validateTest(man.getTest(), man.getTestSections().getTestSections(),
                        man.getSampleTypes().getSampleTypes());
    }
    
    private TestLocal getTestLocal() {
        try{
            InitialContext ctx = new InitialContext();
            return (TestLocal)ctx.lookup("openelis/TestBean/local");
        }catch(Exception e){
             System.out.println(e.getMessage());
             return null;
        }        
    }
}
