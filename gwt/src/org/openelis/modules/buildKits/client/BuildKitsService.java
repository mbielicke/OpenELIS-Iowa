package org.openelis.modules.buildKits.client;

import org.openelis.gwt.screen.Callback;
import org.openelis.manager.BuildKitManager;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class BuildKitsService implements BuildKitsServiceInt, BuildKitsServiceIntAsync{
    
    static BuildKitsService instance;
    
    BuildKitsServiceIntAsync service;
    
    public static BuildKitsService get() {
        if (instance == null)
            instance = new BuildKitsService();
        
        return instance;
    }
    
    private BuildKitsService() {
        service = (BuildKitsServiceIntAsync)GWT.create(BuildKitsServiceInt.class);
    }

    @Override
    public void add(BuildKitManager man, AsyncCallback<BuildKitManager> callback) {
        service.add(man, callback);
    }

    @Override
    public BuildKitManager add(BuildKitManager man) throws Exception {
        Callback<BuildKitManager> callback;
        
        callback = new Callback<BuildKitManager>();
        service.add(man, callback);
        return callback.getResult();
    }

    
}
