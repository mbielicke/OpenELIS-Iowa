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
package org.openelis.domain;

import java.util.Date;

import org.openelis.gwt.common.DataBaseUtil;

/**
 * The class extends test DO and carries several commonly used fields such as
 * method, trailer, and scriptlet names. The additional fields are for
 * read/display only and do not get committed to the database. Note: isChanged
 * will reflect any changes to read/display fields.
 */

public class TestViewDO extends TestDO {

    private static final long serialVersionUID = 1L;

    protected String          methodName, labelName, trailerName, scriptletName;

    public TestViewDO() {
    }

    public TestViewDO(Integer id, String name, String description, String reportingDescription,
                        Integer methodId, String isActive, Date activeBegin, Date activeEnd,
                        String isReportable, Integer timeTransit, Integer timeHolding,
                        Integer timeTaAverage, Integer timeTaWarning, Integer timeTaMax,
                        Integer labelId, Integer labelQty, Integer testTrailerId,
                        Integer scriptletId, Integer testFormatId, Integer revisionMethodId,
                        Integer reportingMethodId, Integer sortingMethodId,
                        Integer reportingSequence, String methodName, String labelName,
                        String testTrailerName, String scriptletName) {
        super(id, name, description, reportingDescription, methodId, isActive, activeBegin,
              activeEnd, isReportable, timeTransit, timeHolding, timeTaAverage, timeTaWarning,
              timeTaMax, labelId, labelQty, testTrailerId, scriptletId, testFormatId,
              revisionMethodId, reportingMethodId, sortingMethodId, reportingSequence);
        setMethodName(methodName);
        setLabelName(labelName);
        setTrailerName(testTrailerName);
        setScriptletName(scriptletName);
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = DataBaseUtil.trim(methodName);
    }

    public String getLabelName() {
        return labelName;
    }

    public void setLabelName(String labelName) {
        this.labelName = DataBaseUtil.trim(labelName);
    }

    public String getTrailerName() {
        return trailerName;
    }

    public void setTrailerName(String trailerName) {
        this.trailerName = DataBaseUtil.trim(trailerName);
    }

    public String getScriptletName() {
        return scriptletName;
    }

    public void setScriptletName(String scriptletName) {
        this.scriptletName = DataBaseUtil.trim(scriptletName);
    }
}
