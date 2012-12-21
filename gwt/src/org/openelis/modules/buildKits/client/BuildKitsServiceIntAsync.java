package org.openelis.modules.buildKits.client;

import org.openelis.manager.BuildKitManager;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface BuildKitsServiceIntAsync {

    void add(BuildKitManager man, AsyncCallback<BuildKitManager> callback);

}
