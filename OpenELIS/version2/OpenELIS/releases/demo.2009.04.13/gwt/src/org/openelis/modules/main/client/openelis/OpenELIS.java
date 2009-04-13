/** Exhibit A - UIRF Open-source Based Public Software License.
* 
* The contents of this file are subject to the UIRF Open-source Based
* Public Software License(the "License"); you may not use this file except
* in compliance with the License. You may obtain a copy of the License at
* openelis.uhl.uiowa.edu
* 
* Software distributed under the License is distributed on an "AS IS"
* basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
* License for the specific language governing rights and limitations
* under the License.
* 
* The Original Code is OpenELIS code.
* 
* The Initial Developer of the Original Code is The University of Iowa.
* Portions created by The University of Iowa are Copyright 2006-2008. All
* Rights Reserved.
* 
* Contributor(s): ______________________________________.
* 
* Alternatively, the contents of this file marked
* "Separately-Licensed" may be used under the terms of a UIRF Software
* license ("UIRF Software License"), in which case the provisions of a
* UIRF Software License are applicable instead of those above. 
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

import org.openelis.gwt.common.RPC;
import org.openelis.gwt.common.SecurityUtil;
import org.openelis.gwt.screen.AppScreen;
import org.openelis.gwt.screen.ClassFactory;
import org.openelis.gwt.screen.ScreenMenuPanel;
import org.openelis.gwt.screen.ScreenWidget;
import org.openelis.gwt.widget.MenuItem;
import org.openelis.gwt.widget.WindowBrowser;
import org.openelis.modules.favorites.client.FavoritesScreen;
import org.openelis.modules.main.client.service.OpenELISServiceInt;
import org.openelis.modules.main.client.service.OpenELISServiceIntAsync;

import java.util.ArrayList;

public class OpenELIS extends AppScreen<OpenELISForm> implements ClickListener {
    
    public static OpenELISServiceIntAsync<OpenELISForm, RPC> screenService = (OpenELISServiceIntAsync)GWT.create(OpenELISServiceInt.class);
    public static ServiceDefTarget target = (ServiceDefTarget)screenService;
    
    public static ArrayList<String> modules = new ArrayList<String>();
    public static WindowBrowser browser;
    public static SecurityUtil security;
    private FavoritesScreen fv;
    
	public OpenELIS() {	    
        super();              
        target.setServiceEntryPoint(target.getServiceEntryPoint()+"?service=org.openelis.modules.main.server.OpenELISService");
        service = screenService;
        OpenELISForm form = new OpenELISForm();
        form.modules = modules;
        getScreen(form);
    }

   public void afterDraw(boolean Success) {
        	browser = (WindowBrowser)getWidget("browser");
        	browser.setBrowserHeight();
            AppScreen.consts = form.appConstants;
            security = form.security;
    }

    public void onClick(Widget item) {
    	if(item == getWidget("EditFavorites")){
    	    if(fv.editing)
                fv.saveFavorites();
            else
                fv.getEditFavorites();
            return;
    	}
        if(item instanceof MenuItem){
        	if(((String)((MenuItem)item).objClass).equals("FavoritesMenu")){
                VerticalPanel fmp = (VerticalPanel)getWidget("favoritesPanel");
                if(fmp.getWidgetCount() == 1){
                	fv = new FavoritesScreen();
                	fmp.add(fv);
                }
                fmp.setVisible(!fmp.isVisible());
                browser.setBrowserHeight();
                return;
        	}else if(((String)((MenuItem)item).objClass).equals("Logout")){
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
            MenuItem menuItem = (MenuItem)item;
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
