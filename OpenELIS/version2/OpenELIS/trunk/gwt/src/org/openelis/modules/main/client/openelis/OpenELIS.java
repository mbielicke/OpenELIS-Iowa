package org.openelis.modules.main.client.openelis;

import java.util.Iterator;

import org.openelis.gwt.screen.AppScreen;
import org.openelis.gwt.screen.ScreenLabel;
import org.openelis.gwt.screen.ScreenMenuPanel;
import org.openelis.gwt.screen.ScreenMenuPopupPanel;
import org.openelis.gwt.screen.ScreenTablePanel;
import org.openelis.gwt.screen.ScreenVertical;
import org.openelis.gwt.screen.ScreenWidget;
import org.openelis.gwt.widget.HoverListener;
import org.openelis.gwt.widget.WindowBrowser;
import org.openelis.modules.main.client.service.OpenELISServiceInt;
import org.openelis.modules.main.client.service.OpenELISServiceIntAsync;

import com.google.gwt.core.client.GWT;
//import com.google.gwt.i18n.client.ConstantsWithLookup;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.ServiceDefTarget;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.DelegatingClickListenerCollection;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.MouseListener;
import com.google.gwt.user.client.ui.MouseListenerCollection;
import com.google.gwt.user.client.ui.PopupListener;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.Widget;

public class OpenELIS extends AppScreen implements PopupListener, MouseListener {
	
	//private OpenELISConstants openElisConstants = null;
	//public static ConstantsWithLookup openElisConstants = (ConstantsWithLookup) AppScreen.getWidgetMap().get("AppConstants");
	public static OpenELISServiceIntAsync screenService = (OpenELISServiceIntAsync)GWT.create(OpenELISServiceInt.class);
    public static ServiceDefTarget target = (ServiceDefTarget)screenService;
    
    public static String modules;
    public static WindowBrowser browser;
    
	public OpenELIS() {	    
        super();
        String base = GWT.getModuleBaseURL();
        base += "OpenELISServlet?service=org.openelis.modules.main.server.OpenELISScreen";
        target.setServiceEntryPoint(base);
        service = screenService;
        getXML();
    }

   public void afterDraw(boolean Success) {
        	//set the constants so we can use it later
           // setOpenElisConstants((OpenELISConstants) constants);
            
        	browser = (WindowBrowser)getWidget("browser");
        	browser.setBrowserHeight();
        	
        	//load left menu
            screenService.getMenuList(new AsyncCallback() {
                public void onSuccess(Object result) {
                    try {
                        ((ScreenVertical)widgets.get("leftMenuPanelContainer")).load((String)result);
                    } catch (Exception e) {
                        Window.alert(e.getMessage());
                    }
                }

                public void onFailure(Throwable caught) {
                    Window.alert(caught.getMessage());
                }
            });
            
         /*   //try and disable a menu element
            FlexTable sampleManagementTable = (FlexTable) getWidget("sampleManagementPanelTable");
            Label projectLabel = (Label) getWidget("projectLabel");
            Label sampleLookupLabel = (Label) getWidget("sampleLookupLabel");
            Label copyLabel = (Label) getWidget("copyLabel");
            FlexTable editTable = (FlexTable) getWidget("editPanelTable");
            
            //disable first row in sample management menu
            sampleManagementTable.getRowFormatter().addStyleName(0,"disabled");
            sampleManagementTable.getCellFormatter().removeStyleName(0,0,"topMenuPanelIconPanel");
            sampleManagementTable.getCellFormatter().addStyleName(0,0,"topMenuPanelIconPanelDisabled");
            projectLabel.addStyleName("disabled");
            
//          disable third row in sample management menu
            sampleManagementTable.getRowFormatter().addStyleName(2,"disabled");
            sampleManagementTable.getCellFormatter().removeStyleName(2,0,"topMenuPanelIconPanel");
            sampleManagementTable.getCellFormatter().addStyleName(2,0,"topMenuPanelIconPanelDisabled");
            sampleLookupLabel.addStyleName("disabled");
            
//          disable second row in edit menu
            editTable.getRowFormatter().addStyleName(1,"disabled");
            editTable.getCellFormatter().removeStyleName(1,0,"topMenuPanelIconPanel");
            editTable.getCellFormatter().addStyleName(1,0,"topMenuPanelIconPanelDisabled");
            copyLabel.addStyleName("disabled");*/
            
            //set the view top menu checkbox to checked
            CheckBox check = (CheckBox) getWidget("showLeftMenu");
            check.setChecked(true);
            
            //load top horizontal menu
          /* OpenELISService.getInstance().getHorizontalMenuList(new AsyncCallback() {
                public void onSuccess(Object result) {
                    try {
                        ((ScreenMenuBar)widgets.get("horizontalMenuBar")).load((String)result);
                    } catch (Exception e) {
                        Window.alert(e.getMessage());
                    }
                }

                public void onFailure(Throwable caught) {
                    Window.alert(caught.getMessage());
                }
            });*/
            
            browser.setBrowserHeight();
    }

    public void onClick(Widget item) {
    	WindowBrowser browser = (WindowBrowser)getWidget("browser");
    	
    	//top menu actions
    	/*if(item == widgets.get("file")){
        	//set the selected css element on the menu bar
	  ((Widget)widgets.get("file")).addStyleName("Selected");
    		//getWidget("file").removeStyleName("topMenuBarItem");
    		//getWidget("file").addStyleName("topMenuBarItemSelected");
    		
        	//open the file menu panel
        	ScreenMenuPopupPanel pn = (ScreenMenuPopupPanel) widgets.get("filePanel");        	
        	((PopupPanel)pn.popupPanel).setPopupPosition(getWidget("file").getAbsoluteLeft(),(getWidget("file").getAbsoluteTop()+getWidget("file").getOffsetHeight()));
        	((PopupPanel)pn.popupPanel).show();
        }else */
    	if(item == widgets.get("edit")){
//        	set the selected css element on the menu bar
        	((Widget)widgets.get("edit")).addStyleName("Selected");
    		//getWidget("edit").removeStyleName("topMenuBarItem");
    		//getWidget("edit").addStyleName("topMenuBarItemSelected");
    		
        	//open the file menu panel
        	ScreenMenuPopupPanel pn = (ScreenMenuPopupPanel) widgets.get("editPanel");
        	((PopupPanel)pn.popupPanel).setPopupPosition(getWidget("edit").getAbsoluteLeft(),(getWidget("edit").getAbsoluteTop()+getWidget("edit").getOffsetHeight()));
        	((PopupPanel)pn.popupPanel).show();
        }else if(item == widgets.get("sampleManagement")){
//        	set the selected css element on the menu bar
    		//getWidget("sampleManagement").removeStyleName("topMenuBarItem");
        	((Widget)widgets.get("sampleManagement")).addStyleName("Selected");
    		//getWidget("sampleManagement").addStyleName("topMenuBarItemSelected");
    		
        	//open the file menu panel
        	ScreenMenuPopupPanel pn = (ScreenMenuPopupPanel) widgets.get("sampleManagementPanel");
        	((PopupPanel)pn.popupPanel).setPopupPosition(getWidget("sampleManagement").getAbsoluteLeft(),(getWidget("sampleManagement").getAbsoluteTop()+getWidget("sampleManagement").getOffsetHeight()));
        	((PopupPanel)pn.popupPanel).show();
        }else if(item == widgets.get("analysisManagement")){
//        	set the selected css element on the menu bar
        	((Widget)widgets.get("analysisManagement")).addStyleName("Selected");
    		//getWidget("analysisManagement").removeStyleName("topMenuBarItem");
    		//getWidget("analysisManagement").addStyleName("topMenuBarItemSelected");
    		
        	//open the file menu panel
        	ScreenMenuPopupPanel pn = (ScreenMenuPopupPanel) widgets.get("analysisManagementPanel");     	
        	((PopupPanel)pn.popupPanel).setPopupPosition(getWidget("analysisManagement").getAbsoluteLeft(),(getWidget("analysisManagement").getAbsoluteTop()+getWidget("analysisManagement").getOffsetHeight()));
        	((PopupPanel)pn.popupPanel).show();
        }else if(item == widgets.get("supplyManagement")){
//        	set the selected css element on the menu bar
        	((Widget)widgets.get("supplyManagement")).addStyleName("Selected");
    		//getWidget("supplyManagement").removeStyleName("topMenuBarItem");
    		//getWidget("supplyManagement").addStyleName("topMenuBarItemSelected");
    		
        	//open the file menu panel
        	ScreenMenuPopupPanel pn = (ScreenMenuPopupPanel) widgets.get("supplyManagementPanel");
        	((PopupPanel)pn.popupPanel).setPopupPosition(getWidget("supplyManagement").getAbsoluteLeft(),(getWidget("supplyManagement").getAbsoluteTop()+getWidget("supplyManagement").getOffsetHeight()));
        	((PopupPanel)pn.popupPanel).show();
        }else if(item == widgets.get("reports")){
//        	set the selected css element on the menu bar
        	((Widget)widgets.get("reports")).addStyleName("Selected");
    		//getWidget("reports").removeStyleName("topMenuBarItem");
    		//getWidget("reports").addStyleName("topMenuBarItemSelected");
    		
        	//open the file menu panel
        	ScreenMenuPopupPanel pn = (ScreenMenuPopupPanel) widgets.get("reportsPanel");
        	((PopupPanel)pn.popupPanel).setPopupPosition(getWidget("reports").getAbsoluteLeft(),(getWidget("reports").getAbsoluteTop()+getWidget("reports").getOffsetHeight()));
        	((PopupPanel)pn.popupPanel).show();
        }else if(item == widgets.get("dataEntry")){
//        	set the selected css element on the menu bar
        	((Widget)widgets.get("dataEntry")).addStyleName("Selected");
    		//getWidget("dataEntry").removeStyleName("topMenuBarItem");
    		//getWidget("dataEntry").addStyleName("topMenuBarItemSelected");
    		
        	//open the file menu panel
        	ScreenMenuPopupPanel pn = (ScreenMenuPopupPanel) widgets.get("dataEntryPanel");
        	((PopupPanel)pn.popupPanel).setPopupPosition(getWidget("dataEntry").getAbsoluteLeft(),(getWidget("dataEntry").getAbsoluteTop()+getWidget("dataEntry").getOffsetHeight()));
        	((PopupPanel)pn.popupPanel).show();
        }else if(item == widgets.get("utilities")){
//        	set the selected css element on the menu bar
        	((Widget)widgets.get("utilities")).addStyleName("Selected");
    		//getWidget("utilities").removeStyleName("topMenuBarItem");
    		//getWidget("utilities").addStyleName("topMenuBarItemSelected");
    		
        	//open the file menu panel
        	ScreenMenuPopupPanel pn = (ScreenMenuPopupPanel) widgets.get("utilitiesPanel");
        	((PopupPanel)pn.popupPanel).setPopupPosition(getWidget("utilities").getAbsoluteLeft(),(getWidget("utilities").getAbsoluteTop()+getWidget("utilities").getOffsetHeight()));
        	((PopupPanel)pn.popupPanel).show();
        }else if(item == widgets.get("favorites")){
//        	set the selected css element on the menu bar
        	((Widget)widgets.get("favorites")).addStyleName("Selected");
    		//getWidget("favorites").removeStyleName("topMenuBarItem");
    		//getWidget("favorites").addStyleName("topMenuBarItemSelected");
    		
        	//open the file menu panel
        	ScreenMenuPopupPanel pn = (ScreenMenuPopupPanel) widgets.get("favoritesPanel");
        	((PopupPanel)pn.popupPanel).setPopupPosition(getWidget("favorites").getAbsoluteLeft(),(getWidget("favorites").getAbsoluteTop()+getWidget("favorites").getOffsetHeight()));
        	((PopupPanel)pn.popupPanel).show();
        }else if(item == widgets.get("window")){
//        	set the selected css element on the menu bar
        	((Widget)widgets.get("window")).addStyleName("Selected");
    		//getWidget("window").removeStyleName("topMenuBarItem");
    		//getWidget("window").addStyleName("topMenuBarItemSelected");
    		
        	//open the file menu panel
        	ScreenMenuPopupPanel pn = (ScreenMenuPopupPanel) widgets.get("windowPanel");
        	((PopupPanel)pn.popupPanel).setPopupPosition(getWidget("window").getAbsoluteLeft(),(getWidget("window").getAbsoluteTop()+getWidget("window").getOffsetHeight()));
        	((PopupPanel)pn.popupPanel).show();
        }else if(item == widgets.get("help")){
//        	set the selected css element on the menu bar
        	((Widget)widgets.get("help")).addStyleName("Selected");
    		//getWidget("help").removeStyleName("topMenuBarItem");
    		//getWidget("help").addStyleName("topMenuBarItemSelected");
    		
        	//open the file menu panel
        	ScreenMenuPopupPanel pn = (ScreenMenuPopupPanel) widgets.get("helpPanel");
        	((PopupPanel)pn.popupPanel).setPopupPosition(getWidget("help").getAbsoluteLeft(),(getWidget("help").getAbsoluteTop()+getWidget("help").getOffsetHeight()));
        	((PopupPanel)pn.popupPanel).show();
        }else if(item == widgets.get("showLeftMenu")) {
        	CheckBox check = (CheckBox) getWidget("showLeftMenu");
        	
        	ScreenVertical vp = (ScreenVertical) widgets.get("leftMenuPanel");
        	if(check.isChecked()){
        		//unhide the left menu
        		vp.setVisible(true);
        		browser.setBrowserHeight();
        		
        	}else{
        		//hide the left menu
        		vp.setVisible(false);
        		browser.setBrowserHeight();
        	}
        	
        }else{
        	System.out.println(widgets.get("help").toString()+" == "+item.toString());
        }
    }

    public void onMouseEnter(Widget sender) {
    	super.onMouseEnter(sender);
    	
    	//addHoverToTablePanel(sender);
    }
    
    public void onMouseLeave(Widget sender) {
    	super.onMouseLeave(sender);
    	
    	//removeHoverFromTablePanel(sender);
    }
    
    public void onMouseDown(Widget sender, int x, int y) {
    	super.onMouseDown(sender, x, y);
    }
    
    public void onMouseUp(Widget sender, int x, int y) {
    	 super.onMouseUp(sender, x, y);
    	 
    	 if(sender == widgets.get("cutRow") || sender == widgets.get("copyRow") || sender == widgets.get("pasteRow"))
        	((PopupPanel)((ScreenMenuPopupPanel) widgets.get("editPanel")).popupPanel).hide();
    	
    	else if(sender == widgets.get("projectRow") || sender == widgets.get("releaseRow") || sender == widgets.get("sampleLookupRow"))
    		((PopupPanel)((ScreenMenuPopupPanel) widgets.get("sampleManagementPanel")).popupPanel).hide();
    	
    	else if(sender == widgets.get("analyteRow") || sender == widgets.get("methodRow") || sender == widgets.get("methodPanelRow") || 
    			sender == widgets.get("qaEventsRow") || sender == widgets.get("resultsRow") || sender == widgets.get("testManagementRow") ||    
    			sender == widgets.get("testTrailerRow") || sender == widgets.get("worksheetsRow"))
    		((PopupPanel)((ScreenMenuPopupPanel) widgets.get("analysisManagementPanel")).popupPanel).hide();
    	
    	else if(sender == widgets.get("instrumentRow") || sender == widgets.get("inventoryRow") || sender == widgets.get("orderRow") || 
    			sender == widgets.get("storageRow") || sender == widgets.get("storageUnitRow"))
    		((PopupPanel)((ScreenMenuPopupPanel) widgets.get("supplyManagementPanel")).popupPanel).hide();
    	
    	else if(sender == widgets.get("fastSampleLoginRow") || sender == widgets.get("organizationRow") || sender == widgets.get("patientRow") || 
    			sender == widgets.get("personRow") || sender == widgets.get("providerRow") || sender == widgets.get("sampleLoginRow"))
    		((PopupPanel)((ScreenMenuPopupPanel) widgets.get("dataEntryPanel")).popupPanel).hide();
    		
    	else if(sender == widgets.get("auxiliaryRow") || sender == widgets.get("dictionaryRow") || sender == widgets.get("labelRow") || 
    			sender == widgets.get("referenceTableRow") || sender == widgets.get("scriptletRow") ||  sender == widgets.get("sectionRow") || 
    			sender == widgets.get("standardNoteRow") || sender == widgets.get("systemVariableRow"))
    		((PopupPanel)((ScreenMenuPopupPanel) widgets.get("utilitiesPanel")).popupPanel).hide();
    	
    	else if(sender == widgets.get("closeCurrentRow") || sender == widgets.get("closeAllRow"))
    		((PopupPanel)((ScreenMenuPopupPanel) widgets.get("windowPanel")).popupPanel).hide();
    	
    	else if(sender == widgets.get("indexRow"))
    		((PopupPanel)((ScreenMenuPopupPanel) widgets.get("helpPanel")).popupPanel).hide();
    	
    	//FIXME reports logic will come in after we sit with dari else if()

    	//HoverListener hl = new HoverListener();
       // hl.onMouseLeave(sender);
    }
    
    public void onPopupClosed(PopupPanel sender, boolean autoClosed) {

    	/*if(sender == ((ScreenMenuPopupPanel)widgets.get("filePanel")).popupPanel){
    		((Widget)widgets.get("file")).removeStyleName("Selected");
    		//getWidget("file").removeStyleName("topMenuBarItemSelected");
    		//getWidget("file").addStyleName("topMenuBarItem");
    		
    	}else */
    	if(sender == ((ScreenMenuPopupPanel)widgets.get("editPanel")).popupPanel){
    		((Widget)widgets.get("edit")).removeStyleName("Selected");
    		//getWidget("edit").removeStyleName("topMenuBarItemSelected");
    		//getWidget("edit").addStyleName("topMenuBarItem");
    		
    	}else if(sender == ((ScreenMenuPopupPanel)widgets.get("sampleManagementPanel")).popupPanel){
    		//getWidget("sampleManagement").removeStyleName("topMenuBarItemSelected");
    		((Widget)widgets.get("sampleManagement")).removeStyleName("Selected");
    		//getWidget("sampleManagement").addStyleName("topMenuBarItem");
    		
    	}else if(sender == ((ScreenMenuPopupPanel)widgets.get("analysisManagementPanel")).popupPanel){
    		((Widget)widgets.get("analysisManagement")).removeStyleName("Selected");
    		//getWidget("analysisManagement").removeStyleName("topMenuBarItemSelected");
    		//getWidget("analysisManagement").addStyleName("topMenuBarItem");
    		
    	}else if(sender == ((ScreenMenuPopupPanel)widgets.get("supplyManagementPanel")).popupPanel){
    		((Widget)widgets.get("supplyManagement")).removeStyleName("Selected");
    		//getWidget("supplyManagement").removeStyleName("topMenuBarItemSelected");
    		//getWidget("supplyManagement").addStyleName("topMenuBarItem");
    		
    	}else if(sender == ((ScreenMenuPopupPanel)widgets.get("reportsPanel")).popupPanel){
    		((Widget)widgets.get("reports")).removeStyleName("Selected");
    		//getWidget("reports").removeStyleName("topMenuBarItemSelected");
    		//getWidget("reports").addStyleName("topMenuBarItem");
    		
    	}else if(sender == ((ScreenMenuPopupPanel)widgets.get("dataEntryPanel")).popupPanel){
    		((Widget)widgets.get("dataEntry")).removeStyleName("Selected");
    		//getWidget("dataEntry").removeStyleName("topMenuBarItemSelected");
    		//getWidget("dataEntry").addStyleName("topMenuBarItem");
    		
    	}else if(sender == ((ScreenMenuPopupPanel)widgets.get("utilitiesPanel")).popupPanel){
    		((Widget)widgets.get("utilities")).removeStyleName("Selected");
    		//getWidget("utilities").removeStyleName("topMenuBarItemSelected");
    		//getWidget("utilities").addStyleName("topMenuBarItem");
    		
    	}else if(sender == ((ScreenMenuPopupPanel)widgets.get("favoritesPanel")).popupPanel){
    		((Widget)widgets.get("favorites")).removeStyleName("Selected");
    		//getWidget("favorites").removeStyleName("topMenuBarItemSelected");
    		//getWidget("favorites").addStyleName("topMenuBarItem");
    		
    	}else if(sender == ((ScreenMenuPopupPanel)widgets.get("windowPanel")).popupPanel){
    		((Widget)widgets.get("window")).removeStyleName("Selected");
    		//getWidget("window").removeStyleName("topMenuBarItemSelected");
    		//getWidget("window").addStyleName("topMenuBarItem");
    		
    	}else if(sender == ((ScreenMenuPopupPanel)widgets.get("helpPanel")).popupPanel){
    		((Widget)widgets.get("help")).removeStyleName("Selected");
    		//getWidget("help").removeStyleName("topMenuBarItemSelected");
    		//getWidget("help").addStyleName("topMenuBarItem");
    	
    	}/*else if(((sender == ((ScreenMenuPopupPanel)widgets.get("reports1SubPanel")).popupPanel) || (sender == ((ScreenMenuPopupPanel)widgets.get("reports2SubPanel")).popupPanel))){
    	
    		*/
    		//getWidget("help").removeStyleName("topMenuBarItemSelected");
    		//getWidget("help").addStyleName("topMenuBarItem");
    		
    		//need to see if there are any hover css on any row in parent panel
    		/*boolean shouldClose = true;
    		FlexTable tablePanel = (FlexTable)getWidget("reportsPanelTable");
    		for(int i=0; i<tablePanel.getRowCount();i++){
    			if(tablePanel.getRowFormatter().getStyleName(i).indexOf("Hover") > -1){
    				shouldClose = false;
    			}    			
    		}
			
    		if(shouldClose){
    			//if a child panel is autoclosed then the parent panel needs closed
    			((ScreenMenuPopupPanel)widgets.get("reportsPanel")).popupPanel.hide();
    			
    			//also need to change the top bar css since we are also closing the parent
    			getWidget("reports").removeStyleName("topMenuBarItemSelected");
        		getWidget("reports").addStyleName("topMenuBarItem");
    		}*/
    		
    	//}

    	
    }
    
    public void setStyleToAllCellsInCol(FlexTable table, int col,String style){
    	for(int i=0;i<table.getRowCount(); i++){
    		table.getCellFormatter().addStyleName(i,col, style);
    	}
    }
}
