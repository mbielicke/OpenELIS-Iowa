package org.openelis.manager;

import java.util.ArrayList;

import org.openelis.domain.AuxDataDO;
import org.openelis.gwt.common.NotFoundException;
import org.openelis.gwt.common.RPC;
import org.openelis.gwt.common.ValidationErrorsList;

public class AuxDataManager implements RPC {

    private static final long                      serialVersionUID = 1L;

    protected ArrayList<AuxDataListItem>           items, deletedList;
    protected Integer                              referenceId, referenceTableId;

    protected transient static AuxDataManagerProxy proxy;

    public static AuxDataManager getInstance() {
        AuxDataManager adm;

        adm = new AuxDataManager();
        adm.items = new ArrayList<AuxDataListItem>();

        return adm;
    }

    public static AuxDataManager fetchById(Integer referenceId, Integer referenceTableId)
                                                                                         throws Exception {
        return proxy().fetchById(referenceId, referenceTableId);
    }

    public static AuxDataManager fetchByIdWithFields(Integer referenceId, Integer referenceTableId)
                                                                                               throws Exception {
        return proxy().fetchWithFields(referenceId, referenceTableId);
    }

    public Integer getReferenceId() {
        return referenceId;
    }

    public void setReferenceId(Integer referenceId) {
        this.referenceId = referenceId;
    }

    public Integer getReferenceTableId() {
        return referenceTableId;
    }

    public void setReferenceTableId(Integer referenceTableId) {
        this.referenceTableId = referenceTableId;
    }

    //
    // other managers
    //
    public AuxFieldManager getFieldsAt(int i) throws Exception {
        AuxDataListItem item = items.get(i);
        if (item.fields == null) {
            if (item.data != null && item.data.getAuxFieldId() != null) {
                try {
                    item.fields = AuxFieldManager.fetchById(item.data.getAuxFieldId());

                } catch (NotFoundException e) {
                    // ignore
                } catch (Exception e) {
                    throw e;
                }
            }
        }

        if (item.fields == null)
            item.fields = AuxFieldManager.getInstance();

        return item.fields;
    }

    // service methods
    public AuxDataManager add() throws Exception {
        return proxy().add(this);
    }

    public AuxDataManager update() throws Exception {
        return proxy().update(this);
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

    private static AuxDataManagerProxy proxy() {
        if (proxy == null)
            proxy = new AuxDataManagerProxy();

        return proxy;
    }

    static class AuxDataListItem implements RPC {
        private static final long serialVersionUID = 1L;

        AuxDataDO                 data;
        AuxFieldManager           fields;
    }
}
