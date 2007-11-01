package org.openelis.client.dataEntry.screen;

import org.openelis.client.main.constants.OpenELISConstants;
import org.openelis.gwt.client.screen.ScreenForm;
import org.openelis.gwt.client.screen.ScreenTable;
import org.openelis.gwt.client.screen.ScreenTablePanel;
import org.openelis.gwt.client.widget.ButtonPanel;
import org.openelis.gwt.client.widget.FormTable;
import org.openelis.gwt.common.IForm;
import org.openelis.gwt.common.StringField;
import org.openelis.gwt.common.TableRow;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.xml.client.Document;

public class OrganizationForm extends ScreenForm {
	
	private OpenELISConstants openElisConstants = null;
	
	Document xml = null;
	public OrganizationForm() {
        super("organization");
        rpc.action = "OrganizationForm";
    }
	
	public void onClick(Widget sender) {
		if (sender == widgets.get("addButton")) {
			ScreenTablePanel tp = (ScreenTablePanel)widgets.get("noteFormPanel");
        	tp.getWidget().setVisible(true);
        }else if (sender == widgets.get("lookupParentOrganizationHtml")){
        	new OrganizationChooseForm();
        }else if(sender == widgets.get("openSidePanelButton")){
        	HorizontalPanel hp = (HorizontalPanel) getWidget("leftPanel");
        	if(hp.isVisible()){
        		hp.setVisible(false);
        		// HTML html = new HTML("<img src=\"Images/close_left_panel.png\">");
        		 HTML screenHtml = (HTML) getWidget("openSidePanelButton");
        		 screenHtml.setHTML("<img src=\"Images/arrow-right-unselected.png\" onmouseover=\"this.src='Images/arrow-right-selected.png';\" onmouseout=\"this.src='Images/arrow-right-unselected.png';\">");
        		 //screenHtml.initWidget(html);
        		// html.setStyleName("ScreenHTML");
        	}else{
        		hp.setVisible(true);
        		HTML screenHtml = (HTML) getWidget("openSidePanelButton");
       		 screenHtml.setHTML("<img src=\"Images/arrow-left-unselected.png\" onmouseover=\"this.src='Images/arrow-left-selected.png';\" onmouseout=\"this.src='Images/arrow-left-unselected.png';\">");
        	}
        }
	}
	
	public void afterSubmit(String method, boolean success) {
        if (method.equals("draw")) {
//        	set the constants so we can use it later
            setOpenElisConstants((OpenELISConstants) constants);
            
        	bpanel = (ButtonPanel)getWidget("buttons");
        	
        	if(constants != null)
        		message.setText(openElisConstants.loadCompleteMessage());
        	else
        		message.setText("done");
        	
        	//HorizontalPanel hp = (HorizontalPanel) getWidget("leftPanel");
        	//hp.setVisible(false);

        	//fill the organizations table
        /*	ScreenAToZPanel organizationsPanel = (ScreenAToZPanel) widgets.get("organizationsTable");
        	FormTable organizationsTable = (FormTable) ((AToZPanel)organizationsPanel.getWidget()).leftTable;
        	//set the organization table manager
        	//((OrganizationTable)((FormTable)organizationsTable.getWidget()).controller.manager).setOrganizationForm(this);
        	organizationsTable = fillOrganizationsTable(organizationsTable);     
        	
        	FormTable table = (FormTable)getWidget("organizationsTable");
        	//((OrganizationTable)table.controller.manager).setOrganizationForm(this);
        	
        	//fill the contacts table
        	ScreenTable contactsTable = (ScreenTable) widgets.get("contactsTable");
        	//set the contacts table manager
        	//((ContactTable)((FormTable)contactsTable.getWidget()).controller.manager).setOrganizationForm(this);
        	//contactsTable = fillContactsTable(contactsTable);    
        	
        	//fill the routes table
        	ScreenTable reportingTable = (ScreenTable) widgets.get("reportingTable");
        	//set the routes table manager
        	//((ContactReportingTable)((FormTable)reportingTable.getWidget()).controller.manager).setOrganizationForm(this);
        	//reportingTable = fillReportingTable(reportingTable);    
        	
        	//disable the 2 remove buttons
        	ScreenButton updateButton = (ScreenButton) widgets.get("removeContactButton");
        	((Button)updateButton.getWidget()).setEnabled(false);
        	ScreenButton createContactButton = (ScreenButton) widgets.get("removeRouteButton");
        	((Button)createContactButton.getWidget()).setEnabled(false);*/
        	
        }
        super.afterSubmit(method,success);
	}
	
    public void fetchData(Object id) {
        rpc.setFieldValue("id", (Integer)id);
        rpc.operation = IForm.DISPLAY;
        callService("fetch");
    }
	
	public void add(int state) {
		// TODO Auto-generated method stub
		super.add(state);
		
		Window.alert("TEST");
	}
	
	public void abort(int state) {
		// TODO Auto-generated method stub
		super.abort(state);
		
		
	}
	
	public void query(int state) {
		// TODO Auto-generated method stub
		super.query(state);
	}
	
	public void commit(int state) {
		// TODO Auto-generated method stub
		super.commit(state);
	}
	
	
	private FormTable fillOrganizationsTable(FormTable tempTable){
		
		for(int i=0;i<50;i++){
			TableRow tempRow = new TableRow();
			StringField tempStringfield = new StringField();
	    	tempStringfield.setValue("Organization Name " + String.valueOf(i));
	    	tempRow.addColumn(tempStringfield);
	    	
	    	tempTable.controller.addRow(tempRow);
		}
		
		return tempTable;
	}
	
	public ScreenTable fillContactsTable(ScreenTable tempTable){

		tempTable = deleteAllRows(tempTable);
		
		for(int i=0;i<10;i++){
			TableRow tempRow = new TableRow();
			StringField tempStringfield1 = new StringField();
			StringField tempStringfield2 = new StringField();
			StringField tempStringfield3 = new StringField();
			StringField tempStringfield4 = new StringField();
			StringField tempStringfield5 = new StringField();
			StringField tempStringfield6 = new StringField();
			StringField tempStringfield7 = new StringField();
			StringField tempStringfield8 = new StringField();
			StringField tempStringfield9 = new StringField();
			StringField tempStringfield10 = new StringField();
			StringField tempStringfield11 = new StringField();
			StringField tempStringfield12 = new StringField();
			tempStringfield1.setValue("LastName"+String.valueOf(i)+", FirstName" + String.valueOf(i));
			tempStringfield2.setValue("P.O. Box 55555");
			tempStringfield3.setValue("12345-"+String.valueOf(i)+ " Fake St. NE ");
			tempStringfield4.setValue("City "+String.valueOf(i));
			tempStringfield5.setValue("IA");
			tempStringfield6.setValue("88888-8888");
			tempStringfield7.setValue("");
			tempStringfield8.setValue("");
			tempStringfield9.setValue("");
			tempStringfield10.setValue("");
			tempStringfield11.setValue("");
			tempStringfield12.setValue("");
	    	
	    	tempRow.addColumn(tempStringfield1);
	    	tempRow.addColumn(tempStringfield2);
	    	tempRow.addColumn(tempStringfield3);
	    	tempRow.addColumn(tempStringfield4);
	    	tempRow.addColumn(tempStringfield5);
	    	tempRow.addColumn(tempStringfield6);
	    	tempRow.addColumn(tempStringfield7);
	    	tempRow.addColumn(tempStringfield8);
	    	tempRow.addColumn(tempStringfield9);
	    	tempRow.addColumn(tempStringfield10);
	    	tempRow.addColumn(tempStringfield11);
	    	tempRow.addColumn(tempStringfield12);
	    	
	    	((FormTable)tempTable.getWidget()).controller.addRow(tempRow);
		}
		
		TableRow row1 = new TableRow();
    	StringField field1 = new StringField();
    	StringField field2 = new StringField();
    	StringField field3 = new StringField();
    	StringField field4 = new StringField();
    	StringField field5 = new StringField();
    	StringField field6 = new StringField();
    	StringField field7 = new StringField();
    	StringField field8 = new StringField();
    	StringField field9 = new StringField();
    	StringField field10 = new StringField();
    	StringField field11 = new StringField();
    	StringField field12 = new StringField();

    	row1.addColumn(field1);
    	row1.addColumn(field2);
    	row1.addColumn(field3);
    	row1.addColumn(field4);
    	row1.addColumn(field5);
    	row1.addColumn(field6);
    	row1.addColumn(field7);
    	row1.addColumn(field8);
    	row1.addColumn(field9);
    	row1.addColumn(field10);
    	row1.addColumn(field11);
    	row1.addColumn(field12);
    	
    	((FormTable)tempTable.getWidget()).controller.addRow(row1);
	
		return tempTable;
	}
	
	public ScreenTable fillReportingTable(ScreenTable tempTable){
		
		tempTable = deleteAllRows(tempTable);
		
		for(int i=0;i<5;i++){
			TableRow tempRow = new TableRow();
			StringField tempStringfield1 = new StringField();
	    	tempStringfield1.setValue("Format"+String.valueOf(i));
	    	
	    	StringField tempStringfield2 = new StringField();
	    	tempStringfield2.setValue("Route"+String.valueOf(i));
	    	
	    	//CheckField tempCheckField = new CheckField();
	    	//tempCheckField.setValue(String.valueOf(i));
	    	
	    	tempRow.addColumn(tempStringfield1);
	    	tempRow.addColumn(tempStringfield2);
	    	//tempRow.addColumn(tempCheckField);
	    	
	    	((FormTable)tempTable.getWidget()).controller.addRow(tempRow);
		}
		TableRow row1 = new TableRow();
    	StringField field1 = new StringField();
    	StringField field2 = new StringField();
    	//CheckField field3 = new CheckField();
    	row1.addColumn(field1);
    	row1.addColumn(field2);
    	//row1.addColumn(field3);
    	
    	((FormTable)tempTable.getWidget()).controller.addRow(row1);
	
		return tempTable;
	}	
	
public ScreenTable fillReportingTable(ScreenTable tempTable,int rowNumber){
		
		tempTable = deleteAllRows(tempTable);
		
		//for(int i=0;i<5;i++){
		//HL7, PRINTER
		if(rowNumber==0 || rowNumber==4){
				TableRow tempRow1 = new TableRow();
				TableRow tempRow2 = new TableRow();
				StringField tempStringfield1 = new StringField();
				StringField tempStringfield2 = new StringField();
				StringField tempStringfield3 = new StringField();
				StringField tempStringfield4 = new StringField();
				tempStringfield1.setValue("HL7");
				tempStringfield2.setValue("route...");
				tempStringfield3.setValue("Printer");
				tempStringfield4.setValue("HL-COMP-4567N");
				
				tempRow1.addColumn(tempStringfield1);
				tempRow1.addColumn(tempStringfield2);
				
				tempRow2.addColumn(tempStringfield3);
				tempRow2.addColumn(tempStringfield4);
				
		    	((FormTable)tempTable.getWidget()).controller.addRow(tempRow1);
		    	((FormTable)tempTable.getWidget()).controller.addRow(tempRow2);
		//HL7,EXCEL	 
			}else if(rowNumber==1 || rowNumber==5){
				TableRow tempRow1 = new TableRow();
				TableRow tempRow2 = new TableRow();
				StringField tempStringfield1 = new StringField();
				StringField tempStringfield2 = new StringField();
				StringField tempStringfield3 = new StringField();
				StringField tempStringfield4 = new StringField();
				tempStringfield1.setValue("HL7");
				tempStringfield2.setValue("route...");
				tempStringfield3.setValue("Excel");
				tempStringfield4.setValue("Email Excel File");
				
				tempRow1.addColumn(tempStringfield1);
				tempRow1.addColumn(tempStringfield2);
				
				tempRow2.addColumn(tempStringfield3);
				tempRow2.addColumn(tempStringfield4);
				
		    	((FormTable)tempTable.getWidget()).controller.addRow(tempRow1);
		    	((FormTable)tempTable.getWidget()).controller.addRow(tempRow2);
		    //EXCEL,PRINTER
			}else if(rowNumber==2 || rowNumber==6){TableRow tempRow1 = new TableRow();
			TableRow tempRow2 = new TableRow();
			StringField tempStringfield1 = new StringField();
			StringField tempStringfield2 = new StringField();
			StringField tempStringfield3 = new StringField();
			StringField tempStringfield4 = new StringField();
			tempStringfield1.setValue("Excel");
			tempStringfield2.setValue("Email Excel File");
			tempStringfield3.setValue("Printer");
			tempStringfield4.setValue("HL-COMP-4567N");
			
			tempRow1.addColumn(tempStringfield1);
			tempRow1.addColumn(tempStringfield2);
			
			tempRow2.addColumn(tempStringfield3);
			tempRow2.addColumn(tempStringfield4);
			
	    	((FormTable)tempTable.getWidget()).controller.addRow(tempRow1);
	    	((FormTable)tempTable.getWidget()).controller.addRow(tempRow2);
		    //WEBADDRESS,HL7
			}else if(rowNumber==3 || rowNumber==7){TableRow tempRow1 = new TableRow();
			TableRow tempRow2 = new TableRow();
			StringField tempStringfield1 = new StringField();
			StringField tempStringfield2 = new StringField();
			StringField tempStringfield3 = new StringField();
			StringField tempStringfield4 = new StringField();
			tempStringfield1.setValue("Web");
			tempStringfield2.setValue("https://www.yourdomain.com/folder");
			tempStringfield3.setValue("HL7");
			tempStringfield4.setValue("route...");
			
			tempRow1.addColumn(tempStringfield1);
			tempRow1.addColumn(tempStringfield2);
			
			tempRow2.addColumn(tempStringfield3);
			tempRow2.addColumn(tempStringfield4);
			
	    	((FormTable)tempTable.getWidget()).controller.addRow(tempRow1);
	    	((FormTable)tempTable.getWidget()).controller.addRow(tempRow2);
		   //EXCEL
			}else if(rowNumber==8 || rowNumber==9){
			TableRow tempRow1 = new TableRow();
			StringField tempStringfield1 = new StringField();
			StringField tempStringfield2 = new StringField();
			tempStringfield1.setValue("Excel");
			tempStringfield2.setValue("Email excel file");
			
			tempRow1.addColumn(tempStringfield1);
			tempRow1.addColumn(tempStringfield2);
			
	    	((FormTable)tempTable.getWidget()).controller.addRow(tempRow1);
			}
		TableRow row1 = new TableRow();
    	StringField field1 = new StringField();
    	StringField field2 = new StringField();
    	//CheckField field3 = new CheckField();
    	row1.addColumn(field1);
    	row1.addColumn(field2);
    	//row1.addColumn(field3);
    	
    	((FormTable)tempTable.getWidget()).controller.addRow(row1);
	
		return tempTable;
	}	
	
	public ScreenTable deleteAllRows(ScreenTable tempTable){
//		remove all the rows before you add any
		while(((FormTable)tempTable.getWidget()).controller.model.numRows()>0){
			((FormTable)tempTable.getWidget()).controller.deleteRow(0);
		}
		
		return tempTable;
	}
	
	private void setOpenElisConstants(OpenELISConstants constants){
		this.openElisConstants = constants;
	}
}
