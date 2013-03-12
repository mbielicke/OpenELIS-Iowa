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

import org.openelis.constants.Messages;
import org.openelis.domain.Constants;
import org.openelis.domain.OrganizationParameterDO;
import org.openelis.domain.OrganizationViewDO;
import org.openelis.ui.common.DataBaseUtil;
import org.openelis.ui.common.EntityLockedException;
import org.openelis.ui.common.ModulePermission;
import org.openelis.ui.common.NotFoundException;
import org.openelis.ui.common.PermissionException;
import org.openelis.ui.common.ValidationErrorsList;
import org.openelis.ui.widget.WindowInt;
import org.openelis.gwt.event.ActionEvent;
import org.openelis.gwt.event.ActionHandler;
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
import org.openelis.web.cache.UserCache;
import org.openelis.web.modules.notificationPreference.client.AddEditEmailScreen.Action;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.Window;

public class NotificationPreferenceScreen extends Screen {

    private ArrayList<OrganizationViewDO>                        organizationList;
    private ArrayList<Integer>                                   idList;
    private ModulePermission                                     userPermission;
    private AppButton                                            addButton, editButton, removeButton;
    private TableWidget                                          table;
    private AddEditEmailScreen                                   addEditEmailScreen;
    private String                                               clause;
    
    /**
     * No-Arg constructor
     */
    public NotificationPreferenceScreen(WindowInt win) throws Exception {
        super((ScreenDefInt)GWT.create(NotificationPreferenceDef.class));
        
        setWindow(win);
        
        userPermission = UserCache.getPermission().getModule("w_notify");
        if (userPermission == null)
            throw new PermissionException(Messages.get().screenPermException("Notification Preference Screen"));

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
                   data.setOrganizationId(organizationList.get(0).getId());
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
        ArrayList<TableDataRow> model;
        Dropdown<Integer> org;

        model = new ArrayList<TableDataRow>();
        model.add(new TableDataRow(null, ""));
        
        for (OrganizationViewDO data : organizationList)
            model.add(new TableDataRow(data.getId(), data.getName()));                

        org = ((Dropdown<Integer>)table.getColumns().get(0).getColumnWidget());
        org.setModel(model);
    }

    private ArrayList<TableDataRow> getTableModel() {
        int j;
        boolean isRec, isRel;
        Integer ind;
        String email;
        OrganizationParameterDO data;
        ArrayList<TableDataRow> model;
        TableDataRow row;
        HashMap<String, Integer> emIndMap;
        ArrayList<OrganizationParameterDO> params, list;

        model = new ArrayList<TableDataRow>();
        if (organizationList == null)
            return model;

        try {
            j = -1;
            
            for (OrganizationViewDO org : organizationList) {
                try {
                    params = NotificationPreferenceService.get().fetchParametersByOrganizationId(org.getId());
                } catch (NotFoundException e) {
                    continue;
                }
                emIndMap = new HashMap<String, Integer>();
                for (int i = 0; i < params.size(); i++) {                                      
                    data = params.get(i);
                    isRel = Constants.dictionary().RELEASED_REPORTTO_EMAIL.equals(data.getTypeId());
                    isRec = Constants.dictionary().RECEIVABLE_REPORTTO_EMAIL.equals(data.getTypeId());
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
                        row.cells.get(0).setValue(org.getId());                    
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
                Window.alert(Messages.get().noPermToAddEmailException());               
                return;
            }
        }
        window.setBusy(Messages.get().fetching());
        try {
           organizationList = NotificationPreferenceService.get().fetchByIds(idList);
        } catch (Exception e) {
            e.printStackTrace();
            Window.alert(e.getMessage());
        }
        window.clearStatus();
    } 

    private ArrayList<Integer> getIdListFromClause() {
        String full[], part[];
        ArrayList<Integer> ids;

        /*
         * the structure of the clause should be "organizationId:id1,id2,..."
         */
        full = clause.split(":");       
        part = full[1].split(",");
        if (part.length == 0)
            return null;
        
        ids = new ArrayList<Integer>();
        for (int i = 0; i < part.length; i++ )
            ids.add(Integer.parseInt(DataBaseUtil.trim(part[i])));        

        return ids;
    }   
    
    private void showAddEdit(AddEditEmailVO data) {
        int h,l;
        ScreenWindow modal;
        
        if (addEditEmailScreen == null) {
            try {
                addEditEmailScreen = new AddEditEmailScreen(organizationList);
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
        
        h = this.getAbsoluteTop();
        l = this.getAbsoluteLeft();
        
        modal = new ScreenWindow(ScreenWindow.Mode.DIALOG, false);
        modal.setContent(addEditEmailScreen, l+60, h+10);
        addEditEmailScreen.setState(state);
        addEditEmailScreen.clearFields(data);
    }
    
    private void add(AddEditEmailVO data) {
        Integer orgId;
        String msg, prevMsg;
        ArrayList<OrganizationParameterDO> params;

        window.setBusy(Messages.get().adding());

        for (int i = 0; i < organizationList.size(); i++ ) {
            try {
                orgId = organizationList.get(i).getId();
                if (orgId.equals(data.getOrganizationId())) {
                    try {
                        params = NotificationPreferenceService.get().fetchParametersByOrganizationId(orgId);
                    } catch (NotFoundException e) {
                        params = new ArrayList<OrganizationParameterDO>();
                    }
                    /*
                     * a DO is created for each type to which the email entered 
                     * on AddEditEmailScreen is specified to belong
                     */
                    createParameters(params, data);

                    NotificationPreferenceService.get().updateForNotify(params);
                    break;
                }
            } catch (EntityLockedException e) {
                Window.alert(Messages.get().recordNotAvailableLockException());
            } catch (ValidationErrorsList e) {
                prevMsg = null;
                for (int j = 0; j < e.size(); j++) {
                    msg = e.getErrorList().get(j).getMessage();
                    /*
                     * If the user tried to add the same email as both received 
                     * and released, and say the email address was invalid, then
                     * the list will contain two errors with exactly the same
                     * message. So we check the message before showing the alert
                     * so that the user doesn't see messages repeated.
                     */
                    if (!msg.equals(prevMsg))
                        Window.alert(msg);
                    prevMsg = msg;
                }                
            } catch (Exception e) {
                Window.alert(e.getMessage());
                e.printStackTrace();
            }
        }
        
        setState(State.DEFAULT);
        DataChangeEvent.fire(this);
        window.setDone(Messages.get().addingComplete());
    }
    
    private void update(AddEditEmailVO data) {
        Integer orgId;
        String msg, prevMsg;
        ArrayList<OrganizationParameterDO> params, list;
        OrganizationParameterDO par;

        window.setBusy(Messages.get().updating());
        
        list = (ArrayList<OrganizationParameterDO>)table.getSelection().data;
        
        for (int i = 0; i < organizationList.size(); i++ ) {
            try {
                orgId = organizationList.get(i).getId();
                if (orgId.equals(data.getOrganizationId())) {
                    try {
                        params = NotificationPreferenceService.get().fetchParametersByOrganizationId(orgId);
                    } catch (NotFoundException e) {
                        params = new ArrayList<OrganizationParameterDO>();
                    }
                    /*
                     * all the fetched DOs corresponding to the ones linked to this
                     * row are tried to be found and their values are set to the
                     * email specified on AddEditEmailScreen
                     */
                    for (int j = 0; j < params.size(); j++ ) {
                        par = params.get(j);
                        for (int k = 0; k < list.size(); k++ ) {
                            if (par.getId().equals(list.get(k).getId())) {
                                par.setValue(data.getEmail());
                                break;
                            }
                        }
                    }
                    
                    NotificationPreferenceService.get().updateForNotify(params);
                    break;
                }
            } catch (EntityLockedException e) {
                Window.alert(Messages.get().recordNotAvailableLockException());
            } catch (ValidationErrorsList e) {
                prevMsg = null;
                for (int j = 0; j < e.size(); j++) {
                    msg = e.getErrorList().get(j).getMessage();
                    /*
                     * If the user tried to add the same email as both received 
                     * and released, and say the email address was invalid, then
                     * the list will contain two errors with exactly the same
                     * message. So we check the message before showing the alert
                     * so that the user doesn't see messages repeated.
                     */
                    if (!msg.equals(prevMsg))
                        Window.alert(msg);
                    prevMsg = msg;
                }
            } catch (Exception e) {
                Window.alert(e.getMessage());
                e.printStackTrace();
            }
        }

        setState(State.DEFAULT);
        DataChangeEvent.fire(this);
        window.setDone(Messages.get().updatingComplete());
    }
    
    private void delete() {
        Integer orgId;
        ArrayList<OrganizationParameterDO> params, list;
        OrganizationParameterDO data, par;
        TableDataRow row;

        window.setBusy(Messages.get().deleting());
        row = table.getSelection();
        list = (ArrayList<OrganizationParameterDO>)row.data;
        
        for (int i = 0; i < organizationList.size(); i++ ) {
            try {
                orgId = organizationList.get(i).getId();
                if (orgId.equals(list.get(0).getOrganizationId())) {
                    params = NotificationPreferenceService.get().fetchParametersByOrganizationId(orgId);
                    /*
                     * all the fetched DOs that have the same ids as the ones linked
                     * to the row being deleted are searched for and removed if 
                     * found
                     */
                    for (int j = 0; j < list.size(); j++ ) {
                        data = list.get(j);
                        for (int k = 0; k < params.size(); k++ ) {
                            par = params.get(k);
                            if (par.getId().equals(data.getId())) {
                                /*
                                 * the criteria used by the code in the back-end
                                 * to remove existing DOs is the value being null 
                                 */
                                par.setValue(null);
                                break;
                            }
                        }
                    }
                    NotificationPreferenceService.get().updateForNotify(params);
                    break;
                }

            } catch (EntityLockedException e) {
                Window.alert(Messages.get().recordNotAvailableLockException());
            } catch (Exception e) {
                Window.alert(e.getMessage());
                e.printStackTrace();
            }
        }

        setState(State.DEFAULT);
        DataChangeEvent.fire(this);
        window.setDone(Messages.get().deleteComplete());
    }
        
    private void createParameters(ArrayList<OrganizationParameterDO> params, AddEditEmailVO data) {        
        boolean addRec, addRel;        
        OrganizationParameterDO par;
        
        addRec = true;
        addRel = true;        
        
        /*
         * If an email address already exists for a given type e.g. 
         * "Released Report To", a new DO for that type and that email isn't
         * created and added. Thus even if "data" has its field for that type e.g.
         * "forReleased" set to "Y", the request is ignored. If however the email
         * can't be found, a DO is created for each type that has 
         * the field corresponding to it in "data" set to "Y".  
         */
        for (int i = 0; i < params.size(); i++) {
            par = params.get(i);
            if (par.getValue().equals(data.getEmail())) {
                if (Constants.dictionary().RECEIVABLE_REPORTTO_EMAIL.equals(par.getTypeId()))
                    addRec = false;
                else if (Constants.dictionary().RELEASED_REPORTTO_EMAIL.equals(par.getTypeId()))
                    addRel = false;
            }                            
        }            
        
        if ("Y".equals(data.getForReceived()) && addRec) {
            par = new OrganizationParameterDO();
            par.setOrganizationId(data.getOrganizationId());        
            par.setTypeId(Constants.dictionary().RECEIVABLE_REPORTTO_EMAIL);
            par.setValue(data.getEmail());            
            params.add(par);
        }
        
        if ("Y".equals(data.getForReleased()) && addRel) {
            par = new OrganizationParameterDO();
            par.setOrganizationId(data.getOrganizationId());        
            par.setTypeId(Constants.dictionary().RELEASED_REPORTTO_EMAIL);
            par.setValue(data.getEmail());            
            params.add(par);
        }
    }
}