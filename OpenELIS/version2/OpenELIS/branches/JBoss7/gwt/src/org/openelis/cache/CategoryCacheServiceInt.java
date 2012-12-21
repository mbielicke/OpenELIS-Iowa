package org.openelis.cache;

import java.util.ArrayList;

import org.openelis.domain.CategoryCacheVO;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("categoryCache")
public interface CategoryCacheServiceInt extends RemoteService {

    CategoryCacheVO getBySystemName(String systemName) throws Exception;

    ArrayList<CategoryCacheVO> getBySystemNames(String... systemNames) throws Exception;

}