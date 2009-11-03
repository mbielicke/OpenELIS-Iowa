package org.openelis.manager;

import java.util.ArrayList;

import org.openelis.domain.AuxFieldValueViewDO;
import org.openelis.gwt.common.RPC;
import org.openelis.gwt.common.ValidationErrorsList;

public class AuxFieldValueManager implements RPC {

    private static final long                                  serialVersionUID = 1L;

    protected Integer                                          auxiliaryFieldId;
    protected ArrayList<AuxFieldValueViewDO>                            values;
    protected ArrayList<AuxFieldValueViewDO>                            deletedList;

    protected transient static AuxFieldValueManagerProxy proxy;
    
    /**
     * Creates a new instance of this object.
     */
    public static AuxFieldValueManager getInstance() {
        AuxFieldValueManager afvm;

        afvm = new AuxFieldValueManager();
        afvm.values = new ArrayList<AuxFieldValueViewDO>();

        return afvm;
    }

    /**
     * Creates a new instance of this object with the specified auxiliary field id. Use this function to load an instance of this object from database.
     */
    public static AuxFieldValueManager fetchByAuxFieldId(Integer auxFieldId) throws Exception {
        return proxy().fetchByAuxFieldId(auxFieldId);
    }
    
    public int count() {
        if (values == null)
            return 0;

        return values.size();
    }
    
    // getters/setters of child objects
    public Integer getAuxiliaryFieldId() {
        return auxiliaryFieldId;
    }

    public void setAuxiliaryFieldId(Integer auxiliaryFieldId) {
        this.auxiliaryFieldId = auxiliaryFieldId;
    }

    public AuxFieldValueViewDO getAuxFieldValueAt(int i) {
        return values.get(i);

    }
    
    public ArrayList<AuxFieldValueViewDO> getValues() {
        return values;
    }

    public void setAuxFieldValueAt(AuxFieldValueViewDO auxFieldValue, int i) {
        values.add(i, auxFieldValue);
    }
    
    public void addAuxFieldValue(AuxFieldValueViewDO auxFieldValue){
        values.add(auxFieldValue);
    }
    
    public void removeAuxFieldValueAt(int i){
        if(values == null || i >= values.size())
            return;
        
        AuxFieldValueViewDO tmp = values.remove(i);
        
        if(deletedList == null)
            deletedList = new ArrayList<AuxFieldValueViewDO>();
        
        if(tmp.getId() != null)
            deletedList.add(tmp);
    }
    
    // service methods
    public AuxFieldValueManager add() throws Exception {
        return proxy().add(this);
    }

    public AuxFieldValueManager update() throws Exception {
        return proxy().update(this);
    }
    
    public void validate() throws Exception {
        ValidationErrorsList errorsList = new ValidationErrorsList();
        
        proxy().validate(this, errorsList);
        
        if(errorsList.size() > 0)
            throw errorsList;
    }
    
    public void validate(ValidationErrorsList errorsList) throws Exception {
        proxy().validate(this, errorsList);
    }

    private static AuxFieldValueManagerProxy proxy() {
        if (proxy == null)
            proxy = new AuxFieldValueManagerProxy();

        return proxy;
    }
    
    int deleteCount() {
        if (deletedList == null)
            return 0;

        return deletedList.size();
    }

    AuxFieldValueViewDO getDeletedAt(int i) {
        return deletedList.get(i);
    }    
}
