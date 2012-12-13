package org.openelis.manager;

import org.openelis.domain.AuxFieldGroupDO;
import org.openelis.gwt.common.NotFoundException;
import org.openelis.gwt.common.RPC;
import org.openelis.gwt.common.ValidationErrorsList;

public class AuxFieldGroupManager implements RPC {

    private static final long                             serialVersionUID = 1L;

    protected AuxFieldGroupDO                             group;
    protected AuxFieldManager                       fields;

    protected transient static AuxFieldGroupManagerProxy proxy;

    /**
     * Creates a new instance of this object.
     */
    public static AuxFieldGroupManager getInstance() {
        AuxFieldGroupManager afgm;

        afgm = new AuxFieldGroupManager();
        afgm.group = new AuxFieldGroupDO();

        return afgm;
    }

    /**
     * Creates a new instance of this object with the specified auxiliary field
     * id. Use this function to load an instance of this object from database.
     */
    public static AuxFieldGroupManager fetchById(Integer id) throws Exception {
        return proxy().fetchById(id);
    }
    
    public static AuxFieldGroupManager fetchByIdWithFields(Integer id) throws Exception {
        return proxy().fetchByIdWithFields(id);
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
                    fields = AuxFieldManager.fetchByGroupId(group.getId());
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
        ValidationErrorsList list;

        list = new ValidationErrorsList();
        proxy().validate(this, list);

        if (list.size() > 0)
            throw list;
    }

    public void validate(ValidationErrorsList list) throws Exception {
        proxy().validate(this, list);
        
        if (list.size() > 0)
            throw list;
    }

    private static AuxFieldGroupManagerProxy proxy() {
        if (proxy == null)
            proxy = new AuxFieldGroupManagerProxy();

        return proxy;
    }
}
