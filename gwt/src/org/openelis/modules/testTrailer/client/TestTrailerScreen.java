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
package org.openelis.modules.testTrailer.client;

import org.openelis.gwt.common.data.deprecated.KeyListManager;
import org.openelis.gwt.common.data.deprecated.QueryStringField;
import org.openelis.gwt.common.data.deprecated.TableDataRow;
import org.openelis.gwt.common.deprecated.Query;
import org.openelis.gwt.screen.deprecated.CommandChain;
import org.openelis.gwt.screen.deprecated.ScreenInputWidget;
import org.openelis.gwt.screen.deprecated.ScreenTextArea;
import org.openelis.gwt.widget.deprecated.AppButton;
import org.openelis.gwt.widget.deprecated.ButtonPanel;
import org.openelis.gwt.widget.deprecated.ResultsTable;
import org.openelis.metamap.TestTrailerMetaMap;
import org.openelis.modules.main.client.OpenELISScreenForm;

import com.google.gwt.user.client.ui.TextBox;

public class TestTrailerScreen extends OpenELISScreenForm<TestTrailerForm,Query<TableDataRow<Integer>>> {
	
	private TextBox nameTextBox;
	private ScreenTextArea textArea;
    private KeyListManager keyList = new KeyListManager();
	
    private TestTrailerMetaMap TestTrailerMeta = new TestTrailerMetaMap();
    
	public TestTrailerScreen() {                
        super("org.openelis.modules.testTrailer.server.TestTrailerService");
        query = new Query<TableDataRow<Integer>>();
        getScreen(new TestTrailerForm());
    }
	
	 public void performCommand(Enum action, Object obj) {
	        if(obj instanceof AppButton){
	           String baction = ((AppButton)obj).action;
	           if(baction.indexOf("*") > -1){
	        	   getTestTrailers(baction);      
	           }else
                   super.performCommand(action, obj);
	        }else{
	            super.performCommand(action, obj);
	        }
	    }
	
	public void afterDraw(boolean success) {
	    ResultsTable atozTable = (ResultsTable)getWidget("azTable");
        ButtonPanel atozButtons = (ButtonPanel)getWidget("atozButtons");
        ButtonPanel bpanel = (ButtonPanel) getWidget("buttons");
        
        CommandChain chain = new CommandChain();
        chain.addCommand(this);
        chain.addCommand(bpanel);
        chain.addCommand(keyList);
        chain.addCommand(atozButtons);
        chain.addCommand(atozTable);
        
        //((CollapsePanel)getWidget("collapsePanel")).addChangeListener(atozTable);
           
        nameTextBox = (TextBox) getWidget(TestTrailerMeta.getName());
        startWidget = (ScreenInputWidget)widgets.get(TestTrailerMeta.getName());
        textArea = (ScreenTextArea) widgets.get(TestTrailerMeta.getText());

		super.afterDraw(success);
	}
	
	public void query() {
    		super.query();
    //		users cant query by text so disable it
    		textArea.enable(false);
    		
    }
	
	private void getTestTrailers(String query) {
		if (state == State.DISPLAY || state == State.DEFAULT) {
		    QueryStringField qField = new QueryStringField(TestTrailerMeta.getName());
            qField.setValue(query);
			commitQuery(qField);
		}
	}
}
