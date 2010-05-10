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
import org.openelis.gwt.widget.AppButton;
import org.openelis.gwt.widget.AutoComplete;
import org.openelis.gwt.widget.Dropdown;
import org.openelis.gwt.widget.QueryFieldUtil;
import org.openelis.gwt.widget.ScreenWindow;
import org.openelis.gwt.widget.TextBox;
import org.openelis.gwt.widget.table.TableDataRow;
import org.openelis.manager.SampleManager;
import org.openelis.manager.SampleSDWISManager;
import org.openelis.meta.SampleMeta;
import org.openelis.modules.pws.client.PwsScreen;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.user.client.Window;

public class SDWISTab extends Screen {
    private Dropdown<Integer>              sDWISSampleTypeId, sDWISSampleCategoryId;
    private TextBox                        sDWISPwsId, pwsName, sDWISFacilityId,
                    sDWISSamplePointId, pointDesc, sDWISCollector;
    private TextBox<Integer>               sDWISStateLabId;
    private AutoComplete<Integer>          reportTo, billTo;
    private AppButton                      pwsButton, reportToLookup, billToLookup;

    private SampleOrganizationLookupScreen organizationScreen;

    protected ScreenService                orgService;

    private SampleManager                  manager;
    private SampleSDWISManager             sdwisManager;

    protected boolean                      loaded = false;

    public SDWISTab(ScreenDefInt def, ScreenWindow window) {
        setDefinition(def);
        setWindow(window);

        orgService = new ScreenService(
                                       "controller?service=org.openelis.modules.organization.server.OrganizationService");

        initialize();
        initializeDropdowns();
    }

    public SDWISTab(ScreenWindow window) throws Exception {
        drawScreen((ScreenDefInt)GWT.create(SDWISTabDef.class));
        setWindow(window);

        orgService = new ScreenService(
                                       "controller?service=org.openelis.modules.organization.server.OrganizationService");

        initialize();
        initializeDropdowns();
    }

    public void initialize() {
        sDWISPwsId = (TextBox)def.getWidget(SampleMeta.getSDWISPwsId());
        addScreenHandler(sDWISPwsId, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                sDWISPwsId.setValue(getSDWISManager().getSDWIS().getPwsId());
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                PwsDO pwsDO;

                getSDWISManager().getSDWIS().setPwsId(event.getValue());

                if (getSDWISManager().getSDWIS().getPwsId() != null) {
                    try {
                        pwsDO = getSDWISManager().validatePwsId(event.getValue());
                        getSDWISManager().getSDWIS().setPwsName(pwsDO.getName());
                        pwsName.setValue(pwsDO.getName());

                    } catch (ValidationErrorsList e) {
                        showErrors(e);
                        getSDWISManager().getSDWIS().setPwsName(null);
                        pwsName.setValue(null);

                    } catch (Exception e) {
                        Window.alert("pwsId valueChange: " + e.getMessage());
                    }
                } else {
                    getSDWISManager().getSDWIS().setPwsName(null);
                    pwsName.setValue(null);
                }
            }

            public void onStateChange(StateChangeEvent<State> event) {
                sDWISPwsId.enable(EnumSet.of(State.QUERY, State.ADD, State.UPDATE)
                                         .contains(event.getState()));
                sDWISPwsId.setQueryMode(event.getState() == State.QUERY);
            }
        });

        pwsButton = (AppButton)def.getWidget("pwsButton");
        addScreenHandler(pwsButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                openPwsScreen();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                pwsButton.enable(EnumSet.of(State.DISPLAY, State.ADD, State.UPDATE)
                                        .contains(event.getState()));
            }
        });

        pwsName = (TextBox)def.getWidget("pwsName");
        addScreenHandler(pwsName, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                pwsName.setValue(getSDWISManager().getSDWIS().getPwsName());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                pwsName.enable(false);
            }
        });

        sDWISStateLabId = (TextBox<Integer>)def.getWidget(SampleMeta.getSDWISStateLabId());
        addScreenHandler(sDWISStateLabId, new ScreenEventHandler<Integer>() {
            public void onDataChange(DataChangeEvent event) {
                sDWISStateLabId.setValue(getSDWISManager().getSDWIS().getStateLabId());
            }

            public void onValueChange(ValueChangeEvent<Integer> event) {
                getSDWISManager().getSDWIS().setStateLabId(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                sDWISStateLabId.enable(EnumSet.of(State.QUERY, State.ADD, State.UPDATE)
                                              .contains(event.getState()));
                sDWISStateLabId.setQueryMode(event.getState() == State.QUERY);
            }
        });

        sDWISFacilityId = (TextBox)def.getWidget(SampleMeta.getSDWISFacilityId());
        addScreenHandler(sDWISFacilityId, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                sDWISFacilityId.setValue(getSDWISManager().getSDWIS().getFacilityId());
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                getSDWISManager().getSDWIS().setFacilityId(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                sDWISFacilityId.enable(EnumSet.of(State.QUERY, State.ADD, State.UPDATE)
                                              .contains(event.getState()));
                sDWISFacilityId.setQueryMode(event.getState() == State.QUERY);
            }
        });

        sDWISSampleTypeId = (Dropdown)def.getWidget(SampleMeta.getSDWISSampleTypeId());
        addScreenHandler(sDWISSampleTypeId, new ScreenEventHandler<Integer>() {
            public void onDataChange(DataChangeEvent event) {
                sDWISSampleTypeId.setSelection(getSDWISManager().getSDWIS().getSampleTypeId());
            }

            public void onValueChange(ValueChangeEvent<Integer> event) {
                getSDWISManager().getSDWIS().setSampleTypeId(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                sDWISSampleTypeId.enable(EnumSet.of(State.QUERY, State.ADD, State.UPDATE)
                                                .contains(event.getState()));
                sDWISSampleTypeId.setQueryMode(event.getState() == State.QUERY);
            }
        });

        sDWISSamplePointId = (TextBox)def.getWidget(SampleMeta.getSDWISSamplePointId());
        addScreenHandler(sDWISSamplePointId, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                sDWISSamplePointId.setValue(getSDWISManager().getSDWIS().getSamplePointId());
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                getSDWISManager().getSDWIS().setSamplePointId(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                sDWISSamplePointId.enable(EnumSet.of(State.QUERY, State.ADD, State.UPDATE)
                                                 .contains(event.getState()));
                sDWISSamplePointId.setQueryMode(event.getState() == State.QUERY);
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
                pointDesc.enable(EnumSet.of(State.QUERY, State.ADD, State.UPDATE)
                                        .contains(event.getState()));
                pointDesc.setQueryMode(event.getState() == State.QUERY);
            }
        });

        sDWISSampleCategoryId = (Dropdown)def.getWidget(SampleMeta.getSDWISSampleCategoryId());
        addScreenHandler(sDWISSampleCategoryId, new ScreenEventHandler<Integer>() {
            public void onDataChange(DataChangeEvent event) {
                sDWISSampleCategoryId.setSelection(getSDWISManager().getSDWIS()
                                                                    .getSampleCategoryId());
            }

            public void onValueChange(ValueChangeEvent<Integer> event) {
                getSDWISManager().getSDWIS().setSampleCategoryId(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                sDWISSampleCategoryId.enable(EnumSet.of(State.QUERY, State.ADD, State.UPDATE)
                                                    .contains(event.getState()));
                sDWISSampleCategoryId.setQueryMode(event.getState() == State.QUERY);
            }
        });

        sDWISCollector = (TextBox)def.getWidget(SampleMeta.getSDWISCollector());
        addScreenHandler(sDWISCollector, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                sDWISCollector.setValue(getSDWISManager().getSDWIS().getCollector());
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                getSDWISManager().getSDWIS().setCollector(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                sDWISCollector.enable(EnumSet.of(State.QUERY, State.ADD, State.UPDATE)
                                             .contains(event.getState()));
                sDWISCollector.setQueryMode(event.getState() == State.QUERY);
            }
        });

        reportTo = (AutoComplete<Integer>)def.getWidget(SampleMeta.getOrgName());
        addScreenHandler(reportTo, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                try {
                    SampleOrganizationViewDO reportToOrg = manager.getOrganizations()
                                                                  .getFirstReportTo();

                    if (reportToOrg != null)
                        reportTo.setSelection(reportToOrg.getOrganizationId(),
                                              reportToOrg.getOrganizationName());
                    else
                        reportTo.setSelection(null, "");

                } catch (Exception e) {
                    Window.alert(e.getMessage());
                }
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                TableDataRow selectedRow = reportTo.getSelection();
                SampleOrganizationViewDO reportToOrg = null;
                try {
                    if (selectedRow.key != null) {
                        reportToOrg = new SampleOrganizationViewDO();
                        reportToOrg.setOrganizationId((Integer)selectedRow.key);
                        reportToOrg.setOrganizationName((String)selectedRow.cells.get(0).value);
                        reportToOrg.setOrganizationCity((String)selectedRow.cells.get(2).value);
                        reportToOrg.setOrganizationState((String)selectedRow.cells.get(3).value);
                    }

                    manager.getOrganizations().setReportTo(reportToOrg);

                    reportToOrg = manager.getOrganizations().getFirstReportTo();

                    if (reportToOrg != null)
                        reportTo.setSelection(reportToOrg.getOrganizationId(),
                                              reportToOrg.getOrganizationName());
                    else
                        reportTo.setSelection(null, "");

                } catch (Exception e) {
                    Window.alert(e.getMessage());
                }
            }

            public void onStateChange(StateChangeEvent<State> event) {
                reportTo.enable(EnumSet.of(State.ADD, State.UPDATE, State.QUERY)
                                       .contains(event.getState()));
                reportTo.setQueryMode(event.getState() == State.QUERY);
            }
        });

        reportTo.addGetMatchesHandler(new GetMatchesHandler() {
            public void onGetMatches(GetMatchesEvent event) {
                getOrganizationMatches(event.getMatch(), reportTo);

            }
        });

        billTo = (AutoComplete<Integer>)def.getWidget(SampleMeta.getBillTo());
        addScreenHandler(billTo, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                try {
                    SampleOrganizationViewDO billToOrg = manager.getOrganizations()
                                                                .getFirstBillTo();

                    if (billToOrg != null)
                        billTo.setSelection(billToOrg.getOrganizationId(),
                                            billToOrg.getOrganizationName());
                    else
                        billTo.setSelection(null, "");

                } catch (Exception e) {
                    Window.alert(e.getMessage());
                }
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                TableDataRow selectedRow = billTo.getSelection();
                SampleOrganizationViewDO billToOrg = null;
                try {
                    if (selectedRow.key != null) {
                        billToOrg = new SampleOrganizationViewDO();
                        billToOrg.setOrganizationId((Integer)selectedRow.key);
                        billToOrg.setOrganizationName((String)selectedRow.cells.get(0).value);
                        billToOrg.setOrganizationCity((String)selectedRow.cells.get(2).value);
                        billToOrg.setOrganizationState((String)selectedRow.cells.get(3).value);
                    }

                    manager.getOrganizations().setBillTo(billToOrg);

                    billToOrg = manager.getOrganizations().getFirstBillTo();

                    if (billToOrg != null)
                        billTo.setSelection(billToOrg.getOrganizationId(),
                                            billToOrg.getOrganizationName());
                    else
                        billTo.setSelection(null, "");

                } catch (Exception e) {
                    Window.alert(e.getMessage());
                }
            }

            public void onStateChange(StateChangeEvent<State> event) {
                billTo.enable(EnumSet.of(State.ADD, State.UPDATE).contains(event.getState()));
                billTo.setQueryMode(event.getState() == State.QUERY);
            }
        });

        billTo.addGetMatchesHandler(new GetMatchesHandler() {
            public void onGetMatches(GetMatchesEvent event) {
                getOrganizationMatches(event.getMatch(), billTo);

            }
        });

        billToLookup = (AppButton)def.getWidget("billToLookup");
        addScreenHandler(billToLookup, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                onOrganizationLookupClick();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                billToLookup.enable(EnumSet.of(State.ADD, State.UPDATE, State.DISPLAY)
                                           .contains(event.getState()));
            }
        });

        reportToLookup = (AppButton)def.getWidget("reportToLookup");
        addScreenHandler(reportToLookup, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                onOrganizationLookupClick();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                reportToLookup.enable(EnumSet.of(State.ADD, State.UPDATE, State.DISPLAY)
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

        try {
            final SDWISTab sdwis = this;
            pwsScreen = new PwsScreen(sDWISPwsId.getValue());

            pwsScreen.addActionHandler(new ActionHandler<PwsScreen.Action>() {
                public void onAction(ActionEvent<PwsScreen.Action> event) {
                    PwsDO pwsDO;
                    if (state == State.ADD || state == State.UPDATE) {
                        if (event.getAction() == PwsScreen.Action.SELECT) {
                            pwsDO = (PwsDO)event.getData();
                            getSDWISManager().getSDWIS().setPwsId(pwsDO.getNumber0());
                            getSDWISManager().getSDWIS().setPwsName(pwsDO.getName());

                            sDWISPwsId.clearExceptions();
                            DataChangeEvent.fire(sdwis);
                            setFocus(sDWISPwsId);

                        }
                    }
                }
            });

            ScreenWindow modal = new ScreenWindow(ScreenWindow.Mode.LOOK_UP);
            modal.setName(consts.get("pwsInformation"));
            modal.setContent(pwsScreen);

        } catch (Exception e) {
            Window.alert("openPWSScreen: " + e.getMessage());
        }
    }

    private void getOrganizationMatches(String match, AutoComplete widget) {
        QueryFieldUtil parser;
        TableDataRow row;
        OrganizationDO data;
        ArrayList<OrganizationDO> list;
        ArrayList<TableDataRow> model;

        parser = new QueryFieldUtil();
        parser.parse(match);

        window.setBusy();
        try {
            list = orgService.callList("fetchByIdOrName", parser.getParameter().get(0));
            model = new ArrayList<TableDataRow>();
            for (int i = 0; i < list.size(); i++ ) {
                row = new TableDataRow(4);
                data = list.get(i);

                row.key = data.getId();
                row.cells.get(0).value = data.getName();
                row.cells.get(1).value = data.getAddress().getStreetAddress();
                row.cells.get(2).value = data.getAddress().getCity();
                row.cells.get(3).value = data.getAddress().getState();

                model.add(row);
            }
            widget.showAutoMatches(model);
        } catch (Throwable e) {
            e.printStackTrace();
            Window.alert(e.getMessage());
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

            ScreenWindow modal = new ScreenWindow(ScreenWindow.Mode.DIALOG);
            modal.setName(consts.get("sampleOrganization"));
            modal.setContent(organizationScreen);

            organizationScreen.setScreenState(state);
            organizationScreen.setManager(manager.getOrganizations());

        } catch (Exception e) {
            e.printStackTrace();
            Window.alert(e.getMessage());
            return;
        }
    }

    private void initializeDropdowns() {
        ArrayList<TableDataRow> model;

        // sample type dropdown
        model = new ArrayList<TableDataRow>();
        model.add(new TableDataRow(null, ""));
        for (DictionaryDO d : DictionaryCache.getListByCategorySystemName("sdwis_sample_type"))
            model.add(new TableDataRow(d.getId(), d.getEntry()));

        sDWISSampleTypeId.setModel(model);

        // sample category dropdown
        model = new ArrayList<TableDataRow>();
        model.add(new TableDataRow(null, ""));
        for (DictionaryDO d : DictionaryCache.getListByCategorySystemName("sdwis_sample_category"))
            model.add(new TableDataRow(d.getId(), d.getEntry()));

        sDWISSampleCategoryId.setModel(model);
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
