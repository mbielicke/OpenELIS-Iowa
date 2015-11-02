package org.openelis.manager;

import java.util.ArrayList;

import org.openelis.domain.AuxFieldGroupDO;
import org.openelis.domain.AuxFieldValueViewDO;
import org.openelis.domain.AuxFieldViewDO;
import org.openelis.domain.DataObject;

/**
 * This class is used to bulk load aux field group manager.
 */
public class AuxFieldGroupManager1Accessor {

    /**
     * Set/get objects from aux field group manager
     */
    public static AuxFieldGroupDO getGroup(AuxFieldGroupManager1 am) {
        return am.group;
    }

    public static void setGroup(AuxFieldGroupManager1 am, AuxFieldGroupDO group) {
        am.group = group;
    }

    public static ArrayList<AuxFieldViewDO> getFields(AuxFieldGroupManager1 am) {
        return am.fields;
    }

    public static void setFields(AuxFieldGroupManager1 am, ArrayList<AuxFieldViewDO> fields) {
        am.fields = fields;
    }

    public static void addField(AuxFieldGroupManager1 am, AuxFieldViewDO test) {
        if (am.fields == null)
            am.fields = new ArrayList<AuxFieldViewDO>();
        am.fields.add(test);
    }

    public static ArrayList<AuxFieldValueViewDO> getValues(AuxFieldGroupManager1 am) {
        return am.values;
    }

    public static void setValues(AuxFieldGroupManager1 am, ArrayList<AuxFieldValueViewDO> values) {
        am.values = values;
    }

    public static void addValue(AuxFieldGroupManager1 am, AuxFieldValueViewDO value) {
        if (am.values == null)
            am.values = new ArrayList<AuxFieldValueViewDO>();
        am.values.add(value);
    }

    public static ArrayList<AuxFieldValueViewDO> getValues(AuxFieldGroupManager1 am,
                                                           AuxFieldViewDO field) {
        return am.value.get(field);
    }

    public static ArrayList<DataObject> getRemoved(AuxFieldGroupManager1 am) {
        return am.removed;
    }

    public static void setRemoved(AuxFieldGroupManager1 am, ArrayList<DataObject> removed) {
        am.removed = removed;
    }
}
