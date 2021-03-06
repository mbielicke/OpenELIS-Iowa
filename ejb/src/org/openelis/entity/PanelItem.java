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
package org.openelis.entity;

/**
 * PanelItem Entity POJO for database
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

import org.openelis.domain.Constants;
import org.openelis.ui.common.DataBaseUtil;
import org.openelis.utils.Audit;
import org.openelis.utils.AuditUtil;
import org.openelis.utils.Auditable;

@NamedQueries({
               @NamedQuery(name = "PanelItem.FetchByPanelId",
                           query = "select distinct new org.openelis.domain.PanelItemDO(p.id,p.panelId,p.type,p.sortOrder,p.name,p.methodName)"
                                   + " from PanelItem p where p.panelId = :id order by p.sortOrder"),
               @NamedQuery(name = "PanelItem.FetchByPanelIds",
                           query = "select distinct new org.openelis.domain.PanelItemDO(p.id,p.panelId,p.type,p.sortOrder,p.name,p.methodName)"
                                   + " from PanelItem p where p.panelId in (:ids) order by p.sortOrder")})
@Entity
@Table(name = "panel_item")
@EntityListeners({AuditUtil.class})
public class PanelItem implements Auditable, Cloneable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer   id;

    @Column(name = "panel_id")
    private Integer   panelId;

    @Column(name = "type")
    private String    type;

    @Column(name = "sort_order")
    private Integer   sortOrder;

    @Column(name = "name")
    private String    name;

    @Column(name = "method_name")
    private String    methodName;

    @Transient
    private PanelItem original;

    public Integer getId() {
        return id;
    }

    protected void setId(Integer id) {
        if (DataBaseUtil.isDifferent(id, this.id))
            this.id = id;
    }

    public Integer getPanelId() {
        return panelId;
    }

    public void setPanelId(Integer panelId) {
        if (DataBaseUtil.isDifferent(panelId, this.panelId))
            this.panelId = panelId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        if (DataBaseUtil.isDifferent(type, this.type))
            this.type = type;
    }

    public Integer getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(Integer sortOrder) {
        if (DataBaseUtil.isDifferent(sortOrder, this.sortOrder))
            this.sortOrder = sortOrder;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if (DataBaseUtil.isDifferent(name, this.name))
            this.name = name;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        if (DataBaseUtil.isDifferent(methodName, this.methodName))
            this.methodName = methodName;
    }

    public void setClone() {
        try {
            original = (PanelItem)this.clone();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Audit getAudit(Integer activity) {
        Audit audit;

        audit = new Audit(activity);
        audit.setReferenceTableId(Constants.table().PANEL_ITEM);
        audit.setReferenceId(getId());
        if (original != null)
            audit.setField("id", id, original.id)
                 .setField("panel_id", panelId, original.panelId)
                 .setField("type", type, original.type)
                 .setField("sort_order", sortOrder, original.sortOrder)
                 .setField("name", name, original.name)
                 .setField("method_name", methodName, original.methodName);

        return audit;
    }
}
