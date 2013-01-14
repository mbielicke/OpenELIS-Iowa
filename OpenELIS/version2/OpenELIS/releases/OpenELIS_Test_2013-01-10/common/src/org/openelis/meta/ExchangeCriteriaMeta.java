/** Exhibit A - UIRF Open-source Based Public Software License.
* 
* The contents of this file are subject to the UIRF Open-source Based
* Public Software License(the "License"); you may not use this file except
* in compliance with the License. You may obtain a copy of the License at
* openelis.uhl.uiowa.edu
* 
* Software distributed under the License is distributed on an "AS IS"
* basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
* License for the specific language governing rights and limitations
* under the License.
* 
* The Original Code is OpenELIS code.
* 
* The Initial Developer of the Original Code is The University of Iowa.
* Portions created by The University of Iowa are Copyright 2006-2008. All
* Rights Reserved.
* 
* Contributor(s): ______________________________________.
* 
* Alternatively, the contents of this file marked
* "Separately-Licensed" may be used under the terms of a UIRF Software
* license ("UIRF Software License"), in which case the provisions of a
* UIRF Software License are applicable instead of those above. 
*/
package org.openelis.meta;

import java.util.Arrays;
import java.util.HashSet;

import org.openelis.gwt.common.Meta;
import org.openelis.gwt.common.MetaMap;

/**
 * Exchange Criteria META data
 */

public class ExchangeCriteriaMeta implements Meta, MetaMap {
    private static final String    ID = "_exchangeCriteria.id",
                                   NAME = "_exchangeCriteria.name",
                                   ENVIRONMENT_ID = "_exchangeCriteria.environmentId",
                                   DESTINATION_URI = "_exchangeCriteria.destinationUri",
                                   IS_ALL_ANALYSES_INCLUDED = "_exchangeCriteria.isAllAnalysesIncluded",
                                   QUERY = "_exchangeCriteria.query",
                                  
                                   PROFILE_ID = "_exchangeProfile.id",
                                   PROFILE_EXCHANGE_CRITERIA = "_exchangeProfile.exchangeCriteriaId",
                                   PROFILE_PROFILE_ID = "_exchangeProfile.profileId",
                                   PROFILE_SORT_ORDER = "_exchangeProfile.sortOrder";
                                  
    private static HashSet<String> names;
    
    static {
        names = new HashSet<String>(Arrays.asList(ID, NAME, ENVIRONMENT_ID, DESTINATION_URI,
                                                  IS_ALL_ANALYSES_INCLUDED, QUERY, PROFILE_ID,
                                                  PROFILE_EXCHANGE_CRITERIA, PROFILE_PROFILE_ID,
                                                  PROFILE_SORT_ORDER));
    }

    public static String getId() {
        return ID;
    }

    public static String getName() {
        return NAME;
    }

    public static String getEnvironmentId() {
        return ENVIRONMENT_ID;
    }

    public static String getDestinationUri() {
        return DESTINATION_URI;
    }

    public static String getIsAllAnalysesIncluded() {
        return IS_ALL_ANALYSES_INCLUDED;
    }

    public static String getQuery() {
        return QUERY;
    }

    public static String getProfileId() {
        return PROFILE_ID;
    }

    public static String getProfileExchangeCriteria() {
        return PROFILE_EXCHANGE_CRITERIA;
    }

    public static String getProfileProfileId() {
        return PROFILE_PROFILE_ID;
    }

    public static String getProfileSortOrder() {
        return PROFILE_SORT_ORDER;
    }

    public boolean hasColumn(String columnName) {
        return names.contains(columnName);
    }

    public String buildFrom(String where) {
        String from;
        
        from = "ExchangeCriteria _exchangeCriteria ";
        if (where.indexOf("exchangeProfile.") > -1)
            from += ",IN (_exchangeCriteria.exchangeProfile) _exchangeProfile ";

        return from;
    }
}