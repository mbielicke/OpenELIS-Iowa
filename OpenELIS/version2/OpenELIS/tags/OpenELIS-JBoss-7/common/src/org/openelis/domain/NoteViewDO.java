package org.openelis.domain;

import java.util.Date;

import org.openelis.gwt.common.DataBaseUtil;

/**
 * The class extends note DO and carries several commonly used field user name
 * The additional field is for read/display only and does not get committed to
 * the database. Note: isChanged will not reflect any changes to read/display
 * fields.
 */

public class NoteViewDO extends NoteDO {

    private static final long serialVersionUID = 1L;

    protected String          systemUser;

    public NoteViewDO() {

    }

    public NoteViewDO(Integer id, Integer referenceId, Integer referenceTable, Date timestamp,
                      String isExternal, Integer systemUserId, String subject, String text,
                      String systemUser) {
        super(id, referenceId, referenceTable, timestamp, isExternal, systemUserId, subject, text);
        setSystemUser(systemUser);
    }

    public String getSystemUser() {
        return systemUser;
    }

    public void setSystemUser(String systemUser) {
        this.systemUser = DataBaseUtil.trim(systemUser);
    }
}
