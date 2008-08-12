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

import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

import org.openelis.gwt.common.FormRPC;
import org.openelis.gwt.common.data.DataModel;
import org.openelis.gwt.common.data.KeyListManager;
import org.openelis.gwt.screen.CommandChain;
import org.openelis.gwt.screen.ScreenAutoDropdown;
import org.openelis.gwt.screen.ScreenInputWidget;
import org.openelis.gwt.widget.AToZTable;
import org.openelis.gwt.widget.AppButton;
import org.openelis.gwt.widget.AutoCompleteDropdown;
import org.openelis.gwt.widget.ButtonPanel;
import org.openelis.gwt.widget.CollapsePanel;
import org.openelis.gwt.widget.FormInt;
import org.openelis.metamap.LabelMetaMap;
import org.openelis.modules.main.client.OpenELISScreenForm;

public class LabelScreen extends OpenELISScreenForm implements ClickListener {

    private static boolean loaded = false;
    private static DataModel printerTypeDropDown ;
    private static DataModel scriptletDropdown;
    
    private ScreenAutoDropdown displayPType = null;
    private ScreenAutoDropdown displayScript = null;
    private TextBox nameTextbox;
    private KeyListManager keyList = new KeyListManager();
    
    private LabelMetaMap Meta = new LabelMetaMap(); 
    
    public LabelScreen() {
        super("org.openelis.modules.label.server.LabelService",!loaded);
    }

    public void performCommand(Enum action, Object obj) {
        if(obj instanceof AppButton){           
           String baction = ((AppButton)obj).action;           
           if (baction.startsWith("query:")) {
               getLabels(baction.substring(6, baction.length()));
           }else
               super.performCommand(action, obj);
         }else{
            super.performCommand(action, obj);
         }
    
    }

    public void onClick(Widget arg0) {
        // TODO Auto-generated method stub
        
    }

    public void afterDraw(boolean success) {
        AToZTable atozTable = (AToZTable) getWidget("azTable");
        ButtonPanel atozButtons = (ButtonPanel)getWidget("atozButtons");
        ButtonPanel bpanel = (ButtonPanel)getWidget("buttons");
        
        CommandChain chain = new CommandChain();
        chain.addCommand(this);
        chain.addCommand(bpanel);
        chain.addCommand(keyList);
        chain.addCommand(atozTable);
        chain.addCommand(atozButtons);
        
        ((CollapsePanel)getWidget("collapsePanel")).addChangeListener(atozTable);

        loaded = true;        

        nameTextbox = (TextBox)getWidget(Meta.getName());
        startWidget = (ScreenInputWidget)widgets.get(Meta.getName());
        
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
