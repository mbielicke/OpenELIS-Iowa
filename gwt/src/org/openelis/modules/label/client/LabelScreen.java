/**
* The contents of this file are subject to the Mozilla Public License
* Version 1.1 (the "License"); you may not use this file except in
* compliance with the License. You may obtain a copy of the License at
* http://www.mozilla.org/MPL/
* 
* Software distributed under the License is distributed on an "AS IS"
* basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
* License for the specific language governing rights and limitations under
* the License.
* 
* The Original Code is OpenELIS code.
* 
* Copyright (C) The University of Iowa.  All Rights Reserved.
*/
package org.openelis.modules.label.client;

import org.openelis.gwt.common.FormRPC;
import org.openelis.gwt.common.data.DataModel;
import org.openelis.gwt.screen.ScreenAutoDropdown;
import org.openelis.gwt.widget.AToZPanel;
import org.openelis.gwt.widget.AutoCompleteDropdown;
import org.openelis.gwt.widget.ButtonPanel;
import org.openelis.gwt.widget.FormInt;
import org.openelis.metamap.LabelMetaMap;
import org.openelis.modules.main.client.OpenELISScreenForm;

import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class LabelScreen extends OpenELISScreenForm implements ClickListener {

    private static boolean loaded = false;
    private static DataModel printerTypeDropDown ;
    private static DataModel scriptletDropdown;
    
    private ScreenAutoDropdown displayPType = null;
    private ScreenAutoDropdown displayScript = null;
    private TextBox nameTextbox;
    
    private LabelMetaMap Meta = new LabelMetaMap(); 
    
    public LabelScreen() {
        super("org.openelis.modules.label.server.LabelService",!loaded);
    }

    public void onChange(Widget sender) {
        
        if(sender == getWidget("atozButtons")){           
           String action = ((ButtonPanel)sender).buttonClicked.action;           
           if (action.startsWith("query:")) {
               getLabels(action.substring(6, action.length()));
           } 
         }else{
            super.onChange(sender);
         }
    
    }

    public void onClick(Widget arg0) {
        // TODO Auto-generated method stub
        
    }

    public void afterDraw(boolean success) {       

        loaded = true;        
        setBpanel((ButtonPanel)getWidget("buttons"));

        nameTextbox = (TextBox)getWidget(Meta.getName());
        
        AToZPanel atozTable = (AToZPanel) getWidget("hideablePanel");
        modelWidget.addChangeListener(atozTable);
        addChangeListener(atozTable);
        
        ButtonPanel atozButtons = (ButtonPanel)getWidget("atozButtons");
        atozButtons.addChangeListener(this);
        
        displayPType = (ScreenAutoDropdown)widgets.get(Meta.getPrinterTypeId());
        displayScript = (ScreenAutoDropdown)widgets.get(Meta.getScriptletId());
        
        //load dropdowns
       if(scriptletDropdown == null){
           printerTypeDropDown = (DataModel)initData.get("printer");               
           scriptletDropdown = (DataModel)initData.get("scriptlet");
       }                                             
            
       ((AutoCompleteDropdown)displayPType.getWidget()).setModel(printerTypeDropDown);
       ((AutoCompleteDropdown)displayScript.getWidget()).setModel(scriptletDropdown);
        
        super.afterDraw(success);
    }
    
    public void query() {
        super.query();
        
        nameTextbox.setFocus(true);
    }
    
    public void add() {
        super.add();
        
        nameTextbox.setFocus(true);
    }
    
    public void afterUpdate(boolean success) {
        super.afterUpdate(success);
        
        nameTextbox.setFocus(true);
    }
    
    private void getLabels(String query) {
      // we only want to allow them to select a letter if they are in display
      // mode..
      if (state == FormInt.State.DISPLAY || state == FormInt.State.DEFAULT) {
    
          FormRPC letterRPC = (FormRPC) this.forms.get("queryByLetter");
         
          letterRPC.setFieldValue(Meta.getName(), query);
           
          commitQuery(letterRPC);
          
         
      }
    }      
}
