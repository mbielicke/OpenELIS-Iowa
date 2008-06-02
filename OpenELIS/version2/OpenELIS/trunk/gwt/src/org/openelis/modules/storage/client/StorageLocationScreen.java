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

		AToZPanel atozTable = (AToZPanel) getWidget("hideablePanel");
		modelWidget.addChangeListener(atozTable);
        addChangeListener(atozTable);
        
        ButtonPanel atozButtons = (ButtonPanel)getWidget("atozButtons");
        atozButtons.addChangeListener(this);
        
        removeEntryButton = (AppButton) getWidget("removeEntryButton");
        
        nameTextbox = (TextBox)getWidget("storageLocation.name");
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
    		
    		letterRPC.setFieldValue("storageLocation.name", query);
    
    		commitQuery(letterRPC);
    	}
    }
}
