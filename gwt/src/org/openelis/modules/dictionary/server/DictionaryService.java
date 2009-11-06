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
import org.openelis.domain.IdNameVO;
import org.openelis.gwt.common.DatabaseException;
import org.openelis.gwt.common.ValidationErrorsList;
import org.openelis.gwt.common.data.Query;
import org.openelis.manager.CategoryManager;
import org.openelis.manager.DictionaryManager;
import org.openelis.modules.dictionary.client.DictionaryRPC;
import org.openelis.persistence.EJBFactory;
import org.openelis.remote.CategoryRemote;
import org.openelis.remote.CategoryManagerRemote;
import org.openelis.remote.DictionaryRemote;

public class DictionaryService {

    private static final int rowPP = 19;

    public CategoryManager fetchById(Integer id) throws Exception {
        try {
            return remoteManager().fetchById(id);
        } catch (RuntimeException e) {
            throw new DatabaseException(e);
        }
    }

    public ArrayList<IdNameVO> fetchByEntry(Query query) throws Exception {
        try {
            return dictRemote().fetchByEntryAndCategoryId(query.getFields());
        } catch (RuntimeException e) {
            throw new DatabaseException(e);
        }
    }

    public ArrayList<IdNameVO> fetchByCategoryName(String name) throws Exception {
        try {
            return remote().fetchByName(name);
        } catch (RuntimeException e) {
            throw new DatabaseException(e);
        }
    }

    public ArrayList<DictionaryDO> fetchByEntry(String entry) throws Exception {
        try {
            return dictRemote().fetchByEntry(entry+"%",10);
        } catch (RuntimeException e) {
            throw new DatabaseException(e);
        }
    }
    
    public CategoryManager fetchWithEntries(Integer id) throws Exception {
        try {
            return remoteManager().fetchWithEntries(id);
        } catch (RuntimeException e) {
            throw new DatabaseException(e);
        }
    }

    public ArrayList<IdNameVO> query(Query query) throws Exception {
        try {
            return remote().query(query.getFields(), query.getPage() * rowPP, rowPP);
        } catch (RuntimeException e) {
            throw new DatabaseException(e);
        }
    }

    public CategoryManager add(CategoryManager man) throws Exception {
        try {
            return remoteManager().add(man);
        } catch (RuntimeException e) {
            throw new DatabaseException(e);
        }
    }

    public CategoryManager update(CategoryManager man) throws Exception {
        try {
            return remoteManager().update(man);
        } catch (RuntimeException e) {
            throw new DatabaseException(e);
        }
    }

    public CategoryManager fetchForUpdate(Integer id) throws Exception {
        try {
            return remoteManager().fetchForUpdate(id);
        } catch (RuntimeException e) {
            throw new DatabaseException(e);
        }
    }

    public CategoryManager abortUpdate(Integer id) throws Exception {
        try {
            return remoteManager().abortUpdate(id);
        } catch (RuntimeException e) {
            throw new DatabaseException(e);
        }
    }
    
    public DictionaryManager fetchEntryByCategoryId(Integer id) throws Exception {
        try {
            return remoteManager().fetchEntryByCategoryId(id);
        } catch (RuntimeException e) {
            throw new DatabaseException(e);
        }
    }
    

    public DictionaryRPC validateDelete(DictionaryRPC rpc) throws Exception {
        try {
            dictRemote().validateForDelete(rpc.data);
        } catch (RuntimeException e) {
            throw new DatabaseException(e);
        } catch (ValidationErrorsList e) {
            rpc.valid = false;
        }

        return rpc;
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
