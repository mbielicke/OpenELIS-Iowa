package org.openelis.manager;

import java.io.Serializable;

import org.openelis.domain.AnalysisViewDO;

public class AnalysisListItem implements Serializable {
    private static final long serialVersionUID = 1L;

    AnalysisResultManager     analysisResult;
    AnalysisViewDO            analysis;
    AnalysisQaEventManager    qaEvents;
    NoteManager               analysisInternalNotes, analysisExternalNote;
    StorageManager            storage;
}
