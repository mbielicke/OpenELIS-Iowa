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
package org.openelis.modules.label.client;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.Widget;

import org.openelis.gwt.common.Query;
import org.openelis.gwt.common.data.TableDataModel;
import org.openelis.gwt.common.data.KeyListManager;
import org.openelis.gwt.common.data.QueryStringField;
import org.openelis.gwt.common.data.TableDataRow;
import org.openelis.gwt.screen.CommandChain;
import org.openelis.gwt.screen.ScreenInputWidget;
import org.openelis.gwt.widget.AppButton;
import org.openelis.gwt.widget.ButtonPanel;
import org.openelis.gwt.widget.CollapsePanel;
import org.openelis.gwt.widget.Dropdown;
import org.openelis.gwt.widget.ResultsTable;
import org.openelis.metamap.LabelMetaMap;
import org.openelis.modules.main.client.OpenELISScreenForm;

public class LabelScreen extends OpenELISScreenForm<LabelForm,Query<TableDataRow<Integer>>> implements ClickListener {
    
    private Dropdown displayPType = null;
    private Dropdown displayScript = null;

    private KeyListManager keyList = new KeyListManager();
    
    private LabelMetaMap Meta = new LabelMetaMap(); 
    
    AsyncCallback<LabelForm> checkModels = new AsyncCallback<LabelForm>() {
        public void onSuccess(LabelForm rpc) {
            if(rpc.printerType != null) {
                setPrinterTypesModel(rpc.printerType);
            }
        }
      
        public void onFailure(Throwable caught) {
            
        }
    };
    
    public LabelScreen() {
        super("org.openelis.modules.label.server.LabelService");
        query = new Query<TableDataRow<Integer>>();
        getScreen(new LabelForm());
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
        ResultsTable atozTable = (ResultsTable) getWidget("azTable");
        ButtonPanel atozButtons = (ButtonPanel)getWidget("atozButtons");
        ButtonPanel bpanel = (ButtonPanel)getWidget("buttons");
        
        CommandChain chain = new CommandChain();
        chain.addCommand(this);
        chain.addCommand(bpanel);
        chain.addCommand(keyList);
        chain.addCommand(atozTable);
        chain.addCommand(atozButtons);
        
        ((CollapsePanel)getWidget("collapsePanel")).addChangeListener(atozTable);
       
        
        startWidget = (ScreenInputWidget)widgets.get(Meta.getName());
        
        displayPType = (Dropdown)getWidget(Meta.getPrinterTypeId());
        displayScript = (Dropdown)getWidget(Meta.getScriptletId());
        
        //load dropdowns
        setPrinterTypesModel(form.printerType);
        setScriptletModel(form.scriptlet);
                  
       
        form.printerType = null;
        form.scriptlet = null;
        
        updateChain.add(0,checkModels);
        fetchChain.add(0,checkModels);
        abortChain.add(0,checkModels);
        commitUpdateChain.add(0,checkModels);
        commitAddChain.add(0,checkModels);
        
        super.afterDraw(success);
    }
    
    private void getLabels(String query) {
      // we only want to allow them to select a letter if they are in display
      // mode..
      if (state == State.DISPLAY || state == State.DEFAULT) {
          QueryStringField qField = new QueryStringField(Meta.getName());
          qField.setValue(query);
          commitQuery(qField);
                   
      }
    }  
    
    private void setPrinterTypesModel(TableDataModel<TableDataRow<Integer>> typesModel) {
        displayPType.setModel(typesModel);
    }
    
    private void setScriptletModel(TableDataModel<TableDataRow<Integer>> model) {
        displayScript.setModel(model);
    }
    
}