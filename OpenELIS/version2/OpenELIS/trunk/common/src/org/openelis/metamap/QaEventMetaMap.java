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
import org.openelis.meta.QaeventMeta;
import org.openelis.meta.TestMeta;

public class QaEventMetaMap extends QaeventMeta implements MetaMap {

    private QaEventTestMetaMap QAEVENT_TEST;
    
    private TestMeta TEST;
    
    public QaEventMetaMap(){
        super("qae.");
        QAEVENT_TEST = new QaEventTestMetaMap(path+"test.");
        TEST = new TestMeta("qae.test."); 
    }
    
    public QaEventMetaMap(String path){
        super(path); 
        QAEVENT_TEST = new QaEventTestMetaMap(path+"test.");
        TEST = new TestMeta(path+"qae.test.");
    }       
    
    public String buildFrom(String name) {                       
        return "QaEvent qae";
    }

   public static QaEventMetaMap getInstance(){
       return new QaEventMetaMap();
   }    
    

   public boolean hasColumn(String name){
       if(name.startsWith("test."))
           return QAEVENT_TEST.hasColumn(name);
       if(name.startsWith(path+"test."))
           return TEST.hasColumn(name);
       return super.hasColumn(name); 
   }
   
   public QaEventTestMetaMap getQaEventTest() {
        return QAEVENT_TEST;
   }
   
   public TestMeta getTest() {
       return TEST;
  }

}
