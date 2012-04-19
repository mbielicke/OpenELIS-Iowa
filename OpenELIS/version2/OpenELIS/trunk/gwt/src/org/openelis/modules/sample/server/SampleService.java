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

import org.openelis.domain.IdAccessionVO;
import org.openelis.domain.PWSDO;
import org.openelis.domain.SampleDO;
import org.openelis.domain.SystemVariableDO;
import org.openelis.gwt.common.data.Query;
import org.openelis.manager.SampleItemManager;
import org.openelis.manager.SampleManager;
import org.openelis.manager.SampleOrganizationManager;
import org.openelis.manager.SampleProjectManager;
import org.openelis.manager.SampleQaEventManager;
import org.openelis.remote.SystemVariableRemote;
import org.openelis.server.EJBFactory;

public class SampleService {
    public SampleManager fetchById(Integer sampleId) throws Exception {
        return EJBFactory.getSampleManager().fetchById(sampleId);
    }

    public SampleManager fetchByAccessionNumber(Integer accessionNumber) throws Exception {
        return EJBFactory.getSampleManager().fetchByAccessionNumber(accessionNumber);
    }
    
    public SampleManager fetchWithItemsAnalyses(Integer sampleId) throws Exception {
        return EJBFactory.getSampleManager().fetchWithItemsAnalysis(sampleId);
    }
    
    public SampleManager fetchWithAllDataById(Integer sampleId) throws Exception {
        return EJBFactory.getSampleManager().fetchWithAllDataById(sampleId);
    }
    
    public SampleManager fetchWithAllDataByAccessionNumber(Integer accessionNumber) throws Exception {
        return EJBFactory.getSampleManager().fetchWithAllDataByAccessionNumber(accessionNumber);
    }

    // sample methods
    public ArrayList<IdAccessionVO> query(Query query) throws Exception {          
        return EJBFactory.getSample().query(query.getFields(), query.getPage() * query.getRowsPerPage(), query.getRowsPerPage());
        
    }

    public SampleManager add(SampleManager man) throws Exception {
        return EJBFactory.getSampleManager().add(man);
    }

    public SampleManager update(SampleManager man) throws Exception {
        return EJBFactory.getSampleManager().update(man);
    }

    public SampleManager fetchForUpdate(Integer sampleId) throws Exception {
        return EJBFactory.getSampleManager().fetchForUpdate(sampleId);
    }

    public SampleManager abortUpdate(Integer sampleId) throws Exception {
        return EJBFactory.getSampleManager().abortUpdate(sampleId);
    }

    // sample org methods
    public SampleOrganizationManager fetchSampleOrganizationsBySampleId(Integer sampleId)
                                                                                         throws Exception {
        return EJBFactory.getSampleManager().fetchSampleOrgsBySampleId(sampleId);
    }

    // sample project methods
    public SampleProjectManager fetchSampleprojectsBySampleId(Integer sampleId) throws Exception {
        return EJBFactory.getSampleManager().fetchSampleProjectsBySampleId(sampleId);
    }

    // sample item methods
    public SampleItemManager fetchSampleItemsBySampleId(Integer sampleId) throws Exception {
        return EJBFactory.getSampleManager().fetchSampleItemsBySampleId(sampleId);
    }

    // sample qa method
    public SampleQaEventManager fetchBySampleId(Integer sampleId) throws Exception {
        return EJBFactory.getSampleQAEventManager().fetchBySampleId(sampleId);
    }

    public SampleManager validateAccessionNumber(SampleDO sampleDO) throws Exception {
        return EJBFactory.getSampleManager().validateAccessionNumber(sampleDO);
    }

    public Integer getNewAccessionNumber() throws Exception {
        SystemVariableDO sysVarDO;
        int tries, i;
        Integer value;
        SystemVariableRemote svRemote;

        sysVarDO = null;
        tries = 5;
        value = null;
        i = 0;
        svRemote = EJBFactory.getSystemVariable();
        while (i < tries && sysVarDO == null) {
            try {
                sysVarDO = svRemote.fetchForUpdateByName("last_accession_number");
                value = Integer.valueOf(sysVarDO.getValue());
                value = value + 1;
                sysVarDO.setValue(value.toString());
                svRemote.update(sysVarDO);
            } catch (Exception e) {
                Thread.sleep(50);
            }
            i++ ;
        }

        return value;
    }
    
    public PWSDO fetchPwsByPwsId(String number0) throws Exception {
        return EJBFactory.getPWS().fetchByNumber0(number0);
    }
}