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
import org.openelis.domain.OrganizationDO;
import org.openelis.domain.ProjectDO;
import org.openelis.domain.SampleEnvironmentalDO;
import org.openelis.domain.SampleOrganizationViewDO;
import org.openelis.domain.SampleProjectViewDO;
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
import org.openelis.gwt.widget.AppButton;
import org.openelis.gwt.widget.AutoComplete;
import org.openelis.gwt.widget.CheckBox;
import org.openelis.gwt.widget.HasField;
import org.openelis.gwt.widget.QueryFieldUtil;
import org.openelis.gwt.widget.ScreenWindow;
import org.openelis.gwt.widget.ScreenWindowInt;
import org.openelis.gwt.widget.TextBox;
import org.openelis.gwt.widget.table.TableDataRow;
import org.openelis.gwt.widget.table.TableWidget;
import org.openelis.manager.SampleEnvironmentalManager;
import org.openelis.manager.SampleManager;
import org.openelis.manager.SampleOrganizationManager;
import org.openelis.meta.SampleMeta;
import org.openelis.modules.organization.client.OrganizationService;
import org.openelis.modules.project.client.ProjectService;

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

public class EnvironmentalTab extends Screen {
    private TextBox                        location, description, collector, collectorPhone;
    private TextBox<Integer>               priority;
    private AutoComplete<Integer>          project, reportTo, billTo;
    private AppButton                      billToLookup, reportToLookup, projectLookup,
                                           locationLookup;
    private CheckBox                       isHazardous;
    
    private EnvironmentalTab               screen;
    private SampleLocationLookupScreen     locationScreen;
    private SampleOrganizationLookupScreen organizationScreen;
    private SampleProjectLookupScreen      projectScreen;

    private SampleManager                  manager, previousManager;
    private SampleEnvironmentalManager     envManager, previousEnvManager;

    private Integer                        sampleReleasedId;

    protected boolean                      loaded = false;

    public EnvironmentalTab(ScreenWindowInt window) throws Exception {
        this(null, window);
    }
    
    public EnvironmentalTab(ScreenDefInt def, ScreenWindowInt window) throws Exception {
        if (def == null)
            drawScreen((ScreenDefInt)GWT.create(EnvironmentalTabDef.class));
        else
            setDefinition(def);
        
        setWindow(window);

        initialize();
        initializeDropdowns();
    }

    public void initialize() {     
        screen = this;
        
        isHazardous = (CheckBox)def.getWidget(SampleMeta.getEnvIsHazardous());
        addScreenHandler(isHazardous, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                isHazardous.setValue(getEnvManager().getEnvironmental().getIsHazardous());
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                getEnvManager().getEnvironmental().setIsHazardous(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                isHazardous.enable(event.getState() == State.QUERY ||
                                   (canEdit() && EnumSet.of(State.ADD, State.UPDATE)
                                                        .contains(event.getState())));
                isHazardous.setQueryMode(event.getState() == State.QUERY);
            }
        });    
        
        isHazardous.addKeyDownHandler(new KeyDownHandler() {            
            public void onKeyDown(KeyDownEvent event) {
                if (canCopyFromPrevious(event)) {
                    String haz;

                    haz = getPreviousEnvManager().getEnvironmental().getIsHazardous();
                    getEnvManager().getEnvironmental().setIsHazardous(haz);
                    isHazardous.setValue(haz);
                    
                    event.preventDefault();
                    event.stopPropagation();                   
                    setFocusToNext(isHazardous);
                }
            }
        });

        priority = (TextBox<Integer>)def.getWidget(SampleMeta.getEnvPriority());
        addScreenHandler(priority, new ScreenEventHandler<Integer>() {
            public void onDataChange(DataChangeEvent event) {
                priority.setValue(getEnvManager().getEnvironmental().getPriority());
            }

            public void onValueChange(ValueChangeEvent<Integer> event) {
                getEnvManager().getEnvironmental().setPriority(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                priority.enable(event.getState() == State.QUERY ||
                                (canEdit() && EnumSet.of(State.ADD, State.UPDATE)
                                                     .contains(event.getState())));
                priority.setQueryMode(event.getState() == State.QUERY);
            }
        });
        
        priority.addKeyDownHandler(new KeyDownHandler() {            
            public void onKeyDown(KeyDownEvent event) {
                if (canCopyFromPrevious(event)) {
                    Integer pr;

                    pr = getPreviousEnvManager().getEnvironmental().getPriority();
                    getEnvManager().getEnvironmental().setPriority(pr) ;
                    priority.setValue(pr);
                    
                    event.preventDefault();
                    event.stopPropagation();
                    setFocusToNext(priority);
                }
            }
        });

        description = (TextBox)def.getWidget(SampleMeta.getEnvDescription());
        addScreenHandler(description, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                description.setValue(getEnvManager().getEnvironmental().getDescription());
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                getEnvManager().getEnvironmental().setDescription(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                description.enable(event.getState() == State.QUERY ||
                                   (canEdit() && EnumSet.of(State.ADD, State.UPDATE)
                                                        .contains(event.getState())));
                description.setQueryMode(event.getState() == State.QUERY);
            }
        });
        
        description.addKeyDownHandler(new KeyDownHandler() {            
            public void onKeyDown(KeyDownEvent event) {
                if (canCopyFromPrevious(event)) {
                    String desc;

                    desc = getPreviousEnvManager().getEnvironmental().getDescription();
                    getEnvManager().getEnvironmental().setDescription(desc);
                    description.setValue(desc);
                    
                    event.preventDefault();
                    event.stopPropagation();
                    setFocusToNext(description);
                }
            }
        });

        collector = (TextBox)def.getWidget(SampleMeta.getEnvCollector());
        addScreenHandler(collector, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                collector.setValue(getEnvManager().getEnvironmental().getCollector());
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                getEnvManager().getEnvironmental().setCollector(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                collector.enable(event.getState() == State.QUERY ||
                                 (canEdit() && EnumSet.of(State.ADD, State.UPDATE)
                                                      .contains(event.getState())));
                collector.setQueryMode(event.getState() == State.QUERY);
            }
        });
        
        collector.addKeyDownHandler(new KeyDownHandler() {            
            public void onKeyDown(KeyDownEvent event) {
                if (canCopyFromPrevious(event)) {
                    String coll;

                    coll = getPreviousEnvManager().getEnvironmental().getCollector();
                    getEnvManager().getEnvironmental().setCollector(coll);
                    collector.setValue(coll);
                    
                    event.preventDefault();
                    event.stopPropagation();
                    setFocusToNext(collector);
                }
            }
        });

        collectorPhone = (TextBox)def.getWidget(SampleMeta.getEnvCollectorPhone());
        addScreenHandler(collectorPhone, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                collectorPhone.setValue(getEnvManager().getEnvironmental().getCollectorPhone());
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                getEnvManager().getEnvironmental().setCollectorPhone(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                collectorPhone.enable(event.getState() == State.QUERY ||
                                      (canEdit() && EnumSet.of(State.ADD, State.UPDATE)
                                                           .contains(event.getState())));
                collectorPhone.setQueryMode(event.getState() == State.QUERY);
            }
        });
        
        collectorPhone.addKeyDownHandler(new KeyDownHandler() {            
            public void onKeyDown(KeyDownEvent event) {
                if (canCopyFromPrevious(event)) {
                    String phone;

                    phone = getPreviousEnvManager().getEnvironmental().getCollectorPhone();
                    getEnvManager().getEnvironmental().setCollectorPhone(phone);
                    collectorPhone.setValue(phone);
                    
                    event.preventDefault();
                    event.stopPropagation();
                    setFocusToNext(collectorPhone);
                }
            }
        });

        location = (TextBox)def.getWidget(SampleMeta.getEnvLocation());
        addScreenHandler(location, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                location.setValue(getEnvManager().getEnvironmental().getLocation());
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                getEnvManager().getEnvironmental().setLocation(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                location.enable(event.getState() == State.QUERY ||
                                (canEdit() && EnumSet.of(State.ADD, State.UPDATE)
                                                     .contains(event.getState())));
                location.setQueryMode(event.getState() == State.QUERY);
            }
        });    
        
        location.addKeyDownHandler(new KeyDownHandler() {            
            public void onKeyDown(KeyDownEvent event) {
                if (canCopyFromPrevious(event)) {
                    String loc;
                    SampleEnvironmentalDO data, prevData;
                    AddressDO addr, prevAddr;
                    
                    data = getEnvManager().getEnvironmental();
                    prevData = getPreviousEnvManager().getEnvironmental();
                    addr = data.getLocationAddress();
                    prevAddr = prevData.getLocationAddress();
                    
                    loc = prevData.getLocation();
                    data.setLocation(loc);
                    addr.setMultipleUnit(prevAddr.getMultipleUnit());
                    addr.setStreetAddress(prevAddr.getStreetAddress());
                    addr.setCity(prevAddr.getCity());
                    addr.setState(prevAddr.getState());
                    addr.setZipCode(prevAddr.getZipCode());
                    addr.setCountry(prevAddr.getCountry());
                    location.setValue(loc);
                    
                    event.preventDefault();
                    event.stopPropagation();
                    setFocusToNext(location);
                }
            }
        });

        project = (AutoComplete<Integer>)def.getWidget(SampleMeta.getProjectName());
        addScreenHandler(project, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                try {
                    SampleProjectViewDO data = manager.getProjects()
                                                           .getFirstPermanentProject();

                    if (data != null)
                        project.setSelection(new TableDataRow(data.getProjectId(), data.getProjectName(), data.getProjectDescription()));
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
                     * if a project was not selected and if there were permanent
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
                    list = ProjectService.get().fetchActiveByName(QueryFieldUtil.parseAutocomplete(event.getMatch()));
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
                TableDataRow row;
                SampleOrganizationViewDO data;    
                OrganizationDO org;    
                Integer orgId;                

                row = reportTo.getSelection();
                try {
                    if (row == null || row.key == null) {
                        manager.getOrganizations().removeReportTo();
                        reportTo.setSelection(null, "");
                        return;
                    }

                    data = manager.getOrganizations().getReportTo();
                    if (data == null) {
                        data = new SampleOrganizationViewDO();
                        manager.getOrganizations().setReportTo(data);
                    }

                    org = (OrganizationDO)row.data;
                    if (org != null)
                        getSampleOrganization(org, data);
                    
                    orgId = data.getOrganizationId();
                    reportTo.setSelection(orgId, data.getOrganizationName());

                    showHoldRefuseWarning(data.getOrganizationId(), data.getOrganizationName());                    
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
        
        reportTo.addKeyDownHandler(new KeyDownHandler() {            
            public void onKeyDown(KeyDownEvent event) {
                if (canCopyFromPrevious(event)) {
                    SampleOrganizationViewDO data, prevData;
                    SampleOrganizationManager man;

                    try {                        
                        man = manager.getOrganizations();
                        prevData = previousManager.getOrganizations().getReportTo();
                        data = man.getReportTo();
                        
                        if (prevData == null) {
                            /*
                             * if there was no report-to in the previous sample
                             * then we try to remove the report-to for this sample
                             * if there is one; we also blank out the autocomplete  
                             */
                            man.removeReportTo();
                            reportTo.setSelection(null, "");
                        } else {
                            /*
                             * if there was a report-to in the previous sample
                             * then we create a DO for it if there isn't one and
                             * set all its relevant fields; we also set the value
                             * in the autocomplete
                             */
                            if (data == null) {
                                data = new SampleOrganizationViewDO();
                                man.setReportTo(data);                                
                            }
                            getSampleOrganization(prevData, data);
                            reportTo.setSelection(data.getOrganizationId(), data.getOrganizationName());     
                        }                        
                    } catch (Exception e) {
                        Window.alert(e.getMessage());
                    }
                    
                    event.preventDefault();
                    event.stopPropagation();
                    setFocusToNext(reportTo);
                }
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
                    
                    billTo.setSelection(data.getOrganizationId(), data.getOrganizationName());

                    showHoldRefuseWarning(data.getOrganizationId(), data.getOrganizationName());
                } catch (Exception e) {
                    Window.alert(e.getMessage());
                }
            }

            public void onStateChange(StateChangeEvent<State> event) {
                billTo.enable(canEdit() && EnumSet.of(State.ADD, State.UPDATE)
                                                   .contains(event.getState()));
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

        locationLookup = (AppButton)def.getWidget("locButton");
        addScreenHandler(locationLookup, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                onLocationLookupClick();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                locationLookup.enable(event.getState() == State.DISPLAY ||
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
            domain.query = SampleManager.ENVIRONMENTAL_DOMAIN_FLAG;
            domain.type = QueryData.Type.STRING;
            fields.add(domain);
        }

        return fields;
    }

    public void setData(SampleManager manager) {
        this.manager = manager;
        envManager = null;
        loaded = false;
    }
    
    public void setPreviousData(SampleManager previousManager) {
        this.previousManager = previousManager;
        previousEnvManager = null;
    }

    public void draw() {
        if ( !loaded)
            DataChangeEvent.fire(this);

        loaded = true;
    }

    private void getOrganizationMatches(String match, AutoComplete widget) {
        TableDataRow row;
        OrganizationDO data;
        ArrayList<OrganizationDO> list;
        ArrayList<TableDataRow> model;

        window.setBusy();
        try {
            list = OrganizationService.get().fetchByIdOrName(QueryFieldUtil.parseAutocomplete(match));
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

    private void onOrganizationLookupClick() {
        try {
            if (organizationScreen == null) {
                organizationScreen = new SampleOrganizationLookupScreen();

                organizationScreen.addActionHandler(new ActionHandler<SampleOrganizationLookupScreen.Action>() {
                    public void onAction(ActionEvent<SampleOrganizationLookupScreen.Action> event) {
                        if (event.getAction() == SampleOrganizationLookupScreen.Action.OK) {
                            DataChangeEvent.fire(screen, reportTo);
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

    private void onLocationLookupClick() {
        try {
            if (locationScreen == null) {
                locationScreen = new SampleLocationLookupScreen();

                locationScreen.addActionHandler(new ActionHandler<SampleLocationLookupScreen.Action>() {
                    public void onAction(ActionEvent<SampleLocationLookupScreen.Action> event) {
                        if (event.getAction() == SampleLocationLookupScreen.Action.OK) {
                            DataChangeEvent.fire(screen, location);
                        }
                    }
                });
            }

            ScreenWindow modal = new ScreenWindow(ScreenWindow.Mode.DIALOG);
            modal.setName(consts.get("sampleLocation"));
            modal.setContent(locationScreen);

            SampleEnvironmentalDO envDO = ((SampleEnvironmentalManager)manager.getDomainManager()).getEnvironmental();

            locationScreen.setScreenState(state);
            locationScreen.setEnvDO(envDO);
        } catch (Exception e) {
            e.printStackTrace();
            Window.alert("error: " + e.getMessage());
            return;
        }
    }
    
    private void initializeDropdowns() {
        try {
            sampleReleasedId = DictionaryCache.getIdBySystemName("sample_released");
        } catch (Exception e) {
            Window.alert(e.getMessage());
            window.close();
        }
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

    private SampleEnvironmentalManager getEnvManager() {
        if (envManager == null) {
            try {
                envManager = (SampleEnvironmentalManager)manager.getDomainManager();
            } catch (Exception e) {
                envManager = SampleEnvironmentalManager.getInstance();
                manager = SampleManager.getInstance();
                manager.getSample().setDomain(SampleManager.ENVIRONMENTAL_DOMAIN_FLAG);
            }
        }
        return envManager;
    }
    
    private SampleEnvironmentalManager getPreviousEnvManager() {
        if (previousEnvManager == null) {
            try {
                previousEnvManager = (SampleEnvironmentalManager)previousManager.getDomainManager();
            } catch (Exception e) {
                previousEnvManager = SampleEnvironmentalManager.getInstance();
                previousEnvManager.getEnvironmental().setIsHazardous("N");
            }
        }
        return previousEnvManager;
    }
    
    private boolean canCopyFromPrevious(KeyDownEvent event) {
        return (previousManager != null) && event.getNativeKeyCode() == 113;
    }
    
    private void setFocusToNext(Widget currWidget) {
        NativeEvent event;
        
        event = Document.get().createKeyPressEvent(false, false, false, false, KeyCodes.KEY_TAB, KeyCodes.KEY_TAB);        
        KeyPressEvent.fireNativeEvent(event, currWidget);
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
    
    private void showHoldRefuseWarning(Integer orgId, String name) throws Exception {
        if (SampleOrganizationUtility.isHoldRefuseSampleForOrg(orgId)) 
            Window.alert(consts.get("orgMarkedAsHoldRefuseSample")+ "'"+ name+"'");
    }
}