package org.openelis.entity;

/**
 * OrganizationParameter Entity POJO for database
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
               @NamedQuery(name = "OrganizationParameter.FetchByOrganizationId",
                           query = "select new org.openelis.domain.OrganizationParameterDO(p.id,p.organizationId,"
                                   + "p.typeId,p.value)"
                                   + " from OrganizationParameter p where p.organizationId = :id"),
               @NamedQuery(name = "OrganizationParameter.FetchByOrganizationIds",
                           query = "select new org.openelis.domain.OrganizationParameterDO(p.id,p.organizationId,"
                                   + "p.typeId,p.value)"
                                   + " from OrganizationParameter p where p.organizationId in (:ids)"),
               @NamedQuery(name = "OrganizationParameter.FetchByOrgIdDictSystemName",
                           query = "select new org.openelis.domain.OrganizationParameterDO(p.id,p.organizationId,"
                                   + "p.typeId,p.value)"
                                   + " from OrganizationParameter p, Dictionary d where p.organizationId = :id and"
                                   + " p.typeId = d.id and d.systemName = :systemName"),
               @NamedQuery(name = "OrganizationParameter.FetchByDictionarySystemName",
                           query = "select new org.openelis.domain.OrganizationParameterDO(p.id,p.organizationId,"
                                   + "p.typeId,p.value)"
                                   + " from OrganizationParameter p, Dictionary d where p.typeId = d.id and d.systemName = :systemName")})
@Entity
@Table(name = "organization_parameter")
@EntityListeners({AuditUtil.class})
public class OrganizationParameter implements Auditable, Cloneable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer               id;

    @Column(name = "organization_id")
    private Integer               organizationId;

    @Column(name = "type_id")
    private Integer               typeId;

    @Column(name = "value")
    private String                value;

    @Transient
    private OrganizationParameter original;

    public Integer getId() {
        return id;
    }

    protected void setId(Integer id) {
        if (DataBaseUtil.isDifferent(id, this.id))
            this.id = id;
    }

    public Integer getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(Integer organizationId) {
        if (DataBaseUtil.isDifferent(organizationId, this.organizationId))
            this.organizationId = organizationId;
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
            original = (OrganizationParameter)this.clone();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Audit getAudit(Integer activity) {
        Audit audit;

        audit = new Audit(activity);
        audit.setReferenceTableId(Constants.table().ORGANIZATION_PARAMETER);
        audit.setReferenceId(getId());
        if (original != null)
            audit.setField("id", id, original.id)
                 .setField("organization_id", organizationId, original.organizationId)
                 .setField("type_id", typeId, original.typeId, Constants.table().DICTIONARY)
                 .setField("value", value, original.value);

        return audit;
    }
}
