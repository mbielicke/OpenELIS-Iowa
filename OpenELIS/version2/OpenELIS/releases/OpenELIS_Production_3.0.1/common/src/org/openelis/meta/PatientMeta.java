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
 * Patient META Data
 */

import java.util.Arrays;
import java.util.HashSet;

import org.openelis.ui.common.Meta;
import org.openelis.ui.common.MetaMap;

public class PatientMeta implements Meta, MetaMap {

    private static final String    ID = "_patient.id", LAST_NAME = "_patient.lastName",
                    FIRST_NAME = "_patient.firstName", MIDDLE_NAME = "_patient.middleName",
                    ADDRESS_ID = "_patient.addressId", BIRTH_DATE = "_patient.birthDate",
                    BIRTH_TIME = "_patient.birthTime", GENDER_ID = "_patient.genderId",
                    RACE_ID = "_patient.raceId", ETHNICITY_ID = "_patient.ethnicityId",
                    NATIONAL_ID = "_patient.nationalId", ADDR_ID = "_address.id",
                    ADDR_MULTIPLE_UNIT = "_address.multipleUnit",
                    ADDR_STREET_ADDRESS = "_address.streetAddress", ADDR_CITY = "_address.city",
                    ADDR_STATE = "_address.state", ADDR_ZIP_CODE = "_address.zipCode",
                    ADDR_WORK_PHONE = "_address.workPhone", ADDR_HOME_PHONE = "_address.homePhone",
                    ADDR_CELL_PHONE = "_address.cellPhone", ADDR_FAX_PHONE = "_address.faxPhone",
                    ADDR_EMAIL = "_address.email", ADDR_COUNTRY = "_address.country";

    private static HashSet<String> names;

    static {
        names = new HashSet<String>(Arrays.asList(ID,
                                                  LAST_NAME,
                                                  FIRST_NAME,
                                                  MIDDLE_NAME,
                                                  ADDRESS_ID,
                                                  BIRTH_DATE,
                                                  BIRTH_TIME,
                                                  GENDER_ID,
                                                  RACE_ID,
                                                  ETHNICITY_ID,
                                                  NATIONAL_ID,
                                                  ADDR_ID,
                                                  ADDR_MULTIPLE_UNIT,
                                                  ADDR_STREET_ADDRESS,
                                                  ADDR_CITY,
                                                  ADDR_STATE,
                                                  ADDR_ZIP_CODE,
                                                  ADDR_WORK_PHONE,
                                                  ADDR_HOME_PHONE,
                                                  ADDR_CELL_PHONE,
                                                  ADDR_FAX_PHONE,
                                                  ADDR_EMAIL,
                                                  ADDR_COUNTRY));
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

    public static String getMiddleName() {
        return MIDDLE_NAME;
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

    public static String getAddressAddressAddressId() {
        return ADDR_ID;
    }

    public static String getAddressMultipleUnit() {
        return ADDR_MULTIPLE_UNIT;
    }

    public static String getAddressStreetAddress() {
        return ADDR_STREET_ADDRESS;
    }

    public static String getAddressCity() {
        return ADDR_CITY;
    }

    public static String getAddressState() {
        return ADDR_STATE;
    }

    public static String getAddressZipCode() {
        return ADDR_ZIP_CODE;
    }

    public static String getAddressWorkPhone() {
        return ADDR_WORK_PHONE;
    }

    public static String getAddressHomePhone() {
        return ADDR_HOME_PHONE;
    }

    public static String getAddressCellPhone() {
        return ADDR_CELL_PHONE;
    }

    public static String getAddressFaxPhone() {
        return ADDR_FAX_PHONE;
    }

    public static String getAddressEmail() {
        return ADDR_EMAIL;
    }

    public static String getAddressCountry() {
        return ADDR_COUNTRY;
    }

    public boolean hasColumn(String columnName) {
        return names.contains(columnName);
    }

    public String buildFrom(String where) {
        String from;

        from = "Patient _patient ";

        /*
         * this makes sure that a patient's address is found even if no address
         * field is in the query, because the patient and address are fetched at
         * the same time; left join is used because address is optional
         */
        from += " LEFT JOIN _patient.address _address ";

        return from;
    }
}