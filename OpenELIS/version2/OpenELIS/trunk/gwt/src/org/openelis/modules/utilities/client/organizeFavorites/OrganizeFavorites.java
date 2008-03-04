package org.openelis.modules.utilities.client.organizeFavorites;

import org.openelis.modules.main.client.OpenELISScreenForm;
import com.google.gwt.user.client.ui.Widget;

public class OrganizeFavorites extends OpenELISScreenForm {
	
    
	public OrganizeFavorites() {
        super("org.openelis.modules.utilities.server.OrganizeFavoritesScreen",false);
    }
	
	public void onClick(Widget sender) {
		if (sender == widgets.get("addButton")) {
			new OrganizeFavoritesAdd();
		}
    }

	public void afterDraw(boolean sucess) {  	
    //    	bpanel = (ButtonPanel)getWidget("buttons");
        	//bpanel.enable("cb", true);
        	message.setText("done");
        	//super.afterDraw(sucess);
	}
	
}
