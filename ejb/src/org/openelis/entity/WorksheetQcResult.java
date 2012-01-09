package org.openelis.entity;

/**
 * WorksheetQcResult Entity POJO for database
 */

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.openelis.domain.ReferenceTable;
import org.openelis.gwt.common.DataBaseUtil;
import org.openelis.utils.Audit;
import org.openelis.utils.AuditUtil;
import org.openelis.utils.Auditable;

@NamedQueries({
    @NamedQuery( name = "WorksheetQcResult.FetchByWorksheetAnalysisId",
                query = "select new org.openelis.domain.WorksheetQcResultViewDO(wqr.id,wqr.worksheetAnalysisId," +
                		"wqr.sortOrder,wqr.qcAnalyteId,wqr.typeId,wqr.value1,wqr.value2," +
                		"wqr.value3,wqr.value4,wqr.value5,wqr.value6,wqr.value7," +
                		"wqr.value8,wqr.value9,wqr.value10,wqr.value11,wqr.value12," +
                		"wqr.value13,wqr.value14,wqr.value15,wqr.value16,wqr.value17," +
                		"wqr.value18,wqr.value19,wqr.value20,wqr.value21,wqr.value22," +
                		"wqr.value23,wqr.value24,wqr.value25,wqr.value26,wqr.value27," +
                		"wqr.value28,wqr.value29,wqr.value30,a.id,a.name) "
                      + " from WorksheetQcResult wqr LEFT JOIN wqr.qcAnalyte qca LEFT JOIN qca.analyte a "+
                        " where wqr.worksheetAnalysisId = :id order by wqr.sortOrder")})
@Entity
@Table(name = "worksheet_qc_result")
@EntityListeners( {AuditUtil.class})
public class WorksheetQcResult implements Auditable, Cloneable {

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer          id;

    @Column(name = "worksheet_analysis_id")
    private Integer          worksheetAnalysisId;

    @Column(name = "sort_order")
    private Integer          sortOrder;

    @Column(name = "qc_analyte_id")
    private Integer          qcAnalyteId;

    @Column(name = "type_id")
    private Integer          typeId;

    @Column(name = "value_1")
    private String           value1;

    @Column(name = "value_2")
    private String           value2;

    @Column(name = "value_3")
    private String           value3;

    @Column(name = "value_4")
    private String           value4;

    @Column(name = "value_5")
    private String           value5;

    @Column(name = "value_6")
    private String           value6;

    @Column(name = "value_7")
    private String           value7;

    @Column(name = "value_8")
    private String           value8;

    @Column(name = "value_9")
    private String           value9;

    @Column(name = "value_10")
    private String           value10;

    @Column(name = "value_11")
    private String           value11;

    @Column(name = "value_12")
    private String           value12;

    @Column(name = "value_13")
    private String           value13;

    @Column(name = "value_14")
    private String           value14;

    @Column(name = "value_15")
    private String           value15;

    @Column(name = "value_16")
    private String           value16;

    @Column(name = "value_17")
    private String           value17;

    @Column(name = "value_18")
    private String           value18;

    @Column(name = "value_19")
    private String           value19;

    @Column(name = "value_20")
    private String           value20;

    @Column(name = "value_21")
    private String           value21;

    @Column(name = "value_22")
    private String           value22;

    @Column(name = "value_23")
    private String           value23;

    @Column(name = "value_24")
    private String           value24;

    @Column(name = "value_25")
    private String           value25;

    @Column(name = "value_26")
    private String           value26;

    @Column(name = "value_27")
    private String           value27;

    @Column(name = "value_28")
    private String           value28;

    @Column(name = "value_29")
    private String           value29;

    @Column(name = "value_30")
    private String           value30;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "qc_analyte_id", insertable = false, updatable = false)
    private QcAnalyte qcAnalyte;
    
    @Transient
    private WorksheetQcResult original;

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

    public Integer getQcAnalyteId() {
        return qcAnalyteId;
    }

    public void setQcAnalyteId(Integer qcAnalyteId) {
        if (DataBaseUtil.isDifferent(qcAnalyteId, this.qcAnalyteId))
            this.qcAnalyteId = qcAnalyteId;
    }

    public Integer getTypeId() {
        return typeId;
    }

    public void setTypeId(Integer typeId) {
        if (DataBaseUtil.isDifferent(typeId, this.typeId))
            this.typeId = typeId;
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

    public QcAnalyte getQcAnalyte() {
        return qcAnalyte;
    }
    
    public void setQcAnalyte(QcAnalyte qcAnalyte) {
        this.qcAnalyte = qcAnalyte;
    }

    public void setClone() {
        try {
            original = (WorksheetQcResult)this.clone();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Audit getAudit() {
        Audit audit;

        audit = new Audit();
        audit.setReferenceTableId(ReferenceTable.WORKSHEET_QC_RESULT);
        audit.setReferenceId(getId());
        if (original != null)
            audit.setField("id", id, original.id)
                 .setField("worksheet_analysis_id", worksheetAnalysisId, original.worksheetAnalysisId, ReferenceTable.WORKSHEET_ANALYSIS)
                 .setField("sort_order", sortOrder, original.sortOrder)
                 .setField("qc_analyte_id", qcAnalyteId, original.qcAnalyteId, ReferenceTable.QC_ANALYTE)
                 .setField("type_id", typeId, original.typeId, ReferenceTable.DICTIONARY)
                 .setField("value_1", value1, original.value1)
                 .setField("value_2", value2, original.value2)
                 .setField("value_3", value3, original.value3)
                 .setField("value_4", value4, original.value4)
                 .setField("value_5", value5, original.value5)
                 .setField("value_6", value6, original.value6)
                 .setField("value_7", value7, original.value7)
                 .setField("value_8", value8, original.value8)
                 .setField("value_9", value9, original.value9)
                 .setField("value_10", value10, original.value10)
                 .setField("value_11", value11, original.value11)
                 .setField("value_12", value12, original.value12)
                 .setField("value_13", value13, original.value13)
                 .setField("value_14", value14, original.value14)
                 .setField("value_15", value15, original.value15)
                 .setField("value_16", value16, original.value16)
                 .setField("value_17", value17, original.value17)
                 .setField("value_18", value18, original.value18)
                 .setField("value_19", value19, original.value19)
                 .setField("value_20", value20, original.value20)
                 .setField("value_21", value21, original.value21)
                 .setField("value_22", value22, original.value22)
                 .setField("value_23", value23, original.value23)
                 .setField("value_24", value24, original.value24)
                 .setField("value_25", value25, original.value25)
                 .setField("value_26", value26, original.value26)
                 .setField("value_27", value27, original.value27)
                 .setField("value_28", value28, original.value28)
                 .setField("value_29", value29, original.value29)
                 .setField("value_30", value30, original.value30);

        return audit;
    }
}
