package org.openelis.modules.scriptlet.client;

import java.util.ArrayList;

import org.openelis.domain.IdNameVO;
import org.openelis.domain.ScriptletDO;
import org.openelis.ui.common.data.Query;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface ScriptletServiceIntAsync {

    void fetchByName(String search, AsyncCallback<ArrayList<IdNameVO>> callback);
    
    void query(Query query, AsyncCallback<ArrayList<IdNameVO>> callback);
    
    void fetchById(Integer id, AsyncCallback<ScriptletDO> callback);

    void fetchByIds(ArrayList<Integer> ids, AsyncCallback<ArrayList<ScriptletDO>> callback);
    
    void fetchForUpdate(Integer id, AsyncCallback<ScriptletDO> callback);
    
    void add(ScriptletDO data, AsyncCallback<ScriptletDO> callback);
    
    void update(ScriptletDO data, AsyncCallback<ScriptletDO> callback);
    
    void abortUpdate(Integer id, AsyncCallback<ScriptletDO> callback);
}
