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
package org.openelis.entity;

/**
  * Analysis View Entity POJO for database 
  */

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import org.openelis.gwt.common.DataBaseUtil;
import org.openelis.gwt.common.Datetime;

@NamedQueries( {                  
    @NamedQuery( name = "AnalysisView.FetchByAnalysisStatusId",
                query = "select distinct new org.openelis.domain.AnalysisViewVO(av.sampleId, av.domain, av.accessionNumber," +
                        "av.receivedDate, av.collectionDate, av.collectionTime, av.enteredDate, av.primaryOrganizationName, av.todoDescription," +
                        "av.worksheetDescription, av.priority, av.testId, av.testName, av.methodName, av.timeTaAverage, av.timeHolding, av.analysisId," +
                        "av.analysisStatusId, av.sectionId, av.sectionName, av.availableDate, av.startedDate, av.completedDate, av.releasedDate," +
                        "av.analysisResultOverride, av.unitOfMeasureId, av.worksheetFormatId)"
                      + " from AnalysisView av where av.analysisStatusId = :statusId order by av.accessionNumber"),
    @NamedQuery( name = "AnalysisView.FetchOther",
                query = "select distinct new org.openelis.domain.AnalysisViewVO(av.sampleId, av.domain, av.accessionNumber," +
                        "av.receivedDate, av.collectionDate, av.collectionTime, av.enteredDate, av.primaryOrganizationName, av.todoDescription," +
                        "av.worksheetDescription, av.priority, av.testId, av.testName, av.methodName, av.timeTaAverage, av.timeHolding, av.analysisId," +
                        "av.analysisStatusId, av.sectionId, av.sectionName, av.availableDate, av.startedDate, av.completedDate, av.releasedDate," +
                        "av.analysisResultOverride, av.unitOfMeasureId, av.worksheetFormatId)"
                      + " from AnalysisView av where av.analysisStatusId not in (select id from Dictionary d where d.systemName in ('analysis_logged_in', 'analysis_initiated',"
                      + " 'analysis_completed', 'analysis_released', 'analysis_cancelled')) order by av.accessionNumber"),
    @NamedQuery( name = "AnalysisView.FetchReleased",
                query = "select distinct new org.openelis.domain.AnalysisViewVO(av.sampleId, av.domain, av.accessionNumber," +
                        "av.receivedDate, av.collectionDate, av.collectionTime, av.enteredDate, av.primaryOrganizationName, av.todoDescription," +
                        "av.worksheetDescription, av.priority, av.testId, av.testName, av.methodName, av.timeTaAverage, av.timeHolding, av.analysisId," +
                        "av.analysisStatusId, av.sectionId, av.sectionName, av.availableDate, av.startedDate, av.completedDate, av.releasedDate," +
                        "av.analysisResultOverride, av.unitOfMeasureId, av.worksheetFormatId)"
                      + " from AnalysisView av where av.releasedDate >= :releasedDate and av.analysisStatusId = (select id from Dictionary d where d.systemName = 'analysis_released')"
                      + " order by av.accessionNumber")})
@Entity
@Table(name = "analysis_view")
public class AnalysisView  {

    @Column(name = "sample_id")
    private Integer                     sampleId;

    @Column(name = "domain")
    private String                      domain;

    @Column(name = "accession_number")
    private Integer                     accessionNumber;
    
    @Column(name = "received_date")
    private Date                        receivedDate;

    @Column(name = "collection_date")
    private Date                        collectionDate;

    @Column(name = "collection_time")
    private Date                        collectionTime;
    
    @Column(name = "entered_date")
    private Date                        enteredDate;

    @Column(name = "primary_organization_name")
    private String                      primaryOrganizationName;

    @Column(name = "todo_description")
    private String                      todoDescription;
    
    @Column(name = "worksheet_description")
    private String                      worksheetDescription;
    
    @Column(name = "priority")
    private Integer                     priority;
    
    @Column(name = "test_id")
    private Integer                     testId;

    @Column(name = "test_name")
    private String                      testName;

    @Column(name = "method_name")
    private String                      methodName;

    @Column(name = "time_ta_average")
    private Integer                     timeTaAverage;

    @Column(name = "time_holding")
    private Integer                     timeHolding;
    
    @Id
    @Column(name = "analysis_id")
    private Integer                     analysisId;

    @Column(name = "analysis_status_id")
    private Integer                     analysisStatusId;
    
    @Column(name = "section_id")
    private Integer                     sectionId;
    
    @Column(name = "section_name")
    private String                      sectionName;

    @Column(name = "available_date")
    private Date                        availableDate;

    @Column(name = "started_date")
    private Date                        startedDate;

    @Column(name = "completed_date")
    private Date                        completedDate;

    @Column(name = "released_date")
    private Date                        releasedDate;

    @Column(name = "analysis_result_override")
    private String                      analysisResultOverride;
    
    @Column(name = "unit_of_measure_id")
    private Integer                     unitOfMeasureId;
    
    @Column(name = "worksheet_format_id")
    private Integer                     worksheetFormatId;
    
    public Integer getSampleId() {
        return sampleId;
    }

    public String getDomain() {
        return domain;
    }

    public Integer getAccessionNumber() {
        return accessionNumber;
    }
    
    public Datetime getReceivedDate() {
        return DataBaseUtil.toYD(receivedDate);
    }
    
    public Datetime getCollectionDate() {
        return DataBaseUtil.toYD(collectionDate);
    }
    
    public Datetime getCollectionTime() {
        return DataBaseUtil.toHM(collectionTime);
    }
    
    public Datetime getEnteredDate() {
        return DataBaseUtil.toYM(enteredDate);
    }

    public String getPrimaryOrganizationName() {
        return primaryOrganizationName;
    }

    public String getToDoDescription() {
        return todoDescription;
    }
    
    public String getWorksheetDescription() {
        return worksheetDescription;
    }
    
    public Integer getPriority() {
        return priority;
    }
    
    public Integer getTestId() {
        return testId;
    }

    public String getTestName() {
        return testName;
    }

    public String getMethodName() {
        return methodName;
    }

    public Integer getTimeTaAverage() {
        return timeTaAverage;
    }

    public Integer getTimeHolding() {
        return timeHolding;
    }

    public Integer getAnalysisId() {
        return analysisId;
    }

    public Integer getAnalysisStatusId() {
        return analysisStatusId;
    }
    
    public Integer getSectionId() {
        return sectionId;
    }
    
    public String getSectionName() {
        return sectionName;
    }
    
    public Datetime getAvailableDate() {
        return DataBaseUtil.toYM(availableDate);
    }
    
    public Datetime getStartedDate() {
        return DataBaseUtil.toYM(startedDate);
    }
    
    public Datetime getCompletedDate() {
        return DataBaseUtil.toYM(completedDate);
    }
    
    public Datetime getReleasedDate() {
        return DataBaseUtil.toYM(releasedDate);
    }

    public String getAnalysisResultOverride() {
        return analysisResultOverride;
    }

    public Integer getUnitOfMeasureId() {
        return unitOfMeasureId;
    }

    public Integer getWorksheetFormatId() {
        return worksheetFormatId;
    }
}   