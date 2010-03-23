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
                                BATCH_CAPACITY       = "_worksheet.batchCapacity",
                                RELATED_WORKSHEET_ID = "_worksheet.relatedWorksheetId",

                                ITEM_ID             = "_worksheetItem.id",
                                ITEM_WORKSHEET_ID   = "_worksheetItem.worksheetId",
                                ITEM_POSITION       = "_worksheetItem.position",

                                ANALYSIS_ID                    = "_worksheetAnalysis.id",
                                ANALYSIS_WORKSHEET_ITEM_ID     = "_worksheetAnalysis.worksheetItemId",
                                ANALYSIS_ACCESSION_NUMBER      = "_worksheetAnalysis.accessionNumber",
                                ANALYSIS_ANALYSIS_ID           = "_worksheetAnalysis.analysisId",
                                ANALYSIS_QC_ID                 = "_worksheetAnalysis.qcId",
                                ANALYSIS_WORKSHEET_ANALYSIS_ID = "_worksheetAnalysis.worksheetAnalysisId",

                                RESULT_ID                    = "_worksheetResult.id",
                                RESULT_WORKSHEET_ANALYSIS_ID = "_worksheetResult.worksheetAnalysisId",
                                RESULT_TEST_ANALYTE_ID       = "_worksheetResult.testAnalyteId",
                                RESULT_TEST_RESULT_ID        = "_worksheetResult.testResultId",
                                RESULT_IS_COLUMN             = "_worksheetResult.isColumn",
                                RESULT_SORT_ORDER            = "_worksheetResult.sortOrder",
                                RESULT_ANALYTE_ID            = "_worksheetResult.analyteId",
                                RESULT_TYPE_ID               = "_worksheetResult.typeId",
                                RESULT_VALUE                 = "_worksheetResult.value",

                                QC_RESULT_ID                    = "_worksheetQcResult.id",
                                QC_RESULT_WORKSHEET_ANALYSIS_ID = "_worksheetQcResult.worksheetAnalysisId",
                                QC_RESULT_SORT_ORDER            = "_worksheetQcResult.sortOrder",
                                QC_RESULT_QC_ANALYTE_ID         = "_worksheetQcResult.qcAnalyteId",
                                QC_RESULT_TYPE_ID               = "_worksheetQcResult.typeId",
                                QC_RESULT_VALUE                 = "_worksheetQcResult.value",
                                
                                SAMP_DESCRIPTION                = "_sample.description",   // combined field for all domain descriptions
                                ANALYSIS_STATUS_ID              = "_analysis.status_id",
                                ANALYSIS_TEST_NAME              = "_analysis.test.name",
                                ANALYSIS_TEST_METHOD_NAME       = "_analysis.test.method.name";

    private static HashSet<String> names;

    static {
        names = new HashSet<String>(Arrays.asList(ID, CREATED_DATE, SYSTEM_USER_ID,
                                    STATUS_ID, FORMAT_ID, ITEM_ID, RELATED_WORKSHEET_ID,
                                    ITEM_WORKSHEET_ID, ITEM_POSITION, ANALYSIS_ID,
                                    ANALYSIS_WORKSHEET_ITEM_ID, ANALYSIS_ACCESSION_NUMBER,
                                    ANALYSIS_ANALYSIS_ID, ANALYSIS_QC_ID, ANALYSIS_WORKSHEET_ANALYSIS_ID,
                                    RESULT_ID, RESULT_WORKSHEET_ANALYSIS_ID, RESULT_TEST_ANALYTE_ID,
                                    RESULT_TEST_RESULT_ID, RESULT_IS_COLUMN, RESULT_SORT_ORDER,
                                    RESULT_ANALYTE_ID, RESULT_TYPE_ID, RESULT_VALUE,
                                    QC_RESULT_ID, QC_RESULT_WORKSHEET_ANALYSIS_ID,
                                    QC_RESULT_SORT_ORDER, QC_RESULT_QC_ANALYTE_ID,
                                    QC_RESULT_TYPE_ID, QC_RESULT_VALUE/*, TEST_NAME,
                                    METHOD_NAME*/));
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

    public static String getBatchCapacity() {
        return BATCH_CAPACITY;
    }

    public static String getRelatedWorksheetId() {
        return RELATED_WORKSHEET_ID;
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

    public static String getWorksheetResultIsColumn() {
        return RESULT_IS_COLUMN;
    }

    public static String getWorksheetResultSortOrder() {
        return RESULT_SORT_ORDER;
    }

    public static String getWorksheetResultAnalyteId() {
        return RESULT_ANALYTE_ID;
    }

    public static String getWorksheetResultTypeId() {
        return RESULT_TYPE_ID;
    }

    public static String getWorksheetResultValue() {
        return RESULT_VALUE;
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
               ", IN (_worksheetItem.worksheetAnalysis) _worksheetAnalysis "/*+
               ", IN (_worksheetAnalysis.analysis) _analysis "+
               ", IN (_analysis.test) _test "+
               ", IN (_test.method) _method "*/;

        return from;
    }
}