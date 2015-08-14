package org.openelis.stfu.meta;

import java.util.Arrays;
import java.util.HashSet;

import org.openelis.ui.common.Meta;
import org.openelis.ui.common.MetaMap;

public class CaseContactLocationMeta implements Meta, MetaMap {

	private static final String ID = "id",
			                    CASE_CONTACT_ID = "caseContactId",
			                    LOCATION = "location",
							    ADDRESS_ID = "addressID";
	
	private static HashSet<String> names;
	
	static {
		names = new HashSet<String>(Arrays.asList(ID,CASE_CONTACT_ID,LOCATION,ADDRESS_ID));
	}
	
	public static String getId() {
		return ID;
	}

	public static String getCaseContactId() {
		return CASE_CONTACT_ID;
	}

	public static String getLocation() {
		return LOCATION;
	}

	public static String getAddressId() {
		return ADDRESS_ID;
	}

	@Override
	public String buildFrom(String where) {
		return "CaseContactLocation _caseContactLocation ";
	}

	@Override
	public boolean hasColumn(String columnName) {
		return names.contains(columnName);
	}
	
	

}
