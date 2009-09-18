package org.openelis.domain;

import org.openelis.utilcommon.DataBaseUtil;

/**
 * The class extends test section DO and carries section. The additional field
 * is for read/display only and does not get committed to the database. Note:
 * isChanged will not reflect any changes to read/display fields.
 */

public class TestSectionViewDO extends TestSectionDO {

    private static final long serialVersionUID = 1L;

    protected String section;
    
    public TestSectionViewDO() {
    }

    public TestSectionViewDO(Integer id, Integer testId, Integer sectionId, Integer flagId, String section) {
        super(id, testId, sectionId, flagId);
        setSection(section);
    }

    public String getSection() {
        return section;
    }

    public void setSection(String section) {
        this.section = DataBaseUtil.trim(section);
    }
}
