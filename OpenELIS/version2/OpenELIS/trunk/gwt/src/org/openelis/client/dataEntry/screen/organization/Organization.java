package org.openelis.client.dataEntry.screen.organization;

import org.openelis.gwt.client.screen.AppScreen;
import org.openelis.gwt.client.screen.AppScreenForm;
import org.openelis.gwt.client.screen.ScreenLabel;
import org.openelis.gwt.client.screen.ScreenPagedTree;
import org.openelis.gwt.client.screen.ScreenTablePanel;
import org.openelis.gwt.client.screen.ScreenTextBox;
import org.openelis.gwt.client.widget.AToZPanel;
import org.openelis.gwt.client.widget.ButtonPanel;
import org.openelis.gwt.client.widget.FormInt;
import org.openelis.gwt.client.widget.FormTable;
import org.openelis.gwt.client.widget.pagedtree.TreeModel;
import org.openelis.gwt.common.AbstractField;
import org.openelis.gwt.common.TableModel;

import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.ConstantsWithLookup;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.ServiceDefTarget;
import com.google.gwt.user.client.ui.DeckPanel;
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
	
	private static OrganizationScreenIntAsync screenService = (OrganizationScreenIntAsync)GWT.create(OrganizationScreenInt.class);
    private static ServiceDefTarget target = (ServiceDefTarget)screenService;
    private int tabSelectedIndex = 0;
    
	Document xml = null;
	public Organization() {
        super();
        String base = GWT.getModuleBaseURL();
        base += "OrganizationScreen";
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
            getOrganizations("a");
        }
        else if(sender == widgets.get("b")){
            getOrganizations("b");
        }
        else if(sender == widgets.get("c")){
            getOrganizations("c");
        }
        else if(sender == widgets.get("d")){
            getOrganizations("d");
        }
        else if(sender == widgets.get("e")){
            getOrganizations("e");
        }
        else if(sender == widgets.get("f")){
            getOrganizations("f");
        }
        else if(sender == widgets.get("g")){
            getOrganizations("g");
        }
        else if(sender == widgets.get("h")){
            getOrganizations("h");
        }
        else if(sender == widgets.get("i")){
            getOrganizations("i");
        }
        else if(sender == widgets.get("j")){
            getOrganizations("j");
        }
        else if(sender == widgets.get("k")){
            getOrganizations("k");
        }
        else if(sender == widgets.get("l")){
            getOrganizations("l");
        }
        else if(sender == widgets.get("m")){
            getOrganizations("m");
        }
        else if(sender == widgets.get("n")){
            getOrganizations("n");
        }
        else if(sender == widgets.get("o")){
            getOrganizations("o");
        }
        else if(sender == widgets.get("p")){
            getOrganizations("p");
        }
        else if(sender == widgets.get("q")){
            getOrganizations("q");
        }
        else if(sender == widgets.get("r")){
            getOrganizations("r");
        }
        else if(sender == widgets.get("s")){
            getOrganizations("s");
        }
        else if(sender == widgets.get("t")){
            getOrganizations("t");
        }
        else if(sender == widgets.get("u")){
            getOrganizations("u");
        }
        else if(sender == widgets.get("v")){
            getOrganizations("v");
        }
        else if(sender == widgets.get("w")){
            getOrganizations("w");
        }
        else if(sender == widgets.get("x")){
            getOrganizations("x");
        }
        else if(sender == widgets.get("y")){
            getOrganizations("y");
        }
        else if(sender == widgets.get("z")){
            getOrganizations("z");
        }
	}
	
	public void afterDraw(boolean success) {
            
        	bpanel = (ButtonPanel)getWidget("buttons");
        	
        	OrganizationContactsTable orgContactsTable = (OrganizationContactsTable)((FormTable)getWidget("contactsTable")).controller.manager;        
        	orgContactsTable.disableRows = true;
            
        	final ScreenPagedTree pagedTree = (ScreenPagedTree)widgets.get("notesTree");
        	pagedTree.controller.setTreeListener(this);
        	
        //	if(constants != null)
        //		message.setText(openElisConstants.getString("loadCompleteMessage"));
        //	else
        		message.setText("done");
        		
        		//get contacts table and set the managers form
        		FormTable contactsTable = (FormTable) getWidget("contactsTable");
        		((OrganizationContactsTable)contactsTable.controller.manager).setOrganizationForm(this);
        		
        		final AToZPanel aToZWidget = (AToZPanel) getWidget("organizationsTable");
        		
        		((OrganizationNameTable)aToZWidget.leftTable.controller.manager).setOrganizationForm(this);
        		
        		screenService.getInitialModel(aToZWidget.leftTable.controller.model, new AsyncCallback(){
        	           public void onSuccess(Object result){
        	        	   aToZWidget.leftTable.controller.setModel((TableModel)result);
        	        	   aToZWidget.leftTable.controller.select(0, 0);
        	        	  // aToZWidget.leftTable.controller.unselect(0);
        	              // load();
        	               afterFetch(true);
        	           }
        	           public void onFailure(Throwable caught){
        	               Window.alert(caught.getMessage());
        	               afterFetch(false);
        	           }
        	        });
        		
        		
        		//ScreenAToZPanel aToZ = (ScreenAToZPanel) widgets.get("organizationsTable");
        		//AToZPanel aToZWidget = (AToZPanel) getWidget("organizationsTable");
        		//aToZWidget.leftTable.controller.setModel(getInitialModel(aToZWidget.leftTable.controller.model));
        	
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
        	super.afterDraw(success);
	}
	
    public void fetchData(AbstractField id) {
    	//need to set the action field to tell the screen what to load
    	//we will load either the contacts tab or the notes tab
    	if(tabSelectedIndex == 0)
    	rpc.setFieldValue("action", "contacts");
        
        fetch(id);
        
        if(tabSelectedIndex == 1){
	        //need to load notes tree if the notes tab is visible
	        final ScreenPagedTree pagedTree = (ScreenPagedTree)widgets.get("notesTree");
	        screenService.getNoteTreeModel(id,true,new AsyncCallback() {
	            public void onSuccess(Object result) {
	            	pagedTree.controller.setModel((TreeModel)result);
	            }
	
	            public void onFailure(Throwable caught) {
	                Window.alert(caught.getMessage());
	        }
	            });
        }
    }
	
	private void getOrganizations(String letter){
		final AToZPanel aToZWidget = (AToZPanel) getWidget("organizationsTable");
		
		//aToZWidget.leftTable.controller.view
		
        screenService.getOrganizationByLetter(letter,aToZWidget.leftTable.controller.model, new AsyncCallback() {
                                                            public void onSuccess(Object result) {
                                                            	aToZWidget.leftTable.controller.setModel((TableModel)result);
                                                            	aToZWidget.leftTable.controller.reset();
                                                            }

                                                            public void onFailure(Throwable caught) {
                                                                Window.alert(caught.getMessage());
                                                            }
                                                        });
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
		
		super.onTabSelected(sources, index);
	}
	
	//button panel action methods
	public void add(int state) {
		super.add(state);
		ScreenTextBox orgId = (ScreenTextBox) widgets.get("orgId");
		orgId.enable(false);
		
		OrganizationContactsTable orgContactsTable = (OrganizationContactsTable)((FormTable)getWidget("contactsTable")).controller.manager;        
    	orgContactsTable.disableRows = false;
	}
	
	public void abort(int state) {
		if(state == FormInt.QUERY){
		//	((DeckPanel)getWidget("formDeck")).showWidget(0);
		
		}
		OrganizationContactsTable orgContactsTable = (OrganizationContactsTable)((FormTable)getWidget("contactsTable")).controller.manager;        
    	orgContactsTable.disableRows = true;
    	
		super.abort(state);		
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
	}
	
	public void next(int state) {
		// TODO Auto-generated method stub
		super.next(state);
	}
	
	public void prev(int state) {
		// TODO Auto-generated method stub
		super.prev(state);
	}
	
	public void query(int state) {
		//((DeckPanel)getWidget("formDeck")).showWidget(1);
		super.query(state);
	}
	
	public void afterCommitQuery(boolean success) {
		//((DeckPanel)getWidget("formDeck")).showWidget(0);
		super.afterCommitQuery(success);
	}
	
	public void afterCommitAdd(boolean success) {
		// TODO Auto-generated method stub
		super.afterCommitAdd(success);
	}
	
	public void afterCommitUpdate(boolean success) {
		
		OrganizationContactsTable orgContactsTable = (OrganizationContactsTable)((FormTable)getWidget("contactsTable")).controller.manager;        
    	orgContactsTable.disableRows = true;
    	
		super.afterCommitUpdate(success);
	}

	public void afterDelete(boolean success) {
		// TODO Auto-generated method stub
		super.afterDelete(success);
	}
	
	public void commit(int state) {
    	((FormTable)getWidget("contactsTable")).controller.unselect(-1);
		super.commit(state);
	}
}
