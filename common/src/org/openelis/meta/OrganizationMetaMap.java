package org.openelis.meta;

import org.openelis.gwt.common.MetaMap;

public class OrganizationMetaMap extends OrganizationMeta implements MetaMap{
    
    public OrganizationMetaMap() {
        super("o.");
    }
    
    public AddressMeta ADDRESS = new AddressMeta("o.address.");
    
    public OrganizationMeta PARENT_ORGANIZATION = new OrganizationMeta("o.parentOrganization.");
    
    public OrganizationContactMetaMap ORGANIZATION_CONTACT = new OrganizationContactMetaMap("contacts.");
    
    public NoteMeta NOTE = new NoteMeta("notes.");

    public AddressMeta getAddress() {
        return ADDRESS;
    }

    public NoteMeta getNote() {
        return NOTE;
    }

    public OrganizationContactMetaMap getOrganizationContact() {
        return ORGANIZATION_CONTACT;
    }

    public OrganizationMeta getParentOrganization() {
        return PARENT_ORGANIZATION;
    }
    
    public static OrganizationMetaMap getInstance() {
        return new OrganizationMetaMap();
    }
    
    public boolean hasColumn(String name){
        if(name.startsWith("o.address."))
            return ADDRESS.hasColumn(name);
        if(name.startsWith("o.parentOrganization."))
            return PARENT_ORGANIZATION.hasColumn(name);
        if(name.startsWith("notes."))
            return NOTE.hasColumn(name);
        if(name.startsWith("contacts."))
            return ORGANIZATION_CONTACT.hasColumn(name);
        return super.hasColumn(name);
    }
    
    public String buildFrom(String name){
        String from = "Organization o ";
        if(name.indexOf("notes.") > -1)
            from += ", IN (o.note) notes ";
        if(name.indexOf("contacts.") > -1)
            from += ", IN (o.organizationContact) contacts "; 
        return from;
    }
}
