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
package org.openelis.modules.PTSampleLogin.client;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.SourcesTabEvents;
import com.google.gwt.user.client.ui.TabListener;
import com.google.gwt.user.client.ui.Widget;

import org.openelis.gwt.common.Query;
import org.openelis.gwt.common.data.KeyListManager;
import org.openelis.gwt.common.data.TableDataModel;
import org.openelis.gwt.common.data.TableDataRow;
import org.openelis.gwt.common.data.TreeDataItem;
import org.openelis.gwt.screen.CommandChain;
import org.openelis.gwt.widget.ButtonPanel;
import org.openelis.gwt.widget.Dropdown;
import org.openelis.gwt.widget.tree.TreeManager;
import org.openelis.gwt.widget.tree.TreeWidget;
import org.openelis.modules.main.client.OpenELISScreenForm;

public class PTSampleLoginScreen extends OpenELISScreenForm<PTSampleLoginForm,Query<TableDataRow<Integer>>> implements ClickListener, TabListener, TreeManager{

    private TreeWidget itemsTestsTree;
    private KeyListManager keyList = new KeyListManager();
    
    public PTSampleLoginScreen() {
        super("org.openelis.modules.PTSampleLogin.server.PTSampleLoginService");
        query = new Query<TableDataRow<Integer>>();
        getScreen(new PTSampleLoginForm());
    }
    
    AsyncCallback<PTSampleLoginForm> checkModels = new AsyncCallback<PTSampleLoginForm>() {
        public void onSuccess(PTSampleLoginForm form) {
            if(form.sampleStatus != null){
                setStatusModel(form.sampleStatus);
                form.sampleStatus = null;
            }
            if(form.ptDepartmentNames != null){
                setDepartmentModel(form.ptDepartmentNames);
                form.ptDepartmentNames = null;
            }
            if(form.ptProviderNames != null) {
                setProviderModel(form.ptProviderNames);
                form.ptProviderNames = null;
            }
        }
        
        public void onFailure(Throwable caught) {
            Window.alert(caught.getMessage());
        }
    };

    public void onClick(Widget sender) {
    
    }
    
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
        
        //build the tree
        TreeDataItem row1 = itemsTestsTree.model.createTreeItem("top");
        row1.cells[0].setValue("0 - Serum");
        row1.cells[1].setValue("Left Arm");
        TreeDataItem row2 = itemsTestsTree.model.createTreeItem("top");
        row2.cells[0].setValue("Hiv - Logged In");
        row1.addItem(row2);
        //TODO itemsTestsTree.model.addRow(row1);
        
        //TODO itemsTestsTree.model.refresh();
        

        //status dropdown
        setStatusModel(form.sampleStatus);
        setProviderModel(form.ptProviderNames);
        setDepartmentModel(form.ptDepartmentNames);
        form.sampleStatus = null;
        form.ptDepartmentNames = null;
        form.ptProviderNames = null;
        
        super.afterDraw(sucess);
    }
    
    public void setStatusModel(TableDataModel<TableDataRow<String>> model) {
        ((Dropdown)getWidget("status")).setModel(model);
    }

    public void setProviderModel(TableDataModel<TableDataRow<String>> model) {
        ((Dropdown)getWidget("ptProvider")).setModel(model);
    }
    
    public void setDepartmentModel(TableDataModel<TableDataRow<String>> model) {
        ((Dropdown)getWidget("ptDepartment")).setModel(model);
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

    public boolean canEdit(TreeWidget widget, TreeDataItem set, int row, int col) {
        return false;
    }

    public boolean canOpen(TreeWidget widget, TreeDataItem addRow, int row) {
        return true;
    }

    public boolean canSelect(TreeWidget widget, TreeDataItem set, int row) {
        return false;
    }

}
