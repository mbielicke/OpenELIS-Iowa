package org.openelis.stfu.meta;

import java.util.Arrays;
import java.util.HashSet;

import org.openelis.ui.common.Meta;
import org.openelis.ui.common.MetaMap;

public class CaseAnalysisMeta implements Meta, MetaMap {
	
	private static final String ID = "id",
			                    CASE_ID = "caseId",
			                    ACCESSION_NUMBER = "accessionNumber",
			                    ORGANIZATION_ID = "organizationId",
			                    TEST_ID = "testId",
			                    STATUS_ID = "statusId",
			                    COLLECTION_DATE = "collectionDate",
			                    COMPLETED_DATE = "completedDate",
			                    CONDITION_ID = "conditionId";
	
	private static HashSet<String> names;
	
	static {
		names = new HashSet<String>(Arrays.asList(ID,CASE_ID,ACCESSION_NUMBER,ORGANIZATION_ID,TEST_ID,
				                                  STATUS_ID,COLLECTION_DATE,COMPLETED_DATE,CONDITION_ID));
	}

	public static String getId() {
		return ID;
	}

	public static String getCaseId() {
		return CASE_ID;
	}

	public static String getAccessionNumber() {
		return ACCESSION_NUMBER;
	}

	public static String getOrganizationId() {
		return ORGANIZATION_ID;
	}

	public static String getTestId() {
		return TEST_ID;
	}

	public static String getStatusId() {
		return STATUS_ID;
	}

	public static String getCollectionDate() {
		return COLLECTION_DATE;
	}

	public static String getCompletedDate() {
		return COMPLETED_DATE;
	}

	public static String getConditionId() {
		return CONDITION_ID;
	}

	@Override
	public String buildFrom(String where) {
		return "CaseAnalysis _caseAnalysis";
	}

	@Override
	public boolean hasColumn(String columnName) {
		return names.contains(columnName);
	}

	
}
