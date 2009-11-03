package org.openelis.domain;

import java.io.Serializable;

public class TestMethodSectionNamesDO implements Serializable{

    /**
     * 
     */
    private static final long serialVersionUID = -2454126477553936793L;
    
    protected Integer testId;
    
    protected String testName;
    
    protected String methodName;
    
    protected String sectionName;
    
    public TestMethodSectionNamesDO() {
       
    }
    
    public TestMethodSectionNamesDO(Integer testId,String testName,
                                    String methodName,String sectionName){
        this.testId = testId;
        this.testName = testName;
        this.methodName = methodName;
        this.sectionName = sectionName;       
    }
    

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public String getSectionName() {
        return sectionName;
    }

    public void setSectionName(String sectionName) {
        this.sectionName = sectionName;
    }

    public Integer getTestId() {
        return testId;
    }

    public void setTestId(Integer testId) {
        this.testId = testId;
    }

    public String getTestName() {
        return testName;
    }

    public void setTestName(String testName) {
        this.testName = testName;
    }


}
