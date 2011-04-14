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
import org.openelis.gwt.services.ScreenService;
import org.openelis.gwt.widget.AppButton;
import org.openelis.gwt.widget.AutoComplete;
import org.openelis.gwt.widget.CheckBox;
import org.openelis.gwt.widget.HasField;
import org.openelis.gwt.widget.QueryFieldUtil;
import org.openelis.gwt.widget.ScreenWindow;
import org.openelis.gwt.widget.TextBox;
import org.openelis.gwt.widget.table.TableDataRow;
import org.openelis.gwt.widget.table.TableWidget;
import org.openelis.manager.SampleEnvironmentalManager;
import org.openelis.manager.SampleManager;
import org.openelis.meta.SampleMeta;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.user.client.Window;

public class EnvironmentalTab extends Screen {
    private TextBox                        location, description, collector, collectorPhone;
    private TextBox<Integer>               priority;
    private AutoComplete<Integer>          project, reportTo, billTo;
    private AppButton                      billToLookup, reportToLookup, projectLookup,
                                           locationLookup;
    private CheckBox                       isHazardous;

    private SampleLocationLookupScreen     locationScreen;
    private SampleOrganizationLookupScreen organizationScreen;
    private SampleProjectLookupScreen      projectScreen;

    private ScreenService                  orgService;
    private ScreenService                  projectService;

    private SampleManager                  manager;
    private SampleEnvironmentalManager     envManager;

    private Integer                        sampleReleasedId;

    protected boolean                      loaded = false;

    public EnvironmentalTab(ScreenWindow window) throws Exception {
        this(null, window);
    }
    
    public EnvironmentalTab(ScreenDefInt def, ScreenWindow window) throws Exception {
        if (def == null)
            drawScreen((ScreenDefInt)GWT.create(EnvironmentalTabDef.class));
        else
            setDefinition(def);
        
        setWindow(window);

        orgService = new ScreenService("controller?service=org.openelis.modules.organization.server.OrganizationService");
        projectService = new ScreenService("controller?service=org.openelis.modules.project.server.ProjectService");

        initialize();
        initializeDropdowns();
    }

    public void initialize() {
        isHazardous = (CheckBox)def.getWidget(SampleMeta.getEnvIsHazardous());
        addScreenHandler(isHazardous, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                isHazardous.setValue(getManager().getEnvironmental().getIsHazardous());
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                getManager().getEnvironmental().setIsHazardous(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                isHazardous.enable(event.getState() == State.QUERY ||
                                   (canEdit() && EnumSet.of(State.ADD, State.UPDATE)
                                                        .contains(event.getState())));
                isHazardous.setQueryMode(event.getState() == State.QUERY);
            }
        });

        priority = (TextBox<Integer>)def.getWidget(SampleMeta.getEnvPriority());
        addScreenHandler(priority, new ScreenEventHandler<Integer>() {
            public void onDataChange(DataChangeEvent event) {
                priority.setValue(getManager().getEnvironmental().getPriority());
            }

            public void onValueChange(ValueChangeEvent<Integer> event) {
                getManager().getEnvironmental().setPriority(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                priority.enable(event.getState() == State.QUERY ||
                                (canEdit() && EnumSet.of(State.ADD, State.UPDATE)
                                                     .contains(event.getState())));
                priority.setQueryMode(event.getState() == State.QUERY);
            }
        });

        description = (TextBox)def.getWidget(SampleMeta.getEnvDescription());
        addScreenHandler(description, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                description.setValue(getManager().getEnvironmental().getDescription());
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                getManager().getEnvironmental().setDescription(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                description.enable(event.getState() == State.QUERY ||
                                   (canEdit() && EnumSet.of(State.ADD, State.UPDATE)
                                                        .contains(event.getState())));
                description.setQueryMode(event.getState() == State.QUERY);
            }
        });

        collector = (TextBox)def.getWidget(SampleMeta.getEnvCollector());
        addScreenHandler(collector, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                collector.setValue(getManager().getEnvironmental().getCollector());
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                getManager().getEnvironmental().setCollector(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                collector.enable(event.getState() == State.QUERY ||
                                 (canEdit() && EnumSet.of(State.ADD, State.UPDATE)
                                                      .contains(event.getState())));
                collector.setQueryMode(event.getState() == State.QUERY);
            }
        });

        collectorPhone = (TextBox)def.getWidget(SampleMeta.getEnvCollectorPhone());
        addScreenHandler(collectorPhone, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                collectorPhone.setValue(getManager().getEnvironmental().getCollectorPhone());
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                getManager().getEnvironmental().setCollectorPhone(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                collectorPhone.enable(event.getState() == State.QUERY ||
                                      (canEdit() && EnumSet.of(State.ADD, State.UPDATE)
                                                           .contains(event.getState())));
                collectorPhone.setQueryMode(event.getState() == State.QUERY);
            }
        });

        location = (TextBox)def.getWidget(SampleMeta.getEnvLocation());
        addScreenHandler(location, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                location.setValue(getManager().getEnvironmental().getLocation());
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                getManager().getEnvironmental().setLocation(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                location.enable(event.getState() == State.QUERY ||
                                (canEdit() && EnumSet.of(State.ADD, State.UPDATE)
                                                     .contains(event.getState())));
                location.setQueryMode(event.getState() == State.QUERY);
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
                SampleOrganizationViewDO billToOrg;

                selectedRow = billTo.getSelection();

                try {
                    if (selectedRow == null || selectedRow.key == null) {
                        manager.getOrganizations().removeBillTo();
                        billTo.setSelection(null, "");
                        return;
                    }

                    billToOrg = manager.getOrganizations().getBillTo();
                    if (billToOrg == null) {
                        billToOrg = new SampleOrganizationViewDO();
                        manager.getOrganizations().setBillTo(billToOrg);
                    }

                    billToOrg.setOrganizationId((Integer)selectedRow.key);
                    billToOrg.setOrganizationName((String)selectedRow.cells.get(0).value);
                    billToOrg.setOrganizationCity((String)selectedRow.cells.get(2).value);
                    billToOrg.setOrganizationState((String)selectedRow.cells.get(3).value);

                    billTo.setSelection(billToOrg.getOrganizationId(),
                                        billToOrg.getOrganizationName());

                } catch (Exception e) {
                    Window.alert(e.getMessage());
                }
            }

            public void onStateChange(StateChangeEvent<State> event) {
                billTo.enable(event.getState() == State.QUERY ||
                              (canEdit() && EnumSet.of(State.ADD, State.UPDATE)
                                                   .contains(event.getState())));
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

    public void draw() {
        if ( !loaded)
            DataChangeEvent.fire(this);

        loaded = true;
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

    private void onProjectLookupClick() {
        try {
            if (projectScreen == null) {
                final EnvironmentalTab env = this;
                projectScreen = new SampleProjectLookupScreen();
                projectScreen.addActionHandler(new ActionHandler<SampleProjectLookupScreen.Action>() {
                    public void onAction(ActionEvent<SampleProjectLookupScreen.Action> event) {
                        if (event.getAction() == SampleProjectLookupScreen.Action.OK) {
                            DataChangeEvent.fire(env, project);

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
                final EnvironmentalTab env = this;
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

    private void onLocationLookupClick() {
        try {
            if (locationScreen == null) {
                final EnvironmentalTab env = this;
                locationScreen = new SampleLocationLookupScreen();

                locationScreen.addActionHandler(new ActionHandler<SampleLocationLookupScreen.Action>() {
                    public void onAction(ActionEvent<SampleLocationLookupScreen.Action> event) {
                        if (event.getAction() == SampleLocationLookupScreen.Action.OK) {
                            DataChangeEvent.fire(env, location);
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
            sampleReleasedId = DictionaryCache.getIdFromSystemName("sample_released");
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

    private SampleEnvironmentalManager getManager() {
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
}
