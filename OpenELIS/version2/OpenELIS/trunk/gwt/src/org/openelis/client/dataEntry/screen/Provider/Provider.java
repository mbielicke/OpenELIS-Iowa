package org.openelis.client.dataEntry.screen.Provider;

import org.openelis.gwt.client.screen.AppScreenForm;
import org.openelis.gwt.client.widget.AppButton;
import org.openelis.gwt.client.widget.ButtonPanel;
import org.openelis.gwt.client.widget.FormInt;
import org.openelis.gwt.client.widget.table.TableWidget;
import org.openelis.gwt.common.FormRPC;
import org.openelis.gwt.common.data.DataModel;
import org.openelis.gwt.common.data.StringField;
import org.openelis.gwt.common.data.TableRow;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.ServiceDefTarget;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SourcesTabEvents;
import com.google.gwt.user.client.ui.TreeItem;
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
        
        /*ProviderAddressesTable  provAddressTable = (ProviderAddressesTable)((TableWidget) getWidget("providerAddressTable")).controller.manager; 
        /provAddressTable.setProviderForm(this);
        /provAddressTable.disableRows = true;
        
        randomTree = (ScreenPagedTree) widgets.get("notesTree");
        vp = (VerticalPanel)randomTree.getParent();
        randomTree.controller.setTreeListener(this);
               
        String woutPx = randomTree.controller.view.height.replaceAll("px", "");
        int intH = new Integer(woutPx).intValue() + 150;
      
         ScrollPanel scrollableView = (ScrollPanel)randomTree.controller.getScrollableView("100%", new Integer(intH).toString()+"px");              
         vp.add(scrollableView);*/     
        super.afterDraw(success);
    }
      
   
        
    public void up(int state) {      
        StringField note = (StringField)rpc.getField("usersNote");  
        note.setValue("");
        
        StringField subject = (StringField)rpc.getField("usersSubject");  
        subject.setValue("");
        super.up(state);      
    }
    
    public void abort(int state){
      if(shownotes){
          loadNotes();
          shownotes = false;
      } 
       super.abort(state); 
    }
    
    public void add(int state){        
        
       // Button removeContactButton = (Button) getWidget("removeAddressButton");
       // removeContactButton.setEnabled(true);
        AppButton removeContactButton = (AppButton) getWidget("removeAddressButton");
        removeContactButton.changeState(AppButton.UNPRESSED);
        
        ProviderAddressesTable  provAddressTable = (ProviderAddressesTable)((TableWidget) getWidget("providerAddressTable")).controller.manager; 
        provAddressTable.disableRows = false;
        
        VerticalPanel vp = (VerticalPanel) getWidget("notesPanel");
        
        vp.clear();
        super.add(state);      
    }
    
    public void afterUpdate(boolean success) {
        super.afterUpdate(success);
        
        //Button removeContactButton = (Button) getWidget("removeAddressButton");
        //removeContactButton.setEnabled(true);
        AppButton removeContactButton = (AppButton) getWidget("removeAddressButton");
        removeContactButton.changeState(AppButton.UNPRESSED);
        
        ProviderAddressesTable  provAddressTable = (ProviderAddressesTable)((TableWidget) getWidget("providerAddressTable")).controller.manager; 
        provAddressTable.disableRows = false;
        
        shownotes = true;
    }
    
    public void query(int state){
      VerticalPanel vp = (VerticalPanel) getWidget("notesPanel");        
      vp.clear();
      shownotes = true;
      super.query(state);
    }
       
    
    public void onClick(Widget sender) {
        if (sender == widgets.get("a")) {
            getProviders("a", sender);
        } else if (sender == widgets.get("b")) {
            getProviders("b", sender);
        } else if (sender == widgets.get("c")) {
            getProviders("c", sender);
        } else if (sender == widgets.get("d")) {
            getProviders("d", sender);
        } else if (sender == widgets.get("e")) {
            getProviders("e", sender);
        } else if (sender == widgets.get("f")) {
            getProviders("f", sender);
        } else if (sender == widgets.get("g")) {
            getProviders("g", sender);
        } else if (sender == widgets.get("h")) {
            getProviders("h", sender);
        } else if (sender == widgets.get("i")) {
            getProviders("i", sender);
        } else if (sender == widgets.get("j")) {
            getProviders("j", sender);
        } else if (sender == widgets.get("k")) {
            getProviders("k", sender);
        } else if (sender == widgets.get("l")) {
            getProviders("l", sender);
        } else if (sender == widgets.get("m")) {
            getProviders("m", sender);
        } else if (sender == widgets.get("n")) {
            getProviders("n", sender);
        } else if (sender == widgets.get("o")) {
            getProviders("o", sender);
        } else if (sender == widgets.get("p")) {
            getProviders("p", sender);
        } else if (sender == widgets.get("q")) {
            getProviders("q", sender);
        } else if (sender == widgets.get("r")) {
            getProviders("r", sender);
        } else if (sender == widgets.get("s")) {
            getProviders("s", sender);
        } else if (sender == widgets.get("t")) {
            getProviders("t", sender);
        } else if (sender == widgets.get("u")) {
            getProviders("u", sender);
        } else if (sender == widgets.get("v")) {
            getProviders("v", sender);
        } else if (sender == widgets.get("w")) {
            getProviders("w", sender);
        } else if (sender == widgets.get("x")) {
            getProviders("x", sender);
        } else if (sender == widgets.get("y")) {
            getProviders("y", sender);
        } else if (sender == widgets.get("z")) {
            getProviders("z", sender);
        }else if (sender == getWidget("removeAddressButton")) {            
            //Window.alert("remove row");
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
            contacts.controller.model.deleteRow(contacts.controller.model
                    .numRows() - 1);
            contacts.controller.reset();
        }
        super.onTabSelected(sources, index);
    }    
    
    public void onTreeItemStateChanged(TreeItem item) {      
     
       
    }
    
    public void afterFetch(boolean success) {
        
        super.afterFetch(success);
        //Window.alert("afterFetch");
        loadNotes();
        
    }
    
    public void commitAdd(){
        
        super.commitAdd();      
        loadNotes();
        //Button removeContactButton = (Button) getWidget("removeAddressButton");
        //removeContactButton.setEnabled(false);
        AppButton removeContactButton = (AppButton) getWidget("removeAddressButton");
        removeContactButton.changeState(AppButton.DISABLED);
    }
    
    public void commitUpdate(){
       
        super.commitUpdate();
        //Window.alert("commitUpdate"); 
        loadNotes();
        
        
       // Button removeContactButton = (Button) getWidget("removeAddressButton");
       // removeContactButton.setEnabled(false);
        AppButton removeContactButton = (AppButton) getWidget("removeAddressButton");
        removeContactButton.changeState(AppButton.DISABLED);
    }

    private void loadNotes(){       
        FormRPC displayRPC = (FormRPC) this.forms.get("display");
        Integer providerId = (Integer)displayRPC.getFieldValue("providerId");
        //DataModel notesModel = (DataModel)displayRPC.getFieldValue("notesModel");
          screenService.getNotesModel(providerId, new AsyncCallback(){
              public void onSuccess(Object result){
                  DataModel notesModel = (DataModel)result; 
                  VerticalPanel vp = (VerticalPanel) getWidget("notesPanel");
                  
                  //we need to remove anything in the notes tab if it exists
                  vp.clear();
                  int i=0;
                  if(notesModel != null){ 
                      Window.alert(new Integer(notesModel.size()).toString());
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
              public void onFailure(Throwable caught){
                  Window.alert(caught.getMessage());
          
              }
          });
           
    }
}
