package org.openelis.domain;

import java.util.ArrayList;

import org.openelis.gwt.common.RPC;

public class DictionaryCacheVO implements RPC {
    private static final long serialVersionUID = 1L;
    
    protected ArrayList<DictionaryCacheCategoryVO> list;
    
    public DictionaryCacheVO(){
        
    }

    public ArrayList<DictionaryCacheCategoryVO> getList() {
        return list;
    }

    public void setList(ArrayList<DictionaryCacheCategoryVO> list) {
        this.list = list;
    }
}
