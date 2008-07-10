/**
* The contents of this file are subject to the Mozilla Public License
* Version 1.1 (the "License"); you may not use this file except in
* compliance with the License. You may obtain a copy of the License at
* http://www.mozilla.org/MPL/
* 
* Software distributed under the License is distributed on an "AS IS"
* basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
* License for the specific language governing rights and limitations under
* the License.
* 
* The Original Code is OpenELIS code.
* 
* Copyright (C) The University of Iowa.  All Rights Reserved.
*/
package org.openelis.metamap;

import org.openelis.gwt.common.MetaMap;
import org.openelis.meta.AddressMeta;
import org.openelis.meta.NoteMeta;
import org.openelis.meta.OrganizationMeta;

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
