package org.openelis.stfu.meta;

import java.util.Arrays;
import java.util.HashSet;

import org.openelis.ui.common.Meta;
import org.openelis.ui.common.MetaMap;

public class CaseTagMeta implements Meta,MetaMap{
	
	private static final String ID = "id",
			                    CASE_ID = "caseId",
			                    TYPE_ID = "typeId",
			                    SYSTEM_USER_ID = "systemUserId",
			                    CREATED_DATE = "createdDate",
			                    REMIND_DATE = "remindDate",
			                    COMPLETED_DATE = "completedDate",
			                    NOTE = "note";
	
	private static HashSet<String> names;
	
	static {
		names = new HashSet<String>(Arrays.asList(ID,CASE_ID,TYPE_ID,SYSTEM_USER_ID,CREATED_DATE,
				                                  REMIND_DATE,COMPLETED_DATE,NOTE));
	}

	public static String getId() {
		return ID;
	}

	public static String getCaseId() {
		return CASE_ID;
	}

	public static String getTypeId() {
		return TYPE_ID;
	}

	public static String getSystemUserId() {
		return SYSTEM_USER_ID;
	}

	public static String getCreatedDate() {
		return CREATED_DATE;
	}

	public static String getRemindDate() {
		return REMIND_DATE;
	}

	public static String getCompletedDate() {
		return COMPLETED_DATE;
	}

	public static String getNote() {
		return NOTE;
	}

	@Override
	public String buildFrom(String where) {
		return "CaseTag _caseTag ";
	}

	@Override
	public boolean hasColumn(String columnName) {
		return names.contains(columnName);
	}
}
