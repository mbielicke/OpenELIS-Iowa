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

import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;

import org.openelis.manager.SampleManager1;
import org.openelis.ui.scriptlet.ScriptletObject;

/**
 * This class is used to provide the information necessary for executing the
 * scriptlets associated with a sample and any of its components
 */
public class SampleSO extends ScriptletObject {

    private static final long serialVersionUID = 1L;

    public enum Action_Before {
        NEW_DOMAIN, ANALYSIS, RESULT, AUX_DATA, QA, SAMPLE_ITEM, PATIENT,
        RECOMPUTE, UPDATE, COMPLETE, RELEASE, UNRELEASE
    }

    public enum Action_After {
        SAMPLE_ITEM_ADDED, SAMPLE_ITEM_CHANGED
    }

    protected EnumSet<Action_Before>                 actionBefore;

    protected EnumSet<Action_After>                  actionAfter;

    protected SampleManager1                         manager;

    protected HashMap<String, Object>                cache;
    
    protected String                                 uid;

    protected HashSet<String>                        changedUids;

    public EnumSet<Action_Before> getActionBefore() {
        return actionBefore;
    }

    public void setActionBefore(EnumSet<Action_Before> actionBefore) {
        this.actionBefore = actionBefore;
    }

    public EnumSet<Action_After> getActionAfter() {
        return actionAfter;
    }

    public void setActionAfter(EnumSet<Action_After> actionAfter) {
        this.actionAfter = actionAfter;
    }

    public void setManager(SampleManager1 manager) {
        this.manager = manager;
    }

    public SampleManager1 getManager() {
        return manager;
    }

    public HashMap<String, Object> getCache() {
        return cache;
    }

    public void setCache(HashMap<String, Object> cache) {
        this.cache = cache;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public HashSet<String> getChangedUids() {
        return changedUids;
    }

    public void setChangedUids(HashSet<String> changedUids) {
        this.changedUids = changedUids;
    }
}