package org.openelis.metamap;

import org.openelis.gwt.common.MetaMap;
import org.openelis.meta.TestMeta;

public class TestMetaMap extends TestMeta implements MetaMap {

    public TestMetaMap(){
        super();
    }
    
    public String buildFrom(String where) {        
        return "Test";
    }
    
    public TestMetaMap(String path){
        super(path);
    }
    
    public boolean hasColumn(String name){        
      return super.hasColumn(name);
    }
}
