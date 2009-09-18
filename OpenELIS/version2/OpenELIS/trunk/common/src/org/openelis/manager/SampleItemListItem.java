package org.openelis.manager;

import java.io.Serializable;

import org.openelis.domain.SampleItemViewDO;

public class SampleItemListItem implements Serializable {

    private static final long serialVersionUID = 1L;

    SampleItemViewDO    sampleItem;
    StorageManager  storage;
    AnalysisManager analysis;
}
