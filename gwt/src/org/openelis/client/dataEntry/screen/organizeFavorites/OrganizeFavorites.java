package org.openelis.client.dataEntry.screen.organizeFavorites;

import org.openelis.gwt.client.screen.AppScreenForm;
import org.openelis.gwt.client.widget.ButtonPanel;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.ServiceDefTarget;
import com.google.gwt.user.client.ui.Widget;

public class OrganizeFavorites extends AppScreenForm {
	
	private static OrganizeFavoritesScreenIntAsync screenService = (OrganizeFavoritesScreenIntAsync)GWT.create(OrganizeFavoritesScreenInt.class);
    private static ServiceDefTarget target = (ServiceDefTarget)screenService;
    
	public OrganizeFavorites() {
        super();
        String base = GWT.getModuleBaseURL();
        base += "OrganizeFavoritesScreen";
        target.setServiceEntryPoint(base);
        service = screenService;
        formService = screenService;
        getXML();
    }
	
	public void onClick(Widget sender) {
		if (sender == widgets.get("addButton")) {
			new OrganizeFavoritesAdd();
		}
    }

	public void afterDraw(boolean sucess) {  	
        	bpanel = (ButtonPanel)getWidget("buttons");
        	bpanel.enable("cb", true);
        	message.setText("done");
        	//super.afterDraw(sucess);
	}
	
}
