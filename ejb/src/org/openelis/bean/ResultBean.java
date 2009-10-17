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
package org.openelis.bean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.annotation.Resource;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.jboss.annotation.security.SecurityDomain;
import org.openelis.domain.AnalyteDO;
import org.openelis.domain.ResultViewDO;
import org.openelis.domain.TestAnalyteViewDO;
import org.openelis.domain.TestResultDO;
import org.openelis.gwt.common.NotFoundException;
import org.openelis.local.ResultLocal;

@Stateless

@SecurityDomain("openelis")
//@RolesAllowed("LOCKINGtest")
public class ResultBean implements ResultLocal {
    
    @PersistenceContext(name = "openelis")
    private EntityManager manager;
   
    @Resource
    private SessionContext ctx;

    public void add(TestAnalyteViewDO itemDO) {
        // TODO Auto-generated method stub
        
    }

    public void update(TestAnalyteViewDO itemDO) {
        // TODO Auto-generated method stub
        
    }
    
    public void delete(TestAnalyteViewDO itemDO) {
        // TODO Auto-generated method stub
        
    }

    public void fetchByTestIdNoResults(Integer testId, ArrayList<ArrayList<ResultViewDO>> results,
                                  HashMap<Integer, TestAnalyteViewDO> testAnalyteList, 
                                  HashMap<Integer, TestResultDO> testResultList,
                                  HashMap<Integer, AnalyteDO> analyteList) throws Exception {
        List<TestAnalyteViewDO> testAnalytes = null;
        List<TestResultDO> testResults = null;
        List<AnalyteDO> analytes = null;
        
        //get test_analytes by test id
        Query query = manager.createNamedQuery("TestAnalyte.TestAnalyteDOListByTestId");
        query.setParameter("testId", testId);
        testAnalytes = query.getResultList();
        
        //get test_results by test id
        query = manager.createNamedQuery("TestResult.FetchByTestId");
        query.setParameter("testId", testId);
        testResults = query.getResultList();
        
        //get analytes for test
        query = manager.createNamedQuery("TestAnalyte.AnalyteByTestId");
        query.setParameter("testId", testId);
        analytes = query.getResultList();
        
        //convert the lists to hashmaps
        testAnalyteList.clear();
        TestAnalyteViewDO testAnalyteDO;
        for(int i=0; i <testAnalytes.size(); i++){
            testAnalyteDO = testAnalytes.get(i);
            testAnalyteList.put(testAnalyteDO.getId(), testAnalyteDO);
        }
        
        testResultList.clear();
        TestResultDO testResultDO;
        for(int j=0; j<testResults.size(); j++){
            testResultDO = testResults.get(j);
            testResultList.put(testResultDO.getId(), testResultDO);
        }
        
        analyteList.clear();
        AnalyteDO analyteDO;
        for(int k=0; k<analytes.size(); k++){
            analyteDO = analytes.get(k);
            analyteList.put(analyteDO.getId(), analyteDO);
        }
        
        //build the grid
        int i, j, rg;
        TestAnalyteViewDO ado;
        ArrayList<ResultViewDO> ar;

        j = -1;
        ar = null;
        results.clear();

        if (testAnalytes == null || testAnalytes.size() == 0)
            throw new NotFoundException();

        for (i = 0; i < testAnalytes.size(); i++ ) {
            ado = testAnalytes.get(i);
            //create a new resultDO
            ResultViewDO resultDO = new ResultViewDO();
            resultDO.setTestAnalyteId(ado.getId());
            resultDO.setAnalyte(ado.getAnalyteName());
            //resultDO.setTestResultId(testResultId);
            resultDO.setIsColumn(ado.getIsColumn());
            resultDO.setSortOrder(ado.getSortOrder());
            resultDO.setIsReportable(ado.getIsReportable());
            resultDO.setTypeId(ado.getTypeId());
            
            rg = ado.getRowGroup();
            resultDO.setRowGroup(rg);

            if (j != rg) {
                ar = new ArrayList<ResultViewDO>(1);
                ar.add(resultDO);
                results.add(ar);
                j = rg;
                continue;
            }
            if ("N".equals(ado.getIsColumn())) {
                ar = new ArrayList<ResultViewDO>(1);
                ar.add(resultDO);
                results.add(ar);
                continue;
            }

            ar.add(resultDO);
        }
    }
    
    public void fetchByAnalysisId(Integer analysisId, ArrayList<ArrayList<ResultViewDO>> results,
                                  HashMap<Integer, AnalyteDO> analyteList) throws Exception {
        List<AnalyteDO> analytes = null;
        List<ResultViewDO> rslts = null;
        
        //get analytes by analysis id
        Query query = manager.createNamedQuery("Result.AnalyteByAnalysisId");
        query.setParameter("id", analysisId);
        analytes = query.getResultList();
        
        //get results by analysis id
        query = manager.createNamedQuery("Result.FetchByAnalysisId");
        query.setParameter("id", analysisId);
        rslts = query.getResultList();
        
        //convert the lists to hashmaps
        analyteList.clear();
        AnalyteDO analyteDO;
        for(int k=0; k<analytes.size(); k++){
            analyteDO = analytes.get(k);
            analyteList.put(analyteDO.getId(), analyteDO);
        }
        
        //build the grid
        int i, j, rg;
        ResultViewDO rdo;
        ArrayList<ResultViewDO> ar;

        j = -1;
        ar = null;
        results.clear();

        if (rslts == null || rslts.size() == 0)
            throw new NotFoundException();

        for (i = 0; i < rslts.size(); i++ ) {
            rdo = rslts.get(i);
            
            rg = rdo.getRowGroup();

            if (j != rg) {
                ar = new ArrayList<ResultViewDO>(1);
                ar.add(rdo);
                results.add(ar);
                j = rg;
                continue;
            }
            if ("N".equals(rdo.getIsColumn())) {
                ar = new ArrayList<ResultViewDO>(1);
                ar.add(rdo);
                results.add(ar);
                continue;
            }

            ar.add(rdo);
        }
    }
}
