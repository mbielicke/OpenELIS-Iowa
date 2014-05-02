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

import java.util.HashMap;

import org.openelis.manager.AuxFieldGroupManager;
import org.openelis.manager.SampleManager1;
import org.openelis.manager.TestManager;
import org.openelis.ui.scriptlet.ScriptletObject;

/**
 * This class is used to provide the information necessary for executing the
 * scriptlets associated with a sample and any of its components
 */
public class SampleSO extends ScriptletObject {

    private static final long                      serialVersionUID = 1L;

    private SampleManager1                         manager;

    private HashMap<Integer, TestManager>          analyses, results;

    private HashMap<Integer, AuxFieldGroupManager> auxData;

    public void setManager(SampleManager1 manager) {
        this.manager = manager;
    }

    public SampleManager1 getManager() {
        return manager;
    }

    public HashMap<Integer, TestManager> getAnalyses() {
        return analyses;
    }

    public void setAnalyses(HashMap<Integer, TestManager> analyses) {
        this.analyses = analyses;
    }

    public HashMap<Integer, TestManager> getResults() {
        return results;
    }

    public void setResults(HashMap<Integer, TestManager> results) {
        this.results = results;
    }

    public HashMap<Integer, AuxFieldGroupManager> getAuxData() {
        return auxData;
    }

    public void setAuxData(HashMap<Integer, AuxFieldGroupManager> auxData) {
        this.auxData = auxData;
    }
}