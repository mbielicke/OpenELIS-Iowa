package org.openelis.modules.label.client;

import java.util.ArrayList;

import org.openelis.domain.IdNameVO;
import org.openelis.domain.LabelDO;
import org.openelis.domain.LabelViewDO;
import org.openelis.gwt.common.data.Query;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface LabelServiceIntAsync {

    void abortUpdate(Integer id, AsyncCallback<LabelViewDO> callback);

    void add(LabelViewDO data, AsyncCallback<LabelViewDO> callback);

    void delete(LabelViewDO data, AsyncCallback<Void> callback);

    void fetchById(Integer id, AsyncCallback<LabelViewDO> callback);

    void fetchByName(String search, AsyncCallback<ArrayList<LabelDO>> callback);

    void fetchForUpdate(Integer id, AsyncCallback<LabelViewDO> callback);

    void query(Query query, AsyncCallback<ArrayList<IdNameVO>> callback);

    void update(LabelViewDO data, AsyncCallback<LabelViewDO> callback);

}
