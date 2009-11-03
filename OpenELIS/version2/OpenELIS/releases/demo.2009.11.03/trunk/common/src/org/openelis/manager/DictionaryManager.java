/**
 * Exhibit A - UIRF Open-source Based Public Software License.
 * 
 * The contents of this file are subject to the UIRF Open-source Based Public
 * Software License(the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * openelis.uhl.uiowa.edu
 * 
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License for
 * the specific language governing rights and limitations under the License.
 * 
 * The Original Code is OpenELIS code.
 * 
 * The Initial Developer of the Original Code is The University of Iowa.
 * Portions created by The University of Iowa are Copyright 2006-2008. All
 * Rights Reserved.
 * 
 * Contributor(s): ______________________________________.
 * 
 * Alternatively, the contents of this file marked "Separately-Licensed" may be
 * used under the terms of a UIRF Software license ("UIRF Software License"), in
 * which case the provisions of a UIRF Software License are applicable instead
 * of those above.
 */
package org.openelis.manager;

import java.util.ArrayList;

import org.openelis.domain.CategoryDO;
import org.openelis.domain.DictionaryViewDO;
import org.openelis.gwt.common.InconsistencyException;
import org.openelis.gwt.common.RPC;

public class DictionaryManager implements RPC {

    private static final long                         serialVersionUID = 1L;

    protected Integer                                 categoryId;
    protected CategoryDO                              category;
    protected ArrayList<DictionaryViewDO>             entries, deleted;

    protected transient static DictionaryManagerProxy proxy;

    /**
     * This is a protected constructor
     */
    protected DictionaryManager() {
        category = null;
        entries = null;
    }

    /**
     * Creates a new instance of this object. A default category object is also
     * created.
     */
    public static DictionaryManager getInstance() {
        DictionaryManager dm;

        dm = new DictionaryManager();
        dm.category = new CategoryDO();
        return dm;
    }

    public CategoryDO getCategory() {
        return category;
    }

    public void setCategory(CategoryDO category) {
        this.category = category;
    }

    public int count() {
        if (entries == null)
            return 0;

        return entries.size();
    }

    public DictionaryViewDO getEntryAt(int i) {
        return entries.get(i);
    }

    public void setEntryAt(DictionaryViewDO entry, int i) {
        entries.set(i, entry);
    }

    public Integer getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Integer categoryId) {
        this.categoryId = categoryId;
    }

    public void addEntry(DictionaryViewDO entry) {
        if (entries == null)
            entries = new ArrayList<DictionaryViewDO>();

        entries.add(entry);
    }

    public void addEntryAt(DictionaryViewDO entry, int i) {
        if (entries == null)
            entries = new ArrayList<DictionaryViewDO>();

        entries.add(i, entry);
    }

    public void removeEntryAt(int i) {
        DictionaryViewDO entry;

        if (entries == null || i >= entries.size())
            return;

        entry = entries.remove(i);
        if (entry.getId() != null)
            if (deleted == null)
                deleted = new ArrayList<DictionaryViewDO>();
        deleted.add(entry);
    }

    public void moveEntry(int oldIndex, int newIndex) {
        DictionaryViewDO entry;

        if (entries == null)
            return;

        entry = entries.remove(oldIndex);
        if(newIndex > oldIndex)
            newIndex--;
        
        if (newIndex >= count())
            addEntry(entry);
        else
            addEntryAt(entry, newIndex);
    }

    public static DictionaryManager fetchByCategoryId(Integer categoryId) throws Exception {
        return proxy().fetchByCategoryId(categoryId);
    }

    public DictionaryManager add() throws Exception {
        return proxy().add(this);
    }

    public DictionaryManager update() throws Exception {
        return proxy().update(this);
    }

    public DictionaryManager fetchForUpdate() throws Exception {
        if (category.getId() == null)
            throw new InconsistencyException("test id is null");

        return proxy().fetchForUpdate(category.getId());
    }

    public DictionaryManager abortUpdate() throws Exception {
        return proxy().abortUpdate(category.getId());
    }

    public void validate() throws Exception {
        proxy().validate(this);
    }

    ArrayList<DictionaryViewDO> getEntries() {
        return entries;
    }

    void setEntries(ArrayList<DictionaryViewDO> entries) {
        this.entries = entries;
    }

    int deleteCount() {
        if (deleted == null)
            return 0;

        return deleted.size();
    }

    DictionaryViewDO getDeletedAt(int i) {
        return deleted.get(i);
    }

    private static DictionaryManagerProxy proxy() {
        if (proxy == null)
            proxy = new DictionaryManagerProxy();
        return proxy;
    }
}
