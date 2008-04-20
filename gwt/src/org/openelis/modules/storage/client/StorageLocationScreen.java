package org.openelis.modules.storage.client;

import org.openelis.gwt.common.FormRPC;
import org.openelis.gwt.common.data.StringField;
import org.openelis.gwt.common.data.TableRow;
import org.openelis.gwt.widget.AToZPanel;
import org.openelis.gwt.widget.AppButton;
import org.openelis.gwt.widget.ButtonPanel;
import org.openelis.gwt.widget.FormInt;
import org.openelis.gwt.widget.table.EditTable;
import org.openelis.gwt.widget.table.TableWidget;
import org.openelis.modules.main.client.OpenELISScreenForm;

import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class StorageLocationScreen extends OpenELISScreenForm implements ClickListener {
	
	private TextBox nameTextbox;
	private EditTable childTable;
	
    private AppButton removeEntryButton;
    
	private Widget selected;
	
	public StorageLocationScreen() {
		super("org.openelis.modules.storage.server.StorageLocationService",false);
        name = "Storage Location";
	}
	
	public void onChange(Widget sender) {
        if(sender == getWidget("atozButtons")){
           String action = ((ButtonPanel)sender).buttonClicked.action;
           if(action.startsWith("query:")){
        	   getStorageLocs(action.substring(6, action.length()), ((ButtonPanel)sender).buttonClicked);      
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

		bpanel = (ButtonPanel) getWidget("buttons");

		AToZPanel atozTable = (AToZPanel) getWidget("hideablePanel");
		modelWidget.addChangeListener(atozTable);
        addChangeListener(atozTable);
        
        ButtonPanel atozButtons = (ButtonPanel)getWidget("atozButtons");
        atozButtons.addChangeListener(this);
        
        removeEntryButton = (AppButton) getWidget("removeEntryButton");
        
        nameTextbox = (TextBox)getWidget("storageLocation.name");
		childTable = ((TableWidget) getWidget("childStorageLocsTable")).controller;
		
		((ChildStorageLocsTable) childTable.manager).setStorageForm(this);
		
		message.setText("Done");

		super.afterDraw(success);
	}
	
	public void add() {
		childTable.setAutoAdd(true);
		super.add();

		//set focus to the name field
		nameTextbox.setFocus(true);
		
		//ChildStorageLocsTable childTableManager = (ChildStorageLocsTable) childTable.controller.manager;
		//childTableManager.disableRows = false;
		
		//childTable.controller.addRow();
	}
	
	public void query() {
		super.query();
		
		//set focus to the name field
		nameTextbox.setFocus(true);
		
		removeEntryButton.changeState(AppButton.DISABLED);
	}
	
	public void afterUpdate(boolean success) {
		super.afterUpdate(success);

		//set focus to the name field
		nameTextbox.setFocus(true);
		
		//ChildStorageLocsTable childTableManager = (ChildStorageLocsTable) childTable.controller.manager;
		//childTableManager.disableRows = false;
//		childTable.setAutoAdd(true);
		//childTable.controller.addRow();
	}
	
	public void up() {
		childTable.setAutoAdd(true);
		super.up();
	}
	
	public void abort() {
		super.abort();
		
		childTable.setAutoAdd(false);
		
		//if add delete the last row
		//if (state == FormInt.UPDATE || state == FormInt.ADD)
		//	childTable.controller.deleteRow(childTable.controller.model.numRows() - 1);
	}
	
	private void getStorageLocs(String letter, Widget sender) {
		// we only want to allow them to select a letter if they are in display
		// mode..
		if (state == FormInt.DISPLAY || state == FormInt.DEFAULT) {

			FormRPC letterRPC = (FormRPC) this.forms.get("queryByLetter");
			
			if(letter.equals("#"))
				letterRPC.setFieldValue("storageLocation.name", "0* | 1* | 2* | 3* | 4* | 5* | 6* | 7* | 8* | 9*");
			else
				letterRPC.setFieldValue("storageLocation.name", letter.toUpperCase() + "* | "+letter.toLowerCase() + "*");

			commitQuery(letterRPC);

			setStyleNameOnButton(sender);
		}
	}
	
	public void afterCommitUpdate(boolean success) {
		//ChildStorageLocsTable childTableManager = (ChildStorageLocsTable) childTable.controller.manager;
		//childTableManager.disableRows = true;
		childTable.setAutoAdd(false);
		
		super.afterCommitUpdate(success);
	}
	
	public void commitAdd() {
		childTable.setAutoAdd(false);
        super.commitAdd();
    } 
	
	protected void setStyleNameOnButton(Widget sender) {
		((AppButton)sender).changeState(AppButton.PRESSED);
		if (selected != null)
			((AppButton)selected).changeState(AppButton.UNPRESSED);
		selected = sender;
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
}
