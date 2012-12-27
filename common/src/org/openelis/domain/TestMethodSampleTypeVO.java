package org.openelis.domain;

import java.io.Serializable;

import org.openelis.gwt.common.DataBaseUtil;

public class TestMethodSampleTypeVO implements Serializable {

    private static final long serialVersionUID = 1L;
    
    protected Integer testId, panelId, sampleTypeId;
    protected String test, method, panel, sampleType;
    
    public TestMethodSampleTypeVO() {
        
    }
    
    //for tests
    public TestMethodSampleTypeVO(Integer testId, String test, String method, 
                                  Integer sampleTypeId, String sampleType) {
        setTestId(testId);
        setTest(test);
        setMethod(method);
        setSampleTypeId(sampleTypeId);
        setSampleType(sampleType);
    }

    //for panels
    public TestMethodSampleTypeVO(Integer panelId, String panel, 
                                  Integer sampleTypeId, String sampleType) {
        setPanelId(panelId);
        setPanel(panel);
        setSampleTypeId(sampleTypeId);
        setSampleType(sampleType);
    }

    
    public Integer getTestId() {
        return testId;
    }
    
    public void setTestId(Integer testId) {
        this.testId = testId;
    }
    
    public Integer getPanelId() {
        return panelId;
    }
    
    public void setPanelId(Integer panelId) {
        this.panelId = panelId;
    }
    
    public Integer getSampleTypeId() {
        return sampleTypeId;
    }
    
    public void setSampleTypeId(Integer sampleTypeId) {
        this.sampleTypeId = sampleTypeId;
    }
    
    public String getTest() {
        return test;
    }
    
    public void setTest(String test) {
        this.test = DataBaseUtil.trim(test);
    }
    
    public String getMethod() {
        return method;
    }
    
    public void setMethod(String method) {
        this.method = DataBaseUtil.trim(method);
    }
    
    public String getSampleType() {
        return sampleType;
    }
    
    public void setSampleType(String sampleType) {
        this.sampleType = DataBaseUtil.trim(sampleType);
    }

    public String getPanel() {
        return panel;
    }

    public void setPanel(String panel) {
        this.panel = DataBaseUtil.trim(panel);
    }
}
