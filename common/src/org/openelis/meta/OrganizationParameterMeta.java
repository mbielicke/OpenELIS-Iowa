package org.openelis.meta;

/**
 * OrganizationParameter META Data
 */

import java.util.HashSet;
import org.openelis.gwt.common.Meta;

public class OrganizationParameterMeta implements Meta {
    private String                path        = "";
    private static final String   entityName  = "OrganizationParameter";

    private static final String   ID              = "id",
                                  ORGANIZATION_ID = "organizationId",
                                  TYPE_ID         = "typeId",
                                  VALUE           = "value";

    private static final String[] columnNames = {ID, ORGANIZATION_ID, TYPE_ID, VALUE};

    private HashSet<String>       columnHashList;

    private void init() {
        columnHashList = new HashSet<String>(columnNames.length);
        for (int i = 0; i < columnNames.length; i++ ) {
            columnHashList.add(path + columnNames[i]);
        }
    }

    public OrganizationParameterMeta() {
        init();
    }

    public OrganizationParameterMeta(String path) {
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

    public String getOrganizationId() {
        return path + ORGANIZATION_ID;
    }

    public String getTypeId() {
        return path + TYPE_ID;
    }

    public String getValue() {
        return path + VALUE;
    }
}
