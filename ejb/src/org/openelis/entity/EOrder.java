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
 * E-Order Entity POJO for database
 */
import java.util.Collection;
import java.util.Date;

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
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.openelis.domain.Constants;
import org.openelis.ui.common.DataBaseUtil;
import org.openelis.ui.common.Datetime;
import org.openelis.utils.Audit;
import org.openelis.utils.AuditUtil;
import org.openelis.utils.Auditable;

@NamedQueries({
               @NamedQuery(name = "EOrder.FetchById",
                           query = "select distinct new org.openelis.domain.EOrderDO(e.id, e.enteredDate, e.paperOrderValidator, e.description)"
                                   + " from EOrder e where e.id = :id"),
               @NamedQuery(name = "EOrder.FetchByIds",
                           query = "select distinct new org.openelis.domain.EOrderDO(e.id, e.enteredDate, e.paperOrderValidator, e.description)"
                                   + " from EOrder e where e.id in (:ids)"),
               @NamedQuery(name = "EOrder.FetchByPaperOrderValidator",
                           query = "select distinct new org.openelis.domain.EOrderDO(e.id, e.enteredDate, e.paperOrderValidator, e.description)"
                                   + " from EOrder e where e.paperOrderValidator like :pov"),
               @NamedQuery(name = "EOrder.ReferenceCount",
                           query = "select count(s)"
                                   + " from Sample s where s.domain in ('C','N') and s.orderId = :id")})
@Entity
@Table(name = "eorder")
@EntityListeners({AuditUtil.class})
public class EOrder implements Auditable, Cloneable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer                id;

    @Column(name = "entered_date")
    private Date                   enteredDate;

    @Column(name = "paper_order_validator")
    private String                 paperOrderValidator;

    @Column(name = "description")
    private String                 description;

    @OneToOne(fetch = FetchType.LAZY, mappedBy = "eOrder")
    private EOrderBody             eOrderBody;

    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "eorder_id", insertable = false, updatable = false)
    private Collection<EOrderLink> eOrderLink;

    @Transient
    private EOrder                 original;

    public Integer getId() {
        return id;
    }

    protected void setId(Integer id) {
        if (DataBaseUtil.isDifferent(id, this.id))
            this.id = id;
    }

    public Datetime getEnteredDate() {
        return DataBaseUtil.toYS(enteredDate);
    }

    public void setEnteredDate(Datetime enteredDate) {
        if (DataBaseUtil.isDifferentYS(enteredDate, this.enteredDate))
            this.enteredDate = DataBaseUtil.toDate(enteredDate);
    }

    public String getPaperOrderValidator() {
        return paperOrderValidator;
    }

    public void setPaperOrderValidator(String paperOrderValidator) {
        if (DataBaseUtil.isDifferent(paperOrderValidator, this.paperOrderValidator))
            this.paperOrderValidator = paperOrderValidator;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        if (DataBaseUtil.isDifferent(description, this.description))
            this.description = description;
    }

    public EOrderBody geteOrderBody() {
        return eOrderBody;
    }

    public void seteOrderBody(EOrderBody eOrderBody) {
        this.eOrderBody = eOrderBody;
    }

    public Collection<EOrderLink> geteOrderLink() {
        return eOrderLink;
    }

    public void seteOrderLink(Collection<EOrderLink> eOrderLink) {
        this.eOrderLink = eOrderLink;
    }

    @Override
    public void setClone() {
        try {
            original = (EOrder)this.clone();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public Audit getAudit(Integer activity) {
        Audit audit;

        audit = new Audit(activity);
        audit.setReferenceTableId(Constants.table().EORDER);
        audit.setReferenceId(getId());
        if (original != null)
            audit.setField("id", id, original.id)
                 .setField("entered_date", enteredDate, original.enteredDate)
                 .setField("paper_order_validator", paperOrderValidator, original.paperOrderValidator)
                 .setField("description", description, original.description);

        return audit;
    }
}