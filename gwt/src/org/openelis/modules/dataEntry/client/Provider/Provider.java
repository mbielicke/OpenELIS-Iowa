package org.openelis.modules.dataEntry.client.Provider;

import org.openelis.gwt.common.FormRPC;
import org.openelis.gwt.common.data.DataModel;
import org.openelis.gwt.common.data.StringField;
import org.openelis.gwt.common.data.TableRow;
import org.openelis.gwt.screen.AppScreenForm;
import org.openelis.gwt.screen.ScreenAutoDropdown;
import org.openelis.gwt.screen.ScreenTableWidget;
import org.openelis.gwt.widget.AppButton;
import org.openelis.gwt.widget.AutoCompleteDropdown;
import org.openelis.gwt.widget.ButtonPanel;
import org.openelis.gwt.widget.FormInt;
import org.openelis.gwt.widget.table.TableAutoDropdown;
import org.openelis.gwt.widget.table.TableController;
import org.openelis.gwt.widget.table.TableWidget;
import org.openelis.modules.utilities.client.standardNotePicker.StandardNotePickerScreen;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.ServiceDefTarget;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SourcesTabEvents;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class Provider extends AppScreenForm{
    //private ConstantsWithLookup openElisConstants = (ConstantsWithLookup) AppScreen.getWidgetMap().get("AppConstants");
    
    private static ProviderServletIntAsync screenService = (ProviderServletIntAsync) GWT
    .create(ProviderServletInt.class);
    
    private static ServiceDefTarget target = (ServiceDefTarget) screenService;

    private Widget selected;
    private boolean shownotes = false;
   // private int tabSelectedIndex = 0;    
   
    public Provider(){
        super();
        String base = GWT.getModuleBaseURL();
        base += "ProviderServlet";
        target.setServiceEntryPoint(base);
        service = screenService;
        formService = screenService;        
        getXML();
    }
    
    public void afterDraw(boolean success) {
        bpanel = (ButtonPanel) getWidget("buttons");        
        message.setText("done");
        
        TableWidget provideNamesTable = (TableWidget) getWidget("providersTable");
        modelWidget.addChangeListener(provideNamesTable.controller);
        
        ((ProviderNamesTable) provideNamesTable.controller.manager).setProviderForm(this);
        
        //Button removeContactButton = (Button) getWidget("removeAddressButton");
        //removeContactButton.setEnabled(false);      
        
        AppButton removeContactButton = (AppButton) getWidget("removeAddressButton");
        removeContactButton.addClickListener(this);
        removeContactButton.changeState(AppButton.DISABLED);
        
        AppButton standardNote = (AppButton) getWidget("standardNoteButton");
        standardNote.changeState(AppButton.DISABLED);
        
        TableController provAddTable = (TableController)(((TableWidget)getWidget("providerAddressTable")).controller);
        provAddTable.setAutoAdd(false);
                
        /*        
        randomTree = (ScreenPagedTree) widgets.get("notesTree");
        vp = (VerticalPanel)randomTree.getParent();
        randomTree.controller.setTreeListener(this);
               
        String woutPx = randomTree.controller.view.height.replaceAll("px", "");
        int intH = new Integer(woutPx).intValue() + 150;
      
         ScrollPanel scrollableView = (ScrollPanel)randomTree.controller.getScrollableView("100%", new Integer(intH).toString()+"px");              
         vp.add(scrollableView);*/     
        loadDropdowns();
        super.afterDraw(success);
        
        bpanel.setButtonState("prev", AppButton.DISABLED);
        bpanel.setButtonState("next", AppButton.DISABLED);
    }
      
   
        
    public void up(int state) {      
        StringField note = (StringField)rpc.getField("usersNote");  
        note.setValue("");
        
        StringField subject = (StringField)rpc.getField("usersSubject");  
        subject.setValue("");
        
        TableController provAddTable = (TableController)(((TableWidget)getWidget("providerAddressTable")).controller);
        provAddTable.setAutoAdd(true);
        
        super.up(state);      
    }
    
    public void abort(int state){
      if(shownotes){
          FormRPC displayRPC = (FormRPC) this.forms.get("display");        
          DataModel notesModel = (DataModel)displayRPC.getFieldValue("notesModel");         
          
          loadNotes(notesModel);
          shownotes = false;
      }
      TableController provAddTable = (TableController)(((TableWidget)getWidget("providerAddressTable")).controller);
      provAddTable.setAutoAdd(false);      
       super.abort(state); 
       
      // need to get the provider name table model
       TableWidget catNameTM = (TableWidget) getWidget("providersTable");
       int rowSelected = catNameTM.controller.selected;               

       // set the update button if needed
       if (rowSelected == -1){
           bpanel.setButtonState("update", AppButton.DISABLED);
           bpanel.setButtonState("prev", AppButton.DISABLED);
           bpanel.setButtonState("next", AppButton.DISABLED);
       }
    }
    
    public void add(int state){        
        
       // Button removeContactButton = (Button) getWidget("removeAddressButton");
       // removeContactButton.setEnabled(true);
        AppButton removeContactButton = (AppButton) getWidget("removeAddressButton");
        removeContactButton.changeState(AppButton.UNPRESSED);
        
        AppButton standardNote = (AppButton) getWidget("standardNoteButton");
        standardNote.changeState(AppButton.UNPRESSED);
        
        VerticalPanel vp = (VerticalPanel) getWidget("notesPanel");        
        vp.clear();
        
        TableController provAddTable = (TableController)(((TableWidget)getWidget("providerAddressTable")).controller);
        provAddTable.setAutoAdd(true);
        
        super.add(state);     
        
//      set focus to the last name field
		TextBox lastName = (TextBox)getWidget("lastName");
		lastName.setFocus(true);
        
        TableWidget catNameTM = (TableWidget) getWidget("providersTable");
        catNameTM.controller.unselect(-1);
    }
    
    public void afterUpdate(boolean success) {
        super.afterUpdate(success);
        
        //Button removeContactButton = (Button) getWidget("removeAddressButton");
        //removeContactButton.setEnabled(true);
        AppButton removeContactButton = (AppButton) getWidget("removeAddressButton");
        removeContactButton.changeState(AppButton.UNPRESSED);
        
        AppButton standardNote = (AppButton) getWidget("standardNoteButton");
        standardNote.changeState(AppButton.UNPRESSED);
        
        shownotes = true;
        
//      set focus to the last name field
		TextBox lastName = (TextBox)getWidget("lastName");
		lastName.setFocus(true);
    }
    
    public void query(int state){
      VerticalPanel vp = (VerticalPanel) getWidget("notesPanel");        
      vp.clear();
      shownotes = true;
      
      super.query(state);
      
//    set focus to the last name field
		TextBox lastName = (TextBox)getWidget("lastName");
		lastName.setFocus(true);
        
         AppButton removeContactButton = (AppButton) getWidget("removeAddressButton");
         removeContactButton.changeState(AppButton.DISABLED);
         
         AppButton standardNote = (AppButton) getWidget("standardNoteButton");
         standardNote.changeState(AppButton.DISABLED);
    }
       
    
    public void onClick(Widget sender) {
        if (sender == getWidget("a")) {
            getProviders("a", sender);
        } else if (sender == getWidget("b")) {
            getProviders("b", sender);
        } else if (sender == getWidget("c")) {
            getProviders("c", sender);
        } else if (sender == getWidget("d")) {
            getProviders("d", sender);
        } else if (sender == getWidget("e")) {
            getProviders("e", sender);
        } else if (sender == getWidget("f")) {
            getProviders("f", sender);
        } else if (sender == getWidget("g")) {
            getProviders("g", sender);
        } else if (sender == getWidget("h")) {
            getProviders("h", sender);
        } else if (sender == getWidget("i")) {
            getProviders("i", sender);
        } else if (sender == getWidget("j")) {
            getProviders("j", sender);
        } else if (sender == getWidget("k")) {
            getProviders("k", sender);
        } else if (sender == getWidget("l")) {
            getProviders("l", sender);
        } else if (sender == getWidget("m")) {
            getProviders("m", sender);
        } else if (sender == getWidget("n")) {
            getProviders("n", sender);
        } else if (sender == getWidget("o")) {
            getProviders("o", sender);
        } else if (sender == getWidget("p")) {
            getProviders("p", sender);
        } else if (sender == getWidget("q")) {
            getProviders("q", sender);
        } else if (sender == getWidget("r")) {
            getProviders("r", sender);
        } else if (sender == getWidget("s")) {
            getProviders("s", sender);
        } else if (sender == getWidget("t")) {
            getProviders("t", sender);
        } else if (sender == getWidget("u")) {
            getProviders("u", sender);
        } else if (sender == getWidget("v")) {
            getProviders("v", sender);
        } else if (sender == getWidget("w")) {
            getProviders("w", sender);
        } else if (sender == getWidget("x")) {
            getProviders("x", sender);
        } else if (sender == getWidget("y")) {
            getProviders("y", sender);
        } else if (sender == getWidget("z")) {
            getProviders("z", sender);
        }else if (sender == getWidget("removeAddressButton")) {                     
            TableWidget provAddTable = (TableWidget) getWidget("providerAddressTable");
            int selectedRow = provAddTable.controller.selected;
            if (selectedRow > -1
                    && provAddTable.controller.model.numRows() > 1) {
                TableRow row = provAddTable.controller.model
                        .getRow(selectedRow);
                
                row.setShow(false);
                // delete the last row of the table because it is autoadd
                provAddTable.controller.model
                        .deleteRow(provAddTable.controller.model.numRows() - 1);
                // reset the model
                provAddTable.controller.reset();
                // need to set the deleted flag to "Y" also
                StringField deleteFlag = new StringField();
                deleteFlag.setValue("Y");

                row.addHidden("deleteFlag", deleteFlag);
            }  
        }else if(sender == getWidget("standardNoteButton")){
            new StandardNotePickerScreen((TextArea)getWidget("usersNote"));
        }        
    }
        
    
    private void getProviders(String letter, Widget sender) {
        // we only want to allow them to select a letter if they are in display
        // mode..
        if (bpanel.getState() == FormInt.DISPLAY) {

            FormRPC letterRPC = (FormRPC) this.forms.get("queryByLetter");
            letterRPC.setFieldValue("lastName", letter.toUpperCase() + "*");
             
            commitQuery(letterRPC);
            
            setStyleNameOnButton(sender);
            
        }
    }
    
    protected Widget setStyleNameOnButton(Widget sender) {
        sender.addStyleName("current");
        if (selected != null)
            selected.removeStyleName("current");
        selected = sender;
        return sender;
    }

    public void onTabSelected(SourcesTabEvents sources, int index) {
        //tabSelectedIndex = index;
        // we need to do a provider addresses table reset so that it will always show
        // the data
        if (index == 0 && bpanel.getState() == FormInt.DISPLAY) {
            TableWidget contacts = (TableWidget) getWidget("providerAddressTable");
            //if(contacts.controller.model.numRows()>0)
            // contacts.controller.model.deleteRow(contacts.controller.model.numRows() - 1);
            contacts.controller.reset();
        }
        super.onTabSelected(sources, index);
    }    
        
    
    public void afterFetch(boolean success) {
        
        super.afterFetch(success);
        //Window.alert("afterFetch");
        FormRPC displayRPC = (FormRPC) this.forms.get("display");        
        DataModel notesModel = (DataModel)displayRPC.getFieldValue("notesModel");         
        
        loadNotes(notesModel);
        
    }
    
    public void commitAdd(){
        TableController provAddController = (TableController)(((TableWidget)getWidget("providerAddressTable")).controller);
        provAddController.unselect(-1);
        super.commitAdd();      
        
        //Button removeContactButton = (Button) getWidget("removeAddressButton");
        //removeContactButton.setEnabled(false);
        AppButton removeContactButton = (AppButton) getWidget("removeAddressButton");
        removeContactButton.changeState(AppButton.DISABLED);
        
        AppButton standardNote = (AppButton) getWidget("standardNoteButton");
        standardNote.changeState(AppButton.DISABLED);
        
        provAddController.setAutoAdd(false);      
    }
    
    public void commitUpdate(){ 
        TableController provAddController = (TableController)(((TableWidget)getWidget("providerAddressTable")).controller);
        provAddController.unselect(-1);
        
        super.commitUpdate();                  
       // Button removeContactButton = (Button) getWidget("removeAddressButton");
       // removeContactButton.setEnabled(false);
        AppButton removeContactButton = (AppButton) getWidget("removeAddressButton");
        removeContactButton.changeState(AppButton.DISABLED);
        
        AppButton standardNote = (AppButton) getWidget("standardNoteButton");
        standardNote.changeState(AppButton.DISABLED);
                
        provAddController.setAutoAdd(false);      
    }
    
    public void afterCommitUpdate(boolean success){
        FormRPC displayRPC = (FormRPC) this.forms.get("display");        
        DataModel notesModel = (DataModel)displayRPC.getFieldValue("notesModel");         
        
        loadNotes(notesModel);
        super.afterCommitUpdate(success);
    }
    
    public void afterCommitAdd(boolean success){
        FormRPC displayRPC = (FormRPC) this.forms.get("display");        
        DataModel notesModel = (DataModel)displayRPC.getFieldValue("notesModel");         
        
        loadNotes(notesModel);
        super.afterCommitAdd(success);
    }
    
    
    public void commitQuery(FormRPC rpcQuery){
        TableController provAddController = (TableController)(((TableWidget)getWidget("providerAddressTable")).controller);
        provAddController.unselect(-1);
        
        super.commitQuery(rpcQuery);
    }

    private void loadNotes(DataModel notesModel){       
                     
        VerticalPanel vp = (VerticalPanel) getWidget("notesPanel");
                  
                  //we need to remove anything in the notes tab if it exists
                  vp.clear();
                  int i=0;
                  if(notesModel != null){ 
                      
                    while(i<notesModel.size()){
                      HorizontalPanel subjectPanel = new HorizontalPanel();
                      HorizontalPanel spacerPanel = new HorizontalPanel();
                      HorizontalPanel bodyPanel = new HorizontalPanel();
                      
                      Label subjectLabel = new Label();
                      Label bodyLabel = new Label();
                      
                      vp.add(subjectPanel);
                      vp.add(bodyPanel);
                      subjectPanel.add(subjectLabel);
                      bodyPanel.add(spacerPanel);
                      bodyPanel.add(bodyLabel);           
                      
                      spacerPanel.setWidth("25px");
                      subjectPanel.setWidth("100%");
                      bodyPanel.setWidth("100%");
                      
                      subjectLabel.addStyleName("NotesText");
                      bodyLabel.addStyleName("NotesText");
                      
                      subjectLabel.setWordWrap(true);
                      bodyLabel.setWordWrap(true);
                      
                      subjectLabel.setText((String)notesModel.get(i).getObject(0).getValue());
                      bodyLabel.setText((String)notesModel.get(i).getObject(1).getValue());
                      
                      i++;
                  }
                 } 
              }
                 
    private void loadDropdowns(){
        
        screenService.getInitialModel("providerType", new AsyncCallback(){
            public void onSuccess(Object result){
                DataModel typeDataModel = (DataModel)result;
                ScreenAutoDropdown displayType = (ScreenAutoDropdown)widgets.get("providerType");
                ScreenAutoDropdown queryType = displayType.getQueryWidget();
                
                ((AutoCompleteDropdown)displayType.getWidget()).setModel(typeDataModel);
                ((AutoCompleteDropdown)queryType.getWidget()).setModel(typeDataModel);
                                       
            }
            public void onFailure(Throwable caught){
                Window.alert(caught.getMessage());
            }
         });
 
        //load state dropdowns
        screenService.getInitialModel("state", new AsyncCallback(){
               public void onSuccess(Object result){
                   DataModel stateDataModel = (DataModel)result;                   
                   
                   ScreenTableWidget displayAddressTable = (ScreenTableWidget)widgets.get("providerAddressTable");
                   ScreenTableWidget queryAddressTable = (ScreenTableWidget)displayAddressTable.getQueryWidget();
                   
                   TableAutoDropdown displayContactState = (TableAutoDropdown)((TableWidget)displayAddressTable.getWidget()).
                                                                                                controller.editors[5];
                   displayContactState.setModel(stateDataModel);
                   
                   TableAutoDropdown queryContactState = (TableAutoDropdown)((TableWidget)queryAddressTable.getWidget()).
                        controller.editors[5];
                   queryContactState.setModel(stateDataModel);
               }
               public void onFailure(Throwable caught){
                   Window.alert(caught.getMessage());
               }
            });
        
        //load country dropdowns
        screenService.getInitialModel("country", new AsyncCallback(){
               public void onSuccess(Object result){
                   DataModel countryDataModel = (DataModel)result;                   
                   
                   ScreenTableWidget displayAddressTable = (ScreenTableWidget)widgets.get("providerAddressTable");
                   ScreenTableWidget queryAddressTable = (ScreenTableWidget)displayAddressTable.getQueryWidget();
                                      
                   
                   TableAutoDropdown displayContactCountry = (TableAutoDropdown)((TableWidget)displayAddressTable.getWidget()).
                                                                                                controller.editors[6];
                   displayContactCountry.setModel(countryDataModel);
                   
                   TableAutoDropdown queryContactCountry = (TableAutoDropdown)((TableWidget)queryAddressTable.getWidget()).
                        controller.editors[6];
                   queryContactCountry.setModel(countryDataModel);
               }
               public void onFailure(Throwable caught){
                   Window.alert(caught.getMessage());
               }
            });
    } 
    
   public boolean validate(){
       TableController provAddController = (TableController)(((TableWidget)getWidget("providerAddressTable")).controller);
       provAddController.unselect(-1);
       boolean noLocErrors =  true;
       
       for(int iter = 0; iter < provAddController.model.numRows(); iter++){
           StringField snfield = (StringField)provAddController.model.getFieldAt(iter, 0);         
                      
           if(!(snfield.getErrors().length==0)){
               noLocErrors = false;                   
           }
       }  
       
       if(!noLocErrors){
           return false;
       }
       
       return true; 
   }
    
}
