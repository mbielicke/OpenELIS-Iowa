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
 * Scriptlet Entity POJO for database
 */

import java.util.Date;

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
import org.openelis.ui.common.Datetime;
import org.openelis.utils.Audit;
import org.openelis.utils.AuditUtil;
import org.openelis.utils.Auditable;

@NamedQueries({
               @NamedQuery( name = "Scriptlet.FetchByName",
                           query = "select distinct new org.openelis.domain.IdNameVO(s.id, s.name) "
                                 + " from Scriptlet s where s.name like :name order by s.name "),
               @NamedQuery( name = "Scriptlet.FetchById",
                           query = "select distinct new org.openelis.domain.ScriptletDO(s.id, s.name, s.bean, s.isActive, s.activeBegin, s.activeEnd)"
                                 + " from Scriptlet s where s.id = :id"),
               @NamedQuery( name = "Scriptlet.FetchByIds",
                           query = "select distinct new org.openelis.domain.ScriptletDO(s.id, s.name, s.bean, s.isActive, s.activeBegin, s.activeEnd)"
                                 + " from Scriptlet s where s.id in (:ids)")})
@Entity
@Table(name = "scriptlet")
@EntityListeners({AuditUtil.class})
public class Scriptlet implements Auditable, Cloneable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer   id;

    @Column(name = "name")
    private String    name;

    @Column(name = "bean")
    private String    bean;

    @Column(name = "is_active")
    private String    isActive;

    @Column(name = "active_begin")
    private Date      activeBegin;

    @Column(name = "active_end")
    private Date      activeEnd;

    @Transient
    private Scriptlet original;

    public Integer getId() {
        return id;
    }

    protected void setId(Integer id) {
        if (DataBaseUtil.isDifferent(id, this.id))
            this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if (DataBaseUtil.isDifferent(name, this.name))
            this.name = name;
    }

    public String getBean() {
        return bean;
    }

    public void setBean(String bean) {
        if (DataBaseUtil.isDifferent(bean, this.bean))
            this.bean = bean;
    }

    public String getIsActive() {
        return isActive;
    }

    public void setIsActive(String isActive) {
        if (DataBaseUtil.isDifferent(isActive, this.isActive))
            this.isActive = isActive;
    }

    public Datetime getActiveBegin() {
        return DataBaseUtil.toYD(activeBegin);
    }

    public void setActiveBegin(Datetime active_begin) {
        if (DataBaseUtil.isDifferentYD(active_begin, this.activeBegin))
            this.activeBegin = active_begin.getDate();
    }

    public Datetime getActiveEnd() {
        return DataBaseUtil.toYD(activeEnd);
    }

    public void setActiveEnd(Datetime active_end) {
        if (DataBaseUtil.isDifferentYD(active_end, this.activeEnd))
            this.activeEnd = active_end.getDate();
    }

    public void setClone() {
        try {
            original = (Scriptlet)this.clone();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Audit getAudit(Integer activity) {
        Audit audit;

        audit = new Audit(activity);
        audit.setReferenceTableId(Constants.table().SCRIPTLET);
        audit.setReferenceId(getId());
        if (original != null)
            audit.setField("id", id, original.id)
                 .setField("name", name, original.name)
                 .setField("bean", bean, original.bean)
                 .setField("is_active", isActive, original.isActive)
                 .setField("active_begin", activeBegin, original.activeBegin)
                 .setField("active_end", activeEnd, original.activeEnd);

        return audit;
    }
}
