package org.openelis.manager;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

import org.openelis.domain.AuxFieldGroupDO;
import org.openelis.domain.AuxFieldValueViewDO;
import org.openelis.domain.AuxFieldViewDO;
import org.openelis.domain.Constants;
import org.openelis.domain.DataObject;
import org.openelis.utilcommon.ResultFormatter;

public class AuxFieldGroupManager1 implements Serializable {

    private static final long                     serialVersionUID = 1L;

    protected AuxFieldGroupDO                     group;
    protected ArrayList<AuxFieldViewDO>           fields;
    protected ArrayList<AuxFieldValueViewDO>      values;
    protected ArrayList<DataObject>               removed;
    protected int                                 nextUID          = -1;
    protected transient ResultFormatter           formatter;

    transient public final AuxField               field            = new AuxField();
    transient public final AuxFieldValue          value            = new AuxFieldValue();
    transient private HashMap<String, DataObject> uidMap;

    /**
     * Initialize an empty aux group manager
     */
    public AuxFieldGroupManager1() {
        group = new AuxFieldGroupDO();
    }

    public AuxFieldGroupDO getGroup() {
        return group;
    }

    /**
     * Returns the next negative Id for this aux group's newly created and as
     * yet uncommitted data objects.
     */
    public int getNextUID() {
        return --nextUID;
    }

    /**
     * Returns the data object using its Uid.
     */
    public DataObject getObject(String uid) {
        if (uidMap == null) {
            uidMap = new HashMap<String, DataObject>();

            if (fields != null)
                for (AuxFieldViewDO data : fields)
                    uidMap.put(Constants.uid().get(data), data);

            if (values != null)
                for (AuxFieldValueViewDO data : values)
                    uidMap.put(Constants.uid().get(data), data);

        }
        return uidMap.get(uid);
    }

    /**
     * Returns the formatter used to format and validate this aux field group's
     * values
     */
    public ResultFormatter getFormatter() throws Exception {
        AuxFieldViewDO af;
        AuxFieldValueViewDO afv;

        if (formatter != null)
            return formatter;

        formatter = new ResultFormatter();
        for (int i = 0; i < field.count(); i++ ) {
            af = field.get(i);
            for (int j = 0; j < value.count(af); j++ ) {
                afv = value.get(af, j);

                formatter.add(afv.getId(),
                              af.getId(),
                              null,
                              afv.getTypeId(),
                              null,
                              null,
                              afv.getValue(),
                              afv.getDictionary(),
                              afv.getDictionaryIsActive());
            }
        }
        return formatter;
    }

    /**
     * Class to manager Auxiliary Field information
     */
    public class AuxField {

        /**
         * Returns the field with the specified id
         */
        public AuxFieldViewDO getById(Integer id) {
            return (AuxFieldViewDO)getObject(Constants.uid().getAuxFieldGroup(id));
        }

        /**
         * Returns the field at specified index.
         */
        public AuxFieldViewDO get(int i) {
            return fields.get(i);
        }

        /**
         * Returns a new field
         */
        public AuxFieldViewDO add() {
            AuxFieldViewDO data;

            data = new AuxFieldViewDO();
            data.setId(getNextUID());
            if (fields == null)
                fields = new ArrayList<AuxFieldViewDO>();
            fields.add(data);
            uidMapAdd(Constants.uid().get(data), data);

            return data;
        }

        /**
         * Returns a new field
         */
        public AuxFieldViewDO addAt(AuxFieldViewDO field, int i) {

            if (fields == null)
                fields = new ArrayList<AuxFieldViewDO>();
            fields.add(i, field);
            uidMapAdd(Constants.uid().get(field), field);

            return field;
        }

        /**
         * Removes an item from the list
         */
        public void remove(int i) {
            AuxFieldViewDO data;

            data = fields.get(i);

            fields.remove(data);
            dataObjectRemove(data.getId(), data);
            uidMapRemove(Constants.uid().get(data));
        }

        public void remove(AuxFieldViewDO data) {
            fields.remove(data);
            dataObjectRemove(data.getId(), data);
            uidMapRemove(Constants.uid().get(data));
        }

        public void move(int oldIndex, int newIndex) {
            AuxFieldViewDO field;

            if (fields == null)
                return;

            field = fields.remove(oldIndex);

            if (newIndex >= count())
                fields.add(field);
            else
                addAt(field, newIndex);
        }

        /**
         * Returns the number of fields associated with this aux group
         */
        public int count() {
            if (fields != null)
                return fields.size();
            return 0;
        }
    }

    public class AuxFieldValue {
        transient protected HashMap<Integer, ArrayList<AuxFieldValueViewDO>> localmap = null;

        /**
         * Returns the aux field values for the specified field.
         */
        public ArrayList<AuxFieldValueViewDO> get(AuxFieldViewDO field) {
            localmapBuild();
            return localmap.get(field.getId());
        }

        /**
         * Returns the aux field value at specified index.
         */
        public AuxFieldValueViewDO get(AuxFieldViewDO field, int i) {
            localmapBuild();
            return localmap.get(field.getId()).get(i);
        }

        public AuxFieldValueViewDO add(AuxFieldViewDO field) {
            AuxFieldValueViewDO data;

            localmapBuild();
            data = new AuxFieldValueViewDO();
            data.setId(getNextUID());
            data.setAuxFieldId(field.getId());
            localmapAdd(data);
            if (values == null)
                values = new ArrayList<AuxFieldValueViewDO>();
            values.add(data);
            uidMapAdd(Constants.uid().get(data), data);
            return data;
        }

        /**
         * Removes an item from the list
         */
        public void remove(AuxFieldViewDO field, int i) {
            AuxFieldValueViewDO data;

            data = value.get(field, i);

            values.remove(data);
            dataObjectRemove(data.getId(), data);
            uidMapRemove(Constants.uid().get(data));
        }

        public void remove(AuxFieldViewDO data) {
            values.remove(data);
            dataObjectRemove(data.getId(), data);
            uidMapRemove(Constants.uid().get(data));
        }

        /**
         * Returns the number of values associated with specified aux field
         */
        public int count(AuxFieldViewDO field) {
            ArrayList<AuxFieldValueViewDO> l;

            if (values != null) {
                localmapBuild();
                l = localmap.get(field.getId());
                if (l != null)
                    return l.size();
            }
            return 0;
        }

        /**
         * create a hash map from value list
         */
        private void localmapBuild() {
            if (localmap == null && values != null) {
                localmap = new HashMap<Integer, ArrayList<AuxFieldValueViewDO>>();
                for (AuxFieldValueViewDO data : values)
                    localmapAdd(data);
            }
        }

        /**
         * adds a new value to the hash map
         */
        private void localmapAdd(AuxFieldValueViewDO data) {
            ArrayList<AuxFieldValueViewDO> l;

            if (localmap != null) {
                l = localmap.get(data.getAuxFieldId());
                if (l == null) {
                    l = new ArrayList<AuxFieldValueViewDO>();
                    localmap.put(data.getAuxFieldId(), l);
                }
                l.add(data);
            }
        }
    }

    /**
     * adds an object to uid map
     */
    private void uidMapAdd(String uid, DataObject data) {
        if (uidMap != null)
            uidMap.put(uid, data);
    }

    /**
     * removes the object from uid map
     */
    private void uidMapRemove(String uid) {
        if (uidMap != null)
            uidMap.remove(uid);
    }

    /**
     * adds the data object to the list of objects that should be removed from
     * the database
     */
    private void dataObjectRemove(Integer id, DataObject data) {
        if (removed == null)
            removed = new ArrayList<DataObject>();
        if (id > 0)
            removed.add(data);
    }

}