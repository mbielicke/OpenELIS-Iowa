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
 * Address META Data
 */

import java.util.Arrays;
import java.util.HashSet;

import org.openelis.ui.common.Meta;
import org.openelis.ui.common.MetaMap;

public class AddressMeta implements Meta, MetaMap {

    private static final String ID = "id", MULTIPLE_UNIT = "multipleUnit", STREET_ADDRESS = "streetAddress",
                                CITY = "city", STATE = "state", ZIP_CODE = "zipCode",
                                WORK_PHONE = "workPhone", HOME_PHONE = "homePhone",
                                CELL_PHONE = "cellPhone", FAX_PHONE = "faxPhone",
                                EMAIL = "email", COUNTRY = "country";

    private static HashSet<String> names;

    static {
        names = new HashSet<String>(Arrays.asList(ID, MULTIPLE_UNIT, STREET_ADDRESS,
                                                  CITY, STATE, ZIP_CODE, WORK_PHONE,
                                                  HOME_PHONE, CELL_PHONE, FAX_PHONE,
                                                  EMAIL, COUNTRY));
    }

    public static String getId() {
        return ID;
    }

    public static String getMultipleUnit() {
        return MULTIPLE_UNIT;
    }

    public static String getStreetAddress() {
        return STREET_ADDRESS;
    }

    public static String getCity() {
        return CITY;
    }

    public static String getState() {
        return STATE;
    }

    public static String getZipCode() {
        return ZIP_CODE;
    }

    public static String getWorkPhone() {
        return WORK_PHONE;
    }

    public static String getHomePhone() {
        return HOME_PHONE;
    }

    public static String getCellPhone() {
        return CELL_PHONE;
    }

    public static String getFaxPhone() {
        return FAX_PHONE;
    }

    public static String getEmail() {
        return EMAIL;
    }

    public static String getCountry() {
        return COUNTRY;
    }

    public boolean hasColumn(String columnName) {
        return names.contains(columnName);
    }

    public String buildFrom(String where) {
        String from;

        from = "Address _address ";

        return from;
    }
}