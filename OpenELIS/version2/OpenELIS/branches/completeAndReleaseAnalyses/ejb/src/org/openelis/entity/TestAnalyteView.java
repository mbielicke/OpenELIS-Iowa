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
package org.openelis.entity;

/**
 * Test Analyte View Entity POJO for database
 */

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import org.openelis.ui.common.DataBaseUtil;
import org.openelis.ui.common.Datetime;

@NamedQuery(name = "TestAnalyteView.FetchById",
            query = "select distinct new org.openelis.domain.TestAnalyteViewVO(tav.id, tav.testId, tav.testName, tav.methodId,"
                  + "tav.methodName, tav.testIsActive, tav.testActiveBegin, tav.testActiveEnd, tav.rowTestAnalyteId, tav.rowAnalyteId,"
                  + "tav.rowAnalyteName, tav.colAnalyteId, tav.colAnalyteName)"
                  + " from TestAnalyteView tav where tav.id = :id")
@Entity
@Table(name = "test_analyte_view")
public class TestAnalyteView {

    @Id
    @Column(name = "id")
    private Integer id;

    @Column(name = "test_id")
    private Integer testId;

    @Column(name = "test_name")
    private String  testName;

    @Column(name = "method_id")
    private Integer methodId;

    @Column(name = "method_name")
    private String  methodName;

    @Column(name = "test_is_active")
    private String  testIsActive;

    @Column(name = "test_active_begin")
    private Date    testActiveBegin;

    @Column(name = "test_active_end")
    private Date    testActiveEnd;

    @Column(name = "row_test_analyte_id")
    private Integer rowTestAnalyteId;

    @Column(name = "row_analyte_id")
    private Integer rowAnalyteId;

    @Column(name = "row_analyte_name")
    private String  rowAnalyteName;

    @Column(name = "col_analyte_id")
    private Integer colAnalyteId;

    @Column(name = "col_analyte_name")
    private String  colAnalyteName;

    public Integer getId() {
        return id;
    }

    public Integer getTestId() {
        return testId;
    }

    public String getTestName() {
        return testName;
    }

    public Integer getMethodId() {
        return methodId;
    }

    public String getMethodName() {
        return methodName;
    }

    public String getIsActive() {
        return testIsActive;
    }

    public Datetime getActiveBegin() {
        return DataBaseUtil.toYD(testActiveBegin);
    }

    public Datetime getActiveEnd() {
        return DataBaseUtil.toYD(testActiveEnd);
    }

    public Integer getRowTestAnalyteId() {
        return rowTestAnalyteId;
    }

    public Integer getRowAnalyteId() {
        return rowAnalyteId;
    }

    public String getRowAnalyteName() {
        return rowAnalyteName;
    }

    public Integer getColAnalyteId() {
        return colAnalyteId;
    }

    public String getColAnalyteName() {
        return colAnalyteName;
    }
}