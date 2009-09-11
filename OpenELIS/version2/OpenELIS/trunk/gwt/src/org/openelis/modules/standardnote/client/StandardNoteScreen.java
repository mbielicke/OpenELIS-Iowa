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
package org.openelis.modules.standardnote.client;

import java.util.ArrayList;

import org.openelis.cache.DictionaryCache;
import org.openelis.domain.DictionaryDO;
import org.openelis.gwt.common.data.deprecated.KeyListManager;
import org.openelis.gwt.common.data.deprecated.QueryStringField;
import org.openelis.gwt.common.data.deprecated.StringObject;
import org.openelis.gwt.common.data.deprecated.TableDataModel;
import org.openelis.gwt.common.data.deprecated.TableDataRow;
import org.openelis.gwt.common.deprecated.Query;
import org.openelis.gwt.screen.deprecated.CommandChain;
import org.openelis.gwt.screen.deprecated.ScreenInputWidget;
import org.openelis.gwt.screen.deprecated.ScreenTextArea;
import org.openelis.gwt.widget.deprecated.AppButton;
import org.openelis.gwt.widget.deprecated.ButtonPanel;
import org.openelis.gwt.widget.deprecated.CollapsePanel;
import org.openelis.gwt.widget.deprecated.Dropdown;
import org.openelis.gwt.widget.deprecated.ResultsTable;
import org.openelis.metamap.StandardNoteMetaMap;
import org.openelis.modules.main.client.OpenELISScreenForm;

import com.google.gwt.user.client.ui.TextBox;

public class StandardNoteScreen extends OpenELISScreenForm<StandardNoteForm,Query<TableDataRow<Integer>>> {

	private ScreenTextArea textArea;
	private TextBox nameTextbox;
    private KeyListManager keyList = new KeyListManager();
    private Dropdown noteType;
	
    
    private StandardNoteMetaMap StandardNoteMeta = new StandardNoteMetaMap();
    
	public StandardNoteScreen() {                
        super("org.openelis.modules.standardnote.server.StandardNoteService");
        query = new Query<TableDataRow<Integer>>();
        getScreen(new StandardNoteForm());
    }

    public void performCommand(Enum action, Object obj) {
        if(obj instanceof AppButton){
           String baction = ((AppButton)obj).action;
           if(baction.startsWith("query:")){
        	   getStandardNotes(baction.substring(6, baction.length()));      
           }else
               super.performCommand(action,obj);
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
        chain.addCommand(atozTable);
        chain.addCommand(atozButtons);
        chain.addCommand(keyList);
        chain.addCommand(bpanel);
        
        //((CollapsePanel)getWidget("collapsePanel")).addChangeListener(atozTable);
        
        textArea = (ScreenTextArea)widgets.get(StandardNoteMeta.getText());
        nameTextbox = (TextBox)getWidget(StandardNoteMeta.getName());
        noteType = (Dropdown)getWidget(StandardNoteMeta.getTypeId());
        
        startWidget = (ScreenInputWidget)widgets.get(StandardNoteMeta.getName());
        
        super.afterDraw(success);
		
		ArrayList cache;
        TableDataModel<TableDataRow> model;
        cache = DictionaryCache.getListByCategorySystemName("standard_note_type");
        model = getDictionaryIdEntryList(cache);
        noteType.setModel(model);
	}
	
	public void query() {
		super.query();
		
		//users cant query by text so disable it
		textArea.enable(false);

		nameTextbox.setFocus(true);
    }

	private void getStandardNotes(String query) {
    	if (state == State.DISPLAY || state == State.DEFAULT) {
    	    QueryStringField qField = new QueryStringField(StandardNoteMeta.getName());
            qField.setValue(query);
    		commitQuery(qField);
    	}
    }
	
	private TableDataModel<TableDataRow> getDictionaryIdEntryList(ArrayList list){
	    TableDataModel<TableDataRow> m = new TableDataModel<TableDataRow>();
        TableDataRow<Integer> row;
        
        if(list == null)
            return m;
        
        m = new TableDataModel<TableDataRow>();
        m.add(new TableDataRow<Integer>(null,new StringObject("")));
        
        for(int i=0; i<list.size(); i++){
            row = new TableDataRow<Integer>(1);
            DictionaryDO dictDO = (DictionaryDO)list.get(i);
            row.key = dictDO.getId();
            row.cells[0] = new StringObject(dictDO.getEntry());
            m.add(row);
        }
        
        return m;
    }
}
