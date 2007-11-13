package org.openelis.client.dataEntry.screen.organization;

import org.openelis.gwt.client.screen.AppScreen;
import org.openelis.gwt.client.screen.AppScreenForm;
import org.openelis.gwt.client.screen.ScreenTablePanel;
import org.openelis.gwt.client.widget.AToZPanel;
import org.openelis.gwt.client.widget.ButtonPanel;
import org.openelis.gwt.client.widget.FormTable;
import org.openelis.gwt.common.AbstractField;
import org.openelis.gwt.common.TableModel;

import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.ConstantsWithLookup;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.ServiceDefTarget;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.xml.client.Document;

public class Organization  extends AppScreenForm {
	
	private ConstantsWithLookup openElisConstants = (ConstantsWithLookup) AppScreen.getWidgetMap().get("AppConstants");
	
	private static OrganizationScreenIntAsync screenService = (OrganizationScreenIntAsync)GWT.create(OrganizationScreenInt.class);
    private static ServiceDefTarget target = (ServiceDefTarget)screenService;
    
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
        rpc.setFieldValue("action", "contacts");
        
        fetch(id);      
    }
	
	public void add(int state) {
		// TODO Auto-generated method stub
		super.add(state);
		
	//	Window.alert("TEST");
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

}
