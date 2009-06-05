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
import org.openelis.util.OrganizationEntryManager;
import org.openelis.util.ProjectEntryManager;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
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
    private boolean canEditTestResultsTab = false;
     
    private KeyListManager keyList = new KeyListManager();
    
    private ScreenDropDownWidget sampleType, container, unit, analysisStatus;
    private ScreenAutoCompleteWidget projectAuto, reportToAuto, billToAuto, section, test, method;
    private ScreenTextBox containerRef, qty, revision;
    private ScreenCheck isReportable;
    private ScreenCalendar startedDate, completedDate, releasedDate, printedDate;
    private Dropdown status;
    
    private ProjectEntryManager projectManager;
    private OrganizationEntryManager orgManager;
    
    private SampleEnvironmentalMetaMap Meta = new SampleEnvironmentalMetaMap();
    
    AsyncCallback<EnvironmentalSampleLoginForm> checkModels = new AsyncCallback<EnvironmentalSampleLoginForm>() {
        public void onSuccess(EnvironmentalSampleLoginForm rpc) {
            
            if(rpc.analysisStatuses != null) {
                setAnalysisStatusModel(rpc.analysisStatuses);
                rpc.analysisStatuses = null;
            }
            if(rpc.sampleContainers != null) {
                setSampleContainerModel(rpc.sampleContainers);
                rpc.sampleContainers = null;
            }
            if(rpc.sampleStatuses != null) {
                setSampleStatusModel(rpc.sampleStatuses);
                rpc.sampleStatuses = null;
            }
            if(rpc.sampleTypes != null) {
                setSampleTypeModel(rpc.sampleTypes);
                rpc.sampleTypes = null;
            }
            if(rpc.units != null) {
                setUnitModel(rpc.units);
                rpc.units = null;
            }
        }
        
        public void onFailure(Throwable caught) {
            
        }
    };
    
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
        else
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
        //bpanel.enableButton("query", false);
        //bpanel.enableButton("add", false);
        
        CommandChain formChain = new CommandChain();
        formChain.addCommand(this);
        formChain.addCommand(bpanel);
        formChain.addCommand(keyList);
        
        itemsTestsTree = (TreeWidget)getWidget("itemsTestsTree");
        itemsTestsTree.model.addTreeModelListener(this);
        
        //buttons
        addItemButton = (AppButton)getWidget("addItemButton");
        addTestButton = (AppButton)getWidget("addTestButton");
        removeRowButton = (AppButton)getWidget("removeRowButton");
        analysisNoteInt = (AppButton)getWidget("analysisNoteInt");
        analysisNoteExt = (AppButton)getWidget("analysisNoteExt");
        
        status = (Dropdown)getWidget(Meta.SAMPLE.getStatusId());
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
        
        setSampleTypeModel(form.sampleTypes);
        setAnalysisStatusModel(form.analysisStatuses);
        setSampleStatusModel(form.sampleStatuses);
        setSampleContainerModel(form.sampleContainers);
        setUnitModel(form.units);
        
        /*
         * Null out the rpc models so they are not sent with future rpc calls
         */
        form.analysisStatuses = null;
        form.sampleStatuses = null;
        form.sampleTypes = null;
        form.sampleContainers = null;
        form.units = null;
        
        form.sampleItemAndAnalysisForm.itemsTestsTree.setValue(itemsTestsTree.model.getData());
        
        updateChain.add(0,checkModels);
        updateChain.add(afterFetch);
        fetchChain.add(0,checkModels);
        fetchChain.add(afterFetch);
        abortChain.add(0,checkModels);
        deleteChain.add(0,checkModels);
        commitUpdateChain.add(0,checkModels);
        commitAddChain.add(0,checkModels);
        
        super.afterDraw(sucess);
    }
    
    public void add() {
        super.add();
        addTestButton.changeState(ButtonState.DISABLED);
    }
    
    public void update() {
        super.update();
        addTestButton.changeState(ButtonState.DISABLED);
    }
    
    protected AsyncCallback<EnvironmentalSampleLoginForm> afterFetch = new AsyncCallback<EnvironmentalSampleLoginForm>() {
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
        if(state == State.ADD || state == State.UPDATE){
            addTestButton.changeState(ButtonState.UNPRESSED);
            itemsTestsTree.setFocus(true);
            
            if("sampleItem".equals(selectedRow.leafType)){
                enableDataForSampleItemRow(true);
                enableDataForTestRow(false);
            }else if("analysis".equals(selectedRow.leafType)){
                enableDataForTestRow(true);
                enableDataForSampleItemRow(false);
                
            }
        }
        
        //load the necessary data
        if("sampleItem".equals(selectedRow.leafType)){
        SampleItemForm subForm = (SampleItemForm)itemsTestsTree.model.getRow(row).getData();
        load(subForm);
        }else if("analysis".equals(selectedRow.leafType)){
            AnalysisForm subForm = (AnalysisForm)itemsTestsTree.model.getRow(row).getData();
            load(subForm);    
        }
        
        
        //FIXME code this
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
    
    public void setAnalysisStatusModel(TableDataModel<TableDataRow<Integer>> analysisStatusesModel) {
        ((TableDropdown)itemsTestsTree.columns.get(1).getColumnWidget("analysis")).setModel(analysisStatusesModel);
        ((Dropdown)analysisStatus.getWidget()).setModel(analysisStatusesModel);
    }
    
    public void setSampleContainerModel(TableDataModel<TableDataRow<Integer>> containersModel) {
        ((Dropdown)container.getWidget()).setModel(containersModel);
    }
    
    public void setSampleStatusModel(TableDataModel<TableDataRow<Integer>> statusesModel) {
        status.setModel(statusesModel);
    }
    
    public void setSampleTypeModel(TableDataModel<TableDataRow<Integer>> typesModel) {
        ((Dropdown)sampleType.getWidget()).setModel(typesModel);
    }
    
    public void setUnitModel(TableDataModel<TableDataRow<Integer>> unitsModel) {
        ((Dropdown)unit.getWidget()).setModel(unitsModel);
    }

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
            projectScreen = new SampleProjectScreen(form.orgProjectForm.sampleProjectForm);
        else
            projectScreen.setForm(form.orgProjectForm.sampleProjectForm);
        
        ScreenWindow modal = new ScreenWindow(null,"Sample Project","sampleProjectScreen","Loading...",true,false);
        modal.setName(consts.get("sampleProject"));
        modal.setContent(projectScreen);
    }
    
    private void onOrganizationLookupClick(){
        if(organizationScreen == null)
            organizationScreen = new SampleOrganizationScreen(form.orgProjectForm.sampleOrgForm);
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
        newRow.cells[0].setValue("0 - ");
        
        if(selectedIndex != -1){
            TreeDataItem selectedRow = itemsTestsTree.model.getSelection();
            if(!"sampleItem".equals(selectedRow.leafType))
                selectedRow = selectedRow.parent;
            selectedRow.addItem(newRow);
            
            if (!selectedRow.open)
                selectedRow.toggle();
        }else
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
    
    private void enableDataForSampleItemRow(boolean enable){
        //sample type
        sampleType.enable(enable);
        //container
        container.enable(enable);
        //container ref
        containerRef.enable(enable);
        //qty
        qty.enable(enable);
        //unit
        unit.enable(enable);
        
        //FIXME add storage tab when implemented
    }
    
    private void enableDataForTestRow(boolean enable){
        //test results
        canEditTestResultsTab = enable;
        
        //analysis ext comment
        if(enable)
            analysisNoteExt.changeState(ButtonState.UNPRESSED);
        else
            analysisNoteExt.changeState(ButtonState.DISABLED);
        
        //analysis int comment
        if(enable)
            analysisNoteInt.changeState(ButtonState.UNPRESSED);
        else
            analysisNoteInt.changeState(ButtonState.DISABLED);
    }
    
    private void loadTabs(int loadIndex){
        
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
    /*
    private void loadSampleItemLeaf(TreeDataItem sampleItemLeaf){
        SampleItemForm subForm = (SampleItemForm)sampleItemLeaf.getData();
        
        if(subForm != null){
            sampleItemLeaf.key = subForm.entityKey;
            sampleItemLeaf.cells[0].setValue(subForm.itemSequence + " - " + subForm.container.getTextValue());
            sampleItemLeaf.cells[1].setValue(subForm.typeOfSample.getTextValue());
        }
    }
    
    private void loadAnalysisLeaf(TreeDataItem analysisLeaf){
        AnalysisForm subForm = (AnalysisForm)analysisLeaf.getData();
        
        if(subForm != null){
            analysisLeaf.key = subForm.entityKey;
            analysisLeaf.cells[0].setValue(subForm.testName.getValue() + " - " + subForm.methodName.getValue());
            analysisLeaf.cells[1].setValue(subForm.statusId.getValue());
        }
    }
    
    private void loadSampleItemTree(){
        
        for(int i=0; i<itemsTestsTree.model.numRows(); i++){
            TreeDataItem item = itemsTestsTree.model.getRow(i);
            
            if("sampleItem".equals(item.leafType))
                loadSampleItemLeaf(item);
            else if("analysis".equals(item.leafType))
                loadAnalysisLeaf(item);
            
            //FIXME evenually make this recursive to load subtrees
        }
        
        itemsTestsTree.model.refresh();
    }
    
    private void loadSampleItemTree(int row){
            TreeDataItem item = itemsTestsTree.model.getRow(row);
            
            if("sampleItem".equals(item.leafType))
                loadSampleItemLeaf(item);
            else if("analysis".equals(item.leafType))
                loadAnalysisLeaf(item);
            
            //FIXME evenually make this recursive to load subtrees
            
           // itemsTestsTree.model.setCell(row, col, value);
    }*/
}