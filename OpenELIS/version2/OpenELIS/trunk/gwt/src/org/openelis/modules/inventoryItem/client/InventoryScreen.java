package org.openelis.modules.inventoryItem.client;

import org.openelis.gwt.common.FormRPC;
import org.openelis.gwt.common.data.DataModel;
import org.openelis.gwt.common.data.DataObject;
import org.openelis.gwt.common.data.NumberObject;
import org.openelis.gwt.common.data.StringObject;
import org.openelis.gwt.common.data.TableField;
import org.openelis.gwt.common.data.TableModel;
import org.openelis.gwt.screen.ScreenMenuItem;
import org.openelis.gwt.screen.ScreenTextArea;
import org.openelis.gwt.screen.ScreenTextBox;
import org.openelis.gwt.screen.ScreenVertical;
import org.openelis.gwt.screen.ScreenWindow;
import org.openelis.gwt.widget.AToZPanel;
import org.openelis.gwt.widget.AppButton;
import org.openelis.gwt.widget.AutoCompleteDropdown;
import org.openelis.gwt.widget.ButtonPanel;
import org.openelis.gwt.widget.FormInt;
import org.openelis.gwt.widget.table.EditTable;
import org.openelis.gwt.widget.table.TableWidget;
import org.openelis.modules.main.client.OpenELISScreenForm;
import org.openelis.modules.standardnotepicker.client.StandardNotePickerScreen;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.SourcesTabEvents;
import com.google.gwt.user.client.ui.TabListener;
import com.google.gwt.user.client.ui.TabPanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class InventoryScreen extends OpenELISScreenForm implements ClickListener, TabListener{

	private ScreenTextBox nameTextbox;
	private ScreenTextBox idTextBox;
	private ScreenTextArea noteText;
	
	private EditTable componentsController, locsController;
    
    private static boolean loaded = false;
    private static DataModel storesDropdown, categoriesDropdown,
                    purchasedUnitsDropdown, dispensedUnitsDropdown;
    
    private boolean          loadComponents = true, 
                             loadLocations = true, 
                             loadComments = true,
                             clearComponents = false, 
                             clearLocations = false, 
                             clearComments = false;
    
    private ScreenVertical   svp       = null;
	
	public InventoryScreen() {
        super("org.openelis.modules.inventory.server.InventoryService",!loaded);
	}

    public void onChange(Widget sender) {
        if(sender == getWidget("atozButtons")){
           String action = ((ButtonPanel)sender).buttonClicked.action;
           if(action.startsWith("query:")){
        	   getInventories(action.substring(6, action.length()), ((ButtonPanel)sender).buttonClicked);      
           }
        }else{
            super.onChange(sender);
        }
    }
    
	public void onClick(Widget sender) {
		if(sender instanceof ScreenMenuItem){
        	if(((String)((ScreenMenuItem)sender).getUserObject()).equals("duplicateRecord")){
                Window.alert("clicked duplicate record");
                return;
        	}
		}else if(sender instanceof AppButton){
			String action = ((AppButton)sender).action;
			if(action.equals("standardNote"))
				onStandardNoteButtonClick();
		}
	}
	
	public void afterDraw(boolean success) {
        AutoCompleteDropdown drop;
        
        loaded = true;
        
		setBpanel((ButtonPanel) getWidget("buttons"));

		AToZPanel atozTable = (AToZPanel) getWidget("hideablePanel");
		modelWidget.addChangeListener(atozTable);
        addChangeListener(atozTable);
        
        ButtonPanel atozButtons = (ButtonPanel)getWidget("atozButtons");
        atozButtons.addChangeListener(this);
        
        nameTextbox = (ScreenTextBox) widgets.get("inventoryItem.name");
        idTextBox = (ScreenTextBox) widgets.get("inventoryItem.id");
        noteText = (ScreenTextArea) widgets.get("note.text");
        
        locsController = ((TableWidget)getWidget("locQuantitiesTable")).controller;
		locsController.setAutoAdd(false);
        ((InventoryLocationsTable)locsController.manager).setInventoryForm(this);
		
		componentsController = ((TableWidget)getWidget("componentsTable")).controller;
		componentsController.setAutoAdd(false);
        ((InventoryComponentsTable)componentsController.manager).setInventoryForm(this);
		
        svp = (ScreenVertical) widgets.get("notesPanel");
        
        if (storesDropdown == null) {
            storesDropdown = (DataModel)initData.get("stores");
            categoriesDropdown = (DataModel)initData.get("categories");
            purchasedUnitsDropdown = (DataModel)initData.get("units");
            dispensedUnitsDropdown = (DataModel)initData.get("units");
        }
        
        drop = (AutoCompleteDropdown)getWidget("inventoryItem.store");
        drop.setModel(storesDropdown);
        
        drop = (AutoCompleteDropdown)getWidget("inventoryItem.category");
        drop.setModel(categoriesDropdown);
        
        drop = (AutoCompleteDropdown)getWidget("inventoryItem.purchasedUnits");
        drop.setModel(purchasedUnitsDropdown);
        
        drop = (AutoCompleteDropdown)getWidget("inventoryItem.dispensedUnits");
        drop.setModel(dispensedUnitsDropdown);
        
		super.afterDraw(success);			
	}
    
    /*
     * Overriden to reset the data in Contact and Note tabs
     */
    public void afterFetch(boolean success) {
        if (success) {
            loadComponents = true;
            loadLocations = true;
            loadComments = true;
            loadTabs();
        }
        super.afterFetch(success);
    }
	
	public void add() {
		componentsController.setAutoAdd(true);
		
		super.add();
		
		nameTextbox.setFocus(true);
		
		idTextBox.enable(false);
	}
	
	public void afterUpdate(boolean success) {
		super.afterUpdate(success);
        if (success) {
            loadComponents = true;
            loadLocations = true;
            loadComments = true;
            
            loadTabs();
        }
		
		nameTextbox.setFocus(true);
		idTextBox.enable(false);
	}
	
	public void query() {		
		super.query();
		
		nameTextbox.setFocus(true);
		
		noteText.enable(false);
	}
	
	public void abort() {
		componentsController.setAutoAdd(false);
        
        if(state == FormInt.ADD){
            loadComponents = false;
            clearComponents = true;
            
            loadLocations = false;
            clearLocations = true;
            
            loadComments = false;
            clearComments = true;
        }else{
            loadComponents = true;
            loadLocations = true;
            loadComments = true;
        }
        
        //the super needs to go before the load tabs method or the table wont load.
        super.abort();
        
        loadTabs();
	}
	
	public void update() {
		//locsController.setAutoAdd(true);
		componentsController.setAutoAdd(true);
		super.update();
	}

	public void afterCommitUpdate(boolean success) {
		componentsController.setAutoAdd(false);
		
		super.afterCommitUpdate(success);
        
//      we need to load the comments tab if it has been already loaded
        if(success){ 
            loadComments = true;
            clearComments = true;
            
            loadTabs();
            
            //the note subject and body fields need to be refeshed after every successful commit
            clearCommentsFields();
        }
	}
	
	public void afterCommitAdd(boolean success) {
		componentsController.setAutoAdd(false);
		
		super.afterCommitAdd(success);
        
//       we need to load the comments tab if it has been already loaded
        if(success){ 
            loadComments = true;
            clearComments = true;
            
            loadTabs();
            
            //the note subject and body fields need to be refeshed after every successful commit
            clearCommentsFields();
        }
	}
	
    /*
     * Overriden to allow lazy loading Contact and Note tabs
     */
	public boolean onBeforeTabSelected(SourcesTabEvents sender, int index) {
        if (index == 0 && loadComponents) {
            if (clearComponents)
                clearComponents();
            fillComponentsModel();
            loadComponents = false;
        } else if (index == 1 && loadLocations) {
            if (clearLocations)
                clearLocations();
            fillLocationsModel();
            loadLocations = false;
        } else if(index == 4 && loadComments){
            if (clearComments)
                clearComments();
            fillCommentsModel();
            loadComments = false;            
        }
        return true;
	}

	public void onTabSelected(SourcesTabEvents sender, int tabIndex) {
	
		
	}
    
    private void loadTabs() {
        TabPanel tabPanel = (TabPanel)getWidget("tabPanel");
        int selectedTab = tabPanel.getTabBar().getSelectedTab();

        if (selectedTab == 0 && loadComponents) {
            // if there was data previously then clear the components table
            // otherwise don't
            if (clearComponents) {
                clearComponents();
            }
            // load the components table
            fillComponentsModel();
            // don't load it again unless the mode changes or a new fetch is
            // done
            loadComponents = false;
            
        } else if (selectedTab == 1 && loadLocations) {
            // if there was data previously then clear the locations table otherwise
            // don't
            if (clearLocations) {
                clearLocations();
            }
            // load the locations table
            fillLocationsModel();
            // don't load it again unless the mode changes or a new fetch is
            // done
            loadLocations = false;

        } else if (selectedTab == 4 && loadComments) {
            // if there was data previously then clear the comments panel otherwise
            // don't
            if (clearComments) {
                clearComments();
            }
            // load the comments model
            fillCommentsModel();
            // don't load it again unless the mode changes or a new fetch is
            // done
            loadComments = false;

        }
    }
    
    private void clearCommentsFields(){
        //     the note subject and body fields need to be refeshed after every successful commit 
           TextBox subjectBox = (TextBox)getWidget("note.subject");           
           subjectBox.setText("");
          TextArea noteArea = (TextArea)getWidget("note.text");
          noteArea.setText("");           
          rpc.setFieldValue("note.subject", null);
          rpc.setFieldValue("note.text", null);  
       }
    
    private void fillComponentsModel(){
        Integer itemId = null;
        NumberObject itemIdObj;
        TableField f;

        if (key == null || key.getKey() == null) {
            clearComponents = false;
            return;
        }

        window.setStatus("","spinnerIcon");
        
        itemId = (Integer)key.getKey().getValue();
        itemIdObj = new NumberObject(NumberObject.INTEGER);
        itemIdObj.setValue(itemId);

        f = new TableField();
        f.setValue(componentsController.model);

        // prepare the argument list for the getObject function
        DataObject[] args = new DataObject[] {itemIdObj, f};

        screenService.getObject("getComponentsModel", args, new AsyncCallback() {
            public void onSuccess(Object result) {
                // get the table model and load it
                // in the table
                rpc.setFieldValue("componentsTable",
                                  (TableModel)((TableField)result).getValue());

                componentsController.loadModel((TableModel)((TableField)result).getValue());

                clearComponents = componentsController.model.numRows() > 0;
                
                window.setStatus("","");
            }

            public void onFailure(Throwable caught) {
                Window.alert(caught.getMessage());
            }
        });
    }
    
    private void fillLocationsModel(){
        Integer itemId = null;
        NumberObject itemIdObj;
        TableField f;

        if (key == null || key.getKey() == null) {
            clearLocations = false;
            return;
        }

        window.setStatus("","spinnerIcon");
        
        itemId = (Integer)key.getKey().getValue();
        itemIdObj = new NumberObject(NumberObject.INTEGER);
        itemIdObj.setValue(itemId);

        f = new TableField();
        f.setValue(locsController.model);

        // prepare the argument list for the getObject function
        DataObject[] args = new DataObject[] {itemIdObj, f};

        screenService.getObject("getLocationsModel", args, new AsyncCallback() {
            public void onSuccess(Object result) {
                // get the table model and load it
                // in the table
                rpc.setFieldValue("locQuantitiesTable",
                                  (TableModel)((TableField)result).getValue());

                locsController.loadModel((TableModel)((TableField)result).getValue());

                clearLocations = locsController.model.numRows() > 0;
                
                window.setStatus("","");
            }

            public void onFailure(Throwable caught) {
                Window.alert(caught.getMessage());
            }
        });
    }
    
    private void fillCommentsModel(){
        Integer itemId = null;
        boolean getModel = false;
         
         // access the database only if id is not null  
         if(key!=null && key.getKey()!=null){        
             getModel = true;
             itemId = (Integer)key.getKey().getValue(); 
          
         }else{
             clearComments = false;
         } 
            
           if(getModel){ 
               window.setStatus("","spinnerIcon");
               NumberObject itemIdObj = new NumberObject(NumberObject.INTEGER);
               itemIdObj.setValue(itemId);
                 
               // prepare the argument list for the getObject function
               DataObject[] args = new DataObject[] {itemIdObj}; 
                 
               screenService.getObject("getCommentsModel", args, new AsyncCallback(){
                   public void onSuccess(Object result){    
                     // get the datamodel, load it in the comments panel and set the value in the rpc
                       String xmlString = (String) ((StringObject)result).getValue();
                       svp.load(xmlString);   
                       
                       if(((VerticalPanel)svp.getPanel()).getWidgetCount() > 0){
                           clearComments = true;
                        }else {
                            clearComments = false;
                        }
                       window.setStatus("","");
                   }
                   
                   public void onFailure(Throwable caught){
                       Window.alert(caught.getMessage());
                   }
               }); 
         }       
    }
    
    private void clearComponents() {
        componentsController.model.reset();
        componentsController.setModel(componentsController.model);
        rpc.setFieldValue("componentsTable", componentsController.model);
    }
    
    private void clearLocations() {
        locsController.model.reset();
        locsController.setModel(locsController.model);
        rpc.setFieldValue("locQuantitiesTable", locsController.model);
    }
	
    private void clearComments() {
        svp = (ScreenVertical)widgets.get("notesPanel");
        svp.clear();
    }
    
	private void getInventories(String letter, Widget sender) {
		if (state == FormInt.DISPLAY || state == FormInt.DEFAULT) {

			FormRPC letterRPC = (FormRPC) this.forms.get("queryByLetter");
			letterRPC.setFieldValue("inventoryItem.name", letter.toUpperCase() + "*");

			commitQuery(letterRPC);
		}
	}
	
	private void onStandardNoteButtonClick(){
   	 	PopupPanel standardNotePopupPanel = new PopupPanel(false,true);
		ScreenWindow pickerWindow = new ScreenWindow(standardNotePopupPanel, "Choose Standard Note", "standardNotePicker", "Loading...");
		pickerWindow.setContent(new StandardNotePickerScreen((TextArea)getWidget("note.text")));
			
		standardNotePopupPanel.add(pickerWindow);
		int left = this.getAbsoluteLeft();
		int top = this.getAbsoluteTop();
		standardNotePopupPanel.setPopupPosition(left,top);
		standardNotePopupPanel.show();
     }

}
