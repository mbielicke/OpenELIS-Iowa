package org.openelis.modules.systemvariable.client;

import org.openelis.gwt.common.FormRPC;
import org.openelis.gwt.widget.AToZPanel;
import org.openelis.gwt.widget.ButtonPanel;
import org.openelis.gwt.widget.FormInt;
import org.openelis.modules.main.client.OpenELISScreenForm;
import org.openelis.newmeta.SystemVariableMetaMap;

import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class SystemVariableScreen extends OpenELISScreenForm implements ClickListener {
    
    private TextBox nameTextBox;
    public SystemVariableScreen() {
        super("org.openelis.modules.systemvariable.server.SystemVariableService",false);
    }
    
    private SystemVariableMetaMap Meta = new SystemVariableMetaMap();
    
    public void onChange(Widget sender) {
        
        if(sender == getWidget("atozButtons")){           
           String action = ((ButtonPanel)sender).buttonClicked.action;           
           if(action.startsWith("query:")){
               getSystemVariables(action.substring(6, action.length()));      
           }
        }else{
            super.onChange(sender);
        }
    }

    public void onClick(Widget arg0) {
        // TODO Auto-generated method stub
        
    }

    public void afterDraw(boolean success) {
        setBpanel((ButtonPanel) getWidget("buttons"));        

        AToZPanel atozTable = (AToZPanel) getWidget("hideablePanel");
        modelWidget.addChangeListener(atozTable);
        addChangeListener(atozTable);
        
        ButtonPanel atozButtons = (ButtonPanel)getWidget("atozButtons");
        atozButtons.addChangeListener(this);
        
        nameTextBox = (TextBox)getWidget(Meta.getName());
        
        super.afterDraw(success);
    }  
    
    public void query() {
        super.query();
        
        nameTextBox.setFocus(true);
    }
    
    public void add() {
        super.add();
        
        nameTextBox.setFocus(true);
    }
    
    public void afterUpdate(boolean success) {
        super.afterUpdate(success);
        
        nameTextBox.setFocus(true);
    }
    
    private void getSystemVariables(String query) {
        if (state == FormInt.State.DISPLAY || state == FormInt.State.DEFAULT) {

            FormRPC letterRPC = (FormRPC) this.forms.get("queryByLetter");
            letterRPC.setFieldValue(Meta.getName(), query);
             
            commitQuery(letterRPC);
            
           
        }
    }
        

}
