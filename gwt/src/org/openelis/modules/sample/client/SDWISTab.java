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
import org.openelis.gwt.widget.AppButton;
import org.openelis.gwt.widget.AutoComplete;
import org.openelis.gwt.widget.Dropdown;
import org.openelis.gwt.widget.HasField;
import org.openelis.gwt.widget.QueryFieldUtil;
import org.openelis.gwt.widget.ScreenWindow;
import org.openelis.gwt.widget.TextBox;
import org.openelis.gwt.widget.table.TableDataRow;
import org.openelis.gwt.widget.table.TableWidget;
import org.openelis.manager.SampleManager;
import org.openelis.manager.SampleSDWISManager;
import org.openelis.meta.SampleMeta;
import org.openelis.modules.pws.client.PwsScreen;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.user.client.Window;

public class SDWISTab extends Screen {
    private Dropdown<Integer>              sampleTypeId, sampleCategoryId;
    private TextBox                        pwsId, pwsName, facilityId,
                                           samplePointId, pointDesc, collector;
    private TextBox<Integer>               stateLabId;
    private AutoComplete<Integer>          reportTo, billTo;
    private AppButton                      pwsButton, reportToLookup, billToLookup;

    private SampleOrganizationLookupScreen organizationScreen;

    protected ScreenService                orgService, pwsService;

    private SampleManager                  manager;
    private SampleSDWISManager             sdwisManager;

    private Integer                        sampleReleasedId;

    protected boolean                      loaded = false;

    public SDWISTab(ScreenWindow window) throws Exception {
        this(null, window);
    }
    
    public SDWISTab(ScreenDefInt def, ScreenWindow window) throws Exception {
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
                        Window.alert("pwsId valueChange: " + e.getMessage());
                    }
                } else {
                    getSDWISManager().getSDWIS().setPwsName(null);
                    pwsName.setValue(null);
                }
            }

            public void onStateChange(StateChangeEvent<State> event) {
                pwsId.enable(event.getState() == State.QUERY ||
                             (canEdit() && EnumSet.of(State.ADD, State.UPDATE).contains(event.getState())));
                pwsId.setQueryMode(event.getState() == State.QUERY);
            }
        });

        pwsButton = (AppButton)def.getWidget("pwsButton");
        addScreenHandler(pwsButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                openPwsScreen();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                pwsButton.enable(event.getState() == State.DISPLAY ||
                                 (canEdit() && EnumSet.of(State.ADD, State.UPDATE)
                                                      .contains(event.getState())));
            }
        });

        pwsName = (TextBox)def.getWidget("pwsName");
        addScreenHandler(pwsName, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                pwsName.setValue(getSDWISManager().getSDWIS().getPwsName());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                pwsName.enable(false);
                pwsName.setQueryMode(event.getState() == State.QUERY);
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
                stateLabId.enable(event.getState() == State.QUERY ||
                                  (canEdit() && EnumSet.of(State.ADD, State.UPDATE)
                                                       .contains(event.getState())));
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
                facilityId.enable(event.getState() == State.QUERY ||
                                  (canEdit() && EnumSet.of(State.ADD, State.UPDATE)
                                                       .contains(event.getState())));
                facilityId.setQueryMode(event.getState() == State.QUERY);
            }
        });

        sampleTypeId = (Dropdown)def.getWidget(SampleMeta.getSDWISSampleTypeId());
        addScreenHandler(sampleTypeId, new ScreenEventHandler<Integer>() {
            public void onDataChange(DataChangeEvent event) {
                sampleTypeId.setSelection(getSDWISManager().getSDWIS().getSampleTypeId());
            }

            public void onValueChange(ValueChangeEvent<Integer> event) {
                getSDWISManager().getSDWIS().setSampleTypeId(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                sampleTypeId.enable(event.getState() == State.QUERY ||
                                    (canEdit() && EnumSet.of(State.ADD, State.UPDATE)
                                                         .contains(event.getState())));
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
                samplePointId.enable(event.getState() == State.QUERY ||
                                     (canEdit() && EnumSet.of(State.ADD, State.UPDATE)
                                                          .contains(event.getState())));
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
                pointDesc.enable(event.getState() == State.QUERY ||
                                 (canEdit() && EnumSet.of(State.ADD, State.UPDATE)
                                                      .contains(event.getState())));
                pointDesc.setQueryMode(event.getState() == State.QUERY);
            }
        });

        sampleCategoryId = (Dropdown)def.getWidget(SampleMeta.getSDWISSampleCategoryId());
        addScreenHandler(sampleCategoryId, new ScreenEventHandler<Integer>() {
            public void onDataChange(DataChangeEvent event) {
                sampleCategoryId.setSelection(getSDWISManager().getSDWIS()
                                                                    .getSampleCategoryId());
            }

            public void onValueChange(ValueChangeEvent<Integer> event) {
                getSDWISManager().getSDWIS().setSampleCategoryId(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                sampleCategoryId.enable(event.getState() == State.QUERY ||
                                        (canEdit() && EnumSet.of(State.ADD, State.UPDATE)
                                                             .contains(event.getState())));
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
                collector.enable(event.getState() == State.QUERY ||
                                 (canEdit() && EnumSet.of(State.ADD, State.UPDATE)
                                                      .contains(event.getState())));
                collector.setQueryMode(event.getState() == State.QUERY);
            }
        });

        reportTo = (AutoComplete<Integer>)def.getWidget(SampleMeta.getOrgName());
        addScreenHandler(reportTo, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                try {
                    SampleOrganizationViewDO reportToOrg = manager.getOrganizations()
                                                                  .getReportTo();

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
                TableDataRow selectedRow;
                SampleOrganizationViewDO data;                              

                selectedRow = reportTo.getSelection();
                try {
                    if (selectedRow == null || selectedRow.key == null) {
                        manager.getOrganizations().removeReportTo();
                        reportTo.setSelection(null, "");
                        return;
                    }

                    data = manager.getOrganizations().getReportTo();
                    if (data == null) {
                        data = new SampleOrganizationViewDO();
                        manager.getOrganizations().setReportTo(data);
                    }

                    data.setOrganizationId((Integer)selectedRow.key);
                    data.setOrganizationName((String)selectedRow.cells.get(0).value);
                    data.setOrganizationCity((String)selectedRow.cells.get(2).value);
                    data.setOrganizationState((String)selectedRow.cells.get(3).value);

                    reportTo.setSelection(data.getOrganizationId(),
                                        data.getOrganizationName());

                } catch (Exception e) {
                    Window.alert(e.getMessage());
                }
            }

            public void onStateChange(StateChangeEvent<State> event) {
                reportTo.enable(event.getState() == State.QUERY ||
                                (canEdit() && EnumSet.of(State.ADD, State.UPDATE)
                                                     .contains(event.getState())));
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
                                                                .getBillTo();

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
                TableDataRow selectedRow;
                SampleOrganizationViewDO data;

                selectedRow = billTo.getSelection();

                try {
                    if (selectedRow == null || selectedRow.key == null) {
                        manager.getOrganizations().removeBillTo();
                        billTo.setSelection(null, "");
                        return;
                    }

                    data = manager.getOrganizations().getBillTo();
                    if (data == null) {
                        data = new SampleOrganizationViewDO();
                        manager.getOrganizations().setBillTo(data);
                    }

                    data.setOrganizationId((Integer)selectedRow.key);
                    data.setOrganizationName((String)selectedRow.cells.get(0).value);
                    data.setOrganizationCity((String)selectedRow.cells.get(2).value);
                    data.setOrganizationState((String)selectedRow.cells.get(3).value);

                    billTo.setSelection(data.getOrganizationId(),
                                        data.getOrganizationName());

                } catch (Exception e) {
                    Window.alert(e.getMessage());
                }
            }

            public void onStateChange(StateChangeEvent<State> event) {
                billTo.enable(event.getState() == State.QUERY ||
                              (canEdit() && EnumSet.of(State.ADD, State.UPDATE).contains(event.getState())));
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
                billToLookup.enable(event.getState() == State.DISPLAY ||
                                    (canEdit() && EnumSet.of(State.ADD, State.UPDATE)
                                                         .contains(event.getState())));
            }
        });

        reportToLookup = (AppButton)def.getWidget("reportToLookup");
        addScreenHandler(reportToLookup, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                onOrganizationLookupClick();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                reportToLookup.enable(event.getState() == State.DISPLAY ||
                                      (canEdit() && EnumSet.of(State.ADD, State.UPDATE)
                                                           .contains(event.getState())));
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
        ScreenWindow modal;

        try {
            final SDWISTab sdwis = this;
            pwsScreen = new PwsScreen(pwsId.getValue());

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

            modal = new ScreenWindow(ScreenWindow.Mode.LOOK_UP);
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

        try {
            sampleReleasedId = DictionaryCache.getIdFromSystemName("sample_released");
        } catch (Exception e) {
            Window.alert(e.getMessage());
            window.close();
        }

        // sample type dropdown
        model = new ArrayList<TableDataRow>();
        model.add(new TableDataRow(null, ""));
        for (DictionaryDO d : DictionaryCache.getListByCategorySystemName("sdwis_sample_type"))
            model.add(new TableDataRow(d.getId(), d.getEntry()));

        sampleTypeId.setModel(model);

        // sample category dropdown
        model = new ArrayList<TableDataRow>();
        model.add(new TableDataRow(null, ""));
        for (DictionaryDO d : DictionaryCache.getListByCategorySystemName("sdwis_sample_category"))
            model.add(new TableDataRow(d.getId(), d.getEntry()));

        sampleCategoryId.setModel(model);
    }
    
    private boolean canEdit() {
        return (manager != null && !sampleReleasedId.equals(manager.getSample().getStatusId()));
    }
    
    public void showErrors(ValidationErrorsList errors) {
        TableFieldErrorException tableE;
        FieldErrorException fieldE;
        TableWidget tableWid;
        HasField field;

        for (Exception ex : errors.getErrorList()) {
            if (ex instanceof TableFieldErrorException) {
                tableE = (TableFieldErrorException) ex;
                tableWid = (TableWidget)def.getWidget(tableE.getTableKey());
                tableWid.setCellException(tableE.getRowIndex(), tableE.getFieldName(), tableE);
            } else if (ex instanceof FieldErrorException) {
                fieldE = (FieldErrorException)ex;
                field = (HasField)def.getWidget(fieldE.getFieldName());
                
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
