package org.openelis.metamap;

import org.openelis.gwt.common.MetaMap;
import org.openelis.meta.SystemVariableMeta;

public class SystemVariableMetaMap extends SystemVariableMeta implements
                                                             MetaMap {
     public SystemVariableMetaMap(){
         super("sv.");
     }
     
     public static SystemVariableMetaMap getInstance(){
         return new SystemVariableMetaMap();
     }
    
    public String buildFrom(String where) {
        return "SystemVariable sv";
    }
    
    public boolean hasColumn(String name){        
        return super.hasColumn(name);
    }

}
