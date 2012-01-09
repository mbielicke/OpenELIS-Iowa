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
 * Worksheet Completion META Data
 */

import java.util.Arrays;
import java.util.HashSet;

import org.openelis.gwt.common.Meta;
import org.openelis.gwt.common.MetaMap;

public class WorksheetCompletionMeta implements Meta, MetaMap {
    private static final String ID                   = "_worksheet.id",
                                CREATED_DATE         = "_worksheet.createdDate",
                                SYSTEM_USER_ID       = "_worksheet.systemUserId",
                                STATUS_ID            = "_worksheet.statusId",
                                FORMAT_ID            = "_worksheet.formatId",
                                SUBSET_CAPACITY      = "_worksheet.subsetCapacity",
                                RELATED_WORKSHEET_ID = "_worksheet.relatedWorksheetId",
                                INSTRUMENT_ID        = "_worksheet.instrumentId",

                                ITEM_ID             = "_worksheetItem.id",
                                ITEM_WORKSHEET_ID   = "_worksheetItem.worksheetId",
                                ITEM_POSITION       = "_worksheetItem.position",

                                ANALYSIS_ID                    = "_worksheetAnalysis.id",
                                ANALYSIS_WORKSHEET_ITEM_ID     = "_worksheetAnalysis.worksheetItemId",
                                ANALYSIS_ACCESSION_NUMBER      = "_worksheetAnalysis.accessionNumber",
                                ANALYSIS_ANALYSIS_ID           = "_worksheetAnalysis.analysisId",
                                ANALYSIS_QC_ID                 = "_worksheetAnalysis.qcId",
                                ANALYSIS_WORKSHEET_ANALYSIS_ID = "_worksheetAnalysis.worksheetAnalysisId",
                                ANALYSIS_QC_SYSTEM_USER_ID     = "_worksheetAnalysis.qcSystemUserId",
                                ANALYSIS_QC_STARTED_DATE       = "_worksheetAnalysis.qcStartedDate",
                                ANALYSIS_IS_FROM_OTHER         = "_worksheetAnalysis.isFromOther",

                                RESULT_ID                    = "_worksheetResult.id",
                                RESULT_WORKSHEET_ANALYSIS_ID = "_worksheetResult.worksheetAnalysisId",
                                RESULT_TEST_ANALYTE_ID       = "_worksheetResult.testAnalyteId",
                                RESULT_TEST_RESULT_ID        = "_worksheetResult.testResultId",
                                RESULT_RESULT_ROW            = "_worksheetResult.resultRow",
                                RESULT_ANALYTE_ID            = "_worksheetResult.analyteId",
                                RESULT_TYPE_ID               = "_worksheetResult.typeId",
                                RESULT_VALUE_1               = "_worksheetResult.value1",
                                RESULT_VALUE_2               = "_worksheetResult.value2",
                                RESULT_VALUE_3               = "_worksheetResult.value3",
                                RESULT_VALUE_4               = "_worksheetResult.value4",
                                RESULT_VALUE_5               = "_worksheetResult.value5",
                                RESULT_VALUE_6               = "_worksheetResult.value6",
                                RESULT_VALUE_7               = "_worksheetResult.value7",
                                RESULT_VALUE_8               = "_worksheetResult.value8",
                                RESULT_VALUE_9               = "_worksheetResult.value9",
                                RESULT_VALUE_10              = "_worksheetResult.value10",
                                RESULT_VALUE_11              = "_worksheetResult.value11",
                                RESULT_VALUE_12              = "_worksheetResult.value12",
                                RESULT_VALUE_13              = "_worksheetResult.value13",
                                RESULT_VALUE_14              = "_worksheetResult.value14",
                                RESULT_VALUE_15              = "_worksheetResult.value15",
                                RESULT_VALUE_16              = "_worksheetResult.value16",
                                RESULT_VALUE_17              = "_worksheetResult.value17",
                                RESULT_VALUE_18              = "_worksheetResult.value18",
                                RESULT_VALUE_19              = "_worksheetResult.value19",
                                RESULT_VALUE_20              = "_worksheetResult.value20",
                                RESULT_VALUE_21              = "_worksheetResult.value21",
                                RESULT_VALUE_22              = "_worksheetResult.value22",
                                RESULT_VALUE_23              = "_worksheetResult.value23",
                                RESULT_VALUE_24              = "_worksheetResult.value24",
                                RESULT_VALUE_25              = "_worksheetResult.value25",
                                RESULT_VALUE_26              = "_worksheetResult.value26",
                                RESULT_VALUE_27              = "_worksheetResult.value27",
                                RESULT_VALUE_28              = "_worksheetResult.value28",
                                RESULT_VALUE_29              = "_worksheetResult.value29",
                                RESULT_VALUE_30              = "_worksheetResult.value30",

                                QC_RESULT_ID                    = "_worksheetQcResult.id",
                                QC_RESULT_WORKSHEET_ANALYSIS_ID = "_worksheetQcResult.worksheetAnalysisId",
                                QC_RESULT_SORT_ORDER            = "_worksheetQcResult.sortOrder",
                                QC_RESULT_QC_ANALYTE_ID         = "_worksheetQcResult.qcAnalyteId",
                                QC_RESULT_TYPE_ID               = "_worksheetQcResult.typeId",
                                QC_RESULT_VALUE                 = "_worksheetQcResult.value",
                                
                                INSTRUMENT_NAME                 = "_instrument.name",
                                SAMP_DESCRIPTION                = "_sample.description",   // combined field for all domain descriptions
                                ANALYSIS_STATUS_ID              = "_analysis.status_id",
                                ANALYSIS_TEST_NAME              = "_analysis.test.name",
                                ANALYSIS_TEST_METHOD_NAME       = "_analysis.test.method.name";
    

    private static HashSet<String> names;

    static {
        names = new HashSet<String>(Arrays.asList(ID, CREATED_DATE, SYSTEM_USER_ID,
                                    STATUS_ID, FORMAT_ID, SUBSET_CAPACITY, RELATED_WORKSHEET_ID,
                                    INSTRUMENT_ID, ITEM_ID, ITEM_WORKSHEET_ID, ITEM_POSITION,
                                    ANALYSIS_ID, ANALYSIS_WORKSHEET_ITEM_ID, ANALYSIS_ACCESSION_NUMBER,
                                    ANALYSIS_ANALYSIS_ID, ANALYSIS_QC_ID, ANALYSIS_WORKSHEET_ANALYSIS_ID,
                                    ANALYSIS_QC_SYSTEM_USER_ID, ANALYSIS_QC_STARTED_DATE,
                                    ANALYSIS_IS_FROM_OTHER,
                                    RESULT_ID, RESULT_WORKSHEET_ANALYSIS_ID, RESULT_TEST_ANALYTE_ID,
                                    RESULT_TEST_RESULT_ID, RESULT_RESULT_ROW, RESULT_ANALYTE_ID,
                                    RESULT_TYPE_ID, RESULT_VALUE_1, RESULT_VALUE_2,
                                    RESULT_VALUE_3, RESULT_VALUE_4, RESULT_VALUE_5,
                                    RESULT_VALUE_6, RESULT_VALUE_7, RESULT_VALUE_8,
                                    RESULT_VALUE_9, RESULT_VALUE_10, RESULT_VALUE_11,
                                    RESULT_VALUE_12, RESULT_VALUE_13, RESULT_VALUE_14,
                                    RESULT_VALUE_15, RESULT_VALUE_16, RESULT_VALUE_17,
                                    RESULT_VALUE_18, RESULT_VALUE_19, RESULT_VALUE_20,
                                    RESULT_VALUE_21, RESULT_VALUE_22, RESULT_VALUE_23,
                                    RESULT_VALUE_24, RESULT_VALUE_25, RESULT_VALUE_26,
                                    RESULT_VALUE_27, RESULT_VALUE_28, RESULT_VALUE_29,
                                    RESULT_VALUE_30, QC_RESULT_ID, QC_RESULT_WORKSHEET_ANALYSIS_ID,
                                    QC_RESULT_SORT_ORDER, QC_RESULT_QC_ANALYTE_ID,
                                    QC_RESULT_TYPE_ID, QC_RESULT_VALUE, INSTRUMENT_NAME/*,
                                    TEST_NAME, METHOD_NAME*/));
    }

    public static String getId() {
        return ID;
    }

    public static String getCreatedDate() {
        return CREATED_DATE;
    }

    public static String getSystemUserId() {
        return SYSTEM_USER_ID;
    }

    public static String getStatusId() {
        return STATUS_ID;
    }

    public static String getFormatId() {
        return FORMAT_ID;
    }

    public static String getSubsetCapacity() {
        return SUBSET_CAPACITY;
    }

    public static String getRelatedWorksheetId() {
        return RELATED_WORKSHEET_ID;
    }

    public static String getInstrumentId() {
        return INSTRUMENT_ID;
    }

    public static String getWorksheetItemId() {
        return ITEM_ID;
    }

    public static String getWorksheetItemWorksheetId() {
        return ITEM_WORKSHEET_ID;
    }

    public static String getWorksheetItemPosition() {
        return ITEM_POSITION;
    }

    public static String getWorksheetAnalysisId() {
        return ANALYSIS_ID;
    }

    public static String getWorksheetAnalysisWorksheetItemId() {
        return ANALYSIS_WORKSHEET_ITEM_ID;
    }

    public static String getWorksheetAnalysisAccessionNumber() {
        return ANALYSIS_ACCESSION_NUMBER;
    }

    public static String getWorksheetAnalysisAnalysisId() {
        return ANALYSIS_ANALYSIS_ID;
    }

    public static String getWorksheetAnalysisQcId() {
        return ANALYSIS_QC_ID;
    }

    public static String getWorksheetAnalysisWorksheetAnalysisId() {
        return ANALYSIS_WORKSHEET_ANALYSIS_ID;
    }

    public static String getWorksheetAnalysisQcSystemUserId() {
        return ANALYSIS_QC_SYSTEM_USER_ID;
    }

    public static String getWorksheetAnalysisQcStartedDate() {
        return ANALYSIS_QC_STARTED_DATE;
    }

    public static String getWorksheetAnalysisIsFromOther() {
        return ANALYSIS_IS_FROM_OTHER;
    }

    public static String getWorksheetResultId() {
        return RESULT_ID;
    }

    public static String getWorksheetResultWorksheetAnalysisId() {
        return RESULT_WORKSHEET_ANALYSIS_ID;
    }

    public static String getWorksheetResultTestAnalyteId() {
        return RESULT_TEST_ANALYTE_ID;
    }

    public static String getWorksheetResultTestResultId() {
        return RESULT_TEST_RESULT_ID;
    }

    public static String getWorksheetResultResultRow() {
        return RESULT_RESULT_ROW;
    }

    public static String getWorksheetResultAnalyteId() {
        return RESULT_ANALYTE_ID;
    }

    public static String getWorksheetResultTypeId() {
        return RESULT_TYPE_ID;
    }

    public static String getWorksheetResultValue1() {
        return RESULT_VALUE_1;
    }

    public static String getWorksheetResultValue2() {
        return RESULT_VALUE_2;
    }

    public static String getWorksheetResultValue3() {
        return RESULT_VALUE_3;
    }

    public static String getWorksheetResultValue4() {
        return RESULT_VALUE_4;
    }

    public static String getWorksheetResultValue5() {
        return RESULT_VALUE_5;
    }

    public static String getWorksheetResultValue6() {
        return RESULT_VALUE_6;
    }

    public static String getWorksheetResultValue7() {
        return RESULT_VALUE_7;
    }

    public static String getWorksheetResultValue8() {
        return RESULT_VALUE_8;
    }

    public static String getWorksheetResultValue9() {
        return RESULT_VALUE_9;
    }

    public static String getWorksheetResultValue10() {
        return RESULT_VALUE_10;
    }

    public static String getWorksheetResultValue11() {
        return RESULT_VALUE_11;
    }

    public static String getWorksheetResultValue12() {
        return RESULT_VALUE_12;
    }

    public static String getWorksheetResultValue13() {
        return RESULT_VALUE_13;
    }

    public static String getWorksheetResultValue14() {
        return RESULT_VALUE_14;
    }

    public static String getWorksheetResultValue15() {
        return RESULT_VALUE_15;
    }

    public static String getWorksheetResultValue16() {
        return RESULT_VALUE_16;
    }

    public static String getWorksheetResultValue17() {
        return RESULT_VALUE_17;
    }

    public static String getWorksheetResultValue18() {
        return RESULT_VALUE_18;
    }

    public static String getWorksheetResultValue19() {
        return RESULT_VALUE_19;
    }

    public static String getWorksheetResultValue20() {
        return RESULT_VALUE_20;
    }

    public static String getWorksheetResultValue21() {
        return RESULT_VALUE_21;
    }

    public static String getWorksheetResultValue22() {
        return RESULT_VALUE_22;
    }

    public static String getWorksheetResultValue23() {
        return RESULT_VALUE_23;
    }

    public static String getWorksheetResultValue24() {
        return RESULT_VALUE_24;
    }

    public static String getWorksheetResultValue25() {
        return RESULT_VALUE_25;
    }

    public static String getWorksheetResultValue26() {
        return RESULT_VALUE_26;
    }

    public static String getWorksheetResultValue27() {
        return RESULT_VALUE_27;
    }

    public static String getWorksheetResultValue28() {
        return RESULT_VALUE_28;
    }

    public static String getWorksheetResultValue29() {
        return RESULT_VALUE_29;
    }

    public static String getWorksheetResultValue30() {
        return RESULT_VALUE_30;
    }

    public static String getWorksheetQcResultId() {
        return QC_RESULT_ID;
    }

    public static String getWorksheetQcResultWorksheetAnalysisId() {
        return QC_RESULT_WORKSHEET_ANALYSIS_ID;
    }

    public static String getWorksheetQcResultSortOrder() {
        return QC_RESULT_SORT_ORDER;
    }

    public static String getWorksheetQcResultQcAnalyteId() {
        return QC_RESULT_QC_ANALYTE_ID;
    }

    public static String getWorksheetQcResultTypeId() {
        return QC_RESULT_TYPE_ID;
    }

    public static String getWorksheetQcResultValue() {
        return QC_RESULT_VALUE;
    }

    public static String getInstrumentName() {
        return INSTRUMENT_NAME;
    }

    public static String getSampleDescription() {
        return SAMP_DESCRIPTION;
    }

    public static String getAnalysisStatusId() {
        return ANALYSIS_STATUS_ID;
    }

    public static String getAnalysisTestName() {
        return ANALYSIS_TEST_NAME;
    }

    public static String getAnalysisTestMethodName() {
        return ANALYSIS_TEST_METHOD_NAME;
    }

    public static HashSet<String> getNames() {
        return names;
    }

    public boolean hasColumn(String columnName) {
        return names.contains(columnName);
    }

    public String buildFrom(String where) {
        String from;

        from = "Worksheet _worksheet "+
               ", IN (_worksheet.worksheetItem) _worksheetItem "+
               ", IN (_worksheetItem.worksheetAnalysis) _worksheetAnalysis "+
               " LEFT JOIN _worksheet.instrument _instrument ";

        return from;
    }
}