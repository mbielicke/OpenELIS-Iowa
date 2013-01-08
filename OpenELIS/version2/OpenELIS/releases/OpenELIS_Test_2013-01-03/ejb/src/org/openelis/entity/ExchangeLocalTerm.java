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
 * ExchangeLocalTerm Entity POJO for database 
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
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.openelis.domain.Constants;
import org.openelis.gwt.common.DataBaseUtil;
import org.openelis.utils.Audit;
import org.openelis.utils.AuditUtil;
import org.openelis.utils.Auditable;

@NamedQueries({
    @NamedQuery( name = "ExchangeLocalTerm.FetchById",
                query = "select new org.openelis.domain.ExchangeLocalTermViewDO(e.id, e.referenceTableId," +
             	    	 "e.referenceId, '', '')"
                       + " from ExchangeLocalTerm e where e.id = :id"),
     @NamedQuery( name = "ExchangeLocalTerm.FetchByReferenceTableIdReferenceId",
                 query = "select new org.openelis.domain.ExchangeLocalTermViewDO(e.id, e.referenceTableId," +
                         "e.referenceId, '', '')"
                       + " from ExchangeLocalTerm e where e.referenceTableId = :referenceTableId and e.referenceId = :referenceId")})
                   
@Entity
@Table(name = "exchange_local_term")
@EntityListeners({AuditUtil.class})
public class ExchangeLocalTerm implements Auditable, Cloneable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer                          id;

    @Column(name = "reference_table_id")
    private Integer                          referenceTableId;

    @Column(name = "reference_id")
    private Integer                          referenceId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reference_id", insertable = false, updatable = false)
    private Analyte                          analyte;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reference_id", insertable = false, updatable = false)
    private Dictionary                       dictionary;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reference_id", insertable = false, updatable = false)
    private Method                           method;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reference_id", insertable = false, updatable = false)
    private Organization                     organization;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reference_id", insertable = false, updatable = false)
    private Test                             test;

    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "exchange_local_term_id")
    private Collection<ExchangeExternalTerm> exchangeExternalTerm;

    @Transient
    private ExchangeLocalTerm                original;

    public Integer getId() {
        return id;
    }

    protected void setId(Integer id) {
        if (DataBaseUtil.isDifferent(id, this.id))
            this.id = id;
    }

    public Integer getReferenceTableId() {
        return referenceTableId;
    }

    public void setReferenceTableId(Integer referenceTableId) {
        if (DataBaseUtil.isDifferent(referenceTableId, this.referenceTableId))
            this.referenceTableId = referenceTableId;
    }

    public Integer getReferenceId() {
        return referenceId;
    }

    public void setReferenceId(Integer referenceId) {
        if (DataBaseUtil.isDifferent(referenceId, this.referenceId))
            this.referenceId = referenceId;
    }

    public Analyte getAnalyte() {
        return analyte;
    }

    public void setAnalyte(Analyte analyte) {
        this.analyte = analyte;
    }

    public Dictionary getDictionary() {
        return dictionary;
    }

    public void setDictionary(Dictionary dictionary) {
        this.dictionary = dictionary;
    }

    public Method getMethod() {
        return method;
    }

    public void setMethod(Method method) {
        this.method = method;
    }

    public Organization getOrganization() {
        return organization;
    }

    public void setOrganization(Organization organization) {
        this.organization = organization;
    }

    public Test getTest() {
        return test;
    }

    public void setTest(Test test) {
        this.test = test;
    }

    public Collection<ExchangeExternalTerm> getExchangeExternalTerm() {
        return exchangeExternalTerm;
    }

    public void setExchangeExternalTerm(Collection<ExchangeExternalTerm> exchangeExternalTerm) {
        this.exchangeExternalTerm = exchangeExternalTerm;
    }

    public void setClone() {
        try {
            original = (ExchangeLocalTerm)this.clone();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Audit getAudit(Integer activity) {
        Audit audit;

        audit = new Audit(activity);
        audit.setReferenceTableId(Constants.table().EXCHANGE_LOCAL_TERM);
        audit.setReferenceId(getId());
        if (original != null)
            audit.setField("id", id, original.id)
                 .setField("reference_id", referenceId, original.referenceId)
                 .setField("reference_table_id", referenceTableId, original.referenceTableId);

        return audit;
    }
}