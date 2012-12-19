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
package org.openelis.meta;

/**
 * InventoryComponent META Data
 */


public class InventoryComponentMeta {
    private static final String   entityName  = "InventoryComponent";

    private static final String   ID = "id",
                                  INVENTORY_ITEM_ID = "inventoryItemId",
                                  COMPONENT_ID = "componentId",
                                  QUANTITY = "quantity";

    private static final String[] columnNames = {ID, INVENTORY_ITEM_ID, COMPONENT_ID, QUANTITY};

    public String getId() {
        return ID;
    }

    public String getInventoryItemId() {
        return INVENTORY_ITEM_ID;
    }

    public String getComponentId() {
        return COMPONENT_ID;
    }

    public String getQuantity() {
        return QUANTITY;
    }
}
