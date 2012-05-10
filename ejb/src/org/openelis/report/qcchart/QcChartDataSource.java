package org.openelis.report.qcchart;

import java.util.*;

import org.openelis.domain.QcChartReportViewVO;

import net.sf.jasperreports.engine.*;

public class QcChartDataSource implements JRDataSource {

    protected Integer                              wId, number;
    protected Double                               plotValue, mean, ucl, lcl, uwl, lwl, sd;
    protected String                               accessionNumber, lotNumber, value1, value2, analyteName;
    protected Date                                 createdDate;
    protected ArrayList<QcChartReportViewVO.Value> values;
    protected Iterator<QcChartReportViewVO.Value>  iter;
    protected QcChartReportViewVO.Value            value;

    public ArrayList<QcChartReportViewVO.Value> getValues() {
        return values;
    }

    public void setValues(ArrayList<QcChartReportViewVO.Value> values) {
        this.values = values;
    }

    public boolean next() throws JRException {

        if (iter == null && values != null)
            iter = values.iterator();

        if (iter.hasNext()) {
            value = (QcChartReportViewVO.Value)iter.next();
            /*
             * Those values not selected to be plotted by the user (IsPlot =
             * "N") are not shown in the qc chart. The following code makes sure
             * that those values are skipped until the next value to be plotted
             * is found.
             */
            if ("N".equals(value.getIsPlot())) {
                while (iter.hasNext() && "N".equals(value.getIsPlot()))
                    value = (QcChartReportViewVO.Value)iter.next();
            }
            /*
             * The previous loop can reach the end of the list and if the last
             * element is one with isPlot = Y it needs to be plotted, which
             * won't happen if this method returns false. So in order to plot
             * this value we return value.getIsPlot(). Otherwise if the last
             * element is one with isPlot = N, then False is returned as usual.
             */
            if ( !iter.hasNext())
                return "Y".equals(value.getIsPlot());
        } else {
            return false;
        }
        return true;
    }

    public Object getFieldValue(JRField field) throws JRException {
        if ("ACCESSION_NUMBER".equals(field.getName()))
            return value.getAccessionNumber();
        else if ("LOT_NUMBER".equals(field.getName()))
            return value.getLotNumber();
        else if ("WORKSHEET_NUMBER".equals(field.getName()))
            return value.getWId();
        else if ("VALUE_1".equals(field.getName()))
            return value.getValue1();
        else if ("VALUE_2".equals(field.getName()))
            return value.getValue2();
        else if ("PLOT_VALUE".equals(field.getName()))
            return value.getPlotValue();
        else if ("MEAN".equals(field.getName()))
            return value.getMean();
        else if ("UCL".equals(field.getName()))
            return value.getUCL();
        else if ("LCL".equals(field.getName()))
            return value.getLCL();
        else if ("UWL".equals(field.getName()))
            return value.getUWL();
        else if ("LWL".equals(field.getName()))
            return value.getLWL();
        else if ("SD".equals(field.getName()))
            return value.getSd();
        else if ("CREATED_DATE".equals(field.getName()))
            return value.getWorksheetCreatedDate().getDate();
        else if ("ANALYTE_NAME".equals(field.getName()))
            return value.getAnalyteName();
        return null;
    }
}
