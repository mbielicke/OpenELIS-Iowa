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
import org.openelis.domain.TestDO;
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

    public TestDO getTestAndUnlock(Integer testId,String session);

    public TestDO getTestAndLock(Integer testId,String session) throws Exception;

    public TestDO getTest(Integer testId);

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
    
    public List<List<TestResultDO>> getTestResults(Integer testId);

    public Integer updateTest(TestDO testDO,List<TestPrepDO> prepTestDOList,
                              List<TestTypeOfSampleDO> sampleTypeDOList,
                              List<TestReflexDO> testReflexDOList,
                              TestWorksheetDO worksheetDO,
                              List<TestWorksheetItemDO> itemDOList,
                              List<TestWorksheetAnalyteDO> twsaDOList,
                              List<TestAnalyteDO> analyteDOList,
                              List<TestSectionDO> sectionDOList,
                              List<TestResultDO> resultDOList) throws Exception;
    
    public List query(ArrayList<AbstractField> fields, int first, int max) throws Exception;
    
    public List getTestAnalyteDropDownValues(Integer testId);    
    
    public List getTestResultsForTestAnalyte(Integer testId,Integer analyteId);
    
    public List<IdNameDO> getTestResultsforTest(Integer testId);
    
    public List<IdNameDO> getResultGroupsForTest(Integer testId);
    
    public List<IdNameDO> getUnitsOfMeasureForTest(Integer testId);   
    
    public List getTestAutoCompleteByName(String name, int maxResults);
    
    public HashMap<Integer,List<IdNameDO>> getAnalyteResultsMap(Integer testId);
    
    public HashMap<Integer,List<Integer>> getResultGroupAnalytesMap(Integer testId);
    
    public HashMap<Integer,Integer> getUnitIdNumResMapForTest(Integer testId);
}
