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

import java.util.Map;

import org.openelis.constants.Messages;
import org.openelis.domain.AuxDataViewDO;
import org.openelis.domain.AuxFieldViewDO;
import org.openelis.domain.Constants;
import org.openelis.domain.PWSDO;
import org.openelis.manager.AuxFieldGroupManager;
import org.openelis.manager.AuxFieldManager;
import org.openelis.modules.pws.client.PWSService;
import org.openelis.ui.common.FormErrorException;
import org.openelis.ui.common.NotFoundException;
import org.openelis.ui.scriptlet.ScriptletInt;
import org.openelis.ui.scriptlet.ScriptletObject.Status;

/**
 * The scriptlet for validating PWS information entered through aux data
 */
public class PWSValidateScriptlet implements ScriptletInt<SampleSO> {

    private Proxy proxy;

    public PWSValidateScriptlet(Proxy proxy) {
        this.proxy = proxy;
    }

    @Override
    public SampleSO run(SampleSO data) {
        return validatePWS(data);
    }

    protected SampleSO validatePWS(SampleSO data) {
        int i;
        String uid;
        AuxDataViewDO aux;
        AuxFieldViewDO auxf;
        AuxFieldManager auxfm;

        /*
         * validate only if an aux data was changed
         */
        if (data.getAuxData() == null)
            return data;

        try {
            for (Map.Entry<Integer, AuxFieldGroupManager> entry : data.getAuxData().entrySet()) {
                uid = Constants.uid().getAuxData(entry.getKey());
                aux = (AuxDataViewDO)data.getManager().getObject(uid);
                auxfm = entry.getValue().getFields();
                /*
                 * find the aux data that was changed
                 */
                for (i = 0; i < auxfm.count(); i++ ) {
                    auxf = auxfm.getAuxFieldAt(i);
                    /*
                     * make sure that the scriptlet linked to the aux data is
                     * for validating PWS, and if it is, then try to fetch a PWS
                     * record whose number0 is the aux data's value
                     */
                    if (aux.getAuxFieldId().equals(auxf.getId()) &&
                        Constants.dictionary().SCRIPTLET_PWS_VALIDATE.equals(auxf.getScriptletId())) {
                        try {
                            proxy.fetchPwsByNumber0(aux.getValue());
                            break;
                        } catch (NotFoundException e) {
                            throw new FormErrorException(Messages.get().gen_invalidValueException());
                        }
                    }
                }
            }
        } catch (Exception e) {
            data.setStatus(Status.FAILED);
            data.addException(e);
        }
        return data;
    }

    public static interface Proxy {
        public PWSDO fetchPwsByNumber0(String value) throws Exception;
    }
}