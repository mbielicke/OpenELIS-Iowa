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

import org.openelis.domain.DataObject;
import org.openelis.domain.TestAnalyteViewDO;
import org.openelis.domain.TestPrepViewDO;
import org.openelis.domain.TestReflexViewDO;
import org.openelis.domain.TestResultViewDO;
import org.openelis.domain.TestSectionViewDO;
import org.openelis.domain.TestTypeOfSampleDO;
import org.openelis.domain.TestViewDO;
import org.openelis.domain.TestWorksheetAnalyteViewDO;
import org.openelis.domain.TestWorksheetItemDO;
import org.openelis.domain.TestWorksheetViewDO;

/**
 * This class is used to bulk load test manager.
 */
public class TestManager1Accessor {
    /**
     * Set/get objects from test manager 
     */
    public static TestViewDO getTest(TestManager1 tm) {
        return tm.test;
    }
    
    public static void setTest(TestManager1 tm, TestViewDO test) {
        tm.test = test;
    }
    
    public static TestWorksheetViewDO getTestWorksheet(TestManager1 tm) {
        return tm.testWorksheet;
    }
    
    public static void setTestWorksheet(TestManager1 tm, TestWorksheetViewDO testWorksheet) {
        tm.testWorksheet = testWorksheet;
    }
    
    public static ArrayList<TestSectionViewDO> getSections(TestManager1 tm) {
        return tm.sections;
    }
    
    public static void setSections(TestManager1 tm, ArrayList<TestSectionViewDO> sections) {
        tm.sections = sections;
    }
    
    public static void addSection(TestManager1 tm, TestSectionViewDO section) {
        if (tm.sections == null)
            tm.sections = new ArrayList<TestSectionViewDO>();
        tm.sections.add(section);
    }
    
    public static ArrayList<TestTypeOfSampleDO> getTypeOfSamples(TestManager1 tm) {
        return tm.typeOfSamples;
    }
    
    public static void setTypeOfSamples(TestManager1 tm, ArrayList<TestTypeOfSampleDO> typeOfSamples) {
        tm.typeOfSamples = typeOfSamples;
    }
    
    public static void addTypeOfSample(TestManager1 tm, TestTypeOfSampleDO typeOfSample) {
        if (tm.typeOfSamples == null)
            tm.typeOfSamples = new ArrayList<TestTypeOfSampleDO>();
        tm.typeOfSamples.add(typeOfSample);
    }
    
    public static ArrayList<TestAnalyteViewDO> getAnalytes(TestManager1 tm) {
        return tm.analytes;
    }
    
    public static void setAnalytes(TestManager1 tm, ArrayList<TestAnalyteViewDO> analytes) {
        tm.analytes = analytes;
    }
    
    public static void addAnalyte(TestManager1 tm, TestAnalyteViewDO analyte) {
        if (tm.analytes == null)
            tm.analytes = new ArrayList<TestAnalyteViewDO>();
        tm.analytes.add(analyte);
    }
    
    public static ArrayList<TestResultViewDO> getResults(TestManager1 tm) {
        return tm.results;
    }
    
    public static void setResults(TestManager1 tm, ArrayList<TestResultViewDO> results) {
        tm.results = results;
    }
    
    public static void addResult(TestManager1 tm, TestResultViewDO result) {
        if (tm.results == null)
            tm.results = new ArrayList<TestResultViewDO>();
        tm.results.add(result);
    }
    
    public static ArrayList<TestPrepViewDO> getPreps(TestManager1 tm) {
        return tm.preps;
    }
    
    public static void setPreps(TestManager1 tm, ArrayList<TestPrepViewDO> preps) {
        tm.preps = preps;
    }
    
    public static void addPrep(TestManager1 tm, TestPrepViewDO prep) {
        if (tm.preps == null)
            tm.preps = new ArrayList<TestPrepViewDO>();
        tm.preps.add(prep);
    }
    
    public static ArrayList<TestReflexViewDO> getReflexes(TestManager1 tm) {
        return tm.reflexes;
    }
    
    public static void setReflexes(TestManager1 tm, ArrayList<TestReflexViewDO> reflexes) {
        tm.reflexes = reflexes;
    }
    
    public static void addReflex(TestManager1 tm, TestReflexViewDO reflex) {
        if (tm.reflexes == null)
            tm.reflexes = new ArrayList<TestReflexViewDO>();
        tm.reflexes.add(reflex);
    }
    
    public static ArrayList<TestWorksheetItemDO> getWorksheetItems(TestManager1 tm) {
        return tm.worksheetItems;
    }
    
    public static void setWorksheetItems(TestManager1 tm, ArrayList<TestWorksheetItemDO> worksheetItems) {
        tm.worksheetItems = worksheetItems;
    }
    
    public static void addWorksheetItem(TestManager1 tm, TestWorksheetItemDO worksheetItem) {
        if (tm.worksheetItems == null)
            tm.worksheetItems = new ArrayList<TestWorksheetItemDO>();
        tm.worksheetItems.add(worksheetItem);
    }
    
    public static ArrayList<TestWorksheetAnalyteViewDO> getWorksheetAnalytes(TestManager1 tm) {
        return tm.worksheetAnalytes;
    }
    
    public static void setWorksheetAnalytes(TestManager1 tm, ArrayList<TestWorksheetAnalyteViewDO> worksheetAnalytes) {
        tm.worksheetAnalytes = worksheetAnalytes;
    }
    
    public static void addWorksheetAnalyte(TestManager1 tm, TestWorksheetAnalyteViewDO worksheetAnalyte) {
        if (tm.worksheetAnalytes == null)
            tm.worksheetAnalytes = new ArrayList<TestWorksheetAnalyteViewDO>();
        tm.worksheetAnalytes.add(worksheetAnalyte);
    }
    
    public static ArrayList<DataObject> getRemoved(TestManager1 tm) {
        return tm.removed;
    }
    
    public static void setRemoved(TestManager1 tm, ArrayList<DataObject> removed) {
        tm.removed = removed;
    }
}