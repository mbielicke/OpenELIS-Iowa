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
  * ToDo Analysis View Entity POJO for database 
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
    @NamedQuery( name = "ToDoAnalysisView.FetchByAnalysisStatusId",
                query = "select distinct new org.openelis.domain.ToDoAnalysisViewVO(av.sampleId, av.domain, av.accessionNumber," +
                        "av.receivedDate, av.collectionDate, av.collectionTime, av.primaryOrganizationName, av.description, av.priority," +
                        "av.testName, av.methodName, av.timeTaAverage, av.timeHolding, av.analysisId," +
                        "av.analysisStatusId, av.availableDate, av.startedDate, av.completedDate, av.releasedDate, av.analysisResultOverride, av.sectionName)"
                      + " from ToDoAnalysisView av where av.analysisStatusId = :statusId order by av.accessionNumber"),
    @NamedQuery( name = "ToDoAnalysisView.FetchOther",
                query = "select distinct new org.openelis.domain.ToDoAnalysisViewVO(av.sampleId, av.domain, av.accessionNumber," +
                        "av.receivedDate, av.collectionDate, av.collectionTime, av.primaryOrganizationName, av.description, av.priority," +
                        "av.testName, av.methodName, av.timeTaAverage, av.timeHolding, av.analysisId," +
                        "av.analysisStatusId, av.availableDate, av.startedDate, av.completedDate, av.releasedDate, av.analysisResultOverride, av.sectionName)"
                      + " from ToDoAnalysisView av, Dictionary d where av.analysisStatusId = d.id and"
                      + " d.systemName not in ('analysis_logged_in', 'analysis_initiated', 'analysis_completed', 'analysis_released', 'analysis_cancelled')) order by av.accessionNumber"),
    @NamedQuery( name = "ToDoAnalysisView.FetchReleased",
                query = "select distinct new org.openelis.domain.ToDoAnalysisViewVO(av.sampleId, av.domain, av.accessionNumber," +
                        "av.receivedDate, av.collectionDate, av.collectionTime, av.primaryOrganizationName, av.description, av.priority," +
                        "av.testName, av.methodName, av.timeTaAverage, av.timeHolding, av.analysisId," +
                        "av.analysisStatusId, av.availableDate, av.startedDate, av.completedDate, av.releasedDate, av.analysisResultOverride, av.sectionName)"
                      + " from ToDoAnalysisView av, Dictionary d where av.analysisStatusId = d.id and d.systemName = 'analysis_released' and av.releasedDate >= :releasedDate order by av.accessionNumber")})
@Entity
@Table(name = "todo_analysis_view")
public class ToDoAnalysisView  {

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

    @Column(name = "primary_organization_name")
    private String                      primaryOrganizationName;

    @Column(name = "description")
    private String                      description;
    
    @Column(name = "priority")
    private Integer                     priority;

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
    
    @Column(name = "section_name")
    private String                      sectionName;

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

    public String getPrimaryOrganizationName() {
        return primaryOrganizationName;
    }

    public String getDescription() {
        return description;
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

    public Integer getPriority() {
        return priority;
    }
    
    public String getSectionName() {
        return sectionName;
    }
}   