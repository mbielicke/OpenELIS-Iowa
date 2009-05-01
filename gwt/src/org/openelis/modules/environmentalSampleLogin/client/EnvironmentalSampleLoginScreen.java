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

import org.openelis.gwt.common.Query;
import org.openelis.gwt.common.data.Field;
import org.openelis.gwt.common.data.KeyListManager;
import org.openelis.gwt.common.data.TableDataModel;
import org.openelis.gwt.common.data.TableDataRow;
import org.openelis.gwt.common.data.TreeDataItem;
import org.openelis.gwt.screen.CommandChain;
import org.openelis.gwt.widget.ButtonPanel;
import org.openelis.gwt.widget.Dropdown;
import org.openelis.gwt.widget.table.TableDropdown;
import org.openelis.gwt.widget.tree.TreeManager;
import org.openelis.gwt.widget.tree.TreeModel;
import org.openelis.gwt.widget.tree.TreeServiceCallInt;
import org.openelis.gwt.widget.tree.TreeWidget;
import org.openelis.metamap.OrderMetaMap;
import org.openelis.metamap.SampleEnvironmentalMetaMap;
import org.openelis.modules.main.client.OpenELISScreenForm;
import org.openelis.modules.standardnotepicker.client.StandardNotePickerForm;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.SourcesTabEvents;
import com.google.gwt.user.client.ui.TabListener;
import com.google.gwt.user.client.ui.Widget;

public class EnvironmentalSampleLoginScreen extends OpenELISScreenForm<EnvironmentalSampleLoginForm,Query<TableDataRow<Integer>>> implements ClickListener, TabListener, TreeManager, TreeServiceCallInt {

    public enum LookupType {LOCATION_VIEW, REPORT_TO_VIEW, PROJECT_VIEW};
    
    private TreeWidget itemsTestsTree;
    private KeyListManager keyList = new KeyListManager();
    
    private Dropdown status;
    
    private SampleEnvironmentalMetaMap Meta = new SampleEnvironmentalMetaMap();
    
    AsyncCallback<EnvironmentalSampleLoginForm> checkModels = new AsyncCallback<EnvironmentalSampleLoginForm>() {
        public void onSuccess(EnvironmentalSampleLoginForm rpc) {
            
            if(rpc.analysisStatuses != null) {
                setAnalysisTypeModel(rpc.analysisStatuses);
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
    
    }
    
    public boolean canPerformCommand(Enum action, Object obj) {
        if(action instanceof LookupType)
            return true;
        else
            return super.canPerformCommand(action, obj);
    }
    
    public void performCommand(Enum action, Object obj) {
        if(action.equals(LookupType.LOCATION_VIEW))
            Window.alert("clicked location view button");
        else if(action.equals(LookupType.REPORT_TO_VIEW))
            Window.alert("clicked report to view button");
        else if(action.equals(LookupType.PROJECT_VIEW))
            Window.alert("clicked project view button");
        else
            super.performCommand(action, obj);
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
        
        //build the tree
        /*TreeDataItem row1 = itemsTestsTree.model.createTreeItem("top");
        ((Field)row1.cells[0]).setValue("0 - #69 Bottle");
        ((Field)row1.cells[1]).setValue("Water");
        TreeDataItem row2 = itemsTestsTree.model.createTreeItem("top");
        ((Field)row2.cells[0]).setValue("Lead Analysis - Logged In");
        TreeDataItem row3 = itemsTestsTree.model.createTreeItem("top");
        ((Field)row3.cells[0]).setValue("Metals - Logged In");
        row1.addItem(row2);
        row1.addItem(row3);
        itemsTestsTree.model.addRow(row1);
        
        TreeDataItem row4 = itemsTestsTree.model.createTreeItem("top");
        ((Field)row4.cells[0]).setValue("1 - #18 Bottle");
        ((Field)row4.cells[1]).setValue("Water");
        TreeDataItem row5 = itemsTestsTree.model.createTreeItem("top");
        ((Field)row5.cells[1]).setValue("Ortho Phosphate - Logged In");
        row4.addItem(row5);
        itemsTestsTree.model.addRow(row4);
        
        itemsTestsTree.model.refresh();
         */
        
        status = (Dropdown)getWidget(Meta.SAMPLE.getStatusId());
        
        //setSampleTypeModel(form.sampleTypes);
        setAnalysisTypeModel(form.analysisStatuses);
        setSampleStatusModel(form.sampleStatuses);
        setSampleContainerModel(form.sampleContainers);
        
        /*
         * Null out the rpc models so they are not sent with future rpc calls
         */
        form.analysisStatuses = null;
        form.sampleStatuses = null;
        //form.sampleTypes = null;
        form.sampleContainers = null;
                
        super.afterDraw(sucess);
        
        form.sampleItemsForm.itemsTestsTree.setValue(itemsTestsTree.model.getData());
    }

    public boolean onBeforeTabSelected(SourcesTabEvents sender, int tabIndex) {
        return true;
    }

    public void onTabSelected(SourcesTabEvents sender, int tabIndex) {
        
    }

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
        return false;
    }

    public void drop(TreeWidget widget, Widget dragWidget, TreeDataItem dropTarget, int targetRow) {}

    public void drop(TreeWidget widget, Widget dragWidget) {}

    public boolean canDrop(TreeWidget widget, Widget dragWidget, Widget dropWidget) {
        // TODO Auto-generated method stub
        return false;
    }
    
    public void setAnalysisTypeModel(TableDataModel<TableDataRow<Integer>> analysisStatusesModel) {
        ((TableDropdown)itemsTestsTree.columns.get(1).getColumnWidget("analysis")).setModel(analysisStatusesModel);
    }
    
    public void setSampleContainerModel(TableDataModel<TableDataRow<Integer>> containersModel) {
        ((TableDropdown)itemsTestsTree.columns.get(0).getColumnWidget("sampleItem")).setModel(containersModel);
    }
    
    public void setSampleStatusModel(TableDataModel<TableDataRow<Integer>> statusesModel) {
        status.setModel(statusesModel);
    }
    
    public void setSampleTypeModel(TableDataModel<TableDataRow<Integer>> typesModel) {
        ((TableDropdown)itemsTestsTree.columns.get(1).getColumnWidget("sampleItem")).setModel(typesModel);
    }

    public void getChildNodes(final TreeModel model, final int row) {
        final TreeDataItem item = model.getRow(row);
        Integer id = item.key;
        item.getItems().clear();

        window.setBusy();

        SampleTreeForm form = new SampleTreeForm();
        form.treeRow = row;
        
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
        
    }
}