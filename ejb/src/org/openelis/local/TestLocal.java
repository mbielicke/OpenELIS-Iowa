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
package org.openelis.local;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.Local;

import org.openelis.domain.TestAnalyteDO;
import org.openelis.domain.TestAnalyteViewDO;
import org.openelis.domain.TestDO;
import org.openelis.domain.TestPrepViewDO;
import org.openelis.domain.TestReflexViewDO;
import org.openelis.domain.TestResultDO;
import org.openelis.domain.TestSectionViewDO;
import org.openelis.domain.TestTypeOfSampleDO;
import org.openelis.domain.TestViewDO;
import org.openelis.domain.TestWorksheetAnalyteViewDO;
import org.openelis.domain.TestWorksheetItemDO;
import org.openelis.domain.TestWorksheetViewDO;

@Local
public interface TestLocal {
    public TestViewDO fetchById(Integer testId) throws Exception;   
    public ArrayList<TestSectionViewDO> getTestSections(Integer testId) throws Exception;
    public ArrayList<TestTypeOfSampleDO> fetchSampleTypesById(Integer testId)throws Exception;
    public ArrayList<ArrayList<TestAnalyteViewDO>> fetchTestAnalytesById(Integer testId)throws Exception;
    public ArrayList<ArrayList<TestResultDO>> fetchTestResultsById(Integer testId) throws Exception;
    public ArrayList<TestPrepViewDO> fetchPrepTestsById(Integer testId) throws Exception;
    public ArrayList<TestReflexViewDO> fetchReflexTestsById(Integer testId) throws Exception;
    public TestWorksheetViewDO getTestWorksheet(Integer testId) throws Exception;
    public ArrayList<TestWorksheetAnalyteViewDO> getTestWorksheetAnalytes(Integer testId) throws Exception;
    public ArrayList<TestWorksheetItemDO> getTestWorksheetItems(Integer testId) throws Exception;
    
    public void add(TestDO testDO) throws Exception;
    public void update(TestDO testDO) throws Exception;
    
    public void updateTestSection(TestSectionViewDO sectDO) throws Exception;
    public void addTestSection(TestSectionViewDO sectDO) throws Exception;
    public void deleteTestSection(TestSectionViewDO sectDO) throws Exception;
    
    public void updateSampleType(TestTypeOfSampleDO sampleType) throws Exception;
    public void addSampleType(TestTypeOfSampleDO sampleType) throws Exception;
    public void deleteSampleType(TestTypeOfSampleDO deletedAt) throws Exception;
       
    public void updateTestAnalyte(TestAnalyteDO anaDO) throws Exception;
    public void addTestAnalyte(TestAnalyteDO anaDO) throws Exception;    
    public void deleteTestAnalyte(TestAnalyteDO deletedAt) throws Exception;
    
    public void updateTestResult(TestResultDO testResult) throws Exception;
    public void addTestResult(TestResultDO testResult) throws Exception;
    public void deleteTestResult(TestResultDO deletedAt) throws Exception;
    
    public void updatePrepTest(TestPrepViewDO prepTest) throws Exception;
    public void addPrepTest(TestPrepViewDO prepTest) throws Exception;
    public void deletePrepTest(TestPrepViewDO deletedAt) throws Exception;
    
    public void updateReflexTest(TestReflexViewDO reflexTest) throws Exception; 
    public void addReflexTest(TestReflexViewDO reflexTest) throws Exception;
    public void deleteReflexTest(TestReflexViewDO deletedAt) throws Exception;  
    
    public void updateTestWorksheet(TestWorksheetViewDO worksheet) throws Exception;
    public void addTestWorksheet(TestWorksheetViewDO worksheet) throws Exception;
    
    public void updateTestWorksheetItem(TestWorksheetItemDO item) throws Exception;
    public void addTestWorksheetItem(TestWorksheetItemDO item) throws Exception;    
    public void deleteTestWorksheetItem(TestWorksheetItemDO deletedItemAt) throws Exception; 
    
    public void updateTestWorksheetAnalyte(TestWorksheetAnalyteViewDO analyte) throws Exception;
    public void addTestWorksheetAnalyte(TestWorksheetAnalyteViewDO analyte) throws Exception;
    public void deleteTestWorksheetAnalyte(TestWorksheetAnalyteViewDO deletedAnalyteAt) throws Exception;
    
    public void validate(TestViewDO test, List<TestSectionViewDO> sections,
                             List<TestTypeOfSampleDO> sampleTypes,
                             ArrayList<ArrayList<TestAnalyteViewDO>> analytes,
                             ArrayList<ArrayList<TestResultDO>> results,
                             ArrayList<TestPrepViewDO> prepTests,
                             ArrayList<TestReflexViewDO> reflexTests, 
                             TestWorksheetViewDO testWorksheetViewDO, 
                             ArrayList<TestWorksheetItemDO> arrayList,
                             ArrayList<TestWorksheetAnalyteViewDO> wsanalytes) throws Exception;   
} 
