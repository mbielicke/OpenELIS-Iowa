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
import org.openelis.domain.SampleOrganizationViewDO;
import org.openelis.domain.SamplePrivateWellViewDO;
import org.openelis.domain.SampleProjectViewDO;
import org.openelis.gwt.common.FieldErrorException;
import org.openelis.gwt.common.TableFieldErrorException;
import org.openelis.gwt.common.Util;
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
import org.openelis.manager.SamplePrivateWellManager;
import org.openelis.meta.SampleMeta;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;

public class PrivateWellTab extends Screen {
    private TextBox                        addressMultipleUnit, reportToAttn, addressStreetAddress,
                                           addressCity, addressWorkPhone, addressZipCode, addressFaxPhone, wellLocation,
                                           locationAddrMultipleUnit, locationAddrStreetAddress, locationAddrCity,
                                           locationAddrZipCode, wellOwner, wellCollector;
    private TextBox<Integer>               wellOrganizationId, wellNumber;
    private AutoComplete                   orgName, billTo,projectName;
    private Dropdown<String>               addressState, locationAddrState;
    private Button                         projectLookup, billToLookup;

    private SampleProjectLookupScreen      projectScreen;
    private SampleOrganizationLookupScreen organizationScreen;

    protected ScreenService                orgService;
    protected ScreenService                projectService;

    private SampleManager                  manager;
    private SamplePrivateWellManager       wellManager;

    protected boolean                      loaded = false;

    public PrivateWellTab(Window window) throws Exception {        
        this(null, window);
    }

    public PrivateWellTab(ScreenDefInt def, Window window) throws Exception {
        if (def == null)
            drawScreen((ScreenDefInt)GWT.create(PrivateWellTabDef.class));
        else
            setDefinition(def);

        setWindow(window);

        orgService = new ScreenService("controller?service=org.openelis.modules.organization.server.OrganizationService");
        projectService = new ScreenService("controller?service=org.openelis.modules.project.server.ProjectService");

        initialize();
        initializeDropdowns();
    }

    public void initialize() {
        orgName = (AutoComplete)def.getWidget(SampleMeta.getWellOrganizationName());
        addScreenHandler(orgName, new ScreenEventHandler<Integer>() {
            public void onDataChange(DataChangeEvent event) {
                if (getManager().getPrivateWell().getOrganizationId() == null) {
                	if(getManager().getPrivateWell().getReportToName() != null)
                		orgName.setValue(getManager().getPrivateWell().getReportToName().hashCode(),
                                         getManager().getPrivateWell().getReportToName());
                	else
                		orgName.setValue(null,"");
                    enableReportToFields(EnumSet.of(State.QUERY, State.ADD, State.UPDATE)
                                                .contains(state));

                } else {
                    orgName.setValue(getManager().getPrivateWell().getOrganization().getName().hashCode(),
                                         getManager().getPrivateWell().getOrganization().getName());
                    enableReportToFields(false);
                }
            }

            public void onValueChange(ValueChangeEvent<Integer> event) {
                OrganizationDO orgDO;
                SamplePrivateWellViewDO wellDO;
                Item<Integer> selectedRow;
                boolean enableAddressValues = false;

                selectedRow = orgName.getSelectedItem();
                if (selectedRow != null)
                    orgDO = (OrganizationDO)selectedRow.getData();
                else
                    orgDO = null;

                wellDO = getManager().getPrivateWell();
                if (orgDO != null) { // it's an org record
                    wellOrganizationId.setValue(orgDO.getId());
                    setAddress(orgDO.getAddress());                    

                    wellDO.setOrganizationId(orgDO.getId());
                    wellDO.setOrganization(orgDO);
                    enableAddressValues = false;                    
                } else if (selectedRow != null) { 
                    //
                    // it's a free text entry we only want to clear out the
                    // address values if it was an org before
                    //
                    if (getManager().getPrivateWell().getOrganizationId() != null) 
                        setAddress(null);
                    
                    wellOrganizationId.setValue(null);
                    wellDO.setReportToName((String)selectedRow.getCell(0));
                    wellDO.setOrganizationId(null);

                    enableAddressValues = true;
                } else { // it's a clear out
                    wellOrganizationId.setValue(null);
                    setAddress(null);

                    wellDO.setReportToName(null);
                    wellDO.getReportToAddress().setId(null);
                    wellDO.setOrganizationId(null);
                    wellDO.getOrganization().setName(null);


                    enableAddressValues = true;
                }

                enableReportToFields(enableAddressValues);
            }

            public void onStateChange(StateChangeEvent<State> event) {
                orgName.setEnabled(EnumSet.of(State.QUERY, State.ADD, State.UPDATE)
                                      .contains(event.getState()));
                orgName.setQueryMode(event.getState() == State.QUERY);
            }
        });

        orgName.addGetMatchesHandler(new GetMatchesHandler() {
            public void onGetMatches(GetMatchesEvent event) {
                QueryFieldUtil parser;
                Item<Integer> row;
                OrganizationDO data;
                ArrayList<OrganizationDO> list;
                ArrayList<Item<Integer>> model;
                int i, maxRows;

                maxRows = 10;
                parser = new QueryFieldUtil();
                try {
                	parser.parse(event.getMatch());
                }catch(Exception e) {
                	
                }

                window.setBusy();
                try {
                    list = orgService.callList("fetchByIdOrName", parser.getParameter().get(0));
                    model = new ArrayList<Item<Integer>>();
                    model.add(row = new Item<Integer>(event.getMatch().hashCode(), event.getMatch(), null,
                                                     null, null));

                    i = 0;
                    while (i < maxRows && i < list.size()) {
                        row = new Item<Integer>(4);
                        data = list.get(i);

                        row.setKey(data.getId());
                        row.setCell(0,data.getName());
                        row.setCell(1,data.getAddress().getStreetAddress());
                        row.setCell(2,data.getAddress().getCity());
                        row.setCell(3,data.getAddress().getState());
                        row.setData(data);

                        model.add(row);
                        i++ ;
                    }
                    orgName.showAutoMatches(model);
                } catch (Throwable e) {
                    e.printStackTrace();
                    com.google.gwt.user.client.Window.alert(e.getMessage());
                }
                window.clearStatus();
            }
        });

        wellOrganizationId = (TextBox<Integer>)def.getWidget(SampleMeta.getWellOrganizationId());
        addScreenHandler(wellOrganizationId, new ScreenEventHandler<Integer>() {
            public void onDataChange(DataChangeEvent event) {
                wellOrganizationId.setValue(getManager().getPrivateWell().getOrganizationId());
            }

            public void onValueChange(ValueChangeEvent<Integer> event) {
                getManager().getPrivateWell().setOrganizationId(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                wellOrganizationId.setEnabled(EnumSet.of(State.QUERY).contains(event.getState()));
                wellOrganizationId.setQueryMode(event.getState() == State.QUERY);
            }
        });

        addressMultipleUnit = (TextBox)def.getWidget(SampleMeta.getWellReportToAddressMultipleUnit());
        addScreenHandler(addressMultipleUnit, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                if (getManager().getPrivateWell().getOrganizationId() == null) {
                    addressMultipleUnit.setValue(getManager().getPrivateWell()
                                                 .getReportToAddress()
                                                 .getMultipleUnit());
                    enableReportToFields(EnumSet.of(State.QUERY, State.ADD, State.UPDATE)
                                                .contains(state));

                } else {
                    addressMultipleUnit.setValue(getManager().getPrivateWell().getOrganization().getAddress().getMultipleUnit());
                    enableReportToFields(false);
                }
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                getManager().getPrivateWell()
                            .getReportToAddress()
                            .setMultipleUnit(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                addressMultipleUnit.setEnabled(EnumSet.of(State.QUERY, State.ADD, State.UPDATE)
                                                  .contains(event.getState()));
                addressMultipleUnit.setQueryMode(event.getState() == State.QUERY);
            }
        });

        reportToAttn = (TextBox)def.getWidget(SampleMeta.getWellReportToAttention());
        addScreenHandler(reportToAttn, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                reportToAttn.setValue(getManager().getPrivateWell().getReportToAttention());                
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                getManager().getPrivateWell().setReportToAttention(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                reportToAttn.setEnabled(EnumSet.of(State.QUERY, State.ADD, State.UPDATE)
                                           .contains(event.getState()));
                reportToAttn.setQueryMode(event.getState() == State.QUERY);
            }
        });

        addressStreetAddress = (TextBox)def.getWidget(SampleMeta.getWellReportToAddressStreetAddress());
        addScreenHandler(addressStreetAddress, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {               
                if (getManager().getPrivateWell().getOrganizationId() == null) {
                    addressStreetAddress.setValue(getManager().getPrivateWell()
                                                 .getReportToAddress()
                                                 .getStreetAddress());
                    enableReportToFields(EnumSet.of(State.QUERY, State.ADD, State.UPDATE)
                                                .contains(state));

                } else {
                    addressStreetAddress.setValue(getManager().getPrivateWell().getOrganization().getAddress().getStreetAddress());
                    enableReportToFields(false);
                }
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                getManager().getPrivateWell()
                            .getReportToAddress()
                            .setStreetAddress(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                addressStreetAddress.setEnabled(EnumSet.of(State.QUERY, State.ADD, State.UPDATE)
                                                   .contains(event.getState()));
                addressStreetAddress.setQueryMode(event.getState() == State.QUERY);
            }
        });

        addressCity = (TextBox)def.getWidget(SampleMeta.getWellReportToAddressCity());
        addScreenHandler(addressCity, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                if (getManager().getPrivateWell().getOrganizationId() == null) {
                    addressCity.setValue(getManager().getPrivateWell()
                                                 .getReportToAddress()
                                                 .getCity());
                    enableReportToFields(EnumSet.of(State.QUERY, State.ADD, State.UPDATE)
                                                .contains(state));

                } else {
                    addressCity.setValue(getManager().getPrivateWell().getOrganization().getAddress().getCity());
                    enableReportToFields(false);
                }
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                getManager().getPrivateWell().getReportToAddress().setCity(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                addressCity.setEnabled(EnumSet.of(State.QUERY, State.ADD, State.UPDATE)
                                          .contains(event.getState()));
                addressCity.setQueryMode(event.getState() == State.QUERY);
            }
        });

        addressWorkPhone = (TextBox)def.getWidget(SampleMeta.getWellReportToAddressWorkPhone());
        addScreenHandler(addressWorkPhone, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {                
                if (getManager().getPrivateWell().getOrganizationId() == null) {
                    addressWorkPhone.setValue(getManager().getPrivateWell()
                                                 .getReportToAddress()
                                                 .getWorkPhone());
                    enableReportToFields(EnumSet.of(State.QUERY, State.ADD, State.UPDATE)
                                                .contains(state));

                } else {
                    addressWorkPhone.setValue(getManager().getPrivateWell().getOrganization().getAddress().getWorkPhone());
                    enableReportToFields(false);
                }
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                getManager().getPrivateWell().getReportToAddress().setWorkPhone(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                addressWorkPhone.setEnabled(EnumSet.of(State.QUERY, State.ADD, State.UPDATE)
                                               .contains(event.getState()));
                addressWorkPhone.setQueryMode(event.getState() == State.QUERY);
            }
        });

        addressState = (Dropdown)def.getWidget(SampleMeta.getWellReportToAddressState());
        addScreenHandler(addressState, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                if (getManager().getPrivateWell().getOrganizationId() == null) {
                    addressState.setValue(getManager().getPrivateWell()
                                                 .getReportToAddress()
                                                 .getState());
                    enableReportToFields(EnumSet.of(State.QUERY, State.ADD, State.UPDATE)
                                                .contains(state));
                } else {
                    addressState.setValue(getManager().getPrivateWell().getOrganization().getAddress().getState());
                    enableReportToFields(false);
                }
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                getManager().getPrivateWell().getReportToAddress().setState(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                addressState.setEnabled(EnumSet.of(State.QUERY, State.ADD, State.UPDATE)
                                           .contains(event.getState()));
                addressState.setQueryMode(event.getState() == State.QUERY);
            }
        });

        addressZipCode = (TextBox)def.getWidget(SampleMeta.getWellReportToAddressZipCode());
        addScreenHandler(addressZipCode, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                if (getManager().getPrivateWell().getOrganizationId() == null) {
                    addressZipCode.setValue(getManager().getPrivateWell()
                                                 .getReportToAddress()
                                                 .getZipCode());
                    enableReportToFields(EnumSet.of(State.QUERY, State.ADD, State.UPDATE)
                                                .contains(state));
                } else {
                    addressZipCode.setValue(getManager().getPrivateWell().getOrganization().getAddress().getZipCode());
                    enableReportToFields(false);
                }
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                getManager().getPrivateWell().getReportToAddress().setZipCode(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                addressZipCode.setEnabled(EnumSet.of(State.QUERY, State.ADD, State.UPDATE)
                                             .contains(event.getState()));
                addressZipCode.setQueryMode(event.getState() == State.QUERY);
            }
        });

        addressFaxPhone = (TextBox)def.getWidget(SampleMeta.getWellReportToAddressFaxPhone());
        addScreenHandler(addressFaxPhone, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                addressFaxPhone.setValue(getManager().getPrivateWell()
                                                     .getReportToAddress()
                                                     .getFaxPhone());
                if (getManager().getPrivateWell().getOrganizationId() == null) {
                    addressFaxPhone.setValue(getManager().getPrivateWell()
                                                 .getReportToAddress()
                                                 .getFaxPhone());
                    enableReportToFields(EnumSet.of(State.QUERY, State.ADD, State.UPDATE)
                                                .contains(state));
                } else {
                    addressFaxPhone.setValue(getManager().getPrivateWell().getOrganization().getAddress().getFaxPhone());
                    enableReportToFields(false);
                }
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                getManager().getPrivateWell().getReportToAddress().setFaxPhone(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                addressFaxPhone.setEnabled(EnumSet.of(State.QUERY, State.ADD, State.UPDATE)
                                              .contains(event.getState()));
                addressFaxPhone.setQueryMode(event.getState() == State.QUERY);
            }
        });

        wellLocation = (TextBox)def.getWidget(SampleMeta.getWellLocation());
        addScreenHandler(wellLocation, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                wellLocation.setValue(getManager().getPrivateWell().getLocation());
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                getManager().getPrivateWell().setLocation(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                wellLocation.setEnabled(EnumSet.of(State.QUERY, State.ADD, State.UPDATE)
                                           .contains(event.getState()));
                wellLocation.setQueryMode(event.getState() == State.QUERY);
            }
        });

        locationAddrMultipleUnit = (TextBox)def.getWidget(SampleMeta.getWellLocationAddrMultipleUnit());
        addScreenHandler(locationAddrMultipleUnit, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                locationAddrMultipleUnit.setValue(getManager().getPrivateWell()
                                                              .getLocationAddress()
                                                              .getMultipleUnit());
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                getManager().getPrivateWell()
                            .getLocationAddress()
                            .setMultipleUnit(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                locationAddrMultipleUnit.setEnabled(EnumSet.of(State.QUERY, State.ADD, State.UPDATE)
                                                       .contains(event.getState()));
                locationAddrMultipleUnit.setQueryMode(event.getState() == State.QUERY);
            }
        });

        locationAddrStreetAddress = (TextBox)def.getWidget(SampleMeta.getWellLocationAddrStreetAddress());
        addScreenHandler(locationAddrStreetAddress, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                locationAddrStreetAddress.setValue(getManager().getPrivateWell()
                                                               .getLocationAddress()
                                                               .getStreetAddress());
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                getManager().getPrivateWell()
                            .getLocationAddress()
                            .setStreetAddress(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                locationAddrStreetAddress.setEnabled(EnumSet.of(State.QUERY, State.ADD, State.UPDATE)
                                                        .contains(event.getState()));
                locationAddrStreetAddress.setQueryMode(event.getState() == State.QUERY);
            }
        });

        locationAddrCity = (TextBox)def.getWidget(SampleMeta.getWellLocationAddrCity());
        addScreenHandler(locationAddrCity, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                locationAddrCity.setValue(getManager().getPrivateWell()
                                                      .getLocationAddress()
                                                      .getCity());
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                getManager().getPrivateWell().getLocationAddress().setCity(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                locationAddrCity.setEnabled(EnumSet.of(State.QUERY, State.ADD, State.UPDATE)
                                               .contains(event.getState()));
                locationAddrCity.setQueryMode(event.getState() == State.QUERY);
            }
        });

        locationAddrState = (Dropdown)def.getWidget(SampleMeta.getWellLocationAddrState());
        addScreenHandler(locationAddrState, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                locationAddrState.setValue(getManager().getPrivateWell()
                                                           .getLocationAddress()
                                                           .getState());
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                getManager().getPrivateWell().getLocationAddress().setState(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                locationAddrState.setEnabled(EnumSet.of(State.QUERY, State.ADD, State.UPDATE)
                                                .contains(event.getState()));
                locationAddrState.setQueryMode(event.getState() == State.QUERY);
            }
        });

        locationAddrZipCode = (TextBox)def.getWidget(SampleMeta.getWellLocationAddrZipCode());
        addScreenHandler(locationAddrZipCode, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                locationAddrZipCode.setValue(getManager().getPrivateWell()
                                                         .getLocationAddress()
                                                         .getZipCode());
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                getManager().getPrivateWell().getLocationAddress().setZipCode(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                locationAddrZipCode.setEnabled(EnumSet.of(State.QUERY, State.ADD, State.UPDATE)
                                                  .contains(event.getState()));
                locationAddrZipCode.setQueryMode(event.getState() == State.QUERY);
            }
        });

        wellOwner = (TextBox)def.getWidget(SampleMeta.getWellOwner());
        addScreenHandler(wellOwner, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                wellOwner.setValue(getManager().getPrivateWell().getOwner());
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                getManager().getPrivateWell().setOwner(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                wellOwner.setEnabled(EnumSet.of(State.QUERY, State.ADD, State.UPDATE)
                                        .contains(event.getState()));
                wellOwner.setQueryMode(event.getState() == State.QUERY);
            }
        });

        wellCollector = (TextBox)def.getWidget(SampleMeta.getWellCollector());
        addScreenHandler(wellCollector, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                wellCollector.setValue(getManager().getPrivateWell().getCollector());
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                getManager().getPrivateWell().setCollector(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                wellCollector.setEnabled(EnumSet.of(State.QUERY, State.ADD, State.UPDATE)
                                            .contains(event.getState()));
                wellCollector.setQueryMode(event.getState() == State.QUERY);
            }
        });

        wellNumber = (TextBox<Integer>)def.getWidget(SampleMeta.getWellWellNumber());
        addScreenHandler(wellNumber, new ScreenEventHandler<Integer>() {
            public void onDataChange(DataChangeEvent event) {
                wellNumber.setValue(getManager().getPrivateWell().getWellNumber());
            }

            public void onValueChange(ValueChangeEvent<Integer> event) {
                getManager().getPrivateWell().setWellNumber(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                wellNumber.setEnabled(EnumSet.of(State.QUERY, State.ADD, State.UPDATE)
                                             .contains(event.getState()));
                wellNumber.setQueryMode(event.getState() == State.QUERY);
            }
        });

        projectName = (AutoComplete)def.getWidget(SampleMeta.getProjectName());
        addScreenHandler(projectName, new ScreenEventHandler<Integer>() {
            public void onDataChange(DataChangeEvent event) {
                try {
                    SampleProjectViewDO projectDO = manager.getProjects()
                                                           .getFirstPermanentProject();

                    if (projectDO != null)
                        projectName.setValue(projectDO.getProjectId(),
                                                 projectDO.getProjectName());
                    else
                        projectName.setValue(null, "");

                } catch (Exception e) {
                    com.google.gwt.user.client.Window.alert(e.getMessage());
                }
            }

            public void onValueChange(ValueChangeEvent<Integer> event) {
                Item<Integer> selectedRow = projectName.getSelectedItem();
                SampleProjectViewDO projectDO = null;
                try {
                    if (selectedRow.getKey() != null) {
                        projectDO = new SampleProjectViewDO();
                        projectDO.setIsPermanent("Y");
                        projectDO.setProjectId((Integer)selectedRow.getKey());
                        projectDO.setProjectName((String)selectedRow.getCell(0));
                        projectDO.setProjectDescription((String)selectedRow.getCell(1));
                    }

                    manager.getProjects().addFirstPermanentProject(projectDO);

                    projectDO = manager.getProjects().getFirstPermanentProject();

                    if (projectDO != null)
                        projectName.setValue(projectDO.getProjectId(),
                                                 projectDO.getProjectName());
                    else
                        projectName.setValue(null, "");

                } catch (Exception e) {
                    com.google.gwt.user.client.Window.alert(e.getMessage());
                }
            }

            public void onStateChange(StateChangeEvent<State> event) {
                projectName.setEnabled(EnumSet.of(State.ADD, State.UPDATE, State.QUERY)
                                          .contains(event.getState()));
                projectName.setQueryMode(event.getState() == State.QUERY);
            }
        });

        projectName.addGetMatchesHandler(new GetMatchesHandler() {
            public void onGetMatches(GetMatchesEvent event) {
                QueryFieldUtil parser;
                Item<Integer> row;
                ProjectDO data;
                ArrayList<ProjectDO> list;
                ArrayList<Item<Integer>> model;

                parser = new QueryFieldUtil();
                try {
                	parser.parse(event.getMatch());
                }catch(Exception e) {
                	
                }

                window.setBusy();
                try {
                    list = projectService.callList("fetchActiveByName", parser.getParameter()
                                                                              .get(0));
                    model = new ArrayList<Item<Integer>>();
                    for (int i = 0; i < list.size(); i++ ) {
                        row = new Item<Integer>(4);
                        data = list.get(i);

                        row.setKey(data.getId());
                        row.setCell(0,data.getName());
                        row.setCell(1,data.getDescription());

                        model.add(row);
                    }
                    projectName.showAutoMatches(model);
                } catch (Throwable e) {
                    e.printStackTrace();
                    com.google.gwt.user.client.Window.alert(e.getMessage());
                }
                window.clearStatus();
            }
        });

        projectLookup = (Button)def.getWidget("projectLookup");
        addScreenHandler(projectLookup, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                onProjectLookupClick();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                projectLookup.setEnabled(EnumSet.of(State.ADD, State.UPDATE, State.DISPLAY)
                                            .contains(event.getState()));
            }
        });

        billTo = (AutoComplete)def.getWidget(SampleMeta.getBillTo());
        addScreenHandler(billTo, new ScreenEventHandler<Integer>() {
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

            public void onValueChange(ValueChangeEvent<Integer> event) {
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
    }

    public ArrayList<QueryData> getQueryFields() {
        QueryData domain;
        ArrayList<QueryData> fields;

        fields = super.getQueryFields();

        if (fields.size() > 0) {
            domain = new QueryData(SampleMeta.getDomain(),QueryData.Type.STRING,SampleManager.WELL_DOMAIN_FLAG);
            fields.add(domain);
        }       

        return fields;
    }        

    public void setData(SampleManager manager) {
        this.manager = manager;
        wellManager = null;
        loaded = false;
    }

    public void draw() {
        if ( !loaded)
            DataChangeEvent.fire(this);

        loaded = true;
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

            ModalWindow modal = new ModalWindow();
            modal.setName(consts.get("sampleProject"));
            modal.setContent(projectScreen);
            projectScreen.setScreenState(state);

            projectScreen.setManager(manager.getProjects());

        } catch (Exception e) {
            e.printStackTrace();
            com.google.gwt.user.client.Window.alert("error: " + e.getMessage());
            return;
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
                final PrivateWellTab well = this;
                organizationScreen = new SampleOrganizationLookupScreen();
                organizationScreen.setCanAddReportTo(false);

                organizationScreen.addActionHandler(new ActionHandler<SampleOrganizationLookupScreen.Action>() {
                    public void onAction(ActionEvent<SampleOrganizationLookupScreen.Action> event) {
                        if (event.getAction() == SampleOrganizationLookupScreen.Action.OK) {
                            DataChangeEvent.fire(well, billTo);
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

    private void enableReportToFields(boolean enable) {
        addressMultipleUnit.setEnabled(enable);
        addressStreetAddress.setEnabled(enable);
        addressCity.setEnabled(enable);
        addressState.setEnabled(enable);
        addressZipCode.setEnabled(enable);
        addressWorkPhone.setEnabled(enable);
        addressFaxPhone.setEnabled(enable);
    }

    private void initializeDropdowns() {
        ArrayList<Item<String>> model;

        // state dropdown
        model = new ArrayList<Item<String>>();
        model.add(new Item<String>(null, ""));
        for (DictionaryDO d : DictionaryCache.getListByCategorySystemName("state"))
            model.add(new Item<String>(d.getEntry(), d.getEntry()));

        addressState.setModel(model);
        locationAddrState.setModel(model);
    }

    public void showErrors(ValidationErrorsList errors) {
        TableFieldErrorException tableE;
        FieldErrorException fieldE;
        Table tableWid;
        HasExceptions field;

        for (Exception ex : errors.getErrorList()) {
            if (ex instanceof TableFieldErrorException) {
                tableE = (TableFieldErrorException)ex;
                tableWid = (Table)def.getWidget(tableE.getTableKey());
                tableWid.addException(tableE.getRowIndex(), tableWid.getColumnByName(tableE.getFieldName()), tableE);
            } else if (ex instanceof FieldErrorException) {
                fieldE = (FieldErrorException)ex;
                field = (HasExceptions)def.getWidget(fieldE.getFieldName());

                if (field != null)
                    field.addException(fieldE);
            }
        }
    }

    private SamplePrivateWellManager getManager() {
        if (wellManager == null) {
            try {
                wellManager = (SamplePrivateWellManager)manager.getDomainManager();
            } catch (Exception e) {
                wellManager = SamplePrivateWellManager.getInstance();
                manager = SampleManager.getInstance();
                manager.getSample().setDomain(SampleManager.WELL_DOMAIN_FLAG);
            }
        }

        return wellManager;
    }
    
    private void setAddress(AddressDO data) {
        if (data == null)
            data = new AddressDO();
        
        addressMultipleUnit.setValue(data.getMultipleUnit());
        addressStreetAddress.setValue(data.getStreetAddress());
        addressCity.setValue(data.getCity());
        addressState.setValue(data.getState());
        addressZipCode.setValue(data.getZipCode());
        addressWorkPhone.setValue(data.getWorkPhone());
        addressFaxPhone.setValue(data.getFaxPhone());
    }
}
