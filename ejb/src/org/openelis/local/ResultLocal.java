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
package org.openelis.local;

import java.util.ArrayList;
import java.util.HashMap;

import javax.ejb.Local;

import org.openelis.domain.AnalyteDO;
import org.openelis.domain.ResultViewDO;
import org.openelis.domain.TestResultDO;
import org.openelis.manager.AnalysisResultManager.TestAnalyteListItem;
import org.openelis.utilcommon.ResultValidator;

@Local
public interface ResultLocal {
    public void fetchByTestIdNoResults(Integer testId, Integer unitId, ArrayList<ArrayList<ResultViewDO>> results,
                                       HashMap<Integer, TestResultDO> testResultList, HashMap<Integer, AnalyteDO> analyteList, 
                                       HashMap<Integer, TestAnalyteListItem> testAnalyteList, ArrayList<ResultValidator> resultValidators) throws Exception;
    public void fetchByAnalysisIdForDisplay(Integer analysisId, ArrayList<ArrayList<ResultViewDO>> results) throws Exception;
    public void fetchByAnalysisId(Integer analysisId, ArrayList<ArrayList<ResultViewDO>> results, 
                                  HashMap<Integer, TestResultDO> testResultList, HashMap<Integer, AnalyteDO> analyteList, 
                                  HashMap<Integer, TestAnalyteListItem> testAnalyteList, ArrayList<ResultValidator> resultValidators) throws Exception;
    public ArrayList<ArrayList<ResultViewDO>> fetchReportableByAnalysisId(Integer analysisId) throws Exception;
    
    public ResultViewDO add(ResultViewDO data);
    public ResultViewDO update(ResultViewDO data);
    public void delete(ResultViewDO data);
}
