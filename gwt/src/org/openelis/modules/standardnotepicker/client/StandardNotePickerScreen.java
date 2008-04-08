package org.openelis.modules.standardnotepicker.client;

import org.openelis.gwt.common.FormRPC;
import org.openelis.gwt.common.data.DataObject;
import org.openelis.gwt.common.data.NumberObject;
import org.openelis.gwt.common.data.PagedTreeField;
import org.openelis.gwt.common.data.StringObject;
import org.openelis.gwt.screen.ScreenAppButton;
import org.openelis.gwt.screen.ScreenPagedTree;
import org.openelis.gwt.screen.ScreenTextBox;
import org.openelis.gwt.screen.ScreenVertical;
import org.openelis.gwt.widget.AppButton;
import org.openelis.gwt.widget.ButtonPanel;
import org.openelis.modules.main.client.OpenELISScreenForm;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.user.client.ui.TreeListener;
import com.google.gwt.user.client.ui.Widget;

public class StandardNotePickerScreen extends OpenELISScreenForm implements TreeListener{

	//ScreenWindow window;
	public TextArea noteTextArea;
	
	public StandardNotePickerScreen(TextArea noteTextArea) {
		super("org.openelis.modules.standardnotepicker.server.StandardNotePickerService",false);
		this.noteTextArea = noteTextArea;		
        name="Standard Note Selection";
	}
	
	public void onClick(Widget sender) {
		String action = ((AppButton)sender).action;
		if(action.equals("find")){
			TextBox findTextBox = (TextBox)getWidget("findTextBox");
			 String queryString = findTextBox.getText()+(findTextBox.getText().endsWith("*") ? "" : "*");
			FormRPC queryRPC = (FormRPC) this.forms.get("queryByNameDescription");
			queryRPC.setFieldValue("standardNote.name", queryString);
			queryRPC.setFieldValue("standardNote.description", queryString);

			StringObject name = new StringObject();
	        StringObject desc = new StringObject();
	        name.setValue(queryString);
	        desc.setValue(queryString);
	        
	       // prepare the argument list for the getObject function
	        DataObject[] args = new DataObject[] {name,desc}; 
	        screenService.getObject("getTreeModel" , args, new AsyncCallback(){
	            public void onSuccess(Object result){
	            	ScreenVertical vp = (ScreenVertical) widgets.get("treeContainer");
	            	ScreenPagedTree tree = (ScreenPagedTree)widgets.get("noteTree");
	            	vp.clear();
	            	vp.getPanel().add(tree);
	                PagedTreeField treeField = (PagedTreeField)result;
	                tree.load(treeField);
	            }
	            
	            public void onFailure(Throwable caught){
	                Window.alert(caught.getMessage());
	            }
	         });        
		}
		super.onClick(sender);
	}
	
	public void afterDraw(boolean sucess) {
        final ScreenPagedTree tree = (ScreenPagedTree)widgets.get("noteTree");
        tree.controller.setTreeListener(this);
        bpanel = (ButtonPanel) getWidget("buttons");
        super.afterDraw(sucess);
        
        ScreenAppButton findButton = (ScreenAppButton)widgets.get("findButton");
        findButton.enable(true);
        ScreenTextBox findTextBox = (ScreenTextBox)widgets.get("findTextBox");
        findTextBox.enable(true);
        
        final ScreenVertical vp = (ScreenVertical) widgets.get("treeContainer");
        final HTML loadingHtml = new HTML();
        loadingHtml.setHTML("<img src=\"Images/OSXspinnerGIF.gif\"> Loading...");
        vp.clear();
        vp.remove(tree);
        vp.getPanel().add(loadingHtml);
        //add(loadingHtml);
        
        message.setText("done");
        
        StringObject name = new StringObject();
        StringObject desc = new StringObject();
        name.setValue("*");
        desc.setValue("*");
        
       // prepare the argument list for the getObject function
        DataObject[] args = new DataObject[] {name,desc}; 
        screenService.getObject("getTreeModel" , args, new AsyncCallback(){
            public void onSuccess(Object result){
            	vp.clear();
            	vp.getPanel().add(tree);
                PagedTreeField treeField = (PagedTreeField)result;
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
			Integer id = Integer.valueOf((String)item.getUserObject());
			final TreeItem finalTreeItem = item;
			finalTreeItem.removeItems();
			
			final ScreenPagedTree tree = (ScreenPagedTree)widgets.get("noteTree");
			final HTML loadingHtml = new HTML();
	        loadingHtml.setHTML("<img src=\"Images/OSXspinnerGIF.gif\"> Loading...");
			finalTreeItem.addItem(loadingHtml);
			
			FormRPC queryRPC = (FormRPC) this.forms.get("queryByNameDescription");

			StringObject name = new StringObject();
	        StringObject desc = new StringObject();
	        name.setValue(queryRPC.getFieldValue("standardNote.name")+(((String)queryRPC.getFieldValue("standardNote.name")).endsWith("*") ? "" : "*"));
	        desc.setValue(queryRPC.getFieldValue("standardNote.description")+(((String)queryRPC.getFieldValue("standardNote.name")).endsWith("*") ? "" : "*"));
	        
	        NumberObject idObj = new NumberObject();
			idObj.setType("integer");
			idObj.setValue(id);
	        
	       // prepare the argument list for the getObject function
	        DataObject[] args = new DataObject[] {idObj,name,desc}; 
	        
            screenService.getObject("getTreeModelSecondLevel", args, new AsyncCallback(){
	            public void onSuccess(Object result){
	               finalTreeItem.removeItems();
	               tree.controller.model.addTextChildItems(finalTreeItem, (String)((StringObject)result).getValue());	           
	            }
	            
	            public void onFailure(Throwable caught){
	            	finalTreeItem.removeItems();
	                Window.alert(caught.getMessage());
	            }
	         });        
		
		}catch(NumberFormatException e){
			//this means that it is the bottom level...
		}
	}
	
	public void abort() {
		window.close();
	}
	
	public void commit() {
		TextArea textArea = (TextArea)getWidget("noteText");
		noteTextArea.setText(noteTextArea.getText()+textArea.getText());
		
		window.close();
	}
}
