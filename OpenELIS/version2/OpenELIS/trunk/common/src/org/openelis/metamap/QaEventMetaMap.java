package org.openelis.metamap;

import org.openelis.gwt.common.MetaMap;
import org.openelis.meta.QaeventMeta;

public class QaEventMetaMap extends QaeventMeta implements MetaMap {

    public QaEventMetaMap(){
        super("qae.");
    }
    
    private QaEventTestMetaMap TEST = new QaEventTestMetaMap("qae.test.");
    
    
    public String buildFrom(String name) {                       
        return "QaEvent qae";
    }

   public static QaEventMetaMap getInstance(){
       return new QaEventMetaMap();
   }    
    

   public boolean hasColumn(String name){
       if(name.startsWith("test."))
           return TEST.hasColumn(name);
       return super.hasColumn(name); 
   }
   
   public QaEventTestMetaMap getTest() {
        return TEST;
   }

}
