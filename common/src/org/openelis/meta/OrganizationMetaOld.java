package org.openelis.meta;

import java.util.HashSet;

import org.openelis.gwt.common.Meta;

public class OrganizationMetaOld implements Meta {
    protected String              path        = "";
    private static final String   entityName  = "Organization";

    private static final String   ID = "id",
                                  PARENT_ORGANIZATION_ID = "parentOrganizationId",
                                  NAME = "name",
                                  IS_ACTIVE = "isActive", 
                                  ADDRESS_ID = "addressId";

    private static final String[] columnNames = {ID, PARENT_ORGANIZATION_ID,
                    NAME, IS_ACTIVE, ADDRESS_ID};

    private HashSet<String>       columnHashList;

    private void init() {
        columnHashList = new HashSet<String>(columnNames.length);
        for (int i = 0; i < columnNames.length; i++ ) {
            columnHashList.add(path + columnNames[i]);
        }
    }

    public OrganizationMetaOld() {
        init();
    }

    public OrganizationMetaOld(String path) {
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

    public String getParentOrganizationId() {
        return path + PARENT_ORGANIZATION_ID;
    }

    public String getName() {
        return path + NAME;
    }

    public String getIsActive() {
        return path + IS_ACTIVE;
    }

    public String getAddressId() {
        return path + ADDRESS_ID;
    }

}