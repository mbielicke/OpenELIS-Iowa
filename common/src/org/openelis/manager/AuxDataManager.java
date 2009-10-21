package org.openelis.manager;

import java.util.ArrayList;

import org.openelis.domain.AuxDataDO;
import org.openelis.domain.AuxFieldGroupDO;
import org.openelis.domain.AuxFieldViewDO;
import org.openelis.gwt.common.NotFoundException;
import org.openelis.gwt.common.RPC;
import org.openelis.gwt.common.ValidationErrorsList;

public class AuxDataManager implements RPC {

    private static final long                            serialVersionUID = 1L;
/*
    protected ArrayList<AuxDataListItem> items, deletedList;
    
    protected transient static AuxFieldGroupManagerProxy proxy;

    public static AuxFieldGroupManager getInstance() {
        AuxFieldGroupManager afgm;

        afgm = new AuxFieldGroupManager();
        afgm.group = new AuxFieldGroupDO();

        return afgm;
    }

    public static AuxFieldGroupManager fetchById(Integer id) throws Exception {
        return proxy().fetchById(id);
    }

    public static AuxFieldGroupManager fetchWithFields(Integer id) throws Exception {
        return proxy().fetchWithFields(id);
    }

    public AuxFieldGroupDO getGroup() {
        return group;
    }

    public void setGroup(AuxFieldGroupDO group) {
        this.group = group;
    }

    //
    // other managers
    //
    public AuxFieldManager getFields() throws Exception {
        if (fields == null) {
            if (group.getId() != null) {
                try {
                    fields = AuxFieldManager.fetchByAuxFieldGroupId(group.getId());
                } catch (NotFoundException e) {
                    // ignore
                } catch (Exception e) {
                    throw e;
                }
            }
            if (fields == null)
                fields = AuxFieldManager.getInstance();
        }
        return fields;
    }

    // service methods
    public AuxFieldGroupManager add() throws Exception {
        return proxy().add(this);
    }

    public AuxFieldGroupManager update() throws Exception {
        return proxy().update(this);
    }

    public AuxFieldGroupManager fetchForUpdate() throws Exception {
        return proxy().fetchForUpdate(group.getId());
    }

    public AuxFieldGroupManager abortUpdate() throws Exception {
        return proxy().abortUpdate(group.getId());
    }

    public void validate() throws Exception {
        ValidationErrorsList errorsList = new ValidationErrorsList();

        proxy().validate(this, errorsList);

        if (errorsList.size() > 0)
            throw errorsList;
    }

    public void validate(ValidationErrorsList errorsList) throws Exception {
        proxy().validate(this, errorsList);
    }

    private static AuxFieldGroupManagerProxy proxy() {
        if (proxy == null)
            proxy = new AuxFieldGroupManagerProxy();

        return proxy;
    }

    static class AuxDataListItem implements RPC {
        private static final long serialVersionUID = 1L;

        AuxDataDO                 data;
        AuxFieldGroupManager      groups;
    }*/
}
