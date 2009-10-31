package org.openelis.meta;

/**
 * Qc META Data
 */

import org.openelis.gwt.common.Meta;

import java.util.HashSet;

public class QcMeta implements Meta {
    public String                 path        = "";
    private static final String   entityName  = "Qc";

    private static final String   ID  = "id",
                                  NAME = "name",
                                  TYPE_ID = "typeId",
                                  INVENTORY_ITEM_ID = "inventoryItemId",
                                  SOURCE = "source",
                                  LOT_NUMBER = "lotNumber",
                                  PREPARED_DATE = "preparedDate",
                                  PREPARED_VOLUME = "preparedVolume",
                                  PREPARED_UNIT_ID = "preparedUnitId",
                                  PREPARED_BY_ID = "preparedById",
                                  USABLE_DATE = "usableDate",
                                  EXPIRE_DATE = "expireDate",
                                  IS_SINGLE_USE = "isSingleUse";

    private static final String[] columnNames = {ID,
                    NAME, TYPE_ID, INVENTORY_ITEM_ID, SOURCE, LOT_NUMBER,
                    PREPARED_DATE, PREPARED_VOLUME, PREPARED_UNIT_ID,
                    PREPARED_BY_ID, USABLE_DATE, EXPIRE_DATE, IS_SINGLE_USE};

    private HashSet<String>       columnHashList;

    private void init() {
        columnHashList = new HashSet<String>(columnNames.length);
        for (int i = 0; i < columnNames.length; i++ ) {
            columnHashList.add(path + columnNames[i]);
        }
    }

    public QcMeta() {
        init();
    }

    public QcMeta(String path) {
        this.path = path;
        init();
    }

    public String getId() {
        return path + ID;
    }

    public String getName() {
        return path + NAME;
    }

    public String getTypeId() {
        return path + TYPE_ID;
    }

    public String getInventoryItemId() {
        return path + INVENTORY_ITEM_ID;
    }

    public String getSource() {
        return path + SOURCE;
    }

    public String getLotNumber() {
        return path + LOT_NUMBER;
    }

    public String getPreparedDate() {
        return path + PREPARED_DATE;
    }

    public String getPreparedVolume() {
        return path + PREPARED_VOLUME;
    }

    public String getPreparedUnitId() {
        return path + PREPARED_UNIT_ID;
    }

    public String getPreparedById() {
        return path + PREPARED_BY_ID;
    }

    public String getUsableDate() {
        return path + USABLE_DATE;
    }

    public String getExpireDate() {
        return path + EXPIRE_DATE;
    }

    public String getIsSingleUse() {
        return path + IS_SINGLE_USE;
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
}