package org.openelis.report.turnaroundstatistic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRField;

import org.openelis.domain.TurnAroundReportViewVO;
import org.openelis.domain.TurnAroundReportViewVO.StatisticType;
import org.openelis.domain.TurnAroundReportViewVO.Value;

public class TurnaroundDataSource implements JRDataSource {

    protected ArrayList<StatisticType>                types;
    protected ArrayList<TurnAroundReportViewVO.Value> values;
    protected Iterator<TurnAroundReportViewVO.Value>  iterator;
    protected TurnAroundReportViewVO.Value            value;
    protected HashMap<String, ArrayList<Value>>       map;

    public ArrayList<TurnAroundReportViewVO.Value> getValues() {
        return values;
    }

    public void setValues(ArrayList<TurnAroundReportViewVO.Value> values) {
        this.values = values;
    }

    public ArrayList<StatisticType> getTypes() {
        return types;
    }

    public void setTypes(ArrayList<StatisticType> types) {
        this.types = types;
    }
    
    public HashMap<String, ArrayList<Value>> getMap() {
        return map;
    }

    public void setMap(HashMap<String, ArrayList<Value>> map) {
        this.map = map;
    }

    public boolean next() throws JRException {

        if (iterator == null && values != null)
            iterator = values.iterator();

        while (iterator.hasNext()) {
            value = (TurnAroundReportViewVO.Value)iterator.next();
            if ("Y".equals(value.getIsPlot()))
                return true;
        }
        return false;
    }

    public Object getFieldValue(JRField field) throws JRException {

        if ("PLOT_DATE".equals(field.getName())) {
            return value.getPlotDate().toString();
        } else if ("TEST".equals(field.getName())) {
            return value.getTest();
        } else if ("METHOD".equals(field.getName())) {
            return value.getMethod();
        } else if ("STAT_1".equals(field.getName())) {
            if (value.hasStats(types.get(0)))
                return value.getStats(types.get(0));
        } else if ("STAT_2".equals(field.getName())) {            
            if (types.size() > 1 && value.hasStats(types.get(1)))
                return value.getStats(types.get(1));
        } else if ("STAT_3".equals(field.getName())) {
            if (types.size() > 2 && value.hasStats(types.get(2)))
                return value.getStats(types.get(2));
        } else if ("STAT_4".equals(field.getName())) {
            if (types.size() > 3 && value.hasStats(types.get(3)))
                return value.getStats(types.get(3));
        } else if ("STAT_5".equals(field.getName())) {
            if (types.size() > 4 && value.hasStats(types.get(4)))
                return value.getStats(types.get(4));
        }
        return null;
    }
}
