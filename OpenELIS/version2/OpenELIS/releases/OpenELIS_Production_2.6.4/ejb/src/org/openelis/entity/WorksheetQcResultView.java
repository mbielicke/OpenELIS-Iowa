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
  * Worksheet QC Result View Entity POJO for database 
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
    @NamedQuery( name = "WorksheetQcResultView.FetchByIds",
                query = "select distinct new org.openelis.domain.WorksheetQcResultViewVO(wqrv.id," +
                        "wqrv.worksheetAnalysisId,wqrv.sortOrder,wqrv.value1,wqrv.value2," +
                        "wqrv.value3,wqrv.value4,wqrv.value5,wqrv.value6,wqrv.value7," +
                        "wqrv.value8,wqrv.value9,wqrv.value10,wqrv.value11,wqrv.value12," +
                        "wqrv.value13,wqrv.value14,wqrv.value15,wqrv.value16,wqrv.value17," +
                        "wqrv.value18,wqrv.value19,wqrv.value20,wqrv.value21,wqrv.value22," +
                        "wqrv.value23,wqrv.value24,wqrv.value25,wqrv.value26,wqrv.value27," +
                        "wqrv.value28,wqrv.value29,wqrv.value30,wqrv.qcName,wqrv.qcTypeId," +
                        "wqrv.source,wqrv.lotNumber,wqrv.locationId,wqrv.preparedDate," +
                        "wqrv.preparedVolume,wqrv.preparedUnitId,wqrv.preparedById," +
                        "wqrv.usableDate,wqrv.expireDate,wqrv.analyteId,wqrv.qcAnalyteTypeId," +
                        "wqrv.expectedValue)"
                      + " from WorksheetQcResultView wqrv"+
                        " where wqrv.id = :id order by wqrv.sortOrder")})
@Entity
@Table(name = "worksheet_qc_result_view")
public class WorksheetQcResultView  {

    @Id
    @Column(name = "id")
    private Integer id;

    @Column(name = "worksheet_analysis_id")
    private Integer worksheetAnalysisId;

    @Column(name = "sort_order")
    private Integer sortOrder;

    @Column(name = "value_1")
    private String  value1;

    @Column(name = "value_2")
    private String  value2;

    @Column(name = "value_3")
    private String  value3;

    @Column(name = "value_4")
    private String  value4;

    @Column(name = "value_5")
    private String  value5;

    @Column(name = "value_6")
    private String  value6;

    @Column(name = "value_7")
    private String  value7;

    @Column(name = "value_8")
    private String  value8;

    @Column(name = "value_9")
    private String  value9;

    @Column(name = "value_10")
    private String  value10;

    @Column(name = "value_11")
    private String  value11;

    @Column(name = "value_12")
    private String  value12;

    @Column(name = "value_13")
    private String  value13;

    @Column(name = "value_14")
    private String  value14;

    @Column(name = "value_15")
    private String  value15;

    @Column(name = "value_16")
    private String  value16;

    @Column(name = "value_17")
    private String  value17;

    @Column(name = "value_18")
    private String  value18;

    @Column(name = "value_19")
    private String  value19;

    @Column(name = "value_20")
    private String  value20;

    @Column(name = "value_21")
    private String  value21;

    @Column(name = "value_22")
    private String  value22;

    @Column(name = "value_23")
    private String  value23;

    @Column(name = "value_24")
    private String  value24;

    @Column(name = "value_25")
    private String  value25;

    @Column(name = "value_26")
    private String  value26;

    @Column(name = "value_27")
    private String  value27;

    @Column(name = "value_28")
    private String  value28;

    @Column(name = "value_29")
    private String  value29;

    @Column(name = "value_30")
    private String  value30;

    @Column(name = "qc_name")
    private String  qcName;

    @Column(name = "qc_type_id")
    private Integer qcTypeId;

    @Column(name = "source")
    private String  source;

    @Column(name = "lot_number")
    private String  lotNumber;

    @Column(name = "location_id")
    private Integer locationId;

    @Column(name = "prepared_date")
    private Date    preparedDate;

    @Column(name = "prepared_volume")
    private Double  preparedVolume;

    @Column(name = "prepared_unit_id")
    private Integer preparedUnitId;

    @Column(name = "prepared_by_id")
    private Integer preparedById;

    @Column(name = "usable_date")
    private Date    usableDate;

    @Column(name = "expire_date")
    private Date    expireDate;

    @Column(name = "analyte_id")
    private Integer analyteId;

    @Column(name = "qc_analyte_type_id")
    private Integer qcAnalyteTypeId;

    @Column(name = "expected_value")
    private String  expectedValue;

    public Integer getId() {
        return id;
    }

    protected void setId(Integer id) {
        if (DataBaseUtil.isDifferent(id, this.id))
            this.id = id;
    }

    public Integer getWorksheetAnalysisId() {
        return worksheetAnalysisId;
    }

    public void setWorksheetAnalysisId(Integer worksheetAnalysisId) {
        if (DataBaseUtil.isDifferent(worksheetAnalysisId, this.worksheetAnalysisId))
            this.worksheetAnalysisId = worksheetAnalysisId;
    }

    public Integer getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(Integer sortOrder) {
        if (DataBaseUtil.isDifferent(sortOrder, this.sortOrder))
            this.sortOrder = sortOrder;
    }

    public String getValue1() {
        return value1;
    }

    public void setValue1(String value1) {
        if (DataBaseUtil.isDifferent(value1, this.value1))
            this.value1 = value1;
    }

    public String getValue2() {
        return value2;
    }

    public void setValue2(String value2) {
        if (DataBaseUtil.isDifferent(value2, this.value2))
            this.value2 = value2;
    }

    public String getValue3() {
        return value3;
    }

    public void setValue3(String value3) {
        if (DataBaseUtil.isDifferent(value3, this.value3))
            this.value3 = value3;
    }

    public String getValue4() {
        return value4;
    }

    public void setValue4(String value4) {
        if (DataBaseUtil.isDifferent(value4, this.value4))
            this.value4 = value4;
    }

    public String getValue5() {
        return value5;
    }

    public void setValue5(String value5) {
        if (DataBaseUtil.isDifferent(value5, this.value5))
            this.value5 = value5;
    }

    public String getValue6() {
        return value6;
    }

    public void setValue6(String value6) {
        if (DataBaseUtil.isDifferent(value6, this.value6))
            this.value6 = value6;
    }

    public String getValue7() {
        return value7;
    }

    public void setValue7(String value7) {
        if (DataBaseUtil.isDifferent(value7, this.value7))
            this.value7 = value7;
    }

    public String getValue8() {
        return value8;
    }

    public void setValue8(String value8) {
        if (DataBaseUtil.isDifferent(value8, this.value8))
            this.value8 = value8;
    }

    public String getValue9() {
        return value9;
    }

    public void setValue9(String value9) {
        if (DataBaseUtil.isDifferent(value9, this.value9))
            this.value9 = value9;
    }

    public String getValue10() {
        return value10;
    }

    public void setValue10(String value10) {
        if (DataBaseUtil.isDifferent(value10, this.value10))
            this.value10 = value10;
    }

    public String getValue11() {
        return value11;
    }

    public void setValue11(String value11) {
        if (DataBaseUtil.isDifferent(value11, this.value11))
            this.value11 = value11;
    }

    public String getValue12() {
        return value12;
    }

    public void setValue12(String value12) {
        if (DataBaseUtil.isDifferent(value12, this.value12))
            this.value12 = value12;
    }

    public String getValue13() {
        return value13;
    }

    public void setValue13(String value13) {
        if (DataBaseUtil.isDifferent(value13, this.value13))
            this.value13 = value13;
    }

    public String getValue14() {
        return value14;
    }

    public void setValue14(String value14) {
        if (DataBaseUtil.isDifferent(value14, this.value14))
            this.value14 = value14;
    }

    public String getValue15() {
        return value15;
    }

    public void setValue15(String value15) {
        if (DataBaseUtil.isDifferent(value15, this.value15))
            this.value15 = value15;
    }

    public String getValue16() {
        return value16;
    }

    public void setValue16(String value16) {
        if (DataBaseUtil.isDifferent(value16, this.value16))
            this.value16 = value16;
    }

    public String getValue17() {
        return value17;
    }

    public void setValue17(String value17) {
        if (DataBaseUtil.isDifferent(value17, this.value17))
            this.value17 = value17;
    }

    public String getValue18() {
        return value18;
    }

    public void setValue18(String value18) {
        if (DataBaseUtil.isDifferent(value18, this.value18))
            this.value18 = value18;
    }

    public String getValue19() {
        return value19;
    }

    public void setValue19(String value19) {
        if (DataBaseUtil.isDifferent(value19, this.value19))
            this.value19 = value19;
    }

    public String getValue20() {
        return value20;
    }

    public void setValue20(String value20) {
        if (DataBaseUtil.isDifferent(value20, this.value20))
            this.value20 = value20;
    }

    public String getValue21() {
        return value21;
    }

    public void setValue21(String value21) {
        if (DataBaseUtil.isDifferent(value21, this.value21))
            this.value21 = value21;
    }

    public String getValue22() {
        return value22;
    }

    public void setValue22(String value22) {
        if (DataBaseUtil.isDifferent(value22, this.value22))
            this.value22 = value22;
    }

    public String getValue23() {
        return value23;
    }

    public void setValue23(String value23) {
        if (DataBaseUtil.isDifferent(value23, this.value23))
            this.value23 = value23;
    }

    public String getValue24() {
        return value24;
    }

    public void setValue24(String value24) {
        if (DataBaseUtil.isDifferent(value24, this.value24))
            this.value24 = value24;
    }

    public String getValue25() {
        return value25;
    }

    public void setValue25(String value25) {
        if (DataBaseUtil.isDifferent(value25, this.value25))
            this.value25 = value25;
    }

    public String getValue26() {
        return value26;
    }

    public void setValue26(String value26) {
        if (DataBaseUtil.isDifferent(value26, this.value26))
            this.value26 = value26;
    }

    public String getValue27() {
        return value27;
    }

    public void setValue27(String value27) {
        if (DataBaseUtil.isDifferent(value27, this.value27))
            this.value27 = value27;
    }

    public String getValue28() {
        return value28;
    }

    public void setValue28(String value28) {
        if (DataBaseUtil.isDifferent(value28, this.value28))
            this.value28 = value28;
    }

    public String getValue29() {
        return value29;
    }

    public void setValue29(String value29) {
        if (DataBaseUtil.isDifferent(value29, this.value29))
            this.value29 = value29;
    }

    public String getValue30() {
        return value30;
    }

    public void setValue30(String value30) {
        if (DataBaseUtil.isDifferent(value30, this.value30))
            this.value30 = value30;
    }
    
    public String getQcName() {
        return qcName;
    }

    public void setQcName(String qcName) {
        if (DataBaseUtil.isDifferent(qcName, this.qcName))
            this.qcName = qcName;
    }

    public Integer getQcTypeId() {
        return qcTypeId;
    }

    public void setQcTypeId(Integer qcTypeId) {
        if (DataBaseUtil.isDifferent(qcTypeId, this.qcTypeId))
            this.qcTypeId = qcTypeId;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        if (DataBaseUtil.isDifferent(source, this.source))
            this.source = source;
    }

    public String getLotNumber() {
        return lotNumber;
    }

    public void setLotNumber(String lotNumber) {
        if (DataBaseUtil.isDifferent(lotNumber, this.lotNumber))
            this.lotNumber = lotNumber;
    }

    public Integer getLocationId() {
        return locationId;
    }

    public void setLocationId(Integer locationId) {
        if (DataBaseUtil.isDifferent(locationId, this.locationId))
            this.locationId = locationId;
    }

    public Datetime getPreparedDate() {
        return DataBaseUtil.toYM(preparedDate);
    }

    public void setPreparedDate(Datetime prepared_date) {
        if (DataBaseUtil.isDifferentYM(prepared_date, this.preparedDate))
            this.preparedDate = DataBaseUtil.toDate(prepared_date);
    }

    public Double getPreparedVolume() {
        return preparedVolume;
    }

    public void setPreparedVolume(Double preparedVolume) {
        if (DataBaseUtil.isDifferent(preparedVolume, this.preparedVolume))
            this.preparedVolume = preparedVolume;
    }

    public Integer getPreparedUnitId() {
        return preparedUnitId;
    }

    public void setPreparedUnitId(Integer preparedUnitId) {
        if (DataBaseUtil.isDifferent(preparedUnitId, this.preparedUnitId))
            this.preparedUnitId = preparedUnitId;
    }

    public Integer getPreparedById() {
        return preparedById;
    }

    public void setPreparedById(Integer preparedById) {
        if (DataBaseUtil.isDifferent(preparedById, this.preparedById))
            this.preparedById = preparedById;
    }

    public Datetime getUsableDate() {
        return DataBaseUtil.toYM(usableDate);
    }

    public void setUsableDate(Datetime usableDate) {
        if (DataBaseUtil.isDifferentYM(usableDate, this.usableDate))
            this.usableDate = DataBaseUtil.toDate(usableDate);
    }

    public Datetime getExpireDate() {
        return DataBaseUtil.toYM(expireDate);
    }

    public void setExpireDate(Datetime expireDate) {
        if (DataBaseUtil.isDifferentYM(expireDate, this.expireDate))
            this.expireDate = DataBaseUtil.toDate(expireDate);
    }

    public Integer getAnalyteId() {
        return analyteId;
    }

    public void setAnalyteId(Integer analyteId) {
        if (DataBaseUtil.isDifferent(analyteId, this.analyteId))
            this.analyteId = analyteId;
    }

    public Integer getQcAnalyteTypeId() {
        return qcAnalyteTypeId;
    }

    public void setQcAnalyteTypeId(Integer qcAnalyteTypeId) {
        if (DataBaseUtil.isDifferent(qcAnalyteTypeId, this.qcAnalyteTypeId))
            this.qcAnalyteTypeId = qcAnalyteTypeId;
    }

    public String getExpectedValue() {
        return expectedValue;
    }

    public void setExpectedValue(String expectedValue) {
        if (DataBaseUtil.isDifferent(expectedValue, this.expectedValue))
            this.expectedValue = expectedValue;
    }
}