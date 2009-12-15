package org.openelis.utilcommon;

import java.util.Date;

import org.openelis.exception.ParseException;
import org.openelis.utilcommon.ResultValidator.Type;

public class ResultRangeDateTime implements Result {
    private static final long serialVersionUID = 1L;

    public void validate(String dateTime) throws ParseException {
        String st[];
        String hhmm;

        if (dateTime == null) {
            return;
        }
        try {
            st = dateTime.split(" ");
            if (st.length != 2)
                throw new ParseException("illegalDateTimeValueException");

            hhmm = st[1];
            if (hhmm.split(":").length != 2)
                throw new ParseException("illegalDateTimeValueException");

            Date.parse(dateTime.replaceAll("-", "/"));
        } catch (IllegalArgumentException ex) {
            throw new ParseException("illegalDateTimeValueException");
        }                    
    }

    public Type getType() {
        return Type.DATE_TIME;
    }
}
