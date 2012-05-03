package org.openelis.report.qcchart;

import net.sf.jasperreports.engine.JRAbstractChartCustomizer;
import net.sf.jasperreports.engine.JRChart;

import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.util.ShapeUtilities;

public class QCChartCustomizer extends JRAbstractChartCustomizer {

    public void customize(JFreeChart chart, JRChart jrChart) {
        NumberAxis n;
        XYPlot plot;
        
        plot = (XYPlot) chart.getPlot();
        /* We assume that the data points are in the first plot series, hence we are starting from 1. 
         * We remove the shapes for all other series (UCL, LCL, UWL, LWL) from
         * the jasper report interface.
         */
        for (int i = 1; i < plot.getSeriesCount(); i++) 
            plot.getRenderer().setSeriesShape(i, ShapeUtilities.createDiamond(0));

        n = (NumberAxis) plot.getDomainAxis();
        n.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
    }

}
