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
package org.openelis.modules.section.client;

import org.openelis.gwt.common.data.deprecated.KeyListManager;
import org.openelis.gwt.common.data.deprecated.QueryStringField;
import org.openelis.gwt.common.data.deprecated.TableDataRow;
import org.openelis.gwt.common.deprecated.Query;
import org.openelis.gwt.screen.deprecated.CommandChain;
import org.openelis.gwt.widget.deprecated.AppButton;
import org.openelis.gwt.widget.deprecated.ButtonPanel;
import org.openelis.gwt.widget.deprecated.ResultsTable;
import org.openelis.metamap.SectionMetaMap;
import org.openelis.modules.main.client.OpenELISScreenForm;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.SyncCallback;
import com.google.gwt.user.client.ui.TextBox;


public class SectionScreen extends OpenELISScreenForm<SectionForm, Query<TableDataRow<Integer>>> {

    private KeyListManager<Integer> keyList = new KeyListManager<Integer>();
    
    private SectionMetaMap SectionMeta = new SectionMetaMap();    
    
    private TextBox sectName;
    
    public SectionScreen() {
        super("org.openelis.modules.section.server.SectionService");
        query = new Query<TableDataRow<Integer>>();
        getScreen(new SectionForm());
    }

    public void afterDraw(boolean success) {
        ButtonPanel bpanel, atozButtons;
        CommandChain chain;       
        ResultsTable atozTable;
        
        atozTable = (ResultsTable)getWidget("azTable");
        atozButtons = (ButtonPanel)getWidget("atozButtons");
        //((CollapsePanel)getWidget("collapsePanel")).addChangeListener(atozTable);

        bpanel = (ButtonPanel)getWidget("buttons");        
        //
        // we are interested in getting button actions in two places,
        // modelwidget and this screen.
        //
        chain = new CommandChain();
        chain.addCommand(this);
        chain.addCommand(bpanel);
        chain.addCommand(keyList);
        chain.addCommand(atozTable);
        chain.addCommand(atozButtons);             
        
        sectName = (TextBox)getWidget(SectionMeta.getName());
                     
        updateChain.add(afterUpdate);
        super.afterDraw(success);
    }
    
    public void performCommand(Enum action, Object obj) {        
        if(obj instanceof AppButton) {
            String query = ((AppButton)obj).action;
            if (query.indexOf("query:") != -1)
                getSections(query.substring(6));
            else                         
                super.performCommand(action, obj);            
         }  else {                         
            super.performCommand(action, obj);
        }        
    }
    
    private void getSections(String query) {
        QueryStringField qField;
        if (state == State.DISPLAY || state == State.DEFAULT) {
            qField = new QueryStringField(SectionMeta.getName());
            qField.setValue(query);
            commitQuery(qField);
        }        
    }
    
    public void query() {
        super.query();
        sectName.setFocus(true);     
    }
    
    public void add() {
        super.add();        
        sectName.setFocus(true);
    }   
    
    protected SyncCallback<SectionForm> afterUpdate = new SyncCallback<SectionForm>() {
        public void onFailure(Throwable caught) {
            Window.alert(caught.getMessage());
        }

        public void onSuccess(SectionForm result) {
            sectName.setFocus(true);
        }
    };
}
