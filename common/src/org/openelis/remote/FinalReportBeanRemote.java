package org.openelis.remote;

import java.util.ArrayList;

import javax.ejb.Remote;

import org.openelis.gwt.common.data.QueryData;
import org.openelis.report.Prompt;

@Remote
public interface FinalReportBeanRemote {

    public ArrayList<Prompt> getPrompts() throws Exception;
    
    public void runReport(ArrayList<QueryData> paramList) throws Exception;
}
