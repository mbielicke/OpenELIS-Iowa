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
package org.openelis.modules.secondDataEntry.client;

import static org.openelis.modules.main.client.Logger.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;

import org.openelis.cache.CategoryCache;
import org.openelis.constants.Messages;
import org.openelis.domain.DictionaryDO;
import org.openelis.domain.OrganizationDO;
import org.openelis.domain.ProjectDO;
import org.openelis.meta.CategoryMeta;
import org.openelis.modules.main.client.resources.OpenELISResources;
import org.openelis.modules.organization1.client.OrganizationService1Impl;
import org.openelis.modules.project.client.ProjectService;
import org.openelis.ui.event.GetMatchesEvent;
import org.openelis.ui.event.GetMatchesHandler;
import org.openelis.ui.screen.Screen;
import org.openelis.ui.widget.AutoComplete;
import org.openelis.ui.widget.Button;
import org.openelis.ui.widget.DoubleHelper;
import org.openelis.ui.widget.Dropdown;
import org.openelis.ui.widget.IntegerHelper;
import org.openelis.ui.widget.Item;
import org.openelis.ui.widget.Label;
import org.openelis.ui.widget.QueryFieldUtil;
import org.openelis.ui.widget.TextBase;
import org.openelis.ui.widget.TextBox;
import org.openelis.ui.widget.calendar.Calendar;
import org.openelis.ui.widget.table.CellRenderer;
import org.openelis.ui.widget.table.Column;
import org.openelis.ui.widget.table.LabelCell;
import org.openelis.ui.widget.table.Table;

import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Focusable;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;

public class SecondDataEntryUtil {

    public enum Type {
        INTEGER, DOUBLE, STRING
    }

    /**
     * Creates a Label; sets the passed value as its text and sets its style as
     * "Prompt"
     */
    public static Label<String> getPromptLabel(String text) {
        return getLabel(text + ":", OpenELISResources.INSTANCE.style().Prompt());
    }

    /**
     * Creates a Label; sets the passed value as its text and sets its style as
     * "Heading"
     */
    public static Label<String> getHeadingLabel(String text) {
        return getLabel(text, OpenELISResources.INSTANCE.style().Heading());
    }

    /**
     * Creates a Label; sets the passed values as its text and style
     * respectively
     */
    private static Label<String> getLabel(String text, String styleName) {
        Label<String> l;

        l = new Label<String>(text);
        l.setStyleName(styleName);

        return l;
    }

    /**
     * Creates a Calendar; sets "width" as its width and the integers as its
     * begin and end precisions
     */
    public static Calendar getCalendar(int width, int begin, int end) {
        Calendar c;

        c = new Calendar();
        c.setWidth(width + "px");
        c.setBegin(begin);
        c.setEnd(end);

        return c;
    }

    /**
     * Creates a TextBox; sets "type" as its type e.g. Integer or String,
     * "width" as its width, "textCase" as its case e.g. UPPER or LOWER,
     * "maxLength" as its maximum allowed length and "mask" as its mask
     */
    public static TextBox getTextBox(Type type, int width, TextBase.Case textCase,
                                     Integer maxLength, String mask) {
        TextBox t;

        t = null;
        switch (type) {
            case INTEGER:
                t = new TextBox<Integer>();
                t.setHelper(new IntegerHelper());
                break;
            case DOUBLE:
                t = new TextBox<Double>();
                t.setHelper(new DoubleHelper());
                break;
            case STRING:
                t = new TextBox<String>();
                break;
        }

        t.setWidth(width + "px");
        if (textCase != null)
            t.setCase(textCase);
        if (maxLength != null)
            t.setMaxLength(maxLength);
        if (mask != null)
            t.setMask(mask);

        return t;
    }

    /**
     * Creates the button used to copy the value from the widget on the right to
     * the one on the left
     */
    public static Button getCopyButton() {
        Button b;

        b = new Button();
        b.setText(Messages.get().moveLeft());
        b.setCss(OpenELISResources.INSTANCE.FormFieldButton());

        return b;
    }

    /**
     * Creates a Dropdown; sets "type" as its type e.g. Integer or String,
     * "width" as its width, "textCase" as its case e.g. UPPER or LOWER, and
     * "model" as its model
     */
    public static Dropdown getDropdown(Type type, int width, TextBase.Case textCase,
                                       ArrayList<Item> model) {
        Dropdown d;

        d = null;
        switch (type) {
            case INTEGER:
                d = new Dropdown<Integer>();
                break;
            case STRING:
                d = new Dropdown<String>();
                break;
        }

        d.setWidth(width + "px");
        if (textCase != null)
            d.setCase(textCase);

        d.setModel(model);

        return d;
    }

    /**
     * Creates an autocomplete for showing organizations; sets "width" as its
     * width, "textCase" as its case e.g. UPPER or LOWER, and "dropWidth" as the
     * total width of its table; the table's columns show various fields of an
     * organization
     */
    public static AutoComplete getOrganizationAutocomplete(int width, TextBase.Case textCase,
                                                           int dropWidth, final Screen parentScreen) {
        final AutoComplete a;
        Table t;

        /*
         * set the basic fields e.g. dimensions
         */
        a = getAutocomplete(width, textCase, dropWidth);

        /*
         * create and set the table
         */
        t = new Table();
        t.setVisibleRows(10);
        t.setHeader(true);

        t.addColumn(getColumn(250, Messages.get().gen_name(), new LabelCell()));
        t.addColumn(getColumn(70, Messages.get().address_aptSuite(), new LabelCell()));
        t.addColumn(getColumn(110, Messages.get().address_street(), new LabelCell()));
        t.addColumn(getColumn(100, Messages.get().address_city(), new LabelCell()));
        t.addColumn(getColumn(20, Messages.get().address_st(), new LabelCell()));

        a.setPopupContext(t);

        /*
         * add the matches handler
         */
        addOrganizationMatchesHandler(a, parentScreen);

        return a;
    }

    /**
     * Adds the matches handler to the passed autocomplete which will show
     * organizations
     */
    public static void addOrganizationMatchesHandler(final AutoComplete a, final Screen parentScreen) {
        a.addGetMatchesHandler(new GetMatchesHandler() {
            public void onGetMatches(GetMatchesEvent event) {
                Item<Integer> row;
                ArrayList<OrganizationDO> list;
                ArrayList<Item<Integer>> model;

                parentScreen.getWindow().setBusy();
                try {
                    list = OrganizationService1Impl.INSTANCE.fetchByIdOrName(QueryFieldUtil.parseAutocomplete(event.getMatch()));
                    model = new ArrayList<Item<Integer>>();
                    for (OrganizationDO data : list) {
                        row = new Item<Integer>(5);

                        row.setKey(data.getId());
                        row.setCell(0, data.getName());
                        row.setCell(1, data.getAddress().getMultipleUnit());
                        row.setCell(2, data.getAddress().getStreetAddress());
                        row.setCell(3, data.getAddress().getCity());
                        row.setCell(4, data.getAddress().getState());
                        row.setData(data);

                        model.add(row);
                    }
                    a.showAutoMatches(model);
                } catch (Throwable e) {
                    Window.alert(e.getMessage());
                    logger.log(Level.SEVERE, e.getMessage(), e);
                }
                parentScreen.getWindow().clearStatus();
            }
        });
    }

    /**
     * Creates an autocomplete for showing organizations; sets "width" as its
     * width, "textCase" as its case e.g. UPPER or LOWER, and "dropWidth" as the
     * total width of its table; the table's columns show various fields of a
     * project
     */
    public static AutoComplete getProjectAutocomplete(int width, TextBase.Case textCase,
                                                      int dropWidth, final Screen parentScreen) {
        final AutoComplete a;
        Table t;

        /*
         * set the basic fields e.g. dimensions
         */
        a = getAutocomplete(width, textCase, dropWidth);

        /*
         * create and set the table
         */
        t = new Table();
        t.setVisibleRows(10);
        t.setHeader(true);

        t.addColumn(getColumn(150, Messages.get().gen_name(), new LabelCell()));
        t.addColumn(getColumn(275, Messages.get().gen_description(), new LabelCell()));

        a.setPopupContext(t);

        /*
         * add the matches handler
         */
        addProjectMatchesHandler(a, parentScreen);

        return a;
    }

    /**
     * Adds the matches handler to the passed autocomplete which will show
     * projects
     */
    public static void addProjectMatchesHandler(final AutoComplete a, final Screen parentScreen) {
        /*
         * add the matches handler
         */
        a.addGetMatchesHandler(new GetMatchesHandler() {
            public void onGetMatches(GetMatchesEvent event) {
                Item<Integer> row;
                ArrayList<ProjectDO> list;
                ArrayList<Item<Integer>> model;

                parentScreen.getWindow().setBusy();
                try {
                    list = ProjectService.get()
                                         .fetchActiveByName(QueryFieldUtil.parseAutocomplete(event.getMatch()));
                    model = new ArrayList<Item<Integer>>();
                    for (ProjectDO data : list) {
                        row = new Item<Integer>(2);

                        row.setKey(data.getId());
                        row.setCell(0, data.getName());
                        row.setCell(1, data.getDescription());
                        row.setData(data);
                        model.add(row);
                    }
                    a.showAutoMatches(model);
                } catch (Throwable e) {
                    Window.alert(e.getMessage());
                    logger.log(Level.SEVERE, e.getMessage(), e);
                }
                parentScreen.getWindow().clearStatus();
            }
        });
    }

    /**
     * Creates a key for the image showing match/no match in the row for the
     * widget with the passed key
     */
    public static String getMatchImageKey(String editWidgetKey) {
        return editWidgetKey + "_im";
    }

    /**
     * Creates a key for the image showing the direction in which the copy was
     * performed in the row for the widget with the passed key
     */
    public static String getCopyImageKey(String editWidgetKey) {
        return editWidgetKey + "_ic";
    }

    /**
     * Creates a key for the widget on the right in the row for the widget with
     * the passed key
     */
    public static String getRightWidgetKey(String editWidgetKey) {
        return editWidgetKey + "_r";
    }

    /**
     * Creates the model for a dropdown filled from dictionary entries;
     * "category" is the category to which the entries belong and "key" is name
     * of the field that should be set as the key of each item in the model
     */
    public static ArrayList<Item> getDictionaryModel(String category, String key) {
        Item row;
        ArrayList<Item> model;

        model = new ArrayList<Item>();
        for (DictionaryDO d : CategoryCache.getBySystemName(category)) {
            row = new Item(1);
            if (CategoryMeta.getDictionaryId().equals(key))
                row.setKey(d.getId());
            else if (CategoryMeta.getDictionaryEntry().equals(key))
                row.setKey(d.getEntry());

            row.setCell(0, d.getEntry());
            row.setEnabled("Y".equals(d.getIsActive()));
            model.add(row);
        }

        return model;
    }

    /**
     * Adds the passed widget to the passed panel; also adds an Image widget
     * next to the widget for showing the icon indicating whether the value
     * entered in the widget matches the value in the manager
     */
    public static void addWidgetAndImage(HorizontalPanel panel, Widget widget, Image image) {
        HorizontalPanel h1;

        panel.add(widget);

        /*
         * some padding between the widget and the image
         */
        h1 = new HorizontalPanel();
        h1.setWidth("5px");
        panel.add(h1);

        /*
         * the blank icon is used as a placeholder; without this icon, there's
         * no space between the widget and the button in the row, until another
         * icon is shown
         */
        image.setResource(OpenELISResources.INSTANCE.blankIcon());
        panel.add(image);
    }

    /**
     * Creates a row at the passed index in the table panel and adds the passed
     * widgets to that row
     */
    public static void addWidgets(int row, FlexTable widgetTable, Widget... widgets) {
        for (int i = 0; i < widgets.length; i++ ) {
            widgetTable.setWidget(row, i, widgets[i]);
            if (row % 2 != 0)
                widgetTable.getRowFormatter()
                           .setStyleName(row,
                                         OpenELISResources.INSTANCE.style().WidgetTableAlternateRow());
        }
    }

    /**
     * Increments by one, the number of times the widget with the passed key has
     * been edited; returns the incremented value
     */
    public static Integer updateNumEdit(String key, HashMap<String, Integer> numEdits) {
        Integer numEdit;

        numEdit = numEdits.get(key);
        if (numEdit == null)
            numEdit = 1;
        else
            numEdit++ ;
        numEdits.put(key, numEdit);

        return numEdit;
    }

    /**
     * Returns the widget that would get the focus when the widget at the passed
     * index in the list of editable widgets, loses focus; the returned widget
     * would depend on the "direction" specified by the boolean flag; if the
     * flag is true, the widget after the one at the passed index would get the
     * focus, otherwise the widget before the one at the passed index would get
     * the focus
     */
    public static Widget onTab(int index, boolean forward, ArrayList<Widget> editWidgets) {
        if (forward) {
            if (index < editWidgets.size() - 1)
                index++ ;
            else
                index = 0;
        } else {
            if (index > 0)
                index-- ;
            else
                index = editWidgets.size() - 1;
        }

        return editWidgets.get(index);
    }

    /**
     * Returns the command that makes the focus get set to the passed widget as
     * soon as it loses focus
     */
    public static ScheduledCommand getFocusCommand(final Focusable widget) {
        ScheduledCommand cmd;

        cmd = new ScheduledCommand() {
            @Override
            public void execute() {
                widget.setFocus(true);
            }
        };

        return cmd;
    }

    /**
     * Creates a table column; sets "width" as its width, "label" as its header
     * , and "renderer" as the class that handles showing the data in the column
     */
    private static Column getColumn(int width, String label, CellRenderer renderer) {
        Column c;

        c = new Column();
        c.setWidth(250);
        c.setLabel(label);
        c.setCellRenderer(renderer);

        return c;
    }

    /**
     * Creates an autocomplete; sets "width" as its width, "textCase" as its
     * case e.g. UPPER or LOWER, and "dropWidth" as the total width of its table
     */
    private static AutoComplete getAutocomplete(int width, TextBase.Case textCase, int dropWidth) {
        AutoComplete a;

        a = new AutoComplete();
        a.setWidth(width + "px");
        a.setCase(textCase);
        a.setDropWidth(dropWidth + "px");

        return a;
    }
}