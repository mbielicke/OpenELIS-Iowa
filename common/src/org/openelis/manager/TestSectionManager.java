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

import org.openelis.domain.TestSectionDO;
import org.openelis.gwt.common.RPC;

public class TestSectionManager implements RPC {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    
    protected Integer testId;
    protected ArrayList<TestSectionDO> sections;
    protected ArrayList<TestSectionDO> deletedSections;
    
    protected transient static TestSectionManagerProxy proxy;
    
    protected TestSectionManager() {
        sections = null;
    }
    
    /**
     * Creates a new instance of this object.
     */
    public static TestSectionManager getInstance() {
        TestSectionManager tsm;
        
        tsm = new TestSectionManager();
        tsm.sections = new ArrayList<TestSectionDO>();
        
        return tsm;
    }
    
    public Integer getTestId() {
        return testId;
    }

    public void setTestId(Integer testId) {
        this.testId = testId;
    }       
   
    
    public int count(){
        if(sections == null)
            return 0;
        
        return sections.size();
    }
    
    public TestSectionDO getSectionAt(int i) {
        return sections.get(i);
    }
    
    public void setSectionAt(TestSectionDO section, int i) {
        sections.set(i, section);
    }
    
    public void addSection(TestSectionDO section){
        if(sections == null)
            sections = new ArrayList<TestSectionDO>();
        
        sections.add(section);
    }
    
    public void addSectionAt(TestSectionDO section, int i){
        if(sections == null)
            sections = new ArrayList<TestSectionDO>();
        
        sections.add(i, section);
    }
    
    public void removeSectionAt(int i){
        if(sections == null || i >= sections.size())
            return;
        
        TestSectionDO tmpDO = sections.remove(i);
        
        if(deletedSections == null)
            deletedSections = new ArrayList<TestSectionDO>();
        
        deletedSections.add(tmpDO);
    }
    
    //service methods
    public TestSectionManager add() throws Exception {
        return proxy().add(this);
    }
    
    public TestSectionManager update() throws Exception {
        return proxy().update(this);
    }
    
    ArrayList<TestSectionDO> getSections() {
        return sections;
    }

    void setSections(ArrayList<TestSectionDO> testSections) {
        this.sections = testSections;
    }
    
    int deleteCount(){
        if(deletedSections == null)
            return 0;
        
        return deletedSections.size();
    }
    
    TestSectionDO getDeletedAt(int i){
        return deletedSections.get(i);
    }
    
    private static TestSectionManagerProxy proxy(){
        if(proxy == null)
            proxy = new TestSectionManagerProxy();
        
        return proxy;
    }
    
    
}
