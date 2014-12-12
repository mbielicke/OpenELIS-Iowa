package org.openelis.stfu.meta;

import java.util.Arrays;
import java.util.HashSet;

import org.openelis.ui.common.Meta;
import org.openelis.ui.common.MetaMap;

public class CaseContactMeta implements Meta,MetaMap{
	
	private static final String  ID = "id",
			                     SOURCE_REFERENCE_ID = "sourceReferenceId",
			                     SOURCE_REFERENCE = "sourceReference",
			                     LAST_NAME = "lastName",
			                     FIRST_NAME = "firstName",
			                     TYPE_ID = "typeId",
			                     NPI = "npi";
	
	private static HashSet<String> names;
	
	static {
		names = new HashSet<String>(Arrays.asList(ID,SOURCE_REFERENCE_ID,SOURCE_REFERENCE,LAST_NAME,FIRST_NAME,TYPE_ID,NPI));
	}

	public static String getId() {
		return ID;
	}

	public static String getSourceReferenceId() {
		return SOURCE_REFERENCE_ID;
	}

	public static String getSourceReference() {
		return SOURCE_REFERENCE;
	}

	public static String getLastName() {
		return LAST_NAME;
	}

	public static String getFirstName() {
		return FIRST_NAME;
	}

	public static String getTypeId() {
		return TYPE_ID;
	}

	public static String getNpi() {
		return NPI;
	}

	@Override
	public String buildFrom(String where) {
		return "CaseContact _caseContact ";
	}

	@Override
	public boolean hasColumn(String columnName) {
		return names.contains(columnName);
	}
	

}
