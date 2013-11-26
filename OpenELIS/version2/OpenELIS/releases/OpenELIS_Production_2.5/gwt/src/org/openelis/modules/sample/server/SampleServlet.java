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
import org.openelis.ui.common.data.Query;
import org.openelis.ui.server.RemoteServlet;
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
        try {        
            return sampleManager.fetchById(sampleId);
        } catch (Exception anyE) {
            throw serializeForGWT(anyE);
        }
    }

    public SampleManager fetchByAccessionNumber(Integer accessionNumber) throws Exception {
        try {        
            return sampleManager.fetchByAccessionNumber(accessionNumber);
        } catch (Exception anyE) {
            throw serializeForGWT(anyE);
        }
    }
    
    public SampleManager fetchWithItemsAnalyses(Integer sampleId) throws Exception {
        try {        
            return sampleManager.fetchWithItemsAnalysis(sampleId);
        } catch (Exception anyE) {
            throw serializeForGWT(anyE);
        }
    }
    
    public SampleManager fetchWithAllDataById(Integer sampleId) throws Exception {
        try {        
            return sampleManager.fetchWithAllDataById(sampleId);
        } catch (Exception anyE) {
            throw serializeForGWT(anyE);
        }
    }
    
    public SampleManager fetchWithAllDataByAccessionNumber(Integer accessionNumber) throws Exception {
        try {        
            return sampleManager.fetchWithAllDataByAccessionNumber(accessionNumber);
        } catch (Exception anyE) {
            throw serializeForGWT(anyE);
        }
    }

    // sample methods
    public ArrayList<IdAccessionVO> query(Query query) throws Exception {          
        try {        
            return sample.query(query.getFields(), query.getPage() * query.getRowsPerPage(), query.getRowsPerPage());
        } catch (Exception anyE) {
            throw serializeForGWT(anyE);
        }
        
    }

    public SampleManager add(SampleManager man) throws Exception {
        try {        
            return sampleManager.add(man);
        } catch (Exception anyE) {
            throw serializeForGWT(anyE);
        }
    }

    public SampleManager update(SampleManager man) throws Exception {
        try {        
            return sampleManager.update(man);
        } catch (Exception anyE) {
            throw serializeForGWT(anyE);
        }
    }

    public SampleManager fetchForUpdate(Integer sampleId) throws Exception {
        try {        
            return sampleManager.fetchForUpdate(sampleId);
        } catch (Exception anyE) {
            throw serializeForGWT(anyE);
        }
    }

    public SampleManager abortUpdate(Integer sampleId) throws Exception {
        try {        
            return sampleManager.abortUpdate(sampleId);
        } catch (Exception anyE) {
            throw serializeForGWT(anyE);
        }
    }

    // sample org methods
    public SampleOrganizationManager fetchSampleOrganizationsBySampleId(Integer sampleId)
                                                                                         throws Exception {
        try {        
            return sampleManager.fetchSampleOrgsBySampleId(sampleId);
        } catch (Exception anyE) {
            throw serializeForGWT(anyE);
        }
    }

    // sample project methods
    public SampleProjectManager fetchSampleprojectsBySampleId(Integer sampleId) throws Exception {
        try {        
            return sampleManager.fetchSampleProjectsBySampleId(sampleId);
        } catch (Exception anyE) {
            throw serializeForGWT(anyE);
        }
    }

    // sample item methods
    public SampleItemManager fetchSampleItemsBySampleId(Integer sampleId) throws Exception {
        try {        
            return sampleManager.fetchSampleItemsBySampleId(sampleId);
        } catch (Exception anyE) {
            throw serializeForGWT(anyE);
        }
    }

    // sample qa method
    public SampleQaEventManager fetchBySampleId(Integer sampleId) throws Exception {
        try {        
            return sampleQaEventManager.fetchBySampleId(sampleId);
        } catch (Exception anyE) {
            throw serializeForGWT(anyE);
        }
    }

    public SampleManager validateAccessionNumber(SampleDO sampleDO) throws Exception {
        try {        
            return sampleManager.validateAccessionNumber(sampleDO);
        } catch (Exception anyE) {
            throw serializeForGWT(anyE);
        }
    }

    @Deprecated
    public Integer getNewAccessionNumber() throws Exception {
        SystemVariableDO sysVarDO;
        int tries, i;
        Integer value;

        // TODO: Need to completely rewrite this!!!

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
        try {        
            return pws.fetchByNumber0(number0);
        } catch (Exception anyE) {
            throw serializeForGWT(anyE);
        }
    }
}
