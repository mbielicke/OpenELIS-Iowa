package org.openelis.modules.dictionary.client;

import java.util.ArrayList;

import org.openelis.domain.DictionaryViewDO;
import org.openelis.domain.IdNameVO;
import org.openelis.gwt.common.data.Query;
import org.openelis.manager.CategoryManager;
import org.openelis.manager.DictionaryManager;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface DictionaryServiceIntAsync {

    void abortUpdate(Integer id, AsyncCallback<CategoryManager> callback);

    void add(CategoryManager man, AsyncCallback<CategoryManager> callback);

    void fetchByCategoryName(String name, AsyncCallback<ArrayList<IdNameVO>> callback);

    void fetchByEntry(Query query, AsyncCallback<ArrayList<IdNameVO>> callback);

    void fetchByEntry(String entry, AsyncCallback<ArrayList<DictionaryViewDO>> callback);

    void fetchById(Integer id, AsyncCallback<CategoryManager> callback);

    void fetchEntryByCategoryId(Integer id, AsyncCallback<DictionaryManager> callback);

    void fetchForUpdate(Integer id, AsyncCallback<CategoryManager> callback);

    void fetchWithEntries(Integer id, AsyncCallback<CategoryManager> callback);

    void query(Query query, AsyncCallback<ArrayList<IdNameVO>> callback);

    void update(CategoryManager man, AsyncCallback<CategoryManager> callback);

    void validateForDelete(DictionaryViewDO data, AsyncCallback<DictionaryViewDO> callback);

}
