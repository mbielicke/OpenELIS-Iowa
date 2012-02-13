/** Exhibit A - UIRF Open-source Based Public Software License.
* 
* The contents of this file are subject to the UIRF Open-source Based
* Public Software License(the "License"); you may not use this file except
* in compliance with the License. You may obtain a copy of the License at
* openelis.uhl.uiowa.edu
* 
* Software distributed under the License is distributed on an "AS IS"
* basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
* License for the specific language governing rights and limitations
* under the License.
* 
* The Original Code is OpenELIS code.
* 
* The Initial Developer of the Original Code is The University of Iowa.
* Portions created by The University of Iowa are Copyright 2006-2008. All
* Rights Reserved.
* 
* Contributor(s): ______________________________________.
* 
* Alternatively, the contents of this file marked
* "Separately-Licensed" may be used under the terms of a UIRF Software
* license ("UIRF Software License"), in which case the provisions of a
* UIRF Software License are applicable instead of those above. 
*/
package org.openelis.domain;

import org.openelis.gwt.common.DataBaseUtil;
import org.openelis.gwt.common.RPC;

/**
 * This class is used in the queries executed in DataViewBean to fetch the data
 * used to populate the .xsl file with or without analysis results. The two
 * constructors allow each query to return different set of fields and to
 * avoid getting back lists of arrays of objects. 
 */

public class DataViewResultFetchVO implements RPC {

    private static final long serialVersionUID = 1L;

    protected Integer         sampleId, sampleAccessionNumber, sampleItemId, 
                              analysisId, analyteId, typeId, resultSortOrder,
                              resultTestAnalyteRowGroup;                              

    protected String          sampleDomain, analyteName, resultIsColumn,
                              value, auxDataAuxFieldValue;                              

    public DataViewResultFetchVO(Integer sampleAccessionNumber, String analyteName,
                           Integer sampleId, String sampleDomain, Integer sampleItemId,
                           Integer analysisId, String resultIsColumn, 
                           Integer analyteId, Integer typeId,
                           String value, Integer resultSortOrder,
                           Integer resultTestAnalyteRowGroup) {
        setSampleAccessionNumber(sampleAccessionNumber);
        setAnalyteName(analyteName);
        setSampleId(sampleId);
        setSampleDomain(sampleDomain);
        setSampleItemId(sampleItemId);
        setAnalysisId(analysisId);
        setResultIsColumn(resultIsColumn);
        setAnalyteId(analyteId);
        setTypeId(typeId);
        setValue(value);
        setResultSortOrder(resultSortOrder);
        setResultTestAnalyteRowGroup(resultTestAnalyteRowGroup);
    }    
    
    public DataViewResultFetchVO(Integer sampleAccessionNumber, Integer sampleId, 
                           String sampleDomain, Integer sampleItemId,
                           Integer analysisId) {
        setSampleAccessionNumber(sampleAccessionNumber);
        setSampleId(sampleId);
        setSampleDomain(sampleDomain);
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

    public Integer getAnalyteId() {
        return analyteId;
    }

    public void setAnalyteId(Integer analyteId) {
        this.analyteId = analyteId;
    }

    public Integer getResultSortOrder() {
        return resultSortOrder;
    }

    public void setResultSortOrder(Integer resultSortOrder) {
        this.resultSortOrder = resultSortOrder;
    }

    public Integer getResultTestAnalyteRowGroup() {
        return resultTestAnalyteRowGroup;
    }

    public void setResultTestAnalyteRowGroup(Integer resultTestAnalyteRowGroup) {
        this.resultTestAnalyteRowGroup = resultTestAnalyteRowGroup;
    }

    public Integer getTypeId() {
        return typeId;
    }

    public void setTypeId(Integer typeId) {
        this.typeId = typeId;
    }

    public String getSampleDomain() {
        return sampleDomain;
    }

    public void setSampleDomain(String sampleDomain) {
        this.sampleDomain = DataBaseUtil.trim(sampleDomain);
    }

    public String getAnalyteName() {
        return analyteName;
    }

    public void setAnalyteName(String analyteName) {
        this.analyteName =  DataBaseUtil.trim(analyteName);
    }

    public String getResultIsColumn() {
        return resultIsColumn;
    }

    public void setResultIsColumn(String resultIsColumn) {
        this.resultIsColumn =  DataBaseUtil.trim(resultIsColumn);
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value =  DataBaseUtil.trim(value);
    }    

    public String getAuxDataAuxFieldValue() {
        return auxDataAuxFieldValue;
    }

    public void setAuxDataAuxFieldValue(String auxDataAuxFieldValue) {
        this.auxDataAuxFieldValue =  DataBaseUtil.trim(auxDataAuxFieldValue);
    }       
}