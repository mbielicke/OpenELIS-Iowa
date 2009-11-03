package org.openelis.metamap;

import org.openelis.gwt.common.MetaMap;
import org.openelis.meta.TestSectionMeta;

public class TestSectionMetaMap extends TestSectionMeta implements MetaMap {

    public String buildFrom(String where) {        
        return "TestSection ";
    }
    
    public TestSectionMetaMap() {
      super();
    }
    
    public TestSectionMetaMap(String path) {
        super(path);
    }
    
    public boolean hasColumn(String name){        
        return super.hasColumn(name);
    }
    
    public static String getTableName(){
        return "sectionTable";
    }

}
