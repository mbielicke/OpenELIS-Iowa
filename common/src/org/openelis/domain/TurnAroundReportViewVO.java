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
package org.openelis.domain;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.openelis.gwt.common.DataBaseUtil;
import org.openelis.gwt.common.Datetime;
import org.openelis.gwt.common.RPC;

public class TurnAroundReportViewVO implements RPC {
    private static final long  serialVersionUID = 1L;

    public enum StatisticType {
        COL_REC ("Col-Rec"), COL_RDY ("Col-Rdy"), COL_REL ("Col-Rel"), 
        REC_RDY ("Rec-Rdy"), REC_COM ("Rec-Com"), REC_REL ("Rec-Rel"), 
        INI_COM ("Ini-Com"), INI_REL ("Ini-Rel"), COM_REL ("Com-Rel");
        
        private final String label;
        
        StatisticType(String label){
            this.label = label;
        }
        
        public String getLabel() {
            return label;
        }
    };
    protected Datetime fromDate, toDate;
    protected ArrayList<StatisticType> types;
    protected Integer intervalId;
    protected String printer;
    protected ArrayList<Value> values;

    public TurnAroundReportViewVO() {
    }
    
    public Datetime getFromDate() {
        return fromDate;
    }

    public void setFromDate(Date fromDate) {
        this.fromDate = DataBaseUtil.toYM(fromDate);
    }
    
    public Datetime getToDate() {
        return toDate;
    }

    public void setToDate(Date toDate) {
        this.toDate = DataBaseUtil.toYM(toDate);
    }

    public ArrayList<StatisticType> getTypes() {
        return types;
    }

    public void setTypes(ArrayList<StatisticType> types) {
        this.types = types;
    }
    
    public Integer getIntervalId() {
        return intervalId;
    }

    public void setIntervalId(Integer intervalId) {
        this.intervalId = intervalId;
    }
    
    public String getPrinter() {
        return printer;
    }

    public void setPrinter(String printer) {
        this.printer = printer;
    }
    
    public ArrayList<Value> getValues() {
        return values;
    }

    public void setValues(ArrayList<Value> values) {
        this.values = values;
    }

    public static class Value implements RPC {
        private static final long serialVersionUID = 1L;

        protected List<PlotValue> plotValues;
        protected String          test, method, isPlot;
        protected Datetime        plotDate;
        protected StatisticType   statisticType;
        protected Stat            stats[];

        public Value() {
            stats = new Stat[StatisticType.values().length];
        }
        
        public List<PlotValue> getPlotValues() {
            return plotValues;
        }

        public void setPlotValues(List<PlotValue> plotValues) {
            this.plotValues = plotValues;
        }

        public String getTest() {
            return test;
        }

        public void setTest(String test) {
            this.test = test;
        }

        public String getMethod() {
            return method;
        }

        public void setMethod(String method) {
            this.method = method;
        }

        public String getIsPlot() {
            return isPlot;
        }

        public void setIsPlot(String isPlot) {
            this.isPlot = isPlot;
        }

        public Datetime getPlotDate() {
            return plotDate;
        }

        public void setPlotDate(Date plotDate) {
            this.plotDate = DataBaseUtil.toYM(plotDate);
        }

        public StatisticType getStatisticType() {
            return statisticType;
        }

        public void setStatisticType(StatisticType statisticType) {
            this.statisticType = statisticType;
        }
        
        public boolean hasStats(StatisticType type) {
            return (stats[type.ordinal()] != null);
        }

        public Stat getStats(StatisticType type) {
            if (stats[type.ordinal()] == null)
                stats[type.ordinal()] = new Stat();
            return stats[type.ordinal()];
        }

        public static class Stat implements RPC {
            private static final long serialVersionUID = 1L;

            protected int             numTested, min, max, sum;
            protected float           sqDiffSum;
            protected Integer         avg, sd;
            
            public Stat() {
            }

            public int getNumTested() {
                return numTested;
            }

            public void setNumTested(int numTested) {
                this.numTested = numTested;
            }

            public int getMin() {
                return min;
            }

            public void setMin(int min) {
                this.min = min;
            }

            public int getMax() {
                return max;
            }

            public void setMax(int max) {
                this.max = max;
            }
            
            public int getSum() {
                return sum;
            }

            public void setSum(int sum) {
                this.sum = sum;
            }

            public Integer getAvg() {
                return avg;
            }

            public void setAvg(Integer avg) {
                this.avg = avg;
            }

            public float getSqDiffSum() {
                return sqDiffSum;
            }

            public void setSqDiffSum(float sqDiffSum) {
                this.sqDiffSum = sqDiffSum;
            }

            public Integer getSd() {
                return sd;
            }

            public void setSd(Integer sd) {
                this.sd = sd;
            }
        }
    }

    public static class PlotValue implements RPC {
        private static final long serialVersionUID = 1L;

        protected Integer         accessionNumber, revision;
        protected Integer             stats[];
        protected String          isPlot;

        public PlotValue() {
            stats = new Integer[StatisticType.values().length];
        }

        public Integer getAccessionNumber() {
            return accessionNumber;
        }

        public void setAccessionNumber(Integer accessionNumber) {
            this.accessionNumber = accessionNumber;
        }

        public Integer getRevision() {
            return revision;
        }

        public void setRevision(Integer revision) {
            this.revision = revision;
        }

        public String getIsPlot() {
            return isPlot;
        }

        public void setIsPlot(String isPlot) {
            this.isPlot = isPlot;
        }      
      
        public void setStatAt(StatisticType stat, Integer value) {
            this.stats[stat.ordinal()] = value;
        }

        public Integer getStatAt(StatisticType stat) {
            return stats[stat.ordinal()];
        }
    }
}
