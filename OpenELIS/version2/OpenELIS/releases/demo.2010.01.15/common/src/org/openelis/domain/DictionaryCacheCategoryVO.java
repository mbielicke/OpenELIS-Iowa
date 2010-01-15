package org.openelis.domain;

import java.util.ArrayList;

import org.openelis.gwt.common.RPC;

public class DictionaryCacheCategoryVO implements RPC {
    private static final long serialVersionUID = 1L;
    
    protected String systemName;
    protected ArrayList<DictionaryDO> dictionaryList;
    
    public DictionaryCacheCategoryVO(){
        
    }

    public String getSystemName() {
        return systemName;
    }

    public void setSystemName(String systemName) {
        this.systemName = systemName;
    }

    public ArrayList<DictionaryDO> getDictionaryList() {
        return dictionaryList;
    }

    public void setDictionaryList(ArrayList<DictionaryDO> dictionaryList) {
        this.dictionaryList = dictionaryList;
    }

}
