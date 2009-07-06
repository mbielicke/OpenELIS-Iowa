/** Exhibit A - UIRF Open-source Based Public Software License.
* 
* The contents of this file are subject to the UIRF Open-source Based
* Public Software License(the "License"); you may not use this file except
* in compliance with the License. You may obtain a copy of the License at
* openelis.uhl.uiowa.edu
* 
* Software distributed under the License is distributed on an "AS IS"
* basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
* License for the specific language governing rights and limitations
* under the License.
* 
* The Original Code is OpenELIS code.
* 
* The Initial Developer of the Original Code is The University of Iowa.
* Portions created by The University of Iowa are Copyright 2006-2008. All
* Rights Reserved.
* 
* Contributor(s): ______________________________________.
* 
* Alternatively, the contents of this file marked
* "Separately-Licensed" may be used under the terms of a UIRF Software
* license ("UIRF Software License"), in which case the provisions of a
* UIRF Software License are applicable instead of those above. 
*/
package org.openelis.domain;

import java.io.Serializable;
import java.util.Date;

import org.openelis.util.Datetime;
import org.openelis.utilcommon.DataBaseUtil;

public class TestDO implements Serializable {

    private static final long serialVersionUID = 1L;

    protected Integer         id;
    protected String          name;
    protected Integer         methodId;
    protected String          methodName;
    protected String          description;
    protected String          reportingDescription;
    protected String          isActive;
    protected Datetime        activeBegin;
    protected Datetime        activeEnd;
    protected String          isReportable;
    protected Integer         timeTransit;
    protected Integer         timeHolding;
    protected Integer         timeTaAverage;
    protected Integer         timeTaWarning;
    protected Integer         timeTaMax;
    protected Integer         labelId;
    protected String          labelName;
    protected Integer         labelQty;
    protected Integer         testTrailerId;
    protected String          testTrailerName;
    protected Integer         scriptletId;
    protected String          scriptletName;
    protected Integer         testFormatId;
    protected Integer         revisionMethodId;
    protected Integer         reportingMethodId;
    protected Integer         sortingMethodId;
    protected Integer         reportingSequence;

    private Boolean           delete           = false;

    public TestDO() {

    }

    public TestDO(Integer id, String name, Integer methodId, String methodName,
                  String description, String reportingDescription,
                  String isActive, Date activeBegin, Date activeEnd, 
                  String isReportable, Integer timeTransit, Integer timeHolding,
                  Integer timeTaAverage, Integer timeTaWarning, Integer timeTaMax,
                  Integer labelId,String labelName, Integer labelQty, 
                  Integer testTrailerId,String testTrailerName,
                  Integer scriptletId,String scriptletName,Integer testFormatId,
                  Integer revisionMethodId,Integer reportingMethodId,
                  Integer sortingMethodId,Integer reportingSequence) {
        setId(id);
        setName(name);
        setMethodId(methodId);
        setMethodName(methodName);
        setDescription(description);
        setReportingDescription(reportingDescription);
        setIsActive(isActive);
        setActiveBegin(activeBegin);
        setActiveEnd(activeEnd);
        setIsReportable(isReportable);
        setTimeTransit(timeTransit);
        setTimeHolding(timeHolding);
        setTimeTaAverage(timeTaAverage);
        setTimeTaWarning(timeTaWarning);
        setTimeTaMax(timeTaMax);
        setLabelId(labelId);
        setLabelName(labelName);
        setLabelQty(labelQty);
        setTestTrailerId(testTrailerId);
        setTestTrailerName(testTrailerName);
        setScriptletId(scriptletId);
        setScriptletName(scriptletName);
        setTestFormatId(testFormatId);
        setRevisionMethodId(revisionMethodId);
        setReportingMethodId(reportingMethodId);
        setSortingMethodId(sortingMethodId);
        setReportingSequence(reportingSequence);
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getMethodId() {
        return methodId;
    }

    public void setMethodId(Integer methodId) {
        this.methodId = methodId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = DataBaseUtil.trim(name);
    }

    public Boolean getDelete() {
        return delete;
    }

    public void setDelete(Boolean delete) {
        this.delete = delete;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = DataBaseUtil.trim(methodName);
    }

    public Datetime getActiveBegin() {
        return activeBegin;
    }

    public void setActiveBegin(Date activeBegin) {
        this.activeBegin = new Datetime(Datetime.YEAR,
                                        Datetime.DAY,
                                        activeBegin);
    }

    public Datetime getActiveEnd() {
        return activeEnd;
    }

    public void setActiveEnd(Date activeEnd) {
        this.activeEnd = new Datetime(Datetime.YEAR, Datetime.DAY, activeEnd);
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = DataBaseUtil.trim(description);
    }

    public String getIsActive() {
        return isActive;
    }

    public void setIsActive(String isActive) {
        this.isActive = DataBaseUtil.trim(isActive);
    }

    public String getIsReportable() {
        return isReportable;
    }

    public void setIsReportable(String isReportable) {
        this.isReportable = DataBaseUtil.trim(isReportable);
    }

    public Integer getLabelId() {
        return labelId;
    }

    public void setLabelId(Integer labelId) {
        this.labelId = labelId;
    }

    public Integer getLabelQty() {
        return labelQty;
    }

    public void setLabelQty(Integer labelQty) {
        this.labelQty = labelQty;
    }

    public String getReportingDescription() {
        return reportingDescription;
    }

    public void setReportingDescription(String reportingDescription) {
        this.reportingDescription = DataBaseUtil.trim(reportingDescription);
    }

    public Integer getRevisionMethodId() {
        return revisionMethodId;
    }

    public void setRevisionMethodId(Integer revisionMethodId) {
        this.revisionMethodId = revisionMethodId;
    }

    public Integer getScriptletId() {
        return scriptletId;
    }

    public void setScriptletId(Integer scriptletId) {
        this.scriptletId = scriptletId;
    }

    public Integer getTestFormatId() {
        return testFormatId;
    }

    public void setTestFormatId(Integer testFormatId) {
        this.testFormatId = testFormatId;
    }

    public Integer getTestTrailerId() {
        return testTrailerId;
    }

    public void setTestTrailerId(Integer testTrailerId) {
        this.testTrailerId = testTrailerId;
    }

    public Integer getTimeHolding() {
        return timeHolding;
    }

    public void setTimeHolding(Integer timeHolding) {
        this.timeHolding = timeHolding;
    }

    public Integer getTimeTaAverage() {
        return timeTaAverage;
    }

    public void setTimeTaAverage(Integer timeTaAverage) {
        this.timeTaAverage = timeTaAverage;
    }

    public Integer getTimeTaMax() {
        return timeTaMax;
    }

    public void setTimeTaMax(Integer timeTaMax) {
        this.timeTaMax = timeTaMax;
    }

    public Integer getTimeTaWarning() {
        return timeTaWarning;
    }

    public void setTimeTaWarning(Integer timeTaWarning) {
        this.timeTaWarning = timeTaWarning;
    }

    public Integer getTimeTransit() {
        return timeTransit;
    }

    public void setTimeTransit(Integer timeTransit) {
        this.timeTransit = timeTransit;
    }

    public Integer getReportingMethodId() {
        return reportingMethodId;
    }

    public void setReportingMethodId(Integer reportingMethodId) {
        this.reportingMethodId = reportingMethodId;
    }

    public Integer getReportingSequence() {
        return reportingSequence;
    }

    public void setReportingSequence(Integer reportingSequence) {
        this.reportingSequence = reportingSequence;
    }

    public Integer getSortingMethodId() {
        return sortingMethodId;
    }

    public void setSortingMethodId(Integer sortingMethodId) {
        this.sortingMethodId = sortingMethodId;
    }

    public String getScriptletName() {
        return scriptletName;
    }

    public void setScriptletName(String scriptletName) {
        this.scriptletName = DataBaseUtil.trim(scriptletName);
    }

    public String getTestTrailerName() {
        return testTrailerName;
    }

    public void setTestTrailerName(String testTrailerName) {
        this.testTrailerName = DataBaseUtil.trim(testTrailerName);
    }

    public String getLabelName() {
        return labelName;
    }

    public void setLabelName(String labelName) {
        this.labelName = DataBaseUtil.trim(labelName);
    }

}
