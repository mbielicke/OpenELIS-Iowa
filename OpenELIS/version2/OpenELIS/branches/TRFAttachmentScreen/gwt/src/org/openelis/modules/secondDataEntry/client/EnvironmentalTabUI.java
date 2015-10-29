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
import static org.openelis.ui.screen.Screen.ShortKeys.*;
import static org.openelis.ui.screen.State.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;

import org.openelis.constants.Messages;
import org.openelis.domain.Constants;
import org.openelis.domain.SampleOrganizationViewDO;
import org.openelis.domain.SampleProjectViewDO;
import org.openelis.manager.SampleManager1;
import org.openelis.meta.CategoryMeta;
import org.openelis.meta.SampleMeta;
import org.openelis.modules.main.client.resources.OpenELISResources;
import org.openelis.ui.common.DataBaseUtil;
import org.openelis.ui.common.Datetime;
import org.openelis.ui.event.DataChangeEvent;
import org.openelis.ui.event.ShortcutHandler;
import org.openelis.ui.event.StateChangeEvent;
import org.openelis.ui.screen.Screen;
import org.openelis.ui.screen.ScreenHandler;
import org.openelis.ui.screen.State;
import org.openelis.ui.widget.AutoComplete;
import org.openelis.ui.widget.AutoCompleteValue;
import org.openelis.ui.widget.Button;
import org.openelis.ui.widget.CheckBox;
import org.openelis.ui.widget.Dropdown;
import org.openelis.ui.widget.Item;
import org.openelis.ui.widget.Label;
import org.openelis.ui.widget.TextBase;
import org.openelis.ui.widget.TextBox;
import org.openelis.ui.widget.calendar.Calendar;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Focusable;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.xml.client.Document;
import com.google.gwt.xml.client.Node;
import com.google.gwt.xml.client.XMLParser;

public class EnvironmentalTabUI extends Screen {
    @UiTemplate("EnvironmentalTab.ui.xml")
    interface EnvironmentalTabUiBinder extends UiBinder<Widget, EnvironmentalTabUI> {
    };

    private static EnvironmentalTabUiBinder    uiBinder = GWT.create(EnvironmentalTabUiBinder.class);

    @UiField
    protected FlexTable                        widgetTable;

    protected SampleManager1                   manager;

    protected Screen                           parentScreen;

    protected EventBus                         parentBus;

    protected EnvironmentalTabUI               screen;

    protected ArrayList<Widget>                editWidgets;

    protected HashMap<Widget, Integer>         numEdits;

    protected boolean                          canEdit;

    protected int                              tabIndex;

    protected static final String              REPORT_TO = "report_to", BILL_TO = "bill_to";

    public EnvironmentalTabUI(Screen parentScreen) {
        this.parentScreen = parentScreen;
        this.parentBus = parentScreen.getEventBus();
        initWidget(uiBinder.createAndBindUi(this));

        screen = this;
        initialize();
    }

    /**
     * Setup state and data change handles for every widget on the screen
     */
    private void initialize() {
        addStateChangeHandler(new StateChangeEvent.Handler() {
            public void onStateChange(StateChangeEvent event) {
                ScheduledCommand cmd;

                if ( !isState(UPDATE) || !canEdit)
                    return;
                /*
                 * this keeps track of how many times a widget with a given key
                 * was edited
                 */
                numEdits = new HashMap<Widget, Integer>();

                /*
                 * this is the index of the most recent widget that lost focus
                 * when Tab was pressed
                 */
                tabIndex = -1;

                /*
                 * set the focus to the first editable widget; if the focus is
                 * not set in this command, the style for focus doesn't show up
                 * even if the widget has the cursor and is enabled
                 */
                cmd = new ScheduledCommand() {
                    @Override
                    public void execute() {
                        if (editWidgets != null && editWidgets.size() > 0)
                            ((Focusable)editWidgets.get(0)).setFocus(true);
                    }
                };
                Scheduler.get().scheduleDeferred(cmd);
            }
        });
        
        /*
         * add shortcuts to select the tabs on the screen by using the Ctrl key
         * and a number, e.g. Ctrl+'1' for the first tab, and so on; the
         * ScheduledCommands make sure that the tab is opened before the focus
         * is set
         */
        addShortcut(new ShortcutHandler() {
            @Override
            public void onShortcut() {
                Command cmd;

                cmd = new Command() {
                    @Override
                    public void execute() {                        
                        logger.log(Level.SEVERE, "env tab Ctrl+1");
                        if (focused != null)
                            logger.log(Level.SEVERE, "focused is not null ");
                    }
                };
                Scheduler.get().scheduleDeferred(cmd);                
            }
        }, '1', CTRL);

        addShortcut(new ShortcutHandler() {
            @Override
            public void onShortcut() {
                Command cmd;

                cmd = new Command() {
                    @Override
                    public void execute() {
                        logger.log(Level.SEVERE, "env tab Ctrl+2");
                        if (focused != null)
                            logger.log(Level.SEVERE, "focused is not null ");
                    }
                };
                Scheduler.get().scheduleDeferred(cmd);
            }
        }, '2', CTRL);
    }

    public void setData(SampleManager1 manager) {
        this.manager = manager;
    }

    public void setState(State state) {
        evaluateEdit();
        this.state = state;
        bus.fireEventFromSource(new StateChangeEvent(state), this);
    }

    public void onDataChange() {
        fireDataChange();
    }

    /**
     * Adds widgets used for verification for the passed domain to the screen
     */
    public void addWidgets(String xml) {
        int i, row;
        Document doc;
        Node root, node;

        row = 0;
        editWidgets = new ArrayList<Widget>();
        /*
         * parse the xml and add widgets for the fields to the widget table
         */
        doc = XMLParser.parse(xml);
        root = doc.getDocumentElement();
        for (i = 0; i < root.getChildNodes().getLength(); i++ ) {
            node = root.getChildNodes().item(i);
            /*
             * this discards unwanted "child" nodes like whitespaces
             */
            if (Node.ELEMENT_NODE != node.getNodeType())
                continue;
            addWidgetRow(node.getNodeName(), ++row);
        }
    }

    private void evaluateEdit() {
        canEdit = manager != null &&
                  Constants.domain().ENVIRONMENTAL.equals(manager.getSample().getDomain());
    }

    /**
     * Adds a row for the widget with the passed key, at the passed index
     */
    private void addWidgetRow(String key, int row) {
        switch (key) {
            case SampleMeta.ORDER_ID:
                addOrderId(row);
                break;
            case SampleMeta.COLLECTION_DATE:
                addCollectionDate(row);
                break;
            case SampleMeta.COLLECTION_TIME:
                addCollectionTime(row);
                break;
            case SampleMeta.RECEIVED_DATE:
                addReceievedDate(row);
                break;
            case SampleMeta.CLIENT_REFERENCE:
                addClientReference(row);
                break;
            case SampleMeta.ENV_IS_HAZARDOUS:
                addEnvIsHazardous(row);
                break;
            case SampleMeta.ENV_PRIORITY:
                addEnvPriority(row);
                break;
            case SampleMeta.ENV_COLLECTOR:
                addEnvCollector(row);
                break;
            case SampleMeta.ENV_COLLECTOR_PHONE:
                addEnvCollectorPhone(row);
                break;
            case SampleMeta.ENV_DESCRIPTION:
                addEnvDescription(row);
                break;
            case SampleMeta.ENV_LOCATION:
                addEnvLocation(row);
                break;
            case SampleMeta.LOCATION_ADDR_MULTIPLE_UNIT:
                addLocationAddrMultipleUnit(row);
                break;
            case SampleMeta.LOCATION_ADDR_STREET_ADDRESS:
                addLocationAddrStreetAddress(row);
                break;
            case SampleMeta.LOCATION_ADDR_CITY:
                addLocationAddrCity(row);
                break;
            case SampleMeta.LOCATION_ADDR_STATE:
                addLocationAddrState(row);
                break;
            case SampleMeta.LOCATION_ADDR_ZIP_CODE:
                addLocationAddrZipCode(row);
                break;
            case SampleMeta.LOCATION_ADDR_COUNTRY:
                addLocationAddrCountry(row);
                break;
            case REPORT_TO:
                addReportTo(row);
                break;
            case BILL_TO:
                addBillTo(row);
                break;
            case SampleMeta.PROJECT_NAME:
                addProject(row);
                break;
        }
    }
    
    /**
     * Adds the row for oder id at the passed index; also adds the applicable
     * screen handlers to the widgets in the row
     */
    private void addOrderId(int row) {
        final int index;
        Label<String> l1;
        final TextBox<Integer> t1, t2;
        final Image i;
        HorizontalPanel h;
        final ScheduledCommand cmd;

        /*
         * create the widgets and add them to the table panel
         */
        l1 = SecondDataEntryUtil.getPromptLabel(Messages.get().order_orderNum());

        t1 = SecondDataEntryUtil.getTextBox(SecondDataEntryUtil.Type.INTEGER, 75, null, null, null);
        i = new Image();
        h = new HorizontalPanel();
        SecondDataEntryUtil.addWidgetAndImage(h, t1, i);

        index = editWidgets.size();
        editWidgets.add(t1);

        t2 = SecondDataEntryUtil.getTextBox(SecondDataEntryUtil.Type.INTEGER, 75, null, null, null);

        SecondDataEntryUtil.addWidgets(row, widgetTable, l1, h, t2);

        /*
         * the scheduled command for setting the focus back to this widget when
         * it loses focus
         */
        cmd = SecondDataEntryUtil.getFocusCommand(t1);

        /*
         * add screen handler for the editable widget
         */
        addScreenHandler(t1, SampleMeta.ORDER_ID, new ScreenHandler<Integer>() {
            public void onDataChange(DataChangeEvent<Integer> event) {
                t1.setValue(null);
            }

            public void onValueChange(ValueChangeEvent<Integer> event) {
                Integer numEdit;

                /*
                 * find out how many times the widget has been edited
                 */
                numEdit = SecondDataEntryUtil.updateNumEdit(t1, numEdits);

                /*
                 * if the value entered is different from the value in the
                 * manager, show the icon for "no match" and if this widget has
                 * been edited multiple times, show the value in the manager in
                 * the widget on the right; if the value entered is the same as
                 * the value in the manager, show the icon for "match"
                 */
                if (DataBaseUtil.isDifferent(event.getValue(), manager.getSample()
                                                                      .getOrderId())) {
                    i.setResource(OpenELISResources.INSTANCE.abort());
                    if (numEdit > 1)
                        t2.setValue(manager.getSample()
                                    .getOrderId());
                    /*
                     * set the focus back on the widget if the user pressed Tab
                     */
                    if (isFocusLostOnTab(t1))
                        Scheduler.get().scheduleDeferred(cmd);
                } else {
                    i.setResource(OpenELISResources.INSTANCE.commit());
                }
                tabIndex = -1;
            }

            public void onStateChange(StateChangeEvent event) {
                t1.setEnabled(isState(UPDATE) && canEdit);
            }

            public Widget onTab(boolean forward) {
                tabIndex = index;
                return SecondDataEntryUtil.onTab(index, forward, editWidgets);
            }
        });

        /*
         * add screen handler for the image
         */
        addScreenHandler(i,
                         SecondDataEntryUtil.getMatchImageKey(SampleMeta.ORDER_ID),
                         new ScreenHandler<Object>() {
                             public void onStateChange(StateChangeEvent event) {
                                 i.setResource(OpenELISResources.INSTANCE.blankIcon());
                             }
                         });

        /*
         * add screen handler for the widget on the right
         */
        addScreenHandler(t2,
                         SecondDataEntryUtil.getRightWidgetKey(SampleMeta.ORDER_ID),
                         new ScreenHandler<Integer>() {
                             public void onDataChange(DataChangeEvent<Integer> event) {
                                 t2.setValue(null);
                             }

                             public void onStateChange(StateChangeEvent event) {
                                 t2.setEnabled(false);
                             }
                         });
    }

    /**
     * Adds the row for collection date to the row at the passed index; also
     * adds the applicable screen handlers to the widgets in the row
     */
    private void addCollectionDate(int row) {
        final int index;
        Label<String> l1, l2;
        final Calendar c1, c2;
        final Image i;
        HorizontalPanel hp;
        //final Button b;
        final ScheduledCommand cmd;

        /*
         * create the widgets and add them to the table panel
         */
        l1 = SecondDataEntryUtil.getPromptLabel(Messages.get().sample_collected());

        c1 = SecondDataEntryUtil.getCalendar(90, 0, 2);
        i = new Image();
        hp = new HorizontalPanel();
        SecondDataEntryUtil.addWidgetAndImage(hp, c1, i);

        index = editWidgets.size();
        editWidgets.add(c1);

        //b = SecondDataEntryUtil.getCopyButton();
        //l2 = SecondDataEntryUtil.getPromptLabel(Messages.get().sample_collected());
        c2 = SecondDataEntryUtil.getCalendar(90, 0, 2);

        //SecondDataEntryUtil.addWidgets(row, widgetTable, l1, hp, b, l2, c2);
        SecondDataEntryUtil.addWidgets(row, widgetTable, l1, hp, c2);

        /*
         * the scheduled command for setting the focus back to this widget when
         * it loses focus
         */
        cmd = SecondDataEntryUtil.getFocusCommand(c1);

        /*
         * add screen handler for the editable widget
         */
        addScreenHandler(c1, SampleMeta.COLLECTION_DATE, new ScreenHandler<Datetime>() {
            public void onDataChange(DataChangeEvent<Datetime> event) {
                c1.setValue(null);
            }

            public void onValueChange(ValueChangeEvent<Datetime> event) {
                Integer numEdit;

                /*
                 * find out how many times the widget has been edited
                 */
                numEdit = SecondDataEntryUtil.updateNumEdit(c1, numEdits);

                /*
                 * if the value entered is different from the value in the
                 * manager, show the icon for "no match" and if this widget has
                 * been edited multiple times, show the value in the manager in
                 * the widget on the right; if the value entered is the same as
                 * the value in the manager, show the icon for "match"
                 */
                if (DataBaseUtil.isDifferent(event.getValue(), manager.getSample()
                                                                      .getCollectionDate())) {
                    i.setResource(OpenELISResources.INSTANCE.abort());
                    if (numEdit > 1) {
                        c2.setValue(manager.getSample().getCollectionDate());
                        //b.setEnabled(true);
                    }
                    /*
                     * set the focus back on the widget if the user pressed Tab
                     */
                    if (isFocusLostOnTab(c1))
                        Scheduler.get().scheduleDeferred(cmd);
                } else {
                    i.setResource(OpenELISResources.INSTANCE.commit());
                }
                tabIndex = -1;
            }

            public void onStateChange(StateChangeEvent event) {
                c1.setEnabled(isState(UPDATE) && canEdit);
            }

            public Widget onTab(boolean forward) {
                tabIndex = index;
                return SecondDataEntryUtil.onTab(index, forward, editWidgets);
            }

        });

        /*
         * add screen handler for the image
         */
        addScreenHandler(i,
                         SecondDataEntryUtil.getMatchImageKey(SampleMeta.COLLECTION_DATE),
                         new ScreenHandler<Datetime>() {
                             public void onStateChange(StateChangeEvent event) {
                                 i.setResource(OpenELISResources.INSTANCE.blankIcon());
                             }
                         });

        /*
         * add screen handler for the button
         */
        /*addScreenHandler(b,
                         SecondDataEntryUtil.getButtonKey(SampleMeta.COLLECTION_DATE),
                         new ScreenHandler<Datetime>() {
                             public void onStateChange(StateChangeEvent event) {
                                 b.setEnabled(false);
                             }
                         });

        b.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                c1.setValue(manager.getSample().getCollectionDate());
                i.setResource(OpenELISResources.INSTANCE.commit());
                c1.setFocus(true);
            }
        });*/

        /*
         * add screen handler for the widget on the right
         */
        addScreenHandler(c2,
                         SecondDataEntryUtil.getRightWidgetKey(SampleMeta.COLLECTION_DATE),
                         new ScreenHandler<Datetime>() {
                             public void onDataChange(DataChangeEvent<Datetime> event) {
                                 c2.setValue(null);
                             }

                             public void onStateChange(StateChangeEvent event) {
                                 c2.setEnabled(false);
                             }
                         });
    }

    /**
     * Adds the row for collection time to the row at the passed index; also
     * adds the applicable screen handlers to the widgets in the row
     */
    private void addCollectionTime(int row) {
        final int index;
        Label<String> l1, l2;
        final Calendar c1, c2;
        final Image i;
        HorizontalPanel hp;
        //final Button b;
        final ScheduledCommand cmd;

        /*
         * create the widgets and add them to the table panel
         */
        l1 = SecondDataEntryUtil.getPromptLabel(Messages.get().gen_time());

        c1 = SecondDataEntryUtil.getCalendar(60, 3, 4);
        i = new Image();
        hp = new HorizontalPanel();
        SecondDataEntryUtil.addWidgetAndImage(hp, c1, i);

        index = editWidgets.size();
        editWidgets.add(c1);

        //b = SecondDataEntryUtil.getCopyButton();
        //l2 = SecondDataEntryUtil.getPromptLabel(Messages.get().gen_time());
        c2 = SecondDataEntryUtil.getCalendar(60, 3, 4);

        //SecondDataEntryUtil.addWidgets(row, widgetTable, l1, hp, b, l2, c2);
        SecondDataEntryUtil.addWidgets(row, widgetTable, l1, hp, c2);

        /*
         * the scheduled command for setting the focus back to this widget when
         * it loses focus
         */
        cmd = SecondDataEntryUtil.getFocusCommand(c1);

        /*
         * add screen handler for the editable widget
         */
        addScreenHandler(c1, SampleMeta.COLLECTION_TIME, new ScreenHandler<Datetime>() {
            public void onDataChange(DataChangeEvent<Datetime> event) {
                c1.setValue(null);
            }

            public void onValueChange(ValueChangeEvent<Datetime> event) {
                Integer numEdit;

                /*
                 * find out how many times the widget has been edited
                 */
                numEdit = SecondDataEntryUtil.updateNumEdit(c1, numEdits);

                /*
                 * if the value entered is different from the value in the
                 * manager, show the icon for "no match" and if this widget has
                 * been edited multiple times, show the value in the manager in
                 * the widget on the right; if the value entered is the same as
                 * the value in the manager, show the icon for "match"
                 */
                if (DataBaseUtil.isDifferentDT(event.getValue(), manager.getSample()
                                                                        .getCollectionTime())) {
                    i.setResource(OpenELISResources.INSTANCE.abort());
                    if (numEdit > 1) {
                        c2.setValue(manager.getSample().getCollectionTime());
                        //b.setEnabled(true);
                    }
                    /*
                     * set the focus back on the widget if the user pressed Tab
                     */
                    if (isFocusLostOnTab(c1))
                        Scheduler.get().scheduleDeferred(cmd);
                } else {
                    i.setResource(OpenELISResources.INSTANCE.commit());
                }
                tabIndex = -1;
            }

            public void onStateChange(StateChangeEvent event) {
                c1.setEnabled(isState(UPDATE) && canEdit);
            }

            public Widget onTab(boolean forward) {
                tabIndex = index;
                return SecondDataEntryUtil.onTab(index, forward, editWidgets);
            }
        });

        /*
         * add screen handler for the image
         */
        addScreenHandler(i,
                         SecondDataEntryUtil.getMatchImageKey(SampleMeta.COLLECTION_TIME),
                         new ScreenHandler<Datetime>() {
                             public void onStateChange(StateChangeEvent event) {
                                 i.setResource(OpenELISResources.INSTANCE.blankIcon());
                             }
                         });

        /*
         * add screen handler for the button
         */
        /*addScreenHandler(b,
                         SecondDataEntryUtil.getButtonKey(SampleMeta.COLLECTION_TIME),
                         new ScreenHandler<Datetime>() {
                             public void onStateChange(StateChangeEvent event) {
                                 b.setEnabled(false);
                             }
                         });

        b.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                c1.setValue(manager.getSample().getCollectionTime());
                i.setResource(OpenELISResources.INSTANCE.commit());
                c1.setFocus(true);
            }
        });*/

        /*
         * add screen handler for the widget on the right
         */
        addScreenHandler(c2,
                         SecondDataEntryUtil.getRightWidgetKey(SampleMeta.COLLECTION_TIME),
                         new ScreenHandler<Datetime>() {
                             public void onDataChange(DataChangeEvent<Datetime> event) {
                                 c2.setValue(null);
                             }

                             public void onStateChange(StateChangeEvent event) {
                                 c2.setEnabled(false);
                             }
                         });
    }

    /**
     * Adds the row for received date to the row at the passed index
     */
    private void addReceievedDate(int row) {
        final int index;
        Label<String> l1, l2;
        final Calendar c1, c2;
        final Image i;
        HorizontalPanel h;
        //final Button b;
        final ScheduledCommand cmd;

        /*
         * create the widgets and add them to the table panel
         */
        l1 = SecondDataEntryUtil.getPromptLabel(Messages.get().sample_received());

        c1 = SecondDataEntryUtil.getCalendar(90, 0, 4);
        i = new Image();
        h = new HorizontalPanel();
        SecondDataEntryUtil.addWidgetAndImage(h, c1, i);

        index = editWidgets.size();
        editWidgets.add(c1);

        //b = SecondDataEntryUtil.getCopyButton();
        //l2 = SecondDataEntryUtil.getPromptLabel(Messages.get().sample_received());
        c2 = SecondDataEntryUtil.getCalendar(90, 0, 4);

        //SecondDataEntryUtil.addWidgets(row, widgetTable, l1, h, b, l2, c2);
        SecondDataEntryUtil.addWidgets(row, widgetTable, l1, h, c2);

        /*
         * the scheduled command for setting the focus back to this widget when
         * it loses focus
         */
        cmd = SecondDataEntryUtil.getFocusCommand(c1);

        /*
         * add screen handler for the editable widget
         */
        addScreenHandler(c1, SampleMeta.RECEIVED_DATE, new ScreenHandler<Datetime>() {
            public void onDataChange(DataChangeEvent<Datetime> event) {
                c1.setValue(null);
            }

            public void onValueChange(ValueChangeEvent<Datetime> event) {
                Integer numEdit;

                /*
                 * find out how many times the widget has been edited
                 */
                numEdit = SecondDataEntryUtil.updateNumEdit(c1, numEdits);

                /*
                 * if the value entered is different from the value in the
                 * manager, show the icon for "no match" and if this widget has
                 * been edited multiple times, show the value in the manager in
                 * the widget on the right; if the value entered is the same as
                 * the value in the manager, show the icon for "match"
                 */
                if (DataBaseUtil.isDifferent(event.getValue(), manager.getSample()
                                                                      .getReceivedDate())) {
                    i.setResource(OpenELISResources.INSTANCE.abort());
                    if (numEdit > 1) {
                        c2.setValue(manager.getSample().getReceivedDate());
                        //b.setEnabled(true);
                    }
                    /*
                     * set the focus back on the widget if the user pressed Tab
                     */
                    if (isFocusLostOnTab(c1))
                        Scheduler.get().scheduleDeferred(cmd);
                } else {
                    i.setResource(OpenELISResources.INSTANCE.commit());
                }
                tabIndex = -1;
            }

            public void onStateChange(StateChangeEvent event) {
                c1.setEnabled(isState(UPDATE) && canEdit);
            }

            public Widget onTab(boolean forward) {
                tabIndex = index;
                return SecondDataEntryUtil.onTab(index, forward, editWidgets);
            }
        });

        /*
         * add screen handler for the button
         */
        addScreenHandler(i,
                         SecondDataEntryUtil.getMatchImageKey(SampleMeta.RECEIVED_DATE),
                         new ScreenHandler<Object>() {
                             public void onStateChange(StateChangeEvent event) {
                                 i.setResource(OpenELISResources.INSTANCE.blankIcon());
                             }
                         });

        /*
         * add screen handler for the button
         */
        /*addScreenHandler(b,
                         SecondDataEntryUtil.getButtonKey(SampleMeta.RECEIVED_DATE),
                         new ScreenHandler<Object>() {
                             public void onStateChange(StateChangeEvent event) {
                                 b.setEnabled(false);
                             }
                         });

        b.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                c1.setValue(manager.getSample().getReceivedDate());
                i.setResource(OpenELISResources.INSTANCE.commit());
                c1.setFocus(true);
            }
        });*/

        /*
         * add screen handler for the widget on the right
         */
        addScreenHandler(c2,
                         SecondDataEntryUtil.getRightWidgetKey(SampleMeta.RECEIVED_DATE),
                         new ScreenHandler<Datetime>() {
                             public void onDataChange(DataChangeEvent<Datetime> event) {
                                 c2.setValue(null);
                             }

                             public void onStateChange(StateChangeEvent event) {
                                 c2.setEnabled(false);
                             }
                         });
    }

    /**
     * Adds the row for client reference at the passed index; also adds the
     * applicable screen handlers to the widgets in the row
     */
    private void addClientReference(int row) {
        final int index;
        Label<String> l1, l2;
        final TextBox<String> t1, t2;
        final Image i;
        HorizontalPanel h;
        //final Button b;
        final ScheduledCommand cmd;

        /*
         * create the widgets and add them to the table panel
         */
        l1 = SecondDataEntryUtil.getPromptLabel(Messages.get().sample_clntRef());

        t1 = SecondDataEntryUtil.getTextBox(SecondDataEntryUtil.Type.STRING,
                                            196,
                                            TextBase.Case.LOWER,
                                            20,
                                            null);
        i = new Image();
        h = new HorizontalPanel();
        SecondDataEntryUtil.addWidgetAndImage(h, t1, i);

        index = editWidgets.size();
        editWidgets.add(t1);

        //b = SecondDataEntryUtil.getCopyButton();
        //l2 = SecondDataEntryUtil.getPromptLabel(Messages.get().sample_clntRef());
        t2 = SecondDataEntryUtil.getTextBox(SecondDataEntryUtil.Type.STRING,
                                            196,
                                            TextBase.Case.LOWER,
                                            20,
                                            null);

        //SecondDataEntryUtil.addWidgets(row, widgetTable, l1, h, b, l2, t2);
        SecondDataEntryUtil.addWidgets(row, widgetTable, l1, h, t2);

        /*
         * the scheduled command for setting the focus back to this widget when
         * it loses focus
         */
        cmd = SecondDataEntryUtil.getFocusCommand(t1);

        /*
         * add screen handler for the editable widget
         */
        addScreenHandler(t1, SampleMeta.CLIENT_REFERENCE, new ScreenHandler<String>() {
            public void onDataChange(DataChangeEvent<String> event) {
                t1.setValue(null);
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                Integer numEdit;

                /*
                 * find out how many times the widget has been edited
                 */
                numEdit = SecondDataEntryUtil.updateNumEdit(t1, numEdits);

                /*
                 * if the value entered is different from the value in the
                 * manager, show the icon for "no match" and if this widget has
                 * been edited multiple times, show the value in the manager in
                 * the widget on the right; if the value entered is the same as
                 * the value in the manager, show the icon for "match"
                 */
                if (DataBaseUtil.isDifferent(event.getValue(), manager.getSample()
                                                                      .getClientReference())) {
                    i.setResource(OpenELISResources.INSTANCE.abort());
                    if (numEdit > 1) {
                        t2.setValue(manager.getSample().getClientReference());
                        //b.setEnabled(true);
                    }
                    /*
                     * set the focus back on the widget if the user pressed Tab
                     */
                    if (isFocusLostOnTab(t1))
                        Scheduler.get().scheduleDeferred(cmd);
                } else {
                    i.setResource(OpenELISResources.INSTANCE.commit());
                }
                tabIndex = -1;
            }

            public void onStateChange(StateChangeEvent event) {
                t1.setEnabled(isState(UPDATE) && canEdit);
            }

            public Widget onTab(boolean forward) {
                tabIndex = index;
                return SecondDataEntryUtil.onTab(index, forward, editWidgets);
            }
        });

        /*
         * add screen handler for the image
         */
        addScreenHandler(i,
                         SecondDataEntryUtil.getMatchImageKey(SampleMeta.CLIENT_REFERENCE),
                         new ScreenHandler<Object>() {
                             public void onStateChange(StateChangeEvent event) {
                                 i.setResource(OpenELISResources.INSTANCE.blankIcon());
                             }
                         });

        /*
         * add screen handler for the button
         */
        /*addScreenHandler(b,
                         SecondDataEntryUtil.getButtonKey(SampleMeta.CLIENT_REFERENCE),
                         new ScreenHandler<Object>() {
                             public void onStateChange(StateChangeEvent event) {
                                 b.setEnabled(false);
                             }
                         });

        b.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                t1.setValue(manager.getSample().getClientReference());
                i.setResource(OpenELISResources.INSTANCE.commit());
                t1.setFocus(true);
            }
        });*/

        /*
         * add screen handler for the widget on the right
         */
        addScreenHandler(t2,
                         SecondDataEntryUtil.getRightWidgetKey(SampleMeta.CLIENT_REFERENCE),
                         new ScreenHandler<String>() {
                             public void onDataChange(DataChangeEvent<String> event) {
                                 t2.setValue(null);
                             }

                             public void onStateChange(StateChangeEvent event) {
                                 t2.setEnabled(false);
                             }
                         });
    }

    /**
     * Adds the row for environmental is_hazardous at the passed index; also
     * adds the applicable screen handlers to the widgets in the row
     */
    private void addEnvIsHazardous(int row) {
        final int index;
        Label<String> l1, l2;
        final CheckBox c1, c2;
        final Image i;
        HorizontalPanel h;
        //final Button b;
        final ScheduledCommand cmd;

        /*
         * create the widgets and add them to the table panel
         */
        l1 = SecondDataEntryUtil.getPromptLabel(Messages.get().sampleEnvironmental_hazardous());

        c1 = new CheckBox();
        i = new Image();
        h = new HorizontalPanel();
        SecondDataEntryUtil.addWidgetAndImage(h, c1, i);

        index = editWidgets.size();
        editWidgets.add(c1);

        //b = SecondDataEntryUtil.getCopyButton();
        //l2 = SecondDataEntryUtil.getPromptLabel(Messages.get().sampleEnvironmental_hazardous());
        c2 = new CheckBox();

        //SecondDataEntryUtil.addWidgets(row, widgetTable, l1, h, b, l2, c2);
        SecondDataEntryUtil.addWidgets(row, widgetTable, l1, h, c2);

        /*
         * the scheduled command for setting the focus back to this widget when
         * it loses focus
         */
        cmd = SecondDataEntryUtil.getFocusCommand(c1);

        /*
         * add screen handler for the editable widget
         */
        addScreenHandler(c1, SampleMeta.ENV_IS_HAZARDOUS, new ScreenHandler<String>() {
            public void onDataChange(DataChangeEvent<String> event) {
                c1.setValue("N");
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                Integer numEdit;

                /*
                 * find out how many times the widget has been edited
                 */
                numEdit = SecondDataEntryUtil.updateNumEdit(c1, numEdits);

                /*
                 * if the value entered is different from the value in the
                 * manager, show the icon for "no match" and if this widget has
                 * been edited multiple times, show the value in the manager in
                 * the widget on the right; if the value entered is the same as
                 * the value in the manager, show the icon for "match"
                 */
                if (DataBaseUtil.isDifferent(event.getValue(), manager.getSampleEnvironmental()
                                                                      .getIsHazardous())) {
                    i.setResource(OpenELISResources.INSTANCE.abort());
                    if (numEdit > 1) {
                        c2.setValue(manager.getSampleEnvironmental().getIsHazardous());
                        //b.setEnabled(true);
                    }
                    /*
                     * set the focus back on the widget if the user pressed Tab
                     */
                    if (isFocusLostOnTab(c1))
                        Scheduler.get().scheduleDeferred(cmd);
                } else {
                    i.setResource(OpenELISResources.INSTANCE.commit());
                }
                tabIndex = -1;
            }

            public void onStateChange(StateChangeEvent event) {
                c1.setEnabled(isState(UPDATE) && canEdit);
            }

            public Widget onTab(boolean forward) {
                tabIndex = index;
                return SecondDataEntryUtil.onTab(index, forward, editWidgets);
            }
        });

        /*
         * add screen handler for the image
         */
        addScreenHandler(i,
                         SecondDataEntryUtil.getMatchImageKey(SampleMeta.ENV_IS_HAZARDOUS),
                         new ScreenHandler<Object>() {
                             public void onStateChange(StateChangeEvent event) {
                                 i.setResource(OpenELISResources.INSTANCE.blankIcon());
                             }
                         });

        /*
         * add screen handler for the button
         */
        /*addScreenHandler(b,
                         SecondDataEntryUtil.getButtonKey(SampleMeta.ENV_IS_HAZARDOUS),
                         new ScreenHandler<Object>() {
                             public void onStateChange(StateChangeEvent event) {
                                 b.setEnabled(false);
                             }
                         });

        b.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                c1.setValue(manager.getSampleEnvironmental().getIsHazardous());
                i.setResource(OpenELISResources.INSTANCE.commit());
                c1.setFocus(true);
            }
        });*/

        /*
         * add screen handler for the widget on the right
         */
        addScreenHandler(c2,
                         SecondDataEntryUtil.getRightWidgetKey(SampleMeta.ENV_IS_HAZARDOUS),
                         new ScreenHandler<String>() {
                             public void onDataChange(DataChangeEvent<String> event) {
                                 c2.setValue("N");
                             }

                             public void onStateChange(StateChangeEvent event) {
                                 c2.setEnabled(false);
                             }
                         });
    }

    /**
     * Adds the row for environmental priority at the passed index
     */
    private void addEnvPriority(int row) {
        final int index;
        Label<String> l1, l2;
        final TextBox<Integer> t1, t2;
        final Image i;
        HorizontalPanel h;
        //final Button b;
        final ScheduledCommand cmd;

        /*
         * create the widgets and add them to the table panel
         */
        l1 = SecondDataEntryUtil.getPromptLabel(Messages.get().gen_priority());

        t1 = SecondDataEntryUtil.getTextBox(SecondDataEntryUtil.Type.INTEGER,
                                            24,
                                            TextBase.Case.LOWER,
                                            null,
                                            null);
        i = new Image();
        h = new HorizontalPanel();
        SecondDataEntryUtil.addWidgetAndImage(h, t1, i);

        index = editWidgets.size();
        editWidgets.add(t1);

        //b = SecondDataEntryUtil.getCopyButton();
        //l2 = SecondDataEntryUtil.getPromptLabel(Messages.get().gen_priority());
        t2 = SecondDataEntryUtil.getTextBox(SecondDataEntryUtil.Type.INTEGER,
                                            24,
                                            TextBase.Case.LOWER,
                                            null,
                                            null);

        //SecondDataEntryUtil.addWidgets(row, widgetTable, l1, h, b, l2, t2);
        SecondDataEntryUtil.addWidgets(row, widgetTable, l1, h, t2);

        /*
         * the scheduled command for setting the focus back to this widget when
         * it loses focus
         */
        cmd = SecondDataEntryUtil.getFocusCommand(t1);

        /*
         * add screen handler for the editable widget
         */
        addScreenHandler(t1, SampleMeta.ENV_PRIORITY, new ScreenHandler<Integer>() {
            public void onDataChange(DataChangeEvent<Integer> event) {
                t1.setValue(null);
            }

            public void onValueChange(ValueChangeEvent<Integer> event) {
                Integer numEdit;

                /*
                 * find out how many times the widget has been edited
                 */
                numEdit = SecondDataEntryUtil.updateNumEdit(t1, numEdits);

                /*
                 * if the value entered is different from the value in the
                 * manager, show the icon for "no match" and if this widget has
                 * been edited multiple times, show the value in the manager in
                 * the widget on the right; if the value entered is the same as
                 * the value in the manager, show the icon for "match"
                 */
                if (DataBaseUtil.isDifferent(event.getValue(), manager.getSampleEnvironmental()
                                                                      .getPriority())) {
                    i.setResource(OpenELISResources.INSTANCE.abort());
                    if (numEdit > 1) {
                        t2.setValue(manager.getSampleEnvironmental().getPriority());
                        //b.setEnabled(true);
                    }
                    /*
                     * set the focus back on the widget if the user pressed Tab
                     */
                    if (isFocusLostOnTab(t1))
                        Scheduler.get().scheduleDeferred(cmd);
                } else {
                    i.setResource(OpenELISResources.INSTANCE.commit());
                }
                tabIndex = -1;
            }

            public void onStateChange(StateChangeEvent event) {
                t1.setEnabled(isState(UPDATE) && canEdit);
            }

            public Widget onTab(boolean forward) {
                tabIndex = index;
                return SecondDataEntryUtil.onTab(index, forward, editWidgets);
            }
        });

        /*
         * add screen handler for the image
         */
        addScreenHandler(i,
                         SecondDataEntryUtil.getMatchImageKey(SampleMeta.ENV_PRIORITY),
                         new ScreenHandler<Object>() {
                             public void onStateChange(StateChangeEvent event) {
                                 i.setResource(OpenELISResources.INSTANCE.blankIcon());
                             }
                         });

        /*
         * add screen handler for the button
         */
        /*addScreenHandler(b,
                         SecondDataEntryUtil.getButtonKey(SampleMeta.ENV_PRIORITY),
                         new ScreenHandler<Object>() {
                             public void onStateChange(StateChangeEvent event) {
                                 b.setEnabled(false);
                             }
                         });

        b.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                t1.setValue(manager.getSampleEnvironmental().getPriority());
                i.setResource(OpenELISResources.INSTANCE.commit());
                t1.setFocus(true);
            }
        });*/

        /*
         * add screen handler for the widget on the right
         */
        addScreenHandler(t2,
                         SecondDataEntryUtil.getRightWidgetKey(SampleMeta.ENV_PRIORITY),
                         new ScreenHandler<Integer>() {
                             public void onDataChange(DataChangeEvent<Integer> event) {
                                 t2.setValue(null);
                             }

                             public void onStateChange(StateChangeEvent event) {
                                 t2.setEnabled(false);
                             }
                         });
    }

    /**
     * Adds the row for environmental collector at the passed index
     */
    private void addEnvCollector(int row) {
        final int index;
        Label<String> l1, l2;
        final TextBox<String> t1, t2;
        final Image i;
        HorizontalPanel h;
        //final Button b;
        final ScheduledCommand cmd;

        /*
         * create the widgets and add them to the table panel
         */
        l1 = SecondDataEntryUtil.getPromptLabel(Messages.get().sample_collector());

        t1 = SecondDataEntryUtil.getTextBox(SecondDataEntryUtil.Type.STRING, 235, null, 40, null);
        i = new Image();
        h = new HorizontalPanel();
        SecondDataEntryUtil.addWidgetAndImage(h, t1, i);

        index = editWidgets.size();
        editWidgets.add(t1);

        //b = SecondDataEntryUtil.getCopyButton();
        //l2 = SecondDataEntryUtil.getPromptLabel(Messages.get().sample_collector());
        t2 = SecondDataEntryUtil.getTextBox(SecondDataEntryUtil.Type.STRING, 235, null, 40, null);

        //SecondDataEntryUtil.addWidgets(row, widgetTable, l1, h, b, l2, t2);
        SecondDataEntryUtil.addWidgets(row, widgetTable, l1, h, t2);

        /*
         * the scheduled command for setting the focus back to this widget when
         * it loses focus
         */
        cmd = SecondDataEntryUtil.getFocusCommand(t1);

        /*
         * add screen handler for the editable widget
         */
        addScreenHandler(t1, SampleMeta.ENV_COLLECTOR, new ScreenHandler<String>() {
            public void onDataChange(DataChangeEvent<String> event) {
                t1.setValue(null);
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                Integer numEdit;

                /*
                 * find out how many times the widget has been edited
                 */
                numEdit = SecondDataEntryUtil.updateNumEdit(t1, numEdits);

                /*
                 * if the value entered is different from the value in the
                 * manager, show the icon for "no match" and if this widget has
                 * been edited multiple times, show the value in the manager in
                 * the widget on the right; if the value entered is the same as
                 * the value in the manager, show the icon for "match"
                 */
                if (DataBaseUtil.isDifferent(event.getValue(), manager.getSampleEnvironmental()
                                                                      .getCollector())) {
                    i.setResource(OpenELISResources.INSTANCE.abort());
                    if (numEdit > 1)
                        t2.setValue(manager.getSampleEnvironmental().getCollector());
                    /*
                     * set the focus back on the widget if the user pressed Tab
                     */
                    if (isFocusLostOnTab(t1))
                        Scheduler.get().scheduleDeferred(cmd);
                } else {
                    i.setResource(OpenELISResources.INSTANCE.commit());
                }
                tabIndex = -1;
            }

            public void onStateChange(StateChangeEvent event) {
                t1.setEnabled(isState(UPDATE) && canEdit);
            }

            public Widget onTab(boolean forward) {
                tabIndex = index;
                return SecondDataEntryUtil.onTab(index, forward, editWidgets);
            }
        });

        /*
         * add screen handler for the image
         */
        addScreenHandler(i,
                         SecondDataEntryUtil.getMatchImageKey(SampleMeta.ENV_COLLECTOR),
                         new ScreenHandler<Object>() {
                             public void onStateChange(StateChangeEvent event) {
                                 i.setResource(OpenELISResources.INSTANCE.blankIcon());
                             }
                         });

        /*
         * add screen handler for the button
         */
        /*addScreenHandler(b,
                         SecondDataEntryUtil.getButtonKey(SampleMeta.ENV_COLLECTOR),
                         new ScreenHandler<Object>() {
                             public void onStateChange(StateChangeEvent event) {
                                 b.setEnabled(false);
                             }
                         });

        b.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                t1.setValue(manager.getSampleEnvironmental().getCollector());
                i.setResource(OpenELISResources.INSTANCE.commit());
                t1.setFocus(true);
            }
        });*/

        /*
         * add screen handler for the widget on the right
         */
        addScreenHandler(t2,
                         SecondDataEntryUtil.getRightWidgetKey(SampleMeta.ENV_COLLECTOR),
                         new ScreenHandler<String>() {
                             public void onDataChange(DataChangeEvent<String> event) {
                                 t2.setValue(null);
                             }

                             public void onStateChange(StateChangeEvent event) {
                                 t2.setEnabled(false);
                             }
                         });
    }

    /**
     * Adds the row for environmental collector phone at the passed index
     */
    private void addEnvCollectorPhone(int row) {
        final int index;
        Label<String> l1, l2;
        final TextBox<String> t1, t2;
        final Image i;
        HorizontalPanel h;
        //final Button b;
        final ScheduledCommand cmd;

        /*
         * create the widgets and add them to the table panel
         */
        l1 = SecondDataEntryUtil.getPromptLabel(Messages.get().address_phone());

        t1 = SecondDataEntryUtil.getTextBox(SecondDataEntryUtil.Type.STRING,
                                            115,
                                            null,
                                            17,
                                            Messages.get().gen_phoneWithExtensionPattern());
        i = new Image();
        h = new HorizontalPanel();
        SecondDataEntryUtil.addWidgetAndImage(h, t1, i);

        index = editWidgets.size();
        editWidgets.add(t1);

        //b = SecondDataEntryUtil.getCopyButton();
        //l2 = SecondDataEntryUtil.getPromptLabel(Messages.get().address_phone());
        t2 = SecondDataEntryUtil.getTextBox(SecondDataEntryUtil.Type.STRING,
                                            115,
                                            null,
                                            17,
                                            Messages.get().gen_phoneWithExtensionPattern());

        //SecondDataEntryUtil.addWidgets(row, widgetTable, l1, h, b, l2, t2);
        SecondDataEntryUtil.addWidgets(row, widgetTable, l1, h, t2);

        /*
         * the scheduled command for setting the focus back to this widget when
         * it loses focus
         */
        cmd = SecondDataEntryUtil.getFocusCommand(t1);

        /*
         * add screen handler for the editable widget
         */
        addScreenHandler(t1, SampleMeta.ENV_COLLECTOR_PHONE, new ScreenHandler<String>() {
            public void onDataChange(DataChangeEvent<String> event) {
                t1.setValue(null);
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                Integer numEdit;

                /*
                 * find out how many times the widget has been edited
                 */
                numEdit = SecondDataEntryUtil.updateNumEdit(t1, numEdits);

                /*
                 * if the value entered is different from the value in the
                 * manager, show the icon for "no match" and if this widget has
                 * been edited multiple times, show the value in the manager in
                 * the widget on the right; if the value entered is the same as
                 * the value in the manager, show the icon for "match"
                 */
                if (DataBaseUtil.isDifferent(event.getValue(), manager.getSampleEnvironmental()
                                                                      .getCollectorPhone())) {
                    i.setResource(OpenELISResources.INSTANCE.abort());
                    if (numEdit > 1) {
                        t2.setValue(manager.getSampleEnvironmental().getCollectorPhone());
                        //b.setEnabled(true);
                    }
                    /*
                     * set the focus back on the widget if the user pressed Tab
                     */
                    if (isFocusLostOnTab(t1))
                        Scheduler.get().scheduleDeferred(cmd);
                } else {
                    i.setResource(OpenELISResources.INSTANCE.commit());
                }
                tabIndex = -1;
            }

            public void onStateChange(StateChangeEvent event) {
                t1.setEnabled(isState(UPDATE) && canEdit);
            }

            public Widget onTab(boolean forward) {
                tabIndex = index;
                return SecondDataEntryUtil.onTab(index, forward, editWidgets);
            }
        });

        /*
         * add screen handler for the image
         */
        addScreenHandler(i,
                         SecondDataEntryUtil.getMatchImageKey(SampleMeta.ENV_COLLECTOR_PHONE),
                         new ScreenHandler<Object>() {
                             public void onStateChange(StateChangeEvent event) {
                                 i.setResource(OpenELISResources.INSTANCE.blankIcon());
                             }
                         });

        /*
         * add screen handler for the button
         */
        /*addScreenHandler(b,
                         SecondDataEntryUtil.getButtonKey(SampleMeta.ENV_COLLECTOR_PHONE),
                         new ScreenHandler<Object>() {
                             public void onStateChange(StateChangeEvent event) {
                                 b.setEnabled(false);
                             }
                         });

        b.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                t1.setValue(manager.getSampleEnvironmental().getCollectorPhone());
                i.setResource(OpenELISResources.INSTANCE.commit());
                t1.setFocus(true);
            }
        });*/

        /*
         * add screen handler for the widget on the right
         */
        addScreenHandler(t2,
                         SecondDataEntryUtil.getRightWidgetKey(SampleMeta.ENV_COLLECTOR_PHONE),
                         new ScreenHandler<String>() {
                             public void onDataChange(DataChangeEvent<String> event) {
                                 t2.setValue(null);
                             }

                             public void onStateChange(StateChangeEvent event) {
                                 t2.setEnabled(false);
                             }
                         });
    }

    /**
     * Adds the row for environmental description at the passed index
     */
    private void addEnvDescription(int row) {
        final int index;
        Label<String> l1, l2;
        final TextBox<String> t1, t2;
        final Image i;
        HorizontalPanel h;
        //final Button b;
        final ScheduledCommand cmd;

        /*
         * create the widgets and add them to the table panel
         */
        l1 = SecondDataEntryUtil.getPromptLabel(Messages.get().gen_description());

        t1 = SecondDataEntryUtil.getTextBox(SecondDataEntryUtil.Type.STRING, 280, null, 40, null);
        i = new Image();
        h = new HorizontalPanel();
        SecondDataEntryUtil.addWidgetAndImage(h, t1, i);

        index = editWidgets.size();
        editWidgets.add(t1);

        //b = SecondDataEntryUtil.getCopyButton();
        //l2 = SecondDataEntryUtil.getPromptLabel(Messages.get().gen_description());
        t2 = SecondDataEntryUtil.getTextBox(SecondDataEntryUtil.Type.STRING, 280, null, 40, null);

        //SecondDataEntryUtil.addWidgets(row, widgetTable, l1, h, b, l2, t2);
        SecondDataEntryUtil.addWidgets(row, widgetTable, l1, h, t2);

        /*
         * the scheduled command for setting the focus back to this widget when
         * it loses focus
         */
        cmd = SecondDataEntryUtil.getFocusCommand(t1);

        /*
         * add screen handler for the editable widget
         */
        addScreenHandler(t1, SampleMeta.ENV_DESCRIPTION, new ScreenHandler<String>() {
            public void onDataChange(DataChangeEvent<String> event) {
                t1.setValue(null);
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                Integer numEdit;

                /*
                 * find out how many times the widget has been edited
                 */
                numEdit = SecondDataEntryUtil.updateNumEdit(t1, numEdits);

                /*
                 * if the value entered is different from the value in the
                 * manager, show the icon for "no match" and if this widget has
                 * been edited multiple times, show the value in the manager in
                 * the widget on the right; if the value entered is the same as
                 * the value in the manager, show the icon for "match"
                 */
                if (DataBaseUtil.isDifferent(event.getValue(), manager.getSampleEnvironmental()
                                                                      .getDescription())) {
                    i.setResource(OpenELISResources.INSTANCE.abort());
                    if (numEdit > 1) {
                        t2.setValue(manager.getSampleEnvironmental().getDescription());
                        //b.setEnabled(true);
                    }
                    /*
                     * set the focus back on the widget if the user pressed Tab
                     */
                    if (isFocusLostOnTab(t1))
                        Scheduler.get().scheduleDeferred(cmd);
                } else {
                    i.setResource(OpenELISResources.INSTANCE.commit());
                }
                tabIndex = -1;
            }

            public void onStateChange(StateChangeEvent event) {
                t1.setEnabled(isState(UPDATE) && canEdit);
            }

            public Widget onTab(boolean forward) {
                tabIndex = index;
                return SecondDataEntryUtil.onTab(index, forward, editWidgets);
            }
        });

        /*
         * add screen handler for the image
         */
        addScreenHandler(i,
                         SecondDataEntryUtil.getMatchImageKey(SampleMeta.ENV_DESCRIPTION),
                         new ScreenHandler<String>() {
                             public void onStateChange(StateChangeEvent event) {
                                 i.setResource(OpenELISResources.INSTANCE.blankIcon());
                             }
                         });

        /*
         * add screen handler for the button
         */
        /*addScreenHandler(b,
                         SecondDataEntryUtil.getButtonKey(SampleMeta.ENV_DESCRIPTION),
                         new ScreenHandler<Datetime>() {
                             public void onStateChange(StateChangeEvent event) {
                                 b.setEnabled(false);
                             }
                         });

        b.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                t1.setValue(manager.getSampleEnvironmental().getDescription());
                i.setResource(OpenELISResources.INSTANCE.commit());
                t1.setFocus(true);
            }
        });*/

        /*
         * add screen handler for the widget on the right
         */
        addScreenHandler(t2,
                         SecondDataEntryUtil.getRightWidgetKey(SampleMeta.ENV_DESCRIPTION),
                         new ScreenHandler<String>() {
                             public void onDataChange(DataChangeEvent<String> event) {
                                 t2.setValue(null);
                             }

                             public void onStateChange(StateChangeEvent event) {
                                 t2.setEnabled(false);
                             }
                         });
    }

    /**
     * Adds the row for environmental description at the passed index
     */
    private void addEnvLocation(int row) {
        final int index;
        Label<String> l1, l2;
        final TextBox<String> t1, t2;
        final Image i;
        HorizontalPanel h;
        //final Button b;
        final ScheduledCommand cmd;

        /*
         * create the widgets and add them to the table panel
         */
        l1 = SecondDataEntryUtil.getPromptLabel(Messages.get().gen_location());

        t1 = SecondDataEntryUtil.getTextBox(SecondDataEntryUtil.Type.STRING,
                                            231,
                                            TextBase.Case.LOWER,
                                            40,
                                            null);
        i = new Image();
        h = new HorizontalPanel();
        SecondDataEntryUtil.addWidgetAndImage(h, t1, i);

        index = editWidgets.size();
        editWidgets.add(t1);

        //b = SecondDataEntryUtil.getCopyButton();
        //l2 = SecondDataEntryUtil.getPromptLabel(Messages.get().gen_location());
        t2 = SecondDataEntryUtil.getTextBox(SecondDataEntryUtil.Type.STRING,
                                            231,
                                            TextBase.Case.LOWER,
                                            40,
                                            null);

        //SecondDataEntryUtil.addWidgets(row, widgetTable, l1, h, b, l2, t2);
        SecondDataEntryUtil.addWidgets(row, widgetTable, l1, h, t2);

        /*
         * the scheduled command for setting the focus back to this widget when
         * it loses focus
         */
        cmd = SecondDataEntryUtil.getFocusCommand(t1);

        /*
         * add screen handler for the editable widget
         */
        addScreenHandler(t1, SampleMeta.ENV_LOCATION, new ScreenHandler<String>() {
            public void onDataChange(DataChangeEvent<String> event) {
                t1.setValue(null);
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                Integer numEdit;

                /*
                 * find out how many times the widget has been edited
                 */
                numEdit = SecondDataEntryUtil.updateNumEdit(t1, numEdits);

                /*
                 * if the value entered is different from the value in the
                 * manager, show the icon for "no match" and if this widget has
                 * been edited multiple times, show the value in the manager in
                 * the widget on the right; if the value entered is the same as
                 * the value in the manager, show the icon for "match"
                 */
                if (DataBaseUtil.isDifferent(event.getValue(), manager.getSampleEnvironmental()
                                                                      .getLocation())) {
                    i.setResource(OpenELISResources.INSTANCE.abort());
                    if (numEdit > 1) {
                        t2.setValue(manager.getSampleEnvironmental().getLocation());
                        //b.setEnabled(true);
                    }
                    /*
                     * set the focus back on the widget if the user pressed Tab
                     */
                    if (isFocusLostOnTab(t1))
                        Scheduler.get().scheduleDeferred(cmd);
                } else {
                    i.setResource(OpenELISResources.INSTANCE.commit());
                }
                tabIndex = -1;
            }

            public void onStateChange(StateChangeEvent event) {
                t1.setEnabled(isState(UPDATE) && canEdit);
            }

            public Widget onTab(boolean forward) {
                tabIndex = index;
                return SecondDataEntryUtil.onTab(index, forward, editWidgets);
            }
        });

        /*
         * add screen handler for the image
         */
        addScreenHandler(i,
                         SecondDataEntryUtil.getMatchImageKey(SampleMeta.ENV_LOCATION),
                         new ScreenHandler<Object>() {
                             public void onStateChange(StateChangeEvent event) {
                                 i.setResource(OpenELISResources.INSTANCE.blankIcon());
                             }
                         });

        /*
         * add screen handler for the button
         */
        /*addScreenHandler(b,
                         SecondDataEntryUtil.getButtonKey(SampleMeta.ENV_LOCATION),
                         new ScreenHandler<Object>() {
                             public void onStateChange(StateChangeEvent event) {
                                 b.setEnabled(false);
                             }
                         });

        b.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                t1.setValue(manager.getSampleEnvironmental().getLocation());
                i.setResource(OpenELISResources.INSTANCE.commit());
                t1.setFocus(true);
            }
        });*/

        /*
         * add screen handler for the widget on the right
         */
        addScreenHandler(t2,
                         SecondDataEntryUtil.getRightWidgetKey(SampleMeta.ENV_LOCATION),
                         new ScreenHandler<String>() {
                             public void onDataChange(DataChangeEvent<String> event) {
                                 t2.setValue(null);
                             }

                             public void onStateChange(StateChangeEvent event) {
                                 t2.setEnabled(false);
                             }
                         });
    }

    /**
     * Adds the row for environmental location multiple unit at the passed index
     */
    private void addLocationAddrMultipleUnit(int row) {
        final int index;
        Label<String> l1, l2;
        final TextBox<String> t1, t2;
        final Image i;
        HorizontalPanel h;
        //final Button b;
        final ScheduledCommand cmd;

        /*
         * create the widgets and add them to the table panel
         */
        l1 = SecondDataEntryUtil.getPromptLabel(Messages.get().address_aptSuite());

        t1 = SecondDataEntryUtil.getTextBox(SecondDataEntryUtil.Type.STRING,
                                            231,
                                            TextBase.Case.UPPER,
                                            null,
                                            null);
        i = new Image();
        h = new HorizontalPanel();
        SecondDataEntryUtil.addWidgetAndImage(h, t1, i);

        index = editWidgets.size();
        editWidgets.add(t1);

        //b = SecondDataEntryUtil.getCopyButton();
        //l2 = SecondDataEntryUtil.getPromptLabel(Messages.get().address_aptSuite());
        t2 = SecondDataEntryUtil.getTextBox(SecondDataEntryUtil.Type.STRING,
                                            231,
                                            TextBase.Case.UPPER,
                                            null,
                                            null);

        //SecondDataEntryUtil.addWidgets(row, widgetTable, l1, h, b, l2, t2);
        SecondDataEntryUtil.addWidgets(row, widgetTable, l1, h, t2);

        /*
         * the scheduled command for setting the focus back to this widget when
         * it loses focus
         */
        cmd = SecondDataEntryUtil.getFocusCommand(t1);

        /*
         * add screen handler for the editable widget
         */
        addScreenHandler(t1, SampleMeta.LOCATION_ADDR_MULTIPLE_UNIT, new ScreenHandler<String>() {
            public void onDataChange(DataChangeEvent<String> event) {
                t1.setValue(null);
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                Integer numEdit;

                /*
                 * find out how many times the widget has been edited
                 */
                numEdit = SecondDataEntryUtil.updateNumEdit(t1, numEdits);

                /*
                 * if the value entered is different from the value in the
                 * manager, show the icon for "no match" and if this widget has
                 * been edited multiple times, show the value in the manager in
                 * the widget on the right; if the value entered is the same as
                 * the value in the manager, show the icon for "match"
                 */
                if (DataBaseUtil.isDifferent(event.getValue(), manager.getSampleEnvironmental()
                                                                      .getLocationAddress()
                                                                      .getMultipleUnit())) {
                    i.setResource(OpenELISResources.INSTANCE.abort());
                    if (numEdit > 1) {
                        t2.setValue(manager.getSampleEnvironmental()
                                           .getLocationAddress()
                                           .getMultipleUnit());
                        //b.setEnabled(true);
                    }
                    /*
                     * set the focus back on the widget if the user pressed Tab
                     */
                    if (isFocusLostOnTab(t1))
                        Scheduler.get().scheduleDeferred(cmd);
                } else {
                    i.setResource(OpenELISResources.INSTANCE.commit());
                }
                tabIndex = -1;
            }

            public void onStateChange(StateChangeEvent event) {
                t1.setEnabled(isState(UPDATE) && canEdit);
            }

            public Widget onTab(boolean forward) {
                tabIndex = index;
                return SecondDataEntryUtil.onTab(index, forward, editWidgets);
            }
        });

        /*
         * add screen handler for the image
         */
        addScreenHandler(i,
                         SecondDataEntryUtil.getMatchImageKey(SampleMeta.LOCATION_ADDR_MULTIPLE_UNIT),
                         new ScreenHandler<Object>() {
                             public void onStateChange(StateChangeEvent event) {
                                 i.setResource(OpenELISResources.INSTANCE.blankIcon());
                             }
                         });

        /*
         * add screen handler for the button
         */
        /*addScreenHandler(b,
                         SecondDataEntryUtil.getButtonKey(SampleMeta.LOCATION_ADDR_MULTIPLE_UNIT),
                         new ScreenHandler<Object>() {
                             public void onStateChange(StateChangeEvent event) {
                                 b.setEnabled(false);
                             }
                         });

        b.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                t1.setValue(manager.getSampleEnvironmental().getLocationAddress().getMultipleUnit());
                i.setResource(OpenELISResources.INSTANCE.commit());
                t1.setFocus(true);
            }
        });*/

        /*
         * add screen handler for the widget on the right
         */
        addScreenHandler(t2,
                         SecondDataEntryUtil.getRightWidgetKey(SampleMeta.LOCATION_ADDR_MULTIPLE_UNIT),
                         new ScreenHandler<String>() {
                             public void onDataChange(DataChangeEvent<String> event) {
                                 t2.setValue(null);
                             }

                             public void onStateChange(StateChangeEvent event) {
                                 t2.setEnabled(false);
                             }
                         });
    }

    /**
     * Adds the row for environmental location street address at the passed
     * index
     */
    private void addLocationAddrStreetAddress(int row) {
        final int index;
        Label<String> l1, l2;
        final TextBox<String> t1, t2;
        final Image i;
        HorizontalPanel h;
        //final Button b;
        final ScheduledCommand cmd;

        /*
         * create the widgets and add them to the table panel
         */
        l1 = SecondDataEntryUtil.getPromptLabel(Messages.get().address_address());

        t1 = SecondDataEntryUtil.getTextBox(SecondDataEntryUtil.Type.STRING,
                                            231,
                                            TextBase.Case.UPPER,
                                            null,
                                            null);
        i = new Image();
        h = new HorizontalPanel();
        SecondDataEntryUtil.addWidgetAndImage(h, t1, i);

        index = editWidgets.size();
        editWidgets.add(t1);

        //b = SecondDataEntryUtil.getCopyButton();
        //l2 = SecondDataEntryUtil.getPromptLabel(Messages.get().address_address());
        t2 = SecondDataEntryUtil.getTextBox(SecondDataEntryUtil.Type.STRING,
                                            231,
                                            TextBase.Case.UPPER,
                                            null,
                                            null);

        //SecondDataEntryUtil.addWidgets(row, widgetTable, l1, h, b, l2, t2);
        SecondDataEntryUtil.addWidgets(row, widgetTable, l1, h, t2);

        /*
         * the scheduled command for setting the focus back to this widget when
         * it loses focus
         */
        cmd = SecondDataEntryUtil.getFocusCommand(t1);

        /*
         * add screen handler for the editable widget
         */
        addScreenHandler(t1, SampleMeta.LOCATION_ADDR_STREET_ADDRESS, new ScreenHandler<String>() {
            public void onDataChange(DataChangeEvent<String> event) {
                t1.setValue(null);
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                Integer numEdit;

                /*
                 * find out how many times the widget has been edited
                 */
                numEdit = SecondDataEntryUtil.updateNumEdit(t1, numEdits);

                /*
                 * if the value entered is different from the value in the
                 * manager, show the icon for "no match" and if this widget has
                 * been edited multiple times, show the value in the manager in
                 * the widget on the right; if the value entered is the same as
                 * the value in the manager, show the icon for "match"
                 */
                if (DataBaseUtil.isDifferent(event.getValue(), manager.getSampleEnvironmental()
                                                                      .getLocationAddress()
                                                                      .getStreetAddress())) {
                    i.setResource(OpenELISResources.INSTANCE.abort());
                    if (numEdit > 1) {
                        t2.setValue(manager.getSampleEnvironmental()
                                           .getLocationAddress()
                                           .getStreetAddress());
                        //b.setEnabled(true);
                    }
                    /*
                     * set the focus back on the widget if the user pressed Tab
                     */
                    if (isFocusLostOnTab(t1))
                        Scheduler.get().scheduleDeferred(cmd);
                } else {
                    i.setResource(OpenELISResources.INSTANCE.commit());
                }
                tabIndex = -1;
            }

            public void onStateChange(StateChangeEvent event) {
                t1.setEnabled(isState(UPDATE) && canEdit);
            }

            public Widget onTab(boolean forward) {
                tabIndex = index;
                return SecondDataEntryUtil.onTab(index, forward, editWidgets);
            }
        });

        /*
         * add screen handler for the image
         */
        addScreenHandler(i,
                         SecondDataEntryUtil.getMatchImageKey(SampleMeta.LOCATION_ADDR_STREET_ADDRESS),
                         new ScreenHandler<Object>() {
                             public void onStateChange(StateChangeEvent event) {
                                 i.setResource(OpenELISResources.INSTANCE.blankIcon());
                             }
                         });

        /*
         * add screen handler for the button
         */
        /*addScreenHandler(b,
                         SecondDataEntryUtil.getButtonKey(SampleMeta.LOCATION_ADDR_STREET_ADDRESS),
                         new ScreenHandler<Object>() {
                             public void onStateChange(StateChangeEvent event) {
                                 b.setEnabled(false);
                             }
                         });

        b.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                t1.setValue(manager.getSampleEnvironmental()
                                   .getLocationAddress()
                                   .getStreetAddress());
                i.setResource(OpenELISResources.INSTANCE.commit());
                t1.setFocus(true);
            }
        });*/

        /*
         * add screen handler for the widget on the right
         */
        addScreenHandler(t2,
                         SecondDataEntryUtil.getRightWidgetKey(SampleMeta.LOCATION_ADDR_STREET_ADDRESS),
                         new ScreenHandler<String>() {
                             public void onDataChange(DataChangeEvent<String> event) {
                                 t2.setValue(null);
                             }

                             public void onStateChange(StateChangeEvent event) {
                                 t2.setEnabled(false);
                             }
                         });
    }

    /**
     * Adds the row for environmental location city at the passed index
     */
    private void addLocationAddrCity(int row) {
        final int index;
        Label<String> l1, l2;
        final TextBox<String> t1, t2;
        final Image i;
        HorizontalPanel h;
        //final Button b;
        final ScheduledCommand cmd;

        /*
         * create the widgets and add them to the table panel
         */
        l1 = SecondDataEntryUtil.getPromptLabel(Messages.get().address_city());

        t1 = SecondDataEntryUtil.getTextBox(SecondDataEntryUtil.Type.STRING,
                                            98,
                                            TextBase.Case.UPPER,
                                            null,
                                            null);
        i = new Image();
        h = new HorizontalPanel();
        SecondDataEntryUtil.addWidgetAndImage(h, t1, i);

        index = editWidgets.size();
        editWidgets.add(t1);

        //b = SecondDataEntryUtil.getCopyButton();
        //l2 = SecondDataEntryUtil.getPromptLabel(Messages.get().address_city());
        t2 = SecondDataEntryUtil.getTextBox(SecondDataEntryUtil.Type.STRING,
                                            98,
                                            TextBase.Case.UPPER,
                                            null,
                                            null);

        //SecondDataEntryUtil.addWidgets(row, widgetTable, l1, h, b, l2, t2);
        SecondDataEntryUtil.addWidgets(row, widgetTable, l1, h, t2);

        /*
         * the scheduled command for setting the focus back to this widget when
         * it loses focus
         */
        cmd = SecondDataEntryUtil.getFocusCommand(t1);

        /*
         * add screen handler for the editable widget
         */
        addScreenHandler(t1, SampleMeta.LOCATION_ADDR_CITY, new ScreenHandler<String>() {
            public void onDataChange(DataChangeEvent<String> event) {
                t1.setValue(null);
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                Integer numEdit;

                /*
                 * find out how many times the widget has been edited
                 */
                numEdit = SecondDataEntryUtil.updateNumEdit(t1, numEdits);

                /*
                 * if the value entered is different from the value in the
                 * manager, show the icon for "no match" and if this widget has
                 * been edited multiple times, show the value in the manager in
                 * the widget on the right; if the value entered is the same as
                 * the value in the manager, show the icon for "match"
                 */
                if (DataBaseUtil.isDifferent(event.getValue(), manager.getSampleEnvironmental()
                                                                      .getLocationAddress()
                                                                      .getCity())) {
                    i.setResource(OpenELISResources.INSTANCE.abort());
                    if (numEdit > 1) {
                        t2.setValue(manager.getSampleEnvironmental().getLocationAddress().getCity());
                        //b.setEnabled(true);
                    }
                    /*
                     * set the focus back on the widget if the user pressed Tab
                     */
                    if (isFocusLostOnTab(t1))
                        Scheduler.get().scheduleDeferred(cmd);
                } else {
                    i.setResource(OpenELISResources.INSTANCE.commit());
                }
                tabIndex = -1;
            }

            public void onStateChange(StateChangeEvent event) {
                t1.setEnabled(isState(UPDATE) && canEdit);
            }

            public Widget onTab(boolean forward) {
                tabIndex = index;
                return SecondDataEntryUtil.onTab(index, forward, editWidgets);
            }
        });

        /*
         * add screen handler for the image
         */
        addScreenHandler(i,
                         SecondDataEntryUtil.getMatchImageKey(SampleMeta.LOCATION_ADDR_CITY),
                         new ScreenHandler<Object>() {
                             public void onStateChange(StateChangeEvent event) {
                                 i.setResource(OpenELISResources.INSTANCE.blankIcon());
                             }
                         });

        /*
         * add screen handler for the button
         */
        /*addScreenHandler(b,
                         SecondDataEntryUtil.getButtonKey(SampleMeta.LOCATION_ADDR_CITY),
                         new ScreenHandler<Object>() {
                             public void onStateChange(StateChangeEvent event) {
                                 b.setEnabled(false);
                             }
                         });

        b.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                t1.setValue(manager.getSampleEnvironmental().getLocationAddress().getCity());
                i.setResource(OpenELISResources.INSTANCE.commit());
                t1.setFocus(true);
            }
        });*/

        /*
         * add screen handler for the widget on the right
         */
        addScreenHandler(t2,
                         SecondDataEntryUtil.getRightWidgetKey(SampleMeta.LOCATION_ADDR_CITY),
                         new ScreenHandler<String>() {
                             public void onDataChange(DataChangeEvent<String> event) {
                                 t2.setValue(null);
                             }

                             public void onStateChange(StateChangeEvent event) {
                                 t2.setEnabled(false);
                             }
                         });
    }

    /**
     * Adds the row for environmental location state at the passed index
     */
    private void addLocationAddrState(int row) {
        final int index;
        Label<String> l1, l2;
        final Dropdown<String> d1, d2;
        final Image i;
        HorizontalPanel h;
        //final Button b;
        final ScheduledCommand cmd;
        ArrayList<Item> model;

        /*
         * create the widgets and add them to the table panel
         */
        l1 = SecondDataEntryUtil.getPromptLabel(Messages.get().address_state());

        model = SecondDataEntryUtil.getDictionaryModel("state", CategoryMeta.getDictionaryEntry());
        d1 = SecondDataEntryUtil.getDropdown(SecondDataEntryUtil.Type.STRING,
                                             42,
                                             TextBase.Case.UPPER,
                                             model);
        i = new Image();
        h = new HorizontalPanel();
        SecondDataEntryUtil.addWidgetAndImage(h, d1, i);

        index = editWidgets.size();
        editWidgets.add(d1);

        //b = SecondDataEntryUtil.getCopyButton();
        //l2 = SecondDataEntryUtil.getPromptLabel(Messages.get().address_state());
        d2 = SecondDataEntryUtil.getDropdown(SecondDataEntryUtil.Type.STRING,
                                             42,
                                             TextBase.Case.UPPER,
                                             model);

        //SecondDataEntryUtil.addWidgets(row, widgetTable, l1, h, b, l2, d2);
        SecondDataEntryUtil.addWidgets(row, widgetTable, l1, h, d2);

        /*
         * the scheduled command for setting the focus back to this widget when
         * it loses focus
         */
        cmd = SecondDataEntryUtil.getFocusCommand(d1);

        /*
         * add screen handler for the editable widget
         */
        addScreenHandler(d1, SampleMeta.LOCATION_ADDR_STATE, new ScreenHandler<String>() {
            public void onDataChange(DataChangeEvent<String> event) {
                d1.setValue(null);
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                Integer numEdit;

                /*
                 * find out how many times the widget has been edited
                 */
                numEdit = SecondDataEntryUtil.updateNumEdit(d1, numEdits);

                /*
                 * if the value entered is different from the value in the
                 * manager, show the icon for "no match" and if this widget has
                 * been edited multiple times, show the value in the manager in
                 * the widget on the right; if the value entered is the same as
                 * the value in the manager, show the icon for "match"
                 */
                if (DataBaseUtil.isDifferent(event.getValue(), manager.getSampleEnvironmental()
                                                                      .getLocationAddress()
                                                                      .getState())) {
                    i.setResource(OpenELISResources.INSTANCE.abort());
                    if (numEdit > 1) {
                        d2.setValue(manager.getSampleEnvironmental()
                                           .getLocationAddress()
                                           .getState());
                        //b.setEnabled(true);
                    }
                    /*
                     * set the focus back on the widget if the user pressed Tab
                     */
                    if (isFocusLostOnTab(d1))
                        Scheduler.get().scheduleDeferred(cmd);
                } else {
                    i.setResource(OpenELISResources.INSTANCE.commit());
                }
                tabIndex = -1;
            }

            public void onStateChange(StateChangeEvent event) {
                d1.setEnabled(isState(UPDATE) && canEdit);
            }

            public Widget onTab(boolean forward) {
                tabIndex = index;
                return SecondDataEntryUtil.onTab(index, forward, editWidgets);
            }
        });

        /*
         * add screen handler for the button
         */
        addScreenHandler(i,
                         SecondDataEntryUtil.getMatchImageKey(SampleMeta.LOCATION_ADDR_STATE),
                         new ScreenHandler<Object>() {
                             public void onStateChange(StateChangeEvent event) {
                                 i.setResource(OpenELISResources.INSTANCE.blankIcon());
                             }
                         });

        /*
         * add screen handler for the button
         */
        /*addScreenHandler(b,
                         SecondDataEntryUtil.getButtonKey(SampleMeta.LOCATION_ADDR_STATE),
                         new ScreenHandler<Object>() {
                             public void onStateChange(StateChangeEvent event) {
                                 b.setEnabled(false);
                             }
                         });

        b.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                d1.setValue(manager.getSampleEnvironmental().getLocationAddress().getState());
                i.setResource(OpenELISResources.INSTANCE.commit());
                d1.setFocus(true);
            }
        });*/

        /*
         * add screen handler for the widget on the right
         */
        addScreenHandler(d2,
                         SecondDataEntryUtil.getRightWidgetKey(SampleMeta.LOCATION_ADDR_STATE),
                         new ScreenHandler<String>() {
                             public void onDataChange(DataChangeEvent<String> event) {
                                 d2.setValue(null);
                             }

                             public void onStateChange(StateChangeEvent event) {
                                 d2.setEnabled(false);
                             }
                         });
    }

    /**
     * Adds the row for environmental location state at the passed index
     */
    private void addLocationAddrZipCode(int row) {
        final int index;
        Label<String> l1, l2;
        final TextBox<String> t1, t2;
        final Image i;
        HorizontalPanel h;
        //final Button b;
        final ScheduledCommand cmd;

        /*
         * create the widgets and add them to the table panel
         */
        l1 = SecondDataEntryUtil.getPromptLabel(Messages.get().address_zipcode());

        t1 = SecondDataEntryUtil.getTextBox(SecondDataEntryUtil.Type.STRING,
                                            75,
                                            TextBase.Case.UPPER,
                                            null,
                                            Messages.get().gen_zipcodePattern());
        i = new Image();
        h = new HorizontalPanel();
        SecondDataEntryUtil.addWidgetAndImage(h, t1, i);

        index = editWidgets.size();
        editWidgets.add(t1);

        //b = SecondDataEntryUtil.getCopyButton();
        //l2 = SecondDataEntryUtil.getPromptLabel(Messages.get().address_zipcode());
        t2 = SecondDataEntryUtil.getTextBox(SecondDataEntryUtil.Type.STRING,
                                            75,
                                            TextBase.Case.UPPER,
                                            null,
                                            Messages.get().gen_zipcodePattern());

        //SecondDataEntryUtil.addWidgets(row, widgetTable, l1, h, b, l2, t2);
        SecondDataEntryUtil.addWidgets(row, widgetTable, l1, h, t2);

        /*
         * the scheduled command for setting the focus back to this widget when
         * it loses focus
         */
        cmd = SecondDataEntryUtil.getFocusCommand(t1);

        /*
         * add screen handler for the editable widget
         */
        addScreenHandler(t1, SampleMeta.LOCATION_ADDR_ZIP_CODE, new ScreenHandler<String>() {
            public void onDataChange(DataChangeEvent<String> event) {
                t1.setValue(null);
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                Integer numEdit;

                /*
                 * find out how many times the widget has been edited
                 */
                numEdit = SecondDataEntryUtil.updateNumEdit(t1, numEdits);

                /*
                 * if the value entered is different from the value in the
                 * manager, show the icon for "no match" and if this widget has
                 * been edited multiple times, show the value in the manager in
                 * the widget on the right; if the value entered is the same as
                 * the value in the manager, show the icon for "match"
                 */
                if (DataBaseUtil.isDifferent(event.getValue(), manager.getSampleEnvironmental()
                                                                      .getLocationAddress()
                                                                      .getZipCode())) {
                    i.setResource(OpenELISResources.INSTANCE.abort());
                    if (numEdit > 1) {
                        t2.setValue(manager.getSampleEnvironmental()
                                           .getLocationAddress()
                                           .getZipCode());
                        //b.setEnabled(true);
                    }
                    /*
                     * set the focus back on the widget if the user pressed Tab
                     */
                    if (isFocusLostOnTab(t1))
                        Scheduler.get().scheduleDeferred(cmd);
                } else {
                    i.setResource(OpenELISResources.INSTANCE.commit());
                }
                tabIndex = -1;
            }

            public void onStateChange(StateChangeEvent event) {
                t1.setEnabled(isState(UPDATE) && canEdit);
            }

            public Widget onTab(boolean forward) {
                tabIndex = index;
                return SecondDataEntryUtil.onTab(index, forward, editWidgets);
            }
        });

        /*
         * add screen handler for the button
         */
        addScreenHandler(i,
                         SecondDataEntryUtil.getMatchImageKey(SampleMeta.LOCATION_ADDR_ZIP_CODE),
                         new ScreenHandler<Object>() {
                             public void onStateChange(StateChangeEvent event) {
                                 i.setResource(OpenELISResources.INSTANCE.blankIcon());
                             }
                         });

        /*
         * add screen handler for the button
         */
        /*addScreenHandler(b,
                         SecondDataEntryUtil.getButtonKey(SampleMeta.LOCATION_ADDR_ZIP_CODE),
                         new ScreenHandler<Object>() {
                             public void onStateChange(StateChangeEvent event) {
                                 b.setEnabled(false);
                             }
                         });

        b.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                t1.setValue(manager.getSampleEnvironmental().getLocationAddress().getZipCode());
                i.setResource(OpenELISResources.INSTANCE.commit());
                t1.setFocus(true);
            }
        });*/

        /*
         * add screen handler for the widget on the right
         */
        addScreenHandler(t2,
                         SecondDataEntryUtil.getRightWidgetKey(SampleMeta.LOCATION_ADDR_ZIP_CODE),
                         new ScreenHandler<String>() {
                             public void onDataChange(DataChangeEvent<String> event) {
                                 t2.setValue(null);
                             }

                             public void onStateChange(StateChangeEvent event) {
                                 t2.setEnabled(false);
                             }
                         });
    }

    /**
     * Adds the row for environmental location country at the passed index
     */
    private void addLocationAddrCountry(int row) {
        final int index;
        Label<String> l1, l2;
        final Dropdown<String> d1, d2;
        final Image i;
        HorizontalPanel h;
        //final Button b;
        final ScheduledCommand cmd;
        ArrayList<Item> model;

        /*
         * create the widgets and add them to the table panel
         */
        l1 = SecondDataEntryUtil.getPromptLabel(Messages.get().address_country());

        model = SecondDataEntryUtil.getDictionaryModel("country", CategoryMeta.getDictionaryEntry());
        d1 = SecondDataEntryUtil.getDropdown(SecondDataEntryUtil.Type.STRING, 231, null, model);
        i = new Image();
        h = new HorizontalPanel();
        SecondDataEntryUtil.addWidgetAndImage(h, d1, i);

        index = editWidgets.size();
        editWidgets.add(d1);

        //b = SecondDataEntryUtil.getCopyButton();
        //l2 = SecondDataEntryUtil.getPromptLabel(Messages.get().address_country());
        d2 = SecondDataEntryUtil.getDropdown(SecondDataEntryUtil.Type.STRING, 231, null, model);

        //SecondDataEntryUtil.addWidgets(row, widgetTable, l1, h, b, l2, d2);
        SecondDataEntryUtil.addWidgets(row, widgetTable, l1, h, d2);

        /*
         * the scheduled command for setting the focus back to this widget when
         * it loses focus
         */
        cmd = SecondDataEntryUtil.getFocusCommand(d1);

        /*
         * add screen handler for the editable widget
         */
        addScreenHandler(d1, SampleMeta.LOCATION_ADDR_COUNTRY, new ScreenHandler<String>() {
            public void onDataChange(DataChangeEvent<String> event) {
                d1.setValue(null);
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                Integer numEdit;

                /*
                 * find out how many times the widget has been edited
                 */
                numEdit = SecondDataEntryUtil.updateNumEdit(d1, numEdits);

                /*
                 * if the value entered is different from the value in the
                 * manager, show the icon for "no match" and if this widget has
                 * been edited multiple times, show the value in the manager in
                 * the widget on the right; if the value entered is the same as
                 * the value in the manager, show the icon for "match"
                 */
                if (DataBaseUtil.isDifferent(event.getValue(), manager.getSampleEnvironmental()
                                                                      .getLocationAddress()
                                                                      .getCountry())) {
                    i.setResource(OpenELISResources.INSTANCE.abort());
                    if (numEdit > 1) {
                        d2.setValue(manager.getSampleEnvironmental()
                                           .getLocationAddress()
                                           .getCountry());
                        //b.setEnabled(true);
                    }
                    /*
                     * set the focus back on the widget if the user pressed Tab
                     */
                    if (isFocusLostOnTab(d1))
                        Scheduler.get().scheduleDeferred(cmd);
                } else {
                    i.setResource(OpenELISResources.INSTANCE.commit());
                }
                tabIndex = -1;
            }

            public void onStateChange(StateChangeEvent event) {
                d1.setEnabled(isState(UPDATE) && canEdit);
            }

            public Widget onTab(boolean forward) {
                tabIndex = index;
                return SecondDataEntryUtil.onTab(index, forward, editWidgets);
            }
        });

        /*
         * add screen handler for the image
         */
        addScreenHandler(i,
                         SecondDataEntryUtil.getMatchImageKey(SampleMeta.LOCATION_ADDR_COUNTRY),
                         new ScreenHandler<Object>() {
                             public void onStateChange(StateChangeEvent event) {
                                 i.setResource(OpenELISResources.INSTANCE.blankIcon());
                             }
                         });

        /*
         * add screen handler for the button
         */
        /*addScreenHandler(b,
                         SecondDataEntryUtil.getButtonKey(SampleMeta.LOCATION_ADDR_COUNTRY),
                         new ScreenHandler<Object>() {
                             public void onStateChange(StateChangeEvent event) {
                                 b.setEnabled(false);
                             }
                         });

        b.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                d1.setValue(manager.getSampleEnvironmental().getLocationAddress().getCountry());
                i.setResource(OpenELISResources.INSTANCE.commit());
                d1.setFocus(true);
            }
        });*/

        /*
         * add screen handler for the widget on the right
         */
        addScreenHandler(d2,
                         SecondDataEntryUtil.getRightWidgetKey(SampleMeta.LOCATION_ADDR_COUNTRY),
                         new ScreenHandler<String>() {
                             public void onDataChange(DataChangeEvent<String> event) {
                                 d2.setValue(null);
                             }

                             public void onStateChange(StateChangeEvent event) {
                                 d2.setEnabled(false);
                             }
                         });
    }

    /**
     * Adds the row for the report-to organization at the passed index
     */
    private void addReportTo(int row) {
        final int index;
        Label<String> l1, l2;
        final AutoComplete a1, a2;
        final Image i;
        HorizontalPanel h;
        //final Button b;
        final ScheduledCommand cmd;

        /*
         * create the widgets and add them to the table panel
         */
        l1 = SecondDataEntryUtil.getPromptLabel(Messages.get().sampleOrganization_reportTo());
        a1 = SecondDataEntryUtil.getOrganizationAutocomplete(180, TextBase.Case.UPPER, 565, parentScreen);
        i = new Image();
        h = new HorizontalPanel();
        SecondDataEntryUtil.addWidgetAndImage(h, a1, i);

        index = editWidgets.size();
        editWidgets.add(a1);

        //b = SecondDataEntryUtil.getCopyButton();
        //l2 = SecondDataEntryUtil.getPromptLabel(Messages.get().sampleOrganization_reportTo());
        a2 = SecondDataEntryUtil.getOrganizationAutocomplete(180, TextBase.Case.UPPER, 565, parentScreen);

        //SecondDataEntryUtil.addWidgets(row, widgetTable, l1, h, b, l2, a2);
        SecondDataEntryUtil.addWidgets(row, widgetTable, l1, h, a2);

        /*
         * the scheduled command for setting the focus back to this widget when
         * it loses focus
         */
        cmd = SecondDataEntryUtil.getFocusCommand(a1);

        /*
         * add screen handler for the editable widget
         */
        addScreenHandler(a1, REPORT_TO, new ScreenHandler<AutoCompleteValue>() {
            public void onDataChange(DataChangeEvent<AutoCompleteValue> event) {
                a1.setValue(null, "");
            }

            public void onValueChange(ValueChangeEvent<AutoCompleteValue> event) {
                Integer numEdit, eventId, orgId;
                String orgName;
                AutoCompleteValue val;
                ArrayList<SampleOrganizationViewDO> orgs;

                /*
                 * find out how many times the widget has been edited
                 */
                numEdit = SecondDataEntryUtil.updateNumEdit(a1, numEdits);

                /*
                 * if the value entered is different from the value in the
                 * manager, show the icon for "no match" and if this widget has
                 * been edited multiple times, show the value in the manager in
                 * the widget on the right; if the value entered is the same as
                 * the value in the manager, show the icon for "match"
                 */
                val = event.getValue();
                eventId = val != null ? val.getId() : null;

                orgs = manager.organization.getByType(Constants.dictionary().ORG_REPORT_TO);
                orgId = null;
                orgName = null;
                if (orgs != null && orgs.size() > 0) {
                    orgId = orgs.get(0).getOrganizationId();
                    orgName = orgs.get(0).getOrganizationName();
                }

                if (DataBaseUtil.isDifferent(eventId, orgId)) {
                    i.setResource(OpenELISResources.INSTANCE.abort());
                    if (numEdit > 1) {
                        a2.setValue(orgId, orgName);
                        //b.setEnabled(true);
                    }
                    /*
                     * set the focus back on the widget if the user pressed Tab
                     */
                    if (isFocusLostOnTab(a1))
                        Scheduler.get().scheduleDeferred(cmd);
                } else {
                    i.setResource(OpenELISResources.INSTANCE.commit());
                }
                tabIndex = -1;
            }

            public void onStateChange(StateChangeEvent event) {
                a1.setEnabled(isState(UPDATE) && canEdit);
            }

            public Widget onTab(boolean forward) {
                tabIndex = index;
                return SecondDataEntryUtil.onTab(index, forward, editWidgets);
            }
        });

        /*
         * add screen handler for the image
         */
        addScreenHandler(i,
                         SecondDataEntryUtil.getMatchImageKey(REPORT_TO),
                         new ScreenHandler<Object>() {
                             public void onStateChange(StateChangeEvent event) {
                                 i.setResource(OpenELISResources.INSTANCE.blankIcon());
                             }
                         });

        /*
         * add screen handler for the button
         */
        /*addScreenHandler(b,
                         SecondDataEntryUtil.getButtonKey(REPORT_TO),
                         new ScreenHandler<Datetime>() {
                             public void onStateChange(StateChangeEvent event) {
                                 b.setEnabled(false);
                             }
                         });

        b.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                Integer orgId;
                String orgName;
                ArrayList<SampleOrganizationViewDO> orgs;

                orgs = manager.organization.getByType(Constants.dictionary().ORG_REPORT_TO);
                orgId = null;
                orgName = null;
                if (orgs != null && orgs.size() > 0) {
                    orgId = orgs.get(0).getOrganizationId();
                    orgName = orgs.get(0).getOrganizationName();
                }

                a1.setValue(orgId, orgName);
                i.setResource(OpenELISResources.INSTANCE.commit());
                a1.setFocus(true);
            }
        });*/

        /*
         * add screen handler for the widget on the right
         */
        addScreenHandler(a2,
                         SecondDataEntryUtil.getRightWidgetKey(REPORT_TO),
                         new ScreenHandler<AutoCompleteValue>() {
                             public void onDataChange(DataChangeEvent<AutoCompleteValue> event) {
                                 a2.setValue(null, "");
                             }

                             public void onStateChange(StateChangeEvent event) {
                                 a2.setEnabled(false);
                             }
                         });
    }

    /**
     * Adds the row for the bill-to organization at the passed index
     */
    private void addBillTo(int row) {
        final int index;
        Label<String> l1, l2;
        final AutoComplete a1, a2;
        final Image i;
        HorizontalPanel h;
        //final Button b;
        final ScheduledCommand cmd;

        l1 = SecondDataEntryUtil.getPromptLabel(Messages.get().sampleOrganization_billTo());
        a1 = SecondDataEntryUtil.getOrganizationAutocomplete(180, TextBase.Case.UPPER, 565, parentScreen);
        i = new Image();
        h = new HorizontalPanel();
        SecondDataEntryUtil.addWidgetAndImage(h, a1, i);

        index = editWidgets.size();
        editWidgets.add(a1);

        //b = SecondDataEntryUtil.getCopyButton();
        //l2 = SecondDataEntryUtil.getPromptLabel(Messages.get().sampleOrganization_billTo());
        a2 = SecondDataEntryUtil.getOrganizationAutocomplete(180, TextBase.Case.UPPER, 565, parentScreen);

        //SecondDataEntryUtil.addWidgets(row, widgetTable, l1, h, b, l2, a2);
        SecondDataEntryUtil.addWidgets(row, widgetTable, l1, h, a2);

        /*
         * the scheduled command for setting the focus back to this widget when
         * it loses focus
         */
        cmd = SecondDataEntryUtil.getFocusCommand(a1);

        /*
         * add screen handler for the editable widget
         */
        addScreenHandler(a1, BILL_TO, new ScreenHandler<AutoCompleteValue>() {
            public void onDataChange(DataChangeEvent<AutoCompleteValue> event) {
                a1.setValue(null, "");
            }

            public void onValueChange(ValueChangeEvent<AutoCompleteValue> event) {
                Integer numEdit, eventId, orgId;
                String orgName;
                AutoCompleteValue val;
                SampleOrganizationViewDO sorg;
                ArrayList<SampleOrganizationViewDO> sorgs;

                /*
                 * find out how many times the widget has been edited
                 */
                numEdit = SecondDataEntryUtil.updateNumEdit(a1, numEdits);

                /*
                 * if the value entered is different from the value in the
                 * manager, show the icon for "no match" and if this widget has
                 * been edited multiple times, show the value in the manager in
                 * the widget on the right; if the value entered is the same as
                 * the value in the manager, show the icon for "match"
                 */
                val = event.getValue();
                eventId = val != null ? val.getId() : null;

                sorgs = manager.organization.getByType(Constants.dictionary().ORG_BILL_TO);
                orgId = null;
                orgName = null;
                if (sorgs != null && sorgs.size() > 0) {
                    sorg = sorgs.get(0);
                    orgId = sorg.getOrganizationId();
                    orgName = sorg.getOrganizationName();
                }

                if (DataBaseUtil.isDifferent(eventId, orgId)) {
                    i.setResource(OpenELISResources.INSTANCE.abort());
                    if (numEdit > 1) {
                        a2.setValue(orgId, orgName);
                        //b.setEnabled(true);
                    }
                    /*
                     * set the focus back on the widget if the user pressed Tab
                     */
                    if (isFocusLostOnTab(a1))
                        Scheduler.get().scheduleDeferred(cmd);
                } else {
                    i.setResource(OpenELISResources.INSTANCE.commit());
                }
                tabIndex = -1;
            }

            public void onStateChange(StateChangeEvent event) {
                a1.setEnabled(isState(UPDATE) && canEdit);
            }

            public Widget onTab(boolean forward) {
                tabIndex = index;
                return SecondDataEntryUtil.onTab(index, forward, editWidgets);
            }
        });

        /*
         * add screen handler for the image
         */
        addScreenHandler(i, SecondDataEntryUtil.getMatchImageKey(BILL_TO), new ScreenHandler<Object>() {
            public void onStateChange(StateChangeEvent event) {
                i.setResource(OpenELISResources.INSTANCE.blankIcon());
            }
        });

        /*
         * add screen handler for the button
         */
        /*addScreenHandler(b,
                         SecondDataEntryUtil.getButtonKey(BILL_TO),
                         new ScreenHandler<Datetime>() {
                             public void onStateChange(StateChangeEvent event) {
                                 b.setEnabled(false);
                             }
                         });

        b.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                Integer orgId;
                String orgName;
                ArrayList<SampleOrganizationViewDO> orgs;

                orgs = manager.organization.getByType(Constants.dictionary().ORG_BILL_TO);
                orgId = null;
                orgName = null;
                if (orgs != null && orgs.size() > 0) {
                    orgId = orgs.get(0).getOrganizationId();
                    orgName = orgs.get(0).getOrganizationName();
                }

                a1.setValue(orgId, orgName);
                i.setResource(OpenELISResources.INSTANCE.commit());
                a1.setFocus(true);
            }
        });*/

        /*
         * add screen handler for the widget on the right
         */
        addScreenHandler(a2,
                         SecondDataEntryUtil.getRightWidgetKey(BILL_TO),
                         new ScreenHandler<AutoCompleteValue>() {
                             public void onDataChange(DataChangeEvent<AutoCompleteValue> event) {
                                 a2.setValue(null, "");
                             }

                             public void onStateChange(StateChangeEvent event) {
                                 a2.setEnabled(false);
                             }
                         });
    }

    /**
     * Adds the row for project at the passed index
     */
    private void addProject(int row) {
        final int index;
        Label<String> l1, l2;
        final AutoComplete a1, a2;
        final Image i;
        HorizontalPanel h;
        //final Button b;
        final ScheduledCommand cmd;

        l1 = SecondDataEntryUtil.getPromptLabel(Messages.get().project_project());
        a1 = SecondDataEntryUtil.getProjectAutocomplete(180, TextBase.Case.LOWER, 440, parentScreen);
        i = new Image();
        h = new HorizontalPanel();
        SecondDataEntryUtil.addWidgetAndImage(h, a1, i);

        index = editWidgets.size();
        editWidgets.add(a1);

        //b = SecondDataEntryUtil.getCopyButton();
        //l2 = SecondDataEntryUtil.getPromptLabel(Messages.get().project_project());
        a2 = SecondDataEntryUtil.getProjectAutocomplete(180, TextBase.Case.LOWER, 440, parentScreen);

        //SecondDataEntryUtil.addWidgets(row, widgetTable, l1, h, b, l2, a2);
        SecondDataEntryUtil.addWidgets(row, widgetTable, l1, h, a2);

        /*
         * the scheduled command for setting the focus back to this widget when
         * it loses focus
         */
        cmd = SecondDataEntryUtil.getFocusCommand(a1);

        /*
         * add screen handler for the editable widget
         */
        addScreenHandler(a1, SampleMeta.PROJECT_NAME, new ScreenHandler<AutoCompleteValue>() {
            public void onDataChange(DataChangeEvent<AutoCompleteValue> event) {
                a1.setValue(null, "");
            }

            public void onValueChange(ValueChangeEvent<AutoCompleteValue> event) {
                Integer numEdit, eventId, projId;
                String projName;
                AutoCompleteValue val;
                SampleProjectViewDO sproj;

                /*
                 * find out how many times the widget has been edited
                 */
                numEdit = SecondDataEntryUtil.updateNumEdit(a1, numEdits);

                /*
                 * if the value entered is different from the value in the
                 * manager, show the icon for "no match" and if this widget has
                 * been edited multiple times, show the value in the manager in
                 * the widget on the right; if the value entered is the same as
                 * the value in the manager, show the icon for "match"
                 */
                val = event.getValue();
                eventId = val != null ? val.getId() : null;

                projId = null;
                projName = null;
                if (manager.project.count() > 0) {
                    sproj = manager.project.get(0);
                    projId = sproj.getProjectId();
                    projName = sproj.getProjectName();
                }

                if (DataBaseUtil.isDifferent(eventId, projId)) {
                    i.setResource(OpenELISResources.INSTANCE.abort());
                    if (numEdit > 1) {
                        a2.setValue(projId, projName);
                        //b.setEnabled(true);
                    }
                    /*
                     * set the focus back on the widget if the user pressed Tab
                     */
                    if (isFocusLostOnTab(a1))
                        Scheduler.get().scheduleDeferred(cmd);
                } else {
                    i.setResource(OpenELISResources.INSTANCE.commit());
                }
                tabIndex = -1;

            }

            public void onStateChange(StateChangeEvent event) {
                a1.setEnabled(isState(UPDATE) && canEdit);
            }

            public Widget onTab(boolean forward) {
                tabIndex = index;
                return SecondDataEntryUtil.onTab(index, forward, editWidgets);
            }
        });

        /*
         * add screen handler for the image
         */
        addScreenHandler(i,
                         SecondDataEntryUtil.getMatchImageKey(SampleMeta.PROJECT_NAME),
                         new ScreenHandler<Object>() {
                             public void onStateChange(StateChangeEvent event) {
                                 i.setResource(OpenELISResources.INSTANCE.blankIcon());
                             }
                         });

        /*
         * add screen handler for the button
         */
        /*addScreenHandler(b,
                         SecondDataEntryUtil.getButtonKey(SampleMeta.PROJECT_NAME),
                         new ScreenHandler<Datetime>() {
                             public void onStateChange(StateChangeEvent event) {
                                 b.setEnabled(false);
                             }
                         });

        b.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                Integer projId;
                String projName;
                SampleProjectViewDO proj;

                projId = null;
                projName = null;
                if (manager.project.count() > 0) {
                    proj = manager.project.get(0);
                    projId = proj.getProjectId();
                    projName = proj.getProjectName();
                }

                a1.setValue(projId, projName);
                i.setResource(OpenELISResources.INSTANCE.commit());
                a1.setFocus(true);
            }
        });*/

        /*
         * add screen handler for the widget on the right
         */
        addScreenHandler(a2,
                         SecondDataEntryUtil.getRightWidgetKey(SampleMeta.PROJECT_NAME),
                         new ScreenHandler<AutoCompleteValue>() {
                             public void onDataChange(DataChangeEvent<AutoCompleteValue> event) {
                                 a2.setValue(null, "");
                             }

                             public void onStateChange(StateChangeEvent event) {
                                 a2.setEnabled(false);
                             }
                         });
    }

    /**
     * Returns true if the passed widget lost focus by Tab being pressed;
     * returns false otherwise
     */
    private boolean isFocusLostOnTab(Widget widget) {
        return tabIndex != -1 && widget == editWidgets.get(tabIndex);
    }
}