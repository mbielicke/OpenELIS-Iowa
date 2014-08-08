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
package org.openelis.modules.sample1.client;

import org.openelis.scriptlet.SampleSO.Operation;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

/**
 * The event used to inform the handler that a scriptlet should be executed on
 * the data specified in the event. The scriptlet to be executed is specified by
 * scriptletId, the field whose value was changed is specified by "changed" and
 * the record that was changed is specified by "uid".
 */
public class RunScriptletEvent extends GwtEvent<RunScriptletEvent.Handler> {

    private static Type<RunScriptletEvent.Handler> TYPE;

    private String                                 uid, changed;
    private Operation                              operation;

    public RunScriptletEvent(String uid, String changed, Operation operation) {
        this.uid = uid;
        this.changed = changed;
        this.operation = operation;
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    @Override
    public Type<RunScriptletEvent.Handler> getAssociatedType() {
        return (Type)TYPE;
    }

    public static Type<RunScriptletEvent.Handler> getType() {
        if (TYPE == null) {
            TYPE = new Type<RunScriptletEvent.Handler>();
        }
        return TYPE;
    }

    public static interface Handler extends EventHandler {
        public void onRunScriptlet(RunScriptletEvent event);
    }

    public String getUid() {
        return uid;
    }

    public String getChanged() {
        return changed;
    }
    
    public Operation getOperation() {
        return operation;
    }

    @Override
    protected void dispatch(Handler handler) {
        handler.onRunScriptlet(this);
    }
}
