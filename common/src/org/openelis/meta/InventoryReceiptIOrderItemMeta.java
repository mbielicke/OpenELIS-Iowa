package org.openelis.meta;

/**
 * InventoryReceiptOrderItem META Data
 */

import java.util.HashSet;

import org.openelis.ui.common.Meta;

public class InventoryReceiptIOrderItemMeta implements Meta {
    private String                path        = "";
    private static final String   entityName  = "InventoryReceiptIOrderItem";

    private static final String   ID          = "id",
                                  INVENTORY_RECEIPT_ID = "inventoryReceiptId",
                                  IORDER_ITEM_ID = "iorderItemId";

    private static final String[] columnNames = {ID, INVENTORY_RECEIPT_ID, IORDER_ITEM_ID};

    private HashSet<String>       columnHashList;

    private void init() {
        columnHashList = new HashSet<String>(columnNames.length);
        for (int i = 0; i < columnNames.length; i++) {
            columnHashList.add(path + columnNames[i]);
        }
    }

    public InventoryReceiptIOrderItemMeta() {
        init();
    }

    public InventoryReceiptIOrderItemMeta(String path) {
        this.path = path;
        init();
    }

    public String[] getColumnList() {
        return columnNames;
    }

    public String getEntity() {
        return entityName;
    }

    public boolean hasColumn(String columnName) {
        return columnHashList.contains(columnName);
    }

    public String getId() {
        return path + ID;
    }

    public String getInventoryReceiptId() {
        return path + INVENTORY_RECEIPT_ID;
    }

    public String getIorderItemId() {
        return path + IORDER_ITEM_ID;
    }
}
