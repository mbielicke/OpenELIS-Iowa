package org.openelis.entity;

/**
 * TestSection Entity POJO for database
 */

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.openelis.domain.ReferenceTable;
import org.openelis.gwt.common.DataBaseUtil;
import org.openelis.utils.Audit;
import org.openelis.utils.AuditUtil;
import org.openelis.utils.Auditable;

@NamedQuery( name = "TestSection.FetchByTestId", 
            query = "select distinct new org.openelis.domain.TestSectionViewDO(ts.id,ts.testId,ts.sectionId,ts.flagId,s.name)"
                  + " from TestSection ts left join ts.section s where ts.testId = :testId")
@Entity
@Table(name = "test_section")
@EntityListeners( {AuditUtil.class})
public class TestSection implements Auditable, Cloneable {

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer     id;

    @Column(name = "test_id")
    private Integer     testId;

    @Column(name = "section_id")
    private Integer     sectionId;

    @Column(name = "flag_id")
    private Integer     flagId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "section_id", insertable = false, updatable = false)
    private Section     section;

    @Transient
    private TestSection original;

    public Integer getId() {
        return id;
    }

    protected void setId(Integer id) {
        if (DataBaseUtil.isDifferent(id, this.id))
            this.id = id;
    }

    public Integer getTestId() {
        return testId;
    }

    public void setTestId(Integer testId) {
        if (DataBaseUtil.isDifferent(testId, this.testId))
            this.testId = testId;
    }

    public Integer getSectionId() {
        return sectionId;
    }

    public void setSectionId(Integer sectionId) {
        if (DataBaseUtil.isDifferent(sectionId, this.sectionId))
            this.sectionId = sectionId;
    }

    public Integer getFlagId() {
        return flagId;
    }

    public void setFlagId(Integer flagId) {
        if (DataBaseUtil.isDifferent(flagId, this.flagId))
            this.flagId = flagId;
    }
    
    public Section getSection() {
        return section;
    }

    public void setSection(Section section) {
        this.section = section;
    }

    public void setClone() {
        try {
            original = (TestSection)this.clone();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Audit getAudit() {
        Audit audit;

        audit = new Audit();
        audit.setReferenceTableId(ReferenceTable.TEST_SECTION);
        audit.setReferenceId(getId());
        if (original != null)
            audit.setField("id", id, original.id)
                 .setField("test_id", testId, original.testId)
                 .setField("section_id", sectionId, original.sectionId, ReferenceTable.SECTION)
                 .setField("flag_id", flagId, original.flagId, ReferenceTable.DICTIONARY);

        return audit;
    }

}
