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
 * E-Order Body Entity POJO for database
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

@NamedQueries({@NamedQuery(name = "EOrderBody.FetchByEOrderId",
                           query = "select new org.openelis.domain.EOrderBodyDO(eb.id, eb.eorderId, eb.xml)"
                                   + " from EOrderBody eb where eb.eorderId = :eorderId")})
@Entity
@Table(name = "eorder_body")
@EntityListeners({AuditUtil.class})
public class EOrderBody implements Auditable, Cloneable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer    id;

    @Column(name = "eorder_id")
    private Integer    eorderId;

    @Column(name = "xml")
    private String     xml;

    @Transient
    private EOrderBody original;

    public Integer getId() {
        return id;
    }

    protected void setId(Integer id) {
        if (DataBaseUtil.isDifferent(id, this.id))
            this.id = id;
    }

    public Integer getEOrderId() {
        return eorderId;
    }

    public void setEOrderId(Integer eorderId) {
        if (DataBaseUtil.isDifferent(eorderId, this.eorderId))
            this.eorderId = eorderId;
    }

    public String getXml() {
        return xml;
    }

    public void setXml(String xml) {
        if (DataBaseUtil.isDifferent(xml, this.xml))
            this.xml = xml;
    }

    @Override
    public void setClone() {
        try {
            original = (EOrderBody)this.clone();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public Audit getAudit(Integer activity) {
        Audit audit;

        audit = new Audit(activity);
        audit.setReferenceTableId(Constants.table().EORDER_BODY);
        audit.setReferenceId(getId());
        if (original != null)
            audit.setField("id", id, original.id)
                 .setField("eorder_id", eorderId, original.eorderId)
                 .setField("xml", xml, original.xml);

        return audit;
    }
}