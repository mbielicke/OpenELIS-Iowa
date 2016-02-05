package org.openelis.domain;

import org.openelis.ui.common.DataBaseUtil;

public class AuxFieldValueViewDO extends AuxFieldValueDO {

    private static final long serialVersionUID = 1L;

    protected String          dictionary, dictionaryIsActive;

    public AuxFieldValueViewDO() {
    }

    public AuxFieldValueViewDO(Integer id, Integer auxFieldId, Integer typeId, String value,
                               String dictionary, String dictionaryIsActive) {
        super(id, auxFieldId, typeId, value);
        setDictionary(dictionary);
        setDictionaryIsActive(dictionaryIsActive);
    }

    public String getDictionary() {
        return dictionary;
    }

    public void setDictionary(String dictionary) {
        this.dictionary = DataBaseUtil.trim(dictionary);
    }

    public String getDictionaryIsActive() {
        return dictionaryIsActive;
    }

    public void setDictionaryIsActive(String dictionaryIsActive) {
        this.dictionaryIsActive = DataBaseUtil.trim(dictionaryIsActive);
    }
}