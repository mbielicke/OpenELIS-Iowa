package org.openelis.modules.main.client.openelis;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.ServiceDefTarget;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.MouseListener;
import com.google.gwt.user.client.ui.PopupListener;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.Widget;

import org.openelis.gwt.screen.AppScreen;
import org.openelis.gwt.screen.ClassFactory;
import org.openelis.gwt.screen.ScreenMenuPopupPanel;
import org.openelis.gwt.screen.ScreenVertical;
import org.openelis.gwt.screen.ScreenWidget;
import org.openelis.gwt.widget.HoverListener;
import org.openelis.gwt.widget.WindowBrowser;
import org.openelis.modules.main.client.service.OpenELISServiceInt;
import org.openelis.modules.main.client.service.OpenELISServiceIntAsync;

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
    	if(item == widgets.get("application")){
        	//set the selected css element on the menu bar
	  ((Widget)widgets.get("application")).addStyleName("Selected");
    		//getWidget("file").removeStyleName("topMenuBarItem");
    		//getWidget("file").addStyleName("topMenuBarItemSelected");
    		
        	//open the file menu panel
        	ScreenMenuPopupPanel pn = (ScreenMenuPopupPanel) widgets.get("applicationPanel");        	
        	((PopupPanel)pn.popupPanel).setPopupPosition(getWidget("application").getAbsoluteLeft(),(getWidget("application").getAbsoluteTop()+getWidget("application").getOffsetHeight()));
        	((PopupPanel)pn.popupPanel).show();
        }else if(item == widgets.get("edit")){
//        	set the selected css element on the menu bar
        	((Widget)widgets.get("edit")).addStyleName("Selected");
    		//getWidget("edit").removeStyleName("topMenuBarItem");
    		//getWidget("edit").addStyleName("topMenuBarItemSelected");
    		
        	//open the edit menu panel
        	ScreenMenuPopupPanel pn = (ScreenMenuPopupPanel) widgets.get("editPanel");
        	((PopupPanel)pn.popupPanel).setPopupPosition(getWidget("edit").getAbsoluteLeft(),(getWidget("edit").getAbsoluteTop()+getWidget("edit").getOffsetHeight()));
        	((PopupPanel)pn.popupPanel).show();
        }else if(item == widgets.get("sample")){
//        	set the selected css element on the menu bar
    		//getWidget("sampleManagement").removeStyleName("topMenuBarItem");
        	((Widget)widgets.get("sample")).addStyleName("Selected");
    		//getWidget("sampleManagement").addStyleName("topMenuBarItemSelected");
    		
        	//open the sample menu panel
        	ScreenMenuPopupPanel pn = (ScreenMenuPopupPanel) widgets.get("samplePanel");
        	((PopupPanel)pn.popupPanel).setPopupPosition(getWidget("sample").getAbsoluteLeft(),(getWidget("sample").getAbsoluteTop()+getWidget("sample").getOffsetHeight()));
        	((PopupPanel)pn.popupPanel).show();
        }else if(item == widgets.get("analysis")){
//        	set the selected css element on the menu bar
        	((Widget)widgets.get("analysis")).addStyleName("Selected");
    		//getWidget("analysisManagement").removeStyleName("topMenuBarItem");
    		//getWidget("analysisManagement").addStyleName("topMenuBarItemSelected");
    		
        	//open the analysis menu panel
        	ScreenMenuPopupPanel pn = (ScreenMenuPopupPanel) widgets.get("analysisPanel");     	
        	((PopupPanel)pn.popupPanel).setPopupPosition(getWidget("analysis").getAbsoluteLeft(),(getWidget("analysis").getAbsoluteTop()+getWidget("analysis").getOffsetHeight()));
        	((PopupPanel)pn.popupPanel).show();
        }else if(item == widgets.get("inventoryOrder")){
//        	set the selected css element on the menu bar
        	((Widget)widgets.get("inventoryOrder")).addStyleName("Selected");
    		//getWidget("supplyManagement").removeStyleName("topMenuBarItem");
    		//getWidget("supplyManagement").addStyleName("topMenuBarItemSelected");
    		
        	//open the Inventory Order menu panel
        	ScreenMenuPopupPanel pn = (ScreenMenuPopupPanel) widgets.get("inventoryOrderPanel");
        	((PopupPanel)pn.popupPanel).setPopupPosition(getWidget("inventoryOrder").getAbsoluteLeft(),(getWidget("inventoryOrder").getAbsoluteTop()+getWidget("inventoryOrder").getOffsetHeight()));
        	((PopupPanel)pn.popupPanel).show();
        }else if(item == widgets.get("instrument")){
//        	set the selected css element on the menu bar
        	((Widget)widgets.get("instrument")).addStyleName("Selected");
    		//getWidget("reports").removeStyleName("topMenuBarItem");
    		//getWidget("reports").addStyleName("topMenuBarItemSelected");
    		
        	//open the file menu panel
        	ScreenMenuPopupPanel pn = (ScreenMenuPopupPanel) widgets.get("instrumentPanel");
        	((PopupPanel)pn.popupPanel).setPopupPosition(getWidget("instrument").getAbsoluteLeft(),(getWidget("instrument").getAbsoluteTop()+getWidget("instrument").getOffsetHeight()));
        	((PopupPanel)pn.popupPanel).show();
        }else if(item == widgets.get("maintenance")){
//        	set the selected css element on the menu bar
        	((Widget)widgets.get("maintenance")).addStyleName("Selected");
    		//getWidget("dataEntry").removeStyleName("topMenuBarItem");
    		//getWidget("dataEntry").addStyleName("topMenuBarItemSelected");
    		
        	//open the file menu panel
        	ScreenMenuPopupPanel pn = (ScreenMenuPopupPanel) widgets.get("maintenancePanel");
        	((PopupPanel)pn.popupPanel).setPopupPosition(getWidget("maintenance").getAbsoluteLeft(),(getWidget("maintenance").getAbsoluteTop()+getWidget("maintenance").getOffsetHeight()));
        	((PopupPanel)pn.popupPanel).show();
        }else if(item == widgets.get("report")){
//        	set the selected css element on the menu bar
        	((Widget)widgets.get("report")).addStyleName("Selected");
    		//getWidget("utilities").removeStyleName("topMenuBarItem");
    		//getWidget("utilities").addStyleName("topMenuBarItemSelected");
    		
        	//open the file menu panel
        	ScreenMenuPopupPanel pn = (ScreenMenuPopupPanel) widgets.get("reportPanel");
        	((PopupPanel)pn.popupPanel).setPopupPosition(getWidget("report").getAbsoluteLeft(),(getWidget("report").getAbsoluteTop()+getWidget("report").getOffsetHeight()));
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
        	
        }else if(item == widgets.get("preferenceRow") || /*item == widgets.get("favoritesMenuRow") ||*/ item == widgets.get("logoutRow"))
   		 	((PopupPanel)((ScreenMenuPopupPanel) widgets.get("applicationPanel")).popupPanel).hide();
   	 
    	if(item == widgets.get("cutRow") || item == widgets.get("copyRow") || item == widgets.get("pasteRow"))
    		((PopupPanel)((ScreenMenuPopupPanel) widgets.get("editPanel")).popupPanel).hide();
   	
    	else if(item == widgets.get("fullLoginRow") || item == widgets.get("quickEntryRow") || item == widgets.get("secondEntryRow") || 
   			item == widgets.get("trackingRow") || item == widgets.get("projectRow") || item == widgets.get("providerRow") || item == widgets.get("organizationRow"))
    		((PopupPanel)((ScreenMenuPopupPanel) widgets.get("samplePanel")).popupPanel).hide();
   	
    	else if(item == widgets.get("worksheetCreationRow") || item == widgets.get("worksheetCompletionRow") || item == widgets.get("addOrCancelRow") || 
   			item == widgets.get("reviewAndReleaseRow") || item == widgets.get("toDoRow") || item == widgets.get("labelForRow") ||    
   			item == widgets.get("storageRow") || item == widgets.get("QCRow"))
    		((PopupPanel)((ScreenMenuPopupPanel) widgets.get("analysisPanel")).popupPanel).hide();
   	
    	else if(item == widgets.get("orderRow") || item == widgets.get("inventoryRow"))
    		((PopupPanel)((ScreenMenuPopupPanel) widgets.get("inventoryOrderPanel")).popupPanel).hide();
   	
    	else if(item == widgets.get("instrumentRow"))
    		((PopupPanel)((ScreenMenuPopupPanel) widgets.get("instrumentPanel")).popupPanel).hide();
   		
    	else if(item == widgets.get("testRow") || item == widgets.get("methodRow") || item == widgets.get("panelRow") || 
   			item == widgets.get("QAEventRow") || item == widgets.get("labSectionRow") ||  item == widgets.get("analyteRow") || 
   			item == widgets.get("dictionaryRow") || item == widgets.get("auxiliaryPromptRow") || item == widgets.get("barcodeLabelRow") || 
   			item == widgets.get("standardNoteRow") || item == widgets.get("trailerForTestRow") || item == widgets.get("storageUnitRow") || 
   			item == widgets.get("storageLocationRow") || item == widgets.get("instrumentMaintRow") || item == widgets.get("scriptletRow") || 
   			item == widgets.get("systemVariableRow"))
    			((PopupPanel)((ScreenMenuPopupPanel) widgets.get("maintenancePanel")).popupPanel).hide();
   	
    	else if(item == widgets.get("finalReportRow") || item == widgets.get("sampleDataExportRow") || item == widgets.get("loginLabelRow"))
    		((PopupPanel)((ScreenMenuPopupPanel) widgets.get("reportPanel")).popupPanel).hide();
   	
    	else if(item == widgets.get("referenceRow")){
    		ScreenMenuPopupPanel pn = (ScreenMenuPopupPanel) widgets.get("reportReferencePanel");
    		((PopupPanel)pn.popupPanel).setPopupPosition(getWidget("reportPanelTable").getAbsoluteLeft()+getWidget("reportPanelTable").getOffsetWidth(),
   															(getWidget("referenceRow").getAbsoluteTop()));
    		((PopupPanel)pn.popupPanel).show();
   	
    	}else if(item == widgets.get("summaryRow")){
    		ScreenMenuPopupPanel pn = (ScreenMenuPopupPanel) widgets.get("reportSummaryPanel");
    		((PopupPanel)pn.popupPanel).setPopupPosition(getWidget("reportPanelTable").getAbsoluteLeft()+getWidget("reportPanelTable").getOffsetWidth(),
																(getWidget("summaryRow").getAbsoluteTop()));
    		((PopupPanel)pn.popupPanel).show();
    	}
        if(((ScreenWidget)item).getUserObject() != null){
            OpenELIS.browser.addScreen((AppScreen)ClassFactory.forName((String)((ScreenWidget)item).getUserObject()), (String)((ScreenWidget)item).getUserObject(), (String)((ScreenWidget)item).getUserObject(), "");
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
    	 
    	//FIXME reports logic will come in after we sit with dari else if()

    	HoverListener hl = new HoverListener();
        hl.onMouseLeave(sender);
        
        super.onMouseUp(sender, x, y);
    }
    
    public void onPopupClosed(PopupPanel sender, boolean autoClosed) {

    	if(sender == ((ScreenMenuPopupPanel)widgets.get("applicationPanel")).popupPanel){
    		((Widget)widgets.get("application")).removeStyleName("Selected");
    		//getWidget("file").removeStyleName("topMenuBarItemSelected");
    		//getWidget("file").addStyleName("topMenuBarItem");
    		
    	}else if(sender == ((ScreenMenuPopupPanel)widgets.get("editPanel")).popupPanel){
    		((Widget)widgets.get("edit")).removeStyleName("Selected");
    		//getWidget("edit").removeStyleName("topMenuBarItemSelected");
    		//getWidget("edit").addStyleName("topMenuBarItem");
    		
    	}else if(sender == ((ScreenMenuPopupPanel)widgets.get("samplePanel")).popupPanel){
    		//getWidget("sampleManagement").removeStyleName("topMenuBarItemSelected");
    		((Widget)widgets.get("sample")).removeStyleName("Selected");
    		//getWidget("sampleManagement").addStyleName("topMenuBarItem");
    		
    	}else if(sender == ((ScreenMenuPopupPanel)widgets.get("analysisPanel")).popupPanel){
    		((Widget)widgets.get("analysis")).removeStyleName("Selected");
    		//getWidget("analysisManagement").removeStyleName("topMenuBarItemSelected");
    		//getWidget("analysisManagement").addStyleName("topMenuBarItem");
    		
    	}else if(sender == ((ScreenMenuPopupPanel)widgets.get("inventoryOrderPanel")).popupPanel){
    		((Widget)widgets.get("inventoryOrder")).removeStyleName("Selected");
    		//getWidget("supplyManagement").removeStyleName("topMenuBarItemSelected");
    		//getWidget("supplyManagement").addStyleName("topMenuBarItem");
    		
    	}else if(sender == ((ScreenMenuPopupPanel)widgets.get("instrumentPanel")).popupPanel){
    		((Widget)widgets.get("instrument")).removeStyleName("Selected");
    		//getWidget("reports").removeStyleName("topMenuBarItemSelected");
    		//getWidget("reports").addStyleName("topMenuBarItem");
    		
    	}else if(sender == ((ScreenMenuPopupPanel)widgets.get("maintenancePanel")).popupPanel){
    		((Widget)widgets.get("maintenance")).removeStyleName("Selected");
    		//getWidget("dataEntry").removeStyleName("topMenuBarItemSelected");
    		//getWidget("dataEntry").addStyleName("topMenuBarItem");
    		
    	}else if(sender == ((ScreenMenuPopupPanel)widgets.get("reportPanel")).popupPanel){
    		((Widget)widgets.get("report")).removeStyleName("Selected");
    		//getWidget("utilities").removeStyleName("topMenuBarItemSelected");
    		//getWidget("utilities").addStyleName("topMenuBarItem");
    		
    	}
    	/*else if(((sender == ((ScreenMenuPopupPanel)widgets.get("reports1SubPanel")).popupPanel) || (sender == ((ScreenMenuPopupPanel)widgets.get("reports2SubPanel")).popupPanel))){
    	
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
