package org.openelis.web.cache;

import org.openelis.domain.DictionaryDO;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface DictionaryCacheServiceIntAsync {

    void getById(Integer id, AsyncCallback<DictionaryDO> callback);

    void getBySystemName(String systemName, AsyncCallback<DictionaryDO> callback);

}
