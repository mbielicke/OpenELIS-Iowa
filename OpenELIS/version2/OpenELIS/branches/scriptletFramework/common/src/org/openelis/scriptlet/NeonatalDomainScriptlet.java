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
package org.openelis.scriptlet;

import org.openelis.domain.SampleNeonatalDO;
import org.openelis.manager.SampleManager1;
import org.openelis.manager.SampleManager1.Load;
import org.openelis.meta.SampleMeta;
import org.openelis.ui.common.Datetime;
import org.openelis.ui.common.NotFoundException;
import org.openelis.ui.scriptlet.ScriptletInt;
import org.openelis.ui.scriptlet.ScriptletObject.Status;

/**
 * The scriptlet for performing operations for the neonatal domain e.g. the ones
 * related to repeat samples
 */
public class NeonatalDomainScriptlet implements ScriptletInt<SampleSO> {

    private Proxy proxy;

    public NeonatalDomainScriptlet(Proxy proxy) {
        this.proxy = proxy;
    }

    @Override
    public SampleSO run(SampleSO data) {
        return checkRepeat(data);
    }

    protected SampleSO checkRepeat(SampleSO data) {
        String changed;
        SampleNeonatalDO sn;
        SampleManager1 sm;

        changed = data.getChanged();
        sm = data.getManager();
        sn = sm.getSampleNeonatal();
        /*
         * don't check for repeat if a field related to neonatal domain hasn't
         * changed or the sample is already marked as repeat or the patient is
         * not an existing one
         */
        if ( (changed.indexOf("sampleNeonatal") == -1 && changed.indexOf("neonatal") == -1) ||
            "Y".equals(sn.getIsRepeat()) || sn.getPatientId() == null)
            return data;

        try {
            proxy.fetchPreviousForNeonatalPatient(sn.getPatientId(), sm.getSample()
                                                                       .getEnteredDate());
            sn.setIsRepeat("Y");
            data.addRerun(SampleMeta.getNeonatalIsRepeat());
        } catch (NotFoundException e) {
            // ignore
        } catch (Exception e) {
            data.setStatus(Status.FAILED);
            data.addException(e);
        }
        return data;
    }

    public static interface Proxy {
        public SampleManager1 fetchPreviousForNeonatalPatient(Integer patientId,
                                                              Datetime enteredDate,
                                                              Load... elements) throws Exception;
    }
}
