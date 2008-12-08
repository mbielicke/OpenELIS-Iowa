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
package org.openelis.modules.method.client;

import org.openelis.gwt.common.FormRPC;
import org.openelis.gwt.common.data.KeyListManager;
import org.openelis.gwt.screen.CommandChain;
import org.openelis.gwt.widget.AToZTable;
import org.openelis.gwt.widget.AppButton;
import org.openelis.gwt.widget.ButtonPanel;
import org.openelis.gwt.widget.CollapsePanel;
import org.openelis.gwt.widget.FormInt;
import org.openelis.metamap.MethodMetaMap;
import org.openelis.modules.main.client.OpenELISScreenForm;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.ChangeListener;
import com.google.gwt.user.client.ui.TextBox;

public class MethodScreen extends OpenELISScreenForm implements ChangeListener{
    private static boolean loaded = false;
    
    private ButtonPanel atozButtons;
    
    private KeyListManager keyList = new KeyListManager();
    
    private MethodMetaMap MethodMeta = new MethodMetaMap();
    
    private TextBox methodName;            
    
    public MethodScreen() {
        super("org.openelis.modules.method.server.MethodService",!loaded);
    }
    
    public void performCommand(Enum action, Object obj) {
        if (obj instanceof AppButton) {
            String baction = ((AppButton)obj).action;
            if (baction.startsWith("query:")) {
                getMethods(baction.substring(6, baction.length()));
            }else
                super.performCommand(action, obj);
        } else{
            super.performCommand(action, obj);
        }
    }
    
    public void afterDraw(boolean success) {
        loaded = true;
        
        AToZTable atozTable;

        //
        // we are interested in getting button actions in two places,
        // modelwidget and us.
        //
        atozTable = (AToZTable)getWidget("azTable");
        ButtonPanel bpanel = (ButtonPanel)getWidget("buttons");
        atozButtons = (ButtonPanel)getWidget("atozButtons");
        
        //methodId = (ScreenTextBox)widgets.get(MethodMeta.getId());
        methodName = (TextBox)getWidget(MethodMeta.getName());
        
        CommandChain formChain = new CommandChain();
        formChain.addCommand(this);
        formChain.addCommand(bpanel);
        formChain.addCommand(keyList);
        formChain.addCommand(atozTable);
        formChain.addCommand(atozButtons);

        bpanel.enableButton("delete", false);
        ((CollapsePanel)getWidget("collapsePanel")).addChangeListener(atozTable);           
         
        updateChain.add(afterUpdate);               
        super.afterDraw(success);
    }
    
    public void query() {        
        super.query();
        //methodId.setFocus(true);
        methodName.setFocus(true);
    }
    
    public void add() {
        super.add();
        //methodId.enable(false);
        methodName.setFocus(true);      
        
    }
  
    
    protected AsyncCallback afterUpdate = new AsyncCallback() {
        public void onFailure(Throwable caught) {   
        }
        public void onSuccess(Object result) {
            //methodId.enable(false);
            methodName.setFocus(true);            
        }
    };
    private void getMethods(String query){
       if (state == FormInt.State.DISPLAY || state == FormInt.State.DEFAULT) {            
            FormRPC rpc;
            rpc = (FormRPC)this.forms.get("queryByLetter");
            rpc.setFieldValue(MethodMeta.getName(), query);
            commitQuery(rpc);
        }
    }   

}
