package org.openelis.modules.main.client.openelis;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.ServiceDefTarget;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import org.openelis.gwt.common.data.ConstantMap;
import org.openelis.gwt.screen.AppConstants;
import org.openelis.gwt.screen.AppScreen;
import org.openelis.gwt.screen.ClassFactory;
import org.openelis.gwt.screen.ScreenMenuItem;
import org.openelis.gwt.screen.ScreenMenuPanel;
import org.openelis.gwt.screen.ScreenWidget;
import org.openelis.gwt.widget.WindowBrowser;
import org.openelis.modules.favorites.client.FavoritesScreen;
import org.openelis.modules.main.client.service.OpenELISServiceInt;
import org.openelis.modules.main.client.service.OpenELISServiceIntAsync;

public class OpenELIS extends AppScreen {
	
	public static OpenELISServiceIntAsync screenService = (OpenELISServiceIntAsync)GWT.create(OpenELISServiceInt.class);
    public static ServiceDefTarget target = (ServiceDefTarget)screenService;
    
    public static String modules;
    public static WindowBrowser browser;
    private FavoritesScreen fv;
    
	public OpenELIS() {	    
        super();
        String base = GWT.getModuleBaseURL();
        base += "OpenELISServlet?service=org.openelis.modules.main.server.OpenELISService";
        target.setServiceEntryPoint(base);
        service = screenService;
        getXMLData();
    }

   public void afterDraw(boolean Success) {
        	browser = (WindowBrowser)getWidget("browser");
        	browser.setBrowserHeight();
            ((AppConstants)ClassFactory.forName("AppConstants")).addMapp((ConstantMap)initData[0]);
    }

    public void onClick(Widget item) {
    	if(item == getWidget("EditFavorites")){
    	    if(fv.editing)
                fv.saveFavorites();
            else
                fv.getEditFavorites();
            return;
    	}
        if(item instanceof ScreenMenuItem){
        	if(((String)((ScreenMenuItem)item).getUserObject()).equals("FavoritesMenu")){
                VerticalPanel fmp = (VerticalPanel)getWidget("favoritesPanel");
                if(fmp.getWidgetCount() == 1){
                	fv = new FavoritesScreen();
                	fmp.add(fv);
                }
                fmp.setVisible(!fmp.isVisible());
                browser.setBrowserHeight();
                return;
        	}else if(((String)((ScreenMenuItem)item).getUserObject()).equals("Logout")){
        		//FIXME logout code should go here soon
        		return;
        	}
            OpenELIS.browser.addScreen((AppScreen)ClassFactory.forName((String)((ScreenMenuItem)item).getUserObject()));
        }
    }
    
    public void setStyleToAllCellsInCol(FlexTable table, int col,String style){
    	for(int i=0;i<table.getRowCount(); i++){
    		table.getCellFormatter().addStyleName(i,col, style);
    	}
    }
    
    public void onDrop(Widget sender, Widget source) {
        VerticalPanel vp = (VerticalPanel)((ScreenMenuPanel)sender).getWidget();
        for(int i = 0; i < vp.getWidgetCount(); i++){
            if(vp.getWidget(i).getAbsoluteTop() > source.getAbsoluteTop()){
                vp.insert((Widget)((ScreenWidget)source).getUserObject(), i);
                break;
            }
        }
    }
}
