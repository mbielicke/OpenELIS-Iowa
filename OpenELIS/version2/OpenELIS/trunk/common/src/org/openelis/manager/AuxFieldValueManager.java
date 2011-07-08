package org.openelis.manager;

import java.util.ArrayList;

import org.openelis.domain.AuxFieldValueViewDO;
import org.openelis.gwt.common.RPC;
import org.openelis.gwt.common.ValidationErrorsList;

public class AuxFieldValueManager implements RPC {

    private static final long                                  serialVersionUID = 1L;

    protected Integer                                          auxiliaryFieldId;
    protected ArrayList<AuxFieldValueViewDO>                   values;
    protected ArrayList<AuxFieldValueViewDO>                   deleted;

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
     * Creates a new instance of this object with the specified auxiliary field id.
     * Use this function to load an instance of this object from database.
     */
    public static AuxFieldValueManager fetchByFieldId(Integer fieldId) throws Exception {
        return proxy().fetchByFieldId(fieldId);
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
        AuxFieldValueViewDO tmp;
        if(values == null || i >= values.size())
            return;
        
        tmp = values.remove(i);                
        
        if(deleted == null)
            deleted = new ArrayList<AuxFieldValueViewDO>();
        
        if(tmp.getId() != null)             
            deleted.add(tmp);
        
    }
    
    public AuxFieldValueViewDO getDefaultValue(){
        AuxFieldValueViewDO item,tmp;
        Integer defaultTypeId;
        
        try{
            defaultTypeId = proxy().getIdFromSystemName("aux_default");
        }catch(Exception e){
            e.printStackTrace(); 
            return null;
        }
        
        item = null;
        for(int i=0; i<count(); i++){
            tmp = values.get(i);
            if(defaultTypeId.equals(tmp.getTypeId())){
                item = tmp;
                break;
            }
        }
        
        return item;
    }
    
    // service methods
    public AuxFieldValueManager add() throws Exception {
        return proxy().add(this);
    }

    public AuxFieldValueManager update() throws Exception {
        return proxy().update(this);
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
    }

    private static AuxFieldValueManagerProxy proxy() {
        if (proxy == null)
            proxy = new AuxFieldValueManagerProxy();

        return proxy;
    }
    
    int deleteCount() {
        if (deleted == null)
            return 0;

        return deleted.size();
    }

    AuxFieldValueViewDO getDeletedAt(int i) {
        return deleted.get(i);
    }    
}
