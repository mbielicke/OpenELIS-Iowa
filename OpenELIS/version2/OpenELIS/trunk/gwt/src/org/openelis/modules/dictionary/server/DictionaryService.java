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

import org.openelis.domain.DictionaryDO;
import org.openelis.domain.DictionaryViewDO;
import org.openelis.domain.IdNameVO;
import org.openelis.gwt.common.data.Query;
import org.openelis.manager.CategoryManager;
import org.openelis.manager.DictionaryManager;
import org.openelis.persistence.EJBFactory;
import org.openelis.remote.CategoryManagerRemote;
import org.openelis.remote.CategoryRemote;
import org.openelis.remote.DictionaryRemote;

public class DictionaryService {

    private static final int rowPP = 20;

    public CategoryManager fetchById(Integer id) throws Exception {
        return remoteManager().fetchById(id);
    }

    public ArrayList<IdNameVO> fetchByEntry(Query query) throws Exception {
        return dictRemote().fetchByEntry(query.getFields());
    }

    public ArrayList<IdNameVO> fetchByCategoryName(String name) throws Exception {
        return remote().fetchByName(name);
    }

    public ArrayList<DictionaryViewDO> fetchByEntry(String entry) throws Exception {
        return dictRemote().fetchByEntry(entry + "%", 10);
    }

    public CategoryManager fetchWithEntries(Integer id) throws Exception {
        return remoteManager().fetchWithEntries(id);
    }

    public ArrayList<IdNameVO> query(Query query) throws Exception {
        return remote().query(query.getFields(), query.getPage() * rowPP, rowPP);
    }

    public CategoryManager add(CategoryManager man) throws Exception {
        return remoteManager().add(man);
    }

    public CategoryManager update(CategoryManager man) throws Exception {
        return remoteManager().update(man);
    }

    public CategoryManager fetchForUpdate(Integer id) throws Exception {
        return remoteManager().fetchForUpdate(id);
    }

    public CategoryManager abortUpdate(Integer id) throws Exception {
        return remoteManager().abortUpdate(id);
    }

    public DictionaryManager fetchEntryByCategoryId(Integer id) throws Exception {
        return remoteManager().fetchEntryByCategoryId(id);
    }

    public DictionaryViewDO validateForDelete(DictionaryViewDO data) throws Exception {
        dictRemote().validateForDelete(data);
        return data;
    }

    private CategoryRemote remote() {
        return (CategoryRemote)EJBFactory.lookup("openelis/CategoryBean/remote");
    }

    private DictionaryRemote dictRemote() {
        return (DictionaryRemote)EJBFactory.lookup("openelis/DictionaryBean/remote");
    }

    private CategoryManagerRemote remoteManager() {
        return (CategoryManagerRemote)EJBFactory.lookup("openelis/CategoryManagerBean/remote");
    }

}
