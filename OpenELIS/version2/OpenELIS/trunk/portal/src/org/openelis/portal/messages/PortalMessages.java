package org.openelis.portal.messages;

/**
 * Interface to represent the messages contained in resource bundle:
 * 	/home/nfalat/workspace/OpenELIS-Portal/src/org/openelis/portal/messages/PortalMessages.properties'.
 */
public interface PortalMessages extends org.openelis.ui.messages.UIMessages {
  
  /**
   * Translated "Final Report".
   * 
   * @return translated "Final Report"
   */
  @DefaultMessage("Final Report")
  @Key("finalReport")
  String finalReport();

  /**
   * Translated "Accession Number".
   * 
   * @return translated "Accession Number"
   */
  @DefaultMessage("Accession Number")
  @Key("finalReport.accessionNumber")
  String finalReport_accessionNumber();

  /**
   * Translated "ALL SAMPLES".
   * 
   * @return translated "ALL SAMPLES"
   */
  @DefaultMessage("ALL SAMPLES")
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
   * Translated "Collected Date".
   * 
   * @return translated "Collected Date"
   */
  @DefaultMessage("Collected Date")
  @Key("finalReport.collectedDate")
  String finalReport_collectedDate();

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
   * Translated "Find Samples".
   * 
   * @return translated "Find Samples"
   */
  @DefaultMessage("Find Samples")
  @Key("finalReport.getSampleList")
  String finalReport_getSampleList();

  /**
   * Translated "<span class = \"helpHeader\"> Locating Your Results </span> <p/> To locate sample results, you must know at least one or more of the following criteria: <ul><li>Your sample accession (lab) number</li> <li>The date your sample was collected</li> <li>A reference that you sent to the laboratory</li> <li>The date the lab completed (released) your test(s)</li></ul> <p/> <span class = \"helpHeader\"> To Find Sample Results </span> <ul> <li> If searching by results that have been completed (released), specify the beginning date in the <b>Released Date</b> and the ending date in the <b>to</b> field. It is recommended that you use the provided calendars to the right of these fields when inputting dates to eliminate any formatting errors. Click on <b>Today</b> at the bottom of the calendar to enter today’s date. </li><li>If you know the laboratory Accession Number (sample number, lab number), use the <b>Accession Number</b> field to specify the beginning number and the <b>to</b> field for the ending number. When looking for a single sample, just enter the number in the <b>Accession Number</b> field.</li><li>You may also search by using multiple fields. For example, to find all the samples you collected and sent to the laboratory in June of 2011, type in your name (as it appeared on the collection form) in the <b>Collector Name</b> field, select (using the calendars provided) 2011-6-1 in the <b>Collected Date</b> field, and 2011-6-30 in the <b>to</b> field.</li><li>The system supports wild card searches only in the following fields: <b>Collector Name</b>, <b>Client Reference</b>, <b>Collection Site</b>, and <b>Collection Town</b>.  By using this search method you would add “*” at the end of your search term. For example, to see a list of samples that share the first three letters “BIG”, enter “BIG*”.  Matches would include “BIG WATER”, “BIG RIVER”, “BIGGER THAN SKY”, etc. </li></ul>".
   * 
   * @return translated "<span class = \"helpHeader\"> Locating Your Results </span> <p/> To locate sample results, you must know at least one or more of the following criteria: <ul><li>Your sample accession (lab) number</li> <li>The date your sample was collected</li> <li>A reference that you sent to the laboratory</li> <li>The date the lab completed (released) your test(s)</li></ul> <p/> <span class = \"helpHeader\"> To Find Sample Results </span> <ul> <li> If searching by results that have been completed (released), specify the beginning date in the <b>Released Date</b> and the ending date in the <b>to</b> field. It is recommended that you use the provided calendars to the right of these fields when inputting dates to eliminate any formatting errors. Click on <b>Today</b> at the bottom of the calendar to enter today’s date. </li><li>If you know the laboratory Accession Number (sample number, lab number), use the <b>Accession Number</b> field to specify the beginning number and the <b>to</b> field for the ending number. When looking for a single sample, just enter the number in the <b>Accession Number</b> field.</li><li>You may also search by using multiple fields. For example, to find all the samples you collected and sent to the laboratory in June of 2011, type in your name (as it appeared on the collection form) in the <b>Collector Name</b> field, select (using the calendars provided) 2011-6-1 in the <b>Collected Date</b> field, and 2011-6-30 in the <b>to</b> field.</li><li>The system supports wild card searches only in the following fields: <b>Collector Name</b>, <b>Client Reference</b>, <b>Collection Site</b>, and <b>Collection Town</b>.  By using this search method you would add “*” at the end of your search term. For example, to see a list of samples that share the first three letters “BIG”, enter “BIG*”.  Matches would include “BIG WATER”, “BIG RIVER”, “BIGGER THAN SKY”, etc. </li></ul>"
   */
  @DefaultMessage("<span class = \"helpHeader\"> Locating Your Results </span> <p/> To locate sample results, you must know at least one or more of the following criteria: <ul><li>Your sample accession (lab) number</li> <li>The date your sample was collected</li> <li>A reference that you sent to the laboratory</li> <li>The date the lab completed (released) your test(s)</li></ul> <p/> <span class = \"helpHeader\"> To Find Sample Results </span> <ul> <li> If searching by results that have been completed (released), specify the beginning date in the <b>Released Date</b> and the ending date in the <b>to</b> field. It is recommended that you use the provided calendars to the right of these fields when inputting dates to eliminate any formatting errors. Click on <b>Today</b> at the bottom of the calendar to enter today’s date. </li><li>If you know the laboratory Accession Number (sample number, lab number), use the <b>Accession Number</b> field to specify the beginning number and the <b>to</b> field for the ending number. When looking for a single sample, just enter the number in the <b>Accession Number</b> field.</li><li>You may also search by using multiple fields. For example, to find all the samples you collected and sent to the laboratory in June of 2011, type in your name (as it appeared on the collection form) in the <b>Collector Name</b> field, select (using the calendars provided) 2011-6-1 in the <b>Collected Date</b> field, and 2011-6-30 in the <b>to</b> field.</li><li>The system supports wild card searches only in the following fields: <b>Collector Name</b>, <b>Client Reference</b>, <b>Collection Site</b>, and <b>Collection Town</b>.  By using this search method you would add “*” at the end of your search term. For example, to see a list of samples that share the first three letters “BIG”, enter “BIG*”.  Matches would include “BIG WATER”, “BIG RIVER”, “BIGGER THAN SKY”, etc. </li></ul>")
  @Key("finalReport.header1")
  String finalReport_header1();

  /**
   * Translated "<span class = \"helpHeader\"> To View the Final Reports </span><ul><li>The following is a list of sample results that match your search criteria and are available for you to view. To view all reports of the listed samples, click on the <b>Select All</b> button, and then the <b>Run Report</b> button.</li><li> To view specific reports, check the box under the <b>Select</b> column heading, and then the <b>Run Report</b> button.</li><li>The PDF report of the selected samples will appear. You may view, save, or print your PDF reports.</li></ul>".
   * 
   * @return translated "<span class = \"helpHeader\"> To View the Final Reports </span><ul><li>The following is a list of sample results that match your search criteria and are available for you to view. To view all reports of the listed samples, click on the <b>Select All</b> button, and then the <b>Run Report</b> button.</li><li> To view specific reports, check the box under the <b>Select</b> column heading, and then the <b>Run Report</b> button.</li><li>The PDF report of the selected samples will appear. You may view, save, or print your PDF reports.</li></ul>"
   */
  @DefaultMessage("<span class = \"helpHeader\"> To View the Final Reports </span><ul><li>The following is a list of sample results that match your search criteria and are available for you to view. To view all reports of the listed samples, click on the <b>Select All</b> button, and then the <b>Run Report</b> button.</li><li> To view specific reports, check the box under the <b>Select</b> column heading, and then the <b>Run Report</b> button.</li><li>The PDF report of the selected samples will appear. You may view, save, or print your PDF reports.</li></ul>")
  @Key("finalReport.header2")
  String finalReport_header2();

  /**
   * Translated "accession number".
   * 
   * @return translated "accession number"
   */
  @DefaultMessage("accession number")
  @Key("finalReport.help.accession")
  String finalReport_help_accession();

  /**
   * Translated "client reference".
   * 
   * @return translated "client reference"
   */
  @DefaultMessage("client reference")
  @Key("finalReport.help.clientReference")
  String finalReport_help_clientReference();

  /**
   * Translated "collected date".
   * 
   * @return translated "collected date"
   */
  @DefaultMessage("collected date")
  @Key("finalReport.help.collected")
  String finalReport_help_collected();

  /**
   * Translated "collector".
   * 
   * @return translated "collector"
   */
  @DefaultMessage("collector")
  @Key("finalReport.help.collector")
  String finalReport_help_collector();

  /**
   * Translated "patient birth date".
   * 
   * @return translated "patient birth date"
   */
  @DefaultMessage("patient birth date")
  @Key("finalReport.help.patientBirth")
  String finalReport_help_patientBirth();

  /**
   * Translated "patient first name".
   * 
   * @return translated "patient first name"
   */
  @DefaultMessage("patient first name")
  @Key("finalReport.help.patientFirst")
  String finalReport_help_patientFirst();

  /**
   * Translated "patient last name".
   * 
   * @return translated "patient last name"
   */
  @DefaultMessage("patient last name")
  @Key("finalReport.help.patientLast")
  String finalReport_help_patientLast();

  /**
   * Translated "project code".
   * 
   * @return translated "project code"
   */
  @DefaultMessage("project code")
  @Key("finalReport.help.project")
  String finalReport_help_project();

  /**
   * Translated "pws id".
   * 
   * @return translated "pws id"
   */
  @DefaultMessage("pws id")
  @Key("finalReport.help.pws")
  String finalReport_help_pws();

  /**
   * Translated "released date".
   * 
   * @return translated "released date"
   */
  @DefaultMessage("released date")
  @Key("finalReport.help.released")
  String finalReport_help_released();

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
   * Translated "Project Code".
   * 
   * @return translated "Project Code"
   */
  @DefaultMessage("Project Code")
  @Key("finalReport.projectCode")
  String finalReport_projectCode();

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
   * Translated "To".
   * 
   * @return translated "To"
   */
  @DefaultMessage("To")
  @Key("gen.to")
  String gen_to();

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
}
