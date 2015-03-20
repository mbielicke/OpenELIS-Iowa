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
package org.openelis.modules.worksheetBuilder.client;

import java.util.ArrayList;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

import org.openelis.domain.QcLotViewDO;

/**
 * The event used to inform the handler that reagents have been loaded from the
 * template and there are lines with multiple qcs from which to choose 
 */
public class ReagentsWithChoicesEvent extends GwtEvent<ReagentsWithChoicesEvent.Handler> {

    private static Type<ReagentsWithChoicesEvent.Handler> TYPE;
    private ArrayList<String>                  reagentChoiceUids;
    private ArrayList<QcLotViewDO>             reagentChoices;

    public ReagentsWithChoicesEvent(ArrayList<QcLotViewDO> reagentChoices, ArrayList<String> reagentChoiceUids) {
        this.reagentChoices = reagentChoices;
        this.reagentChoiceUids = reagentChoiceUids;
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    @Override
    public Type<ReagentsWithChoicesEvent.Handler> getAssociatedType() {
        return (Type)TYPE;
    }

    public static Type<ReagentsWithChoicesEvent.Handler> getType() {
        if (TYPE == null) {
            TYPE = new Type<ReagentsWithChoicesEvent.Handler>();
        }
        return TYPE;
    }

    @Override
    protected void dispatch(Handler handler) {
        handler.onReagentsWithChoicesLoaded(this);
    }

    public static interface Handler extends EventHandler {
        public void onReagentsWithChoicesLoaded(ReagentsWithChoicesEvent event);
    }

    public ArrayList<QcLotViewDO> getReagentChoices() {
        return reagentChoices;
    }

    public ArrayList<String> getReagentChoiceUids() {
        return reagentChoiceUids;
    }
}