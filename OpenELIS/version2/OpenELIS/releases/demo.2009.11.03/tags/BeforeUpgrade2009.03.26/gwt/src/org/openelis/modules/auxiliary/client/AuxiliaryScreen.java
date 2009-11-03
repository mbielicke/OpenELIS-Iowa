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

import java.util.ArrayList;
import java.util.HashMap;

import org.openelis.gwt.common.Form;
import org.openelis.gwt.common.data.DataMap;
import org.openelis.gwt.common.data.DataModel;
import org.openelis.gwt.common.data.DataObject;
import org.openelis.gwt.common.data.DataSet;
import org.openelis.gwt.common.data.KeyListManager;
import org.openelis.gwt.screen.CommandChain;
import org.openelis.gwt.screen.ScreenTableWidget;
import org.openelis.gwt.screen.ScreenWindow;
import org.openelis.gwt.widget.AToZTable;
import org.openelis.gwt.widget.AppButton;
import org.openelis.gwt.widget.ButtonPanel;
import org.openelis.gwt.widget.CollapsePanel;
import org.openelis.gwt.widget.FormInt;
import org.openelis.gwt.widget.AppButton.ButtonState;
import org.openelis.gwt.widget.FormInt.State;
import org.openelis.gwt.widget.table.TableDropdown;
import org.openelis.gwt.widget.table.TableManager;
import org.openelis.gwt.widget.table.TableWidget;
import org.openelis.gwt.widget.table.event.SourcesTableModelEvents;
import org.openelis.gwt.widget.table.event.SourcesTableWidgetEvents;
import org.openelis.gwt.widget.table.event.TableModelListener;
import org.openelis.gwt.widget.table.event.TableWidgetListener;
import org.openelis.metamap.AuxFieldGroupMetaMap;
import org.openelis.modules.dictionaryentrypicker.client.DictionaryEntryPickerScreen;
import org.openelis.modules.main.client.OpenELISScreenForm;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.SyncCallback;
import com.google.gwt.user.client.ui.ChangeListener;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.PopupListener;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.Widget;

public class AuxiliaryScreen extends OpenELISScreenForm<AuxiliaryRPC, AuxiliaryForm, Integer> implements TableManager,
                                                                                                         ClickListener,
                                                                                                         ChangeListener,
                                                                                                         TableWidgetListener,
                                                                                                         TableModelListener,
                                                                                                         PopupListener{
    private ButtonPanel atozButtons;
    
    private KeyListManager keyList = new KeyListManager();
    
    private AuxFieldGroupMetaMap AuxFGMeta = new AuxFieldGroupMetaMap();
    
    private TableWidget auxFieldTableWidget, auxFieldValueTableWidget;
    
    private AppButton removeAuxFieldRowButton,dictionaryLookUpButton;    
    
    private ArrayList<DataSet<Object>> selectedRows = null; 
    
    private DataModel<Integer> defaultModel;
    
    private int tempAnaId = -1;    
        
    private ScreenWindow pickerWindow = null; 
    
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
        auxFieldValueTableWidget.model.enableAutoAdd(false);
        
        auxFieldTableWidget.model.addTableModelListener(this);
        
        removeAuxFieldRowButton = (AppButton)getWidget("removeAuxFieldRowButton");
        dictionaryLookUpButton = (AppButton)getWidget("dictionaryLookUpButton");
         
        setUnitsOfMeasureModel(rpc.units);
        setAuxFieldValueTypesModel(rpc.auxFieldValueTypes);
        setScriptletsModel(rpc.scriptlets);                
        
        updateChain.add(afterUpdate);
        
        updateChain.add(0,checkModels);
        fetchChain.add(0,checkModels);
        abortChain.add(0,checkModels);
        commitUpdateChain.add(0,checkModels);
        commitAddChain.add(0,checkModels);
                
        tempAnaId = -1;     
        
        super.afterDraw(success);               
        
        rpc.form.auxFieldTable.setValue(auxFieldTableWidget.model.getData());
        rpc.form.auxFieldValueTable.setValue(auxFieldValueTableWidget.model.getData());
        
        defaultModel = (DataModel<Integer>)auxFieldValueTableWidget.model.getData().clone();
    }
    
    public void performCommand(Enum action, Object obj) {
        if (obj instanceof AppButton) {
            String query = ((AppButton)obj).action;            
            if (query.indexOf(":") != -1) 
                getAuxFieldGroups(query.substring(6));
            else
                super.performCommand(action, obj);
        } else{
            super.performCommand(action, obj);
        }
    }
    
    public void query() {
        super.query();     
        auxFieldTableWidget.model.enableAutoAdd(false);
        auxFieldValueTableWidget.model.enableAutoAdd(false);  
        removeAuxFieldRowButton.changeState(ButtonState.DISABLED);
    }
    
    public void add() {
        super.add();
        auxFieldValueTableWidget.model.enableAutoAdd(false);
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
            auxFieldValueTableWidget.model.enableAutoAdd(false);   
            auxFieldTableWidget.model.enableAutoAdd(true);  
        }
    };
    
    protected void submitForm() {
        super.submitForm();
    }       
    
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
        if (state == State.UPDATE || state == State.ADD || state == State.QUERY)
            return true;
        return false;          
    }

    public void onClick(Widget sender) {
        if(sender == removeAuxFieldRowButton)
            onRemoveAuxFieldRowButtonClick();
        if(sender == dictionaryLookUpButton)
            onDictionaryLookUpButtonClicked();
        
    }   

    public void getAuxFieldGroups(String query) {
        if (state == FormInt.State.DISPLAY || state == FormInt.State.DEFAULT) {
            Form form = (Form)forms.get("queryByLetter");
            form.setFieldValue(AuxFGMeta.getName(), query);
            commitQuery(form);
        }
    }
    
    public void onPopupClosed(PopupPanel sender, boolean autoClosed) {      
        
    }  
    
    public void startEditing(SourcesTableWidgetEvents sender, int row, int col) {
        // TODO Auto-generated method stub
        
    }

    public void stopEditing(SourcesTableWidgetEvents sender, int row, int col) {
        // TODO Auto-generated method stub
        
    }
    
    public void cellUpdated(SourcesTableModelEvents sender, int row, int cell) {
        // TODO Auto-generated method stub
        
    }

    public void dataChanged(SourcesTableModelEvents sender) {
        // TODO Auto-generated method stub
        
    }

    public void rowAdded(SourcesTableModelEvents sender, int rows) {
        // TODO Auto-generated method stub
        
    }

    public void rowDeleted(SourcesTableModelEvents sender, int row) {
        // TODO Auto-generated method stub
        
    }

    public void rowSelected(SourcesTableModelEvents sender, int row) {
        if(sender == auxFieldTableWidget.model && row < auxFieldTableWidget.model.getData().size()) {          
           auxFieldValueTableWidget.model.enableAutoAdd(true);           
           setModelInFieldValueTable(row);            
        }    
    }

    public void rowUnselected(SourcesTableModelEvents sender, int row) {
        // TODO Auto-generated method stub
        
    }

    public void rowUpdated(SourcesTableModelEvents sender, int row) {
        // TODO Auto-generated method stub
        
    }

    public void unload(SourcesTableModelEvents sender) {
        // TODO Auto-generated method stub
        
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
    
    private void onRemoveAuxFieldRowButtonClick() {
        if (auxFieldTableWidget.model.getData().getSelectedIndex() > -1)
            auxFieldTableWidget.model.deleteRow(auxFieldTableWidget.model.getData()
                                                               .getSelectedIndex());
        
    }
    
    /**
     * This function opens a modal window which allows the users to select one
     * or more dictionary entries to be added to the Test Results table
     */
    private void onDictionaryLookUpButtonClicked() {
        int left = getAbsoluteLeft();
        int top = getAbsoluteTop();
        DictionaryEntryPickerScreen pickerScreen = new DictionaryEntryPickerScreen();        
        PopupPanel dictEntryPickerPopupPanel = new PopupPanel(false, true);
        pickerWindow = new ScreenWindow(dictEntryPickerPopupPanel,
                                                     consts.get("chooseDictEntry"),
                                                     "dictionaryEntryPicker",
                                                     "Loading...");
        pickerScreen.selectedRows = selectedRows;        
        pickerWindow.setContent(pickerScreen);   
        dictEntryPickerPopupPanel.addPopupListener(this);
        dictEntryPickerPopupPanel.add(pickerWindow);
        dictEntryPickerPopupPanel.setPopupPosition(left, top);
        dictEntryPickerPopupPanel.show();
    }
    
    private int getNextTempAnaId() {
        return --tempAnaId;
   }

    public void finishedEditing(SourcesTableWidgetEvents sender,
                                int row,
                                int col) {
        // TODO Auto-generated method stub
        
    }
    
    private void setModelInFieldValueTable(int row) {       
       AuxiliaryGeneralPurposeRPC agrpc = null;      
       final DataSet<Integer> set = auxFieldTableWidget.model.getRow(row);
       DataModel<Integer> model = (DataModel<Integer>)set.getData();
       if(set.getKey() != null && model == null) {            
            agrpc = new AuxiliaryGeneralPurposeRPC();
            agrpc.auxFieldValueModel = (DataModel<Integer>)defaultModel.clone();
            agrpc.key = set.getKey();
            screenService.call("getAuxFieldValueModel", agrpc, new SyncCallback(){
                public void onSuccess(Object result) {                      
                    set.setData(((AuxiliaryGeneralPurposeRPC)result).auxFieldValueModel);                      
                }            
                public void onFailure(Throwable caught) {                
                    Window.alert(caught.getMessage());
                    window.setStatus("", "");
                }
                   
               });                       
        }       
       auxFieldValueTableWidget.model.load((DataModel)set.getData());       
    }            
                 
  }      