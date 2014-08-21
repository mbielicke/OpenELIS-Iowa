package org.openelis.metamap;

import org.openelis.gwt.common.MetaMap;
import org.openelis.meta.AuxFieldGroupMeta;

public class AuxFieldGroupMetaMap extends AuxFieldGroupMeta implements MetaMap{

    private AuxFieldMetaMap AUX_FIELD = new AuxFieldMetaMap("auxField.");
    
    public AuxFieldGroupMetaMap() {
      super("afg.");
    }
    
    public AuxFieldGroupMetaMap(String path){
        super(path);               
    }       
    
    public String buildFrom(String name) {
        String from = "AuxFieldGroup afg ";
        if(name.indexOf("auxField.") > -1)
            from += ", IN (afg.auxField) auxField ";
        return from;
    }
    
    public AuxFieldMetaMap getAuxField() {
        return AUX_FIELD;
    }
    
    public boolean hasColumn(String name){        
        if(name.startsWith("auxField."))
            return AUX_FIELD.hasColumn(name);
        return super.hasColumn(name);
    }

}
