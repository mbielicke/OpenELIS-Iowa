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
package org.openelis.modules.dictionary1.server;

import java.util.ArrayList;

import javax.ejb.EJB;
import javax.servlet.annotation.WebServlet;

import org.openelis.bean.CategoryBean;
import org.openelis.bean.CategoryManager1Bean;
import org.openelis.bean.DictionaryBean;
import org.openelis.domain.DictionaryViewDO;
import org.openelis.domain.IdNameVO;
import org.openelis.ui.common.data.Query;
import org.openelis.ui.server.RemoteServlet;
import org.openelis.manager.CategoryManager1;
import org.openelis.modules.dictionary1.client.DictionaryService1;

@WebServlet("/openelis/dictionary1")
public class DictionaryService1Impl extends RemoteServlet implements DictionaryService1 {

    private static final long serialVersionUID = 1L;

    @EJB
    CategoryManager1Bean      categoryManager1;

    @EJB
    DictionaryBean            dictionary;

    @EJB
    CategoryBean              category;

    public CategoryManager1 fetchById(Integer id) throws Exception {
        try {
            return categoryManager1.fetchById(id);
        } catch (Exception anyE) {
            throw serializeForGWT(anyE);
        }
    }

    public ArrayList<IdNameVO> fetchByEntry(Query query) throws Exception {
        try {
            return dictionary.fetchByEntry(query.getFields());
        } catch (Exception anyE) {
            throw serializeForGWT(anyE);
        }
    }

    public ArrayList<IdNameVO> fetchByCategoryName(String name) throws Exception {
        try {
            return category.fetchByName(name);
        } catch (Exception anyE) {
            throw serializeForGWT(anyE);
        }
    }

    public ArrayList<DictionaryViewDO> fetchByEntry(String entry) throws Exception {
        try {
            return dictionary.fetchByEntry(entry + "%", 10);
        } catch (Exception anyE) {
            throw serializeForGWT(anyE);
        }
    }

    public ArrayList<DictionaryViewDO> fetchByExactEntry(String entry) throws Exception {
        try {
            return dictionary.fetchByEntry(entry, 10);
        } catch (Exception anyE) {
            throw serializeForGWT(anyE);
        }
    }

    // public CategoryManager1 fetchWithEntries(Integer id) throws Exception {
    // try {
    // return categoryManager1.fetchWithEntries(id);
    // } catch (Exception anyE) {
    // throw serializeForGWT(anyE);
    // }
    // }

    public ArrayList<IdNameVO> query(Query query) throws Exception {
        try {
            return category.query(query.getFields(),
                                  query.getPage() * query.getRowsPerPage(),
                                  query.getRowsPerPage());
        } catch (Exception anyE) {
            throw serializeForGWT(anyE);
        }
    }

    public CategoryManager1 add(CategoryManager1 man) throws Exception {
        try {
            return categoryManager1.update(man, false);
        } catch (Exception anyE) {
            throw serializeForGWT(anyE);
        }
    }

    public CategoryManager1 update(CategoryManager1 man) throws Exception {
        try {
            return categoryManager1.update(man, false);
        } catch (Exception anyE) {
            throw serializeForGWT(anyE);
        }
    }

    public CategoryManager1 fetchForUpdate(Integer id) throws Exception {
        try {
            return categoryManager1.fetchForUpdate(id);
        } catch (Exception anyE) {
            throw serializeForGWT(anyE);
        }
    }

    public CategoryManager1 abortUpdate(Integer id) throws Exception {
        try {
            return categoryManager1.unlock(id);
        } catch (Exception anyE) {
            throw serializeForGWT(anyE);
        }
    }

    public DictionaryViewDO validateForDelete(DictionaryViewDO data) throws Exception {
        // TODO: Need to remove returned object.
        try {
            dictionary.validateForDelete(data);
        } catch (Exception anyE) {
            throw serializeForGWT(anyE);
        }
        return data;
    }

    public CategoryManager1 sort(CategoryManager1 cm, boolean ascending) throws Exception {
        try {
            return categoryManager1.sort(cm, ascending);
        } catch (Exception anyE) {
            throw serializeForGWT(anyE);
        }
    }
}
