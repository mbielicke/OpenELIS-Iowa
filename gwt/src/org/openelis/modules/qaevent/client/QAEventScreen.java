package org.openelis.modules.qaevent.client;

import org.openelis.gwt.common.FormRPC;
import org.openelis.gwt.common.data.DataModel;
import org.openelis.gwt.screen.ScreenAutoDropdown;
import org.openelis.gwt.screen.ScreenTextArea;
import org.openelis.gwt.widget.AToZPanel;
import org.openelis.gwt.widget.AutoCompleteDropdown;
import org.openelis.gwt.widget.ButtonPanel;
import org.openelis.gwt.widget.FormInt;
import org.openelis.metamap.QaEventMetaMap;
import org.openelis.modules.main.client.OpenELISScreenForm;

import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

 public class QAEventScreen extends OpenELISScreenForm implements ClickListener{
   
     private TextBox tname = null;
     private ScreenAutoDropdown displayType = null;
     private ScreenAutoDropdown displayTest = null;
     private ScreenTextArea reportingText = null;
     
     private static boolean loaded = false;
     
     private static DataModel qaEventTypeDropDown = null;
     private static DataModel testDropDown = null;
     
     private QaEventMetaMap QAEMeta = new QaEventMetaMap();  
     
     public QAEventScreen(){
         super("org.openelis.modules.qaevent.server.QAEventService",!loaded);  
     }
     
         public void onChange(Widget sender) {
         
         if(sender == getWidget("atozButtons")){           
            String action = ((ButtonPanel)sender).buttonClicked.action;           
            if(action.startsWith("query:")){
                getQAEvents(action.substring(6, action.length()));      
            }
         }else{
             super.onChange(sender);
         }
     }

        public void onClick(Widget arg0) {
            // TODO Auto-generated method stub
            
        }

        public void afterDraw(boolean success) {
             loaded = true;
             
             setBpanel((ButtonPanel) getWidget("buttons"));                           
             
             AToZPanel atozTable = (AToZPanel) getWidget("hideablePanel");
             modelWidget.addChangeListener(atozTable);
             addChangeListener(atozTable);
             
             ButtonPanel atozButtons = (ButtonPanel)getWidget("atozButtons");
             atozButtons.addChangeListener(this);
             
             tname = (TextBox)getWidget(QAEMeta.getName());
             displayType = (ScreenAutoDropdown)widgets.get(QAEMeta.getTypeId());
             displayTest = (ScreenAutoDropdown)widgets.get(QAEMeta.getTestId());

             reportingText = (ScreenTextArea)widgets.get(QAEMeta.getReportingText());       
         
            //load type and test dropdowns
           if(qaEventTypeDropDown == null){
               qaEventTypeDropDown = (DataModel)initData.get("qaevent");               
               testDropDown = (DataModel)initData.get("tests");
           }       

                                        
            ((AutoCompleteDropdown)displayType.getWidget()).setModel(qaEventTypeDropDown);
            ((AutoCompleteDropdown)displayTest.getWidget()).setModel(testDropDown);
                    
                
            super.afterDraw(success); 
        }                 
         
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
         
        public void afterUpdate(boolean success) {
            super.afterUpdate(success);
            
            //set focus to the name field
            tname.setFocus(true);
            reportingText.enable(true);
        }
         
         private void getQAEvents(String query) {
             if (state == FormInt.State.DISPLAY || state == FormInt.State.DEFAULT) {

                 FormRPC letterRPC = (FormRPC) this.forms.get("queryByLetter");
                 letterRPC.setFieldValue(QAEMeta.getName(), query);
                  
                 commitQuery(letterRPC);
             }
         }
         
 }