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
 * Instrument Entity POJO for database
 */

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.openelis.gwt.common.Datetime;
import org.openelis.util.XMLUtil;

import java.util.Collection;
import java.util.Date;
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
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.openelis.utilcommon.DataBaseUtil;
import org.openelis.utils.AuditUtil;
import org.openelis.utils.Auditable;

@NamedQueries( {
    @NamedQuery(name = "Instrument.FetchById",
               query = "select new org.openelis.domain.InstrumentViewDO(inst.id,inst.name,inst.description,inst.modelNumber, " +
                       "inst.serialNumber,inst.typeId,inst.location,inst.isActive,inst.activeBegin,inst.activeEnd,s.id,s.name)" 
                     + " from Instrument inst left join inst.scriptlet s where inst.id = :id "),
    @NamedQuery(name = "Instrument.FetchByNameSerialNumber",
               query = "select new org.openelis.domain.InstrumentViewDO(inst.id,inst.name,inst.description,inst.modelNumber, " +
                       "inst.serialNumber,inst.typeId,inst.location,inst.isActive,inst.activeBegin,inst.activeEnd,s.id,s.name)" 
                     + " from Instrument inst left join inst.scriptlet s where inst.name = :name and inst.serialNumber = :serialNumber")
})

@Entity
@Table(name = "instrument")
@EntityListeners( {AuditUtil.class})
public class Instrument implements Auditable, Cloneable {

    @Id
    @GeneratedValue
    @Column(name = "id")
    private Integer                   id;

    @Column(name = "name")
    private String                    name;

    @Column(name = "description")
    private String                    description;

    @Column(name = "model_number")
    private String                    modelNumber;

    @Column(name = "serial_number")
    private String                    serialNumber;

    @Column(name = "type_id")
    private Integer                   typeId;

    @Column(name = "location")
    private String                    location;

    @Column(name = "is_active")
    private String                    isActive;

    @Column(name = "active_begin")
    private Date                      activeBegin;

    @Column(name = "active_end")
    private Date                      activeEnd;

    @Column(name = "scriptlet_id")
    private Integer                   scriptletId;

    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "instrument_id", insertable = false, updatable = false)
    private Collection<InstrumentLog> instrumentLog;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "scriptlet_id", insertable = false, updatable = false)
    private Scriptlet                 scriptlet;

    @Transient
    private Instrument                original;

    public Integer getId() {
        return id;
    }

    protected void setId(Integer id) {
        if (DataBaseUtil.isDifferent(id,this.id))
            this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if (DataBaseUtil.isDifferent(name,this.name))
            this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        if (DataBaseUtil.isDifferent(description,this.description))
            this.description = description;
    }

    public String getModelNumber() {
        return modelNumber;
    }

    public void setModelNumber(String modelNumber) {
        if (DataBaseUtil.isDifferent(modelNumber,this.modelNumber))
            this.modelNumber = modelNumber;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        if (DataBaseUtil.isDifferent(serialNumber,this.serialNumber))
            this.serialNumber = serialNumber;
    }

    public Integer getTypeId() {
        return typeId;
    }

    public void setTypeId(Integer typeId) {
        if (DataBaseUtil.isDifferent(typeId,this.typeId))
            this.typeId = typeId;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        if (DataBaseUtil.isDifferent(location,this.location))
            this.location = location;
    }

    public String getIsActive() {
        return isActive;
    }

    public void setIsActive(String isActive) {
        if (DataBaseUtil.isDifferent(isActive,this.isActive))
            this.isActive = isActive;
    }

    public Datetime getActiveBegin() {
        return DataBaseUtil.toYD(activeBegin);
    }

    public void setActiveBegin(Datetime activeBegin) {
        if (DataBaseUtil.isDifferentYD(activeBegin,this.activeBegin)) 
            this.activeBegin = DataBaseUtil.toDate(activeBegin);        
    }

    public Datetime getActiveEnd() {
        return DataBaseUtil.toYD(activeEnd);
    }

    public void setActiveEnd(Datetime activeEnd) {
        if (DataBaseUtil.isDifferentYD(activeEnd, this.activeEnd)) 
            this.activeEnd = DataBaseUtil.toDate(activeEnd);       
    }

    public Integer getScriptletId() {
        return scriptletId;
    }

    public void setScriptletId(Integer scriptletId) {
        if (DataBaseUtil.isDifferent(scriptletId, this.scriptletId))
            this.scriptletId = scriptletId;
    }

    public Collection<InstrumentLog> getInstrumentLog() {
        return instrumentLog;
    }

    public void setInstrumentLog(Collection<InstrumentLog> instrumentLog) {
        this.instrumentLog = instrumentLog;
    }

    public Scriptlet getScriptlet() {
        return scriptlet;
    }

    public void setScriptlet(Scriptlet scriptlet) {
        this.scriptlet = scriptlet;
    }

    public void setClone() {
        try {
            original = (Instrument)this.clone();
        } catch (Exception e) {
        }
    }

    public String getChangeXML() {
        try {
            Document doc = XMLUtil.createNew("change");
            Element root = doc.getDocumentElement();

            AuditUtil.getChangeXML(id, original.id, doc, "id");
            AuditUtil.getChangeXML(name, original.name, doc, "name");
            AuditUtil.getChangeXML(description, original.description, doc, "description");
            AuditUtil.getChangeXML(modelNumber, original.modelNumber, doc, "model_number");
            AuditUtil.getChangeXML(serialNumber, original.serialNumber, doc, "serial_number");
            AuditUtil.getChangeXML(typeId, original.typeId, doc, "type_id");
            AuditUtil.getChangeXML(location, original.location, doc, "location");
            AuditUtil.getChangeXML(isActive, original.isActive, doc, "is_active");
            AuditUtil.getChangeXML(activeBegin, original.activeBegin, doc, "active_begin");
            AuditUtil.getChangeXML(activeEnd, original.activeEnd, doc, "active_end");
            AuditUtil.getChangeXML(scriptletId, original.scriptletId, doc, "scriptlet_id");

            if (root.hasChildNodes())
                return XMLUtil.toString(doc);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public String getTableName() {
        return "instrument";
    }

}
