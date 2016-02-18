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
package org.openelis.manager;

import java.util.ArrayList;

import org.openelis.domain.AnalyteParameterViewDO;
import org.openelis.manager.AnalyteParameterManager1.AnalyteCombo;

/**
 * This class is used to load analyte parameter manager.
 */
public class AnalyteParameterManager1Accessor {
    /**
     * Set/get objects from analyte parameter manager
     */
    public static Integer getReferenceId(AnalyteParameterManager1 apm) {
        return apm.referenceId;
    }

    public static void setReferenceId(AnalyteParameterManager1 apm, Integer referenceId) {
        apm.referenceId = referenceId;
    }

    public static Integer getReferenceTableId(AnalyteParameterManager1 apm) {
        return apm.referenceTableId;
    }

    public static void setReferenceTableId(AnalyteParameterManager1 apm, Integer referenceTableId) {
        apm.referenceTableId = referenceTableId;
    }

    public static String getReferenceName(AnalyteParameterManager1 apm) {
        return apm.referenceName;
    }

    public static void setReferenceName(AnalyteParameterManager1 apm, String referenceName) {
        apm.referenceName = referenceName;
    }

    public static ArrayList<AnalyteCombo> getCombinations(AnalyteParameterManager1 apm) {
        return apm.analyteCombos;
    }

    public static void setCombinations(AnalyteParameterManager1 apm,
                                       ArrayList<AnalyteCombo> combinations) {
        apm.analyteCombos = combinations;
    }

    public static void addCombination(AnalyteParameterManager1 apm, AnalyteCombo combination) {
        if (apm.analyteCombos == null)
            apm.analyteCombos = new ArrayList<AnalyteCombo>();
        apm.analyteCombos.add(combination);
    }

    public static void addCombination(AnalyteParameterManager1 apm, AnalyteCombo combination, int i) {
        if (apm.analyteCombos == null)
            apm.analyteCombos = new ArrayList<AnalyteCombo>();
        apm.analyteCombos.add(i, combination);
    }

    public static void removeCombination(AnalyteParameterManager1 apm, int i) {
        apm.analyteCombos.remove(i);
    }

    public static ArrayList<AnalyteParameterViewDO> getParameters(AnalyteParameterManager1 apm,
                                                                  int i) {
        return apm.analyteCombos.get(i).getParameters();
    }

    public static void setParameters(AnalyteParameterManager1 apm,
                                     ArrayList<AnalyteParameterViewDO> parameters, int i) {
        if (apm.analyteCombos == null)
            apm.analyteCombos = new ArrayList<AnalyteCombo>();
        apm.analyteCombos.get(i).setParameters(parameters);
    }

    public static ArrayList<AnalyteParameterViewDO> getRemoved(AnalyteParameterManager1 apm) {
        return apm.removed;
    }

    public static void setRemoved(AnalyteParameterManager1 apm,
                                  ArrayList<AnalyteParameterViewDO> removed) {
        apm.removed = removed;
    }
}