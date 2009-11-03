package org.openelis.manager;

import java.util.ArrayList;

import org.openelis.domain.AuxFieldValueViewDO;
import org.openelis.domain.AuxFieldViewDO;
import org.openelis.gwt.common.NotFoundException;
import org.openelis.gwt.common.RPC;
import org.openelis.gwt.common.ValidationErrorsList;

public class AuxFieldManager implements RPC {

    private static final long                             serialVersionUID = 1L;

    protected Integer                                     auxFieldGroupId;
    protected ArrayList<AuxFieldListItem>                 items, deletedList;

    protected transient static AuxFieldManagerProxy proxy;

    /**
     * Creates a new instance of this object.
     */
    public static AuxFieldManager getInstance() {
        AuxFieldManager afm;

        afm = new AuxFieldManager();
        afm.items = new ArrayList<AuxFieldListItem>();

        return afm;
    }

    public static AuxFieldManager fetchById(Integer id) throws Exception {
        return proxy().fetchById(id);
    }
    
    /**
     * Creates a new instance of this object with the specified auxiliary field
     * id. Use this function to load an instance of this object from database.
     */
    public static AuxFieldManager fetchByGroupId(Integer auxFieldGroupId) throws Exception {
        return proxy().fetchByGroupId(auxFieldGroupId);
    }
    
    public static AuxFieldManager fetchByGroupIdWithValues(Integer auxFieldGroupId) throws Exception {
        return proxy().fetchByGroupIdWithValues(auxFieldGroupId);
    }

    public int count() {
        if (items == null)
            return 0;

        return items.size();
    }

    // getters/setters of child objects
    public Integer getAuxFieldGroupId() {
        return auxFieldGroupId;
    }

    public void setAuxFieldGroupId(Integer auxFieldGroupId) {
        this.auxFieldGroupId = auxFieldGroupId;
    }

    public AuxFieldViewDO getAuxFieldAt(int i) {
        return items.get(i).field;

    }

    public void setAuxFieldAt(AuxFieldViewDO auxField, int i) {
        items.get(i).field = auxField;
    }

    public void addAuxField(AuxFieldViewDO auxField) {
        AuxFieldListItem item = new AuxFieldListItem();
        item.field = auxField;

        items.add(item);
    }
    
    public void addAuxFieldAndValues(AuxFieldViewDO auxField, ArrayList<AuxFieldValueViewDO> values) {
        AuxFieldListItem item = new AuxFieldListItem();
        item.field = auxField;
        item.values = AuxFieldValueManager.getInstance();
        item.values.setAuxiliaryFieldId(auxField.getId());
        
        for(int i=0; i<values.size(); i++)
            item.values.addAuxFieldValue(values.get(i));

        items.add(item);
    }

    public void removeAuxFieldAt(int i) {
        if (items == null || i >= items.size())
            return;

        AuxFieldListItem tmp = items.remove(i);

        if (deletedList == null)
            deletedList = new ArrayList<AuxFieldListItem>();

        if (tmp.field.getId() != null)
            deletedList.add(tmp);
    }

    //
    // other managers
    //
    public AuxFieldValueManager getValuesAt(int i) throws Exception {
        AuxFieldListItem item = items.get(i);
        if (item.values == null) {
            if (item.field != null && item.field.getId() != null) {
                try {
                    item.values = AuxFieldValueManager.fetchByAuxFieldId(item.field.getId());

                } catch (NotFoundException e) {
                    // ignore
                } catch (Exception e) {
                    throw e;
                }
            }
        }

        if (item.values == null)
            item.values = AuxFieldValueManager.getInstance();

        return item.values;
    }

    // service methods
    public AuxFieldManager add() throws Exception {
        return proxy().add(this);
    }

    public AuxFieldManager update() throws Exception {
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

    private static AuxFieldManagerProxy proxy() {
        if (proxy == null)
            proxy = new AuxFieldManagerProxy();

        return proxy;
    }

    int deleteCount() {
        if (deletedList == null)
            return 0;

        return deletedList.size();
    }

    AuxFieldListItem getDeletedAt(int i) {
        return deletedList.get(i);
    }

    static class AuxFieldListItem implements RPC {
        private static final long serialVersionUID = 1L;

        AuxFieldViewDO            field;
        AuxFieldValueManager      values;
    }
}
