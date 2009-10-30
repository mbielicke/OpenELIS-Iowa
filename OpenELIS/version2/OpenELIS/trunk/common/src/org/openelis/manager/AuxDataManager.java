package org.openelis.manager;

import java.util.ArrayList;

import org.openelis.domain.AuxDataDO;
import org.openelis.domain.AuxFieldValueDO;
import org.openelis.domain.AuxFieldViewDO;
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

    public static AuxDataManager fetchByIdForUpdate(Integer referenceId, Integer referenceTableId) throws Exception {
        return proxy().fetchByIdForUpdate(referenceId, referenceTableId);
    }
    
    public int count() {
        if (items == null)
            return 0;

        return items.size();
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
    
    public AuxDataDO getAuxDataAt(int i) {
        return items.get(i).data;

    }

    public void setAuxDataAt(AuxDataDO auxData, int i) {
        items.get(i).data = auxData;
    }

    public void addAuxData(AuxDataDO auxData) {
        AuxDataListItem item = new AuxDataListItem();
        item.data = auxData;

        items.add(item);
    }
    
    public void addAuxDataFieldsAndValues(AuxDataDO auxData, AuxFieldViewDO field, ArrayList<AuxFieldValueDO> values) {
        AuxDataListItem item = new AuxDataListItem();
        item.data = auxData;
        
        AuxFieldManager fieldMan = AuxFieldManager.getInstance();
        fieldMan.addAuxFieldAndValues(field, values);
        item.fields = fieldMan;

        items.add(item);
    }

    public void removeAuxDataAt(int i) {
        if (items == null || i >= items.size())
            return;

        AuxDataListItem tmp = items.remove(i);

        if (deletedList == null)
            deletedList = new ArrayList<AuxDataListItem>();

        if (tmp.data.getId() != null)
            deletedList.add(tmp);
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
    
    public void setFieldsAt(AuxFieldManager fieldManager, int i) {
        items.get(i).fields = fieldManager;
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
