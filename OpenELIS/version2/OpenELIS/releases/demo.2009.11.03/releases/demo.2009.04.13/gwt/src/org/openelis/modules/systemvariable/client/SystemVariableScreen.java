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
package org.openelis.modules.systemvariable.client;

import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.Widget;

import org.openelis.gwt.common.Query;
import org.openelis.gwt.common.data.KeyListManager;
import org.openelis.gwt.common.data.QueryStringField;
import org.openelis.gwt.common.data.TableDataModel;
import org.openelis.gwt.common.data.TableDataRow;
import org.openelis.gwt.screen.CommandChain;
import org.openelis.gwt.screen.ScreenInputWidget;
import org.openelis.gwt.widget.AppButton;
import org.openelis.gwt.widget.ButtonPanel;
import org.openelis.gwt.widget.CollapsePanel;
import org.openelis.gwt.widget.ResultsTable;
import org.openelis.metamap.SystemVariableMetaMap;
import org.openelis.modules.main.client.OpenELISScreenForm;

public class SystemVariableScreen extends OpenELISScreenForm<SystemVariableForm,Query<TableDataRow<Integer>>> implements ClickListener {
    
    private KeyListManager keyList = new KeyListManager();
    
    public SystemVariableScreen() {
        super("org.openelis.modules.systemvariable.server.SystemVariableService");
        query = new Query<TableDataRow<Integer>>();
        getScreen(new SystemVariableForm());        
    }
    
    private SystemVariableMetaMap Meta = new SystemVariableMetaMap();
    
    public void performCommand(Enum action, Object obj) {
        if(obj instanceof AppButton){           
           String baction = ((AppButton)obj).action;           
           if(baction.startsWith("query:")){
               getSystemVariables(baction.substring(6, baction.length()));      
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
        ResultsTable atozTable = (ResultsTable) getWidget("azTable");
        ButtonPanel atozButtons = (ButtonPanel)getWidget("atozButtons");
        ButtonPanel bpanel = (ButtonPanel)getWidget("buttons");
        
        CommandChain chain = new CommandChain();
        chain.addCommand(bpanel);
        chain.addCommand(this);
        chain.addCommand(keyList);
        chain.addCommand(atozButtons);
        chain.addCommand(atozTable);
        
        ((CollapsePanel)getWidget("collapsePanel")).addChangeListener(atozTable);
        
        startWidget = (ScreenInputWidget)widgets.get(Meta.getName());
        
        super.afterDraw(success);
    }  
    
    private void getSystemVariables(String query) {
        if (state == State.DISPLAY || state == State.DEFAULT) {
            QueryStringField qField = new QueryStringField(Meta.getName());
            qField.setValue(query);
            commitQuery(qField);
        }
    }
        

}
