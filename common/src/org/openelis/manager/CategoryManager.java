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

import org.openelis.domain.CategoryDO;
import org.openelis.gwt.common.InconsistencyException;
import org.openelis.gwt.common.NotFoundException;
import org.openelis.gwt.common.RPC;

public class CategoryManager implements RPC {

    private static final long                       serialVersionUID = 1L;

    protected CategoryDO                            category;
    protected DictionaryManager                     entries;

    protected transient static CategoryManagerProxy proxy;

    /**
     * This is a protected constructor
     */
    protected CategoryManager() {
        category = null;
        entries = null;
    }

    /**
     * Creates a new instance of this object. A default category object is also
     * created.
     */
    public static CategoryManager getInstance() {
        CategoryManager manager;

        manager = new CategoryManager();
        manager.category = new CategoryDO();
        return manager;
    }

    public CategoryDO getCategory() {
        return category;
    }

    public void setCategory(CategoryDO category) {
        this.category = category;
    }

    public static CategoryManager fetchById(Integer id) throws Exception {
        return proxy().fetchById(id);
    }

    public static CategoryManager fetchWithEntries(Integer id) throws Exception {
        return proxy().fetchWithEntries(id);
    }

    public CategoryManager add() throws Exception {
        return proxy().add(this);
    }

    public CategoryManager update() throws Exception {
        return proxy().update(this);
    }

    public CategoryManager fetchForUpdate() throws Exception {
        return proxy().fetchForUpdate(category.getId());
    }

    public CategoryManager abortUpdate() throws Exception {
        return proxy().abortUpdate(category.getId());
    }

    public void validate() throws Exception {
        proxy().validate(this);
    }

    //
    // other managers
    //
    public DictionaryManager getEntries() throws Exception {
        if (entries == null) {
            if (category.getId() != null) {
                try {
                    entries = DictionaryManager.fetchByCategoryId(category.getId());
                } catch (NotFoundException e) {
                    // ignore
                } catch (Exception e) {
                    throw e;
                }
            }
            if (entries == null)
                entries = DictionaryManager.getInstance();
        }
        return entries;
    }

    private static CategoryManagerProxy proxy() {
        if (proxy == null)
            proxy = new CategoryManagerProxy();
        return proxy;
    }

}
