package org.openelis.web.modules.main.client;

import java.util.HashMap;

import org.openelis.gwt.screen.Screen;
import org.openelis.gwt.screen.ScreenDefInt;
import org.openelis.web.modules.menu.client.MenuScreen;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.VerticalPanel;

public class OpenELISWebScreen extends Screen implements ValueChangeHandler<String>{
	
	protected static AbsolutePanel content;
	protected static HashMap<String,Screen> screens;
	protected static CrumbLine crumb;
	
	public OpenELISWebScreen() {
		super((ScreenDefInt)GWT.create(OpenELISWebDef.class));
		initialize();
	}
	
	private void initialize() {
		content = (AbsolutePanel)def.getWidget("content");
		screens = new HashMap<String,Screen>();
		crumb = new CrumbLine();
		((VerticalPanel)def.getWidget("container")).insert(crumb, 0);
		History.addValueChangeHandler(this);
		setScreen(new MenuScreen(),"Menu","menu");
	}
	
	public static void setScreen(Screen screen, String name, String key) {
		content.clear();
		content.add(screen);
		History.newItem(key,false);
		screens.put(key, screen);
		crumb.addLink(name, key);
	}
	
	public static void gotoScreen(String key) {
		content.clear();
		content.add(screens.get(key));
		History.newItem(key,false);
	}

	public void onValueChange(ValueChangeEvent<String> event) {
		content.clear();
		content.add(screens.get(event.getValue()));
		crumb.removeLink();
	}

}
