/** Exhibit A - UIRF Open-source Based Public Software License.
* 
* The contents of this file are subject to the UIRF Open-source Based
* Public Software License(the "License"); you may not use this file except
* in compliance with the License. You may obtain a copy of the License at
* openelis.uhl.uiowa.edu
* 
* Software distributed under the License is distributed on an "AS IS"
* basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
* License for the specific language governing rights and limitations
* under the License.
* 
* The Original Code is OpenELIS code.
* 
* The Initial Developer of the Original Code is The University of Iowa.
* Portions created by The University of Iowa are Copyright 2006-2008. All
* Rights Reserved.
* 
* Contributor(s): ______________________________________.
* 
* Alternatively, the contents of this file marked
* "Separately-Licensed" may be used under the terms of a UIRF Software
* license ("UIRF Software License"), in which case the provisions of a
* UIRF Software License are applicable instead of those above. 
*/
package org.openelis.modules.provider.client;


import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.SourcesTabEvents;
import com.google.gwt.user.client.ui.TabListener;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import org.openelis.gwt.common.DefaultRPC;
import org.openelis.gwt.common.Form;
import org.openelis.gwt.common.data.DataModel;
import org.openelis.gwt.common.data.DataObject;
import org.openelis.gwt.common.data.DataSet;
import org.openelis.gwt.common.data.FieldType;
import org.openelis.gwt.common.data.IntegerObject;
import org.openelis.gwt.common.data.KeyListManager;
import org.openelis.gwt.screen.CommandChain;
import org.openelis.gwt.screen.ScreenTableWidget;
import org.openelis.gwt.screen.ScreenTextArea;
import org.openelis.gwt.screen.ScreenTextBox;
import org.openelis.gwt.screen.ScreenVertical;
import org.openelis.gwt.screen.ScreenWindow;
import org.openelis.gwt.widget.AToZTable;
import org.openelis.gwt.widget.AppButton;
import org.openelis.gwt.widget.ButtonPanel;
import org.openelis.gwt.widget.CollapsePanel;
import org.openelis.gwt.widget.Dropdown;
import org.openelis.gwt.widget.FormInt;
import org.openelis.gwt.widget.table.QueryTable;
import org.openelis.gwt.widget.table.TableDropdown;
import org.openelis.gwt.widget.table.TableManager;
import org.openelis.gwt.widget.table.TableWidget;
import org.openelis.metamap.ProviderMetaMap;
import org.openelis.modules.main.client.OpenELISScreenForm;
import org.openelis.modules.standardnotepicker.client.StandardNotePickerScreen;

public class ProviderScreen extends OpenELISScreenForm<ProviderRPC,ProviderForm,Integer> implements ClickListener, 
                                                                  TabListener,
                                                                  TableManager{
         
    private ScreenVertical svp = null;
    private AppButton removeContactButton, standardNoteButton;
    private ScreenTextBox provId = null; 
    private TextBox lastName = null;
    private ScreenTextArea noteArea = null;
    private TableWidget provAddController = null;  
    private QueryTable queryContactTable = null;
    private Dropdown displayType = null;
    private KeyListManager keyList = new KeyListManager();       
    
    private ProviderMetaMap ProvMeta = new ProviderMetaMap(); 
    
    /*
     * This callback is used to check the returned RPC for updated DataModels for the dropdown
     * widgets on the screen.  It is inserted at the front of the call chain.
     * 
     * if model is returned set it to the widgets and make sure to null the rpc field 
     * so it is not sent back with future RPC calls
     */
    AsyncCallback<ProviderRPC> checkModels = new AsyncCallback<ProviderRPC>() {
        public void onSuccess(ProviderRPC rpc) {
            if(rpc.providerTypes != null) {
                setProviderTypesModel(rpc.providerTypes);
                rpc.providerTypes = null;
            }
            if(rpc.countries != null) {
                setCountriesModel(rpc.countries);
                rpc.countries = null;
            }
            if(rpc.states != null) {
                setStatesModel(rpc.states);
                rpc.states = null;
            }
        }
        
        public void onFailure(Throwable caught) {
            
        }
    };
    
    public ProviderScreen(){
        super("org.openelis.modules.provider.server.ProviderService");      
        forms.put("display",new ProviderForm());        
        getScreen(new ProviderRPC());        
    }
    
    public void performCommand(Enum action, Object obj) {
        if(obj instanceof AppButton){           
           String baction = ((AppButton)obj).action;           
           if(baction.startsWith("query:")){
               getProviders(baction.substring(6, baction.length()));      
           }else
               super.performCommand(action, obj);
        }else{
            if(action == State.ADD ||action == State.UPDATE){
                provAddController.model.enableAutoAdd(true);                
            }else{
                provAddController.model.enableAutoAdd(false);
            }
            super.performCommand(action, obj);
        }
    }

    public void onClick(Widget sender) {
        if (sender == removeContactButton)
            onRemoveRowButtonClick();
        else if (sender == standardNoteButton)
            onStandardNoteButtonClick();    
    }

    public void afterDraw(boolean success) {
        AToZTable atozTable = (AToZTable) getWidget("azTable");
        ButtonPanel bpanel = (ButtonPanel) getWidget("buttons");
        ButtonPanel atozButtons = (ButtonPanel)getWidget("atozButtons");
        
        CommandChain chain = new CommandChain();
        chain.addCommand(this);
        chain.addCommand(bpanel);
        chain.addCommand(atozTable);
        chain.addCommand(atozButtons);
        chain.addCommand(keyList);
        
        ((CollapsePanel)getWidget("collapsePanel")).addChangeListener(atozTable);
                   
        //load other widgets
        removeContactButton = (AppButton) getWidget("removeAddressButton");        
        standardNoteButton = (AppButton) getWidget("standardNoteButton");
        
        provId = (ScreenTextBox)widgets.get(ProvMeta.getId());
        lastName = (TextBox)getWidget(ProvMeta.getLastName());
        //subjectBox = (TextBox)getWidget(ProvMeta.getNote().getSubject());
        noteArea = (ScreenTextArea)widgets.get(ProvMeta.getNote().getText());
        svp = (ScreenVertical) widgets.get("notesPanel");
        
        //noteTab = (TabPanel)getWidget("provTabPanel");  
        
        displayType = (Dropdown)getWidget(ProvMeta.getTypeId());
        
        provAddController = ((TableWidget)getWidget("providerAddressTable"));
                
        //load dropdowns                                                                         
        ScreenTableWidget displayAddressTable = (ScreenTableWidget)widgets.get("providerAddressTable");
        provAddController = (TableWidget)displayAddressTable.getWidget();
        queryContactTable = (QueryTable)displayAddressTable.getQueryWidget().getWidget();
       
        /*
         * Setting of the models has been split to three methods so that they can be individually updated when needed.
         * 
         * Models are now pulled directly from RPC rather than initData.
         */
        setProviderTypesModel(rpc.providerTypes);
        setCountriesModel(rpc.countries);
        setStatesModel(rpc.states);
        
        /*
         * Null out the rpc models so they are not sent with future rpc calls
         */
        rpc.providerTypes = null;
        rpc.countries = null;
        rpc.states = null;                      
  
       updateChain.add(afterUpdate);
       
       /*
        * Set the CheckModels to the first call back in all chains
        * 
        * It is debatable if the checkmodels needs to be in all call chains
        * at a minimum though it should be in fetchChain and updateChain 
        */
       updateChain.add(0,checkModels);
       fetchChain.add(0,checkModels);
       abortChain.add(0,checkModels);
       commitUpdateChain.add(0,checkModels);
       commitAddChain.add(0,checkModels);
       
       super.afterDraw(success);
       
       rpc.form.addresses.providerAddressTable.setValue(provAddController.model.getData());
    }
    
    public void setProviderTypesModel(DataModel<Integer> typesModel) {
        displayType.setModel(typesModel);
    }
    
    public void setCountriesModel(DataModel<String> countriesModel) {
        ((TableDropdown)provAddController.columns.get(6).getColumnWidget()).setModel(countriesModel);
        ((TableDropdown)queryContactTable.columns.get(6).getColumnWidget()).setModel(countriesModel);
    }
    
    public void setStatesModel(DataModel<String> statesModel) {
        ((TableDropdown)provAddController.columns.get(5).getColumnWidget()).setModel(statesModel);
        ((TableDropdown)queryContactTable.columns.get(5).getColumnWidget()).setModel(statesModel);
    }
       
    protected AsyncCallback afterUpdate = new AsyncCallback() {
        public void onFailure(Throwable caught) {
        }
        public void onSuccess(Object result) {
            
            provId.enable(false);
                                       
            //set focus to the last name field
            lastName.setFocus(true);
        }
           
    };
       
       

    public void query(){
    	//clearNotes();   	       
       super.query();
    
        //set focus to the last name field
        provId.setFocus(true);
        noteArea.enable(false);
    }

    public void add(){                                                  
        svp.clear();
        
        //provAddController.setAutoAdd(true);         
        super.add();     
        
        noteArea.enable(true);
        provId.enable(false);
        
        //set focus to the last name field       
        lastName.setFocus(true);
        
    }

    public void update() {                        
        noteArea.enable(true);
        super.update();      
    }
    

    
    public boolean onBeforeTabSelected(SourcesTabEvents sender, int index) {
        if(state != FormInt.State.QUERY){
            if (index == 0 && !rpc.form.addresses.load) {
                fillAddressModel();
            } else if (index == 1 && !rpc.form.notes.load) {
                fillNotesModel();
            }
        }
        return true;
    }

    public void onTabSelected(SourcesTabEvents sender, int tabIndex) {
    	// TODO Auto-generated method stub
    	
    }
    
    public boolean canAdd(TableWidget widget,DataSet set, int row) {
        // TODO Auto-generated method stub
        return false;
    }



    public boolean canAutoAdd(TableWidget widget,DataSet row) {        
        return ((DataObject)row.get(0)).getValue() != null && !((DataObject)row.get(0)).getValue().equals("");
    }



    public boolean canDelete(TableWidget widget,DataSet set, int row) {
        // TODO Auto-generated method stub
        return false;
    }



    public boolean canEdit(TableWidget widget,DataSet set, int row, int col) {
        return true;
    }



    public boolean canSelect(TableWidget widget,DataSet set, int row) {
        if(state == FormInt.State.ADD || state == FormInt.State.UPDATE || state == FormInt.State.QUERY){      
            return true;
        } 
        return false;
    }
    
    private void getProviders(String query) {
        if (state == FormInt.State.DISPLAY || state == FormInt.State.DEFAULT) {
            Form letter = (Form) forms.get("queryByLetter");
            
            letter.setFieldValue(ProvMeta.getLastName(), query);            
            commitQuery(letter); 
       }
    }
    
   private void fillNotesModel() {  
       DefaultRPC drpc = null;
       if(key == null)
           return;
       
       window.setStatus("","spinnerIcon");
       
       drpc = new DefaultRPC();
       drpc.key = key;
       drpc.form = rpc.form.notes;
       
       screenService.call("loadNotes", drpc, new AsyncCallback<DefaultRPC>(){
           public void onSuccess(DefaultRPC result){    
               // get the datamodel, load it in the notes panel and set the value in the rpc
               load(result.form);
               rpc.form.setField("notes",rpc.form.notes = (NotesForm)result.form);
               window.setStatus("","");
           }
                  
           public void onFailure(Throwable caught){
               Window.alert(caught.getMessage());
               window.setStatus("","");
           }
       });       
   }
   
   private void fillAddressModel() {
       DefaultRPC drpc = null;
       
       if(key == null)
           return;
       
       window.setStatus("","spinnerIcon");
       
       drpc = new DefaultRPC();
       drpc.key = key;
       drpc.form = rpc.form.addresses;
       
       screenService.call("loadAddresses", drpc, new AsyncCallback<DefaultRPC>() {
           public void onSuccess(DefaultRPC result) {              
               load(result.form);
               rpc.form.setField("addresses",rpc.form.addresses = (AddressesForm)result.form);
               window.setStatus("","");
           }
           public void onFailure(Throwable caught) {
               Window.alert(caught.getMessage());
               window.setStatus("","");
           }
       });
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
    
    private void onRemoveRowButtonClick(){
        provAddController.model.deleteRow(provAddController.model.getData().getSelectedIndex());          
    }

    public boolean canDrag(TableWidget widget, DataSet item, int row) {
        // TODO Auto-generated method stub
        return false;
    }

    public boolean canDrop(TableWidget widget, Widget dragWidget, DataSet dropTarget, int targetRow) {
        // TODO Auto-generated method stub
        return false;
    }

    public void drop(TableWidget widget, Widget dragWidget, DataSet dropTarget, int targetRow) {
        // TODO Auto-generated method stub
        
    }

    public void drop(TableWidget widget, Widget dragWidget) {
        // TODO Auto-generated method stub
        
    }
}
