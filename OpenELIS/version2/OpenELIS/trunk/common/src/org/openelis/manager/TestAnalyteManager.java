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

import org.openelis.domain.TestAnalyteViewDO;
import org.openelis.domain.TestAnalyteViewDO;
import org.openelis.gwt.common.RPC;

public class TestAnalyteManager implements RPC {

    private static final long  serialVersionUID = 1L;
    
    private transient int nextGroup;
    
    protected Integer testId;    
    protected transient static TestAnalyteManagerProxy proxy;    
    protected ArrayList<ArrayList<TestAnalyteViewDO>> analytes;   
    protected ArrayList<TestAnalyteViewDO> deletedAnalytes;
        
    protected TestAnalyteManager() {
        analytes = new ArrayList<ArrayList<TestAnalyteViewDO>>();
        nextGroup = 0;
    }
    
    /**
     * Creates a new instance of this object.
     */
    public static TestAnalyteManager getInstance() {
        TestAnalyteManager tam;
        
        tam = new TestAnalyteManager();        
        return tam;             
    }
    
    public static TestAnalyteManager findByTestId(Integer testId) throws Exception {       
        return proxy().fetchByTestId(testId);
    }
    
    public Integer getTestId() {
        return testId;
    }

    public void setTestId(Integer testId) {
        this.testId = testId;
    }
    
    public int rowCount() {
        if(analytes == null) 
            return 0;
        
        return analytes.size();
    }
    
    public ArrayList<ArrayList<TestAnalyteViewDO>> getAnalytes() {
        return analytes;
    }

    void setAnalytes(ArrayList<ArrayList<TestAnalyteViewDO>> testAnalytes) {
        this.analytes = testAnalytes;
    }   
    
    public void addRow(boolean isNewGroup,boolean referPrev,Integer id) {        
        addRowAt(rowCount()-1,isNewGroup,referPrev,id);        
    }
    
    public void addRowAt(int row,boolean isNewGroup,boolean referPrev,Integer id) {              
        ArrayList<TestAnalyteViewDO> currlist;                                                                   
        
        if(analytes == null)
            analytes = new ArrayList<ArrayList<TestAnalyteViewDO>>();
        
            currlist = createNewDataListAt(row,isNewGroup,referPrev,id);
            if(analytes.size() > row) 
                analytes.add(row,currlist);
            else 
                analytes.add(currlist);                    
        
    }
    
    public void removeRowAt(int row) {
        ArrayList<TestAnalyteViewDO> list;
        TestAnalyteViewDO anaDO;
        Integer id;
        
        if(analytes == null || row >= analytes.size())
            return;
        
        list = analytes.get(row);        
        
        if(deletedAnalytes == null)
            deletedAnalytes = new ArrayList<TestAnalyteViewDO>();
        
        for(int i = 0; i < list.size(); i++) {
            anaDO = list.get(i);
            id = anaDO.getId();
            if(id != null && id > 0)
                deletedAnalytes.add(anaDO);                       
        }
        
        analytes.remove(row);
    }
    
    public void addColumnAt(int row, int col, Integer analyteId) {
        TestAnalyteViewDO anaDO;
        ArrayList<TestAnalyteViewDO> list;
        int i,rg,nrg;
        
        rg = analytes.get(row).get(col-1).getRowGroup();
        for(i = row; i < analytes.size(); i++) {
            list = analytes.get(i);
            if(list.size() <= col-1)
                break;
                      
            nrg = list.get(col-1).getRowGroup();
            if(rg != nrg)
                break;                
            
            anaDO = new TestAnalyteViewDO();
            //anaDO.setId(--tempId);
            anaDO.setAnalyteId(analyteId);        
            anaDO.setIsColumn("Y");            
            anaDO.setIsReportable("N");
            anaDO.setRowGroup(rg);
            
            if(list.size() > col)
                list.add(col,anaDO);
            else
                list.add(anaDO);                                               
        }     
        
        for(i = row-1; i > -1; i--) {
            list = analytes.get(i);
            if(list.size() <= col-1)                
                break;
            
            nrg = list.get(col-1).getRowGroup();
            if(rg != nrg)
                break;
                                             
            anaDO = new TestAnalyteViewDO();
            //anaDO.setId(--tempId);
            anaDO.setAnalyteId(analyteId);        
            anaDO.setIsColumn("Y");            
            anaDO.setIsReportable("N");
            anaDO.setRowGroup(rg);
            
            if(list.size() > col)
                list.add(col,anaDO);
            else
                list.add(anaDO);                                               
        }
        
    }
    
    public void removeColumnAt(int row, int col) {
        TestAnalyteViewDO anaDO,nextDO;
        ArrayList<TestAnalyteViewDO> list;
        int i,rg,nrg;
        
        anaDO = analytes.get(row).get(col);        
        rg = anaDO.getRowGroup();

        if(deletedAnalytes == null) 
            deletedAnalytes = new ArrayList<TestAnalyteViewDO>();
        
        for(i = row; i < analytes.size(); i++) {
            list = analytes.get(i);
            nrg = list.get(0).getRowGroup();
            if(rg != nrg)
                break;
            
            nextDO = list.get(col);      
            if(nextDO.getId() != null) 
                deletedAnalytes.add(nextDO);
            list.remove(col);
        }
        
        for(i = row-1; i > -1; i--) {
            list = analytes.get(i);
            nrg = list.get(0).getRowGroup();
            if(rg != nrg)
                break;
            
            nextDO = list.get(col);  
            if(nextDO.getId() != null) 
                deletedAnalytes.add(nextDO);
            list.remove(col);
        }
    }
    
    public int columnCount(int row) { 
        if(analytes == null)
            return 0;
        try {
            return analytes.get(row).size();
        } catch (IndexOutOfBoundsException ex) {            
            return 0;
        }
    }
    
    public TestAnalyteViewDO getAnalyteAt(int row, int col) {
        TestAnalyteViewDO ado;                        
        try {
            ado = analytes.get(row).get(col);
        } catch (IndexOutOfBoundsException ex) {            
            return null;
        }
        
        return ado;   
    }
    
    public TestAnalyteManager add(HashMap<Integer,Integer> idMap) throws Exception {
        return proxy().add(this,idMap);
    }
    
    public TestAnalyteManager update(HashMap<Integer,Integer> idMap) throws Exception {
        return proxy().update(this,idMap);
    }       
    
    int deleteCount(){
        if(deletedAnalytes == null)
            return 0;
        
        return deletedAnalytes.size();
    }
    
    TestAnalyteViewDO getDeletedAt(int i) {
        return deletedAnalytes.get(i);
    }
    
    private static TestAnalyteManagerProxy proxy() {
        if(proxy == null)
            proxy = new TestAnalyteManagerProxy();
        
        return proxy;
    }    
    
    /**
     *  
     * 
     */
    private ArrayList<TestAnalyteViewDO> createNewDataListAt(int row, boolean isNewGroup,boolean referPrev,Integer id) {
        TestAnalyteViewDO prevDO,currDO;        
        ArrayList<TestAnalyteViewDO> prevlist,currlist;        
        
        prevDO = null;        
        currlist = new ArrayList<TestAnalyteViewDO>(1);
        prevlist = null;
        
        if(isNewGroup) {
            currDO = new TestAnalyteViewDO();
            currDO.setId(id);
            currDO.setRowGroup(getNextGroup());
            currDO.setIsReportable("N");
            currDO.setIsColumn("N");
            currlist.add(currDO);                
        } else {            
            if(referPrev && row-1 >= 0) {
                prevlist = analytes.get(row-1);
            } else {
                prevlist = analytes.get(row+1);
            }
            
            for(int i = 0; i < prevlist.size(); i++) {
                prevDO = prevlist.get(i);
                if(i == 0)
                    currDO = createTestAnalyteViewDO(prevDO, "N",id);
                else
                    currDO = createTestAnalyteViewDO(prevDO, "Y",id);
                currlist.add(currDO);
            }
        }
        
        return currlist;
    }
    
    private TestAnalyteViewDO createTestAnalyteViewDO(TestAnalyteViewDO prevDO,String isColumn,Integer id){
        TestAnalyteViewDO currDO;
        
        currDO = new TestAnalyteViewDO();
        if("Y".equals(isColumn)) {
            currDO.setAnalyteId(prevDO.getAnalyteId());
            currDO.setAnalyteName(prevDO.getAnalyteName());            
            currDO.setTypeId(prevDO.getTypeId());
            currDO.setResultGroup(prevDO.getResultGroup());
            currDO.setIsReportable(prevDO.getIsReportable());
            currDO.setScriptletId(prevDO.getScriptletId());
            currDO.setScriptletName(prevDO.getScriptletName());
        } else {
            currDO.setId(id);
            currDO.setIsReportable("N");
        }
        currDO.setRowGroup(prevDO.getRowGroup());
        currDO.setIsColumn(isColumn);
        
        return currDO;
    } 
    
    private int getNextGroup() {
        int i,rg;
        ArrayList<TestAnalyteViewDO> list;
        
        if(analytes != null) {                     
            for (i = 0; i < analytes.size(); i++) {
                list = analytes.get(i);
                rg = list.get(0).getRowGroup();
                nextGroup = Math.max(nextGroup, rg);
            }
        }
        
        return ++nextGroup;
            
    }

}
