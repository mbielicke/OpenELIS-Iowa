package org.openelis.cache;

import java.util.ArrayList;

import org.openelis.domain.DictionaryDO;

import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.google.gwt.user.client.rpc.XsrfProtectedService;

@RemoteServiceRelativePath("dictionaryCache")
public interface DictionaryCacheServiceInt extends XsrfProtectedService {

    DictionaryDO getBySystemName(String systemName) throws Exception;

    DictionaryDO getById(Integer id) throws Exception;
    
    ArrayList<DictionaryDO> getByIds(ArrayList<Integer> ids) throws Exception;

}