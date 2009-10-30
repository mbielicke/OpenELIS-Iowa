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
import java.util.List;

import javax.naming.InitialContext;

import org.openelis.domain.TestAnalyteViewDO;
import org.openelis.domain.TestResultViewDO;
import org.openelis.gwt.common.GridFieldErrorException;
import org.openelis.gwt.common.ValidationErrorsList;
import org.openelis.local.TestAnalyteLocal;
import org.openelis.metamap.TestMetaMap;
import org.openelis.utilcommon.DataBaseUtil;

public class TestAnalyteManagerProxy{
    
    private static final TestMetaMap meta = new TestMetaMap();
    
    public TestAnalyteManager fetchByTestId(Integer testId) throws Exception {
        ArrayList<ArrayList<TestAnalyteViewDO>> grid;
        TestAnalyteManager tam;
        
        grid = local().fetchByTestId(testId);
        tam = TestAnalyteManager.getInstance();
        tam.setAnalytes(grid);
        tam.setTestId(testId);
        
        return tam;
    }
    
    public TestAnalyteManager add(TestAnalyteManager man,HashMap<Integer,Integer> idMap) throws Exception {
        TestAnalyteLocal al;
        ArrayList<TestAnalyteViewDO> list; 
        ArrayList<ArrayList<TestAnalyteViewDO>> grid;
        TestAnalyteViewDO anaDO;
        int i,j,so,negId;

        al = local();
        grid = man.getAnalytes();
        so = 0;
        negId = 0;
        
        for(i = 0; i < man.rowCount(); i++) {
            list = grid.get(i);
            for(j = 0; j < list.size(); j++) {                
                anaDO = list.get(j);
                if(j == 0)
                    negId = anaDO.getId();                                    
                anaDO.setTestId(man.getTestId());
                anaDO.setSortOrder(++so);
                al.add(anaDO);
                
                if(j == 0)
                    idMap.put(negId, anaDO.getId());
            }
            
        }
        
        return man ;
    }
    
    public TestAnalyteManager update(TestAnalyteManager man,HashMap<Integer,Integer> idMap) throws Exception {
        TestAnalyteLocal al;
        ArrayList<TestAnalyteViewDO> list; 
        ArrayList<ArrayList<TestAnalyteViewDO>> grid;
        TestAnalyteViewDO anaDO;
        int i,j,so,negId;       
        
        
        al = local();
        grid = man.getAnalytes();
        so = 0;
        negId = 0;
        
        for(i = 0; i < man.deleteCount(); i++){
            al.delete(man.getDeletedAt(i));
        }
        
        for(i = 0; i < man.rowCount(); i++) {
            list = grid.get(i);            
            for(j = 0; j < list.size(); j++) {                
                anaDO = list.get(j);                     
                anaDO.setSortOrder(++so);                
                if(anaDO.getId() == null) {                                            
                    anaDO.setTestId(man.getTestId());
                    al.add(anaDO);                    
                } else if(anaDO.getId() < 0){
                    negId = anaDO.getId();
                    anaDO.setTestId(man.getTestId());
                    al.add(anaDO);                    
                    idMap.put(negId, anaDO.getId());
                } else {
                    al.update(anaDO);
                }
            }
            
        }
        
        return man ;
    } 
    
    public void validate(TestAnalyteManager tam, TestResultManager trm,
                         HashMap<Integer, Integer> anaResGrpMap) throws Exception {        
        TestAnalyteLocal al;
        ValidationErrorsList list;
        int i, j; 
        List<TestAnalyteViewDO> analist;
        TestAnalyteViewDO anaDO;
        GridFieldErrorException exc;  
        Integer rg;
        ArrayList<ArrayList<TestAnalyteViewDO>> grid;        
        ArrayList<ArrayList<TestResultViewDO>> results;
        
        al = local();
        list = new ValidationErrorsList();
        grid = tam.getAnalytes();                        
        results = trm.getResults();

        for (i = 0; i < grid.size(); i++ ) {
            analist = grid.get(i);
            
            for (j = 0; j < analist.size(); j++ ) {
                anaDO = analist.get(j);
                rg = anaDO.getResultGroup();
                if(j == 0 && !DataBaseUtil.isEmpty(rg))
                    anaResGrpMap.put(anaDO.getId(), rg);
                
                try {
                    al.validate(anaDO);
                    
                    if(rg > results.size()){
                        exc = new GridFieldErrorException("invalidResultGroupException", i, j,
                                                          meta.TEST_ANALYTE.getResultGroup(),
                                                          "analyteTable");
                        list.add(exc);
                    } 
                } catch (Exception e) {                    
                    DataBaseUtil.mergeException(list, e, "analyteTable", i, j);
                }
                
                                               
            }
        } 
        
        if(list.size() > 0)
            throw list;
                
    }
    
    private TestAnalyteLocal local(){
        try{
            InitialContext ctx = new InitialContext();
            return (TestAnalyteLocal)ctx.lookup("openelis/TestAnalyteBean/local");
        }catch(Exception e){
             System.out.println(e.getMessage());
             return null;
        }
    }
    
}
