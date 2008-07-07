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
* Copyright (C) OpenELIS.  All Rights Reserved.
*/
package org.openelis.modules.storage.client;

import org.openelis.gwt.common.FormRPC;
import org.openelis.gwt.common.data.StringField;
import org.openelis.gwt.common.data.TableRow;
import org.openelis.gwt.widget.AToZPanel;
import org.openelis.gwt.widget.AToZTable;
import org.openelis.gwt.widget.AppButton;
import org.openelis.gwt.widget.ButtonPanel;
import org.openelis.gwt.widget.CollapsePanel;
import org.openelis.gwt.widget.FormInt;
import org.openelis.gwt.widget.table.EditTable;
import org.openelis.gwt.widget.table.TableWidget;
import org.openelis.metamap.StandardNoteMetaMap;
import org.openelis.metamap.StorageLocationMetaMap;
import org.openelis.modules.main.client.OpenELISScreenForm;

import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class StorageLocationScreen extends OpenELISScreenForm implements ClickListener {
	
	private TextBox nameTextbox;
	private EditTable childTable;
	
    private AppButton removeEntryButton;
   	
    private StorageLocationMetaMap StorageLocationMeta = new StorageLocationMetaMap();
    
	public StorageLocationScreen() {
		super("org.openelis.modules.storage.server.StorageLocationService",false);
	}
	
	public void onChange(Widget sender) {
        if(sender == getWidget("atozButtons")){
           String action = ((ButtonPanel)sender).buttonClicked.action;
           if(action.startsWith("query:")){
        	   getStorageLocs(action.substring(6, action.length()));      
           }
        }else{
            super.onChange(sender);
        }
    }

	
	public void onClick(Widget sender) {
		String action = ((AppButton)sender).action;
        if (action.equals("removeRow")) {
			onRemoveRowButtonClick();
		}
	}
	
	public void afterDraw(boolean success) {
		setBpanel((ButtonPanel) getWidget("buttons"));

        AToZTable atozTable = (AToZTable)getWidget("azTable");
        modelWidget.addChangeListener(atozTable);
        addChangeListener(atozTable);
        
        ((CollapsePanel)getWidget("collapsePanel")).addChangeListener(atozTable);
        
        ButtonPanel atozButtons = (ButtonPanel)getWidget("atozButtons");
        atozButtons.addChangeListener(this);
        
        removeEntryButton = (AppButton) getWidget("removeEntryButton");
        
        nameTextbox = (TextBox)getWidget(StorageLocationMeta.getName());
		childTable = ((TableWidget) getWidget("childStorageLocsTable")).controller;
		
		((ChildStorageLocsTable) childTable.manager).setStorageForm(this);

		super.afterDraw(success);
	}
	
	public void query() {
		super.query();
		
		//set focus to the name field
		nameTextbox.setFocus(true);
		
		removeEntryButton.changeState(AppButton.ButtonState.DISABLED);
	}
	
	public void add() {
    	childTable.setAutoAdd(true);
    	super.add();
    
    	//set focus to the name field
    	nameTextbox.setFocus(true);
    }

    public void update() {
    	childTable.setAutoAdd(true);
    	super.update();
    }

    public void afterUpdate(boolean success) {
    		super.afterUpdate(success);
    
    		//set focus to the name field
    		nameTextbox.setFocus(true);
    	}

    public void abort() {		
		childTable.setAutoAdd(false);
		
		super.abort();
	}
	
	public void commitAdd() {
    	childTable.setAutoAdd(false);
        super.commitAdd();
    }

    public void afterCommitUpdate(boolean success) {
		childTable.setAutoAdd(false);
		
        //we need to do this reset to get rid of the last row
        childTable.reset();
        
		super.afterCommitUpdate(success);
	}
	
	private void onRemoveRowButtonClick(){
			int selectedRow = childTable.selected;
			if (selectedRow > -1
				&& childTable.model.numRows() > 1) {
				TableRow row = childTable.model
						.getRow(selectedRow);
                childTable.model.hideRow(row);
				// delete the last row of the table because it is autoadd
				childTable.model
						.deleteRow(childTable.model.numRows() - 1);
				// reset the model
				childTable.reset();
				// need to set the deleted flag to "Y" also
				StringField deleteFlag = new StringField();
				deleteFlag.setValue("Y");

				row.addHidden("deleteFlag", deleteFlag);
			}
    }

    private void getStorageLocs(String query) {
    	if (state == FormInt.State.DISPLAY || state == FormInt.State.DEFAULT) {
    
    		FormRPC letterRPC = (FormRPC) this.forms.get("queryByLetter");
    		
    		letterRPC.setFieldValue(StorageLocationMeta.getName(), query);
    
    		commitQuery(letterRPC);
    	}
    }
}
