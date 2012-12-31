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
        return lookup("AnalysisBean");
    }

    public static AnalysisQAEventBean getAnalysisQAEvent() {
        return lookup("AnalysisQAEventBean");
    }

    public static AnalysisReportFlagsBean getAnalysisReportFlags() {
        return lookup("AnalysisReportFlagsBean");
    }

    public static AnalysisUserBean getAnalysisUser() {
        return lookup("AnalysisUserBean");
    }
    
    public static AnalyteBean getAnalyte() {
        return lookup("AnalyteBean");
    }
    
    public static AnalyteParameterBean getAnalyteParameter() {
        return lookup("AnalyteParameterBean");
    }

    public static AuxDataBean getAuxData() {
        return lookup("AuxDataBean");
    }    

    public static AuxFieldGroupBean getAuxFieldGroup() {
        return lookup("AuxFieldGroupBean");
    }

    public static AuxFieldBean getAuxField() {
        return lookup("AuxFieldBean");
    }

    public static AuxFieldValueBean getAuxFieldValue() {
        return lookup("AuxFieldValueBean");
    }
    
    public static CategoryBean getCategory() {
        return lookup("CategoryBean");
    }

    public static DictionaryBean getDictionary() {
        return lookup("DictionaryBean");
    }  
    
    public static ExchangeLocalTermBean getExchangeLocalTerm() {
        return lookup("ExchangeLocalTermBean");
    }
    
    public static ExchangeExternalTermBean getExchangeExternalTerm() {
        return lookup("ExchangeExternalTermBean");
    }
    
    public static ExchangeCriteriaBean getExchangeCriteria() {
        return lookup("ExchangeCriteriaBean");
    }
    
    public static ExchangeProfileBean getExchangeProfile() {
        return lookup("ExchangeProfileBean");
    }
    
    public static EventLogBean getEventLog() {
        return lookup("EventLogBean");
    }
    
    public static HistoryBean getHistory() {
        return lookup("HistoryBean");
    }

    public static InstrumentBean getInstrument() {
        return lookup("InstrumentBean");
    }   
    
    public static InstrumentLogBean getInstrumentLog() {
        return lookup("InstrumentLogBean");
    }
    
    public static InventoryAdjustmentBean getInventoryAdjustment() {
        return lookup("InventoryAdjustmentBean");
    }

    public static InventoryComponentBean getInventoryComponent() {
        return lookup("InventoryComponentBean");
    }

    public static InventoryItemBean getInventoryItem() {
        return lookup("InventoryItemBean");
    }

    public static InventoryLocationBean getInventoryLocation() {
        return lookup("InventoryLocationBean");
    }    

    public static InventoryReceiptBean getInventoryReceipt() {
        return lookup("InventoryReceiptBean");
    }

    public static InventoryXAdjustBean getInventoryXAdjust() {
        return lookup("InventoryXAdjustBean");
    }

    public static InventoryXPutBean getInventoryXPut() {
        return lookup("InventoryXPutBean");
    }

    public static InventoryXUseBean getInventoryXUse() {
        return lookup("InventoryXUseBean");
    }
    
    public static LockBean getLock() {
        return lookup("LockBean");
    }
    
    public static NoteBean getNote() {
        return lookup("NoteBean");
    }

    public static OrderOrganizationBean getOrderOrganization() {
        return lookup("OrderOrganizationBean");
    }
    
    public static OrderContainerBean getOrderContainer() {
        return lookup("OrderContainerBean");
    }

    public static OrderItemBean getOrderItem() {
        return lookup("OrderItemBean");
    }
    
    public static OrderBean getOrder() {
        return lookup("OrderBean");
    }
    
    public static OrderTestBean getOrderTest() {
        return lookup("OrderTestBean");
    }
    
    public static OrderTestAnalyteBean getOrderTestAnalyte() {
        return lookup("OrderTestAnalyteBean");
    }
    
    public static OrderRecurrenceBean getOrderRecurrence() {
        return lookup("OrderRecurrenceBean");
    }
    
    public static OrganizationContactBean getOrganizationContact() {
        return lookup("OrganizationContactBean");
    }
    
    
    public static OrganizationBean getOrganization() {
        return lookup("OrganizationBean");
    }
    
    
    public static OrganizationParameterBean getOrganizationParameter() {
        return lookup("OrganizationParameterBean");
    }

    public static PanelItemBean getPanelItem() {
        return lookup("PanelItemBean");
    }
    
    public static PanelBean getPanel() {
        return lookup("PanelBean");
    }
    
    public static ProjectBean getProject() {
        return lookup("ProjectBean");
    }
    
    public static ProjectParameterBean getProjectParameter() {
        return lookup("ProjectParameterBean");
    }
    
    public static ProviderBean getProvider() {
        return lookup("ProviderBean");
    }
    
    public static ProviderLocationBean getProviderLocation() {
        return lookup("ProviderLocationBean");
    }
    
    public static PWSAddressBean getPWSAddress() {
        return lookup("PWSAddressBean");
    }
    
    public static PWSFacilityBean getPWSFacility() {
        return lookup("PWSFacilityBean");
    }        
    
    public static PWSBean getPWS() {
        return lookup("PWSBean");
    }
    
    public static PWSMonitorBean getPWSMonitor() {
        return lookup("PWSMonitorBean");
    }

    public static QaEventBean getQaevent() {
        return lookup("QaEventBean");
    }
    
    public static QcAnalyteBean getQcAnalyte() {
        return lookup("QcAnalyteBean");
    }
    
    public static QcLotBean getQcLot() {
        return lookup("QcLotBean");
    }
    
    public static QcBean getQc() {
        return lookup("QcBean");
    }

    public static ResultBean getResult() {
        return lookup("ResultBean");
    }
    
    public static SampleEnvironmentalBean getSampleEnvironmental() {
        return lookup("SampleEnvironmentalBean");
    }
    
    public static SampleItemBean getSampleItem() {
        return lookup("SampleItemBean");
    }
    
    public static SampleBean getSample() {
        return lookup("SampleBean");
    }
    
    public static SampleManagerBean getSampleManager() {
        return lookup("SampleManagerBean");
    }
    
    public static SampleOrganizationBean getSampleOrganization() {
        return lookup("SampleOrganizationBean");
    }
                                                                                                                                                                                                                            
    public static SamplePrivateWellBean getSamplePrivateWell() {
        return lookup("SamplePrivateWellBean");
    }
    
    public static SampleProjectBean getSampleProject() {
        return lookup("SampleProjectBean");
    }
    
    public static SampleQAEventBean getSampleQAEvent() {
        return lookup("SampleQAEventBean");
    }
    
    public static SampleSDWISBean getSampleSDWIS() {
        return lookup("SampleSDWISBean");
    }
    
    public static SectionBean getSection() {
        return lookup("SectionBean");
    }    
    
    public static SectionParameterBean getSectionParameter() {
        return lookup("SectionParameterBean");
    }
    
    public static ShippingItemBean getShippingItem() {
        return lookup("ShippingItemBean");
    }
    
    public static ShippingBean getShipping() {
        return lookup("ShippingBean");
    }
    
    public static ShippingTrackingBean getShippingTracking() {
        return lookup("ShippingTrackingBean");
    }
    
    public static StorageBean getStorage() {
        return lookup("StorageBean");
    }
    
    public static StorageLocationBean getStorageLocation() {
        return lookup("StorageLocationBean");
    }
    
    public static SystemVariableBean getSystemVariable() {
        return lookup("SystemVariableBean");
    }
    
    public static TestAnalyteBean getTestAnalyte() {
        return lookup("TestAnalyteBean");
    }
    
    public static TestBean getTest() {
        return lookup("TestBean");
    }
    
    public static TestPrepBean getTestPrep() {
        return lookup("TestPrepBean");
    }
    
    public static TestReflexBean getTestReflex() {
        return lookup("TestReflexBean");
    }
    
    public static TestResultBean getTestResult() {
        return lookup("TestResultBean");
    }
    
    public static TestSectionBean getTestSection() {
        return lookup("TestSectionBean");
    }
    
    public static TestTrailerBean getTestTrailer() {
        return lookup("TestTrailerBean");
    }
    
    public static TestTypeOfSampleBean getTestTypeOfSample() {
        return lookup("TestTypeOfSampleBean");
    }
    
    public static TestWorksheetAnalyteBean getTestWorksheetAnalyte() {
        return lookup("TestWorksheetAnalyteBean");
    }
    
    public static TestWorksheetItemBean getTestWorksheetItem() {
        return lookup("TestWorksheetItemBean");
    }
    
    public static TestWorksheetBean getTestWorksheet() {
        return lookup("TestWorksheetBean");
    }
    
    public static WorksheetAnalysisBean getWorksheetAnalysis() {
        return lookup("WorksheetAnalysisBean");
    }
    
    public static WorksheetItemBean getWorksheetItem() {
        return lookup("WorksheetItemBean");
    }
    
    public static WorksheetBean getWorksheet() {
        return lookup("WorksheetBean");
    }
    
    public static WorksheetQcResultBean getWorksheetQcResult() {
        return lookup("WorksheetQcResultBean");
    }
    
    public static WorksheetResultBean getWorksheetResult() {
        return lookup("WorksheetResultBean");
    }
    
    public static DictionaryCacheBean getDictionaryCache() {
        return lookup("DictionaryCacheBean");
    }
    
    public static CategoryCacheBean getCategoryCache() {
        return lookup("CategoryCacheBean");
    }
    
    public static InventoryItemCacheBean getInventoryItemCache() {
        return lookup("InventoryItemCacheBean");
    }                                                            
    
    public static SectionCacheBean getSectionCache() {
        return lookup("SectionCacheBean");
    }
    
    public static SessionCacheBean getSessionCache() {
        return lookup("SessionCacheBean");
    }
    
    public static UserCacheBean getUserCache() {
        return lookup("UserCacheBean");
    }                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                 

    public static PreferencesBean getPreferences() {
        return lookup("PreferencesBean");
    }

    public static PrinterCacheBean getPrinterCache() {
    	return lookup("PrinterCacheBean");
    }
    /*
     * Bean from Security project
     */
    public static SystemUserPermissionRemote getSecurity() {
        return lookup("java:global/security/security.jar/SystemUserPermissionBean!org.openelis.security.remote.SystemUserPermissionRemote");
    }

    private static <T> T lookup(String bean) {
        InitialContext ctx;
        
        if (!bean.startsWith("java"))
            bean = "java:app/openelis.jar/" + bean; 
            
        try {
            ctx = new InitialContext();
            return (T)ctx.lookup(bean);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        }
    }
}