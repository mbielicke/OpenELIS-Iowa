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
package org.openelis.remote;

import org.openelis.domain.IdNameDO;
import org.openelis.domain.TestAnalyteDO;
import org.openelis.domain.TestDetailsDO;
import org.openelis.domain.TestIdNameMethodIdDO;
import org.openelis.domain.TestPrepDO;
import org.openelis.domain.TestReflexDO;
import org.openelis.domain.TestResultDO;
import org.openelis.domain.TestSectionDO;
import org.openelis.domain.TestTypeOfSampleDO;
import org.openelis.domain.TestWorksheetAnalyteDO;
import org.openelis.domain.TestWorksheetDO;
import org.openelis.domain.TestWorksheetItemDO;
import org.openelis.gwt.common.data.AbstractField;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.ejb.Remote;

@Remote
public interface TestRemote {

    public TestIdNameMethodIdDO getTestIdNameMethod(Integer testId);

    public TestIdNameMethodIdDO getTestIdNameMethodAndUnlock(Integer testId,
                                                             String session);

    public TestIdNameMethodIdDO getTestIdNameMethodAndLock(Integer testId,
                                                           String session) throws Exception;

    public TestDetailsDO getTestDetails(Integer testId);

    public List<TestPrepDO> getTestPreps(Integer testId);
    
    public List<TestReflexDO> getTestReflexes(Integer testId);
    
    public List<TestTypeOfSampleDO> getTestTypeOfSamples(Integer testId);
    
    public TestWorksheetDO getTestWorksheet(Integer testId);
    
    public List<TestWorksheetItemDO> getTestWorksheetItems(Integer testId);
    
    public List<TestWorksheetAnalyteDO> getTestWorksheetAnalytes(Integer testId);
    
    public List<IdNameDO> getTestAnalytesNotAddedToWorksheet(Integer testId);
    
    public List<TestAnalyteDO> getTestAnalytes(Integer testId);
    
    public List<TestSectionDO> getTestSections(Integer testId);
    
    public List<TestResultDO> getTestResults(Integer testId, Integer resultGroup);

    public Integer updateTest(TestIdNameMethodIdDO testIdNameMethod,
                              TestDetailsDO testDetails,
                              List<TestPrepDO> prepTestDOList,
                              List<TestTypeOfSampleDO> sampleTypeDOList,
                              List<TestReflexDO> testReflexDOList,
                              TestWorksheetDO worksheetDO,
                              List<TestWorksheetItemDO> itemDOList,
                              List<TestWorksheetAnalyteDO> twsaDOList,
                              List<TestAnalyteDO> analyteDOList,
                              List<TestSectionDO> sectionDOList,
                              List<TestResultDO> resultDOList) throws Exception;
    
    public List query(ArrayList<AbstractField> fields, int first, int max) throws Exception;

    public List validateForUpdate(TestIdNameMethodIdDO testIdNameMethod,
                                  TestDetailsDO testDetails,
                                  List<TestPrepDO> prepTestDOList,
                                  List<TestTypeOfSampleDO> sampleTypeDOList,
                                  List<TestReflexDO> testReflexDOList,
                                  TestWorksheetDO worksheetDO,
                                  List<TestWorksheetItemDO> itemDOList,
                                  List<TestWorksheetAnalyteDO> twsaDOList,
                                  List<TestAnalyteDO> analyteDOList,
                                  List<TestSectionDO> sectionDOList,
                                  List<TestResultDO> resultDOList);

    public List validateForAdd(TestIdNameMethodIdDO testIdNameMethod,
                               TestDetailsDO testDetails,
                               List<TestPrepDO> prepTestDOList,
                               List<TestTypeOfSampleDO> sampleTypeDOList,
                               List<TestReflexDO> testReflexDOList,
                               TestWorksheetDO worksheetDO,
                               List<TestWorksheetItemDO> itemDOList,
                               List<TestWorksheetAnalyteDO> twsaDOList,
                               List<TestAnalyteDO> analyteDOList,
                               List<TestSectionDO> sectionDOList,
                               List<TestResultDO> resultDOList);
    
    public List getMethodDropDownValues();

    public List getLabelDropDownValues();

    public List getTestTrailerDropDownValues();

    public List getScriptletDropDownValues();

    public List getPrepTestDropDownValues();
    
    public List getSectionDropDownValues();
    
    public List getTestAnalyteDropDownValues(Integer testId);
    
    public List getTestWSItemTypeDropDownValues();
    
    public List getTestResultsForTestAnalyte(Integer testId,Integer analyteId);
    
    public List<IdNameDO> getTestResultsforTest(Integer testId);
    
    public List<IdNameDO> getResultGroupsForTest(Integer testId);
    
    public List<IdNameDO> getUnitsOfMeasureForTest(Integer testId);
    
    public List getMatchingEntries(String name,int maxResults,String cat);
    
    public HashMap<Integer,List<IdNameDO>> getAnalyteResultsMap(Integer testId);
    
    public HashMap<Integer,List<Integer>> getResultGroupAnalytesMap(Integer testId);
    
    public HashMap<Integer,Integer> getUnitIdNumResMapForTest(Integer testId);
}
