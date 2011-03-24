package org.openelis.utils;

import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.axis.LogarithmicAxis;

import net.sf.jasperreports.engine.JRAbstractChartCustomizer;
import net.sf.jasperreports.engine.JRChart;

public class QAChartCustomizerLogYAxis extends JRAbstractChartCustomizer {

    public void customize(JFreeChart chart, JRChart jrChart) {
        LogarithmicAxis logScale;
        ValueAxis v;

        logScale = new LogarithmicAxis("no. of events");
        logScale.setAllowNegativesFlag(false);
        logScale.setAutoRangeNextLogFlag(true);
        logScale.setStrictValuesFlag(false);
        logScale.setAutoTickUnitSelection(false);
        logScale.autoAdjustRange();

        v = logScale;
        chart.getCategoryPlot().setRangeAxis(v);
    }

}
