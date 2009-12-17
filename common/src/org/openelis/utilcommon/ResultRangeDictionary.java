package org.openelis.utilcommon;

import java.util.HashMap;

import org.openelis.exception.ParseException;
import org.openelis.utilcommon.ResultValidator.Type;

public class ResultRangeDictionary implements ResultType {
    private static final long serialVersionUID = 1L;
    
    protected HashMap<String, Integer> entryList;
    protected HashMap<String, Integer> resultIdList;
    protected Integer id;
    
    public ResultRangeDictionary(){
        entryList = new HashMap<String, Integer>();
        resultIdList = new HashMap<String, Integer>();
    }
    
    public void contains(String entry) throws ParseException {
        if(entry == null)
            return;
        
        if(entryList.get(entry) == null)
            throw new ParseException("illegalDictEntryException");
        else
            id = resultIdList.get(entry);
    }
    
    public void addEntry(Integer id, String entry, Integer testResultId){
        entryList.put(entry, id);
        resultIdList.put(entry, testResultId);
    }
    
    public void addEntry(Integer id, String entry){
        entryList.put(entry, id);
    }

    public Type getType() {
        return Type.DICTIONARY;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
}
