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
package org.openelis.modules.organization.client;

import static org.openelis.client.Logger.logger;
import static org.openelis.ui.screen.State.ADD;
import static org.openelis.ui.screen.State.DEFAULT;
import static org.openelis.ui.screen.State.DISPLAY;
import static org.openelis.ui.screen.State.QUERY;
import static org.openelis.ui.screen.State.UPDATE;

import java.util.ArrayList;

import javax.inject.Inject;

import org.openelis.cache.CategoryCache;
import org.openelis.cache.UserCache;
import org.openelis.domain.AddressDO;
import org.openelis.domain.DictionaryDO;
import org.openelis.domain.IdNameVO;
import org.openelis.domain.OrganizationContactDO;
import org.openelis.domain.OrganizationDO;
import org.openelis.domain.OrganizationParameterDO;
import org.openelis.manager.OrganizationContactManager;
import org.openelis.manager.OrganizationManager;
import org.openelis.manager.OrganizationParameterManager;
import org.openelis.messages.Messages;
import org.openelis.meta.OrganizationMeta;
import org.openelis.ui.annotation.Handler;
import org.openelis.ui.common.LastPageException;
import org.openelis.ui.common.ModulePermission;
import org.openelis.ui.common.NotFoundException;
import org.openelis.ui.common.PermissionException;
import org.openelis.ui.common.ValidationErrorsList;
import org.openelis.ui.common.data.Query;
import org.openelis.ui.common.data.QueryData;
import org.openelis.ui.event.GetMatchesEvent;
import org.openelis.ui.event.GetMatchesHandler;
import org.openelis.ui.mvp.Presenter;
import org.openelis.ui.mvp.View.Validation;
import org.openelis.ui.screen.ScreenNavigator;
import org.openelis.ui.screen.State;
import org.openelis.ui.widget.AutoCompleteValue;
import org.openelis.ui.widget.Browser;
import org.openelis.ui.widget.Button;
import org.openelis.ui.widget.Item;
import org.openelis.ui.widget.QueryFieldUtil;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.BeforeSelectionEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
//import org.openelis.s.history.client.HistoryScreen;

public class OrganizationPresenter extends Presenter {
	
	@Inject 
	Browser browser;
    
    protected OrganizationManager       manager;
    
    protected ModulePermission          userPermission;

    protected ScreenNavigator<IdNameVO> nav;

    //protected NotesTabUI                notesTab;
    
    OrganizationViewImpl view;

    private enum Tabs {
        CONTACT, PARAMETER, NOTE
    };

    private Tabs tab;

    @Inject
    public OrganizationPresenter() {
    	try {
    		userPermission = UserCache.getPermission().getModule("organization");
    		if (userPermission == null)
    			throw new PermissionException(Messages.get().screenPermException("Oranization Screen"));
    	} catch (Exception e) {
    		e.printStackTrace();
    	}
        view = new OrganizationViewImpl();
        view.setPresenter(this);
     
        tab = Tabs.CONTACT;
        manager = OrganizationManager.getInstance();

        try {
            CategoryCache.getBySystemNames("country", "state", "contact_type", "parameter_type");
        } catch (Exception e) {
            Window.alert("OrganizationScreen: missing dictionary entry; " + e.getMessage());
        }

        initialize();
        setState(DEFAULT);
        initializeDropdowns();
        setData(manager);
        
        logger.fine("Organization Screen Opened");
    }
    
    public void setState(State state) {
    	this.state = state;
    	view.setState(state);
    	view.contactTab.setState(state);
    	view.parameterTab.setState(state);
    	nav.enable((state == DEFAULT || state ==  DISPLAY) && userPermission.hasSelectPermission());
    }
    
    public void setData(OrganizationManager data) {
    	this.manager = data;
    	view.setData(data);
    }

    /**
     * Setup state and data change handles for every widget on the screen
     */
    private void initialize() {


        view.orgHistory.addCommand(new Command() {
            @Override
            public void execute() {
                orgHistory();
            }
        });

        view.orgAddressHistory.addCommand(new Command() {
            public void execute() {
                orgAddressHistory();
            }
        });


        view.orgContactHistory.addCommand(new Command() {
            public void execute() {
                orgContactHistory();
            }
        });

        view.orgContactAddressHistory.addCommand(new Command() {
            public void execute() {
                orgContactAddressHistory();
            }
        });

        view.orgParameterHistory.addCommand(new Command() {
            public void execute() {
                orgParameterHistory();
            }
        });
        
        view.parentName.addGetMatchesHandler(new GetMatchesHandler() {
            public void onGetMatches(GetMatchesEvent event) {
                Item<Integer> row;
                OrganizationDO data;
                ArrayList<OrganizationDO> list;
                ArrayList<Item<Integer>> model;

             
                try {
                    list = OrganizationService.get()
                                              .fetchByIdOrName(QueryFieldUtil.parseAutocomplete(event.getMatch()));
                    model = new ArrayList<Item<Integer>>();
                    for (int i = 0; i < list.size(); i++ ) {
                        row = new Item<Integer>(4);
                        data = list.get(i);

                        row.setKey(data.getId());
                        row.setCell(0, data.getName());
                        row.setCell(1, data.getAddress().getStreetAddress());
                        row.setCell(2, data.getAddress().getCity());
                        row.setCell(3, data.getAddress().getState());

                        model.add(row);
                    }
                    view.parentName.showAutoMatches(model);
                } catch (Throwable e) {
                    e.printStackTrace();
                    Window.alert(e.getMessage());
                }
            }
        });
        
        view.tabPanel.setPopoutBrowser(browser);
        
        nav = new ScreenNavigator<IdNameVO>(view.atozTable, view.atozNext, view.atozPrev) {
            public void executeQuery(final Query query) {
                //window.setBusy(Messages.get().querying());

                query.setRowsPerPage(20);
                OrganizationService.get().query(query, new AsyncCallback<ArrayList<IdNameVO>>() {
                    public void onSuccess(ArrayList<IdNameVO> result) {
                        setQueryResult(result);
                    }

                    public void onFailure(Throwable error) {
                        setQueryResult(null);
                        if (error instanceof NotFoundException) {
                            //window.setDone(Messages.get().noRecordsFound());
                            setState(DEFAULT);
                        } else if (error instanceof LastPageException) {
                            //window.setError(Messages.get().noMoreRecordInDir());
                        } else {
                            Window.alert("Error: Organization call query failed; " +
                                         error.getMessage());
                            //window.setError(Messages.get().queryFailed());
                        }
                    }
                });
            }

            public boolean fetch(IdNameVO entry) {
                return fetchById( (entry == null) ? null : entry.getId());
            }

            public ArrayList<Item<Integer>> getModel() {
                ArrayList<IdNameVO> result;
                ArrayList<Item<Integer>> model;

                model = null;
                result = nav.getQueryResult();
                if (result != null) {
                    model = new ArrayList<Item<Integer>>();
                    for (IdNameVO entry : result)
                        model.add(new Item<Integer>(entry.getId(), entry.getName()));
                }
                return model;
            }
        };
    }

    @Handler("id")
    public void idValueChange(ValueChangeEvent<Integer> event) {
        manager.getOrganization().setId(event.getValue());
    }

    @Handler("name")
    public void nameValueChange(ValueChangeEvent<String> event) {
        manager.getOrganization().setName(event.getValue());
    }

    @Handler("city")
    public void cityValueChange(ValueChangeEvent<String> event) {
        manager.getOrganization().getAddress().setCity(event.getValue());
    }

    @Handler("multipleUnit")
    public void multipleUnitValueChange(ValueChangeEvent<String> event) {
        manager.getOrganization().getAddress().setMultipleUnit(event.getValue());
    }
    
    @Handler("state")
    public void stateValueChange(ValueChangeEvent<String> event) {
        manager.getOrganization().getAddress().setState(event.getValue());
    }
    
    @Handler("zipCode")
    public void zipCodeValueChange(ValueChangeEvent<String> event) {
        manager.getOrganization().getAddress().setZipCode(event.getValue());
    }

    @Handler("streetAddress")
    public void streetAddressValueChange(ValueChangeEvent<String> event) {
        manager.getOrganization().getAddress().setStreetAddress(event.getValue());
    }
    
    @Handler("country")
    public void countryValueChange(ValueChangeEvent<String> event) {
        manager.getOrganization().getAddress().setCountry(event.getValue());
    }

    @Handler("parentName")
    public void parentNameValueChange(ValueChangeEvent<AutoCompleteValue> event) {
        manager.getOrganization().setParentOrganizationId(event.getValue().getId());
        manager.getOrganization().setParentOrganizationName(view.parentName.getDisplay());
    }

    @Handler("isActive")
    public void isActiveValueChange(ValueChangeEvent<String> event) {
        manager.getOrganization().setIsActive(event.getValue());
    }


    @Handler("tabPanel")
    public void onBeforeSelection(BeforeSelectionEvent<Integer> event) {
    	int i;

    	// tab screen order should be the same as enum or this will
    	// not work
    	i = event.getItem().intValue();
    	tab = Tabs.values()[i];

    	//window.setBusy();
    	drawTabs();
    	//window.clearStatus();
    }
        
//        notesTab = new NotesTabUI(window, notesPanel, standardNote);
//        addDataChangeHandler(new DataChangeEvent.Handler() {
//            public void onDataChange(DataChangeEvent event) {
//                notesTab.setManager(manager);
//                if (tab == Tabs.NOTE)
//                    drawTabs();
//            }
//        });
//
//        addStateChangeHandler(new StateChangeEvent.Handler() {
//            public void onStateChange(StateChangeEvent event) {
//                notesTab.setState(event.getState());
//            }
//        });

        //
        // left hand navigation panel
        //

    
    @Handler("atozButtons")
    public void atozButtonsClick(ClickEvent event) {
    	Query query;
    	QueryData field;

    	field = new QueryData();
    	field.setKey(OrganizationMeta.getName());
    	field.setQuery( ((Button)event.getSource()).getAction());
    	field.setType(QueryData.Type.STRING);

    	query = new Query();
    	query.setFields(field);
    	nav.setQuery(query);
    }
    
//        window.addBeforeClosedHandler(new BeforeCloseHandler<WindowInt>() {
//            public void onBeforeClosed(BeforeCloseEvent<WindowInt> event) {
//                if (isState(ADD, UPDATE)) {
//                    event.cancel();
//                    window.setError(Messages.get().mustCommitOrAbort());
//                }
//            }
//        });
        
//        window.addCloseHandler(new CloseHandler<WindowInt>() {
//            
//            @Override
//            public void onClose(CloseEvent<WindowInt> event) {
//                tabPanel.close();
//            }
//        });

    private void initializeDropdowns() {
        ArrayList<Item<String>> model;
        ArrayList<DictionaryDO> list;
        Item<String> row;

        // country dropdown
        model = new ArrayList<Item<String>>();
        model.add(new Item<String>(null, ""));
        list = CategoryCache.getBySystemName("country");
        for (DictionaryDO d : list) {
            row = new Item<String>(d.getEntry(), d.getEntry());
            row.setEnabled( ("Y".equals(d.getIsActive())));
            model.add(row);
        }

        view.setCountryModel(model);

        // state dropdown
        model = new ArrayList<Item<String>>();
        model.add(new Item<String>(null, ""));
        list = CategoryCache.getBySystemName("state");
        for (DictionaryDO d : list) {
            row = new Item<String>(d.getEntry(), d.getEntry());
            row.setEnabled( ("Y".equals(d.getIsActive())));
            model.add(row);
        }

        view.setStateModel(model);
    }

    /*
     * basic button methods
     */
    @Handler("query")
    protected void query(ClickEvent event) {
        manager = OrganizationManager.getInstance();

        setState(QUERY);
        setData(manager);

        //notesTab.draw();

        view.id.setFocus(true);
        //window.setDone(Messages.get().enterFieldsToQuery());
    }

    @Handler("previous")
    protected void previous(ClickEvent event) {
        nav.previous();
    }

    @Handler("next")
    protected void next(ClickEvent event) {
        nav.next();
    }

    @Handler("add")
    protected void add(ClickEvent event) {
        manager = OrganizationManager.getInstance();
        manager.getOrganization().setIsActive("Y");

        setState(ADD);
        setData(manager);

        view.name.setFocus(true);
        //window.setDone(Messages.get().enterInformationPressCommit());
    }

    @Handler("update")
    protected void update(ClickEvent event) {
        //window.setBusy(Messages.get().lockForUpdate());

        try {
            manager = manager.fetchForUpdate();

            setState(UPDATE);
            setData(manager);
            view.name.setFocus(true);
        } catch (Exception e) {
            Window.alert(e.getMessage());
        }
        //window.clearStatus();
    }

    @Handler("commit")
    protected void commit(ClickEvent event) {
        Validation validation;
        
        view.finishEditing();
        
        validation = view.validate();

        if (validation.getStatus() != Validation.Status.VALID) {
            //window.setError(Messages.get().correctErrors());
            return;
        }

        switch (super.state) {
            case QUERY:
                Query query;

                query = new Query();
                query.setFields(view.getQueryFields());
                nav.setQuery(query);
                break;
            case ADD:
                //window.setBusy(Messages.get().adding());
                try {
                    manager = manager.add();

                    setState(DISPLAY);
                    setData(manager);
                    //window.setDone(Messages.get().addingComplete());
                } catch (ValidationErrorsList e) {
                    view.showErrors(e);
                } catch (Exception e) {
                    Window.alert("commitAdd(): " + e.getMessage());
                    //window.clearStatus();
                }
                break;
            case UPDATE:
                //window.setBusy(Messages.get().updating());
                try {
                    manager = manager.update();

                    setState(DISPLAY);
                    setData(manager);
                    //window.setDone(Messages.get().updatingComplete());
                } catch (ValidationErrorsList e) {
                    view.showErrors(e);
                } catch (Exception e) {
                    Window.alert("commitUpdate(): " + e.getMessage());
                    //window.clearStatus();
                }
                break;
            default:
                break;
        }
    }

    @Handler("abort")
    protected void abort(ClickEvent event) {
        view.finishEditing();
        view.clearErrors();
        //window.setBusy(Messages.get().cancelChanges());

        switch (super.state) {
            case QUERY:
                fetchById(null);
                //window.setDone(Messages.get().queryAborted());
                break;
            case ADD:
                fetchById(null);
                //window.setDone(Messages.get().addAborted());
                break;
            case UPDATE:
                try {
                    manager = manager.abortUpdate();
                    setState(DISPLAY);
                    setData(manager);
                } catch (Exception e) {
                    Window.alert(e.getMessage());
                    fetchById(null);
                }
                //window.setDone(Messages.get().updateAborted());
                break;
            default:
                //window.clearStatus();
        }
    }

    protected void orgHistory() {
        IdNameVO hist;

        hist = new IdNameVO(manager.getOrganization().getId(), manager.getOrganization().getName());
//      HistoryScreen.showHistory(Messages.get().orgHistory(), Constants.table().ORGANIZATION, hist);
    }

    protected void orgAddressHistory() {
        IdNameVO hist;
        AddressDO addr;

        addr = manager.getOrganization().getAddress();
        hist = new IdNameVO(addr.getId(), addr.getStreetAddress());
//      HistoryScreen.showHistory(Messages.get().orgAddressHistory(),
//                                Constants.table().ADDRESS,
//                                hist);
    }

    protected void orgContactHistory() {
        int i, count;
        IdNameVO refVoList[];
        OrganizationContactManager man;
        OrganizationContactDO data;

        try {
            man = manager.getContacts();
            count = man.count();
            refVoList = new IdNameVO[count];
            for (i = 0; i < count; i++ ) {
                data = man.getContactAt(i);
                refVoList[i] = new IdNameVO(data.getId(), data.getName());
            }
        } catch (Exception e) {
            e.printStackTrace();
            Window.alert(e.getMessage());
            return;
        }

//        HistoryScreen.showHistory(Messages.get().orgContactHistory(),
//                                  Constants.table().ORGANIZATION_CONTACT,
//                                  refVoList);
    }

    protected void orgContactAddressHistory() {
        int i, count;
        IdNameVO refVoList[];
        OrganizationContactManager man;
        AddressDO addr;

        try {
            man = manager.getContacts();
            count = man.count();
            refVoList = new IdNameVO[count];
            for (i = 0; i < count; i++ ) {
                addr = man.getContactAt(i).getAddress();
                refVoList[i] = new IdNameVO(addr.getId(), addr.getStreetAddress());
            }
        } catch (Exception e) {
            e.printStackTrace();
            Window.alert(e.getMessage());
            return;
        }

//        HistoryScreen.showHistory(Messages.get().orgContactAddressHistory(),
//                                  Constants.table().ADDRESS,
//                                  refVoList);
    }

    protected void orgParameterHistory() {
        int i, count;
        IdNameVO refVoList[];
        OrganizationParameterManager man;
        OrganizationParameterDO data;

        try {
            man = manager.getParameters();
            count = man.count();
            refVoList = new IdNameVO[count];
            for (i = 0; i < count; i++ ) {
                data = man.getParameterAt(i);
                refVoList[i] = new IdNameVO(data.getId(), data.getValue());
            }
        } catch (Exception e) {
            e.printStackTrace();
            Window.alert(e.getMessage());
            return;
        }

//        HistoryScreen.showHistory(Messages.get().orgParameterHistory(),
//                                  Constants.table().ORGANIZATION_PARAMETER,
//                                  refVoList);
    }

    protected boolean fetchById(Integer id) {
        if (id == null) {
            manager = OrganizationManager.getInstance();
            setState(DEFAULT);
        } else {
            //window.setBusy(Messages.get().fetching());
            try {
                switch (tab) {
                    case CONTACT:
                        manager = OrganizationManager.getInstance().fetchWithContacts(id);
                        break;
                    case PARAMETER:
                        manager = OrganizationManager.getInstance().fetchWithParameters(id);
                        break;
                    case NOTE:
                        manager = OrganizationManager.getInstance().fetchWithNotes(id);
                        break;
                }
                setState(DISPLAY);
            } catch (NotFoundException e) {
                fetchById(null);
                //window.setDone(Messages.get().noRecordsFound());
                return false;
            } catch (Exception e) {
                fetchById(null);
                e.printStackTrace();
                Window.alert(Messages.get().fetchFailed() + e.getMessage());
                return false;
            }
        }
        setData(manager);
        //window.clearStatus();

        return true;
    }

    private void drawTabs() {
        switch (tab) {
            case NOTE:
                //notesTab.draw();
                break;
        }
    }

	@Override
	public ModulePermission permissions() {
		return userPermission;
	}
}