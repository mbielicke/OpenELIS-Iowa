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
package org.openelis.modules.finalReportSingleReprint.client;

import java.util.ArrayList;
import java.util.EnumSet;

import org.openelis.cache.DictionaryCache;
import org.openelis.domain.AuxDataViewDO;
import org.openelis.domain.OptionListItem;
import org.openelis.domain.OrganizationDO;
import org.openelis.domain.OrganizationParameterDO;
import org.openelis.domain.SampleDO;
import org.openelis.domain.SampleOrganizationViewDO;
import org.openelis.domain.SamplePrivateWellViewDO;
import org.openelis.gwt.common.DataBaseUtil;
import org.openelis.gwt.common.NotFoundException;
import org.openelis.gwt.common.data.Query;
import org.openelis.gwt.common.data.QueryData;
import org.openelis.gwt.event.DataChangeEvent;
import org.openelis.gwt.event.StateChangeEvent;
import org.openelis.gwt.screen.Screen;
import org.openelis.gwt.screen.ScreenDefInt;
import org.openelis.gwt.screen.ScreenEventHandler;
import org.openelis.gwt.services.ScreenService;
import org.openelis.gwt.widget.AppButton;
import org.openelis.gwt.widget.CheckBox;
import org.openelis.gwt.widget.Dropdown;
import org.openelis.gwt.widget.TextArea;
import org.openelis.gwt.widget.TextBox;
import org.openelis.gwt.widget.table.TableDataRow;
import org.openelis.manager.AuxDataManager;
import org.openelis.manager.OrganizationParameterManager;
import org.openelis.manager.Preferences;
import org.openelis.manager.SampleManager;
import org.openelis.manager.SampleOrganizationManager;
import org.openelis.manager.SamplePrivateWellManager;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.Window;

public class FinalReportSingleReprintScreen extends Screen {

    private FormVO                         data;
    private SampleManager                  manager;
    private FinalReportSingleReprintScreen screen;
    private TextBox                        accessionNumber, faxNumber, from, toName, toCompany;
    private CheckBox                       print, fax;
    private Dropdown<Integer>              organization;
    private Dropdown<String>               printer, destination;
    private TextArea                       comment;
    private AppButton                      runReportButton, resetButton;
    private FinalReportScreen              finalReportScreen;
    private Integer                        billToTypeId, finalRepFaxTypeId;   
    private String                         VIEW_PDF_KEY = "-view-";
    private ScreenService                  preferencesService;

    public FinalReportSingleReprintScreen() throws Exception {
        super((ScreenDefInt)GWT.create(FinalReportSingleReprintDef.class));
        preferencesService = new ScreenService("controller?service=org.openelis.modules.preferences.server.PreferencesService");

        DeferredCommand.addCommand(new Command() {
            public void execute() {
                postConstructor();
            }
        });
    }

    private void postConstructor() {   
        reset();
        initialize();
        setState(State.DEFAULT);
        initializeDropdowns();
        DataChangeEvent.fire(this);
    }

    private void initialize() {
        screen = this;
        
        accessionNumber = (TextBox)def.getWidget("accessionNumber");
        addScreenHandler(accessionNumber, new ScreenEventHandler<Integer>() {
            public void onDataChange(DataChangeEvent event) {
                accessionNumber.setValue(data.getAccessionNumber());
            }

            public void onValueChange(ValueChangeEvent<Integer> event) {
                Integer accNum;
                
                manager = null;
                accNum = event.getValue();
                data.setAccessionNumber(accNum);
                data.setOrganizationId(null);
                data.setDestinationId(null);
                data.setFaxNumber(null);
                data.setToName(null);
                data.setToCompany(null);
                
                try {
                    if (accNum != null) {
                        window.setBusy(consts.get("fetching"));
                        manager = SampleManager.fetchWithAllDataByAccessionNumber(accNum);
                    }
                    window.clearStatus();
                } catch (NotFoundException e) {
                    data.setAccessionNumber(null);
                    window.setDone(consts.get("noRecordsFound"));
                } catch (Exception e) {
                    data.setAccessionNumber(null);
                    Window.alert(e.getMessage());
                    window.clearStatus();
                }                             
                
                organization.setModel(getOrgModel());
                destination.setModel(getDestModel());
                clearErrors();
                DataChangeEvent.fire(screen);
            }

            public void onStateChange(StateChangeEvent<State> event) {
                accessionNumber.enable(EnumSet.of(State.DEFAULT).contains(event.getState()));
            }
        });

        print = (CheckBox)def.getWidget("print");
        addScreenHandler(print, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                String val;
                boolean checked;

                val = data.getIsPrint();
                print.setValue(val);
                checked = "Y".equals(val);
                enablePrintFields(checked);
                setPrintFieldsRequired(checked);
                if (!checked)
                    clearExceptionsFromPrintFields();
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                boolean checked;
                String val;

                val = event.getValue();
                data.setIsPrint(val);
                checked = "Y".equals(val);
                enablePrintFields(checked);
                setPrintFieldsRequired(checked);
                if (!checked)
                    clearExceptionsFromPrintFields();
                
                data.setIsFax(checked ? "N" : "Y");
                DataChangeEvent.fire(screen, fax);
            }

            public void onStateChange(StateChangeEvent<State> event) {
                print.enable(EnumSet.of(State.DEFAULT).contains(event.getState()));
            }
        });

        organization = (Dropdown)def.getWidget("organization");
        addScreenHandler(organization, new ScreenEventHandler<Integer>() {
            public void onDataChange(DataChangeEvent event) {
                organization.setSelection(data.getOrganizationId());
            }

            public void onValueChange(ValueChangeEvent<Integer> event) {
                data.setOrganizationId(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                organization.enable(false);
            }
        });

        printer = (Dropdown)def.getWidget("printer");
        addScreenHandler(printer, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                printer.setSelection(data.getPrinter());
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                data.setPrinter(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                printer.enable(false);
            }
        });

        fax = (CheckBox)def.getWidget("fax");
        addScreenHandler(fax, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                String val;
                boolean checked;

                val = data.getIsFax();
                fax.setValue(val);
                checked = "Y".equals(val);
                enableFaxFields(checked);
                setFaxFieldsRequired(checked);
                if (!checked)
                    clearExceptionsFromFaxFields();
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                boolean checked;
                String val;

                val = event.getValue();
                data.setIsFax(val);
                checked = "Y".equals(val);
                enableFaxFields(checked);
                setFaxFieldsRequired(checked);
                if (!checked)
                    clearExceptionsFromFaxFields();
                
                data.setIsPrint(checked ? "N" : "Y");
                DataChangeEvent.fire(screen, print);
            }

            public void onStateChange(StateChangeEvent<State> event) {
                fax.enable(EnumSet.of(State.DEFAULT).contains(event.getState()));
            }
        });

        destination = (Dropdown)def.getWidget("destination");
        addScreenHandler(destination, new ScreenEventHandler<Integer>() {
            public void onDataChange(DataChangeEvent event) {
                destination.setSelection(data.getDestinationId());
            }

            public void onValueChange(ValueChangeEvent<Integer> event) {
                TableDataRow row;
                FaxVO faxVO;
                
                data.setDestinationId(event.getValue());
                                                
                row = destination.getSelection();
                if (row != null) {
                    faxVO = (FaxVO)row.data;
                    data.setFaxNumber(faxVO.getFaxNumber());
                    data.setToName(faxVO.getToName());
                    data.setToCompany(faxVO.getToCompany());
                }
                
                DataChangeEvent.fire(screen,faxNumber);
                DataChangeEvent.fire(screen,toName);
                DataChangeEvent.fire(screen,toCompany);
            }

            public void onStateChange(StateChangeEvent<State> event) {
                destination.enable(false);
            }
        });
        
        faxNumber = (TextBox)def.getWidget("faxNumber");
        addScreenHandler(faxNumber, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                faxNumber.setValue(data.getFaxNumber());
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                data.setFaxNumber(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                faxNumber.enable(false);
            }
        });

        from = (TextBox)def.getWidget("from");
        addScreenHandler(from, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                from.setValue(data.getFrom());
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                data.setFrom(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                from.enable(false);
            }
        });

        toName = (TextBox)def.getWidget("toName");
        addScreenHandler(toName, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                toName.setValue(data.getToName());
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                data.setToName(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                toName.enable(false);
            }
        });

        toCompany = (TextBox)def.getWidget("toCompany");
        addScreenHandler(toCompany, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                toCompany.setValue(data.getToCompany());
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                data.setToCompany(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                toCompany.enable(false);
            }
        });

        comment = (TextArea)def.getWidget("comment");
        addScreenHandler(comment, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                comment.setValue(data.getNote());
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                data.setNote(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                comment.enable(false);
            }
        });

        runReportButton = (AppButton)def.getWidget("runReportButton");
        addScreenHandler(runReportButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                runReport();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                runReportButton.enable(EnumSet.of(State.DEFAULT).contains(event.getState()));
            }
        });

        resetButton = (AppButton)def.getWidget("resetButton");
        addScreenHandler(resetButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                reset();
                screen.clearErrors();
                DataChangeEvent.fire(screen);
                window.clearStatus();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                resetButton.enable(EnumSet.of(State.DEFAULT).contains(event.getState()));
            }
        });
    }

    private void initializeDropdowns() {
        ArrayList<TableDataRow> model;
        ArrayList<OptionListItem> options;

        model = new ArrayList<TableDataRow>();
        try {
            options = preferencesService.callList("getPrinters", "pdf");
            model.add(new TableDataRow(VIEW_PDF_KEY, consts.get("viewInPDF")));
            for (OptionListItem item : options)
                model.add(new TableDataRow(item.getKey(), item.getLabel()));
            billToTypeId = DictionaryCache.getIdBySystemName("org_bill_to");
            finalRepFaxTypeId = DictionaryCache.getIdBySystemName("org_finalrep_fax_number");
        } catch (Exception e) {
            window.close();
            Window.alert("Failed to load printers " + e.getMessage());
        }

        printer.setModel(model);
    }            
    
    private void runReport() {
        Integer id;
        Query query;
        QueryData field;

        setFocus(null);
        if ( !validate()) {
            window.setError(consts.get("correctErrors"));
            return;
        }
        
        query = new Query();    
        
        field = new QueryData();
        field.key = "ACCESSION_NUMBER";
        field.query = data.getAccessionNumber().toString();
        field.type = QueryData.Type.INTEGER;
        query.setFields(field);
        
        if ("Y".equals(data.getIsPrint())) {
            field = new QueryData();
            field.key = "PRINTER";
            field.query = data.getPrinter();
            field.type = QueryData.Type.STRING;
            query.setFields(field);
            
            id = data.getOrganizationId();
            if (id != null) {
                field = new QueryData();
                field.key = "ORGANIZATION_ID";
                field.query = id.toString();
                field.type = QueryData.Type.STRING;
                query.setFields(field);
            }
        } else if ("Y".equals(data.getIsFax())) {
            id = data.getDestinationId();
            if (id != null) {
                field = new QueryData();
                field.key = "ORGANIZATION_ID";
                field.query = id.toString();
                field.type = QueryData.Type.STRING;
                query.setFields(field);
            }
            
            field = new QueryData();
            field.key = "FAX_NUMBER";
            field.query = data.getFaxNumber();
            field.type = QueryData.Type.STRING;
            query.setFields(field);
            
            field = new QueryData();
            field.key = "FROM_COMPANY";
            field.query = data.getFrom();
            field.type = QueryData.Type.STRING;
            query.setFields(field);
            
            field = new QueryData();
            field.key = "FAX_ATTENTION";
            field.query = data.getToName();
            field.type = QueryData.Type.STRING;
            query.setFields(field);
            
            field = new QueryData();
            field.key = "TO_COMPANY";
            field.query = data.getToCompany();
            field.type = QueryData.Type.STRING;
            query.setFields(field);
            
            field = new QueryData();
            field.key = "FAX_NOTE";
            field.query = data.getNote();
            field.type = QueryData.Type.STRING;
            query.setFields(field);            
        }
        
        try {
            if (finalReportScreen == null) 
                finalReportScreen = new FinalReportScreen(window);  
            else
                finalReportScreen.setWindow(window);
            
            finalReportScreen.runReport(query);
        } catch (Exception e) {
            Window.alert(e.getMessage());
            e.printStackTrace();
        }
    }    
    
    private void reset() {
        Preferences prefs;
        
        data = new FormVO();
        data.setIsPrint("Y");
        data.setIsFax("N");     
        data.setFrom(consts.get("fromCompany"));
        try {
            prefs = Preferences.userRoot();
            data.setPrinter(prefs.get("default_printer", VIEW_PDF_KEY));
        } catch (Exception e) {
            Window.alert(e.getMessage());
            e.printStackTrace();
        }
        manager = null;
    }

    private void enablePrintFields(boolean enable) {
        organization.enable(enable);
        printer.enable(enable);
    }
    
    private void setPrintFieldsRequired(boolean required) {
        printer.getField().required = required;
    }
    
    private void clearExceptionsFromPrintFields() {
        printer.clearExceptions();
    }

    private void enableFaxFields(boolean enable) {
        destination.enable(enable);
        faxNumber.enable(enable);
        from.enable(enable);
        toName.enable(enable);
        toCompany.enable(enable);
        comment.enable(enable);
    }  
    
    private void setFaxFieldsRequired(boolean required) {
        faxNumber.getField().required = required;
        from.getField().required = required;
    }
    
    private void clearExceptionsFromFaxFields() {
        faxNumber.clearExceptions();
        from.clearExceptions();
    }
    
    private ArrayList<TableDataRow> getOrgModel() {
        int i;
        Integer orgId;
        ArrayList<Integer> orgIds;
        TableDataRow row;
        ArrayList<TableDataRow> model;
        SampleDO sample;
        SamplePrivateWellViewDO well;        
        OrganizationDO org;
        SampleOrganizationViewDO samOrg;
        SamplePrivateWellManager wellMan;        
        SampleOrganizationManager orgs;
        
        model = new ArrayList<TableDataRow>();
        row = new TableDataRow(null, "");
        model.add(row);

        if (manager == null)             
            return model;
        

        sample = manager.getSample();
        orgIds = new ArrayList<Integer>();
        try {
            if (SampleManager.WELL_DOMAIN_FLAG.equals(sample.getDomain())) {
                wellMan = (SamplePrivateWellManager)manager.getDomainManager();
                well = wellMan.getPrivateWell();
                org = well.getOrganization();
                /*
                 * if this sample is that of domain private well and if its report-to
                 * is an organization that it's added to the list shown in the dropdown  
                 */
                if (org != null) {
                    orgId = org.getId();
                    orgIds.add(orgId);
                    
                    row = new TableDataRow(orgId, org.getName());
                    model.add(row);
                }
            }

            orgs = manager.getOrganizations();
            /*
             * all unique organizations associated with the sample except the one
             * for bill-to are added to the dropdown  
             */
            for (i = 0; i < orgs.count(); i++ ) {
                samOrg = orgs.getOrganizationAt(i);
                orgId = samOrg.getOrganizationId();
                if ( !orgIds.contains(orgId) && !billToTypeId.equals(samOrg.getTypeId())) {
                    row = new TableDataRow(orgId, samOrg.getOrganizationName());
                    model.add(row);
                    orgIds.add(orgId);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            Window.alert(e.getMessage());
        }
        
        return model;
    }
    
    private ArrayList<TableDataRow> getDestModel() {
        int i;
        Integer orgId;
        ArrayList<Integer> orgIds;
        TableDataRow row;
        ArrayList<TableDataRow> model;
        SampleDO sample;
        SamplePrivateWellViewDO well;        
        OrganizationDO org;
        OrganizationParameterDO param;
        FaxVO faxVO;
        SampleOrganizationViewDO samOrg;
        SamplePrivateWellManager wellMan;        
        SampleOrganizationManager orgs;
        
        model = new ArrayList<TableDataRow>();

        if (manager == null) 
            return model;        

        sample = manager.getSample();
        orgIds = new ArrayList<Integer>();
        try {
            if (SampleManager.WELL_DOMAIN_FLAG.equals(sample.getDomain())) {
                wellMan = (SamplePrivateWellManager)manager.getDomainManager();
                well = wellMan.getPrivateWell();
                org = well.getOrganization();
                /*
                 * if this sample is that of domain private well and if its report-to
                 * is an organization that it's added to the list shown in the dropdown  
                 */
                if (org != null) {
                    orgId = org.getId();
                    orgIds.add(orgId);
                    
                    faxVO = new FaxVO();                    
                    faxVO.setToName(well.getReportToAttention());
                    faxVO.setToCompany(org.getName());
                    
                    param = getOrganizationFax(orgId);
                    if (param != null) 
                        faxVO.setFaxNumber(param.getValue());
                    
                    row = new TableDataRow(orgId, org.getName());
                    row.data = faxVO;                                                            
                    model.add(row);
                }
            }

            orgs = manager.getOrganizations();
            /*
             * all unique organizations associated with the sample are added to 
             * the dropdown  
             */
            for (i = 0; i < orgs.count(); i++ ) {
                samOrg = orgs.getOrganizationAt(i);
                orgId = samOrg.getOrganizationId();
                if ( !orgIds.contains(orgId) && !billToTypeId.equals(samOrg.getTypeId())) {                    
                    faxVO = new FaxVO();                
                    faxVO.setToCompany(samOrg.getOrganizationName());
                    faxVO.setToName(samOrg.getOrganizationAttention());
                    
                    param = getOrganizationFax(orgId);
                    if (param != null) 
                        faxVO.setFaxNumber(param.getValue());               
                    
                    row = new TableDataRow(orgId, samOrg.getOrganizationName());                    
                    row.data = faxVO;                    
                    model.add(row);
                    
                    orgIds.add(orgId);
                }
            }
            
            /*
             * this dropdown also shows the option for getting the values for 
             * fax # etc. from the aux-data, if any, for this sample
             */
            row = new TableDataRow(null, consts.get("fromSample"));
            row.data = getAuxDataFax();
            model.add(0, row);
        } catch (Exception e) {
            e.printStackTrace();
            Window.alert(e.getMessage());
        }
        
        return model;
    }
    
    private FaxVO getAuxDataFax() {
        FaxVO faxVO;
        AuxDataViewDO aux;
        AuxDataManager auxm;
        
        faxVO = new FaxVO();
        /*
         * fetch the AuxDataManager and set the values of attention
         * and fax number if the corresponding analytes are found  
         */
        try {
            auxm = manager.getAuxData();
            for (int i = 0; i < auxm.count(); i++ ) {
                aux = auxm.getAuxDataAt(i);
                if ("fax_to_attention".equals(aux.getAnalyteExternalId()))
                    faxVO.setToName(aux.getValue());
                else if ("final_report_fax_num".equals(aux.getAnalyteExternalId()))
                    faxVO.setFaxNumber(aux.getValue());
            }
        } catch (Exception e) {
            e.printStackTrace();
            Window.alert(e.getMessage());
        }
        
        return faxVO;
    }
    
    private OrganizationParameterDO getOrganizationFax(Integer orgId) {
        OrganizationParameterManager opm;
        OrganizationParameterDO op;

        try {
            /*
             * return the parameter of type "final report fax number" if this 
             * organization has one
             */
            opm = OrganizationParameterManager.fetchByOrganizationId(orgId);
            for (int i = 0; i < opm.count(); i++ ) {
                op = opm.getParameterAt(i);
                if (finalRepFaxTypeId.equals(op.getTypeId()))
                    return op;
            }
        } catch (NotFoundException ignE) {
            // ignore
        } catch (Exception e) {
            e.printStackTrace();
            Window.alert(e.getMessage());
        }

        return null;
    }
    
    class FaxVO {
        private String faxNumber, from, toName, toCompany;
        
        public String getFaxNumber() {
            return faxNumber;
        }

        public void setFaxNumber(String faxNumber) {
            this.faxNumber = DataBaseUtil.toString(faxNumber);
        }

        public String getFrom() {
            return from;
        }

        public void setFrom(String from) {
            this.from = DataBaseUtil.toString(from);
        }

        public String getToName() {
            return toName;
        }

        public void setToName(String toName) {
            this.toName = DataBaseUtil.toString(toName);
        }

        public String getToCompany() {
            return toCompany;
        }

        public void setToCompany(String toCompany) {
            this.toCompany = DataBaseUtil.toString(toCompany);
        }
    }
    
    class FormVO extends FaxVO {

        private Integer accessionNumber, organizationId, destinationId;
        private String isPrint, printer, isFax, note;   

        public Integer getAccessionNumber() {
            return accessionNumber;
        }

        public void setAccessionNumber(Integer accessionNumber) {
            this.accessionNumber = accessionNumber;
        }

        public Integer getOrganizationId() {
            return organizationId;
        }

        public void setOrganizationId(Integer organizationId) {
            this.organizationId = organizationId;
        }

        public String getIsPrint() {
            return isPrint;
        }

        public void setIsPrint(String isPrint) {
            this.isPrint = DataBaseUtil.toString(isPrint);
        }

        public String getPrinter() {
            return printer;
        }

        public void setPrinter(String printer) {
            this.printer = DataBaseUtil.toString(printer);
        }

        public String getIsFax() {
            return isFax;
        }

        public void setIsFax(String isFax) {
            this.isFax = DataBaseUtil.toString(isFax);
        }

        public Integer getDestinationId() {
            return destinationId;
        }

        public void setDestinationId(Integer destinationId) {
            this.destinationId = destinationId;
        }
        
        public String getNote() {
            return note;
        }

        public void setNote(String note) {
            this.note = DataBaseUtil.toString(note);
        } 
    }
}