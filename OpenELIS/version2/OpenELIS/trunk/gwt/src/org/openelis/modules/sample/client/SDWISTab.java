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
import org.openelis.domain.PWSDO;
import org.openelis.domain.ProjectDO;
import org.openelis.domain.SampleOrganizationViewDO;
import org.openelis.domain.SampleProjectViewDO;
import org.openelis.domain.SampleSDWISViewDO;
import org.openelis.gwt.common.DataBaseUtil;
import org.openelis.gwt.common.FieldErrorException;
import org.openelis.gwt.common.NotFoundException;
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
import org.openelis.gwt.screen.Screen.State;
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
import org.openelis.manager.SampleSDWISManager;
import org.openelis.meta.SampleMeta;
import org.openelis.modules.pws.client.PWSScreen;

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

public class SDWISTab extends Screen {
    private Dropdown<Integer>              sampleTypeId, sampleCategoryId;
    private TextBox                        pwsId, pwsName, facilityId,
                                           samplePointId, pointDesc, collector;
    private TextBox<Integer>               stateLabId;
    private AutoComplete<Integer>          reportTo, billTo, project;
    private AppButton                      pwsButton, projectLookup, reportToLookup, billToLookup;

    private SDWISTab                       screen;
    private SampleProjectLookupScreen      projectScreen;
    private SampleOrganizationLookupScreen organizationScreen;

    protected ScreenService                projectService, orgService, pwsService;

    private SampleManager                  manager, previousManager;
    private SampleSDWISManager             sdwisManager, previousSDWISManager;

    private Integer                        sampleReleasedId;

    protected boolean                      loaded = false;

    public SDWISTab(ScreenWindowInt window) throws Exception {
        this(null, window);
    }
    
    public SDWISTab(ScreenDefInt def, ScreenWindowInt window) throws Exception {
        if (def == null)
            drawScreen((ScreenDefInt)GWT.create(SDWISTabDef.class));
        else
            setDefinition(def);
        
        setWindow(window);
        
        projectService = new ScreenService("controller?service=org.openelis.modules.project.server.ProjectService");
        orgService = new ScreenService("controller?service=org.openelis.modules.organization.server.OrganizationService");
        pwsService = new ScreenService("controller?service=org.openelis.modules.pws.server.PWSService");
        
        initialize();
        initializeDropdowns();
    }

    public void initialize() {
        screen = this;
        
        pwsId = (TextBox)def.getWidget(SampleMeta.getSDWISPwsNumber0());
        addScreenHandler(pwsId, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                pwsId.setValue(getSDWISManager().getSDWIS().getPwsNumber0());
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                boolean clearValue;
                PWSDO data;

                clearValue = false;
                if (!DataBaseUtil.isEmpty(event.getValue())) { 
                    try {
                        data = pwsService.call("fetchPwsByNumber0", event.getValue());
                        getSDWISManager().getSDWIS().setPwsId(data.getId());
                        getSDWISManager().getSDWIS().setPwsName(data.getName());
                        getSDWISManager().getSDWIS().setPwsNumber0(data.getNumber0());
                        pwsName.setValue(data.getName());
                    } catch (ValidationErrorsList e) {
                        showErrors(e);
                        clearValue = true;
                    } catch (NotFoundException e) {
                        clearValue = true;
                    }catch (Exception e) {
                        Window.alert("pwsId valueChange: " + e.getMessage());
                    }
                } else {
                    clearValue = true;
                }
                
                if (clearValue) {
                    getSDWISManager().getSDWIS().setPwsId(null);
                    getSDWISManager().getSDWIS().setPwsName(null);
                    getSDWISManager().getSDWIS().setPwsNumber0(null);
                    pwsId.setValue(null);
                    pwsName.setValue(null);
                }
                    
            }

            public void onStateChange(StateChangeEvent<State> event) {
                pwsId.enable(event.getState() == State.QUERY ||
                             (canEdit() && EnumSet.of(State.ADD, State.UPDATE).contains(event.getState())));
                pwsId.setQueryMode(event.getState() == State.QUERY);
            }
        });
        
        pwsId.addKeyDownHandler(new KeyDownHandler() {            
            public void onKeyDown(KeyDownEvent event) {
                if (canCopyFromPrevious(event)) {
                    String pnum, pname;
                    SampleSDWISViewDO data, prevData;

                    data = getSDWISManager().getSDWIS();
                    prevData = getPreviousSDWISManager().getSDWIS();
                    pnum = prevData.getPwsNumber0();
                    pname = prevData.getPwsName();
                    
                    data.setPwsNumber0(pnum);                    
                    data.setPwsName(pname);                    
                    pwsId.setValue(pnum);
                    pwsName.setValue(pname);
                    
                    event.preventDefault();
                    event.stopPropagation();                   
                    setFocusToNext(pwsId);
                }
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
        
        stateLabId.addKeyDownHandler(new KeyDownHandler() {            
            public void onKeyDown(KeyDownEvent event) {
                if (canCopyFromPrevious(event)) {
                    Integer labId;

                    labId = getPreviousSDWISManager().getSDWIS().getStateLabId();
                    getSDWISManager().getSDWIS().setStateLabId(labId);
                    stateLabId.setValue(labId);
                    
                    event.preventDefault();
                    event.stopPropagation();                   
                    setFocusToNext(stateLabId);
                }
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
        
        facilityId.addKeyDownHandler(new KeyDownHandler() {            
            public void onKeyDown(KeyDownEvent event) {
                if (canCopyFromPrevious(event)) {
                    String facId;

                    facId = getPreviousSDWISManager().getSDWIS().getFacilityId();
                    getSDWISManager().getSDWIS().setFacilityId(facId);
                    facilityId.setValue(facId);
                    
                    event.preventDefault();
                    event.stopPropagation();                   
                    setFocusToNext(facilityId);
                }
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
        
        sampleTypeId.addKeyDownHandler(new KeyDownHandler() {            
            public void onKeyDown(KeyDownEvent event) {
                if (canCopyFromPrevious(event)) {
                    Integer typeId;

                    typeId = getPreviousSDWISManager().getSDWIS().getSampleTypeId();
                    getSDWISManager().getSDWIS().setSampleTypeId(typeId);
                    sampleTypeId.setValue(typeId);
                    
                    event.preventDefault();
                    event.stopPropagation();                   
                    setFocusToNext(sampleTypeId);
                }
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
        
        sampleCategoryId.addKeyDownHandler(new KeyDownHandler() {            
            public void onKeyDown(KeyDownEvent event) {
                if (canCopyFromPrevious(event)) {
                    Integer catId;

                    catId = getPreviousSDWISManager().getSDWIS().getSampleCategoryId();
                    getSDWISManager().getSDWIS().setSampleCategoryId(catId);
                    sampleCategoryId.setValue(catId);
                    
                    event.preventDefault();
                    event.stopPropagation();                   
                    setFocusToNext(sampleCategoryId);
                }
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
        
        samplePointId.addKeyDownHandler(new KeyDownHandler() {            
            public void onKeyDown(KeyDownEvent event) {
                if (canCopyFromPrevious(event)) {
                    String pntId;

                    pntId = getPreviousSDWISManager().getSDWIS().getSamplePointId();
                    getSDWISManager().getSDWIS().setSamplePointId(pntId);
                    samplePointId.setValue(pntId);
                    
                    event.preventDefault();
                    event.stopPropagation();                   
                    setFocusToNext(samplePointId);
                }
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
        
        pointDesc.addKeyDownHandler(new KeyDownHandler() {            
            public void onKeyDown(KeyDownEvent event) {
                if (canCopyFromPrevious(event)) {
                    String loc;

                    loc = getPreviousSDWISManager().getSDWIS().getLocation();
                    getSDWISManager().getSDWIS().setLocation(loc);
                    pointDesc.setValue(loc);
                    
                    event.preventDefault();
                    event.stopPropagation();                   
                    setFocusToNext(pointDesc);
                }
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
        
        collector.addKeyDownHandler(new KeyDownHandler() {            
            public void onKeyDown(KeyDownEvent event) {
                if (canCopyFromPrevious(event)) {
                    String coll;

                    coll = getPreviousSDWISManager().getSDWIS().getCollector();
                    getSDWISManager().getSDWIS().setCollector(coll);
                    collector.setValue(coll);
                    
                    event.preventDefault();
                    event.stopPropagation();                   
                    setFocusToNext(collector);
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

                } catch (Exception e) {
                    Window.alert(e.getMessage());
                }
            }

            public void onStateChange(StateChangeEvent<State> event) {
                billTo.enable(canEdit() && EnumSet.of(State.ADD, State.UPDATE).contains(event.getState()));
            }
        });

        billTo.addGetMatchesHandler(new GetMatchesHandler() {
            public void onGetMatches(GetMatchesEvent event) {
                getOrganizationMatches(event.getMatch(), billTo);
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
    
    public void setPreviousData(SampleManager previousManager) {
        this.previousManager = previousManager;
        previousSDWISManager = null;
    }

    public void draw() {
        if ( !loaded)
            DataChangeEvent.fire(this);

        loaded = true;
    }

    private void openPwsScreen() {
        PWSScreen pwsScreen;
        ScreenWindow modal;

        try {
            pwsScreen = new PWSScreen(pwsId.getValue());

            pwsScreen.addActionHandler(new ActionHandler<PWSScreen.Action>() {
                public void onAction(ActionEvent<PWSScreen.Action> event) {
                    PWSDO pwsDO;
                    if (state == State.ADD || state == State.UPDATE) {
                        if (event.getAction() == PWSScreen.Action.SELECT) {
                            pwsDO = (PWSDO)event.getData();
                            getSDWISManager().getSDWIS().setPwsId(pwsDO.getId());
                            getSDWISManager().getSDWIS().setPwsName(pwsDO.getName());
                            getSDWISManager().getSDWIS().setPwsNumber0(pwsDO.getNumber0());

                            pwsId.clearExceptions();
                            DataChangeEvent.fire(screen);
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

    private void initializeDropdowns() {
        ArrayList<TableDataRow> model;

        try {
            sampleReleasedId = DictionaryCache.getIdBySystemName("sample_released");
        } catch (Exception e) {
            Window.alert(e.getMessage());
            window.close();
        }

        // sample type dropdown
        model = new ArrayList<TableDataRow>();
        model.add(new TableDataRow(null, ""));
        for (DictionaryDO d : CategoryCache.getBySystemName("sdwis_sample_type"))
            model.add(new TableDataRow(d.getId(), d.getEntry()));

        sampleTypeId.setModel(model);

        // sample category dropdown
        model = new ArrayList<TableDataRow>();
        model.add(new TableDataRow(null, ""));
        for (DictionaryDO d : CategoryCache.getBySystemName("sdwis_sample_category"))
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
    
    private SampleSDWISManager getPreviousSDWISManager() {
        if (previousSDWISManager == null) {
            try {
                previousSDWISManager = (SampleSDWISManager)previousManager.getDomainManager();
            } catch (Exception e) {
                previousSDWISManager = SampleSDWISManager.getInstance();
            }
        }
        return previousSDWISManager;
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
}
