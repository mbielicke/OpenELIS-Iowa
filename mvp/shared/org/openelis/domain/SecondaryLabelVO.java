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

import org.openelis.ui.common.DataBaseUtil;

/**
 * This class is used to carry the data needed for printing a secondary label
 * for a test. Analysis id makes sure that if sample item information needs to
 * be printed on the label then the right sample item can be found even if the
 * test is repeated on the sample, which won't be possible with test id.
 */
public class SecondaryLabelVO implements Serializable {

    private static final long serialVersionUID = 1L;

    protected Integer         sampleId, analysisId;
    protected String          printer;

    public SecondaryLabelVO() {
    }

    public SecondaryLabelVO(Integer sampleId, Integer analysisId, String printer) {
        setSampleId(sampleId);
        setAnalysisId(analysisId);
    }

    public Integer getSampleId() {
        return sampleId;
    }

    public void setSampleId(Integer sampleId) {
        this.sampleId = sampleId;
    }

    public Integer getAnalysisId() {
        return analysisId;
    }

    public void setAnalysisId(Integer analysisId) {
        this.analysisId = analysisId;
    }

    public String getPrinter() {
        return printer;
    }

    public void setPrinter(String printer) {
        this.printer = DataBaseUtil.trim(printer);
    }
}