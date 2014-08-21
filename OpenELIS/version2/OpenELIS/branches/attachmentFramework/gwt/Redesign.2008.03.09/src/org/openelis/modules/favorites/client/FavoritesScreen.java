package org.openelis.modules.favorites.client;

import org.openelis.modules.main.client.OpenELISScreenForm;
import com.google.gwt.user.client.ui.Widget;

public class FavoritesScreen extends OpenELISScreenForm {
	
    
	public FavoritesScreen() {
        super("org.openelis.modules.favorites.server.FavoritesService",false);
        name="Favorites";
    }
	
	public void onClick(Widget sender) {
		if (sender == widgets.get("addButton")) {
			new FavoritesAdd();
		}
    }

	public void afterDraw(boolean sucess) {  	
    //    	bpanel = (ButtonPanel)getWidget("buttons");
        	//bpanel.enable("cb", true);
        	message.setText("done");
        	//super.afterDraw(sucess);
	}
	
}
