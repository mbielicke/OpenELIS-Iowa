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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

import org.openelis.domain.TestWorksheetAnalyteViewDO;
import org.openelis.domain.TestWorksheetItemDO;
import org.openelis.domain.TestWorksheetViewDO;


public class TestWorksheetManager implements Serializable {

    private static final long serialVersionUID = 1L;
    
    protected Integer testId;        
    protected TestWorksheetViewDO worksheet;
    protected ArrayList<TestWorksheetItemDO> items,deletedItems;
    protected ArrayList<TestWorksheetAnalyteViewDO> analytes,deletedAnalytes;
    
    protected transient static TestWorksheetManagerProxy proxy;
    
    /**
     * This is a protected constructor
     */
    protected TestWorksheetManager() {
    }
    
    /**
     * Creates a new instance of this object. A default TestWorksheetViewDO object is also created.
     */
    public static TestWorksheetManager getInstance() {
        TestWorksheetManager twm;
        
        twm = new TestWorksheetManager();
        twm.worksheet = new TestWorksheetViewDO();      
        return twm;
    }
    
    public int itemCount(){
        if(items == null)
            return 0;        
        return items.size();
    }
    
    public int analyteCount() {
        if(analytes == null) 
            return 0;
        
        return analytes.size();
    }
    
    public TestWorksheetViewDO getWorksheet() {
        return worksheet;
    }    
       
    public TestWorksheetItemDO getItemAt(int i) {
        return items.get(i);
    }
    
    public void setItemAt(TestWorksheetItemDO item, int i) {
        if(items == null)
            items = new ArrayList<TestWorksheetItemDO>();
        items.set(i,item);
    }
    
    public void addItem(TestWorksheetItemDO item) {
        if(items == null)
            items = new ArrayList<TestWorksheetItemDO>();        
        items.add(item);
    }
    
    public void addItemAt(TestWorksheetItemDO item, int i) {
        if(items == null)
            items = new ArrayList<TestWorksheetItemDO>();        
        items.add(i,item);
    }
    
    public void removeItemAt(int i) {
        TestWorksheetItemDO item;
        
        if(items == null || i >= items.size()) 
            return;
        
        item = items.remove(i);        
        if(item.getId() != null) {
            if(deletedItems == null) 
                deletedItems = new ArrayList<TestWorksheetItemDO>();
            deletedItems.add(item);
            
        }
    }   
    
    public TestWorksheetAnalyteViewDO getAnalyteAt(int i) {
        return analytes.get(i);
    }
    
    public void setAnalyteAt(TestWorksheetAnalyteViewDO analyte, int i) {
        if(analytes == null)
            analytes = new ArrayList<TestWorksheetAnalyteViewDO>();
        analytes.set(i,analyte);
    }
    
    public void addAnalyte(TestWorksheetAnalyteViewDO analyte) {
        if(analytes == null)
            analytes = new ArrayList<TestWorksheetAnalyteViewDO>();
        analytes.add(analyte);
    }
    
    public void addAnalyteAt(TestWorksheetAnalyteViewDO analyte, int i) {
        if(analytes == null)
            analytes = new ArrayList<TestWorksheetAnalyteViewDO>();        
        analytes.add(i,analyte);
    }
    
    public void removeAnalyteAt(int i) {
        TestWorksheetAnalyteViewDO analyte;
        
        if(analytes == null || i >= analytes.size())
            return;
        
        analyte = analytes.remove(i);
        if(analyte.getId() != null) {
            if(deletedAnalytes == null)
                deletedAnalytes = new ArrayList<TestWorksheetAnalyteViewDO>();
            deletedAnalytes.add(analyte);
        }
    }
    
    public static TestWorksheetManager fetchByTestId(Integer testId) throws Exception {       
        return proxy().fetchByTestId(testId);
    } 
    
    public TestWorksheetManager add(HashMap<Integer,Integer> anaIdMap) throws Exception {
        return proxy().add(this, anaIdMap);
    }
    
    public TestWorksheetManager update(HashMap<Integer,Integer> anaIdMap) throws Exception {
        return proxy().update(this,anaIdMap);
    }
    
    public void validate() throws Exception { 
        proxy().validate(this);
    }
    
    Integer getTestId() {
        return testId;
    }

    public void setTestId(Integer testId) {
        this.testId = testId;
    }       
    
    void setWorksheet(TestWorksheetViewDO testWorksheet) {
        this.worksheet = testWorksheet;
    } 
    
    ArrayList<TestWorksheetItemDO> getItems() {
        return items;
    }

    void setItems(ArrayList<TestWorksheetItemDO> items) {
        this.items = items;
    }
    
    int deleteItemCount(){
        if(deletedItems == null)
            return 0;
        
        return deletedItems.size();
    }
    
    TestWorksheetItemDO getDeletedItemAt(int i) {
        return deletedItems.get(i);
    }
    
    ArrayList<TestWorksheetAnalyteViewDO> getAnalytes() {
        return analytes;
    }

    void setAnalytes(ArrayList<TestWorksheetAnalyteViewDO> analytes) {
        this.analytes = analytes;
    }       
    
    int deleteAnalyteCount(){
        if(deletedAnalytes == null)
            return 0;
        
        return deletedAnalytes.size();
    }
    
    TestWorksheetAnalyteViewDO getDeletedAnalyteAt(int i) {
        return deletedAnalytes.get(i);
    }        
           
    private static TestWorksheetManagerProxy proxy() {
        if(proxy == null)
            proxy = new TestWorksheetManagerProxy();
        
        return proxy;
    }
    
}
