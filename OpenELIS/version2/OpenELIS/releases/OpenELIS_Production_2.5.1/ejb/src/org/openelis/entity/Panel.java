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
 * Panel Entity POJO for database
 */

import java.util.Collection;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.openelis.domain.Constants;
import org.openelis.ui.common.DataBaseUtil;
import org.openelis.utils.Audit;
import org.openelis.utils.AuditUtil;
import org.openelis.utils.Auditable;

@NamedQueries({    
    @NamedQuery( name = "Panel.FetchById", 
                query = "select distinct new org.openelis.domain.PanelDO(p.id,p.name,p.description)"
                      + " from Panel p where p.id = :id"),
    @NamedQuery( name = "Panel.FetchByIds", 
                query = "select distinct new org.openelis.domain.PanelDO(p.id,p.name,p.description)"
                      + " from Panel p where p.id in (:ids)"),                  
    @NamedQuery( name = "Panel.FetchByName",
                query = "select distinct new org.openelis.domain.PanelDO(p.id,p.name,p.description)"
                      + " from Panel p where p.name like :name order by p.name"),
   @NamedQuery( name = "Panel.FetchPanelSampleTypeList",
                query = "select distinct new org.openelis.domain.TestMethodSampleTypeVO(p.id,p.name,type.typeOfSampleId, d.entry)"
                      + " from Panel p LEFT JOIN p.panelItem pi, Test t LEFT JOIN t.method m " +
                      	" INNER JOIN t.testTypeOfSample type LEFT JOIN type.typeDictionary d " +
                      	" where pi.name=t.name AND " +
                        " pi.methodName=m.name AND t.isActive='Y' order by p.name, d.entry"),
   @NamedQuery( name = "Panel.FetchAll", 
               query = "select distinct new org.openelis.domain.PanelDO(p.id,p.name,p.description)"
                      + " from Panel p"),
})

@Entity
@Table(name = "panel")
@EntityListeners( {AuditUtil.class})
public class Panel implements Auditable, Cloneable {

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer               id;

    @Column(name = "name")
    private String                name;

    @Column(name = "description")
    private String                description;

    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "panel_id", insertable = false, updatable = false)
    private Collection<PanelItem> panelItem;

    @Transient
    private Panel                 original;

    public Integer getId() {
        return id;
    }

    protected void setId(Integer id) {
        if (DataBaseUtil.isDifferent(id,this.id))
            this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if (DataBaseUtil.isDifferent(name,this.name))
            this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        if(DataBaseUtil.isDifferent(description,this.description))
            this.description = description;
    }

    public Collection<PanelItem> getPanelItem() {
        return panelItem;
    }

    public void setPanelItem(Collection<PanelItem> panelItem) {
        this.panelItem = panelItem;
    }
    
    public void setClone() {
        try {
            original = (Panel)this.clone();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Audit getAudit(Integer activity) {
        Audit audit;

        audit = new Audit(activity);
        audit.setReferenceTableId(Constants.table().PANEL);
        audit.setReferenceId(getId());
        if (original != null)
            audit.setField("id", id, original.id)
                 .setField("name", name, original.name)
                 .setField("description", description, original.description);

        return audit;
    }

}
