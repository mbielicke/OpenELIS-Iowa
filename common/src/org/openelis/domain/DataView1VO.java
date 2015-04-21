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

import org.openelis.ui.common.DataBaseUtil;
import org.openelis.ui.common.data.QueryData;

/**
 * 
 * This class is used to carry the data entered on Data View screen to the
 * back-end and also between different screens
 */
public class DataView1VO implements Serializable {

    private static final long                  serialVersionUID = 1L;

    protected String                           excludeResultOverride, excludeResults,
                    includeOnlyReportableResults, excludeAuxData, includeOnlyReportableAuxData;
    protected ArrayList<QueryData>             queryFields;
    protected ArrayList<String>                columns;
    protected ArrayList<TestAnalyteDataViewVO> testAnalytes;
    protected ArrayList<AuxFieldDataViewVO>    auxFields;
    
    public DataView1VO() {
        excludeResultOverride = "N";
        excludeResults = "N";
        includeOnlyReportableResults = "Y";
        excludeAuxData = "N";
        includeOnlyReportableAuxData = "Y";        
    }

    public String getExcludeResultOverride() {
        return excludeResultOverride;
    }

    public void setExcludeResultOverride(String excludeResultOverride) {
        this.excludeResultOverride = DataBaseUtil.trim(excludeResultOverride);
    }

    public String getExcludeResults() {
        return excludeResults;
    }

    public void setExcludeResults(String excludeResults) {
        this.excludeResults = DataBaseUtil.trim(excludeResults);
    }

    public String getIncludeOnlyReportableResults() {
        return includeOnlyReportableResults;
    }

    public void setIncludeOnlyReportableResults(String includeOnlyReportableResults) {
        this.includeOnlyReportableResults = DataBaseUtil.trim(includeOnlyReportableResults);
    }

    public String getExcludeAuxData() {
        return excludeAuxData;
    }

    public void setExcludeAuxData(String excludeAuxData) {
        this.excludeAuxData = DataBaseUtil.trim(excludeAuxData);
    }

    public String getIncludeOnlyReportableAuxData() {
        return includeOnlyReportableAuxData;
    }

    public void setIncludeOnlyReportableAuxData(String includeOnlyReportableAuxData) {
        this.includeOnlyReportableAuxData = DataBaseUtil.trim(includeOnlyReportableAuxData);
    }

    public ArrayList<QueryData> getQueryFields() {
        return queryFields;
    }

    public void setQueryFields(ArrayList<QueryData> queryFields) {
        this.queryFields = queryFields;
    }

    public ArrayList<String> getColumns() {
        return columns;
    }

    public void setColumns(ArrayList<String> columns) {
        this.columns = columns;
    }

    public ArrayList<TestAnalyteDataViewVO> getTestAnalytes() {
        return testAnalytes;
    }

    public void setTestAnalytes(ArrayList<TestAnalyteDataViewVO> testAnalytes) {
        this.testAnalytes = testAnalytes;
    }

    public ArrayList<AuxFieldDataViewVO> getAuxFields() {
        return auxFields;
    }

    public void setAuxFields(ArrayList<AuxFieldDataViewVO> auxFields) {
        this.auxFields = auxFields;
    }
}