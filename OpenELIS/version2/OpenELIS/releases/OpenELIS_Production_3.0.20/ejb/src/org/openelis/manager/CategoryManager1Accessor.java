package org.openelis.manager;

import java.util.ArrayList;

import org.openelis.domain.CategoryDO;
import org.openelis.domain.DataObject;
import org.openelis.domain.DictionaryViewDO;

/**
 * This class is used to bulk load category manager
 */
public class CategoryManager1Accessor {
    /**
     * Set/get objects from category manager
     */
    public static CategoryDO getCategory(CategoryManager1 cm) {
        return cm.category;
    }

    public static void setCategory(CategoryManager1 cm, CategoryDO category) {
        cm.category = category;
    }

    public static ArrayList<DictionaryViewDO> getDictionaries(CategoryManager1 cm) {
        return cm.dictionaries;
    }

    public static void setDictionaries(CategoryManager1 cm, ArrayList<DictionaryViewDO> entries) {
        cm.dictionaries = entries;
    }

    public static void addDictionary(CategoryManager1 cm, DictionaryViewDO dictionary) {
        if (cm.dictionaries == null)
            cm.dictionaries = new ArrayList<DictionaryViewDO>();
        cm.dictionaries.add(dictionary);
    }

    public static ArrayList<DataObject> getRemoved(CategoryManager1 cm) {
        return cm.removed;
    }

    public static void setRemoved(CategoryManager1 cm, ArrayList<DataObject> removed) {
        cm.removed = removed;
    }
}
