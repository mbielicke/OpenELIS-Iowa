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

import org.openelis.gwt.common.DataBaseUtil;
import org.openelis.gwt.common.Datetime;
import org.openelis.gwt.common.RPC;

public class QcChartReportViewVO implements RPC {
    private static final long serialVersionUID = 1L;

    public enum ReportType {SPIKE_CONC, SPIKE_PERCENT};
    
    protected ReportType reportType;
    protected Integer plotType, qcType;
    protected String qcName;
    
    protected ArrayList<Value> qcList;
  
    public Integer getPlotType() {
        return plotType;
    }
    public void setPlotType(Integer plotType) {
        this.plotType = plotType;
    }
    
    public Integer getQcType() {
        return qcType;
    }
    public void setQcType(Integer qcType) {
        this.qcType = qcType;
    }
    
    public String getQcName() {
        return qcName;
    }
    public void setQcName(String qcName) {
        this.qcName = qcName;
    }
    public ReportType getReportType() {
        return reportType;
    }
    public void setReportType(ReportType reportType) {
        this.reportType = reportType;
    }
    
    public ArrayList<Value> getQcList() {
        return qcList;
    }
    public void setQcList(ArrayList<Value> qcList) {
        this.qcList = qcList;
    }
    
    public static class Value implements RPC {
        private static final long serialVersionUID = 1L;
        
        protected Integer         qcId, wId, analyteId;
        protected String          accessionNumber, lotNumber, analyteName, value1, value2, isPlot;
        protected Double          plotValue, mean, meanRecovery, uWL, uCL, lWL, lCL, sd;
        protected Datetime        worksheetCreatedDate;
        
        
        public Integer getQcId() {
            return qcId;
        }

        public void setQcId(Integer qcId) {
            this.qcId = qcId;
        }
        
        public Integer getWId() {
            return wId;
        }

        public void setWId(Integer wId) {
            this.wId = wId;
        }

        public Integer getAnalyteId() {
            return analyteId;
        }

        public void setAnalyteId(Integer analyteId) {
            this.analyteId = analyteId;
        }
        
        public String getAccessionNumber() {
            return accessionNumber;
        }

        public void setAccessionNumber(String accessionNumber) {
            this.accessionNumber = DataBaseUtil.trim(accessionNumber);
        }

        public String getLotNumber() {
            return lotNumber;
        }

        public void setLotNumber(String lotNumber) {
            this.lotNumber = DataBaseUtil.trim(lotNumber);
        }

        public String getAnalyteName() {
            return analyteName;
        }

        public void setAnalyteName(String analyteName) {
            this.analyteName = DataBaseUtil.trim(analyteName);
        }
        
        public String getIsPlot() {
            return isPlot;
        }

        public void setIsPlot(String isPlot) {
            this.isPlot = DataBaseUtil.trim(isPlot);
        }
        
        public String getValue1() {
            return value1;
        }

        public void setValue1(String value1) {
            this.value1 = value1;
        }

        public String getValue2() {
            return value2;
        }

        public void setValue2(String value2) {
            this.value2 = value2;
        }

        public Double getPlotValue() {
            return plotValue;
        }

        public void setPlotValue(Double plotValue) {
            this.plotValue = plotValue;
        }

        public Double getMean() {
            return mean;
        }

        public void setMean(Double mean) {
            this.mean = mean;
        }

        public Double getMeanRecovery() {
            return meanRecovery;
        }

        public void setMeanRecovery(Double meanRecovery) {
            this.meanRecovery = meanRecovery;
        }

        public Double getUWL() {
            return uWL;
        }

        public void setUWL(Double uWL) {
            this.uWL = uWL;
        }

        public Double getUCL() {
            return uCL;
        }

        public void setUCL(Double uCL) {
            this.uCL = uCL;
        }

        public Double getLWL() {
            return lWL;
        }

        public void setLWL(Double lWL) {
            this.lWL = lWL;
        }

        public Double getLCL() {
            return lCL;
        }

        public void setLCL(Double lCL) {
            this.lCL = lCL;
        }
        
        public Double getSd() {
            return sd;
        }

        public void setSd(Double sd) {
            this.sd = sd;
        }

        public Datetime getWorksheetCreatedDate() {
            return worksheetCreatedDate;
        }

        public void setWorksheetCreatedDate(Datetime worksheetCreatedDate) {
            this.worksheetCreatedDate = DataBaseUtil.toYM(worksheetCreatedDate);
        }
    }
}
