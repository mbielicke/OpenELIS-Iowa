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
package org.openelis.server;

import java.util.Properties;
import java.util.logging.Logger;

import javax.naming.InitialContext;

import org.openelis.remote.AnalysisManagerRemote;
import org.openelis.remote.AnalysisQAEventManagerRemote;
import org.openelis.remote.AnalysisRemote;
import org.openelis.remote.AnalysisUserManagerRemote;
import org.openelis.remote.AnalyteParameterManagerRemote;
import org.openelis.remote.AnalyteParameterRemote;
import org.openelis.remote.AnalyteRemote;
import org.openelis.remote.AuxDataManagerRemote;
import org.openelis.remote.AuxDataRemote;
import org.openelis.remote.AuxFieldGroupManagerRemote;
import org.openelis.remote.AuxFieldGroupRemote;
import org.openelis.remote.BuildKitManagerRemote;
import org.openelis.remote.BuildKitsReportRemote;
import org.openelis.remote.CategoryCacheRemote;
import org.openelis.remote.CategoryManagerRemote;
import org.openelis.remote.CategoryRemote;
import org.openelis.remote.CompleteReleaseRemote;
import org.openelis.remote.CronRemote;
import org.openelis.remote.DataExchangeReportRemote;
import org.openelis.remote.DataViewRemote;
import org.openelis.remote.DictionaryCacheRemote;
import org.openelis.remote.DictionaryRemote;
import org.openelis.remote.ExchangeLocalTermManagerRemote;
import org.openelis.remote.ExchangeLocalTermRemote;
import org.openelis.remote.FinalReportRemote;
import org.openelis.remote.FinalReportWebRemote;
import org.openelis.remote.HistoryRemote;
import org.openelis.remote.HoldRefuseOrganizationReportRemote;
import org.openelis.remote.InstrumentManagerRemote;
import org.openelis.remote.InstrumentRemote;
import org.openelis.remote.InventoryAdjustmentManagerRemote;
import org.openelis.remote.InventoryAdjustmentRemote;
import org.openelis.remote.InventoryItemCacheRemote;
import org.openelis.remote.InventoryItemManagerRemote;
import org.openelis.remote.InventoryItemRemote;
import org.openelis.remote.InventoryLocationRemote;
import org.openelis.remote.InventoryReceiptManagerRemote;
import org.openelis.remote.InventoryReceiptRemote;
import org.openelis.remote.InventoryTransferManagerRemote;
import org.openelis.remote.LabelRemote;
import org.openelis.remote.MethodRemote;
import org.openelis.remote.NoteManagerRemote;
import org.openelis.remote.OrderManagerRemote;
import org.openelis.remote.OrderRecurrenceReportRemote;
import org.openelis.remote.OrderRemote;
import org.openelis.remote.OrganizationManagerRemote;
import org.openelis.remote.OrganizationRemote;
import org.openelis.remote.PWSManagerRemote;
import org.openelis.remote.PWSRemote;
import org.openelis.remote.PanelManagerRemote;
import org.openelis.remote.PanelRemote;
import org.openelis.remote.PreferencesRemote;
import org.openelis.remote.PrinterCacheRemote;
import org.openelis.remote.ProjectManagerRemote;
import org.openelis.remote.ProjectRemote;
import org.openelis.remote.ProviderManagerRemote;
import org.openelis.remote.ProviderRemote;
import org.openelis.remote.QASummaryReportRemote;
import org.openelis.remote.QaEventRemote;
import org.openelis.remote.QcChartReportRemote;
import org.openelis.remote.QcLotRemote;
import org.openelis.remote.QcManagerRemote;
import org.openelis.remote.QcRemote;
import org.openelis.remote.RequestformReportRemote;
import org.openelis.remote.ResultManagerRemote;
import org.openelis.remote.SDWISUnloadReportRemote;
import org.openelis.remote.SampleInhouseReportRemote;
import org.openelis.remote.SampleLoginLabelReportRemote;
import org.openelis.remote.SampleManagerRemote;
import org.openelis.remote.SampleManager1Remote;
import org.openelis.remote.SampleQAEventManagerRemote;
import org.openelis.remote.SampleRemote;
import org.openelis.remote.SampleStatusReportRemote;
import org.openelis.remote.SampleTrackingRemote;
import org.openelis.remote.ScriptletRemote;
import org.openelis.remote.SectionCacheRemote;
import org.openelis.remote.SectionManagerRemote;
import org.openelis.remote.SectionRemote;
import org.openelis.remote.ShippingManagerRemote;
import org.openelis.remote.ShippingRemote;
import org.openelis.remote.ShippingReportRemote;
import org.openelis.remote.StandardNoteRemote;
import org.openelis.remote.StorageLocationManagerRemote;
import org.openelis.remote.StorageLocationRemote;
import org.openelis.remote.StorageManagerRemote;
import org.openelis.remote.StorageUnitRemote;
import org.openelis.remote.SystemVariableRemote;
import org.openelis.remote.TestManagerRemote;
import org.openelis.remote.TestRemote;
import org.openelis.remote.TestReportRemote;
import org.openelis.remote.TestTrailerRemote;
import org.openelis.remote.TestTypeOfSampleRemote;
import org.openelis.remote.ToDoAnalyteReportRemote;
import org.openelis.remote.ToDoRemote;
import org.openelis.remote.TurnaroundReportRemote;
import org.openelis.remote.TurnaroundStatisticReportRemote;
import org.openelis.remote.UserCacheRemote;
import org.openelis.remote.VerificationReportRemote;
import org.openelis.remote.VolumeReportRemote;
import org.openelis.remote.WorksheetCompletionRemote;
import org.openelis.remote.WorksheetCreationRemote;
import org.openelis.remote.WorksheetManagerRemote;
import org.openelis.remote.WorksheetRemote;
import org.openelis.remote.ExchangeCriteriaManagerRemote;
import org.openelis.remote.ExchangeCriteriaRemote;
import org.openelis.remote.EventLogRemote;
import org.openelis.util.SessionManager;

public class EJBFactory {
    private static Logger log = Logger.getLogger("openelis");
    
    public static AnalysisRemote getAnalysis() {
    	return (AnalysisRemote)lookup("AnalysisBean!org.openelis.remote.AnalysisRemote");
    }
    
    public static AnalysisManagerRemote getAnalysisManager() {
    	return (AnalysisManagerRemote)lookup("AnalysisManagerBean!org.openelis.remote.AnalysisManagerRemote");
    }
    
    public static AnalysisQAEventManagerRemote getAnalysisQAEventManager() {
    	return (AnalysisQAEventManagerRemote)lookup("AnalysisQAEventManagerBean!org.openelis.remote.AnalysisQAEventManagerRemote");
    }
	
    public static PanelRemote getPanel() {
    	return (PanelRemote)lookup("PanelBean!org.openelis.remote.PanelRemote");
    }
    
    public static AnalysisUserManagerRemote getAnalysisUserManager() {
    	return (AnalysisUserManagerRemote)lookup("AnalysisUserManagerBean!org.openelis.remote.AnalysisUserManagerRemote");
    }
    
    public static AnalyteRemote getAnalyte() {
    	return (AnalyteRemote)lookup("AnalyteBean!org.openelis.remote.AnalyteRemote");
    }
    
    public static AnalyteParameterRemote getAnalyteParameter() {
    	return (AnalyteParameterRemote)lookup("AnalyteParameterBean!org.openelis.remote.AnalyteParameterRemote");
    }
    
    public static AnalyteParameterManagerRemote getAnalyteParameterManager() {
    	return (AnalyteParameterManagerRemote)lookup("AnalyteParameterManagerBean!org.openelis.remote.AnalyteParameterManagerRemote");
    }
    
    public static AuxDataRemote getAuxData(){
        return (AuxDataRemote)lookup("AuxDataBean!org.openelis.remote.AuxDataRemote");
    }
    
    public static AuxDataManagerRemote getAuxDataManager(){
        return (AuxDataManagerRemote)lookup("AuxDataManagerBean!org.openelis.remote.AuxDataManagerRemote");
    }
    
    public static AuxFieldGroupRemote getAuxFieldGroup() {
        return (AuxFieldGroupRemote)lookup("AuxFieldGroupBean!org.openelis.remote.AuxFieldGroupRemote");
    }

    public static AuxFieldGroupManagerRemote getAuxFieldGroupManager() {
        return (AuxFieldGroupManagerRemote)lookup("AuxFieldGroupManagerBean!org.openelis.remote.AuxFieldGroupManagerRemote");
    }
    
    public static BuildKitManagerRemote getBuildKitManager() {
        return (BuildKitManagerRemote)lookup("BuildKitManagerBean!org.openelis.remote.BuildKitManagerRemote");        
    }
    
    public static CompleteReleaseRemote getCompleteRelease() {
        return (CompleteReleaseRemote)lookup("CompleteReleaseBean!org.openelis.remote.CompleteReleaseRemote");
    }  
    
    public static DataViewRemote getDataDump() {
        return (DataViewRemote)lookup("DataViewBean!org.openelis.remote.DataViewRemote");
    }     
    
    public static CategoryRemote getCategory() {
        return (CategoryRemote)lookup("CategoryBean!org.openelis.remote.CategoryRemote");
    }
    
    public static CategoryManagerRemote getCategoryManager() {
        return (CategoryManagerRemote)lookup("CategoryManagerBean!org.openelis.remote.CategoryManagerRemote");
    }

    public static DictionaryRemote getDictionary() {
        return (DictionaryRemote)lookup("DictionaryBean!org.openelis.remote.DictionaryRemote");
    }

    public static DataExchangeReportRemote getDataExchangeReport() {
        return (DataExchangeReportRemote)lookup("DataExchangeReportBean!org.openelis.remote.DataExchangeReportRemote");
    }
    
    public static ExchangeLocalTermRemote getExchangeLocalTerm() {
        return (ExchangeLocalTermRemote)lookup("ExchangeLocalTermBean!org.openelis.remote.ExchangeLocalTermRemote");
    }
    
    public static ExchangeLocalTermManagerRemote getExchangeLocalTermManager() {
        return (ExchangeLocalTermManagerRemote)lookup("ExchangeLocalTermManagerBean!org.openelis.remote.ExchangeLocalTermManagerRemote");
    }
    
    public static ExchangeCriteriaManagerRemote getExchangeCriteriaManager() {
        return (ExchangeCriteriaManagerRemote)lookup("ExchangeCriteriaManagerBean!org.openelis.remote.ExchangeCriteriaManagerRemote");
    }
    
    public static ExchangeCriteriaRemote getExchangeCriteria() {
        return (ExchangeCriteriaRemote)lookup("ExchangeCriteriaBean!org.openelis.remote.ExchangeCriteriaRemote");
    }
    
    public static EventLogRemote getEventLog() {
        return (EventLogRemote)lookup("EventLogBean!org.openelis.remote.EventLogRemote");
    }
    
    public static HistoryRemote getHistory() {
        return (HistoryRemote)lookup("HistoryBean!org.openelis.remote.HistoryRemote");
    }
    
    public static InstrumentRemote getInstrument() {
        return (InstrumentRemote)lookup("InstrumentBean!org.openelis.remote.InstrumentRemote");
    }

    public static InstrumentManagerRemote getInstrumentManager() {
        return (InstrumentManagerRemote)lookup("InstrumentManagerBean!org.openelis.remote.InstrumentManagerRemote");
    }
    
    public static InventoryAdjustmentRemote getInventoryAdjustment() {
        return (InventoryAdjustmentRemote)lookup("InventoryAdjustmentBean!org.openelis.remote.InventoryAdjustmentRemote");
    }
    
    public static InventoryAdjustmentManagerRemote getInventoryAdjustmentManager() {
        return (InventoryAdjustmentManagerRemote)lookup("InventoryAdjustmentManagerBean!org.openelis.remote.InventoryAdjustmentManagerRemote"); 
    }
    
    public static InventoryItemRemote getInventoryItem() {
        return (InventoryItemRemote)lookup("InventoryItemBean!org.openelis.remote.InventoryItemRemote");
    }

    public static InventoryItemManagerRemote getInventoryItemManager() {
        return (InventoryItemManagerRemote)lookup("InventoryItemManagerBean!org.openelis.remote.InventoryItemManagerRemote");
    }
    
    public static InventoryLocationRemote getInventoryLocation() {
        return (InventoryLocationRemote)lookup("InventoryLocationBean!org.openelis.remote.InventoryLocationRemote");
    }
    
    public static InventoryReceiptRemote getInventoryReceipt() {
        return (InventoryReceiptRemote)lookup("InventoryReceiptBean!org.openelis.remote.InventoryReceiptRemote");
    }
    
    public static InventoryReceiptManagerRemote getInventoryReceiptManager() {
        return (InventoryReceiptManagerRemote)lookup("InventoryReceiptManagerBean!org.openelis.remote.InventoryReceiptManagerRemote");        
    }
    
    public static InventoryTransferManagerRemote getInventoryTransferManager() {
        return (InventoryTransferManagerRemote)lookup("InventoryTransferManagerBean!org.openelis.remote.InventoryTransferManagerRemote");        
    }
    
    public static LabelRemote getLabel() {
        return (LabelRemote)lookup("LabelBean!org.openelis.remote.LabelRemote");
    }
    
    public static UserCacheRemote getUserCache() {
        return (UserCacheRemote)lookup("UserCacheBean!org.openelis.remote.UserCacheRemote");
    }
    
    public static MethodRemote getMethod() {
        return (MethodRemote)lookup("MethodBean!org.openelis.remote.MethodRemote");
    }
    
    public static NoteManagerRemote getNoteManager() {
        return (NoteManagerRemote)lookup("NoteManagerBean!org.openelis.remote.NoteManagerRemote");
    }
    
    public static OrderRemote getOrder() {
        return (OrderRemote)lookup("OrderBean!org.openelis.remote.OrderRemote");
    }

    public static OrderManagerRemote getOrderManager() {
        return (OrderManagerRemote)lookup("OrderManagerBean!org.openelis.remote.OrderManagerRemote");
    }
    
    public static OrganizationRemote getOrganization() {
        return (OrganizationRemote)lookup("OrganizationBean!org.openelis.remote.OrganizationRemote");
    }

    public static OrganizationManagerRemote getOrganizationManager() {
        return (OrganizationManagerRemote)lookup("OrganizationManagerBean!org.openelis.remote.OrganizationManagerRemote");
    }
    
    public static OrderRecurrenceReportRemote getOrderRecurrenceReport() {
        return (OrderRecurrenceReportRemote)lookup("OrderRecurrenceReportBean!org.openelis.remote.OrderRecurrenceReportRemote");
    }

    public static PanelManagerRemote getPanelManager() {
        return (PanelManagerRemote)lookup("PanelManagerBean!org.openelis.remote.PanelManagerRemote");
    }
    
    public static ProjectRemote getProject() {
        return (ProjectRemote)lookup("ProjectBean!org.openelis.remote.ProjectRemote");
    }

    public static ProjectManagerRemote getProjectManager() {
        return (ProjectManagerRemote)lookup("ProjectManagerBean!org.openelis.remote.ProjectManagerRemote");
    }
    
    public static ProviderRemote getProvider() {
        return (ProviderRemote)lookup("ProviderBean!org.openelis.remote.ProviderRemote");
    }

    public static ProviderManagerRemote getProviderManager() {
        return (ProviderManagerRemote)lookup("ProviderManagerBean!org.openelis.remote.ProviderManagerRemote");
    }
    
    public static PWSRemote getPWS() {
        return (PWSRemote)lookup("PWSBean!org.openelis.remote.PWSRemote");  
    }   
    
    public static PWSManagerRemote getPWSManager() {
        return (PWSManagerRemote)lookup("PWSManagerBean!org.openelis.remote.PWSManagerRemote");  
    } 
    
    public static QaEventRemote getQaEvent() {
        return (QaEventRemote)lookup("QaEventBean!org.openelis.remote.QaEventRemote");
    }
    
    public static QcRemote getQc() {
        return (QcRemote)lookup("QcBean!org.openelis.remote.QcRemote");
    }

    public static QcLotRemote getQcLot() {
        return (QcLotRemote)lookup("QcLotBean!org.openelis.remote.QcLotRemote");
    }

    public static QcManagerRemote getQcManager() {
        return (QcManagerRemote)lookup("QcManagerBean!org.openelis.remote.QcManagerRemote");
    }
    
    public static BuildKitsReportRemote getBuildKitsReport() {
        return (BuildKitsReportRemote)lookup("BuildKitsReportBean!org.openelis.remote.BuildKitsReportRemote");
    }
    
    public static FinalReportRemote getFinalReport() {
        return (FinalReportRemote)lookup("FinalReportBean!org.openelis.remote.FinalReportRemote");
    }
    
    public static FinalReportWebRemote getFinalReportWeb() {
        return (FinalReportWebRemote)lookup("FinalReportWebBean!org.openelis.remote.FinalReportWebRemote");
    }
    
    public static QASummaryReportRemote getQASummaryReport() {
        return (QASummaryReportRemote)lookup("QASummaryReportBean!org.openelis.remote.QASummaryReportRemote");
    } 
    
    public static RequestformReportRemote getRequestformReport() {
        return (RequestformReportRemote)lookup("RequestformReportBean!org.openelis.remote.RequestformReportRemote");
    } 
    
    public static SampleInhouseReportRemote getSampleInhouseReport() {
        return (SampleInhouseReportRemote)lookup("SampleInhouseReportBean!org.openelis.remote.SampleInhouseReportRemote");
    } 
    
    public static SampleLoginLabelReportRemote getSampleLoginLabelReport() {
        return (SampleLoginLabelReportRemote)lookup("SampleLoginLabelReportBean!org.openelis.remote.SampleLoginLabelReportRemote");
    } 
    
    public static SampleStatusReportRemote getSampleStatusReport() {
        return (SampleStatusReportRemote)lookup("SampleStatusReportBean!org.openelis.remote.SampleStatusReportRemote");
    }
    
    public static SDWISUnloadReportRemote getSDWISUnloadReport() {
        return (SDWISUnloadReportRemote)lookup("SDWISUnloadReportBean!org.openelis.remote.SDWISUnloadReportRemote");
    }
    
    public static ShippingReportRemote getShippingReport() {
        return (ShippingReportRemote)lookup("ShippingReportBean!org.openelis.remote.ShippingReportRemote");
    } 
    
    public static TestReportRemote getTestReport() {
        return (TestReportRemote)lookup("TestReportBean!org.openelis.remote.TestReportRemote");
    } 
    
    public static TurnaroundReportRemote getTurnaroundReport() {
        return (TurnaroundReportRemote)lookup("TurnaroundReportBean!org.openelis.remote.TurnaroundReportRemote");
    } 
    
    public static VerificationReportRemote getVerificationReport() {
        return (VerificationReportRemote)lookup("VerificationReportBean!org.openelis.remote.VerificationReportRemote");
    } 
    
    public static VolumeReportRemote getVolumeReport() {
        return (VolumeReportRemote)lookup("VolumeReportBean!org.openelis.remote.VolumeReportRemote");
    } 
    
    public static ResultManagerRemote getResultManager() {
        return (ResultManagerRemote)lookup("ResultManagerBean!org.openelis.remote.ResultManagerRemote");
    }
    
    public static SampleRemote getSample() {
        return (SampleRemote)lookup("SampleBean!org.openelis.remote.SampleRemote");
    }

    public static SampleManagerRemote getSampleManager() {
        return (SampleManagerRemote)lookup("SampleManagerBean!org.openelis.remote.SampleManagerRemote");
    }
    
    public static SampleManager1Remote getSampleManager1() {
        return (SampleManager1Remote)lookup("SampleManager1Bean!org.openelis.remote.SampleManager1Remote");
    }

    public static SampleQAEventManagerRemote getSampleQAEventManager() {
        return (SampleQAEventManagerRemote)lookup("SampleQAEventManagerBean!org.openelis.remote.SampleQAEventManagerRemote");
    }

    public static SystemVariableRemote getSystemVariable() {
        return (SystemVariableRemote)lookup("SystemVariableBean!org.openelis.remote.SystemVariableRemote");
    }
    
    public static SampleTrackingRemote getSampleTracking() {
        return (SampleTrackingRemote)lookup("SampleTrackingBean!org.openelis.remote.SampleTrackingRemote");
    }
    
    public static ScriptletRemote getScriptlet() {
        return (ScriptletRemote)lookup("ScriptletBean!org.openelis.remote.ScriptletRemote");
    }
    
    public static SectionRemote getSection() {
        return (SectionRemote)lookup("SectionBean!org.openelis.remote.SectionRemote");
    }
    
    public static SectionManagerRemote getSectionManager() {
        return (SectionManagerRemote)lookup("SectionManagerBean!org.openelis.remote.SectionManagerRemote");
    }
    
    public static ShippingRemote getShipping() {
        return (ShippingRemote)lookup("ShippingBean!org.openelis.remote.ShippingRemote"); 
    }
    
    public static ShippingManagerRemote getShippingManager() {
        return (ShippingManagerRemote)lookup("ShippingManagerBean!org.openelis.remote.ShippingManagerRemote"); 
    }
    
    public static StandardNoteRemote getStandardNote() {
        return (StandardNoteRemote)lookup("StandardNoteBean!org.openelis.remote.StandardNoteRemote");
    }
    
    public static StorageManagerRemote getStorageManager() {
        return (StorageManagerRemote)lookup("StorageManagerBean!org.openelis.remote.StorageManagerRemote");
    }

    public static StorageLocationRemote getStorageLocation() {
        return (StorageLocationRemote)lookup("StorageLocationBean!org.openelis.remote.StorageLocationRemote");
    }
    
    public static StorageLocationManagerRemote getStorageLocationManager() {
        return (StorageLocationManagerRemote)lookup("StorageLocationManagerBean!org.openelis.remote.StorageLocationManagerRemote");
    }
    
    public static StorageUnitRemote getStorageUnit() {
        return (StorageUnitRemote)lookup("StorageUnitBean!org.openelis.remote.StorageUnitRemote");
    }
    
    public static TestRemote getTest() {
        return (TestRemote)lookup("TestBean!org.openelis.remote.TestRemote");
    }

    public static TestManagerRemote getTestManager() {
        return (TestManagerRemote)lookup("TestManagerBean!org.openelis.remote.TestManagerRemote");
    }
    
    public static TestTrailerRemote getTestTrailer() {
        return (TestTrailerRemote)EJBFactory.lookup("TestTrailerBean!org.openelis.remote.TestTrailerRemote");
    }
    
    public static TestTypeOfSampleRemote getTestTypeOfSample() {
        return (TestTypeOfSampleRemote)EJBFactory.lookup("TestTypeOfSampleBean!org.openelis.remote.TestTypeOfSampleRemote");
    }
    
    public static ToDoRemote getToDo() {
        return (ToDoRemote)EJBFactory.lookup("ToDoBean!org.openelis.remote.ToDoRemote");
    } 
    
    public static WorksheetRemote getWorksheet() {
        return (WorksheetRemote)lookup("WorksheetBean!org.openelis.remote.WorksheetRemote");
    }

    public static WorksheetManagerRemote getWorksheetManager() {
        return (WorksheetManagerRemote)lookup("WorksheetManagerBean!org.openelis.remote.WorksheetManagerRemote");
    }
    
    public static WorksheetCompletionRemote getWorksheetCompletion() {
        return (WorksheetCompletionRemote)lookup("WorksheetCompletionBean!org.openelis.remote.WorksheetCompletionRemote");
    }
    
    public static WorksheetCreationRemote getWorksheetCreation() {
        return (WorksheetCreationRemote)lookup("WorksheetCreationBean!org.openelis.remote.WorksheetCreationRemote");
    }
    
    public static PreferencesRemote getPreferences() {
    	return (PreferencesRemote)lookup("PreferencesManagerBean!org.openelis.remote.PreferencesRemote");
    }
    
    public static SectionCacheRemote getSectionCache() {
    	return (SectionCacheRemote)lookup("SectionCacheBean!org.openelis.remote.SectionCacheRemote");
    }
    
    public static DictionaryCacheRemote getDictionaryCache() {
        return (DictionaryCacheRemote)lookup("DictionaryCacheBean!org.openelis.remote.DictionaryCacheRemote");
    }    
    
    public static CategoryCacheRemote getCategoryCache() {
        return (CategoryCacheRemote)lookup("CategoryCacheBean!org.openelis.remote.CategoryCacheRemote");
    }
    
    public static InventoryItemCacheRemote getInventoryItemCache() {
    	return (InventoryItemCacheRemote)lookup("InventoryItemCacheBean!org.openelis.remote.InventoryItemCacheRemote");
    }
    
    public static DataViewRemote getDataView() {
    	return (DataViewRemote)lookup("DataViewBean!org.openelis.remote.DataViewRemote");
    }
    
    public static CronRemote getCron() {
    	return (CronRemote)lookup("CronBean!org.openelis.remote.CronRemote");
    }
    
    public static QcChartReportRemote getQcChart() {
        return (QcChartReportRemote)lookup("QcChartReportBean!org.openelis.remote.QcChartReportRemote");
    }
    
    public static PrinterCacheRemote getPrinterCache() {
    	return (PrinterCacheRemote)lookup("PrinterCacheBean!org.openelis.remote.PrinterCacheRemote");
    }
    
    public static ToDoAnalyteReportRemote getToDoAnalyteReport() {
        return (ToDoAnalyteReportRemote)lookup("ToDoAnalyteReportBean!org.openelis.remote.ToDoAnalyteReportRemote");
    }
    
    public static TurnaroundStatisticReportRemote getTurnaroundStatisticReport() {
        return (TurnaroundStatisticReportRemote)lookup("TurnaroundStatisticReportBean!org.openelis.remote.TurnaroundStatisticReportRemote");
    }
    
    public static HoldRefuseOrganizationReportRemote getHoldRefuseOrganizationReport() {
        return (HoldRefuseOrganizationReportRemote)lookup("HoldRefuseOrganizationReportBean!org.openelis.remote.HoldRefuseOrganizationReportRemote");
    }
          
    public static Object lookup(String bean) {
    	Object object;
    	InitialContext c;
    	Properties p;
    	
    	try {
    		p = (Properties)SessionManager.getSession().getAttribute("jndiProps");
    		if (p == null) {
    			log.severe("Failed to get user properties for thread id " + Thread.currentThread());
    			return null;
    		}

    		c = new InitialContext(p);
    		if (!bean.startsWith("/"))
                bean = "/openelis/openelis.jar/" + bean;
    		object = c.lookup(bean);
    	} catch (Exception e) {
    		log.severe("Failed to lookup "+ bean +" for thread id "+ Thread.currentThread()+": " +
    				e.getMessage());
    		e.printStackTrace();
    		object = null;
    	}
    	
    	return object;
    }
}