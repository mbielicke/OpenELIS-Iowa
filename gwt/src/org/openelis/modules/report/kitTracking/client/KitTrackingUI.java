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
package org.openelis.modules.report.kitTracking.client;

import static org.openelis.ui.screen.State.DEFAULT;

import java.util.ArrayList;

import org.openelis.cache.CategoryCache;
import org.openelis.cache.SectionCache;
import org.openelis.constants.Messages;
import org.openelis.domain.DictionaryDO;
import org.openelis.domain.IdNameVO;
import org.openelis.domain.OrganizationDO;
import org.openelis.domain.SectionViewDO;
import org.openelis.meta.OrderMeta;
import org.openelis.modules.order.client.OrderService;
import org.openelis.modules.organization.client.OrganizationService;
import org.openelis.ui.common.DataBaseUtil;
import org.openelis.ui.common.Datetime;
import org.openelis.ui.common.OptionListItem;
import org.openelis.ui.common.ReportStatus;
import org.openelis.ui.common.data.Query;
import org.openelis.ui.common.data.QueryData;
import org.openelis.ui.event.DataChangeEvent;
import org.openelis.ui.event.GetMatchesEvent;
import org.openelis.ui.event.GetMatchesHandler;
import org.openelis.ui.event.StateChangeEvent;
import org.openelis.ui.screen.Screen;
import org.openelis.ui.screen.ScreenHandler;
import org.openelis.ui.widget.AutoComplete;
import org.openelis.ui.widget.AutoCompleteValue;
import org.openelis.ui.widget.Button;
import org.openelis.ui.widget.Dropdown;
import org.openelis.ui.widget.MultiDropdown;
import org.openelis.ui.widget.Item;
import org.openelis.ui.widget.QueryFieldUtil;
import org.openelis.ui.widget.WindowInt;
import org.openelis.ui.widget.calendar.Calendar;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.http.client.URL;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Widget;

public class KitTrackingUI extends Screen {
    @UiTemplate("KitTracking.ui.xml")
    interface KitTrackingUiBinder extends UiBinder<Widget, KitTrackingUI> {
    };

    public static final KitTrackingUiBinder uiBinder = GWT.create(KitTrackingUiBinder.class);

    @UiField
    protected Calendar                      startDate, endDate;

    @UiField
    protected Dropdown<Integer>             status;

    @UiField
    protected Dropdown<String>              sortBy, printer;
    
    @UiField
    protected MultiDropdown<Integer>        shipFrom, section;

    @UiField
    protected AutoComplete                  organization, description;

    @UiField
    protected Button                        runReportButton, resetButton;

    protected KitTrackingVO                 data;

    public KitTrackingUI(WindowInt window) throws Exception {
        setWindow(window);

        initWidget(uiBinder.createAndBindUi(this));

        initialize();
        data = new KitTrackingVO();
        setState(DEFAULT);
        fireDataChange();
    }

    /**
     * Setup state and data change handles for every widget on the screen
     */
    private void initialize() {
        ArrayList<Item<Integer>> model;
        ArrayList<Item<String>> stringModel;
        ArrayList<DictionaryDO> entries;
        ArrayList<SectionViewDO> sections;
        Item<Integer> row;
        ArrayList<OptionListItem> printers;

        addScreenHandler(startDate, "startDate", new ScreenHandler<Datetime>() {
            public void onDataChange(DataChangeEvent event) {
                startDate.setValue(data.getStartDate());
            }

            public void onValueChange(ValueChangeEvent<Datetime> event) {
                data.setStartDate(event.getValue());
            }

            public void onStateChange(StateChangeEvent event) {
                startDate.setEnabled(true);
            }

            public Widget onTab(boolean forward) {
                return forward ? endDate : printer;
            }
        });

        addScreenHandler(endDate, "endDate", new ScreenHandler<Datetime>() {
            public void onDataChange(DataChangeEvent event) {
                endDate.setValue(data.getEndDate());
            }

            public void onValueChange(ValueChangeEvent<Datetime> event) {
                data.setEndDate(event.getValue());
            }

            public void onStateChange(StateChangeEvent event) {
                endDate.setEnabled(true);
            }

            public Widget onTab(boolean forward) {
                return forward ? shipFrom : startDate;
            }
        });

        addScreenHandler(shipFrom, "shipFrom", new ScreenHandler<ArrayList<Integer>>() {
            public void onDataChange(DataChangeEvent event) {
                shipFrom.setValue(data.getShipFromIds());
            }

            public void onValueChange(ValueChangeEvent<ArrayList<Integer>> event) {
                data.setShipFromIds(event.getValue());
            }

            public void onStateChange(StateChangeEvent event) {
                shipFrom.setEnabled(true);
            }

            public Widget onTab(boolean forward) {
                return forward ? section : endDate;
            }
        });

        addScreenHandler(section, "section", new ScreenHandler<ArrayList<Integer>>() {
            public void onDataChange(DataChangeEvent event) {
                section.setValue(data.getSectionIds());
            }

            public void onValueChange(ValueChangeEvent<ArrayList<Integer>> event) {
                data.setSectionIds(event.getValue());
            }

            public void onStateChange(StateChangeEvent event) {
                section.setEnabled(true);
            }

            public Widget onTab(boolean forward) {
                return forward ? organization : shipFrom;
            }
        });

        addScreenHandler(organization, "organization", new ScreenHandler<AutoCompleteValue>() {
            public void onDataChange(DataChangeEvent event) {
                organization.setValue(data.getOrganizationId(), data.getOrganizationName());
            }

            public void onValueChange(ValueChangeEvent<AutoCompleteValue> event) {
                AutoCompleteValue row;
                
                row = organization.getValue();
                if (row == null || row.getId() == null) {
                    data.setOrganizationId(null);
                    data.setOrganizationName(null);
                } else {
                    data.setOrganizationId(row.getId());
                    data.setOrganizationName(row.getDisplay());
                }
            }

            public void onStateChange(StateChangeEvent event) {
                organization.setEnabled(true);
            }

            public Widget onTab(boolean forward) {
                return forward ? description : section;
            }
        });

        addScreenHandler(description, "description", new ScreenHandler<AutoCompleteValue>() {
            public void onDataChange(DataChangeEvent event) {
                description.setValue(data.getDescriptionId(), data.getDescription());
            }

            public void onValueChange(ValueChangeEvent<AutoCompleteValue> event) {
                AutoCompleteValue row;
                
                row = description.getValue();
                if (row == null || row.getId() == null) {
                    data.setDescriptionId(null);
                    data.setDescription(null);
                } else {
                    data.setDescriptionId(row.getId());
                    data.setDescription(row.getDisplay());
                }
            }

            public void onStateChange(StateChangeEvent event) {
                description.setEnabled(true);
            }

            public Widget onTab(boolean forward) {
                return forward ? status : organization;
            }
        });

        addScreenHandler(status, "status", new ScreenHandler<Integer>() {
            public void onDataChange(DataChangeEvent event) {
                status.setValue(data.getStatusId());
            }

            public void onValueChange(ValueChangeEvent<Integer> event) {
                data.setStatusId(event.getValue());
            }

            public void onStateChange(StateChangeEvent event) {
                status.setEnabled(true);
            }

            public Widget onTab(boolean forward) {
                return forward ? sortBy : description;
            }
        });

        addScreenHandler(sortBy, "sortBy", new ScreenHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                sortBy.setValue(data.getSortBy());
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                data.setSortBy(event.getValue());
            }

            public void onStateChange(StateChangeEvent event) {
                sortBy.setEnabled(true);
            }

            public Widget onTab(boolean forward) {
                return forward ? printer : status;
            }
        });
        
        addScreenHandler(printer, "printer", new ScreenHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                printer.setValue(data.getPrinter());
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                data.setPrinter(event.getValue());
            }

            public void onStateChange(StateChangeEvent event) {
                printer.setEnabled(true);
            }

            public Widget onTab(boolean forward) {
                return forward ? startDate : sortBy;
            }
        });

        // organization auto complete
        organization.addGetMatchesHandler(new GetMatchesHandler() {
            public void onGetMatches(GetMatchesEvent event) {
                Item<Integer> row;
                OrganizationDO data;
                ArrayList<OrganizationDO> list;
                ArrayList<Item<Integer>> model;

                window.setBusy();
                try {
                    list = OrganizationService.get()
                                              .fetchByIdOrName(QueryFieldUtil.parseAutocomplete(event.getMatch()));
                    model = new ArrayList<Item<Integer>>();
                    for (int i = 0; i < list.size(); i++ ) {
                        row = new Item<Integer>(4);
                        data = list.get(i);

                        row.setKey(data.getId());
                        row.setCell(0, data.getName());
                        row.setCell(1, data.getAddress().getStreetAddress());
                        row.setCell(2, data.getAddress().getCity());
                        row.setCell(3, data.getAddress().getState());

                        model.add(row);
                    }
                    organization.showAutoMatches(model);
                } catch (Throwable e) {
                    e.printStackTrace();
                    Window.alert(e.getMessage());
                }
                window.clearStatus();
            }
        });

        // order description auto complete
        description.addGetMatchesHandler(new GetMatchesHandler() {
            public void onGetMatches(GetMatchesEvent event) {
                Item<Integer> row;
                ArrayList<Item<Integer>> model;
                IdNameVO data;
                ArrayList<IdNameVO> dataList;
                ArrayList<String> matchList;
                String name;

                window.setBusy();
                try {
                    // TODO do we want to do this check for the last query?
                    // if (descQuery == null || ( ! (match.indexOf(descQuery) ==
                    // 0))) {
                    model = new ArrayList<Item<Integer>>();
                    dataList = OrderService.get()
                                           .fetchByDescription(QueryFieldUtil.parseAutocomplete(event.getMatch()));
                    matchList = new ArrayList<String>();
                    for (int i = 0; i < dataList.size(); i++ ) {
                        data = dataList.get(i);
                        name = data.getName();
                        if ( !matchList.contains(name)) {
                            row = new Item<Integer>(i, name);
                            model.add(row);
                            matchList.add(name);
                        }
                    }

                    // if (dataList.size() == 0)
                    // descQuery = match;
                    // }
                    description.showAutoMatches(model);
                } catch (Throwable e) {
                    e.printStackTrace();
                    Window.alert(e.getMessage());
                }
                window.clearStatus();
            }
        });

        addScreenHandler(runReportButton, "runReportButton", new ScreenHandler<Object>() {
            public void onStateChange(StateChangeEvent event) {
                runReportButton.setEnabled(true);
            }
        });

        addScreenHandler(resetButton, "resetButton", new ScreenHandler<Object>() {
            public void onStateChange(StateChangeEvent event) {
                resetButton.setEnabled(true);
            }
        });

        // status dropdown
        model = new ArrayList<Item<Integer>>();
        model.add(new Item<Integer>(null, ""));
        entries = CategoryCache.getBySystemName("order_status");
        for (DictionaryDO d : entries) {
            row = new Item<Integer>(d.getId(), d.getEntry());
            row.setEnabled( ("Y".equals(d.getIsActive())));
            model.add(row);
        }

        status.setModel(model);

        // laboratory location dropdown
        model = new ArrayList<Item<Integer>>();
        model.add(new Item<Integer>(null, ""));
        entries = CategoryCache.getBySystemName("laboratory_location");
        for (DictionaryDO data : entries) {
            row = new Item<Integer>(1);
            row.setKey(data.getId());
            row.setCell(0, data.getEntry());
            row.setEnabled( ("Y".equals(data.getIsActive())));
            model.add(row);
        }

        shipFrom.setModel(model);

        // section dropdown
        model = new ArrayList<Item<Integer>>();
        sections = SectionCache.getList();
        model.add(new Item<Integer>(null, ""));
        for (SectionViewDO data : sections) {
            row = new Item<Integer>(1);
            row.setKey(data.getId());
            row.setCell(0, data.getName());
            model.add(row);
        }
        section.setModel(model);

        // sortBy dropdown
        stringModel = new ArrayList<Item<String>>();
        stringModel.add(new Item<String>("", ""));
        stringModel.add(new Item<String>("o_id", "Order#"));
        stringModel.add(new Item<String>("status", "Order status"));
        stringModel.add(new Item<String>("o.ordered_date", "Order date"));
        stringModel.add(new Item<String>("ship_from_name", "Ship from"));
        stringModel.add(new Item<String>("ship_to_name", "Ship to"));
        stringModel.add(new Item<String>("report_to_name", "Report to"));
        stringModel.add(new Item<String>("requested_by", "Requested by"));
        stringModel.add(new Item<String>("cost_center", "Cost center"));
        sortBy.setModel(stringModel);
        
        // printer dropdown
//        stringModel = new ArrayList<Item<String>>();
//        printers = KitTrackingReportService.get().getPrinterListByType("pdf");
//        stringModel.add(new Item<String>("-view-", "View in PDF"));
//        for (OptionListItem data : printers)
//            stringModel.add(new Item<String>(data.getKey(), data.getLabel()));
//        printer.setModel(stringModel);
    }

    @UiHandler("runReportButton")
    protected void runReport(ClickEvent event) {
        Query query;
        QueryData field;
        Datetime fromDate, toDate;
        ArrayList<QueryData> fields;

        clearErrors();
        if ( !validate()) {
            window.setError(Messages.get().gen_correctErrors());
            return;
        }

        query = new Query();
        fields = new ArrayList<QueryData>();

        fromDate = data.getStartDate();
        toDate = data.getEndDate();
        if (fromDate != null && toDate != null) {
            field = new QueryData();
            field.setKey("FROM_DATE");
            field.setQuery(fromDate.toString());
            field.setType(QueryData.Type.DATE);
            fields.add(field);

            field = new QueryData();
            field.setKey("TO_DATE");
            field.setQuery(toDate.toString());
            field.setType(QueryData.Type.DATE);
            fields.add(field);
        }

        if (data.getShipFromIds() != null) {
            field = new QueryData();
            field.setKey(OrderMeta.getShipFromId());
            field.setQuery(DataBaseUtil.concatWithSeparator(data.getShipFromIds(), ","));
            field.setType(QueryData.Type.INTEGER);
            fields.add(field);
        }

        if (data.getSectionIds() != null) {
            field = new QueryData();
            field.setKey("SECTION_ID");
            field.setQuery(DataBaseUtil.concatWithSeparator(data.getSectionIds(), ","));
            field.setType(QueryData.Type.INTEGER);
            fields.add(field);
        }

        if (data.getOrganizationId() != null) {
            field = new QueryData();
            // TODO is it correct to query by ID?
            field.setKey(OrderMeta.getOrganizationId());
            field.setQuery(data.getOrganizationId().toString());
            field.setType(QueryData.Type.INTEGER);
            fields.add(field);
        }

        if (data.getDescription() != null) {
            field = new QueryData();
            field.setKey(OrderMeta.getDescription());
            field.setQuery(data.getDescription());
            field.setType(QueryData.Type.STRING);
            fields.add(field);
        }

        if (data.getStatusId() != null) {
            field = new QueryData();
            field.setKey(OrderMeta.getStatusId());
            field.setQuery(data.getStatusId().toString());
            field.setType(QueryData.Type.INTEGER);
            fields.add(field);
        }

        if (data.getSortBy() != null) {
            field = new QueryData();
            field.setKey("SORT_BY");
            field.setQuery(data.getSortBy());
            field.setType(QueryData.Type.STRING);
            fields.add(field);
        }
        
        if (data.getPrinter() != null) {
            field = new QueryData();
            field.setKey("PRINTER");
            field.setQuery(data.getPrinter());
            field.setType(QueryData.Type.STRING);
            fields.add(field);
        }
        query.setFields(fields);

        window.setBusy(Messages.get().gen_fetching());
        KitTrackingReportService.get().runReport(query, new AsyncCallback<ReportStatus>() {
            public void onSuccess(ReportStatus status) {
                String url;
                if (ReportStatus.Status.SAVED.equals(status.getStatus())) {
                    url = "/openelis/openelis/report?file=" + status.getMessage();
                    Window.open(URL.encode(url), "KitTrackingReport", null);
                    window.setDone(Messages.get().gen_loadCompleteMessage());
                } else {
                    window.setDone(status.getMessage());
                }
            }

            public void onFailure(Throwable caught) {
                window.setError("Failed");
                caught.printStackTrace();
                Window.alert(caught.getMessage());
            }
        });
    }

    @UiHandler("resetButton")
    protected void reset(ClickEvent event) {
        data = new KitTrackingVO();
        fireDataChange();
    }

    public class KitTrackingVO {

        protected Datetime startDate, endDate;
        protected Integer  statusId, organizationId, descriptionId;
        protected String   organizationName, description, sortBy, printer;
        protected ArrayList<Integer> shipFromIds, sectionIds;
        
        public Datetime getStartDate() {
            return startDate;
        }

        public void setStartDate(Datetime startDate) {
            this.startDate = startDate;
        }

        public Datetime getEndDate() {
            return endDate;
        }

        public void setEndDate(Datetime endDate) {
            this.endDate = endDate;
        }

        public ArrayList<Integer> getShipFromIds() {
            return shipFromIds;
        }

        public void setShipFromIds(ArrayList<Integer> shipFromIds) {
            this.shipFromIds = shipFromIds;
        }

        public ArrayList<Integer> getSectionIds() {
            return sectionIds;
        }

        public void setSectionIds(ArrayList<Integer> sectionIds) {
            this.sectionIds = sectionIds;
        }

        public Integer getStatusId() {
            return statusId;
        }

        public void setStatusId(Integer statusId) {
            this.statusId = statusId;
        }

        public String getSortBy() {
            return sortBy;
        }

        public void setSortBy(String sortBy) {
            this.sortBy = sortBy;
        }

        public Integer getOrganizationId() {
            return organizationId;
        }

        public void setOrganizationId(Integer organizationId) {
            this.organizationId = organizationId;
        }

        public String getOrganizationName() {
            return organizationName;
        }

        public void setOrganizationName(String organizationName) {
            this.organizationName = DataBaseUtil.trim(organizationName);
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = DataBaseUtil.trim(description);
        }

        public Integer getDescriptionId() {
            return descriptionId;
        }

        public void setDescriptionId(Integer descriptionId) {
            this.descriptionId = descriptionId;
        }

        public String getPrinter() {
            return printer;
        }

        public void setPrinter(String printer) {
            this.printer = printer;
        }
        
    }
}
