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

import org.openelis.manager.WorksheetManager1;
import org.openelis.ui.scriptlet.ScriptletObject;

/**
 * This class is used to provide the information necessary for executing the
 * scriptlets associated with a worksheet and any of its components
 */
public class WorksheetSO extends ScriptletObject {

    private static final long serialVersionUID = 1L;

    public enum Action_Before {
        TEMPLATE_LOAD, PRE_TRANSFER
    }

    public enum Action_After {
        MANAGER_MODIFIED
    }

    protected EnumSet<Action_Before>                 actionBefore;

    protected EnumSet<Action_After>                  actionAfter;

    protected WorksheetManager1                      manager;

    protected HashMap<String, Object>                cache;
    
    protected String                                 uid;

    protected HashSet<String>                        changedUids;

    public WorksheetSO() {
        super();
        /*
         * in some cases e.g. when a patient field is changed, action_before is
         * not specified because it's clear from the key of the
         * field("changed"); it's intialized here to make sure that the
         * scriptlets checking for it don't throw exceptions 
         */
        actionBefore = EnumSet.noneOf(Action_Before.class);
    }

    public EnumSet<Action_Before> getActionBefore() {
        return actionBefore;
    }

    public void addActionBefore(Action_Before action) {
        actionBefore.add(action);
    }

    public EnumSet<Action_After> getActionAfter() {
        return actionAfter;
    }

    public void addActionAfter(Action_After action) {
        if (actionAfter == null)
            actionAfter = EnumSet.noneOf(Action_After.class);
        
        actionAfter.add(action);
    }

    public void setManager(WorksheetManager1 manager) {
        this.manager = manager;
    }

    public WorksheetManager1 getManager() {
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

    public void addChangedUid(String uid) {
        if (changedUids == null)
            changedUids = new HashSet<String>();
        
        changedUids.add(uid);
    }
}