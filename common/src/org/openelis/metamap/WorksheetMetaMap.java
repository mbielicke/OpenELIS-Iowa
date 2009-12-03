/**
 * Exhibit A - UIRF Open-source Based Public Software License.
 * 
 * The contents of this file are subject to the UIRF Open-source Based Public
 * Software License(the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * openelis.uhl.uiowa.edu
 * 
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License for
 * the specific language governing rights and limitations under the License.
 * 
 * The Original Code is OpenELIS code.
 * 
 * The Initial Developer of the Original Code is The University of Iowa.
 * Portions created by The University of Iowa are Copyright 2006-2008. All
 * Rights Reserved.
 * 
 * Contributor(s): ______________________________________.
 * 
 * Alternatively, the contents of this file marked "Separately-Licensed" may be
 * used under the terms of a UIRF Software license ("UIRF Software License"), in
 * which case the provisions of a UIRF Software License are applicable instead
 * of those above.
 */
package org.openelis.metamap;

import org.openelis.gwt.common.MetaMap;
import org.openelis.meta.WorksheetMeta;

public class WorksheetMetaMap extends WorksheetMeta implements MetaMap {

    public WorksheetItemMetaMap WORKSHEET_ITEM;

    public WorksheetMetaMap() {
        super("w.");
        WORKSHEET_ITEM = new WorksheetItemMetaMap();
    }

    public WorksheetMetaMap(String path) {
        super(path);
        WORKSHEET_ITEM = new WorksheetItemMetaMap();
    }

    public static WorksheetMetaMap getInstance() {
        return new WorksheetMetaMap();
    }

    public WorksheetItemMetaMap getWorksheetItem() {
        return WORKSHEET_ITEM;
    }

    public boolean hasColumn(String columnName) {
        if (columnName.startsWith("worksheetItem."))
            return WORKSHEET_ITEM.hasColumn(columnName);
        return super.hasColumn(columnName);
    }

    public String buildFrom(String name) {
        String from;

        from = "Worksheet w ";
        if (name.indexOf("worksheetItem.") > -1)
            from += ", IN (w.worksheetItem) worksheetItem ";
        return from;
    }
}