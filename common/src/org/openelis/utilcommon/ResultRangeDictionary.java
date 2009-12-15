package org.openelis.utilcommon;

import java.util.HashMap;

import org.openelis.exception.ParseException;
import org.openelis.utilcommon.ResultValidator.Type;

public class ResultRangeDictionary implements Result {
    private static final long serialVersionUID = 1L;
    
    protected HashMap<Integer, String> keyList;
    protected HashMap<String, Integer> entryList;
    
    public ResultRangeDictionary(){
        keyList = new HashMap<Integer, String>();
        entryList = new HashMap<String, Integer>();
    }
    
    public void validate(Integer id) throws ParseException {
        if(id == null)
            return;
        
        if(keyList.get(id) == null)
            throw new ParseException("illegalDictEntryException");
    }
    
    public void validate(String entry) throws ParseException {
        if(entry == null)
            return;
        
        if(entryList.get(entry) == null)
            throw new ParseException("illegalDictEntryException");
    }
    
    public void addEntry(Integer id, String entry){
        keyList.put(id, entry);
        entryList.put(entry, id);
    }

    public Type getType() {
        return Type.DICTIONARY;
    }
    
}
