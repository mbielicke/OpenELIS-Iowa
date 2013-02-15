package org.openelis.domain;

import org.openelis.gwt.common.DataBaseUtil;

public class AuxFieldViewDO extends AuxFieldDO {

    private static final long serialVersionUID = 1L;

    protected String          auxFieldGroupName, analyteName, methodName, scriptletName,
                              unitOfMeasureName;
    
    public AuxFieldViewDO(){
        
    }
    
    public AuxFieldViewDO(Integer id, Integer auxFieldGroupId, Integer sortOrder, Integer analyteId,
                          String description, Integer methodId, Integer unitOfMeasureId,
                          String isRequired, String isActive, String isReportable, Integer scriptletId,
                          String auxFieldGroupName, String analyteName, String methodName,
                          String scriptletName, String unitOfMeasureName){
        
        super(id, auxFieldGroupId, sortOrder, analyteId, description, methodId, unitOfMeasureId, isRequired, isActive, isReportable, scriptletId);
        setAuxFieldGroupName(auxFieldGroupName);
        setAnalyteName(analyteName);
        setMethodName(methodName);
        setScriptletName(scriptletName);
        setUnitOfMeasureName(unitOfMeasureName);
    }
    
    public String getAuxFieldGroupName() {
        return auxFieldGroupName;        
    }

    public void setAuxFieldGroupName(String auxFieldGroupName) {
        this.auxFieldGroupName = DataBaseUtil.trim(auxFieldGroupName);
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

    public String getUnitOfMeasureName() {
        return unitOfMeasureName;
    }

    public void setUnitOfMeasureName(String unitOfMeasureName) {
        this.unitOfMeasureName = unitOfMeasureName;
    }
}
