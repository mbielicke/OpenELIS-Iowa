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
package org.openelis.modules.sample.client;

import java.util.ArrayList;

import org.openelis.domain.ResultViewDO;

public class ResultDisplayManager {
  
    private ArrayList<Integer> indexes;     
    private ArrayList<ArrayList<ResultViewDO>> grid;
    private boolean isValid;
    private int nextGroup;
    
    public ResultDisplayManager() {       
        indexes = new ArrayList<Integer>();                    
    }           
    
    public void setDataGrid(ArrayList<ArrayList<ResultViewDO>> grid) {
        this.grid = grid;
        isValid = false;        
    }
    
    public boolean isHeaderRow(int r) {
        refreshIndexes();       
        return indexes.get(r) == -1;        
    }
    
    public ResultViewDO getResultAt(int r, int c) {
        int index;
        ResultViewDO rdo;
        
        refreshIndexes();               
        try {
            index = indexes.get(r);
            if(index == -1) 
                index = indexes.get(++r);               
            
            rdo = grid.get(index).get(c);
        } catch (IndexOutOfBoundsException ex) {
            return null;
        }
        
        return rdo;   
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
    
    public int maxColumnCount(){
        int count, tmpCount;
        refreshIndexes();               
        count = 0;
        
        for(int i=0; i<indexes.size(); i++){
            tmpCount=columnCount(i)+1;
            
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
        ResultViewDO rdo;                
        
        if(isValid)
            return;                                                       
        
        j = -1;
        nextGroup = 0;
        
        indexes.clear();                  
                
        for(i = 0 ; i < grid.size(); i++) {
            rdo = (ResultViewDO)grid.get(i).get(0);
            rg = rdo.getRowGroup();
            
            if(j != rg) {                 
                indexes.add(-1);                
                indexes.add(i);
                j = rg;
                continue;
            }            
            if("N".equals(rdo.getIsColumn())) {                
                indexes.add(i);
                continue;
            }                      
                        
            nextGroup = Math.max(nextGroup, rg);
        }
       
        isValid = true;
    }              
    
}
