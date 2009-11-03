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
package org.openelis.modules.qc.client;

import java.util.ArrayList;
import java.util.List;

import org.openelis.cache.DictionaryCache;
import org.openelis.domain.DictionaryDO;
import org.openelis.gwt.common.Form;
import org.openelis.gwt.common.Query;
import org.openelis.gwt.common.data.DropDownField;
import org.openelis.gwt.common.data.KeyListManager;
import org.openelis.gwt.common.data.QueryStringField;
import org.openelis.gwt.common.data.StringField;
import org.openelis.gwt.common.data.StringObject;
import org.openelis.gwt.common.data.TableDataModel;
import org.openelis.gwt.common.data.TableDataRow;
import org.openelis.gwt.screen.CommandChain;
import org.openelis.gwt.screen.ScreenWindow;
import org.openelis.gwt.widget.AppButton;
import org.openelis.gwt.widget.AutoComplete;
import org.openelis.gwt.widget.ButtonPanel;
import org.openelis.gwt.widget.CollapsePanel;
import org.openelis.gwt.widget.Dropdown;
import org.openelis.gwt.widget.ResultsTable;
import org.openelis.gwt.widget.AppButton.ButtonState;
import org.openelis.gwt.widget.table.TableDropdown;
import org.openelis.gwt.widget.table.TableManager;
import org.openelis.gwt.widget.table.TableWidget;
import org.openelis.gwt.widget.table.event.SourcesTableWidgetEvents;
import org.openelis.gwt.widget.table.event.TableWidgetListener;
import org.openelis.metamap.QcMetaMap;
import org.openelis.modules.dictionaryentrypicker.client.DictionaryEntryPickerScreen;
import org.openelis.modules.main.client.OpenELISScreenForm;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.SyncCallback;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class QCScreen extends OpenELISScreenForm<QCForm, Query<TableDataRow<Integer>>> implements TableManager,
                                                                                                  ClickListener,
                                                                                                  TableWidgetListener{

    private KeyListManager<Integer> keyList = new KeyListManager<Integer>();
    
    private QcMetaMap QcMeta = new QcMetaMap();
    
    private AppButton removeQCAnalyteButton, dictionaryLookUpButton;
    
    private TextBox qcName;
    
    private TableWidget qcAnalyteTableWidget;
    
    private Dropdown qcType,preparedUnit;   
    
    private AutoComplete prepBy;    
    
    public QCScreen() {
        super("org.openelis.modules.qc.server.QCService");
        query = new Query<TableDataRow<Integer>>();
        getScreen(new QCForm());
    }
    
    public void afterDraw(boolean success) {
        ButtonPanel bpanel, atozButtons;
        CommandChain chain;       
        ResultsTable atozTable;
        ArrayList cache;
        TableDataModel<TableDataRow> model;
        
        atozTable = (ResultsTable)getWidget("azTable");
        atozButtons = (ButtonPanel)getWidget("atozButtons");
        ((CollapsePanel)getWidget("collapsePanel")).addChangeListener(atozTable);

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
        
        qcType = (Dropdown)getWidget(QcMeta.getTypeId());
        preparedUnit = (Dropdown)getWidget(QcMeta.getPreparedUnitId());
        
        qcAnalyteTableWidget = (TableWidget)getWidget("qcAnalyteTable");
        qcAnalyteTableWidget.addTableWidgetListener(this);
        
        removeQCAnalyteButton = (AppButton)getWidget("removeQCAnalyteButton");
        dictionaryLookUpButton = (AppButton)getWidget("dictionaryLookUpButton");                

        qcName = (TextBox)getWidget(QcMeta.getName());                      
        
        prepBy = (AutoComplete)getWidget(QcMeta.getPreparedById());
        
        updateChain.add(afterUpdate);
        commitUpdateChain.add(commitUpdateCallback);
        commitAddChain.add(commitAddCallback);
        
        super.afterDraw(success);
        
        cache = DictionaryCache.getListByCategorySystemName("unit_of_measure");
        model = getDictionaryIdEntryList(cache);
        preparedUnit.setModel(model);
        
        cache = DictionaryCache.getListByCategorySystemName("qc_analyte_type");
        model = getDictionaryIdEntryList(cache);
        ((TableDropdown)qcAnalyteTableWidget.columns.get(1).getColumnWidget()).setModel(model);
        
        cache = DictionaryCache.getListByCategorySystemName("qc_type");
        model = getDictionaryIdEntryList(cache);
        qcType.setModel(model);
    }
    
    public void performCommand(Enum action, Object obj) { 
        ArrayList<TableDataRow<Integer>> selectedRows;
        String query;
        if(obj instanceof AppButton) {
            query = ((AppButton)obj).action;
            if (query.indexOf("query:") != -1)
                getQCs(query.substring(6));
            else                         
                super.performCommand(action, obj);            
         }  else if(action == DictionaryEntryPickerScreen.Action.COMMIT) {              
             selectedRows = (ArrayList<TableDataRow<Integer>>)obj;
             dictionaryLookupClosed(selectedRows);
         } else{
             super.performCommand(action, obj);
         }   
    }
    
    public boolean canPerformCommand(Enum action, Object obj) {
        if(action == DictionaryEntryPickerScreen.Action.COMMIT || 
                        action == DictionaryEntryPickerScreen.Action.ABORT)
            return true;
        else
            return super.canPerformCommand(action, obj);
    }
    
    public void query() {
        super.query();
        removeQCAnalyteButton.changeState(ButtonState.DISABLED);
        dictionaryLookUpButton.changeState(ButtonState.DISABLED);
        qcAnalyteTableWidget.model.enableAutoAdd(false);   
        prepBy.enabled(false);
    }
    
    public void add() {
        super.add();
        qcName.setFocus(true);
        qcAnalyteTableWidget.model.enableAutoAdd(true); 
        qcAnalyteTableWidget.activeRow = -1;
        prepBy.enabled(true);
    }
    
    public void abort() {
        qcAnalyteTableWidget.model.enableAutoAdd(false); 
        super.abort();
    }
    
    protected SyncCallback<QCForm> afterUpdate = new SyncCallback<QCForm>() {
        public void onFailure(Throwable caught) {
            Window.alert(caught.getMessage());
        }

        public void onSuccess(QCForm result) {
            qcName.setFocus(true);
            qcAnalyteTableWidget.model.enableAutoAdd(true);  
            qcAnalyteTableWidget.activeRow = -1;
            prepBy.enabled(true);
        }
    };   
    
    protected SyncCallback<QCForm> commitUpdateCallback = new SyncCallback<QCForm>() {
        public void onSuccess(QCForm result) {
            if (form.status != Form.Status.invalid)                                
                qcAnalyteTableWidget.model.enableAutoAdd(false); 
        }

        public void onFailure(Throwable caught) {
            handleError(caught);
        }
    };

    protected SyncCallback<QCForm> commitAddCallback = new SyncCallback<QCForm>() {
        public void onSuccess(QCForm result) {
            if (form.status != Form.Status.invalid)                                
                qcAnalyteTableWidget.model.enableAutoAdd(false); 
        }

        public void onFailure(Throwable caught) {
            handleError(caught);
        }
    };

    private void getQCs(String query) {
        QueryStringField qField;
        if (state == State.DISPLAY || state == State.DEFAULT) {
            qField = new QueryStringField(QcMeta.getName());
            qField.setValue(query);
            commitQuery(qField);
        }
        
    }

    public <T> boolean canAdd(TableWidget widget, TableDataRow<T> set, int row) {
        return false;
    }

    public <T> boolean canAutoAdd(TableWidget widget, TableDataRow<T> addRow) {
        DropDownField<Integer> ddField;
        StringField strField;
        String val;
        int empty = 0;
        
        ddField =  (DropDownField<Integer>)addRow.cells[0];
        if(ddField.getSelectedKey()==null)
            empty++;
        
        ddField =  (DropDownField<Integer>)addRow.cells[1];
        if(ddField.getSelectedKey()==null)
            empty++;
        
        strField = (StringField)addRow.cells[3];
        val = strField.getValue();
        if(val == null || (val != null && "".equals(val.trim())))
            empty++;
        
        return (empty != 3);        
        
    }

    public <T> boolean canDelete(TableWidget widget,TableDataRow<T> set,int row) {
        return false;
    }

    public <T> boolean canEdit(TableWidget widget,TableDataRow<T> set,int row,int col) {
        if(state == State.ADD || state == State.UPDATE || state == State.QUERY)
            return true;
        return false;
    }

    public <T> boolean canSelect(TableWidget widget,TableDataRow<T> set,int row) {
        if(state == State.ADD || state == State.UPDATE || state == State.QUERY)
            return true;
        return false;
    }

    public void onClick(Widget sender) {      
        if(sender == removeQCAnalyteButton)            
            onQCAnalyteButtonClicked();
        else if (sender == dictionaryLookUpButton)
            onDictionaryLookUpButtonClicked();
    }
    
    public void finishedEditing(SourcesTableWidgetEvents sender,int row,int col) {    
        Double doubleVal,darray[];
        String finalValue,systemName,value,token,strList[];
        QCGeneralPurposeRPC agrpc;
        boolean convert;
        int iter;
                
        if(sender == qcAnalyteTableWidget && col == 3) {            
            final int currRow = row;            
            value = ((StringField)qcAnalyteTableWidget.model.getRow(row).cells[3]).getValue();            
            darray = new Double[2];
            finalValue = "";
            systemName = getSelectedSystemName(row);                                  
            if(systemName!=null){
                if ("qc_analyte_dictionary".equals(systemName)) {
                    //
                    // Find out if this value is stored in the database if
                    // the type chosen was "Dictionary"
                    //
                    if (!"".equals(value.trim())) {   
                        agrpc = new QCGeneralPurposeRPC();
                        agrpc.stringValue = value;
                        screenService.call("getEntryIdForEntryText",agrpc,
                                           new SyncCallback<QCGeneralPurposeRPC>() {
                                               public void onSuccess(QCGeneralPurposeRPC result) {
                                                   //
                                                   // If this value is not stored in the
                                                   // database then add error to this
                                                   // cell in the "Value" column
                                                   //
                                                   if (result.key == null) {
                                                       qcAnalyteTableWidget.model.setCellError(currRow,3,
                                                                                                   consts.get("illegalDictEntryException"));
                                                   } else {  
                                                       qcAnalyteTableWidget.model.setCell(currRow,3,result.stringValue);
                                                   }
                                               }

                                               public void onFailure(Throwable caught) {
                                                   Window.alert(caught.getMessage());
                                                   window.clearStatus();
                                               }
                        });
                    }    
                } else if ("qc_analyte_numeric".equals(systemName)) {                                              
                    //
                    // Get the string that was entered if the type
                    // chosen was "Numeric" and try to break it up at
                    // the "," if it follows the pattern number,number
                    //
                    if (!"".equals(value.trim())) {    
                        strList = value.split(",");
                        convert = false;
                        if (strList.length == 2) {
                            for (iter = 0; iter < strList.length; iter++) {
                                token = strList[iter];
                                try {
                                    // 
                                    // Convert each number obtained
                                    // from the string and store its value
                                    // converted to double if its a valid
                                    // number, into an array
                                    //
                                    doubleVal = Double.valueOf(token);
                                    darray[iter] = doubleVal;
                                    convert = true;
                                } catch (NumberFormatException ex) {
                                    convert = false;
                                }
                            }
                        }
                        
                        if (convert) {
                            //
                            // If it's a valid string store the converted
                            // string back into the column otherwise add
                            // an error to the cell and store empty
                            // string into the cell
                            //  
                            if (darray[0].toString()
                                            .indexOf(".") == -1) {
                                finalValue = darray[0].toString() + ".0" + ",";
                            } else {
                                finalValue = darray[0].toString() + ",";
                            }
                            
                            if (darray[1].toString()
                                            .indexOf(".") == -1) {
                                finalValue += darray[1].toString() + ".0";
                            } else {
                                finalValue += darray[1].toString();
                            }
                            qcAnalyteTableWidget.model.setCell(currRow,3,finalValue);                            
                        } else {
                            qcAnalyteTableWidget.model.setCellError(currRow,3,
                                                                    consts.get("illegalNumericFormatException"));
                        }    
                    }  
                } else if ("qc_analyte_titer".equals(systemName)) {
                    //
                    // Get the string that was entered if the type chosen
                    // was "Titer" and try to
                    // break it up at the ":" if it follows the pattern
                    // "number:number"
                    //
                    if (!"".equals(value.trim())) {
                        strList = value.split(":");
                        convert = false;
                        if (strList.length == 2) {
                            for (iter = 0; iter < strList.length; iter++) {
                                token = strList[iter];
                                try {
                                    //
                                    //  Convert each number obtained from the
                                    // string and store its value converted to 
                                    // int if it's a valid number, into an array
                                    //
                                    Integer.parseInt(token);                                    
                                    convert = true;
                                } catch (NumberFormatException ex) {
                                    convert = false;
                                }
                            }
                        }
                        if (convert) {
                            //
                            // If it's a valid string store the converted
                            // string back into the column otherwise add
                            // an error to the cell and store empty
                            // string into the cell
                            //  
                            qcAnalyteTableWidget.model.setCell(currRow,3,value);                        
                        } else {
                            qcAnalyteTableWidget.model.setCellError(currRow,3,
                                                                    consts.get("illegalTiterFormatException"));
                        }
                    }                   
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
    
    private void onQCAnalyteButtonClicked() {        
        int index;
        
        index = qcAnalyteTableWidget.modelIndexList[qcAnalyteTableWidget.activeRow];
        if (index > -1)
            qcAnalyteTableWidget.model.deleteRow(index);
    }
    
    private void onDictionaryLookUpButtonClicked() {
        ScreenWindow modal;
        DictionaryEntryPickerScreen pickerScreen;
        
        pickerScreen = new DictionaryEntryPickerScreen();       
        modal = new ScreenWindow(null,"Dictionary LookUp","dictionaryEntryPickerScreen","Loading...",true,false);
        pickerScreen.addCommandListener(this);
        modal.setName(consts.get("chooseDictEntry"));
        modal.setContent(pickerScreen);
    }
    
    private void dictionaryLookupClosed(ArrayList<TableDataRow<Integer>> selectedRows) {              
        Integer key;
                
        key = DictionaryCache.getIdFromSystemName("qc_analyte_dictionary");  
        addQCAnalyteRows(selectedRows,key);                                                      
             
    }
    
    private void addQCAnalyteRows(ArrayList<TableDataRow<Integer>> selectedRows,
                                  Integer key) {
         List<String> entries;
         TableDataRow<Integer> row,set,dictSet;
         String entry;         

         if (selectedRows != null) {
             dictSet = new TableDataRow<Integer>(key); 
             entries = new ArrayList<String>();
             for (int iter = 0; iter < selectedRows.size(); iter++) {
                 set = selectedRows.get(iter);
                 entry = (String)(set.cells[0]).getValue();
                 if (entry != null && !entries.contains(entry.trim())) {
                     entries.add(entry);
                     row = (TableDataRow<Integer>)qcAnalyteTableWidget.model.createRow();
                     row.cells[1].setValue(dictSet);
                     row.cells[3].setValue(entry);                                                     
                     qcAnalyteTableWidget.model.addRow(row);                     
                 }
             }
             qcAnalyteTableWidget.model.refresh();
         }
     }      
    
    private String getSelectedSystemName(int row){
        TableDataRow<Integer> trow;
        String sysname;
        Integer key;
        if(row > -1) {
            trow = qcAnalyteTableWidget.model.getRow(row);
            key = (Integer)(((DropDownField<Integer>)trow.cells[1]).getSelectedKey());            
            sysname = DictionaryCache.getSystemNameFromId(key);
            return sysname;                     
        }    
        return null;
    }

    private TableDataModel<TableDataRow> getDictionaryIdEntryList(ArrayList list){
        TableDataModel<TableDataRow> m;
        TableDataRow<Integer> row;
        DictionaryDO dictDO;
        
        if(list == null)
            return null;
        
        m = new TableDataModel<TableDataRow>();
        m.add(new TableDataRow<Integer>(null,new StringObject("")));
        
        for(int i=0; i<list.size(); i++){
            row = new TableDataRow<Integer>(1);
            dictDO = (DictionaryDO)list.get(i);
            row.key = dictDO.getId();
            row.cells[0] = new StringObject(dictDO.getEntry());
            m.add(row);
        }
        
        return m;
    }
}
