package org.openelis.utilcommon;

import java.util.Date;

import org.openelis.exception.ParseException;
import org.openelis.utilcommon.ResultValidator.Type;

public class ResultRangeTime implements ResultType {
    private static final long serialVersionUID = 1L;

    protected Integer id;
    
    public void contains(String time) throws ParseException {
        String st[];
        Date date;
        
        if (time == null) 
            return;
        
        try {
            st = time.split(":");
           
            if(st.length == 3)
                date = new Date(0,11,31,Integer.parseInt(st[0]),Integer.parseInt(st[1]),Integer.parseInt(st[2]));
            else
                date = new Date(0,11,31,Integer.parseInt(st[0]),Integer.parseInt(st[1]));
            
        } catch (IllegalArgumentException ex) {
            throw new ParseException("illegalTimeValueException");
        }
    }

    public Type getType() {
        return Type.TIME;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
}
