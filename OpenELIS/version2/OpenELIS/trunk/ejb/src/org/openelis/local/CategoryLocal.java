package org.openelis.local;

import javax.ejb.Local;

@Local
public interface CategoryLocal {
    public String getSystemNameForEntryId(Integer entryId);
}
