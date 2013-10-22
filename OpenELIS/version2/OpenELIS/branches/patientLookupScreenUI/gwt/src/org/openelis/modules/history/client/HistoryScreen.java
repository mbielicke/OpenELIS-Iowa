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
package org.openelis.modules.history.client;

import java.util.ArrayList;
import java.util.HashMap;

import org.openelis.cache.CategoryCache;
import org.openelis.constants.Messages;
import org.openelis.domain.DictionaryDO;
import org.openelis.domain.HistoryVO;
import org.openelis.domain.IdNameVO;
import org.openelis.gwt.event.DataChangeEvent;
import org.openelis.gwt.event.StateChangeEvent;
import org.openelis.gwt.screen.Screen;
import org.openelis.gwt.screen.ScreenDefInt;
import org.openelis.gwt.screen.ScreenEventHandler;
import org.openelis.gwt.widget.AppButton;
import org.openelis.gwt.widget.ScreenWindow;
import org.openelis.gwt.widget.table.event.BeforeCellEditedEvent;
import org.openelis.gwt.widget.table.event.BeforeCellEditedHandler;
import org.openelis.gwt.widget.tree.TreeDataItem;
import org.openelis.gwt.widget.tree.TreeWidget;
import org.openelis.gwt.widget.tree.event.BeforeLeafOpenEvent;
import org.openelis.gwt.widget.tree.event.BeforeLeafOpenHandler;
import org.openelis.modules.main.client.OpenELIS;
import org.openelis.ui.common.DataBaseUtil;
import org.openelis.ui.common.data.Query;
import org.openelis.ui.common.data.QueryData;
import org.openelis.ui.event.BeforeCloseEvent;
import org.openelis.ui.event.BeforeCloseHandler;
import org.openelis.ui.widget.WindowInt;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Window;
import com.google.gwt.xml.client.Document;
import com.google.gwt.xml.client.Node;
import com.google.gwt.xml.client.NodeList;
import com.google.gwt.xml.client.XMLParser;

public class HistoryScreen extends Screen {

    protected AppButton                closeButton;
    protected TreeWidget               historyTree;
    protected Integer                  referenceTableId;
    protected IdNameVO                 referenceVoList[];
    protected HashMap<Integer, String> operationMap;
    protected ScreenWindow             popup;
    protected AppButton                nextButton, previousButton;
    protected static HistoryScreen     instance;

    protected HistoryScreen(WindowInt window) throws Exception {
        super((ScreenDefInt)GWT.create(HistoryDef.class));

        setWindow(window);

        initialize();
        setState(State.DEFAULT);
        initializeDropdowns();
    }

    private void initialize() {
        operationMap = new HashMap<Integer, String>();

        historyTree = (TreeWidget)def.getWidget("historyTree");
        addScreenHandler(historyTree, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                historyTree.load(getTreeModel());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                historyTree.enable(true);
            }
        });

        historyTree.addBeforeCellEditedHandler(new BeforeCellEditedHandler() {
            public void onBeforeCellEdited(BeforeCellEditedEvent event) {
                TreeDataItem item;
                Integer refTable, refId;
                String fieldName;
                IdNameVO data;

                event.cancel();
                item = historyTree.getSelection();
                if (item.data != null && event.getCol() == 1) {
                    refTable = Integer.valueOf((String)item.data);
                    refId = Integer.valueOf((String)item.cells.get(1).getValue());
                    fieldName = (String)item.cells.get(0).getValue();
                    data = new IdNameVO(refId, fieldName + ": " + refId.toString());
                    showHistory(Messages.get().history(), refTable, data);
                }
            }
        });

        historyTree.addBeforeLeafOpenHandler(new BeforeLeafOpenHandler() {
            public void onBeforeLeafOpen(BeforeLeafOpenEvent event) {
                TreeDataItem item;
                ArrayList<TreeDataItem> items;

                item = event.getItem();
                if ("itemLabel".equals(item.leafType)) {
                    if (item.getItems().size() > 0)
                        return;

                    /*
                     * try to load this record's history if it was never loaded
                     * before
                     */
                    try {
                        items = getHistoryItems(historyTree.getData().indexOf(item));
                        /*
                         * if no history was found then remove the icon for
                         * opening or closing the node, so that the history
                         * isn't tried to be loaded again
                         */
                        if (item.getItems().size() == 0) {
                            item.checkForChildren(false);
                            historyTree.refresh(true);
                            window.setStatus(Messages.get().noRecordsFound(), "");
                        } else {
                            /*
                             * load the record's history
                             */
                            for (TreeDataItem i : items)
                                item.addItem(i);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Window.alert(e.toString());
                    }
                }
            }
        });
    }

    private void initializeDropdowns() {
        ArrayList<DictionaryDO> list;
        DictionaryDO d;

        list = CategoryCache.getBySystemName("history_type");
        for (int i = 0; i < list.size(); i++ ) {
            d = list.get(i);
            if (DataBaseUtil.isSame( ("history_" + (i + 1)), d.getSystemName()))
                operationMap.put(i + 1, d.getEntry());
        }
    }

    public static void showHistory(String title, Integer referenceTableId, IdNameVO... referenceId) {
        boolean newWindow;
        org.openelis.ui.widget.Window win;

        newWindow = false;
        if (instance == null) {
            newWindow = true;
            win = new org.openelis.ui.widget.Window(false);
            try {
                instance = new HistoryScreen(win);
            } catch (Exception e) {
                e.printStackTrace();
                Window.alert(e.getMessage());
                return;
            }
            win.setContent(instance);
            win.addBeforeClosedHandler(new BeforeCloseHandler<WindowInt>() {
                public void onBeforeClosed(BeforeCloseEvent<WindowInt> event) {
                    instance = null;
                }
            });
        }

        instance.window.setName(title);
        instance.setReferenceVoList(referenceId);
        instance.setReferenceTableId(referenceTableId);
        DataChangeEvent.fire(instance);

        if (newWindow)
            OpenELIS.getBrowser().addWindow(instance.window, "historyScreen");
    }

    public static void showHistory(String title, Integer referenceTableId,
                                   ArrayList<IdNameVO> referenceIds) {
        int i;
        IdNameVO arr[];

        arr = null;
        if (referenceIds != null) {
            arr = new IdNameVO[referenceIds.size()];
            for (i = 0; i < referenceIds.size(); i++ )
                arr[i] = referenceIds.get(i);
        }

        showHistory(title, referenceTableId, arr);
    }

    protected void setReferenceVoList(IdNameVO[] referenceVOList) {
        this.referenceVoList = referenceVOList;
    }

    protected void setReferenceTableId(Integer referenceTableId) {
        this.referenceTableId = referenceTableId;
    }

    private ArrayList<TreeDataItem> getTreeModel() {
        TreeDataItem item;
        ArrayList<TreeDataItem> items, model;

        if (referenceVoList == null || referenceVoList.length == 0 || referenceTableId == null)
            return null;

        model = new ArrayList<TreeDataItem>();

        try {
            if (referenceVoList.length == 1) {
                /*
                 * if only one record's history is shown then just show the
                 * history, don't show a label for the record
                 */
                items = getHistoryItems(0);
                for (TreeDataItem i : items)
                    model.add(i);
                return model;
            }

            for (IdNameVO data : referenceVoList) {
                item = new TreeDataItem(1);
                item.leafType = "itemLabel";
                item.close();
                item.key = data.getId();
                item.cells.get(0).setValue(data.getName());
                item.checkForChildren(true);
                model.add(item);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Window.alert(e.toString());
        }

        return model;
    }

    private ArrayList<TreeDataItem> getHistoryItems(int index) throws Exception {
        Integer activityId;
        String changes, operation;
        ArrayList<HistoryVO> list;
        IdNameVO refVO;
        TreeDataItem item, child;
        Document doc;
        NodeList nodes;
        Node root, node, firstChild;
        Query query;
        QueryData field;
        ArrayList<TreeDataItem> items;

        query = new Query();

        field = new QueryData();
        refVO = referenceVoList[index];
        field.setQuery(refVO.getId().toString());
        query.setFields(field);

        field = new QueryData();
        field.setQuery(referenceTableId.toString());
        query.setFields(field);

        try {
            window.setBusy(Messages.get().fetching());
            list = HistoryService.get().fetchByReferenceIdAndTable(query);
            window.setDone(Messages.get().loadCompleteMessage());
        } catch (Exception e) {
            window.clearStatus();
            throw e;
        }

        items = new ArrayList<TreeDataItem>();
        for (HistoryVO data : list) {
            /*
             * create a node for the record's history 
             */
            item = new TreeDataItem(3);
            item.leafType = "historyItem";
            item.close();
            item.key = data.getId();
            item.cells.get(0).setValue(data.getTimestamp());
            item.cells.get(1).setValue(data.getSystemUserLoginName());
            activityId = data.getActivityId();
            operation = operationMap.get(activityId);
            if (operation == null)
                operation = activityId.toString();

            item.cells.get(2).setValue(operation);

            changes = data.getChanges();
            /*
             * create nodes for showing the changes made to each field 
             */
            if (changes != null) {
                doc = XMLParser.parse(changes);
                root = doc.getDocumentElement();
                nodes = root.getChildNodes();
                for (int i = 0; i < nodes.getLength(); i++ ) {
                    node = nodes.item(i);
                    child = new TreeDataItem(2);
                    child.cells.get(0).setValue(node.getNodeName());

                    firstChild = node.getFirstChild();
                    if (firstChild != null)
                        child.cells.get(1).setValue(firstChild.getNodeValue());

                    child.leafType = "fields";
                    item.addItem(child);
                }
            }
            items.add(item);
        }
        return items;
    }
}