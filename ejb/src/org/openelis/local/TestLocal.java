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
import org.openelis.domain.TestDO;
import org.openelis.domain.TestPrepDO;
import org.openelis.domain.TestReflexDO;
import org.openelis.domain.TestResultDO;
import org.openelis.domain.TestSectionDO;
import org.openelis.domain.TestTypeOfSampleDO;

@Local
public interface TestLocal {
    public TestDO fetchById(Integer testId) throws Exception;   
    public ArrayList<TestSectionDO> getTestSections(Integer testId) throws Exception;
    public ArrayList<TestTypeOfSampleDO> fetchSampleTypesById(Integer testId)throws Exception;
    public ArrayList<ArrayList<TestAnalyteDO>> fetchTestAnalytesById(Integer testId)throws Exception;
    public ArrayList<ArrayList<TestResultDO>> fetchTestResultsById(Integer testId) throws Exception;
    public ArrayList<TestPrepDO> fetchPrepTestsById(Integer testId) throws Exception;
    public ArrayList<TestReflexDO> fetchReflexTestsById(Integer testId) throws Exception;
    
    public void add(TestDO testDO) throws Exception;
    public void update(TestDO testDO) throws Exception;
    
    public void updateTestSection(TestSectionDO sectDO) throws Exception;
    public void addTestSection(TestSectionDO sectDO) throws Exception;
    public void deleteTestSection(TestSectionDO sectDO) throws Exception;
    
    public void updateSampleType(TestTypeOfSampleDO sampleType) throws Exception;
    public void addSampleType(TestTypeOfSampleDO sampleType) throws Exception;
    public void deleteSampleType(TestTypeOfSampleDO deletedAt) throws Exception;
       
    public void updateTestAnalyte(TestAnalyteDO anaDO) throws Exception;
    public void addTestAnalyte(TestAnalyteDO anaDO) throws Exception;    
    public void deleteTestAnalyte(TestAnalyteDO deletedAt) throws Exception;
    
    public void updateTestResult(TestResultDO testResult) throws Exception;
    public void addTestResult(TestResultDO testResult) throws Exception;
    public void deleteTestResult(TestResultDO deletedAt) throws Exception;
    
    public void updatePrepTest(TestPrepDO prepTest) throws Exception;
    public void addPrepTest(TestPrepDO prepTest) throws Exception;
    public void deletePrepTest(TestPrepDO deletedAt) throws Exception;
    
    public void updateReflexTest(TestReflexDO reflexTest) throws Exception; 
    public void addReflexTest(TestReflexDO reflexTest) throws Exception;
    public void deleteReflexTest(TestReflexDO deletedAt) throws Exception;     
    
    public void validateTest(TestDO test, List<TestSectionDO> sections,
                             List<TestTypeOfSampleDO> sampleTypes,
                             ArrayList<ArrayList<TestAnalyteDO>> analytes,
                             ArrayList<ArrayList<TestResultDO>> results,
                             ArrayList<TestPrepDO> prepTests) throws Exception;
                          
} 
