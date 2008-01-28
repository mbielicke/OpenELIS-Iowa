package org.openelis.domain;

import java.io.Serializable;


public class QaEventDO implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    
    protected Integer id;             

    protected String name;             
    
    protected String description;             

    protected Integer test;             

    protected Integer type;             

    protected String isBillable;             

    protected Integer reportingSequence;             

    protected String reportingText;
    
    public QaEventDO(){
        
    }
        

    public QaEventDO(Integer id, String name, String description, Integer test, Integer type, String isBillable, Integer reportingSequence, String reportingText) {
        super();
        this.id = id;
        this.name = name;
        this.description = description;
        this.test = test;
        this.type = type;
        this.isBillable = isBillable;
        this.reportingSequence = reportingSequence;
        this.reportingText = reportingText;
    }



    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getIsBillable() {
        return isBillable;
    }

    public void setIsBillable(String isBillable) {
        this.isBillable = isBillable;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getReportingSequence() {
        return reportingSequence;
    }

    public void setReportingSequence(Integer reportingSequence) {
        this.reportingSequence = reportingSequence;
    }

    public String getReportingText() {
        return reportingText;
    }

    public void setReportingText(String reportingText) {
        this.reportingText = reportingText;
    }

    public Integer getTest() {
        return test;
    }

    public void setTest(Integer test) {
        this.test = test;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

}
