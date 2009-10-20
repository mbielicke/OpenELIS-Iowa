/** Exhibit A - UIRF Open-source Based Public Software License.
* 
* The contents of this file are subject to the UIRF Open-source Based
* Public Software License(the "License"); you may not use this file except
* in compliance with the License. You may obtain a copy of the License at
* openelis.uhl.uiowa.edu
* 
* Software distributed under the License is distributed on an "AS IS"
* basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
* License for the specific language governing rights and limitations
* under the License.
* 
* The Original Code is OpenELIS code.
* 
* The Initial Developer of the Original Code is The University of Iowa.
* Portions created by The University of Iowa are Copyright 2006-2008. All
* Rights Reserved.
* 
* Contributor(s): ______________________________________.
* 
* Alternatively, the contents of this file marked
* "Separately-Licensed" may be used under the terms of a UIRF Software
* license ("UIRF Software License"), in which case the provisions of a
* UIRF Software License are applicable instead of those above. 
*/
package org.openelis.modules.environmentalSampleLogin.client;

import java.util.ArrayList;
import java.util.EnumSet;

import org.openelis.gwt.event.DataChangeEvent;
import org.openelis.gwt.event.StateChangeEvent;
import org.openelis.gwt.screen.Screen;
import org.openelis.gwt.screen.ScreenDefInt;
import org.openelis.gwt.screen.ScreenEventHandler;
import org.openelis.gwt.services.ScreenService;
import org.openelis.gwt.widget.AppButton;
import org.openelis.gwt.widget.TextBox;
import org.openelis.gwt.widget.table.TableDataRow;
import org.openelis.gwt.widget.table.TableWidget;
import org.openelis.gwt.widget.table.event.CellEditedEvent;
import org.openelis.gwt.widget.table.event.CellEditedHandler;
import org.openelis.gwt.widget.table.event.RowAddedEvent;
import org.openelis.gwt.widget.table.event.RowAddedHandler;
import org.openelis.gwt.widget.table.event.RowDeletedEvent;
import org.openelis.gwt.widget.table.event.RowDeletedHandler;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;

public class AuxDataTab extends Screen {
    private boolean loaded;
    protected Screen parentScreen;
    
    protected TableWidget auxGroupsTable, auxValsTable;
    protected AppButton addAuxButton, removeAuxButton;
    protected TextBox auxMethod, auxUnits, auxDesc;
    
    //protected StorageManager manager;
    
    public AuxDataTab(ScreenDefInt def, Screen parentScreen) {
        service = new ScreenService("OpenELISServlet?service=org.openelis.modules.auxiliary.server.AuxiliaryService");
        setDef(def);
        this.parentScreen = parentScreen;
        
        initialize();
    }
    
    private void initialize() {
        auxGroupsTable = (TableWidget)def.getWidget("auxGroupsTable");
        addScreenHandler(auxGroupsTable, new ScreenEventHandler<ArrayList<TableDataRow>>() {
            public void onDataChange(DataChangeEvent event) {
                //auxGroupsTable.load(); // FIXME load(model)
            }

            public void onStateChange(StateChangeEvent<State> event) {
                auxGroupsTable.enable(EnumSet.of(State.QUERY,State.ADD,State.UPDATE).contains(event.getState()));
                auxGroupsTable.setQueryMode(event.getState() == State.QUERY);
            }
        });

        auxGroupsTable.addCellEditedHandler(new CellEditedHandler() {
            public void onCellUpdated(CellEditedEvent event) {
                int r, c;
                Object val;

              //  val = auxGroupsTable.getObject(r,c);

// FIXME missing table col!!! using old table format?

            }
        });

        auxGroupsTable.addRowAddedHandler(new RowAddedHandler() {
            public void onRowAdded(RowAddedEvent event) {
                // FIXME add row added handler
            }
        });

        auxGroupsTable.addRowDeletedHandler(new RowDeletedHandler() {
            public void onRowDeleted(RowDeletedEvent event) {
                // FIXME add row delete handler;
            }
        });

        addAuxButton = (AppButton)def.getWidget("addAuxButton");
        addScreenHandler(addAuxButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                // FIXME add on click handler
            }

            public void onStateChange(StateChangeEvent<State> event) {
                addAuxButton.enable(EnumSet.of(State.DISPLAY).contains(event.getState()));
            }
        });

        removeAuxButton = (AppButton)def.getWidget("removeAuxButton");
        addScreenHandler(removeAuxButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                // FIXME add on click handler
            }

            public void onStateChange(StateChangeEvent<State> event) {
                removeAuxButton.enable(EnumSet.of(State.DISPLAY).contains(event.getState()));
            }
        });

        auxValsTable = (TableWidget)def.getWidget("auxValsTable");
        addScreenHandler(auxValsTable, new ScreenEventHandler<ArrayList<TableDataRow>>() {
            public void onDataChange(DataChangeEvent event) {
              //  auxValsTable.load(); // FIXME load(model)
            }

            public void onStateChange(StateChangeEvent<State> event) {
                auxValsTable.enable(EnumSet.of(State.QUERY,State.ADD,State.UPDATE).contains(event.getState()));
                auxValsTable.setQueryMode(event.getState() == State.QUERY);
            }
        });

        auxValsTable.addCellEditedHandler(new CellEditedHandler() {
            public void onCellUpdated(CellEditedEvent event) {
                int r, c;
                Object val;

              //  val = auxValsTable.getObject(r,c);

// FIXME missing table col!!! using old table format?

            }
        });

        auxValsTable.addRowAddedHandler(new RowAddedHandler() {
            public void onRowAdded(RowAddedEvent event) {
                // FIXME add row added handler
            }
        });

        auxValsTable.addRowDeletedHandler(new RowDeletedHandler() {
            public void onRowDeleted(RowDeletedEvent event) {
                // FIXME add row delete handler;
            }
        });

        auxMethod = (TextBox)def.getWidget("auxMethod");
        addScreenHandler(auxMethod, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
         //       auxMethod.setValue(AUXMETHOD_DO.auxMethod());
            }

            public void onValueChange(ValueChangeEvent<String> event) {
         //       AUXMETHOD_DO.auxMethod(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                auxMethod.enable(EnumSet.of(State.QUERY,State.ADD,State.UPDATE,State.DELETE).contains(event.getState()));
                auxMethod.setQueryMode(event.getState() == State.QUERY);
            }
        });

        auxUnits = (TextBox)def.getWidget("auxUnits");
        addScreenHandler(auxUnits, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
         //       auxUnits.setValue(AUXUNITS_DO.auxUnits());
            }

            public void onValueChange(ValueChangeEvent<String> event) {
        //        AUXUNITS_DO.auxUnits(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                auxUnits.enable(EnumSet.of(State.QUERY,State.ADD,State.UPDATE,State.DELETE).contains(event.getState()));
                auxUnits.setQueryMode(event.getState() == State.QUERY);
            }
        });

        auxDesc = (TextBox)def.getWidget("auxDesc");
        addScreenHandler(auxDesc, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
          //      auxDesc.setValue(AUXDESC_DO.auxDesc());
            }

            public void onValueChange(ValueChangeEvent<String> event) {
         //       AUXDESC_DO.auxDesc(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                auxDesc.enable(EnumSet.of(State.QUERY,State.ADD,State.UPDATE,State.DELETE).contains(event.getState()));
                auxDesc.setQueryMode(event.getState() == State.QUERY);
            }
        });
    }
}
