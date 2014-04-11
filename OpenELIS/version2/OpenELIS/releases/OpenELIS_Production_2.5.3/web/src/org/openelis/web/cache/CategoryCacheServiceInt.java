package org.openelis.web.cache;

import java.util.ArrayList;

import org.openelis.domain.CategoryCacheVO;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.google.gwt.user.client.rpc.XsrfProtectedService;

@RemoteServiceRelativePath("categoryCache")
public interface CategoryCacheServiceInt extends XsrfProtectedService {

    CategoryCacheVO getBySystemName(String systemName) throws Exception;

    ArrayList<CategoryCacheVO> getBySystemNames(String... systemNames) throws Exception;

}