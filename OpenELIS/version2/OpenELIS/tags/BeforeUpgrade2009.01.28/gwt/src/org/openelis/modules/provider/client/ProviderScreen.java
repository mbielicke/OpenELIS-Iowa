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


import org.openelis.gwt.common.FormRPC;
import org.openelis.gwt.common.data.Data;
import org.openelis.gwt.common.data.DataModel;
import org.openelis.gwt.common.data.DataSet;
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

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.SourcesTabEvents;
import com.google.gwt.user.client.ui.TabListener;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class ProviderScreen extends OpenELISScreenForm implements ClickListener, 
                                                                  TabListener,
                                                                  TableManager{
         
    private ScreenVertical svp = null;
    private AppButton removeContactButton, standardNoteButton;
    private ScreenTextBox provId = null; 
    private TextBox lastName = null;
    private ScreenTextArea noteArea = null;
    private TableWidget provAddController = null;    
    private Dropdown displayType = null;
    private KeyListManager keyList = new KeyListManager();
    
    
    private static boolean loaded = false;

    private static DataModel typeDropDown = null;
    private static DataModel stateDropDown = null;  
    private static DataModel countryDropDown = null; 
    
    private ProviderMetaMap ProvMeta = new ProviderMetaMap(); 
    
    public ProviderScreen(){
        super("org.openelis.modules.provider.server.ProviderService",!loaded);
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
        loaded = true;
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
       if(typeDropDown == null){
         typeDropDown = (DataModel) initData.get("providers");
         stateDropDown = (DataModel) initData.get("states");
         countryDropDown = (DataModel) initData.get("countries");
        } 
                                
       displayType.setModel(typeDropDown);                                
    
       ScreenTableWidget displayAddressTable = (ScreenTableWidget)widgets.get("providerAddressTable");
       provAddController = (TableWidget)displayAddressTable.getWidget();
       QueryTable queryContactTable = (QueryTable)displayAddressTable.getQueryWidget().getWidget();
       
       ((TableDropdown)provAddController.columns.get(5).getColumnWidget()).setModel(stateDropDown);
       ((TableDropdown)queryContactTable.columns.get(5).getColumnWidget()).setModel(stateDropDown);
       
       
       ((TableDropdown)provAddController.columns.get(6).getColumnWidget()).setModel(countryDropDown);
       ((TableDropdown)queryContactTable.columns.get(6).getColumnWidget()).setModel(countryDropDown);                      
  
       updateChain.add(afterUpdate);
       
       super.afterDraw(success);
       
       ((FormRPC)rpc.getField("addresses")).setFieldValue("providerAddressTable",
                                                         provAddController.model.getData());

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
    
        //    set focus to the last name field
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
            if (index == 0 && !((FormRPC)rpc.getField("addresses")).load) {
                fillAddressModel();
            } else if (index == 1 && !((FormRPC)rpc.getField("notes")).load) {
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
        return row.get(0).getValue() != null && !row.get(0).getValue().equals("");
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
            FormRPC letterRPC = (FormRPC) this.forms.get("queryByLetter");
            
            letterRPC.setFieldValue(ProvMeta.getLastName(), query);            
            commitQuery(letterRPC); 
       }
    }
    
   private void fillNotesModel(){  
       
       if(key == null)
           return;
       
       window.setStatus("","spinnerIcon");
       
       screenService.getObject("loadNotes", new Data[] {key,rpc.getField("notes")}, new AsyncCallback<FormRPC>(){
           public void onSuccess(FormRPC result){    
               // get the datamodel, load it in the notes panel and set the value in the rpc
               load(result);
               rpc.setField("notes",result);
               window.setStatus("","");
           }
                  
           public void onFailure(Throwable caught){
               Window.alert(caught.getMessage());
               window.setStatus("","");
           }
       });       
   }
   
   private void fillAddressModel(){
       if(key == null)
           return;
       
       window.setStatus("","spinnerIcon");
       
       screenService.getObject("loadAddresses", new Data[] {key,rpc.getField("addresses")}, new AsyncCallback<FormRPC>() {
           public void onSuccess(FormRPC result) {
              
               load(result);
               rpc.setField("addresses", result);
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
