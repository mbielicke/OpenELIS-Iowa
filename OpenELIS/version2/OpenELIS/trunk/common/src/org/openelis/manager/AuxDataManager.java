package org.openelis.manager;

import java.util.ArrayList;

import org.openelis.domain.AuxDataViewDO;
import org.openelis.domain.AuxFieldValueViewDO;
import org.openelis.domain.AuxFieldViewDO;
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

    public static AuxDataManager fetchById(Integer referenceId, Integer referenceTableId) throws Exception {
        return proxy().fetchById(referenceId, referenceTableId);
    }

    // service methods
    public AuxDataManager add() throws Exception {
        return proxy().add(this);
    }

    public AuxDataManager update() throws Exception {
        return proxy().update(this);
    }

    public AuxDataViewDO getAuxDataAt(int i) {
        return items.get(i).data;
    }

    public void setAuxDataAt(AuxDataViewDO auxData, int i) {
        items.get(i).data = auxData;
    }

    public AuxFieldViewDO getAuxFieldAt(int i) {
        return items.get(i).field;
    }

    public void setAuxFieldAt(AuxFieldViewDO auxField, int i) {
        items.get(i).field = auxField;
    }

    public ArrayList<AuxFieldValueViewDO> getAuxValuesAt(int i) {
        return items.get(i).values;
    }

    public void setAuxValuesAt(ArrayList<AuxFieldValueViewDO> auxValues, int i) {
        items.get(i).values = auxValues;
    }

    public void addAuxData(AuxDataViewDO auxData) {
        AuxDataListItem item;
        
        item = new AuxDataListItem();
        item.data = auxData;

        items.add(item);
    }

    public void addAuxDataFieldAndValues(AuxDataViewDO auxData, AuxFieldViewDO auxField,
                                         ArrayList<AuxFieldValueViewDO> auxValues) {
        AuxDataListItem item;
        
        item = new AuxDataListItem();
        item.data = auxData;
        item.field = auxField;
        item.values = auxValues;

        items.add(item);
    }

    public void removeAuxDataAt(int i) {
        AuxDataListItem tmp;

        if (items == null || i >= items.size())
            return;

        tmp = items.remove(i);

        if (deletedList == null)
            deletedList = new ArrayList<AuxDataListItem>();

        if (tmp.data.getId() != null)
            deletedList.add(tmp);
    }

    public void removeAuxDataGroupAt(int i) {
        int             j;
        AuxDataListItem tmp;
        Integer         groupId;

        if (items == null || i >= items.size())
            return;

        // need to get the group id to remove
        groupId = items.get(i).data.getGroupId();

        if (deletedList == null)
            deletedList = new ArrayList<AuxDataListItem>();

        for (j = count() - 1; j > -1; j--) {
            if (groupId.equals(items.get(j).field.getAuxFieldGroupId())) {
                tmp = items.remove(j);
                if (tmp.data.getId() != null)
                    deletedList.add(tmp);
            }
        }
    }

    //
    // other managers
    //

    public int count() {
        if (items == null)
            return 0;
    
        return items.size();
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

    // these are friendly methods so only managers and proxies can call this
    // method
    Integer getReferenceId() {
        return referenceId;
    }

    void setReferenceId(Integer referenceId) {
        this.referenceId = referenceId;
    }

    Integer getReferenceTableId() {
        return referenceTableId;
    }

    void setReferenceTableId(Integer referenceTableId) {
        this.referenceTableId = referenceTableId;
    }

    int deleteCount() {
        if (deletedList == null)
            return 0;
        return deletedList.size();
    }

    AuxDataViewDO getDeletedAuxDataAt(int i) {
        return deletedList.get(i).data;
    }

    private static AuxDataManagerProxy proxy() {
        if (proxy == null)
            proxy = new AuxDataManagerProxy();

        return proxy;
    }

    static class AuxDataListItem implements RPC {
        private static final long serialVersionUID = 1L;

        AuxDataViewDO                  data;
        AuxFieldViewDO                 field;
        ArrayList<AuxFieldValueViewDO> values;
    }
}
