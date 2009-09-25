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

public class WorksheetCreationMetaMap implements MetaMap {
    
    public WorksheetCreationMetaMap() {
    }
    
    public SampleMetaMap SAMPLE = new SampleMetaMap("sample.");
    
    public SampleMetaMap getSample(){
        return SAMPLE;
    }
    
    public boolean hasColumn(String columnName) {
        if(columnName.startsWith("sample."))
            return SAMPLE.hasColumn(columnName);
        if(columnName.startsWith("sampleItem."))
            return SAMPLE.SAMPLE_ITEM.hasColumn(columnName);
        if(columnName.startsWith("analysis."))
            return SAMPLE.SAMPLE_ITEM.ANALYSIS.hasColumn(columnName);
        if(columnName.startsWith("section."))
            return SAMPLE.SAMPLE_ITEM.ANALYSIS.SECTION.hasColumn(columnName);
        if(columnName.startsWith("test."))
            return SAMPLE.SAMPLE_ITEM.ANALYSIS.TEST.hasColumn(columnName);
        if(columnName.startsWith("method."))
            return SAMPLE.SAMPLE_ITEM.ANALYSIS.TEST.METHOD.hasColumn(columnName);
        return false;
    }      

    public String buildFrom(String name) {
        String from;

        from = "Sample sample "+
               ", IN (sample.sampleItem) sampleItem"+
               ", IN (sampleItem.analysis) analysis";
/*
        from = "Sample sample ";
        if (name.indexOf("sampleItem.") > -1)
            from += ", IN (sample.sampleItem) sampleItem";
        if (name.indexOf("analysis.") > -1 ||
            name.indexOf("section.") > -1 ||
            name.indexOf("test.") > -1 ||
            name.indexOf("method.") > -1) {
            if (name.indexOf("sampleItem.") == -1)
                from += ", IN (sample.sampleItem) sampleItem";
            from += ", IN (sampleItem.analysis) analysis";
        }
*/        
        return from;
    }

    public String[] getColumnList() {
        return null;
    }

    public String getEntity() {
        return null;
    }
}
