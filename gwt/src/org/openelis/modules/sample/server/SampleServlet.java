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
package org.openelis.modules.sample.server;

import java.util.ArrayList;

import javax.ejb.EJB;
import javax.servlet.annotation.WebServlet;

import org.openelis.bean.PWSBean;
import org.openelis.bean.SampleBean;
import org.openelis.bean.SampleManagerBean;
import org.openelis.bean.SampleQAEventManagerBean;
import org.openelis.bean.SystemVariableBean;
import org.openelis.domain.IdAccessionVO;
import org.openelis.domain.PWSDO;
import org.openelis.domain.SampleDO;
import org.openelis.domain.SystemVariableDO;
import org.openelis.gwt.common.data.Query;
import org.openelis.gwt.server.RemoteServlet;
import org.openelis.manager.SampleItemManager;
import org.openelis.manager.SampleManager;
import org.openelis.manager.SampleOrganizationManager;
import org.openelis.manager.SampleProjectManager;
import org.openelis.manager.SampleQaEventManager;
import org.openelis.modules.sample.client.SampleServiceInt;

@WebServlet("/openelis/sample")
public class SampleServlet extends RemoteServlet implements SampleServiceInt {
    
    private static final long serialVersionUID = 1L;
    
    @EJB
    SampleManagerBean        sampleManager;
    
    @EJB
    SampleBean               sample;
    
    @EJB
    SampleQAEventManagerBean sampleQaEventManager;
    
    @EJB
    PWSBean                   pws;
    
    @EJB
    SystemVariableBean        systemVariable;

    public SampleManager fetchById(Integer sampleId) throws Exception {
        return sampleManager.fetchById(sampleId);
    }

    public SampleManager fetchByAccessionNumber(Integer accessionNumber) throws Exception {
        return sampleManager.fetchByAccessionNumber(accessionNumber);
    }
    
    public SampleManager fetchWithItemsAnalyses(Integer sampleId) throws Exception {
        return sampleManager.fetchWithItemsAnalysis(sampleId);
    }
    
    public SampleManager fetchWithAllDataById(Integer sampleId) throws Exception {
        return sampleManager.fetchWithAllDataById(sampleId);
    }
    
    public SampleManager fetchWithAllDataByAccessionNumber(Integer accessionNumber) throws Exception {
        return sampleManager.fetchWithAllDataByAccessionNumber(accessionNumber);
    }

    // sample methods
    public ArrayList<IdAccessionVO> query(Query query) throws Exception {          
        return sample.query(query.getFields(), query.getPage() * query.getRowsPerPage(), query.getRowsPerPage());
        
    }

    public SampleManager add(SampleManager man) throws Exception {
        return sampleManager.add(man);
    }

    public SampleManager update(SampleManager man) throws Exception {
        return sampleManager.update(man);
    }

    public SampleManager fetchForUpdate(Integer sampleId) throws Exception {
        return sampleManager.fetchForUpdate(sampleId);
    }

    public SampleManager abortUpdate(Integer sampleId) throws Exception {
        return sampleManager.abortUpdate(sampleId);
    }

    // sample org methods
    public SampleOrganizationManager fetchSampleOrganizationsBySampleId(Integer sampleId)
                                                                                         throws Exception {
        return sampleManager.fetchSampleOrgsBySampleId(sampleId);
    }

    // sample project methods
    public SampleProjectManager fetchSampleprojectsBySampleId(Integer sampleId) throws Exception {
        return sampleManager.fetchSampleProjectsBySampleId(sampleId);
    }

    // sample item methods
    public SampleItemManager fetchSampleItemsBySampleId(Integer sampleId) throws Exception {
        return sampleManager.fetchSampleItemsBySampleId(sampleId);
    }

    // sample qa method
    public SampleQaEventManager fetchBySampleId(Integer sampleId) throws Exception {
        return sampleQaEventManager.fetchBySampleId(sampleId);
    }

    public SampleManager validateAccessionNumber(SampleDO sampleDO) throws Exception {
        return sampleManager.validateAccessionNumber(sampleDO);
    }

    public Integer getNewAccessionNumber() throws Exception {
        SystemVariableDO sysVarDO;
        int tries, i;
        Integer value;

        sysVarDO = null;
        tries = 5;
        value = null;
        i = 0;
        while (i < tries && sysVarDO == null) {
            try {
                sysVarDO = systemVariable.fetchForUpdateByName("last_accession_number");
                value = Integer.valueOf(sysVarDO.getValue());
                value = value + 1;
                sysVarDO.setValue(value.toString());
                systemVariable.update(sysVarDO);
            } catch (Exception e) {
                Thread.sleep(50);
            }
            i++ ;
        }

        return value;
    }
    
    public PWSDO fetchPwsByPwsId(String number0) throws Exception {
        return pws.fetchByNumber0(number0);
    }
}