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
 * TestWorksheetItem Entity POJO for database
 */

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.openelis.domain.Constants;
import org.openelis.gwt.common.DataBaseUtil;
import org.openelis.utils.Audit;
import org.openelis.utils.AuditUtil;
import org.openelis.utils.Auditable;

@NamedQuery(name = "TestWorksheetItem.FetchByTestWorksheetId",
            query = "select distinct new org.openelis.domain.TestWorksheetItemDO(twi.id,twi.testWorksheetId,twi.sortOrder,twi.position,twi.typeId,twi.qcName)"
                  + " from TestWorksheetItem twi where twi.testWorksheetId = :testWorksheetId order by twi.sortOrder")
@Entity
@Table(name = "test_worksheet_item")
@EntityListeners({AuditUtil.class})
public class TestWorksheetItem implements Auditable, Cloneable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer           id;

    @Column(name = "test_worksheet_id")
    private Integer           testWorksheetId;

    @Column(name = "sort_order")
    private Integer           sortOrder;

    @Column(name = "position")
    private Integer           position;

    @Column(name = "type_id")
    private Integer           typeId;

    @Column(name = "qc_name")
    private String            qcName;

    @Transient
    private TestWorksheetItem original;

    public Integer getId() {
        return id;
    }

    protected void setId(Integer id) {
        if (DataBaseUtil.isDifferent(id, this.id))
            this.id = id;
    }

    public Integer getTestWorksheetId() {
        return testWorksheetId;
    }

    public void setTestWorksheetId(Integer testWorksheetId) {
        if (DataBaseUtil.isDifferent(testWorksheetId, this.testWorksheetId))
            this.testWorksheetId = testWorksheetId;
    }

    public Integer getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(Integer sortOrder) {
        if (DataBaseUtil.isDifferent(sortOrder, this.sortOrder))
            this.sortOrder = sortOrder;
    }

    public Integer getPosition() {
        return position;
    }

    public void setPosition(Integer position) {
        if (DataBaseUtil.isDifferent(position, this.position))
            this.position = position;
    }

    public Integer getTypeId() {
        return typeId;
    }

    public void setTypeId(Integer typeId) {
        if (DataBaseUtil.isDifferent(typeId, this.typeId))
            this.typeId = typeId;
    }

    public String getQcName() {
        return qcName;
    }

    public void setQcName(String qcName) {
        if (DataBaseUtil.isDifferent(qcName, this.qcName))
            this.qcName = qcName;
    }

    public void setClone() {
        try {
            original = (TestWorksheetItem)this.clone();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Audit getAudit(Integer activity) {
        Audit audit;

        audit = new Audit(activity);
        audit.setReferenceTableId(Constants.table().TEST_WORKSHEET_ITEM);
        audit.setReferenceId(getId());
        if (original != null)
            audit.setField("id", id, original.id)
                 .setField("test_worksheet_id", testWorksheetId, original.testWorksheetId)
                 .setField("sort_order", sortOrder, original.sortOrder)
                 .setField("position", position, original.position)
                 .setField("type_id", typeId, original.typeId, Constants.table().DICTIONARY)
                 .setField("qc_name", qcName, original.qcName);

        return audit;
    }
}
