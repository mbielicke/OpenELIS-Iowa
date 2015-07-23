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
  * Worksheet Analysis View Entity POJO for database 
  */

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import org.openelis.ui.common.DataBaseUtil;
import org.openelis.ui.common.Datetime;

@NamedQueries( {                  
    @NamedQuery( name = "WorksheetAnalysisView.FetchByWorksheetId",
                query = "select distinct new org.openelis.domain.WorksheetAnalysisViewVO(wav.id, wav.worksheetItemId, wav.worksheetId, wav.formatId, wav.worksheetDescription, " +
                        "wav.accessionNumber, wav.analysisId, wav.qcLotId, wav.qcId, wav.worksheetAnalysisId, wav.systemUsers, wav.startedDate, wav.completedDate, " +
                        "wav.fromOtherId, wav.changeFlagsId, wav.description, wav.testId, wav.testName, wav.methodName, wav.timeTaAverage, wav.timeHolding, wav.sectionName, " +
                        "wav.unitOfMeasureId, wav.unitOfMeasure, wav.analysisStatusId, wav.analysisTypeId, wav.collectionDate, wav.collectionTime, wav.receivedDate, " +
                        "wav.priority)"
                      + " from WorksheetAnalysisView wav where wav.worksheetId = :worksheetId order by wav.worksheetItemId, wav.id"),
    @NamedQuery( name = "WorksheetAnalysisView.FetchByWorksheetIds",
                query = "select distinct new org.openelis.domain.WorksheetAnalysisViewVO(wav.id, wav.worksheetItemId, wav.worksheetId, wav.formatId, wav.worksheetDescription, " +
                        "wav.accessionNumber, wav.analysisId, wav.qcLotId, wav.qcId, wav.worksheetAnalysisId, wav.systemUsers, wav.startedDate, wav.completedDate, " +
                        "wav.fromOtherId, wav.changeFlagsId, wav.description, wav.testId, wav.testName, wav.methodName, wav.timeTaAverage, wav.timeHolding, wav.sectionName, " +
                        "wav.unitOfMeasureId, wav.unitOfMeasure, wav.analysisStatusId, wav.analysisTypeId, wav.collectionDate, wav.collectionTime, wav.receivedDate, " +
                        "wav.priority)"
                      + " from WorksheetAnalysisView wav where wav.worksheetId in (:worksheetIds) order by wav.worksheetId, wav.worksheetItemId, wav.id")})
@Entity
@Table(name = "worksheet_analysis_view")
public class WorksheetAnalysisView  {

    @Id
    @Column(name = "id")
    private Integer                     id;

    @Column(name = "worksheet_item_id")
    private Integer                     worksheetItemId;

    @Column(name = "worksheet_id")
    private Integer                     worksheetId;

    @Column(name = "format_id")
    private Integer                     formatId;

    @Column(name = "worksheet_description")
    private String                      worksheetDescription;
    
    @Column(name = "accession_number")
    private String                      accessionNumber;
    
    @Column(name = "analysis_id")
    private Integer                     analysisId;

    @Column(name = "qc_lot_id")
    private Integer                     qcLotId;

    @Column(name = "qc_id")
    private Integer                     qcId;

    @Column(name = "worksheet_analysis_id")
    private Integer                     worksheetAnalysisId;

    @Column(name = "system_users")
    private String                      systemUsers;

    @Column(name = "started_date")
    private Date                        startedDate;
    
    @Column(name = "completed_date")
    private Date                        completedDate;
    
    @Column(name = "from_other_id")
    private Integer                     fromOtherId;

    @Column(name = "change_flags_id")
    private Integer                     changeFlagsId;

    @Column(name = "description")
    private String                      description;

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
    
    @Column(name = "section_name")
    private String                      sectionName;
    
    @Column(name = "unit_of_measure_id")
    private Integer                     unitOfMeasureId;
    
    @Column(name = "unit_of_measure")
    private String                      unitOfMeasure;
    
    @Column(name = "analysis_status_id")
    private Integer                     analysisStatusId;
    
    @Column(name = "analysis_type_id")
    private Integer                     analysisTypeId;
    
    @Column(name = "collection_date")
    private Date                        collectionDate;

    @Column(name = "collection_time")
    private Date                        collectionTime;

    @Column(name = "received_date")
    private Date                        receivedDate;

    @Column(name = "priority")
    private Integer                     priority;

    public Integer getId() {
        return id;
    }

    public Integer getWorksheetItemId() {
        return worksheetItemId;
    }

    public Integer getWorksheetId() {
        return worksheetId;
    }

    public Integer getFormatId() {
        return formatId;
    }

    public String getWorksheetDescription() {
        return worksheetDescription;
    }
    
    public String getAccessionNumber() {
        return accessionNumber;
    }
    
    public Integer getAnalysisId() {
        return analysisId;
    }

    public Integer getQcLotId() {
        return qcLotId;
    }

    public Integer getQcId() {
        return qcId;
    }

    public Integer getWorksheetAnalysisId() {
        return worksheetAnalysisId;
    }

    public String getSystemUsers() {
        return systemUsers;
    }

    public Datetime getStartedDate() {
        return DataBaseUtil.toYM(startedDate);
    }
    
    public Datetime getCompletedDate() {
        return DataBaseUtil.toYM(completedDate);
    }
    
    public Integer getFromOtherId() {
        return fromOtherId;
    }

    public Integer getChangeFlagsId() {
        return changeFlagsId;
    }

    public String getDescription() {
        return description;
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

    public String getSectionName() {
        return sectionName;
    }

    public Integer getUnitOfMeasureId() {
        return unitOfMeasureId;
    }

    public String getUnitOfMeasure() {
        return unitOfMeasure;
    }

    public Integer getAnalysisStatusId() {
        return analysisStatusId;
    }
    
    public Integer getAnalysisTypeId() {
        return analysisTypeId;
    }
    
    public Datetime getCollectionDate() {
        return DataBaseUtil.toYD(collectionDate);
    }
    
    public Datetime getCollectionTime() {
        return DataBaseUtil.toHM(collectionTime);
    }
    
    public Datetime getReceivedDate() {
        return DataBaseUtil.toYM(receivedDate);
    }
    
    public Integer getPriority() {
        return priority;
    }
}