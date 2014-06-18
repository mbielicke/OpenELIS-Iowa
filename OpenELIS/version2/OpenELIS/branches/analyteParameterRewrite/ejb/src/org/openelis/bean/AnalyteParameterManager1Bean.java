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

import static org.openelis.manager.AnalyteParameterManager1Accessor.*;

import java.util.ArrayList;

import javax.annotation.security.RolesAllowed;
import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.jboss.security.annotation.SecurityDomain;
import org.openelis.domain.AnalyteParameterViewDO;
import org.openelis.domain.Constants;
import org.openelis.domain.QcViewDO;
import org.openelis.domain.TestViewDO;
import org.openelis.manager.AnalyteParameterManager1;
import org.openelis.utils.EJBFactory;

@Stateless
@SecurityDomain("openelis")
public class AnalyteParameterManager1Bean {

    @EJB
    private LockBean             lock;

    @EJB
    private AnalyteParameterBean analyteParameter;

    public AnalyteParameterManager1 getInstance(Integer referenceId, Integer referenceTableId,
                                                String referenceName) throws Exception {
        AnalyteParameterManager1 apm;

        apm = new AnalyteParameterManager1();
        setReferenceId(apm, referenceId);
        setReferenceTableId(apm, referenceTableId);
        setReferenceName(apm, referenceName);

        return apm;
    }

    public AnalyteParameterManager1 fetchByReferenceIdReferenceTableId(Integer referenceId,
                                                                       Integer referenceTableId) throws Exception {
        String refName;
        StringBuilder sb;
        TestViewDO test;
        QcViewDO qc;
        AnalyteParameterManager1 apm;
        ArrayList<AnalyteParameterViewDO> aps;

        refName = null;
        /*
         * fetch the record whose id is the reference id and set the reference
         * name
         */
        if (Constants.table().TEST.equals(referenceTableId)) {
            test = EJBFactory.getTest().fetchById(referenceId);
            sb = new StringBuilder();
            sb.append(test.getName());
            sb.append(", ");
            sb.append(test.getMethodName());
            /*
             * for inactive tests, show the active begin and end dates
             */
            if ("N".equals(test.getIsActive())) {
                sb.append(" [");
                sb.append(test.getActiveBegin());
                sb.append("..");
                sb.append(test.getActiveEnd());
                sb.append("]");
            }
            refName = sb.toString();
        } else if (Constants.table().QC.equals(referenceTableId)) {
            qc = EJBFactory.getQc().fetchById(referenceId);
            
            /*
             * for qcs, show the id next to the name
             */
            sb = new StringBuilder();
            sb.append(qc.getName());
            sb.append(" ");
            sb.append("(");
            sb.append(qc.getId());
            sb.append(")");
            
            refName = sb.toString();
        } else if (Constants.table().PROVIDER.equals(referenceTableId)) {
        }

        apm = getInstance(referenceId, referenceTableId, refName);
        /*
         * fetch and add the parameters
         */
        aps = analyteParameter.fetchByReferenceIdReferenceTableId(referenceId, referenceTableId);
        for (AnalyteParameterViewDO data : aps)
            addParameter(apm, data);

        return apm;
    }

    /**
     * Returns a locked analyte parameter manager with specified reference id
     * and reference table id. The reference table used to create the lock is
     * analyte parameter and not the passed value because we don't want to lock
     * the original record e.g. test or qc. This may end up locking the
     * parameters of a test if it has the same id as a qc and vice-versa, but
     * this is expected to be a very rare occurrence.
     */
    @RolesAllowed("analyteparameter-update")
    public AnalyteParameterManager1 fetchForUpdate(Integer referenceId, Integer referenceTableId) throws Exception {
        lock.lock(Constants.table().ANALYTE_PARAMETER, referenceId);
        return fetchByReferenceIdReferenceTableId(referenceId, referenceTableId);
    }

    /**
     * Unlocks and returns an analyte parameter manager with specified reference
     * id and reference table id. The reference table used to unlock is analyte
     * parameter and not the passed reference table.
     * 
     * @see fetchForUpdate
     */
    @RolesAllowed({"analyteparameter-add", "analyteparameter-update"})
    public AnalyteParameterManager1 unlock(Integer referenceId, Integer referenceTableId) throws Exception {
        lock.unlock(Constants.table().ANALYTE_PARAMETER, referenceId);
        return fetchByReferenceIdReferenceTableId(referenceId, referenceTableId);
    }
}