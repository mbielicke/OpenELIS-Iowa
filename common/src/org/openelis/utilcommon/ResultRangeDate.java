package org.openelis.utilcommon;

import java.util.Date;

import org.openelis.exception.ParseException;
import org.openelis.utilcommon.ResultValidator.Type;

public class ResultRangeDate implements Result {
    private static final long serialVersionUID = 1L;

    public void validate(String date) throws ParseException {
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
}
