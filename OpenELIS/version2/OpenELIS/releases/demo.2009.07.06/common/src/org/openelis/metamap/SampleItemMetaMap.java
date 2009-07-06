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
import org.openelis.meta.SampleItemMeta;

public class SampleItemMetaMap extends SampleItemMeta implements MetaMap {

    private boolean parent = false;
    
    public SampleItemMetaMap(){
        super("sampleItem.");
        PARENT_SAMPLE_ITEM = new SampleItemMetaMap("parentStorageItem.");
        ANALYSIS = new AnalysisMetaMap("a.");
    }
    
    public SampleItemMetaMap(String path){
        this(path, false);
    }
    
    public SampleItemMetaMap(String path, boolean parent){
        super(path);
        ANALYSIS = new AnalysisMetaMap("a.");
        this.parent = parent;
        if(!parent)
            PARENT_SAMPLE_ITEM = new SampleItemMetaMap(path+"parentSampleItem.", true);
            
    }
    
    public SampleItemMetaMap PARENT_SAMPLE_ITEM;
    public AnalysisMetaMap   ANALYSIS;
    
    public SampleItemMetaMap getParentSampleItem(){
        return PARENT_SAMPLE_ITEM;
    }
    
    public AnalysisMetaMap getAnalysis(){
        return ANALYSIS;
    }
    
    public boolean hasColumn(String name){
        if(name.startsWith(path+"parentSampleItem."))
            return PARENT_SAMPLE_ITEM.hasColumn(name);  
        if(name.startsWith(path+"a."))
            return ANALYSIS.hasColumn(name);
        return super.hasColumn(name);
    }
    
    public String buildFrom(String name){
        return "sampleItem";
    }
}
