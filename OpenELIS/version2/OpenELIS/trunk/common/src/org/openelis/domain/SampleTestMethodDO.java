package org.openelis.domain;

import java.util.ArrayList;
import java.util.Date;

import org.openelis.gwt.common.Datetime;
import org.openelis.gwt.common.RPC;
import org.openelis.utilcommon.DataBaseUtil;

public class SampleTestMethodDO implements RPC {

    private static final long serialVersionUID = 1L;
    
    protected TestDO test = new TestDO();
    protected ArrayList<TestSectionDO> sections;
    protected ArrayList<TestPrepDO> prepTests;
    
    public SampleTestMethodDO(){
        
    }
    
    public SampleTestMethodDO(Integer id, String name, Integer methodId, String methodName, String description,
                  String reportingDescription, String isActive, Date activeBegin, Date activeEnd, String isReportable,
                  Integer timeTransit, Integer timeHolding, Integer timeTaAverage, Integer timeTaWarning, Integer timeTaMax,
                  Integer labelId, String labelName, Integer labelQty, Integer testTrailerId, String testTrailerName,
                  Integer scriptletId, String scriptletName, Integer testFormatId, Integer revisionMethodId, Integer reportingMethodId,
                  Integer sortingMethodId, Integer reportingSequence){
        
        test.setId(id);
        test.setName(name);
        test.setMethodId(methodId);
        test.setMethodName(methodName);
        test.setDescription(description);
        test.setReportingDescription(reportingDescription);
        test.setIsActive(isActive);
        test.setActiveBegin(Datetime.getInstance(Datetime.YEAR, Datetime.DAY, activeBegin));
        test.setActiveEnd(Datetime.getInstance(Datetime.YEAR, Datetime.DAY, activeEnd));
        test.setIsReportable(isReportable);
        test.setTimeTransit(timeTransit);
        test.setTimeHolding(timeHolding);
        test.setTimeTaAverage(timeTaAverage);
        test.setTimeTaWarning(timeTaWarning);
        test.setTimeTaMax(timeTaMax);
        test.setLabelId(labelId);
        test.setLabelName(labelName);
        test.setLabelQty(labelQty);
        test.setTestTrailerId(testTrailerId);
        test.setTestTrailerName(testTrailerName);
        test.setScriptletId(scriptletId);
        test.setScriptletName(scriptletName);
        test.setTestFormatId(testFormatId);
        test.setRevisionMethodId(revisionMethodId);
        test.setReportingMethodId(reportingMethodId);
        test.setSortingMethodId(sortingMethodId);
        test.setReportingSequence(reportingSequence);
    }
   
    public TestDO getTest() {
        return test;
    }

    public void setTest(TestDO test) {
        this.test = test;
    }

    public ArrayList<TestSectionDO> getSections() {
        return sections;
    }

    public void setSections(ArrayList<TestSectionDO> sections) {
        this.sections = sections;
    }

    public ArrayList<TestPrepDO> getPrepTests() {
        return prepTests;
    }

    public void setPrepTests(ArrayList<TestPrepDO> prepTests) {
        this.prepTests = prepTests;
    }
}
