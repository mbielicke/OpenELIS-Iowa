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

import org.openelis.ui.common.Meta;
import org.openelis.ui.common.MetaMap;

public class AnalysisViewMeta implements Meta, MetaMap {
    private static final String SAMPLE_ID = "_analysisView.sampleId",
                                DOMAIN = "_analysisView.domain",
                                ACCESSION_NUMBER = "_analysisView.accessionNumber",
                                RECEIVED_DATE = "_analysisView.receivedDate",
                                COLLECTION_DATE = "_analysisView.collectionDate",
                                COLLECTION_TIME = "_analysisView.collectionTime",
                                ENTERED_DATE = "_analysisView.enteredDate",
                                PRIMARY_ORGANIZATION_NAME = "_analysisView.primaryOrganizationName",
                                TODO_DESCRIPTION = "_analysisView.todoDescription",
                                WORKSHEET_DESCRIPTION = "_analysisView.worksheetDescription",
                                PRIORITY = "_analysisView.priority", 
                                TEST_ID = "_analysisView.testId",
                                TEST_NAME = "_analysisView.testName", 
                                METHOD_NAME = "_analysisView.methodName",
                                TIME_TA_AVERAGE = "_analysisView.timeTaAverage",
                                TIME_HOLDING = "_analysisView.timeHolding",
                                TYPE_OF_SAMPLE_ID = "_analysisView.analysisId",
                                ANALYSIS_ID = "_analysisView.analysisId",
                                ANALYSIS_STATUS_ID = "_analysisView.analysisStatusId",
                                SECTION_ID = "_analysisView.sectionId",
                                SECTION_NAME = "_analysisView.sectionName",
                                AVAILABLE_DATE = "_analysisView.availableDate",
                                STARTED_DATE = "_analysisView.startedDate",
                                COMPLETED_DATE = "_analysisView.completedDate",
                                RELEASED_DATE = "_analysisView.releasedDate",
                                ANALYSIS_RESULT_OVERRIDE = "_analysisView.analysisResultOverride",
                                UNIT_OF_MEASURE_ID = "_analysisView.unitOfMeasureId",
                                WORKSHEET_FORMAT_ID = "_analysisView.worksheetFormatId";                                

    private static HashSet<String> names;

    static {
        names = new HashSet<String>(Arrays.asList(SAMPLE_ID, DOMAIN, ACCESSION_NUMBER,
                                                  RECEIVED_DATE, COLLECTION_DATE,
                                                  COLLECTION_TIME, ENTERED_DATE,
                                                  PRIMARY_ORGANIZATION_NAME, TODO_DESCRIPTION,
                                                  WORKSHEET_DESCRIPTION, PRIORITY,
                                                  TEST_ID, TEST_NAME, METHOD_NAME,
                                                  TIME_TA_AVERAGE, TIME_HOLDING,
                                                  TYPE_OF_SAMPLE_ID, ANALYSIS_ID,
                                                  ANALYSIS_STATUS_ID, SECTION_ID, 
                                                  SECTION_NAME, AVAILABLE_DATE,
                                                  STARTED_DATE, COMPLETED_DATE,
                                                  RELEASED_DATE, ANALYSIS_RESULT_OVERRIDE,
                                                  UNIT_OF_MEASURE_ID, WORKSHEET_FORMAT_ID));
    }

    public static String getSampleId() {
        return SAMPLE_ID;
    }

    public static String getDomain() {
        return DOMAIN;
    }

    public static String getAccessionNumber() {
        return ACCESSION_NUMBER;
    }

    public static String getReceivedDate() {
        return RECEIVED_DATE;
    }

    public static String getCollectionDate() {
        return COLLECTION_DATE;
    }

    public static String getCollectionTime() {
        return COLLECTION_TIME;
    }

    public static String getEnteredDate() {
        return ENTERED_DATE;
    }

    public static String getPrimaryOrganizationName() {
        return PRIMARY_ORGANIZATION_NAME;
    }

    public static String getTodoDescription() {
        return TODO_DESCRIPTION;
    }

    public static String getWorksheetDescription() {
        return WORKSHEET_DESCRIPTION;
    }

    public static String getPriority() {
        return PRIORITY;
    }

    public static String getTestId() {
        return TEST_ID;
    }

    public static String getTestName() {
        return TEST_NAME;
    }

    public static String getMethodName() {
        return METHOD_NAME;
    }

    public static String getTimeTaAverage() {
        return TIME_TA_AVERAGE;
    }

    public static String getTimeHolding() {
        return TIME_HOLDING;
    }

    public static String getTypeOfSampleId() {
        return TYPE_OF_SAMPLE_ID;
    }

    public static String getAnalysisId() {
        return ANALYSIS_ID;
    }

    public static String getAnalysisStatusId() {
        return ANALYSIS_STATUS_ID;
    }

    public static String getSectionId() {
        return SECTION_ID;
    }

    public static String getSectionName() {
        return SECTION_NAME;
    }

    public static String getAvailableDate() {
        return AVAILABLE_DATE;
    }

    public static String getStartedDate() {
        return STARTED_DATE;
    }

    public static String getCompletedDate() {
        return COMPLETED_DATE;
    }

    public static String getReleasedDate() {
        return RELEASED_DATE;
    }

    public static String getAnalysisResultOverride() {
        return ANALYSIS_RESULT_OVERRIDE;
    }

    public static String getUnitOfMeasureId() {
        return UNIT_OF_MEASURE_ID;
    }

    public static String getWorksheetFormatId() {
        return WORKSHEET_FORMAT_ID;
    }

    public boolean hasColumn(String columnName) {
        return names.contains(columnName);
    }

    public String buildFrom(String where) {
        String from;
        
        from = "AnalysisView _analysisView ";

        return from;
    }
}