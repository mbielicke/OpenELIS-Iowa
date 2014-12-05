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

import static org.openelis.manager.SampleManager1Accessor.*;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.jboss.security.annotation.SecurityDomain;
import org.openelis.domain.AnalysisViewDO;
import org.openelis.domain.AuxDataViewDO;
import org.openelis.domain.AuxFieldViewDO;
import org.openelis.domain.Constants;
import org.openelis.domain.ResultViewDO;
import org.openelis.domain.SystemVariableDO;
import org.openelis.domain.TestAnalyteViewDO;
import org.openelis.manager.AuxFieldGroupManager;
import org.openelis.manager.SampleManager1;
import org.openelis.manager.TestManager;
import org.openelis.scriptlet.SampleSO;
import org.openelis.scriptlet.ScriptletFactory;
import org.openelis.scriptlet.SampleSO.Action_After;
import org.openelis.scriptlet.SampleSO.Action_Before;
import org.openelis.ui.common.ValidationErrorsList;
import org.openelis.ui.scriptlet.ScriptletInt;
import org.openelis.ui.scriptlet.ScriptletRunner;

/**
 * This class is used to provide various functionalities related to scriptlets
 * in a generic manner
 */

@Stateless
@SecurityDomain("openelis")
public class ScriptletHelperBean {

    @EJB
    private TestManagerBean          testManager;

    @EJB
    private SystemVariableBean       systemVariable;

    @EJB
    private AuxFieldGroupManagerBean auxFieldGroupManager;

    @EJB
    private DictionaryCacheBean      dictionaryCache;

    private static final String      NEO_SCRIPTLET_SYSTEM_VARIABLE = "neonatal_ia_scriptlet_1",
                    ENV_SCRIPTLET_SYSTEM_VARIABLE = "environmental_ia_scriptlet_1",
                    SDWIS_SCRIPTLET_SYSTEM_VARIABLE = "sdwis_ia_scriptlet_1";

    /**
     * creates and returns the cache of objects like TestManager that are used
     * by the scriptlets linked to the various parts of a sample like analyses
     */
    public HashMap<String, Object> createCache(SampleManager1 sm) throws Exception {
        Integer prevId;
        ArrayList<Integer> ids;
        ArrayList<TestManager> tms;
        ArrayList<AuxFieldGroupManager> afgms;
        HashMap<String, Object> cache;

        cache = new HashMap<String, Object>();
        /*
         * the list of tests to be fetched
         */
        ids = new ArrayList<Integer>();
        if (getAnalyses(sm) != null)
            for (AnalysisViewDO data : getAnalyses(sm))
                ids.add(data.getTestId());

        if (ids.size() > 0) {
            tms = testManager.fetchByIds(ids);
            for (TestManager tm : tms)
                cache.put(Constants.uid().getTest(tm.getTest().getId()), tm);
        }

        /*
         * the list of aux field groups to be fetched
         */
        ids.clear();
        prevId = null;
        if (getAuxiliary(sm) != null)
            for (AuxDataViewDO data : getAuxiliary(sm))
                if ( !data.getAuxFieldGroupId().equals(prevId)) {
                    ids.add(data.getAuxFieldGroupId());
                    prevId = data.getAuxFieldGroupId();
                }

        if (ids.size() > 0) {
            afgms = auxFieldGroupManager.fetchByIds(ids);
            for (AuxFieldGroupManager afgm : afgms)
                cache.put(Constants.uid().getAuxFieldGroup(afgm.getGroup().getId()), afgm);
        }

        return cache;
    }

    /**
     * adds the scriptlets for the domain and for all the records in the manager
     * to the scriptlet runner
     */
    public void addScriptlets(SampleManager1 sm, HashMap<String, Object> cache,
                              ScriptletRunner<SampleSO> sr) throws Exception {
        Integer id;
        SystemVariableDO data;

        /*
         * add the scriptlet for the domain, which is the value of the system
         * variable
         */
        data = null;
        if (Constants.domain().ENVIRONMENTAL.equals(getSample(sm).getDomain()))
            data = systemVariable.fetchByName(ENV_SCRIPTLET_SYSTEM_VARIABLE);
        else if (Constants.domain().SDWIS.equals(getSample(sm).getDomain()))
            data = systemVariable.fetchByName(SDWIS_SCRIPTLET_SYSTEM_VARIABLE);
        else if (Constants.domain().NEONATAL.equals(getSample(sm).getDomain()))
            data = systemVariable.fetchByName(NEO_SCRIPTLET_SYSTEM_VARIABLE);

        if (data != null) {
            id = dictionaryCache.getIdBySystemName(data.getValue());
            addScriptlet(id, null, sr);
        }

        /*
         * add all the scriptlets for all tests, test analytes and aux fields
         * linked to the manager
         */
        addTestScriptlets(sm, sr, cache);
        addAuxScriptlets(sm, sr, cache);
    }

    /**
     * adds scriptlets for analyses and results, to the scriptlet runner
     */
    public void addTestScriptlets(SampleManager1 sm, ScriptletRunner<SampleSO> sr,
                                  HashMap<String, Object> cache) throws Exception {
        int i, j;
        Integer id;
        TestAnalyteViewDO ta;
        TestManager tm;
        HashMap<Integer, Integer> ids;

        if (getAnalyses(sm) == null)
            return;

        /*
         * find out the tests and test analytes in the manager for which
         * scriptlets need to be added
         */
        ids = new HashMap<Integer, Integer>();
        for (AnalysisViewDO data : getAnalyses(sm)) {
            tm = get(data.getTestId(), TestManager.class, cache);
            /*
             * scriptlets for analyses
             */
            if (tm.getTest().getScriptletId() != null)
                addScriptlet(tm.getTest().getScriptletId(), data.getId(), sr);

            /*
             * find out which test analytes have scriptlets
             */
            for (i = 0; i < tm.getTestAnalytes().rowCount(); i++ ) {
                for (j = 0; j < tm.getTestAnalytes().columnCount(i); j++ ) {
                    ta = tm.getTestAnalytes().getAnalyteAt(i, j);
                    if (ta.getScriptletId() != null && ids.get(ta.getId()) == null)
                        ids.put(ta.getId(), ta.getScriptletId());
                }
            }
        }

        /*
         * scriptlets for results
         */
        if (getResults(sm) != null)
            for (ResultViewDO data : getResults(sm)) {
                id = ids.get(data.getTestAnalyteId());
                if (id != null)
                    addScriptlet(id, data.getId(), sr);
            }
    }

    /**
     * adds scriptlets for aux data, to the scriptlet runner. If onlyNew is true
     * then only adds the scriptlets for uncommitted records.
     */
    public void addAuxScriptlets(SampleManager1 sm, ScriptletRunner<SampleSO> sr,
                                 HashMap<String, Object> cache) throws Exception {
        int i;
        AuxFieldViewDO auxf;
        AuxFieldGroupManager auxfgm;
        HashSet<Integer> auxfgids;
        HashMap<Integer, Integer> auxfids;

        if (getAuxiliary(sm) == null)
            return;

        auxfids = new HashMap<Integer, Integer>();
        auxfgids = new HashSet<Integer>();
        /*
         * find the ids of the aux groups and also find which aux field is
         * linked to which aux data; duplicate aux groups are not allowed, so an
         * aux field won't be repeated
         */
        for (AuxDataViewDO data : getAuxiliary(sm)) {
            auxfgids.add(data.getAuxFieldGroupId());
            auxfids.put(data.getAuxFieldId(), data.getId());
        }

        /*
         * add the scriptlets linked to the aux fields for the aux data
         * belonging to the groups found above
         */
        for (Integer id : auxfgids) {
            auxfgm = get(id, AuxFieldGroupManager.class, cache);
            for (i = 0; i < auxfgm.getFields().count(); i++ ) {
                auxf = auxfgm.getFields().getAuxFieldAt(i);
                if (auxf.getScriptletId() != null)
                    addScriptlet(auxf.getScriptletId(), auxfids.get(auxf.getId()), sr);
            }
        }
    }

    /**
     * runs the scriptlets for the manager; throws any exceptions found during
     * the execution of the scriptlets
     */
    public void runScriptlets(SampleManager1 sm, HashMap<String, Object> cache, String uid,
                              String changed, Action_Before operation) throws Exception {
        SampleSO data;
        ValidationErrorsList e;
        ScriptletRunner<SampleSO> sr;
        EnumSet<Action_Before> actionBefore;
        EnumSet<Action_After> actionAfter;

        /*
         * create the sciptlet object
         */
        sr = new ScriptletRunner<SampleSO>();
        addScriptlets(sm, cache, sr);

        data = new SampleSO();
        actionBefore = EnumSet.noneOf(Action_Before.class);
        if (operation != null)
            actionBefore.add(operation);
        actionAfter = EnumSet.noneOf(Action_After.class);
        data.setActionBefore(actionBefore);
        data.setActionAfter(actionAfter);
        data.setChanged(changed);
        data.setUid(uid);
        data.setManager(sm);
        data.setCache(cache);
        data.setChangedUids(new HashSet<String>());

        /*
         * run the scriptlets and throw any exceptions found during the
         * execution
         */
        data = sr.run(data);

        e = new ValidationErrorsList();
        if (data.getExceptions() != null && data.getExceptions().size() > 0) {
            for (Exception ex : data.getExceptions())
                e.add(ex);
        }

        if (e.size() > 0)
            throw e;
    }

    /**
     * returns from the cache, the object that has the specified key and is of
     * the specified class
     */
    private <T> T get(Object key, Class<?> c, HashMap<String, Object> cache) {
        String cacheKey;

        cacheKey = null;
        if (c == TestManager.class)
            cacheKey = Constants.uid().getTest((Integer)key);
        else if (c == AuxFieldGroupManager.class)
            cacheKey = Constants.uid().getAuxFieldGroup((Integer)key);

        return (T)cache.get(cacheKey);
    }

    /**
     * adds the scriptlet with the passed ids to the scriptlet runner
     */
    private void addScriptlet(Integer scriptletId, Integer managedId, ScriptletRunner<SampleSO> sr) throws Exception {
        sr.add((ScriptletInt<SampleSO>)ScriptletFactory.get(scriptletId, managedId));
    }
}