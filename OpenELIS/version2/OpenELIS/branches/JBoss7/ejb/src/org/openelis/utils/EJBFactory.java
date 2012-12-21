/**
 * Exhibit A - UIRF Open-source Based Public Software License.
 * 
 * The contents of this file are subject to the UIRF Open-source Based Public
 * Software License(the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * openelis.uhl.uiowa.edu
 * 
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License for
 * the specific language governing rights and limitations under the License.
 * 
 * The Original Code is OpenELIS code.
 * 
 * The Initial Developer of the Original Code is The University of Iowa.
 * Portions created by The University of Iowa are Copyright 2006-2008. All
 * Rights Reserved.
 * 
 * Contributor(s): ______________________________________.
 * 
 * Alternatively, the contents of this file marked "Separately-Licensed" may be
 * used under the terms of a UIRF Software license ("UIRF Software License"), in
 * which case the provisions of a UIRF Software License are applicable instead
 * of those above.
 */
package org.openelis.utils;

import javax.naming.InitialContext;

import org.openelis.bean.AnalysisBean;
import org.openelis.bean.AnalysisQAEventBean;
import org.openelis.bean.AnalysisReportFlagsBean;
import org.openelis.bean.AnalysisUserBean;
import org.openelis.bean.AnalyteBean;
import org.openelis.bean.AnalyteParameterBean;
import org.openelis.bean.AuxDataBean;
import org.openelis.bean.AuxFieldBean;
import org.openelis.bean.AuxFieldGroupBean;
import org.openelis.bean.AuxFieldValueBean;
import org.openelis.bean.CategoryBean;
import org.openelis.bean.CategoryCacheBean;
import org.openelis.bean.DictionaryBean;
import org.openelis.bean.DictionaryCacheBean;
import org.openelis.bean.EventLogBean;
import org.openelis.bean.ExchangeCriteriaBean;
import org.openelis.bean.ExchangeExternalTermBean;
import org.openelis.bean.ExchangeLocalTermBean;
import org.openelis.bean.ExchangeProfileBean;
import org.openelis.bean.HistoryBean;
import org.openelis.bean.InstrumentBean;
import org.openelis.bean.InstrumentLogBean;
import org.openelis.bean.InventoryAdjustmentBean;
import org.openelis.bean.InventoryComponentBean;
import org.openelis.bean.InventoryItemBean;
import org.openelis.bean.InventoryItemCacheBean;
import org.openelis.bean.InventoryLocationBean;
import org.openelis.bean.InventoryReceiptBean;
import org.openelis.bean.InventoryXAdjustBean;
import org.openelis.bean.InventoryXPutBean;
import org.openelis.bean.InventoryXUseBean;
import org.openelis.bean.LockBean;
import org.openelis.bean.NoteBean;
import org.openelis.bean.OrderBean;
import org.openelis.bean.OrderContainerBean;
import org.openelis.bean.OrderItemBean;
import org.openelis.bean.OrderOrganizationBean;
import org.openelis.bean.OrderRecurrenceBean;
import org.openelis.bean.OrderTestAnalyteBean;
import org.openelis.bean.OrderTestBean;
import org.openelis.bean.OrganizationBean;
import org.openelis.bean.OrganizationContactBean;
import org.openelis.bean.OrganizationParameterBean;
import org.openelis.bean.PWSAddressBean;
import org.openelis.bean.PWSBean;
import org.openelis.bean.PWSFacilityBean;
import org.openelis.bean.PWSMonitorBean;
import org.openelis.bean.PanelBean;
import org.openelis.bean.PanelItemBean;
import org.openelis.bean.PreferencesBean;
import org.openelis.bean.PrinterCacheBean;
import org.openelis.bean.ProjectBean;
import org.openelis.bean.ProjectParameterBean;
import org.openelis.bean.ProviderBean;
import org.openelis.bean.ProviderLocationBean;
import org.openelis.bean.QaEventBean;
import org.openelis.bean.QcAnalyteBean;
import org.openelis.bean.QcBean;
import org.openelis.bean.QcLotBean;
import org.openelis.bean.ResultBean;
import org.openelis.bean.SampleBean;
import org.openelis.bean.SampleEnvironmentalBean;
import org.openelis.bean.SampleItemBean;
import org.openelis.bean.SampleManagerBean;
import org.openelis.bean.SampleOrganizationBean;
import org.openelis.bean.SamplePrivateWellBean;
import org.openelis.bean.SampleProjectBean;
import org.openelis.bean.SampleQAEventBean;
import org.openelis.bean.SampleSDWISBean;
import org.openelis.bean.SectionBean;
import org.openelis.bean.SectionCacheBean;
import org.openelis.bean.SectionParameterBean;
import org.openelis.bean.SessionCacheBean;
import org.openelis.bean.ShippingBean;
import org.openelis.bean.ShippingItemBean;
import org.openelis.bean.ShippingTrackingBean;
import org.openelis.bean.StorageBean;
import org.openelis.bean.StorageLocationBean;
import org.openelis.bean.SystemVariableBean;
import org.openelis.bean.TestAnalyteBean;
import org.openelis.bean.TestBean;
import org.openelis.bean.TestPrepBean;
import org.openelis.bean.TestReflexBean;
import org.openelis.bean.TestResultBean;
import org.openelis.bean.TestSectionBean;
import org.openelis.bean.TestTrailerBean;
import org.openelis.bean.TestTypeOfSampleBean;
import org.openelis.bean.TestWorksheetAnalyteBean;
import org.openelis.bean.TestWorksheetBean;
import org.openelis.bean.TestWorksheetItemBean;
import org.openelis.bean.UserCacheBean;
import org.openelis.bean.WorksheetAnalysisBean;
import org.openelis.bean.WorksheetBean;
import org.openelis.bean.WorksheetItemBean;
import org.openelis.bean.WorksheetQcResultBean;
import org.openelis.bean.WorksheetResultBean;
import org.openelis.security.remote.SystemUserPermissionRemote;

/**
 * This static class is used to get local instance EJB beans for non ejb class
 * calls.
 */

public class EJBFactory {
    
    public static AnalysisBean getAnalysis() {
        return (AnalysisBean)lookup("AnalysisBean");
    }

    public static AnalysisQAEventBean getAnalysisQAEvent() {
        return (AnalysisQAEventBean)lookup("AnalysisQAEventBean");
    }

    public static AnalysisReportFlagsBean getAnalysisReportFlags() {
        return (AnalysisReportFlagsBean)lookup("AnalysisReportFlagsBean");
    }

    public static AnalysisUserBean getAnalysisUser() {
        return (AnalysisUserBean)lookup("AnalysisUserBean");
    }
    
    public static AnalyteBean getAnalyte() {
        return (AnalyteBean)lookup("AnalyteBean");
    }
    
    public static AnalyteParameterBean getAnalyteParameter() {
        return (AnalyteParameterBean)lookup("AnalyteParameterBean");
    }

    public static AuxDataBean getAuxData() {
        return (AuxDataBean)lookup("AuxDataBean");
    }    

    public static AuxFieldGroupBean getAuxFieldGroup() {
        return (AuxFieldGroupBean)lookup("AuxFieldGroupBean");
    }

    public static AuxFieldBean getAuxField() {
        return (AuxFieldBean)lookup("AuxFieldBean");
    }

    public static AuxFieldValueBean getAuxFieldValue() {
        return (AuxFieldValueBean)lookup("AuxFieldValueBean");
    }
    
    public static CategoryBean getCategory() {
        return (CategoryBean)lookup("CategoryBean");
    }

    public static DictionaryBean getDictionary() {
        return (DictionaryBean)lookup("DictionaryBean");
    }  
    
    public static ExchangeLocalTermBean getExchangeLocalTerm() {
        return (ExchangeLocalTermBean)lookup("ExchangeLocalTermBean");
    }
    
    public static ExchangeExternalTermBean getExchangeExternalTerm() {
        return (ExchangeExternalTermBean)lookup("ExchangeExternalTermBean");
    }
    
    public static ExchangeCriteriaBean getExchangeCriteria() {
        return (ExchangeCriteriaBean)lookup("ExchangeCriteriaBean");
    }
    
    public static ExchangeProfileBean getExchangeProfile() {
        return (ExchangeProfileBean)lookup("ExchangeProfileBean");
    }
    
    public static EventLogBean getEventLog() {
        return (EventLogBean)lookup("EventLogBean");
    }
    
    public static HistoryBean getHistory() {
        return (HistoryBean)lookup("HistoryBean");
    }

    public static InstrumentBean getInstrument() {
        return (InstrumentBean)lookup("InstrumentBean");
    }   
    
    public static InstrumentLogBean getInstrumentLog() {
        return (InstrumentLogBean)lookup("InstrumentLogBean");
    }
    
    public static InventoryAdjustmentBean getInventoryAdjustment() {
        return (InventoryAdjustmentBean)lookup("InventoryAdjustmentBean");
    }

    public static InventoryComponentBean getInventoryComponent() {
        return (InventoryComponentBean)lookup("InventoryComponentBean");
    }

    public static InventoryItemBean getInventoryItem() {
        return (InventoryItemBean)lookup("InventoryItemBean");
    }

    public static InventoryLocationBean getInventoryLocation() {
        return (InventoryLocationBean)lookup("InventoryLocationBean");
    }    

    public static InventoryReceiptBean getInventoryReceipt() {
        return (InventoryReceiptBean)lookup("InventoryReceiptBean");
    }

    public static InventoryXAdjustBean getInventoryXAdjust() {
        return (InventoryXAdjustBean)lookup("InventoryXAdjustBean");
    }

    public static InventoryXPutBean getInventoryXPut() {
        return (InventoryXPutBean)lookup("InventoryXPutBean");
    }

    public static InventoryXUseBean getInventoryXUse() {
        return (InventoryXUseBean)lookup("InventoryXUseBean");
    }
    
    public static LockBean getLock() {
        return (LockBean)lookup("LockBean");
    }
    
    public static NoteBean getNote() {
        return (NoteBean)lookup("NoteBean");
    }

    public static OrderOrganizationBean getOrderOrganization() {
        return (OrderOrganizationBean)lookup("OrderOrganizationBean");
    }
    
    public static OrderContainerBean getOrderContainer() {
        return (OrderContainerBean)lookup("OrderContainerBean");
    }

    public static OrderItemBean getOrderItem() {
        return (OrderItemBean)lookup("OrderItemBean");
    }
    
    public static OrderBean getOrder() {
        return (OrderBean)lookup("OrderBean");
    }
    
    public static OrderTestBean getOrderTest() {
        return (OrderTestBean)lookup("OrderTestBean");
    }
    
    public static OrderTestAnalyteBean getOrderTestAnalyte() {
        return (OrderTestAnalyteBean)lookup("OrderTestAnalyteBean");
    }
    
    public static OrderRecurrenceBean getOrderRecurrence() {
        return (OrderRecurrenceBean)lookup("OrderRecurrenceBean");
    }
    
    public static OrganizationContactBean getOrganizationContact() {
        return (OrganizationContactBean)lookup("OrganizationContactBean");
    }
    
    
    public static OrganizationBean getOrganization() {
        return (OrganizationBean)lookup("OrganizationBean");
    }
    
    
    public static OrganizationParameterBean getOrganizationParameter() {
        return (OrganizationParameterBean)lookup("OrganizationParameterBean");
    }

    public static PanelItemBean getPanelItem() {
        return (PanelItemBean)lookup("PanelItemBean");
    }
    
    public static PanelBean getPanel() {
        return (PanelBean)lookup("PanelBean");
    }
    
    public static ProjectBean getProject() {
        return (ProjectBean)lookup("ProjectBean");
    }
    
    public static ProjectParameterBean getProjectParameter() {
        return (ProjectParameterBean)lookup("ProjectParameterBean");
    }
    
    public static ProviderBean getProvider() {
        return (ProviderBean)lookup("ProviderBean");
    }
    
    public static ProviderLocationBean getProviderLocation() {
        return (ProviderLocationBean)lookup("ProviderLocationBean");
    }
    
    public static PWSAddressBean getPWSAddress() {
        return (PWSAddressBean)lookup("PWSAddressBean");
    }
    
    public static PWSFacilityBean getPWSFacility() {
        return (PWSFacilityBean)lookup("PWSFacilityBean");
    }        
    
    public static PWSBean getPWS() {
        return (PWSBean)lookup("PWSBean");
    }
    
    public static PWSMonitorBean getPWSMonitor() {
        return (PWSMonitorBean)lookup("PWSMonitorBean");
    }

    public static QaEventBean getQaevent() {
        return (QaEventBean)lookup("QaEventBean");
    }
    
    public static QcAnalyteBean getQcAnalyte() {
        return (QcAnalyteBean)lookup("QcAnalyteBean");
    }
    
    public static QcLotBean getQcLot() {
        return (QcLotBean)lookup("QcLotBean");
    }
    
    public static QcBean getQc() {
        return (QcBean)lookup("QcBean");
    }

    public static ResultBean getResult() {
        return (ResultBean)lookup("ResultBean");
    }
    
    public static SampleEnvironmentalBean getSampleEnvironmental() {
        return (SampleEnvironmentalBean)lookup("SampleEnvironmentalBean");
    }
    
    public static SampleItemBean getSampleItem() {
        return (SampleItemBean)lookup("SampleItemBean");
    }
    
    public static SampleBean getSample() {
        return (SampleBean)lookup("SampleBean");
    }
    
    public static SampleManagerBean getSampleManager() {
        return (SampleManagerBean)lookup("SampleManagerBean");
    }
    
    public static SampleOrganizationBean getSampleOrganization() {
        return (SampleOrganizationBean)lookup("SampleOrganizationBean");
    }
                                                                                                                                                                                                                            
    public static SamplePrivateWellBean getSamplePrivateWell() {
        return (SamplePrivateWellBean)lookup("SamplePrivateWellBean");
    }
    
    public static SampleProjectBean getSampleProject() {
        return (SampleProjectBean)lookup("SampleProjectBean");
    }
    
    public static SampleQAEventBean getSampleQAEvent() {
        return (SampleQAEventBean)lookup("SampleQAEventBean");
    }
    
    public static SampleSDWISBean getSampleSDWIS() {
        return (SampleSDWISBean)lookup("SampleSDWISBean");
    }
    
    public static SectionBean getSection() {
        return (SectionBean)lookup("SectionBean");
    }    
    
    public static SectionParameterBean getSectionParameter() {
        return (SectionParameterBean)lookup("SectionParameterBean");
    }
    
    public static ShippingItemBean getShippingItem() {
        return (ShippingItemBean)lookup("ShippingItemBean");
    }
    
    public static ShippingBean getShipping() {
        return (ShippingBean)lookup("ShippingBean");
    }
    
    public static ShippingTrackingBean getShippingTracking() {
        return (ShippingTrackingBean)lookup("ShippingTrackingBean");
    }
    
    public static StorageBean getStorage() {
        return (StorageBean)lookup("StorageBean");
    }
    
    public static StorageLocationBean getStorageLocation() {
        return (StorageLocationBean)lookup("StorageLocationBean");
    }
    
    public static SystemVariableBean getSystemVariable() {
        return (SystemVariableBean)lookup("SystemVariableBean");
    }
    
    public static TestAnalyteBean getTestAnalyte() {
        return (TestAnalyteBean)lookup("TestAnalyteBean");
    }
    
    public static TestBean getTest() {
        return (TestBean)lookup("TestBean");
    }
    
    public static TestPrepBean getTestPrep() {
        return (TestPrepBean)lookup("TestPrepBean");
    }
    
    public static TestReflexBean getTestReflex() {
        return (TestReflexBean)lookup("TestReflexBean");
    }
    
    public static TestResultBean getTestResult() {
        return (TestResultBean)lookup("TestResultBean");
    }
    
    public static TestSectionBean getTestSection() {
        return (TestSectionBean)lookup("TestSectionBean");
    }
    
    public static TestTrailerBean getTestTrailer() {
        return (TestTrailerBean)lookup("TestTrailerBean");
    }
    
    public static TestTypeOfSampleBean getTestTypeOfSample() {
        return (TestTypeOfSampleBean)lookup("TestTypeOfSampleBean");
    }
    
    public static TestWorksheetAnalyteBean getTestWorksheetAnalyte() {
        return (TestWorksheetAnalyteBean)lookup("TestWorksheetAnalyteBean");
    }
    
    public static TestWorksheetItemBean getTestWorksheetItem() {
        return (TestWorksheetItemBean)lookup("TestWorksheetItemBean");
    }
    
    public static TestWorksheetBean getTestWorksheet() {
        return (TestWorksheetBean)lookup("TestWorksheetBean");
    }
    
    public static WorksheetAnalysisBean getWorksheetAnalysis() {
        return (WorksheetAnalysisBean)lookup("WorksheetAnalysisBean");
    }
    
    public static WorksheetItemBean getWorksheetItem() {
        return (WorksheetItemBean)lookup("WorksheetItemBean");
    }
    
    public static WorksheetBean getWorksheet() {
        return (WorksheetBean)lookup("WorksheetBean");
    }
    
    public static WorksheetQcResultBean getWorksheetQcResult() {
        return (WorksheetQcResultBean)lookup("WorksheetQcResultBean");
    }
    
    public static WorksheetResultBean getWorksheetResult() {
        return (WorksheetResultBean)lookup("WorksheetResultBean");
    }
    
    public static DictionaryCacheBean getDictionaryCache() {
        return (DictionaryCacheBean) lookup("DictionaryCacheBean");
    }
    
    public static CategoryCacheBean getCategoryCache() {
        return (CategoryCacheBean) lookup("CategoryCacheBean");
    }
    
    public static InventoryItemCacheBean getInventoryItemCache() {
        return (InventoryItemCacheBean) lookup("InventoryItemCacheBean");
    }                                                            
    
    public static SectionCacheBean getSectionCache() {
        return (SectionCacheBean) lookup("SectionCacheBean");
    }
    
    public static SessionCacheBean getSessionCache() {
        return (SessionCacheBean)lookup("SessionCacheBean");
    }
    
    public static UserCacheBean getUserCache() {
        return (UserCacheBean)lookup("UserCacheBean");
    }                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                 

    public static PreferencesBean getPreferences() {
        return (PreferencesBean)lookup("PreferencesBean");
    }

    public static PrinterCacheBean getPrinterCache() {
    	return (PrinterCacheBean)lookup("PrinterCacheBean");
    }
    /*
     * Bean from Security project
     */
    public static SystemUserPermissionRemote getSecurity() {
        return (SystemUserPermissionRemote)lookup("java:global/security/security.jar/SystemUserPermissionBean!org.openelis.security.remote.SystemUserPermissionRemote");
    }

    private static Object lookup(String bean) {
        InitialContext ctx;
        
        if (!bean.startsWith("java"))
            bean = "java:app/openelis.jar/" + bean; 
            
        try {
            ctx = new InitialContext();
            return ctx.lookup(bean);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        }
    }
}