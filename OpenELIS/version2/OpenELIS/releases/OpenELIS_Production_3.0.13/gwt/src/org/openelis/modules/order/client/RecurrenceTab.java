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
package org.openelis.modules.order.client;

import java.util.ArrayList;
import java.util.Date;
import java.util.EnumSet;

import org.openelis.cache.CategoryCache;
import org.openelis.constants.Messages;
import org.openelis.domain.Constants;
import org.openelis.domain.DictionaryDO;
import org.openelis.domain.IOrderRecurrenceDO;
import org.openelis.ui.common.Datetime;
import org.openelis.gwt.event.DataChangeEvent;
import org.openelis.gwt.event.StateChangeEvent;
import org.openelis.gwt.screen.Screen;
import org.openelis.gwt.screen.ScreenDefInt;
import org.openelis.gwt.screen.ScreenEventHandler;
import org.openelis.gwt.widget.AppButton;
import org.openelis.gwt.widget.CalendarLookUp;
import org.openelis.gwt.widget.CheckBox;
import org.openelis.gwt.widget.Dropdown;
import org.openelis.gwt.widget.ScreenWindowInt;
import org.openelis.gwt.widget.TextBox;
import org.openelis.gwt.widget.table.TableDataRow;
import org.openelis.gwt.widget.table.TableWidget;
import org.openelis.manager.IOrderManager;
import org.openelis.meta.IOrderMeta;
import org.openelis.ui.widget.WindowInt;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.user.client.Window;

public class RecurrenceTab extends Screen {

    private IOrderManager      manager;
    private IOrderRecurrenceDO recurrence;
    private Dropdown<Integer> recurrenceUnitId;
    private TextBox           recurrenceFrequency, parentOrderId;
    private CalendarLookUp    recurrenceActiveBegin, recurrenceActiveEnd;
    private CheckBox          recurrenceIsActive;
    private AppButton         showDateButton;
    private TableWidget       table;
    private boolean           loaded;

    public RecurrenceTab(ScreenDefInt def, WindowInt window) {
        setDefinition(def);
        setWindow(window);
        initialize();

        initializeDropdowns();
    }

    private void initialize() {
        recurrenceIsActive = (CheckBox)def.getWidget(IOrderMeta.getRecurrenceIsActive());
        addScreenHandler(recurrenceIsActive, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                recurrenceIsActive.setValue(recurrence.getIsActive());
                showDateButton.enable("Y".equals(recurrence.getIsActive()));
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                recurrence.setIsActive(event.getValue());
                showDateButton.enable("Y".equals(recurrence.getIsActive()));
            }

            public void onStateChange(StateChangeEvent<State> event) {
                recurrenceIsActive.enable(canEdit(event.getState()));
                recurrenceIsActive.setQueryMode(event.getState() == State.QUERY);
            }
        });

        recurrenceActiveBegin = (CalendarLookUp)def.getWidget(IOrderMeta.getRecurrenceActiveBegin());
        addScreenHandler(recurrenceActiveBegin, new ScreenEventHandler<Datetime>() {
            public void onDataChange(DataChangeEvent event) {
                recurrenceActiveBegin.setValue(recurrence.getActiveBegin());
            }

            public void onValueChange(ValueChangeEvent<Datetime> event) {
                recurrence.setActiveBegin(event.getValue());
                validateEndDate();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                recurrenceActiveBegin.enable(canEdit(event.getState()));
                recurrenceActiveBegin.setQueryMode(event.getState() == State.QUERY);
            }
        });

        recurrenceActiveEnd = (CalendarLookUp)def.getWidget(IOrderMeta.getRecurrenceActiveEnd());
        addScreenHandler(recurrenceActiveEnd, new ScreenEventHandler<Datetime>() {
            public void onDataChange(DataChangeEvent event) {
                recurrenceActiveEnd.setValue(recurrence.getActiveEnd());
            }

            public void onValueChange(ValueChangeEvent<Datetime> event) {
                recurrence.setActiveEnd(event.getValue());
                validateEndDate();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                recurrenceActiveEnd.enable(canEdit(event.getState()));
                recurrenceActiveEnd.setQueryMode(event.getState() == State.QUERY);
            }
        });

        recurrenceFrequency = (TextBox)def.getWidget(IOrderMeta.getRecurrenceFrequency());
        addScreenHandler(recurrenceFrequency, new ScreenEventHandler<Integer>() {
            public void onDataChange(DataChangeEvent event) {
                recurrenceFrequency.setFieldValue(recurrence.getFrequency());
            }

            public void onValueChange(ValueChangeEvent<Integer> event) {
                recurrence.setFrequency(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                recurrenceFrequency.enable(canEdit(event.getState()));
                recurrenceFrequency.setQueryMode(event.getState() == State.QUERY);
            }
        });

        recurrenceUnitId = (Dropdown<Integer>)def.getWidget(IOrderMeta.getRecurrenceUnitId());
        addScreenHandler(recurrenceUnitId, new ScreenEventHandler<Integer>() {
            public void onDataChange(DataChangeEvent event) {
                recurrenceUnitId.setSelection(recurrence.getUnitId());
            }

            public void onValueChange(ValueChangeEvent<Integer> event) {
                recurrence.setUnitId(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                recurrenceUnitId.enable(canEdit(event.getState()));
                recurrenceUnitId.setQueryMode(event.getState() == State.QUERY);
            }
        });

        parentOrderId = (TextBox)def.getWidget(IOrderMeta.getParentIorderId());
        addScreenHandler(parentOrderId, new ScreenEventHandler<Integer>() {
            public void onDataChange(DataChangeEvent event) {
                parentOrderId.setFieldValue(manager.getIorder().getParentIorderId());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                parentOrderId.enable(EnumSet.of(State.QUERY).contains(event.getState()));
                parentOrderId.setQueryMode(event.getState() == State.QUERY);
            }
        });

        table = (TableWidget)def.getWidget("dateTable");
        addScreenHandler(table, new ScreenEventHandler<ArrayList<TableDataRow>>() {
            public void onDataChange(DataChangeEvent event) {
                table.load(getTableModel(false));
            }

            public void onStateChange(StateChangeEvent<State> event) {
                table.enable(true);
            }
        });

        showDateButton = (AppButton)def.getWidget("showDateButton");
        addScreenHandler(showDateButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                table.load(getTableModel(true));
            }

            public void onStateChange(StateChangeEvent<State> event) {
                showDateButton.enable(false);
            }
        });
    }

    private void initializeDropdowns() {
        ArrayList<TableDataRow> model;
        ArrayList<DictionaryDO> list;
        TableDataRow row;

        model = new ArrayList<TableDataRow>();
        model.add(new TableDataRow(null, ""));
        list = CategoryCache.getBySystemName("order_recurrence_unit");
        for (DictionaryDO d : list) {
            row = new TableDataRow(d.getId(), d.getEntry());
            row.enabled = ("Y".equals(d.getIsActive()));
            model.add(row);
        }
        recurrenceUnitId.setModel(model);
    }

    public void setManager(IOrderManager manager) {
        this.manager = manager;
        loaded = false;
    }

    public void draw() {
        if ( !loaded) {
            try {
                recurrence = manager.getRecurrence();
                DataChangeEvent.fire(this);
                loaded = true;
            } catch (Exception e) {
                Window.alert(e.getMessage());
                e.printStackTrace();
            }
        }
    }

    private ArrayList<TableDataRow> getTableModel(boolean showDates) {
        Integer freq, unit;
        Datetime now, bdt, edt, next;
        ArrayList<TableDataRow> model;

        freq = recurrence.getFrequency();
        unit = recurrence.getUnitId();
        bdt = recurrence.getActiveBegin();
        edt = recurrence.getActiveEnd();
        model = new ArrayList<TableDataRow>();

        if ( !showDates)
            return model;

        if (bdt == null || edt == null || freq == null || freq < 1 || unit == null) {
            if (state == State.ADD || state == State.UPDATE)
                Window.alert(Messages.get().datesFreqUnitNotSpec());
            return model;
        }

        if ( !validateEndDate())
            return model;

        now = Datetime.getInstance(Datetime.YEAR, Datetime.DAY);
        if (Constants.dictionary().ORDER_RECURRENCE_UNIT_DAYS.equals(unit)) {
            if (now.before(bdt)) {
                next = bdt;
            } else if (now.equals(bdt)) {
                next = now;
            } else {
                next = bdt;
                while (next.before(now))
                    next = next.add(freq);
            }

            model = new ArrayList<TableDataRow>();
            while ( !next.after(edt)) {
                if ( !next.before(now))
                    model.add(new TableDataRow(null, next));
                next = next.add(freq);
            }
        } else if (Constants.dictionary().ORDER_RECURRENCE_UNIT_MONTHS.equals(unit)) {
            if ( !validateFrequency())
                return model;
            return getModelByMonth(now);
        } else if (Constants.dictionary().ORDER_RECURRENCE_UNIT_YEARS.equals(unit)) {
            if ( !validateFrequency())
                return model;
            return getModelByYear(now);
        }

        return model;
    }

    private ArrayList<TableDataRow> getModelByMonth(Datetime now) {
        int bday, bmon, byr, nday, nmon, nyr, emon, eyr, nmons, iter;
        Integer freq;
        Date nd;
        Datetime bdt, edt, next;
        ArrayList<TableDataRow> model;

        model = new ArrayList<TableDataRow>();
        freq = recurrence.getFrequency();
        bdt = recurrence.getActiveBegin();
        edt = recurrence.getActiveEnd();

        bday = bdt.getDate().getDate();
        bmon = bdt.getDate().getMonth()+1;
        byr = bdt.getDate().getYear();
        emon = edt.getDate().getMonth()+1;
        eyr = edt.getDate().getYear();

        nday = bday;
        nmon = bmon;
        nyr = byr;

        nmons = (emon - bmon) + ((eyr - nyr) * 12);
        /*
         * if today is the begin date then show it, also if begin date is after
         * today, show it as the first date
         */
        if (now.equals(bdt)) {
            next = Datetime.getInstance(Datetime.YEAR, Datetime.DAY, bdt.getDate());
            model.add(new TableDataRow(null, next));
        } else if (bdt.after(now)) {
            model.add(new TableDataRow(null, bdt));
        }
        iter = 0;
        /*
         * show all the dates after begin date (including today) that can be
         * produced with the frequency
         */
        while (iter < nmons) {
            iter += freq;
            nmon += freq;

            if (nmon > 11) {
                nmon %= 12;
                nyr++ ;
            }
            nd = new Date(nyr, nmon-1, nday);
            if (now.after(nd))
                continue;

            if (edt.before(nd))
                break;
            next = Datetime.getInstance(Datetime.YEAR, Datetime.DAY, nd);
            model.add(new TableDataRow(null, next));
        }

        return model;
    }

    private ArrayList<TableDataRow> getModelByYear(Datetime now) {
        int bday, bmon, byr, nyr;
        Integer freq;
        Date nd;
        Datetime bdt, edt, next;
        ArrayList<TableDataRow> model;

        freq = recurrence.getFrequency();
        bdt = recurrence.getActiveBegin();
        edt = recurrence.getActiveEnd();
        model = new ArrayList<TableDataRow>();

        bday = bdt.getDate().getDate();
        bmon = bdt.getDate().getMonth();
        byr = bdt.getDate().getYear();
        /*
         * if today is the begin date then show it, also if begin date is after
         * today, show it as the first date
         */
        if (now.equals(bdt)) {
            next = Datetime.getInstance(Datetime.YEAR, Datetime.DAY, bdt.getDate());
            model.add(new TableDataRow(null, next));
        } else if (bdt.after(now)) {
            model.add(new TableDataRow(null, bdt));
        }

        nyr = byr + freq;
        nd = new Date(nyr, bmon, bday);
        /*
         * show all the dates after begin date (including today) that can be
         * produced with the frequency
         */
        while ( !edt.before(nd)) {
            if ( !nd.before(now.getDate())) {
                next = Datetime.getInstance(Datetime.YEAR, Datetime.DAY, nd);
                model.add(new TableDataRow(null, next));
            }
            nyr += freq;
            nd = new Date(nyr, bmon, bday);
        }

        return model;
    }

    private boolean validateFrequency() {
        int bday, bmon, byr, nday, nmon, nyr, emon, eyr, nmons, iter;
        Integer freq, unit;
        Datetime bdt, edt;

        freq = recurrence.getFrequency();
        unit = recurrence.getUnitId();
        bdt = recurrence.getActiveBegin();
        edt = recurrence.getActiveEnd();

        bday = bdt.getDate().getDate();
        bmon = bdt.getDate().getMonth()+1;
        byr = bdt.getDate().getYear();
        emon = edt.getDate().getMonth()+1;
        eyr = edt.getDate().getYear();

        nyr = byr;
        nday = bday;
        nmon = bmon;
        if (Constants.dictionary().ORDER_RECURRENCE_UNIT_MONTHS.equals(unit)) {
            /*
             * We calculate the number of months (nmons) between the one that
             * begin date is in and the one that end date is in, inclusive of
             * the latter.
             */
            nmons = (emon - bmon) + ((eyr - nyr) * 12);
            iter = freq;
            while (iter < nmons) {
                /*
                 * Here, "iter" is used to keep track of how close to end date's
                 * month we are, which is "nmons" months after begin date's
                 * month. We can't use "nmon", which is the month that the
                 * currently created date is in for this purpose, because its
                 * value is always betweeen 0 and 11, whereas "nmons" can be
                 * more than 11.
                 */
                nmon += freq;
                if (nmon > 11) {
                    /*
                     * we use 12 and not 11 to calculate the remainder because
                     * otherwise when "nmon" is 12 the remainder is 1 i.e. the
                     * 2nd month and not 0 or the 1st month
                     */
                    nmon %= 12;
                    nyr++ ;
                }
                /*
                 * we have to check to make sure than any month that we generate
                 * a date for has the number of days as specified in the begin
                 * date, otherwise the dates created won't conform to the
                 * frequency
                 */
                switch (nmon) {
                    case 2:
                        if (nday > 29 || ( (nyr % 4 != 0) && nday > 28)) {
                            recurrenceFrequency.addException(new Exception(Messages.get().notAllDatesValid()));
                            return false;
                        }
                        break;
                    case 4:
                    case 6:
                    case 9:
                    case 11:
                        if (nday > 30) {
                            recurrenceFrequency.addException(new Exception(Messages.get().notAllDatesValid()));
                            return false;
                        }
                        break;
                }
                iter += freq;
            }
        } else if (Constants.dictionary().ORDER_RECURRENCE_UNIT_YEARS.equals(unit)) {
            if (nmon != 1 || nday <= 28)
                return true;
            while (nyr < eyr) {
                nyr += freq;
                if (nyr % 4 != 0) {
                    recurrenceFrequency.addException(new Exception(Messages.get().notAllDatesValid()));
                    return false;
                }
            }
        }

        return true;
    }

    private boolean validateEndDate() {
        Datetime bdt, edt;

        bdt = recurrence.getActiveBegin();
        edt = recurrence.getActiveEnd();

        if (bdt == null || edt == null) {
            recurrenceActiveEnd.clearExceptions();
        } else if (edt.before(bdt)) {
            recurrenceActiveEnd.addException(new Exception(Messages.get().endDateAfterBeginDateException()));
            return false;
        } else {
            recurrenceActiveEnd.clearExceptions();
        }

        return true;
    }

    private boolean canEdit(State state) {
        return EnumSet.of(State.QUERY, State.ADD).contains(state) ||
               (state == State.UPDATE && !Constants.dictionary().ORDER_STATUS_PROCESSED.equals(manager.getIorder()
                                                                                                      .getStatusId()));
    }
}