package org.openelis.stfu.meta;

import java.util.Arrays;
import java.util.HashSet;

import org.openelis.ui.common.Meta;
import org.openelis.ui.common.MetaMap;

public class CaseProviderMeta implements Meta,MetaMap {
	
	private static final String ID = "id",
			                    CASE_ID = "caseId",
			                    CASE_CONTACT_ID = "caseContactId",
			                    TYPE_ID = "typeId";
	
	private static HashSet<String> names;
	
	static {
		names = new HashSet<String>(Arrays.asList(ID,CASE_ID,CASE_CONTACT_ID,TYPE_ID));
	}

	public static String getId() {
		return ID;
	}

	public static String getCaseId() {
		return CASE_ID;
	}

	public static String getCaseContactId() {
		return CASE_CONTACT_ID;
	}

	public static String getTypeId() {
		return TYPE_ID;
	}

	@Override
	public String buildFrom(String where) {
		return "Case Provider _caseProvider ";
	}

	@Override
	public boolean hasColumn(String columnName) {
		return names.contains(columnName);
	}
	
	
	

}
