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

import org.openelis.domain.TestAnalyteViewDO;


public class TestAnalyteDisplayManager {
  
    private ArrayList<Integer> indexes;     
    private ArrayList<ArrayList<TestAnalyteViewDO>> grid;
    private boolean isValid;
    private int nextGroup;
    
    public TestAnalyteDisplayManager() {       
        indexes = new ArrayList<Integer>();                    
    }           
    
    public void setDataGrid(ArrayList<ArrayList<TestAnalyteViewDO>> grid) {
        this.grid = grid;
        isValid = false;        
    }
    
    public boolean isHeaderRow(int r) {
        refreshIndexes();       
        return indexes.get(r) == -1;        
    }
    
    public TestAnalyteViewDO getTestAnalyteAt(int r, int c) {
        int index;
        TestAnalyteViewDO ado;
        
        refreshIndexes();               
        try {
            index = indexes.get(r);
            if(index == -1) 
                index = indexes.get(++r);               
            
            ado = grid.get(index).get(c);
        } catch (IndexOutOfBoundsException ex) {
            ex.printStackTrace();
            return null;
        }
        
        return ado;   
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
            
            return grid.get(index).size();
        } catch (IndexOutOfBoundsException ex) {
            ex.printStackTrace();
            return 0;
        }
    }
    
    public int getDataRowIndex(int row) {
        int index;
        refreshIndexes();
        if(grid == null)
            return -1;
        try {
            index = indexes.get(row);
        } catch (IndexOutOfBoundsException ex) {
            //ex.printStackTrace();
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
            //ex.printStackTrace();
            return -1;
        }
    }
    
    public int rowCount() {
        refreshIndexes();
        return indexes.size();
    }
    
    private void refreshIndexes() {
        int i;
        Integer j,rg;
        TestAnalyteViewDO ado;                
        
        if(isValid)
            return;                                                       
        
        j = -1;
        nextGroup = 0;
        
        indexes.clear();                  
                
        for(i = 0 ; i < grid.size(); i++) {
            ado = (TestAnalyteViewDO)grid.get(i).get(0);
            rg = ado.getRowGroup();
            
            if(j != rg) {                 
                indexes.add(-1);                
                indexes.add(i);
                j = rg;
                continue;
            }            
            if("N".equals(ado.getIsColumn())) {                
                indexes.add(i);
                continue;
            }                      
                        
            nextGroup = Math.max(nextGroup, rg);
        }
       
        isValid = true;
    }              
    
}
