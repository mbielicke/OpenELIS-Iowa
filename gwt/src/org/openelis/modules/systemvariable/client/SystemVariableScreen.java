package org.openelis.modules.systemvariable.client;

import org.openelis.gwt.common.FormRPC;
import org.openelis.gwt.common.data.DataSet;
import org.openelis.gwt.common.data.NumberObject;
import org.openelis.gwt.widget.AToZPanel;
import org.openelis.gwt.widget.ButtonPanel;
import org.openelis.gwt.widget.FormInt;
import org.openelis.modules.main.client.OpenELISScreenForm;

import com.google.gwt.user.client.ui.Widget;

public class SystemVariableScreen extends OpenELISScreenForm {

    private static boolean loaded = false;
    
    public SystemVariableScreen() {
        super("org.openelis.modules.systemvariable.server.SystemVariableService",loaded);
        name="SystemVariable";
    }
    
    public void afterDraw(boolean success) {
        //loaded = true;
        bpanel = (ButtonPanel) getWidget("buttons");        
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
    
    public void afterCommitAdd(boolean success){
        Integer svId = (Integer)rpc.getFieldValue("systemVariable.id");
        NumberObject qaeIdObj = new NumberObject();
        qaeIdObj.setType("integer");
        qaeIdObj.setValue(svId);           
        
        //done because key is set to null in AppScreenForm for the add operation 
        if(key ==null){  
         key = new DataSet();
         key.addObject(qaeIdObj);
        }
        else{
            key.setObject(0,qaeIdObj);
        }
        
        super.afterCommitAdd(success);
    }

}
