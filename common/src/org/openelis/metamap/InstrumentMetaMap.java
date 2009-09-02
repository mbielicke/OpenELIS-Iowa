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
import org.openelis.meta.InstrumentMeta;
import org.openelis.meta.ScriptletMeta;

public class InstrumentMetaMap extends InstrumentMeta implements MetaMap {

    private InstrumentLogMetaMap INSTRUMENT_LOG;
    private ScriptletMeta SCRIPTLET;
    
    public InstrumentMetaMap() {
        super("inst.");
        INSTRUMENT_LOG = new InstrumentLogMetaMap("instrumentLog.");        
        SCRIPTLET = new ScriptletMeta("inst.scriptlet.");
    }
    
    public InstrumentMetaMap(String path){
        super(path);        
        INSTRUMENT_LOG = new InstrumentLogMetaMap(path+"instrumentLog.");
        SCRIPTLET = new ScriptletMeta(path+"inst.scriptlet.");
    }
    
    public String buildFrom(String name) {
        String from = "Instrument inst ";       
        if(name.indexOf("instrumentLog.") > -1)
            from += ", IN (inst.instrumentLog) instrumentLog ";
        return from;
    }
    
    public boolean hasColumn(String name){ 
        if(name.startsWith("instrumentLog."))
            return INSTRUMENT_LOG.hasColumn(name);        
        if(name.startsWith(path+"scriptlet."))
            return SCRIPTLET.hasColumn(name);
        return super.hasColumn(name);
    }
    
    public InstrumentLogMetaMap getInstrumentLog() {
        return INSTRUMENT_LOG;
    }
    
    public ScriptletMeta getScriptlet (){
        return SCRIPTLET;
    }   
    
    public static InstrumentMetaMap getInstance() {
        return new InstrumentMetaMap();
    }

}
