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

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

import org.openelis.gwt.common.DefaultRPC;
import org.openelis.gwt.common.Form;
import org.openelis.gwt.common.data.DataModel;
import org.openelis.gwt.common.data.KeyListManager;
import org.openelis.gwt.screen.CommandChain;
import org.openelis.gwt.screen.ScreenTextArea;
import org.openelis.gwt.widget.AToZTable;
import org.openelis.gwt.widget.AppButton;
import org.openelis.gwt.widget.ButtonPanel;
import org.openelis.gwt.widget.CollapsePanel;
import org.openelis.gwt.widget.Dropdown;
import org.openelis.gwt.widget.FormInt;
import org.openelis.metamap.QaEventMetaMap;
import org.openelis.modules.main.client.OpenELISScreenForm;

 public class QAEventScreen extends OpenELISScreenForm<DefaultRPC,Form,Integer> implements ClickListener{
   
     private TextBox tname = null;
     private Dropdown displayType = null;
     private Dropdown displayTest = null;
     private ScreenTextArea reportingText = null;
     private KeyListManager keyList = new KeyListManager();
     
     private static boolean loaded = false;
     
     private static DataModel qaEventTypeDropDown = null;
     private static DataModel testDropDown = null;
     
     private QaEventMetaMap QAEMeta = new QaEventMetaMap();  
     
     public QAEventScreen(){
         super("org.openelis.modules.qaevent.server.QAEventService",!loaded, new DefaultRPC());  
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
             loaded = true;
             AToZTable atozTable = (AToZTable) getWidget("azTable");
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
         
            //load type and test dropdowns
           if(qaEventTypeDropDown == null){
               qaEventTypeDropDown = (DataModel)initData.get("qaevent");               
               testDropDown = (DataModel)initData.get("tests");
           }       

                                        
           // ((Dropdown)displayType.getWidget()).setModel(qaEventTypeDropDown);
           // ((Dropdown)displayTest.getWidget()).setModel(testDropDown);
              
           displayType.setModel(qaEventTypeDropDown);
           displayTest.setModel(testDropDown);
           
            updateChain.add(afterUpdate);
            
            super.afterDraw(success); 
        }           
        
        protected AsyncCallback afterUpdate = new AsyncCallback() {
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
             if (state == FormInt.State.DISPLAY || state == FormInt.State.DEFAULT) {

                 Form letter = (Form)forms.get("queryByLetter");
                 letter.setFieldValue(QAEMeta.getName(), query);
                  
                 commitQuery(letter);
             }
         }
         
 }
