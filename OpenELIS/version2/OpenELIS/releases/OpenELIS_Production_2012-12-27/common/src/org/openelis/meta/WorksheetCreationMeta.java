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

/**
 * Worksheet Creation META Data
 */

import java.util.Arrays;
import java.util.HashSet;

import org.openelis.gwt.common.Meta;
import org.openelis.gwt.common.MetaMap;

public class WorksheetCreationMeta implements Meta, MetaMap {
    private static final String WSHT_ID  = "_worksheet.id",
                                WSHT_RELATED_WORKSHEET_ID = "_worksheet.relatedWorksheetId",
    
                                WSHT_ITEM_POSITION = "_worksheetItem.position",
                                
                                WSHT_ANA_WORKSHEET_ANALYSIS_ID = "_worksheetAnalysis.worksheetAnalysisId",

                                SAMP_ID = "_sample.id",
                                SAMP_DOMAIN = "_sample.domain",
                                SAMP_ACCESSION_NUMBER = "_sample.accessionNumber",
                                SAMP_COLLECTION_DATE = "_sample.collectionDate",
                                SAMP_COLLECTION_TIME = "_sample.collectionTime",
                                SAMP_RECEIVED_DATE = "_sample.receivedDate",
                                SAMP_ENTERED_DATE = "_sample.enteredDate",
                                
                                SAMP_ENV_LOCATION = "_sampleEnvironmental.location",
                                SAMP_ENV_PRIORITY = "_sampleEnvironmental.priority",
                                
                                SAMP_SDWIS_LOCATION = "_sampleSDWIS.location",

                                SAMP_PRIVATE_WELL_LOCATION = "_samplePrivateWell.location",
                                SAMP_PRIVATE_WELL_ORGANIZATION_ID = "_samplePrivateWell.organizationId",
                                SAMP_PRIVATE_WELL_REPORT_TO_NAME = "_samplePrivateWell.reportToName",

//                                  PATIENT_LAST_NAME = "_patient.lastName",
//                                  PATIENT_FIRST_NAME = "_patient.firstName",
                                
                                SAMP_ITEM_TYPE_OF_SAMPLE_ID = "_sampleItem.typeOfSampleId",
                                
                                ANA_ID = "_analysis.id",
                                ANA_TEST_ID = "_analysis.testId",
                                ANA_TEST_NAME = "_analysis.test.name",
                                ANA_TEST_METHOD_NAME = "_analysis.test.method.name",
                                ANA_TEST_TIME_HOLDING = "_analysis.test.timeHolding",
                                ANA_TEST_TIME_TA_AVERAGE = "_analysis.test.timeTaAverage",
                                ANA_SECTION_ID = "_analysis.sectionId",
                                ANA_PRE_ANALYSIS_ID = "_analysis.preAnalysisId",
                                ANA_STATUS_ID = "_analysis.statusId",
                                
                                TEST_WORKSHEET_FORMAT_ID = "_testWorksheet.formatId",

                                SAMP_DESCRIPTION = "_sample.description",   // combined field for all domain descriptions
                                ANA_DUE_DAYS = "_analysis.dueDays",
                                ANA_EXPIRE_DATE = "_analysis.expireDate";
                                

    private static HashSet<String> names;

    static {
        names = new HashSet<String>(Arrays.asList(WSHT_ID, WSHT_RELATED_WORKSHEET_ID,
                                                  WSHT_ITEM_POSITION, WSHT_ANA_WORKSHEET_ANALYSIS_ID, 
                                                  SAMP_ID, SAMP_DOMAIN, SAMP_ACCESSION_NUMBER,
                                                  SAMP_COLLECTION_DATE, SAMP_COLLECTION_TIME,
                                                  SAMP_RECEIVED_DATE, SAMP_ENTERED_DATE,
                                                  SAMP_ENV_LOCATION, SAMP_ENV_PRIORITY,
                                                  SAMP_SDWIS_LOCATION, SAMP_PRIVATE_WELL_LOCATION,
                                                  SAMP_PRIVATE_WELL_ORGANIZATION_ID,
                                                  SAMP_PRIVATE_WELL_REPORT_TO_NAME,
                                                  /*PATIENT_LAST_NAME, PATIENT_FIRST_NAME,*/
                                                  SAMP_ITEM_TYPE_OF_SAMPLE_ID,
                                                  ANA_ID, ANA_TEST_ID, ANA_TEST_NAME,
                                                  ANA_TEST_METHOD_NAME, ANA_TEST_TIME_HOLDING,
                                                  ANA_TEST_TIME_TA_AVERAGE, ANA_SECTION_ID,
                                                  ANA_PRE_ANALYSIS_ID, ANA_STATUS_ID,
                                                  TEST_WORKSHEET_FORMAT_ID, SAMP_DESCRIPTION,
                                                  ANA_DUE_DAYS, ANA_EXPIRE_DATE));
    }

    public static String getWorksheetId() {
        return WSHT_ID;
    }

    public static String getWorksheetRelatedWorksheetId() {
        return WSHT_RELATED_WORKSHEET_ID;
    }

    public static String getWorksheetItemPosition() {
        return WSHT_ITEM_POSITION;
    }

    public static String getWorksheetAnalysisWorksheetAnalysisId() {
        return WSHT_ANA_WORKSHEET_ANALYSIS_ID;
    }

    public static String getSampleId() {
        return SAMP_ID;
    }

    public static String getSampleDomain() {
        return SAMP_DOMAIN;
    }

    public static String getSampleAccessionNumber() {
        return SAMP_ACCESSION_NUMBER;
    }

    public static String getSampleCollectionDate() {
        return SAMP_COLLECTION_DATE;
    }

    public static String getSampleCollectionTime() {
        return SAMP_COLLECTION_TIME;
    }

    public static String getSampleReceivedDate() {
        return SAMP_RECEIVED_DATE;
    }

    public static String getSampleEnteredDate() {
        return SAMP_ENTERED_DATE;
    }

    public static String getSampleEnvironmentalLocation() {
        return SAMP_ENV_LOCATION;
    }

    public static String getSampleEnvironmentalPriority() {
        return SAMP_ENV_PRIORITY;
    }

    public static String getSampleSDWISLocation() {
        return SAMP_SDWIS_LOCATION;
    }

    public static String getSamplePrivateWellLocation() {
        return SAMP_PRIVATE_WELL_LOCATION;
    }

    public static String getSamplePrivateWellOrganizationId() {
        return SAMP_PRIVATE_WELL_ORGANIZATION_ID;
    }

    public static String getSamplePrivateWellReportToName() {
        return SAMP_PRIVATE_WELL_REPORT_TO_NAME;
    }

    /*
    public static String getPatientLastName() {
        return PATIENT_LAST_NAME;
    }

    public static String getPatientFirstName() {
        return PATIENT_FIRST_NAME;
    }
*/
    public static String getSampleItemTypeOfSampleId() {
        return SAMP_ITEM_TYPE_OF_SAMPLE_ID;
    }

    public static String getAnalysisId() {
        return ANA_ID;
    }

    public static String getAnalysisTestId() {
        return ANA_TEST_ID;
    }

    public static String getAnalysisTestName() {
        return ANA_TEST_NAME;
    }

    public static String getAnalysisTestMethodName() {
        return ANA_TEST_METHOD_NAME;
    }

    public static String getAnalysisTestTimeHolding() {
        return ANA_TEST_TIME_HOLDING;
    }

    public static String getAnalysisTestTimeTaAverage() {
        return ANA_TEST_TIME_TA_AVERAGE;
    }

    public static String getAnalysisSectionId() {
        return ANA_SECTION_ID;
    }

    public static String getAnalysisPreAnalysisId() {
        return ANA_PRE_ANALYSIS_ID;
    }

    public static String getAnalysisStatusId() {
        return ANA_STATUS_ID;
    }

    public static String getTestWorksheetFormatId() {
        return TEST_WORKSHEET_FORMAT_ID;
    }

    public static String getSampleDescription() {
        return SAMP_DESCRIPTION;
    }

    public static String getAnalysisDueDays() {
        return ANA_DUE_DAYS;
    }

    public static String getAnalysisExpireDate() {
        return ANA_EXPIRE_DATE;
    }

    public boolean hasColumn(String columnName) {
        return names.contains(columnName);
    }

    public String buildFrom(String where) {
        String from;
        
        from = "Sample _sample "+
               " LEFT JOIN _sample.sampleEnvironmental _sampleEnvironmental "+
               " LEFT JOIN _sample.samplePrivateWell _samplePrivateWell "+
               " LEFT JOIN _sample.sampleSDWIS _sampleSDWIS "+
//               " LEFT JOIN _sample.sampleHuman _sampleHuman "+
//               ", IN (_sampleHuman.patient) _patient "+
               ", IN (_sample.sampleItem) _sampleItem "+
               ", IN (_sampleItem.analysis) _analysis "+
               " LEFT JOIN _analysis.test.testWorksheet _testWorksheet ";

        return from;
    }
}