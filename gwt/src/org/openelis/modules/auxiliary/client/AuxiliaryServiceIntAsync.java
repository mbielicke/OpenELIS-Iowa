package org.openelis.modules.auxiliary.client;

import java.util.ArrayList;

import org.openelis.domain.AuxFieldGroupDO;
import org.openelis.domain.IdNameVO;
import org.openelis.gwt.common.data.Query;
import org.openelis.manager.AuxFieldGroupManager;
import org.openelis.manager.AuxFieldManager;
import org.openelis.manager.AuxFieldValueManager;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface AuxiliaryServiceIntAsync {

    void abortUpdate(Integer id, AsyncCallback<AuxFieldGroupManager> callback);

    void add(AuxFieldGroupManager man, AsyncCallback<AuxFieldGroupManager> callback);

    void fetchActive(AsyncCallback<ArrayList<AuxFieldGroupDO>> callback);

    void fetchFieldByGroupId(Integer groupId, AsyncCallback<AuxFieldManager> callback);

    void fetchFieldByGroupIdWithValues(Integer groupId, AsyncCallback<AuxFieldManager> callback);

    void fetchFieldById(Integer id, AsyncCallback<AuxFieldManager> callback);

    void fetchFieldValueByFieldId(Integer fieldId, AsyncCallback<AuxFieldValueManager> callback);

    void fetchForUpdate(Integer id, AsyncCallback<AuxFieldGroupManager> callback);

    void fetchGroupById(Integer id, AsyncCallback<AuxFieldGroupManager> callback);

    void fetchGroupByIdWithFields(Integer id, AsyncCallback<AuxFieldGroupManager> callback);

    void query(Query query, AsyncCallback<ArrayList<IdNameVO>> callback);

    void update(AuxFieldGroupManager man, AsyncCallback<AuxFieldGroupManager> callback);

}
