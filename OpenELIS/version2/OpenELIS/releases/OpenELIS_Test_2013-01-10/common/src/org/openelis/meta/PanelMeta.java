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
package org.openelis.meta;

/**
  * Panel META Data
  */

import java.util.Arrays;
import java.util.HashSet;

import org.openelis.gwt.common.Meta;
import org.openelis.gwt.common.MetaMap;

public class PanelMeta implements Meta,MetaMap {
	
	private static final String ID = "_panel.id",
                                NAME = "_panel.name",
                                DESCRIPTION = "_panel.description",
                                
                                ITEM_ID = "_panelItem.id",
                                ITEM_PANEL_ID = "_panelItem.panelId",
                                ITEM_TYPE = "_panelItem.type",
                                ITEM_SORT_ORDER = "_panelItem.sortOrder",                                
                                ITEM_NAME = "_panelItem.name",
                                ITEM_METHOD_NAME = "_panelItem.methodName";

	private static HashSet<String> names;
    
    static {
        names = new HashSet<String>(Arrays.asList(ID,NAME,DESCRIPTION,ITEM_ID,
                                                  ITEM_PANEL_ID,ITEM_TYPE,ITEM_SORT_ORDER,
                                                  ITEM_NAME,ITEM_METHOD_NAME));
    }
    
    public static String getId() {
        return ID;
    } 

    public static String getName() {
        return NAME;
    } 

    public static String getDescription() {
        return DESCRIPTION;
    }
    
    public static String getItemId() {
        return ITEM_ID;
    } 

    public static String getItemPanelId() {
        return ITEM_PANEL_ID;
    } 
    
    public static String getType() {
        return ITEM_TYPE;
    } 

    public static String getItemSortOrderId() {
        return ITEM_SORT_ORDER;
    } 

    public static String getItemName() {
        return ITEM_NAME;
    } 

    public static String getItemMethodName() {
        return ITEM_METHOD_NAME;
    } 
    
    public boolean hasColumn(String columnName) {
        return names.contains(columnName);
    }
    
    public String buildFrom(String where) {
        String from;
        
        from = "Panel _panel ";
        if (where.indexOf("panelItem.") > -1)
            from += ",IN (_panel.panelItem) _panelItem ";        
        
        return from;
    } 
}   
