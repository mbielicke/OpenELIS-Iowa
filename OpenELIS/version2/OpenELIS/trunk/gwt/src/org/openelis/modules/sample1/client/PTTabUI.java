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

import static org.openelis.modules.main.client.Logger.*;
import static org.openelis.ui.screen.State.*;

import java.util.ArrayList;
import java.util.logging.Level;

import org.openelis.cache.CategoryCache;
import org.openelis.cache.UserCache;
import org.openelis.domain.Constants;
import org.openelis.domain.DictionaryDO;
import org.openelis.manager.SampleManager1;
import org.openelis.meta.SampleMeta;
import org.openelis.scriptlet.SampleSO.Action_Before;
import org.openelis.ui.common.Datetime;
import org.openelis.ui.common.SystemUserVO;
import org.openelis.ui.event.DataChangeEvent;
import org.openelis.ui.event.GetMatchesEvent;
import org.openelis.ui.event.GetMatchesHandler;
import org.openelis.ui.event.StateChangeEvent;
import org.openelis.ui.screen.Screen;
import org.openelis.ui.screen.ScreenHandler;
import org.openelis.ui.screen.State;
import org.openelis.ui.widget.AutoComplete;
import org.openelis.ui.widget.AutoCompleteValue;
import org.openelis.ui.widget.Dropdown;
import org.openelis.ui.widget.Item;
import org.openelis.ui.widget.QueryFieldUtil;
import org.openelis.ui.widget.TextBox;
import org.openelis.ui.widget.calendar.Calendar;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.VisibleEvent;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Widget;

public class PTTabUI extends Screen {
    @UiTemplate("PTTab.ui.xml")
    interface PTTabUIBinder extends UiBinder<Widget, PTTabUI> {
    };

    private static PTTabUIBinder uiBinder = GWT.create(PTTabUIBinder.class);

    @UiField
    protected Dropdown<Integer>  ptProvider;

    @UiField
    protected Calendar           dueDate;

    @UiField
    protected TextBox<String>    series;

    @UiField
    protected AutoComplete       receivedByName;

    @UiField
    protected Dropdown<String>   additionalDomain;

    protected SampleManager1     manager;

    protected Screen             parentScreen;

    protected EventBus           parentBus;

    protected boolean            canEdit, isBusy, isVisible, redraw, canQuery;

    protected PatientPermission  patientPermission;

    public PTTabUI(Screen parentScreen) {
        this.parentScreen = parentScreen;
        this.parentBus = parentScreen.getEventBus();
        this.window = parentScreen.getWindow();
        initWidget(uiBinder.createAndBindUi(this));
        patientPermission = new PatientPermission();
        initialize();

        manager = null;
    }

    public void initialize() {
        Item<Integer> row;
        Item<String> strow;
        ArrayList<Item<Integer>> model;
        ArrayList<Item<String>> stmodel;

        addScreenHandler(ptProvider, SampleMeta.getPTPTProviderId(), new ScreenHandler<Integer>() {
            public void onDataChange(DataChangeEvent<Integer> event) {
                ptProvider.setValue(getPTProviderId());
            }

            public void onValueChange(ValueChangeEvent<Integer> event) {
                setPTProviderId(event.getValue());
            }

            public void onStateChange(StateChangeEvent event) {
                ptProvider.setEnabled( (isState(QUERY) && canQuery) ||
                                      (canEdit && isState(ADD, UPDATE)));
                ptProvider.setQueryMode( (isState(QUERY) && canQuery));
            }

            public Widget onTab(boolean forward) {
                return forward ? series : receivedByName;
            }
        });

        addScreenHandler(series, SampleMeta.getPTSeries(), new ScreenHandler<String>() {
            public void onDataChange(DataChangeEvent<String> event) {
                series.setValue(getSeries());
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                setSeries(event.getValue());
            }

            public void onStateChange(StateChangeEvent event) {
                series.setEnabled( (isState(QUERY) && canQuery) ||
                                  (canEdit && isState(ADD, UPDATE)));
                series.setQueryMode( (isState(QUERY) && canQuery));
            }

            public Widget onTab(boolean forward) {
                return forward ? dueDate : ptProvider;
            }
        });

        addScreenHandler(dueDate, SampleMeta.getPTDueDate(), new ScreenHandler<Datetime>() {
            public void onDataChange(DataChangeEvent<Datetime> event) {
                dueDate.setValue(getDueDate());
            }

            public void onValueChange(ValueChangeEvent<Datetime> event) {
                setDueDate(event.getValue());
            }

            public void onStateChange(StateChangeEvent event) {
                dueDate.setEnabled( (isState(QUERY) && canQuery) ||
                                   (canEdit && isState(ADD, UPDATE)));
                dueDate.setQueryMode( (isState(QUERY) && canQuery));
            }

            public Widget onTab(boolean forward) {
                return forward ? additionalDomain : series;
            }
        });

        addScreenHandler(additionalDomain,
                         SampleMeta.getPTAdditionalDomain(),
                         new ScreenHandler<String>() {
                             public void onDataChange(DataChangeEvent<String> event) {
                                 additionalDomain.setValue(getAdditionalDomain());
                             }

                             public void onValueChange(ValueChangeEvent<String> event) {
                                 setAdditionalDomain(event.getValue());
                             }

                             public void onStateChange(StateChangeEvent event) {
                                 additionalDomain.setEnabled(isState(QUERY) ||
                                                             (canEdit && isState(ADD, UPDATE)));
                                 additionalDomain.setQueryMode( (isState(QUERY) && canQuery));
                             }

                             public Widget onTab(boolean forward) {
                                 return forward ? receivedByName : dueDate;
                             }
                         });

        addScreenHandler(receivedByName,
                         SampleMeta.getReceivedById(),
                         new ScreenHandler<AutoCompleteValue>() {
                             public void onDataChange(DataChangeEvent<AutoCompleteValue> event) {
                                 try {
                                     receivedByName.setValue(getReceivedById(), getReceivedByName());
                                 } catch (Exception e) {
                                     Window.alert(e.getMessage());
                                     logger.log(Level.SEVERE, e.getMessage(), e);
                                 }
                             }

                             public void onValueChange(ValueChangeEvent<AutoCompleteValue> event) {
                                 Integer id;
                                 SystemUserVO user;

                                 id = null;
                                 if (event.getValue() != null) {
                                     user = (SystemUserVO)event.getValue().getData();
                                     id = user.getId();
                                 }
                                 setReceivedById(id);
                             }

                             public void onStateChange(StateChangeEvent event) {
                                 receivedByName.setEnabled(canEdit && isState(ADD, UPDATE));
                             }

                             public Object getQuery() {
                                 /*
                                  * since this field is not set in query mode,
                                  * the value doesn't get cleared when
                                  * StateChangeEvent is fired; it also doesn't
                                  * get cleared if the tab is not visible,
                                  * because DataChangeEvent is not fired in that
                                  * case; this makes sure that the value doesn't
                                  * get included in the query sent to the
                                  * back-end
                                  */
                                 return null;
                             }

                             public Widget onTab(boolean forward) {
                                 return forward ? ptProvider : additionalDomain;
                             }
                         });

        receivedByName.addGetMatchesHandler(new GetMatchesHandler() {
            public void onGetMatches(GetMatchesEvent event) {
                Item<Integer> item;
                ArrayList<SystemUserVO> users;
                ArrayList<Item<Integer>> model;

                try {
                    users = UserCache.getEmployees(QueryFieldUtil.parseAutocomplete(event.getMatch() +
                                                                                    "%"));
                    model = new ArrayList<Item<Integer>>();
                    for (SystemUserVO user : users) {
                        item = new Item<Integer>(user.getId(), user.getLoginName());
                        item.setData(user);
                        model.add(item);
                    }
                    receivedByName.showAutoMatches(model);
                } catch (Exception e) {
                    Window.alert(e.toString());
                    logger.log(Level.SEVERE, e.getMessage(), e);
                }
            }
        });

        addVisibleHandler(new VisibleEvent.Handler() {
            public void onVisibleOrInvisible(VisibleEvent event) {
                isVisible = event.isVisible();
                displaySamplePT();
            }
        });

        model = new ArrayList<Item<Integer>>();
        for (DictionaryDO d : CategoryCache.getBySystemName("pt_provider")) {
            row = new Item<Integer>(d.getId(), d.getEntry());
            row.setEnabled( ("Y".equals(d.getIsActive())));
            model.add(row);
        }

        ptProvider.setModel(model);

        stmodel = new ArrayList<Item<String>>();
        for (DictionaryDO d : CategoryCache.getBySystemName("sample_domain")) {
            if (Constants.domain().CLINICAL.equals(d.getCode()) ||
                Constants.domain().NEONATAL.equals(d.getCode())) {
                /*
                 * don't enable an additional domain if the user doesn't have
                 * permission to view patients; disable neonatal until it's
                 * fully implemented in OpenELIS
                 */
                strow = new Item<String>(d.getCode(), d.getEntry());
                strow.setEnabled( ("Y".equals(d.getIsActive())) &&
                                 patientPermission.canViewSample(d) &&
                                 Constants.domain().CLINICAL.equals(d.getCode()));
                stmodel.add(strow);
            }
        }

        additionalDomain.setModel(stmodel);
    }

    public void setData(SampleManager1 manager) {
        this.manager = manager;
    }

    public void setState(State state) {
        evaluateEdit();
        this.state = state;
        bus.fireEventFromSource(new StateChangeEvent(state), this);
    }

    public void setCanQuery(boolean canQuery) {
        this.canQuery = canQuery;
    }

    /**
     * notifies the tab that it may need to refresh the display in its widgets;
     * if the data currently showing in the widgets is the same as the data in
     * the latest manager then the widgets are not refreshed
     */
    public void onDataChange() {
        redraw = true;
        displaySamplePT();
    }

    private void displaySamplePT() {
        if ( !isVisible)
            return;

        if (redraw) {
            redraw = false;
            fireDataChange();
        }
    }

    /**
     * determines if the fields on the screen can be edited based on the data
     */
    private void evaluateEdit() {
        canEdit = (manager != null && !Constants.dictionary().SAMPLE_RELEASED.equals(manager.getSample()
                                                                                            .getStatusId()));
    }

    /*
     * getters and setters for the fields at the domain level
     */

    /**
     * Returns pt provider id or null if the manager is null or if this is not a
     * pt sample
     */
    private Integer getPTProviderId() {
        if (manager == null || manager.getSamplePT() == null)
            return null;
        return manager.getSamplePT().getPTProviderId();
    }

    /**
     * Sets the pt provider id
     */
    private void setPTProviderId(Integer ptProviderId) {
        manager.getSamplePT().setPTProviderId(ptProviderId);
    }

    /**
     * Returns series or null if the manager is null or if this is not a pt
     * sample
     */
    private String getSeries() {
        if (manager == null || manager.getSamplePT() == null)
            return null;
        return manager.getSamplePT().getSeries();
    }

    /**
     * Sets the series
     */
    private void setSeries(String series) {
        manager.getSamplePT().setSeries(series);
    }

    /**
     * Returns due date or null if the manager is null or if this is not a pt
     * sample
     */
    private Datetime getDueDate() {
        if (manager == null || manager.getSamplePT() == null)
            return null;
        return manager.getSamplePT().getDueDate();
    }

    private void setDueDate(Datetime dueDate) {
        manager.getSamplePT().setDueDate(dueDate);
    }

    /**
     * Returns additional domain or null if the manager is null or if this is
     * not a pt sample
     */
    private String getAdditionalDomain() {
        if (manager == null || manager.getSamplePT() == null)
            return null;
        return manager.getSamplePT().getAdditionalDomain();
    }

    /**
     * Sets the additional domain
     */
    private void setAdditionalDomain(String additionalDomain) {
        parentBus.fireEvent(new AdditionalDomainChangeEvent(additionalDomain));
    }

    /**
     * Returns the received by id or null if the manager is null or if this is
     * not a pt sample
     */
    private Integer getReceivedById() throws Exception {
        if (manager == null || manager.getSamplePT() == null)
            return null;
        return manager.getSample().getReceivedById();
    }

    /**
     * Returns the login name of the user linked by received by id; returns null
     * if the manager is null or if this is not a pt sample or received by id is
     * null
     */
    private String getReceivedByName() throws Exception {
        if (manager == null || manager.getSamplePT() == null ||
            manager.getSample().getReceivedById() == null)
            return null;
        return UserCache.getSystemUser(manager.getSample().getReceivedById()).getLoginName();
    }

    /**
     * Sets the received by id
     */
    private void setReceivedById(Integer receivedById) {
        manager.getSample().setReceivedById(receivedById);
    }
}