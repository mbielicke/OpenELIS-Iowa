package org.openelis.modules.favorites.client;

import org.openelis.gwt.screen.AppScreenForm;
import org.openelis.gwt.screen.ClassFactory;
import org.openelis.gwt.screen.ScreenMenuPanel;
import org.openelis.modules.main.client.openelis.OpenELIS;
import org.openelis.modules.main.client.service.OpenELISServiceInt;
import org.openelis.modules.main.client.service.OpenELISServiceIntAsync;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.ServiceDefTarget;
import com.google.gwt.user.client.ui.VerticalPanel;

public class EditFavoritesScreen extends AppScreenForm {
        
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
	
	public void afterCommitUpdate(boolean success) {
		super.afterCommitUpdate(success);
		VerticalPanel fmp = (VerticalPanel)((OpenELIS)ClassFactory.forName("OpenELIS")).getWidget("favoritesPanel");
		fmp.remove(1);
		fmp.add(new FavoritesScreen());
	}
	
}
