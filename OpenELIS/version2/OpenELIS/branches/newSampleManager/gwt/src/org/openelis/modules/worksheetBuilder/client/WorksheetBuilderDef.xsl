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
  version="1.0"
  extension-element-prefixes="resource"
  xmlns:locale="xalan://java.util.Locale"
  xmlns:resource="xalan://org.openelis.util.UTFResource"
  xmlns:xalan="http://xml.apache.org/xalan"
  xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xsi:noNamespaceSchemaLocation="http://openelis.uhl.uiowa.edu/schema/ScreenSchema.xsd"
  xsi:schemaLocation="http://www.w3.org/1999/XSL/Transform http://openelis.uhl.uiowa.edu/schema/XSLTSchema.xsd"
  xmlns:meta="xalan://org.openelis.meta.WorksheetBuilderMeta">

  <xsl:import href="IMPORT/button.xsl" />
  <xsl:template match="doc">
    <xsl:variable name="language" select="locale" />
    <xsl:variable name="props" select="props" />
    <xsl:variable name="constants" select="resource:getBundle(string($props),locale:new(string($language)))" />
    <screen id="WorksheetBuilder" name="{resource:getString($constants,'worksheetBuilder')}">
      <HorizontalPanel>
        <CollapsePanel height="340" key="collapsePanel" style="LeftSidePanel">
          <TabPanel height="340" key="tabPanel" width="200">
            <tab key="aToZTab" text="A to Z">
              <HorizontalPanel width="200">
                <buttonGroup key="atozButtons">
                  <VerticalPanel height="100%" padding="0" spacing="0" style="AtoZ">
                    <xsl:call-template name="aToZButton">
                      <xsl:with-param name="keyParam">#</xsl:with-param>
                      <xsl:with-param name="queryParam">&gt;0</xsl:with-param>
                    </xsl:call-template>
                  </VerticalPanel>
                </buttonGroup>
                <VerticalPanel>
                  <table key="atozTable" maxRows="13" style="atozTable" width="auto">
                    <col header="{resource:getString($constants,'worksheetNumber')}" width="150">
                      <label field="Integer" />
                    </col>
                  </table>
                  <widget halign="center">
                    <HorizontalPanel>
                      <appButton enable="false" key="atozPrev" style="Button">
                        <AbsolutePanel style="prevNavIndex" />
                      </appButton>
                      <appButton enable="false" key="atozNext" style="Button">
                        <AbsolutePanel style="nextNavIndex" />
                      </appButton>
                    </HorizontalPanel>
                  </widget>
                </VerticalPanel>
              </HorizontalPanel>
            </tab>
            <tab key="analyteTab" text="{resource:getString($constants,'analytes')}">
	          <VerticalPanel>
	            <table key="analyteTable" width="auto" maxRows="14" showScroll="ALWAYS" style="atozTable">
	              <col key="analyteReportable" width="20" header="">
	                <check />
	              </col>
	              <col width="150" header="{resource:getString($constants,'analytes')}">
	                <label field="String" />
	              </col>
	            </table>
	          </VerticalPanel>
	        </tab>
	      </TabPanel>
        </CollapsePanel>
        <VerticalPanel padding="0" spacing="0">
          <AbsolutePanel spacing="0" style="ButtonPanelContainer">
            <HorizontalPanel>
              <xsl:call-template name="queryButton">
                <xsl:with-param name="language">
                  <xsl:value-of select="language" />
                </xsl:with-param>
              </xsl:call-template>
              <xsl:call-template name="previousButton">
                <xsl:with-param name="language">
                  <xsl:value-of select="language" />
                </xsl:with-param>
              </xsl:call-template>
              <xsl:call-template name="nextButton">
                <xsl:with-param name="language">
                  <xsl:value-of select="language" />
                </xsl:with-param>
              </xsl:call-template>
              <xsl:call-template name="buttonPanelDivider" />
              <xsl:call-template name="addButton">
                <xsl:with-param name="language">
                  <xsl:value-of select="language" />
                </xsl:with-param>
              </xsl:call-template>
              <xsl:call-template name="updateButton">
                <xsl:with-param name="language">
                  <xsl:value-of select="language" />
                </xsl:with-param>
              </xsl:call-template>
              <xsl:call-template name="buttonPanelDivider" />
              <xsl:call-template name="commitButton">
                <xsl:with-param name="language">
                  <xsl:value-of select="language" />
                </xsl:with-param>
              </xsl:call-template>
              <xsl:call-template name="abortButton">
                <xsl:with-param name="language">
                  <xsl:value-of select="language" />
                </xsl:with-param>
              </xsl:call-template>
              <xsl:call-template name="buttonPanelDivider" />
              <menuPanel key="optionsMenu" layout="vertical" style="topBarItemHolder">
                <menuItem>
                  <menuDisplay>
                    <appButton style="ButtonPanelButton" action="option">
                      <HorizontalPanel>
                        <text>
                          <xsl:value-of select='resource:getString($constants,"options")' />
                        </text>
                        <AbsolutePanel width="20" height="20" style="OptionsButtonImage" />
                      </HorizontalPanel>
                    </appButton>
                  </menuDisplay>
                  <menuPanel width="200" layout="vertical" position="below" style="buttonMenuContainer">
                    <menuItem key="worksheetHistory" description="" enable="false" icon="historyIcon" label="{resource:getString($constants,'worksheetHistory')}" />
                  </menuPanel>
                </menuItem>
              </menuPanel>
            </HorizontalPanel>
          </AbsolutePanel>
          <VerticalPanel padding="0" spacing="0" style="WhiteContentPanel">
            <TablePanel style="Form">
              <row>
                <text style="Prompt">
                  <xsl:value-of select="resource:getString($constants,'worksheetNumber')" />:
                </text>
                <textbox key="{meta:getWorksheetId()}" width="100" tab="{meta:getWorksheetStatusId()},worksheetItemTable" field="Integer" />
                <text style="Prompt">
                  <xsl:value-of select="resource:getString($constants,'status')" />:
                </text>
                <dropdown key="{meta:getWorksheetStatusId()}" width="119" popWidth="119" tab="{meta:getWorksheetSystemUserId()},{meta:getWorksheetId()}" field="Integer" />
                <text style="Prompt">
                  <xsl:value-of select="resource:getString($constants,'worksheetUser')" />
                </text>
                <autoComplete key="{meta:getWorksheetSystemUserId()}" width="166" case="LOWER" popWidth="auto" tab="{meta:getWorksheetFormatId()},{meta:getWorksheetStatusId()}" field="Integer" />
              </row>
              <row>
                <text style="Prompt">
                  <xsl:value-of select="resource:getString($constants,'worksheetFormat')" />:
                </text>
                <dropdown key="{meta:getWorksheetFormatId()}" width="130" popWidth="130px" tab="{meta:getWorksheetRelatedWorksheetId()},{meta:getWorksheetSystemUserId()}" field="Integer" />
                <text style="Prompt">
                  <xsl:value-of select="resource:getString($constants,'relatedWorksheetNumber')" />:
                </text>
                <HorizontalPanel>
                  <textbox key="{meta:getWorksheetRelatedWorksheetId()}" width="100" field="Integer" />
                  <appButton key="lookupWorksheetButton" style="LookupButton" tab="{meta:getInstrumentName()},{meta:getWorksheetFormatId()}" action="lookupWorksheet">
                    <AbsolutePanel style="LookupButtonImage" />
                  </appButton>
                </HorizontalPanel>
                <text style="Prompt">
                  <xsl:value-of select="resource:getString($constants,'instrumentName')" />:
                </text>
                <autoComplete key="{meta:getInstrumentName()}" width="166" case="LOWER" popWidth="auto" tab="{meta:getWorksheetCreatedDate()},lookupWorksheetButton" field="Integer">
                  <col width="150" header="{resource:getString($constants,'name')}" />
                  <col width="200" header="{resource:getString($constants,'description')}" />
                  <col width="200" header="{resource:getString($constants,'type')}"/>
                  <col width="200" header="{resource:getString($constants,'location')}" />
                </autoComplete>
              </row>
              <row>
                <text style="Prompt">
                  <xsl:value-of select="resource:getString($constants,'created')" />:
                </text>
                <calendar key="{meta:getWorksheetCreatedDate()}" begin="0" end="4" width="134" pattern="{resource:getString($constants,'dateTimePattern')}" tab="{meta:getWorksheetDescription()},{meta:getInstrumentName()}" />
                <text style="Prompt">
                  <xsl:value-of select="resource:getString($constants,'description')" />:
                </text>
                <widget colspan="3">
                  <textbox key="{meta:getWorksheetDescription()}" width="400" case="LOWER" tab="{meta:getWorksheetCreatedDate()},worksheetItemTable" field="String" />
                </widget>
              </row>
            </TablePanel>
            <table key="worksheetItemTable" width="757" maxRows="9" showScroll="ALWAYS" style="ScreenTableWithSides" tab="{meta:getWorksheetId()},{meta:getWorksheetDescription()}" title="">
              <col key="{meta:getWorksheetItemPosition()}" width="50" header="{resource:getString($constants,'position')}">
                <label field="Integer" />
              </col>
              <col key="{meta:getSampleAccessionNumber()}" width="90" sort="true" header="{resource:getString($constants,'accessionNum')}">
                <label field="String" />
              </col>
              <col key="{meta:getSampleDescription()}" width="110" sort="true" header="{resource:getString($constants,'description')}">
                <label field="String" />
              </col>
              <col key="{meta:getWorksheetAnalysisWorksheetAnalysisId}" width="90" header="{resource:getString($constants,'qcLink')}">
                <dropdown width="70" field="Integer" />
              </col>
              <col key="{meta:getAnalysisTestName()}" width="100" sort="true" header="{resource:getString($constants,'test')}">
                <label field="String" />
              </col>
              <col key="{meta:getAnalysisTestMethodName()}" width="100" sort="true" header="{resource:getString($constants,'method')}">
                <label field="String" />
              </col>
              <col key="{meta:getAnalysisUnitOfMeasureId()}" width="100" header="{resource:getString($constants,'unit')}">
                <autoComplete width="100" case="MIXED" field="Integer">
                  <col width="100" />
                </autoComplete>
              </col>
              <col key="{meta:getAnalysisStatusId()}" width="75" sort="true" header="{resource:getString($constants,'status')}">
                <dropdown width="55" field="Integer" />
              </col>
              <col key="{meta:getSampleCollectionDate()}" width="75" sort="true" header="{resource:getString($constants,'collected')}">
                <calendar begin="0" end="2" pattern="{resource:getString($constants,'datePattern')}" />
              </col>
              <col key="{meta:getSampleReceivedDate()}" width="110" sort="true" header="{resource:getString($constants,'received')}">
                <calendar begin="0" end="4" pattern="{resource:getString($constants,'dateTimePattern')}" />
              </col>
              <col key="{meta:getAnalysisDueDays()}" width="50" sort="true" header="{resource:getString($constants,'due')}">
                <label field="Integer" />
              </col>
              <col key="{meta:getAnalysisExpireDate()}" width="110" sort="true" header="{resource:getString($constants,'expire')}">
                <calendar begin="0" end="4" pattern="{resource:getString($constants,'dateTimePattern')}" />
              </col>
            </table>
            <widget style="TableFooterPanel">
              <HorizontalPanel>
                <menuPanel key="addRowMenu" layout="vertical" style="topBarItemHolder">
                  <menuItem>
                    <menuDisplay>
                      <appButton style="Button">
                        <HorizontalPanel>
                          <AbsolutePanel style="AddRowButtonImage" />
                          <text>
                            <xsl:value-of select='resource:getString($constants,"addRow")' />
                          </text>
                          <AbsolutePanel width="20" height="20" style="OptionsButtonImage" />
                        </HorizontalPanel>
                      </appButton>
                    </menuDisplay>
                    <menuPanel width="200" layout="vertical" position="below" style="buttonMenuContainer">
                      <menuItem key="insertAnalysisAbove" description="" enable="false" icon="AddRowButtonImage" label="{resource:getString($constants,'insertAnalysisAbove')}" />
                      <menuItem key="insertAnalysisBelow" description="" enable="false" icon="AddRowButtonImage" label="{resource:getString($constants,'insertAnalysisBelow')}" />
                      <menuItem key="insertFromWorksheetAbove" description="" enable="false" icon="AddRowButtonImage" label="{resource:getString($constants,'insertFromWorksheetAbove')}" />
                      <menuItem key="insertFromWorksheetBelow" description="" enable="false" icon="AddRowButtonImage" label="{resource:getString($constants,'insertFromWorksheetBelow')}" />
                      <menuItem key="insertFromQcTableAbove" description="" enable="false" icon="AddRowButtonImage" label="{resource:getString($constants,'insertFromQcTableAbove')}" />
                      <menuItem key="insertFromQcTableBelow" description="" enable="false" icon="AddRowButtonImage" label="{resource:getString($constants,'insertFromQcTableBelow')}" />
                    </menuPanel>
                  </menuItem>
                </menuPanel>
                <appButton key="removeRowButton" style="Button" action="removeRow">
                  <HorizontalPanel>
                    <AbsolutePanel style="RemoveRowButtonImage" />
                    <text>
                      <xsl:value-of select="resource:getString($constants,'removeRow')" />
                    </text>
                  </HorizontalPanel>
                </appButton>
                <menuPanel key="loadTemplateMenu" layout="vertical" style="topBarItemHolder">
                  <menuItem>
                    <menuDisplay>
                      <appButton style="Button">
                        <HorizontalPanel>
                          <AbsolutePanel style="AddRowButtonImage" />
                          <text>
                            <xsl:value-of select='resource:getString($constants,"loadTemplate")' />
                          </text>
                          <AbsolutePanel width="20" height="20" style="OptionsButtonImage" />
                        </HorizontalPanel>
                      </appButton>
                    </menuDisplay>
                    <menuPanel width="200" layout="vertical" position="below" style="buttonMenuContainer">
                    </menuPanel>
                  </menuItem>
                </menuPanel>
                <menuPanel key="undoQcsMenu" layout="vertical" style="topBarItemHolder">
                  <menuItem>
                    <menuDisplay>
                      <appButton style="Button">
                        <HorizontalPanel>
	                      <AbsolutePanel style="RemoveRowButtonImage" />
                          <text>
                            <xsl:value-of select='resource:getString($constants,"undoQc")' />
                          </text>
                          <AbsolutePanel width="20" height="20" style="OptionsButtonImage" />
                        </HorizontalPanel>
                      </appButton>
                    </menuDisplay>
                    <menuPanel width="200" layout="vertical" position="below" style="buttonMenuContainer">
                      <menuItem key="undoAll" description="" enable="false" icon="RemoveRowButtonImage" label="All" />
                      <menuItem key="undoTemplate" description="" enable="false" icon="RemoveRowButtonImage" label="Template" />
                      <menuItem key="undoManual" description="" enable="false" icon="RemoveRowButtonImage" label="Manual" />
                    </menuPanel>
                  </menuItem>
                </menuPanel>
              </HorizontalPanel>
            </widget>
          </VerticalPanel>
        </VerticalPanel>
      </HorizontalPanel>
    </screen>
  </xsl:template>
</xsl:stylesheet>
