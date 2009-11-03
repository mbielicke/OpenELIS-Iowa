/** Exhibit A - UIRF Open-source Based Public Software License.
* 
* The contents of this file are subject to the UIRF Open-source Based
* Public Software License(the "License"); you may not use this file except
* in compliance with the License. You may obtain a copy of the License at
* openelis.uhl.uiowa.edu
* 
* Software distributed under the License is distributed on an "AS IS"
* basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
* License for the specific language governing rights and limitations
* under the License.
* 
* The Original Code is OpenELIS code.
* 
* The Initial Developer of the Original Code is The University of Iowa.
* Portions created by The University of Iowa are Copyright 2006-2008. All
* Rights Reserved.
* 
* Contributor(s): ______________________________________.
* 
* Alternatively, the contents of this file marked
* "Separately-Licensed" may be used under the terms of a UIRF Software
* license ("UIRF Software License"), in which case the provisions of a
* UIRF Software License are applicable instead of those above. 
*/
package org.openelis.modules.dictionary.server;

import java.util.ArrayList;

import org.openelis.domain.DictionaryDO;
import org.openelis.domain.IdNameVO;
import org.openelis.gwt.common.DatabaseException;
import org.openelis.gwt.common.ValidationErrorsList;
import org.openelis.gwt.common.data.Query;
import org.openelis.manager.DictionaryManager;
import org.openelis.modules.dictionary.client.DictionaryRPC;
import org.openelis.persistence.EJBFactory;
import org.openelis.remote.CategoryRemote;
import org.openelis.remote.DictionaryManagerRemote;
import org.openelis.remote.DictionaryRemote;

public class DictionaryService {

    private static final int rowPP = 19;
    
    public DictionaryManager fetchByCategoryId(Integer categoryId) throws Exception {
        try {
            return remoteManager().fetchByCategoryId(categoryId);
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
    
    public DictionaryManager add(DictionaryManager man) throws Exception {    
        try {
            return remoteManager().add(man);      
        } catch (RuntimeException e) {
            throw new DatabaseException(e);
        }
    }
    
    public DictionaryManager update(DictionaryManager man) throws Exception {    
        try {
            return remoteManager().update(man);      
        } catch (RuntimeException e) {
            throw new DatabaseException(e);
        }
    }    
    
    public DictionaryManager fetchForUpdate(Integer categoryId) throws Exception {    
        try {
            return remoteManager().fetchForUpdate(categoryId);      
        } catch (RuntimeException e) {
            throw new DatabaseException(e);
        }
    }
    
    public DictionaryManager abortUpdate(Integer categoryId) throws Exception {    
        try {
            return remoteManager().abortUpdate(categoryId);      
        } catch (RuntimeException e) {
            throw new DatabaseException(e);
        }
    } 
    
    public ArrayList<IdNameVO> fetchIdEntryByEntry(String entry) throws Exception { 
        return dictRemote().fetchIdEntryByEntry(entry + "%", 10);
    }
    
    public ArrayList<DictionaryDO> fetchByEntry(String entry) throws Exception { 
        return dictRemote().fetchByEntry(entry);
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
    
    private DictionaryManagerRemote remoteManager() {
        return (DictionaryManagerRemote)EJBFactory.lookup("openelis/DictionaryManagerBean/remote");
    }
    
}
