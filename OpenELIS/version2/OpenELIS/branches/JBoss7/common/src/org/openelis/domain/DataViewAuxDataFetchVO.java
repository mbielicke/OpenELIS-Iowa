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

import java.io.Serializable;

import org.openelis.gwt.common.DataBaseUtil;

/**
 * This class is used in the queries executed in DataViewBean to fetch the data
 * used to populate the .xsl file with aux data. This allows the query to return
 * only the fields required to show aux data in the file and no dummy values need
 * to passed to a constructor of a more generic class with more fields. 
 */

public class DataViewAuxDataFetchVO implements Serializable {

    private static final long serialVersionUID = 1L;

    protected Integer         sampleId, sampleAccessionNumber, auxFieldAnalyteId,
                              typeId;                              

    protected String          sampleDomain, auxFieldAnalyteName, value;                              

    public DataViewAuxDataFetchVO(Integer sampleAccessionNumber, String auxFieldAnalyteName,
                                  Integer sampleId, String sampleDomain,
                                  Integer auxFieldAnalyteId, Integer typeId,
                                  String value) {
        setSampleAccessionNumber(sampleAccessionNumber);
        setAuxFieldAnalyteName(auxFieldAnalyteName);
        setSampleId(sampleId);
        setSampleDomain(sampleDomain);
        setAuxFieldAnalyteId(auxFieldAnalyteId);
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

    public Integer getAnalyteId() {
        return auxFieldAnalyteId;
    }

    public void setAuxFieldAnalyteId(Integer analyteId) {
        this.auxFieldAnalyteId = analyteId;
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
        return auxFieldAnalyteName;
    }

    public void setAuxFieldAnalyteName(String analyteName) {
        this.auxFieldAnalyteName =  DataBaseUtil.trim(analyteName);
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value =  DataBaseUtil.trim(value);
    }        
}