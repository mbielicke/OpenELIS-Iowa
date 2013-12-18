package org.openelis.report.turnaroundstatistic;

import java.awt.Color;
import java.awt.Font;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;

import net.sf.jasperreports.engine.JRAbstractChartCustomizer;
import net.sf.jasperreports.engine.JRChart;

import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.util.ShapeUtilities;
import org.openelis.domain.TurnAroundReportViewVO.StatisticType;
import org.openelis.domain.TurnAroundReportViewVO.Value;
import org.openelis.utils.JasperUtil;

public class TurnaroundCustomizer extends JRAbstractChartCustomizer {
    private TurnaroundDataSource ds;
    private DateFormat           formatter;

    public void customize(JFreeChart chart, JRChart jrChart) {
        DefaultCategoryDataset dataset;
        HashMap<String, ArrayList<Value>> map;
        ArrayList<Value> valueList;
        String testMethodName;
        CategoryPlot plot;

        plot = (CategoryPlot)chart.getPlot();

        dataset = new DefaultCategoryDataset();
        plot.getDomainAxis().setTickLabelFont(new Font("Times New Roman", Font.PLAIN, 6));
        plot.getRangeAxis().setTickLabelFont(new Font("Times New Roman", Font.PLAIN, 6));

        for (int i = 0; i < 9; i++ )
            plot.getRenderer().setSeriesShape(i, ShapeUtilities.createDiamond(2));

        plot.getRenderer().setSeriesPaint(0, Color.RED);
        plot.getRenderer().setSeriesPaint(1, Color.BLUE);
        plot.getRenderer().setSeriesPaint(2, Color.GREEN);
        plot.getRenderer().setSeriesPaint(3, Color.MAGENTA);
        plot.getRenderer().setSeriesPaint(4, Color.CYAN);
        plot.getRenderer().setSeriesPaint(5, Color.ORANGE);
        plot.getRenderer().setSeriesPaint(6, Color.DARK_GRAY);
        plot.getRenderer().setSeriesPaint(7, Color.PINK);
        plot.getRenderer().setSeriesPaint(8, Color.BLACK);

        if (formatter == null)
            formatter = new SimpleDateFormat("yyyy-MM-dd");
        
        if (ds == null)
            ds = (TurnaroundDataSource)getParameterValue("TURNAROUND_DATASOURCE");
        testMethodName = JasperUtil.concatWithSeparator((String)getFieldValue("TEST"),
                                                        ", ",
                                                        (String)getFieldValue("METHOD"));

        /*
         * Create the chart dataset based on the types the user have selected.
         * For example, if the user has selected two statistic types draw a
         * series for each of the type selected.
         */
        map = ds.getMap();
        valueList = map.get(testMethodName);
        for (Value value : valueList) {
            if (value == null || "N".equals(value.getIsPlot()))
                continue;
            for (StatisticType type : ds.getTypes()) {
                if (value.getStats(type).getAvg() != null)
                    dataset.addValue(JasperUtil.daysAndHours(value.getStats(type).getAvg()),
                                     type.getLabel(),
                                     formatter.format(value.getPlotDate().getDate()));
            }
        }
        chart.getCategoryPlot().setDataset(dataset);
    }
}
