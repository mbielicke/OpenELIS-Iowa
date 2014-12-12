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
 * EOrder META Data
 */

import java.util.Arrays;
import java.util.HashSet;

import org.openelis.ui.common.Meta;
import org.openelis.ui.common.MetaMap;

public class EOrderMeta implements Meta, MetaMap {
    private static final String ID  = "_eorder.id",
                                ENTERED_DATE = "_eorder.enteredDate",
                                PAPER_ORDER_VALIDATOR = "_eorder.paperOrderValidator",
                                DESCRIPTION = "_worksheet.description";

    private static HashSet<String> names;

    static {
        names = new HashSet<String>(Arrays.asList(ID, ENTERED_DATE, PAPER_ORDER_VALIDATOR,
                                                  DESCRIPTION));
    }

    public static String getId() {
        return ID;
    }

    public static String getEnteredDate() {
        return ENTERED_DATE;
    }

    public static String getPaperOrderValidator() {
        return PAPER_ORDER_VALIDATOR;
    }

    public static String getDescription() {
        return DESCRIPTION;
    }

    public boolean hasColumn(String columnName) {
        return names.contains(columnName);
    }

    public String buildFrom(String where) {
        String from;
        
        from = "EOrder _eorder";

        return from;
    }
}