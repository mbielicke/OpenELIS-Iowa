package org.openelis.modules.analyte.client;

import org.openelis.gwt.common.FormRPC;
import org.openelis.gwt.widget.AToZPanel;
import org.openelis.gwt.widget.AppButton;
import org.openelis.gwt.widget.ButtonPanel;
import org.openelis.gwt.widget.FormInt;
import org.openelis.modules.main.client.OpenELISScreenForm;

import com.google.gwt.user.client.ui.Widget;

public class AnalyteScreen extends OpenELISScreenForm {
	
	private Widget selected;
	
	public AnalyteScreen() {
		super("org.openelis.modules.analyte.server.AnalyteService",false);
        name="Analyte";
	}
	
	 public void onChange(Widget sender) {
	        if(sender == getWidget("atozButtons")){
	           String action = ((ButtonPanel)sender).buttonClicked.action;
	           if(action.startsWith("query:")){
	        	   getStorageUnits(action.substring(6, action.length()), ((ButtonPanel)sender).buttonClicked);      
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
        
		message.setText("done");

		super.afterDraw(success);
	}
	
	
	
	private void getStorageUnits(String letter, Widget sender) {
		// we only want to allow them to select a letter if they are in display
		// mode..
		if (state == FormInt.DISPLAY || state == FormInt.DEFAULT) {

			FormRPC letterRPC = (FormRPC) this.forms.get("queryByLetter");
			
			if(letter.equals("#"))
				letterRPC.setFieldValue("storageUnit.description", "0* | 1* | 2* | 3* | 4* | 5* | 6* | 7* | 8* | 9*");
			else
				letterRPC.setFieldValue("storageUnit.description", letter.toUpperCase() + "*");

			commitQuery(letterRPC);

			setStyleNameOnButton(sender);
		}
	}
	
	protected void setStyleNameOnButton(Widget sender) {
		((AppButton)sender).changeState(AppButton.PRESSED);
		if (selected != null)
			((AppButton)selected).changeState(AppButton.UNPRESSED);
		selected = sender;
	}
}
