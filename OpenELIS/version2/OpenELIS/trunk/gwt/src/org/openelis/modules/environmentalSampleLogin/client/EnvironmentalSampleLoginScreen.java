/** Exhibit A - UIRF Open-source Based Public Software License.
* 
* The contents of this file are subject to the UIRF Open-source Based
* Public Software License(the "License"); you may not use this file except
* in compliance with the License. You may obtain a copy of the License at
* openelis.uhl.uiowa.edu
* 
* Software distributed under the License is distributed on an "AS IS"
* basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
* License for the specific language governing rights and limitations
* under the License.
* 
* The Original Code is OpenELIS code.
* 
* The Initial Developer of the Original Code is The University of Iowa.
* Portions created by The University of Iowa are Copyright 2006-2008. All
* Rights Reserved.
* 
* Contributor(s): ______________________________________.
* 
* Alternatively, the contents of this file marked
* "Separately-Licensed" may be used under the terms of a UIRF Software
* license ("UIRF Software License"), in which case the provisions of a
* UIRF Software License are applicable instead of those above. 
*/
package org.openelis.modules.environmentalSampleLogin.client;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;

import org.openelis.cache.DictionaryCache;
import org.openelis.domain.AnalysisViewDO;
import org.openelis.domain.DictionaryDO;
import org.openelis.domain.IdNameVO;
import org.openelis.domain.OrganizationDO;
import org.openelis.domain.ProjectDO;
import org.openelis.domain.SampleEnvironmentalDO;
import org.openelis.domain.SampleItemViewDO;
import org.openelis.domain.SampleOrganizationViewDO;
import org.openelis.domain.SampleProjectViewDO;
import org.openelis.domain.TestPrepDO;
import org.openelis.domain.TestPrepViewDO;
import org.openelis.domain.TestSectionViewDO;
import org.openelis.domain.TestViewDO;
import org.openelis.gwt.common.Datetime;
import org.openelis.gwt.common.EntityLockedException;
import org.openelis.gwt.common.LastPageException;
import org.openelis.gwt.common.NotFoundException;
import org.openelis.gwt.common.RPC;
import org.openelis.gwt.common.SecurityException;
import org.openelis.gwt.common.SecurityModule;
import org.openelis.gwt.common.ValidationErrorsList;
import org.openelis.gwt.common.data.Query;
import org.openelis.gwt.event.ActionEvent;
import org.openelis.gwt.event.ActionHandler;
import org.openelis.gwt.event.DataChangeEvent;
import org.openelis.gwt.event.GetMatchesEvent;
import org.openelis.gwt.event.GetMatchesHandler;
import org.openelis.gwt.event.StateChangeEvent;
import org.openelis.gwt.screen.Screen;
import org.openelis.gwt.screen.ScreenDefInt;
import org.openelis.gwt.screen.ScreenEventHandler;
import org.openelis.gwt.screen.ScreenNavigator;
import org.openelis.gwt.services.ScreenService;
import org.openelis.gwt.widget.AppButton;
import org.openelis.gwt.widget.AutoComplete;
import org.openelis.gwt.widget.CalendarLookUp;
import org.openelis.gwt.widget.CheckBox;
import org.openelis.gwt.widget.Confirm;
import org.openelis.gwt.widget.Dropdown;
import org.openelis.gwt.widget.MenuItem;
import org.openelis.gwt.widget.QueryFieldUtil;
import org.openelis.gwt.widget.ScreenWindow;
import org.openelis.gwt.widget.TextBox;
import org.openelis.gwt.widget.AppButton.ButtonState;
import org.openelis.gwt.widget.table.TableDataRow;
import org.openelis.gwt.widget.table.event.BeforeCellEditedEvent;
import org.openelis.gwt.widget.table.event.BeforeCellEditedHandler;
import org.openelis.gwt.widget.table.event.RowAddedEvent;
import org.openelis.gwt.widget.table.event.RowAddedHandler;
import org.openelis.gwt.widget.tree.TreeDataItem;
import org.openelis.gwt.widget.tree.TreeWidget;
import org.openelis.manager.AnalysisManager;
import org.openelis.manager.SampleEnvironmentalManager;
import org.openelis.manager.SampleItemManager;
import org.openelis.manager.SampleManager;
import org.openelis.manager.TestManager;
import org.openelis.manager.TestPrepManager;
import org.openelis.meta.SampleMeta;
import org.openelis.modules.main.client.openelis.OpenELIS;
import org.openelis.modules.sample.client.AnalysisNotesTab;
import org.openelis.modules.sample.client.AnalysisTab;
import org.openelis.modules.sample.client.AuxDataTab;
import org.openelis.modules.sample.client.QAEventsTab;
import org.openelis.modules.sample.client.SampleDataBundle;
import org.openelis.modules.sample.client.SampleItemTab;
import org.openelis.modules.sample.client.SampleLocationLookupScreen;
import org.openelis.modules.sample.client.SampleNotesTab;
import org.openelis.modules.sample.client.SampleOrganizationLookupScreen;
import org.openelis.modules.sample.client.SampleProjectLookupScreen;
import org.openelis.modules.sample.client.StorageTab;
import org.openelis.modules.sample.client.TestResultsTab;
import org.openelis.modules.test.client.TestPrepLookupScreen;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.BeforeSelectionEvent;
import com.google.gwt.event.logical.shared.BeforeSelectionHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.TabPanel;

public class EnvironmentalSampleLoginScreen extends Screen {

    public enum Tabs {
        SAMPLE_ITEM, ANALYSIS, TEST_RESULT, ANALYSIS_NOTES, SAMPLE_NOTES, STORAGE, QA_EVENTS,
        AUX_DATA
    };

    protected Tabs                         tab;
    private Integer                        analysisLoggedInId, analysisCancelledId,
                    analysisReleasedId, analysisInPrep, sampleLoggedInId, sampleErrorStatusId,
                    sampleReleasedId, userId;

    private SampleItemTab                  sampleItemTab;
    private AnalysisTab                    analysisTab;
    private TestResultsTab                 testResultsTab;
    private AnalysisNotesTab               analysisNotesTab;
    private SampleNotesTab                 sampleNotesTab;
    private StorageTab                     storageTab;
    private QAEventsTab                    qaEventsTab;
    private AuxDataTab                     auxDataTab;

    protected TextBox                      location, clientReference, description, collector,
                    collectorPhone;
    protected TextBox<Integer>             accessionNumber, orderNumber, priority;
    protected TextBox<Datetime>            collectedTime;
    protected AutoComplete<Integer>        project, reportTo, billTo;
    protected Dropdown<Integer>            statusId;
    protected TreeWidget                   itemsTree;
    protected AppButton                    removeRow, billToLookup, reportToLookup, projectLookup,
                    locationLookup, addItem, addAnalysis, queryButton, addButton, updateButton,
                    nextButton, prevButton, commitButton, abortButton;
    protected CalendarLookUp               collectedDate, receivedDate;
    protected CheckBox                     isHazardous;
    protected MenuItem                     history;
    protected TabPanel                     tabs;

    private SampleLocationLookupScreen     locationScreen;
    private SampleOrganizationLookupScreen organizationScreen;
    private SampleProjectLookupScreen      projectScreen;
    private TestPrepLookupScreen           prepPickerScreen;
    private Confirm                        cancelAnalysisConfirm;

    protected ScreenService                orgService;
    protected ScreenService                projectService;

    ScreenNavigator                        nav;
    private SecurityModule                 security;
    private int                            tempId;

    private SampleManager                  manager;
    
    public EnvironmentalSampleLoginScreen() throws Exception {
        //Call base to get ScreenDef and draw screen
        super((ScreenDefInt)GWT.create(EnvironmentalSampleLoginDef.class));
        service = new ScreenService("controller?service=org.openelis.modules.environmentalSampleLogin.server.EnvironmentalSampleLoginService");
        orgService = new ScreenService("controller?service=org.openelis.modules.organization.server.OrganizationService");
        projectService = new ScreenService("controller?service=org.openelis.modules.project.server.ProjectService");
        
        security = OpenELIS.security.getModule("sampleenvironmental");
        if (security == null)
            throw new SecurityException("screenPermException", "Environmental Sample Login Screen");
        
        userId = OpenELIS.security.getSystemUserId();
        
        DeferredCommand.addCommand(new Command() {
            public void execute() {
                postConstructor();
            }
        });
    }

    /**
     * This method is called to set the initial state of widgets after the
     * screen is attached to the browser. It is usually called in deferred
     * command.
     */
    private void postConstructor() {
        tab = Tabs.SAMPLE_ITEM;
        manager = SampleManager.getInstance();
        manager.getSample().setDomain(SampleManager.ENVIRONMENTAL_DOMAIN_FLAG);
        
        try{
            DictionaryCache.preloadByCategorySystemNames("sample_status", "analysis_status", "type_of_sample", 
                                                         "source_of_sample", "sample_container", "unit_of_measure", 
                                                         "qaevent_type", "aux_field_value_type", "organization_type");
        }catch(Exception e){
            Window.alert(e.getMessage());
            window.close();
        }
        
        initialize();
        initializeDropdowns();
        setState(State.DEFAULT);
        DataChangeEvent.fire(this);
    }
    
    private void initialize() {
        accessionNumber = (TextBox<Integer>)def.getWidget(SampleMeta.getAccessionNumber());
        addScreenHandler(accessionNumber, new ScreenEventHandler<Integer>() {
            public void onDataChange(DataChangeEvent event) {
                accessionNumber.setValue(getString(manager.getSample().getAccessionNumber()));
            }

            public void onValueChange(final ValueChangeEvent<Integer> event) {
                try{
                    manager.getSample().setAccessionNumber(event.getValue());
                    manager.validateAccessionNumber(manager.getSample());
                    
                }catch(ValidationErrorsList e) {
                    showErrors(e);
                }catch(Exception e){
                    Window.alert(e.getMessage());
                }
            }

            public void onStateChange(StateChangeEvent<State> event) {
                accessionNumber.enable(EnumSet.of(State.ADD, State.UPDATE, State.QUERY)
                                   .contains(event.getState()));
                accessionNumber.setQueryMode(event.getState() == State.QUERY);

                if (EnumSet.of(State.ADD, State.UPDATE, State.QUERY)
                                .contains(event.getState()))
                    accessionNumber.setFocus(true);
            }
        });
        
        orderNumber = (TextBox<Integer>)def.getWidget("orderNumber");
        addScreenHandler(orderNumber, new ScreenEventHandler<Integer>() {
            public void onDataChange(DataChangeEvent event) {
                //orderNumber.setValue(getString(manager.getSample().getorgetAccessionNumber()));
            }

            public void onValueChange(ValueChangeEvent<Integer> event) {
                //manager.getSample().setAccessionNumber(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                orderNumber.enable(EnumSet.of(State.ADD, State.UPDATE)
                                   .contains(event.getState()));
            }
        });
        
        collectedDate = (CalendarLookUp)def.getWidget(SampleMeta.getCollectionDate());
        addScreenHandler(collectedDate, new ScreenEventHandler<Datetime>() {
            public void onDataChange(DataChangeEvent event) {
                collectedDate.setValue(manager.getSample().getCollectionDate());
            }

            public void onValueChange(ValueChangeEvent<Datetime> event) {
                manager.getSample().setCollectionDate(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                collectedDate.enable(EnumSet.of(State.ADD, State.UPDATE, State.QUERY)
                                   .contains(event.getState()));
                collectedDate.setQueryMode(event.getState() == State.QUERY);
            }
        });
        
        collectedTime = (TextBox<Datetime>)def.getWidget(SampleMeta.getCollectionTime());
        addScreenHandler(collectedTime, new ScreenEventHandler<Datetime>() {
            public void onDataChange(DataChangeEvent event) {
                collectedTime.setValue(manager.getSample().getCollectionTime());
            }

            public void onValueChange(ValueChangeEvent<Datetime> event) {
                manager.getSample().setCollectionTime(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                collectedTime.enable(EnumSet.of(State.ADD, State.UPDATE, State.QUERY)
                                   .contains(event.getState()));
                collectedTime.setQueryMode(event.getState() == State.QUERY);
            }
        });
        
        receivedDate = (CalendarLookUp)def.getWidget(SampleMeta.getReceivedDate());
        addScreenHandler(receivedDate, new ScreenEventHandler<Datetime>() {
            public void onDataChange(DataChangeEvent event) {
                receivedDate.setValue(manager.getSample().getReceivedDate());
            }

            public void onValueChange(ValueChangeEvent<Datetime> event) {
                manager.getSample().setReceivedDate(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                receivedDate.enable(EnumSet.of(State.ADD, State.UPDATE, State.QUERY)
                                   .contains(event.getState()));
                receivedDate.setQueryMode(event.getState() == State.QUERY);
            }
        });
        
        statusId = (Dropdown<Integer>)def.getWidget(SampleMeta.getStatusId());
        addScreenHandler(statusId, new ScreenEventHandler<Integer>() {
            public void onDataChange(DataChangeEvent event) {
                statusId.setSelection(manager.getSample().getStatusId());
            }

            public void onValueChange(ValueChangeEvent<Integer> event) {
                manager.getSample().setStatusId(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                statusId.enable(EnumSet.of(State.QUERY)
                                   .contains(event.getState()));
                statusId.setQueryMode(event.getState() == State.QUERY);
            }
        });
        
        clientReference = (TextBox)def.getWidget(SampleMeta.getClientReference());
        addScreenHandler(clientReference, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                clientReference.setValue(manager.getSample().getClientReference());
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                manager.getSample().setClientReference(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                clientReference.enable(EnumSet.of(State.ADD, State.UPDATE, State.QUERY)
                                   .contains(event.getState()));
                clientReference.setQueryMode(event.getState() == State.QUERY);
            }
        });
        
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
        
        itemsTree = (TreeWidget)def.getWidget("itemsTestsTree");
        addScreenHandler(itemsTree, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                itemsTree.load(getTreeModel());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                itemsTree.enable(EnumSet.of(State.ADD, State.UPDATE, State.QUERY)
                                   .contains(event.getState()));
                itemsTree.setQueryMode(event.getState() == State.QUERY);
            }
        });
               
        itemsTree.addBeforeSelectionHandler(new BeforeSelectionHandler<TreeDataItem>(){
           public void onBeforeSelection(BeforeSelectionEvent<TreeDataItem> event) {
               //do nothing
                
            } 
        });
        
        itemsTree.addSelectionHandler(new SelectionHandler<TreeDataItem>(){
           public void onSelection(SelectionEvent<TreeDataItem> event) {
               SampleDataBundle data;
               boolean enable;
               TreeDataItem selection = itemsTree.getSelection();
               
               if(selection != null)
                   data = (SampleDataBundle)selection.data;
               else
                   data = new SampleDataBundle();
               
               enable = false;
               if(state == State.ADD || state == State.UPDATE){
                   if("sampleItem".equals(selection.leafType)){
                       if(selection.hasChildren())
                           enable = false;
                       else
                           enable = true;
                   }else{
                       if(data.analysisTestDO != null && 
                                       (analysisCancelledId.equals(data.analysisTestDO.getStatusId()) || analysisReleasedId.equals(data.analysisTestDO.getStatusId())))
                           enable = false;
                       else
                           enable = true;
               }
               }

               removeRow.enable(enable);
               
               sampleItemTab.setData(data);
               analysisTab.setData(data);
               testResultsTab.setData(data);
               analysisNotesTab.setData(data);
               storageTab.setData(data);
               qaEventsTab.setData(data);
               
               drawTabs();
           }
        });
        
        itemsTree.addBeforeCellEditedHandler(new BeforeCellEditedHandler(){
           public void onBeforeCellEdited(BeforeCellEditedEvent event) {
               //never allowed to edit tree..use tabs
               event.cancel();
            } 
        });
        
        itemsTree.addRowAddedHandler(new RowAddedHandler(){
           public void onRowAdded(RowAddedEvent event) {
               try{
                   TreeDataItem addedRow = (TreeDataItem)event.getRow();
                   
                   if("sampleItem".equals(addedRow.leafType)){
                       manager.getSampleItems().addSampleItem(((SampleDataBundle)addedRow.data).sampleItemDO);
                   }else if("analysis".equals(addedRow.leafType)){
                       SampleDataBundle data = (SampleDataBundle)addedRow.data;
                       int sampleItemIndex = manager.getSampleItems().getIndex(data.sampleItemDO);
                       
                       manager.getSampleItems().getAnalysisAt(sampleItemIndex).addAnalysis(data.analysisTestDO);
                   }

               }catch(Exception e){
                   Window.alert(e.getMessage());
               }
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
        
        addItem = (AppButton)def.getWidget("addItemButton");
        addScreenHandler(addItem, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                onAddItemButtonClick();
            }
            
            public void onStateChange(StateChangeEvent<State> event) {
                addItem.enable(EnumSet.of(State.ADD, State.UPDATE)
                                   .contains(event.getState()));
            }
        });
        
        addAnalysis = (AppButton)def.getWidget("addAnalysisButton");
        addScreenHandler(addAnalysis, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                onAddAnalysisButtonClick();
            }
            
            public void onStateChange(StateChangeEvent<State> event) {
                addAnalysis.enable(EnumSet.of(State.ADD, State.UPDATE)
                                   .contains(event.getState()));
            }
        });
        
        removeRow = (AppButton)def.getWidget("removeRowButton");
        addScreenHandler(removeRow, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                onRemoveRowButtonClick();
            }
            
            public void onStateChange(StateChangeEvent<State> event) {
                removeRow.enable(false);
            }
        });
        
        history = (MenuItem)def.getWidget("history");
        addScreenHandler(history, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {               
                Window.alert("clicked history");
                //history();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                history.enable(EnumSet.of(State.DISPLAY, State.UPDATE).contains(event.getState()));
            }
        }); 
        
        nav = new ScreenNavigator(def) {
            public void executeQuery(final Query query) {
                window.setBusy(consts.get("querying"));

                service.callList("query", query, new AsyncCallback<ArrayList<IdNameVO>>() {
                    public void onSuccess(ArrayList<IdNameVO> result) {
                        setQueryResult(result);
                    }

                    public void onFailure(Throwable error) {
                        setQueryResult(null);
                        if (error instanceof NotFoundException) {
                            window.setDone(consts.get("noRecordsFound"));
                            setState(State.DEFAULT);
                        } else if (error instanceof LastPageException) {
                            window.setError("No more records in this direction");
                        } else {
                            Window.alert("Error: envsample call query failed; " + error.getMessage());
                            window.setError(consts.get("queryFailed"));
                        }
                    }
                });
            }

            public boolean fetch(RPC entry) {
                return fetchById((entry==null)?null:((IdNameVO)entry).getId());
            }

            public ArrayList<TableDataRow> getModel() {
                ArrayList<IdNameVO> result;
                ArrayList<TableDataRow> model;

                result = nav.getQueryResult();
                model = new ArrayList<TableDataRow>();
                if (result != null) {
                    for (IdNameVO entry : result)
                        model.add(new TableDataRow(entry.getId(), entry.getName()));
                }
                return model;
            }
        };
        
        // Set up tabs to recieve State Change events from the main Screen.
        sampleItemTab = new SampleItemTab(def, window);
        addScreenHandler(sampleItemTab, new ScreenEventHandler<Object>() {
            public void onDataChange(DataChangeEvent event) {
                sampleItemTab.setData(new SampleDataBundle());

                if (tab == Tabs.SAMPLE_ITEM)
                    drawTabs();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                sampleItemTab.setState(event.getState());
            }
        });
        
        analysisTab = new AnalysisTab(def, window);
        addScreenHandler(analysisTab, new ScreenEventHandler<Object>() {
            public void onDataChange(DataChangeEvent event) {
                analysisTab.setData(new SampleDataBundle());

                    if (tab == Tabs.ANALYSIS)
                        drawTabs();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                analysisTab.setState(event.getState());
            }
        });
        
        testResultsTab = new TestResultsTab(def, window);
        addScreenHandler(testResultsTab, new ScreenEventHandler<Object>() {
            public void onDataChange(DataChangeEvent event) {
                    testResultsTab.setData(new SampleDataBundle());

                    if (tab == Tabs.TEST_RESULT)
                        drawTabs();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                testResultsTab.setState(event.getState());
            }
        });
        
        analysisNotesTab = new AnalysisNotesTab(def, window, "anExNotesPanel", "anExNoteButton", "anIntNotesPanel", "anIntNoteButton");
        addScreenHandler(analysisNotesTab, new ScreenEventHandler<Object>() {
            public void onDataChange(DataChangeEvent event) {
                analysisNotesTab.setData(new SampleDataBundle());

                if (tab == Tabs.ANALYSIS_NOTES)
                    drawTabs();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                analysisNotesTab.setState(event.getState());
            }
        });
                
        sampleNotesTab = new SampleNotesTab(def, window, "sampleExtNotesPanel", "sampleExtNoteButton", "sampleIntNotesPanel", "sampleIntNoteButton");
        addScreenHandler(sampleNotesTab, new ScreenEventHandler<Object>() {
            public void onDataChange(DataChangeEvent event) {
                sampleNotesTab.setManager(manager);

                    if (tab == Tabs.SAMPLE_NOTES)
                        drawTabs();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                sampleNotesTab.setState(event.getState());
            }
        });
        
        storageTab = new StorageTab(def, window);
        addScreenHandler(storageTab, new ScreenEventHandler<Object>() {
            public void onDataChange(DataChangeEvent event) {
                    storageTab.setData(new SampleDataBundle());

                    if (tab == Tabs.STORAGE)
                        drawTabs();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                storageTab.setState(event.getState());
            }
        });

        qaEventsTab = new QAEventsTab(def, window);
        addScreenHandler(qaEventsTab, new ScreenEventHandler<Object>() {
            public void onDataChange(DataChangeEvent event) {
                    qaEventsTab.setData(new SampleDataBundle());
                    qaEventsTab.setManager(manager);

                    if (tab == Tabs.QA_EVENTS)
                        drawTabs();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                qaEventsTab.setState(event.getState());
            }
        });
        
        auxDataTab = new AuxDataTab(def, window);
        addScreenHandler(auxDataTab, new ScreenEventHandler<Object>() {
            public void onDataChange(DataChangeEvent event) {
                    auxDataTab.setManager(manager);

                    if (tab == Tabs.AUX_DATA)
                        drawTabs();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                auxDataTab.setState(event.getState());
            }
        });
        
        sampleItemTab.addActionHandler(new ActionHandler<SampleItemTab.Action>(){
            public void onAction(ActionEvent<SampleItemTab.Action> event) {
                if(state != State.QUERY && event.getAction() == SampleItemTab.Action.CHANGED){
                    TreeDataItem selected = itemsTree.getSelection();
                    
                    //make sure it is a sample item row
                    if("analysis".equals(selected.leafType))
                        selected = selected.parent;
                    
                    SampleDataBundle data = (SampleDataBundle)selected.data;
                    SampleItemViewDO itemDO = data.sampleItemDO;
                    
                    selected.cells.get(0).value = itemDO.getItemSequence()+" - "+formatTreeString(itemDO.getContainer());
                    selected.cells.get(1).value = formatTreeString(itemDO.getTypeOfSample());
                    
                    itemsTree.refresh(true);
                }
            }
        });
        
        analysisTab.addActionHandler(new ActionHandler<AnalysisTab.Action>(){
            public void onAction(ActionEvent<AnalysisTab.Action> event) {
                if(state != State.QUERY && event.getAction() == AnalysisTab.Action.CHANGED){
                    updateTreeAndCheckPrepTests((ArrayList<SampleDataBundle>)event.getData());

                }
            }
        });
        
        queryButton = (AppButton)def.getWidget("query");
        addScreenHandler(queryButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                query();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                if(EnumSet.of(State.DEFAULT, State.DISPLAY).contains(event.getState()) && 
                                security.hasSelectPermission())
                    queryButton.enable(true);
                else if (event.getState() == State.QUERY)
                    queryButton.setState(ButtonState.LOCK_PRESSED);
                else
                    queryButton.enable(false);
            }
        });

        addButton = (AppButton)def.getWidget("add");
        addScreenHandler(addButton, new ScreenEventHandler<Object>() {
            public void onDataChange(DataChangeEvent event) {
                if (EnumSet.of(State.DEFAULT, State.DISPLAY).contains(state) && 
                                security.hasAddPermission())
                    addButton.enable(canEdit());
            }
            
            public void onClick(ClickEvent event) {
                add();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                if (EnumSet.of(State.DEFAULT, State.DISPLAY).contains(event.getState()) && 
                                security.hasAddPermission())
                    addButton.enable(true);
                else if (EnumSet.of(State.ADD).contains(event.getState()))
                    addButton.setState(ButtonState.LOCK_PRESSED);
                else
                    addButton.enable(false);
            }
        });

        updateButton = (AppButton)def.getWidget("update");
        addScreenHandler(updateButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                update();
            }
            
            public void onDataChange(DataChangeEvent event) {
                if (EnumSet.of(State.DISPLAY).contains(state) && 
                                security.hasUpdatePermission())
                    updateButton.enable(canEdit());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                if (EnumSet.of(State.DISPLAY).contains(event.getState()) && 
                                security.hasUpdatePermission())
                    updateButton.enable(true);
                else if (EnumSet.of(State.UPDATE).contains(event.getState()))
                    updateButton.setState(ButtonState.LOCK_PRESSED);
                else
                    updateButton.enable(false);

            }
        });
        
        nextButton = (AppButton)def.getWidget("next");
        addScreenHandler(nextButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                next();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                nextButton.enable(EnumSet.of(State.DISPLAY).contains(event.getState()));
            }
        });

        prevButton = (AppButton)def.getWidget("previous");
        addScreenHandler(prevButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                previous();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                prevButton.enable(EnumSet.of(State.DISPLAY).contains(event.getState()));
            }
        });

        commitButton = (AppButton)def.getWidget("commit");
        addScreenHandler(commitButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                commit();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                commitButton.enable(EnumSet.of(State.QUERY, State.ADD, State.UPDATE).contains(event.getState()));
            }
        });

        abortButton = (AppButton)def.getWidget("abort");
        addScreenHandler(abortButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                abort();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                abortButton.enable(EnumSet.of(State.QUERY, State.ADD, State.UPDATE).contains(event.getState()));
            }
        });

        // Get TabPanel and set Tab Selection Handlers
        tabs = (TabPanel)def.getWidget("sampleItemTabPanel");
        tabs.addBeforeSelectionHandler(new BeforeSelectionHandler<Integer>() {
            public void onBeforeSelection(BeforeSelectionEvent<Integer> event) {
                int i;

                // tab screen order should be the same as enum or this will
                // not work
                i = event.getItem().intValue();
                tab = Tabs.values()[i];

                window.setBusy("loadingMessage");
                drawTabs();
                window.clearStatus();
            }
        });
    }

    protected void query() {
        manager = SampleManager.getInstance();
        manager.getSample().setDomain(SampleManager.ENVIRONMENTAL_DOMAIN_FLAG);
        
        setState(Screen.State.QUERY);
        DataChangeEvent.fire(this);
        
        //we need to make sure the tabs are cleared
        sampleItemTab.draw();
        analysisTab.draw();
        testResultsTab.draw();
        analysisNotesTab.draw();
        sampleNotesTab.draw();
        storageTab.draw();
        qaEventsTab.draw();
        auxDataTab.draw();
        
        window.setDone(consts.get("enterFieldsToQuery"));
    }

    protected void previous() {
        nav.previous();
    }

    protected void next() {
        nav.next();
    }
    
    public void add() {
        manager = SampleManager.getInstance();
        manager.getSample().setDomain(SampleManager.ENVIRONMENTAL_DOMAIN_FLAG);
        
        //default the form
        try{
            manager.getSample().setRevision(0);
            manager.getSample().setStatusId(sampleLoggedInId);
            manager.getSample().setEnteredDate(Datetime.getInstance(Datetime.YEAR, Datetime.MINUTE));
            manager.getSample().setReceivedDate(Datetime.getInstance(Datetime.YEAR, Datetime.MINUTE));
            manager.getSample().setReceivedById(userId);
            manager.getSample().setNextItemSequence(0);
            ((SampleEnvironmentalManager)manager.getDomainManager()).getEnvironmental().setIsHazardous("N");
            
        }catch(Exception e){
            Window.alert(e.getMessage());
            return;
        }
        
        setState(Screen.State.ADD);
        DataChangeEvent.fire(this);
        window.setDone(consts.get("enterInformationPressCommit"));
    }
    
    protected void update() {
        window.setBusy(consts.get("lockForUpdate"));

        try {
            manager = manager.fetchForUpdate();

            setState(State.UPDATE);
            DataChangeEvent.fire(this);
            window.clearStatus();
            
        } catch (EntityLockedException e) {
            window.clearStatus();
            Window.alert(e.getMessage());
        } catch (Exception e) {
            Window.alert(e.getMessage());
        }
    }

    protected void commit() {
        clearErrors();
        if ( !validate()){
            window.setError(consts.get("correctErrors"));
            return;
        }

        if (state == State.QUERY) {
            Query query;

            query = new Query();
            query.setFields(getQueryFields());
            nav.setQuery(query);
        } else if (state == State.ADD) {
            window.setBusy(consts.get("adding"));
            try {
                manager.validate();
                manager.getSample().setStatusId(sampleLoggedInId);
                manager = manager.add();

                setState(Screen.State.DISPLAY);
                DataChangeEvent.fire(this);
                window.clearStatus();
            } catch (ValidationErrorsList e) {
                showErrors(e);
                
                if(!e.hasErrors() && e.hasWarnings())
                    showWarningsDialog(e);
            } catch (Exception e) {
                Window.alert("commitAdd(): " + e.getMessage());
                window.clearStatus();
            }
        } else if (state == State.UPDATE) {
            window.setBusy(consts.get("updating"));
            try {
                manager.validate();
                manager.getSample().setStatusId(sampleLoggedInId);
                manager = manager.update();

                setState(Screen.State.DISPLAY);
                DataChangeEvent.fire(this);
                window.clearStatus();
            } catch (ValidationErrorsList e) {
                showErrors(e);
                
                if(!e.hasErrors() && e.hasWarnings())
                    showWarningsDialog(e);
            } catch (Exception e) {
                Window.alert("commitUpdate(): " + e.getMessage());
            }
        }
    }
    
    protected void commitWithWarnings() {
        clearErrors();
        manager.getSample().setStatusId(sampleErrorStatusId);
        
        if (state == State.ADD) {
            window.setBusy(consts.get("adding"));
            try {
                manager = manager.add();

                setState(Screen.State.DISPLAY);
                DataChangeEvent.fire(this);
                window.clearStatus();
            } catch (ValidationErrorsList e) {
                showErrors(e);
            } catch (Exception e) {
                Window.alert("commitAdd(): " + e.getMessage());
                window.clearStatus();
            }
        } else if (state == State.UPDATE) {
            window.setBusy(consts.get("updating"));
            try {
                manager = manager.update();

                setState(Screen.State.DISPLAY);
                DataChangeEvent.fire(this);
                window.clearStatus();
            } catch (ValidationErrorsList e) {
                showErrors(e);
            } catch (Exception e) {
                Window.alert("commitUpdate(): " + e.getMessage());
            }
        }
    }
    
    public void abort() {
        clearErrors();
        window.setBusy(consts.get("cancelChanges"));
        
        if (state == State.QUERY) {
            manager = SampleManager.getInstance();
            manager.getSample().setDomain(SampleManager.ENVIRONMENTAL_DOMAIN_FLAG);
            setState(State.DEFAULT);
            DataChangeEvent.fire(this);
            window.setDone(consts.get("queryAborted"));
            
        } else if (state == State.ADD) {
            manager = SampleManager.getInstance();
            manager.getSample().setDomain(SampleManager.ENVIRONMENTAL_DOMAIN_FLAG);
            setState(State.DEFAULT);
            DataChangeEvent.fire(this);
            window.setDone(consts.get("addAborted"));
            
        }else if (state == State.UPDATE) {
            
            try {
                manager = manager.abort();

                setState(State.DISPLAY);
                DataChangeEvent.fire(this);
                window.clearStatus();
            
            } catch (Exception e) {
                Window.alert(e.getMessage());
                window.clearStatus();
            }

        }else{
            window.clearStatus();
        }
    }
    
    protected boolean fetchById(Integer id) {
        if (id == null) {
            manager = SampleManager.getInstance();
            manager.getSample().setDomain(SampleManager.ENVIRONMENTAL_DOMAIN_FLAG);
            
            setState(State.DEFAULT);
        }else{
            window.setBusy(consts.get("fetching"));
            
            try {
                manager = SampleManager.findByIdWithItemsAnalyses(id);
            
            } catch (Exception e) {
                e.printStackTrace();
                setState(State.DEFAULT);
                Window.alert(consts.get("fetchFailed") + e.getMessage());
                window.clearStatus();
                return false;
            }
            setState(Screen.State.DISPLAY);
        }
        DataChangeEvent.fire(this);
        window.clearStatus();
        
        return true;
    }
       
    private void onProjectLookupClick(){
        try {
            if (projectScreen == null) {
                final EnvironmentalSampleLoginScreen env = this;
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
                final EnvironmentalSampleLoginScreen env = this;
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
                    final EnvironmentalSampleLoginScreen env = this;
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
    
    public void onAddItemButtonClick() {
        int nextItemSequence = manager.getSample().getNextItemSequence();

        TreeDataItem newRow = itemsTree.createTreeItem("sampleItem");
        newRow.toggle();
        newRow.cells.get(0).value = nextItemSequence + " - <>";
        newRow.cells.get(1).value = "<>";
        
        SampleItemViewDO siDO = new SampleItemViewDO();
        siDO.setItemSequence(nextItemSequence);
        
        try{
            
            SampleDataBundle data = new SampleDataBundle(manager.getSample(), manager.getSampleItems(),siDO);
            newRow.data = data;
        }catch(Exception e){
            Window.alert(e.getMessage());
            return;
        }
        
        manager.getSample().setNextItemSequence(nextItemSequence+1);
        
        itemsTree.addRow(newRow);
        itemsTree.select(newRow);
    }
    
    public void onAddAnalysisButtonClick() {
        TreeDataItem selectedRow;
        int selectedIndex;
        TreeDataItem newRow;
        
        newRow = itemsTree.createTreeItem("analysis");
        newRow.cells.get(0).value = "<> : <>";
        newRow.cells.get(1).value = analysisLoggedInId;
        
        selectedIndex = itemsTree.getSelectedRow();
        if(selectedIndex == -1)
            onAddItemButtonClick();
        
        selectedIndex = itemsTree.getSelectedRow();
        selectedRow = itemsTree.getRow(selectedIndex);
        
        if(!"sampleItem".equals(selectedRow.leafType))
            selectedRow = selectedRow.parent;
        
        SampleDataBundle sampleItemData = (SampleDataBundle)selectedRow.data;
        SampleItemViewDO itemDO = sampleItemData.sampleItemDO;
        int sampleItemIndex = sampleItemData.sampleItemManager.getIndex(itemDO);
        
        AnalysisViewDO aDO = new AnalysisViewDO();
        aDO.setId(getNextTempId());
        aDO.setStatusId(analysisLoggedInId);
        aDO.setRevision(0);
        
        try{
            SampleDataBundle data = new SampleDataBundle(manager.getSample(), sampleItemData.sampleItemManager, itemDO,
                                                         sampleItemData.sampleItemManager.getAnalysisAt(sampleItemIndex),aDO);
            newRow.data = data;
            itemsTree.addChildItem(selectedRow, newRow);
            itemsTree.select(newRow);
        }catch(Exception e){
            Window.alert(e.getMessage());
            return;
        }
        
        if (!selectedRow.open)
            selectedRow.toggle();
    }
    
    public void onRemoveRowButtonClick() {
        TreeDataItem selectedTreeRow = itemsTree.getSelection();
        
        if("analysis".equals(selectedTreeRow.leafType) && selectedTreeRow.key != null){
            if(cancelAnalysisConfirm == null){
                cancelAnalysisConfirm = new Confirm(Confirm.Type.QUESTION, consts.get("cancelAnalysisMessage"), "No", "Yes");
                cancelAnalysisConfirm.addSelectionHandler(new SelectionHandler<Integer>(){
                    public void onSelection(SelectionEvent<Integer> event) {
                        switch(event.getSelectedItem().intValue()) {
                            case 1 : 
                                cancelAnalysisRow(itemsTree.getSelectedRow());
                                break;
                        }
                    }
                });
            }
            
            cancelAnalysisConfirm.show();

        }else{
            itemsTree.deleteRow(selectedTreeRow);
        }
    }
    
    private void initializeDropdowns(){
        ArrayList<TableDataRow> model;

        //preload dictionary models and single entries, close the window if an error is found 
        try{
            analysisLoggedInId = DictionaryCache.getIdFromSystemName("analysis_logged_in");
            sampleLoggedInId = DictionaryCache.getIdFromSystemName("sample_logged_in");
            sampleErrorStatusId = DictionaryCache.getIdFromSystemName("sample_error");
            analysisCancelledId = DictionaryCache.getIdFromSystemName("analysis_cancelled");
            analysisReleasedId = DictionaryCache.getIdFromSystemName("analysis_released");
            analysisInPrep = DictionaryCache.getIdFromSystemName("analysis_inprep");
            sampleReleasedId = DictionaryCache.getIdFromSystemName("sample_released");
        
        }catch(Exception e){
            Window.alert(e.getMessage());
            window.close();
        }
        
        //sample status dropdown
        model = new ArrayList<TableDataRow>();
        model.add(new TableDataRow(null, ""));
        for (DictionaryDO d : DictionaryCache.getListByCategorySystemName("sample_status"))
            model.add(new TableDataRow(d.getId(), d.getEntry()));

        ((Dropdown<Integer>)def.getWidget(SampleMeta.getStatusId())).setModel(model);

        //analysis status dropdown
        model = new ArrayList<TableDataRow>();
        model.add(new TableDataRow(null, ""));
        for (DictionaryDO d : DictionaryCache.getListByCategorySystemName("analysis_status"))
            model.add(new TableDataRow(d.getId(), d.getEntry()));

        ((Dropdown<Integer>)itemsTree.getColumns().get("analysis").get(1).colWidget).setModel(model);
    }
    
    private void drawTabs() {
        switch (tab) {
            case SAMPLE_ITEM:
                sampleItemTab.draw();
                break;
            case ANALYSIS:
                analysisTab.draw();
                break;
            case TEST_RESULT:
                testResultsTab.draw();
                break;
            case ANALYSIS_NOTES:
                analysisNotesTab.draw();
                break;
            case SAMPLE_NOTES:
                sampleNotesTab.draw();
                break;
            case STORAGE:
                storageTab.draw();
                break;
            case QA_EVENTS:
                qaEventsTab.draw();
                break;
            case AUX_DATA:
                auxDataTab.draw();
                break;
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
    
    
    private ArrayList<TreeDataItem> getTreeModel() {
        int i, j;
        AnalysisManager am;
        SampleItemViewDO itemDO;
        TreeDataItem tmp;
        TreeDataItem treeModelItem, row;
        ArrayList<TreeDataItem> model = new ArrayList<TreeDataItem>();
        
        try{  
            HashMap<Integer, TreeDataItem> keyTable = new HashMap<Integer, TreeDataItem>();
            
            if(manager == null)
                return model;
            
            for(i=0; i<manager.getSampleItems().count(); i++){
                SampleItemManager sim = manager.getSampleItems();
                itemDO = sim.getSampleItemAt(i);
                
                row = new TreeDataItem(2);
                row.leafType = "sampleItem";
                row.toggle();
                row.key = itemDO.getId();
                //container
                row.cells.get(0).value = itemDO.getItemSequence()+" - "+formatTreeString(itemDO.getContainer());
                //source,type
                row.cells.get(1).value = itemDO.getTypeOfSample();
                
                SampleDataBundle data = new SampleDataBundle(manager.getSample(), manager.getSampleItems(), itemDO);
                row.data = data;
                
                tmp = keyTable.get(itemDO.getId());
                if(tmp != null){
                    tmp.addItem(row);
                }else{
                    keyTable.put(itemDO.getId(), row);
                    model.add(row);
                }
                
                am = manager.getSampleItems().getAnalysisAt(i);
                for(j=0; j<am.count(); j++){
                    AnalysisViewDO aDO = (AnalysisViewDO)am.getAnalysisAt(j);
                    
                    treeModelItem = new TreeDataItem(2);
                    treeModelItem.leafType = "analysis";
                    
                    treeModelItem.key = aDO.getId();
                    treeModelItem.cells.get(0).value = formatTreeString(aDO.getTestName()) + " : " + formatTreeString(aDO.getMethodName());
                    treeModelItem.cells.get(1).value = aDO.getStatusId();
                    
                    SampleDataBundle aData = new SampleDataBundle(manager.getSample(), sim, itemDO, am, aDO);
                    treeModelItem.data = aData;
                    
                    row.addItem(treeModelItem);
                }
            }
        } catch (Exception e) {
    
            e.printStackTrace();
            return null;
        }       
        
        return model;
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
    
    private String formatTreeString(String val){
        if(val == null || "".equals(val))
            return "<>";
        
        return val;
    }
    
    private void drawTestPrepScreen(TestPrepManager manager){
        if (prepPickerScreen == null) {
            try {
                prepPickerScreen = new TestPrepLookupScreen();
                prepPickerScreen.addActionHandler(new ActionHandler<TestPrepLookupScreen.Action>() {
                    public void onAction(ActionEvent<TestPrepLookupScreen.Action> event) {
                        if (event.getAction() == TestPrepLookupScreen.Action.SELECTED_PREP_ROW) {
                            
                            TableDataRow selectedRow = (TableDataRow)event.getData();
                            Integer testId = (Integer)selectedRow.key;
                            selectedPrepTest(testId);
                        }
                    }
                });

            } catch (Exception e) {
                e.printStackTrace();
                Window.alert("error: " + e.getMessage());
                return;
            }
        }

        ScreenWindow modal = new ScreenWindow(ScreenWindow.Mode.DIALOG);
        modal.setName(consts.get("prepTestPicker"));
        modal.setContent(prepPickerScreen);
        prepPickerScreen.setManager(manager);
    }
    
    private void selectedPrepTest(Integer prepTestId){
        TreeDataItem selectedRow;
        int selectedIndex;
        SampleDataBundle bundle, tmpBundle;
        ArrayList<SampleDataBundle> bundles;
        Integer currentPrepId = checkForPrepTest(prepTestId);
        
        bundle = null;
        selectedRow = itemsTree.getSelection();
        selectedIndex = itemsTree.getSelectedRow();
        tmpBundle = (SampleDataBundle)selectedRow.data;
        
        if(currentPrepId != null){
            bundle = (SampleDataBundle)selectedRow.data;
            AnalysisViewDO anDO = bundle.analysisTestDO;
            
            anDO.setPreAnalysisId(currentPrepId);
        }else{
            bundle = new SampleDataBundle();
            bundle.sampleDO = manager.getSample();
            bundle.analysisManager = tmpBundle.analysisManager;
            bundle.analysisTestDO = new AnalysisViewDO();
            bundle.sampleItemDO = tmpBundle.sampleItemDO;
            bundle.sampleItemManager = tmpBundle.sampleItemManager;
            bundle.type = SampleDataBundle.Type.ANALYSIS;
            
            TestManager testMan = null;
            try{
                testMan = TestManager.fetchWithPrepTests(prepTestId);
                
            }catch(Exception e){
                Window.alert(e.getMessage());
            }
            
            analysisTab.setupBundle(bundle, testMan);
            
            //need to put new row in tree
            TreeDataItem newPrepRow = new TreeDataItem(2);
            newPrepRow.leafType = "analysis";
            newPrepRow.checkForChildren(false);
            newPrepRow.data = bundle;
            
            //set the selected row to in prep
            itemsTree.setCell(selectedIndex, 1, analysisInPrep);
            tmpBundle.analysisTestDO.setStatusId(analysisInPrep);
            
            //set the pre analysis id
            bundle.analysisTestDO.setId(getNextTempId());
            tmpBundle.analysisTestDO.setPreAnalysisId(bundle.analysisTestDO.getId());
            
            //set the available date on the prep row
            bundle.analysisTestDO.setAvailableDate(Datetime.getInstance(Datetime.YEAR, Datetime.MINUTE));
            
            if("analysis".equals(selectedRow.leafType))
                selectedRow = selectedRow.parent;
            
            if(newPrepRow != null){
                itemsTree.addChildItem(selectedRow, newPrepRow);
                itemsTree.select(newPrepRow);
            }
            
            bundles = new ArrayList<SampleDataBundle>();
            bundles.add(bundle);
            updateTreeAndCheckPrepTests(bundles);
        }
    }
    
    private Integer checkForPrepTest(Integer testId){
        //grab the sample item parent
        Integer id = null;
        TreeDataItem selectedRow = itemsTree.getSelection();
        if("analysis".equals(selectedRow.leafType))
            selectedRow = selectedRow.parent;
        
        for(int i=0; i<selectedRow.getItems().size(); i++){
            SampleDataBundle bundle = (SampleDataBundle)selectedRow.getItem(i).data;
            AnalysisViewDO anDO = bundle.analysisTestDO;
            
            if(testId.equals(anDO.getTestId())){
                id = anDO.getId();
                break;
            }
        }
        
        return id;
        
    }
    
    private TreeDataItem createPrepRowTestRowById(Integer prepTestId){
        SampleDataBundle bundle, selectedBundle;
        TreeDataItem selected = itemsTree.getSelection();
        selectedBundle = (SampleDataBundle)selected.data;
        
        TreeDataItem newRow = new TreeDataItem(2);
        newRow.leafType = "analysis";
        newRow.checkForChildren(false);
        
        try{
            TestManager testMan=null;
            testMan = TestManager.fetchWithPrepTests(prepTestId);
            
            if(testMan != null){
                TestViewDO testDO = testMan.getTest();
                bundle = new SampleDataBundle();
                bundle.type = SampleDataBundle.Type.ANALYSIS;
                
                AnalysisViewDO analysis = new AnalysisViewDO();
                bundle.analysisTestDO = analysis;
                analysis.setId(getNextTempId());
                analysis.setStatusId(analysisLoggedInId);
                analysis.setTestId(prepTestId);
                analysis.setTestName(testDO.getName());
                analysis.setMethodId(testDO.getMethodId());
                analysis.setMethodName(testDO.getMethodName());
                analysis.setIsReportable(testDO.getIsReportable());
                analysis.setRevision(0);
                
                //set the pre analysis to the negative id for now
                selectedBundle.analysisTestDO.setPreAnalysisId(analysis.getId());
                
                bundle.sampleDO = manager.getSample();
                bundle.sampleItemDO = selectedBundle.sampleItemDO;
                bundle.sampleItemManager = selectedBundle.sampleItemManager;
                bundle.analysisManager = selectedBundle.analysisManager;
                bundle.testManager = testMan;
                
                //sections
                TestSectionViewDO defaultDO = testMan.getTestSections().getDefaultSection();
                
                if(defaultDO != null){
                    analysis.setSectionId(defaultDO.getSectionId());
                    analysis.setSectionName(defaultDO.getSection());
                    bundle.sectionsDropdownModel = null;
                }else if(testMan.getTestSections().count() > 0){
                    ArrayList<TestSectionViewDO> sections = testMan.getTestSections().getSections();
                    bundle.sectionsDropdownModel = getSectionsModel(sections);
                }
                newRow.data = bundle;
            }   
        }catch(Exception e){
            Window.alert(e.getMessage());
            return null;
        }
            
        return newRow;
    }
    
    private void updateTreeAndCheckPrepTests(ArrayList<SampleDataBundle> bundles){
        SampleDataBundle bundle, selectedBundle;
        Integer sampleTypeId;
        AnalysisViewDO aDO;
        TreeDataItem selected;
        
        selected = itemsTree.getSelection();
        selectedBundle = (SampleDataBundle)selected.data;
        sampleTypeId = selectedBundle.sampleItemDO.getTypeOfSampleId();
        int selectedIndex = itemsTree.getSelectedRow();
        
        for(int i=0; i<bundles.size(); i++){
            bundle = bundles.get(i);
            aDO = bundle.analysisTestDO;
            
            if(i == 0)
                selected.data = bundle;
            else{
                //the row doesnt exist.  Check to make sure the sample type is right
                //and if it is make a new row and fill it.
                TreeDataItem newRow = new TreeDataItem(2);
                newRow.leafType = "analysis";
                newRow.checkForChildren(false);
                newRow.data = bundle;
                
                itemsTree.addChildItem(selected.parent, newRow);
                selected = newRow;
            }

            itemsTree.select(selected);
            selectedIndex = itemsTree.getSelectedRow();
            itemsTree.setCell(selectedIndex, 0, formatTreeString(aDO.getTestName()) + " : " + formatTreeString(aDO.getMethodName()));
            itemsTree.setCell(selectedIndex, 1, aDO.getStatusId());
            
            //check for prep tests
            try{
                TestPrepManager prepMan = bundle.testManager.getPrepTests();
                if(prepMan.count() > 0){
                    TestPrepViewDO requiredTestPrepDO = prepMan.getRequiredTestPrep();
                    if(requiredTestPrepDO == null)
                        drawTestPrepScreen(prepMan);
                    else
                        selectedPrepTest(requiredTestPrepDO.getPrepTestId());
                }
            }catch(Exception e){
                Window.alert(e.getMessage());
            }
        }
    }
    
    private void cancelAnalysisRow(int selectedIndex){
        TreeDataItem treeRow;
        SampleDataBundle bundle;
        int index;
        AnalysisViewDO anDO;
        
        treeRow = itemsTree.getRow(selectedIndex);
        
        //update the tree row
        bundle = (SampleDataBundle)treeRow.data;
        itemsTree.setCell(selectedIndex, 1, analysisCancelledId);
        
        //update the analysis manager
        index = bundle.analysisManager.getIndex(bundle.analysisTestDO);
        anDO = bundle.analysisManager.getAnalysisAt(index);
        anDO.setStatusId(analysisCancelledId);
        
        //update the sample manager status boolean and the tabs.
        //then redraw the tabs to make sure this change didn't change the status
        manager.setHasReleasedCancelledAnalysis(true);
        sampleItemTab.setData(bundle);
        analysisTab.setData(bundle);
        testResultsTab.setData(bundle);
        analysisNotesTab.setData(bundle);
        storageTab.setData(bundle);
        qaEventsTab.setData(bundle);
        
        drawTabs();
    }
    
    private ArrayList<TableDataRow> getSectionsModel(ArrayList<TestSectionViewDO> sections) {
        ArrayList<TableDataRow> model = new ArrayList<TableDataRow>();
        model.add(new TableDataRow(null, ""));
        
        for(int i=0; i<sections.size(); i++){
            TestSectionViewDO sectionDO = sections.get(i);
            model.add(new TableDataRow(sectionDO.getSectionId(), sectionDO.getSection()));
        }
        
        return model;
    }
    
    private boolean canEdit(){
        return (!sampleReleasedId.equals(manager.getSample().getStatusId()));
    }
    
    protected boolean validate() {
        boolean valid = super.validate();
        
        return (storageTab.validate() && valid);
    }
    
    private int getNextTempId() {
        return --tempId;
    }
}