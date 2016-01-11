package org.openelis.modules.sample1.client;

import static org.openelis.modules.main.client.Logger.*;
import static org.openelis.ui.screen.State.*;

import java.util.ArrayList;
import java.util.logging.Level;

import org.openelis.cache.CategoryCache;
import org.openelis.cache.DictionaryCache;
import org.openelis.constants.Messages;
import org.openelis.domain.AddressDO;
import org.openelis.domain.Constants;
import org.openelis.domain.DictionaryDO;
import org.openelis.domain.OrganizationDO;
import org.openelis.domain.ProjectDO;
import org.openelis.domain.SampleOrganizationViewDO;
import org.openelis.domain.SampleProjectViewDO;
import org.openelis.manager.SampleManager1;
import org.openelis.meta.SampleMeta;
import org.openelis.modules.eorder.client.EOrderLookupUI;
import org.openelis.modules.main.client.OpenELIS;
import org.openelis.modules.order1.client.SendoutOrderScreenUI;
import org.openelis.modules.organization1.client.OrganizationService1Impl;
import org.openelis.modules.project.client.ProjectService;
import org.openelis.ui.common.DataBaseUtil;
import org.openelis.ui.common.Datetime;
import org.openelis.ui.common.data.QueryData;
import org.openelis.ui.event.DataChangeEvent;
import org.openelis.ui.event.GetMatchesEvent;
import org.openelis.ui.event.GetMatchesHandler;
import org.openelis.ui.event.StateChangeEvent;
import org.openelis.ui.resources.UIResources;
import org.openelis.ui.screen.Screen;
import org.openelis.ui.screen.ScreenHandler;
import org.openelis.ui.screen.State;
import org.openelis.ui.widget.AutoComplete;
import org.openelis.ui.widget.AutoCompleteValue;
import org.openelis.ui.widget.Button;
import org.openelis.ui.widget.Dropdown;
import org.openelis.ui.widget.Item;
import org.openelis.ui.widget.ModalWindow;
import org.openelis.ui.widget.QueryFieldUtil;
import org.openelis.ui.widget.Queryable;
import org.openelis.ui.widget.TextBox;
import org.openelis.ui.widget.calendar.Calendar;
import org.openelis.ui.widget.table.Row;
import org.openelis.ui.widget.table.Table;
import org.openelis.ui.widget.table.event.BeforeCellEditedEvent;
import org.openelis.ui.widget.table.event.BeforeCellEditedHandler;
import org.openelis.ui.widget.table.event.CellEditedEvent;
import org.openelis.ui.widget.table.event.CellEditedHandler;
import org.openelis.ui.widget.table.event.RowAddedEvent;
import org.openelis.ui.widget.table.event.RowAddedHandler;
import org.openelis.ui.widget.table.event.RowDeletedEvent;
import org.openelis.ui.widget.table.event.RowDeletedHandler;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.VisibleEvent;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Widget;

public class SampleTabUI extends Screen {

    @UiTemplate("SampleTab.ui.xml")
    interface SampleTabUIBinder extends UiBinder<Widget, SampleTabUI> {
    };

    private static SampleTabUIBinder uiBinder = GWT.create(SampleTabUIBinder.class);

    @UiField
    protected TextBox<Integer>       accessionNumber;

    @UiField
    protected Calendar               collectionDate, collectionTime, receivedDate, releasedDate;

    @UiField
    protected TextBox<String>        clientReference, orderId;

    @UiField
    protected Dropdown<Integer>      status, type;

    @UiField
    protected Dropdown<String>       orgState, orgCountry;

    @UiField
    protected Table                  organizationTable, projectTable;

    @UiField
    protected AutoComplete           organization, project;

    @UiField
    protected Button                 orderLookupButton, addOrganizationButton,
                    removeOrganizationButton, addProjectButton, removeProjectButton;

    protected EOrderLookupUI         eorderLookup;

    protected SampleManager1         manager;

    protected Screen                 parentScreen;

    protected SampleTabUI            screen;

    protected EventBus               parentBus;

    protected QueryFieldUtil         queryFieldUtil;

    protected boolean                canEdit, isBusy, isVisible, redraw;

    public SampleTabUI(Screen parentScreen) {
        this.parentScreen = parentScreen;
        this.parentBus = parentScreen.getEventBus();
        this.window = parentScreen.getWindow();
        initWidget(uiBinder.createAndBindUi(this));
        initialize();

        manager = null;
    }

    public void initialize() {
        Item<Integer> row;
        Item<String> srow;
        ArrayList<Item<Integer>> model;
        ArrayList<Item<String>> smodel;

        screen = this;

        addScreenHandler(accessionNumber,
                         SampleMeta.getAccessionNumber(),
                         new ScreenHandler<Integer>() {
                             public void onDataChange(DataChangeEvent event) {
                                 accessionNumber.setValue(getAccessionNumber());
                             }

                             public void onValueChange(ValueChangeEvent<Integer> event) {
                                 setAccessionNumber(event.getValue());
                             }

                             public void onStateChange(StateChangeEvent event) {
                                 accessionNumber.setEnabled(isState(QUERY) ||
                                                            (canEdit && isState(ADD, UPDATE)));
                                 accessionNumber.setQueryMode(isState(QUERY));
                             }

                             public Widget onTab(boolean forward) {
                                 return forward ? orderId : projectTable;
                             }
                         });

        addScreenHandler(orderId, "orderId", new ScreenHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                orderId.setValue(getOrderId());
            }

            public void onStateChange(StateChangeEvent event) {
                orderId.setEnabled(isState(QUERY));
                orderId.setQueryMode(isState(QUERY));
            }

            public Object getQuery() {
                String query;
                QueryData qd;

                qd = (QueryData)orderId.getQuery();
                if (qd != null) {
                    if (queryFieldUtil == null)
                        queryFieldUtil = new QueryFieldUtil();
                    /*
                     * if the query string is a valid integer query like "1" or
                     * "1..2" then it's assumed to be for send-out orders;
                     * otherwise it's assumed to be for paper order validators
                     */
                    query = qd.getQuery();
                    try {
                        queryFieldUtil.parseInteger(query);
                        qd.setKey(SampleMeta.getOrderId());
                        qd.setType(QueryData.Type.INTEGER);
                    } catch (Exception e) {
                        qd.setKey(SampleMeta.getEorderPaperOrderValidator());
                    }
                }

                return qd;
            }

            public Widget onTab(boolean forward) {
                return forward ? collectionDate : accessionNumber;
            }
        });

        addScreenHandler(orderLookupButton, "orderLookupButton", new ScreenHandler<Integer>() {
            public void onStateChange(StateChangeEvent event) {
                orderLookupButton.setEnabled(isState(DISPLAY, ADD, UPDATE));
            }
        });

        addScreenHandler(collectionDate,
                         SampleMeta.getCollectionDate(),
                         new ScreenHandler<Datetime>() {
                             public void onDataChange(DataChangeEvent event) {
                                 collectionDate.setValue(getCollectionDate());
                             }

                             public void onValueChange(ValueChangeEvent<Datetime> event) {
                                 setCollectionDate(event.getValue());
                             }

                             public void onStateChange(StateChangeEvent event) {
                                 collectionDate.setEnabled(isState(QUERY) ||
                                                           (canEdit && isState(ADD, UPDATE)));
                                 collectionDate.setQueryMode(isState(QUERY));
                             }

                             public Widget onTab(boolean forward) {
                                 return forward ? collectionTime : orderId;
                             }
                         });

        addScreenHandler(collectionTime,
                         SampleMeta.getCollectionTime(),
                         new ScreenHandler<Datetime>() {
                             public void onDataChange(DataChangeEvent event) {
                                 collectionTime.setValue(getCollectionTime());
                             }

                             public void onValueChange(ValueChangeEvent<Datetime> event) {
                                 setCollectionTime(event.getValue());
                             }

                             public void onStateChange(StateChangeEvent event) {
                                 collectionTime.setEnabled(canEdit && isState(ADD, UPDATE));
                             }
                             
                             public Object getQuery() {
                                 /*
                                  * since this field is not set in query mode,
                                  * the value doesn't get cleared when
                                  * StateChangeEvent is fired; it also doesn't
                                  * get cleared if the tab is not visible,
                                  * because DataChangeEvent is not fired in that
                                  * case; this makes sure that the value doesn't
                                  * get included in the query sent to the
                                  * back-end
                                  */
                                 return null;
                             }

                             public Widget onTab(boolean forward) {
                                 return forward ? receivedDate : collectionDate;
                             }
                         });

        addScreenHandler(receivedDate, SampleMeta.getReceivedDate(), new ScreenHandler<Datetime>() {
            public void onDataChange(DataChangeEvent event) {
                receivedDate.setValue(getReceivedDate());
            }

            public void onValueChange(ValueChangeEvent<Datetime> event) {
                setReceivedDate(event.getValue());
            }

            public void onStateChange(StateChangeEvent event) {
                receivedDate.setEnabled(isState(QUERY) || (canEdit && isState(ADD, UPDATE)));
                receivedDate.setQueryMode(isState(QUERY));
            }

            public Widget onTab(boolean forward) {
                return forward ? status : collectionTime;
            }
        });

        addScreenHandler(status, SampleMeta.getStatusId(), new ScreenHandler<Integer>() {
            public void onDataChange(DataChangeEvent event) {
                status.setValue(getStatusId());
            }

            public void onValueChange(ValueChangeEvent<Integer> event) {
                setStatusId(event.getValue());
            }

            public void onStateChange(StateChangeEvent event) {
                status.setEnabled(isState(QUERY));
                status.setQueryMode(isState(QUERY));
            }

            public Widget onTab(boolean forward) {
                return forward ? clientReference : receivedDate;
            }
        });

        addScreenHandler(clientReference,
                         SampleMeta.getClientReference(),
                         new ScreenHandler<String>() {
                             public void onDataChange(DataChangeEvent event) {
                                 clientReference.setValue(getClientReference());
                             }

                             public void onValueChange(ValueChangeEvent<String> event) {
                                 setClientReference(event.getValue());
                             }

                             public void onStateChange(StateChangeEvent event) {
                                 clientReference.setEnabled(isState(QUERY) ||
                                                            (canEdit && isState(ADD, UPDATE)));
                                 clientReference.setQueryMode(isState(QUERY));
                             }

                             public Widget onTab(boolean forward) {
                                 return forward ? releasedDate : status;
                             }
                         });
        
        addScreenHandler(releasedDate, SampleMeta.getReleasedDate(), new ScreenHandler<Datetime>() {
            public void onDataChange(DataChangeEvent event) {
                releasedDate.setValue(getReleasedDate());
            }

            public void onStateChange(StateChangeEvent event) {
                releasedDate.setEnabled(isState(QUERY));
                releasedDate.setQueryMode(isState(QUERY));
            }

            public Widget onTab(boolean forward) {
                return forward ? organizationTable : clientReference;
            }
        });

        addScreenHandler(organizationTable,
                         "organizationTable",
                         new ScreenHandler<ArrayList<Row>>() {
                             public void onDataChange(DataChangeEvent event) {
                                 if ( !isState(QUERY))
                                     organizationTable.setModel(getOrganizationTableModel());
                             }

                             public void onStateChange(StateChangeEvent event) {
                                 organizationTable.setEnabled(true);
                                 organizationTable.setQueryMode(isState(QUERY));
                                 /*
                                  * disable the types that are not allowed for
                                  * this sample's domain
                                  */
                                 if (manager != null) {
                                     try {
                                         for (Item<Integer> row : type.getModel())
                                             row.setEnabled(canAddOrganizationType(row.getKey()));
                                     } catch (Exception e) {
                                         Window.alert(e.getMessage());
                                         logger.log(Level.SEVERE, e.getMessage(), e);
                                     }
                                 }
                             }

                             public Object getQuery() {
                                 ArrayList<QueryData> qds;
                                 QueryData qd;

                                 qds = new ArrayList<QueryData>();
                                 for (int i = 0; i < 9; i++ ) {
                                     qd = (QueryData) ((Queryable)organizationTable.getColumnWidget(i)).getQuery();
                                     if (qd != null) {
                                         switch (i) {
                                             case 0:
                                                 qd.setKey(SampleMeta.getSampleOrgTypeId());
                                                 break;
                                             case 1:
                                                 qd.setKey(SampleMeta.getSampleOrgOrganizationAttention());
                                                 break;
                                             case 2:
                                                 qd.setKey(SampleMeta.getSampleOrgOrganizationId());
                                                 break;
                                             case 3:
                                                 qd.setKey(SampleMeta.getSampleOrgOrganizationName());
                                                 break;
                                             case 4:
                                                 qd.setKey(SampleMeta.getSampleOrgOrganizationAddressMultipleUnit());
                                                 break;
                                             case 5:
                                                 qd.setKey(SampleMeta.getSampleOrgOrganizationAddressStreetAddress());
                                                 break;
                                             case 6:
                                                 qd.setKey(SampleMeta.getSampleOrgOrganizationAddressCity());
                                                 break;
                                             case 7:
                                                 qd.setKey(SampleMeta.getSampleOrgOrganizationAddressState());
                                                 break;
                                             case 8:
                                                 qd.setKey(SampleMeta.getSampleOrgOrganizationAddressZipCode());
                                                 break;
                                             case 9:
                                                 qd.setKey(SampleMeta.getSampleOrgOrganizationAddressCountry());
                                                 break;
                                         }
                                         qds.add(qd);
                                     }
                                 }

                                 return qds;
                             }
                             
                             public Widget onTab(boolean forward) {
                                 return forward ? projectTable : clientReference;
                             }
                         });

        organizationTable.addSelectionHandler(new SelectionHandler<Integer>() {
            @Override
            public void onSelection(SelectionEvent<Integer> event) {
                removeOrganizationButton.setEnabled(canEdit && isState(ADD, UPDATE));
            }
        });

        organizationTable.addBeforeCellEditedHandler(new BeforeCellEditedHandler() {
            @Override
            public void onBeforeCellEdited(BeforeCellEditedEvent event) {
                if ( ! (canEdit && isState(ADD, UPDATE)) || event.getCol() == 2 ||
                    event.getCol() > 3)
                    event.cancel();
            }
        });

        organizationTable.addCellEditedHandler(new CellEditedHandler() {
            public void onCellUpdated(CellEditedEvent event) {
                int r, c;
                Object val;
                AutoCompleteValue row;
                OrganizationDO org;
                AddressDO addr;
                SampleOrganizationViewDO data;

                r = event.getRow();
                c = event.getCol();

                data = manager.organization.get(r);

                val = organizationTable.getValueAt(r, c);

                switch (c) {
                    case 0:
                        data.setTypeId((Integer)val);
                        break;
                    case 1:
                        data.setOrganizationAttention((String)val);
                        break;
                    case 3:
                        row = (AutoCompleteValue)val;
                        if (row != null) {
                            org = (OrganizationDO)row.getData();
                            data.setOrganizationId(org.getId());
                            data.setOrganizationName(org.getName());

                            addr = org.getAddress();
                            data.setOrganizationMultipleUnit(addr.getMultipleUnit());
                            data.setOrganizationStreetAddress(addr.getStreetAddress());
                            data.setOrganizationCity(addr.getCity());
                            data.setOrganizationState(addr.getState());
                            data.setOrganizationZipCode(addr.getZipCode());
                            data.setOrganizationCountry(addr.getCountry());
                        } else {
                            data.setOrganizationId(null);
                            data.setOrganizationName(null);
                            data.setOrganizationMultipleUnit(null);
                            data.setOrganizationStreetAddress(null);
                            data.setOrganizationCity(null);
                            data.setOrganizationState(null);
                            data.setOrganizationZipCode(null);
                            data.setOrganizationCountry(null);
                        }

                        organizationTable.setValueAt(r, 2, data.getOrganizationId());
                        organizationTable.setValueAt(r, 4, data.getOrganizationMultipleUnit());
                        organizationTable.setValueAt(r, 5, data.getOrganizationStreetAddress());
                        organizationTable.setValueAt(r, 6, data.getOrganizationCity());
                        organizationTable.setValueAt(r, 7, data.getOrganizationState());
                        organizationTable.setValueAt(r, 8, data.getOrganizationZipCode());
                        organizationTable.setValueAt(r, 9, data.getOrganizationCountry());

                        try {
                            if (SampleOrganizationUtility1.isHoldRefuseSampleForOrg(data.getOrganizationId()))
                                Window.alert(Messages.get()
                                                     .gen_orgMarkedAsHoldRefuseSample(data.getOrganizationName()));
                        } catch (Exception e) {
                            Window.alert(e.getMessage());
                            logger.log(Level.SEVERE, e.getMessage(), e);
                        }
                        break;
                }
            }
        });

        organization.addGetMatchesHandler(new GetMatchesHandler() {
            public void onGetMatches(GetMatchesEvent event) {
                Item<Integer> row;
                OrganizationDO data;
                ArrayList<OrganizationDO> list;
                ArrayList<Item<Integer>> model;

                parentScreen.setBusy();
                try {
                    list = OrganizationService1Impl.INSTANCE.fetchByIdOrName(QueryFieldUtil.parseAutocomplete(event.getMatch()));
                    model = new ArrayList<Item<Integer>>();
                    for (int i = 0; i < list.size(); i++ ) {
                        row = new Item<Integer>(5);
                        data = list.get(i);

                        row.setKey(data.getId());
                        row.setCell(0, data.getName());
                        row.setCell(1, data.getAddress().getMultipleUnit());
                        row.setCell(2, data.getAddress().getStreetAddress());
                        row.setCell(3, data.getAddress().getCity());
                        row.setCell(4, data.getAddress().getState());
                        row.setData(data);
                        model.add(row);
                    }
                    organization.showAutoMatches(model);
                } catch (Exception e) {
                    Window.alert(e.getMessage());
                    logger.log(Level.SEVERE, e.getMessage(), e);
                }
                parentScreen.clearStatus();
            }
        });

        organizationTable.addRowAddedHandler(new RowAddedHandler() {
            public void onRowAdded(RowAddedEvent event) {
                manager.organization.add();
                removeOrganizationButton.setEnabled(true);
            }
        });

        organizationTable.addRowDeletedHandler(new RowDeletedHandler() {
            public void onRowDeleted(RowDeletedEvent event) {
                manager.organization.remove(event.getIndex());
                removeOrganizationButton.setEnabled(false);
            }
        });

        addScreenHandler(addOrganizationButton,
                         "addOrganizationButton",
                         new ScreenHandler<Object>() {
                             public void onStateChange(StateChangeEvent event) {
                                 addOrganizationButton.setEnabled(canEdit && isState(ADD, UPDATE));
                             }
                         });

        addScreenHandler(removeOrganizationButton,
                         "removeOrganizationButton",
                         new ScreenHandler<Object>() {
                             public void onStateChange(StateChangeEvent event) {
                                 removeOrganizationButton.setEnabled(false);
                             }
                         });

        addScreenHandler(projectTable, "projectTable", new ScreenHandler<ArrayList<Row>>() {
            public void onDataChange(DataChangeEvent event) {
                if ( !isState(QUERY))
                    projectTable.setModel(getProjectTableModel());
            }

            public void onStateChange(StateChangeEvent event) {
                projectTable.setEnabled(true);
                projectTable.setQueryMode(isState(QUERY));
            }

            public Object getQuery() {
                ArrayList<QueryData> qds;
                QueryData qd;

                qds = new ArrayList<QueryData>();
                for (int i = 0; i < 3; i++ ) {
                    qd = (QueryData) ((Queryable)projectTable.getColumnWidget(i)).getQuery();
                    if (qd != null) {
                        switch (i) {
                            case 0:
                                qd.setKey(SampleMeta.getSampleProjProjectName());
                                break;
                            case 1:
                                qd.setKey(SampleMeta.getSampleProjProjectDescription());
                                break;
                            case 2:
                                qd.setKey(SampleMeta.getSampleProjectIsPermanent());
                                break;
                        }
                        qds.add(qd);
                    }
                }

                return qds;
            }
            
            public Widget onTab(boolean forward) {
                return forward ? accessionNumber : organizationTable;
            }
        });

        projectTable.addSelectionHandler(new SelectionHandler<Integer>() {
            public void onSelection(SelectionEvent<Integer> event) {
                removeProjectButton.setEnabled(canEdit && isState(ADD, UPDATE));
            }
        });

        projectTable.addBeforeCellEditedHandler(new BeforeCellEditedHandler() {
            @Override
            public void onBeforeCellEdited(BeforeCellEditedEvent event) {
                if ( ! (canEdit && isState(ADD, UPDATE)) || event.getCol() == 1)
                    event.cancel();
            }
        });

        projectTable.addCellEditedHandler(new CellEditedHandler() {
            public void onCellUpdated(CellEditedEvent event) {
                int r, c;
                SampleProjectViewDO data;
                ProjectDO proj;
                Object val;
                AutoCompleteValue row;

                r = event.getRow();
                c = event.getCol();

                data = manager.project.get(r);

                val = projectTable.getValueAt(r, c);

                switch (c) {
                    case 0:
                        row = (AutoCompleteValue)val;
                        if (row != null) {
                            proj = (ProjectDO)row.getData();
                            data.setProjectId(proj.getId());
                            data.setProjectName(proj.getName());
                            data.setProjectDescription(proj.getDescription());
                        } else {
                            data.setProjectId(null);
                            data.setProjectName(null);
                            data.setProjectDescription(null);
                        }

                        projectTable.setValueAt(r, 1, data.getProjectDescription());
                        break;
                    case 2:
                        data.setIsPermanent((String)val);
                        break;
                }
            }
        });

        project.addGetMatchesHandler(new GetMatchesHandler() {
            public void onGetMatches(GetMatchesEvent event) {
                Item<Integer> row;
                ProjectDO data;
                ArrayList<ProjectDO> list;
                ArrayList<Item<Integer>> model;

                parentScreen.setBusy();
                try {
                    list = ProjectService.get()
                                         .fetchActiveByName(QueryFieldUtil.parseAutocomplete(event.getMatch()));
                    model = new ArrayList<Item<Integer>>();
                    for (int i = 0; i < list.size(); i++ ) {
                        row = new Item<Integer>(4);
                        data = list.get(i);

                        row.setKey(data.getId());
                        row.setCell(0, data.getName());
                        row.setCell(1, data.getDescription());
                        row.setData(data);
                        model.add(row);
                    }
                    project.showAutoMatches(model);
                } catch (Throwable e) {
                    e.printStackTrace();
                    Window.alert(e.getMessage());
                }
                parentScreen.clearStatus();
            }
        });

        projectTable.addRowAddedHandler(new RowAddedHandler() {
            public void onRowAdded(RowAddedEvent event) {
                SampleProjectViewDO data;
                
                data = manager.project.add();
                projectTable.setValueAt(event.getIndex(), 2, data.getIsPermanent());
                removeProjectButton.setEnabled(true);
            }
        });

        projectTable.addRowDeletedHandler(new RowDeletedHandler() {
            public void onRowDeleted(RowDeletedEvent event) {
                manager.project.remove(event.getIndex());
                removeProjectButton.setEnabled(false);
            }
        });

        addScreenHandler(addProjectButton, "addProjectButton", new ScreenHandler<Object>() {
            public void onStateChange(StateChangeEvent event) {
                addProjectButton.setEnabled(canEdit && isState(ADD, UPDATE));
            }
        });

        addScreenHandler(removeProjectButton, "removeProjectButton", new ScreenHandler<Object>() {
            public void onStateChange(StateChangeEvent event) {
                removeProjectButton.setEnabled(false);
            }
        });

        parentBus.addHandler(AccessionChangeEvent.getType(), new AccessionChangeEvent.Handler() {
            @Override
            public void onAccessionChange(AccessionChangeEvent event) {
                if (screen == event.getSource())
                    return;

                redraw = true;
                displaySample();
                if (event.getError() != null)
                    accessionNumber.addException(event.getError());
                isBusy = false;
            }
        });

        addVisibleHandler(new VisibleEvent.Handler() {
            public void onVisibleOrInvisible(VisibleEvent event) {
                isVisible = event.isVisible();
                displaySample();
            }
        });

        /*
         * load models in the dropdowns
         */
        model = new ArrayList<Item<Integer>>();
        for (DictionaryDO d : CategoryCache.getBySystemName("sample_status")) {
            row = new Item<Integer>(d.getId(), d.getEntry());
            row.setEnabled( ("Y".equals(d.getIsActive())));
            model.add(row);
        }

        status.setModel(model);

        model = new ArrayList<Item<Integer>>();
        for (DictionaryDO d : CategoryCache.getBySystemName("organization_type")) {
            row = new Item<Integer>(d.getId(), d.getEntry());
            row.setEnabled( ("Y".equals(d.getIsActive())));
            model.add(row);
        }

        type.setModel(model);

        smodel = new ArrayList<Item<String>>();
        for (DictionaryDO d : CategoryCache.getBySystemName("state")) {
            srow = new Item<String>(d.getEntry(), d.getEntry());
            srow.setEnabled("Y".equals(d.getIsActive()));
            smodel.add(srow);
        }
        orgState.setModel(smodel);

        smodel = new ArrayList<Item<String>>();
        for (DictionaryDO d : CategoryCache.getBySystemName("country")) {
            srow = new Item<String>(d.getEntry(), d.getEntry());
            srow.setEnabled("Y".equals(d.getIsActive()));
            smodel.add(srow);
        }
        orgCountry.setModel(smodel);
    }

    public void setData(SampleManager1 manager) {
        this.manager = manager;
    }

    public void setState(State state) {
        evaluateEdit();
        this.state = state;
        bus.fireEventFromSource(new StateChangeEvent(state), this);
    }

    /**
     * returns true if some operation performed by the tab needs to be completed
     * before the data can be committed
     */
    public boolean getIsBusy() {
        return isBusy;
    }

    /**
     * Notifies the tab that it may need to refresh the display in its widgets;
     * if the data currently showing in the widgets is the same as the data in
     * the latest manager then the widgets are not refreshed
     */
    public void onDataChange() {
        redraw = true;
        displaySample();
    }

    /**
     * Shows the order linked to the sample on the screen corresponding to the
     * type of order e.g. Send-out order screen for environmental samples and
     * E-Order look up screen for clinical samples
     */
    @UiHandler("orderLookupButton")
    protected void orderLookup(ClickEvent event) {
        String domain;
        org.openelis.ui.widget.Window window;
        final SendoutOrderScreenUI orderScreen;
        ScheduledCommand cmd;

        if (getOrderId() == null)
            return;

        domain = manager.getSample().getDomain();

        if (Constants.domain().ENVIRONMENTAL.equals(domain) ||
            Constants.domain().PRIVATEWELL.equals(domain) ||
            Constants.domain().SDWIS.equals(domain)) {
            try {
                window = new org.openelis.ui.widget.Window();
                window.setName(Messages.get().order_sendoutOrder());
                window.setSize("1020px", "588px");
                orderScreen = new SendoutOrderScreenUI(window);
                window.setContent(orderScreen);
                OpenELIS.getBrowser().addWindow(window, "sendoutOrder");
                cmd = new ScheduledCommand() {
                    @Override
                    public void execute() {
                        orderScreen.query(manager.getSample().getOrderId());
                    }
                };
                Scheduler.get().scheduleDeferred(cmd);
            } catch (Throwable e) {
                Window.alert(e.getMessage());
                logger.log(Level.SEVERE, e.getMessage(), e);
            }
        } else if (Constants.domain().CLINICAL.equals(domain)) {
            showEOrderLookup(manager.getSampleClinical().getPaperOrderValidator());
        } else if (Constants.domain().NEONATAL.equals(domain)) {
            showEOrderLookup(manager.getSampleNeonatal().getPaperOrderValidator());
        }
    }

    @UiHandler("addOrganizationButton")
    protected void addOrganization(ClickEvent event) {
        int r;

        organizationTable.addRow();
        r = organizationTable.getRowCount() - 1;
        organizationTable.selectRowAt(r);
        organizationTable.scrollToVisible(r);
        organizationTable.startEditing(r, 0);
    }

    @UiHandler("removeOrganizationButton")
    protected void removeOrganization(ClickEvent event) {
        int r;

        r = organizationTable.getSelectedRow();
        if (r > -1 && organizationTable.getRowCount() > 0)
            organizationTable.removeRowAt(r);
    }

    @UiHandler("addProjectButton")
    protected void addProject(ClickEvent event) {
        int r;

        projectTable.addRow();
        r = projectTable.getRowCount() - 1;
        projectTable.selectRowAt(r);
        projectTable.scrollToVisible(r);
        projectTable.startEditing(r, 0);
    }

    @UiHandler("removeProjectButton")
    protected void removeProject(ClickEvent event) {
        int r;

        r = projectTable.getSelectedRow();
        if (r > -1 && projectTable.getRowCount() > 0)
            projectTable.removeRowAt(r);
    }

    /**
     * determines if the fields on the screen can be edited based on the data
     */
    private void evaluateEdit() {
        canEdit = (manager != null && !Constants.dictionary().SAMPLE_RELEASED.equals(manager.getSample()
                                                                                            .getStatusId()));
    }

    private void displaySample() {
        if ( !isVisible)
            return;

        if (redraw) {
            redraw = false;
            fireDataChange();
        }
    }

    private void showEOrderLookup(String pov) {
        ModalWindow modal;

        if (eorderLookup == null) {
            eorderLookup = new EOrderLookupUI() {
                @Override
                public void select() {
                }

                @Override
                public void cancel() {
                }
            };
        }

        modal = new ModalWindow();
        modal.setSize("735px", "350px");
        modal.setName(Messages.get().eorder_eorderLookup());
        modal.setCSS(UIResources.INSTANCE.window());
        modal.setContent(eorderLookup);

        eorderLookup.setWindow(modal);
        eorderLookup.setState(state);
        eorderLookup.setPaperOrderValidator(pov, manager.getSample().getOrderId());
    }

    /*
     * getters and setters for the fields at the sample level
     */

    /**
     * returns the accession number or null if the manager is null
     */
    private Integer getAccessionNumber() {
        if (manager == null)
            return null;
        return manager.getSample().getAccessionNumber();
    }

    /**
     * Calls the service method to merge a quick-entered sample that has this
     * accession number with the sample on the screen, if the number is not
     * null, the user confirms changing it and the sample on the screen is not
     * an existing one. Otherwise, just sets the number in the manager.
     */
    private void setAccessionNumber(Integer accession) {
        if (accession == null) {
            manager.getSample().setAccessionNumber(accession);
            return;
        }

        isBusy = true;
        if (getAccessionNumber() != null) {
            if ( !Window.confirm(Messages.get().sample_accessionNumberEditConfirm())) {
                accessionNumber.setValue(getAccessionNumber());
                accessionNumber.setFocus(true);
                isBusy = false;
                return;
            }
        }

        /*
         * remove any exceptions added because of the previous value
         */
        accessionNumber.clearExceptions();

        /*
         * notify the main screen that the accession number is to be changed
         */
        parentBus.fireEventFromSource(new AccessionChangeEvent(accession), screen);
    }

    /**
     * returns the order id or the paper order validator for the electronic
     * order or null if the manager is null
     */
    private String getOrderId() {
        String domain, orderId;

        if (manager == null)
            return null;

        domain = manager.getSample().getDomain();
        orderId = null;
        if (Constants.domain().ENVIRONMENTAL.equals(domain) ||
            Constants.domain().PRIVATEWELL.equals(domain) ||
            Constants.domain().SDWIS.equals(domain) ||
            Constants.domain().PT.equals(domain)) {
            orderId = DataBaseUtil.toString(manager.getSample().getOrderId());
        } else if (Constants.domain().CLINICAL.equals(domain)) {
            orderId = manager.getSampleClinical().getPaperOrderValidator();
        } else if (Constants.domain().NEONATAL.equals(domain)) {
            orderId = manager.getSampleNeonatal().getPaperOrderValidator();
        }

        return orderId;
    }

    /**
     * returns the collection date or null if the manager is null
     */
    private Datetime getCollectionDate() {
        if (manager == null)
            return null;
        return manager.getSample().getCollectionDate();
    }

    /**
     * sets the collection date
     */
    private void setCollectionDate(Datetime date) {
        manager.getSample().setCollectionDate(date);
    }

    /**
     * returns the collection time or null if the manager is null
     */
    private Datetime getCollectionTime() {
        if (manager == null)
            return null;
        return manager.getSample().getCollectionTime();
    }

    /**
     * sets the collection time
     */
    private void setCollectionTime(Datetime time) {
        manager.getSample().setCollectionTime(time);
    }

    /**
     * returns the received date or null if the manager is null
     */
    private Datetime getReceivedDate() {
        if (manager == null)
            return null;
        return manager.getSample().getReceivedDate();
    }

    /**
     * sets the received date
     */
    private void setReceivedDate(Datetime date) {
        manager.getSample().setReceivedDate(date);
    }

    /**
     * returns the status or null if the manager is null
     */
    private Integer getStatusId() {
        if (manager == null)
            return null;
        return manager.getSample().getStatusId();
    }

    /**
     * sets the status id
     */
    private void setStatusId(Integer statusId) {
        manager.getSample().setStatusId(statusId);
    }

    /**
     * returns the client reference or null if the manager is null
     */
    private String getClientReference() {
        if (manager == null)
            return null;
        return manager.getSample().getClientReference();
    }

    /**
     * sets the client reference
     */
    private void setClientReference(String clientReference) {
        manager.getSample().setClientReference(clientReference);
    }
    
    /**
     * returns the released date or null if the manager is null
     */
    private Datetime getReleasedDate() {
        if (manager == null)
            return null;
        return manager.getSample().getReleasedDate();
    }

    private ArrayList<Row> getOrganizationTableModel() {
        ArrayList<Row> model;
        SampleOrganizationViewDO data;
        Row row;

        model = new ArrayList<Row>();
        if (manager == null)
            return model;

        for (int i = 0; i < manager.organization.count(); i++ ) {
            data = manager.organization.get(i);

            row = new Row(10);
            row.setCell(0, data.getTypeId());
            row.setCell(1, data.getOrganizationAttention());
            row.setCell(2, data.getOrganizationId());
            row.setCell(3, new AutoCompleteValue(data.getOrganizationId(),
                                                 data.getOrganizationName()));
            row.setCell(4, data.getOrganizationMultipleUnit());
            row.setCell(5, data.getOrganizationStreetAddress());
            row.setCell(6, data.getOrganizationCity());
            row.setCell(7, data.getOrganizationState());
            row.setCell(8, data.getOrganizationZipCode());
            row.setCell(9, data.getOrganizationCountry());
            row.setData(data);
            model.add(row);
        }

        return model;
    }

    private boolean canAddOrganizationType(Integer typeId) throws Exception {
        DictionaryDO data;

        data = DictionaryCache.getById(typeId);
        /*
         * the code contains the list of domains that are allowed to have an
         * orgnaization of this type; so only those types are enabled
         */
        return data.getCode().contains(manager.getSample().getDomain());
    }

    private ArrayList<Row> getProjectTableModel() {
        ArrayList<Row> model;
        SampleProjectViewDO data;
        Row row;

        model = new ArrayList<Row>();
        if (manager == null)
            return model;

        for (int i = 0; i < manager.project.count(); i++ ) {
            data = manager.project.get(i);

            row = new Row(3);

            row.setCell(0, new AutoCompleteValue(data.getProjectId(), data.getProjectName()));
            row.setCell(1, data.getProjectDescription());
            row.setCell(2, data.getIsPermanent());
            model.add(row);
        }

        return model;
    }
}