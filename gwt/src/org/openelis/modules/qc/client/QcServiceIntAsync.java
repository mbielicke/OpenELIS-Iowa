package org.openelis.modules.qc.client;

import java.util.ArrayList;

import org.openelis.domain.IdNameVO;
import org.openelis.domain.QcDO;
import org.openelis.domain.QcLotDO;
import org.openelis.domain.QcLotViewDO;
import org.openelis.gwt.common.data.Query;
import org.openelis.manager.QcAnalyteManager;
import org.openelis.manager.QcLotManager;
import org.openelis.manager.QcManager;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface QcServiceIntAsync {

    void abortUpdate(Integer id, AsyncCallback<QcManager> callback);

    void add(QcManager man, AsyncCallback<QcManager> callback);

    void duplicate(Integer id, AsyncCallback<QcManager> callback);

    void fetchActiveByName(Query query, AsyncCallback<ArrayList<QcLotViewDO>> callback);

    void fetchActiveByName(String search, AsyncCallback<ArrayList<QcLotViewDO>> callback);

    void fetchAnalyteByQcId(Integer id, AsyncCallback<QcAnalyteManager> callback);

    void fetchById(Integer id, AsyncCallback<QcManager> callback);

    void fetchByName(String search, AsyncCallback<ArrayList<QcDO>> callback);

    void fetchForUpdate(Integer id, AsyncCallback<QcManager> callback);

    void fetchLotById(Integer id, AsyncCallback<QcLotViewDO> callback);

    void fetchLotByQcId(Integer id, AsyncCallback<QcLotManager> callback);

    void fetchWithAnalytes(Integer id, AsyncCallback<QcManager> callback);

    void fetchWithLots(Integer id, AsyncCallback<QcManager> callback);

    void query(Query query, AsyncCallback<ArrayList<IdNameVO>> callback);

    void update(QcManager man, AsyncCallback<QcManager> callback);

    void validateForDelete(QcLotViewDO data, AsyncCallback<QcLotDO> callback);

}
