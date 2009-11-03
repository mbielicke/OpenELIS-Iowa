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
package org.openelis.modules.analyte.client;

import com.google.gwt.user.client.ui.TextBox;

import org.openelis.gwt.common.Query;
import org.openelis.gwt.common.data.KeyListManager;
import org.openelis.gwt.common.data.QueryStringField;
import org.openelis.gwt.common.data.TableDataRow;
import org.openelis.gwt.screen.AppScreenForm;
import org.openelis.gwt.screen.CommandChain;
import org.openelis.gwt.screen.ScreenInputWidget;
import org.openelis.gwt.widget.AppButton;
import org.openelis.gwt.widget.ButtonPanel;
import org.openelis.gwt.widget.CollapsePanel;
import org.openelis.gwt.widget.ResultsTable;
import org.openelis.metamap.AnalyteMetaMap;
import org.openelis.modules.main.client.OpenELISScreenForm;

public class AnalyteScreen extends OpenELISScreenForm<AnalyteForm,Query<TableDataRow<Integer>> > {
	
	private TextBox nameTextBox;
    private KeyListManager keyList = new KeyListManager();
    private static final AnalyteMetaMap Meta = new AnalyteMetaMap();

	public AnalyteScreen() {                
        super("org.openelis.modules.analyte.server.AnalyteService");
        query = new Query<TableDataRow<Integer>>();
        getScreen(new AnalyteForm());
    }
     
    public void performCommand(Enum action, Object obj) {
        if(obj instanceof AppButton){
            String actionString = ((AppButton)obj).action;
            if(actionString.startsWith("query:")){
                getAnalytes(actionString.substring(6, actionString.length()));
            }else 
                super.performCommand(action, obj);
        }else{
            super.performCommand(action, obj);
        }
    }
	
	public void afterDraw(boolean success) {
        ButtonPanel atozButtons = (ButtonPanel)getWidget("atozButtons");
        ButtonPanel bpanel = (ButtonPanel) getWidget("buttons");
        ResultsTable atozTable = (ResultsTable)getWidget("azTable");
        
        CommandChain chain = new CommandChain();
        chain.addCommand(this);
        chain.addCommand(bpanel);
        chain.addCommand(keyList);
        chain.addCommand(atozTable);
        chain.addCommand(atozButtons);
        
        ((CollapsePanel)getWidget("collapsePanel")).addChangeListener(atozTable);
        
        nameTextBox = (TextBox) getWidget(Meta.getName());
        
        startWidget = (ScreenInputWidget)widgets.get(Meta.getName());

		super.afterDraw(success);
	}
	
	private void getAnalytes(String query) {
		if (state == State.DISPLAY || state == State.DEFAULT) {
		    QueryStringField qField = new QueryStringField(Meta.getName());
            qField.setValue(query);
            commitQuery(qField);
		}
	}
}
