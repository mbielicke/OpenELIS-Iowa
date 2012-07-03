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
 * SystemVariable Entity POJO for database
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
import org.openelis.utilcommon.AuditActivity;
import org.openelis.utils.Audit;
import org.openelis.utils.AuditUtil;
import org.openelis.utils.Auditable;

@NamedQueries({
    @NamedQuery( name = "SystemVariable.FetchById",
                query = "select new org.openelis.domain.SystemVariableDO(id, name, value)"
                      + " from SystemVariable where id = :id"),
    @NamedQuery( name = "SystemVariable.FetchByName",
                query = "select new org.openelis.domain.SystemVariableDO(id, name, value)"
                      + " from SystemVariable where name like :name")})                     


@Entity
@Table(name = "system_variable")
@EntityListeners({AuditUtil.class})
public class SystemVariable implements Auditable, Cloneable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer        id;

    @Column(name = "name")
    private String         name;

    @Column(name = "value")
    private String         value;

    @Transient
    private SystemVariable original;

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

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        if (DataBaseUtil.isDifferent(value, this.value))
            this.value = value;
    }

    public void setClone() {
        try {
            original = (SystemVariable)this.clone();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Audit getAudit(AuditActivity activity) {
        Audit audit;
        
        audit = new Audit(activity);
        audit.setReferenceTableId(ReferenceTable.SYSTEM_VARIABLE);
        audit.setReferenceId(getId());
        if (original != null)
            audit.setField("id", id, original.id)
                 .setField("name", name, original.name)
                 .setField("value", value, original.value);
        
        return audit;
    }

}
