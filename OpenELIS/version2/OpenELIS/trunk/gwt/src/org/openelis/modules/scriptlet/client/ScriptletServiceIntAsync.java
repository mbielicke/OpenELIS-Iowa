package org.openelis.modules.scriptlet.client;

import java.util.ArrayList;

import org.openelis.domain.IdNameVO;
import org.openelis.scriptlet.ScriptletObject;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface ScriptletServiceIntAsync {

    void fetchByName(String search, AsyncCallback<ArrayList<IdNameVO>> callback);
    
    void run(ScriptletObject so, AsyncCallback<ScriptletObject> callback);

}
