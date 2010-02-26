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

package org.openelis.modules.test.client;

import java.util.ArrayList;

import org.openelis.domain.ResultViewDO;
import org.openelis.domain.TestAnalyteViewDO;
import org.openelis.domain.TestResultDO;
import org.openelis.manager.AnalysisResultManager;


public class TestAnalyteDisplayManager<T> {
    protected enum Type {
        TEST, SAMPLE
    }
    
    protected ArrayList<Integer> indexes;     
    protected ArrayList<ArrayList<T>> grid;
    protected boolean isValid;
    protected int nextGroup;
    protected Type type;
    
    public TestAnalyteDisplayManager() {
        indexes = new ArrayList<Integer>();
    }           
    
    public void setDataGrid(ArrayList<ArrayList<T>> grid) {
        this.grid = grid;
        isValid = false;        
    }
    
    public void setType(Type type){
        this.type = type;
    }
    
    public boolean isHeaderRow(int r) {
        refreshIndexes();       
        return indexes.get(r) == -1;        
    }
    
    public T getObjectAt(int r, int c) {
        int index;
        T aDo;
        
        refreshIndexes();
        try {
            index = indexes.get(r);
            if(index == -1) 
                index = indexes.get(++r);               
            
            aDo = (T)grid.get(index).get(c);
        } catch (IndexOutOfBoundsException ex) {
            return null;
        }
        
        return aDo;   
    }
    
    public int columnCount(int row) {         
        int index;
        refreshIndexes();
        if(grid == null)
            return 0;
        try {
            index = indexes.get(row);
            if(index == -1)
                index = indexes.get(++row);
            
            if(type == Type.TEST)
                return grid.get(index).size();
            else
                return grid.get(index).size()+2;
        } catch (IndexOutOfBoundsException ex) {
            return 0;
        }
    }
    
    public int maxColumnCount(){
        int count, tmpCount;
        refreshIndexes();               
        count = 0;
        
        for(int i=0; i<indexes.size(); i++){
            tmpCount=columnCount(i);
            
            if(tmpCount > count)
                count = tmpCount;
        }
        
        return count;
    }
    
    public int getDataRowIndex(int row) {
        int index;
        refreshIndexes();
        if(grid == null)
            return -1;
        try {
            index = indexes.get(row);
        } catch (IndexOutOfBoundsException ex) {
            return -1;
        }
        
        if(index == -1)
            index = indexes.get(++row);
        
        return index;
    }
    
    public int getIndexAt(int index) {
        refreshIndexes();
        if(grid == null)
            return -1;
        try {
            return indexes.get(index);
        } catch (IndexOutOfBoundsException ex) {
            return -1;
        }
    }
    
    public int rowCount() {
        refreshIndexes();
        return indexes.size();
    }
    
    public void validateResultValue(AnalysisResultManager man, ResultViewDO resultDO, 
                                    Integer unitOfMeasureId) throws Exception {
        Integer testResultId;
        TestResultDO testResultDo;
        
        
        testResultId = man.validateResultValue(resultDO.getResultGroup(),
                                                   unitOfMeasureId,
                                                   resultDO.getValue());
        
        testResultDo = man.getTestResultList().get(testResultId);
        resultDO.setTypeId(testResultDo.getTypeId());
        resultDO.setTestResultId(testResultDo.getId());
    }
    
    private void refreshIndexes() {
        int i;
        Integer j,rg;
        String isColumn;
        TestAnalyteViewDO ado;
        ResultViewDO rdo;
        
        if(isValid)
            return;                                                       
        
        j = -1;
        nextGroup = 0;
        
        indexes.clear();                  
                
        for(i = 0 ; i < grid.size(); i++) {
            if(type == Type.TEST){
                ado = (TestAnalyteViewDO)grid.get(i).get(0);
                rg = ado.getRowGroup();
                isColumn = ado.getIsColumn();
            }else{
                rdo = (ResultViewDO)grid.get(i).get(0);
                rg = rdo.getRowGroup();
                isColumn = rdo.getIsColumn();
            }
                
            if(j != rg) {                 
                indexes.add(-1);                
                indexes.add(i);
                j = rg;
                continue;
            }            
            if("N".equals(isColumn)) {                
                indexes.add(i);
                continue;
            }                      
                        
            nextGroup = Math.max(nextGroup, rg);
        }
       
        isValid = true;
    }              
    
}
