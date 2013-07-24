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
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import javax.ejb.Stateless;

import org.jboss.security.annotation.SecurityDomain;
import org.openelis.domain.AuxDataViewDO;
import org.openelis.domain.AuxFieldValueViewDO;
import org.openelis.domain.AuxFieldViewDO;
import org.openelis.domain.Constants;
import org.openelis.manager.AuxFieldManager;
import org.openelis.manager.AuxFieldValueManager;

/**
 * This class is used for adding or removing aux data associated with a sample,
 * order etc.
 */

@Stateless
@SecurityDomain("openelis")
public class AuxDataHelperBean {

    /**
     * Adds aux groups specified by the list of ids to the list of aux data, if
     * the group isn't already present in the list of aux data.
     */
    public void addAuxGroups(ArrayList<AuxDataViewDO> auxiliary, Set<Integer> groupIds) throws Exception {
        Set<Integer> addIds;

        /*
         * make sure that only the groups not already in the list of aux data
         * get added to it
         */
        addIds = getDifference(auxiliary, groupIds);

        addAuxGroups(auxiliary, addIds, null);
    }

    /**
     * Adds aux groups specified by the keys in the map to the list of aux data.
     * If the aux data's analyte can be found in the map then the value is set
     * from the map, otherwise it's set as the default of the corresponding aux
     * field in the group.
     */
    public void addAuxGroups(ArrayList<AuxDataViewDO> auxiliary,
                             HashMap<Integer, HashMap<Integer, AuxDataViewDO>> grpMap) throws Exception {
        Set<Integer> addIds;

        /*
         * make sure that only the groups not already in the list of aux data
         * get added to it
         */
        addIds = getDifference(auxiliary, grpMap.keySet());

        addAuxGroups(auxiliary, addIds, grpMap);
    }

    /**
     * Removes the groups specified by the ids from the list of aux data. Adds
     * the removed objects to the list returned.
     */
    public ArrayList<AuxDataViewDO> removeAuxGroups(ArrayList<AuxDataViewDO> auxiliary,
                                                    Set<Integer> groupIds) {
        Integer prevId;        
        ArrayList<AuxDataViewDO> removed;

        removed = new ArrayList<AuxDataViewDO>();
        for (int i = 0; i < auxiliary.size(); i++ ) {
            prevId = auxiliary.get(i).getGroupId();
            if (groupIds.contains(prevId)) {
                do {
                    removed.add(auxiliary.remove(i));
                } while (i < auxiliary.size() && auxiliary.get(i).getGroupId().equals(prevId));
            }
        }

        return removed;
    }

    /**
     * Adds aux group specified by the id to the list of aux data. If the aux
     * data's analyte can be found in the map then the value is set from the
     * map, otherwise it's set as the default of the corresponding aux field in
     * the group.
     */
    private void addAuxGroups(ArrayList<AuxDataViewDO> auxiliary, Set<Integer> addIds,
                              HashMap<Integer, HashMap<Integer, AuxDataViewDO>> grps) throws Exception {
        HashMap<Integer, AuxDataViewDO> auxMap;
        AuxFieldViewDO af;
        AuxFieldManager afm;
        AuxDataViewDO aux1, aux2;

        for (Integer id : addIds) {
            auxMap = null;
            if (grps != null)
                auxMap = grps.get(id);

            /*
             * fields for the aux group are fetched and aux data for them is
             * added
             */
            afm = AuxFieldManager.fetchByGroupIdWithValues(id);
            for (int i = 0; i < afm.count(); i++ ) {
                af = afm.getAuxFieldAt(i);
                if ("N".equals(af.getIsActive()))
                    continue;
                aux1 = new AuxDataViewDO();
                aux1.setAuxFieldId(af.getId());
                aux1.setGroupId(id);
                aux1.setAnalyteId(af.getAnalyteId());
                aux1.setAnalyteName(af.getAnalyteName());
                aux1.setIsReportable(af.getIsReportable());
                if (auxMap == null) {
                    /*
                     * set the value as the default for the aux data's field
                     */
                    aux1.setValue(getDefault(afm.getValuesAt(i)));
                } else {
                    /*
                     * set the value from the map if the aux data's analyte can
                     * be found in the map
                     */
                    aux2 = auxMap.get(af.getAnalyteId());
                    if (aux2 != null) {
                        aux1.setIsReportable(aux2.getIsReportable());
                        aux1.setTypeId(aux2.getTypeId());
                        aux1.setValue(aux2.getValue());
                    }
                }
                auxiliary.add(aux1);
            }
        }
    }

    /**
     * returns the group ids present in the set but not in the list of
     * aux data
     */
    private Set<Integer> getDifference(ArrayList<AuxDataViewDO> auxiliary,
                                                     Set<Integer> groupIds) {
        Integer prevId;
        HashSet<Integer> addIds;

        prevId = null;
        addIds = new HashSet<Integer>(groupIds);
        for (AuxDataViewDO a : auxiliary) {
            if ( !a.getGroupId().equals(prevId)) {
                if (groupIds.contains(a.getGroupId()))
                    addIds.remove(a.getGroupId());
                prevId = a.getGroupId();
            }
        }
        return addIds;
    }

    /**
     * returns the default value, if any, defined for a particular aux field
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