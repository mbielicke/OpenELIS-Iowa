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
 * The class carries the data, e.g. the ids of the samples, analyses etc, used to
 * run Final Report for different domains. The fields are considered read/display
 * and do not get committed to the database.
 */

public class FinalReportVO implements RPC {

    private static final long serialVersionUID = 1L;

    protected Integer         sampleId, accessionNumber, revision, organizationId,
                              analysisId, organizationTypeId;
    protected String          domain, organizationName, organizationAttention;
    
    public FinalReportVO() {
    }
    
    public FinalReportVO(Integer sampleId, Integer accessionNumber, Integer revision,
                         String domain, Integer organizationId, Integer organizationTypeId,
                         String organizationName, String organizationAttention, Integer analysisId) {
        setSampleId(sampleId);
        setAccessionNumber(accessionNumber);
        setRevision(revision);
        setDomain(domain);
        setOrganizationId(organizationId);
        setOrganizationTypeId(organizationTypeId);
        setOrganizationName(organizationName);
        setOrganizationAttention(organizationAttention);
        setAnalysisId(analysisId);
    }
    
    public Integer getSampleId() {
        return sampleId;
    }

    public void setSampleId(Integer sampleId) {
        this.sampleId = sampleId;
    }

    public Integer getAccessionNumber() {
        return accessionNumber;
    }

    public void setAccessionNumber(Integer accessionNumber) {
        this.accessionNumber = accessionNumber;
    }

    public Integer getRevision() {
        return revision;
    }

    public void setRevision(Integer revision) {
        this.revision = revision;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = DataBaseUtil.trim(domain);
    }

    public Integer getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(Integer organizationId) {
        this.organizationId = organizationId;
    }

    public String getOrganizationName() {
        return organizationName;
    }

    public void setOrganizationName(String organizationName) {
        this.organizationName = DataBaseUtil.trim(organizationName);
    }
    
    public String getOrganizationAttention() {
        return organizationAttention;
    }

    public void setOrganizationAttention(String organizationAttention) {
        this.organizationAttention = DataBaseUtil.trim(organizationAttention);
    }

    public Integer getAnalysisId() {
        return analysisId;
    }

    public void setAnalysisId(Integer analysisId) {
        this.analysisId = analysisId;
    }

    public Integer getOrganizationTypeId() {
        return organizationTypeId;
    }

    public void setOrganizationTypeId(Integer organizationTypeId) {
        this.organizationTypeId = organizationTypeId;
    }
}