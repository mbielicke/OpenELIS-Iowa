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
import org.openelis.meta.PanelMeta;

public class PanelMetaMap extends PanelMeta implements MetaMap {

    public PanelMetaMap(){
        super("p.");
    }
            
    
    public PanelMetaMap(String path){
        super(path);                
    }
    
    private PanelItemMetaMap PANEL_ITEM = new PanelItemMetaMap("panelItem.");

    public boolean hasColumn(String name){                
        if(name.startsWith("panelItem."))
            return PANEL_ITEM.hasColumn(name);
        return super.hasColumn(name);
    }
    
    public String buildFrom(String name) {                                
       String from = "Panel p ";
       if(name.indexOf("panelItem.") > -1)
        from += ", IN (p.panelItem) panelItem ";
       return from;
    }

    public PanelItemMetaMap getPanelItem(){
        return PANEL_ITEM;
    }       
}
