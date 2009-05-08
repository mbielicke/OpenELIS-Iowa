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
package org.openelis.modules.clinicalSampleLogin.client;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.SourcesTabEvents;
import com.google.gwt.user.client.ui.TabListener;
import com.google.gwt.user.client.ui.Widget;

import org.openelis.gwt.common.Query;
import org.openelis.gwt.common.data.Field;
import org.openelis.gwt.common.data.KeyListManager;
import org.openelis.gwt.common.data.TableDataRow;
import org.openelis.gwt.common.data.TreeDataItem;
import org.openelis.gwt.screen.CommandChain;
import org.openelis.gwt.screen.ScreenWindow;
import org.openelis.gwt.widget.ButtonPanel;
import org.openelis.gwt.widget.tree.TreeManager;
import org.openelis.gwt.widget.tree.TreeWidget;
import org.openelis.modules.main.client.OpenELISScreenForm;
import org.openelis.modules.sampleOrganization.client.SampleOrganizationScreen;
import org.openelis.modules.sampleProject.client.SampleProjectScreen;

public class ClinicalSampleLoginScreen extends OpenELISScreenForm<ClinicalSampleLoginForm,Query<TableDataRow<Integer>>> implements ClickListener, TabListener, TreeManager{

    private TreeWidget itemsTestsTree;
    public enum LookupType {PATIENT_SEARCH, PATIENT_NAME_SEARCH, PATIENT_COMMENTS, ORGANIZATION_VIEW, PROJECT_VIEW};
    private KeyListManager keyList = new KeyListManager();
    
    public ClinicalSampleLoginScreen() {
        super("org.openelis.modules.clinicalSampleLogin.server.ClinicalSampleLoginService");
        query = new Query<TableDataRow<Integer>>();
        getScreen(new ClinicalSampleLoginForm());
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
        if(action.equals(LookupType.PATIENT_SEARCH))
            Window.alert("clicked patient search button");
        else if(action.equals(LookupType.PATIENT_NAME_SEARCH))
            Window.alert("clicked patient name search button");
        else if(action.equals(LookupType.PATIENT_COMMENTS))
            Window.alert("clicked patient comments button");
        else if(action.equals(LookupType.ORGANIZATION_VIEW))
            onOrganizationLookupClick();
        else if(action.equals(LookupType.PROJECT_VIEW))
            onProjectLookupClick();
        else
            super.performCommand(action, obj);
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
        ((Field)row1.cells[0]).setValue("0 - Serum");
        ((Field)row1.cells[1]).setValue("Left Arm");
        TreeDataItem row2 = itemsTestsTree.model.createTreeItem("top");
        ((Field)row2.cells[0]).setValue("Hiv - Logged In");
        row1.addItem(row2);
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
    
    private void onProjectLookupClick(){
        ScreenWindow modal = new ScreenWindow(null,"Sample Project","sampleProjectScreen","Loading...",true,false);
        modal.setName(consts.get("sampleProject"));
        modal.setContent(new SampleProjectScreen());
    }
    
    private void onOrganizationLookupClick(){
        ScreenWindow modal = new ScreenWindow(null,"Sample Organization","sampleOrganizationScreen","Loading...",true,false);
        modal.setName(consts.get("sampleOrganization"));
        modal.setContent(new SampleOrganizationScreen());
    }
}