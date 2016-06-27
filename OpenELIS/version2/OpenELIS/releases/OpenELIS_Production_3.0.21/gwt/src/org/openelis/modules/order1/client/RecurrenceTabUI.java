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

import static org.openelis.ui.screen.State.ADD;
import static org.openelis.ui.screen.State.DISPLAY;
import static org.openelis.ui.screen.State.QUERY;
import static org.openelis.ui.screen.State.UPDATE;

import java.util.ArrayList;
import java.util.Date;

import org.openelis.cache.CategoryCache;
import org.openelis.constants.Messages;
import org.openelis.domain.Constants;
import org.openelis.domain.DictionaryDO;
import org.openelis.domain.IOrderRecurrenceDO;
import org.openelis.manager.IOrderManager1;
import org.openelis.meta.IOrderMeta;
import org.openelis.ui.common.DataBaseUtil;
import org.openelis.ui.common.Datetime;
import org.openelis.ui.event.DataChangeEvent;
import org.openelis.ui.event.StateChangeEvent;
import org.openelis.ui.screen.Screen;
import org.openelis.ui.screen.ScreenHandler;
import org.openelis.ui.screen.State;
import org.openelis.ui.widget.Button;
import org.openelis.ui.widget.CheckBox;
import org.openelis.ui.widget.Dropdown;
import org.openelis.ui.widget.Item;
import org.openelis.ui.widget.TextBox;
import org.openelis.ui.widget.calendar.Calendar;
import org.openelis.ui.widget.table.Row;
import org.openelis.ui.widget.table.Table;
import org.openelis.ui.widget.table.event.BeforeCellEditedEvent;
import org.openelis.ui.widget.table.event.BeforeCellEditedHandler;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.VisibleEvent;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Widget;

public class RecurrenceTabUI extends Screen {

    @UiTemplate("RecurrenceTab.ui.xml")
    interface RecurrenceTabUiBinder extends UiBinder<Widget, RecurrenceTabUI> {
    };

    private static RecurrenceTabUiBinder uiBinder = GWT.create(RecurrenceTabUiBinder.class);

    @UiField
    protected CheckBox                   active;

    @UiField
    protected Calendar                   beginDate, endDate;

    @UiField
    protected Dropdown<Integer>          unit;

    @UiField
    protected TextBox<Integer>           frequency, parentOrderNum;

    @UiField
    protected Table                      table;

    @UiField
    protected Button                     showDatesButton;

    protected Screen                     parentScreen;

    protected EventBus                   parentBus;

    protected boolean                    isVisible, canEdit, redraw;

    protected IOrderManager1             manager;

    protected IOrderRecurrenceDO         recurrence;

    public RecurrenceTabUI(Screen parentScreen) {
        this.parentScreen = parentScreen;
        this.parentBus = parentScreen.getEventBus();
        this.window = parentScreen.getWindow();
        initWidget(uiBinder.createAndBindUi(this));
        initialize();

        manager = null;
        recurrence = null;
    }

    private void initialize() {
        ArrayList<DictionaryDO> list;
        ArrayList<Item<Integer>> model;
        Item<Integer> item;

        addScreenHandler(active, IOrderMeta.getRecurrenceIsActive(), new ScreenHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                active.setValue(getIsActive());
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                setIsActive(event.getValue());
            }

            public void onStateChange(StateChangeEvent event) {
                active.setEnabled(isState(QUERY) || ( !canEdit && isState(ADD, UPDATE)));
                active.setQueryMode(isState(QUERY));
            }

            public Widget onTab(boolean forward) {
                return forward ? beginDate : parentOrderNum;
            }
        });

        addScreenHandler(beginDate,
                         IOrderMeta.getRecurrenceActiveBegin(),
                         new ScreenHandler<Datetime>() {
                             public void onDataChange(DataChangeEvent event) {
                                 beginDate.setValue(getActiveBegin());
                             }

                             public void onValueChange(ValueChangeEvent<Datetime> event) {
                                 setActiveBegin(event.getValue());
                                 validateEndDate();
                             }

                             public void onStateChange(StateChangeEvent event) {
                                 beginDate.setEnabled(isState(QUERY) ||
                                                      ( !canEdit && recurrence != null && isState(ADD,
                                                                                                  UPDATE)));
                                 beginDate.setQueryMode(isState(QUERY));
                             }

                             public Widget onTab(boolean forward) {
                                 return forward ? endDate : active;
                             }
                         });

        addScreenHandler(endDate,
                         IOrderMeta.getRecurrenceActiveEnd(),
                         new ScreenHandler<Datetime>() {
                             public void onDataChange(DataChangeEvent event) {
                                 endDate.setValue(getActiveEnd());
                             }

                             public void onValueChange(ValueChangeEvent<Datetime> event) {
                                 setActiveEnd(event.getValue());
                                 validateEndDate();
                             }

                             public void onStateChange(StateChangeEvent event) {
                                 endDate.setEnabled(isState(QUERY) ||
                                                    ( !canEdit && recurrence != null && isState(ADD,
                                                                                                UPDATE)));
                                 endDate.setQueryMode(isState(QUERY));
                             }

                             public Widget onTab(boolean forward) {
                                 return forward ? frequency : beginDate;
                             }
                         });

        addScreenHandler(frequency,
                         IOrderMeta.getRecurrenceFrequency(),
                         new ScreenHandler<Integer>() {
                             public void onDataChange(DataChangeEvent event) {
                                 frequency.setValue(getFrequency());
                             }

                             public void onValueChange(ValueChangeEvent<Integer> event) {
                                 setFrequency(event.getValue());
                             }

                             public void onStateChange(StateChangeEvent event) {
                                 frequency.setEnabled(isState(QUERY) ||
                                                      ( !canEdit && recurrence != null && isState(ADD,
                                                                                                  UPDATE)));
                                 frequency.setQueryMode(isState(QUERY));
                             }

                             public Widget onTab(boolean forward) {
                                 return forward ? unit : endDate;
                             }
                         });

        addScreenHandler(unit, IOrderMeta.getRecurrenceUnitId(), new ScreenHandler<Integer>() {
            public void onDataChange(DataChangeEvent event) {
                unit.setValue(getUnitId());
            }

            public void onValueChange(ValueChangeEvent<Integer> event) {
                setUnitId(event.getValue());
            }

            public void onStateChange(StateChangeEvent event) {
                unit.setEnabled(isState(QUERY) ||
                                ( !canEdit && recurrence != null && isState(ADD, UPDATE)));
                unit.setQueryMode(isState(QUERY));
            }

            public Widget onTab(boolean forward) {
                return forward ? parentOrderNum : frequency;
            }
        });

        addScreenHandler(parentOrderNum,
                         IOrderMeta.getParentIorderId(),
                         new ScreenHandler<Integer>() {
                             public void onDataChange(DataChangeEvent event) {
                                 parentOrderNum.setValue(getParentOrderId());
                             }

                             public void onStateChange(StateChangeEvent event) {
                                 parentOrderNum.setEnabled(isState(QUERY));
                                 parentOrderNum.setQueryMode(isState(QUERY));
                             }

                             public Widget onTab(boolean forward) {
                                 return forward ? active : unit;
                             }
                         });

        addScreenHandler(table, "table", new ScreenHandler<ArrayList<Row>>() {
            public void onDataChange(DataChangeEvent event) {
                table.setModel(getTableModel(false));
            }

            public void onStateChange(StateChangeEvent event) {
                table.setEnabled(isState(DISPLAY, ADD, UPDATE));
            }
        });

        table.addBeforeCellEditedHandler(new BeforeCellEditedHandler() {
            public void onBeforeCellEdited(BeforeCellEditedEvent event) {
                event.cancel();
            }
        });

        addScreenHandler(showDatesButton, "showDatesButton", new ScreenHandler<Object>() {
            public void onStateChange(StateChangeEvent event) {
                showDatesButton.setEnabled(recurrence != null &&
                                           "Y".equals(recurrence.getIsActive()));
            }
        });

        addVisibleHandler(new VisibleEvent.Handler() {
            public void onVisibleOrInvisible(VisibleEvent event) {
                isVisible = event.isVisible();
                displayRecurrence();
            }
        });

        model = new ArrayList<Item<Integer>>();
        list = CategoryCache.getBySystemName("order_recurrence_unit");
        for (DictionaryDO data : list) {
            item = new Item<Integer>(data.getId(), data.getEntry());
            item.setEnabled( ("Y".equals(data.getIsActive())));
            model.add(item);
        }
        unit.setModel(model);
    }

    @UiHandler("showDatesButton")
    protected void showDates(ClickEvent event) {
        boolean showDates;
        Integer freq, unit;
        Datetime bdt, edt;

        showDates = true;
        if (recurrence != null) {

            freq = recurrence.getFrequency();
            unit = recurrence.getUnitId();
            bdt = recurrence.getActiveBegin();
            edt = recurrence.getActiveEnd();

            if (bdt == null || edt == null || freq == null || freq < 1 || unit == null) {
                if (isState(ADD, UPDATE))
                    Window.alert(Messages.get().order_datesFreqUnitNotSpec());
                showDates = false;
            } else if ( !validateEndDate()) {
                showDates = false;
            } else if (Constants.dictionary().ORDER_RECURRENCE_UNIT_MONTHS.equals(unit) ||
                       Constants.dictionary().ORDER_RECURRENCE_UNIT_YEARS.equals(unit)) {
                if ( !validateFrequency())
                    showDates = false;
            }
        } else {
            showDates = false;
        }
        table.setModel(getTableModel(showDates));

    }

    public void setData(IOrderManager1 manager) {
        if (DataBaseUtil.isDifferent(this.manager, manager)) {
            this.manager = manager;
        }
    }

    public void setState(State state) {
        evaluateEdit();
        this.state = state;
        bus.fireEventFromSource(new StateChangeEvent(state), this);
    }

    public void onDataChange() {
        String act1;
        Datetime ab1, ae1;
        Integer freq1, unit1, poid1;

        act1 = null;
        ab1 = null;
        ae1 = null;
        freq1 = null;
        unit1 = null;

        poid1 = getParentOrderId();
        recurrence = getRecurrence();
        if (recurrence != null) {
            act1 = recurrence.getIsActive();
            ab1 = recurrence.getActiveBegin();
            ae1 = recurrence.getActiveEnd();
            freq1 = recurrence.getFrequency();
            unit1 = recurrence.getUnitId();
        }
        /*
         * find out if there's any difference between the recurrence data being
         * displayed and the recurrence data in the manager
         */
        if (DataBaseUtil.isDifferent(act1, active.getValue()) ||
            DataBaseUtil.isDifferent(ab1, beginDate.getValue()) ||
            DataBaseUtil.isDifferent(ae1, endDate.getValue()) ||
            DataBaseUtil.isDifferent(freq1, frequency.getValue()) ||
            DataBaseUtil.isDifferent(unit1, unit.getValue()) ||
            DataBaseUtil.isDifferent(poid1, parentOrderNum.getValue()))
            redraw = true;
        displayRecurrence();
    }

    private void displayRecurrence() {
        if ( !isVisible)
            return;

        if (redraw) {
            redraw = false;
            evaluateEdit();
            setState(state);
            fireDataChange();
        }
    }

    private ArrayList<Row> getTableModel(boolean showDates) {
        Integer unit;
        Datetime now;
        ArrayList<Row> model;

        model = new ArrayList<Row>();
        if ( !showDates)
            return model;

        unit = recurrence.getUnitId();

        now = Datetime.getInstance(Datetime.YEAR, Datetime.DAY);
        if (Constants.dictionary().ORDER_RECURRENCE_UNIT_DAYS.equals(unit))
            getModelByDay(now, model);
        else if (Constants.dictionary().ORDER_RECURRENCE_UNIT_MONTHS.equals(unit))
            getModelByMonth(now, model);
        else if (Constants.dictionary().ORDER_RECURRENCE_UNIT_YEARS.equals(unit))
            getModelByYear(now, model);

        return model;
    }

    private void getModelByDay(Datetime now, ArrayList<Row> model) {
        Integer freq;
        Datetime bdt, edt, next;

        freq = recurrence.getFrequency();
        bdt = recurrence.getActiveBegin();
        edt = recurrence.getActiveEnd();

        if (now.before(bdt)) {
            next = bdt;
        } else if (now.equals(bdt)) {
            next = now;
        } else {
            next = bdt;
            while (next.before(now))
                next = next.add(freq);
        }

        while ( !next.after(edt)) {
            if ( !next.before(now))
                model.add(new Row(next));
            next = next.add(freq);
        }
    }

    private void getModelByMonth(Datetime now, ArrayList<Row> model) {
        int bday, bmon, byr, nday, nmon, nyr;
        Integer freq;
        Date nd;
        Datetime bdt, edt, ndt;

        freq = recurrence.getFrequency();
        bdt = recurrence.getActiveBegin();
        edt = recurrence.getActiveEnd();

        bday = bdt.getDate().getDate();
        bmon = bdt.getDate().getMonth();
        byr = bdt.getDate().getYear();

        nday = bday;
        nmon = bmon;
        nyr = byr;

        /*
         * show all the dates after begin date (including today) that can be
         * produced with the frequency
         */
        nd = new Date(nyr, nmon, nday);
        ndt = Datetime.getInstance(Datetime.YEAR, Datetime.DAY, nd);

        while ( !DataBaseUtil.isAfter(ndt, edt)) {
            if ( !now.after(ndt))
                model.add(new Row(ndt));
            nmon += freq;

            if (nmon > 11) {
                nyr += nmon / 12;
                nmon %= 12;
            }
            nd.setDate(nday);
            nd.setMonth(nmon);
            nd.setYear(nyr);
            ndt = Datetime.getInstance(Datetime.YEAR, Datetime.DAY, nd);
        }
    }

    private void getModelByYear(Datetime now, ArrayList<Row> model) {
        int bday, bmon, byr, nyr;
        Integer freq;
        Date nd;
        Datetime bdt, edt, ndt;

        freq = recurrence.getFrequency();
        bdt = recurrence.getActiveBegin();
        edt = recurrence.getActiveEnd();

        bday = bdt.getDate().getDate();
        bmon = bdt.getDate().getMonth();
        byr = bdt.getDate().getYear();
        /*
         * if today is the begin date then show it, also if begin date is after
         * today, show it as the first date
         */
        if (now.equals(bdt)) {
            ndt = Datetime.getInstance(Datetime.YEAR, Datetime.DAY, bdt.getDate());
            model.add(new Row(ndt));
        } else if (bdt.after(now)) {
            model.add(new Row(bdt));
        }

        nyr = byr + freq;
        nd = new Date(nyr, bmon, bday);
        /*
         * show all the dates after begin date (including today) that can be
         * produced with the frequency
         */
        while ( !edt.before(nd)) {
            if ( !nd.before(now.getDate())) {
                ndt = Datetime.getInstance(Datetime.YEAR, Datetime.DAY, nd);
                model.add(new Row(ndt));
            }
            nyr += freq;
            nd = new Date(nyr, bmon, bday);
        }
    }

    private boolean validateFrequency() {
        int bday, bmon, byr, nday, nmon, nyr, eyr;
        Integer freq, unit;
        Datetime ndt, bdt, edt, now;
        Date nd;
        Integer orderId;

        orderId = recurrence.getIorderId();
        if (orderId == null)
            orderId = 0;

        freq = recurrence.getFrequency();
        unit = recurrence.getUnitId();
        bdt = recurrence.getActiveBegin();
        edt = recurrence.getActiveEnd();
        now = Datetime.getInstance(Datetime.YEAR, Datetime.DAY);

        bday = bdt.getDate().getDate();
        bmon = bdt.getDate().getMonth();
        byr = bdt.getDate().getYear();
        eyr = edt.getDate().getYear();

        nyr = byr;
        nday = bday;
        nmon = bmon;
        if (Constants.dictionary().ORDER_RECURRENCE_UNIT_MONTHS.equals(unit)) {
            nd = new Date(nyr, nmon, nday);
            ndt = Datetime.getInstance(Datetime.YEAR, Datetime.DAY, nd);
            while ( !DataBaseUtil.isAfter(ndt, edt)) {
                if ( !now.after(ndt)) {
                    /*
                     * we have to check to make sure than any month that we
                     * generate a date for has the number of days as specified
                     * in the begin date, otherwise the dates created won't
                     * conform to the frequency
                     */
                    switch (nmon) {
                        case 1:
                            if (nday > 29 || ( (nyr % 4 != 0) && nday > 28)) {
                                setError(Messages.get().order_notAllDatesValid(orderId));
                                return false;
                            }
                            break;
                        case 3:
                        case 5:
                        case 8:
                        case 10:
                            if (nday > 30) {
                                setError(Messages.get().order_notAllDatesValid(orderId));
                                return false;
                            }
                            break;
                    }
                }
                nmon += freq;
                if (nmon > 11) {
                    /*
                     * we use 12 and not 11 to calculate the remainder because
                     * otherwise when "nmon" is 12 the remainder is 1 i.e. the
                     * 2nd month and not 0 or the 1st month
                     */
                    nyr += nmon / 12;
                    nmon %= 12;
                }

                nd.setDate(nday);
                nd.setMonth(nmon);
                nd.setYear(nyr);
                ndt = Datetime.getInstance(Datetime.YEAR, Datetime.DAY, nd);
            }
        } else if (Constants.dictionary().ORDER_RECURRENCE_UNIT_YEARS.equals(unit)) {
            if (nmon != 1 || nday <= 28)
                return true;
            while (nyr < eyr) {
                nyr += freq;
                if (nyr % 4 != 0) {
                    setError(Messages.get().order_notAllDatesValid(orderId));
                    return false;
                }
            }
        }

        return true;
    }

    private boolean validateEndDate() {
        Integer orderId;
        Datetime bdt, edt;
        IOrderRecurrenceDO data;

        data = manager.getRecurrence();
        bdt = data.getActiveBegin();
        edt = data.getActiveEnd();
        orderId = data.getIorderId();
        if (orderId == null)
            orderId = 0;

        if (bdt == null || edt == null) {
            endDate.clearExceptions();
        } else if (edt.before(bdt)) {
            endDate.addException(new Exception(Messages.get()
                                                       .order_endDateAfterBeginDateException(orderId)));
            return false;
        } else {
            endDate.clearExceptions();
        }

        return true;
    }

    private void evaluateEdit() {
        canEdit = manager != null &&
                  Constants.dictionary().ORDER_STATUS_PROCESSED.equals(manager.getIorder()
                                                                              .getStatusId());
    }

    private String getIsActive() {
        if (recurrence == null)
            return null;

        return recurrence.getIsActive();
    }

    private void setIsActive(String isActive) {
        /*
         * if the checkbox is checked to make this a recurring order and the
         * manager doesn't have an OrderRecurrenceDO then one is created and all
         * the other fields are enabled
         */
        if ("Y".equals(isActive) && recurrence == null) {
            recurrence = manager.addRecurrence();
            beginDate.setEnabled(true);
            endDate.setEnabled(true);
            frequency.setEnabled(true);
            unit.setEnabled(true);
            parentBus.fireEvent(new AddRecurrenceEvent());
        }
        recurrence.setIsActive(isActive);
        showDatesButton.setEnabled("Y".equals(isActive));
    }

    private Datetime getActiveBegin() {
        if (recurrence == null)
            return null;

        return recurrence.getActiveBegin();
    }

    private void setActiveBegin(Datetime activeBegin) {
        recurrence.setActiveBegin(activeBegin);
    }

    private Datetime getActiveEnd() {
        if (recurrence == null)
            return null;

        return recurrence.getActiveEnd();
    }

    private void setActiveEnd(Datetime activeEnd) {
        recurrence.setActiveEnd(activeEnd);
    }

    private Integer getFrequency() {
        if (recurrence == null)
            return null;

        return recurrence.getFrequency();
    }

    private void setFrequency(Integer frequency) {
        recurrence.setFrequency(frequency);
    }

    private Integer getUnitId() {
        if (recurrence == null)
            return null;

        return recurrence.getUnitId();
    }

    private void setUnitId(Integer unitId) {
        recurrence.setUnitId(unitId);
    }

    private Integer getParentOrderId() {
        if (manager == null)
            return null;
        return manager.getIorder().getParentIorderId();
    }

    private IOrderRecurrenceDO getRecurrence() {
        if (manager == null)
            return null;
        return manager.getRecurrence();
    }
}
