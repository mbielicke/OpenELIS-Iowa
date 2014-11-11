package org.openelis.portal.messages;

/**
 * Interface to represent the messages contained in resource bundle:
 * 	/home/nfalat/workspace/OpenELIS-Portal/src/org/openelis/portal/messages/PortalMessages.properties'.
 */
public interface PortalMessages extends org.openelis.ui.messages.UIMessages {
  
  /**
   * Translated "Completed".
   * 
   * @return translated "Completed"
   */
  @DefaultMessage("Completed")
  @Key("analysis.completed")
  String analysis_completed();

  /**
   * Translated "Method".
   * 
   * @return translated "Method"
   */
  @DefaultMessage("Method")
  @Key("analysis.method")
  String analysis_method();

  /**
   * Translated "QA Event".
   * 
   * @return translated "QA Event"
   */
  @DefaultMessage("QA Event")
  @Key("analysis.qaEvent")
  String analysis_qaEvent();

  /**
   * Translated "Revision".
   * 
   * @return translated "Revision"
   */
  @DefaultMessage("Revision")
  @Key("analysis.revision")
  String analysis_revision();

  /**
   * Translated "Started".
   * 
   * @return translated "Started"
   */
  @DefaultMessage("Started")
  @Key("analysis.started")
  String analysis_started();

  /**
   * Translated "Test".
   * 
   * @return translated "Test"
   */
  @DefaultMessage("Test")
  @Key("analysis.test")
  String analysis_test();

  /**
   * Translated "Unit".
   * 
   * @return translated "Unit"
   */
  @DefaultMessage("Unit")
  @Key("analysis.unit")
  String analysis_unit();

  /**
   * Translated "Select Analysis Fields For Output".
   * 
   * @return translated "Select Analysis Fields For Output"
   */
  @DefaultMessage("Select Analysis Fields For Output")
  @Key("dataView.analysisFields")
  String dataView_analysisFields();

  /**
   * Translated "Test Analyte".
   * 
   * @return translated "Test Analyte"
   */
  @DefaultMessage("Test Analyte")
  @Key("dataView.analyte")
  String dataView_analyte();

  /**
   * Translated "Auxiliary Data".
   * 
   * @return translated "Auxiliary Data"
   */
  @DefaultMessage("Auxiliary Data")
  @Key("dataView.aux")
  String dataView_aux();

  /**
   * Translated "Continue to Report Fields".
   * 
   * @return translated "Continue to Report Fields"
   */
  @DefaultMessage("Continue to Report Fields")
  @Key("dataView.continue")
  String dataView_continue();

  /**
   * Translated "no results were found".
   * 
   * @return translated "no results were found"
   */
  @DefaultMessage("no results were found")
  @Key("dataView.error.noResults")
  String dataView_error_noResults();

  /**
   * Translated "The lab number assigned to the sample by SHL. Enter the accession number in this field to search for a single sample or enter the beginning number in the Accession Number field and the ending number in the To field to search for a series of samples.".
   * 
   * @return translated "The lab number assigned to the sample by SHL. Enter the accession number in this field to search for a single sample or enter the beginning number in the Accession Number field and the ending number in the To field to search for a series of samples."
   */
  @DefaultMessage("The lab number assigned to the sample by SHL. Enter the accession number in this field to search for a single sample or enter the beginning number in the Accession Number field and the ending number in the To field to search for a series of samples.")
  @Key("dataView.help.accession")
  String dataView_help_accession();

  /**
   * Translated "This value is one item that ties your information to the sample paperwork. It could be your organization''s lab number or another unique identifier for a sample that you collect.".
   * 
   * @return translated "This value is one item that ties your information to the sample paperwork. It could be your organization''s lab number or another unique identifier for a sample that you collect."
   */
  @DefaultMessage("This value is one item that ties your information to the sample paperwork. It could be your organization''s lab number or another unique identifier for a sample that you collect.")
  @Key("dataView.help.clientReference")
  String dataView_help_clientReference();

  /**
   * Translated "The date that the sample was collected. You must enter a beginning date in the Collected Date field and an ending date in the To field.".
   * 
   * @return translated "The date that the sample was collected. You must enter a beginning date in the Collected Date field and an ending date in the To field."
   */
  @DefaultMessage("The date that the sample was collected. You must enter a beginning date in the Collected Date field and an ending date in the To field.")
  @Key("dataView.help.collected")
  String dataView_help_collected();

  /**
   * Translated "TODO".
   * 
   * @return translated "TODO"
   */
  @DefaultMessage("TODO")
  @Key("dataView.help.collectionSite")
  String dataView_help_collectionSite();

  /**
   * Translated "TODO".
   * 
   * @return translated "TODO"
   */
  @DefaultMessage("TODO")
  @Key("dataView.help.collectionTown")
  String dataView_help_collectionTown();

  /**
   * Translated "The name or ID of the collector of the sample.".
   * 
   * @return translated "The name or ID of the collector of the sample."
   */
  @DefaultMessage("The name or ID of the collector of the sample.")
  @Key("dataView.help.collector")
  String dataView_help_collector();

  /**
   * Translated "The name of the project that has been assigned to the sample. A project can be used to group samples with a similar purpose.".
   * 
   * @return translated "The name of the project that has been assigned to the sample. A project can be used to group samples with a similar purpose."
   */
  @DefaultMessage("The name of the project that has been assigned to the sample. A project can be used to group samples with a similar purpose.")
  @Key("dataView.help.project")
  String dataView_help_project();

  /**
   * Translated "The date and time that the sample was released (finalized). You must enter a beginning date and time in the Released Date field and an ending date and time in the To field.".
   * 
   * @return translated "The date and time that the sample was released (finalized). You must enter a beginning date and time in the Released Date field and an ending date and time in the To field."
   */
  @DefaultMessage("The date and time that the sample was released (finalized). You must enter a beginning date and time in the Released Date field and an ending date and time in the To field.")
  @Key("dataView.help.released")
  String dataView_help_released();

  /**
   * Translated "<span style=\"float:left;font-size:13pt;\" class = \"helpHeader\"> Locating Your Result </span><span style=\"float:left;font-size:11pt;\"><ul><li>To locate <span style=\"color:red;font-weight:bold;\">Test Analytes</span> and <span style=\"color:red;font-weight:bold;\">Auxiliary Data</span>, enter information in one or more search fields. It is recommended that you use the provided calendars to the right of these fields when inputting dates to eliminate any formatting errors. Click on <b>Today</b> at the bottom of the calendar to enter today''s date.<li>The system supports wild card searches only in the following fields: <span style=\"color:red;font-weight:bold;\">Collector Name</span>, <span style=\"color:red;font-weight:bold;\">Client Reference</span>, <span style=\"color:red;font-weight:bold;\">Collection Site</span>, and <span style=\"color:red;font-weight:bold;\">Collection Town</span>.  By using this search method you would add \"*\" at the end of your search term. For example, to see a list of samples that share the first three letters \"BIG\", enter \"BIG*\".  Matches would include \"BIG WATER\", \"BIG RIVER\", \"BIGGER THAN SKY\", etc.</li><li>You may also narrow your search by entering information in multiple fields.</li><li>Click the <span style=\"color:red;font-weight:bold;\">Continue to Report Fields</span> button to view a list of available report fields for sample, organization, and analysis information.</li></ul></span><span class = \"helpHeader\"><br/><br/><br/><br/><br/><br/><br/><br/><br/> Disclaimer: </span> <ul> <li>Results from this report represent analytical values as of the date they are generated.  Future revisions may affect these results and official final results should be reviewed from the <span style=\"color:red;font-weight:bold;\">Environmental Final Report</span> function to assure their accuracy.</li></ul>".
   * 
   * @return translated "<span style=\"float:left;font-size:13pt;\" class = \"helpHeader\"> Locating Your Result </span><span style=\"float:left;font-size:11pt;\"><ul><li>To locate <span style=\"color:red;font-weight:bold;\">Test Analytes</span> and <span style=\"color:red;font-weight:bold;\">Auxiliary Data</span>, enter information in one or more search fields. It is recommended that you use the provided calendars to the right of these fields when inputting dates to eliminate any formatting errors. Click on <b>Today</b> at the bottom of the calendar to enter today''s date.<li>The system supports wild card searches only in the following fields: <span style=\"color:red;font-weight:bold;\">Collector Name</span>, <span style=\"color:red;font-weight:bold;\">Client Reference</span>, <span style=\"color:red;font-weight:bold;\">Collection Site</span>, and <span style=\"color:red;font-weight:bold;\">Collection Town</span>.  By using this search method you would add \"*\" at the end of your search term. For example, to see a list of samples that share the first three letters \"BIG\", enter \"BIG*\".  Matches would include \"BIG WATER\", \"BIG RIVER\", \"BIGGER THAN SKY\", etc.</li><li>You may also narrow your search by entering information in multiple fields.</li><li>Click the <span style=\"color:red;font-weight:bold;\">Continue to Report Fields</span> button to view a list of available report fields for sample, organization, and analysis information.</li></ul></span><span class = \"helpHeader\"><br/><br/><br/><br/><br/><br/><br/><br/><br/> Disclaimer: </span> <ul> <li>Results from this report represent analytical values as of the date they are generated.  Future revisions may affect these results and official final results should be reviewed from the <span style=\"color:red;font-weight:bold;\">Environmental Final Report</span> function to assure their accuracy.</li></ul>"
   */
  @DefaultMessage("<span style=\"float:left;font-size:13pt;\" class = \"helpHeader\"> Locating Your Result </span><span style=\"float:left;font-size:11pt;\"><ul><li>To locate <span style=\"color:red;font-weight:bold;\">Test Analytes</span> and <span style=\"color:red;font-weight:bold;\">Auxiliary Data</span>, enter information in one or more search fields. It is recommended that you use the provided calendars to the right of these fields when inputting dates to eliminate any formatting errors. Click on <b>Today</b> at the bottom of the calendar to enter today''s date.<li>The system supports wild card searches only in the following fields: <span style=\"color:red;font-weight:bold;\">Collector Name</span>, <span style=\"color:red;font-weight:bold;\">Client Reference</span>, <span style=\"color:red;font-weight:bold;\">Collection Site</span>, and <span style=\"color:red;font-weight:bold;\">Collection Town</span>.  By using this search method you would add \"*\" at the end of your search term. For example, to see a list of samples that share the first three letters \"BIG\", enter \"BIG*\".  Matches would include \"BIG WATER\", \"BIG RIVER\", \"BIGGER THAN SKY\", etc.</li><li>You may also narrow your search by entering information in multiple fields.</li><li>Click the <span style=\"color:red;font-weight:bold;\">Continue to Report Fields</span> button to view a list of available report fields for sample, organization, and analysis information.</li></ul></span><span class = \"helpHeader\"><br/><br/><br/><br/><br/><br/><br/><br/><br/> Disclaimer: </span> <ul> <li>Results from this report represent analytical values as of the date they are generated.  Future revisions may affect these results and official final results should be reviewed from the <span style=\"color:red;font-weight:bold;\">Environmental Final Report</span> function to assure their accuracy.</li></ul>")
  @Key("dataView.help.screen")
  String dataView_help_screen();

  /**
   * Translated "Select Organization Fields For Output".
   * 
   * @return translated "Select Organization Fields For Output"
   */
  @DefaultMessage("Select Organization Fields For Output")
  @Key("dataView.orgFields")
  String dataView_orgFields();

  /**
   * Translated "Select Patient Fields For Output".
   * 
   * @return translated "Select Patient Fields For Output"
   */
  @DefaultMessage("Select Patient Fields For Output")
  @Key("dataView.patientFields")
  String dataView_patientFields();

  /**
   * Translated "Query".
   * 
   * @return translated "Query"
   */
  @DefaultMessage("Query")
  @Key("dataView.query")
  String dataView_query();

  /**
   * Translated "Select Sample Fields For Output".
   * 
   * @return translated "Select Sample Fields For Output"
   */
  @DefaultMessage("Select Sample Fields For Output")
  @Key("dataView.sampleFields")
  String dataView_sampleFields();

  /**
   * Translated "Please select at least one test analyte or aux data".
   * 
   * @return translated "Please select at least one test analyte or aux data"
   */
  @DefaultMessage("Please select at least one test analyte or aux data")
  @Key("dataView.selectOneAnaOrAux")
  String dataView_selectOneAnaOrAux();

  /**
   * Translated "Selected Analytes".
   * 
   * @return translated "Selected Analytes"
   */
  @DefaultMessage("Selected Analytes")
  @Key("dataView.selectedAnalytes")
  String dataView_selectedAnalytes();

  /**
   * Translated "Email".
   * 
   * @return translated "Email"
   */
  @DefaultMessage("Email")
  @Key("emailNotification.email")
  String emailNotification_email();

  /**
   * Translated "Organization".
   * 
   * @return translated "Organization"
   */
  @DefaultMessage("Organization")
  @Key("emailNotification.org")
  String emailNotification_org();

  /**
   * Translated "You do not have permission to access {0}".
   * 
   * @return translated "You do not have permission to access {0}"
   */
  @DefaultMessage("You do not have permission to access {0}")
  @Key("error.screenPerm")
  String error_screenPerm(String arg0);

  /**
   * Translated "ANY SAMPLES".
   * 
   * @return translated "ANY SAMPLES"
   */
  @DefaultMessage("ANY SAMPLES")
  @Key("finalReport.anySamples")
  String finalReport_anySamples();

  /**
   * Translated "CLINICAL SAMPLES ONLY".
   * 
   * @return translated "CLINICAL SAMPLES ONLY"
   */
  @DefaultMessage("CLINICAL SAMPLES ONLY")
  @Key("finalReport.clinicalOnly")
  String finalReport_clinicalOnly();

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
   * Translated "<span style=\"float:left;font-size:13pt;\" class = \"helpHeader\"> Locating Your Results </span> <br/> <span style=\"float:left;font-size:11pt;\"> <p/> You only need to enter information in one field to conduct a search for your results. Examples include: <span style=\"color:red;font-weight:bold;\">Collected Date</span>, <span style=\"color:red;font-weight:bold;\">Accession Number</span>, <span style=\"color:red;font-weight:bold;\">Collector Name</span>, or <span style=\"color:red;font-weight:bold;\">Patient''s Last Name</span> <p/> It is recommended that you use the calendar to the right of each date field when entering a date to eliminate any formatting errors. You must click out of the calendar to enter the selected date. <p/> You may also narrow your search by entering information in multiple fields. For example, to find all of the samples that you collected and sent to the laboratory in June of 2014, enter your name (as it appeared on the collection form) in the <span style=\"color:red;font-weight:bold;\">Collector Name</span> field and select (using the calendars provided) 2014-06-01 in the <span style=\"color:red;font-weight:bold;\">Collected Date</span> field and 2014-06-30 in the <span style=\"color:red;font-weight:bold;\">To</span> field.<p/> The system supports wild card searches in the following fields: <span style=\"color:red;font-weight:bold;\">Client Reference</span>, <span style=\"color:red;font-weight:bold;\">Collector Name</span>, <span style=\"color:red;font-weight:bold;\">PWS ID</span>, <span style=\"color:red;font-weight:bold;\">Patient''s First Name</span>, and <span style=\"color:red;font-weight:bold;\">Patient''s Last Name</span>. By using this search method you would add an * at the beginning and/or end of your search term. <p/> For example, to see a list of patient results for a patient whose last name starts with the letters <i>schm</i>, enter <i>schm*</i> in the <span style=\"color:red;font-weight:bold;\">Patient''s Last Name</span> field. Matches would include: <i>schmidt</i>, <i>schmitt</i>, <i>schmitz</i>, etc. <p/> In another example, to see a list of results for samples with a collector name of <i>buck</i>, enter <i>*buck*</i> in the <span style=\"color:red;font-weight:bold;\">Collector Name</span> field. Matches would include: <i>joseph buck</i>, <i>joe buck</i>, <i>j buck</i>, <i>buck joseph</i>, <i>buck joe</i>, <i>buck j</i>, etc.<p/> Click the <span style=\"color:red;font-weight:bold;\">Find Samples</span> button to display a list of samples that match your search criteria. The <span style=\"color:red;font-weight:bold;\">Reset</span> button clears all of the search fields.</span>".
   * 
   * @return translated "<span style=\"float:left;font-size:13pt;\" class = \"helpHeader\"> Locating Your Results </span> <br/> <span style=\"float:left;font-size:11pt;\"> <p/> You only need to enter information in one field to conduct a search for your results. Examples include: <span style=\"color:red;font-weight:bold;\">Collected Date</span>, <span style=\"color:red;font-weight:bold;\">Accession Number</span>, <span style=\"color:red;font-weight:bold;\">Collector Name</span>, or <span style=\"color:red;font-weight:bold;\">Patient''s Last Name</span> <p/> It is recommended that you use the calendar to the right of each date field when entering a date to eliminate any formatting errors. You must click out of the calendar to enter the selected date. <p/> You may also narrow your search by entering information in multiple fields. For example, to find all of the samples that you collected and sent to the laboratory in June of 2014, enter your name (as it appeared on the collection form) in the <span style=\"color:red;font-weight:bold;\">Collector Name</span> field and select (using the calendars provided) 2014-06-01 in the <span style=\"color:red;font-weight:bold;\">Collected Date</span> field and 2014-06-30 in the <span style=\"color:red;font-weight:bold;\">To</span> field.<p/> The system supports wild card searches in the following fields: <span style=\"color:red;font-weight:bold;\">Client Reference</span>, <span style=\"color:red;font-weight:bold;\">Collector Name</span>, <span style=\"color:red;font-weight:bold;\">PWS ID</span>, <span style=\"color:red;font-weight:bold;\">Patient''s First Name</span>, and <span style=\"color:red;font-weight:bold;\">Patient''s Last Name</span>. By using this search method you would add an * at the beginning and/or end of your search term. <p/> For example, to see a list of patient results for a patient whose last name starts with the letters <i>schm</i>, enter <i>schm*</i> in the <span style=\"color:red;font-weight:bold;\">Patient''s Last Name</span> field. Matches would include: <i>schmidt</i>, <i>schmitt</i>, <i>schmitz</i>, etc. <p/> In another example, to see a list of results for samples with a collector name of <i>buck</i>, enter <i>*buck*</i> in the <span style=\"color:red;font-weight:bold;\">Collector Name</span> field. Matches would include: <i>joseph buck</i>, <i>joe buck</i>, <i>j buck</i>, <i>buck joseph</i>, <i>buck joe</i>, <i>buck j</i>, etc.<p/> Click the <span style=\"color:red;font-weight:bold;\">Find Samples</span> button to display a list of samples that match your search criteria. The <span style=\"color:red;font-weight:bold;\">Reset</span> button clears all of the search fields.</span>"
   */
  @DefaultMessage("<span style=\"float:left;font-size:13pt;\" class = \"helpHeader\"> Locating Your Results </span> <br/> <span style=\"float:left;font-size:11pt;\"> <p/> You only need to enter information in one field to conduct a search for your results. Examples include: <span style=\"color:red;font-weight:bold;\">Collected Date</span>, <span style=\"color:red;font-weight:bold;\">Accession Number</span>, <span style=\"color:red;font-weight:bold;\">Collector Name</span>, or <span style=\"color:red;font-weight:bold;\">Patient''s Last Name</span> <p/> It is recommended that you use the calendar to the right of each date field when entering a date to eliminate any formatting errors. You must click out of the calendar to enter the selected date. <p/> You may also narrow your search by entering information in multiple fields. For example, to find all of the samples that you collected and sent to the laboratory in June of 2014, enter your name (as it appeared on the collection form) in the <span style=\"color:red;font-weight:bold;\">Collector Name</span> field and select (using the calendars provided) 2014-06-01 in the <span style=\"color:red;font-weight:bold;\">Collected Date</span> field and 2014-06-30 in the <span style=\"color:red;font-weight:bold;\">To</span> field.<p/> The system supports wild card searches in the following fields: <span style=\"color:red;font-weight:bold;\">Client Reference</span>, <span style=\"color:red;font-weight:bold;\">Collector Name</span>, <span style=\"color:red;font-weight:bold;\">PWS ID</span>, <span style=\"color:red;font-weight:bold;\">Patient''s First Name</span>, and <span style=\"color:red;font-weight:bold;\">Patient''s Last Name</span>. By using this search method you would add an * at the beginning and/or end of your search term. <p/> For example, to see a list of patient results for a patient whose last name starts with the letters <i>schm</i>, enter <i>schm*</i> in the <span style=\"color:red;font-weight:bold;\">Patient''s Last Name</span> field. Matches would include: <i>schmidt</i>, <i>schmitt</i>, <i>schmitz</i>, etc. <p/> In another example, to see a list of results for samples with a collector name of <i>buck</i>, enter <i>*buck*</i> in the <span style=\"color:red;font-weight:bold;\">Collector Name</span> field. Matches would include: <i>joseph buck</i>, <i>joe buck</i>, <i>j buck</i>, <i>buck joseph</i>, <i>buck joe</i>, <i>buck j</i>, etc.<p/> Click the <span style=\"color:red;font-weight:bold;\">Find Samples</span> button to display a list of samples that match your search criteria. The <span style=\"color:red;font-weight:bold;\">Reset</span> button clears all of the search fields.</span>")
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
   * Translated "Reference Information".
   * 
   * @return translated "Reference Information"
   */
  @DefaultMessage("Reference Information")
  @Key("finalReport.referenceInfo")
  String finalReport_referenceInfo();

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
   * Translated "Status".
   * 
   * @return translated "Status"
   */
  @DefaultMessage("Status")
  @Key("finalReport.select.status")
  String finalReport_select_status();

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
   * Translated "Back To Search".
   * 
   * @return translated "Back To Search"
   */
  @DefaultMessage("Back To Search")
  @Key("gen.back")
  String gen_back();

  /**
   * Translated "Fetching samples....".
   * 
   * @return translated "Fetching samples...."
   */
  @DefaultMessage("Fetching samples....")
  @Key("gen.fetchingSamples")
  String gen_fetchingSamples();

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
   * Translated "Data View by Spreadsheet".
   * 
   * @return translated "Data View by Spreadsheet"
   */
  @DefaultMessage("Data View by Spreadsheet")
  @Key("main.dataView")
  String main_dataView();

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
   * Translated "Address".
   * 
   * @return translated "Address"
   */
  @DefaultMessage("Address")
  @Key("org.address")
  String org_address();

  /**
   * Translated "Apt/Suite #".
   * 
   * @return translated "Apt/Suite #"
   */
  @DefaultMessage("Apt/Suite #")
  @Key("org.apt")
  String org_apt();

  /**
   * Translated "City".
   * 
   * @return translated "City"
   */
  @DefaultMessage("City")
  @Key("org.city")
  String org_city();

  /**
   * Translated "Name".
   * 
   * @return translated "Name"
   */
  @DefaultMessage("Name")
  @Key("org.name")
  String org_name();

  /**
   * Translated "State".
   * 
   * @return translated "State"
   */
  @DefaultMessage("State")
  @Key("org.state")
  String org_state();

  /**
   * Translated "Zip Code".
   * 
   * @return translated "Zip Code"
   */
  @DefaultMessage("Zip Code")
  @Key("org.zip")
  String org_zip();

  /**
   * Translated "Birth Date".
   * 
   * @return translated "Birth Date"
   */
  @DefaultMessage("Birth Date")
  @Key("patient.birthDate")
  String patient_birthDate();

  /**
   * Translated "Ethnicity".
   * 
   * @return translated "Ethnicity"
   */
  @DefaultMessage("Ethnicity")
  @Key("patient.ethnicity")
  String patient_ethnicity();

  /**
   * Translated "First Name".
   * 
   * @return translated "First Name"
   */
  @DefaultMessage("First Name")
  @Key("patient.firstName")
  String patient_firstName();

  /**
   * Translated "Gender".
   * 
   * @return translated "Gender"
   */
  @DefaultMessage("Gender")
  @Key("patient.gender")
  String patient_gender();

  /**
   * Translated "Last Name".
   * 
   * @return translated "Last Name"
   */
  @DefaultMessage("Last Name")
  @Key("patient.lastName")
  String patient_lastName();

  /**
   * Translated "Race".
   * 
   * @return translated "Race"
   */
  @DefaultMessage("Race")
  @Key("patient.race")
  String patient_race();

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
   * Translated "Accession Number".
   * 
   * @return translated "Accession Number"
   */
  @DefaultMessage("Accession Number")
  @Key("sample.accessionNumber")
  String sample_accessionNumber();

  /**
   * Translated "Client Reference".
   * 
   * @return translated "Client Reference"
   */
  @DefaultMessage("Client Reference")
  @Key("sample.clientReference")
  String sample_clientReference();

  /**
   * Translated "Collected Date".
   * 
   * @return translated "Collected Date"
   */
  @DefaultMessage("Collected Date")
  @Key("sample.collectedDate")
  String sample_collectedDate();

  /**
   * Translated "Collection Site".
   * 
   * @return translated "Collection Site"
   */
  @DefaultMessage("Collection Site")
  @Key("sample.collectionSite")
  String sample_collectionSite();

  /**
   * Translated "Collection Town".
   * 
   * @return translated "Collection Town"
   */
  @DefaultMessage("Collection Town")
  @Key("sample.collectionTown")
  String sample_collectionTown();

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
   * Translated "Location City".
   * 
   * @return translated "Location City"
   */
  @DefaultMessage("Location City")
  @Key("sample.locationCity")
  String sample_locationCity();

  /**
   * Translated "Phone #".
   * 
   * @return translated "Phone #"
   */
  @DefaultMessage("Phone #")
  @Key("sample.phone")
  String sample_phone();

  /**
   * Translated "Project Code".
   * 
   * @return translated "Project Code"
   */
  @DefaultMessage("Project Code")
  @Key("sample.projectCode")
  String sample_projectCode();

  /**
   * Translated "Received".
   * 
   * @return translated "Received"
   */
  @DefaultMessage("Received")
  @Key("sample.received")
  String sample_received();

  /**
   * Translated "Released".
   * 
   * @return translated "Released"
   */
  @DefaultMessage("Released")
  @Key("sample.released")
  String sample_released();

  /**
   * Translated "Released Date".
   * 
   * @return translated "Released Date"
   */
  @DefaultMessage("Released Date")
  @Key("sample.releasedDate")
  String sample_releasedDate();

  /**
   * Translated "Source".
   * 
   * @return translated "Source"
   */
  @DefaultMessage("Source")
  @Key("sample.source")
  String sample_source();

  /**
   * Translated "Status".
   * 
   * @return translated "Status"
   */
  @DefaultMessage("Status")
  @Key("sample.status")
  String sample_status();

  /**
   * Translated "Sample Type".
   * 
   * @return translated "Sample Type"
   */
  @DefaultMessage("Sample Type")
  @Key("sample.type")
  String sample_type();

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
   * Translated "<span style=\"float:left;font-size:13pt;\" class = \"helpHeader\">Locating Samples Sent to the Laboratory </span><br/><br/><span style=\"float:left;font-size:11pt;\">To locate samples submitted to the laboratory, you must know at least one or more of the following criteria: <ul><li>Collection Date</li><li>Accession Number</li><li>Client Reference</li><li>Project Code</li></ul></span><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><span style=\"float:left;font-size:13pt;\" class = \"helpHeader\">To Find the Status of Samples </span><br/><span style=\"float:left;font-size:11pt;\"><ul><li>If searching by sample collection date, specify the beginning date in the <span style=\"color:red;font-weight:bold;\">Collected Date</span> and the ending date in the <span style=\"color:red;font-weight:bold;\">To</span> field. It is recommended that you use the provided calendars to the right of these fields when inputting dates to eliminate any formatting errors. Click on <b>Today</b> at the bottom of the calendar to enter today''s date. </li><li>To locate samples by the reference information that you supplied on the collection form, enter the reference information in the <span style=\"color:red;font-weight:bold;\">Client Reference</span> field. You may use part of a reference by adding a \"*\" at the end of your search term (known as a wildcard search). For example, to see a list of samples that share the first three letters \"BIG\", enter \"BIG*\". Matches would include \"BIG WATER\", \"BIG RIVER\", \"BIGGER THAN SKY\", etc. </li><li>You may also narrow your search by entering information in multiple fields.</li></ul></span>".
   * 
   * @return translated "<span style=\"float:left;font-size:13pt;\" class = \"helpHeader\">Locating Samples Sent to the Laboratory </span><br/><br/><span style=\"float:left;font-size:11pt;\">To locate samples submitted to the laboratory, you must know at least one or more of the following criteria: <ul><li>Collection Date</li><li>Accession Number</li><li>Client Reference</li><li>Project Code</li></ul></span><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><span style=\"float:left;font-size:13pt;\" class = \"helpHeader\">To Find the Status of Samples </span><br/><span style=\"float:left;font-size:11pt;\"><ul><li>If searching by sample collection date, specify the beginning date in the <span style=\"color:red;font-weight:bold;\">Collected Date</span> and the ending date in the <span style=\"color:red;font-weight:bold;\">To</span> field. It is recommended that you use the provided calendars to the right of these fields when inputting dates to eliminate any formatting errors. Click on <b>Today</b> at the bottom of the calendar to enter today''s date. </li><li>To locate samples by the reference information that you supplied on the collection form, enter the reference information in the <span style=\"color:red;font-weight:bold;\">Client Reference</span> field. You may use part of a reference by adding a \"*\" at the end of your search term (known as a wildcard search). For example, to see a list of samples that share the first three letters \"BIG\", enter \"BIG*\". Matches would include \"BIG WATER\", \"BIG RIVER\", \"BIGGER THAN SKY\", etc. </li><li>You may also narrow your search by entering information in multiple fields.</li></ul></span>"
   */
  @DefaultMessage("<span style=\"float:left;font-size:13pt;\" class = \"helpHeader\">Locating Samples Sent to the Laboratory </span><br/><br/><span style=\"float:left;font-size:11pt;\">To locate samples submitted to the laboratory, you must know at least one or more of the following criteria: <ul><li>Collection Date</li><li>Accession Number</li><li>Client Reference</li><li>Project Code</li></ul></span><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><span style=\"float:left;font-size:13pt;\" class = \"helpHeader\">To Find the Status of Samples </span><br/><span style=\"float:left;font-size:11pt;\"><ul><li>If searching by sample collection date, specify the beginning date in the <span style=\"color:red;font-weight:bold;\">Collected Date</span> and the ending date in the <span style=\"color:red;font-weight:bold;\">To</span> field. It is recommended that you use the provided calendars to the right of these fields when inputting dates to eliminate any formatting errors. Click on <b>Today</b> at the bottom of the calendar to enter today''s date. </li><li>To locate samples by the reference information that you supplied on the collection form, enter the reference information in the <span style=\"color:red;font-weight:bold;\">Client Reference</span> field. You may use part of a reference by adding a \"*\" at the end of your search term (known as a wildcard search). For example, to see a list of samples that share the first three letters \"BIG\", enter \"BIG*\". Matches would include \"BIG WATER\", \"BIG RIVER\", \"BIGGER THAN SKY\", etc. </li><li>You may also narrow your search by entering information in multiple fields.</li></ul></span>")
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
