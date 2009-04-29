/** Exhibit A - UIRF Open-source Based Public Software License.
* 
* The contents of this file are subject to the UIRF Open-source Based
* Public Software License(the "License"); you may not use this file except
* in compliance with the License. You may obtain a copy of the License at
* openelis.uhl.uiowa.edu
* 
* Software distributed under the License is distributed on an "AS IS"
* basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
* License for the specific language governing rights and limitations
* under the License.
* 
* The Original Code is OpenELIS code.
* 
* The Initial Developer of the Original Code is The University of Iowa.
* Portions created by The University of Iowa are Copyright 2006-2008. All
* Rights Reserved.
* 
* Contributor(s): ______________________________________.
* 
* Alternatively, the contents of this file marked
* "Separately-Licensed" may be used under the terms of a UIRF Software
* license ("UIRF Software License"), in which case the provisions of a
* UIRF Software License are applicable instead of those above. 
*/
package org.openelis.metamap;

import org.openelis.gwt.common.MetaMap;
import org.openelis.meta.OrganizationMeta;
import org.openelis.meta.SampleOrganizationMeta;

public class SampleOrganizationMetaMap extends SampleOrganizationMeta implements MetaMap{
    public SampleOrganizationMetaMap() {
        super("sampleOrg.");
        ORGANIZATION = new OrganizationMeta(path+"organization");
    }
    
    public SampleOrganizationMetaMap(String path) {
        super(path);
        ORGANIZATION = new OrganizationMeta(path+"organization");
    }
    
    public OrganizationMeta ORGANIZATION;
    
    public OrganizationMeta getOrganization(){
        return ORGANIZATION;
    }
    
    public boolean hasColumn(String name){
        /*if(name.startsWith(path+"address."))
            return ADDRESS.hasColumn(name);
        if(name.startsWith(path+"parentOrganization."))
            return PARENT_ORGANIZATION.hasColumn(name);
        if(name.startsWith("notes."))
            return NOTE.hasColumn(name);
        if(name.startsWith("contacts."))
            return ORGANIZATION_CONTACT.hasColumn(name);
            */
        return super.hasColumn(name);
    }
    
    public String buildFrom(String name){
        String from = "";
        /*Organization o ";
        if(name.indexOf("notes.") > -1)
            from += ", IN (o.note) notes ";
        if(name.indexOf("contacts.") > -1)
            from += ", IN (o.organizationContact) contacts ";
            */ 
        return from;
    }
}
