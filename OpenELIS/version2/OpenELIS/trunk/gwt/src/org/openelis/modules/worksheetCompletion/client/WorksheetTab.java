package org.openelis.modules.worksheetCompletion.client;

import java.util.ArrayList;
import java.util.EnumSet;

import org.openelis.cache.DictionaryCache;
import org.openelis.domain.AnalysisViewDO;
import org.openelis.domain.DictionaryDO;
import org.openelis.domain.OrganizationContactDO;
import org.openelis.domain.QcDO;
import org.openelis.domain.WorksheetAnalysisDO;
import org.openelis.domain.WorksheetItemDO;
import org.openelis.gwt.event.DataChangeEvent;
import org.openelis.gwt.event.StateChangeEvent;
import org.openelis.gwt.screen.Screen;
import org.openelis.gwt.screen.ScreenDefInt;
import org.openelis.gwt.screen.ScreenEventHandler;
import org.openelis.gwt.widget.AppButton;
import org.openelis.gwt.widget.Dropdown;
import org.openelis.gwt.widget.ScreenWindow;
import org.openelis.gwt.widget.table.TableDataRow;
import org.openelis.gwt.widget.table.TableRow;
import org.openelis.gwt.widget.table.TableWidget;
import org.openelis.gwt.widget.table.event.BeforeCellEditedEvent;
import org.openelis.gwt.widget.table.event.BeforeCellEditedHandler;
import org.openelis.gwt.widget.table.event.CellEditedEvent;
import org.openelis.gwt.widget.table.event.CellEditedHandler;
import org.openelis.gwt.widget.table.event.RowAddedEvent;
import org.openelis.gwt.widget.table.event.RowAddedHandler;
import org.openelis.gwt.widget.table.event.RowDeletedEvent;
import org.openelis.gwt.widget.table.event.RowDeletedHandler;
import org.openelis.gwt.widget.table.event.UnselectionEvent;
import org.openelis.gwt.widget.table.event.UnselectionHandler;
import org.openelis.manager.AnalysisManager;
import org.openelis.manager.OrganizationManager;
import org.openelis.manager.QcManager;
import org.openelis.manager.SampleDataBundle;
import org.openelis.manager.SampleItemManager;
import org.openelis.manager.SampleManager;
import org.openelis.manager.WorksheetAnalysisManager;
import org.openelis.manager.WorksheetManager;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.user.client.Window;

public class WorksheetTab extends Screen {

    private boolean          loaded;
    private Integer          formatBatch, formatTotal;
    private AppButton        editMultipleButton;
    private TableWidget      table;
    private WorksheetManager manager;

    public WorksheetTab(ScreenDefInt def, ScreenWindow window) {
        setDefinition(def);
        setWindow(window);
        initialize();
        
        initializeDropdowns();
    }

    private void initialize() {
        table = (TableWidget)def.getWidget("worksheetItemTable");
        addScreenHandler(table, new ScreenEventHandler<ArrayList<TableDataRow>>() {
            public void onDataChange(DataChangeEvent event) {
                if (state != State.QUERY)
                    table.load(getTableModel());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                table.enable(true);
            }
        });

        table.addSelectionHandler(new SelectionHandler<TableRow>() {
            public void onSelection(SelectionEvent event) {
                if (table.getSelectedRow() != -1)
                    editMultipleButton.enable(true);
            }
        });
        
        table.addUnselectionHandler(new UnselectionHandler<TableDataRow>() {
            public void onUnselection(UnselectionEvent event) {
                if (table.getSelectedRow() == -1)
                    editMultipleButton.enable(false);
            }
        });
        
        table.addBeforeCellEditedHandler(new BeforeCellEditedHandler() {
            public void onBeforeCellEdited(BeforeCellEditedEvent event) {
                event.cancel();
            }
        });

        table.addCellEditedHandler(new CellEditedHandler() {
            public void onCellUpdated(CellEditedEvent event) {
/*
                int r, c;
                Object val;
                OrganizationContactDO data;

                if (state == State.QUERY)
                    return;

                r = event.getRow();
                c = event.getCol();
                val = table.getObject(r,c);

                try {
                    data = manager.getContacts().getContactAt(r);
                } catch (Exception e) {
                    Window.alert(e.getMessage());
                    return;
                }

                switch (c) {
                    case 0:
                        data.setContactTypeId((Integer)val);
                        break;
                    case 1:
                        data.setName((String)val);
                        break;
                    case 2:
                        data.getAddressDO().setMultipleUnit((String)val);
                        break;
                    case 3:
                        data.getAddressDO().setStreetAddress((String)val);
                        break;
                    case 4:
                        data.getAddressDO().setCity((String)val);
                        break;
                    case 5:
                        data.getAddressDO().setState((String)val);
                        break;
                    case 6:
                        data.getAddressDO().setZipCode((String)val);
                        break;
                    case 7:
                        data.getAddressDO().setCountry((String)val);
                        break;
                    case 8:
                        data.getAddressDO().setWorkPhone((String)val);
                        break;
                    case 9:
                        data.getAddressDO().setHomePhone((String)val);
                        break;
                    case 10:
                        data.getAddressDO().setCellPhone((String)val);
                        break;
                    case 11:
                        data.getAddressDO().setFaxPhone((String)val);
                        break;
                    case 12:
                        data.getAddressDO().setEmail((String)val);
                        break;
                }
*/
            }
        });

        editMultipleButton = (AppButton)def.getWidget("editMultipleButton");
        addScreenHandler(editMultipleButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                editMultiple();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                editMultipleButton.enable(false);
            }
        });
    }

    private void initializeDropdowns() {
        ArrayList<DictionaryDO> dictList;
        ArrayList<TableDataRow> model;

        try {
            formatBatch = DictionaryCache.getIdFromSystemName("wsheet_num_format_batch");
            formatTotal = DictionaryCache.getIdFromSystemName("wsheet_num_format_total");
        } catch (Exception e) {
            Window.alert(e.getMessage());
            window.close();
        }
        
        //
        // load analysis status dropdown model
        //
        dictList  = DictionaryCache.getListByCategorySystemName("analysis_status");
        model = new ArrayList<TableDataRow>();
        model.add(new TableDataRow(null, ""));
        for (DictionaryDO resultDO : dictList)
            model.add(new TableDataRow(resultDO.getId(),resultDO.getEntry()));
        ((Dropdown<Integer>)table.getColumns().get(6).getColumnWidget()).setModel(model);
    }
    
    private ArrayList<TableDataRow> getTableModel() {
        int                      i, j;
        ArrayList<TableDataRow>  model;
        TableDataRow             row;
        AnalysisManager          aManager;
        AnalysisViewDO           aVDO;
        QcManager                qcManager;
        SampleDataBundle         bundle;
        SampleManager            sManager;
        SampleItemManager        siManager;
        WorksheetAnalysisDO      waDO;
        WorksheetAnalysisManager waManager;
        WorksheetItemDO          wiDO;
        
        model = new ArrayList<TableDataRow>();
        if (manager == null)
            return model;

        try {
            for (i = 0; i < manager.getItems().count(); i++) {
                wiDO = manager.getItems().getWorksheetItemAt(i);
                waManager = manager.getItems().getWorksheetAnalysisAt(i);

                row = new TableDataRow(9);
                row.cells.get(0).value = getPositionNumber(wiDO.getPosition());
                for (j = 0; j < waManager.count(); j++) {
                    waDO = waManager.getWorksheetAnalysisAt(j);

                    if (j != 0)
                        row.cells.get(0).value = null;
                    
                    row.cells.get(1).value = waDO.getAccessionNumber();

                    if (waDO.getAnalysisId() != null) {
                        bundle = waManager.getBundleAt(j);
                        sManager = bundle.getSampleManager();
                        aManager = sManager.getSampleItems().getAnalysisAt(bundle.getSampleItemIndex());
                        aVDO = aManager.getAnalysisAt(bundle.getAnalysisIndex());

                        row.cells.get(2).value = "";
                        row.cells.get(3).value = "";
                        row.cells.get(4).value = aVDO.getTestName();
                        row.cells.get(5).value = aVDO.getMethodName();
                        row.cells.get(6).value = aVDO.getStatusId();
                        row.cells.get(7).value = "";
                        row.cells.get(8).value = "";
                        row.data = bundle;
                    } else if (waDO.getQcId() != null) {
                        qcManager = QcManager.fetchById(waDO.getQcId());
                        
                        row.cells.get(2).value = qcManager.getQc().getName();
                        row.cells.get(3).value = "";
                        row.cells.get(4).value = "";
                        row.cells.get(5).value = "";
                        row.cells.get(6).value = 0;
                        row.cells.get(7).value = "";
                        row.cells.get(8).value = "";
                        row.data = qcManager;
                    }
                }
                model.add(row);
            }
        } catch (Exception e) {
            Window.alert(e.getMessage());
            e.printStackTrace();
        }

        return model;
    }

    public void setManager(WorksheetManager manager) {
        this.manager = manager;
        loaded = false;
    }

    public void draw() {
        if ( !loaded)
            DataChangeEvent.fire(this);

        loaded = true;
    }
    
    protected void editMultiple() {
        // TODO - Add edit multiple code
        Window.alert("Edit Multiple Popup");
    }

    private Object getPositionNumber(int position) {
        int    major, minor;
        Object positionNumber;
        
        positionNumber = "";
        if (formatBatch.equals(manager.getWorksheet().getFormatId())) {
            major = getPositionMajorNumber(position+1);
            minor = getPositionMinorNumber(position+1);
            positionNumber = major+"-"+minor;
        } else if (formatTotal.equals(manager.getWorksheet().getFormatId())) {
            positionNumber = position + 1;
        }
        
        return positionNumber;
    }
    
    /**
     * Parses the position number and returns the major number
     * for batch numbering.
     */
   private int getPositionMajorNumber(int position) {
       return (int) (position / (double)manager.getWorksheet().getBatchCapacity() + .99);
   }

   /**
     * Parses the position number and returns the minor number
     * for batch numbering.
     */
   private int getPositionMinorNumber(int position) {
       return position - (getPositionMajorNumber(position) - 1) * manager.getWorksheet().getBatchCapacity();
   }
}
