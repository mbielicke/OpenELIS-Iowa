package org.openelis.modules.analyte.client;

import java.util.ArrayList;

import org.openelis.domain.AnalyteDO;
import org.openelis.domain.AnalyteViewDO;
import org.openelis.domain.IdNameVO;
import org.openelis.gwt.common.data.Query;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface AnalyteServiceIntAsync {

    void abortUpdate(Integer id, AsyncCallback<AnalyteViewDO> callback);

    void add(AnalyteViewDO data, AsyncCallback<AnalyteDO> callback);

    void delete(AnalyteDO data, AsyncCallback<Void> callback);

    void fetchById(Integer id, AsyncCallback<AnalyteViewDO> callback);

    void fetchByName(String search, AsyncCallback<ArrayList<AnalyteDO>> callback);

    void fetchForUpdate(Integer id, AsyncCallback<AnalyteViewDO> callback);

    void query(Query query, AsyncCallback<ArrayList<IdNameVO>> callback);

    void update(AnalyteViewDO data, AsyncCallback<AnalyteDO> callback);

}
