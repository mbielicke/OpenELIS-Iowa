package org.openelis.metamap;

import org.openelis.gwt.common.MetaMap;
import org.openelis.meta.TestResultMeta;

public class TestResultMetaMap extends TestResultMeta implements MetaMap {

    public String buildFrom(String where) {        
        return "TestResult ";
    }
    
    private static final String tableName = "TestResult";

    public TestResultMetaMap() {
      super();  
    }
    
    public TestResultMetaMap(String path){
        super(path);        
    }
    
    public boolean hasColumn(String name){        
        return super.hasColumn(name);
    }

    public static String getTableName() {
        return tableName;
    }
}
