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

import org.openelis.gwt.common.DataBaseUtil;


/**
 * Class represents the fields in database table exchange_criteria.
 */

public class ExchangeCriteriaDO extends DataObject {

    private static final long serialVersionUID = 1L;

    protected Integer         id, environmentId;
    protected String          name, destinationUri, isAllAnalysesIncluded, query;
    
    public ExchangeCriteriaDO() {
    }
    
    public ExchangeCriteriaDO(Integer id, String name, Integer environmentId,
                              String destinationUri, String isAllAnalysesIncluded, 
                              String query) {
        setId(id);
        setName(name);
        setEnvironmentId(environmentId);
        setDestinationUri(destinationUri);
        setIsAllAnalysesIncluded(isAllAnalysesIncluded);
        setQuery(query);
        
        _changed = false;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
        _changed = true;
    }

    public Integer getEnvironmentId() {
        return environmentId;
    }

    public void setEnvironmentId(Integer environmentId) {
        this.environmentId = environmentId;
        _changed = true;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = DataBaseUtil.trim(name);
        _changed = true;
    }

    public String getDestinationUri() {
        return destinationUri;
    }

    public void setDestinationUri(String destinationUri) {
        this.destinationUri = DataBaseUtil.trim(destinationUri);
        _changed = true;
    }

    public String getIsAllAnalysesIncluded() {
        return isAllAnalysesIncluded;
    }

    public void setIsAllAnalysesIncluded(String isAllAnalysesIncluded) {
        this.isAllAnalysesIncluded = DataBaseUtil.trim(isAllAnalysesIncluded);
        _changed = true;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = DataBaseUtil.trim(query);
        _changed = true;
    }
}