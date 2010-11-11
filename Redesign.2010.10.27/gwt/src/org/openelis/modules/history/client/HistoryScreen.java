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
import org.openelis.gwt.common.DataBaseUtil;
import org.openelis.gwt.common.data.Query;
import org.openelis.gwt.common.data.QueryData;
import org.openelis.gwt.event.DataChangeEvent;
import org.openelis.gwt.event.StateChangeEvent;
import org.openelis.gwt.screen.Screen;
import org.openelis.gwt.screen.ScreenDefInt;
import org.openelis.gwt.screen.ScreenEventHandler;
import org.openelis.gwt.services.ScreenService;
import org.openelis.gwt.widget.Button;
import org.openelis.gwt.widget.table.event.BeforeCellEditedEvent;
import org.openelis.gwt.widget.table.event.BeforeCellEditedHandler;
import org.openelis.gwt.widget.tree.Node;
import org.openelis.gwt.widget.tree.Tree;
import org.openelis.gwt.widget.tree.event.BeforeNodeOpenEvent;
import org.openelis.gwt.widget.tree.event.BeforeNodeOpenHandler;
import org.openelis.modules.main.client.openelis.OpenELIS;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Window;
import com.google.gwt.xml.client.Document;
import com.google.gwt.xml.client.NodeList;
import com.google.gwt.xml.client.XMLParser;

public class HistoryScreen extends Screen {

    protected Button                   closeButton;
    protected Tree                     historyTree;

    protected Integer                  referenceTableId;
    protected IdNameVO                 referenceVoList[];
    protected HashMap<Integer, String> operationMap;    
    protected org.openelis.gwt.widget.Window      popup;
    protected Button                   nextButton, previousButton;
    protected static HistoryScreen     instance;     
    
    protected HistoryScreen() throws Exception {
        super((ScreenDefInt)GWT.create(HistoryDef.class));
        service = new ScreenService("controller?service=org.openelis.modules.history.server.HistoryService");
        
        initialize();
        setState(State.DEFAULT);
        initializeDropdowns();
    }
    
    private void initialize() {
        operationMap = new HashMap<Integer, String>();

        historyTree = (Tree)def.getWidget("historyTree");
        addScreenHandler(historyTree, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                historyTree.setRoot(getRoot());               
            }

            public void onStateChange(StateChangeEvent<State> event) {
                historyTree.setEnabled(true);
            }
        });
        
        historyTree.addBeforeCellEditedHandler(new BeforeCellEditedHandler() {
            public void onBeforeCellEdited(BeforeCellEditedEvent event) {
                Node node;
                Integer refTable, refId;
                String fieldName;
                IdNameVO data;
                
                event.cancel(); 
                node = historyTree.getNodeAt(historyTree.getSelectedNode());
                if (node.getData() != null && event.getCol() == 1) {
                    refTable = Integer.valueOf((String)node.getData());
                    refId = Integer.valueOf((String)node.getCell(1)); 
                    fieldName = (String)node.getCell(0);
                    data = new IdNameVO(refId, fieldName + ": "+ refId.toString());                    
                    showHistory(consts.get("history"), refTable, data);
                }                                                  
            }            
        });
        
        historyTree.addBeforeNodeOpenHandler(new BeforeNodeOpenHandler() {
            public void onBeforeNodeOpen(BeforeNodeOpenEvent event) {
                Node node;
                int index;
                
                node = event.getNode();                
                if("itemLabel".equals(node.getType())) {
                    index = event.getRow();
                    addHistoryItems(index, node);
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
    
    protected void initializeWindow(String title) {
        OpenELIS.getBrowser().addScreen(this);
    }
    
    protected void setReferenceVoList(IdNameVO[] referenceVOList) {
        this.referenceVoList = referenceVOList;
    }

    protected void setReferenceTableId(Integer referenceTableId) {
        this.referenceTableId = referenceTableId;
    }

    private Node getRoot() {
        Node root, node;

        if (referenceVoList == null || referenceTableId == null || referenceVoList.length == 0)
            return null;
        
        root = new Node();

        try {
            for (IdNameVO data : referenceVoList) {
                node = new Node(3);
                node.setType("itemLabel");
                node.setOpen(false);
                node.setKey(data.getId());
                node.setCell(0,data.getName());
                node.setDeferLoadingUntilExpand(true);
                
                root.add(node);
            }
            
            if(referenceVoList.length == 1) {
                node = root.getChildAt(0);
                //node.toggle();
                addHistoryItems(0, node);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Window.alert(e.toString());
        }

        return root;
    }
    
    private void addHistoryItems(int index, Node parent) {
        Integer activityId;
        ArrayList<HistoryVO> list;
        IdNameVO refVO;
        Node item, child;
        Document doc;
        String changes, operation;
        NodeList nodes;
        com.google.gwt.xml.client.Node root, node, refTable;
        Query query;
        QueryData field; 

        if(parent.getChildCount() > 0)
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
                item = new Node(3);
                item.setType("historyItem");
                item.setOpen(false);
                item.setKey(data.getId());
                item.setCell(0,data.getTimestamp());
                item.setCell(1,data.getSystemUserLoginName());
                activityId = data.getActivityId();
                operation = operationMap.get(activityId);
                if (operation == null)
                    operation = activityId.toString();

                item.setCell(2,operation);

                changes = data.getChanges();
                if (changes != null) {
                    doc = XMLParser.parse(changes);
                    root = doc.getDocumentElement();
                    nodes = root.getChildNodes();
                    for (int i = 0; i < nodes.getLength(); i++ ) {
                        node = nodes.item(i);
                        refTable = node.getAttributes().getNamedItem("refTable");                        
                        child = new Node(2);                        
                        child.setCell(0,node.getNodeName());
                        child.setCell(1,node.getFirstChild().getNodeValue());
                        if (refTable != null) { 
                            child.setData(refTable.getNodeValue());          
                            child.setType("linkfields");
                        } else {
                            child.setType("fields");
                        }
                        item.add(child);
                    }

                }
                parent.add(item);                     
            }
        } catch (Exception e) {
            e.printStackTrace();
            Window.alert(e.toString());
            window.clearStatus();
        }
        
        window.setDone(consts.get("loadCompleteMessage"));
        if(parent.getChildCount() == 0) {
            parent.setDeferLoadingUntilExpand(false);
            //historyTree.refresh(true);
            window.setStatus(consts.get("noRecordsFound"), "");
        }
    }
}
