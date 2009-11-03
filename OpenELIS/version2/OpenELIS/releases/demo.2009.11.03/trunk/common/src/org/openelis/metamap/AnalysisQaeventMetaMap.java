package org.openelis.metamap;

import org.openelis.gwt.common.MetaMap;
import org.openelis.meta.AnalysisQaeventMeta;
import org.openelis.meta.QaEventMeta;

public class AnalysisQaeventMetaMap extends AnalysisQaeventMeta implements MetaMap {

    public AnalysisQaeventMetaMap(){
        super("analysisQaEvent.");
        QA_EVENT = new QaEventMeta(path+"qaEvent.");
    }
    
    public AnalysisQaeventMetaMap(String path){
        super(path);
        QA_EVENT = new QaEventMeta(path+"qaEvent.");
    }
    
    protected QaEventMeta QA_EVENT;
    
    public QaEventMeta getQaevent(){
        return QA_EVENT;
    }
    
    public String buildFrom(String where) {
        return "analysisqa";
    }
    
    public boolean hasColumn(String columnName) {
        if(columnName.startsWith(path+"qaEvent."))
            return QA_EVENT.hasColumn(columnName);
        return super.hasColumn(columnName);
    }
}