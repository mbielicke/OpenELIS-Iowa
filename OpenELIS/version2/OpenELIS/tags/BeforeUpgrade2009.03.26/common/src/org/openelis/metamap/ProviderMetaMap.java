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