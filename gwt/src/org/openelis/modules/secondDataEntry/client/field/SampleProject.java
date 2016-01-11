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
package org.openelis.modules.secondDataEntry.client.field;

import static org.openelis.modules.main.client.Logger.*;
import static org.openelis.ui.screen.State.*;

import java.util.ArrayList;
import java.util.logging.Level;

import org.openelis.domain.Constants;
import org.openelis.domain.ProjectDO;
import org.openelis.domain.SampleProjectViewDO;
import org.openelis.manager.SampleManager1;
import org.openelis.meta.SampleMeta;
import org.openelis.modules.main.client.resources.OpenELISResources;
import org.openelis.modules.project.client.ProjectService;
import org.openelis.modules.secondDataEntry.client.VerificationScreen;
import org.openelis.ui.common.DataBaseUtil;
import org.openelis.ui.event.DataChangeEvent;
import org.openelis.ui.event.GetMatchesEvent;
import org.openelis.ui.event.GetMatchesHandler;
import org.openelis.ui.event.StateChangeEvent;
import org.openelis.ui.screen.ScreenHandler;
import org.openelis.ui.widget.AutoComplete;
import org.openelis.ui.widget.AutoCompleteValue;
import org.openelis.ui.widget.Item;
import org.openelis.ui.widget.QueryFieldUtil;

import com.google.gwt.dom.client.TableRowElement;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;

/**
 * This class manages the widgets used for verifying sample project
 */
public class SampleProject extends SingleField<AutoComplete> {
    public SampleProject(VerificationScreen parentScreen, TableRowElement tableRowElement,
                         AutoComplete editableWidget, AutoComplete nonEditableWidget,
                         Image matchImage, Image copyImage, int rowIndex) {
        super(parentScreen, tableRowElement, editableWidget, nonEditableWidget, matchImage,
              copyImage, rowIndex);
        init();
    }

    /**
     * Makes the row in which the widgets are shown, visible and sets its style;
     * adds handlers to the widgets in the row
     */
    protected void init() {
        setRowVisible();

        key = SampleMeta.PROJECT_NAME;
        parentScreen.addScreenHandler(editableWidget, key, new ScreenHandler<AutoCompleteValue>() {
            public void onDataChange(DataChangeEvent<AutoCompleteValue> event) {
                clear();
            }

            public void onValueChange(ValueChangeEvent<AutoCompleteValue> event) {
                valueChanged();
                parentScreen.setTabFocusLostWidget(null);
            }

            public void onStateChange(StateChangeEvent event) {
                editableWidget.setEnabled(parentScreen.isState(UPDATE));
                nonEditableWidget.setEnabled(false);
            }

            public Widget onTab(boolean forward) {
                parentScreen.setTabFocusLostWidget(editableWidget);
                return forward ? nextTabWidget : prevTabWidget;
            }
        });

        editableWidget.addGetMatchesHandler(new GetMatchesHandler() {
            public void onGetMatches(GetMatchesEvent event) {
                Item<Integer> row;
                ArrayList<ProjectDO> list;
                ArrayList<Item<Integer>> model;

                parentScreen.getWindow().setBusy();
                try {
                    list = ProjectService.get()
                                         .fetchActiveByName(QueryFieldUtil.parseAutocomplete(event.getMatch()));
                    model = new ArrayList<Item<Integer>>();
                    for (ProjectDO data : list) {
                        row = new Item<Integer>(data.getId(), data.getName(), data.getDescription());
                        row.setData(data);
                        model.add(row);
                    }
                    editableWidget.showAutoMatches(model);
                } catch (Throwable e) {
                    Window.alert(e.getMessage());
                    logger.log(Level.SEVERE, e.getMessage(), e);
                }
                parentScreen.getWindow().clearStatus();
            }
        });
    }

    /**
     * Copies the first project from the manager to the editable widget
     */
    public void copyFromSample() {
        Integer projId, sprojId;
        String sprojName;
        SampleProjectViewDO sproj;

        projId = editableWidget.getValue() != null ? editableWidget.getValue().getId() : null;
        sproj = getFirstProject();
        sprojId = sproj != null ? sproj.getProjectId() : null;
        sprojName = sproj != null ? sproj.getProjectName() : null;
        if (numEdit > 1 && DataBaseUtil.isDifferent(projId, sprojId)) {
            editableWidget.setValue(sprojId, sprojName);
            matchImage.setResource(OpenELISResources.INSTANCE.commit());
            copyImage.setResource(OpenELISResources.INSTANCE.arrowLeftImage());
            isVerified = true;
            operation = 1;
        }
    }

    /**
     * Copies project from the editable widget to the manager and makes it the
     * first project
     */
    public void copyToSample() {
        Integer projId, sprojId;
        ProjectDO proj;
        SampleProjectViewDO sproj;

        proj = editableWidget.getValue() != null ? (ProjectDO)editableWidget.getValue().getData()
                                                : null;
        projId = proj != null ? proj.getId() : null;
        sproj = getFirstProject();
        sprojId = sproj != null ? sproj.getProjectId() : null;
        if (numEdit > 1 && !editableWidget.hasExceptions() &&
            DataBaseUtil.isDifferent(projId, sprojId)) {
            changeProject(proj);
            nonEditableWidget.setValue(editableWidget.getValue());
            matchImage.setResource(OpenELISResources.INSTANCE.commit());
            copyImage.setResource(OpenELISResources.INSTANCE.arrowRightImage());
            isVerified = true;
            operation = 2;
        }
    }

    /**
     * Verifies whether the value entered by the user is the same as the
     * sample's first project; increments the number of times the value has been
     * changed; if the values are different and the value has been changed more
     * than once, shows the sample's first project to the user
     */
    public void valueChanged() {
        Integer id;
        String name;
        SampleProjectViewDO sproj;

        /*
         * increment the number of times the value has been changed
         */
        numEdit++ ;

        /*
         * verify whether the value entered is different from the one in the
         * manager; if it is and this widget has been edited multiple times,
         * show the value in the manager in the widget on the right; blank the
         * icon for the direction of copy because the current value was not
         * copied to or from the manager
         */
        verify();
        if ( !isVerified) {
            copyImage.setResource(OpenELISResources.INSTANCE.blankIcon());
            if (numEdit > 1) {
                sproj = getFirstProject();
                id = sproj != null ? sproj.getProjectId() : null;
                name = sproj != null ? sproj.getProjectName() : null;
                nonEditableWidget.setValue(id, name);
            }
            /*
             * set the focus back to the editable widget if it lost focus by
             * pressing Tab
             */
            if (parentScreen.getTabFocusLostWidget() == editableWidget)
                refocus();
        }
    }

    /**
     * Verifies whether the value entered by the user is the same as the
     * sample's first project
     */
    public void verify() {
        Integer projId, sprojId;
        SampleProjectViewDO sproj;

        projId = editableWidget.getValue() != null ? editableWidget.getValue().getId() : null;
        sproj = getFirstProject();
        sprojId = sproj != null ? sproj.getProjectId() : null;
        isVerified = !DataBaseUtil.isDifferent(projId, sprojId);
        matchImage.setResource(isVerified ? OpenELISResources.INSTANCE.commit()
                                         : OpenELISResources.INSTANCE.abort());
    }

    /**
     * Sets all the widgets and class fields to their default values
     */
    protected void clear() {
        super.clear();
        editableWidget.setValue(null);
        nonEditableWidget.setValue(null);
        matchImage.setResource(OpenELISResources.INSTANCE.blankIcon());
        copyImage.setResource(OpenELISResources.INSTANCE.blankIcon());
    }

    /**
     * Returns the first project from the manager, or null if the manager
     * doesn't have any projects
     */
    private SampleProjectViewDO getFirstProject() {
        return parentScreen.getManager().project.count() > 0 ? parentScreen.getManager().project.get(0)
                                                            : null;
    }

    /**
     * Adds or updates the first project of the sample if the argument is not
     * null, otherwise deletes the first project
     */
    private void changeProject(ProjectDO proj) {
        String domain;
        SampleProjectViewDO sproj;
        SampleManager1 sm;

        sproj = getFirstProject();
        sm = parentScreen.getManager();
        if (proj == null) {
            /*
             * if a project was not selected and if there were projects present
             * then the first project is deleted and the next project is set as
             * the first one
             */
            if (sproj != null)
                sm.project.remove(sproj);
        } else {
            /*
             * otherwise the first project is modified or a new one is created
             * if no project existed
             */
            if (sproj == null) {
                sproj = sm.project.add();
                domain = sm.getSample().getDomain();
                if (Constants.domain().CLINICAL.equals(domain) ||
                    Constants.domain().NEONATAL.equals(domain) ||
                    Constants.domain().PT.equals(domain))
                    sproj.setIsPermanent("N");
            }

            sproj.setProjectId(proj.getId());
            sproj.setProjectName(proj.getName());
            sproj.setProjectDescription(proj.getDescription());
        }
    }
}