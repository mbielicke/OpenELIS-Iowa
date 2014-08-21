package org.openelis.modules.main.client.openelis;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.ServiceDefTarget;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Widget;

import org.openelis.gwt.screen.AppScreen;
import org.openelis.gwt.screen.ClassFactory;
import org.openelis.gwt.screen.ScreenMenuItem;
import org.openelis.gwt.widget.WindowBrowser;
import org.openelis.modules.main.client.service.OpenELISServiceInt;
import org.openelis.modules.main.client.service.OpenELISServiceIntAsync;

public class OpenELIS extends AppScreen {
	
	public static OpenELISServiceIntAsync screenService = (OpenELISServiceIntAsync)GWT.create(OpenELISServiceInt.class);
    public static ServiceDefTarget target = (ServiceDefTarget)screenService;
    
    public static String modules;
    public static WindowBrowser browser;
    
	public OpenELIS() {	    
        super();
        String base = GWT.getModuleBaseURL();
        base += "OpenELISServlet?service=org.openelis.modules.main.server.OpenELISService";
        target.setServiceEntryPoint(base);
        service = screenService;
        getXML();
    }

   public void afterDraw(boolean Success) {
        	browser = (WindowBrowser)getWidget("browser");
        	browser.setBrowserHeight();
    }

    public void onClick(Widget item) {
        if(item instanceof ScreenMenuItem){
            OpenELIS.browser.addScreen((AppScreen)ClassFactory.forName((String)((ScreenMenuItem)item).getUserObject()));
        }
    }
    
    public void setStyleToAllCellsInCol(FlexTable table, int col,String style){
    	for(int i=0;i<table.getRowCount(); i++){
    		table.getCellFormatter().addStyleName(i,col, style);
    	}
    }
}
