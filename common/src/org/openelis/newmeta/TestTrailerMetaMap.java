package org.openelis.newmeta;

import org.openelis.gwt.common.MetaMap;

public class TestTrailerMetaMap extends TestTrailerMeta implements MetaMap{

    public TestTrailerMetaMap() {
        super("testTrailer.");
    }

    public boolean hasColumn(String name){
        return super.hasColumn(name);
    }
    
    public String buildFrom(String where) {
        return "TestTrailer testTrailer ";
    }
}
