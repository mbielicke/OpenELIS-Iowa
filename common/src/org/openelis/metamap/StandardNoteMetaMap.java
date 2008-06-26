package org.openelis.metamap;

import org.openelis.gwt.common.MetaMap;
import org.openelis.meta.StandardNoteMeta;

public class StandardNoteMetaMap extends StandardNoteMeta implements MetaMap{
    
    public StandardNoteMetaMap() {
        super("standardNote.");
    }

    public boolean hasColumn(String name){
        return super.hasColumn(name);
    }
    
    public String buildFrom(String where) {
        return "StandardNote standardNote ";
    }
}
