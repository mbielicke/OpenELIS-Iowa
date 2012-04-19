package org.openelis.remote;

import java.util.ArrayList;

import javax.ejb.Remote;

import org.openelis.domain.FinalReportWebVO;
import org.openelis.domain.IdNameVO;
import org.openelis.gwt.common.data.QueryData;

@Remote
public interface FinalReportWebRemote {

    public ArrayList<FinalReportWebVO> getSampleEnvironmentalList(ArrayList<QueryData> fields) throws Exception ;
    
    public ArrayList<FinalReportWebVO> getSamplePrivateWellList(ArrayList<QueryData> fields) throws Exception;
    
    public ArrayList<FinalReportWebVO> getSampleSDWISList(ArrayList<QueryData> fields) throws Exception; 
    
    public ArrayList<IdNameVO> getEnvironmentalProjectList() throws Exception;
    
    public ArrayList<IdNameVO> getPrivateWellProjectList() throws Exception;    
}
