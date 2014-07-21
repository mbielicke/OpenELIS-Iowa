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
 * Data object for sample QC data. Each analysis ID needs a corresponding list
 * of QC analyte IDs at the same index in the array lists
 */
public class SampleQcVO implements Serializable {

    private static final long                               serialVersionUID = 1L;

    protected Integer                                       accession;

    protected ArrayList<Integer>                            analysisIds;

    protected ArrayList<ArrayList<Integer>>                 qcAnalyteIds;

    protected ArrayList<ArrayList<WorksheetQcResultViewVO>> qcAnalytes;

    public Integer getAccession() {
        return accession;
    }

    public void setAccession(Integer accession) {
        this.accession = accession;
    }

    public ArrayList<Integer> getAnalysisIds() {
        return analysisIds;
    }

    public void setAnalysisIds(ArrayList<Integer> analysisIds) {
        this.analysisIds = analysisIds;
    }

    public ArrayList<ArrayList<Integer>> getQcAnalyteIds() {
        return qcAnalyteIds;
    }

    public void setQcAnalyteIds(ArrayList<ArrayList<Integer>> qcAnalyteIds) {
        this.qcAnalyteIds = qcAnalyteIds;
    }

    public ArrayList<ArrayList<WorksheetQcResultViewVO>> getQcAnalytes() {
        return qcAnalytes;
    }

    public void setQcAnalytes(ArrayList<ArrayList<WorksheetQcResultViewVO>> qcAnalytes) {
        this.qcAnalytes = qcAnalytes;
    }

    public void addQcAnalytes(ArrayList<WorksheetQcResultViewVO> qcAnalytes) {
        if (this.qcAnalytes == null)
            this.qcAnalytes = new ArrayList<ArrayList<WorksheetQcResultViewVO>>();
        this.qcAnalytes.add(qcAnalytes);
    }

    public void add(Integer analysisId, ArrayList<Integer> qcAnalyteIds) {
        analysisIds.add(analysisId);
        this.qcAnalyteIds.add(qcAnalyteIds);
    }

    public void clear() {
        analysisIds.clear();
        qcAnalyteIds.clear();
    }
}
