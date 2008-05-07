package org.openelis.modules.systemvariable.client;

import org.openelis.gwt.common.FormRPC;
import org.openelis.gwt.widget.AToZPanel;
import org.openelis.gwt.widget.ButtonPanel;
import org.openelis.gwt.widget.FormInt;
import org.openelis.modules.main.client.OpenELISScreenForm;

import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.Widget;

public class SystemVariableScreen extends OpenELISScreenForm implements ClickListener {
    
    public SystemVariableScreen() {
        super("org.openelis.modules.systemvariable.server.SystemVariableService",false);
        name="System Variable";
    }
    
    public void afterDraw(boolean success) {
        setBpanel((ButtonPanel) getWidget("buttons"));        
        message.setText("done");

        AToZPanel atozTable = (AToZPanel) getWidget("hideablePanel");
        modelWidget.addChangeListener(atozTable);
        addChangeListener(atozTable);
        
        ButtonPanel atozButtons = (ButtonPanel)getWidget("atozButtons");
        atozButtons.addChangeListener(this);
        
        super.afterDraw(success);
    }  
    
    public void onChange(Widget sender) {
        
        if(sender == getWidget("atozButtons")){           
           String action = ((ButtonPanel)sender).buttonClicked.action;           
           if(action.startsWith("query:")){
               getSystemVariables(action.substring(6, action.length()), ((ButtonPanel)sender).buttonClicked);      
           }
        }else{
            super.onChange(sender);
        }
    }
    
    private void getSystemVariables(String letter, Widget sender) {
        // we only want to allow them to select a letter if they are in display
        // mode..
        if (state == FormInt.DISPLAY || state == FormInt.DEFAULT) {

            FormRPC letterRPC = (FormRPC) this.forms.get("queryByLetter");
            letterRPC.setFieldValue("systemVariable.name", letter.toLowerCase() + "*");
             
            commitQuery(letterRPC);
            
           
        }
    }

    public void onClick(Widget arg0) {
        // TODO Auto-generated method stub
        
    }
        

}
