/**
 * Exhibit A - UIRF Open-source Based Public Software License.
 * 
 * The contents of this file are subject to the UIRF Open-source Based Public
 * Software License(the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * openelis.uhl.uiowa.edu
 * 
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License for
 * the specific language governing rights and limitations under the License.
 * 
 * The Original Code is OpenELIS code.
 * 
 * The Initial Developer of the Original Code is The University of Iowa.
 * Portions created by The University of Iowa are Copyright 2006-2008. All
 * Rights Reserved.
 * 
 * Contributor(s): ______________________________________.
 * 
 * Alternatively, the contents of this file marked "Separately-Licensed" may be
 * used under the terms of a UIRF Software license ("UIRF Software License"), in
 * which case the provisions of a UIRF Software License are applicable instead
 * of those above.
 */
package org.openelis.metamap;

import org.openelis.gwt.common.MetaMap;
import org.openelis.meta.LabelMeta;
import org.openelis.meta.MethodMeta;
import org.openelis.meta.ScriptletMeta;
import org.openelis.meta.TestMeta;
import org.openelis.meta.TestTrailerMeta;

public class TestMetaMap extends TestMeta implements MetaMap {

    public TestPrepMetaMap             TEST_PREP;
    public TestTypeOfSampleMetaMap     TEST_TYPE_OF_SAMPLE;
    public TestReflexMetaMap           TEST_REFLEX;
    public TestAnalyteMetaMap          TEST_ANALYTE;
    public TestWorksheetMetaMap        TEST_WORKSHEET;
    public MethodMeta                  METHOD;
    public ScriptletMeta               SCRIPTLET;
    public LabelMeta                   LABEL;
    public TestWorksheetItemMetaMap    TEST_WORKSHEET_ITEM;
    public TestSectionMetaMap          TEST_SECTION;
    public TestResultMetaMap           TEST_RESULT;
    public TestWorksheetAnalyteMetaMap TEST_WORKSHEET_ANALYTE;
    public TestTrailerMeta             TEST_TRAILER;

    public TestMetaMap() {
        super("t.");
        TEST_PREP = new TestPrepMetaMap("testPrep.");
        TEST_TYPE_OF_SAMPLE = new TestTypeOfSampleMetaMap("testTypeOfSample.");
        TEST_REFLEX = new TestReflexMetaMap("testReflex.");
        TEST_ANALYTE = new TestAnalyteMetaMap("testAnalyte.");
        TEST_WORKSHEET = new TestWorksheetMetaMap("testWorksheet.");
        METHOD = new MethodMeta(path + "method.");
        SCRIPTLET = new ScriptletMeta(path + "scriptlet.");
        LABEL = new LabelMeta(path + "label.");
        TEST_WORKSHEET_ITEM = new TestWorksheetItemMetaMap("testWorksheetItem.");
        TEST_SECTION = new TestSectionMetaMap("testSection.");
        TEST_RESULT = new TestResultMetaMap("testResult.");
        TEST_WORKSHEET_ANALYTE = new TestWorksheetAnalyteMetaMap("testWorksheetAnalyte.");
        TEST_TRAILER = new TestTrailerMeta(path + "testTrailer.");
    }

    public TestMetaMap(String path) {
        super(path);
        TEST_PREP = new TestPrepMetaMap(path + "testPrep.");
        TEST_TYPE_OF_SAMPLE = new TestTypeOfSampleMetaMap(path + "testTypeOfSample.");
        TEST_REFLEX = new TestReflexMetaMap(path + "testReflex.");
        TEST_ANALYTE = new TestAnalyteMetaMap(path + "testAnalyte.");
        TEST_WORKSHEET = new TestWorksheetMetaMap(path + "testWorksheet.");
        METHOD = new MethodMeta(path + "method.");
        SCRIPTLET = new ScriptletMeta(path + "scriptlet.");
        LABEL = new LabelMeta(path + "label.");
        TEST_WORKSHEET_ITEM = new TestWorksheetItemMetaMap(path + "testWorksheetItem.");
        TEST_SECTION = new TestSectionMetaMap(path + "testSection.");
        TEST_RESULT = new TestResultMetaMap(path + "testResult.");
        TEST_WORKSHEET_ANALYTE = new TestWorksheetAnalyteMetaMap(path + "testWorksheetAnalyte.");
        TEST_TRAILER = new TestTrailerMeta(path + "testTrailer.");
    }

    public TestWorksheetItemMetaMap getTestWorksheetItem() {
        return TEST_WORKSHEET_ITEM;
    }

    public TestWorksheetAnalyteMetaMap getTestWorksheetAnalyte() {
        return TEST_WORKSHEET_ANALYTE;
    }

    public MethodMeta getMethod() {
        return METHOD;
    }

    public ScriptletMeta getScriptlet() {
        return SCRIPTLET;
    }

    public LabelMeta getLabel() {
        return LABEL;
    }

    public static TestMetaMap getInstance() {
        return new TestMetaMap();
    }

    public TestPrepMetaMap getTestPrep() {
        return TEST_PREP;
    }

    public TestTypeOfSampleMetaMap getTestTypeOfSample() {
        return TEST_TYPE_OF_SAMPLE;
    }

    public TestReflexMetaMap getTestReflex() {
        return TEST_REFLEX;
    }

    public TestAnalyteMetaMap getTestAnalyte() {
        return TEST_ANALYTE;
    }

    public TestWorksheetMetaMap getTestWorksheet() {
        return TEST_WORKSHEET;
    }

    public TestSectionMetaMap getTestSection() {
        return TEST_SECTION;
    }

    public TestResultMetaMap getTestResult() {
        return TEST_RESULT;
    }

    public TestTrailerMeta getTestTrailer() {
        return TEST_TRAILER;
    }

    public boolean hasColumn(String name) {
        if (name.startsWith("testPrep."))
            return TEST_PREP.hasColumn(name);
        else if (name.startsWith("testTypeOfSample."))
            return TEST_TYPE_OF_SAMPLE.hasColumn(name);
        else if (name.startsWith("testReflex."))
            return TEST_REFLEX.hasColumn(name);
        else if (name.startsWith("testWorksheet."))
            return TEST_WORKSHEET.hasColumn(name);
        else if (name.startsWith("testWorksheetItem."))
            return TEST_WORKSHEET_ITEM.hasColumn(name);
        else if (name.startsWith("testWorksheetAnalyte."))
            return TEST_WORKSHEET_ANALYTE.hasColumn(name);
        else if (name.startsWith("testAnalyte."))
            return TEST_ANALYTE.hasColumn(name);
        else if (name.startsWith("testSection."))
            return TEST_SECTION.hasColumn(name);
        else if (name.startsWith("testResult."))
            return TEST_RESULT.hasColumn(name);
        else if (name.startsWith(path + "method."))
            return METHOD.hasColumn(name);
        else if (name.startsWith(path + "scriptlet."))
            return SCRIPTLET.hasColumn(name);
        else if (name.startsWith(path + "label."))
            return LABEL.hasColumn(name);
        else if (name.startsWith(path + "testTrailer."))
            return TEST_TRAILER.hasColumn(name);
        return super.hasColumn(name);
    }

    public String buildFrom(String name) {
        String from, wsFrom, wsiFrom;
        
        wsFrom = ", IN (t.testWorksheet) testWorksheet ";
        wsiFrom = ", IN (testWorksheet.testWorksheetItem) testWorksheetItem ";

        from = "Test t ";
        if (name.indexOf("testPrep.") > -1)
            from += ", IN (t.testPrep) testPrep ";
        if (name.indexOf("testTypeOfSample.") > -1)
            from += ", IN (t.testTypeOfSample) testTypeOfSample ";
        if (name.indexOf("testReflex.") > -1)
            from += ", IN (t.testReflex) testReflex ";
        if (name.indexOf("testAnalyte.") > -1)
            from += ", IN (t.testAnalyte) testAnalyte ";
        if (name.indexOf("testSection.") > -1)
            from += ", IN (t.testSection) testSection ";
        if (name.indexOf("testResult.") > -1)
            from += ", IN (t.testResult) testResult ";
        if (name.indexOf("testWorksheetAnalyte.") > -1)
            from += ", IN (t.testWorksheetAnalyte) testWorksheetAnalyte ";
        if (name.indexOf("testWorksheet.") > -1)
            from += wsFrom;
        if (name.indexOf("testWorksheetItem.") > -1)
            if (from.indexOf(wsFrom) < 0)
                from += wsFrom + wsiFrom;
            else
                from += wsiFrom;
        return from;
    }
}
