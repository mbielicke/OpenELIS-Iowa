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
import java.util.HashMap;

import javax.naming.InitialContext;

import org.openelis.domain.TestReflexViewDO;
import org.openelis.local.TestLocal;

public class TestReflexManagerProxy {
    
    public TestReflexManager add(TestReflexManager man,
                                 HashMap<Integer,Integer> analyteMap,
                                 HashMap<Integer,Integer> resultMap) throws Exception {
        TestLocal tl;
        TestReflexViewDO reflexTest;
        Integer anaId, resId;
        
        tl = getTestLocal();         
        
        for(int i=0; i < man.count(); i++) {
            reflexTest = man.getReflexAt(i);
            reflexTest.setTestId(man.getTestId());
            
            anaId = reflexTest.getTestAnalyteId();
            resId = reflexTest.getTestResultId();
            
            if(anaId < 0)
                reflexTest.setTestAnalyteId(analyteMap.get(anaId));
            
            if(resId < 0)
                reflexTest.setTestResultId(resultMap.get(resId));
            
            tl.addReflexTest(reflexTest);
        }
        
        return man;
    }
    
    public TestReflexManager update(TestReflexManager man,
                                    HashMap<Integer,Integer> analyteMap,
                                    HashMap<Integer,Integer> resultMap) throws Exception {
        TestLocal tl;
        TestReflexViewDO reflexTest;
        Integer anaId, resId;
        
        tl = getTestLocal(); 
                
        for(int i = 0; i < man.deleteCount(); i++) {
            System.out.println("delete%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%");
            tl.deleteReflexTest(man.getDeletedAt(i));
        }
        
        for(int i=0; i<man.count(); i++){
            reflexTest = man.getReflexAt(i);
            
            anaId = reflexTest.getTestAnalyteId();
            resId = reflexTest.getTestResultId();
            
            System.out.println("anaId "+ anaId+"%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%");
            System.out.println("resId "+ resId+"%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%");
            if(anaId < 0)
                reflexTest.setTestAnalyteId(analyteMap.get(anaId));
            
            if(resId < 0)
                reflexTest.setTestResultId(resultMap.get(resId));
            
            if(reflexTest.getId() == null){                
                reflexTest.setTestId(man.getTestId());
                System.out.println("add%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%");
                tl.addReflexTest(reflexTest);
            } else {
                System.out.println("update %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%");
                tl.updateReflexTest(reflexTest);
            }
        }
        
        return man;
    }
    
    public TestReflexManager fetchByTestId(Integer testId) throws Exception {
        TestReflexManager trm;
        TestLocal tl;
        ArrayList<TestReflexViewDO> reflexTests;    
        
        tl = getTestLocal();
        reflexTests = tl.fetchReflexTestsById(testId);
        trm = TestReflexManager.getInstance();
        trm.setReflexes(reflexTests);
        return trm;
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
