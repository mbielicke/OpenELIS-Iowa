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
