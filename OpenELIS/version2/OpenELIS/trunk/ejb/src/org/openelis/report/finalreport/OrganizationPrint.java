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
package org.openelis.report.finalreport;


/**
 * Class to manage a jasper print object for given organization.
 */
public class OrganizationPrint {

	private Integer organizationId, sampleId, pageCount;
    private String organizationName, faxNumber, fromCompany, toCompany, faxNote, faxAttention;
	
	public OrganizationPrint() {
    }
	
	public OrganizationPrint(Integer organizationId, String organizationName, String faxAttention, Integer sampleId) {
	    setOrganizationId(organizationId);
	    setOrganizationName(organizationName);
	    setFaxAttention(faxAttention); 
	    setSampleId(sampleId);
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

	public String setOrganizationName(String organizationName) {
	    return this.organizationName = organizationName;
	}

	public Integer getSampleId() {
		return sampleId;
	}

    public void setSampleId(Integer sampleId) {
        this.sampleId = sampleId;
    }

    public Integer getPageCount() {
        return pageCount;
    }

    public void setPageCount(Integer pageCount) {
        this.pageCount = pageCount;
    }
    
	public String getFaxNumber() {
        return faxNumber;
    }

    public void setFaxNumber(String faxNumber) {
        this.faxNumber = faxNumber;
    }
    
    public String getFromCompany() {
        return fromCompany;
    }

    public void setFromCompany(String fromCompany) {
        this.fromCompany = fromCompany;
    }
    
    public String getToCompany() {
        return toCompany;
    }

    public void setToCompany(String toCompany) {
        this.toCompany = toCompany;
    }

    public String getFaxNote() {
        return faxNote;
    }

    public void setFaxNote(String faxNote) {
        this.faxNote = faxNote;
    }    

    public String getFaxAttention() {
        return faxAttention;
    }

    public void setFaxAttention(String faxAttention) {
        this.faxAttention = faxAttention;
    }    
}