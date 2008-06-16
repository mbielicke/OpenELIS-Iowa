package org.openelis.modules.analyte.client;

import org.openelis.gwt.common.FormRPC;
import org.openelis.gwt.widget.AToZPanel;
import org.openelis.gwt.widget.ButtonPanel;
import org.openelis.gwt.widget.FormInt;
import org.openelis.modules.main.client.OpenELISScreenForm;
import org.openelis.newmeta.AnalyteMetaMap;

import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class AnalyteScreen extends OpenELISScreenForm {
	
	private TextBox nameTextBox;
    private static final AnalyteMetaMap Meta = new AnalyteMetaMap();

	public AnalyteScreen() {
		super("org.openelis.modules.analyte.server.AnalyteService",false);
	}
	
	 public void onChange(Widget sender) {
	        if(sender == getWidget("atozButtons")){
	           String action = ((ButtonPanel)sender).buttonClicked.action;
	           if(action.startsWith("query:")){
	        	   getAnalytes(action.substring(6, action.length()));      
	           }
	        }else{
	            super.onChange(sender);
	        }
	    }
	
	public void afterDraw(boolean success) {
	
		setBpanel((ButtonPanel) getWidget("buttons"));

		AToZPanel atozTable = (AToZPanel) getWidget("hideablePanel");
		modelWidget.addChangeListener(atozTable);
        addChangeListener(atozTable);
        
        ButtonPanel atozButtons = (ButtonPanel)getWidget("atozButtons");
        atozButtons.addChangeListener(this);
        
        nameTextBox = (TextBox) getWidget(Meta.getName());

		super.afterDraw(success);
	}
	
	public void add() {
		super.add();

		//set focus to the name field
		nameTextBox.setFocus(true);
	}
	
	public void query() {
		super.query();
		
		//set focus to the name field
		nameTextBox.setFocus(true);
	}
	
	public void afterUpdate(boolean success) {
		super.afterUpdate(success);

		//set focus to the name field
		nameTextBox.setFocus(true);
	}
	
	private void getAnalytes(String query) {
		if (state == FormInt.State.DISPLAY || state == FormInt.State.DEFAULT) {

			FormRPC letterRPC = (FormRPC) this.forms.get("queryByLetter");

			letterRPC.setFieldValue(Meta.getName(), query);

			commitQuery(letterRPC);
		}
	}
}
