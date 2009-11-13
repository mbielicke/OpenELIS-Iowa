package org.openelis.metamap;

import org.openelis.gwt.common.MetaMap;
import org.openelis.meta.QaEventMeta;
import org.openelis.meta.SampleQaeventMeta;

public class SampleQaeventMetaMap extends SampleQaeventMeta implements MetaMap {

    public SampleQaeventMetaMap(){
        super("sampleQaEvent.");
        QA_EVENT = new QaEventMeta(path+"qaEvent.");
    }
    
    public SampleQaeventMetaMap(String path){
        super(path);
        QA_EVENT = new QaEventMeta(path+"qaEvent.");
    }
    
    protected QaEventMeta QA_EVENT;
    
    public QaEventMeta getQaevent(){
        return QA_EVENT;
    }
    
    public String buildFrom(String where) {
        return "sampleqa";
    }
    
    public boolean hasColumn(String columnName) {
        if(columnName.startsWith(path+"qaEvent."))
            return QA_EVENT.hasColumn(columnName);
        return super.hasColumn(columnName);
    }
}
