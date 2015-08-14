package org.openelis.stfu.meta;

import java.util.Arrays;
import java.util.HashSet;

import org.openelis.ui.common.Meta;
import org.openelis.ui.common.MetaMap;

public class CasePatientMeta implements Meta,MetaMap {
	
	private static final String ID = "id",
			                    LAST_NAME = "lastName",
			                    FIRST_NAME = "firstName",
			                    MAIDEN_NAME = "maidenName",
			                    ADDRESS_ID = "addressId",
			                    BIRTH_DATE = "birthDate",
			                    BIRTH_TIME = "birthTime",
			                    GENDER_ID = "genderId",
			                    RACE_ID = "raceId",
			                    ETHNICITY_ID = "ethnicityId",
			                    NATIONAL_ID = "nationalId";
	
	private static HashSet<String> names;
	
	static {
		names = new HashSet<String>(Arrays.asList(ID,LAST_NAME,FIRST_NAME,MAIDEN_NAME,ADDRESS_ID,
				                                  BIRTH_DATE,BIRTH_TIME,GENDER_ID,RACE_ID,ETHNICITY_ID,NATIONAL_ID));
	}
	
	public static String getId() {
		return ID;
	}

	public static String getLastName() {
		return LAST_NAME;
	}

	public static String getFirstName() {
		return FIRST_NAME;
	}

	public static String getMaidenName() {
		return MAIDEN_NAME;
	}

	public static String getAddressId() {
		return ADDRESS_ID;
	}

	public static String getBirthDate() {
		return BIRTH_DATE;
	}

	public static String getBirthTime() {
		return BIRTH_TIME;
	}

	public static String getGenderId() {
		return GENDER_ID;
	}

	public static String getRaceId() {
		return RACE_ID;
	}

	public static String getEthnicityId() {
		return ETHNICITY_ID;
	}

	public static String getNationalId() {
		return NATIONAL_ID;
	}

	@Override
	public String buildFrom(String where) {
		return "CasePatient _casePatient ";
	}

	@Override
	public boolean hasColumn(String columnName) {
		return names.contains(columnName);
	}
	
	

}
