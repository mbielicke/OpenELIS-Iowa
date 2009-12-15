package org.openelis.utilcommon;

import org.openelis.exception.ParseException;
import org.openelis.gwt.common.RPC;
import org.openelis.utilcommon.ResultValidator.Type;

public interface Result extends RPC {
    public void validate(String value) throws ParseException;
    public Type getType();
}
