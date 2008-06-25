package org.openelis.meta;

import org.openelis.gwt.common.MetaMap;


public class AnalyteMetaMap extends AnalyteMeta implements MetaMap {

    public AnalyteMetaMap() {
        super();
        PARENT_ANALYTE = new AnalyteMeta("parentAnalyte.");
    }
    
    public AnalyteMetaMap(String path){
        super(path);
        PARENT_ANALYTE = new AnalyteMeta(path+"parentAnalyte.");
    }
    
    public AnalyteMeta PARENT_ANALYTE; 
    
    public boolean hasColumn(String name){
        if(name.startsWith(path+"parentAnalyte."))
            return PARENT_ANALYTE.hasColumn(name);
        return super.hasColumn(name);
    }
    
    public String buildFrom(String where) {
        // TODO Auto-generated method stub
        return "Analyte ";
    }
    
    public AnalyteMeta getParentAnalyte(){
        return PARENT_ANALYTE;
    }

}
