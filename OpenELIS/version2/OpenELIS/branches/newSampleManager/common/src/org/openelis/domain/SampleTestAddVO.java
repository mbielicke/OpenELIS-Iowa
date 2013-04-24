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
package org.openelis.domain;

import java.io.Serializable;
import java.util.ArrayList;

import org.openelis.manager.SampleManager1;
import org.openelis.ui.common.ValidationErrorsList;

/**
 * This class is used for transporting the data needed for adding tests to a
 * SampleManager and any errors or warnings resulting from trying to do so
 */

public class SampleTestAddVO implements Serializable {

    private static final long      serialVersionUID = 1L;

    protected SampleManager1       manager;

    protected ValidationErrorsList errors;

    protected ArrayList<AddTest>   tests;

    public SampleManager1 getManager() {
        return manager;
    }

    public void setManager(SampleManager1 manager) {
        this.manager = manager;
    }

    public ValidationErrorsList getErrors() {
        return errors;
    }

    public void setErrors(ValidationErrorsList errors) {
        this.errors = errors;
    }

    public void addTest(Integer sampleItemId, Integer testId, Integer prepTestId, boolean isPrep) {
        if (tests == null)
            tests = new ArrayList<AddTest>();

        tests.add(new AddTest(sampleItemId, testId, prepTestId, isPrep));
    }

    public ArrayList<AddTest> getTests() {
        return tests;
    }

    public class AddTest implements Serializable {

        private static final long serialVersionUID = 1L;

        protected boolean         isPrep;

        protected Integer         testId, prepTestId, sampleItemId;

        protected AddTest(Integer sampleItemId, Integer testId, Integer prepTestId, boolean isPrep) {
            this.sampleItemId = sampleItemId;
            this.testId = testId;
            this.prepTestId = prepTestId;
            this.isPrep = isPrep;
        }

        public Integer getSampleItemId() {
            return sampleItemId;
        }

        public Integer getTestId() {
            return testId;
        }

        public Integer getPrepTestId() {
            return prepTestId;
        }

        public boolean isPrep() {
            return isPrep;
        }
    }
}