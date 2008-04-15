package org.openelis.modules.favorites.client;

import org.openelis.gwt.screen.AppScreen;
import org.openelis.gwt.screen.ScreenMenuPanel;
import org.openelis.gwt.widget.WindowBrowser;
import org.openelis.modules.main.client.service.OpenELISServiceInt;
import org.openelis.modules.main.client.service.OpenELISServiceIntAsync;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.ServiceDefTarget;

public class EditFavoritesScreen extends AppScreen {
        
	public static OpenELISServiceIntAsync screenService = (OpenELISServiceIntAsync)GWT.create(OpenELISServiceInt.class);
    public static ServiceDefTarget target = (ServiceDefTarget)screenService;
    
	public EditFavoritesScreen() {	    
        super();
        String base = GWT.getModuleBaseURL();
        base += "OpenELISServlet?service=org.openelis.modules.favorites.server.EditFavoritesService";
        target.setServiceEntryPoint(base);
        service = screenService;
        getXML();
    }
	
	public void afterDraw(boolean success) {
		((ScreenMenuPanel)widgets.get("editFavoritesPanel")).setSize(getAbsoluteTop());
		enable(true);
	}
	
}
