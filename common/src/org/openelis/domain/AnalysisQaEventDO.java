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
package org.openelis.domain;

import org.openelis.gwt.common.DataBaseUtil;

public class AnalysisQaEventDO extends DataObject {

    private static final long serialVersionUID = 1L;

    protected Integer         id;
    protected Integer         analysisId;
    protected Integer         qaeventId;
    protected Integer         typeId;
    protected String          isBillable;

    public AnalysisQaEventDO() {
    }

    public AnalysisQaEventDO(Integer id, Integer analysisId, Integer qaeventId, Integer typeId, String isBillable) {
        setId(id);
        setAnalysisId(analysisId);
        setQaEventId(qaeventId);
        setTypeId(typeId);
        setIsBillable(isBillable);
        _changed = false;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
        _changed = true;
    }

    public Integer getAnalysisId() {
        return analysisId;
    }

    public void setAnalysisId(Integer analysisId) {
        this.analysisId = analysisId;
        _changed = true;
    }

    public Integer getQaEventId() {
        return qaeventId;
    }

    public void setQaEventId(Integer qaeventId) {
        this.qaeventId = qaeventId;
        _changed = true;
    }

    public Integer getQaeventId() {
        return qaeventId;
    }

    public void setQaeventId(Integer qaeventId) {
        this.qaeventId = qaeventId;
        _changed = true;
    }

    public Integer getTypeId() {
        return typeId;
    }

    public void setTypeId(Integer typeId) {
        this.typeId = typeId;
        _changed = true;
    }

    public String getIsBillable() {
        return isBillable;
    }

    public void setIsBillable(String isBillable) {
        this.isBillable = DataBaseUtil.trim(isBillable);
        _changed = true;
    }
}
