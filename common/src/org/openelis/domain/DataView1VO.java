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
                    includeNotReportableResults, excludeAuxData, includeNotReportableAuxData;
    protected ArrayList<QueryData>             queryFields;
    protected ArrayList<String>                columns;
    protected ArrayList<DataViewAnalyteVO> testAnalytes, auxFields;
    
    public DataView1VO() {
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

    public String getIncludeNotReportableResults() {
        return includeNotReportableResults;
    }

    public void setIncludeNotReportableResults(String includeNotReportableResults) {
        this.includeNotReportableResults = DataBaseUtil.trim(includeNotReportableResults);
    }

    public String getExcludeAuxData() {
        return excludeAuxData;
    }

    public void setExcludeAuxData(String excludeAuxData) {
        this.excludeAuxData = DataBaseUtil.trim(excludeAuxData);
    }

    public String getIncludeNotReportableAuxData() {
        return includeNotReportableAuxData;
    }

    public void setIncludeNotReportableAuxData(String includeNotReportableAuxData) {
        this.includeNotReportableAuxData = DataBaseUtil.trim(includeNotReportableAuxData);
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

    public ArrayList<DataViewAnalyteVO> getTestAnalytes() {
        return testAnalytes;
    }

    public void setTestAnalytes(ArrayList<DataViewAnalyteVO> testAnalytes) {
        this.testAnalytes = testAnalytes;
    }

    public ArrayList<DataViewAnalyteVO> getAuxFields() {
        return auxFields;
    }

    public void setAuxFields(ArrayList<DataViewAnalyteVO> auxFields) {
        this.auxFields = auxFields;
    }
}