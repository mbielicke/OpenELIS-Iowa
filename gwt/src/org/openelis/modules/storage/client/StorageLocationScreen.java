package org.openelis.modules.storage.client;

import org.openelis.gwt.common.FormRPC;
import org.openelis.gwt.common.data.BooleanObject;
import org.openelis.gwt.common.data.DataObject;
import org.openelis.gwt.common.data.NumberObject;
import org.openelis.gwt.common.data.StringObject;
import org.openelis.gwt.screen.ScreenVertical;
import org.openelis.gwt.widget.AToZPanel;
import org.openelis.gwt.widget.AppButton;
import org.openelis.gwt.widget.ButtonPanel;
import org.openelis.gwt.widget.FormInt;
import org.openelis.gwt.widget.table.TableWidget;
import org.openelis.modules.main.client.OpenELISScreenForm;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class StorageLocationScreen extends OpenELISScreenForm {
	
	private TextBox nameTextbox;
	private TableWidget childTable;
	
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
	
	public void afterDraw(boolean success) {

		bpanel = (ButtonPanel) getWidget("buttons");

		AToZPanel atozTable = (AToZPanel) getWidget("hideablePanel");
		modelWidget.addChangeListener(atozTable);
        addChangeListener(atozTable);
        
        ButtonPanel atozButtons = (ButtonPanel)getWidget("atozButtons");
        atozButtons.addChangeListener(this);
        
        nameTextbox = (TextBox)getWidget("storageLocation.name");
		childTable = (TableWidget) getWidget("childStorageLocsTable");
		
		message.setText("done");
		
		((ChildStorageLocsTable) childTable.controller.manager).setStorageForm(this);

		super.afterDraw(success);
	}
	
	public void add() {
		super.add();

		//set focus to the name field
		nameTextbox.setFocus(true);
		
		ChildStorageLocsTable childTableManager = (ChildStorageLocsTable) childTable.controller.manager;
		childTableManager.disableRows = false;
		childTable.controller.setAutoAdd(true);
		childTable.controller.addRow();
	}
	
	public void query() {
		super.query();
		
		//set focus to the name field
		nameTextbox.setFocus(true);
	}
	
	public void afterUpdate(boolean success) {
		super.afterUpdate(success);

		//set focus to the name field
		nameTextbox.setFocus(true);
		
		ChildStorageLocsTable childTableManager = (ChildStorageLocsTable) childTable.controller.manager;
		childTableManager.disableRows = false;
		childTable.controller.setAutoAdd(true);
		childTable.controller.addRow();
	}
	
	public void abort() {
		super.abort();
		
		childTable.controller.setAutoAdd(false);
		
		//if add delete the last row
		if (state == FormInt.UPDATE || state == FormInt.ADD)
			childTable.controller.deleteRow(childTable.controller.model.numRows() - 1);
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
		ChildStorageLocsTable childTableManager = (ChildStorageLocsTable) childTable.controller.manager;
		childTableManager.disableRows = true;
		childTable.controller.setAutoAdd(false);
		
		super.afterCommitUpdate(success);
	}
	
	protected void setStyleNameOnButton(Widget sender) {
		((AppButton)sender).changeState(AppButton.PRESSED);
		if (selected != null)
			((AppButton)selected).changeState(AppButton.UNPRESSED);
		selected = sender;
	}
	
	protected boolean validate() {
	/*	StringObject nameObj = new StringObject();
		nameObj.setValue(nameTextbox.getText());
        
       // prepare the argument list for the getObject function
        DataObject[] args = new DataObject[] {nameObj}; 
        boolean unique;
       screenService.getObject("validateUniqueName", args, new AsyncCallback(){
          public void onSuccess(Object result){    
        	  Boolean boolResult = (Boolean)((BooleanObject)result).getValue();
        	  unique = boolResult.booleanValue();
             
          }
          
          public void onFailure(Throwable caught){
              Window.alert(caught.getMessage());
          }
      }); */
		return super.validate();
	}
}
