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

import org.openelis.domain.TestPrepViewDO;
import org.openelis.local.TestLocal;

public class TestPrepManagerProxy {

    public TestPrepManager add(TestPrepManager man) throws Exception {        
        TestLocal tl;
        TestPrepViewDO prepTest;
        
        tl = getTestLocal();         
        
        for(int i=0; i<man.count(); i++){
            prepTest = man.getPrepAt(i);
            prepTest.setTestId(man.getTestId());
            
            tl.addPrepTest(prepTest);
        }
        
        return man;
    }
    
    public TestPrepManager update(TestPrepManager man) throws Exception {        
        TestLocal tl;
        TestPrepViewDO prepTest;
        
        tl = getTestLocal(); 
        
        for(int i = 0; i < man.deleteCount(); i++) {
            tl.deletePrepTest(man.getDeletedAt(i));
        }
        
        for(int i=0; i<man.count(); i++){
            prepTest = man.getPrepAt(i);
            
            if(prepTest.getId() == null){
                prepTest.setTestId(man.getTestId());
                tl.addPrepTest(prepTest);
            }else
                tl.updatePrepTest(prepTest);
        }
        
        return man;
    }
    
    public TestPrepManager fetchByTestId(Integer testId) throws Exception {
        TestPrepManager tpm;
        TestLocal tl;
        ArrayList<TestPrepViewDO> prepTests;
        
        tl = getTestLocal();
        prepTests = tl.fetchPrepTestsById(testId);
        tpm = TestPrepManager.getInstance();
        tpm.setPreps(prepTests);
        return tpm;
        
    }
    
    private TestLocal getTestLocal(){
        try{
            InitialContext ctx = new InitialContext();
            return (TestLocal)ctx.lookup("openelis/TestBean/local");
        }catch(Exception e){
             System.out.println(e.getMessage());
             return null;
        }
    }
}
