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
 * This class is used in the query to fetch the aux analytes and values shown to
 * the user, so that he/she can choose the analytes and values shown in the file
 * generated for data view
 */

public class DataViewAuxDataFetch1VO implements Serializable {

    private static final long serialVersionUID = 1L;

    protected Integer         sampleId, sampleAccessionNumber, auxFieldAnalyteId, typeId;

    protected String          auxFieldAnalyteName, value;

    public DataViewAuxDataFetch1VO(Integer sampleId, Integer sampleAccessionNumber, Integer auxFieldAnalyteId,
                                   String auxFieldAnalyteName, Integer typeId, String value) {
        setSampleId(sampleId);
        setSampleAccessionNumber(sampleAccessionNumber);
        setAuxFieldAnalyteId(auxFieldAnalyteId);
        setAuxFieldAnalyteName(auxFieldAnalyteName);
        setTypeId(typeId);
        setValue(value);
    }

    public Integer getSampleId() {
        return sampleId;
    }

    public void setSampleId(Integer sampleId) {
        this.sampleId = sampleId;
    }

    public Integer getSampleAccessionNumber() {
        return sampleAccessionNumber;
    }

    public void setSampleAccessionNumber(Integer sampleAccessionNumber) {
        this.sampleAccessionNumber = sampleAccessionNumber;
    }
    
    public Integer getAuxFieldAnalyteId() {
        return auxFieldAnalyteId;
    }

    public void setAuxFieldAnalyteId(Integer analyteId) {
        this.auxFieldAnalyteId = analyteId;
    }

    public String getAuxFieldAnalyteName() {
        return auxFieldAnalyteName;
    }

    public void setAuxFieldAnalyteName(String auxFieldAnalyteName) {
        this.auxFieldAnalyteName = auxFieldAnalyteName;
    }

    public Integer getTypeId() {
        return typeId;
    }

    public void setTypeId(Integer typeId) {
        this.typeId = typeId;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = DataBaseUtil.trim(value);
    }
}