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
package org.openelis.portal.modules.main.client;

import static org.openelis.portal.client.Logger.remote;

import java.util.logging.Level;

import org.openelis.constants.Messages;
import org.openelis.ui.common.DataBaseUtil;
import org.openelis.ui.common.ReportStatus;
import org.openelis.ui.common.ReportStatus.Status;
import org.openelis.ui.event.StateChangeEvent;
import org.openelis.ui.resources.UIResources;
import org.openelis.ui.screen.Screen;
import org.openelis.ui.screen.ScreenHandler;
import org.openelis.ui.widget.Button;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.Widget;

public abstract class QuestionPopupUI extends Screen {

    @UiTemplate("QuestionPopup.ui.xml")
    interface QuestionPopupUIBinder extends UiBinder<Widget, QuestionPopupUI> {
    };

    private static QuestionPopupUIBinder uiBinder = GWT.create(QuestionPopupUIBinder.class);

    @UiField
    protected Button                     yesButton, noButton, cancelButton;

    protected static QuestionPopupUI     questionPopup;

    protected static PopupPanel          popup;

    public QuestionPopupUI() {
        initWidget(uiBinder.createAndBindUi(this));
        initialize();
    }

    private void initialize() {
        addScreenHandler(yesButton, "yesButton", new ScreenHandler<Object>() {
            public void onStateChange(StateChangeEvent event) {
                yesButton.setEnabled(true);
            }

            public Widget onTab(boolean forward) {
                return forward ? noButton : cancelButton;
            }
        });

        addScreenHandler(noButton, "noButton", new ScreenHandler<Object>() {
            public void onStateChange(StateChangeEvent event) {
                noButton.setEnabled(true);
            }

            public Widget onTab(boolean forward) {
                return forward ? cancelButton : yesButton;
            }
        });

        addScreenHandler(cancelButton, "cancelButton", new ScreenHandler<Object>() {
            public void onStateChange(StateChangeEvent event) {
                cancelButton.setEnabled(true);
            }

            public Widget onTab(boolean forward) {
                return forward ? yesButton : noButton;
            }
        });
    }

    /**
     * overridden to respond to the user clicking "yes"
     */
    public abstract void yes();

    /**
     * overridden to respond to the user clicking "no"
     */
    public abstract void no();

    /**
     * overridden to respond to the user clicking "cancel"
     */
    public abstract void cancel();

    @UiHandler("yesButton")
    protected void yes(ClickEvent event) {
        popup.hide();
        yes();
    }

    @UiHandler("noButton")
    protected void no(ClickEvent event) {
        popup.hide();
        no();
    }

    @UiHandler("cancelButton")
    protected void cancel(ClickEvent event) {
        popup.hide();
        cancel();
    }

    public static void popup() {
        if (questionPopup == null) {
            questionPopup = new QuestionPopupUI() {
                @Override
                public void yes() {
                    submit(Messages.get().gen_yes());
                    MainScreen.logout();
                }

                @Override
                public void no() {
                    submit(Messages.get().gen_no());
                    MainScreen.logout();
                }

                public void cancel() {
                    // Do nothing
                }

                private void submit(String response) {
                    try {
                        UserResponseService.get().saveResponse(response,
                                                               new AsyncCallback<ReportStatus>() {
                                                                   @Override
                                                                   public void onSuccess(ReportStatus result) {
                                                                       if (result != null) {
                                                                           if (Status.SAVED.equals(result.getStatus())) {
                                                                               // do
                                                                               // nothing
                                                                           } else {
                                                                               remote().log(Level.SEVERE,
                                                                                            DataBaseUtil.toString(result.getMessage()));
                                                                           }
                                                                       } else {
                                                                           remote().log(Level.SEVERE,
                                                                                        "No status was returned");
                                                                       }
                                                                   }

                                                                   @Override
                                                                   public void onFailure(Throwable caught) {
                                                                       remote().log(Level.SEVERE,
                                                                                    DataBaseUtil.toString(caught.getMessage()),
                                                                                    caught);
                                                                   }
                                                               });
                    } catch (Exception e) {
                        remote().log(Level.SEVERE, e.getMessage(), e);
                    }
                }
            };
            UIResources.INSTANCE.portalStatus().ensureInjected();
            questionPopup.setStyleName(UIResources.INSTANCE.portalStatus().Button());
        }
        if (popup == null) {
            popup = new PopupPanel();
            popup.setSize("500px", "100px");
            popup.setModal(true);
            popup.setAutoHideEnabled(false);
        }

        popup.setWidget(questionPopup);

        popup.setPopupPositionAndShow(new PopupPanel.PositionCallback() {

            @Override
            public void setPosition(int offsetWidth, int offsetHeight) {
                int width = com.google.gwt.user.client.Window.getClientWidth();
                popup.setPopupPosition(width - 550, 50);
            }

        });
    }
}
