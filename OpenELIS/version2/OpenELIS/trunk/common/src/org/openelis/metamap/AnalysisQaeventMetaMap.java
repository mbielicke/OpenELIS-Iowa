package org.openelis.metamap;

import org.openelis.gwt.common.MetaMap;
import org.openelis.meta.AnalysisQaeventMeta;
import org.openelis.meta.QaeventMeta;

public class AnalysisQaeventMetaMap extends AnalysisQaeventMeta implements MetaMap {

    public AnalysisQaeventMetaMap(){
        super("analysisQaEvent.");
        QA_EVENT = new QaeventMeta(path+"qaEvent.");
    }
    
    public AnalysisQaeventMetaMap(String path){
        super(path);
        QA_EVENT = new QaeventMeta(path+"qaEvent.");
    }
    
    protected QaeventMeta QA_EVENT;
    
    public QaeventMeta getQaevent(){
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