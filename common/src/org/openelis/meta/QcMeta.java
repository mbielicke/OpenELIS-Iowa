package org.openelis.meta;

/**
 * Qc META Data
 */

import java.util.Arrays;
import java.util.HashSet;

import org.openelis.ui.common.Meta;
import org.openelis.ui.common.MetaMap;

public class QcMeta implements Meta, MetaMap {
    private static final String   ID  = "_qc.id",
                                  NAME = "_qc.name",
                                  TYPE_ID = "_qc.typeId",
                                  INVENTORY_ITEM_ID = "_qc.inventoryItemId",
                                  SOURCE = "_qc.source",                                  
                                  IS_ACTIVE = "_qc.isActive",
                                  
                                  ANA_ID = "_qcAnalyte.id",
                                  ANA_QC_ID = "_qcAnalyte.qcId",
                                  ANA_SORT_ORDER = "_qcAnalyte.sortOrder",
                                  ANA_ANALYTE_ID = "_qcAnalyte.analyteId",
                                  ANA_TYPE_ID = "_qcAnalyte.typeId",
                                  ANA_VALUE = "_qcAnalyte.value",
                                  ANA_IS_TRENDABLE = "_qcAnalyte.isTrendable",
                                  
                                  LOT_ID = "_qcLot.id",       
                                  LOT_QC_ID = "_qcLot.qcId",
                                  LOT_LOT_NUMBER = "_qcLot.lotNumber",
                                  LOT_LOCATION_ID = "_qcLot.locationId",
                                  LOT_PREPARED_DATE = "_qcLot.preparedDate",
                                  LOT_PREPARED_VOLUME = "_qcLot.preparedVolume",
                                  LOT_PREPARED_UNIT_ID = "_qcLot.preparedUnitId",
                                  LOT_PREPARED_BY_ID = "_qcLot.preparedById",
                                  LOT_USABLE_DATE = "_qcLot.usableDate",
                                  LOT_EXPIRE_DATE = "_qcLot.expireDate",
                                  LOT_IS_ACTIVE = "_qcLot.isActive",

                                  INVENTORY_ITEM_NAME = "_qc.inventoryItem.name",
                                  ANA_ANALYTE_NAME = "_qcAnalyte.analyte.name";

    private static HashSet<String> names;

    static {
        names = new HashSet<String>(Arrays.asList(ID, NAME, TYPE_ID, INVENTORY_ITEM_ID,
                                                  SOURCE, IS_ACTIVE, ANA_ID, ANA_QC_ID, 
                                                  ANA_SORT_ORDER, ANA_ANALYTE_ID,
                                                  ANA_TYPE_ID, ANA_VALUE, ANA_IS_TRENDABLE,
                                                  LOT_ID, LOT_QC_ID, LOT_LOT_NUMBER,
                                                  LOT_LOCATION_ID, LOT_PREPARED_DATE, 
                                                  LOT_PREPARED_VOLUME, LOT_PREPARED_UNIT_ID,
                                                  LOT_PREPARED_BY_ID, LOT_USABLE_DATE,
                                                  LOT_EXPIRE_DATE, LOT_IS_ACTIVE,
                                                  INVENTORY_ITEM_NAME, ANA_ANALYTE_NAME));
    }

    public static String getId() {
        return ID;
    }

    public static String getName() {
        return NAME;
    }

    public static String getTypeId() {
        return TYPE_ID;
    }

    public static String getInventoryItemId() {
        return INVENTORY_ITEM_ID;
    }

    public static String getSource() {
        return SOURCE;
    }

    public static String getIsActive() {
        return IS_ACTIVE;
    }

    public static String getQcAnalyteId() {
        return ANA_ID;
    }

    public static String getQcAnalyteQcId() {
        return ANA_QC_ID;
    }

    public static String getQcAnalyteSortOrder() {
        return ANA_SORT_ORDER;
    }

    public static String getQcAnalyteAnalyteId() {
        return ANA_ANALYTE_ID;
    }

    public static String getQcAnalyteTypeId() {
        return ANA_TYPE_ID;
    }

    public static String getQcAnalyteValue() {
        return ANA_VALUE;
    }

    public static String getQcAnalyteIsTrendable() {
        return ANA_IS_TRENDABLE;
    }
    
    public static String getQcLotId() {
        return LOT_ID;
    }

    public static String getQcLotQcId() {
        return LOT_QC_ID;
    }

    public static String getQcLotLotNumber() {
        return LOT_LOT_NUMBER;
    }

    public static String getQcLotLocationId() {
        return LOT_LOCATION_ID;
    }

    public static String getQcLotPreparedDate() {
        return LOT_PREPARED_DATE;
    }

    public static String getQcLotPreparedVolume() {
        return LOT_PREPARED_VOLUME;
    }

    public static String getQcLotPreparedUnitId() {
        return LOT_PREPARED_UNIT_ID;
    }

    public static String getQcLotPreparedById() {
        return LOT_PREPARED_BY_ID;
    }

    public static String getQcLotUsableDate() {
        return LOT_USABLE_DATE;
    }

    public static String getQcLotExpireDate() {
        return LOT_EXPIRE_DATE;
    }

    public static String getQcLotIsActive() {
        return LOT_IS_ACTIVE;
    }

    public static String getInventoryItemName() {
        return INVENTORY_ITEM_NAME;
    }

    public static String getQcAnalyteAnalyteName() {
        return ANA_ANALYTE_NAME;
    }

    public boolean hasColumn(String columnName) {
        return names.contains(columnName);
    }

    public String buildFrom(String where) {
        String from;
        
        from = "Qc _qc ";
        if (where.indexOf("qcAnalyte.") > -1)
            from += ",IN (_qc.qcAnalyte) _qcAnalyte ";
        if (where.indexOf("qcLot.") > -1)
            from += ",IN (_qc.qcLot) _qcLot ";

        return from;
    }
}