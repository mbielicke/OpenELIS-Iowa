package org.openelis.manager;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import org.openelis.domain.CategoryDO;
import org.openelis.domain.DataObject;
import org.openelis.domain.DictionaryViewDO;

public class CategoryManager1 implements Serializable {
    private static final long             serialVersionUID = 1L;

    protected CategoryDO                  category;
    protected ArrayList<DictionaryViewDO> dictionaries;
    protected ArrayList<DataObject>       removed;

    transient public final Dictionary     dictionary       = new Dictionary();

    /**
     * Initialize an empty category manager
     */
    public CategoryManager1() {
        category = new CategoryDO();
    }

    /**
     * Returns the category DO
     */
    public CategoryDO getCategory() {
        return category;
    }

    /**
     * Class to manager Dictionary Entry information
     */
    public class Dictionary {
        public DictionaryViewDO get(int i) {
            return dictionaries.get(i);
        }

        public DictionaryViewDO add() {
            DictionaryViewDO data;

            data = new DictionaryViewDO();
            if (dictionaries == null)
                dictionaries = new ArrayList<DictionaryViewDO>();
            dictionaries.add(data);

            return data;
        }

        public void add(DictionaryViewDO entry) {
            if (dictionaries == null)
                dictionaries = new ArrayList<DictionaryViewDO>();
            dictionaries.add(entry);
        }

        public void addAt(DictionaryViewDO entry, int i) {
            if (dictionaries == null)
                dictionaries = new ArrayList<DictionaryViewDO>();
            dictionaries.add(i, entry);
        }

        /**
         * Removes an entry from the list
         */
        public void remove(int i) {
            DictionaryViewDO data;

            if (dictionaries == null || i >= dictionaries.size())
                return;

            data = dictionaries.remove(i);
            dataObjectRemove(data.getId(), data);
        }

        public void move(int oldIndex, int newIndex) {
            DictionaryViewDO entry;

            if (dictionaries == null)
                return;

            entry = dictionaries.remove(oldIndex);

            if (newIndex >= count())
                dictionaries.add(entry);
            else
                addAt(entry, newIndex);
        }

        /**
         * Returns the number of entries associated with this category
         */
        public int count() {
            if (dictionaries != null)
                return dictionaries.size();
            return 0;
        }
    }

    /**
     * Adds the specified data object to the list of objects that should be
     * removed from the database.
     */
    private void dataObjectRemove(Integer id, DataObject data) {
        if (removed == null)
            removed = new ArrayList<DataObject>();
        if (id != null)
            removed.add(data);
    }

    /**
     * sorts the dictionary list on the entries
     */
    public void sort(Comparator<DictionaryViewDO> comparator) {
        Collections.sort(dictionaries, comparator);
    }
}
