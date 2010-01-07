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
 * StorageUnit Entity POJO for database
 */

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.openelis.domain.ReferenceTable;
import org.openelis.utilcommon.DataBaseUtil;
import org.openelis.utils.Audit;
import org.openelis.utils.AuditUtil;
import org.openelis.utils.Auditable;

@NamedQueries( {
    @NamedQuery( name = "StorageUnit.FetchById",
                query = "select new org.openelis.domain.StorageUnitDO(s.id,s.categoryId,s.description,s.isSingular)"
                      + " from StorageUnit s where s.id = :id"),
    @NamedQuery( name = "StorageUnit.FetchByDescription",
                query = "select new org.openelis.domain.IdNameVO(s.id,s.description,d.entry) "
                      + " from StorageUnit s, Dictionary d where s.categoryId = d.id and s.description like :description order by s.description"),
    @NamedQuery( name = "StorageUnit.ReferenceCheck",
                query = "select storageUnitId from StorageLocation s where s.storageUnitId = :id"),                     
})

@Entity
@Table(name = "storage_unit")
@EntityListeners( {AuditUtil.class})
public class StorageUnit implements Auditable, Cloneable {

    @Id
    @GeneratedValue
    @Column(name = "id")
    private Integer     id;

    @Column(name = "category_id")
    private Integer      categoryId;

    @Column(name = "description")
    private String      description;

    @Column(name = "is_singular")
    private String      isSingular;

    @Transient
    private StorageUnit original;

    public Integer getId() {
        return id;
    }

    protected void setId(Integer id) {
        if (DataBaseUtil.isDifferent(id, this.id))
            this.id = id;
    }

    public Integer getCategory() {
        return categoryId;
    }

    public void setCategory(Integer categoryId) {
        if (DataBaseUtil.isDifferent(categoryId, this.categoryId))
            this.categoryId = categoryId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        if (DataBaseUtil.isDifferent(description, this.description))
            this.description = description;
    }

    public String getIsSingular() {
        return isSingular;
    }

    public void setIsSingular(String isSingular) {
        if (DataBaseUtil.isDifferent(isSingular, this.isSingular))
            this.isSingular = isSingular;
    }

    public void setClone() {
        try {
            original = (StorageUnit)this.clone();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Audit getAudit() {
        Audit audit;

        audit = new Audit();
        audit.setReferenceTableId(ReferenceTable.STORAGE_UNIT);
        audit.setReferenceId(getId());
        if (original != null)
            audit.setField("id", id, original.id)
                 .setField("category_id", categoryId, original.categoryId)
                 .setField("description", description, original.description)
                 .setField("is_singular", isSingular, original.isSingular);

        return audit;
    }
}
