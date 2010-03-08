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
import org.openelis.domain.AddressDO;
import org.openelis.domain.DictionaryDO;
import org.openelis.domain.OrganizationDO;
import org.openelis.domain.ProjectDO;
import org.openelis.domain.SamplePrivateWellViewDO;
import org.openelis.domain.SampleProjectViewDO;
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
import org.openelis.manager.SamplePrivateWellManager;
import org.openelis.meta.SampleMeta;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.user.client.Window;

public class PrivateWellTab extends Screen {

    private TextBox                   addressMultipleUnit, addressStreetAddress, addressCity,
                    addressWorkPhone, addressZipCode, addressFaxPhone, wellLocation,
                    locationAddrMultipleUnit, locationAddrStreetAddress, locationAddrCity,
                    locationAddrZipCode, wellOwner, wellCollector;
    private TextBox<Integer>          wellOrganizationId, wellWellNumber;
    private AutoComplete<Integer>     orgName;
    private Dropdown<String>          addressState, locationAddrState;
    private AutoComplete<Integer>     projectName;
    private AppButton                 projectLookup;

    private SampleProjectLookupScreen projectScreen;

    protected ScreenService           orgService;
    protected ScreenService           projectService;

    private SampleManager             manager;

    protected boolean                 loaded = false;

    public PrivateWellTab(ScreenDefInt def, ScreenWindow window) {
        setDefinition(def);
        setWindow(window);

        orgService = new ScreenService("controller?service=org.openelis.modules.organization.server.OrganizationService");
        projectService = new ScreenService("controller?service=org.openelis.modules.project.server.ProjectService");

        initialize();
        initializeDropdowns();
    }

    public void initialize() {
        orgName = (AutoComplete<Integer>)def.getWidget(SampleMeta.getOrgName());
        addScreenHandler(orgName, new ScreenEventHandler<Integer>() {
            public void onDataChange(DataChangeEvent event) {
                if (getWellManager().getPrivateWell().getOrganizationId() == null)
                    orgName.setSelection(null, getWellManager().getPrivateWell().getReportToName());
                else
                    orgName.setSelection(getWellManager().getPrivateWell().getOrganizationId(),
                                         getWellManager().getPrivateWell().getOrgName());
            }

            public void onValueChange(ValueChangeEvent<Integer> event) {
                OrganizationDO orgDO;
                SamplePrivateWellViewDO wellDO;
                boolean enableAddressValues = false;

                wellDO = getWellManager().getPrivateWell();
                if (event.getValue() != null) {
                    orgDO = (OrganizationDO)orgName.getSelection().data;
                    wellOrganizationId.setValue(getString(event.getValue()));
                    addressMultipleUnit.setValue(orgDO.getAddress().getMultipleUnit());
                    addressStreetAddress.setValue(orgDO.getAddress().getStreetAddress());
                    addressCity.setValue(orgDO.getAddress().getCity());
                    addressState.setValue(orgDO.getAddress().getState());
                    addressZipCode.setValue(orgDO.getAddress().getZipCode());
                    addressWorkPhone.setValue(orgDO.getAddress().getWorkPhone());
                    addressFaxPhone.setValue(orgDO.getAddress().getFaxPhone());
                    enableAddressValues = false;

                    wellDO.setOrganizationId(event.getValue());
                    wellDO.setOrgName(orgDO.getName());
                    wellDO.setReportToName(null);
                    wellDO.setReportToAddressId(null);
                    getWellManager().setOrganizationAddress(orgDO.getAddress());
                } else {
                    wellOrganizationId.setValue(getString(event.getValue()));
                    addressMultipleUnit.setValue(null);
                    addressStreetAddress.setValue(null);
                    addressCity.setValue(null);
                    addressState.setValue(null);
                    addressZipCode.setValue(null);
                    addressWorkPhone.setValue(null);
                    addressFaxPhone.setValue(null);
                    enableAddressValues = true;

                    wellDO.setReportToName((String)orgName.getSelection().getCells().get(0));
                    wellDO.setReportToAddressId(null);
                    wellDO.setOrganizationId(event.getValue());
                    wellDO.setOrgName(null);
                    getWellManager().setReportToAddress(new AddressDO());
                }

                addressMultipleUnit.enable(enableAddressValues);
                addressStreetAddress.enable(enableAddressValues);
                addressCity.enable(enableAddressValues);
                addressState.enable(enableAddressValues);
                addressZipCode.enable(enableAddressValues);
                addressWorkPhone.enable(enableAddressValues);
                addressFaxPhone.enable(enableAddressValues);
            }

            public void onStateChange(StateChangeEvent<State> event) {
                orgName.enable(EnumSet.of(State.QUERY, State.ADD, State.UPDATE)
                                      .contains(event.getState()));
                orgName.setQueryMode(event.getState() == State.QUERY);
            }
        });

        orgName.addGetMatchesHandler(new GetMatchesHandler() {
            public void onGetMatches(GetMatchesEvent event) {
                QueryFieldUtil parser;
                TableDataRow row;
                OrganizationDO data;
                ArrayList<OrganizationDO> list;
                ArrayList<TableDataRow> model;

                parser = new QueryFieldUtil();
                parser.parse(event.getMatch());

                window.setBusy();
                try {
                    list = orgService.callList("fetchByIdOrName", parser.getParameter().get(0));
                    model = new ArrayList<TableDataRow>();
                    model.add(row = new TableDataRow(null, event.getMatch(), null, null, null));

                    for (int i = 0; i < list.size(); i++ ) {
                        row = new TableDataRow(4);
                        data = list.get(i);

                        row.key = data.getId();
                        row.cells.get(0).value = data.getName();
                        row.cells.get(1).value = data.getAddress().getStreetAddress();
                        row.cells.get(2).value = data.getAddress().getCity();
                        row.cells.get(3).value = data.getAddress().getState();
                        row.data = data;

                        model.add(row);
                    }
                    orgName.showAutoMatches(model);
                } catch (Throwable e) {
                    e.printStackTrace();
                    Window.alert(e.getMessage());
                }
                window.clearStatus();
            }
        });

        wellOrganizationId = (TextBox<Integer>)def.getWidget(SampleMeta.getWellOrganizationId());
        addScreenHandler(wellOrganizationId, new ScreenEventHandler<Integer>() {
            public void onDataChange(DataChangeEvent event) {
                wellOrganizationId.setValue(getString(getWellManager().getPrivateWell()
                                                                      .getOrganizationId()));
            }

            public void onValueChange(ValueChangeEvent<Integer> event) {
                getWellManager().getPrivateWell().setOrganizationId(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                wellOrganizationId.enable(EnumSet.of(State.QUERY).contains(event.getState()));
                wellOrganizationId.setQueryMode(event.getState() == State.QUERY);
            }
        });

        addressMultipleUnit = (TextBox)def.getWidget(SampleMeta.getAddressMultipleUnit());
        addScreenHandler(addressMultipleUnit, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                addressMultipleUnit.setValue(getWellManager().getAddress().getMultipleUnit());
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                getWellManager().getAddress().setMultipleUnit(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                addressMultipleUnit.enable(EnumSet.of(State.QUERY, State.ADD, State.UPDATE)
                                                  .contains(event.getState()));
                addressMultipleUnit.setQueryMode(event.getState() == State.QUERY);
            }
        });

        addressStreetAddress = (TextBox)def.getWidget(SampleMeta.getAddressStreetAddress());
        addScreenHandler(addressStreetAddress, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                addressStreetAddress.setValue(getWellManager().getAddress().getStreetAddress());
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                getWellManager().getAddress().setStreetAddress(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                addressStreetAddress.enable(EnumSet.of(State.QUERY, State.ADD, State.UPDATE)
                                                   .contains(event.getState()));
                addressStreetAddress.setQueryMode(event.getState() == State.QUERY);
            }
        });

        addressCity = (TextBox)def.getWidget(SampleMeta.getAddressCity());
        addScreenHandler(addressCity, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                addressCity.setValue(getWellManager().getAddress().getCity());
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                getWellManager().getAddress().setCity(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                addressCity.enable(EnumSet.of(State.QUERY, State.ADD, State.UPDATE)
                                          .contains(event.getState()));
                addressCity.setQueryMode(event.getState() == State.QUERY);
            }
        });

        addressWorkPhone = (TextBox)def.getWidget(SampleMeta.getAddressWorkPhone());
        addScreenHandler(addressWorkPhone, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                addressWorkPhone.setValue(getWellManager().getAddress().getWorkPhone());
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                getWellManager().getAddress().setWorkPhone(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                addressWorkPhone.enable(EnumSet.of(State.QUERY, State.ADD, State.UPDATE)
                                               .contains(event.getState()));
                addressWorkPhone.setQueryMode(event.getState() == State.QUERY);
            }
        });

        addressState = (Dropdown)def.getWidget(SampleMeta.getAddressState());
        addScreenHandler(addressState, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                addressState.setSelection(getWellManager().getAddress().getState());
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                getWellManager().getAddress().setState(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                addressState.enable(EnumSet.of(State.QUERY, State.ADD, State.UPDATE)
                                           .contains(event.getState()));
                addressState.setQueryMode(event.getState() == State.QUERY);
            }
        });

        addressZipCode = (TextBox)def.getWidget(SampleMeta.getAddressZipCode());
        addScreenHandler(addressZipCode, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                addressZipCode.setValue(getWellManager().getAddress().getZipCode());
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                getWellManager().getAddress().setZipCode(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                addressZipCode.enable(EnumSet.of(State.QUERY, State.ADD, State.UPDATE)
                                             .contains(event.getState()));
                addressZipCode.setQueryMode(event.getState() == State.QUERY);
            }
        });

        addressFaxPhone = (TextBox)def.getWidget(SampleMeta.getAddressFaxPhone());
        addScreenHandler(addressFaxPhone, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                addressFaxPhone.setValue(getWellManager().getAddress().getFaxPhone());
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                getWellManager().getAddress().setFaxPhone(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                addressFaxPhone.enable(EnumSet.of(State.QUERY, State.ADD, State.UPDATE)
                                              .contains(event.getState()));
                addressFaxPhone.setQueryMode(event.getState() == State.QUERY);
            }
        });

        wellLocation = (TextBox)def.getWidget(SampleMeta.getWellLocation());
        addScreenHandler(wellLocation, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                wellLocation.setValue(getWellManager().getPrivateWell().getLocation());
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                getWellManager().getPrivateWell().setLocation(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                wellLocation.enable(EnumSet.of(State.QUERY, State.ADD, State.UPDATE)
                                           .contains(event.getState()));
                wellLocation.setQueryMode(event.getState() == State.QUERY);
            }
        });

        locationAddrMultipleUnit = (TextBox)def.getWidget(SampleMeta.getLocationAddrMultipleUnit());
        addScreenHandler(locationAddrMultipleUnit, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                locationAddrMultipleUnit.setValue(getWellManager().getPrivateWell()
                                                                  .getLocationAddressDO()
                                                                  .getMultipleUnit());
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                getWellManager().getPrivateWell()
                                .getLocationAddressDO()
                                .setMultipleUnit(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                locationAddrMultipleUnit.enable(EnumSet.of(State.QUERY, State.ADD, State.UPDATE)
                                                       .contains(event.getState()));
                locationAddrMultipleUnit.setQueryMode(event.getState() == State.QUERY);
            }
        });

        locationAddrStreetAddress = (TextBox)def.getWidget(SampleMeta.getLocationAddrStreetAddress());
        addScreenHandler(locationAddrStreetAddress, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                locationAddrStreetAddress.setValue(getWellManager().getPrivateWell()
                                                                   .getLocationAddressDO()
                                                                   .getStreetAddress());
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                getWellManager().getPrivateWell()
                                .getLocationAddressDO()
                                .setStreetAddress(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                locationAddrStreetAddress.enable(EnumSet.of(State.QUERY, State.ADD, State.UPDATE)
                                                        .contains(event.getState()));
                locationAddrStreetAddress.setQueryMode(event.getState() == State.QUERY);
            }
        });

        locationAddrCity = (TextBox)def.getWidget(SampleMeta.getLocationAddrCity());
        addScreenHandler(locationAddrCity, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                locationAddrCity.setValue(getWellManager().getPrivateWell()
                                                          .getLocationAddressDO()
                                                          .getCity());
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                getWellManager().getPrivateWell().getLocationAddressDO().setCity(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                locationAddrCity.enable(EnumSet.of(State.QUERY, State.ADD, State.UPDATE)
                                               .contains(event.getState()));
                locationAddrCity.setQueryMode(event.getState() == State.QUERY);
            }
        });

        locationAddrState = (Dropdown)def.getWidget(SampleMeta.getLocationAddrState());
        addScreenHandler(locationAddrState, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                locationAddrState.setSelection(getWellManager().getPrivateWell()
                                                               .getLocationAddressDO()
                                                               .getState());
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                getWellManager().getPrivateWell().getLocationAddressDO().setState(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                locationAddrState.enable(EnumSet.of(State.QUERY, State.ADD, State.UPDATE)
                                                .contains(event.getState()));
                locationAddrState.setQueryMode(event.getState() == State.QUERY);
            }
        });

        locationAddrZipCode = (TextBox)def.getWidget(SampleMeta.getLocationAddrZipCode());
        addScreenHandler(locationAddrZipCode, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                locationAddrZipCode.setValue(getWellManager().getPrivateWell()
                                                             .getLocationAddressDO()
                                                             .getZipCode());
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                getWellManager().getPrivateWell()
                                .getLocationAddressDO()
                                .setZipCode(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                locationAddrZipCode.enable(EnumSet.of(State.QUERY, State.ADD, State.UPDATE)
                                                  .contains(event.getState()));
                locationAddrZipCode.setQueryMode(event.getState() == State.QUERY);
            }
        });

        wellOwner = (TextBox)def.getWidget(SampleMeta.getWellOwner());
        addScreenHandler(wellOwner, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                wellOwner.setValue(getWellManager().getPrivateWell().getOwner());
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                getWellManager().getPrivateWell().setOwner(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                wellOwner.enable(EnumSet.of(State.QUERY, State.ADD, State.UPDATE)
                                        .contains(event.getState()));
                wellOwner.setQueryMode(event.getState() == State.QUERY);
            }
        });

        wellCollector = (TextBox)def.getWidget(SampleMeta.getWellCollector());
        addScreenHandler(wellCollector, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                wellCollector.setValue(getWellManager().getPrivateWell().getCollector());
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                getWellManager().getPrivateWell().setCollector(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                wellCollector.enable(EnumSet.of(State.QUERY, State.ADD, State.UPDATE)
                                            .contains(event.getState()));
                wellCollector.setQueryMode(event.getState() == State.QUERY);
            }
        });

        wellWellNumber = (TextBox<Integer>)def.getWidget(SampleMeta.getWellWellNumber());
        addScreenHandler(wellWellNumber, new ScreenEventHandler<Integer>() {
            public void onDataChange(DataChangeEvent event) {
                wellWellNumber.setValue(getString(getWellManager().getPrivateWell().getWellNumber()));
            }

            public void onValueChange(ValueChangeEvent<Integer> event) {
                getWellManager().getPrivateWell().setWellNumber(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                wellWellNumber.enable(EnumSet.of(State.QUERY, State.ADD, State.UPDATE)
                                             .contains(event.getState()));
                wellWellNumber.setQueryMode(event.getState() == State.QUERY);
            }
        });

        projectName = (AutoComplete<Integer>)def.getWidget(SampleMeta.getProjectName());
        addScreenHandler(projectName, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                try {
                    SampleProjectViewDO projectDO = manager.getProjects()
                                                           .getFirstPermanentProject();

                    if (projectDO != null)
                        projectName.setSelection(projectDO.getId(), projectDO.getProjectName());
                    else
                        projectName.setSelection(null, "");

                } catch (Exception e) {
                    Window.alert(e.getMessage());
                }
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                TableDataRow selectedRow = projectName.getSelection();
                SampleProjectViewDO projectDO = null;
                try {
                    if (selectedRow.key != null) {
                        projectDO = new SampleProjectViewDO();
                        projectDO.setIsPermanent("Y");
                        projectDO.setProjectId((Integer)selectedRow.key);
                        projectDO.setProjectName((String)selectedRow.cells.get(0).value);
                        projectDO.setProjectDescription((String)selectedRow.cells.get(1).value);
                    }

                    manager.getProjects().addFirstPermanentProject(projectDO);

                    projectDO = manager.getProjects().getFirstPermanentProject();

                    if (projectDO != null)
                        projectName.setSelection(projectDO.getProjectId(),
                                                 projectDO.getProjectName());
                    else
                        projectName.setSelection(null, "");

                } catch (Exception e) {
                    Window.alert(e.getMessage());
                }
            }

            public void onStateChange(StateChangeEvent<State> event) {
                projectName.enable(EnumSet.of(State.ADD, State.UPDATE, State.QUERY)
                                          .contains(event.getState()));
                projectName.setQueryMode(event.getState() == State.QUERY);
            }
        });

        projectName.addGetMatchesHandler(new GetMatchesHandler() {
            public void onGetMatches(GetMatchesEvent event) {
                QueryFieldUtil parser;
                TableDataRow row;
                ProjectDO data;
                ArrayList<ProjectDO> list;
                ArrayList<TableDataRow> model;

                parser = new QueryFieldUtil();
                parser.parse(event.getMatch());

                window.setBusy();
                try {
                    list = projectService.callList("fetchActiveByName", parser.getParameter()
                                                                              .get(0));
                    model = new ArrayList<TableDataRow>();
                    for (int i = 0; i < list.size(); i++ ) {
                        row = new TableDataRow(4);
                        data = list.get(i);

                        row.key = data.getId();
                        row.cells.get(0).value = data.getName();
                        row.cells.get(1).value = data.getDescription();

                        model.add(row);
                    }
                    projectName.showAutoMatches(model);
                } catch (Throwable e) {
                    e.printStackTrace();
                    Window.alert(e.getMessage());
                }
                window.clearStatus();
            }
        });

        projectLookup = (AppButton)def.getWidget("projectLookup");
        addScreenHandler(projectLookup, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                onProjectLookupClick();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                projectLookup.enable(EnumSet.of(State.ADD, State.UPDATE, State.DISPLAY)
                                            .contains(event.getState()));
            }
        });
    }

    private void onProjectLookupClick() {
        try {
            if (projectScreen == null) {
                final PrivateWellTab env = this;
                projectScreen = new SampleProjectLookupScreen();
                projectScreen.addActionHandler(new ActionHandler<SampleProjectLookupScreen.Action>() {
                    public void onAction(ActionEvent<SampleProjectLookupScreen.Action> event) {
                        if (event.getAction() == SampleProjectLookupScreen.Action.OK) {
                            DataChangeEvent.fire(env, projectName);

                        }
                    }
                });
            }

            ScreenWindow modal = new ScreenWindow(ScreenWindow.Mode.DIALOG);
            modal.setName(consts.get("sampleProject"));
            modal.setContent(projectScreen);
            projectScreen.setScreenState(state);

            projectScreen.setManager(manager.getProjects());

        } catch (Exception e) {
            e.printStackTrace();
            Window.alert("error: " + e.getMessage());
            return;
        }
    }

    private void initializeDropdowns() {
        ArrayList<TableDataRow> model;

        // state dropdown
        model = new ArrayList<TableDataRow>();
        model.add(new TableDataRow(null, ""));
        for (DictionaryDO d : DictionaryCache.getListByCategorySystemName("state"))
            model.add(new TableDataRow(d.getEntry(), d.getEntry()));

        addressState.setModel(model);
        locationAddrState.setModel(model);
    }

    private SamplePrivateWellManager getWellManager() {
        SamplePrivateWellManager wellManager;

        try {
            wellManager = (SamplePrivateWellManager)manager.getDomainManager();
        } catch (Exception e) {
            e.printStackTrace();
            Window.alert(e.getMessage());
            wellManager = SamplePrivateWellManager.getInstance();
            manager.getSample().setDomain(SampleManager.WELL_DOMAIN_FLAG);
        }

        return wellManager;
    }

    public void setData(SampleManager manager) {
        this.manager = manager;
        loaded = false;
    }

    public void draw() {
        if ( !loaded)
            DataChangeEvent.fire(this);

        loaded = true;
    }
}
