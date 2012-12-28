package org.openelis.local;

import javax.ejb.Local;


@Local
public interface TurnaroundNotificationWarningReportLocal {
    public void runReport() throws Exception;
}
