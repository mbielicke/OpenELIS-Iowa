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

import org.openelis.cache.DictionaryCache;
import org.openelis.domain.DictionaryDO;
import org.openelis.gwt.common.Query;
import org.openelis.gwt.common.data.DropDownField;
import org.openelis.gwt.common.data.KeyListManager;
import org.openelis.gwt.common.data.StringObject;
import org.openelis.gwt.common.data.TableDataModel;
import org.openelis.gwt.common.data.TableDataRow;
import org.openelis.gwt.common.data.TreeDataItem;
import org.openelis.gwt.screen.CommandChain;
import org.openelis.gwt.screen.ScreenAutoCompleteWidget;
import org.openelis.gwt.screen.ScreenCalendar;
import org.openelis.gwt.screen.ScreenCheck;
import org.openelis.gwt.screen.ScreenDropDownWidget;
import org.openelis.gwt.screen.ScreenTab;
import org.openelis.gwt.screen.ScreenTabPanel;
import org.openelis.gwt.screen.ScreenTextBox;
import org.openelis.gwt.screen.ScreenWindow;
import org.openelis.gwt.widget.AppButton;
import org.openelis.gwt.widget.AutoComplete;
import org.openelis.gwt.widget.ButtonPanel;
import org.openelis.gwt.widget.Dropdown;
import org.openelis.gwt.widget.AppButton.ButtonState;
import org.openelis.gwt.widget.table.TableDropdown;
import org.openelis.gwt.widget.table.TableManager;
import org.openelis.gwt.widget.table.TableWidget;
import org.openelis.gwt.widget.tree.TreeManager;
import org.openelis.gwt.widget.tree.TreeModel;
import org.openelis.gwt.widget.tree.TreeWidget;
import org.openelis.gwt.widget.tree.event.SourcesTreeModelEvents;
import org.openelis.gwt.widget.tree.event.TreeModelListener;
import org.openelis.metamap.SampleEnvironmentalMetaMap;
import org.openelis.modules.main.client.OpenELISScreenForm;
import org.openelis.modules.sampleLocation.client.SampleLocationScreen;
import org.openelis.modules.sampleOrganization.client.SampleOrganizationScreen;
import org.openelis.modules.sampleProject.client.SampleProjectScreen;
import org.openelis.utilgwt.OrganizationEntryManager;
import org.openelis.utilgwt.ProjectEntryManager;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.SyncCallback;
import com.google.gwt.user.client.ui.ChangeListener;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.SourcesTabEvents;
import com.google.gwt.user.client.ui.TabListener;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class EnvironmentalSampleLoginScreen extends OpenELISScreenForm<EnvironmentalSampleLoginForm,Query<TableDataRow<Integer>>> implements ClickListener, ChangeListener, TabListener, TreeManager, TreeModelListener, TableManager {

    public enum LookupType {LOCATION_VIEW, REPORT_TO_VIEW, PROJECT_VIEW};
    
    private TreeWidget itemsTestsTree;
    private AppButton addItemButton, addTestButton, removeRowButton, analysisNoteInt, analysisNoteExt;
    private SampleLocationScreen locationScreen;
    private SampleOrganizationScreen organizationScreen;
    private SampleProjectScreen projectScreen;
    private TextBox locationTextBox;
    private ScreenTabPanel tabPanel;
    private boolean canEditTestResultsTab;
    
    private KeyListManager keyList = new KeyListManager();
    
    private ScreenDropDownWidget sampleType, sampleStatus, container, unit, analysisStatus;
    private ScreenAutoCompleteWidget projectAuto, reportToAuto, billToAuto, section, test, method;
    private ScreenTextBox containerRef, qty, revision;
    private ScreenCheck isReportable;
    private ScreenCalendar startedDate, completedDate, releasedDate, printedDate;
    
    private ProjectEntryManager projectManager;
    private OrganizationEntryManager orgManager;
    
    private SampleEnvironmentalMetaMap Meta = new SampleEnvironmentalMetaMap();
    
    public EnvironmentalSampleLoginScreen() {
        super("org.openelis.modules.environmentalSampleLogin.server.EnvironmentalSampleLoginService");
        query = new Query<TableDataRow<Integer>>();
        
        getScreen(new EnvironmentalSampleLoginForm());
    }

    public void onClick(Widget sender) {
        if (sender == addItemButton)
            onAddItemButtonClick();
        else if (sender == addTestButton)
            onAddTestButtonClick();
        else if (sender == removeRowButton)
            onRemoveRowButtonClick();
    }
    
    public boolean canPerformCommand(Enum action, Object obj) {
        if(action instanceof LookupType)
            return true;
        else
            return super.canPerformCommand(action, obj);
    }
    
    public void performCommand(Enum action, Object obj) {
        if(action.equals(LookupType.LOCATION_VIEW))
            onLocationLookupClick();
        else if(action.equals(LookupType.REPORT_TO_VIEW))
            onOrganizationLookupClick();
        else if(action.equals(LookupType.PROJECT_VIEW))
            onProjectLookupClick();
        else if(action.equals(SampleProjectScreen.Action.Commited)){
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
    
    public void afterDraw(boolean sucess) {
        ButtonPanel bpanel = (ButtonPanel)getWidget("buttons");
        
        //disable the buttons for the demo for now
       // bpanel.enableButton("query", false);
       // bpanel.enableButton("add", false);
        
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
        
        ArrayList cache;
        TableDataModel<TableDataRow> model;
        cache = DictionaryCache.getListByCategorySystemName("type_of_sample");
        model = getDictionaryIdEntryList(cache);
        ((Dropdown)sampleType.getWidget()).setModel(model);
        
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
        
    }
    
    public void add() {
        super.add();
        addTestButton.changeState(ButtonState.DISABLED);
        
        //default the status dropdown to initiated
        form.statusId.setValue(new TableDataRow<Integer>(DictionaryCache.getIdFromSystemName("sample_initiated")));
        sampleStatus.load(form.statusId);
        
        form.nextItemSequence = 0;
    }
    
    public void update() {
        super.update();
        addTestButton.changeState(ButtonState.DISABLED);
    }
    
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
    };
    
    

    public boolean onBeforeTabSelected(SourcesTabEvents sender, int tabIndex) {
        return true;
    }

    public void onTabSelected(SourcesTabEvents sender, int tabIndex) {
        loadTabs();
    }

    //
    //start tree manager methods
    //
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
    //
    //end tree manager methods
    //
    
    //
    //start tree model listener
    //
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
    //
    //end tree model listener
    //
    
    //
    //start table manager methods
    //
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
        if(projectScreen == null)
            projectScreen = new SampleProjectScreen(form.orgProjectForm.sampleProjectForm, this);
        else
            projectScreen.setForm(form.orgProjectForm.sampleProjectForm);
        
        ScreenWindow modal = new ScreenWindow(null,"Sample Project","sampleProjectScreen","Loading...",true,false);
        modal.setName(consts.get("sampleProject"));
        modal.setContent(projectScreen);
    }
    
    private void onOrganizationLookupClick(){
        if(organizationScreen == null)
            organizationScreen = new SampleOrganizationScreen(form.orgProjectForm.sampleOrgForm, this);
        else
            organizationScreen.setForm(form.orgProjectForm.sampleOrgForm);
        
        ScreenWindow modal = new ScreenWindow(null,"Sample Organization","sampleOrganizationScreen","Loading...",true,false);
        modal.setName(consts.get("sampleOrganization"));
        modal.setContent(organizationScreen);
    }
    
    private void onLocationLookupClick(){
        //we need to load the address if it isnt already loaded
        if(form.envInfoForm.locationForm.load)
            createSampleLocationPopup();
        
        else{
            window.setBusy();
            screenService.call("loadLocationForm", form.envInfoForm.locationForm, new AsyncCallback<SampleLocationForm>(){
                public void onSuccess(SampleLocationForm result){
                    form.envInfoForm.locationForm = result;
                    window.clearStatus();
                    createSampleLocationPopup();
                    
                }
                
                public void onFailure(Throwable caught){
                    Window.alert(caught.getMessage());
                }
             });  
        }
        
    }
    
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
        
    }
    
    public void onAddItemButtonClick() {
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
        */
        itemsTestsTree.model.addRow(newRow);
        
        itemsTestsTree.model.refresh();
    }
    
    public void onAddTestButtonClick() {
        TreeDataItem newRow = itemsTestsTree.model.createTreeItem("analysis");
        newRow.cells[0].setValue("TEST");
        TreeDataItem selectedRow = itemsTestsTree.model.getRow(itemsTestsTree.model.getSelectedIndex());
        
        if(!"sampleItem".equals(selectedRow.leafType))
            selectedRow = selectedRow.parent;
        
        selectedRow.addItem(newRow);
        
        if (!selectedRow.open)
            selectedRow.toggle();
        
        itemsTestsTree.model.refresh();
    }
    
    public void onRemoveRowButtonClick() {
        if(itemsTestsTree.model.getSelectedIndex() != -1)
            itemsTestsTree.model.deleteRow(itemsTestsTree.model.getSelectedIndex());
        
        addTestButton.changeState(ButtonState.DISABLED);
    }
    
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
    }
    
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
    }
    
    //tab load methods
    private void loadSampleItemTab(TreeDataItem itemRow){
        SampleItemForm subForm = (SampleItemForm)itemRow.getData();
        
        if(subForm != null)
            load(subForm);
    }
    
    private void loadAnalysisTab(TreeDataItem testRow){
        AnalysisForm subForm = (AnalysisForm)testRow.getData();
        if(subForm  != null)
            load(subForm);  
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
    
    private TableDataModel<TableDataRow> getDictionaryIdEntryList(ArrayList list){
        if(list == null)
            return null;
        
        TableDataModel<TableDataRow> m = new TableDataModel<TableDataRow>();
        
        for(int i=0; i<list.size(); i++){
            TableDataRow<Integer> row = new TableDataRow<Integer>(1);
            DictionaryDO dictDO = (DictionaryDO)list.get(i);
            row.key = dictDO.getId();
            row.cells[0] = new StringObject(dictDO.getEntry());
            m.add(row);
        }
        
        return m;
    }
}