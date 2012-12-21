package org.openelis.modules.dictionary.client;

import java.util.ArrayList;

import org.openelis.domain.DictionaryViewDO;
import org.openelis.domain.IdNameVO;
import org.openelis.gwt.common.data.Query;
import org.openelis.manager.CategoryManager;
import org.openelis.manager.DictionaryManager;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("dictionary")
public interface DictionaryServiceInt extends RemoteService {

    CategoryManager fetchById(Integer id) throws Exception;

    ArrayList<IdNameVO> fetchByEntry(Query query) throws Exception;

    ArrayList<IdNameVO> fetchByCategoryName(String name) throws Exception;

    ArrayList<DictionaryViewDO> fetchByEntry(String entry) throws Exception;

    CategoryManager fetchWithEntries(Integer id) throws Exception;

    ArrayList<IdNameVO> query(Query query) throws Exception;

    CategoryManager add(CategoryManager man) throws Exception;

    CategoryManager update(CategoryManager man) throws Exception;

    CategoryManager fetchForUpdate(Integer id) throws Exception;

    CategoryManager abortUpdate(Integer id) throws Exception;

    DictionaryManager fetchEntryByCategoryId(Integer id) throws Exception;

    DictionaryViewDO validateForDelete(DictionaryViewDO data) throws Exception;

}