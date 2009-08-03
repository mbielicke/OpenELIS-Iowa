/**
 * Exhibit A - UIRF Open-source Based Public Software License.
 * 
 * The contents of this file are subject to the UIRF Open-source Based Public Software License(the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at openelis.uhl.uiowa.edu
 * 
 * Software distributed under the License is distributed on an "AS IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License for the specific language governing rights and limitations under the License.
 * 
 * The Original Code is OpenELIS code.
 * 
 * The Initial Developer of the Original Code is The University of Iowa. Portions created by The University of Iowa are Copyright 2006-2008. All Rights Reserved.
 * 
 * Contributor(s): ______________________________________.
 * 
 * Alternatively, the contents of this file marked "Separately-Licensed" may be used under the terms of a UIRF Software license ("UIRF Software License"), in which case the provisions of a UIRF Software License are applicable instead of those above.
 */
package org.openelis.modules.standardnotepicker.client;

import java.util.ArrayList;

import org.openelis.cache.DictionaryCache;
import org.openelis.domain.DictionaryDO;
import org.openelis.domain.NoteDO;
import org.openelis.domain.StandardNoteDO;
import org.openelis.gwt.common.SecurityUtil;
import org.openelis.gwt.common.rewrite.Query;
import org.openelis.gwt.common.rewrite.QueryData;
import org.openelis.gwt.event.ActionEvent;
import org.openelis.gwt.event.ActionHandler;
import org.openelis.gwt.event.DataChangeEvent;
import org.openelis.gwt.event.HasActionHandlers;
import org.openelis.gwt.event.StateChangeEvent;
import org.openelis.gwt.screen.rewrite.Screen;
import org.openelis.gwt.screen.rewrite.ScreenEventHandler;
import org.openelis.gwt.services.ScreenService;
import org.openelis.gwt.widget.CalendarLookUp;
import org.openelis.gwt.widget.TextBox;
import org.openelis.gwt.widget.rewrite.AppButton;
import org.openelis.gwt.widget.rewrite.TextArea;
import org.openelis.gwt.widget.tree.rewrite.TreeDataItem;
import org.openelis.gwt.widget.tree.rewrite.TreeRow;
import org.openelis.gwt.widget.tree.rewrite.TreeWidget;
import org.openelis.gwt.widget.tree.rewrite.event.LeafOpenedEvent;
import org.openelis.gwt.widget.tree.rewrite.event.LeafOpenedHandler;
import org.openelis.metamap.StandardNoteMetaMap;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Window;

public class EditNoteScreen extends Screen implements
                                          HasActionHandlers<EditNoteScreen.Action> {

    private ScreenService service;
    private NoteDO        noteDO;

    public enum Action {
        COMMIT, ABORT
    };

    protected TextArea                text, preview;
    protected TextBox                 search;
    protected AppButton               pasteButton, findButton;
    protected TreeWidget              tree;

    protected ArrayList<DictionaryDO> categoryList;

    private StandardNoteMetaMap       meta = new StandardNoteMetaMap();

    public EditNoteScreen() throws Exception {
        // Call base to get ScreenDef and draw screen
        super("OpenELISServlet?service=org.openelis.modules.standardnotepicker.server.EditNoteService");

        service = new ScreenService("OpenELISServlet?service=org.openelis.modules.standardnotepicker.server.EditNoteService");

        // Setup link between Screen and widget Handlers
        initialize();

        // Initialize Screen
        setState(State.DEFAULT);

    }

    private void initialize() {

        tree = (TreeWidget)def.getWidget("noteTree");
        
        tree.addLeafOpenedHandler(new LeafOpenedHandler() {
            public void onLeafOpened(LeafOpenedEvent event) {
                final TreeDataItem row = event.getItem();
                if (!row.isLoaded()) {
                    buildTree(row, find((Integer)row.key));
                    row.checkForChildren(false);

                }
            }
        });

        tree.addSelectionHandler(new SelectionHandler<TreeRow>() {
            public void onSelection(SelectionEvent<TreeRow> event) {
                TreeRow selectedRow = event.getSelectedItem();
                TreeDataItem item = selectedRow.item;

                if ("note".equals(item.leafType))
                    preview.setValue((String)item.data);
            }
        });
        
        final TextBox subject = (TextBox)def.getWidget("subject");
        addScreenHandler(subject, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                subject.setValue(noteDO.getSubject());
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                noteDO.setSubject(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                if (noteDO != null)
                    subject.enable("N".equals(noteDO.getIsExternal()));

            }
        });

        text = (TextArea)def.getWidget("text");
        addScreenHandler(text, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                text.setValue(noteDO.getText());
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                noteDO.setText(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                text.enable(true);

            }
        });

        search = (TextBox)def.getWidget("findTextBox");
        addScreenHandler(search, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                search.setValue(null);
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                // FIXME remove for now find();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                search.enable(true);

            }
        });

        preview = (TextArea)def.getWidget("preview");
        addScreenHandler(preview, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                preview.setValue(null);
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                // disable the paste button if the preview text is empty
                if ("".equals(preview.getValue()))
                    pasteButton.enable(false);
                else
                    pasteButton.enable(true);
            }

            public void onStateChange(StateChangeEvent<State> event) {
                preview.enable(false);

            }
        });

        pasteButton = (AppButton)def.getWidget("pasteButton");
        addScreenHandler(pasteButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                paste();
            }

            public void stateChange(StateChangeEvent<State> event) {
                pasteButton.enable(true);
            }
        });

        findButton = (AppButton)def.getWidget("findButton");
        addScreenHandler(findButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                if("".equals(search.getValue()))
                    buildTree();
                
                buildTree(find());
            }

            public void stateChange(StateChangeEvent<State> event) {
                findButton.enable(true);
            }
        });

        final AppButton commitButton = (AppButton)def.getWidget("commit");
        addScreenHandler(commitButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                commit();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                commitButton.enable(true);
            }
        });

        final AppButton abortButton = (AppButton)def.getWidget("abort");
        addScreenHandler(abortButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                abort();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                abortButton.enable(true);
            }
        });
    }

    private ArrayList<StandardNoteDO> find() {
        Query<StandardNoteDO> query = new Query();
        ArrayList<QueryData> list = new ArrayList<QueryData>();
        QueryData a, b;
        a = new QueryData();
        a.type = QueryData.Type.STRING;
        a.query = search.getValue();
        a.key = meta.getName();
        list.add(a);

        b = new QueryData();
        b.type = QueryData.Type.STRING;
        b.query = search.getValue();
        b.key = meta.getDescription();
        list.add(b);

        query.fields = list;

        return query(query);
    }

    private ArrayList<StandardNoteDO> find(Integer typeId) {
        Query<StandardNoteDO> query = new Query();
        ArrayList<QueryData> list = new ArrayList<QueryData>();
        QueryData field = new QueryData();
        field.type = QueryData.Type.INTEGER;
        field.query = String.valueOf(typeId);
        field.key = meta.getTypeId();
        list.add(field);

        query.fields = list;

        return query(query);
    }

    private ArrayList<StandardNoteDO> query(Query<StandardNoteDO> query) {
        window.setBusy("Querying...");

        try {
            Query<StandardNoteDO> q = service.call("query", query);
            window.clearStatus();

            return q.results;

        } catch (Exception e) {
            e.printStackTrace();
            Window.alert(e.getMessage());
            window.clearStatus();
            return null;
        }
    }

    private void paste() {
        String tmpValue;
        int cursorIndex = text.getCursorPos();
        text.setFocus(true);
        tmpValue = text.getText().substring(0, cursorIndex) + preview.getText()
                   + text.getText().substring(cursorIndex);
        text.setValue(tmpValue, true);
        text.setSelectionRange(cursorIndex, preview.getText().length());
    }

    public void commit() {

        ActionEvent.fire(this, Action.COMMIT, null);
        window.close();
    }

    public void abort() {
        ActionEvent.fire(this, Action.ABORT, null);
        window.close();
    }

    private void buildTree(ArrayList<StandardNoteDO> noteList) {
          Integer oldTypeId, currentTypeId;
          TreeDataItem catNode = null, noteNode = null;
          StandardNoteDO note;
        
          if(tree.numRows() > 0)
              tree.clear();
          
          oldTypeId = null;
          for(int i=0; i<noteList.size(); i++){
              note = noteList.get(i); 
              currentTypeId = note.getType();
              
              if(!currentTypeId.equals(oldTypeId)){
                 oldTypeId = currentTypeId;
                 DictionaryDO dictDO = DictionaryCache.getEntryFromId(currentTypeId);
                 
                 catNode = new TreeDataItem(1);
                 catNode.leafType = "category";
                 catNode.key = dictDO.getId();
                 catNode.cells.get(0).value = dictDO.getEntry();
                 catNode.open = true;
                 tree.addRow(catNode);
              }
              
              noteNode = new TreeDataItem(1);
              noteNode.leafType = "note";
              noteNode.key = noteDO.getId();
              noteNode.cells.get(0).value = note.getName() + " : "
                                                 + note.getDescription();
              noteNode.data = note.getText();
              catNode.addItem(noteNode);
          }
          
          tree.refresh(false);
          //DataChangeEvent.fire(this);
         
    }

    private void buildTree(TreeDataItem row, ArrayList<StandardNoteDO> noteList) {
        for (int i = 0; i < noteList.size(); i++) {
            StandardNoteDO noteDO = noteList.get(i);

            TreeDataItem treeModelItem = new TreeDataItem(1);
            treeModelItem.leafType = "note";
            treeModelItem.key = noteDO.getId();
            treeModelItem.cells.get(0).value = noteDO.getName() + " : "
                                               + noteDO.getDescription();
            treeModelItem.data = noteDO.getText();

            row.addItem(treeModelItem);
        }

        tree.refresh(true);
        //DataChangeEvent.fire(this);
    }

    private void buildTree() {
        if(tree.numRows() > 0)
            tree.clear();
        
        if (categoryList == null)
            categoryList = DictionaryCache.getListByCategorySystemName("standard_note_type");

        ArrayList<TreeDataItem> treeList = new ArrayList<TreeDataItem>();
        for (int i = 0; i < categoryList.size(); i++) {
            DictionaryDO dictDO = categoryList.get(i);

            TreeDataItem treeModelItem = new TreeDataItem(1);
            treeModelItem.leafType = "category";
            treeModelItem.checkForChildren(true);
            treeModelItem.key = dictDO.getId();
            treeModelItem.cells.get(0).value = dictDO.getEntry();

            treeList.add(treeModelItem);
        }

        tree.setModel(treeList);
        tree.refresh(false);
        //DataChangeEvent.fire(this);
    }

    /*
     * public void loadQuery(Query<StandardNoteDO> query) { ArrayList<StandardNoteDO> resultList = query.results;
     * 
     * if(resultList == null || resultList.size() == 0) { window.setDone("No records found"); }else window.setDone(consts.get("queryingComplete"));
     * 
     * }
     */

    public void setNote(NoteDO note) {
        noteDO = note;
        
        buildTree();
        DataChangeEvent.fire(this);
    }

    public HandlerRegistration addActionHandler(ActionHandler<Action> handler) {
        return addHandler(handler, ActionEvent.getType());
    }
}
