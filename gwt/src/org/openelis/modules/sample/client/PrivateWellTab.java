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

import org.openelis.cache.CategoryCache;
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
import org.openelis.gwt.widget.AppButton;
import org.openelis.gwt.widget.AutoComplete;
import org.openelis.gwt.widget.Dropdown;
import org.openelis.gwt.widget.HasField;
import org.openelis.gwt.widget.QueryFieldUtil;
import org.openelis.gwt.widget.ScreenWindow;
import org.openelis.gwt.widget.ScreenWindowInt;
import org.openelis.gwt.widget.TextBox;
import org.openelis.gwt.widget.table.TableDataRow;
import org.openelis.gwt.widget.table.TableWidget;
import org.openelis.manager.SampleManager;
import org.openelis.manager.SampleOrganizationManager;
import org.openelis.manager.SamplePrivateWellManager;
import org.openelis.meta.SampleMeta;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Widget;

public class PrivateWellTab extends Screen {
    private TextBox                        addressMultipleUnit, reportToAttn, addressStreetAddress,
                                           addressCity, addressWorkPhone, addressZipCode, addressFaxPhone, wellLocation,
                                           locationAddrMultipleUnit, locationAddrStreetAddress, locationAddrCity,
                                           locationAddrZipCode, wellOwner, wellCollector;
    private TextBox<Integer>               wellOrganizationId, wellNumber;
    private AutoComplete<String>           orgName;
    private AutoComplete<Integer>          billTo;
    private Dropdown<String>               addressState, locationAddrState;
    private AutoComplete<Integer>          project;
    private AppButton                      projectLookup, billToLookup;

    private PrivateWellTab                 screen;
    private SampleProjectLookupScreen      projectScreen;
    private SampleOrganizationLookupScreen organizationScreen;

    protected ScreenService                projectService, orgService;

    private SampleManager                  manager, previousManager;
    private SamplePrivateWellManager       wellManager, previousWellManager;

    private Integer                        sampleReleasedId;
    
    protected boolean                      loaded = false;

    public PrivateWellTab(ScreenWindowInt window) throws Exception {        
        this(null, window);
    }

    public PrivateWellTab(ScreenDefInt def, ScreenWindowInt window) throws Exception {
        if (def == null)
            drawScreen((ScreenDefInt)GWT.create(PrivateWellTabDef.class));
        else
            setDefinition(def);

        setWindow(window);

        projectService = new ScreenService("controller?service=org.openelis.modules.project.server.ProjectService");
        orgService = new ScreenService("controller?service=org.openelis.modules.organization.server.OrganizationService");

        initialize();
        initializeDropdowns();
    }

    public void initialize() {
        screen = this;
        
        orgName = (AutoComplete<String>)def.getWidget(SampleMeta.getWellOrganizationName());
        addScreenHandler(orgName, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                if (getWellManager().getPrivateWell().getOrganizationId() == null) {
                    orgName.setSelection(getWellManager().getPrivateWell().getReportToName(),
                                         getWellManager().getPrivateWell().getReportToName());
                    enableReportToFields(state == State.QUERY ||
                                         (canEdit() && EnumSet.of(State.ADD, State.UPDATE)
                                                              .contains(state)));
                } else {
                    orgName.setSelection(getWellManager().getPrivateWell().getOrganization().getId().toString(),
                                         getWellManager().getPrivateWell().getOrganization().getName());
                    enableReportToFields(false);
                }
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                OrganizationDO orgDO;
                SamplePrivateWellViewDO wellDO;
                TableDataRow selectedRow;
                boolean enableAddressValues = false;

                selectedRow = orgName.getSelection();
                if (selectedRow != null)
                    orgDO = (OrganizationDO)selectedRow.data;
                else
                    orgDO = null;

                wellDO = getWellManager().getPrivateWell();
                if (orgDO != null) {
                    // it's an org record
                    wellOrganizationId.setValue(orgDO.getId());

                    setAddress(orgDO.getAddress());                    

                    wellDO.setOrganizationId(orgDO.getId());
                    wellDO.setOrganization(orgDO);
                    wellDO.setReportToName(null);
                    
                    enableAddressValues = false;                    
                } else if (selectedRow != null) { 
                    //
                    // it's a free text entry we only want to clear out the
                    // address values if it was an org before
                    //
                    wellOrganizationId.setValue("");

                    if (wellDO.getOrganizationId() != null)
                       setAddress(null);
                    
                    wellDO.setOrganizationId(null);
                    wellDO.setOrganization(null);
                    wellDO.setReportToName(event.getValue());

                    enableAddressValues = true;
                } else {
                    // it's a clear out
                    wellOrganizationId.setValue("");
                    
                    setAddress(null);

                    wellDO.setOrganizationId(null);
                    wellDO.setOrganization(null);
                    wellDO.setReportToName(null);

                    enableAddressValues = true;
                }

                enableReportToFields(enableAddressValues);
            }

            public void onStateChange(StateChangeEvent<State> event) {
                boolean enable;
                
                enable = event.getState() == State.QUERY ||
                         (canEdit() && EnumSet.of(State.ADD, State.UPDATE).contains(event.getState()));
                
                orgName.enable(enable);
                orgName.setQueryMode(event.getState() == State.QUERY);

                enableReportToFields(enable && getWellManager().getPrivateWell().getOrganizationId() == null);
            }
        });
        
        orgName.addKeyDownHandler(new KeyDownHandler() {
            public void onKeyDown(KeyDownEvent event) {
                String repName;
                SamplePrivateWellViewDO data, prevData;
                OrganizationDO prevOrg;                
                
                if (canCopyFromPrevious(event)) {  
                    data = getWellManager().getPrivateWell();
                    prevData = getPreviousWellManager().getPrivateWell();                    
                    prevOrg = prevData.getOrganization();
                    
                    if (prevOrg != null) {         
                        wellOrganizationId.setValue(prevOrg.getId());
                        setAddress(prevOrg.getAddress());
                        data.setOrganization(prevOrg);
                        data.setOrganizationId(prevOrg.getId());
                        data.setReportToName(null);                        
                        orgName.setSelection(prevOrg.getId().toString(), prevOrg.getName());
                        enableReportToFields(false);
                    } else {
                        wellOrganizationId.setValue("");
                        setAddress(prevData.getReportToAddress());
                        data.setOrganization(null);
                        data.setOrganizationId(null);                        
                        repName = prevData.getReportToName();
                        data.setReportToName(repName);                        
                        orgName.setSelection(repName, repName);
                        enableReportToFields(true);
                    }                     
                    
                    event.preventDefault();
                    event.stopPropagation();
                    setFocusToNext(orgName);
                }
            }
        });

        orgName.addGetMatchesHandler(new GetMatchesHandler() {
            public void onGetMatches(GetMatchesEvent event) {
                TableDataRow row;
                OrganizationDO data;
                ArrayList<OrganizationDO> list;
                ArrayList<TableDataRow> model;
                int i, maxRows;

                maxRows = 10;

                window.setBusy();
                try {
                    list = orgService.callList("fetchByIdOrName", QueryFieldUtil.parseAutocomplete(event.getMatch()));
                    model = new ArrayList<TableDataRow>();
                    model.add(new TableDataRow(event.getMatch(), event.getMatch(),
                                               null, null, null));

                    i = 0;
                    while (i < maxRows && i < list.size()) {
                        row = new TableDataRow(4);
                        data = list.get(i);

                        row.key = data.getId().toString();
                        row.cells.get(0).value = data.getName();
                        row.cells.get(1).value = data.getAddress().getStreetAddress();
                        row.cells.get(2).value = data.getAddress().getCity();
                        row.cells.get(3).value = data.getAddress().getState();
                        row.data = data;

                        model.add(row);
                        i++ ;
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
                wellOrganizationId.setValue(getWellManager().getPrivateWell().getOrganizationId());
            }

            public void onValueChange(ValueChangeEvent<Integer> event) {
                getWellManager().getPrivateWell().setOrganizationId(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                wellOrganizationId.enable(EnumSet.of(State.QUERY).contains(event.getState()));
                wellOrganizationId.setQueryMode(event.getState() == State.QUERY);
            }
        });

        addressMultipleUnit = (TextBox)def.getWidget(SampleMeta.getWellReportToAddressMultipleUnit());
        addScreenHandler(addressMultipleUnit, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                if (getWellManager().getPrivateWell().getOrganizationId() == null) {
                    addressMultipleUnit.setValue(getWellManager().getPrivateWell().getReportToAddress()
                                                             .getMultipleUnit());
                } else {
                    addressMultipleUnit.setValue(getWellManager().getPrivateWell().getOrganization()
                                                             .getAddress().getMultipleUnit());
                }
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                getWellManager().getPrivateWell()
                            .getReportToAddress()
                            .setMultipleUnit(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                addressMultipleUnit.setQueryMode(event.getState() == State.QUERY);
            }
        });
        
        addressMultipleUnit.addKeyDownHandler(new KeyDownHandler() {            
            public void onKeyDown(KeyDownEvent event) {
                String multi;
                SamplePrivateWellViewDO data, prevData;
                AddressDO addr;
                
                if (canCopyFromPrevious(event)) {
                    data = getWellManager().getPrivateWell();
                    prevData = getPreviousWellManager().getPrivateWell();
                    multi = "";
                    addr = prevData.getReportToAddress();
                    if (addr != null)
                        multi = addr.getMultipleUnit();
                    data.getReportToAddress().setMultipleUnit(multi);
                    addressMultipleUnit.setValue(multi);
                    
                    event.preventDefault();
                    event.stopPropagation();
                    setFocusToNext(addressMultipleUnit);
                }
            }
        });

        reportToAttn = (TextBox)def.getWidget(SampleMeta.getWellReportToAttention());
        addScreenHandler(reportToAttn, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                reportToAttn.setValue(getWellManager().getPrivateWell().getReportToAttention());                
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                getWellManager().getPrivateWell().setReportToAttention(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                reportToAttn.enable(event.getState() == State.QUERY ||
                                    (canEdit() && EnumSet.of(State.ADD, State.UPDATE)
                                                         .contains(event.getState())));
                reportToAttn.setQueryMode(event.getState() == State.QUERY);
            }
        });
        
        reportToAttn.addKeyDownHandler(new KeyDownHandler() {            
            public void onKeyDown(KeyDownEvent event) {
                String attn;
                SamplePrivateWellViewDO data, prevData;
                
                if (canCopyFromPrevious(event)) {
                    data = getWellManager().getPrivateWell();
                    prevData = getPreviousWellManager().getPrivateWell();
                    attn = prevData.getReportToAttention();
                    data.setReportToAttention(attn);
                    reportToAttn.setValue(attn);
                    
                    event.preventDefault();
                    event.stopPropagation();
                    setFocusToNext(reportToAttn);
                }
            }
        });

        addressStreetAddress = (TextBox)def.getWidget(SampleMeta.getWellReportToAddressStreetAddress());
        addScreenHandler(addressStreetAddress, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {               
                if (getWellManager().getPrivateWell().getOrganizationId() == null) {
                    addressStreetAddress.setValue(getWellManager().getPrivateWell().getReportToAddress()
                                                              .getStreetAddress());
                } else {
                    addressStreetAddress.setValue(getWellManager().getPrivateWell().getOrganization()
                                                              .getAddress().getStreetAddress());
                }
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                getWellManager().getPrivateWell()
                            .getReportToAddress()
                            .setStreetAddress(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                addressStreetAddress.setQueryMode(event.getState() == State.QUERY);
            }
        });
        
        addressStreetAddress.addKeyDownHandler(new KeyDownHandler() {            
            public void onKeyDown(KeyDownEvent event) {
                String strt;
                SamplePrivateWellViewDO data, prevData;
                AddressDO addr;
                
                if (canCopyFromPrevious(event)) {
                    data = getWellManager().getPrivateWell();
                    prevData = getPreviousWellManager().getPrivateWell();
                    strt = "";
                    addr = prevData.getReportToAddress();
                    if (addr != null)
                        strt = addr.getStreetAddress();
                    data.getReportToAddress().setStreetAddress(strt);
                    addressStreetAddress.setValue(strt);
                    
                    event.preventDefault();
                    event.stopPropagation();
                    setFocusToNext(addressStreetAddress);
                }
            }
        });

        addressCity = (TextBox)def.getWidget(SampleMeta.getWellReportToAddressCity());
        addScreenHandler(addressCity, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                if (getWellManager().getPrivateWell().getOrganizationId() == null) {
                    addressCity.setValue(getWellManager().getPrivateWell().getReportToAddress()
                                                     .getCity());
                } else {
                    addressCity.setValue(getWellManager().getPrivateWell().getOrganization()
                                                     .getAddress().getCity());
                }
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                getWellManager().getPrivateWell().getReportToAddress().setCity(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                addressCity.setQueryMode(event.getState() == State.QUERY);
            }
        });

        addressCity.addKeyDownHandler(new KeyDownHandler() {            
            public void onKeyDown(KeyDownEvent event) {
                String city;
                SamplePrivateWellViewDO data, prevData;
                AddressDO addr;
                
                if (canCopyFromPrevious(event)) {
                    data = getWellManager().getPrivateWell();
                    prevData = getPreviousWellManager().getPrivateWell();
                    city = "";
                    addr = prevData.getReportToAddress();
                    if (addr != null)
                        city = addr.getCity();
                    data.getReportToAddress().setCity(city);
                    addressCity.setValue(city);
                    
                    event.preventDefault();
                    event.stopPropagation();
                    setFocusToNext(addressCity);
                }
            }
        });
        
        addressWorkPhone = (TextBox)def.getWidget(SampleMeta.getWellReportToAddressWorkPhone());
        addScreenHandler(addressWorkPhone, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {                
                if (getWellManager().getPrivateWell().getOrganizationId() == null) {
                    addressWorkPhone.setValue(getWellManager().getPrivateWell().getReportToAddress()
                                                          .getWorkPhone());
                } else {
                    addressWorkPhone.setValue(getWellManager().getPrivateWell().getOrganization()
                                                          .getAddress().getWorkPhone());
                }
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                getWellManager().getPrivateWell().getReportToAddress().setWorkPhone(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                addressWorkPhone.setQueryMode(event.getState() == State.QUERY);
            }
        });
        
        addressWorkPhone.addKeyDownHandler(new KeyDownHandler() {            
            public void onKeyDown(KeyDownEvent event) {
                String phone;
                SamplePrivateWellViewDO data, prevData;
                AddressDO addr;
                
                if (canCopyFromPrevious(event)) {
                    data = getWellManager().getPrivateWell();
                    prevData = getPreviousWellManager().getPrivateWell();
                    phone = "";
                    addr = prevData.getReportToAddress();
                    if (addr != null)
                        phone = addr.getWorkPhone();
                    data.getReportToAddress().setWorkPhone(phone);
                    addressWorkPhone.setValue(phone);
                    
                    event.preventDefault();
                    event.stopPropagation();
                    setFocusToNext(addressWorkPhone);
                }
            }
        });

        addressState = (Dropdown)def.getWidget(SampleMeta.getWellReportToAddressState());
        addScreenHandler(addressState, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                if (getWellManager().getPrivateWell().getOrganizationId() == null) {
                    addressState.setValue(getWellManager().getPrivateWell().getReportToAddress()
                                                      .getState());
                } else {
                    addressState.setValue(getWellManager().getPrivateWell().getOrganization()
                                                      .getAddress().getState());
                }
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                getWellManager().getPrivateWell().getReportToAddress().setState(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                addressState.setQueryMode(event.getState() == State.QUERY);
            }
        });
        
        addressState.addKeyDownHandler(new KeyDownHandler() {            
            public void onKeyDown(KeyDownEvent event) {
                String state;
                SamplePrivateWellViewDO data, prevData;
                AddressDO addr;
                
                if (canCopyFromPrevious(event)) {
                    data = getWellManager().getPrivateWell();
                    prevData = getPreviousWellManager().getPrivateWell();
                    state = "";
                    addr = prevData.getReportToAddress();
                    if (addr != null)
                        state = addr.getState();
                    data.getReportToAddress().setState(state);
                    addressState.setValue(state);
                    
                    event.preventDefault();
                    event.stopPropagation();
                    setFocusToNext(addressState);
                }
            }
        });

        addressZipCode = (TextBox)def.getWidget(SampleMeta.getWellReportToAddressZipCode());
        addScreenHandler(addressZipCode, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                if (getWellManager().getPrivateWell().getOrganizationId() == null) {
                    addressZipCode.setValue(getWellManager().getPrivateWell().getReportToAddress()
                                                        .getZipCode());
                } else {
                    addressZipCode.setValue(getWellManager().getPrivateWell().getOrganization()
                                                        .getAddress().getZipCode());
                }
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                getWellManager().getPrivateWell().getReportToAddress().setZipCode(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                addressZipCode.setQueryMode(event.getState() == State.QUERY);
            }
        });
        
        addressZipCode.addKeyDownHandler(new KeyDownHandler() {            
            public void onKeyDown(KeyDownEvent event) {
                String zip;
                SamplePrivateWellViewDO data, prevData;
                AddressDO addr;
                
                if (canCopyFromPrevious(event)) {
                    data = getWellManager().getPrivateWell();
                    prevData = getPreviousWellManager().getPrivateWell();
                    zip = "";
                    addr = prevData.getReportToAddress();
                    if (addr != null)
                        zip = addr.getZipCode();
                    data.getReportToAddress().setZipCode(zip);
                    addressZipCode.setValue(zip);
                    
                    event.preventDefault();
                    event.stopPropagation();
                    setFocusToNext(addressZipCode);
                }
            }
        });

        addressFaxPhone = (TextBox)def.getWidget(SampleMeta.getWellReportToAddressFaxPhone());
        addScreenHandler(addressFaxPhone, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                if (getWellManager().getPrivateWell().getOrganizationId() == null) {
                    addressFaxPhone.setValue(getWellManager().getPrivateWell().getReportToAddress()
                                                         .getFaxPhone());
                } else {
                    addressFaxPhone.setValue(getWellManager().getPrivateWell().getReportToAddress()
                                                         .getFaxPhone());
                }
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                getWellManager().getPrivateWell().getReportToAddress().setFaxPhone(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                addressFaxPhone.setQueryMode(event.getState() == State.QUERY);
            }
        });
        
        addressFaxPhone.addKeyDownHandler(new KeyDownHandler() {            
            public void onKeyDown(KeyDownEvent event) {
                String fax;
                SamplePrivateWellViewDO data, prevData;
                AddressDO addr;
                
                if (canCopyFromPrevious(event)) {
                    data = getWellManager().getPrivateWell();
                    prevData = getPreviousWellManager().getPrivateWell();
                    fax = "";
                    addr = prevData.getReportToAddress();
                    if (addr != null)
                        fax = addr.getFaxPhone();
                    data.getReportToAddress().setFaxPhone(fax);
                    addressFaxPhone.setValue(fax);
                    
                    event.preventDefault();
                    event.stopPropagation();
                    setFocusToNext(addressFaxPhone);
                }
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
                wellLocation.enable(event.getState() == State.QUERY ||
                                    (canEdit() && EnumSet.of(State.ADD, State.UPDATE)
                                                         .contains(event.getState())));
                wellLocation.setQueryMode(event.getState() == State.QUERY);
            }
        });
        
        wellLocation.addKeyDownHandler(new KeyDownHandler() {            
            public void onKeyDown(KeyDownEvent event) {
                String loc, multi, strt, city, state, zip;
                SamplePrivateWellViewDO data, prevData;
                AddressDO addr, prevAddr;
                
                if (canCopyFromPrevious(event)) {
                    data = getWellManager().getPrivateWell();
                    prevData = getPreviousWellManager().getPrivateWell();
                    addr = data.getLocationAddress();
                    prevAddr = prevData.getLocationAddress();                    
                    loc = prevData.getLocation();
                    multi = prevAddr.getMultipleUnit();
                    strt = prevAddr.getStreetAddress();
                    city = prevAddr.getCity();
                    state = prevAddr.getState();
                    zip = prevAddr.getZipCode();
                    
                    data.setLocation(loc);
                    addr.setMultipleUnit(multi);
                    addr.setStreetAddress(strt);
                    addr.setCity(city);
                    addr.setState(state);
                    addr.setZipCode(zip);
                    
                    wellLocation.setValue(loc);
                    locationAddrMultipleUnit.setValue(multi);
                    locationAddrStreetAddress.setValue(strt);
                    locationAddrCity.setValue(city);
                    locationAddrState.setValue(state);
                    locationAddrZipCode.setValue(zip);
                                        
                    event.preventDefault();
                    event.stopPropagation();
                    setFocusToNext(wellLocation);
                }
            }
        });

        locationAddrMultipleUnit = (TextBox)def.getWidget(SampleMeta.getWellLocationAddrMultipleUnit());
        addScreenHandler(locationAddrMultipleUnit, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                locationAddrMultipleUnit.setValue(getWellManager().getPrivateWell().getLocationAddress()
                                                              .getMultipleUnit());
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                getWellManager().getPrivateWell().getLocationAddress().setMultipleUnit(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                locationAddrMultipleUnit.enable(event.getState() == State.QUERY ||
                                                (canEdit() && EnumSet.of(State.ADD, State.UPDATE)
                                                                     .contains(event.getState())));
                locationAddrMultipleUnit.setQueryMode(event.getState() == State.QUERY);
            }
        });
        
        locationAddrMultipleUnit.addKeyDownHandler(new KeyDownHandler() {            
            public void onKeyDown(KeyDownEvent event) {
                String multi;
                SamplePrivateWellViewDO data, prevData;
                AddressDO addr, prevAddr;
                
                if (canCopyFromPrevious(event)) {
                    data = getWellManager().getPrivateWell();
                    prevData = getPreviousWellManager().getPrivateWell();
                    addr = data.getLocationAddress();
                    prevAddr = prevData.getLocationAddress();                    
                    multi = prevAddr.getMultipleUnit();
                    
                    addr.setMultipleUnit(multi);                    
                    locationAddrMultipleUnit.setValue(multi);
                                        
                    event.preventDefault();
                    event.stopPropagation();
                    setFocusToNext(locationAddrMultipleUnit);
                }
            }
        });

        locationAddrStreetAddress = (TextBox)def.getWidget(SampleMeta.getWellLocationAddrStreetAddress());
        addScreenHandler(locationAddrStreetAddress, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                locationAddrStreetAddress.setValue(getWellManager().getPrivateWell()
                                                               .getLocationAddress()
                                                               .getStreetAddress());
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                getWellManager().getPrivateWell().getLocationAddress().setStreetAddress(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                locationAddrStreetAddress.enable(event.getState() == State.QUERY ||
                                                 (canEdit() && EnumSet.of(State.ADD, State.UPDATE)
                                                                      .contains(event.getState())));
                locationAddrStreetAddress.setQueryMode(event.getState() == State.QUERY);
            }
        });
        
        locationAddrStreetAddress.addKeyDownHandler(new KeyDownHandler() {            
            public void onKeyDown(KeyDownEvent event) {
                String strt;
                SamplePrivateWellViewDO data, prevData;
                AddressDO addr, prevAddr;
                
                if (canCopyFromPrevious(event)) {
                    data = getWellManager().getPrivateWell();
                    prevData = getPreviousWellManager().getPrivateWell();
                    addr = data.getLocationAddress();
                    prevAddr = prevData.getLocationAddress();                    
                    strt = prevAddr.getStreetAddress();
                    
                    addr.setStreetAddress(strt);                    
                    locationAddrStreetAddress.setValue(strt);
                                        
                    event.preventDefault();
                    event.stopPropagation();
                    setFocusToNext(locationAddrStreetAddress);
                }
            }
        });

        locationAddrCity = (TextBox)def.getWidget(SampleMeta.getWellLocationAddrCity());
        addScreenHandler(locationAddrCity, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                locationAddrCity.setValue(getWellManager().getPrivateWell().getLocationAddress()
                                                      .getCity());
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                getWellManager().getPrivateWell().getLocationAddress().setCity(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                locationAddrCity.enable(event.getState() == State.QUERY ||
                                        (canEdit() && EnumSet.of(State.ADD, State.UPDATE)
                                                             .contains(event.getState())));
                locationAddrCity.setQueryMode(event.getState() == State.QUERY);
            }
        });
        
        locationAddrCity.addKeyDownHandler(new KeyDownHandler() {            
            public void onKeyDown(KeyDownEvent event) {
                String city;
                SamplePrivateWellViewDO data, prevData;
                AddressDO addr, prevAddr;
                
                if (canCopyFromPrevious(event)) {
                    data = getWellManager().getPrivateWell();
                    prevData = getPreviousWellManager().getPrivateWell();
                    addr = data.getLocationAddress();
                    prevAddr = prevData.getLocationAddress();                    
                    city = prevAddr.getCity();
                    
                    addr.setCity(city);
                    locationAddrCity.setValue(city);
                                        
                    event.preventDefault();
                    event.stopPropagation();
                    setFocusToNext(locationAddrCity);
                }
            }
        });

        locationAddrState = (Dropdown)def.getWidget(SampleMeta.getWellLocationAddrState());
        addScreenHandler(locationAddrState, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                locationAddrState.setSelection(getWellManager().getPrivateWell().getLocationAddress()
                                                           .getState());
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                getWellManager().getPrivateWell().getLocationAddress().setState(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                locationAddrState.enable(event.getState() == State.QUERY ||
                                         (canEdit() && EnumSet.of(State.ADD, State.UPDATE)
                                                              .contains(event.getState())));
                locationAddrState.setQueryMode(event.getState() == State.QUERY);
            }
        });
        
        locationAddrState.addKeyDownHandler(new KeyDownHandler() {            
            public void onKeyDown(KeyDownEvent event) {
                String state;
                SamplePrivateWellViewDO data, prevData;
                AddressDO addr, prevAddr;
                
                if (canCopyFromPrevious(event)) {
                    data = getWellManager().getPrivateWell();
                    prevData = getPreviousWellManager().getPrivateWell();
                    addr = data.getLocationAddress();
                    prevAddr = prevData.getLocationAddress();                    
                    state = prevAddr.getState();
                    
                    addr.setState(state);                    
                    locationAddrState.setValue(state);
                                        
                    event.preventDefault();
                    event.stopPropagation();
                    setFocusToNext(locationAddrState);
                }
            }
        });


        locationAddrZipCode = (TextBox)def.getWidget(SampleMeta.getWellLocationAddrZipCode());
        addScreenHandler(locationAddrZipCode, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                locationAddrZipCode.setValue(getWellManager().getPrivateWell().getLocationAddress()
                                                         .getZipCode());
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                getWellManager().getPrivateWell().getLocationAddress().setZipCode(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                locationAddrZipCode.enable(event.getState() == State.QUERY ||
                                           (canEdit() && EnumSet.of(State.ADD, State.UPDATE)
                                                                .contains(event.getState())));
                locationAddrZipCode.setQueryMode(event.getState() == State.QUERY);
            }
        });
        
        locationAddrZipCode.addKeyDownHandler(new KeyDownHandler() {            
            public void onKeyDown(KeyDownEvent event) {
                String zip;
                SamplePrivateWellViewDO data, prevData;
                AddressDO addr, prevAddr;
                
                if (canCopyFromPrevious(event)) {
                    data = getWellManager().getPrivateWell();
                    prevData = getPreviousWellManager().getPrivateWell();
                    addr = data.getLocationAddress();
                    prevAddr = prevData.getLocationAddress();                    
                    zip = prevAddr.getZipCode();
                    
                    addr.setZipCode(zip);                    
                    locationAddrZipCode.setValue(zip);
                                        
                    event.preventDefault();
                    event.stopPropagation();
                    setFocusToNext(locationAddrZipCode);
                }
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
                wellOwner.enable(event.getState() == State.QUERY ||
                                 (canEdit() && EnumSet.of(State.ADD, State.UPDATE)
                                                      .contains(event.getState())));
                wellOwner.setQueryMode(event.getState() == State.QUERY);
            }
        });
        
        wellOwner.addKeyDownHandler(new KeyDownHandler() {            
            public void onKeyDown(KeyDownEvent event) {
                String owner;
                SamplePrivateWellViewDO data, prevData;
                
                if (canCopyFromPrevious(event)) {
                    data = getWellManager().getPrivateWell();
                    prevData = getPreviousWellManager().getPrivateWell();
                    owner = prevData.getOwner();
                    
                    data.setOwner(owner);                    
                    wellOwner.setValue(owner);
                                        
                    event.preventDefault();
                    event.stopPropagation();
                    setFocusToNext(wellOwner);
                }
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
                wellCollector.enable(event.getState() == State.QUERY ||
                                     (canEdit() && EnumSet.of(State.ADD, State.UPDATE)
                                                          .contains(event.getState())));
                wellCollector.setQueryMode(event.getState() == State.QUERY);
            }
        });
        
        wellCollector.addKeyDownHandler(new KeyDownHandler() {            
            public void onKeyDown(KeyDownEvent event) {
                String coll;
                SamplePrivateWellViewDO data, prevData;
                
                if (canCopyFromPrevious(event)) {
                    data = getWellManager().getPrivateWell();
                    prevData = getPreviousWellManager().getPrivateWell();
                    coll = prevData.getCollector();
                    
                    data.setCollector(coll);                    
                    wellCollector.setValue(coll);
                                        
                    event.preventDefault();
                    event.stopPropagation();
                    setFocusToNext(wellCollector);
                }
            }
        });

        wellNumber = (TextBox<Integer>)def.getWidget(SampleMeta.getWellWellNumber());
        addScreenHandler(wellNumber, new ScreenEventHandler<Integer>() {
            public void onDataChange(DataChangeEvent event) {
                wellNumber.setValue(Util.toString(getWellManager().getPrivateWell().getWellNumber()));
            }

            public void onValueChange(ValueChangeEvent<Integer> event) {
                getWellManager().getPrivateWell().setWellNumber(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                wellNumber.enable(event.getState() == State.QUERY ||
                                  (canEdit() && EnumSet.of(State.ADD, State.UPDATE)
                                                       .contains(event.getState())));
                wellNumber.setQueryMode(event.getState() == State.QUERY);
            }
        });
        
        wellNumber.addKeyDownHandler(new KeyDownHandler() {            
            public void onKeyDown(KeyDownEvent event) {
                Integer wnum;
                SamplePrivateWellViewDO data, prevData;
                
                if (canCopyFromPrevious(event)) {
                    data = getWellManager().getPrivateWell();
                    prevData = getPreviousWellManager().getPrivateWell();
                    wnum = prevData.getWellNumber();
                    
                    data.setWellNumber(wnum);                    
                    wellNumber.setValue(Util.toString(wnum));
                                        
                    event.preventDefault();
                    event.stopPropagation();
                    setFocusToNext(wellNumber);
                }
            }
        });

        project = (AutoComplete<Integer>)def.getWidget(SampleMeta.getProjectName());
        addScreenHandler(project, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                SampleProjectViewDO data;
                
                try {
                    data = manager.getProjects().getFirstPermanentProject();
                    if (data != null)
                        project.setSelection(new TableDataRow(data.getProjectId(),
                                                              data.getProjectName(),
                                                              data.getProjectDescription()));
                    else
                        project.setSelection(new TableDataRow(null, "", ""));
                } catch (Exception e) {
                    Window.alert(e.getMessage());
                }
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                TableDataRow row;
                SampleProjectViewDO data;

                row = project.getSelection();
                data = null;
                try {
                    /*
                     * if a project was not selected and it there were permanent
                     * projects present then we delete the first permanent project
                     * and set the next permanent one as the first project in the list;  
                     * otherwise we modify the first existing permanent project
                     * or create a new one if none existed
                     */
                    if (row == null || row.key == null) {                        
                        manager.getProjects().removeFirstPermanentProject();
                        data = manager.getProjects().getFirstPermanentProject();
                        if (data != null) {
                            manager.getProjects().setProjectAt(data, 0);
                            
                            project.setSelection(new TableDataRow(data.getProjectId(), data.getProjectName(), data.getProjectDescription()));
                        } else {
                            project.setSelection(new TableDataRow(null, "", ""));                            
                        }
                    } else {
                        data = manager.getProjects().getFirstPermanentProject();
                        if (data == null) {
                            data = new SampleProjectViewDO();
                            data.setIsPermanent("Y");                            
                            manager.getProjects().addProjectAt(data, 0);
                        }
                        data.setProjectId((Integer)row.key);
                        data.setProjectName((String)row.cells.get(0).getValue());
                        data.setProjectDescription((String)row.cells.get(1).getValue());                        
                    } 
                } catch (Exception e) {
                    Window.alert(e.getMessage());
                }
            }
            public void onStateChange(StateChangeEvent<State> event) {
                project.enable(event.getState() == State.QUERY ||
                               (canEdit() && EnumSet.of(State.ADD, State.UPDATE)
                                                    .contains(event.getState())));
                project.setQueryMode(event.getState() == State.QUERY);
            }
        });

        project.addGetMatchesHandler(new GetMatchesHandler() {
            public void onGetMatches(GetMatchesEvent event) {
                TableDataRow row;
                ProjectDO data;
                ArrayList<ProjectDO> list;
                ArrayList<TableDataRow> model;

                window.setBusy();
                try {
                    list = projectService.callList("fetchActiveByName", QueryFieldUtil.parseAutocomplete(event.getMatch()));
                    model = new ArrayList<TableDataRow>();
                    for (int i = 0; i < list.size(); i++ ) {
                        row = new TableDataRow(4);
                        data = list.get(i);

                        row.key = data.getId();
                        row.cells.get(0).value = data.getName();
                        row.cells.get(1).value = data.getDescription();

                        model.add(row);
                    }
                    project.showAutoMatches(model);
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
                projectLookup.enable(event.getState() == State.DISPLAY ||
                                     (canEdit() && EnumSet.of(State.ADD, State.UPDATE)
                                                          .contains(event.getState())));
            }
        });

        billTo = (AutoComplete<Integer>)def.getWidget(SampleMeta.getBillTo());
        addScreenHandler(billTo, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                SampleOrganizationViewDO billToOrg;

                try {
                    billToOrg = manager.getOrganizations().getBillTo();
                    if (billToOrg != null)
                        billTo.setSelection(billToOrg.getOrganizationId(), billToOrg.getOrganizationName());
                    else
                        billTo.setSelection(null, "");
                } catch (Exception e) {
                    Window.alert(e.getMessage());
                }
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                TableDataRow row;
                SampleOrganizationViewDO data;
                OrganizationDO org;

                row = billTo.getSelection();

                try {
                    if (row == null || row.key == null) {
                        manager.getOrganizations().removeBillTo();
                        billTo.setSelection(null, "");
                        return;
                    }

                    data = manager.getOrganizations().getBillTo();
                    if (data == null) {
                        data = new SampleOrganizationViewDO();
                        manager.getOrganizations().setBillTo(data);
                    }

                    org = (OrganizationDO)row.data;
                    if (org != null)
                        getSampleOrganization(org, data);

                    billTo.setSelection(data.getOrganizationId(),  data.getOrganizationName());                    
                } catch (Exception e) {
                    Window.alert(e.getMessage());
                }
            }

            public void onStateChange(StateChangeEvent<State> event) {
                billTo.enable(canEdit() && EnumSet.of(State.ADD, State.UPDATE).contains(event.getState()));
                billTo.setQueryMode(event.getState() == State.QUERY);
            }
        });
        
        billTo.addKeyDownHandler(new KeyDownHandler() {            
            public void onKeyDown(KeyDownEvent event) {
                if (canCopyFromPrevious(event)) {
                    SampleOrganizationViewDO data, prevData;
                    SampleOrganizationManager man;

                    try {                        
                        man = manager.getOrganizations();
                        prevData = previousManager.getOrganizations().getBillTo();
                        data = man.getBillTo();
                        
                        if (prevData == null) {
                            /*
                             * if there was no bill-to in the previous sample
                             * then we try to remove the bill-to for this sample
                             * if there is one; we also blank out the autocomplete  
                             */
                            man.removeBillTo();
                            billTo.setSelection(null, "");
                        } else {
                            /*
                             * if there was a bill-to in the previous sample
                             * then we create a DO for it if there isn't one and
                             * set all its relevant fields; we also set the value
                             * in the autocomplete
                             */
                            if (data == null) {
                                data = new SampleOrganizationViewDO();
                                man.setBillTo(data);                                
                            }
                            getSampleOrganization(prevData, data);
                            billTo.setSelection(data.getOrganizationId(), data.getOrganizationName());     
                        }                        
                    } catch (Exception e) {
                        Window.alert(e.getMessage());
                    }
                    
                    event.preventDefault();
                    event.stopPropagation();
                    setFocusToNext(billTo);
                }
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
    }

    public ArrayList<QueryData> getQueryFields() {
        QueryData domain;
        ArrayList<QueryData> fields;

        fields = super.getQueryFields();

        if (fields.size() > 0) {
            domain = new QueryData();
            domain.key = SampleMeta.getDomain();
            domain.query = SampleManager.WELL_DOMAIN_FLAG;
            domain.type = QueryData.Type.STRING;
            fields.add(domain);
        }       

        return fields;
    }        

    public void setData(SampleManager manager) {
        this.manager = manager;
        wellManager = null;
        loaded = false;
    }
    
    public void setPreviousData(SampleManager previousManager) {
        this.previousManager = previousManager;
        previousWellManager = null;
    }

    public void draw() {
        if ( !loaded)
            DataChangeEvent.fire(this);

        loaded = true;
    }

    private void onProjectLookupClick() {
        try {
            if (projectScreen == null) {
                projectScreen = new SampleProjectLookupScreen();
                projectScreen.addActionHandler(new ActionHandler<SampleProjectLookupScreen.Action>() {
                    public void onAction(ActionEvent<SampleProjectLookupScreen.Action> event) {
                        if (event.getAction() == SampleProjectLookupScreen.Action.OK) {
                            DataChangeEvent.fire(screen, project);

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

    private void getOrganizationMatches(String match, AutoComplete widget) {
        TableDataRow row;
        OrganizationDO data;
        ArrayList<OrganizationDO> list;
        ArrayList<TableDataRow> model;

        window.setBusy();
        try {
            list = orgService.callList("fetchByIdOrName", QueryFieldUtil.parseAutocomplete(match));
            model = new ArrayList<TableDataRow>();
            for (int i = 0; i < list.size(); i++ ) {
                row = new TableDataRow(4);
                data = list.get(i);

                row.key = data.getId();
                row.data = data;
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
                organizationScreen = new SampleOrganizationLookupScreen();
                organizationScreen.setCanAddReportTo(false);

                organizationScreen.addActionHandler(new ActionHandler<SampleOrganizationLookupScreen.Action>() {
                    public void onAction(ActionEvent<SampleOrganizationLookupScreen.Action> event) {
                        if (event.getAction() == SampleOrganizationLookupScreen.Action.OK) {
                            DataChangeEvent.fire(screen, billTo);
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

    private void enableReportToFields(boolean enable) {
        addressMultipleUnit.enable(enable);
        addressStreetAddress.enable(enable);
        addressCity.enable(enable);
        addressState.enable(enable);
        addressZipCode.enable(enable);
        addressWorkPhone.enable(enable);
        addressFaxPhone.enable(enable);
    }

    private void initializeDropdowns() {
        ArrayList<TableDataRow> model;

        try {
            sampleReleasedId = DictionaryCache.getIdBySystemName("sample_released");
        } catch (Exception e) {
            Window.alert(e.getMessage());
            window.close();
        }

        // state dropdown
        model = new ArrayList<TableDataRow>();
        model.add(new TableDataRow(null, ""));
        for (DictionaryDO d : CategoryCache.getBySystemName("state"))
            model.add(new TableDataRow(d.getEntry(), d.getEntry()));

        addressState.setModel(model);
        locationAddrState.setModel(model);
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
                tableE = (TableFieldErrorException)ex;
                tableWid = (TableWidget)def.getWidget(tableE.getTableKey());
                tableWid.setCellException(tableE.getRowIndex(), tableE.getFieldName(), tableE);
            } else if (ex instanceof FieldErrorException) {
                fieldE = (FieldErrorException)ex;
                field = (HasField)def.getWidget(fieldE.getFieldName());

                if (field != null)
                    field.addException(fieldE);
            }
        }
    }

    private SamplePrivateWellManager getWellManager() {
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
    
    private SamplePrivateWellManager getPreviousWellManager() {
        if (previousWellManager == null) {
            try {
                previousWellManager = (SamplePrivateWellManager)previousManager.getDomainManager();
            } catch (Exception e) {
                previousWellManager = SamplePrivateWellManager.getInstance();
            }
        }
        return previousWellManager;
    }
    
    private boolean canCopyFromPrevious(KeyDownEvent event) {
        return (previousManager != null) && event.getNativeKeyCode() == 113;
    }
    
    private void setFocusToNext(Widget currWidget) {
        NativeEvent event;
        
        event = Document.get().createKeyPressEvent(false, false, false, false, KeyCodes.KEY_TAB, KeyCodes.KEY_TAB);        
        KeyPressEvent.fireNativeEvent(event, currWidget);
    }
    
    private void setAddress(AddressDO data) {
        if (data == null) {
            data = getWellManager().getPrivateWell().getReportToAddress();
            data.setMultipleUnit(null);
            data.setStreetAddress(null);
            data.setCity(null);
            data.setState(null);
            data.setZipCode(null);
            data.setWorkPhone(null);
            data.setFaxPhone(null);
        }
        
        addressMultipleUnit.setValue(data.getMultipleUnit());
        addressStreetAddress.setValue(data.getStreetAddress());
        addressCity.setValue(data.getCity());
        addressState.setValue(data.getState());
        addressZipCode.setValue(data.getZipCode());
        addressWorkPhone.setValue(data.getWorkPhone());
        addressFaxPhone.setValue(data.getFaxPhone());
    }
    
    private void getSampleOrganization(OrganizationDO org, SampleOrganizationViewDO data) {
        AddressDO addr;
        
        addr = org.getAddress();
        data.setOrganizationId(org.getId());
        data.setOrganizationName(org.getName());
        data.setOrganizationMultipleUnit(addr.getMultipleUnit());
        data.setOrganizationStreetAddress(addr.getStreetAddress());
        data.setOrganizationCity(addr.getCity());
        data.setOrganizationState(addr.getState());
        data.setOrganizationZipCode(addr.getZipCode());
        data.setOrganizationCountry(addr.getCountry());
    }
    
    private void getSampleOrganization(SampleOrganizationViewDO prevData, SampleOrganizationViewDO data) {
        data.setOrganizationId(prevData.getOrganizationId());
        data.setOrganizationName(prevData.getOrganizationName());
        data.setOrganizationMultipleUnit(prevData.getOrganizationMultipleUnit());
        data.setOrganizationStreetAddress(prevData.getOrganizationStreetAddress());
        data.setOrganizationCity(prevData.getOrganizationCity());
        data.setOrganizationState(prevData.getOrganizationState());
        data.setOrganizationZipCode(prevData.getOrganizationZipCode());
        data.setOrganizationCountry(prevData.getOrganizationCountry());
    }
}
