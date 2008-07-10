/**
* The contents of this file are subject to the Mozilla Public License
* Version 1.1 (the "License"); you may not use this file except in
* compliance with the License. You may obtain a copy of the License at
* http://www.mozilla.org/MPL/
* 
* Software distributed under the License is distributed on an "AS IS"
* basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
* License for the specific language governing rights and limitations under
* the License.
* 
* The Original Code is OpenELIS code.
* 
* Copyright (C) The University of Iowa.  All Rights Reserved.
*/
package org.openelis.modules.main.client.openelis;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.ServiceDefTarget;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import org.openelis.gwt.common.data.CollectionField;
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

import java.util.HashMap;

public class OpenELIS extends AppScreen implements ClickListener{
	
	public static OpenELISServiceIntAsync screenService = (OpenELISServiceIntAsync)GWT.create(OpenELISServiceInt.class);
    public static ServiceDefTarget target = (ServiceDefTarget)screenService;
    
    public static CollectionField modules = new CollectionField();
    public static WindowBrowser browser;
    private FavoritesScreen fv;
    
	public OpenELIS() {	    
        super();
        String base = GWT.getModuleBaseURL();
        base += "OpenELISServlet?service=org.openelis.modules.main.server.OpenELISService";
        target.setServiceEntryPoint(base);
        service = screenService;
        HashMap map = new HashMap();
        map.put("modules",modules);
        getXMLData(map);
    }

   public void afterDraw(boolean Success) {
        	browser = (WindowBrowser)getWidget("browser");
        	browser.setBrowserHeight();
            ((AppConstants)ClassFactory.forName("AppConstants")).addMapp((ConstantMap)initData.get("AppConstants"));
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
        	if(((String)((ScreenMenuItem)item).objClass).equals("FavoritesMenu")){
                VerticalPanel fmp = (VerticalPanel)getWidget("favoritesPanel");
                if(fmp.getWidgetCount() == 1){
                	fv = new FavoritesScreen();
                	fmp.add(fv);
                }
                fmp.setVisible(!fmp.isVisible());
                browser.setBrowserHeight();
                return;
        	}else if(((String)((ScreenMenuItem)item).objClass).equals("Logout")){
        	    screenService.logout(new AsyncCallback() {
                   public void onSuccess(Object result){
                       Window.open("http://www.uhl.uiowa.edu", "_self", null);
                   }
                   public void onFailure(Throwable caught){
                       Window.alert(caught.getMessage());
                   }
                });
        		return;
        	}
            ScreenMenuItem menuItem = (ScreenMenuItem)item;
            if(menuItem.args != null){
                OpenELIS.browser.addScreen((AppScreen)ClassFactory.forName(menuItem.objClass,menuItem.args),menuItem.key);
            }else{    
                OpenELIS.browser.addScreen((AppScreen)ClassFactory.forName(menuItem.objClass),menuItem.key);
            }
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
