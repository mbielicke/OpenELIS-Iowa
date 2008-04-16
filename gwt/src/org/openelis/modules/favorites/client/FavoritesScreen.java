package org.openelis.modules.favorites.client;

import org.openelis.gwt.screen.AppScreen;
import org.openelis.gwt.screen.ScreenMenuPanel;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.ServiceDefTarget;

public class FavoritesScreen extends AppScreen {
        
	public static FavoritesServiceIntAsync screenService = (FavoritesServiceIntAsync)GWT.create(FavoritesServiceInt.class);
    public static ServiceDefTarget target = (ServiceDefTarget)screenService;
    public boolean editing;
    
	public FavoritesScreen() {	    
        super();
        String base = GWT.getModuleBaseURL();
        base += "OpenELISServlet?service=org.openelis.modules.favorites.server.FavoritesService";
        target.setServiceEntryPoint(base);
        getFavorites();
    }
	
	public void afterDraw(boolean success){
        super.afterDraw(success);
		((ScreenMenuPanel)widgets.get("favoritesMenu")).setSize(getAbsoluteTop());
	}
    
    public void getFavorites(){
        screenService.getFavorites(null, new AsyncCallback() {
            public void onSuccess(Object result){
                redrawScreen((String)result);
            }
            public void onFailure(Throwable caught){
                Window.alert(caught.getMessage());
            }
        });
    }
    
    public void getEditFavorites() {
        screenService.getEditFavorites(new AsyncCallback() {
            public void onSuccess(Object result){
                redrawScreen((String)result);
                editing=true;
                enable(true);
            }
            public void onFailure(Throwable caught){
                Window.alert(caught.getMessage());
            }
        });
    }
    
    public void saveFavorites() {
        screenService.saveFavorites(rpc, new AsyncCallback() {
            public void onSuccess(Object result){
                redrawScreen((String)result);
                editing=false;
            }
            public void onFailure(Throwable caught){
                Window.alert(caught.getMessage());
            }
        });
    }
	
}
