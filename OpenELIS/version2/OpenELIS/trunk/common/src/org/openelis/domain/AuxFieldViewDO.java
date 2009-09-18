package org.openelis.domain;

import org.openelis.utilcommon.DataBaseUtil;

public class AuxFieldViewDO extends AuxFieldDO {

    private static final long serialVersionUID = 1L;
    
    protected String analyteName, methodName, scriptletName;
    
    public AuxFieldViewDO(){
        
    }
    
    public AuxFieldViewDO(Integer id, Integer auxFieldGroupId, Integer sortOrder, Integer analyteId,
                          String description, Integer methodId, Integer unitOfMeasureId,
                          String isRequired, String isActive, String isReportable, Integer scriptletId,
                          String analyteName, String methodName, String scriptletName){
        
        super(id, auxFieldGroupId, sortOrder, analyteId, description, methodId, unitOfMeasureId, isRequired, isActive, isReportable, scriptletId);
        
        setAnalyteName(analyteName);
        setMethodName(methodName);
        setScriptletName(scriptletName);
    }

    public String getAnalyteName() {
        return analyteName;
    }

    public void setAnalyteName(String analyteName) {
        this.analyteName = DataBaseUtil.trim(analyteName);
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = DataBaseUtil.trim(methodName);
    }

    public String getScriptletName() {
        return scriptletName;
    }

    public void setScriptletName(String scriptletName) {
        this.scriptletName = DataBaseUtil.trim(scriptletName);
    }
}
