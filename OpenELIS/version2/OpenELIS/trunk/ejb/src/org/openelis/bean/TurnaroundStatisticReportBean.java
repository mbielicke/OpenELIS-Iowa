package org.openelis.bean;

import java.io.File;
import java.io.FileOutputStream;
import java.net.URL;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import net.sf.jasperreports.engine.JRExporter;
import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.engine.util.JRLoader;

import org.apache.log4j.Logger;
import org.jboss.ejb3.annotation.SecurityDomain;
import org.openelis.domain.DictionaryDO;
import org.openelis.domain.TurnAroundReportViewVO;
import org.openelis.domain.TurnAroundReportViewVO.PlotValue;
import org.openelis.domain.TurnAroundReportViewVO.StatisticType;
import org.openelis.domain.TurnAroundReportViewVO.Value;
import org.openelis.domain.TurnAroundReportViewVO.Value.Stat;
import org.openelis.gwt.common.DataBaseUtil;
import org.openelis.gwt.common.InconsistencyException;
import org.openelis.gwt.common.NotFoundException;
import org.openelis.gwt.common.ReportStatus;
import org.openelis.gwt.common.data.QueryData;
import org.openelis.local.AnalysisQAEventLocal;
import org.openelis.local.DictionaryLocal;
import org.openelis.local.SampleQAEventLocal;
import org.openelis.local.SessionCacheLocal;
import org.openelis.meta.SampleMeta;
import org.openelis.remote.TurnaroundStatisticReportRemote;
import org.openelis.report.turnaroundstatistic.TurnaroundDataSource;
import org.openelis.util.QueryBuilderV2;
import org.openelis.utils.EJBFactory;
import org.openelis.utils.JasperUtil;
import org.openelis.utils.ReportUtil;

@Stateless
@SecurityDomain("openelis")
public class TurnaroundStatisticReportBean implements TurnaroundStatisticReportRemote {
    @PersistenceContext(unitName = "openelis")
    private EntityManager           manager;
    
    @EJB
    private SessionCacheLocal       session;

    @EJB
    private DictionaryLocal         dictionary;
    
    @EJB
    private SampleQAEventLocal      sampleQAEvent;

    @EJB
    private AnalysisQAEventLocal    analysisQAEvent;

    private static Integer          releasedStatusId, ptSampleTypeId;
    
    private static DictionaryDO     analysisDailyTotalPlotInterval, weeklyTotalPlotInterval,
                                    monthlyTotalPlotInterval;

    private static final Logger     log  = Logger.getLogger(TurnaroundStatisticReportBean.class);

    private static final SampleMeta meta = new SampleMeta();

    @PostConstruct
    public void init() {
        try {
            releasedStatusId = dictionary.fetchBySystemName("analysis_released").getId();
            analysisDailyTotalPlotInterval = dictionary.fetchBySystemName("turnaround_daily");
            weeklyTotalPlotInterval = dictionary.fetchBySystemName("turnaround_weekly");
            monthlyTotalPlotInterval = dictionary.fetchBySystemName("turnaround_monthly");
            ptSampleTypeId = dictionary.fetchBySystemName("pt_sample").getId();
        } catch (Throwable e) {
            log.error("Failed to lookup constants for dictionary entries", e);
        }
    }

    public TurnAroundReportViewVO fetchForTurnaroundStatistic(ArrayList<QueryData> paramList) throws Exception {
        int i;
        Boolean sampleHasOverride;
        Integer plotTypeId, analysisId, sampleId;
        String anaRelDateParam, plotInterval, excludePT;
        DateFormat format;
        Date currentReleasedDate;
        TurnAroundReportViewVO data;
        Object result[];
        ArrayList<Object[]> resultList;
        HashMap<String, QueryData> param;
        HashMap<Integer, Boolean> sampleOverrideMap;

        param = ReportUtil.getMapParameter(paramList);
        plotInterval = ReportUtil.getSingleParameter(param, "PLOT_INTERVAL");
        excludePT = ReportUtil.getSingleParameter(param, "EXCLUDE_PT");
        anaRelDateParam = ReportUtil.getSingleParameter(param, SampleMeta.getAnalysisReleasedDate());
        format = new SimpleDateFormat("yyyy-MM-dd");
        currentReleasedDate = format.parse(anaRelDateParam.split("[..]")[0]);

        try {
            plotTypeId = Integer.parseInt(plotInterval);
        } catch (Exception e) {
            throw new InconsistencyException("You must specify a valid plot interval");
        }

        /*
         * Removing the fields which will not be needed in the query from the
         * paramlist
         */
        i = 0;
        while (i < paramList.size()) {
            if ( ("PLOT_INTERVAL").equals(paramList.get(i).key) ||
                ("EXCLUDE_PT").equals(paramList.get(i).key)) {
                paramList.remove(i);
                if (i > 0)
                    i-- ;
            } else {
                i++ ;
            }
        }

        /*
         * method for querying database to retrieve the records which satisfy
         * the query.
         */
        resultList = getValues(paramList, excludePT);
        /*
         * Filtering out records which don't have sample or analysis QA overrides. This
         * was needed since outer queries could not be written using EJBQL.
         */
        i = 0;
        sampleOverrideMap = new HashMap<Integer, Boolean>();
        while (i < resultList.size()) {
            result = resultList.get(i);
            analysisId = (Integer)result[8];
            sampleId = (Integer)result[13];
            sampleHasOverride = sampleOverrideMap.get(sampleId);
            
            if (sampleHasOverride == null) {
                try {
                    sampleQAEvent.fetchResultOverrideBySampleId(sampleId);
                    sampleOverrideMap.put(sampleId, true);
                    /*
                     * remove if sample qaevent override is found
                     */
                    resultList.remove(i);
                    continue;
                } catch (NotFoundException e) {
                    sampleOverrideMap.put(sampleId, false);
                }
            } else if (sampleHasOverride) {
                resultList.remove(i);
                continue;
            } 

            try {
                analysisQAEvent.fetchResultOverrideByAnalysisId(analysisId);
                /*
                 * remove if analysis qaevent override is found
                 */
                resultList.remove(i);
            } catch (NotFoundException exc) {
                i++ ;
            }
        }

        data = new TurnAroundReportViewVO();
        if (analysisDailyTotalPlotInterval.getId().equals(plotTypeId))
            data = getDataForDailyReport(resultList, currentReleasedDate);
        else if (weeklyTotalPlotInterval.getId().equals(plotTypeId))
            data = fetchForWeeklyReport(resultList, currentReleasedDate);
        else if (monthlyTotalPlotInterval.getId().equals(plotTypeId))
            data = fetchForMonthlyReport(resultList, currentReleasedDate);

        return data;
    }
    
    private ArrayList<Object[]> getValues(ArrayList<QueryData> fields, String excludePT) throws Exception {
        QueryBuilderV2 builder;
        Query query;

        builder = new QueryBuilderV2();
        builder.setMeta(meta);
        builder.setSelect("distinct " + SampleMeta.getAccessionNumber() + "," +
                          SampleMeta.getAnalysisRevision() + "," +
                          SampleMeta.getAnalysisTestName() + "," +
                          SampleMeta.getAnalysisMethodName() + "," +
                          SampleMeta.getCollectionDate() + "," + SampleMeta.getCollectionTime() +
                          "," + SampleMeta.getReceivedDate() + "," +
                          SampleMeta.getAnalysisReleasedDate() + "," + SampleMeta.getAnalysisId() +
                          "," + SampleMeta.getAnalysisTestId() + "," +
                          SampleMeta.getAnalysisAvailableDate() + "," +
                          SampleMeta.getAnalysisCompletedDate() + "," +
                          SampleMeta.getAnalysisStartedDate() + "," + SampleMeta.getId());
        builder.addWhere(SampleMeta.getAnalysisTestIsActive() + "=" + "'Y'");
        builder.addWhere(SampleMeta.getAnalysisStatusId() + "=" + releasedStatusId);
        builder.addWhere(SampleMeta.getAnalysisTestMethodId() + "=" +
                         SampleMeta.getAnalysisMethodId());
        if ("Y".equals(excludePT))
            builder.addWhere(SampleMeta.getItemTypeOfSampleId() + "!=" + ptSampleTypeId);
        builder.constructWhere(fields);
        builder.setOrderBy(SampleMeta.getAnalysisReleasedDate() + ", " +
                           SampleMeta.getAnalysisTestName() + ", " +
                           SampleMeta.getAnalysisMethodName());

        query = manager.createQuery(builder.getEJBQL());
        builder.setQueryParams(query, fields);

        return DataBaseUtil.toArrayList(query.getResultList());
    }
    
    private TurnAroundReportViewVO getDataForDailyReport(ArrayList<Object[]> resultList,
                                                       Date startDate) throws Exception {
        Integer currTestId, prevTestId;
        DateFormat format;
        Date currReleaseDate, nextReleasedDate;
        TurnAroundReportViewVO data;
        ArrayList<Value> values;
        Value value;
        List<PlotValue> plotValues;

        data = new TurnAroundReportViewVO();
        values = new ArrayList<Value>();
        format = new SimpleDateFormat("yyyy-MM-dd");

        prevTestId = null;
        plotValues = null;
        nextReleasedDate = startDate;

        for (Object[] result : resultList) {
            currReleaseDate = format.parse( ((Timestamp)result[7]).toString());
            currTestId = (Integer)result[9];
            /*
             * for the purpose of calculating the statistics for a test, the
             * rows returned from the database are grouped first by day and then
             * by test
             */
            if (currReleaseDate.compareTo(nextReleasedDate) == 0) {
                if (currTestId.equals(prevTestId)) {
                    plotValues.add(createPlotValue(result));
                } else {
                    /*
                     * this is done to group the rows by test
                     */
                    value = createValue(result);
                    values.add(value);
                    plotValues = value.getPlotValues();
                    prevTestId = currTestId;
                }
            } else {
                /*
                 * this is done to group the rows by day
                 */
                value = createValue(result);
                values.add(value);
                plotValues = value.getPlotValues();
                /*
                 * The nextreleased date needs to be set to the current released date so
                 * that all the analyses released during that day can be grouped
                 * by this date.
                 */
                nextReleasedDate = currReleaseDate;
                prevTestId = currTestId;
            }
        }
        
        data.setValues(values);
        calculateStatistics(data);

        return data;
    }

    private TurnAroundReportViewVO fetchForWeeklyReport(ArrayList<Object[]> resultList,
                                                        Date startDate) throws Exception {
        Integer currTestId;
        Calendar calStart, calPlot;
        DateFormat format;
        Date nextReleasedDate, currReleasedDate;
        TurnAroundReportViewVO data;
        ArrayList<Value> values;
        Value value;
        HashMap<Integer, Value> testValueMap;

        data = new TurnAroundReportViewVO();
        values = new ArrayList<Value>();
        format = new SimpleDateFormat("yyyy-MM-dd");

        calStart = Calendar.getInstance();
        nextReleasedDate = getNextSunday(calStart, startDate);
       
        testValueMap = new HashMap<Integer, Value>();
        calPlot = Calendar.getInstance();

        for (Object[] result : resultList) {
            currReleasedDate = format.parse( ((Timestamp)result[7]).toString());
            currTestId = (Integer)result[9];
            /*
             * for the purpose of calculating the statistics for a test, the
             * rows returned from the database are grouped first by week and then
             * by test
             */
            if (currReleasedDate.compareTo(nextReleasedDate) <= 0) {
                value = testValueMap.get(currTestId);
                if (value != null) {
                    value.getPlotValues().add(createPlotValue(result));
                } else {
                    /*
                     * new test
                     */
                    calPlot.setTime(currReleasedDate);
                    value = createValueWeekly(result, calPlot);
                    testValueMap.put(currTestId, value);
                    values.add(value);
                }
            } else {
                /*
                 * this is done to group the rows by week
                 */
                testValueMap.clear();
                calPlot.setTime(currReleasedDate);
                value = createValueWeekly(result, calPlot);
                testValueMap.put(currTestId, value);
                values.add(value);
                /*
                 * The next released date needs to be set to the next Sunday
                 * that all the analyses released during this period
                 * can be grouped by this date.
                 */
                nextReleasedDate = getNextSunday(calStart, currReleasedDate);
            }
        }
        
        data.setValues(values);
        calculateStatistics(data);
        
        return data;
    }
    
    private TurnAroundReportViewVO fetchForMonthlyReport(ArrayList<Object[]> resultList,
                                                         Date startDate) throws Exception {
        Integer currTestId;
        Calendar calStart, calPlot;
        DateFormat format;
        Date nextReleasedDate, currReleasedDate;
        TurnAroundReportViewVO data;
        ArrayList<Value> values;
        Value value;
        List<PlotValue> plotValues;
        HashMap<Integer, Value> testValueMap;

        data = new TurnAroundReportViewVO();
        values = new ArrayList<Value>();
        format = new SimpleDateFormat("yyyy-MM-dd");

        calStart = Calendar.getInstance();
        nextReleasedDate = getFirstOfNextMonth(calStart, startDate);

        plotValues = null;
        testValueMap = new HashMap<Integer, Value>();
        calPlot = Calendar.getInstance();

        for (Object[] result : resultList) {
            currReleasedDate = format.parse( ((Timestamp)result[7]).toString());
            currTestId = (Integer)result[9];
            /*
             * for the purpose of calculating the statistics for a test, the
             * rows returned from the database are grouped first by day and then
             * by test
             */
            if (currReleasedDate.compareTo(nextReleasedDate) < 0) {
                value = testValueMap.get(currTestId);
                if (value != null) {
                    plotValues = value.getPlotValues();
                    plotValues.add(createPlotValue(result));
                } else {
                    /*
                     * new test
                     */
                    calPlot.setTime(currReleasedDate);
                    value = createValueMonthly(result, calPlot);
                    testValueMap.put(currTestId, value);
                    values.add(value);
                }
            } else {
                /*
                 * this is done to group the rows by month
                 */
                testValueMap = new HashMap<Integer, Value>();
                calPlot.setTime(currReleasedDate);
                value = createValueMonthly(result, calPlot);
                testValueMap.put(currTestId, value);
                values.add(value);
                /*
                 * The next released date needs to be set to the 1st of the next month
                 * that all the analyses released during this period can be grouped by this date.
                 */
                nextReleasedDate = getFirstOfNextMonth(calStart, currReleasedDate);
            }
        }

        data.setValues(values);
        calculateStatistics(data);
        
        return data;
    }
    
    /*
     * Copies common fields to the data VO for plotting
     */
    private Value createValue(Object[] result) throws Exception {
        Value value;
        ArrayList<PlotValue> plotValues;

        value = new Value();
        value.setIsPlot("Y");
        value.setPlotDate((Timestamp)result[7]);
        value.setTest((String)result[2]);
        value.setMethod((String)result[3]);
        
        plotValues = new ArrayList<PlotValue>();
        plotValues.add(createPlotValue(result));
        value.setPlotValues(plotValues);

        return value;
    }

    private Value createValueWeekly(Object[] result, Calendar calPlot) throws Exception {
        Value value;
        ArrayList<PlotValue> plotValues;
        
        value = new Value();
        value.setIsPlot("Y");
        value.setTest((String)result[2]);
        value.setMethod((String)result[3]);
        /*
         * Setting the plotDate to the latest previous Sunday because the date in 
         * the calendar may be several weeks after the latest plot date. 
         */
        calPlot.add(Calendar.DATE, - (calPlot.get(Calendar.DAY_OF_WEEK) - Calendar.SUNDAY));
        value.setPlotDate(calPlot.getTime());
        
        plotValues = new ArrayList<PlotValue>();
        plotValues.add(createPlotValue(result));
        value.setPlotValues(plotValues);

        return value;
    }
    
    private Value createValueMonthly(Object[] result, Calendar calPlot) throws Exception {
        Value value;
        ArrayList<PlotValue> plotValues;

        value = new Value();
        value.setIsPlot("Y");
        value.setTest((String)result[2]);
        value.setMethod((String)result[3]);
        /*
         * Setting the plotDate to the latest previous 1st of the Month because the date in
         * the calendar may be several months after the latest plot date. 
         */
        calPlot.set(Calendar.DAY_OF_MONTH, 1);   
        value.setPlotDate(calPlot.getTime());
        
        plotValues = new ArrayList<PlotValue>();
        plotValues.add(createPlotValue(result));
        value.setPlotValues(plotValues);

        return value;
    }

    private Date getNextSunday(Calendar cal, Date currPlotDate) {
        cal.setTime(currPlotDate);
        /*
         * Calculate number of days to get to the Sunday of the following week
         */
        cal.add(Calendar.WEEK_OF_MONTH, 1);
        cal.set(Calendar.DAY_OF_WEEK, 1);
        return cal.getTime();
    }
    
    private Date getFirstOfNextMonth(Calendar cal, Date currPlotDate) {
        cal.setTime(currPlotDate);
        /*
         * Set the date to be the 1st of the next month.
         */
        cal.add(Calendar.MONTH, 1);
        cal.set(Calendar.DAY_OF_MONTH, 1);        
        return cal.getTime();
    }

    private PlotValue createPlotValue(Object[] result) throws Exception {
        Integer accessionNumber, revision;
        Timestamp colDateTime, colDate, colTime, recDate, relDate, availDate, startedDate, complDate;
        PlotValue plotValue;

        colDateTime = null;
        complDate = startedDate = null;
        accessionNumber = (Integer)result[0];
        revision = (Integer)result[1];
        colDate = (Timestamp)result[4];
        colTime = (Timestamp)result[5];
        recDate = (Timestamp)result[6];
        relDate = (Timestamp)result[7];
        availDate = (Timestamp)result[10];
        complDate = (Timestamp)result[11];
        startedDate = (Timestamp)result[12];

        plotValue = new PlotValue();
        plotValue.setIsPlot("Y");
        plotValue.setAccessionNumber(accessionNumber);
        plotValue.setRevision(revision);

        if (colDate != null) {
            if (colTime != null)
                colDateTime = JasperUtil.concatDateAndTime(colDate, colTime);
            else
                colDateTime = colDate;
            
            if (recDate != null)
                plotValue.setStatAt(StatisticType.COL_REC,
                                    JasperUtil.delta_hours(colDateTime, recDate));
            if (availDate != null)
                plotValue.setStatAt(StatisticType.COL_RDY,
                                    JasperUtil.delta_hours(colDateTime, availDate));
            if (relDate != null)
                plotValue.setStatAt(StatisticType.COL_REL,
                                    JasperUtil.delta_hours(colDateTime, relDate));
        }

        if (recDate != null) {
            if (availDate != null)
                plotValue.setStatAt(StatisticType.REC_RDY,
                                    JasperUtil.delta_hours(recDate, availDate));

            if (complDate != null)
                plotValue.setStatAt(StatisticType.REC_COM,
                                    JasperUtil.delta_hours(recDate, complDate));

            if (relDate != null)
                plotValue.setStatAt(StatisticType.REC_REL, JasperUtil.delta_hours(recDate, relDate));
        }
        
        if (startedDate != null) {
            if (complDate != null)
                plotValue.setStatAt(StatisticType.INI_COM,
                                    JasperUtil.delta_hours(startedDate, complDate));

            if (relDate != null)
                plotValue.setStatAt(StatisticType.INI_REL,
                                    JasperUtil.delta_hours(startedDate, relDate));
        }
        
        if (complDate != null && relDate != null)
            plotValue.setStatAt(StatisticType.COM_REL, JasperUtil.delta_hours(complDate, relDate));

        return plotValue;
    }

    public ReportStatus runReport(TurnAroundReportViewVO data) throws Exception {
        Integer intervalId;
        String printer, fromDate, toDate, key, intervalType;
        ReportStatus status;
        String printstat;
        ArrayList<Value> values, valueList;
        URL url;
        File tempFile;
        JasperReport jreport;
        JasperPrint jprint;
        JRExporter jexport;
        TurnaroundDataSource ds;
        HashMap<String, Object> jparam;
        HashMap<String, ArrayList<Value>> map;

        fromDate = data.getFromDate().toString();
        toDate = data.getToDate().toString();
        printer = data.getPrinter();
        intervalId = data.getIntervalId();
        intervalType = null;
        if (analysisDailyTotalPlotInterval.getId().equals(intervalId))
            intervalType = analysisDailyTotalPlotInterval.getEntry();
        else if (weeklyTotalPlotInterval.getId().equals(intervalId))
            intervalType = weeklyTotalPlotInterval.getEntry();
        else if (monthlyTotalPlotInterval.getId().equals(intervalId))
            intervalType = monthlyTotalPlotInterval.getEntry();
        /*
         * push status into session so we can query it while the report is
         * running
         */
        status = new ReportStatus();
        session.setAttribute("TurnaroundStatisticReport", status);

        values = data.getValues();
        if (values.size() > 1)
            Collections.sort(values, new MyComparator());
        
        calculateStatistics(data);
        ds = new TurnaroundDataSource();
        ds.setTypes(data.getTypes());
        ds.setValues(values);

        /*
         * Create HashMap with a new entry for each new test and method. The
         * value would be Value.
         */
        map = new HashMap<String, ArrayList<Value>>();
        for (Value value : values) {
            key = DataBaseUtil.concatWithSeparator(value.getTest(), ", ", value.getMethod());
            if ( !map.containsKey(key)) {
                valueList = new ArrayList<Value>();
                map.put(key, valueList);
            } else {
                valueList = map.get(key);
            }
            valueList.add(value);
        }
        ds.setMap(map);

        status.setMessage("Initializing report");
        url = ReportUtil.getResourceURL("org/openelis/report/turnaroundstatistic/main.jasper");

        tempFile = File.createTempFile("turnaroundstatisticreport", ".pdf", new File("/tmp"));
        jparam = new HashMap<String, Object>();
        jparam.put("LOGNAME", EJBFactory.getUserCache().getName());
        jparam.put("FROM_DATE", fromDate);
        jparam.put("TO_DATE", toDate);
        jparam.put("INTERVAL_TYPE", intervalType);
        jparam.put("TURNAROUND_DATASOURCE", ds);

        status.setMessage("Loading report");
        jreport = (JasperReport)JRLoader.loadObject(url);
        jprint = JasperFillManager.fillReport(jreport, jparam, ds);
        jexport = new JRPdfExporter();
        jexport.setParameter(JRExporterParameter.OUTPUT_STREAM, new FileOutputStream(tempFile));
        jexport.setParameter(JRExporterParameter.JASPER_PRINT, jprint);

        status.setMessage("Outputing report").setPercentComplete(20);

        jexport.exportReport();
        status.setPercentComplete(100);

        if (ReportUtil.isPrinter(printer)) {
            printstat = ReportUtil.print(tempFile, printer, 1);
            status.setMessage(printstat).setStatus(ReportStatus.Status.PRINTED);
        } else {
            tempFile = ReportUtil.saveForUpload(tempFile);
            status.setMessage(tempFile.getName())
                  .setPath(ReportUtil.getSystemVariableValue("upload_stream_directory"))
                  .setStatus(ReportStatus.Status.SAVED);
        }
        return status;
    }

    private void calculateStatistics(TurnAroundReportViewVO data) throws Exception {
        int count, min, max;
        Integer statValue;
        Stat stat;

        for (Value val : data.getValues()) {
            if ("N".equals(val.getIsPlot()))
                continue;
            /*
             * reset all fields in stat for all types to zero to make sure that
             * it accurately reflects the number of analyses to be plotted which
             * might have changed from the previous time.
             */
            for (StatisticType statType : StatisticType.values()) {
                if (val.hasStats(statType)) {
                    stat = val.getStats(statType);
                    stat.setNumTested(0);
                    stat.setSum(0);
                    stat.setMax(0);
                    stat.setMin(99999);
                    stat.setAvg(0);
                    stat.setSd(0);
                    stat.setSqDiffSum(0);
                }
            }

            for (PlotValue plotValue : val.getPlotValues()) {
                for (StatisticType type : StatisticType.values()) {
                   
                    statValue = plotValue.getStatAt(type);
                    if (statValue == null)
                        continue;
                    /*
                     * If there is a record for a particular statistic type, we
                     * get the record and update the statistics.
                     */
                    if (val.hasStats(type)) {
                        stat = val.getStats(type);
                        count = stat.getNumTested();
                        /*
                         * Only analyses with isPlot set to "Y" should be considered in the calculation of statistics                      * not be considered in the number tested
                         */
                        if ("Y".equals(plotValue.getIsPlot())) {
                            stat.setNumTested( ++count);
                            stat.setSum(statValue + stat.getSum());
                            min = stat.getMin();
                            stat.setMin(statValue < min ? statValue : min);
                            max = stat.getMax();
                            stat.setMax(statValue > max ? statValue : max);
                        }
                    } else if ("Y".equals(plotValue.getIsPlot())) {
                        /*
                         * Only analyses with isPlot set to "Y" should be considered in the calculation of statistics                      * not be considered in the number tested
                         */
                        stat = val.getStats(type);
                        stat.setMin(statValue);
                        stat.setMax(statValue);
                        stat.setNumTested(1);
                        stat.setSum(statValue);
                        stat.setSqDiffSum(0);
                    }
                }
            }
        }

        /*
         * calculate the sum of square of difference between the stat value and
         * the mean for each statistic type for the purpose of calculating SD.
         */
        for (Value val : data.getValues()) {
            if ("N".equals(val.getIsPlot()))
                continue;
            for (PlotValue plotValue : val.getPlotValues()) {
                if ("N".equals(plotValue.getIsPlot()))
                    continue;
                for (StatisticType statType : StatisticType.values()) {
                    statValue = plotValue.getStatAt(statType);
                    if (statValue != null) {
                        if (val.hasStats(statType)) {
                            stat = val.getStats(statType);
                            if (stat.getNumTested() > 0) {
                                stat.setAvg(stat.getSum() / stat.getNumTested());
                                stat.setSqDiffSum(stat.getSqDiffSum() +
                                                  (statValue - stat.getAvg()) *
                                                  (statValue - stat.getAvg()));
                            }
                        }
                    }
                }
            }
            /*
             * set SD to the square root of the sum of square of difference
             * divided by count - 1.
             */
            for (PlotValue plotValue : val.getPlotValues()) {
                if ("N".equals(plotValue.getIsPlot()))
                    continue;
                for (StatisticType statType : StatisticType.values()) {
                    statValue = plotValue.getStatAt(statType);
                    if (statValue != null) {
                        if (val.hasStats(statType)) {
                            stat = val.getStats(statType);
                            if (stat.getNumTested() > 1)
                                stat.setSd((int)Math.sqrt(stat.getSqDiffSum() /
                                                                      (stat.getNumTested() - 1)));
                        }
                    }
                }
            }
        }
    }

    class MyComparator implements Comparator<Value> {
        public int compare(Value v1, Value v2) {
            return (v1.getTest() + v1.getMethod()).compareTo(v2.getTest() + v2.getMethod());
        }
    }

}