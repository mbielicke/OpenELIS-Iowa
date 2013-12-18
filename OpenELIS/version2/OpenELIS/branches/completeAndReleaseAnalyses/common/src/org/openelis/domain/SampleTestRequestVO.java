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

/**
 * This class is used for transporting data needed for choosing or adding test
 * to sample.
 */

public class SampleTestRequestVO implements Serializable {

    private static final long    serialVersionUID = 1L;

    /*
     * id of the sample item to which the test should be added
     */
    protected Integer            sampleItemId;

    /*
     * id of the test that could/should be added. In case of prep test/reflex
     * test, the bean provides this test to be chosen by the user.
     */
    protected Integer            testId;

    /*
     * if this id is not null then the analysis prep/reflex id is set to be the
     * newly added test
     */
    protected Integer            analysisId;

    /*
     * if this id is not null then it will be the section id of the added test
     */
    protected Integer            sectionId;

    /*
     * this id identifies the result that triggered the request for a reflex
     * test
     */
    protected Integer            resultId;

    /*
     * if testId is null, then the tests from the panel with this id are added
     * to the sample; otherwise, this id specifies the panelId for all analyses
     * that are being added
     */
    protected Integer            panelId;

    /*
     * this flag if true, forces the requested test to be added even if it's
     * present in the sample
     */
    protected boolean            allowDuplicate;

    /*
     * if this list is not null then in the results for this analysis, only the
     * row analytes from the list are marked reportable
     */
    protected ArrayList<Integer> reportableAnalytes;

    protected SampleTestRequestVO() {
    }

    public SampleTestRequestVO(Integer sampleItemId, Integer testId, Integer analysisId,
                               Integer sectionId, Integer resultId, Integer panelId,
                               boolean allowDuplicate, ArrayList<Integer> reportableAnalytes) {
        this.sampleItemId = sampleItemId;
        this.testId = testId;
        this.analysisId = analysisId;
        this.sectionId = sectionId;
        this.resultId = resultId;
        this.panelId = panelId;
        this.allowDuplicate = allowDuplicate;
        this.reportableAnalytes = reportableAnalytes;
    }

    /**
     * returns the id of the sample item to which the test should be added
     */
    public Integer getSampleItemId() {
        return sampleItemId;
    }

    /**
     * returns the id of the test that could/should be added. In case of prep
     * test/reflex test, the bean provides this test to be chosen by the user.
     */
    public Integer getTestId() {
        return testId;
    }

    /**
     * returns the analysisId. If this id is not null then the analysis
     * prep/reflex id is set to be the newly added test
     */
    public Integer getAnalysisId() {
        return analysisId;
    }

    /**
     * returns the sectionId. if this id is not null then it will be the section
     * id of the added test
     */
    public Integer getSectionId() {
        return sectionId;
    }
    
    public void setSectionId(Integer sectionId) {
        this.sectionId = sectionId;
    }

    /**
     * returns the id identifying the result that triggered the request for a
     * reflex test
     */
    public Integer getResultId() {
        return resultId;
    }

    /**
     * returns the panelId. If testId is null, then the tests from the panel
     * with this id are added to the sample; otherwise, this id specifies the
     * panelId for all analyses that are being added
     */
    public Integer getPanelId() {
        return panelId;
    }

    /**
     * returns the flag used to force adding the requested test even if it's
     * present in the sample
     */
    public boolean getAllowDuplicate() {
        return allowDuplicate;
    }

    /**
     * return the list that determines which row analytes in the results for
     * this analysis should be marked reportable
     */
    public ArrayList<Integer> getReportableAnalytes() {
        return reportableAnalytes;
    }
}