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
import org.openelis.gwt.widget.Button;
import org.openelis.gwt.widget.AutoComplete;
import org.openelis.gwt.widget.CheckBox;
import org.openelis.gwt.widget.HasExceptions;
import org.openelis.gwt.widget.Item;
import org.openelis.gwt.widget.ModalWindow;
import org.openelis.gwt.widget.QueryFieldUtil;
import org.openelis.gwt.widget.Window;
import org.openelis.gwt.widget.TextBox;
import org.openelis.gwt.widget.table.Row;
import org.openelis.gwt.widget.table.Table;
import org.openelis.manager.SampleEnvironmentalManager;
import org.openelis.manager.SampleManager;
import org.openelis.meta.SampleMeta;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;

public class EnvironmentalTab extends Screen {
    private TextBox                        location, description, collector, collectorPhone;
    private TextBox<Integer>               priority;
    private AutoComplete                   project, reportTo, billTo;
    private Button                         billToLookup, reportToLookup, projectLookup,
                                           locationLookup;
    private CheckBox                       isHazardous;

    private SampleLocationLookupScreen     locationScreen;
    private SampleOrganizationLookupScreen organizationScreen;
    private SampleProjectLookupScreen      projectScreen;

    private ScreenService                  orgService;
    private ScreenService                  projectService;

    private SampleManager                  manager;
    private SampleEnvironmentalManager     envManager;

    protected boolean                      loaded = false;

    public EnvironmentalTab(Window window) throws Exception {
        this(null, window);
    }
    
    public EnvironmentalTab(ScreenDefInt def, Window window) throws Exception {
        if (def == null)
            drawScreen((ScreenDefInt)GWT.create(EnvironmentalTabDef.class));
        else
            setDefinition(def);
        
        setWindow(window);

        orgService = new ScreenService("controller?service=org.openelis.modules.organization.server.OrganizationService");
        projectService = new ScreenService("controller?service=org.openelis.modules.project.server.ProjectService");

        initialize();
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
                isHazardous.setEnabled(EnumSet.of(State.ADD, State.UPDATE, State.QUERY)
                                          .contains(event.getState()));
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
                priority.setEnabled(EnumSet.of(State.ADD, State.UPDATE, State.QUERY)
                                       .contains(event.getState()));
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
                description.setEnabled(EnumSet.of(State.ADD, State.UPDATE, State.QUERY)
                                          .contains(event.getState()));
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
                collector.setEnabled(EnumSet.of(State.ADD, State.UPDATE, State.QUERY)
                                        .contains(event.getState()));
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
                collectorPhone.setEnabled(EnumSet.of(State.ADD, State.UPDATE, State.QUERY)
                                             .contains(event.getState()));
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
                location.setEnabled(EnumSet.of(State.ADD, State.UPDATE, State.QUERY)
                                       .contains(event.getState()));
                location.setQueryMode(event.getState() == State.QUERY);
            }
        });

        project = (AutoComplete)def.getWidget(SampleMeta.getProjectName());
        addScreenHandler(project, new ScreenEventHandler<Integer>() {
            public void onDataChange(DataChangeEvent event) {
                try {
                    SampleProjectViewDO projectDO = manager.getProjects()
                                                           .getFirstPermanentProject();

                    if (projectDO != null)
                        project.setValue(projectDO.getProjectId(), projectDO.getProjectName());
                    else
                        project.setValue(null, "");

                } catch (Exception e) {
                    com.google.gwt.user.client.Window.alert(e.getMessage());
                }
            }

            public void onValueChange(ValueChangeEvent<Integer> event) {
                Item<Integer> selectedRow = project.getSelectedItem();
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
                        project.setValue(projectDO.getProjectId(), projectDO.getProjectName());
                    else
                        project.setValue(null, "");

                } catch (Exception e) {
                    com.google.gwt.user.client.Window.alert(e.getMessage());
                }
            }

            public void onStateChange(StateChangeEvent<State> event) {
                project.setEnabled(EnumSet.of(State.ADD, State.UPDATE, State.QUERY)
                                      .contains(event.getState()));
                project.setQueryMode(event.getState() == State.QUERY);
            }
        });

        project.addGetMatchesHandler(new GetMatchesHandler() {
            public void onGetMatches(GetMatchesEvent event) {
                QueryFieldUtil parser;
                Item<Integer> row;
                ProjectDO data;
                ArrayList<ProjectDO> list;
                ArrayList<Item<Integer>> model;

                parser = new QueryFieldUtil();
                try {
                	parser.parse(event.getMatch());
                }catch(Exception e){
                	
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
                    project.showAutoMatches(model);
                } catch (Throwable e) {
                    e.printStackTrace();
                    com.google.gwt.user.client.Window.alert(e.getMessage());
                }
                window.clearStatus();
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

        locationLookup = (Button)def.getWidget("locButton");
        addScreenHandler(locationLookup, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                onLocationLookupClick();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                locationLookup.setEnabled(EnumSet.of(State.ADD, State.UPDATE, State.DISPLAY)
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
        Item<Integer> row;
        OrganizationDO data;
        ArrayList<OrganizationDO> list;
        ArrayList<Item<Integer>> model;

        parser = new QueryFieldUtil();
        try {
        	parser.parse(match);
        }catch(Exception e){
        	
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

            ModalWindow modal = new ModalWindow();
            modal.setName(consts.get("sampleLocation"));
            modal.setContent(locationScreen);

            SampleEnvironmentalDO envDO = ((SampleEnvironmentalManager)manager.getDomainManager()).getEnvironmental();

            locationScreen.setScreenState(state);
            locationScreen.setEnvDO(envDO);
        } catch (Exception e) {
            e.printStackTrace();
            com.google.gwt.user.client.Window.alert("error: " + e.getMessage());
            return;
        }
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
