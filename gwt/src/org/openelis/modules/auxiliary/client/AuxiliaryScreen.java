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
package org.openelis.modules.auxiliary.client;

import org.openelis.gwt.common.Form;
import org.openelis.gwt.common.data.DataModel;
import org.openelis.gwt.common.data.DataObject;
import org.openelis.gwt.common.data.DataSet;
import org.openelis.gwt.common.data.KeyListManager;
import org.openelis.gwt.screen.CommandChain;
import org.openelis.gwt.screen.ScreenTableWidget;
import org.openelis.gwt.widget.AToZTable;
import org.openelis.gwt.widget.AppButton;
import org.openelis.gwt.widget.ButtonPanel;
import org.openelis.gwt.widget.CollapsePanel;
import org.openelis.gwt.widget.FormInt;
import org.openelis.gwt.widget.FormInt.State;
import org.openelis.gwt.widget.table.TableDropdown;
import org.openelis.gwt.widget.table.TableManager;
import org.openelis.gwt.widget.table.TableWidget;
import org.openelis.metamap.AuxFieldGroupMetaMap;
import org.openelis.modules.main.client.OpenELISScreenForm;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.ChangeListener;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.Widget;

public class AuxiliaryScreen extends OpenELISScreenForm<AuxiliaryRPC, AuxiliaryForm, Integer> implements TableManager,
                                                                                                         ClickListener,
                                                                                                         ChangeListener{
    private ButtonPanel atozButtons;
    
    private KeyListManager keyList = new KeyListManager();
    
    private AuxFieldGroupMetaMap AuxFGMeta = new AuxFieldGroupMetaMap();
    
    private TableWidget auxFieldTableWidget, auxFieldValueTableWidget;
    
    AsyncCallback<AuxiliaryRPC> checkModels = new AsyncCallback<AuxiliaryRPC>() {
     
      public void onSuccess(AuxiliaryRPC rpc) {
            if(rpc.units != null) {
                setUnitsOfMeasureModel(rpc.units);
                rpc.units = null;
            }
            if(rpc.auxFieldValueTypes != null) {
                setAuxFieldValueTypesModel(rpc.auxFieldValueTypes);
                rpc.auxFieldValueTypes = null;
            }
      }

        public void onFailure(Throwable caught) {
            
        }
    };
    
    public AuxiliaryScreen() {
        super("org.openelis.modules.auxiliary.server.AuxiliaryService");
        forms.put("display", new AuxiliaryForm());
        getScreen(new AuxiliaryRPC());
    }

    public void afterDraw(boolean success) {
        AToZTable atozTable;

        //
        // we are interested in getting button actions in two places,
        // modelwidget and the screen.
        //
        atozTable = (AToZTable)getWidget("azTable");
        ButtonPanel bpanel = (ButtonPanel)getWidget("buttons");
        atozButtons = (ButtonPanel)getWidget("atozButtons");  
        
        CommandChain formChain = new CommandChain();
        formChain.addCommand(this);
        formChain.addCommand(bpanel);
        formChain.addCommand(keyList);
        formChain.addCommand(atozTable);
        formChain.addCommand(atozButtons);
        
        ((CollapsePanel)getWidget("collapsePanel")).addChangeListener(atozTable);
        
        ScreenTableWidget s = (ScreenTableWidget)widgets.get("auxFieldTable");
        auxFieldTableWidget = (TableWidget)s.getWidget();
        s = (ScreenTableWidget)widgets.get("auxFieldValueTable");  
        auxFieldValueTableWidget = (TableWidget)s.getWidget();
        
        setUnitsOfMeasureModel(rpc.units);
        setAuxFieldValueTypesModel(rpc.auxFieldValueTypes);
        setScriptletsModel(rpc.scriptlets);                
        
        updateChain.add(afterUpdate);
        
        updateChain.add(0,checkModels);
        fetchChain.add(0,checkModels);
        abortChain.add(0,checkModels);
        commitUpdateChain.add(0,checkModels);
        commitAddChain.add(0,checkModels);
        
        
        super.afterDraw(success);
        
        rpc.form.auxFieldTable.setValue(auxFieldTableWidget.model.getData());
        rpc.form.auxFieldValueTable.setValue(auxFieldValueTableWidget.model.getData());
        
    }
    
    public void performCommand(Enum action, Object obj) {
        if (obj instanceof AppButton) {
            String baction = ((AppButton)obj).action;
            if (baction.startsWith("query:")) {
                getAuxFieldGroups(baction.substring(6));
            }else
                super.performCommand(action, obj);
        } else{
            super.performCommand(action, obj);
        }
    }
    
    public void query() {
        super.query();     
        auxFieldTableWidget.model.enableAutoAdd(false);
        auxFieldValueTableWidget.model.enableAutoAdd(false);  
    }
    
    public void add() {
        super.add();
        auxFieldValueTableWidget.model.enableAutoAdd(true); 
        auxFieldTableWidget.model.enableAutoAdd(true);  
    }
    
    public void abort() {
        auxFieldValueTableWidget.model.enableAutoAdd(false); 
        auxFieldTableWidget.model.enableAutoAdd(false);  
        super.abort();
    }
    
    protected AsyncCallback afterUpdate = new AsyncCallback() {
        public void onFailure(Throwable caught) {
            Window.alert(caught.getMessage());
        }

        public void onSuccess(Object result) {
            auxFieldValueTableWidget.model.enableAutoAdd(true);   
            auxFieldTableWidget.model.enableAutoAdd(true);  
        }
    };
    
    
    
    public boolean canAdd(TableWidget widget, DataSet set, int row) {
        // TODO Auto-generated method stub
        return true;
    }

    public boolean canAutoAdd(TableWidget widget, DataSet set) {
       return ((DataObject)set.get(0)).getValue() != null && !((DataObject)set.get(0)).getValue()
        .equals(-1);
    }

    public boolean canDelete(TableWidget widget, DataSet set, int row) {
        // TODO Auto-generated method stub
        return false;
    }

    public boolean canEdit(TableWidget widget, DataSet set, int row, int col) {
        if (state == State.UPDATE || state == State.ADD || state == State.QUERY)
            return true;
        
        return false;
    }

    public boolean canSelect(TableWidget widget, DataSet set, int row) {
        // TODO Auto-generated method stub
        return true;
    }

    public void onClick(Widget sender) {
        // TODO Auto-generated method stub
        
    }
    
    public void getAuxFieldGroups(String query) {
        if (state == FormInt.State.DISPLAY || state == FormInt.State.DEFAULT) {
            Form form = (Form)forms.get("queryByLetter");
            form.setFieldValue(AuxFGMeta.getName(), query);
            commitQuery(form);
        }
    }
    
    
    private void setAuxFieldValueTypesModel(DataModel<Integer> auxFieldValueTypes) {
        ((TableDropdown)auxFieldValueTableWidget.columns.get(0).getColumnWidget()).setModel(auxFieldValueTypes);
    }

    private void setUnitsOfMeasureModel(DataModel<Integer> units) {
        ((TableDropdown)auxFieldTableWidget.columns.get(2).getColumnWidget()).setModel(units);
    }
    
    private void setScriptletsModel(DataModel<Integer> scriptlets) {
        ((TableDropdown)auxFieldTableWidget.columns.get(7).getColumnWidget()).setModel(scriptlets);
    }

}
