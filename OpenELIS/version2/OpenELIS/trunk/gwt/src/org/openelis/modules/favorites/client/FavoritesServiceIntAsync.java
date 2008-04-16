package org.openelis.modules.favorites.client;

import com.google.gwt.user.client.rpc.AsyncCallback;

import org.openelis.gwt.common.FormRPC;
import org.openelis.gwt.common.Preferences;

public interface FavoritesServiceIntAsync {
    
    public void getFavorites(Preferences prefs, AsyncCallback callback);
    
    public void getEditFavorites(AsyncCallback callback);
    
    public void saveFavorites(FormRPC rpc, AsyncCallback callback);

}
