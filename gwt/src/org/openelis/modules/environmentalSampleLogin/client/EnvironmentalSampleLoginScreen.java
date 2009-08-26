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
import java.util.Set;

import org.openelis.cache.DictionaryCache;
import org.openelis.common.AutocompleteRPC;
import org.openelis.domain.AnalysisTestDO;
import org.openelis.domain.DictionaryDO;
import org.openelis.domain.IdNameDO;
import org.openelis.domain.OrganizationAutoDO;
import org.openelis.domain.ProjectDO;
import org.openelis.domain.SampleEnvironmentalDO;
import org.openelis.domain.SampleItemDO;
import org.openelis.domain.SampleOrganizationDO;
import org.openelis.domain.SampleProjectDO;
import org.openelis.gwt.common.Datetime;
import org.openelis.gwt.common.EntityLockedException;
import org.openelis.gwt.common.RPC;
import org.openelis.gwt.common.SecurityModule;
import org.openelis.gwt.common.ValidationErrorsList;
import org.openelis.gwt.common.rewrite.QueryData;
import org.openelis.gwt.event.ActionEvent;
import org.openelis.gwt.event.ActionHandler;
import org.openelis.gwt.event.DataChangeEvent;
import org.openelis.gwt.event.GetMatchesEvent;
import org.openelis.gwt.event.GetMatchesHandler;
import org.openelis.gwt.event.StateChangeEvent;
import org.openelis.gwt.screen.ScreenWindow;
import org.openelis.gwt.screen.rewrite.Screen;
import org.openelis.gwt.screen.rewrite.ScreenEventHandler;
import org.openelis.gwt.screen.rewrite.ScreenNavigator;
import org.openelis.gwt.widget.HasField;
import org.openelis.gwt.widget.TextBox;
import org.openelis.gwt.widget.rewrite.AppButton;
import org.openelis.gwt.widget.rewrite.AutoComplete;
import org.openelis.gwt.widget.rewrite.CalendarLookUp;
import org.openelis.gwt.widget.rewrite.CheckBox;
import org.openelis.gwt.widget.rewrite.Dropdown;
import org.openelis.gwt.widget.rewrite.AppButton.ButtonState;
import org.openelis.gwt.widget.table.rewrite.TableDataRow;
import org.openelis.gwt.widget.table.rewrite.event.BeforeCellEditedEvent;
import org.openelis.gwt.widget.table.rewrite.event.BeforeCellEditedHandler;
import org.openelis.gwt.widget.table.rewrite.event.RowAddedEvent;
import org.openelis.gwt.widget.table.rewrite.event.RowAddedHandler;
import org.openelis.gwt.widget.tree.rewrite.TreeDataItem;
import org.openelis.gwt.widget.tree.rewrite.TreeRow;
import org.openelis.gwt.widget.tree.rewrite.TreeWidget;
import org.openelis.manager.AnalysisManager;
import org.openelis.manager.SampleEnvironmentalManager;
import org.openelis.manager.SampleItemManager;
import org.openelis.manager.SampleManager;
import org.openelis.metamap.SampleEnvironmentalMetaMap;
import org.openelis.modules.main.client.openelis.OpenELIS;
import org.openelis.modules.sampleLocation.client.SampleLocationScreen;
import org.openelis.modules.sampleOrganization.client.SampleOrganizationScreen;
import org.openelis.modules.sampleProject.client.SampleProjectScreen;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.BeforeSelectionEvent;
import com.google.gwt.event.logical.shared.BeforeSelectionHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.TabPanel;

public class EnvironmentalSampleLoginScreen extends Screen {

    public enum Tabs {
        SAMPLE_ITEM, ANALYSIS, TEST_RESULT, AN_EXT_COMMENT, AN_INT_COMMENTS,
        STORAGE, SMP_EXT_COMMENT, SMP_INT_COMMENTS
    };

    protected Tabs                     tab = Tabs.SAMPLE_ITEM;

    private SampleItemTab              sampleItemTab;
    private AnalysisTab                analysisTab;
    private TestResultsTab             testResultsTab;
    private SampleExCommentTab         sampleExtCommentTab;
    private SampleIntCommentsTab       sampleIntCommentsTab;
    private AnalysisExCommentTab       analysisExtCommentTab;
    private AnalysisIntCommentsTab     analysisIntCommentsTab;
    private StorageTab                 storageTab;
    
    
    protected TextBox location;
    protected AutoComplete<Integer> project, reportTo, billTo;
    protected TreeWidget itemsTree;

    private SampleLocationScreen locationScreen;
    private SampleOrganizationScreen organizationScreen;
    private SampleProjectScreen projectScreen;
    
    private SampleEnvironmentalMetaMap Meta;
    
    ScreenNavigator nav;
    private SecurityModule       security;
    
    private SampleManager manager;
    
    public EnvironmentalSampleLoginScreen() throws Exception {
        //Call base to get ScreenDef and draw screen
        super("OpenELISServlet?service=org.openelis.modules.environmentalSampleLogin.server.EnvironmentalSampleLoginService");
        manager = SampleManager.getInstance();
        manager.getSample().setDomain(SampleManager.ENVIRONMENTAL_DOMAIN_FLAG);

        nav = new ScreenNavigator<SampleEnvQuery>(this) {
            public void getSelection(RPC entry) {
                fetch(((IdNameDO)entry).getId());
            }
            public void loadPage(SampleEnvQuery query) {
                loadQueryPage(query);
            }
        };
        Meta = new SampleEnvironmentalMetaMap();
        
        //FIXME change this when we can add the module
        security = OpenELIS.security.getModule("organization");
        
        sampleItemTab = new SampleItemTab(def);
        
        analysisTab = new AnalysisTab(def);
        
        testResultsTab = new TestResultsTab(def);
        
        analysisExtCommentTab = new AnalysisExCommentTab(def, "anExNotesPanel", "anExNoteButton", true);
        
        analysisIntCommentsTab = new AnalysisIntCommentsTab(def, "anIntNotesPanel", "anIntNoteButton", false);
        
        storageTab = new StorageTab(def);
        
        sampleExtCommentTab = new SampleExCommentTab(def, "sampleExtNotesPanel", "sampleExtNoteButton", true);
        
        sampleIntCommentsTab = new SampleIntCommentsTab(def, "sampleIntNotesPanel", "sampleIntNoteButton", false);
        
        // Setup link between Screen and widget Handlers
        initialize();
        
        //setup the dropdowns
        setStatusModel();
        setAnalysisStatusModel();
        
        //Initialize Screen
        setState(State.DEFAULT);
        
        DataChangeEvent.fire(this);
    }
    
    private void initialize() {
        final TextBox accessionNumber = (TextBox)def.getWidget(Meta.SAMPLE.getAccessionNumber());
        addScreenHandler(accessionNumber, new ScreenEventHandler<Integer>() {
            public void onDataChange(DataChangeEvent event) {
                accessionNumber.setValue(getString(manager.getSample().getAccessionNumber()));
            }

            public void onValueChange(ValueChangeEvent<Integer> event) {
                manager.getSample().setAccessionNumber(event.getValue());
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
        
        final TextBox orderNumber = (TextBox)def.getWidget("orderNumber");
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
        
        final CalendarLookUp collectedDate = (CalendarLookUp)def.getWidget(Meta.SAMPLE.getCollectionDate());
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
        
        final CalendarLookUp collectedTime = (CalendarLookUp)def.getWidget(Meta.SAMPLE.getCollectionTime());
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
        
        final CalendarLookUp receivedDate = (CalendarLookUp)def.getWidget(Meta.SAMPLE.getReceivedDate());
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
        
        final Dropdown<Integer> statusId = (Dropdown<Integer>)def.getWidget(Meta.SAMPLE.getStatusId());
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
        
        final TextBox clientReference = (TextBox)def.getWidget(Meta.SAMPLE.getClientReference());
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
        
        final CheckBox isHazardous = (CheckBox)def.getWidget(Meta.getIsHazardous());
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
        
        final TextBox description = (TextBox)def.getWidget(Meta.getDescription());
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
        
        final TextBox collector = (TextBox)def.getWidget(Meta.getCollector());
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
        
        final TextBox collectorPhone = (TextBox)def.getWidget(Meta.getCollectorPhone());
        addScreenHandler(collector, new ScreenEventHandler<String>() {
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
        
        location = (TextBox)def.getWidget(Meta.getSamplingLocation());
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
        
        project = (AutoComplete<Integer>)def.getWidget(Meta.SAMPLE.SAMPLE_PROJECT.PROJECT.getName());
        addScreenHandler(project, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                try{
                    SampleProjectDO projectDO = manager.getProjects().getFirstPermanentProject();
                    
                    if(projectDO != null)
                        project.setSelection(projectDO.getId(), projectDO.getProject().getName());
                    else
                        project.setSelection(null, "");
                    
                }catch(Exception e){
                    Window.alert(e.getMessage());
                }
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                TableDataRow selectedRow = project.getSelection();
                SampleProjectDO projectDO = null;
                try{
                    if(selectedRow.key != null){
                        projectDO = new SampleProjectDO();
                        projectDO.setIsPermanent("Y");
                        projectDO.setProjectId((Integer)selectedRow.key);
                        projectDO.getProject().setName((String)selectedRow.cells.get(0).value);
                        projectDO.getProject().setDescription((String)selectedRow.cells.get(1).value);
                    }
                    
                    manager.getProjects().addFirstPermanentProject(projectDO);
                    
                    projectDO = manager.getProjects().getFirstPermanentProject();
                    
                    if(projectDO != null)
                        project.setSelection(projectDO.getProjectId(), projectDO.getProject().getName());
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
               AutocompleteRPC rpc = new AutocompleteRPC();
               rpc.match = event.getMatch();
               try {
                   rpc = service.call("getProjectMatches", rpc);
                   ArrayList<TableDataRow> model = new ArrayList<TableDataRow>();
                       
                   for (int i=0; i<rpc.model.size(); i++){
                       ProjectDO autoDO = (ProjectDO)rpc.model.get(i);
                       
                       TableDataRow row = new TableDataRow(2);
                       row.key = autoDO.getId();
                       row.cells.get(0).value = autoDO.getName();
                       row.cells.get(1).value = autoDO.getDescription();
                       model.add(row);
                   } 
                   
                   project.showAutoMatches(model);
                       
               }catch(Exception e) {
                   Window.alert(e.getMessage());                     
               }
            } 
        });
        
        reportTo = (AutoComplete<Integer>)def.getWidget(Meta.SAMPLE.SAMPLE_ORGANIZATION.ORGANIZATION.getName());
        addScreenHandler(reportTo, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                try{
                    SampleOrganizationDO reportToOrg = manager.getOrganizations().getFirstReportTo();
                    
                    if(reportToOrg != null)
                        reportTo.setSelection(reportToOrg.getId(), reportToOrg.getOrganization().getName());
                    else
                        reportTo.setSelection(null, "");
                    
                }catch(Exception e){
                    Window.alert(e.getMessage());
                }
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                TableDataRow selectedRow = project.getSelection();
                SampleOrganizationDO reportToOrg = null;
                try{
                    if(selectedRow.key != null){
                        reportToOrg = new SampleOrganizationDO();
                        reportToOrg.setOrganizationId((Integer)selectedRow.key);
                        reportToOrg.getOrganization().setName((String)selectedRow.cells.get(0).value);
                        reportToOrg.getOrganization().getAddressDO().setCity((String)selectedRow.cells.get(2).value);
                        reportToOrg.getOrganization().getAddressDO().setState((String)selectedRow.cells.get(3).value);
                    }
                    
                    manager.getOrganizations().setReportTo(reportToOrg);
                    
                    reportToOrg = manager.getOrganizations().getFirstReportTo();
                    
                    if(reportToOrg != null)
                        reportTo.setSelection(reportToOrg.getId(), reportToOrg.getOrganization().getName());
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
        
        billTo = (AutoComplete<Integer>)def.getWidget("billTo");
        addScreenHandler(billTo, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                try{
                    SampleOrganizationDO billToOrg = manager.getOrganizations().getFirstBillTo();
                    
                    if(billToOrg != null)
                        billTo.setSelection(billToOrg.getId(), billToOrg.getOrganization().getName());
                    else
                        billTo.setSelection(null, "");
                    
                }catch(Exception e){
                    Window.alert(e.getMessage());
                }
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                //((SampleEnvironmentalManager)manager.getDomainManager()).getEnvironmental().setSamplingLocation(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                billTo.enable(EnumSet.of(State.ADD, State.UPDATE, State.QUERY)
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
               
        itemsTree.addBeforeSelectionHandler(new BeforeSelectionHandler<TreeRow>(){
           public void onBeforeSelection(BeforeSelectionEvent<TreeRow> event) {
               //do nothing
                
            } 
        });
        itemsTree.addSelectionHandler(new SelectionHandler<TreeRow>(){
           public void onSelection(SelectionEvent<TreeRow> event) {
               SampleDataBundle data;
               
               TreeDataItem selection = itemsTree.getSelection();
               
               if(selection != null)
                   data = (SampleDataBundle)selection.data;
               else
                   data = new SampleDataBundle();
               
               sampleItemTab.setData(data);
               analysisTab.setData(data);
               testResultsTab.setData(data);
               analysisExtCommentTab.setData(data);
               analysisIntCommentsTab.setData(data);
               storageTab.setData(data);
               
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
        
        final AppButton billToLookup = (AppButton)def.getWidget("billToLookup");
        addScreenHandler(billToLookup, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                onOrganizationLookupClick();
            }
            
            public void onStateChange(StateChangeEvent<State> event) {
                billToLookup.enable(EnumSet.of(State.ADD, State.UPDATE, State.DISPLAY)
                                   .contains(event.getState()));
            }
        });
        
        final AppButton reportToLookup = (AppButton)def.getWidget("reportToLookup");
        addScreenHandler(reportToLookup, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                onOrganizationLookupClick();
            }
            
            public void onStateChange(StateChangeEvent<State> event) {
                reportToLookup.enable(EnumSet.of(State.ADD, State.UPDATE, State.DISPLAY)
                                   .contains(event.getState()));
            }
        });
        
        final AppButton projectLookup = (AppButton)def.getWidget("projectLookup");
        addScreenHandler(projectLookup, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                onProjectLookupClick();
            }
            
            public void onStateChange(StateChangeEvent<State> event) {
                projectLookup.enable(EnumSet.of(State.ADD, State.UPDATE, State.DISPLAY)
                                   .contains(event.getState()));
            }
        });
        
        final AppButton locationLookup = (AppButton)def.getWidget("locButton");
        addScreenHandler(locationLookup, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                onLocationLookupClick();
            }
            
            public void onStateChange(StateChangeEvent<State> event) {
                locationLookup.enable(EnumSet.of(State.ADD, State.UPDATE, State.DISPLAY)
                                   .contains(event.getState()));
            }
        });
        
        final AppButton addItem = (AppButton)def.getWidget("addItemButton");
        addScreenHandler(addItem, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                onAddItemButtonClick();
            }
            
            public void onStateChange(StateChangeEvent<State> event) {
                addItem.enable(EnumSet.of(State.ADD, State.UPDATE)
                                   .contains(event.getState()));
            }
        });
        
        final AppButton addTest = (AppButton)def.getWidget("addTestButton");
        addScreenHandler(addTest, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                onAddTestButtonClick();
            }
            
            public void onStateChange(StateChangeEvent<State> event) {
                addTest.enable(EnumSet.of(State.ADD, State.UPDATE)
                                   .contains(event.getState()));
            }
        });
        
        final AppButton removeRow = (AppButton)def.getWidget("removeRowButton");
        addScreenHandler(removeRow, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                onRemoveRowButtonClick();
            }
            
            public void onStateChange(StateChangeEvent<State> event) {
                removeRow.enable(EnumSet.of(State.ADD, State.UPDATE)
                                   .contains(event.getState()));
            }
        });
        
        //Create Default blank model for AutoComplete fields reportto, billto, and project
        //project.setSelection(null, "");
        //reportTo.setSelection(null, "");
        //billTo.setSelection(null, "");
        
        // Set up tabs to recieve State Change events from the main Screen.
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
        
        addScreenHandler(analysisExtCommentTab, new ScreenEventHandler<Object>() {
            public void onDataChange(DataChangeEvent event) {
                    analysisExtCommentTab.setData(new SampleDataBundle());

                    if (tab == Tabs.AN_EXT_COMMENT)
                        drawTabs();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                analysisExtCommentTab.setState(event.getState());
            }
        });
        
        addScreenHandler(analysisIntCommentsTab, new ScreenEventHandler<Object>() {
            public void onDataChange(DataChangeEvent event) {
                    analysisIntCommentsTab.setData(new SampleDataBundle());

                    if (tab == Tabs.AN_INT_COMMENTS)
                        drawTabs();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                analysisIntCommentsTab.setState(event.getState());
            }
        });
        
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
        
        addScreenHandler(sampleExtCommentTab, new ScreenEventHandler<Object>() {
            public void onDataChange(DataChangeEvent event) {
                sampleExtCommentTab.setManager(manager);

                if (tab == Tabs.SMP_EXT_COMMENT)
                    drawTabs();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                sampleExtCommentTab.setState(event.getState());
            }
        });
        
        addScreenHandler(sampleIntCommentsTab, new ScreenEventHandler<Object>() {
            public void onDataChange(DataChangeEvent event) {
                sampleIntCommentsTab.setManager(manager);

                if (tab == Tabs.SMP_INT_COMMENTS)
                    drawTabs();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                sampleIntCommentsTab.setState(event.getState());
            }
        });
        
        sampleItemTab.addActionHandler(new ActionHandler<SampleItemTab.Action>(){
            public void onAction(ActionEvent<SampleItemTab.Action> event) {
                if(event.getAction() == SampleItemTab.Action.CHANGED){
                    TreeDataItem selected = itemsTree.getSelection();
                    int selectedIndex = itemsTree.getSelectedIndex();
                    
                    //make sure it is a sample item row
                    if("analysis".equals(selected.leafType))
                        selected = selected.parent;
                    
                    SampleDataBundle data = (SampleDataBundle)selected.data;
                    SampleItemDO itemDO = data.sampleItemDO;
                    
                    itemsTree.setCell(selectedIndex, 0, itemDO.getItemSequence()+" - "+itemDO.getContainer());
                    itemsTree.setCell(selectedIndex, 1, itemDO.getTypeOfSample());
                }
            }
        });
        
        analysisTab.addActionHandler(new ActionHandler<AnalysisTab.Action>(){
            public void onAction(ActionEvent<AnalysisTab.Action> event) {
                if(event.getAction() == AnalysisTab.Action.CHANGED){
                    TreeDataItem selected = itemsTree.getSelection();
                    int selectedIndex = itemsTree.getSelectedIndex();
                    
                    SampleDataBundle data = (SampleDataBundle)selected.data;
                    AnalysisTestDO aDO = data.analysisTestDO;
                    
                    itemsTree.setCell(selectedIndex, 0, aDO.test.getName() + " : " + aDO.test.getMethodName());
                    itemsTree.setCell(selectedIndex, 1, aDO.getStatusId());
                }
            }
        });
        
        final AppButton queryButton = (AppButton)def.getWidget("query");
        addScreenHandler(queryButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                query();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                if(EnumSet.of(State.DEFAULT, State.DISPLAY).contains(event.getState()) && 
                                security.hasSelectPermission())
                    queryButton.enable(true);
                else if (event.getState() == State.QUERY)
                    queryButton.changeState(ButtonState.LOCK_PRESSED);
                else
                    queryButton.enable(false);
            }
        });

        final AppButton addButton = (AppButton)def.getWidget("add");
        addScreenHandler(addButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                add();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                if (EnumSet.of(State.DEFAULT, State.DISPLAY).contains(event.getState()) && 
                                security.hasAddPermission())
                    addButton.enable(true);
                else if (EnumSet.of(State.ADD).contains(event.getState()))
                    addButton.changeState(ButtonState.LOCK_PRESSED);
                else
                    addButton.enable(false);
            }
        });

        final AppButton updateButton = (AppButton)def.getWidget("update");
        addScreenHandler(updateButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                update();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                if (EnumSet.of(State.DISPLAY).contains(event.getState()) && 
                                security.hasUpdatePermission())
                    updateButton.enable(true);
                else if (EnumSet.of(State.UPDATE).contains(event.getState()))
                    updateButton.changeState(ButtonState.LOCK_PRESSED);
                else
                    updateButton.enable(false);

            }
        });

        final AppButton nextButton = (AppButton)def.getWidget("next");
        addScreenHandler(nextButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                next();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                nextButton.enable(EnumSet.of(State.DISPLAY).contains(event.getState()));
            }
        });

        final AppButton prevButton = (AppButton)def.getWidget("previous");
        addScreenHandler(prevButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                previous();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                prevButton.enable(EnumSet.of(State.DISPLAY).contains(event.getState()));
            }
        });

        final AppButton commitButton = (AppButton)def.getWidget("commit");
        addScreenHandler(commitButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                commit();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                commitButton.enable(EnumSet.of(State.QUERY, State.ADD, State.UPDATE).contains(event.getState()));
            }
        });

        final AppButton abortButton = (AppButton)def.getWidget("abort");
        addScreenHandler(abortButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                abort();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                abortButton.enable(EnumSet.of(State.QUERY, State.ADD, State.UPDATE).contains(event.getState()));
            }
        });

        // Get TabPanel and set Tab Selection Handlers
        final TabPanel tabs = (TabPanel)def.getWidget("sampleItemTabPanel");
        tabs.addSelectionHandler(new SelectionHandler<Integer>() {
            public void onSelection(SelectionEvent<Integer> event) {
                int tabIndex = event.getSelectedItem().intValue();
                if (tabIndex == Tabs.SAMPLE_ITEM.ordinal())
                    tab = Tabs.SAMPLE_ITEM;
                else if (tabIndex == Tabs.ANALYSIS.ordinal())
                    tab = Tabs.ANALYSIS;
                else if (tabIndex == Tabs.TEST_RESULT.ordinal())
                    tab = Tabs.TEST_RESULT;
                else if (tabIndex == Tabs.AN_EXT_COMMENT.ordinal())
                    tab = Tabs.AN_EXT_COMMENT;
                else if (tabIndex == Tabs.AN_INT_COMMENTS.ordinal())
                    tab = Tabs.AN_INT_COMMENTS;
                else if (tabIndex == Tabs.STORAGE.ordinal())
                    tab = Tabs.STORAGE;
                else if (tabIndex == Tabs.SMP_EXT_COMMENT.ordinal())
                    tab = Tabs.SMP_EXT_COMMENT;
                else if (tabIndex == Tabs.SMP_INT_COMMENTS.ordinal())
                    tab = Tabs.SMP_INT_COMMENTS;
                
                window.setBusy(consts.get("loadingMessage"));
                
                drawTabs();
                window.clearStatus();
            }
        });
    }

    protected void query() {
        manager = SampleManager.getInstance();
        manager.getSample().setDomain(SampleManager.ENVIRONMENTAL_DOMAIN_FLAG);
        
        DataChangeEvent.fire(this);
        setState(Screen.State.QUERY);
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
            ((SampleEnvironmentalManager)manager.getDomainManager()).getEnvironmental().setIsHazardous("N");
        }catch(Exception e){
            Window.alert(e.getMessage());
            return;
        }
        
        manager.getSample().setStatusId(DictionaryCache.getIdFromSystemName("sample_initiated"));
        manager.getSample().setNextItemSequence(0);
        
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
    
    public void commit() {
        if (state == State.UPDATE) {
            if (validate()) {
                window.setBusy(consts.get("updating"));
                commitUpdate();
            } else {
                window.setError(consts.get("correctErrors"));
            }
        }
        if (state == State.ADD) {
            if (validate()) {
                window.setBusy(consts.get("adding"));
                commitAdd();
            } else {
                window.setError(consts.get("correctErrors"));
            }
        }
        if (state == State.QUERY) {
            if (validate()) {
                ArrayList<QueryData> qFields = getQueryFields();
                commitQuery(qFields);
            } else {
                window.setError(consts.get("correctErrors"));
            }
        }
    }

    public void abort() {
        if (state == State.UPDATE) {
            window.setBusy(consts.get("cancelChanges"));

            try {
                manager = manager.abort();

                DataChangeEvent.fire(this);
                clearErrors();
                setState(State.DISPLAY);
                
                window.clearStatus();
            } catch (Exception e) {
                Window.alert(e.getMessage());
                window.clearStatus();
            }

        } else if (state == State.ADD) {
            manager = SampleManager.getInstance();
            manager.getSample().setDomain(SampleManager.ENVIRONMENTAL_DOMAIN_FLAG);
            
            DataChangeEvent.fire(this);
            clearErrors();
            setState(State.DEFAULT);
            window.setDone(consts.get("addAborted"));
        } else if (state == State.QUERY) {
            manager = SampleManager.getInstance();
            manager.getSample().setDomain(SampleManager.ENVIRONMENTAL_DOMAIN_FLAG);

            DataChangeEvent.fire(this);
            setState(State.DEFAULT);
            clearErrors();
            window.setDone(consts.get("queryAborted"));
        }
    }
    
    protected void fetch(Integer id) {
        window.setBusy(consts.get("fetching"));
        
        try {
            manager = SampleManager.findByIdWithItemsAnalyses(id);
        
        } catch (Exception e) {
            setState(Screen.State.DEFAULT);
            Window.alert(consts.get("fetchFailed") + e.getMessage());
            window.clearStatus();

            return;
        }

        DataChangeEvent.fire(this);

        setState(Screen.State.DISPLAY);
        window.clearStatus();
    }
    
    public ArrayList<QueryData> getQueryFields() {
        ArrayList<QueryData> list = new ArrayList<QueryData>();
        Set<String> keys = def.getWidgets().keySet();
        for (String key : def.getWidgets().keySet()) {
            if (def.getWidget(key) instanceof HasField) {
                ((HasField)def.getWidget(key)).getQuery(list, key);
            }
        }
        return list;
    }
    
    public void commitAdd() {
        window.setBusy(consts.get("commiting"));
        try {
            manager = manager.add();

            DataChangeEvent.fire(this);
            setState(Screen.State.DISPLAY);
            window.clearStatus();
        } catch (ValidationErrorsList e) {
            showErrors(e);

        } catch (Exception e) {
            Window.alert("commitAdd(): " + e.getMessage());
            window.clearStatus();
        }
    }

    public void commitUpdate() {
        window.setBusy(consts.get("commiting"));
        try {
            manager = manager.update();

            DataChangeEvent.fire(this);
            setState(Screen.State.DISPLAY);
            window.clearStatus();
        } catch (ValidationErrorsList e) {
            showErrors(e);

        } catch (Exception e) {
            Window.alert("commitUpdate(): " + e.getMessage());

        }
    }

    public void commitQuery(ArrayList<QueryData> qFields) {
        SampleEnvQuery query = new SampleEnvQuery();
        query.fields = qFields;
        window.setBusy(consts.get("querying"));

        service.call("query", query, new AsyncCallback<SampleEnvQuery>() {
            public void onSuccess(SampleEnvQuery query) {
                loadQuery(query);
            }

            public void onFailure(Throwable caught) {
                if (caught instanceof ValidationErrorsList)
                    showErrors((ValidationErrorsList)caught);
                else
                    Window.alert(caught.getMessage());
            }
        });
    }
    
    public void loadQuery(SampleEnvQuery query) {
        manager = SampleManager.getInstance();
        manager.getSample().setDomain(SampleManager.ENVIRONMENTAL_DOMAIN_FLAG);
        
        DataChangeEvent.fire(this);

        loadQueryPage(query);
    }

    private void loadQueryPage(SampleEnvQuery query) {
        window.setDone(consts.get("queryingComplete"));
        if (query.results == null || query.results.size() == 0) {
            window.setDone(consts.get("noRecordsFound"));
        } else
            window.setDone(consts.get("queryingComplete"));
        query.model = new ArrayList<TableDataRow>();
        for (IdNameDO entry : query.results) {
            query.model.add(new TableDataRow(entry.getId(), entry.getName()));
        }
        nav.setQuery(query);
    }
       
    private void onProjectLookupClick(){
        try {
            if (projectScreen == null) {
                final EnvironmentalSampleLoginScreen env = this;
                projectScreen = new SampleProjectScreen();
                projectScreen.addActionHandler(new ActionHandler<SampleProjectScreen.Action>() {
                        public void onAction(ActionEvent<SampleProjectScreen.Action> event) {
                            if (event.getAction() == SampleProjectScreen.Action.COMMIT) {
                                DataChangeEvent.fire(env, project);

                            }
                        }
                    });
            }
    
            ScreenWindow modal = new ScreenWindow(null,
                                                  "Edit Sample Project",
                                                  "sampleProjectScreen",
                                                  "",
                                                  true,
                                                  false);
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
                organizationScreen = new SampleOrganizationScreen();
                
                organizationScreen.addActionHandler(new ActionHandler<SampleOrganizationScreen.Action>() {
                    public void onAction(ActionEvent<SampleOrganizationScreen.Action> event) {
                        if (event.getAction() == SampleOrganizationScreen.Action.COMMIT) {
                            DataChangeEvent.fire(env, reportTo);
                            DataChangeEvent.fire(env, billTo);
                        }
                    }
                });
            }
            
            ScreenWindow modal = new ScreenWindow(null,
                                                  "Edit Sample Organization",
                                                  "sampleOrganizationScreen",
                                                  "",
                                                  true,
                                                  false);
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
                    locationScreen = new SampleLocationScreen();
                    
                    locationScreen.addActionHandler(new ActionHandler<SampleLocationScreen.Action>() {
                        public void onAction(ActionEvent<SampleLocationScreen.Action> event) {
                            if (event.getAction() == SampleLocationScreen.Action.COMMIT) {
                                DataChangeEvent.fire(env, location);
                            }
                        }
                    });
            }
    
            ScreenWindow modal = new ScreenWindow(null,
                                                  "Edit Sample Location",
                                                  "sampleLocationScreen",
                                                  "",
                                                  true,
                                                  false);
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
        newRow.cells.get(0).value = nextItemSequence + " - ";
        
        SampleItemDO siDO = new SampleItemDO();
        siDO.setItemSequence(nextItemSequence);
        
        try{
            
            SampleDataBundle data = new SampleDataBundle(manager.getSampleItems(),siDO);
            newRow.data = data;
        }catch(Exception e){
            Window.alert(e.getMessage());
            return;
        }
        
        manager.getSample().setNextItemSequence(nextItemSequence+1);
        
        itemsTree.addRow(newRow);
    }
    
    public void onAddTestButtonClick() {
        TreeDataItem newRow = itemsTree.createTreeItem("analysis");
        newRow.cells.get(0).value = "TEST";
        
        TreeDataItem selectedRow = itemsTree.getRow(itemsTree.getSelectedIndex());
        
        if(!"sampleItem".equals(selectedRow.leafType))
            selectedRow = selectedRow.parent;
        
        SampleDataBundle sampleItemData = (SampleDataBundle)selectedRow.data;
        SampleItemDO itemDO = sampleItemData.sampleItemDO;
        int sampleItemIndex = sampleItemData.sampleItemManager.getIndex(itemDO);
        
        AnalysisTestDO aDO = new AnalysisTestDO();
        
        
        try{
            SampleDataBundle data = new SampleDataBundle(sampleItemData.sampleItemManager, itemDO,
                                                         sampleItemData.sampleItemManager.getAnalysisAt(sampleItemIndex),aDO);
            newRow.data = data;
            selectedRow.addItem(newRow);
        }catch(Exception e){
            Window.alert(e.getMessage());
            return;
        }
        
        if (!selectedRow.open)
            selectedRow.toggle();
        
        itemsTree.refresh(true);
    }
    
    public void onRemoveRowButtonClick() {/*
        if(itemsTestsTree.model.getSelectedIndex() != -1)
            itemsTestsTree.model.deleteRow(itemsTestsTree.model.getSelectedIndex());
        
        addTestButton.changeState(ButtonState.DISABLED);*/
    }
    
    
    /*
    private void redrawRow(){
        if("sampleItem".equals(itemsTestsTree.model.getSelection().leafType))
            redrawSampleItemRow();
        else if("analysis".equals(itemsTestsTree.model.getSelection().leafType))
            redrawAnalysisRow();
    }
    
    private void redrawSampleItemRow(){
        TreeModel model = itemsTestsTree.model; 
        int selectedRow = model.getSelectedIndex();
        SampleItemForm subForm = (SampleItemForm)model.getSelection().getData();
        
        if(subForm != null){
            model.setCell(selectedRow, 0, subForm.itemSequence + " - " + subForm.container.getTextValue());
            model.setCell(selectedRow, 1, subForm.typeOfSample.getTextValue());
        }
    }
    
    private void redrawAnalysisRow(){
        TreeModel model = itemsTestsTree.model; 
        int selectedRow = model.getSelectedIndex();
        AnalysisForm subForm = (AnalysisForm)model.getSelection().getData();
        
        if(subForm != null){
            model.setCell(selectedRow, 0, subForm.testId.getTextValue() + " - " + subForm.methodId.getTextValue());
            model.setCell(selectedRow, 1, subForm.statusId.getValue());
        }
    }*/
    
   
    private ArrayList<TableDataRow> getDictionaryIdEntryList(ArrayList list){
        ArrayList<TableDataRow> m = new ArrayList<TableDataRow>();
        TableDataRow row;
        if(list == null)
            return m;
        
        m.add(new TableDataRow(null,""));
        
        for(int i=0; i<list.size(); i++){
            row = new TableDataRow(1);
            DictionaryDO dictDO = (DictionaryDO)list.get(i);
            row.key = dictDO.getId();
            row.cells.get(0).value = dictDO.getEntry();
            m.add(row);
        }
        
        return m;
    }

    private void setStatusModel() {
        ArrayList cache;
        ArrayList<TableDataRow> model;
        cache = DictionaryCache.getListByCategorySystemName("sample_status");
        model = getDictionaryIdEntryList(cache);
        ((Dropdown<Integer>)def.getWidget(Meta.SAMPLE.getStatusId())).setModel(model);
    }
    
    private void setAnalysisStatusModel(){
        ArrayList cache;
        ArrayList<TableDataRow> model;
        cache = DictionaryCache.getListByCategorySystemName("analysis_status");
        model = getDictionaryIdEntryList(cache);
        
        ((Dropdown<Integer>)itemsTree.columns.get("analysis").get(1).colWidget).setModel(model);
    }
    
    private void drawTabs() {
        if (tab == Tabs.SAMPLE_ITEM)
            sampleItemTab.draw();
        else if (tab == Tabs.ANALYSIS)
            analysisTab.draw();
        else if (tab == Tabs.TEST_RESULT)
            testResultsTab.draw();
        else if (tab == Tabs.AN_EXT_COMMENT)
            analysisExtCommentTab.draw();
        else if (tab == Tabs.AN_INT_COMMENTS)
            analysisIntCommentsTab.draw();
        else if (tab == Tabs.STORAGE)
            storageTab.draw();
        else if (tab == Tabs.SMP_EXT_COMMENT)
            sampleExtCommentTab.draw();
        else if (tab == Tabs.SMP_INT_COMMENTS)
            sampleIntCommentsTab.draw();
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
        SampleItemDO itemDO;
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
                row.cells.get(0).value = itemDO.getItemSequence()+" - "+itemDO.getContainer();
                //source,type
                row.cells.get(1).value = itemDO.getTypeOfSample();
                
                SampleDataBundle data = new SampleDataBundle(manager.getSampleItems(), itemDO);
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
                    AnalysisTestDO aDO = (AnalysisTestDO)am.getAnalysisAt(j);
                    
                    treeModelItem = new TreeDataItem(2);
                    treeModelItem.leafType = "analysis";
                    
                    treeModelItem.key = aDO.getId();
                    treeModelItem.cells.get(0).value = aDO.test.getName() + " : " + aDO.test.getMethodName();
                    treeModelItem.cells.get(1).value = aDO.getStatusId();
                    
                    SampleDataBundle aData = new SampleDataBundle(sim, itemDO, am, aDO);
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
        AutocompleteRPC rpc = new AutocompleteRPC();
        rpc.match = match;
        try {
            rpc = service.call("getOrganizationMatches", rpc);
            ArrayList<TableDataRow> model = new ArrayList<TableDataRow>();
                
            for (int i=0; i<rpc.model.size(); i++){
                OrganizationAutoDO autoDO = (OrganizationAutoDO)rpc.model.get(i);
                
                TableDataRow row = new TableDataRow(4);
                row.key = autoDO.getId();
                row.cells.get(0).value = autoDO.getName();
                row.cells.get(1).value = autoDO.getAddress();
                row.cells.get(2).value = autoDO.getCity();
                row.cells.get(3).value = autoDO.getState();
  
                model.add(row);
            } 
            
            widget.showAutoMatches(model);
                
        }catch(Exception e) {
            Window.alert(e.getMessage());                     
        }
    }
}