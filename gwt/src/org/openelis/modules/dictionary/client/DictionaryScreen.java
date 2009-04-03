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
package org.openelis.modules.dictionary.client;


import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.Widget;

import org.openelis.gwt.common.Form;
import org.openelis.gwt.common.Query;
import org.openelis.gwt.common.data.DataMap;
import org.openelis.gwt.common.data.TableDataModel;
import org.openelis.gwt.common.data.DataObject;
import org.openelis.gwt.common.data.TableDataRow;
import org.openelis.gwt.common.data.IntegerField;
import org.openelis.gwt.common.data.KeyListManager;
import org.openelis.gwt.common.data.QueryStringField;
import org.openelis.gwt.screen.CommandChain;
import org.openelis.gwt.screen.ScreenInputWidget;
import org.openelis.gwt.widget.AppButton;
import org.openelis.gwt.widget.ButtonPanel;
import org.openelis.gwt.widget.CollapsePanel;
import org.openelis.gwt.widget.Dropdown;
import org.openelis.gwt.widget.ResultsTable;
import org.openelis.gwt.widget.AppButton.ButtonState;
import org.openelis.gwt.widget.table.TableManager;
import org.openelis.gwt.widget.table.TableWidget;
import org.openelis.gwt.widget.table.event.SourcesTableWidgetEvents;
import org.openelis.gwt.widget.table.event.TableWidgetListener;
import org.openelis.metamap.CategoryMetaMap;
import org.openelis.modules.main.client.OpenELISScreenForm;

public class DictionaryScreen extends OpenELISScreenForm<DictionaryForm,Query<TableDataRow<Integer>>> implements ClickListener,
                                                                    TableManager,
                                                                    TableWidgetListener{

    private TableWidget dictEntryController = null;
    private AppButton removeEntryButton = null;
    //private TextBox tname = null;
    private KeyListManager keyList = new KeyListManager();

    private Dropdown displaySection = null;
    
    private static TableDataModel<TableDataRow<Integer>> sectionDropDown = null;
        
    private CategoryMetaMap CatMap = new CategoryMetaMap();
    public DictionaryScreen() {
        super("org.openelis.modules.dictionary.server.DictionaryService");
        query = new Query<TableDataRow<Integer>>();
        getScreen(new DictionaryForm());        
    }
    
    public void afterDraw(boolean success) {       
        ResultsTable atozTable = (ResultsTable) getWidget("azTable");
        ButtonPanel atozButtons = (ButtonPanel)getWidget("atozButtons");
        ButtonPanel bpanel = (ButtonPanel)getWidget("buttons");
        
        CommandChain chain = new CommandChain();
        chain.addCommand(this);
        chain.addCommand(keyList);
        chain.addCommand(bpanel);
        chain.addCommand(atozTable);
        chain.addCommand(atozButtons);
        
        ((CollapsePanel)getWidget("collapsePanel")).addChangeListener(atozTable);
               
        dictEntryController = ((TableWidget)getWidget("dictEntTable"));

        dictEntryController.addTableWidgetListener(this);

        startWidget = (ScreenInputWidget)widgets.get(CatMap.getName());
        removeEntryButton = (AppButton)getWidget("removeEntryButton");
                
        displaySection = (Dropdown)getWidget(CatMap.getSectionId());       
                     
        setSectionsModel(form.sections);
        form.sections = null;
        
        // override the callbacks
        updateChain.add(afterUpdate);
        super.afterDraw(success);        
        
        form.dictEntTable.setValue(dictEntryController.model.getData());
    }
    
    public void performCommand(Enum action, Object obj) {        
        if(obj instanceof AppButton) {
            String query = ((AppButton)obj).action;
            if (query.indexOf("query:") != -1)
                getCategories(query.substring(6));
            else                         
                super.performCommand(action, obj);            
         }  else {                         
            super.performCommand(action, obj);
        }        
    }

    public void onClick(Widget sender) {
        if(sender == removeEntryButton)  
           onRemoveRowButtonClick();            
     }    

    public void query() {
        super.query();
        removeEntryButton.changeState(ButtonState.DISABLED);
        dictEntryController.model.enableAutoAdd(false);         
    }
    
    public void add() {
        super.add();
        dictEntryController.model.enableAutoAdd(true); 
    }
    
    public void abort() {
        dictEntryController.model.enableAutoAdd(false); 
        super.abort();
    }
    
    protected AsyncCallback afterUpdate = new AsyncCallback() {
        public void onFailure(Throwable caught) {
            Window.alert(caught.getMessage());
        }

        public void onSuccess(Object result) {
            dictEntryController.model.enableAutoAdd(true);             
        }
    };
    
    protected void submitForm(){  
        super.submitForm();
        dictEntryController.model.enableAutoAdd(false);
    }

    private void getCategories(String query) {
        if (state == State.DISPLAY || state == State.DEFAULT) {
            QueryStringField qField = new QueryStringField(CatMap.getName());
            qField.setValue(query);
            commitQuery(qField);
        }
    }
    
    private void onRemoveRowButtonClick(){
      if(dictEntryController.model.getData().getSelectedIndex() > -1)  {
        dictEntryController.model
        .deleteRow(dictEntryController.model.getData().getSelectedIndex());
      } 
    }     
            

    public boolean canAdd(TableWidget widget,TableDataRow set, int row) {
        // TODO Auto-generated method stub
        return false;
    }


    public boolean canAutoAdd(TableWidget widget,TableDataRow row) {        
        return ((DataObject)row.cells[0]).getValue() != null;
    }


    public boolean canDelete(TableWidget widget,TableDataRow set, int row) {
        // TODO Auto-generated method stub
        return false;
    }


    public boolean canEdit(TableWidget widget, TableDataRow set, int row, int col) {        
        if(state == State.UPDATE || state == State.ADD|| state == State.QUERY)
            return true;       
           return false;
       }

    public boolean canSelect(TableWidget widget, TableDataRow set, int row) {
        if(state == State.UPDATE || state == State.ADD|| state == State.QUERY)
            return true;       
           return false;
       }

    public void finishedEditing(SourcesTableWidgetEvents sender, int row, int col) {
      if(col == 3)  {
        final TableDataRow<Integer> set = dictEntryController.model.getRow(row) ;
        final int currRow = row;
        DataMap data =  (DataMap)set.getData();
        DictionaryEntryTextRPC detrpc = null;
        Integer id = null;        
        if(data != null) {            
            id = ((IntegerField)data.get("id")).getValue();
            if(id != null) {
                detrpc = new DictionaryEntryTextRPC();
                detrpc.id = id;                
                screenService.call("getNumResultsAffected",detrpc,
                                        new AsyncCallback<DictionaryEntryTextRPC>() {
                                          public void onSuccess(DictionaryEntryTextRPC result) {
                                            if(result != null) {
                                             if(result.count > 0) {
                                              boolean ok = Window.confirm(consts.get("entryAddedAsResultValue"));
                                               if(!ok) {                                                 
                                                 dictEntryController.model.setCell(currRow, 3, result.entryText);
                                                 dictEntryController.model.refresh();  
                                               } 
                                             } 
                                            }    
                                                                                              
                                           }

                                            public void onFailure(Throwable caught) {
                                                Window.alert(caught.getMessage());
                                                window.setStatus("", "");
                                            }
                                        });
            }
        }
      } 
    }

    public void startEditing(SourcesTableWidgetEvents sender, int row, int col) {
        // TODO Auto-generated method stub
        
    }

    public void stopEditing(SourcesTableWidgetEvents sender, int row, int col) {
        // TODO Auto-generated method stub
        
    }
    
    private void setSectionsModel(TableDataModel model) {
        displaySection.setModel(model);   
    }
    
}