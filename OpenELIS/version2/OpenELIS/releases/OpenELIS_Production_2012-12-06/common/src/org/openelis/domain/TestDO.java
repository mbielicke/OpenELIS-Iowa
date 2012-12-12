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

import java.util.Date;

import org.openelis.gwt.common.DataBaseUtil;
import org.openelis.gwt.common.Datetime;

/**
 * Class represents the fields in database table test.
 */

public class TestDO extends DataObject {

    private static final long serialVersionUID = 1L;

    protected Integer         id, methodId, timeTransit, timeHolding, timeTaAverage, timeTaWarning,
                              timeTaMax, labelId, labelQty, testTrailerId, scriptletId, testFormatId,
                              revisionMethodId, reportingMethodId, sortingMethodId, reportingSequence;
    protected String          name, description, reportingDescription, isActive, isReportable;
    protected Datetime        activeBegin, activeEnd, labelName;

    public TestDO() {
    }

    public TestDO(Integer id, String name, String description, String reportingDescription,
                  Integer methodId, String isActive, Date activeBegin, Date activeEnd,
                  String isReportable, Integer timeTransit, Integer timeHolding,
                  Integer timeTaAverage, Integer timeTaWarning, Integer timeTaMax, Integer labelId,
                  Integer labelQty, Integer testTrailerId, Integer scriptletId,
                  Integer testFormatId, Integer revisionMethodId, Integer reportingMethodId,
                  Integer sortingMethodId, Integer reportingSequence) {
        setId(id);
        setName(name);
        setDescription(description);
        setReportingDescription(reportingDescription);
        setMethodId(methodId);
        setIsActive(isActive);
        setActiveBegin(DataBaseUtil.toYD(activeBegin));
        setActiveEnd(DataBaseUtil.toYD(activeEnd));
        setIsReportable(isReportable);
        setTimeTransit(timeTransit);
        setTimeHolding(timeHolding);
        setTimeTaAverage(timeTaAverage);
        setTimeTaWarning(timeTaWarning);
        setTimeTaMax(timeTaMax);
        setLabelId(labelId);
        setLabelQty(labelQty);
        setTestTrailerId(testTrailerId);
        setScriptletId(scriptletId);
        setTestFormatId(testFormatId);
        setRevisionMethodId(revisionMethodId);
        setReportingMethodId(reportingMethodId);
        setSortingMethodId(sortingMethodId);
        setReportingSequence(reportingSequence);
        _changed = false;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
        _changed = true;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = DataBaseUtil.trim(name);
        _changed = true;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = DataBaseUtil.trim(description);
        _changed = true;
    }

    public String getReportingDescription() {
        return reportingDescription;
    }

    public void setReportingDescription(String reportingDescription) {
        this.reportingDescription = DataBaseUtil.trim(reportingDescription);
        _changed = true;
    }

    public Integer getMethodId() {
        return methodId;
    }

    public void setMethodId(Integer methodId) {
        this.methodId = methodId;
        _changed = true;
    }

    public String getIsActive() {
        return isActive;
    }

    public void setIsActive(String isActive) {
        this.isActive = DataBaseUtil.trim(isActive);
        _changed = true;
    }

    public Datetime getActiveBegin() {
        return activeBegin;
    }

    public void setActiveBegin(Datetime activeBegin) {
        this.activeBegin = DataBaseUtil.toYD(activeBegin);
        _changed = true;
    }

    public Datetime getActiveEnd() {
        return activeEnd;
    }

    public void setActiveEnd(Datetime activeEnd) {
        this.activeEnd = DataBaseUtil.toYD(activeEnd);
        _changed = true;
    }

    public String getIsReportable() {
        return isReportable;
    }

    public void setIsReportable(String isReportable) {
        this.isReportable = DataBaseUtil.trim(isReportable);
        _changed = true;
    }

    public Integer getTimeTransit() {
        return timeTransit;
    }

    public void setTimeTransit(Integer timeTransit) {
        this.timeTransit = timeTransit;
        _changed = true;
    }

    public Integer getTimeHolding() {
        return timeHolding;
    }

    public void setTimeHolding(Integer timeHolding) {
        this.timeHolding = timeHolding;
        _changed = true;
    }

    public Integer getTimeTaAverage() {
        return timeTaAverage;
    }

    public void setTimeTaAverage(Integer timeTaAverage) {
        this.timeTaAverage = timeTaAverage;
        _changed = true;
    }

    public Integer getTimeTaMax() {
        return timeTaMax;
    }

    public void setTimeTaMax(Integer timeTaMax) {
        this.timeTaMax = timeTaMax;
        _changed = true;
    }

    public Integer getTimeTaWarning() {
        return timeTaWarning;
    }

    public void setTimeTaWarning(Integer timeTaWarning) {
        this.timeTaWarning = timeTaWarning;
        _changed = true;
    }

    public Integer getLabelId() {
        return labelId;
    }

    public void setLabelId(Integer labelId) {
        this.labelId = labelId;
        _changed = true;
    }

    public Integer getLabelQty() {
        return labelQty;
    }

    public void setLabelQty(Integer labelQty) {
        this.labelQty = labelQty;
        _changed = true;
    }

    public Integer getTestTrailerId() {
        return testTrailerId;
    }

    public void setTestTrailerId(Integer testTrailerId) {
        this.testTrailerId = testTrailerId;
        _changed = true;
    }

    public Integer getScriptletId() {
        return scriptletId;
    }

    public void setScriptletId(Integer scriptletId) {
        this.scriptletId = scriptletId;
        _changed = true;
    }

    public Integer getTestFormatId() {
        return testFormatId;
    }

    public void setTestFormatId(Integer testFormatId) {
        this.testFormatId = testFormatId;
        _changed = true;
    }

    public Integer getRevisionMethodId() {
        return revisionMethodId;
    }

    public void setRevisionMethodId(Integer revisionMethodId) {
        this.revisionMethodId = revisionMethodId;
        _changed = true;
    }

    public Integer getReportingMethodId() {
        return reportingMethodId;
    }

    public void setReportingMethodId(Integer reportingMethodId) {
        this.reportingMethodId = reportingMethodId;
        _changed = true;
    }

    public Integer getSortingMethodId() {
        return sortingMethodId;
    }

    public void setSortingMethodId(Integer sortingMethodId) {
        this.sortingMethodId = sortingMethodId;
        _changed = true;
    }

    public Integer getReportingSequence() {
        return reportingSequence;
    }

    public void setReportingSequence(Integer reportingSequence) {
        this.reportingSequence = reportingSequence;
        _changed = true;
    }
}