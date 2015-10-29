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
import java.util.HashSet;
import java.util.logging.Level;

import org.openelis.constants.Messages;
import org.openelis.domain.Constants;
import org.openelis.domain.OrganizationDO;
import org.openelis.domain.PWSDO;
import org.openelis.domain.ProjectDO;
import org.openelis.domain.SampleOrganizationViewDO;
import org.openelis.domain.SampleProjectViewDO;
import org.openelis.manager.IOrderManager1;
import org.openelis.manager.SampleManager1;
import org.openelis.meta.CategoryMeta;
import org.openelis.meta.SampleMeta;
import org.openelis.modules.main.client.resources.OpenELISResources;
import org.openelis.modules.order1.client.OrderService1;
import org.openelis.modules.pws.client.PWSService;
import org.openelis.modules.sample1.client.SampleOrganizationUtility1;
import org.openelis.ui.common.DataBaseUtil;
import org.openelis.ui.common.Datetime;
import org.openelis.ui.common.NotFoundException;
import org.openelis.ui.event.DataChangeEvent;
import org.openelis.ui.event.ShortcutHandler;
import org.openelis.ui.event.StateChangeEvent;
import org.openelis.ui.screen.Screen;
import org.openelis.ui.screen.ScreenHandler;
import org.openelis.ui.screen.State;
import org.openelis.ui.widget.AutoComplete;
import org.openelis.ui.widget.AutoCompleteValue;
import org.openelis.ui.widget.Dropdown;
import org.openelis.ui.widget.Item;
import org.openelis.ui.widget.Label;
import org.openelis.ui.widget.TextBase;
import org.openelis.ui.widget.TextBox;
import org.openelis.ui.widget.calendar.Calendar;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Focusable;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.xml.client.Document;
import com.google.gwt.xml.client.Node;
import com.google.gwt.xml.client.XMLParser;

public class SDWISTabUI extends Screen {
    @UiTemplate("SDWISTab.ui.xml")
    interface SDWISTabUiBinder extends UiBinder<Widget, SDWISTabUI> {
    };

    private static SDWISTabUiBinder            uiBinder = GWT.create(SDWISTabUiBinder.class);

    @UiField
    protected FlexTable                        widgetTable;

    protected SampleManager1                   manager;

    protected Screen                           parentScreen;

    protected EventBus                         parentBus;

    protected SDWISTabUI                       screen;

    protected ArrayList<Widget>                editableWidgets;

    protected HashSet<Widget>                  verifiedWidgets;

    protected HashMap<Widget, Integer>         numEdits;

    protected HashMap<Widget, Command>         ctrl1Commands, ctrl2Commands;

    protected HashMap<Integer, IOrderManager1> orderManagers;

    protected HashMap<String, PWSDO>           pwsDOs;

    protected boolean                          canEdit;

    protected int                              tabIndex;

    protected static final String              REPORT_TO = "report_to", BILL_TO = "bill_to";

    public SDWISTabUI(Screen parentScreen) {
        this.parentScreen = parentScreen;
        this.parentBus = parentScreen.getEventBus();
        initWidget(uiBinder.createAndBindUi(this));

        initialize();
    }

    /**
     * Setup state and data change handles for every widget on the screen
     */
    private void initialize() {
        screen = this;

        /*
         * these contain the commands for the shortcuts "Ctrl+1" and "Ctrl+2"
         * respectively, for each editable widget
         */
        ctrl1Commands = new HashMap<Widget, Command>();
        ctrl2Commands = new HashMap<Widget, Command>();

        orderManagers = new HashMap<Integer, IOrderManager1>();
        pwsDOs = new HashMap<String, PWSDO>();

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
                 * this contains are all the widgets that have been verified
                 */
                verifiedWidgets = new HashSet<Widget>();

                /*
                 * set the focus to the first editable widget; if the focus is
                 * not set in this command, the style for focus doesn't show up
                 * even if the widget has the cursor and is enabled
                 */
                cmd = new ScheduledCommand() {
                    @Override
                    public void execute() {
                        if (editableWidgets != null && editableWidgets.size() > 0)
                            ((Focusable)editableWidgets.get(0)).setFocus(true);
                    }
                };
                Scheduler.get().scheduleDeferred(cmd);
            }
        });

        /*
         * add handlers for the shortcuts "Ctrl+1" and "Ctrl+2"; when one of the
         * shortcuts is used, find out which widget has focus; execute the
         * command for that widget and that shortcut; don't execute the command
         * unless the widget has been edited at least twice, because otherwise
         * the user won't know what the value for that field in the manager is
         */
        addShortcut(new ShortcutHandler() {
            @Override
            public void onShortcut() {
                Integer numEdit;
                Command cmd;

                if (focused != null) {
                    numEdit = numEdits.get(focused);
                    if (numEdit != null && numEdit >= 2) {
                        cmd = ctrl1Commands.get(focused);
                        if (cmd != null)
                            cmd.execute();
                    }
                }
            }
        }, '1', CTRL);

        addShortcut(new ShortcutHandler() {
            @Override
            public void onShortcut() {
                Integer numEdit;
                Command cmd;

                if (focused != null) {
                    numEdit = numEdits.get(focused);
                    if (numEdit != null && numEdit >= 2) {
                        cmd = ctrl2Commands.get(focused);
                        if (cmd != null)
                            cmd.execute();
                    }
                }
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

        editableWidgets = new ArrayList<Widget>();
        /*
         * parse the xml and add widgets for the fields to the widget table
         */
        doc = XMLParser.parse(xml);
        root = doc.getDocumentElement();
        row = 0;
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
                  Constants.domain().SDWIS.equals(manager.getSample().getDomain());
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
            case SampleMeta.SDWIS_PWS_NUMBER0:
                addSDWISPwsNumber0(row);
                break;
            case SampleMeta.SDWIS_STATE_LAB_ID:
                addSDWISStateLabId(row);
                break;
            case SampleMeta.SDWIS_FACILITY_ID:
                addSDWISFacilityId(row);
                break;
            case SampleMeta.SDWIS_SAMPLE_TYPE_ID:
                addSDWISSampleTypeId(row);
                break;
            case SampleMeta.SDWIS_SAMPLE_CATEGORY_ID:
                addSDWISSampleCategoryId(row);
                break;
            case SampleMeta.SDWIS_SAMPLE_POINT_ID:
                addSDWISSamplePointId(row);
                break;
            case SampleMeta.SDWIS_LOCATION:
                addSDWISLocation(row);
                break;
            case SampleMeta.SDWIS_PRIORITY:
                addSDWISPriority(row);
                break;
            case SampleMeta.SDWIS_COLLECTOR:
                addSDWISCollector(row);
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
        Label<String> l;
        final TextBox<Integer> t1, t2;
        final Image i1, i2;
        HorizontalPanel h;
        final ScheduledCommand scmd;
        Command c1cmd, c2cmd;

        /*
         * create the widgets and add them to the table panel
         */
        l = SecondDataEntryUtil.getPromptLabel(Messages.get().order_orderNum());

        t1 = SecondDataEntryUtil.getTextBox(SecondDataEntryUtil.Type.INTEGER, 75, null, null, null);
        i1 = new Image();
        h = new HorizontalPanel();
        SecondDataEntryUtil.addWidgetAndImage(h, t1, i1);

        index = editableWidgets.size();
        editableWidgets.add(t1);

        i2 = new Image();
        t2 = SecondDataEntryUtil.getTextBox(SecondDataEntryUtil.Type.INTEGER, 75, null, null, null);

        SecondDataEntryUtil.addWidgets(row, widgetTable, l, h, i2, t2);

        /*
         * the scheduled command for setting the focus back to this widget when
         * it loses focus
         */
        scmd = SecondDataEntryUtil.getFocusCommand(t1);

        /*
         * add screen handler for the editable widget
         */
        addScreenHandler(t1, SampleMeta.ORDER_ID, new ScreenHandler<Integer>() {
            public void onDataChange(DataChangeEvent<Integer> event) {
                t1.setValue(null);
            }

            public void onValueChange(ValueChangeEvent<Integer> event) {
                Integer numEdit, orderId;

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
                orderId = event.getValue();
                if (DataBaseUtil.isDifferent(orderId, manager.getSample().getOrderId())) {
                    i1.setResource(OpenELISResources.INSTANCE.abort());
                    if (numEdit > 1)
                        t2.setValue(manager.getSample().getOrderId());
                    /*
                     * set the focus back on the widget if the user pressed Tab
                     */
                    if (isFocusLostOnTab(t1))
                        Scheduler.get().scheduleDeferred(scmd);
                    verifiedWidgets.remove(t1);
                } else {
                    i1.setResource(OpenELISResources.INSTANCE.commit());
                    verifiedWidgets.add(t1);
                }
                tabIndex = -1;
            }

            public void onStateChange(StateChangeEvent event) {
                t1.setEnabled(isState(UPDATE) && canEdit);
            }

            public Widget onTab(boolean forward) {
                tabIndex = index;
                return SecondDataEntryUtil.onTab(index, forward, editableWidgets);
            }
        });

        /*
         * the command executed when the shortcut "Ctrl+1" is used
         */
        c1cmd = new Command() {
            @Override
            public void execute() {
                if (DataBaseUtil.isDifferent(t1.getValue(), manager.getSample().getOrderId())) {
                    t1.setValue(manager.getSample().getOrderId());
                    i1.setResource(OpenELISResources.INSTANCE.commit());
                    i2.setResource(OpenELISResources.INSTANCE.arrowLeftImage());
                    verifiedWidgets.add(t1);
                }
            }
        };
        ctrl1Commands.put(t1, c1cmd);

        /*
         * the command executed when the shortcut "Ctrl+2" is used
         */
        c2cmd = new Command() {
            @Override
            public void execute() {
                if (DataBaseUtil.isDifferent(t1.getValue(), manager.getSample().getOrderId())) {
                    /*
                     * if the entered value is not a valid send-out order id,
                     * show the user an error and clear the value
                     */
                    if ( !isValidSendOutOrder(t1.getValue())) {
                        Window.alert(Messages.get()
                                             .sample_orderIdInvalidException(manager.getSample()
                                                                                    .getAccessionNumber(),
                                                                             t1.getValue()));
                        return;
                    }
                    manager.getSample().setOrderId(t1.getValue());
                    t2.setValue(t1.getValue());
                    i1.setResource(OpenELISResources.INSTANCE.commit());
                    i2.setResource(OpenELISResources.INSTANCE.arrowRightImage());
                    verifiedWidgets.add(t1);
                }
            }
        };
        ctrl2Commands.put(t1, c2cmd);

        /*
         * add screen handler for the image for match/no match
         */
        addScreenHandler(i1,
                         SecondDataEntryUtil.getMatchImageKey(SampleMeta.ORDER_ID),
                         new ScreenHandler<Object>() {
                             public void onStateChange(StateChangeEvent event) {
                                 i1.setResource(OpenELISResources.INSTANCE.blankIcon());
                             }
                         });

        /*
         * add screen handler for the image for showing the direction in which
         * the data was copied
         */
        addScreenHandler(i2,
                         SecondDataEntryUtil.getCopyImageKey(SampleMeta.ORDER_ID),
                         new ScreenHandler<Object>() {
                             public void onStateChange(StateChangeEvent event) {
                                 i2.setResource(OpenELISResources.INSTANCE.blankIcon());
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
        Label<String> l;
        final Calendar c1, c2;
        final Image i1, i2;
        HorizontalPanel h;
        final ScheduledCommand scmd;
        Command c1cmd, c2cmd;

        /*
         * create the widgets and add them to the table panel
         */
        l = SecondDataEntryUtil.getPromptLabel(Messages.get().sample_collected());

        c1 = SecondDataEntryUtil.getCalendar(90, 0, 2);
        i1 = new Image();
        h = new HorizontalPanel();
        SecondDataEntryUtil.addWidgetAndImage(h, c1, i1);

        index = editableWidgets.size();
        editableWidgets.add(c1);

        i2 = new Image();
        c2 = SecondDataEntryUtil.getCalendar(90, 0, 2);

        SecondDataEntryUtil.addWidgets(row, widgetTable, l, h, i2, c2);

        /*
         * the scheduled command for setting the focus back to this widget when
         * it loses focus
         */
        scmd = SecondDataEntryUtil.getFocusCommand(c1);

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
                if (DataBaseUtil.isDifferentYD(event.getValue(), manager.getSample()
                                                                        .getCollectionDate())) {
                    i1.setResource(OpenELISResources.INSTANCE.abort());
                    if (numEdit > 1)
                        c2.setValue(manager.getSample().getCollectionDate());
                    /*
                     * set the focus back on the widget if the user pressed Tab
                     */
                    if (isFocusLostOnTab(c1))
                        Scheduler.get().scheduleDeferred(scmd);
                    verifiedWidgets.remove(c1);
                } else {
                    i1.setResource(OpenELISResources.INSTANCE.commit());
                    verifiedWidgets.add(c1);
                }
                tabIndex = -1;
            }

            public void onStateChange(StateChangeEvent event) {
                c1.setEnabled(isState(UPDATE) && canEdit);
            }

            public Widget onTab(boolean forward) {
                tabIndex = index;
                return SecondDataEntryUtil.onTab(index, forward, editableWidgets);
            }
        });

        /*
         * the command executed when the shortcut "Ctrl+1" is used
         */
        c1cmd = new Command() {
            @Override
            public void execute() {
                if (DataBaseUtil.isDifferentYD(c1.getValue(), manager.getSample()
                                                                     .getCollectionDate())) {
                    c1.setValue(manager.getSample().getCollectionDate());
                    i1.setResource(OpenELISResources.INSTANCE.commit());
                    i2.setResource(OpenELISResources.INSTANCE.arrowLeftImage());
                    verifiedWidgets.add(c1);
                }
            }
        };
        ctrl1Commands.put(c1, c1cmd);

        /*
         * the command executed when the shortcut "Ctrl+2" is used
         */
        c2cmd = new Command() {
            @Override
            public void execute() {
                if (DataBaseUtil.isDifferentYD(c1.getValue(), manager.getSample()
                                                                     .getCollectionDate())) {
                    manager.getSample().setCollectionDate(c1.getValue());
                    c2.setValue(c1.getValue());
                    i1.setResource(OpenELISResources.INSTANCE.commit());
                    i2.setResource(OpenELISResources.INSTANCE.arrowRightImage());
                    verifiedWidgets.add(c1);
                }
            }
        };
        ctrl2Commands.put(c1, c2cmd);

        /*
         * add screen handler for the image for match/no match
         */
        addScreenHandler(i1,
                         SecondDataEntryUtil.getMatchImageKey(SampleMeta.COLLECTION_DATE),
                         new ScreenHandler<Datetime>() {
                             public void onStateChange(StateChangeEvent event) {
                                 i1.setResource(OpenELISResources.INSTANCE.blankIcon());
                             }
                         });

        /*
         * add screen handler for the image for showing the direction in which
         * the data was copied
         */
        addScreenHandler(i2,
                         SecondDataEntryUtil.getCopyImageKey(SampleMeta.COLLECTION_DATE),
                         new ScreenHandler<Object>() {
                             public void onStateChange(StateChangeEvent event) {
                                 i2.setResource(OpenELISResources.INSTANCE.blankIcon());
                             }
                         });

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
        Label<String> l;
        final Calendar c1, c2;
        final Image i1, i2;
        HorizontalPanel h;
        final ScheduledCommand scmd;
        Command c1cmd, c2cmd;

        /*
         * create the widgets and add them to the table panel
         */
        l = SecondDataEntryUtil.getPromptLabel(Messages.get().gen_time());

        c1 = SecondDataEntryUtil.getCalendar(60, 3, 4);
        i1 = new Image();
        h = new HorizontalPanel();
        SecondDataEntryUtil.addWidgetAndImage(h, c1, i1);

        index = editableWidgets.size();
        editableWidgets.add(c1);

        i2 = new Image();
        c2 = SecondDataEntryUtil.getCalendar(60, 3, 4);

        SecondDataEntryUtil.addWidgets(row, widgetTable, l, h, i2, c2);

        /*
         * the scheduled command for setting the focus back to this widget when
         * it loses focus
         */
        scmd = SecondDataEntryUtil.getFocusCommand(c1);

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
                    i1.setResource(OpenELISResources.INSTANCE.abort());
                    if (numEdit > 1)
                        c2.setValue(manager.getSample().getCollectionTime());
                    /*
                     * set the focus back on the widget if the user pressed Tab
                     */
                    if (isFocusLostOnTab(c1))
                        Scheduler.get().scheduleDeferred(scmd);
                    verifiedWidgets.remove(c1);
                } else {
                    i1.setResource(OpenELISResources.INSTANCE.commit());
                    verifiedWidgets.add(c1);
                }
                tabIndex = -1;
            }

            public void onStateChange(StateChangeEvent event) {
                c1.setEnabled(isState(UPDATE) && canEdit);
            }

            public Widget onTab(boolean forward) {
                tabIndex = index;
                return SecondDataEntryUtil.onTab(index, forward, editableWidgets);
            }
        });

        /*
         * the command executed when the shortcut "Ctrl+1" is used
         */
        c1cmd = new Command() {
            @Override
            public void execute() {
                if (DataBaseUtil.isDifferentDT(c1.getValue(), manager.getSample()
                                                                     .getCollectionTime())) {
                    c1.setValue(manager.getSample().getCollectionTime());
                    i1.setResource(OpenELISResources.INSTANCE.commit());
                    i2.setResource(OpenELISResources.INSTANCE.arrowLeftImage());
                    verifiedWidgets.add(c1);
                }
            }
        };
        ctrl1Commands.put(c1, c1cmd);

        /*
         * the command executed when the shortcut "Ctrl+2" is used
         */
        c2cmd = new Command() {
            @Override
            public void execute() {
                if (DataBaseUtil.isDifferentDT(c1.getValue(), manager.getSample()
                                                                     .getCollectionTime())) {
                    manager.getSample().setCollectionTime(c1.getValue());
                    c2.setValue(c1.getValue());
                    i1.setResource(OpenELISResources.INSTANCE.commit());
                    i2.setResource(OpenELISResources.INSTANCE.arrowRightImage());
                    verifiedWidgets.add(c1);
                }
            }
        };
        ctrl2Commands.put(c1, c2cmd);

        /*
         * add screen handler for the image for match/no match
         */
        addScreenHandler(i1,
                         SecondDataEntryUtil.getMatchImageKey(SampleMeta.COLLECTION_TIME),
                         new ScreenHandler<Datetime>() {
                             public void onStateChange(StateChangeEvent event) {
                                 i1.setResource(OpenELISResources.INSTANCE.blankIcon());
                             }
                         });

        /*
         * add screen handler for the image for showing the direction in which
         * the data was copied
         */
        addScreenHandler(i2,
                         SecondDataEntryUtil.getCopyImageKey(SampleMeta.COLLECTION_TIME),
                         new ScreenHandler<Object>() {
                             public void onStateChange(StateChangeEvent event) {
                                 i2.setResource(OpenELISResources.INSTANCE.blankIcon());
                             }
                         });

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
        Label<String> l;
        final Calendar c1, c2;
        final Image i1, i2;
        HorizontalPanel h;
        final ScheduledCommand scmd;
        Command c1cmd, c2cmd;

        /*
         * create the widgets and add them to the table panel
         */
        l = SecondDataEntryUtil.getPromptLabel(Messages.get().sample_received());

        c1 = SecondDataEntryUtil.getCalendar(90, 0, 4);
        i1 = new Image();
        h = new HorizontalPanel();
        SecondDataEntryUtil.addWidgetAndImage(h, c1, i1);

        index = editableWidgets.size();
        editableWidgets.add(c1);

        i2 = new Image();
        c2 = SecondDataEntryUtil.getCalendar(90, 0, 4);

        SecondDataEntryUtil.addWidgets(row, widgetTable, l, h, i2, c2);

        /*
         * the scheduled command for setting the focus back to this widget when
         * it loses focus
         */
        scmd = SecondDataEntryUtil.getFocusCommand(c1);

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
                if (DataBaseUtil.isDifferentYM(event.getValue(), manager.getSample()
                                                                        .getReceivedDate())) {
                    i1.setResource(OpenELISResources.INSTANCE.abort());
                    if (numEdit > 1)
                        c2.setValue(manager.getSample().getReceivedDate());
                    /*
                     * set the focus back on the widget if the user pressed Tab
                     */
                    if (isFocusLostOnTab(c1))
                        Scheduler.get().scheduleDeferred(scmd);
                    verifiedWidgets.remove(c1);
                } else {
                    i1.setResource(OpenELISResources.INSTANCE.commit());
                    verifiedWidgets.add(c1);
                }
                tabIndex = -1;
            }

            public void onStateChange(StateChangeEvent event) {
                c1.setEnabled(isState(UPDATE) && canEdit);
            }

            public Widget onTab(boolean forward) {
                tabIndex = index;
                return SecondDataEntryUtil.onTab(index, forward, editableWidgets);
            }
        });

        /*
         * the command executed when the shortcut "Ctrl+1" is used
         */
        c1cmd = new Command() {
            @Override
            public void execute() {
                if (DataBaseUtil.isDifferentYM(c1.getValue(), manager.getSample().getReceivedDate())) {
                    c1.setValue(manager.getSample().getReceivedDate());
                    i1.setResource(OpenELISResources.INSTANCE.commit());
                    i2.setResource(OpenELISResources.INSTANCE.arrowLeftImage());
                    verifiedWidgets.add(c1);
                }
            }
        };
        ctrl1Commands.put(c1, c1cmd);

        /*
         * the command executed when the shortcut "Ctrl+2" is used
         */
        c2cmd = new Command() {
            @Override
            public void execute() {
                if (DataBaseUtil.isDifferentYM(c1.getValue(), manager.getSample().getReceivedDate())) {
                    manager.getSample().setReceivedDate(c1.getValue());
                    c2.setValue(c1.getValue());
                    i1.setResource(OpenELISResources.INSTANCE.commit());
                    i2.setResource(OpenELISResources.INSTANCE.arrowRightImage());
                    verifiedWidgets.add(c1);
                }
            }
        };
        ctrl2Commands.put(c1, c2cmd);

        /*
         * add screen handler for the image for match/no match
         */
        addScreenHandler(i1,
                         SecondDataEntryUtil.getMatchImageKey(SampleMeta.RECEIVED_DATE),
                         new ScreenHandler<Object>() {
                             public void onStateChange(StateChangeEvent event) {
                                 i1.setResource(OpenELISResources.INSTANCE.blankIcon());
                             }
                         });

        /*
         * add screen handler for the image for showing the direction in which
         * the data was copied
         */
        addScreenHandler(i2,
                         SecondDataEntryUtil.getCopyImageKey(SampleMeta.RECEIVED_DATE),
                         new ScreenHandler<Object>() {
                             public void onStateChange(StateChangeEvent event) {
                                 i2.setResource(OpenELISResources.INSTANCE.blankIcon());
                             }
                         });

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
        Label<String> l;
        final TextBox<String> t1, t2;
        final Image i1, i2;
        HorizontalPanel h;
        final ScheduledCommand scmd;
        Command c1cmd, c2cmd;

        /*
         * create the widgets and add them to the table panel
         */
        l = SecondDataEntryUtil.getPromptLabel(Messages.get().sample_clntRef());

        t1 = SecondDataEntryUtil.getTextBox(SecondDataEntryUtil.Type.STRING,
                                            196,
                                            TextBase.Case.LOWER,
                                            20,
                                            null);
        i1 = new Image();
        h = new HorizontalPanel();
        SecondDataEntryUtil.addWidgetAndImage(h, t1, i1);

        index = editableWidgets.size();
        editableWidgets.add(t1);

        i2 = new Image();
        t2 = SecondDataEntryUtil.getTextBox(SecondDataEntryUtil.Type.STRING,
                                            196,
                                            TextBase.Case.LOWER,
                                            20,
                                            null);

        SecondDataEntryUtil.addWidgets(row, widgetTable, l, h, i2, t2);

        /*
         * the scheduled command for setting the focus back to this widget when
         * it loses focus
         */
        scmd = SecondDataEntryUtil.getFocusCommand(t1);

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
                    i1.setResource(OpenELISResources.INSTANCE.abort());
                    if (numEdit > 1)
                        t2.setValue(manager.getSample().getClientReference());
                    /*
                     * set the focus back on the widget if the user pressed Tab
                     */
                    if (isFocusLostOnTab(t1))
                        Scheduler.get().scheduleDeferred(scmd);
                    verifiedWidgets.remove(t1);
                } else {
                    i1.setResource(OpenELISResources.INSTANCE.commit());
                    verifiedWidgets.add(t1);
                }
                tabIndex = -1;
            }

            public void onStateChange(StateChangeEvent event) {
                t1.setEnabled(isState(UPDATE) && canEdit);
            }

            public Widget onTab(boolean forward) {
                tabIndex = index;
                return SecondDataEntryUtil.onTab(index, forward, editableWidgets);
            }
        });

        /*
         * the command executed when the shortcut "Ctrl+1" is used
         */
        c1cmd = new Command() {
            @Override
            public void execute() {
                if (DataBaseUtil.isDifferent(t1.getValue(), manager.getSample()
                                                                   .getClientReference())) {
                    t1.setValue(manager.getSample().getClientReference());
                    i1.setResource(OpenELISResources.INSTANCE.commit());
                    i2.setResource(OpenELISResources.INSTANCE.arrowLeftImage());
                    verifiedWidgets.add(t1);
                }
            }
        };
        ctrl1Commands.put(t1, c1cmd);

        /*
         * the command executed when the shortcut "Ctrl+2" is used
         */
        c2cmd = new Command() {
            @Override
            public void execute() {
                if (DataBaseUtil.isDifferent(t1.getValue(), manager.getSample()
                                                                   .getClientReference())) {
                    manager.getSample().setClientReference(t1.getValue());
                    t2.setValue(t1.getValue());
                    i1.setResource(OpenELISResources.INSTANCE.commit());
                    i2.setResource(OpenELISResources.INSTANCE.arrowRightImage());
                    verifiedWidgets.add(t1);
                }
            }
        };
        ctrl2Commands.put(t1, c2cmd);

        /*
         * add screen handler for the image for match/no match
         */
        addScreenHandler(i1,
                         SecondDataEntryUtil.getMatchImageKey(SampleMeta.CLIENT_REFERENCE),
                         new ScreenHandler<Object>() {
                             public void onStateChange(StateChangeEvent event) {
                                 i1.setResource(OpenELISResources.INSTANCE.blankIcon());
                             }
                         });

        /*
         * add screen handler for the image for showing the direction in which
         * the data was copied
         */
        addScreenHandler(i2,
                         SecondDataEntryUtil.getCopyImageKey(SampleMeta.CLIENT_REFERENCE),
                         new ScreenHandler<Object>() {
                             public void onStateChange(StateChangeEvent event) {
                                 i2.setResource(OpenELISResources.INSTANCE.blankIcon());
                             }
                         });

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
     * Adds the row for sdwis pws number0 at the passed index; also adds the
     * applicable screen handlers to the widgets in the row
     */
    private void addSDWISPwsNumber0(int row) {
        final int index;
        Label<String> l;
        final TextBox<String> t1, t2;
        final Image i1, i2;
        HorizontalPanel h;
        final ScheduledCommand scmd;
        Command c1cmd, c2cmd;

        /*
         * create the widgets and add them to the table panel
         */
        l = SecondDataEntryUtil.getPromptLabel(Messages.get().pws_id());

        t1 = SecondDataEntryUtil.getTextBox(SecondDataEntryUtil.Type.STRING,
                                            75,
                                            TextBase.Case.UPPER,
                                            9,
                                            null);
        i1 = new Image();
        h = new HorizontalPanel();
        SecondDataEntryUtil.addWidgetAndImage(h, t1, i1);

        index = editableWidgets.size();
        editableWidgets.add(t1);

        i2 = new Image();
        t2 = SecondDataEntryUtil.getTextBox(SecondDataEntryUtil.Type.STRING,
                                            75,
                                            TextBase.Case.UPPER,
                                            9,
                                            null);

        SecondDataEntryUtil.addWidgets(row, widgetTable, l, h, i2, t2);

        /*
         * the scheduled command for setting the focus back to this widget when
         * it loses focus
         */
        scmd = SecondDataEntryUtil.getFocusCommand(t1);

        /*
         * add screen handler for the editable widget
         */
        addScreenHandler(t1, SampleMeta.SDWIS_PWS_NUMBER0, new ScreenHandler<String>() {
            public void onDataChange(DataChangeEvent<String> event) {
                t1.setValue(null);
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                Integer numEdit;
                String number0;

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
                number0 = event.getValue();
                if (DataBaseUtil.isDifferent(number0, manager.getSampleSDWIS().getPwsNumber0())) {
                    i1.setResource(OpenELISResources.INSTANCE.abort());
                    if (numEdit > 1)
                        t2.setValue(manager.getSampleSDWIS().getPwsNumber0());
                    /*
                     * set the focus back on the widget if the user pressed Tab
                     */
                    if (isFocusLostOnTab(t1))
                        Scheduler.get().scheduleDeferred(scmd);
                    verifiedWidgets.remove(t1);
                } else {
                    i1.setResource(OpenELISResources.INSTANCE.commit());
                    verifiedWidgets.add(t1);
                }
                tabIndex = -1;
            }

            public void onStateChange(StateChangeEvent event) {
                t1.setEnabled(isState(UPDATE) && canEdit);
            }

            public Widget onTab(boolean forward) {
                tabIndex = index;
                return SecondDataEntryUtil.onTab(index, forward, editableWidgets);
            }
        });

        /*
         * the command executed when the shortcut "Ctrl+1" is used
         */
        c1cmd = new Command() {
            @Override
            public void execute() {
                if (DataBaseUtil.isDifferent(t1.getValue(), manager.getSampleSDWIS()
                                                                   .getPwsNumber0())) {
                    t1.setValue(manager.getSampleSDWIS().getPwsNumber0());
                    i1.setResource(OpenELISResources.INSTANCE.commit());
                    i2.setResource(OpenELISResources.INSTANCE.arrowLeftImage());
                    verifiedWidgets.add(t1);
                }
            }
        };
        ctrl1Commands.put(t1, c1cmd);

        /*
         * the command executed when the shortcut "Ctrl+2" is used
         */
        c2cmd = new Command() {
            @Override
            public void execute() {
                if (DataBaseUtil.isDifferent(t1.getValue(), manager.getSampleSDWIS()
                                                                   .getPwsNumber0())) {
                    /*
                     * if the entered value is not a valid pws number0, show the
                     * user an error and clear the value
                     */
                    if ( !isValidPws(t1.getValue())) {
                        Window.alert(Messages.get()
                                             .secondDataEntry_invalidPwsException(manager.getSample()
                                                                                         .getAccessionNumber(),
                                                                                  t1.getValue()));
                        return;
                    }
                    manager.getSampleSDWIS().setPwsNumber0(t1.getValue());
                    t2.setValue(t1.getValue());
                    i1.setResource(OpenELISResources.INSTANCE.commit());
                    i2.setResource(OpenELISResources.INSTANCE.arrowRightImage());
                    verifiedWidgets.add(t1);
                }
            }
        };
        ctrl2Commands.put(t1, c2cmd);

        /*
         * add screen handler for the image for match/no match
         */
        addScreenHandler(i1,
                         SecondDataEntryUtil.getMatchImageKey(SampleMeta.SDWIS_PWS_NUMBER0),
                         new ScreenHandler<Object>() {
                             public void onStateChange(StateChangeEvent event) {
                                 i1.setResource(OpenELISResources.INSTANCE.blankIcon());
                             }
                         });

        /*
         * add screen handler for the image for showing the direction in which
         * the data was copied
         */
        addScreenHandler(i2,
                         SecondDataEntryUtil.getCopyImageKey(SampleMeta.SDWIS_PWS_NUMBER0),
                         new ScreenHandler<Object>() {
                             public void onStateChange(StateChangeEvent event) {
                                 i2.setResource(OpenELISResources.INSTANCE.blankIcon());
                             }
                         });

        /*
         * add screen handler for the widget on the right
         */
        addScreenHandler(t2,
                         SecondDataEntryUtil.getRightWidgetKey(SampleMeta.SDWIS_PWS_NUMBER0),
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
     * Adds the row for sdwis state lab id at the passed index; also adds the
     * applicable screen handlers to the widgets in the row
     */
    private void addSDWISStateLabId(int row) {
        final int index;
        Label<String> l;
        final TextBox<Integer> t1, t2;
        final Image i1, i2;
        HorizontalPanel h;
        final ScheduledCommand scmd;
        Command c1cmd, c2cmd;

        /*
         * create the widgets and add them to the table panel
         */
        l = SecondDataEntryUtil.getPromptLabel(Messages.get().sampleSDWIS_stateLabNo());

        t1 = SecondDataEntryUtil.getTextBox(SecondDataEntryUtil.Type.INTEGER, 109, null, null, null);
        i1 = new Image();
        h = new HorizontalPanel();
        SecondDataEntryUtil.addWidgetAndImage(h, t1, i1);

        index = editableWidgets.size();
        editableWidgets.add(t1);

        i2 = new Image();
        t2 = SecondDataEntryUtil.getTextBox(SecondDataEntryUtil.Type.INTEGER, 109, null, null, null);

        SecondDataEntryUtil.addWidgets(row, widgetTable, l, h, i2, t2);

        /*
         * the scheduled command for setting the focus back to this widget when
         * it loses focus
         */
        scmd = SecondDataEntryUtil.getFocusCommand(t1);

        /*
         * add screen handler for the editable widget
         */
        addScreenHandler(t1, SampleMeta.SDWIS_STATE_LAB_ID, new ScreenHandler<Integer>() {
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
                if (DataBaseUtil.isDifferent(event.getValue(), manager.getSampleSDWIS()
                                                                      .getStateLabId())) {
                    i1.setResource(OpenELISResources.INSTANCE.abort());
                    if (numEdit > 1)
                        t2.setValue(manager.getSampleSDWIS().getStateLabId());
                    /*
                     * set the focus back on the widget if the user pressed Tab
                     */
                    if (isFocusLostOnTab(t1))
                        Scheduler.get().scheduleDeferred(scmd);
                    verifiedWidgets.remove(t1);
                } else {
                    i1.setResource(OpenELISResources.INSTANCE.commit());
                    verifiedWidgets.add(t1);
                }
                tabIndex = -1;
            }

            public void onStateChange(StateChangeEvent event) {
                t1.setEnabled(isState(UPDATE) && canEdit);
            }

            public Widget onTab(boolean forward) {
                tabIndex = index;
                return SecondDataEntryUtil.onTab(index, forward, editableWidgets);
            }
        });

        /*
         * the command executed when the shortcut "Ctrl+1" is used
         */
        c1cmd = new Command() {
            @Override
            public void execute() {
                if (DataBaseUtil.isDifferent(t1.getValue(), manager.getSampleSDWIS()
                                                                   .getStateLabId())) {
                    t1.setValue(manager.getSampleSDWIS().getStateLabId());
                    i1.setResource(OpenELISResources.INSTANCE.commit());
                    i2.setResource(OpenELISResources.INSTANCE.arrowLeftImage());
                    verifiedWidgets.add(t1);
                }
            }
        };
        ctrl1Commands.put(t1, c1cmd);

        /*
         * the command executed when the shortcut "Ctrl+2" is used
         */
        c2cmd = new Command() {
            @Override
            public void execute() {
                if (DataBaseUtil.isDifferent(t1.getValue(), manager.getSampleSDWIS()
                                                                   .getStateLabId())) {
                    manager.getSampleSDWIS().setStateLabId(t1.getValue());
                    t2.setValue(t1.getValue());
                    i1.setResource(OpenELISResources.INSTANCE.commit());
                    i2.setResource(OpenELISResources.INSTANCE.arrowRightImage());
                    verifiedWidgets.add(t1);
                }
            }
        };
        ctrl2Commands.put(t1, c2cmd);

        /*
         * add screen handler for the image for match/no match
         */
        addScreenHandler(i1,
                         SecondDataEntryUtil.getMatchImageKey(SampleMeta.SDWIS_STATE_LAB_ID),
                         new ScreenHandler<Object>() {
                             public void onStateChange(StateChangeEvent event) {
                                 i1.setResource(OpenELISResources.INSTANCE.blankIcon());
                             }
                         });
        /*
         * add screen handler for the image for showing the direction in which
         * the data was copied
         */
        addScreenHandler(i2,
                         SecondDataEntryUtil.getCopyImageKey(SampleMeta.SDWIS_STATE_LAB_ID),
                         new ScreenHandler<Object>() {
                             public void onStateChange(StateChangeEvent event) {
                                 i2.setResource(OpenELISResources.INSTANCE.blankIcon());
                             }
                         });

        /*
         * add screen handler for the widget on the right
         */
        addScreenHandler(t2,
                         SecondDataEntryUtil.getRightWidgetKey(SampleMeta.SDWIS_STATE_LAB_ID),
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
     * Adds the row for sdwis facility id at the passed index; also adds the
     * applicable screen handlers to the widgets in the row
     */
    private void addSDWISFacilityId(int row) {
        final int index;
        Label<String> l;
        final TextBox<String> t1, t2;
        final Image i1, i2;
        HorizontalPanel h;
        final ScheduledCommand scmd;
        Command c1cmd, c2cmd;

        /*
         * create the widgets and add them to the table panel
         */
        l = SecondDataEntryUtil.getPromptLabel(Messages.get().sampleSDWIS_facilityId());

        t1 = SecondDataEntryUtil.getTextBox(SecondDataEntryUtil.Type.STRING,
                                            75,
                                            TextBase.Case.UPPER,
                                            12,
                                            null);
        i1 = new Image();
        h = new HorizontalPanel();
        SecondDataEntryUtil.addWidgetAndImage(h, t1, i1);

        index = editableWidgets.size();
        editableWidgets.add(t1);

        i2 = new Image();
        t2 = SecondDataEntryUtil.getTextBox(SecondDataEntryUtil.Type.STRING,
                                            75,
                                            TextBase.Case.UPPER,
                                            12,
                                            null);

        SecondDataEntryUtil.addWidgets(row, widgetTable, l, h, i2, t2);

        /*
         * the scheduled command for setting the focus back to this widget when
         * it loses focus
         */
        scmd = SecondDataEntryUtil.getFocusCommand(t1);

        /*
         * add screen handler for the editable widget
         */
        addScreenHandler(t1, SampleMeta.SDWIS_FACILITY_ID, new ScreenHandler<String>() {
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
                if (DataBaseUtil.isDifferent(event.getValue(), manager.getSampleSDWIS()
                                                                      .getFacilityId())) {
                    i1.setResource(OpenELISResources.INSTANCE.abort());
                    if (numEdit > 1)
                        t2.setValue(manager.getSampleSDWIS().getFacilityId());
                    /*
                     * set the focus back on the widget if the user pressed Tab
                     */
                    if (isFocusLostOnTab(t1))
                        Scheduler.get().scheduleDeferred(scmd);
                    verifiedWidgets.remove(t1);
                } else {
                    i1.setResource(OpenELISResources.INSTANCE.commit());
                    verifiedWidgets.add(t1);
                }
                tabIndex = -1;
            }

            public void onStateChange(StateChangeEvent event) {
                t1.setEnabled(isState(UPDATE) && canEdit);
            }

            public Widget onTab(boolean forward) {
                tabIndex = index;
                return SecondDataEntryUtil.onTab(index, forward, editableWidgets);
            }
        });

        /*
         * the command executed when the shortcut "Ctrl+1" is used
         */
        c1cmd = new Command() {
            @Override
            public void execute() {
                if (DataBaseUtil.isDifferent(t1.getValue(), manager.getSampleSDWIS()
                                                                   .getFacilityId())) {
                    t1.setValue(manager.getSampleSDWIS().getFacilityId());
                    i1.setResource(OpenELISResources.INSTANCE.commit());
                    i2.setResource(OpenELISResources.INSTANCE.arrowLeftImage());
                    verifiedWidgets.add(t1);
                }
            }
        };
        ctrl1Commands.put(t1, c1cmd);

        /*
         * the command executed when the shortcut "Ctrl+2" is used
         */
        c2cmd = new Command() {
            @Override
            public void execute() {
                if (DataBaseUtil.isDifferent(t1.getValue(), manager.getSampleSDWIS()
                                                                   .getFacilityId())) {
                    manager.getSampleSDWIS().setFacilityId(t1.getValue());
                    t2.setValue(t1.getValue());
                    i1.setResource(OpenELISResources.INSTANCE.commit());
                    i2.setResource(OpenELISResources.INSTANCE.arrowRightImage());
                    verifiedWidgets.add(t1);
                }
            }
        };
        ctrl2Commands.put(t1, c2cmd);

        /*
         * add screen handler for the image for match/no match
         */
        addScreenHandler(i1,
                         SecondDataEntryUtil.getMatchImageKey(SampleMeta.SDWIS_FACILITY_ID),
                         new ScreenHandler<Object>() {
                             public void onStateChange(StateChangeEvent event) {
                                 i1.setResource(OpenELISResources.INSTANCE.blankIcon());
                             }
                         });

        /*
         * add screen handler for the image for showing the direction in which
         * the data was copied
         */
        addScreenHandler(i2,
                         SecondDataEntryUtil.getCopyImageKey(SampleMeta.SDWIS_FACILITY_ID),
                         new ScreenHandler<Object>() {
                             public void onStateChange(StateChangeEvent event) {
                                 i2.setResource(OpenELISResources.INSTANCE.blankIcon());
                             }
                         });

        /*
         * add screen handler for the widget on the right
         */
        addScreenHandler(t2,
                         SecondDataEntryUtil.getRightWidgetKey(SampleMeta.SDWIS_FACILITY_ID),
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
     * Adds the row for sdwis sample type id at the passed index; also adds the
     * applicable screen handlers to the widgets in the row
     */
    private void addSDWISSampleTypeId(int row) {
        final int index;
        Label<String> l;
        final Dropdown<Integer> d1, d2;
        final Image i1, i2;
        HorizontalPanel h;
        final ScheduledCommand scmd;
        Command c1cmd, c2cmd;
        ArrayList<Item> model;

        /*
         * create the widgets and add them to the table panel
         */
        l = SecondDataEntryUtil.getPromptLabel(Messages.get().gen_sampleType());

        model = SecondDataEntryUtil.getDictionaryModel("sdwis_sample_type",
                                                       CategoryMeta.getDictionaryId());
        d1 = SecondDataEntryUtil.getDropdown(SecondDataEntryUtil.Type.INTEGER, 120, null, model);
        i1 = new Image();
        h = new HorizontalPanel();
        SecondDataEntryUtil.addWidgetAndImage(h, d1, i1);

        index = editableWidgets.size();
        editableWidgets.add(d1);

        i2 = new Image();
        d2 = SecondDataEntryUtil.getDropdown(SecondDataEntryUtil.Type.INTEGER, 120, null, model);

        SecondDataEntryUtil.addWidgets(row, widgetTable, l, h, i2, d2);

        /*
         * the scheduled command for setting the focus back to this widget when
         * it loses focus
         */
        scmd = SecondDataEntryUtil.getFocusCommand(d1);

        /*
         * add screen handler for the editable widget
         */
        addScreenHandler(d1, SampleMeta.SDWIS_SAMPLE_TYPE_ID, new ScreenHandler<String>() {
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
                if (DataBaseUtil.isDifferent(event.getValue(), manager.getSampleSDWIS()
                                                                      .getSampleTypeId())) {
                    i1.setResource(OpenELISResources.INSTANCE.abort());
                    if (numEdit > 1)
                        d2.setValue(manager.getSampleSDWIS().getSampleTypeId());
                    /*
                     * set the focus back on the widget if the user pressed Tab
                     */
                    if (isFocusLostOnTab(d1))
                        Scheduler.get().scheduleDeferred(scmd);
                    verifiedWidgets.remove(d1);
                } else {
                    i1.setResource(OpenELISResources.INSTANCE.commit());
                    verifiedWidgets.add(d1);
                }
                tabIndex = -1;
            }

            public void onStateChange(StateChangeEvent event) {
                d1.setEnabled(isState(UPDATE) && canEdit);
            }

            public Widget onTab(boolean forward) {
                tabIndex = index;
                return SecondDataEntryUtil.onTab(index, forward, editableWidgets);
            }
        });

        /*
         * the command executed when the shortcut "Ctrl+1" is used
         */
        c1cmd = new Command() {
            @Override
            public void execute() {
                if (DataBaseUtil.isDifferent(d1.getValue(), manager.getSampleSDWIS()
                                                                   .getSampleTypeId())) {
                    d1.setValue(manager.getSampleSDWIS().getSampleTypeId());
                    i1.setResource(OpenELISResources.INSTANCE.commit());
                    i2.setResource(OpenELISResources.INSTANCE.arrowLeftImage());
                    verifiedWidgets.add(d1);
                }
            }
        };
        ctrl1Commands.put(d1, c1cmd);

        /*
         * the command executed when the shortcut "Ctrl+2" is used
         */
        c2cmd = new Command() {
            @Override
            public void execute() {
                if (DataBaseUtil.isDifferent(d1.getValue(), manager.getSampleSDWIS()
                                                                   .getSampleTypeId())) {
                    manager.getSampleSDWIS().setSampleTypeId(d1.getValue());
                    d2.setValue(d1.getValue());
                    i1.setResource(OpenELISResources.INSTANCE.commit());
                    i2.setResource(OpenELISResources.INSTANCE.arrowRightImage());
                    verifiedWidgets.add(d1);
                }
            }
        };
        ctrl2Commands.put(d1, c2cmd);

        /*
         * add screen handler for the image for match/no match
         */
        addScreenHandler(i1,
                         SecondDataEntryUtil.getMatchImageKey(SampleMeta.SDWIS_SAMPLE_TYPE_ID),
                         new ScreenHandler<Object>() {
                             public void onStateChange(StateChangeEvent event) {
                                 i1.setResource(OpenELISResources.INSTANCE.blankIcon());
                             }
                         });

        /*
         * add screen handler for the image for showing the direction in which
         * the data was copied
         */
        addScreenHandler(i2,
                         SecondDataEntryUtil.getCopyImageKey(SampleMeta.SDWIS_SAMPLE_TYPE_ID),
                         new ScreenHandler<Object>() {
                             public void onStateChange(StateChangeEvent event) {
                                 i2.setResource(OpenELISResources.INSTANCE.blankIcon());
                             }
                         });

        /*
         * add screen handler for the widget on the right
         */
        addScreenHandler(d2,
                         SecondDataEntryUtil.getRightWidgetKey(SampleMeta.SDWIS_SAMPLE_TYPE_ID),
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
     * Adds the row for sdwis category id at the passed index; also adds the
     * applicable screen handlers to the widgets in the row
     */
    private void addSDWISSampleCategoryId(int row) {
        final int index;
        Label<String> l;
        final Dropdown<Integer> d1, d2;
        final Image i1, i2;
        HorizontalPanel h;
        final ScheduledCommand scmd;
        Command c1cmd, c2cmd;
        ArrayList<Item> model;

        /*
         * create the widgets and add them to the table panel
         */
        l = SecondDataEntryUtil.getPromptLabel(Messages.get().sampleSDWIS_sampleCat());

        model = SecondDataEntryUtil.getDictionaryModel("sdwis_sample_category",
                                                       CategoryMeta.getDictionaryId());
        d1 = SecondDataEntryUtil.getDropdown(SecondDataEntryUtil.Type.INTEGER, 109, null, model);
        i1 = new Image();
        h = new HorizontalPanel();
        SecondDataEntryUtil.addWidgetAndImage(h, d1, i1);

        index = editableWidgets.size();
        editableWidgets.add(d1);

        d2 = SecondDataEntryUtil.getDropdown(SecondDataEntryUtil.Type.INTEGER, 109, null, model);

        i2 = new Image();
        SecondDataEntryUtil.addWidgets(row, widgetTable, l, h, i2, d2);

        /*
         * the scheduled command for setting the focus back to this widget when
         * it loses focus
         */
        scmd = SecondDataEntryUtil.getFocusCommand(d1);

        /*
         * add screen handler for the editable widget
         */
        addScreenHandler(d1, SampleMeta.SDWIS_SAMPLE_CATEGORY_ID, new ScreenHandler<String>() {
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
                if (DataBaseUtil.isDifferent(event.getValue(), manager.getSampleSDWIS()
                                                                      .getSampleCategoryId())) {
                    i1.setResource(OpenELISResources.INSTANCE.abort());
                    if (numEdit > 1)
                        d2.setValue(manager.getSampleSDWIS().getSampleCategoryId());
                    /*
                     * set the focus back on the widget if the user pressed Tab
                     */
                    if (isFocusLostOnTab(d1))
                        Scheduler.get().scheduleDeferred(scmd);
                    verifiedWidgets.remove(d1);
                } else {
                    i1.setResource(OpenELISResources.INSTANCE.commit());
                    verifiedWidgets.add(d1);
                }
                tabIndex = -1;
            }

            public void onStateChange(StateChangeEvent event) {
                d1.setEnabled(isState(UPDATE) && canEdit);
            }

            public Widget onTab(boolean forward) {
                tabIndex = index;
                return SecondDataEntryUtil.onTab(index, forward, editableWidgets);
            }
        });

        /*
         * the command executed when the shortcut "Ctrl+1" is used
         */
        c1cmd = new Command() {
            @Override
            public void execute() {
                if (DataBaseUtil.isDifferent(d1.getValue(), manager.getSampleSDWIS()
                                                                   .getSampleCategoryId())) {
                    d1.setValue(manager.getSampleSDWIS().getSampleCategoryId());
                    i1.setResource(OpenELISResources.INSTANCE.commit());
                    i2.setResource(OpenELISResources.INSTANCE.arrowLeftImage());
                    verifiedWidgets.add(d1);
                }
            }
        };
        ctrl1Commands.put(d1, c1cmd);

        /*
         * the command executed when the shortcut "Ctrl+2" is used
         */
        c2cmd = new Command() {
            @Override
            public void execute() {
                if (DataBaseUtil.isDifferent(d1.getValue(), manager.getSampleSDWIS()
                                                                   .getSampleCategoryId())) {
                    manager.getSampleSDWIS().setSampleCategoryId(d1.getValue());
                    d2.setValue(d1.getValue());
                    i1.setResource(OpenELISResources.INSTANCE.commit());
                    i2.setResource(OpenELISResources.INSTANCE.arrowRightImage());
                    verifiedWidgets.add(d1);
                }
            }
        };
        ctrl2Commands.put(d1, c2cmd);

        /*
         * add screen handler for the image for match/no match
         */
        addScreenHandler(i1,
                         SecondDataEntryUtil.getMatchImageKey(SampleMeta.SDWIS_SAMPLE_CATEGORY_ID),
                         new ScreenHandler<Object>() {
                             public void onStateChange(StateChangeEvent event) {
                                 i1.setResource(OpenELISResources.INSTANCE.blankIcon());
                             }
                         });

        /*
         * add screen handler for the image for showing the direction in which
         * the data was copied
         */
        addScreenHandler(i2,
                         SecondDataEntryUtil.getCopyImageKey(SampleMeta.SDWIS_SAMPLE_CATEGORY_ID),
                         new ScreenHandler<Object>() {
                             public void onStateChange(StateChangeEvent event) {
                                 i2.setResource(OpenELISResources.INSTANCE.blankIcon());
                             }
                         });

        /*
         * add screen handler for the widget on the right
         */
        addScreenHandler(d2,
                         SecondDataEntryUtil.getRightWidgetKey(SampleMeta.SDWIS_SAMPLE_CATEGORY_ID),
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
     * Adds the row for sdwis sample point id at the passed index; also adds the
     * applicable screen handlers to the widgets in the row
     */
    private void addSDWISSamplePointId(int row) {
        final int index;
        Label<String> l;
        final TextBox<String> t1, t2;
        final Image i1, i2;
        HorizontalPanel h;
        final ScheduledCommand scmd;
        Command c1cmd, c2cmd;

        /*
         * create the widgets and add them to the table panel
         */
        l = SecondDataEntryUtil.getPromptLabel(Messages.get().sampleSDWIS_samplePtId());

        t1 = SecondDataEntryUtil.getTextBox(SecondDataEntryUtil.Type.STRING,
                                            75,
                                            TextBase.Case.UPPER,
                                            11,
                                            null);
        i1 = new Image();
        h = new HorizontalPanel();
        SecondDataEntryUtil.addWidgetAndImage(h, t1, i1);

        index = editableWidgets.size();
        editableWidgets.add(t1);

        i2 = new Image();
        t2 = SecondDataEntryUtil.getTextBox(SecondDataEntryUtil.Type.STRING,
                                            75,
                                            TextBase.Case.UPPER,
                                            11,
                                            null);

        SecondDataEntryUtil.addWidgets(row, widgetTable, l, h, i2, t2);

        /*
         * the scheduled command for setting the focus back to this widget when
         * it loses focus
         */
        scmd = SecondDataEntryUtil.getFocusCommand(t1);

        /*
         * add screen handler for the editable widget
         */
        addScreenHandler(t1, SampleMeta.SDWIS_SAMPLE_POINT_ID, new ScreenHandler<String>() {
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
                if (DataBaseUtil.isDifferent(event.getValue(), manager.getSampleSDWIS()
                                                                      .getSamplePointId())) {
                    i1.setResource(OpenELISResources.INSTANCE.abort());
                    if (numEdit > 1)
                        t2.setValue(manager.getSampleSDWIS().getSamplePointId());
                    /*
                     * set the focus back on the widget if the user pressed Tab
                     */
                    if (isFocusLostOnTab(t1))
                        Scheduler.get().scheduleDeferred(scmd);
                    verifiedWidgets.remove(t1);
                } else {
                    i1.setResource(OpenELISResources.INSTANCE.commit());
                    verifiedWidgets.add(t1);
                }
                tabIndex = -1;
            }

            public void onStateChange(StateChangeEvent event) {
                t1.setEnabled(isState(UPDATE) && canEdit);
            }

            public Widget onTab(boolean forward) {
                tabIndex = index;
                return SecondDataEntryUtil.onTab(index, forward, editableWidgets);
            }
        });

        /*
         * the command executed when the shortcut "Ctrl+1" is used
         */
        c1cmd = new Command() {
            @Override
            public void execute() {
                if (DataBaseUtil.isDifferent(t1.getValue(), manager.getSampleSDWIS()
                                                                   .getSamplePointId())) {
                    t1.setValue(manager.getSampleSDWIS().getSamplePointId());
                    i1.setResource(OpenELISResources.INSTANCE.commit());
                    i2.setResource(OpenELISResources.INSTANCE.arrowLeftImage());
                    verifiedWidgets.add(t1);
                }
            }
        };
        ctrl1Commands.put(t1, c1cmd);

        /*
         * the command executed when the shortcut "Ctrl+2" is used
         */
        c2cmd = new Command() {
            @Override
            public void execute() {
                if (DataBaseUtil.isDifferent(t1.getValue(), manager.getSampleSDWIS()
                                                                   .getSamplePointId())) {
                    manager.getSampleSDWIS().setSamplePointId(t1.getValue());
                    t2.setValue(t1.getValue());
                    i1.setResource(OpenELISResources.INSTANCE.commit());
                    i2.setResource(OpenELISResources.INSTANCE.arrowRightImage());
                    verifiedWidgets.add(t1);
                }
            }
        };
        ctrl2Commands.put(t1, c2cmd);

        /*
         * add screen handler for the image for match/no match
         */
        addScreenHandler(i1,
                         SecondDataEntryUtil.getMatchImageKey(SampleMeta.SDWIS_SAMPLE_POINT_ID),
                         new ScreenHandler<Object>() {
                             public void onStateChange(StateChangeEvent event) {
                                 i1.setResource(OpenELISResources.INSTANCE.blankIcon());
                             }
                         });

        /*
         * add screen handler for the image for showing the direction in which
         * the data was copied
         */
        addScreenHandler(i2,
                         SecondDataEntryUtil.getCopyImageKey(SampleMeta.SDWIS_SAMPLE_POINT_ID),
                         new ScreenHandler<Object>() {
                             public void onStateChange(StateChangeEvent event) {
                                 i2.setResource(OpenELISResources.INSTANCE.blankIcon());
                             }
                         });

        /*
         * add screen handler for the widget on the right
         */
        addScreenHandler(t2,
                         SecondDataEntryUtil.getRightWidgetKey(SampleMeta.SDWIS_SAMPLE_POINT_ID),
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
     * Adds the row for sdwis location at the passed index; also adds the
     * applicable screen handlers to the widgets in the row
     */
    private void addSDWISLocation(int row) {
        final int index;
        Label<String> l;
        final TextBox<String> t1, t2;
        final Image i1, i2;
        HorizontalPanel h;
        final ScheduledCommand scmd;
        Command c1cmd, c2cmd;

        /*
         * create the widgets and add them to the table panel
         */
        l = SecondDataEntryUtil.getPromptLabel(Messages.get().gen_location());

        t1 = SecondDataEntryUtil.getTextBox(SecondDataEntryUtil.Type.STRING,
                                            215,
                                            TextBase.Case.LOWER,
                                            40,
                                            null);
        i1 = new Image();
        h = new HorizontalPanel();
        SecondDataEntryUtil.addWidgetAndImage(h, t1, i1);

        index = editableWidgets.size();
        editableWidgets.add(t1);

        i2 = new Image();
        t2 = SecondDataEntryUtil.getTextBox(SecondDataEntryUtil.Type.STRING,
                                            215,
                                            TextBase.Case.LOWER,
                                            40,
                                            null);

        SecondDataEntryUtil.addWidgets(row, widgetTable, l, h, i2, t2);

        /*
         * the scheduled command for setting the focus back to this widget when
         * it loses focus
         */
        scmd = SecondDataEntryUtil.getFocusCommand(t1);

        /*
         * add screen handler for the editable widget
         */
        addScreenHandler(t1, SampleMeta.SDWIS_LOCATION, new ScreenHandler<String>() {
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
                if (DataBaseUtil.isDifferent(event.getValue(), manager.getSampleSDWIS()
                                                                      .getLocation())) {
                    i1.setResource(OpenELISResources.INSTANCE.abort());
                    if (numEdit > 1)
                        t2.setValue(manager.getSampleSDWIS().getLocation());
                    /*
                     * set the focus back on the widget if the user pressed Tab
                     */
                    if (isFocusLostOnTab(t1))
                        Scheduler.get().scheduleDeferred(scmd);
                    verifiedWidgets.remove(t1);
                } else {
                    i1.setResource(OpenELISResources.INSTANCE.commit());
                    verifiedWidgets.add(t1);
                }
                tabIndex = -1;
            }

            public void onStateChange(StateChangeEvent event) {
                t1.setEnabled(isState(UPDATE) && canEdit);
            }

            public Widget onTab(boolean forward) {
                tabIndex = index;
                return SecondDataEntryUtil.onTab(index, forward, editableWidgets);
            }
        });

        /*
         * the command executed when the shortcut "Ctrl+1" is used
         */
        c1cmd = new Command() {
            @Override
            public void execute() {
                if (DataBaseUtil.isDifferent(t1.getValue(), manager.getSampleSDWIS().getLocation())) {
                    t1.setValue(manager.getSampleSDWIS().getLocation());
                    i1.setResource(OpenELISResources.INSTANCE.commit());
                    i2.setResource(OpenELISResources.INSTANCE.arrowLeftImage());
                    verifiedWidgets.add(t1);
                }
            }
        };
        ctrl1Commands.put(t1, c1cmd);

        /*
         * the command executed when the shortcut "Ctrl+2" is used
         */
        c2cmd = new Command() {
            @Override
            public void execute() {
                if (DataBaseUtil.isDifferent(t1.getValue(), manager.getSampleSDWIS().getLocation())) {
                    manager.getSampleSDWIS().setLocation(t1.getValue());
                    t2.setValue(t1.getValue());
                    i1.setResource(OpenELISResources.INSTANCE.commit());
                    i2.setResource(OpenELISResources.INSTANCE.arrowRightImage());
                    verifiedWidgets.add(t1);
                }
            }
        };
        ctrl2Commands.put(t1, c2cmd);

        /*
         * add screen handler for the image for match/no match
         */
        addScreenHandler(i1,
                         SecondDataEntryUtil.getMatchImageKey(SampleMeta.SDWIS_LOCATION),
                         new ScreenHandler<Object>() {
                             public void onStateChange(StateChangeEvent event) {
                                 i1.setResource(OpenELISResources.INSTANCE.blankIcon());
                             }
                         });

        /*
         * add screen handler for the image for showing the direction in which
         * the data was copied
         */
        addScreenHandler(i2,
                         SecondDataEntryUtil.getCopyImageKey(SampleMeta.SDWIS_LOCATION),
                         new ScreenHandler<Object>() {
                             public void onStateChange(StateChangeEvent event) {
                                 i2.setResource(OpenELISResources.INSTANCE.blankIcon());
                             }
                         });

        /*
         * add screen handler for the widget on the right
         */
        addScreenHandler(t2,
                         SecondDataEntryUtil.getRightWidgetKey(SampleMeta.SDWIS_LOCATION),
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
     * Adds the row for sdwis location at the passed index; also adds the
     * applicable screen handlers to the widgets in the row
     */
    private void addSDWISPriority(int row) {
        final int index;
        Label<String> l1;
        final TextBox<Integer> t1, t2;
        final Image i1, i2;
        HorizontalPanel h;
        final ScheduledCommand scmd;
        Command c1cmd, c2cmd;

        /*
         * create the widgets and add them to the table panel
         */
        l1 = SecondDataEntryUtil.getPromptLabel(Messages.get().gen_priority());

        t1 = SecondDataEntryUtil.getTextBox(SecondDataEntryUtil.Type.INTEGER, 24, null, null, null);
        i1 = new Image();
        h = new HorizontalPanel();
        SecondDataEntryUtil.addWidgetAndImage(h, t1, i1);

        index = editableWidgets.size();
        editableWidgets.add(t1);

        i2 = new Image();
        t2 = SecondDataEntryUtil.getTextBox(SecondDataEntryUtil.Type.INTEGER, 24, null, null, null);

        SecondDataEntryUtil.addWidgets(row, widgetTable, l1, h, i2, t2);

        /*
         * the scheduled command for setting the focus back to this widget when
         * it loses focus
         */
        scmd = SecondDataEntryUtil.getFocusCommand(t1);

        /*
         * add screen handler for the editable widget
         */
        addScreenHandler(t1, SampleMeta.SDWIS_PRIORITY, new ScreenHandler<Integer>() {
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
                if (DataBaseUtil.isDifferent(event.getValue(), manager.getSampleSDWIS()
                                                                      .getPriority())) {
                    i1.setResource(OpenELISResources.INSTANCE.abort());
                    if (numEdit > 1)
                        t2.setValue(manager.getSampleSDWIS().getPriority());
                    /*
                     * set the focus back on the widget if the user pressed Tab
                     */
                    if (isFocusLostOnTab(t1))
                        Scheduler.get().scheduleDeferred(scmd);
                    verifiedWidgets.remove(t1);
                } else {
                    i1.setResource(OpenELISResources.INSTANCE.commit());
                    verifiedWidgets.add(t1);
                }
                tabIndex = -1;
            }

            public void onStateChange(StateChangeEvent event) {
                t1.setEnabled(isState(UPDATE) && canEdit);
            }

            public Widget onTab(boolean forward) {
                tabIndex = index;
                return SecondDataEntryUtil.onTab(index, forward, editableWidgets);
            }
        });

        /*
         * the command executed when the shortcut "Ctrl+1" is used
         */
        c1cmd = new Command() {
            @Override
            public void execute() {
                if (DataBaseUtil.isDifferent(t1.getValue(), manager.getSampleSDWIS().getPriority())) {
                    t1.setValue(manager.getSampleSDWIS().getPriority());
                    i1.setResource(OpenELISResources.INSTANCE.commit());
                    i2.setResource(OpenELISResources.INSTANCE.arrowLeftImage());
                    verifiedWidgets.add(t1);
                }
            }
        };
        ctrl1Commands.put(t1, c1cmd);

        /*
         * the command executed when the shortcut "Ctrl+2" is used
         */
        c2cmd = new Command() {
            @Override
            public void execute() {
                if (DataBaseUtil.isDifferent(t1.getValue(), manager.getSampleSDWIS().getPriority())) {
                    manager.getSampleSDWIS().setPriority(t1.getValue());
                    t2.setValue(t1.getValue());
                    i1.setResource(OpenELISResources.INSTANCE.commit());
                    i2.setResource(OpenELISResources.INSTANCE.arrowRightImage());
                    verifiedWidgets.add(t1);
                }
            }
        };
        ctrl2Commands.put(t1, c2cmd);

        /*
         * add screen handler for the image for match/no match
         */
        addScreenHandler(i1,
                         SecondDataEntryUtil.getMatchImageKey(SampleMeta.SDWIS_PRIORITY),
                         new ScreenHandler<Object>() {
                             public void onStateChange(StateChangeEvent event) {
                                 i1.setResource(OpenELISResources.INSTANCE.blankIcon());
                             }
                         });

        /*
         * add screen handler for the image for showing the direction in which
         * the data was copied
         */
        addScreenHandler(i2,
                         SecondDataEntryUtil.getCopyImageKey(SampleMeta.SDWIS_PRIORITY),
                         new ScreenHandler<Object>() {
                             public void onStateChange(StateChangeEvent event) {
                                 i2.setResource(OpenELISResources.INSTANCE.blankIcon());
                             }
                         });

        /*
         * add screen handler for the widget on the right
         */
        addScreenHandler(t2,
                         SecondDataEntryUtil.getRightWidgetKey(SampleMeta.SDWIS_PRIORITY),
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
     * Adds the row for sdwis collector at the passed index; also adds the
     * applicable screen handlers to the widgets in the row
     */
    private void addSDWISCollector(int row) {
        final int index;
        Label<String> l;
        final TextBox<String> t1, t2;
        final Image i1, i2;
        HorizontalPanel h;
        final ScheduledCommand scmd;
        Command c1cmd, c2cmd;

        /*
         * create the widgets and add them to the table panel
         */
        l = SecondDataEntryUtil.getPromptLabel(Messages.get().sample_collector());

        t1 = SecondDataEntryUtil.getTextBox(SecondDataEntryUtil.Type.STRING,
                                            162,
                                            TextBase.Case.LOWER,
                                            20,
                                            null);
        i1 = new Image();
        h = new HorizontalPanel();
        SecondDataEntryUtil.addWidgetAndImage(h, t1, i1);

        index = editableWidgets.size();
        editableWidgets.add(t1);

        i2 = new Image();
        t2 = SecondDataEntryUtil.getTextBox(SecondDataEntryUtil.Type.STRING,
                                            162,
                                            TextBase.Case.LOWER,
                                            20,
                                            null);

        SecondDataEntryUtil.addWidgets(row, widgetTable, l, h, i2, t2);

        /*
         * the scheduled command for setting the focus back to this widget when
         * it loses focus
         */
        scmd = SecondDataEntryUtil.getFocusCommand(t1);

        /*
         * add screen handler for the editable widget
         */
        addScreenHandler(t1, SampleMeta.SDWIS_COLLECTOR, new ScreenHandler<String>() {
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
                if (DataBaseUtil.isDifferent(event.getValue(), manager.getSampleSDWIS()
                                                                      .getCollector())) {
                    i1.setResource(OpenELISResources.INSTANCE.abort());
                    if (numEdit > 1)
                        t2.setValue(manager.getSampleEnvironmental().getCollector());
                    /*
                     * set the focus back on the widget if the user pressed Tab
                     */
                    if (isFocusLostOnTab(t1))
                        Scheduler.get().scheduleDeferred(scmd);
                    verifiedWidgets.remove(t1);
                } else {
                    i1.setResource(OpenELISResources.INSTANCE.commit());
                    verifiedWidgets.remove(t1);
                }
                tabIndex = -1;
            }

            public void onStateChange(StateChangeEvent event) {
                t1.setEnabled(isState(UPDATE) && canEdit);
            }

            public Widget onTab(boolean forward) {
                tabIndex = index;
                return SecondDataEntryUtil.onTab(index, forward, editableWidgets);
            }
        });

        /*
         * the command executed when the shortcut "Ctrl+1" is used
         */
        c1cmd = new Command() {
            @Override
            public void execute() {
                if (DataBaseUtil.isDifferent(t1.getValue(), manager.getSampleSDWIS().getCollector())) {
                    t1.setValue(manager.getSampleSDWIS().getCollector());
                    i1.setResource(OpenELISResources.INSTANCE.commit());
                    i2.setResource(OpenELISResources.INSTANCE.arrowLeftImage());
                    verifiedWidgets.add(t1);
                }
            }
        };
        ctrl1Commands.put(t1, c1cmd);

        /*
         * the command executed when the shortcut "Ctrl+2" is used
         */
        c2cmd = new Command() {
            @Override
            public void execute() {
                if (DataBaseUtil.isDifferent(t1.getValue(), manager.getSampleSDWIS().getCollector())) {
                    manager.getSampleSDWIS().setCollector(t1.getValue());
                    t2.setValue(t1.getValue());
                    i1.setResource(OpenELISResources.INSTANCE.commit());
                    i2.setResource(OpenELISResources.INSTANCE.arrowRightImage());
                    verifiedWidgets.add(t1);
                }
            }
        };
        ctrl2Commands.put(t1, c2cmd);

        /*
         * add screen handler for the image for match/no match
         */
        addScreenHandler(i1,
                         SecondDataEntryUtil.getMatchImageKey(SampleMeta.SDWIS_COLLECTOR),
                         new ScreenHandler<Object>() {
                             public void onStateChange(StateChangeEvent event) {
                                 i1.setResource(OpenELISResources.INSTANCE.blankIcon());
                             }
                         });

        /*
         * add screen handler for the image for showing the direction in which
         * the data was copied
         */
        addScreenHandler(i2,
                         SecondDataEntryUtil.getCopyImageKey(SampleMeta.SDWIS_COLLECTOR),
                         new ScreenHandler<Object>() {
                             public void onStateChange(StateChangeEvent event) {
                                 i2.setResource(OpenELISResources.INSTANCE.blankIcon());
                             }
                         });

        /*
         * add screen handler for the widget on the right
         */
        addScreenHandler(t2,
                         SecondDataEntryUtil.getRightWidgetKey(SampleMeta.SDWIS_COLLECTOR),
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
     * Adds the row for the report-to organization at the passed index
     */
    private void addReportTo(int row) {
        final int index;
        Label<String> l;
        final AutoComplete a1, a2;
        final Image i1, i2;
        HorizontalPanel h;
        final ScheduledCommand scmd;
        Command c1cmd, c2cmd;

        /*
         * create the widgets and add them to the table panel
         */
        l = SecondDataEntryUtil.getPromptLabel(Messages.get().sampleOrganization_reportTo());
        a1 = SecondDataEntryUtil.getOrganizationAutocomplete(180,
                                                             TextBase.Case.UPPER,
                                                             565,
                                                             parentScreen);
        i1 = new Image();
        h = new HorizontalPanel();
        SecondDataEntryUtil.addWidgetAndImage(h, a1, i1);

        index = editableWidgets.size();
        editableWidgets.add(a1);

        i2 = new Image();
        a2 = SecondDataEntryUtil.getOrganizationAutocomplete(180,
                                                             TextBase.Case.UPPER,
                                                             565,
                                                             parentScreen);

        SecondDataEntryUtil.addWidgets(row, widgetTable, l, h, i2, a2);

        /*
         * the scheduled command for setting the focus back to this widget when
         * it loses focus
         */
        scmd = SecondDataEntryUtil.getFocusCommand(a1);

        /*
         * add screen handler for the editable widget
         */
        addScreenHandler(a1, REPORT_TO, new ScreenHandler<AutoCompleteValue>() {
            public void onDataChange(DataChangeEvent<AutoCompleteValue> event) {
                a1.setValue(null, "");
            }

            public void onValueChange(ValueChangeEvent<AutoCompleteValue> event) {
                Integer numEdit;
                SampleOrganizationViewDO sorg;

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
                sorg = getSampleOrganization(Constants.dictionary().ORG_REPORT_TO);
                if (isDifferentOrganization(event.getValue(), sorg)) {
                    i1.setResource(OpenELISResources.INSTANCE.abort());
                    if (numEdit > 1)
                        a2.setValue(sorg != null ? sorg.getOrganizationId() : null,
                                    sorg != null ? sorg.getOrganizationName() : null);
                    /*
                     * set the focus back on the widget if the user pressed Tab
                     */
                    if (isFocusLostOnTab(a1))
                        Scheduler.get().scheduleDeferred(scmd);
                    verifiedWidgets.remove(a1);
                } else {
                    i1.setResource(OpenELISResources.INSTANCE.commit());
                    verifiedWidgets.add(a1);
                }
                tabIndex = -1;
            }

            public void onStateChange(StateChangeEvent event) {
                a1.setEnabled(isState(UPDATE) && canEdit);
            }

            public Widget onTab(boolean forward) {
                tabIndex = index;
                return SecondDataEntryUtil.onTab(index, forward, editableWidgets);
            }
        });

        /*
         * the command executed when the shortcut "Ctrl+1" is used
         */
        c1cmd = new Command() {
            @Override
            public void execute() {
                SampleOrganizationViewDO sorg;

                sorg = getSampleOrganization(Constants.dictionary().ORG_REPORT_TO);
                if (isDifferentOrganization(a1.getValue(), sorg)) {
                    a1.setValue(sorg != null ? sorg.getOrganizationId() : null,
                                sorg != null ? sorg.getOrganizationName() : null);
                    i1.setResource(OpenELISResources.INSTANCE.commit());
                    i2.setResource(OpenELISResources.INSTANCE.arrowLeftImage());
                    verifiedWidgets.add(a1);
                }
            }
        };
        ctrl1Commands.put(a1, c1cmd);

        /*
         * the command executed when the shortcut "Ctrl+2" is used
         */
        c2cmd = new Command() {
            @Override
            public void execute() {
                SampleOrganizationViewDO sorg;
                OrganizationDO org;
                AutoCompleteValue val;

                sorg = getSampleOrganization(Constants.dictionary().ORG_REPORT_TO);
                val = a1.getValue();
                if (isDifferentOrganization(val, sorg)) {
                    org = val != null ? (OrganizationDO)val.getData() : null;
                    changeOrganization(Constants.dictionary().ORG_REPORT_TO, org);
                    a2.setValue(org != null ? org.getId() : null, org != null ? org.getName()
                                                                             : null);
                    i1.setResource(OpenELISResources.INSTANCE.commit());
                    i2.setResource(OpenELISResources.INSTANCE.arrowRightImage());
                    verifiedWidgets.add(a1);
                }
            }
        };
        ctrl2Commands.put(a1, c2cmd);

        /*
         * add screen handler for the image for match/no match
         */
        addScreenHandler(i1,
                         SecondDataEntryUtil.getMatchImageKey(REPORT_TO),
                         new ScreenHandler<Object>() {
                             public void onStateChange(StateChangeEvent event) {
                                 i1.setResource(OpenELISResources.INSTANCE.blankIcon());
                             }
                         });

        /*
         * add screen handler for the image for showing the direction in which
         * the data was copied
         */
        addScreenHandler(i2,
                         SecondDataEntryUtil.getCopyImageKey(REPORT_TO),
                         new ScreenHandler<Object>() {
                             public void onStateChange(StateChangeEvent event) {
                                 i2.setResource(OpenELISResources.INSTANCE.blankIcon());
                             }
                         });

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
        Label<String> l;
        final AutoComplete a1, a2;
        final Image i1, i2;
        HorizontalPanel h;
        final ScheduledCommand scmd;
        Command c1cmd, c2cmd;

        l = SecondDataEntryUtil.getPromptLabel(Messages.get().sampleOrganization_billTo());
        a1 = SecondDataEntryUtil.getOrganizationAutocomplete(180,
                                                             TextBase.Case.UPPER,
                                                             565,
                                                             parentScreen);
        i1 = new Image();
        h = new HorizontalPanel();
        SecondDataEntryUtil.addWidgetAndImage(h, a1, i1);

        index = editableWidgets.size();
        editableWidgets.add(a1);

        i2 = new Image();
        a2 = SecondDataEntryUtil.getOrganizationAutocomplete(180,
                                                             TextBase.Case.UPPER,
                                                             565,
                                                             parentScreen);

        SecondDataEntryUtil.addWidgets(row, widgetTable, l, h, i2, a2);

        /*
         * the scheduled command for setting the focus back to this widget when
         * it loses focus
         */
        scmd = SecondDataEntryUtil.getFocusCommand(a1);

        /*
         * add screen handler for the editable widget
         */
        addScreenHandler(a1, BILL_TO, new ScreenHandler<AutoCompleteValue>() {
            public void onDataChange(DataChangeEvent<AutoCompleteValue> event) {
                a1.setValue(null, "");
            }

            public void onValueChange(ValueChangeEvent<AutoCompleteValue> event) {
                Integer numEdit;
                SampleOrganizationViewDO sorg;

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
                sorg = getSampleOrganization(Constants.dictionary().ORG_BILL_TO);
                if (isDifferentOrganization(event.getValue(), sorg)) {
                    i1.setResource(OpenELISResources.INSTANCE.abort());
                    if (numEdit > 1)
                        a2.setValue(sorg != null ? sorg.getOrganizationId() : null,
                                    sorg != null ? sorg.getOrganizationName() : null);
                    /*
                     * set the focus back on the widget if the user pressed Tab
                     */
                    if (isFocusLostOnTab(a1))
                        Scheduler.get().scheduleDeferred(scmd);
                    verifiedWidgets.remove(a1);
                } else {
                    i1.setResource(OpenELISResources.INSTANCE.commit());
                    verifiedWidgets.remove(a1);
                }
                tabIndex = -1;
            }

            public void onStateChange(StateChangeEvent event) {
                a1.setEnabled(isState(UPDATE) && canEdit);
            }

            public Widget onTab(boolean forward) {
                tabIndex = index;
                return SecondDataEntryUtil.onTab(index, forward, editableWidgets);
            }
        });

        /*
         * the command executed when the shortcut "Ctrl+1" is used
         */
        c1cmd = new Command() {
            @Override
            public void execute() {
                SampleOrganizationViewDO sorg;

                sorg = getSampleOrganization(Constants.dictionary().ORG_BILL_TO);
                if (isDifferentOrganization(a1.getValue(), sorg)) {
                    a1.setValue(sorg != null ? sorg.getOrganizationId() : null,
                                sorg != null ? sorg.getOrganizationName() : null);
                    i1.setResource(OpenELISResources.INSTANCE.commit());
                    i2.setResource(OpenELISResources.INSTANCE.arrowLeftImage());
                    verifiedWidgets.add(a1);
                }
            }
        };
        ctrl1Commands.put(a1, c1cmd);

        /*
         * the command executed when the shortcut "Ctrl+2" is used
         */
        c2cmd = new Command() {
            @Override
            public void execute() {
                SampleOrganizationViewDO sorg;
                OrganizationDO org;
                AutoCompleteValue val;

                /*
                 * change the bill-to organization in the manager to the one
                 * entered by the user if the two are different; also refresh
                 * the autocomplete on the right to show the copied value; mark
                 * the field as verified
                 */
                sorg = getSampleOrganization(Constants.dictionary().ORG_BILL_TO);
                val = a1.getValue();
                if (isDifferentOrganization(val, sorg)) {
                    org = val != null ? (OrganizationDO)val.getData() : null;
                    changeOrganization(Constants.dictionary().ORG_BILL_TO, org);
                    a2.setValue(org != null ? org.getId() : null, org != null ? org.getName()
                                                                             : null);
                    i1.setResource(OpenELISResources.INSTANCE.commit());
                    i2.setResource(OpenELISResources.INSTANCE.arrowRightImage());
                    verifiedWidgets.add(a1);
                }
            }
        };
        ctrl2Commands.put(a1, c2cmd);

        /*
         * add screen handler for the image for match/no match
         */
        addScreenHandler(i1,
                         SecondDataEntryUtil.getMatchImageKey(BILL_TO),
                         new ScreenHandler<Object>() {
                             public void onStateChange(StateChangeEvent event) {
                                 i1.setResource(OpenELISResources.INSTANCE.blankIcon());
                             }
                         });

        /*
         * add screen handler for the image for showing the direction in which
         * the data was copied
         */
        addScreenHandler(i2,
                         SecondDataEntryUtil.getCopyImageKey(BILL_TO),
                         new ScreenHandler<Object>() {
                             public void onStateChange(StateChangeEvent event) {
                                 i2.setResource(OpenELISResources.INSTANCE.blankIcon());
                             }
                         });

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
        Label<String> l;
        final AutoComplete a1, a2;
        final Image i1, i2;
        HorizontalPanel h;
        final ScheduledCommand scmd;
        Command c1cmd, c2cmd;

        l = SecondDataEntryUtil.getPromptLabel(Messages.get().project_project());
        a1 = SecondDataEntryUtil.getProjectAutocomplete(180, TextBase.Case.LOWER, 440, parentScreen);
        i1 = new Image();
        h = new HorizontalPanel();
        SecondDataEntryUtil.addWidgetAndImage(h, a1, i1);

        index = editableWidgets.size();
        editableWidgets.add(a1);

        i2 = new Image();
        a2 = SecondDataEntryUtil.getProjectAutocomplete(180, TextBase.Case.LOWER, 440, parentScreen);

        SecondDataEntryUtil.addWidgets(row, widgetTable, l, h, i2, a2);

        /*
         * the scheduled command for setting the focus back to this widget when
         * it loses focus
         */
        scmd = SecondDataEntryUtil.getFocusCommand(a1);

        /*
         * add screen handler for the editable widget
         */
        addScreenHandler(a1, SampleMeta.PROJECT_NAME, new ScreenHandler<AutoCompleteValue>() {
            public void onDataChange(DataChangeEvent<AutoCompleteValue> event) {
                a1.setValue(null, "");
            }

            public void onValueChange(ValueChangeEvent<AutoCompleteValue> event) {
                Integer numEdit;
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
                sproj = getFirstProject(manager);
                if (isDifferentProject(event.getValue(), sproj)) {
                    i1.setResource(OpenELISResources.INSTANCE.abort());
                    if (numEdit > 1)
                        a2.setValue(sproj != null ? sproj.getProjectId() : null,
                                    sproj != null ? sproj.getProjectName() : null);
                    /*
                     * set the focus back on the widget if the user pressed Tab
                     */
                    if (isFocusLostOnTab(a1))
                        Scheduler.get().scheduleDeferred(scmd);
                    verifiedWidgets.remove(a1);
                } else {
                    i1.setResource(OpenELISResources.INSTANCE.commit());
                    verifiedWidgets.add(a1);
                }
                tabIndex = -1;

            }

            public void onStateChange(StateChangeEvent event) {
                a1.setEnabled(isState(UPDATE) && canEdit);
            }

            public Widget onTab(boolean forward) {
                tabIndex = index;
                return SecondDataEntryUtil.onTab(index, forward, editableWidgets);
            }
        });

        /*
         * the command executed when the shortcut "Ctrl+1" is used
         */
        c1cmd = new Command() {
            @Override
            public void execute() {
                SampleProjectViewDO sproj;

                sproj = getFirstProject(manager);
                if (isDifferentProject(a1.getValue(), sproj)) {
                    a1.setValue(sproj != null ? sproj.getProjectId() : null,
                                sproj != null ? sproj.getProjectName() : null);
                    i1.setResource(OpenELISResources.INSTANCE.commit());
                    i2.setResource(OpenELISResources.INSTANCE.arrowLeftImage());
                    verifiedWidgets.add(a1);
                }
            }
        };
        ctrl1Commands.put(a1, c1cmd);

        /*
         * the command executed when the shortcut "Ctrl+2" is used
         */
        c2cmd = new Command() {
            @Override
            public void execute() {
                SampleProjectViewDO sproj;
                ProjectDO proj;
                AutoCompleteValue val;

                sproj = getFirstProject(manager);
                val = a1.getValue();
                if (isDifferentProject(val, sproj)) {
                    proj = val != null ? (ProjectDO)val.getData() : null;
                    changeProject(proj);
                    a2.setValue(proj != null ? proj.getId() : null, proj != null ? proj.getName()
                                                                                : null);
                    i1.setResource(OpenELISResources.INSTANCE.commit());
                    i2.setResource(OpenELISResources.INSTANCE.arrowRightImage());
                    verifiedWidgets.add(a1);
                }
            }
        };
        ctrl2Commands.put(a1, c2cmd);

        /*
         * add screen handler for the image for match/no match
         */
        addScreenHandler(i1,
                         SecondDataEntryUtil.getMatchImageKey(SampleMeta.PROJECT_NAME),
                         new ScreenHandler<Object>() {
                             public void onStateChange(StateChangeEvent event) {
                                 i1.setResource(OpenELISResources.INSTANCE.blankIcon());
                             }
                         });

        /*
         * add screen handler for the image for showing the direction in which
         * the data was copied
         */
        addScreenHandler(i2,
                         SecondDataEntryUtil.getCopyImageKey(SampleMeta.PROJECT_NAME),
                         new ScreenHandler<Object>() {
                             public void onStateChange(StateChangeEvent event) {
                                 i2.setResource(OpenELISResources.INSTANCE.blankIcon());
                             }
                         });

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
        return tabIndex != -1 && widget == editableWidgets.get(tabIndex);
    }

    /**
     * Tries to fetch an order by "orderId" if it's not null; if an order
     * couldn't be fetched, which means that "orderId" is not the id of a valid
     * order, or if the order isn't a send-out order, returns false; returns
     * true if "orderId" is null because that's a valid value for orderId of a
     * sample
     */
    private boolean isValidSendOutOrder(Integer orderId) {
        IOrderManager1 om;

        if (orderId == null)
            return true;

        om = orderManagers.get(orderId);
        if (om == null) {
            try {
                om = OrderService1.get().fetchById(orderId);
                orderManagers.put(orderId, om);
            } catch (Exception e) {
                Window.alert(e.getMessage());
                logger.log(Level.SEVERE, e.getMessage(), e);
                return false;
            }
        }

        return om != null && Constants.iorder().SEND_OUT.equals(om.getIorder().getType());
    }

    /**
     * Tries to fetch a pws record by "number0" if it's not empty; if a pws
     * record couldn't be fetched, which means that "number0" is not the number0
     * of a valid pws record, returns false; returns true if "number0" is empty
     * because the validation in the back-end will prevent the sample from being
     * committed
     */
    private boolean isValidPws(String number0) {
        PWSDO data;

        if (DataBaseUtil.isEmpty(number0))
            return true;

        data = pwsDOs.get(number0);
        if (data == null) {
            try {
                data = PWSService.get().fetchPwsByNumber0(number0);
                pwsDOs.put(number0, data);
            } catch (NotFoundException e) {
                return false;
            } catch (Exception e) {
                Window.alert(e.getMessage());
                logger.log(Level.SEVERE, e.getMessage(), e);
                return false;
            }
        }
        return true;
    }

    /**
     * Returns true if the id of the passed value is different from the
     * organization id of the passed sample organization; returns false
     * otherwise
     */
    private boolean isDifferentOrganization(AutoCompleteValue val, SampleOrganizationViewDO org) {
        return DataBaseUtil.isDifferent(val != null ? val.getId() : null,
                                        org != null ? org.getOrganizationId() : null);
    }

    /**
     * Adds or updates the sample organization of the specified type if the
     * passed organization is not null, otherwise deletes the sample
     * organization of this type. Also refreshes the display of the autocomplete
     * showing the organization of this type.
     */
    private void changeOrganization(Integer type, OrganizationDO org) {
        SampleOrganizationViewDO data;

        data = getSampleOrganization(type);
        if (org == null) {
            /*
             * this method is called only when the organization changes and if
             * there isn't a sample organization of this type selected
             * currently, then there must have been before, thus it needs to be
             * removed from the manager
             */
            manager.organization.remove(data);
        } else {
            if (data == null) {
                /*
                 * an organization was selected by the user but there isn't one
                 * present in the manager, thus it needs to be added
                 */
                data = manager.organization.add(org);
                data.setTypeId(type);
            } else {
                /*
                 * the organization was changed, so the sample organization
                 * needs to be updated
                 */
                data.setOrganizationId(org.getId());
                data.setOrganizationName(org.getName());
                data.setOrganizationMultipleUnit(org.getAddress().getMultipleUnit());
                data.setOrganizationStreetAddress(org.getAddress().getStreetAddress());
                data.setOrganizationCity(org.getAddress().getCity());
                data.setOrganizationState(org.getAddress().getState());
                data.setOrganizationZipCode(org.getAddress().getZipCode());
                data.setOrganizationCountry(org.getAddress().getCountry());
            }

            /*
             * warn the user if samples from this organization are to held or
             * refused
             */
            try {
                if (SampleOrganizationUtility1.isHoldRefuseSampleForOrg(org.getId()))
                    Window.alert(Messages.get().gen_orgMarkedAsHoldRefuseSample(org.getName()));
            } catch (Exception e) {
                Window.alert(e.getMessage());
                logger.log(Level.SEVERE, e.getMessage(), e);
            }
        }
    }

    /**
     * returns the organization of the specified type from the manager or null
     * if the manager is null or doesn't have an organization of this type
     */
    private SampleOrganizationViewDO getSampleOrganization(Integer type) {
        ArrayList<SampleOrganizationViewDO> sorgs;

        sorgs = manager.organization.getByType(type);
        if (sorgs != null && sorgs.size() > 0)
            return sorgs.get(0);

        return null;
    }

    /**
     * Returns true if the id of the passed value is different from the project
     * id of the passed sample project; returns false otherwise
     */
    private boolean isDifferentProject(AutoCompleteValue val, SampleProjectViewDO sproj) {
        return DataBaseUtil.isDifferent(val != null ? val.getId() : null,
                                        sproj != null ? sproj.getProjectId() : null);
    }

    /**
     * Adds or updates the first project of the sample if the argument is not
     * null, otherwise deletes the first project. Also refreshes the display of
     * the autocomplete.
     */
    private void changeProject(ProjectDO proj) {
        SampleProjectViewDO data;

        data = getFirstProject(manager);
        if (proj == null) {
            /*
             * if a project was not selected and if there were projects present
             * then the first project is deleted and the next project is set as
             * the first one
             */
            if (data != null) {
                manager.project.remove(data);
                data = getFirstProject(manager);
            }
        } else {
            /*
             * otherwise the first project is modified or a new one is created
             * if no project existed
             */
            if (data == null)
                data = manager.project.add();

            data.setProjectId(proj.getId());
            data.setProjectName(proj.getName());
            data.setProjectDescription(proj.getDescription());
        }
    }

    /**
     * Returns the first project from the manager, or null if the manager
     * doesn't have any projects
     */
    private SampleProjectViewDO getFirstProject(SampleManager1 sm) {
        return sm.project.count() > 0 ? sm.project.get(0) : null;
    }
}