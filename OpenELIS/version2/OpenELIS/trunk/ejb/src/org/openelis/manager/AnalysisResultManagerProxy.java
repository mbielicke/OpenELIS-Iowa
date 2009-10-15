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
package org.openelis.manager;

import java.util.ArrayList;
import java.util.HashMap;

import javax.naming.InitialContext;

import org.openelis.domain.AnalysisViewDO;
import org.openelis.domain.AnalyteDO;
import org.openelis.domain.ResultViewDO;
import org.openelis.domain.TestAnalyteViewDO;
import org.openelis.domain.TestResultDO;
import org.openelis.gwt.common.ValidationErrorsList;
import org.openelis.local.AnalysisLocal;
import org.openelis.local.ResultLocal;
import org.openelis.utils.ReferenceTableCache;

public class AnalysisResultManagerProxy {
    public AnalysisResultManager fetchByAnalysisId(Integer analysisId) throws Exception {
        return null;
        /*
        ArrayList<ArrayList<TestAnalyteViewDO>> grid = 
            (ArrayList<ArrayList<TestAnalyteViewDO>>)local().fetchByAnalysisId(analysisId);
        AnalysisResultManager arm = AnalysisResultManager.getInstance();
        arm.setResults(grid);
        
        arm.setAnalysisId(analysisId);

        return arm;*/
    }
    
    public AnalysisResultManager fetchNewByTestId(Integer testId) throws Exception {
        System.out.println("1");
        ArrayList<ArrayList<ResultViewDO>> results = new ArrayList<ArrayList<ResultViewDO>>();
        System.out.println("2");
        HashMap<Integer, TestAnalyteViewDO> testAnalyteList = new HashMap<Integer, TestAnalyteViewDO>();
        System.out.println("3");
        HashMap<Integer, TestResultDO> testResultList = new HashMap<Integer, TestResultDO>();
        System.out.println("4");
        HashMap<Integer, AnalyteDO> analyteList = new HashMap<Integer, AnalyteDO>();
        System.out.println("5");
        System.out.println("["+results+"]");
        local().fetchByTestIdNoResults(testId, results, testAnalyteList, testResultList, analyteList);
        System.out.println("["+results+"]");
        AnalysisResultManager man = AnalysisResultManager.getInstance();
        man.setResults(results);
        man.setTestAnalyteList(testAnalyteList);
        man.setTestResultList(testResultList);
        man.setAnalyteList(analyteList);
        
        return man;
    }
    
    public AnalysisResultManager add(AnalysisResultManager man) throws Exception {
        return null;
    }
    
    public AnalysisResultManager update(AnalysisResultManager man) throws Exception {
        return null;
    }
    
    public void validate(AnalysisResultManager man, ValidationErrorsList errorsList) throws Exception {
        
    }
    
    private ResultLocal local(){
        try{
            InitialContext ctx = new InitialContext();
            return (ResultLocal)ctx.lookup("openelis/ResultBean/local");
        }catch(Exception e){
             System.out.println(e.getMessage());
             return null;
        }
    }
}
