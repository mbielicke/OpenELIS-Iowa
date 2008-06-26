package org.openelis.metamap;

import org.openelis.gwt.common.MetaMap;
import org.openelis.meta.LabelMeta;

public class LabelMetaMap extends LabelMeta implements MetaMap {

    public LabelMetaMap(){
        super("l.");
    } 
    
    public boolean hasColumn(String name){
        return super.hasColumn(name); 
    }
    
    public static LabelMetaMap getInstance(){
      return new LabelMetaMap();
    }
    
    public String buildFrom(String where) {        
        return "Label l";
    }

}
