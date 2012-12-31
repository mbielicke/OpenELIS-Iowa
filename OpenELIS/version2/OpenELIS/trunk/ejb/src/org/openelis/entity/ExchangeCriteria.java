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
import org.openelis.gwt.common.DataBaseUtil;
import org.openelis.utils.Audit;
import org.openelis.utils.AuditUtil;
import org.openelis.utils.Auditable;

/**
 * Exchange Criteria Entity POJO for database
 */
@NamedQueries({
     @NamedQuery( name = "ExchangeCriteria.FetchById",
                 query = "select new org.openelis.domain.ExchangeCriteriaViewDO(ec.id, ec.name," +
                         "ec.environmentId, ec.destinationUri, ec.isAllAnalysesIncluded, ec.query)"
                       + " from ExchangeCriteria ec where ec.id = :id"),
     @NamedQuery( name = "ExchangeCriteria.FetchByName",
                 query = "select new org.openelis.domain.ExchangeCriteriaViewDO(ec.id, ec.name," +
                         "ec.environmentId, ec.destinationUri, ec.isAllAnalysesIncluded, ec.query)"
                       + " from ExchangeCriteria ec where ec.name = :name")})
                     
@Entity
@Table(name = "exchange_criteria")
@EntityListeners({AuditUtil.class})
public class ExchangeCriteria implements Auditable, Cloneable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer          id;

    @Column(name = "name")
    private String           name;

    @Column(name = "environment_id")
    private Integer          environmentId;

    @Column(name = "destination_uri")
    private String           destinationUri;
    
    @Column(name = "is_all_analyses_included")
    private String           isAllAnalysesIncluded;

    @Column(name = "query")
    private String           query;
    
    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "exchange_criteria_id", insertable = false, updatable = false)
    private Collection<ExchangeProfile> exchangeProfile;

    @Transient
    private ExchangeCriteria original;

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

    public Integer getEnvironmentId() {
        return environmentId;
    }

    public void setEnvironmentId(Integer environmentId) {
        if (DataBaseUtil.isDifferent(environmentId, this.environmentId))
            this.environmentId = environmentId;
    }

    public String getDestinationUri() {
        return destinationUri;
    }

    public void setDestinationUri(String destinationUri) {
        if (DataBaseUtil.isDifferent(destinationUri, this.destinationUri))
            this.destinationUri = destinationUri;
    }

    public String getIsAllAnalysesIncluded() {
        return isAllAnalysesIncluded;
    }

    public void setIsAllAnalysesIncluded(String isAllAnalysesIncluded) {
        if (DataBaseUtil.isDifferent(isAllAnalysesIncluded, this.isAllAnalysesIncluded))
            this.isAllAnalysesIncluded = isAllAnalysesIncluded;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        if (DataBaseUtil.isDifferent(query, this.query))
            this.query = query;
    }

    public Collection<ExchangeProfile> getExchangeProfile() {
        return exchangeProfile;
    }

    public void setExchangeProfile(Collection<ExchangeProfile> exchangeProfile) {
        this.exchangeProfile = exchangeProfile;
    }

    public void setClone() {
        try {
            original = (ExchangeCriteria)this.clone();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Audit getAudit(Integer activity) {
        Audit audit;

        audit = new Audit(activity);
        audit.setReferenceTableId(Constants.table().EXCHANGE_CRITERIA);
        audit.setReferenceId(getId());
        if (original != null)
            audit.setField("id", id, original.id)
                 .setField("name", name, original.name)
                 .setField("environment_id", environmentId, original.environmentId, Constants.table().DICTIONARY)
                 .setField("destinationUri", destinationUri, original.destinationUri)
                 .setField("is_all_analyses_included", isAllAnalysesIncluded, original.isAllAnalysesIncluded)
                 .setField("query", query, original.query);

        return audit;
    }
}