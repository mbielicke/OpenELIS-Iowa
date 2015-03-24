package org.openelis.stfu.meta;

import java.util.Arrays;
import java.util.HashSet;

import org.openelis.ui.common.Meta;
import org.openelis.ui.common.MetaMap;

public class CaseResultMeta implements Meta,MetaMap {
	
	private static final String ID = "id",
			                    CASE_ANALYSIS_ID = "caseAnalysisId",
			                    TEST_ANALYTE_ID = "testAnalyteId",
			                    TEST_RESULT_ID = "testResultId",
			                    ROW = "row",
			                    COL = "col",
			                    IS_REPORTABLE = "isReportable",
			                    ANALYTE_ID = "analyteId",
			                    TYPE_ID = "typeId",
			                    VALUE = "value";
	
	private static HashSet<String> names;
	
	static {
		names = new HashSet<String>(Arrays.asList(ID,CASE_ANALYSIS_ID,TEST_ANALYTE_ID,TEST_RESULT_ID,
				                                  ROW,COL,IS_REPORTABLE,ANALYTE_ID,TYPE_ID,VALUE));
	}

	public static String getId() {
		return ID;
	}

	public static String getCaseAnalysisId() {
		return CASE_ANALYSIS_ID;
	}

	public static String getTestAnalyteId() {
		return TEST_ANALYTE_ID;
	}

	public static String getTestResultId() {
		return TEST_RESULT_ID;
	}

	public static String getRow() {
		return ROW;
	}

	public static String getCol() {
		return COL;
	}

	public static String getIsReportable() {
		return IS_REPORTABLE;
	}

	public static String getAnalyteId() {
		return ANALYTE_ID;
	}

	public static String getTypeId() {
		return TYPE_ID;
	}

	public static String getValue() {
		return VALUE;
	}

	@Override
	public String buildFrom(String where) {
		return "CaseResult _caseResult ";
	}

	@Override
	public boolean hasColumn(String columnName) {
		return names.contains(columnName);
	}

}
