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
  * Provider Entity POJO for database 
  */

import java.util.Collection;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.openelis.util.XMLUtil;
import org.openelis.utilcommon.DataBaseUtil;
import org.openelis.utils.AuditUtil;
import org.openelis.utils.Auditable;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

@NamedQueries({
	@NamedQuery(name = "Provider.FetchById", 
			    query = "select new org.openelis.domain.ProviderDO(p.id,p.lastName,p.firstName,p.middleName,p.typeId,p.npi)"                                                                                                  
					  + " from Provider p where p.id = :id")
})

@Entity
@Table(name = "provider")
@EntityListeners( {AuditUtil.class})
public class Provider implements Auditable, Cloneable {

    @Id
    @GeneratedValue
    @Column(name = "id")
    private Integer                      id;

    @Column(name = "last_name")
    private String                       lastName;

    @Column(name = "first_name")
    private String                       firstName;

    @Column(name = "middle_name")
    private String                       middleName;

    @Column(name = "type_id")
    private Integer                      typeId;

    @Column(name = "npi")
    private String                       npi;

    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "provider_id", insertable = false, updatable = false)
    private Collection<ProviderLocation> providerLocation;

    @Transient
    private Provider                     original;

    public Integer getId() {
        return id;
    }

    protected void setId(Integer id) {
        if (DataBaseUtil.isDifferent(id, this.id))
            this.id = id;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        if (DataBaseUtil.isDifferent(lastName, this.lastName))
            this.lastName = lastName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        if (DataBaseUtil.isDifferent(firstName, this.firstName))
            this.firstName = firstName;
    }

    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(String middleName) {
        if (DataBaseUtil.isDifferent(middleName, this.middleName))
            this.middleName = middleName;
    }

    public Integer getTypeId() {
        return typeId;
    }

    public void setTypeId(Integer typeId) {
        if (DataBaseUtil.isDifferent(typeId, this.typeId))
            this.typeId = typeId;
    }

    public String getNpi() {
        return npi;
    }

    public void setNpi(String npi) {
        if (DataBaseUtil.isDifferent(npi, this.npi))
            this.npi = npi;
    }

    public void setClone() {
        try {
            original = (Provider)this.clone();
        } catch (Exception e) {
        }
    }

    public String getChangeXML() {
        try {
            Document doc = XMLUtil.createNew("change");
            Element root = doc.getDocumentElement();

            AuditUtil.getChangeXML(id, original.id, doc, "id");
            AuditUtil.getChangeXML(lastName, original.lastName, doc, "last_name");
            AuditUtil.getChangeXML(firstName, original.firstName, doc, "first_name");
            AuditUtil.getChangeXML(middleName, original.middleName, doc, "middle_name");
            AuditUtil.getChangeXML(typeId, original.typeId, doc, "type_id");
            AuditUtil.getChangeXML(npi, original.npi, doc, "npi");

            if (root.hasChildNodes())
                return XMLUtil.toString(doc);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public String getTableName() {
        return "provider";
    }

    public Collection<ProviderLocation> getProviderLocation() {
        return providerLocation;
    }

    public void setProviderLocation(Collection<ProviderLocation> providerLocation) {
        this.providerLocation = providerLocation;
    }
}
