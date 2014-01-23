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
package org.openelis.manager;

import java.io.Serializable;
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
import org.openelis.utilcommon.ResultFormatter;

public class TestManager1 implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * Flags that specify what optional data to load with the manager
     */
    public enum Load {
        SECTION, TYPE, ANALYTE, RESULT, PREP, REFLEX, WORKSHEET
    };

    protected TestViewDO                            test;
    protected ArrayList<TestSectionViewDO>          sections;
    protected ArrayList<TestTypeOfSampleDO>         types;
    protected ArrayList<TestAnalyteViewDO>          analytes;
    protected ArrayList<TestResultViewDO>           results;
    protected ArrayList<TestPrepViewDO>             preps;
    protected ArrayList<TestReflexViewDO>           reflexes;
    protected TestWorksheetViewDO                   worksheet;
    protected ArrayList<TestWorksheetItemDO>        items;
    protected ArrayList<TestWorksheetAnalyteViewDO> worksheetAnalytes;
    protected ArrayList<DataObject>                 removed;
    protected int                                   nextUID = -1;

    protected transient ResultFormatter             formatter;
    transient public final TestSection              section = new TestSection();
    transient public final TestType                 type    = new TestType();
    transient public final TestAnalyte              analyte = new TestAnalyte();
    transient public final TestResult               result  = new TestResult();
    transient public final TestPrep                 prep    = new TestPrep();
    transient public final TestReflex               reflex  = new TestReflex();

    /**
     * Class to manage Test Section information
     */
    public class TestSection {
        /**
         * Returns the section at specified index.
         */
        public TestSectionViewDO get(int i) {
            return sections.get(i);
        }

        public TestSectionViewDO add() {
            TestSectionViewDO data;

            data = new TestSectionViewDO();
            if (sections == null)
                sections = new ArrayList<TestSectionViewDO>();
            sections.add(data);

            return data;
        }

        /**
         * Removes a section from the list
         */
        public void remove(int i) {
            TestSectionViewDO data;

            data = sections.remove(i);
            dataObjectRemove(data.getId(), data);
        }

        public void remove(TestSectionViewDO data) {
            sections.remove(data);
            dataObjectRemove(data.getId(), data);
        }

        /**
         * Returns the number of sections associated with this test
         */
        public int count() {
            if (sections != null)
                return sections.size();
            return 0;
        }
    }

    /**
     * Class to manage Test Type information
     */
    public class TestType {
        /**
         * Returns the type at specified index.
         */
        public TestTypeOfSampleDO get(int i) {
            return types.get(i);
        }

        public TestTypeOfSampleDO add() {
            TestTypeOfSampleDO data;

            data = new TestTypeOfSampleDO();
            if (types == null)
                types = new ArrayList<TestTypeOfSampleDO>();
            types.add(data);

            return data;
        }

        /**
         * Removes a type from the list
         */
        public void remove(int i) {
            TestTypeOfSampleDO data;

            data = types.remove(i);
            dataObjectRemove(data.getId(), data);
        }

        public void remove(TestTypeOfSampleDO data) {
            types.remove(data);
            dataObjectRemove(data.getId(), data);
        }

        /**
         * Returns the number of types associated with this test
         */
        public int count() {
            if (types != null)
                return types.size();
            return 0;
        }
    }

    /**
     * Class to manage Test Analyte information
     */
    public class TestAnalyte {
        /**
         * Returns the analyte at specified index.
         */
        public TestAnalyteViewDO get(int i) {
            return analytes.get(i);
        }

        public TestAnalyteViewDO add() {
            TestAnalyteViewDO data;

            data = new TestAnalyteViewDO();
            if (analytes == null)
                analytes = new ArrayList<TestAnalyteViewDO>();
            analytes.add(data);

            return data;
        }

        /**
         * Removes an analyte from the list
         */
        public void remove(int i) {
            TestAnalyteViewDO data;

            data = analytes.remove(i);
            dataObjectRemove(data.getId(), data);
        }

        public void remove(TestAnalyteViewDO data) {
            analytes.remove(data);
            dataObjectRemove(data.getId(), data);
        }

        /**
         * Returns the number of analytes associated with this test
         */
        public int count() {
            if (analytes != null)
                return analytes.size();
            return 0;
        }
    }

    /**
     * Class to manage Test Result information
     */
    public class TestResult {
        /**
         * Returns the result at specified index.
         */
        public TestResultViewDO get(int i) {
            return results.get(i);
        }

        public TestResultViewDO add() {
            TestResultViewDO data;

            data = new TestResultViewDO();
            if (results == null)
                results = new ArrayList<TestResultViewDO>();
            results.add(data);

            return data;
        }

        /**
         * Removes a result from the list
         */
        public void remove(int i) {
            TestResultViewDO data;

            data = results.remove(i);
            dataObjectRemove(data.getId(), data);
        }

        public void remove(TestResultViewDO data) {
            results.remove(data);
            dataObjectRemove(data.getId(), data);
        }

        /**
         * Returns the number of results associated with this test
         */
        public int count() {
            if (results != null)
                return results.size();
            return 0;
        }
    }

    /**
     * Class to manage Test Prep information
     */
    public class TestPrep {
        /**
         * Returns the prep at specified index.
         */
        public TestPrepViewDO get(int i) {
            return preps.get(i);
        }

        public TestPrepViewDO add() {
            TestPrepViewDO data;

            data = new TestPrepViewDO();
            if (preps == null)
                preps = new ArrayList<TestPrepViewDO>();
            preps.add(data);

            return data;
        }

        /**
         * Removes a prep from the list
         */
        public void remove(int i) {
            TestPrepViewDO data;

            data = preps.remove(i);
            dataObjectRemove(data.getId(), data);
        }

        public void remove(TestPrepViewDO data) {
            preps.remove(data);
            dataObjectRemove(data.getId(), data);
        }

        /**
         * Returns the number of preps associated with this test
         */
        public int count() {
            if (preps != null)
                return preps.size();
            return 0;
        }
    }

    /**
     * Class to manage Test Reflex information
     */
    public class TestReflex {
        /**
         * Returns the reflex at specified index.
         */
        public TestReflexViewDO get(int i) {
            return reflexes.get(i);
        }

        public TestReflexViewDO add() {
            TestReflexViewDO data;

            data = new TestReflexViewDO();
            if (reflexes == null)
                reflexes = new ArrayList<TestReflexViewDO>();
            reflexes.add(data);

            return data;
        }

        /**
         * Removes a reflex from the list
         */
        public void remove(int i) {
            TestReflexViewDO data;

            data = reflexes.remove(i);
            dataObjectRemove(data.getId(), data);
        }

        public void remove(TestReflexViewDO data) {
            reflexes.remove(data);
            dataObjectRemove(data.getId(), data);
        }

        /**
         * Returns the number of reflexes associated with this test
         */
        public int count() {
            if (reflexes != null)
                return reflexes.size();
            return 0;
        }
    }

    /**
     * Adds the specified data object to the list of objects that should be
     * removed from the database.
     */
    private void dataObjectRemove(Integer id, DataObject data) {
        if (removed == null)
            removed = new ArrayList<DataObject>();
        if (id != null)
            removed.add(data);
    }
}
