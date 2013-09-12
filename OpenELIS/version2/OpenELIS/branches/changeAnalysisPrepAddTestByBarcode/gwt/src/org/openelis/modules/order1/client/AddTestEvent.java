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
package org.openelis.modules.order1.client;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

/**
 * This class is used to notify the handler that a test or panel was chosen by a
 * user to be added to an order
 */
public class AddTestEvent extends GwtEvent<AddTestEvent.Handler> {

    public enum AddType {
        TEST, PANEL
    }

    private static Type<AddTestEvent.Handler> TYPE;
    private AddType                           addType;
    private Integer                           id, index;

    public AddTestEvent(AddType type, Integer id, Integer index) {
        this.addType = type;
        this.id = id;
        this.index = index;
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    @Override
    public Type<AddTestEvent.Handler> getAssociatedType() {
        return (Type)TYPE;
    }

    public static Type<AddTestEvent.Handler> getType() {
        if (TYPE == null) {
            TYPE = new Type<AddTestEvent.Handler>();
        }
        return TYPE;
    }

    public AddType getAddType() {
        return addType;
    }

    public Integer getId() {
        return id;
    }
    
    public Integer getIndex() {
        return index;
    }

    public static interface Handler extends EventHandler {
        public void onAddTest(AddTestEvent event);
    }

    @Override
    protected void dispatch(Handler handler) {
        handler.onAddTest(this);
    }
}
