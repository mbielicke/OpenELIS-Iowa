/**
* The contents of this file are subject to the Mozilla Public License
* Version 1.1 (the "License"); you may not use this file except in
* compliance with the License. You may obtain a copy of the License at
* http://www.mozilla.org/MPL/
* 
* Software distributed under the License is distributed on an "AS IS"
* basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
* License for the specific language governing rights and limitations under
* the License.
* 
* The Original Code is OpenELIS code.
* 
* Copyright (C) The University of Iowa.  All Rights Reserved.
*/
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
