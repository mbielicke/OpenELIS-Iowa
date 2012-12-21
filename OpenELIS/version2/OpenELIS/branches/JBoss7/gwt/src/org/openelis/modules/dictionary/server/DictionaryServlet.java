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
package org.openelis.modules.dictionary.server;

import java.util.ArrayList;

import javax.ejb.EJB;
import javax.servlet.annotation.WebServlet;

import org.openelis.bean.CategoryBean;
import org.openelis.bean.CategoryManagerBean;
import org.openelis.bean.DictionaryBean;
import org.openelis.domain.DictionaryViewDO;
import org.openelis.domain.IdNameVO;
import org.openelis.gwt.common.data.Query;
import org.openelis.gwt.server.AppServlet;
import org.openelis.manager.CategoryManager;
import org.openelis.manager.DictionaryManager;
import org.openelis.modules.dictionary.client.DictionaryServiceInt;

@WebServlet("/openelis/dictionary")
public class DictionaryServlet extends AppServlet implements DictionaryServiceInt {
    
    private static final long serialVersionUID = 1L;
    
    @EJB
    CategoryManagerBean categoryManager;
    
    @EJB
    DictionaryBean      dictionary;
    
    @EJB
    CategoryBean        category;

    public CategoryManager fetchById(Integer id) throws Exception {
        return categoryManager.fetchById(id);
    }

    public ArrayList<IdNameVO> fetchByEntry(Query query) throws Exception {
        return dictionary.fetchByEntry(query.getFields());
    }

    public ArrayList<IdNameVO> fetchByCategoryName(String name) throws Exception {
        return category.fetchByName(name);
    }

    public ArrayList<DictionaryViewDO> fetchByEntry(String entry) throws Exception {
        return dictionary.fetchByEntry(entry + "%", 10);
    }

    public CategoryManager fetchWithEntries(Integer id) throws Exception {
        return categoryManager.fetchWithEntries(id);
    }

    public ArrayList<IdNameVO> query(Query query) throws Exception {
        return category.query(query.getFields(), query.getPage() * query.getRowsPerPage(), query.getRowsPerPage());
    }

    public CategoryManager add(CategoryManager man) throws Exception {
        return categoryManager.add(man);
    }

    public CategoryManager update(CategoryManager man) throws Exception {
        return categoryManager.update(man);
    }

    public CategoryManager fetchForUpdate(Integer id) throws Exception {
        return categoryManager.fetchForUpdate(id);
    }

    public CategoryManager abortUpdate(Integer id) throws Exception {
        return categoryManager.abortUpdate(id);
    }

    public DictionaryManager fetchEntryByCategoryId(Integer id) throws Exception {
        return categoryManager.fetchEntryByCategoryId(id);
    }

    public DictionaryViewDO validateForDelete(DictionaryViewDO data) throws Exception {
    	dictionary.validateForDelete(data);
        return data;
    }
}
