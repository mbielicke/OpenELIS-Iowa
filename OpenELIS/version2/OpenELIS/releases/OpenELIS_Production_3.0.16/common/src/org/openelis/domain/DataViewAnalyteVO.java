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

/**
 * This class contains the data for an analyte and a list of result or aux data
 * values for it. They are shown to the user so that one or more of them can be
 * selected to be included in the data view report.
 */
public class DataViewAnalyteVO implements Serializable {

    private static final long             serialVersionUID = 1L;

    protected Integer                     analyteId;
    protected String                      analyteName;
    protected ArrayList<DataViewValueVO> values;
    protected String                      isIncluded;

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

    public ArrayList<DataViewValueVO> getValues() {
        return values;
    }

    public void setValues(ArrayList<DataViewValueVO> values) {
        this.values = values;
    }
}