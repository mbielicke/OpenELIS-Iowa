package org.openelis.stfu.meta;

import java.util.Arrays;
import java.util.HashSet;

import org.openelis.ui.common.Meta;
import org.openelis.ui.common.MetaMap;

public class CaseMeta implements Meta, MetaMap {
	
	private static final String ID = "id", 
			                    CREATED_DATE = "createdDate",
			                    PATIENT_ID = "patientId",
			                    NEXT_OF_KIN_ID = "nextOfKinId",
			                    CASE_PATIENT_ID = "casePatientId",
			                    CASE_NEXT_OF_KIN_ID = "caseNextOfKinId",
			                    ORGANIZATION_ID = "organizationId",
			                    COMPLETED_DATE = "completedDate",
			                    IS_FINALIZED = "isFinalized";
	
	private static HashSet<String> names;
	
	static{
		names = new HashSet<String>(Arrays.asList(ID,CREATED_DATE,PATIENT_ID,NEXT_OF_KIN_ID,CASE_PATIENT_ID,
				                                  CASE_NEXT_OF_KIN_ID,ORGANIZATION_ID,COMPLETED_DATE,IS_FINALIZED));
	}

	public static String getId() {
		return ID;
	}

	public static String getCreatedDate() {
		return CREATED_DATE;
	}

	public static String getPatientId() {
		return PATIENT_ID;
	}

	public static String getNextOfKinId() {
		return NEXT_OF_KIN_ID;
	}

	public static String getCasePatientId() {
		return CASE_PATIENT_ID;
	}

	public static String getCaseNextOfKinId() {
		return CASE_NEXT_OF_KIN_ID;
	}

	public static String getOrganizationId() {
		return ORGANIZATION_ID;
	}

	public static String getCompletedDate() {
		return COMPLETED_DATE;
	}

	public static String getIsFinalized() {
		return IS_FINALIZED;
	}

	@Override
	public boolean hasColumn(String columnName) {
		  return names.contains(columnName);
	}
	
	@Override
	public String buildFrom(String where) {
		return "Case _case ";
	}

}
