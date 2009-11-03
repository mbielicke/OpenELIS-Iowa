package org.openelis.modules.favorites.client;

import org.openelis.gwt.common.data.CheckField;
import org.openelis.gwt.common.data.StringField;
import org.openelis.gwt.common.data.TableRow;
import org.openelis.gwt.screen.ScreenForm;
import org.openelis.gwt.screen.ScreenTableWidget;
import org.openelis.gwt.widget.ButtonPanel;
import org.openelis.gwt.widget.PopupWindow;
import org.openelis.gwt.widget.table.TableWidget;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class FavoritesAdd extends ScreenForm {
	public PopupWindow window;
	private Label newMessage;
	//private Label message;
	public FavoritesAdd() {
        super("organizeFavoritesAdd");
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
            ScreenTableWidget table = (ScreenTableWidget) widgets.get("favoriteItemsTable");
            table = fillReportingTable(table);
            
            DeferredCommand.addCommand(new Command() {
                public void execute() {
                    bpanel = (ButtonPanel) getWidget("popupButtons");
                	//bpanel.enable("cb", true);
                	message = newMessage;

                	
                    window.setVisible(true);
                    window.setPopupPosition((Window.getClientWidth() - window.getOffsetWidth())/2,100);
                    window.size();
                }
            });
        }
	}
	
public ScreenTableWidget fillReportingTable(ScreenTableWidget tempTable){
		
		tempTable = deleteAllRows(tempTable);
		
		for(int i=0;i<15;i++){
			TableRow tempRow = new TableRow();
			CheckField tempDCheckField1 = new CheckField();
			StringField tempStringfield1 = new StringField();
	    	tempStringfield1.setValue("Format"+String.valueOf(i));
	    	
	    	tempRow.addColumn(tempDCheckField1);
	    	tempRow.addColumn(tempStringfield1);
	    	//tempRow.addColumn(tempCheckField);
	    	
	    	((TableWidget)tempTable.getWidget()).controller.addRow(tempRow);
		}
		TableRow row1 = new TableRow();
    	StringField field1 = new StringField();
    	CheckField field3 = new CheckField();
    	row1.addColumn(field3);
    	row1.addColumn(field1);
    	//row1.addColumn(field3);
    	
    	((TableWidget)tempTable.getWidget()).controller.addRow(row1);
	
		return tempTable;
	}	

	public ScreenTableWidget deleteAllRows(ScreenTableWidget tempTable){
//	remove all the rows before you add any
	while(((TableWidget)tempTable.getWidget()).controller.model.numRows()>0){
		((TableWidget)tempTable.getWidget()).controller.deleteRow(0);
	}
	
	return tempTable;
}
	
}
