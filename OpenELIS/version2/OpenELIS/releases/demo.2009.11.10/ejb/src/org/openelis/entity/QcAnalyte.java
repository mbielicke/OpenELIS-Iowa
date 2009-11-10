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
 * QcAnalyte Entity POJO for database
 */

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.openelis.util.XMLUtil;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.openelis.utilcommon.DataBaseUtil;
import org.openelis.utils.AuditUtil;
import org.openelis.utils.Auditable;

@NamedQueries({
    @NamedQuery( name = "QcAnalyte.FetchByQcId",
                query = "select new org.openelis.domain.QcAnalyteViewDO(qca.id,qca.qcId,qca.analyteId," +
                		"qca.typeId,qca.value,qca.isTrendable,a.name,'')"
                      + " from QcAnalyte qca left join qca.analyte a where qca.qcId = :id")})
@Entity
@Table(name = "qc_analyte")
@EntityListeners( {AuditUtil.class})
public class QcAnalyte implements Auditable, Cloneable {

    @Id
    @GeneratedValue
    @Column(name = "id")
    private Integer   id;

    @Column(name = "qc_id")
    private Integer   qcId;

    @Column(name = "analyte_id")
    private Integer   analyteId;

    @Column(name = "type_id")
    private Integer   typeId;

    @Column(name = "value")
    private String    value;

    @Column(name = "is_trendable")
    private String    isTrendable;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "analyte_id", insertable = false, updatable = false)
    private Analyte   analyte;

    @Transient
    private QcAnalyte original;

    public Integer getId() {
        return id;
    }

    protected void setId(Integer id) {
        if (DataBaseUtil.isDifferent(id, this.id))
            this.id = id;
    }

    public Integer getQcId() {
        return qcId;
    }

    public void setQcId(Integer qcId) {
        if (DataBaseUtil.isDifferent(qcId, this.qcId))
            this.qcId = qcId;
    }

    public Integer getAnalyteId() {
        return analyteId;
    }

    public void setAnalyteId(Integer analyteId) {
        if (DataBaseUtil.isDifferent(analyteId, this.analyteId))
            this.analyteId = analyteId;
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

    public String getIsTrendable() {
        return isTrendable;
    }

    public void setIsTrendable(String isTrendable) {
        if (DataBaseUtil.isDifferent(isTrendable, this.isTrendable))
            this.isTrendable = isTrendable;
    }

    public Analyte getAnalyte() {
        return analyte;
    }

    public void setAnalyte(Analyte analyte) {
        this.analyte = analyte;
    }

    public void setClone() {
        try {
            original = (QcAnalyte)this.clone();
        } catch (Exception e) {
        }
    }

    public String getChangeXML() {
        try {
            Document doc = XMLUtil.createNew("change");
            Element root = doc.getDocumentElement();

            AuditUtil.getChangeXML(id, original.id, doc, "id");
            AuditUtil.getChangeXML(qcId, original.qcId, doc, "qc_id");
            AuditUtil.getChangeXML(analyteId, original.analyteId, doc, "analyte_id");
            AuditUtil.getChangeXML(typeId, original.typeId, doc, "type_id");
            AuditUtil.getChangeXML(value, original.value, doc, "value");
            AuditUtil.getChangeXML(isTrendable, original.isTrendable, doc, "is_trendable");

            if (root.hasChildNodes())
                return XMLUtil.toString(doc);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public String getTableName() {
        return "qc_analyte";
    }
}
