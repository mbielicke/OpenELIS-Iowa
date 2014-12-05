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

import java.util.logging.Level;

import org.openelis.constants.Messages;
import org.openelis.domain.AuxDataViewDO;
import org.openelis.domain.DictionaryDO;
import org.openelis.domain.PWSDO;
import org.openelis.scriptlet.SampleSO.Action_Before;
import org.openelis.ui.common.FormErrorException;
import org.openelis.ui.common.NotFoundException;
import org.openelis.ui.scriptlet.ScriptletInt;
import org.openelis.ui.scriptlet.ScriptletObject.Status;

/**
 * The scriptlet for validating PWS information entered through aux data
 */
public class PwsValidateScriptlet1 implements ScriptletInt<SampleSO> {

    private Proxy               proxy;

    private Integer             auxDataId;

    public PwsValidateScriptlet1(Proxy proxy, Integer auxDataId) throws Exception {
        this.proxy = proxy;
        this.auxDataId = auxDataId;

        proxy.log(Level.FINE, "Initializing PwsValidateScriptlet1");
    }

    @Override
    public SampleSO run(SampleSO data) {
        proxy.log(Level.FINE, "In PwsValidateScriptlet1.run");
        /*
         * validate only if an aux data was changed
         */
        if ( !data.getActionBefore().contains(Action_Before.AUX_DATA))
            return data;

        validatePWS(data);
        return data;
    }

    /**
     * Validates whether the value of the aux data, that triggered this
     * scriptlet, is the number0 of an existing PWS record, if it's linked to
     * the scriptlet
     */
    private void validatePWS(SampleSO data) {
        AuxDataViewDO aux;

        try {
            proxy.log(Level.FINE,
                      "Finding the aux data and aux field that was changed to trigger the scriptlet");
            /*
             * get the changed aux data by using the uid in the SO and find out
             * if it's managed by this scriptlet; don't do anything if it's not
             */
            aux = (AuxDataViewDO)data.getManager().getObject(data.getUid());
            if ( !aux.getId().equals(auxDataId))
                return;
            /*
             * try to fetch a PWS record whose number0 is the aux data's value
             */
            proxy.log(Level.FINE, "Fetching PWS record for number0: " + aux.getValue());
            proxy.fetchPwsByNumber0(aux.getValue());
        } catch (NotFoundException e) {
            data.setStatus(Status.FAILED);
            data.addException(new FormErrorException(Messages.get().gen_invalidValueException()));
        } catch (Exception e) {
            data.setStatus(Status.FAILED);
            data.addException(e);
        }
    }

    public static interface Proxy {
        public DictionaryDO getDictionaryBySystemName(String systemName) throws Exception;

        public PWSDO fetchPwsByNumber0(String value) throws Exception;

        public void log(Level level, String message);
    }
}