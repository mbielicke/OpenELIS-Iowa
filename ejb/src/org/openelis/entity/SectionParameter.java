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
 * SectionParameter Entity POJO for database
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
    @NamedQuery( name = "SectionParameter.FetchBySectionId",
                query = "select new org.openelis.domain.SectionParameterDO(p.id,p.sectionId, p.typeId, p.value)"
                      + " from SectionParameter p where p.sectionId = :id"),
    @NamedQuery( name = "SectionParameter.FetchBySectionIdAndTypeId",
                query = "select new org.openelis.domain.SectionParameterDO(p.id,p.sectionId, p.typeId, p.value)"
                      + " from SectionParameter p where p.sectionId = :id and p.typeId = :typeId")})
                      
@Entity
@Table(name = "section_parameter")
@EntityListeners({AuditUtil.class})
public class SectionParameter implements Auditable, Cloneable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer          id;

    @Column(name = "section_id")
    private Integer          sectionId;

    @Column(name = "type_id")
    private Integer          typeId;

    @Column(name = "value")
    private String           value;

    @Transient
    private SectionParameter original;

    public Integer getId() {
        return id;
    }

    protected void setId(Integer id) {
        if (DataBaseUtil.isDifferent(id, this.id))
            this.id = id;
    }

    public Integer getSectionId() {
        return sectionId;
    }

    public void setSectionId(Integer sectionId) {
        if (DataBaseUtil.isDifferent(sectionId, this.sectionId))
            this.sectionId = sectionId;
    }

    public Integer getTypeId() {
        return typeId;
    }

    public void setTypeId(Integer typeId) {
        if (DataBaseUtil.isDifferent(typeId, this.typeId))
            this.typeId = typeId;
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
            original = (SectionParameter)this.clone();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Audit getAudit(Integer activity) {
        Audit audit;

        audit = new Audit(activity);
        audit.setReferenceTableId(Constants.table().SECTION_PARAMETER);
        audit.setReferenceId(getId());
        if (original != null)
            audit.setField("id", id, original.id)
                 .setField("section_id", sectionId, original.sectionId)
                 .setField("type_id", typeId, original.typeId, Constants.table().DICTIONARY)
                 .setField("value", value, original.value);
        
        return audit;
    }
}
