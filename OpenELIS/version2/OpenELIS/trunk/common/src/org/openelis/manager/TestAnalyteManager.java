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

import org.openelis.domain.TestAnalyteDO;
import org.openelis.gwt.common.RPC;

public class TestAnalyteManager implements RPC {

    private static final long  serialVersionUID = 1L;
    
    private transient int nextGroup;
    
    protected Integer testId;    
    protected transient static TestAnalyteManagerProxy proxy;    
    protected ArrayList<ArrayList<TestAnalyteDO>> testAnalytes;   
    protected ArrayList<TestAnalyteDO> deletedTestAnalytes;
        
    protected TestAnalyteManager() {
        testAnalytes = new ArrayList<ArrayList<TestAnalyteDO>>();
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
        if(testAnalytes == null) 
            return 0;
        
        return testAnalytes.size();
    }
    
    public ArrayList<ArrayList<TestAnalyteDO>> getTestAnalytes() {
        return testAnalytes;
    }

    public void setTestAnalytes(ArrayList<ArrayList<TestAnalyteDO>> testAnalytes) {
        this.testAnalytes = testAnalytes;
    }   
    
    public void addRow(boolean isNewGroup,boolean referPrev) {        
        addRowAt(rowCount()-1,isNewGroup,referPrev);        
    }
    
    public void addRowAt(int row,boolean isNewGroup,boolean referPrev) {              
        ArrayList<TestAnalyteDO> currlist;                                                                   
        
        if(testAnalytes == null)
            testAnalytes = new ArrayList<ArrayList<TestAnalyteDO>>();
        
            currlist = createNewDataListAt(row,isNewGroup,referPrev);
            if(testAnalytes.size() > row) 
                testAnalytes.add(row,currlist);
            else 
                testAnalytes.add(currlist);                    
        
    }
    
    public void removeRowAt(int row) {
        ArrayList<TestAnalyteDO> list;
        TestAnalyteDO anaDO;
        
        if(testAnalytes == null || row >= testAnalytes.size())
            return;
        
        list = testAnalytes.get(row);        
        
        if(deletedTestAnalytes == null)
            deletedTestAnalytes = new ArrayList<TestAnalyteDO>();
        
        for(int i = 0; i < list.size(); i++) {
            anaDO = list.get(i);
            if(anaDO.getId() != null)
                deletedTestAnalytes.add(anaDO);                       
        }
        
        testAnalytes.remove(row);
    }
    
    public void addColumnAt(int row, int col, Integer analyteId) {
        TestAnalyteDO anaDO;
        ArrayList<TestAnalyteDO> list;
        int i,rg,nrg;
        
        rg = testAnalytes.get(row).get(col-1).getRowGroup();
        for(i = row; i < testAnalytes.size(); i++) {
            list = testAnalytes.get(i);
            if(list.size() <= col-1)
                break;
                      
            nrg = list.get(col-1).getRowGroup();
            if(rg != nrg)
                break;                
            
            anaDO = new TestAnalyteDO();
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
            list = testAnalytes.get(i);
            if(list.size() <= col-1)                
                break;
            
            nrg = list.get(col-1).getRowGroup();
            if(rg != nrg)
                break;
                                             
            anaDO = new TestAnalyteDO();
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
        TestAnalyteDO anaDO,nextDO;
        ArrayList<TestAnalyteDO> list;
        int i,rg,nrg;
        
        anaDO = testAnalytes.get(row).get(col);        
        rg = anaDO.getRowGroup();

        if(deletedTestAnalytes == null) 
            deletedTestAnalytes = new ArrayList<TestAnalyteDO>();
        
        for(i = row; i < testAnalytes.size(); i++) {
            list = testAnalytes.get(i);
            nrg = list.get(0).getRowGroup();
            if(rg != nrg)
                break;
            
            nextDO = list.get(col);                                
            deletedTestAnalytes.add(nextDO);
            list.remove(col);
        }
        
        for(i = row-1; i > -1; i--) {
            list = testAnalytes.get(i);
            nrg = list.get(0).getRowGroup();
            if(rg != nrg)
                break;
            
            nextDO = list.get(col);                       
            deletedTestAnalytes.add(nextDO);
            list.remove(col);
        }
    }
    
    public int columnCount(int row) { 
        if(testAnalytes == null)
            return 0;
        try {
            return testAnalytes.get(row).size();
        } catch (IndexOutOfBoundsException ex) {            
            return 0;
        }
    }
    
    public TestAnalyteDO getTestAnalyteAt(int row, int col) {
        TestAnalyteDO ado;                        
        try {
            ado = testAnalytes.get(row).get(col);
        } catch (IndexOutOfBoundsException ex) {            
            return null;
        }
        
        return ado;   
    }
    
    public TestAnalyteManager add() throws Exception {
        return proxy().add(this);
    }
    
    public TestAnalyteManager update() throws Exception {
        return proxy().update(this);
    }       
    
    int deleteCount(){
        if(deletedTestAnalytes == null)
            return 0;
        
        return deletedTestAnalytes.size();
    }
    
    TestAnalyteDO getDeletedAt(int i) {
        return deletedTestAnalytes.get(i);
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
    private ArrayList<TestAnalyteDO> createNewDataListAt(int row, boolean isNewGroup,boolean referPrev) {
        TestAnalyteDO prevDO,currDO;        
        ArrayList<TestAnalyteDO> prevlist,currlist;        
        
        prevDO = null;        
        currlist = new ArrayList<TestAnalyteDO>(1);
        prevlist = null;
        
        if(isNewGroup) {
            currDO = new TestAnalyteDO();
            currDO.setRowGroup(getNextGroup());
            currDO.setIsReportable("N");
            currDO.setIsColumn("N");
            currlist.add(currDO);                
        } else {
            //if(row-1 >= 0)
              //  prevlist = testAnalytes.get(row-1);
            //else 
              //  prevlist = testAnalytes.get(row+1);
            
            if(referPrev && row-1 >= 0) {
                prevlist = testAnalytes.get(row-1);
            } else {
                prevlist = testAnalytes.get(row+1);
            }
            
            for(int i = 0; i < prevlist.size(); i++) {
                prevDO = prevlist.get(i);
                if(i == 0)
                    currDO = createTestAnalyteDO(prevDO, "N");
                else
                    currDO = createTestAnalyteDO(prevDO, "Y");
                currlist.add(currDO);
            }
        }
        
        return currlist;
    }
    
    private TestAnalyteDO createTestAnalyteDO(TestAnalyteDO prevDO,String isColumn){
        TestAnalyteDO currDO;
        
        currDO = new TestAnalyteDO();
        if("Y".equals(isColumn)) {
            currDO.setAnalyteId(prevDO.getAnalyteId());
            currDO.setAnalyteName(prevDO.getAnalyteName());            
            currDO.setTypeId(prevDO.getTypeId());
            currDO.setResultGroup(prevDO.getResultGroup());
            currDO.setIsReportable(prevDO.getIsReportable());
            currDO.setScriptletId(prevDO.getScriptletId());
            currDO.setScriptletName(prevDO.getScriptletName());
        } else {
            currDO.setIsReportable("N");
        }
        currDO.setRowGroup(prevDO.getRowGroup());
        currDO.setIsColumn(isColumn);
        
        return currDO;
    } 
    
    private int getNextGroup() {
        int i,rg;
        ArrayList<TestAnalyteDO> list;
        
        if(testAnalytes != null) {                     
            for (i = 0; i < testAnalytes.size(); i++) {
                list = testAnalytes.get(i);
                rg = list.get(0).getRowGroup();
                nextGroup = Math.max(nextGroup, rg);
            }
        }
        
        return ++nextGroup;
            
    }

}
