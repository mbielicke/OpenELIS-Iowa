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
package org.openelis.portal.modules.finalReport.client;

import org.openelis.ui.common.DataBaseUtil;
import org.openelis.ui.common.Datetime;

public class FinalReportFormVO {

    private Datetime collectedFrom, collectedTo, releasedFrom, releasedTo, patientBirthFrom,
                    patientBirthTo;
    private String   clientReference, envCollector, sdwisCollector, pwsId, patientFirst,
                    patientLast;
    private Integer  accessionFrom, accessionTo, projectCode;

    public Datetime getCollectedFrom() {
        return collectedFrom;
    }

    public void setCollectedFrom(Datetime collectedFrom) {
        this.collectedFrom = DataBaseUtil.toYD(collectedFrom);
    }

    public Datetime getCollectedTo() {
        return collectedTo;
    }

    public void setCollectedTo(Datetime collectedTo) {
        this.collectedTo = DataBaseUtil.toYD(collectedTo);
    }

    public Datetime getReleasedFrom() {
        return releasedFrom;
    }

    public void setReleasedFrom(Datetime releasedFrom) {
        this.releasedFrom = DataBaseUtil.toYD(releasedFrom);
    }

    public Datetime getReleasedTo() {
        return releasedTo;
    }

    public void setReleasedTo(Datetime releasedTo) {
        this.releasedTo = DataBaseUtil.toYD(releasedTo);
    }

    public Datetime getPatientBirthFrom() {
        return patientBirthFrom;
    }

    public void setPatientBirthFrom(Datetime patientBirthFrom) {
        this.patientBirthFrom = DataBaseUtil.toYD(patientBirthFrom);
    }

    public Datetime getPatientBirthTo() {
        return patientBirthTo;
    }

    public void setPatientBirthTo(Datetime patientBirthTo) {
        this.patientBirthTo = DataBaseUtil.toYD(patientBirthTo);
    }

    public String getClientReference() {
        return clientReference;
    }

    public void setClientReference(String clientReference) {
        this.clientReference = DataBaseUtil.trim(clientReference);
    }

    public String getEnvCollector() {
        return envCollector;
    }

    public void setEnvCollector(String envCollector) {
        this.envCollector = DataBaseUtil.trim(envCollector);
    }

    public String getSdwisCollector() {
        return sdwisCollector;
    }

    public void setSdwisCollector(String sdwisCollector) {
        this.sdwisCollector = DataBaseUtil.trim(sdwisCollector);
    }

    public String getPwsId() {
        return pwsId;
    }

    public void setPwsId(String pwsId) {
        this.pwsId = DataBaseUtil.trim(pwsId);
    }

    public String getPatientFirst() {
        return patientFirst;
    }

    public void setPatientFirst(String patientFirst) {
        this.patientFirst = DataBaseUtil.trim(patientFirst);
    }

    public String getPatientLast() {
        return patientLast;
    }

    public void setPatientLast(String patientLast) {
        this.patientLast = DataBaseUtil.trim(patientLast);
    }

    public Integer getAccessionFrom() {
        return accessionFrom;
    }

    public void setAccessionFrom(Integer accessionFrom) {
        this.accessionFrom = accessionFrom;
    }

    public Integer getAccessionTo() {
        return accessionTo;
    }

    public void setAccessionTo(Integer accessionTo) {
        this.accessionTo = accessionTo;
    }

    public Integer getProjectCode() {
        return projectCode;
    }

    public void setProjectCode(Integer projectCode) {
        this.projectCode = projectCode;
    }
}