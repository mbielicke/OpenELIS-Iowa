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

import org.openelis.domain.Constants;
import org.openelis.domain.TestReflexViewDO;
import org.openelis.domain.TestResultViewDO;
import org.openelis.gwt.common.DataBaseUtil;
import org.openelis.gwt.common.InconsistencyException;
import org.openelis.gwt.common.TableFieldErrorException;
import org.openelis.gwt.common.ValidationErrorsList;
import org.openelis.local.TestReflexLocal;
import org.openelis.meta.TestMeta;
import org.openelis.utils.EJBFactory;

public class TestReflexManagerProxy {
    
    public TestReflexManager fetchByTestId(Integer testId) throws Exception {
        TestReflexManager trm;        
        ArrayList<TestReflexViewDO> reflexTests;    
                
        reflexTests = EJBFactory.getTestReflex().fetchByTestId(testId);
        trm = TestReflexManager.getInstance();
        trm.setReflexes(reflexTests);
        return trm;
    }
    
    public TestReflexManager add(TestReflexManager man,
                                 HashMap<Integer,Integer> analyteMap,
                                 HashMap<Integer,Integer> resultMap) throws Exception {
        TestReflexLocal tl;
        TestReflexViewDO reflexTest;
        Integer anaId, resId;
        
        tl = EJBFactory.getTestReflex();         
        
        for(int i=0; i < man.count(); i++) {
            reflexTest = man.getReflexAt(i);
            reflexTest.setTestId(man.getTestId());
            
            anaId = reflexTest.getTestAnalyteId();
            resId = reflexTest.getTestResultId();
            
            if(anaId < 0)
                reflexTest.setTestAnalyteId(analyteMap.get(anaId));
            
            if(resId < 0)
                reflexTest.setTestResultId(resultMap.get(resId));
            
            tl.add(reflexTest);
        }
        
        return man;
    }
    
    public TestReflexManager update(TestReflexManager man,
                                    HashMap<Integer,Integer> analyteMap,
                                    HashMap<Integer,Integer> resultMap) throws Exception {
        TestReflexLocal tl;
        TestReflexViewDO reflexTest;
        Integer anaId, resId;
        
        tl = EJBFactory.getTestReflex(); 
                
        for(int i = 0; i < man.deleteCount(); i++) {            
            tl.delete(man.getDeletedAt(i));
        }
        
        for(int i=0; i<man.count(); i++){
            reflexTest = man.getReflexAt(i);
            
            anaId = reflexTest.getTestAnalyteId();
            resId = reflexTest.getTestResultId();
                        
            if(anaId < 0)
                reflexTest.setTestAnalyteId(analyteMap.get(anaId));
            
            if(resId < 0)
                reflexTest.setTestResultId(resultMap.get(resId));
            
            if(reflexTest.getId() == null){                
                reflexTest.setTestId(man.getTestId());             
                tl.add(reflexTest);
            } else {                
                tl.update(reflexTest);
            }
        }
        
        return man;
    }
    
    public void validate(TestReflexManager trm,
                         boolean anaListValid,boolean resListValid,
                         HashMap<Integer, Integer> anaResGrpMap,
                         HashMap<Integer, List<TestResultViewDO>> resGrpRsltMap) throws Exception{
        int i;
        TestReflexViewDO data;
        List<List<Integer>> idsList;
        List<Integer> ids;
        String fieldName;
        ArrayList<TestReflexViewDO> testReflexDOList;
        ValidationErrorsList list;
        TestReflexLocal rl;
        Integer testId, anaId, resultId, typeId;

        fieldName = null;
        idsList = new ArrayList<List<Integer>>();
        testReflexDOList = trm.getReflexes();
        list = new ValidationErrorsList();
        rl = EJBFactory.getTestReflex();

        if(testReflexDOList == null)
            return;
        
        for (i = 0; i < testReflexDOList.size(); i++ ) {
            data = testReflexDOList.get(i);

            ids = new ArrayList<Integer>();

            testId = data.getAddTestId();            
            anaId = data.getTestAnalyteId();
            resultId = data.getTestResultId();
            
            if (testId != null &&  anaId != null && resultId != null) {
                ids.add(testId);
                ids.add(anaId);
                ids.add(resultId);
            }
                    
            try {
                rl.validate(data);                        
            
                if (!idsList.contains(ids)) {
                    idsList.add(ids);
                } else {
                    fieldName = TestMeta.getReflexAddTestName();
                    throw new InconsistencyException("fieldUniqueOnlyException");
                }
                
                fieldName = TestMeta.getReflexTestResultValue();
                validateAnalyteResultMapping(anaListValid, resListValid, anaResGrpMap,
                                             resGrpRsltMap, data);
                
                typeId = getResultTypeForReflexId(anaResGrpMap, resGrpRsltMap, data);
                    
                if(DataBaseUtil.isSame(Constants.dictionary().TEST_RES_TYPE_DEFAULT, typeId))                             
                    throw new InconsistencyException("resultDefaultReflexTestException");
                        

            } catch (InconsistencyException ex) {
                list.add(new TableFieldErrorException(ex.getMessage(),i,
                                                      fieldName,"testReflexTable"));
                
                
            } catch (Exception ex) {
                DataBaseUtil.mergeException(list, ex, "testReflexTable", i);
            }
        }
        
        if(list.size() > 0)
            throw list;
        
    }
    
    /**
     * This method validates whether the test result id and test analyte id in
     * "refDO" form a valid mapping such that the test result id belongs to the
     * result group that has been selected for the test analyte represented by
     * its id
     */
    private void validateAnalyteResultMapping(boolean anaListValid,
                                              boolean resListValid,
                                              HashMap<Integer, Integer> anaResGrpMap,
                                              HashMap<Integer, List<TestResultViewDO>> resGrpRsltMap,
                                              TestReflexViewDO data) throws InconsistencyException {
        Integer rg, resId, anaId;
        List<TestResultViewDO> resList;

        resId = data.getTestResultId();
        anaId = data.getTestAnalyteId();

        if (!anaListValid || !resListValid)
            return;

        //
        // find the result group selected for the test analyte from anaResGrpMap
        // using its id set in refDO,
        //        
        rg = anaResGrpMap.get(anaId);
        if (rg != null) {
            //
            // if the list obtained from anaResGrpMap does not contain the
            // test result id in refDO then that implies that this test result
            // doesn't belong to the result group selected for the test analyte
            // and thus an exception is thrown containing this message.
            //
            resList = resGrpRsltMap.get(rg);
            if (resList == null) {
                throw new InconsistencyException("resultDoesntBelongToAnalyteException");
            } else if (!listContainsId(resList, resId)) {
                throw new InconsistencyException("resultDoesntBelongToAnalyteException");
            }
        }
    }
    
    private Integer getResultTypeForReflexId(HashMap<Integer, Integer> anaResGrpMap,
                                              HashMap<Integer, List<TestResultViewDO>> resGrpRsltMap,
                                              TestReflexViewDO data) {
        Integer rg, anaId;
        List<TestResultViewDO> resList;
        TestResultViewDO result;

        anaId = data.getTestAnalyteId();

        //
        // find the result group selected for the test analyte from anaResGrpMap
        // using its id set in data,
        //        
        rg = anaResGrpMap.get(anaId);
        if (rg != null) {
            resList = resGrpRsltMap.get(rg);
            if (resList != null) {
                for(int j = 0; j < resList.size(); j++) {
                    result = resList.get(j);
                    if(DataBaseUtil.isSame(data.getTestResultId(), result.getId())) {                            
                        return result.getTypeId();
                    }
                }
            } 
        }   
        
        return null;
    }
    
    private boolean listContainsId(List<TestResultViewDO> resList, Integer resId) {
        TestResultViewDO data;
        
        for(int i = 0; i < resList.size(); i++) {
            data = resList.get(i);
            if(DataBaseUtil.isSame(resId, data.getId()))
                return true;
        }
        return false;
    }       
}
