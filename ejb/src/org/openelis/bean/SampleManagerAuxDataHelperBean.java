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

import java.util.ArrayList;

import org.openelis.domain.AuxDataViewDO;
import org.openelis.domain.AuxFieldValueViewDO;
import org.openelis.domain.AuxFieldViewDO;
import org.openelis.domain.Constants;
import org.openelis.manager.AuxFieldManager;
import org.openelis.manager.AuxFieldValueManager;

/**
 * This class is used for adding aux data to or removing it from a sample
 */

public class SampleManagerAuxDataHelperBean {

    /**
     * Adds the aux groups specified by the list of ids to the list of aux data.
     */
    public void addAuxGroups(ArrayList<AuxDataViewDO> auxiliary, ArrayList<Integer> groupIds) throws Exception {
        int i;
        AuxDataViewDO aux;
        AuxFieldViewDO af;
        AuxFieldManager afm;

        /*
         * fields for the aux group are fetched and aux data for them is added
         * to the sample
         */
        for (Integer id : groupIds) {
            afm = AuxFieldManager.fetchByGroupIdWithValues(id);
            for (i = 0; i < afm.count(); i++ ) {
                af = afm.getAuxFieldAt(i);
                if ("N".equals(af.getIsActive()))
                    continue;
                aux = new AuxDataViewDO();
                aux.setAuxFieldId(af.getId());
                aux.setGroupId(id);
                aux.setAnalyteId(af.getAnalyteId());
                aux.setAnalyteName(af.getAnalyteName());
                // TODO validate the value and set the type
                aux.setIsReportable(af.getIsReportable());
                aux.setValue(getDefault(afm.getValuesAt(i)));

                auxiliary.add(aux);
            }
        }
    }

    /**
     * Removes the groups specified by the ids from the list of aux data. Adds
     * the removed objects to the list returned.
     */
    public ArrayList<AuxDataViewDO> removeAuxGroups(ArrayList<AuxDataViewDO> auxiliary,
                                                    ArrayList<Integer> groupIds) {
        boolean remove;
        Integer prevId;
        ArrayList<AuxDataViewDO> removeList;

        removeList = new ArrayList<AuxDataViewDO>();
        prevId = null;
        remove = false;

        for (AuxDataViewDO aux : auxiliary) {
            if ( !aux.getGroupId().equals(prevId)) {
                /*
                 * to avoid unneccessary linear search, the following check is
                 * performed only when the group changes
                 */
                remove = groupIds.contains(aux.getGroupId());
                prevId = aux.getGroupId();
            }
            if (remove) {
                removeList.add(aux);
                auxiliary.remove(aux);
            }
        }

        return removeList;
    }

    /**
     * Returns the default value, if any, defined for a particular aux field
     */
    private String getDefault(AuxFieldValueManager afvm) {
        AuxFieldValueViewDO afv;

        if (afvm.count() == 0)
            return null;

        for (int i = 0; i < afvm.count(); i++ ) {
            afv = afvm.getAuxFieldValueAt(i);
            if (Constants.dictionary().AUX_DEFAULT.equals(afv.getTypeId()))
                return afv.getValue();
        }

        return null;
    }
}
