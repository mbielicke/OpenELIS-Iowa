package org.openelis.domain;

import org.openelis.gwt.common.DataBaseUtil;

public class AuxFieldValueViewDO extends AuxFieldValueDO {

    private static final long serialVersionUID = 1L;

    protected String dictionary;
    
    public AuxFieldValueViewDO(){
        
    }
    
    public AuxFieldValueViewDO(Integer id, Integer auxFieldId, Integer typeId, String value, String dictionary) {
        super(id, auxFieldId, typeId, value);
        setDictionary(dictionary);
    }

    public String getDictionary() {
        return dictionary;
    }

    public void setDictionary(String dictionary) {
        this.dictionary = DataBaseUtil.trim(dictionary);
    }
}
