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

import java.util.ArrayList;

import org.openelis.gwt.common.DataBaseUtil;
import org.openelis.gwt.common.RPC;

/**
 * This class is used for managing the analytes and results that will be part of
 * the spreadsheet delivered as a result of executing a data dump
 */
public class TestAnalyteDataDumpVO implements RPC {

    private static final long             serialVersionUID = 1L;

    protected Integer                     testAnalyteId, analyteId;
    protected String                      analyteName;   
    protected ArrayList<ResultDataDumpVO> results;
    protected String                      isIncluded;

    public Integer getTestAnalyteId() {
        return testAnalyteId;
    }

    public void setTestAnalyteId(Integer testAnalyteId) {
        this.testAnalyteId = testAnalyteId;
    }

    public String getAnalyteName() {
        return analyteName;
    }

    public void setAnalyteName(String analyteName) {
        this.analyteName = DataBaseUtil.trim(analyteName);
    }
    
    public Integer getAnalyteId() {
        return analyteId;
    }

    public void setAnalyteId(Integer analyteId) {
        this.analyteId = analyteId;
    }
    
    public String getIsIncluded() {
        return isIncluded;
    }

    public void setIsIncluded(String isIncluded) {
        this.isIncluded = DataBaseUtil.trim(isIncluded);
    }

    public ArrayList<ResultDataDumpVO> getResults() {
        return results;
    }

    public void setResults(ArrayList<ResultDataDumpVO> results) {
        this.results = results;
    }
}