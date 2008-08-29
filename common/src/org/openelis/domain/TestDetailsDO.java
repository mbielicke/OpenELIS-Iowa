/**
* The contents of this file are subject to the Mozilla Public License
* Version 1.1 (the "License"); you may not use this file except in
* compliance with the License. You may obtain a copy of the License at
* http://www.mozilla.org/MPL/
* 
* Software distributed under the License is distributed on an "AS IS"
* basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
* License for the specific language governing rights and limitations under
* the License.
* 
* The Original Code is OpenELIS code.
* 
* Copyright (C) The University of Iowa.  All Rights Reserved.
*/
package org.openelis.domain;

import java.io.Serializable;
import java.util.Date;

import org.openelis.util.Datetime;



public class TestDetailsDO implements Serializable{

    /**
     * 
     */
    private static final long serialVersionUID = -4733716943094549441L;
    
    protected String description;             

    protected String reportingDescription;   
    
    protected String isActive;             

    protected Datetime activeBegin;             

    protected Datetime activeEnd;             

    protected String isReportable;             

    protected Integer timeTransit;             

    protected Integer timeHolding;             
    
    protected Integer timeTaAverage;             

    protected Integer timeTaWarning;             

    protected Integer timeTaMax;             

    protected Integer labelId;             

    protected Integer labelQty;             
    
    protected Integer testTrailerId;             

    protected Integer sectionId;             

    protected Integer scriptletId;             

    protected Integer testFormatId;             

    protected Integer revisionMethodId;

    public TestDetailsDO(){
        
    }
    
    public TestDetailsDO(String description, String reportingDescription,                            
                         String isActive,Date activeBegin,Date activeEnd,             
                         String isReportable, Integer timeTransit,
                         Integer timeHolding, Integer timeTaAverage,             
                         Integer timeTaWarning, Integer timeTaMax,             
                         Integer labelId, Integer labelQty,Integer testTrailerId,                         
                         Integer sectionId, Integer scriptletId,
                         Integer testFormatId, Integer revisionMethodId){
         
        this.description = description;
        this.reportingDescription = reportingDescription;
        this.isActive = isActive;
        setActiveBegin(activeBegin);
        setActiveEnd(activeEnd);
        this.isReportable = isReportable;
        this.timeTransit = timeTransit;
        this.timeHolding = timeHolding;
        this.timeTaAverage = timeTaAverage;
        this.timeTaWarning = timeTaWarning;
        this.timeTaMax = timeTaMax;
        this.labelId = labelId;
        this.labelQty = labelQty;
        this.testTrailerId = testTrailerId;
        this.sectionId = sectionId;
        this.scriptletId = scriptletId;
        this.testFormatId = testFormatId;        
        this.revisionMethodId = revisionMethodId;
        
    }
    
    public Datetime getActiveBegin() {
        return activeBegin;
    }

    public void setActiveBegin(Date activeBegin) {
        //this.activeBegin = activeBegin;
        this.activeBegin = new Datetime(Datetime.YEAR,Datetime.DAY,activeBegin);
    }

    public Datetime getActiveEnd() {
        return activeEnd;
    }

    public void setActiveEnd(Date activeEnd) {
        this.activeEnd = new Datetime(Datetime.YEAR,Datetime.DAY,activeEnd);
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getIsActive() {
        return isActive;
    }

    public void setIsActive(String isActive) {
        this.isActive = isActive;
    }

    public String getIsReportable() {
        return isReportable;
    }

    public void setIsReportable(String isReportable) {
        this.isReportable = isReportable;
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
        this.reportingDescription = reportingDescription;
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

    public Integer getSectionId() {
        return sectionId;
    }

    public void setSectionId(Integer sectionId) {
        this.sectionId = sectionId;
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


}
