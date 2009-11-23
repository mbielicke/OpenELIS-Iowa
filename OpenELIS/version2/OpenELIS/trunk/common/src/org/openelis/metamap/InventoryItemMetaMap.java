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
import org.openelis.meta.DictionaryMeta;
import org.openelis.meta.InventoryItemMeta;
import org.openelis.meta.NoteMeta;

public class InventoryItemMetaMap extends InventoryItemMeta implements MetaMap {

    public NoteMeta                  ITEM_NOTE;
    public InventoryComponentMetaMap INVENTORY_COMPONENT;
    public InventoryLocationMetaMap  INVENTORY_LOCATION;
    public DictionaryMeta            STORE_META;
    public InventoryItemMeta         PARENT_INVENTORY_ITEM;

    public InventoryItemMetaMap() {
        super("ii.");
        INVENTORY_COMPONENT = new InventoryComponentMetaMap("inventoryComponent.");
        INVENTORY_LOCATION = new InventoryLocationMetaMap("inventoryLocation.");
        PARENT_INVENTORY_ITEM = new InventoryItemMeta(path + "parentInventoryItem.");
        STORE_META = new DictionaryMeta("store.");
        ITEM_NOTE = new NoteMeta("note.");
    }

    public InventoryItemMetaMap(String path) {
        super(path);
        INVENTORY_COMPONENT = new InventoryComponentMetaMap(path + "inventoryComponent.");
        INVENTORY_LOCATION = new InventoryLocationMetaMap(path + "inventoryLocation.");
        PARENT_INVENTORY_ITEM = new InventoryItemMeta(path + "parentInventoryItem.");
        STORE_META = new DictionaryMeta(path + "store.");
        ITEM_NOTE = new NoteMeta(path + "note.");
    }

    public InventoryComponentMetaMap getInventoryComponent() {
        return INVENTORY_COMPONENT;
    }

    public InventoryLocationMetaMap getInventoryLocation() {
        return INVENTORY_LOCATION;
    }

    public DictionaryMeta getStore() {
        return STORE_META;
    }

    public InventoryItemMeta getParentInventoryItem() {
        return PARENT_INVENTORY_ITEM;
    }

    public NoteMeta getNote() {
        return ITEM_NOTE;
    }

    public static InventoryItemMetaMap getInstance() {
        return new InventoryItemMetaMap();
    }

    public boolean hasColumn(String name) {
        if (name.startsWith("inventoryComponent."))
            return INVENTORY_COMPONENT.hasColumn(name);
        else if (name.startsWith("inventoryLocation."))
            return INVENTORY_LOCATION.hasColumn(name);
        else if (name.startsWith("store."))
            return STORE_META.hasColumn(name);
        else if (name.startsWith(path+"parentInventoryItem."))
            return PARENT_INVENTORY_ITEM.hasColumn(name);
        else 
            return super.hasColumn(name);
    }

    public String buildFrom(String name) {
        String from = "InventoryItem ii ";
        if (name.indexOf("inventory_component.") > -1)
            from += ", IN (ii.inventoryComponent) inventoryComponent ";
        if (name.indexOf("inventoryLocation.") > -1)
            from += ", IN (ii.inventoryLocation) inventory_location";
        from += ", Dictionary store ";
        return from;
    }
}
