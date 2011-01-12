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

import org.openelis.manager.Preferences;
import org.openelis.gwt.screen.Screen;
import org.openelis.gwt.screen.ScreenDefInt;
import org.openelis.gwt.services.ScreenService;
import org.openelis.gwt.widget.CheckMenuItem;
import org.openelis.gwt.widget.MenuItem;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class FavoritesScreen extends Screen {
        
    public boolean editing;
    public ArrayList<String> favorites; 
    private ArrayList<String> menuKeys;
    private ArrayList<CheckMenuItem> checks;
    private Preferences prefs;
    
	public FavoritesScreen(ScreenDefInt def) throws Throwable {
		this.def = def;
		service = new ScreenService("controller?service=org.openelis.modules.favorites.server.FavoritesService");
		prefs = Preferences.userRoot();
		showFavorites();
	}
	
	private void showFavorites() {
		String favs;
		ScrollPanel sp;
		VerticalPanel vp;
		MenuItem favMenu,mi;
		favorites = new ArrayList<String>();
		favs = prefs.get("favorites","");
		if(favs.equals(""))
			return;
		
		sp = new ScrollPanel();
		sp.setHeight((Window.getClientHeight()-65)+"px");
		vp = new VerticalPanel();
		sp.setWidget(vp);
		for(String fav : favs.split(",")) {
			mi = (MenuItem)def.getWidget(fav);
			if(mi != null && mi.isEnabled()){			
				favMenu = new MenuItem(mi.getIcon(),mi.getDisplay(),"");
				favMenu.setEnabled(true);
				for(Command comm : mi.getCommands()) 
					favMenu.addCommand(comm);
				vp.add(favMenu);
				favorites.add(fav);
			}
		}
		clear();
		add(sp);
	}
	
	public void edit() {
		ScrollPanel sp;
		VerticalPanel vp;
		CheckMenuItem cm; 
		MenuItem mi;
		Widget wid;
		
		editing = true;
		sp = new ScrollPanel();
		sp.setHeight((Window.getClientHeight()-65)+"px");
		vp = new VerticalPanel();
		sp.setWidget(vp);
		menuKeys = new ArrayList<String>();
		checks = new ArrayList<CheckMenuItem>();
		
		for(String key : def.getWidgets().keySet()) {
			wid = def.getWidget(key);
			if(wid instanceof MenuItem && ((MenuItem)wid).isEnabled()){
				menuKeys.add(key);
				mi = (MenuItem)wid;
			    
				cm = new CheckMenuItem(mi.getIcon(),mi.getDisplay(),false);
				cm.setCheck(favorites.contains(key));
				checks.add(cm);
				
				vp.add(cm);
			}
		}
		clear();
		add(sp);
	}
	
	public void stopEditing() {
		editing = false;
		StringBuffer sb = new StringBuffer("");
		for(int i = 0; i < checks.size(); i++) {
			if(checks.get(i).isChecked()){
				if(sb.length() > 0)
					sb.append(",");
				sb.append(menuKeys.get(i));
			}	
		}
		String favs = sb.toString();
		prefs.put("favorites", favs);
		showFavorites();
	}
	
}
