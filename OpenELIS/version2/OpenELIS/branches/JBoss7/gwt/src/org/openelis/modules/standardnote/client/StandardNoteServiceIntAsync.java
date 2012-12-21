package org.openelis.modules.standardnote.client;

import java.util.ArrayList;

import org.openelis.domain.IdNameVO;
import org.openelis.domain.StandardNoteDO;
import org.openelis.gwt.common.data.Query;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface StandardNoteServiceIntAsync {

    void abortUpdate(Integer id, AsyncCallback<StandardNoteDO> callback);

    void add(StandardNoteDO data, AsyncCallback<StandardNoteDO> callback);

    void delete(StandardNoteDO data, AsyncCallback<Void> callback);

    void fetchById(Integer id, AsyncCallback<StandardNoteDO> callback);

    void fetchByNameOrDescription(Query query, AsyncCallback<ArrayList<StandardNoteDO>> callback);

    void fetchBySystemVariableName(String name, AsyncCallback<StandardNoteDO> callback);

    void fetchByType(Integer typeId, AsyncCallback<ArrayList<StandardNoteDO>> callback);

    void fetchForUpdate(Integer id, AsyncCallback<StandardNoteDO> callback);

    void query(Query query, AsyncCallback<ArrayList<IdNameVO>> callback);

    void update(StandardNoteDO data, AsyncCallback<StandardNoteDO> callback);

}
