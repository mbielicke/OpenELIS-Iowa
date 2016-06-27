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
package org.openelis.modules.report.dataView1.client;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

/**
 * The event used to inform the handler a column needs to be added to or removed
 * from the ones that will be shown in the data view report. The meta key for
 * the column is specified by "column".
 */
public class ColumnEvent extends GwtEvent<ColumnEvent.Handler> {

    public enum Action {
        ADD, REMOVE
    }

    private static Type<ColumnEvent.Handler> TYPE;
    private Action                           action;
    private String                           column;

    public ColumnEvent(String column, Action action) {
        this.column = column;
        this.action = action;
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    @Override
    public Type<ColumnEvent.Handler> getAssociatedType() {
        return (Type)TYPE;
    }

    public static Type<ColumnEvent.Handler> getType() {
        if (TYPE == null) {
            TYPE = new Type<ColumnEvent.Handler>();
        }
        return TYPE;
    }

    public static interface Handler extends EventHandler {
        public void onColumn(ColumnEvent event);
    }

    public Action getAction() {
        return action;
    }

    public String getColumn() {
        return column;
    }

    @Override
    protected void dispatch(Handler handler) {
        handler.onColumn(this);
    }
}