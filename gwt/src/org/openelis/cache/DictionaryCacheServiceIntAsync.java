package org.openelis.cache;

import java.util.ArrayList;

import org.openelis.domain.DictionaryDO;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface DictionaryCacheServiceIntAsync {

    void getById(Integer id, AsyncCallback<DictionaryDO> callback);

    void getBySystemName(String systemName, AsyncCallback<DictionaryDO> callback);

    void getByIds(ArrayList<Integer> ids, AsyncCallback<ArrayList<DictionaryDO>> callback);
}
