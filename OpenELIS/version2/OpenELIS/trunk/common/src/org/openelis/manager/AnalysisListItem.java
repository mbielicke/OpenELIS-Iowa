package org.openelis.manager;

import java.io.Serializable;

import org.openelis.domain.AnalysisTestDO;

public class AnalysisListItem implements Serializable {
    private static final long serialVersionUID = 1L;

    AnalysisTestDO            analysis;
    AnalysisQaEventManager    qaEvents;
    NoteManager               analysisInternalNotes, analysisExternalNote;
    StorageManager            storage;
}
