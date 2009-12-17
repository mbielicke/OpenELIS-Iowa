package org.openelis.utilcommon;

import org.openelis.exception.ParseException;
import org.openelis.gwt.common.RPC;
import org.openelis.utilcommon.ResultValidator.Type;

public interface ResultType extends RPC {
    public void contains(String value) throws ParseException;
    public Type getType();
    public void setId(Integer id);
    public Integer getId();
}
