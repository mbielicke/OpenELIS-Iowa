package org.openelis.manager;

import java.io.Serializable;

import org.openelis.domain.AuxFieldGroupDO;
import org.openelis.domain.AuxFieldValueViewDO;
import org.openelis.domain.AuxFieldViewDO;
import org.openelis.ui.common.NotFoundException;
import org.openelis.ui.common.ValidationErrorsList;
import org.openelis.utilcommon.ResultFormatter;

public class AuxFieldGroupManager implements Serializable {

    private static final long                            serialVersionUID = 1L;

    protected AuxFieldGroupDO                            group;
    protected AuxFieldManager                            fields;

    protected transient ResultFormatter                  formatter;

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

    /**
     * Returns the formatter used to format and validate this aux field group's 
     * values
     */
    public ResultFormatter getFormatter() throws Exception {
        AuxFieldManager afm;
        AuxFieldValueManager afvm;
        AuxFieldViewDO af;
        AuxFieldValueViewDO afv;

        if (formatter != null)
            return formatter;
        
        afm = getFields();
        formatter = new ResultFormatter();
        for (int i = 0; i < afm.count(); i++ ) {
            af = afm.getAuxFieldAt(i);
            afvm = afm.getValuesAt(i);
            for (int j = 0; j < afvm.count(); j++ ) {
                afv = afvm.getValues().get(j);

                formatter.add(afv.getId(), af.getId(), null, afv.getTypeId(),
                              null, null, afv.getValue(), afv.getDictionary());
            }
        }
        return formatter;
    }

    private static AuxFieldGroupManagerProxy proxy() {
        if (proxy == null)
            proxy = new AuxFieldGroupManagerProxy();

        return proxy;
    }
}