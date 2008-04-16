package org.openelis.modules.favorites.client;

import org.openelis.gwt.screen.AppModule;
import org.openelis.gwt.screen.ClassFactory;

public class FavoritesEntry implements AppModule {

    public void onModuleLoad() {
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
