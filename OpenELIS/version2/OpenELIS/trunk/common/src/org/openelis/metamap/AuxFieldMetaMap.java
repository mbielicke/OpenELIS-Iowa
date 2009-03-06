package org.openelis.metamap;

import org.openelis.gwt.common.MetaMap;
import org.openelis.meta.AuxFieldMeta;

public class AuxFieldMetaMap extends AuxFieldMeta implements MetaMap {

    public String buildFrom(String where) {        
        return "AuxField auxf";
    }

}
