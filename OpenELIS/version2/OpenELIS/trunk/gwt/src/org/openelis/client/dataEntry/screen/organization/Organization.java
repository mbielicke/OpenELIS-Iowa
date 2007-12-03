package org.openelis.client.dataEntry.screen.organization;

import org.openelis.gwt.client.screen.AppScreen;
import org.openelis.gwt.client.screen.AppScreenForm;
import org.openelis.gwt.client.screen.ScreenAToZPanel;
import org.openelis.gwt.client.screen.ScreenLabel;
import org.openelis.gwt.client.screen.ScreenPagedTree;
import org.openelis.gwt.client.screen.ScreenTable;
import org.openelis.gwt.client.screen.ScreenTablePanel;
import org.openelis.gwt.client.screen.ScreenTextBox;
import org.openelis.gwt.client.widget.ButtonPanel;
import org.openelis.gwt.client.widget.FormInt;
import org.openelis.gwt.client.widget.FormTable;
import org.openelis.gwt.client.widget.pagedtree.TreeModel;
import org.openelis.gwt.common.AbstractField;
import org.openelis.gwt.common.FormRPC;
import org.openelis.gwt.common.StringField;
import org.openelis.gwt.common.TableField;
import org.openelis.gwt.common.TableModel;
import org.openelis.gwt.common.TableRow;

import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.ConstantsWithLookup;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.ServiceDefTarget;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.SourcesTabEvents;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.xml.client.Document;
import com.google.gwt.xml.client.Node;
import com.google.gwt.xml.client.NodeList;
import com.google.gwt.xml.client.XMLParser;

public class Organization  extends AppScreenForm{
	
	private ConstantsWithLookup openElisConstants = (ConstantsWithLookup) AppScreen.getWidgetMap().get("AppConstants");
	
	private static OrganizationServletIntAsync screenService = (OrganizationServletIntAsync)GWT.create(OrganizationServletInt.class);
    private static ServiceDefTarget target = (ServiceDefTarget)screenService;
    private int tabSelectedIndex = 0;
    private Widget selected;
    
	Document xml = null;
	public Organization() {
        super();
        String base = GWT.getModuleBaseURL();
        base += "OrganizationServlet";
        target.setServiceEntryPoint(base);
        service = screenService;
        formService = screenService;
        getXML();
    }
	
	public void onClick(Widget sender) {
		if (sender == widgets.get("addButton")) {
			ScreenTablePanel tp = (ScreenTablePanel)widgets.get("noteFormPanel");
        	tp.getWidget().setVisible(true);
        //}else if (sender == widgets.get("lookupParentOrganizationHtml")){
        	//new OrganizationChoose();
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
		
		if(sender == widgets.get("a")){
            getOrganizations("a", sender);
        }
        else if(sender == widgets.get("b")){
            getOrganizations("b", sender);
        }
        else if(sender == widgets.get("c")){
            getOrganizations("c", sender);
        }
        else if(sender == widgets.get("d")){
            getOrganizations("d", sender);
        }
        else if(sender == widgets.get("e")){
            getOrganizations("e", sender);
        }
        else if(sender == widgets.get("f")){
            getOrganizations("f", sender);
        }
        else if(sender == widgets.get("g")){
            getOrganizations("g", sender);
        }
        else if(sender == widgets.get("h")){
            getOrganizations("h", sender);
        }
        else if(sender == widgets.get("i")){
            getOrganizations("i", sender);
        }
        else if(sender == widgets.get("j")){
            getOrganizations("j", sender);
        }
        else if(sender == widgets.get("k")){
            getOrganizations("k", sender);
        }
        else if(sender == widgets.get("l")){
            getOrganizations("l", sender);
        }
        else if(sender == widgets.get("m")){
            getOrganizations("m", sender);
        }
        else if(sender == widgets.get("n")){
            getOrganizations("n", sender);
        }
        else if(sender == widgets.get("o")){
            getOrganizations("o", sender);
            setStyleNameOnButton(sender);
        }
        else if(sender == widgets.get("p")){
            getOrganizations("p", sender);
        }
        else if(sender == widgets.get("q")){
            getOrganizations("q", sender);
        }
        else if(sender == widgets.get("r")){
            getOrganizations("r", sender);
        }
        else if(sender == widgets.get("s")){
            getOrganizations("s", sender);
        }
        else if(sender == widgets.get("t")){
            getOrganizations("t", sender);
        }
        else if(sender == widgets.get("u")){
            getOrganizations("u", sender);
        }
        else if(sender == widgets.get("v")){
            getOrganizations("v", sender);
        }
        else if(sender == widgets.get("w")){
            getOrganizations("w", sender);
        }
        else if(sender == widgets.get("x")){
            getOrganizations("x", sender);
        }
        else if(sender == widgets.get("y")){
            getOrganizations("y", sender);
        }
        else if(sender == widgets.get("z")){
            getOrganizations("z", sender);
        }else if(sender == widgets.get("removeContactButton")){
        	FormTable orgContactsTable = (FormTable)getWidget("contactsTable");        
        	int selectedRow = orgContactsTable.controller.selected;
        	if(selectedRow > -1 && orgContactsTable.controller.model.numRows() > 1){
        		TableRow row = orgContactsTable.controller.model.getRow(selectedRow);
        		row.setShow(false);
        		//delete the last row of the table because it is autoadd
        		orgContactsTable.controller.model.deleteRow(orgContactsTable.controller.model.numRows()-1);
        		//reset the model
        		orgContactsTable.controller.reset();
        		//need to set the deleted flag to "Y" also
        		 StringField deleteFlag = new StringField();
        		 deleteFlag.setValue("Y");
        		 
    			 row.addHidden("deleteFlag", deleteFlag);
        	}
        	
        }else if(sender == ((ScreenAToZPanel)widgets.get("hideablePanel")).div){
        	DeferredCommand.addCommand(new Command() {
                public void execute() {
                	ScreenAToZPanel panel = (ScreenAToZPanel)widgets.get("hideablePanel");
                	
                	//get the selected row
                	int selectedRow = ((FormTable)getWidget("organizationsTable")).controller.selected;
//                	only need to reset the table if the user is opening the panel
                	if(panel.panelOpen())
                		((FormTable)getWidget("organizationsTable")).controller.reset();
                	//if the table had a selected row previously then reselect it
                	if(selectedRow > -1)
                		((FormTable)getWidget("organizationsTable")).controller.select(selectedRow, 0);
                }
            });
        	
        }
	}
	
	public void afterDraw(boolean success) {
        
        	bpanel = (ButtonPanel)getWidget("buttons");
        	
        	OrganizationContactsTable orgContactsTable = (OrganizationContactsTable)((FormTable)getWidget("contactsTable")).controller.manager;        
        	orgContactsTable.disableRows = true;
            
        	final ScreenPagedTree pagedTree = (ScreenPagedTree)widgets.get("notesTree");
        	pagedTree.controller.setTreeListener(this);
        	
        	Button removeContactButton = (Button) getWidget("removeContactButton");
        	removeContactButton.setEnabled(false);
        	
        	//enable the show inactive check box
        	((CheckBox)getWidget("inactive")).setEnabled(true);
        //	if(constants != null)
        //		message.setText(openElisConstants.getString("loadCompleteMessage"));
        //	else
        		message.setText("done");
        		
    		//get contacts table and set the managers form
    		FormTable contactsTable = (FormTable) getWidget("contactsTable");
    		((OrganizationContactsTable)contactsTable.controller.manager).setOrganizationForm(this);
    		contactsTable.controller.select(0, 5);
    		
//        		get contacts table and set the managers form
    		FormTable orgNameTable = (FormTable) getWidget("organizationsTable");
    		((OrganizationNameTable)orgNameTable.controller.manager).setOrganizationForm(this);

        	super.afterDraw(success);
	}
	
    public void fetchData(AbstractField id) {
    	//need to set the action field to tell the screen what to load
    	//we will load either the contacts tab or the notes tab
    	if(tabSelectedIndex == 0)
    	rpc.setFieldValue("action", "contacts");
        
        fetch(id);
        
        //FIXME load everything for now until the new framework is in place
       // if(tabSelectedIndex == 1){
	        //need to load notes tree if the notes tab is visible
	        final ScreenPagedTree pagedTree = (ScreenPagedTree)widgets.get("notesTree");
	        screenService.getNoteTreeModel(id,true,new AsyncCallback() {
	            public void onSuccess(Object result) {
	            	pagedTree.controller.setModel((TreeModel)result);
                    
	            	afterFetch(true);
	            }
	
	            public void onFailure(Throwable caught) {
	                Window.alert(caught.getMessage());
	                afterFetch(false);
	        }
	            });
        //}
    }
    
    public void afterFetch(boolean success){
        super.afterFetch(success);
        
        //need to get the org name table model
        FormTable orgNameTM = (FormTable)getWidget("organizationsTable");
        int numberOfRows = orgNameTM.controller.model.numRows();
        int rowSelected = orgNameTM.controller.selected;
        //need to set the next and previous buttons accordingly
        if(numberOfRows > 1 && rowSelected < numberOfRows-1)
        	bpanel.enable("n", true);
        else
        	bpanel.enable("n", false);
        
        if(numberOfRows > 1 && rowSelected > 0)
        	bpanel.enable("p", true);
        else
        	bpanel.enable("p", false);
    }
	
	private void getOrganizations(String letter, Widget sender){
		//we only want to allow them to select a letter if they are in display mode..
		if(bpanel.getState() == FormInt.DISPLAY){
			final FormTable orgTable = (FormTable) getWidget("organizationsTable");
			
			//aToZWidget.leftTable.controller.view
			FormRPC letterRPC = (FormRPC)this.forms.get("queryByLetter");
			
	        screenService.getOrganizationByLetter(letter,orgTable.controller.model, letterRPC, new AsyncCallback() {
	                                                            public void onSuccess(Object result) {
	                                                            	orgTable.controller.setModel((TableModel)result);
	                                                           	
	                                                            	//select the first row of the org names table...
	                                                                if(((TableModel)result).numRows() > 0)
	                                                                	orgTable.controller.select( 0, 0);
	                                                                
	                                                                //enable the next button
	                                                                if(((TableModel)result).numRows() > 1)
	                                                                	bpanel.enable("n", true);     
	                                                            }
	
	                                                            public void onFailure(Throwable caught) {
	                                                                Window.alert(caught.getMessage());
	                                                            }
	                                                        });
	        setStyleNameOnButton(sender);
		}
	}
	

	public void onTreeItemStateChanged(TreeItem item) {
		 final TreeItem currItem = item;
         if(currItem.getChildCount() > 0){
           if(currItem.getChild(0).getText().equals("dummy"))               
             currItem.removeItems();
           else return;
         }        
         
         if(currItem.getUserObject()!=null){ 
             screenService.getNoteTreeSecondLevelXml((String)currItem.getUserObject(),false,new AsyncCallback() {
                 
                 public void onSuccess(Object result) {
                   
                     try {                          
                         Document doc = XMLParser.parse((String)result);                   
                         Node node = doc.getDocumentElement();
                         
                         NodeList items = node.getChildNodes();
                         for (int i = 0; i < items.getLength(); i++) {
                            if (items.item(i).getNodeType() == Node.ELEMENT_NODE) {
                              TreeItem childitem = createTreeItem(items.item(i));                                                  
                              currItem.addItem(childitem);
                            }
                            
                         }
                           
                                                                                   
                     } catch (Exception e) {
                         Window.alert(e.getMessage());
                     }
                 }

                 public void onFailure(Throwable caught) {
                     Window.alert(caught.getMessage());
                 }
             });
          }  
               
		//super.onTreeItemStateChanged(item);
	}
	
	private TreeItem createTreeItem(Node node){
	       
        String itemText = null;
        Object userObject = null;
        ScreenLabel label = null;
        if (node.getAttributes().getNamedItem("text") != null) {
            itemText = node.getAttributes().getNamedItem("text").getNodeValue();
        }
        if (node.getAttributes().getNamedItem("value") != null) {
            userObject = node.getAttributes().getNamedItem("value").getNodeValue();
        }
        
        TreeItem item = null;
        if(itemText!=null){
          label = new ScreenLabel(itemText,userObject)  ;
        } 
        
        if(label!=null){
            //initWidget(label);
            item = new TreeItem(label);
           // item.setText(((Label)label.getWidget()).getText());
        }
          item.setUserObject(userObject);
          
        NodeList items = node.getChildNodes();
        for (int i = 0; i < items.getLength(); i++) {
            if (items.item(i).getNodeType() == Node.ELEMENT_NODE) {
                item.addItem(createTreeItem(items.item(i)));
            }
        }
        return item;
    } 

	public void onTabSelected(SourcesTabEvents sources, int index) {
		tabSelectedIndex = index;
		//we need to do a org contacts table reset so that it will always show the data
		if(index == 0 && bpanel.getState() == FormInt.DISPLAY){
			FormTable contacts = (FormTable)getWidget("contactsTable");
			contacts.controller.model.deleteRow(contacts.controller.model.numRows()-1);
			contacts.controller.reset();
		}
		super.onTabSelected(sources, index);
	}
	
	//button panel action methods
	public void add(int state) {
		super.add(state);
		ScreenTextBox orgId = (ScreenTextBox) widgets.get("orgId");
		orgId.enable(false);
		
		OrganizationContactsTable orgContactsTable = (OrganizationContactsTable)((FormTable)getWidget("contactsTable")).controller.manager;        
    	orgContactsTable.disableRows = false;
    	
    	//unselect the row from the table
    	((FormTable)getWidget("organizationsTable")).controller.unselect(-1);
    	
    	Button removeContactButton = (Button) getWidget("removeContactButton");
    	removeContactButton.setEnabled(true);
    	
		FormTable contactsTable = (FormTable) getWidget("contactsTable");
	
		contactsTable.controller.select(0, 5);
	}
	
	public void abort(int state) {
		FormTable orgContacts = (FormTable)getWidget("contactsTable");
		//need to remove the extra table row on update
		if(bpanel.getState() == FormInt.UPDATE)
			orgContacts.controller.model.deleteRow(orgContacts.controller.model.numRows()-1);
		
		super.abort(state);	
		
		OrganizationContactsTable orgContactsTable = (OrganizationContactsTable)orgContacts.controller.manager;        
    	orgContactsTable.disableRows = true;
    	orgContacts.controller.unselect(-1);
    	
    	Button removeContactButton = (Button) getWidget("removeContactButton");
    	removeContactButton.setEnabled(false);
    	
    	 //need to get the org name table model
        FormTable orgNameTM = (FormTable)getWidget("organizationsTable");
        int numberOfRows = orgNameTM.controller.model.numRows();
        int totalPages = orgNameTM.controller.model.totalPages;
        int rowSelected = orgNameTM.controller.selected;
        int currentPage = orgNameTM.controller.model.pageIndex;

        //set the next previous buttons if needed
        if(numberOfRows > 1 && rowSelected != -1 && (rowSelected < numberOfRows-1 || currentPage < totalPages-1))
        	bpanel.enable("n", true);
        else
        	bpanel.enable("n", false);
        
        if(numberOfRows > 1 && rowSelected > 0)
        	bpanel.enable("p", true);
        else
        	bpanel.enable("p", false);
        
    	//set the update button if needed
    	if(rowSelected != -1)
    		bpanel.enable("u",true);	
	}

	public void up(int state) {	
		super.up(state);
	}
	
	public void afterUpdate(boolean success) {
		super.afterUpdate(success);
		
		ScreenTextBox orgId = (ScreenTextBox) widgets.get("orgId");
		orgId.enable(false);
		
		OrganizationContactsTable orgContactsTable = (OrganizationContactsTable)((FormTable)getWidget("contactsTable")).controller.manager;        
    	orgContactsTable.disableRows = false;
    	
    	Button removeContactButton = (Button) getWidget("removeContactButton");
    	removeContactButton.setEnabled(true);
	}
	
	public void next(int state) {
		Window.alert("next");
		super.next(state);
	}
	
	public void prev(int state) {
		Window.alert("prev");
		super.prev(state);
	}
	
	public void query(int state) {
    	
		super.query(state);
	}
	
	public void afterCommitQuery(Object field, boolean success) {
		super.afterCommitQuery(field, success);
		//set the table model
		FormTable orgsTable = (FormTable) getWidget("organizationsTable");
		TableModel model = (TableModel)((TableField)field).getValue();
        orgsTable.controller.setModel(model);
        
        //select the first row of the org names table...
        if(orgsTable.controller.model.numRows() > 0)
        	orgsTable.controller.select( 0, 0);
        
        //enable the next button
        if(model.numRows() > 1)
        	bpanel.enable("n", true);     
        
    	if(selected != null){
    		selected.removeStyleName("current");
    		selected = null;
    	}
	}
	
	public void afterCommitAdd(boolean success) {
		Button removeContactButton = (Button) getWidget("removeContactButton");
    	removeContactButton.setEnabled(true);
    	
		super.afterCommitAdd(success);
	}
	
	public void afterCommitUpdate(boolean success) {
		
		OrganizationContactsTable orgContactsTable = (OrganizationContactsTable)((FormTable)getWidget("contactsTable")).controller.manager;        
    	orgContactsTable.disableRows = true;
    	
    	Button removeContactButton = (Button) getWidget("removeContactButton");
    	removeContactButton.setEnabled(false);
    	
		super.afterCommitUpdate(success);
	}

	public void afterDelete(boolean success) {
		// TODO Auto-generated method stub
		super.afterDelete(success);
	}
	
	public void commit(int state) {
		if(state == FormInt.QUERY){
			((FormTable)((ScreenTable)((ScreenTable)widgets.get("contactsTable")).getQueryWidget()).getWidget()).controller.unselect(-1);
		}else{
			((FormTable)getWidget("contactsTable")).controller.unselect(-1);
		}
		super.commit(state);
	}
	
	protected Widget setStyleNameOnButton(Widget sender){
		sender.addStyleName("current");
        if(selected != null)
            selected.removeStyleName("current");
        selected = sender;
        return sender;
	}
}
