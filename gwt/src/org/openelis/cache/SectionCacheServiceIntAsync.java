package org.openelis.cache;

import java.util.ArrayList;

import org.openelis.domain.SectionViewDO;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface SectionCacheServiceIntAsync {
    void getList(String name, AsyncCallback<ArrayList<SectionViewDO>> callback);
}
