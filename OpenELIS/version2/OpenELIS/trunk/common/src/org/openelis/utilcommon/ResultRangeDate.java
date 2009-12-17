package org.openelis.utilcommon;

import java.util.Date;

import org.openelis.exception.ParseException;
import org.openelis.utilcommon.ResultValidator.Type;

public class ResultRangeDate implements ResultType {
    private static final long serialVersionUID = 1L;
    
    protected Integer id;
    
    public void contains(String date) throws ParseException {
        if(date == null)
            return;        
        try {
            Date.parse(date.replaceAll("-", "/"));
        } catch (IllegalArgumentException ex){
            throw new ParseException("illegalDateValueException");
        }   
        
    }

    public Type getType() {
        return Type.DATE;
    }
    
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
}
