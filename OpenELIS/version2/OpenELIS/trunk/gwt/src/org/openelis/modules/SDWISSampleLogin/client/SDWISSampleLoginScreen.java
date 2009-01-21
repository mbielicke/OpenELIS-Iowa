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
package org.openelis.modules.SDWISSampleLogin.client;

import org.openelis.gwt.common.data.DataModel;
import org.openelis.gwt.common.data.KeyListManager;
import org.openelis.gwt.common.data.NumberObject;
import org.openelis.gwt.common.data.TreeDataItem;
import org.openelis.gwt.screen.CommandChain;
import org.openelis.gwt.widget.ButtonPanel;
import org.openelis.gwt.widget.Dropdown;
import org.openelis.gwt.widget.tree.TreeManager;
import org.openelis.gwt.widget.tree.TreeWidget;
import org.openelis.modules.main.client.OpenELISScreenForm;

import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.SourcesTabEvents;
import com.google.gwt.user.client.ui.TabListener;
import com.google.gwt.user.client.ui.Widget;

public class SDWISSampleLoginScreen extends OpenELISScreenForm implements ClickListener, TabListener, TreeManager{
    
    private static boolean loaded = false;
    private static DataModel sampleStatusDropdown, sampleTypeDropdown,
                    sampleCategoryDropdown, leadSampleTypeDropdown;
    
    private TreeWidget itemsTestsTree;
    private KeyListManager keyList = new KeyListManager();
    
    public SDWISSampleLoginScreen() {
        super("org.openelis.modules.SDWISSampleLogin.server.SDWISSampleLoginService", !loaded);
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
        row1.get(0).setValue("0 - Bottle #26");
        row1.get(1).setValue("Water");
        TreeDataItem row2 = itemsTestsTree.model.createTreeItem("top", new NumberObject(1));
        row2.get(0).setValue("SDWA Pb - Logged In");
        row1.addItem(row2);
        TreeDataItem row3 = itemsTestsTree.model.createTreeItem("top", new NumberObject(0));
        row3.get(0).setValue("1 - Bottle #74");
        row3.get(1).setValue("Water");
        TreeDataItem row4 = itemsTestsTree.model.createTreeItem("top", new NumberObject(1));
        row4.get(0).setValue("SDWA TOC - Logged In");
        row3.addItem(row4);
        itemsTestsTree.model.addRow(row1);
        itemsTestsTree.model.addRow(row3);
        
        itemsTestsTree.model.refresh();
        
        if (sampleStatusDropdown == null) {
            sampleStatusDropdown = (DataModel)initData.get("sampleStatus");
            sampleTypeDropdown = (DataModel)initData.get("sampleTypes");
            sampleCategoryDropdown = (DataModel)initData.get("sampleCats");
            leadSampleTypeDropdown = (DataModel)initData.get("leadSampleTypes");
        }

        //sample status dropdown
        drop = (Dropdown)getWidget("status");
        drop.setModel(sampleStatusDropdown);
        
        //sample category dropdown
        drop = (Dropdown)getWidget("sampleCategory");
        drop.setModel(sampleCategoryDropdown);
        
        //sample type dropdown
        drop = (Dropdown)getWidget("sampleType");
        drop.setModel(sampleTypeDropdown);
        
        //lead sample type dropdown
        drop = (Dropdown)getWidget("leadSampleType");
        drop.setModel(leadSampleTypeDropdown);

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
