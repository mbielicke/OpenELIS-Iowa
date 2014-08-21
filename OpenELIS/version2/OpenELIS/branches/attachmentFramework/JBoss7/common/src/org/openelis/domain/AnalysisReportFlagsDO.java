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
 * Class represents the fields in database table analysis_report_flags.
 */

public class AnalysisReportFlagsDO extends DataObject {

    private static final long serialVersionUID = 1L;

    protected Integer         analysisId, billedAnalytes;
    protected String          notifiedReceived, notifiedReleased, billedZero;
    protected Datetime        billedDate;

    public AnalysisReportFlagsDO() {
    }

    public AnalysisReportFlagsDO(Integer analysisId, String notifiedReceived, String notifiedReleased,
                                 Date billedDate, Integer billedAnalytes, String billedZero) {
        setAnalysisId(analysisId);
        setNotifiedReceived(notifiedReceived);
        setNotifiedReleased(notifiedReleased);
        setBilledDate(DataBaseUtil.toYD(billedDate));
        setBilledAnalytes(billedAnalytes);
        setBilledZero(billedZero);
        _changed = false;
    }

    public Integer getAnalysisId() {
        return analysisId;
    }

    public void setAnalysisId(Integer analysisId) {
        this.analysisId = analysisId;
        _changed = true;
    }

    public String getNotifiedReceived() {
        return notifiedReceived;
    }

    public void setNotifiedReceived(String notifiedReceived) {
        this.notifiedReceived = DataBaseUtil.trim(notifiedReceived);
        _changed = true;
    }

    public String getNotifiedReleased() {
        return notifiedReleased;
    }

    public void setNotifiedReleased(String notifiedReleased) {
        this.notifiedReleased = DataBaseUtil.trim(notifiedReleased);
        _changed = true;
    }

    public Datetime getBilledDate() {
        return billedDate;
    }

    public void setBilledDate(Datetime billedDate) {
        this.billedDate = DataBaseUtil.toYD(billedDate);
        _changed = true;
    }

    public Integer getBilledAnalytes() {
        return billedAnalytes;
    }

    public void setBilledAnalytes(Integer billedAnalytes) {
        this.billedAnalytes = billedAnalytes;
        _changed = true;
    }

    public String getBilledZero() {
        return billedZero;
    }

    public void setBilledZero(String billedZero) {
        this.billedZero = DataBaseUtil.trim(billedZero);
        _changed = true;
    }
}