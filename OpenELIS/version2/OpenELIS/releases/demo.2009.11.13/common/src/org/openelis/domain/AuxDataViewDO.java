package org.openelis.domain;

public class AuxDataViewDO extends AuxDataDO {

    private static final long serialVersionUID = 1L;
    
    protected String dictionary;
    
    public AuxDataViewDO(){
        
    }
    
    public AuxDataViewDO(Integer id, Integer sortOrder, Integer auxFieldId, Integer referenceId,
                     Integer referenceTableId, String isReportable, Integer typeId, String value, String dictionary){
        super(id, sortOrder, auxFieldId, referenceId, referenceTableId, isReportable, typeId, value);
        setDictionary(dictionary);
    }

    public String getDictionary() {
        return dictionary;
    }

    public void setDictionary(String dictionary) {
        this.dictionary = dictionary;
    }
}
