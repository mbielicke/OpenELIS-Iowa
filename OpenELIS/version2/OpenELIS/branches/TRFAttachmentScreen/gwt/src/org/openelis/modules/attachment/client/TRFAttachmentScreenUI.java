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

import static org.openelis.modules.main.client.Logger.*;
import static org.openelis.ui.screen.State.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;

import org.openelis.constants.Messages;
import org.openelis.domain.AttachmentIssueViewDO;
import org.openelis.manager.AttachmentManager;
import org.openelis.modules.attachment.client.AttachmentIssueEvent.Action;
import org.openelis.modules.main.client.OpenELIS;
import org.openelis.ui.common.ValidationErrorsList;
import org.openelis.ui.event.BeforeCloseEvent;
import org.openelis.ui.event.BeforeCloseHandler;
import org.openelis.ui.event.StateChangeEvent;
import org.openelis.ui.screen.AsyncCallbackUI;
import org.openelis.ui.screen.Screen;
import org.openelis.ui.screen.ScreenHandler;
import org.openelis.ui.screen.State;
import org.openelis.ui.widget.TabLayoutPanel;
import org.openelis.ui.widget.WindowInt;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Widget;

public abstract class TRFAttachmentScreenUI extends Screen {

    @UiTemplate("TRFAttachment.ui.xml")
    interface TRFAttachmentUiBinder extends UiBinder<Widget, TRFAttachmentScreenUI> {
    };

    private static final TRFAttachmentUiBinder                  uiBinder = GWT.create(TRFAttachmentUiBinder.class);

    protected TRFAttachmentScreenUI                             screen;

    protected boolean                                           closeHandlerAdded;

    protected String                                            defaultPattern;

    @UiField
    protected TabLayoutPanel                                    tabPanel;

    @UiField(provided = true)
    protected TRFTabUI                                          trfTab;

    @UiField(provided = true)
    protected IssuesTabUI                                       issuesTab;

    protected AsyncCallbackUI<ArrayList<AttachmentIssueViewDO>> fetchIssuesCall;

    protected AsyncCallbackUI<AttachmentIssueViewDO>            fetchForUpdateCall, updateCall,
                    unlockCall;

    protected AsyncCallback<Void>                               deleteCall;

    protected Integer                                           attachmentId;

    protected Object                                            originalSource;

    protected ArrayList<AttachmentIssueViewDO>                  issueList;

    protected HashMap<Integer, AttachmentIssueViewDO>           issueMap;

    public TRFAttachmentScreenUI() throws Exception {
        trfTab = new TRFTabUI(this) {
            @Override
            public String getPattern() {
                return screen.getPattern();
            }
        };

        issuesTab = new IssuesTabUI(this);

        initWidget(uiBinder.createAndBindUi(this));

        initialize();
        setState(DEFAULT);

        logger.fine("Data Entry TRF Attachment Screen Opened");
    }

    protected void initialize() {
        screen = this;

        /*
         * this is done to make the tabs detachable
         */
        tabPanel.setPopoutBrowser(OpenELIS.getBrowser());

        addScreenHandler(trfTab, "trfTab", new ScreenHandler<Object>() {
            public void onStateChange(StateChangeEvent event) {
                trfTab.setState(event.getState());
            }

            public Object getQuery() {
                return trfTab.getQueryFields();
            }
        });

        addScreenHandler(issuesTab, "issuesTab", new ScreenHandler<Object>() {
            public void onStateChange(StateChangeEvent event) {
                issuesTab.setState(event.getState());
            }
        });

        bus.addHandler(AttachmentIssueEvent.getType(), new AttachmentIssueEvent.Handler() {
            @Override
            public void onAttachmentIssue(AttachmentIssueEvent event) {
                if (screen == event.getSource())
                    return;
                originalSource = event.getOriginalSource();
                switch (event.getAction()) {
                    case FETCH:
                        if (isAnyTabLocked()) {
                            Window.alert(Messages.get().trfAttachment_commitAbortOnOtherTab());
                            return;
                        }
                        fetchIssues();
                        break;
                    case ADD:
                        commitUpdate(event.getAttachmentId());
                        break;
                    case UPDATE:
                        commitUpdate(event.getAttachmentId());
                        break;
                    case DELETE:
                        commitDelete(event.getAttachmentId());
                        break;
                    case LOCK:
                        if (isAnyTabLocked()) {
                            Window.alert(Messages.get().trfAttachment_commitAbortOnOtherTab());
                            return;
                        }
                        fetchForUpdate(event.getAttachmentId());
                        break;
                    case UNLOCK:
                        abort(event.getAttachmentId());
                        break;
                }
            }
        });
    }

    /**
     * Overridden to specify the pattern for the TRFs of a particular domain
     */
    public abstract String getPattern();

    /**
     * Sets the screen's window and adds a BeforeCloseHandler to the window; the
     * handler is added here and not in initialize because the window is a new
     * one every time the screen is brought up and the window won't be available
     * in initialize, the first time the screen is brought up
     */
    public void setWindow(WindowInt window) {
        super.setWindow(window);
        /*
         * this flag needs to be reset every time the screen's window changes,
         * which can happen when the screen is brought up in a modal window and
         * in that case, the handler for BeforeCloseHandler will be needed to be
         * added to the new window
         */
        closeHandlerAdded = false;

        window.addBeforeClosedHandler(new BeforeCloseHandler<WindowInt>() {
            public void onBeforeClosed(BeforeCloseEvent<WindowInt> event) {
                if (trfTab.isAttachmentsLocked()) {
                    event.cancel();
                    setError(Messages.get().trfAttachment_firstUnlockAll());
                } else if (isAnyTabLocked()) {
                    event.cancel();
                    setError(Messages.get().gen_mustCommitOrAbort());
                }
            }
        });
    }

    /**
     * Executes a query to fetch unattached attachments for a particular domain;
     * the domain is specified by the passed pattern
     */
    public void fetchUnattached(String pattern) {
        trfTab.fetchUnattached(pattern);
    }

    /**
     * If a row is selected returns its manager; otherwise, returns null
     */
    public AttachmentManager getSelected() {
        return trfTab.getSelected();
    }

    /**
     * Closes the tab panel and all detached tabs
     */
    public void closeTabPanel() {
        tabPanel.close();
    }

    private void fetchIssues() {
        setBusy(Messages.get().gen_fetching());

        issueList = null;
        issueMap = null;

        if (fetchIssuesCall == null) {
            fetchIssuesCall = new AsyncCallbackUI<ArrayList<AttachmentIssueViewDO>>() {
                public void success(ArrayList<AttachmentIssueViewDO> result) {
                    issueList = result;
                    issueMap = new HashMap<Integer, AttachmentIssueViewDO>();

                    for (AttachmentIssueViewDO data : result)
                        issueMap.put(data.getAttachmentId(), data);

                    fireAttachmentIssue(AttachmentIssueEvent.Action.FETCH, null);
                    clearStatus();
                }

                public void notFound() {
                    setDone(Messages.get().gen_noRecordsFound());
                    fireAttachmentIssue(AttachmentIssueEvent.Action.FETCH, null);
                }

                public void failure(Throwable e) {
                    Window.alert(e.getMessage());
                    logger.log(Level.SEVERE, e.getMessage(), e);
                    clearStatus();
                    fireAttachmentIssue(AttachmentIssueEvent.Action.FETCH, null);
                }
            };
        }

        AttachmentService.get().fetchIssues(fetchIssuesCall);
    }

    protected void fetchForUpdate(Integer attachmentId) {
        setBusy(Messages.get().gen_lockForUpdate());

        this.attachmentId = attachmentId;
        if (fetchForUpdateCall == null) {
            fetchForUpdateCall = new AsyncCallbackUI<AttachmentIssueViewDO>() {
                public void success(AttachmentIssueViewDO result) {
                    refreshIssues(result, false);
                    fireAttachmentIssue(AttachmentIssueEvent.Action.LOCK, screen.attachmentId);
                }

                public void failure(Throwable e) {
                    Window.alert(e.getMessage());
                    logger.log(Level.SEVERE, e.getMessage() != null ? e.getMessage() : "null", e);
                }

                public void finish() {
                    clearStatus();
                }
            };
        }

        AttachmentService.get().fetchIssueForUpdate(attachmentId, fetchForUpdateCall);
    }

    /**
     * Commits the data on the screen to the database; shows any errors/warnings
     * encountered during the commit, otherwise loads the screen with the
     * committed data; if the checkbox for the attachment was checked i.e. it
     * was locked by this user before Update was clicked, tries to lock it
     * again, because update removes the lock in the back-end
     */
    protected void commitUpdate(Integer attachmentId) {
        AttachmentIssueViewDO data;

        setBusy(Messages.get().gen_updating());

        data = issueMap.get(attachmentId);

        if (updateCall == null) {
            updateCall = new AsyncCallbackUI<AttachmentIssueViewDO>() {
                public void success(AttachmentIssueViewDO result) {
                    clearStatus();
                    refreshIssues(result, false);
                    fireAttachmentIssue(AttachmentIssueEvent.Action.UPDATE,
                                        result.getAttachmentId());
                }

                public void validationErrors(ValidationErrorsList e) {
                    showErrors(e);
                }

                public void failure(Throwable e) {
                    Window.alert("commitUpdate(): " + e.getMessage());
                    logger.log(Level.SEVERE, e.getMessage(), e);
                    clearStatus();
                }
            };
        }

        if (data.getId() == null)
            AttachmentService.get().addIssue(data, updateCall);
        else
            AttachmentService.get().updateIssue(data, updateCall);
    }

    /**
     * Commits the data on the screen to the database; shows any errors/warnings
     * encountered during the commit, otherwise loads the screen with the
     * committed data; if the checkbox for the attachment was checked i.e. it
     * was locked by this user before Update was clicked, tries to lock it
     * again, because update removes the lock in the back-end
     */
    protected void commitDelete(Integer attachmentId) {
        setBusy(Messages.get().gen_updating());

        this.attachmentId = attachmentId;
        if (deleteCall == null) {
            deleteCall = new AsyncCallbackUI<Void>() {
                public void success(Void result) {
                    clearStatus();
                    refreshIssues(issueMap.get(screen.attachmentId), true);
                    fireAttachmentIssue(AttachmentIssueEvent.Action.DELETE, screen.attachmentId);
                }

                public void failure(Throwable e) {
                    Window.alert("commitdelete(): " + e.getMessage());
                    logger.log(Level.SEVERE, e.getMessage(), e);
                    clearStatus();
                }
            };
        }
        AttachmentService.get().deleteIssue(issueMap.get(attachmentId), deleteCall);
    }

    /**
     * Reverts any changes made to the data on the screen and disables editing
     * of the widgets; if the checkbox for the attachment was checked i.e. it
     * was locked by this user before Update was clicked, tries to lock it
     * again, because the unlock removes the lock in the back-end
     */
    protected void abort(Integer attachmentId) {
        setBusy(Messages.get().gen_cancelChanges());

        this.attachmentId = attachmentId;
        if (unlockCall == null) {
            unlockCall = new AsyncCallbackUI<AttachmentIssueViewDO>() {
                public void success(AttachmentIssueViewDO result) {
                    setDone(Messages.get().gen_updateAborted());
                    refreshIssues(result, false);
                    logger.log(Level.SEVERE, "abort: " + screen.attachmentId);
                    fireAttachmentIssue(AttachmentIssueEvent.Action.UNLOCK, screen.attachmentId);
                }

                public void failure(Throwable e) {
                    Window.alert(e.getMessage());
                    logger.log(Level.SEVERE, e.getMessage(), e);
                    clearStatus();
                }
            };
        }

        AttachmentService.get().unlockIssue(attachmentId, unlockCall);
    }

    private void fireAttachmentIssue(Action action, Integer attachmentId) {
        bus.fireEventFromSource(new AttachmentIssueEvent(action,
                                                         attachmentId,
                                                         issueList,
                                                         issueMap,
                                                         originalSource), screen);
    }

    private void refreshIssues(AttachmentIssueViewDO data, boolean remove) {
        boolean found;

        if (data == null)
            return;

        found = false;
        for (int i = 0; i < issueList.size(); i++ ) {
            if (data.getAttachmentId().equals(issueList.get(i).getAttachmentId())) {
                if (remove)
                    issueList.remove(i);
                else
                    issueList.set(i, data);
                found = true;
                break;
            }
        }

        if ( !found && !remove)
            issueList.add(data);

        if (remove)
            issueMap.remove(data.getAttachmentId());
        else
            issueMap.put(data.getAttachmentId(), data);
    }

    private boolean isAnyTabLocked() {
        State trfState, issueState;

        trfState = trfTab.getState();
        issueState = issuesTab.getState();
        return (UPDATE.equals(trfState) || DELETE.equals(trfState) || QUERY.equals(trfState) ||
                UPDATE.equals(issueState) || DELETE.equals(issueState));
    }
}