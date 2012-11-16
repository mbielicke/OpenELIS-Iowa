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

import org.openelis.local.AnalysisLocal;
import org.openelis.local.AnalysisQAEventLocal;
import org.openelis.local.AnalysisReportFlagsLocal;
import org.openelis.local.AnalysisUserLocal;
import org.openelis.local.AnalyteLocal;
import org.openelis.local.AnalyteParameterLocal;
import org.openelis.local.AuxDataLocal;
import org.openelis.local.AuxFieldGroupLocal;
import org.openelis.local.AuxFieldLocal;
import org.openelis.local.AuxFieldValueLocal;
import org.openelis.local.CategoryCacheLocal;
import org.openelis.local.CategoryLocal;
import org.openelis.local.DictionaryCacheLocal;
import org.openelis.local.DictionaryLocal;
import org.openelis.local.EventLogLocal;
import org.openelis.local.ExchangeCriteriaLocal;
import org.openelis.local.ExchangeExternalTermLocal;
import org.openelis.local.ExchangeLocalTermLocal;
import org.openelis.local.ExchangeProfileLocal;
import org.openelis.local.HistoryLocal;
import org.openelis.local.InstrumentLocal;
import org.openelis.local.InstrumentLogLocal;
import org.openelis.local.InventoryAdjustmentLocal;
import org.openelis.local.InventoryComponentLocal;
import org.openelis.local.InventoryItemCacheLocal;
import org.openelis.local.InventoryItemLocal;
import org.openelis.local.InventoryLocationLocal;
import org.openelis.local.InventoryReceiptLocal;
import org.openelis.local.InventoryXAdjustLocal;
import org.openelis.local.InventoryXPutLocal;
import org.openelis.local.InventoryXUseLocal;
import org.openelis.local.LockLocal;
import org.openelis.local.MethodLocal;
import org.openelis.local.NoteLocal;
import org.openelis.local.OrderContainerLocal;
import org.openelis.local.OrderItemLocal;
import org.openelis.local.OrderLocal;
import org.openelis.local.OrderManagerLocal;
import org.openelis.local.OrderOrganizationLocal;
import org.openelis.local.OrderRecurrenceLocal;
import org.openelis.local.OrderTestAnalyteLocal;
import org.openelis.local.OrderTestLocal;
import org.openelis.local.OrganizationContactLocal;
import org.openelis.local.OrganizationLocal;
import org.openelis.local.OrganizationParameterLocal;
import org.openelis.local.PWSAddressLocal;
import org.openelis.local.PWSFacilityLocal;
import org.openelis.local.PWSLocal;
import org.openelis.local.PWSMonitorLocal;
import org.openelis.local.PanelItemLocal;
import org.openelis.local.PanelLocal;
import org.openelis.local.PreferencesLocal;
import org.openelis.local.PrinterCacheLocal;
import org.openelis.local.ProjectLocal;
import org.openelis.local.ProjectParameterLocal;
import org.openelis.local.ProviderLocal;
import org.openelis.local.ProviderLocationLocal;
import org.openelis.local.QaeventLocal;
import org.openelis.local.QcAnalyteLocal;
import org.openelis.local.QcLocal;
import org.openelis.local.QcLotLocal;
import org.openelis.local.ResultLocal;
import org.openelis.local.SampleEnvironmentalLocal;
import org.openelis.local.SampleItemLocal;
import org.openelis.local.SampleLocal;
import org.openelis.local.SampleManagerLocal;
import org.openelis.local.SampleOrganizationLocal;
import org.openelis.local.SamplePrivateWellLocal;
import org.openelis.local.SampleProjectLocal;
import org.openelis.local.SampleQAEventLocal;
import org.openelis.local.SampleSDWISLocal;
import org.openelis.local.SectionCacheLocal;
import org.openelis.local.SectionLocal;
import org.openelis.local.SectionParameterLocal;
import org.openelis.local.SessionCacheLocal;
import org.openelis.local.ShippingItemLocal;
import org.openelis.local.ShippingLocal;
import org.openelis.local.ShippingTrackingLocal;
import org.openelis.local.StorageLocal;
import org.openelis.local.StorageLocationLocal;
import org.openelis.local.SystemVariableLocal;
import org.openelis.local.TestAnalyteLocal;
import org.openelis.local.TestLocal;
import org.openelis.local.TestPrepLocal;
import org.openelis.local.TestReflexLocal;
import org.openelis.local.TestResultLocal;
import org.openelis.local.TestSectionLocal;
import org.openelis.local.TestTrailerLocal;
import org.openelis.local.TestTypeOfSampleLocal;
import org.openelis.local.TestWorksheetAnalyteLocal;
import org.openelis.local.TestWorksheetItemLocal;
import org.openelis.local.TestWorksheetLocal;
import org.openelis.local.UserCacheLocal;
import org.openelis.local.WorksheetAnalysisLocal;
import org.openelis.local.WorksheetItemLocal;
import org.openelis.local.WorksheetLocal;
import org.openelis.local.WorksheetQcResultLocal;
import org.openelis.local.WorksheetResultLocal;
import org.openelis.security.remote.SystemUserPermissionRemote;

/**
 * This static class is used to get local instance EJB beans for non ejb class
 * calls.
 */

public class EJBFactory {
    
    public static AnalysisLocal getAnalysis() {
        return (AnalysisLocal)lookup("AnalysisBean!org.openelis.local.AnalysisLocal");
    }

    public static AnalysisQAEventLocal getAnalysisQAEvent() {
        return (AnalysisQAEventLocal)lookup("AnalysisQAEventBean!org.openelis.local.AnalysisQAEventLocal");
    }

    public static AnalysisReportFlagsLocal getAnalysisReportFlags() {
        return (AnalysisReportFlagsLocal)lookup("AnalysisReportFlagsBean!org.openelis.local.AnalysisReportFlagsLocal");
    }

    public static AnalysisUserLocal getAnalysisUser() {
        return (AnalysisUserLocal)lookup("AnalysisUserBean!org.openelis.local.AnalysisUserLocal");
    }
    
    public static AnalyteLocal getAnalyte() {
        return (AnalyteLocal)lookup("AnalyteBean!org.openelis.local.AnalyteLocal");
    }
    
    public static AnalyteParameterLocal getAnalyteParameter() {
        return (AnalyteParameterLocal)lookup("AnalyteParameterBean!org.openelis.local.AnalyteParameterLocal");
    }

    public static AuxDataLocal getAuxData() {
        return (AuxDataLocal)lookup("AuxDataBean!org.openelis.local.AuxDataLocal");
    }    

    public static AuxFieldGroupLocal getAuxFieldGroup() {
        return (AuxFieldGroupLocal)lookup("AuxFieldGroupBean!org.openelis.local.AuxFieldGroupLocal");
    }

    public static AuxFieldLocal getAuxField() {
        return (AuxFieldLocal)lookup("AuxFieldBean!org.openelis.local.AuxFieldLocal");
    }

    public static AuxFieldValueLocal getAuxFieldValue() {
        return (AuxFieldValueLocal)lookup("AuxFieldValueBean!org.openelis.local.AuxFieldValueLocal");
    }
    
    public static CategoryLocal getCategory() {
        return (CategoryLocal)lookup("CategoryBean!org.openelis.local.CategoryLocal");
    }

    public static DictionaryLocal getDictionary() {
        return (DictionaryLocal)lookup("DictionaryBean!org.openelis.local.DictionaryLocal");
    }  
    
    public static ExchangeLocalTermLocal getExchangeLocalTerm() {
        return (ExchangeLocalTermLocal)lookup("ExchangeLocalTermBean!org.openelis.local.ExchangeLocalTermLocal");
    }
    
    public static ExchangeExternalTermLocal getExchangeExternalTerm() {
        return (ExchangeExternalTermLocal)lookup("ExchangeExternalTermBean!org.openelis.local.ExchangeExternalTermLocal");
    }
    
    public static ExchangeCriteriaLocal getExchangeCriteria() {
        return (ExchangeCriteriaLocal)lookup("ExchangeCriteriaBean!org.openelis.local.ExchangeCriteriaLocal");        
    }
    
    public static ExchangeProfileLocal getExchangeProfile() {
        return (ExchangeProfileLocal)lookup("ExchangeProfileBean!org.openelis.local.ExchangeProfileLocal");
    }
    
    public static EventLogLocal getEventLog() {
        return (EventLogLocal)lookup("EventLogBean!org.openelis.local.EventLogLocal");
    }
    
    public static HistoryLocal getHistory() {
        return (HistoryLocal)lookup("HistoryBean!org.openelis.local.HistoryLocal");
    }

    public static InstrumentLocal getInstrument() {
        return (InstrumentLocal)lookup("InstrumentBean!org.openelis.local.InstrumentLocal");
    }   
    
    public static InstrumentLogLocal getInstrumentLog() {
        return (InstrumentLogLocal)lookup("InstrumentLogBean!org.openelis.local.InstrumentLogLocal");
    }
    
    public static InventoryAdjustmentLocal getInventoryAdjustment() {
        return (InventoryAdjustmentLocal)lookup("InventoryAdjustmentBean!org.openelis.local.InventoryAdjustmentLocal");
    }

    public static InventoryComponentLocal getInventoryComponent() {
        return (InventoryComponentLocal)lookup("InventoryComponentBean!org.openelis.local.InventoryComponentLocal");
    }

    public static InventoryItemLocal getInventoryItem() {
        return (InventoryItemLocal)lookup("InventoryItemBean!org.openelis.local.InventoryItemLocal");
    }

    public static InventoryLocationLocal getInventoryLocation() {
        return (InventoryLocationLocal)lookup("InventoryLocationBean!org.openelis.local.InventoryLocationLocal");
    }    

    public static InventoryReceiptLocal getInventoryReceipt() {
        return (InventoryReceiptLocal)lookup("InventoryReceiptBean!org.openelis.local.InventoryReceiptLocal");
    }

    public static InventoryXAdjustLocal getInventoryXAdjust() {
        return (InventoryXAdjustLocal)lookup("InventoryXAdjustBean!org.openelis.local.InventoryXAdjustLocal");
    }

    public static InventoryXPutLocal getInventoryXPut() {
        return (InventoryXPutLocal)lookup("InventoryXPutBean!org.openelis.local.InventoryXPutLocal");
    }

    public static InventoryXUseLocal getInventoryXUse() {
        return (InventoryXUseLocal)lookup("InventoryXUseBean!org.openelis.local.InventoryXUseLocal");
    }
    
    public static LockLocal getLock() {
        return (LockLocal)lookup("LockBean!org.openelis.local.LockLocal");
    }
    
    public static MethodLocal getMethod() {
        return (MethodLocal)lookup("MethodBean!org.openelis.local.MethodLocal");
    }

    public static NoteLocal getNote() {
        return (NoteLocal)lookup("NoteBean!org.openelis.local.NoteLocal");
    }

    public static OrderOrganizationLocal getOrderOrganization() {
        return (OrderOrganizationLocal)lookup("OrderOrganizationBean!org.openelis.local.OrderOrganizationLocal");
    }
    
    public static OrderContainerLocal getOrderContainer() {
        return (OrderContainerLocal)lookup("OrderContainerBean!org.openelis.local.OrderContainerLocal");
    }

    public static OrderItemLocal getOrderItem() {
        return (OrderItemLocal)lookup("OrderItemBean!org.openelis.local.OrderItemLocal");
    }
    
    public static OrderLocal getOrder() {
        return (OrderLocal)lookup("OrderBean!org.openelis.local.OrderLocal");
    }
    
    public static OrderManagerLocal getOrderManager() {
        return (OrderManagerLocal)lookup("OrderManagerBean!org.openelis.local.OrderManagerLocal");
    }
    
    public static OrderTestLocal getOrderTest() {
        return (OrderTestLocal)lookup("OrderTestBean!org.openelis.local.OrderTestLocal");
    }
    
    public static OrderTestAnalyteLocal getOrderTestAnalyte() {
        return (OrderTestAnalyteLocal)lookup("OrderTestAnalyteBean!org.openelis.local.OrderTestAnalyteLocal");
    }
    
    public static OrderRecurrenceLocal getOrderRecurrence() {
        return (OrderRecurrenceLocal)lookup("OrderRecurrenceBean!org.openelis.local.OrderRecurrenceLocal");
    }
    
    public static OrganizationContactLocal getOrganizationContact() {
        return (OrganizationContactLocal)lookup("OrganizationContactBean!org.openelis.local.OrganizationContactLocal");
    }
    
    public static OrganizationLocal getOrganization() {
        return (OrganizationLocal)lookup("OrganizationBean!org.openelis.local.OrganizationLocal");
    }
    
    public static OrganizationParameterLocal getOrganizationParameter() {
        return (OrganizationParameterLocal)lookup("OrganizationParameterBean!org.openelis.local.OrganizationParameterLocal");
    }

    public static PanelItemLocal getPanelItem() {
        return (PanelItemLocal)lookup("PanelItemBean!org.openelis.local.PanelItemLocal");
    }
    
    public static PanelLocal getPanel() {
        return (PanelLocal)lookup("PanelBean!org.openelis.local.PanelLocal");
    }
    
    public static ProjectLocal getProject() {
        return (ProjectLocal)lookup("ProjectBean!org.openelis.local.ProjectLocal");
    }
    
    public static ProjectParameterLocal getProjectParameter() {
        return (ProjectParameterLocal)lookup("ProjectParameterBean!org.openelis.local.ProjectParameterLocal");
    }
    
    public static ProviderLocal getProvider() {
        return (ProviderLocal)lookup("ProviderBean!org.openelis.local.ProviderLocal");
    }
    
    public static ProviderLocationLocal getProviderLocation() {
        return (ProviderLocationLocal)lookup("ProviderLocationBean!org.openelis.local.ProviderLocationLocal");
    }
    
    public static PWSAddressLocal getPWSAddress() {
        return (PWSAddressLocal)lookup("PWSAddressBean!org.openelis.local.PWSAddressLocal");
    }
    
    public static PWSFacilityLocal getPWSFacility() {
        return (PWSFacilityLocal)lookup("PWSFacilityBean!org.openelis.local.PWSFacilityLocal");
    }        
    
    public static PWSLocal getPWS() {
        return (PWSLocal)lookup("PWSBean!org.openelis.local.PWSLocal");
    }
    
    public static PWSMonitorLocal getPWSMonitor() {
        return (PWSMonitorLocal)lookup("PWSMonitorBean!org.openelis.local.PWSMonitorLocal");
    }

    public static QaeventLocal getQaevent() {
        return (QaeventLocal)lookup("QaEventBean!org.openelis.local.QaEventLocal");
    }
    
    public static QcAnalyteLocal getQcAnalyte() {
        return (QcAnalyteLocal)lookup("QcAnalyteBean!org.openelis.local.QcAnalyteLocal");
    }
    
    public static QcLotLocal getQcLot() {
        return (QcLotLocal)lookup("QcLotBean!org.openelis.local.QcLotLocal");
    }
    
    public static QcLocal getQc() {
        return (QcLocal)lookup("QcBean!org.openelis.local.QcLocal");
    }

    public static ResultLocal getResult() {
        return (ResultLocal)lookup("ResultBean!org.openelis.local.ResultLocal");
    }
    
    public static SampleEnvironmentalLocal getSampleEnvironmental() {
        return (SampleEnvironmentalLocal)lookup("SampleEnvironmentalBean!org.openelis.local.SampleEnvironmentalLocal");
    }
    
    public static SampleItemLocal getSampleItem() {
        return (SampleItemLocal)lookup("SampleItemBean!org.openelis.local.SampleItemLocal");                                                                                                                                                                                                                                                        
    }
    
    public static SampleLocal getSample() {
        return (SampleLocal)lookup("SampleBean!org.openelis.local.SampleLocal");
    }
    
    public static SampleManagerLocal getSampleManager() {
        return (SampleManagerLocal)lookup("SampleManagerBean!org.openelis.local.SampleManagerLocal");
    }
    
    public static SampleOrganizationLocal getSampleOrganization() {
        return (SampleOrganizationLocal)lookup("SampleOrganizationBean!org.openelis.local.SampleOrganizationLocal");
    }
                                                                                                                                                                                                                            
    public static SamplePrivateWellLocal getSamplePrivateWell() {
        return (SamplePrivateWellLocal)lookup("SamplePrivateWellBean!org.openelis.local.SamplePrivateWellLocal");
    }
    
    public static SampleProjectLocal getSampleProject() {
        return (SampleProjectLocal)lookup("SampleProjectBean!org.openelis.local.SampleProjectLocal");
    }
    
    public static SampleQAEventLocal getSampleQAEvent() {
        return (SampleQAEventLocal)lookup("SampleQAEventBean!org.openelis.local.SampleQAEventLocal");
    }
    
    public static SampleSDWISLocal getSampleSDWIS() {
        return (SampleSDWISLocal)lookup("SampleSDWISBean!org.openelis.local.SampleSDWISLocal");
    }
    
    public static SectionLocal getSection() {
        return (SectionLocal)lookup("SectionBean!org.openelis.local.SectionLocal");
    }    
    
    public static SectionParameterLocal getSectionParameter() {
        return (SectionParameterLocal)lookup("SectionParameterBean!org.openelis.local.SectionParameterLocal");    
    }
    
    public static ShippingItemLocal getShippingItem() {
        return (ShippingItemLocal)lookup("ShippingItemBean!org.openelis.local.ShippingItemLocal");
    }
    
    public static ShippingLocal getShipping() {
        return (ShippingLocal)lookup("ShippingBean!org.openelis.local.ShippingLocal");
    }
    
    public static ShippingTrackingLocal getShippingTracking() {
        return (ShippingTrackingLocal)lookup("ShippingTrackingBean!org.openelis.local.ShippingTrackingLocal");
    }
    
    public static StorageLocal getStorage() {
        return (StorageLocal)lookup("StorageBean!org.openelis.local.StorageLocal");
    }
    
    public static StorageLocationLocal getStorageLocation() {
        return (StorageLocationLocal)lookup("StorageLocationBean!org.openelis.local.StorageLocationLocal");
    }
    
    public static SystemVariableLocal getSystemVariable() {
        return (SystemVariableLocal)lookup("SystemVariableBean!org.openelis.local.SystemVariableLocal");
    }
    
    public static TestAnalyteLocal getTestAnalyte() {
        return (TestAnalyteLocal)lookup("TestAnalyteBean!org.openelis.local.TestAnalyteLocal");
    }
    
    public static TestLocal getTest() {
        return (TestLocal)lookup("TestBean!org.openelis.local.TestLocal");
    }
    
    public static TestPrepLocal getTestPrep() {
        return (TestPrepLocal)lookup("TestPrepBean!org.openelis.local.TestPrepLocal");
    }
    
    public static TestReflexLocal getTestReflex() {
        return (TestReflexLocal)lookup("TestReflexBean!org.openelis.local.TestReflexLocal");
    }
    
    public static TestResultLocal getTestResult() {
        return (TestResultLocal)lookup("TestResultBean!org.openelis.local.TestResultLocal");
    }
    
    public static TestSectionLocal getTestSection() {
        return (TestSectionLocal)lookup("TestSectionBean!org.openelis.local.TestSectionLocal");
    }
    
    public static TestTrailerLocal getTestTrailer() {
        return (TestTrailerLocal)lookup("TestTrailerBean!org.openelis.local.TestTrailerLocal");
    }
    
    public static TestTypeOfSampleLocal getTestTypeOfSample() {
        return (TestTypeOfSampleLocal)lookup("TestTypeOfSampleBean!org.openelis.local.TestTypeOfSampleLocal");
    }
    
    public static TestWorksheetAnalyteLocal getTestWorksheetAnalyte() {
        return (TestWorksheetAnalyteLocal)lookup("TestWorksheetAnalyteBean!org.openelis.local.TestWorksheetAnalyteLocal");
    }
    
    public static TestWorksheetItemLocal getTestWorksheetItem() {
        return (TestWorksheetItemLocal)lookup("TestWorksheetItemBean!org.openelis.local.TestWorksheetItemLocal");
    }
    
    public static TestWorksheetLocal getTestWorksheet() {
        return (TestWorksheetLocal)lookup("TestWorksheetBean!org.openelis.local.TestWorksheetLocal");
    }
    
    public static WorksheetAnalysisLocal getWorksheetAnalysis() {
        return (WorksheetAnalysisLocal)lookup("WorksheetAnalysisBean!org.openelis.local.WorksheetAnalysisLocal");
    }
    
    public static WorksheetItemLocal getWorksheetItem() {
        return (WorksheetItemLocal)lookup("WorksheetItemBean!org.openelis.local.WorksheetItemLocal");
    }
    
    public static WorksheetLocal getWorksheet() {
        return (WorksheetLocal)lookup("WorksheetBean!org.openelis.local.WorksheetLocal");
    }
    
    public static WorksheetQcResultLocal getWorksheetQcResult() {
        return (WorksheetQcResultLocal)lookup("WorksheetQcResultBean!org.openelis.local.WorksheetQcResultLocal");
    }
    
    public static WorksheetResultLocal getWorksheetResult() {
        return (WorksheetResultLocal)lookup("WorksheetResultBean!org.openelis.local.WorksheetResultLocal");
    }
    
    public static DictionaryCacheLocal getDictionaryCache() {
        return (DictionaryCacheLocal) lookup("DictionaryCacheBean!org.openelis.local.DictionaryCacheLocal");
    }
    
    public static CategoryCacheLocal getCategoryCache() {
        return (CategoryCacheLocal) lookup("CategoryCacheBean!org.openelis.local.CategoryCacheLocal");
    }
    
    public static InventoryItemCacheLocal getInventoryItemCache() {
        return (InventoryItemCacheLocal) lookup("InventoryItemCacheBean!org.openelis.local.InventoryItemCacheLocal");
    }                                                            
    
    public static SectionCacheLocal getSectionCache() {
        return (SectionCacheLocal) lookup("SectionCacheBean!org.openelis.local.SectionCacheLocal");
    }
    
    public static SessionCacheLocal getSessionCache() {
        return (SessionCacheLocal)lookup("SessionCacheBean!org.openelis.local.SessionCacheLocal");
    }
    
    public static UserCacheLocal getUserCache() {
        return (UserCacheLocal)lookup("UserCacheBean!org.openelis.local.UserCacheLocal");
    }                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                 

    public static PreferencesLocal getPreferences() {
        return (PreferencesLocal)lookup("PreferencesBean!org.openelis.local.PreferencesLocal");
    }

    public static PrinterCacheLocal getPrinterCache() {
    	return (PrinterCacheLocal)lookup("PrinterCacheBean!org.openelis.local.PrinterCacheLocal");
    }
    /*
     * Bean from Security project
     */
    public static SystemUserPermissionRemote getSecurity() {
        return (SystemUserPermissionRemote)lookup("/security/security.jar/SystemUserPermissionBean!org.openelis.security.remote.SystemUserPermissionRemote");
    }

    private static Object lookup(String bean) {
        InitialContext ctx;
        
        if (!bean.startsWith("/"))
            bean = "/openelis/openelis.jar/" + bean; 
            
        try {
            ctx = new InitialContext();
            return ctx.lookup(bean);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        }
    }
}