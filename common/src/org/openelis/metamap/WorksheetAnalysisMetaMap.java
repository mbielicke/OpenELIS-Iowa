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
import org.openelis.meta.QcMeta;
import org.openelis.meta.WorksheetAnalysisMeta;

public class WorksheetAnalysisMetaMap extends WorksheetAnalysisMeta implements MetaMap {
    
    public QcMeta                QC;
    public AnalysisMetaMap       ANALYSIS;
    public WorksheetAnalysisMeta RELATED_WORKSHEET_ANALYSIS;
    
    public WorksheetAnalysisMetaMap() {
        super("wa.");
        QC                         = new QcMeta();
        ANALYSIS                   = new AnalysisMetaMap();
        RELATED_WORKSHEET_ANALYSIS = new WorksheetAnalysisMeta();
    }
    
    public WorksheetAnalysisMetaMap(String path) {
        super(path);
        QC       = new QcMeta();
        ANALYSIS = new AnalysisMetaMap(path+"analysis.");
        RELATED_WORKSHEET_ANALYSIS = new WorksheetAnalysisMeta(path+"relatedAnalysis.");
    }
    
    public static WorksheetAnalysisMetaMap getInstance() {
        return new WorksheetAnalysisMetaMap();
    }
    
    public QcMeta getQc() {
        return QC;
    }
    
    public AnalysisMetaMap getAnalysis() {
        return ANALYSIS;
    }
    
    public WorksheetAnalysisMeta getRelatedWorksheetAnalysis() {
        return RELATED_WORKSHEET_ANALYSIS;
    }
    
    public boolean hasColumn(String columnName) {
        if (columnName.startsWith(path+"qc."))
            return QC.hasColumn(columnName);
        if (columnName.startsWith(path+"analysis."))
            return ANALYSIS.hasColumn(columnName);
        if (columnName.startsWith(path+"relatedWorksheetAnalysis."))
            return RELATED_WORKSHEET_ANALYSIS.hasColumn(columnName);
        return super.hasColumn(columnName);
    }      

    public String buildFrom(String name) {
        String from;
        
        from = "WorksheetAnalysis wa ";
        if (name.indexOf("qc.") > -1)
            from += ", IN (wa.qc) qc ";
        if (name.indexOf("analysis.") > -1)
            from += ", IN (wa.analysis) analysis ";

        return from;
    }
}
