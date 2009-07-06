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
import org.openelis.meta.SampleHumanMeta;

public class SampleHumanMetaMap extends SampleHumanMeta implements MetaMap {
    
    public SampleHumanMetaMap() {
        super("sh.");
    }
    
    public SampleHumanMetaMap(String path){
        super(path);
    }
    
    public SampleMetaMap SAMPLE = new SampleMetaMap("sample.");
    
    public PatientMetaMap PATIENT = new PatientMetaMap("patient."); 
    
    public ProviderMetaMap PROVIDER = new ProviderMetaMap("provider.");
    
    public PatientMetaMap getPatient() {
        return PATIENT;
    }
    
    public ProviderMetaMap getProvider() {
        return PROVIDER;
    }
    
    public SampleMetaMap getSample() {
        return SAMPLE;
    }

    public String buildFrom(String where) {       
        String from = "SampleHuman sh ";
        from += ", IN (sh.sample) sample ";
        
        //if(where.indexOf("provider.") > -1)
            from += ", IN (sh.provider) provider ";
        //if(where.indexOf("patient.") > -1)
            from += ", IN (sh.patient) patient ";
            
        return from;
    }
    
    public boolean hasColumn(String name){        
        if(name.startsWith("patient."))
            return PATIENT.hasColumn(name); 
        if(name.startsWith("provider."))
            return PROVIDER.hasColumn(name);
        return super.hasColumn(name);
    }

}
