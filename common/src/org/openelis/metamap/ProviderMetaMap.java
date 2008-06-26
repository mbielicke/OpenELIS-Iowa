package org.openelis.metamap;

import org.openelis.gwt.common.MetaMap;
import org.openelis.meta.NoteMeta;
import org.openelis.meta.ProviderMeta;

public class ProviderMetaMap extends ProviderMeta implements MetaMap {
 
    public ProviderMetaMap(){
        super("p.");
    }
    
    private ProviderAddressMetaMap PROVIDER_ADDRESS = new ProviderAddressMetaMap("locations."); 
    
    private NoteMeta NOTE = new NoteMeta("notes.");
    
    public String buildFrom(String name) {
        String from = "Provider p ";
        if(name.indexOf("notes.") > -1)
            from += ", IN (p.provNote) notes ";
        if(name.indexOf("locations.") > -1)
            from += ", IN (p.providerAddress) locations "; 
        return from;
    }
    
    public ProviderAddressMetaMap getProviderAddress(){
        return PROVIDER_ADDRESS;
    }
    
    public NoteMeta getNote() {
        return NOTE;
    }
    
    public boolean hasColumn(String name){        
        if(name.startsWith("notes."))
            return NOTE.hasColumn(name);
        if(name.startsWith("locations."))
            return PROVIDER_ADDRESS.hasColumn(name);
        return super.hasColumn(name);
    }

}
