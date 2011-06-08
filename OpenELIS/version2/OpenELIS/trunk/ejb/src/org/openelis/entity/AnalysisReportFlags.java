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

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import org.openelis.gwt.common.DataBaseUtil;
import org.openelis.gwt.common.Datetime;

@NamedQueries({
    @NamedQuery( name = "AnalysisReportFlags.FetchByAnalysisId",
                query = "select new org.openelis.domain.AnalysisReportFlagsDO(rf.analysisId," +
                        "rf.notifiedReceived,rf.notifiedReleased,rf.billedDate,rf.billedAnalytes,rf.billedZero)"
                      + " from AnalysisReportFlags rf where rf.analysisId = :id")})

@Entity
@Table(name = "analysis_report_flags")
public class AnalysisReportFlags {

    @Id
    @Column(name = "analysis_id")
    private Integer               analysisId;

    @Column(name = "notified_received")
    private String                notifiedReceived;

    @Column(name = "notified_released")
    private String                notifiedReleased;

    @Column(name = "billed_date")
    private Date                  billedDate;

    @Column(name = "billed_analytes")
    private Integer               billedAnalytes;

    @Column(name = "billed_zero")
    private String                billedZero;

    public Integer getAnalysisId() {
        return analysisId;
    }

    public void setAnalysisId(Integer analysisId) {
        if (DataBaseUtil.isDifferent(analysisId, this.analysisId))
            this.analysisId = analysisId;
    }

    public String getNotifiedReceived() {
        return notifiedReceived;
    }

    public void setNotifiedReceived(String notifiedReceived) {
        if (DataBaseUtil.isDifferent(notifiedReceived, this.notifiedReceived))
            this.notifiedReceived = notifiedReceived;
    }

    public String getNotifiedReleased() {
        return notifiedReleased;
    }

    public void setNotifiedReleased(String notifiedReleased) {
        if (DataBaseUtil.isDifferent(notifiedReleased, this.notifiedReleased))
            this.notifiedReleased = notifiedReleased;
    }

    public Datetime getBilledDate() {
        return DataBaseUtil.toYD(billedDate);
    }

    public void setBilledDate(Datetime billedDate) {
        if (DataBaseUtil.isDifferentYD(billedDate, this.billedDate))
            this.billedDate = DataBaseUtil.toDate(billedDate);
    }

    public Integer getBilledAnalytes() {
        return billedAnalytes;
    }

    public void setBilledAnalytes(Integer billedAnalytes) {
        if (DataBaseUtil.isDifferent(billedAnalytes, this.billedAnalytes))
            this.billedAnalytes = billedAnalytes;
    }

    public String getBilledZero() {
        return billedZero;
    }

    public void setBilledZero(String billedZero) {
        if (DataBaseUtil.isDifferent(billedZero, this.billedZero))
            this.billedZero = billedZero;
    }
}
