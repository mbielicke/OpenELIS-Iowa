package org.openelis.manager;

import java.io.Serializable;

import org.openelis.domain.SampleItemDO;

public class SampleItemListItem implements Serializable {

    private static final long serialVersionUID = 1L;

    SampleItemDO    sampleItem;
    StorageManager  storage;
    AnalysisManager analysis;
}
