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
package org.openelis.entity;

/**
  * AnalysisUser Entity POJO for database 
  */

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.openelis.domain.ReferenceTable;
import org.openelis.gwt.common.DataBaseUtil;
import org.openelis.utils.Audit;
import org.openelis.utils.AuditUtil;
import org.openelis.utils.Auditable;

@NamedQueries( {
     @NamedQuery( name = "AnalysisUser.FetchById",
                query = "select new org.openelis.domain.AnalysisUserViewDO(a.id, a.analysisId, a.systemUserId, a.actionId, '')"
                      + " from AnalysisUser a where a.id = :id"),
    @NamedQuery( name = "AnalysisUser.FetchByAnalysisId",
                query = "select new org.openelis.domain.AnalysisUserViewDO(a.id, a.analysisId, a.systemUserId,a.actionId, '')"
                      + " from AnalysisUser a where a.analysisId = :id "),
    @NamedQuery( name = "AnalysisUser.FetchByActionAndAnalysisId",
                query = "select new org.openelis.domain.AnalysisUserViewDO(a.id, a.analysisId, a.systemUserId,a.actionId, '')"
                      + " from AnalysisUser a where a.analysisId = :analysisId and a.actionId = :actionId")})

@Entity
@Table(name = "analysis_user")
@EntityListeners( {AuditUtil.class})
public class AnalysisUser implements Auditable, Cloneable {

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer      id;

    @Column(name = "analysis_id")
    private Integer      analysisId;

    @Column(name = "system_user_id")
    private Integer      systemUserId;

    @Column(name = "action_id")
    private Integer      actionId;

    @Transient
    private AnalysisUser original;

    public Integer getId() {
        return id;
    }

    protected void setId(Integer id) {
        if (DataBaseUtil.isDifferent(id, this.id))
            this.id = id;
    }

    public Integer getAnalysisId() {
        return analysisId;
    }

    public void setAnalysisId(Integer analysisId) {
        if (DataBaseUtil.isDifferent(analysisId,this.analysisId))
            this.analysisId = analysisId;
    }

    public Integer getSystemUserId() {
        return systemUserId;
    }

    public void setSystemUserId(Integer systemUserId) {
        if (DataBaseUtil.isDifferent(systemUserId,this.systemUserId))
            this.systemUserId = systemUserId;
    }

    public Integer getActionId() {
        return actionId;
    }

    public void setActionId(Integer actionId) {
        if (DataBaseUtil.isDifferent(actionId,this.actionId))
            this.actionId = actionId;
    }

    public void setClone() {
        try {
            original = (AnalysisUser)this.clone();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Audit getAudit() {
        Audit audit;

        audit = new Audit();
        audit.setReferenceTableId(ReferenceTable.ANALYSIS_USER);
        audit.setReferenceId(getId());
        if (original != null)
            audit.setField("id", id, original.id)
                 .setField("analysis_id", analysisId, original.analysisId, ReferenceTable.ANALYSIS)
                 .setField("system_user_id", systemUserId, original.systemUserId)
                 .setField("action_id", actionId, original.actionId, ReferenceTable.DICTIONARY);

        return audit;
    }

}
