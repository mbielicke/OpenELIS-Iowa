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

import org.openelis.cache.DictionaryCache;
import org.openelis.domain.DictionaryDO;
import org.openelis.domain.HistoryVO;
import org.openelis.domain.IdNameVO;
import org.openelis.gwt.common.data.Query;
import org.openelis.gwt.common.data.QueryData;
import org.openelis.gwt.event.DataChangeEvent;
import org.openelis.gwt.event.StateChangeEvent;
import org.openelis.gwt.screen.Screen;
import org.openelis.gwt.screen.ScreenDefInt;
import org.openelis.gwt.screen.ScreenEventHandler;
import org.openelis.gwt.services.ScreenService;
import org.openelis.gwt.widget.AppButton;
import org.openelis.gwt.widget.ScreenWindow;
import org.openelis.gwt.widget.table.event.BeforeCellEditedEvent;
import org.openelis.gwt.widget.table.event.BeforeCellEditedHandler;
import org.openelis.gwt.widget.tree.TreeDataItem;
import org.openelis.gwt.widget.tree.TreeWidget;
import org.openelis.gwt.widget.tree.event.BeforeLeafOpenEvent;
import org.openelis.gwt.widget.tree.event.BeforeLeafOpenHandler;
import org.openelis.modules.main.client.openelis.OpenELIS;
import org.openelis.utilcommon.DataBaseUtil;

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
    
    public static void showHistory(String title, Integer referenceTableId, IdNameVO... referenceId) {        
        if (instance == null) {
            try {
                instance = new HistoryScreen();
            } catch (Exception e) {
                e.printStackTrace();
                Window.alert(e.getMessage());
                return;
            }
        }
        instance.initializeWindow(title);
        instance.setReferenceVoList(referenceId);
        instance.setReferenceTableId(referenceTableId);
        DataChangeEvent.fire(instance); 
    }
    
    protected HistoryScreen() throws Exception {
        super((ScreenDefInt)GWT.create(HistoryDef.class));
        service = new ScreenService("controller?service=org.openelis.modules.history.server.HistoryService");
        
        // Setup link between Screen and widget Handlers
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
                    data = new IdNameVO(refId, fieldName + ": "+ refId.toString());                    
                    showHistory(consts.get("history"), refTable, data);
                }                                                  
            }            
        });
        
        historyTree.addBeforeLeafOpenHandler(new BeforeLeafOpenHandler() {
            public void onBeforeLeafOpen(BeforeLeafOpenEvent event) {
                TreeDataItem item;
                int index;
                
                item = event.getItem();                
                if("itemLabel".equals(item.leafType)) {
                    index = historyTree.getData().indexOf(item);
                    addHistoryItems(index, item);
                }                
            }            
        });                
    }
    
    private void initializeDropdowns() {
        ArrayList<DictionaryDO> list;
        DictionaryDO d;

        list = DictionaryCache.getListByCategorySystemName("history_type");
        for (int i = 0; i < list.size(); i++ ) {
            d = list.get(i);
            if (DataBaseUtil.isSame( ("history_" + (i+1)), d.getSystemName()))
                operationMap.put(i+1, d.getEntry());
        }
    }

    private void initializeWindow(String title) {
    	OpenELIS.browser.addScreen(this);
    }
    
    protected void setReferenceVoList(IdNameVO[] referenceVOList) {
        this.referenceVoList = referenceVOList;
    }

    protected void setReferenceTableId(Integer referenceTableId) {
        this.referenceTableId = referenceTableId;
    }

    private ArrayList<TreeDataItem> getTreeModel() {
        ArrayList<TreeDataItem> model;
        TreeDataItem item;

        if (referenceVoList == null || referenceTableId == null || referenceVoList.length == 0)
            return null;
        
        model = new ArrayList<TreeDataItem>();

        try {
            for (IdNameVO data : referenceVoList) {
                item = new TreeDataItem(1);
                item.leafType = "itemLabel";
                item.close();
                item.key = data.getId();
                item.cells.get(0).setValue(data.getName());
                item.checkForChildren(true);
                
                model.add(item);
            }
            
            if(referenceVoList.length == 1) {
                item = model.get(0);
                item.toggle();
                addHistoryItems(0, item);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Window.alert(e.toString());
        }

        return model;
    }
    
    private void addHistoryItems(int index, TreeDataItem parent) {
        Integer activityId;
        ArrayList<HistoryVO> list;
        IdNameVO refVO;
        TreeDataItem item, child;
        Document doc;
        String changes, operation;
        NodeList nodes;
        Node root, node, refTable;
        Query query;
        QueryData field; 

        if(parent.getItems().size() > 0)
            return;       
        
        query = new Query();

        field = new QueryData();
        refVO = referenceVoList[index];
        field.query = refVO.getId().toString();
        query.setFields(field);

        field = new QueryData();
        field.query = referenceTableId.toString();
        query.setFields(field);

        window.setBusy(consts.get("fetching"));
        try {
            list = service.callList("fetchByReferenceIdAndTable", query);
            for (HistoryVO data : list) {
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
                if (changes != null) {
                    doc = XMLParser.parse(changes);
                    root = doc.getDocumentElement();
                    nodes = root.getChildNodes();
                    for (int i = 0; i < nodes.getLength(); i++ ) {
                        node = nodes.item(i);
                        refTable = node.getAttributes().getNamedItem("refTable");                        
                        child = new TreeDataItem(2);                        
                        child.cells.get(0).setValue(node.getNodeName());
                        child.cells.get(1).setValue(node.getFirstChild().getNodeValue());
                        if (refTable != null) { 
                            child.data = refTable.getNodeValue();          
                            child.leafType = "linkfields";
                        } else {
                            child.leafType = "fields";
                        }
                        item.addItem(child);
                    }

                }
                parent.addItem(item);                     
            }
        } catch (Exception e) {
            e.printStackTrace();
            Window.alert(e.toString());
            window.clearStatus();
        }
        
        window.setDone(consts.get("loadCompleteMessage"));
        if(parent.getItems().size() == 0) {
            parent.checkForChildren(false);
            historyTree.refresh(true);
            window.setStatus(consts.get("noRecordsFound"), "");
        }
    }
}
