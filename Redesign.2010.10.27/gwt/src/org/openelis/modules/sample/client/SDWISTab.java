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
package org.openelis.modules.sample.client;

import java.util.ArrayList;
import java.util.EnumSet;

import org.openelis.cache.DictionaryCache;
import org.openelis.domain.DictionaryDO;
import org.openelis.domain.OrganizationDO;
import org.openelis.domain.PwsDO;
import org.openelis.domain.SampleOrganizationViewDO;
import org.openelis.gwt.common.FieldErrorException;
import org.openelis.gwt.common.TableFieldErrorException;
import org.openelis.gwt.common.ValidationErrorsList;
import org.openelis.gwt.common.data.QueryData;
import org.openelis.gwt.event.ActionEvent;
import org.openelis.gwt.event.ActionHandler;
import org.openelis.gwt.event.DataChangeEvent;
import org.openelis.gwt.event.GetMatchesEvent;
import org.openelis.gwt.event.GetMatchesHandler;
import org.openelis.gwt.event.StateChangeEvent;
import org.openelis.gwt.screen.Screen;
import org.openelis.gwt.screen.ScreenDefInt;
import org.openelis.gwt.screen.ScreenEventHandler;
import org.openelis.gwt.services.ScreenService;
import org.openelis.gwt.widget.Button;
import org.openelis.gwt.widget.AutoComplete;
import org.openelis.gwt.widget.Dropdown;
import org.openelis.gwt.widget.HasExceptions;
import org.openelis.gwt.widget.Item;
import org.openelis.gwt.widget.ModalWindow;
import org.openelis.gwt.widget.QueryFieldUtil;
import org.openelis.gwt.widget.Window;
import org.openelis.gwt.widget.TextBox;
import org.openelis.gwt.widget.table.Row;
import org.openelis.gwt.widget.table.Table;
import org.openelis.manager.SampleManager;
import org.openelis.manager.SampleSDWISManager;
import org.openelis.meta.SampleMeta;
import org.openelis.modules.pws.client.PwsScreen;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;

public class SDWISTab extends Screen {
    private Dropdown<Integer>              sampleTypeId, sampleCategoryId;
    private TextBox                        pwsId, pwsName, facilityId,
                                           samplePointId, pointDesc, collector;
    private TextBox<Integer>               stateLabId;
    private AutoComplete                   reportTo, billTo;
    private Button                         pwsButton, reportToLookup, billToLookup;

    private SampleOrganizationLookupScreen organizationScreen;

    protected ScreenService                orgService, pwsService;

    private SampleManager                  manager;
    private SampleSDWISManager             sdwisManager;

    protected boolean                      loaded = false;

    public SDWISTab(Window window) throws Exception {
        this(null, window);
    }
    
    public SDWISTab(ScreenDefInt def, Window window) throws Exception {
        if (def == null)
            drawScreen((ScreenDefInt)GWT.create(SDWISTabDef.class));
        else
            setDefinition(def);
        
        setWindow(window);
        
        orgService = new ScreenService("controller?service=org.openelis.modules.organization.server.OrganizationService");
        pwsService = new ScreenService("controller?service=org.openelis.modules.pws.server.PwsService");

        initialize();
        initializeDropdowns();
    }

    public void initialize() {
        pwsId = (TextBox)def.getWidget(SampleMeta.getSDWISPwsId());
        addScreenHandler(pwsId, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                pwsId.setValue(getSDWISManager().getSDWIS().getPwsId());
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                PwsDO data;

                getSDWISManager().getSDWIS().setPwsId(event.getValue());

                if (getSDWISManager().getSDWIS().getPwsId() != null) {
                    try {
                        data = pwsService.call("fetchByPwsId", getSDWISManager().getSDWIS().getPwsId());
                        getSDWISManager().getSDWIS().setPwsName(data.getName());
                        pwsName.setValue(data.getName());

                    } catch (ValidationErrorsList e) {
                        showErrors(e);
                        getSDWISManager().getSDWIS().setPwsName(null);
                        pwsName.setValue(null);

                    } catch (Exception e) {
                        com.google.gwt.user.client.Window.alert("pwsId valueChange: " + e.getMessage());
                    }
                } else {
                    getSDWISManager().getSDWIS().setPwsName(null);
                    pwsName.setValue(null);
                }
            }

            public void onStateChange(StateChangeEvent<State> event) {
                pwsId.setEnabled(EnumSet.of(State.QUERY, State.ADD, State.UPDATE)
                                         .contains(event.getState()));
                pwsId.setQueryMode(event.getState() == State.QUERY);
            }
        });

        pwsButton = (Button)def.getWidget("pwsButton");
        addScreenHandler(pwsButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                openPwsScreen();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                pwsButton.setEnabled(EnumSet.of(State.DISPLAY, State.ADD, State.UPDATE)
                                        .contains(event.getState()));
            }
        });

        pwsName = (TextBox)def.getWidget("pwsName");
        addScreenHandler(pwsName, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                pwsName.setValue(getSDWISManager().getSDWIS().getPwsName());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                pwsName.setEnabled(false);
            }
        });

        stateLabId = (TextBox<Integer>)def.getWidget(SampleMeta.getSDWISStateLabId());
        addScreenHandler(stateLabId, new ScreenEventHandler<Integer>() {
            public void onDataChange(DataChangeEvent event) {
                stateLabId.setValue(getSDWISManager().getSDWIS().getStateLabId());
            }

            public void onValueChange(ValueChangeEvent<Integer> event) {
                getSDWISManager().getSDWIS().setStateLabId(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                stateLabId.setEnabled(EnumSet.of(State.QUERY, State.ADD, State.UPDATE)
                                              .contains(event.getState()));
                stateLabId.setQueryMode(event.getState() == State.QUERY);
            }
        });

        facilityId = (TextBox)def.getWidget(SampleMeta.getSDWISFacilityId());
        addScreenHandler(facilityId, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                facilityId.setValue(getSDWISManager().getSDWIS().getFacilityId());
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                getSDWISManager().getSDWIS().setFacilityId(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                facilityId.setEnabled(EnumSet.of(State.QUERY, State.ADD, State.UPDATE)
                                              .contains(event.getState()));
                facilityId.setQueryMode(event.getState() == State.QUERY);
            }
        });

        sampleTypeId = (Dropdown)def.getWidget(SampleMeta.getSDWISSampleTypeId());
        addScreenHandler(sampleTypeId, new ScreenEventHandler<Integer>() {
            public void onDataChange(DataChangeEvent event) {
                sampleTypeId.setValue(getSDWISManager().getSDWIS().getSampleTypeId());
            }

            public void onValueChange(ValueChangeEvent<Integer> event) {
                getSDWISManager().getSDWIS().setSampleTypeId(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                sampleTypeId.setEnabled(EnumSet.of(State.QUERY, State.ADD, State.UPDATE)
                                                .contains(event.getState()));
                sampleTypeId.setQueryMode(event.getState() == State.QUERY);
            }
        });

        samplePointId = (TextBox)def.getWidget(SampleMeta.getSDWISSamplePointId());
        addScreenHandler(samplePointId, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                samplePointId.setValue(getSDWISManager().getSDWIS().getSamplePointId());
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                getSDWISManager().getSDWIS().setSamplePointId(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                samplePointId.setEnabled(EnumSet.of(State.QUERY, State.ADD, State.UPDATE)
                                                 .contains(event.getState()));
                samplePointId.setQueryMode(event.getState() == State.QUERY);
            }
        });

        pointDesc = (TextBox)def.getWidget(SampleMeta.getSDWISLocation());
        addScreenHandler(pointDesc, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                pointDesc.setValue(getSDWISManager().getSDWIS().getLocation());
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                getSDWISManager().getSDWIS().setLocation(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                pointDesc.setEnabled(EnumSet.of(State.QUERY, State.ADD, State.UPDATE)
                                        .contains(event.getState()));
                pointDesc.setQueryMode(event.getState() == State.QUERY);
            }
        });

        sampleCategoryId = (Dropdown)def.getWidget(SampleMeta.getSDWISSampleCategoryId());
        addScreenHandler(sampleCategoryId, new ScreenEventHandler<Integer>() {
            public void onDataChange(DataChangeEvent event) {
                sampleCategoryId.setValue(getSDWISManager().getSDWIS()
                                                                    .getSampleCategoryId());
            }

            public void onValueChange(ValueChangeEvent<Integer> event) {
                getSDWISManager().getSDWIS().setSampleCategoryId(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                sampleCategoryId.setEnabled(EnumSet.of(State.QUERY, State.ADD, State.UPDATE)
                                                    .contains(event.getState()));
                sampleCategoryId.setQueryMode(event.getState() == State.QUERY);
            }
        });

        collector = (TextBox)def.getWidget(SampleMeta.getSDWISCollector());
        addScreenHandler(collector, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                collector.setValue(getSDWISManager().getSDWIS().getCollector());
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                getSDWISManager().getSDWIS().setCollector(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                collector.setEnabled(EnumSet.of(State.QUERY, State.ADD, State.UPDATE)
                                             .contains(event.getState()));
                collector.setQueryMode(event.getState() == State.QUERY);
            }
        });

        reportTo = (AutoComplete)def.getWidget(SampleMeta.getOrgName());
        addScreenHandler(reportTo, new ScreenEventHandler<Integer>() {
            public void onDataChange(DataChangeEvent event) {
                try {
                    SampleOrganizationViewDO reportToOrg = manager.getOrganizations()
                                                                  .getFirstReportTo();

                    if (reportToOrg != null)
                        reportTo.setValue(reportToOrg.getOrganizationId(),
                                              reportToOrg.getOrganizationName());
                    else
                        reportTo.setValue(null, "");

                } catch (Exception e) {
                    com.google.gwt.user.client.Window.alert(e.getMessage());
                }
            }

            public void onValueChange(ValueChangeEvent<Integer> event) {
                Item<Integer> selectedRow = reportTo.getSelectedItem();
                SampleOrganizationViewDO reportToOrg = null;
                try {
                    if (selectedRow.getKey() != null) {
                        reportToOrg = new SampleOrganizationViewDO();
                        reportToOrg.setOrganizationId((Integer)selectedRow.getKey());
                        reportToOrg.setOrganizationName((String)selectedRow.getCell(0));
                        reportToOrg.setOrganizationCity((String)selectedRow.getCell(2));
                        reportToOrg.setOrganizationState((String)selectedRow.getCell(3));
                    }

                    manager.getOrganizations().setReportTo(reportToOrg);

                    reportToOrg = manager.getOrganizations().getFirstReportTo();

                    if (reportToOrg != null)
                        reportTo.setValue(reportToOrg.getOrganizationId(),
                                              reportToOrg.getOrganizationName());
                    else
                        reportTo.setValue(null, "");

                } catch (Exception e) {
                    com.google.gwt.user.client.Window.alert(e.getMessage());
                }
            }

            public void onStateChange(StateChangeEvent<State> event) {
                reportTo.setEnabled(EnumSet.of(State.ADD, State.UPDATE, State.QUERY)
                                       .contains(event.getState()));
                reportTo.setQueryMode(event.getState() == State.QUERY);
            }
        });

        reportTo.addGetMatchesHandler(new GetMatchesHandler() {
            public void onGetMatches(GetMatchesEvent event) {
                getOrganizationMatches(event.getMatch(), reportTo);

            }
        });

        billTo = (AutoComplete)def.getWidget(SampleMeta.getBillTo());
        addScreenHandler(billTo, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                try {
                    SampleOrganizationViewDO billToOrg = manager.getOrganizations()
                                                                .getFirstBillTo();

                    if (billToOrg != null)
                        billTo.setValue(billToOrg.getOrganizationId(),
                                        billToOrg.getOrganizationName());
                    else
                        billTo.setValue(null, "");

                } catch (Exception e) {
                    com.google.gwt.user.client.Window.alert(e.getMessage());
                }
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                Item<Integer> selectedRow = billTo.getSelectedItem();
                SampleOrganizationViewDO billToOrg = null;
                try {
                    if (selectedRow.getKey() != null) {
                        billToOrg = new SampleOrganizationViewDO();
                        billToOrg.setOrganizationId((Integer)selectedRow.getKey());
                        billToOrg.setOrganizationName((String)selectedRow.getCell(0));
                        billToOrg.setOrganizationCity((String)selectedRow.getCell(2));
                        billToOrg.setOrganizationState((String)selectedRow.getCell(3));
                    }

                    manager.getOrganizations().setBillTo(billToOrg);

                    billToOrg = manager.getOrganizations().getFirstBillTo();

                    if (billToOrg != null)
                        billTo.setValue(billToOrg.getOrganizationId(),
                                        billToOrg.getOrganizationName());
                    else
                        billTo.setValue(null, "");

                } catch (Exception e) {
                    com.google.gwt.user.client.Window.alert(e.getMessage());
                }
            }

            public void onStateChange(StateChangeEvent<State> event) {
                billTo.setEnabled(EnumSet.of(State.ADD, State.UPDATE).contains(event.getState()));
                billTo.setQueryMode(event.getState() == State.QUERY);
            }
        });

        billTo.addGetMatchesHandler(new GetMatchesHandler() {
            public void onGetMatches(GetMatchesEvent event) {
                getOrganizationMatches(event.getMatch(), billTo);

            }
        });

        billToLookup = (Button)def.getWidget("billToLookup");
        addScreenHandler(billToLookup, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                onOrganizationLookupClick();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                billToLookup.setEnabled(EnumSet.of(State.ADD, State.UPDATE, State.DISPLAY)
                                           .contains(event.getState()));
            }
        });

        reportToLookup = (Button)def.getWidget("reportToLookup");
        addScreenHandler(reportToLookup, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                onOrganizationLookupClick();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                reportToLookup.setEnabled(EnumSet.of(State.ADD, State.UPDATE, State.DISPLAY)
                                             .contains(event.getState()));
            }
        });
    }

    public ArrayList<QueryData> getQueryFields() {
        QueryData domain;
        ArrayList<QueryData> fields;

        fields = super.getQueryFields();

        if (fields.size() > 0) {
            domain = new QueryData();
            domain.key = SampleMeta.getDomain();
            domain.query = SampleManager.SDWIS_DOMAIN_FLAG;
            domain.type = QueryData.Type.STRING;
            fields.add(domain);
        }

        return fields;
    }

    public void setData(SampleManager manager) {
        this.manager = manager;
        sdwisManager = null;
        loaded = false;
    }

    public void draw() {
        if ( !loaded)
            DataChangeEvent.fire(this);

        loaded = true;
    }

    private void openPwsScreen() {
        PwsScreen pwsScreen;
        ModalWindow modal;

        try {
            final SDWISTab sdwis = this;
            pwsScreen = new PwsScreen((String)pwsId.getValue());

            pwsScreen.addActionHandler(new ActionHandler<PwsScreen.Action>() {
                public void onAction(ActionEvent<PwsScreen.Action> event) {
                    PwsDO pwsDO;
                    if (state == State.ADD || state == State.UPDATE) {
                        if (event.getAction() == PwsScreen.Action.SELECT) {
                            pwsDO = (PwsDO)event.getData();
                            getSDWISManager().getSDWIS().setPwsId(pwsDO.getNumber0());
                            getSDWISManager().getSDWIS().setPwsName(pwsDO.getName());

                            pwsId.clearExceptions();
                            DataChangeEvent.fire(sdwis);
                            setFocus(pwsId);

                        }
                    }
                }
            });

            modal = new ModalWindow();
            modal.setName(consts.get("pwsInformation"));
            modal.setContent(pwsScreen);

        } catch (Exception e) {
            com.google.gwt.user.client.Window.alert("openPWSScreen: " + e.getMessage());
        }
    }

    private void getOrganizationMatches(String match, AutoComplete widget) {
        QueryFieldUtil parser;
        Item<Integer> row;
        OrganizationDO data;
        ArrayList<OrganizationDO> list;
        ArrayList<Item<Integer>> model;

        parser = new QueryFieldUtil();
        try {
        	parser.parse(match);
        }catch(Exception e) {
        	
        }

        window.setBusy();
        try {
            list = orgService.callList("fetchByIdOrName", parser.getParameter().get(0));
            model = new ArrayList<Item<Integer>>();
            for (int i = 0; i < list.size(); i++ ) {
                row = new Item<Integer>(4);
                data = list.get(i);

                row.setKey(data.getId());
                row.setCell(0,data.getName());
                row.setCell(1,data.getAddress().getStreetAddress());
                row.setCell(2,data.getAddress().getCity());
                row.setCell(3,data.getAddress().getState());

                model.add(row);
            }
            widget.showAutoMatches(model);
        } catch (Throwable e) {
            e.printStackTrace();
            com.google.gwt.user.client.Window.alert(e.getMessage());
        }
        window.clearStatus();
    }

    private void onOrganizationLookupClick() {
        try {
            if (organizationScreen == null) {
                final SDWISTab env = this;
                organizationScreen = new SampleOrganizationLookupScreen();

                organizationScreen.addActionHandler(new ActionHandler<SampleOrganizationLookupScreen.Action>() {
                    public void onAction(ActionEvent<SampleOrganizationLookupScreen.Action> event) {
                        if (event.getAction() == SampleOrganizationLookupScreen.Action.OK) {
                            DataChangeEvent.fire(env, reportTo);
                            DataChangeEvent.fire(env, billTo);
                        }
                    }
                });
            }

            ModalWindow modal = new ModalWindow();
            modal.setName(consts.get("sampleOrganization"));
            modal.setContent(organizationScreen);

            organizationScreen.setScreenState(state);
            organizationScreen.setManager(manager.getOrganizations());

        } catch (Exception e) {
            e.printStackTrace();
            com.google.gwt.user.client.Window.alert(e.getMessage());
            return;
        }
    }

    private void initializeDropdowns() {
        ArrayList<Item<Integer>> model;

        // sample type dropdown
        model = new ArrayList<Item<Integer>>();
        model.add(new Item<Integer>(null, ""));
        for (DictionaryDO d : DictionaryCache.getListByCategorySystemName("sdwis_sample_type"))
            model.add(new Item<Integer>(d.getId(), d.getEntry()));

        sampleTypeId.setModel(model);

        // sample category dropdown
        model = new ArrayList<Item<Integer>>();
        model.add(new Item<Integer>(null, ""));
        for (DictionaryDO d : DictionaryCache.getListByCategorySystemName("sdwis_sample_category"))
            model.add(new Item<Integer>(d.getId(), d.getEntry()));

        sampleCategoryId.setModel(model);
    }
    
    public void showErrors(ValidationErrorsList errors) {
        TableFieldErrorException tableE;
        FieldErrorException fieldE;
        Table tableWid;
        HasExceptions field;

        for (Exception ex : errors.getErrorList()) {
            if (ex instanceof TableFieldErrorException) {
                tableE = (TableFieldErrorException) ex;
                tableWid = (Table)def.getWidget(tableE.getTableKey());
                tableWid.addException(tableE.getRowIndex(), tableWid.getColumnByName(tableE.getFieldName()), tableE);
            } else if (ex instanceof FieldErrorException) {
                fieldE = (FieldErrorException)ex;
                field = (HasExceptions)def.getWidget(fieldE.getFieldName());
                
                if(field != null)
                    field.addException(fieldE);
            }
        }
    }

    private SampleSDWISManager getSDWISManager() {
        if (sdwisManager == null) {
            try {
                sdwisManager = (SampleSDWISManager)manager.getDomainManager();
            } catch (Exception e) {
                sdwisManager = SampleSDWISManager.getInstance();
                manager = SampleManager.getInstance();
                manager.getSample().setDomain(SampleManager.SDWIS_DOMAIN_FLAG);
            }
        }

        return sdwisManager;
    }
}
