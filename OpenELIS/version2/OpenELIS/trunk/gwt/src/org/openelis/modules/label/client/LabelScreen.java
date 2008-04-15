package org.openelis.modules.label.client;

import org.openelis.gwt.common.FormRPC;
import org.openelis.gwt.common.data.DataModel;
import org.openelis.gwt.screen.ScreenAutoDropdown;
import org.openelis.gwt.widget.AToZPanel;
import org.openelis.gwt.widget.AppButton;
import org.openelis.gwt.widget.AutoCompleteDropdown;
import org.openelis.gwt.widget.ButtonPanel;
import org.openelis.gwt.widget.FormInt;
import org.openelis.modules.main.client.OpenELISScreenForm;

import com.google.gwt.user.client.ui.Widget;

public class LabelScreen extends OpenELISScreenForm {

    private static boolean loaded = false;
    private static DataModel printerTypeDropDown ;
    private static DataModel scriptletDropdown;
    
    private ScreenAutoDropdown displayPType = null;
    private ScreenAutoDropdown displayScript = null;
    
    public LabelScreen() {
        super("org.openelis.modules.label.server.LabelService",!loaded);
        name = "Label";
    }
    
    private Widget selected;

    public void afterDraw(boolean success) {       

        loaded = true;        
        bpanel = (ButtonPanel)getWidget("buttons");
        message.setText("done");

        AToZPanel atozTable = (AToZPanel) getWidget("hideablePanel");
        modelWidget.addChangeListener(atozTable);
        addChangeListener(atozTable);
        
        ButtonPanel atozButtons = (ButtonPanel)getWidget("atozButtons");
        atozButtons.addChangeListener(this);
        
        displayPType = (ScreenAutoDropdown)widgets.get("label.printerType");
        displayScript = (ScreenAutoDropdown)widgets.get("label.scriptlet");
        
        loadDropdowns();
        
        super.afterDraw(success);
    }
    
      public void onChange(Widget sender) {
        
        if(sender == getWidget("atozButtons")){           
           String action = ((ButtonPanel)sender).buttonClicked.action;           
           if (action.startsWith("query:")) {
               getLabels(action.substring(6, action.length()), ((ButtonPanel)sender).buttonClicked);
           } 
         }else{
            super.onChange(sender);
         }
       
    }
      
      private void getLabels(String letter, Widget sender) {
          // we only want to allow them to select a letter if they are in display
          // mode..
          if (state == FormInt.DISPLAY || state == FormInt.DEFAULT) {

              FormRPC letterRPC = (FormRPC) this.forms.get("queryByLetter");
              if(letter.equals("#"))
                letterRPC.setFieldValue("label.name", "0* | 1* | 2* | 3* | 4* | 5* | 6* | 7* | 8* | 9*");
              else
               letterRPC.setFieldValue("label.name", letter.toLowerCase() + "*");
               
              commitQuery(letterRPC);
              
             
          }
      }
      
      protected void setStyleNameOnButton(Widget sender) {
        ((AppButton)sender).changeState(AppButton.PRESSED);
        if (selected != null)
            ((AppButton)selected).changeState(AppButton.UNPRESSED);
        selected = sender;
      }  

      private void loadDropdowns(){
          
          //load dropdowns
             if(scriptletDropdown == null){
                 printerTypeDropDown = (DataModel)initData[0];               
                 scriptletDropdown = (DataModel)initData[1];
             }                                             
                  ((AutoCompleteDropdown)displayPType.getWidget()).setModel(printerTypeDropDown);
                  ((AutoCompleteDropdown)displayScript.getWidget()).setModel(scriptletDropdown);
                                                            
              }
      
            
}
