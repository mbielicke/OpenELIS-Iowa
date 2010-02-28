package org.openelis.remote;

import javax.ejb.Remote;

import org.openelis.manager.TestAnalyteManager;
import org.openelis.manager.TestManager;
import org.openelis.manager.TestPrepManager;
import org.openelis.manager.TestReflexManager;
import org.openelis.manager.TestResultManager;
import org.openelis.manager.TestTypeOfSampleManager;
import org.openelis.manager.TestWorksheetManager;

@Remote
public interface TestManagerRemote {
    public TestManager fetchById(Integer testId) throws Exception;

    public TestManager fetchWithSampleTypes(Integer testId) throws Exception;

    public TestManager fetchWithAnalytesAndResults(Integer testId) throws Exception;

    public TestManager fetchWithPrepTestsSampleTypes(Integer testId) throws Exception;

    public TestManager fetchWithPrepTestsAndReflexTests(Integer testId) throws Exception;

    public TestManager fetchWithWorksheet(Integer testId) throws Exception;

    public TestManager update(TestManager man) throws Exception;

    public TestManager add(TestManager man) throws Exception;

    public TestManager fetchForUpdate(Integer testId) throws Exception;

    public TestManager abortUpdate(Integer testId) throws Exception;

    public TestTypeOfSampleManager fetchSampleTypeByTestId(Integer testId) throws Exception;

    public TestAnalyteManager fetchTestAnalytesByTestId(Integer testId) throws Exception;

    public TestResultManager fetchTestResultsByTestId(Integer testId) throws Exception;

    public TestPrepManager fetchPrepTestsByTestId(Integer testId) throws Exception;

    public TestReflexManager fetchReflexiveTestsByTestId(Integer testId) throws Exception;

    public TestWorksheetManager fetchWorksheetByTestId(Integer testId) throws Exception;

}
