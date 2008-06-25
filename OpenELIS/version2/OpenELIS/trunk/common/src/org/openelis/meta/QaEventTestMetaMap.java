package org.openelis.meta;

import org.openelis.gwt.common.MetaMap;

public class QaEventTestMetaMap extends TestMeta implements MetaMap {

    public QaEventTestMetaMap(){
        super();
    }
    
    public QaEventTestMetaMap(String path){
        super(path);
        METHOD = new MethodMeta(path+"method.");
    }
    
    private MethodMeta METHOD;
    
    public MethodMeta getMethod(){
        return METHOD;
    }
    
    public String buildFrom(String where) {
        return "test ";
    }
    
    public boolean hasColumn(String name){
        if(name.startsWith(path+"method."))
            return METHOD.hasColumn(name);
        return super.hasColumn(name);
    }

}
