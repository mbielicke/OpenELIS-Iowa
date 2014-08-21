package org.openelis.web.cache;

import org.openelis.domain.DictionaryDO;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("dictionaryCache")
public interface DictionaryCacheServiceInt extends RemoteService {

    DictionaryDO getBySystemName(String systemName) throws Exception;

    DictionaryDO getById(Integer id) throws Exception;

}