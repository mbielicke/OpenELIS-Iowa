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

import org.openelis.domain.AnalyteDO;
import org.openelis.domain.ResultViewDO;
import org.openelis.domain.TestAnalyteViewDO;
import org.openelis.domain.TestResultDO;
import org.openelis.gwt.common.ValidationErrorsList;
import org.openelis.local.ResultLocal;
import org.openelis.manager.AnalysisResultManager.TestAnalyteListItem;
import org.openelis.utilcommon.ResultValidator;

public class AnalysisResultManagerProxy {
    
    public AnalysisResultManager fetchByAnalysisIdForDisplay(Integer analysisId) throws Exception {
        ArrayList<ArrayList<ResultViewDO>> results = new ArrayList<ArrayList<ResultViewDO>>();
        
        local().fetchByAnalysisIdForDisplay(analysisId, results);
        AnalysisResultManager man = AnalysisResultManager.getInstance();
        man.setResults(results);
        
        return man;
    }
    
    public AnalysisResultManager fetchByAnalysisId(Integer analysisId, Integer testId) throws Exception {
        ArrayList<ArrayList<ResultViewDO>> results = new ArrayList<ArrayList<ResultViewDO>>();
        HashMap<Integer, TestResultDO> testResultList = new HashMap<Integer, TestResultDO>();
        HashMap<Integer, AnalyteDO> analyteList = new HashMap<Integer, AnalyteDO>();
        HashMap<Integer, TestAnalyteListItem> testAnalyteList = new HashMap<Integer, TestAnalyteListItem>();
        ArrayList<ResultValidator> resultValidators = new ArrayList<ResultValidator>();
        
        local().fetchByAnalysisId(analysisId, results, testResultList, analyteList, testAnalyteList, resultValidators);
        AnalysisResultManager man = AnalysisResultManager.getInstance();
        man.setResults(results);
        man.setTestResultList(testResultList);
        man.setAnalyteList(analyteList);
        man.setTestAnalyteList(testAnalyteList);
        man.setResultValidators(resultValidators);
        man.setTestManager(TestManager.fetchWithAnalytesAndResults(testId));
        
        return man;
    }
    
    public AnalysisResultManager fetchNewByTestId(Integer testId) throws Exception {
        ArrayList<ArrayList<ResultViewDO>> results = new ArrayList<ArrayList<ResultViewDO>>();
        HashMap<Integer, TestResultDO> testResultList = new HashMap<Integer, TestResultDO>();
        HashMap<Integer, AnalyteDO> analyteList = new HashMap<Integer, AnalyteDO>();
        HashMap<Integer, TestAnalyteListItem> testAnalyteList = new HashMap<Integer, TestAnalyteListItem>();
        ArrayList<ResultValidator> resultValidators = new ArrayList<ResultValidator>();
        
        local().fetchByTestIdNoResults(testId, results, testResultList, analyteList, testAnalyteList, resultValidators);
        AnalysisResultManager man = AnalysisResultManager.getInstance();
        man.setResults(results);
        man.setTestResultList(testResultList);
        man.setAnalyteList(analyteList);
        man.setTestAnalyteList(testAnalyteList);
        man.setResultValidators(resultValidators);
        man.setTestManager(TestManager.fetchWithAnalytesAndResults(testId));
        
        return man;
    }
    
    public AnalysisResultManager add(AnalysisResultManager man) throws Exception {
        ResultLocal rl;
        ArrayList<ResultViewDO> list;
        ArrayList<ArrayList<ResultViewDO>> grid;
        ResultViewDO data;
        int i, j, so;

        rl = local();
        grid = man.getResults();
        so = 0;
        for (i = 0; i < man.rowCount(); i++ ) {
            list = grid.get(i);
            for (j = 0; j < list.size(); j++ ) {
                data = list.get(j);
                
                data.setAnalysisId(man.getAnalysisId());
                data.setSortOrder( ++so);
                
                System.out.println("["+data.getId()+"]");
                System.out.println("["+data.getAnalysisId()+"]");
                System.out.println("["+data.getTestAnalyteId()+"]");
                System.out.println("["+data.getTestResultId()+"]");
                System.out.println("["+data.getIsColumn()+"]");
                System.out.println("["+data.getSortOrder()+"]");
                System.out.println("["+data.getIsReportable()+"]");
                System.out.println("["+data.getAnalyteId()+"]");
                System.out.println("["+data.getTypeId()+"]");
                System.out.println("["+data.getValue()+"]");
                
                rl.add(data);
            }
        }

        return man;
    }
    
    public AnalysisResultManager update(AnalysisResultManager man) throws Exception {
        ResultLocal rl;
        ArrayList<ResultViewDO> list;
        ArrayList<ArrayList<ResultViewDO>> grid;
        ResultViewDO data;
        int i, j, so;

        rl = local();
        grid = man.getResults();
        so = 0;
        
        for (i = 0; i < man.deleteCount(); i++ ) {
            rl.delete(man.getDeletedAt(i));
        }

        for (i = 0; i < man.rowCount(); i++ ) {
            list = grid.get(i);
            for (j = 0; j < list.size(); j++ ) {
                data = list.get(j);
                data.setSortOrder( ++so);
                if (data.getId() == null) {
                    System.out.println("88888888888888888888888add");
                    data.setAnalysisId(man.getAnalysisId());
                    rl.add(data);
                } else {
                    System.out.println("7777777777777777update");
                    rl.update(data);
                }
            }
        }

        return man;
    }
    
    public ArrayList<AnalyteDO> getAlias(Integer analyteId) throws Exception {
        throw new UnsupportedOperationException();
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
