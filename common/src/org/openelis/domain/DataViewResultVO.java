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
 * This class is used in the queries for data view report. The data fetched
 * could then be used to either show analytes and values to the user or to
 * generate the output file.
 */
public class DataViewResultVO implements Serializable {

    private static final long serialVersionUID = 1L;

    protected Integer         sampleId, sampleAccessionNumber, sampleItemId, analysisId, id,
                    analyteId, typeId;

    protected String          analyteName, value;

    public DataViewResultVO(Integer sampleId, Integer sampleAccessionNumber,
                                  Integer sampleItemId, Integer analysisId, Integer id,
                                  Integer analyteId, String analysisName, Integer typeId,
                                  String value) {
        setSampleId(sampleId);
        setSampleAccessionNumber(sampleAccessionNumber);
        setSampleItemId(sampleItemId);
        setAnalysisId(analysisId);
        setId(id);
        setAnalyteId(analyteId);
        setAnalyteName(analysisName);
        setTypeId(typeId);
        setValue(value);
    }
    
    public DataViewResultVO(Integer sampleId, Integer sampleAccessionNumber,
                                  Integer analyteId, String analysisName, Integer typeId,
                                  String value) {
        setSampleId(sampleId);
        setSampleAccessionNumber(sampleAccessionNumber);
        setAnalyteId(analyteId);
        setAnalyteName(analysisName);
        setTypeId(typeId);
        setValue(value);
    }

    public DataViewResultVO(Integer sampleId, Integer sampleAccessionNumber,
                                  Integer sampleItemId, Integer analysisId) {
        setSampleId(sampleId);
        setSampleAccessionNumber(sampleAccessionNumber);
        setSampleItemId(sampleItemId);
        setAnalysisId(analysisId);
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

    public Integer getSampleItemId() {
        return sampleItemId;
    }

    public void setSampleItemId(Integer sampleItemId) {
        this.sampleItemId = sampleItemId;
    }

    public Integer getAnalysisId() {
        return analysisId;
    }

    public void setAnalysisId(Integer analysisId) {
        this.analysisId = analysisId;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getAnalyteId() {
        return analyteId;
    }

    public void setAnalyteId(Integer analyteId) {
        this.analyteId = analyteId;
    }

    public String getAnalyteName() {
        return analyteName;
    }

    public void setAnalyteName(String analyteName) {
        this.analyteName = DataBaseUtil.trim(analyteName);
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