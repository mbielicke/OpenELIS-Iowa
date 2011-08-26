/**
 * Exhibit A - UIRF Open-source Based Public Software License.
 * 
 * The contents of this file are subject to the UIRF Open-source Based Public
 * Software License(the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * openelis.uhl.uiowa.edu
 * 
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License for
 * the specific language governing rights and limitations under the License.
 * 
 * The Original Code is OpenELIS code.
 * 
 * The Initial Developer of the Original Code is The University of Iowa.
 * Portions created by The University of Iowa are Copyright 2006-2008. All
 * Rights Reserved.
 * 
 * Contributor(s): ______________________________________.
 * 
 * Alternatively, the contents of this file marked "Separately-Licensed" may be
 * used under the terms of a UIRF Software license ("UIRF Software License"), in
 * which case the provisions of a UIRF Software License are applicable instead
 * of those above.
 */
package org.openelis.modules.favorites.client;

import java.util.ArrayList;
import java.util.Collections;

import org.openelis.gwt.screen.Screen;
import org.openelis.gwt.screen.ScreenDefInt;
import org.openelis.gwt.widget.AppButton;
import org.openelis.gwt.widget.CheckBox;
import org.openelis.gwt.widget.MenuItem;
import org.openelis.manager.Preferences;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * The class provides a quick menu side panel on the main screen. The list of
 * menu items are saved and restored through user preferences. The list can be
 * edited by switching the panel into an "edit" mode.
 * 
 */
public class FavoritesScreen extends Screen {

    protected ArrayList<Item> list;
    protected ScrollPanel     panel;
    protected boolean         editing;

    /*
     * Create the side panel and display the preferred menu items
     */
    public FavoritesScreen(ScreenDefInt def) throws Throwable {
        AppButton editButton;

        this.def = def;

        // for scroll resize
        Window.addResizeHandler(new ResizeHandler() {
            public void onResize(ResizeEvent event) {
                resize();
            }
        });

        // for edit button above favorite
        editButton = (AppButton)def.getWidget("EditFavorites");
        if (editButton != null) {
            editButton.enable(true);
            editButton.addClickHandler(new ClickHandler() {
                public void onClick(ClickEvent event) {
                    if (editing)
                        stopEditing();
                    else
                        startEditing();
                    editing = !editing;
                }
            });
        }

        panel = new ScrollPanel();
        panel.setWidget(new VerticalPanel());

        list = new ArrayList<Item>();
        getFavorites();
        showFavorites();

        // resize for the first time
        DeferredCommand.addCommand(new Command() {
            public void execute() {
                resize();
            }
        });
    }

    /*
     * Parses the "favorites" preference to get previously selected menu keys
     */
    protected void getFavorites() throws Throwable {
        String favs;
        MenuItem mi;
        Preferences pref;

        pref = Preferences.userRoot();
        favs = pref.get("favorites", "");
        for (String key : favs.split(",")) {
            mi = (MenuItem)def.getWidget(key);
            if (mi != null)
                list.add(new Item(key, mi));
        }
    }

    /*
     * Displays the menu items
     */
    protected void showFavorites() {
        VerticalPanel vp;
        MenuItem mi;
        AbsolutePanel ap;

        vp = (VerticalPanel)panel.getWidget();
        vp.clear();

        for (Item item : list) {
            if (item.menuItem.isEnabled()) {
                final MenuItem menuItem = item.menuItem;

                ap = new AbsolutePanel();
                mi = new MenuItem(menuItem.icon, menuItem.label, null);
                mi.enable(true);
                mi.addClickHandler(new ClickHandler() {
                    public void onClick(ClickEvent event) {
                        ClickEvent.fireNativeEvent(event.getNativeEvent(), menuItem);
                    }
                });
                ap.add(mi);
                vp.add(ap);
            }
        }
        screenpanel.clear();
        screenpanel.add(panel);
    }

    /*
     * Switches the view into edit mode and offers the user a list of all
     * available menu items. Adds a checkbox to every item for selection.
     */
    protected void startEditing() {
        int i;
        Widget w;
        CheckBox cb;
        VerticalPanel vp;
        FlexTable layout;
        ArrayList<Item> tmpList;

        vp = (VerticalPanel)panel.getWidget();
        vp.clear();

        // get all the menuitem widgets, sort them
        tmpList = new ArrayList<Item>();
        for (String key : def.getWidgets().keySet()) {
            w = def.getWidget(key);
            if (w instanceof MenuItem && ((MenuItem)w).isEnabled())
                tmpList.add(new Item(key, (MenuItem)w));
        }
        Collections.sort(tmpList);

        // build a new menu list with check box for selection
        i = 0;
        layout = new FlexTable();
        for (Item item : tmpList) {
            final Item checkItem = item;

            cb = new CheckBox();
            if (list.contains(item)) {
                item.isChecked = true;
                cb.setState(CheckBox.CHECKED);
            }
            cb.addValueChangeHandler(new ValueChangeHandler<String>() {
                public void onValueChange(ValueChangeEvent<String> event) {
                    checkItem.isChecked = event.getValue() == CheckBox.CHECKED;
                }
            });

            layout.insertRow(i);
            layout.setWidget(i, 0, cb);
            layout.setWidget(i, 1, new MenuItem(null, item.menuItem.labelText, null));
            i++ ;
        }
        vp.add(layout);
        list = tmpList;
        
        resize();
    }

    /*
     * Saves the preferred list and switches the view to the normal.
     */
    protected void stopEditing() {
        StringBuffer sb;
        ArrayList<Item> tmpList;

        sb = new StringBuffer();
        tmpList = new ArrayList<Item>();

        // build the preference list from selected items
        for (Item item : list) {
            if (item.isChecked) {
                if (sb.length() > 0)
                    sb.append(",");
                sb.append(item.key);
                tmpList.add(item);
            }
        }

        // save the preferences and redraw
        try {
            Preferences.userRoot().put("favorites", sb.toString());
            Preferences.userRoot().flush();

            list = tmpList;
            showFavorites();
        } catch (Exception e) {
            Window.alert("Could not save your settings; " + e.getMessage());
        }
    }

    protected void resize() {
        panel.setHeight( (Window.getClientHeight() - screenpanel.getAbsoluteTop()) + "px");
    }

    /*
     * favorite items
     */
    class Item implements Comparable<Item> {
        boolean  isChecked;
        String   key;
        MenuItem menuItem;

        public Item(String key, MenuItem menuItem) {
            this.key = key;
            this.menuItem = menuItem;
        }

        public int compareTo(Item o) {
            return menuItem.label.compareTo(o.menuItem.label);
        }

        public boolean equals(Object o) {
            if (o != null && o instanceof Item && key != null)
                return key.equals( ((Item)o).key);
            return false;
        }

        public int hasCode() {
            return key.hashCode();
        }
    }
}
