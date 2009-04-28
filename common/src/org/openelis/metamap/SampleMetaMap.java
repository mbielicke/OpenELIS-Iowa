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
import org.openelis.meta.SampleMeta;

public class SampleMetaMap extends SampleMeta implements MetaMap {
    
    public SampleMetaMap() {
        super("sample.");
        SAMPLE_ITEM = new SampleItemMetaMap(path+"sampleItem.");
        SAMPLE_ORGANIZATION = new SampleOrganizationMetaMap(path+"sampleOrganization.");
        SAMPLE_PROJECT = new SampleProjectMetaMap(path+"sampleProject.");
    }
    
    public SampleMetaMap(String path) {
        super(path);
        
        SAMPLE_ITEM = new SampleItemMetaMap(path+"sampleItem.");
        SAMPLE_ORGANIZATION = new SampleOrganizationMetaMap(path+"sampleOrganization.");
        SAMPLE_PROJECT = new SampleProjectMetaMap(path+"sampleProject.");
    }
    
    public SampleItemMetaMap SAMPLE_ITEM;
    
    public SampleOrganizationMetaMap SAMPLE_ORGANIZATION;
    
    public SampleProjectMetaMap SAMPLE_PROJECT;
    
    public SampleItemMetaMap getSampleItem(){
        return SAMPLE_ITEM;
    }
    
    public SampleOrganizationMetaMap getSampleOrganization(){
        return SAMPLE_ORGANIZATION;
    }
    
    public SampleProjectMetaMap getSampleProject(){
        return SAMPLE_PROJECT;
    }
    
    public String buildFrom(String where) {
        return "sample";
    }

    public boolean hasColumn(String columnName) {
        if(columnName.startsWith(path+"sampleItem."))
            return SAMPLE_ITEM.hasColumn(columnName);
        if(columnName.startsWith(path+"sampleOrganization."))
            return SAMPLE_ORGANIZATION.hasColumn(columnName);
        if(columnName.startsWith(path+"sampleProject."))
            return SAMPLE_PROJECT.hasColumn(columnName);
        return super.hasColumn(columnName);
    }      

}
