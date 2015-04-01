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
 * E-Order Link Entity POJO for database
 */
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import org.openelis.ui.common.DataBaseUtil;

@NamedQueries({@NamedQuery(name = "EOrderLink.FetchByEOrderId",
                           query = "select distinct new org.openelis.domain.EOrderLinkDO(el.id, el.eorderId, el.reference, el.subId, el.name, el.value)"
                                   + " from EOrderLink el where el.eorderId = :eorderId")})
@Entity
@Table(name = "eorder_link")
public class EOrderLink {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer    id;

    @Column(name = "eorder_id")
    private Integer    eorderId;

    @Column(name = "reference")
    private String     reference;

    @Column(name = "sub_id")
    private String     subId;

    @Column(name = "name")
    private String     name;

    @Column(name = "value")
    private String     value;

    public Integer getId() {
        return id;
    }

    protected void setId(Integer id) {
        if (DataBaseUtil.isDifferent(id, this.id))
            this.id = id;
    }

    public Integer geteOrderId() {
        return eorderId;
    }

    public void setEOrderId(Integer eorderId) {
        if (DataBaseUtil.isDifferent(eorderId, this.eorderId))
            this.eorderId = eorderId;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        if (DataBaseUtil.isDifferent(reference, this.reference))
            this.reference = reference;
    }

    public String getSubId() {
        return subId;
    }

    public void setSubId(String subId) {
        if (DataBaseUtil.isDifferent(subId, this.subId))
            this.subId = subId;
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
}