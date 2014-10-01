package org.openelis.portal.messages;

/**
 * Interface to represent the messages contained in resource bundle:
 * 	/home/nfalat/workspace/OpenELIS-Portal/src/org/openelis/portal/messages/PortalMessages.properties'.
 */
public interface PortalMessages extends org.openelis.ui.messages.UIMessages {
  
  /**
   * Translated "Data View by Spreadsheet".
   * 
   * @return translated "Data View by Spreadsheet"
   */
  @DefaultMessage("Data View by Spreadsheet")
  @Key("dataView")
  String dataView();

  /**
   * Translated "You do not have permission to access {0}".
   * 
   * @return translated "You do not have permission to access {0}"
   */
  @DefaultMessage("You do not have permission to access {0}")
  @Key("error.screenPerm")
  String error_screenPerm(String arg0);

  /**
   * Translated "Accession Number".
   * 
   * @return translated "Accession Number"
   */
  @DefaultMessage("Accession Number")
  @Key("finalReport.accessionNumber")
  String finalReport_accessionNumber();

  /**
   * Translated "ANY SAMPLES".
   * 
   * @return translated "ANY SAMPLES"
   */
  @DefaultMessage("ANY SAMPLES")
  @Key("finalReport.anySamples")
  String finalReport_anySamples();

  /**
   * Translated "Back To Search".
   * 
   * @return translated "Back To Search"
   */
  @DefaultMessage("Back To Search")
  @Key("finalReport.back")
  String finalReport_back();

  /**
   * Translated "CLINICAL SAMPLES ONLY".
   * 
   * @return translated "CLINICAL SAMPLES ONLY"
   */
  @DefaultMessage("CLINICAL SAMPLES ONLY")
  @Key("finalReport.clinicalOnly")
  String finalReport_clinicalOnly();

  /**
   * Translated "Collector Name".
   * 
   * @return translated "Collector Name"
   */
  @DefaultMessage("Collector Name")
  @Key("finalReport.collector")
  String finalReport_collector();

  /**
   * Translated "ENVIRONMENTAL SAMPLES ONLY".
   * 
   * @return translated "ENVIRONMENTAL SAMPLES ONLY"
   */
  @DefaultMessage("ENVIRONMENTAL SAMPLES ONLY")
  @Key("finalReport.environmentalOnly")
  String finalReport_environmentalOnly();

  /**
   * Translated "Please query for at least one field".
   * 
   * @return translated "Please query for at least one field"
   */
  @DefaultMessage("Please query for at least one field")
  @Key("finalReport.error.emptyQueryException")
  String finalReport_error_emptyQueryException();

  /**
   * Translated "Please select at least one sample, then press Run Report".
   * 
   * @return translated "Please select at least one sample, then press Run Report"
   */
  @DefaultMessage("Please select at least one sample, then press Run Report")
  @Key("finalReport.error.noSampleSelected")
  String finalReport_error_noSampleSelected();

  /**
   * Translated "No samples returned".
   * 
   * @return translated "No samples returned"
   */
  @DefaultMessage("No samples returned")
  @Key("finalReport.error.noSamples")
  String finalReport_error_noSamples();

  /**
   * Translated "Must enter start accession number with end accession number".
   * 
   * @return translated "Must enter start accession number with end accession number"
   */
  @DefaultMessage("Must enter start accession number with end accession number")
  @Key("finalReport.error.noStartAccession")
  String finalReport_error_noStartAccession();

  /**
   * Translated "Must enter start date with end date".
   * 
   * @return translated "Must enter start date with end date"
   */
  @DefaultMessage("Must enter start date with end date")
  @Key("finalReport.error.noStartDate")
  String finalReport_error_noStartDate();

  /**
   * Translated "Please query for only one of environmental/SDWIS/clinical at a time.".
   * 
   * @return translated "Please query for only one of environmental/SDWIS/clinical at a time."
   */
  @DefaultMessage("Please query for only one of environmental/SDWIS/clinical at a time.")
  @Key("finalReport.error.queryDomainException")
  String finalReport_error_queryDomainException();

  /**
   * Translated "Find Samples".
   * 
   * @return translated "Find Samples"
   */
  @DefaultMessage("Find Samples")
  @Key("finalReport.getSampleList")
  String finalReport_getSampleList();

  /**
   * Translated "The lab number assigned to the sample by SHL. Enter the accession number in this field to search for a single sample or enter the beginning number in the Accession Number field and the ending number in the To field to search for a series of samples.".
   * 
   * @return translated "The lab number assigned to the sample by SHL. Enter the accession number in this field to search for a single sample or enter the beginning number in the Accession Number field and the ending number in the To field to search for a series of samples."
   */
  @DefaultMessage("The lab number assigned to the sample by SHL. Enter the accession number in this field to search for a single sample or enter the beginning number in the Accession Number field and the ending number in the To field to search for a series of samples.")
  @Key("finalReport.help.accession")
  String finalReport_help_accession();

  /**
   * Translated "This value is one item that ties your information to the sample paperwork. It could be your organization''s lab number or another unique identifier for a sample that you collect.".
   * 
   * @return translated "This value is one item that ties your information to the sample paperwork. It could be your organization''s lab number or another unique identifier for a sample that you collect."
   */
  @DefaultMessage("This value is one item that ties your information to the sample paperwork. It could be your organization''s lab number or another unique identifier for a sample that you collect.")
  @Key("finalReport.help.clientReference")
  String finalReport_help_clientReference();

  /**
   * Translated "The date that the sample was collected. You must enter a beginning date in the Collected Date field and an ending date in the To field.".
   * 
   * @return translated "The date that the sample was collected. You must enter a beginning date in the Collected Date field and an ending date in the To field."
   */
  @DefaultMessage("The date that the sample was collected. You must enter a beginning date in the Collected Date field and an ending date in the To field.")
  @Key("finalReport.help.collected")
  String finalReport_help_collected();

  /**
   * Translated "The name or ID of the collector of the environmental sample.".
   * 
   * @return translated "The name or ID of the collector of the environmental sample."
   */
  @DefaultMessage("The name or ID of the collector of the environmental sample.")
  @Key("finalReport.help.envCollector")
  String finalReport_help_envCollector();

  /**
   * Translated "The patient''s date of birth for a clinical sample. You must enter an ending date in the To field to search for a range of birth dates.".
   * 
   * @return translated "The patient''s date of birth for a clinical sample. You must enter an ending date in the To field to search for a range of birth dates."
   */
  @DefaultMessage("The patient''s date of birth for a clinical sample. You must enter an ending date in the To field to search for a range of birth dates.")
  @Key("finalReport.help.patientBirth")
  String finalReport_help_patientBirth();

  /**
   * Translated "The first name of the patient for a clinical sample.".
   * 
   * @return translated "The first name of the patient for a clinical sample."
   */
  @DefaultMessage("The first name of the patient for a clinical sample.")
  @Key("finalReport.help.patientFirst")
  String finalReport_help_patientFirst();

  /**
   * Translated "The last name of the patient for a clinical sample.".
   * 
   * @return translated "The last name of the patient for a clinical sample."
   */
  @DefaultMessage("The last name of the patient for a clinical sample.")
  @Key("finalReport.help.patientLast")
  String finalReport_help_patientLast();

  /**
   * Translated "The name of the project that has been assigned to the sample. A project can be used to group samples with a similar purpose.".
   * 
   * @return translated "The name of the project that has been assigned to the sample. A project can be used to group samples with a similar purpose."
   */
  @DefaultMessage("The name of the project that has been assigned to the sample. A project can be used to group samples with a similar purpose.")
  @Key("finalReport.help.project")
  String finalReport_help_project();

  /**
   * Translated "The Public Water Supply ID is a unique identifier assigned by the state to all public water supplies. The PWS Id begins with a two letter state abbreviation followed by a unique seven digit number.".
   * 
   * @return translated "The Public Water Supply ID is a unique identifier assigned by the state to all public water supplies. The PWS Id begins with a two letter state abbreviation followed by a unique seven digit number."
   */
  @DefaultMessage("The Public Water Supply ID is a unique identifier assigned by the state to all public water supplies. The PWS Id begins with a two letter state abbreviation followed by a unique seven digit number.")
  @Key("finalReport.help.pws")
  String finalReport_help_pws();

  /**
   * Translated "The date and time that the sample was released (finalized). You must enter a beginning date and time in the Released Date field and an ending date and time in the To field.".
   * 
   * @return translated "The date and time that the sample was released (finalized). You must enter a beginning date and time in the Released Date field and an ending date and time in the To field."
   */
  @DefaultMessage("The date and time that the sample was released (finalized). You must enter a beginning date and time in the Released Date field and an ending date and time in the To field.")
  @Key("finalReport.help.released")
  String finalReport_help_released();

  /**
   * Translated "<span style=\"float:left;font-size:11pt;\" class = \"helpHeader\"> Locating Your Results </span> <br/> <p/> You only need to enter information in one field to conduct a search for your results. Examples include: <b>Collected Date</b>, <b>Accession Number</b>, <b>Collector Name</b>, or <b>Patient''s Last Name</b> <p/> It is recommended that you use the calendar to the right of each date field when entering a date to eliminate any formatting errors. You must click out of the calendar to enter the selected date. <p/> You may also narrow your search by entering information in multiple fields. For example, to find all of the samples that you collected and sent to the laboratory in June of 2014, enter your name (as it appeared on the collection form) in the <b>Collector Name</b> field and select (using the calendars provided) 2014-06-01 in the <b>Collected Date</b> field and 2014-06-30 in the <b>To</b> field.<p/> The system supports wild card searches in the following fields: <b>Client Reference</b>, <b>Collector Name</b>, <b>PWS ID</b>, <b>Patient''s First Name</b>, and <b>Patient''s Last Name</b>. By using this search method you would add an * at the beginning and/or end of your search term. <p/> For example, to see a list of patient results for a patient whose last name starts with the letters <i>schm</i>, enter <i>schm*</i> in the <b>Patient''s Last Name</b> field. Matches would include: <i>schmidt</i>, <i>schmitt</i>, <i>schmitz</i>, etc. <p/> In another example, to see a list of results for samples with a collector name of <i>buck</i>, enter <i>*buck*</i> in the <b>Collector Name</b> field. Matches would include: <i>joseph buck</i>, <i>joe buck</i>, <i>j buck</i>, <i>buck joseph</i>, <i>buck joe</i>, <i>buck j</i>, etc.<p/> Click the <b>Find Samples</b> button to display a list of samples that match your search criteria. The <b>Reset</b> button clears all of the search fields.".
   * 
   * @return translated "<span style=\"float:left;font-size:11pt;\" class = \"helpHeader\"> Locating Your Results </span> <br/> <p/> You only need to enter information in one field to conduct a search for your results. Examples include: <b>Collected Date</b>, <b>Accession Number</b>, <b>Collector Name</b>, or <b>Patient''s Last Name</b> <p/> It is recommended that you use the calendar to the right of each date field when entering a date to eliminate any formatting errors. You must click out of the calendar to enter the selected date. <p/> You may also narrow your search by entering information in multiple fields. For example, to find all of the samples that you collected and sent to the laboratory in June of 2014, enter your name (as it appeared on the collection form) in the <b>Collector Name</b> field and select (using the calendars provided) 2014-06-01 in the <b>Collected Date</b> field and 2014-06-30 in the <b>To</b> field.<p/> The system supports wild card searches in the following fields: <b>Client Reference</b>, <b>Collector Name</b>, <b>PWS ID</b>, <b>Patient''s First Name</b>, and <b>Patient''s Last Name</b>. By using this search method you would add an * at the beginning and/or end of your search term. <p/> For example, to see a list of patient results for a patient whose last name starts with the letters <i>schm</i>, enter <i>schm*</i> in the <b>Patient''s Last Name</b> field. Matches would include: <i>schmidt</i>, <i>schmitt</i>, <i>schmitz</i>, etc. <p/> In another example, to see a list of results for samples with a collector name of <i>buck</i>, enter <i>*buck*</i> in the <b>Collector Name</b> field. Matches would include: <i>joseph buck</i>, <i>joe buck</i>, <i>j buck</i>, <i>buck joseph</i>, <i>buck joe</i>, <i>buck j</i>, etc.<p/> Click the <b>Find Samples</b> button to display a list of samples that match your search criteria. The <b>Reset</b> button clears all of the search fields."
   */
  @DefaultMessage("<span style=\"float:left;font-size:11pt;\" class = \"helpHeader\"> Locating Your Results </span> <br/> <p/> You only need to enter information in one field to conduct a search for your results. Examples include: <b>Collected Date</b>, <b>Accession Number</b>, <b>Collector Name</b>, or <b>Patient''s Last Name</b> <p/> It is recommended that you use the calendar to the right of each date field when entering a date to eliminate any formatting errors. You must click out of the calendar to enter the selected date. <p/> You may also narrow your search by entering information in multiple fields. For example, to find all of the samples that you collected and sent to the laboratory in June of 2014, enter your name (as it appeared on the collection form) in the <b>Collector Name</b> field and select (using the calendars provided) 2014-06-01 in the <b>Collected Date</b> field and 2014-06-30 in the <b>To</b> field.<p/> The system supports wild card searches in the following fields: <b>Client Reference</b>, <b>Collector Name</b>, <b>PWS ID</b>, <b>Patient''s First Name</b>, and <b>Patient''s Last Name</b>. By using this search method you would add an * at the beginning and/or end of your search term. <p/> For example, to see a list of patient results for a patient whose last name starts with the letters <i>schm</i>, enter <i>schm*</i> in the <b>Patient''s Last Name</b> field. Matches would include: <i>schmidt</i>, <i>schmitt</i>, <i>schmitz</i>, etc. <p/> In another example, to see a list of results for samples with a collector name of <i>buck</i>, enter <i>*buck*</i> in the <b>Collector Name</b> field. Matches would include: <i>joseph buck</i>, <i>joe buck</i>, <i>j buck</i>, <i>buck joseph</i>, <i>buck joe</i>, <i>buck j</i>, etc.<p/> Click the <b>Find Samples</b> button to display a list of samples that match your search criteria. The <b>Reset</b> button clears all of the search fields.")
  @Key("finalReport.help.screen")
  String finalReport_help_screen();

  /**
   * Translated "The name or ID of the collector of the safe drinking water sample.".
   * 
   * @return translated "The name or ID of the collector of the safe drinking water sample."
   */
  @DefaultMessage("The name or ID of the collector of the safe drinking water sample.")
  @Key("finalReport.help.sdwisCollector")
  String finalReport_help_sdwisCollector();

  /**
   * Translated "Patient''s Date of Birth".
   * 
   * @return translated "Patient''s Date of Birth"
   */
  @DefaultMessage("Patient''s Date of Birth")
  @Key("finalReport.patientBirth")
  String finalReport_patientBirth();

  /**
   * Translated "Patient''s First Name".
   * 
   * @return translated "Patient''s First Name"
   */
  @DefaultMessage("Patient''s First Name")
  @Key("finalReport.patientFirst")
  String finalReport_patientFirst();

  /**
   * Translated "Patient''s Last Name".
   * 
   * @return translated "Patient''s Last Name"
   */
  @DefaultMessage("Patient''s Last Name")
  @Key("finalReport.patientLast")
  String finalReport_patientLast();

  /**
   * Translated "Project".
   * 
   * @return translated "Project"
   */
  @DefaultMessage("Project")
  @Key("finalReport.project")
  String finalReport_project();

  /**
   * Translated "Project Code".
   * 
   * @return translated "Project Code"
   */
  @DefaultMessage("Project Code")
  @Key("finalReport.projectCode")
  String finalReport_projectCode();

  /**
   * Translated "Reference Information".
   * 
   * @return translated "Reference Information"
   */
  @DefaultMessage("Reference Information")
  @Key("finalReport.referenceInfo")
  String finalReport_referenceInfo();

  /**
   * Translated "Released Date".
   * 
   * @return translated "Released Date"
   */
  @DefaultMessage("Released Date")
  @Key("finalReport.releasedDate")
  String finalReport_releasedDate();

  /**
   * Translated "Reset".
   * 
   * @return translated "Reset"
   */
  @DefaultMessage("Reset")
  @Key("finalReport.reset")
  String finalReport_reset();

  /**
   * Translated "Run Report".
   * 
   * @return translated "Run Report"
   */
  @DefaultMessage("Run Report")
  @Key("finalReport.run")
  String finalReport_run();

  /**
   * Translated "SDWIS SAMPLES ONLY".
   * 
   * @return translated "SDWIS SAMPLES ONLY"
   */
  @DefaultMessage("SDWIS SAMPLES ONLY")
  @Key("finalReport.sdwisOnly")
  String finalReport_sdwisOnly();

  /**
   * Translated "Select".
   * 
   * @return translated "Select"
   */
  @DefaultMessage("Select")
  @Key("finalReport.select")
  String finalReport_select();

  /**
   * Translated "Accession #".
   * 
   * @return translated "Accession #"
   */
  @DefaultMessage("Accession #")
  @Key("finalReport.select.accession")
  String finalReport_select_accession();

  /**
   * Translated "Collection Site".
   * 
   * @return translated "Collection Site"
   */
  @DefaultMessage("Collection Site")
  @Key("finalReport.select.site")
  String finalReport_select_site();

  /**
   * Translated "Status".
   * 
   * @return translated "Status"
   */
  @DefaultMessage("Status")
  @Key("finalReport.select.status")
  String finalReport_select_status();

  /**
   * Translated "Collection Town".
   * 
   * @return translated "Collection Town"
   */
  @DefaultMessage("Collection Town")
  @Key("finalReport.select.town")
  String finalReport_select_town();

  /**
   * Translated "Select All".
   * 
   * @return translated "Select All"
   */
  @DefaultMessage("Select All")
  @Key("finalReport.selectAll")
  String finalReport_selectAll();

  /**
   * Translated "Unselect All".
   * 
   * @return translated "Unselect All"
   */
  @DefaultMessage("Unselect All")
  @Key("finalReport.unselectAll")
  String finalReport_unselectAll();

  /**
   * Translated "Accession #".
   * 
   * @return translated "Accession #"
   */
  @DefaultMessage("Accession #")
  @Key("gen.accessionNumber")
  String gen_accessionNumber();

  /**
   * Translated "Collected Date".
   * 
   * @return translated "Collected Date"
   */
  @DefaultMessage("Collected Date")
  @Key("gen.collectedDate")
  String gen_collectedDate();

  /**
   * Translated "Generating report....".
   * 
   * @return translated "Generating report...."
   */
  @DefaultMessage("Generating report....")
  @Key("gen.genReportMessage")
  String gen_genReportMessage();

  /**
   * Translated "To".
   * 
   * @return translated "To"
   */
  @DefaultMessage("To")
  @Key("gen.to")
  String gen_to();

  /**
   * Translated "Account Information".
   * 
   * @return translated "Account Information"
   */
  @DefaultMessage("Account Information")
  @Key("main.accountInfo")
  String main_accountInfo();

  /**
   * Translated "Email Notification".
   * 
   * @return translated "Email Notification"
   */
  @DefaultMessage("Email Notification")
  @Key("main.emailNotification")
  String main_emailNotification();

  /**
   * Translated "Final Report".
   * 
   * @return translated "Final Report"
   */
  @DefaultMessage("Final Report")
  @Key("main.finalReport")
  String main_finalReport();

  /**
   * Translated "Logout".
   * 
   * @return translated "Logout"
   */
  @DefaultMessage("Logout")
  @Key("main.logout")
  String main_logout();

  /**
   * Translated "Reports".
   * 
   * @return translated "Reports"
   */
  @DefaultMessage("Reports")
  @Key("main.reports")
  String main_reports();

  /**
   * Translated "Sample Status".
   * 
   * @return translated "Sample Status"
   */
  @DefaultMessage("Sample Status")
  @Key("main.sampleStatus")
  String main_sampleStatus();

  /**
   * Translated "Clinical Test Request Form".
   * 
   * @return translated "Clinical Test Request Form"
   */
  @DefaultMessage("Clinical Test Request Form")
  @Key("main.testRequest")
  String main_testRequest();

  /**
   * Translated "Project".
   * 
   * @return translated "Project"
   */
  @DefaultMessage("Project")
  @Key("project.project")
  String project_project();

  /**
   * Translated "PWS ID".
   * 
   * @return translated "PWS ID"
   */
  @DefaultMessage("PWS ID")
  @Key("pws.id")
  String pws_id();

  /**
   * Translated "Client Reference".
   * 
   * @return translated "Client Reference"
   */
  @DefaultMessage("Client Reference")
  @Key("sample.clientReference")
  String sample_clientReference();

  /**
   * Translated "Completed".
   * 
   * @return translated "Completed"
   */
  @DefaultMessage("Completed")
  @Key("sampleStatus.completed")
  String sampleStatus_completed();

  /**
   * Translated "Date Received".
   * 
   * @return translated "Date Received"
   */
  @DefaultMessage("Date Received")
  @Key("sampleStatus.dateReceived")
  String sampleStatus_dateReceived();

  /**
   * Translated "Sample/Test Description".
   * 
   * @return translated "Sample/Test Description"
   */
  @DefaultMessage("Sample/Test Description")
  @Key("sampleStatus.description")
  String sampleStatus_description();

  /**
   * Translated "<span class = \"helpHeader\">Locating Samples Sent to the Laboratory </span><p/> To locate samples submitted to the laboratory, you must know at least one or more of the following criteria: <ul><li>Collection Date</li><li>Accession Number</li><li>Client Reference</li><li>Project Code</li></ul><p/> <span class = \"helpHeader\">To Find the Status of Samples </span><ul> <li> If searching by sample collection date, specify the beginning date in the <b>Collected Date</b> and the ending date in the <b>to</b> field. It is recommended that you use the provided calendars to the right of these fields when inputting dates to eliminate any formatting errors. Click on <b>Today</b> at the bottom of the calendar to enter today’s date. </li><li>To locate samples by the reference information that you supplied on the collection form, enter the reference information in the <b>Client Reference</b> field. You may use part of a reference by adding a “*” at the end of your search term (known as a wildcard search). For example, to see a list of samples that share the first three letters “BIG”, enter “BIG*”. Matches would include “BIG WATER”, “BIG RIVER”, “BIGGER THAN SKY”, etc. </li><li>You may also narrow your search by entering information in multiple fields.</li></ul>".
   * 
   * @return translated "<span class = \"helpHeader\">Locating Samples Sent to the Laboratory </span><p/> To locate samples submitted to the laboratory, you must know at least one or more of the following criteria: <ul><li>Collection Date</li><li>Accession Number</li><li>Client Reference</li><li>Project Code</li></ul><p/> <span class = \"helpHeader\">To Find the Status of Samples </span><ul> <li> If searching by sample collection date, specify the beginning date in the <b>Collected Date</b> and the ending date in the <b>to</b> field. It is recommended that you use the provided calendars to the right of these fields when inputting dates to eliminate any formatting errors. Click on <b>Today</b> at the bottom of the calendar to enter today’s date. </li><li>To locate samples by the reference information that you supplied on the collection form, enter the reference information in the <b>Client Reference</b> field. You may use part of a reference by adding a “*” at the end of your search term (known as a wildcard search). For example, to see a list of samples that share the first three letters “BIG”, enter “BIG*”. Matches would include “BIG WATER”, “BIG RIVER”, “BIGGER THAN SKY”, etc. </li><li>You may also narrow your search by entering information in multiple fields.</li></ul>"
   */
  @DefaultMessage("<span class = \"helpHeader\">Locating Samples Sent to the Laboratory </span><p/> To locate samples submitted to the laboratory, you must know at least one or more of the following criteria: <ul><li>Collection Date</li><li>Accession Number</li><li>Client Reference</li><li>Project Code</li></ul><p/> <span class = \"helpHeader\">To Find the Status of Samples </span><ul> <li> If searching by sample collection date, specify the beginning date in the <b>Collected Date</b> and the ending date in the <b>to</b> field. It is recommended that you use the provided calendars to the right of these fields when inputting dates to eliminate any formatting errors. Click on <b>Today</b> at the bottom of the calendar to enter today’s date. </li><li>To locate samples by the reference information that you supplied on the collection form, enter the reference information in the <b>Client Reference</b> field. You may use part of a reference by adding a “*” at the end of your search term (known as a wildcard search). For example, to see a list of samples that share the first three letters “BIG”, enter “BIG*”. Matches would include “BIG WATER”, “BIG RIVER”, “BIGGER THAN SKY”, etc. </li><li>You may also narrow your search by entering information in multiple fields.</li></ul>")
  @Key("sampleStatus.help.screen")
  String sampleStatus_help_screen();

  /**
   * Translated "In Progress".
   * 
   * @return translated "In Progress"
   */
  @DefaultMessage("In Progress")
  @Key("sampleStatus.inProgress")
  String sampleStatus_inProgress();

  /**
   * Translated "QA Event".
   * 
   * @return translated "QA Event"
   */
  @DefaultMessage("QA Event")
  @Key("sampleStatus.qaEvent")
  String sampleStatus_qaEvent();

  /**
   * Translated "Test Status".
   * 
   * @return translated "Test Status"
   */
  @DefaultMessage("Test Status")
  @Key("sampleStatus.testStatus")
  String sampleStatus_testStatus();
}
