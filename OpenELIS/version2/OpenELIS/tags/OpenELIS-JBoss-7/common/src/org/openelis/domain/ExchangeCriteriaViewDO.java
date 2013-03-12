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
package org.openelis.domain;

import java.util.ArrayList;

import org.openelis.gwt.common.data.QueryData;

/**
 * The class extends exchange criteria DO and adds a list of query data. The
 * additional field is for carrying the contents of the query in a form that makes
 * it easier for displaying the query in a user-friendly format.
 * The field organization name is added as a separate field as opposed to being 
 * part of the list because the query is not executed for the name of an organization
 * but for its id and the name can't be a part of the query because if the name 
 * of an organization changes then the query won't return any results because the
 * name and the id will conflict with each other. 
 * Note: isChanged will reflect any changes to read/display fields.
 */

public class ExchangeCriteriaViewDO extends ExchangeCriteriaDO {

    private static final long      serialVersionUID = 1L;

    protected ArrayList<QueryData> fields; 
    
    public ExchangeCriteriaViewDO() {
    }

    public ExchangeCriteriaViewDO(Integer id, String name, Integer environmentId, 
                                  String location, String isAllAnalysesIncluded,
                                  String query) {
        super(id, name, environmentId, location, isAllAnalysesIncluded, query);
    }

    public ArrayList<QueryData> getFields() {
        return fields;
    }

    public void setFields(ArrayList<QueryData> fields) {
        this.fields = fields;
    }
}