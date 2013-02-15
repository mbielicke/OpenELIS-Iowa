package org.openelis.modules.preferences.client;

import org.openelis.gwt.screen.Callback;
import org.openelis.manager.Preferences;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class PreferencesService implements PreferencesServiceInt, PreferencesServiceIntAsync {
    
    static PreferencesService instance;
    
    PreferencesServiceIntAsync service;
    
    public static PreferencesService get() {
        if(instance == null)
            instance = new PreferencesService();
        
        return instance;
    }
    
    private PreferencesService() {
        service = (PreferencesServiceIntAsync)GWT.create(PreferencesServiceInt.class);
    }

    @Override
    public void flush(Preferences prefs, AsyncCallback<Void> callback) {
        service.flush(prefs, callback);
    }

    @Override
    public void systemRoot(AsyncCallback<Preferences> callback) {
        service.systemRoot(callback);
    }

    @Override
    public void userRoot(AsyncCallback<Preferences> callback) {
        service.userRoot(callback);
    }

    @Override
    public Preferences systemRoot() throws Exception {
        Callback<Preferences> callback;
        
        callback = new Callback<Preferences>();
        service.systemRoot(callback);
        return callback.getResult();
    }

    @Override
    public Preferences userRoot() throws Exception {
        Callback<Preferences> callback;
        
        callback = new Callback<Preferences>();
        service.userRoot(callback);
        return callback.getResult();
    }

    @Override
    public void flush(Preferences prefs) throws Exception {
        Callback<Void> callback;
        
        callback = new Callback<Void>();
        service.flush(prefs, callback);
        callback.getResult();
    }

}
