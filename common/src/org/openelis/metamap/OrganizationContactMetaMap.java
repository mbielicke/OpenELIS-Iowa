package org.openelis.metamap;

import org.openelis.gwt.common.MetaMap;
import org.openelis.meta.AddressMeta;
import org.openelis.meta.OrganizationContactMeta;

public class OrganizationContactMetaMap extends OrganizationContactMeta implements
                                                                       MetaMap {
    
    public OrganizationContactMetaMap() {
        super();
    }
    
    public OrganizationContactMetaMap(String path){
        super(path);
        ADDRESS = new AddressMeta(path + "address.");
    }
    
    public AddressMeta ADDRESS; 
    
    public AddressMeta getAddress() {
        return ADDRESS;
    }

    public String buildFrom(String where) {
        return "OrganizationContact ";
    }
    
    public boolean hasColumn(String name){
        if(name.startsWith(path+"address."))
            return ADDRESS.hasColumn(name);
        return super.hasColumn(name);
    }

}
