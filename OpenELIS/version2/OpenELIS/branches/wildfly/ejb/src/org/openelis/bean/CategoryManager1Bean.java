package org.openelis.bean;

import static org.openelis.manager.CategoryManager1Accessor.addDictionary;
import static org.openelis.manager.CategoryManager1Accessor.getCategory;
import static org.openelis.manager.CategoryManager1Accessor.getDictionaries;
import static org.openelis.manager.CategoryManager1Accessor.getRemoved;
import static org.openelis.manager.CategoryManager1Accessor.setCategory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.logging.Logger;

import javax.annotation.Resource;
import javax.annotation.security.RolesAllowed;
import javax.ejb.EJB;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;

import org.jboss.security.annotation.SecurityDomain;
import org.openelis.domain.CategoryDO;
import org.openelis.domain.Constants;
import org.openelis.domain.DataObject;
import org.openelis.domain.DictionaryViewDO;
import org.openelis.domain.IdNameVO;
import org.openelis.manager.CategoryManager1;
import org.openelis.ui.common.DataBaseUtil;
import org.openelis.ui.common.ValidationErrorsList;
import org.openelis.ui.common.data.QueryData;

@Stateless
@SecurityDomain("openelis")
public class CategoryManager1Bean {

    @Resource
    SessionContext                 ctx;

    @EJB
    LockBean                       lock;

    @EJB
    CategoryBean                   category;

    @EJB
    DictionaryBean                 dictionary;

    protected DictionaryComparator comparator;

    private static final Logger    log = Logger.getLogger("openelis");

    public CategoryManager1 getInstance() {
        CategoryManager1 cm;
        CategoryDO c;

        cm = new CategoryManager1();
        c = new CategoryDO();
        setCategory(cm, c);
        return cm;
    }

    /**
     * Returns a category manager for specified primary id
     */
    public CategoryManager1 fetchById(Integer categoryId) throws Exception {
        ArrayList<Integer> ids;
        ArrayList<CategoryManager1> cms;

        ids = new ArrayList<Integer>(1);
        ids.add(categoryId);
        cms = fetchByIds(ids);
        return cms.size() == 0 ? null : cms.get(0);
    }

    /**
     * Returns category managers for specified primary ids
     */
    public ArrayList<CategoryManager1> fetchByIds(ArrayList<Integer> categoryIds) throws Exception {
        CategoryManager1 cm;
        ArrayList<Integer> ids1;
        ArrayList<CategoryManager1> cms;
        ArrayList<CategoryDO> categories;
        HashMap<Integer, CategoryManager1> map;

        /*
         * to reduce database select calls, we are going to fetch everything for
         * a given select and unroll through loops.
         */
        cms = new ArrayList<CategoryManager1>();

        /*
         * if there are no categories, then return an empty list
         */
        categories = category.fetchByIds(categoryIds);
        if (categories.size() < 1)
            return cms;

        /*
         * build level 1, everything is based on category ids
         */
        ids1 = new ArrayList<Integer>();
        map = new HashMap<Integer, CategoryManager1>();

        for (CategoryDO data : categories) {
            cm = new CategoryManager1();
            setCategory(cm, data);
            cms.add(cm);

            ids1.add(data.getId()); // for fetch
            map.put(data.getId(), cm); // for linking
        }

        /*
         * various lists for each category
         */
        for (DictionaryViewDO data : dictionary.fetchByCategoryIds(ids1)) {
            cm = map.get(data.getCategoryId());
            addDictionary(cm, data);
        }
        return cms;
    }

    /**
     * Returns a category manager based on the specified query
     */
    public ArrayList<CategoryManager1> fetchByQuery(ArrayList<QueryData> fields, int first, int max) throws Exception {
        ArrayList<Integer> ids;

        ids = new ArrayList<Integer>();

        for (IdNameVO vo : category.query(fields, first, max))
            ids.add(vo.getId());
        return fetchByIds(ids);
    }

    /**
     * Returns a locked category manager with specified category id
     */
    @RolesAllowed("dictionary-update")
    public CategoryManager1 fetchForUpdate(Integer categoryId) throws Exception {
        ArrayList<Integer> ids;
        ArrayList<CategoryManager1> cms;

        ids = new ArrayList<Integer>(1);
        ids.add(categoryId);
        cms = fetchForUpdate(ids);
        return cms.size() == 0 ? null : cms.get(0);
    }

    /**
     * Returns a list of locked category managers with specified category ids
     */
    @RolesAllowed("dictionary-update")
    public ArrayList<CategoryManager1> fetchForUpdate(ArrayList<Integer> categoryIds) throws Exception {
        lock.lock(Constants.table().CATEGORY, categoryIds);
        return fetchByIds(categoryIds);
    }

    /**
     * Unlocks and returns a category manager with specified category id
     */
    @RolesAllowed({"dictionary-add", "dictionary-update"})
    public CategoryManager1 unlock(Integer categoryId) throws Exception {
        ArrayList<Integer> ids;
        ArrayList<CategoryManager1> sms;

        ids = new ArrayList<Integer>(1);
        ids.add(categoryId);
        sms = unlock(ids);
        return sms.size() == 0 ? null : sms.get(0);
    }

    /**
     * Unlocks and returns list of category managers with specified category ids
     */
    @RolesAllowed({"dictionary-add", "dictionary-update"})
    public ArrayList<CategoryManager1> unlock(ArrayList<Integer> categoryIds) throws Exception {
        lock.unlock(Constants.table().CATEGORY, categoryIds);
        return fetchByIds(categoryIds);
    }

    /**
     * Adds/updates the category and related records in the database. The
     * records are validated before add/update and the category record must have
     * a lock record if it has an id.
     */
    @RolesAllowed({"dictionary-add", "dictionary-update"})
    public CategoryManager1 update(CategoryManager1 cm, boolean ignoreWarnings) throws Exception {
        ArrayList<CategoryManager1> cms;

        cms = new ArrayList<CategoryManager1>(1);
        cms.add(cm);
        update(cms, ignoreWarnings);
        return cms.get(0);
    }

    /**
     * Adds/updates all the categories and related records in the database. All
     * the records are validated before add/update and the category records must
     * each have a lock record if they have an id.
     */
    @RolesAllowed({"dictionary-add", "dictionary-update"})
    public ArrayList<CategoryManager1> update(ArrayList<CategoryManager1> cms,
                                              boolean ignoreWarnings) throws Exception {
        Integer so;
        HashSet<Integer> ids;
        ArrayList<Integer> locks;

        ids = new HashSet<Integer>();

        validate(cms, ignoreWarnings);

        /*
         * check all the locks
         */
        for (CategoryManager1 cm : cms) {
            if (getCategory(cm).getId() != null)
                ids.add(getCategory(cm).getId());
        }
        if (ids.size() > 0) {
            locks = new ArrayList<Integer>(ids);
            lock.validateLock(Constants.table().CATEGORY, locks);
        } else {
            locks = null;
        }
        ids = null;

        for (CategoryManager1 cm : cms) {

            /*
             * go through remove list and delete all the unwanted records
             */
            if (getRemoved(cm) != null) {
                for (DataObject data : getRemoved(cm)) {
                    if (data instanceof DictionaryViewDO)
                        dictionary.delete( ((DictionaryViewDO)data));
                    else
                        throw new Exception("ERROR: DataObject passed for removal is of unknown type");
                }
            }
            // add/update category
            if (getCategory(cm).getId() == null)
                category.add(getCategory(cm));
            else
                category.update(getCategory(cm));

            so = 0;
            if (getDictionaries(cm) != null) {
                for (DictionaryViewDO data : getDictionaries(cm)) {
                    so++ ;
                    if ( !DataBaseUtil.isSame(so, data.getSortOrder()))
                        data.setSortOrder(so);
                    if (data.getId() == null) {
                        data.setCategoryId(getCategory(cm).getId());
                        dictionary.add(data);
                    } else {
                        dictionary.update(data);
                    }
                }
            }
        }

        if (locks != null)
            lock.unlock(Constants.table().CATEGORY, locks);

        return cms;
    }

    /**
     * 
     */
    public CategoryManager1 sort(CategoryManager1 cm, boolean ascending) {
        if (comparator == null)
            comparator = new DictionaryComparator();
        comparator.setSortAscending(ascending);
        Collections.sort(getDictionaries(cm), comparator);
        return cm;
    }

    /**
     * Validates the category manager for add or update. The routine throws a
     * list of exceptions/warnings listing all the problems for each category.
     */
    private void validate(ArrayList<CategoryManager1> cms, boolean ignoreWarning) throws Exception {
        ValidationErrorsList e;

        e = new ValidationErrorsList();

        for (CategoryManager1 cm : cms) {
            /*
             * category level
             */
            if (getCategory(cm) != null) {
                if (getCategory(cm).isChanged()) {
                    try {
                        category.validate(getCategory(cm));
                    } catch (Exception err) {
                        DataBaseUtil.mergeException(e, err);
                    }
                }
            }

            if (getDictionaries(cm) != null) {
                for (DictionaryViewDO data : getDictionaries(cm)) {
                    try {
                        dictionary.validate(data);
                    } catch (Exception err) {
                        DataBaseUtil.mergeException(e, err);
                    }
                }
            }
        }

        if (e.size() > 0)
            throw e;
    }

    //
    // Sort for Dictionary Entries
    //
    private class DictionaryComparator implements Comparator<DictionaryViewDO> {
        boolean ascending;

        public void setSortAscending(boolean ascending) {
            this.ascending = ascending;
        }

        public int compare(DictionaryViewDO data1, DictionaryViewDO data2) {
            String entry1, entry2;

            entry1 = data1.getEntry();
            entry2 = data2.getEntry();

            if (ascending) {
                return compare(entry1, entry2);
            } else {
                return (compare(entry1, entry2) * -1);
            }
        }

        private int compare(String entry1, String entry2) {
            if (DataBaseUtil.isEmpty(entry1)) {
                if (DataBaseUtil.isEmpty(entry2))
                    return 0;
                else
                    return 1;
            } else {
                if (DataBaseUtil.isEmpty(entry2))
                    return -1;
                else
                    return entry1.compareTo(entry2);
            }
        }

    }

}
