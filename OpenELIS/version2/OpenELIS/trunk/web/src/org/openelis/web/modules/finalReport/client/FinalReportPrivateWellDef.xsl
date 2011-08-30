<!--
Exhibit A - UIRF Open-source Based Public Software License.
  
The contents of this file are subject to the UIRF Open-source Based
Public Software License(the "License"); you may not use this file except
in compliance with the License. You may obtain a copy of the License at
openelis.uhl.uiowa.edu
  
Software distributed under the License is distributed on an "AS IS"
basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
License for the specific language governing rights and limitations
under the License.
  
The Original Code is OpenELIS code.
  
The Initial Developer of the Original Code is The University of Iowa.
Portions created by The University of Iowa are Copyright 2006-2008. All
Rights Reserved.
  
Contributor(s): ______________________________________.
  
Alternatively, the contents of this file marked
"Separately-Licensed" may be used under the terms of a UIRF Software
license ("UIRF Software License"), in which case the provisions of a
UIRF Software License are applicable instead of those above. 
  -->
<xsl:stylesheet
  extension-element-prefixes="resource"
  version="1.0"
  xmlns:fn="http://www.w3.org/2005/xpath-functions"
  xmlns:locale="xalan://java.util.Locale"
  xmlns:meta="xalan://org.openelis.meta.SampleWebMeta"
  xmlns:resource="xalan://org.openelis.util.UTFResource"
  xmlns:xalan="http://xml.apache.org/xalan"
  xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xsi:noNamespaceSchemaLocation="http://openelis.uhl.uiowa.edu/schema/ScreenSchema.xsd"
  xsi:schemaLocation="http://www.w3.org/1999/XSL/Transform http://openelis.uhl.uiowa.edu/schema/XSLTSchema.xsd">

  <xalan:component prefix="resource">
    <xalan:script lang="javaclass" src="xalan://org.openelis.util.UTFResource" />
  </xalan:component>
  <xalan:component prefix="locale">
    <xalan:script lang="javaclass" src="xalan://java.util.Locale" />
  </xalan:component>
  <xsl:variable name="language">
    <xsl:value-of select="doc/locale" />
  </xsl:variable>
  <xsl:variable name="props">
    <xsl:value-of select="doc/props" />
  </xsl:variable>
  <xsl:variable name="constants" select="resource:getBundle(string($props),locale:new(string($language)))" />
  <xsl:template match="doc">
    <screen id="FinalReportPrivateWell" name="{resource:getString($constants,'finalReport')}">
      <DeckPanel height="100%" key="deck" width="100%" style="ContentPanel">
        <deck>
          <HorizontalPanel padding="0" spacing="0" >
            <VerticalPanel padding="0" spacing="0">
              <VerticalPanel style="subform">
                <TablePanel style="Form">
                  <row>
                    <text style="Prompt">
                      <xsl:value-of select="resource:getString($constants,'dateReleased')" />:
                    </text>
                    <widget>
                      <calendar begin="0" end="2" key="{meta:getReleasedDateFrom()}" pattern="{resource:getString($constants,'datePattern')}" width="90" />
                    </widget>
                    <text style="Prompt">
                      <xsl:value-of select="resource:getString($constants,'to')" />:
                    </text>
                    <widget>
                      <calendar begin="0" end="2" key="{meta:getReleasedDateTo()}" pattern="{resource:getString($constants,'datePattern')}" width="90" />
                    </widget>
                  </row>
                  <row>
                    <text style="Prompt">
                      <xsl:value-of select="resource:getString($constants,'dateCollected')" />:
                    </text>
                    <widget>
                      <calendar begin="0" end="2" key="{meta:getCollectionDateFrom()}" pattern="{resource:getString($constants,'datePattern')}" width="90" />
                    </widget>
                    <text style="Prompt">
                      <xsl:value-of select="resource:getString($constants,'to')" />:
                    </text>
                    <widget>
                      <calendar begin="0" end="2" key="{meta:getCollectionDateTo()}" pattern="{resource:getString($constants,'datePattern')}" width="90" />
                    </widget>
                  </row>
                  <row>
                    <text style="Prompt">
                      <xsl:value-of select="resource:getString($constants,'accessionNumber')" />:
                    </text>
                    <widget>
                      <textbox field="Integer" key="{meta:getAccessionNumberFrom()}" max="60" width="86" />
                    </widget>
                    <text style="Prompt">
                      <xsl:value-of select="resource:getString($constants,'to')" />:
                    </text>
                    <widget>
                      <textbox field="Integer" key="{meta:getAccessionNumberTo()}" max="60" width="86" />
                    </widget>
                  </row>
                  <row>
                    <text style="Prompt">
                      <xsl:value-of select='resource:getString($constants,"collectorName")' />:
                    </text>
                    <widget colspan="4">
                      <textbox case="MIXED" field="String" key="{meta:getWellCollector()}" max="60" width="202" />
                    </widget>
                  </row>
                  <row>
                    <text style="Prompt">
                      <xsl:value-of select='resource:getString($constants,"clientReference")' />:
                    </text>
                    <widget colspan="4">
                      <textbox case="MIXED" field="String" key="{meta:getClientReference()}" max="60" width="202" />
                    </widget>
                  </row>
                  <row>
                    <text style="Prompt">
                      <xsl:value-of select='resource:getString($constants,"collectionSite")' />:
                    </text>
                    <widget colspan="4">
                      <textbox case="MIXED" field="String" key="{meta:getWellLocation()}" max="60" width="202" />
                    </widget>
                  </row>
                  <row>
                    <text style="Prompt">
                      <xsl:value-of select='resource:getString($constants,"collectionTown")' />:
                    </text>
                    <widget colspan="4">
                      <textbox case="MIXED" field="String" key="{meta:getLocationAddrCity()}" max="60" width="202" />
                    </widget>
                  </row>
                  <row>
                    <text style="Prompt">
                      <xsl:value-of select='resource:getString($constants,"owner")' />:
                    </text>
                    <widget colspan="4">
                      <textbox case="MIXED" field="String" key="{meta:getWellOwner()}" max="60" width="202" />
                    </widget>
                  </row>
                  <row>
                    <text style="Prompt">
                      <xsl:value-of select='resource:getString($constants,"projectCode")' />:
                    </text>
                    <widget colspan="4">
                      <dropdown field="Integer" key="{meta:getProjectId()}" tab="{meta:getReleasedDateFrom()},{meta:getWellOwner()}" width="202" />
                    </widget>
                  </row>
                </TablePanel>
              </VerticalPanel>
              <TablePanel style="HorizontalDivider" width="100%">
                <row>
                  <html>&lt;hr/&gt;</html>
                </row>
              </TablePanel>
              <HorizontalPanel style="TableFooterPanel">
                <widget halign="center">
                  <appButton key="getSampleListButton" style="Button">
                    <HorizontalPanel>
                      <AbsolutePanel />
                      <text>
                        <xsl:value-of select='resource:getString($constants,"getSampleList")' />
                      </text>
                    </HorizontalPanel>
                  </appButton>
                </widget>
                <widget halign="center">
                  <appButton key="resetButton" style="Button">
                    <HorizontalPanel>
                      <AbsolutePanel />
                      <text>
                        <xsl:value-of select='resource:getString($constants,"reset")' />
                      </text>
                    </HorizontalPanel>
                  </appButton>
                </widget>
              </HorizontalPanel>
              <TablePanel style="Form">
                <row>
                  <AbsolutePanel key="noSampleSelectedPanel" style="largeWarnIcon" visible="false"></AbsolutePanel>
                  <label field="String" key="noSampleSelected" style="Prompt"></label>
                </row>
              </TablePanel>
            </VerticalPanel>
            <VerticalPanel padding="0" spacing="0" style="help">
              <TablePanel width="100%">
                <row>
                  <html> <![CDATA[<span class = \"helpHeader\"> Locating your result: </span> <p/> To find sample results, you must know your sample accession number, or the date that the sample was collected, or a reference that you sent to the laboratory, or the date the laboratory completed (released) your test. To locate sample(s), fill in one or more search fields. Click the \"Get Samples\" button to view a list of samples that match your search criteria. <p/> <span class = \"helpHeader\"> To find sample results: </span> <ul> <li> If searching by results that have been completed (released), specify the beginning date in the \"Released Date\" and the ending date in the \"to\" field (you may use the calendar button to select dates). </li><li>If you know the laboratory Accession Number (sample number, lab number), use the \"Accession Number\" field to specify the beginning number and the \"to\" for the ending number. When looking for a single sample, just enter the \"Accession Number\" field.</li><li>You may also search by multiple fields. For example, to find all the samples that you collected and sent to the laboratory in June of 2011, type in your name (as it appeared in the collection form) in the \"Collector Name\", 2011-6-1 in \"Collected Date\", and 2011-6-30 in the \"to\" field.</li></ul>]]></html>
                </row>
              </TablePanel>
            </VerticalPanel>
          </HorizontalPanel>
        </deck>
        <deck>
          <VerticalPanel padding="0" spacing="0">
            <VerticalPanel style="help">
              <TablePanel>
               <row> 
                 <html> <![CDATA[<span class = \"helpHeader\"> To view final reports: </span> The following is a list of sample results that match your search and are available for you to view. To view all the samples, click on the \"Select All\" button, and then \"Get Reports\" button. To selectively view some of the reports, click the check box under \"Select\" column heading, and then \"Get Reports\" button. The PDF report of all the selected samples will appear shortly. You may view, save, or print your PDF reports.]]></html>
               </row>               
              </TablePanel>
            </VerticalPanel>
            <VerticalPanel>
              <widget valign="top">
                <table key="sampleEntTable" maxRows="12" showScroll="ALWAYS" style="ScreenTableWithSides" title="" width="auto">
                  <col header="{resource:getString($constants,'select')}" key="select" width="60">
                    <check />
                  </col>
                  <col header="{resource:getString($constants,'AccessionNo')}" key="accessionNumber" width="120">
                    <textbox field="Integer" />
                  </col>
                  <col header="{resource:getString($constants,'collectionSite')}" key="collectionSite" width="120">
                    <textbox field="String" />
                  </col>
                  <col header="{resource:getString($constants,'dateCollected')}" key="collectedDate" width="120">
                    <label field="Date" pattern="{resource:getString($constants,'dateTimeMinutePattern')}" />
                  </col>
                  <col header="{resource:getString($constants,'collectorName')}" key="collectorName" width="120">
                    <textbox field="String" />
                  </col>
                  <col header="{resource:getString($constants,'status')}" key="status" width="120">
                    <dropdown field="Integer" width="120" />
                  </col>
                  <col header="{resource:getString($constants,'collectionTown')}" key="town" width="120">
                    <textbox field="String" />
                  </col>
                  <col header="{resource:getString($constants,'owner')}" key="owner" width="120">
                    <textbox field="String" />
                  </col>
                </table>
              </widget>
            </VerticalPanel>
            <VerticalPanel height="20">
              <label field="String" key="numSampleSelected" style="Prompt" />
            </VerticalPanel>
            <HorizontalPanel style="TableFooterPanel">
              <widget halign="center">
                <appButton key="backButton" style="Button" visible="false">
                  <HorizontalPanel>
                    <AbsolutePanel />                   
                  </HorizontalPanel>
                </appButton>
              </widget>
              <widget halign="center">
                <appButton key="selectAllButton" style="Button">
                  <HorizontalPanel>
                    <AbsolutePanel />
                    <text>
                      <xsl:value-of select='resource:getString($constants,"selectAll")' />
                    </text>
                  </HorizontalPanel>
                </appButton>
              </widget>
              <widget halign="center">
                <appButton key="resettButton" style="Button">
                  <HorizontalPanel>
                    <AbsolutePanel />
                    <text>
                      <xsl:value-of select='resource:getString($constants,"selectNone")' />
                    </text>
                  </HorizontalPanel>
                </appButton>
              </widget>
              <widget halign="center">
                <appButton key="runReportButton" style="Button">
                  <HorizontalPanel>
                    <AbsolutePanel />
                    <text>
                      <xsl:value-of select='resource:getString($constants,"getReport")' />
                    </text>
                  </HorizontalPanel>
                </appButton>
              </widget>
            </HorizontalPanel>
          </VerticalPanel>
        </deck>
      </DeckPanel>
    </screen>
  </xsl:template>
</xsl:stylesheet>