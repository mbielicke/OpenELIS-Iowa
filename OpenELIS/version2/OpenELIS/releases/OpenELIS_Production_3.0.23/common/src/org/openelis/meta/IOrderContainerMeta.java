package org.openelis.meta;

/**
 * OrderContainer META Data
 */

import java.util.HashSet;

import org.openelis.ui.common.Meta;

public class IOrderContainerMeta implements Meta {
    private String                path        = "";
    private static final String   entityName  = "OrderContainer";

    private static final String   ID = "id",
                                  IORDER_ID = "iorderId",
                                  CONTAINER_ID = "containerId",
                                  NUMBER_OF_CONTAINERS = "numberOfContainers",
                                  TYPE_OF_SAMPLE_ID = "typeOfSampleId";

    private static final String[] columnNames = {ID, IORDER_ID, CONTAINER_ID,
                    NUMBER_OF_CONTAINERS, TYPE_OF_SAMPLE_ID};

    private HashSet<String>       columnHashList;

    private void init() {
        columnHashList = new HashSet<String>(columnNames.length);
        for (int i = 0; i < columnNames.length; i++) {
            columnHashList.add(path + columnNames[i]);
        }
    }

    public IOrderContainerMeta() {
        init();
    }

    public IOrderContainerMeta(String path) {
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

    public String getIorderId() {
        return path + IORDER_ID;
    }

    public String getContainerId() {
        return path + CONTAINER_ID;
    }

    public String getNumberOfContainers() {
        return path + NUMBER_OF_CONTAINERS;
    }

    public String getTypeOfSampleId() {
        return path + TYPE_OF_SAMPLE_ID;
    }
}
