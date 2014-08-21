package org.openelis.web.cache;

import java.util.ArrayList;

import org.openelis.domain.CategoryCacheVO;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface CategoryCacheServiceIntAsync {

    void getBySystemName(String systemName, AsyncCallback<CategoryCacheVO> callback);

    void getBySystemNames(String[] systemNames, AsyncCallback<ArrayList<CategoryCacheVO>> callback);

}
