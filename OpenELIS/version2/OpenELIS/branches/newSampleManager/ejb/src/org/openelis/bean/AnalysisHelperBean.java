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
package org.openelis.bean;

import static org.openelis.manager.SampleManager1Accessor.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.jboss.security.annotation.SecurityDomain;
import org.openelis.constants.Messages;
import org.openelis.domain.AnalysisViewDO;
import org.openelis.domain.Constants;
import org.openelis.domain.ResultViewDO;
import org.openelis.domain.SampleItemViewDO;
import org.openelis.domain.TestAnalyteViewDO;
import org.openelis.domain.TestSectionViewDO;
import org.openelis.domain.TestTypeOfSampleDO;
import org.openelis.domain.TestViewDO;
import org.openelis.manager.SampleManager1;
import org.openelis.manager.TestAnalyteManager;
import org.openelis.manager.TestManager;
import org.openelis.manager.TestPrepManager;
import org.openelis.ui.common.Datetime;
import org.openelis.ui.common.FormErrorWarning;
import org.openelis.ui.common.NotFoundException;
import org.openelis.ui.common.ValidationErrorsList;

/**
 * This class is used to provide various functionalities related to analyses in
 * a generic manner
 */

@Stateless
@SecurityDomain("openelis")
public class AnalysisHelperBean {

    @EJB
    private TestBean            test;

    private static final Logger log = Logger.getLogger("openelis");

    /**
     * For each test in the VO that passes validation, adds an analysis and
     * results to the manager. Adds validation errors, to the returned VO
     */
    public AnalysisViewDO addAnalysis(SampleManager1 sm, Integer itemId, Integer addTestId,                                       
                                       ValidationErrorsList e) throws Exception {
        AnalysisViewDO ana;
        TestViewDO t;
        TestManager tm;
        TestSectionViewDO ts;
        ArrayList<TestTypeOfSampleDO> types;

        try {
            tm = TestManager.fetchWithPrepTestsSampleTypes(addTestId);
            t = tm.getTest();
        } catch (Exception ex) {
            log.log(Level.SEVERE, "Missing/invalid test '" + addTestId + "'", ex);
            throw ex;
        }

        /*
         * if test not active, try to find an active test with this name and
         * method
         */
        ana = null;
        if ("N".equals(t.getIsActive())) {
            try {
                t = test.fetchActiveByNameMethodName(t.getName(), t.getMethodName());
                tm = TestManager.fetchWithPrepTestsSampleTypes(t.getId());
                t = tm.getTest();
            } catch (NotFoundException ex) {
                e.add(new FormErrorWarning(Messages.get()
                                                   .inactiveTestOnOrderException(t.getName(),
                                                                                 t.getMethodName())));
                return ana;
            }
        }
        /*
         * find out if this user has permission to add this test
         */
        ts = tm.getTestSections().getDefaultSection();
        if (ts == null || !tm.canAssignThisSection(ts)) {
            e.add(new FormErrorWarning(Messages.get()
                                               .insufficientPrivilegesAddTest(t.getName(),
                                                                              t.getMethodName())));
        }

        /*
         * create the analysis and set the default unit
         */
        ana = new AnalysisViewDO();
        ana.setId(sm.getNextUID());
        ana.setRevision(0);
        ana.setTestId(t.getId());
        ana.setTestName(t.getName());
        ana.setMethodId(t.getMethodId());
        ana.setMethodName(t.getMethodName());
        ana.setIsReportable(t.getIsReportable());
        ana.setIsPreliminary("N");

        for (SampleItemViewDO item : getItems(sm)) {
            if (item.getId().equals(itemId)) {
                ana.setSampleItemId(item.getId());
                /*
                 * the first unit within the sample type is the default unit
                 */
                types = tm.getSampleTypes().getTypesBySampleType(item.getTypeOfSampleId());
                if (types.size() > 0)
                    ana.setUnitOfMeasureId(types.get(0).getUnitOfMeasureId());
                break;
            }
        }

        /*
         * if there is a default section then set it
         */
        if (ts != null)
            ana.setSectionId(ts.getSectionId());

        org.openelis.manager.SampleManager1Accessor.addAnalysis(sm, ana);
        
        addResults(sm, tm, ana);
        
        return ana;
    }

    public void addResults(SampleManager1 sm, TestManager tm, AnalysisViewDO ana) throws Exception {
        ResultViewDO r;
        TestAnalyteManager tam;

        tam = tm.getTestAnalytes();
        for (ArrayList<TestAnalyteViewDO> tas : tam.getAnalytes()) {
            for (TestAnalyteViewDO ta : tas) {
                r = new ResultViewDO();
                r.setId(sm.getNextUID());
                r.setTestAnalyteId(ta.getId());
                r.setTestAnalyteTypeId(ta.getTypeId());
                r.setIsColumn(ta.getIsColumn());
                r.setIsReportable(ta.getIsReportable());
                r.setAnalyteId(ta.getAnalyteId());
                r.setAnalyte(ta.getAnalyteName());
                r.setResultGroup(ta.getResultGroup());
                r.setRowGroup(ta.getRowGroup());
                addResult(sm, r);
            }
        }
    }
    
    public ArrayList<Integer> findPrepTests(HashMap<Integer, AnalysisViewDO> analyses,
                                            AnalysisViewDO ana, TestManager tm,
                                            ArrayList<Integer> prepIds) throws Exception {
         int i;
         boolean foundPrep;
         AnalysisViewDO prep;
         TestPrepManager tpm;
         /*
          * if this test requires prep tests, first look in the list of existing
          * analyses otherwise add the prep test to the list shown to the user to
          * choose a prep test
          */
         
         tpm = tm.getPrepTests();
         foundPrep = false;
         for (i = 0; i < tpm.count(); i++ ) {
             prep = analyses.get(tpm.getPrepAt(i).getPrepTestId());
             if (prep != null) {
                 ana.setPreAnalysisId(prep.getId());
                 ana.setPreAnalysisTest(prep.getTestName());
                 ana.setPreAnalysisMethod(prep.getMethodName());
                 if (Constants.dictionary().ANALYSIS_COMPLETED.equals(prep.getStatusId()) ||
                     Constants.dictionary().ANALYSIS_RELEASED.equals(prep.getStatusId())) {
                     ana.setStatusId(Constants.dictionary().ANALYSIS_LOGGED_IN);
                     ana.setAvailableDate(Datetime.getInstance(Datetime.YEAR, Datetime.MINUTE));
                 } else {
                     ana.setStatusId(Constants.dictionary().ANALYSIS_INPREP);
                 }
                 foundPrep = true;
                 break;
             }
         }

         if ( !foundPrep && tpm.count() > 0) {
             prepIds = new ArrayList<Integer>();
             for (i = 0; i < tpm.count(); i++ )
                 prepIds.add(tpm.getPrepAt(i).getPrepTestId());            
         }
         return prepIds;
     }
}