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

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

/**
 * The event is used to inform the handler that a qa event was removed from
 * either the sample or an analysis. If an analysis qa event was removed, the
 * unique identifier for the analysis is specified through uid.
 */
public class QAEventRemovedEvent extends GwtEvent<QAEventRemovedEvent.Handler> {

    private static Type<QAEventRemovedEvent.Handler> TYPE;
    private String                                   uid;

    public QAEventRemovedEvent(String uid) {
        this.uid = uid;
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    @Override
    public Type<QAEventRemovedEvent.Handler> getAssociatedType() {
        return (Type)TYPE;
    }

    public static Type<QAEventRemovedEvent.Handler> getType() {
        if (TYPE == null) {
            TYPE = new Type<QAEventRemovedEvent.Handler>();
        }
        return TYPE;
    }

    public static interface Handler extends EventHandler {
        public void onQAEventRemoved(QAEventRemovedEvent event);
    }

    public String getUid() {
        return uid;
    }

    @Override
    protected void dispatch(Handler handler) {
        handler.onQAEventRemoved(this);
    }
}