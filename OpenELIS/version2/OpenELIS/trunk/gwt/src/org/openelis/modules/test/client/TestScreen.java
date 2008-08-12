package org.openelis.modules.test.client;

import org.openelis.gwt.common.FormRPC;
import org.openelis.gwt.common.data.DataModel;
import org.openelis.gwt.common.data.DataObject;
import org.openelis.gwt.common.data.NumberObject;
import org.openelis.gwt.common.data.PagedTreeField;
import org.openelis.gwt.common.data.TableField;
import org.openelis.gwt.common.data.TableRow;
import org.openelis.gwt.screen.ScreenPagedTree;
import org.openelis.gwt.screen.ScreenTableWidget;
import org.openelis.gwt.screen.ScreenVertical;
import org.openelis.gwt.widget.AToZTable;
import org.openelis.gwt.widget.AppButton;
import org.openelis.gwt.widget.ButtonPanel;
import org.openelis.gwt.widget.CollapsePanel;
import org.openelis.gwt.widget.FormInt;
import org.openelis.gwt.widget.table.TableController;
import org.openelis.gwt.widget.table.TableManager;
import org.openelis.metamap.TestMetaMap;
import org.openelis.modules.main.client.OpenELISScreenForm;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.ChangeListener;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.SourcesTabEvents;
import com.google.gwt.user.client.ui.TabListener;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.user.client.ui.TreeListener;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.xml.client.Document;
import com.google.gwt.xml.client.XMLParser;

public class TestScreen extends OpenELISScreenForm implements
                                                  ClickListener,
                                                  TabListener,
                                                  ChangeListener,
                                                  TreeListener,
                                                  TableManager{
    private static boolean loaded = false;
    
    private TestMetaMap TestMeta = new TestMetaMap();
    
    private static String tableXML = "<table width=\"360px\" key=\"analyteTable\" manager=\"this\" maxRows=\"1\" title=\"\" showError=\"false\" showScroll = \"false\">"+
                            "<headers>Value,"+
                            "Scriptlet,"+
                            "Reportable,"+
                            "Type</headers>"+
                            "<widths>120,100,20,100</widths>"+                                        
                            "<editors>"+
                                "<textbox case=\"mixed\"/>"+                                                                                     
                                "<autoDropdown  case=\"lower\"  width=\"120px\"/> "+                                            
                                //"<textbox case=\"mixed\"/>"+
                                "<check/>"+
                               // "<textbox case=\"mixed\"/>"+
                                "<autoDropdown  case=\"lower\" width=\"120px\"/> "+                                
                            "</editors>"+
                            "<fields>"+                                
                                "<string key=\"value\" required=\"true\"/>"+
                                "<dropdown key=\"scriptlet\" required=\"true\"/>"+                                
                                //"<string key=\"value2\" required=\"true\"/>"+
                                "<check key=\"reportable\" required=\"false\"/>"+
                                //"<string key=\"value3\" required=\"true\"/>"+
                                "<dropdown key=\"type\" required=\"false\"/>"+                                
                            "</fields>"+
                            "<sorts>false,false,false,false</sorts>"+
                            "<filters>false,false,false,false</filters>"+
                            "<colAligns>left,left,left,left</colAligns>"+
                           "</table>";
                            
                            
    public TestScreen() {
        super("org.openelis.modules.test.server.TestService",!loaded);
    }
    
    public void performCommand(Enum action, Object obj) {
        if (obj instanceof AppButton) {
            String baction = ((AppButton)obj).action;
            if (baction.startsWith("query:")) {
                getTests(baction.substring(6, baction.length()));
            }else
                super.performCommand(action, obj);
        } else {
            super.performCommand(action, obj);
        }
    }
    
    public void onClick(Widget sender) {
        

    }

    
    public boolean onBeforeTabSelected(SourcesTabEvents sender, int tabIndex) {
        //if (tabIndex == 0 && !((FormRPC)rpc.getField("details")).load) {
            //fillContactsModel();
        //}
      if(tabIndex ==1){
       final ScreenPagedTree tree = (ScreenPagedTree)widgets.get("analyteTree");
        tree.controller.setTreeListener(this);
        setBpanel((ButtonPanel) getWidget("buttons"));
        
        final ScreenVertical vp = (ScreenVertical) widgets.get("treeContainer");
        
        vp.clear();
        vp.remove(tree);
        
        window.setStatus("","spinnerIcon");
        
        
        
       // prepare the argument list for the getObject function
        //DataObject[] args = new DataObject[] {new DataObject()}; 
        screenService.getObject("getTreeModel" , null, new AsyncCallback(){
            public void onSuccess(Object result){
                vp.clear();
                vp.getPanel().add(tree);
                PagedTreeField treeField = (PagedTreeField)result;
                tree.load(treeField);
                
                window.setStatus("","");
            }
            
            public void onFailure(Throwable caught){
                Window.alert(caught.getMessage());
            }
         }); 
      } 
        return true;
    }

    private void getTests(String query) {
        if (state == FormInt.State.DISPLAY || state == FormInt.State.DEFAULT) {
            
            FormRPC rpc;

            rpc = (FormRPC)this.forms.get("queryByLetter");
            rpc.setFieldValue(TestMeta.getName(), query);
            commitQuery(rpc);
        }
    }
    
    public void onTabSelected(SourcesTabEvents sender, int tabIndex) {
       

    }
    
    public void afterDraw(boolean success) {
        loaded = true;
        setBpanel((ButtonPanel) getWidget("buttons"));        
        message.setText("Done");
                   
//      load other widgets
        AToZTable atozTable = (AToZTable) getWidget("azTable");        
        modelWidget.addCommandListener(atozTable);
        atozTable.modelWidget = modelWidget;
        addCommandListener(atozTable);
        
       ((CollapsePanel)getWidget("collapsePanel")).addChangeListener(atozTable);
        
        ButtonPanel atozButtons = (ButtonPanel)getWidget("atozButtons");
        atozButtons.addCommandListener(this);                           
        setBpanel((ButtonPanel)getWidget("buttons"));
               
        super.afterDraw(success);
    }

    public void onTreeItemSelected(TreeItem item) {
        // TODO Auto-generated method stub
        
    }

    public void onTreeItemStateChanged(TreeItem item) {  
       
        Document doc = XMLParser.parse(tableXML);
        
        final TreeItem finalTreeItem = item;        
        final ScreenTableWidget table = new ScreenTableWidget(doc.getDocumentElement(), this);
        DataObject[] args = new DataObject[] {new NumberObject()}; 
        for(int i =0 ; i < 5 ; i++){
         screenService.getObject("getAnalyte" , args, new AsyncCallback(){
            public void onSuccess(Object result){               
             table.load((TableField) result);  
             finalTreeItem.removeItems();
             finalTreeItem.addItem(table);
                          
            }
            
            public void onFailure(Throwable caught){
                Window.alert(caught.getMessage());
            }
         });
        } 
      }

    public boolean action(int row, int col, TableController controller) {
        // TODO Auto-generated method stub
        return false;
    }

    public boolean canDelete(int row, TableController controller) {
        // TODO Auto-generated method stub
        return false;
    }

    public boolean canEdit(int row, int col, TableController controller) {
        // TODO Auto-generated method stub
        return false;
    }

    public boolean canInsert(int row, TableController controller) {
        // TODO Auto-generated method stub
        return false;
    }

    public boolean canSelect(int row, TableController controller) {
        // TODO Auto-generated method stub
        return false;
    }

    public boolean doAutoAdd(TableRow addRow, TableController controller) {
        // TODO Auto-generated method stub
        return false;
    }

    public void finishedEditing(int row, int col, TableController controller) {
        // TODO Auto-generated method stub
        
    }

    public void getNextPage(TableController controller) {
        // TODO Auto-generated method stub
        
    }

    public void getPage(int page) {
        // TODO Auto-generated method stub
        
    }

    public void getPreviousPage(TableController controller) {
        // TODO Auto-generated method stub
        
    }

    public void rowAdded(int row, TableController controller) {
        // TODO Auto-generated method stub
        
    }

    public void setModel(TableController controller, DataModel model) {
        // TODO Auto-generated method stub
        
    }

    public void setMultiple(int row, int col, TableController controller) {
        // TODO Auto-generated method stub
        
    } 
        
  }

