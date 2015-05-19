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
package org.openelis.modules.attachment.client;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

/**
 * This class is used to notify the handler to open the file linked to the
 * attachment specified by id. The flag isSameWindow specifies whether the file
 * should be opened in the same browser window as before or a different one.
 */
public class DisplayAttachmentEvent extends GwtEvent<DisplayAttachmentEvent.Handler> {

    private static Type<DisplayAttachmentEvent.Handler> TYPE;

    private Integer                                     id;

    private boolean                                     isSameWindow;

    public DisplayAttachmentEvent(Integer id, boolean isSameWindow) {        
        this.id = id;
        this.isSameWindow = isSameWindow;
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    @Override
    public Type<DisplayAttachmentEvent.Handler> getAssociatedType() {
        return (Type)TYPE;
    }

    public static Type<DisplayAttachmentEvent.Handler> getType() {
        if (TYPE == null) {
            TYPE = new Type<DisplayAttachmentEvent.Handler>();
        }
        return TYPE;
    }

    public static interface Handler extends EventHandler {
        public void onDisplayAttachment(DisplayAttachmentEvent event);
    }

    public Integer getId() {
        return id;
    }
    
    public boolean getIsSameWindow() {
        return isSameWindow;
    }

    @Override
    protected void dispatch(Handler handler) {
        handler.onDisplayAttachment(this);
    }
}