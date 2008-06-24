package org.openelis.newmeta;

import org.openelis.gwt.common.MetaMap;

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
