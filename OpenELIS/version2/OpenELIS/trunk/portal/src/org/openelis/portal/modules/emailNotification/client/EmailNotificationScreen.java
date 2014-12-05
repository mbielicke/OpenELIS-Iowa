package org.openelis.portal.modules.emailNotification.client;

import static org.openelis.ui.screen.State.QUERY;

import java.util.ArrayList;
import java.util.HashMap;

import org.openelis.domain.Constants;
import org.openelis.domain.DictionaryDO;
import org.openelis.domain.OrganizationParameterDO;
import org.openelis.domain.OrganizationViewDO;
import org.openelis.portal.cache.CategoryCache;
import org.openelis.portal.cache.UserCache;
import org.openelis.portal.client.resources.Resources;
import org.openelis.portal.messages.Messages;
import org.openelis.ui.common.DataBaseUtil;
import org.openelis.ui.common.EntityLockedException;
import org.openelis.ui.common.ModulePermission;
import org.openelis.ui.common.NotFoundException;
import org.openelis.ui.common.ValidationErrorsList;
import org.openelis.ui.event.StateChangeEvent;
import org.openelis.ui.screen.Screen;
import org.openelis.ui.screen.ScreenHandler;
import org.openelis.ui.widget.Item;
import org.openelis.ui.widget.table.Row;
import org.openelis.ui.widget.table.event.CellClickedEvent;
import org.openelis.ui.widget.table.event.CellClickedHandler;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.Window;

public class EmailNotificationScreen extends Screen {

    private EmailNotificationUI           ui    = GWT.create(EmailNotificationUIImpl.class);

    private ArrayList<OrganizationViewDO> organizationList;
    private ArrayList<DictionaryDO>       filterList;
    private ArrayList<Integer>            idList;
    private ModulePermission              userPermission;
    private String                        clause;
    private ScheduledCommand              cmd;

    private static char                   delim = ';';

    public EmailNotificationScreen() {
        initWidget(ui.asWidget());

        userPermission = UserCache.getPermission().getModule("w_notify");
        if (userPermission == null) {
            Window.alert(Messages.get().error_screenPerm("Email Notification Screen"));
            return;
        }

        clause = userPermission.getClause();

        initialize();
        setState(QUERY);
    }

    /**
     * Setup state and data change handles for every widget on the screen
     */
    private void initialize() {
        ArrayList<Item<Integer>> model;
        ArrayList<Item<String>> smodel;

        fetchByIdList();
        filterList = CategoryCache.getBySystemName("email_filter");

        cmd = new ScheduledCommand() {
            @Override
            public void execute() {
                ui.getTable().onResize();
            }
        };

        addScreenHandler(ui.getTable(), "table", new ScreenHandler<ArrayList<Row>>() {
            public void onStateChange(StateChangeEvent event) {
                ui.getTable().setEnabled(true);
            }
        });

        ui.getTable().addValueChangeHandler(new ValueChangeHandler<ArrayList<? extends Row>>() {

            @Override
            public void onValueChange(ValueChangeEvent<ArrayList<? extends Row>> event) {
                ui.getTable().clearExceptions(event.getValue().get(0), 1);
                ui.getTable().clearExceptions(event.getValue().get(0), 2);
            }
        });

        ui.getTable().addCellClickedHandler(new CellClickedHandler() {

            @Override
            public void onCellClicked(CellClickedEvent event) {
                if (event.getCol() == 0) {
                    remove(event.getRow());
                    Scheduler.get().scheduleDeferred(cmd);
                }
            }
        });

        ui.getAddButton().addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                Row row;

                row = new Row(7);
                row.setCell(0, Resources.INSTANCE.icon().removeImage());
                row.setCell(1, null);
                row.setCell(2, null);
                row.setCell(3, null);
                row.setCell(4, null);
                row.setCell(5, "N");
                row.setCell(6, "N");
                ui.getTable().addRow(row);
                ui.getTable().selectRowAt(ui.getTable().getRowCount() - 1);
            }
        });

        ui.getCommitButton().addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                commit();
            }
        });

        model = new ArrayList<Item<Integer>>();
        model.add(new Item<Integer>(null, ""));

        for (OrganizationViewDO data : organizationList)
            model.add(new Item<Integer>(data.getId(), data.getName()));

        ui.getOrg().setModel(model);

        smodel = new ArrayList<Item<String>>();
        smodel.add(new Item<String>(null, ""));
        for (DictionaryDO data : filterList) {
            smodel.add(new Item<String>(data.getCode(), data.getEntry()));
        }

        ui.getFilter().setModel(smodel);

        ui.getTable().setModel(getTableModel());

        Scheduler.get().scheduleDeferred(cmd);
    }

    /**
     * Creates a table model from the parameters of each organization in the
     * organization list
     */
    private ArrayList<Row> getTableModel() {
        int i;
        boolean isRec, isRel;
        String email, filter, filterValue, value[];
        OrganizationParameterDO data;
        Row row;
        ArrayList<Row> model;
        ArrayList<OrganizationParameterDO> params, list;

        model = new ArrayList<Row>();
        if (organizationList == null)
            return model;

        try {
            for (OrganizationViewDO org : organizationList) {
                try {
                    params = EmailNotificationService.get()
                                                     .fetchParametersByOrganizationId(org.getId());
                } catch (NotFoundException e) {
                    continue;
                }
                for (i = 0; i < params.size(); i++ ) {
                    data = params.get(i);
                    isRel = Constants.dictionary().RELEASED_REPORTTO_EMAIL.equals(data.getTypeId());
                    isRec = Constants.dictionary().RECEIVABLE_REPORTTO_EMAIL.equals(data.getTypeId());
                    if ( !isRel && !isRec)
                        continue;

                    value = decode(data.getValue());
                    email = value[0];
                    filter = value[1];
                    filterValue = value[2];

                    row = new Row(7);
                    row.setCell(0, Resources.INSTANCE.icon().removeImage());
                    row.setCell(1, org.getId());
                    row.setCell(2, email);
                    row.setCell(3, filter);
                    row.setCell(4, filterValue);
                    if (isRec)
                        row.setCell(5, "Y");
                    else
                        row.setCell(5, "N");
                    if (isRel)
                        row.setCell(6, "Y");
                    else
                        row.setCell(6, "N");
                    list = new ArrayList<OrganizationParameterDO>();
                    list.add(data);
                    row.setData(list);
                    model.add(row);
                }
            }

        } catch (Exception e) {
            Window.alert(e.getMessage());
        }

        return model;
    }

    /**
     * fetch the organization list
     */
    private void fetchByIdList() {
        if (idList == null) {
            idList = getIdListFromClause();
            /*
             * if idList is null then there aren't any organizations specified
             * in this user's system user module for this screen
             */
            if (idList == null) {
                Window.alert(Messages.get().emailNotification_error_noPermToAddEmail());
                return;
            }
        }
        try {
            organizationList = EmailNotificationService.get().fetchByIds(idList);
        } catch (Exception e) {
            Window.alert(e.getMessage());
        }
    }

    /**
     * parse the IDs from the user permission clause
     */
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

    /**
     * Validates the organization parameter data and commits if there are no
     * errors.
     */
    private void commit() {
        boolean exceptions;
        String email, filter, filterValue;
        OrganizationParameterDO param;
        ArrayList<OrganizationParameterDO> params, commitList;
        HashMap<Integer, ArrayList<OrganizationParameterDO>> allParams;

        ui.getTable().clearExceptions();
        exceptions = false;
        allParams = new HashMap<Integer, ArrayList<OrganizationParameterDO>>();
        for (Row row : ui.getTable().getModel()) {

            /*
             * validate that all data is correct before commit
             */
            if (DataBaseUtil.isEmpty(row.getCell(1))) {
                ui.getTable()
                  .addException(row,
                                1,
                                new Exception(Messages.get()
                                                      .emailNotification_error_noOrganization()));
                exceptions = true;
            }
            if (DataBaseUtil.isEmpty(row.getCell(2))) {
                ui.getTable()
                  .addException(row,
                                2,
                                new Exception(Messages.get().emailNotification_error_noEmail()));
                exceptions = true;
            }

            params = row.getData();
            commitList = new ArrayList<OrganizationParameterDO>();
            if (params == null || params.size() < 1) {
                params = new ArrayList<OrganizationParameterDO>();
                param = new OrganizationParameterDO();
                params.add(param);
            }
            param = null;
            for (OrganizationParameterDO p : params) {
                p.setOrganizationId((Integer)row.getCell(1));
                email = encode((String)row.getCell(2));
                filter = (String)row.getCell(3);
                filterValue = (String)row.getCell(4);

                /*
                 * set all of the correct data in the organization parameter
                 * objects
                 */
                if (DataBaseUtil.isEmpty(filter) || DataBaseUtil.isEmpty(filterValue))
                    p.setValue(email);
                else
                    p.setValue(email + delim + filter + filterValue);
                if (p.getTypeId() == null)
                    p.setTypeId(Constants.dictionary().RECEIVABLE_REPORTTO_EMAIL);
                if ("Y".equals(row.getCell(5))) {
                    if ("Y".equals(row.getCell(6))) {
                        param = new OrganizationParameterDO();
                        param.setOrganizationId(p.getOrganizationId());
                        param.setValue(p.getValue());
                        if (Constants.dictionary().RECEIVABLE_REPORTTO_EMAIL.equals(p.getTypeId()))
                            param.setTypeId(Constants.dictionary().RELEASED_REPORTTO_EMAIL);
                        else
                            param.setTypeId(Constants.dictionary().RECEIVABLE_REPORTTO_EMAIL);
                    } else {
                        p.setTypeId(Constants.dictionary().RECEIVABLE_REPORTTO_EMAIL);
                    }
                } else {
                    if ("Y".equals(row.getCell(6))) {
                        p.setTypeId(Constants.dictionary().RELEASED_REPORTTO_EMAIL);
                    } else if (p.getId() == null) {
                        continue;
                    } else {
                        p.setValue(null);
                    }
                }
                commitList.add(p);
            }
            if (param != null)
                commitList.add(param);
            if (commitList.size() > 0) {
                if (allParams.get(commitList.get(0).getOrganizationId()) == null) {
                    allParams.put(commitList.get(0).getOrganizationId(), commitList);
                } else {
                    allParams.get(commitList.get(0).getOrganizationId()).addAll(commitList);
                }
            }
        }
        if (exceptions)
            return;

        /*
         * if there are no errors, commit the data
         */
        for (Integer key : allParams.keySet()) {
            update(allParams.get(key));
        }
    }

    /**
     * commits updated email notification data
     */
    private void update(ArrayList<OrganizationParameterDO> params) {
        String msg, prevMsg;

        window.setBusy(Messages.get().msg_updating());

        try {
            EmailNotificationService.get().updateForNotify(params);
        } catch (EntityLockedException e) {
            Window.alert(Messages.get().exc_recordNotAvailableLock());
            return;
        } catch (ValidationErrorsList e) {
            prevMsg = null;
            for (int j = 0; j < e.size(); j++ ) {
                msg = e.getErrorList().get(j).getMessage();
                /*
                 * If the user tried to add the same email as both received and
                 * released, and say the email address was invalid, then the
                 * list will contain two errors with exactly the same message.
                 * So we check the message before showing the alert so that the
                 * user doesn't see messages repeated.
                 */
                if ( !msg.equals(prevMsg))
                    Window.alert(msg);
                prevMsg = msg;
            }
            return;
        } catch (Exception e) {
            Window.alert(e.getMessage());
            return;
        } finally {
            window.clearStatus();
        }

        ui.getTable().setModel(getTableModel());
    }

    /**
     * remove an entry from the database if it exists there, or from the table
     * if it was never committed to the database
     */
    private void remove(int r) {
        Integer orgId;
        ArrayList<OrganizationParameterDO> params, list;
        OrganizationParameterDO data, par;
        Row row;

        window.setBusy(Messages.get().msg_deleting());
        row = ui.getTable().getRowAt(r);
        list = (ArrayList<OrganizationParameterDO>)row.getData();
        if (list == null || list.size() < 1) {
            ui.getTable().removeRowAt(r);
            window.clearStatus();
            return;
        }

        for (int i = 0; i < organizationList.size(); i++ ) {
            try {
                orgId = row.getCell(1);
                params = EmailNotificationService.get().fetchParametersByOrganizationId(orgId);
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
                             * the criteria used by the code in the back-end to
                             * remove existing DOs is the value being null
                             */
                            par.setValue(null);
                            break;
                        }
                    }
                }
                EmailNotificationService.get().updateForNotify(params);
                break;

            } catch (EntityLockedException e) {
                Window.alert(Messages.get().exc_recordNotAvailableLock());
            } catch (Exception e) {
                Window.alert(e.getMessage());
            }
        }

        ui.getTable().removeRowAt(r);
        window.clearStatus();
    }

    /**
     * escapes special characters that are used to separate the email string
     * from the filter data
     */
    private String encode(String raw) {
        int i;
        char character, str[];
        StringBuffer sb;

        if (raw == null)
            return raw;

        sb = new StringBuffer();
        str = raw.toCharArray();
        for (i = 0; i < raw.length(); i++ ) {
            character = str[i];
            if (character == '\\') {
                sb.append("\\\\");
            } else if (character == delim) {
                sb.append("\\;");
            } else {
                sb.append(character);
            }
        }
        return sb.toString();
    }

    /**
     * separates the email string from the filter data using the delimiter
     * character
     */
    private String[] decode(String encoded) throws Exception {
        int i;
        char character, str[];
        StringBuffer sb;
        String[] decoded;

        decoded = new String[3];
        if (encoded == null)
            return decoded;

        sb = new StringBuffer();

        str = encoded.toCharArray();
        for (i = 0; i < encoded.length(); i++ ) {
            character = str[i];
            if (character == '\\') {
                try {
                    character = str[ ++i];
                } catch (IndexOutOfBoundsException e) {
                    throw new Exception("invalid escape character");
                }
                sb.append(character);
            } else if (character == delim) {
                break;
            } else {
                sb.append(character);
            }
        }
        decoded[0] = sb.toString();
        if (i == encoded.length())
            return decoded;
        try {
            decoded[1] = encoded.substring(i + 1, i + 3);
            decoded[2] = encoded.substring(i + 3);
        } catch (IndexOutOfBoundsException e) {
            throw new Exception("Invalid filter string");
        }
        return decoded;
    }
}