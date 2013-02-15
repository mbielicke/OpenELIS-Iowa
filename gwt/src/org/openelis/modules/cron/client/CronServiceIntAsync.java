package org.openelis.modules.cron.client;

import java.util.ArrayList;

import org.openelis.domain.CronDO;
import org.openelis.domain.IdNameVO;
import org.openelis.gwt.common.data.Query;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface CronServiceIntAsync {

    void abortUpdate(Integer id, AsyncCallback<CronDO> callback);

    void add(CronDO data, AsyncCallback<CronDO> callback);

    void delete(CronDO data, AsyncCallback<Void> callback);

    void fetchById(Integer id, AsyncCallback<CronDO> callback);

    void fetchForUpdate(Integer id, AsyncCallback<CronDO> callback);

    void query(Query query, AsyncCallback<ArrayList<IdNameVO>> callback);

    void update(CronDO data, AsyncCallback<CronDO> callback);

}
