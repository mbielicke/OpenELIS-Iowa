package org.openelis.client.dataEntry.screen;

import org.openelis.gwt.client.screen.ScreenForm;
import org.openelis.gwt.client.screen.ScreenTable;
import org.openelis.gwt.client.widget.ButtonPanel;
import org.openelis.gwt.client.widget.FormTable;
import org.openelis.gwt.client.widget.PopupWindow;
import org.openelis.gwt.common.CheckField;
import org.openelis.gwt.common.StringField;
import org.openelis.gwt.common.TableRow;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class OrganizeFavoritesAddForm extends ScreenForm {
	public PopupWindow window;
	private Label newMessage;
	//private Label message;
	public OrganizeFavoritesAddForm() {
        super("organizeFavoritesAdd.xml");
        rpc.action = "OrganizeFavoritesAddForm";
        getXML();
    }
	
	public void onClick(Widget sender) {
		/*if (sender == widgets.get("okButton")) {
			window.close();
		}else if (sender == widgets.get("cancelButton")) {
			window.close();
		}*/
	}
	
	public void afterSubmit(String method, boolean success) {
        if (method.equals("draw")) {      	
        	
        	window = new PopupWindow("Add Favorites");
            window.content.add(this);
            window.content.setStyleName("Content");
            window.setContentPanel(window.content);
            
            //fill the table
            ScreenTable table = (ScreenTable) widgets.get("favoriteItemsTable");
            table = fillReportingTable(table);
            
            DeferredCommand.addCommand(new Command() {
                public void execute() {
                    bpanel = (ButtonPanel) getWidget("popupButtons");
                	bpanel.enable("cb", true);
                	message = newMessage;

                	
                    window.setVisible(true);
                    window.setPopupPosition((Window.getClientWidth() - window.getOffsetWidth())/2,100);
                    window.size();
                }
            });
        }
	}
	
public ScreenTable fillReportingTable(ScreenTable tempTable){
		
		tempTable = deleteAllRows(tempTable);
		
		for(int i=0;i<15;i++){
			TableRow tempRow = new TableRow();
			CheckField tempDCheckField1 = new CheckField();
			StringField tempStringfield1 = new StringField();
	    	tempStringfield1.setValue("Format"+String.valueOf(i));
	    	
	    	tempRow.addColumn(tempDCheckField1);
	    	tempRow.addColumn(tempStringfield1);
	    	//tempRow.addColumn(tempCheckField);
	    	
	    	((FormTable)tempTable.getWidget()).controller.addRow(tempRow);
		}
		TableRow row1 = new TableRow();
    	StringField field1 = new StringField();
    	CheckField field3 = new CheckField();
    	row1.addColumn(field3);
    	row1.addColumn(field1);
    	//row1.addColumn(field3);
    	
    	((FormTable)tempTable.getWidget()).controller.addRow(row1);
	
		return tempTable;
	}	

	public ScreenTable deleteAllRows(ScreenTable tempTable){
//	remove all the rows before you add any
	while(((FormTable)tempTable.getWidget()).controller.model.numRows()>0){
		((FormTable)tempTable.getWidget()).controller.deleteRow(0);
	}
	
	return tempTable;
}
	
}
