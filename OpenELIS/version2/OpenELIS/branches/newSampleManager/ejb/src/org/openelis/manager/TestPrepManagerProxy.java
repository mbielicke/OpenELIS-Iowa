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
import java.util.List;

import org.openelis.bean.TestPrepBean;
import org.openelis.domain.TestPrepViewDO;
import org.openelis.gwt.common.DataBaseUtil;
import org.openelis.gwt.common.TableFieldErrorException;
import org.openelis.gwt.common.ValidationErrorsList;
import org.openelis.meta.TestMeta;
import org.openelis.utils.EJBFactory;

public class TestPrepManagerProxy {

    public TestPrepManager fetchByTestId(Integer testId) throws Exception {
        TestPrepManager tpm;
        ArrayList<TestPrepViewDO> prepTests;
                
        prepTests = EJBFactory.getTestPrep().fetchByTestId(testId);
        tpm = TestPrepManager.getInstance();
        tpm.setPreps(prepTests);
        return tpm;
        
    }
    
    public TestPrepManager add(TestPrepManager man) throws Exception {        
        TestPrepBean tl;
        TestPrepViewDO prepTest;
        
        tl = EJBFactory.getTestPrep();         
        
        for(int i=0; i<man.count(); i++){
            prepTest = man.getPrepAt(i);
            prepTest.setTestId(man.getTestId());
            
            tl.add(prepTest);
        }
        
        return man;
    }
    
    public TestPrepManager update(TestPrepManager man) throws Exception {        
        TestPrepBean tl;
        TestPrepViewDO prepTest;
        
        tl = EJBFactory.getTestPrep(); 
        
        for(int i = 0; i < man.deleteCount(); i++) {
            tl.delete(man.getDeletedAt(i));
        }
        
        for(int i=0; i<man.count(); i++){
            prepTest = man.getPrepAt(i);
            
            if(prepTest.getId() == null){
                prepTest.setTestId(man.getTestId());
                tl.add(prepTest);
            }else
                tl.update(prepTest);
        }
        
        return man;
    }
    
    public void validate(TestPrepManager man) throws Exception {
        int numReq, i;
        ValidationErrorsList list;
        List<Integer> testPrepIdList;
        TableFieldErrorException exc;
        TestPrepViewDO prepDO;
        Integer prepId;        
        TestPrepBean pl;

        testPrepIdList = new ArrayList<Integer>();
        numReq = 0;
        list = new ValidationErrorsList();        
        pl = EJBFactory.getTestPrep();
        
        for (i = 0; i < man.count(); i++ ) {
            prepDO = man.getPrepAt(i);
            prepId = prepDO.getPrepTestId();
            
            try {
                pl.validate(prepDO);
            } catch (Exception e) {
                DataBaseUtil.mergeException(list, e, "testPrepTable", i);
            }
            
            if (!testPrepIdList.contains(prepId)) {
                testPrepIdList.add(prepId);
            } else {
                exc = new TableFieldErrorException("fieldUniqueOnlyException", i,
                                                   TestMeta.getPrepPrepTestName(),"testPrepTable");
                list.add(exc);
            }
            
            if (DataBaseUtil.isDifferent("Y",prepDO.getIsOptional())) {
                if (numReq >= 1) {
                    exc = new TableFieldErrorException("moreThanOnePrepTestOptionalException", i,
                                                       TestMeta.getPrepIsOptional(),"testPrepTable");
                    list.add(exc);
                }
                numReq++ ;
            }
        }
        
        if(list.size() > 0)
            throw list;
    }
}
