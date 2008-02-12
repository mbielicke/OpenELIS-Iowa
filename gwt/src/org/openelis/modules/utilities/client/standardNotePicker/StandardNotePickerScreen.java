package org.openelis.modules.utilities.client.standardNotePicker;

import org.openelis.gwt.common.data.PagedTreeField;
import org.openelis.gwt.screen.AppScreenForm;
import org.openelis.gwt.screen.ScreenPagedTree;
import org.openelis.gwt.widget.ButtonPanel;
import org.openelis.gwt.widget.PopupWindow;
import org.openelis.gwt.widget.pagedtree.TreeModel;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.ServiceDefTarget;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.user.client.ui.TreeListener;

public class StandardNotePickerScreen extends AppScreenForm implements TreeListener{

	private static StandardNotePickerServletIntAsync screenService = (StandardNotePickerServletIntAsync) GWT
	.create(StandardNotePickerServletInt.class);
	
	private static ServiceDefTarget target = (ServiceDefTarget) screenService;
	PopupWindow window;
	
	public StandardNotePickerScreen() {
		super();
		String base = GWT.getModuleBaseURL();
		base += "StandardNotePickerServlet";
		target.setServiceEntryPoint(base);
		service = screenService;
		formService = screenService;
		getXML();
	}
	
	public void afterDraw(boolean sucess) {
        window = new PopupWindow("Choose Standard Note");
        window.content.add(this);
        window.content.setStyleName("Content");
        window.setContentPanel(window.content);
        ScreenPagedTree tree = (ScreenPagedTree)widgets.get("noteTree");
        tree.controller.setTreeListener(this);
        bpanel = (ButtonPanel) getWidget("buttons");
        
       // ((ChooseMFKTable)((TableWidget)getWidget("chooseMFKTable")).controller.manager).setMFKForm(this);
        DeferredCommand.addCommand(new Command() {
            public void execute() {
                window.setVisible(true);
                window.setPopupPosition((Window.getClientWidth() - window.getOffsetWidth())/2,100);
                window.size();
            }
        });
        
       
        screenService.getTreeModel(new AsyncCallback(){
            public void onSuccess(Object result){
                ScreenPagedTree tree = (ScreenPagedTree)widgets.get("noteTree");
                PagedTreeField treeField = new PagedTreeField();
                treeField.setValue((TreeModel)result);
                tree.load(treeField);
            }
            
            public void onFailure(Throwable caught){
                Window.alert(caught.getMessage());
            }
         });        
	}
	
	public void onTreeItemSelected(TreeItem item) {
		try{
			int id = Integer.parseInt((String)item.getUserObject());
			item.setState(!item.getState(), true);
		
		}catch(NumberFormatException e){
			TextArea textArea = (TextArea)getWidget("noteText");
			textArea.setText((String)item.getUserObject());
		}
	}
	
	public void onTreeItemStateChanged(TreeItem item) {
		try{
			int id = Integer.parseInt((String)item.getUserObject());
			final TreeItem finalTreeItem = item;
			finalTreeItem.removeItems();
//			need async request to xml for 2nd layer
			screenService.getTreeModelSecondLevel(id,new AsyncCallback(){
	            public void onSuccess(Object result){
	                ScreenPagedTree tree = (ScreenPagedTree)widgets.get("noteTree");
	                tree.controller.model.addTextChildItems(finalTreeItem, (String)result);	           
	            }
	            
	            public void onFailure(Throwable caught){
	                Window.alert(caught.getMessage());
	            }
	         });        
		
		}catch(NumberFormatException e){
			//this means that it is the bottom level...
		}
	}
	
	public void abort(int state) {
		window.close();
	}
	
	public void commit(int state) {
		Window.alert("set the value");
		window.close();
	}
}
