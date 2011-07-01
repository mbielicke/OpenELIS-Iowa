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
package org.openelis.web.modules.notificationPreference.client;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;

import org.openelis.cache.DictionaryCache;
import org.openelis.cache.UserCache;
import org.openelis.domain.OrganizationParameterDO;
import org.openelis.domain.OrganizationViewDO;
import org.openelis.gwt.common.EntityLockedException;
import org.openelis.gwt.common.ModulePermission;
import org.openelis.gwt.common.PermissionException;
import org.openelis.gwt.common.data.Query;
import org.openelis.gwt.common.data.QueryData;
import org.openelis.gwt.event.ActionEvent;
import org.openelis.gwt.event.ActionHandler;
import org.openelis.gwt.event.DataChangeEvent;
import org.openelis.gwt.event.StateChangeEvent;
import org.openelis.gwt.screen.Screen;
import org.openelis.gwt.screen.ScreenDefInt;
import org.openelis.gwt.screen.ScreenEventHandler;
import org.openelis.gwt.services.ScreenService;
import org.openelis.gwt.widget.AppButton;
import org.openelis.gwt.widget.Dropdown;
import org.openelis.gwt.widget.ScreenWindow;
import org.openelis.gwt.widget.table.TableDataRow;
import org.openelis.gwt.widget.table.TableRow;
import org.openelis.gwt.widget.table.TableWidget;
import org.openelis.gwt.widget.table.event.BeforeCellEditedEvent;
import org.openelis.gwt.widget.table.event.BeforeCellEditedHandler;
import org.openelis.manager.OrganizationManager;
import org.openelis.manager.OrganizationParameterManager;
import org.openelis.web.modules.notificationPreference.client.AddEditEmailScreen.Action;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.Window;

public class NotificationPreferenceScreen extends Screen {

    private ArrayList<OrganizationManager> managerList;    
    private ModulePermission               userPermission;
    private Integer                        receivableReportToId, releasedReportToId;
    private AppButton                      addButton, editButton, removeButton;
    private TableWidget                    table;    
    private AddEditEmailScreen             addEditEmailScreen;
    private String                         clause;
    private Query                          idList;
    
    /**
     * No-Arg constructor
     */
    public NotificationPreferenceScreen() throws Exception {
        super((ScreenDefInt)GWT.create(NotificationPreferenceDef.class));
        service = new ScreenService("controller?service=org.openelis.modules.organization.server.OrganizationService");

        userPermission = UserCache.getPermission().getModule("w_notify");
        if (userPermission == null)
            throw new PermissionException("screenPermException", "Notification Preference Screen");

        clause = userPermission.getClause();

        DeferredCommand.addCommand(new Command() {
            public void execute() {
                postConstructor();
            }
        });
    }

    /**
     * This method is called to set the initial state of widgets after the
     * screen is attached to the browser. It is usually called in deferred
     * command.
     */
    private void postConstructor() {                
        fetchByIdList();
        initialize();
        setState(State.ADD);
        initializeDropdowns();
        DataChangeEvent.fire(this);
    }

    /**
     * Initialize widgets
     */
    private void initialize() {
        table = (TableWidget)def.getWidget("orgTable");
        addScreenHandler(table, new ScreenEventHandler<ArrayList<TableDataRow>>() {
            public void onDataChange(DataChangeEvent event) {
                table.load(getTableModel());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                table.enable(true);
            }
        });
        
        table.addSelectionHandler(new SelectionHandler<TableRow>() {            
            public void onSelection(SelectionEvent<TableRow> event) {
                setState(State.UPDATE);
            }
        });

        table.addBeforeCellEditedHandler(new BeforeCellEditedHandler() {
            public void onBeforeCellEdited(BeforeCellEditedEvent event) {
                event.cancel();               
            }
        });

        addButton = (AppButton)def.getWidget("addButton");
        addScreenHandler(addButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                AddEditEmailVO data;
                OrganizationParameterDO par;
                TableDataRow row;
                ArrayList<OrganizationParameterDO> list;
                
                data = new AddEditEmailVO();
                row = table.getSelection();     
                /*
                 * if a row was selected, the widgets on AddEditEmailScreen are 
                 * initialized with the data in the row otherwise the widgets are
                 * set to the default values 
                 */
                if (row  == null) {
                    data.setOrganizationId(managerList.get(0).getOrganization().getId());
                } else {
                    list = (ArrayList<OrganizationParameterDO>)row.data;       
                    par = list.get(0);                                                    
                    data.setOrganizationId(par.getOrganizationId());
                }
                setState(State.ADD);
                showAddEdit(data);              
            }

            public void onStateChange(StateChangeEvent<State> event) {
                addButton.enable(true);
            }
        });

        editButton = (AppButton)def.getWidget("editButton");
        addScreenHandler(editButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {                
                AddEditEmailVO data;
                OrganizationParameterDO par;
                TableDataRow row;
                ArrayList<OrganizationParameterDO> list;
                                
                /*
                 * the widgets on AddEditEmailScreen are initialized with the data
                 * in the selected row 
                 */
                row = table.getSelection();                
                data = new AddEditEmailVO();
                list = (ArrayList<OrganizationParameterDO>) row.data;
                par = list.get(0);                               
                data.setOrganizationId(par.getOrganizationId());
                data.setEmail(par.getValue());
                data.setForReceived((String)row.cells.get(2).getValue());
                data.setForReleased((String)row.cells.get(3).getValue());
                showAddEdit(data);           
            }

            public void onStateChange(StateChangeEvent<State> event) {
                editButton.enable(EnumSet.of(State.UPDATE).contains(event.getState()));
            }
        });
        
        removeButton = (AppButton)def.getWidget("removeButton");
        addScreenHandler(removeButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                delete();           
            }

            public void onStateChange(StateChangeEvent<State> event) {
                removeButton.enable(EnumSet.of(State.UPDATE).contains(event.getState()));
            }
        });             
    }

    private void initializeDropdowns() {
        OrganizationViewDO data;
        ArrayList<TableDataRow> model;
        Dropdown<Integer> org;

        model = new ArrayList<TableDataRow>();
        model.add(new TableDataRow(null, ""));
        
        try {
            releasedReportToId = DictionaryCache.getBySystemName("released_reportto_email").getId();
            receivableReportToId = DictionaryCache.getBySystemName("receivable_reportto_email").getId();
        } catch (Exception e) {
            Window.alert(e.getMessage());
            window.close();
        }

        model = new ArrayList<TableDataRow>();
        for (OrganizationManager man : managerList) {
            data = man.getOrganization();            
            model.add(new TableDataRow(data.getId(), data.getName()));
        }

        org = ((Dropdown<Integer>)table.getColumns().get(0).getColumnWidget());
        org.setModel(model);
    }

    private ArrayList<TableDataRow> getTableModel() {
        int j;
        boolean isRec, isRel;
        Integer ind;
        String email;
        OrganizationParameterDO data;
        OrganizationParameterManager man;
        ArrayList<TableDataRow> model;
        TableDataRow row;
        HashMap<String, Integer> emIndMap;
        ArrayList<OrganizationParameterDO> list;

        model = new ArrayList<TableDataRow>();
        if (managerList == null)
            return model;

        try {
            j = -1;
            for (OrganizationManager manager : managerList) {
                man = manager.getParameters();
                emIndMap = new HashMap<String, Integer>();
                for (int i = 0; i < man.count(); i++) {                                      
                    data = man.getParameterAt(i);
                    isRel = releasedReportToId.equals(data.getTypeId());
                    isRec = receivableReportToId.equals(data.getTypeId());
                    if (!isRel && !isRec)
                        continue;      
                    /*
                     * since we need to show just one row for any email even though
                     * it may have been added twice to an organization, we have to
                     * keep track of the index where the email was first added in
                     * the model and update that row appropriately if we encounter
                     * the email again 
                     */
                    email = data.getValue();
                    ind = emIndMap.get(email);
                    if (ind != null) {    
                         //
                         // a row for this email has already been added     
                         //
                        row = model.get(ind);                  
                        list = (ArrayList<OrganizationParameterDO>)row.data;
                        list.add(data);
                    } else {
                        /*
                         * Since this is the first time this email has been encountered,
                         * a new row is created in the model and a new entry
                         * in the hashmap(emIndMap) is added with the email as the 
                         * key and the index of the row in the model as the value. 
                         * The two checkboxes are also set by default to "N" so 
                         * that if this email has been added for only one type
                         * and thus isn't found again, the checkbox for the missing
                         * type will show up (correctly) unchecked.                          
                         */
                        row = new TableDataRow(4);
                        row.key = data.getId();
                        row.cells.get(0).setValue(manager.getOrganization().getId());                    
                        row.cells.get(1).setValue(email); 
                        row.cells.get(2).setValue("N"); 
                        row.cells.get(3).setValue("N"); 
                        list = new ArrayList<OrganizationParameterDO>();
                        list.add(data);
                        row.data = list;                        
                        emIndMap.put(email, ++j);                        
                        model.add(row);
                    }
                    if (isRec) 
                        row.cells.get(2).setValue("Y");
                    else 
                        row.cells.get(3).setValue("Y");                      
                }                                
            }
        } catch (Exception e) {
            Window.alert(e.getMessage());
            e.printStackTrace();
        }

        return model;
    }  

    private void fetchByIdList() {
        if (idList == null) {
            idList = getIdListFromClause();     
            /*
             * if idList is null then there aren't any organizations specified in
             * this user's system user module for this screen   
             */
            if (idList == null) {
                Window.alert(consts.get("noPermToAddEmailException"));               
                return;
            }
        }
        window.setBusy(consts.get("fetching"));
        try {
            managerList = service.callList("fetchByIdList", idList);
        } catch (Exception e) {
            e.printStackTrace();
            Window.alert(e.getMessage());
        }
        window.clearStatus();
    } 

    private Query getIdListFromClause() {
        String full[], part[];
        Query query;
        QueryData field;

        //
        // the structure of the clause should be "organizationid:id1,id2,..."
        //
        full = clause.split(":");
        query = new Query();
        part = full[1].split(",");
        if (part.length == 0)
            return null;
        for (int i = 0; i < part.length; i++ ) {            
            field = new QueryData();
            field.key = "ORG_ID";
            field.query = part[i];
            field.type = QueryData.Type.INTEGER;
            query.setFields(field);
        }

        return query;
    }   
    
    private void showAddEdit(AddEditEmailVO data) {
        int h,l;
        ScreenWindow modal;
        
        if (addEditEmailScreen == null) {
            try {
                addEditEmailScreen = new AddEditEmailScreen(managerList);
            } catch (Exception e) {
                e.printStackTrace();
                Window.alert("AddEditEmailScreen error: " + e.getMessage());
                return;
            }   
            
            addEditEmailScreen.addActionHandler(new ActionHandler<AddEditEmailScreen.Action>() {                              
                public void onAction(ActionEvent<Action> event) {
                    AddEditEmailVO data;
                    
                    data = (AddEditEmailVO)event.getData();
                    if (data == null) 
                        return;
                    
                    if (state == State.ADD)                  
                        add(data);
                    else 
                        update(data);                                                                                                       
                }
            });
        }
        
        h = this.getOffsetHeight();
        l = this.getAbsoluteLeft();
        
        modal = new ScreenWindow(ScreenWindow.Mode.DIALOG, true);
        modal.setContent(addEditEmailScreen, l+10, h-60);
        addEditEmailScreen.setState(state);
        addEditEmailScreen.clearFields(data);
    }
    
    
    private void add(AddEditEmailVO data) {
        OrganizationManager oman; 
        OrganizationParameterManager pman;
        ArrayList<OrganizationParameterDO> list;
        
        window.setBusy(consts.get("adding"));
        try {
             //
             // we try to lock the manager on the screen to be updated and commit its data  
             //
            for (int i = 0; i < managerList.size(); i++) {                
                oman = managerList.get(i);
                if (oman.getOrganization().getId().equals(data.getOrganizationId())) {
                    oman = oman.fetchForUpdate();  
                    pman = oman.getParameters();
                    list = createParameters(pman,data);
                    /*
                     * a DO is created for each type that the email entered on 
                     * AddEditEmailScreen is specified to belong to and each of
                     * those DO's is added to the OrganizationParameterManager 
                     * in this OrganizationManager    
                     */
                    for (int j = 0; j < list.size(); j++) 
                        pman.addParameter(list.get(j));                    
                    managerList.set(i, oman.update());
                    break;
                }
            }
            
            setState(State.DEFAULT);
            DataChangeEvent.fire(this);
            window.setDone(consts.get("addingComplete"));
        } catch (EntityLockedException e) {
            Window.alert(consts.get("recordNotAvailableLockException"));
            window.clearStatus();
        } catch (Exception e) {
            Window.alert(e.getMessage());
            e.printStackTrace();
            window.clearStatus();
        }
    }
    
    private void update(AddEditEmailVO data) {
        OrganizationManager oman; 
        OrganizationParameterManager pman;
        ArrayList<OrganizationParameterDO> list;
        OrganizationParameterDO par;
        TableDataRow row;
        
        
        window.setBusy(consts.get("updating"));
        try {
             //
             // we try to lock the manager on the screen to be updated and commit its data  
             //
            row = table.getSelection();
            list = (ArrayList<OrganizationParameterDO>)row.data;            
            for (int i = 0; i < managerList.size(); i++) {                
                oman = managerList.get(i);
                if (oman.getOrganization().getId().equals(data.getOrganizationId())) {
                    oman = oman.fetchForUpdate();  
                    pman = oman.getParameters();
                    /*
                     * all the DO's in the fetched manager corresponding to 
                     * the ones linked to this row are tried to be found and
                     * their values are set to data's email     
                     */
                    for (int j = 0; j < pman.count(); j++) {
                        par = pman.getParameterAt(j);
                        for (int k = 0; k < list.size(); k++) {
                            if (par.getId().equals(list.get(k).getId())) {
                                par.setValue(data.getEmail());
                                break;
                            }
                        }
                    }
                    managerList.set(i, oman.update());
                    break;
                }
            }
            
            setState(State.DEFAULT);
            DataChangeEvent.fire(this);
            window.setDone(consts.get("updatingComplete"));
        } catch (EntityLockedException e) {
            Window.alert(consts.get("recordNotAvailableLockException"));
            window.clearStatus();
        } catch (Exception e) {
            Window.alert(e.getMessage());
            e.printStackTrace();
            window.clearStatus();
        }
    }
    
    private void delete() {
        OrganizationManager oman; 
        OrganizationParameterManager pman;
        ArrayList<OrganizationParameterDO> list;
        OrganizationParameterDO data, par;
        TableDataRow row;
                
        window.setBusy(consts.get("deleting"));
        try {
             //
             // we try to lock the manager on the screen to be updated and commit its data  
             //
            row = table.getSelection();
            list = (ArrayList<OrganizationParameterDO>)row.data;            
            for (int i = 0; i < managerList.size(); i++) {                
                oman = managerList.get(i);
                if (oman.getOrganization().getId().equals(list.get(0).getOrganizationId())) {
                    oman = oman.fetchForUpdate();  
                    pman = oman.getParameters();
                    /*
                     * all the DO's in the fetched OrganizationParameterManager
                     * that have the same ids as the ones linked to the row being
                     * deleted are searched for and removed if found
                     */
                    for (int j = 0; j < list.size(); j++) {
                        data = list.get(j);
                        for (int k = 0; k < pman.count(); k++) {
                            par = pman.getParameterAt(k);
                            if (par.getId().equals(data.getId())) {
                                pman.removeParameter(par);
                                break;
                            }
                        }
                    }
                    managerList.set(i, oman.update());
                    break;
                }
            }
            
            setState(State.DEFAULT);
            DataChangeEvent.fire(this);
            window.setDone(consts.get("deleteComplete"));
        } catch (EntityLockedException e) {
            Window.alert(consts.get("recordNotAvailableLockException"));
            window.clearStatus();
        } catch (Exception e) {
            Window.alert(e.getMessage());
            e.printStackTrace();
            window.clearStatus();
        }        
    }
    
    private ArrayList<OrganizationParameterDO> createParameters(OrganizationParameterManager man, AddEditEmailVO data) {        
        boolean addRec, addRel;        
        ArrayList<OrganizationParameterDO> list;
        OrganizationParameterDO par;
        
        list = new ArrayList<OrganizationParameterDO>();
        addRec = true;
        addRel = true;        
        
        /*
         * If an email address already exists in the manager for a given type
         * e.g. "Released Report To" a new DO for that type and that email isn't
         * created and added. Thus even if "data" has its field for that type e.g.
         * "forReleased" set to "Y", the request is ignored. If however the email
         * can't be found in the manager, a DO is created for each type that has 
         * the field corresponding to it in "data" set to "Y".  
         */
        for (int i = 0; i < man.count(); i++) {
            par = man.getParameterAt(i);
            if (par.getValue().equals(data.getEmail())) {
                if (receivableReportToId.equals(par.getTypeId()))
                    addRec = false;
                else if (releasedReportToId.equals(par.getTypeId()))
                    addRel = false;
            }                            
        }            
        
        if ("Y".equals(data.getForReceived()) && addRec) {
            par = new OrganizationParameterDO();
            par.setOrganizationId(data.getOrganizationId());        
            par.setTypeId(receivableReportToId);
            par.setValue(data.getEmail());            
            list.add(par);
        }
        
        if ("Y".equals(data.getForReleased()) && addRel) {
            par = new OrganizationParameterDO();
            par.setOrganizationId(data.getOrganizationId());        
            par.setTypeId(releasedReportToId);
            par.setValue(data.getEmail());            
            list.add(par);
        }
        
        return list;
    }
}