package org.openelis.client.main.screen.openelis;

import org.openelis.client.dataEntry.screen.organization.Organization;
import org.openelis.client.dataEntry.screen.organizeFavorites.OrganizeFavorites;
import org.openelis.client.main.service.OpenELISService;
import org.openelis.gwt.client.screen.AppScreen;
import org.openelis.gwt.client.screen.ScreenLabel;
import org.openelis.gwt.client.screen.ScreenMenuPanel;
import org.openelis.gwt.client.screen.ScreenMenuPopupPanel;
import org.openelis.gwt.client.screen.ScreenTablePanel;
import org.openelis.gwt.client.screen.ScreenVertical;
import org.openelis.gwt.client.screen.ScreenWidget;
import org.openelis.gwt.client.widget.WindowBrowser;

import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.ConstantsWithLookup;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.ServiceDefTarget;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupListener;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.Widget;

public class OpenELIS extends AppScreen implements PopupListener {
	
	//private OpenELISConstants openElisConstants = null;
	private ConstantsWithLookup openElisConstants = (ConstantsWithLookup) AppScreen.getWidgetMap().get("AppConstants");
	private static OpenELISScreenIntAsync screenService = (OpenELISScreenIntAsync)GWT.create(OpenELISScreenInt.class);
    private static ServiceDefTarget target = (ServiceDefTarget)screenService;
    
	public OpenELIS() {	    
        super();
        String base = GWT.getModuleBaseURL();
        base += "OpenELISScreen";
        target.setServiceEntryPoint(base);
        service = screenService;
        getXML();
    }

   public void afterDraw(boolean Success) {
        	//set the constants so we can use it later
           // setOpenElisConstants((OpenELISConstants) constants);
            
        	WindowBrowser browser = (WindowBrowser)getWidget("browser");
        	browser.setBrowserHeight();
        	
        	//load left menu
            OpenELISService.getInstance().getMenuList(new AsyncCallback() {
                public void onSuccess(Object result) {
                    try {
                        ((ScreenMenuPanel)widgets.get("menuList")).load((String)result);
                    } catch (Exception e) {
                        Window.alert(e.getMessage());
                    }
                }

                public void onFailure(Throwable caught) {
                    Window.alert(caught.getMessage());
                }
            });
            
            //try and disable a menu element
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
            copyLabel.addStyleName("disabled");
            
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
    }

    public void onClick(Widget item) {
    	WindowBrowser browser = (WindowBrowser)getWidget("browser");
    	
    	//top menu actions
/*        if (item == widgets.get("patientForm")) {
        	browser.addScreen(new PatientForm(), "Patient", "Patient");
        }else if(item == widgets.get("providerForm")){
        	//browser.addScreen(new UtilitiesForm(), "V/S Codes", "VSCodes");
        }else if(item == widgets.get("organizationFormSmall2")){
        	browser.addScreen(new OrganizationFormSmall2(), "Organization Form", "Organization");
        }else if(item == widgets.get("testForm")){
        	//browser.addScreen(new UtilitiesForm(), "V/S Codes", "VSCodes");
        }else if(item == widgets.get("utilitiesForm")){
        	browser.addScreen(new UtilitiesForm(), "Utilities", "utilities");
        }else if(item == widgets.get("screen1")){
        	browser.addScreen(new Screen1(), "Screen 1", "Organization");
        }else if(item == widgets.get("screen2")){
        	browser.addScreen(new Screen2(), "Screen 2", "Organization");
  */    if(item == widgets.get("file")){
        	//set the selected css element on the menu bar
    		getWidget("file").removeStyleName("topMenuBarItem");
    		getWidget("file").addStyleName("topMenuBarItemSelected");
    		
        	//open the file menu panel
        	ScreenMenuPopupPanel pn = (ScreenMenuPopupPanel) widgets.get("filePanel");        	
        	((PopupPanel)pn.popupPanel).setPopupPosition(getWidget("file").getAbsoluteLeft()-3,(getWidget("topMenu").getAbsoluteTop()+getWidget("topMenu").getOffsetHeight()));
        	((PopupPanel)pn.popupPanel).show();
        }else if(item == widgets.get("edit")){
//        	set the selected css element on the menu bar
    		getWidget("edit").removeStyleName("topMenuBarItem");
    		getWidget("edit").addStyleName("topMenuBarItemSelected");
    		
        	//open the file menu panel
        	ScreenMenuPopupPanel pn = (ScreenMenuPopupPanel) widgets.get("editPanel");
        	((PopupPanel)pn.popupPanel).setPopupPosition(getWidget("edit").getAbsoluteLeft()-3,(getWidget("topMenu").getAbsoluteTop()+getWidget("topMenu").getOffsetHeight()));
        	((PopupPanel)pn.popupPanel).show();
        }else if(item == widgets.get("sampleManagement")){
//        	set the selected css element on the menu bar
    		getWidget("sampleManagement").removeStyleName("topMenuBarItem");
    		getWidget("sampleManagement").addStyleName("topMenuBarItemSelected");
    		
        	//open the file menu panel
        	ScreenMenuPopupPanel pn = (ScreenMenuPopupPanel) widgets.get("sampleManagementPanel");
        	((PopupPanel)pn.popupPanel).setPopupPosition(getWidget("sampleManagement").getAbsoluteLeft()-3,(getWidget("topMenu").getAbsoluteTop()+getWidget("topMenu").getOffsetHeight()));
        	((PopupPanel)pn.popupPanel).show();
        }else if(item == widgets.get("analysisManagement")){
//        	set the selected css element on the menu bar
    		getWidget("analysisManagement").removeStyleName("topMenuBarItem");
    		getWidget("analysisManagement").addStyleName("topMenuBarItemSelected");
    		
        	//open the file menu panel
        	ScreenMenuPopupPanel pn = (ScreenMenuPopupPanel) widgets.get("analysisManagementPanel");     	
        	((PopupPanel)pn.popupPanel).setPopupPosition(getWidget("analysisManagement").getAbsoluteLeft()-3,(getWidget("topMenu").getAbsoluteTop()+getWidget("topMenu").getOffsetHeight()));
        	((PopupPanel)pn.popupPanel).show();
        }else if(item == widgets.get("supplyManagement")){
//        	set the selected css element on the menu bar
    		getWidget("supplyManagement").removeStyleName("topMenuBarItem");
    		getWidget("supplyManagement").addStyleName("topMenuBarItemSelected");
    		
        	//open the file menu panel
        	ScreenMenuPopupPanel pn = (ScreenMenuPopupPanel) widgets.get("supplyManagementPanel");
        	((PopupPanel)pn.popupPanel).setPopupPosition(getWidget("supplyManagement").getAbsoluteLeft()-3,(getWidget("topMenu").getAbsoluteTop()+getWidget("topMenu").getOffsetHeight()));
        	((PopupPanel)pn.popupPanel).show();
        }else if(item == widgets.get("reports")){
//        	set the selected css element on the menu bar
    		getWidget("reports").removeStyleName("topMenuBarItem");
    		getWidget("reports").addStyleName("topMenuBarItemSelected");
    		
        	//open the file menu panel
        	ScreenMenuPopupPanel pn = (ScreenMenuPopupPanel) widgets.get("reportsPanel");
        	((PopupPanel)pn.popupPanel).setPopupPosition(getWidget("reports").getAbsoluteLeft()-3,(getWidget("topMenu").getAbsoluteTop()+getWidget("topMenu").getOffsetHeight()));
        	((PopupPanel)pn.popupPanel).show();
        }else if(item == widgets.get("dataEntry")){
//        	set the selected css element on the menu bar
    		getWidget("dataEntry").removeStyleName("topMenuBarItem");
    		getWidget("dataEntry").addStyleName("topMenuBarItemSelected");
    		
        	//open the file menu panel
        	ScreenMenuPopupPanel pn = (ScreenMenuPopupPanel) widgets.get("dataEntryPanel");
        	((PopupPanel)pn.popupPanel).setPopupPosition(getWidget("dataEntry").getAbsoluteLeft()-3,(getWidget("topMenu").getAbsoluteTop()+getWidget("topMenu").getOffsetHeight()));
        	((PopupPanel)pn.popupPanel).show();
        }else if(item == widgets.get("utilities")){
//        	set the selected css element on the menu bar
    		getWidget("utilities").removeStyleName("topMenuBarItem");
    		getWidget("utilities").addStyleName("topMenuBarItemSelected");
    		
        	//open the file menu panel
        	ScreenMenuPopupPanel pn = (ScreenMenuPopupPanel) widgets.get("utilitiesPanel");
        	((PopupPanel)pn.popupPanel).setPopupPosition(getWidget("utilities").getAbsoluteLeft()-3,(getWidget("topMenu").getAbsoluteTop()+getWidget("topMenu").getOffsetHeight()));
        	((PopupPanel)pn.popupPanel).show();
        }else if(item == widgets.get("favorites")){
//        	set the selected css element on the menu bar
    		getWidget("favorites").removeStyleName("topMenuBarItem");
    		getWidget("favorites").addStyleName("topMenuBarItemSelected");
    		
        	//open the file menu panel
        	ScreenMenuPopupPanel pn = (ScreenMenuPopupPanel) widgets.get("favoritesPanel");
        	((PopupPanel)pn.popupPanel).setPopupPosition(getWidget("favorites").getAbsoluteLeft()-3,(getWidget("topMenu").getAbsoluteTop()+getWidget("topMenu").getOffsetHeight()));
        	((PopupPanel)pn.popupPanel).show();
        }else if(item == widgets.get("window")){
//        	set the selected css element on the menu bar
    		getWidget("window").removeStyleName("topMenuBarItem");
    		getWidget("window").addStyleName("topMenuBarItemSelected");
    		
        	//open the file menu panel
        	ScreenMenuPopupPanel pn = (ScreenMenuPopupPanel) widgets.get("windowPanel");
        	((PopupPanel)pn.popupPanel).setPopupPosition(getWidget("window").getAbsoluteLeft()-3,(getWidget("topMenu").getAbsoluteTop()+getWidget("topMenu").getOffsetHeight()));
        	((PopupPanel)pn.popupPanel).show();
        }else if(item == widgets.get("help")){
//        	set the selected css element on the menu bar
    		getWidget("help").removeStyleName("topMenuBarItem");
    		getWidget("help").addStyleName("topMenuBarItemSelected");
    		
        	//open the file menu panel
        	ScreenMenuPopupPanel pn = (ScreenMenuPopupPanel) widgets.get("helpPanel");
        	((PopupPanel)pn.popupPanel).setPopupPosition(getWidget("help").getAbsoluteLeft()-3,(getWidget("topMenu").getAbsoluteTop()+getWidget("topMenu").getOffsetHeight()));
        	((PopupPanel)pn.popupPanel).show();
        }else if(item == widgets.get("sampleManagement")){
//        	set the selected css element on the menu bar
    		getWidget("sampleManagement").removeStyleName("topMenuBarItem");
    		getWidget("sampleManagement").addStyleName("topMenuBarItemSelected");
    		
        	//open the file menu panel
        	ScreenMenuPopupPanel pn = (ScreenMenuPopupPanel) widgets.get("sampleManagementPanel");
        	((PopupPanel)pn.popupPanel).setPopupPosition(getWidget("sampleManagement").getAbsoluteLeft()-3,(getWidget("topMenu").getAbsoluteTop()+getWidget("topMenu").getOffsetHeight()));
        	((PopupPanel)pn.popupPanel).show();
        }else if((item == widgets.get("category1Icon")) || (item == widgets.get("category1Description")) || (item == widgets.get("category1")) || (item == widgets.get("category1Arrow"))){
//        	set the selected css element on the menu bar
    		//getWidget("sampleManagement").removeStyleName("topMenuBarItem");
    		//getWidget("sampleManagement").addStyleName("topMenuBarItemSelected");
    		
        	//open the file menu panel
        	ScreenMenuPopupPanel pn = (ScreenMenuPopupPanel) widgets.get("reports1SubPanel");
        	((PopupPanel)pn.popupPanel).setPopupPosition(getWidget("category1Arrow").getAbsoluteLeft()+getWidget("category1Arrow").getOffsetWidth()+1,(getWidget("category1").getAbsoluteTop()));
        	((PopupPanel)pn.popupPanel).show();
        }else if((item == widgets.get("category2Icon")) || (item == widgets.get("category2Description")) || (item == widgets.get("category2")) || (item == widgets.get("category2Arrow"))){
//        	set the selected css element on the menu bar
    		//getWidget("sampleManagement").removeStyleName("topMenuBarItem");
    		//getWidget("sampleManagement").addStyleName("topMenuBarItemSelected");
    		
        	//open the file menu panel
        	ScreenMenuPopupPanel pn = (ScreenMenuPopupPanel) widgets.get("reports2SubPanel");
        	((PopupPanel)pn.popupPanel).setPopupPosition(getWidget("category2Arrow").getAbsoluteLeft()+getWidget("category2Arrow").getOffsetWidth()+1,(getWidget("category2").getAbsoluteTop()));
        	((PopupPanel)pn.popupPanel).show();
        }else if((item == widgets.get("category3Icon")) || (item == widgets.get("category3Description")) || (item == widgets.get("category3")) || (item == widgets.get("category3Arrow"))){
//        	set the selected css element on the menu bar
    		//getWidget("sampleManagement").removeStyleName("topMenuBarItem");
    		//getWidget("sampleManagement").addStyleName("topMenuBarItemSelected");
    		
        	//open the file menu panel
        	ScreenMenuPopupPanel pn = (ScreenMenuPopupPanel) widgets.get("reports1SubPanel");
        	((PopupPanel)pn.popupPanel).setPopupPosition(getWidget("category3Arrow").getAbsoluteLeft()+getWidget("category3Arrow").getOffsetWidth()+1,(getWidget("category3").getAbsoluteTop()));
        	((PopupPanel)pn.popupPanel).show();
        	
        //top menu items
        }else if((item == widgets.get("logoutIcon")) || (item == widgets.get("logoutLabel")) || (item == widgets.get("logoutDescription")) || 
        	(item == widgets.get("favTopLogout")) || (item == widgets.get("favLeftLogout"))) {
        	//if the file panel is open we need to close it
        	ScreenMenuPopupPanel pn = (ScreenMenuPopupPanel) widgets.get("filePanel");        	
  
        	closeTopMenuPanel(pn,(Label) getWidget("file"),(ScreenLabel) widgets.get("logoutLabel"));
        	//we need to do the logout action
        	//FIXME code the logout action
        }else if((item == widgets.get("cutIcon")) || (item == widgets.get("cutLabel")) || (item == widgets.get("favTopCut")) || (item == widgets.get("favLeftCut"))){
//        	if the edit panel is open we need to close it
        	ScreenMenuPopupPanel pn = (ScreenMenuPopupPanel) widgets.get("editPanel");        	
  
        	closeTopMenuPanel(pn,(Label) getWidget("edit"),(ScreenLabel) widgets.get("cutLabel"));
        	//we need to do the cut action
        	//FIXME code the cut action
        }else if((item == widgets.get("copyIcon")) || (item == widgets.get("copyLabel")) || (item == widgets.get("favTopCopy")) || (item == widgets.get("favLeftCopy"))){
//        	if the edit panel is open we need to close it
        	ScreenMenuPopupPanel pn = (ScreenMenuPopupPanel) widgets.get("editPanel");        	
  
        	closeTopMenuPanel(pn,(Label) getWidget("edit"),(ScreenLabel) widgets.get("copyLabel"));
        	//we need to do the copy action
        	//FIXME code the copy action
        }else if((item == widgets.get("pasteIcon")) || (item == widgets.get("pasteLabel")) || (item == widgets.get("favTopPaste")) || (item == widgets.get("favLeftPaste"))){
//        	if the edit panel is open we need to close it
        	ScreenMenuPopupPanel pn = (ScreenMenuPopupPanel) widgets.get("editPanel");        	
  
        	closeTopMenuPanel(pn,(Label) getWidget("edit"),(ScreenLabel) widgets.get("pasteLabel"));
        	//we need to do the paste action
        	//FIXME code the paste action
        }else if((item == widgets.get("projectIcon")) || (item == widgets.get("projectLabel")) || (item == widgets.get("projectDescription")) || 
        		(item == widgets.get("favTopProject")) || (item == widgets.get("favLeftProject"))){
//        	if the edit sample management is open we need to close it
        	ScreenMenuPopupPanel pn = (ScreenMenuPopupPanel) widgets.get("sampleManagementPanel");        	
  
        	closeTopMenuPanel(pn,(Label) getWidget("sampleManagement"),(ScreenLabel) widgets.get("projectLabel"));
        	//we need to do the project action
        	//FIXME code the project action
        }else if((item == widgets.get("releaseIcon")) || (item == widgets.get("releaseLabel")) || (item == widgets.get("releaseDescription")) || 
        		(item == widgets.get("favTopRelease")) || (item == widgets.get("favLeftRelease"))){
//        	if the edit sample management is open we need to close it
        	ScreenMenuPopupPanel pn = (ScreenMenuPopupPanel) widgets.get("sampleManagementPanel");        	
  
        	closeTopMenuPanel(pn,(Label) getWidget("sampleManagement"),(ScreenLabel) widgets.get("releaseLabel"));
        	//we need to do the release action
        	//FIXME code the release action
        }else if((item == widgets.get("sampleLookupIcon")) || (item == widgets.get("sampleLookupLabel")) || (item == widgets.get("sampleLookupDescription")) || 
        		(item == widgets.get("favTopSampleLookup")) || (item == widgets.get("favLeftSampleLookup"))){
//        	if the edit sample management is open we need to close it
        	ScreenMenuPopupPanel pn = (ScreenMenuPopupPanel) widgets.get("sampleManagementPanel");        	
  
        	closeTopMenuPanel(pn,(Label) getWidget("sampleManagement"),(ScreenLabel) widgets.get("sampleLookupLabel"));
        	//we need to do the sample lookup action
        	//FIXME code the sample lookup action
        }else if((item == widgets.get("analyteIcon")) || (item == widgets.get("analyteLabel")) || (item == widgets.get("analyteDescription")) || 
        		(item == widgets.get("favTopAnalyte")) || (item == widgets.get("favLeftAnalyte"))){
//        	if the edit analysis management is open we need to close it
        	ScreenMenuPopupPanel pn = (ScreenMenuPopupPanel) widgets.get("analysisManagementPanel");        	
  
        	closeTopMenuPanel(pn,(Label) getWidget("analysisManagement"),(ScreenLabel) widgets.get("analyteLabel"));
        	//we need to do the analyte action
        	//FIXME code the analyte action
        }else if((item == widgets.get("methodIcon")) || (item == widgets.get("methodLabel")) || (item == widgets.get("methodDescription")) || 
        		(item == widgets.get("favTopMethod")) || (item == widgets.get("favLeftMethod"))){
//        	if the edit analysis management is open we need to close it
        	ScreenMenuPopupPanel pn = (ScreenMenuPopupPanel) widgets.get("analysisManagementPanel");        	
  
        	closeTopMenuPanel(pn,(Label) getWidget("analysisManagement"),(ScreenLabel) widgets.get("methodLabel"));
        	//we need to do the method action
        	//FIXME code the method action
        }else if((item == widgets.get("methodPanelIcon")) || (item == widgets.get("methodPanelLabel")) || (item == widgets.get("methodPanelDescription")) || 
        		(item == widgets.get("favTopMethodPanel")) || (item == widgets.get("favLeftMethodPanel"))){
//        	if the edit analysis management is open we need to close it
        	ScreenMenuPopupPanel pn = (ScreenMenuPopupPanel) widgets.get("analysisManagementPanel");        	
  
        	closeTopMenuPanel(pn,(Label) getWidget("analysisManagement"),(ScreenLabel) widgets.get("methodPanelLabel"));
        	//we need to do the method panel action
        	//FIXME code the method panel action
        }else if((item == widgets.get("qaEventsIcon")) || (item == widgets.get("qaEventsLabel")) || (item == widgets.get("qaEventsDescription")) || 
        		(item == widgets.get("favTopqaEvents")) || (item == widgets.get("favLeftqaEvents"))){
//        	if the edit analysis management is open we need to close it
        	ScreenMenuPopupPanel pn = (ScreenMenuPopupPanel) widgets.get("analysisManagementPanel");        	
  
        	closeTopMenuPanel(pn,(Label) getWidget("analysisManagement"),(ScreenLabel) widgets.get("qaEventsLabel"));
        	//we need to do the qa events action
        	//FIXME code the qa events action
        }else if((item == widgets.get("resultsIcon")) || (item == widgets.get("resultsLabel")) || (item == widgets.get("resultsDescription")) || 
        		(item == widgets.get("favTopResults")) || (item == widgets.get("favLeftResults"))){
//        	if the edit analysis management is open we need to close it
        	ScreenMenuPopupPanel pn = (ScreenMenuPopupPanel) widgets.get("analysisManagementPanel");        	
  
        	closeTopMenuPanel(pn,(Label) getWidget("analysisManagement"),(ScreenLabel) widgets.get("resultsLabel"));
        	//we need to do the results action
        	//FIXME code the results action
        }else if((item == widgets.get("testManagementIcon")) || (item == widgets.get("testManagementLabel")) || (item == widgets.get("testManagementDescription")) ||
        		(item == widgets.get("favTopTestManangement")) || (item == widgets.get("favLeftTestManagement"))){
//        	if the edit analysis management is open we need to close it
        	ScreenMenuPopupPanel pn = (ScreenMenuPopupPanel) widgets.get("analysisManagementPanel");        	
  
        	closeTopMenuPanel(pn,(Label) getWidget("analysisManagement"),(ScreenLabel) widgets.get("testManagementLabel"));
        	//we need to do the test management action
        	//FIXME code the test management action
        }else if((item == widgets.get("testTrailerIcon")) || (item == widgets.get("testTrailerLabel")) || (item == widgets.get("testTrailerDescription")) || 
        		(item == widgets.get("favTopTestTrailer")) || (item == widgets.get("favLeftTestTrailer"))){
//        	if the edit analysis management is open we need to close it
        	ScreenMenuPopupPanel pn = (ScreenMenuPopupPanel) widgets.get("analysisManagementPanel");        	
  
        	closeTopMenuPanel(pn,(Label) getWidget("analysisManagement"),(ScreenLabel) widgets.get("testTrailerLabel"));
        	//we need to do the test trailer action
        	//FIXME code the test trailer action
        }else if((item == widgets.get("worksheetsIcon")) || (item == widgets.get("worksheetsLabel")) || (item == widgets.get("worksheetsDescription")) || 
        		(item == widgets.get("favTopWorksheets")) || (item == widgets.get("favLeftWorksheets"))){
//        	if the edit analysis management is open we need to close it
        	ScreenMenuPopupPanel pn = (ScreenMenuPopupPanel) widgets.get("analysisManagementPanel");        	
  
        	closeTopMenuPanel(pn,(Label) getWidget("analysisManagement"),(ScreenLabel) widgets.get("worksheetsLabel"));
        	//we need to do the worksheets action
        	//FIXME code the worksheets action
        }else if((item == widgets.get("instrumentIcon")) || (item == widgets.get("instrumentLabel")) || (item == widgets.get("instrumentDescription")) || 
        		(item == widgets.get("favTopInstrument")) || (item == widgets.get("favLeftInstrument"))){
//        	if the supply management is open we need to close it
        	ScreenMenuPopupPanel pn = (ScreenMenuPopupPanel) widgets.get("supplyManagementPanel");        	
  
        	closeTopMenuPanel(pn,(Label) getWidget("supplyManagement"),(ScreenLabel) widgets.get("instrumentLabel"));
        	//we need to do the instrument action
        	//FIXME code the instrument action
        }else if((item == widgets.get("inventoryIcon")) || (item == widgets.get("inventoryLabel")) || (item == widgets.get("inventoryDescription")) || 
        		(item == widgets.get("favTopInventory")) || (item == widgets.get("favLeftInventory"))){
//        	if the supply management is open we need to close it
        	ScreenMenuPopupPanel pn = (ScreenMenuPopupPanel) widgets.get("supplyManagementPanel");        	
  
        	closeTopMenuPanel(pn,(Label) getWidget("supplyManagement"),(ScreenLabel) widgets.get("inventoryLabel"));
        	//we need to do the inventory action
        	//FIXME code the inventory action
        }else if((item == widgets.get("orderIcon")) || (item == widgets.get("orderLabel")) || (item == widgets.get("orderDescription")) || 
        		(item == widgets.get("favTopOrder")) || (item == widgets.get("favLeftOrder"))){
//        	if the supply management is open we need to close it
        	ScreenMenuPopupPanel pn = (ScreenMenuPopupPanel) widgets.get("supplyManagementPanel");        	
  
        	closeTopMenuPanel(pn,(Label) getWidget("supplyManagement"),(ScreenLabel) widgets.get("orderLabel"));
        	//we need to do the order action
        	//FIXME code the order action
        }else if((item == widgets.get("storageIcon")) || (item == widgets.get("storageLabel")) || (item == widgets.get("storageDescription")) || 
        		(item == widgets.get("favTopStorage")) || (item == widgets.get("favLeftStorage"))){
//        	if the supply management is open we need to close it
        	ScreenMenuPopupPanel pn = (ScreenMenuPopupPanel) widgets.get("supplyManagementPanel");        	
  
        	closeTopMenuPanel(pn,(Label) getWidget("supplyManagement"),(ScreenLabel) widgets.get("storageLabel"));
        	//we need to do the storage action
        	//FIXME code the storage action
        }else if((item == widgets.get("fastSampleLoginIcon")) || (item == widgets.get("fastSampleLoginLabel")) || (item == widgets.get("fastSampleLoginDescription")) || 
        		(item == widgets.get("favTopFastSampleLogin")) || (item == widgets.get("favLeftFastSampleLogin"))){
//        	if the data entry is open we need to close it
        	ScreenMenuPopupPanel pn = (ScreenMenuPopupPanel) widgets.get("dataEntryPanel");        	
  
        	closeTopMenuPanel(pn,(Label) getWidget("dataEntry"),(ScreenLabel) widgets.get("fastSampleLoginLabel"));
        	//we need to do the fast sample login action
        	//FIXME code the fast sample login action
        }else if((item == widgets.get("organizationIcon")) || (item == widgets.get("organizationLabel")) || (item == widgets.get("organizationDescription")) || 
        		(item == widgets.get("favTopOrganization")) || (item == widgets.get("favLeftOrganization"))){
//        	if the data entry is open we need to close it
        	ScreenMenuPopupPanel pn = (ScreenMenuPopupPanel) widgets.get("dataEntryPanel");        	
  
        	closeTopMenuPanel(pn,(Label) getWidget("dataEntry"),(ScreenLabel) widgets.get("organizationLabel"));
        	
        	//we need to do the organization action        	openElisConstants.loadingMessage()
        	browser.addScreen(new Organization(), openElisConstants.getString("organization"), "Organization", openElisConstants.getString("loadingMessage"));
        }else if((item == widgets.get("patientIcon")) || (item == widgets.get("patientLabel")) || (item == widgets.get("patientDescription")) || 
        		(item == widgets.get("favTopPatient")) || (item == widgets.get("favLeftPatient"))){
//        	if the data entry is open we need to close it
        	ScreenMenuPopupPanel pn = (ScreenMenuPopupPanel) widgets.get("dataEntryPanel");        	
        	closeTopMenuPanel(pn,(Label) getWidget("dataEntry"),(ScreenLabel) widgets.get("patientLabel"));
        	//we need to do the patient action
        	//FIXME code the patient action
        }else if((item == widgets.get("personIcon")) || (item == widgets.get("personLabel")) || (item == widgets.get("personDescription")) || 
        		(item == widgets.get("favTopPerson")) || (item == widgets.get("favLeftPerson"))){
//        	if the data entry is open we need to close it
        	ScreenMenuPopupPanel pn = (ScreenMenuPopupPanel) widgets.get("dataEntryPanel");        	
        	closeTopMenuPanel(pn,(Label) getWidget("dataEntry"),(ScreenLabel) widgets.get("personLabel"));
        	//we need to do the person action
        	//FIXME code the person action
        }else if((item == widgets.get("providerIcon")) || (item == widgets.get("providerLabel")) || (item == widgets.get("providerDescription")) || 
        		(item == widgets.get("favTopFastProvider")) || (item == widgets.get("favLeftFastProvider"))){
//        	if the data entry is open we need to close it
        	ScreenMenuPopupPanel pn = (ScreenMenuPopupPanel) widgets.get("dataEntryPanel");        	
  
        	closeTopMenuPanel(pn,(Label) getWidget("dataEntry"),(ScreenLabel) widgets.get("providerLabel"));
        	//we need to do the provider action
        	//FIXME code the provider action
        }else if((item == widgets.get("sampleLoginIcon")) || (item == widgets.get("sampleLoginLabel")) || (item == widgets.get("sampleLoginDescription")) || 
        		(item == widgets.get("favTopSampleLogin")) || (item == widgets.get("favLeftSampleLogin"))){
//        	if the data entry is open we need to close it
        	ScreenMenuPopupPanel pn = (ScreenMenuPopupPanel) widgets.get("dataEntryPanel");        	
  
        	closeTopMenuPanel(pn,(Label) getWidget("dataEntry"),(ScreenLabel) widgets.get("sampleLoginLabel"));
        	//we need to do the sample login action
        	//FIXME code the sample login action
        }else if((item == widgets.get("auxiliaryIcon")) || (item == widgets.get("auxiliaryLabel")) || (item == widgets.get("auxiliaryDescription")) || 
        		(item == widgets.get("favTopAuxiliary")) || (item == widgets.get("favLeftAuxiliary"))){
//        	if the utilities is open we need to close it
        	ScreenMenuPopupPanel pn = (ScreenMenuPopupPanel) widgets.get("utilitiesPanel");        	
  
        	closeTopMenuPanel(pn,(Label) getWidget("utilities"),(ScreenLabel) widgets.get("auxiliaryLabel"));
        	//we need to do the auxiliary action
        	//FIXME code the auxiliary action
        }else if((item == widgets.get("dictionaryIcon")) || (item == widgets.get("dictionaryLabel")) || (item == widgets.get("dictionaryDescription")) || 
        		(item == widgets.get("favTopDictionary")) || (item == widgets.get("favLeftDictionary"))){
//        	if the utilities is open we need to close it
        	ScreenMenuPopupPanel pn = (ScreenMenuPopupPanel) widgets.get("utilitiesPanel");        	
  
        	closeTopMenuPanel(pn,(Label) getWidget("utilities"),(ScreenLabel) widgets.get("dictionaryLabel"));
        	//we need to do the dictionary action
        	//FIXME code the dictionary action
        }else if((item == widgets.get("labelIcon")) || (item == widgets.get("labelLabel")) || (item == widgets.get("labelDescription")) || 
        		(item == widgets.get("favTopLabel")) || (item == widgets.get("favLeftLabel"))){
//        	if the utilities is open we need to close it
        	ScreenMenuPopupPanel pn = (ScreenMenuPopupPanel) widgets.get("utilitiesPanel");        	
  
        	closeTopMenuPanel(pn,(Label) getWidget("utilities"),(ScreenLabel) widgets.get("labelLabel"));
        	//we need to do the label action
        	//FIXME code the label action
        }else if((item == widgets.get("referenceTableIcon")) || (item == widgets.get("referenceTableLabel")) || (item == widgets.get("referenceTableDescription")) || 
        		(item == widgets.get("favTopReferenceTable")) || (item == widgets.get("favLeftReferenceTable"))){
//        	if the utilities is open we need to close it
        	ScreenMenuPopupPanel pn = (ScreenMenuPopupPanel) widgets.get("utilitiesPanel");        	
  
        	closeTopMenuPanel(pn,(Label) getWidget("utilities"),(ScreenLabel) widgets.get("referenceTableLabel"));
        	//we need to do the reference table action
        	//FIXME code the reference table action
        }else if((item == widgets.get("scriptletIcon")) || (item == widgets.get("scriptletLabel")) || (item == widgets.get("scriptletDescription")) || 
        		(item == widgets.get("favTopScriptlet")) || (item == widgets.get("favLeftScriptlet"))){
//        	if the utilities is open we need to close it
        	ScreenMenuPopupPanel pn = (ScreenMenuPopupPanel) widgets.get("utilitiesPanel");        	
  
        	closeTopMenuPanel(pn,(Label) getWidget("utilities"),(ScreenLabel) widgets.get("scriptletLabel"));
        	//we need to do the scriptlet action
        	//FIXME code the scriptlet action
        }else if((item == widgets.get("sectionIcon")) || (item == widgets.get("sectionLabel")) || (item == widgets.get("sectionDescription")) || 
        		(item == widgets.get("favTopSection")) || (item == widgets.get("favLeftSection"))){
//        	if the utilities is open we need to close it
        	ScreenMenuPopupPanel pn = (ScreenMenuPopupPanel) widgets.get("utilitiesPanel");        	
  
        	closeTopMenuPanel(pn,(Label) getWidget("utilities"),(ScreenLabel) widgets.get("sectionLabel"));
        	//we need to do the section action
        	//FIXME code the section action
        }else if((item == widgets.get("standardNoteIcon")) || (item == widgets.get("standardNoteLabel")) || (item == widgets.get("standardNoteDescription")) || 
        		(item == widgets.get("favTopStandardNote")) || (item == widgets.get("favLeftStandardNote"))){
//        	if the utilities is open we need to close it
        	ScreenMenuPopupPanel pn = (ScreenMenuPopupPanel) widgets.get("utilitiesPanel");        	
  
        	closeTopMenuPanel(pn,(Label) getWidget("utilities"),(ScreenLabel) widgets.get("standardNoteLabel"));
        	//we need to do the standard note action
        	//FIXME code the standard note action
        }else if((item == widgets.get("systemVariableIcon")) || (item == widgets.get("systemVariableLabel")) || (item == widgets.get("systemVariableDescription")) || 
        		(item == widgets.get("favTopSystemVariable")) || (item == widgets.get("favLeftSystemVariable"))){
//        	if the utilities is open we need to close it
        	ScreenMenuPopupPanel pn = (ScreenMenuPopupPanel) widgets.get("utilitiesPanel");        	
  
        	closeTopMenuPanel(pn,(Label) getWidget("utilities"),(ScreenLabel) widgets.get("systemVariableLabel"));
        	//we need to do the system variable action
        	//FIXME code the system variable action
        }else if(item == widgets.get("organizeFavoritesLeft") || item == widgets.get("organizeFavoritesLabel")){
//        	if the favorites is open we need to close it
        	ScreenMenuPopupPanel pn = (ScreenMenuPopupPanel) widgets.get("favoritesPanel");        	
  
        	closeTopMenuPanel(pn,(Label) getWidget("favorites"),(ScreenLabel) widgets.get("organizeFavoritesLabel"));
        	//we need to do the system variable action
        	browser.addScreen(new OrganizeFavorites(), "Organize Favorites", openElisConstants.getString("organizeFavorites"),openElisConstants.getString("loadingMessage"));
        }else if((item == widgets.get("closeCurrentWindowLabel")) || 
        		(item == widgets.get("favTopCloseCurrentWindow")) || (item == widgets.get("favLeftCloseCurrentWindow"))){
//        	if the window is open we need to close it
        	ScreenMenuPopupPanel pn = (ScreenMenuPopupPanel) widgets.get("windowPanel");        	
  
        	closeTopMenuPanel(pn,(Label) getWidget("window"),(ScreenLabel) widgets.get("closeCurrentWindowLabel"));
        	//we need to do the close current window action
        	//FIXME code the close current window action
        }else if((item == widgets.get("closeAllWindowsLabel")) || 
        		(item == widgets.get("favTopCloseAllWindows")) || (item == widgets.get("favLeftCloseAllWindows"))){
//        	if the window is open we need to close it
        	ScreenMenuPopupPanel pn = (ScreenMenuPopupPanel) widgets.get("windowPanel");        	
  
        	closeTopMenuPanel(pn,(Label) getWidget("window"),(ScreenLabel) widgets.get("closeAllWindowsLabel"));
        	//we need to do the close all windows action
        	//FIXME code the close all windows action
        }else if((item == widgets.get("indexIcon")) || (item == widgets.get("indexLabel")) || (item == widgets.get("indexDescription")) || 
        		(item == widgets.get("favTopIndex")) || (item == widgets.get("favLeftIndex"))){
//        	if the help is open we need to close it
        	ScreenMenuPopupPanel pn = (ScreenMenuPopupPanel) widgets.get("helpPanel");        	
  
        	closeTopMenuPanel(pn,(Label) getWidget("help"),(ScreenLabel) widgets.get("indexLabel"));
        	//we need to do the help action
        	//FIXME code the help action
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
    
    private void closeTopMenuPanel(ScreenMenuPopupPanel pn, Label menuLabel, ScreenLabel menuPanelItem){
    	((PopupPanel)pn.popupPanel).hide();
    	
    	removeHoverFromTablePanel(menuPanelItem);
    }
    
    private void addHoverToTablePanel(Widget sender){
    	String screenWidget;
		screenWidget = (String) ((ScreenWidget)sender).getUserObject();
		
		if(screenWidget.indexOf(":")>-1){
			//we need to add the hover effect to the row
			String tableKey = screenWidget.substring(0,screenWidget.indexOf(":"));
			int rowNumber = Integer.valueOf(screenWidget.substring(screenWidget.indexOf(":")+1)).intValue();
			
			ScreenTablePanel tablePanel = (ScreenTablePanel) widgets.get(tableKey);
			tablePanel.onMouseEnter(rowNumber);
		}
    }
    
    private void removeHoverFromTablePanel(Widget sender){
    	String screenWidget;
		screenWidget = (String) ((ScreenWidget)sender).getUserObject();
		
		if(screenWidget.indexOf(":")>-1){
			//we need to add the hover effect to the row
			String tableKey = screenWidget.substring(0,screenWidget.indexOf(":"));
			int rowNumber = Integer.valueOf(screenWidget.substring(screenWidget.indexOf(":")+1)).intValue();
			
			ScreenTablePanel tablePanel = (ScreenTablePanel) widgets.get(tableKey);
			tablePanel.onMouseLeave(rowNumber);
		}
    }

    public void onMouseEnter(Widget sender) {
    	super.onMouseEnter(sender);
    	
    	addHoverToTablePanel(sender);
    }
    
    public void onMouseLeave(Widget sender) {
    	super.onMouseLeave(sender);
    	
    	removeHoverFromTablePanel(sender);
    }
    
    public void onPopupClosed(PopupPanel sender, boolean autoClosed) {

    	if(sender == ((ScreenMenuPopupPanel)widgets.get("filePanel")).popupPanel){
    		getWidget("file").removeStyleName("topMenuBarItemSelected");
    		getWidget("file").addStyleName("topMenuBarItem");
    		
    	}else if(sender == ((ScreenMenuPopupPanel)widgets.get("editPanel")).popupPanel){
    		getWidget("edit").removeStyleName("topMenuBarItemSelected");
    		getWidget("edit").addStyleName("topMenuBarItem");
    		
    	}else if(sender == ((ScreenMenuPopupPanel)widgets.get("sampleManagementPanel")).popupPanel){
    		getWidget("sampleManagement").removeStyleName("topMenuBarItemSelected");
    		getWidget("sampleManagement").addStyleName("topMenuBarItem");
    		
    	}else if(sender == ((ScreenMenuPopupPanel)widgets.get("analysisManagementPanel")).popupPanel){
    		getWidget("analysisManagement").removeStyleName("topMenuBarItemSelected");
    		getWidget("analysisManagement").addStyleName("topMenuBarItem");
    		
    	}else if(sender == ((ScreenMenuPopupPanel)widgets.get("supplyManagementPanel")).popupPanel){
    		getWidget("supplyManagement").removeStyleName("topMenuBarItemSelected");
    		getWidget("supplyManagement").addStyleName("topMenuBarItem");
    		
    	}else if(sender == ((ScreenMenuPopupPanel)widgets.get("reportsPanel")).popupPanel){
    		getWidget("reports").removeStyleName("topMenuBarItemSelected");
    		getWidget("reports").addStyleName("topMenuBarItem");
    		
    	}else if(sender == ((ScreenMenuPopupPanel)widgets.get("dataEntryPanel")).popupPanel){
    		getWidget("dataEntry").removeStyleName("topMenuBarItemSelected");
    		getWidget("dataEntry").addStyleName("topMenuBarItem");
    		
    	}else if(sender == ((ScreenMenuPopupPanel)widgets.get("utilitiesPanel")).popupPanel){
    		getWidget("utilities").removeStyleName("topMenuBarItemSelected");
    		getWidget("utilities").addStyleName("topMenuBarItem");
    		
    	}else if(sender == ((ScreenMenuPopupPanel)widgets.get("favoritesPanel")).popupPanel){
    		getWidget("favorites").removeStyleName("topMenuBarItemSelected");
    		getWidget("favorites").addStyleName("topMenuBarItem");
    		
    	}else if(sender == ((ScreenMenuPopupPanel)widgets.get("windowPanel")).popupPanel){
    		getWidget("window").removeStyleName("topMenuBarItemSelected");
    		getWidget("window").addStyleName("topMenuBarItem");
    		
    	}else if(sender == ((ScreenMenuPopupPanel)widgets.get("helpPanel")).popupPanel){
    		getWidget("help").removeStyleName("topMenuBarItemSelected");
    		getWidget("help").addStyleName("topMenuBarItem");
    	
    	}else if(((sender == ((ScreenMenuPopupPanel)widgets.get("reports1SubPanel")).popupPanel) || (sender == ((ScreenMenuPopupPanel)widgets.get("reports2SubPanel")).popupPanel))){
    		//getWidget("help").removeStyleName("topMenuBarItemSelected");
    		//getWidget("help").addStyleName("topMenuBarItem");
    		
    		//need to see if there are any hover css on any row in parent panel
    		boolean shouldClose = true;
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
    		}
    		
    	}

    	
    }
    
    public void setStyleToAllCellsInCol(FlexTable table, int col,String style){
    	for(int i=0;i<table.getRowCount(); i++){
    		table.getCellFormatter().addStyleName(i,col, style);
    	}
    }
}
