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

import org.openelis.gwt.common.data.DataModel;
import org.openelis.gwt.common.data.KeyListManager;
import org.openelis.gwt.common.data.NumberObject;
import org.openelis.gwt.common.data.TreeDataItem;
import org.openelis.gwt.screen.CommandChain;
import org.openelis.gwt.screen.ScreenTableWidget;
import org.openelis.gwt.widget.ButtonPanel;
import org.openelis.gwt.widget.Dropdown;
import org.openelis.gwt.widget.table.QueryTable;
import org.openelis.gwt.widget.table.TableDropdown;
import org.openelis.gwt.widget.table.TableWidget;
import org.openelis.gwt.widget.tree.TreeManager;
import org.openelis.gwt.widget.tree.TreeWidget;
import org.openelis.modules.main.client.OpenELISScreenForm;

import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.SourcesTabEvents;
import com.google.gwt.user.client.ui.TabListener;
import com.google.gwt.user.client.ui.Widget;

public class PTSampleLoginScreen extends OpenELISScreenForm implements ClickListener, TabListener, TreeManager{

    private static boolean loaded = false;
    private static DataModel statusDropdown, ptProviderDropdown,
    ptDepartmentDropdown;
    
    private TreeWidget itemsTestsTree;
    private KeyListManager keyList = new KeyListManager();
    
    public PTSampleLoginScreen() {
        super("org.openelis.modules.PTSampleLogin.server.PTSampleLoginService", !loaded);
    }

    public void onClick(Widget sender) {
    
    }
    
    public void afterDraw(boolean sucess) {
        loaded = true;
        Dropdown drop;
        ButtonPanel bpanel = (ButtonPanel)getWidget("buttons");
        
        CommandChain formChain = new CommandChain();
        formChain.addCommand(this);
        formChain.addCommand(bpanel);
        formChain.addCommand(keyList);

        itemsTestsTree = (TreeWidget)getWidget("itemsTestsTree");
        
        //build the tree
        TreeDataItem row1 = itemsTestsTree.model.createTreeItem("top", new NumberObject(0));
        row1.get(0).setValue("0 - Serum");
        row1.get(1).setValue("Left Arm");
        TreeDataItem row2 = itemsTestsTree.model.createTreeItem("top", new NumberObject(1));
        row2.get(0).setValue("Hiv - Logged In");
        row1.addItem(row2);
        //TODO itemsTestsTree.model.addRow(row1);
        
        //TODO itemsTestsTree.model.refresh();
        
        if (statusDropdown == null) {
            statusDropdown = (DataModel)initData.get("sampleStaus");
            ptProviderDropdown = (DataModel)initData.get("providerNames");
            ptDepartmentDropdown = (DataModel)initData.get("departmentNames");
        }

        //status dropdown
        drop = (Dropdown)getWidget("status");
        drop.setModel(statusDropdown);

        //pt provider dropdown
        drop = (Dropdown)getWidget("ptProvider");
        drop.setModel(ptProviderDropdown);
        
        //pt department dropdown
        drop = (Dropdown)getWidget("ptDepartment");
        drop.setModel(ptDepartmentDropdown);
        
        super.afterDraw(sucess);
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
}
