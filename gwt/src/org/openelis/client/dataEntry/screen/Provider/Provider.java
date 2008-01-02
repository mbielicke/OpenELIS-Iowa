package org.openelis.client.dataEntry.screen.Provider;

import org.openelis.gwt.client.screen.AppScreenForm;
import org.openelis.gwt.client.screen.ScreenPagedTree;
import org.openelis.gwt.client.widget.ButtonPanel;
import org.openelis.gwt.client.widget.FormInt;
import org.openelis.gwt.client.widget.pagedtree.TreeModel;
import org.openelis.gwt.client.widget.table.TableWidget;
import org.openelis.gwt.common.FormRPC;
import org.openelis.gwt.common.data.OptionField;
import org.openelis.gwt.common.data.StringField;
import org.openelis.gwt.common.data.TableRow;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.ServiceDefTarget;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SourcesTabEvents;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class Provider extends AppScreenForm{
    //private ConstantsWithLookup openElisConstants = (ConstantsWithLookup) AppScreen.getWidgetMap().get("AppConstants");
    
    private static ProviderServletIntAsync screenService = (ProviderServletIntAsync) GWT
    .create(ProviderServletInt.class);
    
    private static ServiceDefTarget target = (ServiceDefTarget) screenService;

    private ScreenPagedTree randomTree = null;
    private VerticalPanel vp = null; 
    private Widget selected;
    private int tabSelectedIndex = 0;    
   
    public Provider(){
        super();
        String base = GWT.getModuleBaseURL();
        base += "ProviderServlet";
        target.setServiceEntryPoint(base);
        service = screenService;
        formService = screenService;        
        getXML();
    }
    
    public void afterDraw(boolean success) {
        bpanel = (ButtonPanel) getWidget("buttons");
        
        message.setText("done");
        
        TableWidget provideNamesTable = (TableWidget) getWidget("providersTable");
        modelWidget.addChangeListener(provideNamesTable.controller);
        
        ((ProviderNamesTable) provideNamesTable.controller.manager).setProviderForm(this);
        
        Button removeContactButton = (Button) getWidget("removeAddressButton");
        removeContactButton.setEnabled(false);                      
        
        //ProviderAddressesTable  provAddressTable = (ProviderAddressesTable)((TableWidget) getWidget("providerAddressTable")).controller.manager; 
        //provAddressTable.setProviderForm(this);
        //provAddressTable.disableRows = true;
        
        randomTree = (ScreenPagedTree) widgets.get("notesTree");
        vp = (VerticalPanel)randomTree.getParent();
        randomTree.controller.setTreeListener(this);
               
        String woutPx = randomTree.controller.view.height.replaceAll("px", "");
        int intH = new Integer(woutPx).intValue() + 150;
      
         ScrollPanel scrollableView = (ScrollPanel)randomTree.controller.getScrollableView("100%", new Integer(intH).toString()+"px");              
         vp.add(scrollableView);             
        super.afterDraw(success);
    }
      
   
        
    public void up(int state) {      
        StringField note = (StringField)rpc.getField("usersNote");  
        note.setValue("");
        
        StringField subject = (StringField)rpc.getField("usersSubject");  
        subject.setValue("");
        super.up(state);      
    }
    
    public void add(int state){
        OptionField provOpt =  (OptionField)rpc.getField("providerType");
        provOpt.getOptions().clear();
        provOpt.addOption("118","Nurse");
        provOpt.addOption("119","Physician");
        
        super.add(state);
        
        Button removeContactButton = (Button) getWidget("removeAddressButton");
        removeContactButton.setEnabled(true);
        
        ProviderAddressesTable  provAddressTable = (ProviderAddressesTable)((TableWidget) getWidget("providerAddressTable")).controller.manager; 
        provAddressTable.disableRows = false;
              
    }
    
    public void afterUpdate(boolean success) {
        super.afterUpdate(success);
        
        Button removeContactButton = (Button) getWidget("removeAddressButton");
        removeContactButton.setEnabled(true);
        
        ProviderAddressesTable  provAddressTable = (ProviderAddressesTable)((TableWidget) getWidget("providerAddressTable")).controller.manager; 
        provAddressTable.disableRows = false;                
    }
       
    
    public void onClick(Widget sender) {
        if (sender == widgets.get("a")) {
            getProviders("a", sender);
        } else if (sender == widgets.get("b")) {
            getProviders("b", sender);
        } else if (sender == widgets.get("c")) {
            getProviders("c", sender);
        } else if (sender == widgets.get("d")) {
            getProviders("d", sender);
        } else if (sender == widgets.get("e")) {
            getProviders("e", sender);
        } else if (sender == widgets.get("f")) {
            getProviders("f", sender);
        } else if (sender == widgets.get("g")) {
            getProviders("g", sender);
        } else if (sender == widgets.get("h")) {
            getProviders("h", sender);
        } else if (sender == widgets.get("i")) {
            getProviders("i", sender);
        } else if (sender == widgets.get("j")) {
            getProviders("j", sender);
        } else if (sender == widgets.get("k")) {
            getProviders("k", sender);
        } else if (sender == widgets.get("l")) {
            getProviders("l", sender);
        } else if (sender == widgets.get("m")) {
            getProviders("m", sender);
        } else if (sender == widgets.get("n")) {
            getProviders("n", sender);
        } else if (sender == widgets.get("o")) {
            getProviders("o", sender);
            setStyleNameOnButton(sender);
        } else if (sender == widgets.get("p")) {
            getProviders("p", sender);
        } else if (sender == widgets.get("q")) {
            getProviders("q", sender);
        } else if (sender == widgets.get("r")) {
            getProviders("r", sender);
        } else if (sender == widgets.get("s")) {
            getProviders("s", sender);
        } else if (sender == widgets.get("t")) {
            getProviders("t", sender);
        } else if (sender == widgets.get("u")) {
            getProviders("u", sender);
        } else if (sender == widgets.get("v")) {
            getProviders("v", sender);
        } else if (sender == widgets.get("w")) {
            getProviders("w", sender);
        } else if (sender == widgets.get("x")) {
            getProviders("x", sender);
        } else if (sender == widgets.get("y")) {
            getProviders("y", sender);
        } else if (sender == widgets.get("z")) {
            getProviders("z", sender);
        }else if (sender == widgets.get("removeAddressButton")) {            
            TableWidget provAddTable = (TableWidget) getWidget("providerAddressTable");
            int selectedRow = provAddTable.controller.selected;
            if (selectedRow > -1
                    && provAddTable.controller.model.numRows() > 1) {
                TableRow row = provAddTable.controller.model
                        .getRow(selectedRow);
                
                row.setShow(false);
                // delete the last row of the table because it is autoadd
                provAddTable.controller.model
                        .deleteRow(provAddTable.controller.model.numRows() - 1);
                // reset the model
                provAddTable.controller.reset();
                // need to set the deleted flag to "Y" also
                StringField deleteFlag = new StringField();
                deleteFlag.setValue("Y");

                row.addHidden("deleteFlag", deleteFlag);
            }  
        }
    }
        
    
    private void getProviders(String letter, Widget sender) {
        // we only want to allow them to select a letter if they are in display
        // mode..
        if (bpanel.getState() == FormInt.DISPLAY) {

            FormRPC letterRPC = (FormRPC) this.forms.get("queryByLetter");
            letterRPC.setFieldValue("lastName", letter.toUpperCase() + "*");
             
            commitQuery(letterRPC);
            
            setStyleNameOnButton(sender);
            
        }
    }
    
    protected Widget setStyleNameOnButton(Widget sender) {
        sender.addStyleName("current");
        if (selected != null)
            selected.removeStyleName("current");
        selected = sender;
        return sender;
    }

    public void onTabSelected(SourcesTabEvents sources, int index) {
        tabSelectedIndex = index;
        // we need to do a provider addresses table reset so that it will always show
        // the data
        if (index == 0 && bpanel.getState() == FormInt.DISPLAY) {
            TableWidget contacts = (TableWidget) getWidget("providerAddressTable");
            contacts.controller.model.deleteRow(contacts.controller.model
                    .numRows() - 1);
            contacts.controller.reset();
        }
        super.onTabSelected(sources, index);
    }    
    
    public void onTreeItemStateChanged(TreeItem item) {
        final TreeItem currItem = item;
        if (currItem.getChildCount() > 0) {
            if (currItem.getChild(0).getText().equals("dummy"))
                currItem.removeItems();
            else
                return;
        }

        if (currItem.getUserObject() != null) {
            screenService.getNoteTreeSecondLevelXml((String) currItem
                    .getUserObject(), false, new AsyncCallback() {

                public void onSuccess(Object result) {

                    try {
                       /* Document doc = XMLParser.parse((String) result);
                        Node node = doc.getDocumentElement();

                        NodeList items = node.getChildNodes();
                        for (int i = 0; i < items.getLength(); i++) {
                            if (items.item(i).getNodeType() == Node.ELEMENT_NODE) {
                                TreeItem childitem = createTreeItem(items
                                        .item(i));
                                currItem.addItem(childitem);
                            }

                        }*/
                        
                     TreeModel model = randomTree.controller.model;
                     model.addTextChildItems(currItem, (String)result);

                    } catch (Exception e) {
                        Window.alert(e.getMessage());
                    }
                }

                public void onFailure(Throwable caught) {
                    Window.alert(caught.getMessage());
                }
            });
        }

       
    }

    /*private TreeItem createTreeItem(Node node) {

        String itemText = null;
        Object userObject = null;
        ScreenLabel label = null;
        if (node.getAttributes().getNamedItem("text") != null) {
            itemText = node.getAttributes().getNamedItem("text").getNodeValue();
        }
        if (node.getAttributes().getNamedItem("value") != null) {
            userObject = node.getAttributes().getNamedItem("value")
                    .getNodeValue();
        }

        TreeItem item = null;
        if (itemText != null) {
            label = new ScreenLabel(itemText, userObject);
        }

        if (label != null) {
            // initWidget(label);
            item = new TreeItem(label);
            // item.setText(((Label)label.getWidget()).getText());
        }
        item.setUserObject(userObject);

        NodeList items = node.getChildNodes();
        for (int i = 0; i < items.getLength(); i++) {
            if (items.item(i).getNodeType() == Node.ELEMENT_NODE) {
                item.addItem(createTreeItem(items.item(i)));
            }
        }
        return item;
    }*/
}
