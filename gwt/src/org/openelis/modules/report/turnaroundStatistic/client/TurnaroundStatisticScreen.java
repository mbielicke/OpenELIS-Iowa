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
package org.openelis.modules.report.turnaroundStatistic.client;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

import org.openelis.cache.CategoryCache;
import org.openelis.cache.DictionaryCache;
import org.openelis.cache.SectionCache;
import org.openelis.domain.DictionaryDO;
import org.openelis.domain.OrganizationDO;
import org.openelis.domain.SectionViewDO;
import org.openelis.domain.TestMethodVO;
import org.openelis.domain.TurnAroundReportViewVO;
import org.openelis.domain.TurnAroundReportViewVO.PlotValue;
import org.openelis.domain.TurnAroundReportViewVO.StatisticType;
import org.openelis.domain.TurnAroundReportViewVO.Value;
import org.openelis.gwt.common.DataBaseUtil;
import org.openelis.gwt.common.Datetime;
import org.openelis.gwt.common.NotFoundException;
import org.openelis.gwt.common.OptionListItem;
import org.openelis.gwt.common.ReportStatus;
import org.openelis.gwt.common.data.Query;
import org.openelis.gwt.common.data.QueryData;
import org.openelis.gwt.event.DataChangeEvent;
import org.openelis.gwt.event.GetMatchesEvent;
import org.openelis.gwt.event.GetMatchesHandler;
import org.openelis.gwt.event.StateChangeEvent;
import org.openelis.gwt.screen.Screen;
import org.openelis.gwt.screen.ScreenDefInt;
import org.openelis.gwt.screen.ScreenEventHandler;
import org.openelis.gwt.services.ScreenService;
import org.openelis.gwt.widget.AppButton;
import org.openelis.gwt.widget.AutoComplete;
import org.openelis.gwt.widget.CalendarLookUp;
import org.openelis.gwt.widget.CheckBox;
import org.openelis.gwt.widget.Dropdown;
import org.openelis.gwt.widget.QueryFieldUtil;
import org.openelis.gwt.widget.table.TableDataRow;
import org.openelis.gwt.widget.table.TableRow;
import org.openelis.gwt.widget.table.TableWidget;
import org.openelis.gwt.widget.table.event.BeforeCellEditedEvent;
import org.openelis.gwt.widget.table.event.BeforeCellEditedHandler;
import org.openelis.gwt.widget.table.event.CellEditedEvent;
import org.openelis.gwt.widget.table.event.CellEditedHandler;
import org.openelis.meta.SampleMeta;
import org.openelis.modules.organization.client.OrganizationService;
import org.openelis.modules.preferences.client.PrinterService;
import org.openelis.modules.test.client.TestService;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * 
 * This class is used to carry the data entered on TurnaroundStatistic screen to the back-end
 * and also between different screens
 */

public class TurnaroundStatisticScreen extends Screen {

    private TurnaroundVO              data;
    private CalendarLookUp            releasedDateFrom, releasedDateTo;
    private Dropdown<Integer>         section, test, plotIntervalType, statType;
    private Dropdown<String>           printer;
    protected AppButton               getDataButton, plotDataButton, selectAllPlotDataButton,
                                      unselectAllPlotDataButton, selectAllAnalysisButton, unselectAllAnalysisButton;
    private TableWidget               plotDataTable, analysisTable;
    private TurnAroundReportViewVO    plotData;
    private CheckBox                  showAnalysis, excludePTSample;
    private AutoComplete<Integer>     organization;
    private TurnaroundStatisticScreen screen;
    private TurnaroundStatisticReportScreen turnaroundReportScreen;
    private String                    recRelLabel;
    private ArrayList<StatisticType>  statTypeList;

    public TurnaroundStatisticScreen() throws Exception {
        super((ScreenDefInt)GWT.create(TurnaroundStatisticDef.class));

        DeferredCommand.addCommand(new Command() {
            public void execute() {
                postConstructor();
            }
        });
    }

    /**
     * This method is called to set the initial state of widgets after the
     * screen is attached to the browser. It is usually called in deferred
     * command.
     */
    private void postConstructor() {
        
        initialize();
        setState(State.DEFAULT);
        initializeDropdowns();

        data = new TurnaroundVO();
        data.setIsShowAnalysis("N");
        DataChangeEvent.fire(this);
    }

    /**
     * Setup state and data change handles for every widget on the screen
     */
    @SuppressWarnings("unchecked")
    private void initialize() {
        screen = this;

        releasedDateFrom = (CalendarLookUp)def.getWidget("releasedFrom");
        addScreenHandler(releasedDateFrom, new ScreenEventHandler<Datetime>() {
            public void onValueChange(ValueChangeEvent<Datetime> event) {
                data.setReleasedDateFrom(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                releasedDateFrom.enable(EnumSet.of(State.DEFAULT).contains(event.getState()));
            }
        });

        releasedDateTo = (CalendarLookUp)def.getWidget("releasedTo");
        addScreenHandler(releasedDateTo, new ScreenEventHandler<Datetime>() {
            public void onValueChange(ValueChangeEvent<Datetime> event) {
                data.setReleasedDateTo(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                releasedDateTo.enable(EnumSet.of(State.DEFAULT).contains(event.getState()));
            }
        });
        
        showAnalysis = (CheckBox)def.getWidget("showAnalysis");
        addScreenHandler(showAnalysis, new ScreenEventHandler<String>() {
           public void onValueChange(ValueChangeEvent<String> event) {
                data.setIsShowAnalysis(event.getValue());
                /*
                 * Clear the analysis table based on whether showAnalysis checkbox is checked or not.
                 */
                DataChangeEvent.fire(screen, analysisTable);
            }

            public void onStateChange(StateChangeEvent<State> event) {
                showAnalysis.enable(EnumSet.of(State.DEFAULT).contains(event.getState()));
            }
        });

        section = (Dropdown)def.getWidget("section");
        addScreenHandler(section, new ScreenEventHandler<Integer>() {
           public void onValueChange(ValueChangeEvent<Integer> event) {
                data.setSectionId(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                section.enable(EnumSet.of(State.DEFAULT).contains(event.getState()));
            }

        });

        test = (Dropdown)def.getWidget("test");
        addScreenHandler(test, new ScreenEventHandler<Integer>() {
           public void onValueChange(ValueChangeEvent<Integer> event) {
                data.setTestId(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                test.enable(EnumSet.of(State.DEFAULT).contains(event.getState()));
            }

        });

        printer = (Dropdown)def.getWidget("printer");
        addScreenHandler(printer, new ScreenEventHandler<String>() {
            public void onValueChange(ValueChangeEvent<String> event) {
                data.setPrinter(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                printer.enable(EnumSet.of(State.DEFAULT).contains(event.getState()));
            }

        });

        plotIntervalType = (Dropdown<Integer>)def.getWidget("plot");
        addScreenHandler(plotIntervalType, new ScreenEventHandler<Integer>() {
            public void onValueChange(ValueChangeEvent<Integer> event) {
                data.setPlotIntervalId(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                plotIntervalType.enable(EnumSet.of(State.DEFAULT).contains(event.getState()));
            }
        });

        statType = (Dropdown<Integer>)def.getWidget("stats");
        statType.setMultiSelect(true);
        addScreenHandler(statType, new ScreenEventHandler<Integer>() {
            public void onValueChange(ValueChangeEvent<Integer> event) {
                data.setPlotStatisticIds(statType.getSelectionKeys());
                /*
                 * Create a list of statistic types which user selects
                 * to be sent to the back end.
                 */
                if (statTypeList == null)
                    statTypeList = new ArrayList<TurnAroundReportViewVO.StatisticType>();
                statTypeList.clear();
                for (TableDataRow row : statType.getSelections()) {
                    if(row.data.equals("ALL")){
                        for (StatisticType statType : StatisticType.values()) { 
                            if(!statTypeList.contains(statType))
                                statTypeList.add(statType);
                        }
                        break;
                    } else {
                        statTypeList.add((StatisticType)row.data);
                    }
                }
            }

            public void onStateChange(StateChangeEvent<State> event) {
                statType.enable(EnumSet.of(State.DEFAULT).contains(event.getState()));
            }
        });

        excludePTSample = (CheckBox)def.getWidget("excludePTSample");
        addScreenHandler(excludePTSample, new ScreenEventHandler<String>() {
            public void onValueChange(ValueChangeEvent<String> event) {
                data.setIsExcludePTSample(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                excludePTSample.enable(EnumSet.of(State.DEFAULT).contains(event.getState()));
            }
        });

        organization = (AutoComplete)def.getWidget("organization");
        addScreenHandler(organization, new ScreenEventHandler<Integer>() {
            public void onValueChange(ValueChangeEvent<Integer> event) {
                data.setOrganizationId(event.getValue());
                data.setOrganizationName(organization.getTextBoxDisplay());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                organization.enable(EnumSet.of(State.DEFAULT).contains(event.getState()));

            }
        });
        organization.addGetMatchesHandler(new GetMatchesHandler() {
            public void onGetMatches(GetMatchesEvent event) {
                getOrganizationMatches(event.getMatch(), organization);
            }
        });

        getDataButton = (AppButton)def.getWidget("getData");
        addScreenHandler(getDataButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                getData();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                getDataButton.enable(EnumSet.of(State.DEFAULT).contains(event.getState()));
            }
        });

        plotDataButton = (AppButton)def.getWidget("plotdata");
        addScreenHandler(plotDataButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                plotData();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                plotDataButton.enable(false);
            }
        });

        plotDataTable = (TableWidget)def.getWidget("plotDataTable");
        addScreenHandler(plotDataTable, new ScreenEventHandler<ArrayList<TableDataRow>>() {
            public void onDataChange(DataChangeEvent event) {
                plotDataTable.load(getPlotDataTableModel());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                plotDataTable.enable(true);
            }
        });
        
        plotDataTable.addSelectionHandler(new SelectionHandler<TableRow>() {            
            public void onSelection(SelectionEvent<TableRow> event) {   
                DataChangeEvent.fire(screen, analysisTable);
            }
        });

        plotDataTable.addBeforeCellEditedHandler(new BeforeCellEditedHandler() {
            public void onBeforeCellEdited(BeforeCellEditedEvent event) {
                if (event.getCol() > 0)
                    event.cancel();
            }
        });

        plotDataTable.addCellEditedHandler(new CellEditedHandler() {
            public void onCellUpdated(CellEditedEvent event) {
                int r, c;
                String val;
                TableDataRow row;
                TurnAroundReportViewVO.Value data;

                r = event.getRow();
                c = event.getCol();
                val = (String)plotDataTable.getObject(r, c);

                row = plotDataTable.getRow(r);
                data = (TurnAroundReportViewVO.Value)row.data;
                data.setIsPlot(val);
                /*
                 * when the checkbox for a plot Value is checked or unchecked
                 * the checkboxes for all analytes asscoiated with it need to be
                 * checked or unchecked too.
                 */
                updateAnalysisForPlotData(data, val);
                DataChangeEvent.fire(screen, analysisTable);
            }
        });
        
        analysisTable = (TableWidget)def.getWidget("plotAnalysisDataTable");
        addScreenHandler(analysisTable, new ScreenEventHandler<ArrayList<TableDataRow>>() {
            public void onDataChange(DataChangeEvent event) {
                analysisTable.load(getAnalysisTableModel());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                analysisTable.enable(true);
            }
        });

        analysisTable.addBeforeCellEditedHandler(new BeforeCellEditedHandler() {
            public void onBeforeCellEdited(BeforeCellEditedEvent event) {
                if (event.getCol() > 0)
                    event.cancel();
            }
        });

        analysisTable.addCellEditedHandler(new CellEditedHandler() {
            public void onCellUpdated(CellEditedEvent event) {
                int r, c;
                Object val;
                TableDataRow row;
                PlotValue data;
                Value value;

                r = event.getRow();
                c = event.getCol();
                val = analysisTable.getObject(r, c);

                row = analysisTable.getRow(r);
                data = (PlotValue)row.data;
                data.setIsPlot((String)val);
                value = (Value)plotDataTable.getSelection().data;
                /*
                 *  To check or uncheck the Value associated with this 
                 *  plot value whether or not all plot values are checked.
                 */
                updateIsPlotForValue(value);
            }
        });

        selectAllPlotDataButton = (AppButton)def.getWidget("selectAllPlotDataButton");
        addScreenHandler(selectAllPlotDataButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                for (int i = 0; i < plotDataTable.numRows(); i++ ) {
                    plotDataTable.setCell(i, 0, "Y");
                    plotData.getValues().get(i).setIsPlot("Y");
                    updateAnalysisForPlotData(plotData.getValues().get(i),"Y");
                    DataChangeEvent.fire(screen, analysisTable);
                }
            }

            public void onStateChange(StateChangeEvent<State> event) {
                selectAllPlotDataButton.enable(EnumSet.of(State.DEFAULT).contains(event.getState()));
            }
        });

        unselectAllPlotDataButton = (AppButton)def.getWidget("unselectAllPlotDataButton");
        addScreenHandler(unselectAllPlotDataButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                for (int i = 0; i < plotDataTable.numRows(); i++ ) {
                    plotDataTable.setCell(i, 0, "N");
                    plotData.getValues().get(i).setIsPlot("N");
                    updateAnalysisForPlotData(plotData.getValues().get(i),"N");
                    DataChangeEvent.fire(screen, analysisTable);
                }
            }

            public void onStateChange(StateChangeEvent<State> event) {
                unselectAllPlotDataButton.enable(EnumSet.of(State.DEFAULT).contains(event.getState()));
            }
        });
        
        selectAllAnalysisButton = (AppButton)def.getWidget("selectAllAnalysisButton");
        addScreenHandler(selectAllAnalysisButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                ArrayList<TableDataRow> model;
                Object val;
                PlotValue data;
                Value value;
                
                model = analysisTable.getData();

                if (model == null || model.size() == 0)
                    return;
                for (int i = 0; i < model.size(); i++) {
                    val = analysisTable.getCell(i, 0).getValue();
                    if ("N".equals(val)) {                        
                        analysisTable.setCell(i, 0, "Y");
                        data = (PlotValue)analysisTable.getRow(i).data;
                        data.setIsPlot("Y");
                    }
                }
                value = (Value)plotDataTable.getSelection().data;
                updateIsPlotForValue(value);
            }

            public void onStateChange(StateChangeEvent<State> event) {
                selectAllAnalysisButton.enable(EnumSet.of(State.DEFAULT).contains(event.getState()));
            }
        });

        unselectAllAnalysisButton = (AppButton)def.getWidget("unselectAllAnalysisButton");
        addScreenHandler(unselectAllAnalysisButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                ArrayList<TableDataRow> model;
                Object val;                   
                PlotValue data;
                Value value;
                model = analysisTable.getData();
                if (model == null || model.size() == 0)
                    return;
                for (int i = 0; i < model.size(); i++) {
                    val = analysisTable.getCell(i, 0).getValue();
                    if ("Y".equals(val)) {                        
                        analysisTable.setCell(i, 0, "N");
                        data = (PlotValue)analysisTable.getRow(i).data;
                        data.setIsPlot("N");
                    }
                }
                value = (Value)plotDataTable.getSelection().data;
                updateIsPlotForValue(value);
            }

            public void onStateChange(StateChangeEvent<State> event) {
                unselectAllAnalysisButton.enable(EnumSet.of(State.DEFAULT).contains(event.getState()));
            }
        });
    }

    private void initializeDropdowns() {
        ArrayList<TableDataRow> model;
        ArrayList<DictionaryDO> ls;
        ArrayList<SectionViewDO> list;
        ArrayList<OptionListItem> options;
        ArrayList<TestMethodVO> tests;
        TableDataRow row;

        try {
            recRelLabel = DictionaryCache.getBySystemName("turnaround_rec_rel").getEntry();
            
            model = new ArrayList<TableDataRow>();
            list = SectionCache.getList();
            model.add(new TableDataRow(null, ""));
            for (SectionViewDO data : list)
                model.add(new TableDataRow(data.getId(), data.getName()));
            section.setModel(model);

            model = new ArrayList<TableDataRow>();
            tests = TestService.get().fetchByName("%");
            model.add(new TableDataRow(null, ""));
            for (TestMethodVO item : tests)
                model.add(new TableDataRow(item.getTestId(), item.getTestName() + ", " + item.getMethodName()));
            test.setModel(model);

            model = new ArrayList<TableDataRow>();
            model.add(new TableDataRow("-view-", "View in PDF"));
            options = PrinterService.get().getPrinters("pdf");

            for (OptionListItem item : options)
                model.add(new TableDataRow(item.getKey(), item.getLabel()));
            printer.setModel(model);
        } catch (Exception e) {
            Window.alert(e.getMessage());
            window.close();
        }
        
        model = new ArrayList<TableDataRow>();
        ls = CategoryCache.getBySystemName("turnaround_statistic_range");

        for (DictionaryDO data : ls) {
            row = new TableDataRow(data.getId(), data.getEntry());
            row.enabled = "Y".equals(data.getIsActive());
            if("turnaround_all".equals(data.getSystemName()))
                row.data = "ALL";
            else if ("turnaround_col_rec".equals(data.getSystemName()))
                row.data = TurnAroundReportViewVO.StatisticType.COL_REC;
            else if ("turnaround_col_rdy".equals(data.getSystemName()))
                row.data = TurnAroundReportViewVO.StatisticType.COL_RDY;
            else if ("turnaround_col_rel".equals(data.getSystemName()))
                row.data = TurnAroundReportViewVO.StatisticType.COL_REL;
            else if ("turnaround_rec_rdy".equals(data.getSystemName()))
                row.data = TurnAroundReportViewVO.StatisticType.REC_RDY;
            else if ("turnaround_rec_cmp".equals(data.getSystemName()))
                row.data = TurnAroundReportViewVO.StatisticType.REC_COM;
            else if ("turnaround_rec_rel".equals(data.getSystemName()))
                row.data = TurnAroundReportViewVO.StatisticType.REC_REL;
            else if ("turnaround_ini_cmp".equals(data.getSystemName()))
                row.data = TurnAroundReportViewVO.StatisticType.INI_COM;
            else if ("turnaround_ini_rel".equals(data.getSystemName()))
                row.data = TurnAroundReportViewVO.StatisticType.INI_REL;
            else if ("turnaround_cmp_rel".equals(data.getSystemName()))
                row.data = TurnAroundReportViewVO.StatisticType.COM_REL;

            model.add(row);
        }
        statType.setModel(model);

        model = new ArrayList<TableDataRow>();
        ls = CategoryCache.getBySystemName("turnaround_plot_interval");

        for (DictionaryDO data : ls) {
            row = new TableDataRow(data.getId(), data.getEntry());
            row.enabled = ("Y".equals(data.getIsActive()));
            row.data = data;
            model.add(row);
        }
        plotIntervalType.setModel(model);
    }

    public void setPlotData(TurnAroundReportViewVO plotData) {
        this.plotData = plotData;
        DataChangeEvent.fire(this);
    }

    protected void getData() {
        Query query;
        QueryData field;
        Datetime fromDate, toDate;
        Integer plotInterval, section, test, organization;
        String exclPT;
        ArrayList<QueryData> fields;

        refreshScreen();
        clearErrors();
        if ( !validate()) {
            window.setError(consts.get("correctErrors"));
            return;
        }

        query = new Query();
        fields = new ArrayList<QueryData>();

        fromDate = data.getReleasedDateFrom();
        toDate = data.getReleasedDateTo();
        if(fromDate != null && toDate != null) {
            field = new QueryData();
            field.key = SampleMeta.getAnalysisReleasedDate();
            field.query = DataBaseUtil.concatWithSeparator(fromDate.toString(), "..", toDate.toString());
            field.type = QueryData.Type.DATE;
            fields.add(field);
        }
        
        plotInterval = data.getPlotIntervalId();
        if (plotInterval != null) {
            field = new QueryData();
            field.key = "PLOT_INTERVAL";
            field.query = plotInterval.toString();
            field.type = QueryData.Type.INTEGER;
            fields.add(field);
        }

        section = data.getSectionId();
        if (section != null) {
            field = new QueryData();
            field.key = SampleMeta.getAnalysisSectionId();
            field.query = section.toString();
            field.type = QueryData.Type.INTEGER;
            fields.add(field);
        }

        test = data.getTestId();
        if (test != null) {
            field = new QueryData();
            field.key = SampleMeta.getAnalysisTestId();
            field.query = test.toString();
            field.type = QueryData.Type.INTEGER;
            fields.add(field);
        }

        organization = data.getOrganizationId();
        if (organization != null) {
            field = new QueryData();
            field.key = SampleMeta.getOrgId();
            field.query = organization.toString();
            field.type = QueryData.Type.INTEGER;
            fields.add(field);
        }

        exclPT = data.getIsExcludePTSample();
        if (exclPT != null) {
            field = new QueryData();
            field.key = "EXCLUDE_PT";
            field.query = exclPT.toString();
            field.type = QueryData.Type.STRING;
            fields.add(field);
        }
        query.setFields(fields);

        window.setBusy(consts.get("fetching"));
        TurnaroundStatisticReportService.get().fetchForTurnaroundStatistic(query, new AsyncCallback<TurnAroundReportViewVO>() {
            public void onSuccess(TurnAroundReportViewVO result) {
                setPlotData(result);
                plotDataButton.enable(true);
                window.setDone(consts.get("done"));
            }

            public void onFailure(Throwable error) {
                setPlotData(null);
                if (error instanceof NotFoundException) {
                    window.setDone(consts.get("noRecordsFound"));
                    setState(State.DEFAULT);
                } else {
                    Window.alert("Error: Method call query failed; " + error.getMessage());
                    window.setError(consts.get("queryFailed"));
                }
            }
        });
    }

    protected void plotData() {
        boolean allUnSelected;
        

        allUnSelected = true;
        for (int i = 0; i < plotDataTable.numRows(); i++ ) {
            if ("Y".equals(plotData.getValues().get(i).getIsPlot())) {
                allUnSelected = false;
                break;
            } 
        }
        /* 
         * If user clicks on plot data without selecting any values then we don't send the data 
         * to the back end.
         */
        if (allUnSelected) {
            window.setError(consts.get("noSampleSelectedError"));
            return;
        }
        try {
            if (turnaroundReportScreen == null)
                turnaroundReportScreen = new TurnaroundStatisticReportScreen(window);
            else
                turnaroundReportScreen.setWindow(window);
            
            plotData.setTypes(statTypeList);
            plotData.setFromDate(data.getReleasedDateFrom().getDate());
            plotData.setToDate(data.getReleasedDateTo().getDate());
            plotData.setIntervalId(data.getPlotIntervalId());
            plotData.setPrinter(data.getPrinter());
            turnaroundReportScreen.runReport(plotData, new AsyncCallback<ReportStatus>() {

                @Override
                public void onFailure(Throwable caught) {
                    // TODO Auto-generated method stub
                    
                }

                @Override
                public void onSuccess(ReportStatus result) {
                    // TODO Auto-generated method stub
                    
                }
            });
        } catch (Exception e) {
            Window.alert(e.getMessage());
            e.printStackTrace();
        }    
    }

    private ArrayList<TableDataRow> getPlotDataTableModel() {
        Float min, max, avg, sd;
        String label;
        TableDataRow tr;
        StatisticType type;
        ArrayList<TableDataRow> model;
        Value.Stat stat;
        
        model = new ArrayList<TableDataRow>();
        if (plotData == null || plotData.getValues() == null || plotData.getValues().size() == 0)
            return model;
        
        avg = null; 
        sd = null;
        type = null;
        label = null;
        /*
         * if user selects ALL as the plot statistics, only Rec-Rel data is shown in the table on the screen. Otherwise choose the first type selected.
         */
        for (TableDataRow r : statType.getSelections()) {
            if (r.data.equals("ALL")) {
                type = StatisticType.REC_REL;
                label = recRelLabel;
                break;
            } 
        }
        
        if( type == null) {
            tr = statType.getSelections().get(0);
            type = (TurnAroundReportViewVO.StatisticType)tr.data;
            label = (String)tr.cells.get(0).getValue();
        }
        
        for (Value val : plotData.getValues()) {
            stat = val.getStats(type);
            avg = daysAndHours(stat.getAvg());
            sd = daysAndHours(stat.getSd());
            min = daysAndHours(stat.getMin());
            max = daysAndHours(stat.getMax());
            tr = new TableDataRow(null, val.getIsPlot(), val.getPlotDate(), val.getTest(),
                                  val.getMethod(), label, min, max, avg, sd, stat.getNumTested());
            tr.data = val;
            model.add(tr);
        }
        return model;
    }
    
    private ArrayList<TableDataRow> getAnalysisTableModel() {
        TableDataRow tr;
        ArrayList<TableDataRow> model;
        Value val;
        int i;

        model = new ArrayList<TableDataRow>();
        if ( plotData == null || plotDataTable.getSelectedRow() == -1 || "N".equals(data.getIsShowAnalysis()))
            return model;
        val = (Value)plotDataTable.getSelection().data;
        for (PlotValue pv : val.getPlotValues()) {
            tr = new TableDataRow(12);
            tr.cells.get(0).setValue(pv.getIsPlot());
            tr.cells.get(1).setValue(pv.getAccessionNumber());
            tr.cells.get(2).setValue(pv.getRevision());
            for (StatisticType statType : StatisticType.values()) 
                tr.cells.get(3 + statType.ordinal()).setValue(daysAndHours(pv.getStatAt(statType)));
            
            tr.data = pv;
            model.add(tr);
        }
        return model;
    }
    
    private void updateAnalysisForPlotData(Value value, String val) {
        PlotValue analysis;       
        List<PlotValue> plotValues;
                          
        plotValues = value.getPlotValues();
        for (int i = 0; i < plotValues.size(); i++) {
            analysis = plotValues.get(i);
            analysis.setIsPlot(val);
        } 
    }
    
    private void updateIsPlotForValue(Value data) {
        boolean hasChecked;
        /*
         * A value should be included in the list of values to be plotted only
         * if atleast one of its plot value is selected. So we need to update
         * the isPlot field of the Value based on isPlot field for all its plot
         * values.
         */
        hasChecked = false;
        for (PlotValue pv : data.getPlotValues()) {
            if ("Y".equals(pv.getIsPlot())) {
                hasChecked = true;
                break;
            }
        }
        if (hasChecked)
            data.setIsPlot("Y");
        else
            data.setIsPlot("N");
        plotDataTable.setCell(plotDataTable.getSelectedRow(), 0, data.getIsPlot());
    }

    private void refreshScreen() {
        plotDataButton.enable(false);
        plotData = null;
        plotDataTable.load(getPlotDataTableModel());
        analysisTable.load(getAnalysisTableModel());
    }

    private void getOrganizationMatches(String match, AutoComplete widget) {
        TableDataRow row;
        ArrayList<OrganizationDO> orgs;
        ArrayList<TableDataRow> model;

        window.setBusy();
        try {
            orgs = OrganizationService.get().fetchByIdOrName(QueryFieldUtil.parseAutocomplete(match));
            model = new ArrayList<TableDataRow>();
            for (OrganizationDO data : orgs ) {
                row = new TableDataRow(4);
                row.key = data.getId();
                row.data = data;
                row.cells.get(0).setValue(data.getName());
                row.cells.get(1).setValue(data.getAddress().getStreetAddress());
                row.cells.get(2).setValue(data.getAddress().getCity());
                row.cells.get(3).setValue(data.getAddress().getState());

                model.add(row);
            }
            widget.showAutoMatches(model);
        } catch (Throwable e) {
            e.printStackTrace();
            Window.alert(e.getMessage());
        }
        window.clearStatus();
    }
    
    public static Float daysAndHours(Integer hours) {
        int days, hrs;
        ArrayList<Object> daysAndHrs;
        
        if (hours == null)
            return null;
        daysAndHrs = new ArrayList<Object>();
        days = Math.abs(hours) / 24;
        hrs = Math.abs(hours) % 24;
        daysAndHrs.add(days);
        
        if (hrs < 10)
            daysAndHrs.add(".0");
        else
            daysAndHrs.add(".");
        
        daysAndHrs.add(hrs);
        
        if (hours < 0)
            daysAndHrs.add(0, "-");
        
        return Float.parseFloat(DataBaseUtil.concatWithSeparator(daysAndHrs, ""));
    }

    public class TurnaroundVO {

        protected Integer plotIntervalId, sectionId, testId, organizationId;
        protected ArrayList<Integer> plotStatisticIds;
        protected String  isShowAnalysis, isExcludePTSample, printer, organizationName;
        protected Datetime releasedDateFrom, releasedDateTo;

        public Integer getPlotIntervalId() {
            return plotIntervalId;
        }

        public void setPlotIntervalId(Integer plotIntervalId) {
            this.plotIntervalId = plotIntervalId;
        }

        public ArrayList getPlotStatisticIds() {
            return plotStatisticIds;
        }

        public void setPlotStatisticIds(ArrayList plotStatisticId) {
            this.plotStatisticIds = plotStatisticId;
        }

        public Integer getSectionId() {
            return sectionId;
        }

        public void setSectionId(Integer sectionId) {
            this.sectionId = sectionId;
        }

        public Integer getTestId() {
            return testId;
        }

        public void setTestId(Integer testId) {
            this.testId = testId;
        }

        public String getPrinter() {
            return printer;
        }

        public void setPrinter(String printer) {
            this.printer = DataBaseUtil.toString(printer);
        }

        public Integer getOrganizationId() {
            return organizationId;
        }

        public void setOrganizationId(Integer organizationId) {
            this.organizationId = organizationId;
        }
        
        public String getOrganizationName() {
            return organizationName;
        }

        public void setOrganizationName(String organizationName) {
            this.organizationName = organizationName;
        }

        
        public String getIsShowAnalysis() {
            return isShowAnalysis;
        }

        public void setIsShowAnalysis(String isShowAnalysis) {
            this.isShowAnalysis = DataBaseUtil.toString(isShowAnalysis);
        }

        public String getIsExcludePTSample() {
            return isExcludePTSample;
        }

        public void setIsExcludePTSample(String isExcludePTSample) {
            this.isExcludePTSample = DataBaseUtil.toString(isExcludePTSample);
        }

        public Datetime getReleasedDateFrom() {
            return releasedDateFrom;
        }

        public void setReleasedDateFrom(Datetime releasedDateFrom) {
            this.releasedDateFrom = releasedDateFrom;
        }

        public Datetime getReleasedDateTo() {
            return releasedDateTo;
        }

        public void setReleasedDateTo(Datetime releasedDateTo) {
            this.releasedDateTo = releasedDateTo;
        }
    }
}
