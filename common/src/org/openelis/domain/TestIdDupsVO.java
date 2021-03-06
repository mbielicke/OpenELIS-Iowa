package org.openelis.domain;

import java.io.Serializable;

public class TestIdDupsVO implements Serializable {

    private static final long serialVersionUID = 1L;
    protected Integer testId;
    protected boolean checkForDups;
    
    public TestIdDupsVO() {
        
    }
    
    public TestIdDupsVO(Integer testId, boolean checkForDups) {
        setTestId(testId);
        setCheckForDups(checkForDups);
    }

    public Integer getTestId() {
        return testId;
    }

    public void setTestId(Integer testId) {
        this.testId = testId;
    }

    public boolean isCheckForDups() {
        return checkForDups;
    }

    public void setCheckForDups(boolean checkForDups) {
        this.checkForDups = checkForDups;
    }
}
