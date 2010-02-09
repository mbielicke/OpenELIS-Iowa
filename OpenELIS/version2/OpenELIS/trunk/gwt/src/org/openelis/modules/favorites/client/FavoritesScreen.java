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

import java.util.ArrayList;

import org.openelis.gwt.common.RPC;
import org.openelis.gwt.screen.Screen;
import org.openelis.gwt.screen.ScreenDefInt;
import org.openelis.gwt.services.ScreenService;
import org.openelis.gwt.widget.CheckBox;
import org.openelis.gwt.widget.MenuItem;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.HasAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class FavoritesScreen extends Screen {
        
    public boolean editing;
    public ArrayList<String> favorites; 
    private ArrayList<String> menuKeys;
    private ArrayList<CheckBox> checks;
    
	public FavoritesScreen(ScreenDefInt def) throws Throwable {
		this.def = def;
		service = new ScreenService("OpenELISServlet?service=org.openelis.modules.favorites.server.FavoritesService");
		getFavorites();
		showFavorites();
	}
	
	private void showFavorites() {
		ScrollPanel sp = new ScrollPanel();
		sp.setHeight((Window.getClientHeight()-65)+"px");
		VerticalPanel vp = new VerticalPanel();
		sp.setWidget(vp);
		for(String fav : favorites) {
			MenuItem favMenu = (MenuItem)def.getWidget(fav);
			if(favMenu != null && favMenu.isEnabled()){
				final String menuKey = fav;
				AbsolutePanel ap = new AbsolutePanel();
				MenuItem clone = favMenu.clone();
				clone.enable(true);
				clone.addClickHandler(new ClickHandler(){
					public void onClick(ClickEvent event) {
						ClickEvent.fireNativeEvent(event.getNativeEvent(), def.getWidget(menuKey));
					}
				});
				ap.add(clone);
				vp.add(ap);
			}
		}
		screenpanel.clear();
		screenpanel.add(sp);
	}
	
	public void edit() {
		editing = true;
		ScrollPanel sp = new ScrollPanel();
		sp.setHeight((Window.getClientHeight()-65)+"px");
		VerticalPanel vp = new VerticalPanel();
		sp.setWidget(vp);
		menuKeys = new ArrayList<String>();
		checks = new ArrayList<CheckBox>();
		for(String key : def.getWidgets().keySet()) {
			Widget wid = def.getWidget(key);
			if(wid instanceof MenuItem && ((MenuItem)wid).isEnabled()){
				menuKeys.add(key);
				HorizontalPanel hp = new HorizontalPanel();
				hp.setSpacing(0);
				CheckBox cb = new CheckBox();
				checks.add(cb);
				if(favorites.contains(key))
					cb.setState(CheckBox.CHECKED);
				hp.add(cb);
				hp.setCellVerticalAlignment(cb, HasAlignment.ALIGN_MIDDLE);
				AbsolutePanel ap = new AbsolutePanel();
				ap.add(((MenuItem)wid).clone());
				hp.add(ap);
				vp.add(hp);
			}
		}
		screenpanel.clear();
		screenpanel.add(sp);
	}
	
	public void stopEditing() {
		editing = false;
		StringBuffer sb = new StringBuffer("");
		for(int i = 0; i < checks.size(); i++) {
			if(checks.get(i).getState() == CheckBox.CHECKED){
				if(sb.length() > 0)
					sb.append(",");
				sb.append(menuKeys.get(i));
			}	
		}
		String favs = sb.toString();
		favorites = new ArrayList<String>();
		for(String fav : favs.split(",")){
			favorites.add(fav);
		}
		showFavorites();
		saveFavorites(favs);	
	}
	
	private void getFavorites() throws Throwable {
		String favs = service.callString("getFavorites");
		favorites = new ArrayList<String>();
		if(favs == null)
			return;
		for(String fav : favs.split(",")){
			favorites.add(fav);
		}
	}
	
	private void saveFavorites(String favs) {
		System.out.println("saving favs "+favs);
		service.call("saveFavorites",favs,new AsyncCallback<RPC>() {
			public void onSuccess(RPC result) {
				
			}
			public void onFailure(Throwable caught) {
				Window.alert(caught.getMessage());
			}
		});
	}
	
}
