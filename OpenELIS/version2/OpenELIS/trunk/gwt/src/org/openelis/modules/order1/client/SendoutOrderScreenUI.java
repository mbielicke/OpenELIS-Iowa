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
package org.openelis.modules.order1.client;

import static org.openelis.modules.main.client.Logger.*;
import static org.openelis.ui.screen.Screen.ShortKeys.*;
import static org.openelis.ui.screen.State.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;

import org.openelis.cache.CacheProvider;
import org.openelis.cache.CategoryCache;
import org.openelis.cache.DictionaryCache;
import org.openelis.cache.UserCache;
import org.openelis.constants.Messages;
import org.openelis.domain.AuxDataViewDO;
import org.openelis.domain.Constants;
import org.openelis.domain.DictionaryDO;
import org.openelis.domain.IdNameVO;
import org.openelis.domain.IOrderContainerDO;
import org.openelis.domain.IOrderItemViewDO;
import org.openelis.domain.IOrderOrganizationViewDO;
import org.openelis.domain.IOrderReturnVO;
import org.openelis.domain.IOrderTestViewDO;
import org.openelis.domain.OrganizationDO;
import org.openelis.domain.ShippingViewDO;
import org.openelis.gwt.widget.ScreenWindow;
import org.openelis.manager.AuxFieldGroupManager;
import org.openelis.manager.IOrderManager1;
import org.openelis.manager.ShippingManager;
import org.openelis.manager.TestManager;
import org.openelis.meta.IOrderMeta;
import org.openelis.modules.auxData.client.AddAuxGroupEvent;
import org.openelis.modules.auxData.client.AuxDataTabUI;
import org.openelis.modules.auxData.client.RemoveAuxGroupEvent;
import org.openelis.modules.auxiliary.client.AuxiliaryService;
import org.openelis.modules.history.client.HistoryScreen;
import org.openelis.modules.main.client.OpenELIS;
import org.openelis.modules.organization1.client.OrganizationService1Impl;
import org.openelis.modules.report.client.RequestFormReportService;
import org.openelis.modules.sample1.client.SampleOrganizationUtility1;
import org.openelis.modules.shipping.client.ShippingScreen;
import org.openelis.modules.shipping.client.ShippingService;
import org.openelis.modules.test.client.TestService;
import org.openelis.ui.common.Caution;
import org.openelis.ui.common.Datetime;
import org.openelis.ui.common.ModulePermission;
import org.openelis.ui.common.PermissionException;
import org.openelis.ui.common.ReportStatus;
import org.openelis.ui.common.ValidationErrorsList;
import org.openelis.ui.common.Warning;
import org.openelis.ui.common.data.Query;
import org.openelis.ui.common.data.QueryData;
import org.openelis.ui.event.BeforeCloseEvent;
import org.openelis.ui.event.BeforeCloseHandler;
import org.openelis.ui.event.DataChangeEvent;
import org.openelis.ui.event.GetMatchesEvent;
import org.openelis.ui.event.GetMatchesHandler;
import org.openelis.ui.event.StateChangeEvent;
import org.openelis.ui.screen.AsyncCallbackUI;
import org.openelis.ui.screen.Screen;
import org.openelis.ui.screen.ScreenHandler;
import org.openelis.ui.screen.ScreenNavigator;
import org.openelis.ui.screen.State;
import org.openelis.ui.widget.AutoComplete;
import org.openelis.ui.widget.AutoCompleteValue;
import org.openelis.ui.widget.Button;
import org.openelis.ui.widget.ButtonGroup;
import org.openelis.ui.widget.Dropdown;
import org.openelis.ui.widget.Item;
import org.openelis.ui.widget.Menu;
import org.openelis.ui.widget.MenuItem;
import org.openelis.ui.widget.QueryFieldUtil;
import org.openelis.ui.widget.TabLayoutPanel;
import org.openelis.ui.widget.TextBox;
import org.openelis.ui.widget.WindowInt;
import org.openelis.ui.widget.calendar.Calendar;
import org.openelis.ui.widget.table.Table;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.http.client.URL;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.SyncCallback;
import com.google.gwt.user.client.ui.Widget;

public class SendoutOrderScreenUI extends Screen implements CacheProvider {

    @UiTemplate("SendoutOrder.ui.xml")
    interface SendoutOrderUiBinder extends UiBinder<Widget, SendoutOrderScreenUI> {
    };

    public static final SendoutOrderUiBinder uiBinder = GWT.create(SendoutOrderUiBinder.class);

    protected IOrderManager1                  manager;

    protected ModulePermission               userPermission;

    protected ScreenNavigator<IdNameVO>      nav;

    @UiField
    protected ButtonGroup                    atozButtons;

    @UiField
    protected Table                          atozTable;

    @UiField
    protected Button                         query, previous, next, add, update, commit, abort,
                    optionsButton, atozNext, atozPrev;

    @UiField
    protected Menu                           optionsMenu, historyMenu;

    @UiField
    protected MenuItem                       duplicate, shippingInfo, orderRequestForm,
                    orderHistory, orderOrganizationHistory, orderItemHistory, orderTestHistory,
                    orderContainerHistory;

    @UiField
    protected TextBox<Integer>               id, neededDays, numberOfForms;

    @UiField
    protected TextBox<String>                organizationAttention, multipleUnit, requestedBy,
                    streetAddress, city, zipCode;

    @UiField
    protected Calendar                       orderedDate;

    @UiField
    protected Dropdown<Integer>              status, shipFrom, costCenter;

    @UiField
    protected Dropdown<String>               orgState;

    @UiField
    protected AutoComplete                   shipTo, description;

    @UiField
    protected TabLayoutPanel                 tabPanel;

    @UiField(provided = true)
    protected OrganizationTabUI              organizationTab;

    @UiField(provided = true)
    protected AuxDataTabUI                   auxDataTab;

    @UiField(provided = true)
    protected TestTabUI                      testTab;

    @UiField(provided = true)
    protected ContainerTabUI                 containerTab;

    @UiField(provided = true)
    protected SendoutOrderItemTabUI          itemTab;

    @UiField(provided = true)
    protected ShippingNotesTabUI             shippingNotesTab;

    @UiField(provided = true)
    protected CustomerNotesTabUI             customerNotesTab;

    @UiField(provided = true)
    protected InternalNotesTabUI             internalNotesTab;

    @UiField(provided = true)
    protected SampleNotesTabUI               sampleNotesTab;

    @UiField(provided = true)
    protected RecurrenceTabUI                recurrenceTab;

    @UiField(provided = true)
    protected SendoutOrderFillTabUI          fillTab;

    protected SendoutOrderScreenUI           screen;

    private ShippingManager                  shippingManager;

    private ShippingScreen                   shippingScreen;

    protected String                         descQuery;

    protected HashMap<String, Object>        cache;

    protected AsyncCallbackUI<ArrayList<IdNameVO>> queryCall;

    protected AsyncCallbackUI<IOrderManager1>           addCall, fetchForUpdateCall, 
                                                         updateCall, fetchByIdCall, unlockCall;   
    
    protected AsyncCallbackUI<IOrderReturnVO>   duplicateCall;

    // @formatter:off
    protected IOrderManager1.Load             elements[] = {
                                                            IOrderManager1.Load.SAMPLE_DATA,
                                                            IOrderManager1.Load.ORGANIZATION,
                                                            IOrderManager1.Load.ITEMS,
                                                            IOrderManager1.Load.RECURRENCE
                                                    };
    // @formatter:on

    public SendoutOrderScreenUI(WindowInt window) throws Exception {
        setWindow(window);

        userPermission = UserCache.getPermission().getModule("sendoutorder");
        if (userPermission == null)
            throw new PermissionException(Messages.get()
                                                  .gen_screenPermException("Send-out Order Screen"));

        try {
            CategoryCache.getBySystemNames("order_status",
                                           "cost_centers",
                                           "laboratory_location",
                                           "state",
                                           "organization_type",
                                           "country",
                                           "sample_container",
                                           "type_of_sample",
                                           "inventory_store",
                                           "inventory_unit",
                                           "standard_note_type",
                                           "order_recurrence_unit");
        } catch (Exception e) {
            Window.alert(e.getMessage());
            logger.log(Level.SEVERE, e.getMessage(), e);
            window.close();
        }

        organizationTab = new OrganizationTabUI(this);
        testTab = new TestTabUI(this);
        containerTab = new ContainerTabUI(this);
        itemTab = new SendoutOrderItemTabUI(this);
        shippingNotesTab = new ShippingNotesTabUI(this);
        customerNotesTab = new CustomerNotesTabUI(this);
        internalNotesTab = new InternalNotesTabUI(this);
        sampleNotesTab = new SampleNotesTabUI(this);
        recurrenceTab = new RecurrenceTabUI(this);
        fillTab = new SendoutOrderFillTabUI(this);

        auxDataTab = new AuxDataTabUI(this) {
            @Override
            public boolean evaluateEdit() {
                return manager != null;
            }

            @Override
            public int count() {
                if (manager != null)
                    return manager.auxData.count();
                return 0;
            }

            @Override
            public AuxDataViewDO get(int i) {
                return manager.auxData.get(i);
            }

            @Override
            public String getAuxFieldMetaKey() {
                return IOrderMeta.getAuxDataAuxFieldId();
            }

            @Override
            public String getValueMetaKey() {
                return IOrderMeta.getAuxDataValue();
            }
        };

        initWidget(uiBinder.createAndBindUi(this));

        manager = null;

        initialize();
        setData();
        setState(DEFAULT);
        fireDataChange();

        logger.fine("Sendout Order Screen Opened");
    }

    /**
     * Setup state and data change handles for every widget on the screen
     */
    private void initialize() {
        ArrayList<Item<Integer>> model;
        ArrayList<Item<String>> smodel;
        ArrayList<DictionaryDO> list;
        Item<Integer> row;
        Item<String> srow;

        screen = this;

        //
        // button panel buttons
        //
        addStateChangeHandler(new StateChangeEvent.Handler() {
            public void onStateChange(StateChangeEvent event) {
                query.setEnabled(isState(QUERY, DEFAULT, DISPLAY) &&
                                 userPermission.hasSelectPermission());
                if (isState(QUERY)) {
                    query.lock();
                    query.setPressed(true);
                }
            }
        });

        addShortcut(query, 'q', CTRL);

        addStateChangeHandler(new StateChangeEvent.Handler() {
            public void onStateChange(StateChangeEvent event) {
                previous.setEnabled(isState(DISPLAY));
            }
        });

        addShortcut(previous, 'p', CTRL);

        addStateChangeHandler(new StateChangeEvent.Handler() {
            public void onStateChange(StateChangeEvent event) {
                next.setEnabled(isState(DISPLAY));
            }
        });

        addShortcut(next, 'n', CTRL);

        addStateChangeHandler(new StateChangeEvent.Handler() {
            public void onStateChange(StateChangeEvent event) {
                add.setEnabled(isState(ADD, DEFAULT, DISPLAY) && userPermission.hasAddPermission());
                if (isState(ADD)) {
                    add.lock();
                    add.setPressed(true);
                }
            }
        });

        addShortcut(add, 'a', CTRL);

        addStateChangeHandler(new StateChangeEvent.Handler() {
            public void onStateChange(StateChangeEvent event) {
                update.setEnabled(isState(UPDATE, DISPLAY) && userPermission.hasUpdatePermission());
                if (isState(UPDATE)) {
                    update.lock();
                    update.setPressed(true);
                }
            }
        });

        addShortcut(update, 'u', CTRL);

        addStateChangeHandler(new StateChangeEvent.Handler() {
            public void onStateChange(StateChangeEvent event) {
                commit.setEnabled(isState(QUERY, ADD, UPDATE));
            }
        });

        addShortcut(commit, 'm', CTRL);

        addStateChangeHandler(new StateChangeEvent.Handler() {
            public void onStateChange(StateChangeEvent event) {
                abort.setEnabled(isState(QUERY, ADD, UPDATE));
            }
        });

        addShortcut(abort, 'o', CTRL);

        addStateChangeHandler(new StateChangeEvent.Handler() {
            public void onStateChange(StateChangeEvent event) {
                optionsMenu.setEnabled(isState(DISPLAY));
                optionsButton.setEnabled(isState(DISPLAY));
                historyMenu.setEnabled(isState(DISPLAY));
            }
        });

        /*
         * option menu items
         */
        addStateChangeHandler(new StateChangeEvent.Handler() {
            public void onStateChange(StateChangeEvent event) {
                duplicate.setEnabled(isState(State.DISPLAY) && userPermission.hasAddPermission());
            }
        });
        duplicate.addCommand(new Command() {
            public void execute() {
                duplicate();
            }
        });

        addStateChangeHandler(new StateChangeEvent.Handler() {
            public void onStateChange(StateChangeEvent event) {
                shippingInfo.setEnabled(isState(State.DISPLAY));
            }
        });
        shippingInfo.addCommand(new Command() {
            public void execute() {
                shippingInfo();
            }
        });

        addStateChangeHandler(new StateChangeEvent.Handler() {
            public void onStateChange(StateChangeEvent event) {
                orderRequestForm.setEnabled(isState(State.DISPLAY));
            }
        });
        orderRequestForm.addCommand(new Command() {
            public void execute() {
                orderRequestForm();
            }
        });

        addStateChangeHandler(new StateChangeEvent.Handler() {
            public void onStateChange(StateChangeEvent event) {
                orderHistory.setEnabled(isState(DISPLAY));
            }
        });
        orderHistory.addCommand(new Command() {
            @Override
            public void execute() {
                orderHistory();
            }
        });

        addStateChangeHandler(new StateChangeEvent.Handler() {
            public void onStateChange(StateChangeEvent event) {
                orderOrganizationHistory.setEnabled(isState(DISPLAY));
            }
        });
        orderOrganizationHistory.addCommand(new Command() {
            @Override
            public void execute() {
                orderOrganizationHistory();
            }
        });

        addStateChangeHandler(new StateChangeEvent.Handler() {
            public void onStateChange(StateChangeEvent event) {
                orderItemHistory.setEnabled(isState(DISPLAY));
            }
        });
        orderItemHistory.addCommand(new Command() {
            @Override
            public void execute() {
                orderItemHistory();
            }
        });

        addStateChangeHandler(new StateChangeEvent.Handler() {
            public void onStateChange(StateChangeEvent event) {
                orderTestHistory.setEnabled(isState(DISPLAY));
            }
        });
        orderTestHistory.addCommand(new Command() {
            @Override
            public void execute() {
                orderTestHistory();
            }
        });

        addStateChangeHandler(new StateChangeEvent.Handler() {
            public void onStateChange(StateChangeEvent event) {
                orderContainerHistory.setEnabled(isState(DISPLAY));
            }
        });
        orderContainerHistory.addCommand(new Command() {
            @Override
            public void execute() {
                orderContainerHistory();
            }
        });

        //
        // screen fields
        //
        addScreenHandler(id, IOrderMeta.getId(), new ScreenHandler<Integer>() {
            public void onDataChange(DataChangeEvent event) {
                id.setValue(getId());
            }

            public void onStateChange(StateChangeEvent event) {
                id.setEnabled(isState(QUERY));
                id.setQueryMode(isState(QUERY));
            }

            public Widget onTab(boolean forward) {
                return forward ? neededDays : zipCode;
            }
        });

        addScreenHandler(neededDays, IOrderMeta.getNeededInDays(), new ScreenHandler<Integer>() {
            public void onDataChange(DataChangeEvent event) {
                neededDays.setValue(getNeededDays());
            }

            public void onValueChange(ValueChangeEvent<Integer> event) {
                setNeededDays(event.getValue());
            }

            public void onStateChange(StateChangeEvent event) {
                neededDays.setEnabled(isState(QUERY, ADD, UPDATE));
                neededDays.setQueryMode(isState(QUERY));
            }

            public Widget onTab(boolean forward) {
                return forward ? numberOfForms : id;
            }
        });

        addScreenHandler(numberOfForms, IOrderMeta.getNumberOfForms(), new ScreenHandler<Integer>() {
            public void onDataChange(DataChangeEvent event) {
                numberOfForms.setValue(getNumberOfForms());
            }

            public void onValueChange(ValueChangeEvent<Integer> event) {
                setNumberOfForms(event.getValue());
            }

            public void onStateChange(StateChangeEvent event) {
                numberOfForms.setEnabled(isState(QUERY, ADD, UPDATE));
                numberOfForms.setQueryMode(isState(QUERY));
            }

            public Widget onTab(boolean forward) {
                return forward ? shipFrom : neededDays;
            }
        });

        addScreenHandler(shipFrom, IOrderMeta.getShipFromId(), new ScreenHandler<Integer>() {
            public void onDataChange(DataChangeEvent event) {
                shipFrom.setValue(getShipFromId());
            }

            public void onValueChange(ValueChangeEvent<Integer> event) {
                setShipFromId(event.getValue());
            }

            public void onStateChange(StateChangeEvent event) {
                shipFrom.setEnabled(isState(QUERY, ADD, UPDATE));
                shipFrom.setQueryMode(isState(QUERY));
            }

            public Widget onTab(boolean forward) {
                return forward ? shipTo : numberOfForms;
            }
        });

        addScreenHandler(shipTo, IOrderMeta.getOrganizationName(), new ScreenHandler<Integer>() {
            public void onDataChange(DataChangeEvent event) {
                setOrganizationNameSelection();
            }

            public void onValueChange(ValueChangeEvent<Integer> event) {
                getOrganizationNameFromSelection();
            }

            public void onStateChange(StateChangeEvent event) {
                shipTo.setEnabled(isState(QUERY, ADD, UPDATE));
                shipTo.setQueryMode(isState(QUERY));
            }

            public Widget onTab(boolean forward) {
                return forward ? status : shipFrom;
            }
        });

        shipTo.addGetMatchesHandler(new GetMatchesHandler() {
            public void onGetMatches(GetMatchesEvent event) {
                getOrganizationMatches(event.getMatch());
            }
        });

        addScreenHandler(status, IOrderMeta.getStatusId(), new ScreenHandler<Integer>() {
            public void onDataChange(DataChangeEvent event) {
                status.setValue(getStatusId());
            }

            public void onValueChange(ValueChangeEvent<Integer> event) {
                setStatusId(event.getValue());
            }

            public void onStateChange(StateChangeEvent event) {
                Integer statusId;

                statusId = getStatusId();

                if (isState(QUERY)) {
                    status.setEnabled(true);
                    status.setQueryMode(true);
                } else if (isState(ADD, UPDATE)) {
                    /*
                     * If adding or updating an order, the user should only
                     * change the status if it is not processed, recurring, or
                     * cancelled.
                     */
                    status.setEnabled( !Constants.dictionary().ORDER_STATUS_PROCESSED.equals(statusId) &&
                                      !Constants.dictionary().ORDER_STATUS_RECURRING.equals(statusId) &&
                                      !Constants.dictionary().ORDER_STATUS_CANCELLED.equals(statusId));
                    status.setQueryMode(false);
                } else {
                    status.setEnabled(false);
                    status.setQueryMode(false);
                }

                if ( !status.isEnabled())
                    return;

                for (Item<Integer> item : status.getModel()) {
                    if (status.isQueryMode()) {
                        /*
                         * no options are to be disabled in Query state
                         */
                        item.setEnabled(true);
                        continue;
                    }
                    if (Constants.dictionary().ORDER_STATUS_PENDING.equals(item.getKey()) ||
                        Constants.dictionary().ORDER_STATUS_ON_HOLD.equals(item.getKey()) ||
                        Constants.dictionary().ORDER_STATUS_TEMPLATE.equals(item.getKey()))
                        item.setEnabled(true);
                    else if (Constants.dictionary().ORDER_STATUS_PROCESSED.equals(item.getKey()) ||
                             Constants.dictionary().ORDER_STATUS_CANCELLED.equals(item.getKey()))
                        /*
                         * the options for "Processed" and "Cancelled" are only
                         * enabled for an existing order and only if it is
                         * pending or on hold
                         */
                        item.setEnabled(manager.getIorder().getId() != null &&
                                        (Constants.dictionary().ORDER_STATUS_PENDING.equals(statusId) || Constants.dictionary().ORDER_STATUS_ON_HOLD.equals(statusId)));
                    else
                        item.setEnabled(false);
                }

            }

            public Widget onTab(boolean forward) {
                return forward ? organizationAttention : shipTo;
            }
        });

        addScreenHandler(organizationAttention,
                         IOrderMeta.getOrganizationAttention(),
                         new ScreenHandler<String>() {
                             public void onDataChange(DataChangeEvent event) {
                                 organizationAttention.setValue(getOrganizationAttention());
                             }

                             public void onValueChange(ValueChangeEvent<String> event) {
                                 setOrganizationAttention(event.getValue());
                             }

                             public void onStateChange(StateChangeEvent event) {
                                 organizationAttention.setEnabled(isState(QUERY, ADD, UPDATE));
                                 organizationAttention.setQueryMode(isState(QUERY));
                             }

                             public Widget onTab(boolean forward) {
                                 return forward ? orderedDate : status;
                             }
                         });

        addScreenHandler(orderedDate, IOrderMeta.getOrderedDate(), new ScreenHandler<Datetime>() {
            public void onDataChange(DataChangeEvent event) {
                orderedDate.setValue(getOrderedDate());
            }

            public void onValueChange(ValueChangeEvent<Datetime> event) {
                setOrderedDate(event.getValue());
            }

            public void onStateChange(StateChangeEvent event) {
                orderedDate.setEnabled(isState(QUERY, ADD, UPDATE));
                orderedDate.setQueryMode(isState(QUERY));
            }

            public Widget onTab(boolean forward) {
                return forward ? multipleUnit : organizationAttention;
            }
        });

        addScreenHandler(multipleUnit,
                         IOrderMeta.getIorderOrganizationOrganizationAddressMultipleUnit(),
                         new ScreenHandler<String>() {
                             public void onDataChange(DataChangeEvent event) {
                                 multipleUnit.setValue(getMultipleUnit());
                             }

                             public void onStateChange(StateChangeEvent event) {
                                 multipleUnit.setEnabled(isState(QUERY));
                                 multipleUnit.setQueryMode(isState(QUERY));
                             }

                             public Widget onTab(boolean forward) {
                                 return forward ? requestedBy : orderedDate;
                             }
                         });

        addScreenHandler(requestedBy, IOrderMeta.getRequestedBy(), new ScreenHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                requestedBy.setValue(getRequestedBy());
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                setRequestedBy(event.getValue());
            }

            public void onStateChange(StateChangeEvent event) {
                requestedBy.setEnabled(isState(QUERY, ADD, UPDATE));
                requestedBy.setQueryMode(isState(QUERY));
            }

            public Widget onTab(boolean forward) {
                return forward ? streetAddress : multipleUnit;
            }
        });

        addScreenHandler(streetAddress,
                         IOrderMeta.getOrganizationAddressStreetAddress(),
                         new ScreenHandler<String>() {
                             public void onDataChange(DataChangeEvent event) {
                                 streetAddress.setValue(getStreetAddress());
                             }

                             public void onStateChange(StateChangeEvent event) {
                                 streetAddress.setEnabled(isState(QUERY));
                                 streetAddress.setQueryMode(isState(QUERY));
                             }

                             public Widget onTab(boolean forward) {
                                 return forward ? costCenter : requestedBy;
                             }
                         });

        addScreenHandler(costCenter, IOrderMeta.getCostCenterId(), new ScreenHandler<Integer>() {
            public void onDataChange(DataChangeEvent event) {
                costCenter.setValue(getCostCenterId());
            }

            public void onValueChange(ValueChangeEvent<Integer> event) {
                setCostCenterId(event.getValue());
            }

            public void onStateChange(StateChangeEvent event) {
                costCenter.setEnabled(isState(QUERY, ADD, UPDATE));
                costCenter.setQueryMode(isState(QUERY));
            }

            public Widget onTab(boolean forward) {
                return forward ? city : streetAddress;
            }
        });

        addScreenHandler(city, IOrderMeta.getOrganizationAddressCity(), new ScreenHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                city.setValue(getCity());
            }

            public void onStateChange(StateChangeEvent event) {
                city.setEnabled(isState(QUERY));
                city.setQueryMode(isState(QUERY));
            }

            public Widget onTab(boolean forward) {
                return forward ? description : costCenter;
            }
        });

        addScreenHandler(description, IOrderMeta.getDescription(), new ScreenHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                setDescriptionSelection();
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                getDescriptionFromSelection();
            }

            public void onStateChange(StateChangeEvent event) {
                description.setEnabled(isState(QUERY, ADD, UPDATE));
                description.setQueryMode(isState(QUERY));
            }

            public Widget onTab(boolean forward) {
                return forward ? orgState : city;
            }
        });

        description.addGetMatchesHandler(new GetMatchesHandler() {
            public void onGetMatches(GetMatchesEvent event) {
                getDescriptionMatches(event.getMatch());
            }
        });

        addScreenHandler(orgState,
                         IOrderMeta.getOrganizationAddressState(),
                         new ScreenHandler<String>() {
                             public void onDataChange(DataChangeEvent event) {
                                 orgState.setValue(getState());
                             }

                             public void onStateChange(StateChangeEvent event) {
                                 orgState.setEnabled(isState(QUERY));
                                 orgState.setQueryMode(isState(QUERY));
                             }

                             public Widget onTab(boolean forward) {
                                 return forward ? zipCode : description;
                             }
                         });

        addScreenHandler(zipCode,
                         IOrderMeta.getOrganizationAddressZipCode(),
                         new ScreenHandler<String>() {
                             public void onDataChange(DataChangeEvent event) {
                                 zipCode.setValue(getZipCode());
                             }

                             public void onStateChange(StateChangeEvent event) {
                                 zipCode.setEnabled(isState(QUERY));
                                 zipCode.setQueryMode(isState(QUERY));
                             }

                             public Widget onTab(boolean forward) {
                                 return forward ? id : orgState;
                             }
                         });

        /*
         * tabs
         */
        tabPanel.setPopoutBrowser(OpenELIS.getBrowser());

        addScreenHandler(organizationTab, "organizationTab", new ScreenHandler<Object>() {
            public void onDataChange(DataChangeEvent event) {
                organizationTab.onDataChange();
            }

            public void onStateChange(StateChangeEvent event) {
                organizationTab.setState(event.getState());
            }

            public Object getQuery() {
                return organizationTab.getQueryFields();
            }
        });

        addScreenHandler(auxDataTab, "auxDataTab", new ScreenHandler<Object>() {
            public void onDataChange(DataChangeEvent event) {
                auxDataTab.onDataChange();
            }

            public void onStateChange(StateChangeEvent event) {
                auxDataTab.setState(event.getState());
            }

            public Object getQuery() {
                return auxDataTab.getQueryFields();
            }
        });

        /*
         * querying by this tab is allowed on this screen, but not on all
         * screens
         */
        auxDataTab.setCanQuery(true);

        addScreenHandler(testTab, "testTab", new ScreenHandler<Object>() {
            public void onDataChange(DataChangeEvent event) {
                testTab.onDataChange();
            }

            public void onStateChange(StateChangeEvent event) {
                testTab.setState(event.getState());
            }

            public Object getQuery() {
                return testTab.getQueryFields();
            }
        });

        addScreenHandler(containerTab, "containerTab", new ScreenHandler<Object>() {
            public void onDataChange(DataChangeEvent event) {
                containerTab.onDataChange();
            }

            public void onStateChange(StateChangeEvent event) {
                containerTab.setState(event.getState());
            }

            public Object getQuery() {
                return containerTab.getQueryFields();
            }
        });

        addScreenHandler(itemTab, "itemTab", new ScreenHandler<Object>() {
            public void onDataChange(DataChangeEvent event) {
                itemTab.onDataChange();
            }

            public void onStateChange(StateChangeEvent event) {
                itemTab.setState(event.getState());
            }

            public Object getQuery() {
                return itemTab.getQueryFields();
            }
        });

        addScreenHandler(shippingNotesTab, "shippingNotesTab", new ScreenHandler<Object>() {
            public void onDataChange(DataChangeEvent event) {
                shippingNotesTab.onDataChange();
            }

            public void onStateChange(StateChangeEvent event) {
                shippingNotesTab.setState(event.getState());
            }

            public Object getQuery() {
                return null;
            }
        });

        addScreenHandler(customerNotesTab, "customerNotesTab", new ScreenHandler<Object>() {
            public void onDataChange(DataChangeEvent event) {
                customerNotesTab.onDataChange();
            }

            public void onStateChange(StateChangeEvent event) {
                customerNotesTab.setState(event.getState());
            }

            public Object getQuery() {
                return null;
            }
        });

        addScreenHandler(internalNotesTab, "internalNotesTab", new ScreenHandler<Object>() {
            public void onDataChange(DataChangeEvent event) {
                internalNotesTab.onDataChange();
            }

            public void onStateChange(StateChangeEvent event) {
                internalNotesTab.setState(event.getState());
            }

            public Object getQuery() {
                return null;
            }
        });

        addScreenHandler(sampleNotesTab, "sampleNotesTab", new ScreenHandler<Object>() {
            public void onDataChange(DataChangeEvent event) {
                sampleNotesTab.onDataChange();
            }

            public void onStateChange(StateChangeEvent event) {
                sampleNotesTab.setState(event.getState());
            }

            public Object getQuery() {
                return null;
            }
        });

        addScreenHandler(recurrenceTab, "recurrenceTab", new ScreenHandler<Object>() {
            public void onDataChange(DataChangeEvent event) {
                recurrenceTab.onDataChange();
            }

            public void onStateChange(StateChangeEvent event) {
                recurrenceTab.setState(event.getState());
            }

            public Object getQuery() {
                return recurrenceTab.getQueryFields();
            }
        });

        addScreenHandler(fillTab, "fillTab", new ScreenHandler<Object>() {
            public void onDataChange(DataChangeEvent event) {
                fillTab.onDataChange();
            }

            public void onStateChange(StateChangeEvent event) {
                fillTab.setState(event.getState());
            }
        });

        //
        // left hand navigation panel
        //
        nav = new ScreenNavigator<IdNameVO>(atozTable, atozNext, atozPrev) {
            public void executeQuery(final Query query) {
                QueryData field;

                setBusy(Messages.get().gen_querying());

                if (queryCall == null) {
                    queryCall = new AsyncCallbackUI<ArrayList<IdNameVO>>() {
                        public void success(ArrayList<IdNameVO> result) {
                            clearStatus();
                            setQueryResult(result);
                        }

                        public void notFound() {
                            setQueryResult(null);
                            setState(DEFAULT);
                            setDone(Messages.get().gen_noRecordsFound());
                        }

                        public void lastPage() {
                            setQueryResult(null);
                            setError(Messages.get().gen_noMoreRecordInDir());
                        }

                        public void failure(Throwable error) {
                            setQueryResult(null);
                            Window.alert("Error: Send-out Order call query failed; " +
                                         error.getMessage());
                            setError(Messages.get().gen_queryFailed());
                        }
                    };
                }
                /*
                 * this screen should only query for send-out orders
                 */
                field = new QueryData(IOrderMeta.getType(),
                                      QueryData.Type.STRING,
                                      Constants.iorder().SEND_OUT);
                query.setFields(field);
                query.setRowsPerPage(25);
                OrderService1.get().query(query, queryCall);
            }

            public boolean fetch(IdNameVO entry) {
                fetchById( (entry == null) ? null : entry.getId());
                return true;
            }

            public ArrayList<Item<Integer>> getModel() {
                ArrayList<IdNameVO> result;
                ArrayList<Item<Integer>> model;
                Item<Integer> row;

                model = null;
                result = nav.getQueryResult();
                if (result != null) {
                    model = new ArrayList<Item<Integer>>();
                    for (IdNameVO entry : result) {
                        row = new Item<Integer>(2);
                        row.setKey(entry.getId());
                        row.setCell(0, entry.getId());
                        row.setCell(1, entry.getName());
                        model.add(row);
                    }
                }
                return model;
            }
        };

        addStateChangeHandler(new StateChangeEvent.Handler() {
            public void onStateChange(StateChangeEvent event) {
                boolean enable;
                enable = isState(DEFAULT, DISPLAY) && userPermission.hasSelectPermission();
                atozButtons.setEnabled(enable);
                nav.enable(enable);
            }
        });

        atozButtons.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                Query query;
                QueryData field;

                field = new QueryData();
                field.setKey(IOrderMeta.getId());
                field.setQuery( ((Button)event.getSource()).getAction());
                field.setType(QueryData.Type.INTEGER);

                query = new Query();
                query.setFields(field);
                nav.setQuery(query);
            }
        });

        bus.addHandler(AddTestEvent.getType(), new AddTestEvent.Handler() {
            public void onAddTest(AddTestEvent event) {
                switch (event.getAddType()) {
                    case TEST:
                        addTest(event.getId(), true, event.getIndex());
                        break;
                    case PANEL:
                        addTest(event.getId(), false, event.getIndex());
                        break;
                }
            }
        });

        bus.addHandler(RemoveTestEvent.getType(), new RemoveTestEvent.Handler() {
            public void onRemoveTest(RemoveTestEvent event) {
                removeTests(event.getIds());
            }
        });

        bus.addHandler(AddAuxGroupEvent.getType(), new AddAuxGroupEvent.Handler() {
            @Override
            public void onAddAuxGroup(AddAuxGroupEvent event) {
                IOrderReturnVO ret;
                ArrayList<Integer> ids;

                if (screen == event.getSource())
                    return;

                ids = event.getGroupIds();
                if (ids != null && ids.size() > 0) {
                    try {
                        ret = OrderService1.get().addAuxGroups(manager, ids);
                        manager = ret.getManager();
                        setData();
                        setState(state);
                        bus.fireEventFromSource(new AddAuxGroupEvent(ids), screen);
                        if (ret.getErrors() != null && ret.getErrors().size() > 0)
                            showErrors(ret.getErrors());
                        else
                            clearStatus();
                    } catch (Exception e) {
                        Window.alert(e.getMessage());
                        logger.log(Level.SEVERE, e.getMessage(), e);
                    }
                }
            }
        });

        bus.addHandler(RemoveAuxGroupEvent.getType(), new RemoveAuxGroupEvent.Handler() {
            @Override
            public void onRemoveAuxGroup(RemoveAuxGroupEvent event) {
                if (event.getGroupIds() != null && event.getGroupIds().size() > 0) {
                    if (screen == event.getSource())
                        return;

                    try {
                        manager = OrderService1.get().removeAuxGroups(manager, event.getGroupIds());
                        setData();
                        setState(state);
                        bus.fireEventFromSource(new RemoveAuxGroupEvent(event.getGroupIds()),
                                                screen);
                    } catch (Exception e) {
                        Window.alert(e.getMessage());
                        logger.log(Level.SEVERE, e.getMessage(), e);
                    }
                }
            }
        });

        bus.addHandler(AddRecurrenceEvent.getType(), new AddRecurrenceEvent.Handler() {
            public void onAddRecurrence(AddRecurrenceEvent event) {
                status.setEnabled( !Constants.dictionary().ORDER_STATUS_RECURRING.equals(getStatusId()));
                fireDataChange();
            }
        });

        window.addBeforeClosedHandler(new BeforeCloseHandler<WindowInt>() {
            public void onBeforeClosed(BeforeCloseEvent<WindowInt> event) {
                if (isState(ADD, UPDATE)) {
                    event.cancel();
                    setError(Messages.get().gen_mustCommitOrAbort());
                } else {
                    /*
                     * make sure that all detached tabs are closed when the main
                     * screen is closed
                     */
                    tabPanel.close();
                }
            }
        });

        // order status dropdown
        model = new ArrayList<Item<Integer>>();
        list = CategoryCache.getBySystemName("order_status");
        for (DictionaryDO d : list) {
            row = new Item<Integer>(d.getId(), d.getEntry());
            row.setEnabled("Y".equals(d.getIsActive()));
            model.add(row);
        }

        status.setModel(model);

        model = new ArrayList<Item<Integer>>();
        list = CategoryCache.getBySystemName("cost_centers");
        for (DictionaryDO d : list) {
            row = new Item<Integer>(d.getId(), d.getEntry());
            row.setEnabled("Y".equals(d.getIsActive()));
            model.add(row);
        }

        costCenter.setModel(model);

        model = new ArrayList<Item<Integer>>();
        list = CategoryCache.getBySystemName("laboratory_location");
        for (DictionaryDO d : list) {
            row = new Item<Integer>(d.getId(), d.getEntry());
            row.setEnabled("Y".equals(d.getIsActive()));
            model.add(row);
        }

        shipFrom.setModel(model);

        smodel = new ArrayList<Item<String>>();
        smodel.add(new Item<String>(null, ""));
        list = CategoryCache.getBySystemName("state");
        for (DictionaryDO d : list) {
            srow = new Item<String>(d.getEntry(), d.getEntry());
            srow.setEnabled("Y".equals(d.getIsActive()));
            smodel.add(srow);
        }

        orgState.setModel(smodel);
    }

    /*
     * basic button methods
     */
    @UiHandler("query")
    protected void query(ClickEvent event) {
        manager = null;
        /*
         * the tab for aux data uses the cache in query state
         */
        cache = new HashMap<String, Object>();
        setData();
        setState(QUERY);
        fireDataChange();
        id.setFocus(true);
        setDone(Messages.get().gen_enterFieldsToQuery());
    }

    @UiHandler("previous")
    protected void previous(ClickEvent event) {
        nav.previous();
    }

    @UiHandler("next")
    protected void next(ClickEvent event) {
        nav.next();
    }

    @UiHandler("add")
    protected void add(ClickEvent event) {
        setBusy();

        if (addCall == null) {
            addCall = new AsyncCallbackUI<IOrderManager1>() {
                public void success(IOrderManager1 result) {
                    manager = result;
                    cache = new HashMap<String, Object>();
                    setData();
                    setState(ADD);
                    fireDataChange();
                    neededDays.setFocus(true);
                    setDone(Messages.get().gen_enterInformationPressCommit());
                }

                public void failure(Throwable error) {
                    Window.alert(error.getMessage());
                    logger.log(Level.SEVERE, error.getMessage(), error);
                    clearStatus();
                }
            };
        }

        OrderService1.get().getInstance(Constants.iorder().SEND_OUT, addCall);
    }

    @UiHandler("update")
    protected void update(ClickEvent event) {
        setBusy(Messages.get().lockForUpdate());

        if (fetchForUpdateCall == null) {
            fetchForUpdateCall = new AsyncCallbackUI<IOrderManager1>() {
                public void success(IOrderManager1 result) {
                    manager = result;
                    if (Constants.dictionary().ORDER_STATUS_CANCELLED.equals(manager.getIorder()
                                                                                    .getStatusId())) {
                        Window.alert(Messages.get().order_cancelledOrderCantBeUpdated());
                        try {
                            manager = OrderService1.get().unlock(manager.getIorder().getId(),
                                                                 IOrderManager1.Load.SAMPLE_DATA,
                                                                 IOrderManager1.Load.ORGANIZATION,
                                                                 IOrderManager1.Load.ITEMS,
                                                                 IOrderManager1.Load.RECURRENCE);
                        } catch (Exception e) {
                            Window.alert(e.getMessage());
                            logger.log(Level.SEVERE, e.getMessage() != null ? e.getMessage()
                                                                           : "null", e);
                        }
                        setData();
                        setState(DISPLAY);
                        fireDataChange();
                    } else {
                        try {
                            buildCache();
                        } catch (Exception e) {
                            Window.alert(e.getMessage());
                            logger.log(Level.SEVERE, e.getMessage() != null ? e.getMessage()
                                                                           : "null", e);
                        }
                        setData();
                        setState(UPDATE);
                        fireDataChange();
                        neededDays.setFocus(true);
                    }
                }

                public void failure(Throwable e) {
                    Window.alert(e.getMessage());
                    logger.log(Level.SEVERE, e.getMessage() != null ? e.getMessage() : "null", e);
                }

                public void finish() {
                    clearStatus();
                }
            };
        }

        OrderService1.get()
                     .fetchForUpdate(manager.getIorder().getId(), elements, fetchForUpdateCall);
    }

    @UiHandler("commit")
    protected void commit(ClickEvent event) {
        commit(false);
    }

    private void commit(boolean ignoreWarning) {
        Validation validation;

        finishEditing();

        validation = validate();

        switch (validation.getStatus()) {
            case WARNINGS:
                /*
                 * show the warnings and ask the user if the data should still
                 * be committed; commit only if the user says yes
                 */
                if ( !Window.confirm(getWarnings(validation.getExceptions(), true)))
                    return;
                break;
            case FLAGGED:
                /*
                 * some part of the screen has some operation that needs to be
                 * completed before committing the data
                 */
                return;
            case ERRORS:
                setError(Messages.get().gen_correctErrors());
                return;
        }

        switch (super.state) {
            case QUERY:
                commitQuery();
                break;
            case ADD:
                commitUpdate(ignoreWarning);
                break;
            case UPDATE:
                commitUpdate(ignoreWarning);
                break;
        }
    }

    protected void commitQuery() {
        Query query;

        query = new Query();
        query.setFields(getQueryFields());
        nav.setQuery(query);
        cache = null;
    }

    protected void commitUpdate(final boolean ignoreWarning) {
        if (isState(ADD))
            setBusy(Messages.get().gen_adding());
        else
            setBusy(Messages.get().gen_updating());

        if (updateCall == null) {
            updateCall = new AsyncCallbackUI<IOrderManager1>() {
                public void success(IOrderManager1 result) {
                    manager = result;
                    setData();
                    setState(DISPLAY);
                    fireDataChange();
                    clearStatus();
                    cache = null;
                }

                public void validationErrors(ValidationErrorsList e) {
                    showErrors(e);
                    if ( !e.hasErrors() && e.hasWarnings() && !ignoreWarning) {
                        if (Window.confirm(getWarnings(e.getErrorList(), true)))
                            commitUpdate(true);
                    }
                }

                public void failure(Throwable e) {
                    if (isState(ADD))
                        Window.alert("commitAdd(): " + e.getMessage());
                    else
                        Window.alert("commitUpdate(): " + e.getMessage());
                    logger.log(Level.SEVERE, e.getMessage(), e);
                    clearStatus();
                }
            };
        }

        OrderService1.get().update(manager, ignoreWarning, updateCall);
    }

    @UiHandler("abort")
    protected void abort(ClickEvent event) {
        finishEditing();
        clearErrors();
        setBusy(Messages.get().gen_cancelChanges());

        if (isState(QUERY)) {
            try {
                manager = null;
                setData();
                setState(DEFAULT);
                fireDataChange();
                setDone(Messages.get().gen_queryAborted());
            } catch (Exception e) {
                Window.alert(e.getMessage());
                logger.log(Level.SEVERE, e.getMessage(), e);
                clearStatus();
            }
        } else if (isState(ADD)) {
            if ( !Window.confirm(Messages.get().order_abortWarning())) {
                setDone(Messages.get().gen_enterInformationPressCommit());
                return;
            }
            manager = null;
            setData();
            setState(DEFAULT);
            fireDataChange();
            setDone(Messages.get().gen_addAborted());
        } else if (isState(UPDATE)) {
            if ( !Window.confirm(Messages.get().order_abortWarning())) {
                clearStatus();
                return;
            }

            if (unlockCall == null) {
                unlockCall = new AsyncCallbackUI<IOrderManager1>() {
                    public void success(IOrderManager1 result) {
                        manager = result;
                        setData();
                        setState(DISPLAY);
                        fireDataChange();
                        setDone(Messages.get().gen_updateAborted());
                    }

                    public void failure(Throwable e) {
                        Window.alert(e.getMessage());
                        logger.log(Level.SEVERE, e.getMessage(), e);
                        clearStatus();
                    }
                };
            }

            OrderService1.get().unlock(manager.getIorder().getId(), elements, unlockCall);
        }
        cache = null;
    }

    protected void duplicate() {
        if (duplicateCall == null) {
            duplicateCall = new AsyncCallbackUI<IOrderReturnVO>() {
                public void success(IOrderReturnVO result) {
                    manager = result.getManager();
                    if (result.getErrors().hasWarnings())
                        Window.alert(getWarnings(result.getErrors().getErrorList(), false));
                    if ( !result.getErrors().hasWarnings() && result.getErrors().hasErrors())
                        showErrors(result.getErrors());
                    manager.getIorder().setParentIorderId(null);
                    /*
                     * the screen is in add state, so we need the cache here
                     */
                    try {
                        buildCache();
                    } catch (Exception ex) {
                        Window.alert(ex.getMessage());
                        logger.log(Level.SEVERE, ex.getMessage(), ex);
                    }
                    setData();
                    setState(ADD);
                    fireDataChange();
                }

                public void failure(Throwable e) {
                    Window.alert(e.getMessage());
                    logger.log(Level.SEVERE, e.getMessage(), e);
                }
            };
        }

        OrderService1.get().duplicate(manager.getIorder().getId(), duplicateCall);
    }

    protected void shippingInfo() {
        try {
            setBusy(Messages.get().gen_fetching());

            ShippingService.get().fetchByOrderId(manager.getIorder().getId(),
                                                 new SyncCallback<ShippingViewDO>() {
                                                     public void onSuccess(ShippingViewDO result) {
                                                         try {
                                                             if (result != null)
                                                                 shippingManager = ShippingManager.fetchById(result.getId());
                                                             else
                                                                 shippingManager = null;
                                                         } catch (Throwable e) {
                                                             shippingManager = null;
                                                             Window.alert(e.getMessage());
                                                             logger.log(Level.SEVERE,
                                                                        e.getMessage(),
                                                                        e);
                                                             clearStatus();
                                                         }
                                                     }

                                                     public void onFailure(Throwable error) {
                                                         shippingManager = null;
                                                         error.printStackTrace();
                                                         Window.alert("Error: Fetch failed; " +
                                                                      error.getMessage());
                                                         clearStatus();
                                                     }
                                                 });

            if (shippingManager != null)
                showShippingScreen(shippingManager);
            else
                setDone(Messages.get().gen_noRecordsFound());
        } catch (Throwable e) {
            Window.alert(e.getMessage());
            logger.log(Level.SEVERE, e.getMessage(), e);
            clearStatus();
            return;
        }
    }

    protected void orderRequestForm() {
        Query query;
        QueryData field;

        query = new Query();
        field = new QueryData();
        field.setKey("ORDER_ID");
        field.setQuery(manager.getIorder().getId().toString());
        field.setType(QueryData.Type.INTEGER);
        query.setFields(field);

        field = new QueryData();
        field.setKey("PRINTER");
        field.setQuery("-view-");
        field.setType(QueryData.Type.INTEGER);
        query.setFields(field);

        setBusy(Messages.get().gen_generatingReport());
        RequestFormReportService.get().runReport(query, new AsyncCallback<ReportStatus>() {
            public void onSuccess(ReportStatus status) {
                String url;
                if (ReportStatus.Status.SAVED.equals(status.getStatus())) {
                    url = "/openelis/openelis/report?file=" + status.getMessage();
                    Window.open(URL.encode(url), "OrderRequestFormReport", null);
                    setDone(Messages.get().gen_loadCompleteMessage());
                } else {
                    setDone(status.getMessage());
                }
            }

            public void onFailure(Throwable caught) {
                setError("Failed");
                caught.printStackTrace();
                Window.alert(caught.getMessage());
            }
        });
        // window.clearStatus();
    }

    protected void orderHistory() {
        IdNameVO hist;

        hist = new IdNameVO(manager.getIorder().getId(), manager.getIorder().getId().toString());
        HistoryScreen.showHistory(Messages.get().order_orderHistory(),
                                  Constants.table().IORDER,
                                  hist);
    }

    protected void orderOrganizationHistory() {
        int i, count;
        IdNameVO refVoList[];
        IOrderOrganizationViewDO data;

        count = manager.organization.count();
        refVoList = new IdNameVO[count];
        for (i = 0; i < count; i++ ) {
            data = manager.organization.get(i);
            refVoList[i] = new IdNameVO(data.getId(), data.getOrganizationName());
        }

        HistoryScreen.showHistory(Messages.get().order_orderOrganizationHistory(),
                                  Constants.table().IORDER_ORGANIZATION,
                                  refVoList);
    }

    protected void orderItemHistory() {
        int i, count;
        IdNameVO refVoList[];
        IOrderItemViewDO data;

        count = manager.item.count();
        refVoList = new IdNameVO[count];
        for (i = 0; i < count; i++ ) {
            data = manager.item.get(i);
            refVoList[i] = new IdNameVO(data.getId(), data.getInventoryItemName());
        }
        HistoryScreen.showHistory(Messages.get().order_orderItemHistory(),
                                  Constants.table().IORDER_ITEM,
                                  refVoList);
    }

    protected void orderTestHistory() {
        int i, count;
        IdNameVO refVoList[];
        IOrderTestViewDO data;

        count = manager.test.count();
        refVoList = new IdNameVO[count];
        for (i = 0; i < count; i++ ) {
            data = manager.test.get(i);
            refVoList[i] = new IdNameVO(data.getId(), data.getTestName());
        }
        HistoryScreen.showHistory(Messages.get().order_orderTestHistory(),
                                  Constants.table().IORDER_TEST,
                                  refVoList);
    }

    protected void orderContainerHistory() {
        int i, count;
        IdNameVO refVoList[];
        IOrderContainerDO data;
        DictionaryDO dict;

        try {
            count = manager.container.count();
            refVoList = new IdNameVO[count];
            for (i = 0; i < count; i++ ) {
                data = manager.container.get(i);
                dict = DictionaryCache.getById(data.getContainerId());
                refVoList[i] = new IdNameVO(data.getId(), dict.getEntry());
            }
            HistoryScreen.showHistory(Messages.get().order_orderContainerHistory(),
                                      Constants.table().IORDER_CONTAINER,
                                      refVoList);
        } catch (Exception e) {
            Window.alert(e.getMessage());
            logger.log(Level.SEVERE, e.getMessage(), e);
        }
    }

    /**
     * Executes a query to fetch the order whose id is the passed value
     */
    public void query(Integer id) {
        Query query;
        QueryData field;

        if (id == null)
            return;

        query = new Query();
        query.setRowsPerPage(25);
        field = new QueryData();
        field.setKey(IOrderMeta.getId());
        field.setQuery(id.toString());
        field.setType(QueryData.Type.INTEGER);

        query.setFields(field);
        nav.setQuery(query);
        cache = null;
    }

    /**
     * Returns from the cache, the object that has the specified key and is of
     * the specified class
     */
    @Override
    public <T> T get(Object key, Class<?> c) {
        String cacheKey;
        Object obj;

        if (cache == null)
            return null;

        cacheKey = null;
        if (c == TestManager.class)
            cacheKey = "tm:" + key;
        else if (c == AuxFieldGroupManager.class)
            cacheKey = "am:" + key;

        obj = cache.get(cacheKey);
        if (obj != null)
            return (T)obj;

        /*
         * if the requested object is not in the cache then obtain it and put it
         * in the cache
         */
        try {
            if (c == TestManager.class)
                obj = TestService.get().fetchById((Integer)key);
            else if (c == AuxFieldGroupManager.class)
                obj = AuxiliaryService.get().fetchById((Integer)key);

            cache.put(cacheKey, obj);
        } catch (Exception e) {
            Window.alert(e.getMessage());
            logger.log(Level.SEVERE, e.getMessage(), e);
        }
        return (T)obj;
    }

    private void setOrganizationNameSelection() {
        OrganizationDO org;

        org = getShipTo();
        if (org != null)
            shipTo.setValue(org.getId(), org.getName());
        else
            shipTo.setValue(null, "");
    }

    private void getOrganizationNameFromSelection() {
        AutoCompleteValue row;
        OrganizationDO org;

        row = shipTo.getValue();
        if (row == null || row.getId() == null) {
            /*
             * this method is called only when the ship-to changes and if there
             * isn't a ship-to selected currently, then there must have been
             * before, thus it needs to be removed from the manager
             */
            manager.getIorder().setOrganizationId(null);
            manager.getIorder().setOrganization(null);
            shipTo.setValue(null, "");
            multipleUnit.setValue(null);
            streetAddress.setValue(null);
            city.setValue(null);
            orgState.setValue(null);
            zipCode.setValue(null);
        } else {
            org = (OrganizationDO)row.getData();
            manager.getIorder().setOrganizationId(org.getId());
            manager.getIorder().setOrganization(org);
            shipTo.setValue(org.getId(), org.getName());
            multipleUnit.setValue(org.getAddress().getMultipleUnit());
            streetAddress.setValue(org.getAddress().getStreetAddress());
            city.setValue(org.getAddress().getCity());
            orgState.setValue(org.getAddress().getState());
            zipCode.setValue(org.getAddress().getZipCode());

            /*
             * warn the user if samples from this organization are to held or
             * refused
             */
            try {
                showHoldRefuseWarning(org.getId(), org.getName());
            } catch (Exception e) {
                Window.alert(e.getMessage());
                logger.log(Level.SEVERE, e.getMessage(), e);
            }
        }
    }

    private void getOrganizationMatches(String match) {
        Item<Integer> row;
        OrganizationDO data;
        ArrayList<OrganizationDO> list;
        ArrayList<Item<Integer>> model;

        setBusy();
        try {
            list = OrganizationService1Impl.INSTANCE.fetchByIdOrName(QueryFieldUtil.parseAutocomplete(match));
            model = new ArrayList<Item<Integer>>();
            for (int i = 0; i < list.size(); i++ ) {
                row = new Item<Integer>(5);
                data = list.get(i);

                row.setKey(data.getId());
                row.setData(data);
                row.setCell(0, data.getName());
                row.setCell(1, data.getAddress().getMultipleUnit());
                row.setCell(2, data.getAddress().getStreetAddress());
                row.setCell(3, data.getAddress().getCity());
                row.setCell(4, data.getAddress().getState());

                model.add(row);
            }
            shipTo.showAutoMatches(model);
        } catch (Throwable e) {
            Window.alert(e.getMessage());
            logger.log(Level.SEVERE, e.getMessage(), e);
        }
        clearStatus();
    }

    private void setDescriptionSelection() {
        if (manager == null)
            description.setValue(null, "");
        else
            description.setValue(0, getDescription());
    }

    private void getDescriptionFromSelection() {
        AutoCompleteValue row;
        IdNameVO data;

        row = description.getValue();
        if (row == null || row.getId() == null || row.getData() == null) {
            manager.getIorder().setDescription(null);
        } else {
            data = (IdNameVO)row.getData();
            manager.getIorder().setDescription(data.getName());
        }

    }

    private void getDescriptionMatches(String match) {
        Item<Integer> row;
        ArrayList<Item<Integer>> model;
        IdNameVO data;
        ArrayList<IdNameVO> dataList;
        ArrayList<String> matchList;
        String name;

        setBusy();
        try {
            model = new ArrayList<Item<Integer>>();
            row = new Item<Integer>(0, match);
            row.setData(new IdNameVO(null, match));
            model.add(row);
            if (descQuery == null || match.indexOf(descQuery) != 0) {
                dataList = OrderService1.get().fetchByDescription(match + "%", 10);
                matchList = new ArrayList<String>();
                for (int i = 0; i < dataList.size(); i++ ) {
                    data = dataList.get(i);
                    name = data.getName();
                    if ( !matchList.contains(name)) {
                        row = new Item<Integer>(i + 1, name);
                        row.setData(data);
                        model.add(row);
                        matchList.add(name);
                    }
                }

                if (dataList.size() == 0)
                    descQuery = match;
            }
            description.showAutoMatches(model);
        } catch (Throwable e) {
            Window.alert(e.getMessage());
            logger.log(Level.SEVERE, e.getMessage(), e);
        }
        clearStatus();
    }

    /*
     * getters and setters
     */
    private Integer getId() {
        if (manager == null)
            return null;
        return manager.getIorder().getId();
    }

    private Integer getNeededDays() {
        if (manager == null)
            return null;
        return manager.getIorder().getNeededInDays();
    }

    private void setNeededDays(Integer neededDays) {
        manager.getIorder().setNeededInDays(neededDays);
    }

    private Integer getNumberOfForms() {
        if (manager == null)
            return null;
        return manager.getIorder().getNumberOfForms();
    }

    private void setNumberOfForms(Integer numberOfForms) {
        manager.getIorder().setNumberOfForms(numberOfForms);
    }

    private String getMultipleUnit() {
        OrganizationDO org;

        org = getShipTo();
        if (org == null)
            return null;
        return org.getAddress().getMultipleUnit();
    }

    private String getOrganizationAttention() {
        if (manager == null)
            return null;
        return manager.getIorder().getOrganizationAttention();
    }

    private void setOrganizationAttention(String organizationAttention) {
        manager.getIorder().setOrganizationAttention(organizationAttention);
    }

    private String getRequestedBy() {
        if (manager == null)
            return null;
        return manager.getIorder().getRequestedBy();
    }

    private void setRequestedBy(String requestedBy) {
        manager.getIorder().setRequestedBy(requestedBy);
    }

    private String getStreetAddress() {
        OrganizationDO org;

        org = getShipTo();
        if (org == null)
            return null;
        return org.getAddress().getStreetAddress();
    }

    private String getCity() {
        OrganizationDO org;

        org = getShipTo();
        if (org == null)
            return null;
        return org.getAddress().getCity();
    }

    private String getZipCode() {
        OrganizationDO org;

        org = getShipTo();
        if (org == null)
            return null;
        return org.getAddress().getZipCode();
    }

    private Datetime getOrderedDate() {
        if (manager == null)
            return null;
        return manager.getIorder().getOrderedDate();
    }

    private void setOrderedDate(Datetime date) {
        manager.getIorder().setOrderedDate(date);
    }

    private Integer getStatusId() {
        if (manager == null)
            return null;
        return manager.getIorder().getStatusId();
    }

    private void setStatusId(Integer statusId) {
        manager.getIorder().setStatusId(statusId);
    }

    private Integer getShipFromId() {
        if (manager == null)
            return null;
        return manager.getIorder().getShipFromId();
    }

    private void setShipFromId(Integer shipFromId) {
        manager.getIorder().setShipFromId(shipFromId);
    }

    private Integer getCostCenterId() {
        if (manager == null)
            return null;
        return manager.getIorder().getCostCenterId();
    }

    private void setCostCenterId(Integer costCenterId) {
        manager.getIorder().setCostCenterId(costCenterId);
    }

    private String getState() {
        OrganizationDO org;

        org = getShipTo();
        if (org == null)
            return null;
        return org.getAddress().getState();
    }

    private String getDescription() {
        if (manager == null)
            return null;
        return manager.getIorder().getDescription();
    }

    /**
     * returns the ship-to organization from the manager if there is one
     */
    private OrganizationDO getShipTo() {
        if (manager == null)
            return null;

        return manager.getIorder().getOrganization();
    }

    /**
     * Sets the latest manager in the tabs
     */
    private void setData() {
        organizationTab.setData(manager);
        testTab.setData(manager);
        containerTab.setData(manager);
        itemTab.setData(manager);
        shippingNotesTab.setData(manager);
        customerNotesTab.setData(manager);
        internalNotesTab.setData(manager);
        sampleNotesTab.setData(manager);
        recurrenceTab.setData(manager);
        fillTab.setData(manager);
    }

    private void fetchById(Integer id) {
        if (id == null) {
            manager = null;
            setData();
            setState(DEFAULT);
            fireDataChange();
            clearStatus();
        } else {
            setBusy(Messages.get().gen_fetching());
            if (fetchByIdCall == null) {
                fetchByIdCall = new AsyncCallbackUI<IOrderManager1>() {
                    public void success(IOrderManager1 result) {
                        manager = result;
                        setData();
                        setState(DISPLAY);
                    }

                    public void notFound() {
                        fetchById(null);
                        setDone(Messages.get().gen_noRecordsFound());
                    }

                    public void failure(Throwable e) {
                        fetchById(null);
                        Window.alert(Messages.get().gen_fetchFailed() + e.getMessage());
                        logger.log(Level.SEVERE, e.getMessage(), e);
                    }

                    public void finish() {
                        fireDataChange();
                        clearStatus();
                    }
                };
            }

            OrderService1.get().fetchById(id, elements, fetchByIdCall);
        }
    }

    private void showHoldRefuseWarning(Integer orgId, String name) throws Exception {
        if (SampleOrganizationUtility1.isHoldRefuseSampleForOrg(orgId))
            Window.alert(Messages.get().gen_orgMarkedAsHoldRefuseSample(name));
    }

    /**
     * creates or updates the cache of objects like TestManager that are used
     * frequently by the different parts of the screen
     */
    private void buildCache() throws Exception {
        int i;
        Integer prevId;
        ArrayList<Integer> ids;
        AuxDataViewDO aux;
        IOrderTestViewDO test;
        ArrayList<TestManager> tms;
        ArrayList<AuxFieldGroupManager> afgms;

        cache = new HashMap<String, Object>();

        /*
         * the list of tests to be fetched
         */
        ids = new ArrayList<Integer>();
        for (i = 0; i < manager.test.count(); i++ ) {
            test = manager.test.get(i);
            ids.add(test.getTestId());
        }

        if (ids.size() > 0) {
            tms = TestService.get().fetchByIds(ids);
            for (TestManager tm : tms)
                cache.put("tm:" + tm.getTest().getId(), tm);
        }

        /*
         * the list of aux field groups to be fetched
         */
        ids.clear();
        prevId = null;
        for (i = 0; i < manager.auxData.count(); i++ ) {
            aux = manager.auxData.get(i);
            if ( !aux.getAuxFieldGroupId().equals(prevId)) {
                ids.add(aux.getAuxFieldGroupId());
                prevId = aux.getAuxFieldGroupId();
            }
        }

        if (ids.size() > 0) {
            afgms = AuxiliaryService.get().fetchByIds(ids);
            for (AuxFieldGroupManager afgm : afgms)
                cache.put("am:" + afgm.getGroup().getId(), afgm);
        }
    }

    /**
     * Creates a string containing the message that there are warnings on the
     * screen, followed by all warning messages, followed by the question
     * whether the data should be committed
     */
    private String getWarnings(ArrayList<Exception> warnings, boolean isConfirm) {
        StringBuilder b;

        b = new StringBuilder();
        b.append(Messages.get().gen_warningDialogLine1()).append("\n");
        if (warnings != null) {
            for (Exception ex : warnings) {
                if (ex instanceof Warning || ex instanceof Caution)
                    b.append(" * ").append(ex.getMessage()).append("\n");
            }
        }

        if (isConfirm)
            b.append("\n").append(Messages.get().gen_warningDialogLastLine());

        return b.toString();
    }

    private void addTest(Integer id, boolean isTest, Integer index) {
        int numAuxBef, numAuxAft;
        IOrderReturnVO ret;
        try {
            numAuxBef = manager.auxData.count();
            ret = OrderService1.get().addTest(manager, id, isTest, index);
            manager = ret.getManager();
            numAuxAft = manager.auxData.count();
            setData();
            setState(state);
            fireDataChange();
            if (numAuxAft > numAuxBef) {
                /*
                 * the number of aux data after adding the test is more than the
                 * ones before, so it means that a panel was added which linked
                 * to some aux groups, so notify the tabs
                 */
                bus.fireEventFromSource(new AddAuxGroupEvent(null), this);
            }
            if (ret.getErrors() != null && ret.getErrors().size() > 0)
                showErrors(ret.getErrors());
            else
                clearStatus();
        } catch (Exception ex) {
            Window.alert(ex.getMessage());
            logger.log(Level.SEVERE, ex.getMessage(), ex);
        }
    }

    private void removeTests(ArrayList<Integer> ids) {
        try {
            manager = OrderService1.get().removeTests(manager, ids);
            setData();
            setState(state);
            fireDataChange();
        } catch (Exception ex) {
            Window.alert(ex.getMessage());
            logger.log(Level.SEVERE, ex.getMessage(), ex);
        }
    }

    private void showShippingScreen(ShippingManager manager) throws Exception {
        ScreenWindow modal;

        modal = new ScreenWindow(ScreenWindow.Mode.LOOK_UP);
        modal.setName(Messages.get().gen_shipping());
        if (shippingScreen == null)
            shippingScreen = new ShippingScreen(modal);

        modal.setContent(shippingScreen);
        shippingScreen.loadShippingData(manager, org.openelis.gwt.screen.Screen.State.DISPLAY);
        clearStatus();
    }
}