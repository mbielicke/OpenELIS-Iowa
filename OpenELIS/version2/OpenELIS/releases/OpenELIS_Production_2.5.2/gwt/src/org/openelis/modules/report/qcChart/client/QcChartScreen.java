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
package org.openelis.modules.report.qcChart.client;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.openelis.cache.CategoryCache;
import org.openelis.constants.Messages;
import org.openelis.domain.DictionaryDO;
import org.openelis.domain.QcChartReportViewVO;
import org.openelis.domain.QcDO;
import org.openelis.ui.common.DataBaseUtil;
import org.openelis.ui.common.Datetime;
import org.openelis.ui.common.NotFoundException;
import org.openelis.ui.common.data.Query;
import org.openelis.ui.common.data.QueryData;
import org.openelis.gwt.event.DataChangeEvent;
import org.openelis.gwt.event.GetMatchesEvent;
import org.openelis.gwt.event.GetMatchesHandler;
import org.openelis.gwt.event.StateChangeEvent;
import org.openelis.gwt.screen.Screen;
import org.openelis.gwt.screen.ScreenDefInt;
import org.openelis.gwt.screen.ScreenEventHandler;
import org.openelis.gwt.widget.AppButton;
import org.openelis.gwt.widget.AutoComplete;
import org.openelis.gwt.widget.CalendarLookUp;
import org.openelis.gwt.widget.Dropdown;
import org.openelis.gwt.widget.HasField;
import org.openelis.gwt.widget.QueryFieldUtil;
import org.openelis.gwt.widget.TextBox;
import org.openelis.gwt.widget.table.TableDataRow;
import org.openelis.gwt.widget.table.TableWidget;
import org.openelis.gwt.widget.table.event.BeforeCellEditedEvent;
import org.openelis.gwt.widget.table.event.BeforeCellEditedHandler;
import org.openelis.gwt.widget.table.event.CellEditedEvent;
import org.openelis.gwt.widget.table.event.CellEditedHandler;
import org.openelis.meta.QcChartMeta;
import org.openelis.modules.qc.client.QcService;
import org.openelis.ui.widget.WindowInt;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * 
 * This class is used to carry the data entered on QcList screen to the back-end
 * and also between different screens
 */

public class QcChartScreen extends Screen {

    private AutoComplete<String> qcName;
    private CalendarLookUp       workSheetCreatedDateFrom, workSheetCreatedDateTo;
    private TextBox<Integer>     numInstances;
    private Dropdown<Integer>    plotType, qcLocation;
    protected AppButton          getDataButton, recomputeButton, plotDataButton,
                                 selectButton, selectAllButton, unselectAllButton,
                                 unselectButton;
    private TableWidget          plotDataTable;
    private QcChartReportViewVO  results;
    private QcChartReportScreen  qcChartReportScreen;
    private Integer              typeDynamicId, typeQcSpike, typeQcBlank, typeQcDuplicate;

    public QcChartScreen(WindowInt window) throws Exception {
        super((ScreenDefInt)GWT.create(QcChartDef.class));
        
        setWindow(window);

        try {
            CategoryCache.getBySystemNames("qc_type");
        } catch (Exception e) {
            Window.alert("QcListScreen: missing dictionary entry; " + e.getMessage());
            window.close();
        }

        initialize();
        setState(State.DEFAULT);
        initializeDropdowns();

        DataChangeEvent.fire(this);
    }

    /**
     * Setup state and data change handles for every widget on the screen
     */
    @SuppressWarnings("unchecked")
    private void initialize() {
        qcName = (AutoComplete)def.getWidget(QcChartMeta.getQCName());
        addScreenHandler(qcName, new ScreenEventHandler<String>() {
            public void onValueChange(ValueChangeEvent<String> event) {
                disableScreenButtons();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                qcName.enable(EnumSet.of(State.DEFAULT).contains(event.getState()));
            }

        });
        qcName.addGetMatchesHandler(new GetMatchesHandler() {
            public void onGetMatches(GetMatchesEvent event) {
                TableDataRow row;
                ArrayList<QcDO> list;
                ArrayList<TableDataRow> model;
                TreeMap<String, Integer> map;

                map = new TreeMap<String, Integer>();
                window.setBusy();
                try {
                    list = QcService.get().fetchByName(QueryFieldUtil.parseAutocomplete(event.getMatch()));
                    for (QcDO item : list)
                        map.put(item.getName(), item.getTypeId());
                    model = new ArrayList<TableDataRow>();
                    for (Entry<String, Integer> entry : map.entrySet()) {
                        row = new TableDataRow(2);
                        row.key = entry.getKey();
                        row.data = entry.getValue();
                        row.cells.get(0).value = entry.getKey();
                        row.cells.get(1).value = entry.getValue();
                        model.add(row);
                    }
                    qcName.showAutoMatches(model);
                } catch (Throwable e) {
                    e.printStackTrace();
                    Window.alert(e.getMessage());
                }
                window.clearStatus();
            }

        });

        workSheetCreatedDateFrom = (CalendarLookUp)def.getWidget(QcChartMeta.getWorksheetCreatedDateFrom());
        addScreenHandler(workSheetCreatedDateFrom, new ScreenEventHandler<Datetime>() {
            public void onStateChange(StateChangeEvent<State> event) {
                workSheetCreatedDateFrom.enable(EnumSet.of(State.DEFAULT)
                                                       .contains(event.getState()));
            }
        });

        workSheetCreatedDateTo = (CalendarLookUp)def.getWidget(QcChartMeta.getWorksheetCreatedDateTo());
        addScreenHandler(workSheetCreatedDateTo, new ScreenEventHandler<Datetime>() {
            public void onStateChange(StateChangeEvent<State> event) {
                workSheetCreatedDateTo.enable(EnumSet.of(State.DEFAULT).contains(event.getState()));
            }
        });

        numInstances = (TextBox<Integer>)def.getWidget(QcChartMeta.getNumInstances());
        addScreenHandler(numInstances, new ScreenEventHandler<Integer>() {
            public void onStateChange(StateChangeEvent<State> event) {
                numInstances.enable(EnumSet.of(State.DEFAULT).contains(event.getState()));
            }

        });

        plotType = (Dropdown<Integer>)def.getWidget(QcChartMeta.getPlotType());
        addScreenHandler(plotType, new ScreenEventHandler<Integer>() {
            public void onValueChange(ValueChangeEvent<Integer> event) {
                disableScreenButtons();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                plotType.enable(EnumSet.of(State.DEFAULT).contains(event.getState()));
            }
        });

        qcLocation = (Dropdown<Integer>)def.getWidget(QcChartMeta.getLocationId());
        addScreenHandler(qcLocation, new ScreenEventHandler<Integer>() {
            public void onValueChange(ValueChangeEvent<Integer> event) {
                disableScreenButtons();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                qcLocation.enable(EnumSet.of(State.DEFAULT).contains(event.getState()));
            }
        });

        getDataButton = (AppButton)def.getWidget("getData");
        addScreenHandler(getDataButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                getPlotData();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                getDataButton.enable(EnumSet.of(State.DEFAULT).contains(event.getState()));
            }
        });

        recomputeButton = (AppButton)def.getWidget("reCompute");
        addScreenHandler(recomputeButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                reCompute();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                recomputeButton.enable(false);
            }
        });

        plotDataButton = (AppButton)def.getWidget("plotdata");
        addScreenHandler(plotDataButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                plotGraph();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                plotDataButton.enable(false);
            }
        });

        plotDataTable = (TableWidget)def.getWidget("plotDataTable");
        addScreenHandler(plotDataTable, new ScreenEventHandler<ArrayList<TableDataRow>>() {
            public void onDataChange(DataChangeEvent event) {
                plotDataTable.load(getTableModel());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                plotDataTable.enable(true);
            }
        });

        plotDataTable.addBeforeCellEditedHandler(new BeforeCellEditedHandler() {
            public void onBeforeCellEdited(BeforeCellEditedEvent event) {
                TableDataRow row;
                QcChartReportViewVO.Value data;

                row = plotDataTable.getRow(event.getRow());
                data = (QcChartReportViewVO.Value)row.data;
                // if the row has corrupt values just display the value to the
                // user. Also except the checkbox all other columns shoudl be
                // non editable.
                if ( (event.getCol() > 0) || (data.getPlotValue() == null))
                    event.cancel();
            }
        });

        plotDataTable.addCellEditedHandler(new CellEditedHandler() {
            public void onCellUpdated(CellEditedEvent event) {
                int r, c;
                Object val;
                TableDataRow row;
                QcChartReportViewVO.Value data;

                r = event.getRow();
                c = event.getCol();
                val = plotDataTable.getObject(r, c);

                row = plotDataTable.getRow(event.getRow());
                data = (QcChartReportViewVO.Value)row.data;
                data.setIsPlot((String)val);
                results.getQcList().get(r).setIsPlot((String)val);
            }
        });

        selectButton = (AppButton)def.getWidget("selectButton");
        addScreenHandler(selectButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                int list[];

                list = plotDataTable.getSelectedRows();
                for (int i = 0; i < list.length; i++) {
                    plotDataTable.setCell(list[i], 0, "Y");
                    results.getQcList().get(list[i]).setIsPlot("Y");
                }
            }

            public void onStateChange(StateChangeEvent<State> event) {
                selectButton.enable(EnumSet.of(State.DEFAULT).contains(event.getState()));
            }
        });

        selectAllButton = (AppButton)def.getWidget("selectAllButton");
        addScreenHandler(selectAllButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                for (int i = 0; i < plotDataTable.numRows(); i++) {
                    plotDataTable.setCell(i, 0, "Y");
                    results.getQcList().get(i).setIsPlot("Y");
                }
            }

            public void onStateChange(StateChangeEvent<State> event) {
                selectAllButton.enable(EnumSet.of(State.DEFAULT).contains(event.getState()));
            }
        });

        unselectAllButton = (AppButton)def.getWidget("unselectAllButton");
        addScreenHandler(unselectAllButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                for (int i = 0; i < plotDataTable.numRows(); i++) {
                    plotDataTable.setCell(i, 0, "N");
                    results.getQcList().get(i).setIsPlot("N");
                }
            }

            public void onStateChange(StateChangeEvent<State> event) {
                unselectAllButton.enable(EnumSet.of(State.DEFAULT).contains(event.getState()));
            }
        });

        unselectButton = (AppButton)def.getWidget("unselectButton");
        addScreenHandler(unselectButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                int list[];

                list = plotDataTable.getSelectedRows();
                for (int i = 0; i < list.length; i++) {
                    plotDataTable.setCell(list[i], 0, "N");
                    results.getQcList().get(list[i]).setIsPlot("N");
                }
            }

            public void onStateChange(StateChangeEvent<State> event) {
                unselectButton.enable(EnumSet.of(State.DEFAULT).contains(event.getState()));
            }
        });
    }

    protected void initializeDropdowns() {
        ArrayList<TableDataRow> model;
        ArrayList<DictionaryDO> list;
        TableDataRow row;

        model = new ArrayList<TableDataRow>();
        list = CategoryCache.getBySystemName("laboratory_location");
        for (DictionaryDO data : list) {
            row = new TableDataRow(data.getId(), data.getEntry());
            row.enabled = ("Y".equals(data.getIsActive()));
            model.add(row);
        }
        qcLocation.setModel(model);

        model = new ArrayList<TableDataRow>();
        list = CategoryCache.getBySystemName("qc_type");
        for (DictionaryDO data : list) {
            row = new TableDataRow(data.getId(), data.getEntry());
            row.enabled = ("Y".equals(data.getIsActive()));
            model.add(row);
        }
        ((Dropdown)qcName.getColumnWidget(QcChartMeta.getQCType())).setModel(model);

        model = new ArrayList<TableDataRow>();
        list = CategoryCache.getBySystemName("qc_chart_type");
        typeDynamicId = null;
        for (DictionaryDO data : list) {
            row = new TableDataRow(data.getId(), data.getEntry());
            row.enabled = ("Y".equals(data.getIsActive()));
            model.add(row);
            if ("chart_type_dynamic".equals(data.getSystemName()))
                typeDynamicId = data.getId();
        }
        plotType.setModel(model);
        plotType.setSelection(typeDynamicId);
    }

    public void setResults(QcChartReportViewVO results) {
        this.results = results;
        DataChangeEvent.fire(this);
    }

    public boolean validate() {
        return super.validate() && validateFromToFields();
    }

    public boolean validateFromToFields() {
        boolean valid, fromEmpty, toEmpty, numberEmpty;
        Exception le;
        valid = true;

        fromEmpty = workSheetCreatedDateFrom.getValue() == null;
        toEmpty = workSheetCreatedDateTo.getValue() == null;
        numberEmpty = DataBaseUtil.isEmpty(numInstances.getValue());
        if (!fromEmpty) {
            if (toEmpty) {
                workSheetCreatedDateTo.addException(new Exception(Messages.get().fieldRequiredException()));
                valid = false;
            } else if (!numberEmpty) {
                le = new Exception(Messages.get().requiredEitherFields());
                workSheetCreatedDateTo.addException(le);
                workSheetCreatedDateFrom.addException(le);
                numInstances.addException(le);
                valid = false;
            }
        } else if (!toEmpty) {
            workSheetCreatedDateFrom.addException(new Exception(Messages.get().fieldRequiredException()));
            valid = false;
        } else if (toEmpty && numberEmpty) {
            le = new Exception(Messages.get().requiredEitherFields());
            workSheetCreatedDateTo.addException(le);
            workSheetCreatedDateFrom.addException(le);
            numInstances.addException(le);
            valid = false;
        }
        return valid;
    }

    //
    // add QC Type as a separate query field since it is part of the
    // QC Name field
    //
    @SuppressWarnings("unchecked")
    public ArrayList<QueryData> getQueryFields() {
        Object fieldValue;
        ArrayList<QueryData> list;
        ArrayList<TableDataRow> selections;
        QueryData qd;
        QueryFieldUtil qField;
        TableDataRow row;

        list = new ArrayList<QueryData>();
        for (String key : def.getWidgets().keySet()) {
            if (def.getWidget(key) instanceof AutoComplete) {
                row = ((AutoComplete)def.getWidget(key)).getSelection();
                if (row != null && row.key != null) {
                    qd = new QueryData();
                    qd.setKey(key);
                    qd.setQuery((String)row.key);
                    qd.setType(QueryData.Type.STRING);
                    list.add(qd);

                    qField = new QueryFieldUtil();
                    qField.parse(qd.getQuery());
                }
            } else if (def.getWidget(key) instanceof CalendarLookUp) {
                if (((CalendarLookUp)def.getWidget(key)).getStringValue() != null && ((CalendarLookUp)def.getWidget(key)).getStringValue().length() > 0) {
                    qd = new QueryData();
                    qd.setQuery(((CalendarLookUp)def.getWidget(key)).getStringValue());
                    qd.setKey(key);
                    qd.setType(QueryData.Type.DATE);
                    list.add(qd);
                }
            } else if (def.getWidget(key) instanceof Dropdown) {
                selections = ((Dropdown)def.getWidget(key)).getSelections();
                if (selections.size() > 0) {
                    row = selections.get(0);
                    
                    qd = new QueryData();
                    qd.setKey(key);
                    qd.setType(QueryData.Type.INTEGER);
                    qd.setQuery(row.key.toString());
                    list.add(qd);
                }
            } else if (def.getWidget(key) instanceof TextBox) {
                fieldValue = ((TextBox)def.getWidget(key)).getFieldValue();
                if (fieldValue != null) {
                    qd = new QueryData();
                    qd.setQuery(fieldValue.toString());
                    qd.setKey(key);
                    qd.setType(QueryData.Type.INTEGER);
                    list.add(qd);
                }
            } else if (def.getWidget(key) instanceof HasField) {
                ((HasField)def.getWidget(key)).getQuery(list, key);
            }
        }

        row = qcName.getSelection();
        if (row != null && row.key != null) {
            qd = new QueryData();
            qd.setKey(QcChartMeta.getQCType());
            qd.setQuery(((Integer)row.data).toString());
            qd.setType(QueryData.Type.INTEGER);
            list.add(qd);

            qField = new QueryFieldUtil();
            qField.parse(qd.getQuery());
        }

        return list;
    }

    protected void getPlotData() {
        Query query;

        disableScreenButtons();
        clearErrors();
        if (!validate()) {
            window.setError(Messages.get().correctErrors());
            return;
        }

        query = new Query();
        query.setFields(getQueryFields());

        window.setBusy();
        QcChartReportService.get().fetchForQcChart(query, new AsyncCallback<QcChartReportViewVO>() {
            public void onSuccess(QcChartReportViewVO result) {
                setResults(result);
                recomputeButton.enable(true);
                plotDataButton.enable(true);
                if (result.getQcList().size() > 0)
                    window.setDone(Messages.get().loadCompleteMessage());
                else
                    window.setDone(Messages.get().noRecordsFound());
            }

            public void onFailure(Throwable error) {
                setResults(null);
                if (error instanceof NotFoundException) {
                    window.setDone(Messages.get().noRecordsFound());
                    setState(State.DEFAULT);
                } else {
                    Window.alert("Error: Method call query failed; " +
                                 error.getMessage());
                    window.setError(Messages.get().queryFailed());
                }
            }
        });
        window.clearStatus();
    }

    protected void reCompute() {
        window.setBusy();

        QcChartReportService.get().recompute(results, new AsyncCallback<QcChartReportViewVO>() {
            public void onSuccess(QcChartReportViewVO result) {
                setResults(result);
                window.setDone(Messages.get().loadCompleteMessage());
            }

            public void onFailure(Throwable error) {
                setResults(null);
                if (error instanceof NotFoundException) {
                    window.setDone(Messages.get().noRecordsFound());
                    setState(State.DEFAULT);
                } else {
                    Window.alert("Error: Method call query failed; " + error.getMessage());
                    window.setError(Messages.get().queryFailed());
                }
            }
        });

        window.clearStatus();
    }

    protected void plotGraph() {
        int cnt;

        cnt = 0;
        for (int i = 0; i < plotDataTable.numRows(); i++) {
            if ("Y".equals(results.getQcList().get(i).getIsPlot()))
                break;
            else
                cnt++;
        }
        if (plotDataTable.numRows() == cnt) {
            window.setError(Messages.get().noSampleSelectedError());
            return;
        }
        try {
            if (qcChartReportScreen == null)
                qcChartReportScreen = new QcChartReportScreen(window);
            else
                qcChartReportScreen.setWindow(window);

            results.setQcName((String)qcName.getSelection().getCells().get(0));
            qcChartReportScreen.runReport(results);
        } catch (Exception e) {
            Window.alert(e.getMessage());
            e.printStackTrace();
        }
    }

    private ArrayList<TableDataRow> getTableModel() {
        ArrayList<TableDataRow> model;
        QcChartReportViewVO.Value data;
        TableDataRow tr;

        model = new ArrayList<TableDataRow>();
        if (results == null || results.getQcList() == null || results.getQcList().size() == 0)
            return model;

        for (int i = 0; i < results.getQcList().size(); i++) {
            data = results.getQcList().get(i);
            tr = new TableDataRow(null,
                                  data.getIsPlot(),
                                  data.getAccessionNumber(),
                                  data.getLotNumber(),
                                  data.getWorksheetCreatedDate(),
                                  data.getAnalyteName(),
                                  data.getValue1(),
                                  data.getValue2(),
                                  data.getPlotValue(),
                                  data.getMean(),
                                  data.getUWL(),
                                  data.getUCL(),
                                  data.getLWL(),
                                  data.getLCL());
            tr.data = data;
            model.add(tr);
        }
        return model;
    }

    private void disableScreenButtons() {
        recomputeButton.enable(false);
        plotDataButton.enable(false);
        results = null;
        plotDataTable.load(getTableModel());
    }
}
