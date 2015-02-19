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
package org.openelis.meta;

import java.util.Arrays;
import java.util.HashSet;

import org.openelis.ui.common.Meta;
import org.openelis.ui.common.MetaMap;

public class SampleViewMeta implements Meta, MetaMap {

    private static final String    ID = "_sampleView.sampleId", 
                    DOMAIN = "_sampleView.domain",
                    ACCESSION_NUMBER = "_sampleView.accessionNumber",
                    ACCESSION_NUMBER_FROM = "_display.accessionNumberFrom",
                    ACCESSION_NUMBER_TO = "_display.accessionNumberTo",
                    REVISION = "_sampleView.sampleRevision",
                    RECEIVED_DATE = "_sampleView.receivedDate",
                    COLLECTION_DATE = "_sampleView.collectionDate",
                    COLLECTION_DATE_FROM = "_display.collectionDateFrom",
                    COLLECTION_DATE_TO = "_display.collectionDateTo",
                    COLLECTION_TIME = "_sampleView.collectionTime",
                    STATUS_ID = "_sampleView.sampleStatusId",
                    CLIENT_REFERENCE = "_sampleView.clientReference",
                    RELEASED_DATE = "_sampleView.sampleReleasedDate",
                    RELEASED_DATE_FROM = "_display.sampleReleasedDateFrom",
                    RELEASED_DATE_TO = "_display.sampleReleasedDateTo",
                    REPORT_TO_ID = "_sampleView.reportToId",
                    REPORT_TO = "_sampleView.reportToName", 
                    COLLECTOR = "_sampleView.collector",
                    LOCATION = "_sampleView.location", 
                    LOCATION_CITY = "_sampleView.locationCity",
                    PROJECT_ID = "_sampleView.projectId",
                    PROJECT = "_sampleView.projectName", 
                    PWS_NUMBER0 = "_sampleView.pwsNumber0",
                    PWS_NAME = "_sampleView.pwsName",
                    SDWIS_FACILITY_ID = "_sampleView.sdwisFacilityId",
                    PATIENT_LAST_NAME = "_sampleView.patientLastName",
                    PATIENT_FIRST_NAME = "_sampleView.patientFirstName",
                    PATIENT_BIRTH_DATE = "_sampleView.patientBirthDate",
                    PATIENT_BIRTH_DATE_FROM = "_display.patientBirthDateFrom",
                    PATIENT_BIRTH_DATE_TO = "_display.patientBirthDateTo",
                    ANALYSIS_ID = "_sampleView.analysisId",
                    ANALYSIS_REVISION = "_sampleView.analysisRevision",
                    ANALYSIS_IS_REPORTABLE = "_sampleView.analysisIsReportable",
                    ANALYSIS_STATUS_ID = "_sampleView.analysisStatusId",
                    TEST_REPORTING_DESCRIPTION = "_sampleView.testReportingDescription",
                    METHOD_REPORTING_DESCRIPTION = "_sampleView.methodReportingDescription";

    private static HashSet<String> names;

    static {
        names = new HashSet<String>(Arrays.asList(ID,
                                                  DOMAIN,
                                                  ACCESSION_NUMBER,
                                                  ACCESSION_NUMBER_FROM,
                                                  ACCESSION_NUMBER_TO,
                                                  REVISION,
                                                  RECEIVED_DATE,
                                                  COLLECTION_DATE,
                                                  COLLECTION_DATE_FROM,
                                                  COLLECTION_DATE_TO,
                                                  COLLECTION_TIME,
                                                  STATUS_ID,
                                                  CLIENT_REFERENCE,
                                                  RELEASED_DATE,
                                                  RELEASED_DATE_FROM,
                                                  RELEASED_DATE_TO,
                                                  REPORT_TO_ID,
                                                  REPORT_TO,
                                                  COLLECTOR,
                                                  LOCATION,
                                                  LOCATION_CITY,
                                                  PROJECT_ID,
                                                  PROJECT,
                                                  PWS_NUMBER0,
                                                  PWS_NAME,
                                                  SDWIS_FACILITY_ID,
                                                  PATIENT_LAST_NAME,
                                                  PATIENT_FIRST_NAME,
                                                  PATIENT_BIRTH_DATE,
                                                  PATIENT_BIRTH_DATE_FROM,
                                                  PATIENT_BIRTH_DATE_TO,
                                                  ANALYSIS_ID,
                                                  ANALYSIS_REVISION,
                                                  ANALYSIS_IS_REPORTABLE,
                                                  ANALYSIS_STATUS_ID,
                                                  TEST_REPORTING_DESCRIPTION,
                                                  METHOD_REPORTING_DESCRIPTION));
    }

    public static String getId() {
        return ID;
    }

    public static String getDomain() {
        return DOMAIN;
    }

    public static String getAccessionNumber() {
        return ACCESSION_NUMBER;
    }

    public static String getAccessionNumberFrom() {
        return ACCESSION_NUMBER_FROM;
    }

    public static String getAccessionNumberTo() {
        return ACCESSION_NUMBER_TO;
    }

    public static String getRevision() {
        return REVISION;
    }

    public static String getReceivedDate() {
        return RECEIVED_DATE;
    }

    public static String getCollectionDate() {
        return COLLECTION_DATE;
    }

    public static String getCollectionDateFrom() {
        return COLLECTION_DATE_FROM;
    }

    public static String getCollectionDateTo() {
        return COLLECTION_DATE_TO;
    }

    public static String getCollectionTime() {
        return COLLECTION_TIME;
    }

    public static String getStatusId() {
        return STATUS_ID;
    }

    public static String getClientReference() {
        return CLIENT_REFERENCE;
    }

    public static String getReleasedDate() {
        return RELEASED_DATE;
    }

    public static String getReleasedDateFrom() {
        return RELEASED_DATE_FROM;
    }

    public static String getReleasedDateTo() {
        return RELEASED_DATE_TO;
    }

    public static String getReportToId() {
        return REPORT_TO_ID;
    }

    public static String getReportTo() {
        return REPORT_TO;
    }

    public static String getCollector() {
        return COLLECTOR;
    }

    public static String getLocation() {
        return LOCATION;
    }

    public static String getLocationCity() {
        return LOCATION_CITY;
    }

    public static String getProjectId() {
        return PROJECT_ID;
    }
    
    public static String getProject() {
        return PROJECT;
    }

    public static String getPwsNumber0() {
        return PWS_NUMBER0;
    }

    public static String getPwsName() {
        return PWS_NAME;
    }

    public static String getSdwisFacilityId() {
        return SDWIS_FACILITY_ID;
    }

    public static String getPatientLastName() {
        return PATIENT_LAST_NAME;
    }

    public static String getPatientFirstName() {
        return PATIENT_FIRST_NAME;
    }

    public static String getPatientBirthDate() {
        return PATIENT_BIRTH_DATE;
    }

    public static String getPatientBirthDateFrom() {
        return PATIENT_BIRTH_DATE_FROM;
    }

    public static String getPatientBirthDateTo() {
        return PATIENT_BIRTH_DATE_TO;
    }

    public static String getAnalysisId() {
        return ANALYSIS_ID;
    }

    public static String getAnalysisRevision() {
        return ANALYSIS_REVISION;
    }
    
    public static String getAnalysisIsReportable() {
        return ANALYSIS_IS_REPORTABLE;
    }

    public static String getAnalysisStatusId() {
        return ANALYSIS_STATUS_ID;
    }

    public static String getTestReportingDescription() {
        return TEST_REPORTING_DESCRIPTION;
    }

    public static String getMethodReportingDescription() {
        return METHOD_REPORTING_DESCRIPTION;
    }

    public static HashSet<String> getNames() {
        return names;
    }

    public boolean hasColumn(String columnName) {
        return names.contains(columnName);
    }

    public String buildFrom(String where) {
        String from = "SampleView _sampleView ";

        return from;
    }
}
