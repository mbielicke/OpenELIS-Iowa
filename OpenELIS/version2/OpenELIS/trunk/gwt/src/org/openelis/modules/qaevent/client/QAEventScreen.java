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
package org.openelis.modules.qaevent.client;

import java.util.ArrayList;

import org.openelis.cache.DictionaryCache;
import org.openelis.domain.DictionaryDO;
import org.openelis.gwt.common.Query;
import org.openelis.gwt.common.data.KeyListManager;
import org.openelis.gwt.common.data.QueryStringField;
import org.openelis.gwt.common.data.StringObject;
import org.openelis.gwt.common.data.TableDataModel;
import org.openelis.gwt.common.data.TableDataRow;
import org.openelis.gwt.screen.CommandChain;
import org.openelis.gwt.screen.ScreenTextArea;
import org.openelis.gwt.widget.AppButton;
import org.openelis.gwt.widget.ButtonPanel;
import org.openelis.gwt.widget.CollapsePanel;
import org.openelis.gwt.widget.Dropdown;
import org.openelis.gwt.widget.ResultsTable;
import org.openelis.metamap.QaEventMetaMap;
import org.openelis.modules.main.client.OpenELISScreenForm;

import com.google.gwt.user.client.rpc.SyncCallback;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

 public class QAEventScreen extends OpenELISScreenForm<QAEventForm,Query<TableDataRow<Integer>>> implements ClickListener{
   
     private TextBox tname = null;
     private Dropdown displayType = null;
     private Dropdown displayTest = null;
     private ScreenTextArea reportingText = null;
     private KeyListManager keyList = new KeyListManager();    
     
     private QaEventMetaMap QAEMeta = new QaEventMetaMap();  
     
     public QAEventScreen(){
         super("org.openelis.modules.qaevent.server.QAEventService"); 
         query = new Query<TableDataRow<Integer>>();
         getScreen(new QAEventForm());
     }
     
     public void performCommand(Enum action, Object obj) {
         if(obj instanceof AppButton) {
            String baction = ((AppButton)obj).action;           
            if(baction.startsWith("query:")){
                getQAEvents(baction.substring(6, baction.length()));      
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
             chain.addCommand(keyList);
             chain.addCommand(this);
             chain.addCommand(atozTable);
             chain.addCommand(atozButtons);
             
             ((CollapsePanel)getWidget("collapsePanel")).addChangeListener(atozTable);
                          
             tname = (TextBox)getWidget(QAEMeta.getName());
             displayType = (Dropdown)getWidget(QAEMeta.getTypeId());
             displayTest = (Dropdown)getWidget(QAEMeta.getTestId());

             reportingText = (ScreenTextArea)widgets.get(QAEMeta.getReportingText());       
         
            setTests(form.tests);
           
            form.tests = null;
            
            updateChain.add(afterUpdate);
            
            super.afterDraw(success); 
            
            ArrayList cache;
            TableDataModel<TableDataRow> model;
            cache = DictionaryCache.getListByCategorySystemName("qaevent_type");
            model = getDictionaryIdEntryList(cache);
            displayType.setModel(model);
            
        }           
        
        protected SyncCallback afterUpdate = new SyncCallback() {
            public void onFailure(Throwable caught) {   
            }
            public void onSuccess(Object result) {
                tname.setFocus(true);
                reportingText.enable(true);
            }
        };    
         
         public void query() {
            
            super.query();
            
         //set focus to the name field            
            tname.setFocus(true);
           
         // disable the text area so that it doesn't get included in the query, this is done because most 
         // databases don't support querying by BLOBs
            reportingText.enable(false);
            
        }                 
                      
         
         public void add(){                                  
             super.add();       
             
             reportingText.enable(true);
            tname.setFocus(true);            
         }
         
         private void getQAEvents(String query) {
             if (state == State.DISPLAY || state == State.DEFAULT) {
                 QueryStringField qField = new QueryStringField(QAEMeta.getName());
                 qField.setValue(query);
                 commitQuery(qField);
             }
         }
         
         private void setTests(TableDataModel<TableDataRow<Integer>> model) {
             displayTest.setModel(model);
         }
         
         private TableDataModel<TableDataRow> getDictionaryIdEntryList(ArrayList list){
             if(list == null)
                 return null;
             
             TableDataModel<TableDataRow> m = new TableDataModel<TableDataRow>();
             
             for(int i=0; i<list.size(); i++){
                 TableDataRow<Integer> row = new TableDataRow<Integer>(1);
                 DictionaryDO dictDO = (DictionaryDO)list.get(i);
                 row.key = dictDO.getId();
                 row.cells[0] = new StringObject(dictDO.getEntry());
                 m.add(row);
             }
             
             return m;
         }
 }
