package org.openelis.stfu.meta;

import java.util.Arrays;
import java.util.HashSet;

import org.openelis.ui.common.Meta;
import org.openelis.ui.common.MetaMap;

public class CaseUserMeta implements Meta,MetaMap {
	
	private static final String ID = "id",
			                    CASE_ID = "caseId",
			                    SYSTEM_USER_ID = "systemUserId",
			                    SECTION_ID = "sectionId",
			                    ACTION_ID = "actionId";
	
	private static HashSet<String> names;
	
	static {
		names = new HashSet<String>(Arrays.asList(ID,CASE_ID,SYSTEM_USER_ID,SECTION_ID,ACTION_ID));
	}

	public static String getId() {
		return ID;
	}

	public static String getCaseId() {
		return CASE_ID;
	}

	public static String getSystemUserId() {
		return SYSTEM_USER_ID;
	}

	public static String getSectionId() {
		return SECTION_ID;
	}

	public static String getActionId() {
		return ACTION_ID;
	}

	@Override
	public String buildFrom(String where) {
		return "CaseUser _caseUser ";
	}

	@Override
	public boolean hasColumn(String columnName) {
		return names.contains(columnName);
	}
}
