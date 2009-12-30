package org.openelis.modules.sampleTracking.client;

import java.util.ArrayList;
import java.util.EnumSet;

import org.openelis.domain.OrganizationDO;
import org.openelis.domain.ProjectDO;
import org.openelis.domain.SampleEnvironmentalDO;
import org.openelis.domain.SampleOrganizationViewDO;
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
import org.openelis.gwt.widget.CheckBox;
import org.openelis.gwt.widget.QueryFieldUtil;
import org.openelis.gwt.widget.ScreenWindow;
import org.openelis.gwt.widget.TextBox;
import org.openelis.gwt.widget.table.TableDataRow;
import org.openelis.manager.SampleEnvironmentalManager;
import org.openelis.manager.SampleManager;
import org.openelis.meta.SampleMeta;
import org.openelis.modules.sample.client.SampleLocationLookupScreen;
import org.openelis.modules.sample.client.SampleOrganizationLookupScreen;
import org.openelis.modules.sample.client.SampleProjectLookupScreen;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.user.client.Window;

public class EnvironmentTab extends Screen {
	
    protected CheckBox                     isHazardous;
    protected TextBox<Integer>             priority;
    protected TextBox                      description,collector,collectorPhone,location;
    protected AppButton					   billToLookup, reportToLookup, projectLookup, locationLookup;
    protected AutoComplete<Integer>        project, reportTo, billTo;
    
    protected SampleManager     manager;
    
    private SampleLocationLookupScreen     locationScreen;
    private SampleOrganizationLookupScreen organizationScreen;
    private SampleProjectLookupScreen      projectScreen;

    protected ScreenService                orgService;
    protected ScreenService                projectService;
    
    protected boolean loaded = false;
	
	public EnvironmentTab(ScreenDefInt def, ScreenWindow window) {
        setDef(def);
        setWindow(window);
        
        initialize();
	}
	
	public void initialize() {
        isHazardous = (CheckBox)def.getWidget(SampleMeta.getEnvIsHazardous());
        addScreenHandler(isHazardous, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                isHazardous.setValue(getEnvManager().getEnvironmental().getIsHazardous());
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                getEnvManager().getEnvironmental().setIsHazardous(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                isHazardous.enable(EnumSet.of(State.ADD, State.UPDATE, State.QUERY)
                                   .contains(event.getState())); 
                isHazardous.setQueryMode(event.getState() == State.QUERY);
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
                priority.enable(EnumSet.of(State.ADD, State.UPDATE, State.QUERY)
                                   .contains(event.getState()));
                priority.setQueryMode(event.getState() == State.QUERY);
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
                description.enable(EnumSet.of(State.ADD, State.UPDATE, State.QUERY)
                                   .contains(event.getState()));
                description.setQueryMode(event.getState() == State.QUERY);
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
                collector.enable(EnumSet.of(State.ADD, State.UPDATE, State.QUERY)
                                   .contains(event.getState()));
                collector.setQueryMode(event.getState() == State.QUERY);
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
                collectorPhone.enable(EnumSet.of(State.ADD, State.UPDATE, State.QUERY)
                                   .contains(event.getState()));
                collectorPhone.setQueryMode(event.getState() == State.QUERY);
            }
        });
        
        location = (TextBox)def.getWidget(SampleMeta.getEnvSamplingLocation());
        addScreenHandler(location, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                location.setValue(getEnvManager().getEnvironmental().getSamplingLocation());
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                getEnvManager().getEnvironmental().setSamplingLocation(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                location.enable(EnumSet.of(State.ADD, State.UPDATE, State.QUERY)
                                   .contains(event.getState()));
                location.setQueryMode(event.getState() == State.QUERY);
            }
        });
        
        project = (AutoComplete<Integer>)def.getWidget(SampleMeta.getProjectName());
        addScreenHandler(project, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                try{
                    SampleProjectViewDO projectDO = manager.getProjects().getFirstPermanentProject();
                    
                    if(projectDO != null)
                        project.setSelection(projectDO.getId(), projectDO.getProjectName());
                    else
                        project.setSelection(null, "");
                    
                }catch(Exception e){
                    Window.alert(e.getMessage());
                }
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                TableDataRow selectedRow = project.getSelection();
                SampleProjectViewDO projectDO = null;
                try{
                    if(selectedRow.key != null){
                        projectDO = new SampleProjectViewDO();
                        projectDO.setIsPermanent("Y");
                        projectDO.setProjectId((Integer)selectedRow.key);
                        projectDO.setProjectName((String)selectedRow.cells.get(0).value);
                        projectDO.setProjectDescription((String)selectedRow.cells.get(1).value);
                    }
                    
                    manager.getProjects().addFirstPermanentProject(projectDO);
                    
                    projectDO = manager.getProjects().getFirstPermanentProject();
                    
                    if(projectDO != null)
                        project.setSelection(projectDO.getProjectId(), projectDO.getProjectName());
                    else
                        project.setSelection(null, "");
                    
                }catch(Exception e){
                    Window.alert(e.getMessage());
                }
            }

            public void onStateChange(StateChangeEvent<State> event) {
                project.enable(EnumSet.of(State.ADD, State.UPDATE, State.QUERY)
                                   .contains(event.getState()));
                project.setQueryMode(event.getState() == State.QUERY);
            }
        });
        
        project.addGetMatchesHandler(new GetMatchesHandler(){
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
                    list = projectService.callList("fetchActiveByName", parser.getParameter().get(0));
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
                try{
                    SampleOrganizationViewDO reportToOrg = manager.getOrganizations().getFirstReportTo();
                    
                    if(reportToOrg != null)
                        reportTo.setSelection(reportToOrg.getOrganizationId(), reportToOrg.getOrganizationName());
                    else
                        reportTo.setSelection(null, "");
                    
                }catch(Exception e){
                    Window.alert(e.getMessage());
                }
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                TableDataRow selectedRow = reportTo.getSelection();
                SampleOrganizationViewDO reportToOrg = null;
                try{
                    if(selectedRow.key != null){
                        reportToOrg = new SampleOrganizationViewDO();
                        reportToOrg.setOrganizationId((Integer)selectedRow.key);
                        reportToOrg.setOrganizationName((String)selectedRow.cells.get(0).value);
                        reportToOrg.setOrganizationCity((String)selectedRow.cells.get(2).value);
                        reportToOrg.setOrganizationState((String)selectedRow.cells.get(3).value);
                    }
                    
                    manager.getOrganizations().setReportTo(reportToOrg);
                    
                    reportToOrg = manager.getOrganizations().getFirstReportTo();
                    
                    if(reportToOrg != null)
                        reportTo.setSelection(reportToOrg.getOrganizationId(), reportToOrg.getOrganizationName());
                    else
                        reportTo.setSelection(null, "");
                    
                }catch(Exception e){
                    Window.alert(e.getMessage());
                }
            }

            public void onStateChange(StateChangeEvent<State> event) {
                reportTo.enable(EnumSet.of(State.ADD, State.UPDATE, State.QUERY)
                                   .contains(event.getState()));
                reportTo.setQueryMode(event.getState() == State.QUERY);
            }
        });
        
        reportTo.addGetMatchesHandler(new GetMatchesHandler(){
           public void onGetMatches(GetMatchesEvent event) {
               getOrganizationMatches(event.getMatch(), reportTo);
                
            } 
        });
        
        billTo = (AutoComplete<Integer>)def.getWidget(SampleMeta.getBillTo());
        addScreenHandler(billTo, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                try{
                    SampleOrganizationViewDO billToOrg = manager.getOrganizations().getFirstBillTo();
                    
                    if(billToOrg != null)
                        billTo.setSelection(billToOrg.getOrganizationId(), billToOrg.getOrganizationName());
                    else
                        billTo.setSelection(null, "");
                    
                }catch(Exception e){
                    Window.alert(e.getMessage());
                }
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                TableDataRow selectedRow = billTo.getSelection();
                SampleOrganizationViewDO billToOrg = null;
                try{
                    if(selectedRow.key != null){
                        billToOrg = new SampleOrganizationViewDO();
                        billToOrg.setOrganizationId((Integer)selectedRow.key);
                        billToOrg.setOrganizationName((String)selectedRow.cells.get(0).value);
                        billToOrg.setOrganizationCity((String)selectedRow.cells.get(2).value);
                        billToOrg.setOrganizationState((String)selectedRow.cells.get(3).value);
                    }
                    
                    manager.getOrganizations().setBillTo(billToOrg);
                    
                    billToOrg = manager.getOrganizations().getFirstBillTo();
                    
                    if(billToOrg != null)
                        billTo.setSelection(billToOrg.getOrganizationId(), billToOrg.getOrganizationName());
                    else
                        billTo.setSelection(null, "");
                    
                }catch(Exception e){
                    Window.alert(e.getMessage());
                }
            }

            public void onStateChange(StateChangeEvent<State> event) {
                billTo.enable( EnumSet.of(State.ADD, State.UPDATE)
                                   .contains(event.getState()));
                billTo.setQueryMode(event.getState() == State.QUERY);
            }
        });
        
        billTo.addGetMatchesHandler(new GetMatchesHandler(){
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
        
        locationLookup = (AppButton)def.getWidget("locButton");
        addScreenHandler(locationLookup, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                onLocationLookupClick();
            }
            
            public void onStateChange(StateChangeEvent<State> event) {
                locationLookup.enable(EnumSet.of(State.ADD, State.UPDATE, State.DISPLAY)
                                   .contains(event.getState()));
            }
        });        
	}
	
	public void setData(SampleManager manager) {
		this.manager = manager;
		loaded = false;
	}
	
	public void draw() {
        if(!loaded)
            DataChangeEvent.fire(this);
        
        loaded = true;
	}
	
    private void onProjectLookupClick(){
        try {
            if (projectScreen == null) {
                final EnvironmentTab env = this;
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
	
    private void onOrganizationLookupClick(){
        try{
            if(organizationScreen == null){
                final EnvironmentTab env = this;
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
            
        }catch(Exception e){
            e.printStackTrace();
            Window.alert(e.getMessage());
            return;
        }
    }
    
    private void onLocationLookupClick(){
        try {
            if (locationScreen == null) {
                    final EnvironmentTab env = this;
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
    
    private SampleEnvironmentalManager getEnvManager(){
        SampleEnvironmentalManager envManager;
        
        try{
            envManager = (SampleEnvironmentalManager)manager.getDomainManager();
        }catch(Exception e){
            e.printStackTrace();
            Window.alert(e.getMessage());
            envManager = SampleEnvironmentalManager.getInstance();
            manager.getSample().setDomain(SampleManager.ENVIRONMENTAL_DOMAIN_FLAG);
        }
        
        return envManager; 
    }

    private void getOrganizationMatches(String match, AutoComplete widget){
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
}
