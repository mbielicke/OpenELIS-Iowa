package org.openelis.modules.instrument.client;

import java.util.ArrayList;

import org.openelis.domain.IdNameVO;
import org.openelis.domain.InstrumentViewDO;
import org.openelis.gwt.common.data.Query;
import org.openelis.manager.InstrumentLogManager;
import org.openelis.manager.InstrumentManager;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface InstrumentServiceIntAsync {

    void abortUpdate(Integer id, AsyncCallback<InstrumentManager> callback);

    void add(InstrumentManager man, AsyncCallback<InstrumentManager> callback);

    void fetchActiveByName(String name, AsyncCallback<ArrayList<InstrumentViewDO>> callback);

    void fetchById(Integer id, AsyncCallback<InstrumentManager> callback);

    void fetchByName(String name, AsyncCallback<ArrayList<InstrumentViewDO>> callback);

    void fetchForUpdate(Integer id, AsyncCallback<InstrumentManager> callback);

    void fetchLogByInstrumentId(Integer id, AsyncCallback<InstrumentLogManager> callback);

    void fetchWithLogs(Integer id, AsyncCallback<InstrumentManager> callback);

    void query(Query query, AsyncCallback<ArrayList<IdNameVO>> callback);

    void update(InstrumentManager man, AsyncCallback<InstrumentManager> callback);

}
