package org.openelis.utilcommon;

import java.util.ArrayList;

import org.openelis.exception.ParseException;

public class AuxDataValidator extends ResultValidator {
    private static final long serialVersionUID = 1L;
    
    public Integer validate(Integer unitId, String value) throws ParseException {
        ArrayList<Item> list;
        Integer id;

        list = units.get(unitId);
        id=null;
        if (list == null)
            list = units.get(0);
        
        for (Item item : list) {
            item.resultRange.contains(value);
            id = item.id;
        }

        return id;
    }
}
