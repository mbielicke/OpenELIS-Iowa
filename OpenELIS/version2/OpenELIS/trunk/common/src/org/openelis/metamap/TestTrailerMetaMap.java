package org.openelis.metamap;

import org.openelis.gwt.common.MetaMap;
import org.openelis.meta.TestTrailerMeta;

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
