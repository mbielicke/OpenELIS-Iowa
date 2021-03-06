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

import org.openelis.domain.AttachmentDO;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

/**
 * This class is used to notify the handler that an attachment should be added
 * to a record e.g. sample; the attachment is specified by the AttachmentDO
 */
public class AddAttachmentEvent extends GwtEvent<AddAttachmentEvent.Handler> {

    private static Type<AddAttachmentEvent.Handler> TYPE;
    
    private AttachmentDO attachment;     

    public AddAttachmentEvent(AttachmentDO attachment) {
        this.attachment = attachment;
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    @Override
    public Type<AddAttachmentEvent.Handler> getAssociatedType() {
        return (Type)TYPE;
    }

    public static Type<AddAttachmentEvent.Handler> getType() {
        if (TYPE == null) {
            TYPE = new Type<AddAttachmentEvent.Handler>();
        }
        return TYPE;
    }

    public static interface Handler extends EventHandler {
        public void onAttachmentAdded(AddAttachmentEvent event);
    }
    
    public AttachmentDO getAttachment() {
        return attachment;
    }

    @Override
    protected void dispatch(Handler handler) {
        handler.onAttachmentAdded(this);
    }
}