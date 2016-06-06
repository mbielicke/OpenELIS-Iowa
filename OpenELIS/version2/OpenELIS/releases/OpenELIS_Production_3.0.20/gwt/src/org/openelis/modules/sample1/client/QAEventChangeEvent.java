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
 * This event is used to inform the handler that the sample's or an analysis's
 * qa events have changed, e.g. by a new qa event being added or a qa event's
 * type being changed. The unique identifier for the analysis is specified
 * through uid. In the case of the sample the uid is null.
 */
public class QAEventChangeEvent extends GwtEvent<QAEventChangeEvent.Handler> {

    private static Type<QAEventChangeEvent.Handler> TYPE;
    private String                                  uid;

    public QAEventChangeEvent(String uid) {
        this.uid = uid;
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    @Override
    public Type<QAEventChangeEvent.Handler> getAssociatedType() {
        return (Type)TYPE;
    }

    public static Type<QAEventChangeEvent.Handler> getType() {
        if (TYPE == null) {
            TYPE = new Type<QAEventChangeEvent.Handler>();
        }
        return TYPE;
    }

    public static interface Handler extends EventHandler {
        public void onQAEventChange(QAEventChangeEvent event);
    }

    public String getUid() {
        return uid;
    }

    @Override
    protected void dispatch(Handler handler) {
        handler.onQAEventChange(this);
    }
}