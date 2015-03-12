package org.openelis.constants;

/**
 * Interface to represent the messages contained in resource bundle:
<<<<<<< .working
 * 	/home/tschmidt/luna/wildfly/OpenELIS-Common/bin/org/openelis/constants/OpenELISConstants.properties'.
=======
 * 	/home/akampoow/workspace/OpenELIS-Common/bin/org/openelis/constants/OpenELISConstants.properties'.
>>>>>>> .merge-right.r9006
 */
public interface OpenELISConstants extends com.google.gwt.i18n.client.Messages {
  
  /**
   * Translated "QA Event".
   * 
   * @return translated "QA Event"
  
   */
  @DefaultMessage("QA Event")
  @Key("QAEvent")
  String QAEvent();

  /**
   * Translated "Define quality assurance indicators that can be assigned to a sample/analysis.".
   * 
   * @return translated "Define quality assurance indicators that can be assigned to a sample/analysis."
  
   */
  @DefaultMessage("Define quality assurance indicators that can be assigned to a sample/analysis.")
  @Key("QAEventDescription")
  String QAEventDescription();

  /**
   * Translated "QAEvent Report".
   * 
   * @return translated "QAEvent Report"
  
   */
  @DefaultMessage("QAEvent Report")
  @Key("QAEventReport")
  String QAEventReport();

  /**
   * Translated "QA Summary Report".
   * 
   * @return translated "QA Summary Report"
  
   */
  @DefaultMessage("QA Summary Report")
  @Key("QASummaryReport")
  String QASummaryReport();

  /**
   * Translated "QC".
   * 
   * @return translated "QC"
  
   */
  @DefaultMessage("QC")
  @Key("QC")
  String QC();

  /**
   * Translated "Define quality control items.".
   * 
   * @return translated "Define quality control items."
  
   */
  @DefaultMessage("Define quality control items.")
  @Key("QCDescription")
  String QCDescription();

  /**
   * Translated "QC Lookup".
   * 
   * @return translated "QC Lookup"
  
   */
  @DefaultMessage("QC Lookup")
  @Key("QCLookup")
  String QCLookup();

  /**
   * Translated "Test Request Forms".
   * 
   * @return translated "Test Request Forms"
  
   */
  @DefaultMessage("Test Request Forms")
  @Key("TRFReport")
  String TRFReport();

  /**
   * Translated "For".
   * 
   * @return translated "For"
  
   */
  @DefaultMessage("For")
  @Key("_for")
  String _for();

  /**
   * Translated "Package".
   * 
   * @return translated "Package"
  
   */
  @DefaultMessage("Package")
  @Key("_package")
  String _package();

  /**
   * Translated "Abbreviation".
   * 
   * @return translated "Abbreviation"
  
   */
  @DefaultMessage("Abbreviation")
  @Key("abbr")
  String abbr();

  /**
   * Translated "Abort".
   * 
   * @return translated "Abort"
  
   */
  @DefaultMessage("Abort")
  @Key("abort")
  String abort();

  /**
   * Translated "A shipping record will not be created for these orders if you abort at this stage and you will not be able to create one later. Are you sure you want to abort?".
   * 
   * @return translated "A shipping record will not be created for these orders if you abort at this stage and you will not be able to create one later. Are you sure you want to abort?"
  
   */
  @DefaultMessage("A shipping record will not be created for these orders if you abort at this stage and you will not be able to create one later. Are you sure you want to abort?")
  @Key("abortNotCreateShippingRecord")
  String abortNotCreateShippingRecord();

  /**
   * Translated "All of your changes will be lost if you abort. Are you sure?".
   * 
   * @return translated "All of your changes will be lost if you abort. Are you sure?"
  
   */
  @DefaultMessage("All of your changes will be lost if you abort. Are you sure?")
  @Key("abortWarning")
  String abortWarning();

  /**
   * Translated "Acc #".
   * 
   * @return translated "Acc #"
  
   */
  @DefaultMessage("Acc #")
  @Key("accNum")
  String accNum();

  /**
   * Translated "Accession #".
   * 
   * @return translated "Accession #"
  
   */
  @DefaultMessage("Accession #")
  @Key("accessionNum")
  String accessionNum();

  /**
   * Translated "Invalid Accession Number: Number is already entered into the system".
   * 
   * @return translated "Invalid Accession Number: Number is already entered into the system"
  
   */
  @DefaultMessage("Invalid Accession Number: Number is already entered into the system")
  @Key("accessionNumberDuplicate")
  String accessionNumberDuplicate();

  /**
   * Translated "Are you sure you want to change this sample''s accession number?".
   * 
   * @return translated "Are you sure you want to change this sample''s accession number?"
  
   */
  @DefaultMessage("Are you sure you want to change this sample''s accession number?")
  @Key("accessionNumberEditConfirm")
  String accessionNumberEditConfirm();

  /**
   * Translated "Invalid Accession Number: Number not in use yet".
   * 
   * @return translated "Invalid Accession Number: Number not in use yet"
  
   */
  @DefaultMessage("Invalid Accession Number: Number not in use yet")
  @Key("accessionNumberNotInUse")
  String accessionNumberNotInUse();

  /**
   * Translated "Invalid Accession Number : Number must be greater than zero  ".
   * 
   * @return translated "Invalid Accession Number : Number must be greater than zero  "
  
   */
  @DefaultMessage("Invalid Accession Number : Number must be greater than zero  ")
  @Key("accessionNumberNotPositiveException")
  String accessionNumberNotPositiveException();

  /**
   * Translated "Active".
   * 
   * @return translated "Active"
  
   */
  @DefaultMessage("Active")
  @Key("active")
  String active();

  /**
   * Translated "Activity ".
   * 
   * @return translated "Activity "
  
   */
  @DefaultMessage("Activity ")
  @Key("activity")
  String activity();

  /**
   * Translated "Add".
   * 
   * @return translated "Add"
  
   */
  @DefaultMessage("Add")
  @Key("add")
  String add();

  /**
   * Translated "Add aborted".
   * 
   * @return translated "Add aborted"
  
   */
  @DefaultMessage("Add aborted")
  @Key("addAborted")
  String addAborted();

  /**
   * Translated "Add Analysis".
   * 
   * @return translated "Add Analysis"
  
   */
  @DefaultMessage("Add Analysis")
  @Key("addAnalysis")
  String addAnalysis();

  /**
   * Translated "+ Analyte".
   * 
   * @return translated "+ Analyte"
  
   */
  @DefaultMessage("+ Analyte")
  @Key("addAnalyte")
  String addAnalyte();

  /**
   * Translated "Add Analyte After".
   * 
   * @return translated "Add Analyte After"
  
   */
  @DefaultMessage("Add Analyte After")
  @Key("addAnalyteAfter")
  String addAnalyteAfter();

  /**
   * Translated "Add Analyte Before".
   * 
   * @return translated "Add Analyte Before"
  
   */
  @DefaultMessage("Add Analyte Before")
  @Key("addAnalyteBefore")
  String addAnalyteBefore();

  /**
   * Translated "Add Before".
   * 
   * @return translated "Add Before"
  
   */
  @DefaultMessage("Add Before")
  @Key("addBefore")
  String addBefore();

  /**
   * Translated "+ Column".
   * 
   * @return translated "+ Column"
  
   */
  @DefaultMessage("+ Column")
  @Key("addColumn")
  String addColumn();

  /**
   * Translated "Add Group".
   * 
   * @return translated "Add Group"
  
   */
  @DefaultMessage("Add Group")
  @Key("addGroup")
  String addGroup();

  /**
   * Translated "Add Group After".
   * 
   * @return translated "Add Group After"
  
   */
  @DefaultMessage("Add Group After")
  @Key("addGroupAfter")
  String addGroupAfter();

  /**
   * Translated "Add Group Before".
   * 
   * @return translated "Add Group Before"
  
   */
  @DefaultMessage("Add Group Before")
  @Key("addGroupBefore")
  String addGroupBefore();

  /**
   * Translated "+ Header".
   * 
   * @return translated "+ Header"
  
   */
  @DefaultMessage("+ Header")
  @Key("addHeader")
  String addHeader();

  /**
   * Translated "Add Item".
   * 
   * @return translated "Add Item"
  
   */
  @DefaultMessage("Add Item")
  @Key("addItem")
  String addItem();

  /**
   * Translated "Add Location".
   * 
   * @return translated "Add Location"
  
   */
  @DefaultMessage("Add Location")
  @Key("addLocation")
  String addLocation();

  /**
   * Translated "Add Note".
   * 
   * @return translated "Add Note"
  
   */
  @DefaultMessage("Add Note")
  @Key("addNote")
  String addNote();

  /**
   * Translated "Add or Cancel".
   * 
   * @return translated "Add or Cancel"
  
   */
  @DefaultMessage("Add or Cancel")
  @Key("addOrCancel")
  String addOrCancel();

  /**
   * Translated "Add analyses to samples or cancel existing analyses.".
   * 
   * @return translated "Add analyses to samples or cancel existing analyses."
  
   */
  @DefaultMessage("Add analyses to samples or cancel existing analyses.")
  @Key("addOrCancelDescription")
  String addOrCancelDescription();

  /**
   * Translated "Add Row".
   * 
   * @return translated "Add Row"
  
   */
  @DefaultMessage("Add Row")
  @Key("addRow")
  String addRow();

  /**
   * Translated "+ Row".
   * 
   * @return translated "+ Row"
  
   */
  @DefaultMessage("+ Row")
  @Key("addRowTest")
  String addRowTest();

  /**
   * Translated "Add Test".
   * 
   * @return translated "Add Test"
  
   */
  @DefaultMessage("Add Test")
  @Key("addTest")
  String addTest();

  /**
   * Translated "Add To Existing".
   * 
   * @return translated "Add To Existing"
  
   */
  @DefaultMessage("Add To Existing")
  @Key("addToExisting")
  String addToExisting();

  /**
   * Translated "Adding...".
   * 
   * @return translated "Adding..."
  
   */
  @DefaultMessage("Adding...")
  @Key("adding")
  String adding();

  /**
   * Translated "Adding...Complete".
   * 
   * @return translated "Adding...Complete"
  
   */
  @DefaultMessage("Adding...Complete")
  @Key("addingComplete")
  String addingComplete();

  /**
   * Translated "Adding Failed. Make corrections and try again or Abort".
   * 
   * @return translated "Adding Failed. Make corrections and try again or Abort"
  
   */
  @DefaultMessage("Adding Failed. Make corrections and try again or Abort")
  @Key("addingFailed")
  String addingFailed();

  /**
   * Translated "Additional Info".
   * 
   * @return translated "Additional Info"
  
   */
  @DefaultMessage("Additional Info")
  @Key("additionalInfo")
  String additionalInfo();

  /**
   * Translated "Additional Label".
   * 
   * @return translated "Additional Label"
  
   */
  @DefaultMessage("Additional Label")
  @Key("additionalLabel")
  String additionalLabel();

  /**
   * Translated "Addr1".
   * 
   * @return translated "Addr1"
  
   */
  @DefaultMessage("Addr1")
  @Key("addr1")
  String addr1();

  /**
   * Translated "Addr2".
   * 
   * @return translated "Addr2"
  
   */
  @DefaultMessage("Addr2")
  @Key("addr2")
  String addr2();

  /**
   * Translated "Address".
   * 
   * @return translated "Address"
  
   */
  @DefaultMessage("Address")
  @Key("address")
  String address();

  /**
   * Translated "Address".
   * 
   * @return translated "Address"
  
   */
  @DefaultMessage("Address")
  @Key("address.address")
  String address_address();

  /**
   * Translated "Apt/Suite #".
   * 
   * @return translated "Apt/Suite #"
  
   */
  @DefaultMessage("Apt/Suite #")
  @Key("address.aptSuite")
  String address_aptSuite();

  /**
   * Translated "City".
   * 
   * @return translated "City"
  
   */
  @DefaultMessage("City")
  @Key("address.city")
  String address_city();

  /**
   * Translated "City, State, Zip".
   * 
   * @return translated "City, State, Zip"
  
   */
  @DefaultMessage("City, State, Zip")
  @Key("address.cityStateZip")
  String address_cityStateZip();

  /**
   * Translated "Country".
   * 
   * @return translated "Country"
  
   */
  @DefaultMessage("Country")
  @Key("address.country")
  String address_country();

  /**
   * Translated "Phone #".
   * 
   * @return translated "Phone #"
  
   */
  @DefaultMessage("Phone #")
  @Key("address.phone")
  String address_phone();

  /**
   * Translated "St".
   * 
   * @return translated "St"
  
   */
  @DefaultMessage("St")
  @Key("address.st")
  String address_st();

  /**
   * Translated "State".
   * 
   * @return translated "State"
  
   */
  @DefaultMessage("State")
  @Key("address.state")
  String address_state();

  /**
   * Translated "Street".
   * 
   * @return translated "Street"
  
   */
  @DefaultMessage("Street")
  @Key("address.street")
  String address_street();

  /**
   * Translated "Zip Code".
   * 
   * @return translated "Zip Code"
  
   */
  @DefaultMessage("Zip Code")
  @Key("address.zipcode")
  String address_zipcode();

  /**
   * Translated "Adj Date".
   * 
   * @return translated "Adj Date"
  
   */
  @DefaultMessage("Adj Date")
  @Key("adjDate")
  String adjDate();

  /**
   * Translated "Adj Quan".
   * 
   * @return translated "Adj Quan"
  
   */
  @DefaultMessage("Adj Quan")
  @Key("adjQuan")
  String adjQuan();

  /**
   * Translated "Adjustment #".
   * 
   * @return translated "Adjustment #"
  
   */
  @DefaultMessage("Adjustment #")
  @Key("adjustmentNum")
  String adjustmentNum();

  /**
   * Translated "Air Quality Export".
   * 
   * @return translated "Air Quality Export"
  
   */
  @DefaultMessage("Air Quality Export")
  @Key("airQuality.airQualityExport")
  String airQuality_airQualityExport();

  /**
   * Translated "You must specify Action for this report".
   * 
   * @return translated "You must specify Action for this report"
  
   */
  @DefaultMessage("You must specify Action for this report")
  @Key("airQuality.noActionException")
  String airQuality_noActionException();

  /**
   * Translated "You must specify From Date and To Date or accession number for this report".
   * 
   * @return translated "You must specify From Date and To Date or accession number for this report"
  
   */
  @DefaultMessage("You must specify From Date and To Date or accession number for this report")
  @Key("airQuality.noDataException")
  String airQuality_noDataException();

  /**
   * Translated "You may only specify From Date and To Date or accession number for this report".
   * 
   * @return translated "You may only specify From Date and To Date or accession number for this report"
  
   */
  @DefaultMessage("You may only specify From Date and To Date or accession number for this report")
  @Key("airQuality.tooMuchDataException")
  String airQuality_tooMuchDataException();

  /**
   * Translated "Alias".
   * 
   * @return translated "Alias"
  
   */
  @DefaultMessage("Alias")
  @Key("alias")
  String alias();

  /**
   * Translated "All Categories ".
   * 
   * @return translated "All Categories "
  
   */
  @DefaultMessage("All Categories ")
  @Key("allCategories")
  String allCategories();

  /**
   * Translated "All items do not have locations specified".
   * 
   * @return translated "All items do not have locations specified"
  
   */
  @DefaultMessage("All items do not have locations specified")
  @Key("allItemsDontHaveLocation")
  String allItemsDontHaveLocation();

  /**
   * Translated "All items must be from the same store".
   * 
   * @return translated "All items must be from the same store"
  
   */
  @DefaultMessage("All items must be from the same store")
  @Key("allItemsSameStoreException")
  String allItemsSameStoreException();

  /**
   * Translated "If one of the sections is set as \"Default\", then all the others must be set to blank.".
   * 
   * @return translated "If one of the sections is set as \"Default\", then all the others must be set to blank."
  
   */
  @DefaultMessage("If one of the sections is set as \"Default\", then all the others must be set to blank.")
  @Key("allSectBlankIfDefException")
  String allSectBlankIfDefException();

  /**
   * Translated "All section cannot be set to the blank option. At least one of them must be set as the default one, if the rest are set to blank.".
   * 
   * @return translated "All section cannot be set to the blank option. At least one of them must be set as the default one, if the rest are set to blank."
  
   */
  @DefaultMessage("All section cannot be set to the blank option. At least one of them must be set as the default one, if the rest are set to blank.")
  @Key("allSectCantBeBlankException")
  String allSectCantBeBlankException();

  /**
   * Translated "If one of the sections is set as \"Match User Location\", then all the others must be set to this option.".
   * 
   * @return translated "If one of the sections is set as \"Match User Location\", then all the others must be set to this option."
  
   */
  @DefaultMessage("If one of the sections is set as \"Match User Location\", then all the others must be set to this option.")
  @Key("allSectMatchFlagException")
  String allSectMatchFlagException();

  /**
   * Translated "All Tests".
   * 
   * @return translated "All Tests"
  
   */
  @DefaultMessage("All Tests")
  @Key("allTests")
  String allTests();

  /**
   * Translated "The \"Analyte\" and \"Type\" fields are required for each analyte".
   * 
   * @return translated "The \"Analyte\" and \"Type\" fields are required for each analyte"
  
   */
  @DefaultMessage("The \"Analyte\" and \"Type\" fields are required for each analyte")
  @Key("anaTypeAndNameRequired")
  String anaTypeAndNameRequired();

  /**
   * Translated "Analyses".
   * 
   * @return translated "Analyses"
  
   */
  @DefaultMessage("Analyses")
  @Key("analyses")
  String analyses();

  /**
   * Translated "Analysis".
   * 
   * @return translated "Analysis"
  
   */
  @DefaultMessage("Analysis")
  @Key("analysis")
  String analysis();

  /**
   * Translated "Action".
   * 
   * @return translated "Action"
  
   */
  @DefaultMessage("Action")
  @Key("analysis.action")
  String analysis_action();

  /**
   * Translated "Analysis".
   * 
   * @return translated "Analysis"
  
   */
  @DefaultMessage("Analysis")
  @Key("analysis.analysis")
  String analysis_analysis();

  /**
   * Translated "Cancel Analysis?".
   * 
   * @return translated "Cancel Analysis?"
  
   */
  @DefaultMessage("Cancel Analysis?")
  @Key("analysis.cancelCaption")
  String analysis_cancelCaption();

  /**
   * Translated "You may not remove a committed analysis row. However, you can change the status to ''Cancelled''. Would you like to change the status to ''Cancelled''?".
   * 
   * @return translated "You may not remove a committed analysis row. However, you can change the status to ''Cancelled''. Would you like to change the status to ''Cancelled''?"
  
   */
  @DefaultMessage("You may not remove a committed analysis row. However, you can change the status to ''Cancelled''. Would you like to change the status to ''Cancelled''?")
  @Key("analysis.cancelMessage")
  String analysis_cancelMessage();

  /**
   * Translated "Analysis is cancelled. You cannot add QA events.".
   * 
   * @return translated "Analysis is cancelled. You cannot add QA events."
  
   */
  @DefaultMessage("Analysis is cancelled. You cannot add QA events.")
  @Key("analysis.cantAddQACancelled")
  String analysis_cantAddQACancelled();

  /**
   * Translated "Accession # {0,number,#0}: Cannot cancel {1}, {2} - It is the prep analysis for the released analysis {3}, {4}".
   * 
   * @param arg0 "{0,number,#0}"
   * @param arg1 "{1}"
   * @param arg2 "{2}"
   * @param arg3 "{3}"
   * @param arg4 "{4}"
   * @return translated "Accession # {0,number,#0}: Cannot cancel {1}, {2} - It is the prep analysis for the released analysis {3}, {4}"
  
   */
  @DefaultMessage("Accession # {0,number,#0}: Cannot cancel {1}, {2} - It is the prep analysis for the released analysis {3}, {4}")
  @Key("analysis.cantCancelPrepWithReleasedTest")
  String analysis_cantCancelPrepWithReleasedTest(Integer arg0,  String arg1,  String arg2,  String arg3,  String arg4);

  /**
   * Translated "Accession # {0,number,#0}: Cannot cancel {1}, {2} - It does not exist in the system ".
   * 
   * @param arg0 "{0,number,#0}"
   * @param arg1 "{1}"
   * @param arg2 "{2}"
   * @return translated "Accession # {0,number,#0}: Cannot cancel {1}, {2} - It does not exist in the system "
  
   */
  @DefaultMessage("Accession # {0,number,#0}: Cannot cancel {1}, {2} - It does not exist in the system ")
  @Key("analysis.cantCancelUncommitedException")
  String analysis_cantCancelUncommitedException(Integer arg0,  String arg1,  String arg2);

  /**
   * Translated "Accession # {0,number,#0}: Cannot change the prep analysis of {1}, {2} - It is in {3} status".
   * 
   * @param arg0 "{0,number,#0}"
   * @param arg1 "{1}"
   * @param arg2 "{2}"
   * @param arg3 "{3}"
   * @return translated "Accession # {0,number,#0}: Cannot change the prep analysis of {1}, {2} - It is in {3} status"
  
   */
  @DefaultMessage("Accession # {0,number,#0}: Cannot change the prep analysis of {1}, {2} - It is in {3} status")
  @Key("analysis.cantChangePrepException")
  String analysis_cantChangePrepException(Integer arg0,  String arg1,  String arg2,  String arg3);

  /**
   * Translated "Accession # {0,number,#0}: Cannot change the status of {1}, {2} from {3} to {4}".
   * 
   * @param arg0 "{0,number,#0}"
   * @param arg1 "{1}"
   * @param arg2 "{2}"
   * @param arg3 "{3}"
   * @param arg4 "{4}"
   * @return translated "Accession # {0,number,#0}: Cannot change the status of {1}, {2} from {3} to {4}"
  
   */
  @DefaultMessage("Accession # {0,number,#0}: Cannot change the status of {1}, {2} from {3} to {4}")
  @Key("analysis.cantChangeStatusException")
  String analysis_cantChangeStatusException(Integer arg0,  String arg1,  String arg2,  String arg3,  String arg4);

  /**
   * Translated "Accession # {0,number,#0}: Cannot complete {1}, {2}  ".
   * 
   * @param arg0 "{0,number,#0}"
   * @param arg1 "{1}"
   * @param arg2 "{2}"
   * @return translated "Accession # {0,number,#0}: Cannot complete {1}, {2}  "
  
   */
  @DefaultMessage("Accession # {0,number,#0}: Cannot complete {1}, {2}  ")
  @Key("analysis.cantCompleteException")
  String analysis_cantCompleteException(Integer arg0,  String arg1,  String arg2);

  /**
   * Translated "Cannot copy blank section".
   * 
   * @return translated "Cannot copy blank section"
  
   */
  @DefaultMessage("Cannot copy blank section")
  @Key("analysis.cantCopyBlankSect")
  String analysis_cantCopyBlankSect();

  /**
   * Translated "Accession # {0,number,#0}: Cannot release {1} : {2} - Sample is not verified".
   * 
   * @param arg0 "{0,number,#0}"
   * @param arg1 "{1}"
   * @param arg2 "{2}"
   * @return translated "Accession # {0,number,#0}: Cannot release {1} : {2} - Sample is not verified"
  
   */
  @DefaultMessage("Accession # {0,number,#0}: Cannot release {1} : {2} - Sample is not verified")
  @Key("analysis.cantReleaseSampleNotVerifiedException")
  String analysis_cantReleaseSampleNotVerifiedException(Integer arg0,  String arg1,  String arg2);

  /**
   * Translated "Accession # {0,number,#0}: Cannot remove {1}, {2} - It exists in the system ".
   * 
   * @param arg0 "{0,number,#0}"
   * @param arg1 "{1}"
   * @param arg2 "{2}"
   * @return translated "Accession # {0,number,#0}: Cannot remove {1}, {2} - It exists in the system "
  
   */
  @DefaultMessage("Accession # {0,number,#0}: Cannot remove {1}, {2} - It exists in the system ")
  @Key("analysis.cantRemoveCommitedException")
  String analysis_cantRemoveCommitedException(Integer arg0,  String arg1,  String arg2);

  /**
   * Translated "Accession # {0,number,#0}: Cannot remove {1}, {2} during add/update - It should be cancelled or removed separately".
   * 
   * @param arg0 "{0,number,#0}"
   * @param arg1 "{1}"
   * @param arg2 "{2}"
   * @return translated "Accession # {0,number,#0}: Cannot remove {1}, {2} during add/update - It should be cancelled or removed separately"
  
   */
  @DefaultMessage("Accession # {0,number,#0}: Cannot remove {1}, {2} during add/update - It should be cancelled or removed separately")
  @Key("analysis.cantRemoveInUpdateException")
  String analysis_cantRemoveInUpdateException(Integer arg0,  String arg1,  String arg2);

  /**
   * Translated "Analysis has been cancelled. You cannot remove QA events.".
   * 
   * @return translated "Analysis has been cancelled. You cannot remove QA events."
  
   */
  @DefaultMessage("Analysis has been cancelled. You cannot remove QA events.")
  @Key("analysis.cantRemoveQACancelled")
  String analysis_cantRemoveQACancelled();

  /**
   * Translated "Analysis has been released. You cannot remove non-internal QA events.".
   * 
   * @return translated "Analysis has been released. You cannot remove non-internal QA events."
  
   */
  @DefaultMessage("Analysis has been released. You cannot remove non-internal QA events.")
  @Key("analysis.cantRemoveQAReleased")
  String analysis_cantRemoveQAReleased();

  /**
   * Translated "Accession # {0,number,#0}: Cannot set the cancelled analysis {1}, {2} as the prep analysis of {3}, {4}".
   * 
   * @param arg0 "{0,number,#0}"
   * @param arg1 "{1}"
   * @param arg2 "{2}"
   * @param arg3 "{3}"
   * @param arg4 "{4}"
   * @return translated "Accession # {0,number,#0}: Cannot set the cancelled analysis {1}, {2} as the prep analysis of {3}, {4}"
  
   */
  @DefaultMessage("Accession # {0,number,#0}: Cannot set the cancelled analysis {1}, {2} as the prep analysis of {3}, {4}")
  @Key("analysis.cantSetAsPrepException")
  String analysis_cantSetAsPrepException(Integer arg0,  String arg1,  String arg2,  String arg3,  String arg4);

  /**
   * Translated "Accession # {0,number,#0}: Cannot unrelease {1}, {2} - Sample has a released {3}, {4} ".
   * 
   * @param arg0 "{0,number,#0}"
   * @param arg1 "{1}"
   * @param arg2 "{2}"
   * @param arg3 "{3}"
   * @param arg4 "{4}"
   * @return translated "Accession # {0,number,#0}: Cannot unrelease {1}, {2} - Sample has a released {3}, {4} "
  
   */
  @DefaultMessage("Accession # {0,number,#0}: Cannot unrelease {1}, {2} - Sample has a released {3}, {4} ")
  @Key("analysis.cantUnreleaseCarrierException")
  String analysis_cantUnreleaseCarrierException(Integer arg0,  String arg1,  String arg2,  String arg3,  String arg4);

  /**
   * Translated "An analysis can not be its own prep analysis".
   * 
   * @return translated "An analysis can not be its own prep analysis"
  
   */
  @DefaultMessage("An analysis can not be its own prep analysis")
  @Key("analysis.circularReference")
  String analysis_circularReference();

  /**
   * Translated "Accession # {0,number,#0}: Cannot release {1} : {2} - Status needs to be Completed".
   * 
   * @param arg0 "{0,number,#0}"
   * @param arg1 "{1}"
   * @param arg2 "{2}"
   * @return translated "Accession # {0,number,#0}: Cannot release {1} : {2} - Status needs to be Completed"
  
   */
  @DefaultMessage("Accession # {0,number,#0}: Cannot release {1} : {2} - Status needs to be Completed")
  @Key("analysis.completeStatusRequiredToRelease")
  String analysis_completeStatusRequiredToRelease(Integer arg0,  String arg1,  String arg2);

  /**
   * Translated "Completed".
   * 
   * @return translated "Completed"
  
   */
  @DefaultMessage("Completed")
  @Key("analysis.completed")
  String analysis_completed();

  /**
   * Translated "Accession # {0,number,#0}: Item {1,number,#0} - {2}, {3} - Completed date can''t be after released date".
   * 
   * @param arg0 "{0,number,#0}"
   * @param arg1 "{1,number,#0}"
   * @param arg2 "{2}"
   * @param arg3 "{3}"
   * @return translated "Accession # {0,number,#0}: Item {1,number,#0} - {2}, {3} - Completed date can''t be after released date"
  
   */
  @DefaultMessage("Accession # {0,number,#0}: Item {1,number,#0} - {2}, {3} - Completed date can''t be after released date")
  @Key("analysis.completedDateAfterReleasedException")
  String analysis_completedDateAfterReleasedException(Integer arg0,  Integer arg1,  String arg2,  String arg3);

  /**
   * Translated "Accession # {0,number,#0}: Item {1,number} - {2}, {3} - Completed date can''t be in the future".
   * 
   * @param arg0 "{0,number,#0}"
   * @param arg1 "{1,number}"
   * @param arg2 "{2}"
   * @param arg3 "{3}"
   * @return translated "Accession # {0,number,#0}: Item {1,number} - {2}, {3} - Completed date can''t be in the future"
  
   */
  @DefaultMessage("Accession # {0,number,#0}: Item {1,number} - {2}, {3} - Completed date can''t be in the future")
  @Key("analysis.completedDateInFutureException")
  String analysis_completedDateInFutureException(Integer arg0,  Integer arg1,  String arg2,  String arg3);

  /**
   * Translated "Accession # {0,number,#0}: Item {1,number,#0} - {2}, {3} - Unit ''{4}'' is inactive and was not duplicated".
   * 
   * @param arg0 "{0,number,#0}"
   * @param arg1 "{1,number,#0}"
   * @param arg2 "{2}"
   * @param arg3 "{3}"
   * @param arg4 "{4}"
   * @return translated "Accession # {0,number,#0}: Item {1,number,#0} - {2}, {3} - Unit ''{4}'' is inactive and was not duplicated"
  
   */
  @DefaultMessage("Accession # {0,number,#0}: Item {1,number,#0} - {2}, {3} - Unit ''{4}'' is inactive and was not duplicated")
  @Key("analysis.inactiveUnitWarning")
  String analysis_inactiveUnitWarning(Integer arg0,  Integer arg1,  String arg2,  String arg3,  String arg4);

  /**
   * Translated "Accession # {0,number,#0}: Insufficient privileges to add {1}, {2}".
   * 
   * @param arg0 "{0,number,#0}"
   * @param arg1 "{1}"
   * @param arg2 "{2}"
   * @return translated "Accession # {0,number,#0}: Insufficient privileges to add {1}, {2}"
  
   */
  @DefaultMessage("Accession # {0,number,#0}: Insufficient privileges to add {1}, {2}")
  @Key("analysis.insufficientPrivilegesAddTestWarning")
  String analysis_insufficientPrivilegesAddTestWarning(Integer arg0,  String arg1,  String arg2);

  /**
   * Translated "Accession # {0,number,#0}: Insufficient privileges to cancel {1}, {2}".
   * 
   * @param arg0 "{0,number,#0}"
   * @param arg1 "{1}"
   * @param arg2 "{2}"
   * @return translated "Accession # {0,number,#0}: Insufficient privileges to cancel {1}, {2}"
  
   */
  @DefaultMessage("Accession # {0,number,#0}: Insufficient privileges to cancel {1}, {2}")
  @Key("analysis.insufficientPrivilegesCancelException")
  String analysis_insufficientPrivilegesCancelException(Integer arg0,  String arg1,  String arg2);

  /**
   * Translated "Accession # {0,number,#0}: Insufficient privileges to complete {1}, {2}".
   * 
   * @param arg0 "{0,number,#0}"
   * @param arg1 "{1}"
   * @param arg2 "{2}"
   * @return translated "Accession # {0,number,#0}: Insufficient privileges to complete {1}, {2}"
  
   */
  @DefaultMessage("Accession # {0,number,#0}: Insufficient privileges to complete {1}, {2}")
  @Key("analysis.insufficientPrivilegesCompleteException")
  String analysis_insufficientPrivilegesCompleteException(Integer arg0,  String arg1,  String arg2);

  /**
   * Translated "Accession # {0,number,#0}: Insufficient privileges to initiate {1}, {2}".
   * 
   * @param arg0 "{0,number,#0}"
   * @param arg1 "{1}"
   * @param arg2 "{2}"
   * @return translated "Accession # {0,number,#0}: Insufficient privileges to initiate {1}, {2}"
  
   */
  @DefaultMessage("Accession # {0,number,#0}: Insufficient privileges to initiate {1}, {2}")
  @Key("analysis.insufficientPrivilegesInitiateException")
  String analysis_insufficientPrivilegesInitiateException(Integer arg0,  String arg1,  String arg2);

  /**
   * Translated "Accession # {0,number,#0}: Insufficient privileges to release {1}, {2}".
   * 
   * @param arg0 "{0,number,#0}"
   * @param arg1 "{1}"
   * @param arg2 "{2}"
   * @return translated "Accession # {0,number,#0}: Insufficient privileges to release {1}, {2}"
  
   */
  @DefaultMessage("Accession # {0,number,#0}: Insufficient privileges to release {1}, {2}")
  @Key("analysis.insufficientPrivilegesReleaseException")
  String analysis_insufficientPrivilegesReleaseException(Integer arg0,  String arg1,  String arg2);

  /**
   * Translated "Accession # {0,number,#0}: Insufficient privileges to unrelease {1}, {2}".
   * 
   * @param arg0 "{0,number,#0}"
   * @param arg1 "{1}"
   * @param arg2 "{2}"
   * @return translated "Accession # {0,number,#0}: Insufficient privileges to unrelease {1}, {2}"
  
   */
  @DefaultMessage("Accession # {0,number,#0}: Insufficient privileges to unrelease {1}, {2}")
  @Key("analysis.insufficientPrivilegesUnreleaseException")
  String analysis_insufficientPrivilegesUnreleaseException(Integer arg0,  String arg1,  String arg2);

  /**
   * Translated "Accession # {0,number,#0} : Cannot add analyte ''{1}'' to {2}, {3} at this position".
   * 
   * @param arg0 "{0,number,#0}"
   * @param arg1 "{1}"
   * @param arg2 "{2}"
   * @param arg3 "{3}"
   * @return translated "Accession # {0,number,#0} : Cannot add analyte ''{1}'' to {2}, {3} at this position"
  
   */
  @DefaultMessage("Accession # {0,number,#0} : Cannot add analyte ''{1}'' to {2}, {3} at this position")
  @Key("analysis.invalidPositionForAnalyteException")
  String analysis_invalidPositionForAnalyteException(Integer arg0,  String arg1,  String arg2,  String arg3);

  /**
   * Translated "Accession # {0,number,#0}: Cannot initiate {1}, {2} - It is in {3} status".
   * 
   * @param arg0 "{0,number,#0}"
   * @param arg1 "{1}"
   * @param arg2 "{2}"
   * @param arg3 "{3}"
   * @return translated "Accession # {0,number,#0}: Cannot initiate {1}, {2} - It is in {3} status"
  
   */
  @DefaultMessage("Accession # {0,number,#0}: Cannot initiate {1}, {2} - It is in {3} status")
  @Key("analysis.invalidStatusForInitiateException")
  String analysis_invalidStatusForInitiateException(Integer arg0,  String arg1,  String arg2,  String arg3);

  /**
   * Translated "Results may be lost".
   * 
   * @return translated "Results may be lost"
  
   */
  @DefaultMessage("Results may be lost")
  @Key("analysis.loseResultsCaption")
  String analysis_loseResultsCaption();

  /**
   * Translated "Changing the method may cause you to lose the results you have entered.  Are you sure you want to continue?".
   * 
   * @return translated "Changing the method may cause you to lose the results you have entered.  Are you sure you want to continue?"
  
   */
  @DefaultMessage("Changing the method may cause you to lose the results you have entered.  Are you sure you want to continue?")
  @Key("analysis.loseResultsWarning")
  String analysis_loseResultsWarning();

  /**
   * Translated "No Add permission for - {0} : {1}, {2}".
   * 
   * @param arg0 "{0}"
   * @param arg1 "{1}"
   * @param arg2 "{2}"
   * @return translated "No Add permission for - {0} : {1}, {2}"
  
   */
  @DefaultMessage("No Add permission for - {0} : {1}, {2}")
  @Key("analysis.noAssignPermission")
  String analysis_noAssignPermission(String arg0,  String arg1,  String arg2);

  /**
   * Translated "No cancel permission for - {0} : {1}, {2}".
   * 
   * @param arg0 "{0}"
   * @param arg1 "{1}"
   * @param arg2 "{2}"
   * @return translated "No cancel permission for - {0} : {1}, {2}"
  
   */
  @DefaultMessage("No cancel permission for - {0} : {1}, {2}")
  @Key("analysis.noCancelPermission")
  String analysis_noCancelPermission(String arg0,  String arg1,  String arg2);

  /**
   * Translated "No Complete permission for - {0} : {1}, {2}".
   * 
   * @param arg0 "{0}"
   * @param arg1 "{1}"
   * @param arg2 "{2}"
   * @return translated "No Complete permission for - {0} : {1}, {2}"
  
   */
  @DefaultMessage("No Complete permission for - {0} : {1}, {2}")
  @Key("analysis.noCompletePermission")
  String analysis_noCompletePermission(String arg0,  String arg1,  String arg2);

  /**
   * Translated "No Release permission for - {0} : {1}, {2}".
   * 
   * @param arg0 "{0}"
   * @param arg1 "{1}"
   * @param arg2 "{2}"
   * @return translated "No Release permission for - {0} : {1}, {2}"
  
   */
  @DefaultMessage("No Release permission for - {0} : {1}, {2}")
  @Key("analysis.noReleasePermission")
  String analysis_noReleasePermission(String arg0,  String arg1,  String arg2);

  /**
   * Translated "A QA event has been added that makes this analysis not billable".
   * 
   * @return translated "A QA event has been added that makes this analysis not billable"
  
   */
  @DefaultMessage("A QA event has been added that makes this analysis not billable")
  @Key("analysis.notBillable")
  String analysis_notBillable();

  /**
   * Translated "Accession # {0,number,#0}: Item {1,number,#0} - {2}, {3} - {4}".
   * 
   * @param arg0 "{0,number,#0}"
   * @param arg1 "{1,number,#0}"
   * @param arg2 "{2}"
   * @param arg3 "{3}"
   * @param arg4 "{4}"
   * @return translated "Accession # {0,number,#0}: Item {1,number,#0} - {2}, {3} - {4}"
  
   */
  @DefaultMessage("Accession # {0,number,#0}: Item {1,number,#0} - {2}, {3} - {4}")
  @Key("analysis.noteException")
  String analysis_noteException(Integer arg0,  Integer arg1,  String arg2,  String arg3,  String arg4);

  /**
   * Translated "Analysis Notes".
   * 
   * @return translated "Analysis Notes"
  
   */
  @DefaultMessage("Analysis Notes")
  @Key("analysis.notes")
  String analysis_notes();

  /**
   * Translated "Accession # {0,number,#0}: {1}, {2} - Partner and individual accession numbers cannot be the same".
   * 
   * @param arg0 "{0,number,#0}"
   * @param arg1 "{1}"
   * @param arg2 "{2}"
   * @return translated "Accession # {0,number,#0}: {1}, {2} - Partner and individual accession numbers cannot be the same"
  
   */
  @DefaultMessage("Accession # {0,number,#0}: {1}, {2} - Partner and individual accession numbers cannot be the same")
  @Key("analysis.partAccCantBeSameAsIndException")
  String analysis_partAccCantBeSameAsIndException(Integer arg0,  String arg1,  String arg2);

  /**
   * Translated "Preliminary".
   * 
   * @return translated "Preliminary"
  
   */
  @DefaultMessage("Preliminary")
  @Key("analysis.preliminary")
  String analysis_preliminary();

  /**
   * Translated "Printed".
   * 
   * @return translated "Printed"
  
   */
  @DefaultMessage("Printed")
  @Key("analysis.printed")
  String analysis_printed();

  /**
   * Translated "Analysis QA Events ".
   * 
   * @return translated "Analysis QA Events "
  
   */
  @DefaultMessage("Analysis QA Events ")
  @Key("analysis.qaEvents")
  String analysis_qaEvents();

  /**
   * Translated "Released".
   * 
   * @return translated "Released"
  
   */
  @DefaultMessage("Released")
  @Key("analysis.released")
  String analysis_released();

  /**
   * Translated "Analysis Released Date".
   * 
   * @return translated "Analysis Released Date"
  
   */
  @DefaultMessage("Analysis Released Date")
  @Key("analysis.releasedDate")
  String analysis_releasedDate();

  /**
   * Translated "Revision".
   * 
   * @return translated "Revision"
  
   */
  @DefaultMessage("Revision")
  @Key("analysis.revision")
  String analysis_revision();

  /**
   * Translated "Sample Prep".
   * 
   * @return translated "Sample Prep"
  
   */
  @DefaultMessage("Sample Prep")
  @Key("analysis.samplePrep")
  String analysis_samplePrep();

  /**
   * Translated "Accession # {0,number,#0}: Item {1,number,#0} - {2}, {3} - Section Missing".
   * 
   * @param arg0 "{0,number,#0}"
   * @param arg1 "{1,number,#0}"
   * @param arg2 "{2}"
   * @param arg3 "{3}"
   * @return translated "Accession # {0,number,#0}: Item {1,number,#0} - {2}, {3} - Section Missing"
  
   */
  @DefaultMessage("Accession # {0,number,#0}: Item {1,number,#0} - {2}, {3} - Section Missing")
  @Key("analysis.sectionIdMissingException")
  String analysis_sectionIdMissingException(Integer arg0,  Integer arg1,  String arg2,  String arg3);

  /**
   * Translated "Started".
   * 
   * @return translated "Started"
  
   */
  @DefaultMessage("Started")
  @Key("analysis.started")
  String analysis_started();

  /**
   * Translated "Accession # {0,number,#0}: Item {1,number,#0} - {2}, {3} - Started date can''t be after completed".
   * 
   * @param arg0 "{0,number,#0}"
   * @param arg1 "{1,number,#0}"
   * @param arg2 "{2}"
   * @param arg3 "{3}"
   * @return translated "Accession # {0,number,#0}: Item {1,number,#0} - {2}, {3} - Started date can''t be after completed"
  
   */
  @DefaultMessage("Accession # {0,number,#0}: Item {1,number,#0} - {2}, {3} - Started date can''t be after completed")
  @Key("analysis.startedDateAfterCompletedException")
  String analysis_startedDateAfterCompletedException(Integer arg0,  Integer arg1,  String arg2,  String arg3);

  /**
   * Translated "Accession # {0,number,#0}: Item {1,number,#0} - {2}, {3} - Started date is before available date".
   * 
   * @param arg0 "{0,number,#0}"
   * @param arg1 "{1,number,#0}"
   * @param arg2 "{2}"
   * @param arg3 "{3}"
   * @return translated "Accession # {0,number,#0}: Item {1,number,#0} - {2}, {3} - Started date is before available date"
  
   */
  @DefaultMessage("Accession # {0,number,#0}: Item {1,number,#0} - {2}, {3} - Started date is before available date")
  @Key("analysis.startedDateBeforeAvailableCaution")
  String analysis_startedDateBeforeAvailableCaution(Integer arg0,  Integer arg1,  String arg2,  String arg3);

  /**
   * Translated "Accession # {0,number,#0}: Item {1,number,#0} - {2}, {3} - Started date can''t be in the future".
   * 
   * @param arg0 "{0,number,#0}"
   * @param arg1 "{1,number,#0}"
   * @param arg2 "{2}"
   * @param arg3 "{3}"
   * @return translated "Accession # {0,number,#0}: Item {1,number,#0} - {2}, {3} - Started date can''t be in the future"
  
   */
  @DefaultMessage("Accession # {0,number,#0}: Item {1,number,#0} - {2}, {3} - Started date can''t be in the future")
  @Key("analysis.startedDateInFutureException")
  String analysis_startedDateInFutureException(Integer arg0,  Integer arg1,  String arg2,  String arg3);

  /**
   * Translated "Analysis Status".
   * 
   * @return translated "Analysis Status"
  
   */
  @DefaultMessage("Analysis Status")
  @Key("analysis.status")
  String analysis_status();

  /**
   * Translated "Accession # {0,number,#0}: Item {1,number,#0} - One or more tests are missing".
   * 
   * @param arg0 "{0,number,#0}"
   * @param arg1 "{1,number,#0}"
   * @return translated "Accession # {0,number,#0}: Item {1,number,#0} - One or more tests are missing"
  
   */
  @DefaultMessage("Accession # {0,number,#0}: Item {1,number,#0} - One or more tests are missing")
  @Key("analysis.testIdMissingException")
  String analysis_testIdMissingException(Integer arg0,  Integer arg1);

  /**
   * Translated "Unit of measure invalid for this sample type".
   * 
   * @return translated "Unit of measure invalid for this sample type"
  
   */
  @DefaultMessage("Unit of measure invalid for this sample type")
  @Key("analysis.unitInvalidForSampleType")
  String analysis_unitInvalidForSampleType();

  /**
   * Translated "Accession # {0,number,#0}: Item {1,number,#0} - {2}, {3} - Unit of measure invalid for sample type".
   * 
   * @param arg0 "{0,number,#0}"
   * @param arg1 "{1,number,#0}"
   * @param arg2 "{2}"
   * @param arg3 "{3}"
   * @return translated "Accession # {0,number,#0}: Item {1,number,#0} - {2}, {3} - Unit of measure invalid for sample type"
  
   */
  @DefaultMessage("Accession # {0,number,#0}: Item {1,number,#0} - {2}, {3} - Unit of measure invalid for sample type")
  @Key("analysis.unitInvalidWarning")
  String analysis_unitInvalidWarning(Integer arg0,  Integer arg1,  String arg2,  String arg3);

  /**
   * Translated "Accession # {0,number,#0}: Item {1,number,#0} - {2}, {3} - Unit of measure is required".
   * 
   * @param arg0 "{0,number,#0}"
   * @param arg1 "{1,number,#0}"
   * @param arg2 "{2}"
   * @param arg3 "{3}"
   * @return translated "Accession # {0,number,#0}: Item {1,number,#0} - {2}, {3} - Unit of measure is required"
  
   */
  @DefaultMessage("Accession # {0,number,#0}: Item {1,number,#0} - {2}, {3} - Unit of measure is required")
  @Key("analysis.unitRequiredException")
  String analysis_unitRequiredException(Integer arg0,  Integer arg1,  String arg2,  String arg3);

  /**
   * Translated "Accession # {0,number,#0}: {1}, {2} - Released {3}, {4} not found on this sample. Press 'Run Scriptlets' for latest data.".
   * 
   * @param arg0 "{0,number,#0}"
   * @param arg1 "{1}"
   * @param arg2 "{2}"
   * @param arg3 "{3}"
   * @param arg4 "{4}"
   * @return translated "Accession # {0,number,#0}: {1}, {2} - Released {3}, {4} not found on this sample. Press 'Run Scriptlets' for latest data."
  
   */
  @DefaultMessage("Accession # {0,number,#0}: {1}, {2} - Released {3}, {4} not found on this sample. Press 'Run Scriptlets' for latest data.")
  @Key("analysis.validIndCarrierNotFoundException")
  String analysis_validIndCarrierNotFoundException(Integer arg0,  String arg1,  String arg2,  String arg3,  String arg4);

  /**
   * Translated "Accession # {0,number,#0}: {1}, {2} - Released {3}, {4} not found on partner sample. Press 'Run Scriptlets' for latest data.".
   * 
   * @param arg0 "{0,number,#0}"
   * @param arg1 "{1}"
   * @param arg2 "{2}"
   * @param arg3 "{3}"
   * @param arg4 "{4}"
   * @return translated "Accession # {0,number,#0}: {1}, {2} - Released {3}, {4} not found on partner sample. Press 'Run Scriptlets' for latest data."
  
   */
  @DefaultMessage("Accession # {0,number,#0}: {1}, {2} - Released {3}, {4} not found on partner sample. Press 'Run Scriptlets' for latest data.")
  @Key("analysis.validPartCarrierNotFoundException")
  String analysis_validPartCarrierNotFoundException(Integer arg0,  String arg1,  String arg2,  String arg3,  String arg4);

  /**
   * Translated "Analysis has already been completed".
   * 
   * @return translated "Analysis has already been completed"
  
   */
  @DefaultMessage("Analysis has already been completed")
  @Key("analysisAlreadyComplete")
  String analysisAlreadyComplete();

  /**
   * Translated "Analysis has already been released".
   * 
   * @return translated "Analysis has already been released"
  
   */
  @DefaultMessage("Analysis has already been released")
  @Key("analysisAlreadyReleased")
  String analysisAlreadyReleased();

  /**
   * Translated "Analysis is cancelled or released; you may not edit it".
   * 
   * @return translated "Analysis is cancelled or released; you may not edit it"
  
   */
  @DefaultMessage("Analysis is cancelled or released; you may not edit it")
  @Key("analysisCancledOrReleased")
  String analysisCancledOrReleased();

  /**
   * Translated "An Ext Comment".
   * 
   * @return translated "An Ext Comment"
  
   */
  @DefaultMessage("An Ext Comment")
  @Key("analysisExtrnlCmnts")
  String analysisExtrnlCmnts();

  /**
   * Translated "Analysis Fields To Search By".
   * 
   * @return translated "Analysis Fields To Search By"
  
   */
  @DefaultMessage("Analysis Fields To Search By")
  @Key("analysisFieldSearchBy")
  String analysisFieldSearchBy();

  /**
   * Translated "Sample cannot be duplicated because one or more analyses have advanced beyond logged-in ".
   * 
   * @return translated "Sample cannot be duplicated because one or more analyses have advanced beyond logged-in "
  
   */
  @DefaultMessage("Sample cannot be duplicated because one or more analyses have advanced beyond logged-in ")
  @Key("analysisHasAdvancedStatusException")
  String analysisHasAdvancedStatusException();

  /**
   * Translated "Sample cannot be duplicated because ''{0}'' has been reflexed by another analysis  ".
   * 
   * @param arg0 "{0}"
   * @return translated "Sample cannot be duplicated because ''{0}'' has been reflexed by another analysis  "
  
   */
  @DefaultMessage("Sample cannot be duplicated because ''{0}'' has been reflexed by another analysis  ")
  @Key("analysisHasReflexAnalysesException")
  String analysisHasReflexAnalysesException(String arg0);

  /**
   * Translated "Analysis Id".
   * 
   * @return translated "Analysis Id"
  
   */
  @DefaultMessage("Analysis Id")
  @Key("analysisId")
  String analysisId();

  /**
   * Translated "An Int Comments ".
   * 
   * @return translated "An Int Comments "
  
   */
  @DefaultMessage("An Int Comments ")
  @Key("analysisIntrnlCmnts")
  String analysisIntrnlCmnts();

  /**
   * Translated "A QA event has been added that makes this analysis not billable".
   * 
   * @return translated "A QA event has been added that makes this analysis not billable"
  
   */
  @DefaultMessage("A QA event has been added that makes this analysis not billable")
  @Key("analysisNotBillable")
  String analysisNotBillable();

  /**
   * Translated "An analysis cannot be moved to its own item".
   * 
   * @return translated "An analysis cannot be moved to its own item"
  
   */
  @DefaultMessage("An analysis cannot be moved to its own item")
  @Key("analysisNotMovedToOwnItem")
  String analysisNotMovedToOwnItem();

  /**
   * Translated "Analysis Notes".
   * 
   * @return translated "Analysis Notes"
  
   */
  @DefaultMessage("Analysis Notes")
  @Key("analysisNotes")
  String analysisNotes();

  /**
   * Translated "Analysis/Prep Test & Method".
   * 
   * @return translated "Analysis/Prep Test & Method"
  
   */
  @DefaultMessage("Analysis/Prep Test & Method")
  @Key("analysisPrepTestMethod")
  String analysisPrepTestMethod();

  /**
   * Translated "Analysis QA Event".
   * 
   * @return translated "Analysis QA Event"
  
   */
  @DefaultMessage("Analysis QA Event")
  @Key("analysisQAEvent")
  String analysisQAEvent();

  /**
   * Translated "Accession # {0,number,#0}: Item {1,number,#0} - {2}, {3} - Invalid QA Event ''{4}''".
   * 
   * @param arg0 "{0,number,#0}"
   * @param arg1 "{1,number,#0}"
   * @param arg2 "{2}"
   * @param arg3 "{3}"
   * @param arg4 "{4}"
   * @return translated "Accession # {0,number,#0}: Item {1,number,#0} - {2}, {3} - Invalid QA Event ''{4}''"
  
   */
  @DefaultMessage("Accession # {0,number,#0}: Item {1,number,#0} - {2}, {3} - Invalid QA Event ''{4}''")
  @Key("analysisQAEvent.invalidQAException")
  String analysisQAEvent_invalidQAException(Integer arg0,  Integer arg1,  String arg2,  String arg3,  String arg4);

  /**
   * Translated "Accession # {0,number,#0}: Item {1,number,#0} - {2}, {3} - Type is required for QA Event ''{4}''".
   * 
   * @param arg0 "{0,number,#0}"
   * @param arg1 "{1,number,#0}"
   * @param arg2 "{2}"
   * @param arg3 "{3}"
   * @param arg4 "{4}"
   * @return translated "Accession # {0,number,#0}: Item {1,number,#0} - {2}, {3} - Type is required for QA Event ''{4}''"
  
   */
  @DefaultMessage("Accession # {0,number,#0}: Item {1,number,#0} - {2}, {3} - Type is required for QA Event ''{4}''")
  @Key("analysisQAEvent.typeRequiredException")
  String analysisQAEvent_typeRequiredException(Integer arg0,  Integer arg1,  String arg2,  String arg3,  String arg4);

  /**
   * Translated "Analysis Revision".
   * 
   * @return translated "Analysis Revision"
  
   */
  @DefaultMessage("Analysis Revision")
  @Key("analysisRevision")
  String analysisRevision();

  /**
   * Translated "{0} : {1} - Section Missing".
   * 
   * @param arg0 "{0}"
   * @param arg1 "{1}"
   * @return translated "{0} : {1} - Section Missing"
  
   */
  @DefaultMessage("{0} : {1} - Section Missing")
  @Key("analysisSectionIdMissing")
  String analysisSectionIdMissing(String arg0,  String arg1);

  /**
   * Translated "Analysis Status".
   * 
   * @return translated "Analysis Status"
  
   */
  @DefaultMessage("Analysis Status")
  @Key("analysisStatus")
  String analysisStatus();

  /**
   * Translated "Item {0} - One or more tests are missing".
   * 
   * @param arg0 "{0}"
   * @return translated "Item {0} - One or more tests are missing"
  
   */
  @DefaultMessage("Item {0} - One or more tests are missing")
  @Key("analysisTestIdMissing")
  String analysisTestIdMissing(String arg0);

  /**
   * Translated "{0} : {1} - Unit of measure Missing".
   * 
   * @param arg0 "{0}"
   * @param arg1 "{1}"
   * @return translated "{0} : {1} - Unit of measure Missing"
  
   */
  @DefaultMessage("{0} : {1} - Unit of measure Missing")
  @Key("analysisUnitIdMissing")
  String analysisUnitIdMissing(String arg0,  String arg1);

  /**
   * Translated "{0} : {1} - Unit of measure invalid for sample type".
   * 
   * @param arg0 "{0}"
   * @param arg1 "{1}"
   * @return translated "{0} : {1} - Unit of measure invalid for sample type"
  
   */
  @DefaultMessage("{0} : {1} - Unit of measure invalid for sample type")
  @Key("analysisUnitInvalid")
  String analysisUnitInvalid(String arg0,  String arg1);

  /**
   * Translated "{0} : {1} - Unit of measure is required".
   * 
   * @param arg0 "{0}"
   * @param arg1 "{1}"
   * @return translated "{0} : {1} - Unit of measure is required"
  
   */
  @DefaultMessage("{0} : {1} - Unit of measure is required")
  @Key("analysisUnitRequired")
  String analysisUnitRequired(String arg0,  String arg1);

  /**
   * Translated "You may not remove released user information".
   * 
   * @return translated "You may not remove released user information"
  
   */
  @DefaultMessage("You may not remove released user information")
  @Key("analysisUser.actionException")
  String analysisUser_actionException();

  /**
   * Translated "You may not remove released user information".
   * 
   * @return translated "You may not remove released user information"
  
   */
  @DefaultMessage("You may not remove released user information")
  @Key("analysisUserActionException")
  String analysisUserActionException();

  /**
   * Translated "Analyte".
   * 
   * @return translated "Analyte"
  
   */
  @DefaultMessage("Analyte")
  @Key("analyte")
  String analyte();

  /**
   * Translated "Only those analytes that aren''t added to a group can be grouped".
   * 
   * @return translated "Only those analytes that aren''t added to a group can be grouped"
  
   */
  @DefaultMessage("Only those analytes that aren''t added to a group can be grouped")
  @Key("analyteAlreadyGrouped")
  String analyteAlreadyGrouped();

  /**
   * Translated "Analyte cannot be deleted, one or more auxiliary fields are still linked to it".
   * 
   * @return translated "Analyte cannot be deleted, one or more auxiliary fields are still linked to it"
  
   */
  @DefaultMessage("Analyte cannot be deleted, one or more auxiliary fields are still linked to it")
  @Key("analyteAuxFieldDeleteException")
  String analyteAuxFieldDeleteException();

  /**
   * Translated "Analyte cannot be deleted, one or more analytes are still linked to it".
   * 
   * @return translated "Analyte cannot be deleted, one or more analytes are still linked to it"
  
   */
  @DefaultMessage("Analyte cannot be deleted, one or more analytes are still linked to it")
  @Key("analyteDeleteException")
  String analyteDeleteException();

  /**
   * Translated "This analyte has been removed from the list of analytes for this test. Please choose another analyte".
   * 
   * @return translated "This analyte has been removed from the list of analytes for this test. Please choose another analyte"
  
   */
  @DefaultMessage("This analyte has been removed from the list of analytes for this test. Please choose another analyte")
  @Key("analyteDeleted")
  String analyteDeleted();

  /**
   * Translated "Define chemical and non-chemical information for tests and auxiliary prompts.".
   * 
   * @return translated "Define chemical and non-chemical information for tests and auxiliary prompts."
  
   */
  @DefaultMessage("Define chemical and non-chemical information for tests and auxiliary prompts.")
  @Key("analyteDescription")
  String analyteDescription();

  /**
   * Translated "Analyte Group".
   * 
   * @return translated "Analyte Group"
  
   */
  @DefaultMessage("Analyte Group")
  @Key("analyteGroup")
  String analyteGroup();

  /**
   * Translated "History - Analyte".
   * 
   * @return translated "History - Analyte"
  
   */
  @DefaultMessage("History - Analyte")
  @Key("analyteHistory")
  String analyteHistory();

  /**
   * Translated "No analyte found for position {0} analyte ''{1}''".
   * 
   * @param arg0 "{0}"
   * @param arg1 "{1}"
   * @return translated "No analyte found for position {0} analyte ''{1}''"
  
   */
  @DefaultMessage("No analyte found for position {0} analyte ''{1}''")
  @Key("analyteLookupFormException")
  String analyteLookupFormException(String arg0,  String arg1);

  /**
   * Translated "Analyte cannot be deleted, one or more methods are still linked to it".
   * 
   * @return translated "Analyte cannot be deleted, one or more methods are still linked to it"
  
   */
  @DefaultMessage("Analyte cannot be deleted, one or more methods are still linked to it")
  @Key("analyteMethodDeleteException")
  String analyteMethodDeleteException();

  /**
   * Translated "The name of this analyte has been changed. Please choose another analyte or the changed name".
   * 
   * @return translated "The name of this analyte has been changed. Please choose another analyte or the changed name"
  
   */
  @DefaultMessage("The name of this analyte has been changed. Please choose another analyte or the changed name")
  @Key("analyteNameChanged")
  String analyteNameChanged();

  /**
   * Translated "There are analytes added to this test but no results. Are you sure you would like to commit?".
   * 
   * @return translated "There are analytes added to this test but no results. Are you sure you would like to commit?"
  
   */
  @DefaultMessage("There are analytes added to this test but no results. Are you sure you would like to commit?")
  @Key("analyteNoResults")
  String analyteNoResults();

  /**
   * Translated "Only those analytes that appear at adjacent positions in the tree can be grouped with each other".
   * 
   * @return translated "Only those analytes that appear at adjacent positions in the tree can be grouped with each other"
  
   */
  @DefaultMessage("Only those analytes that appear at adjacent positions in the tree can be grouped with each other")
  @Key("analyteNotAdjcnt")
  String analyteNotAdjcnt();

  /**
   * Translated "Test Row {0} - Please uncheck the analyte ''{1}'' as it has been removed from the test".
   * 
   * @param arg0 "{0}"
   * @param arg1 "{1}"
   * @return translated "Test Row {0} - Please uncheck the analyte ''{1}'' as it has been removed from the test"
  
   */
  @DefaultMessage("Test Row {0} - Please uncheck the analyte ''{1}'' as it has been removed from the test")
  @Key("analyteNotPresentInTestException")
  String analyteNotPresentInTestException(String arg0,  String arg1);

  /**
   * Translated "Analyte Parameter  ".
   * 
   * @return translated "Analyte Parameter  "
  
   */
  @DefaultMessage("Analyte Parameter  ")
  @Key("analyteParameter")
  String analyteParameter();

  /**
   * Translated "Add Analyte".
   * 
   * @return translated "Add Analyte"
  
   */
  @DefaultMessage("Add Analyte")
  @Key("analyteParameter.addAnalyte")
  String analyteParameter_addAnalyte();

  /**
   * Translated "Add Parameter".
   * 
   * @return translated "Add Parameter"
  
   */
  @DefaultMessage("Add Parameter")
  @Key("analyteParameter.addParameter")
  String analyteParameter_addParameter();

  /**
   * Translated "Analyte Parameter".
   * 
   * @return translated "Analyte Parameter"
  
   */
  @DefaultMessage("Analyte Parameter")
  @Key("analyteParameter.analyteParameter")
  String analyteParameter_analyteParameter();

  /**
   * Translated "{0}: At least one of P1, P2, P3 is required".
   * 
   * @return translated "{0}: At least one of P1, P2, P3 is required"
   */
  @DefaultMessage("{0}: At least one of P1, P2, P3 is required")
  @Key("analyteParameter.atleastOnePRequiredException")
  String analyteParameter_atleastOnePRequiredException(String arg0);

  /**
   * Translated "{0}: Begin date must be after every previous parameter''s end date".
   * 
   * @return translated "{0}: Begin date must be after every previous parameter''s end date"
   */
  @DefaultMessage("{0}: Begin date must be after every previous parameter''s end date")
  @Key("analyteParameter.beginDateAfterPrevEndDateException")
  String analyteParameter_beginDateAfterPrevEndDateException(String arg0);

  /**
   * Translated "{0}: Begin date must be more than a minute ahead of every previous parameter''s begin date".
   * 
   * @return translated "{0}: Begin date must be more than a minute ahead of every previous parameter''s begin date"
   */
  @DefaultMessage("{0}: Begin date must be more than a minute ahead of every previous parameter''s begin date")
  @Key("analyteParameter.beginDateMinuteAheadException")
  String analyteParameter_beginDateMinuteAheadException(String arg0);

  /**
   * Translated "{0}: Begin date is required".
   * 
   * @return translated "{0}: Begin date is required"
   */
  @DefaultMessage("{0}: Begin date is required")
  @Key("analyteParameter.beginDateRequiredException")
  String analyteParameter_beginDateRequiredException(String arg0);

  /**
   * Translated "{0}: End date must be after begin date".
   * 
   * @return translated "{0}: End date must be after begin date"
   */
  @DefaultMessage("{0}: End date must be after begin date")
  @Key("analyteParameter.endDateAfterBeginException")
  String analyteParameter_endDateAfterBeginException(String arg0);

  /**
   * Translated "{0}: End date is required".
   * 
   * @return translated "{0}: End date is required"
   */
  @DefaultMessage("{0}: End date is required")
  @Key("analyteParameter.endDateRequiredException")
  String analyteParameter_endDateRequiredException(String arg0);

  /**
   * Translated "Name is required".
   * 
   * @return translated "Name is required"
   */
  @DefaultMessage("Name is required")
  @Key("analyteParameter.nameRequiredException")
  String analyteParameter_nameRequiredException();

  /**
   * Translated "P1".
   * 
   * @return translated "P1"
  
   */
  @DefaultMessage("P1")
  @Key("analyteParameter.p1")
  String analyteParameter_p1();

  /**
   * Translated "P2".
   * 
   * @return translated "P2"
  
   */
  @DefaultMessage("P2")
  @Key("analyteParameter.p2")
  String analyteParameter_p2();

  /**
   * Translated "P3".
   * 
   * @return translated "P3"
  
   */
  @DefaultMessage("P3")
  @Key("analyteParameter.p3")
  String analyteParameter_p3();

  /**
   * Translated "{0} already has existing parameters".
   * 
   * @return translated "{0} already has existing parameters"
   */
  @DefaultMessage("{0} already has existing parameters")
  @Key("analyteParameter.parametersExistException")
  String analyteParameter_parametersExistException(String arg0);

  /**
   * Translated "{0} must have at least one parameter".
   * 
   * @return translated "{0} must have at least one parameter"
   */
  @DefaultMessage("{0} must have at least one parameter")
  @Key("analyteParameter.recordHasNoParametersException")
  String analyteParameter_recordHasNoParametersException(String arg0);

  /**
   * Translated "Repeated {0}: Change sample type or unit to differentiate      ".
   * 
   * @return translated "Repeated {0}: Change sample type or unit to differentiate      "
   */
  @DefaultMessage("Repeated {0}: Change sample type or unit to differentiate      ")
  @Key("analyteParameter.repeatedComboException")
  String analyteParameter_repeatedComboException(String arg0);

  /**
   * Translated "Select an analyte row to add a parameter".
   * 
   * @return translated "Select an analyte row to add a parameter"
   */
  @DefaultMessage("Select an analyte row to add a parameter")
  @Key("analyteParameter.selectAnalyteToAddParam")
  String analyteParameter_selectAnalyteToAddParam();

  /**
   * Translated "Select one or more row(s) to duplicate".
   * 
   * @return translated "Select one or more row(s) to duplicate"
   */
  @DefaultMessage("Select one or more row(s) to duplicate")
  @Key("analyteParameter.selectOneOrMoreRowsToDup")
  String analyteParameter_selectOneOrMoreRowsToDup();

  /**
   * Translated "Select one or more row(s) to remove".
   * 
   * @return translated "Select one or more row(s) to remove"
   */
  @DefaultMessage("Select one or more row(s) to remove")
  @Key("analyteParameter.selectOneOrMoreRowsToRemove")
  String analyteParameter_selectOneOrMoreRowsToRemove();

  /**
   * Translated "Please select a sample type before selecting unit".
   * 
   * @return translated "Please select a sample type before selecting unit"
  
   */
  @DefaultMessage("Please select a sample type before selecting unit")
  @Key("analyteParameter.selectSampleTypeBeforeUnit")
  String analyteParameter_selectSampleTypeBeforeUnit();

  /**
   * Translated "Please select a type".
   * 
   * @return translated "Please select a type"
  
   */
  @DefaultMessage("Please select a type")
  @Key("analyteParameter.selectType")
  String analyteParameter_selectType();

  /**
   * Translated "Type is required".
   * 
   * @return translated "Type is required"
   */
  @DefaultMessage("Type is required")
  @Key("analyteParameter.typeRequiredException")
  String analyteParameter_typeRequiredException();

  /**
   * Translated "Define ranges for various analytical parameters.".
   * 
   * @return translated "Define ranges for various analytical parameters."
  
   */
  @DefaultMessage("Define ranges for various analytical parameters.")
  @Key("analyteParameterDescription")
  String analyteParameterDescription();

  /**
   * Translated "Analyte cannot be deleted, one or more QCs are still linked to it".
   * 
   * @return translated "Analyte cannot be deleted, one or more QCs are still linked to it"
  
   */
  @DefaultMessage("Analyte cannot be deleted, one or more QCs are still linked to it")
  @Key("analyteQCDeleteException")
  String analyteQCDeleteException();

  /**
   * Translated "Analyte cannot be deleted, one or more results are still linked to it".
   * 
   * @return translated "Analyte cannot be deleted, one or more results are still linked to it"
  
   */
  @DefaultMessage("Analyte cannot be deleted, one or more results are still linked to it")
  @Key("analyteResultDeleteException")
  String analyteResultDeleteException();

  /**
   * Translated "Analyte cannot be deleted, one or more tests are still linked to it".
   * 
   * @return translated "Analyte cannot be deleted, one or more tests are still linked to it"
  
   */
  @DefaultMessage("Analyte cannot be deleted, one or more tests are still linked to it")
  @Key("analyteTestDeleteException")
  String analyteTestDeleteException();

  /**
   * Translated "Type is required for an analyte".
   * 
   * @return translated "Type is required for an analyte"
  
   */
  @DefaultMessage("Type is required for an analyte")
  @Key("analyteTypeRequiredException")
  String analyteTypeRequiredException();

  /**
   * Translated "Analytes".
   * 
   * @return translated "Analytes"
  
   */
  @DefaultMessage("Analytes")
  @Key("analytes")
  String analytes();

  /**
   * Translated "Analytes & Results".
   * 
   * @return translated "Analytes & Results"
  
   */
  @DefaultMessage("Analytes & Results")
  @Key("analytesResults")
  String analytesResults();

  /**
   * Translated "Analytes excluded from worksheet".
   * 
   * @return translated "Analytes excluded from worksheet"
  
   */
  @DefaultMessage("Analytes excluded from worksheet")
  @Key("analytesWS")
  String analytesWS();

  /**
   * Translated "Animal Sample".
   * 
   * @return translated "Animal Sample"
  
   */
  @DefaultMessage("Animal Sample")
  @Key("animalSample")
  String animalSample();

  /**
   * Translated "Animal Sample Login".
   * 
   * @return translated "Animal Sample Login"
  
   */
  @DefaultMessage("Animal Sample Login")
  @Key("animalSampleLogin")
  String animalSampleLogin();

  /**
   * Translated "Fully login animal sample and analysis related information.".
   * 
   * @return translated "Fully login animal sample and analysis related information."
  
   */
  @DefaultMessage("Fully login animal sample and analysis related information.")
  @Key("animalSampleLoginDescription")
  String animalSampleLoginDescription();

  /**
   * Translated "Application".
   * 
   * @return translated "Application"
  
   */
  @DefaultMessage("Application")
  @Key("application")
  String application();

  /**
   * Translated "Apt/Suite #".
   * 
   * @return translated "Apt/Suite #"
  
   */
  @DefaultMessage("Apt/Suite #")
  @Key("aptSuite")
  String aptSuite();

  /**
   * Translated "At least one pair of \"From\" and \"To\" fields, e.g. the ones for Completed Date, must be filled ".
   * 
   * @return translated "At least one pair of \"From\" and \"To\" fields, e.g. the ones for Completed Date, must be filled "
  
   */
  @DefaultMessage("At least one pair of \"From\" and \"To\" fields, e.g. the ones for Completed Date, must be filled ")
  @Key("atLeastOnePairFilledException")
  String atLeastOnePairFilledException();

  /**
   * Translated "You cannot remove the last result row under a heading".
   * 
   * @return translated "You cannot remove the last result row under a heading"
  
   */
  @DefaultMessage("You cannot remove the last result row under a heading")
  @Key("atLeastOneResultUnderHeading")
  String atLeastOneResultUnderHeading();

  /**
   * Translated "Please fill at least one field under Sample Selection Criteria".
   * 
   * @return translated "Please fill at least one field under Sample Selection Criteria"
  
   */
  @DefaultMessage("Please fill at least one field under Sample Selection Criteria")
  @Key("atleastOneFieldFilledException")
  String atleastOneFieldFilledException();

  /**
   * Translated "At least one of P1, P2, P3 is required for analyte ''{0}''".
   * 
   * @param arg0 "{0}"
   * @return translated "At least one of P1, P2, P3 is required for analyte ''{0}''"
  
   */
  @DefaultMessage("At least one of P1, P2, P3 is required for analyte ''{0}''")
  @Key("atleastOnePRequiredForAnalyteException")
  String atleastOnePRequiredForAnalyteException(String arg0);

  /**
   * Translated "At least one result group must be added before any results can be added for this test".
   * 
   * @return translated "At least one result group must be added before any results can be added for this test"
  
   */
  @DefaultMessage("At least one result group must be added before any results can be added for this test")
  @Key("atleastOneResGrp")
  String atleastOneResGrp();

  /**
   * Translated "A result group must have at least one result in it".
   * 
   * @return translated "A result group must have at least one result in it"
  
   */
  @DefaultMessage("A result group must have at least one result in it")
  @Key("atleastOneResInResGrp")
  String atleastOneResInResGrp();

  /**
   * Translated "A test must have at least one sample type".
   * 
   * @return translated "A test must have at least one sample type"
  
   */
  @DefaultMessage("A test must have at least one sample type")
  @Key("atleastOneSampleTypeException")
  String atleastOneSampleTypeException();

  /**
   * Translated "A test must have at least one section added to it".
   * 
   * @return translated "A test must have at least one section added to it"
  
   */
  @DefaultMessage("A test must have at least one section added to it")
  @Key("atleastOneSection")
  String atleastOneSection();

  /**
   * Translated "At least two analytes must be selected to form a group".
   * 
   * @return translated "At least two analytes must be selected to form a group"
  
   */
  @DefaultMessage("At least two analytes must be selected to form a group")
  @Key("atleastTwoAnalytes")
  String atleastTwoAnalytes();

  /**
   * Translated "Analyte cannot be removed as the row group has less than two analytes".
   * 
   * @return translated "Analyte cannot be removed as the row group has less than two analytes"
  
   */
  @DefaultMessage("Analyte cannot be removed as the row group has less than two analytes")
  @Key("atleastTwoRowsInRowGroup")
  String atleastTwoRowsInRowGroup();

  /**
   * Translated "Attach".
   * 
   * @return translated "Attach"
  
   */
  @DefaultMessage("Attach")
  @Key("attachment.attach")
  String attachment_attach();

  /**
   * Translated "Attachment".
   * 
   * @return translated "Attachment"
  
   */
  @DefaultMessage("Attachment")
  @Key("attachment.attachment")
  String attachment_attachment();

  /**
   * Translated "Auto Select Next".
   * 
   * @return translated "Auto Select Next"
  
   */
  @DefaultMessage("Auto Select Next")
  @Key("attachment.autoSelectNext")
  String attachment_autoSelectNext();

  /**
   * Translated "Attachment ''{0}'': Description is required ".
   * 
   * @param arg0 "{0}"
   * @return translated "Attachment ''{0}'': Description is required "
  
   */
  @DefaultMessage("Attachment ''{0}'': Description is required ")
  @Key("attachment.descRequiredException")
  String attachment_descRequiredException(String arg0);

  /**
   * Translated "Attachment Description".
   * 
   * @return translated "Attachment Description"
  
   */
  @DefaultMessage("Attachment Description")
  @Key("attachment.description")
  String attachment_description();

  /**
   * Translated "Detach".
   * 
   * @return translated "Detach"
  
   */
  @DefaultMessage("Detach")
  @Key("attachment.detach")
  String attachment_detach();

  /**
   * Translated "Display".
   * 
   * @return translated "Display"
  
   */
  @DefaultMessage("Display")
  @Key("attachment.display")
  String attachment_display();

  /**
   * Translated "File Name".
   * 
   * @return translated "File Name"
  
   */
  @DefaultMessage("File Name")
  @Key("attachment.fileName")
  String attachment_fileName();

  /**
   * Translated "Path to attachment directory is missing. Please contact the system administrator.".
   * 
   * @return translated "Path to attachment directory is missing. Please contact the system administrator."
  
   */
  @DefaultMessage("Path to attachment directory is missing. Please contact the system administrator.")
  @Key("attachment.missingPathException")
  String attachment_missingPathException();

  /**
   * Translated "Can not save attachment file ''{0}''. Please contact the system administrator.".
   * 
   * @param arg0 "{0}"
   * @return translated "Can not save attachment file ''{0}''. Please contact the system administrator."
  
   */
  @DefaultMessage("Can not save attachment file ''{0}''. Please contact the system administrator.")
  @Key("attachment.moveFileException")
  String attachment_moveFileException(String arg0);

  /**
   * Translated "Sample # {0,number,#0}".
   * 
   * @param arg0 "{0,number,#0}"
   * @return translated "Sample # {0,number,#0}"
  
   */
  @DefaultMessage("Sample # {0,number,#0}")
  @Key("attachment.sampleDescription")
  String attachment_sampleDescription(Integer arg0);

  /**
   * Translated "Attachment ''{0}'': You do not have permission to view the attachment".
   * 
   * @param arg0 "{0}"
   * @return translated "Attachment ''{0}'': You do not have permission to view the attachment"
  
   */
  @DefaultMessage("Attachment ''{0}'': You do not have permission to view the attachment")
  @Key("attachment.viewPermException")
  String attachment_viewPermException(String arg0);

  /**
   * Translated "Worksheet # {0,number,#0}".
   * 
   * @return translated "Worksheet # {0,number,#0}"
   */
  @DefaultMessage("Worksheet # {0,number,#0}")
  @Key("attachment.worksheetDescription")
  String attachment_worksheetDescription(Integer arg0);

  /**
   * Translated "Attention".
   * 
   * @return translated "Attention"
  
   */
  @DefaultMessage("Attention")
  @Key("attention")
  String attention();

  /**
   * Translated "Attn".
   * 
   * @return translated "Attn"
  
   */
  @DefaultMessage("Attn")
  @Key("attn")
  String attn();

  /**
   * Translated "Authentication Failure".
   * 
   * @return translated "Authentication Failure"
  
   */
  @DefaultMessage("Authentication Failure")
  @Key("authFailure")
  String authFailure();

  /**
   * Translated "One or more Auto Reflex Test(s) must have a Section assigned".
   * 
   * @return translated "One or more Auto Reflex Test(s) must have a Section assigned"
  
   */
  @DefaultMessage("One or more Auto Reflex Test(s) must have a Section assigned")
  @Key("autoReflexTestNeedsSection")
  String autoReflexTestNeedsSection();

  /**
   * Translated "Auto Reorder".
   * 
   * @return translated "Auto Reorder"
  
   */
  @DefaultMessage("Auto Reorder")
  @Key("autoReorder")
  String autoReorder();

  /**
   * Translated "Aux Data".
   * 
   * @return translated "Aux Data"
  
   */
  @DefaultMessage("Aux Data")
  @Key("aux.data")
  String aux_data();

  /**
   * Translated "Accession # {0,number,#0}: Aux data ''{1}'' with DEFAULT value ''{2}'' invalid; please update the aux group definition".
   * 
   * @param arg0 "{0,number,#0}"
   * @param arg1 "{1}"
   * @param arg2 "{2}"
   * @return translated "Accession # {0,number,#0}: Aux data ''{1}'' with DEFAULT value ''{2}'' invalid; please update the aux group definition"
  
   */
  @DefaultMessage("Accession # {0,number,#0}: Aux data ''{1}'' with DEFAULT value ''{2}'' invalid; please update the aux group definition")
  @Key("aux.defaultValueInvalidException")
  String aux_defaultValueInvalidException(Integer arg0,  String arg1,  String arg2);

  /**
   * Translated "No matching active auxiliary group found for ''{0}''".
   * 
   * @param arg0 "{0}"
   * @return translated "No matching active auxiliary group found for ''{0}''"
  
   */
  @DefaultMessage("No matching active auxiliary group found for ''{0}''")
  @Key("aux.inactiveGroupException")
  String aux_inactiveGroupException(String arg0);

  /**
   * Translated "You must first select a table row before clicking add".
   * 
   * @return translated "You must first select a table row before clicking add"
  
   */
  @DefaultMessage("You must first select a table row before clicking add")
  @Key("aux.noSelectedRow")
  String aux_noSelectedRow();

  /**
   * Translated "This will remove all rows with the same group as this row.\n\nPress Ok to continue.".
   * 
   * @return translated "This will remove all rows with the same group as this row.\n\nPress Ok to continue."
  
   */
  @DefaultMessage("This will remove all rows with the same group as this row.\n\nPress Ok to continue.")
  @Key("aux.removeMessage")
  String aux_removeMessage();

  /**
   * Translated "Please select an analyte before entering the value".
   * 
   * @return translated "Please select an analyte before entering the value"
  
   */
  @DefaultMessage("Please select an analyte before entering the value")
  @Key("aux.selectAnalyteBeforeValue")
  String aux_selectAnalyteBeforeValue();

  /**
   * Translated "Accession # {0,number,#0}: Aux data ''{1}'' with value ''{2}'' invalid".
   * 
   * @param arg0 "{0,number,#0}"
   * @param arg1 "{1}"
   * @param arg2 "{2}"
   * @return translated "Accession # {0,number,#0}: Aux data ''{1}'' with value ''{2}'' invalid"
  
   */
  @DefaultMessage("Accession # {0,number,#0}: Aux data ''{1}'' with value ''{2}'' invalid")
  @Key("aux.valueInvalidException")
  String aux_valueInvalidException(Integer arg0,  String arg1,  String arg2);

  /**
   * Translated "An auxiliary group cannot be added to a panel more than once ".
   * 
   * @return translated "An auxiliary group cannot be added to a panel more than once "
  
   */
  @DefaultMessage("An auxiliary group cannot be added to a panel more than once ")
  @Key("auxAlreadyAddedException")
  String auxAlreadyAddedException();

  /**
   * Translated "Aux Data".
   * 
   * @return translated "Aux Data"
  
   */
  @DefaultMessage("Aux Data")
  @Key("auxData")
  String auxData();

  /**
   * Translated "You can only query 1 aux data row at a time".
   * 
   * @return translated "You can only query 1 aux data row at a time"
  
   */
  @DefaultMessage("You can only query 1 aux data row at a time")
  @Key("auxDataOneQueryException")
  String auxDataOneQueryException();

  /**
   * Translated "Field values belonging to an auxiliary field can have the type \"Default\" only if there is one other type selected for one of them.".
   * 
   * @return translated "Field values belonging to an auxiliary field can have the type \"Default\" only if there is one other type selected for one of them."
  
   */
  @DefaultMessage("Field values belonging to an auxiliary field can have the type \"Default\" only if there is one other type selected for one of them.")
  @Key("auxDefaultWithNoOtherTypeException")
  String auxDefaultWithNoOtherTypeException();

  /**
   * Translated "Values of type Dictionary must be unique among the field values belonging to an auxiliary field.".
   * 
   * @return translated "Values of type Dictionary must be unique among the field values belonging to an auxiliary field."
  
   */
  @DefaultMessage("Values of type Dictionary must be unique among the field values belonging to an auxiliary field.")
  @Key("auxDictEntryNotUniqueException")
  String auxDictEntryNotUniqueException();

  /**
   * Translated "There is already an active auxiliary field group in the system with the same name".
   * 
   * @return translated "There is already an active auxiliary field group in the system with the same name"
  
   */
  @DefaultMessage("There is already an active auxiliary field group in the system with the same name")
  @Key("auxFieldGroupActiveException")
  String auxFieldGroupActiveException();

  /**
   * Translated "History - Field Group".
   * 
   * @return translated "History - Field Group"
  
   */
  @DefaultMessage("History - Field Group")
  @Key("auxFieldGroupHistory")
  String auxFieldGroupHistory();

  /**
   * Translated "This auxiliary field group has an overlapping begin date or end date with another auxiliary field group which has the same name as this one.".
   * 
   * @return translated "This auxiliary field group has an overlapping begin date or end date with another auxiliary field group which has the same name as this one."
  
   */
  @DefaultMessage("This auxiliary field group has an overlapping begin date or end date with another auxiliary field group which has the same name as this one.")
  @Key("auxFieldGroupTimeOverlapException")
  String auxFieldGroupTimeOverlapException();

  /**
   * Translated "History - Field".
   * 
   * @return translated "History - Field"
  
   */
  @DefaultMessage("History - Field")
  @Key("auxFieldHistory")
  String auxFieldHistory();

  /**
   * Translated "An auxiliary field must be selected before any field values can be added.".
   * 
   * @return translated "An auxiliary field must be selected before any field values can be added."
  
   */
  @DefaultMessage("An auxiliary field must be selected before any field values can be added.")
  @Key("auxFieldSelFirst")
  String auxFieldSelFirst();

  /**
   * Translated "History - Field Value".
   * 
   * @return translated "History - Field Value"
  
   */
  @DefaultMessage("History - Field Value")
  @Key("auxFieldValueHistory")
  String auxFieldValueHistory();

  /**
   * Translated "Aux Group Selection".
   * 
   * @return translated "Aux Group Selection"
  
   */
  @DefaultMessage("Aux Group Selection")
  @Key("auxGroupSelection")
  String auxGroupSelection();

  /**
   * Translated "Aux Groups".
   * 
   * @return translated "Aux Groups"
  
   */
  @DefaultMessage("Aux Groups")
  @Key("auxGroups")
  String auxGroups();

  /**
   * Translated "The auxiliary group ''{0}'' has already been added".
   * 
   * @param arg0 "{0}"
   * @return translated "The auxiliary group ''{0}'' has already been added"
  
   */
  @DefaultMessage("The auxiliary group ''{0}'' has already been added")
  @Key("auxGrpAlreadyAddedException")
  String auxGrpAlreadyAddedException(String arg0);

  /**
   * Translated "Field values belonging to an auxiliary field can have only one of these types: \"Alpha Lowercase\",\"Alpha Mixedcase\" and \"Alpha Uppercase\".".
   * 
   * @return translated "Field values belonging to an auxiliary field can have only one of these types: \"Alpha Lowercase\",\"Alpha Mixedcase\" and \"Alpha Uppercase\"."
  
   */
  @DefaultMessage("Field values belonging to an auxiliary field can have only one of these types: \"Alpha Lowercase\",\"Alpha Mixedcase\" and \"Alpha Uppercase\".")
  @Key("auxMoreThanOneAlphaTypeException")
  String auxMoreThanOneAlphaTypeException();

  /**
   * Translated "Field values belonging to an auxiliary field can have only one of these types: \"Date\",\"Date-Time\" and \"Time\".".
   * 
   * @return translated "Field values belonging to an auxiliary field can have only one of these types: \"Date\",\"Date-Time\" and \"Time\"."
  
   */
  @DefaultMessage("Field values belonging to an auxiliary field can have only one of these types: \"Date\",\"Date-Time\" and \"Time\".")
  @Key("auxMoreThanOneDateTypeException")
  String auxMoreThanOneDateTypeException();

  /**
   * Translated "Field values belonging to an auxiliary field can have the type \"Default\" only once.".
   * 
   * @return translated "Field values belonging to an auxiliary field can have the type \"Default\" only once."
  
   */
  @DefaultMessage("Field values belonging to an auxiliary field can have the type \"Default\" only once.")
  @Key("auxMoreThanOneDefaultException")
  String auxMoreThanOneDefaultException();

  /**
   * Translated "Field values belonging to an auxiliary field must have either the same type or the type \"Default\".".
   * 
   * @return translated "Field values belonging to an auxiliary field must have either the same type or the type \"Default\"."
  
   */
  @DefaultMessage("Field values belonging to an auxiliary field must have either the same type or the type \"Default\".")
  @Key("auxMoreThanOneTypeException")
  String auxMoreThanOneTypeException();

  /**
   * Translated "This range is overlapping with ranges associated with one or more field values belonging to an auxiliary field.".
   * 
   * @return translated "This range is overlapping with ranges associated with one or more field values belonging to an auxiliary field."
  
   */
  @DefaultMessage("This range is overlapping with ranges associated with one or more field values belonging to an auxiliary field.")
  @Key("auxNumRangeOverlapException")
  String auxNumRangeOverlapException();

  /**
   * Translated "Reportable".
   * 
   * @return translated "Reportable"
  
   */
  @DefaultMessage("Reportable")
  @Key("auxReportable")
  String auxReportable();

  /**
   * Translated "Auxiliary Prompt".
   * 
   * @return translated "Auxiliary Prompt"
  
   */
  @DefaultMessage("Auxiliary Prompt")
  @Key("auxiliaryPrompt")
  String auxiliaryPrompt();

  /**
   * Translated "Create supplemental information fields that can be attached to orders/samples.".
   * 
   * @return translated "Create supplemental information fields that can be attached to orders/samples."
  
   */
  @DefaultMessage("Create supplemental information fields that can be attached to orders/samples.")
  @Key("auxiliaryPromptDescription")
  String auxiliaryPromptDescription();

  /**
   * Translated "Available".
   * 
   * @return translated "Available"
  
   */
  @DefaultMessage("Available")
  @Key("available")
  String available();

  /**
   * Translated "Available On Worksheet".
   * 
   * @return translated "Available On Worksheet"
  
   */
  @DefaultMessage("Available On Worksheet")
  @Key("availableWS")
  String availableWS();

  /**
   * Translated "Avg".
   * 
   * @return translated "Avg"
  
   */
  @DefaultMessage("Avg")
  @Key("average")
  String average();

  /**
   * Translated "Avg Cost".
   * 
   * @return translated "Avg Cost"
  
   */
  @DefaultMessage("Avg Cost")
  @Key("averageCost")
  String averageCost();

  /**
   * Translated "Avg Daily Use".
   * 
   * @return translated "Avg Daily Use"
  
   */
  @DefaultMessage("Avg Daily Use")
  @Key("averageDailyUse")
  String averageDailyUse();

  /**
   * Translated "Avg Lead Time".
   * 
   * @return translated "Avg Lead Time"
  
   */
  @DefaultMessage("Avg Lead Time")
  @Key("averageLeadTime")
  String averageLeadTime();

  /**
   * Translated "% Avg. TAT Used".
   * 
   * @return translated "% Avg. TAT Used"
  
   */
  @DefaultMessage("% Avg. TAT Used")
  @Key("avgTA")
  String avgTA();

  /**
   * Translated "Back To Search".
   * 
   * @return translated "Back To Search"
  
   */
  @DefaultMessage("Back To Search")
  @Key("backToSearch")
  String backToSearch();

  /**
   * Translated "Barcode".
   * 
   * @return translated "Barcode"
  
   */
  @DefaultMessage("Barcode")
  @Key("barcode")
  String barcode();

  /**
   * Translated "Bean".
   * 
   * @return translated "Bean"
  
   */
  @DefaultMessage("Bean")
  @Key("bean")
  String bean();

  /**
   * Translated "Begin".
   * 
   * @return translated "Begin"
  
   */
  @DefaultMessage("Begin")
  @Key("begin")
  String begin();

  /**
   * Translated "Begin Date".
   * 
   * @return translated "Begin Date"
  
   */
  @DefaultMessage("Begin Date")
  @Key("beginDate")
  String beginDate();

  /**
   * Translated "Begin date for ''{0}'' must be after the end dates of its previous entries ".
   * 
   * @param arg0 "{0}"
   * @return translated "Begin date for ''{0}'' must be after the end dates of its previous entries "
  
   */
  @DefaultMessage("Begin date for ''{0}'' must be after the end dates of its previous entries ")
  @Key("beginDateAfterPreviousEndDateException")
  String beginDateAfterPreviousEndDateException(String arg0);

  /**
   * Translated "Begin date must be more than a minute ahead of the begin date of each previous entry of this analyte   ".
   * 
   * @return translated "Begin date must be more than a minute ahead of the begin date of each previous entry of this analyte   "
  
   */
  @DefaultMessage("Begin date must be more than a minute ahead of the begin date of each previous entry of this analyte   ")
  @Key("beginDateInvalidException")
  String beginDateInvalidException();

  /**
   * Translated "Begin date for ''{0}'' must be more than a minute ahead of the begin dates of its previous entries  ".
   * 
   * @param arg0 "{0}"
   * @return translated "Begin date for ''{0}'' must be more than a minute ahead of the begin dates of its previous entries  "
  
   */
  @DefaultMessage("Begin date for ''{0}'' must be more than a minute ahead of the begin dates of its previous entries  ")
  @Key("beginDateInvalidWithParamException")
  String beginDateInvalidWithParamException(String arg0);

  /**
   * Translated "Begin date is required for analyte ''{0}'' ".
   * 
   * @param arg0 "{0}"
   * @return translated "Begin date is required for analyte ''{0}'' "
  
   */
  @DefaultMessage("Begin date is required for analyte ''{0}'' ")
  @Key("beginDateRequiredForAnalyteException")
  String beginDateRequiredForAnalyteException(String arg0);

  /**
   * Translated "You are at the beginning of the query results".
   * 
   * @return translated "You are at the beginning of the query results"
  
   */
  @DefaultMessage("You are at the beginning of the query results")
  @Key("beginningQueryException")
  String beginningQueryException();

  /**
   * Translated "Bill To".
   * 
   * @return translated "Bill To"
  
   */
  @DefaultMessage("Bill To")
  @Key("billTo")
  String billTo();

  /**
   * Translated "This sample must have a bill to".
   * 
   * @return translated "This sample must have a bill to"
  
   */
  @DefaultMessage("This sample must have a bill to")
  @Key("billToMissingWarning")
  String billToMissingWarning();

  /**
   * Translated "Billable".
   * 
   * @return translated "Billable"
  
   */
  @DefaultMessage("Billable")
  @Key("billable")
  String billable();

  /**
   * Translated "Billing Report".
   * 
   * @return translated "Billing Report"
  
   */
  @DefaultMessage("Billing Report")
  @Key("billingReport")
  String billingReport();

  /**
   * Translated "Birth".
   * 
   * @return translated "Birth"
  
   */
  @DefaultMessage("Birth")
  @Key("birth")
  String birth();

  /**
   * Translated "Browse".
   * 
   * @return translated "Browse"
  
   */
  @DefaultMessage("Browse")
  @Key("browse")
  String browse();

  /**
   * Translated "Build Kits".
   * 
   * @return translated "Build Kits"
  
   */
  @DefaultMessage("Build Kits")
  @Key("buildKits")
  String buildKits();

  /**
   * Translated "Assemble components to build kits.".
   * 
   * @return translated "Assemble components to build kits."
  
   */
  @DefaultMessage("Assemble components to build kits.")
  @Key("buildKitsDescription")
  String buildKitsDescription();

  /**
   * Translated "Bulk".
   * 
   * @return translated "Bulk"
  
   */
  @DefaultMessage("Bulk")
  @Key("bulk")
  String bulk();

  /**
   * Translated "By".
   * 
   * @return translated "By"
  
   */
  @DefaultMessage("By")
  @Key("by")
  String by();

  /**
   * Translated "A sample''s domain can only be changed once during an update".
   * 
   * @return translated "A sample''s domain can only be changed once during an update"
  
   */
  @DefaultMessage("A sample''s domain can only be changed once during an update")
  @Key("canChangeDomainOnlyOnce")
  String canChangeDomainOnlyOnce();

  /**
   * Translated "Cancel".
   * 
   * @return translated "Cancel"
  
   */
  @DefaultMessage("Cancel")
  @Key("cancel")
  String cancel();

  /**
   * Translated "Cancel Analysis?".
   * 
   * @return translated "Cancel Analysis?"
  
   */
  @DefaultMessage("Cancel Analysis?")
  @Key("cancelAnalysisCaption")
  String cancelAnalysisCaption();

  /**
   * Translated "You may not remove a committed analysis row. However, you can change the status to ''Cancelled''. Would you like to change the status to ''Cancelled''?".
   * 
   * @return translated "You may not remove a committed analysis row. However, you can change the status to ''Cancelled''. Would you like to change the status to ''Cancelled''?"
  
   */
  @DefaultMessage("You may not remove a committed analysis row. However, you can change the status to ''Cancelled''. Would you like to change the status to ''Cancelled''?")
  @Key("cancelAnalysisMessage")
  String cancelAnalysisMessage();

  /**
   * Translated "Canceling changes ...".
   * 
   * @return translated "Canceling changes ..."
  
   */
  @DefaultMessage("Canceling changes ...")
  @Key("cancelChanges")
  String cancelChanges();

  /**
   * Translated "Cancel Test".
   * 
   * @return translated "Cancel Test"
  
   */
  @DefaultMessage("Cancel Test")
  @Key("cancelTest")
  String cancelTest();

  /**
   * Translated "Cancelled orders cannot be updated".
   * 
   * @return translated "Cancelled orders cannot be updated"
  
   */
  @DefaultMessage("Cancelled orders cannot be updated")
  @Key("cancelledOrderCantBeUpdated")
  String cancelledOrderCantBeUpdated();

  /**
   * Translated "Not allowed to add a Bill To record".
   * 
   * @return translated "Not allowed to add a Bill To record"
  
   */
  @DefaultMessage("Not allowed to add a Bill To record")
  @Key("cantAddBillToException")
  String cantAddBillToException();

  /**
   * Translated "Adding a column is not allowed at this position".
   * 
   * @return translated "Adding a column is not allowed at this position"
  
   */
  @DefaultMessage("Adding a column is not allowed at this position")
  @Key("cantAddColumn")
  String cantAddColumn();

  /**
   * Translated "Sample and/or analysis is released. You cannot add non-internal QA events.".
   * 
   * @return translated "Sample and/or analysis is released. You cannot add non-internal QA events."
  
   */
  @DefaultMessage("Sample and/or analysis is released. You cannot add non-internal QA events.")
  @Key("cantAddQAEvent")
  String cantAddQAEvent();

  /**
   * Translated "Not allowed to add a Report To record".
   * 
   * @return translated "Not allowed to add a Report To record"
  
   */
  @DefaultMessage("Not allowed to add a Report To record")
  @Key("cantAddReportToException")
  String cantAddReportToException();

  /**
   * Translated "Not allowed to add a secondary bill to record".
   * 
   * @return translated "Not allowed to add a secondary bill to record"
  
   */
  @DefaultMessage("Not allowed to add a secondary bill to record")
  @Key("cantAddSecondReortToException")
  String cantAddSecondReortToException();

  /**
   * Translated "Add to existing is only allowed for bulk inventory items".
   * 
   * @return translated "Add to existing is only allowed for bulk inventory items"
  
   */
  @DefaultMessage("Add to existing is only allowed for bulk inventory items")
  @Key("cantAddToExistingException")
  String cantAddToExistingException();

  /**
   * Translated "The Accession Number for a Quick Entry sample cannot be changed until it has been fully logged in.".
   * 
   * @return translated "The Accession Number for a Quick Entry sample cannot be changed until it has been fully logged in."
  
   */
  @DefaultMessage("The Accession Number for a Quick Entry sample cannot be changed until it has been fully logged in.")
  @Key("cantChangeQuickEntryAccessionNumber")
  String cantChangeQuickEntryAccessionNumber();

  /**
   * Translated "Groups cannot be grouped with other analytes".
   * 
   * @return translated "Groups cannot be grouped with other analytes"
  
   */
  @DefaultMessage("Groups cannot be grouped with other analytes")
  @Key("cantGroupGroups")
  String cantGroupGroups();

  /**
   * Translated "A quick entered sample cannot be loaded if the order # is present ".
   * 
   * @return translated "A quick entered sample cannot be loaded if the order # is present "
  
   */
  @DefaultMessage("A quick entered sample cannot be loaded if the order # is present ")
  @Key("cantLoadQEIfOrderNumPresent")
  String cantLoadQEIfOrderNumPresent();

  /**
   * Translated "Removing a column is not allowed at this position".
   * 
   * @return translated "Removing a column is not allowed at this position"
  
   */
  @DefaultMessage("Removing a column is not allowed at this position")
  @Key("cantRemoveColumn")
  String cantRemoveColumn();

  /**
   * Translated "The item cannot be selected as it has been checked out".
   * 
   * @return translated "The item cannot be selected as it has been checked out"
  
   */
  @DefaultMessage("The item cannot be selected as it has been checked out")
  @Key("cantSelectItem")
  String cantSelectItem();

  /**
   * Translated "Analysis has been released. You cannot edit non-internal QA events.".
   * 
   * @return translated "Analysis has been released. You cannot edit non-internal QA events."
  
   */
  @DefaultMessage("Analysis has been released. You cannot edit non-internal QA events.")
  @Key("cantUpdateAnalysisQAEvent")
  String cantUpdateAnalysisQAEvent();

  /**
   * Translated "You can only update 1 row at a time".
   * 
   * @return translated "You can only update 1 row at a time"
  
   */
  @DefaultMessage("You can only update 1 row at a time")
  @Key("cantUpdateMultiple")
  String cantUpdateMultiple();

  /**
   * Translated "Updating a released sample is not allowed".
   * 
   * @return translated "Updating a released sample is not allowed"
  
   */
  @DefaultMessage("Updating a released sample is not allowed")
  @Key("cantUpdateReleasedException")
  String cantUpdateReleasedException();

  /**
   * Translated "One or more analysis have been released. You cannot edit non-internal sample QA events.".
   * 
   * @return translated "One or more analysis have been released. You cannot edit non-internal sample QA events."
  
   */
  @DefaultMessage("One or more analysis have been released. You cannot edit non-internal sample QA events.")
  @Key("cantUpdateSampleQAEvent")
  String cantUpdateSampleQAEvent();

  /**
   * Translated "The sample must be fully logged in".
   * 
   * @return translated "The sample must be fully logged in"
  
   */
  @DefaultMessage("The sample must be fully logged in")
  @Key("cantVerifyQuickEntry")
  String cantVerifyQuickEntry();

  /**
   * Translated "Category Name".
   * 
   * @return translated "Category Name"
  
   */
  @DefaultMessage("Category Name")
  @Key("catName")
  String catName();

  /**
   * Translated "Catalog #".
   * 
   * @return translated "Catalog #"
  
   */
  @DefaultMessage("Catalog #")
  @Key("catalogNum")
  String catalogNum();

  /**
   * Translated "Categories".
   * 
   * @return translated "Categories"
  
   */
  @DefaultMessage("Categories")
  @Key("categories")
  String categories();

  /**
   * Translated "Category".
   * 
   * @return translated "Category"
  
   */
  @DefaultMessage("Category")
  @Key("category")
  String category();

  /**
   * Translated "History - Category".
   * 
   * @return translated "History - Category"
  
   */
  @DefaultMessage("History - Category")
  @Key("categoryHistory")
  String categoryHistory();

  /**
   * Translated "Cell #".
   * 
   * @return translated "Cell #"
  
   */
  @DefaultMessage("Cell #")
  @Key("cellNumber")
  String cellNumber();

  /**
   * Translated "Change Domain".
   * 
   * @return translated "Change Domain"
  
   */
  @DefaultMessage("Change Domain")
  @Key("changeDomain")
  String changeDomain();

  /**
   * Translated "Change Domain".
   * 
   * @return translated "Change Domain"
  
   */
  @DefaultMessage("Change Domain")
  @Key("changeDomain.changeDomain")
  String changeDomain_changeDomain();

  /**
   * Translated "Change To".
   * 
   * @return translated "Change To"
  
   */
  @DefaultMessage("Change To")
  @Key("changeDomain.changeTo")
  String changeDomain_changeTo();

  /**
   * Translated "Change Password".
   * 
   * @return translated "Change Password"
  
   */
  @DefaultMessage("Change Password")
  @Key("changePassword")
  String changePassword();

  /**
   * Translated "Change To".
   * 
   * @return translated "Change To"
  
   */
  @DefaultMessage("Change To")
  @Key("changeTo")
  String changeTo();

  /**
   * Translated "Check All".
   * 
   * @return translated "Check All"
  
   */
  @DefaultMessage("Check All")
  @Key("checkAll")
  String checkAll();

  /**
   * Translated "Check In".
   * 
   * @return translated "Check In"
  
   */
  @DefaultMessage("Check In")
  @Key("checkIn")
  String checkIn();

  /**
   * Translated "Check Out".
   * 
   * @return translated "Check Out"
  
   */
  @DefaultMessage("Check Out")
  @Key("checkOut")
  String checkOut();

  /**
   * Translated "Check Out date must not be before Check In date.".
   * 
   * @return translated "Check Out date must not be before Check In date."
  
   */
  @DefaultMessage("Check Out date must not be before Check In date.")
  @Key("checkinDateAfterCheckoutDateException")
  String checkinDateAfterCheckoutDateException();

  /**
   * Translated "Choose Dictionary Entry".
   * 
   * @return translated "Choose Dictionary Entry"
  
   */
  @DefaultMessage("Choose Dictionary Entry")
  @Key("chooseDictEntry")
  String chooseDictEntry();

  /**
   * Translated "This value belongs to more than one category, please choose it by specifying the category".
   * 
   * @return translated "This value belongs to more than one category, please choose it by specifying the category"
  
   */
  @DefaultMessage("This value belongs to more than one category, please choose it by specifying the category")
  @Key("chooseValueByCategory")
  String chooseValueByCategory();

  /**
   * Translated "City".
   * 
   * @return translated "City"
  
   */
  @DefaultMessage("City")
  @Key("city")
  String city();

  /**
   * Translated "Clinical Sample".
   * 
   * @return translated "Clinical Sample"
  
   */
  @DefaultMessage("Clinical Sample")
  @Key("clinicalSample")
  String clinicalSample();

  /**
   * Translated "Clinical Sample Login".
   * 
   * @return translated "Clinical Sample Login"
  
   */
  @DefaultMessage("Clinical Sample Login")
  @Key("clinicalSampleLogin")
  String clinicalSampleLogin();

  /**
   * Translated "Fully login clinical sample and analysis related information.".
   * 
   * @return translated "Fully login clinical sample and analysis related information."
  
   */
  @DefaultMessage("Fully login clinical sample and analysis related information.")
  @Key("clinicalSampleLoginDescription")
  String clinicalSampleLoginDescription();

  /**
   * Translated "Client Reference".
   * 
   * @return translated "Client Reference"
  
   */
  @DefaultMessage("Client Reference")
  @Key("clntRef")
  String clntRef();

  /**
   * Translated "3. Close the window when done.".
   * 
   * @return translated "3. Close the window when done."
  
   */
  @DefaultMessage("3. Close the window when done.")
  @Key("closeVerifyWindow")
  String closeVerifyWindow();

  /**
   * Translated "4. Close the window when done.   ".
   * 
   * @return translated "4. Close the window when done.   "
  
   */
  @DefaultMessage("4. Close the window when done.   ")
  @Key("closeWindow")
  String closeWindow();

  /**
   * Translated "Compl-Rel".
   * 
   * @return translated "Compl-Rel"
  
   */
  @DefaultMessage("Compl-Rel")
  @Key("cmp-rel")
  String cmp_rel();

  /**
   * Translated "Code".
   * 
   * @return translated "Code"
  
   */
  @DefaultMessage("Code")
  @Key("code")
  String code();

  /**
   * Translated "Col-Rdy".
   * 
   * @return translated "Col-Rdy"
  
   */
  @DefaultMessage("Col-Rdy")
  @Key("col-rdy")
  String col_rdy();

  /**
   * Translated "Col-Rec".
   * 
   * @return translated "Col-Rec"
  
   */
  @DefaultMessage("Col-Rec")
  @Key("col-rec")
  String col_rec();

  /**
   * Translated "Col-Rel".
   * 
   * @return translated "Col-Rel"
  
   */
  @DefaultMessage("Col-Rel")
  @Key("col-rel")
  String col_rel();

  /**
   * Translated "Collapse".
   * 
   * @return translated "Collapse"
  
   */
  @DefaultMessage("Collapse")
  @Key("collapse")
  String collapse();

  /**
   * Translated "Collected".
   * 
   * @return translated "Collected"
  
   */
  @DefaultMessage("Collected")
  @Key("collected")
  String collected();

  /**
   * Translated "Collected date can''t be after entered".
   * 
   * @return translated "Collected date can''t be after entered"
  
   */
  @DefaultMessage("Collected date can''t be after entered")
  @Key("collectedDateAfterEnteredError")
  String collectedDateAfterEnteredError();

  /**
   * Translated "Collected date can''t be after received".
   * 
   * @return translated "Collected date can''t be after received"
  
   */
  @DefaultMessage("Collected date can''t be after received")
  @Key("collectedDateAfterReceivedError")
  String collectedDateAfterReceivedError();

  /**
   * Translated "This sample must have a collected date".
   * 
   * @return translated "This sample must have a collected date"
  
   */
  @DefaultMessage("This sample must have a collected date")
  @Key("collectedDateMissingWarning")
  String collectedDateMissingWarning();

  /**
   * Translated "Collection time can''t be specified without collection date".
   * 
   * @return translated "Collection time can''t be specified without collection date"
  
   */
  @DefaultMessage("Collection time can''t be specified without collection date")
  @Key("collectedTimeWithoutDateError")
  String collectedTimeWithoutDateError();

  /**
   * Translated "Collected date shouldn''t be more than 180 days before entered date".
   * 
   * @return translated "Collected date shouldn''t be more than 180 days before entered date"
  
   */
  @DefaultMessage("Collected date shouldn''t be more than 180 days before entered date")
  @Key("collectedTooOldWarning")
  String collectedTooOldWarning();

  /**
   * Translated "Collection Date".
   * 
   * @return translated "Collection Date"
  
   */
  @DefaultMessage("Collection Date")
  @Key("collectionDate")
  String collectionDate();

  /**
   * Translated "Collector".
   * 
   * @return translated "Collector"
  
   */
  @DefaultMessage("Collector")
  @Key("collector")
  String collector();

  /**
   * Translated "Collector/Organization Info".
   * 
   * @return translated "Collector/Organization Info"
  
   */
  @DefaultMessage("Collector/Organization Info")
  @Key("collectorOrgInfo")
  String collectorOrgInfo();

  /**
   * Translated "Column".
   * 
   * @return translated "Column"
  
   */
  @DefaultMessage("Column")
  @Key("column")
  String column();

  /**
   * Translated "No analyte found for position {0} analyte ''{1}'' column ''{2}''".
   * 
   * @param arg0 "{0}"
   * @param arg1 "{1}"
   * @param arg2 "{2}"
   * @return translated "No analyte found for position {0} analyte ''{1}'' column ''{2}''"
  
   */
  @DefaultMessage("No analyte found for position {0} analyte ''{1}'' column ''{2}''")
  @Key("columnAnalyteLookupFormException")
  String columnAnalyteLookupFormException(String arg0,  String arg1,  String arg2);

  /**
   * Translated "Column analytes must not be marked as Supplemental".
   * 
   * @return translated "Column analytes must not be marked as Supplemental"
  
   */
  @DefaultMessage("Column analytes must not be marked as Supplemental")
  @Key("columnAnalyteSupplException")
  String columnAnalyteSupplException();

  /**
   * Translated "Comment".
   * 
   * @return translated "Comment"
  
   */
  @DefaultMessage("Comment")
  @Key("comment")
  String comment();

  /**
   * Translated "Comments".
   * 
   * @return translated "Comments"
  
   */
  @DefaultMessage("Comments")
  @Key("comments")
  String comments();

  /**
   * Translated "Commit".
   * 
   * @return translated "Commit"
  
   */
  @DefaultMessage("Commit")
  @Key("commit")
  String commit();

  /**
   * Translated "There were errors with 1 or more records, please fix the errors and try again".
   * 
   * @return translated "There were errors with 1 or more records, please fix the errors and try again"
  
   */
  @DefaultMessage("There were errors with 1 or more records, please fix the errors and try again")
  @Key("commitErrors")
  String commitErrors();

  /**
   * Translated "Committing ...".
   * 
   * @return translated "Committing ..."
  
   */
  @DefaultMessage("Committing ...")
  @Key("commiting")
  String commiting();

  /**
   * Translated "Common".
   * 
   * @return translated "Common"
  
   */
  @DefaultMessage("Common")
  @Key("common")
  String common();

  /**
   * Translated "This component does not have the same store as this kit".
   * 
   * @return translated "This component does not have the same store as this kit"
  
   */
  @DefaultMessage("This component does not have the same store as this kit")
  @Key("compStoreNotSameAsKitStoreException")
  String compStoreNotSameAsKitStoreException();

  /**
   * Translated "Complete".
   * 
   * @return translated "Complete"
  
   */
  @DefaultMessage("Complete")
  @Key("complete")
  String complete();

  /**
   * Translated "Accession # {0,number,#0} : {1}, {2} has already been released".
   * 
   * @param arg0 "{0,number,#0}"
   * @param arg1 "{1}"
   * @param arg2 "{2}"
   * @return translated "Accession # {0,number,#0} : {1}, {2} has already been released"
  
   */
  @DefaultMessage("Accession # {0,number,#0} : {1}, {2} has already been released")
  @Key("completeRelease.alreadyReleased")
  String completeRelease_alreadyReleased(Integer arg0,  String arg1,  String arg2);

  /**
   * Translated "You can only update 1 row at a time".
   * 
   * @return translated "You can only update 1 row at a time"
  
   */
  @DefaultMessage("You can only update 1 row at a time")
  @Key("completeRelease.cantUpdateMultiple")
  String completeRelease_cantUpdateMultiple();

  /**
   * Translated "Complete and Release".
   * 
   * @return translated "Complete and Release"
  
   */
  @DefaultMessage("Complete and Release")
  @Key("completeRelease.completeAndRelease")
  String completeRelease_completeAndRelease();

  /**
   * Translated "You are about to complete {0,number,#0} records.\n\nPress OK to continue or Cancel to abort.".
   * 
   * @param arg0 "{0,number,#0}"
   * @return translated "You are about to complete {0,number,#0} records.\n\nPress OK to continue or Cancel to abort."
  
   */
  @DefaultMessage("You are about to complete {0,number,#0} records.\n\nPress OK to continue or Cancel to abort.")
  @Key("completeRelease.completeMultipleWarning")
  String completeRelease_completeMultipleWarning(Integer arg0);

  /**
   * Translated "Perform final review and release of analyses.".
   * 
   * @return translated "Perform final review and release of analyses."
  
   */
  @DefaultMessage("Perform final review and release of analyses.")
  @Key("completeRelease.description")
  String completeRelease_description();

  /**
   * Translated "This analysis has a status of ''{0}'', press Ok to complete or Cancel to abort.".
   * 
   * @param arg0 "{0}"
   * @return translated "This analysis has a status of ''{0}'', press Ok to complete or Cancel to abort."
  
   */
  @DefaultMessage("This analysis has a status of ''{0}'', press Ok to complete or Cancel to abort.")
  @Key("completeRelease.onHoldWarning")
  String completeRelease_onHoldWarning(String arg0);

  /**
   * Translated "Query By Worksheet".
   * 
   * @return translated "Query By Worksheet"
  
   */
  @DefaultMessage("Query By Worksheet")
  @Key("completeRelease.queryByWorksheet")
  String completeRelease_queryByWorksheet();

  /**
   * Translated "You are about to release {0,number,#0} records.\n\nPress OK to continue or Cancel to abort.".
   * 
   * @param arg0 "{0,number,#0}"
   * @return translated "You are about to release {0,number,#0} records.\n\nPress OK to continue or Cancel to abort."
  
   */
  @DefaultMessage("You are about to release {0,number,#0} records.\n\nPress OK to continue or Cancel to abort.")
  @Key("completeRelease.releaseMultipleWarning")
  String completeRelease_releaseMultipleWarning(Integer arg0);

  /**
   * Translated "Please select exactly one row to unrelease".
   * 
   * @return translated "Please select exactly one row to unrelease"
  
   */
  @DefaultMessage("Please select exactly one row to unrelease")
  @Key("completeRelease.selOneRowUnrelease")
  String completeRelease_selOneRowUnrelease();

  /**
   * Translated "Unrelease Analysis".
   * 
   * @return translated "Unrelease Analysis"
  
   */
  @DefaultMessage("Unrelease Analysis")
  @Key("completeRelease.unrelease")
  String completeRelease_unrelease();

  /**
   * Translated "You must add an internal note when sample/analysis is unreleased. \n\nPress Ok to continue or Cancel to abort.".
   * 
   * @return translated "You must add an internal note when sample/analysis is unreleased. \n\nPress Ok to continue or Cancel to abort."
  
   */
  @DefaultMessage("You must add an internal note when sample/analysis is unreleased. \n\nPress Ok to continue or Cancel to abort.")
  @Key("completeRelease.unreleaseMessage")
  String completeRelease_unreleaseMessage();

  /**
   * Translated "Unable to set {0} : {1} to completed - result for {2} : {3} is invalid ".
   * 
   * @param arg0 "{0}"
   * @param arg1 "{1}"
   * @param arg2 "{2}"
   * @param arg3 "{3}"
   * @return translated "Unable to set {0} : {1} to completed - result for {2} : {3} is invalid "
  
   */
  @DefaultMessage("Unable to set {0} : {1} to completed - result for {2} : {3} is invalid ")
  @Key("completeStatusInvalidResultsException")
  String completeStatusInvalidResultsException(String arg0,  String arg1,  String arg2,  String arg3);

  /**
   * Translated "Unable to set {0} : {1} to completed - result for {2} : {3} is required".
   * 
   * @param arg0 "{0}"
   * @param arg1 "{1}"
   * @param arg2 "{2}"
   * @param arg3 "{3}"
   * @return translated "Unable to set {0} : {1} to completed - result for {2} : {3} is required"
  
   */
  @DefaultMessage("Unable to set {0} : {1} to completed - result for {2} : {3} is required")
  @Key("completeStatusRequiredResultsException")
  String completeStatusRequiredResultsException(String arg0,  String arg1,  String arg2,  String arg3);

  /**
   * Translated "Status needs to be completed to release {0} : {1}".
   * 
   * @param arg0 "{0}"
   * @param arg1 "{1}"
   * @return translated "Status needs to be completed to release {0} : {1}"
  
   */
  @DefaultMessage("Status needs to be completed to release {0} : {1}")
  @Key("completeStatusRequiredToRelease")
  String completeStatusRequiredToRelease(String arg0,  String arg1);

  /**
   * Translated "Completed".
   * 
   * @return translated "Completed"
  
   */
  @DefaultMessage("Completed")
  @Key("completed")
  String completed();

  /**
   * Translated "Completed By".
   * 
   * @return translated "Completed By"
  
   */
  @DefaultMessage("Completed By")
  @Key("completedBy")
  String completedBy();

  /**
   * Translated "Completed Date".
   * 
   * @return translated "Completed Date"
  
   */
  @DefaultMessage("Completed Date")
  @Key("completedDate")
  String completedDate();

  /**
   * Translated "{0} : {1} - Completed date can''t be in the future".
   * 
   * @param arg0 "{0}"
   * @param arg1 "{1}"
   * @return translated "{0} : {1} - Completed date can''t be in the future"
  
   */
  @DefaultMessage("{0} : {1} - Completed date can''t be in the future")
  @Key("completedDateInFutureError")
  String completedDateInFutureError(String arg0,  String arg1);

  /**
   * Translated "Completed|Released".
   * 
   * @return translated "Completed|Released"
  
   */
  @DefaultMessage("Completed|Released")
  @Key("completedOrReleased")
  String completedOrReleased();

  /**
   * Translated "Component".
   * 
   * @return translated "Component"
  
   */
  @DefaultMessage("Component")
  @Key("component")
  String component();

  /**
   * Translated "Components".
   * 
   * @return translated "Components"
  
   */
  @DefaultMessage("Components")
  @Key("components")
  String components();

  /**
   * Translated "Ctmt. Lvl. ".
   * 
   * @return translated "Ctmt. Lvl. "
  
   */
  @DefaultMessage("Ctmt. Lvl. ")
  @Key("contLevel")
  String contLevel();

  /**
   * Translated "Contact".
   * 
   * @return translated "Contact"
  
   */
  @DefaultMessage("Contact")
  @Key("contact")
  String contact();

  /**
   * Translated "Contact Name".
   * 
   * @return translated "Contact Name"
  
   */
  @DefaultMessage("Contact Name")
  @Key("contactName")
  String contactName();

  /**
   * Translated "Contacts for this Organization".
   * 
   * @return translated "Contacts for this Organization"
  
   */
  @DefaultMessage("Contacts for this Organization")
  @Key("contactsForThisOrganization")
  String contactsForThisOrganization();

  /**
   * Translated "Container".
   * 
   * @return translated "Container"
  
   */
  @DefaultMessage("Container")
  @Key("container")
  String container();

  /**
   * Translated "Container Reference".
   * 
   * @return translated "Container Reference"
  
   */
  @DefaultMessage("Container Reference")
  @Key("containerReference")
  String containerReference();

  /**
   * Translated "Controls/Parameters".
   * 
   * @return translated "Controls/Parameters"
  
   */
  @DefaultMessage("Controls/Parameters")
  @Key("controlsParameters")
  String controlsParameters();

  /**
   * Translated "Copy".
   * 
   * @return translated "Copy"
  
   */
  @DefaultMessage("Copy")
  @Key("copy")
  String copy();

  /**
   * Translated "Copy To All".
   * 
   * @return translated "Copy To All"
  
   */
  @DefaultMessage("Copy To All")
  @Key("copyToAll")
  String copyToAll();

  /**
   * Translated "Copy To Empty".
   * 
   * @return translated "Copy To Empty"
  
   */
  @DefaultMessage("Copy To Empty")
  @Key("copyToEmpty")
  String copyToEmpty();

  /**
   * Translated "Please correct the errors indicated, then press Commit".
   * 
   * @return translated "Please correct the errors indicated, then press Commit"
  
   */
  @DefaultMessage("Please correct the errors indicated, then press Commit")
  @Key("correctErrors")
  String correctErrors();

  /**
   * Translated "Please correct the errors indicated, then press Process".
   * 
   * @return translated "Please correct the errors indicated, then press Process"
  
   */
  @DefaultMessage("Please correct the errors indicated, then press Process")
  @Key("correctErrorsProcess")
  String correctErrorsProcess();

  /**
   * Translated "Cost".
   * 
   * @return translated "Cost"
  
   */
  @DefaultMessage("Cost")
  @Key("cost")
  String cost();

  /**
   * Translated "Cost Center".
   * 
   * @return translated "Cost Center"
  
   */
  @DefaultMessage("Cost Center")
  @Key("costCenter")
  String costCenter();

  /**
   * Translated "Country".
   * 
   * @return translated "Country"
  
   */
  @DefaultMessage("Country")
  @Key("country")
  String country();

  /**
   * Translated "County".
   * 
   * @return translated "County"
  
   */
  @DefaultMessage("County")
  @Key("county")
  String county();

  /**
   * Translated "Created Date".
   * 
   * @return translated "Created Date"
  
   */
  @DefaultMessage("Created Date")
  @Key("created")
  String created();

  /**
   * Translated "Creation Date".
   * 
   * @return translated "Creation Date"
  
   */
  @DefaultMessage("Creation Date")
  @Key("creationDate")
  String creationDate();

  /**
   * Translated "Cron".
   * 
   * @return translated "Cron"
  
   */
  @DefaultMessage("Cron")
  @Key("cron")
  String cron();

  /**
   * Translated "Schedule Cron jobs to be run on the server.".
   * 
   * @return translated "Schedule Cron jobs to be run on the server."
  
   */
  @DefaultMessage("Schedule Cron jobs to be run on the server.")
  @Key("cronDescription")
  String cronDescription();

  /**
   * Translated "History - Cron".
   * 
   * @return translated "History - Cron"
  
   */
  @DefaultMessage("History - Cron")
  @Key("cronHistory")
  String cronHistory();

  /**
   * Translated "Cron Tab".
   * 
   * @return translated "Cron Tab"
  
   */
  @DefaultMessage("Cron Tab")
  @Key("cronTab")
  String cronTab();

  /**
   * Translated "Current".
   * 
   * @return translated "Current"
  
   */
  @DefaultMessage("Current")
  @Key("current")
  String current();

  /**
   * Translated "Current Date/Time".
   * 
   * @return translated "Current Date/Time"
  
   */
  @DefaultMessage("Current Date/Time")
  @Key("currentDateTime")
  String currentDateTime();

  /**
   * Translated "Customer".
   * 
   * @return translated "Customer"
  
   */
  @DefaultMessage("Customer")
  @Key("customer")
  String customer();

  /**
   * Translated "Customer Notes".
   * 
   * @return translated "Customer Notes"
  
   */
  @DefaultMessage("Customer Notes")
  @Key("customerNotes")
  String customerNotes();

  /**
   * Translated "Cut".
   * 
   * @return translated "Cut"
  
   */
  @DefaultMessage("Cut")
  @Key("cut")
  String cut();

  /**
   * Translated "Data Exchange ''{0}'': Successfully sent messages".
   * 
   * @param arg0 "{0}"
   * @return translated "Data Exchange ''{0}'': Successfully sent messages"
  
   */
  @DefaultMessage("Data Exchange ''{0}'': Successfully sent messages")
  @Key("dataExchange.executedCriteria")
  String dataExchange_executedCriteria(String arg0);

  /**
   * Translated "Data Exchange ''{0}'': Could not find the last run date ".
   * 
   * @param arg0 "{0}"
   * @return translated "Data Exchange ''{0}'': Could not find the last run date "
  
   */
  @DefaultMessage("Data Exchange ''{0}'': Could not find the last run date ")
  @Key("dataExchange.lastRunFetchException")
  String dataExchange_lastRunFetchException(String arg0);

  /**
   * Translated "Data Exchange: You must specify 1 or more accession numbers to message".
   * 
   * @return translated "Data Exchange: You must specify 1 or more accession numbers to message"
  
   */
  @DefaultMessage("Data Exchange: You must specify 1 or more accession numbers to message")
  @Key("dataExchange.noAccessionException")
  String dataExchange_noAccessionException();

  /**
   * Translated "Data Exchange ''{0}'': Could not find criteria".
   * 
   * @param arg0 "{0}"
   * @return translated "Data Exchange ''{0}'': Could not find criteria"
  
   */
  @DefaultMessage("Data Exchange ''{0}'': Could not find criteria")
  @Key("dataExchange.noCriteriaFoundException")
  String dataExchange_noCriteriaFoundException(String arg0);

  /**
   * Translated "Data Exchange ''{0}'': Query not specified".
   * 
   * @param arg0 "{0}"
   * @return translated "Data Exchange ''{0}'': Query not specified"
  
   */
  @DefaultMessage("Data Exchange ''{0}'': Query not specified")
  @Key("dataExchange.noQueryException")
  String dataExchange_noQueryException(String arg0);

  /**
   * Translated "Data Exchange ''{0}'': No samples found for messaging".
   * 
   * @param arg0 "{0}"
   * @return translated "Data Exchange ''{0}'': No samples found for messaging"
  
   */
  @DefaultMessage("Data Exchange ''{0}'': No samples found for messaging")
  @Key("dataExchange.noSamplesFound")
  String dataExchange_noSamplesFound(String arg0);

  /**
   * Translated "Data Exchange: You must specify a URI export location".
   * 
   * @return translated "Data Exchange: You must specify a URI export location"
  
   */
  @DefaultMessage("Data Exchange: You must specify a URI export location")
  @Key("dataExchange.noUriException")
  String dataExchange_noUriException();

  /**
   * Translated "Data View".
   * 
   * @return translated "Data View"
  
   */
  @DefaultMessage("Data View")
  @Key("dataView")
  String dataView();

  /**
   * Translated "Date Collected".
   * 
   * @return translated "Date Collected"
  
   */
  @DefaultMessage("Date Collected")
  @Key("dateCollected")
  String dateCollected();

  /**
   * Translated "yyyyMMdd".
   * 
   * @return translated "yyyyMMdd"
  
   */
  @DefaultMessage("yyyyMMdd")
  @Key("dateCompressedPattern")
  String dateCompressedPattern();

  /**
   * Translated "yyyy-MM-dd".
   * 
   * @return translated "yyyy-MM-dd"
  
   */
  @DefaultMessage("yyyy-MM-dd")
  @Key("datePattern")
  String datePattern();

  /**
   * Translated "Date Rec".
   * 
   * @return translated "Date Rec"
  
   */
  @DefaultMessage("Date Rec")
  @Key("dateRec")
  String dateRec();

  /**
   * Translated "yyyyMMddHHmm".
   * 
   * @return translated "yyyyMMddHHmm"
  
   */
  @DefaultMessage("yyyyMMddHHmm")
  @Key("dateTimeCompressedPattern")
  String dateTimeCompressedPattern();

  /**
   * Translated "yyyy-MM-dd HH:mm".
   * 
   * @return translated "yyyy-MM-dd HH:mm"
  
   */
  @DefaultMessage("yyyy-MM-dd HH:mm")
  @Key("dateTimePattern")
  String dateTimePattern();

  /**
   * Translated "yyyy-MM-dd HH:mm:ss".
   * 
   * @return translated "yyyy-MM-dd HH:mm:ss"
  
   */
  @DefaultMessage("yyyy-MM-dd HH:mm:ss")
  @Key("dateTimeSecondPattern")
  String dateTimeSecondPattern();

  /**
   * Translated "Valid Begin and End dates, Frequency and Unit must be specified to show dates".
   * 
   * @return translated "Valid Begin and End dates, Frequency and Unit must be specified to show dates"
  
   */
  @DefaultMessage("Valid Begin and End dates, Frequency and Unit must be specified to show dates")
  @Key("datesFreqUnitNotSpec")
  String datesFreqUnitNotSpec();

  /**
   * Translated "EEE MMM d, yyyy".
   * 
   * @return translated "EEE MMM d, yyyy"
  
   */
  @DefaultMessage("EEE MMM d, yyyy")
  @Key("dayInYearPattern")
  String dayInYearPattern();

  /**
   * Translated "Days".
   * 
   * @return translated "Days"
  
   */
  @DefaultMessage("Days")
  @Key("days")
  String days();

  /**
   * Translated "Days in Initiated".
   * 
   * @return translated "Days in Initiated"
  
   */
  @DefaultMessage("Days in Initiated")
  @Key("daysInInitiated")
  String daysInInitiated();

  /**
   * Translated "Error loading default user for position {0} analysis {1}".
   * 
   * @param arg0 "{0}"
   * @param arg1 "{1}"
   * @return translated "Error loading default user for position {0} analysis {1}"
  
   */
  @DefaultMessage("Error loading default user for position {0} analysis {1}")
  @Key("defaultWorksheetQcUserFormException")
  String defaultWorksheetQcUserFormException(String arg0,  String arg1);

  /**
   * Translated "Delete".
   * 
   * @return translated "Delete"
  
   */
  @DefaultMessage("Delete")
  @Key("delete")
  String delete();

  /**
   * Translated "Delete Aborted".
   * 
   * @return translated "Delete Aborted"
  
   */
  @DefaultMessage("Delete Aborted")
  @Key("deleteAborted")
  String deleteAborted();

  /**
   * Translated "Delete...Complete".
   * 
   * @return translated "Delete...Complete"
  
   */
  @DefaultMessage("Delete...Complete")
  @Key("deleteComplete")
  String deleteComplete();

  /**
   * Translated "Pressing commit will delete the current record from the database".
   * 
   * @return translated "Pressing commit will delete the current record from the database"
  
   */
  @DefaultMessage("Pressing commit will delete the current record from the database")
  @Key("deleteMessage")
  String deleteMessage();

  /**
   * Translated "Deleting...".
   * 
   * @return translated "Deleting..."
  
   */
  @DefaultMessage("Deleting...")
  @Key("deleting")
  String deleting();

  /**
   * Translated "Desc".
   * 
   * @return translated "Desc"
  
   */
  @DefaultMessage("Desc")
  @Key("desc")
  String desc();

  /**
   * Translated "Description".
   * 
   * @return translated "Description"
  
   */
  @DefaultMessage("Description")
  @Key("description")
  String description();

  /**
   * Translated "Method Description".
   * 
   * @return translated "Method Description"
  
   */
  @DefaultMessage("Method Description")
  @Key("descriptionOfTheMethod")
  String descriptionOfTheMethod();

  /**
   * Translated "Test Description".
   * 
   * @return translated "Test Description"
  
   */
  @DefaultMessage("Test Description")
  @Key("descriptionOfTheTest")
  String descriptionOfTheTest();

  /**
   * Translated "The destination URI must begin with either ''file://'' or ''socket://''".
   * 
   * @return translated "The destination URI must begin with either ''file://'' or ''socket://''"
  
   */
  @DefaultMessage("The destination URI must begin with either ''file://'' or ''socket://''")
  @Key("destURIMustHaveFileOrSocketException")
  String destURIMustHaveFileOrSocketException();

  /**
   * Translated "Destination".
   * 
   * @return translated "Destination"
  
   */
  @DefaultMessage("Destination")
  @Key("destination")
  String destination();

  /**
   * Translated "Destination URI".
   * 
   * @return translated "Destination URI"
  
   */
  @DefaultMessage("Destination URI")
  @Key("destinationUri")
  String destinationUri();

  /**
   * Translated "Detail:".
   * 
   * @return translated "Detail:"
  
   */
  @DefaultMessage("Detail:")
  @Key("detail")
  String detail();

  /**
   * Translated "Details".
   * 
   * @return translated "Details"
  
   */
  @DefaultMessage("Details")
  @Key("details")
  String details();

  /**
   * Translated "Dictionary".
   * 
   * @return translated "Dictionary"
  
   */
  @DefaultMessage("Dictionary")
  @Key("dictionary")
  String dictionary();

  /**
   * Translated "One or more dictionary entries cannot be deleted, other entries are linked to them".
   * 
   * @return translated "One or more dictionary entries cannot be deleted, other entries are linked to them"
  
   */
  @DefaultMessage("One or more dictionary entries cannot be deleted, other entries are linked to them")
  @Key("dictionaryDeleteException")
  String dictionaryDeleteException();

  /**
   * Translated "Define values that can be used to complete information in the OpenELIS system.".
   * 
   * @return translated "Define values that can be used to complete information in the OpenELIS system."
  
   */
  @DefaultMessage("Define values that can be used to complete information in the OpenELIS system.")
  @Key("dictionaryDescription")
  String dictionaryDescription();

  /**
   * Translated "Dictionary Entry".
   * 
   * @return translated "Dictionary Entry"
  
   */
  @DefaultMessage("Dictionary Entry")
  @Key("dictionaryEntry")
  String dictionaryEntry();

  /**
   * Translated "Dictionary Entry Selection ".
   * 
   * @return translated "Dictionary Entry Selection "
  
   */
  @DefaultMessage("Dictionary Entry Selection ")
  @Key("dictionaryEntrySelection")
  String dictionaryEntrySelection();

  /**
   * Translated "History - Dictionary".
   * 
   * @return translated "History - Dictionary"
  
   */
  @DefaultMessage("History - Dictionary")
  @Key("dictionaryHistory")
  String dictionaryHistory();

  /**
   * Translated "Discard ".
   * 
   * @return translated "Discard "
  
   */
  @DefaultMessage("Discard ")
  @Key("discard")
  String discard();

  /**
   * Translated "Dispensed Units".
   * 
   * @return translated "Dispensed Units"
  
   */
  @DefaultMessage("Dispensed Units")
  @Key("dispensedUnits")
  String dispensedUnits();

  /**
   * Translated "''$''###,###,##0.00;(''$''###,###,##0.00)".
   * 
   * @return translated "''$''###,###,##0.00;(''$''###,###,##0.00)"
  
   */
  @DefaultMessage("''$''###,###,##0.00;(''$''###,###,##0.00)")
  @Key("displayCurrencyFormat")
  String displayCurrencyFormat();

  /**
   * Translated "###.######".
   * 
   * @return translated "###.######"
  
   */
  @DefaultMessage("###.######")
  @Key("displayDoubleFormat")
  String displayDoubleFormat();

  /**
   * Translated "Do Not Inventory".
   * 
   * @return translated "Do Not Inventory"
  
   */
  @DefaultMessage("Do Not Inventory")
  @Key("doNotInventory")
  String doNotInventory();

  /**
   * Translated "Domain".
   * 
   * @return translated "Domain"
  
   */
  @DefaultMessage("Domain")
  @Key("domain")
  String domain();

  /**
   * Translated "Domain Specific Field".
   * 
   * @return translated "Domain Specific Field"
  
   */
  @DefaultMessage("Domain Specific Field")
  @Key("domainSpecField")
  String domainSpecField();

  /**
   * Translated "Due".
   * 
   * @return translated "Due"
  
   */
  @DefaultMessage("Due")
  @Key("due")
  String due();

  /**
   * Translated "The position subsequent to a position of type Fixed cannot be of the Duplicate type".
   * 
   * @return translated "The position subsequent to a position of type Fixed cannot be of the Duplicate type"
  
   */
  @DefaultMessage("The position subsequent to a position of type Fixed cannot be of the Duplicate type")
  @Key("duplPosAfterFixedPosException")
  String duplPosAfterFixedPosException();

  /**
   * Translated "A vendor order must not have the same inventory item more than once  ".
   * 
   * @return translated "A vendor order must not have the same inventory item more than once  "
  
   */
  @DefaultMessage("A vendor order must not have the same inventory item more than once  ")
  @Key("duplicateInvItemVendorOrderException")
  String duplicateInvItemVendorOrderException();

  /**
   * Translated "At the most one QC can be associated with any position".
   * 
   * @return translated "At the most one QC can be associated with any position"
  
   */
  @DefaultMessage("At the most one QC can be associated with any position")
  @Key("duplicatePosForQCsException")
  String duplicatePosForQCsException();

  /**
   * Translated "Duplicate".
   * 
   * @return translated "Duplicate"
  
   */
  @DefaultMessage("Duplicate")
  @Key("duplicateRecord")
  String duplicateRecord();

  /**
   * Translated "Duplicate the current record".
   * 
   * @return translated "Duplicate the current record"
  
   */
  @DefaultMessage("Duplicate the current record")
  @Key("duplicateRecordDescription")
  String duplicateRecordDescription();

  /**
   * Translated "Analytes included on a worksheet for a test must be unique".
   * 
   * @return translated "Analytes included on a worksheet for a test must be unique"
  
   */
  @DefaultMessage("Analytes included on a worksheet for a test must be unique")
  @Key("duplicateWSAnalyteException")
  String duplicateWSAnalyteException();

  /**
   * Translated "Dynamically".
   * 
   * @return translated "Dynamically"
  
   */
  @DefaultMessage("Dynamically")
  @Key("dynamic")
  String dynamic();

  /**
   * Translated "Edit".
   * 
   * @return translated "Edit"
  
   */
  @DefaultMessage("Edit")
  @Key("edit")
  String edit();

  /**
   * Translated "Edit Note".
   * 
   * @return translated "Edit Note"
  
   */
  @DefaultMessage("Edit Note")
  @Key("editNote")
  String editNote();

  /**
   * Translated "Changing entries from the past may cause inconsistencies in the data ".
   * 
   * @return translated "Changing entries from the past may cause inconsistencies in the data "
  
   */
  @DefaultMessage("Changing entries from the past may cause inconsistencies in the data ")
  @Key("editPreviousWarning")
  String editPreviousWarning();

  /**
   * Translated "Save To Excel File".
   * 
   * @return translated "Save To Excel File"
  
   */
  @DefaultMessage("Save To Excel File")
  @Key("editWorksheet")
  String editWorksheet();

  /**
   * Translated "########0.00;-########0.00".
   * 
   * @return translated "########0.00;-########0.00"
  
   */
  @DefaultMessage("########0.00;-########0.00")
  @Key("editorCurrencyFormat")
  String editorCurrencyFormat();

  /**
   * Translated "Effective Begin".
   * 
   * @return translated "Effective Begin"
  
   */
  @DefaultMessage("Effective Begin")
  @Key("effectiveBegin")
  String effectiveBegin();

  /**
   * Translated "Effective End".
   * 
   * @return translated "Effective End"
  
   */
  @DefaultMessage("Effective End")
  @Key("effectiveEnd")
  String effectiveEnd();

  /**
   * Translated "8 - 10 days ago".
   * 
   * @return translated "8 - 10 days ago"
  
   */
  @DefaultMessage("8 - 10 days ago")
  @Key("eightToTenDays")
  String eightToTenDays();

  /**
   * Translated "11-20 days ago".
   * 
   * @return translated "11-20 days ago"
  
   */
  @DefaultMessage("11-20 days ago")
  @Key("elevnToTwntyDays")
  String elevnToTwntyDays();

  /**
   * Translated "Email".
   * 
   * @return translated "Email"
  
   */
  @DefaultMessage("Email")
  @Key("email")
  String email();

  /**
   * Translated "You may not execute an empty query".
   * 
   * @return translated "You may not execute an empty query"
  
   */
  @DefaultMessage("You may not execute an empty query")
  @Key("emptyQueryException")
  String emptyQueryException();

  /**
   * Translated "One or more result groups are empty; Result groups must have one or more results".
   * 
   * @return translated "One or more result groups are empty; Result groups must have one or more results"
  
   */
  @DefaultMessage("One or more result groups are empty; Result groups must have one or more results")
  @Key("emptyResultGroupException")
  String emptyResultGroupException();

  /**
   * Translated "End".
   * 
   * @return translated "End"
  
   */
  @DefaultMessage("End")
  @Key("end")
  String end();

  /**
   * Translated "End Date".
   * 
   * @return translated "End Date"
  
   */
  @DefaultMessage("End Date")
  @Key("endDate")
  String endDate();

  /**
   * Translated "End date must not be before begin date".
   * 
   * @return translated "End date must not be before begin date"
  
   */
  @DefaultMessage("End date must not be before begin date")
  @Key("endDateAfterBeginDateException")
  String endDateAfterBeginDateException();

  /**
   * Translated "End date must be after today".
   * 
   * @return translated "End date must be after today"
  
   */
  @DefaultMessage("End date must be after today")
  @Key("endDateAfterToday")
  String endDateAfterToday();

  /**
   * Translated "End date must be after begin date  ".
   * 
   * @return translated "End date must be after begin date  "
  
   */
  @DefaultMessage("End date must be after begin date  ")
  @Key("endDateInvalidException")
  String endDateInvalidException();

  /**
   * Translated "End date must be after begin date for analyte ''{0}''  ".
   * 
   * @param arg0 "{0}"
   * @return translated "End date must be after begin date for analyte ''{0}''  "
  
   */
  @DefaultMessage("End date must be after begin date for analyte ''{0}''  ")
  @Key("endDateInvalidWithParamException")
  String endDateInvalidWithParamException(String arg0);

  /**
   * Translated "End date is required for analyte ''{0}''".
   * 
   * @param arg0 "{0}"
   * @return translated "End date is required for analyte ''{0}''"
  
   */
  @DefaultMessage("End date is required for analyte ''{0}''")
  @Key("endDateRequiredForAnalyteException")
  String endDateRequiredForAnalyteException(String arg0);

  /**
   * Translated "End Day".
   * 
   * @return translated "End Day"
  
   */
  @DefaultMessage("End Day")
  @Key("endDay")
  String endDay();

  /**
   * Translated "Ending Released Date".
   * 
   * @return translated "Ending Released Date"
  
   */
  @DefaultMessage("Ending Released Date")
  @Key("endRelDt")
  String endRelDt();

  /**
   * Translated "You are at the end of your query results".
   * 
   * @return translated "You are at the end of your query results"
  
   */
  @DefaultMessage("You are at the end of your query results")
  @Key("endingQueryException")
  String endingQueryException();

  /**
   * Translated "The accession number must be entered before loading an order".
   * 
   * @return translated "The accession number must be entered before loading an order"
  
   */
  @DefaultMessage("The accession number must be entered before loading an order")
  @Key("enterAccNumBeforeOrderLoad")
  String enterAccNumBeforeOrderLoad();

  /**
   * Translated "Please enter an email or press Cancel".
   * 
   * @return translated "Please enter an email or press Cancel"
  
   */
  @DefaultMessage("Please enter an email or press Cancel")
  @Key("enterEmailOrCancel")
  String enterEmailOrCancel();

  /**
   * Translated "Enter fields to query by then press Commit".
   * 
   * @return translated "Enter fields to query by then press Commit"
  
   */
  @DefaultMessage("Enter fields to query by then press Commit")
  @Key("enterFieldsToQuery")
  String enterFieldsToQuery();

  /**
   * Translated "Enter information in the fields, then press Commit".
   * 
   * @return translated "Enter information in the fields, then press Commit"
  
   */
  @DefaultMessage("Enter information in the fields, then press Commit")
  @Key("enterInformationPressCommit")
  String enterInformationPressCommit();

  /**
   * Translated "Enter Search".
   * 
   * @return translated "Enter Search"
  
   */
  @DefaultMessage("Enter Search")
  @Key("enterSearch")
  String enterSearch();

  /**
   * Translated "A shipping barcode must be entered before a tracking number".
   * 
   * @return translated "A shipping barcode must be entered before a tracking number"
  
   */
  @DefaultMessage("A shipping barcode must be entered before a tracking number")
  @Key("enterShippingBeforeTracking")
  String enterShippingBeforeTracking();

  /**
   * Translated "Please enter a valid barcode ".
   * 
   * @return translated "Please enter a valid barcode "
  
   */
  @DefaultMessage("Please enter a valid barcode ")
  @Key("enterValidBarcode")
  String enterValidBarcode();

  /**
   * Translated "Entered".
   * 
   * @return translated "Entered"
  
   */
  @DefaultMessage("Entered")
  @Key("entered")
  String entered();

  /**
   * Translated "Entered Date".
   * 
   * @return translated "Entered Date"
  
   */
  @DefaultMessage("Entered Date")
  @Key("enteredDate")
  String enteredDate();

  /**
   * Translated "This record is locked by {0} until {1}".
   * 
   * @param arg0 "{0}"
   * @param arg1 "{1}"
   * @return translated "This record is locked by {0} until {1}"
  
   */
  @DefaultMessage("This record is locked by {0} until {1}")
  @Key("entityLockException")
  String entityLockException(String arg0,  String arg1);

  /**
   * Translated "Dictionary Entries".
   * 
   * @return translated "Dictionary Entries"
  
   */
  @DefaultMessage("Dictionary Entries")
  @Key("entries")
  String entries();

  /**
   * Translated "Entry".
   * 
   * @return translated "Entry"
  
   */
  @DefaultMessage("Entry")
  @Key("entry")
  String entry();

  /**
   * Translated "Environmental Collector".
   * 
   * @return translated "Environmental Collector"
  
   */
  @DefaultMessage("Environmental Collector")
  @Key("env.collector")
  String env_collector();

  /**
   * Translated "Environmental Location".
   * 
   * @return translated "Environmental Location"
  
   */
  @DefaultMessage("Environmental Location")
  @Key("env.location")
  String env_location();

  /**
   * Translated "Environmental Info".
   * 
   * @return translated "Environmental Info"
  
   */
  @DefaultMessage("Environmental Info")
  @Key("envInfo")
  String envInfo();

  /**
   * Translated "Environment".
   * 
   * @return translated "Environment"
  
   */
  @DefaultMessage("Environment")
  @Key("environment")
  String environment();

  /**
   * Translated "Environmental".
   * 
   * @return translated "Environmental"
  
   */
  @DefaultMessage("Environmental")
  @Key("environmental")
  String environmental();

  /**
   * Translated "Environmental Final Report".
   * 
   * @return translated "Environmental Final Report"
  
   */
  @DefaultMessage("Environmental Final Report")
  @Key("environmentalFinalReport")
  String environmentalFinalReport();

  /**
   * Translated "Environmental Results by Analyte".
   * 
   * @return translated "Environmental Results by Analyte"
  
   */
  @DefaultMessage("Environmental Results by Analyte")
  @Key("environmentalResultByAnalyte")
  String environmentalResultByAnalyte();

  /**
   * Translated "Environmental Sample".
   * 
   * @return translated "Environmental Sample"
  
   */
  @DefaultMessage("Environmental Sample")
  @Key("environmentalSample")
  String environmentalSample();

  /**
   * Translated "Environmental Sample Login".
   * 
   * @return translated "Environmental Sample Login"
  
   */
  @DefaultMessage("Environmental Sample Login")
  @Key("environmentalSampleLogin")
  String environmentalSampleLogin();

  /**
   * Translated "Fully login environmental sample and analysis related information.".
   * 
   * @return translated "Fully login environmental sample and analysis related information."
  
   */
  @DefaultMessage("Fully login environmental sample and analysis related information.")
  @Key("environmentalSampleLoginDescription")
  String environmentalSampleLoginDescription();

  /**
   * Translated "EOrder cannot be deleted, one or more samples are still linked to it".
   * 
   * @return translated "EOrder cannot be deleted, one or more samples are still linked to it"
  
   */
  @DefaultMessage("EOrder cannot be deleted, one or more samples are still linked to it")
  @Key("eorder.deleteException")
  String eorder_deleteException();

  /**
   * Translated "EOrder Lookup".
   * 
   * @return translated "EOrder Lookup"
  
   */
  @DefaultMessage("EOrder Lookup")
  @Key("eorder.eorderLookup")
  String eorder_eorderLookup();

  /**
   * Translated "This order is incomplete. It has no sample information.".
   * 
   * @return translated "This order is incomplete. It has no sample information."
   */
  @DefaultMessage("This order is incomplete. It has no sample information.")
  @Key("eorder.noSampleDataException")
  String eorder_noSampleDataException();

  /**
   * Translated "Order Number".
   * 
   * @return translated "Order Number"
  
   */
  @DefaultMessage("Order Number")
  @Key("eorder.orderNumber")
  String eorder_orderNumber();

  /**
   * Translated "System failure when looking up dictionary entry for mapping ''{0}''.".
   * 
   * @param arg0 "{0}"
   * @return translated "System failure when looking up dictionary entry for mapping ''{0}''."
  
   */
  @DefaultMessage("System failure when looking up dictionary entry for mapping ''{0}''.")
  @Key("eorderImport.dictionaryLookupFailure")
  String eorderImport_dictionaryLookupFailure(String arg0);

  /**
   * Translated "Dictionary entry not found for mapping ''{0}''.".
   * 
   * @param arg0 "{0}"
   * @return translated "Dictionary entry not found for mapping ''{0}''."
  
   */
  @DefaultMessage("Dictionary entry not found for mapping ''{0}''.")
  @Key("eorderImport.dictionaryNotFound")
  String eorderImport_dictionaryNotFound(String arg0);

  /**
   * Translated "Error loading result formatter for test Id: {0}.".
   * 
   * @param arg0 "{0}"
   * @return translated "Error loading result formatter for test Id: {0}."
  
   */
  @DefaultMessage("Error loading result formatter for test Id: {0}.")
  @Key("eorderImport.errorLoadingResultFormatter")
  String eorderImport_errorLoadingResultFormatter(String arg0);

  /**
   * Translated "The organization ''{1} ({2})'' for code ''{0}'' is inactive.".
   * 
   * @param arg0 "{0}"
   * @param arg1 "{1}"
   * @param arg2 "{2}"
   * @return translated "The organization ''{1} ({2})'' for code ''{0}'' is inactive."
  
   */
  @DefaultMessage("The organization ''{1} ({2})'' for code ''{0}'' is inactive.")
  @Key("eorderImport.inactiveOrgWarning")
  String eorderImport_inactiveOrgWarning(String arg0,  String arg1,  String arg2);

  /**
   * Translated "Invalid value ''{0}'' for test ''{1}, {2}'' analyte ''{3}'' in order.".
   * 
   * @param arg0 "{0}"
   * @param arg1 "{1}"
   * @param arg2 "{2}"
   * @param arg3 "{3}"
   * @return translated "Invalid value ''{0}'' for test ''{1}, {2}'' analyte ''{3}'' in order."
  
   */
  @DefaultMessage("Invalid value ''{0}'' for test ''{1}, {2}'' analyte ''{3}'' in order.")
  @Key("eorderImport.invalidValue")
  String eorderImport_invalidValue(String arg0,  String arg1,  String arg2,  String arg3);

  /**
   * Translated "System failure when looking up terms for the value ''{0}'' in the order field ''{1}''.".
   * 
   * @param arg0 "{0}"
   * @param arg1 "{1}"
   * @return translated "System failure when looking up terms for the value ''{0}'' in the order field ''{1}''."
  
   */
  @DefaultMessage("System failure when looking up terms for the value ''{0}'' in the order field ''{1}''.")
  @Key("eorderImport.localTermLookupFailure")
  String eorderImport_localTermLookupFailure(String arg0,  String arg1);

  /**
   * Translated "No terms were found for the value ''{0}'' in the order field ''{1}''.".
   * 
   * @param arg0 "{0}"
   * @param arg1 "{1}"
   * @return translated "No terms were found for the value ''{0}'' in the order field ''{1}''."
  
   */
  @DefaultMessage("No terms were found for the value ''{0}'' in the order field ''{1}''.")
  @Key("eorderImport.localTermNotFound")
  String eorderImport_localTermNotFound(String arg0,  String arg1);

  /**
   * Translated "Path to eorder import file directory is missing. Please contact the system administrator.".
   * 
   * @return translated "Path to eorder import file directory is missing. Please contact the system administrator."
  
   */
  @DefaultMessage("Path to eorder import file directory is missing. Please contact the system administrator.")
  @Key("eorderImport.missingPath")
  String eorderImport_missingPath();

  /**
   * Translated "Multliple patients were found matching data in the order; please use the field search button to choose the appropriate patient.".
   * 
   * @return translated "Multliple patients were found matching data in the order; please use the field search button to choose the appropriate patient."
  
   */
  @DefaultMessage("Multliple patients were found matching data in the order; please use the field search button to choose the appropriate patient.")
  @Key("eorderImport.mulitpleMatchPatients")
  String eorderImport_mulitpleMatchPatients();

  /**
   * Translated "Multiple terms were found for the value ''{0}'' in the order field ''{1}''.".
   * 
   * @param arg0 "{0}"
   * @param arg1 "{1}"
   * @return translated "Multiple terms were found for the value ''{0}'' in the order field ''{1}''."
  
   */
  @DefaultMessage("Multiple terms were found for the value ''{0}'' in the order field ''{1}''.")
  @Key("eorderImport.multipleLocalTerms")
  String eorderImport_multipleLocalTerms(String arg0,  String arg1);

  /**
   * Translated "Note From EOrder".
   * 
   * @return translated "Note From EOrder"
  
   */
  @DefaultMessage("Note From EOrder")
  @Key("eorderImport.orderNoteSubject")
  String eorderImport_orderNoteSubject();

  /**
   * Translated "The organization name was not supplied in the order.".
   * 
   * @return translated "The organization name was not supplied in the order."
  
   */
  @DefaultMessage("The organization name was not supplied in the order.")
  @Key("eorderImport.organizationNotSupplied")
  String eorderImport_organizationNotSupplied();

  /**
   * Translated "System failure when looking up existing patient records for matching against order data.".
   * 
   * @return translated "System failure when looking up existing patient records for matching against order data."
  
   */
  @DefaultMessage("System failure when looking up existing patient records for matching against order data.")
  @Key("eorderImport.patientLookupFailure")
  String eorderImport_patientLookupFailure();

  /**
   * Translated "Source of Sample ''{0}'' does not match source specified in order ''{1}''".
   * 
   * @param arg0 "{0}"
   * @param arg1 "{1}"
   * @return translated "Source of Sample ''{0}'' does not match source specified in order ''{1}''"
  
   */
  @DefaultMessage("Source of Sample ''{0}'' does not match source specified in order ''{1}''")
  @Key("eorderImport.sourceOfSampleMismatch")
  String eorderImport_sourceOfSampleMismatch(String arg0,  String arg1);

  /**
   * Translated "Source Other ''{0}'' does not match source other specified in order ''{1}''".
   * 
   * @param arg0 "{0}"
   * @param arg1 "{1}"
   * @return translated "Source Other ''{0}'' does not match source other specified in order ''{1}''"
  
   */
  @DefaultMessage("Source Other ''{0}'' does not match source other specified in order ''{1}''")
  @Key("eorderImport.sourceOtherMismatch")
  String eorderImport_sourceOtherMismatch(String arg0,  String arg1);

  /**
   * Translated "Unable to parse birth date ''{0}'' from eorder.".
   * 
   * @param arg0 "{0}"
   * @return translated "Unable to parse birth date ''{0}'' from eorder."
  
   */
  @DefaultMessage("Unable to parse birth date ''{0}'' from eorder.")
  @Key("eorderImport.unparsableBirthDate")
  String eorderImport_unparsableBirthDate(String arg0);

  /**
   * Translated "Unable to parse birth time ''{0}'' from eorder.".
   * 
   * @param arg0 "{0}"
   * @return translated "Unable to parse birth time ''{0}'' from eorder."
  
   */
  @DefaultMessage("Unable to parse birth time ''{0}'' from eorder.")
  @Key("eorderImport.unparsableBirthTime")
  String eorderImport_unparsableBirthTime(String arg0);

  /**
   * Translated "Unable to parse collection date ''{0}'' from eorder.".
   * 
   * @param arg0 "{0}"
   * @return translated "Unable to parse collection date ''{0}'' from eorder."
  
   */
  @DefaultMessage("Unable to parse collection date ''{0}'' from eorder.")
  @Key("eorderImport.unparsableCollectionDate")
  String eorderImport_unparsableCollectionDate(String arg0);

  /**
   * Translated "Unable to parse collection time ''{0}'' from eorder.".
   * 
   * @param arg0 "{0}"
   * @return translated "Unable to parse collection time ''{0}'' from eorder."
  
   */
  @DefaultMessage("Unable to parse collection time ''{0}'' from eorder.")
  @Key("eorderImport.unparsableCollectionTime")
  String eorderImport_unparsableCollectionTime(String arg0);

  /**
   * Translated "Error with sample accession #".
   * 
   * @return translated "Error with sample accession #"
  
   */
  @DefaultMessage("Error with sample accession #")
  @Key("errorSampleAccNum")
  String errorSampleAccNum();

  /**
   * Translated "One or more auxiliary field values associated with this auxiliary field are in error. Please click this row to make those field values and the errors visible.".
   * 
   * @return translated "One or more auxiliary field values associated with this auxiliary field are in error. Please click this row to make those field values and the errors visible."
  
   */
  @DefaultMessage("One or more auxiliary field values associated with this auxiliary field are in error. Please click this row to make those field values and the errors visible.")
  @Key("errorsWithAuxFieldValuesException")
  String errorsWithAuxFieldValuesException();

  /**
   * Translated "Ethnicity ".
   * 
   * @return translated "Ethnicity "
  
   */
  @DefaultMessage("Ethnicity ")
  @Key("ethnicity")
  String ethnicity();

  /**
   * Translated "History - Exchange Criteria".
   * 
   * @return translated "History - Exchange Criteria"
  
   */
  @DefaultMessage("History - Exchange Criteria")
  @Key("exchangeCriteriaHistory")
  String exchangeCriteriaHistory();

  /**
   * Translated "Exchange Data Selection".
   * 
   * @return translated "Exchange Data Selection"
  
   */
  @DefaultMessage("Exchange Data Selection")
  @Key("exchangeDataSelection")
  String exchangeDataSelection();

  /**
   * Translated "Exchange Data Selection Description".
   * 
   * @return translated "Exchange Data Selection Description"
  
   */
  @DefaultMessage("Exchange Data Selection Description")
  @Key("exchangeDataSelectionDescription")
  String exchangeDataSelectionDescription();

  /**
   * Translated "History - Exchange Profile".
   * 
   * @return translated "History - Exchange Profile"
  
   */
  @DefaultMessage("History - Exchange Profile")
  @Key("exchangeProfileHistory")
  String exchangeProfileHistory();

  /**
   * Translated "Exchange Vocabulary Map".
   * 
   * @return translated "Exchange Vocabulary Map"
  
   */
  @DefaultMessage("Exchange Vocabulary Map")
  @Key("exchangeVocabularyMap")
  String exchangeVocabularyMap();

  /**
   * Translated "Define vocabulary mappings from local to external codes for electronic data transmission".
   * 
   * @return translated "Define vocabulary mappings from local to external codes for electronic data transmission"
  
   */
  @DefaultMessage("Define vocabulary mappings from local to external codes for electronic data transmission")
  @Key("exchangeVocabularyMapDescription")
  String exchangeVocabularyMapDescription();

  /**
   * Translated "Exclude PT Sample".
   * 
   * @return translated "Exclude PT Sample"
  
   */
  @DefaultMessage("Exclude PT Sample")
  @Key("exclPTSample")
  String exclPTSample();

  /**
   * Translated "Exclude Aux Data".
   * 
   * @return translated "Exclude Aux Data"
  
   */
  @DefaultMessage("Exclude Aux Data")
  @Key("excludeAuxData")
  String excludeAuxData();

  /**
   * Translated "Exclude Not Reportable Analyses".
   * 
   * @return translated "Exclude Not Reportable Analyses"
  
   */
  @DefaultMessage("Exclude Not Reportable Analyses")
  @Key("excludeNotReportableAnalyses")
  String excludeNotReportableAnalyses();

  /**
   * Translated "Exclude Result Override".
   * 
   * @return translated "Exclude Result Override"
  
   */
  @DefaultMessage("Exclude Result Override")
  @Key("excludeResultOverride")
  String excludeResultOverride();

  /**
   * Translated "Exclude Results".
   * 
   * @return translated "Exclude Results"
  
   */
  @DefaultMessage("Exclude Results")
  @Key("excludeResults")
  String excludeResults();

  /**
   * Translated "Execute Query".
   * 
   * @return translated "Execute Query"
  
   */
  @DefaultMessage("Execute Query")
  @Key("executeQuery")
  String executeQuery();

  /**
   * Translated "An existing sample cannot be filled from an order".
   * 
   * @return translated "An existing sample cannot be filled from an order"
  
   */
  @DefaultMessage("An existing sample cannot be filled from an order")
  @Key("existSampleCantFillFromOrder")
  String existSampleCantFillFromOrder();

  /**
   * Translated "Exit".
   * 
   * @return translated "Exit"
  
   */
  @DefaultMessage("Exit")
  @Key("exit")
  String exit();

  /**
   * Translated "% Exp Completion".
   * 
   * @return translated "% Exp Completion"
  
   */
  @DefaultMessage("% Exp Completion")
  @Key("expCompletion")
  String expCompletion();

  /**
   * Translated "Exp Date".
   * 
   * @return translated "Exp Date"
  
   */
  @DefaultMessage("Exp Date")
  @Key("expDate")
  String expDate();

  /**
   * Translated "Expand".
   * 
   * @return translated "Expand"
  
   */
  @DefaultMessage("Expand")
  @Key("expand")
  String expand();

  /**
   * Translated "Expected Value".
   * 
   * @return translated "Expected Value"
  
   */
  @DefaultMessage("Expected Value")
  @Key("expectedValue")
  String expectedValue();

  /**
   * Translated "Expiration Date".
   * 
   * @return translated "Expiration Date"
  
   */
  @DefaultMessage("Expiration Date")
  @Key("expirationDate")
  String expirationDate();

  /**
   * Translated "Expire date must not be the same as or before usable date".
   * 
   * @return translated "Expire date must not be the same as or before usable date"
  
   */
  @DefaultMessage("Expire date must not be the same as or before usable date")
  @Key("expireBeforeUsableException")
  String expireBeforeUsableException();

  /**
   * Translated "Expire Date".
   * 
   * @return translated "Expire Date"
  
   */
  @DefaultMessage("Expire Date")
  @Key("expireDate")
  String expireDate();

  /**
   * Translated "Your Lock on this record has expired; Please abort and try again".
   * 
   * @return translated "Your Lock on this record has expired; Please abort and try again"
  
   */
  @DefaultMessage("Your Lock on this record has expired; Please abort and try again")
  @Key("expiredLockException")
  String expiredLockException();

  /**
   * Translated "Export To Location".
   * 
   * @return translated "Export To Location"
  
   */
  @DefaultMessage("Export To Location")
  @Key("exportToLocation")
  String exportToLocation();

  /**
   * Translated "Export To Excel".
   * 
   * @return translated "Export To Excel"
  
   */
  @DefaultMessage("Export To Excel")
  @Key("exportToXl")
  String exportToXl();

  /**
   * Translated "Add To Exst".
   * 
   * @return translated "Add To Exst"
  
   */
  @DefaultMessage("Add To Exst")
  @Key("ext")
  String ext();

  /**
   * Translated "Ext Order #".
   * 
   * @return translated "Ext Order #"
  
   */
  @DefaultMessage("Ext Order #")
  @Key("extOrderNum")
  String extOrderNum();

  /**
   * Translated "Ext QC".
   * 
   * @return translated "Ext QC"
  
   */
  @DefaultMessage("Ext QC")
  @Key("extQC")
  String extQC();

  /**
   * Translated "Ext Reference".
   * 
   * @return translated "Ext Reference"
  
   */
  @DefaultMessage("Ext Reference")
  @Key("extReference")
  String extReference();

  /**
   * Translated "External".
   * 
   * @return translated "External"
  
   */
  @DefaultMessage("External")
  @Key("external")
  String external();

  /**
   * Translated "External Coding System".
   * 
   * @return translated "External Coding System"
  
   */
  @DefaultMessage("External Coding System")
  @Key("externalCodingSystem")
  String externalCodingSystem();

  /**
   * Translated "External Description".
   * 
   * @return translated "External Description"
  
   */
  @DefaultMessage("External Description")
  @Key("externalDescription")
  String externalDescription();

  /**
   * Translated "External Id".
   * 
   * @return translated "External Id"
  
   */
  @DefaultMessage("External Id")
  @Key("externalId")
  String externalId();

  /**
   * Translated "External Term".
   * 
   * @return translated "External Term"
  
   */
  @DefaultMessage("External Term")
  @Key("externalTerm")
  String externalTerm();

  /**
   * Translated "History - External Term".
   * 
   * @return translated "History - External Term"
  
   */
  @DefaultMessage("History - External Term")
  @Key("externalTermHistory")
  String externalTermHistory();

  /**
   * Translated "Ext Cmts".
   * 
   * @return translated "Ext Cmts"
  
   */
  @DefaultMessage("Ext Cmts")
  @Key("extrnlCmnts")
  String extrnlCmnts();

  /**
   * Translated "Facility".
   * 
   * @return translated "Facility"
  
   */
  @DefaultMessage("Facility")
  @Key("facility")
  String facility();

  /**
   * Translated "Facility Id".
   * 
   * @return translated "Facility Id"
  
   */
  @DefaultMessage("Facility Id")
  @Key("facilityId")
  String facilityId();

  /**
   * Translated "FAILED RUN".
   * 
   * @return translated "FAILED RUN"
  
   */
  @DefaultMessage("FAILED RUN")
  @Key("failedRunSubject")
  String failedRunSubject();

  /**
   * Translated "Failed to lookup dictionary entry by system name = ".
   * 
   * @return translated "Failed to lookup dictionary entry by system name = "
  
   */
  @DefaultMessage("Failed to lookup dictionary entry by system name = ")
  @Key("failedToLookupDictforSysName")
  String failedToLookupDictforSysName();

  /**
   * Translated "Fast Sample Login".
   * 
   * @return translated "Fast Sample Login"
  
   */
  @DefaultMessage("Fast Sample Login")
  @Key("fastSampleLogin")
  String fastSampleLogin();

  /**
   * Translated "Fast sample login description".
   * 
   * @return translated "Fast sample login description"
  
   */
  @DefaultMessage("Fast sample login description")
  @Key("fastSampleLoginDescription")
  String fastSampleLoginDescription();

  /**
   * Translated "Favorites".
   * 
   * @return translated "Favorites"
  
   */
  @DefaultMessage("Favorites")
  @Key("favorites")
  String favorites();

  /**
   * Translated "Favorites Menu".
   * 
   * @return translated "Favorites Menu"
  
   */
  @DefaultMessage("Favorites Menu")
  @Key("favoritesMenu")
  String favoritesMenu();

  /**
   * Translated "Show or hide the left favorite menu.".
   * 
   * @return translated "Show or hide the left favorite menu."
  
   */
  @DefaultMessage("Show or hide the left favorite menu.")
  @Key("favoritesMenuDescription")
  String favoritesMenuDescription();

  /**
   * Translated "Fax".
   * 
   * @return translated "Fax"
  
   */
  @DefaultMessage("Fax")
  @Key("fax")
  String fax();

  /**
   * Translated "Fax #".
   * 
   * @return translated "Fax #"
  
   */
  @DefaultMessage("Fax #")
  @Key("faxNumber")
  String faxNumber();

  /**
   * Translated "Error: Could not retrieve the record.".
   * 
   * @return translated "Error: Could not retrieve the record."
  
   */
  @DefaultMessage("Error: Could not retrieve the record.")
  @Key("fetchFailed")
  String fetchFailed();

  /**
   * Translated "Fetching ...".
   * 
   * @return translated "Fetching ..."
  
   */
  @DefaultMessage("Fetching ...")
  @Key("fetching")
  String fetching();

  /**
   * Translated "Date is too far in the future".
   * 
   * @return translated "Date is too far in the future"
  
   */
  @DefaultMessage("Date is too far in the future")
  @Key("fieldFutureException")
  String fieldFutureException();

  /**
   * Translated "Field exceeded maximum value".
   * 
   * @return translated "Field exceeded maximum value"
  
   */
  @DefaultMessage("Field exceeded maximum value")
  @Key("fieldMaxException")
  String fieldMaxException();

  /**
   * Translated "Field exceeded maximum length".
   * 
   * @return translated "Field exceeded maximum length"
  
   */
  @DefaultMessage("Field exceeded maximum length")
  @Key("fieldMaxLengthException")
  String fieldMaxLengthException();

  /**
   * Translated "Field is below minimum value".
   * 
   * @return translated "Field is below minimum value"
  
   */
  @DefaultMessage("Field is below minimum value")
  @Key("fieldMinException")
  String fieldMinException();

  /**
   * Translated "Field is below minimum length".
   * 
   * @return translated "Field is below minimum length"
  
   */
  @DefaultMessage("Field is below minimum length")
  @Key("fieldMinLengthException")
  String fieldMinLengthException();

  /**
   * Translated "Field must be numeric".
   * 
   * @return translated "Field must be numeric"
  
   */
  @DefaultMessage("Field must be numeric")
  @Key("fieldNumericException")
  String fieldNumericException();

  /**
   * Translated "Field Office #".
   * 
   * @return translated "Field Office #"
  
   */
  @DefaultMessage("Field Office #")
  @Key("fieldOfficeNum")
  String fieldOfficeNum();

  /**
   * Translated "Date is too far in the past".
   * 
   * @return translated "Date is too far in the past"
  
   */
  @DefaultMessage("Date is too far in the past")
  @Key("fieldPastException")
  String fieldPastException();

  /**
   * Translated "Field is required".
   * 
   * @return translated "Field is required"
  
   */
  @DefaultMessage("Field is required")
  @Key("fieldRequiredException")
  String fieldRequiredException();

  /**
   * Translated "A record with this value already exists. Please enter a unique value for this field".
   * 
   * @return translated "A record with this value already exists. Please enter a unique value for this field"
  
   */
  @DefaultMessage("A record with this value already exists. Please enter a unique value for this field")
  @Key("fieldUniqueException")
  String fieldUniqueException();

  /**
   * Translated "Please enter a unique value for this field.".
   * 
   * @return translated "Please enter a unique value for this field."
  
   */
  @DefaultMessage("Please enter a unique value for this field.")
  @Key("fieldUniqueOnlyException")
  String fieldUniqueOnlyException();

  /**
   * Translated "Specified file name is not valid; please report this error to your sysadmin".
   * 
   * @return translated "Specified file name is not valid; please report this error to your sysadmin"
  
   */
  @DefaultMessage("Specified file name is not valid; please report this error to your sysadmin")
  @Key("fileNameNotValidException")
  String fileNameNotValidException();

  /**
   * Translated "Fill Order".
   * 
   * @return translated "Fill Order"
  
   */
  @DefaultMessage("Fill Order")
  @Key("fillOrder")
  String fillOrder();

  /**
   * Translated "Manage and package internal and send-out orders for processing.".
   * 
   * @return translated "Manage and package internal and send-out orders for processing."
  
   */
  @DefaultMessage("Manage and package internal and send-out orders for processing.")
  @Key("fillOrderDescription")
  String fillOrderDescription();

  /**
   * Translated "The order items in this row have changed, are you sure you want to unselect?".
   * 
   * @return translated "The order items in this row have changed, are you sure you want to unselect?"
  
   */
  @DefaultMessage("The order items in this row have changed, are you sure you want to unselect?")
  @Key("fillOrderItemsChangedConfirm")
  String fillOrderItemsChangedConfirm();

  /**
   * Translated "You need to check at least one order before committing.".
   * 
   * @return translated "You need to check at least one order before committing."
  
   */
  @DefaultMessage("You need to check at least one order before committing.")
  @Key("fillOrderNoneChecked")
  String fillOrderNoneChecked();

  /**
   * Translated "Only pending orders can be processed.".
   * 
   * @return translated "Only pending orders can be processed."
  
   */
  @DefaultMessage("Only pending orders can be processed.")
  @Key("fillOrderOnlyPendingRowsCanBeChecked")
  String fillOrderOnlyPendingRowsCanBeChecked();

  /**
   * Translated "Not enough in the qty fields to fill this order".
   * 
   * @return translated "Not enough in the qty fields to fill this order"
  
   */
  @DefaultMessage("Not enough in the qty fields to fill this order")
  @Key("fillOrderQtyException")
  String fillOrderQtyException();

  /**
   * Translated "Filled".
   * 
   * @return translated "Filled"
  
   */
  @DefaultMessage("Filled")
  @Key("filled")
  String filled();

  /**
   * Translated "Final Report".
   * 
   * @return translated "Final Report"
  
   */
  @DefaultMessage("Final Report")
  @Key("finalReport")
  String finalReport();

  /**
   * Translated "Final Report (Batch)".
   * 
   * @return translated "Final Report (Batch)"
  
   */
  @DefaultMessage("Final Report (Batch)")
  @Key("finalReportBatch")
  String finalReportBatch();

  /**
   * Translated "Final Report (Batch Reprint)".
   * 
   * @return translated "Final Report (Batch Reprint)"
  
   */
  @DefaultMessage("Final Report (Batch Reprint)")
  @Key("finalReportBatchReprint")
  String finalReportBatchReprint();

  /**
   * Translated "Final Report Single/Reprint".
   * 
   * @return translated "Final Report Single/Reprint"
  
   */
  @DefaultMessage("Final Report Single/Reprint")
  @Key("finalReportSingleReprint")
  String finalReportSingleReprint();

  /**
   * Translated "FinalReport {0} R {1,number,#0}".
   * 
   * @param arg0 "{0}"
   * @param arg1 "{1,number,#0}"
   * @return translated "FinalReport {0} R {1,number,#0}"
  
   */
  @DefaultMessage("FinalReport {0} R {1,number,#0}")
  @Key("finalreport.attachmentEsaveDescription")
  String finalreport_attachmentEsaveDescription(String arg0,  Integer arg1);

  /**
   * Translated "finalreport{0}r{1,number,#0}-{2}.pdf".
   * 
   * @param arg0 "{0}"
   * @param arg1 "{1,number,#0}"
   * @param arg2 "{2}"
   * @return translated "finalreport{0}r{1,number,#0}-{2}.pdf"
  
   */
  @DefaultMessage("finalreport{0}r{1,number,#0}-{2}.pdf")
  @Key("finalreport.attachmentEsaveFileName")
  String finalreport_attachmentEsaveFileName(String arg0,  Integer arg1,  String arg2);

  /**
   * Translated "Preview Final Report".
   * 
   * @return translated "Preview Final Report"
  
   */
  @DefaultMessage("Preview Final Report")
  @Key("finalreport.preview")
  String finalreport_preview();

  /**
   * Translated "View Final Report".
   * 
   * @return translated "View Final Report"
  
   */
  @DefaultMessage("View Final Report")
  @Key("finalreport.view")
  String finalreport_view();

  /**
   * Translated "Find".
   * 
   * @return translated "Find"
  
   */
  @DefaultMessage("Find")
  @Key("find")
  String find();

  /**
   * Translated "FIPS".
   * 
   * @return translated "FIPS"
  
   */
  @DefaultMessage("FIPS")
  @Key("fips")
  String fips();

  /**
   * Translated "First Name".
   * 
   * @return translated "First Name"
  
   */
  @DefaultMessage("First Name")
  @Key("firstName")
  String firstName();

  /**
   * Translated "Position must be specified if type is Fixed or Duplicate".
   * 
   * @return translated "Position must be specified if type is Fixed or Duplicate"
  
   */
  @DefaultMessage("Position must be specified if type is Fixed or Duplicate")
  @Key("fixedDuplicatePosException")
  String fixedDuplicatePosException();

  /**
   * Translated "Flag".
   * 
   * @return translated "Flag"
  
   */
  @DefaultMessage("Flag")
  @Key("flag")
  String flag();

  /**
   * Translated "Flags".
   * 
   * @return translated "Flags"
  
   */
  @DefaultMessage("Flags")
  @Key("flags")
  String flags();

  /**
   * Translated "4 - 7 days ago".
   * 
   * @return translated "4 - 7 days ago"
  
   */
  @DefaultMessage("4 - 7 days ago")
  @Key("fourToSevenDays")
  String fourToSevenDays();

  /**
   * Translated "Frequency must be greater than zero".
   * 
   * @return translated "Frequency must be greater than zero"
  
   */
  @DefaultMessage("Frequency must be greater than zero")
  @Key("freqInvalidException")
  String freqInvalidException();

  /**
   * Translated "Frequency".
   * 
   * @return translated "Frequency"
  
   */
  @DefaultMessage("Frequency")
  @Key("frequency")
  String frequency();

  /**
   * Translated "From".
   * 
   * @return translated "From"
  
   */
  @DefaultMessage("From")
  @Key("from")
  String from();

  /**
   * Translated "State Hygienic Laboratory".
   * 
   * @return translated "State Hygienic Laboratory"
  
   */
  @DefaultMessage("State Hygienic Laboratory")
  @Key("fromCompany")
  String fromCompany();

  /**
   * Translated "From Exp Date".
   * 
   * @return translated "From Exp Date"
  
   */
  @DefaultMessage("From Exp Date")
  @Key("fromExpDate")
  String fromExpDate();

  /**
   * Translated "From Item".
   * 
   * @return translated "From Item"
  
   */
  @DefaultMessage("From Item")
  @Key("fromItem")
  String fromItem();

  /**
   * Translated "From Loc".
   * 
   * @return translated "From Loc"
  
   */
  @DefaultMessage("From Loc")
  @Key("fromLoc")
  String fromLoc();

  /**
   * Translated "From Lot #".
   * 
   * @return translated "From Lot #"
  
   */
  @DefaultMessage("From Lot #")
  @Key("fromLotNum")
  String fromLotNum();

  /**
   * Translated "From Sample".
   * 
   * @return translated "From Sample"
  
   */
  @DefaultMessage("From Sample")
  @Key("fromSample")
  String fromSample();

  /**
   * Translated "Full Detail".
   * 
   * @return translated "Full Detail"
  
   */
  @DefaultMessage("Full Detail")
  @Key("fullDetail")
  String fullDetail();

  /**
   * Translated "Full Login".
   * 
   * @return translated "Full Login"
  
   */
  @DefaultMessage("Full Login")
  @Key("fullLogin")
  String fullLogin();

  /**
   * Translated "Enter sample with all supporting information.".
   * 
   * @return translated "Enter sample with all supporting information."
  
   */
  @DefaultMessage("Enter sample with all supporting information.")
  @Key("fullLoginDescription")
  String fullLoginDescription();

  /**
   * Translated "Abort".
   * 
   * @return translated "Abort"
  
   */
  @DefaultMessage("Abort")
  @Key("gen.abort")
  String gen_abort();

  /**
   * Translated "Active".
   * 
   * @return translated "Active"
  
   */
  @DefaultMessage("Active")
  @Key("gen.active")
  String gen_active();

  /**
   * Translated "Add".
   * 
   * @return translated "Add"
  
   */
  @DefaultMessage("Add")
  @Key("gen.add")
  String gen_add();

  /**
   * Translated "Add aborted".
   * 
   * @return translated "Add aborted"
  
   */
  @DefaultMessage("Add aborted")
  @Key("gen.addAborted")
  String gen_addAborted();

  /**
   * Translated "Add Note".
   * 
   * @return translated "Add Note"
  
   */
  @DefaultMessage("Add Note")
  @Key("gen.addNote")
  String gen_addNote();

  /**
   * Translated "Add Row".
   * 
   * @return translated "Add Row"
  
   */
  @DefaultMessage("Add Row")
  @Key("gen.addRow")
  String gen_addRow();

  /**
   * Translated "Add Test".
   * 
   * @return translated "Add Test"
  
   */
  @DefaultMessage("Add Test")
  @Key("gen.addTest")
  String gen_addTest();

  /**
   * Translated "Adding...".
   * 
   * @return translated "Adding..."
  
   */
  @DefaultMessage("Adding...")
  @Key("gen.adding")
  String gen_adding();

  /**
   * Translated "Adding...Complete".
   * 
   * @return translated "Adding...Complete"
  
   */
  @DefaultMessage("Adding...Complete")
  @Key("gen.addingComplete")
  String gen_addingComplete();

  /**
   * Translated "ABCDEFGHIJKLMNOPQRSTUVWXYZ".
   * 
   * @return translated "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
  
   */
  @DefaultMessage("ABCDEFGHIJKLMNOPQRSTUVWXYZ")
  @Key("gen.alphabet")
  String gen_alphabet();

  /**
   * Translated "Analyte".
   * 
   * @return translated "Analyte"
  
   */
  @DefaultMessage("Analyte")
  @Key("gen.analyte")
  String gen_analyte();

  /**
   * Translated "Analytes".
   * 
   * @return translated "Analytes"
  
   */
  @DefaultMessage("Analytes")
  @Key("gen.analytes")
  String gen_analytes();

  /**
   * Translated "Barcode".
   * 
   * @return translated "Barcode"
  
   */
  @DefaultMessage("Barcode")
  @Key("gen.barcode")
  String gen_barcode();

  /**
   * Translated "Begin Date".
   * 
   * @return translated "Begin Date"
  
   */
  @DefaultMessage("Begin Date")
  @Key("gen.beginDate")
  String gen_beginDate();

  /**
   * Translated "Billable".
   * 
   * @return translated "Billable"
  
   */
  @DefaultMessage("Billable")
  @Key("gen.billable")
  String gen_billable();

  /**
   * Translated "Cancel".
   * 
   * @return translated "Cancel"
  
   */
  @DefaultMessage("Cancel")
  @Key("gen.cancel")
  String gen_cancel();

  /**
   * Translated "Canceling changes ...".
   * 
   * @return translated "Canceling changes ..."
  
   */
  @DefaultMessage("Canceling changes ...")
  @Key("gen.cancelChanges")
  String gen_cancelChanges();

  /**
   * Translated "Cancel Test".
   * 
   * @return translated "Cancel Test"
  
   */
  @DefaultMessage("Cancel Test")
  @Key("gen.cancelTest")
  String gen_cancelTest();

  /**
   * Translated "Check All".
   * 
   * @return translated "Check All"
  
   */
  @DefaultMessage("Check All")
  @Key("gen.checkAll")
  String gen_checkAll();

  /**
   * Translated "Check In".
   * 
   * @return translated "Check In"
  
   */
  @DefaultMessage("Check In")
  @Key("gen.checkIn")
  String gen_checkIn();

  /**
   * Translated "Check Out".
   * 
   * @return translated "Check Out"
  
   */
  @DefaultMessage("Check Out")
  @Key("gen.checkOut")
  String gen_checkOut();

  /**
   * Translated "Click for more records".
   * 
   * @return translated "Click for more records"
  
   */
  @DefaultMessage("Click for more records")
  @Key("gen.clickForMore")
  String gen_clickForMore();

  /**
   * Translated "Collapse".
   * 
   * @return translated "Collapse"
  
   */
  @DefaultMessage("Collapse")
  @Key("gen.collapse")
  String gen_collapse();

  /**
   * Translated "Standard Comment Library".
   * 
   * @return translated "Standard Comment Library"
  
   */
  @DefaultMessage("Standard Comment Library")
  @Key("gen.commentLibrary")
  String gen_commentLibrary();

  /**
   * Translated "Commit".
   * 
   * @return translated "Commit"
  
   */
  @DefaultMessage("Commit")
  @Key("gen.commit")
  String gen_commit();

  /**
   * Translated "Complete".
   * 
   * @return translated "Complete"
  
   */
  @DefaultMessage("Complete")
  @Key("gen.complete")
  String gen_complete();

  /**
   * Translated "Container".
   * 
   * @return translated "Container"
  
   */
  @DefaultMessage("Container")
  @Key("gen.container")
  String gen_container();

  /**
   * Translated "Please correct the errors indicated, then press Commit".
   * 
   * @return translated "Please correct the errors indicated, then press Commit"
  
   */
  @DefaultMessage("Please correct the errors indicated, then press Commit")
  @Key("gen.correctErrors")
  String gen_correctErrors();

  /**
   * Translated "Cost".
   * 
   * @return translated "Cost"
  
   */
  @DefaultMessage("Cost")
  @Key("gen.cost")
  String gen_cost();

  /**
   * Translated "Created".
   * 
   * @return translated "Created"
  
   */
  @DefaultMessage("Created")
  @Key("gen.created")
  String gen_created();

  /**
   * Translated "Date Rec".
   * 
   * @return translated "Date Rec"
  
   */
  @DefaultMessage("Date Rec")
  @Key("gen.dateRec")
  String gen_dateRec();

  /**
   * Translated "Desc".
   * 
   * @return translated "Desc"
  
   */
  @DefaultMessage("Desc")
  @Key("gen.desc")
  String gen_desc();

  /**
   * Translated "Description".
   * 
   * @return translated "Description"
  
   */
  @DefaultMessage("Description")
  @Key("gen.description")
  String gen_description();

  /**
   * Translated "Details".
   * 
   * @return translated "Details"
  
   */
  @DefaultMessage("Details")
  @Key("gen.details")
  String gen_details();

  /**
   * Translated "Dispensed Units".
   * 
   * @return translated "Dispensed Units"
  
   */
  @DefaultMessage("Dispensed Units")
  @Key("gen.dispensedUnits")
  String gen_dispensedUnits();

  /**
   * Translated "Due".
   * 
   * @return translated "Due"
  
   */
  @DefaultMessage("Due")
  @Key("gen.due")
  String gen_due();

  /**
   * Translated "Duplicate".
   * 
   * @return translated "Duplicate"
  
   */
  @DefaultMessage("Duplicate")
  @Key("gen.duplicateRecord")
  String gen_duplicateRecord();

  /**
   * Translated "Edit Note".
   * 
   * @return translated "Edit Note"
  
   */
  @DefaultMessage("Edit Note")
  @Key("gen.editNote")
  String gen_editNote();

  /**
   * Translated "You may not execute an empty query".
   * 
   * @return translated "You may not execute an empty query"
  
   */
  @DefaultMessage("You may not execute an empty query")
  @Key("gen.emptyQueryException")
  String gen_emptyQueryException();

  /**
   * Translated "End Date".
   * 
   * @return translated "End Date"
  
   */
  @DefaultMessage("End Date")
  @Key("gen.endDate")
  String gen_endDate();

  /**
   * Translated "Enter fields to query by then press Commit".
   * 
   * @return translated "Enter fields to query by then press Commit"
  
   */
  @DefaultMessage("Enter fields to query by then press Commit")
  @Key("gen.enterFieldsToQuery")
  String gen_enterFieldsToQuery();

  /**
   * Translated "Enter information in the fields, then press Commit".
   * 
   * @return translated "Enter information in the fields, then press Commit"
  
   */
  @DefaultMessage("Enter information in the fields, then press Commit")
  @Key("gen.enterInformationPressCommit")
  String gen_enterInformationPressCommit();

  /**
   * Translated "Entered Date".
   * 
   * @return translated "Entered Date"
  
   */
  @DefaultMessage("Entered Date")
  @Key("gen.enteredDate")
  String gen_enteredDate();

  /**
   * Translated "Entry".
   * 
   * @return translated "Entry"
  
   */
  @DefaultMessage("Entry")
  @Key("gen.entry")
  String gen_entry();

  /**
   * Translated "Exp Date".
   * 
   * @return translated "Exp Date"
  
   */
  @DefaultMessage("Exp Date")
  @Key("gen.expDate")
  String gen_expDate();

  /**
   * Translated "Expand".
   * 
   * @return translated "Expand"
  
   */
  @DefaultMessage("Expand")
  @Key("gen.expand")
  String gen_expand();

  /**
   * Translated "Expire".
   * 
   * @return translated "Expire"
  
   */
  @DefaultMessage("Expire")
  @Key("gen.expire")
  String gen_expire();

  /**
   * Translated "Ext Reference".
   * 
   * @return translated "Ext Reference"
  
   */
  @DefaultMessage("Ext Reference")
  @Key("gen.extReference")
  String gen_extReference();

  /**
   * Translated "External".
   * 
   * @return translated "External"
  
   */
  @DefaultMessage("External")
  @Key("gen.external")
  String gen_external();

  /**
   * Translated "Failed".
   * 
   * @return translated "Failed"
  
   */
  @DefaultMessage("Failed")
  @Key("gen.failed")
  String gen_failed();

  /**
   * Translated "Error: Could not retrieve the record.".
   * 
   * @return translated "Error: Could not retrieve the record."
  
   */
  @DefaultMessage("Error: Could not retrieve the record.")
  @Key("gen.fetchFailed")
  String gen_fetchFailed();

  /**
   * Translated "Fetching ...".
   * 
   * @return translated "Fetching ..."
  
   */
  @DefaultMessage("Fetching ...")
  @Key("gen.fetching")
  String gen_fetching();

  /**
   * Translated "First".
   * 
   * @return translated "First"
  
   */
  @DefaultMessage("First")
  @Key("gen.first")
  String gen_first();

  /**
   * Translated "First Name".
   * 
   * @return translated "First Name"
  
   */
  @DefaultMessage("First Name")
  @Key("gen.firstName")
  String gen_firstName();

  /**
   * Translated "Frequency".
   * 
   * @return translated "Frequency"
  
   */
  @DefaultMessage("Frequency")
  @Key("gen.frequency")
  String gen_frequency();

  /**
   * Translated "Generating Report...".
   * 
   * @return translated "Generating Report..."
  
   */
  @DefaultMessage("Generating Report...")
  @Key("gen.generatingReport")
  String gen_generatingReport();

  /**
   * Translated "Group".
   * 
   * @return translated "Group"
  
   */
  @DefaultMessage("Group")
  @Key("gen.group")
  String gen_group();

  /**
   * Translated "History".
   * 
   * @return translated "History"
  
   */
  @DefaultMessage("History")
  @Key("gen.history")
  String gen_history();

  /**
   * Translated "Id".
   * 
   * @return translated "Id"
  
   */
  @DefaultMessage("Id")
  @Key("gen.id")
  String gen_id();

  /**
   * Translated "Initializing...".
   * 
   * @return translated "Initializing..."
  
   */
  @DefaultMessage("Initializing...")
  @Key("gen.initializing")
  String gen_initializing();

  /**
   * Translated "Internal".
   * 
   * @return translated "Internal"
  
   */
  @DefaultMessage("Internal")
  @Key("gen.internal")
  String gen_internal();

  /**
   * Translated "Value is invalid".
   * 
   * @return translated "Value is invalid"
  
   */
  @DefaultMessage("Value is invalid")
  @Key("gen.invalidValueException")
  String gen_invalidValueException();

  /**
   * Translated "Inventory Item".
   * 
   * @return translated "Inventory Item"
  
   */
  @DefaultMessage("Inventory Item")
  @Key("gen.inventoryItem")
  String gen_inventoryItem();

  /**
   * Translated "Item".
   * 
   * @return translated "Item"
  
   */
  @DefaultMessage("Item")
  @Key("gen.item")
  String gen_item();

  /**
   * Translated "Last".
   * 
   * @return translated "Last"
  
   */
  @DefaultMessage("Last")
  @Key("gen.last")
  String gen_last();

  /**
   * Translated "Last Name".
   * 
   * @return translated "Last Name"
  
   */
  @DefaultMessage("Last Name")
  @Key("gen.lastName")
  String gen_lastName();

  /**
   * Translated "Done".
   * 
   * @return translated "Done"
  
   */
  @DefaultMessage("Done")
  @Key("gen.loadCompleteMessage")
  String gen_loadCompleteMessage();

  /**
   * Translated "Location".
   * 
   * @return translated "Location"
  
   */
  @DefaultMessage("Location")
  @Key("gen.location")
  String gen_location();

  /**
   * Translated "Locking record for Update...".
   * 
   * @return translated "Locking record for Update..."
  
   */
  @DefaultMessage("Locking record for Update...")
  @Key("gen.lockForUpdate")
  String gen_lockForUpdate();

  /**
   * Translated "Lot #".
   * 
   * @return translated "Lot #"
  
   */
  @DefaultMessage("Lot #")
  @Key("gen.lotNum")
  String gen_lotNum();

  /**
   * Translated "Method".
   * 
   * @return translated "Method"
  
   */
  @DefaultMessage("Method")
  @Key("gen.method")
  String gen_method();

  /**
   * Translated "Middle".
   * 
   * @return translated "Middle"
  
   */
  @DefaultMessage("Middle")
  @Key("gen.middle")
  String gen_middle();

  /**
   * Translated "Middle Name".
   * 
   * @return translated "Middle Name"
  
   */
  @DefaultMessage("Middle Name")
  @Key("gen.middleName")
  String gen_middleName();

  /**
   * Translated "Path to upload directory is missing. Please contact the system administrator.".
   * 
   * @return translated "Path to upload directory is missing. Please contact the system administrator."
  
   */
  @DefaultMessage("Path to upload directory is missing. Please contact the system administrator.")
  @Key("gen.missingUploadPathException")
  String gen_missingUploadPathException();

  /**
   * Translated "Move".
   * 
   * @return translated "Move"
  
   */
  @DefaultMessage("Move")
  @Key("gen.move")
  String gen_move();

  /**
   * Translated "Move Down".
   * 
   * @return translated "Move Down"
  
   */
  @DefaultMessage("Move Down")
  @Key("gen.moveDown")
  String gen_moveDown();

  /**
   * Translated "<<".
   * 
   * @return translated "<<"
  
   */
  @DefaultMessage("<<")
  @Key("gen.moveLeft")
  String gen_moveLeft();

  /**
   * Translated "Move Up".
   * 
   * @return translated "Move Up"
  
   */
  @DefaultMessage("Move Up")
  @Key("gen.moveUp")
  String gen_moveUp();

  /**
   * Translated "You must Commit or Abort changes first".
   * 
   * @return translated "You must Commit or Abort changes first"
  
   */
  @DefaultMessage("You must Commit or Abort changes first")
  @Key("gen.mustCommitOrAbort")
  String gen_mustCommitOrAbort();

  /**
   * Translated "Name".
   * 
   * @return translated "Name"
  
   */
  @DefaultMessage("Name")
  @Key("gen.name")
  String gen_name();

  /**
   * Translated "999-99-9999".
   * 
   * @return translated "999-99-9999"
  
   */
  @DefaultMessage("999-99-9999")
  @Key("gen.nationalIdPattern")
  String gen_nationalIdPattern();

  /**
   * Translated "Next".
   * 
   * @return translated "Next"
  
   */
  @DefaultMessage("Next")
  @Key("gen.next")
  String gen_next();

  /**
   * Translated "No".
   * 
   * @return translated "No"
  
   */
  @DefaultMessage("No")
  @Key("gen.no")
  String gen_no();

  /**
   * Translated "No more records in this direction".
   * 
   * @return translated "No more records in this direction"
  
   */
  @DefaultMessage("No more records in this direction")
  @Key("gen.noMoreRecordInDir")
  String gen_noMoreRecordInDir();

  /**
   * Translated "No records found".
   * 
   * @return translated "No records found"
  
   */
  @DefaultMessage("No records found")
  @Key("gen.noRecordsFound")
  String gen_noRecordsFound();

  /**
   * Translated "Not Reportable".
   * 
   * @return translated "Not Reportable"
  
   */
  @DefaultMessage("Not Reportable")
  @Key("gen.notReportable")
  String gen_notReportable();

  /**
   * Translated "Note".
   * 
   * @return translated "Note"
  
   */
  @DefaultMessage("Note")
  @Key("gen.note")
  String gen_note();

  /**
   * Translated "Note Editor".
   * 
   * @return translated "Note Editor"
  
   */
  @DefaultMessage("Note Editor")
  @Key("gen.noteEditor")
  String gen_noteEditor();

  /**
   * Translated "Considering {0} cases.".
   * 
   * @param arg0 "{0}"
   * @return translated "Considering {0} cases."
  
   */
  @DefaultMessage("Considering {0} cases.")
  @Key("gen.numberCases")
  String gen_numberCases(String arg0);

  /**
   * Translated "OK".
   * 
   * @return translated "OK"
  
   */
  @DefaultMessage("OK")
  @Key("gen.ok")
  String gen_ok();

  /**
   * Translated "Options".
   * 
   * @return translated "Options"
  
   */
  @DefaultMessage("Options")
  @Key("gen.options")
  String gen_options();

  /**
   * Translated "Order Request Form".
   * 
   * @return translated "Order Request Form"
  
   */
  @DefaultMessage("Order Request Form")
  @Key("gen.orderRequestForm")
  String gen_orderRequestForm();

  /**
   * Translated "Samples from {0} are to be held or refused".
   * 
   * @param arg0 "{0}"
   * @return translated "Samples from {0} are to be held or refused"
  
   */
  @DefaultMessage("Samples from {0} are to be held or refused")
  @Key("gen.orgMarkedAsHoldRefuseSample")
  String gen_orgMarkedAsHoldRefuseSample(String arg0);

  /**
   * Translated "Organization".
   * 
   * @return translated "Organization"
  
   */
  @DefaultMessage("Organization")
  @Key("gen.organization")
  String gen_organization();

  /**
   * Translated "Panel".
   * 
   * @return translated "Panel"
  
   */
  @DefaultMessage("Panel")
  @Key("gen.panel")
  String gen_panel();

  /**
   * Translated "Parent".
   * 
   * @return translated "Parent"
  
   */
  @DefaultMessage("Parent")
  @Key("gen.parent")
  String gen_parent();

  /**
   * Translated "999/999-9999".
   * 
   * @return translated "999/999-9999"
  
   */
  @DefaultMessage("999/999-9999")
  @Key("gen.phonePattern")
  String gen_phonePattern();

  /**
   * Translated "999/999-9999.9999".
   * 
   * @return translated "999/999-9999.9999"
  
   */
  @DefaultMessage("999/999-9999.9999")
  @Key("gen.phoneWithExtensionPattern")
  String gen_phoneWithExtensionPattern();

  /**
   * Translated "Pop-out".
   * 
   * @return translated "Pop-out"
  
   */
  @DefaultMessage("Pop-out")
  @Key("gen.popout")
  String gen_popout();

  /**
   * Translated "Previous".
   * 
   * @return translated "Previous"
  
   */
  @DefaultMessage("Previous")
  @Key("gen.previous")
  String gen_previous();

  /**
   * Translated "Print".
   * 
   * @return translated "Print"
  
   */
  @DefaultMessage("Print")
  @Key("gen.print")
  String gen_print();

  /**
   * Translated "Printer".
   * 
   * @return translated "Printer"
  
   */
  @DefaultMessage("Printer")
  @Key("gen.printer")
  String gen_printer();

  /**
   * Translated "Priority".
   * 
   * @return translated "Priority"
  
   */
  @DefaultMessage("Priority")
  @Key("gen.priority")
  String gen_priority();

  /**
   * Translated "Are you sure you want to change this?".
   * 
   * @return translated "Are you sure you want to change this?"
  
   */
  @DefaultMessage("Are you sure you want to change this?")
  @Key("gen.qaEventEditConfirm")
  String gen_qaEventEditConfirm();

  /**
   * Translated "Qty".
   * 
   * @return translated "Qty"
  
   */
  @DefaultMessage("Qty")
  @Key("gen.qty")
  String gen_qty();

  /**
   * Translated "Quantity".
   * 
   * @return translated "Quantity"
  
   */
  @DefaultMessage("Quantity")
  @Key("gen.quantity")
  String gen_quantity();

  /**
   * Translated "Query".
   * 
   * @return translated "Query"
  
   */
  @DefaultMessage("Query")
  @Key("gen.query")
  String gen_query();

  /**
   * Translated "Query aborted".
   * 
   * @return translated "Query aborted"
  
   */
  @DefaultMessage("Query aborted")
  @Key("gen.queryAborted")
  String gen_queryAborted();

  /**
   * Translated "Query failed".
   * 
   * @return translated "Query failed"
  
   */
  @DefaultMessage("Query failed")
  @Key("gen.queryFailed")
  String gen_queryFailed();

  /**
   * Translated "Querying....".
   * 
   * @return translated "Querying...."
  
   */
  @DefaultMessage("Querying....")
  @Key("gen.querying")
  String gen_querying();

  /**
   * Translated "Querying....Complete".
   * 
   * @return translated "Querying....Complete"
  
   */
  @DefaultMessage("Querying....Complete")
  @Key("gen.queryingComplete")
  String gen_queryingComplete();

  /**
   * Translated "Received By".
   * 
   * @return translated "Received By"
  
   */
  @DefaultMessage("Received By")
  @Key("gen.receivedBy")
  String gen_receivedBy();

  /**
   * Translated "Received Date".
   * 
   * @return translated "Received Date"
  
   */
  @DefaultMessage("Received Date")
  @Key("gen.receivedDate")
  String gen_receivedDate();

  /**
   * Translated "Refresh".
   * 
   * @return translated "Refresh"
  
   */
  @DefaultMessage("Refresh")
  @Key("gen.refresh")
  String gen_refresh();

  /**
   * Translated "Release".
   * 
   * @return translated "Release"
  
   */
  @DefaultMessage("Release")
  @Key("gen.release")
  String gen_release();

  /**
   * Translated "Remove".
   * 
   * @return translated "Remove"
  
   */
  @DefaultMessage("Remove")
  @Key("gen.remove")
  String gen_remove();

  /**
   * Translated "Remove Row".
   * 
   * @return translated "Remove Row"
  
   */
  @DefaultMessage("Remove Row")
  @Key("gen.removeRow")
  String gen_removeRow();

  /**
   * Translated "Repeat".
   * 
   * @return translated "Repeat"
  
   */
  @DefaultMessage("Repeat")
  @Key("gen.repeat")
  String gen_repeat();

  /**
   * Translated "Reportable".
   * 
   * @return translated "Reportable"
  
   */
  @DefaultMessage("Reportable")
  @Key("gen.reportable")
  String gen_reportable();

  /**
   * Translated "Sample Type".
   * 
   * @return translated "Sample Type"
  
   */
  @DefaultMessage("Sample Type")
  @Key("gen.sampleType")
  String gen_sampleType();

  /**
   * Translated "Saving...".
   * 
   * @return translated "Saving..."
  
   */
  @DefaultMessage("Saving...")
  @Key("gen.saving")
  String gen_saving();

  /**
   * Translated "Saving...Complete".
   * 
   * @return translated "Saving...Complete"
  
   */
  @DefaultMessage("Saving...Complete")
  @Key("gen.savingComplete")
  String gen_savingComplete();

  /**
   * Translated "You do not have permission to access {0}".
   * 
   * @param arg0 "{0}"
   * @return translated "You do not have permission to access {0}"
  
   */
  @DefaultMessage("You do not have permission to access {0}")
  @Key("gen.screenPermException")
  String gen_screenPermException(String arg0);

  /**
   * Translated "Search".
   * 
   * @return translated "Search"
  
   */
  @DefaultMessage("Search")
  @Key("gen.search")
  String gen_search();

  /**
   * Translated "Section".
   * 
   * @return translated "Section"
  
   */
  @DefaultMessage("Section")
  @Key("gen.section")
  String gen_section();

  /**
   * Translated "Select".
   * 
   * @return translated "Select"
  
   */
  @DefaultMessage("Select")
  @Key("gen.select")
  String gen_select();

  /**
   * Translated "Select All".
   * 
   * @return translated "Select All"
  
   */
  @DefaultMessage("Select All")
  @Key("gen.selectAll")
  String gen_selectAll();

  /**
   * Translated "Sequence".
   * 
   * @return translated "Sequence"
  
   */
  @DefaultMessage("Sequence")
  @Key("gen.sequence")
  String gen_sequence();

  /**
   * Translated "Series".
   * 
   * @return translated "Series"
   */
  @DefaultMessage("Series")
  @Key("gen.series")
  String gen_series();

  /**
   * Translated "Shipping".
   * 
   * @return translated "Shipping"
  
   */
  @DefaultMessage("Shipping")
  @Key("gen.shipping")
  String gen_shipping();

  /**
   * Translated "Similar".
   * 
   * @return translated "Similar"
  
   */
  @DefaultMessage("Similar")
  @Key("gen.similar")
  String gen_similar();

  /**
   * Translated "Status".
   * 
   * @return translated "Status"
  
   */
  @DefaultMessage("Status")
  @Key("gen.status")
  String gen_status();

  /**
   * Translated "Storage Unit".
   * 
   * @return translated "Storage Unit"
  
   */
  @DefaultMessage("Storage Unit")
  @Key("gen.storageUnit")
  String gen_storageUnit();

  /**
   * Translated "Store".
   * 
   * @return translated "Store"
  
   */
  @DefaultMessage("Store")
  @Key("gen.store")
  String gen_store();

  /**
   * Translated "Test".
   * 
   * @return translated "Test"
  
   */
  @DefaultMessage("Test")
  @Key("gen.test")
  String gen_test();

  /**
   * Translated "Time".
   * 
   * @return translated "Time"
  
   */
  @DefaultMessage("Time")
  @Key("gen.time")
  String gen_time();

  /**
   * Translated "Type".
   * 
   * @return translated "Type"
  
   */
  @DefaultMessage("Type")
  @Key("gen.type")
  String gen_type();

  /**
   * Translated "Uncheck All".
   * 
   * @return translated "Uncheck All"
  
   */
  @DefaultMessage("Uncheck All")
  @Key("gen.uncheckAll")
  String gen_uncheckAll();

  /**
   * Translated "Unit".
   * 
   * @return translated "Unit"
  
   */
  @DefaultMessage("Unit")
  @Key("gen.unit")
  String gen_unit();

  /**
   * Translated "Unselect All".
   * 
   * @return translated "Unselect All"
  
   */
  @DefaultMessage("Unselect All")
  @Key("gen.unselectAll")
  String gen_unselectAll();

  /**
   * Translated "Update".
   * 
   * @return translated "Update"
  
   */
  @DefaultMessage("Update")
  @Key("gen.update")
  String gen_update();

  /**
   * Translated "Update aborted".
   * 
   * @return translated "Update aborted"
  
   */
  @DefaultMessage("Update aborted")
  @Key("gen.updateAborted")
  String gen_updateAborted();

  /**
   * Translated "Updating...".
   * 
   * @return translated "Updating..."
  
   */
  @DefaultMessage("Updating...")
  @Key("gen.updating")
  String gen_updating();

  /**
   * Translated "Updating...Complete".
   * 
   * @return translated "Updating...Complete"
  
   */
  @DefaultMessage("Updating...Complete")
  @Key("gen.updatingComplete")
  String gen_updatingComplete();

  /**
   * Translated "User".
   * 
   * @return translated "User"
  
   */
  @DefaultMessage("User")
  @Key("gen.user")
  String gen_user();

  /**
   * Translated "Username".
   * 
   * @return translated "Username"
  
   */
  @DefaultMessage("Username")
  @Key("gen.username")
  String gen_username();

  /**
   * Translated "Value".
   * 
   * @return translated "Value"
  
   */
  @DefaultMessage("Value")
  @Key("gen.value")
  String gen_value();

  /**
   * Translated "Press Ok to commit anyway or cancel to fix these warnings.".
   * 
   * @return translated "Press Ok to commit anyway or cancel to fix these warnings."
  
   */
  @DefaultMessage("Press Ok to commit anyway or cancel to fix these warnings.")
  @Key("gen.warningDialogLastLine")
  String gen_warningDialogLastLine();

  /**
   * Translated "There are warnings on the screen:".
   * 
   * @return translated "There are warnings on the screen:"
  
   */
  @DefaultMessage("There are warnings on the screen:")
  @Key("gen.warningDialogLine1")
  String gen_warningDialogLine1();

  /**
   * Translated "Worksheet".
   * 
   * @return translated "Worksheet"
  
   */
  @DefaultMessage("Worksheet")
  @Key("gen.worksheet")
  String gen_worksheet();

  /**
   * Translated "Yes".
   * 
   * @return translated "Yes"
  
   */
  @DefaultMessage("Yes")
  @Key("gen.yes")
  String gen_yes();

  /**
   * Translated "99999-9999".
   * 
   * @return translated "99999-9999"
  
   */
  @DefaultMessage("99999-9999")
  @Key("gen.zipcodePattern")
  String gen_zipcodePattern();

  /**
   * Translated "Generating report....".
   * 
   * @return translated "Generating report...."
  
   */
  @DefaultMessage("Generating report....")
  @Key("genReportMessage")
  String genReportMessage();

  /**
   * Translated "Gender".
   * 
   * @return translated "Gender"
  
   */
  @DefaultMessage("Gender")
  @Key("gender")
  String gender();

  /**
   * Translated "Generating Report...".
   * 
   * @return translated "Generating Report..."
  
   */
  @DefaultMessage("Generating Report...")
  @Key("generatingReport")
  String generatingReport();

  /**
   * Translated "Get Data".
   * 
   * @return translated "Get Data"
  
   */
  @DefaultMessage("Get Data")
  @Key("getData")
  String getData();

  /**
   * Translated "Getting report parameters".
   * 
   * @return translated "Getting report parameters"
  
   */
  @DefaultMessage("Getting report parameters")
  @Key("gettingReportParam")
  String gettingReportParam();

  /**
   * Translated "Group".
   * 
   * @return translated "Group"
  
   */
  @DefaultMessage("Group")
  @Key("group")
  String group();

  /**
   * Translated "Group Analytes".
   * 
   * @return translated "Group Analytes"
  
   */
  @DefaultMessage("Group Analytes")
  @Key("groupAnalytes")
  String groupAnalytes();

  /**
   * Translated "Group Name".
   * 
   * @return translated "Group Name"
  
   */
  @DefaultMessage("Group Name")
  @Key("groupName")
  String groupName();

  /**
   * Translated "Rslt. Grp.".
   * 
   * @return translated "Rslt. Grp."
  
   */
  @DefaultMessage("Rslt. Grp.")
  @Key("groupNum")
  String groupNum();

  /**
   * Translated "Hzrd. Lvl.".
   * 
   * @return translated "Hzrd. Lvl."
  
   */
  @DefaultMessage("Hzrd. Lvl.")
  @Key("hazardLavel")
  String hazardLavel();

  /**
   * Translated "Hazardous".
   * 
   * @return translated "Hazardous"
  
   */
  @DefaultMessage("Hazardous")
  @Key("hazardous")
  String hazardous();

  /**
   * Translated "Header".
   * 
   * @return translated "Header"
  
   */
  @DefaultMessage("Header")
  @Key("header")
  String header();

  /**
   * Translated "A header cannot be added in the middle of an analyte group".
   * 
   * @return translated "A header cannot be added in the middle of an analyte group"
  
   */
  @DefaultMessage("A header cannot be added in the middle of an analyte group")
  @Key("headerCantBeAddedInsideGroup")
  String headerCantBeAddedInsideGroup();

  /**
   * Translated "Headers and analyte rows cannot be selected together".
   * 
   * @return translated "Headers and analyte rows cannot be selected together"
  
   */
  @DefaultMessage("Headers and analyte rows cannot be selected together")
  @Key("headerCantSelWithAnalytes")
  String headerCantSelWithAnalytes();

  /**
   * Translated "History".
   * 
   * @return translated "History"
  
   */
  @DefaultMessage("History")
  @Key("history")
  String history();

  /**
   * Translated "History - Analysis".
   * 
   * @return translated "History - Analysis"
  
   */
  @DefaultMessage("History - Analysis")
  @Key("history.analysis")
  String history_analysis();

  /**
   * Translated "History - Analysis QA Event".
   * 
   * @return translated "History - Analysis QA Event"
  
   */
  @DefaultMessage("History - Analysis QA Event")
  @Key("history.analysisQA")
  String history_analysisQA();

  /**
   * Translated "History - Aux Data".
   * 
   * @return translated "History - Aux Data"
  
   */
  @DefaultMessage("History - Aux Data")
  @Key("history.auxData")
  String history_auxData();

  /**
   * Translated "History - Current Result".
   * 
   * @return translated "History - Current Result"
  
   */
  @DefaultMessage("History - Current Result")
  @Key("history.currentResult")
  String history_currentResult();

  /**
   * Translated "History - Next Of Kin".
   * 
   * @return translated "History - Next Of Kin"
  
   */
  @DefaultMessage("History - Next Of Kin")
  @Key("history.nextOfKin")
  String history_nextOfKin();

  /**
   * Translated "History - Patient".
   * 
   * @return translated "History - Patient"
  
   */
  @DefaultMessage("History - Patient")
  @Key("history.patient")
  String history_patient();

  /**
   * Translated "History - Sample".
   * 
   * @return translated "History - Sample"
  
   */
  @DefaultMessage("History - Sample")
  @Key("history.sample")
  String history_sample();

  /**
   * Translated "History - Sample Clinical".
   * 
   * @return translated "History - Sample Clinical"
  
   */
  @DefaultMessage("History - Sample Clinical")
  @Key("history.sampleClinical")
  String history_sampleClinical();

  /**
   * Translated "History - Sample Environmental".
   * 
   * @return translated "History - Sample Environmental"
  
   */
  @DefaultMessage("History - Sample Environmental")
  @Key("history.sampleEnvironmental")
  String history_sampleEnvironmental();

  /**
   * Translated "History - Sample Item".
   * 
   * @return translated "History - Sample Item"
  
   */
  @DefaultMessage("History - Sample Item")
  @Key("history.sampleItem")
  String history_sampleItem();

  /**
   * Translated "History - Sample Neonatal".
   * 
   * @return translated "History - Sample Neonatal"
  
   */
  @DefaultMessage("History - Sample Neonatal")
  @Key("history.sampleNeonatal")
  String history_sampleNeonatal();

  /**
   * Translated "History - Sample Organization".
   * 
   * @return translated "History - Sample Organization"
  
   */
  @DefaultMessage("History - Sample Organization")
  @Key("history.sampleOrganization")
  String history_sampleOrganization();

  /**
   * Translated "History - Sample PT".
   * 
   * @return translated "History - Sample PT"
   */
  @DefaultMessage("History - Sample PT")
  @Key("history.samplePT")
  String history_samplePT();

  /**
   * Translated "History - Private Well".
   * 
   * @return translated "History - Private Well"
  
   */
  @DefaultMessage("History - Private Well")
  @Key("history.samplePrivateWell")
  String history_samplePrivateWell();

  /**
   * Translated "History - Sample Project".
   * 
   * @return translated "History - Sample Project"
  
   */
  @DefaultMessage("History - Sample Project")
  @Key("history.sampleProject")
  String history_sampleProject();

  /**
   * Translated "History - Sample QA Event".
   * 
   * @return translated "History - Sample QA Event"
  
   */
  @DefaultMessage("History - Sample QA Event")
  @Key("history.sampleQA")
  String history_sampleQA();

  /**
   * Translated "History - Sample SDWIS".
   * 
   * @return translated "History - Sample SDWIS"
  
   */
  @DefaultMessage("History - Sample SDWIS")
  @Key("history.sampleSDWIS")
  String history_sampleSDWIS();

  /**
   * Translated "History - Storage".
   * 
   * @return translated "History - Storage"
  
   */
  @DefaultMessage("History - Storage")
  @Key("history.storage")
  String history_storage();

  /**
   * Translated "History - Analysis".
   * 
   * @return translated "History - Analysis"
  
   */
  @DefaultMessage("History - Analysis")
  @Key("historyAnalysis")
  String historyAnalysis();

  /**
   * Translated "History - Analysis QA Event".
   * 
   * @return translated "History - Analysis QA Event"
  
   */
  @DefaultMessage("History - Analysis QA Event")
  @Key("historyAnalysisQA")
  String historyAnalysisQA();

  /**
   * Translated "History - Aux Data".
   * 
   * @return translated "History - Aux Data"
  
   */
  @DefaultMessage("History - Aux Data")
  @Key("historyAuxData")
  String historyAuxData();

  /**
   * Translated "History - Current Result".
   * 
   * @return translated "History - Current Result"
  
   */
  @DefaultMessage("History - Current Result")
  @Key("historyCurrentResult")
  String historyCurrentResult();

  /**
   * Translated "View the history of the record".
   * 
   * @return translated "View the history of the record"
  
   */
  @DefaultMessage("View the history of the record")
  @Key("historyDescription")
  String historyDescription();

  /**
   * Translated "History - Sample".
   * 
   * @return translated "History - Sample"
  
   */
  @DefaultMessage("History - Sample")
  @Key("historySample")
  String historySample();

  /**
   * Translated "History - Sample Environmental".
   * 
   * @return translated "History - Sample Environmental"
  
   */
  @DefaultMessage("History - Sample Environmental")
  @Key("historySampleEnvironmental")
  String historySampleEnvironmental();

  /**
   * Translated "History - Sample Item".
   * 
   * @return translated "History - Sample Item"
  
   */
  @DefaultMessage("History - Sample Item")
  @Key("historySampleItem")
  String historySampleItem();

  /**
   * Translated "History - Sample Organization".
   * 
   * @return translated "History - Sample Organization"
  
   */
  @DefaultMessage("History - Sample Organization")
  @Key("historySampleOrganization")
  String historySampleOrganization();

  /**
   * Translated "History - Private Well".
   * 
   * @return translated "History - Private Well"
  
   */
  @DefaultMessage("History - Private Well")
  @Key("historySamplePrivateWell")
  String historySamplePrivateWell();

  /**
   * Translated "History - Sample Project".
   * 
   * @return translated "History - Sample Project"
  
   */
  @DefaultMessage("History - Sample Project")
  @Key("historySampleProject")
  String historySampleProject();

  /**
   * Translated "History - Sample QA Event".
   * 
   * @return translated "History - Sample QA Event"
  
   */
  @DefaultMessage("History - Sample QA Event")
  @Key("historySampleQA")
  String historySampleQA();

  /**
   * Translated "History - Sample SDWIS".
   * 
   * @return translated "History - Sample SDWIS"
  
   */
  @DefaultMessage("History - Sample SDWIS")
  @Key("historySampleSDWIS")
  String historySampleSDWIS();

  /**
   * Translated "History - Sample Specific".
   * 
   * @return translated "History - Sample Specific"
  
   */
  @DefaultMessage("History - Sample Specific")
  @Key("historySampleSpec")
  String historySampleSpec();

  /**
   * Translated "History - Storage".
   * 
   * @return translated "History - Storage"
  
   */
  @DefaultMessage("History - Storage")
  @Key("historyStorage")
  String historyStorage();

  /**
   * Translated "Hold/Refuse Organizations".
   * 
   * @return translated "Hold/Refuse Organizations"
  
   */
  @DefaultMessage("Hold/Refuse Organizations")
  @Key("holdRefuseOrganization")
  String holdRefuseOrganization();

  /**
   * Translated "% Holding Used".
   * 
   * @return translated "% Holding Used"
  
   */
  @DefaultMessage("% Holding Used")
  @Key("holding")
  String holding();

  /**
   * Translated "Home #".
   * 
   * @return translated "Home #"
  
   */
  @DefaultMessage("Home #")
  @Key("homeNumber")
  String homeNumber();

  /**
   * Translated "hours".
   * 
   * @return translated "hours"
  
   */
  @DefaultMessage("hours")
  @Key("hours")
  String hours();

  /**
   * Translated "Id".
   * 
   * @return translated "Id"
  
   */
  @DefaultMessage("Id")
  @Key("id")
  String id();

  /**
   * Translated "Identification".
   * 
   * @return translated "Identification"
  
   */
  @DefaultMessage("Identification")
  @Key("identification")
  String identification();

  /**
   * Translated "Values of type \"Date-Time\" must be of the format [yyyy-MM-dd HH:mm]".
   * 
   * @return translated "Values of type \"Date-Time\" must be of the format [yyyy-MM-dd HH:mm]"
  
   */
  @DefaultMessage("Values of type \"Date-Time\" must be of the format [yyyy-MM-dd HH:mm]")
  @Key("illegalDateTimeValueException")
  String illegalDateTimeValueException();

  /**
   * Translated "Values of type \"Date\" must be of the format [yyyy-MM-dd]".
   * 
   * @return translated "Values of type \"Date\" must be of the format [yyyy-MM-dd]"
  
   */
  @DefaultMessage("Values of type \"Date\" must be of the format [yyyy-MM-dd]")
  @Key("illegalDateValueException")
  String illegalDateValueException();

  /**
   * Translated "The default value for this record is invalid, please fix this on the Auxiliary prompt screen".
   * 
   * @return translated "The default value for this record is invalid, please fix this on the Auxiliary prompt screen"
  
   */
  @DefaultMessage("The default value for this record is invalid, please fix this on the Auxiliary prompt screen")
  @Key("illegalDefaultValueException")
  String illegalDefaultValueException();

  /**
   * Translated "The default value ''{0}'' for auxiliary field ''{1}'' is invalid, please fix this on the Auxiliary prompt screen".
   * 
   * @param arg0 "{0}"
   * @param arg1 "{1}"
   * @return translated "The default value ''{0}'' for auxiliary field ''{1}'' is invalid, please fix this on the Auxiliary prompt screen"
  
   */
  @DefaultMessage("The default value ''{0}'' for auxiliary field ''{1}'' is invalid, please fix this on the Auxiliary prompt screen")
  @Key("illegalDefaultValueForAuxFieldException")
  String illegalDefaultValueForAuxFieldException(String arg0,  String arg1);

  /**
   * Translated "This value does not exist in the system.".
   * 
   * @return translated "This value does not exist in the system."
  
   */
  @DefaultMessage("This value does not exist in the system.")
  @Key("illegalDictEntryException")
  String illegalDictEntryException();

  /**
   * Translated "Values of type \"Numeric\" must be of the format [min,max]".
   * 
   * @return translated "Values of type \"Numeric\" must be of the format [min,max]"
  
   */
  @DefaultMessage("Values of type \"Numeric\" must be of the format [min,max]")
  @Key("illegalNumericFormatException")
  String illegalNumericFormatException();

  /**
   * Translated "For values of type \"Numeric\", Min must be less than Max".
   * 
   * @return translated "For values of type \"Numeric\", Min must be less than Max"
  
   */
  @DefaultMessage("For values of type \"Numeric\", Min must be less than Max")
  @Key("illegalNumericRangeException")
  String illegalNumericRangeException();

  /**
   * Translated "Value is invalid (must be >={0} and < {1}) ".
   * 
   * @param arg0 "{0}"
   * @param arg1 "{1}"
   * @return translated "Value is invalid (must be >={0} and < {1}) "
  
   */
  @DefaultMessage("Value is invalid (must be >={0} and < {1}) ")
  @Key("illegalNumericValueException")
  String illegalNumericValueException(String arg0,  String arg1);

  /**
   * Translated "Result value is invalid".
   * 
   * @return translated "Result value is invalid"
  
   */
  @DefaultMessage("Result value is invalid")
  @Key("illegalResultValueException")
  String illegalResultValueException();

  /**
   * Translated "Result value ''{0}'' is invalid for position {1} analyte ''{2}'' column ''{3}''".
   * 
   * @param arg0 "{0}"
   * @param arg1 "{1}"
   * @param arg2 "{2}"
   * @param arg3 "{3}"
   * @return translated "Result value ''{0}'' is invalid for position {1} analyte ''{2}'' column ''{3}''"
  
   */
  @DefaultMessage("Result value ''{0}'' is invalid for position {1} analyte ''{2}'' column ''{3}''")
  @Key("illegalResultValueFormException")
  String illegalResultValueFormException(String arg0,  String arg1,  String arg2,  String arg3);

  /**
   * Translated "Values of type \"Time\" must be of the format [HH:mm]".
   * 
   * @return translated "Values of type \"Time\" must be of the format [HH:mm]"
  
   */
  @DefaultMessage("Values of type \"Time\" must be of the format [HH:mm]")
  @Key("illegalTimeValueException")
  String illegalTimeValueException();

  /**
   * Translated "Values of type \"Titer\" must be of the format [min:max] where min and max must be greater than zero".
   * 
   * @return translated "Values of type \"Titer\" must be of the format [min:max] where min and max must be greater than zero"
  
   */
  @DefaultMessage("Values of type \"Titer\" must be of the format [min:max] where min and max must be greater than zero")
  @Key("illegalTiterFormatException")
  String illegalTiterFormatException();

  /**
   * Translated "For values of type \"Titer\", Min must be less than or equal to Max".
   * 
   * @return translated "For values of type \"Titer\", Min must be less than or equal to Max"
  
   */
  @DefaultMessage("For values of type \"Titer\", Min must be less than or equal to Max")
  @Key("illegalTiterRangeException")
  String illegalTiterRangeException();

  /**
   * Translated "This unit of measure doesn''t belong to the list of units of measure associated with this test. \n Invalid unit is: {0}   ".
   * 
   * @param arg0 "{0}"
   * @return translated "This unit of measure doesn''t belong to the list of units of measure associated with this test. \n Invalid unit is: {0}   "
  
   */
  @DefaultMessage("This unit of measure doesn''t belong to the list of units of measure associated with this test. \n Invalid unit is: {0}   ")
  @Key("illegalUnitOfMeasureException")
  String illegalUnitOfMeasureException(String arg0);

  /**
   * Translated "This number must be the Id of an existing worksheet.".
   * 
   * @return translated "This number must be the Id of an existing worksheet."
  
   */
  @DefaultMessage("This number must be the Id of an existing worksheet.")
  @Key("illegalWorksheetIdException")
  String illegalWorksheetIdException();

  /**
   * Translated "Invalid user for position {0} analysis {1}".
   * 
   * @param arg0 "{0}"
   * @param arg1 "{1}"
   * @return translated "Invalid user for position {0} analysis {1}"
  
   */
  @DefaultMessage("Invalid user for position {0} analysis {1}")
  @Key("illegalWorksheetUserFormException")
  String illegalWorksheetUserFormException(String arg0,  String arg1);

  /**
   * Translated "Import Analytes ".
   * 
   * @return translated "Import Analytes "
  
   */
  @DefaultMessage("Import Analytes ")
  @Key("importAnalytes")
  String importAnalytes();

  /**
   * Translated "Imported the report to organization data, not the report to auxiliary data".
   * 
   * @return translated "Imported the report to organization data, not the report to auxiliary data"
  
   */
  @DefaultMessage("Imported the report to organization data, not the report to auxiliary data")
  @Key("importOrderReportToException")
  String importOrderReportToException();

  /**
   * Translated "In Progress".
   * 
   * @return translated "In Progress"
  
   */
  @DefaultMessage("In Progress")
  @Key("inProgress")
  String inProgress();

  /**
   * Translated "The unit ''{0}'' is inactive and was not assigned to ''{1}, {2}''".
   * 
   * @param arg0 "{0}"
   * @param arg1 "{1}"
   * @param arg2 "{2}"
   * @return translated "The unit ''{0}'' is inactive and was not assigned to ''{1}, {2}''"
  
   */
  @DefaultMessage("The unit ''{0}'' is inactive and was not assigned to ''{1}, {2}''")
  @Key("inactiveAnalysisUnitWarning")
  String inactiveAnalysisUnitWarning(String arg0,  String arg1,  String arg2);

  /**
   * Translated "The auxiliary group ''{0}'' is inactive and was not added to the sample ".
   * 
   * @param arg0 "{0}"
   * @return translated "The auxiliary group ''{0}'' is inactive and was not added to the sample "
  
   */
  @DefaultMessage("The auxiliary group ''{0}'' is inactive and was not added to the sample ")
  @Key("inactiveAuxGroupWarning")
  String inactiveAuxGroupWarning(String arg0);

  /**
   * Translated "The container ''{0}'' is inactive and was not assigned to Item # {1,number,#0}".
   * 
   * @param arg0 "{0}"
   * @param arg1 "{1,number,#0}"
   * @return translated "The container ''{0}'' is inactive and was not assigned to Item # {1,number,#0}"
  
   */
  @DefaultMessage("The container ''{0}'' is inactive and was not assigned to Item # {1,number,#0}")
  @Key("inactiveContainerWarning")
  String inactiveContainerWarning(String arg0,  Integer arg1);

  /**
   * Translated "The entry {0} under category {1} has been deactivate".
   * 
   * @param arg0 "{0}"
   * @param arg1 "{1}"
   * @return translated "The entry {0} under category {1} has been deactivate"
  
   */
  @DefaultMessage("The entry {0} under category {1} has been deactivate")
  @Key("inactiveDictionaryException")
  String inactiveDictionaryException(String arg0,  String arg1);

  /**
   * Translated "The organization ''{0}'' is inactive and was not added to the sample ".
   * 
   * @param arg0 "{0}"
   * @return translated "The organization ''{0}'' is inactive and was not added to the sample "
  
   */
  @DefaultMessage("The organization ''{0}'' is inactive and was not added to the sample ")
  @Key("inactiveOrgWarning")
  String inactiveOrgWarning(String arg0);

  /**
   * Translated "The project ''{0}'' is inactive and was not added to the sample ".
   * 
   * @param arg0 "{0}"
   * @return translated "The project ''{0}'' is inactive and was not added to the sample "
  
   */
  @DefaultMessage("The project ''{0}'' is inactive and was not added to the sample ")
  @Key("inactiveProjectWarning")
  String inactiveProjectWarning(String arg0);

  /**
   * Translated "The unit ''{0}'' is inactive and was not assigned to Item # {1,number,#0}".
   * 
   * @param arg0 "{0}"
   * @param arg1 "{1,number,#0}"
   * @return translated "The unit ''{0}'' is inactive and was not assigned to Item # {1,number,#0}"
  
   */
  @DefaultMessage("The unit ''{0}'' is inactive and was not assigned to Item # {1,number,#0}")
  @Key("inactiveSampleItemUnitWarning")
  String inactiveSampleItemUnitWarning(String arg0,  Integer arg1);

  /**
   * Translated "The sample type ''{0}'' is inactive and was not assigned to Item # {1,number,#0}".
   * 
   * @param arg0 "{0}"
   * @param arg1 "{1,number,#0}"
   * @return translated "The sample type ''{0}'' is inactive and was not assigned to Item # {1,number,#0}"
  
   */
  @DefaultMessage("The sample type ''{0}'' is inactive and was not assigned to Item # {1,number,#0}")
  @Key("inactiveSampleTypeWarning")
  String inactiveSampleTypeWarning(String arg0,  Integer arg1);

  /**
   * Translated "The source ''{0}'' is inactive and was not assigned to Item # {1,number,#0}".
   * 
   * @param arg0 "{0}"
   * @param arg1 "{1,number,#0}"
   * @return translated "The source ''{0}'' is inactive and was not assigned to Item # {1,number,#0}"
  
   */
  @DefaultMessage("The source ''{0}'' is inactive and was not assigned to Item # {1,number,#0}")
  @Key("inactiveSourceWarning")
  String inactiveSourceWarning(String arg0,  Integer arg1);

  /**
   * Translated "''{0}, {1}'' cannot be imported because there is no matching active test".
   * 
   * @param arg0 "{0}"
   * @param arg1 "{1}"
   * @return translated "''{0}, {1}'' cannot be imported because there is no matching active test"
  
   */
  @DefaultMessage("''{0}, {1}'' cannot be imported because there is no matching active test")
  @Key("inactiveTestOnOrderException")
  String inactiveTestOnOrderException(String arg0,  String arg1);

  /**
   * Translated "''{0}, {1}'' is inactive and was not added to the sample ".
   * 
   * @param arg0 "{0}"
   * @param arg1 "{1}"
   * @return translated "''{0}, {1}'' is inactive and was not added to the sample "
  
   */
  @DefaultMessage("''{0}, {1}'' is inactive and was not added to the sample ")
  @Key("inactiveTestWarning")
  String inactiveTestWarning(String arg0,  String arg1);

  /**
   * Translated "Include".
   * 
   * @return translated "Include"
  
   */
  @DefaultMessage("Include")
  @Key("include")
  String include();

  /**
   * Translated "Include All Analyses".
   * 
   * @return translated "Include All Analyses"
  
   */
  @DefaultMessage("Include All Analyses")
  @Key("includeAllAnalyses")
  String includeAllAnalyses();

  /**
   * Translated "Init-Compl".
   * 
   * @return translated "Init-Compl"
  
   */
  @DefaultMessage("Init-Compl")
  @Key("ini-cmp")
  String ini_cmp();

  /**
   * Translated "Init-Rel".
   * 
   * @return translated "Init-Rel"
  
   */
  @DefaultMessage("Init-Rel")
  @Key("ini-rel")
  String ini_rel();

  /**
   * Translated "Initiated".
   * 
   * @return translated "Initiated"
  
   */
  @DefaultMessage("Initiated")
  @Key("initiated")
  String initiated();

  /**
   * Translated "Analysis Above".
   * 
   * @return translated "Analysis Above"
  
   */
  @DefaultMessage("Analysis Above")
  @Key("insertAnalysisAbove")
  String insertAnalysisAbove();

  /**
   * Translated "Analysis Below".
   * 
   * @return translated "Analysis Below"
  
   */
  @DefaultMessage("Analysis Below")
  @Key("insertAnalysisBelow")
  String insertAnalysisBelow();

  /**
   * Translated "Insert From Worksheet".
   * 
   * @return translated "Insert From Worksheet"
  
   */
  @DefaultMessage("Insert From Worksheet")
  @Key("insertAnalysisWorksheet")
  String insertAnalysisWorksheet();

  /**
   * Translated "From QC Table Above".
   * 
   * @return translated "From QC Table Above"
  
   */
  @DefaultMessage("From QC Table Above")
  @Key("insertFromQcTableAbove")
  String insertFromQcTableAbove();

  /**
   * Translated "From QC Table Below".
   * 
   * @return translated "From QC Table Below"
  
   */
  @DefaultMessage("From QC Table Below")
  @Key("insertFromQcTableBelow")
  String insertFromQcTableBelow();

  /**
   * Translated "From Another Worksheet Above".
   * 
   * @return translated "From Another Worksheet Above"
  
   */
  @DefaultMessage("From Another Worksheet Above")
  @Key("insertFromWorksheetAbove")
  String insertFromWorksheetAbove();

  /**
   * Translated "From Another Worksheet Below".
   * 
   * @return translated "From Another Worksheet Below"
  
   */
  @DefaultMessage("From Another Worksheet Below")
  @Key("insertFromWorksheetBelow")
  String insertFromWorksheetBelow();

  /**
   * Translated "Insert QC From Lookup".
   * 
   * @return translated "Insert QC From Lookup"
  
   */
  @DefaultMessage("Insert QC From Lookup")
  @Key("insertQCLookup")
  String insertQCLookup();

  /**
   * Translated "Instrument".
   * 
   * @return translated "Instrument"
  
   */
  @DefaultMessage("Instrument")
  @Key("instrument")
  String instrument();

  /**
   * Translated "Barcode Type".
   * 
   * @return translated "Barcode Type"
  
   */
  @DefaultMessage("Barcode Type")
  @Key("instrumentBarcode.barcodeType")
  String instrumentBarcode_barcodeType();

  /**
   * Translated "Instrument Barcode Report".
   * 
   * @return translated "Instrument Barcode Report"
  
   */
  @DefaultMessage("Instrument Barcode Report")
  @Key("instrumentBarcode.instrumentBarcodeReport")
  String instrumentBarcode_instrumentBarcodeReport();

  /**
   * Translated "Worksheet Id".
   * 
   * @return translated "Worksheet Id"
  
   */
  @DefaultMessage("Worksheet Id")
  @Key("instrumentBarcode.worksheetId")
  String instrumentBarcode_worksheetId();

  /**
   * Translated "Define instruments that can be used in the laboratory.".
   * 
   * @return translated "Define instruments that can be used in the laboratory."
  
   */
  @DefaultMessage("Define instruments that can be used in the laboratory.")
  @Key("instrumentDescription")
  String instrumentDescription();

  /**
   * Translated "History - Instrument".
   * 
   * @return translated "History - Instrument"
  
   */
  @DefaultMessage("History - Instrument")
  @Key("instrumentHistory")
  String instrumentHistory();

  /**
   * Translated "Accession".
   * 
   * @return translated "Accession"
  
   */
  @DefaultMessage("Accession")
  @Key("instrumentInterface.accessionNumber")
  String instrumentInterface_accessionNumber();

  /**
   * Translated "Analyst".
   * 
   * @return translated "Analyst"
  
   */
  @DefaultMessage("Analyst")
  @Key("instrumentInterface.analyst")
  String instrumentInterface_analyst();

  /**
   * Translated "Instrument name ''{0}'' in import file is different from the one already assigned to the worksheet ''{1}''.".
   * 
   * @param arg0 "{0}"
   * @param arg1 "{1}"
   * @return translated "Instrument name ''{0}'' in import file is different from the one already assigned to the worksheet ''{1}''."
  
   */
  @DefaultMessage("Instrument name ''{0}'' in import file is different from the one already assigned to the worksheet ''{1}''.")
  @Key("instrumentInterface.differentInstrumentName")
  String instrumentInterface_differentInstrumentName(String arg0,  String arg1);

  /**
   * Translated "Injection".
   * 
   * @return translated "Injection"
  
   */
  @DefaultMessage("Injection")
  @Key("instrumentInterface.injectionDateTime")
  String instrumentInterface_injectionDateTime();

  /**
   * Translated "Instrument ID".
   * 
   * @return translated "Instrument ID"
  
   */
  @DefaultMessage("Instrument ID")
  @Key("instrumentInterface.instrumentId")
  String instrumentInterface_instrumentId();

  /**
   * Translated "Invalid file name for instrument import file.".
   * 
   * @return translated "Invalid file name for instrument import file."
  
   */
  @DefaultMessage("Invalid file name for instrument import file.")
  @Key("instrumentInterface.invalidFileName")
  String instrumentInterface_invalidFileName();

  /**
   * Translated "Invalid instrument name ''{0}''".
   * 
   * @param arg0 "{0}"
   * @return translated "Invalid instrument name ''{0}''"
  
   */
  @DefaultMessage("Invalid instrument name ''{0}''")
  @Key("instrumentInterface.invalidInstrumentName")
  String instrumentInterface_invalidInstrumentName(String arg0);

  /**
   * Translated "Path to instrument file directory is missing. Please contact the system administrator.".
   * 
   * @return translated "Path to instrument file directory is missing. Please contact the system administrator."
  
   */
  @DefaultMessage("Path to instrument file directory is missing. Please contact the system administrator.")
  @Key("instrumentInterface.missingPath")
  String instrumentInterface_missingPath();

  /**
   * Translated "Missing required column ''{0}''.".
   * 
   * @param arg0 "{0}"
   * @return translated "Missing required column ''{0}''."
  
   */
  @DefaultMessage("Missing required column ''{0}''.")
  @Key("instrumentInterface.missingRequiredColumn")
  String instrumentInterface_missingRequiredColumn(String arg0);

  /**
   * Translated "Worksheet #{0,number,#0} has no analyses at position #{1,number,#0}.".
   * 
   * @param arg0 "{0,number,#0}"
   * @param arg1 "{1,number,#0}"
   * @return translated "Worksheet #{0,number,#0} has no analyses at position #{1,number,#0}."
  
   */
  @DefaultMessage("Worksheet #{0,number,#0} has no analyses at position #{1,number,#0}.")
  @Key("instrumentInterface.worksheetHasNoAnalysesAtPosition")
  String instrumentInterface_worksheetHasNoAnalysesAtPosition(Integer arg0,  Integer arg1);

  /**
   * Translated "Worksheet #{0,number,#0} has no items.".
   * 
   * @param arg0 "{0,number,#0}"
   * @return translated "Worksheet #{0,number,#0} has no items."
  
   */
  @DefaultMessage("Worksheet #{0,number,#0} has no items.")
  @Key("instrumentInterface.worksheetHasNoItems")
  String instrumentInterface_worksheetHasNoItems(Integer arg0);

  /**
   * Translated "Worksheet #{0,number,#0} not found for instrument import file.".
   * 
   * @param arg0 "{0,number,#0}"
   * @return translated "Worksheet #{0,number,#0} not found for instrument import file."
  
   */
  @DefaultMessage("Worksheet #{0,number,#0} not found for instrument import file.")
  @Key("instrumentInterface.worksheetNotFound")
  String instrumentInterface_worksheetNotFound(Integer arg0);

  /**
   * Translated "Worksheet #{0,number,#0} it not in ''Working'' status.".
   * 
   * @param arg0 "{0,number,#0}"
   * @return translated "Worksheet #{0,number,#0} it not in ''Working'' status."
  
   */
  @DefaultMessage("Worksheet #{0,number,#0} it not in ''Working'' status.")
  @Key("instrumentInterface.worksheetWrongStatus")
  String instrumentInterface_worksheetWrongStatus(Integer arg0);

  /**
   * Translated "History - Instrument Log".
   * 
   * @return translated "History - Instrument Log"
  
   */
  @DefaultMessage("History - Instrument Log")
  @Key("instrumentLogHistory")
  String instrumentLogHistory();

  /**
   * Translated "Define instruments used for testing.".
   * 
   * @return translated "Define instruments used for testing."
  
   */
  @DefaultMessage("Define instruments used for testing.")
  @Key("instrumentMainDescription")
  String instrumentMainDescription();

  /**
   * Translated "Instrument Name".
   * 
   * @return translated "Instrument Name"
  
   */
  @DefaultMessage("Instrument Name")
  @Key("instrumentName")
  String instrumentName();

  /**
   * Translated "There is already an instrument in the system with the same name and serial number.".
   * 
   * @return translated "There is already an instrument in the system with the same name and serial number."
  
   */
  @DefaultMessage("There is already an instrument in the system with the same name and serial number.")
  @Key("instrumentUniqueException")
  String instrumentUniqueException();

  /**
   * Translated "Insufficient privileges to add - {0} : {1}".
   * 
   * @param arg0 "{0}"
   * @param arg1 "{1}"
   * @return translated "Insufficient privileges to add - {0} : {1}"
  
   */
  @DefaultMessage("Insufficient privileges to add - {0} : {1}")
  @Key("insufficientPrivilegesAddTest")
  String insufficientPrivilegesAddTest(String arg0,  String arg1);

  /**
   * Translated "Insufficient privileges to add - {0} : {1} for {2} section".
   * 
   * @param arg0 "{0}"
   * @param arg1 "{1}"
   * @param arg2 "{2}"
   * @return translated "Insufficient privileges to add - {0} : {1} for {2} section"
  
   */
  @DefaultMessage("Insufficient privileges to add - {0} : {1} for {2} section")
  @Key("insufficientPrivilegesAddTestForSection")
  String insufficientPrivilegesAddTestForSection(String arg0,  String arg1,  String arg2);

  /**
   * Translated "Insufficient privileges to cancel - {0} : {1}".
   * 
   * @param arg0 "{0}"
   * @param arg1 "{1}"
   * @return translated "Insufficient privileges to cancel - {0} : {1}"
  
   */
  @DefaultMessage("Insufficient privileges to cancel - {0} : {1}")
  @Key("insufficientPrivilegesCancelAnalysis")
  String insufficientPrivilegesCancelAnalysis(String arg0,  String arg1);

  /**
   * Translated "Insufficient privileges to complete - {0} : {1}".
   * 
   * @param arg0 "{0}"
   * @param arg1 "{1}"
   * @return translated "Insufficient privileges to complete - {0} : {1}"
  
   */
  @DefaultMessage("Insufficient privileges to complete - {0} : {1}")
  @Key("insufficientPrivilegesCompleteAnalysis")
  String insufficientPrivilegesCompleteAnalysis(String arg0,  String arg1);

  /**
   * Translated "Insufficient privileges to initiate - {0} : {1}".
   * 
   * @param arg0 "{0}"
   * @param arg1 "{1}"
   * @return translated "Insufficient privileges to initiate - {0} : {1}"
  
   */
  @DefaultMessage("Insufficient privileges to initiate - {0} : {1}")
  @Key("insufficientPrivilegesInitiateAnalysis")
  String insufficientPrivilegesInitiateAnalysis(String arg0,  String arg1);

  /**
   * Translated "Insufficient privileges to release - {0} : {1}".
   * 
   * @param arg0 "{0}"
   * @param arg1 "{1}"
   * @return translated "Insufficient privileges to release - {0} : {1}"
  
   */
  @DefaultMessage("Insufficient privileges to release - {0} : {1}")
  @Key("insufficientPrivilegesReleaseAnalysis")
  String insufficientPrivilegesReleaseAnalysis(String arg0,  String arg1);

  /**
   * Translated "Insufficient privileges to un-initiate - {0} : {1}".
   * 
   * @param arg0 "{0}"
   * @param arg1 "{1}"
   * @return translated "Insufficient privileges to un-initiate - {0} : {1}"
  
   */
  @DefaultMessage("Insufficient privileges to un-initiate - {0} : {1}")
  @Key("insufficientPrivilegesUnInitiateAnalysis")
  String insufficientPrivilegesUnInitiateAnalysis(String arg0,  String arg1);

  /**
   * Translated "Insufficient privileges to unrelease - {0} : {1}".
   * 
   * @param arg0 "{0}"
   * @param arg1 "{1}"
   * @return translated "Insufficient privileges to unrelease - {0} : {1}"
  
   */
  @DefaultMessage("Insufficient privileges to unrelease - {0} : {1}")
  @Key("insufficientPrivilegesUnreleaseAnalysis")
  String insufficientPrivilegesUnreleaseAnalysis(String arg0,  String arg1);

  /**
   * Translated "Internal".
   * 
   * @return translated "Internal"
  
   */
  @DefaultMessage("Internal")
  @Key("internal")
  String internal();

  /**
   * Translated "Internal Order".
   * 
   * @return translated "Internal Order"
  
   */
  @DefaultMessage("Internal Order")
  @Key("internalOrder")
  String internalOrder();

  /**
   * Translated "Order items from in-house store.".
   * 
   * @return translated "Order items from in-house store."
  
   */
  @DefaultMessage("Order items from in-house store.")
  @Key("internalOrderDescription")
  String internalOrderDescription();

  /**
   * Translated "This interval is overlapping with intervals associated with one or more log entries. ".
   * 
   * @return translated "This interval is overlapping with intervals associated with one or more log entries. "
  
   */
  @DefaultMessage("This interval is overlapping with intervals associated with one or more log entries. ")
  @Key("intervalOverlapException")
  String intervalOverlapException();

  /**
   * Translated "Int Cmts".
   * 
   * @return translated "Int Cmts"
  
   */
  @DefaultMessage("Int Cmts")
  @Key("intrnlCmnts")
  String intrnlCmnts();

  /**
   * Translated "History - Component".
   * 
   * @return translated "History - Component"
  
   */
  @DefaultMessage("History - Component")
  @Key("invComponentHistory")
  String invComponentHistory();

  /**
   * Translated "History - Inventory Item".
   * 
   * @return translated "History - Inventory Item"
  
   */
  @DefaultMessage("History - Inventory Item")
  @Key("invItemHistory")
  String invItemHistory();

  /**
   * Translated "History - Location/Quantity".
   * 
   * @return translated "History - Location/Quantity"
  
   */
  @DefaultMessage("History - Location/Quantity")
  @Key("invLocationHistory")
  String invLocationHistory();

  /**
   * Translated "Status cannot be changed to {0} for position {1} analysis {2}".
   * 
   * @param arg0 "{0}"
   * @param arg1 "{1}"
   * @param arg2 "{2}"
   * @return translated "Status cannot be changed to {0} for position {1} analysis {2}"
  
   */
  @DefaultMessage("Status cannot be changed to {0} for position {1} analysis {2}")
  @Key("invalidAnalysisStatusChange")
  String invalidAnalysisStatusChange(String arg0,  String arg1,  String arg2);

  /**
   * Translated "Not able to find EJB with the entered lookup path".
   * 
   * @return translated "Not able to find EJB with the entered lookup path"
  
   */
  @DefaultMessage("Not able to find EJB with the entered lookup path")
  @Key("invalidBeanPath")
  String invalidBeanPath();

  /**
   * Translated "Cost needs to be greater than 0".
   * 
   * @return translated "Cost needs to be greater than 0"
  
   */
  @DefaultMessage("Cost needs to be greater than 0")
  @Key("invalidCostException")
  String invalidCostException();

  /**
   * Translated "Invalid Cron Tab expression has been entered".
   * 
   * @return translated "Invalid Cron Tab expression has been entered"
  
   */
  @DefaultMessage("Invalid Cron Tab expression has been entered")
  @Key("invalidCronTab")
  String invalidCronTab();

  /**
   * Translated "Invalid Date format entered.".
   * 
   * @return translated "Invalid Date format entered."
  
   */
  @DefaultMessage("Invalid Date format entered.")
  @Key("invalidDateFormat")
  String invalidDateFormat();

  /**
   * Translated "Value entered is not a valid double.".
   * 
   * @return translated "Value entered is not a valid double."
  
   */
  @DefaultMessage("Value entered is not a valid double.")
  @Key("invalidDouble")
  String invalidDouble();

  /**
   * Translated "{0} is an invalid entry, please try again".
   * 
   * @param arg0 "{0}"
   * @return translated "{0} is an invalid entry, please try again"
  
   */
  @DefaultMessage("{0} is an invalid entry, please try again")
  @Key("invalidEntryException")
  String invalidEntryException(String arg0);

  /**
   * Translated "Invalid format for email address: {0} ".
   * 
   * @param arg0 "{0}"
   * @return translated "Invalid format for email address: {0} "
  
   */
  @DefaultMessage("Invalid format for email address: {0} ")
  @Key("invalidFormatEmailException")
  String invalidFormatEmailException(String arg0);

  /**
   * Translated "Quantity needs to be at least 0".
   * 
   * @return translated "Quantity needs to be at least 0"
  
   */
  @DefaultMessage("Quantity needs to be at least 0")
  @Key("invalidItemQuantityException")
  String invalidItemQuantityException();

  /**
   * Translated "Quantity needs to be at least 1".
   * 
   * @return translated "Quantity needs to be at least 1"
  
   */
  @DefaultMessage("Quantity needs to be at least 1")
  @Key("invalidLocationQuantityException")
  String invalidLocationQuantityException();

  /**
   * Translated "No valid method with this signature found".
   * 
   * @return translated "No valid method with this signature found"
  
   */
  @DefaultMessage("No valid method with this signature found")
  @Key("invalidMethod")
  String invalidMethod();

  /**
   * Translated "Number of packages needs to be at least 1".
   * 
   * @return translated "Number of packages needs to be at least 1"
  
   */
  @DefaultMessage("Number of packages needs to be at least 1")
  @Key("invalidNumPackagesException")
  String invalidNumPackagesException();

  /**
   * Translated "Number of parameters does match for method entered  ".
   * 
   * @return translated "Number of parameters does match for method entered  "
  
   */
  @DefaultMessage("Number of parameters does match for method entered  ")
  @Key("invalidNumParams")
  String invalidNumParams();

  /**
   * Translated "Pws ID is invalid".
   * 
   * @return translated "Pws ID is invalid"
  
   */
  @DefaultMessage("Pws ID is invalid")
  @Key("invalidPwsException")
  String invalidPwsException();

  /**
   * Translated "Input is not a valid query.".
   * 
   * @return translated "Input is not a valid query."
  
   */
  @DefaultMessage("Input is not a valid query.")
  @Key("invalidQueryFormat")
  String invalidQueryFormat();

  /**
   * Translated "Invalid result group.".
   * 
   * @return translated "Invalid result group."
  
   */
  @DefaultMessage("Invalid result group.")
  @Key("invalidResultGroupException")
  String invalidResultGroupException();

  /**
   * Translated "The sample type for the container with Item # {0} is invalid for ''{1}''  ".
   * 
   * @param arg0 "{0}"
   * @param arg1 "{1}"
   * @return translated "The sample type for the container with Item # {0} is invalid for ''{1}''  "
  
   */
  @DefaultMessage("The sample type for the container with Item # {0} is invalid for ''{1}''  ")
  @Key("invalidSampleTypeForTestWarning")
  String invalidSampleTypeForTestWarning(String arg0,  String arg1);

  /**
   * Translated "Invalid value for selected type".
   * 
   * @return translated "Invalid value for selected type"
  
   */
  @DefaultMessage("Invalid value for selected type")
  @Key("invalidValueException")
  String invalidValueException();

  /**
   * Translated "Store value needs to be entered before you can add an inventory item".
   * 
   * @return translated "Store value needs to be entered before you can add an inventory item"
  
   */
  @DefaultMessage("Store value needs to be entered before you can add an inventory item")
  @Key("inventoryAdjItemAutoException")
  String inventoryAdjItemAutoException();

  /**
   * Translated "Store value needs to be entered before you can add an inventory location".
   * 
   * @return translated "Store value needs to be entered before you can add an inventory location"
  
   */
  @DefaultMessage("Store value needs to be entered before you can add an inventory location")
  @Key("inventoryAdjLocAutoException")
  String inventoryAdjLocAutoException();

  /**
   * Translated "Inventory Adjustment".
   * 
   * @return translated "Inventory Adjustment"
  
   */
  @DefaultMessage("Inventory Adjustment")
  @Key("inventoryAdjustment")
  String inventoryAdjustment();

  /**
   * Translated "Manage inventory discrepancies.".
   * 
   * @return translated "Manage inventory discrepancies."
  
   */
  @DefaultMessage("Manage inventory discrepancies.")
  @Key("inventoryAdjustmentDescription")
  String inventoryAdjustmentDescription();

  /**
   * Translated "History - Adjustment".
   * 
   * @return translated "History - Adjustment"
  
   */
  @DefaultMessage("History - Adjustment")
  @Key("inventoryAdjustmentHistory")
  String inventoryAdjustmentHistory();

  /**
   * Translated "History - Location Adjustment".
   * 
   * @return translated "History - Location Adjustment"
  
   */
  @DefaultMessage("History - Location Adjustment")
  @Key("inventoryAdjustmentLocationHistory")
  String inventoryAdjustmentLocationHistory();

  /**
   * Translated "Component needs to come from same store as inventory item".
   * 
   * @return translated "Component needs to come from same store as inventory item"
  
   */
  @DefaultMessage("Component needs to come from same store as inventory item")
  @Key("inventoryComponentStoreException")
  String inventoryComponentStoreException();

  /**
   * Translated "Inventory Item".
   * 
   * @return translated "Inventory Item"
  
   */
  @DefaultMessage("Inventory Item")
  @Key("inventoryItem")
  String inventoryItem();

  /**
   * Translated "Define the list of inventory items used in the laboratory such as reagents, bottles, etc.".
   * 
   * @return translated "Define the list of inventory items used in the laboratory such as reagents, bottles, etc."
  
   */
  @DefaultMessage("Define the list of inventory items used in the laboratory such as reagents, bottles, etc.")
  @Key("inventoryItemDescription")
  String inventoryItemDescription();

  /**
   * Translated "A record with this name and store already exists. Please enter a unique value for this field".
   * 
   * @return translated "A record with this name and store already exists. Please enter a unique value for this field"
  
   */
  @DefaultMessage("A record with this name and store already exists. Please enter a unique value for this field")
  @Key("inventoryItemNameUniqueException")
  String inventoryItemNameUniqueException();

  /**
   * Translated "Please specify a store before adding components".
   * 
   * @return translated "Please specify a store before adding components"
  
   */
  @DefaultMessage("Please specify a store before adding components")
  @Key("inventoryNoStoreException")
  String inventoryNoStoreException();

  /**
   * Translated "Inventory/Order".
   * 
   * @return translated "Inventory/Order"
  
   */
  @DefaultMessage("Inventory/Order")
  @Key("inventoryOrder")
  String inventoryOrder();

  /**
   * Translated "Inventory Receipt".
   * 
   * @return translated "Inventory Receipt"
  
   */
  @DefaultMessage("Inventory Receipt")
  @Key("inventoryReceipt")
  String inventoryReceipt();

  /**
   * Translated "Manage items coming into inventory from external sources.".
   * 
   * @return translated "Manage items coming into inventory from external sources."
  
   */
  @DefaultMessage("Manage items coming into inventory from external sources.")
  @Key("inventoryReceiptDescription")
  String inventoryReceiptDescription();

  /**
   * Translated "Order # is invalid".
   * 
   * @return translated "Order # is invalid"
  
   */
  @DefaultMessage("Order # is invalid")
  @Key("inventoryReceiptInvalidOrderIdException")
  String inventoryReceiptInvalidOrderIdException();

  /**
   * Translated "Inventory Transfer".
   * 
   * @return translated "Inventory Transfer"
  
   */
  @DefaultMessage("Inventory Transfer")
  @Key("inventoryTransfer")
  String inventoryTransfer();

  /**
   * Translated "Manage the transfer of inventory items within stores and locations.".
   * 
   * @return translated "Manage the transfer of inventory items within stores and locations."
  
   */
  @DefaultMessage("Manage the transfer of inventory items within stores and locations.")
  @Key("inventoryTransferDescription")
  String inventoryTransferDescription();

  /**
   * Translated "FROM inventory item needs to be filled out before the TO inventory item".
   * 
   * @return translated "FROM inventory item needs to be filled out before the TO inventory item"
  
   */
  @DefaultMessage("FROM inventory item needs to be filled out before the TO inventory item")
  @Key("inventoryTransferFromItemException")
  String inventoryTransferFromItemException();

  /**
   * Translated "There is not enough quantity on hand in the from location to fulfill this transfer".
   * 
   * @return translated "There is not enough quantity on hand in the from location to fulfill this transfer"
  
   */
  @DefaultMessage("There is not enough quantity on hand in the from location to fulfill this transfer")
  @Key("inventoryTransferQtyException")
  String inventoryTransferQtyException();

  /**
   * Translated "Item".
   * 
   * @return translated "Item"
  
   */
  @DefaultMessage("Item")
  @Key("item")
  String item();

  /**
   * Translated "Items/Analyses".
   * 
   * @return translated "Items/Analyses"
  
   */
  @DefaultMessage("Items/Analyses")
  @Key("itemAnalyses")
  String itemAnalyses();

  /**
   * Translated "An inventory item cannot be flagged as both Bulk and Lot # Required".
   * 
   * @return translated "An inventory item cannot be flagged as both Bulk and Lot # Required"
  
   */
  @DefaultMessage("An inventory item cannot be flagged as both Bulk and Lot # Required")
  @Key("itemCantBeBulkAndLotReqException")
  String itemCantBeBulkAndLotReqException();

  /**
   * Translated "An inventory item cannot be flagged as both Bulk and Serial # Required".
   * 
   * @return translated "An inventory item cannot be flagged as both Bulk and Serial # Required"
  
   */
  @DefaultMessage("An inventory item cannot be flagged as both Bulk and Serial # Required")
  @Key("itemCantBeBulkAndSerialReqException")
  String itemCantBeBulkAndSerialReqException();

  /**
   * Translated "This item does not have any locations specified".
   * 
   * @return translated "This item does not have any locations specified"
  
   */
  @DefaultMessage("This item does not have any locations specified")
  @Key("itemDoesntHaveLocation")
  String itemDoesntHaveLocation();

  /**
   * Translated "This location must not be an existing one".
   * 
   * @return translated "This location must not be an existing one"
  
   */
  @DefaultMessage("This location must not be an existing one")
  @Key("itemExistAtLocationException")
  String itemExistAtLocationException();

  /**
   * Translated "Items flagged as Do Not Inventory cannot be filled".
   * 
   * @return translated "Items flagged as Do Not Inventory cannot be filled"
  
   */
  @DefaultMessage("Items flagged as Do Not Inventory cannot be filled")
  @Key("itemFlagDontInvCantBeFilled")
  String itemFlagDontInvCantBeFilled();

  /**
   * Translated "Inventory items flagged as \"Do Not Inventory\" cannot be transferred".
   * 
   * @return translated "Inventory items flagged as \"Do Not Inventory\" cannot be transferred"
  
   */
  @DefaultMessage("Inventory items flagged as \"Do Not Inventory\" cannot be transferred")
  @Key("itemFlaggedDontInventoryException")
  String itemFlaggedDontInventoryException();

  /**
   * Translated "Item Information".
   * 
   * @return translated "Item Information"
  
   */
  @DefaultMessage("Item Information")
  @Key("itemInformation")
  String itemInformation();

  /**
   * Translated "This location is not an existing one ".
   * 
   * @return translated "This location is not an existing one "
  
   */
  @DefaultMessage("This location is not an existing one ")
  @Key("itemNotExistAtLocationException")
  String itemNotExistAtLocationException();

  /**
   * Translated "Item #".
   * 
   * @return translated "Item #"
  
   */
  @DefaultMessage("Item #")
  @Key("itemNum")
  String itemNum();

  /**
   * Translated "Test Row {0} - Item # must not be negative".
   * 
   * @param arg0 "{0}"
   * @return translated "Test Row {0} - Item # must not be negative"
  
   */
  @DefaultMessage("Test Row {0} - Item # must not be negative")
  @Key("itemNumCantBeNegativeException")
  String itemNumCantBeNegativeException(String arg0);

  /**
   * Translated "Test Row {0} - Item # is required".
   * 
   * @param arg0 "{0}"
   * @return translated "Test Row {0} - Item # is required"
  
   */
  @DefaultMessage("Test Row {0} - Item # is required")
  @Key("itemNumRequiredException")
  String itemNumRequiredException(String arg0);

  /**
   * Translated "This item is serialized, you can only create one at a time".
   * 
   * @return translated "This item is serialized, you can only create one at a time"
  
   */
  @DefaultMessage("This item is serialized, you can only create one at a time")
  @Key("itemSerializedException")
  String itemSerializedException();

  /**
   * Translated "Items".
   * 
   * @return translated "Items"
  
   */
  @DefaultMessage("Items")
  @Key("items")
  String items();

  /**
   * Translated "Sample Items and Analyses".
   * 
   * @return translated "Sample Items and Analyses"
  
   */
  @DefaultMessage("Sample Items and Analyses")
  @Key("itemsAndAnalyses")
  String itemsAndAnalyses();

  /**
   * Translated "Some items cannot be moved as they are already stored at the destination location".
   * 
   * @return translated "Some items cannot be moved as they are already stored at the destination location"
  
   */
  @DefaultMessage("Some items cannot be moved as they are already stored at the destination location")
  @Key("itemsCantBeMoved")
  String itemsCantBeMoved();

  /**
   * Translated "Items Ordered".
   * 
   * @return translated "Items Ordered"
  
   */
  @DefaultMessage("Items Ordered")
  @Key("itemsOrdered")
  String itemsOrdered();

  /**
   * Translated "Items Shipped".
   * 
   * @return translated "Items Shipped"
  
   */
  @DefaultMessage("Items Shipped")
  @Key("itemsShipped")
  String itemsShipped();

  /**
   * Translated "Kit".
   * 
   * @return translated "Kit"
  
   */
  @DefaultMessage("Kit")
  @Key("kit")
  String kit();

  /**
   * Translated "All components of a kit must be from the same store as the kit".
   * 
   * @return translated "All components of a kit must be from the same store as the kit"
  
   */
  @DefaultMessage("All components of a kit must be from the same store as the kit")
  @Key("kitAndComponentSameStoreException")
  String kitAndComponentSameStoreException();

  /**
   * Translated "A kit must have at least one component".
   * 
   * @return translated "A kit must have at least one component"
  
   */
  @DefaultMessage("A kit must have at least one component")
  @Key("kitAtleastOneComponentException")
  String kitAtleastOneComponentException();

  /**
   * Translated "Kit Component Name".
   * 
   * @return translated "Kit Component Name"
  
   */
  @DefaultMessage("Kit Component Name")
  @Key("kitComponentName")
  String kitComponentName();

  /**
   * Translated "Ending Sent Date".
   * 
   * @return translated "Ending Sent Date"
  
   */
  @DefaultMessage("Ending Sent Date")
  @Key("kitTracking.endSentDate")
  String kitTracking_endSentDate();

  /**
   * Translated "Kit Tracking Report".
   * 
   * @return translated "Kit Tracking Report"
  
   */
  @DefaultMessage("Kit Tracking Report")
  @Key("kitTracking.kitTrackingReport")
  String kitTracking_kitTrackingReport();

  /**
   * Translated "Sort By".
   * 
   * @return translated "Sort By"
  
   */
  @DefaultMessage("Sort By")
  @Key("kitTracking.sortBy")
  String kitTracking_sortBy();

  /**
   * Translated "Starting Ordered Date".
   * 
   * @return translated "Starting Ordered Date"
  
   */
  @DefaultMessage("Starting Ordered Date")
  @Key("kitTracking.startOrderDate")
  String kitTracking_startOrderDate();

  /**
   * Translated "LCL".
   * 
   * @return translated "LCL"
  
   */
  @DefaultMessage("LCL")
  @Key("lCL")
  String lCL();

  /**
   * Translated "LWL".
   * 
   * @return translated "LWL"
  
   */
  @DefaultMessage("LWL")
  @Key("lWL")
  String lWL();

  /**
   * Translated "Laboratory Section".
   * 
   * @return translated "Laboratory Section"
  
   */
  @DefaultMessage("Laboratory Section")
  @Key("labSection")
  String labSection();

  /**
   * Translated "Define laboratory sections that perform related tasks.".
   * 
   * @return translated "Define laboratory sections that perform related tasks."
  
   */
  @DefaultMessage("Define laboratory sections that perform related tasks.")
  @Key("labSectionDescription")
  String labSectionDescription();

  /**
   * Translated "Label".
   * 
   * @return translated "Label"
  
   */
  @DefaultMessage("Label")
  @Key("label")
  String label();

  /**
   * Translated "Label cannot be deleted, it is being used by one or more tests".
   * 
   * @return translated "Label cannot be deleted, it is being used by one or more tests"
  
   */
  @DefaultMessage("Label cannot be deleted, it is being used by one or more tests")
  @Key("labelDeleteException")
  String labelDeleteException();

  /**
   * Translated "Define formats for additional analysis labels.".
   * 
   * @return translated "Define formats for additional analysis labels."
  
   */
  @DefaultMessage("Define formats for additional analysis labels.")
  @Key("labelDescription")
  String labelDescription();

  /**
   * Translated "Label For".
   * 
   * @return translated "Label For"
  
   */
  @DefaultMessage("Label For")
  @Key("labelFor")
  String labelFor();

  /**
   * Translated "Print additional labels for analyses and aliquots.".
   * 
   * @return translated "Print additional labels for analyses and aliquots."
  
   */
  @DefaultMessage("Print additional labels for analyses and aliquots.")
  @Key("labelForDescription")
  String labelForDescription();

  /**
   * Translated "History - Label".
   * 
   * @return translated "History - Label"
  
   */
  @DefaultMessage("History - Label")
  @Key("labelHistory")
  String labelHistory();

  /**
   * Translated "Labor".
   * 
   * @return translated "Labor"
  
   */
  @DefaultMessage("Labor")
  @Key("labor")
  String labor();

  /**
   * Translated "Last Name".
   * 
   * @return translated "Last Name"
  
   */
  @DefaultMessage("Last Name")
  @Key("lastName")
  String lastName();

  /**
   * Translated "You have reached the last page of your query results".
   * 
   * @return translated "You have reached the last page of your query results"
  
   */
  @DefaultMessage("You have reached the last page of your query results")
  @Key("lastPageException")
  String lastPageException();

  /**
   * Translated "Last Run".
   * 
   * @return translated "Last Run"
  
   */
  @DefaultMessage("Last Run")
  @Key("lastRun")
  String lastRun();

  /**
   * Translated "Done".
   * 
   * @return translated "Done"
  
   */
  @DefaultMessage("Done")
  @Key("loadCompleteMessage")
  String loadCompleteMessage();

  /**
   * Translated "Load From Date ".
   * 
   * @return translated "Load From Date "
  
   */
  @DefaultMessage("Load From Date ")
  @Key("loadFromDate")
  String loadFromDate();

  /**
   * Translated "Load From Excel File".
   * 
   * @return translated "Load From Excel File"
  
   */
  @DefaultMessage("Load From Excel File")
  @Key("loadFromEditFile")
  String loadFromEditFile();

  /**
   * Translated "Load From Instrument File".
   * 
   * @return translated "Load From Instrument File"
  
   */
  @DefaultMessage("Load From Instrument File")
  @Key("loadFromInstrumentFile")
  String loadFromInstrumentFile();

  /**
   * Translated "Load QC Template".
   * 
   * @return translated "Load QC Template"
  
   */
  @DefaultMessage("Load QC Template")
  @Key("loadTemplate")
  String loadTemplate();

  /**
   * Translated "Loading...".
   * 
   * @return translated "Loading..."
  
   */
  @DefaultMessage("Loading...")
  @Key("loadingMessage")
  String loadingMessage();

  /**
   * Translated "Local Term".
   * 
   * @return translated "Local Term"
  
   */
  @DefaultMessage("Local Term")
  @Key("localTerm")
  String localTerm();

  /**
   * Translated "History - Local Term".
   * 
   * @return translated "History - Local Term"
  
   */
  @DefaultMessage("History - Local Term")
  @Key("localTermHistory")
  String localTermHistory();

  /**
   * Translated "Location".
   * 
   * @return translated "Location"
  
   */
  @DefaultMessage("Location")
  @Key("location")
  String location();

  /**
   * Translated "Location Address".
   * 
   * @return translated "Location Address"
  
   */
  @DefaultMessage("Location Address")
  @Key("locationAddress")
  String locationAddress();

  /**
   * Translated "Location Apt/Suite #".
   * 
   * @return translated "Location Apt/Suite #"
  
   */
  @DefaultMessage("Location Apt/Suite #")
  @Key("locationAptSuite")
  String locationAptSuite();

  /**
   * Translated "Location City".
   * 
   * @return translated "Location City"
  
   */
  @DefaultMessage("Location City")
  @Key("locationCity")
  String locationCity();

  /**
   * Translated "Loc #".
   * 
   * @return translated "Loc #"
  
   */
  @DefaultMessage("Loc #")
  @Key("locationNum")
  String locationNum();

  /**
   * Translated "Location/Quantity".
   * 
   * @return translated "Location/Quantity"
  
   */
  @DefaultMessage("Location/Quantity")
  @Key("locationQuantity")
  String locationQuantity();

  /**
   * Translated "Location is required for this row".
   * 
   * @return translated "Location is required for this row"
  
   */
  @DefaultMessage("Location is required for this row")
  @Key("locationRequiredForRowException")
  String locationRequiredForRowException();

  /**
   * Translated "Location State".
   * 
   * @return translated "Location State"
  
   */
  @DefaultMessage("Location State")
  @Key("locationState")
  String locationState();

  /**
   * Translated "Location Zip Code".
   * 
   * @return translated "Location Zip Code"
  
   */
  @DefaultMessage("Location Zip Code")
  @Key("locationZipCode")
  String locationZipCode();

  /**
   * Translated "Locations".
   * 
   * @return translated "Locations"
  
   */
  @DefaultMessage("Locations")
  @Key("locations")
  String locations();

  /**
   * Translated "Locking record for Update...".
   * 
   * @return translated "Locking record for Update..."
  
   */
  @DefaultMessage("Locking record for Update...")
  @Key("lockForUpdate")
  String lockForUpdate();

  /**
   * Translated "Log".
   * 
   * @return translated "Log"
  
   */
  @DefaultMessage("Log")
  @Key("log")
  String log();

  /**
   * Translated "Logged In".
   * 
   * @return translated "Logged In"
  
   */
  @DefaultMessage("Logged In")
  @Key("loggedIn")
  String loggedIn();

  /**
   * Translated "Login".
   * 
   * @return translated "Login"
  
   */
  @DefaultMessage("Login")
  @Key("login")
  String login();

  /**
   * Translated "Additional Login Barcode Labels".
   * 
   * @return translated "Additional Login Barcode Labels"
  
   */
  @DefaultMessage("Additional Login Barcode Labels")
  @Key("loginAdditionalLabelReport")
  String loginAdditionalLabelReport();

  /**
   * Translated "Login Barcode Labels".
   * 
   * @return translated "Login Barcode Labels"
  
   */
  @DefaultMessage("Login Barcode Labels")
  @Key("loginBarcode")
  String loginBarcode();

  /**
   * Translated "Additional Login Barcode Labels".
   * 
   * @return translated "Additional Login Barcode Labels"
  
   */
  @DefaultMessage("Additional Login Barcode Labels")
  @Key("loginBarcodeAdd")
  String loginBarcodeAdd();

  /**
   * Translated "Login Label".
   * 
   * @return translated "Login Label"
  
   */
  @DefaultMessage("Login Label")
  @Key("loginLabel")
  String loginLabel();

  /**
   * Translated "Login Barcode Labels".
   * 
   * @return translated "Login Barcode Labels"
  
   */
  @DefaultMessage("Login Barcode Labels")
  @Key("loginLabelReport")
  String loginLabelReport();

  /**
   * Translated "Logout".
   * 
   * @return translated "Logout"
  
   */
  @DefaultMessage("Logout")
  @Key("logout")
  String logout();

  /**
   * Translated "Exit the application.".
   * 
   * @return translated "Exit the application."
  
   */
  @DefaultMessage("Exit the application.")
  @Key("logoutDescription")
  String logoutDescription();

  /**
   * Translated "Logs".
   * 
   * @return translated "Logs"
  
   */
  @DefaultMessage("Logs")
  @Key("logs")
  String logs();

  /**
   * Translated "Lookup Item".
   * 
   * @return translated "Lookup Item"
  
   */
  @DefaultMessage("Lookup Item")
  @Key("lookupItem")
  String lookupItem();

  /**
   * Translated "Results may be lost".
   * 
   * @return translated "Results may be lost"
  
   */
  @DefaultMessage("Results may be lost")
  @Key("loseResultsCaption")
  String loseResultsCaption();

  /**
   * Translated "Changing the test/method may cause you to lose the results you have entered.  Are you sure you want to continue?".
   * 
   * @return translated "Changing the test/method may cause you to lose the results you have entered.  Are you sure you want to continue?"
  
   */
  @DefaultMessage("Changing the test/method may cause you to lose the results you have entered.  Are you sure you want to continue?")
  @Key("loseResultsWarning")
  String loseResultsWarning();

  /**
   * Translated "Lot Information".
   * 
   * @return translated "Lot Information"
  
   */
  @DefaultMessage("Lot Information")
  @Key("lotInformation")
  String lotInformation();

  /**
   * Translated "Lot No.".
   * 
   * @return translated "Lot No."
  
   */
  @DefaultMessage("Lot No.")
  @Key("lotNum")
  String lotNum();

  /**
   * Translated "Lot # is required for item".
   * 
   * @return translated "Lot # is required for item"
  
   */
  @DefaultMessage("Lot # is required for item")
  @Key("lotNumRequiredForOrderItemException")
  String lotNumRequiredForOrderItemException();

  /**
   * Translated "Lot number is required for this row".
   * 
   * @return translated "Lot number is required for this row"
  
   */
  @DefaultMessage("Lot number is required for this row")
  @Key("lotNumRequiredForRowException")
  String lotNumRequiredForRowException();

  /**
   * Translated "Lot Number ".
   * 
   * @return translated "Lot Number "
  
   */
  @DefaultMessage("Lot Number ")
  @Key("lotNumber")
  String lotNumber();

  /**
   * Translated "Lot # Required".
   * 
   * @return translated "Lot # Required"
  
   */
  @DefaultMessage("Lot # Required")
  @Key("maintainLot")
  String maintainLot();

  /**
   * Translated "Maintenance".
   * 
   * @return translated "Maintenance"
  
   */
  @DefaultMessage("Maintenance")
  @Key("maintenance")
  String maintenance();

  /**
   * Translated "Manage Multiple Samples".
   * 
   * @return translated "Manage Multiple Samples"
  
   */
  @DefaultMessage("Manage Multiple Samples")
  @Key("manageMultipleSamples")
  String manageMultipleSamples();

  /**
   * Translated "Manufacturing".
   * 
   * @return translated "Manufacturing"
  
   */
  @DefaultMessage("Manufacturing")
  @Key("manufacturing")
  String manufacturing();

  /**
   * Translated "Max Order Level".
   * 
   * @return translated "Max Order Level"
  
   */
  @DefaultMessage("Max Order Level")
  @Key("maxOrderLevel")
  String maxOrderLevel();

  /**
   * Translated "Max".
   * 
   * @return translated "Max"
  
   */
  @DefaultMessage("Max")
  @Key("maximum")
  String maximum();

  /**
   * Translated "Mean".
   * 
   * @return translated "Mean"
  
   */
  @DefaultMessage("Mean")
  @Key("mean")
  String mean();

  /**
   * Translated "Med".
   * 
   * @return translated "Med"
  
   */
  @DefaultMessage("Med")
  @Key("median")
  String median();

  /**
   * Translated "State Hygienic Laboratory University of Iowa Test Results".
   * 
   * @return translated "State Hygienic Laboratory University of Iowa Test Results"
  
   */
  @DefaultMessage("State Hygienic Laboratory University of Iowa Test Results")
  @Key("messageOnTopOfSpreadsheet")
  String messageOnTopOfSpreadsheet();

  /**
   * Translated "Method".
   * 
   * @return translated "Method"
  
   */
  @DefaultMessage("Method")
  @Key("method")
  String method();

  /**
   * Translated "There is already an active method in the system with the same name".
   * 
   * @return translated "There is already an active method in the system with the same name"
  
   */
  @DefaultMessage("There is already an active method in the system with the same name")
  @Key("methodActiveException")
  String methodActiveException();

  /**
   * Translated "This method has been assigned to an active test, thus it can''''t be deactivated ".
   * 
   * @return translated "This method has been assigned to an active test, thus it can''''t be deactivated "
  
   */
  @DefaultMessage("This method has been assigned to an active test, thus it can''''t be deactivated ")
  @Key("methodAssignedToActiveTestException")
  String methodAssignedToActiveTestException();

  /**
   * Translated "Define methods by which tests are performed.".
   * 
   * @return translated "Define methods by which tests are performed."
  
   */
  @DefaultMessage("Define methods by which tests are performed.")
  @Key("methodDescription")
  String methodDescription();

  /**
   * Translated "History - Method".
   * 
   * @return translated "History - Method"
  
   */
  @DefaultMessage("History - Method")
  @Key("methodHistory")
  String methodHistory();

  /**
   * Translated "This method has an overlapping begin or end date with another method with the same name".
   * 
   * @return translated "This method has an overlapping begin or end date with another method with the same name"
  
   */
  @DefaultMessage("This method has an overlapping begin or end date with another method with the same name")
  @Key("methodTimeOverlapException")
  String methodTimeOverlapException();

  /**
   * Translated "Middle Name".
   * 
   * @return translated "Middle Name"
  
   */
  @DefaultMessage("Middle Name")
  @Key("middleName")
  String middleName();

  /**
   * Translated "Item {0} - At least one analysis is required".
   * 
   * @param arg0 "{0}"
   * @return translated "Item {0} - At least one analysis is required"
  
   */
  @DefaultMessage("Item {0} - At least one analysis is required")
  @Key("minOneAnalysisException")
  String minOneAnalysisException(String arg0);

  /**
   * Translated "At least one sample item is required".
   * 
   * @return translated "At least one sample item is required"
  
   */
  @DefaultMessage("At least one sample item is required")
  @Key("minOneSampleItemException")
  String minOneSampleItemException();

  /**
   * Translated "Min Order Level".
   * 
   * @return translated "Min Order Level"
  
   */
  @DefaultMessage("Min Order Level")
  @Key("minOrderLevel")
  String minOrderLevel();

  /**
   * Translated "Min".
   * 
   * @return translated "Min"
  
   */
  @DefaultMessage("Min")
  @Key("minimum")
  String minimum();

  /**
   * Translated "Missing file name or content type parameter; please report this error to your sysadmin".
   * 
   * @return translated "Missing file name or content type parameter; please report this error to your sysadmin"
  
   */
  @DefaultMessage("Missing file name or content type parameter; please report this error to your sysadmin")
  @Key("missingFileContentTypeException")
  String missingFileContentTypeException();

  /**
   * Translated "At least one location is missing in the items ordered tree".
   * 
   * @return translated "At least one location is missing in the items ordered tree"
  
   */
  @DefaultMessage("At least one location is missing in the items ordered tree")
  @Key("missingLocException")
  String missingLocException();

  /**
   * Translated "At least one quantity is missing in the items ordered tree".
   * 
   * @return translated "At least one quantity is missing in the items ordered tree"
  
   */
  @DefaultMessage("At least one quantity is missing in the items ordered tree")
  @Key("missingQuantityException")
  String missingQuantityException();

  /**
   * Translated "Model #".
   * 
   * @return translated "Model #"
  
   */
  @DefaultMessage("Model #")
  @Key("modelNumber")
  String modelNumber();

  /**
   * Translated "You do not have {0} permission in {1}".
   * 
   * @param arg0 "{0}"
   * @param arg1 "{1}"
   * @return translated "You do not have {0} permission in {1}"
  
   */
  @DefaultMessage("You do not have {0} permission in {1}")
  @Key("modulePermException")
  String modulePermException(String arg0,  String arg1);

  /**
   * Translated "Monitor".
   * 
   * @return translated "Monitor"
  
   */
  @DefaultMessage("Monitor")
  @Key("monitor")
  String monitor();

  /**
   * Translated "Month".
   * 
   * @return translated "Month"
  
   */
  @DefaultMessage("Month")
  @Key("month")
  String month();

  /**
   * Translated "At the most one log entry can have its end date unspecified.".
   * 
   * @return translated "At the most one log entry can have its end date unspecified."
  
   */
  @DefaultMessage("At the most one log entry can have its end date unspecified.")
  @Key("moreThanOneEndDateAbsentException")
  String moreThanOneEndDateAbsentException();

  /**
   * Translated "More than one prep tests cannot be marked as not optional".
   * 
   * @return translated "More than one prep tests cannot be marked as not optional"
  
   */
  @DefaultMessage("More than one prep tests cannot be marked as not optional")
  @Key("moreThanOnePrepTestOptionalException")
  String moreThanOnePrepTestOptionalException();

  /**
   * Translated "> 3 days ago".
   * 
   * @return translated "> 3 days ago"
  
   */
  @DefaultMessage("> 3 days ago")
  @Key("moreThanThreeDays")
  String moreThanThreeDays();

  /**
   * Translated "> 30 days ago".
   * 
   * @return translated "> 30 days ago"
  
   */
  @DefaultMessage("> 30 days ago")
  @Key("moreThanThrtyDays")
  String moreThanThrtyDays();

  /**
   * Translated "> 10 days ago".
   * 
   * @return translated "> 10 days ago"
  
   */
  @DefaultMessage("> 10 days ago")
  @Key("moreThenTenDays")
  String moreThenTenDays();

  /**
   * Translated "Most Recent QCs".
   * 
   * @return translated "Most Recent QCs"
  
   */
  @DefaultMessage("Most Recent QCs")
  @Key("mostRecentQc")
  String mostRecentQc();

  /**
   * Translated "Move".
   * 
   * @return translated "Move"
  
   */
  @DefaultMessage("Move")
  @Key("move")
  String move();

  /**
   * Translated "Move Down".
   * 
   * @return translated "Move Down"
  
   */
  @DefaultMessage("Move Down")
  @Key("moveDown")
  String moveDown();

  /**
   * Translated "<<".
   * 
   * @return translated "<<"
  
   */
  @DefaultMessage("<<")
  @Key("moveLeft")
  String moveLeft();

  /**
   * Translated "Move Up".
   * 
   * @return translated "Move Up"
  
   */
  @DefaultMessage("Move Up")
  @Key("moveUp")
  String moveUp();

  /**
   * Translated "More than one active QC Lot was found matching ''{0}'' at row {1}".
   * 
   * @param arg0 "{0}"
   * @param arg1 "{1}"
   * @return translated "More than one active QC Lot was found matching ''{0}'' at row {1}"
  
   */
  @DefaultMessage("More than one active QC Lot was found matching ''{0}'' at row {1}")
  @Key("multiMatchingActiveQc")
  String multiMatchingActiveQc(String arg0,  String arg1);

  /**
   * Translated "Multiple rows selected. Currently edited column: ".
   * 
   * @return translated "Multiple rows selected. Currently edited column: "
  
   */
  @DefaultMessage("Multiple rows selected. Currently edited column: ")
  @Key("multiSelRowEditCol")
  String multiSelRowEditCol();

  /**
   * Translated "Only one record with the type Bill To is allowed".
   * 
   * @return translated "Only one record with the type Bill To is allowed"
  
   */
  @DefaultMessage("Only one record with the type Bill To is allowed")
  @Key("multipleBillToException")
  String multipleBillToException();

  /**
   * Translated "A sample can have only one new internal note".
   * 
   * @return translated "A sample can have only one new internal note"
  
   */
  @DefaultMessage("A sample can have only one new internal note")
  @Key("multipleInternalNoteException")
  String multipleInternalNoteException();

  /**
   * Translated "Only one record with the type Report To is allowed ".
   * 
   * @return translated "Only one record with the type Report To is allowed "
  
   */
  @DefaultMessage("Only one record with the type Report To is allowed ")
  @Key("multipleReportToException")
  String multipleReportToException();

  /**
   * Translated "The selected analyses are from multiple tests, only the first QC template will be loaded.".
   * 
   * @return translated "The selected analyses are from multiple tests, only the first QC template will be loaded."
  
   */
  @DefaultMessage("The selected analyses are from multiple tests, only the first QC template will be loaded.")
  @Key("multipleTestsOnWorksheet")
  String multipleTestsOnWorksheet();

  /**
   * Translated "You must Commit or Abort changes first".
   * 
   * @return translated "You must Commit or Abort changes first"
  
   */
  @DefaultMessage("You must Commit or Abort changes first")
  @Key("mustCommitOrAbort")
  String mustCommitOrAbort();

  /**
   * Translated "The sample must have at least one analysis assigned.".
   * 
   * @return translated "The sample must have at least one analysis assigned."
  
   */
  @DefaultMessage("The sample must have at least one analysis assigned.")
  @Key("mustHaveAnalysesToVerify")
  String mustHaveAnalysesToVerify();

  /**
   * Translated "The default printer and default barcode printer must be specified to process orders  ".
   * 
   * @return translated "The default printer and default barcode printer must be specified to process orders  "
  
   */
  @DefaultMessage("The default printer and default barcode printer must be specified to process orders  ")
  @Key("mustSpecifyDefPrinters")
  String mustSpecifyDefPrinters();

  /**
   * Translated "Name".
   * 
   * @return translated "Name"
  
   */
  @DefaultMessage("Name")
  @Key("name")
  String name();

  /**
   * Translated "Name/Date & Time".
   * 
   * @return translated "Name/Date & Time"
  
   */
  @DefaultMessage("Name/Date & Time")
  @Key("nameDateAndTime")
  String nameDateAndTime();

  /**
   * Translated "Name List".
   * 
   * @return translated "Name List"
  
   */
  @DefaultMessage("Name List")
  @Key("nameList")
  String nameList();

  /**
   * Translated "Name & Method ".
   * 
   * @return translated "Name & Method "
  
   */
  @DefaultMessage("Name & Method ")
  @Key("nameMethod")
  String nameMethod();

  /**
   * Translated "Needed In Days".
   * 
   * @return translated "Needed In Days"
  
   */
  @DefaultMessage("Needed In Days")
  @Key("neededDays")
  String neededDays();

  /**
   * Translated "# Days".
   * 
   * @return translated "# Days"
  
   */
  @DefaultMessage("# Days")
  @Key("neededNumDays")
  String neededNumDays();

  /**
   * Translated "Neonatal Screening Sample Login".
   * 
   * @return translated "Neonatal Screening Sample Login"
  
   */
  @DefaultMessage("Neonatal Screening Sample Login")
  @Key("neonatalScreeningSampleLogin")
  String neonatalScreeningSampleLogin();

  /**
   * Translated "Fully login neonatal sample and analysis related information.".
   * 
   * @return translated "Fully login neonatal sample and analysis related information."
  
   */
  @DefaultMessage("Fully login neonatal sample and analysis related information.")
  @Key("neonatalScreeningSampleLoginDescription")
  String neonatalScreeningSampleLoginDescription();

  /**
   * Translated "There was a problem creating a new accession number, please try again".
   * 
   * @return translated "There was a problem creating a new accession number, please try again"
  
   */
  @DefaultMessage("There was a problem creating a new accession number, please try again")
  @Key("newAccessionNumError")
  String newAccessionNumError();

  /**
   * Translated "New Note".
   * 
   * @return translated "New Note"
  
   */
  @DefaultMessage("New Note")
  @Key("newNote")
  String newNote();

  /**
   * Translated "Newborn Screening Sample".
   * 
   * @return translated "Newborn Screening Sample"
  
   */
  @DefaultMessage("Newborn Screening Sample")
  @Key("newbornScreeningSample")
  String newbornScreeningSample();

  /**
   * Translated "Next".
   * 
   * @return translated "Next"
  
   */
  @DefaultMessage("Next")
  @Key("next")
  String next();

  /**
   * Translated "No".
   * 
   * @return translated "No"
  
   */
  @DefaultMessage("No")
  @Key("no")
  String no();

  /**
   * Translated "There is no active auxiliary group in the system with this name".
   * 
   * @return translated "There is no active auxiliary group in the system with this name"
  
   */
  @DefaultMessage("There is no active auxiliary group in the system with this name")
  @Key("noActiveAuxGrpException")
  String noActiveAuxGrpException();

  /**
   * Translated "There were no active tests found for the panel".
   * 
   * @return translated "There were no active tests found for the panel"
  
   */
  @DefaultMessage("There were no active tests found for the panel")
  @Key("noActiveTestFoundForPanelException")
  String noActiveTestFoundForPanelException();

  /**
   * Translated "There are no active tests in the system with this name and method".
   * 
   * @return translated "There are no active tests in the system with this name and method"
  
   */
  @DefaultMessage("There are no active tests in the system with this name and method")
  @Key("noActiveTestsException")
  String noActiveTestsException();

  /**
   * Translated "No Analytes Found For Selected Row".
   * 
   * @return translated "No Analytes Found For Selected Row"
  
   */
  @DefaultMessage("No Analytes Found For Selected Row")
  @Key("noAnalytesFoundForRow")
  String noAnalytesFoundForRow();

  /**
   * Translated "You do not have assign permission for this test".
   * 
   * @return translated "You do not have assign permission for this test"
  
   */
  @DefaultMessage("You do not have assign permission for this test")
  @Key("noAssignTestPermission")
  String noAssignTestPermission();

  /**
   * Translated "Cannot cancel prep analysis ''{0}, {1}'' with released analytical analysis ''{2}, {3}''".
   * 
   * @param arg0 "{0}"
   * @param arg1 "{1}"
   * @param arg2 "{2}"
   * @param arg3 "{3}"
   * @return translated "Cannot cancel prep analysis ''{0}, {1}'' with released analytical analysis ''{2}, {3}''"
  
   */
  @DefaultMessage("Cannot cancel prep analysis ''{0}, {1}'' with released analytical analysis ''{2}, {3}''")
  @Key("noCancelPrepWithReleasedTest")
  String noCancelPrepWithReleasedTest(String arg0,  String arg1,  String arg2,  String arg3);

  /**
   * Translated "You do not have complete permission for this test".
   * 
   * @return translated "You do not have complete permission for this test"
  
   */
  @DefaultMessage("You do not have complete permission for this test")
  @Key("noCompleteTestPermission")
  String noCompleteTestPermission();

  /**
   * Translated "No container is present for Item # {0} which is assigned to ''{1}'' ".
   * 
   * @param arg0 "{0}"
   * @param arg1 "{1}"
   * @return translated "No container is present for Item # {0} which is assigned to ''{1}'' "
  
   */
  @DefaultMessage("No container is present for Item # {0} which is assigned to ''{1}'' ")
  @Key("noContainerWithItemNumWarning")
  String noContainerWithItemNumWarning(String arg0,  String arg1);

  /**
   * Translated "Missing or undefined sample domain ".
   * 
   * @return translated "Missing or undefined sample domain "
  
   */
  @DefaultMessage("Missing or undefined sample domain ")
  @Key("noDomainException")
  String noDomainException();

  /**
   * Translated "There are no items to process  ".
   * 
   * @return translated "There are no items to process  "
  
   */
  @DefaultMessage("There are no items to process  ")
  @Key("noItemsToProcess")
  String noItemsToProcess();

  /**
   * Translated "Please select a location for this row".
   * 
   * @return translated "Please select a location for this row"
  
   */
  @DefaultMessage("Please select a location for this row")
  @Key("noLocationSelectedForRowException")
  String noLocationSelectedForRowException();

  /**
   * Translated "No active QC matching ''{0}'' at position {1}".
   * 
   * @param arg0 "{0}"
   * @param arg1 "{1}"
   * @return translated "No active QC matching ''{0}'' at position {1}"
  
   */
  @DefaultMessage("No active QC matching ''{0}'' at position {1}")
  @Key("noMatchingActiveQc")
  String noMatchingActiveQc(String arg0,  String arg1);

  /**
   * Translated "No more records in this direction".
   * 
   * @return translated "No more records in this direction"
  
   */
  @DefaultMessage("No more records in this direction")
  @Key("noMoreRecordInDir")
  String noMoreRecordInDir();

  /**
   * Translated "Released or cancelled analyses cannot be moved".
   * 
   * @return translated "Released or cancelled analyses cannot be moved"
  
   */
  @DefaultMessage("Released or cancelled analyses cannot be moved")
  @Key("noMoveReleasedCancelledAnalyses")
  String noMoveReleasedCancelledAnalyses();

  /**
   * Translated "Please select one or more orders for processing".
   * 
   * @return translated "Please select one or more orders for processing"
  
   */
  @DefaultMessage("Please select one or more orders for processing")
  @Key("noOrdersSelectForProcess")
  String noOrdersSelectForProcess();

  /**
   * Translated "You don''t have the permission to add emails for any organization. Please contact the system administrator".
   * 
   * @return translated "You don''t have the permission to add emails for any organization. Please contact the system administrator"
  
   */
  @DefaultMessage("You don''t have the permission to add emails for any organization. Please contact the system administrator")
  @Key("noPermToAddEmailException")
  String noPermToAddEmailException();

  /**
   * Translated "No records found".
   * 
   * @return translated "No records found"
  
   */
  @DefaultMessage("No records found")
  @Key("noRecordsFound")
  String noRecordsFound();

  /**
   * Translated "Please select atleast one data point, then press Plot Data".
   * 
   * @return translated "Please select atleast one data point, then press Plot Data"
  
   */
  @DefaultMessage("Please select atleast one data point, then press Plot Data")
  @Key("noSampleSelectedError")
  String noSampleSelectedError();

  /**
   * Translated "No sample type specified for the container with Item # {0}  ".
   * 
   * @param arg0 "{0}"
   * @return translated "No sample type specified for the container with Item # {0}  "
  
   */
  @DefaultMessage("No sample type specified for the container with Item # {0}  ")
  @Key("noSampleTypeForContainerWarning")
  String noSampleTypeForContainerWarning(String arg0);

  /**
   * Translated "No samples have been found. Please change your search criteria and try again.".
   * 
   * @return translated "No samples have been found. Please change your search criteria and try again."
  
   */
  @DefaultMessage("No samples have been found. Please change your search criteria and try again.")
  @Key("noSamplesFoundChangeSearch")
  String noSamplesFoundChangeSearch();

  /**
   * Translated "Test has no sections assigned to it".
   * 
   * @return translated "Test has no sections assigned to it"
  
   */
  @DefaultMessage("Test has no sections assigned to it")
  @Key("noSectionsForTest")
  String noSectionsForTest();

  /**
   * Translated "You need to ship at least 1 item.".
   * 
   * @return translated "You need to ship at least 1 item."
  
   */
  @DefaultMessage("You need to ship at least 1 item.")
  @Key("noShippingItemsException")
  String noShippingItemsException();

  /**
   * Translated "A panel must have at least one test or auxiliary group added to it  ".
   * 
   * @return translated "A panel must have at least one test or auxiliary group added to it  "
  
   */
  @DefaultMessage("A panel must have at least one test or auxiliary group added to it  ")
  @Key("noTestAssignedToPanelException")
  String noTestAssignedToPanelException();

  /**
   * Translated "Please specify at least one test in the query since all analyses are not to be included".
   * 
   * @return translated "Please specify at least one test in the query since all analyses are not to be included"
  
   */
  @DefaultMessage("Please specify at least one test in the query since all analyses are not to be included")
  @Key("noTestForNotIncludeAllAnalysesException")
  String noTestForNotIncludeAllAnalysesException();

  /**
   * Translated "# to Retrieve".
   * 
   * @return translated "# to Retrieve"
  
   */
  @DefaultMessage("# to Retrieve")
  @Key("noToRetrieve")
  String noToRetrieve();

  /**
   * Translated "Please enter atleast one query field and then click ''Find Samples''".
   * 
   * @return translated "Please enter atleast one query field and then click ''Find Samples''"
  
   */
  @DefaultMessage("Please enter atleast one query field and then click ''Find Samples''")
  @Key("nofieldSelectedError")
  String nofieldSelectedError();

  /**
   * Translated "Not all dates generated with this frequency are valid".
   * 
   * @return translated "Not all dates generated with this frequency are valid"
  
   */
  @DefaultMessage("Not all dates generated with this frequency are valid")
  @Key("notAllDatesValid")
  String notAllDatesValid();

  /**
   * Translated "Not enough quantity on hand at this location, please adjust".
   * 
   * @return translated "Not enough quantity on hand at this location, please adjust"
  
   */
  @DefaultMessage("Not enough quantity on hand at this location, please adjust")
  @Key("notEnoughQuantityOnHand")
  String notEnoughQuantityOnHand();

  /**
   * Translated "Not For Sale".
   * 
   * @return translated "Not For Sale"
  
   */
  @DefaultMessage("Not For Sale")
  @Key("notForSale")
  String notForSale();

  /**
   * Translated "Cannot lookup the sample requested because the screen is busy".
   * 
   * @return translated "Cannot lookup the sample requested because the screen is busy"
  
   */
  @DefaultMessage("Cannot lookup the sample requested because the screen is busy")
  @Key("notProperState")
  String notProperState();

  /**
   * Translated "Not sufficient quantity at location".
   * 
   * @return translated "Not sufficient quantity at location"
  
   */
  @DefaultMessage("Not sufficient quantity at location")
  @Key("notSuffcientQtyAtLocException")
  String notSuffcientQtyAtLocException();

  /**
   * Translated "Note".
   * 
   * @return translated "Note"
  
   */
  @DefaultMessage("Note")
  @Key("note")
  String note();

  /**
   * Translated "Internal note subject/text cannot be empty".
   * 
   * @return translated "Internal note subject/text cannot be empty"
  
   */
  @DefaultMessage("Internal note subject/text cannot be empty")
  @Key("note.internalEmptyException")
  String note_internalEmptyException();

  /**
   * Translated "Notes".
   * 
   * @return translated "Notes"
  
   */
  @DefaultMessage("Notes")
  @Key("note.notes")
  String note_notes();

  /**
   * Translated "Note Editor".
   * 
   * @return translated "Note Editor"
  
   */
  @DefaultMessage("Note Editor")
  @Key("noteEditor")
  String noteEditor();

  /**
   * Translated "Notes".
   * 
   * @return translated "Notes"
  
   */
  @DefaultMessage("Notes")
  @Key("notes")
  String notes();

  /**
   * Translated "Email Notification".
   * 
   * @return translated "Email Notification"
  
   */
  @DefaultMessage("Email Notification")
  @Key("notificationPreference")
  String notificationPreference();

  /**
   * Translated "NPI".
   * 
   * @return translated "NPI"
  
   */
  @DefaultMessage("NPI")
  @Key("npi")
  String npi();

  /**
   * Translated "No. of Analyses".
   * 
   * @return translated "No. of Analyses"
  
   */
  @DefaultMessage("No. of Analyses")
  @Key("numAnalyses")
  String numAnalyses();

  /**
   * Translated "Days Left".
   * 
   * @return translated "Days Left"
  
   */
  @DefaultMessage("Days Left")
  @Key("numDaysLeft")
  String numDaysLeft();

  /**
   * Translated "# Forms".
   * 
   * @return translated "# Forms"
  
   */
  @DefaultMessage("# Forms")
  @Key("numForms")
  String numForms();

  /**
   * Translated "# instances".
   * 
   * @return translated "# instances"
  
   */
  @DefaultMessage("# instances")
  @Key("numInstances")
  String numInstances();

  /**
   * Translated "# of Packages".
   * 
   * @return translated "# of Packages"
  
   */
  @DefaultMessage("# of Packages")
  @Key("numPackages")
  String numPackages();

  /**
   * Translated "# Rec".
   * 
   * @return translated "# Rec"
  
   */
  @DefaultMessage("# Rec")
  @Key("numRec")
  String numRec();

  /**
   * Translated "# Rec cannot be less than zero ".
   * 
   * @return translated "# Rec cannot be less than zero "
  
   */
  @DefaultMessage("# Rec cannot be less than zero ")
  @Key("numRecNotLessThanZeroException")
  String numRecNotLessThanZeroException();

  /**
   * Translated "# Rec is required for received items ".
   * 
   * @return translated "# Rec is required for received items "
  
   */
  @DefaultMessage("# Rec is required for received items ")
  @Key("numRecReqForReceivedItemsException")
  String numRecReqForReceivedItemsException();

  /**
   * Translated "# Req".
   * 
   * @return translated "# Req"
  
   */
  @DefaultMessage("# Req")
  @Key("numReq")
  String numReq();

  /**
   * Translated "# Req cannot be less than # Rec ".
   * 
   * @return translated "# Req cannot be less than # Rec "
  
   */
  @DefaultMessage("# Req cannot be less than # Rec ")
  @Key("numReqLessThanNumRecException")
  String numReqLessThanNumRecException();

  /**
   * Translated "# Requested".
   * 
   * @return translated "# Requested"
  
   */
  @DefaultMessage("# Requested")
  @Key("numRequested")
  String numRequested();

  /**
   * Translated "Transfer inventory in or lower the number of kits requested".
   * 
   * @return translated "Transfer inventory in or lower the number of kits requested"
  
   */
  @DefaultMessage("Transfer inventory in or lower the number of kits requested")
  @Key("numRequestedIsToHigh")
  String numRequestedIsToHigh();

  /**
   * Translated "# Requested must be more than zero".
   * 
   * @return translated "# Requested must be more than zero"
  
   */
  @DefaultMessage("# Requested must be more than zero")
  @Key("numRequestedMoreThanZeroException")
  String numRequestedMoreThanZeroException();

  /**
   * Translated "# Sample".
   * 
   * @return translated "# Sample"
  
   */
  @DefaultMessage("# Sample")
  @Key("numSample")
  String numSample();

  /**
   * Translated "No. of Samples".
   * 
   * @return translated "No. of Samples"
  
   */
  @DefaultMessage("No. of Samples")
  @Key("numSamples")
  String numSamples();

  /**
   * Translated "samples have been found.".
   * 
   * @return translated "samples have been found."
  
   */
  @DefaultMessage("samples have been found.")
  @Key("numSamplesFound")
  String numSamplesFound();

  /**
   * Translated "# Tested".
   * 
   * @return translated "# Tested"
  
   */
  @DefaultMessage("# Tested")
  @Key("numTested")
  String numTested();

  /**
   * Translated "# To Make".
   * 
   * @return translated "# To Make"
  
   */
  @DefaultMessage("# To Make")
  @Key("numToMake")
  String numToMake();

  /**
   * Translated "Format".
   * 
   * @return translated "Format"
  
   */
  @DefaultMessage("Format")
  @Key("numberFormat")
  String numberFormat();

  /**
   * Translated "OK".
   * 
   * @return translated "OK"
  
   */
  @DefaultMessage("OK")
  @Key("ok")
  String ok();

  /**
   * Translated "There are still records in the table. Commit these records?".
   * 
   * @return translated "There are still records in the table. Commit these records?"
  
   */
  @DefaultMessage("There are still records in the table. Commit these records?")
  @Key("onCloseConfirmBody")
  String onCloseConfirmBody();

  /**
   * Translated "Uncommited Records".
   * 
   * @return translated "Uncommited Records"
  
   */
  @DefaultMessage("Uncommited Records")
  @Key("onCloseConfirmTitle")
  String onCloseConfirmTitle();

  /**
   * Translated "On Hand".
   * 
   * @return translated "On Hand"
  
   */
  @DefaultMessage("On Hand")
  @Key("onHand")
  String onHand();

  /**
   * Translated "This analysis has a status of ''On Hold,'' press Ok to complete or Cancel to abort.".
   * 
   * @return translated "This analysis has a status of ''On Hold,'' press Ok to complete or Cancel to abort."
  
   */
  @DefaultMessage("This analysis has a status of ''On Hold,'' press Ok to complete or Cancel to abort.")
  @Key("onHoldWarning")
  String onHoldWarning();

  /**
   * Translated "One or More items are linked to this row. Please unlink them before removing this row.".
   * 
   * @return translated "One or More items are linked to this row. Please unlink them before removing this row."
  
   */
  @DefaultMessage("One or More items are linked to this row. Please unlink them before removing this row.")
  @Key("oneOrMoreQcLinkOnRemove")
  String oneOrMoreQcLinkOnRemove();

  /**
   * Translated "{0} : {1} - One or more result values invalid".
   * 
   * @param arg0 "{0}"
   * @param arg1 "{1}"
   * @return translated "{0} : {1} - One or more result values invalid"
  
   */
  @DefaultMessage("{0} : {1} - One or more result values invalid")
  @Key("oneOrMoreResultValuesInvalid")
  String oneOrMoreResultValuesInvalid(String arg0,  String arg1);

  /**
   * Translated "Only one row can be selected for removal".
   * 
   * @return translated "Only one row can be selected for removal"
  
   */
  @DefaultMessage("Only one row can be selected for removal")
  @Key("oneRowsSelForRemoval")
  String oneRowsSelForRemoval();

  /**
   * Translated "Please commit your worksheet before attempting another load".
   * 
   * @return translated "Please commit your worksheet before attempting another load"
  
   */
  @DefaultMessage("Please commit your worksheet before attempting another load")
  @Key("oneWorksheetLoadPerCommit")
  String oneWorksheetLoadPerCommit();

  /**
   * Translated "There can be only one active external term per profile".
   * 
   * @return translated "There can be only one active external term per profile"
  
   */
  @DefaultMessage("There can be only one active external term per profile")
  @Key("onlyOneActiveExtTermPerProfileException")
  String onlyOneActiveExtTermPerProfileException();

  /**
   * Translated "Only one type must be selected for querying".
   * 
   * @return translated "Only one type must be selected for querying"
  
   */
  @DefaultMessage("Only one type must be selected for querying")
  @Key("onlyOneTypeSelectionForQueryException")
  String onlyOneTypeSelectionForQueryException();

  /**
   * Translated "Only pending or back-ordered orders can be selected to be processed".
   * 
   * @return translated "Only pending or back-ordered orders can be selected to be processed"
  
   */
  @DefaultMessage("Only pending or back-ordered orders can be selected to be processed")
  @Key("onlyPendingBackOrderedProcessed")
  String onlyPendingBackOrderedProcessed();

  /**
   * Translated "Only orders with no items can be processed through this option ".
   * 
   * @return translated "Only orders with no items can be processed through this option "
  
   */
  @DefaultMessage("Only orders with no items can be processed through this option ")
  @Key("onlyProcessOrdersWithNoItems")
  String onlyProcessOrdersWithNoItems();

  /**
   * Translated "Only pending orders can be processed through this option ".
   * 
   * @return translated "Only pending orders can be processed through this option "
  
   */
  @DefaultMessage("Only pending orders can be processed through this option ")
  @Key("onlyProcessPendingOrders")
  String onlyProcessPendingOrders();

  /**
   * Translated "Open Query".
   * 
   * @return translated "Open Query"
  
   */
  @DefaultMessage("Open Query")
  @Key("openQuery")
  String openQuery();

  /**
   * Translated "Operation".
   * 
   * @return translated "Operation"
  
   */
  @DefaultMessage("Operation")
  @Key("operation")
  String operation();

  /**
   * Translated "Lower case text".
   * 
   * @return translated "Lower case text"
  
   */
  @DefaultMessage("Lower case text")
  @Key("optionALPHA_LOWER")
  String optionALPHA_LOWER();

  /**
   * Translated "Mixed case text".
   * 
   * @return translated "Mixed case text"
  
   */
  @DefaultMessage("Mixed case text")
  @Key("optionALPHA_MIXED")
  String optionALPHA_MIXED();

  /**
   * Translated "Upper case text".
   * 
   * @return translated "Upper case text"
  
   */
  @DefaultMessage("Upper case text")
  @Key("optionALPHA_UPPER")
  String optionALPHA_UPPER();

  /**
   * Translated "Date in ''yyyy-mm-dd'' format".
   * 
   * @return translated "Date in ''yyyy-mm-dd'' format"
  
   */
  @DefaultMessage("Date in ''yyyy-mm-dd'' format")
  @Key("optionDATE")
  String optionDATE();

  /**
   * Translated "Date and time in ''yyyy-mm-dd hh:mm'' format".
   * 
   * @return translated "Date and time in ''yyyy-mm-dd hh:mm'' format"
  
   */
  @DefaultMessage("Date and time in ''yyyy-mm-dd hh:mm'' format")
  @Key("optionDATE_TIME")
  String optionDATE_TIME();

  /**
   * Translated "Dictionary entry '{0}' ".
   * 
   * @return translated "Dictionary entry '{0}' "
  
   */
  @DefaultMessage("Dictionary entry '{0}' ")
  @Key("optionDICTIONARY")
  String optionDICTIONARY();

  /**
   * Translated "Numeric Range in between '{0}'".
   * 
   * @return translated "Numeric Range in between '{0}'"
  
   */
  @DefaultMessage("Numeric Range in between '{0}'")
  @Key("optionNUMERIC")
  String optionNUMERIC();

  /**
   * Translated "Time in ''hh:mm'' format".
   * 
   * @return translated "Time in ''hh:mm'' format"
  
   */
  @DefaultMessage("Time in ''hh:mm'' format")
  @Key("optionTIME")
  String optionTIME();

  /**
   * Translated "Titer range in between '{0}'".
   * 
   * @return translated "Titer range in between '{0}'"
  
   */
  @DefaultMessage("Titer range in between '{0}'")
  @Key("optionTITER")
  String optionTITER();

  /**
   * Translated "Optional".
   * 
   * @return translated "Optional"
  
   */
  @DefaultMessage("Optional")
  @Key("optional")
  String optional();

  /**
   * Translated "Options".
   * 
   * @return translated "Options"
  
   */
  @DefaultMessage("Options")
  @Key("options")
  String options();

  /**
   * Translated "OR".
   * 
   * @return translated "OR"
  
   */
  @DefaultMessage("OR")
  @Key("or")
  String or();

  /**
   * Translated "Ord #".
   * 
   * @return translated "Ord #"
  
   */
  @DefaultMessage("Ord #")
  @Key("ordNum")
  String ordNum();

  /**
   * Translated "Order".
   * 
   * @return translated "Order"
  
   */
  @DefaultMessage("Order")
  @Key("order")
  String order();

  /**
   * Translated "All of your changes will be lost if you abort. Are you sure?".
   * 
   * @return translated "All of your changes will be lost if you abort. Are you sure?"
  
   */
  @DefaultMessage("All of your changes will be lost if you abort. Are you sure?")
  @Key("order.abortWarning")
  String order_abortWarning();

  /**
   * Translated "Attention".
   * 
   * @return translated "Attention"
  
   */
  @DefaultMessage("Attention")
  @Key("order.attention")
  String order_attention();

  /**
   * Translated "by".
   * 
   * @return translated "by"
  
   */
  @DefaultMessage("by")
  @Key("order.by")
  String order_by();

  /**
   * Translated "Cancelled orders cannot be updated".
   * 
   * @return translated "Cancelled orders cannot be updated"
  
   */
  @DefaultMessage("Cancelled orders cannot be updated")
  @Key("order.cancelledOrderCantBeUpdated")
  String order_cancelledOrderCantBeUpdated();

  /**
   * Translated "Catalog #".
   * 
   * @return translated "Catalog #"
  
   */
  @DefaultMessage("Catalog #")
  @Key("order.catalogNum")
  String order_catalogNum();

  /**
   * Translated "Order # {0,number,#0}: Container Item Sequence is required".
   * 
   * @param arg0 "{0,number,#0}"
   * @return translated "Order # {0,number,#0}: Container Item Sequence is required"
  
   */
  @DefaultMessage("Order # {0,number,#0}: Container Item Sequence is required")
  @Key("order.containerItemSequenceRequiredException")
  String order_containerItemSequenceRequiredException(Integer arg0);

  /**
   * Translated "Order # {0,number,#0}: Container is required".
   * 
   * @param arg0 "{0,number,#0}"
   * @return translated "Order # {0,number,#0}: Container is required"
  
   */
  @DefaultMessage("Order # {0,number,#0}: Container is required")
  @Key("order.containerRequiredException")
  String order_containerRequiredException(Integer arg0);

  /**
   * Translated "Cost Center".
   * 
   * @return translated "Cost Center"
  
   */
  @DefaultMessage("Cost Center")
  @Key("order.costCenter")
  String order_costCenter();

  /**
   * Translated "Order # {0,number,#0}: Cost Center is required".
   * 
   * @param arg0 "{0,number,#0}"
   * @return translated "Order # {0,number,#0}: Cost Center is required"
  
   */
  @DefaultMessage("Order # {0,number,#0}: Cost Center is required")
  @Key("order.costCenterRequiredException")
  String order_costCenterRequiredException(Integer arg0);

  /**
   * Translated "Customer".
   * 
   * @return translated "Customer"
  
   */
  @DefaultMessage("Customer")
  @Key("order.customer")
  String order_customer();

  /**
   * Translated "Valid Begin and End dates, Frequency and Unit must be specified to show dates".
   * 
   * @return translated "Valid Begin and End dates, Frequency and Unit must be specified to show dates"
  
   */
  @DefaultMessage("Valid Begin and End dates, Frequency and Unit must be specified to show dates")
  @Key("order.datesFreqUnitNotSpec")
  String order_datesFreqUnitNotSpec();

  /**
   * Translated "Order # {0,number,#0}: A vendor order must not have the inventory item \"{1}\" more than once".
   * 
   * @param arg0 "{0,number,#0}"
   * @param arg1 "{1}"
   * @return translated "Order # {0,number,#0}: A vendor order must not have the inventory item \"{1}\" more than once"
  
   */
  @DefaultMessage("Order # {0,number,#0}: A vendor order must not have the inventory item \"{1}\" more than once")
  @Key("order.duplicateInvItemVendorOrderException")
  String order_duplicateInvItemVendorOrderException(Integer arg0,  String arg1);

  /**
   * Translated "Order # {0,number,#0}: End date must not be before begin date".
   * 
   * @param arg0 "{0,number,#0}"
   * @return translated "Order # {0,number,#0}: End date must not be before begin date"
  
   */
  @DefaultMessage("Order # {0,number,#0}: End date must not be before begin date")
  @Key("order.endDateAfterBeginDateException")
  String order_endDateAfterBeginDateException(Integer arg0);

  /**
   * Translated "Ext Order #".
   * 
   * @return translated "Ext Order #"
  
   */
  @DefaultMessage("Ext Order #")
  @Key("order.extOrderNum")
  String order_extOrderNum();

  /**
   * Translated "Filled".
   * 
   * @return translated "Filled"
  
   */
  @DefaultMessage("Filled")
  @Key("order.filled")
  String order_filled();

  /**
   * Translated "Order # {0,number,#0}: Frequency must be greater than zero".
   * 
   * @param arg0 "{0,number,#0}"
   * @return translated "Order # {0,number,#0}: Frequency must be greater than zero"
  
   */
  @DefaultMessage("Order # {0,number,#0}: Frequency must be greater than zero")
  @Key("order.freqInvalidException")
  String order_freqInvalidException(Integer arg0);

  /**
   * Translated "The auxiliary group {0} is inactive and was not duplicated".
   * 
   * @param arg0 "{0}"
   * @return translated "The auxiliary group {0} is inactive and was not duplicated"
  
   */
  @DefaultMessage("The auxiliary group {0} is inactive and was not duplicated")
  @Key("order.inactiveAuxGroupWarning")
  String order_inactiveAuxGroupWarning(String arg0);

  /**
   * Translated "The container {0} is inactive and should be changed".
   * 
   * @param arg0 "{0}"
   * @return translated "The container {0} is inactive and should be changed"
  
   */
  @DefaultMessage("The container {0} is inactive and should be changed")
  @Key("order.inactiveContainerWarning")
  String order_inactiveContainerWarning(String arg0);

  /**
   * Translated "The inventory item {0} is inactive and was not duplicated".
   * 
   * @param arg0 "{0}"
   * @return translated "The inventory item {0} is inactive and was not duplicated"
  
   */
  @DefaultMessage("The inventory item {0} is inactive and was not duplicated")
  @Key("order.inactiveItemWarning")
  String order_inactiveItemWarning(String arg0);

  /**
   * Translated "The organization {0} is inactive and was not duplicated".
   * 
   * @param arg0 "{0}"
   * @return translated "The organization {0} is inactive and was not duplicated"
  
   */
  @DefaultMessage("The organization {0} is inactive and was not duplicated")
  @Key("order.inactiveOrganizationWarning")
  String order_inactiveOrganizationWarning(String arg0);

  /**
   * Translated "The sample type {0} is inactive and was not duplicated".
   * 
   * @param arg0 "{0}"
   * @return translated "The sample type {0} is inactive and was not duplicated"
  
   */
  @DefaultMessage("The sample type {0} is inactive and was not duplicated")
  @Key("order.inactiveSampleTypeWarning")
  String order_inactiveSampleTypeWarning(String arg0);

  /**
   * Translated "The test {0}, {1} is inactive and was not duplicated".
   * 
   * @param arg0 "{0}"
   * @param arg1 "{1}"
   * @return translated "The test {0}, {1} is inactive and was not duplicated"
  
   */
  @DefaultMessage("The test {0}, {1} is inactive and was not duplicated")
  @Key("order.inactiveTestWarning")
  String order_inactiveTestWarning(String arg0,  String arg1);

  /**
   * Translated "Order # {0,number,#0}: The sample type for the container with Item # {1} is invalid for ''{2}''  ".
   * 
   * @param arg0 "{0,number,#0}"
   * @param arg1 "{1}"
   * @param arg2 "{2}"
   * @return translated "Order # {0,number,#0}: The sample type for the container with Item # {1} is invalid for ''{2}''  "
  
   */
  @DefaultMessage("Order # {0,number,#0}: The sample type for the container with Item # {1} is invalid for ''{2}''  ")
  @Key("order.invalidSampleTypeForTestWarning")
  String order_invalidSampleTypeForTestWarning(Integer arg0,  String arg1,  String arg2);

  /**
   * Translated "Order # {0,number,#0}: Inventory Item is required".
   * 
   * @param arg0 "{0,number,#0}"
   * @return translated "Order # {0,number,#0}: Inventory Item is required"
  
   */
  @DefaultMessage("Order # {0,number,#0}: Inventory Item is required")
  @Key("order.inventoryItemRequiredException")
  String order_inventoryItemRequiredException(Integer arg0);

  /**
   * Translated "Order # {0,number,#0}: Inventory Quantity is required".
   * 
   * @param arg0 "{0,number,#0}"
   * @return translated "Order # {0,number,#0}: Inventory Quantity is required"
  
   */
  @DefaultMessage("Order # {0,number,#0}: Inventory Quantity is required")
  @Key("order.inventoryQuantityRequiredException")
  String order_inventoryQuantityRequiredException(Integer arg0);

  /**
   * Translated "Item #".
   * 
   * @return translated "Item #"
  
   */
  @DefaultMessage("Item #")
  @Key("order.itemNum")
  String order_itemNum();

  /**
   * Translated "Items".
   * 
   * @return translated "Items"
  
   */
  @DefaultMessage("Items")
  @Key("order.items")
  String order_items();

  /**
   * Translated "Order # {0,number,#0}: Duplicating more than one container is not allowed".
   * 
   * @param arg0 "{0,number,#0}"
   * @return translated "Order # {0,number,#0}: Duplicating more than one container is not allowed"
  
   */
  @DefaultMessage("Order # {0,number,#0}: Duplicating more than one container is not allowed")
  @Key("order.multiRowDuplicateNotAllowed")
  String order_multiRowDuplicateNotAllowed(Integer arg0);

  /**
   * Translated "Order # {0,number,#0}: Moving up/down more than one container is not allowed".
   * 
   * @param arg0 "{0,number,#0}"
   * @return translated "Order # {0,number,#0}: Moving up/down more than one container is not allowed"
  
   */
  @DefaultMessage("Order # {0,number,#0}: Moving up/down more than one container is not allowed")
  @Key("order.multiRowMoveNotAllowed")
  String order_multiRowMoveNotAllowed(Integer arg0);

  /**
   * Translated "Order # {0,number,#0}: Check All/Uncheck All for more than one test is not allowed".
   * 
   * @param arg0 "{0,number,#0}"
   * @return translated "Order # {0,number,#0}: Check All/Uncheck All for more than one test is not allowed"
  
   */
  @DefaultMessage("Order # {0,number,#0}: Check All/Uncheck All for more than one test is not allowed")
  @Key("order.multiTestCheckNotAllowed")
  String order_multiTestCheckNotAllowed(Integer arg0);

  /**
   * Translated "Order # {0,number,#0}\": Only one record with the type Bill To is allowed".
   * 
   * @param arg0 "{0,number,#0}"
   * @return translated "Order # {0,number,#0}\": Only one record with the type Bill To is allowed"
  
   */
  @DefaultMessage("Order # {0,number,#0}\": Only one record with the type Bill To is allowed")
  @Key("order.multipleBillToException")
  String order_multipleBillToException(Integer arg0);

  /**
   * Translated "Order # {0,number,#0}: Only one record with the type Report To is allowed ".
   * 
   * @param arg0 "{0,number,#0}"
   * @return translated "Order # {0,number,#0}: Only one record with the type Report To is allowed "
  
   */
  @DefaultMessage("Order # {0,number,#0}: Only one record with the type Report To is allowed ")
  @Key("order.multipleReportToException")
  String order_multipleReportToException(Integer arg0);

  /**
   * Translated "Needed In Days".
   * 
   * @return translated "Needed In Days"
  
   */
  @DefaultMessage("Needed In Days")
  @Key("order.neededDays")
  String order_neededDays();

  /**
   * Translated "Order # {0,number,#0}: Needed In Days is required".
   * 
   * @param arg0 "{0,number,#0}"
   * @return translated "Order # {0,number,#0}: Needed In Days is required"
  
   */
  @DefaultMessage("Order # {0,number,#0}: Needed In Days is required")
  @Key("order.neededInDaysRequiredException")
  String order_neededInDaysRequiredException(Integer arg0);

  /**
   * Translated "Order # {0,number,#0}: No container is present for Item # {1} which is assigned to ''{2}'' ".
   * 
   * @param arg0 "{0,number,#0}"
   * @param arg1 "{1}"
   * @param arg2 "{2}"
   * @return translated "Order # {0,number,#0}: No container is present for Item # {1} which is assigned to ''{2}'' "
  
   */
  @DefaultMessage("Order # {0,number,#0}: No container is present for Item # {1} which is assigned to ''{2}'' ")
  @Key("order.noContainerWithItemNumWarning")
  String order_noContainerWithItemNumWarning(Integer arg0,  String arg1,  String arg2);

  /**
   * Translated "Order # {0,number,#0}: No sample type specified for the container with Item # {1}  ".
   * 
   * @param arg0 "{0,number,#0}"
   * @param arg1 "{1}"
   * @return translated "Order # {0,number,#0}: No sample type specified for the container with Item # {1}  "
  
   */
  @DefaultMessage("Order # {0,number,#0}: No sample type specified for the container with Item # {1}  ")
  @Key("order.noSampleTypeForContainerWarning")
  String order_noSampleTypeForContainerWarning(Integer arg0,  String arg1);

  /**
   * Translated "Order # {0,number,#0}: Not all dates generated with this frequency are valid".
   * 
   * @param arg0 "{0,number,#0}"
   * @return translated "Order # {0,number,#0}: Not all dates generated with this frequency are valid"
  
   */
  @DefaultMessage("Order # {0,number,#0}: Not all dates generated with this frequency are valid")
  @Key("order.notAllDatesValid")
  String order_notAllDatesValid(Integer arg0);

  /**
   * Translated "# Forms".
   * 
   * @return translated "# Forms"
  
   */
  @DefaultMessage("# Forms")
  @Key("order.numForms")
  String order_numForms();

  /**
   * Translated "Order # {0,number,#0}: # Forms is required".
   * 
   * @param arg0 "{0,number,#0}"
   * @return translated "Order # {0,number,#0}: # Forms is required"
  
   */
  @DefaultMessage("Order # {0,number,#0}: # Forms is required")
  @Key("order.numFormsRequiredException")
  String order_numFormsRequiredException(Integer arg0);

  /**
   * Translated "Order".
   * 
   * @return translated "Order"
  
   */
  @DefaultMessage("Order")
  @Key("order.order")
  String order_order();

  /**
   * Translated "History - Container".
   * 
   * @return translated "History - Container"
  
   */
  @DefaultMessage("History - Container")
  @Key("order.orderContainerHistory")
  String order_orderContainerHistory();

  /**
   * Translated "Order Date".
   * 
   * @return translated "Order Date"
  
   */
  @DefaultMessage("Order Date")
  @Key("order.orderDate")
  String order_orderDate();

  /**
   * Translated "History - Order".
   * 
   * @return translated "History - Order"
  
   */
  @DefaultMessage("History - Order")
  @Key("order.orderHistory")
  String order_orderHistory();

  /**
   * Translated "History - Item".
   * 
   * @return translated "History - Item"
  
   */
  @DefaultMessage("History - Item")
  @Key("order.orderItemHistory")
  String order_orderItemHistory();

  /**
   * Translated "Order #".
   * 
   * @return translated "Order #"
  
   */
  @DefaultMessage("Order #")
  @Key("order.orderNum")
  String order_orderNum();

  /**
   * Translated "History - Organization".
   * 
   * @return translated "History - Organization"
  
   */
  @DefaultMessage("History - Organization")
  @Key("order.orderOrganizationHistory")
  String order_orderOrganizationHistory();

  /**
   * Translated "Order Will Recur On".
   * 
   * @return translated "Order Will Recur On"
  
   */
  @DefaultMessage("Order Will Recur On")
  @Key("order.orderRecurOn")
  String order_orderRecurOn();

  /**
   * Translated "Shipping Notes".
   * 
   * @return translated "Shipping Notes"
  
   */
  @DefaultMessage("Shipping Notes")
  @Key("order.orderShippingNotes")
  String order_orderShippingNotes();

  /**
   * Translated "History - Test".
   * 
   * @return translated "History - Test"
  
   */
  @DefaultMessage("History - Test")
  @Key("order.orderTestHistory")
  String order_orderTestHistory();

  /**
   * Translated "Order # {0,number,#0}: Organization is required".
   * 
   * @param arg0 "{0,number,#0}"
   * @return translated "Order # {0,number,#0}: Organization is required"
  
   */
  @DefaultMessage("Order # {0,number,#0}: Organization is required")
  @Key("order.organizationRequiredException")
  String order_organizationRequiredException(Integer arg0);

  /**
   * Translated "Order # {0,number,#0}: Organization Type is required".
   * 
   * @param arg0 "{0,number,#0}"
   * @return translated "Order # {0,number,#0}: Organization Type is required"
  
   */
  @DefaultMessage("Order # {0,number,#0}: Organization Type is required")
  @Key("order.organizationTypeRequiredException")
  String order_organizationTypeRequiredException(Integer arg0);

  /**
   * Translated "Parent Order #".
   * 
   * @return translated "Parent Order #"
  
   */
  @DefaultMessage("Parent Order #")
  @Key("order.parentOrderNum")
  String order_parentOrderNum();

  /**
   * Translated "Order # {0,number,#0}: This value must not exceed {1}".
   * 
   * @param arg0 "{0,number,#0}"
   * @param arg1 "{1}"
   * @return translated "Order # {0,number,#0}: This value must not exceed {1}"
  
   */
  @DefaultMessage("Order # {0,number,#0}: This value must not exceed {1}")
  @Key("order.qtyNotMoreThanMaxException")
  String order_qtyNotMoreThanMaxException(Integer arg0,  String arg1);

  /**
   * Translated "Recur".
   * 
   * @return translated "Recur"
  
   */
  @DefaultMessage("Recur")
  @Key("order.recur")
  String order_recur();

  /**
   * Translated "Errors ocurred while recuring the order".
   * 
   * @return translated "Errors ocurred while recuring the order"
  
   */
  @DefaultMessage("Errors ocurred while recuring the order")
  @Key("order.recurError")
  String order_recurError();

  /**
   * Translated "Order # {0,number,#0}: Recurrence Active Begin is required".
   * 
   * @param arg0 "{0,number,#0}"
   * @return translated "Order # {0,number,#0}: Recurrence Active Begin is required"
  
   */
  @DefaultMessage("Order # {0,number,#0}: Recurrence Active Begin is required")
  @Key("order.recurrenceActiveBeginRequiredException")
  String order_recurrenceActiveBeginRequiredException(Integer arg0);

  /**
   * Translated "Order # {0,number,#0}: Recurrence Active End is required".
   * 
   * @param arg0 "{0,number,#0}"
   * @return translated "Order # {0,number,#0}: Recurrence Active End is required"
  
   */
  @DefaultMessage("Order # {0,number,#0}: Recurrence Active End is required")
  @Key("order.recurrenceActiveEndRequiredException")
  String order_recurrenceActiveEndRequiredException(Integer arg0);

  /**
   * Translated "Order # {0,number,#0}: Recurrence Frequency is required".
   * 
   * @param arg0 "{0,number,#0}"
   * @return translated "Order # {0,number,#0}: Recurrence Frequency is required"
  
   */
  @DefaultMessage("Order # {0,number,#0}: Recurrence Frequency is required")
  @Key("order.recurrenceFrequencyRequiredException")
  String order_recurrenceFrequencyRequiredException(Integer arg0);

  /**
   * Translated "Order # {0,number,#0}: Recurrence Unit is required".
   * 
   * @param arg0 "{0,number,#0}"
   * @return translated "Order # {0,number,#0}: Recurrence Unit is required"
  
   */
  @DefaultMessage("Order # {0,number,#0}: Recurrence Unit is required")
  @Key("order.recurrenceUnitRequiredException")
  String order_recurrenceUnitRequiredException(Integer arg0);

  /**
   * Translated "Remove Test".
   * 
   * @return translated "Remove Test"
  
   */
  @DefaultMessage("Remove Test")
  @Key("order.removeTest")
  String order_removeTest();

  /**
   * Translated "Requested By".
   * 
   * @return translated "Requested By"
  
   */
  @DefaultMessage("Requested By")
  @Key("order.requestedBy")
  String order_requestedBy();

  /**
   * Translated "Send-out Order".
   * 
   * @return translated "Send-out Order"
  
   */
  @DefaultMessage("Send-out Order")
  @Key("order.sendoutOrder")
  String order_sendoutOrder();

  /**
   * Translated "Order items and supplies to be sent to external users.".
   * 
   * @return translated "Order items and supplies to be sent to external users."
  
   */
  @DefaultMessage("Order items and supplies to be sent to external users.")
  @Key("order.sendoutOrderDescription")
  String order_sendoutOrderDescription();

  /**
   * Translated "Ship From".
   * 
   * @return translated "Ship From"
  
   */
  @DefaultMessage("Ship From")
  @Key("order.shipFrom")
  String order_shipFrom();

  /**
   * Translated "Order # {0,number,#0}: Ship From is required".
   * 
   * @param arg0 "{0,number,#0}"
   * @return translated "Order # {0,number,#0}: Ship From is required"
  
   */
  @DefaultMessage("Order # {0,number,#0}: Ship From is required")
  @Key("order.shipFromRequiredException")
  String order_shipFromRequiredException(Integer arg0);

  /**
   * Translated "Ship To".
   * 
   * @return translated "Ship To"
  
   */
  @DefaultMessage("Ship To")
  @Key("order.shipTo")
  String order_shipTo();

  /**
   * Translated "Shipping Information".
   * 
   * @return translated "Shipping Information"
  
   */
  @DefaultMessage("Shipping Information")
  @Key("order.shippingInfo")
  String order_shippingInfo();

  /**
   * Translated "Show Upcoming Dates".
   * 
   * @return translated "Show Upcoming Dates"
  
   */
  @DefaultMessage("Show Upcoming Dates")
  @Key("order.showDates")
  String order_showDates();

  /**
   * Translated "Order # {0,number,#0} Status is required".
   * 
   * @param arg0 "{0,number,#0}"
   * @return translated "Order # {0,number,#0} Status is required"
  
   */
  @DefaultMessage("Order # {0,number,#0} Status is required")
  @Key("order.statusRequiredException")
  String order_statusRequiredException(Integer arg0);

  /**
   * Translated "Test by Method, Description".
   * 
   * @return translated "Test by Method, Description"
  
   */
  @DefaultMessage("Test by Method, Description")
  @Key("order.testByMethodDescription")
  String order_testByMethodDescription();

  /**
   * Translated "Unit Cost".
   * 
   * @return translated "Unit Cost"
  
   */
  @DefaultMessage("Unit Cost")
  @Key("order.unitCost")
  String order_unitCost();

  /**
   * Translated "Vendor".
   * 
   * @return translated "Vendor"
  
   */
  @DefaultMessage("Vendor")
  @Key("order.vendor")
  String order_vendor();

  /**
   * Translated "{0} couldn''t be found ".
   * 
   * @param arg0 "{0}"
   * @return translated "{0} couldn''t be found "
  
   */
  @DefaultMessage("{0} couldn''t be found ")
  @Key("orderAuxDataNotFoundError")
  String orderAuxDataNotFoundError(String arg0);

  /**
   * Translated "History - Container".
   * 
   * @return translated "History - Container"
  
   */
  @DefaultMessage("History - Container")
  @Key("orderContainerHistory")
  String orderContainerHistory();

  /**
   * Translated "Order Date".
   * 
   * @return translated "Order Date"
  
   */
  @DefaultMessage("Order Date")
  @Key("orderDate")
  String orderDate();

  /**
   * Translated "Place and fill orders for inventory items.".
   * 
   * @return translated "Place and fill orders for inventory items."
  
   */
  @DefaultMessage("Place and fill orders for inventory items.")
  @Key("orderDescription")
  String orderDescription();

  /**
   * Translated "History - Order".
   * 
   * @return translated "History - Order"
  
   */
  @DefaultMessage("History - Order")
  @Key("orderHistory")
  String orderHistory();

  /**
   * Translated "Order # must be the id of an existing Send-out order".
   * 
   * @return translated "Order # must be the id of an existing Send-out order"
  
   */
  @DefaultMessage("Order # must be the id of an existing Send-out order")
  @Key("orderIdInvalidException")
  String orderIdInvalidException();

  /**
   * Translated "{0} ''{1}'' is invalid and couldn''t be imported".
   * 
   * @param arg0 "{0}"
   * @param arg1 "{1}"
   * @return translated "{0} ''{1}'' is invalid and couldn''t be imported"
  
   */
  @DefaultMessage("{0} ''{1}'' is invalid and couldn''t be imported")
  @Key("orderImportError")
  String orderImportError(String arg0,  String arg1);

  /**
   * Translated "History - Item".
   * 
   * @return translated "History - Item"
  
   */
  @DefaultMessage("History - Item")
  @Key("orderItemHistory")
  String orderItemHistory();

  /**
   * Translated "Note From Order".
   * 
   * @return translated "Note From Order"
  
   */
  @DefaultMessage("Note From Order")
  @Key("orderNoteSubject")
  String orderNoteSubject();

  /**
   * Translated "Order #".
   * 
   * @return translated "Order #"
  
   */
  @DefaultMessage("Order #")
  @Key("orderNum")
  String orderNum();

  /**
   * Translated "History - Organization".
   * 
   * @return translated "History - Organization"
  
   */
  @DefaultMessage("History - Organization")
  @Key("orderOrganizationHistory")
  String orderOrganizationHistory();

  /**
   * Translated "Order Will Recur On".
   * 
   * @return translated "Order Will Recur On"
  
   */
  @DefaultMessage("Order Will Recur On")
  @Key("orderRecurOn")
  String orderRecurOn();

  /**
   * Translated "Order Recurrence".
   * 
   * @return translated "Order Recurrence"
  
   */
  @DefaultMessage("Order Recurrence")
  @Key("orderRecurrence")
  String orderRecurrence();

  /**
   * Translated "Order Request Form".
   * 
   * @return translated "Order Request Form"
  
   */
  @DefaultMessage("Order Request Form")
  @Key("orderRequestForm")
  String orderRequestForm();

  /**
   * Translated "Shipping Notes".
   * 
   * @return translated "Shipping Notes"
  
   */
  @DefaultMessage("Shipping Notes")
  @Key("orderShippingNotes")
  String orderShippingNotes();

  /**
   * Translated "This order is not pending; you cannot update it ".
   * 
   * @return translated "This order is not pending; you cannot update it "
  
   */
  @DefaultMessage("This order is not pending; you cannot update it ")
  @Key("orderStatusNotPendingForUpdate")
  String orderStatusNotPendingForUpdate();

  /**
   * Translated "This order is neither pending nor recurring; you cannot update it ".
   * 
   * @return translated "This order is neither pending nor recurring; you cannot update it "
  
   */
  @DefaultMessage("This order is neither pending nor recurring; you cannot update it ")
  @Key("orderStatusNotPendingOrRecurForUpdate")
  String orderStatusNotPendingOrRecurForUpdate();

  /**
   * Translated "History - Test".
   * 
   * @return translated "History - Test"
  
   */
  @DefaultMessage("History - Test")
  @Key("orderTestHistory")
  String orderTestHistory();

  /**
   * Translated "History - Organization Address ".
   * 
   * @return translated "History - Organization Address "
  
   */
  @DefaultMessage("History - Organization Address ")
  @Key("orgAddressHistory")
  String orgAddressHistory();

  /**
   * Translated "History - Contact Address".
   * 
   * @return translated "History - Contact Address"
  
   */
  @DefaultMessage("History - Contact Address")
  @Key("orgContactAddressHistory")
  String orgContactAddressHistory();

  /**
   * Translated "History - Contact".
   * 
   * @return translated "History - Contact"
  
   */
  @DefaultMessage("History - Contact")
  @Key("orgContactHistory")
  String orgContactHistory();

  /**
   * Translated "History - Organization".
   * 
   * @return translated "History - Organization"
  
   */
  @DefaultMessage("History - Organization")
  @Key("orgHistory")
  String orgHistory();

  /**
   * Translated "Org Id".
   * 
   * @return translated "Org Id"
  
   */
  @DefaultMessage("Org Id")
  @Key("orgId")
  String orgId();

  /**
   * Translated "Samples from {0} are to be held or refused   ".
   * 
   * @param arg0 "{0}"
   * @return translated "Samples from {0} are to be held or refused   "
  
   */
  @DefaultMessage("Samples from {0} are to be held or refused   ")
  @Key("orgMarkedAsHoldRefuseSample")
  String orgMarkedAsHoldRefuseSample(String arg0);

  /**
   * Translated "An organization must be specified for a section flagged as \"External\"".
   * 
   * @return translated "An organization must be specified for a section flagged as \"External\""
  
   */
  @DefaultMessage("An organization must be specified for a section flagged as \"External\"")
  @Key("orgNotSpecForExtSectionException")
  String orgNotSpecForExtSectionException();

  /**
   * Translated "History - Parameter".
   * 
   * @return translated "History - Parameter"
  
   */
  @DefaultMessage("History - Parameter")
  @Key("orgParameterHistory")
  String orgParameterHistory();

  /**
   * Translated "Organization".
   * 
   * @return translated "Organization"
  
   */
  @DefaultMessage("Organization")
  @Key("organization")
  String organization();

  /**
   * Translated "Cell #".
   * 
   * @return translated "Cell #"
  
   */
  @DefaultMessage("Cell #")
  @Key("organization.cellNumber")
  String organization_cellNumber();

  /**
   * Translated "Email".
   * 
   * @return translated "Email"
  
   */
  @DefaultMessage("Email")
  @Key("organization.email")
  String organization_email();

  /**
   * Translated "Fax #".
   * 
   * @return translated "Fax #"
  
   */
  @DefaultMessage("Fax #")
  @Key("organization.faxNumber")
  String organization_faxNumber();

  /**
   * Translated "Home #".
   * 
   * @return translated "Home #"
  
   */
  @DefaultMessage("Home #")
  @Key("organization.homeNumber")
  String organization_homeNumber();

  /**
   * Translated "Work #".
   * 
   * @return translated "Work #"
  
   */
  @DefaultMessage("Work #")
  @Key("organization.workNumber")
  String organization_workNumber();

  /**
   * Translated "Manage submitter and partner information.".
   * 
   * @return translated "Manage submitter and partner information."
  
   */
  @DefaultMessage("Manage submitter and partner information.")
  @Key("organizationDescription")
  String organizationDescription();

  /**
   * Translated "Organization Info".
   * 
   * @return translated "Organization Info"
  
   */
  @DefaultMessage("Organization Info")
  @Key("organizationInfo")
  String organizationInfo();

  /**
   * Translated "Organization Name".
   * 
   * @return translated "Organization Name"
  
   */
  @DefaultMessage("Organization Name")
  @Key("organizationName")
  String organizationName();

  /**
   * Translated "Organization #".
   * 
   * @return translated "Organization #"
  
   */
  @DefaultMessage("Organization #")
  @Key("organizationNum")
  String organizationNum();

  /**
   * Translated "Organizations".
   * 
   * @return translated "Organizations"
  
   */
  @DefaultMessage("Organizations")
  @Key("organizationRef")
  String organizationRef();

  /**
   * Translated "ClientReleasedReport".
   * 
   * @return translated "ClientReleasedReport"
  
   */
  @DefaultMessage("ClientReleasedReport")
  @Key("organizationRelRef")
  String organizationRelRef();

  /**
   * Translated "Organizations".
   * 
   * @return translated "Organizations"
  
   */
  @DefaultMessage("Organizations")
  @Key("organizations")
  String organizations();

  /**
   * Translated "Other".
   * 
   * @return translated "Other"
  
   */
  @DefaultMessage("Other")
  @Key("other")
  String other();

  /**
   * Translated "Other analytes from the same analyte group were also removed".
   * 
   * @return translated "Other analytes from the same analyte group were also removed"
  
   */
  @DefaultMessage("Other analytes from the same analyte group were also removed")
  @Key("otherAnalytesRemoved")
  String otherAnalytesRemoved();

  /**
   * Translated "Override ".
   * 
   * @return translated "Override "
  
   */
  @DefaultMessage("Override ")
  @Key("override")
  String override();

  /**
   * Translated "Owner".
   * 
   * @return translated "Owner"
  
   */
  @DefaultMessage("Owner")
  @Key("owner")
  String owner();

  /**
   * Translated "P1".
   * 
   * @return translated "P1"
  
   */
  @DefaultMessage("P1")
  @Key("p1")
  String p1();

  /**
   * Translated "P2".
   * 
   * @return translated "P2"
  
   */
  @DefaultMessage("P2")
  @Key("p2")
  String p2();

  /**
   * Translated "P3   ".
   * 
   * @return translated "P3   "
  
   */
  @DefaultMessage("P3   ")
  @Key("p3")
  String p3();

  /**
   * Translated "Panel".
   * 
   * @return translated "Panel"
  
   */
  @DefaultMessage("Panel")
  @Key("panel")
  String panel();

  /**
   * Translated "Define a grouping of tests that are often assigned to a sample.".
   * 
   * @return translated "Define a grouping of tests that are often assigned to a sample."
  
   */
  @DefaultMessage("Define a grouping of tests that are often assigned to a sample.")
  @Key("panelDescription")
  String panelDescription();

  /**
   * Translated "History - Panel".
   * 
   * @return translated "History - Panel"
  
   */
  @DefaultMessage("History - Panel")
  @Key("panelHistory")
  String panelHistory();

  /**
   * Translated "Panel Item".
   * 
   * @return translated "Panel Item"
  
   */
  @DefaultMessage("Panel Item")
  @Key("panelItem")
  String panelItem();

  /**
   * Translated "History - Panel Item".
   * 
   * @return translated "History - Panel Item"
  
   */
  @DefaultMessage("History - Panel Item")
  @Key("panelItemHistory")
  String panelItemHistory();

  /**
   * Translated "Error loading sections for selected panel".
   * 
   * @return translated "Error loading sections for selected panel"
  
   */
  @DefaultMessage("Error loading sections for selected panel")
  @Key("panelSectionLoadError")
  String panelSectionLoadError();

  /**
   * Translated "Panel Selection".
   * 
   * @return translated "Panel Selection"
  
   */
  @DefaultMessage("Panel Selection")
  @Key("panelSelection")
  String panelSelection();

  /**
   * Translated "Parameter".
   * 
   * @return translated "Parameter"
  
   */
  @DefaultMessage("Parameter")
  @Key("parameter")
  String parameter();

  /**
   * Translated "Parameters ".
   * 
   * @return translated "Parameters "
  
   */
  @DefaultMessage("Parameters ")
  @Key("parameters")
  String parameters();

  /**
   * Translated "Parent".
   * 
   * @return translated "Parent"
  
   */
  @DefaultMessage("Parent")
  @Key("parent")
  String parent();

  /**
   * Translated "Parent Analyte".
   * 
   * @return translated "Parent Analyte"
  
   */
  @DefaultMessage("Parent Analyte")
  @Key("parentAnalyte")
  String parentAnalyte();

  /**
   * Translated "Parent Item".
   * 
   * @return translated "Parent Item"
  
   */
  @DefaultMessage("Parent Item")
  @Key("parentItem")
  String parentItem();

  /**
   * Translated "Parent Location".
   * 
   * @return translated "Parent Location"
  
   */
  @DefaultMessage("Parent Location")
  @Key("parentLocation")
  String parentLocation();

  /**
   * Translated "An inventory item cannot be flagged as Lot # Required if its parent inventory item is not".
   * 
   * @return translated "An inventory item cannot be flagged as Lot # Required if its parent inventory item is not"
  
   */
  @DefaultMessage("An inventory item cannot be flagged as Lot # Required if its parent inventory item is not")
  @Key("parentNotFlaggedLotReqException")
  String parentNotFlaggedLotReqException();

  /**
   * Translated "This is the id of the order that was recurred to create this order".
   * 
   * @return translated "This is the id of the order that was recurred to create this order"
  
   */
  @DefaultMessage("This is the id of the order that was recurred to create this order")
  @Key("parentOrderDescription")
  String parentOrderDescription();

  /**
   * Translated "Parent Order #".
   * 
   * @return translated "Parent Order #"
  
   */
  @DefaultMessage("Parent Order #")
  @Key("parentOrderNum")
  String parentOrderNum();

  /**
   * Translated "Parent Org".
   * 
   * @return translated "Parent Org"
  
   */
  @DefaultMessage("Parent Org")
  @Key("parentOrganization")
  String parentOrganization();

  /**
   * Translated "Parent Ratio".
   * 
   * @return translated "Parent Ratio"
  
   */
  @DefaultMessage("Parent Ratio")
  @Key("parentRatio")
  String parentRatio();

  /**
   * Translated "Parent ratio must be greater than zero".
   * 
   * @return translated "Parent ratio must be greater than zero"
  
   */
  @DefaultMessage("Parent ratio must be greater than zero")
  @Key("parentRatioMoreThanZeroException")
  String parentRatioMoreThanZeroException();

  /**
   * Translated "Parent ratio is required if a parent inventory item is specified".
   * 
   * @return translated "Parent ratio is required if a parent inventory item is specified"
  
   */
  @DefaultMessage("Parent ratio is required if a parent inventory item is specified")
  @Key("parentRatioReqIfParentItemSpecException")
  String parentRatioReqIfParentItemSpecException();

  /**
   * Translated "Parent Section".
   * 
   * @return translated "Parent Section"
  
   */
  @DefaultMessage("Parent Section")
  @Key("parentSection")
  String parentSection();

  /**
   * Translated "Parent Storage".
   * 
   * @return translated "Parent Storage"
  
   */
  @DefaultMessage("Parent Storage")
  @Key("parentStorage")
  String parentStorage();

  /**
   * Translated "Password:".
   * 
   * @return translated "Password:"
  
   */
  @DefaultMessage("Password:")
  @Key("password")
  String password();

  /**
   * Translated "Paste".
   * 
   * @return translated "Paste"
  
   */
  @DefaultMessage("Paste")
  @Key("paste")
  String paste();

  /**
   * Translated "Patient".
   * 
   * @return translated "Patient"
  
   */
  @DefaultMessage("Patient")
  @Key("patient")
  String patient();

  /**
   * Translated "Birth".
   * 
   * @return translated "Birth"
  
   */
  @DefaultMessage("Birth")
  @Key("patient.birth")
  String patient_birth();

  /**
   * Translated "Birth (D,T)".
   * 
   * @return translated "Birth (D,T)"
  
   */
  @DefaultMessage("Birth (D,T)")
  @Key("patient.birthDT")
  String patient_birthDT();

  /**
   * Translated "Birth date is required for a patient".
   * 
   * @return translated "Birth date is required for a patient"
  
   */
  @DefaultMessage("Birth date is required for a patient")
  @Key("patient.birthDateRequiredException")
  String patient_birthDateRequiredException();

  /**
   * Translated "City is required for a patient".
   * 
   * @return translated "City is required for a patient"
  
   */
  @DefaultMessage("City is required for a patient")
  @Key("patient.cityRequiredException")
  String patient_cityRequiredException();

  /**
   * Translated "Edit".
   * 
   * @return translated "Edit"
  
   */
  @DefaultMessage("Edit")
  @Key("patient.edit")
  String patient_edit();

  /**
   * Translated "Ethnicity".
   * 
   * @return translated "Ethnicity"
  
   */
  @DefaultMessage("Ethnicity")
  @Key("patient.ethnicity")
  String patient_ethnicity();

  /**
   * Translated "First name is required for a patient".
   * 
   * @return translated "First name is required for a patient"
  
   */
  @DefaultMessage("First name is required for a patient")
  @Key("patient.firstNameRequiredException")
  String patient_firstNameRequiredException();

  /**
   * Translated "Full Window Search".
   * 
   * @return translated "Full Window Search"
  
   */
  @DefaultMessage("Full Window Search")
  @Key("patient.fullWindowSearch")
  String patient_fullWindowSearch();

  /**
   * Translated "A future birth date is not allowed for a patient".
   * 
   * @return translated "A future birth date is not allowed for a patient"
   */
  @DefaultMessage("A future birth date is not allowed for a patient")
  @Key("patient.futureBirthDateException")
  String patient_futureBirthDateException();

  /**
   * Translated "Gender".
   * 
   * @return translated "Gender"
  
   */
  @DefaultMessage("Gender")
  @Key("patient.gender")
  String patient_gender();

  /**
   * Translated "Last name is required for a patient".
   * 
   * @return translated "Last name is required for a patient"
  
   */
  @DefaultMessage("Last name is required for a patient")
  @Key("patient.lastnameRequiredException")
  String patient_lastnameRequiredException();

  /**
   * Translated "Maiden".
   * 
   * @return translated "Maiden"
  
   */
  @DefaultMessage("Maiden")
  @Key("patient.maiden")
  String patient_maiden();

  /**
   * Translated "NID".
   * 
   * @return translated "NID"
  
   */
  @DefaultMessage("NID")
  @Key("patient.nationalId")
  String patient_nationalId();

  /**
   * Translated "Next Of Kin".
   * 
   * @return translated "Next Of Kin"
  
   */
  @DefaultMessage("Next Of Kin")
  @Key("patient.nextOfKin")
  String patient_nextOfKin();

  /**
   * Translated "Patient".
   * 
   * @return translated "Patient"
  
   */
  @DefaultMessage("Patient")
  @Key("patient.patient")
  String patient_patient();

  /**
   * Translated "Race".
   * 
   * @return translated "Race"
  
   */
  @DefaultMessage("Race")
  @Key("patient.race")
  String patient_race();

  /**
   * Translated "Relation".
   * 
   * @return translated "Relation"
  
   */
  @DefaultMessage("Relation")
  @Key("patient.relation")
  String patient_relation();

  /**
   * Translated "Search By Fields".
   * 
   * @return translated "Search By Fields"
  
   */
  @DefaultMessage("Search By Fields")
  @Key("patient.searchByFields")
  String patient_searchByFields();

  /**
   * Translated "Street Address is required for a patient ".
   * 
   * @return translated "Street Address is required for a patient "
  
   */
  @DefaultMessage("Street Address is required for a patient ")
  @Key("patient.streetAddressRequiredException")
  String patient_streetAddressRequiredException();

  /**
   * Translated "Unlink".
   * 
   * @return translated "Unlink"
  
   */
  @DefaultMessage("Unlink")
  @Key("patient.unlink")
  String patient_unlink();

  /**
   * Translated "Patient description".
   * 
   * @return translated "Patient description"
  
   */
  @DefaultMessage("Patient description")
  @Key("patientDescription")
  String patientDescription();

  /**
   * Translated "This NID has been used for another patient".
   * 
   * @return translated "This NID has been used for another patient"
   */
  @DefaultMessage("This NID has been used for another patient")
  @Key("patientLookup.nidUsedForOtherPatient")
  String patientLookup_nidUsedForOtherPatient();

  /**
   * Translated "Patient Lookup".
   * 
   * @return translated "Patient Lookup"
  
   */
  @DefaultMessage("Patient Lookup")
  @Key("patientLookup.patientLookup")
  String patientLookup_patientLookup();

  /**
   * Translated "Period ".
   * 
   * @return translated "Period "
  
   */
  @DefaultMessage("Period ")
  @Key("period")
  String period();

  /**
   * Translated "Person".
   * 
   * @return translated "Person"
  
   */
  @DefaultMessage("Person")
  @Key("person")
  String person();

  /**
   * Translated "Person description".
   * 
   * @return translated "Person description"
  
   */
  @DefaultMessage("Person description")
  @Key("personDescription")
  String personDescription();

  /**
   * Translated "Phone #".
   * 
   * @return translated "Phone #"
  
   */
  @DefaultMessage("Phone #")
  @Key("phone")
  String phone();

  /**
   * Translated "Phone/Email".
   * 
   * @return translated "Phone/Email"
  
   */
  @DefaultMessage("Phone/Email")
  @Key("phoneEmail")
  String phoneEmail();

  /**
   * Translated "999/999-9999".
   * 
   * @return translated "999/999-9999"
  
   */
  @DefaultMessage("999/999-9999")
  @Key("phonePattern")
  String phonePattern();

  /**
   * Translated "999/999-9999.9999".
   * 
   * @return translated "999/999-9999.9999"
  
   */
  @DefaultMessage("999/999-9999.9999")
  @Key("phoneWithExtensionPattern")
  String phoneWithExtensionPattern();

  /**
   * Translated "Phys #".
   * 
   * @return translated "Phys #"
  
   */
  @DefaultMessage("Phys #")
  @Key("physCount")
  String physCount();

  /**
   * Translated "Phys # must not be equal to On Hand".
   * 
   * @return translated "Phys # must not be equal to On Hand"
  
   */
  @DefaultMessage("Phys # must not be equal to On Hand")
  @Key("physCountNotEqualToOnHandException")
  String physCountNotEqualToOnHandException();

  /**
   * Translated "Please select a type     ".
   * 
   * @return translated "Please select a type     "
  
   */
  @DefaultMessage("Please select a type     ")
  @Key("pleaseSelectType")
  String pleaseSelectType();

  /**
   * Translated "Plot".
   * 
   * @return translated "Plot"
  
   */
  @DefaultMessage("Plot")
  @Key("plot")
  String plot();

  /**
   * Translated "Plot Data".
   * 
   * @return translated "Plot Data"
  
   */
  @DefaultMessage("Plot Data")
  @Key("plotData")
  String plotData();

  /**
   * Translated "Plot Date".
   * 
   * @return translated "Plot Date"
  
   */
  @DefaultMessage("Plot Date")
  @Key("plotDate")
  String plotDate();

  /**
   * Translated "Plot Interval".
   * 
   * @return translated "Plot Interval"
  
   */
  @DefaultMessage("Plot Interval")
  @Key("plotInterval")
  String plotInterval();

  /**
   * Translated "Plot Using".
   * 
   * @return translated "Plot Using"
  
   */
  @DefaultMessage("Plot Using")
  @Key("plotUsing")
  String plotUsing();

  /**
   * Translated "Plot Value".
   * 
   * @return translated "Plot Value"
  
   */
  @DefaultMessage("Plot Value")
  @Key("plotValue")
  String plotValue();

  /**
   * Translated "Please select a category".
   * 
   * @return translated "Please select a category"
  
   */
  @DefaultMessage("Please select a category")
  @Key("plsSelCat")
  String plsSelCat();

  /**
   * Translated "Please select a store".
   * 
   * @return translated "Please select a store"
  
   */
  @DefaultMessage("Please select a store")
  @Key("plsSelStore")
  String plsSelStore();

  /**
   * Translated "Point Desc".
   * 
   * @return translated "Point Desc"
  
   */
  @DefaultMessage("Point Desc")
  @Key("pointDesc")
  String pointDesc();

  /**
   * Translated "Pop-out".
   * 
   * @return translated "Pop-out"
  
   */
  @DefaultMessage("Pop-out")
  @Key("popout")
  String popout();

  /**
   * Translated "Population".
   * 
   * @return translated "Population"
  
   */
  @DefaultMessage("Population")
  @Key("population")
  String population();

  /**
   * Translated "The value for position must not exceed the subset capacity".
   * 
   * @return translated "The value for position must not exceed the subset capacity"
  
   */
  @DefaultMessage("The value for position must not exceed the subset capacity")
  @Key("posExcSubsetCapacityException")
  String posExcSubsetCapacityException();

  /**
   * Translated "The value for position must not exceed the total capacity".
   * 
   * @return translated "The value for position must not exceed the total capacity"
  
   */
  @DefaultMessage("The value for position must not exceed the total capacity")
  @Key("posExcTotalCapacityException")
  String posExcTotalCapacityException();

  /**
   * Translated "Position must be greater than zero".
   * 
   * @return translated "Position must be greater than zero"
  
   */
  @DefaultMessage("Position must be greater than zero")
  @Key("posMoreThanZeroException")
  String posMoreThanZeroException();

  /**
   * Translated "Type must not be Duplicate if position is 1".
   * 
   * @return translated "Type must not be Duplicate if position is 1"
  
   */
  @DefaultMessage("Type must not be Duplicate if position is 1")
  @Key("posOneDuplicateException")
  String posOneDuplicateException();

  /**
   * Translated "Position must not be specifed if type is not Fixed or Duplicate".
   * 
   * @return translated "Position must not be specifed if type is not Fixed or Duplicate"
  
   */
  @DefaultMessage("Position must not be specifed if type is not Fixed or Duplicate")
  @Key("posSpecifiedException")
  String posSpecifiedException();

  /**
   * Translated "Position".
   * 
   * @return translated "Position"
  
   */
  @DefaultMessage("Position")
  @Key("position")
  String position();

  /**
   * Translated "Positive Test Count".
   * 
   * @return translated "Positive Test Count"
  
   */
  @DefaultMessage("Positive Test Count")
  @Key("positiveTestCount")
  String positiveTestCount();

  /**
   * Translated "Preference".
   * 
   * @return translated "Preference"
  
   */
  @DefaultMessage("Preference")
  @Key("preference")
  String preference();

  /**
   * Translated "Change your default settings including your current geographical location.".
   * 
   * @return translated "Change your default settings including your current geographical location."
  
   */
  @DefaultMessage("Change your default settings including your current geographical location.")
  @Key("preferenceDescription")
  String preferenceDescription();

  /**
   * Translated "Default Bar Code Printer".
   * 
   * @return translated "Default Bar Code Printer"
  
   */
  @DefaultMessage("Default Bar Code Printer")
  @Key("preferences.defaultBarCode")
  String preferences_defaultBarCode();

  /**
   * Translated "Default Printer".
   * 
   * @return translated "Default Printer"
  
   */
  @DefaultMessage("Default Printer")
  @Key("preferences.defaultPrinter")
  String preferences_defaultPrinter();

  /**
   * Translated "Preliminary".
   * 
   * @return translated "Preliminary"
  
   */
  @DefaultMessage("Preliminary")
  @Key("preliminary")
  String preliminary();

  /**
   * Translated "Prep Test & Reflex Test".
   * 
   * @return translated "Prep Test & Reflex Test"
  
   */
  @DefaultMessage("Prep Test & Reflex Test")
  @Key("prepAndReflex")
  String prepAndReflex();

  /**
   * Translated "Prep Test".
   * 
   * @return translated "Prep Test"
  
   */
  @DefaultMessage("Prep Test")
  @Key("prepTest")
  String prepTest();

  /**
   * Translated "Failed to remove test after the Prep Test Popup was cancelled".
   * 
   * @return translated "Failed to remove test after the Prep Test Popup was cancelled"
  
   */
  @DefaultMessage("Failed to remove test after the Prep Test Popup was cancelled")
  @Key("prepTestCancelledCleanupException")
  String prepTestCancelledCleanupException();

  /**
   * Translated "Prep Test & Method".
   * 
   * @return translated "Prep Test & Method"
  
   */
  @DefaultMessage("Prep Test & Method")
  @Key("prepTestMethod")
  String prepTestMethod();

  /**
   * Translated "Prep Test {0} must have a Section assigned".
   * 
   * @param arg0 "{0}"
   * @return translated "Prep Test {0} must have a Section assigned"
  
   */
  @DefaultMessage("Prep Test {0} must have a Section assigned")
  @Key("prepTestNeedsSection")
  String prepTestNeedsSection(String arg0);

  /**
   * Translated "Prep Test Selection".
   * 
   * @return translated "Prep Test Selection"
  
   */
  @DefaultMessage("Prep Test Selection")
  @Key("prepTestPicker")
  String prepTestPicker();

  /**
   * Translated "You must choose the appropriate prep test(s) before your analysis(es) may be added".
   * 
   * @return translated "You must choose the appropriate prep test(s) before your analysis(es) may be added"
  
   */
  @DefaultMessage("You must choose the appropriate prep test(s) before your analysis(es) may be added")
  @Key("prepTestRequiredException")
  String prepTestRequiredException();

  /**
   * Translated "Prep Test must be chosen for {0}".
   * 
   * @param arg0 "{0}"
   * @return translated "Prep Test must be chosen for {0}"
  
   */
  @DefaultMessage("Prep Test must be chosen for {0}")
  @Key("prepTestRequiredForTestException")
  String prepTestRequiredForTestException(String arg0);

  /**
   * Translated "Prepared By".
   * 
   * @return translated "Prepared By"
  
   */
  @DefaultMessage("Prepared By")
  @Key("preparedBy")
  String preparedBy();

  /**
   * Translated "Prepared Date".
   * 
   * @return translated "Prepared Date"
  
   */
  @DefaultMessage("Prepared Date")
  @Key("preparedDate")
  String preparedDate();

  /**
   * Translated "Prepared Unit".
   * 
   * @return translated "Prepared Unit"
  
   */
  @DefaultMessage("Prepared Unit")
  @Key("preparedUnit")
  String preparedUnit();

  /**
   * Translated "Prepared Volume".
   * 
   * @return translated "Prepared Volume"
  
   */
  @DefaultMessage("Prepared Volume")
  @Key("preparedVolume")
  String preparedVolume();

  /**
   * Translated "Preview Final Report".
   * 
   * @return translated "Preview Final Report"
  
   */
  @DefaultMessage("Preview Final Report")
  @Key("previewFinalReport")
  String previewFinalReport();

  /**
   * Translated "Previous".
   * 
   * @return translated "Previous"
  
   */
  @DefaultMessage("Previous")
  @Key("previous")
  String previous();

  /**
   * Translated "Print".
   * 
   * @return translated "Print"
  
   */
  @DefaultMessage("Print")
  @Key("print")
  String print();

  /**
   * Translated "Print Labels".
   * 
   * @return translated "Print Labels"
  
   */
  @DefaultMessage("Print Labels")
  @Key("printLabels")
  String printLabels();

  /**
   * Translated "Printed".
   * 
   * @return translated "Printed"
  
   */
  @DefaultMessage("Printed")
  @Key("printed")
  String printed();

  /**
   * Translated "Printed Date".
   * 
   * @return translated "Printed Date"
  
   */
  @DefaultMessage("Printed Date")
  @Key("printedDate")
  String printedDate();

  /**
   * Translated "Printer".
   * 
   * @return translated "Printer"
  
   */
  @DefaultMessage("Printer")
  @Key("printer")
  String printer();

  /**
   * Translated "Printer Type".
   * 
   * @return translated "Printer Type"
  
   */
  @DefaultMessage("Printer Type")
  @Key("printerType")
  String printerType();

  /**
   * Translated "Priority".
   * 
   * @return translated "Priority"
  
   */
  @DefaultMessage("Priority")
  @Key("priority")
  String priority();

  /**
   * Translated "Priority Profile".
   * 
   * @return translated "Priority Profile"
  
   */
  @DefaultMessage("Priority Profile")
  @Key("priorityProfile")
  String priorityProfile();

  /**
   * Translated "Private Well".
   * 
   * @return translated "Private Well"
  
   */
  @DefaultMessage("Private Well")
  @Key("privateWell")
  String privateWell();

  /**
   * Translated "Private Well Final Report".
   * 
   * @return translated "Private Well Final Report"
  
   */
  @DefaultMessage("Private Well Final Report")
  @Key("privateWellFinalReport")
  String privateWellFinalReport();

  /**
   * Translated "Private Well Info".
   * 
   * @return translated "Private Well Info"
  
   */
  @DefaultMessage("Private Well Info")
  @Key("privateWellInfo")
  String privateWellInfo();

  /**
   * Translated "Private Well Water Sample".
   * 
   * @return translated "Private Well Water Sample"
  
   */
  @DefaultMessage("Private Well Water Sample")
  @Key("privateWellWaterSample")
  String privateWellWaterSample();

  /**
   * Translated "Private Well Water Sample Login".
   * 
   * @return translated "Private Well Water Sample Login"
  
   */
  @DefaultMessage("Private Well Water Sample Login")
  @Key("privateWellWaterSampleLogin")
  String privateWellWaterSampleLogin();

  /**
   * Translated "Fully login private well water sample and analysis related information.".
   * 
   * @return translated "Fully login private well water sample and analysis related information."
  
   */
  @DefaultMessage("Fully login private well water sample and analysis related information.")
  @Key("privateWellWaterSampleLoginDescription")
  String privateWellWaterSampleLoginDescription();

  /**
   * Translated "Process".
   * 
   * @return translated "Process"
  
   */
  @DefaultMessage("Process")
  @Key("process")
  String process();

  /**
   * Translated "Process Shipping".
   * 
   * @return translated "Process Shipping"
  
   */
  @DefaultMessage("Process Shipping")
  @Key("processShipping")
  String processShipping();

  /**
   * Translated "Processed By".
   * 
   * @return translated "Processed By"
  
   */
  @DefaultMessage("Processed By")
  @Key("processedBy")
  String processedBy();

  /**
   * Translated "Processed Date".
   * 
   * @return translated "Processed Date"
  
   */
  @DefaultMessage("Processed Date")
  @Key("processedDate")
  String processedDate();

  /**
   * Translated "Product URI".
   * 
   * @return translated "Product URI"
  
   */
  @DefaultMessage("Product URI")
  @Key("productURI")
  String productURI();

  /**
   * Translated "Profile".
   * 
   * @return translated "Profile"
  
   */
  @DefaultMessage("Profile")
  @Key("profile")
  String profile();

  /**
   * Translated "Profile Version".
   * 
   * @return translated "Profile Version"
  
   */
  @DefaultMessage("Profile Version")
  @Key("profileVersion")
  String profileVersion();

  /**
   * Translated "Project".
   * 
   * @return translated "Project"
  
   */
  @DefaultMessage("Project")
  @Key("project")
  String project();

  /**
   * Translated "Project".
   * 
   * @return translated "Project"
  
   */
  @DefaultMessage("Project")
  @Key("project.project")
  String project_project();

  /**
   * Translated "There is already an active project in the system with the same name ".
   * 
   * @return translated "There is already an active project in the system with the same name "
  
   */
  @DefaultMessage("There is already an active project in the system with the same name ")
  @Key("projectActiveException")
  String projectActiveException();

  /**
   * Translated "Create and manage projects that can be associated with samples.".
   * 
   * @return translated "Create and manage projects that can be associated with samples."
  
   */
  @DefaultMessage("Create and manage projects that can be associated with samples.")
  @Key("projectDescription")
  String projectDescription();

  /**
   * Translated "History - Project".
   * 
   * @return translated "History - Project"
  
   */
  @DefaultMessage("History - Project")
  @Key("projectHistory")
  String projectHistory();

  /**
   * Translated "History - Project Parameter".
   * 
   * @return translated "History - Project Parameter"
  
   */
  @DefaultMessage("History - Project Parameter")
  @Key("projectParameterHistory")
  String projectParameterHistory();

  /**
   * Translated "This project has an overlapping begin date or end date with another project which has the same name as this.".
   * 
   * @return translated "This project has an overlapping begin date or end date with another project which has the same name as this."
  
   */
  @DefaultMessage("This project has an overlapping begin date or end date with another project which has the same name as this.")
  @Key("projectTimeOverlapException")
  String projectTimeOverlapException();

  /**
   * Translated "Provider/Organization Info".
   * 
   * @return translated "Provider/Organization Info"
  
   */
  @DefaultMessage("Provider/Organization Info")
  @Key("provOrgInfo")
  String provOrgInfo();

  /**
   * Translated "Provider".
   * 
   * @return translated "Provider"
  
   */
  @DefaultMessage("Provider")
  @Key("provider")
  String provider();

  /**
   * Translated "External Id".
   * 
   * @return translated "External Id"
  
   */
  @DefaultMessage("External Id")
  @Key("provider.externalId")
  String provider_externalId();

  /**
   * Translated "Provider (L,F)".
   * 
   * @return translated "Provider (L,F)"
  
   */
  @DefaultMessage("Provider (L,F)")
  @Key("provider.lf")
  String provider_lf();

  /**
   * Translated "NPI".
   * 
   * @return translated "NPI"
  
   */
  @DefaultMessage("NPI")
  @Key("provider.npi")
  String provider_npi();

  /**
   * Translated "Provider".
   * 
   * @return translated "Provider"
  
   */
  @DefaultMessage("Provider")
  @Key("provider.provider")
  String provider_provider();

  /**
   * Translated "Enter health care professionals and their contact information.".
   * 
   * @return translated "Enter health care professionals and their contact information."
  
   */
  @DefaultMessage("Enter health care professionals and their contact information.")
  @Key("providerDescription")
  String providerDescription();

  /**
   * Translated "History - Provider".
   * 
   * @return translated "History - Provider"
  
   */
  @DefaultMessage("History - Provider")
  @Key("providerHistory")
  String providerHistory();

  /**
   * Translated "History - Provider Location".
   * 
   * @return translated "History - Provider Location"
  
   */
  @DefaultMessage("History - Provider Location")
  @Key("providerLocationHistory")
  String providerLocationHistory();

  /**
   * Translated "Providers".
   * 
   * @return translated "Providers"
  
   */
  @DefaultMessage("Providers")
  @Key("providers")
  String providers();

  /**
   * Translated "PT Sample".
   * 
   * @return translated "PT Sample"
  
   */
  @DefaultMessage("PT Sample")
  @Key("ptSample")
  String ptSample();

  /**
   * Translated "PT Sample Login".
   * 
   * @return translated "PT Sample Login"
  
   */
  @DefaultMessage("PT Sample Login")
  @Key("ptSampleLogin")
  String ptSampleLogin();

  /**
   * Translated "Fully login proficiency testing sample and analysis related information.".
   * 
   * @return translated "Fully login proficiency testing sample and analysis related information."
  
   */
  @DefaultMessage("Fully login proficiency testing sample and analysis related information.")
  @Key("ptSampleLoginDescription")
  String ptSampleLoginDescription();

  /**
   * Translated "Patient Info".
   * 
   * @return translated "Patient Info"
  
   */
  @DefaultMessage("Patient Info")
  @Key("ptntInfo")
  String ptntInfo();

  /**
   * Translated "PWS ID".
   * 
   * @return translated "PWS ID"
  
   */
  @DefaultMessage("PWS ID")
  @Key("pws.id")
  String pws_id();

  /**
   * Translated "PWS ID".
   * 
   * @return translated "PWS ID"
  
   */
  @DefaultMessage("PWS ID")
  @Key("pwsId")
  String pwsId();

  /**
   * Translated "Pws ID is required".
   * 
   * @return translated "Pws ID is required"
  
   */
  @DefaultMessage("Pws ID is required")
  @Key("pwsIdRequiredException")
  String pwsIdRequiredException();

  /**
   * Translated "PWS Information".
   * 
   * @return translated "PWS Information"
  
   */
  @DefaultMessage("PWS Information")
  @Key("pwsInformation")
  String pwsInformation();

  /**
   * Translated "Public Water Supply Information used for SDWIS samples.".
   * 
   * @return translated "Public Water Supply Information used for SDWIS samples."
  
   */
  @DefaultMessage("Public Water Supply Information used for SDWIS samples.")
  @Key("pwsInformationDescription")
  String pwsInformationDescription();

  /**
   * Translated "PWS Name".
   * 
   * @return translated "PWS Name"
  
   */
  @DefaultMessage("PWS Name")
  @Key("pwsName")
  String pwsName();

  /**
   * Translated "Violation".
   * 
   * @return translated "Violation"
  
   */
  @DefaultMessage("Violation")
  @Key("pwsViolation")
  String pwsViolation();

  /**
   * Translated "Violation Date".
   * 
   * @return translated "Violation Date"
  
   */
  @DefaultMessage("Violation Date")
  @Key("pwsViolationDate")
  String pwsViolationDate();

  /**
   * Translated "Sample ID".
   * 
   * @return translated "Sample ID"
  
   */
  @DefaultMessage("Sample ID")
  @Key("pwsViolationSampleId")
  String pwsViolationSampleId();

  /**
   * Translated "QA Events".
   * 
   * @return translated "QA Events"
  
   */
  @DefaultMessage("QA Events")
  @Key("qaEvent.qaEvents")
  String qaEvent_qaEvents();

  /**
   * Translated "Are you sure you want to change this?".
   * 
   * @return translated "Are you sure you want to change this?"
  
   */
  @DefaultMessage("Are you sure you want to change this?")
  @Key("qaEventEditConfirm")
  String qaEventEditConfirm();

  /**
   * Translated "QA Event Selection".
   * 
   * @return translated "QA Event Selection"
  
   */
  @DefaultMessage("QA Event Selection")
  @Key("qaEventSelection")
  String qaEventSelection();

  /**
   * Translated "QA Events".
   * 
   * @return translated "QA Events"
  
   */
  @DefaultMessage("QA Events")
  @Key("qaEvents")
  String qaEvents();

  /**
   * Translated "QA Events for Sample & Test".
   * 
   * @return translated "QA Events for Sample & Test"
  
   */
  @DefaultMessage("QA Events for Sample & Test")
  @Key("qaLookUp")
  String qaLookUp();

  /**
   * Translated "History - QA Event".
   * 
   * @return translated "History - QA Event"
  
   */
  @DefaultMessage("History - QA Event")
  @Key("qaeventHistory")
  String qaeventHistory();

  /**
   * Translated "The combination of the names of the Qa Event and Test must be unique ".
   * 
   * @return translated "The combination of the names of the Qa Event and Test must be unique "
  
   */
  @DefaultMessage("The combination of the names of the Qa Event and Test must be unique ")
  @Key("qaeventTestComboUnique")
  String qaeventTestComboUnique();

  /**
   * Translated "Expire Date".
   * 
   * @return translated "Expire Date"
  
   */
  @DefaultMessage("Expire Date")
  @Key("qc.expireDate")
  String qc_expireDate();

  /**
   * Translated "Get Data".
   * 
   * @return translated "Get Data"
  
   */
  @DefaultMessage("Get Data")
  @Key("qc.getData")
  String qc_getData();

  /**
   * Translated "Invalid value for selected type".
   * 
   * @return translated "Invalid value for selected type"
  
   */
  @DefaultMessage("Invalid value for selected type")
  @Key("qc.invalidValueException")
  String qc_invalidValueException();

  /**
   * Translated "Lot Number ".
   * 
   * @return translated "Lot Number "
  
   */
  @DefaultMessage("Lot Number ")
  @Key("qc.lotNumber")
  String qc_lotNumber();

  /**
   * Translated "You must first select a table row before clicking add".
   * 
   * @return translated "You must first select a table row before clicking add"
  
   */
  @DefaultMessage("You must first select a table row before clicking add")
  @Key("qc.noSelectedRow")
  String qc_noSelectedRow();

  /**
   * Translated "Prepared By".
   * 
   * @return translated "Prepared By"
  
   */
  @DefaultMessage("Prepared By")
  @Key("qc.preparedBy")
  String qc_preparedBy();

  /**
   * Translated "Prepared Date".
   * 
   * @return translated "Prepared Date"
  
   */
  @DefaultMessage("Prepared Date")
  @Key("qc.preparedDate")
  String qc_preparedDate();

  /**
   * Translated "Prepared Unit".
   * 
   * @return translated "Prepared Unit"
  
   */
  @DefaultMessage("Prepared Unit")
  @Key("qc.preparedUnit")
  String qc_preparedUnit();

  /**
   * Translated "Prepared Volume".
   * 
   * @return translated "Prepared Volume"
  
   */
  @DefaultMessage("Prepared Volume")
  @Key("qc.preparedVolume")
  String qc_preparedVolume();

  /**
   * Translated "QC Analyte".
   * 
   * @return translated "QC Analyte"
  
   */
  @DefaultMessage("QC Analyte")
  @Key("qc.qcAnalyte")
  String qc_qcAnalyte();

  /**
   * Translated "QC Lookup".
   * 
   * @return translated "QC Lookup"
  
   */
  @DefaultMessage("QC Lookup")
  @Key("qc.qcLookup")
  String qc_qcLookup();

  /**
   * Translated "QC Name".
   * 
   * @return translated "QC Name"
  
   */
  @DefaultMessage("QC Name")
  @Key("qc.qcName")
  String qc_qcName();

  /**
   * Translated "Usable Date".
   * 
   * @return translated "Usable Date"
  
   */
  @DefaultMessage("Usable Date")
  @Key("qc.usableDate")
  String qc_usableDate();

  /**
   * Translated "History - QC Analyte".
   * 
   * @return translated "History - QC Analyte"
  
   */
  @DefaultMessage("History - QC Analyte")
  @Key("qcAnalyteHistory")
  String qcAnalyteHistory();

  /**
   * Translated "QC Chart".
   * 
   * @return translated "QC Chart"
  
   */
  @DefaultMessage("QC Chart")
  @Key("qcChart")
  String qcChart();

  /**
   * Translated "QC Report".
   * 
   * @return translated "QC Report"
  
   */
  @DefaultMessage("QC Report")
  @Key("qcChartReport")
  String qcChartReport();

  /**
   * Translated "History - QC".
   * 
   * @return translated "History - QC"
  
   */
  @DefaultMessage("History - QC")
  @Key("qcHistory")
  String qcHistory();

  /**
   * Translated "QC Items".
   * 
   * @return translated "QC Items"
  
   */
  @DefaultMessage("QC Items")
  @Key("qcItems")
  String qcItems();

  /**
   * Translated "QC Link".
   * 
   * @return translated "QC Link"
  
   */
  @DefaultMessage("QC Link")
  @Key("qcLink")
  String qcLink();

  /**
   * Translated "One or more lots cannot be deleted, other entries are linked to them".
   * 
   * @return translated "One or more lots cannot be deleted, other entries are linked to them"
  
   */
  @DefaultMessage("One or more lots cannot be deleted, other entries are linked to them")
  @Key("qcLotDeleteException")
  String qcLotDeleteException();

  /**
   * Translated "History - QC Lot".
   * 
   * @return translated "History - QC Lot"
  
   */
  @DefaultMessage("History - QC Lot")
  @Key("qcLotHistory")
  String qcLotHistory();

  /**
   * Translated "QC Name".
   * 
   * @return translated "QC Name"
  
   */
  @DefaultMessage("QC Name")
  @Key("qcName")
  String qcName();

  /**
   * Translated "Qty".
   * 
   * @return translated "Qty"
  
   */
  @DefaultMessage("Qty")
  @Key("qty")
  String qty();

  /**
   * Translated "The quantity can be set as zero, but the item cannot be removed".
   * 
   * @return translated "The quantity can be set as zero, but the item cannot be removed"
  
   */
  @DefaultMessage("The quantity can be set as zero, but the item cannot be removed")
  @Key("qtyAdjustedItemNotRemoved")
  String qtyAdjustedItemNotRemoved();

  /**
   * Translated "Quantity more than quantity on hand".
   * 
   * @return translated "Quantity more than quantity on hand"
  
   */
  @DefaultMessage("Quantity more than quantity on hand")
  @Key("qtyMoreThanQtyOnhandException")
  String qtyMoreThanQtyOnhandException();

  /**
   * Translated "Quantity more than quantity ordered".
   * 
   * @return translated "Quantity more than quantity ordered"
  
   */
  @DefaultMessage("Quantity more than quantity ordered")
  @Key("qtyMoreThanQtyOrderedException")
  String qtyMoreThanQtyOrderedException();

  /**
   * Translated "This value must not exceed {0}".
   * 
   * @param arg0 "{0}"
   * @return translated "This value must not exceed {0}"
  
   */
  @DefaultMessage("This value must not exceed {0}")
  @Key("qtyNotMoreThanMaxException")
  String qtyNotMoreThanMaxException(String arg0);

  /**
   * Translated "Qty On Hand".
   * 
   * @return translated "Qty On Hand"
  
   */
  @DefaultMessage("Qty On Hand")
  @Key("qtyOnHand")
  String qtyOnHand();

  /**
   * Translated "Quantity on hand cannot be less than quantity received ".
   * 
   * @return translated "Quantity on hand cannot be less than quantity received "
  
   */
  @DefaultMessage("Quantity on hand cannot be less than quantity received ")
  @Key("qtyOnHandLessThanQtyRecException")
  String qtyOnHandLessThanQtyRecException();

  /**
   * Translated "Quantity on hand is not sufficient for # Requested".
   * 
   * @return translated "Quantity on hand is not sufficient for # Requested"
  
   */
  @DefaultMessage("Quantity on hand is not sufficient for # Requested")
  @Key("qtyOnHandNotSufficientException")
  String qtyOnHandNotSufficientException();

  /**
   * Translated "Quantity received must be more than zero ".
   * 
   * @return translated "Quantity received must be more than zero "
  
   */
  @DefaultMessage("Quantity received must be more than zero ")
  @Key("qtyRecMoreThanZeroException")
  String qtyRecMoreThanZeroException();

  /**
   * Translated "Invalid quantity, please check parent ratio".
   * 
   * @return translated "Invalid quantity, please check parent ratio"
  
   */
  @DefaultMessage("Invalid quantity, please check parent ratio")
  @Key("qtyToParentRatioInvalid")
  String qtyToParentRatioInvalid();

  /**
   * Translated "Quant Limit".
   * 
   * @return translated "Quant Limit"
  
   */
  @DefaultMessage("Quant Limit")
  @Key("quantLimit")
  String quantLimit();

  /**
   * Translated "Quantity".
   * 
   * @return translated "Quantity"
  
   */
  @DefaultMessage("Quantity")
  @Key("quantity")
  String quantity();

  /**
   * Translated "Quantity On-hand".
   * 
   * @return translated "Quantity On-hand"
  
   */
  @DefaultMessage("Quantity On-hand")
  @Key("quantityOnHand")
  String quantityOnHand();

  /**
   * Translated "Qty Requested".
   * 
   * @return translated "Qty Requested"
  
   */
  @DefaultMessage("Qty Requested")
  @Key("quantityRequested")
  String quantityRequested();

  /**
   * Translated "Query".
   * 
   * @return translated "Query"
  
   */
  @DefaultMessage("Query")
  @Key("query")
  String query();

  /**
   * Translated "Query aborted".
   * 
   * @return translated "Query aborted"
  
   */
  @DefaultMessage("Query aborted")
  @Key("queryAborted")
  String queryAborted();

  /**
   * Translated "Please query for only one domain at a time.".
   * 
   * @return translated "Please query for only one domain at a time."
  
   */
  @DefaultMessage("Please query for only one domain at a time.")
  @Key("queryDomainException")
  String queryDomainException();

  /**
   * Translated "A query must be executed before updating records".
   * 
   * @return translated "A query must be executed before updating records"
  
   */
  @DefaultMessage("A query must be executed before updating records")
  @Key("queryExeBeforeUpdate")
  String queryExeBeforeUpdate();

  /**
   * Translated "Your query timed out, please try again.".
   * 
   * @return translated "Your query timed out, please try again."
  
   */
  @DefaultMessage("Your query timed out, please try again.")
  @Key("queryExpiredException")
  String queryExpiredException();

  /**
   * Translated "Query failed".
   * 
   * @return translated "Query failed"
  
   */
  @DefaultMessage("Query failed")
  @Key("queryFailed")
  String queryFailed();

  /**
   * Translated "Query Results".
   * 
   * @return translated "Query Results"
  
   */
  @DefaultMessage("Query Results")
  @Key("queryResults")
  String queryResults();

  /**
   * Translated "Querying....".
   * 
   * @return translated "Querying...."
  
   */
  @DefaultMessage("Querying....")
  @Key("querying")
  String querying();

  /**
   * Translated "Querying...Complete".
   * 
   * @return translated "Querying...Complete"
  
   */
  @DefaultMessage("Querying...Complete")
  @Key("queryingComplete")
  String queryingComplete();

  /**
   * Translated "There were errors with your commit".
   * 
   * @return translated "There were errors with your commit"
  
   */
  @DefaultMessage("There were errors with your commit")
  @Key("quickCommitError")
  String quickCommitError();

  /**
   * Translated "Quick Entry".
   * 
   * @return translated "Quick Entry"
  
   */
  @DefaultMessage("Quick Entry")
  @Key("quickEntry")
  String quickEntry();

  /**
   * Translated "Quickly login samples with minimal information to allow testing to begin.".
   * 
   * @return translated "Quickly login samples with minimal information to allow testing to begin."
  
   */
  @DefaultMessage("Quickly login samples with minimal information to allow testing to begin.")
  @Key("quickEntryDescription")
  String quickEntryDescription();

  /**
   * Translated "A Quick Entry sample exists for the specified accession number".
   * 
   * @return translated "A Quick Entry sample exists for the specified accession number"
  
   */
  @DefaultMessage("A Quick Entry sample exists for the specified accession number")
  @Key("quickEntryNumberExists")
  String quickEntryNumberExists();

  /**
   * Translated "Race".
   * 
   * @return translated "Race"
  
   */
  @DefaultMessage("Race")
  @Key("race")
  String race();

  /**
   * Translated "Range Type".
   * 
   * @return translated "Range Type"
  
   */
  @DefaultMessage("Range Type")
  @Key("rangeType")
  String rangeType();

  /**
   * Translated "Recompute".
   * 
   * @return translated "Recompute"
  
   */
  @DefaultMessage("Recompute")
  @Key("reCompute")
  String reCompute();

  /**
   * Translated "Reason".
   * 
   * @return translated "Reason"
  
   */
  @DefaultMessage("Reason")
  @Key("reason")
  String reason();

  /**
   * Translated "Rec-Compl".
   * 
   * @return translated "Rec-Compl"
  
   */
  @DefaultMessage("Rec-Compl")
  @Key("rec-cmp")
  String rec_cmp();

  /**
   * Translated "Rec-Rdy".
   * 
   * @return translated "Rec-Rdy"
  
   */
  @DefaultMessage("Rec-Rdy")
  @Key("rec-rdy")
  String rec_rdy();

  /**
   * Translated "Rec-Rel".
   * 
   * @return translated "Rec-Rel"
  
   */
  @DefaultMessage("Rec-Rel")
  @Key("rec-rel")
  String rec_rel();

  /**
   * Translated "Receipt ".
   * 
   * @return translated "Receipt "
  
   */
  @DefaultMessage("Receipt ")
  @Key("receipt")
  String receipt();

  /**
   * Translated "Received".
   * 
   * @return translated "Received"
  
   */
  @DefaultMessage("Received")
  @Key("received")
  String received();

  /**
   * Translated "Received By must have a value".
   * 
   * @return translated "Received By must have a value"
  
   */
  @DefaultMessage("Received By must have a value")
  @Key("receivedByNoValueException")
  String receivedByNoValueException();

  /**
   * Translated "Received Date".
   * 
   * @return translated "Received Date"
  
   */
  @DefaultMessage("Received Date")
  @Key("receivedDate")
  String receivedDate();

  /**
   * Translated "Received date must have a value".
   * 
   * @return translated "Received date must have a value"
  
   */
  @DefaultMessage("Received date must have a value")
  @Key("receivedDateNoValueException")
  String receivedDateNoValueException();

  /**
   * Translated "Received date is NOT today.  Is ''{0}'' the correct date to use?".
   * 
   * @param arg0 "{0}"
   * @return translated "Received date is NOT today.  Is ''{0}'' the correct date to use?"
  
   */
  @DefaultMessage("Received date is NOT today.  Is ''{0}'' the correct date to use?")
  @Key("receivedDateNotTodayExceptionBody")
  String receivedDateNotTodayExceptionBody(String arg0);

  /**
   * Translated "Received date is not today".
   * 
   * @return translated "Received date is not today"
  
   */
  @DefaultMessage("Received date is not today")
  @Key("receivedDateNotTodayExceptionTitle")
  String receivedDateNotTodayExceptionTitle();

  /**
   * Translated "Received date required".
   * 
   * @return translated "Received date required"
  
   */
  @DefaultMessage("Received date required")
  @Key("receivedDateRequiredException")
  String receivedDateRequiredException();

  /**
   * Translated "Received date shouldn''t be more than 180 days before entered date".
   * 
   * @return translated "Received date shouldn''t be more than 180 days before entered date"
  
   */
  @DefaultMessage("Received date shouldn''t be more than 180 days before entered date")
  @Key("receivedTooOldWarning")
  String receivedTooOldWarning();

  /**
   * Translated "This record must have at least one active analyte   ".
   * 
   * @return translated "This record must have at least one active analyte   "
  
   */
  @DefaultMessage("This record must have at least one active analyte   ")
  @Key("recordHasNoActiveAnalytesException")
  String recordHasNoActiveAnalytesException();

  /**
   * Translated "This record must have at least one analyte".
   * 
   * @return translated "This record must have at least one analyte"
  
   */
  @DefaultMessage("This record must have at least one analyte")
  @Key("recordHasNoAnalytesException")
  String recordHasNoAnalytesException();

  /**
   * Translated "This record is not available at this time for you to add/edit/remove. Please try at another time. (Locked)".
   * 
   * @return translated "This record is not available at this time for you to add/edit/remove. Please try at another time. (Locked)"
  
   */
  @DefaultMessage("This record is not available at this time for you to add/edit/remove. Please try at another time. (Locked)")
  @Key("recordNotAvailableLockException")
  String recordNotAvailableLockException();

  /**
   * Translated "Recur".
   * 
   * @return translated "Recur"
  
   */
  @DefaultMessage("Recur")
  @Key("recur")
  String recur();

  /**
   * Translated "Recurred Orders".
   * 
   * @return translated "Recurred Orders"
  
   */
  @DefaultMessage("Recurred Orders")
  @Key("recurredOrders")
  String recurredOrders();

  /**
   * Translated "Reference".
   * 
   * @return translated "Reference"
  
   */
  @DefaultMessage("Reference")
  @Key("reference")
  String reference();

  /**
   * Translated "Reference To".
   * 
   * @return translated "Reference To"
  
   */
  @DefaultMessage("Reference To")
  @Key("referenceTo")
  String referenceTo();

  /**
   * Translated "Reflex Test & Method".
   * 
   * @return translated "Reflex Test & Method"
  
   */
  @DefaultMessage("Reflex Test & Method")
  @Key("reflexTestMethod")
  String reflexTestMethod();

  /**
   * Translated "Reflex Test {0} must have a Section assigned".
   * 
   * @param arg0 "{0}"
   * @return translated "Reflex Test {0} must have a Section assigned"
  
   */
  @DefaultMessage("Reflex Test {0} must have a Section assigned")
  @Key("reflexTestNeedsSection")
  String reflexTestNeedsSection(String arg0);

  /**
   * Translated "Reflex Test Selection".
   * 
   * @return translated "Reflex Test Selection"
  
   */
  @DefaultMessage("Reflex Test Selection")
  @Key("reflexTestPicker")
  String reflexTestPicker();

  /**
   * Translated "Reflex Test".
   * 
   * @return translated "Reflex Test"
  
   */
  @DefaultMessage("Reflex Test")
  @Key("reflexiveTest")
  String reflexiveTest();

  /**
   * Translated "Refresh".
   * 
   * @return translated "Refresh"
  
   */
  @DefaultMessage("Refresh")
  @Key("refresh")
  String refresh();

  /**
   * Translated "Related Entry".
   * 
   * @return translated "Related Entry"
  
   */
  @DefaultMessage("Related Entry")
  @Key("relEntry")
  String relEntry();

  /**
   * Translated "Related Worksheet #".
   * 
   * @return translated "Related Worksheet #"
  
   */
  @DefaultMessage("Related Worksheet #")
  @Key("relatedWorksheetNumber")
  String relatedWorksheetNumber();

  /**
   * Translated "Release".
   * 
   * @return translated "Release"
  
   */
  @DefaultMessage("Release")
  @Key("release")
  String release();

  /**
   * Translated "You''re about to release {0} records.\n\nPress Ok to continue or Cancel to abort.".
   * 
   * @param arg0 "{0}"
   * @return translated "You''re about to release {0} records.\n\nPress Ok to continue or Cancel to abort."
  
   */
  @DefaultMessage("You''re about to release {0} records.\n\nPress Ok to continue or Cancel to abort.")
  @Key("releaseMultipleWarning")
  String releaseMultipleWarning(String arg0);

  /**
   * Translated "Released".
   * 
   * @return translated "Released"
  
   */
  @DefaultMessage("Released")
  @Key("released")
  String released();

  /**
   * Translated "Released By".
   * 
   * @return translated "Released By"
  
   */
  @DefaultMessage("Released By")
  @Key("releasedBy")
  String releasedBy();

  /**
   * Translated "Released Date".
   * 
   * @return translated "Released Date"
  
   */
  @DefaultMessage("Released Date")
  @Key("releasedDate")
  String releasedDate();

  /**
   * Translated "Reload".
   * 
   * @return translated "Reload"
  
   */
  @DefaultMessage("Reload")
  @Key("reload")
  String reload();

  /**
   * Translated "- Analyte".
   * 
   * @return translated "- Analyte"
  
   */
  @DefaultMessage("- Analyte")
  @Key("removeAnalyte")
  String removeAnalyte();

  /**
   * Translated "This will remove all rows with the same group as this row.\n\nPress Ok to continue.".
   * 
   * @return translated "This will remove all rows with the same group as this row.\n\nPress Ok to continue."
  
   */
  @DefaultMessage("This will remove all rows with the same group as this row.\n\nPress Ok to continue.")
  @Key("removeAuxMessage")
  String removeAuxMessage();

  /**
   * Translated "- Column".
   * 
   * @return translated "- Column"
  
   */
  @DefaultMessage("- Column")
  @Key("removeColumn")
  String removeColumn();

  /**
   * Translated "Please remove the empty rows from the table for Report To".
   * 
   * @return translated "Please remove the empty rows from the table for Report To"
  
   */
  @DefaultMessage("Please remove the empty rows from the table for Report To")
  @Key("removeEmptyReportToRows")
  String removeEmptyReportToRows();

  /**
   * Translated "- Header".
   * 
   * @return translated "- Header"
  
   */
  @DefaultMessage("- Header")
  @Key("removeHeader")
  String removeHeader();

  /**
   * Translated "Remove Row".
   * 
   * @return translated "Remove Row"
  
   */
  @DefaultMessage("Remove Row")
  @Key("removeRow")
  String removeRow();

  /**
   * Translated "- Row".
   * 
   * @return translated "- Row"
  
   */
  @DefaultMessage("- Row")
  @Key("removeRowTest")
  String removeRowTest();

  /**
   * Translated "Reorder Level".
   * 
   * @return translated "Reorder Level"
  
   */
  @DefaultMessage("Reorder Level")
  @Key("reorderLevel")
  String reorderLevel();

  /**
   * Translated "Repeat".
   * 
   * @return translated "Repeat"
  
   */
  @DefaultMessage("Repeat")
  @Key("repeat")
  String repeat();

  /**
   * Translated "This value is required and it must be at least 1".
   * 
   * @return translated "This value is required and it must be at least 1"
  
   */
  @DefaultMessage("This value is required and it must be at least 1")
  @Key("repeatNullForAnalyteException")
  String repeatNullForAnalyteException();

  /**
   * Translated "3. Repeat the above process for every shipping record.".
   * 
   * @return translated "3. Repeat the above process for every shipping record."
  
   */
  @DefaultMessage("3. Repeat the above process for every shipping record.")
  @Key("repeatProcess")
  String repeatProcess();

  /**
   * Translated "2. Repeat the above process for every sample record.".
   * 
   * @return translated "2. Repeat the above process for every sample record."
  
   */
  @DefaultMessage("2. Repeat the above process for every sample record.")
  @Key("repeatVerifyProcess")
  String repeatVerifyProcess();

  /**
   * Translated "Replace the current list of samples with the one generated on this date? ".
   * 
   * @return translated "Replace the current list of samples with the one generated on this date? "
  
   */
  @DefaultMessage("Replace the current list of samples with the one generated on this date? ")
  @Key("replaceCurrentSampleList")
  String replaceCurrentSampleList();

  /**
   * Translated "Report".
   * 
   * @return translated "Report"
  
   */
  @DefaultMessage("Report")
  @Key("report")
  String report();

  /**
   * Translated "Data Export".
   * 
   * @return translated "Data Export"
  
   */
  @DefaultMessage("Data Export")
  @Key("report.dataExport")
  String report_dataExport();

  /**
   * Translated "Export".
   * 
   * @return translated "Export"
  
   */
  @DefaultMessage("Export")
  @Key("report.export")
  String report_export();

  /**
   * Translated "Generating Data View...".
   * 
   * @return translated "Generating Data View..."
  
   */
  @DefaultMessage("Generating Data View...")
  @Key("report.genDataView")
  String report_genDataView();

  /**
   * Translated "Outputing report".
   * 
   * @return translated "Outputing report"
  
   */
  @DefaultMessage("Outputing report")
  @Key("report.outputReport")
  String report_outputReport();

  /**
   * Translated "Private Well Attachment".
   * 
   * @return translated "Private Well Attachment"
  
   */
  @DefaultMessage("Private Well Attachment")
  @Key("report.privateWellAttachment")
  String report_privateWellAttachment();

  /**
   * Translated "Reset".
   * 
   * @return translated "Reset"
  
   */
  @DefaultMessage("Reset")
  @Key("report.reset")
  String report_reset();

  /**
   * Translated "Run Report".
   * 
   * @return translated "Run Report"
  
   */
  @DefaultMessage("Run Report")
  @Key("report.runReport")
  String report_runReport();

  /**
   * Translated "Tube Labels ".
   * 
   * @return translated "Tube Labels "
   */
  @DefaultMessage("Tube Labels ")
  @Key("report.tubeLabels")
  String report_tubeLabels();

  /**
   * Translated "Report Description".
   * 
   * @return translated "Report Description"
  
   */
  @DefaultMessage("Report Description")
  @Key("reportDescription")
  String reportDescription();

  /**
   * Translated "Report To".
   * 
   * @return translated "Report To"
  
   */
  @DefaultMessage("Report To")
  @Key("reportTo")
  String reportTo();

  /**
   * Translated "Report/Bill To".
   * 
   * @return translated "Report/Bill To"
  
   */
  @DefaultMessage("Report/Bill To")
  @Key("reportToBillTo")
  String reportToBillTo();

  /**
   * Translated "This sample must have a report to".
   * 
   * @return translated "This sample must have a report to"
  
   */
  @DefaultMessage("This sample must have a report to")
  @Key("reportToMissingWarning")
  String reportToMissingWarning();

  /**
   * Translated "Reportable".
   * 
   * @return translated "Reportable"
  
   */
  @DefaultMessage("Reportable")
  @Key("reportable")
  String reportable();

  /**
   * Translated "Reporting".
   * 
   * @return translated "Reporting"
  
   */
  @DefaultMessage("Reporting")
  @Key("reporting")
  String reporting();

  /**
   * Translated "Report Method".
   * 
   * @return translated "Report Method"
  
   */
  @DefaultMessage("Report Method")
  @Key("reportingMethod")
  String reportingMethod();

  /**
   * Translated "Report Sequence".
   * 
   * @return translated "Report Sequence"
  
   */
  @DefaultMessage("Report Sequence")
  @Key("reportingSequence")
  String reportingSequence();

  /**
   * Translated "Requested By".
   * 
   * @return translated "Requested By"
  
   */
  @DefaultMessage("Requested By")
  @Key("requestedBy")
  String requestedBy();

  /**
   * Translated "Required".
   * 
   * @return translated "Required"
  
   */
  @DefaultMessage("Required")
  @Key("required")
  String required();

  /**
   * Translated "Please fill either From and To Dates or Number of Instances field.".
   * 
   * @return translated "Please fill either From and To Dates or Number of Instances field."
  
   */
  @DefaultMessage("Please fill either From and To Dates or Number of Instances field.")
  @Key("requiredEitherFields")
  String requiredEitherFields();

  /**
   * Translated "Result is required".
   * 
   * @return translated "Result is required"
  
   */
  @DefaultMessage("Result is required")
  @Key("requiredResultException")
  String requiredResultException();

  /**
   * Translated "Reset".
   * 
   * @return translated "Reset"
  
   */
  @DefaultMessage("Reset")
  @Key("reset")
  String reset();

  /**
   * Translated "Result".
   * 
   * @return translated "Result"
  
   */
  @DefaultMessage("Result")
  @Key("result")
  String result();

  /**
   * Translated "Accession # {0,number,#0}: {1}, {2} - Analytes with these external ids were not found: ''{3}''".
   * 
   * @param arg0 "{0,number,#0}"
   * @param arg1 "{1}"
   * @param arg2 "{2}"
   * @param arg3 "{3}"
   * @return translated "Accession # {0,number,#0}: {1}, {2} - Analytes with these external ids were not found: ''{3}''"
  
   */
  @DefaultMessage("Accession # {0,number,#0}: {1}, {2} - Analytes with these external ids were not found: ''{3}''")
  @Key("result.analytesNotFoundException")
  String result_analytesNotFoundException(Integer arg0,  String arg1,  String arg2,  String arg3);

  /**
   * Translated "A row group must have at least one result row".
   * 
   * @return translated "A row group must have at least one result row"
  
   */
  @DefaultMessage("A row group must have at least one result row")
  @Key("result.atleastOneResultInRowGroup")
  String result_atleastOneResultInRowGroup();

  /**
   * Translated "There must be at least one row for this analyte because it is required ".
   * 
   * @return translated "There must be at least one row for this analyte because it is required "
   */
  @DefaultMessage("There must be at least one row for this analyte because it is required ")
  @Key("result.cantRemoveReqAnalyte")
  String result_cantRemoveReqAnalyte();

  /**
   * Translated "Current Result".
   * 
   * @return translated "Current Result"
  
   */
  @DefaultMessage("Current Result")
  @Key("result.current")
  String result_current();

  /**
   * Translated "Accession # {0,number,#0}: {1}, {2} - {3} with DEFAULT value ''{4}'' invalid; please update the test definition".
   * 
   * @param arg0 "{0,number,#0}"
   * @param arg1 "{1}"
   * @param arg2 "{2}"
   * @param arg3 "{3}"
   * @param arg4 "{4}"
   * @return translated "Accession # {0,number,#0}: {1}, {2} - {3} with DEFAULT value ''{4}'' invalid; please update the test definition"
  
   */
  @DefaultMessage("Accession # {0,number,#0}: {1}, {2} - {3} with DEFAULT value ''{4}'' invalid; please update the test definition")
  @Key("result.defaultValueInvalidException")
  String result_defaultValueInvalidException(Integer arg0,  String arg1,  String arg2,  String arg3,  String arg4);

  /**
   * Translated "An analysis needs to be selected to view current result history".
   * 
   * @return translated "An analysis needs to be selected to view current result history"
  
   */
  @DefaultMessage("An analysis needs to be selected to view current result history")
  @Key("result.historyException")
  String result_historyException();

  /**
   * Translated "Result(s) Overridden".
   * 
   * @return translated "Result(s) Overridden"
  
   */
  @DefaultMessage("Result(s) Overridden")
  @Key("result.overridden")
  String result_overridden();

  /**
   * Translated "Accession # {0,number,#0}: {1}, {2} - Sample not found for accession # {3,number,#0}.".
   * 
   * @param arg0 "{0,number,#0}"
   * @param arg1 "{1}"
   * @param arg2 "{2}"
   * @param arg3 "{3,number,#0}"
   * @return translated "Accession # {0,number,#0}: {1}, {2} - Sample not found for accession # {3,number,#0}."
  
   */
  @DefaultMessage("Accession # {0,number,#0}: {1}, {2} - Sample not found for accession # {3,number,#0}.")
  @Key("result.partSamNotFoundException")
  String result_partSamNotFoundException(Integer arg0,  String arg1,  String arg2,  Integer arg3);

  /**
   * Translated "Accession # {0,number,#0}: {1}, {2} - {3} with value ''{4}'' invalid".
   * 
   * @param arg0 "{0,number,#0}"
   * @param arg1 "{1}"
   * @param arg2 "{2}"
   * @param arg3 "{3}"
   * @param arg4 "{4}"
   * @return translated "Accession # {0,number,#0}: {1}, {2} - {3} with value ''{4}'' invalid"
  
   */
  @DefaultMessage("Accession # {0,number,#0}: {1}, {2} - {3} with value ''{4}'' invalid")
  @Key("result.valueInvalidException")
  String result_valueInvalidException(Integer arg0,  String arg1,  String arg2,  String arg3,  String arg4);

  /**
   * Translated "Accession # {0,number,#0}: {1}, {2} - {3} is required to have a value".
   * 
   * @param arg0 "{0,number,#0}"
   * @param arg1 "{1}"
   * @param arg2 "{2}"
   * @param arg3 "{3}"
   * @return translated "Accession # {0,number,#0}: {1}, {2} - {3} is required to have a value"
  
   */
  @DefaultMessage("Accession # {0,number,#0}: {1}, {2} - {3} is required to have a value")
  @Key("result.valueRequiredException")
  String result_valueRequiredException(Integer arg0,  String arg1,  String arg2,  String arg3);

  /**
   * Translated "Results of type \"Default\" must not be selected for reflex tests".
   * 
   * @return translated "Results of type \"Default\" must not be selected for reflex tests"
  
   */
  @DefaultMessage("Results of type \"Default\" must not be selected for reflex tests")
  @Key("resultDefaultReflexTestException")
  String resultDefaultReflexTestException();

  /**
   * Translated "This result has been removed from the list of results for this analyte. Please choose another result".
   * 
   * @return translated "This result has been removed from the list of results for this analyte. Please choose another result"
  
   */
  @DefaultMessage("This result has been removed from the list of results for this analyte. Please choose another result")
  @Key("resultDeleted")
  String resultDeleted();

  /**
   * Translated "This result does not belong to the result group that has been selected for the analyte in this row ".
   * 
   * @return translated "This result does not belong to the result group that has been selected for the analyte in this row "
  
   */
  @DefaultMessage("This result does not belong to the result group that has been selected for the analyte in this row ")
  @Key("resultDoesntBelongToAnalyteException")
  String resultDoesntBelongToAnalyteException();

  /**
   * Translated "Result Fields To Search By".
   * 
   * @return translated "Result Fields To Search By"
  
   */
  @DefaultMessage("Result Fields To Search By")
  @Key("resultFieldSearchBy")
  String resultFieldSearchBy();

  /**
   * Translated "One or more result groups are empty and will be removed on commit.\n Are you sure you would like to commit?".
   * 
   * @return translated "One or more result groups are empty and will be removed on commit.\n Are you sure you would like to commit?"
  
   */
  @DefaultMessage("One or more result groups are empty and will be removed on commit.\n Are you sure you would like to commit?")
  @Key("resultGroupsEmpty")
  String resultGroupsEmpty();

  /**
   * Translated "One or more analytes don''t have any result group selected for them. Are you sure you would like to commit?".
   * 
   * @return translated "One or more analytes don''t have any result group selected for them. Are you sure you would like to commit?"
  
   */
  @DefaultMessage("One or more analytes don''t have any result group selected for them. Are you sure you would like to commit?")
  @Key("resultGrpNotSelForAll")
  String resultGrpNotSelForAll();

  /**
   * Translated "An analysis needs to be selected to view current result history".
   * 
   * @return translated "An analysis needs to be selected to view current result history"
  
   */
  @DefaultMessage("An analysis needs to be selected to view current result history")
  @Key("resultHistoryException")
  String resultHistoryException();

  /**
   * Translated "There are results added to this test but no analytes. Are you sure you would like to commit?".
   * 
   * @return translated "There are results added to this test but no analytes. Are you sure you would like to commit?"
  
   */
  @DefaultMessage("There are results added to this test but no analytes. Are you sure you would like to commit?")
  @Key("resultNoAnalytes")
  String resultNoAnalytes();

  /**
   * Translated "The value of this result has been changed. Please choose another result or the changed value".
   * 
   * @return translated "The value of this result has been changed. Please choose another result or the changed value"
  
   */
  @DefaultMessage("The value of this result has been changed. Please choose another result or the changed value")
  @Key("resultValueChanged")
  String resultValueChanged();

  /**
   * Translated "Result(s) Overridden".
   * 
   * @return translated "Result(s) Overridden"
  
   */
  @DefaultMessage("Result(s) Overridden")
  @Key("resultsOverridden")
  String resultsOverridden();

  /**
   * Translated "Retrieving Samples".
   * 
   * @return translated "Retrieving Samples"
  
   */
  @DefaultMessage("Retrieving Samples")
  @Key("retrSamples")
  String retrSamples();

  /**
   * Translated "Rev".
   * 
   * @return translated "Rev"
  
   */
  @DefaultMessage("Rev")
  @Key("rev")
  String rev();

  /**
   * Translated "Complete and Release".
   * 
   * @return translated "Complete and Release"
  
   */
  @DefaultMessage("Complete and Release")
  @Key("reviewAndRelease")
  String reviewAndRelease();

  /**
   * Translated "Perform final review and release of analyses.".
   * 
   * @return translated "Perform final review and release of analyses."
  
   */
  @DefaultMessage("Perform final review and release of analyses.")
  @Key("reviewAndReleaseDescription")
  String reviewAndReleaseDescription();

  /**
   * Translated "Revision".
   * 
   * @return translated "Revision"
  
   */
  @DefaultMessage("Revision")
  @Key("revision")
  String revision();

  /**
   * Translated "Revision Method".
   * 
   * @return translated "Revision Method"
  
   */
  @DefaultMessage("Revision Method")
  @Key("revisionMethod")
  String revisionMethod();

  /**
   * Translated "Rich Text Editor".
   * 
   * @return translated "Rich Text Editor"
  
   */
  @DefaultMessage("Rich Text Editor")
  @Key("richTextEditor")
  String richTextEditor();

  /**
   * Translated "Rdng. Method".
   * 
   * @return translated "Rdng. Method"
  
   */
  @DefaultMessage("Rdng. Method")
  @Key("roundingMethod")
  String roundingMethod();

  /**
   * Translated "Accession # {0} - {1}".
   * 
   * @param arg0 "{0}"
   * @param arg1 "{1}"
   * @return translated "Accession # {0} - {1}"
  
   */
  @DefaultMessage("Accession # {0} - {1}")
  @Key("rowError")
  String rowError(String arg0,  String arg1);

  /**
   * Translated "Run".
   * 
   * @return translated "Run"
  
   */
  @DefaultMessage("Run")
  @Key("run")
  String run();

  /**
   * Translated "Run Report".
   * 
   * @return translated "Run Report"
  
   */
  @DefaultMessage("Run Report")
  @Key("runReport")
  String runReport();

  /**
   * Translated "Only orders with the same shipping destination, status and type can be combined together".
   * 
   * @return translated "Only orders with the same shipping destination, status and type can be combined together"
  
   */
  @DefaultMessage("Only orders with the same shipping destination, status and type can be combined together")
  @Key("sameShipToStatusTypeOrderCombined")
  String sameShipToStatusTypeOrderCombined();

  /**
   * Translated "Sample".
   * 
   * @return translated "Sample"
  
   */
  @DefaultMessage("Sample")
  @Key("sample")
  String sample();

  /**
   * Translated "Accession #".
   * 
   * @return translated "Accession #"
  
   */
  @DefaultMessage("Accession #")
  @Key("sample.accessionNum")
  String sample_accessionNum();

  /**
   * Translated "Accession # {0,number,#0}: Number is already entered into the system".
   * 
   * @param arg0 "{0,number,#0}"
   * @return translated "Accession # {0,number,#0}: Number is already entered into the system"
  
   */
  @DefaultMessage("Accession # {0,number,#0}: Number is already entered into the system")
  @Key("sample.accessionNumberDuplicate")
  String sample_accessionNumberDuplicate(Integer arg0);

  /**
   * Translated "Are you sure you want to change this sample''s accession number?".
   * 
   * @return translated "Are you sure you want to change this sample''s accession number?"
  
   */
  @DefaultMessage("Are you sure you want to change this sample''s accession number?")
  @Key("sample.accessionNumberEditConfirm")
  String sample_accessionNumberEditConfirm();

  /**
   * Translated "Accession # {0,number,#0}: Number is not in use yet".
   * 
   * @param arg0 "{0,number,#0}"
   * @return translated "Accession # {0,number,#0}: Number is not in use yet"
  
   */
  @DefaultMessage("Accession # {0,number,#0}: Number is not in use yet")
  @Key("sample.accessionNumberNotInUse")
  String sample_accessionNumberNotInUse(Integer arg0);

  /**
   * Translated "Accession # {0,number,#0}: Number is invalid".
   * 
   * @param arg0 "{0,number,#0}"
   * @return translated "Accession # {0,number,#0}: Number is invalid"
  
   */
  @DefaultMessage("Accession # {0,number,#0}: Number is invalid")
  @Key("sample.accessionNumberNotValidException")
  String sample_accessionNumberNotValidException(Integer arg0);

  /**
   * Translated "Accession # {0,number,#0}".
   * 
   * @param arg0 "{0,number,#0}"
   * @return translated "Accession # {0,number,#0}"
  
   */
  @DefaultMessage("Accession # {0,number,#0}")
  @Key("sample.accessionPrefix")
  String sample_accessionPrefix(Integer arg0);

  /**
   * Translated "Sample is released. You cannot add an analysis.".
   * 
   * @return translated "Sample is released. You cannot add an analysis."
  
   */
  @DefaultMessage("Sample is released. You cannot add an analysis.")
  @Key("sample.cantAddAnalysis")
  String sample_cantAddAnalysis();

  /**
   * Translated "Accession # '{0}': Not allowed to add organization of this type to record".
   * 
   * @return translated "Accession # '{0}': Not allowed to add organization of this type to record"
  
   */
  @DefaultMessage("Accession # '{0}': Not allowed to add organization of this type to record")
  @Key("sample.cantAddOrgTypeToDomainException")
  String sample_cantAddOrgTypeToDomainException();

  /**
   * Translated "Sample and/or analysis is released. You cannot add non-internal QA events.".
   * 
   * @return translated "Sample and/or analysis is released. You cannot add non-internal QA events."
  
   */
  @DefaultMessage("Sample and/or analysis is released. You cannot add non-internal QA events.")
  @Key("sample.cantAddQA")
  String sample_cantAddQA();

  /**
   * Translated "The domain cannot be changed because the sample does not exist in the system".
   * 
   * @return translated "The domain cannot be changed because the sample does not exist in the system"
  
   */
  @DefaultMessage("The domain cannot be changed because the sample does not exist in the system")
  @Key("sample.cantChangeDomainNewSampleException")
  String sample_cantChangeDomainNewSampleException();

  /**
   * Translated "The domain of a quick-entered sample cannot be changed through this option".
   * 
   * @return translated "The domain of a quick-entered sample cannot be changed through this option"
  
   */
  @DefaultMessage("The domain of a quick-entered sample cannot be changed through this option")
  @Key("sample.cantChangeDomainQuickEntryException")
  String sample_cantChangeDomainQuickEntryException();

  /**
   * Translated "The domain of a released sample cannot be changed".
   * 
   * @return translated "The domain of a released sample cannot be changed"
  
   */
  @DefaultMessage("The domain of a released sample cannot be changed")
  @Key("sample.cantChangeDomainReleasedSampleException")
  String sample_cantChangeDomainReleasedSampleException();

  /**
   * Translated "The domain cannot be changed to Quick Entry".
   * 
   * @return translated "The domain cannot be changed to Quick Entry"
  
   */
  @DefaultMessage("The domain cannot be changed to Quick Entry")
  @Key("sample.cantChangeDomainToQuickEntryException")
  String sample_cantChangeDomainToQuickEntryException();

  /**
   * Translated "Accession # {0,number,#0}: Cannot duplicate sample - One or more analyses have advanced beyond logged-in".
   * 
   * @param arg0 "{0,number,#0}"
   * @return translated "Accession # {0,number,#0}: Cannot duplicate sample - One or more analyses have advanced beyond logged-in"
  
   */
  @DefaultMessage("Accession # {0,number,#0}: Cannot duplicate sample - One or more analyses have advanced beyond logged-in")
  @Key("sample.cantDuplicateAnaPastLoggedInException")
  String sample_cantDuplicateAnaPastLoggedInException(Integer arg0);

  /**
   * Translated "Accession # {0,number,#0}: Cannot duplicate a completed or released sample".
   * 
   * @param arg0 "{0,number,#0}"
   * @return translated "Accession # {0,number,#0}: Cannot duplicate a completed or released sample"
  
   */
  @DefaultMessage("Accession # {0,number,#0}: Cannot duplicate a completed or released sample")
  @Key("sample.cantDuplicateCompRelException")
  String sample_cantDuplicateCompRelException(Integer arg0);

  /**
   * Translated "Accession # {0,number,#0}: Cannot duplicate sample - {1}, {2} has been reflexed by another analysis".
   * 
   * @param arg0 "{0,number,#0}"
   * @param arg1 "{1}"
   * @param arg2 "{2}"
   * @return translated "Accession # {0,number,#0}: Cannot duplicate sample - {1}, {2} has been reflexed by another analysis"
  
   */
  @DefaultMessage("Accession # {0,number,#0}: Cannot duplicate sample - {1}, {2} has been reflexed by another analysis")
  @Key("sample.cantDuplicateReflexAnaException")
  String sample_cantDuplicateReflexAnaException(Integer arg0,  String arg1,  String arg2);

  /**
   * Translated "Accession # {0,number,#0}: A quick entered sample cannot be loaded if the order # is present ".
   * 
   * @param arg0 "{0,number,#0}"
   * @return translated "Accession # {0,number,#0}: A quick entered sample cannot be loaded if the order # is present "
  
   */
  @DefaultMessage("Accession # {0,number,#0}: A quick entered sample cannot be loaded if the order # is present ")
  @Key("sample.cantLoadQEOrderPresentException")
  String sample_cantLoadQEOrderPresentException(Integer arg0);

  /**
   * Translated "One or more analysis have been released. You cannot remove non-internal sample QA events.".
   * 
   * @return translated "One or more analysis have been released. You cannot remove non-internal sample QA events."
  
   */
  @DefaultMessage("One or more analysis have been released. You cannot remove non-internal sample QA events.")
  @Key("sample.cantRemoveQA")
  String sample_cantRemoveQA();

  /**
   * Translated "The non-billable QA Event ''{0}'' was made billable because sample and/or analysis is released. ".
   * 
   * @param arg0 "{0}"
   * @return translated "The non-billable QA Event ''{0}'' was made billable because sample and/or analysis is released. "
  
   */
  @DefaultMessage("The non-billable QA Event ''{0}'' was made billable because sample and/or analysis is released. ")
  @Key("sample.changedToBillable")
  String sample_changedToBillable(String arg0);

  /**
   * Translated "Please choose a test or panel".
   * 
   * @return translated "Please choose a test or panel"
  
   */
  @DefaultMessage("Please choose a test or panel")
  @Key("sample.chooseTestOrPanel")
  String sample_chooseTestOrPanel();

  /**
   * Translated "Clinical".
   * 
   * @return translated "Clinical"
  
   */
  @DefaultMessage("Clinical")
  @Key("sample.clinical")
  String sample_clinical();

  /**
   * Translated "Client Reference".
   * 
   * @return translated "Client Reference"
  
   */
  @DefaultMessage("Client Reference")
  @Key("sample.clntRef")
  String sample_clntRef();

  /**
   * Translated "Collected".
   * 
   * @return translated "Collected"
  
   */
  @DefaultMessage("Collected")
  @Key("sample.collected")
  String sample_collected();

  /**
   * Translated "Collected Date".
   * 
   * @return translated "Collected Date"
  
   */
  @DefaultMessage("Collected Date")
  @Key("sample.collectedDate")
  String sample_collectedDate();

  /**
   * Translated "Accession # {0,number,#0}: Collected date can''t be after entered".
   * 
   * @param arg0 "{0,number,#0}"
   * @return translated "Accession # {0,number,#0}: Collected date can''t be after entered"
  
   */
  @DefaultMessage("Accession # {0,number,#0}: Collected date can''t be after entered")
  @Key("sample.collectedDateAfterEnteredException")
  String sample_collectedDateAfterEnteredException(Integer arg0);

  /**
   * Translated "Accession # {0,number,#0}: Collected date can''t be after received".
   * 
   * @param arg0 "{0,number,#0}"
   * @return translated "Accession # {0,number,#0}: Collected date can''t be after received"
  
   */
  @DefaultMessage("Accession # {0,number,#0}: Collected date can''t be after received")
  @Key("sample.collectedDateAfterReceivedException")
  String sample_collectedDateAfterReceivedException(Integer arg0);

  /**
   * Translated "Accession # {0,number,#0}: Collection time can''t be specified without collection date".
   * 
   * @param arg0 "{0,number,#0}"
   * @return translated "Accession # {0,number,#0}: Collection time can''t be specified without collection date"
  
   */
  @DefaultMessage("Accession # {0,number,#0}: Collection time can''t be specified without collection date")
  @Key("sample.collectedTimeWithoutDateException")
  String sample_collectedTimeWithoutDateException(Integer arg0);

  /**
   * Translated "Accession # {0,number,#0}: Collected date shouldn''t be more than 180 days before entered date".
   * 
   * @param arg0 "{0,number,#0}"
   * @return translated "Accession # {0,number,#0}: Collected date shouldn''t be more than 180 days before entered date"
  
   */
  @DefaultMessage("Accession # {0,number,#0}: Collected date shouldn''t be more than 180 days before entered date")
  @Key("sample.collectedTooOldWarning")
  String sample_collectedTooOldWarning(Integer arg0);

  /**
   * Translated "Collector".
   * 
   * @return translated "Collector"
  
   */
  @DefaultMessage("Collector")
  @Key("sample.collector")
  String sample_collector();

  /**
   * Translated "Sample Description".
   * 
   * @return translated "Sample Description"
  
   */
  @DefaultMessage("Sample Description")
  @Key("sample.description")
  String sample_description();

  /**
   * Translated "The domain of this sample has changed".
   * 
   * @return translated "The domain of this sample has changed"
  
   */
  @DefaultMessage("The domain of this sample has changed")
  @Key("sample.domainChangedException")
  String sample_domainChangedException();

  /**
   * Translated "Accession # {0,number,#0} : Specified domain is invalid".
   * 
   * @param arg0 "{0,number,#0}"
   * @return translated "Accession # {0,number,#0} : Specified domain is invalid"
  
   */
  @DefaultMessage("Accession # {0,number,#0} : Specified domain is invalid")
  @Key("sample.domainNotValid")
  String sample_domainNotValid(Integer arg0);

  /**
   * Translated "The accession number must be entered before loading an order".
   * 
   * @return translated "The accession number must be entered before loading an order"
  
   */
  @DefaultMessage("The accession number must be entered before loading an order")
  @Key("sample.enterAccNumBeforeOrderLoad")
  String sample_enterAccNumBeforeOrderLoad();

  /**
   * Translated "Accession # {0,number,#0}: Entered date is required".
   * 
   * @param arg0 "{0,number,#0}"
   * @return translated "Accession # {0,number,#0}: Entered date is required"
  
   */
  @DefaultMessage("Accession # {0,number,#0}: Entered date is required")
  @Key("sample.enteredDateRequiredException")
  String sample_enteredDateRequiredException(Integer arg0);

  /**
   * Translated "Environmental".
   * 
   * @return translated "Environmental"
  
   */
  @DefaultMessage("Environmental")
  @Key("sample.environmental")
  String sample_environmental();

  /**
   * Translated "From TRF".
   * 
   * @return translated "From TRF"
  
   */
  @DefaultMessage("From TRF")
  @Key("sample.fromTRF")
  String sample_fromTRF();

  /**
   * Translated "Accession # {0,number,#0}: The auxiliary group ''{1}'' is inactive and wasn''t added to the sample".
   * 
   * @param arg0 "{0,number,#0}"
   * @param arg1 "{1}"
   * @return translated "Accession # {0,number,#0}: The auxiliary group ''{1}'' is inactive and wasn''t added to the sample"
  
   */
  @DefaultMessage("Accession # {0,number,#0}: The auxiliary group ''{1}'' is inactive and wasn''t added to the sample")
  @Key("sample.inactiveAuxGroupWarning")
  String sample_inactiveAuxGroupWarning(Integer arg0,  String arg1);

  /**
   * Translated "Accession # {0,number,#0}: The organization ''{1}'' is inactive and wasn''t added to the sample".
   * 
   * @param arg0 "{0,number,#0}"
   * @param arg1 "{1}"
   * @return translated "Accession # {0,number,#0}: The organization ''{1}'' is inactive and wasn''t added to the sample"
  
   */
  @DefaultMessage("Accession # {0,number,#0}: The organization ''{1}'' is inactive and wasn''t added to the sample")
  @Key("sample.inactiveOrgWarning")
  String sample_inactiveOrgWarning(Integer arg0,  String arg1);

  /**
   * Translated "Accession # {0,number,#0}: The project ''{1}'' is inactive and wasn''t added to the sample".
   * 
   * @param arg0 "{0,number,#0}"
   * @param arg1 "{1}"
   * @return translated "Accession # {0,number,#0}: The project ''{1}'' is inactive and wasn''t added to the sample"
  
   */
  @DefaultMessage("Accession # {0,number,#0}: The project ''{1}'' is inactive and wasn''t added to the sample")
  @Key("sample.inactiveProjectWarning")
  String sample_inactiveProjectWarning(Integer arg0,  String arg1);

  /**
   * Translated "Accession # {0,number,#0}: {1}, {2} is inactive and was not added to the sample ".
   * 
   * @param arg0 "{0,number,#0}"
   * @param arg1 "{1}"
   * @param arg2 "{2}"
   * @return translated "Accession # {0,number,#0}: {1}, {2} is inactive and was not added to the sample "
  
   */
  @DefaultMessage("Accession # {0,number,#0}: {1}, {2} is inactive and was not added to the sample ")
  @Key("sample.inactiveTestWarning")
  String sample_inactiveTestWarning(Integer arg0,  String arg1,  String arg2);

  /**
   * Translated "{0} is an invalid entry, please try again".
   * 
   * @param arg0 "{0}"
   * @return translated "{0} is an invalid entry, please try again"
  
   */
  @DefaultMessage("{0} is an invalid entry, please try again")
  @Key("sample.invalidEntryException")
  String sample_invalidEntryException(String arg0);

  /**
   * Translated "Items/Analyses".
   * 
   * @return translated "Items/Analyses"
  
   */
  @DefaultMessage("Items/Analyses")
  @Key("sample.itemAnalyses")
  String sample_itemAnalyses();

  /**
   * Translated "Accession # {0,number,#0}: The sample must have at least one sample item".
   * 
   * @param arg0 "{0,number,#0}"
   * @return translated "Accession # {0,number,#0}: The sample must have at least one sample item"
  
   */
  @DefaultMessage("Accession # {0,number,#0}: The sample must have at least one sample item")
  @Key("sample.minOneSampleItemException")
  String sample_minOneSampleItemException(Integer arg0);

  /**
   * Translated "Accession # {0,number,#0}: The sample must have only one report to".
   * 
   * @param arg0 "{0,number,#0}"
   * @return translated "Accession # {0,number,#0}: The sample must have only one report to"
  
   */
  @DefaultMessage("Accession # {0,number,#0}: The sample must have only one report to")
  @Key("sample.moreThanOneReportToException")
  String sample_moreThanOneReportToException(Integer arg0);

  /**
   * Translated "Accession # ''{0}'': Only one record with the type Bill To is allowed".
   * 
   * @param arg0 "{0}"
   * @return translated "Accession # ''{0}'': Only one record with the type Bill To is allowed"
  
   */
  @DefaultMessage("Accession # ''{0}'': Only one record with the type Bill To is allowed")
  @Key("sample.multipleBillToException")
  String sample_multipleBillToException(String arg0);

  /**
   * Translated "Accession # ''{0}'': Only one record with the type Birth Hospital is allowed".
   * 
   * @param arg0 "{0}"
   * @return translated "Accession # ''{0}'': Only one record with the type Birth Hospital is allowed"
  
   */
  @DefaultMessage("Accession # ''{0}'': Only one record with the type Birth Hospital is allowed")
  @Key("sample.multipleBirthHospException")
  String sample_multipleBirthHospException(String arg0);

  /**
   * Translated "Accession # ''{0}'': Only one record with the type Report To is allowed ".
   * 
   * @param arg0 "{0}"
   * @return translated "Accession # ''{0}'': Only one record with the type Report To is allowed "
  
   */
  @DefaultMessage("Accession # ''{0}'': Only one record with the type Report To is allowed ")
  @Key("sample.multipleReportToException")
  String sample_multipleReportToException(String arg0);

  /**
   * Translated "Neonatal".
   * 
   * @return translated "Neonatal"
  
   */
  @DefaultMessage("Neonatal")
  @Key("sample.neonatal")
  String sample_neonatal();

  /**
   * Translated "Accession # {0,number,#0}: Missing or undefined domain".
   * 
   * @param arg0 "{0,number,#0}"
   * @return translated "Accession # {0,number,#0}: Missing or undefined domain"
  
   */
  @DefaultMessage("Accession # {0,number,#0}: Missing or undefined domain")
  @Key("sample.noDomainException")
  String sample_noDomainException(Integer arg0);

  /**
   * Translated "A QA event has been added that makes this sample and all its analyses not billable".
   * 
   * @return translated "A QA event has been added that makes this sample and all its analyses not billable"
  
   */
  @DefaultMessage("A QA event has been added that makes this sample and all its analyses not billable")
  @Key("sample.notBillable")
  String sample_notBillable();

  /**
   * Translated "Accession # {0,number,#0}: This number has already been used for a fully logged in sample ".
   * 
   * @param arg0 "{0,number,#0}"
   * @return translated "Accession # {0,number,#0}: This number has already been used for a fully logged in sample "
  
   */
  @DefaultMessage("Accession # {0,number,#0}: This number has already been used for a fully logged in sample ")
  @Key("sample.notQuickEntryException")
  String sample_notQuickEntryException(Integer arg0);

  /**
   * Translated "Accession # {0,number,#0}: {1}".
   * 
   * @param arg0 "{0,number,#0}"
   * @param arg1 "{1}"
   * @return translated "Accession # {0,number,#0}: {1}"
  
   */
  @DefaultMessage("Accession # {0,number,#0}: {1}")
  @Key("sample.noteException")
  String sample_noteException(Integer arg0,  String arg1);

  /**
   * Translated "Sample Notes".
   * 
   * @return translated "Sample Notes"
  
   */
  @DefaultMessage("Sample Notes")
  @Key("sample.notes")
  String sample_notes();

  /**
   * Translated "Accession # {0,number,#0}: Order # {1,number,#0} must be the id of an existing Send-out order".
   * 
   * @param arg0 "{0,number,#0}"
   * @param arg1 "{1,number,#0}"
   * @return translated "Accession # {0,number,#0}: Order # {1,number,#0} must be the id of an existing Send-out order"
  
   */
  @DefaultMessage("Accession # {0,number,#0}: Order # {1,number,#0} must be the id of an existing Send-out order")
  @Key("sample.orderIdInvalidException")
  String sample_orderIdInvalidException(Integer arg0,  Integer arg1);

  /**
   * Translated "Accession # {0,number,#0}: {1} ''{2}'' is invalid and couldn''t be imported".
   * 
   * @param arg0 "{0,number,#0}"
   * @param arg1 "{1}"
   * @param arg2 "{2}"
   * @return translated "Accession # {0,number,#0}: {1} ''{2}'' is invalid and couldn''t be imported"
  
   */
  @DefaultMessage("Accession # {0,number,#0}: {1} ''{2}'' is invalid and couldn''t be imported")
  @Key("sample.orderImportException")
  String sample_orderImportException(Integer arg0,  String arg1,  String arg2);

  /**
   * Translated "Sample Organization".
   * 
   * @return translated "Sample Organization"
  
   */
  @DefaultMessage("Sample Organization")
  @Key("sample.organization")
  String sample_organization();

  /**
   * Translated "Accession # {0,number,#0}: Last name is required for a patient".
   * 
   * @param arg0 "{0,number,#0}"
   * @return translated "Accession # {0,number,#0}: Last name is required for a patient"
  
   */
  @DefaultMessage("Accession # {0,number,#0}: Last name is required for a patient")
  @Key("sample.patientLastNameRequiredException")
  String sample_patientLastNameRequiredException(Integer arg0);

  /**
   * Translated "Private Well".
   * 
   * @return translated "Private Well"
  
   */
  @DefaultMessage("Private Well")
  @Key("sample.privateWell")
  String sample_privateWell();

  /**
   * Translated "Sample Project".
   * 
   * @return translated "Sample Project"
  
   */
  @DefaultMessage("Sample Project")
  @Key("sample.project")
  String sample_project();

  /**
   * Translated "Provider/Organization Info".
   * 
   * @return translated "Provider/Organization Info"
   */
  @DefaultMessage("Provider/Organization Info")
  @Key("sample.providerOrganizationInfo")
  String sample_providerOrganizationInfo();

  /**
   * Translated "PT".
   * 
   * @return translated "PT"
   */
  @DefaultMessage("PT")
  @Key("sample.pt")
  String sample_pt();

  /**
   * Translated "Sample QA Events".
   * 
   * @return translated "Sample QA Events"
  
   */
  @DefaultMessage("Sample QA Events")
  @Key("sample.qaEvents")
  String sample_qaEvents();

  /**
   * Translated "Received".
   * 
   * @return translated "Received"
  
   */
  @DefaultMessage("Received")
  @Key("sample.received")
  String sample_received();

  /**
   * Translated "Accession # {0,number,#0}: Received date is required".
   * 
   * @param arg0 "{0,number,#0}"
   * @return translated "Accession # {0,number,#0}: Received date is required"
  
   */
  @DefaultMessage("Accession # {0,number,#0}: Received date is required")
  @Key("sample.receivedDateRequiredException")
  String sample_receivedDateRequiredException(Integer arg0);

  /**
   * Translated "Accession # {0,number,#0}: Received date shouldn''t be more than 180 days before entered date ".
   * 
   * @param arg0 "{0,number,#0}"
   * @return translated "Accession # {0,number,#0}: Received date shouldn''t be more than 180 days before entered date "
  
   */
  @DefaultMessage("Accession # {0,number,#0}: Received date shouldn''t be more than 180 days before entered date ")
  @Key("sample.receivedTooOldWarning")
  String sample_receivedTooOldWarning(Integer arg0);

  /**
   * Translated "Sample Released Date".
   * 
   * @return translated "Sample Released Date"
  
   */
  @DefaultMessage("Sample Released Date")
  @Key("sample.releasedDate")
  String sample_releasedDate();

  /**
   * Translated "Accession # {0,number,#0}: Sample must have a report to".
   * 
   * @param arg0 "{0,number,#0}"
   * @return translated "Accession # {0,number,#0}: Sample must have a report to"
  
   */
  @DefaultMessage("Accession # {0,number,#0}: Sample must have a report to")
  @Key("sample.reportToMissingWarning")
  String sample_reportToMissingWarning(Integer arg0);

  /**
   * Translated "Sample".
   * 
   * @return translated "Sample"
  
   */
  @DefaultMessage("Sample")
  @Key("sample.sample")
  String sample_sample();

  /**
   * Translated "An item must be selected to add an analysis".
   * 
   * @return translated "An item must be selected to add an analysis"
  
   */
  @DefaultMessage("An item must be selected to add an analysis")
  @Key("sample.sampleItemSelectedToAddAnalysis")
  String sample_sampleItemSelectedToAddAnalysis();

  /**
   * Translated "Sample type is required before adding test".
   * 
   * @return translated "Sample type is required before adding test"
  
   */
  @DefaultMessage("Sample type is required before adding test")
  @Key("sample.sampleItemTypeRequired")
  String sample_sampleItemTypeRequired();

  /**
   * Translated "SDWIS".
   * 
   * @return translated "SDWIS"
  
   */
  @DefaultMessage("SDWIS")
  @Key("sample.sdwis")
  String sample_sdwis();

  /**
   * Translated "Sample Specific".
   * 
   * @return translated "Sample Specific"
  
   */
  @DefaultMessage("Sample Specific")
  @Key("sample.specific")
  String sample_specific();

  /**
   * Translated "Sample Status".
   * 
   * @return translated "Sample Status"
  
   */
  @DefaultMessage("Sample Status")
  @Key("sample.status")
  String sample_status();

  /**
   * Translated "Test Lookup".
   * 
   * @return translated "Test Lookup"
  
   */
  @DefaultMessage("Test Lookup")
  @Key("sample.testLookup")
  String sample_testLookup();

  /**
   * Translated "Tracking".
   * 
   * @return translated "Tracking"
  
   */
  @DefaultMessage("Tracking")
  @Key("sample.tracking")
  String sample_tracking();

  /**
   * Translated "Sample Type".
   * 
   * @return translated "Sample Type"
  
   */
  @DefaultMessage("Sample Type")
  @Key("sample.type")
  String sample_type();

  /**
   * Translated "Type/Status".
   * 
   * @return translated "Type/Status"
  
   */
  @DefaultMessage("Type/Status")
  @Key("sample.typeStatus")
  String sample_typeStatus();

  /**
   * Translated "Accession # {0,number,#0}: You must add an internal note when sample/analysis is unreleased".
   * 
   * @param arg0 "{0,number,#0}"
   * @return translated "Accession # {0,number,#0}: You must add an internal note when sample/analysis is unreleased"
  
   */
  @DefaultMessage("Accession # {0,number,#0}: You must add an internal note when sample/analysis is unreleased")
  @Key("sample.unreleaseNoNoteException")
  String sample_unreleaseNoNoteException(Integer arg0);

  /**
   * Translated "Status needs to be ''{0}'' to unrelease".
   * 
   * @param arg0 "{0}"
   * @return translated "Status needs to be ''{0}'' to unrelease"
  
   */
  @DefaultMessage("Status needs to be ''{0}'' to unrelease")
  @Key("sample.wrongStatusUnrelease")
  String sample_wrongStatusUnrelease(String arg0);

  /**
   * Translated "Sample Cat".
   * 
   * @return translated "Sample Cat"
  
   */
  @DefaultMessage("Sample Cat")
  @Key("sampleCat")
  String sampleCat();

  /**
   * Translated "Sample Category is required".
   * 
   * @return translated "Sample Category is required"
  
   */
  @DefaultMessage("Sample Category is required")
  @Key("sampleCatRequiredException")
  String sampleCatRequiredException();

  /**
   * Translated "Clinical Info".
   * 
   * @return translated "Clinical Info"
  
   */
  @DefaultMessage("Clinical Info")
  @Key("sampleClinical.clinicalInfo")
  String sampleClinical_clinicalInfo();

  /**
   * Translated "Clinical Sample Login".
   * 
   * @return translated "Clinical Sample Login"
  
   */
  @DefaultMessage("Clinical Sample Login")
  @Key("sampleClinical.login")
  String sampleClinical_login();

  /**
   * Translated "Fully login clinical sample and analysis related information.".
   * 
   * @return translated "Fully login clinical sample and analysis related information."
  
   */
  @DefaultMessage("Fully login clinical sample and analysis related information.")
  @Key("sampleClinical.loginDescription")
  String sampleClinical_loginDescription();

  /**
<<<<<<< .working
   * Translated "Provider/Organization Info".
   * 
   * @return translated "Provider/Organization Info"
  
   */
  @DefaultMessage("Provider/Organization Info")
  @Key("sampleClinical.providerOrganizationInfo")
  String sampleClinical_providerOrganizationInfo();

  /**
=======
>>>>>>> .merge-right.r9006
   * Translated "Sample Clinical".
   * 
   * @return translated "Sample Clinical"
  
   */
  @DefaultMessage("Sample Clinical")
  @Key("sampleClinical.sampleClinical")
  String sampleClinical_sampleClinical();

  /**
   * Translated "Since there are containers already present in the sample, the \ntests and sample types may not get assigned as defined in the order.\nWould you still like to load the order?".
   * 
   * @return translated "Since there are containers already present in the sample, the \ntests and sample types may not get assigned as defined in the order.\nWould you still like to load the order?"
  
   */
  @DefaultMessage("Since there are containers already present in the sample, the \ntests and sample types may not get assigned as defined in the order.\nWould you still like to load the order?")
  @Key("sampleContainsItems")
  String sampleContainsItems();

  /**
   * Translated "Sample Data Export".
   * 
   * @return translated "Sample Data Export"
  
   */
  @DefaultMessage("Sample Data Export")
  @Key("sampleDataExport")
  String sampleDataExport();

  /**
   * Translated "The domain of this sample has changed".
   * 
   * @return translated "The domain of this sample has changed"
  
   */
  @DefaultMessage("The domain of this sample has changed")
  @Key("sampleDomainChangedException")
  String sampleDomainChangedException();

  /**
   * Translated "Environmental Info".
   * 
   * @return translated "Environmental Info"
  
   */
  @DefaultMessage("Environmental Info")
  @Key("sampleEnvironmental.envInfo")
  String sampleEnvironmental_envInfo();

  /**
   * Translated "Hazardous".
   * 
   * @return translated "Hazardous"
  
   */
  @DefaultMessage("Hazardous")
  @Key("sampleEnvironmental.hazardous")
  String sampleEnvironmental_hazardous();

  /**
   * Translated "Environmental Sample Login".
   * 
   * @return translated "Environmental Sample Login"
  
   */
  @DefaultMessage("Environmental Sample Login")
  @Key("sampleEnvironmental.login")
  String sampleEnvironmental_login();

  /**
   * Translated "Fully login environmental sample and analysis related information.".
   * 
   * @return translated "Fully login environmental sample and analysis related information."
  
   */
  @DefaultMessage("Fully login environmental sample and analysis related information.")
  @Key("sampleEnvironmental.loginDescription")
  String sampleEnvironmental_loginDescription();

  /**
   * Translated "Organization Info".
   * 
   * @return translated "Organization Info"
  
   */
  @DefaultMessage("Organization Info")
  @Key("sampleEnvironmental.organizationInfo")
  String sampleEnvironmental_organizationInfo();

  /**
   * Translated "Sample Environmental".
   * 
   * @return translated "Sample Environmental"
  
   */
  @DefaultMessage("Sample Environmental")
  @Key("sampleEnvironmental.sampleEnvironmental")
  String sampleEnvironmental_sampleEnvironmental();

  /**
   * Translated "Smp Ext Comment".
   * 
   * @return translated "Smp Ext Comment"
  
   */
  @DefaultMessage("Smp Ext Comment")
  @Key("sampleExtrnlCmnts")
  String sampleExtrnlCmnts();

  /**
   * Translated "Sample Fields To Search By".
   * 
   * @return translated "Sample Fields To Search By"
  
   */
  @DefaultMessage("Sample Fields To Search By")
  @Key("sampleFieldSearchBy")
  String sampleFieldSearchBy();

  /**
   * Translated "Samples In-House Report".
   * 
   * @return translated "Samples In-House Report"
  
   */
  @DefaultMessage("Samples In-House Report")
  @Key("sampleInhouseReport")
  String sampleInhouseReport();

  /**
   * Translated "Status of Samples Received".
   * 
   * @return translated "Status of Samples Received"
  
   */
  @DefaultMessage("Status of Samples Received")
  @Key("sampleInhouseStatusReport")
  String sampleInhouseStatusReport();

  /**
   * Translated "Smp Int Comments ".
   * 
   * @return translated "Smp Int Comments "
  
   */
  @DefaultMessage("Smp Int Comments ")
  @Key("sampleIntrnlCmnts")
  String sampleIntrnlCmnts();

  /**
   * Translated "Sample Item".
   * 
   * @return translated "Sample Item"
  
   */
  @DefaultMessage("Sample Item")
  @Key("sampleItem")
  String sampleItem();

  /**
   * Translated "Accession # {0,number,#0}: Cannot remove Item {1,number,#0} - One or more analyses are linked to it ".
   * 
   * @param arg0 "{0,number,#0}"
   * @param arg1 "{1,number,#0}"
   * @return translated "Accession # {0,number,#0}: Cannot remove Item {1,number,#0} - One or more analyses are linked to it "
  
   */
  @DefaultMessage("Accession # {0,number,#0}: Cannot remove Item {1,number,#0} - One or more analyses are linked to it ")
  @Key("sampleItem.cantRemoveException")
  String sampleItem_cantRemoveException(Integer arg0,  Integer arg1);

  /**
   * Translated "Container Reference".
   * 
   * @return translated "Container Reference"
  
   */
  @DefaultMessage("Container Reference")
  @Key("sampleItem.containerReference")
  String sampleItem_containerReference();

  /**
   * Translated "Accession # {0,number,#0}: Item {1,number,#0} - Container ''{2}'' is inactive and was not assigned".
   * 
   * @param arg0 "{0,number,#0}"
   * @param arg1 "{1,number,#0}"
   * @param arg2 "{2}"
   * @return translated "Accession # {0,number,#0}: Item {1,number,#0} - Container ''{2}'' is inactive and was not assigned"
  
   */
  @DefaultMessage("Accession # {0,number,#0}: Item {1,number,#0} - Container ''{2}'' is inactive and was not assigned")
  @Key("sampleItem.inactiveContainerWarning")
  String sampleItem_inactiveContainerWarning(Integer arg0,  Integer arg1,  String arg2);

  /**
   * Translated "Accession # {0,number,#0}: Item {1,number,#0} - Sample type ''{2}'' is inactive and was not assigned ".
   * 
   * @param arg0 "{0,number,#0}"
   * @param arg1 "{1,number,#0}"
   * @param arg2 "{2}"
   * @return translated "Accession # {0,number,#0}: Item {1,number,#0} - Sample type ''{2}'' is inactive and was not assigned "
  
   */
  @DefaultMessage("Accession # {0,number,#0}: Item {1,number,#0} - Sample type ''{2}'' is inactive and was not assigned ")
  @Key("sampleItem.inactiveSampleTypeWarning")
  String sampleItem_inactiveSampleTypeWarning(Integer arg0,  Integer arg1,  String arg2);

  /**
   * Translated "Accession # {0,number,#0}: Item {1,number,#0} - Source ''{2}'' is inactive and was not assigned".
   * 
   * @param arg0 "{0,number,#0}"
   * @param arg1 "{1,number,#0}"
   * @param arg2 "{2}"
   * @return translated "Accession # {0,number,#0}: Item {1,number,#0} - Source ''{2}'' is inactive and was not assigned"
  
   */
  @DefaultMessage("Accession # {0,number,#0}: Item {1,number,#0} - Source ''{2}'' is inactive and was not assigned")
  @Key("sampleItem.inactiveSourceWarning")
  String sampleItem_inactiveSourceWarning(Integer arg0,  Integer arg1,  String arg2);

  /**
   * Translated "Accession # {0,number,#0}: Item {1,number,#0} - Unit ''{2}'' is inactive and was not assigned".
   * 
   * @param arg0 "{0,number,#0}"
   * @param arg1 "{1,number,#0}"
   * @param arg2 "{2}"
   * @return translated "Accession # {0,number,#0}: Item {1,number,#0} - Unit ''{2}'' is inactive and was not assigned"
  
   */
  @DefaultMessage("Accession # {0,number,#0}: Item {1,number,#0} - Unit ''{2}'' is inactive and was not assigned")
  @Key("sampleItem.inactiveUnitWarning")
  String sampleItem_inactiveUnitWarning(Integer arg0,  Integer arg1,  String arg2);

  /**
   * Translated "Sample Item".
   * 
   * @return translated "Sample Item"
  
   */
  @DefaultMessage("Sample Item")
  @Key("sampleItem.sampleItem")
  String sampleItem_sampleItem();

  /**
   * Translated "Source".
   * 
   * @return translated "Source"
  
   */
  @DefaultMessage("Source")
  @Key("sampleItem.source")
  String sampleItem_source();

  /**
   * Translated "Source Other".
   * 
   * @return translated "Source Other"
  
   */
  @DefaultMessage("Source Other")
  @Key("sampleItem.sourceOther")
  String sampleItem_sourceOther();

  /**
   * Translated "Accession # {0,number,#0}: Item {1,number,#0} - Type missing".
   * 
   * @param arg0 "{0,number,#0}"
   * @param arg1 "{1,number,#0}"
   * @return translated "Accession # {0,number,#0}: Item {1,number,#0} - Type missing"
  
   */
  @DefaultMessage("Accession # {0,number,#0}: Item {1,number,#0} - Type missing")
  @Key("sampleItem.typeMissing")
  String sampleItem_typeMissing(Integer arg0,  Integer arg1);

  /**
   * Translated "An item must be selected to add an analysis if there are more than one item present".
   * 
   * @return translated "An item must be selected to add an analysis if there are more than one item present"
  
   */
  @DefaultMessage("An item must be selected to add an analysis if there are more than one item present")
  @Key("sampleItemSelectedToAddAnalysis")
  String sampleItemSelectedToAddAnalysis();

  /**
   * Translated "Item {0} - Type missing".
   * 
   * @param arg0 "{0}"
   * @return translated "Item {0} - Type missing"
  
   */
  @DefaultMessage("Item {0} - Type missing")
  @Key("sampleItemTypeMissing")
  String sampleItemTypeMissing(String arg0);

  /**
   * Translated "Sample type is required before selecting test".
   * 
   * @return translated "Sample type is required before selecting test"
  
   */
  @DefaultMessage("Sample type is required before selecting test")
  @Key("sampleItemTypeRequired")
  String sampleItemTypeRequired();

  /**
   * Translated "Sample Location".
   * 
   * @return translated "Sample Location"
  
   */
  @DefaultMessage("Sample Location")
  @Key("sampleLocation")
  String sampleLocation();

  /**
   * Translated "Sample Login".
   * 
   * @return translated "Sample Login"
  
   */
  @DefaultMessage("Sample Login")
  @Key("sampleLogin")
  String sampleLogin();

  /**
   * Translated "Sample login description".
   * 
   * @return translated "Sample login description"
  
   */
  @DefaultMessage("Sample login description")
  @Key("sampleLoginDescription")
  String sampleLoginDescription();

  /**
   * Translated "Sample Management".
   * 
   * @return translated "Sample Management"
  
   */
  @DefaultMessage("Sample Management")
  @Key("sampleManagement")
  String sampleManagement();

  /**
   * Translated "Description...".
   * 
   * @return translated "Description..."
  
   */
  @DefaultMessage("Description...")
  @Key("sampleManagementDescription")
  String sampleManagementDescription();

  /**
   * Translated "Birth Order".
   * 
   * @return translated "Birth Order"
  
   */
  @DefaultMessage("Birth Order")
  @Key("sampleNeonatal.birthOrder")
  String sampleNeonatal_birthOrder();

  /**
   * Translated "Collect Age".
   * 
   * @return translated "Collect Age"
  
   */
  @DefaultMessage("Collect Age")
  @Key("sampleNeonatal.collectAge")
  String sampleNeonatal_collectAge();

  /**
   * Translated "Collect Valid".
   * 
   * @return translated "Collect Valid"
  
   */
  @DefaultMessage("Collect Valid")
  @Key("sampleNeonatal.collectValid")
  String sampleNeonatal_collectValid();

  /**
   * Translated "Feeding".
   * 
   * @return translated "Feeding"
  
   */
  @DefaultMessage("Feeding")
  @Key("sampleNeonatal.feeding")
  String sampleNeonatal_feeding();

  /**
   * Translated "Gest Age".
   * 
   * @return translated "Gest Age"
  
   */
  @DefaultMessage("Gest Age")
  @Key("sampleNeonatal.gestAge")
  String sampleNeonatal_gestAge();

  /**
   * Translated "Neonatal Screening Sample Login".
   * 
   * @return translated "Neonatal Screening Sample Login"
  
   */
  @DefaultMessage("Neonatal Screening Sample Login")
  @Key("sampleNeonatal.login")
  String sampleNeonatal_login();

  /**
   * Translated "Fully login neonatal sample and analysis related information.".
   * 
   * @return translated "Fully login neonatal sample and analysis related information."
  
   */
  @DefaultMessage("Fully login neonatal sample and analysis related information.")
  @Key("sampleNeonatal.loginDescription")
  String sampleNeonatal_loginDescription();

  /**
   * Translated "NICU".
   * 
   * @return translated "NICU"
  
   */
  @DefaultMessage("NICU")
  @Key("sampleNeonatal.nicu")
  String sampleNeonatal_nicu();

  /**
   * Translated "Accession # {0,number,#0}: Patient birth date must not be after collection date".
   * 
   * @param arg0 "{0,number,#0}"
   * @return translated "Accession # {0,number,#0}: Patient birth date must not be after collection date"
  
   */
  @DefaultMessage("Accession # {0,number,#0}: Patient birth date must not be after collection date")
  @Key("sampleNeonatal.patBirthDateAfterCollectDateException")
  String sampleNeonatal_patBirthDateAfterCollectDateException(Integer arg0);

  /**
   * Translated "Sample Neonatal".
   * 
   * @return translated "Sample Neonatal"
  
   */
  @DefaultMessage("Sample Neonatal")
  @Key("sampleNeonatal.sampleNeonatal")
  String sampleNeonatal_sampleNeonatal();

  /**
   * Translated "Trans Age".
   * 
   * @return translated "Trans Age"
  
   */
  @DefaultMessage("Trans Age")
  @Key("sampleNeonatal.transAge")
  String sampleNeonatal_transAge();

  /**
   * Translated "Trans Date".
   * 
   * @return translated "Trans Date"
  
   */
  @DefaultMessage("Trans Date")
  @Key("sampleNeonatal.transDate")
  String sampleNeonatal_transDate();

  /**
   * Translated "Transfused".
   * 
   * @return translated "Transfused"
  
   */
  @DefaultMessage("Transfused")
  @Key("sampleNeonatal.transfused")
  String sampleNeonatal_transfused();

  /**
   * Translated "Weight".
   * 
   * @return translated "Weight"
  
   */
  @DefaultMessage("Weight")
  @Key("sampleNeonatal.weight")
  String sampleNeonatal_weight();

  /**
   * Translated "A QA event has been added that makes this sample and all its analyses not billable".
   * 
   * @return translated "A QA event has been added that makes this sample and all its analyses not billable"
  
   */
  @DefaultMessage("A QA event has been added that makes this sample and all its analyses not billable")
  @Key("sampleNotBillable")
  String sampleNotBillable();

  /**
   * Translated "Accession #{0} must be verified before {1} : {2} can be released.".
   * 
   * @param arg0 "{0}"
   * @param arg1 "{1}"
   * @param arg2 "{2}"
   * @return translated "Accession #{0} must be verified before {1} : {2} can be released."
  
   */
  @DefaultMessage("Accession #{0} must be verified before {1} : {2} can be released.")
  @Key("sampleNotVerifiedForAnalysisRelease")
  String sampleNotVerifiedForAnalysisRelease(String arg0,  String arg1,  String arg2);

  /**
   * Translated "Sample Notes".
   * 
   * @return translated "Sample Notes"
  
   */
  @DefaultMessage("Sample Notes")
  @Key("sampleNotes")
  String sampleNotes();

  /**
   * Translated "Sample Organization".
   * 
   * @return translated "Sample Organization"
  
   */
  @DefaultMessage("Sample Organization")
  @Key("sampleOrganization")
  String sampleOrganization();

  /**
   * Translated "Bill To".
   * 
   * @return translated "Bill To"
  
   */
  @DefaultMessage("Bill To")
  @Key("sampleOrganization.billTo")
  String sampleOrganization_billTo();

  /**
   * Translated "Birth Hospital".
   * 
   * @return translated "Birth Hospital"
  
   */
  @DefaultMessage("Birth Hospital")
  @Key("sampleOrganization.birthHospital")
  String sampleOrganization_birthHospital();

  /**
   * Translated "Querying for more than one type of organization is not allowed".
   * 
   * @return translated "Querying for more than one type of organization is not allowed"
  
   */
  @DefaultMessage("Querying for more than one type of organization is not allowed")
  @Key("sampleOrganization.cantQueryByMultipleTypeException")
  String sampleOrganization_cantQueryByMultipleTypeException();

  /**
   * Translated "Report To".
   * 
   * @return translated "Report To"
  
   */
  @DefaultMessage("Report To")
  @Key("sampleOrganization.reportTo")
  String sampleOrganization_reportTo();

  /**
   * Translated "Additional Domain".
   * 
   * @return translated "Additional Domain"
   */
  @DefaultMessage("Additional Domain")
  @Key("samplePT.additionalDomain")
  String samplePT_additionalDomain();

  /**
   * Translated "PT Sample Login".
   * 
   * @return translated "PT Sample Login"
   */
  @DefaultMessage("PT Sample Login")
  @Key("samplePT.login")
  String samplePT_login();

  /**
   * Translated "Fully login proficiency testing sample and analysis related information.".
   * 
   * @return translated "Fully login proficiency testing sample and analysis related information."
   */
  @DefaultMessage("Fully login proficiency testing sample and analysis related information.")
  @Key("samplePT.loginDescription")
  String samplePT_loginDescription();

  /**
   * Translated "Accession # {0,number,#0}: Provider is required".
   * 
   * @return translated "Accession # {0,number,#0}: Provider is required"
   */
  @DefaultMessage("Accession # {0,number,#0}: Provider is required")
  @Key("samplePT.providerRequiredException")
  String samplePT_providerRequiredException(Integer arg0);

  /**
   * Translated "PT Info".
   * 
   * @return translated "PT Info"
   */
  @DefaultMessage("PT Info")
  @Key("samplePT.ptInfo")
  String samplePT_ptInfo();

  /**
   * Translated "Sample PT".
   * 
   * @return translated "Sample PT"
   */
  @DefaultMessage("Sample PT")
  @Key("samplePT.samplePT")
  String samplePT_samplePT();

  /**
   * Translated "Accession # {0,number,#0}: Series is required".
   * 
   * @return translated "Accession # {0,number,#0}: Series is required"
   */
  @DefaultMessage("Accession # {0,number,#0}: Series is required")
  @Key("samplePT.seriesRequiredException")
  String samplePT_seriesRequiredException(Integer arg0);

  /**
   * Translated "Sample Prep".
   * 
   * @return translated "Sample Prep"
  
   */
  @DefaultMessage("Sample Prep")
  @Key("samplePrep")
  String samplePrep();

  /**
   * Translated "Sample prep can''t point to itself".
   * 
   * @return translated "Sample prep can''t point to itself"
  
   */
  @DefaultMessage("Sample prep can''t point to itself")
  @Key("samplePrepSampleException")
  String samplePrepSampleException();

  /**
   * Translated "Sample Project".
   * 
   * @return translated "Sample Project"
  
   */
  @DefaultMessage("Sample Project")
  @Key("sampleProject")
  String sampleProject();

  /**
   * Translated "Is Perm".
   * 
   * @return translated "Is Perm"
  
   */
  @DefaultMessage("Is Perm")
  @Key("sampleProject.isPerm")
  String sampleProject_isPerm();

  /**
   * Translated "Sample Pt Id".
   * 
   * @return translated "Sample Pt Id"
  
   */
  @DefaultMessage("Sample Pt Id")
  @Key("samplePtId")
  String samplePtId();

  /**
   * Translated "Sample Pt Id is required".
   * 
   * @return translated "Sample Pt Id is required"
  
   */
  @DefaultMessage("Sample Pt Id is required")
  @Key("samplePtIdRequiredException")
  String samplePtIdRequiredException();

  /**
   * Translated "Sample QA Events ".
   * 
   * @return translated "Sample QA Events "
  
   */
  @DefaultMessage("Sample QA Events ")
  @Key("sampleQAEvent")
  String sampleQAEvent();

  /**
   * Translated "Accession # {0,number,#0}: Type is required for QA Event ''{1}''".
   * 
   * @param arg0 "{0,number,#0}"
   * @param arg1 "{1}"
   * @return translated "Accession # {0,number,#0}: Type is required for QA Event ''{1}''"
  
   */
  @DefaultMessage("Accession # {0,number,#0}: Type is required for QA Event ''{1}''")
  @Key("sampleQAEvent.typeRequiredException")
  String sampleQAEvent_typeRequiredException(Integer arg0,  String arg1);

  /**
   * Translated "Analysis Position".
   * 
   * @return translated "Analysis Position"
  
   */
  @DefaultMessage("Analysis Position")
  @Key("sampleQc.analysisPosition")
  String sampleQc_analysisPosition();

  /**
   * Translated "Check All QCs".
   * 
   * @return translated "Check All QCs"
  
   */
  @DefaultMessage("Check All QCs")
  @Key("sampleQc.checkAllQcs")
  String sampleQc_checkAllQcs();

  /**
   * Translated "Analysis/Worksheet/QC/QC Analyte".
   * 
   * @return translated "Analysis/Worksheet/QC/QC Analyte"
  
   */
  @DefaultMessage("Analysis/Worksheet/QC/QC Analyte")
  @Key("sampleQc.label")
  String sampleQc_label();

  /**
   * Translated "Sample QC".
   * 
   * @return translated "Sample QC"
  
   */
  @DefaultMessage("Sample QC")
  @Key("sampleQc.sampleQc")
  String sampleQc_sampleQc();

  /**
   * Translated "Uncheck All QCs".
   * 
   * @return translated "Uncheck All QCs"
  
   */
  @DefaultMessage("Uncheck All QCs")
  @Key("sampleQc.uncheckAllQcs")
  String sampleQc_uncheckAllQcs();

  /**
   * Translated "Sample Qty".
   * 
   * @return translated "Sample Qty"
  
   */
  @DefaultMessage("Sample Qty")
  @Key("sampleQty")
  String sampleQty();

  /**
   * Translated "Quick Entry".
   * 
   * @return translated "Quick Entry"
  
   */
  @DefaultMessage("Quick Entry")
  @Key("sampleQuickEntry.quickEntry")
  String sampleQuickEntry_quickEntry();

  /**
   * Translated "Sample Revision".
   * 
   * @return translated "Sample Revision"
  
   */
  @DefaultMessage("Sample Revision")
  @Key("sampleRevision")
  String sampleRevision();

  /**
   * Translated "Sample Category".
   * 
   * @return translated "Sample Category"
  
   */
  @DefaultMessage("Sample Category")
  @Key("sampleSDWIS.category")
  String sampleSDWIS_category();

  /**
   * Translated "SDWIS Collector".
   * 
   * @return translated "SDWIS Collector"
  
   */
  @DefaultMessage("SDWIS Collector")
  @Key("sampleSDWIS.collector")
  String sampleSDWIS_collector();

  /**
   * Translated "Collector/Organization Info".
   * 
   * @return translated "Collector/Organization Info"
  
   */
  @DefaultMessage("Collector/Organization Info")
  @Key("sampleSDWIS.collectorOrgInfo")
  String sampleSDWIS_collectorOrgInfo();

  /**
   * Translated "Facility Id".
   * 
   * @return translated "Facility Id"
  
   */
  @DefaultMessage("Facility Id")
  @Key("sampleSDWIS.facilityId")
  String sampleSDWIS_facilityId();

  /**
   * Translated "SDWIS Location".
   * 
   * @return translated "SDWIS Location"
  
   */
  @DefaultMessage("SDWIS Location")
  @Key("sampleSDWIS.location")
  String sampleSDWIS_location();

  /**
   * Translated "SDWIS Sample Login".
   * 
   * @return translated "SDWIS Sample Login"
  
   */
  @DefaultMessage("SDWIS Sample Login")
  @Key("sampleSDWIS.login")
  String sampleSDWIS_login();

  /**
   * Translated "Fully login safe drinking water sample and analysis related information.".
   * 
   * @return translated "Fully login safe drinking water sample and analysis related information."
  
   */
  @DefaultMessage("Fully login safe drinking water sample and analysis related information.")
  @Key("sampleSDWIS.loginDescription")
  String sampleSDWIS_loginDescription();

  /**
   * Translated "Point Desc".
   * 
   * @return translated "Point Desc"
  
   */
  @DefaultMessage("Point Desc")
  @Key("sampleSDWIS.pointDesc")
  String sampleSDWIS_pointDesc();

  /**
   * Translated "Accession # {0,number,#0}: PWS ID is required".
   * 
   * @param arg0 "{0,number,#0}"
   * @return translated "Accession # {0,number,#0}: PWS ID is required"
  
   */
  @DefaultMessage("Accession # {0,number,#0}: PWS ID is required")
  @Key("sampleSDWIS.pwsIdRequiredException")
  String sampleSDWIS_pwsIdRequiredException(Integer arg0);

  /**
   * Translated "PWS Name".
   * 
   * @return translated "PWS Name"
  
   */
  @DefaultMessage("PWS Name")
  @Key("sampleSDWIS.pwsName")
  String sampleSDWIS_pwsName();

  /**
   * Translated "Sample Cat".
   * 
   * @return translated "Sample Cat"
  
   */
  @DefaultMessage("Sample Cat")
  @Key("sampleSDWIS.sampleCat")
  String sampleSDWIS_sampleCat();

  /**
   * Translated "Accession # {0,number,#0}: SDWIS Sample Category is required".
   * 
   * @param arg0 "{0,number,#0}"
   * @return translated "Accession # {0,number,#0}: SDWIS Sample Category is required"
  
   */
  @DefaultMessage("Accession # {0,number,#0}: SDWIS Sample Category is required")
  @Key("sampleSDWIS.sampleCatRequiredException")
  String sampleSDWIS_sampleCatRequiredException(Integer arg0);

  /**
   * Translated "Sample Pt Id".
   * 
   * @return translated "Sample Pt Id"
  
   */
  @DefaultMessage("Sample Pt Id")
  @Key("sampleSDWIS.samplePtId")
  String sampleSDWIS_samplePtId();

  /**
   * Translated "Accession # {0,number,#0}: SDWIS Sample Pt Id is required".
   * 
   * @param arg0 "{0,number,#0}"
   * @return translated "Accession # {0,number,#0}: SDWIS Sample Pt Id is required"
  
   */
  @DefaultMessage("Accession # {0,number,#0}: SDWIS Sample Pt Id is required")
  @Key("sampleSDWIS.samplePtIdRequiredException")
  String sampleSDWIS_samplePtIdRequiredException(Integer arg0);

  /**
   * Translated "Sample SDWIS".
   * 
   * @return translated "Sample SDWIS"
  
   */
  @DefaultMessage("Sample SDWIS")
  @Key("sampleSDWIS.sampleSDWIS")
  String sampleSDWIS_sampleSDWIS();

  /**
   * Translated "SDWIS Sample Type".
   * 
   * @return translated "SDWIS Sample Type"
  
   */
  @DefaultMessage("SDWIS Sample Type")
  @Key("sampleSDWIS.sampleType")
  String sampleSDWIS_sampleType();

  /**
   * Translated "Accession # {0,number,#0}: SDWIS Sample Type is required".
   * 
   * @param arg0 "{0,number,#0}"
   * @return translated "Accession # {0,number,#0}: SDWIS Sample Type is required"
  
   */
  @DefaultMessage("Accession # {0,number,#0}: SDWIS Sample Type is required")
  @Key("sampleSDWIS.sampleTypeRequiredException")
  String sampleSDWIS_sampleTypeRequiredException(Integer arg0);

  /**
   * Translated "SDWIS Info".
   * 
   * @return translated "SDWIS Info"
  
   */
  @DefaultMessage("SDWIS Info")
  @Key("sampleSDWIS.sdwisInfo")
  String sampleSDWIS_sdwisInfo();

  /**
   * Translated "State Lab #".
   * 
   * @return translated "State Lab #"
  
   */
  @DefaultMessage("State Lab #")
  @Key("sampleSDWIS.stateLabNo")
  String sampleSDWIS_stateLabNo();

  /**
   * Translated "Sample Selection Criteria".
   * 
   * @return translated "Sample Selection Criteria"
  
   */
  @DefaultMessage("Sample Selection Criteria")
  @Key("sampleSelectionCriteria")
  String sampleSelectionCriteria();

  /**
   * Translated "Sample Status".
   * 
   * @return translated "Sample Status"
  
   */
  @DefaultMessage("Sample Status")
  @Key("sampleStatus")
  String sampleStatus();

  /**
   * Translated "A sample''s domain can only be changed once during an update ".
   * 
   * @return translated "A sample''s domain can only be changed once during an update "
  
   */
  @DefaultMessage("A sample''s domain can only be changed once during an update ")
  @Key("sampleTracking.canChangeDomainOnlyOnce")
  String sampleTracking_canChangeDomainOnlyOnce();

  /**
   * Translated "Change Domain".
   * 
   * @return translated "Change Domain"
  
   */
  @DefaultMessage("Change Domain")
  @Key("sampleTracking.changeDomain")
  String sampleTracking_changeDomain();

  /**
   * Translated "Lookup and manage general sample information.".
   * 
   * @return translated "Lookup and manage general sample information."
  
   */
  @DefaultMessage("Lookup and manage general sample information.")
  @Key("sampleTracking.description")
  String sampleTracking_description();

  /**
   * Translated "Cannot lookup the sample requested because the screen is busy".
   * 
   * @return translated "Cannot lookup the sample requested because the screen is busy"
  
   */
  @DefaultMessage("Cannot lookup the sample requested because the screen is busy")
  @Key("sampleTracking.notProperState")
  String sampleTracking_notProperState();

  /**
   * Translated "Please query for only one domain at a time.".
   * 
   * @return translated "Please query for only one domain at a time."
  
   */
  @DefaultMessage("Please query for only one domain at a time.")
  @Key("sampleTracking.queryDomainException")
  String sampleTracking_queryDomainException();

  /**
   * Translated "Tracking".
   * 
   * @return translated "Tracking"
  
   */
  @DefaultMessage("Tracking")
  @Key("sampleTracking.tracking")
  String sampleTracking_tracking();

  /**
   * Translated "Type/Status".
   * 
   * @return translated "Type/Status"
  
   */
  @DefaultMessage("Type/Status")
  @Key("sampleTracking.typeStatus")
  String sampleTracking_typeStatus();

  /**
   * Translated "Unrelease Sample".
   * 
   * @return translated "Unrelease Sample"
  
   */
  @DefaultMessage("Unrelease Sample")
  @Key("sampleTracking.unrelease")
  String sampleTracking_unrelease();

  /**
   * Translated "Unreleasing a sample will reset the release date and increment the revision number. \n\nPress Ok to continue or Cancel to abort.".
   * 
   * @return translated "Unreleasing a sample will reset the release date and increment the revision number. \n\nPress Ok to continue or Cancel to abort."
  
   */
  @DefaultMessage("Unreleasing a sample will reset the release date and increment the revision number. \n\nPress Ok to continue or Cancel to abort.")
  @Key("sampleTracking.unreleaseMessage")
  String sampleTracking_unreleaseMessage();

  /**
   * Translated "Sample Type".
   * 
   * @return translated "Sample Type"
  
   */
  @DefaultMessage("Sample Type")
  @Key("sampleType")
  String sampleType();

  /**
   * Translated "Sample Type Barcode".
   * 
   * @return translated "Sample Type Barcode"
  
   */
  @DefaultMessage("Sample Type Barcode")
  @Key("sampleTypeBarcode")
  String sampleTypeBarcode();

  /**
   * Translated "{0} : {1} - Sample type invalid".
   * 
   * @param arg0 "{0}"
   * @param arg1 "{1}"
   * @return translated "{0} : {1} - Sample type invalid"
  
   */
  @DefaultMessage("{0} : {1} - Sample type invalid")
  @Key("sampleTypeInvalid")
  String sampleTypeInvalid(String arg0,  String arg1);

  /**
   * Translated "Sample Type is required".
   * 
   * @return translated "Sample Type is required"
  
   */
  @DefaultMessage("Sample Type is required")
  @Key("sampleTypeRequiredException")
  String sampleTypeRequiredException();

  /**
   * Translated "Save".
   * 
   * @return translated "Save"
  
   */
  @DefaultMessage("Save")
  @Key("save")
  String save();

  /**
   * Translated "Save Query".
   * 
   * @return translated "Save Query"
  
   */
  @DefaultMessage("Save Query")
  @Key("saveQuery")
  String saveQuery();

  /**
   * Translated "Saving...".
   * 
   * @return translated "Saving..."
  
   */
  @DefaultMessage("Saving...")
  @Key("saving")
  String saving();

  /**
   * Translated "Saving...Complete".
   * 
   * @return translated "Saving...Complete"
  
   */
  @DefaultMessage("Saving...Complete")
  @Key("savingComplete")
  String savingComplete();

  /**
   * Translated "1. Scan in the barcode for the sample accession number to be verified.".
   * 
   * @return translated "1. Scan in the barcode for the sample accession number to be verified."
  
   */
  @DefaultMessage("1. Scan in the barcode for the sample accession number to be verified.")
  @Key("scanSampleAccessionBarcode")
  String scanSampleAccessionBarcode();

  /**
   * Translated "1. Scan in the barcode for the shipping record.".
   * 
   * @return translated "1. Scan in the barcode for the shipping record."
  
   */
  @DefaultMessage("1. Scan in the barcode for the shipping record.")
  @Key("scanShippingBarcode")
  String scanShippingBarcode();

  /**
   * Translated "2. Scan in one or more tracking numbers associated with that record.".
   * 
   * @return translated "2. Scan in one or more tracking numbers associated with that record."
  
   */
  @DefaultMessage("2. Scan in one or more tracking numbers associated with that record.")
  @Key("scanTrackingBarcode")
  String scanTrackingBarcode();

  /**
   * Translated "You do not have permission to access {0}".
   * 
   * @param arg0 "{0}"
   * @return translated "You do not have permission to access {0}"
  
   */
  @DefaultMessage("You do not have permission to access {0}")
  @Key("screenPermException")
  String screenPermException(String arg0);

  /**
   * Translated "Scriptlet".
   * 
   * @return translated "Scriptlet"
  
   */
  @DefaultMessage("Scriptlet")
  @Key("scriptlet")
  String scriptlet();

  /**
   * Translated "Define action scripts that can be assigned to different processes.".
   * 
   * @return translated "Define action scripts that can be assigned to different processes."
  
   */
  @DefaultMessage("Define action scripts that can be assigned to different processes.")
  @Key("scriptletDescription")
  String scriptletDescription();

  /**
   * Translated "1 SD".
   * 
   * @return translated "1 SD"
  
   */
  @DefaultMessage("1 SD")
  @Key("sd")
  String sd();

  /**
   * Translated "SDWIS".
   * 
   * @return translated "SDWIS"
  
   */
  @DefaultMessage("SDWIS")
  @Key("sdwis")
  String sdwis();

  /**
   * Translated "SDWIS Final Report".
   * 
   * @return translated "SDWIS Final Report"
  
   */
  @DefaultMessage("SDWIS Final Report")
  @Key("sdwisFinalReport")
  String sdwisFinalReport();

  /**
   * Translated "SDWIS Info".
   * 
   * @return translated "SDWIS Info"
  
   */
  @DefaultMessage("SDWIS Info")
  @Key("sdwisInfo")
  String sdwisInfo();

  /**
   * Translated "SDWIS Sample".
   * 
   * @return translated "SDWIS Sample"
  
   */
  @DefaultMessage("SDWIS Sample")
  @Key("sdwisSample")
  String sdwisSample();

  /**
   * Translated "SDWIS Sample Login".
   * 
   * @return translated "SDWIS Sample Login"
  
   */
  @DefaultMessage("SDWIS Sample Login")
  @Key("sdwisSampleLogin")
  String sdwisSampleLogin();

  /**
   * Translated "Fully login safe drinking water sample and analysis related information.".
   * 
   * @return translated "Fully login safe drinking water sample and analysis related information."
  
   */
  @DefaultMessage("Fully login safe drinking water sample and analysis related information.")
  @Key("sdwisSampleLoginDescription")
  String sdwisSampleLoginDescription();

  /**
   * Translated "No abnormal test results were found".
   * 
   * @return translated "No abnormal test results were found"
  
   */
  @DefaultMessage("No abnormal test results were found")
  @Key("sdwisScan.noAbnormalResultsFoundException")
  String sdwisScan_noAbnormalResultsFoundException();

  /**
   * Translated "Accession # {0,number,#0}: Could not create order from sample because it has no organizations".
   * 
   * @param arg0 "{0,number,#0}"
   * @return translated "Accession # {0,number,#0}: Could not create order from sample because it has no organizations"
  
   */
  @DefaultMessage("Accession # {0,number,#0}: Could not create order from sample because it has no organizations")
  @Key("sdwisScan.noSampleOrgsException")
  String sdwisScan_noSampleOrgsException(Integer arg0);

  /**
   * Translated "No valid monitor was found for violation: {0}".
   * 
   * @param arg0 "{0}"
   * @return translated "No valid monitor was found for violation: {0}"
  
   */
  @DefaultMessage("No valid monitor was found for violation: {0}")
  @Key("sdwisScan.noValidMonitorWarning")
  String sdwisScan_noValidMonitorWarning(String arg0);

  /**
   * Translated "SDWIS Unload".
   * 
   * @return translated "SDWIS Unload"
  
   */
  @DefaultMessage("SDWIS Unload")
  @Key("sdwisUnloadReport")
  String sdwisUnloadReport();

  /**
   * Translated "Search".
   * 
   * @return translated "Search"
  
   */
  @DefaultMessage("Search")
  @Key("search")
  String search();

  /**
   * Translated "Second Entry".
   * 
   * @return translated "Second Entry"
  
   */
  @DefaultMessage("Second Entry")
  @Key("secondEntry")
  String secondEntry();

  /**
   * Translated "Re-enter sample information to validate initial entry.".
   * 
   * @return translated "Re-enter sample information to validate initial entry."
  
   */
  @DefaultMessage("Re-enter sample information to validate initial entry.")
  @Key("secondEntryDescription")
  String secondEntryDescription();

  /**
   * Translated "Please correct the errors indicated, then press Print".
   * 
   * @return translated "Please correct the errors indicated, then press Print"
  
   */
  @DefaultMessage("Please correct the errors indicated, then press Print")
  @Key("secondaryLabel.correctErrorsPrint")
  String secondaryLabel_correctErrorsPrint();

  /**
   * Translated "Print additional labels for analyses.".
   * 
   * @return translated "Print additional labels for analyses."
  
   */
  @DefaultMessage("Print additional labels for analyses.")
  @Key("secondaryLabel.description")
  String secondaryLabel_description();

  /**
   * Translated "Some errors occurred while printing labels".
   * 
   * @return translated "Some errors occurred while printing labels"
  
   */
  @DefaultMessage("Some errors occurred while printing labels")
  @Key("secondaryLabel.errorsPrintingLabels")
  String secondaryLabel_errorsPrintingLabels();

  /**
   * Translated "Accession # {0,number,#0} : Failed to fetch test record with id {1,number,#0}".
   * 
   * @param arg0 "{0,number,#0}"
   * @param arg1 "{1,number,#0}"
   * @return translated "Accession # {0,number,#0} : Failed to fetch test record with id {1,number,#0}"
  
   */
  @DefaultMessage("Accession # {0,number,#0} : Failed to fetch test record with id {1,number,#0}")
  @Key("secondaryLabel.failedToFetchTestException")
  String secondaryLabel_failedToFetchTestException(Integer arg0,  Integer arg1);

  /**
   * Translated "Accession # {0,number,#0} : {1}, {2} - No label type defined".
   * 
   * @param arg0 "{0,number,#0}"
   * @param arg1 "{1}"
   * @param arg2 "{2}"
   * @return translated "Accession # {0,number,#0} : {1}, {2} - No label type defined"
  
   */
  @DefaultMessage("Accession # {0,number,#0} : {1}, {2} - No label type defined")
  @Key("secondaryLabel.labelNotDefinedException")
  String secondaryLabel_labelNotDefinedException(Integer arg0,  String arg1,  String arg2);

  /**
   * Translated "Accession # {0,number,#0} : {1}, {2} - Label quantity must be at least one".
   * 
   * @param arg0 "{0,number,#0}"
   * @param arg1 "{1}"
   * @param arg2 "{2}"
   * @return translated "Accession # {0,number,#0} : {1}, {2} - Label quantity must be at least one"
  
   */
  @DefaultMessage("Accession # {0,number,#0} : {1}, {2} - Label quantity must be at least one")
  @Key("secondaryLabel.labelQtyLessThanOneException")
  String secondaryLabel_labelQtyLessThanOneException(Integer arg0,  String arg1,  String arg2);

  /**
   * Translated "There are no labels to print".
   * 
   * @return translated "There are no labels to print"
  
   */
  @DefaultMessage("There are no labels to print")
  @Key("secondaryLabel.noLabelsToPrintException")
  String secondaryLabel_noLabelsToPrintException();

  /**
   * Translated "Accession # {0,number,#0} : {1}, {2} - The sample does not have a patient".
   * 
   * @param arg0 "{0,number,#0}"
   * @param arg1 "{1}"
   * @param arg2 "{2}"
   * @return translated "Accession # {0,number,#0} : {1}, {2} - The sample does not have a patient"
  
   */
  @DefaultMessage("Accession # {0,number,#0} : {1}, {2} - The sample does not have a patient")
  @Key("secondaryLabel.noPatientOnSampleException")
  String secondaryLabel_noPatientOnSampleException(Integer arg0,  String arg1,  String arg2);

  /**
   * Translated "Please select a test".
   * 
   * @return translated "Please select a test"
  
   */
  @DefaultMessage("Please select a test")
  @Key("secondaryLabel.pleaseSelectTest")
  String secondaryLabel_pleaseSelectTest();

  /**
   * Translated "You must specify a printer for this report".
   * 
   * @return translated "You must specify a printer for this report"
  
   */
  @DefaultMessage("You must specify a printer for this report")
  @Key("secondaryLabel.printerUnspecifiedException")
  String secondaryLabel_printerUnspecifiedException();

  /**
   * Translated "A sample record with id {0,number,#0} does not exist".
   * 
   * @param arg0 "{0,number,#0}"
   * @return translated "A sample record with id {0,number,#0} does not exist"
  
   */
  @DefaultMessage("A sample record with id {0,number,#0} does not exist")
  @Key("secondaryLabel.sampleNotExistException")
  String secondaryLabel_sampleNotExistException(Integer arg0);

  /**
   * Translated "Secondary Labels".
   * 
   * @return translated "Secondary Labels"
  
   */
  @DefaultMessage("Secondary Labels")
  @Key("secondaryLabel.secondaryLabels")
  String secondaryLabel_secondaryLabels();

  /**
   * Translated "Accession # {0,number,#0} : A test record with id {1,number,#0} does not exist".
   * 
   * @param arg0 "{0,number,#0}"
   * @param arg1 "{1,number,#0}"
   * @return translated "Accession # {0,number,#0} : A test record with id {1,number,#0} does not exist"
  
   */
  @DefaultMessage("Accession # {0,number,#0} : A test record with id {1,number,#0} does not exist")
  @Key("secondaryLabel.testNotExistException")
  String secondaryLabel_testNotExistException(Integer arg0,  Integer arg1);

  /**
   * Translated "The test is either not on the sample or is cancelled".
   * 
   * @return translated "The test is either not on the sample or is cancelled"
  
   */
  @DefaultMessage("The test is either not on the sample or is cancelled")
  @Key("secondaryLabel.testNotOnSampleException")
  String secondaryLabel_testNotOnSampleException();

  /**
   * Translated "Accession # {0,number,#0} : {1}, {2} - Unknown label type defined".
   * 
   * @param arg0 "{0,number,#0}"
   * @param arg1 "{1}"
   * @param arg2 "{2}"
   * @return translated "Accession # {0,number,#0} : {1}, {2} - Unknown label type defined"
  
   */
  @DefaultMessage("Accession # {0,number,#0} : {1}, {2} - Unknown label type defined")
  @Key("secondaryLabel.unknownLabelTypeException")
  String secondaryLabel_unknownLabelTypeException(Integer arg0,  String arg1,  String arg2);

  /**
   * Translated "A section cannot be made its own parent section".
   * 
   * @return translated "A section cannot be made its own parent section"
  
   */
  @DefaultMessage("A section cannot be made its own parent section")
  @Key("sectItsOwnParentException")
  String sectItsOwnParentException();

  /**
   * Translated "Section".
   * 
   * @return translated "Section"
  
   */
  @DefaultMessage("Section")
  @Key("section")
  String section();

  /**
   * Translated "History - Section".
   * 
   * @return translated "History - Section"
  
   */
  @DefaultMessage("History - Section")
  @Key("sectionHistory")
  String sectionHistory();

  /**
   * Translated "Section Name:".
   * 
   * @return translated "Section Name:"
  
   */
  @DefaultMessage("Section Name:")
  @Key("sectionName")
  String sectionName();

  /**
   * Translated "History - Parameter".
   * 
   * @return translated "History - Parameter"
  
   */
  @DefaultMessage("History - Parameter")
  @Key("sectionParameterHistory")
  String sectionParameterHistory();

  /**
   * Translated "You do not have permission to {0} for section {1}".
   * 
   * @param arg0 "{0}"
   * @param arg1 "{1}"
   * @return translated "You do not have permission to {0} for section {1}"
  
   */
  @DefaultMessage("You do not have permission to {0} for section {1}")
  @Key("sectionPermException")
  String sectionPermException(String arg0,  String arg1);

  /**
   * Translated "Sections".
   * 
   * @return translated "Sections"
  
   */
  @DefaultMessage("Sections")
  @Key("sections")
  String sections();

  /**
   * Translated "To select this value please select its analyte".
   * 
   * @return translated "To select this value please select its analyte"
  
   */
  @DefaultMessage("To select this value please select its analyte")
  @Key("selAnaToSelVal")
  String selAnaToSelVal();

  /**
   * Translated "Please select at least one common or domain field".
   * 
   * @return translated "Please select at least one common or domain field"
  
   */
  @DefaultMessage("Please select at least one common or domain field")
  @Key("selAtleastOneField")
  String selAtleastOneField();

  /**
   * Translated "Please select fields from only one domain".
   * 
   * @return translated "Please select fields from only one domain"
  
   */
  @DefaultMessage("Please select fields from only one domain")
  @Key("selFieldsOneDomain")
  String selFieldsOneDomain();

  /**
   * Translated "Please select a ''From Item''".
   * 
   * @return translated "Please select a ''From Item''"
  
   */
  @DefaultMessage("Please select a ''From Item''")
  @Key("selFromItem")
  String selFromItem();

  /**
   * Translated "Please select exactly one row to unrelease".
   * 
   * @return translated "Please select exactly one row to unrelease"
  
   */
  @DefaultMessage("Please select exactly one row to unrelease")
  @Key("selOneRowUnrelease")
  String selOneRowUnrelease();

  /**
   * Translated "Please select one or more rows to transfer".
   * 
   * @return translated "Please select one or more rows to transfer"
  
   */
  @DefaultMessage("Please select one or more rows to transfer")
  @Key("selRowsToTransfer")
  String selRowsToTransfer();

  /**
   * Translated "Please select a ''To Item''".
   * 
   * @return translated "Please select a ''To Item''"
  
   */
  @DefaultMessage("Please select a ''To Item''")
  @Key("selToItem")
  String selToItem();

  /**
   * Translated "Select".
   * 
   * @return translated "Select"
  
   */
  @DefaultMessage("Select")
  @Key("select")
  String select();

  /**
   * Translated "Select All".
   * 
   * @return translated "Select All"
  
   */
  @DefaultMessage("Select All")
  @Key("selectAll")
  String selectAll();

  /**
   * Translated "Please select a valid analyte before selecting a result.".
   * 
   * @return translated "Please select a valid analyte before selecting a result."
  
   */
  @DefaultMessage("Please select a valid analyte before selecting a result.")
  @Key("selectAnaBeforeRes")
  String selectAnaBeforeRes();

  /**
   * Translated "Select Analysis Fields For Output".
   * 
   * @return translated "Select Analysis Fields For Output"
  
   */
  @DefaultMessage("Select Analysis Fields For Output")
  @Key("selectAnalysisField")
  String selectAnalysisField();

  /**
   * Translated "Select Category".
   * 
   * @return translated "Select Category"
  
   */
  @DefaultMessage("Select Category")
  @Key("selectCategory")
  String selectCategory();

  /**
   * Translated "Select Environmental Fields For Output".
   * 
   * @return translated "Select Environmental Fields For Output"
  
   */
  @DefaultMessage("Select Environmental Fields For Output")
  @Key("selectEnvironmentalField")
  String selectEnvironmentalField();

  /**
   * Translated "Please select an item".
   * 
   * @return translated "Please select an item"
  
   */
  @DefaultMessage("Please select an item")
  @Key("selectItem")
  String selectItem();

  /**
   * Translated "Please select at least one test analyte or aux data".
   * 
   * @return translated "Please select at least one test analyte or aux data"
  
   */
  @DefaultMessage("Please select at least one test analyte or aux data")
  @Key("selectOneAnaOrAux")
  String selectOneAnaOrAux();

  /**
   * Translated "Please select one or more analyses".
   * 
   * @return translated "Please select one or more analyses"
  
   */
  @DefaultMessage("Please select one or more analyses")
  @Key("selectOneOrMoreAnalyses")
  String selectOneOrMoreAnalyses();

  /**
   * Translated "Select Organization Fields For Output".
   * 
   * @return translated "Select Organization Fields For Output"
  
   */
  @DefaultMessage("Select Organization Fields For Output")
  @Key("selectOrganizationField")
  String selectOrganizationField();

  /**
   * Translated "Select Private Well Fields For Output".
   * 
   * @return translated "Select Private Well Fields For Output"
  
   */
  @DefaultMessage("Select Private Well Fields For Output")
  @Key("selectPrivateWellField")
  String selectPrivateWellField();

  /**
   * Translated "Please select a record to update first".
   * 
   * @return translated "Please select a record to update first"
  
   */
  @DefaultMessage("Please select a record to update first")
  @Key("selectRecordToUpdate")
  String selectRecordToUpdate();

  /**
   * Translated "Select Sample Fields For Output".
   * 
   * @return translated "Select Sample Fields For Output"
  
   */
  @DefaultMessage("Select Sample Fields For Output")
  @Key("selectSampleField")
  String selectSampleField();

  /**
   * Translated "Select Sample Item Fields For Output".
   * 
   * @return translated "Select Sample Item Fields For Output"
  
   */
  @DefaultMessage("Select Sample Item Fields For Output")
  @Key("selectSampleItemField")
  String selectSampleItemField();

  /**
   * Translated "Select SDWIS Fields For Output  ".
   * 
   * @return translated "Select SDWIS Fields For Output  "
  
   */
  @DefaultMessage("Select SDWIS Fields For Output  ")
  @Key("selectSdwisField")
  String selectSdwisField();

  /**
   * Translated "Send-out".
   * 
   * @return translated "Send-out"
  
   */
  @DefaultMessage("Send-out")
  @Key("sendOut")
  String sendOut();

  /**
   * Translated "Send-out Order".
   * 
   * @return translated "Send-out Order"
  
   */
  @DefaultMessage("Send-out Order")
  @Key("sendoutOrder")
  String sendoutOrder();

  /**
   * Translated "Order items and supplies to be sent to external users.".
   * 
   * @return translated "Order items and supplies to be sent to external users."
  
   */
  @DefaultMessage("Order items and supplies to be sent to external users.")
  @Key("sendoutOrderDescription")
  String sendoutOrderDescription();

  /**
   * Translated "Sequence".
   * 
   * @return translated "Sequence"
  
   */
  @DefaultMessage("Sequence")
  @Key("sequence")
  String sequence();

  /**
   * Translated "Serial #".
   * 
   * @return translated "Serial #"
  
   */
  @DefaultMessage("Serial #")
  @Key("serialNum")
  String serialNum();

  /**
   * Translated "Serial #".
   * 
   * @return translated "Serial #"
  
   */
  @DefaultMessage("Serial #")
  @Key("serialNumber")
  String serialNumber();

  /**
   * Translated "Serial # Required".
   * 
   * @return translated "Serial # Required"
  
   */
  @DefaultMessage("Serial # Required")
  @Key("serialRequired")
  String serialRequired();

  /**
   * Translated "Series".
   * 
   * @return translated "Series"
  
   */
  @DefaultMessage("Series")
  @Key("series")
  String series();

  /**
   * Translated "Ship From".
   * 
   * @return translated "Ship From"
  
   */
  @DefaultMessage("Ship From")
  @Key("shipFrom")
  String shipFrom();

  /**
   * Translated "Shipped from and shipped to need to match on all rows selected".
   * 
   * @return translated "Shipped from and shipped to need to match on all rows selected"
  
   */
  @DefaultMessage("Shipped from and shipped to need to match on all rows selected")
  @Key("shipFromshipToInvalidException")
  String shipFromshipToInvalidException();

  /**
   * Translated "Ship To".
   * 
   * @return translated "Ship To"
  
   */
  @DefaultMessage("Ship To")
  @Key("shipTo")
  String shipTo();

  /**
   * Translated "Ship To Address".
   * 
   * @return translated "Ship To Address"
  
   */
  @DefaultMessage("Ship To Address")
  @Key("shipToAddress")
  String shipToAddress();

  /**
   * Translated "Ship To/Requested By".
   * 
   * @return translated "Ship To/Requested By"
  
   */
  @DefaultMessage("Ship To/Requested By")
  @Key("shipToRequestedBy")
  String shipToRequestedBy();

  /**
   * Translated "Shipped Date".
   * 
   * @return translated "Shipped Date"
  
   */
  @DefaultMessage("Shipped Date")
  @Key("shippedDate")
  String shippedDate();

  /**
   * Translated "Shipped From".
   * 
   * @return translated "Shipped From"
  
   */
  @DefaultMessage("Shipped From")
  @Key("shippedFrom")
  String shippedFrom();

  /**
   * Translated "Shipped Method".
   * 
   * @return translated "Shipped Method"
  
   */
  @DefaultMessage("Shipped Method")
  @Key("shippedMethod")
  String shippedMethod();

  /**
   * Translated "Shipped To".
   * 
   * @return translated "Shipped To"
  
   */
  @DefaultMessage("Shipped To")
  @Key("shippedTo")
  String shippedTo();

  /**
   * Translated "Shipping".
   * 
   * @return translated "Shipping"
  
   */
  @DefaultMessage("Shipping")
  @Key("shipping")
  String shipping();

  /**
   * Translated "View, manage, and complete the shipping process.".
   * 
   * @return translated "View, manage, and complete the shipping process."
  
   */
  @DefaultMessage("View, manage, and complete the shipping process.")
  @Key("shippingDescription")
  String shippingDescription();

  /**
   * Translated "History - Shipping".
   * 
   * @return translated "History - Shipping"
  
   */
  @DefaultMessage("History - Shipping")
  @Key("shippingHistory")
  String shippingHistory();

  /**
   * Translated "Shipping Information".
   * 
   * @return translated "Shipping Information"
  
   */
  @DefaultMessage("Shipping Information")
  @Key("shippingInfo")
  String shippingInfo();

  /**
   * Translated "History - Item".
   * 
   * @return translated "History - Item"
  
   */
  @DefaultMessage("History - Item")
  @Key("shippingItemHistory")
  String shippingItemHistory();

  /**
   * Translated "Shipping Notes".
   * 
   * @return translated "Shipping Notes"
  
   */
  @DefaultMessage("Shipping Notes")
  @Key("shippingNotes")
  String shippingNotes();

  /**
   * Translated "Shipping Report".
   * 
   * @return translated "Shipping Report"
  
   */
  @DefaultMessage("Shipping Report")
  @Key("shippingReport")
  String shippingReport();

  /**
   * Translated "No changes were made because the shipment was aborted.".
   * 
   * @return translated "No changes were made because the shipment was aborted."
  
   */
  @DefaultMessage("No changes were made because the shipment was aborted.")
  @Key("shippingScreenAbort")
  String shippingScreenAbort();

  /**
   * Translated "This record has been already processed. Are you sure you want to upate it?".
   * 
   * @return translated "This record has been already processed. Are you sure you want to upate it?"
  
   */
  @DefaultMessage("This record has been already processed. Are you sure you want to upate it?")
  @Key("shippingStatusProcessed")
  String shippingStatusProcessed();

  /**
   * Translated "This record has been marked as shipped. Are you sure you want to upate it?".
   * 
   * @return translated "This record has been marked as shipped. Are you sure you want to upate it?"
  
   */
  @DefaultMessage("This record has been marked as shipped. Are you sure you want to upate it?")
  @Key("shippingStatusShipped")
  String shippingStatusShipped();

  /**
   * Translated "History - Tracking".
   * 
   * @return translated "History - Tracking"
  
   */
  @DefaultMessage("History - Tracking")
  @Key("shippingTrackingHistory")
  String shippingTrackingHistory();

  /**
   * Translated "Show Analysis".
   * 
   * @return translated "Show Analysis"
  
   */
  @DefaultMessage("Show Analysis")
  @Key("showAnalysis")
  String showAnalysis();

  /**
   * Translated "Show Dates".
   * 
   * @return translated "Show Dates"
  
   */
  @DefaultMessage("Show Dates")
  @Key("showDates")
  String showDates();

  /**
   * Translated "Show Manifest".
   * 
   * @return translated "Show Manifest"
  
   */
  @DefaultMessage("Show Manifest")
  @Key("showManifest")
  String showManifest();

  /**
   * Translated "Show my section(s) only".
   * 
   * @return translated "Show my section(s) only"
  
   */
  @DefaultMessage("Show my section(s) only")
  @Key("showMySectOnly")
  String showMySectOnly();

  /**
   * Translated "S.D.".
   * 
   * @return translated "S.D."
  
   */
  @DefaultMessage("S.D.")
  @Key("significantDigits")
  String significantDigits();

  /**
   * Translated "Sign In".
   * 
   * @return translated "Sign In"
  
   */
  @DefaultMessage("Sign In")
  @Key("signin")
  String signin();

  /**
   * Translated "Similar".
   * 
   * @return translated "Similar"
  
   */
  @DefaultMessage("Similar")
  @Key("similar")
  String similar();

  /**
   * Translated "Singular".
   * 
   * @return translated "Singular"
  
   */
  @DefaultMessage("Singular")
  @Key("singular")
  String singular();

  /**
   * Translated "6-10 days ago".
   * 
   * @return translated "6-10 days ago"
  
   */
  @DefaultMessage("6-10 days ago")
  @Key("sixToTenDays")
  String sixToTenDays();

  /**
   * Translated "A destination URI beginning with ''socket://'' must be of the format ''socket://hostname:port''".
   * 
   * @return translated "A destination URI beginning with ''socket://'' must be of the format ''socket://hostname:port''"
  
   */
  @DefaultMessage("A destination URI beginning with ''socket://'' must be of the format ''socket://hostname:port''")
  @Key("socketURIMustHaveHostAndPortException")
  String socketURIMustHaveHostAndPortException();

  /**
   * Translated "Sort Order".
   * 
   * @return translated "Sort Order"
  
   */
  @DefaultMessage("Sort Order")
  @Key("sortOrder")
  String sortOrder();

  /**
   * Translated "Sort Method".
   * 
   * @return translated "Sort Method"
  
   */
  @DefaultMessage("Sort Method")
  @Key("sortingMethod")
  String sortingMethod();

  /**
   * Translated "Source".
   * 
   * @return translated "Source"
  
   */
  @DefaultMessage("Source")
  @Key("source")
  String source();

  /**
   * Translated "Source Other".
   * 
   * @return translated "Source Other"
  
   */
  @DefaultMessage("Source Other")
  @Key("sourceOther")
  String sourceOther();

  /**
   * Translated "Please specify the Destination URI".
   * 
   * @return translated "Please specify the Destination URI"
  
   */
  @DefaultMessage("Please specify the Destination URI")
  @Key("specifyDestURI")
  String specifyDestURI();

  /**
   * Translated "St".
   * 
   * @return translated "St"
  
   */
  @DefaultMessage("St")
  @Key("st")
  String st();

  /**
   * Translated "Starting Released Date".
   * 
   * @return translated "Starting Released Date"
  
   */
  @DefaultMessage("Starting Released Date")
  @Key("stRelDt")
  String stRelDt();

  /**
   * Translated "Standard Note".
   * 
   * @return translated "Standard Note"
  
   */
  @DefaultMessage("Standard Note")
  @Key("standardNote")
  String standardNote();

  /**
   * Translated "Create commonly used notes for convenient entry.".
   * 
   * @return translated "Create commonly used notes for convenient entry."
  
   */
  @DefaultMessage("Create commonly used notes for convenient entry.")
  @Key("standardNoteDescription")
  String standardNoteDescription();

  /**
   * Translated "History - Standard Note".
   * 
   * @return translated "History - Standard Note"
  
   */
  @DefaultMessage("History - Standard Note")
  @Key("standardNoteHistory")
  String standardNoteHistory();

  /**
   * Translated "Standard Note Selection".
   * 
   * @return translated "Standard Note Selection"
  
   */
  @DefaultMessage("Standard Note Selection")
  @Key("standardNoteSelection")
  String standardNoteSelection();

  /**
   * Translated "Start Day".
   * 
   * @return translated "Start Day"
  
   */
  @DefaultMessage("Start Day")
  @Key("startDay")
  String startDay();

  /**
   * Translated "Started".
   * 
   * @return translated "Started"
  
   */
  @DefaultMessage("Started")
  @Key("started")
  String started();

  /**
   * Translated "Started Date".
   * 
   * @return translated "Started Date"
  
   */
  @DefaultMessage("Started Date")
  @Key("startedDate")
  String startedDate();

  /**
   * Translated "{0} : {1} - Started date can''t be after completed".
   * 
   * @param arg0 "{0}"
   * @param arg1 "{1}"
   * @return translated "{0} : {1} - Started date can''t be after completed"
  
   */
  @DefaultMessage("{0} : {1} - Started date can''t be after completed")
  @Key("startedDateAfterCompletedError")
  String startedDateAfterCompletedError(String arg0,  String arg1);

  /**
   * Translated "{0} : {1} - Started date is before available date".
   * 
   * @param arg0 "{0}"
   * @param arg1 "{1}"
   * @return translated "{0} : {1} - Started date is before available date"
  
   */
  @DefaultMessage("{0} : {1} - Started date is before available date")
  @Key("startedDateBeforeAvailableCaution")
  String startedDateBeforeAvailableCaution(String arg0,  String arg1);

  /**
   * Translated "{0} : {1} - Started date can''t be in the future".
   * 
   * @param arg0 "{0}"
   * @param arg1 "{1}"
   * @return translated "{0} : {1} - Started date can''t be in the future"
  
   */
  @DefaultMessage("{0} : {1} - Started date can''t be in the future")
  @Key("startedDateInFutureError")
  String startedDateInFutureError(String arg0,  String arg1);

  /**
   * Translated "Stat Type".
   * 
   * @return translated "Stat Type"
  
   */
  @DefaultMessage("Stat Type")
  @Key("statType")
  String statType();

  /**
   * Translated "State".
   * 
   * @return translated "State"
  
   */
  @DefaultMessage("State")
  @Key("state")
  String state();

  /**
   * Translated "State ID".
   * 
   * @return translated "State ID"
  
   */
  @DefaultMessage("State ID")
  @Key("stateId")
  String stateId();

  /**
   * Translated "State Lab #".
   * 
   * @return translated "State Lab #"
  
   */
  @DefaultMessage("State Lab #")
  @Key("stateLabNo")
  String stateLabNo();

  /**
   * Translated "State Lab Number is required".
   * 
   * @return translated "State Lab Number is required"
  
   */
  @DefaultMessage("State Lab Number is required")
  @Key("stateLabNumRequiredException")
  String stateLabNumRequiredException();

  /**
   * Translated "State Type".
   * 
   * @return translated "State Type"
  
   */
  @DefaultMessage("State Type")
  @Key("stateType")
  String stateType();

  /**
   * Translated "Plot Statistic".
   * 
   * @return translated "Plot Statistic"
  
   */
  @DefaultMessage("Plot Statistic")
  @Key("statistic")
  String statistic();

  /**
   * Translated "Status".
   * 
   * @return translated "Status"
  
   */
  @DefaultMessage("Status")
  @Key("status")
  String status();

  /**
   * Translated "Status must be either \"Processed\" or \"Shipped\" to process shipping".
   * 
   * @return translated "Status must be either \"Processed\" or \"Shipped\" to process shipping"
  
   */
  @DefaultMessage("Status must be either \"Processed\" or \"Shipped\" to process shipping")
  @Key("statusProcessedShipped")
  String statusProcessedShipped();

  /**
   * Translated "Storage".
   * 
   * @return translated "Storage"
  
   */
  @DefaultMessage("Storage")
  @Key("storage")
  String storage();

  /**
   * Translated "Check out date must not be before check in date".
   * 
   * @return translated "Check out date must not be before check in date"
  
   */
  @DefaultMessage("Check out date must not be before check in date")
  @Key("storage.invalidDateRangeException")
  String storage_invalidDateRangeException();

  /**
   * Translated "Storage".
   * 
   * @return translated "Storage"
  
   */
  @DefaultMessage("Storage")
  @Key("storage.storage")
  String storage_storage();

  /**
   * Translated "Manage and track stored items in the laboratory by location.".
   * 
   * @return translated "Manage and track stored items in the laboratory by location."
  
   */
  @DefaultMessage("Manage and track stored items in the laboratory by location.")
  @Key("storageDescription")
  String storageDescription();

  /**
   * Translated "Storage location cannot be deleted, one or more storage locations are still linked to it".
   * 
   * @return translated "Storage location cannot be deleted, one or more storage locations are still linked to it"
  
   */
  @DefaultMessage("Storage location cannot be deleted, one or more storage locations are still linked to it")
  @Key("storageLocDeleteException")
  String storageLocDeleteException();

  /**
   * Translated "Storage location is required ".
   * 
   * @return translated "Storage location is required "
  
   */
  @DefaultMessage("Storage location is required ")
  @Key("storageLocReqForItemException")
  String storageLocReqForItemException();

  /**
   * Translated "Storage Location".
   * 
   * @return translated "Storage Location"
  
   */
  @DefaultMessage("Storage Location")
  @Key("storageLocation")
  String storageLocation();

  /**
   * Translated "One or more storage locations cannot be deleted, other records are still linked to them".
   * 
   * @return translated "One or more storage locations cannot be deleted, other records are still linked to them"
  
   */
  @DefaultMessage("One or more storage locations cannot be deleted, other records are still linked to them")
  @Key("storageLocationDeleteException")
  String storageLocationDeleteException();

  /**
   * Translated "Define places that can be used in the laboratory. (eg., Virology Freezer #1)".
   * 
   * @return translated "Define places that can be used in the laboratory. (eg., Virology Freezer #1)"
  
   */
  @DefaultMessage("Define places that can be used in the laboratory. (eg., Virology Freezer #1)")
  @Key("storageLocationDescription")
  String storageLocationDescription();

  /**
   * Translated "History - Storage Location".
   * 
   * @return translated "History - Storage Location"
  
   */
  @DefaultMessage("History - Storage Location")
  @Key("storageLocationHistory")
  String storageLocationHistory();

  /**
   * Translated "Storage location cannot be deleted, one or more inventory locations are still linked to it".
   * 
   * @return translated "Storage location cannot be deleted, one or more inventory locations are still linked to it"
  
   */
  @DefaultMessage("Storage location cannot be deleted, one or more inventory locations are still linked to it")
  @Key("storageLocationInventoryLocationDeleteException")
  String storageLocationInventoryLocationDeleteException();

  /**
   * Translated "Storage Location/Item".
   * 
   * @return translated "Storage Location/Item"
  
   */
  @DefaultMessage("Storage Location/Item")
  @Key("storageLocationItem")
  String storageLocationItem();

  /**
   * Translated "Storage Location Name".
   * 
   * @return translated "Storage Location Name"
  
   */
  @DefaultMessage("Storage Location Name")
  @Key("storageLocationName")
  String storageLocationName();

  /**
   * Translated "Storage Location Selection".
   * 
   * @return translated "Storage Location Selection"
  
   */
  @DefaultMessage("Storage Location Selection")
  @Key("storageLocationSelection")
  String storageLocationSelection();

  /**
   * Translated "Storage location cannot be deleted, one or more storage records are still linked to it".
   * 
   * @return translated "Storage location cannot be deleted, one or more storage records are still linked to it"
  
   */
  @DefaultMessage("Storage location cannot be deleted, one or more storage records are still linked to it")
  @Key("storageLocationStorageDeleteException")
  String storageLocationStorageDeleteException();

  /**
   * Translated "Storage needs to be checked out before another row is added".
   * 
   * @return translated "Storage needs to be checked out before another row is added"
  
   */
  @DefaultMessage("Storage needs to be checked out before another row is added")
  @Key("storageNotCheckedOutException")
  String storageNotCheckedOutException();

  /**
   * Translated "Storage Sub Unit".
   * 
   * @return translated "Storage Sub Unit"
  
   */
  @DefaultMessage("Storage Sub Unit")
  @Key("storageSubUnit")
  String storageSubUnit();

  /**
   * Translated "Storage Unit".
   * 
   * @return translated "Storage Unit"
  
   */
  @DefaultMessage("Storage Unit")
  @Key("storageUnit")
  String storageUnit();

  /**
   * Translated "Storage unit cannot be deleted, one or more storage locations are still linked to it".
   * 
   * @return translated "Storage unit cannot be deleted, one or more storage locations are still linked to it"
  
   */
  @DefaultMessage("Storage unit cannot be deleted, one or more storage locations are still linked to it")
  @Key("storageUnitDeleteException")
  String storageUnitDeleteException();

  /**
   * Translated "Define elements of storage that can be used in storage locations. (eg., -70 degree freezer)".
   * 
   * @return translated "Define elements of storage that can be used in storage locations. (eg., -70 degree freezer)"
  
   */
  @DefaultMessage("Define elements of storage that can be used in storage locations. (eg., -70 degree freezer)")
  @Key("storageUnitDescription")
  String storageUnitDescription();

  /**
   * Translated "History - Storage Unit".
   * 
   * @return translated "History - Storage Unit"
  
   */
  @DefaultMessage("History - Storage Unit")
  @Key("storageUnitHistory")
  String storageUnitHistory();

  /**
   * Translated "Storage Units".
   * 
   * @return translated "Storage Units"
  
   */
  @DefaultMessage("Storage Units")
  @Key("storageUnits")
  String storageUnits();

  /**
   * Translated "Store".
   * 
   * @return translated "Store"
  
   */
  @DefaultMessage("Store")
  @Key("store")
  String store();

  /**
   * Translated "Street".
   * 
   * @return translated "Street"
  
   */
  @DefaultMessage("Street")
  @Key("street")
  String street();

  /**
   * Translated "Sub-Assembly".
   * 
   * @return translated "Sub-Assembly"
  
   */
  @DefaultMessage("Sub-Assembly")
  @Key("subAssembly")
  String subAssembly();

  /**
   * Translated "History - Sub Location".
   * 
   * @return translated "History - Sub Location"
  
   */
  @DefaultMessage("History - Sub Location")
  @Key("subLocationHistory")
  String subLocationHistory();

  /**
   * Translated "Subject".
   * 
   * @return translated "Subject"
  
   */
  @DefaultMessage("Subject")
  @Key("subject")
  String subject();

  /**
   * Translated "Subset Capacity".
   * 
   * @return translated "Subset Capacity"
  
   */
  @DefaultMessage("Subset Capacity")
  @Key("subsetCapacity")
  String subsetCapacity();

  /**
   * Translated "Subset Capacity must be greater than zero".
   * 
   * @return translated "Subset Capacity must be greater than zero"
  
   */
  @DefaultMessage("Subset Capacity must be greater than zero")
  @Key("subsetCapacityMoreThanZeroException")
  String subsetCapacityMoreThanZeroException();

  /**
   * Translated "Suggestions".
   * 
   * @return translated "Suggestions"
  
   */
  @DefaultMessage("Suggestions")
  @Key("suggestions")
  String suggestions();

  /**
   * Translated "Sum of quantities less than quantity ordered".
   * 
   * @return translated "Sum of quantities less than quantity ordered"
  
   */
  @DefaultMessage("Sum of quantities less than quantity ordered")
  @Key("sumOfQtyLessThanQtyOrderedException")
  String sumOfQtyLessThanQtyOrderedException();

  /**
   * Translated "Sum of quantities exceeds quantity ordered".
   * 
   * @return translated "Sum of quantities exceeds quantity ordered"
  
   */
  @DefaultMessage("Sum of quantities exceeds quantity ordered")
  @Key("sumOfQtyMoreThanQtyOrderedException")
  String sumOfQtyMoreThanQtyOrderedException();

  /**
   * Translated "Summary".
   * 
   * @return translated "Summary"
  
   */
  @DefaultMessage("Summary")
  @Key("summary")
  String summary();

  /**
   * Translated "System".
   * 
   * @return translated "System"
  
   */
  @DefaultMessage("System")
  @Key("system")
  String system();

  /**
   * Translated "System Name".
   * 
   * @return translated "System Name"
  
   */
  @DefaultMessage("System Name")
  @Key("systemName")
  String systemName();

  /**
   * Translated "System Variable".
   * 
   * @return translated "System Variable"
  
   */
  @DefaultMessage("System Variable")
  @Key("systemVariable")
  String systemVariable();

  /**
   * Translated "Missing/invalid system variable ''{0}''".
   * 
   * @param arg0 "{0}"
   * @return translated "Missing/invalid system variable ''{0}''"
  
   */
  @DefaultMessage("Missing/invalid system variable ''{0}''")
  @Key("systemVariable.missingInvalidSystemVariable")
  String systemVariable_missingInvalidSystemVariable(String arg0);

  /**
   * Translated "Define system properties used by OpenELIS administrators.".
   * 
   * @return translated "Define system properties used by OpenELIS administrators."
  
   */
  @DefaultMessage("Define system properties used by OpenELIS administrators.")
  @Key("systemVariableDescription")
  String systemVariableDescription();

  /**
   * Translated "History - SystemVariable".
   * 
   * @return translated "History - SystemVariable"
  
   */
  @DefaultMessage("History - SystemVariable")
  @Key("systemVariableHistory")
  String systemVariableHistory();

  /**
   * Translated "Table Options".
   * 
   * @return translated "Table Options"
  
   */
  @DefaultMessage("Table Options")
  @Key("tableOptions")
  String tableOptions();

  /**
   * Translated "Test".
   * 
   * @return translated "Test"
  
   */
  @DefaultMessage("Test")
  @Key("test")
  String test();

  /**
   * Translated "No matching active test found for ''{0}, {1}''".
   * 
   * @param arg0 "{0}"
   * @param arg1 "{1}"
   * @return translated "No matching active test found for ''{0}, {1}''"
  
   */
  @DefaultMessage("No matching active test found for ''{0}, {1}''")
  @Key("test.inactiveTestException")
  String test_inactiveTestException(String arg0,  String arg1);

  /**
   * Translated "Invalid value for selected type".
   * 
   * @return translated "Invalid value for selected type"
  
   */
  @DefaultMessage("Invalid value for selected type")
  @Key("test.invalidValue")
  String test_invalidValue();

  /**
   * Translated "Test Name".
   * 
   * @return translated "Test Name"
  
   */
  @DefaultMessage("Test Name")
  @Key("test.name")
  String test_name();

  /**
   * Translated "You must first select a table row before clicking add".
   * 
   * @return translated "You must first select a table row before clicking add"
  
   */
  @DefaultMessage("You must first select a table row before clicking add")
  @Key("test.noSelectedRow")
  String test_noSelectedRow();

  /**
   * Translated "Prep Test".
   * 
   * @return translated "Prep Test"
  
   */
  @DefaultMessage("Prep Test")
  @Key("test.prepTest")
  String test_prepTest();

  /**
   * Translated "Test Results".
   * 
   * @return translated "Test Results"
  
   */
  @DefaultMessage("Test Results")
  @Key("test.results")
  String test_results();

  /**
   * Translated "There is already an active test in the system with the same name and method".
   * 
   * @return translated "There is already an active test in the system with the same name and method"
  
   */
  @DefaultMessage("There is already an active test in the system with the same name and method")
  @Key("testActiveException")
  String testActiveException();

  /**
   * Translated "This test has already been added to the panel. Add it anyway?".
   * 
   * @return translated "This test has already been added to the panel. Add it anyway?"
  
   */
  @DefaultMessage("This test has already been added to the panel. Add it anyway?")
  @Key("testAlreadyAdded")
  String testAlreadyAdded();

  /**
   * Translated "Analyte".
   * 
   * @return translated "Analyte"
  
   */
  @DefaultMessage("Analyte")
  @Key("testAnalyte")
  String testAnalyte();

  /**
   * Translated "Filter by Test Analyte and Aux Data".
   * 
   * @return translated "Filter by Test Analyte and Aux Data"
  
   */
  @DefaultMessage("Filter by Test Analyte and Aux Data")
  @Key("testAnalyteAuxDataFilter")
  String testAnalyteAuxDataFilter();

  /**
   * Translated "The test analyte linked to this result record has been deleted".
   * 
   * @return translated "The test analyte linked to this result record has been deleted"
  
   */
  @DefaultMessage("The test analyte linked to this result record has been deleted")
  @Key("testAnalyteDefinitionChanged")
  String testAnalyteDefinitionChanged();

  /**
   * Translated "Test Analyte".
   * 
   * @return translated "Test Analyte"
  
   */
  @DefaultMessage("Test Analyte")
  @Key("testAnalyteHeading")
  String testAnalyteHeading();

  /**
   * Translated "History - Analyte ".
   * 
   * @return translated "History - Analyte "
  
   */
  @DefaultMessage("History - Analyte ")
  @Key("testAnalyteHistory")
  String testAnalyteHistory();

  /**
   * Translated "Test Analyte Selection".
   * 
   * @return translated "Test Analyte Selection"
  
   */
  @DefaultMessage("Test Analyte Selection")
  @Key("testAnalyteSelection")
  String testAnalyteSelection();

  /**
   * Translated "Include".
   * 
   * @return translated "Include"
  
   */
  @DefaultMessage("Include")
  @Key("testAnalyteSelection.include")
  String testAnalyteSelection_include();

  /**
   * Translated "Test Analyte Selection".
   * 
   * @return translated "Test Analyte Selection"
  
   */
  @DefaultMessage("Test Analyte Selection")
  @Key("testAnalyteSelection.testAnalyteSelection")
  String testAnalyteSelection_testAnalyteSelection();

  /**
   * Translated "Test Count by Facility".
   * 
   * @return translated "Test Count by Facility"
  
   */
  @DefaultMessage("Test Count by Facility")
  @Key("testCountByFacility")
  String testCountByFacility();

  /**
   * Translated "Values of type \"Date-Time\" must be unique in the same result group".
   * 
   * @return translated "Values of type \"Date-Time\" must be unique in the same result group"
  
   */
  @DefaultMessage("Values of type \"Date-Time\" must be unique in the same result group")
  @Key("testDateTimeValueNotUniqueException")
  String testDateTimeValueNotUniqueException();

  /**
   * Translated "Values of type \"Date\" must be unique in the same result group".
   * 
   * @return translated "Values of type \"Date\" must be unique in the same result group"
  
   */
  @DefaultMessage("Values of type \"Date\" must be unique in the same result group")
  @Key("testDateValueNotUniqueException")
  String testDateValueNotUniqueException();

  /**
   * Translated "Group {0} cannot have \"Default\" as the only type for a unit".
   * 
   * @param arg0 "{0}"
   * @return translated "Group {0} cannot have \"Default\" as the only type for a unit"
  
   */
  @DefaultMessage("Group {0} cannot have \"Default\" as the only type for a unit")
  @Key("testDefaultWithNoOtherTypeException")
  String testDefaultWithNoOtherTypeException(String arg0);

  /**
   * Translated "Define tests for use in laboratory.".
   * 
   * @return translated "Define tests for use in laboratory."
  
   */
  @DefaultMessage("Define tests for use in laboratory.")
  @Key("testDescription")
  String testDescription();

  /**
   * Translated "Test Details ".
   * 
   * @return translated "Test Details "
  
   */
  @DefaultMessage("Test Details ")
  @Key("testDetails")
  String testDetails();

  /**
   * Translated "Group {0} must have unique \"Dictionary\" values".
   * 
   * @param arg0 "{0}"
   * @return translated "Group {0} must have unique \"Dictionary\" values"
  
   */
  @DefaultMessage("Group {0} must have unique \"Dictionary\" values")
  @Key("testDictEntryNotUniqueException")
  String testDictEntryNotUniqueException(String arg0);

  /**
   * Translated "Format".
   * 
   * @return translated "Format"
  
   */
  @DefaultMessage("Format")
  @Key("testFormat")
  String testFormat();

  /**
   * Translated "History - Test".
   * 
   * @return translated "History - Test"
  
   */
  @DefaultMessage("History - Test")
  @Key("testHistory")
  String testHistory();

  /**
   * Translated "There is already an inactive test in the system with the same name and method".
   * 
   * @return translated "There is already an inactive test in the system with the same name and method"
  
   */
  @DefaultMessage("There is already an inactive test in the system with the same name and method")
  @Key("testInactiveException")
  String testInactiveException();

  /**
   * Translated "Test & Method".
   * 
   * @return translated "Test & Method"
  
   */
  @DefaultMessage("Test & Method")
  @Key("testMethod")
  String testMethod();

  /**
   * Translated "Test, Method, Description".
   * 
   * @return translated "Test, Method, Description"
  
   */
  @DefaultMessage("Test, Method, Description")
  @Key("testMethodDescription")
  String testMethodDescription();

  /**
   * Translated "Test, Method, Sample Type must have a value ".
   * 
   * @return translated "Test, Method, Sample Type must have a value "
  
   */
  @DefaultMessage("Test, Method, Sample Type must have a value ")
  @Key("testMethodNoValueException")
  String testMethodNoValueException();

  /**
   * Translated "Test, Method, Sample Type".
   * 
   * @return translated "Test, Method, Sample Type"
  
   */
  @DefaultMessage("Test, Method, Sample Type")
  @Key("testMethodSampleType")
  String testMethodSampleType();

  /**
   * Translated "The Test ''{0}'' is not valid for Sample Type ''{1}''".
   * 
   * @param arg0 "{0}"
   * @param arg1 "{1}"
   * @return translated "The Test ''{0}'' is not valid for Sample Type ''{1}''"
  
   */
  @DefaultMessage("The Test ''{0}'' is not valid for Sample Type ''{1}''")
  @Key("testMethodSampleTypeMismatch")
  String testMethodSampleTypeMismatch(String arg0,  String arg1);

  /**
   * Translated "Group {0} cannot have more than one \"Alpha\" type".
   * 
   * @param arg0 "{0}"
   * @return translated "Group {0} cannot have more than one \"Alpha\" type"
  
   */
  @DefaultMessage("Group {0} cannot have more than one \"Alpha\" type")
  @Key("testMoreThanOneAlphaTypeException")
  String testMoreThanOneAlphaTypeException(String arg0);

  /**
   * Translated "A result group can have only one of the following types: \"Date\",\"Date-Time\" and \"Time\"".
   * 
   * @return translated "A result group can have only one of the following types: \"Date\",\"Date-Time\" and \"Time\""
  
   */
  @DefaultMessage("A result group can have only one of the following types: \"Date\",\"Date-Time\" and \"Time\"")
  @Key("testMoreThanOneDateTypeException")
  String testMoreThanOneDateTypeException();

  /**
   * Translated "Group {0} cannot have more than one \"Default\" value for a unit ".
   * 
   * @param arg0 "{0}"
   * @return translated "Group {0} cannot have more than one \"Default\" value for a unit "
  
   */
  @DefaultMessage("Group {0} cannot have more than one \"Default\" value for a unit ")
  @Key("testMoreThanOneDefaultForUnitException")
  String testMoreThanOneDefaultForUnitException(String arg0);

  /**
   * Translated "Test Name:".
   * 
   * @return translated "Test Name:"
  
   */
  @DefaultMessage("Test Name:")
  @Key("testName")
  String testName();

  /**
   * Translated "Test Row {0} - Test name is required".
   * 
   * @param arg0 "{0}"
   * @return translated "Test Row {0} - Test name is required"
  
   */
  @DefaultMessage("Test Row {0} - Test name is required")
  @Key("testNameRequiredException")
  String testNameRequiredException(String arg0);

  /**
   * Translated "This numeric range is overlapping with numeric ranges associated with one or more results in this result group that have the same unit as this one".
   * 
   * @return translated "This numeric range is overlapping with numeric ranges associated with one or more results in this result group that have the same unit as this one"
  
   */
  @DefaultMessage("This numeric range is overlapping with numeric ranges associated with one or more results in this result group that have the same unit as this one")
  @Key("testNumRangeOverlapException")
  String testNumRangeOverlapException();

  /**
   * Translated "History - Prep".
   * 
   * @return translated "History - Prep"
  
   */
  @DefaultMessage("History - Prep")
  @Key("testPrepHistory")
  String testPrepHistory();

  /**
   * Translated "History - Reflex".
   * 
   * @return translated "History - Reflex"
  
   */
  @DefaultMessage("History - Reflex")
  @Key("testReflexHistory")
  String testReflexHistory();

  /**
   * Translated "Test Report".
   * 
   * @return translated "Test Report"
  
   */
  @DefaultMessage("Test Report")
  @Key("testReport")
  String testReport();

  /**
   * Translated "Reportable".
   * 
   * @return translated "Reportable"
  
   */
  @DefaultMessage("Reportable")
  @Key("testReportable")
  String testReportable();

  /**
   * Translated "Test Result Flags".
   * 
   * @return translated "Test Result Flags"
  
   */
  @DefaultMessage("Test Result Flags")
  @Key("testResultFlags")
  String testResultFlags();

  /**
   * Translated "History - Result".
   * 
   * @return translated "History - Result"
  
   */
  @DefaultMessage("History - Result")
  @Key("testResultHistory")
  String testResultHistory();

  /**
   * Translated "Test Results".
   * 
   * @return translated "Test Results"
  
   */
  @DefaultMessage("Test Results")
  @Key("testResults")
  String testResults();

  /**
   * Translated "History - Sample Type".
   * 
   * @return translated "History - Sample Type"
  
   */
  @DefaultMessage("History - Sample Type")
  @Key("testSampleTypeHistory")
  String testSampleTypeHistory();

  /**
   * Translated "Test Section".
   * 
   * @return translated "Test Section"
  
   */
  @DefaultMessage("Test Section")
  @Key("testSection")
  String testSection();

  /**
   * Translated "History - Section".
   * 
   * @return translated "History - Section"
  
   */
  @DefaultMessage("History - Section")
  @Key("testSectionHistory")
  String testSectionHistory();

  /**
   * Translated "Error loading sections for selected test".
   * 
   * @return translated "Error loading sections for selected test"
  
   */
  @DefaultMessage("Error loading sections for selected test")
  @Key("testSectionLoadError")
  String testSectionLoadError();

  /**
   * Translated "Test Section must have a value ".
   * 
   * @return translated "Test Section must have a value "
  
   */
  @DefaultMessage("Test Section must have a value ")
  @Key("testSectionNoValueException")
  String testSectionNoValueException();

  /**
   * Translated "Analysis/Prep Test & Method".
   * 
   * @return translated "Analysis/Prep Test & Method"
  
   */
  @DefaultMessage("Analysis/Prep Test & Method")
  @Key("testSelection.analysisPrepTestMethod")
  String testSelection_analysisPrepTestMethod();

  /**
   * Translated "Analysis/Reflex Test & Method".
   * 
   * @return translated "Analysis/Reflex Test & Method"
  
   */
  @DefaultMessage("Analysis/Reflex Test & Method")
  @Key("testSelection.analysisReflexTestMethod")
  String testSelection_analysisReflexTestMethod();

  /**
   * Translated "One or more Auto Reflex Test(s) must have a Section assigned".
   * 
   * @return translated "One or more Auto Reflex Test(s) must have a Section assigned"
  
   */
  @DefaultMessage("One or more Auto Reflex Test(s) must have a Section assigned")
  @Key("testSelection.autoReflexTestNeedsSection")
  String testSelection_autoReflexTestNeedsSection();

  /**
   * Translated "Copy To All".
   * 
   * @return translated "Copy To All"
  
   */
  @DefaultMessage("Copy To All")
  @Key("testSelection.copyToAll")
  String testSelection_copyToAll();

  /**
   * Translated "Copy To Empty".
   * 
   * @return translated "Copy To Empty"
  
   */
  @DefaultMessage("Copy To Empty")
  @Key("testSelection.copyToEmpty")
  String testSelection_copyToEmpty();

  /**
   * Translated "NEW".
   * 
   * @return translated "NEW"
  
   */
  @DefaultMessage("NEW")
  @Key("testSelection.newAccession")
  String testSelection_newAccession();

  /**
   * Translated "Failed to remove test after the Prep Test Popup was cancelled".
   * 
   * @return translated "Failed to remove test after the Prep Test Popup was cancelled"
  
   */
  @DefaultMessage("Failed to remove test after the Prep Test Popup was cancelled")
  @Key("testSelection.prepTestCancelledCleanupException")
  String testSelection_prepTestCancelledCleanupException();

  /**
   * Translated "Prep Test ''{0}, {1}'' must have a Section assigned".
   * 
   * @param arg0 "{0}"
   * @param arg1 "{1}"
   * @return translated "Prep Test ''{0}, {1}'' must have a Section assigned"
  
   */
  @DefaultMessage("Prep Test ''{0}, {1}'' must have a Section assigned")
  @Key("testSelection.prepTestNeedsSection")
  String testSelection_prepTestNeedsSection(String arg0,  String arg1);

  /**
   * Translated "You must choose the appropriate prep test(s) before your analysis(es) may be added".
   * 
   * @return translated "You must choose the appropriate prep test(s) before your analysis(es) may be added"
  
   */
  @DefaultMessage("You must choose the appropriate prep test(s) before your analysis(es) may be added")
  @Key("testSelection.prepTestRequiredException")
  String testSelection_prepTestRequiredException();

  /**
   * Translated "Prep Test must be chosen for ''{0}, {1}''".
   * 
   * @param arg0 "{0}"
   * @param arg1 "{1}"
   * @return translated "Prep Test must be chosen for ''{0}, {1}''"
  
   */
  @DefaultMessage("Prep Test must be chosen for ''{0}, {1}''")
  @Key("testSelection.prepTestRequiredForTestException")
  String testSelection_prepTestRequiredForTestException(String arg0,  String arg1);

  /**
   * Translated "Prep Test Selection".
   * 
   * @return translated "Prep Test Selection"
  
   */
  @DefaultMessage("Prep Test Selection")
  @Key("testSelection.prepTestSelection")
  String testSelection_prepTestSelection();

  /**
   * Translated "Reflex Test ''{0}, {1}'' must have a Section assigned".
   * 
   * @param arg0 "{0}"
   * @param arg1 "{1}"
   * @return translated "Reflex Test ''{0}, {1}'' must have a Section assigned"
  
   */
  @DefaultMessage("Reflex Test ''{0}, {1}'' must have a Section assigned")
  @Key("testSelection.reflexTestNeedsSection")
  String testSelection_reflexTestNeedsSection(String arg0,  String arg1);

  /**
   * Translated "Reflex Test Selection".
   * 
   * @return translated "Reflex Test Selection"
  
   */
  @DefaultMessage("Reflex Test Selection")
  @Key("testSelection.reflexTestSelection")
  String testSelection_reflexTestSelection();

  /**
   * Translated "This test has an overlapping begin date or end date with another test which has the same name and method as this one".
   * 
   * @return translated "This test has an overlapping begin date or end date with another test which has the same name and method as this one"
  
   */
  @DefaultMessage("This test has an overlapping begin date or end date with another test which has the same name and method as this one")
  @Key("testTimeOverlapException")
  String testTimeOverlapException();

  /**
   * Translated "Values of type \"Time\" must be unique in the same result group".
   * 
   * @return translated "Values of type \"Time\" must be unique in the same result group"
  
   */
  @DefaultMessage("Values of type \"Time\" must be unique in the same result group")
  @Key("testTimeValueNotUniqueException")
  String testTimeValueNotUniqueException();

  /**
   * Translated "This titer range is overlapping with titer ranges associated with one or more results in this result group that have the same unit as this one".
   * 
   * @return translated "This titer range is overlapping with titer ranges associated with one or more results in this result group that have the same unit as this one"
  
   */
  @DefaultMessage("This titer range is overlapping with titer ranges associated with one or more results in this result group that have the same unit as this one")
  @Key("testTiterRangeOverlapException")
  String testTiterRangeOverlapException();

  /**
   * Translated "Trailer".
   * 
   * @return translated "Trailer"
  
   */
  @DefaultMessage("Trailer")
  @Key("testTrailer")
  String testTrailer();

  /**
   * Translated "Test trailer cannot be deleted, one or more tests are still linked to it".
   * 
   * @return translated "Test trailer cannot be deleted, one or more tests are still linked to it"
  
   */
  @DefaultMessage("Test trailer cannot be deleted, one or more tests are still linked to it")
  @Key("testTrailerDeleteException")
  String testTrailerDeleteException();

  /**
   * Translated "History - Test Trailer".
   * 
   * @return translated "History - Test Trailer"
  
   */
  @DefaultMessage("History - Test Trailer")
  @Key("testTrailerHistory")
  String testTrailerHistory();

  /**
   * Translated "This test is being used as a prep test for another test, hence it cannot be deactivated".
   * 
   * @return translated "This test is being used as a prep test for another test, hence it cannot be deactivated"
  
   */
  @DefaultMessage("This test is being used as a prep test for another test, hence it cannot be deactivated")
  @Key("testUsedAsPrepTestException")
  String testUsedAsPrepTestException();

  /**
   * Translated "This test is being used as a reflexive test for another test, hence it cannot be deactivated".
   * 
   * @return translated "This test is being used as a reflexive test for another test, hence it cannot be deactivated"
  
   */
  @DefaultMessage("This test is being used as a reflexive test for another test, hence it cannot be deactivated")
  @Key("testUsedAsReflexTestException")
  String testUsedAsReflexTestException();

  /**
   * Translated "History - Worksheet Analyte".
   * 
   * @return translated "History - Worksheet Analyte"
  
   */
  @DefaultMessage("History - Worksheet Analyte")
  @Key("testWorksheetAnalyteHistory")
  String testWorksheetAnalyteHistory();

  /**
   * Translated "History - Worksheet".
   * 
   * @return translated "History - Worksheet"
  
   */
  @DefaultMessage("History - Worksheet")
  @Key("testWorksheetHistory")
  String testWorksheetHistory();

  /**
   * Translated "History - Worksheet Item".
   * 
   * @return translated "History - Worksheet Item"
  
   */
  @DefaultMessage("History - Worksheet Item")
  @Key("testWorksheetItemHistory")
  String testWorksheetItemHistory();

  /**
   * Translated "Tests and Containers".
   * 
   * @return translated "Tests and Containers"
  
   */
  @DefaultMessage("Tests and Containers")
  @Key("testsAndContainers")
  String testsAndContainers();

  /**
   * Translated "Text".
   * 
   * @return translated "Text"
  
   */
  @DefaultMessage("Text")
  @Key("text")
  String text();

  /**
   * Translated "3 days ago".
   * 
   * @return translated "3 days ago"
  
   */
  @DefaultMessage("3 days ago")
  @Key("threeDays")
  String threeDays();

  /**
   * Translated "Time".
   * 
   * @return translated "Time"
  
   */
  @DefaultMessage("Time")
  @Key("time")
  String time();

  /**
   * Translated "Holding hours".
   * 
   * @return translated "Holding hours"
  
   */
  @DefaultMessage("Holding hours")
  @Key("timeHolding")
  String timeHolding();

  /**
   * Translated "99:99".
   * 
   * @return translated "99:99"
  
   */
  @DefaultMessage("99:99")
  @Key("timeMask")
  String timeMask();

  /**
   * Translated "HH:mm".
   * 
   * @return translated "HH:mm"
  
   */
  @DefaultMessage("HH:mm")
  @Key("timePattern")
  String timePattern();

  /**
   * Translated "Time Since Analyses Completed".
   * 
   * @return translated "Time Since Analyses Completed"
  
   */
  @DefaultMessage("Time Since Analyses Completed")
  @Key("timeSinceAnalysesCompleted")
  String timeSinceAnalysesCompleted();

  /**
   * Translated "Time Since Analyses Initiated".
   * 
   * @return translated "Time Since Analyses Initiated"
  
   */
  @DefaultMessage("Time Since Analyses Initiated")
  @Key("timeSinceAnalysesInitiated")
  String timeSinceAnalysesInitiated();

  /**
   * Translated "Time Since Analyses Logged-In".
   * 
   * @return translated "Time Since Analyses Logged-In"
  
   */
  @DefaultMessage("Time Since Analyses Logged-In")
  @Key("timeSinceAnalysesLoggedIn")
  String timeSinceAnalysesLoggedIn();

  /**
   * Translated "Time Since Samples Received".
   * 
   * @return translated "Time Since Samples Received"
  
   */
  @DefaultMessage("Time Since Samples Received")
  @Key("timeSinceSamplesReceived")
  String timeSinceSamplesReceived();

  /**
   * Translated "Transit days".
   * 
   * @return translated "Transit days"
  
   */
  @DefaultMessage("Transit days")
  @Key("timeTransit")
  String timeTransit();

  /**
   * Translated "Extend Time".
   * 
   * @return translated "Extend Time"
  
   */
  @DefaultMessage("Extend Time")
  @Key("timeoutExtendTime")
  String timeoutExtendTime();

  /**
   * Translated "Timeout Warning".
   * 
   * @return translated "Timeout Warning"
  
   */
  @DefaultMessage("Timeout Warning")
  @Key("timeoutHeader")
  String timeoutHeader();

  /**
   * Translated "Logout".
   * 
   * @return translated "Logout"
  
   */
  @DefaultMessage("Logout")
  @Key("timeoutLogout")
  String timeoutLogout();

  /**
   * Translated "Your session is about to expire, do you want to\nlogout or extend your session".
   * 
   * @return translated "Your session is about to expire, do you want to\nlogout or extend your session"
  
   */
  @DefaultMessage("Your session is about to expire, do you want to\nlogout or extend your session")
  @Key("timeoutWarning")
  String timeoutWarning();

  /**
   * Translated "To".
   * 
   * @return translated "To"
  
   */
  @DefaultMessage("To")
  @Key("to")
  String to();

  /**
   * Translated "To Be Verified".
   * 
   * @return translated "To Be Verified"
  
   */
  @DefaultMessage("To Be Verified")
  @Key("toBeVerified")
  String toBeVerified();

  /**
   * Translated "To Company".
   * 
   * @return translated "To Company"
  
   */
  @DefaultMessage("To Company")
  @Key("toCompany")
  String toCompany();

  /**
   * Translated "To-Do".
   * 
   * @return translated "To-Do"
  
   */
  @DefaultMessage("To-Do")
  @Key("toDo")
  String toDo();

  /**
   * Translated "To-Do Analyte Report".
   * 
   * @return translated "To-Do Analyte Report"
  
   */
  @DefaultMessage("To-Do Analyte Report")
  @Key("toDoAnalyteReport")
  String toDoAnalyteReport();

  /**
   * Translated "View your teams workload.".
   * 
   * @return translated "View your teams workload."
  
   */
  @DefaultMessage("View your teams workload.")
  @Key("toDoDescription")
  String toDoDescription();

  /**
   * Translated "To Item".
   * 
   * @return translated "To Item"
  
   */
  @DefaultMessage("To Item")
  @Key("toItem")
  String toItem();

  /**
   * Translated "To Loc".
   * 
   * @return translated "To Loc"
  
   */
  @DefaultMessage("To Loc")
  @Key("toLoc")
  String toLoc();

  /**
   * Translated "To Name".
   * 
   * @return translated "To Name"
  
   */
  @DefaultMessage("To Name")
  @Key("toName")
  String toName();

  /**
   * Translated "Today".
   * 
   * @return translated "Today"
  
   */
  @DefaultMessage("Today")
  @Key("today")
  String today();

  /**
   * Translated "Total".
   * 
   * @return translated "Total"
  
   */
  @DefaultMessage("Total")
  @Key("total")
  String total();

  /**
   * Translated "Total Capacity".
   * 
   * @return translated "Total Capacity"
  
   */
  @DefaultMessage("Total Capacity")
  @Key("totalCapacity")
  String totalCapacity();

  /**
   * Translated "Total Capacity must be greater than zero".
   * 
   * @return translated "Total Capacity must be greater than zero"
  
   */
  @DefaultMessage("Total Capacity must be greater than zero")
  @Key("totalCapacityMoreThanZeroException")
  String totalCapacityMoreThanZeroException();

  /**
   * Translated "Total Capacity must be a non-zero multiple of Subset Capacity ".
   * 
   * @return translated "Total Capacity must be a non-zero multiple of Subset Capacity "
  
   */
  @DefaultMessage("Total Capacity must be a non-zero multiple of Subset Capacity ")
  @Key("totalCapacityMultipleException")
  String totalCapacityMultipleException();

  /**
   * Translated "Total needed can''t be more than quantity on hand. Transfer inventory in or lower number requested".
   * 
   * @return translated "Total needed can''t be more than quantity on hand. Transfer inventory in or lower number requested"
  
   */
  @DefaultMessage("Total needed can''t be more than quantity on hand. Transfer inventory in or lower number requested")
  @Key("totalIsGreaterThanOnHandException")
  String totalIsGreaterThanOnHandException();

  /**
   * Translated "Total {0} taken from {1} exceeds quantity on hand at the location ".
   * 
   * @param arg0 "{0}"
   * @param arg1 "{1}"
   * @return translated "Total {0} taken from {1} exceeds quantity on hand at the location "
  
   */
  @DefaultMessage("Total {0} taken from {1} exceeds quantity on hand at the location ")
  @Key("totalItemsMoreThanQtyOnHandException")
  String totalItemsMoreThanQtyOnHandException(String arg0,  String arg1);

  /**
   * Translated "Tracking".
   * 
   * @return translated "Tracking"
  
   */
  @DefaultMessage("Tracking")
  @Key("tracking")
  String tracking();

  /**
   * Translated "Lookup and manage general sample information.".
   * 
   * @return translated "Lookup and manage general sample information."
  
   */
  @DefaultMessage("Lookup and manage general sample information.")
  @Key("trackingDescription")
  String trackingDescription();

  /**
   * Translated "Tracking #''s".
   * 
   * @return translated "Tracking #''s"
  
   */
  @DefaultMessage("Tracking #''s")
  @Key("trackingNums")
  String trackingNums();

  /**
   * Translated "Trailer for Test".
   * 
   * @return translated "Trailer for Test"
  
   */
  @DefaultMessage("Trailer for Test")
  @Key("trailerForTest")
  String trailerForTest();

  /**
   * Translated "Define a standard paragraph for the final report that can be associated with tests.".
   * 
   * @return translated "Define a standard paragraph for the final report that can be associated with tests."
  
   */
  @DefaultMessage("Define a standard paragraph for the final report that can be associated with tests.")
  @Key("trailerForTestDescription")
  String trailerForTestDescription();

  /**
   * Translated "Transfer".
   * 
   * @return translated "Transfer"
  
   */
  @DefaultMessage("Transfer")
  @Key("transfer")
  String transfer();

  /**
   * Translated "Transfer Inventory".
   * 
   * @return translated "Transfer Inventory"
  
   */
  @DefaultMessage("Transfer Inventory")
  @Key("transferInventory")
  String transferInventory();

  /**
   * Translated "Transferring is not allowed because the parent item has not been set for".
   * 
   * @return translated "Transferring is not allowed because the parent item has not been set for"
  
   */
  @DefaultMessage("Transferring is not allowed because the parent item has not been set for")
  @Key("transferNotAllowed")
  String transferNotAllowed();

  /**
   * Translated "Trend".
   * 
   * @return translated "Trend"
  
   */
  @DefaultMessage("Trend")
  @Key("trendable")
  String trendable();

  /**
   * Translated "Reserve".
   * 
   * @return translated "Reserve"
  
   */
  @DefaultMessage("Reserve")
  @Key("trfAttachment.reserve")
  String trfAttachment_reserve();

  /**
   * Translated "TRF Attachment".
   * 
   * @return translated "TRF Attachment"
  
   */
  @DefaultMessage("TRF Attachment")
  @Key("trfAttachment.trfAttachment")
  String trfAttachment_trfAttachment();

  /**
   * Translated "Tube #".
   * 
   * @return translated "Tube #"
  
   */
  @DefaultMessage("Tube #")
  @Key("tubeNum")
  String tubeNum();

  /**
   * Translated "Turn Around".
   * 
   * @return translated "Turn Around"
  
   */
  @DefaultMessage("Turn Around")
  @Key("turnAround")
  String turnAround();

  /**
   * Translated "Average days".
   * 
   * @return translated "Average days"
  
   */
  @DefaultMessage("Average days")
  @Key("turnAroundAverage")
  String turnAroundAverage();

  /**
   * Translated "Max. days".
   * 
   * @return translated "Max. days"
  
   */
  @DefaultMessage("Max. days")
  @Key("turnAroundMax")
  String turnAroundMax();

  /**
   * Translated "Turnaround Statistics Report".
   * 
   * @return translated "Turnaround Statistics Report"
  
   */
  @DefaultMessage("Turnaround Statistics Report")
  @Key("turnAroundStatistic")
  String turnAroundStatistic();

  /**
   * Translated "Turnaround Statistics Report".
   * 
   * @return translated "Turnaround Statistics Report"
  
   */
  @DefaultMessage("Turnaround Statistics Report")
  @Key("turnAroundStatisticReport")
  String turnAroundStatisticReport();

  /**
   * Translated "Warning days".
   * 
   * @return translated "Warning days"
  
   */
  @DefaultMessage("Warning days")
  @Key("turnAroundWarn")
  String turnAroundWarn();

  /**
   * Translated "Turnaround".
   * 
   * @return translated "Turnaround"
  
   */
  @DefaultMessage("Turnaround")
  @Key("turnaround")
  String turnaround();

  /**
   * Translated "21-30 days ago".
   * 
   * @return translated "21-30 days ago"
  
   */
  @DefaultMessage("21-30 days ago")
  @Key("twntyOneToThrtyDays")
  String twntyOneToThrtyDays();

  /**
   * Translated "2 days ago".
   * 
   * @return translated "2 days ago"
  
   */
  @DefaultMessage("2 days ago")
  @Key("twoDays")
  String twoDays();

  /**
   * Translated "2-5 days ago".
   * 
   * @return translated "2-5 days ago"
  
   */
  @DefaultMessage("2-5 days ago")
  @Key("twoToFiveDays")
  String twoToFiveDays();

  /**
   * Translated "Type".
   * 
   * @return translated "Type"
  
   */
  @DefaultMessage("Type")
  @Key("type")
  String type();

  /**
   * Translated "Type/Analyses".
   * 
   * @return translated "Type/Analyses"
  
   */
  @DefaultMessage("Type/Analyses")
  @Key("typeAnalyses")
  String typeAnalyses();

  /**
   * Translated "Type/Status".
   * 
   * @return translated "Type/Status"
  
   */
  @DefaultMessage("Type/Status")
  @Key("typeStatus")
  String typeStatus();

  /**
   * Translated "UCL".
   * 
   * @return translated "UCL"
  
   */
  @DefaultMessage("UCL")
  @Key("uCL")
  String uCL();

  /**
   * Translated "UWL".
   * 
   * @return translated "UWL"
  
   */
  @DefaultMessage("UWL")
  @Key("uWL")
  String uWL();

  /**
   * Translated "Uncheck All".
   * 
   * @return translated "Uncheck All"
  
   */
  @DefaultMessage("Uncheck All")
  @Key("uncheckAll")
  String uncheckAll();

  /**
   * Translated "Undo QCs".
   * 
   * @return translated "Undo QCs"
  
   */
  @DefaultMessage("Undo QCs")
  @Key("undoQc")
  String undoQc();

  /**
   * Translated "Ungroup Analytes".
   * 
   * @return translated "Ungroup Analytes"
  
   */
  @DefaultMessage("Ungroup Analytes")
  @Key("ungroupAnalytes")
  String ungroupAnalytes();

  /**
   * Translated "Unit".
   * 
   * @return translated "Unit"
  
   */
  @DefaultMessage("Unit")
  @Key("unit")
  String unit();

  /**
   * Translated "Unit Cost".
   * 
   * @return translated "Unit Cost"
  
   */
  @DefaultMessage("Unit Cost")
  @Key("unitCost")
  String unitCost();

  /**
   * Translated "Unit of Measure ".
   * 
   * @return translated "Unit of Measure "
  
   */
  @DefaultMessage("Unit of Measure ")
  @Key("unitOfMeasure")
  String unitOfMeasure();

  /**
   * Translated "You need a unit of measure on this analysis before entering this result".
   * 
   * @return translated "You need a unit of measure on this analysis before entering this result"
  
   */
  @DefaultMessage("You need a unit of measure on this analysis before entering this result")
  @Key("unitOfMeasureException")
  String unitOfMeasureException();

  /**
   * Translated "Unrelease Analysis".
   * 
   * @return translated "Unrelease Analysis"
  
   */
  @DefaultMessage("Unrelease Analysis")
  @Key("unreleaseAnalysisCaption")
  String unreleaseAnalysisCaption();

  /**
   * Translated "You must add an internal note when sample/analysis is unreleased. \n\nPress Ok to continue or Cancel to abort.".
   * 
   * @return translated "You must add an internal note when sample/analysis is unreleased. \n\nPress Ok to continue or Cancel to abort."
  
   */
  @DefaultMessage("You must add an internal note when sample/analysis is unreleased. \n\nPress Ok to continue or Cancel to abort.")
  @Key("unreleaseAnalysisMessage")
  String unreleaseAnalysisMessage();

  /**
   * Translated "You may not unrelease more than one analysis at a time.".
   * 
   * @return translated "You may not unrelease more than one analysis at a time."
  
   */
  @DefaultMessage("You may not unrelease more than one analysis at a time.")
  @Key("unreleaseMultipleException")
  String unreleaseMultipleException();

  /**
   * Translated "You must add an internal note when sample/analysis is unreleased".
   * 
   * @return translated "You must add an internal note when sample/analysis is unreleased"
  
   */
  @DefaultMessage("You must add an internal note when sample/analysis is unreleased")
  @Key("unreleaseNoNoteException")
  String unreleaseNoNoteException();

  /**
   * Translated "Unrelease Sample".
   * 
   * @return translated "Unrelease Sample"
  
   */
  @DefaultMessage("Unrelease Sample")
  @Key("unreleaseSampleCaption")
  String unreleaseSampleCaption();

  /**
   * Translated "Unreleasing a sample will reset the release date and increment the revision number. \n\nPress Ok to continue or Cancel to abort. ".
   * 
   * @return translated "Unreleasing a sample will reset the release date and increment the revision number. \n\nPress Ok to continue or Cancel to abort. "
  
   */
  @DefaultMessage("Unreleasing a sample will reset the release date and increment the revision number. \n\nPress Ok to continue or Cancel to abort. ")
  @Key("unreleaseSampleMessage")
  String unreleaseSampleMessage();

  /**
   * Translated "Unselect".
   * 
   * @return translated "Unselect"
  
   */
  @DefaultMessage("Unselect")
  @Key("unselect")
  String unselect();

  /**
   * Translated "Unselect All".
   * 
   * @return translated "Unselect All"
  
   */
  @DefaultMessage("Unselect All")
  @Key("unselectAll")
  String unselectAll();

  /**
   * Translated "UPC".
   * 
   * @return translated "UPC"
  
   */
  @DefaultMessage("UPC")
  @Key("upc")
  String upc();

  /**
   * Translated "Update".
   * 
   * @return translated "Update"
  
   */
  @DefaultMessage("Update")
  @Key("update")
  String update();

  /**
   * Translated "Update aborted".
   * 
   * @return translated "Update aborted"
  
   */
  @DefaultMessage("Update aborted")
  @Key("updateAborted")
  String updateAborted();

  /**
   * Translated "Update Failed. Make corrections and try again or Abort.".
   * 
   * @return translated "Update Failed. Make corrections and try again or Abort."
  
   */
  @DefaultMessage("Update Failed. Make corrections and try again or Abort.")
  @Key("updateFailed")
  String updateFailed();

  /**
   * Translated "Update fields, then press commit".
   * 
   * @return translated "Update fields, then press commit"
  
   */
  @DefaultMessage("Update fields, then press commit")
  @Key("updateFields")
  String updateFields();

  /**
   * Translated "Update fields then, press Commit".
   * 
   * @return translated "Update fields then, press Commit"
  
   */
  @DefaultMessage("Update fields then, press Commit")
  @Key("updateFieldsPressCommit")
  String updateFieldsPressCommit();

  /**
   * Translated "Updating...".
   * 
   * @return translated "Updating..."
  
   */
  @DefaultMessage("Updating...")
  @Key("updating")
  String updating();

  /**
   * Translated "Updating...Complete".
   * 
   * @return translated "Updating...Complete"
  
   */
  @DefaultMessage("Updating...Complete")
  @Key("updatingComplete")
  String updatingComplete();

  /**
   * Translated "Usable date must not be before prepared date".
   * 
   * @return translated "Usable date must not be before prepared date"
  
   */
  @DefaultMessage("Usable date must not be before prepared date")
  @Key("usableBeforePrepException")
  String usableBeforePrepException();

  /**
   * Translated "Usable Date".
   * 
   * @return translated "Usable Date"
  
   */
  @DefaultMessage("Usable Date")
  @Key("usableDate")
  String usableDate();

  /**
   * Translated "Use Date Range".
   * 
   * @return translated "Use Date Range"
  
   */
  @DefaultMessage("Use Date Range")
  @Key("useDateRange")
  String useDateRange();

  /**
   * Translated "User".
   * 
   * @return translated "User"
  
   */
  @DefaultMessage("User")
  @Key("user")
  String user();

  /**
   * Translated "User Name".
   * 
   * @return translated "User Name"
  
   */
  @DefaultMessage("User Name")
  @Key("userName")
  String userName();

  /**
   * Translated "User/Value".
   * 
   * @return translated "User/Value"
  
   */
  @DefaultMessage("User/Value")
  @Key("userValue")
  String userValue();

  /**
   * Translated "Username:".
   * 
   * @return translated "Username:"
  
   */
  @DefaultMessage("Username:")
  @Key("username")
  String username();

  /**
   * Translated "Value1".
   * 
   * @return translated "Value1"
  
   */
  @DefaultMessage("Value1")
  @Key("v1")
  String v1();

  /**
   * Translated "Value2".
   * 
   * @return translated "Value2"
  
   */
  @DefaultMessage("Value2")
  @Key("v2")
  String v2();

  /**
   * Translated "Validating Delete ...".
   * 
   * @return translated "Validating Delete ..."
  
   */
  @DefaultMessage("Validating Delete ...")
  @Key("validatingDelete")
  String validatingDelete();

  /**
   * Translated "Value".
   * 
   * @return translated "Value"
  
   */
  @DefaultMessage("Value")
  @Key("value")
  String value();

  /**
   * Translated "A value must not be specified for this type".
   * 
   * @return translated "A value must not be specified for this type"
  
   */
  @DefaultMessage("A value must not be specified for this type")
  @Key("valuePresentForTypeException")
  String valuePresentForTypeException();

  /**
   * Translated "Vendor".
   * 
   * @return translated "Vendor"
  
   */
  @DefaultMessage("Vendor")
  @Key("vendor")
  String vendor();

  /**
   * Translated "Vendor Address".
   * 
   * @return translated "Vendor Address"
  
   */
  @DefaultMessage("Vendor Address")
  @Key("vendorAddress")
  String vendorAddress();

  /**
   * Translated "Vendor Order".
   * 
   * @return translated "Vendor Order"
  
   */
  @DefaultMessage("Vendor Order")
  @Key("vendorOrder")
  String vendorOrder();

  /**
   * Translated "Order items from external vendors.".
   * 
   * @return translated "Order items from external vendors."
  
   */
  @DefaultMessage("Order items from external vendors.")
  @Key("vendorOrderDescription")
  String vendorOrderDescription();

  /**
   * Translated "Verify Sample Data Entry".
   * 
   * @return translated "Verify Sample Data Entry"
  
   */
  @DefaultMessage("Verify Sample Data Entry")
  @Key("verification")
  String verification();

  /**
   * Translated "The sample must be fully logged in".
   * 
   * @return translated "The sample must be fully logged in"
  
   */
  @DefaultMessage("The sample must be fully logged in")
  @Key("verification.cantVerifyQuickEntry")
  String verification_cantVerifyQuickEntry();

  /**
   * Translated "3. Close the window when done.".
   * 
   * @return translated "3. Close the window when done."
  
   */
  @DefaultMessage("3. Close the window when done.")
  @Key("verification.closeVerifyWindow")
  String verification_closeVerifyWindow();

  /**
   * Translated "The sample must have at least one analysis assigned.".
   * 
   * @return translated "The sample must have at least one analysis assigned."
  
   */
  @DefaultMessage("The sample must have at least one analysis assigned.")
  @Key("verification.mustHaveAnalysesToVerify")
  String verification_mustHaveAnalysesToVerify();

  /**
   * Translated "2. Repeat the above process for every sample record.".
   * 
   * @return translated "2. Repeat the above process for every sample record."
  
   */
  @DefaultMessage("2. Repeat the above process for every sample record.")
  @Key("verification.repeatVerifyProcess")
  String verification_repeatVerifyProcess();

  /**
   * Translated "1. Scan in the barcode for the sample accession number to be verified.".
   * 
   * @return translated "1. Scan in the barcode for the sample accession number to be verified."
  
   */
  @DefaultMessage("1. Scan in the barcode for the sample accession number to be verified.")
  @Key("verification.scanSampleAccessionBarcode")
  String verification_scanSampleAccessionBarcode();

  /**
   * Translated "Verify Sample Data Entry".
   * 
   * @return translated "Verify Sample Data Entry"
  
   */
  @DefaultMessage("Verify Sample Data Entry")
  @Key("verification.verification")
  String verification_verification();

  /**
   * Translated "The status of the sample record must be \"Not Verified\"".
   * 
   * @return translated "The status of the sample record must be \"Not Verified\""
  
   */
  @DefaultMessage("The status of the sample record must be \"Not Verified\"")
  @Key("verification.wrongStatusForVerifying")
  String verification_wrongStatusForVerifying();

  /**
   * Translated "Verification Report".
   * 
   * @return translated "Verification Report"
  
   */
  @DefaultMessage("Verification Report")
  @Key("verificationReport")
  String verificationReport();

  /**
   * Translated "Version".
   * 
   * @return translated "Version"
  
   */
  @DefaultMessage("Version")
  @Key("version")
  String version();

  /**
   * Translated "View Final Report".
   * 
   * @return translated "View Final Report"
  
   */
  @DefaultMessage("View Final Report")
  @Key("viewFinalReport")
  String viewFinalReport();

  /**
   * Translated "View in PDF".
   * 
   * @return translated "View in PDF"
  
   */
  @DefaultMessage("View in PDF")
  @Key("viewInPDF")
  String viewInPDF();

  /**
   * Translated "Volume Report".
   * 
   * @return translated "Volume Report"
  
   */
  @DefaultMessage("Volume Report")
  @Key("volumeReport")
  String volumeReport();

  /**
   * Translated "Press Ok to commit anyway or cancel to fix these warnings.".
   * 
   * @return translated "Press Ok to commit anyway or cancel to fix these warnings."
  
   */
  @DefaultMessage("Press Ok to commit anyway or cancel to fix these warnings.")
  @Key("warningDialogLastLine")
  String warningDialogLastLine();

  /**
   * Translated "There are warnings on the screen:".
   * 
   * @return translated "There are warnings on the screen:"
  
   */
  @DefaultMessage("There are warnings on the screen:")
  @Key("warningDialogLine1")
  String warningDialogLine1();

  /**
   * Translated "Water Type".
   * 
   * @return translated "Water Type"
  
   */
  @DefaultMessage("Water Type")
  @Key("waterType")
  String waterType();

  /**
   * Translated "Please select at least one sample, then press Run Report".
   * 
   * @return translated "Please select at least one sample, then press Run Report"
  
   */
  @DefaultMessage("Please select at least one sample, then press Run Report")
  @Key("web.noSampleSelected")
  String web_noSampleSelected();

  /**
   * Translated "Well/Collector Info".
   * 
   * @return translated "Well/Collector Info"
  
   */
  @DefaultMessage("Well/Collector Info")
  @Key("wellCollectorInfo")
  String wellCollectorInfo();

  /**
   * Translated "Well Num".
   * 
   * @return translated "Well Num"
  
   */
  @DefaultMessage("Well Num")
  @Key("wellNum")
  String wellNum();

  /**
   * Translated "Work #".
   * 
   * @return translated "Work #"
  
   */
  @DefaultMessage("Work #")
  @Key("workNumber")
  String workNumber();

  /**
   * Translated "Worksheet".
   * 
   * @return translated "Worksheet"
  
   */
  @DefaultMessage("Worksheet")
  @Key("worksheet")
  String worksheet();

  /**
   * Translated "Worksheet is at capacity; cannot add more rows".
   * 
   * @return translated "Worksheet is at capacity; cannot add more rows"
  
   */
  @DefaultMessage("Worksheet is at capacity; cannot add more rows")
  @Key("worksheet.atCapacity")
  String worksheet_atCapacity();

  /**
   * Translated "Changing the worksheet layout has significant consequences for instrument interfacing and importing of existing Excel files. The command that you have just performed has not been executed; you may reperform the command if you still wish to proceed.".
   * 
   * @return translated "Changing the worksheet layout has significant consequences for instrument interfacing and importing of existing Excel files. The command that you have just performed has not been executed; you may reperform the command if you still wish to proceed."
  
   */
  @DefaultMessage("Changing the worksheet layout has significant consequences for instrument interfacing and importing of existing Excel files. The command that you have just performed has not been executed; you may reperform the command if you still wish to proceed.")
  @Key("worksheet.builderUpdateWarning")
  String worksheet_builderUpdateWarning();

  /**
   * Translated "The capacity of this worksheet has been exceeded; the last {0,number,#0} items in your selection have not been added.".
   * 
   * @param arg0 "{0,number,#0}"
   * @return translated "The capacity of this worksheet has been exceeded; the last {0,number,#0} items in your selection have not been added."
  
   */
  @DefaultMessage("The capacity of this worksheet has been exceeded; the last {0,number,#0} items in your selection have not been added.")
  @Key("worksheet.capacityExceeded")
  String worksheet_capacityExceeded(Integer arg0);

  /**
   * Translated "Reagents were loaded, please view the Reagent/Media tab to see issues that arose from this load.".
   * 
   * @return translated "Reagents were loaded, please view the Reagent/Media tab to see issues that arose from this load."
  
   */
  @DefaultMessage("Reagents were loaded, please view the Reagent/Media tab to see issues that arose from this load.")
  @Key("worksheet.checkReagentTab")
  String worksheet_checkReagentTab();

  /**
   * Translated "You must choose a format for this worksheet before adding analyses from another worksheet.".
   * 
   * @return translated "You must choose a format for this worksheet before adding analyses from another worksheet."
  
   */
  @DefaultMessage("You must choose a format for this worksheet before adding analyses from another worksheet.")
  @Key("worksheet.chooseFormatBeforeAddFromOther")
  String worksheet_chooseFormatBeforeAddFromOther();

  /**
   * Translated "You must have complete permissions for the ''{0}'' section to {1} this row.".
   * 
   * @param arg0 "{0}"
   * @param arg1 "{1}"
   * @return translated "You must have complete permissions for the ''{0}'' section to {1} this row."
  
   */
  @DefaultMessage("You must have complete permissions for the ''{0}'' section to {1} this row.")
  @Key("worksheet.completePermissionRequiredForOperation")
  String worksheet_completePermissionRequiredForOperation(String arg0,  String arg1);

  /**
   * Translated "Created Date".
   * 
   * @return translated "Created Date"
  
   */
  @DefaultMessage("Created Date")
  @Key("worksheet.createdDate")
  String worksheet_createdDate();

  /**
   * Translated "Error fetching worksheet display directory system variable".
   * 
   * @return translated "Error fetching worksheet display directory system variable"
  
   */
  @DefaultMessage("Error fetching worksheet display directory system variable")
  @Key("worksheet.displayDirectoryLookupException")
  String worksheet_displayDirectoryLookupException();

  /**
   * Translated "Edit Multiple".
   * 
   * @return translated "Edit Multiple"
  
   */
  @DefaultMessage("Edit Multiple")
  @Key("worksheet.editMultiple")
  String worksheet_editMultiple();

  /**
   * Translated "Error changing analysis status for position {0} analysis {1}: {2}".
   * 
   * @param arg0 "{0}"
   * @param arg1 "{1}"
   * @param arg2 "{2}"
   * @return translated "Error changing analysis status for position {0} analysis {1}: {2}"
  
   */
  @DefaultMessage("Error changing analysis status for position {0} analysis {1}: {2}")
  @Key("worksheet.errorChangingAnalysisStatus")
  String worksheet_errorChangingAnalysisStatus(String arg0,  String arg1,  String arg2);

  /**
   * Translated "Error changing analysis unit for position {0} analysis {1}: {2}".
   * 
   * @param arg0 "{0}"
   * @param arg1 "{1}"
   * @param arg2 "{2}"
   * @return translated "Error changing analysis unit for position {0} analysis {1}: {2}"
  
   */
  @DefaultMessage("Error changing analysis unit for position {0} analysis {1}: {2}")
  @Key("worksheet.errorChangingAnalysisUnit")
  String worksheet_errorChangingAnalysisUnit(String arg0,  String arg1,  String arg2);

  /**
   * Translated "Error loading additional analytes for position {0} analysis {1}: {2}".
   * 
   * @param arg0 "{0}"
   * @param arg1 "{1}"
   * @param arg2 "{2}"
   * @return translated "Error loading additional analytes for position {0} analysis {1}: {2}"
  
   */
  @DefaultMessage("Error loading additional analytes for position {0} analysis {1}: {2}")
  @Key("worksheet.errorLoadingAdditionalAnalytes")
  String worksheet_errorLoadingAdditionalAnalytes(String arg0,  String arg1,  String arg2);

  /**
   * Translated "Error loading result formatter for {0}: {1}".
   * 
   * @param arg0 "{0}"
   * @param arg1 "{1}"
   * @return translated "Error loading result formatter for {0}: {1}"
  
   */
  @DefaultMessage("Error loading result formatter for {0}: {1}")
  @Key("worksheet.errorLoadingResultFormatter")
  String worksheet_errorLoadingResultFormatter(String arg0,  String arg1);

  /**
   * Translated "Position {0} analysis {1}: {2}".
   * 
   * @param arg0 "{0}"
   * @param arg1 "{1}"
   * @param arg2 "{2}"
   * @return translated "Position {0} analysis {1}: {2}"
  
   */
  @DefaultMessage("Position {0} analysis {1}: {2}")
  @Key("worksheet.errorPrefix")
  String worksheet_errorPrefix(String arg0,  String arg1,  String arg2);

  /**
   * Translated "Export to Excel".
   * 
   * @return translated "Export to Excel"
  
   */
  @DefaultMessage("Export to Excel")
  @Key("worksheet.exportToExcel")
  String worksheet_exportToExcel();

  /**
   * Translated "Worksheet was exported to".
   * 
   * @return translated "Worksheet was exported to"
  
   */
  @DefaultMessage("Worksheet was exported to")
  @Key("worksheet.exportedToExcelFile")
  String worksheet_exportedToExcelFile();

  /**
   * Translated "Format".
   * 
   * @return translated "Format"
  
   */
  @DefaultMessage("Format")
  @Key("worksheet.format")
  String worksheet_format();

  /**
   * Translated "If Empty".
   * 
   * @return translated "If Empty"
  
   */
  @DefaultMessage("If Empty")
  @Key("worksheet.ifEmpty")
  String worksheet_ifEmpty();

  /**
   * Translated "Invalid user ''{0}'' for position {1} analysis {2}".
   * 
   * @param arg0 "{0}"
   * @param arg1 "{1}"
   * @param arg2 "{2}"
   * @return translated "Invalid user ''{0}'' for position {1} analysis {2}"
  
   */
  @DefaultMessage("Invalid user ''{0}'' for position {1} analysis {2}")
  @Key("worksheet.illegalWorksheetUserFormException")
  String worksheet_illegalWorksheetUserFormException(String arg0,  String arg1,  String arg2);

  /**
   * Translated "Import From Excel".
   * 
   * @return translated "Import From Excel"
  
   */
  @DefaultMessage("Import From Excel")
  @Key("worksheet.importFromExcel")
  String worksheet_importFromExcel();

  /**
   * Translated "Analysis Above".
   * 
   * @return translated "Analysis Above"
  
   */
  @DefaultMessage("Analysis Above")
  @Key("worksheet.insertAnalysisAbove")
  String worksheet_insertAnalysisAbove();

  /**
   * Translated "Analysis Below".
   * 
   * @return translated "Analysis Below"
  
   */
  @DefaultMessage("Analysis Below")
  @Key("worksheet.insertAnalysisBelow")
  String worksheet_insertAnalysisBelow();

  /**
   * Translated "Analysis This Position (Pooling)".
   * 
   * @return translated "Analysis This Position (Pooling)"
  
   */
  @DefaultMessage("Analysis This Position (Pooling)")
  @Key("worksheet.insertAnalysisThisPosition")
  String worksheet_insertAnalysisThisPosition();

  /**
   * Translated "From QC Table Above".
   * 
   * @return translated "From QC Table Above"
  
   */
  @DefaultMessage("From QC Table Above")
  @Key("worksheet.insertFromQcTableAbove")
  String worksheet_insertFromQcTableAbove();

  /**
   * Translated "From QC Table Below".
   * 
   * @return translated "From QC Table Below"
  
   */
  @DefaultMessage("From QC Table Below")
  @Key("worksheet.insertFromQcTableBelow")
  String worksheet_insertFromQcTableBelow();

  /**
   * Translated "From QC Table This Position (Pooling)".
   * 
   * @return translated "From QC Table This Position (Pooling)"
  
   */
  @DefaultMessage("From QC Table This Position (Pooling)")
  @Key("worksheet.insertFromQcTableThisPosition")
  String worksheet_insertFromQcTableThisPosition();

  /**
   * Translated "From Another Worksheet Above".
   * 
   * @return translated "From Another Worksheet Above"
  
   */
  @DefaultMessage("From Another Worksheet Above")
  @Key("worksheet.insertFromWorksheetAbove")
  String worksheet_insertFromWorksheetAbove();

  /**
   * Translated "From Another Worksheet Below".
   * 
   * @return translated "From Another Worksheet Below"
  
   */
  @DefaultMessage("From Another Worksheet Below")
  @Key("worksheet.insertFromWorksheetBelow")
  String worksheet_insertFromWorksheetBelow();

  /**
   * Translated "From Another Worksheet This Position (Pooling)".
   * 
   * @return translated "From Another Worksheet This Position (Pooling)"
  
   */
  @DefaultMessage("From Another Worksheet This Position (Pooling)")
  @Key("worksheet.insertFromWorksheetThisPosition")
  String worksheet_insertFromWorksheetThisPosition();

  /**
   * Translated "Instrument Name".
   * 
   * @return translated "Instrument Name"
  
   */
  @DefaultMessage("Instrument Name")
  @Key("worksheet.instrumentName")
  String worksheet_instrumentName();

  /**
   * Translated "Invalid override user(s) specified: {0}".
   * 
   * @param arg0 "{0}"
   * @return translated "Invalid override user(s) specified: {0}"
  
   */
  @DefaultMessage("Invalid override user(s) specified: {0}")
  @Key("worksheet.invalidUsersException")
  String worksheet_invalidUsersException(String arg0);

  /**
   * Translated "The following Analyses have not been added because of permission, status, or QA Event".
   * 
   * @return translated "The following Analyses have not been added because of permission, status, or QA Event"
  
   */
  @DefaultMessage("The following Analyses have not been added because of permission, status, or QA Event")
  @Key("worksheet.itemsNotAdded")
  String worksheet_itemsNotAdded();

  /**
   * Translated "Load QC Template".
   * 
   * @return translated "Load QC Template"
  
   */
  @DefaultMessage("Load QC Template")
  @Key("worksheet.loadTemplate")
  String worksheet_loadTemplate();

  /**
   * Translated "More than one active reagent lot was found for row {0,number,#0}".
   * 
   * @param arg0 "{0,number,#0}"
   * @return translated "More than one active reagent lot was found for row {0,number,#0}"
  
   */
  @DefaultMessage("More than one active reagent lot was found for row {0,number,#0}")
  @Key("worksheet.multiMatchingActiveReagent")
  String worksheet_multiMatchingActiveReagent(Integer arg0);

  /**
   * Translated "No Analytes Found For Selected Row".
   * 
   * @return translated "No Analytes Found For Selected Row"
  
   */
  @DefaultMessage("No Analytes Found For Selected Row")
  @Key("worksheet.noAnalytesFoundForRow")
  String worksheet_noAnalytesFoundForRow();

  /**
   * Translated "One or More items are linked to this row. Please unlink them before removing this row.".
   * 
   * @return translated "One or More items are linked to this row. Please unlink them before removing this row."
  
   */
  @DefaultMessage("One or More items are linked to this row. Please unlink them before removing this row.")
  @Key("worksheet.oneOrMoreQcLinkOnRemove")
  String worksheet_oneOrMoreQcLinkOnRemove();

  /**
   * Translated "User(s)".
   * 
   * @return translated "User(s)"
  
   */
  @DefaultMessage("User(s)")
  @Key("worksheet.overrideUsers")
  String worksheet_overrideUsers();

  /**
   * Translated "Error validating override user(s)".
   * 
   * @return translated "Error validating override user(s)"
  
   */
  @DefaultMessage("Error validating override user(s)")
  @Key("worksheet.overrideUsersValidationException")
  String worksheet_overrideUsersValidationException();

  /**
   * Translated "Overrides".
   * 
   * @return translated "Overrides"
  
   */
  @DefaultMessage("Overrides")
  @Key("worksheet.overrides")
  String worksheet_overrides();

  /**
   * Translated "Position".
   * 
   * @return translated "Position"
  
   */
  @DefaultMessage("Position")
  @Key("worksheet.position")
  String worksheet_position();

  /**
   * Translated "QC Link".
   * 
   * @return translated "QC Link"
  
   */
  @DefaultMessage("QC Link")
  @Key("worksheet.qcLink")
  String worksheet_qcLink();

  /**
   * Translated "Reagents/Media".
   * 
   * @return translated "Reagents/Media"
  
   */
  @DefaultMessage("Reagents/Media")
  @Key("worksheet.reagentsMedia")
  String worksheet_reagentsMedia();

  /**
   * Translated "Related Worksheet #".
   * 
   * @return translated "Related Worksheet #"
  
   */
  @DefaultMessage("Related Worksheet #")
  @Key("worksheet.relatedWorksheetNumber")
  String worksheet_relatedWorksheetNumber();

  /**
   * Translated "Transfer Results".
   * 
   * @return translated "Transfer Results"
  
   */
  @DefaultMessage("Transfer Results")
  @Key("worksheet.transferResults")
  String worksheet_transferResults();

  /**
   * Translated "All".
   * 
   * @return translated "All"
  
   */
  @DefaultMessage("All")
  @Key("worksheet.undoAll")
  String worksheet_undoAll();

  /**
   * Translated "Manual".
   * 
   * @return translated "Manual"
  
   */
  @DefaultMessage("Manual")
  @Key("worksheet.undoManual")
  String worksheet_undoManual();

  /**
   * Translated "Undo QCs".
   * 
   * @return translated "Undo QCs"
  
   */
  @DefaultMessage("Undo QCs")
  @Key("worksheet.undoQc")
  String worksheet_undoQc();

  /**
   * Translated "Template".
   * 
   * @return translated "Template"
  
   */
  @DefaultMessage("Template")
  @Key("worksheet.undoTemplate")
  String worksheet_undoTemplate();

  /**
   * Translated "Unparseable completed date for position {0} analysis {1}".
   * 
   * @param arg0 "{0}"
   * @param arg1 "{1}"
   * @return translated "Unparseable completed date for position {0} analysis {1}"
  
   */
  @DefaultMessage("Unparseable completed date for position {0} analysis {1}")
  @Key("worksheet.unparseableCompletedDate")
  String worksheet_unparseableCompletedDate(String arg0,  String arg1);

  /**
   * Translated "Unparseable started date for position {0} analysis {1}".
   * 
   * @param arg0 "{0}"
   * @param arg1 "{1}"
   * @return translated "Unparseable started date for position {0} analysis {1}"
  
   */
  @DefaultMessage("Unparseable started date for position {0} analysis {1}")
  @Key("worksheet.unparseableStartedDate")
  String worksheet_unparseableStartedDate(String arg0,  String arg1);

  /**
   * Translated "No Analyses Found For Worksheet Id: {0}".
   * 
   * @param arg0 "{0}"
   * @return translated "No Analyses Found For Worksheet Id: {0}"
  
   */
  @DefaultMessage("No Analyses Found For Worksheet Id: {0}")
  @Key("worksheet.worksheetAnalysesNotFound")
  String worksheet_worksheetAnalysesNotFound(String arg0);

  /**
   * Translated "Worksheet Row Selection".
   * 
   * @return translated "Worksheet Row Selection"
  
   */
  @DefaultMessage("Worksheet Row Selection")
  @Key("worksheet.worksheetAnalysisSelection")
  String worksheet_worksheetAnalysisSelection();

  /**
   * Translated "Worksheet Analysis Selection".
   * 
   * @return translated "Worksheet Analysis Selection"
  
   */
  @DefaultMessage("Worksheet Analysis Selection")
  @Key("worksheet.worksheetBuilderLookup")
  String worksheet_worksheetBuilderLookup();

  /**
   * Translated "History - Worksheet".
   * 
   * @return translated "History - Worksheet"
  
   */
  @DefaultMessage("History - Worksheet")
  @Key("worksheet.worksheetHistory")
  String worksheet_worksheetHistory();

  /**
   * Translated "Worksheet Lookup".
   * 
   * @return translated "Worksheet Lookup"
  
   */
  @DefaultMessage("Worksheet Lookup")
  @Key("worksheet.worksheetLookup")
  String worksheet_worksheetLookup();

  /**
   * Translated "Worksheet #".
   * 
   * @return translated "Worksheet #"
  
   */
  @DefaultMessage("Worksheet #")
  @Key("worksheet.worksheetNumber")
  String worksheet_worksheetNumber();

  /**
   * Translated "The following rows are in a status that does not allow edit: {0}".
   * 
   * @param arg0 "{0}"
   * @return translated "The following rows are in a status that does not allow edit: {0}"
  
   */
  @DefaultMessage("The following rows are in a status that does not allow edit: {0}")
  @Key("worksheet.wrongStatusNoEditRows")
  String worksheet_wrongStatusNoEditRows(String arg0);

  /**
   * Translated "No Analyses Found For Worksheet Id: {0}".
   * 
   * @param arg0 "{0}"
   * @return translated "No Analyses Found For Worksheet Id: {0}"
  
   */
  @DefaultMessage("No Analyses Found For Worksheet Id: {0}")
  @Key("worksheetAnalysesNotFound")
  String worksheetAnalysesNotFound(String arg0);

  /**
   * Translated "One or More items are directly/circularly linked to themself.".
   * 
   * @return translated "One or More items are directly/circularly linked to themself."
  
   */
  @DefaultMessage("One or More items are directly/circularly linked to themself.")
  @Key("worksheetAnalysisLinkError")
  String worksheetAnalysisLinkError();

  /**
   * Translated "Worksheet Row Selection".
   * 
   * @return translated "Worksheet Row Selection"
  
   */
  @DefaultMessage("Worksheet Row Selection")
  @Key("worksheetAnalysisSelection")
  String worksheetAnalysisSelection();

  /**
   * Translated "Worksheet Builder".
   * 
   * @return translated "Worksheet Builder"
  
   */
  @DefaultMessage("Worksheet Builder")
  @Key("worksheetBuilder")
  String worksheetBuilder();

  /**
   * Translated "Create a list of analyses for batch/run processing.".
   * 
   * @return translated "Create a list of analyses for batch/run processing."
  
   */
  @DefaultMessage("Create a list of analyses for batch/run processing.")
  @Key("worksheetBuilderDescription")
  String worksheetBuilderDescription();

  /**
   * Translated "Worksheet Analysis Selection".
   * 
   * @return translated "Worksheet Analysis Selection"
  
   */
  @DefaultMessage("Worksheet Analysis Selection")
  @Key("worksheetBuilderLookup")
  String worksheetBuilderLookup();

  /**
   * Translated "You must choose a format for this worksheet before adding analyses from another worksheet.".
   * 
   * @return translated "You must choose a format for this worksheet before adding analyses from another worksheet."
  
   */
  @DefaultMessage("You must choose a format for this worksheet before adding analyses from another worksheet.")
  @Key("worksheetChooseFormatBeforeAddFromOther")
  String worksheetChooseFormatBeforeAddFromOther();

  /**
   * Translated "Worksheet Completion".
   * 
   * @return translated "Worksheet Completion"
  
   */
  @DefaultMessage("Worksheet Completion")
  @Key("worksheetCompletion")
  String worksheetCompletion();

  /**
   * Translated "Process the worksheet by entering results, completing, and reviewing analyses.".
   * 
   * @return translated "Process the worksheet by entering results, completing, and reviewing analyses."
  
   */
  @DefaultMessage("Process the worksheet by entering results, completing, and reviewing analyses.")
  @Key("worksheetCompletionDescription")
  String worksheetCompletionDescription();

  /**
   * Translated "Worksheet edit via Excel file cancelled".
   * 
   * @return translated "Worksheet edit via Excel file cancelled"
  
   */
  @DefaultMessage("Worksheet edit via Excel file cancelled")
  @Key("worksheetCompletionEditCancelled")
  String worksheetCompletionEditCancelled();

  /**
   * Translated "Worksheet will be saved for editing to".
   * 
   * @return translated "Worksheet will be saved for editing to"
  
   */
  @DefaultMessage("Worksheet will be saved for editing to")
  @Key("worksheetCompletionEditConfirm")
  String worksheetCompletionEditConfirm();

  /**
   * Translated "Worksheet Creation".
   * 
   * @return translated "Worksheet Creation"
  
   */
  @DefaultMessage("Worksheet Creation")
  @Key("worksheetCreation")
  String worksheetCreation();

  /**
   * Translated "Create a list of analyses for batch/run processing.".
   * 
   * @return translated "Create a list of analyses for batch/run processing."
  
   */
  @DefaultMessage("Create a list of analyses for batch/run processing.")
  @Key("worksheetCreationDescription")
  String worksheetCreationDescription();

  /**
   * Translated "Worksheet Creation Lookup".
   * 
   * @return translated "Worksheet Creation Lookup"
  
   */
  @DefaultMessage("Worksheet Creation Lookup")
  @Key("worksheetCreationLookup")
  String worksheetCreationLookup();

  /**
   * Translated "Worksheets cannot be changed once they have been saved.\nAre you sure you would like to save this worksheet?".
   * 
   * @return translated "Worksheets cannot be changed once they have been saved.\nAre you sure you would like to save this worksheet?"
  
   */
  @DefaultMessage("Worksheets cannot be changed once they have been saved.\nAre you sure you would like to save this worksheet?")
  @Key("worksheetCreationSaveConfirm")
  String worksheetCreationSaveConfirm();

  /**
   * Translated "Error fetching worksheet display directory system variable".
   * 
   * @return translated "Error fetching worksheet display directory system variable"
  
   */
  @DefaultMessage("Error fetching worksheet display directory system variable")
  @Key("worksheetDisplayDirectoryLookupException")
  String worksheetDisplayDirectoryLookupException();

  /**
   * Translated "This worksheet has not been saved.\nAre you sure you would like to exit without saving?".
   * 
   * @return translated "This worksheet has not been saved.\nAre you sure you would like to exit without saving?"
  
   */
  @DefaultMessage("This worksheet has not been saved.\nAre you sure you would like to exit without saving?")
  @Key("worksheetExitConfirm")
  String worksheetExitConfirm();

  /**
   * Translated "An Excel file for this worksheet already exists, please delete it before trying to export".
   * 
   * @return translated "An Excel file for this worksheet already exists, please delete it before trying to export"
  
   */
  @DefaultMessage("An Excel file for this worksheet already exists, please delete it before trying to export")
  @Key("worksheetFileExists")
  String worksheetFileExists();

  /**
   * Translated "Worksheet File Upload".
   * 
   * @return translated "Worksheet File Upload"
  
   */
  @DefaultMessage("Worksheet File Upload")
  @Key("worksheetFileUpload")
  String worksheetFileUpload();

  /**
   * Translated "Format".
   * 
   * @return translated "Format"
  
   */
  @DefaultMessage("Format")
  @Key("worksheetFormat")
  String worksheetFormat();

  /**
   * Translated "Error loading column name/index mappings (from).\nResults will be copied by index.".
   * 
   * @return translated "Error loading column name/index mappings (from).\nResults will be copied by index."
  
   */
  @DefaultMessage("Error loading column name/index mappings (from).\nResults will be copied by index.")
  @Key("worksheetFromColumnMappingLoadError")
  String worksheetFromColumnMappingLoadError();

  /**
   * Translated "History - Worksheet".
   * 
   * @return translated "History - Worksheet"
  
   */
  @DefaultMessage("History - Worksheet")
  @Key("worksheetHistory")
  String worksheetHistory();

  /**
   * Translated "Worksheet is full, cannot add more Analyses".
   * 
   * @return translated "Worksheet is full, cannot add more Analyses"
  
   */
  @DefaultMessage("Worksheet is full, cannot add more Analyses")
  @Key("worksheetIsFull")
  String worksheetIsFull();

  /**
   * Translated "The following Analyses have not been added because their worksheet format does not match".
   * 
   * @return translated "The following Analyses have not been added because their worksheet format does not match"
  
   */
  @DefaultMessage("The following Analyses have not been added because their worksheet format does not match")
  @Key("worksheetItemsFormatConflict")
  String worksheetItemsFormatConflict();

  /**
   * Translated "The following Analyses have not been added because of permission, status, or QA Event".
   * 
   * @return translated "The following Analyses have not been added because of permission, status, or QA Event"
  
   */
  @DefaultMessage("The following Analyses have not been added because of permission, status, or QA Event")
  @Key("worksheetItemsNotAdded")
  String worksheetItemsNotAdded();

  /**
   * Translated "Worksheet Layout".
   * 
   * @return translated "Worksheet Layout"
  
   */
  @DefaultMessage("Worksheet Layout")
  @Key("worksheetLayout")
  String worksheetLayout();

  /**
   * Translated "Worksheet Lookup".
   * 
   * @return translated "Worksheet Lookup"
  
   */
  @DefaultMessage("Worksheet Lookup")
  @Key("worksheetLookup")
  String worksheetLookup();

  /**
   * Translated "More than one active QC Lot was found for position {0}".
   * 
   * @param arg0 "{0}"
   * @return translated "More than one active QC Lot was found for position {0}"
  
   */
  @DefaultMessage("More than one active QC Lot was found for position {0}")
  @Key("worksheetMultiMatchingActiveQc")
  String worksheetMultiMatchingActiveQc(String arg0);

  /**
   * Translated "You may not save an empty worksheet".
   * 
   * @return translated "You may not save an empty worksheet"
  
   */
  @DefaultMessage("You may not save an empty worksheet")
  @Key("worksheetNotSaveEmpty")
  String worksheetNotSaveEmpty();

  /**
   * Translated "Worksheet #".
   * 
   * @return translated "Worksheet #"
  
   */
  @DefaultMessage("Worksheet #")
  @Key("worksheetNumber")
  String worksheetNumber();

  /**
   * Translated "Error fetching worksheet output directory system variable".
   * 
   * @return translated "Error fetching worksheet output directory system variable"
  
   */
  @DefaultMessage("Error fetching worksheet output directory system variable")
  @Key("worksheetOutputDirectoryLookupException")
  String worksheetOutputDirectoryLookupException();

  /**
   * Translated "This is a Duplicate QC item.\nAre you sure you would like to remove it?".
   * 
   * @return translated "This is a Duplicate QC item.\nAre you sure you would like to remove it?"
  
   */
  @DefaultMessage("This is a Duplicate QC item.\nAre you sure you would like to remove it?")
  @Key("worksheetRemoveDuplicateQCConfirm")
  String worksheetRemoveDuplicateQCConfirm();

  /**
   * Translated "This QC item is a ''Last Of'' and may be removed from multiple rows.\nAre you sure you would like to remove it?".
   * 
   * @return translated "This QC item is a ''Last Of'' and may be removed from multiple rows.\nAre you sure you would like to remove it?"
  
   */
  @DefaultMessage("This QC item is a ''Last Of'' and may be removed from multiple rows.\nAre you sure you would like to remove it?")
  @Key("worksheetRemoveLastOfQCConfirm")
  String worksheetRemoveLastOfQCConfirm();

  /**
   * Translated "This is a QC item.\nAre you sure you would like to remove it?".
   * 
   * @return translated "This is a QC item.\nAre you sure you would like to remove it?"
  
   */
  @DefaultMessage("This is a QC item.\nAre you sure you would like to remove it?")
  @Key("worksheetRemoveQCConfirm")
  String worksheetRemoveQCConfirm();

  /**
   * Translated "Accession #{0} is locked by {1} until {2}".
   * 
   * @param arg0 "{0}"
   * @param arg1 "{1}"
   * @param arg2 "{2}"
   * @return translated "Accession #{0} is locked by {1} until {2}"
  
   */
  @DefaultMessage("Accession #{0} is locked by {1} until {2}")
  @Key("worksheetSampleLockException")
  String worksheetSampleLockException(String arg0,  String arg1,  String arg2);

  /**
   * Translated "Error fetching worksheet template directory system variable".
   * 
   * @return translated "Error fetching worksheet template directory system variable"
  
   */
  @DefaultMessage("Error fetching worksheet template directory system variable")
  @Key("worksheetTemplateDirectoryLookupException")
  String worksheetTemplateDirectoryLookupException();

  /**
   * Translated "The following analyses were removed due to a capacity overflow on the loaded template.\nIf you are updating this worksheet, the worksheet data for those analyses will be lost.".
   * 
   * @return translated "The following analyses were removed due to a capacity overflow on the loaded template.\nIf you are updating this worksheet, the worksheet data for those analyses will be lost."
  
   */
  @DefaultMessage("The following analyses were removed due to a capacity overflow on the loaded template.\nIf you are updating this worksheet, the worksheet data for those analyses will be lost.")
  @Key("worksheetTemplateRemovedAnalyses")
  String worksheetTemplateRemovedAnalyses();

  /**
   * Translated "Error loading column name/index mappings (to).\nResults will be copied by index.".
   * 
   * @return translated "Error loading column name/index mappings (to).\nResults will be copied by index."
  
   */
  @DefaultMessage("Error loading column name/index mappings (to).\nResults will be copied by index.")
  @Key("worksheetToColumnMappingLoadError")
  String worksheetToColumnMappingLoadError();

  /**
   * Translated "Username".
   * 
   * @return translated "Username"
  
   */
  @DefaultMessage("Username")
  @Key("worksheetUser")
  String worksheetUser();

  /**
   * Translated "The selected analyses are from multiple tests, please select analyses that belong to only one test.".
   * 
   * @return translated "The selected analyses are from multiple tests, please select analyses that belong to only one test."
  
   */
  @DefaultMessage("The selected analyses are from multiple tests, please select analyses that belong to only one test.")
  @Key("worksheet_oneTestForEditMultiple")
  String worksheet_oneTestForEditMultiple();

  /**
   * Translated "The selected analyses use multiple units, please select analyses that use only one unit.".
   * 
   * @return translated "The selected analyses use multiple units, please select analyses that use only one unit."
  
   */
  @DefaultMessage("The selected analyses use multiple units, please select analyses that use only one unit.")
  @Key("worksheet_oneUnitForEditMultiple")
  String worksheet_oneUnitForEditMultiple();

  /**
   * Translated "Some analyses were not checked for transfer due to a duplicate being present.".
   * 
   * @return translated "Some analyses were not checked for transfer due to a duplicate being present."
  
   */
  @DefaultMessage("Some analyses were not checked for transfer due to a duplicate being present.")
  @Key("worksheet_uncheckedDuplicateAnalyses")
  String worksheet_uncheckedDuplicateAnalyses();

  /**
   * Translated "The status of the shipping record must be either \"Processed\" or \"Shipped\"".
   * 
   * @return translated "The status of the shipping record must be either \"Processed\" or \"Shipped\""
  
   */
  @DefaultMessage("The status of the shipping record must be either \"Processed\" or \"Shipped\"")
  @Key("wrongStatusForProcessing")
  String wrongStatusForProcessing();

  /**
   * Translated "The status of the sample record must be \"Not Verified\"".
   * 
   * @return translated "The status of the sample record must be \"Not Verified\""
  
   */
  @DefaultMessage("The status of the sample record must be \"Not Verified\"")
  @Key("wrongStatusForVerifying")
  String wrongStatusForVerifying();

  /**
   * Translated "Status needs to be ''Initiated'' , ''On Hold'' or ''Logged-In'' to complete".
   * 
   * @return translated "Status needs to be ''Initiated'' , ''On Hold'' or ''Logged-In'' to complete"
  
   */
  @DefaultMessage("Status needs to be ''Initiated'' , ''On Hold'' or ''Logged-In'' to complete")
  @Key("wrongStatusNoComplete")
  String wrongStatusNoComplete();

  /**
   * Translated "Status must not be ''Released'', ''Cancelled'' or ''In-Prep'' to initiate".
   * 
   * @return translated "Status must not be ''Released'', ''Cancelled'' or ''In-Prep'' to initiate"
  
   */
  @DefaultMessage("Status must not be ''Released'', ''Cancelled'' or ''In-Prep'' to initiate")
  @Key("wrongStatusNoInitiate")
  String wrongStatusNoInitiate();

  /**
   * Translated "Status needs to be ''Released'' to unrelease".
   * 
   * @return translated "Status needs to be ''Released'' to unrelease"
  
   */
  @DefaultMessage("Status needs to be ''Released'' to unrelease")
  @Key("wrongStatusUnrelease")
  String wrongStatusUnrelease();

  /**
   * Translated "Yes".
   * 
   * @return translated "Yes"
  
   */
  @DefaultMessage("Yes")
  @Key("yes")
  String yes();

  /**
   * Translated "Yesterday".
   * 
   * @return translated "Yesterday"
  
   */
  @DefaultMessage("Yesterday")
  @Key("yesterday")
  String yesterday();

  /**
   * Translated "There needs to be at least one order item in your order".
   * 
   * @return translated "There needs to be at least one order item in your order"
  
   */
  @DefaultMessage("There needs to be at least one order item in your order")
  @Key("zeroOrderItemsException")
  String zeroOrderItemsException();

  /**
   * Translated "Zip Code".
   * 
   * @return translated "Zip Code"
  
   */
  @DefaultMessage("Zip Code")
  @Key("zipcode")
  String zipcode();

  /**
   * Translated "99999-9999".
   * 
   * @return translated "99999-9999"
  
   */
  @DefaultMessage("99999-9999")
  @Key("zipcodePattern")
  String zipcodePattern();
}
