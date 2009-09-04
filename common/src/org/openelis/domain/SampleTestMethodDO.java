package org.openelis.domain;

import org.openelis.gwt.common.RPC;
import org.openelis.utilcommon.DataBaseUtil;

public class SampleTestMethodDO implements RPC {

    private static final long serialVersionUID = 1L;
    
    protected Integer testId;
    protected String testName;
    protected Integer methodId;
    protected String methodName;
    protected String isReportable;
    
    //sections
    //pre tests
    
    public SampleTestMethodDO(){
        
    }
    
    public SampleTestMethodDO(Integer testId, String testName, Integer methodId,
                              String methodName, String isReportable){
        setTestId(testId);
        setTestName(testName);
        setMethodId(methodId);
        setMethodName(methodName);
        setIsReportable(isReportable);
        
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
        this.testName = DataBaseUtil.trim(testName);
    }

    public Integer getMethodId() {
        return methodId;
    }

    public void setMethodId(Integer methodId) {
        this.methodId = methodId;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = DataBaseUtil.trim(methodName);
    }

    public String getIsReportable() {
        return isReportable;
    }

    public void setIsReportable(String isReportable) {
        this.isReportable = isReportable;
    }
}
