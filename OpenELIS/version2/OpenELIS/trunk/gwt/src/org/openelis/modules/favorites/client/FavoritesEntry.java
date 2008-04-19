package org.openelis.modules.favorites.client;

import org.openelis.gwt.screen.AppModule;
import org.openelis.gwt.screen.ClassFactory;
import org.openelis.modules.main.client.openelis.OpenELIS;

public class FavoritesEntry implements AppModule {

    public void onModuleLoad() {
    	OpenELIS.modules.addItem(getModuleName());
        ClassFactory.addClass(new String[] {"FavoritesScreen"}, 
                               new ClassFactory.Factory() {
                                   public Object newInstance(Object[] args) {
                                       return new FavoritesScreen();
                                   }
                               }
        );
    }

    public String getModuleName() {
        return "Favorites";
    }

}
