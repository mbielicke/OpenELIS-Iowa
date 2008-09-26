/**
* The contents of this file are subject to the Mozilla Public License
* Version 1.1 (the "License"); you may not use this file except in
* compliance with the License. You may obtain a copy of the License at
* http://www.mozilla.org/MPL/
* 
* Software distributed under the License is distributed on an "AS IS"
* basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
* License for the specific language governing rights and limitations under
* the License.
* 
* The Original Code is OpenELIS code.
* 
* Copyright (C) The University of Iowa.  All Rights Reserved.
*/
package org.openelis.modules.panel.client;

import org.openelis.gwt.common.data.DataModel;
import org.openelis.gwt.common.data.KeyListManager;
import org.openelis.gwt.common.data.TableRow;
import org.openelis.gwt.screen.CommandChain;
import org.openelis.gwt.widget.AToZTable;
import org.openelis.gwt.widget.ButtonPanel;
import org.openelis.gwt.widget.CollapsePanel;
import org.openelis.gwt.widget.ScrollList;
import org.openelis.gwt.widget.table.TableController;
import org.openelis.gwt.widget.table.TableManager;
import org.openelis.modules.main.client.OpenELISScreenForm;

import com.google.gwt.user.client.dnd.DropListener;
import com.google.gwt.user.client.ui.ChangeListener;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.Widget;

public class PanelScreen extends OpenELISScreenForm implements
                                                   ClickListener,
                                                   ChangeListener,
                                                   TableManager,
                                                   DropListener{
        
    private static boolean loaded = false;

    private KeyListManager keyList = new KeyListManager();
    
    public void onClick(Widget sender) {
        // TODO Auto-generated method stub

    }
    
    public PanelScreen() {
        super("org.openelis.modules.panel.server.PanelService",!loaded);
    }
    
    public void afterDraw(boolean success) {
        loaded = true;
        ButtonPanel bpanel = (ButtonPanel) getWidget("buttons");        
        AToZTable atozTable = (AToZTable) getWidget("azTable");    
        ButtonPanel atozButtons = (ButtonPanel)getWidget("atozButtons");        
        
        CommandChain chain = new CommandChain();
        chain.addCommand(this);
        chain.addCommand(atozTable);
        chain.addCommand(atozButtons);
        chain.addCommand(keyList);
        chain.addCommand(bpanel);
                
        bpanel.enableButton("delete", false);
        
        ((CollapsePanel)getWidget("collapsePanel")).addChangeListener(atozTable);        
        super.afterDraw(success);
        
        ((ScrollList)getWidget("allTests")).setDataModel((DataModel)initData.get("allTests"));
        ((ScrollList)getWidget("allTests")).scrollLoad(0);
  }

    public boolean action(int row, int col, TableController controller) {
        // TODO Auto-generated method stub
        return false;
    }

    public boolean canDelete(int row, TableController controller) {
        // TODO Auto-generated method stub
        return false;
    }

    public boolean canEdit(int row, int col, TableController controller) {
        // TODO Auto-generated method stub
        return false;
    }

    public boolean canInsert(int row, TableController controller) {
        // TODO Auto-generated method stub
        return false;
    }

    public boolean canSelect(int row, TableController controller) {
        // TODO Auto-generated method stub
        return false;
    }

    public boolean doAutoAdd(TableRow addRow, TableController controller) {
        // TODO Auto-generated method stub
        return false;
    }

    public void finishedEditing(int row, int col, TableController controller) {
        // TODO Auto-generated method stub
        
    }

    public void getNextPage(TableController controller) {
        // TODO Auto-generated method stub
        
    }

    public void getPage(int page) {
        // TODO Auto-generated method stub
        
    }

    public void getPreviousPage(TableController controller) {
        // TODO Auto-generated method stub
        
    }

    public void rowAdded(int row, TableController controller) {
        // TODO Auto-generated method stub
        
    }

    public void setModel(TableController controller, DataModel model) {
        // TODO Auto-generated method stub
        
    }

    public void setMultiple(int row, int col, TableController controller) {
        // TODO Auto-generated method stub
        
    }

    public void onDrop(Widget sender, Widget source) {
        // TODO Auto-generated method stub
        
    }

    public void onDropEnter(Widget sender, Widget source) {
        // TODO Auto-generated method stub
        
    }

    public void onDropExit(Widget sender, Widget source) {
        // TODO Auto-generated method stub
        
    }

    public void onDropOver(Widget sender, Widget source) {
        // TODO Auto-generated method stub
        
    } 
}