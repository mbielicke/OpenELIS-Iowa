/**
 * Exhibit A - UIRF Open-source Based Public Software License.
 * 
 * The contents of this file are subject to the UIRF Open-source Based Public
 * Software License(the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * openelis.uhl.uiowa.edu
 * 
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License for
 * the specific language governing rights and limitations under the License.
 * 
 * The Original Code is OpenELIS code.
 * 
 * The Initial Developer of the Original Code is The University of Iowa.
 * Portions created by The University of Iowa are Copyright 2006-2008. All
 * Rights Reserved.
 * 
 * Contributor(s): ______________________________________.
 * 
 * Alternatively, the contents of this file marked "Separately-Licensed" may be
 * used under the terms of a UIRF Software license ("UIRF Software License"), in
 * which case the provisions of a UIRF Software License are applicable instead
 * of those above.
 */
package org.openelis.modules.note.client;

import java.util.ArrayList;

import org.openelis.cache.CategoryCache;
import org.openelis.cache.DictionaryCache;
import org.openelis.domain.DictionaryDO;
import org.openelis.domain.NoteViewDO;
import org.openelis.domain.StandardNoteDO;
import org.openelis.gwt.common.DataBaseUtil;
import org.openelis.gwt.common.FieldErrorException;
import org.openelis.gwt.common.NotFoundException;
import org.openelis.gwt.common.data.Query;
import org.openelis.gwt.common.data.QueryData;
import org.openelis.gwt.event.ActionEvent;
import org.openelis.gwt.event.ActionHandler;
import org.openelis.gwt.event.DataChangeEvent;
import org.openelis.gwt.event.HasActionHandlers;
import org.openelis.gwt.event.StateChangeEvent;
import org.openelis.gwt.screen.Screen;
import org.openelis.gwt.screen.ScreenDefInt;
import org.openelis.gwt.screen.ScreenEventHandler;
import org.openelis.gwt.widget.AppButton;
import org.openelis.gwt.widget.QueryFieldUtil;
import org.openelis.gwt.widget.TextArea;
import org.openelis.gwt.widget.TextBox;
import org.openelis.gwt.widget.tree.TreeDataItem;
import org.openelis.gwt.widget.tree.TreeWidget;
import org.openelis.gwt.widget.tree.event.LeafOpenedEvent;
import org.openelis.gwt.widget.tree.event.LeafOpenedHandler;
import org.openelis.meta.StandardNoteMeta;
import org.openelis.modules.standardnote.client.StandardNoteService;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class EditNoteScreen extends Screen implements HasActionHandlers<EditNoteScreen.Action> {

    private NoteViewDO              note;
    private TextArea                text, preview;
    private TextBox                 subject, search;
    private AppButton               pasteButton, findButton, okButton, cancelButton;
    private TreeWidget              tree;
    private ArrayList<DictionaryDO> categoryList;

    public enum Action {
        OK, CANCEL
    };

    public EditNoteScreen() throws Exception {
        super((ScreenDefInt)GWT.create(EditNoteDef.class));

        initialize();
        setState(State.DEFAULT);
    }

    private void initialize() {
        tree = (TreeWidget)def.getWidget("noteTree");
        addScreenHandler(tree, new ScreenEventHandler<ArrayList<TreeDataItem>>() {
            public void onStateChange(StateChangeEvent<State> event) {
                tree.enable(true);
            }
        });

        tree.addLeafOpenedHandler(new LeafOpenedHandler() {
            public void onLeafOpened(LeafOpenedEvent event) {
                final TreeDataItem row = event.getItem();
                if ( !row.isLoaded()) {
                    find(row, (Integer)row.key);
                    row.checkForChildren(false);
                }
            }
        });

        tree.addSelectionHandler(new SelectionHandler<TreeDataItem>() {
            public void onSelection(SelectionEvent<TreeDataItem> event) {
                TreeDataItem item, parent;

                item = event.getSelectedItem();
                if ("note".equals(item.leafType)) {
                    preview.setValue((String)item.data, true);
                    /*
                     * set the category's name as the subject if it already hasn't
                     * been specified and if the textbox for it is showing
                     */
                    if (subject.isEnabled() && subject.isVisible() &&
                        DataBaseUtil.isEmpty(subject.getText())) {
                        parent = item.parent;
                        subject.setText((String)parent.cells.get(0).getValue());
                    }
                } else {
                    preview.setValue("", true);
                }
            }
        });

        subject = (TextBox)def.getWidget("subject");
        addScreenHandler(subject, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                subject.setValue(note.getSubject());
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                // OK button will set the note's subject
            }

            public void onStateChange(StateChangeEvent<State> event) {
                if (note != null) {
                    if ("N".equals(note.getIsExternal())) {
                        subject.enable(true);
                        subject.setVisible(true);
                    } else {
                        subject.enable(false);
                        subject.setVisible(false);
                    }
                }

            }
        });

        text = (TextArea)def.getWidget("text");
        addScreenHandler(text, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                text.setValue(note.getText());
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                // OK button will set the note's text
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
                pasteButton.enable(! "".equals(preview.getValue()));
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

            public void onStateChange(StateChangeEvent<State> event) {
                pasteButton.enable(! "".equals(preview.getValue()));
            }
        });

        findButton = (AppButton)def.getWidget("findButton");
        addScreenHandler(findButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                if ("".equals(search.getValue()))
                    buildTree();
                else
                    find();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                findButton.enable(true);
            }
        });

        okButton = (AppButton)def.getWidget("ok");
        addScreenHandler(okButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                ok();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                okButton.enable(true);
            }
        });

        cancelButton = (AppButton)def.getWidget("cancel");
        addScreenHandler(cancelButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                cancel();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                cancelButton.enable(true);
            }
        });
    }

    private void find() {
        Query query;
        QueryData field;
        QueryFieldUtil parser;
        
        query = new Query();
        parser = new QueryFieldUtil();
        parser.parse("*" + search.getValue() + "*");

        field = new QueryData();
        field.type = QueryData.Type.STRING;
        field.query = parser.getParameter().get(0);
        field.key = StandardNoteMeta.getName();
        query.setFields(field);

        field = new QueryData();
        field.type = QueryData.Type.STRING;
        field.query = parser.getParameter().get(0);
        field.key = StandardNoteMeta.getDescription();
        query.setFields(field);

        window.setBusy("querying");

        StandardNoteService.get().fetchByNameOrDescription(query, new AsyncCallback<ArrayList<StandardNoteDO>>() {
            public void onSuccess(ArrayList<StandardNoteDO> result) {
                buildTree(result);
                window.clearStatus();
            }

            public void onFailure(Throwable error) {
                buildTree(null);

                if (error instanceof NotFoundException) {
                    window.setDone(consts.get("noRecordsFound"));
                    setState(State.DEFAULT);
                } else {
                    Window.alert("Error: EditNote call query failed; " + error.getMessage());
                    window.setError(consts.get("queryFailed"));
                }
            }
        });
    }

    private void find(final TreeDataItem row, Integer typeId) {
        window.setBusy("querying");

        StandardNoteService.get().fetchByType(typeId, new AsyncCallback<ArrayList<StandardNoteDO>>() {
            public void onSuccess(ArrayList<StandardNoteDO> result) {
                buildTree(row, result);
                window.clearStatus();
            }

            public void onFailure(Throwable error) {
                buildTree(row, null);

                if (error instanceof NotFoundException) {
                    window.setDone(consts.get("noRecordsFound"));
                    setState(State.DEFAULT);
                } else {
                    Window.alert("Error: EditNote call query failed; " + error.getMessage());
                    window.setError(consts.get("queryFailed"));
                }
            }
        });
    }

    private void paste() {
        String tmpValue;
        int cursorIndex = text.getCursorPos();
        text.setFocus(true);
        tmpValue = text.getText().substring(0, cursorIndex) + preview.getText() +
                   text.getText().substring(cursorIndex);
        text.setValue(tmpValue, true);
        text.setSelectionRange(cursorIndex, preview.getText().length());
    }

    public void ok() {
        String trimText;
        
        clearErrors();
        if (validate()) {
            note.setSubject(subject.getText());
            trimText = text.getText();
            //
            // a PatternSyntaxException is thrown if trimText is empty
            //
            if (!DataBaseUtil.isEmpty(trimText)) {
                //
                // Convert bunch of repeated chars to reasonable white spaces
                //
                trimText = trimText.replaceAll("\n{3, }","\n\n"); // 3 or more to 2
                trimText = trimText.replaceAll(" {2, }"," ");     // 2 or more to 1
            }
            note.setText(trimText);
            setState(State.DEFAULT);
            ActionEvent.fire(this, Action.OK, null);
            clearErrors();
            window.close();
        }
    }

    public void cancel() {
        setState(State.DEFAULT);
        ActionEvent.fire(this, Action.CANCEL, null);
        clearErrors();
        window.close();
    }

    public boolean validate() {
        boolean valid;
        
        valid = true;
        if (note != null) {
            if ("N".equals(note.getIsExternal())) {
                if (subject.getValue().trim().length() == 0 && text.getValue().trim().length() > 0) {
                    subject.addException(new FieldErrorException("fieldRequiredException", ""));
                    window.setError(consts.get("correctErrors"));
                    valid = false;
                }
            }
        }

        return valid;
    }

    private void buildTree(ArrayList<StandardNoteDO> noteList) {
        Integer oldTypeId, currentTypeId;
        TreeDataItem catNode = null, noteNode = null;
        StandardNoteDO note;
        DictionaryDO dictDO;

        if (tree.numRows() > 0)
            tree.clear();

        if (noteList == null)
            return;

        oldTypeId = null;
        for (int i = 0; i < noteList.size(); i++ ) {
            note = noteList.get(i);
            currentTypeId = note.getTypeId();

            if ( !currentTypeId.equals(oldTypeId)) {
                oldTypeId = currentTypeId;

                try {
                    dictDO = DictionaryCache.getById(currentTypeId);
                } catch (Exception e) {
                    Window.alert(e.getMessage());
                    dictDO = null;
                }

                catNode = new TreeDataItem(1);
                catNode.leafType = "category";
                catNode.key = dictDO.getId();
                catNode.cells.get(0).value = dictDO.getEntry();
                catNode.open = true;
                tree.addRow(catNode);
            }

            noteNode = new TreeDataItem(1);
            noteNode.leafType = "note";
            noteNode.key = note.getId();
            noteNode.cells.get(0).value = note.getName() + " : " + note.getDescription();
            noteNode.data = note.getText();
            tree.addChildItem(catNode, noteNode);
        }
    }

    private void buildTree(TreeDataItem row, ArrayList<StandardNoteDO> noteList) {
        if (noteList == null)
            return;

        for (int i = 0; i < noteList.size(); i++ ) {
            StandardNoteDO noteDO = noteList.get(i);

            TreeDataItem treeModelItem = new TreeDataItem(1);
            treeModelItem.leafType = "note";
            treeModelItem.key = noteDO.getId();
            treeModelItem.cells.get(0).value = noteDO.getName() + " : " + noteDO.getDescription();
            treeModelItem.data = noteDO.getText();

            tree.addChildItem(row, treeModelItem);
        }
    }

    private void buildTree() {
        DictionaryDO data;
        TreeDataItem item;
        if (tree.numRows() > 0)
            tree.clear();

        if (categoryList == null)
            categoryList = CategoryCache.getBySystemName("standard_note_type");

        ArrayList<TreeDataItem> treeList = new ArrayList<TreeDataItem>();
        for (int i = 0; i < categoryList.size(); i++ ) {
            data = categoryList.get(i);

            item = new TreeDataItem(1);
            item.leafType = "category";
            item.checkForChildren(true);
            item.key = data.getId();
            item.cells.get(0).value = data.getEntry();

            treeList.add(item);
        }

        tree.load(treeList);
    }

    public void setNote(NoteViewDO aNote) {
        note = aNote;

        buildTree();
        DataChangeEvent.fire(this);
        StateChangeEvent.fire(this, state);
        DeferredCommand.addCommand(new Command() {
            public void execute() {
                if ("N".equals(note.getIsExternal()))
                    setFocus(subject);
                else
                    setFocus(text);
            }
        });
    }

    public HandlerRegistration addActionHandler(ActionHandler<Action> handler) {
        return addHandler(handler, ActionEvent.getType());
    }
}
