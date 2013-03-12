package org.openelis.modules.qaevent.client;

import java.util.ArrayList;

import org.openelis.domain.IdNameVO;
import org.openelis.domain.QaEventDO;
import org.openelis.domain.QaEventViewDO;
import org.openelis.gwt.common.data.Query;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface QAEventServiceIntAsync {

    void abortUpdate(Integer id, AsyncCallback<QaEventViewDO> callback);

    void add(QaEventViewDO data, AsyncCallback<QaEventViewDO> callback);

    void fetchByCommon(AsyncCallback<ArrayList<QaEventDO>> callback);

    void fetchById(Integer id, AsyncCallback<QaEventViewDO> callback);

    void fetchByTestId(Integer id, AsyncCallback<ArrayList<QaEventDO>> callback);

    void fetchForUpdate(Integer id, AsyncCallback<QaEventViewDO> callback);

    void query(Query query, AsyncCallback<ArrayList<IdNameVO>> callback);

    void update(QaEventViewDO data, AsyncCallback<QaEventViewDO> callback);

}
