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
import java.util.Set;

import org.openelis.cache.DictionaryCache;
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
import org.openelis.gwt.event.BeforeGetMatchesEvent;
import org.openelis.gwt.event.BeforeGetMatchesHandler;
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
import org.openelis.gwt.widget.tree.rewrite.TreeDataItem;
import org.openelis.gwt.widget.tree.rewrite.TreeWidget;
import org.openelis.manager.SampleEnvironmentalManager;
import org.openelis.manager.SampleManager;
import org.openelis.metamap.SampleEnvironmentalMetaMap;
import org.openelis.modules.main.client.openelis.OpenELIS;
import org.openelis.modules.sampleLocation.client.SampleLocationScreen;
import org.openelis.modules.sampleOrganization.client.SampleOrganizationScreen;
import org.openelis.modules.sampleProject.client.SampleProjectScreen;
import org.openelis.utilgwt.OrganizationEntryManager;
import org.openelis.utilgwt.ProjectEntryManager;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.TabPanel;

public class EnvironmentalSampleLoginScreen extends Screen implements BeforeGetMatchesHandler, GetMatchesHandler {

    public enum Tabs {SAMPLE_ITEM,ANALYSIS, TEST_RESULT, AN_EXT_COMMENT, 
                        AN_INT_COMMENTS, STORAGE, SMP_EXT_COMMENT, SMP_INT_COMMENTS};    
    protected Tabs               tab           = Tabs.SAMPLE_ITEM;
    protected TextBox location;
    protected AutoComplete<Integer> project;
    //private TreeWidget itemsTestsTree;
    //private AppButton addItemButton, addTestButton, removeRowButton, analysisNoteInt, analysisNoteExt;
    private SampleLocationScreen locationScreen;
    private SampleOrganizationScreen organizationScreen;
    private SampleProjectScreen projectScreen;
    //private TextBox locationTextBox;
    //private ScreenTabPanel tabPanel;
    private boolean canEditTestResultsTab;
    
    //private ScreenDropDownWidget sampleType, sampleStatus, container, unit, analysisStatus;
    //private ScreenAutoCompleteWidget projectAuto, reportToAuto, billToAuto, section, test, method;
    //private ScreenTextBox containerRef, qty, revision;
    //private ScreenCheck isReportable;
    //private ScreenCalendar startedDate, completedDate, releasedDate, printedDate;
    
    private ProjectEntryManager projectManager;
    private OrganizationEntryManager orgManager;
    
    private SampleEnvironmentalMetaMap Meta;
    
    ScreenNavigator nav;
    private SecurityModule       security;
    
    private SampleManager manager;
    
    public EnvironmentalSampleLoginScreen() throws Exception {
        //////////////
     // Call base to get ScreenDef and draw screen
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

        // Setup link between Screen and widget Handlers
        initialize();

        /*
        // Create the Handler for the Contacts tab passing in the ScreenDef
        contactsTab = new ContactsTab(def);

        // Create the Handler for the Notes tab passing in the ScreenDef;
        notesTab = new NotesTab(def);

        // Set up tabs to recieve State Change events from the main Screen.
        addScreenHandler(contactsTab, new ScreenEventHandler<Object>() {
            public void onDataChange(DataChangeEvent event) {
                contactsTab.setManager(manager);

                if (tab == Tabs.CONTACTS)
                    drawTabs();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                StateChangeEvent.fire(contactsTab, event.getState());
            }
        });
*/
  
        // Initialize Screen
        setState(State.DEFAULT);
        
        //setup the dropdowns
        setStatusModel();
        
        /////////////////////////////
        //FIXME move this
        /*
        cache = DictionaryCache.getListByCategorySystemName("analysis_status");
        model = getDictionaryIdEntryList(cache);
        ((TableDropdown)itemsTestsTree.columns.get(1).getColumnWidget("analysis")).setModel(model);
        ((Dropdown)analysisStatus.getWidget()).setModel(model);
        
        cache = DictionaryCache.getListByCategorySystemName("sample_status");
        model = getDictionaryIdEntryList(cache);
        ((Dropdown)sampleStatus.getWidget()).setModel(model);
        
        cache = DictionaryCache.getListByCategorySystemName("sample_container");
        model = getDictionaryIdEntryList(cache);
        ((Dropdown)container.getWidget()).setModel(model);
        
        cache = DictionaryCache.getListByCategorySystemName("unit_of_measure");
        model = getDictionaryIdEntryList(cache);
        ((Dropdown)unit.getWidget()).setModel(model);
        */
        //////////////////////////
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
        
        final AutoComplete<Integer> reportTo = (AutoComplete<Integer>)def.getWidget(Meta.SAMPLE.SAMPLE_ORGANIZATION.ORGANIZATION.getName());
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
        
        final AutoComplete<Integer> billTo = (AutoComplete<Integer>)def.getWidget("billTo");
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
        
        final TreeWidget itemsTree = (TreeWidget)def.getWidget("itemsTestsTree");
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
        
        final AppButton billToLookup = (AppButton)def.getWidget("billToLookup");
        addScreenHandler(billToLookup, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                onOrganizationLookupClick();
            }
            
            public void onStateChange(StateChangeEvent<State> event) {
                billToLookup.enable(EnumSet.of(State.ADD, State.UPDATE, State.QUERY)
                                   .contains(event.getState()));
            }
        });
        
        final AppButton reportToLookup = (AppButton)def.getWidget("reportToLookup");
        addScreenHandler(reportToLookup, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                onOrganizationLookupClick();
            }
            
            public void onStateChange(StateChangeEvent<State> event) {
                reportToLookup.enable(EnumSet.of(State.ADD, State.UPDATE, State.QUERY)
                                   .contains(event.getState()));
            }
        });
        
        final AppButton projectLookup = (AppButton)def.getWidget("projectLookup");
        addScreenHandler(projectLookup, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                onProjectLookupClick();
            }
            
            public void onStateChange(StateChangeEvent<State> event) {
                projectLookup.enable(EnumSet.of(State.ADD, State.UPDATE, State.QUERY)
                                   .contains(event.getState()));
            }
        });
        
        final AppButton locationLookup = (AppButton)def.getWidget("locButton");
        addScreenHandler(locationLookup, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                onLocationLookupClick();
            }
            
            public void onStateChange(StateChangeEvent<State> event) {
                locationLookup.enable(EnumSet.of(State.ADD, State.UPDATE, State.QUERY)
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
        project.setSelection(null, "");
        reportTo.setSelection(null, "");
        billTo.setSelection(null, "");
        
        // Screens now must implement AutoCompleteCallInt and set themselves as the calling interface
        //reportTo.addBeforeGetMatchesHandler(this);
        //reportTo.addGetMatchesHandler(this);
        //billTo.addBeforeGetMatchesHandler(this);
        //billTo.addGetMatchesHandler(this);
        //project.addBeforeGetMatchesHandler(this);
        //project.addGetMatchesHandler(this);
        
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

    /*
    public void performCommand(Enum action, Object obj) {
        if(action.equals(SampleProjectScreen.Action.Commited)){
            form.orgProjectForm.sampleProjectForm.sampleProjectTable.setValue(obj);
            projectManager.setList(form.orgProjectForm.sampleProjectForm.sampleProjectTable.getValue());
            TableDataRow newTableRow = projectManager.getFirstPermanentProject();
            
            if(newTableRow != null)
                projectAuto.load((DropDownField<Integer>)newTableRow.cells[0]);
            else
                ((AutoComplete)projectAuto.getWidget()).setSelections(new ArrayList());
            
        }else if(action.equals(SampleOrganizationScreen.Action.Commited)){
            form.orgProjectForm.sampleOrgForm.sampleOrganizationTable.setValue(obj);
            orgManager.setList(form.orgProjectForm.sampleOrgForm.sampleOrganizationTable.getValue());
            
            //update billto
            TableDataRow newTableRow = orgManager.getBillTo();
            if(newTableRow != null)
                billToAuto.load((DropDownField<Integer>)newTableRow.cells[2]);
            else
                ((AutoComplete)billToAuto.getWidget()).setSelections(new ArrayList());
            
            //update report to
            newTableRow = orgManager.getReportTo();
            if(newTableRow != null)
                reportToAuto.load((DropDownField<Integer>)newTableRow.cells[2]);
            else
                ((AutoComplete)reportToAuto.getWidget()).setSelections(new ArrayList());
            
        }else
            super.performCommand(action, obj);
    }
    */
    
    /*
    public void onChange(Widget sender) {
        if(sender == locationTextBox)
            form.envInfoForm.locationForm.samplingLocation.setValue(locationTextBox.getText());
        else if(sender == projectAuto.getWidget())
            updateProjectListAfterChange();
        else if(sender == reportToAuto.getWidget())
            updateReportToListAfterChange();
        else if(sender == billToAuto.getWidget())
            updateBillToListAfterChange();
        //sample item  tab
        
        //analysis tab
        else if(sender == test.getWidget()){
            //get the subform to save it to
            AnalysisForm af = (AnalysisForm)itemsTestsTree.model.getSelection().getData();
            AutoComplete td = (AutoComplete)test.getWidget();
            ArrayList selections = td.getSelections();
            
            if(selections.size() == 0){
                AutoComplete md = (AutoComplete)method.getWidget();
                md.setSelections(new ArrayList());
            }
            
            StringObject methodName = (StringObject)((TableDataRow<Integer>)selections.get(0)).cells[1];
            Integer methodId = (Integer)((TableDataRow<Integer>)selections.get(0)).getData().getValue();
            DropDownField<Integer> methodField = new DropDownField<Integer>();
            TableDataModel<TableDataRow<Integer>> methodModel = new TableDataModel<TableDataRow<Integer>>();
            methodModel.add(new TableDataRow<Integer>(methodId, methodName));
            methodField.setModel(methodModel);
            methodField.setValue(methodModel.get(0));
            method.load(methodField);
            
            test.submit(af.testId);
            method.submit(af.methodId);
            redrawRow();
        }
        else
            super.onChange(sender);
    }
    */
    
    /*
    public void afterDraw(boolean sucess) {
        ButtonPanel bpanel = (ButtonPanel)getWidget("buttons");
        
        //disable the buttons for the demo for now
        bpanel.enableButton("query", false);
        bpanel.enableButton("add", false);
        
        CommandChain formChain = new CommandChain();
        formChain.addCommand(this);
        formChain.addCommand(bpanel);
        formChain.addCommand(keyList);
        
        itemsTestsTree = (TreeWidget)getWidget("itemsTestsTree");
        itemsTestsTree.model.addTreeModelListener(this);
        
        tabPanel = (ScreenTabPanel)widgets.get("sampleItemTabPanel");
        
        //buttons
        addItemButton = (AppButton)getWidget("addItemButton");
        addTestButton = (AppButton)getWidget("addTestButton");
        removeRowButton = (AppButton)getWidget("removeRowButton");
        analysisNoteInt = (AppButton)getWidget("analysisNoteInt");
        analysisNoteExt = (AppButton)getWidget("analysisNoteExt");
        
        sampleStatus = (ScreenDropDownWidget)widgets.get(Meta.SAMPLE.getStatusId());
        locationTextBox = (TextBox)getWidget(Meta.getSamplingLocation());
        projectAuto = (ScreenAutoCompleteWidget)widgets.get(Meta.SAMPLE.SAMPLE_PROJECT.PROJECT.getName());
        projectManager = new ProjectEntryManager();
        reportToAuto = (ScreenAutoCompleteWidget)widgets.get(Meta.SAMPLE.SAMPLE_ORGANIZATION.ORGANIZATION.getName());
        billToAuto = (ScreenAutoCompleteWidget)widgets.get("billTo");
        orgManager = new OrganizationEntryManager();
        
        //sample item tab widgets
        sampleType = (ScreenDropDownWidget)widgets.get(Meta.SAMPLE.SAMPLE_ITEM.getTypeOfSampleId());
        container = (ScreenDropDownWidget)widgets.get(Meta.SAMPLE.SAMPLE_ITEM.getContainerId());
        containerRef = (ScreenTextBox)widgets.get(Meta.SAMPLE.SAMPLE_ITEM.getContainerReference());
        qty = (ScreenTextBox)widgets.get(Meta.SAMPLE.SAMPLE_ITEM.getQuantity());
        unit = (ScreenDropDownWidget)widgets.get(Meta.SAMPLE.SAMPLE_ITEM.getUnitOfMeasureId());
        
        //analysis tab widgets
        test = (ScreenAutoCompleteWidget)widgets.get(Meta.SAMPLE.SAMPLE_ITEM.ANALYSIS.TEST.getName());
        method = (ScreenAutoCompleteWidget)widgets.get(Meta.SAMPLE.SAMPLE_ITEM.ANALYSIS.TEST.METHOD.getName());
        analysisStatus = (ScreenDropDownWidget)widgets.get(Meta.SAMPLE.SAMPLE_ITEM.ANALYSIS.getStatusId());
        revision = (ScreenTextBox)widgets.get(Meta.SAMPLE.SAMPLE_ITEM.ANALYSIS.getRevision());
        isReportable = (ScreenCheck)widgets.get(Meta.SAMPLE.SAMPLE_ITEM.ANALYSIS.getIsReportable());
        section = (ScreenAutoCompleteWidget)widgets.get(Meta.SAMPLE.SAMPLE_ITEM.ANALYSIS.getSectionId());
        startedDate = (ScreenCalendar) widgets.get(Meta.SAMPLE.SAMPLE_ITEM.ANALYSIS.getStartedDate());
        completedDate = (ScreenCalendar) widgets.get(Meta.SAMPLE.SAMPLE_ITEM.ANALYSIS.getCompletedDate());
        releasedDate = (ScreenCalendar) widgets.get(Meta.SAMPLE.SAMPLE_ITEM.ANALYSIS.getReleasedDate());
        printedDate = (ScreenCalendar) widgets.get(Meta.SAMPLE.SAMPLE_ITEM.ANALYSIS.getPrintedDate());
    
        form.sampleItemAndAnalysisForm.itemsTestsTree.setValue(itemsTestsTree.model.getData());
        
        updateChain.add(afterFetch);
        fetchChain.add(afterFetch);
        
        super.afterDraw(sucess);
    }
    */
    
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
        
        DataChangeEvent.fire(this);
        setState(Screen.State.ADD);
        window.setDone(consts.get("enterInformationPressCommit"));
    }
    
    protected void update() {
        window.setBusy(consts.get("lockForUpdate"));

        try {
            manager = manager.fetchForUpdate();

            DataChangeEvent.fire(this);
            window.clearStatus();
            setState(State.UPDATE);

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
            manager = SampleManager.findById(id);
        
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
    
    //FIXME rewrite this
    /*
    protected SyncCallback<EnvironmentalSampleLoginForm> afterFetch = new SyncCallback<EnvironmentalSampleLoginForm>() {
        public void onFailure(Throwable caught) {   
        }
        public void onSuccess(EnvironmentalSampleLoginForm result) {
            TableDataRow<Integer> perm,report,bill;
            projectManager.setList(form.orgProjectForm.sampleProjectForm.sampleProjectTable.getValue());
            perm = projectManager.getFirstPermanentProject();
          
            if(perm != null)
                projectAuto.load((DropDownField<Integer>)perm.cells[0]);
            
            orgManager.setList(form.orgProjectForm.sampleOrgForm.sampleOrganizationTable.getValue());
            report = orgManager.getReportTo();
            bill = orgManager.getBillTo();
            
            if(report != null)
                reportToAuto.load((DropDownField<Integer>)report.cells[2]);
            
            if(bill != null)
                billToAuto.load((DropDownField<Integer>)bill.cells[2]);
        }
    };*/
    
    
/*
    public boolean onBeforeTabSelected(SourcesTabEvents sender, int tabIndex) {
        return true;
    }

    public void onTabSelected(SourcesTabEvents sender, int tabIndex) {
        loadTabs();
    }
*/
    //
    //start tree manager methods
    //
    /*
    public boolean canAdd(TreeWidget widget, TreeDataItem set, int row) {
        return false;
    }

    public boolean canClose(TreeWidget widget, TreeDataItem set, int row) {
        return true;
    }

    public boolean canDelete(TreeWidget widget, TreeDataItem set, int row) {
        return false;
    }

    public boolean canDrag(TreeWidget widget, TreeDataItem item, int row) {
        return false;
    }

    public boolean canDrop(TreeWidget widget, Widget dragWidget, TreeDataItem dropTarget, int targetRow) {
        return false;
    }

    public boolean canEdit(TreeWidget widget, TreeDataItem set, int row, int col) {
        return false;
    }

    public boolean canOpen(TreeWidget widget, TreeDataItem addRow, int row) {
        return true;
    }

    public boolean canSelect(TreeWidget widget, TreeDataItem set, int row) {
        return true;
    }

    public void drop(TreeWidget widget, Widget dragWidget, TreeDataItem dropTarget, int targetRow) {}

    public void drop(TreeWidget widget, Widget dragWidget) {}

    public boolean canDrop(TreeWidget widget, Widget dragWidget, Widget dropWidget) {
        return false;
    }
    */
    //
    //end tree manager methods
    //
    
    //
    //start tree model listener
    //
    /*
    public void cellUpdated(SourcesTreeModelEvents sender, int row, int cell) {}

    public void dataChanged(SourcesTreeModelEvents sender) {}

    public void rowAdded(SourcesTreeModelEvents sender, int rows) {}

    public void rowClosed(SourcesTreeModelEvents sender, int row, TreeDataItem item) {}

    public void rowDeleted(SourcesTreeModelEvents sender, int row) {}

    public void rowOpened(SourcesTreeModelEvents sender, int row, TreeDataItem item) {}

    public void rowSelectd(SourcesTreeModelEvents sender, int row) {
        TreeDataItem selectedRow = itemsTestsTree.model.getRow(row);
        //enable/disable the tabs and tree buttons
        if(state == State.ADD || state == State.UPDATE)
            addTestButton.changeState(ButtonState.UNPRESSED);
//            itemsTestsTree.setFocus(true);
          
        loadTabs();
   }

    public void rowUnselected(SourcesTreeModelEvents sender, int row) {}

    public void rowUpdated(SourcesTreeModelEvents sender, int row) {}

    public void unload(SourcesTreeModelEvents sender) {}
    */
    //
    //end tree model listener
    //
    
    //
    //start table manager methods
    //
    /*
    public boolean canAdd(TableWidget widget, TableDataRow set, int row) {
        return false;
    }

    public boolean canAutoAdd(TableWidget widget, TableDataRow addRow) {
        return false;
    }

    public boolean canDelete(TableWidget widget,TableDataRow set, int row) {
        return false;
    }

    public boolean canEdit(TableWidget widget, TableDataRow set, int row, int col) {
        return canEditTestResultsTab;
    }

    public boolean canSelect(TableWidget widget, TableDataRow set, int row) {
        return true;
    }
    */
    //
    //end table manager methods
    //

    /*
    public void getChildNodes(final TreeModel model, final int row) {
        final TreeDataItem item = model.getRow(row);
        Integer id = item.key;
        item.getItems().clear();

        window.setBusy();

        SampleTreeForm form = new SampleTreeForm();
        form.treeRow = row;
        form.sampleItemId = id;
        
        screenService.call("getSampleItemAnalysesTreeModel", form, new AsyncCallback<SampleTreeForm>(){
            public void onSuccess(SampleTreeForm result){
                for(int i=0; i<result.treeModel.size(); i++)
                    item.addItem(result.treeModel.get(i));
                item.loaded = true;
                
                model.toggle(row);
                
                window.clearStatus();
            }
            
            public void onFailure(Throwable caught){
                Window.alert(caught.getMessage());
            }
         });        
    }*/
    
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
    
            projectScreen.setProjectManager(manager.getProjects());
            
        } catch (Exception e) {
            e.printStackTrace();
            Window.alert("error: " + e.getMessage());
            return;
        }
    }
    
    private void onOrganizationLookupClick(){
        /*
        if(organizationScreen == null)
            organizationScreen = new SampleOrganizationScreen(form.orgProjectForm.sampleOrgForm, this);
        else
            organizationScreen.setForm(form.orgProjectForm.sampleOrgForm);
        
        ScreenWindow modal = new ScreenWindow(null,"Sample Organization","sampleOrganizationScreen","Loading...",true,false);
        modal.setName(consts.get("sampleOrganization"));
        modal.setContent(organizationScreen);
        */
    }
    
    private void onLocationLookupClick(){
        try {
            if (locationScreen == null) {
                
                    locationScreen = new SampleLocationScreen();
                    locationScreen.addActionHandler(new ActionHandler<SampleLocationScreen.Action>() {
                        public void onAction(ActionEvent<SampleLocationScreen.Action> event) {
                            if (event.getAction() == SampleLocationScreen.Action.COMMIT) {
                                fireLocationDataChanged();
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
            locationScreen.setEnvDO(envDO);
            
        } catch (Exception e) {
            e.printStackTrace();
            Window.alert("error: " + e.getMessage());
            return;
        }
    }
    
    /*
    private void createSampleLocationPopup(){
        if(locationScreen == null)
            locationScreen = new SampleLocationScreen(form.envInfoForm.locationForm);
        else
            locationScreen.setForm(form.envInfoForm.locationForm);
        
        ScreenWindow modal = new ScreenWindow(null,"Sample Location","sampleLocationScreen","Loading...",true,false);
        modal.setName(consts.get("sampleLocation"));
        modal.setContent(locationScreen);
    }
    
    private void updateProjectListAfterChange(){
        TableDataRow<Integer> selectedRow, newTableRow = null;
        TableDataModel<TableDataRow<Integer>> projectModel = form.orgProjectForm.sampleProjectForm.sampleProjectTable.getValue();
        ArrayList selections = ((AutoComplete)projectAuto.getWidget()).getSelections();
        
        if(selections.size() > 0){
            selectedRow = (TableDataRow<Integer>)selections.get(0);
            newTableRow = projectModel.createNewSet();
            
            TableDataModel<TableDataRow<Integer>> model = new TableDataModel<TableDataRow<Integer>>();
            model.add(selectedRow);
            ((DropDownField<Integer>)newTableRow.cells[0]).setModel(model);
            newTableRow.cells[0].setValue(model.get(0));
            newTableRow.cells[1].setValue(selectedRow.cells[1].getValue());
            newTableRow.cells[2].setValue("Y");
        }

        projectManager.addFirstPermanentProject(newTableRow);
        
        newTableRow = projectManager.getFirstPermanentProject();
        
        if(newTableRow != null)
            projectAuto.load((DropDownField<Integer>)newTableRow.cells[0]);
        else
            ((AutoComplete)projectAuto.getWidget()).setSelections(new ArrayList());
        
    }
    
    private void updateReportToListAfterChange(){
        TableDataRow<Integer> selectedRow, newTableRow = null;
        TableDataModel<TableDataRow<Integer>> orgModel = form.orgProjectForm.sampleOrgForm.sampleOrganizationTable.getValue();
        ArrayList selections = ((AutoComplete)reportToAuto.getWidget()).getSelections();
        
        if(selections.size() > 0){
            selectedRow = (TableDataRow<Integer>)selections.get(0);
            newTableRow = orgModel.createNewSet();
            
            TableDataModel<TableDataRow<Integer>> model = new TableDataModel<TableDataRow<Integer>>();
            model.add(selectedRow);
            newTableRow.cells[1].setValue(selectedRow.key);
            ((DropDownField<Integer>)newTableRow.cells[2]).setModel(model);
            newTableRow.cells[2].setValue(model.get(0));
            newTableRow.cells[3].setValue(selectedRow.cells[2].getValue());
            newTableRow.cells[4].setValue(selectedRow.cells[3].getValue());
        }

        orgManager.setReportTo(newTableRow);
        
        newTableRow = orgManager.getReportTo();
        
        if(newTableRow != null)
            reportToAuto.load((DropDownField<Integer>)newTableRow.cells[2]);
        else
            ((AutoComplete)reportToAuto.getWidget()).setSelections(new ArrayList());
        
    }
    
    private void updateBillToListAfterChange(){
        TableDataRow<Integer> selectedRow, newTableRow = null;
        TableDataModel<TableDataRow<Integer>> orgModel = form.orgProjectForm.sampleOrgForm.sampleOrganizationTable.getValue();
        ArrayList selections = ((AutoComplete)billToAuto.getWidget()).getSelections();
        
        if(selections.size() > 0){
            selectedRow = (TableDataRow<Integer>)selections.get(0);
            newTableRow = orgModel.createNewSet();
            
            TableDataModel<TableDataRow<Integer>> model = new TableDataModel<TableDataRow<Integer>>();
            model.add(selectedRow);
            newTableRow.cells[1].setValue(selectedRow.key);
            ((DropDownField<Integer>)newTableRow.cells[2]).setModel(model);
            newTableRow.cells[2].setValue(model.get(0));
            newTableRow.cells[3].setValue(selectedRow.cells[2].getValue());
            newTableRow.cells[4].setValue(selectedRow.cells[3].getValue());
        }

        orgManager.setBillTo(newTableRow);
        
        newTableRow = orgManager.getBillTo();
        
        if(newTableRow != null)
            billToAuto.load((DropDownField<Integer>)newTableRow.cells[2]);
        else
            ((AutoComplete)billToAuto.getWidget()).setSelections(new ArrayList());
        
    }*/
    
    public void onAddItemButtonClick() {/*
        int selectedIndex = itemsTestsTree.model.getSelectedIndex();
        TreeDataItem newRow = itemsTestsTree.model.createTreeItem("sampleItem");
        newRow.cells[0].setValue(form.nextItemSequence + " - ");
        
        form.nextItemSequence++;
        
        /*if(selectedIndex != -1){
            TreeDataItem selectedRow = itemsTestsTree.model.getSelection();
            if(!"sampleItem".equals(selectedRow.leafType))
                selectedRow = selectedRow.parent;
            selectedRow.addItem(newRow);
            
            if (!selectedRow.open)
                selectedRow.toggle();
        }else
        
        itemsTestsTree.model.addRow(newRow);
        
        itemsTestsTree.model.refresh();*/
    }
    
    public void onAddTestButtonClick() {/*
        TreeDataItem newRow = itemsTestsTree.model.createTreeItem("analysis");
        newRow.cells[0].setValue("TEST");
        TreeDataItem selectedRow = itemsTestsTree.model.getRow(itemsTestsTree.model.getSelectedIndex());
        
        if(!"sampleItem".equals(selectedRow.leafType))
            selectedRow = selectedRow.parent;
        
        selectedRow.addItem(newRow);
        
        if (!selectedRow.open)
            selectedRow.toggle();
        
        itemsTestsTree.model.refresh();*/
    }
    
    public void onRemoveRowButtonClick() {/*
        if(itemsTestsTree.model.getSelectedIndex() != -1)
            itemsTestsTree.model.deleteRow(itemsTestsTree.model.getSelectedIndex());
        
        addTestButton.changeState(ButtonState.DISABLED);*/
    }
    
    
    /*
    private void loadTabs(){
        int tabSelected = tabPanel.getSelectedIndex();
        
        String treeRowType = null;
        
        if(itemsTestsTree.model.getSelectedIndex() != -1)
            treeRowType = itemsTestsTree.model.getSelection().leafType;
        
        if(tabSelected == 0){
            if("sampleItem".equals(treeRowType)){
                enableSampleItemTab(true);
                loadSampleItemTab(itemsTestsTree.model.getSelection());
            }else if("analysis".equals(treeRowType)){
                enableSampleItemTab(true);
                loadSampleItemTab(itemsTestsTree.model.getSelection().parent);
            }
        }else if(tabSelected == 1){
            if("sampleItem".equals(treeRowType)){
                enableAnalysisTab(false);
            }else if("analysis".equals(treeRowType)){
                enableAnalysisTab(true);
                loadAnalysisTab(itemsTestsTree.model.getSelection());
            }
        }else if(tabSelected == 2){
            //
        }else if(tabSelected == 3){
            //
        }else if(tabSelected == 4){
            //
        }else if(tabSelected == 5){
            //
        }else if(tabSelected == 6){
            //
        }else if(tabSelected == 7){
            //
        }
    }*/
    
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
    
    //tab load methods
    private void loadSampleItemTab(TreeDataItem itemRow){/*
        SampleItemForm subForm = (SampleItemForm)itemRow.getData();
        
        if(subForm != null)
            load(subForm);*/
    }
    
    private void loadAnalysisTab(TreeDataItem testRow){/*
        AnalysisForm subForm = (AnalysisForm)testRow.getData();
        if(subForm  != null)
            load(subForm);  */
    }
    
    private void loadTestResultsTab(TreeDataItem testRow){
        
    }
    
    private void loadAnExtCommentTab(TreeDataItem testRow){
        
    }
    
    private void loadAnIntCommentsTab(TreeDataItem testRow){
        
    }
    
    private void loadStorageTab(TreeDataItem itemRow){
        
    }
    
    private void loadSmpExtCommentTab(){
        
    }
    
    private void loadSmpIntCommentsTab(){
        
    }
    
    //tab enable/disable methods
    /*
    private void enableSampleItemTab(boolean enable){
        sampleType.enable(enable);
        container.enable(enable);
        containerRef.enable(enable);
        qty.enable(enable);
        unit.enable(enable);
    }
    
    private void enableAnalysisTab(boolean enable){
        test.enable(enable);
        revision.enable(enable);
        isReportable.enable(enable);
        section.enable(enable);
        startedDate.enable(enable);
        completedDate.enable(enable);
        releasedDate.enable(enable);
        printedDate.enable(enable);
    }
    
    private void enableTestResultsTab(boolean enable){
        canEditTestResultsTab = enable; 
    }
    
    private void enableAnExtCommentTab(boolean enable){
        if(enable)
            analysisNoteExt.changeState(ButtonState.UNPRESSED);
        else
            analysisNoteExt.changeState(ButtonState.DISABLED);
    }
    
    private void enableAnIntCommentsTab(boolean enable){
        if(enable)
            analysisNoteInt.changeState(ButtonState.UNPRESSED);
        else
            analysisNoteInt.changeState(ButtonState.DISABLED);        
    }
    
    private void enableStorageTab(boolean enable){
        //FIXME add this code when we have widgets on that tab
    }
    */
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
    private void drawTabs() {
        /*
        if (tab == Tabs.CONTACTS) {
            contactsTab.draw();
        } else if (tab == Tabs.IDENTIFIERS) {
            // manager = OrganizationsManager.findByIdWithIdentifiers(id);
        } else if (tab == Tabs.NOTES) {
            notesTab.draw();
        }*/
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
    
    
    //new methods
    public void onBeforeGetMatches(BeforeGetMatchesEvent event) {
        
    }

    public void onGetMatches(GetMatchesEvent event) {
        /*
        String cat = ((AutoComplete)event.getSource()).cat;
        AutocompleteRPC rpc = new AutocompleteRPC();
        rpc.match = event.getMatch();
        
        try {
            rpc = service.call("getMatches", rpc);
            ArrayList<TableDataRow> model = new ArrayList<TableDataRow>();
            
            
            
            
            
            ((AutoComplete)event.getSource()).showAutoMatches(model);
        }catch(Exception e) {
            Window.alert(e.getMessage());                     
        }
        */
    }
        
    private ArrayList<TreeDataItem> getTreeModel() {
        ArrayList<TreeDataItem> model = new ArrayList<TreeDataItem>();
        
        if(manager == null)
            return model;
        
        try{   
            for(int iter = 0;iter < manager.getSampleItems().count();iter++) {
                SampleItemDO sampleItemRow = (SampleItemDO)manager.getSampleItems().getSampleItemAt(iter);
            
               TreeDataItem row = new TreeDataItem(2);
               row.leafType = "sampleItem";
               row.key = sampleItemRow.getId();
               row.cells.get(0).value = sampleItemRow.getItemSequence()+" - "+sampleItemRow.getContainer();
               row.open = false;
               row.checkForChildren(true);
               
               model.add(row);
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
    
    private void fireLocationDataChanged(){
        DataChangeEvent.fire(this, location);
    }
    
    private void updateReportTo(){
        
    }
    
    private void updateBillTo(){
        
    }
}