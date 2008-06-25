package org.openelis.newmeta;

import org.openelis.gwt.common.MetaMap;

public class ProviderAddressMetaMap extends ProviderAddressMeta implements
                                                               MetaMap {
    public ProviderAddressMetaMap(){
        super();
    }
    
    public ProviderAddressMetaMap(String path){
        super(path);
        ADDRESS = new AddressMeta(path + "address.");
    }
    
    public AddressMeta ADDRESS; 
    
    public AddressMeta getAddress() {
        return ADDRESS;
    }
    
    public String buildFrom(String where) {        
        return "ProviderAddress ";
    }
    
    public boolean hasColumn(String name){
        if(name.startsWith(path+"address."))
            return ADDRESS.hasColumn(name);
        return super.hasColumn(name);
    }

}
