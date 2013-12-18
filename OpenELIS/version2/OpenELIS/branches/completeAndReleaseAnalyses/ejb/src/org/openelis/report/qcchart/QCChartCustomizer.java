package org.openelis.report.qcchart;

import java.awt.Color;

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
        plot.getRenderer().setSeriesShape(0, ShapeUtilities.createDiamond(2));
        
        for (int i = 1; i < plot.getSeriesCount(); i++) 
            plot.getRenderer().setSeriesShape(i, ShapeUtilities.createDiamond(0));

        plot.getRenderer().setSeriesPaint(0, Color.RED);
        plot.getRenderer().setSeriesPaint(1, Color.BLUE);
        plot.getRenderer().setSeriesPaint(2, Color.GREEN);
        plot.getRenderer().setSeriesPaint(3, Color.ORANGE);
        plot.getRenderer().setSeriesPaint(4, Color.MAGENTA);
        plot.getRenderer().setSeriesPaint(5, Color.CYAN);
        
        n = (NumberAxis) plot.getDomainAxis();
        n.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
    }

}
