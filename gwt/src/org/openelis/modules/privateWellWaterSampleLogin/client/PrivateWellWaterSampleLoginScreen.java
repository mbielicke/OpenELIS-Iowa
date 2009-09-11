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
package org.openelis.modules.privateWellWaterSampleLogin.client;

import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.SourcesTabEvents;
import com.google.gwt.user.client.ui.TabListener;
import com.google.gwt.user.client.ui.Widget;

import org.openelis.gwt.common.data.deprecated.KeyListManager;
import org.openelis.gwt.common.data.deprecated.TableDataRow;
import org.openelis.gwt.common.data.deprecated.TreeDataItem;
import org.openelis.gwt.common.deprecated.Query;
import org.openelis.gwt.screen.deprecated.CommandChain;
import org.openelis.gwt.widget.deprecated.ButtonPanel;
import org.openelis.gwt.widget.tree.deprecated.TreeManager;
import org.openelis.gwt.widget.tree.deprecated.TreeWidget;
import org.openelis.modules.main.client.OpenELISScreenForm;

public class PrivateWellWaterSampleLoginScreen extends OpenELISScreenForm<PrivateWellWaterSampleLoginForm,Query<TableDataRow<Integer>>> implements ClickListener, TabListener, TreeManager{
    
    private TreeWidget itemsTestsTree;
    private KeyListManager keyList = new KeyListManager();
    
    public PrivateWellWaterSampleLoginScreen() {
        super("org.openelis.modules.privateWellWaterSampleLogin.server.PrivateWellWaterSampleLoginService");
        query = new Query<TableDataRow<Integer>>();
        getScreen(new PrivateWellWaterSampleLoginForm());
    }

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
        row1.cells[0].setValue("0 - Private Kit");
        row1.cells[1].setValue("Well");
        TreeDataItem row2 = itemsTestsTree.model.createTreeItem("top");
        row2.cells[0].setValue("Total Coliform - Logged In");
        TreeDataItem row3 = itemsTestsTree.model.createTreeItem("top");
        row3.cells[0].setValue("Nitrate - Logged In");
        row1.addItem(row2);
        row1.addItem(row3);
        itemsTestsTree.model.addRow(row1);
        
        itemsTestsTree.model.refresh();
        
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
