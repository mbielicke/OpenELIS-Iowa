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

import java.util.ArrayList;
import java.util.HashMap;

import org.openelis.domain.AttachmentIssueViewDO;
import org.openelis.ui.screen.Screen;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

/**
 * This class is used to notify the handler that some operation related to
 * attachment issues needs to be or was performed e.g. fetching or removing
 * issue(s). If the operation needs to be or was performed on a particular
 * issue, its attachment id is specified by "attachmentId".
 */
public class AttachmentIssueEvent extends GwtEvent<AttachmentIssueEvent.Handler> {

    public enum Action {
        FETCH, ADD, UPDATE, DELETE, LOCK, UNLOCK
    }

    private static Type<AttachmentIssueEvent.Handler> TYPE;
    private Action                                    action;
    private Integer                                   attachmentId;
    private ArrayList<AttachmentIssueViewDO>          issueList;
    private HashMap<Integer, AttachmentIssueViewDO>   issueMap;
    private Object                                    originalSource;

    public AttachmentIssueEvent(Action action, Integer attachmentId,
                                ArrayList<AttachmentIssueViewDO> issueList,
                                HashMap<Integer, AttachmentIssueViewDO> issueMap,
                                Object originalSource) {
        this.action = action;
        this.attachmentId = attachmentId;
        this.issueList = issueList;
        this.issueMap = issueMap;
        this.originalSource = originalSource;
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    @Override
    public Type<AttachmentIssueEvent.Handler> getAssociatedType() {
        return (Type)TYPE;
    }

    public static Type<AttachmentIssueEvent.Handler> getType() {
        if (TYPE == null) {
            TYPE = new Type<AttachmentIssueEvent.Handler>();
        }
        return TYPE;
    }

    public static interface Handler extends EventHandler {
        public void onAttachmentIssue(AttachmentIssueEvent event);
    }

    public Action getAction() {
        return action;
    }

    public Integer getAttachmentId() {
        return attachmentId;
    }

    public ArrayList<AttachmentIssueViewDO> getIssueList() {
        return issueList;
    }

    public HashMap<Integer, AttachmentIssueViewDO> getIssueMap() {
        return issueMap;
    }

    public Object getOriginalSource() {
        return originalSource;
    }

    @Override
    protected void dispatch(Handler handler) {
        handler.onAttachmentIssue(this);
    }
}