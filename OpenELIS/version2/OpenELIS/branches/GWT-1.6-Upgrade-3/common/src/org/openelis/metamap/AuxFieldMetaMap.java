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
import org.openelis.meta.AnalyteMeta;
import org.openelis.meta.AuxFieldMeta;
import org.openelis.meta.MethodMeta;

public class AuxFieldMetaMap extends AuxFieldMeta implements MetaMap {

    private AuxFieldValueMetaMap AUX_FIELD_VALUE = new AuxFieldValueMetaMap("auxFieldValue.");
    
    private MethodMeta METHOD = new MethodMeta("auxField.method.");
    
    private AnalyteMeta ANALYTE = new AnalyteMeta("auxField.analyte.");
    
    public AuxFieldMetaMap() {
        super("auxField.");
    }
    
    public AuxFieldMetaMap(String path){
        super(path);               
    }
    
    public String buildFrom(String name) {        
     String from = "AuxField auxField";
     if(name.indexOf("auxFieldValue.") > -1)
         from += ", IN (auxField.auxFieldValue) auxFieldValue ";
     return from;
    }
    
    public AnalyteMeta getAnalyte(){
        return ANALYTE;
    }
    
    public MethodMeta getMethod(){
        return METHOD;
    }
    
    public static AuxFieldMetaMap getInstance(){
        return new AuxFieldMetaMap();
    }
    
    public AuxFieldValueMetaMap getAuxFieldValue(){
       return AUX_FIELD_VALUE;
    }
    
    public boolean hasColumn(String name){        
        if(name.startsWith("auxFieldValue."))
            return AUX_FIELD_VALUE.hasColumn(name);
        if(name.startsWith(path+"method."))
            return METHOD.hasColumn(name);
        if(name.startsWith(path+"analyte."))
            return ANALYTE.hasColumn(name);
        return super.hasColumn(name);
    }
    
    public static String getTableName() {
        return "AuxField";        
    }
}