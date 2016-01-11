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
 * This class is used to carry the data returned by the query executed on
 * "Second Data Entry" screen; the query returns the accession numbers and
 * domains of not verified and not quick-entry samples along with the users who
 * made any changes to the samples, according to the history; HistoryVO is not
 * used because to get the accession number, its "changes" will have to be
 * parsed and the accession number may not be one of the changed fields
 */
public class SecondDataEntryVO implements Serializable {

    private static final long serialVersionUID = 1L;

    protected Integer         sampleId, sampleAccessionNumber;
    protected String          sampleDomain, historySystemUserLoginName;

    public SecondDataEntryVO() {
    }

    public SecondDataEntryVO(Integer sampleId, Integer sampleAccessionNumber, String sampleDomain,
                             String historysystemUserLoginName) {
        setSampleId(sampleId);
        setSampleAccessionNumber(sampleAccessionNumber);
        setSampleDomain(sampleDomain);
        setHistorySystemUserLoginName(historysystemUserLoginName);
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

    public String getSampleDomain() {
        return sampleDomain;
    }

    public void setSampleDomain(String sampleDomain) {
        this.sampleDomain = DataBaseUtil.trim(sampleDomain);
    }

    public String getHistorysystemUserLoginName() {
        return historySystemUserLoginName;
    }

    public void setHistorySystemUserLoginName(String historySystemUserLoginName) {
        this.historySystemUserLoginName = DataBaseUtil.trim(historySystemUserLoginName);
    }
}