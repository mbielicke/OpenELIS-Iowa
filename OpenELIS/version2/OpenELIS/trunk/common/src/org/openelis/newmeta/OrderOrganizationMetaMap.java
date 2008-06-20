package org.openelis.newmeta;

import org.openelis.gwt.common.MetaMap;

public class OrderOrganizationMetaMap extends OrganizationMeta implements MetaMap{

    public OrderOrganizationMetaMap() {
        super();
    }
    
    public OrderOrganizationMetaMap(String path){
        super(path);
        ADDRESS = new AddressMeta(path + "address.");
    }
    
    public AddressMeta ADDRESS; 
    
    public AddressMeta getAddress() {
        return ADDRESS;
    }

    public String buildFrom(String where) {
        return "Organization ";
    }
    
    public boolean hasColumn(String name){
        if(name.startsWith(path+"address."))
            return ADDRESS.hasColumn(name);
        return super.hasColumn(name);
    }

}
