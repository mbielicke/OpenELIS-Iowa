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
    <screen id="FinalReportSDWIS" name="{resource:getString($constants,'finalReport')}">
      <DeckPanel height="100%" key="deck" width="100%">
        <deck>
          <HorizontalPanel padding="0" spacing="0" style="WhiteContentPanel">
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
                      <textbox case="MIXED" field="String" key="{meta:getSDWISCollector()}" max="60" width="202" />
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
                      <textbox case="MIXED" field="String" key="{meta:getSDWISLocation()}" max="60" width="202" />
                    </widget>
                  </row>
                  <row>
                    <text style="Prompt">
                      <xsl:value-of select='resource:getString($constants,"pwsId")' />:
                    </text>
                    <widget colspan="4">
                      <textbox field="String" key="{meta:getPwsNumber0()}" max="60" width="202" />
                    </widget>
                  </row>
                  <row>
                    <text style="Prompt">
                      <xsl:value-of select='resource:getString($constants,"facilityId")' />:
                    </text>
                    <widget colspan="4">
                      <textbox field="String" key="{meta:getSDWISFacilityId()}" max="60" tab="{meta:getReleasedDateFrom()},{meta:getPwsNumber0()}" width="202" />
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
            <VerticalPanel height="280" padding="0" spacing="0" style="help" width="400">
              <TablePanel width="100%">
                <row>
                  <html>Lorem ipsum dolor sit amet, consectetuer adipiscing elit, sed diam nonummy nibh euismod tincidunt ut laoreet dolore magna aliquam erat volutpat Lorem ipsum dolor sit amet, consectetuer  adipiscing elit, sed diam nonummy nibh euismod tincidunt ut laoreet dolore magna aliquam erat volutpat</html>
                </row>
              </TablePanel>
            </VerticalPanel>
          </HorizontalPanel>
        </deck>
        <deck>
          <VerticalPanel padding="0" spacing="0" style="WhiteContentPanel">
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
                  <col header="{resource:getString($constants,'pwsId')}" key="pwsId" width="120">
                    <textbox field="String" />
                  </col>
                  <col header="{resource:getString($constants,'pwsName')}" key="pwsName" width="120">
                    <textbox field="String" />
                  </col>
                  <col header="{resource:getString($constants,'facilityId')}" key="facilityId" width="120">
                    <textbox field="Integer" />
                  </col>
                </table>
              </widget>
            </VerticalPanel>
            <VerticalPanel height="20">
              <label field="String" key="numSampleSelected" style="Prompt" />
            </VerticalPanel>
            <HorizontalPanel style="TableFooterPanel">
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
                <appButton key="runReportButton" style="Button">
                  <HorizontalPanel>
                    <AbsolutePanel />
                    <text>
                      <xsl:value-of select='resource:getString($constants,"runReport")' />
                    </text>
                  </HorizontalPanel>
                </appButton>
              </widget>
              <widget halign="center">
                <appButton key="resettButton" style="Button">
                  <HorizontalPanel>
                    <AbsolutePanel />
                    <text>
                      <xsl:value-of select='resource:getString($constants,"reset")' />
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