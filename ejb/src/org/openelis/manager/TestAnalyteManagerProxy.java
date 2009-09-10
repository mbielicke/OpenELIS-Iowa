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

import org.openelis.domain.TestAnalyteDO;
import org.openelis.local.TestLocal;

public class TestAnalyteManagerProxy{
    
    public TestAnalyteManager add(TestAnalyteManager man) throws Exception {
        TestLocal tl;
        ArrayList<TestAnalyteDO> list; 
        ArrayList<ArrayList<TestAnalyteDO>> grid;
        TestAnalyteDO anaDO;
        int i,j,so;        

        tl = getTestLocal();
        grid = man.getAnalytes();
        so = 0;
        
        for(i = 0; i < man.rowCount(); i++) {
            list = grid.get(i);
            for(j = 0; j < list.size(); j++) {
                anaDO = list.get(j); 
                anaDO.setTestId(man.getTestId());
                anaDO.setSortOrder(++so);
                tl.addTestAnalyte(anaDO);
            }
            
        }
        
        return man ;
    }
    
    public TestAnalyteManager update(TestAnalyteManager man) throws Exception {
        TestLocal tl;
        ArrayList<TestAnalyteDO> list; 
        ArrayList<ArrayList<TestAnalyteDO>> grid;
        TestAnalyteDO anaDO;
        int i,j,so;        
        
        tl = getTestLocal();
        grid = man.getAnalytes();
        so = 0;
        
        for(i = 0; i < man.deleteCount(); i++){
            tl.deleteTestAnalyte(man.getDeletedAt(i));
        }
        
        for(i = 0; i < man.rowCount(); i++) {
            list = grid.get(i);            
            for(j = 0; j < list.size(); j++) {
                anaDO = list.get(j);                
                anaDO.setSortOrder(++so);
                if(anaDO.getId() == null){                    
                    anaDO.setTestId(man.getTestId());
                    tl.addTestAnalyte(anaDO);                    
                }else {
                    tl.updateTestAnalyte(anaDO);
                }
            }
            
        }
        
        return man ;
    }
    
    public TestAnalyteManager fetchByTestId(Integer testId) throws Exception {
        TestLocal tl;
        ArrayList<ArrayList<TestAnalyteDO>> grid;
        TestAnalyteManager tam;
        
        tl = getTestLocal();
        grid = tl.fetchTestAnalytesById(testId);
        tam = TestAnalyteManager.getInstance();
        tam.setAnalytes(grid);
        tam.setTestId(testId);
        
        return tam;
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
