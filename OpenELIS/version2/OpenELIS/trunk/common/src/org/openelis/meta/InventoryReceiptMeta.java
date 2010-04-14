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
 * InventoryReceipt META Data
 */

import java.util.Arrays;
import java.util.HashSet;

import org.openelis.gwt.common.Meta;
import org.openelis.gwt.common.MetaMap;

public class InventoryReceiptMeta implements Meta, MetaMap {

    public static final String   ID = "id", 
                                 INVENTORY_ITEM_ID = "inventoryItemId",
                                 ORDER_ITEM_ID = "orderItemId", 
                                 ORGANIZATION_ID = "organizationId",
                                 RECEIVED_DATE = "receivedDate", 
                                 QUANTITY_RECEIVED = "quantityReceived",
                                 UNIT_COST = "unitCost", 
                                 QC_REFERENCE = "qcReference",
                                 EXTERNAL_REFERENCE = "externalReference",
                                 UPC = "upc";

    private static HashSet<String> names;

    static {
        names = new HashSet<String>(Arrays.asList(ID, INVENTORY_ITEM_ID, ORDER_ITEM_ID,
                                                  ORGANIZATION_ID, RECEIVED_DATE,
                                                  QUANTITY_RECEIVED, UNIT_COST, QC_REFERENCE,
                                                  EXTERNAL_REFERENCE, UPC));
    }

    public static String getId() {
        return ID;
    }

    public static String getInventoryItemId() {
        return INVENTORY_ITEM_ID;
    }

    public static String getOrderItemId() {
        return ORDER_ITEM_ID;
    }

    public static String getOrganizationId() {
        return ORGANIZATION_ID;
    }

    public static String getReceivedDate() {
        return RECEIVED_DATE;
    }

    public static String getQuantityReceived() {
        return QUANTITY_RECEIVED;
    }

    public static String getUnitCost() {
        return UNIT_COST;
    }

    public static String getQcReference() {
        return QC_REFERENCE;
    }

    public static String getExternalReference() {
        return EXTERNAL_REFERENCE;
    }

    public static String getUpc() {
        return UPC;
    }

    public boolean hasColumn(String columnName) {
        return names.contains(columnName);
    }
    
    public String buildFrom(String where) {
        return null;
    }

}
