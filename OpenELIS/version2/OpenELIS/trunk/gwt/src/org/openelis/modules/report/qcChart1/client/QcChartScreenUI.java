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
package org.openelis.modules.report.qcChart1.client;

import static org.openelis.modules.main.client.Logger.logger;
import static org.openelis.ui.screen.Screen.Validation.Status.VALID;
import static org.openelis.ui.screen.State.DEFAULT;

import java.util.ArrayList;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.logging.Level;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Widget;

import org.openelis.cache.CategoryCache;
import org.openelis.constants.Messages;
import org.openelis.domain.DictionaryDO;
import org.openelis.domain.QcChartReportViewVO;
import org.openelis.domain.QcDO;
import org.openelis.meta.QcChartMeta;
import org.openelis.modules.qc.client.QcService;
import org.openelis.ui.common.DataBaseUtil;
import org.openelis.ui.common.Datetime;
import org.openelis.ui.common.FieldErrorException;
import org.openelis.ui.common.NotFoundException;
import org.openelis.ui.common.ValidationErrorsList;
import org.openelis.ui.common.data.Query;
import org.openelis.ui.common.data.QueryData;
import org.openelis.ui.event.GetMatchesEvent;
import org.openelis.ui.event.GetMatchesHandler;
import org.openelis.ui.event.StateChangeEvent;
import org.openelis.ui.screen.Screen;
import org.openelis.ui.screen.ScreenHandler;
import org.openelis.ui.widget.AutoComplete;
import org.openelis.ui.widget.AutoCompleteValue;
import org.openelis.ui.widget.Button;
import org.openelis.ui.widget.Dropdown;
import org.openelis.ui.widget.Item;
import org.openelis.ui.widget.QueryFieldUtil;
import org.openelis.ui.widget.TextBox;
import org.openelis.ui.widget.WindowInt;
import org.openelis.ui.widget.calendar.Calendar;
import org.openelis.ui.widget.table.event.BeforeCellEditedEvent;
import org.openelis.ui.widget.table.event.BeforeCellEditedHandler;
import org.openelis.ui.widget.table.event.CellEditedEvent;
import org.openelis.ui.widget.table.event.CellEditedHandler;
import org.openelis.ui.widget.table.event.UnselectionEvent;
import org.openelis.ui.widget.table.event.UnselectionHandler;
import org.openelis.ui.widget.tree.Node;
import org.openelis.ui.widget.tree.Tree;

/**
 * 
 * This class is used to carry the data entered on QcList screen to the back-end
 * and also between different screens
 */

public class QcChartScreenUI extends Screen {

    @UiTemplate("QcChart.ui.xml")
    interface QcChartScreenUiBinder extends UiBinder<Widget, QcChartScreenUI> {
    };

    private static QcChartScreenUiBinder         uiBinder = GWT.create(QcChartScreenUiBinder.class);

    private QcChartReportViewVO                  results;

    @UiField
    protected AutoComplete                       qcName;

    @UiField
    protected Button                             getDataButton, plotDataButton,
                                                 expandButton, collapseButton, selectButton,
                                                 unselectButton, selectAllButton,
                                                 unselectAllButton;

    @UiField
    protected Calendar                           fromDate, toDate;

    @UiField
    protected Dropdown<Integer>                  location, plotType, qcType;

    @UiField
    protected Tree                               plotDataTree;

    @UiField
    protected TextBox<Integer>                   numberToRetrieve;

    protected AsyncCallback<QcChartReportViewVO> fetchDataCall;

    private static final String                  ANALYTE_LEAF = "analyte", DATA_LEAF = "data";

    public QcChartScreenUI(WindowInt window) throws Exception {
        setWindow(window);

        try {
            CategoryCache.getBySystemNames("qc_type");
        } catch (Exception e) {
            Window.alert("QcChartScreen: missing dictionary entry; " + e.getMessage());
            window.close();
        }

        initWidget(uiBinder.createAndBindUi(this));

        initialize();
        setState(DEFAULT);
        fireDataChange();
        
        logger.fine("QC Chart Screen Opened");
    }

    /**
     * Setup state and data change handles for every widget on the screen
     */
    private void initialize() {
        ArrayList<Item<Integer>> model;
        ArrayList<DictionaryDO> dictList;
        Integer typeDynamicId;
        
        addStateChangeHandler(new StateChangeEvent.Handler() {
            public void onStateChange(StateChangeEvent event) {
                getDataButton.setEnabled(true);
            }
        });

        addScreenHandler(fromDate, QcChartMeta.getWorksheetCreatedDateFrom(), new ScreenHandler<Datetime>() {
            public void onStateChange(StateChangeEvent event) {
                fromDate.setEnabled(true);
                fromDate.setQueryMode(false);
            }

            public Widget onTab(boolean forward) {
                return forward ? toDate : unselectAllButton;
            }
        });

        addScreenHandler(toDate, QcChartMeta.getWorksheetCreatedDateTo(), new ScreenHandler<Datetime>() {
            public void onStateChange(StateChangeEvent event) {
                toDate.setEnabled(true);
                toDate.setQueryMode(false);
            }

            public Widget onTab(boolean forward) {
                return forward ? numberToRetrieve : fromDate;
            }
        });

        addScreenHandler(numberToRetrieve, QcChartMeta.getNumInstances(), new ScreenHandler<Integer>() {
            public void onStateChange(StateChangeEvent event) {
                numberToRetrieve.setEnabled(true);
                numberToRetrieve.setQueryMode(false);
            }

            public Widget onTab(boolean forward) {
                return forward ? qcName : toDate;
            }
        });

        addScreenHandler(qcName, QcChartMeta.getQCName(), new ScreenHandler<String>() {
            public void onValueChange(ValueChangeEvent<String> event) {
                disableScreenButtons();
            }

            public void onStateChange(StateChangeEvent event) {
                qcName.setEnabled(true);
            }
            
            public Widget onTab(boolean forward) {
                return forward ? location : numberToRetrieve;
            }
        });

        qcName.addGetMatchesHandler(new GetMatchesHandler() {
            public void onGetMatches(GetMatchesEvent event) {
                int i;
                ArrayList<Item<Integer>> model;
                ArrayList<QcDO> matches;
                Item<Integer> row;
                TreeMap<String, Integer> map;

                map = new TreeMap<String, Integer>();
                window.setBusy();
                try {
                    i = 0;
                    model = new ArrayList<Item<Integer>>();
                    matches = QcService.get().fetchByName(QueryFieldUtil.parseAutocomplete(event.getMatch()));
                    for (QcDO item : matches)
                        map.put(item.getName(), item.getTypeId());
                    for (Entry<String, Integer> entry : map.entrySet()) {
                        row = new Item<Integer>(2);
                        row.setKey(i++);
                        row.setCell(0, entry.getKey());
                        row.setCell(1, entry.getValue());
                        row.setData(entry);
                        model.add(row);
                    }
                    qcName.showAutoMatches(model);
                } catch (Throwable e) {
                    Window.alert(e.getMessage());
                    logger.log(Level.SEVERE, e.getMessage(), e);
                }
                clearStatus();
            }
        });

        addScreenHandler(location, QcChartMeta.getLocationId(), new ScreenHandler<Integer>() {
            public void onValueChange(ValueChangeEvent<Integer> event) {
                disableScreenButtons();
            }

            public void onStateChange(StateChangeEvent event) {
                location.setEnabled(true);
            }
            
            public Widget onTab(boolean forward) {
                return forward ? plotType : qcName;
            }
        });

        addScreenHandler(plotType, QcChartMeta.getPlotType(), new ScreenHandler<Integer>() {
            public void onValueChange(ValueChangeEvent<Integer> event) {
                disableScreenButtons();
            }

            public void onStateChange(StateChangeEvent event) {
                plotType.setEnabled(true);
            }
            
            public Widget onTab(boolean forward) {
                return forward ? plotDataTree : location;
            }
        });

        addScreenHandler(plotDataTree, "plotDataTree", new ScreenHandler<String>() {
            public void onStateChange(StateChangeEvent event) {
                plotDataTree.setEnabled(true);
                plotDataTree.setAllowMultipleSelection(true);
            }
            
            public Widget onTab(boolean forward) {
                return forward ? expandButton : plotType;
            }
        });

        plotDataTree.addBeforeCellEditedHandler(new BeforeCellEditedHandler() {
            public void onBeforeCellEdited(BeforeCellEditedEvent event) {
                Node node;

                node = plotDataTree.getNodeAt(plotDataTree.getSelectedNode());
                if (event.getCol() > 0 || ANALYTE_LEAF.equals(node.getType()))
                    event.cancel();
            }
        });

        plotDataTree.addCellEditedHandler(new CellEditedHandler() {
            public void onCellUpdated(CellEditedEvent event) {
                Object val;
                Integer data;
                Node node;
                

                val = plotDataTree.getValueAt(event.getRow(), event.getCol());
                node = plotDataTree.getNodeAt(plotDataTree.getSelectedNode());
                data = node.getData();
                if (event.getCol() == 0 && DATA_LEAF.equals(node.getType()))
                    results.getQcList().get(data).setIsPlot((String)val);
            }
        });
        
        plotDataTree.addSelectionHandler(new SelectionHandler<Integer>() {
            public void onSelection(SelectionEvent<Integer> event) {
                selectButton.setEnabled(true);
                unselectButton.setEnabled(true);
            }
        });
        
        plotDataTree.addUnselectionHandler(new UnselectionHandler<Integer>() {
            public void onUnselection(UnselectionEvent<Integer> event) {
                if (plotDataTree.getSelectedNodes().length == 0) {
                    selectButton.setEnabled(false);
                    unselectButton.setEnabled(false);
                }
            }
        });
        
        addScreenHandler(expandButton, "expandButton", new ScreenHandler<Object>() {
            public Widget onTab(boolean forward) {
                return forward ? collapseButton : plotDataTree;
            }
        });

        addScreenHandler(collapseButton, "collapseButton", new ScreenHandler<Object>() {
            public Widget onTab(boolean forward) {
                return forward ? selectButton : expandButton;
            }
        });

        addScreenHandler(selectButton, "selectButton", new ScreenHandler<Object>() {
            public Widget onTab(boolean forward) {
                return forward ? unselectButton : collapseButton;
            }
        });

        addScreenHandler(unselectButton, "unselectButton", new ScreenHandler<Object>() {
            public Widget onTab(boolean forward) {
                return forward ? selectAllButton : selectButton;
            }
        });

        addScreenHandler(selectAllButton, "selectAllButton", new ScreenHandler<Object>() {
            public Widget onTab(boolean forward) {
                return forward ? unselectAllButton : unselectButton;
            }
        });

        addScreenHandler(unselectAllButton, "unselectAllButton", new ScreenHandler<Object>() {
            public Widget onTab(boolean forward) {
                return forward ? fromDate : selectAllButton;
            }
        });

        //
        // load location dropdown model
        //
        dictList = CategoryCache.getBySystemName("laboratory_location");
        model = new ArrayList<Item<Integer>>();
        for (DictionaryDO data : dictList)
            model.add(new Item<Integer>(data.getId(), data.getEntry()));
        location.setModel(model);

        //
        // load qc type dropdown model
        //
        dictList = CategoryCache.getBySystemName("qc_type");
        model = new ArrayList<Item<Integer>>();
        for (DictionaryDO data : dictList)
            model.add(new Item<Integer>(data.getId(), data.getEntry()));
        qcType.setModel(model);

        dictList = CategoryCache.getBySystemName("qc_chart_type");
        model = new ArrayList<Item<Integer>>();
        typeDynamicId = null;
        for (DictionaryDO data : dictList) {
            model.add(new Item<Integer>(data.getId(), data.getEntry()));
            if ("chart_type_dynamic".equals(data.getSystemName()))
                typeDynamicId = data.getId();
        }
        plotType.setModel(model);
        plotType.setValue(typeDynamicId);
    }

    public boolean validateFromToFields() {
        boolean valid, fromEmpty, toEmpty, numberEmpty;
        ValidationErrorsList errors;

        valid = true;
        errors = new ValidationErrorsList();
        fromEmpty = DataBaseUtil.isEmpty(fromDate.getValue());
        toEmpty = DataBaseUtil.isEmpty(toDate.getValue());
        numberEmpty = DataBaseUtil.isEmpty(numberToRetrieve.getValue());
        if (!fromEmpty) {
            if (toEmpty) {
                errors.add(new FieldErrorException(Messages.get().fieldRequiredException(), QcChartMeta.getWorksheetCreatedDateTo()));
                valid = false;
            } else if (!numberEmpty) {
                errors.add(new FieldErrorException(Messages.get().qcChart_requiredEitherFields(), QcChartMeta.getWorksheetCreatedDateFrom()));
                errors.add(new FieldErrorException(Messages.get().qcChart_requiredEitherFields(), QcChartMeta.getWorksheetCreatedDateTo()));
                errors.add(new FieldErrorException(Messages.get().qcChart_requiredEitherFields(), QcChartMeta.getNumInstances()));
                valid = false;
            }
        } else if (!toEmpty) {
            errors.add(new FieldErrorException(Messages.get().fieldRequiredException(), QcChartMeta.getWorksheetCreatedDateFrom()));
            valid = false;
        } else if (toEmpty && numberEmpty) {
            errors.add(new FieldErrorException(Messages.get().qcChart_requiredEitherFields(), QcChartMeta.getWorksheetCreatedDateFrom()));
            errors.add(new FieldErrorException(Messages.get().qcChart_requiredEitherFields(), QcChartMeta.getWorksheetCreatedDateTo()));
            errors.add(new FieldErrorException(Messages.get().qcChart_requiredEitherFields(), QcChartMeta.getNumInstances()));
            valid = false;
        }
        
        if (errors.size() > 0)
            showErrors(errors);
        
        return valid;
    }

    //
    // add QC Type as a separate query field since it is part of the
    // QC Name field
    //
    @SuppressWarnings("unchecked")
    public ArrayList<QueryData> getQueryFields() {
        ArrayList<QueryData> list;
        AutoCompleteValue value;
        QueryData qd;

        list = super.getQueryFields();

        value = qcName.getValue();
        if (value != null && value.getData() != null) {
            for (QueryData data : list) {
                if (QcChartMeta.getQCName().equals(data.getKey())) {
                    data.setQuery(((Entry<String, Integer>)value.getData()).getKey());
                    data.setType(QueryData.Type.STRING);
                    break;
                }
            }
            qd = new QueryData();
            qd.setKey(QcChartMeta.getQCType());
            qd.setQuery(((Entry<String, Integer>)value.getData()).getValue().toString());
            qd.setType(QueryData.Type.INTEGER);
            list.add(qd);
        }

        return list;
    }

    @SuppressWarnings("unused")
    @UiHandler("getDataButton")
    protected void executeQuery(ClickEvent event) {
        Query query;
        Validation validation;

        finishEditing();
        clearErrors();
        disableScreenButtons();
        
        validation = validate();
        
        if (validation.getStatus() != VALID || !validateFromToFields()) {
            setError(Messages.get().qcChart_correctErrors());
            return;
        }

        query = new Query();
        query.setFields(getQueryFields());

        setBusy(Messages.get().querying());
        if (fetchDataCall == null) {
            fetchDataCall = new AsyncCallback<QcChartReportViewVO>() {
                public void onSuccess(QcChartReportViewVO result) {
                    setQueryResult(result);
                }
    
                public void onFailure(Throwable error) {
                    setQueryResult(null);
                    if (error instanceof NotFoundException) {
                        setDone(Messages.get().noRecordsFound());
                    } else {
                        Window.alert("Error: QcChartReport call fetchForQcChart failed; " +
                                     error.getMessage());
                        setError(Messages.get().queryFailed());
                    }
                }
            };
        }
            
        QcChartReportService1.get().fetchData(query, fetchDataCall);
    }

    public void setQueryResult(QcChartReportViewVO results) {
        Integer index, lastAnalyteId;
        Node root, anode, dnode;

        this.results = results;
        root = new Node();
        if (results == null || results.getQcList() == null || results.getQcList().size() == 0) {
            selectAllButton.setEnabled(false);
            unselectAllButton.setEnabled(false);
            setDone(Messages.get().noRecordsFound());
        } else {
            anode = new Node();
            index = 0;
            lastAnalyteId = -1;
            for (QcChartReportViewVO.Value data : results.getQcList()) {
                if (!lastAnalyteId.equals(data.getAnalyteId())) {
                    anode = new Node(2);
                    anode.setType(ANALYTE_LEAF);
                    anode.setCell(1, data.getAnalyteName());
                    anode.setOpen(true);
                    root.add(anode);
                    lastAnalyteId = data.getAnalyteId();
                }
                
                dnode = new Node(7);
                dnode.setType(DATA_LEAF);
                dnode.setCell(0, data.getIsPlot());
                dnode.setCell(1, data.getAccessionNumber());
                dnode.setCell(2, data.getLotNumber());
                dnode.setCell(3, data.getWorksheetCreatedDate());
                dnode.setCell(4, data.getValue1());
                dnode.setCell(5, data.getValue2());
                dnode.setCell(6, data.getPlotValue());
                dnode.setData(index++);
                anode.add(dnode);
            }
            plotDataButton.setEnabled(true);
            expandButton.setEnabled(true);
            collapseButton.setEnabled(true);
            selectAllButton.setEnabled(true);
            unselectAllButton.setEnabled(true);
            setDone(Messages.get().gen_queryingComplete());
        }
        plotDataTree.setRoot(root);
    }

    @SuppressWarnings({"unused", "unchecked"})
    @UiHandler("plotDataButton")
    protected void plotGraph(ClickEvent event) {
        boolean hasPlottable;
        QcChartReportScreen1 plot;

        hasPlottable = false;
        for (QcChartReportViewVO.Value data : results.getQcList()) {
            if ("Y".equals(data.getIsPlot())) {
                hasPlottable = true;
                break;
            }
        }
        if (!hasPlottable) {
            setError(Messages.get().noSampleSelectedError());
            return;
        }
        try {
            plot = new QcChartReportScreen1(window);
            results.setQcName(((Entry<String, Integer>)qcName.getValue().getData()).getKey());
            plot.runReport(results);
        } catch (Exception e) {
            Window.alert(e.getMessage());
            logger.log(Level.SEVERE, e.getMessage(), e);
        }
    }

    /**
     * Expands the tree at every level
     */
    @SuppressWarnings("unused")
    @UiHandler("expandButton")
    protected void expand(ClickEvent event) {
        plotDataTree.expand(1);
    }

    /**
     * Collpases the tree at every level
     */
    @SuppressWarnings("unused")
    @UiHandler("collapseButton")
    protected void collapse(ClickEvent event) {
        plotDataTree.collapse();
    }

    @SuppressWarnings("unused")
    @UiHandler("selectButton")
    protected void select(ClickEvent event) {
        Node node;
        
        for (Integer index : plotDataTree.getSelectedNodes()) {
            node = plotDataTree.getNodeAt(index);
            if (DATA_LEAF.equals(node.getType())) {
                node.setCell(0, "Y");
                results.getQcList().get((Integer)node.getData()).setIsPlot("Y");
                plotDataTree.refreshNode(node);
            }
        }
    }

    @SuppressWarnings("unused")
    @UiHandler("unselectButton")
    protected void unselect(ClickEvent event) {
        Node node;
        
        for (Integer index : plotDataTree.getSelectedNodes()) {
            node = plotDataTree.getNodeAt(index);
            if (DATA_LEAF.equals(node.getType())) {
                node.setCell(0, "N");
                results.getQcList().get((Integer)node.getData()).setIsPlot("N");
                plotDataTree.refreshNode(node);
            }
        }
    }

    @SuppressWarnings("unused")
    @UiHandler("selectAllButton")
    protected void selectAll(ClickEvent event) {
        int i;
        Node node;
        
        for (i = 0; i < plotDataTree.getRowCount(); i++) {
            node = plotDataTree.getNodeAt(i);
            if (DATA_LEAF.equals(node.getType())) {
                node.setCell(0, "Y");
                results.getQcList().get((Integer)node.getData()).setIsPlot("Y");
                plotDataTree.refreshNode(node);
            }
        }
    }

    @SuppressWarnings("unused")
    @UiHandler("unselectAllButton")
    protected void unselectAll(ClickEvent event) {
        int i;
        Node node;
        
        for (i = 0; i < plotDataTree.getRowCount(); i++) {
            node = plotDataTree.getNodeAt(i);
            if (DATA_LEAF.equals(node.getType())) {
                node.setCell(0, "N");
                results.getQcList().get((Integer)node.getData()).setIsPlot("N");
                plotDataTree.refreshNode(node);
            }
        }
    }

    private void disableScreenButtons() {
        plotDataButton.setEnabled(false);
        expandButton.setEnabled(false);
        collapseButton.setEnabled(false);
        results = null;
        plotDataTree.setRoot(new Node());
    }
}