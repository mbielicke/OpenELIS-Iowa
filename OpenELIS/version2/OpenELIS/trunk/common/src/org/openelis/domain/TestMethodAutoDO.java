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
package org.openelis.domain;

import java.util.Date;

import org.openelis.gwt.common.Datetime;
import org.openelis.gwt.common.RPC;
import org.openelis.utilcommon.DataBaseUtil;

public class TestMethodAutoDO implements RPC {

    private static final long serialVersionUID = 1L;
    
    protected Integer testId;
    protected String testName;
    protected Integer methodId;
    protected String methodName;
    protected String testDescription;
    protected String methodDescription;
    protected Datetime activeBegin;
    protected Datetime activeEnd;
    
    public TestMethodAutoDO(){
        
    }
    
    public TestMethodAutoDO(Integer testId, String testName, 
                            String testDescription,Integer methodId,
                            String methodName,String methodDescription){
        setTestId(testId);
        setTestName(testName);
        setMethodId(methodId);
        setMethodName(methodName);
        setTestDescription(testDescription);
        setMethodDescription(methodDescription);
    }

    public TestMethodAutoDO(Integer testId, String testName, 
                            String testDescription,Integer methodId,
                            String methodName,String methodDescription,
                            Date activeBegin, Date activeEnd){
        setTestId(testId);
        setTestName(testName);
        setMethodId(methodId);
        setMethodName(methodName);
        setTestDescription(testDescription);
        setMethodDescription(methodDescription);
        setActiveBegin(Datetime.getInstance(Datetime.YEAR, Datetime.DAY, activeBegin));
        setActiveEnd(Datetime.getInstance(Datetime.YEAR, Datetime.DAY, activeEnd));
    }

    public Integer getTestId() {
        return testId;
    }

    public void setTestId(Integer testId) {
        this.testId = testId;
    }

    public String getTestName() {
        return DataBaseUtil.trim(testName);
    }

    public void setTestName(String testName) {
        this.testName = testName;
    }

    public Integer getMethodId() {
        return methodId;
    }

    public void setMethodId(Integer methodId) {
        this.methodId = methodId;
    }

    public String getMethodName() {
        return DataBaseUtil.trim(methodName);
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public String getTestDescription() {
        return DataBaseUtil.trim(testDescription);
    }

    public String getMethodDescription() {
        return DataBaseUtil.trim(methodDescription);
    }

    public void setTestDescription(String testDescription) {
        this.testDescription = testDescription;
    }

    public void setMethodDescription(String methodDescription) {
        this.methodDescription = methodDescription;
    }

    public Datetime getActiveBegin() {
        return activeBegin;
    }

    public void setActiveBegin(Datetime activeBegin) {
        this.activeBegin = DataBaseUtil.toYD(activeBegin);
    }

    public Datetime getActiveEnd() {
        return activeEnd;
    }

    public void setActiveEnd(Datetime activeEnd) {
        this.activeEnd = DataBaseUtil.toYD(activeEnd);
    }
}
