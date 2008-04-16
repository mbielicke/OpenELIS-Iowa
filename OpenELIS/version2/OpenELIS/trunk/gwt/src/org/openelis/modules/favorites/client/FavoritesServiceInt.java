package org.openelis.modules.favorites.client;

import com.google.gwt.user.client.rpc.RemoteService;

import org.openelis.gwt.common.FormRPC;
import org.openelis.gwt.common.Preferences;

public interface FavoritesServiceInt extends RemoteService {
    
    public String getFavorites(Preferences prefs);
    
    public String getEditFavorites();
    
    public String saveFavorites(FormRPC rpc);

}
