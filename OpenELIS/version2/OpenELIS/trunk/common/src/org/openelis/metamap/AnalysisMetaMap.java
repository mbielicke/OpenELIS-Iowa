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
import org.openelis.meta.AnalysisMeta;
import org.openelis.meta.SectionMeta;

public class AnalysisMetaMap extends AnalysisMeta implements MetaMap {
    
    public AnalysisMetaMap(){
        super("analysis.");
        SECTION = new SectionMeta();
        TEST = new TestMetaMap();
        ANALYSIS_QA_EVENT = new AnalysisQaeventMetaMap();
    }
    
    public AnalysisMetaMap(String path){
        super(path);
        SECTION = new SectionMeta(path+"section.");
        TEST = new TestMetaMap(path+"test.");
        ANALYSIS_QA_EVENT = new AnalysisQaeventMetaMap();
    }
    
    public TestMetaMap TEST;
    public SectionMeta SECTION;
    public AnalysisQaeventMetaMap ANALYSIS_QA_EVENT;
    
    public TestMetaMap getTest(){
        return TEST;
    }
    
    public SectionMeta getSection(){
        return SECTION;
    }
    
    public AnalysisQaeventMetaMap getAnalysisQaevent(){
        return ANALYSIS_QA_EVENT;
    }
    
    public boolean hasColumn(String name){
        if(name.startsWith(path+"test."))
            return TEST.hasColumn(name);
        else if(name.startsWith(path+"section."))
            return SECTION.hasColumn(name);  
        else if(name.startsWith(path+"analysisQAEvent"))
            return ANALYSIS_QA_EVENT.hasColumn(name);
        return super.hasColumn(name);
    }
    
    public String buildFrom(String name){
        return "analysis";
    }
}
