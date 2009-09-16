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

import org.openelis.domain.TestSectionDO;
import org.openelis.domain.TestTypeOfSampleDO;
import org.openelis.local.TestLocal;


public class TestSectionManagerProxy {

    public TestSectionManager add(TestSectionManager man) throws Exception { 
        TestLocal tl;
        TestSectionDO section;
        
        tl = getTestLocal();         
        
        for(int i=0; i<man.count(); i++){
            section = man.getSectionAt(i);
            section.setTestId(man.getTestId());
            
            tl.addTestSection(section);
        }
        
        return man;
    }
    
    public TestSectionManager update(TestSectionManager man) throws Exception {
        TestLocal tl;
        TestSectionDO section;
        
        tl = getTestLocal(); 
        
        for(int i = 0; i < man.deleteCount(); i++) {
            tl.deleteTestSection(man.getDeletedAt(i));
        }
        
        for(int i = 0; i < man.count(); i++){
            section = man.getSectionAt(i);
            
            if(section.getId() == null){
                section.setTestId(man.getTestId());
                tl.addTestSection(section);
            }else
                tl.updateTestSection(section);
        }
        
        return man;
    }
    
    public TestTypeOfSampleManager fetchByTestId(Integer testId) throws Exception {
        TestLocal tl;
        ArrayList<TestTypeOfSampleDO> sampleTypes;
        TestTypeOfSampleManager ttsm;
        
        tl = getTestLocal();
        sampleTypes = tl.fetchSampleTypesById(testId);
        ttsm = TestTypeOfSampleManager.getInstance();
        ttsm.setTypes(sampleTypes);
        ttsm.setTestId(testId);
        
        return ttsm;
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
 