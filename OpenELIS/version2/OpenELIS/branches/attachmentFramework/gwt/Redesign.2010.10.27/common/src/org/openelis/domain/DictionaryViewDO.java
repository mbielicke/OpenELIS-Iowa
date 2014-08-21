package org.openelis.domain;

import org.openelis.gwt.common.DataBaseUtil;

/**
 * The class extends dictionary DO and adds a commonly used field related entry
 * name. The additional fields are for read/display only and do not get
 * committed to the database. Note: isChanged will reflect any changes to
 * read/display fields.
 */

public class DictionaryViewDO extends DictionaryDO {

    private static final long serialVersionUID = 1L;

    protected String          relatedEntryName;

    public DictionaryViewDO() {
    }

    public DictionaryViewDO(Integer id, Integer sortOrder, Integer categoryId,
                            Integer relatedEntryId, String systemName, String isActive,
                            String localAbbrev, String entry, String relatedEntryName) {
        super(id, sortOrder, categoryId, relatedEntryId, systemName, isActive, localAbbrev, entry);
        setRelatedEntryName(relatedEntryName);
    }

    public String getRelatedEntryName() {
        return relatedEntryName;
    }

    public void setRelatedEntryName(String relatedEntryName) {
        this.relatedEntryName = DataBaseUtil.trim(relatedEntryName);
    }
}
